package com.github.sbcharr;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple Write-Ahead Log (WAL) implementation in Java.
 *
 * This class demonstrates how logs are appended to disk before applying changes to memory,
 * providing durability guarantees similar to databases.
 */
public class WriteAheadLog {
    private final File logFile;
    private final BufferedWriter writer;

    /**
     * Initializes the Write-Ahead Log with a given file name.
     * Creates or appends to the file if it already exists.
     *
     * @param fileName The name of the log file to use.
     * @throws IOException If the file cannot be created or opened.
     */
    public WriteAheadLog(String fileName) throws IOException {
        this.logFile = new File(fileName);
        // Open the file in append mode to preserve previous entries
        this.writer = new BufferedWriter(new FileWriter(logFile, true));
    }

    /**
     * Writes a single operation to the log file.
     * Each operation is flushed immediately to ensure durability.
     *
     * @param operation The string representing the operation (e.g., PUT, GET).
     * @throws IOException If writing to the file fails.
     */
    public void log(String operation) throws IOException {
        writer.write(operation);
        writer.newLine();    // Add newline to separate log entries
        writer.flush();      // Critical: force write to disk for durability
    }

    /**
     * Reads all logged operations from the file.
     * Useful during recovery after a crash.
     *
     * @return A list of all operations recorded in the WAL.
     * @throws IOException If reading the file fails.
     */
    public List<String> readAll() throws IOException {
        List<String> entries = new ArrayList<>();

        if (!logFile.exists()) {
            return entries; // No entries if file doesn't exist
        }

        // BufferedReader ensures efficient line-by-line reading
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                entries.add(line); // Collect each line into the list
            }
        }

        return entries;
    }

    /**
     * Closes the writer stream.
     * Should be called when logging is done to release system resources.
     *
     * @throws IOException If closing the writer fails.
     */
    public void close() throws IOException  {
        writer.close();
    }
}

