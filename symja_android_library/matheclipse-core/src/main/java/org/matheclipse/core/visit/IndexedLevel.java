package org.matheclipse.core.visit;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
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
 * A level specification for levels used in the function <code>MapIndexed</code>
 *  
 * Example: the nested list <code>{x,{y}}</code> has depth <code>3</code> 
 * 
 */
public class IndexedLevel {
	protected final BiFunction<IExpr, IExpr, IExpr> fFunction;

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
	 * An <code>expr</code> is interpreted as a <i>level specification</i> for the allowed levels in an AST.<br>
	 * If <code>expr</code> is a non-negative IInteger iValue set Level {1,iValue};<br>
	 * If <code>expr</code> is a negative IInteger iValue set Level {iValue, 0}; <br>
	 * If <code>expr</code> is a List {i0Value, i1Value} set Level {i0Value, i1Value};<br>
	 * 
	 * @param function
	 *            the function which should be applied for an element
	 * @param unevaledLevelExpr
	 *            the given <i>level specification</i>
	 * @param includeHeads
	 *            set to <code>true</code>, if the header of an AST expression should be included
	 * @throws MathException
	 *             if the <code>expr</code> is not a <i>level specification</i>
	 */
	public IndexedLevel(final BiFunction<IExpr, IExpr, IExpr> function, final IExpr unevaledLevelExpr,
			boolean includeHeads, final EvalEngine engine) {
		IExpr levelExpr = engine.evaluate(unevaledLevelExpr);
		fFromLevel = fToLevel = -1;
		fFromDepth = fToDepth = 0;
		this.fIncludeHeads = includeHeads;
		this.fFunction = function;
		if (levelExpr instanceof IInteger) {
			final IInteger value = (IInteger) levelExpr;

			if (value.isNegative()) {
				fFromDepth = Integer.MIN_VALUE;
				fToDepth = Validate.checkIntType(value, Integer.MIN_VALUE);
				fFromLevel = 1;
				fToLevel = Integer.MAX_VALUE;
			} else {
				fToLevel = Validate.checkIntType(value, Integer.MIN_VALUE);
				fFromLevel = 1;
				fFromDepth = Integer.MIN_VALUE;
				fToDepth = -1;
			}
			return;
		}
		if (levelExpr.isList()) {
			final IAST lst = (IAST) levelExpr;

			if (lst.isAST1()) {
				if (lst.arg1() instanceof IInteger) {
					final IInteger i = (IInteger) lst.arg1();

					final int level = Validate.checkIntType(i, Integer.MIN_VALUE);
					if (i.isNegative()) {
						fFromDepth = level;
						fToDepth = level;
						fFromLevel = 0;
						fToLevel = Integer.MAX_VALUE;
					} else {
						fToLevel = level;
						fFromLevel = level;
						fFromDepth = Integer.MIN_VALUE;
						fToDepth = -1;
					}
					return;
				}
			} else {
				if ((lst.isAST2())) {
					if ((lst.arg1() instanceof IInteger) && (lst.arg2() instanceof IInteger)) {
						final IInteger i0 = (IInteger) lst.arg1();
						final IInteger i1 = (IInteger) lst.arg2();
						if (i0.isNegative() && i1.isNegative()) {
							fFromDepth = Validate.checkIntType(i0, Integer.MIN_VALUE);
							fToDepth = Validate.checkIntType(i1, Integer.MIN_VALUE);
							fFromLevel = 0;
							fToLevel = Integer.MAX_VALUE;
						} else if (i0.isNegative()) {
							// all subexpressions at levels i0 or above with a depth of -i1 or less.
							fFromDepth = Validate.checkIntType(i0, Integer.MIN_VALUE);
							fToDepth = -1;
							fFromLevel = 0;
							fToLevel = Validate.checkIntType(i1, Integer.MIN_VALUE);
						} else if (i1.isNegative()) {
							// all subexpressions at any level greater equal i0 that have a depth of -i1 or greater.
							fFromDepth = Integer.MIN_VALUE;
							fToDepth = Validate.checkIntType(i1, Integer.MIN_VALUE);
							fFromLevel = Validate.checkIntType(i0, Integer.MIN_VALUE);
							fToLevel = Integer.MAX_VALUE;
						} else {
							fFromDepth = Integer.MIN_VALUE;
							fToDepth = -1;
							fFromLevel = Validate.checkIntType(i0, Integer.MIN_VALUE);
							fToLevel = Validate.checkIntType(i1, Integer.MIN_VALUE);
						}
						return;
					} else if ((lst.arg1() instanceof IInteger) && (lst.arg2().isInfinity())) {
						final IInteger i0 = (IInteger) lst.arg1();
						if (i0.isNegative()) {
							throw new MathException("Invalid Level specification: " + levelExpr.toString());
						} else {
							fFromDepth = Integer.MIN_VALUE;
							fToDepth = -1;
							fFromLevel = Validate.checkIntType(i0, Integer.MIN_VALUE);
							fToLevel = Integer.MAX_VALUE;
						}
						return;
					} else if ((lst.arg1().isNegativeInfinity()) && (lst.arg2().isInfinity())) {
						// level specification {-Infinity, Infinity} is effectively the same as {0,-1}
						fFromDepth = Integer.MIN_VALUE;
						fToDepth = -1;
						fFromLevel = 0;
						fToLevel = Integer.MAX_VALUE;
						return;
					}
				}
			}
		}
		if (levelExpr.isInfinity() || levelExpr.equals(F.All)) {
			// level specification Infinity and -1 are equivalent
			fToLevel = Integer.MAX_VALUE;
			fFromLevel = 1;
			fFromDepth = Integer.MIN_VALUE;
			fToDepth = -1;
			return;
		}
		throw new MathException("Invalid Level specification: " + levelExpr.toString());
	} 

