package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.ExprVariables;
import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.convert.JASModInteger;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.expression.ASTRange;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

import edu.jas.arith.BigRational;
import edu.jas.arith.ModLong;
import edu.jas.arith.ModLongRing;
import edu.jas.poly.GenPolynomial;

/**
 * Greatest common divisor of two polynomials. See: <a href=
 * "http://en.wikipedia.org/wiki/Greatest_common_divisor_of_two_polynomials" >Wikipedia:Greatest common divisor of two
 * polynomials</a>
 */
public class PolynomialGCD extends AbstractFunctionEvaluator {

	public PolynomialGCD() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 3);
		
		ExprVariables eVar = new ExprVariables(ast.arg1());
		if (!eVar.isSize(1)) {
			// gcd only possible for univariate polynomials
			return null;
		}
		IExpr expr = F.evalExpandAll(ast.arg1());
		if (ast.size() > 3 && ast.last().isRuleAST()) {
			return gcdWithOption(ast, expr, eVar);
		}
		try {
			ASTRange r = new ASTRange(eVar.getVarList(), 1);
			JASConvert<BigRational> jas = new JASConvert<BigRational>(r.toList(), BigRational.ZERO);
			GenPolynomial<BigRational> poly = jas.expr2JAS(expr);
			GenPolynomial<BigRational> temp;
			for (int i = 2; i < ast.size(); i++) {
				eVar = new ExprVariables(ast.get(i));
				if (!eVar.isSize(1)) {
					// gcd only possible for univariate polynomials
					return null;
				}
				expr = F.evalExpandAll(ast.get(i));
				temp = jas.expr2JAS(expr);
				poly = poly.gcd(temp);
			}
			return jas.rationalPoly2Expr(poly);
		} catch (JASConversionException e) {
			if (Config.DEBUG) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private IExpr gcdWithOption(final IAST ast, IExpr expr, ExprVariables eVar) {
		final Options options = new Options(ast.topHead(), ast, ast.size() - 1);
		IExpr option = options.getOption("Modulus");
		if (option != null && option.isSignedNumber()) {
			return modulusGCD(ast, expr, eVar, option);
		}
		return null;
	}

	private IExpr modulusGCD(final IAST ast, IExpr expr, ExprVariables eVar, IExpr option) {
		try {
			// found "Modulus" option => use ModIntegerRing
			ASTRange r = new ASTRange(eVar.getVarList(), 1);
			// ModIntegerRing modIntegerRing = JASConvert.option2ModIntegerRing((ISignedNumber) option);
			// JASConvert<ModInteger> jas = new JASConvert<ModInteger>(r.toList(), modIntegerRing);
			ModLongRing modIntegerRing = JASModInteger.option2ModLongRing((ISignedNumber) option);
			JASModInteger jas = new JASModInteger(r.toList(), modIntegerRing);
			GenPolynomial<ModLong> poly = jas.expr2JAS(expr);
			GenPolynomial<ModLong> temp;
			for (int i = 2; i < ast.size() - 1; i++) {
				eVar = new ExprVariables(ast.get(i));
				if (!eVar.isSize(1)) {
					// gcd only possible for univariate polynomials
					return null;
				}
				expr = F.evalExpandAll(ast.get(i));
				temp = jas.expr2JAS(expr);
				poly = poly.gcd(temp);
			}
			return Factor.factorModulus(jas, modIntegerRing, poly, false);
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