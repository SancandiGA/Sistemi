import java.io.*;
import java.net.*;
import java.util.Scanner;

public class CalcolatriceClient {
    private static final String HOST = "localhost"; // Indirizzo del server
    private static final int PORTA = 8844; // Porta del server

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PORTA);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            // Menu Utente
            System.out.println("=== CALCOLATRICE REMOTA ===");
            System.out.println("Connesso a " + HOST + ":" + PORTA);
            System.out.println("Formato: NUMERO OPERAZIONE NUMERO");
            System.out.println("Operazioni: + - * /");
            System.out.println("Scrivi 'quit' per uscire");

            // Loop di Interazione
            while (true) {
                System.out.print("Calcolo > ");
                String input = scanner.nextLine();

                if (input.equalsIgnoreCase("quit")) {
                    out.println("QUIT"); // Invia il comando di uscita al server
                    break; // Esci dal loop
                }

                // Invia richiesta e ricevi risposta
                out.println(input); // Invia l'operazione al server
                String risposta = in.readLine(); // Ricevi la risposta (che include il nome del server)
                
                // Stampa la risposta completa dal server
                System.out.println(risposta); 
            }
        } catch (IOException e) {
            System.err.println("Errore nella comunicazione con il server: " + e.getMessage());
            System.err.println("Assicurati che il server sia avviato.");
        }
    }
}
