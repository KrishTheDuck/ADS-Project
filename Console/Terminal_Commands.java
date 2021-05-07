package EOY_ADS_PROJECT.Console;

/**
 * Enumeration that describes all commands and descriptions allowed in the Console.
 *
 * @author Krish Sridhar, Kevin Wang
 * @since 1.0
 * Date: 3/2/2021
 */
public enum Terminal_Commands {
    help("Produces list of all commands."), run("Runs a program. Syntax: <run> <file_path>"), rnr("Recompile And Run: <run> <source_code_file_path>")
    ,exit("Exits terminal.");

    Terminal_Commands(String description) {
        this.description = description;
    }

    private final String description;

    public String description() {
        return this.description;
    }
}
