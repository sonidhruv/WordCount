import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * TextServer is a simple server application that reads a text file
 * and sends its content line by line to connecting clients.
 */
public class TextServer {

    /**
     * The main entry point for the server application.
     * It parses command-line arguments, sets up the server socket,
     * and starts listening for client connections.
     *
     * @param args Command-line arguments: <file> <port>
     */
    public static void main(String[] args) {
        // Check if the correct number of command-line arguments is provided.
        if (args.length != 2) {
            // Print usage instructions to standard error.
            System.err.println("Usage: java TextServer <file> <port>");
            // Exit the application with an error code.
            System.exit(1);
        }

        // Extract the file path and port number from the arguments.
        String filePath = args[0];
        int port = Integer.parseInt(args[1]); // Convert port argument to integer.

        // Use a try-with-resources block to ensure the ServerSocket is closed.
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            // Inform the user that the server has started and on which port.
            System.out.println("Server started on port " + port);
            // Start the main server loop to handle incoming connections.
            runServer(serverSocket, filePath);
        } catch (IOException e) {
            // Catch and print any IO errors that occur during server startup.
            System.err.println("Could not start server on port " + port + ": " + e.getMessage());
        }
    }

    /**
     * Runs the main server loop, continuously accepting client connections.
     * The server will run until the thread is interrupted (though not explicitly handled in this simple example).
     *
     * @param serverSocket The ServerSocket to accept connections from.
     * @param filePath     The path to the text file to be served.
     */
    private static void runServer(ServerSocket serverSocket, String filePath) {
        // Loop indefinitely to accept connections until the thread is interrupted.
        while (!Thread.currentThread().isInterrupted()) {
            // Handle a single client connection within this loop iteration.
            handleClientConnection(serverSocket, filePath);
        }
    }

    /**
     * Handles a single client connection: accepts the connection, reads the file,
     * and sends the file content line by line to the client.
     * Resources (Socket, PrintWriter, BufferedReader) are closed automatically by try-with-resources.
     *
     * @param serverSocket The ServerSocket to accept the connection from.
     * @param filePath     The path to the text file to send to the client.
     */
    private static void handleClientConnection(ServerSocket serverSocket, String filePath) {
        // Use try-with-resources for Socket, PrintWriter, and BufferedReader to ensure they are closed.
        try (Socket clientSocket = serverSocket.accept(); // Accept an incoming client connection.
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true); // Get the output stream to send data to the client, auto-flushing.
             BufferedReader fileReader = new BufferedReader(new FileReader(filePath))) { // Get a reader for the text file.

            // Inform the user about the connected client's address.
            System.out.println("Client connected from " + clientSocket.getInetAddress());

            String line;
            // Read the file line by line.
            while ((line = fileReader.readLine()) != null) {
                // Send each line to the connected client.
                out.println(line);
            }

            // Connection is closed automatically when exiting the try block.
        } catch (IOException e) {
            // Catch and print any IO errors that occur during client handling (e.g., connection reset).
            System.err.println("Error handling client connection: " + e.getMessage());
        }
    }
}