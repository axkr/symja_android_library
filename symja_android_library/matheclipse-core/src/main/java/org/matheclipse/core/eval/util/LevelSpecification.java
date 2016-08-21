package org.matheclipse.core.eval.util;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.parser.client.math.MathException;

/**
 * Level specification for expression trees
 */
public class LevelSpecification extends LevelSpec {

	/**
	 * Create a LevelSpecification with level from 0 to Integer.MAX_VALUE
	 * 
	 */
	public LevelSpecification() {
		super();
		fFromLevel = 0;
		fToLevel = Integer.MAX_VALUE;
		fFromDepth = Integer.MIN_VALUE;
		fToDepth = -1;
	}

	/**
	 * Create a LevelSpecification from an IInteger or IAST list-object.<br>
	 * <br>
	 * 
	 * If <code>expr</code> is a non-negative IInteger iValue set Level
	 * {1,iValue};<br>
	 * If <code>expr</code> is a negative IInteger iValue set Level {iValue, 0};<br>
	 * If <code>expr</code> is a List {i0Value, i1Value} set Level {i0Value,
	 * i1Value};<br>
	 * 
	 * @param expr
	 * @param includeHeads
	 *          TODO
	 * @throws MathException
	 *           if the expr is not a <i>level specification</i>
	 * @see
	 */
	public LevelSpecification(final IExpr expr, boolean includeHeads) {
		super(0, includeHeads);
		fFromLevel = fToLevel = -1;
		fFromDepth = fToDepth = 0;
		if (expr instanceof IInteger) {
			final IInteger value = (IInteger) expr;

			if (value.isNegative()) {
				fFromDepth = Integer.MIN_VALUE;
				fToDepth = value.toBigNumerator().intValue();
				fFromLevel = 1;
				fToLevel = Integer.MAX_VALUE;
			} else {
				fToLevel = value.toBigNumerator().intValue();
				fFromLevel = 1;
				fFromDepth = Integer.MIN_VALUE;
				fToDepth = -1;
			}
			return;
		}
		if (expr.isList()) {
			final IAST lst = (IAST) expr;

			if (lst.isAST1()) {
				if (lst.arg1() instanceof IInteger) {
					final IInteger i = (IInteger) lst.arg1() ;

					if (i.isNegative()) {
						fFromDepth = i.toBigNumerator().intValue();
						fToDepth = i.toBigNumerator().intValue();
						fFromLevel = 0;
						fToLevel = Integer.MAX_VALUE;
						if (fToDepth < fFromDepth) {
							throw new MathException("Invalid Level specification: " + expr.toString());
						}
					} else {
						fToLevel = i.toBigNumerator().intValue();
						fFromLevel = i.toBigNumerator().intValue();
						fFromDepth = Integer.MIN_VALUE;
						fToDepth = -1;
						if (fToLevel < fFromLevel) {
							throw new MathException("Invalid Level specification: " + expr.toString());
						}
					}
					return;
				}
			} else {
				if ((lst.isAST2())) {
					if ((lst.arg1()  instanceof IInteger) && (lst.arg2()  instanceof IInteger)) {
						final IInteger i0 = (IInteger) lst.arg1() ;
						final IInteger i1 = (IInteger) lst.arg2() ;
						if (i0.isNegative() && i1.isNegative()) {
							fFromDepth = i0.toBigNumerator().intValue();
							fToDepth = i1.toBigNumerator().intValue();
							fFromLevel = 0;
							fToLevel = Integer.MAX_VALUE;
						} else if (i0.isNegative()) {
							throw new MathException("Invalid Level specification: " + expr.toString());
						} else if (i1.isNegative()) {
							fFromDepth = Integer.MIN_VALUE;
							fToDepth = i1.toBigNumerator().intValue();
							fFromLevel = i0.toBigNumerator().intValue();
							fToLevel = Integer.MAX_VALUE;
						} else {
							fFromDepth = Integer.MIN_VALUE;
							fToDepth = -1;
							fFromLevel = i0.toBigNumerator().intValue();
							fToLevel = i1.toBigNumerator().intValue();
						}
						return;
					} else if ((lst.arg1()  instanceof IInteger) && (lst.arg2() .isInfinity())) {
						final IInteger i0 = (IInteger) lst.arg1() ;
						if (i0.isNegative()) {
							throw new MathException("Invalid Level specification: " + expr.toString());
						} else {
							fFromDepth = Integer.MIN_VALUE;
							fToDepth = -1;
							fFromLevel = i0.toBigNumerator().intValue();
							fToLevel = Integer.MAX_VALUE;
						}
						return;
					}
				}
			}
		}
		if (expr.equals(F.CInfinity)) {
			fToLevel = Integer.MAX_VALUE;
			fFromLevel = 1;
			fFromDepth = Integer.MIN_VALUE;
			fToDepth = -1;
			return;
		}
		throw new MathException("Invalid Level specification: " + expr.toString());
	}

	/**
	 * Create a LevelSpecification with only the given level
	 * 
	 */
	public LevelSpecification(final int level) {
		this(level, level);
	}

	/**
	 * Create a LevelSpecification with the given level range
	 * 
	 */
	public LevelSpecification(final int levelFrom, final int levelTo) {
		super();
		fFromLevel = levelFrom;
		fToLevel = levelTo;
		fFromDepth = Integer.MIN_VALUE;
		fToDepth = -1;
	}

	/**
	 * Get the low level-limit
	 * 
	 * @return the <code>from</code> value of the level instance
	 */
	public int getFrom() {
		return fFromLevel;
	}

	/**
	 * Get the high level-limit.
	 * 
	 * @return the <code>to</code> value of the level instance
	 */
	public int getTo() {
		return fToLevel;
	}

	public final boolean includesDepth(int i) {
		i *= -1;
		return ((fFromDepth <= i) && (fToDepth >= i));
	}

	public final boolean includesLevel(final int i) {
		return ((fFromLevel <= i) && (fToLevel >= i));
	}

	public final int compareDepth(int i) {
		i *= -1;
		if (fFromDepth > i) {
			return -1;
		}
		if (fToDepth < i) {
			return 1;
		}
		return 0;
	}

	public final int compareLevel() {
		return compareLevel(fCurrentLevel);
	}

	public final int compareLevel(final int i) {
		if (fFromLevel > i) {
			return -1;
		}
		if (fToLevel < i) {
			return 1;
		}
		return 0;
	}

	/**
	 * @param i
	 */
	public void setFrom(final int i) {
		fFromLevel = i;
	}

	/**
	 * @param i
	 */
	public void setTo(final int i) {
		fToLevel = i;
	}
}
