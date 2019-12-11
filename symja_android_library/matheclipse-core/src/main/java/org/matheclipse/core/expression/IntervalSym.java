package org.matheclipse.core.expression;

import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

public class IntervalSym {

	private static boolean isNormalized(final IAST ast) {
		return ast.isEvalFlagOn(IAST.BUILT_IN_EVALED);
	}

	private static IAST normalize(final IAST ast) {
		if (isNormalized(ast)) {
			return ast;
		}
		return normalize(ast, EvalEngine.get());
	}

	public static IAST normalize(final IAST ast, EvalEngine engine) {
		try {
			IASTAppendable result = ast.copyAppendable();
			boolean evaled = false;
			for (int i = 1; i < ast.size(); i++) {
				IAST temp = normalizeArgument(ast.get(i), engine);
				if (temp.isPresent()) {
					evaled = true;
					result.set(i, temp);
				}
			}
			EvalAttributes.sort(result);
			result.addEvalFlags(IAST.BUILT_IN_EVALED);
			if (ast.size() > 2) {
				int j = 1;
				IAST list1 = (IAST) result.arg1();
				IExpr min1 = list1.arg1();
				IExpr max1 = list1.arg2();
				int i = 2;
				while (i < result.size()) {
					IAST list2 = (IAST) result.get(i);
					IExpr min2 = list2.arg1();
					IExpr max2 = list2.arg2();
					if (min2.lessEqual(max1).isTrue()) {
						if (max2.lessEqual(max1).isTrue()) {
							evaled = true;
							result.remove(i);
							continue;
						} else {
							evaled = true;
							result.remove(i);
							list1 = F.List(min1, max2);
							max1 = max2;
							continue;
						}
					}
					result.set(j++, list1);
					list1 = list2;
					min1 = min2;
					max1 = max2;
					i++;
				}
				result.set(j, list1);
			}
			if (evaled) {
				return result;
			}
			if (ast instanceof IASTMutable) {
				EvalAttributes.sort((IASTMutable) ast);
				ast.addEvalFlags(IAST.BUILT_IN_EVALED);
			}
		} catch (RuntimeException rex) {
			engine.printMessage("Interval: " + rex.getMessage());
		}
		return F.NIL;
	}

	private static IAST normalizeArgument(final IExpr arg1, final EvalEngine engine) {
		if (arg1.isList()) {
			if (arg1.size() == 3) {
				IAST list = (IAST) arg1;
				IExpr min = list.arg1();
				IExpr max = list.arg2();
				if (min.isRealResult() && max.isRealResult()) {
					if (min.greaterThan(max).isTrue()) {
						return F.List(max, min);
					}
				}
				return F.NIL;
			}
			// The expression `1` is not a valid interval.
			String str = IOFunctions.getMessage("nvld", F.List(arg1), engine);
			throw new ArgumentTypeException(str);
		}
		return F.List(arg1, arg1);
	}

	public static IExpr log(final IAST ast) {
		IAST interval = normalize(ast);
		if (interval.isPresent()) {
			IASTMutable result = interval.copy();
			for (int i = 1; i < interval.size(); i++) {
				IAST list = (IAST) interval.get(i);
				IExpr min = list.arg1();
				IExpr max = list.arg2();
				if (min.isNonNegativeResult() && max.isNonNegativeResult()) {
					min = F.Log.of(min);
					max = F.Log.of(max);
					result.set(i, F.List(min, max));
				} else {
					return F.NIL;
				}
			}
			return result;
		}
		return F.NIL;
	}

	public static IExpr plus(final IAST ast1, final IAST ast2) {
		IAST interval1 = normalize(ast1);
		IAST interval2 = normalize(ast2);
		if (interval1.isPresent() && interval2.isPresent()) {
			IASTAppendable result = F.IntervalAlloc(interval1.size() * interval2.size());
			for (int i = 1; i < interval1.size(); i++) {
				IAST list1 = (IAST) interval1.get(i);
				IExpr min1 = list1.arg1();
				IExpr max1 = list1.arg2();
	
				for (int j = 1; j < interval2.size(); j++) {
					IAST list2 = (IAST) interval2.get(j);
					IExpr min2 = list2.arg1();
					IExpr max2 = list2.arg2();
	
					IAST list = F.List(min1.plus(min2), //
							max1.plus(max2));
					result.append(list);
				}
			}
			return result;
		}
		return F.NIL;
	}

	public static IExpr plus(final IExpr scalar, final IAST ast2) {
		IAST interval2 = normalize(ast2);
		if (scalar.isRealResult() && interval2.isPresent()) {
			IASTAppendable result = F.IntervalAlloc(interval2.size());
			for (int j = 1; j < interval2.size(); j++) {
				IAST list2 = (IAST) interval2.get(j);
				IExpr min2 = list2.arg1();
				IExpr max2 = list2.arg2();
	
				IAST list = F.List(scalar.plus(min2), //
						scalar.plus(max2));
				result.append(list);
			}
			return result;
		}
		return F.NIL;
	}

