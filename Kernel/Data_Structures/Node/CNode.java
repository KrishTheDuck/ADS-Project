package Kernel.Data_Structures.Node;

import java.io.Serial;
import java.util.ArrayList;

public final class CNode extends AbstractNode {
    @Serial
    private static final long serialVersionUID = 213491234232L;
    private String condition;

    /**
     * Instantiates a Conditional Node object with a name (which can be considered the executing condition or the scope name)
     * an empty ArrayList of length 3, and a reference to the parent AbstractNode .
     *
     * @param name   name header
     * @param parent parent AbstractNode
     * @see AbstractNode #INode(String, ArrayList, AbstractNode , boolean)
     */
    public CNode(String name, AbstractNode parent, String condition) {
        super(name, new ArrayList<>(), parent);
        this.condition = condition;
    }


    /**
     * Instantiates a AbstractNode  object with a name (which can be considered the executing condition or the scope name)
     * an empty ArrayList of length 3, and a reference to the parent AbstractNode .
     *
     * @param name     name header
     * @param parent   parent AbstractNode
     * @param children a set of children
     */
    public CNode(String name, ArrayList<AbstractNode> children, AbstractNode parent, String condition) {
        super(name, children, parent);
        this.condition = condition;
    }

    public String condition() {
        return this.condition;
    }

    public void set_condition(String condition) {
        this.condition = condition;
    }

    @Override
    public String toString() {
        return "c: " + this.name + " " + this.condition;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof CNode) && this.condition.equals(((CNode) o).condition);
    }
}
