import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

/*
    The Server manages all interactions between the clients. The way it works is everytime a client "joins",
    The Server stores it in an array (with each client having a distinct username). When a client sends a message
    it gets sent to the server to be processed (to see whether it is a regular message or a command). In case of a
    Regular message, the same message gets sent to every other client in the chat.
 */


/*
    There are 5 different methods:
    1) startServer() - this method continuously accepts new clients and makes sure to connect with it
    2) main() - the main method, which obviously runs the server
    3) removeUser() - removes the user (self-explanatory but still)
    4) broadcast() - sends the message inputted from one user to the rest (however there is also an option to send the message to everyone,
    including the original sender)
    5) whisper() - sends the message inputted from one user to a specific second user
 */

public class Server {
    // Thread-safe list to hold all active client handlers
    private final CopyOnWriteArrayList<ChatUserHandler> handlers = new CopyOnWriteArrayList<>();

    public void startServer(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Chat Server started on port " + port);

            while (true) {
                // 1) A new connection from a new client
                Socket clientSocket = serverSocket.accept();

                // 2) Create a new handler for the client
                ChatUserHandler newUser = new ChatUserHandler(clientSocket, this);

                // 3) Broadcast a new arrival
                broadcast("[SERVER] "+newUser.getUsername() + " has joined the chat.", null);

                // 4) Add the handler to the master list
                handlers.add(newUser);

                // 5) Start the handler's thread to begin listening to the client
                new Thread(newUser).start();

                // 6) Simply sends a welcome message to the new user's terminal
                newUser.sendWelcomeMessage();

                // 7) Inform about the number of users using the chat
                broadcast("[SERVER] There are currently " +handlers.size()+ " users in the chat!", null);
            }
        } catch (IOException e) {
            System.err.println("Server exception: " + e.getMessage());
        }
    }

    // Starts the server
    public static void main(String[] args) {
        Server chatServer = new Server();
        Scanner scanner = new Scanner(System.in);
        System.out.print("Input port (for local input '8080'): ");
        int port = scanner.nextInt();
        chatServer.startServer(port);
    }


    // Used to remove a handler when a client disconnects
    public void removeUser(ChatUserHandler user) {
        handlers.remove(user);
        broadcast("[SERVER] "+user.getUsername() + " has left the chat.", null);
    }

    // Sends a message to all users, optionally excluding the sender
    public void broadcast(String message, ChatUserHandler sender) {
        for (ChatUserHandler handler : handlers) {
            if (handler != sender) {
                handler.sendMessage(message);
            }
        }
    }

    // Sends a private message to a specific user
    public void whisper(String senderName, String targetName, String message) {
        boolean targetFound = false;

        for (ChatUserHandler handler : handlers) {
            if (handler.getUsername().equalsIgnoreCase(targetName)) {
                // Sends the private message in case the target is found
                handler.sendMessage("[WHISPER from " + senderName + "]: " + message);
                targetFound = true;
                break;
            }
        }

        // Informs the sender if the target wasn't found
        for (ChatUserHandler senderHandler : handlers) {
            if (senderHandler.getUsername().equalsIgnoreCase(senderName)) {
                if (!targetFound) {
                    senderHandler.sendMessage("Error: User '" + targetName + "' not found.");
                } else {
                    // Confirm to the sender that the whisper was sent
                    senderHandler.sendMessage("[WHISPER to " + targetName + "]: " + message);
                }
                break;
            }
        }
    }

    public CopyOnWriteArrayList<ChatUserHandler> getHandlers() {
        return handlers;
    }
}