package it.siali.playthecity.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import it.siali.playthecity.documets.Archetype;

@Repository
public interface ArchetypeRepository extends MongoRepository<Archetype, String> {
    // Trova le configurazioni dell'archetipo tramite lo slug (es: "urban-ninja")
    Optional<Archetype> findBySlug(String slug);
}