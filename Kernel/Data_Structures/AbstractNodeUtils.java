package Kernel.Data_Structures;

import Kernel.Data_Structures.Node.AbstractNode;
import Kernel.Data_Structures.Node.CNode;
import Kernel.Data_Structures.Node.INode;

import java.util.List;
import java.util.Queue;

public class AbstractNodeUtils {
    private AbstractNode head;

    public AbstractNodeUtils(String head) {
        this.head = new INode(head, null);
    }

    public AbstractNodeUtils(AbstractNode node) {
        this.head = node;
    }

    public Queue<String> code() {
        return this.head.code();
    }


    public AbstractNode head() {
        AbstractNode head = this.head;
        while (head.parent() != null) {
            head = move_back();
        }
        return head;
    }

    public void set_condition(String condition) {
        if (this.head instanceof CNode)
            ((CNode) this.head).set_condition(condition);
    }

    public void load(String... code) {
        for (String n : code)
            this.head.push(n);
    }


    public void add(String name, boolean isCondition) {
        if (this.head == null) {
            this.head = new INode(name, null);
            return;
        }
        AbstractNode new_head = (isCondition) ? new CNode(name, head, null) : new INode(name, head);
        this.head.add(new_head);
    }

    public void add_and_enter(String name, boolean isCondition) {
        if (this.head == null) {
            this.head = new INode(name, null);
            return;
        }
        AbstractNode new_head = (isCondition) ? new CNode(name, this.head, null) : new INode(name, this.head);
        this.head.add(new_head);
        this.head = new_head;
    }

    public AbstractNode move_back() {
        AbstractNode save = this.head;
        if ((this.head = this.head.parent()) == null) {
            this.head = save;
        }
        return this.head;
    }

    public void print() {
        System.out.println(head() + ": ");
        System.out.println(head().code());
        print(head().children());
    }

    private void print(List<AbstractNode> nodes) {
        for (AbstractNode node : nodes) {
            System.out.println("children of " + node + ": " + node.children());
            System.out.println("code block of " + node + ": " + node.code());
            print(node.children());
        }
    }

    public String toString() {
        return this.head().name();
    }

    public void delete(INode child) {
        this.head.children().remove(child);
    }
}
