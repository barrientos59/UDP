package Tasca1;// Tasca1.DatagramSocketClient.java
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class DatagramSocketClient {
    InetAddress serverIP;
    int serverPort;
    DatagramSocket socket;

    public void init(String host, int port) throws SocketException, UnknownHostException {
        serverIP = InetAddress.getByName(host);
        serverPort = port;
        socket = new DatagramSocket();
    }

    public void runClient() throws IOException {
        byte[] receivedData = new byte[1024];
        byte[] sendingData;

        // A l'inici, el client ha d'entrar el seu nom
        System.out.print("Entra el teu nom: ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String clientName = reader.readLine();

        // El primer missatge és el nom del client
        sendingData = clientName.getBytes();

        // El servidor atén el port indefinidament
        while (mustContinue(sendingData)) {
            DatagramPacket packet = new DatagramPacket(sendingData, sendingData.length, serverIP, serverPort);
            // Enviament de la resposta
            socket.send(packet);

            // Creació del paquet per rebre les dades
            packet = new DatagramPacket(receivedData, 1024);
            // Espera de les dades
            socket.receive(packet);

            // Processament de les dades rebudes i obtenció de la resposta
            String serverResponse = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Servidor: " + serverResponse);

            sendingData = getDataToRequest(packet.getData(), packet.getLength());
        }
    }

    private byte[] getDataToRequest(byte[] data, int length) {
        // Procés diferent per cada aplicació
        // En aquest cas, es llegeix la següent entrada de l'usuari
        System.out.print("Entra el teu missatge (adeu per desconectar): ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            return reader.readLine().getBytes();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    private boolean mustContinue(byte[] sendingData) {
        // Procés diferent per cada aplicació
        // En aquest cas, es comprova si el missatge no és "adeu"
        String msg = new String(sendingData).trim();
        return !msg.equalsIgnoreCase("adeu");
    }
    public static void main(String[] args) {

        // Lanzar el cliente
        DatagramSocketClient client = new DatagramSocketClient();
        try {
            client.init("localhost", 9876); // Puedes cambiar el host y puerto según sea necesario
            client.runClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
