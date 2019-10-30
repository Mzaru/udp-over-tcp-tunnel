import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Relay {

    public static void main(String[] args) {
        ServerSocket echoServer = null;
        Socket clientSocket = null;
        try {
            echoServer = new ServerSocket(10000);
        } catch (IOException e) {
            System.out.println(e);
        }

        while(true) {
            try {
                clientSocket = echoServer.accept();
                new Thread(new RelayTCPThread(clientSocket)).start();
            } catch (IOException e) {
                System.out.println(e);
            }
        }

    }
}
