package com.duy.lang;

/**
 * Created by Duy on 2/21/2018.
 */

public class DMath {

    /**
     * Returns the sum of its arguments,
     * throwing an exception if the result overflows an {@code int}.
     *
     * @param x the first value
     * @param y the second value
     * @return the result
     * @throws ArithmeticException if the result overflows an int
     * @since 1.8
     */
    public static int addExact(int x, int y) {
        int r = x + y;
        // HD 2-12 Overflow iff both arguments have the opposite sign of the result
        if (((x ^ r) & (y ^ r)) < 0) {
            throw new ArithmeticException("integer overflow");
        }
        return r;
    }

    /**
     * Returns the sum of its arguments,
     * throwing an exception if the result overflows a {@code long}.
     *
     * @param x the first value
     * @param y the second value
     * @return the result
     * @throws ArithmeticException if the result overflows a long
     * @since 1.8
     */
    public static long addExact(long x, long y) {
        long r = x + y;
        // HD 2-12 Overflow iff both arguments have the opposite sign of the result
        if (((x ^ r) & (y ^ r)) < 0) {
            throw new ArithmeticException("long overflow");
        }
        return r;
    }

    /**
     * Returns the difference of the arguments,
     * throwing an exception if the result overflows an {@code int}.
     *
     * @param x the first value
     * @param y the second value to subtract from the first
     * @return the result
     * @throws ArithmeticException if the result overflows an int
     * @since 1.8
     */
    public static int subtractExact(int x, int y) {
        int r = x - y;
        // HD 2-12 Overflow iff the arguments have different signs and
        // the sign of the result is different than the sign of x
        if (((x ^ y) & (x ^ r)) < 0) {
            throw new ArithmeticException("integer overflow");
        }
        return r;
    }

    /**
     * Returns the difference of the arguments,
     * throwing an exception if the result overflows a {@code long}.
     *
     * @param x the first value
     * @param y the second value to subtract from the first
     * @return the result
     * @throws ArithmeticException if the result overflows a long
     * @since 1.8
     */
    public static long subtractExact(long x, long y) {
        long r = x - y;
        // HD 2-12 Overflow iff the arguments have different signs and
        // the sign of the result is different than the sign of x
        if (((x ^ y) & (x ^ r)) < 0) {
            throw new ArithmeticException("long overflow");
        }
        return r;
    }

    /**
     * Returns the product of the arguments,
     * throwing an exception if the result overflows an {@code int}.
     *
     * @param x the first value
     * @param y the second value
     * @return the result
     * @throws ArithmeticException if the result overflows an int
     * @since 1.8
     */
    public static int multiplyExact(int x, int y) {
        long r = (long) x * (long) y;
        if ((int) r != r) {
            throw new ArithmeticException("integer overflow");
        }
        return (int) r;
    }

    /**
     * Returns the product of the arguments,
     * throwing an exception if the result overflows a {@code long}.
     *
     * @param x the first value
     * @param y the second value
     * @return the result
     * @throws ArithmeticException if the result overflows a long
     * @since 1.8
     */
    public static long multiplyExact(long x, long y) {
        long r = x * y;
        long ax = Math.abs(x);
        long ay = Math.abs(y);
        if (((ax | ay) >>> 31 != 0)) {
            // Some bits greater than 2^31 that might cause overflow
            // Check the result using the divide operator
            // and check for the special case of Long.MIN_VALUE * -1
            if (((y != 0) && (r / y != x)) ||
                    (x == Long.MIN_VALUE && y == -1)) {
                throw new ArithmeticException("long overflow");
            }
        }
        return r;
    }

    /**
     * Returns the argument incremented by one, throwing an exception if the
     * result overflows an {@code int}.
     *
     * @param a the value to increment
     * @return the result
     * @throws ArithmeticException if the result overflows an int
     * @since 1.8
     */
    public static int incrementExact(int a) {
        if (a == Integer.MAX_VALUE) {
            throw new ArithmeticException("integer overflow");
        }

        return a + 1;
    }

