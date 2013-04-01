package org.matheclipse.core.reflection.system;

import java.math.BigInteger;
import java.util.List;
import java.util.SortedMap;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.ExprVariables;
import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.expression.ASTRange;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

import edu.jas.arith.BigRational;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.ufd.FactorAbstract;
import edu.jas.ufd.FactorFactory;

/**
 * Factor a univariate polynomial
 * 
 */
public class Factor extends AbstractFunctionEvaluator {

	public Factor() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		if (ast.size() != 2 && ast.size() != 3) {
			return null;
		}

		ExprVariables eVar = new ExprVariables(ast.get(1));
		// if (!eVar.isSize(1)) {
		// throw new WrongArgumentType(ast, ast.get(1), 1,
		// "Factorization only implemented for univariate polynomials");
		// }
		try {
			IExpr expr = F.evalExpandAll(ast.get(1));
			ASTRange r = new ASTRange(eVar.getVarList(), 1);
			List<IExpr> varList = r.toList();

			if (ast.size() == 3) {
				return factorWithOption(ast, expr, varList, false);
			}
			return factor(expr, varList, false);

		} catch (JASConversionException e) {
			if (Config.DEBUG) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static IExpr factor(IExpr expr, List<IExpr> varList, boolean factorSquareFree) throws JASConversionException {
		JASConvert<BigRational> jas = new JASConvert<BigRational>(varList, BigRational.ZERO);
		GenPolynomial<BigRational> polyRat = jas.expr2JAS(expr);
		Object[] objects = jas.factorTerms(polyRat);
		java.math.BigInteger gcd = (java.math.BigInteger) objects[0];
		java.math.BigInteger lcm = (java.math.BigInteger) objects[1];
		GenPolynomial<edu.jas.arith.BigInteger> poly = (GenPolynomial<edu.jas.arith.BigInteger>) objects[2];
		FactorAbstract<edu.jas.arith.BigInteger> factorAbstract = FactorFactory.getImplementation(edu.jas.arith.BigInteger.ONE);
		SortedMap<GenPolynomial<edu.jas.arith.BigInteger>, Long> map;
		if (factorSquareFree) {
			map = factorAbstract.squarefreeFactors(poly);// factors(poly);
		} else {
			map = factorAbstract.factors(poly);
		}
		IAST result = F.Times();
		if (!gcd.equals(java.math.BigInteger.ONE) || !lcm.equals(java.math.BigInteger.ONE)) {
			result.add(F.fraction(gcd, lcm));
		}
		for (SortedMap.Entry<GenPolynomial<edu.jas.arith.BigInteger>, Long> entry : map.entrySet()) {
			if (entry.getKey().isONE() && entry.getValue().equals(1L)) {
				continue;
			}
			result.add(F.Power(jas.integerPoly2Expr(entry.getKey()), F.integer(entry.getValue())));
		}
		return result;
	}

	public static IExpr factorList(IExpr expr, List<IExpr> varList, boolean factorSquareFree) throws JASConversionException {
		JASConvert<BigRational> jas = new JASConvert<BigRational>(varList, BigRational.ZERO);
		GenPolynomial<BigRational> polyRat = jas.expr2JAS(expr);
		Object[] objects = jas.factorTerms(polyRat);
		java.math.BigInteger gcd = (java.math.BigInteger) objects[0];
		java.math.BigInteger lcm = (java.math.BigInteger) objects[1];
		GenPolynomial<edu.jas.arith.BigInteger> poly = (GenPolynomial<edu.jas.arith.BigInteger>) objects[2];
		FactorAbstract<edu.jas.arith.BigInteger> factorAbstract = FactorFactory.getImplementation(edu.jas.arith.BigInteger.ONE);
		SortedMap<GenPolynomial<edu.jas.arith.BigInteger>, Long> map;
		if (factorSquareFree) {
			map = factorAbstract.squarefreeFactors(poly);// factors(poly);
		} else {
			map = factorAbstract.factors(poly);
		}
		IAST result = F.List();
		if (!gcd.equals(java.math.BigInteger.ONE) || !lcm.equals(java.math.BigInteger.ONE)) {
			result.add(F.List(F.fraction(gcd, lcm), F.C1));
		}
		for (SortedMap.Entry<GenPolynomial<edu.jas.arith.BigInteger>, Long> entry : map.entrySet()) {
			if (entry.getKey().isONE() && entry.getValue().equals(1L)) {
				continue;
			}
			result.add(F.List(jas.integerPoly2Expr(entry.getKey()), F.integer(entry.getValue())));
		}
		return result;
	}

	public static IExpr factorWithOption(final IAST ast, IExpr expr, List<IExpr> varList, boolean factorSquareFree)
			throws JASConversionException {
		final Options options = new Options(ast.topHead(), ast, 2);
		IExpr option = options.getOption("Modulus");
		if (option != null && option.isInteger()) {
			try {
				// found "Modulus" option => use ModIntegerRing
				final BigInteger value = ((IInteger) option).getBigNumerator();
				int intValue = ((IInteger) option).toInt();
				ModIntegerRing modIntegerRing = new ModIntegerRing(intValue, value.isProbablePrime(32));
				JASConvert<ModInteger> jas = new JASConvert<ModInteger>(varList, modIntegerRing);
				GenPolynomial<ModInteger> poly = jas.expr2JAS(expr);

				FactorAbstract<ModInteger> factorAbstract = FactorFactory.getImplementation(modIntegerRing);
				SortedMap<GenPolynomial<ModInteger>, Long> map;
				if (factorSquareFree) {
					map = factorAbstract.squarefreeFactors(poly);
				} else {
					map = factorAbstract.factors(poly);
				}
				IAST result = F.Times();
				for (SortedMap.Entry<GenPolynomial<ModInteger>, Long> entry : map.entrySet()) {
					GenPolynomial<ModInteger> singleFactor = entry.getKey();
					Long val = entry.getValue();
					result.add(F.Power(jas.modIntegerPoly2Expr(singleFactor), F.integer(val)));
				}
				return result;
			} catch (ArithmeticException ae) {
				// toInt() conversion failed
				if (Config.DEBUG) {
					ae.printStackTrace();
				}
			}
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
		return null; // no evaluation
	}

}