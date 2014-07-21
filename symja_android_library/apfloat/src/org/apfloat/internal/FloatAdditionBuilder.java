package org.apfloat.internal;

import org.apfloat.spi.AdditionBuilder;
import org.apfloat.spi.AdditionStrategy;

/**
 * Creates additions for the specified radix and the <code>float</code> element type.<p>
 *
 * @since 1.7.0
 * @version 1.7.0
 * @author Mikko Tommila
 */

public class FloatAdditionBuilder
    implements AdditionBuilder<Float>
{
    /**
     * Default constructor.
     */

    public FloatAdditionBuilder()
    {
    }

    public AdditionStrategy<Float> createAddition(int radix)
    {
        AdditionStrategy<Float> additionStrategy = new FloatAdditionStrategy(radix);
        return additionStrategy;
    }
}
