package org.apfloat.internal;

/**
 * Exception indicating a different implementation of the apfloat SPI
 * being used in two operands of a calculation.<p>
 *
 * @since 1.5
 * @version 1.5
 * @author Mikko Tommila
 */

public class ImplementationMismatchException
    extends ApfloatInternalException
{
    /**
     * Constructs a new apfloat implementation mismatch exception with an empty detail message.
     */

    public ImplementationMismatchException()
    {
    }

    /**
     * Constructs a new apfloat implementation mismatch exception with the specified detail message.
     *
     * @param message The detail message.
     */

    public ImplementationMismatchException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new apfloat implementation mismatch exception with the specified detail message and cause.
     *
     * @param message The detail message.
     * @param cause Originating cause of the exception.
     */

    public ImplementationMismatchException(String message, Throwable cause)
    {
        super(message, cause);
    }

    private static final long serialVersionUID = -7022924635011038776L;
}
