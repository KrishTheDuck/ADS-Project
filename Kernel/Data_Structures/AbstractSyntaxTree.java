package Kernel.Data_Structures;

import java.util.List;
import java.util.Queue;

@SuppressWarnings("unused")
public class AbstractSyntaxTree {
    private Node head;

    public AbstractSyntaxTree(String head, boolean isCondition) {
        this.head = new Node(head, null, isCondition);
    }

    public AbstractSyntaxTree() {
        this.head = null;
    }

    public Node current() {
        return this.head;
    }

    public String consume_next() {
        return this.head.pop();
    }

    public Queue<String> code() {
        return this.head.code();
    }

    public String gci() { //get current instruction
        return this.head.peek();
    }

    public Node head() {
        Node head = this.head;
        while (head.parent() != null) {
            head = move_back();
        }
        return head;
    }

    public void enter(int child) {
        this.head = head.children().get(child);
    }

    public boolean enter(String child) {
        for (Node n : this.head.children()) {
            if (n.name().equals(child)) {
                this.head = n;
                return true;
            }
        }
        return false;
    }

    public void load(String name) {
        this.head.push(name);
    }

    public void add_no_enter(String name, boolean isCondition) {
        if (this.head == null) {
            this.head = new Node(name, null, isCondition);
            return;
        }
        Node new_head = new Node(name, head, isCondition);
        this.head.add(new_head);
    }

    public void add_and_enter(String name, boolean isCondition) {
        if (this.head == null) {
            this.head = new Node(name, null, isCondition);
            return;
        }
        Node new_head = new Node(name, this.head, isCondition);
        this.head.add(new_head);
        this.head = new_head;
    }

    public void enter_and_add(String name, int i, boolean isCondition) {
        if (i >= this.head.children().size()) return;
        if (this.head == null) {
            this.head = new Node(name, null, isCondition);
            return;
        }
        this.head = this.head.children().get(i);
        this.head.add(new Node(name, this.head, isCondition));
    }

    public Node move_back() {
        Node save = this.head;
        if ((this.head = this.head.parent()) == null) {
            this.head = save;
        }
        return this.head;
    }

    public Node move_back_and_delete() {
        Node save = this.head;
        if ((this.head = this.head.parent()) == null) {
            this.head = save;
        } else {
            this.head.set_children(save.children());
        }
        return this.head;
    }

    public Node move_back_and_delete_branch() {
        Node save = this.head;
        if ((this.head = this.head.parent()) == null) {
            this.head = save;
        } else {
            this.head.remove(save);
        }
        return this.head;
    }

    public void print() {
        System.out.println(head() + ": ");
        System.out.println(head().code());
        print(head().children());
    }

    private void print(List<Node> nodes) {
        for (Node node : nodes) {
            System.out.println("children of " + node + ": " + node.children());
            System.out.println("code block of " + node + ": " + node.code());
            print(node.children());
        }
    }

    public void delete(Node child) {
        this.head.children().remove(child);
    }

    public void set_condition(String token) {
        this.head.set_condition(token);
    }
}
