package Console;

import Kernel.Compiler;
import Kernel.Executor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.OutputStream;


/**
 * Outlines console properties such as color, size, execution, parsing, and I/O.
 * <p>
 * TODO native buffered input stream?
 * </p>
 *
 * @author Krish Sridhar, Kevin Wang
 * @see Console.Terminal_Commands
 * @see Compiler
 * @since 1.0
 * Date: 3/2/2021
 */
public final class Terminal extends JFrame {
    private JPanel contentPane;
    private JTextField command_input;
    private JTextArea command_output;
    private static BufferedOutputStream nbos; //native buffered output stream

    //set up how the GUI behaves and looks
    public Terminal() {
        setContentPane(contentPane); //little to no idea what this does
        setPreferredSize(new Dimension(800, 450)); //create a gui with 800x450
        command_output.setEditable(false); //you cant edit the output pane

        //set the font to the classic consolas in output field and input field
        command_output.setFont(new Font("Consolas", Font.PLAIN, 14));
        command_input.setFont(new Font("Consolas", Font.PLAIN, 14));

        //make all of it dark
        command_input.setBackground(Color.BLACK);
        command_output.setBackground(Color.BLACK);
        getContentPane().setBackground(Color.BLACK);

        //set the font color to white
        command_input.setForeground(Color.WHITE);
        command_output.setForeground(Color.WHITE);

        //"enter" means you accept input
        command_input.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String command = command_input.getText();

                    print(true, command);
                    command_input.setText("");
                    command = (parse(command.split(" ")));
                    print(false, command);
                }
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public static String readInput(Terminal t) {
        final String[] input = new String[1];
        t.command_input.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    input[0] = t.command_input.getText();
                }
            }
        });
        return input[0];
    }

    //throw away everything and stop the GUI

    private void onCancel() {
        try {
            nbos.close();
        } catch (Exception ignored) {
        }

        dispose();
    }
    //parse the entered commands
    // '!' are the symbols indicating commands are present

    private String parse(String[] args) {
        char operator = args[0].charAt(0); //stores operator
        String cmd = args[0].substring(1); //stores issued command
        System.out.println(cmd);
        if (operator == '!') {
            return switch (cmd) {
                case "help" -> print_commands(); //print all commands
                case "rnr" -> execute(args[1], false); //run a program by accepting a file path
                case "run" -> execute(args[1], true);
                case "exit" -> exitAndReturn();
                default -> "Error: \"" + cmd + "\" is an unsupported command"; //jic user thinks he's funny
            };
        } else {
            return "Error: Operator \"" + operator + "\" is an unsupported operator";
        }
    }

    private String exitAndReturn() {
        System.exit(0);
        return "Exiting";
    }

    //works in tandem with parse to print out to the console when the file exists
    //will eventually hand over operations to the Compiler class which will check for syntax

    private String execute(String path, boolean shouldReCompile) {
        //!run <file_path>
        File file = new File(path);
        print(false, "Executing....");
        if (file.exists()) {//if the file doesn't even exist don't bother
            try {
                if (shouldReCompile) {
                    Compiler c = new Compiler(file);
                    File instruction_file = c.compile();
                    Executor.execute(instruction_file);
                } else {
                    Executor.execute(file);
                }
            } catch (Exception e) {
                print(false, e.toString());
            }
            return "Execution Finished."; //return executing so the parse command can return some string
        }
        return "file not found!";
    }
    ////See the Terminal_Commands enumeration to see all supported commands

    private String print_commands() {
        StringBuilder sb = new StringBuilder();
        for (Terminal_Commands cmd : Terminal_Commands.values()) {
            sb.append(cmd).append(">>").append(cmd.description()).append("\n"); // command>>description
        }
        return sb.toString();
    }

    public static void println(boolean userPrints, String... s) {
        try {
            if (userPrints)
                for (String str : s)
                    nbos.write(("$User: " + str + "\n").getBytes());
            else
                for (String str : s)
                    nbos.write(("Console: " + str + "\n").getBytes());
            nbos.write(10);
            nbos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void print(boolean userPrints, String... message) {
        try {
            if (userPrints)
                nbos.write("$User: ".getBytes());
            else
                nbos.write("Console: ".getBytes());

            for (String str : message)
                nbos.write(str.getBytes());
            nbos.write(10);
            nbos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Terminal evoke() {
        Terminal dialog = new Terminal();
        dialog.pack();
        dialog.setVisible(true);
        dialog.command_output.setText("Terminal Commands...\n");
        nbos = new BufferedOutputStream(new JTextAreaOutputStream(dialog.command_output));
        return dialog;
    }

    //start up this shit my boy
    public static void main(String... args) {
        evoke();
    }

    private static class JTextAreaOutputStream extends OutputStream {

        private final JTextArea command_output;

        private JTextAreaOutputStream(final JTextArea command_output) {
            this.command_output = command_output;
        }

        @Override
        public void write(int b) {
            command_output.append(String.valueOf((char) b));
            command_output.setCaretPosition(command_output.getDocument().getLength());
        }
    }
}
