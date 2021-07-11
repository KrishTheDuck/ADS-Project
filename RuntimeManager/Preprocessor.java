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

    //streams the source code into each instruction
    private static void stream(String method_body, FSTObjectOutput out, String function_name) throws IOException {
        function_name = AbstractNode.serialize(function_name);
        System.out.println("functionName: " + function_name);
        AbstractNodeUtils ast = new AbstractNodeUtils(function_name);

        StringBuilder acc = new StringBuilder();

        Stack<String> scopes = new Stack<>(); //keeps track of entering scopes
        Stack<String> vars = new Stack<>();

        scopes.add(function_name);

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
        System.out.println();

        writeObjectToStream(out, ast.head());
        out.flush();
    }

    private static void writeObjectToStream(FSTObjectOutput outputStream, AbstractNode head) throws IOException {
        byte[] b = fstc.asByteArray(head);
        outputStream.writeInt(b.length);
        outputStream.write(b, 0, b.length);
        outputStream.flush();
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
        StringBuilder file = new StringBuilder(); //store in a string


        _start = System.currentTimeMillis();
        total:
        {
            Stack<List<String>> methods = new Stack<>(); //stores most important parts of method (return type, method name, parameters, method body)
            normalize:
            {
                methods = normalize(src_code); //get rid of random bits of data that are insignificant
            }
            register:
            {
                Executor.setLibrarySize(methods.size());
                System.out.println("Method count: " + methods.size() + "\n");
            }
            stream:
            {
                while (methods.size() > 0) {
                    List<String> m = methods.pop();
                    stream(m.get(1), out, m.get(0));
                }
            }
        }
        _end = System.currentTimeMillis();
        System.out.println("Done... " + (_end - _start));
        return inf_file;
    }

    //normalizes source code file by removing extra spaces and comments then storing everything into a StringBuilder object
    private static Stack<List<String>> normalize(File src_code) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(src_code));//read in file and store
        Stack<List<String>> methods = new Stack<>();

        char c;
        while ((c = (char) bis.read()) != '\uFFFF') {
            switch (c) {
                case PreprocessorFlags.comment_marker -> {
                    do {
                        c = (char) bis.read();
                    } while (c != '\n' && c != PreprocessorFlags.comment_marker);
                }
                case PreprocessorFlags.mdelim -> {
                    methods.add(new ArrayList<>(2));

                    StringBuilder container = new StringBuilder();
                    while ((c = getOrIgnore(bis, c)) != '{') {
                        container.append(c);
                    }
                    methods.peek().add(container.toString());
                    container.delete(0, container.length());

                    int counter = 1;

                    while ((c = getOrIgnore(bis, c)) != '\uFFFF') {
                        if (c == '{') {
                            ++counter;
                        } else if (c == '}') {
                            --counter;
                            if (counter == 0) break;
                        }
                        container.append(c);
                    }
                    methods.peek().add(container.toString());
                    System.out.println(methods);
                    System.out.println();
                }
            }
        }
        return methods;
    }

    private static char getOrIgnore(BufferedInputStream bis, char prev_char) throws IOException {
        char c;
        while ((c = (char) bis.read()) != '\uFFFF') {
            if (c != '\t' && c != '\r' && c != '\n' && c != PreprocessorFlags.comment_marker) {
                if (prev_char == ' ' && c == ' ')
                    continue;
                return c;
            }
        }
        return c;
    }
}