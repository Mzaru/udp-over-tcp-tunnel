import java.io.PrintWriter;
import java.util.Scanner;

public class AgentDisconnect implements Runnable {
    private PrintWriter out;

    public AgentDisconnect(PrintWriter out) {
        this.out = out;
    }

    @Override
    public void run() {
        Scanner scan = new Scanner(System.in);
        while (true) {
            String in = scan.nextLine();
            if (in.equals("disconnect")) {
               synchronized (out) {
                   out.println("disconnect");
                   break;
               }
            }
        }
    }
}
