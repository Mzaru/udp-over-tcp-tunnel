import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class RelayUDPThread implements Runnable {
    private volatile boolean disconnect = false;
    private DatagramSocket socket;
    private RelayTCPThread thread;
    private int port;

    public RelayUDPThread(RelayTCPThread thread, int port) {
        this.thread = thread;
        this.port = port;
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(5000);
        } catch (SocketException ignored) {
        }
    }

    @Override
    public void run() {
        while (!disconnect) {
            byte[] buffer = new byte[256];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(packet);
                System.out.println("received");

                if (!packet.getAddress().equals(thread.getAddr()) || packet.getPort() != port) {
                    continue;
                }
                String data = new String(packet.getData(), 0, packet.getLength());
                int port = packet.getPort();
                thread.getOut().println(String.format("%d&%s", port, data));
            } catch (SocketTimeoutException ignored) {
            } catch (IOException e) {
                System.out.println(e);
            }
        }
        System.out.println("disconnected udp");
    }

    public void send(DatagramPacket packet) throws IOException {
        socket.send(packet);
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public void disconnect() {
        disconnect = true;
    }
}
