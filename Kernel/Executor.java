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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Executes instruction file.
 *
 * @author Krish Sridhar, Kevin Wang
 * @see Preprocessor
 * @since 1.0
 * Date: March 2021
 */

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
        AbstractSyntaxTree ast = new AbstractSyntaxTree("start", false);

        //create an execution path first before computing

        //start from beginning of file
        String line;
        while (!(line = br.readLine()).equals("EXIT")) {
            line = line.strip(); //clear tabs
            System.out.println("Considering: " + line);
            //if encounter "entr" create a node
            if (line.startsWith("entr")) {
                String[] tokens = line.split(Preprocessor.tknzr);
                System.out.println("\tEntering: " + tokens[1]);
                //load the instruction as the first line of code.
                switch (tokens[1]) {
                    case "if", "elif" -> {
                        System.out.println("\tConditional: " + tokens[2]);
                        ast.add_and_enter(tokens[1], true); //add scope name
                        ast.load(tokens[2]); //set first condition to this
                    }
                    case "else" -> {
                        ast.add_and_enter(tokens[1], true); //add scope name
                        ast.load("rslv"); //set first condition to 'rslv' which should tell the execution path line to resolve to this condition if all else fails
                    }
                    default -> ast.add_and_enter(tokens[1], false); //not a condition just a custom scope
                }
                //if encounter "end" return to parent
            } else if (line.startsWith("end")) { //ending a scope
                String token = line.substring(3 + Preprocessor.tknzr.length() - 1);
                System.out.println("\tEnding scope: " + ast.current());
                ast.move_back();
                ast.load("chk " + token); //says to search for the next token and check for execution
            } else {
                System.out.println("\tLoading instruction: " + line);
                ast.load(line);
            }
        }

        System.out.println("Starting execution...");
        //time for recursive search
        //first i return to the first node
        ast.head();
        //i need to iterate over the main node and recursively check children when i need to.
        System.out.println("main branch: " + ast.head().code());
        List<String> procedure = new ArrayList<>(15); //noteme if this starts working delete procedure and start doing execution on command
        while (ast.code().size() > 0) {
            String statement = ast.gci(); //get next instruction
            String[] tokens = statement.split(Preprocessor.tknzr); //get all the tokens
            System.out.println("Checking: " + Arrays.toString(tokens));
            switch (tokens[0]) { //opcodes baby
                //memory allocate for var
                case "mal", "set" -> {
                    //dont do any instructins nows
                    procedure.add(statement);
                    //consume the instruction and look at next
                    ast.consume_next();
                }
                //check child !!!RECURSION TIME!!!
                case "chk" -> {
                    RDS(ast, procedure);
                }
            }
        }
        System.out.println("procedure: " + procedure);
    }


    //rds or recursive directed search is a recursive algorithm designed to follow the execution path line of the ast and return a result
    private static void RDS(AbstractSyntaxTree ast, List<String> q) throws Exception {
        //first i have to get the current instruction
        String[] tokens = ast.consume_next().split(Preprocessor.tknzr); //if its truly a chk command it should be "chk  <node name>"
        if (!ast.enter(tokens[1])) { //enter this child
            throw new Exception("Nonexistent child: " + tokens[1]);
        }
        System.out.println("Statement: " + Arrays.toString(tokens));
        Node n = ast.current();
        if (n.isCondition()) { //if its a condition evaluate
            //if a forget about it
            boolean condition = !UniversalParser.evaluate(ast.consume_next()).equals("0");
            if (condition) //if condition is true load instructions
            {
                while (ast.code().size() > 0) {
                    String s = ast.consume_next();
                    if (s.startsWith("entr")) //recur
                        RDS(ast, q);
                    q.add(s);
                }
                //if the condition is true i don't want to evaluate any consecutive elif or else
                ast.move_back_and_delete_branch();
                for (Node child : ast.current().children()) { //for every child
                    if (child.name().equals("elif")) { //if its elif delete
                        ast.delete(child);
                        continue; //go to next
                    } else if (child.name().equals("else")) { //if i get na else that's the end of the chain don't eval more
                        ast.delete(child); //delete
                        break; //no more deletion
                    }
                    break; //not an elif or else chain already done don't iterate anymore.
                }
            }  //condition is false delete branch and return
            ast.move_back_and_delete_branch();
        }
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
}
