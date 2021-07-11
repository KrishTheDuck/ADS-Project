package Kernel.Data_Structures.Node;


import org.nustaq.serialization.annotations.Predict;

import java.io.Serial;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Abstract node class that defines all entries that go into the AbstractNodeUtils.
 * Date: 6/13/2021
 *
 * @author Krish Sridhar
 */
@Predict({INode.class, CNode.class})
public abstract class AbstractNode implements Serializable {
    @Serial
    private static final long serialVersionUID = -2134534234124L;
    protected final byte[] serial;
    private final Queue<String> code = new LinkedList<>();
    protected String name;
    protected AbstractNode parent;
    protected List<AbstractNode> children;

    protected AbstractNode(String name, ArrayList<AbstractNode> children, AbstractNode parent) {
        byte[] serial1;
        this.name = name;
        this.children = children;
        this.parent = parent;
        try {
            serial1 = MessageDigest.getInstance("SHA-1").digest((this.name).getBytes());
        } catch (NoSuchAlgorithmException ignored) {
            serial1 = new byte[0];
        }
        this.serial = serial1;
    }

    public static String serialize(String name) {
        StringBuilder sb = new StringBuilder();
        byte[] bytes = name.getBytes();
        for (int i = 0, bytesLength = bytes.length; i < (bytesLength >> 2); i++) {
            sb.append(bytes[i] & 0XFF + 0x100);
        }
        return sb.toString();
    }

    public final List<AbstractNode> children() {
        return this.children;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public final String name() {
        return this.name;
    }

    public final AbstractNode parent() {
        return this.parent;
    }

    public final boolean add(AbstractNode node) {
        return this.children.add(node);
    }

    public final void remove(AbstractNode node) {
        this.children.remove(node);
    }

    public final void push(String line) {
        this.code.add(line);
    }

    public final String pop() {
        return this.code.remove();
    }

    public final Queue<String> code() {
        return this.code;
    }

    public final AbstractNode find_first(String node_name) {
        for (AbstractNode n : this.children()) {
            if (n.name().equals(node_name)) return n;
        }
        return null;
    }

    public final int indexOf(AbstractNode node) {
        for (int i = 0; i < this.children().size(); i++) {
            if (this.children().get(i).equals(node)) return i;
        }
        return -1;
    }

    public abstract String toString();

    public abstract boolean equals(Object o);

    public final String serial() {
        //construct string
        StringBuilder sb = new StringBuilder();
        byte[] bytes = this.serial;
        for (int i = 0, bytesLength = bytes.length; i < (bytesLength >> 2); i++) {
            sb.append(bytes[i] & 0XFF + 0x100);
        }
        return sb.toString();
    }
}
