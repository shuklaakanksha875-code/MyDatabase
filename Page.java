package mydb;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Page {

    public static final int PAGE_SIZE = 4096;

    private final byte[] data;

    public Page() {
        data = new byte[PAGE_SIZE];
    }

    public Page(byte[] input) {

        data = new byte[PAGE_SIZE];

        int length = Math.min(
                input.length,
                PAGE_SIZE
        );

        System.arraycopy(
                input,
                0,
                data,
                0,
                length
        );
    }

    // Put text inside page
    public void writeString(String text) {

        byte[] bytes = text.getBytes(
                StandardCharsets.UTF_8
        );

        int length = Math.min(
                bytes.length,
                PAGE_SIZE
        );

        System.arraycopy(
                bytes,
                0,
                data,
                0,
                length
            );
          }
          public void writeBytes(byte[] input) {

            int length = Math.min(
            input.length,
            PAGE_SIZE
             );

          System.arraycopy(
            input,
            0,
            data,
            0,
            length
              );
             }

    // Read page as String
         public String readString() {

         int length = 0;

         while (
                length < PAGE_SIZE &&
                data[length] != 0
         ) {
            length++;
         }

         return new String(
                data,
                0,
                length,
                StandardCharsets.UTF_8
              );
            }

    // Get complete page bytes
          public byte[] getBytes() {

          return Arrays.copyOf(
                data,
                data.length
             );
           }
    
         public void writeToFile(RandomAccessFile file, int pageId) throws IOException {

         long position = (long) pageId * PAGE_SIZE;

         file.seek(position);

         file.write(getBytes());

            }
         public static Page readFromFile(DatabaseFile db, int pageId) throws IOException {

            long position = (long) pageId * PAGE_SIZE;

          byte[] data = db.read(position, PAGE_SIZE);

          return new Page(data);
 
         }
         }