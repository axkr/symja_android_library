package org.matheclipse.core.reflection.system;

import java.util.List;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.expression.ASTRange;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.ExprReverseComparator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;

import edu.jas.arith.BigRational;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.ufd.FactorAbstract;
import edu.jas.ufd.FactorFactory;

/**
 * Check if a univariate polynomial is square free
 * 
 */
public class SquareFreeQ extends AbstractFunctionEvaluator {

	public SquareFreeQ() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2, 3);

		if (ast.arg1().isRational()) {
			// check for integers not implemented yet
			return null;
		}
		VariablesSet eVar = new VariablesSet(ast.arg1());
		if (eVar.isSize(0)) {
			if (ast.arg1().isAtom()) {
				return F.False;
			}
			eVar.add(F.$s("x"));
		}
		if (!eVar.isSize(1)) {
			throw new WrongArgumentType(ast, ast.arg1(), 1, "SquareFreeQ only implemented for univariate polynomials");
		}
		try {
			IExpr expr = F.evalExpandAll(ast.arg1());
			ASTRange r = new ASTRange(eVar.getVarList(), 1);
			List<IExpr> varList = r.toList();

			if (ast.size() == 3) {
				return F.bool(isSquarefreeWithOption(ast, expr, varList, engine));
			}
			return F.bool(isSquarefree(expr, varList));
		} catch (JASConversionException e) {
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static boolean isSquarefree(IExpr expr, List<IExpr> varList) throws JASConversionException {
		JASConvert<BigRational> jas = new JASConvert<BigRational>(varList, BigRational.ZERO);
		GenPolynomial<BigRational> poly = jas.expr2JAS(expr, false);

		FactorAbstract<BigRational> factorAbstract = FactorFactory.getImplementation(BigRational.ONE);
		return factorAbstract.isSquarefree(poly);
	}

	public static boolean isSquarefreeWithOption(final IAST lst, IExpr expr, List<IExpr> varList, final EvalEngine engine) throws JASConversionException {
		final Options options = new Options(lst.topHead(), lst, 2, engine);
		IExpr option = options.getOption("Modulus");
		if (option != null && option.isSignedNumber()) {

			// found "Modulus" option => use ModIntegerRing
			ModIntegerRing modIntegerRing = JASConvert.option2ModIntegerRing((ISignedNumber)option);
			JASConvert<ModInteger> jas = new JASConvert<ModInteger>(varList, modIntegerRing);
			GenPolynomial<ModInteger> poly = jas.expr2JAS(expr, false);

			FactorAbstract<ModInteger> factorAbstract = FactorFactory.getImplementation(modIntegerRing);
			return factorAbstract.isSquarefree(poly);
		}
		// option = options.getOption("GaussianIntegers");
		// if (option != null && option.equals(F.True)) {
		// try {
		// ComplexRing<edu.jas.arith.BigInteger> fac = new
		// ComplexRing<edu.jas.arith.BigInteger>(edu.jas.arith.BigInteger.ONE);
		//						
		// JASConvert<edu.jas.structure.Complex<edu.jas.arith.BigInteger>> jas =
		// new JASConvert<edu.jas.structure.Complex<edu.jas.arith.BigInteger>>(
		// varList, fac);
		// GenPolynomial<edu.jas.structure.Complex<edu.jas.arith.BigInteger>>
		// poly = jas.expr2Poly(expr);
		// FactorAbstract<edu.jas.structure.Complex<edu.jas.arith.BigInteger>>
		// factorAbstract = FactorFactory
		// .getImplementation(fac);
		// SortedMap<GenPolynomial<edu.jas.structure.Complex<edu.jas.arith.BigInteger>>,
		// Long> map = factorAbstract.factors(poly);
		// IAST result = F.Times();
		// for
		// (SortedMap.Entry<GenPolynomial<edu.jas.structure.Complex<edu.jas.arith.BigInteger>>,
		// Long> entry : map.entrySet()) {
		// GenPolynomial<edu.jas.structure.Complex<edu.jas.arith.BigInteger>>
		// singleFactor = entry.getKey();
		// // GenPolynomial<edu.jas.arith.BigComplex> integerCoefficientPoly
		// // = (GenPolynomial<edu.jas.arith.BigComplex>) jas
		// // .factorTerms(singleFactor)[2];
		// // Long val = entry.getValue();
		// // result.add(F.Power(jas.integerPoly2Expr(integerCoefficientPoly),
		// // F.integer(val)));
		// System.out.println(singleFactor);
		// }
		// return result;
		// } catch (ArithmeticException ae) {
		// // toInt() conversion failed
		// if (Config.DEBUG) {
		// ae.printStackTrace();
		// }
		// return null; // no evaluation
		// }
		// }
		return false; // no evaluation
	}

}