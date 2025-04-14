package com.github.sbcharr;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WriteAheadLogTest {
    private static final String LOG_FILE = "wal-test.log";
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
    void testLogAndReadBack() throws IOException {
        wal.log("PUT:hello:world");
        wal.log("PUT:foo:bar");

        List<String> entries = wal.readAll();
        assertEquals(2, entries.size());
        assertEquals("PUT:hello:world", entries.get(0));
        assertEquals("PUT:foo:bar", entries.get(1));
    }

    @Test
    void testLogFilePersistsData() throws IOException {
        wal.log("PUT:key:value");
        wal.close();

        WriteAheadLog newWal = new WriteAheadLog(LOG_FILE);
        List<String> entries = newWal.readAll();
        assertEquals(1, entries.size());
        assertEquals("PUT:key:value", entries.get(0));
        newWal.close();
    }
}
