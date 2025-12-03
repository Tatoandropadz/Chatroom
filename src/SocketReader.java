import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketReader extends Thread{

    private final DataInputStream input;

    public SocketReader(Socket socket) throws IOException {
        this.input = new DataInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        while(true) {
            try {
                String message = input.readUTF();
                System.out.println(message);
            } catch (IOException e) {
                System.out.println("IOExpection: " + e.getMessage());
            }
        }
    }
}
