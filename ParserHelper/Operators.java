package ParserHelper;

import Kernel.Data_Structures.OrderedPairList;
import Kernel.Data_Structures.Pair;
import Kernel.Data_Structures.PairCollection;
import Kernel.Data_Structures.SortedPairList;

import java.util.List;

/**
 * A class that asserts the operator precedence of the language.
 *
 * @author Krish Sridhar
 * @see PairCollection
 * @see SortedPairList
 * @see OrderedPairList
 * @see Pair
 * @since 1.0
 */
final class Operators {
    public static final PairCollection<String, Integer> operators = new OrderedPairList<>(21) {
        {
            add("!", 0);
            add("~", 0);
            //cast goes here
            add("%", 1);
            add("*", 1);
            add("**", 1);
            add("/", 1);
            add("+", 2);
            add("-", 2);
            add("<<", 3);
            add(">>", 3);
            add(">>>", 3);
            add("<", 4);
            add("<=", 5);
            add(">", 5);
            add(">=", 5);
            add("==", 6);
            add("!=", 6);
            add("&", 7);
            add("^", 8);
            add("|", 9);
            add("&&", 10);
            add("||", 11);
        }
    }; //operator precedence

    /**
     * Accepts a list of operators and then returns the order they should be evaluated with in order of highest to lowest precedence.
     *
     * @param operators List of operators.
     * @return An integer array containing indexes of which operators to evaluate first, in order of highest to lowest precedence.
     * @see PairCollection
     * @see OrderedPairList
     */
    public static int[] order(List<String> operators) {
        PairCollection<String, Integer> map = new OrderedPairList<>(operators.size());
        for (int i = 0, MAX = operators.size(); i < MAX; i++) {
            map.add(operators.get(i), i);
        }
        return order(map);
    }

    /**
     * Accepts a <code>PairCollection</code> of operators.
     *
     * @param map A List of operator-index pairs.
     * @return An integer array containing indexes of which operators to evaluate first, in order of highest to lowest precedence.
     * @see Operators#order(List)
     * @see SortedPairList
     * @see Pair
     */
    public static int[] order(final PairCollection<String, Integer> map) {

        //System.out.println("given map: " + map);

        PairCollection<Integer, Integer> precedence = new SortedPairList<>(map.size());

        //swap operators for ints
        for (Pair<String, Integer> entry : map) {
            precedence.add(operators.getPairFromKey(entry.key()).value(), entry.value());
            //System.out.println(precedence);
        }

        //System.out.println("given precedence: " + precedence);

        //now precedence is the sorted order
        int[] order = new int[precedence.size()];
        int i = 0;
        for (Pair<Integer, Integer> entry : precedence) {
            order[i++] = entry.value();
        }

        return order;
    }
}
