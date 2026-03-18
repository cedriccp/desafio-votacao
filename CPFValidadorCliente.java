@Component
@Slf4j
public class CpfValidadorCliente {

    /**
     * Simula a validação externa de CPF conforme requisitos do bônus.
     * @param cpf String do CPF enviado pelo mobile
     * @return CpfStatusResponse com o status de permissão
     * @throws CpfNotFoundException (Mapeada para 404 via @ControllerAdvice)
     */
    public CpfStatusResponse validar(String cpf) {
        log.info("Validando CPF: {}", cpf);

        // Simulação de CPF Inválido (Exemplo: CPFs que terminam em '00')
        // Retorna 404 Not Found conforme o requisito do desafio
        if (cpf == null || cpf.endsWith("00") || cpf.length() != 11) {
            log.warn("CPF {} inválido ou não encontrado.", cpf);
            throw new ResourceNotFoundException("CPF inválido ou inexistente.");
        }

        // Simulação de aleatoriedade para ABLE ou UNABLE
        // Em um cenário real, aqui haveria uma chamada RestTemplate ou WebClient
        boolean isAble = new Random().nextBoolean();

        if (isAble) {
            return new CpfStatusResponse(CpfStatusResponse.ABLE);
        } else {
            // O requisito pede 404 também se o status for UNABLE_TO_VOTE
            log.info("CPF {} reconhecido, mas não autorizado a votar.", cpf);
            throw new ResourceNotFoundException("UNABLE_TO_VOTE");
        }
    }
}
