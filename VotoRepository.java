/* aplico as regras de voto único e contagem performática.*/
@Repository
public interface VotoRepository extends JpaRepository<Voto, Long> {
 /* *
     * Verifica se um associado já votou em uma pauta específica.
     * Indexado no banco de dados para busca em O(1) ou O(log n).
     */
    boolean existsByPautaIdAndCpf(Long pautaId, String cpf);

    /**
     * Conta os votos diretamente no Banco de Dados.
     * Essencial para a Tarefa Bônus 2: Evita carregar milhares de registros na JVM.
     * SQL gerado: SELECT COUNT(*) FROM voto WHERE pauta_id = ? AND escolha = ?
     */
    long countByPautaIdAndEscolha(Long pautaId, String escolha);
}