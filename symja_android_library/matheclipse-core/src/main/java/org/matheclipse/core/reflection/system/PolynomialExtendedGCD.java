package org.matheclipse.core.reflection.system;

import java.util.List;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.convert.JASIExpr;
import org.matheclipse.core.convert.JASModInteger;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.expression.ASTRange;
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
 * Greatest common divisor of two polynomials. See: <a href=
 * "http://en.wikipedia.org/wiki/Greatest_common_divisor_of_two_polynomials" >Wikipedia:Greatest common divisor of two
 * polynomials</a>
 */
public class PolynomialExtendedGCD extends AbstractFunctionEvaluator {

	public PolynomialExtendedGCD() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 4, 5);

		ISymbol x = Validate.checkSymbolType(ast, 3);
		IExpr expr1 = F.evalExpandAll(ast.arg1());
		IExpr expr2 = F.evalExpandAll(ast.arg2());
		VariablesSet eVar = new VariablesSet();
		eVar.add(x);

		ASTRange r = new ASTRange(eVar.getVarList(), 1);
		if (ast.size() == 5) {
			List<IExpr> varList = r.toList();
			final Options options = new Options(ast.topHead(), ast, 4, engine);
			IExpr option = options.getOption("Modulus");
			if (option.isSignedNumber()) {
				try {
					// found "Modulus" option => use ModIntegerRing
					ModLongRing modIntegerRing = JASModInteger.option2ModLongRing((ISignedNumber) option);
					JASModInteger jas = new JASModInteger(varList, modIntegerRing);
					GenPolynomial<ModLong> poly1 = jas.expr2JAS(expr1);
					GenPolynomial<ModLong> poly2 = jas.expr2JAS(expr2);
					GenPolynomial<ModLong>[] result = poly1.egcd(poly2);
					IAST list = F.List();
					list.add(jas.modLongPoly2Expr(result[0]));
					IAST subList = F.List();
					subList.add(jas.modLongPoly2Expr(result[1]));
					subList.add(jas.modLongPoly2Expr(result[2]));
					list.add(subList);
					return list;
				} catch (JASConversionException e) {
					if (Config.DEBUG) {
						e.printStackTrace();
					}
				}
				return F.NIL;
			}
		}

		try {
			JASConvert<BigRational> jas = new JASConvert<BigRational>(r.toList(), BigRational.ZERO);
			GenPolynomial<BigRational> poly1 = jas.expr2JAS(expr1, false);
			GenPolynomial<BigRational> poly2 = jas.expr2JAS(expr2, false);
			GenPolynomial<BigRational>[] result = poly1.egcd(poly2);
			IAST list = F.List();
			list.add(jas.rationalPoly2Expr(result[0]));
			IAST subList = F.List();
			subList.add(jas.rationalPoly2Expr(result[1]));
			subList.add(jas.rationalPoly2Expr(result[2]));
			list.add(subList);
			return list;
		} catch (JASConversionException e0) {
			try {
				JASIExpr jas = new JASIExpr(r.toList(), new ExprRingFactory());
				GenPolynomial<IExpr> poly1 = jas.expr2IExprJAS(expr1);
				GenPolynomial<IExpr> poly2 = jas.expr2IExprJAS(expr2);
				GenPolynomial<IExpr>[] result = poly1.egcd(poly2);
				IAST list = F.List();
				list.add(jas.exprPoly2Expr(result[0], x));
				IAST subList = F.List();
				subList.add(F.eval(F.Together(jas.exprPoly2Expr(result[1], x))));
				subList.add(F.eval(F.Together(jas.exprPoly2Expr(result[2], x))));
				list.add(subList);
				return list;
			} catch (JASConversionException e) {
				// if (Config.DEBUG) {
				e.printStackTrace();
				// }
			}
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}
}