import Console.Terminal;
import RuntimeManager.RuntimePool;

import java.io.File;
import java.io.RandomAccessFile;

public class Main {
    public static void main(String[] args) {
        try {
            Terminal.evoke();
            RandomAccessFile out = RuntimePool.compile(new File("C:\\Users\\srikr\\Desktop\\adsproject\\ADS-Project\\Files\\$Input.txt"));
            RuntimePool.execute(out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}