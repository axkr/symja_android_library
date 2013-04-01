package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;

import com.google.common.base.Function;

/**
 * 
 */
public class Coefficient extends AbstractFunctionEvaluator {

	private class PlusFunction implements Function<IExpr, IExpr> {
		IExpr arg2;
		IInteger n;

		public PlusFunction(IExpr arg2, IInteger n) {
			this.arg2 = arg2;
			this.n = n;
		}

		@Override
		public IExpr apply(IExpr from) {
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
		IExpr expr = F.evalExpandAll(ast.get(1));
		IExpr arg2 = ast.get(2);
		if (!arg2.isSymbol()){
			// TODO allow multinomials
			return null;
		}
		IInteger n = F.C1;
		if (ast.size() == 4) {
			n = Validate.checkIntegerType(ast, 3);
		}
		if (expr.isAST()) {
			IAST expAST = (IAST) expr;
			Function<IExpr, IExpr> plusFunction = new PlusFunction(arg2, n);
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
			return coefficientAtom(expr, arg2, n);
		}
	}

	private static IExpr coefficientTimes(IAST times, IExpr arg2, IInteger n) {
		for (int i = 1; i < times.size(); i++) {
			if (times.get(i).isPower()) {
				IAST pow = (IAST) times.get(i);
				if (pow.get(1).equals(arg2)) {
					if (pow.get(2).equals(n)) {
						times = times.clone();
						times.remove(i);
						return times;
					}
					return F.C0;
				}
			} else if (times.get(i).equals(arg2)) {
				if (n.equals(F.C0)) {
					return F.C0;
				} else if (n.equals(F.C1)) {
					times = times.clone();
					times.remove(i);
					return times;
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
		if (powerAST.get(1).equals(arg2)) {
			if (powerAST.get(2).equals(n)) {
				return F.C1;
			}
			return F.C0;
		}
		return powerAST;
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