package com.github.sbcharr;

import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class KeyValueStoreTest {

    private static final String LOG_FILE = "kvstore-test.log";
    private WriteAheadLog wal;

    @BeforeEach
    void setUp() throws IOException {
        new File(LOG_FILE).delete();
        wal = new WriteAheadLog(LOG_FILE);
    }

    @AfterEach
    void tearDown() throws IOException {
        wal.close();
        new File(LOG_FILE).delete();
    }

    @Test
    void testPutAndGet() throws IOException {
        KeyValueStore store = new KeyValueStore(wal);
        store.put("x", "42");
        store.put("y", "hello");

        assertEquals("42", store.get("x"));
        assertEquals("hello", store.get("y"));
    }

    @Test
    void testRecoveryFromLog() throws IOException {
        {
            // Initial store writes to WAL
            KeyValueStore store = new KeyValueStore(wal);
            store.put("a", "alpha");
            store.put("b", "beta");
        }

        // Simulate crash by reloading from WAL
        WriteAheadLog newWal = new WriteAheadLog(LOG_FILE);
        KeyValueStore recovered = new KeyValueStore(newWal);

        assertEquals("alpha", recovered.get("a"));
        assertEquals("beta", recovered.get("b"));
        newWal.close();
    }

    @Test
    void testPartialCorruptLogEntryIgnored() throws IOException {
        wal.log("PUT:valid:key");
        wal.log("CORRUPTED ENTRY"); // Simulate a bad log line

        KeyValueStore store = new KeyValueStore(wal);
        assertEquals("key", store.get("valid")); // Only valid one recovered
    }
}

