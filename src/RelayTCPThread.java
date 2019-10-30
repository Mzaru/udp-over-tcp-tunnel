import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class RelayTCPThread implements Runnable {
    private volatile boolean disconnect = false;
    private Socket socket;
    private InetAddress addr;
    private Map<Integer, RelayUDPThread> threads = new HashMap<>();
    private PrintWriter out = null;

    public RelayTCPThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        BufferedReader in = null;

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String line;
            //(line = in.readLine()) != null(line = in.readLine()) != null
            while (!disconnect) {
                line = in.readLine();
                if (line.split("&", 2)[0].equals("config")) {
                    System.out.println(line.split("&", 2)[1]);
                    addr = InetAddress.getByName(line.split("&", 2)[1]);
                    System.out.println("Relay was configured for " + addr.getHostAddress());
                } else if (line.split("&", 3)[0].equals("send")) {
                    System.out.println(line);
                    int port = Integer.parseInt(line.split("&", 3)[1]);
                    byte[] message = line.split("&", 3)[2].getBytes();
                    DatagramPacket packet = new DatagramPacket(message, message.length, addr, port);
                    System.out.println("Sending to " + addr.getHostAddress() + " " + port);
                    RelayUDPThread thread = threads.get(port);
                    if (thread == null) {
                        thread = new RelayUDPThread(this, port);
                        threads.put(port, thread);
                        new Thread(thread).start();
                    }
                    thread.send(packet);
                } else if (line.equals("disconnect")) {
                    disconnect();
                }
            }

        } catch (IOException e) {
            System.out.println(e);
        }
        System.out.println("disconnected tcp");
    }

    public InetAddress getAddr() {
        return addr;
    }

    public PrintWriter getOut() {
        return out;
    }

    public void disconnect() {
        out.println("disconnect");
        threads.values().stream().forEach(RelayUDPThread::disconnect);
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        disconnect = true;
    }
}
