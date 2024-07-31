package org.matheclipse.core.numerics.utils;

public final class Constants {

    /**
     * The machine epsilon.
     */
    public static final double EPSILON = Math.ulp(1.0);

    /**
     * The closest {@code double} value to the square root of two.
     */
    public static final double SQRT2 = Math.sqrt(2.0);

    /**
     * The closest {@code double} value to the square root of three.
     */
    public static final double SQRT3 = Math.sqrt(3.0);

    /**
     * The closest {@code double} value to the square root of five.
     */
    public static final double SQRT5 = Math.sqrt(5.0);

    /**
     * The closest {@code double} value to the number 1 divided by the natural-base
     * logarithm of 2.
     */
    public static final double LOG2_INV = 1.4426950408889634073599;

    private Constants() {
    }
}
