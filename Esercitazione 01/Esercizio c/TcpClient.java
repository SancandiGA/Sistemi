import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class TcpClient {
    public static void main(String[] args) throws Exception {

        String indirizzoServer = "127.0.0.1"; // localhost
        int portaServer = 8765;
        String messaggioClient = "";
        String messaggioServer = "";

        try {
            // Crea la connessione al socket del server
            System.out.print("Client: Connessione al server=" + indirizzoServer + ":" + portaServer + " ... ");
            Socket socket = new Socket(indirizzoServer, portaServer);
            System.out.println("Connesso");

            // Crea stream di input e output per leggere/scrivere dati
            BufferedReader inUtente = new BufferedReader(new InputStreamReader(System.in));
            DataInputStream inSocket = new DataInputStream(socket.getInputStream());
            DataOutputStream outSocket = new DataOutputStream(socket.getOutputStream());

            while (true) { // Ciclo infinito, si interrompe solo con la condizione di uscita
                // Chiedi all'utente di inserire del testo o 'quit'
                System.out.print("Client: inserisci il messaggio da inviare> ");
                messaggioClient = inUtente.readLine();

                // Invia il testo inserito al server
                System.out.println("Client: invio il messaggio: " + messaggioClient);
                outSocket.writeUTF(messaggioClient);
                outSocket.flush();

                // Leggi i dati dallo stream di input del socket
                messaggioServer = inSocket.readUTF();
                System.out.println("Client: ricevuto il messaggio: " + messaggioServer);

                // Parsing della risposta del server (assumendo che il server invii "Vocali: X, Consonanti: Y")
                String[] parti = messaggioServer.split(",");
                int vocali = Integer.parseInt(parti[0].split(":")[1].trim());
                int consonanti = Integer.parseInt(parti[1].split(":")[1].trim());

                // Condizione di uscita
                if (consonanti == vocali / 2) {
                    System.out.println("Client: Condizione di uscita soddisfatta. Terminazione.");
                    break; // Esce dal ciclo
                }
            }

            // Chiudi le risorse
            outSocket.close();
            inSocket.close();
            inUtente.close();
            socket.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
