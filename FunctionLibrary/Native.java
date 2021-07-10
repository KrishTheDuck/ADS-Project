package FunctionLibrary;

/**
 * Native abstract class to be extended by classes that wish to be apart of the FLMapper class.
 * Managers of the Native Library should assert that the FLMapper class is made aware of the newly implemented classes.
 *
 * @author Krish Sridhar
 * @see FunctionLibrary.FLMapper
 * @since 1.0
 * Date: June 9, 2021
 */
public abstract class Native implements Comparable<Native> {
    public static <T> int GenerateHashCode(Class<T> clazz) {
        String name = clazz.getSimpleName();
        long sum = 0L;
        for (byte nb : name.getBytes()) {
            sum += nb;
        }
        return (int) ((sum ^ clazz.hashCode()) & (clazz.hashCode() >>> 8));
    }

    public abstract int getSerial();

    public abstract int compareTo(Native o); //noteme in case the library is too big use binary searches with compare to instead
}