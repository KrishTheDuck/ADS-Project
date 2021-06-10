package RuntimeManager;

import FunctionLibrary.FLMapper;
import Kernel.Data_Structures.AbstractSyntaxTree;
import Kernel.Data_Structures.Node;
import LanguageExceptions.FunctionNotFoundException;
import LanguageExceptions.LibraryNotFoundException;
import ParserHelper.UniversalParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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

final class Executor {
    //todo implement set to method
    public static void ExecutionEngine(String line) throws FunctionNotFoundException, LibraryNotFoundException {
        String[] instructions = line.split(Preprocessor.tknzr);
        System.out.println("Instruction: " + Arrays.toString(instructions));
        switch (instructions[0]) {
            case "mal" -> { //mal <properties> <name> <value> OR mal <properties> <name> call <library> <function_name> <args>
                //properties
                String[] properties = instructions[1].split(" ");
                String value;
                if (instructions[3].equals("call")) {
                    value = FLMapper.mapFunctionToExecution(instructions[4], instructions[5], (instructions.length == 7) ? instructions[6] : "").toString();
                } else {
                    value = ReplaceWithValue(instructions[3].split(" "));
                }
                RuntimePool.commit(instructions[2], properties[properties.length - 1], UniversalParser.evaluate(value), properties);
            }
            case "set" -> {
                instructions[2] = ReplaceWithValue(instructions[2].split(" "));
                RuntimePool.setValue(instructions[1], UniversalParser.evaluate(instructions[2]));
            }
            //library, function name, args
            case "call" -> {
                FLMapper.mapFunctionToExecution(instructions[1], instructions[2], instructions.length == 3 ? "" : instructions[3]);
            }
        }
        System.out.println(RuntimePool.__RVM__);
    }

    public static String ReplaceWithValue(String... tokens) {
        for (int i = 0, tokensLength = tokens.length; i < tokensLength; i++) {
            if (tokens[i].matches("[a-zA-Z]+[0-9]*")) {
                System.out.println(tokens[i]);
                tokens[i] = RuntimePool.value(tokens[i]);
            }
        }
        return String.join(" ", tokens);
    }


    public static void execute(File inf_file) throws Exception {
//        rvm = new RuntimeVariableManipulation();
        BufferedReader br = new BufferedReader(new FileReader(inf_file));
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
                        System.out.println("\tConditional: else");
                        ast.add_and_enter(tokens[1], true); //add scope name
                        ast.set_condition("1");
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

        System.out.println("\nStarting execution...\n");
        //time for recursive search
        //first i return to the first node
        ast.head();
        //!!!GOTTA PUSH THE EXIT CALL!!!
        ast.load("EXIT");
        System.out.println("Main code: " + ast.code());
        //checking each child:
        Node current = ast.current();
        final Node head = current;
        while (current != null && !(line = current.pop()).equals("EXIT")) { //keep iterating
            System.out.println("CODE LEFT: " + current.code()); //todo tokens and code left is not synced

            String[] tokens = line.split(Preprocessor.tknzr);
            System.out.println("\tTOKENS: " + Arrays.toString(tokens));
            if (tokens[0].equals("chk")) { //chk child
                if ("if".equals(tokens[1]) || "elif".equals(tokens[1])) { //conditional
                    Node checking = current.find(tokens[1]);
                    System.out.println("Checking condition: " + checking.condition());
                    boolean enter = !UniversalParser.evaluate(ReplaceWithValue(checking.condition().split(" "))).equals("0");
                    System.out.println(enter + " should enter: " + checking);
                    if (enter) { //if true enter and !!!DELETE ALL CONSECUTIVE ELIF ELSE CHILDREN!!!
                        List<Node> children = current.children();
                        System.out.println("Start from index: " + current.indexOf(checking) + 1);
                        for (int i = current.indexOf(checking) + 1, childrenSize = children.size(); i < childrenSize; i++) {
                            Node child = children.get(i);
                            if (child.name().equals("elif")) {
                                System.out.println("Deleting: " + child + " -> " + child.code());
                                current.delete(child);
                                head.pop();
                                continue;
                            } else if (child.name().equals("else")) {
                                System.out.println("Final delete: " + child + " -> " + child.code());
                                current.delete(child);
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
                ExecutionEngine(line);
            }
        }
    }
}
