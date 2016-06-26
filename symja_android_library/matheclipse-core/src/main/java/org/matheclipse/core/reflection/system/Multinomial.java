package org.matheclipse.core.reflection.system;

import java.util.List;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.AbstractIntegerSym;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Returns the multinomial coefficient.
 * 
 * See
 * <a href="http://en.wikipedia.org/wiki/Multinomial_coefficient">Multinomial
 * coefficient</a>
 */
public class Multinomial extends AbstractFunctionEvaluator {
	public Multinomial() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2);

		if (ast.isAST1()) {
			return F.C1;
		}
		if (ast.isAST2()) {
			return F.Binomial(F.Plus(ast.arg1(), ast.arg2()), ast.arg1());
		}
		for (int i = 1; i < ast.size(); i++) {
			if (!(ast.get(i).isInteger())) {
				return F.NIL;
			}
			if (((IInteger) ast.get(i)).isNegative()) {
				return F.NIL;
			}
		}

		return multinomial(ast);

	}

	public static IInteger multinomial(final IAST ast) {
		IInteger[] k = new IInteger[ast.size() - 1];
		IInteger n = F.C0;
		for (int i = 1; i < ast.size(); i++) {
			k[i - 1] = (IInteger) ast.get(i);
			n = n.add(k[i - 1]);
		}

		IInteger result = Factorial.factorial(n);
		for (int i = 0; i < k.length; i++) {
			result = result.div(Factorial.factorial(k[i]));
		}
		return result;
	}

	/**
	 * 
	 * @param indices
	 *            the non-negative coefficients
	 * @param n
	 *            the sum of the non-negative coefficients
	 * @return
	 */
	public static IInteger multinomial(final int[] indices, final int n) {
		IInteger bn = AbstractIntegerSym.valueOf(n);
		IInteger result = Factorial.factorial(bn);
		for (int i = 0; i < indices.length; i++) {
			if (indices[i] != 0) {
				result = result.div(Factorial.factorial(AbstractIntegerSym.valueOf(indices[i])));
			}
		}
		return result;
	}

	@Override
	public void setUp(ISymbol symbol) {
		symbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
	}
}
