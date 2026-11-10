package mydb;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BPlusTree {

    private BPlusTreeNode root;
    private StorageManager storage;
    private Map<Integer, Integer> pageMap;
    

   public BPlusTree() {
    root = new BPlusTreeNode(true);
    pageMap = new HashMap<>();
}
    
     public BPlusTree(StorageManager storage) {

    this.storage = storage;

    root = new BPlusTreeNode(true);

    pageMap = new HashMap<>();
}             // =====================================================
// LOAD RECORD FROM DISK
// =====================================================

public void loadRecord(
        int key,
        String value,
        int pageId
) {

    InsertResult result = insertRecursive(
            root,
            key,
            value
    );

    if (result != null) {

        BPlusTreeNode newRoot =
                new BPlusTreeNode(false);

        newRoot.getKeys().add(
                result.separatorKey
        );

        newRoot.getChildren().add(root);

        newRoot.getChildren().add(
                result.rightNode
        );

        root = newRoot;
    }

    pageMap.put(key, pageId);
}
    // =====================================================
    // INSERT
    // =====================================================

    public void insert(int key, String value) 
      throws IOException {

        InsertResult result = insertRecursive(
                root,
                key,
                value
        );

        // Root split हुआ
        if (result != null) {

            BPlusTreeNode newRoot =
                    new BPlusTreeNode(false);

            newRoot.getKeys().add(
                    result.separatorKey
            );

            newRoot.getChildren().add(root);
            newRoot.getChildren().add(result.rightNode);

            root = newRoot;

            System.out.println(
                    "Root split completed."
            );
        }
           if (storage != null) {

               Record record =
               new Record(key, value);

                 int pageId = storage.allocatePage();
                 pageMap.put(
                  key,
                   pageId
                     );
                 storage.saveRecord(
                 pageId,
                  record
                        );
                  }
        System.out.println(
                "Inserted: " + key + " -> " + value
        );
    }

    // =====================================================
    // RECURSIVE INSERT
    // =====================================================

    private InsertResult insertRecursive(
            BPlusTreeNode node,
            int key,
            String value
    ) {

        // ---------------------------------------------
        // LEAF NODE
        // ---------------------------------------------

        if (node.isLeaf()) {

            int position = 0;

            while (
                    position < node.getKeys().size()
                            && node.getKeys().get(position) < key
            ) {
                position++;
            }

            // Duplicate key
            if (
                    position < node.getKeys().size()
                            && node.getKeys().get(position) == key
            ) {

                node.getValues().set(
                        position,
                        value
                );

                return null;
            }

            node.getKeys().add(
                    position,
                    key
            );

            node.getValues().add(
                    position,
                    value
            );

            // Check overflow
            if (node.isFull()) {
                return splitLeaf(node);
            }

            return null;
        }

        // ---------------------------------------------
        // INTERNAL NODE
        // ---------------------------------------------

        int childIndex = 0;

        while (
                childIndex < node.getKeys().size()
                        && key >= node.getKeys().get(childIndex)
        ) {
            childIndex++;
        }

        BPlusTreeNode child =
                node.getChildren().get(childIndex);

        InsertResult result =
                insertRecursive(
                        child,
                        key,
                        value
                );

        // Child split हुआ
        if (result != null) {

            node.getKeys().add(
                    childIndex,
                    result.separatorKey
            );

            node.getChildren().add(
                    childIndex + 1,
                    result.rightNode
            );

            // Internal node overflow
            if (node.isFull()) {
                return splitInternal(node);
            }
        }

        return null;
    }

    // =====================================================
    // SPLIT LEAF
    // =====================================================

    private InsertResult splitLeaf(
            BPlusTreeNode leaf
    ) {

        BPlusTreeNode right =
                new BPlusTreeNode(true);

        int middle =
                leaf.getKeys().size() / 2;

        // Move second half to right
        while (
                leaf.getKeys().size() > middle
        ) {

            int last =
                    leaf.getKeys().size() - 1;

            int key =
                    leaf.getKeys().remove(last);

            String value =
                    leaf.getValues().remove(last);

            right.getKeys().add(
                    0,
                    key
            );

            right.getValues().add(
                    0,
                    value
            );
        }

        // Connect leaf nodes
        right.setNext(
                leaf.getNext()
        );

        leaf.setNext(right);

        // First key of right leaf goes to parent
        int separator =
                right.getKeys().get(0);

        System.out.println(
                "Leaf split: separator = "
                        + separator
        );

        return new InsertResult(
                separator,
                right
        );
    }

    // =====================================================
    // SPLIT INTERNAL NODE
    // =====================================================

    private InsertResult splitInternal(
            BPlusTreeNode node
    ) {

        BPlusTreeNode right =
                new BPlusTreeNode(false);

        int middle =
                node.getKeys().size() / 2;

        // Middle key moves to parent
        int separator =
                node.getKeys().get(middle);

        // Move keys after middle
        for (
                int i = middle + 1;
                i < node.getKeys().size();
        ) {

            right.getKeys().add(
                    node.getKeys().remove(i)
            );
        }

        // Move children after middle
        while (
                node.getChildren().size()
                        > middle + 1
        ) {

            right.getChildren().add(
                    0,
                    node.getChildren().remove(
                            node.getChildren().size() - 1
                    )
            );
        }

        // Remove middle key from left node
        node.getKeys().remove(middle);

        System.out.println(
                "Internal node split: separator = "
                        + separator
        );

        return new InsertResult(
                separator,
                right
        );
    }

    // =====================================================
    // SEARCH
    // =====================================================

    public String search(int key) {

        BPlusTreeNode leaf =
                findLeaf(root, key);

        for (
                int i = 0;
                i < leaf.getKeys().size();
                i++
        ) {

            if (
                    leaf.getKeys().get(i) == key
            ) {

                String value =
                        leaf.getValues().get(i);

                System.out.println(
                        "Found: "
                                + key
                                + " -> "
                                + value
                );

                return value;
            }
        }

        System.out.println(
                "Key " + key + " not found."
        );

        return null;
    }

    // =====================================================
    // FIND LEAF
    // =====================================================

    private BPlusTreeNode findLeaf(
            BPlusTreeNode node,
            int key
    ) {

        if (node.isLeaf()) {
            return node;
        }

        int childIndex = 0;

        while (
                childIndex < node.getKeys().size()
                        && key >= node.getKeys().get(childIndex)
        ) {
            childIndex++;
        }

        return findLeaf(
                node.getChildren().get(childIndex),
                key
        );
    }

    // =====================================================
    // DELETE
    // =====================================================

         public void delete(int key) throws IOException {
        BPlusTreeNode leaf =
                findLeaf(root, key);

        for (
                int i = 0;
                i < leaf.getKeys().size();
                i++
        ) {

            if (
                    leaf.getKeys().get(i) == key
            ) {

                leaf.getKeys().remove(i);
                leaf.getValues().remove(i);
                if (storage != null) {

    Integer pageId =
            pageMap.get(key);

    if (pageId != null) {

        storage.deleteRecord(
                pageId
        );

        pageMap.remove(key);
    }
}

                System.out.println(
                        "Deleted key: " + key
                );

                return;
            }
        }

        System.out.println(
                "Key " + key + " not found."
        );
    }
    // =====================================================
// UPDATE
// =====================================================

public void update(int key, String newValue) throws IOException {

    BPlusTreeNode leaf =
            findLeaf(root, key);

    for (int i = 0; i < leaf.getKeys().size(); i++) {

        if (leaf.getKeys().get(i) == key) {

            // Update value in B+ Tree
            leaf.getValues().set(
                    i,
                    newValue
            );

            // Update value on disk
            if (storage != null) {

                Integer pageId =
                        pageMap.get(key);

                if (pageId != null) {

                    Record record =
                            new Record(
                                    key,
                                    newValue
                            );

                    storage.saveRecord(
                            pageId,
                            record
                    );
                }
            }

            System.out.println(
                    "Updated: "
                            + key
                            + " -> "
                            + newValue
            );

            return;
        }
    }

    System.out.println(
            "Key " + key + " not found."
    );
}

    // =====================================================
    // PRINT ALL
    // =====================================================
           public String getAllRecords() {

    StringBuilder result =
            new StringBuilder();

    BPlusTreeNode node = root;

    // Go to leftmost leaf
    while (!node.isLeaf()) {
        node = node.getChildren().get(0);
    }

    // Traverse all leaves
    while (node != null) {

        for (int i = 0;
             i < node.getKeys().size();
             i++) {

            result.append(
                    node.getKeys().get(i)
            );

            result.append(" -> ");

            result.append(
                    node.getValues().get(i)
            );

            result.append("\n");
        }

        node = node.getNext();
    }

    return result.toString();
}
    public void printAll() {

        System.out.println();
        System.out.println(
                "----- B+ Tree Data -----"
        );

        printNode(root);

        System.out.println(
                "------------------------"
        );
    }
             public String getAllRecords() {

    StringBuilder result =
            new StringBuilder();

    BPlusTreeNode node = root;

    // Leftmost leaf तक जाओ
    while (!node.isLeaf()) {
        node = node.getChildren().get(0);
    }

    // सभी leaf nodes पढ़ो
    while (node != null) {

        for (int i = 0;
             i < node.getKeys().size();
             i++) {

            result.append(
                    node.getKeys().get(i)
            );

            result.append(" -> ");

            result.append(
                    node.getValues().get(i)
            );

            result.append("\n");
        }

        node = node.getNext();
    }

    return result.toString();
}

    private void printNode(
            BPlusTreeNode node
    ) {

        if (node.isLeaf()) {

            for (
                    int i = 0;
                    i < node.getKeys().size();
                    i++
            ) {

                System.out.println(
                        node.getKeys().get(i)
                                + " -> "
                                + node.getValues().get(i)
                );
            }

            return;
        }

        for (
                BPlusTreeNode child
                        : node.getChildren()
        ) {

            printNode(child);
        }
    }

    // =====================================================
    // PRINT TREE STRUCTURE
    // =====================================================

    public void printTree() {

        System.out.println();
        System.out.println(
                "===== TREE STRUCTURE ====="
        );

        printStructure(root, 0);

        System.out.println(
                "=========================="
        );
    }

    private void printStructure(
            BPlusTreeNode node,
            int level
    ) {

        for (int i = 0; i < level; i++) {
            System.out.print("    ");
        }

        if (node.isLeaf()) {

            System.out.println(
                    "Leaf: " + node.getKeys()
            );

        } else {

            System.out.println(
                    "Internal: " + node.getKeys()
            );

            for (
                    BPlusTreeNode child
                            : node.getChildren()
            ) {

                printStructure(
                        child,
                        level + 1
                );
            }
        }
    }

    // =====================================================
    // SIZE
    // =====================================================

    public int size() {

        return countRecords(root);
    }

    private int countRecords(
            BPlusTreeNode node
    ) {

        if (node.isLeaf()) {
            return node.getKeys().size();
        }

        int total = 0;

        for (
                BPlusTreeNode child
                        : node.getChildren()
        ) {

            total += countRecords(child);
        }

        return total;
    }

    // =====================================================
    // INSERT RESULT
    // =====================================================

    private static class InsertResult {

        int separatorKey;

        BPlusTreeNode rightNode;

        InsertResult(
                int separatorKey,
                BPlusTreeNode rightNode
        ) {

            this.separatorKey =
                    separatorKey;

            this.rightNode =
                    rightNode;
        }
    }
             public Record readRecordFromDisk(int key) throws IOException {

    Integer pageId = pageMap.get(key);

    if (pageId == null) {
        System.out.println("Key " + key + " not found on disk.");
        return null;
    }

    return storage.readRecord(pageId);
}
}