	public static IExpr times(final IAST ast1, final IAST ast2) {
		IAST interval1 = normalize(ast1);
		IAST interval2 = normalize(ast2);
		if (interval1.isPresent() && interval2.isPresent()) {
			IASTAppendable result = F.IntervalAlloc(interval1.size() * interval2.size());
			for (int i = 1; i < interval1.size(); i++) {
				IAST list1 = (IAST) interval1.get(i);
				IExpr min1 = list1.arg1();
				IExpr max1 = list1.arg2();
	
				for (int j = 1; j < interval2.size(); j++) {
					IAST list2 = (IAST) interval2.get(j);
					IExpr min2 = list2.arg1();
					IExpr max2 = list2.arg2();
	
					IAST list = F.List( //
							F.Min(min1.times(min2), min1.times(max2), max1.times(min2), max1.times(max2)), //
							F.Max(min1.times(min2), min1.times(max2), max1.times(min2), max1.times(max2)));
					result.append(list);
				}
			}
			return result;
		}
		return F.NIL;
	}

	public static IExpr times(final IExpr scalar, final IAST ast2) {
		IAST interval2 = normalize(ast2);
		if (scalar.isRealResult() && interval2.isPresent()) {
			IASTAppendable result = F.IntervalAlloc(interval2.size());
			for (int j = 1; j < interval2.size(); j++) {
				IAST list2 = (IAST) interval2.get(j);
				IExpr min2 = list2.arg1();
				IExpr max2 = list2.arg2();
	
				IAST list = F.List( //
						F.Min(scalar.times(min2), scalar.times(max2), scalar.times(min2), scalar.times(max2)), //
						F.Max(scalar.times(min2), scalar.times(max2), scalar.times(min2), scalar.times(max2)));
				result.append(list);
			}
			return result;
		}
		return F.NIL;
	}

	/**
	 * <p>
	 * Calculate <code>Interval({lower, upper},...,...) ^ exp</code>.
	 * </p>
	 * <p>
	 * See: <a href= "https://de.wikipedia.org/wiki/Intervallarithmetik#Elementare_Funktionen"> Intervallarithmetik -
	 * Elementare Funktionen</a>
	 * </p>
	 * 
	 * @param ast
	 * @param exp
	 * @return
	 */
	public static IExpr power(final IAST ast, IInteger exp) {
		IAST interval = normalize(ast);
		if (interval.isPresent()) {
			boolean negative = false;
	
			if (exp.isNegative()) {
				negative = true;
				exp = exp.negate();
			}
			if (exp.isOne()) {
				if (negative) {
					IASTAppendable result = F.IntervalAlloc(ast.size());
					for (int i = 1; i < interval.size(); i++) {
						IAST list = (IAST) interval.get(i);
						if (list.arg1().isRealResult() && list.arg2().isRealResult()) {
							if (list.arg1().isNegativeResult()) {
								if (list.arg2().isNegativeResult()) {
									result.append(F.List(list.arg1().inverse(), list.arg2().inverse()));
								} else {
									result.append(F.List(F.CNInfinity, list.arg1().inverse()));
									if (!list.arg2().isZero()) {
										result.append(F.List(list.arg2().inverse(), F.CInfinity));
									}
	
								}
							} else {
								if (list.arg1().isZero()) {
									if (list.arg2().isZero()) {
										result.append(F.List(F.CNInfinity, F.CInfinity));
									} else {
										result.append(F.List(list.arg2().inverse(), F.CInfinity));
									}
								} else {
									result.append(F.List(list.arg1().inverse(), list.arg2().inverse()));
								}
							}
						} else {
							return F.NIL;
						}
					}
					return result;
				}
				return ast;
			}
			IASTAppendable result = F.IntervalAlloc(ast.size());
			for (int i = 1; i < interval.size(); i++) {
				IAST list = (IAST) interval.get(i);
				if (list.arg1().isRealResult() && list.arg2().isRealResult()) {
					if (exp.isEven()) {
						if (list.arg1().isNonNegativeResult()) {
							result.append(F.List(list.arg1().power(exp), list.arg2().power(exp)));
						} else {
							if (list.arg2().isNegativeResult()) {
								result.append(F.List(list.arg2().power(exp), list.arg1().power(exp)));
							} else {
								result.append(F.List(F.C0, F.Max(list.arg1().power(exp), list.arg2().power(exp))));
							}
						}
					} else {
						result.append(F.List(list.arg1().power(exp), list.arg2().power(exp)));
					}
				} else {
					return F.NIL;
				}
			}
			if (negative) {
				return F.Power(result, F.CN1);
			}
			return result;
		}
		return F.NIL;
	}

	public static IExpr power(IExpr base, final IAST ast) {
		IAST interval = normalize(ast);
		if (interval.isPresent()) {
			if (base.isNegative()) {
				if (base.isMinusOne()) {
					return F.NIL;
				}
				return F.Times(F.Power(F.CN1, ast), F.Power(base.negate(), ast));
			}
			// if (EvalEngine.get().evalTrue(F.Greater(base, F.C1))) {
			IASTAppendable result = F.IntervalAlloc(ast.size());
			for (int i = 1; i < interval.size(); i++) {
				IAST list = (IAST) interval.get(i);
				if (base.isZero()) {
					if (list.arg1().isNegativeResult() || list.arg2().isNegativeResult()) {
						return F.Indeterminate;
					}
				}
				result.append(F.List(base.power(list.arg1()), base.power(list.arg2())));
			}
			return result;
			// }
		}
		return F.NIL;
	}

}
