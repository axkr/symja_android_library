package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.JASIExpr;
import org.matheclipse.core.convert.JASModInteger;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.expression.ExprRingFactory;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.polynomials.ExprPolynomial;
import org.matheclipse.core.polynomials.ExprPolynomialRing;
import org.matheclipse.core.polynomials.ExprTermOrder;

import edu.jas.arith.ModLong;
import edu.jas.arith.ModLongRing;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.Monomial;
import edu.jas.poly.TermOrder;
import edu.jas.poly.TermOrderByName;

/**
 * <pre>
 * MonomialList(polynomial, list - of - variables)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * get the list of monomials of a <code>polynomial</code> expression, with respect to the
 * <code>list-of-variables</code>.
 * </p>
 * </blockquote>
 * <p>
 * See:<br />
 * </p>
 * <ul>
 * <li><a href="http://en.wikipedia.org/wiki/Monomial">Wikipedia - Monomial</a><br />
 * </li>
 * </ul>
 */
public class MonomialList extends AbstractFunctionEvaluator {
	/**
	 * Use the JAS library to represent the polynomials
	 */
	public final static boolean USE_JAS_POLYNOMIAL = true;

	public MonomialList() {
	}

	/**
	 * Get the list of monomials of a polynomial expression.
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Monomial">Wikipedia - Monomial<a/>
	 */
	@Override
	public IExpr evaluate(final IAST ast, final EvalEngine engine) {
		Validate.checkRange(ast, 2, 5);

		IExpr expr = F.evalExpandAll(ast.arg1(), engine);
		VariablesSet eVar;
		IAST symbolList = F.List();
		List<IExpr> varList;
		if (ast.isAST1()) {
			// extract all variables from the polynomial expression
			eVar = new VariablesSet(ast.arg1());
			eVar.appendToList(symbolList);
			varList = eVar.getArrayList();
		} else {
			symbolList = Validate.checkSymbolOrSymbolList(ast, 2);
			varList = new ArrayList<IExpr>(symbolList.size() - 1);
			for (int i = 1; i < symbolList.size(); i++) {
				varList.add(symbolList.get(i));
			}
		}
		TermOrder termOrder = TermOrderByName.Lexicographic;
		try {
			if (ast.size() > 3) {
				if (ast.arg3() instanceof IStringX) {
					String orderStr = ast.arg3().toString(); // NegativeLexicographic
					termOrder = Options.getMonomialOrder(orderStr, termOrder);
				}
				final Options options = new Options(ast.topHead(), ast, 2, engine);
				IExpr option = options.getOption("Modulus");
				if (option.isSignedNumber()) {
					return monomialListModulus(expr, varList, termOrder, option);
				}
			}
			if (USE_JAS_POLYNOMIAL) {
				return monomialList(expr, varList, termOrder);
			} else {
				ExprPolynomialRing ring = new ExprPolynomialRing(symbolList, new ExprTermOrder(termOrder.getEvord()));
				ExprPolynomial poly = ring.create(expr);
				return poly.monomialList();
			}
		} catch (JASConversionException jce) {
			// toInt() conversion failed
			if (Config.DEBUG) {
				jce.printStackTrace();
			}
		}
		return F.NIL;
	}

	/**
	 * Get the monomial list of a univariate polynomial.
	 * 
	 * @param polynomial
	 * @param variable
	 * @param termOrder
	 *            the JAS term ordering
	 * @return the list of monomials of the univariate polynomial.
	 */
	private static IAST monomialList(IExpr polynomial, final List<IExpr> variablesList, final TermOrder termOrder)
			throws JASConversionException {
		JASIExpr jas = new JASIExpr(variablesList, ExprRingFactory.CONST, termOrder, false);
		GenPolynomial<IExpr> polyExpr = jas.expr2IExprJAS(polynomial);

		Set<Entry<ExpVector, IExpr>> set = polyExpr.getMap().entrySet();
		IASTAppendable list = F.ListAlloc(set.size());
		for (Map.Entry<ExpVector, IExpr> monomial : set) {
			IExpr coeff = monomial.getValue();
			ExpVector exp = monomial.getKey();
			IASTAppendable monomTimes = F.TimesAlloc(exp.length() + 1);
			jas.monomialToExpr(coeff, exp, monomTimes);
			list.append(monomTimes);
		}

		return list;
	}

	/**
	 * Get the monomial list of a univariate polynomial with coefficients reduced by a modulo value.
	 * 
	 * @param polynomial
	 * @param variable
	 * @param termOrder
	 *            the JAS term ordering
	 * @param option
	 *            the &quot;Modulus&quot; option
	 * @return the list of monomials of the univariate polynomial.
	 */
	private static IAST monomialListModulus(IExpr polynomial, List<IExpr> variablesList, final TermOrder termOrder,
			IExpr option) throws JASConversionException {
		try {
			// found "Modulus" option => use ModIntegerRing
			ModLongRing modIntegerRing = JASModInteger.option2ModLongRing((ISignedNumber) option);
			JASModInteger jas = new JASModInteger(variablesList, modIntegerRing);
			GenPolynomial<ModLong> polyExpr = jas.expr2JAS(polynomial);
			IASTAppendable list = F.ListAlloc(polyExpr.length());
			for (Monomial<ModLong> monomial : polyExpr) {
				ModLong coeff = monomial.coefficient();
				ExpVector exp = monomial.exponent();
				IASTAppendable monomTimes = F.TimesAlloc(exp.length() + 1);
				jas.monomialToExpr(F.integer(coeff.getVal()), exp, monomTimes);
				list.append(monomTimes);
			}
			return list;
		} catch (ArithmeticException ae) {
			// toInt() conversion failed
			if (Config.DEBUG) {
				ae.printStackTrace();
			}
		}
		return F.NIL;
	}

}