package org.matheclipse.core.reflection.system;

import java.util.Comparator;
import java.util.List;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.JASModInteger;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.ExponentArray;
import org.matheclipse.core.polynomials.Polynomial;

import edu.jas.arith.ModLong;
import edu.jas.arith.ModLongRing;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.Monomial;
import edu.jas.poly.TermOrder;

/**
 * Get the list of monomials of a polynomial expression.
 * 
 * See <a href="http://en.wikipedia.org/wiki/Monomial">Wikipedia - Monomial<a/>
 */
public class MonomialList extends AbstractFunctionEvaluator {

	public MonomialList() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 2, 5);

		IExpr expr = F.evalExpandAll(ast.arg1());
		IAST vars = F.List();
		VariablesSet eVar;
		if (ast.size() == 2) {
			// extract all variables from the polynomial expression
			eVar = new VariablesSet(ast.arg1());
			eVar.appendToList(vars);
		} else {
			IAST symbolList = Validate.checkSymbolOrSymbolList(ast, 2);
			eVar = new VariablesSet(symbolList);
			eVar.appendToList(vars);
		}
		int termOrder = TermOrder.INVLEX;
		Comparator<ExponentArray> comparator = ExponentArray.lexicographic();
		try {
			if (ast.size() > 3) {
				if (ast.arg3() instanceof IStringX) {
					String orderStr = ast.arg3().toString();
					if (orderStr.equals("DegreeLexicographic")) {
						termOrder = TermOrder.LEX;
						comparator = ExponentArray.degreeLexicographic();
					} else {
						return null;
					}
				} else {
					final Options options = new Options(ast.topHead(), ast, 2);
					IExpr option = options.getOption("Modulus");
					if (option != null && option.isSignedNumber()) {
						return monomialListModulus(expr, eVar.getArrayList(), termOrder, option);
					} else {
						return null;
					}
				}
			}
			Polynomial poly = new Polynomial(expr, vars, comparator);
			if (poly.isPolynomial()) {
				return poly.monomialList();
			}
			throw new WrongArgumentType(ast, expr, 1, "Polynomial expected!");
			// return monomialList(expr, eVar.getArrayList(), termOrder);
		} catch (JASConversionException jce) {
			// toInt() conversion failed
			if (Config.DEBUG) {
				jce.printStackTrace();
			}
		}
		return null;
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
	// public static IAST monomialList(IExpr polynomial, final List<IExpr> variablesList, final int termOrder)
	// throws JASConversionException {
	// JASIExpr jas = new JASIExpr(variablesList, new ExprRingFactory(), new TermOrder(termOrder), false);
	// GenPolynomial<IExpr> polyExpr = jas.expr2IExprJAS(polynomial);
	// IAST list = F.List();
	// for (Monomial<IExpr> monomial : polyExpr) {
	// IExpr coeff = monomial.coefficient();
	// ExpVector exp = monomial.exponent();
	// IAST monomTimes = F.Times(coeff);
	// long lExp;
	// ISymbol variable;
	// for (int i = 0; i < exp.length(); i++) {
	// lExp = exp.getVal(i);
	// if (lExp != 0) {
	// variable = (ISymbol) variablesList.get(i);
	// monomTimes.add(F.Power(variable, F.integer(lExp)));
	// }
	// }
	// list.add(monomTimes);
	// }
	// return list;
	// }

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
	private static IAST monomialListModulus(IExpr polynomial, List<ISymbol> variablesList, final int termOrder, IExpr option)
			throws JASConversionException {
		try {
			// found "Modulus" option => use ModIntegerRing
			ModLongRing modIntegerRing = JASModInteger.option2ModLongRing((ISignedNumber) option);
			JASModInteger jas = new JASModInteger(variablesList, modIntegerRing);
			GenPolynomial<ModLong> polyExpr = jas.expr2JAS(polynomial);
			IAST list = F.List();
			for (Monomial<ModLong> monomial : polyExpr) {
				ModLong coeff = monomial.coefficient();
				ExpVector exp = monomial.exponent();
				IAST monomTimes = F.Times(F.integer(coeff.getVal()));
				long lExp;
				ISymbol variable;
				for (int i = 0; i < exp.length(); i++) {
					lExp = exp.getVal(i);
					if (lExp != 0) {
						variable = (ISymbol) variablesList.get(i);
						monomTimes.add(F.Power(variable, F.integer(lExp)));
					}
				}
				list.add(monomTimes);
			}
			return list;
		} catch (ArithmeticException ae) {
			// toInt() conversion failed
			if (Config.DEBUG) {
				ae.printStackTrace();
			}
		}
		return null;
	}

}