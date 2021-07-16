import Kernel.RuntimeManager.RuntimePool;

import java.io.File;
import java.io.RandomAccessFile;

public class Main {
    public static void main(String[] args) {
        try {
//            Terminal.evoke();
            RandomAccessFile out = RuntimePool.compile(new File("C:\\Users\\srikr\\Documents\\GitHub\\ADS-Project\\Files\\$Input.txt"));
//            RuntimePool.execute(out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}