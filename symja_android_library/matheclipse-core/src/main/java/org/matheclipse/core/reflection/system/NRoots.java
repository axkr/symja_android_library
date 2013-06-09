package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CI;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.evalExpandAll;
import static org.matheclipse.core.expression.F.fraction;
import static org.matheclipse.core.expression.F.integer;

import java.util.SortedMap;

import org.matheclipse.core.convert.ExprVariables;
import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;

import edu.jas.arith.BigRational;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.Monomial;
import edu.jas.ufd.FactorAbstract;
import edu.jas.ufd.FactorFactory;

/**
 * Determine the numerical roots of a univariate polynomial
 * 
 * See Wikipedia entries for: <a
 * href="http://en.wikipedia.org/wiki/Quadratic_equation">Quadratic equation
 * </a>, <a href="http://en.wikipedia.org/wiki/Cubic_function">Cubic
 * function</a> and <a
 * href="http://en.wikipedia.org/wiki/Quartic_function">Quartic function</a>
 * 
 * @see Roots
 */
public class NRoots extends AbstractFunctionEvaluator {

	public NRoots() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		if (ast.size() != 2) {
			return null;
		}
		IExpr temp = roots(ast);
		if (temp == null || !temp.isList()) {
			return null;
		}
		IAST list = (IAST) temp;
		IAST result = F.List();
		for (int i = 1; i < list.size(); i++) {
			result.add(F.evaln(list.get(i)));
		}
		return result;
	}

	protected static IAST roots(final IAST ast) {
		ExprVariables eVar = new ExprVariables(ast.get(1));
		if (!eVar.isSize(1)) {
			// factor only possible for univariate polynomials
			return null;
		}
		IExpr expr = evalExpandAll(ast.get(1));
		IAST variables = eVar.getVarList();
		IExpr denom = F.C1;
		if (expr.isAST()) {
			expr = Together.together((IAST) expr);

			// split expr into numerator and denominator
			denom = F.eval(F.Denominator(expr));
			if (!denom.isOne()) {
				// search roots for the numerator expression
				expr = F.eval(F.Numerator(expr));
			}
		}
		return rootsOfVariable(expr, denom, variables);
	}

	protected static IAST rootsOfVariable(final IExpr expr, final IExpr denom, final IAST variables) {
		IAST result = List();
		IAST resultList = RootIntervals.croots(expr, true);
		if (resultList == null) {
			return null;
		}
		if (resultList.size() > 0) {
			result.addAll(resultList);
		}
		return result;

	}

}