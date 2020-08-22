package org.matheclipse.core.builtin;

import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticMeasurement;
import org.gavaghan.geodesy.GlobalPosition;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.IntervalSym;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.GeoPositionExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class IntervalFunctions {
	/**
	 * 
	 * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation in static
	 * initializer</a>
	 */
	private static class Initializer {

		private static void init() {
			S.Interval.setEvaluator(new Interval());
			S.IntervalMemberQ.setEvaluator(new IntervalMemberQ());
			S.IntervalIntersection.setEvaluator(new IntervalIntersection());
			S.IntervalUnion.setEvaluator(new IntervalUnion());
		}
	}

	/**
	 * <pre>
	 * <code>Interval({a, b})
	 * </code>
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * represents the interval from <code>a</code> to <code>b</code>.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See
	 * </p>
	 * <ul>
	 * <li><a href="https://en.wikipedia.org/wiki/Interval_arithmetic">Wikipedia - Interval arithmetic</a></li>
	 * <li><a href="https://en.wikipedia.org/wiki/Interval_(mathematics)">Wikipedia - Interval (mathematics)</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * <code>&gt;&gt; Interval({1, 6}) * Interval({0, 2}) 
	 * Interval({0,12})
	 * 
	 * &gt;&gt; Interval({1.5, 6}) * Interval({0.1, 2.7})
	 * Interval({0.15,16.2})
	 * 
	 * &gt;&gt; Sign(Interval({-43, -42})) 
	 * -1
	 * 
	 * &gt;&gt; Im(Interval({-Infinity, Infinity}))
	 * 0
	 * 
	 * &gt;&gt; ArcCot(Interval({-1, Infinity})) 
	 * Interval({-Pi/2,-Pi/4},{0,Pi/2})
	 * </code>
	 * </pre>
	 * 
	 * <h3>Related terms</h3>
	 * <p>
	 * <a href="IntervalIntersection.md">IntervalIntersection</a>, <a href="IntervalUnion.md">IntervalUnion</a>
	 * </p>
	 */
	private final static class Interval extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isEvalFlagOff(IAST.BUILT_IN_EVALED)) {
				IAST result = IntervalSym.normalize(ast, engine);
				if (result.isPresent()) {
					return result;
				}
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	/**
	 * <pre>
	 * <code>IntervalMemberQ(interval, interval-or-real-number)
	 * </code>
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>True</code>, if <code>interval-or-real-number</code> is completly sourrounded by
	 * <code>interval</code>
	 * </p>
	 * </blockquote>
	 * <p>
	 * See
	 * </p>
	 * <ul>
	 * <li><a href="https://en.wikipedia.org/wiki/Interval_arithmetic">Wikipedia - Interval arithmetic</a></li>
	 * <li><a href="https://en.wikipedia.org/wiki/Interval_(mathematics)">Wikipedia - Interval (mathematics)</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * <code>&gt;&gt; IntervalMemberQ(Interval({4,10}), Interval({2*Pi, 3*Pi})) 
	 * True
	 * 
	 * &gt;&gt; IntervalMemberQ(Interval({4,10}), Interval({2*Pi, 4*Pi})) 
	 * False
	 * </code>
	 * </pre>
	 * 
	 * <h3>Related terms</h3>
	 * <p>
	 * <a href="Interval.md">Interval</a>, <a href="IntervalIntersection.md">IntervalIntersection</a>,
	 * <a href="IntervalUnion.md">IntervalUnion</a>
	 * </p>
	 */
	private final static class IntervalMemberQ extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.arg1().isInterval()) {
				IAST interval1 = IntervalSym.normalize((IAST) ast.arg1());
				if (ast.arg2().isInterval()) {
					if (interval1.isPresent()) {
						IAST interval2 = IntervalSym.normalize((IAST) ast.arg2());
						if (interval2.isPresent()) {
							IASTAppendable copyInterval2 = interval2.copyAppendable();

							for (int i = 1; i < interval1.size(); i++) {
								IAST list1 = (IAST) interval1.get(i);
								IExpr min1 = list1.arg1();
								IExpr max1 = list1.arg2();
								boolean included = false;
								for (int j = 1; j < interval2.size(); j++) {
									IAST list2 = (IAST) interval2.get(j);
									IExpr min2 = list2.arg1();
									IExpr max2 = list2.arg2();
									if (F.LessEqual.ofQ(engine, min1, min2) && //
											F.GreaterEqual.ofQ(engine, max1, max2)) {
										copyInterval2.remove(j);
										if (copyInterval2.size() <= 1) {
											return F.True;
										}
										included = true;
										break;
									}
								}
								if (!included) {
									return F.False;
								}
							}
							if (copyInterval2.size() <= 1) {
								return F.True;
							}
						}
					}
				} else {
					IExpr arg2 = ast.arg2();
					for (int i = 1; i < interval1.size(); i++) {
						IAST list1 = (IAST) interval1.get(i);
						IExpr min1 = list1.arg1();
						IExpr max1 = list1.arg2();
						if (F.LessEqual.ofQ(engine, min1, arg2) && //
								F.GreaterEqual.ofQ(engine, max1, arg2)) {
							return F.True;
						}
					}
				}
			}
			return F.False;
		}

		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_2_2;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}

	}

	/**
	 * <pre>
	 * <code>IntervalIntersection(interval_1, interval_2, ...)
	 * </code>
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * compute the intersection of the intervals <code>interval_1, interval_2, ...</code>
	 * </p>
	 * </blockquote>
	 * <p>
	 * See
	 * </p>
	 * <ul>
	 * <li><a href="https://en.wikipedia.org/wiki/Interval_arithmetic">Wikipedia - Interval arithmetic</a></li>
	 * <li><a href="https://en.wikipedia.org/wiki/Interval_(mathematics)">Wikipedia - Interval (mathematics)</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * <code>&gt;&gt; IntervalIntersection(Interval({1, 2}, {3, 4}, {5, 7}, {8, 8.5}), Interval({1.5, 3.5}, {4.1, 6}, {9, 10})) 
	 * Interval({1.5,2},{3,3.5},{5,6})
	 * </code>
	 * </pre>
	 * 
	 * <h3>Related terms</h3>
	 * <p>
	 * <a href="Interval.md">Interval</a>, <a href="IntervalUnion.md">IntervalUnion</a>
	 * </p>
	 */
	private final static class IntervalIntersection extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			for (int i = 1; i < ast.size(); i++) {
				if (!ast.get(i).isInterval()) {
					return F.NIL;
				}
				IAST interval = (IAST) ast.get(i);
				for (int j = 1; j < interval.size(); j++) {
					if (!interval.get(j).isList2()) {
						return F.NIL;
					}
					IAST list1 = (IAST) interval.get(j);
					IExpr min1 = list1.arg1();
					IExpr max1 = list1.arg2();
					if (!min1.isRealResult() || !max1.isRealResult()) {
						return F.NIL;
					}
				}
			}
			IAST result = (IAST) ast.arg1();
			result = IntervalSym.normalize(result, engine).orElse(result);
			for (int i = 2; i < ast.size(); i++) {
				IAST interval = (IAST) ast.get(i);
				IAST normalizedArg = IntervalSym.normalize(interval, engine).orElse(interval);
				result = intersection(result, normalizedArg, engine);
				if (result.size() == 1) {
					return result;
				}
			}
			IAST normalized = IntervalSym.normalize(result, engine);
			return normalized.orElse(result);
		}

		private IAST intersection(final IAST interval1, final IAST interval2, EvalEngine engine) {
			IASTAppendable result = F.ast(S.Interval, 3, false);
			for (int i = 1; i < interval1.size(); i++) {
				IAST list1 = (IAST) interval1.get(i);
				IExpr min1 = list1.arg1();
				IExpr max1 = list1.arg2();
				for (int j = 1; j < interval2.size(); j++) {
					IAST list2 = (IAST) interval2.get(j);
					IExpr min2 = list2.arg1();
					IExpr max2 = list2.arg2();
					if (F.Less.ofQ(engine, max1, min2) || //
							F.Less.ofQ(engine, max2, min1)) {
						continue;
					}

					if (F.LessEqual.ofQ(engine, min1, min2)) {
						min1 = min2;
					}
					if (F.GreaterEqual.ofQ(engine, max1, max2)) {
						max1 = max2;
					}
					result.append(F.List(min1, max1));
				}
			}
			return result;
		}

		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_1_INFINITY;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}

	}

	/**
	 * <pre>
	 * <code>IntervalUnion(interval_1, interval_2, ...)
	 * </code>
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * compute the union of the intervals <code>interval_1, interval_2, ...</code>
	 * </p>
	 * </blockquote>
	 * <p>
	 * See
	 * </p>
	 * <ul>
	 * <li><a href="https://en.wikipedia.org/wiki/Interval_arithmetic">Wikipedia - Interval arithmetic</a></li>
	 * <li><a href="https://en.wikipedia.org/wiki/Interval_(mathematics)">Wikipedia - Interval (mathematics)</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * <code>&gt;&gt; IntervalUnion(Interval({1, 2}, {3, 4}, {5, 7}, {8, 8.5}), Interval({1.5, 3.5}, {4.1, 6}, {9, 10}))
	 * Interval({1,4},{4.1,7},{8,8.5},{9,10})
	 * </code>
	 * </pre>
	 * 
	 * <h3>Related terms</h3>
	 * <p>
	 * <a href="Interval.md">Interval</a>, <a href="IntervalIntersection.md">IntervalIntersection</a>
	 * </p>
	 */
	private final static class IntervalUnion extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			int size = 2;
			for (int i = 1; i < ast.size(); i++) {
				if (!ast.get(i).isInterval()) {
					return F.NIL;
				}
				IAST interval = (IAST) ast.get(i);
				size += interval.argSize();
				for (int j = 1; j < interval.size(); j++) {
					if (!interval.get(j).isList2()) {
						return F.NIL;
					}
					IAST list1 = (IAST) interval.get(j);
					IExpr min1 = list1.arg1();
					IExpr max1 = list1.arg2();
					if (!min1.isRealResult() || !max1.isRealResult()) {
						return F.NIL;
					}
				}
			}
			IASTAppendable result = F.ast(S.Interval, size, false);
			for (int i = 1; i < ast.size(); i++) {
				IAST interval = (IAST) ast.get(i);
				for (int j = 1; j < interval.size(); j++) {
					result.append(interval.get(j));
				}
			}
			IAST normalized = IntervalSym.normalize(result, engine);
			return normalized.orElse(result);
		}

		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_1_INFINITY;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}

	}

	public static void initialize() {
		Initializer.init();
	}

	private IntervalFunctions() {

	}

}
