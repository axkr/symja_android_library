package org.apfloat.internal;

import org.apfloat.spi.AdditionBuilder;
import org.apfloat.spi.AdditionStrategy;

/**
 * Creates additions for the specified radix and the <code>double</code> element type.<p>
 *
 * @since 1.7.0
 * @version 1.7.0
 * @author Mikko Tommila
 */

public class DoubleAdditionBuilder
    implements AdditionBuilder<Double>
{
    /**
     * Default constructor.
     */

    public DoubleAdditionBuilder()
    {
    }

    public AdditionStrategy<Double> createAddition(int radix)
    {
        AdditionStrategy<Double> additionStrategy = new DoubleAdditionStrategy(radix);
        return additionStrategy;
    }
}
