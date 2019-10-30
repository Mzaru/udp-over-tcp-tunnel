import java.util.*;
import java.net.*;
import java.io.*;

public class UdpClient {
    public static void main(String[] args) throws SocketException {
        DatagramSocket socket = null;
        DatagramPacket packet;
        DatagramPacket packet1;
        DatagramPacket packet2;

        try {
            System.out.println("Próbuję utowrzyć gniazdo");
            socket = new DatagramSocket();
            System.out.println("Gniazdo utworzone");
        } catch(SocketException e) {
            System.err.println("Błąd przy tworzeniu gniazda: " + e);
            System.exit(1);
        }

        String dstring = new Date().toString();
        byte[] bufor = dstring.getBytes();

        String dstring1 = new Date().toString();
        byte[] bufor1 = dstring.getBytes();

        String dstring2 = new Date().toString();
        byte[] bufor2 = dstring.getBytes();

        InetAddress address = null;
        Integer port = null;
        try {
            if(args.length == 0) {
                port = 10001;
                address = InetAddress.getByName("localhost");
            } else {
                address = InetAddress.getByName(args[0]);
                port = Integer.parseInt(args[1]);
            }
        } catch (UnknownHostException e) {
            System.err.println("Nieznany host: " + args[0]);
        }
        packet = new DatagramPacket(bufor, bufor.length, address, port);
        packet1 = new DatagramPacket(bufor1, bufor1.length, address, port+1);
        packet2 = new DatagramPacket(bufor1, bufor1.length, address, port+2);

        try {
            System.out.println("Próbuję wysłać pakiet");
            socket.send(packet);
            System.out.println("Pakiet wysłany");
            //Thread.sleep(1000);
            socket.send(packet1);
            System.out.println("Pakiet wysłany");
            socket.send(packet2);
            System.out.println("Pakiet wysłany");
        } catch(IOException e) {
            System.err.println("Problem z odesłaniem pakietu: " + e);
            System.exit(1);
        } /*catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        bufor = new byte[256];
        packet = new DatagramPacket(bufor, bufor.length);
        try{
            System.out.println("Czekam na pakiet");
            socket.receive(packet);
        } catch(IOException e) {
            System.exit(1);
        }

        String received = new String(packet.getData(), 0, packet.getLength());
        System.out.println("Odebrałem: " + received);

        address = packet.getAddress();
        port = packet.getPort();
        System.out.println("z adresu " + address.toString() + ":" + port);

        try{
            System.out.println("Czekam na pakiet");
            socket.receive(packet);
        } catch(IOException e) {
            System.exit(1);
        }

        received = new String(packet.getData(), 0, packet.getLength());
        System.out.println("Odebrałem: " + received);

        address = packet.getAddress();
        port = packet.getPort();
        System.out.println("z adresu " + address.toString() + ":" + port);

        try{
            System.out.println("Czekam na pakiet");
            socket.receive(packet);
        } catch(IOException e) {
            System.exit(1);
        }

        received = new String(packet.getData(), 0, packet.getLength());
        System.out.println("Odebrałem: " + received);

        address = packet.getAddress();
        port = packet.getPort();
        System.out.println("z adresu " + address.toString() + ":" + port);

        socket.close();
    }
}
