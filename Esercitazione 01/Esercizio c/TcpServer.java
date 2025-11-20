import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpServer {

    public static void main(String[] args) throws IOException {
        int porta = 8765; // Porta del server
        ServerSocket socketServer = new ServerSocket(porta);
        System.out.println("Server: in ascolto sulla porta " + porta);

        while (true) {
            Socket socketClient = socketServer.accept(); // Attende una connessione
            System.out.println("Server: connessione accettata da " + socketClient.getInetAddress().getHostAddress());

            // Crea stream di input e output per comunicare con il client
            DataInputStream in = new DataInputStream(socketClient.getInputStream());
            DataOutputStream out = new DataOutputStream(socketClient.getOutputStream());

            try {
                while (true) {
                    String messaggioClient = in.readUTF();
                    System.out.println("Server: ricevuto: " + messaggioClient);

                    // Conta vocali e consonanti
                    int[] conteggi = contaVocaliEConsonanti(messaggioClient);
                    int vocali = conteggi[0];
                    int consonanti = conteggi[1];

                    // Invia la risposta al client
                    String risposta = "Vocali: " + vocali + ", Consonanti: " + consonanti;
                    out.writeUTF(risposta);
                    out.flush();
                    System.out.println("Server: inviato: " + risposta);

                    // Condizione di uscita
                    if (consonanti == vocali / 2) {
                        System.out.println("Server: Condizione di uscita soddisfatta.");
                        break; // Esce dal ciclo interno
                    }
                }
            } catch (IOException e) {
                System.out.println("Server: Errore durante la comunicazione con il client: " + e.getMessage());
            } finally {
                // Chiudi le risorse per questo client
                in.close();
                out.close();
                socketClient.close();
	       socketServer.close();
                System.out.println("Server: Connessione chiusa con il client.");
            }
        }
    }

    // Funzione per contare vocali e consonanti
    public static int[] contaVocaliEConsonanti(String testo) {
        int vocali = 0;
        int consonanti = 0;
        testo = testo.toLowerCase(); // Converti in minuscolo per semplificare

        for (char carattere : testo.toCharArray()) {
            if (Character.isLetter(carattere)) {
                if ("aeiou".indexOf(carattere) != -1) {
                    vocali++;
                } else {
                    consonanti++;
                }
            }
        }
        return new int[]{vocali, consonanti};
    }
}
