package it.siali.playthecity.repository;

import it.siali.playthecity.documets.OnboardingQuiz;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OnboardingQuizRepository extends MongoRepository<OnboardingQuiz, String> {
}