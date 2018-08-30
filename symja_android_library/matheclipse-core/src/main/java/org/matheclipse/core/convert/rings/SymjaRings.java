package org.matheclipse.core.convert.rings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

import cc.redberry.rings.Rational;
import cc.redberry.rings.Rings;
import cc.redberry.rings.bigint.BigInteger;
import cc.redberry.rings.poly.MultivariateRing;
import cc.redberry.rings.poly.PolynomialFactorDecomposition;
import cc.redberry.rings.poly.PolynomialMethods;
import cc.redberry.rings.poly.multivar.Monomial;
import cc.redberry.rings.poly.multivar.MonomialOrder;
import cc.redberry.rings.poly.multivar.MultivariatePolynomial;

public class SymjaRings {
	final int numberOfVariables;
	final MultivariateRing<MultivariatePolynomial<Rational<BigInteger>>> ring;
	final ArrayList<String> variables;
	final List<IExpr> symbols;

	public SymjaRings(List<IExpr> symbols) {
		this.symbols = symbols;
		this.variables = new ArrayList<String>(symbols.size());
		for (int i = 0; i < symbols.size(); i++) {
			variables.add(symbols.get(i).toString());
		}
		numberOfVariables = variables.size();
		ring = Rings.MultivariateRing(numberOfVariables, Rings.Q);
	}

	public IExpr toExpr(MultivariatePolynomial<MultivariatePolynomial<Rational<BigInteger>>> poly) {
		IASTAppendable plus = F.PlusAlloc(poly.size());
		for (Monomial<MultivariatePolynomial<Rational<BigInteger>>> monomial : poly) {
			MultivariatePolynomial<Rational<BigInteger>> coeff = monomial.coefficient;
			if (!coeff.isZero()) {
				IExpr coefficient = toExprP(coeff);
				int[] exponents = monomial.exponents;
				IASTAppendable times = F.TimesAlloc(1 + exponents.length);
				times.append(coefficient);
				for (int i = 0; i < exponents.length; i++) {
					if (exponents[i] != 0) {
						if (exponents[i] == 1) {
							times.append(symbols.get(i));
						} else {
							times.append(F.Power(symbols.get(i), exponents[i]));
						}
					}
				}
				plus.append(times.getOneIdentity(F.C0));
			}
		}
		return plus.getOneIdentity(F.C0);
	}

	private IExpr toExprP(MultivariatePolynomial<Rational<BigInteger>> poly) {
		IASTAppendable plus = F.PlusAlloc(poly.size());
		for (Monomial<Rational<BigInteger>> monomial : poly) {
			Rational<BigInteger> coeff = monomial.coefficient;
			if (!coeff.isZero()) {
				Iterator<BigInteger> iter = coeff.stream().iterator();
				BigInteger numerator = iter.next();
				BigInteger denominator = iter.next();
				IFraction coefficient = F.fraction(new java.math.BigInteger(numerator.toByteArray()),
						new java.math.BigInteger(denominator.toByteArray()));
				int[] exponents = monomial.exponents;
				IASTAppendable times = F.TimesAlloc(1 + exponents.length);
				// if (!coeff.isOne()) {
				times.append(coefficient);
				// }
				for (int i = 0; i < exponents.length; i++) {
					if (exponents[i] != 0) {
						if (exponents[i] == 1) {
							times.append(symbols.get(i));
						} else {
							times.append(F.Power(symbols.get(i), exponents[i]));
						}
					}
				}
				plus.append(times.getOneIdentity(F.C0));
			}
		}
		return plus.getOneIdentity(F.C0);
	}

