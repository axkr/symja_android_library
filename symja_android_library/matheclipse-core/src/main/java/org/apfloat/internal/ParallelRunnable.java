package org.apfloat.internal;

import java.util.concurrent.atomic.AtomicLong;

import org.apfloat.spi.Util;

/**
 * Abstract class for a <code>Runnable</code> that can be run in parallel by
 * multiple threads. Internally, the <code>ParallelRunnable</code> splits the
 * work to many small batches, which are run one at a time, and can be run in
 * parallel by multiple threads. The <code>ParallelRunnable</code> isn't
 * completed until all batches are completed, i.e. the {@link #run()} method
 * only returns when all batches are completed.
 *
 * @since 1.1
 * @version 1.8.0
 * @author Mikko Tommila
 */

public abstract class ParallelRunnable
    implements Runnable
{
    /**
     * Subclass constructor.
     */

    protected ParallelRunnable(long length)
    {
        // Set the batch size to be some balanced value with respect to the batch size and the number of batches
        this.preferredBatchSize = Util.sqrt4down(length);
        this.length = length;
        this.started = new AtomicLong();
        this.completed = new AtomicLong();
    }

    /**
     * Repeatedly get a batch of work and run it, until all batches are
     * completed. This method can (and should) be called from multiple
     * threads in parallel.
     */

    public final void run()
    {
        // Run batches as long as there are any available
        while (runBatch());

        // Wait until all batches are completed (the above only says all batches were started)
        // Note that accessing this atomic variable also ensures that memory writes in other
        // threads have happened-before we get here and see that the task is completed
        while (this.completed.get() < this.length)
        {
            Thread.yield();     // Do not waste time
        }
    }

    /**
     * Run one batch if available. Returns <code>true</code> if a batch was
     * actually acquired and run, <code>false</code> if all batches were
     * already started and none could be run. This method can be used by any
     * thread to steal and complete a minimal amount of work.<p>
     *
     * Note that if a batch could not be run, it does not mean that all of
     * the batches are already completed - some could still be running.
     *
     * @return If a batch was actually run.
     */

    public final boolean runBatch()
    {
        boolean isRun = false;
        if (this.started.get() < this.length)
        {
            long batchSize = Math.max(MINIMUM_BATCH_SIZE, getPreferredBatchSize());
            long startValue = this.started.getAndAdd(batchSize);
            long length = Math.min(batchSize, this.length - startValue);
            if (length > 0)
            {
                Runnable runnable = getRunnable(startValue, length);
                runnable.run();
                // This ensures that all memory writes in the Runnable happen-before other threads can see that the batch was completed
                this.completed.addAndGet(length);
                isRun = true;
            }
        }
        return isRun;
    }

    /**
     * Get the Runnable object for strides which fit in an <code>int</code>.
     *
     * @param startValue The starting value for the stride.
     * @param length The length of the stride.
     *
     * @return The Runnable object for the specified stride.
     */

    protected Runnable getRunnable(int startValue, int length)
    {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Get the Runnable object for strides which fit only in a <code>long</code>.
     *
     * @param startValue The starting value for the stride.
     * @param length The length of the stride.
     *
     * @return The Runnable object for the specified stride.
     */

    protected Runnable getRunnable(long startValue, long length)
    {
        if (startValue <= Integer.MAX_VALUE - length)   // Avoid overflow
        {
            return getRunnable((int) startValue, (int) length);
        }
        else
        {
            throw new UnsupportedOperationException("Not implemented");
        }
    }

    /**
     * Get the preferred batch size.
     *
     * @return The preferred batch size.
     *
     * @since 1.7.0
     */

    protected long getPreferredBatchSize()
    {
        return this.preferredBatchSize;
    }

    private static final int MINIMUM_BATCH_SIZE = 16;

    private long length;
    private long preferredBatchSize;
    private AtomicLong started;
    private AtomicLong completed;
}
