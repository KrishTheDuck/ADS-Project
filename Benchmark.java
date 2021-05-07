package EOY_ADS_PROJECT;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Benchmark {

    public static void main(String[] args) {
        String test = "print(a +\" \"+b+\" \"+c+\" \"+d)";
        System.out.println(test);
        Pattern p = Pattern.compile("\\((.*?)\\)");
        Matcher m = p.matcher(test);
        while (m.find()) {
            System.out.println(m.group());
        }
        System.out.println(test.matches("(.*)\\((.*)\\)(.*)"));
    }
}