	public MultivariatePolynomial<MultivariatePolynomial<Rational<BigInteger>>> toPolynomial(IExpr expr) {
		if (expr.isPlus()) {
			IAST plus = (IAST) expr;
			MultivariatePolynomial<MultivariatePolynomial<Rational<BigInteger>>> poly = MultivariatePolynomial
					.zero(numberOfVariables, ring, MonomialOrder.DEFAULT);
			for (int i = 1; i < plus.size(); i++) {
				poly.add(toPolynomial(plus.get(i)));
			}
			return poly;
		} else if (expr.isTimes()) {
			IAST times = (IAST) expr;
			MultivariatePolynomial<MultivariatePolynomial<Rational<BigInteger>>> poly = MultivariatePolynomial
					.one(numberOfVariables, ring, MonomialOrder.DEFAULT);
			for (int i = 1; i < times.size(); i++) {
				poly.multiply(toPolynomial(times.get(i)));
			}
			return poly;
		} else if (expr.isPower()) {
			IAST power = (IAST) expr;
			int exp = power.exponent().toIntDefault(Integer.MIN_VALUE);
			if (power.base().isSymbol()) {
				ISymbol symbol = (ISymbol) power.base();
				MultivariatePolynomial<Rational<BigInteger>> coefficient = ring.getOne();
				int[] exponents = new int[numberOfVariables];
				exponents(symbol, exp, exponents);
				@SuppressWarnings("unchecked")
				MultivariatePolynomial<MultivariatePolynomial<Rational<BigInteger>>> poly = MultivariatePolynomial
						.create(numberOfVariables, ring, MonomialOrder.DEFAULT,
								new Monomial<MultivariatePolynomial<Rational<BigInteger>>>(exponents, coefficient));
				return poly;
			}
		} else if (expr.isSymbol()) {
			ISymbol symbol = (ISymbol) expr;
			MultivariatePolynomial<Rational<BigInteger>> coefficient = ring.getOne();
			int[] exponents = new int[numberOfVariables];
			exponents(symbol, 1, exponents);
			@SuppressWarnings("unchecked")
			MultivariatePolynomial<MultivariatePolynomial<Rational<BigInteger>>> poly = MultivariatePolynomial.create(
					numberOfVariables, ring, MonomialOrder.DEFAULT,
					new Monomial<MultivariatePolynomial<Rational<BigInteger>>>(exponents, coefficient));
			return poly;
		} else if (expr.isInteger()) {
			java.math.BigInteger javaBigInt = ((IInteger) expr).toBigNumerator();
			MultivariatePolynomial<Rational<BigInteger>> coefficient = ring
					.valueOfBigInteger(new BigInteger(javaBigInt));
			@SuppressWarnings("unchecked")
			MultivariatePolynomial<MultivariatePolynomial<Rational<BigInteger>>> poly = MultivariatePolynomial.create(
					numberOfVariables, ring, MonomialOrder.DEFAULT,
					new Monomial<MultivariatePolynomial<Rational<BigInteger>>>(numberOfVariables, coefficient));
			return poly;
		} else if (expr.isFraction()) {
			Rational<BigInteger> coefficient = new Rational<BigInteger>(Rings.Z,
					new BigInteger(((IFraction) expr).toBigNumerator()),
					new BigInteger(((IFraction) expr).toBigDenominator()));
			MultivariatePolynomial<Rational<BigInteger>> coefficientPolynomial = ring.getOne().multiply(coefficient);
			@SuppressWarnings("unchecked")
			MultivariatePolynomial<MultivariatePolynomial<Rational<BigInteger>>> poly = MultivariatePolynomial.create(
					numberOfVariables, ring, MonomialOrder.DEFAULT,
					new Monomial<MultivariatePolynomial<Rational<BigInteger>>>(numberOfVariables,
							coefficientPolynomial));
			return poly;
		}
		return null;
	}

	private int[] exponents(ISymbol base, int exponent, int[] exponents) {
		if (exponents == null) {
			exponents = new int[numberOfVariables];
		}
		for (int i = 0; i < variables.size(); i++) {
			if (base.getSymbolName().equals(variables.get(i))) {
				exponents[i] = exponent;
			}
		}
		return exponents;
	}

	public static IExpr gcd(IAST ast, List<IExpr> varList, EvalEngine engine) {
		try {
			System.out.println(ast.toString());
			SymjaRings formula = new SymjaRings(varList);
			IAST[] asts = new IAST[2];
			MultivariatePolynomial<MultivariatePolynomial<Rational<BigInteger>>> polys[] = (MultivariatePolynomial<MultivariatePolynomial<Rational<BigInteger>>>[]) new MultivariatePolynomial[ast
					.argSize()];
			for (int i = 0; i < polys.length; i++) {
				IExpr expr = F.evalExpandAll(ast.get(i + 1), engine);
				polys[i] = formula.toPolynomial(expr);
			}
			MultivariatePolynomial<MultivariatePolynomial<Rational<BigInteger>>>  result = PolynomialMethods.PolynomialGCD(polys);
			System.out.println(result.toString());
			System.out.println();
			return formula.toExpr(result); 
		} catch (RuntimeException rex) {
			rex.printStackTrace();
		}
		return F.NIL;
	}

	public static IExpr factor(IExpr pExpr, List<IExpr> varList) {
		try {
			// System.out.println(pExpr.toString());
			SymjaRings formula = new SymjaRings(varList);
			MultivariatePolynomial<MultivariatePolynomial<Rational<BigInteger>>> poly = formula.toPolynomial(pExpr);
			PolynomialFactorDecomposition<MultivariatePolynomial<MultivariatePolynomial<Rational<BigInteger>>>> result = PolynomialMethods
					.Factor(poly);
			IASTAppendable times = F.TimesAlloc(result.size());

			if (!result.unit.isOne()) {
				times.append(formula.toExpr(result.unit));
			}
			for (int i = 0; i < result.size(); i++) {
				int exponent = result.getExponent(i);
				if (exponent == 1) {
					times.append(formula.toExpr(result.get(i)));
				} else {
					times.append(F.Power(formula.toExpr(result.get(i)), exponent));
				}
			}
			// System.out.println(times.toString());
			// System.out.println();
			return times.getOneIdentity(F.C0);
		} catch (RuntimeException rex) {
			rex.printStackTrace();
		}
		return pExpr;
	} 

}
