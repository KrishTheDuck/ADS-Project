package EOY_ADS_PROJECT;

import java.io.IOException;

//0.003 seconds
//13 MB average
public class Main {
    public static void main(String[] args) throws IOException {
//        String line = Parser.EVALUATE("(~3+~2+~1)+(~2)*3*2-3", new IntegerParser());
        String expression = "(~3+~2+~1)-(~2)*3*2+-3";
        System.out.println(expression.replaceAll("(?<!\\+)-", "+-"));
//        System.out.println(line);
//        String del = "\n";
//        System.out.println("delimiter: " + del);
    }
}
/*This shit is an entire YEAR LONG project so all the requirements and things we have to do are going to be pretty big. If
 * we can knock out the basic functionality within 2 or 3 weeks we can add features later.*/

// [---- PLANNING/QUESTIONS: ----]

// -> read in input
//      -> what will syntax be like?
//          -> will it reflect c syntax?
// -> establish order of execution
//      -> maybe a linking file that writes all the steps of execution compiled and run that
// -> execution could be by stack
// -> print output into a file
//      -> output could be thrown into a gui or a custom terminal type system
// -> if program seeks user input then we have to create a terminal
// -> compiling could occur by using commands in the terminal such as "run <filename>"
// we can use background processes to assert that the file is the right type of executable
// or we could create our own special file type and run it by double clicking.

// [---- PROGRAM STEPS/IDEAS: ----]
/* (1) Read in file -> (2) convert into set of steps -> (3) convert into instructions (maybe machine or bytecode) -> (4) run*/

// (1) Reading inputs: (Preprocessing 1)
/*We first need to determine syntax. Afterwards we need to determine syntax correction.*/

// (2) Converting into steps (linking):
/*We read the main function and look through each step. Iteration probably will be expanded in the stack as multiple operations. We can make
 * our own file type or something here that can be run through another program we make and outputted.*/

// (3 optional) Converting into existing procedural language (assembly)
/* after converting into a set of succinct we could work towards converting those instructions into assembly or some other procedural lang
so that we can make execute it without creating a kernel. If not, we can create our own file type that leads to program.*/

// (4) Running code (execution)
/* after conversion into some other procedural language (if we're doing that at all) we have to run the code. If converting into assembly
 * we can just execute the assembly file that outputs from step 3. If not, we have to create a special executing program that runs the instructions.
 * If we decide to create a special file extension for the steps that we create from step 2, we can hopefully just double click on the file and Windows
 * would lead us right to the execution program. If not, we could create a custom GUI Terminal where we input a file path and the background process would
 * take care of everything else. Output would happen in-terminal.*/

// [---- PROGRAM FEATURES: ----]
/*We need some features to possible add to this language so that it actually works well. Some features are a REQUIREMENT for the language to be good.
 * These requirements are either functions or some symbol or whatever that implements them. Other OPTIONAL features could be through implementation
 * or special datatypes or something like that.*/

// REQUIREMENT: built-in callable functions
/*The user needs some functions that can be used to aid them in program development. Some necessary functions might be a "console in"
 * and "console out" function. We could allow the user to read files through "file readers". For this we can create a custom library of
 * some sort with just simple functions. */

// REQUIREMENT: datatypes and keywords
/*We need to be able to map values to datatypes so that we can assert that the user doesn't do unintended operations and try putting a String in an int operation.
 * We also need datatypes and keywords to give special properties to variables.*/

//REQUIREMENT: operations and operators
/*This is probably the bare minimum we need for the language to be functional (literally and metaphorically). NO MATTER WHAT, this is most definitely the
 * first thing we implement, if not, one of the most important.
 *
 * Operators:
 *  +, -, *, ^, / , &, &&, |, ||, %, !, .  ... TBD

 *
 * To make it easier on ourselves we can just make all these operators applicable to every datatype so we can do less work on preprocessing and
 * syntax correction. These are all mathematical and bitwise operators but we could probably figure some use for them in other datatypes. If not, preprocessing
 * might be difficult since we'd have to check if both variables/values on either side of the binary operators are correct before we move forward. */

// FUNCTIONS: I/O
/*In case we give input and output functions we need a terminal so that we can actually do that shit. We need to tell the executing program to idle the cpu.
 * until we notify it to start again. Maybe, if we're ambitious enough, we can include multi-threading implicitly.*/

// [---- IMPLEMENTATION NOTES: ----]
/* Implementing some of the harder parts of this project might require usage of JSON and some tricks.*/

