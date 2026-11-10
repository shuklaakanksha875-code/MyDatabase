package mydb;

import java.util.ArrayList;
import java.util.List;

public class BPlusTreeNode {

    private static final int MAX_KEYS = 4;

    private final List<Integer> keys;
    private final List<String> values;
    private final List<BPlusTreeNode> children;

    private boolean leaf;
    private BPlusTreeNode next;

    public BPlusTreeNode(boolean leaf) {

        this.leaf = leaf;

        keys = new ArrayList<>();
        values = new ArrayList<>();
        children = new ArrayList<>();

        next = null;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public List<Integer> getKeys() {
        return keys;
    }

    public List<String> getValues() {
        return values;
    }

    public List<BPlusTreeNode> getChildren() {
        return children;
    }

    public BPlusTreeNode getNext() {
        return next;
    }

    public void setNext(BPlusTreeNode next) {
        this.next = next;
    }

    public boolean isFull() {
        return keys.size() > MAX_KEYS;
    }

    public int keyCount() {
        return keys.size();
    }

    public static int getMaxKeys() {
        return MAX_KEYS;
    }
}