package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.convert.JASModInteger;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.expression.ASTRange;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.ExprReverseComparator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

import edu.jas.arith.BigInteger;
import edu.jas.arith.ModLong;
import edu.jas.arith.ModLongRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.ufd.GCDFactory;
import edu.jas.ufd.GreatestCommonDivisorAbstract;

/**
 * Least common multiple of two polynomials. See also: <a href=
 * "http://en.wikipedia.org/wiki/Greatest_common_divisor_of_two_polynomials" >Wikipedia:Greatest common divisor of two
 * polynomials</a>
 */
public class PolynomialLCM extends AbstractFunctionEvaluator {

	public PolynomialLCM() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 3);
		
		VariablesSet eVar = new VariablesSet();
		eVar.addVarList(ast, 1);
		
		ASTRange r = new ASTRange(eVar.getVarList(), 1);
		IExpr expr = F.evalExpandAll(ast.arg1());
		if (ast.size() > 3) {
			final Options options = new Options(ast.topHead(), ast, ast.size() - 1, engine);
			IExpr option = options.getOption("Modulus");
			if (option != null && option.isSignedNumber()) {
				try {
					// found "Modulus" option => use ModIntegerRing
					ModLongRing modIntegerRing = JASModInteger.option2ModLongRing((ISignedNumber) option);
					JASModInteger jas = new JASModInteger(r.toList(), modIntegerRing);
					GenPolynomial<ModLong> poly = jas.expr2JAS(expr);
					GenPolynomial<ModLong> temp;
					GreatestCommonDivisorAbstract<ModLong> factory = GCDFactory.getImplementation(modIntegerRing);
					for (int i = 2; i < ast.size() - 1; i++) {
						expr = F.evalExpandAll(ast.get(i));
						temp = jas.expr2JAS(expr);
						poly = factory.lcm(poly, temp);
					}

					return Factor.factorModulus(jas, modIntegerRing, poly.monic(), false);
					// return jas.modLongPoly2Expr(poly.monic());
				} catch (JASConversionException e) {
					if (Config.DEBUG) {
						e.printStackTrace();
					}
					return null;
				}
			}
		}
		try {
			JASConvert<BigInteger> jas = new JASConvert<BigInteger>(r.toList(), BigInteger.ZERO);
			GenPolynomial<BigInteger> poly = jas.expr2JAS(expr, false);
			GenPolynomial<BigInteger> temp;
			GreatestCommonDivisorAbstract<BigInteger> factory = GCDFactory.getImplementation(BigInteger.ZERO);
			for (int i = 2; i < ast.size(); i++) {
				expr = F.evalExpandAll(ast.get(i));
				temp = jas.expr2JAS(expr, false);
				poly = factory.lcm(poly, temp);
			}
			return jas.integerPoly2Expr(poly.monic());
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