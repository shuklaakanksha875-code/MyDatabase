package mydb;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Record {

    private int key;
    private String value;

    public Record(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public byte[] toBytes() {

        byte[] valueBytes =
                value.getBytes(StandardCharsets.UTF_8);

        ByteBuffer buffer =
                ByteBuffer.allocate(8 + valueBytes.length);

        buffer.putInt(key);
        buffer.putInt(valueBytes.length);
        buffer.put(valueBytes);

        return buffer.array();
    }

    public static Record fromBytes(byte[] data) {

        ByteBuffer buffer =
                ByteBuffer.wrap(data);

        int key = buffer.getInt();

        int valueLength =
                buffer.getInt();

        byte[] valueBytes =
                new byte[valueLength];

        buffer.get(valueBytes);

        String value =
                new String(
                        valueBytes,
                        StandardCharsets.UTF_8
                );

        return new Record(key, value);
    }
}