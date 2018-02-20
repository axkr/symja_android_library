package org.apfloat;

/**
 * Exception indicating a result that would have no significant digits.<p>
 *
 * An example would be <code>ApfloatMath.sin(new Apfloat(1e100))</code>.
 * Rather than returning an arbitrary result, this exception should be thrown.<p>
 *
 * Note that a subtraction where a complete loss of digits occurs does not throw
 * this exception but instead just returns zero.
 *
 * @since 1.5
 * @version 1.5
 * @author Mikko Tommila
 */

public class LossOfPrecisionException
    extends ApfloatRuntimeException
{
    /**
     * Constructs a new apfloat loss of precision exception with an empty detail message.
     */

    public LossOfPrecisionException()
    {
    }

    /**
     * Constructs a new apfloat loss of precision exception with the specified detail message.
     *
     * @param message The detail message.
     */

    public LossOfPrecisionException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new apfloat loss of precision exception with the specified detail message and cause.
     *
     * @param message The detail message.
     * @param cause Originating cause of the exception.
     */

    public LossOfPrecisionException(String message, Throwable cause)
    {
        super(message, cause);
    }

    private static final long serialVersionUID = -7022924635011038776L;
}
