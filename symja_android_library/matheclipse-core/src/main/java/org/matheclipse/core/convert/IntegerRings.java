package org.matheclipse.core.convert;

import java.util.ArrayList;

import org.matheclipse.core.expression.BigIntegerSym;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

//import cc.redberry.rings.Ring;
//import cc.redberry.rings.Rings;
//import cc.redberry.rings.bigint.BigInteger;
//import cc.redberry.rings.poly.PolynomialFactorDecomposition;
//import cc.redberry.rings.poly.PolynomialMethods;
//import cc.redberry.rings.poly.multivar.Monomial;
//import cc.redberry.rings.poly.multivar.MonomialOrder;
//import cc.redberry.rings.poly.multivar.MultivariatePolynomial;

public class IntegerRings {
//	final int numberOfVariables;
//	final Ring<cc.redberry.rings.bigint.BigInteger> ring = Rings.Z;
//	final ArrayList<String> variables;
//	final ArrayList<ISymbol> symbols;
//
//	public IntegerRings(ArrayList<ISymbol> symbols) {
//		this.symbols = symbols;
//		this.variables = new ArrayList<String>(symbols.size());
//		for (int i = 0; i < symbols.size(); i++) {
//			variables.add(symbols.get(i).getSymbolName());
//		}
//		numberOfVariables = variables.size();
//	}
//
//	public IExpr toExpr(MultivariatePolynomial<BigInteger> poly) {
//		IASTAppendable plus = F.PlusAlloc(poly.size());
//		for (Monomial<BigInteger> monomial : poly) {
//			if (!monomial.coefficient.isZero()) {
//				IInteger coefficient = new BigIntegerSym(monomial.coefficient.toByteArray());
//				int[] exponents = monomial.exponents;
//				IASTAppendable times = F.TimesAlloc(1 + exponents.length);
//				if (!monomial.coefficient.isOne()) {
//					times.append(coefficient);
//				}
//				for (int i = 0; i < exponents.length; i++) {
//					if (exponents[i] != 0) {
//						if (exponents[i] == 1) {
//							times.append(symbols.get(i));
//						} else {
//							times.append(F.Power(symbols.get(i), exponents[i]));
//						}
//					}
//				}
//				plus.append(times.getOneIdentity(F.C0));
//			}
//		}
//		return plus;
//	}
//
//	public MultivariatePolynomial<BigInteger> toPolynomial(IExpr expr) {
//		if (expr.isPlus()) {
//			IAST plus = (IAST) expr;
//			MultivariatePolynomial<BigInteger> poly = MultivariatePolynomial.zero(numberOfVariables, ring,
//					MonomialOrder.DEFAULT);
//			for (int i = 1; i < plus.size(); i++) {
//				poly.add(toPolynomial(plus.get(i)));
//			}
//			return poly;
//		} else if (expr.isTimes()) {
//			IAST times = (IAST) expr;
//			MultivariatePolynomial<BigInteger> poly = MultivariatePolynomial.one(numberOfVariables, ring,
//					MonomialOrder.DEFAULT);
//			for (int i = 1; i < times.size(); i++) {
//				poly.multiply(toPolynomial(times.get(i)));
//			}
//			return poly;
//		} else if (expr.isPower()) {
//			IAST power = (IAST) expr;
//			int exp = power.exponent().toIntDefault(Integer.MIN_VALUE);
//			if (power.base().isSymbol()) {
//				ISymbol symbol = (ISymbol) power.base();
//				cc.redberry.rings.bigint.BigInteger coefficient = ring.getOne();
//				int[] exponents = new int[numberOfVariables];
//				exponents(symbol, exp, exponents);
//				@SuppressWarnings("unchecked")
//				MultivariatePolynomial<BigInteger> poly = MultivariatePolynomial.create(numberOfVariables, ring,
//						MonomialOrder.DEFAULT, new Monomial<BigInteger>(exponents, coefficient));
//				return poly;
//			}
//		} else if (expr.isSymbol()) {
//			ISymbol symbol = (ISymbol) expr;
//			cc.redberry.rings.bigint.BigInteger coefficient = ring.getOne();
//			int[] exponents = new int[numberOfVariables];
//			exponents(symbol, 1, exponents);
//			@SuppressWarnings("unchecked")
//			MultivariatePolynomial<BigInteger> poly = MultivariatePolynomial.create(numberOfVariables, ring,
//					MonomialOrder.DEFAULT, new Monomial<BigInteger>(exponents, coefficient));
//			return poly;
//		} else if (expr.isInteger()) {
//			java.math.BigInteger javaBigInt = ((IInteger) expr).toBigNumerator();
//			cc.redberry.rings.bigint.BigInteger coefficient = new cc.redberry.rings.bigint.BigInteger(javaBigInt);
//			@SuppressWarnings("unchecked")
//			MultivariatePolynomial<BigInteger> poly = MultivariatePolynomial.create(numberOfVariables, ring,
//					MonomialOrder.DEFAULT, new Monomial<BigInteger>(numberOfVariables, coefficient));
//			return poly;
//		} 
//		return null;
//	}
//
//	private int[] exponents(ISymbol base, int exponent, int[] exponents) {
//		if (exponents == null) {
//			exponents = new int[numberOfVariables];
//		}
//		for (int i = 0; i < variables.size(); i++) {
//			if (base.getSymbolName().equals(variables.get(i))) {
//				exponents[i] = exponent;
//			}
//		}
//		return exponents;
//	}
//
//	public static void main(String[] args) {
//		// ArrayList<String> vars = new ArrayList<String>();
//		// vars.add("x");
//		// vars.add("y");
//		ArrayList<ISymbol> vars = new ArrayList<ISymbol>();
//		vars.add(F.x);
//		vars.add(F.y);
//		IntegerRings formula = new IntegerRings(vars);
//		MultivariatePolynomial<BigInteger> poly = formula
//				.toPolynomial(F.Plus(F.Power(F.x, F.C2), F.Times(F.CN1, F.Power(F.y, F.C2))));
//		System.out.println(poly.toString());
//		PolynomialFactorDecomposition<MultivariatePolynomial<BigInteger>> result = PolynomialMethods.Factor(poly);
//		MultivariatePolynomial<BigInteger> p = result.get(0);
//		IExpr first = formula.toExpr(p);
//		System.out.println(first.toString());
//		p = result.get(1);
//		IExpr second = formula.toExpr(p);
//		System.out.println(second.toString());
//		Assert.assertEquals(2, PolynomialMethods.Factor(poly).size());
//	}
}
