package Kernel.Data_Structures.Node;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * Object wrapper for a AbstractNode  in the Abstract Syntax Tree. Contains an name header, which can state the executing
 * condition or scope name, a Queue of code, a list of children, and a reference back to the parent AbstractNode .
 *
 * @author Krish Sridhar
 * Date: May 30, 2021
 * @see Queue
 * @see AbstractNodeUtils
 * @see List
 * @since 1.0
 */
public class INode extends AbstractNode {
    @Serial
    private static final long serialVersionUID = 1892347912387L;

    /**
     * Instantiates a AbstractNode  object with a name (which can be considered the executing condition or the scope name)
     * an empty ArrayList of length 3, and a reference to the parent AbstractNode .
     *
     * @param name   name header
     * @param parent parent AbstractNode
     * @see AbstractNode #INode(String, ArrayList, AbstractNode , boolean)
     */
    public INode(String name, AbstractNode parent) {
        super(name, new ArrayList<>(), parent);
    }

    /**
     * Instantiates a AbstractNode  object with a name (which can be considered the executing condition or the scope name)
     * an empty ArrayList of length 3, and a reference to the parent AbstractNode .
     *
     * @param name     name header
     * @param parent   parent AbstractNode
     * @param children a set of children
     */
    public INode(String name, ArrayList<AbstractNode> children, AbstractNode parent) {
        super(name, children, parent);
    }

    @Override
    public String toString() {
        return "i: " + this.name;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof INode i) && (i.hashCode() == this.hashCode());
    }
}