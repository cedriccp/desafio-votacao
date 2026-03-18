/* Responsável pela persistência básica das pautas.*/

@Repository
public interface PautaRepository extends JpaRepository<Pauta, Long> {
    // Métodos padrão do JpaRepository (save, findById, etc.)
}
