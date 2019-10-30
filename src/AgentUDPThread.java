import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;

public class AgentUDPThread implements Runnable {
    private DatagramSocket socket;
    private InetAddress addr = null;
    private int port;
    private final PrintWriter out;
    private volatile boolean disconnect;

    public AgentUDPThread(DatagramSocket socket, PrintWriter out) {
        this.socket = socket;
        this.out = out;
        try {
            socket.setSoTimeout(5000);
        } catch (SocketException ignored) {
        }
        System.out.println(socket.getLocalPort());
    }

    @Override
    public void run() {
        while (!disconnect) {
            byte[] buffer = new byte[256];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(packet);
                System.out.println("received");
                String data = new String(packet.getData(), 0, packet.getLength());
                if (addr == null) {
                    addr = packet.getAddress();
                    port = packet.getPort();
                }
                if (!packet.getAddress().equals(addr) || packet.getPort() != port) {//
                    continue;
                }
                data = String.format("send&%d&%s", socket.getLocalPort(), data);
                System.out.println(data);
                synchronized (out) {
                    out.println(data);
                }

            } catch (SocketTimeoutException ignored) {
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    public int getPort() {
        return port;
    }

    public void send(DatagramPacket packet) throws IOException {
        socket.send(packet);
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public InetAddress getAddr() {
        return addr;
    }

    public void disconnect() {
        disconnect = true;
    }
}
