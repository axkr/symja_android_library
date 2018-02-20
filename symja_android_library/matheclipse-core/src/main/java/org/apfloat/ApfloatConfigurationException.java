package org.apfloat;

/**
 * Exception indicating an error in the apfloat configuration.<p>
 *
 * @since 1.5
 * @version 1.5
 * @author Mikko Tommila
 */

public class ApfloatConfigurationException
    extends ApfloatRuntimeException
{
    /**
     * Constructs a new apfloat configuration exception with an empty detail message.
     */

    public ApfloatConfigurationException()
    {
    }

    /**
     * Constructs a new apfloat configuration exception with the specified detail message.
     *
     * @param message The detail message.
     */

    public ApfloatConfigurationException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new apfloat configuration exception with the specified detail message and cause.
     *
     * @param message The detail message.
     * @param cause Originating cause of the exception.
     */

    public ApfloatConfigurationException(String message, Throwable cause)
    {
        super(message, cause);
    }

    private static final long serialVersionUID = -7022924635011038776L;
}
