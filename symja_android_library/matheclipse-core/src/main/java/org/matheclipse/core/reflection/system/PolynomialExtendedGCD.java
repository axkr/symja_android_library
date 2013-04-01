package org.matheclipse.core.reflection.system;

import java.math.BigInteger;
import java.util.List;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.ExprVariables;
import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.expression.ASTRange;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

import edu.jas.arith.BigRational;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.poly.GenPolynomial;

/**
 * Greatest common divisor of two polynomials. See: <a href=
 * "http://en.wikipedia.org/wiki/Greatest_common_divisor_of_two_polynomials"
 * >Wikipedia:Greatest common divisor of two polynomials</a>
 */
public class PolynomialExtendedGCD extends AbstractFunctionEvaluator {

	public PolynomialExtendedGCD() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 4, 5);
		if (!(ast.get(3) instanceof ISymbol)) {
			return null;
		}

		ISymbol x = (ISymbol) ast.get(3);
		IExpr expr1 = F.evalExpandAll(ast.get(1));
		IExpr expr2 = F.evalExpandAll(ast.get(2));
		ExprVariables eVar = new ExprVariables(expr1);
		if (!eVar.isSize(1) || !eVar.contains(x)) {
			// egcd only possible for univariate polynomials
			return null;
		}
		eVar = new ExprVariables(expr2);
		if (!eVar.isSize(1) || !eVar.contains(x)) {
			// egcd only possible for univariate polynomials
			return null;
		}
		ASTRange r = new ASTRange(eVar.getVarList(), 1);
		if (ast.size() == 5) {
			List<IExpr> varList = r.toList();
			final Options options = new Options(ast.topHead(), ast, 4);
			IExpr option = options.getOption("Modulus");
			if (option != null && option.isInteger()) {
				try {
					// found "Modulus" option => use ModIntegerRing
					final BigInteger value = ((IInteger) option).getBigNumerator();
					int intValue = ((IInteger) option).toInt();
					ModIntegerRing modIntegerRing = new ModIntegerRing(intValue, value.isProbablePrime(32));
					JASConvert<ModInteger> jas = new JASConvert<ModInteger>(varList, modIntegerRing);
					GenPolynomial<ModInteger> poly1 = jas.expr2JAS(expr1);
					GenPolynomial<ModInteger> poly2 = jas.expr2JAS(expr2);
					GenPolynomial<ModInteger>[] result = poly1.egcd(poly2);
					IAST list = F.List();
					list.add(jas.modIntegerPoly2Expr(result[0]));
					IAST subList = F.List();
					subList.add(jas.modIntegerPoly2Expr(result[1]));
					subList.add(jas.modIntegerPoly2Expr(result[2]));
					list.add(subList);
					return list;
				} catch (JASConversionException e) {
					if (Config.DEBUG) {
						e.printStackTrace();
					}
				}
				return null;
			}
		}

		try {
			JASConvert<BigRational> jas = new JASConvert<BigRational>(r.toList(), BigRational.ZERO);
			GenPolynomial<BigRational> poly1 = jas.expr2JAS(expr1);
			GenPolynomial<BigRational> poly2 = jas.expr2JAS(expr2);
			GenPolynomial<BigRational>[] result = poly1.egcd(poly2);
			IAST list = F.List();
			list.add(jas.rationalPoly2Expr(result[0]));
			IAST subList = F.List();
			subList.add(jas.rationalPoly2Expr(result[1]));
			subList.add(jas.rationalPoly2Expr(result[2]));
			list.add(subList);
			return list;
		} catch (JASConversionException e) {
			if (Config.DEBUG) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}
}