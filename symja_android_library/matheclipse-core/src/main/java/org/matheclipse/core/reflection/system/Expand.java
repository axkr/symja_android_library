package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.PlusOp;
import org.matheclipse.core.eval.PowerOp;
import org.matheclipse.core.eval.TimesOp;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.combinatoric.KPermutationsIterable;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

public class Expand extends AbstractFunctionEvaluator {
	private static class Expander {

		boolean expandNegativePowers;

		boolean distributePlus;
		/**
		 * Pattern which may be <code>null</code>
		 */
		IExpr pattern;

		public Expander(IExpr pattern, boolean expandNegativePowers, boolean distributePlus) {
			this.pattern = pattern;
			this.expandNegativePowers = expandNegativePowers;
			this.distributePlus = distributePlus;
		}

		/**
		 * Check if the given expression doesn't contain the pattern.
		 * 
		 * @param expression
		 * @return
		 */
		public boolean isPatternFree(IExpr expression) {
			return (pattern != null && expression.isFree(pattern, false));
		}

		public IExpr expandAST(final IAST ast) {

			if (isPatternFree(ast)) {
				return null;
			}
			if (ast.isPower()) {
				return expandPowerNull(ast);
			} else if (ast.isTimes()) {
				// (a+b)*(c+d)...

				IExpr[] temp = Apart.getFractionalPartsTimes(ast, false);
				if (temp == null) {
					return expandTimes(ast);
				}
				if (temp[0].equals(F.C1)) {
					if (temp[1].isTimes()) {
						return F.Power(expandTimes((IAST) temp[1]), F.CN1);
					}
					if (temp[1].isPower() || temp[1].isPlus()) {
						IExpr denom = expandAST((IAST) temp[1]);
						if (denom != null) {
							return F.Power(denom, F.CN1);
						}
					}
					return null;
				}

				if (temp[1].equals(F.C1)) {
					return expandTimes(ast);
				}

				if (temp[0].isTimes()) {
					temp[0] = expandTimes((IAST) temp[0]);
				}
				if (expandNegativePowers) {
					if (temp[1].isTimes()) {
						temp[1] = expandTimes((IAST) temp[1]);
					} else {
						if (temp[1].isPower() || temp[1].isPlus()) {
							IExpr denom = expandAST((IAST) temp[1]);
							if (denom != null) {
								temp[1] = denom;
							}
						}
					}

				}
				IExpr powerAST = PowerOp.power(temp[1], F.CN1);
				if (distributePlus && temp[0].isPlus()) {
					return ((IAST) temp[0]).mapAt(F.Times(null, powerAST), 1);
				}
				return F.Times(temp[0], powerAST);
			} else if (ast.isPlus()) {
				IAST result = null;
				for (int i = 1; i < ast.size(); i++) {
					final IExpr arg = ast.get(i);
					if (arg.isAST()) {
						IExpr temp = expand((IAST) arg, pattern, expandNegativePowers, false);
						if (temp != null) {
							if (result == null) {
								result = ast.copyUntil(i);
							}
							result.add(temp);
							continue;
						}
					}
					if (result != null) {
						result.add(arg);
					}
				}
				if (result != null) {
					return PlusOp.plus(result);
				}
			}
			return null;
		}

		/**
		 * Expand <code>(a+b)^i</code> with <code>i</code> an integer number in the range Integer.MIN_VALUE to Integer.MAX_VALUE.
		 * 
		 * @param powerAST
		 * @return
		 */
		public IExpr expandPowerNull(final IAST powerAST) {
			try {
				int exp = Validate.checkPowerExponent(powerAST);
				if ((powerAST.arg1().isPlus())) {
					if (exp < 0) {
						if (expandNegativePowers) {
							exp *= (-1);
							return F.Power(expandPower((IAST) powerAST.arg1(), exp), F.CN1);
						}
						return null;
					}
					return expandPower((IAST) powerAST.arg1(), exp);
				}
			} catch (WrongArgumentType e) {
				return null;
			}
			return null;
		}

		/**
		 * Expand a polynomial power with the multinomial theorem. See <a
		 * href="http://en.wikipedia.org/wiki/Multinomial_theorem">Wikipedia - Multinomial theorem</a>
		 * 
		 * @param plusAST
		 * @param n
		 *            <code>n &ge; 0</code>
		 * @return
		 */
		private IExpr expandPower(final IAST plusAST, final int n) {
			if (n == 1) {
				return plusAST;
			}
			if (n == 0) {
				return F.C1;
			}

			final IAST expandedResult = F.Plus();
			Expand.NumberPartititon part = new Expand.NumberPartititon(plusAST, n, expandedResult);
			part.partition();
			return PlusOp.plus(expandedResult);
		}

		private IExpr expandTimes(final IAST timesAST) {
			IExpr result = timesAST.arg1();
			IExpr temp;
			if (result.isPower()) {
				temp = expandPowerNull((IAST) result);
				if (temp != null) {
					result = temp;
				}
			}
			for (int i = 2; i < timesAST.size(); i++) {
				temp = timesAST.get(i);
				if (temp.isPower()) {
					temp = expandPowerNull((IAST) temp);
					if (temp == null) {
						temp = timesAST.get(i);
					}
				}
				result = expandTimesBinary(result, temp);
			}
			return result;
		}

		private IExpr expandTimesBinary(final IExpr expr0, final IExpr expr1) {
			if (expr0.isPlus()) {
				if (!expr1.isPlus()) {
					return expandExprTimesPlus(expr1, (IAST) expr0);
				}
				final IAST ast1 = assurePlus(expr1);
				return expandPlusTimesPlus((IAST) expr0, ast1);
			}
			if (expr1.isPlus()) {
				return expandExprTimesPlus(expr0, (IAST) expr1);
			}
			return evalTimesBinary(expr0, expr1);
		}

