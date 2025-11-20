import java.io.*;
import java.net.*;

public class CalcolatriceServer {
    private static final int PORTA = 8844;
    private static int operazioniEseguite = 0; // Contatore operazioni

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORTA)) {
            System.out.println("Server avviato sulla porta " + PORTA);

            while (true) {
                Socket client = serverSocket.accept(); // Accetta la connessione del client
                System.out.println("Nuova connessione da: " + client.getInetAddress().getHostAddress());

                // Gestione della comunicazione con il client in un thread separato
                new Thread(() -> {
                    try (
                            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                            PrintWriter out = new PrintWriter(client.getOutputStream(), true)
                    ) {
                        String richiesta;
                        while ((richiesta = in.readLine()) != null) {
                            System.out.println("Ricevuta richiesta: " + richiesta);

                            if (richiesta.equalsIgnoreCase("QUIT")) {
                                break; // Uscita dal ciclo e chiusura della connessione
                            }

                            String risultato = elaboraRichiesta(richiesta);
                            out.println(risultato); // Invia il risultato al client
                        }
                    } catch (IOException e) {
                        System.err.println("Errore durante la comunicazione con il client: " + e.getMessage());
                    } finally {
                        try {
                            client.close(); // Chiudi la connessione del client
                            System.out.println("Connessione chiusa con: " + client.getInetAddress().getHostAddress());
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
            operazioniEseguite++; // Incrementa il contatore
            return "Risultato: " + risultato;
        } catch (NumberFormatException e) {
            return "ERRORE: Numeri non validi. Assicurati di inserire numeri validi.";
        } catch (Exception e) {
            return "ERRORE: Si è verificato un errore durante l'elaborazione della richiesta.";
        }
    }
}
