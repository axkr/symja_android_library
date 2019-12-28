package org.matheclipse.core.expression;

import java.util.Comparator;

import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

public class IntervalSym {
	private final static Comparator<IExpr> INTERVAL_COMPARATOR = new Comparator<IExpr>() {
		@Override
		public int compare(IExpr o1, IExpr o2) {
			if (o1.first().equals(o2.first())) {
				if (o1.second().equals(o2.second())) {
					return 0;
				}
				return (F.Greater.ofQ(o1.second(), o2.second())) ? 1 : -1;
			}
			return (F.Greater.ofQ(o1.first(), o2.first())) ? 1 : -1;
		}

	};

	private static boolean isNormalized(final IAST ast) {
		return ast.isEvalFlagOn(IAST.BUILT_IN_EVALED);
	}

	/**
	 * The list of intervals are sorted and overlapping intervals are merged.
	 * 
	 * @param intervalList
	 * @return
	 */
	private static IAST normalize(final IAST intervalList) {
		if (isNormalized(intervalList)) {
			return intervalList;
		}
		return normalize(intervalList, EvalEngine.get());
	}

	/**
	 * The list of intervals are sorted and overlapping intervals are merged.
	 * 
	 * @param intervalList
	 * @param engine
	 * @return
	 */
	public static IAST normalize(final IAST intervalList, EvalEngine engine) {
		try {
			IASTAppendable result = intervalList.copyAppendable();
			boolean evaled = false;
			for (int i = 1; i < intervalList.size(); i++) {
				IAST temp = normalizeArgument(intervalList.get(i), engine);
				if (temp.isPresent()) {
					evaled = true;
					result.set(i, temp);
				}
			}
			EvalAttributes.sort(result, INTERVAL_COMPARATOR);
			result.addEvalFlags(IAST.BUILT_IN_EVALED);
			if (intervalList.size() > 2) {
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
			if (intervalList instanceof IASTMutable) {
				EvalAttributes.sort((IASTMutable) intervalList, INTERVAL_COMPARATOR);
				intervalList.addEvalFlags(IAST.BUILT_IN_EVALED);
			}
		} catch (RuntimeException rex) {
			engine.printMessage("Interval: " + rex.getMessage());
		}
		return F.NIL;
	}

	/**
	 * If the argument is a list of 2 elements, try sorting the elements. If the argument is not a list return a new
	 * <code>{argOfIntervalList, argOfIntervalList]</code>
	 * 
	 * @param argOfIntervalList
	 * @param engine
	 * @return
	 */
	private static IAST normalizeArgument(final IExpr argOfIntervalList, final EvalEngine engine) {
		if (argOfIntervalList.isList()) {
			if (argOfIntervalList.size() == 3) {
				IAST list = (IAST) argOfIntervalList;
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
			String str = IOFunctions.getMessage("nvld", F.List(argOfIntervalList), engine);
			throw new ArgumentTypeException(str);
		}
		return F.List(argOfIntervalList, argOfIntervalList);
	}

	public static IExpr abs(final IAST ast) {
		IAST interval = normalize(ast);
		if (interval.isPresent()) {
			IASTAppendable result = F.IntervalAlloc(interval.size());
			EvalEngine engine = EvalEngine.get();
			for (int i = 1; i < interval.size(); i++) {
				IAST list = (IAST) interval.get(i);
				IExpr min = list.arg1();
				IExpr max = list.arg2();
				if (engine.evalTrue(F.Less(min, F.C0)) && //
						engine.evalTrue(F.GreaterEqual(max, F.C0))) {
					result.append(F.List(F.C0, F.Max(F.Abs(min), F.Abs(max))));
				} else if (engine.evalTrue(F.GreaterEqual(min, F.C0)) && //
						engine.evalTrue(F.GreaterEqual(max, F.C0))) {
					result.append(F.List(F.Abs(min), F.Abs(max)));
				} else if (engine.evalTrue(F.Less(min, F.C0)) && //
						engine.evalTrue(F.Less(max, F.C0))) {
					result.append(F.List(F.Abs(max), F.Abs(min)));
				} else {
					return F.NIL;
				}

			}
			return result;
		}
		return F.NIL;
	}

	public static IExpr arccos(final IAST ast) {
		IAST interval = normalize(ast);
		if (interval.isPresent()) {
			try {
				EvalEngine engine = EvalEngine.get();
				IASTMutable result = interval.copy();
				for (int i = 1; i < interval.size(); i++) {
					IAST list = (IAST) interval.get(i);
					IExpr min = list.arg1();
					IExpr max = list.arg2();
					if (engine.evalTrue(F.GreaterEqual(min, F.CN1)) && //
							engine.evalTrue(F.LessEqual(max, F.C1))) {
						result.set(i, F.List(F.ArcCos(min), F.ArcCos(max)));
					} else {
						return F.NIL;
					}
				}
				return result;
			} catch (RuntimeException rex) {
				//
			}
		}
		return F.NIL;
	}

	public static IExpr arccot(final IAST ast) {
		IAST interval = normalize(ast);
		if (interval.isPresent()) {
			IASTAppendable result = F.IntervalAlloc(interval.size());
			EvalEngine engine = EvalEngine.get();
			for (int i = 1; i < interval.size(); i++) {
				IAST list = (IAST) interval.get(i);
				IExpr min = list.arg1();
				IExpr max = list.arg2();
				if (engine.evalTrue(F.GreaterEqual(min, F.C0)) && //
						engine.evalTrue(F.GreaterEqual(max, F.C0))) {
					// if (min.isNonNegativeResult() && max.isNonNegativeResult()) {
					result.append(F.List(F.ArcCot(min), F.ArcCot(max)));
				} else if (engine.evalTrue(F.Less(min, F.C0)) && //
						engine.evalTrue(F.GreaterEqual(max, F.C0))) {
					result.append(F.List(F.CNPiHalf, F.ArcCot(min)));
					result.append(F.List(F.ArcCot(max), F.CPiHalf));
				} else if (engine.evalTrue(F.Less(min, F.C0)) && //
						engine.evalTrue(F.Less(max, F.C0))) {
					result.append(F.List(F.ArcCot(min), F.ArcCot(max)));
				} else {
					return F.NIL;
				}
			}
			return result;
		}
		return F.NIL;
	}

	public static IExpr arcsin(final IAST ast) {
		IAST interval = normalize(ast);
		if (interval.isPresent()) {
			try {
				EvalEngine engine = EvalEngine.get();
				IASTMutable result = interval.copy();
				for (int i = 1; i < interval.size(); i++) {
					IAST list = (IAST) interval.get(i);
					IExpr min = list.arg1();
					IExpr max = list.arg2();
					if (engine.evalTrue(F.GreaterEqual(min, F.CN1)) && //
							engine.evalTrue(F.LessEqual(max, F.C1))) {
						result.set(i, F.List(F.ArcSin(min), F.ArcSin(max)));
					} else {
						return F.NIL;
					}
				}
				return result;
			} catch (RuntimeException rex) {
				//
			}
		}
		return F.NIL;
	}

	public static IExpr arctan(final IAST ast) {
		IAST interval = normalize(ast);
		if (interval.isPresent()) {
			try {
				IASTMutable result = interval.copy();
				for (int i = 1; i < interval.size(); i++) {
					IAST list = (IAST) interval.get(i);
					IExpr min = list.arg1();
					IExpr max = list.arg2();
					result.set(i, F.List(F.ArcTan(min), F.ArcTan(max)));
				}
				return result;
			} catch (RuntimeException rex) {
				//
			}
		}
		return F.NIL;
	}

	public static IExpr cos(final IAST ast) {
		IAST interval = normalize(ast);
		if (interval.isPresent()) {
			try {
				IASTMutable result = interval.copy();
				EvalEngine engine = EvalEngine.get();
				for (int i = 1; i < interval.size(); i++) {
					IAST list = (IAST) interval.get(i);
					IExpr min = list.arg1();
					IExpr max = list.arg2();
					IAST difference = F.Subtract(max, min);
					if (engine.evalTrue(F.GreaterEqual(difference, F.C2Pi))) {
						// difference >= 2 * Pi
						result.set(i, F.List(F.CN1, F.C1));
						continue;
					}
					// slope from 1st derivative
					double dMin = engine.evalDouble(F.Sin(min).negate());
					double dMax = engine.evalDouble(F.Sin(max).negate());
					if (engine.evalTrue(F.LessEqual(difference, F.Pi))) {
						if (dMin >= 0) {
							if (dMax >= 0) {
								result.set(i, F.List(F.Cos(min), F.Cos(max)));
							} else {
								result.set(i, F.List(F.Min(F.Cos(min), F.Cos(max)), F.C1));
							}
						} else {
							if (dMax < 0) {
								result.set(i, F.List(F.Cos(max), F.Cos(min)));
							} else {
								result.set(i, F.List(F.CN1, F.Max(F.Cos(min), F.Cos(max))));
							}
						}
					} else { // difference between {Pi, 2*Pi}
						if (dMin >= 0) {
							if (dMax > 0) {
								result.set(i, F.List(F.CN1, F.C1));
							} else {
								result.set(i, F.List(F.Min(F.Cos(min), F.Cos(max)), F.C1));
							}
						} else {
							if (dMax < 0) {
								result.set(i, F.List(F.CN1, F.C1));
							} else {
								result.set(i, F.List(F.CN1, F.Max(F.Cos(min), F.Cos(max))));
							}
						}
					}
				}
				return result;
			} catch (RuntimeException rex) {
				//
			}
		}
		return F.NIL;
	}

	public static IExpr cot(final IAST ast) {
		IAST interval = normalize(ast);
		if (interval.isPresent()) {
			try {
				IASTAppendable result = F.IntervalAlloc(interval.size());
				EvalEngine engine = EvalEngine.get();
				for (int i = 1; i < interval.size(); i++) {
					IAST list = (IAST) interval.get(i);
					IExpr min = list.arg1();
					IExpr max = list.arg2();
					IAST difference = F.Subtract(max, min);
					if (engine.evalTrue(F.GreaterEqual(difference, F.Pi))) {
						// >= Pi
						result.append(F.List(F.CNInfinity, F.CInfinity));
						continue;
					}
					double dMin = engine.evalDouble(F.Cot(min));
					double dMax = engine.evalDouble(F.Cot(max));
					if (engine.evalTrue(F.LessEqual(difference, F.CPiHalf))) {
						// difference <= 1/2*Pi
						if (dMin < 0) {
							if (dMax >= 0) {
								result.append(F.List(F.CNInfinity, F.Cot(min)));
								result.append(F.List(F.Cot(max), F.CInfinity));
							} else {
								result.append(F.List(F.Cot(max), F.Cot(min)));
							}
						} else {
							result.append(F.List(F.Cot(min), F.Cot(max)));
						}
					} else {// difference between {Pi/2, Pi}
						if (dMin >= 0) {
							if (dMax < 0) {
								result.append(F.List(F.CNInfinity, F.Cot(max)));
								result.append(F.List(F.Cot(min), F.CInfinity));
							} else {
								if (dMin < dMax) {
									result.append(F.List(F.CNInfinity, F.Cot(min)));
									result.append(F.List(F.Cot(max), F.CInfinity));
								} else {
									result.append(F.List(F.CNInfinity, F.CInfinity));
								}
							}
						} else {
							if (dMax < 0) {
								if (dMin < dMax) {
									result.append(F.List(F.Cot(max), F.CInfinity));
									result.append(F.List(F.CNInfinity, F.Cot(min)));
								} else {
									result.append(F.List(F.CNInfinity, F.CInfinity));
								}
							} else {
								result.append(F.List(F.CNInfinity, F.CInfinity));
							}
						}
					}
				}
				return result;
			} catch (RuntimeException rex) {
				//
			}
		}
		return F.NIL;
	}

	/**
	 * 
	 * @param symbol
	 * @param ast
	 * @return
	 */
	public static IExpr mapSymbol(ISymbol symbol, final IAST ast) {
		IAST interval = normalize(ast);
		if (interval.isPresent()) {
			IASTAppendable result = F.IntervalAlloc(interval.size());
			for (int i = 1; i < interval.size(); i++) {
				IAST list = (IAST) interval.get(i);
				IExpr min = list.arg1();
				IExpr max = list.arg2();
				if (min.isRealResult() && max.isRealResult()) {
					result.append(F.List(F.unaryAST1(symbol, min), F.unaryAST1(symbol, max)));
				} else {
					return F.NIL;
				}

			}
			return result;
		}
		return F.NIL;
	}

	public static IExpr log(final IAST ast) {
		IAST interval = normalize(ast);
		if (interval.isPresent()) {
			IASTMutable result = interval.copy();
			EvalEngine engine = EvalEngine.get();
			for (int i = 1; i < interval.size(); i++) {
				IAST list = (IAST) interval.get(i);
				IExpr min = list.arg1();
				IExpr max = list.arg2();
				if (min.isNonNegativeResult() && max.isNonNegativeResult()) {
					min = F.Log.of(engine, min);
					max = F.Log.of(engine, max);
					result.set(i, F.List(min, max));
				} else {
					return F.NIL;
				}
			}
			return result;
		}
		return F.NIL;
	}

	public static IExpr sin(final IAST ast) {
		IAST interval = normalize(ast);
		if (interval.isPresent()) {
			try {
				IASTMutable result = interval.copy();
				EvalEngine engine = EvalEngine.get();
				for (int i = 1; i < interval.size(); i++) {
					IAST list = (IAST) interval.get(i);
					IExpr min = list.arg1();
					IExpr max = list.arg2();
					IAST difference = F.Subtract(max, min);
					if (engine.evalTrue(F.GreaterEqual(difference, F.C2Pi))) {
						// difference >= 2 * Pi
						result.set(i, F.List(F.CN1, F.C1));
						continue;
					}
					// slope from 1st derivative
					double dMin = engine.evalDouble(F.Cos(min));
					double dMax = engine.evalDouble(F.Cos(max));
					if (engine.evalTrue(F.LessEqual(difference, F.Pi))) {
						if (dMin >= 0) {
							if (dMax >= 0) {
								result.set(i, F.List(F.Sin(min), F.Sin(max)));
							} else {
								result.set(i, F.List(F.Min(F.Sin(min), F.Sin(max)), F.C1));
							}
						} else {
							if (dMax < 0) {
								result.set(i, F.List(F.Sin(max), F.Sin(min)));
							} else {
								result.set(i, F.List(F.CN1, F.Max(F.Sin(min), F.Sin(max))));
							}
						}
					} else {// difference between {Pi, 2*Pi}
						if (dMin >= 0) {
							if (dMax > 0) {
								result.set(i, F.List(F.CN1, F.C1));
							} else {
								result.set(i, F.List(F.Min(F.Sin(min), F.Sin(max)), F.C1));
							}
						} else {
							if (dMax < 0) {
								result.set(i, F.List(F.CN1, F.C1));
							} else {
								result.set(i, F.List(F.CN1, F.Max(F.Sin(min), F.Sin(max))));
							}
						}
					}
				}
				return result;
			} catch (RuntimeException rex) {
				//
			}
		}
		return F.NIL;
	}

	public static IExpr tan(final IAST ast) {
		IAST interval = normalize(ast);
		if (interval.isPresent()) {
			try {
				IASTAppendable result = F.IntervalAlloc(interval.size());
				EvalEngine engine = EvalEngine.get();
				for (int i = 1; i < interval.size(); i++) {
					IAST list = (IAST) interval.get(i);
					IExpr min = list.arg1();
					IExpr max = list.arg2();
					IAST difference = F.Subtract(max, min);
					if (engine.evalTrue(F.GreaterEqual(difference, F.Pi))) {
						// >= Pi
						result.append(F.List(F.CNInfinity, F.CInfinity));
						continue;
					}
					double dMin = engine.evalDouble(F.Tan(min));
					double dMax = engine.evalDouble(F.Tan(max));
					if (engine.evalTrue(F.LessEqual(difference, F.CPiHalf))) {
						// difference <= 1/2*Pi
						if (dMin >= 0) {
							if (dMax < 0) {
								result.append(F.List(F.CNInfinity, F.Tan(max)));
								result.append(F.List(F.Tan(min), F.CInfinity));
							} else {
								result.append(F.List(F.Tan(min), F.Tan(max)));
							}
						} else {
							result.append(F.List(F.Tan(min), F.Tan(max)));
						}
					} else {// difference between {Pi/2, Pi}
						if (dMin >= 0) {
							if (dMax < 0) {
								result.append(F.List(F.CNInfinity, F.Tan(max)));
								result.append(F.List(F.Tan(min), F.CInfinity));
							} else {
								if (dMin <= dMax) {
									result.append(F.List(F.CNInfinity, F.CInfinity));
								} else {
									result.append(F.List(F.CNInfinity, F.Tan(max)));
									result.append(F.List(F.Tan(min), F.CInfinity));
								}
							}
						} else {
							if (dMax < 0) {
								if (dMin <= dMax) {
									result.append(F.List(F.CNInfinity, F.CInfinity));
								} else {
									result.append(F.List(F.CNInfinity, F.Tan(max)));
									result.append(F.List(F.Tan(min), F.CInfinity));
								}
							} else {
								result.append(F.List(F.Tan(min), F.Tan(max)));
							}
						}
					}
				}
				return result;
			} catch (RuntimeException rex) {
				//
			}
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
