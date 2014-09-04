package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.Polynomial;
import org.matheclipse.parser.client.SyntaxError;

import com.google.common.base.Function;

/**
 * 
 * See: <a href="http://en.wikipedia.org/wiki/Coefficient">Wikipedia - Coefficient</a>
 */
public class Coefficient extends AbstractFunctionEvaluator {

	private static class PlusFunction implements Function<IExpr, IExpr> {
		IExpr arg2;
		IInteger n;

		public PlusFunction(IExpr arg2, IInteger n) {
			this.arg2 = arg2;
			this.n = n;
		}

		@Override
		public IExpr apply(IExpr from) throws ArithmeticException {
			if (from.isPower()) {
				return coefficientPower((IAST) from, arg2, n);
			} else if (from.isTimes()) {
				return coefficientTimes((IAST) from, arg2, n);
			} else {
				return coefficientAtom(from, arg2, n);
			}
		}
	}

	public Coefficient() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 3, 4);
		IExpr expr = F.evalExpandAll(ast.arg1());
		ISymbol arg2 = Validate.checkSymbolType(ast, 2);

		try {
			long n = 1;
			if (ast.size() == 4) {
				if (ast.arg3().isNegativeInfinity()) {
					return F.C0;
				}
				n = Validate.checkLongType(ast.arg3());
			}
			Polynomial poly = new Polynomial(expr, (ISymbol) arg2);
			return poly.coefficient(n);
//			return coefficient(expr, (ISymbol) arg2, n);
		} catch (ArithmeticException ae) {

		}
		return null;
	}

	/**
	 * <code>Coefficient(expr, x, n)</code>
	 * 
	 * @param expr
	 * @param x
	 *            a symbol to compare with
	 * @param n
	 * @return
	 */
	public static IExpr coefficient(IExpr expr, ISymbol x, IInteger n) {
		if (expr.isAST()) {
			IAST expAST = (IAST) expr;
			Function<IExpr, IExpr> plusFunction = new PlusFunction(x, n);
			if (expAST.isPlus()) {
				// TODO implement a special sum() method instead of map
				IAST filterAST = expAST.map(plusFunction);
				if (filterAST.size() == 1) {
					return F.C0;
				}
				return F.eval(filterAST);
			} else {
				return plusFunction.apply(expAST);
			}
		} else {
			return coefficientAtom(expr, x, n);
		}
	}

	private static IExpr coefficientTimes(IAST times, IExpr arg2, IInteger n) throws ArithmeticException {
		for (int i = 1; i < times.size(); i++) {
			if (times.get(i).isPower()) {
				IAST pow = (IAST) times.get(i);
				if (pow.equalsAt(1, arg2)) {
					if (pow.arg2().isNumEqualInteger(n)) {
						return times.removeAtClone(i);
					}
					return F.C0;
				}
			} else if (times.equalsAt(i, arg2)) {
				if (n.equals(F.C0)) {
					return F.C0;
				} else if (n.equals(F.C1)) {
					return times.removeAtClone(i);
				}
				return F.C0;
			}
		}
		if (n.equals(F.C0)) {
			return times;
		}
		return F.C0;
	}

	private static IExpr coefficientPower(IAST powerAST, IExpr arg2, IInteger n) {
		if (powerAST.equalsAt(1, arg2)) {
			if (powerAST.arg2().isNumEqualInteger(n)) {
				return F.C1;
			}
			return F.C0;
		}
		return F.C0;
	}

	private static IExpr coefficientAtom(IExpr expr, IExpr arg2, IInteger n) {
		if (n.equals(F.C0)) {
			if (expr.equals(arg2)) {
				return F.C0;
			}
			return expr;
		} else if (n.equals(F.C1)) {
			if (expr.equals(arg2)) {
				return F.C1;
			}
			return F.C0;
		}
		return F.C0;
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE);
		super.setUp(symbol);
	}
}