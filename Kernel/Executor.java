package Kernel;

import Kernel.Data_Structures.AbstractSyntaxTree;
import Kernel.Data_Structures.Node;
import Kernel.RuntimeManipulation.RuntimeVariableManipulation;
import LanguageExceptions.AlreadyCommittedException;
import LanguageExceptions.NonExistentVariableException;
import ParserHelper.UniversalParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Executes instruction file.
 *
 * @author Krish Sridhar, Kevin Wang
 * @see Preprocessor
 * @since 1.0
 * Date: March 2021
 */
//TODO one day. not today. not this year. but one day. we need to implement a branch prediction algorithm. This will be the hardest fucking thing you've ever done.
//  Pattern p = Pattern.compile("[a-zA-Z0-9.]+");
//  Matcher m = p.matcher(expression);

public final class Executor {
    private static RuntimeVariableManipulation rvm;

    public static void main(String[] args) throws Exception {
        InstructionLoader("C:\\Users\\srikr\\Desktop\\adsproject\\ADS-Project\\$instruction.txt");
    }

    public static void InstructionLoader(String path) throws Exception {
        InstructionLoader(new File(path));
    }

    public static void InstructionLoader(File f) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(f));
        AbstractSyntaxTree ast = new AbstractSyntaxTree("start");

        //start loading
        String line;
        String scope = "start";
        while ((line = br.readLine()) != null) {
            line = line.strip();
            if (line.startsWith("chk") || line.startsWith("echk") || line.startsWith("rslv")) {
                ast.add_and_enter(line);
                scope = (line.startsWith("rslv")) ? "rslv" : line.substring(0, line.indexOf(Preprocessor.tknzr));
            } else if (line.startsWith("end")) {
                ast.move_back();
                switch (scope) {
                    case "else", "elif" -> ast.load("cnc"); //check next child
                    case "if" -> ast.load("cc"); //check first child
                    case "start" -> {
                    }
                    default -> throw new IllegalStateException("Unexpected scope: " + scope);
                }
                ast.load("cc");
            } else {
                ast.load(line);
            }
        }
        //begin execution
        execute(ast);
    }

    public static void ExecutionEngine(String... lines) throws AlreadyCommittedException, NonExistentVariableException {
        for (String line : lines) {
            String[] instructions = line.split(Preprocessor.tknzr);
            switch (instructions[0]) {
                //properties, datatype, value
                case "mal" -> rvm.commit(instructions[2], null, instructions[1], instructions[3]);
                //property, name, value
                case "set" -> rvm.set("value", instructions[1], instructions[2]);
            }
        }
    }

    private static void execute(AbstractSyntaxTree ast) {
        Node curr = ast.head();
        Queue<String> qin = new LinkedList<>();

        String code;
        int child = 0;
        System.out.println("Current Node: " + curr);
        while (!(code = curr.pop()).equals("EXIT")) {
            System.out.print("\t");
            if (code.equals("cc")) {
                curr = ast.enter(child++);
                System.out.println("Checking child: " + curr + " and evaluating condition: " + curr.instruction()); //todo else if statements need peeking; use iteration to check over the children and evaluate
                String[] condition = curr.instruction().split(Preprocessor.tknzr);
                String cond_opcode = condition[0];
                String bool = condition[1];
                String[] statement = bool.split(" ");
                for (String token : statement) { //todo replace vars

                }
                if (UniversalParser.evaluate(curr.instruction().substring(curr.instruction().indexOf(Preprocessor.tknzr))).equals("0")) { //if the if-statement is false remove it
                    System.out.println("Condition evaluated to 0, deleting...");
                    curr = ast.move_back_and_delete_branch();
                    System.out.println("Current Node: " + curr);
                }
            } else if (code.startsWith("end")) {
                System.out.println("Branch ended. " + curr);
                curr = ast.move_back_and_delete_branch(); //todo when you add iteration make sure "delete branch" is changed until its ascertained that it won't run
                System.out.println("Current Node: " + curr);
            } else {
                qin.add(code);
                System.out.println("Added to queue: " + code);
            }
        }

        System.out.println(qin);
    }

    private static String eval_cond(String bool) {
        return UniversalParser.evaluate(bool);
    }

}
