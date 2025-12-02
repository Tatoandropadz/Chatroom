import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class SocketWriter extends Thread{

    private final DataOutputStream output;
    private final Scanner scanner;

    public SocketWriter(Socket socket, Scanner scanner) throws IOException{
        this.output = new DataOutputStream(socket.getOutputStream());
        this.scanner = scanner;
    }

    @Override
    public void run() {
        while(true) {
            try {
                String serverMessage = scanner.nextLine();
                output.writeUTF(serverMessage);
                output.flush();
            } catch (IOException e) {
                System.out.println("IOExpection: " + e.getMessage());
            }
        }
    }
}
