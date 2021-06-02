package Kernel.Data_Structures;

import java.util.List;

@SuppressWarnings("unused")
public class AbstractSyntaxTree {
    private Node head;

    public AbstractSyntaxTree(String head) {
        this.head = new Node(head, null);
    }

    public AbstractSyntaxTree() {
        this.head = null;
    }

    public Node current() {
        return this.head;
    }

    public Node head() {
        Node head = this.head;
        while (head.parent() != null) {
            head = move_back();
        }
        return head;
    }

    public Node enter(int child) {
        return head.children().get(child);
    }

    public void load(String conditional) {
        this.head.push(conditional);
    }

    public void add_no_enter(String conditional) {
        if (this.head == null) {
            this.head = new Node(conditional, null);
            return;
        }
        Node new_head = new Node(conditional, head);
        this.head.add(new_head);
    }

    public void add_and_enter(String conditional) {
        if (this.head == null) {
            this.head = new Node(conditional, null);
            return;
        }
        Node new_head = new Node(conditional, this.head);
        this.head.add(new_head);
        this.head = new_head;
    }

    public void enter_and_add(String conditional, int i) {
        if (i >= this.head.children().size()) return;
        if (this.head == null) {
            this.head = new Node(conditional, null);
            return;
        }
        this.head = this.head.children().get(i);
        this.head.add(new Node(conditional, this.head));
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
        System.out.println(head().children());
        print(head().children());
    }

    private void print(List<Node> nodes) {
        for (Node node : nodes) {
            System.out.println(node + ": " + node.code());
            System.out.println(node.children());
            print(node.children());
        }
    }
}
