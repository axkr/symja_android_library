package org.matheclipse.core.reflection.system;

import java.util.List;
import java.util.SortedMap;

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
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

import edu.jas.arith.BigRational;
import edu.jas.arith.ModLong;
import edu.jas.arith.ModLongRing;
import edu.jas.poly.Complex;
import edu.jas.poly.ComplexRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.TermOrder;
import edu.jas.poly.TermOrderByName;
import edu.jas.ufd.FactorAbstract;
import edu.jas.ufd.FactorComplex;
import edu.jas.ufd.FactorFactory;

/**
 * Factor a univariate polynomial
 * 
 */
public class Factor extends AbstractFunctionEvaluator {

	public Factor() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2, 3);

		VariablesSet eVar = new VariablesSet(ast.arg1());
		// if (!eVar.isSize(1)) {
		// throw new WrongArgumentType(ast, ast.arg1(), 1,
		// "Factorization only implemented for univariate polynomials");
		// }
		try {
			IExpr expr = F.evalExpandAll(ast.arg1());
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

		GenPolynomial<BigRational> polyRat = jas.expr2JAS(expr, false);
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
		GenPolynomial<BigRational> polyRat = jas.expr2JAS(expr, false);
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

	/**
	 * Factor the <code>expr</code> with the option given in <code>ast</code>.
	 * 
	 * @param ast
	 * @param expr
	 * @param varList
	 * @param factorSquareFree
	 * @return
	 * @throws JASConversionException
	 */
	public static IExpr factorWithOption(final IAST ast, IExpr expr, List<IExpr> varList, boolean factorSquareFree)
			throws JASConversionException {
		final Options options = new Options(ast.topHead(), ast, 2);
		IExpr option = options.getOption("Modulus");
		if (option != null && option.isSignedNumber()) {
			return factorModulus(expr, varList, factorSquareFree, option);
		}
		option = options.getOption("GaussianIntegers");
		if (option != null && option.isTrue()) {
			return factorComplex(expr, varList, F.Times, false, false);
		}
		option = options.getOption("Extension");
		if (option != null && option.equals(F.CI)) {
			return factorComplex(expr, varList, F.Times, false, false);
		}
		return null; // no evaluation
	}

	/**
	 * Factor the <code>expr</code> in the domain of GaussianIntegers.
	 * 
	 * @param expr
	 * @param varList
	 * @param head
	 *            the head of the result AST
	 * @param noGCDLCM
	 * @param numeric2Rational TODO
	 * @return
	 * @throws JASConversionException
	 */
	public static IAST factorComplex(IExpr expr, List<IExpr> varList, ISymbol head, boolean noGCDLCM, boolean numeric2Rational) throws JASConversionException {
		JASConvert<BigRational> jas = new JASConvert<BigRational>(varList, BigRational.ZERO);
		GenPolynomial<BigRational> polyRat = jas.expr2JAS(expr, numeric2Rational);
		return factorComplex(polyRat, jas, varList, head, noGCDLCM);
	}

	public static IAST factorComplex(GenPolynomial<BigRational> polyRat, JASConvert<BigRational> jas, List<IExpr> varList,
			ISymbol head, boolean noGCDLCM) {
		TermOrder termOrder = TermOrderByName.Lexicographic;
		// Object[] objects = jas.factorTerms(polyRat);
		String[] vars = new String[varList.size()];
		for (int i = 0; i < varList.size(); i++) {
			vars[i] = varList.get(i).toString();
		}
		Object[] objects = JASConvert.rationalFromRationalCoefficientsFactor(new GenPolynomialRing<BigRational>(BigRational.ZERO,
				varList.size(), termOrder, vars), polyRat);
		java.math.BigInteger gcd = (java.math.BigInteger) objects[0];
		java.math.BigInteger lcm = (java.math.BigInteger) objects[1];
		GenPolynomial<BigRational> poly = (GenPolynomial<BigRational>) objects[2];

		ComplexRing<BigRational> cfac = new ComplexRing<BigRational>(BigRational.ZERO);
		GenPolynomialRing<Complex<BigRational>> cpfac = new GenPolynomialRing<Complex<BigRational>>(cfac, 1, termOrder);
		GenPolynomial<Complex<BigRational>> a = PolyUtil.complexFromAny(cpfac, poly);
		FactorComplex<BigRational> factorAbstract = new FactorComplex<BigRational>(cfac);
		SortedMap<GenPolynomial<Complex<BigRational>>, Long> map = factorAbstract.factors(a);

		IAST result = F.ast(head);
		if (!noGCDLCM) {
			if (!gcd.equals(java.math.BigInteger.ONE) || !lcm.equals(java.math.BigInteger.ONE)) {
				result.add(F.fraction(gcd, lcm));
			}
		}
		GenPolynomial<Complex<BigRational>> temp;
		for (SortedMap.Entry<GenPolynomial<Complex<BigRational>>, Long> entry : map.entrySet()) {
			if (entry.getKey().isONE() && entry.getValue().equals(1L)) {
				continue;
			}
			temp = entry.getKey();
			result.add(F.Power(jas.complexPoly2Expr(entry.getKey()), F.integer(entry.getValue())));
		}
		return result;
	}

	private static IAST factorModulus(IExpr expr, List<IExpr> varList, boolean factorSquareFree, IExpr option)
			throws JASConversionException {
		try {
			// found "Modulus" option => use ModIntegerRing
			ModLongRing modIntegerRing = JASModInteger.option2ModLongRing((ISignedNumber) option);
			JASModInteger jas = new JASModInteger(varList, modIntegerRing);
			GenPolynomial<ModLong> poly = jas.expr2JAS(expr);

			return factorModulus(jas, modIntegerRing, poly, factorSquareFree);
		} catch (ArithmeticException ae) {
			// toInt() conversion failed
			if (Config.DEBUG) {
				ae.printStackTrace();
			}
		}
		return null;
	}

	public static IAST factorModulus(JASModInteger jas, ModLongRing modIntegerRing, GenPolynomial<ModLong> poly,
			boolean factorSquareFree) {
		FactorAbstract<ModLong> factorAbstract = FactorFactory.getImplementation(modIntegerRing);
		SortedMap<GenPolynomial<ModLong>, Long> map;
		if (factorSquareFree) {
			map = factorAbstract.squarefreeFactors(poly);
		} else {
			map = factorAbstract.factors(poly);
		}
		IAST result = F.Times();
		for (SortedMap.Entry<GenPolynomial<ModLong>, Long> entry : map.entrySet()) {
			GenPolynomial<ModLong> singleFactor = entry.getKey();
			Long val = entry.getValue();
			result.add(F.Power(jas.modLongPoly2Expr(singleFactor), F.integer(val)));
		}
		return result;
	}

}