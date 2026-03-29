export interface ArchetypeWeights {
  urban_explorer: number;
  social_connector: number;
  planner_strategist: number;
  local_insider: number;
  scavenger_collector: number;
}

export interface QuizOption {
  optionId: string;
  label: string;
  archetypeWeights: ArchetypeWeights;
  imageUrl?: string;
}

export interface QuizQuestion {
  questionId: string;
  text: string;
  options: QuizOption[];
}

export interface Quiz {
  id: string;
  version: string;
  questions: QuizQuestion[];
}

export interface UserProfile {
  age: number;
  city: string;
  answers: string[];
  archetypeScores: ArchetypeWeights;
  dominantArchetype: string;
}

export interface POI {
  id: string;
  title: string;
  description: string;
  imageUrl: string;
  status: 'locked' | 'unlocked' | 'completed';
  challenge: Challenge;
}

export interface Challenge {
  question: string;
  options: string[];
  correctAnswer: number;
}

export type GameView = 'setup' | 'quiz' | 'loading' | 'board';
