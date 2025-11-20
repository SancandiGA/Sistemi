import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class ChatServer {
    private static final int PORT = 8080;
    // Set per memorizzare tutti i PrintWriter dei client connessi
    private static Set<PrintWriter> clientWriters = new HashSet<>();

    public static void main(String[] args) {
        System.out.println("Server Chat avviato sulla porta " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nuovo client connesso: " + clientSocket.getRemoteSocketAddress());
                
                // Avvia un thread separato per gestire il client
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Errore server principale: " + e.getMessage());
        }
    }

    // Metodo per trasmettere un messaggio a tutti i client connessi
    public static void broadcast(String message) {
        // Itera sulla copia dei writer per evitare eccezioni di modifica concorrente
        for (PrintWriter writer : new HashSet<>(clientWriters)) {
            try {
                writer.println(message); // Invia il messaggio
            } catch (Exception e) {
                System.err.println("Errore nell'invio a un client.");
                // Se c'è un errore, è probabile che il client si sia disconnesso,
                // quindi lo rimuoviamo dalla lista.
                clientWriters.remove(writer);
            }
        }
    }

    // Classe per gestire la connessione di un singolo client
    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter out;
        private String clientName; // Per identificare il client

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()))) {
                
                // 1. Ottieni il canale di output e aggiungilo al set globale
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                clientWriters.add(out);
                
                // 2. Leggi il nome del client (il client dovrebbe inviarlo per primo)
                clientName = in.readLine();
                if (clientName == null) {
                    clientName = "Utente Sconosciuto";
                }
                
                // 3. Notifica a tutti l'ingresso del nuovo utente
                broadcast(">>> " + clientName + " si è unito alla chat.");
                System.out.println(clientName + " si è unito.");

                // 4. Ciclo principale di ascolto
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Ricevuto da " + clientName + ": " + inputLine);
                    // Trasmetti il messaggio a tutti gli altri
                    broadcast(clientName + ": " + inputLine);
                }
                
            } catch (IOException e) {
                // Potrebbe succedere quando un client si disconnette improvvisamente
                System.err.println("Connessione con " + clientName + " interrotta: " + e.getMessage());
            } finally {
                // 5. Gestione della disconnessione
                if (out != null) {
                    clientWriters.remove(out);
                }
                if (clientName != null) {
                    broadcast(">>> " + clientName + " ha lasciato la chat.");
                    System.out.println(clientName + " si è disconnesso.");
                }
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println("Errore chiusura socket: " + e.getMessage());
                }
            }
        }
    }
}
