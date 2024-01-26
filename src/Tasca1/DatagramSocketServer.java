package Tasca1;// Tasca1.DatagramSocketServer.java
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class DatagramSocketServer {
    DatagramSocket socket;

    public void init(int port) throws SocketException {
        socket = new DatagramSocket(port);
    }

    public void runServer() throws IOException {
        byte[] receivingData = new byte[1024];
        byte[] sendingData;
        InetAddress clientIP;
        int clientPort;

        // El servidor atén el port indefinidament
        while (true) {
            // Creació del paquet per rebre les dades
            DatagramPacket packet = new DatagramPacket(receivingData, 1024);
            // Espera de les dades
            socket.receive(packet);

            // Obtenció del missatge del client en majúscules
            String clientMessage = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Client: " + clientMessage);

            // Si el client envia "adeu", es desconnecta
            if (clientMessage.equalsIgnoreCase("adeu")) {
                System.out.println("Client disconnected.");
                continue; // Continua esperant nous clients
            }

            // Procésament de les dades rebudes i obtenció de la resposta
            sendingData = processData(packet.getData(), packet.getLength());

            // Obtenció de l'adreça del client
            clientIP = packet.getAddress();
            // Obtenció del port del client
            clientPort = packet.getPort();
            // Creació del paquet per enviar la resposta
            packet = new DatagramPacket(sendingData, sendingData.length, clientIP, clientPort);
            // Enviament de la resposta
            socket.send(packet);
        }
    }

    private byte[] processData(byte[] data, int length) {
        // Procés diferent per cada aplicació
        // En aquest cas, es converteix el missatge a majúscules
        String msg = new String(data, 0, length);
        String response = msg.toUpperCase();
        return response.getBytes();
    }
    public static void main(String[] args) {
        // Lanzar el servidor en un hilo separado
        new Thread(() -> {
            DatagramSocketServer server = new DatagramSocketServer();
            try {
                server.init(9876); // Puedes cambiar el puerto según sea necesario
                server.runServer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
