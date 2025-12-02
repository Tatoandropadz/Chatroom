import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;


public class Server {
    public static void main(String args[]) throws IOException, InterruptedException {

        ServerSocket server = new ServerSocket(8080);
        System.out.println("Server started");

        Socket socket = server.accept();

        Scanner scanner = new Scanner(System.in);
        Thread reader = new SocketReader(socket);
        Thread writer = new SocketWriter(socket, scanner);

        reader.start();
        writer.start();

        reader.join();
        writer.join();
    }
}
