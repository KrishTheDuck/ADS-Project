package Console;

import Kernel.Executor;
import Kernel.Preprocessor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Scanner;

public class Terminal extends JFrame {
    private static BufferedOutputStream nbos;
    private static JTextAreaInputStream nbis;
    private JTextField input;
    private JTextArea output;
    private JPanel content;

    public Terminal() {
        super("Terminal");
        super.setContentPane(content);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(800, 500));
        pack();
        setVisible(true);

        //set background color
        input.setBackground(Color.BLACK);
        output.setBackground(Color.BLACK);
        getContentPane().setBackground(Color.BLACK);

        //set foreground color
        input.setForeground(Color.WHITE);
        output.setForeground(Color.WHITE);
        getContentPane().setForeground(Color.BLACK);

        //set font
        input.setFont(new Font("Consolas", Font.PLAIN, 14));
        output.setFont(new Font("Consolas", Font.PLAIN, 14));

        //set properties
        input.setEditable(true);
        output.setEditable(false);

        input.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String command = input.getText();
                    print(true, command);
                    input.setText("");
                    nbis.updateBuffer(command);
                    command = (parse(command.split(" ")));
                    print(false, command);
                }
            }
        });

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                onCancel();
            }
        });
        content.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public static byte[] readLine() {
        Scanner scan;
        return nbis.flush();
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
    //parse the entered commands
    // '!' are the symbols indicating commands are present

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
        dialog.output.setText("Terminal Commands...\n");
        nbos = new BufferedOutputStream(new JTextAreaOutputStream(dialog.output));
        nbis = new JTextAreaInputStream(dialog.output);
        try {
            nbos.write(dialog.print_commands().getBytes());
            nbos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dialog;
    }

    //works in tandem with parse to print out to the console when the file exists
    //will eventually hand over operations to the Preprocessor class which will check for syntax
    public static void main(String... args) {
        evoke();
    }

    private void onCancel() {
        try {
            nbos.close();
        } catch (Exception ignored) {
        }
        dispose();
    }

    //See the Terminal_Commands enumeration to see all supported commands
    private String parse(String[] args) {
        char operator = args[0].charAt(0); //stores operator
        String cmd = args[0].substring(1); //stores issued command
        System.out.println(cmd);
        if (operator == '!') {
            return switch (cmd) {
                case "help" -> print_commands(); //print all commands
                case "rnr" -> execute(args[1], false); //run a program by accepting a file path
                case "run" -> execute(args[1], true);
                case "readLine" -> new String(nbis.flush());
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

    private String execute(String path, boolean shouldReCompile) {
        //!run <file_path>
        File file = new File(path);
        print(false, "Executing....");
        if (file.exists()) {//if the file doesn't even exist don't bother
            try {
                if (shouldReCompile) {
                    Preprocessor c = new Preprocessor(file);
                    File instruction_file = c.compile();
                    Executor.InstructionLoader(instruction_file);
                } else {
                    Executor.InstructionLoader(file);
                }
            } catch (Exception e) {
                print(false, e.toString());
            }
            return "Execution Finished."; //return executing so the parse command can return some string
        }
        return "file not found!";
    }

    private String print_commands() {
        StringBuilder sb = new StringBuilder("\n-------------------------------------------------------------\n");
        for (Terminal_Commands cmd : Terminal_Commands.values()) {
            sb.append(cmd).append(">> ").append(cmd.description()).append("\n"); // command>>description
        }
        sb.append("-------------------------------------------------------------\n");
        return sb.toString();
    }

    private static class JTextAreaInputStream {
        private byte[] buffer;

        private JTextAreaInputStream(final JTextArea command_output) {
            String[] arr = command_output.getText().split("\n");
            this.buffer = arr[arr.length - 1].getBytes();
        }

        public void updateBuffer(String line) {
            buffer = line.getBytes();
        }

        public byte[] flushWith(String new_line) {
            byte[] prev = buffer;
            this.buffer = new_line.getBytes();
            return prev;
        }
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
