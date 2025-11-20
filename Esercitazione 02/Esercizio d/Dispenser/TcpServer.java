import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpServer {
    private static final int PORT = 8080;
    // Contatore statico e condiviso per assegnare i numeri progressivi
    private static int progressiveCounter = 0; 
    
    // Oggetto usato come "lucchetto" per la sincronizzazione del contatore
    private static final Object counterLock = new Object(); 

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server TCP avviato sulla porta " + PORT);

            while (true) {
                // Il server accetta una connessione client
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connesso: " + clientSocket.getRemoteSocketAddress());

                // Crea un gestore in un thread separato per ogni client
                Runnable clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.err.println("Errore server: " + e.getMessage());
        }
    }
    
    // Metodo sincronizzato per ottenere il prossimo numero progressivo
    public static int getNextProgressiveNumber() {
        // Il blocco synchronized assicura che solo un thread alla volta 
        // possa eseguire il codice al suo interno, prevenendo errori sul contatore.
        synchronized (counterLock) {
            // Pre-incrementa: prima incrementa, poi restituisce il nuovo valore
            return ++progressiveCounter; 
        }
    }

    // Classe interna che implementa Runnable per gestire il client
    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            int currentNumber = 0;
            // Ottiene il numero progressivo non appena il client si connette
            currentNumber = TcpServer.getNextProgressiveNumber(); 
            System.out.println("Assegnato numero " + currentNumber + " al client " + clientSocket.getRemoteSocketAddress());
            
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(
                    clientSocket.getOutputStream(), true)) {

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Client " + currentNumber + " ha inviato: " + inputLine);
                    
                    // Invia il numero progressivo come risposta, ignorando il messaggio inviato
                    out.println("Il tuo numero progressivo è: " + currentNumber); 
                }
            } catch (IOException e) {
                // Questo si verifica tipicamente quando il client si disconnette
                System.err.println("Client " + currentNumber + " disconnesso (Errore: " + e.getMessage() + ")");
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println("Errore chiusura: " + e.getMessage());
                }
            }
        }
    }
}
