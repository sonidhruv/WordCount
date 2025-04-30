# Text Distribution and Word Frequency Analysis System

This Java-based system consists of two components:
- **TextServer**: A server that reads a local text file and sends its content to clients line by line.
- **TextClient**: A client that connects to two servers concurrently, reads the text data, counts word frequencies, and prints the top N most common words.

## Project Structure

```
.
├── TextServer.java
└── TextClient.java
```

## Deployment Instructions

1. **Compile the Java Files**
   ```bash
   javac TextServer.java TextClient.java
   ```

2. **Start Two Server Instances**
   In separate terminal windows, start two servers on different ports with different (or same) text files:
   ```bash
   java TextServer path/to/textfile1.txt 5000
   java TextServer path/to/textfile2.txt 6000
   ```

3. **Run the Client**
   Once both servers are running, execute the client from a new terminal:
   ```bash
   java TextClient localhost 5000 localhost 6000
   ```

## Running the Code

1. Ensure both server instances are running and serving valid text files.
2. The client connects to both servers concurrently and begins receiving text data.
3. It processes incoming lines, counts the frequency of each word, and prints the **top 5 most common words** after both connections complete.

## Assumptions

- The input text files contain readable English text.
- Each server sends data to **one client** at a time (sequential handling of connections).
- The application is executed in a trusted environment (no input validation for file content or malicious clients).

## Static Data

- The number of top words to display is **hardcoded to 5** in the `TextClient` (`printTopWords(5)`).
- The client assumes **exactly two servers** are provided via command-line arguments.

## Dependency Management

- The project **only uses core Java libraries**; no external dependencies are required.
- No unnecessary libraries or frameworks are included.

## Logic Explanation

### `TextServer.java`
- Reads the specified text file line by line.
- Listens on a given port for incoming client connections.
- Sends each line of the file to the connected client via socket output stream.

### `TextClient.java`
- Takes two server addresses and ports as input.
- Creates two threads to connect to both servers simultaneously.
- Reads incoming lines from both servers.
- Splits each line into lowercase words, using non-alphabetic characters as delimiters.
- Uses a `ConcurrentHashMap` to count word frequencies in a thread-safe manner.
- After processing both streams, prints the top 5 most frequent words in descending order.

