package mydb;

public class Main {

    public static void main(String[] args) {

        Database db = new Database(
                "data/mydatabase.db"
        );

        StorageManager storage =
                new StorageManager(db);
                 // =====================================
// STEP 13 - PAGE ALLOCATION TEST
// =====================================

int pageA = storage.allocatePage();
int pageB = storage.allocatePage();
int pageC = storage.allocatePage();

System.out.println();
System.out.println(
        "===== PAGE ALLOCATION TEST ====="
);

System.out.println(
        "Allocated Page: " + pageA
);

System.out.println(
        "Allocated Page: " + pageB
);

System.out.println(
        "Allocated Page: " + pageC
);
        try {

            // =====================================
            // STEP 1 - DATABASE
            // =====================================

            db.open();

            Page page = new Page();

            page.writeString(
                    "Hello from my own database!"
            );

            db.writePage(0, page);

            Page loadedPage =
                    db.readPage(0);

            System.out.println();
            System.out.println(
                    "Data read from database:"
            );

            System.out.println(
                    loadedPage.readString()
            );


            // =====================================
            // STEP 2 - PAGE TEST
            // =====================================

            Page page21 = new Page();

            page21.writeString(
                    "Hello from Page 21!"
            );

            int page21Id = 21;

            db.writePage(
                    page21Id,
                    page21
            );

            Page loadedPage21 =
                    db.readPage(page21Id);

            System.out.println();
            System.out.println(
                    "Data read from Page "
                            + page21Id + ":"
            );

            System.out.println(
                    loadedPage21.readString()
            );


            // =====================================
            // STEP 3 - RECORD TEST
            // =====================================

            System.out.println();
            System.out.println(
                    "===== RECORD TEST ====="
            );

            Record record =
                    new Record(
                            10,
                            "Akanksha"
                    );

            System.out.println(
                    "Key: " + record.getKey()
            );

            System.out.println(
                    "Value: " + record.getValue()
            );


            // =====================================
            // STEP 4 - SAVE RECORD USING STORAGE
            // =====================================

            Record diskRecord =
                    new Record(
                            101,
                            "Database Record"
                    );

            int recordPageId = 30;

            storage.saveRecord(
                    recordPageId,
                    diskRecord
            );


            // =====================================
            // STEP 5 - READ RECORD FROM DATABASE
            // =====================================

            Record loadedDiskRecord =
                    storage.readRecord(
                            recordPageId
                    );

            System.out.println();

            System.out.println(
                    "===== READ RECORD FROM DATABASE ====="
            );

            System.out.println(
                    "Key: "
                            + loadedDiskRecord.getKey()
            );

            System.out.println(
                    "Value: "
                            + loadedDiskRecord.getValue()
            );


            // =====================================
            // STEP 6 - B+ TREE TEST
            // =====================================

            System.out.println();
            System.out.println(
                    "===== B+ TREE TEST ====="
            );

            BPlusTree tree =
                    new BPlusTree(storage);


            // =====================================
            // INSERT RECORDS
            // =====================================

            tree.insert(
                    10,
                    "Akanksha"
            );

            tree.insert(
                    20,
                    "Rahul"
            );

            tree.insert(
                    30,
                    "Priya"
            );

            tree.insert(
                    40,
                    "Aman"
            );

            tree.insert(
                    50,
                    "Neha"
            );

            tree.insert(
                    60,
                    "Riya"
            );

            tree.insert(
                    70,
                    "Karan"
            );

            tree.insert(
                    80,
                    "Anjali"
            );

            tree.insert(
                    90,
                    "Rohit"
            );

           tree.insert(
                    100,
                    "pooja"
            );


                    System.out.println();
System.out.println("===== UPDATE TEST =====");

tree.update(50, "Akanksha");

System.out.println(
        "Updated record: "
                + tree.search(50)
);
            


            // =====================================
            // READ B+ TREE RECORD FROM DISK
            // =====================================

            Record diskRecord50 =
        tree.readRecordFromDisk(50);
            System.out.println();

            System.out.println(
                    "===== B+ TREE RECORD FROM DISK ====="
            );

            System.out.println(
                    "Key: "
                            + diskRecord50.getKey()
            );

            System.out.println(
                    "Value: "
                            + diskRecord50.getValue()
            );


            // =====================================
            // SEARCH
            // =====================================

            System.out.println();

            tree.search(30);
            tree.search(70);
            tree.search(100);
            tree.search(999);


            // =====================================
            // PRINT TREE
            // =====================================

            tree.printTree();


            // =====================================
            // PRINT DATA
            // =====================================

            tree.printAll();


            // =====================================
            // SIZE
            // =====================================

            System.out.println();

            System.out.println(
                    "Total records: "
                            + tree.size()
            );


            // =====================================
            // DELETE
            // =====================================

            tree.delete(30);

            tree.delete(70);


            System.out.println();

            tree.printAll();

            System.out.println();

            System.out.println(
                    "Records after deletion: "
                            + tree.size()
            );


            // =====================================
            // DATABASE CHECK
            // =====================================

            System.out.println();

            System.out.println(
                    "===== DATABASE CHECK ====="
            );

            Record databaseRecord10 =
                    storage.readRecord(10);

            System.out.println(
                    "Page 10 Record: "
                            + databaseRecord10.getKey()
                            + " -> "
                            + databaseRecord10.getValue()
            );


            // =====================================
            // FINAL DATABASE TEST
            // =====================================

           Record databaseRecord100 =
        tree.readRecordFromDisk(100);
            System.out.println();

            System.out.println(
                    "===== FINAL DATABASE TEST ====="
            );

            System.out.println(
                    "Key: "
                            + databaseRecord100.getKey()
            );

            System.out.println(
                    "Value: "
                            + databaseRecord100.getValue()
            );


            // =====================================
            // CLOSE DATABASE
            // =====================================

            db.close();

        } catch (Exception e) {

            System.out.println(
                    "Database error:"
            );

            e.printStackTrace();

        } finally {

            System.out.println();

            System.out.println(
                    "Program finished."
            );
        }
    }
}