    /**
     * Returns the argument incremented by one, throwing an exception if the
     * result overflows a {@code long}.
     *
     * @param a the value to increment
     * @return the result
     * @throws ArithmeticException if the result overflows a long
     * @since 1.8
     */
    public static long incrementExact(long a) {
        if (a == Long.MAX_VALUE) {
            throw new ArithmeticException("long overflow");
        }

        return a + 1L;
    }

    /**
     * Returns the argument decremented by one, throwing an exception if the
     * result overflows an {@code int}.
     *
     * @param a the value to decrement
     * @return the result
     * @throws ArithmeticException if the result overflows an int
     * @since 1.8
     */
    public static int decrementExact(int a) {
        if (a == Integer.MIN_VALUE) {
            throw new ArithmeticException("integer overflow");
        }

        return a - 1;
    }

    /**
     * Returns the argument decremented by one, throwing an exception if the
     * result overflows a {@code long}.
     *
     * @param a the value to decrement
     * @return the result
     * @throws ArithmeticException if the result overflows a long
     * @since 1.8
     */
    public static long decrementExact(long a) {
        if (a == Long.MIN_VALUE) {
            throw new ArithmeticException("long overflow");
        }

        return a - 1L;
    }

    /**
     * Returns the negation of the argument, throwing an exception if the
     * result overflows an {@code int}.
     *
     * @param a the value to negate
     * @return the result
     * @throws ArithmeticException if the result overflows an int
     * @since 1.8
     */
    public static int negateExact(int a) {
        if (a == Integer.MIN_VALUE) {
            throw new ArithmeticException("integer overflow");
        }

        return -a;
    }

    /**
     * Returns the negation of the argument, throwing an exception if the
     * result overflows a {@code long}.
     *
     * @param a the value to negate
     * @return the result
     * @throws ArithmeticException if the result overflows a long
     * @since 1.8
     */
    public static long negateExact(long a) {
        if (a == Long.MIN_VALUE) {
            throw new ArithmeticException("long overflow");
        }

        return -a;
    }

    /**
     * Returns the value of the {@code long} argument;
     * throwing an exception if the value overflows an {@code int}.
     *
     * @param value the long value
     * @return the argument as an int
     * @throws ArithmeticException if the {@code argument} overflows an int
     * @since 1.8
     */
    public static int toIntExact(long value) {
        if ((int) value != value) {
            throw new ArithmeticException("integer overflow");
        }
        return (int) value;
    }

    /**
     * Returns the largest (closest to positive infinity)
     * {@code int} value that is less than or equal to the algebraic quotient.
     * There is one special case, if the dividend is the
     * {@linkplain Integer#MIN_VALUE Integer.MIN_VALUE} and the divisor is {@code -1},
     * then integer overflow occurs and
     * the result is equal to the {@code Integer.MIN_VALUE}.
     * <p>
     * Normal integer division operates under the round to zero rounding mode
     * (truncation).  This operation instead acts under the round toward
     * negative infinity (floor) rounding mode.
     * The floor rounding mode gives different results than truncation
     * when the exact result is negative.
     * <ul>
     * <li>If the signs of the arguments are the same, the results of
     * {@code floorDiv} and the {@code /} operator are the same.  <br>
     * For example, {@code floorDiv(4, 3) == 1} and {@code (4 / 3) == 1}.</li>
     * <li>If the signs of the arguments are different,  the quotient is negative and
     * {@code floorDiv} returns the integer less than or equal to the quotient
     * and the {@code /} operator returns the integer closest to zero.<br>
     * For example, {@code floorDiv(-4, 3) == -2},
     * whereas {@code (-4 / 3) == -1}.
     * </li>
     * </ul>
     * <p>
     *
     * @param x the dividend
     * @param y the divisor
     * @return the largest (closest to positive infinity)
     * {@code int} value that is less than or equal to the algebraic quotient.
     * @throws ArithmeticException if the divisor {@code y} is zero
     * @see #floorMod(int, int)
     * @see #floor(double)
     * @since 1.8
     */
    public static int floorDiv(int x, int y) {
        int r = x / y;
        // if the signs are different and modulo not zero, round down
        if ((x ^ y) < 0 && (r * y != x)) {
            r--;
        }
        return r;
    }

