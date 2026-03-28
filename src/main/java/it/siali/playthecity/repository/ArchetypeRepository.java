@Repository
public interface ArchetypeRepository extends MongoRepository<Archetype, String> {
    // Trova le configurazioni dell'archetipo tramite lo slug (es: "urban-ninja")
    Optional<Archetype> findBySlug(String slug);
}