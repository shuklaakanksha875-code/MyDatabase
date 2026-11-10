package mydb;

import java.util.Scanner;

public class DBShell {

    public static void main(String[] args) {

        Database db =
                new Database("data/mydatabase.db");

        StorageManager storage =
                new StorageManager(db);

        try {

            // =========================================
            // OPEN DATABASE
            // =========================================

            db.open();

            // =========================================
            // CREATE B+ TREE
            // =========================================

            BPlusTree tree =
                    new BPlusTree(storage);

            // =========================================
            // LOAD EXISTING RECORDS FROM DISK
            // =========================================

            storage.loadRecordsIntoTree(tree);

            // =========================================
            // START SHELL
            // =========================================

            Scanner scanner =
                    new Scanner(System.in);

            System.out.println();
            System.out.println("================================");
            System.out.println("          MY DATABASE");
            System.out.println("================================");
            System.out.println("Commands:");
            System.out.println("INSERT key value");
            System.out.println("SEARCH key");
            System.out.println("UPDATE key value");
            System.out.println("DELETE key");
            System.out.println("SHOW");
            System.out.println("TREE");
            System.out.println("SIZE");
            System.out.println("EXIT");
            System.out.println("================================");

            // =========================================
            // COMMAND LOOP
            // =========================================

            while (true) {

                System.out.print("\nmydb> ");

                String input =
                        scanner.nextLine().trim();

                if (input.isEmpty()) {
                    continue;
                }

                String[] parts =
                        input.split("\\s+", 3);

                String command =
                        parts[0].toUpperCase();

                try {

                    // =================================
                    // INSERT
                    // =================================

                    switch (command) {

                        case "INSERT":

                            if (parts.length < 3) {

                                System.out.println(
                                        "Usage: INSERT key value"
                                );

                                break;
                            }

                            int insertKey =
                                    Integer.parseInt(parts[1]);

                            String insertValue =
                                    parts[2];

                            tree.insert(
                                    insertKey,
                                    insertValue
                            );

                            break;


                        // =================================
                        // SEARCH
                        // =================================

                        case "SEARCH":

                            if (parts.length < 2) {

                                System.out.println(
                                        "Usage: SEARCH key"
                                );

                                break;
                            }

                            int searchKey =
                                    Integer.parseInt(parts[1]);

                            tree.search(searchKey);

                            break;


                        // =================================
                        // UPDATE
                        // =================================

                        case "UPDATE":

                            if (parts.length < 3) {

                                System.out.println(
                                        "Usage: UPDATE key value"
                                );

                                break;
                            }

                            int updateKey =
                                    Integer.parseInt(parts[1]);

                            String updateValue =
                                    parts[2];

                            tree.update(
                                    updateKey,
                                    updateValue
                            );

                            break;


                        // =================================
                        // DELETE
                        // =================================

                        case "DELETE":

                            if (parts.length < 2) {

                                System.out.println(
                                        "Usage: DELETE key"
                                );

                                break;
                            }

                            int deleteKey =
                                    Integer.parseInt(parts[1]);

                            tree.delete(deleteKey);

                            break;


                        // =================================
                        // SHOW
                        // =================================

                        case "SHOW":

                            tree.printAll();

                            break;


                        // =================================
                        // TREE
                        // =================================

                        case "TREE":

                            tree.printTree();

                            break;


                        // =================================
                        // SIZE
                        // =================================

                        case "SIZE":

                            System.out.println(
                                    "Total records: "
                                            + tree.size()
                            );

                            break;


                        // =================================
                        // EXIT
                        // =================================

                        case "EXIT":

                            scanner.close();

                            db.close();

                            System.out.println(
                                    "Goodbye!"
                            );

                            return;


                        // =================================
                        // UNKNOWN COMMAND
                        // =================================

                        default:

                            System.out.println(
                                    "Unknown command."
                            );
                    }

                } catch (NumberFormatException e) {

                    System.out.println(
                            "Key must be a number."
                    );

                } catch (Exception e) {

                    System.out.println(
                            "Error: "
                                    + e.getMessage()
                    );
                }
            }

        } catch (Exception e) {

            System.out.println(
                    "Database error:"
            );

            e.printStackTrace();

        }
    }
}