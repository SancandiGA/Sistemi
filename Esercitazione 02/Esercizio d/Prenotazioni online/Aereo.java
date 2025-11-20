import java.util.HashMap;
import java.util.Map;

public class Aereo {
    // Matrice che rappresenta la disposizione dei posti (es. 5 righe x 4 colonne)
    private static final int NUM_RIGHE = 5;
    private static final int NUM_COLONNE = 4;
    private final Posto[][] posti;
    
    // Per un'autenticazione semplice (solo per esempio)
    private final Map<String, String> utentiRegistrati; 

    public Aereo() {
        this.posti = new Posto[NUM_RIGHE][NUM_COLONNE];
        inizializzaPosti();
        
        this.utentiRegistrati = new HashMap<>();
        utentiRegistrati.put("alice", "pass123");
        utentiRegistrati.put("bob", "password");
    }

    private void inizializzaPosti() {
        char[] colonne = {'A', 'B', 'C', 'D'};
        for (int i = 0; i < NUM_RIGHE; i++) {
            for (int j = 0; j < NUM_COLONNE; j++) {
                // Le righe partono da 1
                posti[i][j] = new Posto(i + 1, colonne[j]); 
            }
        }
    }
    
    // --- Metodi di Autenticazione ---
    
    public boolean autentica(String nomeUtente, String password) {
        return utentiRegistrati.containsKey(nomeUtente) && 
               utentiRegistrati.get(nomeUtente).equals(password);
    }

    // --- Metodi di Gestione Posti ---
    
    // Restituisce il posto specificato (riga e colonna char)
    private Posto getPosto(int riga, char colonna) {
        if (riga < 1 || riga > NUM_RIGHE) return null;
        
        int indiceRiga = riga - 1;
        int indiceColonna = -1;
        
        char[] colonne = {'A', 'B', 'C', 'D'};
        for (int j = 0; j < colonne.length; j++) {
            if (colonne[j] == Character.toUpperCase(colonna)) {
                indiceColonna = j;
                break;
            }
        }
        
        if (indiceColonna < 0 || indiceColonna >= NUM_COLONNE) return null;
        
        return posti[indiceRiga][indiceColonna];
    }
    
    // Esegue l'azione di prenotazione
    public String prenotaPosto(int riga, char colonna, String nomeUtente) {
        Posto posto = getPosto(riga, colonna);
        
        if (posto == null) {
            return "ERRORE: Posto non valido (es. 1A, 5D).";
        }
        
        if (posto.isPrenotato() && nomeUtente.equals(posto.getUtentePrenotato())) {
            return "Hai già prenotato il posto " + posto.toString() + ".";
        }
        
        if (posto.prenota(nomeUtente)) {
            return "SUCCESSO: Posto " + posto.toString() + " prenotato per te.";
        } else {
            return "ERRORE: Posto " + posto.toString() + " è già prenotato da un altro utente.";
        }
    }
    
    // Esegue l'azione di cancellazione
    public String cancellaPrenotazione(int riga, char colonna, String nomeUtente) {
        Posto posto = getPosto(riga, colonna);
        
        if (posto == null) {
            return "ERRORE: Posto non valido (es. 1A, 5D).";
        }
        
        if (!posto.isPrenotato()) {
            return "ERRORE: Il posto " + posto.toString() + " non è prenotato.";
        }

        if (nomeUtente.equals(posto.getUtentePrenotato())) {
            if (posto.cancella(nomeUtente)) {
                return "SUCCESSO: Prenotazione del posto " + posto.toString() + " cancellata.";
            } else {
                 // Dovrebbe essere impossibile data la verifica dell'utente sopra, ma per sicurezza.
                 return "ERRORE: Cancellazione fallita. Riprova.";
            }
        } else {
            return "ERRORE: Il posto " + posto.toString() + " è prenotato da un altro utente.";
        }
    }

    // Crea la rappresentazione testuale della matrice per l'invio al client
    public String getMappaPosti() {
        StringBuilder sb = new StringBuilder();
        
        // Intestazione Colonne
        sb.append("   A B C D\n"); 
        
        for (int i = 0; i < NUM_RIGHE; i++) {
            // Numero Riga
            sb.append(String.format("%2d ", i + 1)); 
            for (int j = 0; j < NUM_COLONNE; j++) {
                sb.append(posti[i][j].getStato()).append(" ");
            }
            sb.append("\n");
        }
        
        // Legenda
        sb.append("\nLegenda: X = Prenotato, . = Libero\n");
        
        return sb.toString();
    }
}
