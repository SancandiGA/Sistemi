import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class TcpClient {
    private static final String HOST = "localhost";
    private static final int PORT = 8080;

    public static void main(String[] args) {
        // 

        try (Socket socket = new Socket(HOST, PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Connesso al server " + HOST + ":" + PORT);
            
            // Riceve il messaggio di benvenuto/login dal server
            String serverMessage = in.readLine(); 
            System.out.println("Server: " + serverMessage);
            
            // --- Fase di Autenticazione ---
            boolean autenticato = false;
            while (!autenticato) {
                System.out.print("LOGIN <utente> <password>: ");
                String loginInput = scanner.nextLine();
                
                if (loginInput.equalsIgnoreCase("quit")) break;
                
                out.println(loginInput);
                
                // Legge la risposta del server
                String rispostaAutenticazione = in.readLine();
                System.out.println("Server: " + rispostaAutenticazione);
                
                if (rispostaAutenticazione.toUpperCase().startsWith("SUCCESSO")) {
                    // Se l'autenticazione è riuscita, riceve anche la riga successiva
                    String comandiDisponibili = in.readLine();
                    System.out.println("Server: " + comandiDisponibili);
                    autenticato = true;
                } else if (rispostaAutenticazione.toUpperCase().startsWith("ERRORE")) {
                    System.out.println("Riprova l'autenticazione.");
                }
            }
            
            if (!autenticato) {
                System.out.println("Autenticazione annullata. Disconnessione...");
                out.println("QUIT"); // Invia QUIT al server prima di uscire
                return;
            }

            // --- Fase di Gestione Prenotazioni ---
            String userInput;
            while (true) {
                System.out.print("\nComando (MAPPA, PRENOTA <posto>, CANCELLA <posto>, o QUIT): ");
                userInput = scanner.nextLine();
                
                if ("quit".equalsIgnoreCase(userInput)) {
                    out.println("QUIT");
                    break;
                }
                
                out.println(userInput); // Invia il comando al server
                
                // Riceve la risposta del server. 
                // La MAPPA è multilinea, gestiamo la lettura finché non arriva l'indicatore di fine
                String response;
                while ((response = in.readLine()) != null) {
                    System.out.println(response);
                    // L'indicatore di fine risposta (una riga che contiene SUCCESSO/ERRORE o la Legenda per la MAPPA)
                    if (response.toUpperCase().startsWith("SUCCESSO") || 
                        response.toUpperCase().startsWith("ERRORE") ||
                        response.contains("Legenda")) {
                        break; 
                    }
                }
            }
            
        } catch (IOException e) {
            System.err.println("Errore client: " + e.getMessage());
        } 
        
        System.out.println("Client disconnesso.");
    }
}
