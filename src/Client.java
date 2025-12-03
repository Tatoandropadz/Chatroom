import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String args[]) throws IOException, InterruptedException {

        Socket socket = new Socket("localhost", 8080);
        System.out.println("Connected to server!");

        Scanner scanner = new Scanner(System.in);
        ChatUser user1 = new ChatUser("Tato", socket, scanner);

        user1.startReader();
        user1.startWriter();

        user1.joinReader();
        user1.joinWriter();
    }
}
