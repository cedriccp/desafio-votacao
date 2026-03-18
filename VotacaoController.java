/* 
Controller, componente que faz a ponte entre o aplicativo mobile e o VotacaoService.
Conforme o desafio, este Controller não retorna apenas dados puros, mas sim a estrutura de FORMULARIO e SELECAO definida no Anexo 1. Para facilitar o teste em diferentes ambientes (Emulador, Wi-Fi, Nuvem), utilizei uma propriedade configurável para o domínio da URL.
*/

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class VotacaoController {

    private final VotacaoService votacaoService;

    @Value("${app.api-host:localhost:8080}")
    private String apiHost;

    // --- ENDPOINTS DE NEGÓCIO (API REST) ---

    @PostMapping("/pautas")
    public ResponseEntity<Pauta> criarPauta(@RequestBody PautaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(votacaoService.cadastrarPauta(dto));
    }

    @PostMapping("/pautas/{id}/abrir")
    public ResponseEntity<SessaoVotacao> abrirSessao(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer minutos = body.getOrDefault("minutos", 1);
        return ResponseEntity.ok(votacaoService.abrirSessao(id, minutos));
    }

    @PostMapping("/pautas/{id}/votos")
    public ResponseEntity<Map<String, String>> votar(@PathVariable Long id, @RequestBody VotoRequestDTO dto) {
        votacaoService.registrarVoto(id, dto);
        return ResponseEntity.ok(Map.of("mensagem", "Voto registrado com sucesso!"));
    }

    @GetMapping("/pautas/{id}/resultado")
    public ResponseEntity<ResultadoDTO> obterResultado(@PathVariable Long id) {
        return ResponseEntity.ok(votacaoService.obterResultado(id));
    }

    // --- ENDPOINTS DE INTERFACE (SERVER-DRIVEN UI PARA MOBILE) ---

    @GetMapping("/telas/pautas/nova")
    public ResponseEntity<Map<String, Object>> getTelaNovaPauta() {
        return ResponseEntity.ok(Map.of(
            "tipoTela", "FORMULARIO",
            "titulo", "Cadastrar Nova Pauta",
            "itens", List.of(
                Map.of("id", "titulo", "label", "Título", "tipo", "TEXTO"),
                Map.of("id", "descricao", "label", "Descrição", "tipo", "TEXTO")
            ),
            "botoes", List.of(
                Map.of("texto", "Salvar", "url", String.format("http://%s/api/v1/pautas", apiHost), "metodo", "POST")
            )
        ));
    }

    @GetMapping("/telas/pautas/{id}/votar")
    public ResponseEntity<Map<String, Object>> getTelaVotacao(@PathVariable Long id) {
        String baseUrl = String.format("http://%s/api/v1/pautas/%d/votos", apiHost, id);
        
        return ResponseEntity.ok(Map.of(
            "tipoTela", "SELECAO",
            "titulo", "Selecione seu Voto",
            "itens", List.of(
                Map.of("label", "Sim", "url", baseUrl, "body", Map.of("voto", "SIM", "cpf", "")),
                Map.of("label", "Não", "url", baseUrl, "body", Map.of("voto", "NAO", "cpf", ""))
            )
        ));
    }
}
