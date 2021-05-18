package Kernel;

import java.util.ArrayList;
import java.util.List;

public class InstructionNode {
    private final String instruction;
    private final InstructionNode parent;
    private final StringBuilder code = new StringBuilder();
    private List<InstructionNode> children;

    private InstructionNode(String instruction) {
        this.instruction = instruction;
        this.children = null;
        parent = null;
    }

    public InstructionNode(String instruction, InstructionNode parent) {
        this.instruction = instruction;
        this.children = new ArrayList<>(3);
        this.parent = parent;
    }

    public InstructionNode(String instruction, ArrayList<InstructionNode> children, InstructionNode parent) {
        this.instruction = instruction;
        this.children = children;
        this.parent = parent;
    }

    public List<InstructionNode> children() {
        return children;
    }

    public void set_children(List<InstructionNode> children) {
        this.children = children;
    }

    public String instruction() {
        return instruction;
    }

    public InstructionNode parent() {
        return parent;
    }


    public void add(String instruction) {
        if (children != null)
            children.add(new InstructionNode(instruction, this));
    }

    public void remove(String instruction) {
        if (children != null)
            children.remove(new InstructionNode(instruction));
    }

    public void remove(InstructionNode instruction) {
        if (children != null)
            children.remove(instruction);
    }

    public void add(InstructionNode new_head) {
        if (children != null)
            children.add(new_head);
    }

    public void append(String instructions) {
        code.append(instructions).append("\n");
    }

    public String code(boolean replaceNewLine) {
        return (replaceNewLine) ? code.toString().replace("\n", "") : code.toString();
    }

    public String toString() {
        return instruction;
    }
}