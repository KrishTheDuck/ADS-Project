package FunctionLibrary.Library;

import FunctionLibrary.Native;
import Kernel.Datatypes.FloatString;
import Kernel.Datatypes.IntString;
import LanguageExceptions.FunctionNotFoundException;

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
public final strictfp class QuickMath extends Native {
    public final static float PI = 3.1415926535f;
    public final static float E = 2.718281f;
    public final static float LN2 = 0.6931471806f;
    public final static float sqrt2 = 1.41421356237f;
    private final static int DEFAULT_ACCURACY = 10;

    private static final int Serial = Native.GenerateHashCode(QuickMath.class);

    public QuickMath() {
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
    //0.0002107 for bitwise
    //0.0002218 for multiplying
    //(~(i) % 2 + i % 2)

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

    /**
     * Calculates {@code base} to the power of {@code exponent}.
     *
     * @apiNote As direct calculation may be inefficient, we treat
     * the exponent as a binary number for less iterations.
     */
    public static double POW(float base, int exponent) {
        double ans = base;
        if (exponent == 0) return 1;

        --exponent;
        while (exponent > 0) {
            ans += (base - 1) * (exponent & 1) * ans; // genius math hack
            exponent >>= 1; //bit shift exponent down
            base *= base;
        }
        return ans;
    }

    public static int POW(int base, int exponent) {
        return (int) POW((float) base, exponent);
    }

    public static int MPOW(long base, int exponent, long mod) {
        long ans = base % mod;
        if (exponent == 0) return 1;
        --exponent;
        while (exponent > 0) {
            ans = (ans + ((base - 1) * (exponent & 1) * ans)) % mod;
            exponent >>= 1;
            base = (base * base) % mod;
        }
        return (int) (ans % mod);
    }


    //TODO fix exp so that approximation is better here

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
    //2 ^(111)
    //2^1 + 2^2 + 2^3
    //111

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

    public static Object map(String function_name, String args) throws FunctionNotFoundException {
        return switch (function_name) {
            case "bsqrt" -> {
                float i = FloatString.stof(args.getBytes());
                System.out.println("Parameter: " + i);
                yield BAKSH_sqrt(FloatString.stof(args.getBytes()));
            }
            case "qsqrt" -> {
                float i = FloatString.stof(args.getBytes());
                System.out.println("Parameter: " + i);
                yield Q_sqrt(FloatString.stof(args.getBytes()));
            }
            case "max" -> {
                String[] args2 = args.split(" ");
                yield max(IntString.stoi(args2[0]), IntString.stoi(args2[1]));
            }
            case "min" -> {
                String[] args2 = args.split(" ");
                yield min(IntString.stoi(args2[0]), IntString.stoi(args2[1]));
            }
            case "log" -> {
                String[] args2 = args.split(" ");
                yield log(FloatString.stof(args2[0]), FloatString.stof(args2[1]));
            }
            case "nlog" -> nlog(FloatString.stof(args.getBytes()));
            case "ABS" -> {
                if (args.contains("."))
                    yield ABS(FloatString.stof(args));
                else
                    yield ABS(IntString.stoi(args));
            }
            case "GCF" -> {
                String[] s = args.split(" ");
                int[] i = new int[s.length];

                for (int j = 0; j < i.length; j++) {
                    i[j] = IntString.stoi(s[j]);
                }
                yield GCF(i);
            }

            default -> throw new FunctionNotFoundException("Function \"" + function_name + "\" does not exist in library StandardIO.");
        };
    }

    //http://www.cs.cmu.edu/afs/cs/academic/class/15451-f14/www/lectures/lec6/karp-rabin-09-15-14.pdf
    //https://ocw.mit.edu/courses/mathematics/18-783-elliptic-curves-spring-2019/lecture-notes/MIT18_783S19_lec12.pdf

    //random prime generator
    public static int random_prime() {
        int length = 22;
        int b = urgbs(length);
        while (!miller_rabin(b)) {
            b = urgbs(length);
        }
        return b;
    }

    public static int urgbs(int size) {
        byte[] b = new byte[size];
        for (int i = 0; i < size; i++) {
            b[i] = (byte) (2 * Math.random());
        }
        return IntString.btoi(b);
    }


    public static void main(String[] args) {
        System.out.println(random_prime());
    }


    public static boolean miller_rabin(int n) {
        if (n == 2)
            return true;
        if (n % 2 == 0 || n <= 1)
            return false;

        // Find n = 2^d * r + 1
        int d = n - 1;
        while (d % 2 == 0)
            d >>= 1;

        accuracy:
        for (int i = 0; i < DEFAULT_ACCURACY; i++) {
            int a = 2 + (int) (Math.random() % (n - 4));
            // a^d % n
            int x = MPOW(a, d, n);
            if (x == 1 || x == n - 1)
                continue;

            while (d != n - 1) {
                x = MPOW(x, 2, n);
                d <<= 1;

                if (x == 1)
                    return false;
                if (x == n - 1)
                    continue accuracy;
            }
            return false;
        }
        return true;
    }


    @Override
    public int getSerial() {
        return Serial;
    }

    @Override
    public int compareTo(Native o) {
        return Serial - o.getSerial();
    }
}
