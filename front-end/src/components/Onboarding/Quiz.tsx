import { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { QuizProgress } from './QuizProgress';
import { Sparkles, Loader2, BookOpen, Compass, ArrowRight } from 'lucide-react';

interface QuizProps {
  // Questi dati arrivano dal SetupForm
  userData: {
    age: string;
    city: string;
    coords: { lat: string; lon: string } | null;
  };
  onComplete: (finalResult: any) => void;
}

export const Quiz = ({ userData, onComplete }: QuizProps) => {
  const [loading, setLoading] = useState(true);
  const [finalizing, setFinalizing] = useState(false);
  const [questions, setQuestions] = useState<any[]>([]);
  const [behavioralCount, setBehavioralCount] = useState(0);
  const [sessionId, setSessionId] = useState<string>("");
  const [currentQuestion, setCurrentQuestion] = useState(0);
  
  // Mappa delle risposte: { "questionId": "optionId" }
  const [answersMap, setAnswersMap] = useState<Record<string, string>>({});

  const letters = ["A", "B", "C", "D"];

  useEffect(() => {
    const initQuiz = async () => {
      try {
        const response = await fetch('http://localhost:8080/api/game/onboarding/start', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' }
        });
        const data = await response.json();
        
        // Calcoliamo quante sono le domande comportamentali per dividere i progressi
        setBehavioralCount(data.behavioralQuestions.length);
        
        // Uniamo i due array per il flusso del quiz
        setQuestions([...data.behavioralQuestions, ...data.culturalQuestions]);
        setSessionId(data.sessionId);
        setLoading(false);
      } catch (error) {
        console.error("Errore fetch iniziale:", error);
      }
    };
    initQuiz();
  }, []);

  const handleOptionSelect = async (option: any) => {
    const currentQ = questions[currentQuestion];
    
    // Aggiorniamo la mappa delle risposte mantenendo l'ID domanda come chiave
    const newAnswersMap = { 
      ...answersMap, 
      [currentQ.id]: option.optionId 
    };
    setAnswersMap(newAnswersMap);

    if (currentQuestion < questions.length - 1) {
      setCurrentQuestion(currentQuestion + 1);
    } else {
      // Se è l'ultima domanda del set completo, inviamo tutto al server
      await finalizeOnboarding(newAnswersMap);
    }
  };

  const finalizeOnboarding = async (finalAnswers: Record<string, string>) => {
    setFinalizing(true);
    try {
      const payload = {
        sessionId,
        city: userData.city,
        age: userData.age,
        latitude: userData.coords?.lat,
        longitude: userData.coords?.lon,
        answers: finalAnswers
      };

      const response = await fetch('http://localhost:8080/api/game/onboarding/finalize', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });

      const result = await response.json();
      
      // Passiamo il JSON dell'avventura (con steps, narrative, ecc.) al componente padre
      onComplete(result); 
    } catch (error) {
      console.error("Errore finalizzazione:", error);
    } finally {
      setFinalizing(false);
    }
  };

  if (loading || finalizing) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-indigo-500 via-purple-500 to-pink-500 flex items-center justify-center p-6 text-center">
        <div className="text-white">
          <Loader2 className="w-16 h-16 animate-spin mx-auto mb-6 opacity-80" />
          <motion.h2 
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            className="text-2xl font-black uppercase tracking-tighter"
          >
            {finalizing ? "Plasmando la tua avventura..." : "Interrogando il destino..."}
          </motion.h2>
          <p className="mt-2 text-indigo-100 italic opacity-70">
            {finalizing ? "Bergamo sta preparando i suoi segreti..." : "Un momento di pazienza..."}
          </p>
        </div>
      </div>
    );
  }

  const isBehavioral = currentQuestion < behavioralCount;
  const question = questions[currentQuestion];
  
  // Calcolo progresso interno alla sezione corrente (1 di 5, poi resetta a 1 di 3)
  const currentStepInSection = isBehavioral ? currentQuestion + 1 : (currentQuestion - behavioralCount) + 1;
  const totalStepsInSection = isBehavioral ? behavioralCount : questions.length - behavioralCount;

  return (
    <div className="min-h-screen bg-gradient-to-br from-indigo-500 via-purple-500 to-pink-500 flex items-center justify-center p-4 font-sans">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="bg-white rounded-[2.5rem] shadow-2xl p-6 md:p-12 max-w-3xl w-full"
      >
        {/* Storytelling Header */}
        <div className="text-center mb-10">
          <motion.div 
            initial={{ scale: 0.8 }}
            animate={{ scale: 1 }}
            className="inline-flex items-center gap-2 px-5 py-2 rounded-full bg-indigo-50 text-indigo-600 text-[10px] font-black uppercase tracking-[0.2em] mb-6"
          >
            {isBehavioral ? <Compass className="w-4 h-4" /> : <BookOpen className="w-4 h-4" />}
            {isBehavioral ? "Fase 1: Spirito Esplorativo" : "Fase 2: Retaggio Culturale"}
          </motion.div>
          
          <h1 className="text-3xl md:text-4xl font-black text-gray-900 mb-3 tracking-tighter">
            {isBehavioral ? "Conosciamoci meglio!" : "L'ultimo ostacolo!"}
          </h1>
          <p className="text-gray-500 text-sm md:text-base italic leading-relaxed max-w-lg mx-auto">
            {isBehavioral 
              ? "Ogni scelta rivela il tuo archetipo urbano. Tutto questo ci aiuterà a creare un'avventura fantastica su misura per te!"
              : "Le città parlano a chi sa ascoltare. Dimostra la tua conoscenza per affinare i dettagli della tua missione."}
          </p>
        </div>

        {/* ProgressBar divisa con etichette chiare */}
        <div className="mb-12">
          <QuizProgress current={currentStepInSection} total={totalStepsInSection} />
          <div className="flex justify-between mt-4 text-[10px] font-black uppercase tracking-widest text-gray-400">
            <span className="flex items-center gap-2">
              <span className={isBehavioral ? "text-indigo-600" : ""}>Attitudine</span>
              <ArrowRight className="w-3 h-3 opacity-30" />
              <span className={!isBehavioral ? "text-pink-600" : ""}>Cultura</span>
            </span>
            <span>Domanda {currentStepInSection} di {totalStepsInSection}</span>
          </div>
        </div>

        <AnimatePresence mode="wait">
          <motion.div
            key={currentQuestion}
            initial={{ opacity: 0, x: 20 }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: -20 }}
            transition={{ duration: 0.4, ease: "easeOut" }}
          >
            <div className="mb-10">
              <div className="flex items-start gap-5">
                <div className="w-14 h-14 bg-gradient-to-br from-indigo-500 to-purple-600 rounded-2xl flex items-center justify-center flex-shrink-0 shadow-xl -rotate-2">
                  <Sparkles className="w-7 h-7 text-white" />
                </div>
                <h2 className="text-xl md:text-2xl font-bold text-gray-800 leading-tight pt-1">
                  {question.text}
                </h2>
              </div>
            </div>

            <div className="grid grid-cols-1 gap-4">
              {question.options.map((option: any, i: number) => (
                <motion.button
                  key={option.optionId}
                  onClick={() => handleOptionSelect(option)}
                  whileHover={{ scale: 1.01, x: 8 }}
                  whileTap={{ scale: 0.98 }}
                  className="group flex items-center gap-5 w-full p-5 md:p-6 rounded-[1.5rem] border-2 border-gray-50 bg-gray-50/50 hover:border-indigo-400 hover:bg-white hover:shadow-xl transition-all duration-300 text-left"
                >
                  <span className="w-12 h-12 rounded-xl bg-white border border-gray-100 group-hover:bg-indigo-600 group-hover:text-white flex items-center justify-center text-lg font-black text-indigo-600 transition-all shrink-0 shadow-sm group-hover:rotate-6">
                    {letters[i]}
                  </span>
                  <div className="flex-1 text-base md:text-lg font-bold text-gray-700 group-hover:text-gray-900 transition-colors">
                    {option.label}
                  </div>
                  <ArrowRight className="w-6 h-6 text-gray-200 opacity-0 group-hover:opacity-100 group-hover:translate-x-2 transition-all" />
                </motion.button>
              ))}
            </div>
          </motion.div>
        </AnimatePresence>
      </motion.div>
    </div>
  );
};