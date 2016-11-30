package nbipackage;

import java.io.*;
import java.nio.channels.*;

public class FileUtils {

    public FileUtils() {
        ;
    }

    public static void copyFile(String in, String out) throws IOException {

        System.out.println("	" + out);
        FileChannel inChannel = new FileInputStream(in).getChannel();
        FileChannel outChannel = new FileOutputStream(out).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (IOException e) {
            System.err.println(e);
            throw e;
        } finally {
            if (inChannel != null) {
                inChannel.close();
            }
            if (outChannel != null) {
                outChannel.close();
            }
        }
    }
}
