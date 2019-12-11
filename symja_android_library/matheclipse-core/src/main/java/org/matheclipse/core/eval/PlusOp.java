package org.matheclipse.core.eval;

import java.util.HashMap;
import java.util.Map;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.expression.ASTSeriesData;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.IntervalSym;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.math.MathException;

import ch.ethz.idsc.tensor.qty.IQuantity;

/**
 * <p>
 * Plus operator for adding multiple arguments with the <code>plus(argument)</code> method and returning the result,
 * with the <code>getSum()</code> method, if <code>isEvaled()</code> returns <code>true</code>.
 * </p>
 * See: <a href="http://www.cs.berkeley.edu/~fateman/papers/newsimp.pdf">Experiments in Hash-coded Algebraic
 * Simplification</a>
 * 
 */
public final class PlusOp {
	/**
	 * Merge IExpr keys by adding their values into this map.
	 */
	private final Map<IExpr, IExpr> plusMap;

	/**
	 * <code>true</code> if plus was really evaluated
	 */
	private boolean evaled;

	/**
	 * The value of the addition of numbers.
	 */
	private IExpr numberValue;

	/**
	 * Constructor.
	 * 
	 * @param size
	 *            the approximated size of the resulting <code>Plus()</code> AST.
	 */
	public PlusOp(final int size) {
		plusMap = new HashMap<IExpr, IExpr>(size + 5 + size / 10);
		evaled = false;
		numberValue = null;
	}

	/**
	 * Add or merge the <code>key, value</code> pair into the given <code>plusMap</code>.
	 * 
	 * @param key
	 *            the key expression
	 * @param value
	 *            the value expression
	 */
	private boolean addMerge(final IExpr key, final IExpr value) {
		IExpr temp = plusMap.get(key);
		if (temp == null) {
			plusMap.put(key, value);
			return false;
		}
		// merge both values
		if (temp.isNumber() && value.isNumber()) {
			temp = temp.plus(value);
			if (temp.isZero()) {
				plusMap.remove(key);
				return true;
			}
		} else if (temp.head().equals(F.Plus)) {
			if (!(temp instanceof IASTAppendable)) {
				temp = ((IAST) temp).copyAppendable();
			}
			((IASTAppendable) temp).append(value);
		} else {
			temp = F.Plus(temp, value);
		}
		plusMap.put(key, temp);
		return true;
	}

	/**
	 * Get the current evaluated result of the summation as a <code>Plus()</code> expression with respecting the
	 * <code>OneIdentity</code> attribute.
	 * 
	 * @return
	 */
	public IExpr getSum() {

		IASTAppendable result = F.PlusAlloc(plusMap.size());
		if (numberValue != null && !numberValue.isZero()) {
			if (numberValue.isComplexInfinity()) {
				return numberValue;
			}
			result.append(numberValue);
		}
		for (Map.Entry<IExpr, IExpr> element : plusMap.entrySet()) {
			final IExpr temp = element.getKey();
			if (element.getValue().isOne()) {
				if (temp.isPlus()) {
					result.appendArgs((IAST) temp);
				} else {
					result.append(temp);
				}
				continue;
			}
			result.append(F.Times(element.getValue(), temp));
		}
		// result.addEvalFlags(IAST.IS_EVALED);
		return result.oneIdentity0();
	}

	/**
	 * Test if any evaluation occurred by calling the <code>plus()</code> method
	 * 
	 * @return <code>true</code> if an evaluation occurred.
	 */
	public boolean isEvaled() {
		return evaled;
	}

	private IExpr negativeInfinityPlus(final IExpr o1) {
		if (o1.isInfinity()) {
			EvalEngine.get().printMessage("Indeterminate expression Infinity-Infinity");
			return F.Indeterminate;
		} else if (o1.isNegativeInfinity()) {
			return F.CNInfinity;
		}
		return F.CNInfinity;
	}

