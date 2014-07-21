package org.apfloat.internal;

import java.util.Map;
import java.util.HashMap;

import org.apfloat.ApfloatRuntimeException;

/**
 * Message passing helper class for parallel codes.
 * The message passer can hold one message for each receiver.
 * This class is safe for concurrent use from multiple threads.
 *
 * @param <K> The receiver type for this message passer.
 * @param <V> The message type for this message passer.
 *
 * @since 1.6
 * @version 1.6
 * @author Mikko Tommila
 */

public class MessagePasser<K, V>
{
    private Map<K, V> messages;

    /**
     * Default constructor.
     */

    public MessagePasser()
    {
        this.messages = new HashMap<K, V>();
    }

    /**
     * Send a message.
     *
     * @param receiver The receiver.
     * @param message The message. Must not be <code>null</code>.
     */

    public synchronized void sendMessage(K receiver, V message)
    {
        assert (message != null);
        assert (!this.messages.containsKey(receiver));

        this.messages.put(receiver, message);

        notifyAll();
    }

    /**
     * Get a message if one is available. This method will not block.
     *
     * @param receiver The receiver.
     *
     * @return The message, or <code>null</code> if none is available.
     */

    public synchronized V getMessage(K receiver)
    {
        V message = this.messages.remove(receiver);

        return message;
    }

    /**
     * Receive a message. This method will block until a message is available.
     *
     * @param receiver The receiver.
     *
     * @return The message.
     */

    public synchronized V receiveMessage(K receiver)
        throws ApfloatRuntimeException
    {
        V message;
        while ((message = this.messages.remove(receiver)) == null)
        {
            try
            {
                wait();
            }
            catch (InterruptedException ie)
            {
                throw new ApfloatInternalException("Wait for received message interrupted", ie);
            }
        }

        return message;
    }
}
