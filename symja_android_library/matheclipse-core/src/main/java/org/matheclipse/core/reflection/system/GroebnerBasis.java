package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;

import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

import edu.jas.application.GBAlgorithmBuilder;
import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.gb.GroebnerBaseAbstract;
import edu.jas.gbufd.GroebnerBasePartial;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.OptimizedPolynomialList;
import edu.jas.poly.OrderedPolynomialList;
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

			return computeGroebnerBasis(polys, vars, termOrder);
		}
		return F.NIL;
	}

	/**
	 * 
	 * @param listOfPolynomials
	 *            a list of polynomials
	 * @param listOfVariables
	 *            a list of variable symbols
	 * @param termOrder
	 *            the term order
	 * @return <code>F.NIL</code> if
	 *         <code>stopUnevaluatedOnPolynomialConversionError==true</code> and
	 *         one of the polynomials in <code>listOfPolynomials</code> are not
	 *         convertible to JAS polynomials
	 */
	private static IAST computeGroebnerBasis(IAST listOfPolynomials, IAST listOfVariables, TermOrder termOrder) {
		List<ISymbol> varList = new ArrayList<ISymbol>(listOfVariables.size() - 1);
		String[] pvars = new String[listOfVariables.size() - 1];

		for (int i = 1; i < listOfVariables.size(); i++) {
			if (!listOfVariables.get(i).isSymbol()) {
				return F.NIL;
			}
			varList.add((ISymbol) listOfVariables.get(i));
			pvars[i - 1] = ((ISymbol) listOfVariables.get(i)).toString();
		}

		List<GenPolynomial<BigRational>> polyList = new ArrayList<GenPolynomial<BigRational>>(
				listOfPolynomials.size() - 1);
		JASConvert<BigRational> jas = new JASConvert<BigRational>(varList, BigRational.ZERO, termOrder);
		for (int i = 1; i < listOfPolynomials.size(); i++) {
			IExpr expr = F.evalExpandAll(listOfPolynomials.get(i));
			try {
				GenPolynomial<BigRational> poly = jas.expr2JAS(expr, false);
				polyList.add(poly);
			} catch (JASConversionException e) {
				return F.NIL;
			}

		}

		if (polyList.size() == 0) {
			return F.NIL;
		}
		GroebnerBasePartial<BigRational> gbp = new GroebnerBasePartial<BigRational>();
		OptimizedPolynomialList<BigRational> opl = gbp.partialGB(polyList, pvars);
		List<GenPolynomial<BigRational>> list = OrderedPolynomialList.sort(opl.list);
		IAST resultList = F.List();
		for (GenPolynomial<BigRational> p : list) {
			// convert rational to integer coefficients and add
			// polynomial to result list
			resultList.add(jas.integerPoly2Expr((GenPolynomial<BigInteger>) jas.factorTerms(p)[2]));
		}

		return resultList;
	}

	/**
	 * Used in <code>Solve()</code> function to reduce the polynomial list of
	 * equations.
	 * 
	 * @param listOfPolynomials
	 *            a list of polynomials
	 * @param listOfVariables
	 *            a list of variable symbols
	 * @return <code>F.NIL</code> if
	 *         <code>stopUnevaluatedOnPolynomialConversionError==true</code> and
	 *         one of the polynomials in <code>listOfPolynomials</code> are not
	 *         convertible to JAS polynomials
	 */
	public static IAST solveGroebnerBasis(IAST listOfPolynomials, IAST listOfVariables) {
		List<ISymbol> varList = new ArrayList<ISymbol>(listOfVariables.size() - 1);
		for (int i = 1; i < listOfVariables.size(); i++) {
			if (!listOfVariables.get(i).isSymbol()) {
				return F.NIL;
			}
			varList.add((ISymbol) listOfVariables.get(i));
		}

		IAST rest = F.List();
		List<GenPolynomial<BigRational>> polyList = new ArrayList<GenPolynomial<BigRational>>(
				listOfPolynomials.size() - 1);
		TermOrder termOrder = TermOrderByName.IGRLEX;
		JASConvert<BigRational> jas = new JASConvert<BigRational>(varList, BigRational.ZERO, termOrder);
		for (int i = 1; i < listOfPolynomials.size(); i++) {
			IExpr expr = F.evalExpandAll(listOfPolynomials.get(i));
			try {
				GenPolynomial<BigRational> poly = jas.expr2JAS(expr, false);
				polyList.add(poly);
			} catch (JASConversionException e) {
				rest.add(expr);
			}

		}

		if (polyList.size() == 0) {
			return F.NIL;
		}
		GroebnerBaseAbstract<BigRational> engine = GBAlgorithmBuilder
				.<BigRational> polynomialRing(jas.getPolynomialRingFactory()).fractionFree().syzygyPairlist().build();
		List<GenPolynomial<BigRational>> opl = engine.GB(polyList);
		IAST resultList = F.List();
		// convert rational to integer coefficients and add
		// polynomial to result list
		for (GenPolynomial<BigRational> p : opl) {
			resultList.add(jas.integerPoly2Expr((GenPolynomial<BigInteger>) jas.factorTerms(p)[2]));
		}
		for (int i = 1; i < rest.size(); i++) {
			resultList.add(rest.get(i));
		}
		return resultList;
	}

}