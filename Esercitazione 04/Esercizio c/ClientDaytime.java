import java.net.*;

public class ClientDaytime {
    public static void main(String[] args) {
        String serverHostname = "127.0.0.1";
        int port = 1313;

        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress address = InetAddress.getByName(serverHostname);

            byte[] sendBuf = new byte[1]; 
            DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, address, port);
            socket.send(sendPacket);
            System.out.println("Richiesta inviata al server...");

            byte[] receiveBuf = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuf, receiveBuf.length);

            socket.receive(receivePacket);
            String risposta = new String(receivePacket.getData(), 0, receivePacket.getLength());

            System.out.println("Risposta dal server: " + risposta);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
