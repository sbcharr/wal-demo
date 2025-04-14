package com.github.sbcharr;

import java.io.IOException;
import java.util.Scanner;

/**
 * Entry point for the WAL-backed KeyValueStore demo.
 *
 * Supported commands:
 * - put <key> <value>: Adds or updates a key-value pair
 * - get <key>: Retrieves the value for the given key
 * - print: Displays all in-memory key-value pairs
 * - exit: Exits the program
 */
public class App {
    public static void main(String[] args) throws IOException {
        // Create or reuse the WAL file; append mode ensures existing logs are retained
        WriteAheadLog wal = new WriteAheadLog("wal.log");

        // Initialize the store; this will also recover from WAL if it exists
        KeyValueStore store = new KeyValueStore(wal);

        // Simple CLI for interacting with the key-value store
        Scanner scanner = new Scanner(System.in);
        System.out.println("Simple Write-Ahead Log Based Key-Value Store");
        System.out.println("Type 'put {key} {value}', 'get {key}', 'print', or 'exit'");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                break; // Graceful shutdown
            }

            if (input.equalsIgnoreCase("print")) {
                store.printStore();
                continue;
            }

            String[] tokens = input.split(" ");

            if (tokens.length == 2 && tokens[0].equals("get")) {
                // Retrieve value from store
                System.out.println("Value: " + store.get(tokens[1]));
            } else if (tokens.length == 3 && tokens[0].equals("put")) {
                // Write value to WAL and store
                store.put(tokens[1], tokens[2]);
                System.out.println("OK");
            } else {
                // Help message for incorrect input
                System.out.println("Commands: put {key} {value} | get {key} | print | exit");
            }
        }

        // Close WAL and Scanner before exiting
        wal.close();
        scanner.close();
    }
}

