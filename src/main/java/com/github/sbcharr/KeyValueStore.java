package com.github.sbcharr;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A minimal key-value store backed by a Write-Ahead Log (WAL).
 *
 * The WAL ensures durability: every write operation is logged before updating in-memory data.
 * On restart, the store can recover its state from the WAL.
 */
public class KeyValueStore {
    // In-memory key-value data structure
    private final Map<String, String> store = new HashMap<>();

    // Write-Ahead Log for durability
    private final WriteAheadLog wal;

    /**
     * Initializes the key-value store and triggers recovery from existing WAL.
     *
     * @param wal A WriteAheadLog instance.
     * @throws IOException If recovery fails due to I/O issues.
     */
    public KeyValueStore(WriteAheadLog wal) throws IOException {
        this.wal = wal;
        recoverFromLog(); // Load previous state from WAL
    }

    /**
     * Inserts or updates a key-value pair in the store.
     * First logs the operation to WAL, then updates the in-memory map.
     *
     * @param key   The key to insert or update.
     * @param value The value associated with the key.
     */
    public void put(String key, String value) {
        try {
            // Log the operation before applying it to memory
            wal.log("PUT:" + key + ":" + value);
        } catch (IOException ex) {
            // Log failure should not crash the app; but in real systems, rollback may be required
            ex.printStackTrace();
        }
        store.put(key, value);
    }

    /**
     * Retrieves the value for a given key.
     *
     * @param key The key to look up.
     * @return The value or null if not found.
     */
    public String get(String key) {
        return store.get(key);
    }

    /**
     * Rebuilds the in-memory state by replaying the WAL log entries.
     * This simulates crash recovery.
     *
     * @throws IOException If reading the WAL fails.
     */
    public void recoverFromLog() throws IOException {
        List<String> entries = wal.readAll();

        for (String entry : entries) {
            if (entry.startsWith("PUT:")) {
                String[] walEntryParts = entry.split(":");
                if (walEntryParts.length == 3) {
                    String key = walEntryParts[1];
                    String value = walEntryParts[2];
                    store.put(key, value); // Replay operation
                }
            }
        }
    }

    /**
     * Utility method to print all key-value pairs in the current in-memory store.
     */
    public void printStore() {
        System.out.println("In-memory store ->");
        if (!store.isEmpty()) {
            for (Map.Entry<String, String> entry : store.entrySet()) {
                System.out.println(entry.getKey() + "=" + entry.getValue());
            }
        }
    }
}

