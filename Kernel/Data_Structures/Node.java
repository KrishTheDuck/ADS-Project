package Kernel.Data_Structures;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Node {
    private final String instruction;
    private final Node parent;
    private final Queue<String> code = new LinkedList<>();
    private List<Node> children;

    private Node(String instruction) {
        this.instruction = instruction;
        this.children = null;
        parent = null;
    }

    public Node(String instruction, Node parent) {
        this.instruction = instruction;
        this.children = new ArrayList<>(3);
        this.parent = parent;
    }

    public Node(String instruction, ArrayList<Node> children, Node parent) {
        this.instruction = instruction;
        this.children = children;
        this.parent = parent;
    }

    public List<Node> children() {
        return children;
    }

    public void set_children(List<Node> children) {
        this.children = children;
    }

    public String instruction() {
        return instruction;
    }

    public Node parent() {
        return parent;
    }


    public void add(String instruction) {
        if (children != null)
            children.add(new Node(instruction, this));
    }

    public void remove(String instruction) {
        if (children != null)
            children.remove(new Node(instruction));
    }

    public void remove(Node instruction) {
        if (children != null)
            children.remove(instruction);
    }

    public void add(Node new_head) {
        if (children != null)
            children.add(new_head);
    }

    public void push(String instructions) {
        code.add(instructions);
    }

    public String pop() {
        return code.remove();
    }

    public Queue<String> code() {
        return code;
    }

    public String toString() {
        return instruction;
    }
}