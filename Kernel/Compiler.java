package Kernel;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Executor;

/**
 * Takes in a file path, compiles instructions, creates a variable control JSON file, creates a instruction file preserving order of insertion.
 * <p>
 * TODO: make conditionals, functions, and recursive functions work probably use some kind of pattern-matching algorithm in {@linkplain Executor}
 * </p>
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
    private File src_code;
    private File inf_code;
    private final static char comment_marker = '#';

    public final static String op_t = "<>";

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

    private static <T> String ARR_TO_STR(T[] arr) {
        StringBuilder sb = new StringBuilder();
        for (T element : arr) {
            sb.append(element.toString()).append(" ");
        }
        return sb.toString();
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

    private Compiler() {
    }

    public static void main(String... args) throws Exception {
//        Terminal t = Terminal.evoke();
        Compiler c = new Compiler(new File("C:\\Users\\srikr\\Desktop\\adsproject\\ADS-Project\\$Input.txt"));
        File f = c.compile();
//        Executor.execute(new File("C:\\Users\\srikr\\Desktop\\Programs\\ADS\\$instruction.txt"));
//        System.out.println(Arrays.toString(" ".getBytes()));
    }
}
//Test

/*
*
        for (ArrayList<String> method_properties : methods) {
            String line;
            String datatype, varname;
            StringBuilder value = new StringBuilder();
            String[] expression;
            String[] sub;
            StringBuilder instruction = new StringBuilder();
            boolean arrow_operator = false;

            instruction.delete(0, instruction.length());
            method_tokenizer = new StringTokenizer(method_properties.get(3), ";");

            while (method_tokenizer.hasMoreTokens()) {
                if (((line = method_tokenizer.nextToken().strip()).contains("=") || (arrow_operator = line.contains("~>")))) { //not a plain method call
                    System.out.println("Instruction: " + line);

                    value.delete(0, value.length());
                    //first ascertain what type the variable you're setting to is
                    expression = (arrow_operator) ? line.strip().split("~>") : line.strip().split("=");
                    sub = expression[0].strip().split(" ");
                    value.append(expression[1].strip());

                    //Everytime we find a var we replace it with <method>.<var>

                    Pattern p = Pattern.compile("[a-zA-Z0-9]+");
                    Matcher m = p.matcher(value);
                    ArrayList<String> vars = new ArrayList<>();

                    int i = 0;
                    while (m.find()) { //everytime we add a <method>. to the beginning of a var we have to increase the next index subsequently.
                        String s = m.group().trim().strip();
                        if (IntegerParser.isInteger(s) || s.equals(" ") || s.equals("")) continue;

                        vars.add(s + " " + (m.start() + i) + " " + (m.end() + i));
                        i += method_properties.get(0).length() + 1;
                    }
                    //replace values

                    for (String var : vars) {
                        String[] tokens = var.split(" "); // varname, start pos, end pos
                        value.replace(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]), method_properties.get(0) + "." + tokens[0]);
                    }

                    //NOTEME if you find a way to get "find" working for a certain starting index use better algo with concurrency instead of procedural

                    if (sub.length == 1) { //most likely fits [variable/struct, value/struct/method]
                        varname = sub[0].strip();
                        instruction.append("SET" + op_t).append(method_properties.get(0)).append(".").append(varname).append(op_t + "TO" + op_t);
                    } else if (sub.length == 2) { //most likely fits [datatype, variable, value/struct/method]
                        datatype = sub[0].strip();
                        varname = sub[1].strip();
                        instruction.append("CREATE" + op_t).append(datatype).append(op_t).append(method_properties.get(0)).append(".").append(varname).append(op_t + "TO" + op_t);
                    } else { //if there are more than one elements to the variable side then something went horribly wrong
                        throw new IOException("Expression error: \"" + line + "\" was parsed into \"" + Arrays.toString(expression) + "\" of length " + expression.length + " and could not be split into given cases.");
                    }

                    if (varname.contains(".")) { //setting to a struct
                        instruction.append(value); //<STRUCT NAME>.<variable
                    } else if (arrow_operator) { // setting to a method (method must return value)
                        instruction.append("METHOD" + op_t).append(value); //METHOD AHHHHHHHHHHHH
                    } else { //setting to another var or a value
                        instruction.append("EXECUTE" + op_t).append(value);
                    }
                } else if (line.strip().matches("(.*?)\\((.*?)\\)(.*?)")) {
                    System.out.println("Plain method call detected: " + line);
                    int index = line.indexOf(".");
                    Matcher m;
                    boolean a = (m = Pattern.compile("\\((.*?)\\)").matcher(line)).find();
                    String s = m.group().replaceAll("[()]", "");
                    System.out.println("Params: " + s + "\n\n\n");
                    String[] params = s.split(",");

                    //TODO fix the <method>. assignment.
                    System.out.println("Method params: " + Arrays.toString(params));
                    for (int i = 0; i < params.length; i++) {
                        String var = params[i];
                        if (var.startsWith("\"") && var.endsWith("\"")) continue;

                        params[i] = var.replace(var, method_properties.get(0) + "." + var);
                    }

                    if (index < m.start() && index != -1) { //there is a library call
                        instruction.append("FROMLIB" + op_t).append(line, 0, index).append(op_t).append(line, index + 1, m.start()).append(op_t).append(Arrays.toString(params)); //FROMLIB <LIBRARY> <FUNCTION>
                    } else {
                        instruction.append("CALL" + op_t).append(line, 0, m.start()).append(op_t).append(Arrays.toString(params));
                    }
                }
                instruction.append("\n");
                arrow_operator = false;
            }
            System.out.println("Instruction Stack for method \"" + method_properties.get(0) + "\":");
            System.out.println(instruction);
            method_properties.set(3, instruction.toString());
        }

        //write to file
        int index = indexOfMain(methods);
        BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(this.inf_code));
        method_tokenizer = new StringTokenizer(methods.get(index).get(3), "\n", true);

        //compile structs first

        for (ArrayList<String> method : methods) {
            switch (method.get(2)) {
                case "struct" -> {
                    StringBuilder params = new StringBuilder();
                    StringTokenizer st = new StringTokenizer(method.get(1).substring(1, method.get(1).length() - 1), ",");
                    while (st.hasMoreTokens()) {
                        params.append(op_t).append(st.nextToken().strip());
                    }
                    fos.write(("COMPILE" + op_t + "STRUCT" + op_t + method.get(0) + params + " \n").getBytes());
                }
                case "queue", "list", "stack" -> {
                }
            }
        }

        while (method_tokenizer.hasMoreTokens()) {
            fos.write(method_tokenizer.nextToken().getBytes());
        }
        fos.write("END".getBytes());
        fos.flush();

        fos.close();
*
* */