// FETCHING AND STORING VARIABLES
/*Variables and their values need to be stored somewhere. One idea is through a TreeMap which is a HashMap that is sorted. That way we could dynamically
 * control memory. With a JSON we could store values as a string like a python dictionary and read from the json file with class that controls allocating
 * and deallocating memory. However, we might not be able to save the datatype in JSON so we would have to do some parsing (additional work). However,
 * this would probably decrease the amount of memory the language uses during runtime.*/

// STORING INSTRUCTIONS
/*Instructions can be stored in a text file so that it's easier to read and pop from. We could also use a stack or queue data structure to order the operations
 * so that we can just execute and pop as we go. A data structure like that would probably just require parsing from the string instructions inside each Node
 * or whatever doesn't really matter. Specific stack implementation is not that difficult. Using a file would probably be cooler though; if we could build a
 * special file extension to run that. */

// TERMINAL
/* Simple GUI with a text-box for instructions and an output pane for outputting errors or program outputs. Not that hard not that difficult. */

// [---- SYNTAX: ----]
/* Iteration (for, while, do-while)
 * Display/Output
 * Input
 * Functions/Calling
 * Labels
 * Datatypes -> Integer, Short, Long, Double, Boolean, String, Character, Arrays, Lists, Queue, Stack, Map, etc.
 * Operators -> +, -, *, ^, / , &, &&, |, ||, %, !, '.', ',', ... TBD
 */

// [---- LIBRARY ----]
/*In order for the language to be properly usable and effective we need a library of executable functions.
 *
 * QuickMath: quicker algorithms and easier to access math library to substitute at run time
 *
 * */

// [---- LANGUAGE DESIGN ----]
/* What the language contains in terms of structures, methods, datatypes, classes, etc.
 *
 * METHODS:
 * <return_type> <method name>(<parameters>)
 * {
 * }
 * MUST HAVE A MAIN METHOD!!!:
 * int main()
 * {
 *
 * }
 *
 * VARIABLES (? - optional, $ - required):
 * <?properties> <$datatype> <$variable name> = <?init value>;
 * <?properties> <$datatype> <$variable name>;
 *
 * STRUCTURES (data aggregates):
 *
 * struct <structure name>
 * {
 * <variable>
 * <variable>
 * <variable>
 *    ...
 * }
 * >>> treat <structure name> as a <datatype>:
 * >>> access datatypes in structure through '.' operator
 * >>> variables utilizing the structure type will copy the structure
 * >>> json data organization is a vital facet for facilitating efficient data manipulation
 *
 * */
// [---- FILE STRUCTURE ----]
/* What files does a normal executable have?
 *
 * - A JSON controlling variables during execution
 * - An instruction file containing procedural instructions
 * -? A method file containing all methods and order of execution?
 * - Normal source file
 *
 * */
// [---- PROGRAM EXECUTION PATH, DETAILED ----]
/* // "?" indicates optional
 * 0.5?) Once user is done coding on whatever they can click "run" or double click on the file to start the GUI with the file path or something;
 * 1) Path is given to .alpha file
 * 2) Verification of file authenticity (is it actually a .alpha file?)
 * 2.5?) Storing the file in an extraneous cache-like file to make verification quicker
 * 3) conversion of file into a standardized form -> moving brackets, semi-colons, and statements to their respective lines
 * 4) Syntax verification of file. (Mark all methods and store instructions or run instructions at runtime?)
 * 5) if syntax is incorrect, throw exception to terminal at line number and return to step 1, else continue to compilation
 * 6) Create instruction file .txt or .ifa (instruction file alpha)
 * 7) Create a JSON for storing, destroying, and manipulating variables
 * 7.5?) Create a package for storing normal source file, instruction file, variable json, method instruction
 * 8) compile all structures first: store variable names and values in JSON
 * 9) compile methods into instruction sets for quick calling
 * 10) enter main method, run through order of execution, compile instructions and variable initialization.
 * 11) when exiting scopes write "destroy" for all local variables
 * 12) if a method is called, either recall the method from the method file or jump to declaration line
 * 13) continue compiling instructions.
 * 14) once value is returned or end of method is found return to calling method and continue execution
 * 15) if an iteration is reached either make an array of instructions to repeatedly process during runtime or unwrap the entire array.
 * 15) once execution reaches end of main method save the instruction file and pass it for execution.
 * 16) run through instruction file and parse instructions.
 * 17) if print statement is reached print to terminal
 * 18) if iteration loop is unwrapped nothing needs to be done, if it's stored as an array of instructions find proper for loop and iterate
 * 19) if datatype is updated update value in json, if it is destroyed delete entry in json, if it is casted changed datatype
 * 20?) once end of instruction list is reached, delete the instruction list and json list depending on config file?
 *
 * */