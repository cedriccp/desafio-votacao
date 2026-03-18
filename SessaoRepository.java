@Repository
public interface SessaoRepository extends JpaRepository<SessaoVotacao, Long> {
    // Busca sessões que já deveriam estar fechadas mas não foram processadas
    List<SessaoVotacao> findByDataFechamentoBefore(LocalDateTime agora);
}
