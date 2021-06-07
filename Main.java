import Console.Terminal;
import Kernel.Executor;
import Kernel.Preprocessor;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        try {
            Terminal t = Terminal.evoke();
            System.out.println("\n\nStarting Preprocessing...\n\n");
            Preprocessor c = new Preprocessor(new File("C:\\Users\\srikr\\Desktop\\adsproject\\ADS-Project\\$Input.txt"));
            File f = c.compile();
            System.out.println("\n\nPreprocessing finished. Beginning Execution.\n\n");
            Executor.InstructionLoader("C:\\Users\\srikr\\Desktop\\adsproject\\ADS-Project\\$instruction.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
