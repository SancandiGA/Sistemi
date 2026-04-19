import java.net.*;
import java.util.Scanner;

public class ClientMaiuscolo {
    public static void main(String[] args) throws Exception {
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName("localhost");
        Scanner inFromUser = new Scanner(System.in);
        
        String messaggio = inFromUser.nextLine();
        byte[] sendData = messaggio.getBytes();
        
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
        clientSocket.send(sendPacket);
        
        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        
        String risposta = new String(receivePacket.getData(), 0, receivePacket.getLength());
        System.out.println(risposta);
        
        clientSocket.close();
    }
}
