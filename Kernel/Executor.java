package EOY_ADS_PROJECT.Compiler.Kernel;


import EOY_ADS_PROJECT.Compiler.ParserHelper.*;
import EOY_ADS_PROJECT.FunctionLibrary.FLibMapper;
import EOY_ADS_PROJECT.LanguageExceptions.NonExistentVariableException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Executes instruction file.
 *
 * @author Krish Sridhar, Kevin Wang
 * @see EOY_ADS_PROJECT.Compiler
 * @since 1.0
 */
//TODO one day. not today. not this year. but one day. we need to implement a branch prediction algorithm. This will be the hardest fucking thing you've ever done.
@SuppressWarnings("switch")
public final class Executor {
    public static void main(String[] args) throws IOException {

    }

    //TODO assert authenticity (file is proper format or whatever)

    private static boolean assert_authenticity(File f) {
        return true;
    }

    public static void execute(File i_file) throws Exception {
        System.out.println("Starting Execution!\n\n");
        RuntimeVariableManipulation rvm = new RuntimeVariableManipulation();

        if (!assert_authenticity(i_file))
            throw new IOException("File " + i_file.getName() + " at " + i_file.getAbsolutePath() + " is not an authentic .(whatever) file.");

        BufferedReader br = new BufferedReader(new FileReader(i_file));

        //start reading
        String instruction;
        while (!(instruction = br.readLine().strip()).equals("END")) {
            String[] terms = instruction.split(Compiler.op_t);
            String opcode = terms[0]; //instruction
            System.out.println("Instruction: " + Arrays.toString(terms));
            switch (opcode) {
                case "COMPILE" -> {
                    String data_structure = terms[1];
                    switch (data_structure) {
                        case "STRUCT" -> {
                            System.out.println("Data structure: STRUCT -> " + Arrays.toString(terms));
                            String[] params = new String[terms.length - 1];
                            //NOTEME if any other special property is added make sure you find it and place it in index 0 params
                            params[0] = "null";

                            if (terms.length - 3 >= 0) System.arraycopy(terms, 3, params, 2, terms.length - 3);

                            rvm.commit_SL(terms[2], params);
                        }
                        case "QUEUE", "STACK" -> {

                        }
                        default -> throw new UnsupportedOperationException("Instantiation of struct type " + data_structure + " is unsupported.");
                    }
                }
                //SET <varname> TO EXECUTE/METHOD <expression>
                case "SET" -> {// sets a value
                    String varname = terms[1];
                    String value = terms[terms.length - 1];
                    rvm.setValue(varname, EvaluateExpression(value, rvm.getDatatype(varname), rvm));
                }
                //CREATE <datatype> <varname> TO EXECUTE/METHOD <expression>
                case "CREATE" -> { //attempts to create a value in a scope
                    String datatype = terms[1];
                    String varname = terms[2];
                    String value = terms[terms.length - 1];
                    if (datatype.contains("::")) {
                        System.out.println("Data structure time!");
                        String ds = datatype.substring(0, datatype.indexOf("::"));
                        System.out.println("Variable " + varname + " is of the " + ds + " data structure");
                    } else {
                        rvm.commit(varname, "null", datatype, EvaluateExpression(value, datatype, rvm));
                    }
                }
                //FROMLIB <library> <function> <args>
                case "FROMLIB" -> {
                    System.out.println("Term: " + Arrays.toString(terms));
                    String lib = terms[1];
                    String function_name = terms[2];
                    System.out.println(terms.length);
                    String[] args = terms[3].substring(1, terms[3].length() - 1).split(",");

                    System.out.println("Args: " + Arrays.toString(args));
                    Object value = FLibMapper.mapFunctionToExecution(lib, function_name, args);
                }
                //TODO FIXME NOTEME idfk lmao
                case "CALL" -> {

                }

                default -> throw new UnsupportedOperationException("Operation " + opcode + " is unsupported.");
            }
            System.out.println("Current Stack: " + rvm);
        }

        System.out.println("Execution Finished!");
        System.out.println("Instruction stack: " + rvm);
    }

    private static String EvaluateExpression(String expression, String datatype, RuntimeVariableManipulation rvm) throws NonExistentVariableException {
        System.out.println("Expression detected: " + expression);
        Parser eval;
        //switch vars with values and update accordingly
        Pattern p = Pattern.compile("[a-zA-Z0-9.]+");
        Matcher m = p.matcher(expression);

        while (m.find()) {
            //if its just an integer don't bother
            String var = m.group().strip();
            int index = expression.indexOf(var);
            if (IntegerParser.isInteger(var)) continue;

            System.out.println("Variable detected: " + var);
            //just check if we have postfix or prefix incrementer
            if (index > 2 && index < expression.length() - 2) {
                String op$ = expression.charAt(index - 1) + "" + expression.charAt(index - 2);//operator before
                switch (op$) {//increment then use
                    case "++" -> {
                        rvm.setValue(var, parseFrom(datatype, rvm.getValue(var), 1));
                        expression = expression.replace(op$ + "" + var, rvm.getValue(var));
                    }
                    case "--" -> {
                        rvm.setValue(var, parseFrom(datatype, rvm.getValue(var), -1));
                        expression = expression.replace(op$ + "" + var, rvm.getValue(var));
                    }
                    default -> { //use then increment
                        op$ = expression.charAt(index + 1) + "" + expression.charAt(index + 2);
                        switch (op$) {
                            case "++" -> {
                                expression = expression.replace(var + "" + op$, rvm.getValue(var));
                                rvm.setValue(var, String.valueOf(Integer.parseInt(rvm.getValue(var)) + 1));
                            }
                            case "--" -> {
                                expression = expression.replace(var + "" + op$, rvm.getValue(var));
                                rvm.setValue(var, parseFrom(datatype, rvm.getValue(var), -1));
                            }
                            default -> expression = expression.replace(var, rvm.getValue(var)); //no incrementer
                        }
                    }
                }
            } else {
                expression = expression.replace(var, rvm.getValue(var));
            }
        }

        eval = switch (datatype) {
            case "int" -> new IntegerParser();
            case "long" -> new LongParser();
            case "short" -> new ShortParser();
            case "double" -> new DoubleParser();
            case "float" -> new FloatParser();
            case "bool" -> new BooleanParser();
            case "string" -> new StringParser();
            default -> throw new IllegalArgumentException("Datatype \"" + datatype + "\" is unsupported.");
        };

        return Parser.EVALUATE(expression, eval);
    }

    //FIXME ++ -- should not work on boolean???
    private static String parseFrom(String datatype, String num, int addition) {
        return switch (datatype) {
            case "int" -> String.valueOf(Integer.parseInt(num) + addition);
            case "long" -> String.valueOf(Long.parseLong(num) + addition);
            case "short" -> String.valueOf(Short.parseShort(num) + addition);
            case "double" -> String.valueOf(Double.parseDouble(num) + addition);
            case "float" -> String.valueOf(Float.parseFloat(num) + addition);
            case "boolean" -> (num.equalsIgnoreCase("true") || num.equals("yes") || num.equals("1")) ? "true" : (num.equalsIgnoreCase("false") || num.equals("no") || num.equals("0")) ? "false" : num;
            default -> throw new UnsupportedOperationException("Datatype \"" + datatype + "\" is not natively supported.");
        };
    }


}
