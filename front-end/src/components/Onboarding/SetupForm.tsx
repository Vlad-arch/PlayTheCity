import { useState, useEffect, useRef } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { MapPin, Sparkles, User, ChevronDown, Loader2, ArrowRight } from 'lucide-react';

interface SetupFormProps {
  onComplete: (age: string, city: string, coords: { lat: string; lon: string } | null) => void;
}

const AGE_RANGES = [
  { value: " <18", label: "Meno di 18 anni" },
  { value: "18-24", label: "18 – 24 anni" },
  { value: "25-30", label: "25 – 30 anni" },
  { value: "31-40", label: "31 – 40 anni" },
  { value: "41-50", label: "41 – 50 anni" },
  { value: "51-65", label: "51 – 65 anni" },
  { value: ">65", label: "Oltre 65 anni" },
];

export const SetupForm = ({ onComplete }: SetupFormProps) => {
  const [age, setAge] = useState('');
  const [city, setCity] = useState('');
  const [cityInput, setCityInput] = useState('');
  const [selectedCoords, setSelectedCoords] = useState<{ lat: string; lon: string } | null>(null);
  const [suggestions, setSuggestions] = useState<any[]>([]);
  const [loadingCities, setLoadingCities] = useState(false);
  const [showSuggestions, setShowSuggestions] = useState(false);
  const debounceRef = useRef<NodeJS.Timeout | null>(null);

  // Logica Autocomplete con estrazione coordinate
  useEffect(() => {
    if (cityInput.length < 3) {
      setSuggestions([]);
      return;
    }

    // Evita di cercare se l'input corrente è esattamente uguale alla città già selezionata
    if (cityInput === city) return;

    if (debounceRef.current) clearTimeout(debounceRef.current);

    debounceRef.current = setTimeout(async () => {
      setLoadingCities(true);
      try {
        const res = await fetch(
          `https://nominatim.openstreetmap.org/search?q=${encodeURIComponent(cityInput)}&addressdetails=1&limit=6&format=json`,
          { headers: { "Accept-Language": "it" } }
        );
        const data = await res.json();
        
        const cities = data.map((d: any) => ({
          name: d.address.city || d.address.town || d.address.village || d.address.municipality || d.display_name.split(",")[0],
          lat: d.lat,
          lon: d.lon,
          full_address: d.display_name
        }))
        // Rimuove duplicati basati sul nome della città
        .filter((v: any, i: number, a: any[]) => v.name && a.findIndex(t => t.name === v.name) === i);

        setSuggestions(cities);
        setShowSuggestions(true);
      } catch (error) {
        console.error("Errore ricerca città:", error);
      } finally {
        setLoadingCities(false);
      }
    }, 400);

    return () => {
      if (debounceRef.current) clearTimeout(debounceRef.current);
    };
  }, [cityInput, city]);

  const selectCity = (cityObj: any) => {
    setCity(cityObj.name);
    setCityInput(cityObj.name);
    setSelectedCoords({ lat: cityObj.lat, lon: cityObj.lon });
    setShowSuggestions(false);
    setSuggestions([]);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (age && city) {
      onComplete(age, city, selectedCoords);
    }
  };

  const canContinue = age !== '' && city !== '';

  return (
    <div className="min-h-screen bg-gradient-to-br from-orange-400 via-pink-500 to-purple-600 flex items-center justify-center p-4 font-sans text-gray-800">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="bg-white rounded-3xl shadow-2xl p-8 max-w-md w-full"
      >
        {/* Icona Animata */}
        <motion.div
          initial={{ scale: 0 }}
          animate={{ scale: 1 }}
          transition={{ delay: 0.2, type: 'spring', stiffness: 200 }}
          className="w-20 h-20 bg-gradient-to-br from-orange-400 to-pink-500 rounded-2xl flex items-center justify-center mx-auto mb-6 shadow-lg rotate-3"
        >
          <Sparkles className="w-10 h-10 text-white" />
        </motion.div>

        <h1 className="text-4xl font-black text-center mb-2 bg-gradient-to-r from-orange-500 to-pink-600 bg-clip-text text-transparent italic tracking-tight">
          Ludopolis
        </h1>
        <p className="text-gray-500 text-center mb-10 text-sm font-medium">
          Tutto questo ci aiuterà a creare una avventura fantastica pensata proprio per te!
        </p>

        <form onSubmit={handleSubmit} className="space-y-8">
          {/* Selettore Età */}
          <div>
            <label className="flex items-center gap-2 text-xs font-bold uppercase tracking-widest text-gray-400 mb-3 ml-1">
              <User className="w-4 h-4 text-orange-500" />
              Quanti anni hai?
            </label>
            <div className="relative">
              <select
                value={age}
                onChange={(e) => setAge(e.target.value)}
                required
                className="w-full px-5 py-4 rounded-2xl border-2 border-gray-100 bg-gray-50 focus:border-pink-500 focus:bg-white focus:outline-none transition-all appearance-none cursor-pointer font-semibold"
              >
                <option value="" disabled>Seleziona la tua fascia d'età</option>
                {AGE_RANGES.map((range) => (
                  <option key={range.value} value={range.value}>
                    {range.label}
                  </option>
                ))}
              </select>
              <ChevronDown className="absolute right-5 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400 pointer-events-none" />
            </div>
          </div>

          {/* Input Città con Autocomplete */}
          <div className="relative">
            <label className="flex items-center gap-2 text-xs font-bold uppercase tracking-widest text-gray-400 mb-3 ml-1">
              <MapPin className="w-4 h-4 text-pink-500" />
              Dove inizia il viaggio?
            </label>
            <div className="relative">
              <input
                type="text"
                value={cityInput}
                onChange={(e) => {
                  setCityInput(e.target.value);
                  if (city) {
                    setCity('');
                    setSelectedCoords(null);
                  }
                }}
                onFocus={() => suggestions.length > 0 && setShowSuggestions(true)}
                onBlur={() => setTimeout(() => setShowSuggestions(false), 200)}
                placeholder="Es. Roma, Firenze, Milano..."
                required
                autoComplete="off"
                className="w-full pl-5 pr-14 py-4 rounded-2xl border-2 border-gray-100 bg-gray-50 focus:border-pink-500 focus:bg-white focus:outline-none transition-all font-semibold"
              />
              <div className="absolute right-5 top-1/2 -translate-y-1/2">
                {loadingCities ? (
                  <Loader2 className="w-6 h-6 text-pink-500 animate-spin" />
                ) : (
                  <MapPin className={`w-6 h-6 transition-colors ${selectedCoords ? 'text-pink-500' : 'text-gray-300'}`} />
                )}
              </div>
            </div>

            {/* Lista Suggerimenti */}
            <AnimatePresence>
              {showSuggestions && suggestions.length > 0 && (
                <motion.ul
                  initial={{ opacity: 0, y: -10 }}
                  animate={{ opacity: 1, y: 0 }}
                  exit={{ opacity: 0, y: -10 }}
                  className="absolute z-50 w-full mt-3 bg-white border-2 border-gray-50 rounded-2xl shadow-2xl overflow-hidden max-h-60 overflow-y-auto"
                >
                  {suggestions.map((s, index) => (
                    <li
                      key={index}
                      onMouseDown={() => selectCity(s)}
                      className="flex items-center gap-4 px-5 py-4 hover:bg-pink-50 cursor-pointer transition-colors border-b last:border-none border-gray-50"
                    >
                      <div className="w-8 h-8 rounded-full bg-pink-100 flex items-center justify-center shrink-0">
                        <MapPin className="w-4 h-4 text-pink-500" />
                      </div>
                      <div className="flex flex-col">
                        <span className="text-sm font-bold text-gray-700">{s.name}</span>
                        <span className="text-[10px] text-gray-400 truncate max-w-[250px]">{s.full_address}</span>
                      </div>
                    </li>
                  ))}
                </motion.ul>
              )}
            </AnimatePresence>
          </div>

          {/* Bottone Submit */}
          <motion.button
            type="submit"
            disabled={!canContinue}
            whileHover={canContinue ? { scale: 1.02 } : {}}
            whileTap={canContinue ? { scale: 0.98 } : {}}
            className={`w-full py-5 flex items-center justify-center gap-3 text-white font-black rounded-2xl shadow-xl transition-all uppercase tracking-tighter ${
              canContinue 
                ? 'bg-gradient-to-r from-orange-500 to-pink-600 hover:shadow-orange-200 opacity-100' 
                : 'bg-gray-200 cursor-not-allowed text-gray-400'
            }`}
          >
            Inizia l'Avventura
            <ArrowRight className={`w-5 h-5 transition-transform ${canContinue ? 'translate-x-0 group-hover:translate-x-1' : ''}`} />
          </motion.button>
        </form>
      </motion.div>
    </div>
  );
};