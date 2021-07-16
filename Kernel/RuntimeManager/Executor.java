package Kernel.RuntimeManager;

import FunctionLibrary.FLMapper;
import Kernel.Data_Structures.Node.AbstractNode;
import Kernel.Data_Structures.Node.CNode;
import Kernel.Data_Structures.OrderedPairList;
import LanguageExceptions.FailFastException;
import LanguageExceptions.FunctionNotFoundException;
import LanguageExceptions.LibraryNotFoundException;
import ParserHelper.UniversalParser;
import org.jetbrains.annotations.NotNull;
import org.nustaq.serialization.FSTObjectInput;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.rmi.UnexpectedException;
import java.util.Arrays;
import java.util.List;

/**
 * Executes instruction file.
 *
 * @author Krish Sridhar
 * @see Compiler
 * @since 1.0
 * Date: March 2021
 */

final class Executor {
    private static OrderedPairList<String, AbstractNode> library;
    private static int length;

    public static void ExecutionEngine(String line) throws LibraryNotFoundException, FunctionNotFoundException {
        String[] instructions = line.split(PreprocessorFlags.tknzr);
        System.out.println("Instruction: " + Arrays.toString(instructions));
        switch (instructions[0]) {
            case "mal" -> { //mal <properties> <name> <value> OR mal <properties> <name> call <library> <function_name> <args>
                //properties
                String[] properties = instructions[1].split(" ");
                String value = instructions[3].equals("call") ? UniversalParser.function_evaluate(instructions[4]) : UniversalParser.ReplaceWithValue(instructions[3].split(" "));
                RuntimePool.commit(instructions[2], properties[properties.length - 1], UniversalParser.evaluate(value), properties);
            }
            case "set" -> {
                instructions[1] = UniversalParser.ReplaceWithValue(instructions[1].split(" "));
                System.out.println(instructions[1]);
                RuntimePool.setValue(instructions[1], UniversalParser.evaluate(instructions[2]));
            }
            //library, function name, args
            case "call" -> {
                int i = PreprocessorFlags.call_delim.length();

                int count = 0;
                int k = 0;
                while (k < instructions[1].length() - i && count < 2) {
                    String chars = instructions[1].substring(k, k + i);
                    if (chars.equals(PreprocessorFlags.call_delim)) {
                        ++count;
                    }
                    k += i;
                }

                if (count > 1) {
                    UniversalParser.function_evaluate(instructions[1]);
                    break;
                }

                String[] broken = UniversalParser.break_call(instructions[1]);
                FLMapper.mapFunctionToExecution(broken[0], broken[1], broken[2]);
            }
            case "del" -> RuntimePool.delete(instructions[1]);
        }
        System.out.println("VM: " + RuntimePool.__RVM__);
    }

    public static void execute(RandomAccessFile f) throws Exception {
        f.seek(0);
        FSTObjectInput ois = new FSTObjectInput(new BufferedInputStream(new FileInputStream(f.getFD())));
        System.out.println("Serial: " + SharedData.MAIN_CODE);
        long _start, _end;
        _start = System.currentTimeMillis();
        for (int i = 0; i < length; i++) {
            AbstractNode ast = readObjectFromStream(ois);
            library.add(ast.serial(), ast);
        }
        _end = System.currentTimeMillis();
        System.out.println("Load time: " + (_end - _start));

        _start = System.currentTimeMillis();
        SharedData.library = library;
        search(library.getPairFromKey(SharedData.MAIN_CODE).value());
        _end = System.currentTimeMillis();
        System.out.println("Execution time: " + (_end - _start));
    }


    private static @NotNull AbstractNode readObjectFromStream(FSTObjectInput inputStream) throws Exception {
        Throwable error = null;
        try {
            int len = inputStream.readInt();
            if (len != 0) {
                byte[] buffer = new byte[len];
                while (len > 0) {
                    len -= inputStream.read(buffer, buffer.length - len, len);
                }
                return (AbstractNode) Compiler.fstc.getObjectInput(buffer).readObject(AbstractNode.class);
            }
        } catch (Exception e) {
            error = e.getCause();
        }
        throw new UnexpectedException("Reading AST Object code has lead to unexpected error..." + error);
    }

    @SuppressWarnings("unused")
    public static void search(AbstractNode current) throws FailFastException, LibraryNotFoundException, FunctionNotFoundException {
        final AbstractNode head = current;
        String line;
        System.out.println(current.code());
        SharedData.current_scope.push(head.serial());
        while (current != null && !(line = current.pop()).equals(PreprocessorFlags.EXIT)) { //keep iterating
//            System.out.println("CODE LEFT: " + current.code() + " current: " + line);
            String[] tokens = line.split(PreprocessorFlags.tknzr);
            switch (tokens[0]) {
                case "chk" -> {  //chk child
                    SharedData.current_scope.push(tokens[1]);
                    if ("if".equals(tokens[1]) || "elif".equals(tokens[1])) { //conditional
                        AbstractNode checking = current.find_first(tokens[1]);
                        if (checking == null) throw new FailFastException();
                        boolean enter = !UniversalParser.evaluate(UniversalParser.ReplaceWithValue(((CNode) checking).condition().split(" "))).equals("0.0");
                        if (enter) { //if true enter and !!!DELETE ALL CONSECUTIVE ELIF ELSE CHILDREN!!!
                            cascade_deletion(current, checking, head);
                            current = checking;
                        } else { //if false remove.
                            current.remove(checking);
                        }
                    } else { //enter always
                        current = current.find_first(tokens[1]); //child AbstractNode;
                    }
                }
                case "end" -> {//exit script
                    SharedData.current_scope.pop();
                    AbstractNode save = current;
                    current = current.parent();
                    current.remove(save);
                }
                //delete AbstractNode
                default -> {
                    long start, end;
                    start = System.currentTimeMillis();
                    ExecutionEngine(line);
                    end = System.currentTimeMillis();
                    System.out.println("\tTime: " + (end - start));
                }
            }
        }

    }

    private static void cascade_deletion(AbstractNode current, AbstractNode checking, AbstractNode
            head) {
        System.out.println("\tDELETING:");
        List<AbstractNode> children = current.children();
        for (int i = current.indexOf(checking) + 1; i < children.size(); i++) {
            AbstractNode child = children.get(i);
            if (child.name().equals("elif")) {
                System.out.println("Deleting: " + child + " -> " + child.code());
                current.remove(child);
                head.pop();
                continue;
            } else if (child.name().equals("else")) {
                System.out.println("Final delete: " + child + " -> " + child.code());
                current.remove(child);
                break;
            }
            break;
        }
    }

    public static void setLibrarySize(int size) {
        length = size;
        library = new OrderedPairList<>(length);
    }
}
