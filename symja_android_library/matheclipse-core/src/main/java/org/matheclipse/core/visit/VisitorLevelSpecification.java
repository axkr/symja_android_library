package org.matheclipse.core.visit;

import java.util.function.Function;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IPatternSequence;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.math.MathException;

/**
 * A level specification visitor for levels in abstract syntax trees (AST).
 * 
 * Example: the nested list <code>{x,{y}}</code> has depth <code>3</code>
 * 
 */
public class VisitorLevelSpecification extends AbstractVisitor {
	protected final Function<IExpr, IExpr> fFunction;

	protected int fFromLevel;

	protected int fToLevel;

	protected int fFromDepth;

	protected int fToDepth;

	protected final boolean fIncludeHeads;

	protected int fCurrentLevel;

	protected int fCurrentDepth;

	/**
	 * Create a LevelSpecification from an IInteger or IAST list-object.<br>
	 * <br>
	 * 
	 * An <code>expr</code> is interpreted as a <i>level specification</i> for
	 * the allowed levels in an AST.<br>
	 * If <code>expr</code> is a non-negative IInteger iValue set Level
	 * {1,iValue};<br>
	 * If <code>expr</code> is a negative IInteger iValue set Level {iValue, 0};
	 * <br>
	 * If <code>expr</code> is a List {i0Value, i1Value} set Level {i0Value,
	 * i1Value};<br>
	 * 
	 * @param function
	 *            the function which should be applied for an element
	 * @param expr
	 *            the given <i>level specification</i>
	 * @param includeHeads
	 *            set to <code>true</code>, if the header of an AST expression
	 *            should be included
	 * @throws MathException
	 *             if the <code>expr</code> is not a <i>level specification</i>
	 * @see
	 */
	public VisitorLevelSpecification(final Function<IExpr, IExpr> function, final IExpr expr, boolean includeHeads) {
		IExpr levelExpr = F.eval(expr);
		fFromLevel = fToLevel = -1;
		fFromDepth = fToDepth = 0;
		this.fIncludeHeads = includeHeads;
		this.fFunction = function;
		if (levelExpr instanceof IInteger) {
			final IInteger value = (IInteger) levelExpr;

			if (value.isNegative()) {
				fFromDepth = Integer.MIN_VALUE;
				fToDepth = Validate.checkIntType(value, Integer.MIN_VALUE);// value.getBigNumerator().intValue();
				fFromLevel = 1;
				fToLevel = Integer.MAX_VALUE;
			} else {
				fToLevel = Validate.checkIntType(value, Integer.MIN_VALUE);// value.getBigNumerator().intValue();
				fFromLevel = 1;
				fFromDepth = Integer.MIN_VALUE;
				fToDepth = -1;
			}
			return;
		}
		if (levelExpr.isList()) {
			final IAST lst = (IAST) levelExpr;

			if (lst.size() == 2) {
				if (lst.arg1() instanceof IInteger) {
					final IInteger i = (IInteger) lst.arg1();

					if (i.isNegative()) {
						fFromDepth = Validate.checkIntType(i, Integer.MIN_VALUE);// i.getBigNumerator().intValue();
						fToDepth = Validate.checkIntType(i, Integer.MIN_VALUE);// i.getBigNumerator().intValue();
						fFromLevel = 0;
						fToLevel = Integer.MAX_VALUE;
						if (fToDepth < fFromDepth) {
							throw new MathException("Invalid Level specification: " + levelExpr.toString());
						}
					} else {
						fToLevel = Validate.checkIntType(i, Integer.MIN_VALUE);// i.getBigNumerator().intValue();
						fFromLevel = Validate.checkIntType(i, Integer.MIN_VALUE); // i.getBigNumerator().intValue();
						fFromDepth = Integer.MIN_VALUE;
						fToDepth = -1;
						if (fToLevel < fFromLevel) {
							throw new MathException("Invalid Level specification: " + levelExpr.toString());
						}
					}
					return;
				}
			} else {
				if ((lst.size() == 3)) {
					if ((lst.arg1() instanceof IInteger) && (lst.arg2() instanceof IInteger)) {
						final IInteger i0 = (IInteger) lst.arg1();
						final IInteger i1 = (IInteger) lst.arg2();
						if (i0.isNegative() && i1.isNegative()) {
							fFromDepth = Validate.checkIntType(i0, Integer.MIN_VALUE);// i0.getBigNumerator().intValue();
							fToDepth = Validate.checkIntType(i1, Integer.MIN_VALUE); // i1.getBigNumerator().intValue();
							fFromLevel = 0;
							fToLevel = Integer.MAX_VALUE;
						} else if (i0.isNegative()) {
							throw new MathException("Invalid Level specification: " + levelExpr.toString());
						} else if (i1.isNegative()) {
							fFromDepth = Integer.MIN_VALUE;
							fToDepth = Validate.checkIntType(i1, Integer.MIN_VALUE);// i1.getBigNumerator().intValue();
							fFromLevel = Validate.checkIntType(i0, Integer.MIN_VALUE);// i0.getBigNumerator().intValue();
							fToLevel = Integer.MAX_VALUE;
						} else {
							fFromDepth = Integer.MIN_VALUE;
							fToDepth = -1;
							fFromLevel = Validate.checkIntType(i0, Integer.MIN_VALUE);// i0.getBigNumerator().intValue();
							fToLevel = Validate.checkIntType(i1, Integer.MIN_VALUE);// i1.getBigNumerator().intValue();
						}
						return;
					} else if ((lst.arg1() instanceof IInteger) && (lst.arg2().isInfinity())) {
						final IInteger i0 = (IInteger) lst.arg1();
						if (i0.isNegative()) {
							throw new MathException("Invalid Level specification: " + levelExpr.toString());
						} else {
							fFromDepth = Integer.MIN_VALUE;
							fToDepth = -1;
							fFromLevel = Validate.checkIntType(i0, Integer.MIN_VALUE);// i0.getBigNumerator().intValue();
							fToLevel = Integer.MAX_VALUE;
						}
						return;
					}
				}
			}
		}
		if (levelExpr.equals(F.CInfinity)) {
			fToLevel = Integer.MAX_VALUE;
			fFromLevel = 1;
			fFromDepth = Integer.MIN_VALUE;
			fToDepth = -1;
			return;
		}
		throw new MathException("Invalid Level specification: " + levelExpr.toString());
	}

