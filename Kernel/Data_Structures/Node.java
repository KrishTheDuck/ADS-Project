package Kernel.Data_Structures;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Object wrapper for a node in the Abstract Syntax Tree. Contains an name header, which can state the executing
 * condition or scope name, a Queue of code, a list of children, and a reference back to the parent node.
 *
 * @author Krish Sridhar
 * Date: May 30, 2021
 * @see Queue
 * @see AbstractSyntaxTree
 * @see List
 * @since 1.0
 */
public class Node {
    private final String name;
    private final Node parent;
    private final Queue<String> code = new LinkedList<>();
    private final boolean isCondition;
    private List<Node> children;
    private String condition;

    //private node initialization. SHOULD NOT BE USED BY USER!!!
    private Node(String name) {
        this(name, null, null, false);
    }

    /**
     * Instantiates a node object with a name (which can be considered the executing condition or the scope name)
     * an empty ArrayList of size 3, and a reference to the parent node.
     *
     * @param name   name header
     * @param parent parent node
     * @see Node#Node(String, ArrayList, Node, boolean)
     */
    public Node(String name, Node parent, boolean isCondition) {
        this(name, new ArrayList<>(3), parent, isCondition);
    }

    /**
     * Instantiates a node object with a name (which can be considered the executing condition or the scope name)
     * an empty ArrayList of size 3, and a reference to the parent node.
     *
     * @param name     name header
     * @param parent   parent node
     * @param children a set of children
     */
    public Node(String name, ArrayList<Node> children, Node parent, boolean isCondition) {
        this.name = name;
        this.children = children;
        this.parent = parent;
        this.isCondition = isCondition;
        if (!this.isCondition)
            condition = null;
        else
            condition = "";
    }

    /**
     * @return Null if not a condition or the first code line if it is a condition.
     */
    public String condition() {
        return this.condition;
    }

    /**
     * Returns this node's children.
     *
     * @return Children
     */
    public List<Node> children() {
        return this.children;
    }

    /**
     * Sets this node's children.
     *
     * @param children a list of nodes
     */
    public void set_children(List<Node> children) {
        this.children = children;
    }


    /**
     * Stores a condition if we need one.
     *
     * @param condition String condition
     */
    public void set_condition(String condition) {
        if (this.isCondition)
            this.condition = condition;
        else
            throw new IllegalArgumentException("Node \"" + name + "\" is not conditional.\nCode body:\n" + code);
    }

    /**
     * Returns the name of the scope.
     *
     * @return name of scope
     */
    public String name() {
        return name;
    }

    /**
     * Returns parent node.
     *
     * @return parent node
     */
    public Node parent() {
        return parent;
    }


    /**
     * Adds a node given a scope name.
     *
     * @param name String name of scope.
     */
    public void add(String name, boolean isCondition) {
        if (children != null)
            children.add(new Node(name, this, isCondition));
    }

    /**
     * Removes a child given the name of the child.
     *
     * @param name String name of scope.
     */
    public void remove(String name) {
        if (children != null)
            children.remove(new Node(name));
    }


    /**
     * Removes a child given the reference.
     *
     * @param node Node to remove
     */
    public void remove(Node node) {
        if (children != null)
            children.remove(node);
    }

    /**
     * Adds a new child node.
     *
     * @param node New node
     */
    public void add(Node node) {
        if (children != null)
            children.add(node);
    }


    /**
     * Pushes a statement into the code body.
     *
     * @param statement String statement
     */
    public void push(String statement) {
        code.add(statement);
    }


    /**
     * Returns first line of code.
     *
     * @return First string element.
     */
    public String pop() {
        return code.remove();
    }

    /**
     * Returns code body.
     *
     * @return Queue of code body.
     */
    public Queue<String> code() {
        return code;
    }

    /**
     * Returns the name of the node.
     * {@inheritDoc}
     */
    public String toString() {
        return name;
    }

    /**
     * Returns next code unit
     *
     * @return First code string
     */
    public String peek() {
        return this.code.peek();
    }

    /**
     * Returns if the node is a condition
     *
     * @return boolean if its a condition
     */
    public boolean isCondition() {
        return this.isCondition;
    }


    /**
     * Finds the first occurrence of a node in the children given the name.
     *
     * @param node_name Name of the node
     * @return Node if found and null if nothing is found
     */
    public Node find(String node_name) {
        for (Node n : this.children) {
            if (n.name.equals(node_name))
                return n;
        }
        return null;
    }

    /**
     * Remove the node that we should delete.
     *
     * @param node Node to delete
     */
    public void delete(Node node) {
        this.children.remove(node);
    }

    public int indexOf(Node checking) {
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i).equals(checking)) return i;
        }
        return -1;
    }
}