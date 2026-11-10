package mydb;

import java.io.IOException;

public class Database {

    private final String databasePath;

    private DatabaseFile databaseFile;

    public Database(String databasePath) {
        this.databasePath = databasePath;
    }

    // Open database
    public void open() throws IOException {

        if (databaseFile != null) {
            System.out.println("Database is already open.");
            return;
        }

        databaseFile = new DatabaseFile(
                databasePath
        );

        databaseFile.open();

        System.out.println("Database opened successfully.");
    }

    // Close database
    public void close() throws IOException {

        if (databaseFile == null) {
            System.out.println("Database is already closed.");
            return;
        }

        databaseFile.close();

        databaseFile = null;

        System.out.println("Database closed successfully.");
    }

    // Write one page
    public void writePage(
            int pageNumber,
            Page page
    ) throws IOException {

        checkOpen();

        long position =
                (long) pageNumber * Page.PAGE_SIZE;

        databaseFile.write(
                position,
                page.getBytes()
        );

        System.out.println(
                "Page " + pageNumber + " written."
        );
    }

    // Read one page
    public Page readPage(
            int pageNumber
    ) throws IOException {

        checkOpen();

        long position =
                (long) pageNumber * Page.PAGE_SIZE;

        byte[] data = databaseFile.read(
                position,
                Page.PAGE_SIZE
        );

        return new Page(data);
    }

    // Get database size
    public long getSize() throws IOException {

        checkOpen();

        return databaseFile.size();
    }

    private void checkOpen() {

        if (databaseFile == null) {
            throw new IllegalStateException(
                    "Database is not open."
            );
        }
    }
}