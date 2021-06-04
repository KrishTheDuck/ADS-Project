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
import java.util.Arrays;
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
                        ast.set_condition(tokens[2]); //set first condition to this
                    }
                    case "else" -> {
                        ast.add_and_enter(tokens[1], true); //add scope name
                        ast.load("else");
                    }
                    default -> ast.add_and_enter(tokens[1], false); //not a condition just a custom scope
                }
                //if encounter "end" return to parent
            } else if (line.startsWith("end")) { //ending a scope
                String token = line.substring(3 + Preprocessor.tknzr.length() - 1);
                System.out.println("\tEnding scope: " + ast.current());
                ast.load("end");
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
        //!!!GOTTA PUSH THE EXIT CALL!!!
        ast.load("EXIT");
        System.out.println("Main code: " + ast.code());
        //checking each child:
        Node current = ast.current();
        Queue<String> mainLoop = new LinkedList<>();

        while (!(line = current.pop()).equals("EXIT")) { //keep iterating
            String[] tokens = line.split(Preprocessor.tknzr);
            System.out.println("\tTOKENS: " + Arrays.toString(tokens));
            if (tokens[0].equals("chk")) {
                if ("if".equals(tokens[1]) || "elif".equals(tokens[1])) { //conditional
                    Node checking = current.find(tokens[1]);
                    boolean enter = !UniversalParser.evaluate(checking.condition()).equals("0");
                    System.out.println(enter + " should enter: " + checking);
                    if (enter) { //if true enter and !!!DELETE ALL CONSECUTIVE ELIF ELSE CHILDREN!!!
                        for (Node n : current.children()) { //delete consecutive elif and else chains
                            if (n.name().equals("elif")) {
                                current.delete(n);
                                continue;
                            } else if (n.name().equals("else")) {
                                current.delete(n);
                                break;
                            }
                            break;
                        }
                        current = checking;
                    } else { //if false remove.
                        current.delete(checking);
                    }
                }
                //noteme because we delete all chains on "true" if we find an "else" block we have to execute it
                else { //enter always
                    current = current.find(tokens[1]); //child node;
                }
            } else if ("end".equals(tokens[0])) { //exit script
                //delete node
                Node save = current;
                current = current.parent();
                current.delete(save);
            } else {
                mainLoop.add(line);
            }
        }
        System.out.println(mainLoop);
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