    /**
     * Returns the largest (closest to positive infinity)
     * {@code long} value that is less than or equal to the algebraic quotient.
     * There is one special case, if the dividend is the
     * {@linkplain Long#MIN_VALUE Long.MIN_VALUE} and the divisor is {@code -1},
     * then integer overflow occurs and
     * the result is equal to the {@code Long.MIN_VALUE}.
     * <p>
     * Normal integer division operates under the round to zero rounding mode
     * (truncation).  This operation instead acts under the round toward
     * negative infinity (floor) rounding mode.
     * The floor rounding mode gives different results than truncation
     * when the exact result is negative.
     * <p>
     * For examples, see {@link #floorDiv(int, int)}.
     *
     * @param x the dividend
     * @param y the divisor
     * @return the largest (closest to positive infinity)
     * {@code long} value that is less than or equal to the algebraic quotient.
     * @throws ArithmeticException if the divisor {@code y} is zero
     * @see #floorMod(long, long)
     * @see #floor(double)
     * @since 1.8
     */
    public static long floorDiv(long x, long y) {
        long r = x / y;
        // if the signs are different and modulo not zero, round down
        if ((x ^ y) < 0 && (r * y != x)) {
            r--;
        }
        return r;
    }

    /**
     * Returns the floor modulus of the {@code int} arguments.
     * <p>
     * The floor modulus is {@code x - (floorDiv(x, y) * y)},
     * has the same sign as the divisor {@code y}, and
     * is in the range of {@code -abs(y) < r < +abs(y)}.
     * <p>
     * <p>
     * The relationship between {@code floorDiv} and {@code floorMod} is such that:
     * <ul>
     * <li>{@code floorDiv(x, y) * y + floorMod(x, y) == x}
     * </ul>
     * <p>
     * The difference in values between {@code floorMod} and
     * the {@code %} operator is due to the difference between
     * {@code floorDiv} that returns the integer less than or equal to the quotient
     * and the {@code /} operator that returns the integer closest to zero.
     * <p>
     * Examples:
     * <ul>
     * <li>If the signs of the arguments are the same, the results
     * of {@code floorMod} and the {@code %} operator are the same.  <br>
     * <ul>
     * <li>{@code floorMod(4, 3) == 1}; &nbsp; and {@code (4 % 3) == 1}</li>
     * </ul>
     * <li>If the signs of the arguments are different, the results differ from the {@code %} operator.<br>
     * <ul>
     * <li>{@code floorMod(+4, -3) == -2}; &nbsp; and {@code (+4 % -3) == +1} </li>
     * <li>{@code floorMod(-4, +3) == +2}; &nbsp; and {@code (-4 % +3) == -1} </li>
     * <li>{@code floorMod(-4, -3) == -1}; &nbsp; and {@code (-4 % -3) == -1 } </li>
     * </ul>
     * </li>
     * </ul>
     * <p>
     * If the signs of arguments are unknown and a positive modulus
     * is needed it can be computed as {@code (floorMod(x, y) + abs(y)) % abs(y)}.
     *
     * @param x the dividend
     * @param y the divisor
     * @return the floor modulus {@code x - (floorDiv(x, y) * y)}
     * @throws ArithmeticException if the divisor {@code y} is zero
     * @see #floorDiv(int, int)
     * @since 1.8
     */
    public static int floorMod(int x, int y) {
        int r = x - floorDiv(x, y) * y;
        return r;
    }

    /**
     * Returns the floor modulus of the {@code long} arguments.
     * <p>
     * The floor modulus is {@code x - (floorDiv(x, y) * y)},
     * has the same sign as the divisor {@code y}, and
     * is in the range of {@code -abs(y) < r < +abs(y)}.
     * <p>
     * <p>
     * The relationship between {@code floorDiv} and {@code floorMod} is such that:
     * <ul>
     * <li>{@code floorDiv(x, y) * y + floorMod(x, y) == x}
     * </ul>
     * <p>
     * For examples, see {@link #floorMod(int, int)}.
     *
     * @param x the dividend
     * @param y the divisor
     * @return the floor modulus {@code x - (floorDiv(x, y) * y)}
     * @throws ArithmeticException if the divisor {@code y} is zero
     * @see #floorDiv(long, long)
     * @since 1.8
     */
    public static long floorMod(long x, long y) {
        return x - floorDiv(x, y) * y;
    }

}
