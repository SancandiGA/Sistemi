import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpServer {
    private static final int PORT = 8080;
    // Istanza unica della risorsa condivisa (la mappa dei posti)
    private static final Aereo aereo = new Aereo(); 

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server TCP avviato sulla porta " + PORT);

            // 

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connesso: " + clientSocket.getRemoteSocketAddress());

                // Passa l'oggetto Aereo a ClientHandler
                Runnable clientHandler = new ClientHandler(clientSocket, aereo);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.err.println("Errore server: " + e.getMessage());
        }
    }

    // Classe interna che gestisce la comunicazione con un singolo client
    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final Aereo aereo;
        private boolean autenticato = false;
        private String nomeUtente = null;

        public ClientHandler(Socket socket, Aereo aereo) {
            this.clientSocket = socket;
            this.aereo = aereo;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(
                         clientSocket.getOutputStream(), true)) {

                out.println("Benvenuto! Devi autenticarti. Formato: LOGIN <utente> <password>");

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    
                    if (inputLine.equalsIgnoreCase("QUIT")) break;
                    
                    if (!autenticato) {
                        gestisciAutenticazione(inputLine, out);
                    } else {
                        gestisciComando(inputLine, out);
                    }
                }
            } catch (IOException e) {
                System.err.println("Client disconnesso forzatamente: " + e.getMessage());
            } finally {
                System.out.println("Client disconnesso: " + clientSocket.getRemoteSocketAddress());
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println("Errore chiusura: " + e.getMessage());
                }
            }
        }
        
        // Logica per l'autenticazione
        private void gestisciAutenticazione(String comando, PrintWriter out) {
            String[] parti = comando.split(" ");
            
            if (parti.length == 3 && parti[0].equalsIgnoreCase("LOGIN")) {
                String utente = parti[1];
                String password = parti[2];
                
                if (aereo.autentica(utente, password)) {
                    this.nomeUtente = utente;
                    this.autenticato = true;
                    out.println("SUCCESSO: Autenticazione riuscita per " + utente + ".");
                    out.println("Ora puoi usare i comandi: MAPPA, PRENOTA <posto>, CANCELLA <posto> o QUIT.");
                } else {
                    out.println("ERRORE: Nome utente o password errati.");
                }
            } else {
                out.println("ERRORE: Formato di login non corretto. Usa: LOGIN <utente> <password>");
            }
        }
        
        // Logica per i comandi (visualizzazione/prenotazione)
        private void gestisciComando(String comando, PrintWriter out) {
            String risposta;
            
            if (comando.equalsIgnoreCase("MAPPA")) {
                // Visualizza la matrice dei posti
                risposta = "\nMAPPA POSTI\n" + aereo.getMappaPosti();
                out.println(risposta);
                
            } else if (comando.toUpperCase().startsWith("PRENOTA ")) {
                // Prenota un posto (es. PRENOTA 3A)
                String[] parti = comando.split(" ");
                if (parti.length == 2) {
                    risposta = tentaPrenotazione(parti[1], nomeUtente);
                } else {
                    risposta = "ERRORE: Formato non corretto. Usa: PRENOTA <posto> (es. PRENOTA 3A)";
                }
                out.println(risposta);
                
            } else if (comando.toUpperCase().startsWith("CANCELLA ")) {
                // Cancella una prenotazione (es. CANCELLA 3A)
                String[] parti = comando.split(" ");
                if (parti.length == 2) {
                    risposta = tentaCancellazione(parti[1], nomeUtente);
                } else {
                    risposta = "ERRORE: Formato non corretto. Usa: CANCELLA <posto> (es. CANCELLA 3A)";
                }
                out.println(risposta);
                
            } else {
                out.println("ERRORE: Comando non riconosciuto. Comandi disponibili: MAPPA, PRENOTA <posto>, CANCELLA <posto>, QUIT.");
            }
        }
        
        // Parsing del comando di prenotazione/cancellazione
        private String tentaPrenotazione(String posto, String utente) {
            // Il posto è una stringa come "3A"
            try {
                int riga = Integer.parseInt(posto.substring(0, posto.length() - 1));
                char colonna = posto.toUpperCase().charAt(posto.length() - 1);
                
                return aereo.prenotaPosto(riga, colonna, utente);
                
            } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                return "ERRORE: Formato posto non valido. Usa: <riga><colonna> (es. 1A, 5D).";
            }
        }
        
        private String tentaCancellazione(String posto, String utente) {
            try {
                int riga = Integer.parseInt(posto.substring(0, posto.length() - 1));
                char colonna = posto.toUpperCase().charAt(posto.length() - 1);
                
                return aereo.cancellaPrenotazione(riga, colonna, utente);
                
            } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                return "ERRORE: Formato posto non valido. Usa: <riga><colonna> (es. 1A, 5D).";
            }
        }
    }
}
