package org.apfloat.internal;

import org.apfloat.spi.AdditionBuilder;
import org.apfloat.spi.AdditionStrategy;

/**
 * Creates additions for the specified radix and the <code>long</code> element type.<p>
 *
 * @since 1.7.0
 * @version 1.7.0
 * @author Mikko Tommila
 */

public class LongAdditionBuilder
    implements AdditionBuilder<Long>
{
    /**
     * Default constructor.
     */

    public LongAdditionBuilder()
    {
    }

    public AdditionStrategy<Long> createAddition(int radix)
    {
        AdditionStrategy<Long> additionStrategy = new LongAdditionStrategy(radix);
        return additionStrategy;
    }
}