	/**
	 * Add an argument <code>arg</code> to this <code>Plus()</code> expression.
	 * 
	 * @param arg
	 * @return <code>F.Indeterminate</code> if the result is indeterminated, <code>F.NIL</code> otherwise.
	 */
	public IExpr plus(final IExpr arg) {
		// if (arg.isPlus()) {
		// // flatten the Plus() argument
		// final IAST plusAST = (IAST) arg;
		// return plusUntilPosition(plusAST, plusAST.size());
		// }
		if (arg.isIndeterminate()) {
			return F.Indeterminate;
		}

		try {
			if (numberValue != null && numberValue.isDirectedInfinity()) {
				if (numberValue.isComplexInfinity()) {
					if (arg.isDirectedInfinity()) {
						return F.Indeterminate;
					}
					numberValue = F.CComplexInfinity;
					evaled = true;
					return F.NIL;
				} else if (numberValue.isInfinity()) {
					if (arg.isInfinity()) {
						evaled = true;
						return F.NIL;
					}
					if (arg.isDirectedInfinity()) {
						return F.Indeterminate;
					}
					if (arg.isRealResult()) {
						evaled = true;
						return F.NIL;
					}
				} else if (numberValue.isNegativeInfinity()) {
					if (arg.isNegativeInfinity()) {
						evaled = true;
						return F.NIL;
					}
					if (arg.isDirectedInfinity()) {
						return F.Indeterminate;
					}
					if (arg.isRealResult()) {
						evaled = true;
						return F.NIL;
					}
				}
			}

			if (arg.isNumber()) {
				if (arg.isZero()) {
					evaled = true;
					return F.NIL;
				}
				if (numberValue == null) {
					numberValue = arg;
					return F.NIL;
				}
				if (numberValue.isNumber()) {
					numberValue = numberValue.plus(arg);
					evaled = true;
					return F.NIL;
				}
				if (numberValue.isInfinity()) {
					if (arg.isNegativeInfinity()) {
						EvalEngine.get().printMessage("Indeterminate expression Infinity-Infinity");
						return F.Indeterminate;
					}
					numberValue = F.CInfinity;
					evaled = true;
					return F.NIL;
				}
				if (numberValue.isNegativeInfinity()) {
					numberValue = negativeInfinityPlus(arg);
					if (numberValue.isIndeterminate()) {
						return F.Indeterminate;
					}
					evaled = true;
					return F.NIL;
				}
				return F.NIL;
			} else if (arg.isAST()) {
				final IAST ast = (IAST) arg;
				final int headID = ((IAST) arg).headID();
				if (headID >= ID.DirectedInfinity) {
					switch (headID) {
					case ID.DirectedInfinity:
						if (arg.isDirectedInfinity()) {
							if (numberValue == null) {
								numberValue = arg;
								if (arg.isComplexInfinity()) {
									if (plusMap.size() > 0) {
										evaled = true;
									}
								}
								return F.NIL;
							}
							if (arg.isInfinity()) {
								if (numberValue.isNegativeInfinity()) {
									EvalEngine.get().printMessage("Indeterminate expression Infinity-Infinity");
									return F.Indeterminate;
								}
								numberValue = F.CInfinity;
								evaled = true;
								return F.NIL;
							} else if (arg.isNegativeInfinity()) {
								numberValue = negativeInfinityPlus(numberValue);
								if (numberValue.isIndeterminate()) {
									return F.Indeterminate;
								}
								evaled = true;
								return F.NIL;
							} else if (arg.isComplexInfinity()) {
								if (numberValue.isDirectedInfinity()) {
									return F.Indeterminate;
								}
								numberValue = F.CComplexInfinity;
								evaled = true;
								return F.NIL;
							}
						}
						break;
					case ID.Times:
						if (ast.size() > 1) {
							if (ast.arg1().isNumber()) {
								if (addMerge(ast.rest().oneIdentity1(), ast.arg1())) {
									evaled = true;
								}
								return F.NIL;
							}
							if (addMerge(ast, F.C1)) {
								evaled = true;
							}
						}
						return F.NIL;
					case ID.Interval:
						if (arg.isInterval()) {
							if (numberValue == null) {
								numberValue = arg;
								return F.NIL;
							}
							if (numberValue.isInterval()) {
								numberValue = IntervalSym.plus((IAST) numberValue,
										(IAST) arg);
							} else {
								numberValue = IntervalSym.plus(numberValue,
										(IAST) arg);
							}
							evaled = true;
							return F.NIL;
						}
						break;
					case ID.Quantity:
						if (arg.isQuantity()) {
							if (numberValue == null) {
								numberValue = arg;
								return F.NIL;
							}
							IQuantity q = (IQuantity) arg;
							numberValue = q.plus(numberValue);
							if (numberValue.isPresent()) {
								evaled = true;
							}
							return F.NIL;
						}
						break;
					case ID.SeriesData:
						if (arg instanceof ASTSeriesData) {
							if (numberValue == null) {
								numberValue = arg;
								return F.NIL;
							}
							numberValue = ((ASTSeriesData) arg).plus(numberValue);
							evaled = true;
							return F.NIL;
						}
						break;
					}
				}
			}
			if (addMerge(arg, F.C1)) {
				evaled = true;
			}
		} catch (MathException mex) {
			if (Config.SHOW_STACKTRACE) {
				mex.printStackTrace();
			}
		}
		return F.NIL;
	}

	// private static IExpr plusInterval(final IExpr o0, final IExpr o1) {
	// return F.Interval(F.List(o0.lower().plus(o1.lower()), o0.upper().plus(o1.upper())));
	// }
}
