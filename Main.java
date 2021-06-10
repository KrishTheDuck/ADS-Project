import Console.Terminal;
import RuntimeManager.RuntimePool;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        try {
            Terminal.evoke();
            File out = RuntimePool.compile(new File("C:\\Users\\srikr\\Desktop\\adsproject\\ADS-Project\\$Input.txt"));
            RuntimePool.execute(out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
