package EOY_ADS_PROJECT.FunctionLibrary;

/**
 * Basic mathematical function and operation library optimized for faster start-up times and higher consistency.
 * <p>
 * <li>
 * TODO:
 * <ul>Execution time for each method is incredibly quick; however, calling time takes much much much longer.
 * <ul>Find a fix by either caching method calls through some other class or whatever
 * <ul>FIX SINE AND COSINE
 * </li>
 * </p>
 *
 * @author Krish Sridhar, Kevin Wang
 * @see java.lang.Math
 * @since 1.0
 */
@SuppressWarnings("unused")
public final class QuickMath {
    public final static float PI = 3.1415926535f;
    public final static float E = 2.718281f;
    public final static float LN2 = 0.6931471806f;
    private final static int DEFAULT_ACCURACY = 10;
    public final static float sqrt2 = 1.41421356237f;

    private QuickMath() {
    }
    //0.0002107 for bitwise
    //0.0002218 for multiplying
    //(~(i) % 2 + i % 2)

    public static void main(String[] args) {

    }


    /**
     * Square root algorithm utilizing the Bakshali approximation.
     *
     * @apiNote the algorithm is optimized through algebraic manipulation
     * so that it takes up less memory and uses less time.
     */
    public static float BAKSH_sqrt(float x) {
        float a;
        float x0 = 1f;

        int n = (int) (x % 5) + DEFAULT_ACCURACY >> 1;
        for (int i = 0; i < n; i++) {
            a = 0.5f * (x - x0 * x0) / x0;
            x0 = (x0 * x0 + 2f * a * x0 + a * a * 0.5f) / (x0 + a);
        }
        return x0;
    }

    /**
     * Binary search for the square root by first calculating initial approximations
     * and then applying a simple binary search algorithm to arrive at a final guess.
     *
     * @apiNote the initial values are computed using HM-GM-AM so that the initial values
     * properly bound the guess.
     */
    public static float Q_sqrt(float x) {
        float min = ((x * 2f) * (1f / (x + 1f)));
        float max = (x + 1f) / 2f;
        float guess = 0.5f * max + 0.5f * min;

        int n = (int) (x % 5) + DEFAULT_ACCURACY >> 1;
        for (int i = 0; i < n; i++) {
            if (guess * guess > x)
                max = guess;
            else
                min = guess;

            guess = 0.5f * max + 0.5f * min;
        }
        return guess;
    }

    /**
     * Bitwise max function returns higher value
     */
    public static int max(int a, int b) {
        return a - ((a - b) & ((a - b) >> 31));
    }

    /**
     * Bitwise min function returns lower value
     */
    public static int min(int a, int b) {
        return b + ((a - b) & (a - b) >> 31);
    }

    /**
     * Calculates the logarithm given a base and an argument.
     *
     * @apiNote The algorithm utilizes the change of base formula
     * in conjunction with the natural logarithmic approximation.
     * @see QuickMath#nlog
     */
    public static float log(float base, float arg) {
        return (nlog(arg)) / nlog(base);
    }

    /**
     * Computes the natural logarithm with a given float through Arithmetic-Geometric Mean (AGM).
     *
     * @apiNote as method call time in Java can be incredibly long with
     * respect to the actual algorithm execution, the Arithmetic-Geometric
     * Mean algorithm is moved into the nlog method.
     */
    public static float nlog(float x) {
        if (x == 1) return 0;

        float agm = 1f;
        float g1 = 1 / (x * (1 << (DEFAULT_ACCURACY - 2)));
        float arithmetic;
        float geometric;

        for (int i = 0; i < 5; i++) {
            arithmetic = (agm + g1) / 2f;
            geometric = BAKSH_sqrt(agm * g1);
            agm = arithmetic;
            g1 = geometric;
        }

        return (PI / 2f) * (1 / agm) - DEFAULT_ACCURACY * LN2;
    }

    /**
     * Fast absolute value using bitwise operators.
     */
    public static int ABS(int x) {
        return (x + (x >> 31)) ^ (x >> 31);
    }

    /**
     * Absolute value utilizing casting tricks and bit manipulation
     */
    public static float ABS(float f) {
        return (ABS((int) f) + ABS((int) (((int) f - f) * 1000)) / 1000f);
    }

    public static double ABS(double d) {
        return (ABS((int) d) + ABS((int) (((int) d - d) * 100000)) / 100000d);
    }


    //TODO fix exp so that approximation is better here

    /**
     * Calculates {@code base} to the power of {@code exponent}.
     *
     * @apiNote As direct calculation may be inefficient, we treat
     * the exponent as a binary number for less iterations.
     */
    public static double POW(float base, int exponent) {
        double ans = base;
        while (exponent > 0) {
            ans += (base - 1) * (exponent & 1) * ans; // genius math hack
            exponent >>= 1; //bit shift exponent down
            base *= base;
        }
        return ans;
    }

    //GCF algorithm utilizing a quick Euclidean algorithm
    public static int GCF(int... ints) {
        int gcd = pair_GCF(ints[0], ints[1]);
        for (int i = 2, n = ints.length; i < n; i++) {
            gcd = pair_GCF(ints[2], gcd);
        }
        return gcd;
    }

    //TODO optimize gcf algorithm utilizing stein's algo or through better loops
    /*Quick Greatest Common Factor Algorithm.*/
    private static int pair_GCF(int a, int b) {
        int t;
        while (b != 0) {
            t = a;
            a = b;
            b = t % b;
        }
        return a;
    }

    /*
     * Bhaskara's Approximation:
     *
     *   16x(pi-x)
     * -------------
     * 5pi^2-4x(pi-x)
     * */
    public static float sin(float x) {
        float upper = (PI - x) * 16f * x;
        float lower = 5 * PI * PI - 4f * x * (PI - x);
        return upper / lower;

    }

    /*
     * Bhaskara's Approximation:
     *
     * pi^2 - 4*x^2
     * ------------
     * pi^2 + x^2
     */
    public static float cos(float x) {
        float upper = PI * PI - 4f * x * x;
        float lower = PI * PI + x * x;
        return upper / lower;
    }

    //TODO: make pow function better so that this round function operates faster
    public static strictfp float round_to_int(double d) {
        return (int) (d + 0.5);
    }

    //factorial through Stirling Approximation
    //TODO: fix exp so that stirling factorial works
    public static float stirling_factorial(float f) {
        return 0;
    }

    //TODO: fix this shit
    public static float exp(float x) {

        return 0;
    }
}
