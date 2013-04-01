package org.matheclipse.core.expression;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.commons.math3.fraction.BigFraction;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INumber;

/**
 *
 */
public class NumberUtil {
	public static final BigInteger MINUS_ONE = BigInteger.valueOf(-1);

	public static boolean isZero(final IExpr e) {
		if (e instanceof INumber) {
			return ((INumber) e).isZero();
		}
		return false;
	}

	public static boolean isZero(final BigFraction fraction) {
		return fraction.equals(BigFraction.ZERO);
	}

	public static BigFraction inverse(final BigFraction fraction) {
		return new BigFraction(fraction.getDenominator(), fraction.getNumerator());
	}

	public static boolean isOne(final IExpr i) {
		return (i instanceof IInteger) && ((IInteger) i).getBigNumerator().equals(BigInteger.ONE);
	}

	public static boolean isMinusOne(final IExpr i) {
		return (i instanceof IInteger) && ((IInteger) i).getBigNumerator().equals(MINUS_ONE);
	}

	public static boolean isLargerThan(BigInteger a, BigInteger b) {
		return (a.compareTo(b) > 0);
	}

	public static boolean isLessThan(BigInteger a, BigInteger b) {
		return (a.compareTo(b) < 0);
	}

	public static boolean isNegative(BigInteger a) {
		return (a.compareTo(BigInteger.ZERO) < 0);
	}

	public static boolean isPositive(BigInteger a) {
		return (a.compareTo(BigInteger.ZERO) > 0);
	}

	public static boolean isZero(BigInteger a) {
		return a.equals(BigInteger.ZERO);
	}

	public static boolean isOne(BigInteger a) {
		return a.equals(BigInteger.ONE);
	}

	public static boolean isMinusOne(BigInteger a) {
		return a.equals(MINUS_ONE);
	}

	public static boolean isEven(BigInteger a) {
		return !a.testBit(0);
	}

	public static boolean isOdd(BigInteger a) {
		return a.testBit(0);
	}

	public static long toLong(BigInteger a) throws ArithmeticException {
		long val = a.longValue();
		if (!a.equals(BigInteger.valueOf(val))) {
			throw new ArithmeticException("toLong: number to large");
		}
		return val;
	}

	public static int toInt(BigInteger a) throws ArithmeticException {
		int val = a.intValue();
		if (!a.equals(BigInteger.valueOf(val))) {
			throw new ArithmeticException("toInt: number to large");
		}
		return val;
	}
 
	/**
	 * Compares the absolute value of two rational numbers.
	 * 
	 * @param that
	 *          the rational number to be compared with.
	 * @return <code>|this| > |that|</code>
	 */
	public static boolean isLargerThan(BigFraction a, BigFraction b) {
		return a.compareTo(b) > 0;
	}

	public static boolean isNegative(BigFraction a) {
		return a.compareTo(BigFraction.ZERO) < 0;
	}

	public static boolean isPositive(BigFraction a) {
		return a.compareTo(BigFraction.ZERO) > 0;
	}

	/**
	 * Floor, round towards negative infinity.
	 * <P>
	 * Possible loss of precision.
	 */
	public static BigInteger floor(BigFraction f) {
		// [rounding step, possible loss of precision step]
		return round(f, BigDecimal.ROUND_FLOOR);
	}

	/**
	 * Ceiling, round towards positive infinity.
	 * <P>
	 * Possible loss of precision.
	 */
	public static BigInteger ceiling(BigFraction f) {
		// [rounding step, possible loss of precision step]
		return round(f, BigDecimal.ROUND_CEILING);
	}

	/**
	 * Truncate, round towards zero.
	 * <P>
	 * Possible loss of precision.
	 */
	public static BigInteger trunc(BigFraction f) {
		// [rounding step, possible loss of precision step]
		return round(f, BigDecimal.ROUND_DOWN);
	}

	/**
	 * Integer part.
	 * <P>
	 * Possible loss of precision.
	 */
	public static BigInteger integerPart(BigFraction f) {
		// [rounding step, possible loss of precision step]
		return round(f, BigDecimal.ROUND_DOWN);
	}

	/**
	 * Fractional part.
	 * <P>
	 * Possible loss of precision.
	 */
	public static BigFraction fractionalPart(BigFraction f) {
		// this==ip+fp; sign(fp)==sign(this)
		// [possible loss of precision step]
		return f.subtract(new BigFraction(integerPart(f), BigInteger.ONE));
	}

