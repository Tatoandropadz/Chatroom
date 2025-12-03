import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class SocketWriter extends Thread{

    private final DataOutputStream output;
    private final Scanner scanner;
    private String username;

    public SocketWriter(Socket socket, Scanner scanner, String username) throws IOException{
        this.output = new DataOutputStream(socket.getOutputStream());
        this.scanner = scanner;
        this.username = username;
    }


    // This method detects if a user has sent a command to change username and verifies it to be valid
    Boolean changeUser(String message) {
        return false;
    }


    @Override
    public void run() {
        while(true) {
            try {
                String message = scanner.nextLine();
                output.writeUTF(username+": "+message);
                output.flush();
            } catch (IOException e) {
                System.out.println("IOExpection: " + e.getMessage());
            }
        }
    }
}
