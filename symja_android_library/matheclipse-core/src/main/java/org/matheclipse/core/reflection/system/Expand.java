package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.Expand;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Times;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.IConstantHeaders;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.generic.combinatoric.KPermutationsIterable;

public class Expand extends AbstractFunctionEvaluator implements IConstantHeaders {

	private static class NumberPartititon {
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
				precalculatedPowerASTs.add(F.Power(expr, F.Null));
			}
		}

		private void addFactor(int[] j) {
			final KPermutationsIterable perm = new KPermutationsIterable(j, m, m);
			IInteger multinomial = F.integer(Multinomial.multinomial(j, n));
			final IAST times = Times();
			IAST temp;
			for (int[] indices : perm) {
				final IAST timesAST = times.clone();
				timesAST.add(multinomial);
				for (int k = 0; k < m; k++) {
					if (indices[k] != 0) {
						temp = precalculatedPowerASTs.getAST(k + 1).clone();
						temp.set(2, F.integer(indices[k]));
						timesAST.add(temp);
					}
				}
				expandedResult.add(timesAST);
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

	private static IAST assurePlus(final IExpr expr) {
		if (expr.isPlus()) {
			return (IAST) expr;
		}
		// if expr is not of the form Plus[...], generate Plus[expr]
		return F.Plus(expr);
	}

	public static IExpr expand(final IAST ast, IExpr patt) {
		if (patt != null) {
			if (ast.isFree(patt, true)) {
				return null; 
			}
		}
		if (ast.isPower()) {
			// (a+b)^exp
			if ((ast.get(1).isPlus()) && (ast.get(2).isInteger())) {
				int exp = Validate.checkIntType(ast, 2, Integer.MIN_VALUE);
				if (exp < 0) {
					exp *= (-1);
					return F.Power(expandPower((IAST) ast.get(1), exp), F.CN1);
				}
				return expandPower((IAST) ast.get(1), exp);
			}
		} else if (ast.isTimes()) {
			// (a+b)*(c+d)...

			IExpr[] temp = Apart.getFractionalPartsTimes(ast, false);
			if (temp[0].equals(F.C1)) {
				if (temp[1].isTimes()) {
					return F.Power(expandTimes((IAST) temp[1]), F.CN1);
				}
				if (temp[1].isPower() || temp[1].isPlus()) {
					IExpr denom = expand((IAST) temp[1], patt);
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
			if (temp[1].isTimes()) {
				temp[1] = expandTimes((IAST) temp[1]);
			} else {
				if (temp[1].isPower() || temp[1].isPlus()) {
					IExpr denom = expand((IAST) temp[1], patt);
					if (denom != null) {
						temp[1] = denom;
					}
				}
			}
			return F.Times(temp[0], F.Power(temp[1], F.CN1));
		} else if (ast.isPlus()) {
			return ast.map(Functors.replace1st(Expand(F.Null)));
		}
		return null;
	}

	/**
	 * Expand a polynomial power with the multinomial theorem. See <a
	 * href="http://en.wikipedia.org/wiki/Multinomial_theorem">Wikipedia -
	 * Multinomial theorem</a>
	 * 
	 * @param plusAST
	 * @param n
	 *          <code>n &ge; 0</code>
	 * @return
	 */
	public static IExpr expandPower(final IAST plusAST, final int n) {
		if (n == 1) {
			return plusAST;
		}
		if (n == 0) {
			return F.C1;
		}

		final IAST expandedResult = Plus();
		NumberPartititon part = new NumberPartititon(plusAST, n, expandedResult);
		part.partition();
		return expandedResult;
	}

	private static IExpr expandTimes(final IAST timesAST) {
		IExpr result = timesAST.get(1);
		for (int i = 2; i < timesAST.size(); i++) {
			result = expandTimesBinary(result, timesAST.get(i));
		}
		return result;
	}

	public static IExpr expandTimesBinary(final IExpr expr0, final IExpr expr1) {
		if (expr0.isPlus()) {
			if (!expr1.isPlus()) {
				return F.eval(expandTimesPlus(expr1, (IAST) expr0));
			}
			final IAST ast1 = assurePlus(expr1);
			return F.eval(expandTimesPlus((IAST) expr0, ast1));
		}
		if (expr1.isPlus()) {
			if (!expr0.isPlus()) {
				return F.eval(expandTimesPlus(expr0, (IAST) expr1));
			}
			final IAST ast0 = assurePlus(expr0);
			return F.eval(expandTimesPlus(ast0, (IAST) expr1));
		}
		return F.eval(F.Times(expr0, expr1));
	}

	public static IAST expandTimesPlus(final IAST plusAST0, final IAST plusAST1) {
		// (a+b)*(c+d) -> a*c+a*d+b*c+b*d
		final IAST pList = Plus();
		for (int i = 1; i < plusAST0.size(); i++) {
			plusAST1.args().map(pList, Functors.replace2nd(Times(plusAST0.get(i), F.Null)));
		}
		return pList;
	}

	public static IAST expandTimesPlus(final IExpr expr1, final IAST plusAST) {
		// expr*(a+b+c) -> expr*a+expr*b+expr*c
		final IAST pList = Plus();
		for (int i = 1; i < plusAST.size(); i++) {
			pList.add(F.Times(expr1, plusAST.get(i)));
		}
		return pList;
	}

	public Expand() {
		super();
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 2, 3);

		if (ast.get(1).isAST()) {
			IExpr patt = null;
			if (ast.size() > 2) {
				patt = ast.get(2);
			}
			IExpr temp = expand((IAST) ast.get(1), patt);
			if (temp != null) {
				return temp;
			}
		}

		return ast.get(1);
	}
}