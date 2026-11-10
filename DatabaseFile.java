package mydb;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class DatabaseFile {

    private final File file;
    private RandomAccessFile database;

    public DatabaseFile(String filePath) {
        this.file = new File(filePath);
    }

    // Open database file
    public void open() throws IOException {

        File parent = file.getParentFile();

        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        database = new RandomAccessFile(file, "rw");

        System.out.println("Database file opened:");
        System.out.println(file.getAbsolutePath());
    }

    // Close database file
    public void close() throws IOException {

        if (database != null) {
            database.close();
            database = null;

            System.out.println("Database file closed.");
        }
    }

    // Write bytes at a specific position
    public void write(long position, byte[] data) throws IOException {

        checkOpen();

        database.seek(position);
        database.write(data);
    }

    // Read bytes from a specific position
    public byte[] read(long position, int length) throws IOException {

        checkOpen();

        byte[] data = new byte[length];

        database.seek(position);

        int bytesRead = database.read(data);

        if (bytesRead == -1) {
            return new byte[0];
        }

        if (bytesRead < length) {
            byte[] actualData = new byte[bytesRead];

            System.arraycopy(
                    data,
                    0,
                    actualData,
                    0,
                    bytesRead
            );

            return actualData;
        }

        return data;
    }

    // Get current file size
    public long size() throws IOException {

        checkOpen();

        return database.length();
    }

    private void checkOpen() {

        if (database == null) {
            throw new IllegalStateException(
                    "Database is not open."
            );
        }
    }
}