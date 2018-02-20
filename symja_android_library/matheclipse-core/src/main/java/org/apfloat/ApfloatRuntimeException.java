package org.apfloat;

/**
 * Exception indicating some unexpected error situation.
 * This exception can be thrown in different situations, for example:
 *
 * <ul>
 *   <li>The result of an operation would have infinite size. For example,
 *       <code>new Apfloat(2).divide(new Apfloat(3))</code>, in radix 10.</li>
 *   <li>Overflow. If the exponent is too large to fit in a <code>long</code>,
 *       the situation can't be handled. Also, there is no "infinity" apfloat
 *       value that could be returned as the result.</li>
 *   <li>Total loss of precision. For example, <code>ApfloatMath.sin(new Apfloat(1e100))</code>.
 *       If the magnitude (100) is far greater than the precision (1) then
 *       the value of the <code>sin()</code> function can't be determined
 *       to any accuracy.</li>
 *   <li>Some other internal limitation.</li>
 * </ul>
 *
 * @version 1.1
 * @author Mikko Tommila
 */

public class ApfloatRuntimeException
    extends RuntimeException
{
    /**
     * Constructs a new apfloat runtime exception with an empty detail message.
     */

    public ApfloatRuntimeException()
    {
    }

    /**
     * Constructs a new apfloat runtime exception with the specified detail message.
     *
     * @param message The detail message.
     */

    public ApfloatRuntimeException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new apfloat runtime exception with the specified detail message and cause.
     *
     * @param message The detail message.
     * @param cause Originating cause of the exception.
     */

    public ApfloatRuntimeException(String message, Throwable cause)
    {
        super(message, cause);
    }

    private static final long serialVersionUID = -7022924635011038776L;
}