	public IndexedLevel(final BiFunction<IExpr, IExpr, IExpr> function, final int level,
			final boolean includeHeads) {
		this(function, level, level, includeHeads);
	} 

	public IndexedLevel(final BiFunction<IExpr, IExpr, IExpr> function, final int fromLevel, final int toLevel,
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
	public IndexedLevel(final BiFunction<IExpr, IExpr, IExpr> function, final int fromLevel, final int toLevel,
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

	public IExpr visitExpr(IExpr element, int[] indx) {
		if (isInRange(fCurrentLevel, -1)) {
			return fFunction.apply(element, createIndexes(indx));
		}
		return F.NIL;
	}

	public IExpr visitAST(IAST ast, int[] indx) {
		int[] minDepth = new int[] { 0 };
		IASTMutable[] result = new IASTMutable[] { F.NIL };
		try {
			int size = indx.length;
			final int[] newIndx = new int[size + 1];
			for (int j = 0; j < size; j++) {
				newIndx[j] = indx[j];
			}
			fCurrentLevel++;
			if (fIncludeHeads) {
				newIndx[size] = 0;
				IExpr element = ast.get(0);
				if (element.isAST()) {
					IExpr temp = visitAST((IAST) element, newIndx);
					if (temp.isPresent()) {
						element = temp;
					}
				}
				final IExpr temp = visitExpr(element, newIndx);
				if (temp.isPresent()) {
					if (!result[0].isPresent()) {
						result[0] = createResult(ast, temp);
					}
					result[0].set(0, temp);
				}
				if (fCurrentDepth < minDepth[0]) {
					minDepth[0] = fCurrentDepth;
				}
			}
			ast.forEach((x, i) -> {
				newIndx[size] = i;
				IExpr element = x;
				boolean evaled = false;
				if (element.isAST()) {
					IExpr temp = visitAST((IAST) element, newIndx);
					if (temp.isPresent()) {
						evaled = true;
						element = temp;
					}
				}
				final IExpr temp = visitExpr(element, newIndx);
				if (temp.isPresent()) {
					if (!result[0].isPresent()) {
						result[0] = createResult(ast, temp);
					}
					result[0].set(i, temp);
				} else if (evaled) {
					if (!result[0].isPresent()) {
						result[0] = createResult(ast, temp);
					}
					result[0].set(i, element);
				}
				if (fCurrentDepth < minDepth[0]) {
					minDepth[0] = fCurrentDepth;
				}
			});

			fCurrentDepth = --minDepth[0]; 
		} finally {
			fCurrentLevel--;
		}

		return result[0];
	}

	private IASTAppendable createIndexes(int[] indx) {
		IASTAppendable list = F.ListAlloc(indx.length);
		for (int i = 0; i < indx.length; i++) {
			list.append(F.ZZ(indx[i]));
		}
		return list;
	}

	public IASTMutable createResult(IAST ast, final IExpr x) {
		return ast.copy();
	}
}