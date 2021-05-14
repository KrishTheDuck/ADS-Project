package EOY_ADS_PROJECT.Compiler.Kernel;

import EOY_ADS_PROJECT.Compiler.ParserHelper.IntegerParser;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private File program_file;
    public final static String op_t = "<>";

    public Compiler(File program_file) throws FileNotFoundException {
        if (program_file.exists()) {
            if (program_file.canRead()) {
                //TODO register file types and check if they correct
                this.program_file = program_file;
            }
        } else {
            throw new FileNotFoundException();
        }
    }

    public File compile() throws Exception {
        StringBuilder file = new StringBuilder(); //store in a string

        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(program_file));//read in file and store
        char prev_char = (char) bis.read();
        char c;
        //normalizing source code
        /*
         * comment
         * code
         * code comment
         * */
        while ((c = (char) bis.read()) != '\uFFFF') {
            if (prev_char == '#') { //comment marker
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

        File i_file = new File("instruction.txt"); //create instruction file

        if (!i_file.exists()) {
            if (!i_file.createNewFile())
                throw new Exception("Instruction file creation failed.");
        } else {
            System.out.println("file exists at: " + i_file.getAbsolutePath());
        }

        System.out.println("This is the normalized file: "+file);

        StringTokenizer method_tokenizer = new StringTokenizer(file.toString(), "$"); //universal String Tokenizer object
        List<ArrayList<String>> methods = new ArrayList<>(); //stores most important parts of method (return type, method name, parameters, method body)

        while (method_tokenizer.hasMoreTokens()) {
            String method, return_type, method_name, method_body;
            String[] params;
            method = method_tokenizer.nextToken().strip();
            return_type = method.substring(0, method.indexOf(" ")).strip();
            method_name = method.substring(method.indexOf(" "), method.indexOf("(")).strip();

            if (return_type.equals("struct") && method.endsWith(";")) {
                method_body = "";
            } else {
                method_body = method.substring(method.indexOf("{") + 1, method.lastIndexOf("}"));
            }

            params = method.substring(method.indexOf("(") + 1, method.indexOf(")")).split(",");
            System.out.printf("Method: %s\nReturn Type: %s\nMethod Name: %s\nParameters: %s\n", method, return_type, method_name, Arrays.toString(params));
            System.out.println("-----------------");

            methods.add(new ArrayList<>(Arrays.asList(method_name, Arrays.toString(params), return_type, method_body)));
        }

        //NOTEME method name, parameters, return type, method body

        //FIXME CANNOT UNWRAP METHODS AS IT WILL BECOME INFINITE WHEN RECURSION IS DETECTED!!!!!
        // - Solution: don't unwrap method calls leave em as is and compile when needed

        //compilation
        System.out.println("\n\nUnwrapping methods...\n\n");


        //NOTEME really fucking annoying-to-code compilation. all this does is check every condition lazily. please please please please make this better.
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
        BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(i_file));
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
        return i_file;
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

    /*NOTEME we need to pass in Terminal instance to Executor through some kind of method or instruction as "read" will not be possible
     *  or we could create a command in i_file that evokes the Terminal. Either one idk.*/
    public static void main(String... args) throws Exception {
//        Terminal t = Terminal.evoke();
        Compiler c = new Compiler(new File("C:\\Users\\srikr\\Desktop\\Programs\\ADS\\src\\main\\java\\EOY_ADS_PROJECT\\$Input.txt"));
        File f = c.compile();
//        Executor.execute(new File("C:\\Users\\srikr\\Desktop\\Programs\\ADS\\instruction.txt"));
    }
}
// PLEASE WORK