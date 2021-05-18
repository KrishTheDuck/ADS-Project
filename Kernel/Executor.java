package Kernel;

import Kernel.RuntimeManipulation.RuntimeVariableManipulation;
import LanguageExceptions.AlreadyCommittedException;
import LanguageExceptions.NonExistentVariableException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Executes instruction file.
 *
 * @author Krish Sridhar, Kevin Wang
 * @see Kernel.Compiler
 * @since 1.0
 */
//TODO one day. not today. not this year. but one day. we need to implement a branch prediction algorithm. This will be the hardest fucking thing you've ever done.
//  Pattern p = Pattern.compile("[a-zA-Z0-9.]+");
//  Matcher m = p.matcher(expression);

public final class Executor {
    private static RuntimeVariableManipulation rvm;

    public static void main(String[] args) {

    }

    public static void InstructionLoader(String path) throws IOException {
        InstructionLoader(new File(path));
    }

    public static void InstructionLoader(File f) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(f));
        AbstractSyntaxTree ast = new AbstractSyntaxTree("start");


        String line;
        while ((line = br.readLine()) != null) {
            line = line.strip();
            if (line.startsWith("chk") || line.startsWith("echk") || line.startsWith("rslv")) {
                ast.add_and_enter(line);
            } else if (line.startsWith("end")) {
                ast.move_back();
                ast.load("cc ");
            } else {
                ast.load(line);
            }
        }
        ast.print();
    }

    public static void ExecutionEngine(String... lines) throws AlreadyCommittedException, NonExistentVariableException {
        for (String line : lines) {
            String[] instructions = line.split(Compiler.tknzr);
            switch (instructions[0]) {
                //properties, datatype, value
                case "mal" -> rvm.commit(instructions[2], null, instructions[1], instructions[3]);
                //property, name, value
                case "set" -> rvm.set("value", instructions[1], instructions[2]);
            }
        }
    }

    private static boolean eval_cond(String bool) {
        return true;
    }

}
