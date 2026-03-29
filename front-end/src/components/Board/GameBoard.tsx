import { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Zap, Coins, Trophy, Map as MapIcon, ChevronLeft, Sparkles, Share2, Home, X } from 'lucide-react';
import { useGame } from '../../contexts/GameContext';
import { POINode } from './POINode';
import { POIModal } from './POIModal';

export const GameBoard = () => {
  const { pois, unlockNextPOI, completePOI, score, setScore, hints, setHints, setCurrentView, userProfile } = useGame();
  const [selectedPOI, setSelectedPOI] = useState<any>(null);
  const [showFinalSummary, setShowFinalSummary] = useState(false);

  const adventure = userProfile as any;
  const completedCount = pois.filter(p => p.status === 'completed').length;
  const totalCount = pois.length;

  const getMultiplier = () => {
    const diff = adventure?.difficulty?.toLowerCase();
    if (diff === 'alto') return 1.5;
    if (diff === 'medio') return 1.2;
    return 1.0;
  };

  const multiplier = getMultiplier();
  const finalScore = Math.round(score * multiplier);

  useEffect(() => {
    if (totalCount > 0 && completedCount === totalCount) {
      const timer = setTimeout(() => {
        setHints(prev => prev + 5);
        setShowFinalSummary(true);
      }, 1500);
      return () => clearTimeout(timer);
    }
  }, [completedCount, totalCount]);

  const handleShare = () => {
    const text = `Ho completato "${adventure?.adventureTitle}" a ${adventure?.city} con ${finalScore} punti! 🚀`;
    window.open(`https://wa.me/?text=${encodeURIComponent(text)}`, '_blank');
  };

  return (
    <div className="min-h-screen bg-orange-50/30 pb-10 md:pb-20 font-sans overflow-x-hidden">
      {/* TOP BAR - Più compatta su mobile */}
      <div className="sticky top-0 z-30 w-full bg-white/90 backdrop-blur-md border-b border-orange-100 p-2 md:p-4 shadow-sm">
        <div className="max-w-6xl mx-auto flex items-center justify-between gap-2">
          <button onClick={() => setCurrentView('setup')} className="flex items-center gap-1 text-gray-400 hover:text-orange-600 font-bold text-[10px] md:text-xs uppercase tracking-widest transition-colors shrink-0">
            <ChevronLeft className="w-3 h-3 md:w-4 md:h-4" /> <span className="hidden xs:inline">Esci</span>
          </button>
          
          <div className="flex items-center gap-1.5 md:gap-4">
            <div className="bg-yellow-400 text-yellow-900 px-2 py-1 md:px-4 md:py-2 rounded-xl md:rounded-2xl shadow-[0_2px_0_0_#ca8a04] md:shadow-[0_4px_0_0_#ca8a04] font-black flex items-center gap-1 md:gap-2 text-xs md:text-base">
              <Zap className="w-3 h-3 md:w-4 md:h-4 fill-current" /> {hints}
            </div>
            <div className="bg-white px-2 py-1 md:px-4 md:py-2 rounded-xl md:rounded-2xl shadow-md border border-orange-100 font-black text-gray-800 flex items-center gap-1 md:gap-2 text-xs md:text-base">
              <Coins className="w-3 h-3 md:w-4 md:h-4 text-orange-500" /> {score}
            </div>
            <div className="bg-indigo-600 text-white px-2 py-1 md:px-4 md:py-2 rounded-xl md:rounded-2xl font-black flex items-center gap-1 md:gap-2 shadow-lg text-xs md:text-base">
              <Trophy className="w-3 h-3 md:w-4 md:h-4" /> {completedCount}/{totalCount}
            </div>
          </div>
        </div>
      </div>

      {/* TITOLO E NARRATIVA - Ridimensionati per mobile */}
      <div className="max-w-4xl mx-auto pt-6 md:pt-12 px-4 md:px-6 text-center space-y-3 md:space-y-6">
        <div className="inline-flex items-center gap-2 px-3 py-1 bg-white border border-emerald-100 text-emerald-700 rounded-full text-[8px] md:text-[10px] font-black uppercase tracking-[0.1em] md:tracking-[0.2em] shadow-sm">
          <MapIcon className="w-3 h-3" /> {adventure?.city} • {adventure?.archetype}
        </div>
        <h1 className="text-3xl md:text-7xl font-black text-gray-900 tracking-tighter leading-tight drop-shadow-sm px-2">
          {adventure?.adventureTitle}
        </h1>
        <p className="text-gray-500 text-sm md:text-xl italic max-w-2xl mx-auto leading-relaxed px-4 border-l-2 md:border-l-4 border-orange-200">
          "{adventure?.overallNarrative}"
        </p>
      </div>

      {/* BOARD STYLE "GIOCO DELL'OCA" - Grid ottimizzata */}
      <div className="max-w-6xl mx-auto p-4 md:p-20 mt-6 md:mt-10 relative">
        {/* Grid: 2 colonne su mobile, 3 su desktop per ridurre lo scrolling */}
        <div className="grid grid-cols-2 lg:grid-cols-3 gap-4 md:gap-24 relative z-10">
          {pois.map((poi, index) => (
            <POINode 
              key={poi.id} 
              poi={poi} 
              index={index} 
              onClick={() => poi.status !== 'locked' && setSelectedPOI(poi)} 
            />
          ))}
        </div>

        {/* LINEA DI PERCORSO - Nascosta su mobile per pulizia */}
        <svg className="absolute inset-0 w-full h-full pointer-events-none opacity-20 hidden lg:block" style={{ minHeight: '600px' }}>
          <path
            d="M 150 200 C 300 200, 500 400, 800 400 S 1000 600, 200 800"
            fill="transparent"
            stroke="#f97316"
            strokeWidth="10"
            strokeLinecap="round"
            strokeDasharray="20 30"
          />
        </svg>
      </div>

      {/* MODALE POI */}
      <POIModal
        poi={selectedPOI}
        isOpen={!!selectedPOI}
        onClose={() => setSelectedPOI(null)}
        onComplete={() => {
          if (selectedPOI && selectedPOI.status !== 'completed') {
            completePOI(selectedPOI.id);
            unlockNextPOI(selectedPOI.id);
          }
        }}
      />

      {/* MODALE RIEPILOGO FINALE - Adattata per mobile */}
      <AnimatePresence>
        {showFinalSummary && (
          <div className="fixed inset-0 z-[200] flex items-center justify-center p-2 md:p-4 bg-gray-900/90 backdrop-blur-md">
            <motion.div 
              initial={{ scale: 0.9, opacity: 0, y: 50 }}
              animate={{ scale: 1, opacity: 1, y: 0 }}
              exit={{ scale: 0.9, opacity: 0, y: 50 }}
              className="bg-white rounded-[2rem] md:rounded-[3.5rem] p-6 md:p-12 max-w-xl w-full text-center shadow-2xl relative max-h-[95vh] overflow-y-auto"
            >
              <button 
                onClick={() => setShowFinalSummary(false)}
                className="absolute top-4 right-4 md:top-8 md:right-8 p-1.5 md:p-2 bg-gray-100 hover:bg-gray-200 rounded-full transition-colors"
              >
                <X className="w-4 h-4 md:w-6 md:h-6 text-gray-500" />
              </button>

              <div className="w-12 h-12 md:w-20 md:h-20 bg-yellow-400 rounded-2xl md:rounded-3xl flex items-center justify-center mx-auto mb-4 md:mb-6 shadow-lg">
                <Trophy className="w-6 h-6 md:w-10 md:h-10 text-white" />
              </div>
              
              <h2 className="text-2xl md:text-4xl font-black text-gray-900 mb-1 md:mb-2 uppercase italic tracking-tighter">Vittoria!</h2>
              <p className="text-gray-400 text-sm md:text-base mb-6 md:mb-8 font-medium">Hai completato il percorso con onore.</p>

              <div className="space-y-3 md:space-y-4 mb-6 md:mb-8 text-left">
                <div className="bg-orange-50 p-4 md:p-6 rounded-2xl md:rounded-3xl border-2 border-orange-100 relative overflow-hidden">
                  <div className="flex justify-between items-end relative z-10">
                    <div>
                      <p className="text-[8px] md:text-[10px] font-black uppercase text-orange-400 tracking-widest mb-1">Punteggio Totale</p>
                      <p className="text-3xl md:text-5xl font-black text-orange-600">{finalScore}</p>
                    </div>
                    <div className="text-right">
                      <p className="text-[8px] md:text-[10px] font-bold text-gray-400 uppercase tracking-tighter">Moltiplicatore {adventure?.difficulty}</p>
                      <p className="text-base md:text-xl font-black text-gray-800">x{multiplier}</p>
                    </div>
                  </div>
                  <Coins className="absolute -right-4 -bottom-4 w-16 h-16 md:w-24 md:h-24 text-orange-200/50 rotate-12" />
                </div>
                
                <div className="bg-yellow-400 p-3 md:p-4 rounded-xl md:rounded-2xl flex items-center justify-center gap-2 md:gap-3 text-yellow-900 font-black text-xs md:text-sm shadow-md">
                  <Zap className="w-4 h-4 md:w-5 md:h-5 fill-current" /> +5 FULMINI BONUS AGGIUNTI!
                </div>
              </div>

              <div className="flex flex-col gap-2 md:gap-3">
                <button 
                  onClick={handleShare}
                  className="w-full flex items-center justify-center gap-2 md:gap-3 bg-indigo-600 text-white py-3 md:py-5 rounded-xl md:rounded-2xl font-black uppercase tracking-widest hover:bg-indigo-700 transition-all shadow-xl text-xs md:text-base"
                >
                  <Share2 className="w-4 h-4" /> Condividi
                </button>
                <button 
                  onClick={() => window.location.reload()}
                  className="w-full flex items-center justify-center gap-2 bg-gray-100 text-gray-500 py-2.5 md:py-4 rounded-xl md:rounded-2xl font-bold uppercase text-[10px] md:text-xs hover:bg-gray-200 transition-all"
                >
                  <Home className="w-3 h-3" /> Home
                </button>
              </div>
            </motion.div>
          </div>
        )}
      </AnimatePresence>
    </div>
  );
};