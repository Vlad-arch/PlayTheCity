import { Quiz } from '../types';

export const quizData: Quiz = {
  id: "$s",
  version: "1.0",
  questions: [
    {
      questionId: "q1",
      text: "Sei davanti a un portone chiuso che dà su un tetto panoramico dove si dice ci sia la festa più vibey della città. Cosa fai?",
      options: [
        {
          optionId: "q1o1",
          label: "Cerco un modo alternativo per salire: scale di servizio, un vicolo nascosto, tutto pur di vedere la vista.",
          imageUrl: "https://images.pexels.com/photos/1105766/pexels-photo-1105766.jpeg?auto=compress&cs=tinysrgb&w=400",
          archetypeWeights: {
            urban_explorer: 0.9,
            social_connector: 0.0,
            planner_strategist: 0.1,
            local_insider: 0.0,
            scavenger_collector: 0.0
          }
        },
        {
          optionId: "q1o2",
          label: "Suono al campanello, mi presento e provo a entrare con garbo: magari conosco qualcuno dentro.",
          imageUrl: "https://images.pexels.com/photos/3184291/pexels-photo-3184291.jpeg?auto=compress&cs=tinysrgb&w=400",
          archetypeWeights: {
            urban_explorer: 0.1,
            social_connector: 0.8,
            planner_strategist: 0.1,
            local_insider: 0.0,
            scavenger_collector: 0.0
          }
        },
        {
          optionId: "q1o3",
          label: "Controllo l'evento su app, leggo recensioni e vedo il miglior orario/ingresso: ottimizzare è vita.",
          imageUrl: "https://images.pexels.com/photos/887751/pexels-photo-887751.jpeg?auto=compress&cs=tinysrgb&w=400",
          archetypeWeights: {
            urban_explorer: 0.0,
            social_connector: 0.1,
            planner_strategist: 0.9,
            local_insider: 0.0,
            scavenger_collector: 0.0
          }
        },
        {
          optionId: "q1o4",
          label: "Cerco il bar o il locale sotto: magari c'è un conoscente del posto che ci porta su dopo.",
          imageUrl: "https://images.pexels.com/photos/1267696/pexels-photo-1267696.jpeg?auto=compress&cs=tinysrgb&w=400",
          archetypeWeights: {
            urban_explorer: 0.2,
            social_connector: 0.3,
            planner_strategist: 0.0,
            local_insider: 0.7,
            scavenger_collector: 0.0
          }
        }
      ]
    },
    {
      questionId: "q2",
      text: "Ti imbatti in un mercato di quartiere pieno di bancarelle, musiche e odori. Hai 30 minuti liberi: come li spendi?",
      options: [
        {
          optionId: "q2o1",
          label: "Mi perdo tra le bancarelle senza meta, prendo quello che attira di più e scatto foto di vibe.",
          imageUrl: "https://images.pexels.com/photos/1268101/pexels-photo-1268101.jpeg?auto=compress&cs=tinysrgb&w=400",
          archetypeWeights: {
            urban_explorer: 0.7,
            social_connector: 0.1,
            planner_strategist: 0.0,
            local_insider: 0.2,
            scavenger_collector: 0.4
          }
        },
        {
          optionId: "q2o2",
          label: "Mi unisco a un gruppo o tour improvvisato per ascoltare storie e fare nuove conoscenze.",
          imageUrl: "https://images.pexels.com/photos/1058277/pexels-photo-1058277.jpeg?auto=compress&cs=tinysrgb&w=400",
          archetypeWeights: {
            urban_explorer: 0.2,
            social_connector: 0.9,
            planner_strategist: 0.0,
            local_insider: 0.3,
            scavenger_collector: 0.0
          }
        },
        {
          optionId: "q2o3",
          label: "Traccio una mini-mappa mentale delle bancarelle da non perdere e torno dopo con calma.",
          imageUrl: "https://images.pexels.com/photos/1170412/pexels-photo-1170412.jpeg?auto=compress&cs=tinysrgb&w=400",
          archetypeWeights: {
            urban_explorer: 0.1,
            social_connector: 0.0,
            planner_strategist: 0.9,
            local_insider: 0.2,
            scavenger_collector: 0.1
          }
        },
        {
          optionId: "q2o4",
          label: "Parlo con i venditori locali per capire i segreti del quartiere: storie, ricette, insider tips.",
          imageUrl: "https://images.pexels.com/photos/1109197/pexels-photo-1109197.jpeg?auto=compress&cs=tinysrgb&w=400",
          archetypeWeights: {
            urban_explorer: 0.1,
            social_connector: 0.4,
            planner_strategist: 0.0,
            local_insider: 0.9,
            scavenger_collector: 0.2
          }
        }
      ]
    },
    {
      questionId: "q3",
      text: "È sabato sera e hai tre opzioni: un concerto underground, una mostra d'arte interattiva o una cena con foodie locali. Cosa scegli?",
      options: [
        {
          optionId: "q3o1",
          label: "Il concerto underground: voglio scoprire nuovi artisti e perdermi nella musica.",
          imageUrl: "https://images.pexels.com/photos/1105666/pexels-photo-1105666.jpeg?auto=compress&cs=tinysrgb&w=400",
          archetypeWeights: {
            urban_explorer: 0.8,
            social_connector: 0.2,
            planner_strategist: 0.0,
            local_insider: 0.1,
            scavenger_collector: 0.3
          }
        },
        {
          optionId: "q3o2",
          label: "La mostra d'arte: amo gli spazi che stimolano conversazioni e connessioni.",
          imageUrl: "https://images.pexels.com/photos/1646953/pexels-photo-1646953.jpeg?auto=compress&cs=tinysrgb&w=400",
          archetypeWeights: {
            urban_explorer: 0.3,
            social_connector: 0.7,
            planner_strategist: 0.1,
            local_insider: 0.2,
            scavenger_collector: 0.5
          }
        },
        {
          optionId: "q3o3",
          label: "La cena con foodie: ho già prenotato, letto recensioni e preparato domande per gli chef.",
          imageUrl: "https://images.pexels.com/photos/1001773/pexels-photo-1001773.jpeg?auto=compress&cs=tinysrgb&w=400",
          archetypeWeights: {
            urban_explorer: 0.0,
            social_connector: 0.3,
            planner_strategist: 0.9,
            local_insider: 0.4,
            scavenger_collector: 0.1
          }
        },
        {
          optionId: "q3o4",
          label: "La cena con foodie: voglio sentire le storie dei local e scoprire posti autentici.",
          imageUrl: "https://images.pexels.com/photos/1267696/pexels-photo-1267696.jpeg?auto=compress&cs=tinysrgb&w=400",
          archetypeWeights: {
            urban_explorer: 0.2,
            social_connector: 0.4,
            planner_strategist: 0.1,
            local_insider: 0.9,
            scavenger_collector: 0.2
          }
        }
      ]
    }
  ]
};
