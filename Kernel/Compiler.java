package Kernel;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Takes in a file path, compiles instructions, creates a variable control JSON file, creates a instruction file preserving order of insertion.
 *
 * <p>
 * since alpha (without conditionals functions or recursions) compilation of three instructions takes approximately 14-18 ms.
 * </p>
 *
 * @author Kevin Wang, Krish Sridhar
 * @since 1.0
 * Date: February 24, 2021
 */
@SuppressWarnings("unused")
public final class Compiler {
    public final static String op_t = "<>";
    private final static char comment_marker = '#';
    private File src_code;
    private File inf_code;

    public Compiler(File program_file) throws FileNotFoundException {
        if (program_file.exists()) {
            if (program_file.canRead()) {
                //TODO register file types and check if they correct
                this.src_code = program_file;
                this.inf_code = new File("C:\\Users\\srikr\\Desktop\\adsproject\\ADS-Project\\$instruction.txt");
            }
        } else {
            throw new FileNotFoundException();
        }
    }

    private Compiler() {
    }

    private static <T> String ARR_TO_STR(T[] arr) {
        StringBuilder sb = new StringBuilder();
        for (T element : arr) {
            sb.append(element.toString()).append(" ");
        }
        return sb.toString();
    }

    public static void main(String... args) throws Exception {
//        Terminal t = Terminal.evoke();
        Compiler c = new Compiler(new File("C:\\Users\\srikr\\Desktop\\adsproject\\ADS-Project\\$Input.txt"));
        File f = c.compile();
//        Executor.execute(new File("C:\\Users\\srikr\\Desktop\\Programs\\ADS\\$instruction.txt"));
//        System.out.println(Arrays.toString(" ".getBytes()));
    }

    private void normalize(StringBuilder file) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(src_code));//read in file and store
        char prev_char = (char) bis.read();
        char c;
        while ((c = (char) bis.read()) != '\uFFFF') {
            if (prev_char == comment_marker) { //comment marker
                do {
                    prev_char = (char) bis.read();
                } while (prev_char != '\n');
                continue;
            }

            if (c != '\n' && c != '\r' && c != '\t' && c != '#') {
                if (prev_char == ' ' && c == ' ')
                    continue;
                file.append(c);
            }
            prev_char = c;
        }
        bis.close();

    }

    private void register_components(List<ArrayList<String>> methods, StringBuilder file) {
        StringTokenizer method_tokenizer = new StringTokenizer(file.toString(), "$"); //universal String Tokenizer object
        while (method_tokenizer.hasMoreTokens()) {
            String method, return_type, method_name, method_body;
            String[] params;
            method = method_tokenizer.nextToken().strip();
            System.out.println("Method: " + method);
            return_type = method.substring(0, method.indexOf(" ")).strip();
            System.out.println("Return_type: " + return_type);
            method_name = (!return_type.equals("DEFINE") ? method.substring(method.indexOf(" "), method.indexOf("(")).strip() :
                    method.substring(method.indexOf(" "), method.indexOf("->"))).strip();

            System.out.println("Method name: " + method_name);

            if (return_type.equals("DEFINE")) {
                method_body = method.substring(method.indexOf("->"));
            } else {
                method_body = method.substring(method.indexOf("{") + 1, method.lastIndexOf("}"));
            }

            params = (!return_type.equals("DEFINE")) ? method.substring(method.indexOf("(") + 1, method.indexOf(")")).split(",") : new String[0];
            System.out.println("-----------------");

            methods.add(new ArrayList<>(Arrays.asList(method_name, Arrays.toString(params), return_type, method_body)));
        }
    }

    private Queue<String> stream(String method_body) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(method_body.getBytes(StandardCharsets.UTF_8)));

        Queue<String> list = new LinkedList<>();
        Stack<String> scopes = new Stack<>();

        char i;
        String scope;
        StringBuilder acc = new StringBuilder();
        while ((i = (char) bis.read()) != '\uFFFF') {
            switch (i) {
                case '{', ';' -> {
                    if (acc.toString().strip().startsWith("if")) {
                        scope = "end if";
                        scopes.push(scope);
                    } else if (acc.toString().strip().startsWith("else")) {
                        scope = "end else";
                        scopes.push(scope);
                    } else if (acc.toString().strip().startsWith("else if")) {
                        scope = "end elif";
                        scopes.push(scope);
                    }
                    list.add(acc.toString());
                    acc.delete(0, acc.length());
                }
                case '}' -> list.add(scopes.pop());
                default -> acc.append(i);
            }
        }
        return list;
    }

    public File compile() throws Exception {
        StringBuilder file = new StringBuilder(); //store in a string
        normalize(file);


        if (!this.inf_code.exists()) {
            if (!this.inf_code.createNewFile())
                throw new Exception("Instruction file creation failed.");
        } else {
            System.out.println("file exists at: " + this.inf_code.getAbsolutePath());
        }

        System.out.println("This is the normalized file: " + file);

        List<ArrayList<String>> methods = new ArrayList<>(); //stores most important parts of method (return type, method name, parameters, method body)
        register_components(methods, file);

        System.out.println("\nUnwrapping methods...\n");

        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(inf_code));
        //todo finish statement wise stream

        Queue<String> instructions = stream(methods.get(indexOfMain(methods)).get(3));
        int tab = 0;
        System.out.println(instructions);
        while (instructions.size() > 0) {
            String[] lhs;
            String rhs;
            String opcode;
            String instruction = instructions.remove().strip();

            for (int i = 0; i < tab; i++) {
                bos.write(9);
            }

            //instruction: opcode lhs rhs
            if (instruction.contains("=")) {
                rhs = instruction.substring(instruction.indexOf("=") + 1).strip();
                lhs = instruction.substring(0, instruction.indexOf("=")).strip().split("[ ]+");
                opcode = (lhs.length >= 2) ? "mal" : "set";
                instruction = opcode + " " + ARR_TO_STR(lhs) + " " + rhs;
            } else if (instruction.startsWith("if") || instruction.startsWith("else if")) {
                ++tab;
                opcode = "chk";
                rhs = instruction.substring(instruction.indexOf("(") + 1, instruction.indexOf(")"));
                instruction = opcode + " " + rhs;
            } else if (instruction.startsWith("else")) {
                ++tab;
                opcode = "rslv";
                instruction = opcode;
            } else if (instruction.startsWith("end")) {
                --tab;
                instruction = (instruction.endsWith("if")) ? "end chk" : "end rslv";
            } else if (instruction.contains(".")) {
                opcode = "call";
                lhs = instruction.substring(instruction.indexOf("(") + 1, instruction.indexOf(")")).split(",");
                rhs = instruction.substring(instruction.indexOf(".") + 1, instruction.indexOf("("));
                String function = instruction.substring(0, instruction.indexOf("."));
                instruction = opcode + " " + function + " " + rhs + " " + ARR_TO_STR(lhs);
            }

            bos.write(instruction.getBytes());
            bos.write(10);
        }
        bos.flush();


        return this.inf_code;
    }

    private int indexOfMain(List<ArrayList<String>> methods) {
        int index = 0;
        for (ArrayList<String> method : methods) {
            if (method.get(0).equals("main"))
                return index;
            ++index;
        }
        return -1;
    }
}