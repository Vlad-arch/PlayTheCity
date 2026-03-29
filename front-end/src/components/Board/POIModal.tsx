import { useState, useRef, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { X, CheckCircle, XCircle, BookOpen, HelpCircle, Zap, Coins, Volume2, Loader2, VolumeX } from 'lucide-react';
import { useGame } from '../../contexts/GameContext'; 

interface POIModalProps {
  poi: any;
  isOpen: boolean;
  onClose: () => void;
  onComplete: () => void;
}

const ELEVENLABS_API_KEY = import.meta.env.VITE_ELEVENLABS_API_KEY;
const VOICE_ID = "21m00Tcm4TlvDq8ikWAM"; 

export const POIModal = ({ poi, isOpen, onClose, onComplete }: POIModalProps) => {
  const [selectedAnswer, setSelectedAnswer] = useState<number | null>(null);
  const [showResult, setShowResult] = useState(false);
  const [isCorrect, setIsCorrect] = useState(false);
  const [attempts, setAttempts] = useState(0);
  const [disabledOptions, setDisabledOptions] = useState<number[]>([]);
  const [isLoadingAudio, setIsLoadingAudio] = useState(false);
  const [isPlaying, setIsPlaying] = useState(false);
  const audioRef = useRef<HTMLAudioElement | null>(null);

  const { score, setScore, hints, setHints } = useGame();

  useEffect(() => {
    if (!isOpen && audioRef.current) {
      audioRef.current.pause();
      audioRef.current = null;
      setIsPlaying(false);
    }
  }, [isOpen]);

  if (!poi) return null;

  const quiz = poi.quiz;
  const correctAnswerIndex = quiz.correctAnswerIndex;

  const handlePlayNarrative = async () => {
    if (!ELEVENLABS_API_KEY || isLoadingAudio) return;
    if (audioRef.current && !audioRef.current.paused) {
      audioRef.current.pause();
      audioRef.current = null;
      setIsPlaying(false);
      return;
    }
    setIsLoadingAudio(true);
    try {
      const response = await fetch(`https://api.elevenlabs.io/v1/text-to-speech/${VOICE_ID}`, {
        method: 'POST',
        headers: {
          'xi-api-key': ELEVENLABS_API_KEY,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          text: poi.narrative,
          model_id: "eleven_multilingual_v2",
          voice_settings: { stability: 0.5, similarity_boost: 0.75 }
        }),
      });
      if (!response.ok) throw new Error('TTS Error');
      const blob = await response.blob();
      const url = URL.createObjectURL(blob);
      const audio = new Audio(url);
      audioRef.current = audio;
      audio.onplay = () => { setIsLoadingAudio(false); setIsPlaying(true); };
      audio.onended = () => { setIsPlaying(false); setIsLoadingAudio(false); };
      audio.onpause = () => setIsPlaying(false);
      await audio.play();
    } catch (error) {
      console.error("ElevenLabs Error:", error);
      setIsLoadingAudio(false);
      setIsPlaying(false);
    }
  };

  const handleAnswerSelect = (index: number) => {
    if (disabledOptions.includes(index)) return;
    setSelectedAnswer(index);
    const correct = index === correctAnswerIndex;
    setIsCorrect(correct);
    setShowResult(true);
    if (correct) {
      let pointsToAdd = 100;
      if (attempts === 1) pointsToAdd = 90;
      else if (attempts === 2) pointsToAdd = 70;
      else if (attempts >= 3) pointsToAdd = 20;
      setScore((prev: number) => prev + pointsToAdd);
      setTimeout(() => { onComplete(); handleClose(); }, 2500);
    } else {
      setAttempts(prev => prev + 1);
      setTimeout(() => {
        setDisabledOptions(prev => [...prev, index]);
        setShowResult(false);
        setSelectedAnswer(null);
      }, 1500);
    }
  };

  const useHint = () => {
    if (hints < 5 || disabledOptions.length >= quiz.options.length - 1) return;
    const wrongOptions = quiz.options
      .map((_: any, i: number) => i)
      .filter((i: number) => i !== correctAnswerIndex && !disabledOptions.includes(i));
    const toDisable = wrongOptions.sort(() => 0.5 - Math.random()).slice(0, 2);
    setDisabledOptions(prev => [...prev, ...toDisable]);
    setHints((prev: number) => prev - 5);
  };

  const handleClose = () => {
    if (audioRef.current) { audioRef.current.pause(); audioRef.current = null; }
    setIsPlaying(false);
    setSelectedAnswer(null);
    setShowResult(false);
    setIsCorrect(false);
    setAttempts(0);
    setDisabledOptions([]);
    onClose();
  };

  return (
    <AnimatePresence>
      {isOpen && (
        <>
          <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }} onClick={handleClose} className="fixed inset-0 bg-black/70 backdrop-blur-md z-[100]" />

          <AnimatePresence>
            {isCorrect && showResult && (
              <motion.div key="confetti-layer" initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }} className="fixed inset-0 z-[200] pointer-events-none">
                {[...Array(50)].map((_, i) => (
                  <motion.div key={i} initial={{ x: "50vw", y: "50vh", scale: 0, rotate: 0 }} animate={{ x: [`50vw`, `${Math.random() * 100}vw`], y: [`50vh`, `${Math.random() * 100}vh`], scale: [0, 1, 0] }} transition={{ duration: 2, delay: Math.random() * 0.1 }} className="absolute w-2 h-2 rounded-sm" style={{ background: ['#f59e0b', '#ec4899', '#8b5cf6', '#10b981', '#3b82f6'][i % 5] }} />
                ))}
              </motion.div>
            )}
          </AnimatePresence>

          <div className="fixed inset-0 z-[110] flex items-center justify-center p-2 md:p-4 pointer-events-none">
            <motion.div
              initial={{ opacity: 0, scale: 0.9, y: 30 }}
              animate={{ opacity: 1, scale: 1, y: 0 }}
              exit={{ opacity: 0, scale: 0.9, y: 30 }}
              className="bg-white rounded-[1.5rem] md:rounded-[2.5rem] shadow-2xl max-w-2xl w-full max-h-[95vh] md:max-h-[90vh] overflow-hidden flex flex-col pointer-events-auto"
            >
              <div className="absolute top-4 left-4 md:top-6 md:left-8 flex gap-2 md:gap-3 z-50">
                <div className="bg-yellow-400 text-yellow-900 px-2 py-1 md:px-3 md:py-1.5 rounded-xl md:rounded-2xl flex items-center gap-1.5 shadow-lg font-black text-[10px] md:text-xs uppercase">
                  <Zap className="w-3 h-3 md:w-4 md:h-4 fill-current" /> {hints}
                </div>
                <div className="bg-white/90 backdrop-blur-md px-2 py-1 md:px-3 md:py-1.5 rounded-xl md:rounded-2xl flex items-center gap-1.5 shadow-lg font-black text-[10px] md:text-xs text-gray-800 uppercase">
                  <Coins className="w-3 h-3 md:w-4 md:h-4 text-orange-500" /> {score}
                </div>
              </div>

              <div className="relative h-48 md:h-64 shrink-0">
                <img src={poi.imageUrl} className="w-full h-full object-cover" alt={poi.name} />
                <div className="absolute inset-0 bg-gradient-to-t from-black/80 to-transparent" />
                
                {ELEVENLABS_API_KEY && (
                  <motion.button 
                    onClick={handlePlayNarrative}
                    animate={isPlaying || isLoadingAudio ? { scale: [1, 1.1, 1] } : {}}
                    transition={isPlaying || isLoadingAudio ? { repeat: Infinity, duration: 1.5 } : {}}
                    className={`absolute bottom-4 right-16 md:bottom-6 md:right-20 w-10 h-10 md:w-14 md:h-14 rounded-xl md:rounded-2xl flex items-center justify-center text-white shadow-2xl transition-all active:scale-90 z-50 border border-white/50 ${
                        isPlaying 
                        ? 'bg-gradient-to-tr from-red-500 to-pink-600' 
                        : 'bg-gradient-to-tr from-indigo-500 to-purple-600'
                    }`}
                  >
                    {isLoadingAudio ? <Loader2 className="w-5 h-5 md:w-7 md:h-7 animate-spin" /> : isPlaying ? <VolumeX className="w-5 h-5 md:w-7 md:h-7" /> : <Volume2 className="w-5 h-5 md:w-7 md:h-7" />}
                  </motion.button>
                )}

                <button
                  onClick={handleClose}
                  className="absolute top-4 right-4 md:top-6 md:right-6 w-10 h-10 md:w-12 md:h-12 bg-white/10 backdrop-blur-md rounded-xl md:rounded-2xl flex items-center justify-center text-white hover:bg-white hover:text-gray-900 transition-all"
                >
                  <X className="w-5 h-5 md:w-6 md:h-6" />
                </button>
                <div className="absolute bottom-4 left-6 right-6 md:bottom-6 md:left-8 md:right-8">
                  <h2 className="text-xl md:text-3xl font-black text-white tracking-tight leading-tight">{poi.name}</h2>
                </div>
              </div>

              <div className="p-5 md:p-8 overflow-y-auto font-sans custom-scrollbar">
                <div className="bg-gray-50 rounded-xl md:rounded-2xl p-4 md:p-6 mb-6 md:mb-8 border border-gray-100 italic text-gray-600 text-sm md:text-base leading-relaxed">
                  "{poi.narrative}"
                </div>

                <div className="bg-indigo-50 rounded-[1.2rem] md:rounded-[2rem] p-5 md:p-8 border-2 border-indigo-100 relative">
                  <div className="flex justify-between items-center mb-4 md:mb-6">
                    <div className="flex items-center gap-1.5 text-indigo-600 font-black uppercase text-[10px] md:text-xs tracking-widest">
                      <HelpCircle className="w-4 h-4 md:w-5 md:h-5" /> Sfida
                    </div>
                    
                    <button 
                      onClick={useHint}
                      disabled={hints < 5 || disabledOptions.length >= quiz.options.length - 1 || showResult}
                      className={`flex items-center gap-1.5 px-3 py-1.5 md:px-4 md:py-2 rounded-lg md:rounded-xl font-black text-[10px] md:text-xs uppercase transition-all ${
                        hints >= 5 && !showResult ? 'bg-yellow-400 text-yellow-900 shadow-md' : 'bg-gray-200 text-gray-400 shadow-none'
                      }`}
                    >
                      <Zap className="w-3.5 h-3.5 md:w-4 md:h-4 fill-current" /> Aiuto
                    </button>
                  </div>

                  <p className="text-base md:text-xl font-bold text-gray-800 mb-4 md:mb-6 leading-snug">
                    {quiz.question}
                  </p>

                  <div className="grid grid-cols-1 gap-2.5 md:gap-3">
                    {quiz.options.map((option: string, index: number) => {
                      const isDisabled = disabledOptions.includes(index);
                      const isSelected = selectedAnswer === index;

                      return (
                        <motion.button
                          key={index}
                          onClick={() => !showResult && handleAnswerSelect(index)}
                          disabled={showResult || isDisabled}
                          className={`w-full text-left p-3.5 md:p-5 rounded-xl md:rounded-2xl border-2 font-bold text-sm md:text-base transition-all flex items-center justify-between ${
                            isDisabled 
                              ? 'bg-red-50 border-red-100 text-red-300 opacity-60' 
                              : isSelected
                                ? isCorrect 
                                  ? 'bg-green-500 border-green-500 text-white' 
                                  : 'bg-red-500 border-red-500 text-white'
                                : 'bg-white border-white hover:border-indigo-300 text-gray-700'
                          }`}
                        >
                          <span className="flex-1 pr-2">{option}</span>
                          {isSelected && showResult && (
                            <div className="shrink-0">
                              {isCorrect ? <CheckCircle className="w-5 h-5 md:w-6 md:h-6" /> : <XCircle className="w-5 h-5 md:w-6 md:h-6" />}
                            </div>
                          )}
                        </motion.button>
                      );
                    })}
                  </div>

                  <AnimatePresence>
                    {showResult && (
                      <motion.div
                        initial={{ opacity: 0, y: 10 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0 }}
                        className={`mt-4 md:mt-6 p-3 md:p-4 rounded-xl md:rounded-2xl text-center font-bold text-xs md:text-sm ${
                          isCorrect ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'
                        }`}
                      >
                        {isCorrect ? "✨ Ottimo lavoro!" : "❌ Riprova!"}
                        <p className="text-[9px] md:text-[10px] uppercase opacity-70 mt-1">{quiz.explanation}</p>
                      </motion.div>
                    )}
                  </AnimatePresence>
                </div>
              </div>
            </motion.div>
          </div>
        </>
      )}
    </AnimatePresence>
  );
};