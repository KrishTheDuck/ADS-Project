package Kernel.RuntimeManager;

import Kernel.Data_Structures.Node.AbstractNode;
import Kernel.Data_Structures.OrderedPairList;

import java.util.Stack;

public final class SharedData {
    public static transient final Stack<String> current_scope = new Stack<>();
    public static transient String MAIN_CODE = "";
    public static transient OrderedPairList<String, AbstractNode> library;
}
