/* implementação da classe VotacaoService. 
Esta classe é a principal da aplicação */

@Service
@RequiredArgsConstructor
@Slf4j
public class VotacaoService {

    private final PautaRepository pautaRepository;
    private final SessaoRepository sessaoRepository;
    private final VotoRepository votoRepository;
    private final CpfValidatorClient cpfValidatorClient;

    /**
     * Cria uma nova pauta para votação.
     */
    @Transactional
    public Pauta cadastrarPauta(PautaDTO dto) {
        log.info("Cadastrando nova pauta: {}", dto.titulo());
        Pauta pauta = new Pauta();
        pauta.setTitulo(dto.titulo());
        pauta.setDescricao(dto.descricao());
        return pautaRepository.save(pauta);
    }

    /**
     * Abre a sessão de votação. Se minutos não for informado, assume 1 minuto.
     */
    @Transactional
    public SessaoVotacao abrirSessao(Long pautaId, Integer minutos) {
        Pauta pauta = pautaRepository.findById(pautaId)
                .orElseThrow(() -> new ResourceNotFoundException("Pauta não encontrada."));

        if (sessaoRepository.existsById(pautaId)) {
            throw new BusinessException("Já existe uma sessão aberta ou finalizada para esta pauta.");
        }

        int tempo = (minutos != null && minutos > 0) ? minutos : 1;
        
        SessaoVotacao sessao = new SessaoVotacao();
        sessao.setPautaId(pautaId);
        sessao.setDataAbertura(LocalDateTime.now());
        sessao.setDataFechamento(LocalDateTime.now().plusMinutes(tempo));

        log.info("Sessão aberta para pauta {} por {} minutos.", pautaId, tempo);
        return sessaoRepository.save(sessao);
    }

    /**
     * Registra o voto do associado com validações rigorosas.
     */
    @Transactional
    public void registrarVoto(Long pautaId, VotoRequestDTO dto) {
        // 1. Validação Bônus 1: CPF Externo
        // Se retornar 404 no Client, a exceção sobe e interrompe o fluxo aqui.
        cpfValidatorClient.validar(dto.cpf());

        // 2. Validação de existência da sessão
        SessaoVotacao sessao = sessaoRepository.findById(pautaId)
                .orElseThrow(() -> new BusinessException("Votação não iniciada para esta pauta."));

        // 3. Validação de tempo (Timer)
        if (LocalDateTime.now().isAfter(sessao.getDataFechamento())) {
            log.warn("Tentativa de voto em sessão encerrada. Pauta: {}", pautaId);
            throw new BusinessException("A sessão de votação já está encerrada.");
        }

        // 4. Validação de Voto Único (Idempotência)
        if (votoRepository.existsByPautaIdAndCpf(pautaId, dto.cpf())) {
            throw new BusinessException("Este CPF já votou nesta pauta.");
        }

        // 5. Persistência do Voto
        Voto voto = new Voto();
        voto.setPautaId(pautaId);
        voto.setCpf(dto.cpf());
        voto.setEscolha(dto.voto().toUpperCase()); // "SIM" ou "NAO"
        
        votoRepository.save(voto);
        log.info("Voto registrado com sucesso para o CPF {} na pauta {}", dto.cpf(), pautaId);
    }

    /**
     * Contabiliza os resultados (Foco em Performance - Bônus 2).
     */
    @Transactional(readOnly = true)
    public ResultadoDTO obterResultado(Long pautaId) {
        if (!pautaRepository.existsById(pautaId)) {
            throw new ResourceNotFoundException("Pauta inexistente.");
        }

        long votosSim = votoRepository.countByPautaIdAndEscolha(pautaId, "SIM");
        long votosNao = votoRepository.countByPautaIdAndEscolha(pautaId, "NAO");
        
        String status = (votosSim > votosNao) ? "APROVADA" : "REPROVADA";
        if (votosSim == votosNao && (votosSim + votosNao) > 0) status = "EMPATE";

        return new ResultadoDTO(pautaId, votosSim, votosNao, status);
    }
}
