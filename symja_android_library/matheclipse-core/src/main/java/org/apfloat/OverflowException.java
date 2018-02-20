package org.apfloat;

/**
 * Exception indicating an overflow in a calculation.<p>
 *
 * For example <code>ApfloatMath.exp(new Apfloat(1e100))</code>.<p>
 *
 * If the exponent is too large to fit in a <code>long</code>,
 * the situation can't be handled. Note that there is no
 * "infinity" apfloat value that could be returned as the result.
 *
 * @since 1.5
 * @version 1.5
 * @author Mikko Tommila
 */

public class OverflowException
    extends ApfloatRuntimeException
{
    /**
     * Constructs a new apfloat overflow exception with an empty detail message.
     */

    public OverflowException()
    {
    }

    /**
     * Constructs a new apfloat overflow exception with the specified detail message.
     *
     * @param message The detail message.
     */

    public OverflowException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new apfloat overflow exception with the specified detail message and cause.
     *
     * @param message The detail message.
     * @param cause Originating cause of the exception.
     */

    public OverflowException(String message, Throwable cause)
    {
        super(message, cause);
    }

    private static final long serialVersionUID = -7022924635011038776L;
}
