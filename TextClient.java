import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TextClient is a simple client application that connects to multiple
 * TextServer instances, receives text data, counts word frequencies,
 * and prints the top N most common words.
 */
public class TextClient {

    // A thread-safe map to store word counts across multiple threads.
    private static final ConcurrentHashMap<String, Integer> wordCounts = new ConcurrentHashMap<>();

    /**
     * The main entry point for the client application.
     * It parses command-line arguments, creates threads to connect to servers,
     * waits for threads to complete, and prints the top word counts.
     *
     * @param args Command-line arguments: <host1> <port1> <host2> <port2>
     */
    public static void main(String[] args) {
        // Check if the correct number of command-line arguments is provided.
        if (args.length != 4) {
            // Print usage instructions to standard error.
            System.err.println("Usage: java TextClient <host1> <port1> <host2> <port2>");
            // Exit the application with an error code.
            System.exit(1);
        }

        // Extract host and port information for two servers from the arguments.
        String host1 = args[0];
        int port1 = Integer.parseInt(args[1]); // Convert port arguments to integers.
        String host2 = args[2];
        int port2 = Integer.parseInt(args[3]);

        // Create separate threads to connect to and process data from each server concurrently.
        Thread server1Thread = new Thread(() -> processServer(host1, port1));
        Thread server2Thread = new Thread(() -> processServer(host2, port2));

        // Start both threads.
        server1Thread.start();
        server2Thread.start();

        try {
            // Wait for both server processing threads to complete.
            server1Thread.join();
            server2Thread.join();
        } catch (InterruptedException e) {
            // Catch and print any interruption errors while waiting for threads.
            System.err.println("Thread interrupted: " + e.getMessage());
        }

        // Once both threads finish, print the top 5 most common words.
        printTopWords(5);
    }

    /**
     * Connects to a given server, reads text data line by line,
     * and processes each line to count word frequencies.
     *
     * @param host The hostname or IP address of the server.
     * @param port The port number of the server.
     */
    private static void processServer(String host, int port) {
        // Use a try-with-resources block for the Socket and BufferedReader to ensure they are closed.
        try (Socket socket = new Socket(host, port); // Establish a connection to the server.
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) { // Get an input stream reader to read data from the server.

            String line;
            // Read data line by line from the server.
            while ((line = in.readLine()) != null) {
                // Process each received line to extract and count words.
                processLine(line);
            }
        } catch (IOException e) {
            // Catch and print any IO errors that occur during server communication.
            System.err.println("Error processing server " + host + ":" + port + ": " + e.getMessage());
        }
    }

    /**
     * Processes a single line of text: converts it to lowercase, splits it into words,
     * and updates the word counts in the {@code wordCounts} map.
     *
     * @param line The line of text to process.
     */
    private static void processLine(String line) {
        // Convert the line to lowercase to ensure case-insensitive counting.
        // Split the line into words using non-alphabetic characters as delimiters.
        String[] words = line.toLowerCase().split("[^a-zA-Z]+");

        // Iterate through the array of words.
        for (String word : words) {
            // Check if the word is not empty (resulting from multiple delimiters).
            if (!word.isEmpty()) {
                // Atomically update the count for the word in the ConcurrentHashMap.
                // If the word is not present, add it with a count of 1; otherwise, increment its count.
                wordCounts.compute(word, (k, v) -> (v == null) ? 1 : v + 1);
            }
        }
    }

    /**
     * Prints the top N most common words based on the calculated word counts.
     * The words are sorted in descending order of their counts.
     *
     * @param limit The maximum number of top words to print.
     */
    private static void printTopWords(int limit) {
        // Get a stream of the word count entries.
        List<Map.Entry<String, Integer>> topEntries = wordCounts.entrySet().stream()
                // Sort the entries in descending order based on their value (the word count).
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                // Limit the stream to the top N entries.
                .limit(limit)
                // Collect the limited stream into a List.
                .toList();

        // Print a header for the top words.
        System.out.println("Top " + limit + " most common words:");
        // Iterate through the list of top entries and print each word and its count.
        for (Map.Entry<String, Integer> entry : topEntries) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}