import java.net.*;

public class ClientDaytime {
    public static void main(String[] args) throws Exception {
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName("localhost");
        
        byte[] sendData = new byte[0];
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 1313);
        clientSocket.send(sendPacket);
        
        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        
        String risposta = new String(receivePacket.getData(), 0, receivePacket.getLength());
        System.out.println(risposta);
        
        clientSocket.close();
    }
}
