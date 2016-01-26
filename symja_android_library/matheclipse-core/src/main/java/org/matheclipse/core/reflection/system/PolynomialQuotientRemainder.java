package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.convert.JASIExpr;
import org.matheclipse.core.convert.JASModInteger;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.expression.ExprRingFactory;
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
 * 
 * See: <a href="http://en.wikipedia.org/wiki/Polynomial_long_division">Wikipedia :Polynomial long division</a>
 * 
 * @see org.matheclipse.core.reflection.system.PolynomialQuotient
 * @see org.matheclipse.core.reflection.system.PolynomialRemainder
 */
public class PolynomialQuotientRemainder extends AbstractFunctionEvaluator {

	public PolynomialQuotientRemainder() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 4, 5);
		ISymbol variable = Validate.checkSymbolType(ast, 3);
		IExpr arg1 = F.evalExpandAll(ast.arg1());
		IExpr arg2 = F.evalExpandAll(ast.arg2());

		if (ast.size() == 5) {
			final Options options = new Options(ast.topHead(), ast, 4, engine);
			IExpr option = options.getOption("Modulus");
			if (option != null && option.isSignedNumber()) {
				IExpr[] result = quotientRemainderModInteger(arg1, arg2, variable, option);
				if (result == null) {
					return F.UNEVALED;
				}
				IAST list = F.List();
				list.add(result[0]);
				list.add(result[1]);
				return list;
			}
			return F.UNEVALED;
		}
		IExpr[] result = quotientRemainder(arg1, arg2, variable);
		if (result == null) {
			return F.UNEVALED;
		}
		IAST list = F.List();
		list.add(result[0]);
		list.add(result[1]);
		return list;
	}

	public static IExpr[] quotientRemainder(final IExpr arg1, IExpr arg2, ISymbol variable) {

		try {
			JASConvert<BigRational> jas = new JASConvert<BigRational>(variable, BigRational.ZERO);
			GenPolynomial<BigRational> poly1 = jas.expr2JAS(arg1, false);
			GenPolynomial<BigRational> poly2 = jas.expr2JAS(arg2, false);
			GenPolynomial<BigRational>[] divRem = poly1.quotientRemainder(poly2);
			IExpr[] result = new IExpr[2];
			result[0] = jas.rationalPoly2Expr(divRem[0]);
			result[1] = jas.rationalPoly2Expr(divRem[1]);
			return result;
		} catch (JASConversionException e1) {
			try {
				JASIExpr jas = new JASIExpr(variable, new ExprRingFactory());
				GenPolynomial<IExpr> poly1 = jas.expr2IExprJAS(arg1);
				GenPolynomial<IExpr> poly2 = jas.expr2IExprJAS(arg2);
				GenPolynomial<IExpr>[] divRem = poly1.quotientRemainder(poly2);
				IExpr[] result = new IExpr[2];
				result[0] = jas.exprPoly2Expr(divRem[0], variable);
				result[1] = jas.exprPoly2Expr(divRem[1], variable);
				return result;
			} catch (JASConversionException e) {
				if (Config.DEBUG) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public IExpr[] quotientRemainderModInteger(IExpr arg1, IExpr arg2, ISymbol variable, IExpr option) {
		try {
			// found "Modulus" option => use ModIntegerRing
			ModLongRing modIntegerRing = JASModInteger.option2ModLongRing((ISignedNumber) option);
			JASModInteger jas = new JASModInteger(variable, modIntegerRing);
			GenPolynomial<ModLong> poly1 = jas.expr2JAS(arg1);
			GenPolynomial<ModLong> poly2 = jas.expr2JAS(arg2);
			GenPolynomial<ModLong>[] divRem = poly1.quotientRemainder(poly2);
			IExpr[] result = new IExpr[2];
			result[0] = jas.modLongPoly2Expr(divRem[0]);
			result[1] = jas.modLongPoly2Expr(divRem[1]);
			return result;
		} catch (JASConversionException e) {
			if (Config.DEBUG) {
				e.printStackTrace();
			}
		}
		return null;
	}

}