package org.apfloat.internal;

/**
 * Exception indicating a backing storage failure.<p>
 *
 * For example, a large number can be stored on disk.
 * An <code>IOException</code> can be thrown in any of
 * the disk operations, for example if a file can't be
 * created, or if the disk is full.
 *
 * @since 1.5
 * @version 1.5
 * @author Mikko Tommila
 */

public class BackingStorageException
    extends ApfloatInternalException
{
    /**
     * Constructs a new apfloat backing storage exception with an empty detail message.
     */

    public BackingStorageException()
    {
    }

    /**
     * Constructs a new apfloat backing storage exception with the specified detail message.
     *
     * @param message The detail message.
     */

    public BackingStorageException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new apfloat backing storage exception with the specified detail message and cause.
     *
     * @param message The detail message.
     * @param cause Originating cause of the exception.
     */

    public BackingStorageException(String message, Throwable cause)
    {
        super(message, cause);
    }

    private static final long serialVersionUID = -7022924635011038776L;
}
