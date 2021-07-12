package RuntimeManager;

import java.util.Stack;

public final class SharedData {
    public static transient final Stack<String> current_scope = new Stack<>();
    public static transient String MAIN_CODE = "";
}
