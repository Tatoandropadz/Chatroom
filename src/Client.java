import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String args[]) throws IOException, InterruptedException {

        Socket socket = new Socket("localhost", 8080);
        System.out.println("Connected to server!");

        Scanner scanner = new Scanner(System.in);
        Thread reader = new SocketReader(socket);
        Thread writer = new SocketWriter(socket, scanner);

        reader.start();
        writer.start();

        reader.join();
        writer.join();
    }
}
