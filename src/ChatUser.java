import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ChatUser {
    private String username;
    private Thread reader;
    private Thread writer;

    public ChatUser(String username, Socket socket, Scanner scanner) throws IOException {
        this.username = username;
        this.reader = new SocketReader(socket);
        this.writer = new SocketWriter(socket, scanner, username);
    }


    // Implement these 4 methods for thread activation

    void startReader() {
        reader.start();
    }

    void startWriter() {
        writer.start();
    }

    void joinReader() throws InterruptedException {
        reader.join();
    }

    void joinWriter() throws InterruptedException {
        writer.join();
    }

    // Getter and Setter for username

    void changeUsername(String newUsername) {
        this.username = newUsername;
    }

    String getUsername() {
        return this.username;
    }
}
