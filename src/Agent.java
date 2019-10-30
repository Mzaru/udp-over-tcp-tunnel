import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Agent {

    private static Socket socket;
    private static List<AgentUDPThread> threads = new ArrayList<>();

    public static void main(String[] args) {
        InetAddress relay = null;
        String receiver = null;
        List<Integer> ports = new ArrayList<>();
        PrintWriter out = null;
        BufferedReader in = null;
        try {
            relay = InetAddress.getByName(args[0]);
            receiver = args[1];
            Arrays.stream(args[2].split(",")).forEach(s -> ports.add(Integer.parseInt(s)));
        } catch (UnknownHostException e) {
            System.out.println(e);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Wrong input");
        }

        try {
            socket = new Socket(relay, 10000);
            System.out.println("Connected to relay");
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.println("config&" + receiver);
        System.out.println("Configured the relay");
        try {
            for (int port : ports) {
                AgentUDPThread thread = new AgentUDPThread(new DatagramSocket(port), out);
                threads.add(thread);
                new Thread(thread).start();
            }
            System.out.println("Created threads for UDP Ports");
        } catch (SocketException e) {
            System.out.println(e);
        }
        String line;
        new Thread(new AgentDisconnect(out)).start();
        while (true) {
            try {
                line = in.readLine();
                if (line.equals("disconnect")) {
                    disconnect();
                    break;
                }
                System.out.println("received");
                int port = Integer.parseInt(line.split("&", 2)[0]);
                byte[] data = line.split("&", 2)[1].getBytes();
                AgentUDPThread thread = threads.stream().filter(UDPThread -> UDPThread.getSocket().getLocalPort() == port).findFirst().get();
                DatagramPacket packet = new DatagramPacket(data, data.length, thread.getAddr(), thread.getPort());
                thread.send(packet);
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    public static void disconnect() {
        threads.stream().forEach(AgentUDPThread::disconnect);
    }
}
