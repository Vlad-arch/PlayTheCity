import { POI } from '../types';

export const generateMockPOIs = (city: string): POI[] => {
  return [
    {
      id: 'poi-1',
      title: 'Piazza Storica',
      description: `Nel cuore di ${city}, questa piazza rappresenta secoli di storia. Circondata da palazzi storici e caffè caratteristici, è il punto di ritrovo perfetto per iniziare l'avventura urbana.`,
      imageUrl: 'https://images.pexels.com/photos/2138922/pexels-photo-2138922.jpeg?auto=compress&cs=tinysrgb&w=800',
      status: 'unlocked',
      challenge: {
        question: 'Quale elemento architettonico è più comune nelle piazze storiche italiane?',
        options: [
          'Grattacieli moderni',
          'Fontane e obelischi',
          'Centri commerciali',
          'Parcheggi sotterranei'
        ],
        correctAnswer: 1
      }
    },
    {
      id: 'poi-2',
      title: 'Mercato Locale',
      description: `Un'esplosione di colori, profumi e sapori. Il mercato di ${city} è dove i local fanno la spesa e dove puoi scoprire le vere tradizioni culinarie della città.`,
      imageUrl: 'https://images.pexels.com/photos/1192032/pexels-photo-1192032.jpeg?auto=compress&cs=tinysrgb&w=800',
      status: 'locked',
      challenge: {
        question: 'Qual è il momento migliore per visitare un mercato locale?',
        options: [
          'Tarda sera',
          'Mattina presto',
          'Dopo mezzanotte',
          'Durante la pausa pranzo'
        ],
        correctAnswer: 1
      }
    },
    {
      id: 'poi-3',
      title: 'Street Art Quarter',
      description: `Il quartiere più colorato e artistico di ${city}. Murales giganti, installazioni creative e una scena artistica vibrante ti aspettano ad ogni angolo.`,
      imageUrl: 'https://images.pexels.com/photos/1084510/pexels-photo-1084510.jpeg?auto=compress&cs=tinysrgb&w=800',
      status: 'locked',
      challenge: {
        question: 'Cosa rende speciale la street art nelle città moderne?',
        options: [
          'Costa molto',
          'È temporanea per definizione',
          'Democratizza l\'arte portandola nelle strade',
          'Richiede permessi difficili'
        ],
        correctAnswer: 2
      }
    },
    {
      id: 'poi-4',
      title: 'Rooftop Panoramico',
      description: `La vista più spettacolare di ${city}. Da qui puoi vedere tutta la città dall'alto e capire come si sviluppa il tessuto urbano tra vecchio e nuovo.`,
      imageUrl: 'https://images.pexels.com/photos/1769405/pexels-photo-1769405.jpeg?auto=compress&cs=tinysrgb&w=800',
      status: 'locked',
      challenge: {
        question: 'Perché i rooftop sono diventati così popolari nelle città?',
        options: [
          'Sono economici',
          'Offrono prospettive uniche e spazi di socializzazione',
          'Hanno sempre il WiFi',
          'Sono obbligatori per legge'
        ],
        correctAnswer: 1
      }
    },
    {
      id: 'poi-5',
      title: 'Vicolo Nascosto',
      description: `Un segreto che solo i local conoscono. Questo vicolo di ${city} nasconde una storia affascinante e un'atmosfera magica che ti farà sentire in un'altra epoca.`,
      imageUrl: 'https://images.pexels.com/photos/1034662/pexels-photo-1034662.jpeg?auto=compress&cs=tinysrgb&w=800',
      status: 'locked',
      challenge: {
        question: 'Cosa rende speciali i vicoli nascosti delle città?',
        options: [
          'Sono sempre deserti',
          'Preservano storie e atmosfere autentiche',
          'Hanno segnaletica moderna',
          'Sono accessibili in auto'
        ],
        correctAnswer: 1
      }
    }
  ];
};
