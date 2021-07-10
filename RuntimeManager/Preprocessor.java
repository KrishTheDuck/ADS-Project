package RuntimeManager;

import Kernel.Data_Structures.AbstractNodeUtils;
import Kernel.Data_Structures.Node.AbstractNode;
import Kernel.Data_Structures.Node.CNode;
import Kernel.Data_Structures.Node.INode;
import ParserHelper.UniversalParser;
import org.nustaq.serialization.FSTConfiguration;
import org.nustaq.serialization.FSTObjectOutput;

import java.io.*;
import java.util.*;

/**
 * Takes in a file path, compiles instructions, creates a variable control JSON file, creates a instruction file preserving order of insertion.
 *
 * @author Krish Sridhar
 * @see <a href=https://github.com/RuedigerMoeller/fast-serialization>Fast Serialization</a>
 * @since 1.0
 * <p>Date: February 24, 2021
 */
@SuppressWarnings("unused")
final class Preprocessor {
    public static final FSTConfiguration fstc = FSTConfiguration.createDefaultConfiguration();

    static {
        fstc.registerClass(AbstractNode.class, INode.class, CNode.class);
    }

    //you cant use me if you don't give me a file.
    private Preprocessor() {
    }

    //converts an array of type T to a string that can be appended to the file
    //it uses the compilation tokenizer so that reading from the file can be simple.
    private static <T> String ARR_TO_STR(T[] arr) {
        StringBuilder sb = new StringBuilder();
        for (T element : arr) {
            sb.append(element.toString()).append(PreprocessorFlags.tknzr);
        }
        return sb.toString();
    }

    //extends an array given varargs.
    private static String[] extend(String[] arr, String... vars) {
        String[] n_arr = new String[arr.length + vars.length];

        if (n_arr.length - arr.length >= 0) {
            System.arraycopy(arr, 0, n_arr, 0, arr.length);
            System.arraycopy(vars, 0, n_arr, arr.length, n_arr.length - arr.length);
        }
        return n_arr;
    }

    //converts the source codes methods into a list of lists which contains only the most pertinent information
    //about a method such as the method body, parameters, name, and return signature.
    private static void register_components(Queue<ArrayList<String>> methods, StringBuilder file) {
        StringTokenizer method_tokenizer = new StringTokenizer(file.toString(), PreprocessorFlags.mdelim); //Tokenize by method delimiter
        while (method_tokenizer.hasMoreTokens()) { //for every method
            String method, return_type, method_name, method_body; //important information
            String[] params;
            method = method_tokenizer.nextToken().strip(); // get the entire method
            return_type = method.substring(0, method.indexOf(" ")).strip(); // get the return type (first token)
            method_name = method.substring(method.indexOf(" "), method.indexOf("(")).strip(); //get the name (second token)
            method_body = method.substring(method.indexOf("{") + 1, method.lastIndexOf("}")); //get method body from first to last curly bracket
            params = method.substring(method.indexOf("(") + 1, method.indexOf(")")).split(PreprocessorFlags.pdelim); //get the parameters and split by parameter delimiter
            methods.add(new ArrayList<>(Arrays.asList(method_name, Arrays.toString(params), return_type, method_body))); //stores method info into the list
        }
    }


    //streams the source code into each instruction
    private static void stream(String method_body, FSTObjectOutput out, String function_name) throws IOException {
        AbstractNodeUtils ast = new AbstractNodeUtils(function_name);

        StringBuilder acc = new StringBuilder();

        Stack<String> scopes = new Stack<>(); //keeps track of entering scopes
        Stack<String> vars = new Stack<>();

        scopes.add(ast.head().serial());

        String p = "";

        char[] charArray = method_body.toCharArray();
        for (char i : charArray) {
            switch (i) {
                case ';' -> {
                    int e_index = acc.indexOf("=");
                    int p_index = acc.indexOf(PreprocessorFlags.call_delim);
                    String lhs = "", rhs = "";
                    if (acc.indexOf("return") != -1) {
                        lhs = "return" + PreprocessorFlags.tknzr;
                        if (p_index != -1) { //return call to other method
                            rhs = "call" + PreprocessorFlags.tknzr + acc.substring(8);
                        } else { //return expression?
                            rhs = acc.substring(8);
                        }
                    } else if (e_index != -1 || p_index != -1) {
                        if (p_index != -1) { //yes method
                            if (e_index != -1) {
                                if (e_index < p_index) { //equality first
                                    lhs = ReplaceWithMallocScript(new StringBuilder(acc.substring(0, e_index + 1).strip()), scopes, vars);
                                    rhs = "call" + PreprocessorFlags.tknzr + acc.substring(e_index + 1).strip();
                                } else { //normal call
                                    lhs = "call" + PreprocessorFlags.tknzr + acc.toString().strip();
                                }
                            } else {//no equality but yes method
                                rhs = "call" + PreprocessorFlags.tknzr + acc.toString().strip();
                            }
                        } else { //no method but yes equality
                            lhs = ReplaceWithMallocScript(acc, scopes, vars);
                        }
                    }
                    String line = lhs + rhs;
                    acc.delete(0, acc.length());
                    ast.load(line);
                }
                case '{' -> {   //'{' operator indicates entering a scope, ';' operator indicates instruction end
                    String s = acc.toString().strip();
                    String scope = "";
                    if (s.startsWith("if")) { //if condition scope
                        scope = "if";
                        p = acc.substring(acc.indexOf("(") + 1, acc.lastIndexOf(")"));
                    } else if (s.startsWith("else if")) { //else if
                        scope = "elif";
                        p = acc.substring(acc.indexOf("(") + 1, acc.lastIndexOf(")"));
                    } else if (s.startsWith("for")) {
                        scope = "for";
                        p = acc.substring(acc.indexOf("(") + 1, acc.lastIndexOf(")"));
                    } else if (s.startsWith("do")) {
                        scope = "do_while";
                        p = acc.substring(acc.indexOf("(") + 1, acc.lastIndexOf(")"));
                    } else if (s.startsWith("while")) {
                        scope = "while";
                        p = acc.substring(acc.indexOf("(") + 1, acc.lastIndexOf(")"));
                    } else if (s.startsWith("else")) { //else
                        scope = "else";
                        p = "";
                    } else if (s.endsWith(":")) { //a custom scope "<scope name> : {}", ":" operator indicates the name of scope
                        scope = acc.substring(0, acc.indexOf(":")).strip(); //uds: user defined scope
                        p = "";
                    }
                    scopes.push(scope);
                    switch (scope) {
                        case "if", "elif" -> {
                            ast.add_and_enter(scope, true);
                            ast.set_condition(p);
                        }
                        case "else" -> {
                            ast.add_and_enter(scope, true);
                            ast.set_condition("1");
                        }
                        default -> ast.add_and_enter(scope, false);
                    }
                    acc.delete(0, acc.length()); //clear the string
                }
                case '}' -> {
                    String scope = UniversalParser.scopeIt(scopes);
                    if (vars.peek().startsWith(scope)) {
                        do {
                            ast.load("del" + PreprocessorFlags.tknzr + vars.pop());
                        } while (vars.peek().startsWith(scope));
                    }
                    acc.delete(0, acc.length());
                    ast.load("end");
                    ast.move_back();
                    ast.load("chk" + PreprocessorFlags.tknzr + scopes.pop());
                }
                default -> acc.append(i); //add char to stringbuilder
            }
        }
        ast.head();
        //delete the rest
        while (vars.size() > 0)
            ast.load("del" + PreprocessorFlags.tknzr + vars.pop());
        //load exit flag
        ast.load(PreprocessorFlags.EXIT);
        ast.print();

        writeObjectToStream(out, ast.head());
        out.flush();
    }

