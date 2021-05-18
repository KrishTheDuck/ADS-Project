package Kernel;

import java.util.List;

public class AbstractSyntaxTree {
    private InstructionNode head;


    public AbstractSyntaxTree(String head) {
        this.head = new InstructionNode(head, null);
    }

    public AbstractSyntaxTree() {
        this.head = null;
    }

    public static void main(String[] args) {

    }

    public InstructionNode head() {
        InstructionNode head = this.head;
        while (head.parent() != null) {
            head = move_back();
        }
        return head;
    }

    public void load(String conditional) {
        this.head.append(conditional);
    }

    public void add_no_enter(String conditional) {
        if (this.head == null) {
            this.head = new InstructionNode(conditional, null);
            return;
        }
        InstructionNode new_head = new InstructionNode(conditional, head);
        this.head.add(new_head);
    }

    public void add_and_enter(String conditional) {
        if (this.head == null) {
            this.head = new InstructionNode(conditional, null);
            return;
        }
        InstructionNode new_head = new InstructionNode(conditional, this.head);
        this.head.add(new_head);
        this.head = new_head;
    }

    public void enter_and_add(String conditional, int i) {
        if (i >= this.head.children().size()) return;
        if (this.head == null) {
            this.head = new InstructionNode(conditional, null);
            return;
        }
        this.head = this.head.children().get(i);
        this.head.add(new InstructionNode(conditional, this.head));
    }

    public InstructionNode move_back() {
        InstructionNode save = this.head;
        if ((this.head = this.head.parent()) == null) {
            this.head = save;
        }
        return this.head;
    }

    public InstructionNode move_back_and_delete() {
        InstructionNode save = this.head;
        if ((this.head = this.head.parent()) == null) {
            this.head = save;
        } else {
            this.head.set_children(save.children());
        }
        return this.head;
    }

    public InstructionNode move_back_and_delete_branch() {
        InstructionNode save = this.head;
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

    private void print(List<InstructionNode> nodes) {
        for (InstructionNode node : nodes) {
            System.out.println(node + ": " + node.code(true));
            System.out.println(node.children());
            print(node.children());
        }
    }
}
