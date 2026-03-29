import { createContext, useContext, useState, ReactNode, Dispatch, SetStateAction } from 'react';
import { GameView, UserProfile, POI } from '../types';

interface GameContextType {
  currentView: GameView;
  setCurrentView: (view: GameView) => void;
  userProfile: UserProfile | null;
  setUserProfile: Dispatch<SetStateAction<UserProfile | null>>;
  pois: POI[];
  setPois: (pois: POI[]) => void;
  unlockNextPOI: (currentId: string) => void;
  completePOI: (id: string) => void;
  score: number;
  setScore: Dispatch<SetStateAction<number>>;
  hints: number;
  setHints: Dispatch<SetStateAction<number>>;
}

const GameContext = createContext<GameContextType | undefined>(undefined);

export const GameProvider = ({ children }: { children: ReactNode }) => {
  const [currentView, setCurrentView] = useState<GameView>('setup');
  const [userProfile, setUserProfile] = useState<UserProfile | null>(null);
  const [pois, setPois] = useState<POI[]>([]);
  const [score, setScore] = useState(0);
  const [hints, setHints] = useState(20); // 20 fulmini a partita

  const unlockNextPOI = (currentId: string) => {
    setPois(prev => {
      const currentIndex = prev.findIndex(poi => poi.id === currentId);
      if (currentIndex !== -1 && currentIndex < prev.length - 1) {
        return prev.map((poi, idx) => 
          idx === currentIndex + 1 ? { ...poi, status: 'discovery' as const } : poi
        );
      }
      return prev;
    });
  };

  const completePOI = (id: string) => {
    setPois(prev =>
      prev.map(poi =>
        poi.id === id ? { ...poi, status: 'completed' as const } : poi
      )
    );
  };

  return (
    <GameContext.Provider
      value={{
        currentView,
        setCurrentView,
        userProfile,
        setUserProfile,
        pois,
        setPois,
        unlockNextPOI,
        completePOI,
        score,
        setScore,
        hints,
        setHints,
      }}
    >
      {children}
    </GameContext.Provider>
  );
};

export const useGame = () => {
  const context = useContext(GameContext);
  if (!context) {
    throw new Error('useGame must be used within a GameProvider');
  }
  return context;
};