	/**
	 * Define a level specification for all elements on level <code>level</code>
	 * .
	 * 
	 * @param level
	 */
	public VisitorLevelSpecification(final Function<IExpr, IExpr> function, final int level) {
		this(function, level, true);
	}

	public VisitorLevelSpecification(final Function<IExpr, IExpr> function, final int level,
			final boolean includeHeads) {
		this(function, level, level, includeHeads);
	}

	public VisitorLevelSpecification(final Function<IExpr, IExpr> function, final int fromLevel, final int toLevel) {
		this(function, fromLevel, toLevel, true);
	}

	public VisitorLevelSpecification(final Function<IExpr, IExpr> function, final int fromLevel, final int toLevel,
			final boolean includeHeads) {
		this(function, fromLevel, toLevel, Integer.MIN_VALUE, -1, includeHeads);
	}

	/**
	 * 
	 * Example value set for including all levels:<br>
	 * <code>fromLevel = 0;</code><br>
	 * <code>toLevel = Integer.MAX_VALUE;</code><br>
	 * <code>fromDepth = Integer.MIN_VALUE;</code><br>
	 * <code>toDepth = -1;</code><br>
	 * 
	 * @param function
	 *            the function which should be applied for an element
	 * @param fromLevel
	 * @param toLevel
	 * @param fromDepth
	 * @param toDepth
	 * @param includeHeads
	 */
	public VisitorLevelSpecification(final Function<IExpr, IExpr> function, final int fromLevel, final int toLevel,
			final int fromDepth, final int toDepth, final boolean includeHeads) {
		fFunction = function;
		fFromLevel = fromLevel;
		fToLevel = toLevel;
		fCurrentLevel = 0;
		fIncludeHeads = includeHeads;
		fFromDepth = fromDepth;
		fCurrentDepth = -1;
		fToDepth = toDepth;
	}

