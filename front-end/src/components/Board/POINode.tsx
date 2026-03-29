import { motion } from 'framer-motion';
import { Lock, CheckCircle2, MapPin, Sparkles } from 'lucide-react';
import { POI } from '../../types';

interface POINodeProps {
  poi: any; // Usiamo any per gestire i nuovi campi dal backend
  index: number;
  onClick: () => void;
}

export const POINode = ({ poi, index, onClick }: POINodeProps) => {
  // Mappatura flessibile dei nomi dei campi
  const title = poi.locationName || poi.name || poi.title || "Tappa sconosciuta";
  const isLocked = poi.status === 'locked';
  const isCompleted = poi.status === 'completed';
  const isDiscovery = poi.status === 'discovery';

  return (
    <motion.div
      initial={{ opacity: 0, scale: 0.8 }}
      animate={{ opacity: 1, scale: 1 }}
      transition={{ delay: index * 0.1 }}
      className="relative group"
    >
      {/* Etichetta numerica fluttuante */}
      <div className={`absolute -top-4 -left-4 w-10 h-10 rounded-xl flex items-center justify-center font-black z-20 shadow-lg transition-transform group-hover:-rotate-12 ${
        isLocked ? 'bg-gray-300 text-gray-500' : 'bg-gradient-to-br from-orange-500 to-pink-600 text-white'
      }`}>
        {index + 1}
      </div>

      <motion.button
        onClick={onClick}
        disabled={isLocked}
        whileHover={!isLocked ? { y: -10, scale: 1.02 } : {}}
        whileTap={!isLocked ? { scale: 0.98 } : {}}
        className={`relative w-full aspect-[4/5] rounded-[2rem] overflow-hidden border-4 transition-all duration-500 shadow-xl ${
          isLocked 
            ? 'border-gray-200 grayscale opacity-80 cursor-not-allowed' 
            : isCompleted
            ? 'border-green-400 shadow-green-100'
            : 'border-white hover:border-pink-400 ring-4 ring-transparent hover:ring-pink-100'
        }`}
      >
        {/* Immagine di sfondo */}
        <img
          src={poi.imageUrl}
          alt={title}
          className="absolute inset-0 w-full h-full object-cover transition-transform duration-700 group-hover:scale-110"
        />
        
        {/* Overlay gradiente */}
        <div className={`absolute inset-0 bg-gradient-to-t transition-opacity duration-500 ${
          isLocked 
            ? 'from-gray-900/90 via-gray-900/40 to-transparent' 
            : 'from-black/80 via-black/20 to-transparent opacity-90 group-hover:opacity-100'
        }`} />

        {/* Contenuto Testuale */}
        <div className="absolute inset-x-0 bottom-0 p-6 text-left">
          {isDiscovery && (
            <motion.div 
              animate={{ opacity: [0.5, 1, 0.5] }}
              transition={{ repeat: Infinity, duration: 2 }}
              className="flex items-center gap-1 text-[10px] font-black uppercase tracking-tighter text-orange-400 mb-1"
            >
              <Sparkles className="w-3 h-3" />
              Tappa Attuale
            </motion.div>
          )}
          
          <h3 className="text-xl font-black text-white leading-tight mb-2 drop-shadow-md">
            {title}
          </h3>

          <div className="flex items-center justify-between">
            {isLocked ? (
              <div className="flex items-center gap-2 text-gray-400 font-bold text-xs uppercase tracking-widest">
                <Lock className="w-4 h-4" />
                Bloccato
              </div>
            ) : isCompleted ? (
              <div className="flex items-center gap-2 text-green-400 font-bold text-xs uppercase tracking-widest">
                <CheckCircle2 className="w-4 h-4" />
                Completato
              </div>
            ) : (
              <div className="flex items-center gap-2 text-pink-400 font-bold text-xs uppercase tracking-widest">
                <MapPin className="w-4 h-4" />
                Esplora
              </div>
            )}
          </div>
        </div>

        {/* Effetto Bagliore per la tappa attuale */}
        {isDiscovery && (
          <div className="absolute inset-0 bg-gradient-to-tr from-orange-500/20 to-pink-500/20 pointer-events-none animate-pulse" />
        )}
      </motion.button>

      {/* Linea di connessione decorativa per mobile (opzionale) */}
      <div className="md:hidden absolute -bottom-6 left-1/2 -translate-x-1/2 w-1 h-6 bg-gradient-to-b from-gray-200 to-transparent" />
    </motion.div>
  );
};