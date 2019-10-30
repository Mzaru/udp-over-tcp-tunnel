import java.util.*;
import java.net.*;
import java.io.*;

public class UdpServer {
	public static void main(String[] args) {
		// gniazdo do oczekiwania na dane
		DatagramSocket socket = null;
		DatagramSocket socket1 = null;
		DatagramSocket socket2 = null;
		// pakiet
		DatagramPacket packet = null;
		DatagramPacket packet1 = null;
		DatagramPacket packet2 = null;

		// otwórz gniazdo
		try {
			System.out.println("Próbuję utowrzyć gniazdo");
			// utwórz gniazdo
			socket = new DatagramSocket(10001);
			socket1 = new DatagramSocket(10002);
			socket2 = new DatagramSocket(10003);

			// przestaw w tryb rozgłoszeniowy
			socket.setBroadcast(true);
			socket1.setBroadcast(true);
			socket2.setBroadcast(true);

			System.out.println("Gniazdo utworzone");
		} catch (SocketException e) {
			System.err.println("Błąd przy tworzeniu gniazda: " + e);
			System.exit(1);
		}
		// utwórz pakiet dla odbierania danych
		byte[] bufor = new byte[256];
		packet = new DatagramPacket(bufor, bufor.length);

		byte[] bufor1 = new byte[256];
		packet1 = new DatagramPacket(bufor1, bufor1.length);

		byte[] bufor2 = new byte[256];
		packet2 = new DatagramPacket(bufor2, bufor2.length);
		while (true) {
			try {
				System.out.println("Czekam na pakiet");
				// odbierz pakiet
				socket.receive(packet);
				socket1.receive(packet1);
				socket2.receive(packet2);
			} catch (IOException e) {
				System.err.println("Błąd przy odbieraniu pakietu: " + e);
				System.exit(1);
			}
			// wypisz co dostałeś
			String received = new String(packet.getData(), 0, packet.getLength());
			String received1 = new String(packet1.getData(), 0, packet1.getLength());
			String received2 = new String(packet2.getData(), 0, packet2.getLength());

			System.out.println("Odebrałem: " + received);
			System.out.println("Odebrałem: " + received1);
			System.out.println("Odebrałem: " + received2);


			// pobierz adres i port z odebranego pakietu
			InetAddress address = packet.getAddress();
			int port = packet.getPort();
			System.out.println("z adresu " + address.toString() + ":" + port);

			InetAddress address1 = packet1.getAddress();
			int port1 = packet1.getPort();
			System.out.println("z adresu " + address1.toString() + ":" + port1);

			InetAddress address2 = packet2.getAddress();
			int port2 = packet2.getPort();
			System.out.println("z adresu " + address2.toString() + ":" + port2);

			int length = packet.getLength();
			int length1 = packet1.getLength();
			int length2 = packet2.getLength();

			// teraz odeślemy odpowiedź
			// utwórz nowy pakiet do odesłania
			packet = new DatagramPacket(bufor, length, address, port);
			packet1 = new DatagramPacket(bufor1, length1, address1, port1);
			packet2 = new DatagramPacket(bufor2, length2, address2, port2);

			try {
				//Thread.sleep(5000);
				System.out.println("Próbuję odesłać pakiet");
				// odeślij go do odbiorcy
				socket.send(packet);
				System.out.println("Odesłano");
				socket1.send(packet1);
				System.out.println("Odesłano");
				socket2.send(packet2);
				System.out.println("Odesłano");
			} catch (IOException e) {
				System.err.println("Problem z odesłaniem pakietu: " + e);
				System.exit(1);
			} /*catch (InterruptedException e) {
				e.printStackTrace();
			}*/
		}
			// posprzątaj
			//socket.close();
	}
}
