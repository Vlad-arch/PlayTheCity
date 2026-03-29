import { useEffect } from 'react';
import { GameProvider, useGame } from './contexts/GameContext';
import { SetupForm } from './components/Onboarding/SetupForm';
import { Quiz } from './components/Onboarding/Quiz';
import { LoadingScreen } from './components/Onboarding/LoadingScreen';
import { GameBoard } from './components/Board/GameBoard';

function AppContent() {
  const { currentView, setCurrentView, setUserProfile, setPois, userProfile } = useGame();

  const handleSetupComplete = (age: string, city: string, coords: { lat: string; lon: string } | null) => {
    setUserProfile({
      age,
      city,
      coords,
      answers: {},
      archetypeScores: {}, 
      dominantArchetype: '',
    });
    setCurrentView('quiz');
  };

  const handleQuizComplete = (finalResult: any) => {
    // 1. Aggiorniamo il profilo utente con i dati dell'avventura (archetipo, titolo, ecc.)
    setUserProfile(prev => ({
      ...prev!,
      ...finalResult, 
    }));
    
    // 2. Mappiamo gli 'steps' del backend nel formato 'POI' del frontend
    if (finalResult.steps) {
      const mappedPois = finalResult.steps.map((step: any, index: number) => ({
        id: `step-${index}`,
        name: step.locationName,
        description: step.description,
        lat: step.lat,
        lon: step.lon,
        imageUrl: step.imageUrl,
        // Dati extra per il modal
        narrative: step.narrative,
        quiz: step.quiz,
        // Logica di sblocco sequenziale
        status: index === 0 ? 'discovery' : 'locked',
        category: finalResult.archetype // usiamo l'archetipo come categoria estetica
      }));
      
      setPois(mappedPois);
    }

    setCurrentView('loading');

    // Transizione alla board
    setTimeout(() => {
      setCurrentView('board');
    }, 3000);
  };

  useEffect(() => {
    document.title = 'Ludopolis - Urban Adventure Game';
  }, []);

  return (
    <>
      {currentView === 'setup' && (
        <SetupForm onComplete={handleSetupComplete} />
      )}
      
      {currentView === 'quiz' && userProfile && (
        <Quiz 
          userData={userProfile} 
          onComplete={handleQuizComplete} 
        />
      )}
      
      {currentView === 'loading' && <LoadingScreen />}
      {currentView === 'board' && <GameBoard />}
    </>
  );
}

function App() {
  return (
    <GameProvider>
      <AppContent />
    </GameProvider>
  );
}

export default App;