	public void incCurrentLevel() {
		fCurrentLevel++;
	}

	public void decCurrentLevel() {
		fCurrentLevel--;
	}

	public boolean isInRange(int level, int depth) {
		return (level >= fFromLevel) && (level <= fToLevel) && (depth >= fFromDepth) && (depth <= fToDepth);
	}

	/**
	 * 
	 */
	public IExpr visit(IInteger element) {
		fCurrentDepth = -1;
		if (isInRange(fCurrentLevel, -1)) {
			return fFunction.apply(element);
		}
		return F.NIL;
	}

	/**
	 * 
	 */
	public IExpr visit(IFraction element) {
		fCurrentDepth = -1;
		if (isInRange(fCurrentLevel, -1)) {
			return fFunction.apply(element);
		}
		return F.NIL;
	}

	/**
	 * 
	 */
	public IExpr visit(IComplex element) {
		fCurrentDepth = -1;
		if (isInRange(fCurrentLevel, -1)) {
			return fFunction.apply(element);
		}
		return F.NIL;
	}

	/**
	 * 
	 */
	public IExpr visit(INum element) {
		fCurrentDepth = -1;
		if (isInRange(fCurrentLevel, -1)) {
			return fFunction.apply(element);
		}
		return F.NIL;
	}

	/**
	 * 
	 */
	public IExpr visit(IComplexNum element) {
		fCurrentDepth = -1;
		if (isInRange(fCurrentLevel, -1)) {
			return fFunction.apply(element);
		}
		return F.NIL;
	}

	/**
	 * 
	 */
	public IExpr visit(ISymbol element) {
		fCurrentDepth = -1;
		if (isInRange(fCurrentLevel, -1)) {
			return fFunction.apply(element);
		}
		return F.NIL;
	}

	/**
	 * 
	 */
	public IExpr visit(IPattern element) {
		fCurrentDepth = -1;
		if (isInRange(fCurrentLevel, -1)) {
			return fFunction.apply(element);
		}
		return F.NIL;
	}

	/**
	 * 
	 */
	public IExpr visit(IPatternSequence element) {
		fCurrentDepth = -1;
		if (isInRange(fCurrentLevel, -1)) {
			return fFunction.apply(element);
		}
		return F.NIL;
	}

	/**
	 * 
	 */
	public IExpr visit(IStringX element) {
		fCurrentDepth = -1;
		if (isInRange(fCurrentLevel, -1)) {
			return fFunction.apply(element);
		}
		return F.NIL;
	}

	public IExpr visitExpr(IExpr element) {
		if (isInRange(fCurrentLevel, -1)) {
			return fFunction.apply(element);
		}
		return F.NIL;
	}

	public IExpr visit(IAST ast) {
		int minDepth = -1;
		IExpr temp;
		IAST result = F.NIL;
		try {
			fCurrentLevel++;
			if (fIncludeHeads) {
				temp = ast.get(0).accept(this);
				if (temp.isPresent()) {
					if (!result.isPresent()) {
						result = ast.copy();
					}
					result.set(0, temp);
				}
				if (fCurrentDepth < minDepth) {
					minDepth = fCurrentDepth;
				}
			}
			for (int i = 1; i < ast.size(); i++) {
				temp = ast.get(i).accept(this);
				if (temp.isPresent()) {
					if (!result.isPresent()) {
						result = ast.copy();
					}
					result.set(i, temp);
				}
				if (fCurrentDepth < minDepth) {
					minDepth = fCurrentDepth;
				}
			}
		} finally {
			fCurrentLevel--;
		}
		fCurrentDepth = --minDepth;
		if (isInRange(fCurrentLevel, minDepth)) {
			if (!result.isPresent()) {
				return fFunction.apply(ast);
			} else {
				temp = fFunction.apply(result);
				if (temp.isPresent()) {
					return temp;
				}
			}
		}
		return result;

	}
}