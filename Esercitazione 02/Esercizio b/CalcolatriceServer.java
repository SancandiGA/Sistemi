import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CalcolatriceServer {
    private static final int PORTA = 8844;
    // Uso AtomicInteger per un contatore thread-safe
    private static final AtomicInteger operazioniEseguite = new AtomicInteger(0);
    private static String nomeServer;

    public static void main(String[] args) {
        try {
            // Ottieni il nome host del server
            nomeServer = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            nomeServer = "Server Sconosciuto";
        }

        try (ServerSocket serverSocket = new ServerSocket(PORTA)) {
            System.out.println("Server '" + nomeServer + "' avviato sulla porta " + PORTA);
            System.out.println("In attesa di connessioni...");

            while (true) {
                // Il server accetta la connessione di un client
                Socket clientSocket = serverSocket.accept();
                
                // Variabili per il log
                String clientAddress = clientSocket.getInetAddress().getHostAddress();
                String clientHost = clientSocket.getInetAddress().getHostName();

                System.out.println("Nuova connessione sul server '" + nomeServer + 
                               "' da client: " + clientHost + " (" + clientAddress + ")");

                // Avvio di un nuovo Thread per gestire la comunicazione con il client
                new Thread(() -> {
                    try (
                        // Apri i canali di comunicazione
                        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
                    ) {
                        String richiesta;
                        while ((richiesta = in.readLine()) != null) {
                            System.out.println("Ricevuta richiesta su '" + nomeServer + "' da " + clientAddress + ": " + richiesta);

                            if (richiesta.equalsIgnoreCase("QUIT")) {
                                break; // Uscita dal ciclo e chiusura della connessione
                            }

                            String risultato = elaboraRichiesta(richiesta);
                            // Invia la risposta al client includendo il nome del server
                            out.println("Risultato (da " + nomeServer + "): " + risultato); 
                        }
                    } catch (IOException e) {
                        System.err.println("Errore durante la comunicazione con " + clientAddress + ": " + e.getMessage());
                    } finally {
                        try {
                            clientSocket.close(); // Chiudi la connessione del client
                            System.out.println("Connessione chiusa con client: " + clientAddress);
                        } catch (IOException e) {
                            System.err.println("Errore durante la chiusura della connessione: " + e.getMessage());
                        }
                    }
                }).start(); // Avvia il thread
            }
        } catch (IOException e) {
            System.err.println("Errore del server: " + e.getMessage());
        }
    }

    /**
     * Logica per elaborare e calcolare la richiesta.
     */
    private static String elaboraRichiesta(String richiesta) {
        try {
            String[] parti = richiesta.split(" ");
            if (parti.length != 3) {
                return "ERRORE: Formato non valido. Usa il formato: numero operazione numero (es. 10 + 5)";
            }

            double num1 = Double.parseDouble(parti[0]);
            String operazione = parti[1];
            double num2 = Double.parseDouble(parti[2]);

            double risultato = 0;
            switch (operazione) {
                case "+":
                    risultato = num1 + num2;
                    break;
                case "-":
                    risultato = num1 - num2;
                    break;
                case "*":
                    risultato = num1 * num2;
                    break;
                case "/":
                    if (num2 == 0) {
                        return "ERRORE: Divisione per zero!";
                    }
                    risultato = num1 / num2;
                    break;
                default:
                    return "ERRORE: Operazione non supportata. Usa +, -, *, /";
            }
            
            // Incrementa in modo sicuro il contatore globale
            operazioniEseguite.incrementAndGet(); 
            return String.valueOf(risultato); // Restituisci il risultato come stringa
        } catch (NumberFormatException e) {
            return "ERRORE: Numeri non validi. Assicurati di inserire numeri validi.";
        } catch (Exception e) {
            return "ERRORE: Si è verificato un errore durante l'elaborazione della richiesta.";
        }
    }
}
