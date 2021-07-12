package Kernel.Data_Structures.Node;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Arrays;

public class FNode extends AbstractNode {
    @Serial
    private static final long serialVersionUID = 903872212L;
    private final String[] properties;
    private final String[] parameters;
    private final String return_type;

    public FNode(String name, String[] properties, String[] parameters, String return_type) {
        super(return_type + " " + name + " " + Arrays.toString(parameters), new ArrayList<>(), null);
        this.properties = properties;
        this.parameters = parameters;
        this.return_type = return_type;
    }

    @Override
    public String toString() {
        return super.name + " => properties: " + Arrays.toString(properties) + ", parameters: " + Arrays.toString(parameters) + ", return type: " + return_type;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof FNode f && f.serial == this.serial;
    }

    public String return_type() {
        return this.return_type;
    }

    public String[] parameters() {
        return this.parameters;
    }

    public String[] properties() {
        return this.properties;
    }
}
