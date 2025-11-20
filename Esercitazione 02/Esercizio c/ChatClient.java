import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private static final String HOST = "localhost";
    private static final int PORT = 8080;
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Inserisci il tuo nome: ");
        String userName = scanner.nextLine();

        try (Socket socket = new Socket(HOST, PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(
                 new InputStreamReader(socket.getInputStream()))) {
            
            System.out.println("\n*** Connesso al server Chat! Digita 'quit' per uscire. ***\n");
            
            // 1. Invia il nome utente al server
            out.println(userName);

            // 2. Crea un thread per ricevere messaggi dal server
            Thread listenerThread = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        System.out.println(serverMessage); // Stampa i messaggi ricevuti
                    }
                } catch (IOException e) {
                    // Potrebbe essere normale se il socket viene chiuso dal thread principale
                    // System.err.println("Connessione interrotta in ascolto.");
                }
            });
            listenerThread.start();

            // 3. Il thread principale gestisce l'invio dei messaggi dell'utente
            String userInput;
            while (true) {
                userInput = scanner.nextLine();
                if ("quit".equalsIgnoreCase(userInput)) {
                    // Chiude il socket, il che farà terminare anche il listenerThread
                    socket.close(); 
                    break;
                }
                out.println(userInput); // Invia il messaggio al server
            }
            
        } catch (IOException e) {
            System.err.println("Errore di connessione o I/O: " + e.getMessage());
        } finally {
            scanner.close();
            System.out.println("Client disconnesso.");
        }
    }
}
