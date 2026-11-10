package mydb;

import java.io.IOException;

public class StorageManager {

    private final Database database;

    // Next page that can be allocated
    private int nextPageId = 0;

    public StorageManager(Database database) {
    this.database = database;

    try {
        nextPageId = getAllocatedPageCount();
    } catch (Exception e) {
        nextPageId = 0;
    }
}

    // =====================================================
    // ALLOCATE PAGE
    // =====================================================

              public int allocatePage() throws IOException {

    nextPageId = getAllocatedPageCount();

    return nextPageId++;
}

    // =====================================================
    // SAVE RECORD - AUTOMATIC PAGE
    // =====================================================

    public int saveRecord(Record record) throws IOException {

        int pageId = allocatePage();

        saveRecord(pageId, record);

        return pageId;
    }

    // =====================================================
    // SAVE RECORD - SPECIFIC PAGE
    // =====================================================

    public void saveRecord(
            int pageId,
            Record record
    ) throws IOException {

        byte[] recordBytes = record.toBytes();

        Page page = new Page();

        page.writeBytes(recordBytes);

        database.writePage(
                pageId,
                page
        );

        System.out.println(
                "Record saved: "
                        + record.getKey()
                        + " -> "
                        + record.getValue()
                        + " at Page "
                        + pageId
        );
    }

    // =====================================================
    // READ RECORD
    // =====================================================

    public Record readRecord(
            int pageId
    ) throws IOException {

        Page page = database.readPage(pageId);

        byte[] data = page.getBytes();

        return Record.fromBytes(data);
    }

    // =====================================================
    // DELETE RECORD
    // =====================================================

    public void deleteRecord(
            int pageId
    ) throws IOException {

        Page emptyPage = new Page();

        database.writePage(
                pageId,
                emptyPage
        );

        System.out.println(
                "Record deleted from Page "
                        + pageId
        );
    }

    // =====================================================
    // GET ALLOCATED PAGE COUNT
    // =====================================================

    public int getAllocatedPageCount()
            throws IOException {

        long size = database.getSize();

        return (int) (
                (size + Page.PAGE_SIZE - 1)
                        / Page.PAGE_SIZE
        );
    }

    // =====================================================
    // LOAD EXISTING RECORDS FROM DISK
    // =====================================================

    public void loadRecordsIntoTree(
            BPlusTree tree
    ) throws IOException {

        int pageCount =
                getAllocatedPageCount();

        for (
                int pageId = 0;
                pageId < pageCount;
                pageId++
        ) {

            try {

                Record record =
                        readRecord(pageId);

                if (record != null) {

                    tree.loadRecord(
                            record.getKey(),
                            record.getValue(),
                            pageId
                    );
                }

            } catch (Exception e) {

                // Page may contain
                // normal text or empty data.
                // Ignore and continue.
            }
        }

        // IMPORTANT:
        // Existing pages are already used.
        // Next new page must be after them.
        nextPageId = pageCount;
    }

    // =====================================================
    // GET NEXT PAGE ID
    // =====================================================

    public int getNextPageId() {

        return nextPageId;
    }
}