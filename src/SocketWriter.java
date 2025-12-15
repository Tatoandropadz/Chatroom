import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

// Basically writes the messages

public class SocketWriter extends Thread{

    private final DataOutputStream output;
    private final Scanner scanner;
    public volatile boolean exitIntentional = false;


    Boolean getExitIntentional() {
        return exitIntentional;
    }

    public SocketWriter(Socket socket, Scanner scanner) throws IOException{
        this.output = new DataOutputStream(socket.getOutputStream());
        this.scanner = scanner;
    }



    @Override
    public void run() {
        while(true) {
            try {
                String message = scanner.nextLine();
                if (message.startsWith("/exit")) {
                    // if user wants to exit
                    exitIntentional = true;
                }
                output.writeUTF(message);
                output.flush();
            } catch (IOException e) {
                try {
                    throw new IOException();
                } catch (IOException ex) {
                    throw new RuntimeException("The user has exited the program!");
                }
            }
        }
    }
}
