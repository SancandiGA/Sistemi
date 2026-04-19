import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ServerDaytime {
    private static final String LOG_FILE = "connessioni.txt";
    private static Map<String, Integer> logConnessioni = new HashMap<>();

    public static void main(String[] args) throws Exception {
        caricaDati();
        DatagramSocket serverSocket = new DatagramSocket(1313);
        byte[] receiveData = new byte[1024];

        while(true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            
            String clientIP = receivePacket.getAddress().getHostAddress();
            int conteggio = logConnessioni.getOrDefault(clientIP, 0) + 1;
            logConnessioni.put(clientIP, conteggio);
            salvaDati();

            String risposta;
            if (conteggio < 10) {
                risposta = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } else {
                risposta = "Hai esaurito i bonus gratuiti! Il servizio è ora a pagamento.";
            }

            byte[] sendData = risposta.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort());
            serverSocket.send(sendPacket);
        }
    }

    private static void caricaDati() {
        try (BufferedReader reader = new BufferedReader(new FileReader(LOG_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parti = line.split(":");
                logConnessioni.put(parti[0], Integer.parseInt(parti[1]));
            }
        } catch (IOException e) {}
    }

    private static void salvaDati() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE))) {
            for (Map.Entry<String, Integer> entry : logConnessioni.entrySet()) {
                writer.println(entry.getKey() + ":" + entry.getValue());
            }
        } catch (IOException e) {}
    }
}
