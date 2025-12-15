import java.io.*;
import java.net.Socket;


/*
    This file is used to handle all communication for a single client connection on the Server side.
    It has 4 main methods

    getUsername(), sendMessage() and sendWelcomeMessage() are all self-explanatory
    The main run() method has a while loop that "listens" for inputs from the client.


    The checkMessage() method checks whether the message inputted by the client is a command or a normal message
 */

public class ChatUserHandler implements Runnable {
    private final Socket clientSocket;
    private final DataInputStream input; // Used to receive messages
    private final DataOutputStream output; // Used to send out messages
    private String username; // Unique username for this client
    public volatile Boolean running = true;

    // Reference to the main Server class
    private final Server server;

    public ChatUserHandler(Socket socket, Server server) throws IOException {
        this.clientSocket = socket;
        this.server = server;
        this.input = new DataInputStream(socket.getInputStream());
        this.output = new DataOutputStream(socket.getOutputStream());
        // Initial username before the client sets it
        this.username = "Anonymous"+(server.getHandlers().size()+1);
    }

    // A simple getter for the username
    public String getUsername() {
        return username;
    }

    // Sends a message only to this specific client
    public void sendMessage(String message) {
        try {
            output.writeUTF(message);
            output.flush();
        } catch (IOException e) {
            // In case of an error
            System.err.println("Error sending message to " + username + ": " + e.getMessage());
        }
    }

    public void sendWelcomeMessage() {
        sendMessage("[SERVER] Welcome! Your initial name is: " + username);
    }

    // Check for commands
    /*
        The main method I use for checking commands is splitting the message into words and analyzing word count/start of the String
        to check.
     */
    public void checkMessage (String rawMessage) throws IOException {
        if (rawMessage.equals("")) {
            //do nothing
        }
        else {

            // If the message starts with '/' then it should be a command
            if (rawMessage.charAt(0) == '/') {
                if (rawMessage.startsWith("/changeUser")) {
                    String[] parts = rawMessage.split(" ");
                    String newName = parts[1].trim();
                    if (parts.length == 2) {
                        String oldName = this.username;
                        this.username = newName;
                        server.broadcast(oldName + " has changed their name to " + this.username, this);
                        sendMessage("Username successfully changed to: " + this.username);
                    } else {
                        sendMessage("Usage: /changeUser <new_username>");
                    }
                }
                else if (rawMessage.startsWith("/whisper")) {
                    String[] parts = rawMessage.split(" ", 3);
                    if (parts.length == 3) {
                        String targetUser = parts[1];
                        String whisperMsg = parts[2];
                        server.whisper(this.username, targetUser, whisperMsg);
                    }
                    else {
                        sendMessage("Usage: /whisper <target_username> <message>");
                    }
                }
                else if (rawMessage.startsWith("/exit")) {
                    System.out.println(username + " disconnected.");
                    System.out.println("[SERVER] There are currently " +server.getHandlers().size()+ " users in the chat!");
                    running = false;
                    server.removeUser(this);
                    clientSocket.close();
                }
                else {
                    sendMessage("The command you inputted does not exist!");
                }
            }
            // If it doesn't start with a '/' then it is a normal message and gets processed normally
            else {
                // Normal message: broadcast to all others
                server.broadcast(this.username + ": " + rawMessage, this);
            }
        }
    }

    // Main thread loop: continuously listens to messages from the client
    @Override
    public void run() {
        try {
            while (running) {
                String rawMessage = input.readUTF();
                System.out.println("Received from " + username + ": " + rawMessage);


                checkMessage(rawMessage);
            }
        } catch (IOException e) {
            System.out.println(username + " disconnected.");
            System.out.println("[SERVER] There are currently " +server.getHandlers().size()+ " users in the chat!");
            server.removeUser(this);
            try {
                clientSocket.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

    }
}