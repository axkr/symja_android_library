package com.duy.lang;

import static java.lang.Double.doubleToLongBits;

/**
 * Created by Duy on 2/20/2018.
 */

public class DDouble {
    /**
     * Returns a hash code for a {@code double} value; compatible with
     * {@code Double.hashCode()}.
     *
     * @param value the value to hash
     * @return a hash code value for a {@code double} value.
     * @since 1.8
     */
    public static int hashCode(double value) {
        long bits = doubleToLongBits(value);
        return (int) (bits ^ (bits >>> 32));
    }

}
