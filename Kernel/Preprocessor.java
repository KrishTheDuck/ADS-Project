package Kernel;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Takes in a file path, compiles instructions, creates a variable control JSON file, creates a instruction file preserving order of insertion.
 *
 * @author Kevin Wang, Krish Sridhar
 * @since 1.0
 * Date: February 24, 2021
 */
@SuppressWarnings("unused")
public final class Preprocessor {
    public final static String tknzr = "  "; //stores the token that separates meaningful operation codes
    private final static char comment_marker = '#'; //stores the token that indicates a comment
    private final static String mdelim = "$"; //stores method delimiter
    private final static String pdelim = ","; //stores parameter delimiter
    private File src_code; //source code
    private File inf_code; //instruction file code


    /**
     * @param program_file Source code file.
     * @throws FileNotFoundException If the file does not exist or cannot be read.
     */
    public Preprocessor(File program_file) throws FileNotFoundException {
        if (program_file.exists() && program_file.canRead()) {
            //TODO register file types and check if they correct
            this.src_code = program_file;
            this.inf_code = new File("C:\\Users\\srikr\\Desktop\\adsproject\\ADS-Project\\$instruction.txt");
        } else {
            throw new FileNotFoundException();
        }
    }

    //you cant use me if you don't give me a file.
    private Preprocessor() {
    }

    public static void main(String... args) throws Exception {
//        Terminal t = Terminal.evoke();
        Preprocessor c = new Preprocessor(new File("C:\\Users\\srikr\\Desktop\\adsproject\\ADS-Project\\$Input.txt"));
        File f = c.compile();
        System.out.println("Compilation finished. Beginning Execution.");
//        Executor.InstructionLoader("C:\\Users\\srikr\\Desktop\\adsproject\\ADS-Project\\$instruction.txt");
    }

