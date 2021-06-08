package Console;

public enum Terminal_Commands {
    rnr("Recompile and Rerun. Will accept a file path for the source code, recompile the code, and re-execute."),
    run("Will run a given instruction file."),
    exit("Exits the Terminal.");
    private final String description;

    Terminal_Commands(String description) {
        this.description = description;
    }

    public String description() {
        return this.description;
    }
}