	/**
	 * Round.
	 * <P>
	 * Round mode is one of {
	 * <CODE>ROUND_UP, ROUND_DOWN, ROUND_CEILING, ROUND_FLOOR,
	 * ROUND_HALF_UP, ROUND_HALF_DOWN, ROUND_HALF_EVEN,
	 * ROUND_HALF_CEILING, ROUND_HALF_FLOOR, ROUND_HALF_ODD,
	 * ROUND_UNNECESSARY, DEFAULT_ROUND_MODE (==ROUND_HALF_UP)</CODE> .
	 * <P>
	 * If rounding isn't necessary, i.e. this BigRational is an integer, [as an
	 * optimization] this BigRational is returned.
	 * <P>
	 * Possible loss of precision.
	 */
	// @PrecisionLoss
	public static BigInteger round(BigFraction f, int roundMode) {
		// [rounding step, possible loss of precision step]
		return roundToBigInteger(f, roundMode);
	}

	/**
	 * Round to BigInteger helper function. Internally used.
	 * <P>
	 * Possible loss of precision.
	 */
	// @PrecisionLoss
	private static BigInteger roundToBigInteger(BigFraction f, int roundMode) {
		// note: remainder and its duplicate are calculated for all cases.

		BigInteger n = f.getNumerator();
		final BigInteger q = f.getDenominator();

		final int sgn = n.signum();

		// optimization
		if (sgn == 0) {
			// [typically not reached due to earlier test for integers]
			return BigInteger.ZERO;
		}

		// keep info on the sign
		final boolean pos = (sgn > 0);

		// operate on positive values
		if (!pos) {
			n = n.negate();
		}

		final BigInteger[] divrem = n.divideAndRemainder(q);
		BigInteger dv = divrem[0];
		final BigInteger r = divrem[1];

		// return if we don't need to round, independent of rounding mode
		if (r.equals(BigInteger.ZERO)) {
			// [typically not reached since remainder is not zero
			// with normalized that are not integerp]
			if (!pos) {
				dv = dv.negate();
			}

			return dv;
		}

		boolean up = false;
		final int comp = r.multiply(BigInteger.valueOf(2L)).compareTo(q);

		switch (roundMode) {

		// Rounding mode to round away from zero.
		case BigDecimal.ROUND_UP:
			up = true;
			break;

		// Rounding mode to round towards zero.
		case BigDecimal.ROUND_DOWN:
			up = false;
			break;

		// Rounding mode to round towards positive infinity.
		case BigDecimal.ROUND_CEILING:
			up = pos;
			break;

		// Rounding mode to round towards negative infinity.
		case BigDecimal.ROUND_FLOOR:
			up = !pos;
			break;

		// Rounding mode to round towards "nearest neighbor" unless both
		// neighbors are equidistant, in which case round up.
		case BigDecimal.ROUND_HALF_UP:
			up = (comp >= 0);
			break;

		// Rounding mode to round towards "nearest neighbor" unless both
		// neighbors are equidistant, in which case round down.
		case BigDecimal.ROUND_HALF_DOWN:
			up = (comp > 0);
			break;

		// case BigDecimal.ROUND_HALF_CEILING:
		// up = (comp != 0 ? comp > 0 : pos);
		// break;
		//
		// case BigDecimal.ROUND_HALF_FLOOR:
		// up = (comp != 0 ? comp > 0 : !pos);
		// break;

		// Rounding mode to round towards the "nearest neighbor" unless both
		// neighbors are equidistant, in which case, round towards the even
		// neighbor.
		case BigDecimal.ROUND_HALF_EVEN:
			up = (comp != 0 ? comp > 0 : !dv.remainder(BigInteger.valueOf(2L)).equals(BigInteger.ZERO));
			break;

		// case BigDecimal.ROUND_HALF_ODD:
		// up = (comp != 0 ? comp > 0 :
		// bigIntegerIsZero(dv.remainder(BIG_INTEGER_TWO)));
		// break;

		// Rounding mode to assert that the requested operation has an exact
		// result, hence no rounding is necessary. If this rounding mode is
		// specified on an operation that yields an inexact result, an
		// ArithmeticException is thrown.
		// case ROUND_UNNECESSARY:
		// if (!r.isZero()) {
		// throw new ArithmeticException("rounding necessary");
		// }
		// // [typically not reached due to earlier test for integerp]
		// up = false;
		// break;

		default:
			throw new IllegalArgumentException("unsupported rounding mode");
		}

		if (up) {
			dv = dv.add(BigInteger.ONE);
		}

		if (!pos) {
			dv = dv.negate();
		}

		// [rounding step, possible loss of precision step]
		return dv;
	}

}