    private static void writeObjectToStream(FSTObjectOutput outputStream, AbstractNode head) throws IOException {
        byte[] b = fstc.asByteArray(head);
        outputStream.writeInt(b.length);
        outputStream.write(b, 0, b.length);
        outputStream.flush();
        System.out.println();
    }


    private static String ReplaceWithMallocScript(StringBuilder instruction, Stack<String> scopes, Stack<String> vars) {
        instruction = (instruction.charAt(0) == ' ') ? new StringBuilder(instruction.substring(1)) : instruction;
        String[] lhs = instruction.substring(0, instruction.indexOf("=")).split("[ ]+"); //get value and properties
        lhs[lhs.length - 1] = UniversalParser.scopeIt(scopes) + lhs[lhs.length - 1];
        vars.push(lhs[lhs.length - 1]);
        String rhs = instruction.substring(instruction.indexOf("=") + 1);
        instruction.delete(0, instruction.length()); //clear string
        String opcode = lhs.length >= 2 ? "mal" : "set";
        instruction.append(opcode).append(PreprocessorFlags.tknzr).append(ARR_TO_STR(lhs)).append(rhs.strip()); //add instruction
        return instruction.toString();
    }

    /**
     * Compiles the source code into language and syntax that the executor can more easily understand.
     *
     * @return Instruction File.
     * @throws IOException When instruction file cannot be created.
     */
    @SuppressWarnings("all")
    public static RandomAccessFile compile(File src_code) throws IOException, NoSuchFieldException, IllegalAccessException {
        final long _start, _end;

        RandomAccessFile inf_file = new RandomAccessFile("C:\\Users\\srikr\\Desktop\\adsproject\\ADS-Project\\Files\\$instruction.txt", "rwd");
        inf_file.seek(0);
        FSTObjectOutput out = new FSTObjectOutput(new BufferedOutputStream(new FileOutputStream(inf_file.getFD())), fstc);
//        ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(inf_file.getFD())));
        StringBuilder file = new StringBuilder(); //store in a string


        _start = System.currentTimeMillis();
        total:
        {
            normalize:
            {
                normalize(file, src_code); //get rid of random bits of data that are insignificant
            }
            Queue<ArrayList<String>> methods = new LinkedList<>(); //stores most important parts of method (return type, method name, parameters, method body)
            register:
            {
                register_components(methods, file); //store the components of the "file"  in the "methods" list
                Executor.setLibrarySize(methods.size());
                System.out.println("Method count: " + methods.size() + "\n");
            }
            stream:
            {
                while (methods.size() > 0) {
                    List<String> m = methods.remove();
                    stream(m.get(3), out, m.get(2) + " " + m.get(0) + " " + String.join(" ", m.get(1)));
                }
            }
        }
        _end = System.currentTimeMillis();
        System.out.println("Done... " + (_end - _start));
        return inf_file;
    }

    //normalizes source code file by removing extra spaces and comments then storing everything into a StringBuilder object
    private static void normalize(StringBuilder file, File src_code) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(src_code));//read in file and store
        char prev_char = (char) bis.read();
        char c;
        while ((c = (char) bis.read()) != '\uFFFF') { //continue until EOF
            if (prev_char == PreprocessorFlags.comment_marker) { //remove comment marker
                do {
                    prev_char = (char) bis.read();
                } while (prev_char != '\n');
                continue;
            }

            if (c != '\n' && c != '\r' && c != '\t' && c != '#') { //remove extras
                if (prev_char == ' ' && c == ' ')
                    continue;
                file.append(c);
            }
            prev_char = c;
        }
        bis.close();
    }
}