		/**
		 * <code>(a+b)*(c+d) -> a*c+a*d+b*c+b*d</code>
		 * 
		 * @param plusAST0
		 * @param plusAST1
		 * @return
		 */
		private IExpr expandPlusTimesPlus(final IAST plusAST0, final IAST plusAST1) {
			IAST result = F.Plus();
			for (int i = 1; i < plusAST0.size(); i++) {
				for (int j = 1; j < plusAST1.size(); j++) {
					// evaluate to flatten out Times() exprs
					evalAndExpandAST(plusAST0.get(i), plusAST1.get(j), result);
				}
			}
			return PlusOp.plus(result);
		}

		/**
		 * <code>expr*(a+b+c) -> expr*a+expr*b+expr*c</code>
		 * 
		 * @param expr1
		 * @param plusAST
		 * @return
		 */
		private IExpr expandExprTimesPlus(final IExpr expr1, final IAST plusAST) {
			IAST result = F.Plus();
			for (int i = 1; i < plusAST.size(); i++) {
				// evaluate to flatten out Times() exprs
				 evalAndExpandAST(expr1, plusAST.get(i), result);
			}
			return PlusOp.plus(result);
		}

		/**
		 * Evaluate <code>expr1 * expr2</code> and expand the resulting expression, if it's an <code>IAST</code>. After that add the
		 * resulting expression to the <code>PlusOp</code>
		 * 
		 * @param result
		 * @param expr
		 */
		public void evalAndExpandAST(IExpr expr1, IExpr expr2, final IAST result) {
			IExpr arg = evalTimesBinary(expr1, expr2);
			if (arg.isAST()) {
				IExpr res = expandAST((IAST) arg);
				if (res != null) {
					result.add(res);
					return;
				}
			}
			result.add(arg);
		}

		public IExpr evalTimesBinary(IExpr expr1, IExpr expr2) {
			return TimesOp.times(expr1, expr2);
			// TimesOp timesOp = new TimesOp(2);
			// IExpr arg = timesOp.times(expr1);
			// if (arg != null) {
			// return arg;
			// }
			// arg = timesOp.times(expr2);
			// if (arg != null) {
			// return arg;
			// }
			// return timesOp.getProduct();
		}

		/**
		 * If <code>expr</code> is not of the form <code>Plus(...)</code>, return <code>Plus(expr)</code>, else return
		 * <code>expr</code>.
		 * 
		 * @param expr
		 * @return
		 */
		private IAST assurePlus(final IExpr expr) {
			if (expr.isPlus()) {
				return (IAST) expr;
			}
			return F.Plus(expr);
		}

	}

	static class NumberPartititon {
		IAST expandedResult;
		int m;
		int n;
		int[] parts;
		IAST precalculatedPowerASTs;

		public NumberPartititon(IAST plusAST, int n, IAST expandedResult) {

			this.expandedResult = expandedResult;
			this.n = n;
			this.m = plusAST.size() - 1;
			// System.out.println(m+"/"+n);
			this.parts = new int[m];
			// precalculate all Power[] ASTs:
			this.precalculatedPowerASTs = F.List();
			for (IExpr expr : plusAST) {
				precalculatedPowerASTs.add(expr);
			}
		}

		private void addFactor(int[] j) {
			final KPermutationsIterable perm = new KPermutationsIterable(j, m, m);
			IInteger multinomial = F.integer(Multinomial.multinomial(j, n));
			final IAST times = F.Times();
			IExpr temp;
			for (int[] indices : perm) {
				final IAST timesAST = times.clone();
				if (!multinomial.isOne()) {
					timesAST.add(multinomial);
				}
				for (int k = 0; k < m; k++) {
					if (indices[k] != 0) {
						temp = precalculatedPowerASTs.get(k + 1);
						if (indices[k] == 1) {
							timesAST.add(temp);
						} else {
							if (temp.isTimes()) {
								IAST ast = (IAST) temp;
								for (int i = 1; i < ast.size(); i++) {
									timesAST.add(PowerOp.power(ast.get(i), F.integer(indices[k])));
								}
							} else {
								timesAST.add(PowerOp.power(temp, F.integer(indices[k])));
							}
						}

					}
				}
				expandedResult.add(TimesOp.times(timesAST));
			}
		}

		public void partition() {
			partition(n, n, 0);
		}

		private void partition(int n, int max, int currentIndex) {
			if (n == 0) {
				addFactor(parts);
				return;
			}
			if (currentIndex >= m) {
				return;
			}
			int old;
			old = parts[currentIndex];
			int min = Math.min(max, n);

			for (int i = min; i >= 1; i--) {
				parts[currentIndex] = i;
				partition(n - i, i, currentIndex + 1);
			}
			parts[currentIndex] = old;
		}
	}

	/**
	 * Expand the given <code>ast</code> expression.
	 * 
	 * @param ast
	 * @param patt
	 * @param distributePlus
	 *            TODO
	 * @return <code>null</code> if the expression couldn't be expanded.
	 */
	public static IExpr expand(final IAST ast, IExpr patt, boolean expandNegativePowers, boolean distributePlus) {
		Expander expander = new Expander(patt, expandNegativePowers, distributePlus);
		return expander.expandAST(ast);
	}

	public Expand() {
		super();
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 2, 3);

		if (ast.arg1().isAST()) {
			IExpr patt = null;
			if (ast.size() > 2) {
				patt = ast.arg2();
			}
			IExpr temp = expand((IAST) ast.arg1(), patt, false, true);
			if (temp != null) {
				return temp;
			}
		}

		return ast.arg1();
	}
}