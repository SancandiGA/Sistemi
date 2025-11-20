public class Posto {
    private final int riga;
    private final char colonna;
    private boolean prenotato;
    private String utentePrenotato; // Memorizza l'utente che ha prenotato il posto

    public Posto(int riga, char colonna) {
        this.riga = riga;
        this.colonna = colonna;
        this.prenotato = false;
        this.utentePrenotato = null;
    }

    public int getRiga() { return riga; }
    public char getColonna() { return colonna; }
    public boolean isPrenotato() { return prenotato; }
    public String getUtentePrenotato() { return utentePrenotato; }

    // Metodo per prenotare il posto
    public synchronized boolean prenota(String nomeUtente) {
        if (!prenotato) {
            this.prenotato = true;
            this.utentePrenotato = nomeUtente;
            return true; // Prenotazione riuscita
        }
        return false; // Già prenotato
    }

    // Metodo per cancellare la prenotazione
    public synchronized boolean cancella(String nomeUtente) {
        // Può cancellare solo l'utente che ha prenotato il posto
        if (prenotato && nomeUtente.equals(this.utentePrenotato)) {
            this.prenotato = false;
            this.utentePrenotato = null;
            return true; // Cancellazione riuscita
        }
        return false; // Cancellazione fallita (o non prenotato, o prenotato da altri)
    }

    @Override
    public String toString() {
        return String.format("%d%c", riga, colonna);
    }
    
    // Rappresentazione per la matrice: 'X' se prenotato, '.' se libero
    public String getStato() {
        return prenotato ? "X" : ".";
    }
}
