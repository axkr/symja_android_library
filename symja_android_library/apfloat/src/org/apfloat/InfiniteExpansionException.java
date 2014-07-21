package org.apfloat;

/**
 * Exception indicating that the result of an operation
 * would have infinite size.<p>
 *
 * For example, <code>new Apfloat(2).divide(new Apfloat(3))</code>, in radix 10.
 *
 * @since 1.5
 * @version 1.5
 * @author Mikko Tommila
 */

public class InfiniteExpansionException
    extends ApfloatRuntimeException
{
    /**
     * Constructs a new apfloat infinite expansion exception with an empty detail message.
     */

    public InfiniteExpansionException()
    {
    }

    /**
     * Constructs a new apfloat infinite expansion exception with the specified detail message.
     *
     * @param message The detail message.
     */

    public InfiniteExpansionException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new apfloat infinite expansion exception with the specified detail message and cause.
     *
     * @param message The detail message.
     * @param cause Originating cause of the exception.
     */

    public InfiniteExpansionException(String message, Throwable cause)
    {
        super(message, cause);
    }

    private static final long serialVersionUID = -7022924635011038776L;
}
