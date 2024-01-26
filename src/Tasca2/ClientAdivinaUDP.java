package Tasca2;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Scanner;

public class ClientAdivinaUDP {
    /** Client/Jugador que ha d'encertar un numero del Tasca2.ServidorAdivinaUDP.java -> Comunicació UDP
     **  Si el servidor no respòn en 5 segons representa que aquest ha acabat la comunicació perquè un
     **  altre client ha guanyat. Per tant s'ha perdut la partida.
     **/

    private int portDesti;
    private int result;
    private String Nom, ipSrv;
    private int intents;
    private InetAddress adrecaDesti;

    public ClientAdivinaUDP(String ip, int port) {
        this.portDesti = port;
        result = -1;
        intents = 0;
        ipSrv = ip;

        try {
            adrecaDesti = InetAddress.getByName(ipSrv);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void setNom(String n) {
        Nom=n;
    }

    public int getIntents () {
        return intents;
    }

    public void runClient() throws IOException {
        byte [] receivedData = new byte[4];
        int n;

        //Missatge de benvinguda
        System.out.println("Hola " + Nom + "! Comencem!\n Digues un número: ");

        //Bucle de joc
        while(result!=0 & result!=-2) {

            Scanner sc = new Scanner(System.in);
            n = sc.nextInt();
            byte[] missatge = ByteBuffer.allocate(4).putInt(n).array();

            //creació del paquet a enviar
            DatagramPacket packet = new DatagramPacket(missatge,missatge.length,adrecaDesti,portDesti);
            //creació d'un sòcol temporal amb el qual realitzar l'enviament
            DatagramSocket socket = new DatagramSocket();
            //Enviament del missatge
            socket.send(packet);

            //creació del paquet per rebre les dades
            packet = new DatagramPacket(receivedData, 4);
            //espera de les dades
            socket.setSoTimeout(5000);
            try {
                socket.receive(packet);
                //processament de les dades rebudes i obtenció de la resposta
                result = getDataToRequest(packet.getData(), packet.getLength());
            }catch(SocketTimeoutException e) {
                System.out.println("El servidor no respòn: " + e.getMessage());
                result=-2;
            }
        }


    }

    private int getDataToRequest(byte[] data, int length) {
        int nombre = ByteBuffer.wrap(data).getInt();

        if(nombre==0) System.out.println("Correcte");
        else if (nombre==1) System.out.println("Més petit");
        else System.out.println("Més gran");
        intents++;

        return nombre;
    }



    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public static void main(String[] args) {
        String jugador, ipSrv, port;

        //Demanem la ip del servidor i nom del jugador
        System.out.println("IP del servidor?");
        Scanner sip = new Scanner(System.in);
        ipSrv = sip.next();
        System.out.println("port del servidor?");
        port = sip.next();
        System.out.println("Nom jugador:");
        jugador = sip.next();

        ClientAdivinaUDP cAdivina = new ClientAdivinaUDP(ipSrv, Integer.valueOf(port));

        cAdivina.setNom(jugador);
        try {
            cAdivina.runClient();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(cAdivina.getResult() == 0) {
            System.out.println("Fi, ho has aconseguit amb "+ cAdivina.getIntents() +" intents");
        } else {
            System.out.println("Has perdut");
        }

    }

}