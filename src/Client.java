import java.io.*;
import java.net.Socket;
import java.util.Scanner;

// The actual client that is being run.

public class Client {
    public static void main(String[] args) throws InterruptedException {

        Scanner scanner = new Scanner(System.in);
        System.out.print("Input address (for local input 'localhost'): ");
        String address = scanner.next();
        System.out.print("Input port (for local input '8080'): ");
        int port = scanner.nextInt();
        SocketWriter writer = null;

        while (true) {

            try {
                Socket socket = new Socket(address, port);
                System.out.println("[SERVER] Connected to server!");


                Thread reader = new SocketReader(socket);
                writer = new SocketWriter(socket, scanner);

                reader.start();
                writer.start();

                reader.join();
                writer.join();
                if (writer.getExitIntentional() == true) {
                    break;
                }
            } catch (IOException e) {
            }
        }
    }
}