    //converts an array of type T to a string that can be appended to the file
    //it uses the compilation tokenizer so that reading from the file can be simple.
    private static <T> String ARR_TO_STR(T[] arr) {
        StringBuilder sb = new StringBuilder();
        for (T element : arr) {
            sb.append(element.toString()).append(tknzr);
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

    //normalizes source code file by removing extra spaces and comments then storing everything into a StringBuilder object
    private void normalize(StringBuilder file) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(src_code));//read in file and store
        char prev_char = (char) bis.read();
        char c;
        while ((c = (char) bis.read()) != '\uFFFF') { //continue until EOF
            if (prev_char == comment_marker) { //remove comment marker
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

    //converts the source codes methods into a list of lists which contains only the most pertinent information
    //about a method such as the method body, parameters, name, and return signature.
    private void register_components(List<ArrayList<String>> methods, StringBuilder file) {
        StringTokenizer method_tokenizer = new StringTokenizer(file.toString(), mdelim); //Tokenize by method delimiter

        while (method_tokenizer.hasMoreTokens()) { //for every method
            String method, return_type, method_name, method_body; //important information
            String[] params;
            method = method_tokenizer.nextToken().strip(); // get the entire method
            System.out.println("Method: " + method);

            return_type = method.substring(0, method.indexOf(" ")).strip(); // get the return type (first token)
            System.out.println("Return_type: " + return_type);

            method_name = method.substring(method.indexOf(" "), method.indexOf("(")).strip(); //get the name (second token)
            System.out.println("Method name: " + method_name);

            method_body = method.substring(method.indexOf("{") + 1, method.lastIndexOf("}")); //get method body from first to last curly bracket
            params = method.substring(method.indexOf("(") + 1, method.indexOf(")")).split(pdelim); //get the parameters and split by parameter delimiter

            System.out.println("-----------------");
            methods.add(new ArrayList<>(Arrays.asList(method_name, Arrays.toString(params), return_type, method_body))); //stores method info into the list
        }
    }

    //streams the source code into each instruction
    private Queue<String> stream(String method_body) throws IOException {

        BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(method_body.getBytes(StandardCharsets.UTF_8)));
        Queue<String> list = new LinkedList<>(); //the list of instructions
        Stack<String> scopes = new Stack<>(); //keeps track of entering scopes

        char i;
        String scope;
        StringBuilder acc = new StringBuilder();

        while ((i = (char) bis.read()) != '\uFFFF') {
            switch (i) {
                case '{', ';' -> { //'{' operator indicates entering a scope, ';' operator indicates instruction end
                    if (acc.toString().strip().startsWith("if")) { //if condition scope
                        scope = "if";
                        scopes.push(scope);
                    } else if (acc.toString().strip().startsWith("else if")) { //else if
                        scope = "elif";
                        scopes.push(scope);
                    } else if (acc.toString().strip().startsWith("else")) { //else
                        scope = "else";
                        scopes.push(scope);
                    } else if (acc.toString().strip().endsWith(":")) { //a custom scope "<scope name> : {}", ":" operator indicates the name of scope
                        scope = acc.substring(0, acc.indexOf(":")).strip(); //uds: user defined scope
                        scopes.push(scope);
                        acc.delete(0, acc.length());
                        acc.append("uds ").append(scope);
                    }
                    list.add(acc.toString().strip()); //add the instruction
                    acc.delete(0, acc.length()); //clear the string
                }
                case '}' -> list.add("end" + tknzr + scopes.pop());  //indicate scope end instruction
                default -> acc.append(i); //add char to stringbuilder
            }
        }
        return list; //return streamed instruction
    }


    /**
     * Compiles the source code into language and syntax that the executor can more easily understand.
     *
     * @return Instruction File.
     * @throws IOException When instruction file cannot be created.
     */
    public File compile() throws IOException {
        StringBuilder file = new StringBuilder(); //store in a string
        normalize(file); //get rid of random bits of data that are insignificant

        //create the instruction file
        if (!this.inf_code.exists()) {
            if (!this.inf_code.createNewFile())
                throw new IOException("Instruction file creation failed.");
        } else {
            System.out.println("file exists at: " + this.inf_code.getAbsolutePath());
        }

        System.out.println("This is the normalized file: " + file);

        List<ArrayList<String>> methods = new ArrayList<>(); //stores most important parts of method (return type, method name, parameters, method body)
        register_components(methods, file); //store the components of the "file"  in the "methods" list


        System.out.println("\nUnwrapping methods...\n");
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(inf_code));
        //noteme the list is ordered: method_name, params, return type, method_body
        Queue<String> instructions = stream(methods.get(indexOfMain(methods)).get(3)); //get the index of the main method (starting point)

        int tab = 0; //controls tab length so that user can see going on easier
        System.out.println(instructions);
        while (instructions.size() > 0) { //pop instructions
            String[] lhs; //left hand side of instruction
            String rhs; //right hand side of instruction
            String opcode; //operation code of instruction -> indicates what operation to do
            StringBuilder instruction = new StringBuilder(instructions.remove()); //stores the entire instruction
            System.out.println("Instruction: " + instruction);

            for (int i = 0; i < tab; i++) { //gimme some tabs
                bos.write(9);
            }

            //each instruction is of form: opcode lhs rhs
            if (instruction.toString().startsWith("uds")) { //user defined scope todo add some functionality
                ++tab;
                opcode = instruction.substring(4);
                instruction.delete(0, instruction.length());
                instruction.append("entr").append(tknzr).append(opcode);
            }
            //if the instruction is indicating a conditional scope
            else if (instruction.toString().startsWith("if") || instruction.toString().startsWith("else if")) {
                ++tab; //increase tab
                opcode = (instruction.toString().startsWith("else if")) ? "elif" : "if"; //opcode
                rhs = instruction.substring(instruction.indexOf("(") + 1, instruction.indexOf(")")); //get the condition
                instruction.delete(0, instruction.length()); //clear the string
                instruction.append("entr").append(tknzr).append(opcode).append(tknzr).append(rhs); //add the instruction
            } else if (instruction.toString().startsWith("else")) {
                instruction.delete(0, instruction.length()); //delete instruction
                ++tab; //increase tab
                instruction.append("else"); //add instruction
            }
            //if the instruction is indicating an end of scope
            else if (instruction.toString().startsWith("end")) {
                --tab;//decrease tab size
            }
            //if instruction is a method call
            else if (instruction.toString().contains(".")) {
                opcode = "call";
                lhs = instruction.substring(instruction.indexOf("(") + 1, instruction.indexOf(")")).split(","); //get params
                rhs = instruction.substring(instruction.indexOf(".") + 1, instruction.indexOf("(")); //get method name
                String function = instruction.substring(0, instruction.indexOf(".")); //get the library
                instruction.replace(0, instruction.length(), opcode + tknzr + function + tknzr + rhs + tknzr + ARR_TO_STR(lhs)); //add instruction
            }
            //if the "=" operator is present then a value is being set or created to another
            else if (instruction.toString().contains("=")) {
                rhs = instruction.substring(instruction.indexOf("=") + 1).strip(); //get value to set to
                lhs = instruction.substring(0, instruction.indexOf("=")).strip().split("[ ]+"); //get value and properties
                instruction.delete(0, instruction.length()); //clear string
                //if lhs >= 2 then its probably creating a value
                //else its probably setting a value
                opcode = lhs.length >= 2 ? "mal" : "set";
                instruction.append(opcode).append(tknzr).append(ARR_TO_STR(lhs)).append(rhs); //add instruction
            }
            System.out.println("Instruction: " + instruction);
            bos.write(instruction.toString().getBytes()); //write the instruction in the proper format
            bos.write(10); //new line
        }

        bos.write("EXIT".getBytes()); //stop signal
        bos.flush();
        return this.inf_code; //return file
    }

    //finds where the main method is in the list of methods
    //simple o(n) search
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