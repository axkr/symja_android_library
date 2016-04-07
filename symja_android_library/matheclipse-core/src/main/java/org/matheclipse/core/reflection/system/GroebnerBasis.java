package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.gbufd.GroebnerBasePartial;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.OptimizedPolynomialList;
import edu.jas.poly.TermOrder;
import edu.jas.poly.TermOrderByName;

/**
 * Compute the Groebner basis for a list of polynomials.
 * 
 * See
 * <ul>
 * <li><a href="https://en.wikipedia.org/wiki/Gr%C3%B6bner_basis">EN-Wikipedia:
 * Gröbner basis</a></li>
 * <li><a href="https://de.wikipedia.org/wiki/Gr%C3%B6bnerbasis">DE-Wikipedia:
 * Gröbner basis</a></li>
 * </ul>
 */
public class GroebnerBasis extends AbstractFunctionEvaluator {

	public GroebnerBasis() {
		// empty constructor
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (ast.size() >= 3) {

			if (ast.arg1().isVector() < 0 || ast.arg2().isVector() < 0) {
				return F.NIL;
			}
			TermOrder termOrder = TermOrderByName.Lexicographic;
			if (ast.size() > 3) {
				final Options options = new Options(ast.topHead(), ast, ast.size() - 1, engine);
				termOrder = options.getMonomialOrder(ast, termOrder);
			}

			IAST polys = (IAST) ast.arg1();
			IAST vars = (IAST) ast.arg2();
			if (vars.size() <= 1) {
				return F.NIL;
			}

			return computeGroebnerBasis(polys, vars, termOrder, true);
		}
		return F.NIL;
	}

	/**
	 * Try to compute a Grobner basis for all expressions in
	 * <code>listOfPolynomials</code> which are polynomials for the given
	 * <code>listOfVariables</code>. Append the non-polynomial expressions at
	 * the end of the resulting list if necessary.
	 * 
	 * @param listOfPolynomials
	 *            a list of polynomials
	 * @param listOfVariables
	 *            a list of variable symbols
	 * @param stopUnevaluatedOnPolynomialConversionError
	 * @return <code>F.NIL</code> if
	 *         <code>stopUnevaluatedOnPolynomialConversionError==true</code> and
	 *         one of the polynomials in <code>listOfPolynomials</code> are not
	 *         convertible to JAS polynomials
	 * @return
	 */
	public static IAST computeGroebnerBasis(IAST listOfPolynomials, IAST listOfVariables,
			boolean stopUnevaluatedOnPolynomialConversionError) {
		TermOrder termOrder = TermOrderByName.Lexicographic;
		return computeGroebnerBasis(listOfPolynomials, listOfVariables, termOrder,
				stopUnevaluatedOnPolynomialConversionError);
	}

	/**
	 * 
	 * @param listOfPolynomials
	 *            a list of polynomials
	 * @param listOfVariables
	 *            a list of variable symbols
	 * @param termOrder
	 *            the term order
	 * @param stopUnevaluatedOnPolynomialConversionError
	 * @return <code>F.NIL</code> if
	 *         <code>stopUnevaluatedOnPolynomialConversionError==true</code> and
	 *         one of the polynomials in <code>listOfPolynomials</code> are not
	 *         convertible to JAS polynomials
	 */
	private static IAST computeGroebnerBasis(IAST listOfPolynomials, IAST listOfVariables, TermOrder termOrder,
			boolean stopUnevaluatedOnPolynomialConversionError) {
		List<ISymbol> varList = new ArrayList<ISymbol>(listOfVariables.size() - 1);
		String[] pvars = new String[listOfVariables.size() - 1];

		for (int i = 1; i < listOfVariables.size(); i++) {
			if (!listOfVariables.get(i).isSymbol()) {
				return F.NIL;
			}
			varList.add((ISymbol) listOfVariables.get(i));
			pvars[i - 1] = ((ISymbol) listOfVariables.get(i)).toString();
		}
		IAST rest = F.List();
		GroebnerBasePartial<BigRational> gbp = new GroebnerBasePartial<BigRational>();
		List<GenPolynomial<BigRational>> polyList = new ArrayList<GenPolynomial<BigRational>>(
				listOfPolynomials.size() - 1);
		JASConvert<BigRational> jas = new JASConvert<BigRational>(varList, BigRational.ZERO, termOrder);
		for (int i = 1; i < listOfPolynomials.size(); i++) {
			IExpr expr = F.evalExpandAll(listOfPolynomials.get(i));
			try {
				GenPolynomial<BigRational> poly = jas.expr2JAS(expr, false);
				polyList.add(poly);
			} catch (JASConversionException e) {
				if (stopUnevaluatedOnPolynomialConversionError) {
					return F.NIL;
				}
				rest.add(expr);
			}

		}

		if (polyList.size() == 0) {
			return F.NIL;
		}
		OptimizedPolynomialList<BigRational> opl = gbp.partialGB(polyList, pvars);

		IAST resultList = F.List();
		for (GenPolynomial<BigRational> p : opl.list) {
			// convert rational to integer coefficients and add
			// polynomial to result list
			resultList.add(jas.integerPoly2Expr((GenPolynomial<BigInteger>) jas.factorTerms(p)[2]));
		}
		for (int i = 1; i < rest.size(); i++) {
			resultList.add(rest.get(i));
		}
		return resultList;
	}

}