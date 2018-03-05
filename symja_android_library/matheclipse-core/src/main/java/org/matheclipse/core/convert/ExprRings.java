package org.matheclipse.core.convert;

import java.util.ArrayList;
import java.util.Iterator;

import org.matheclipse.core.expression.BigIntegerSym;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

//import cc.redberry.rings.ARing;
//import cc.redberry.rings.bigint.BigInteger;
//import cc.redberry.rings.poly.PolynomialFactorDecomposition;
//import cc.redberry.rings.poly.PolynomialMethods;
//import cc.redberry.rings.poly.multivar.Monomial;
//import cc.redberry.rings.poly.multivar.MonomialOrder;
//import cc.redberry.rings.poly.multivar.MultivariatePolynomial;

public class ExprRings {
//	XRing CONST = new XRing();
//
//	public class XRing extends ARing<IExpr> {
//		public XRing() {
//
//		}
//
//		@Override
//		public boolean isField() {
//			return false;
//		}
//
//		@Override
//		public boolean isEuclideanRing() {
//			return true;
//		}
//
//		@Override
//		public BigInteger cardinality() {
//			return null;
//		}
//
//		@Override
//		public BigInteger characteristic() {
//			return BigInteger.ZERO;
//		}
//
//		@Override
//		public IExpr add(IExpr a, IExpr b) {
//			return a.add(b);
//		}
//
//		@Override
//		public IExpr subtract(IExpr a, IExpr b) {
//			return a.subtract(b);
//		}
//
//		@Override
//		public IExpr multiply(IExpr a, IExpr b) {
//			return a.multiply(b);
//		}
//
//		@Override
//		public IExpr negate(IExpr element) {
//			return element.negate();
//		}
//
//		@Override
//		public IExpr copy(IExpr element) {
//			if (element.isAST()) {
//				return ((IAST) element.copy());
//			}
//			return element;
//		}
//
//		@Override
//		public IExpr[] divideAndRemainder(IExpr dividend, IExpr divider) {
//			return dividend.quotientRemainder(divider);
//		}
//
//		@Override
//		public IExpr reciprocal(IExpr element) {
//			return element.reciprocal();
//		}
//
//		@Override
//		public IExpr getZero() {
//			return F.C0;
//		}
//
//		@Override
//		public IExpr getOne() {
//			return F.C1;
//		}
//
//		@Override
//		public boolean isZero(IExpr element) {
//			return element.isZero();
//		}
//
//		@Override
//		public boolean isOne(IExpr element) {
//			return element.isOne();
//		}
//
//		@Override
//		public boolean isUnit(IExpr element) {
//			return isOne(element);
//		}
//
//		@Override
//		public IExpr valueOf(long val) {
//			return F.integer(val);
//		}
//
//		@Override
//		public IExpr valueOfBigInteger(BigInteger val) {
//			return new BigIntegerSym(val.toByteArray());
//		}
//
//		@Override
//		public IExpr valueOf(IExpr val) {
//			return val;
//		}
//
//		@Override
//		public Iterator<IExpr> iterator() {
//			throw new UnsupportedOperationException();
//		}
//
//		@Override
//		public int compare(IExpr o1, IExpr o2) {
//			return o1.compareTo(o2);
//		}
//
//	}
//
//	final int numberOfVariables;
//	final XRing ring = CONST;
//	final ArrayList<String> variables;
//	final ArrayList<ISymbol> symbols;
//
//	public ExprRings(ArrayList<ISymbol> symbols) {
//		this.symbols = symbols;
//		this.variables = new ArrayList<String>(symbols.size());
//		for (int i = 0; i < symbols.size(); i++) {
//			variables.add(symbols.get(i).getSymbolName());
//		}
//		numberOfVariables = variables.size();
//	}
//
//	public IExpr toExpr(MultivariatePolynomial<IExpr> poly) {
//		IASTAppendable plus = F.PlusAlloc(poly.size());
//		for (Monomial<IExpr> monomial : poly) {
//			if (!monomial.coefficient.isZero()) {
//				int[] exponents = monomial.exponents;
//				IASTAppendable times = F.TimesAlloc(1 + exponents.length);
//				if (!monomial.coefficient.isOne()) {
//					times.append(monomial.coefficient);
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
//	public MultivariatePolynomial<IExpr> toPolynomial(IExpr expr) {
//		if (expr.isPlus()) {
//			IAST plus = (IAST) expr;
//			MultivariatePolynomial<IExpr> poly = MultivariatePolynomial.zero(numberOfVariables, ring,
//					MonomialOrder.DEFAULT);
//			for (int i = 1; i < plus.size(); i++) {
//				poly.add(toPolynomial(plus.get(i)));
//			}
//			return poly;
//		} else if (expr.isTimes()) {
//			IAST times = (IAST) expr;
//			MultivariatePolynomial<IExpr> poly = MultivariatePolynomial.one(numberOfVariables, ring,
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
//				IExpr coefficient = ring.getOne();
//				int[] exponents = new int[numberOfVariables];
//				exponents(symbol, exp, exponents);
//				@SuppressWarnings("unchecked")
//				MultivariatePolynomial<IExpr> poly = MultivariatePolynomial.create(numberOfVariables, ring,
//						MonomialOrder.DEFAULT, new Monomial<IExpr>(exponents, coefficient));
//				return poly;
//			}
//		} else if (expr.isSymbol()) {
//			ISymbol symbol = (ISymbol) expr;
//			IExpr coefficient = ring.getOne();
//			int[] exponents = new int[numberOfVariables];
//			exponents(symbol, 1, exponents);
//			@SuppressWarnings("unchecked")
//			MultivariatePolynomial<IExpr> poly = MultivariatePolynomial.create(numberOfVariables, ring,
//					MonomialOrder.DEFAULT, new Monomial<IExpr>(exponents, coefficient));
//			return poly;
//		} else if (expr.isInteger()) {
//			IInteger coefficient = (IInteger) expr;
//			@SuppressWarnings("unchecked")
//			MultivariatePolynomial<IExpr> poly = MultivariatePolynomial.create(numberOfVariables, ring,
//					MonomialOrder.DEFAULT, new Monomial<IExpr>(numberOfVariables, coefficient));
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
//		ExprRings formula = new ExprRings(vars);
//		MultivariatePolynomial<IExpr> poly = formula
//				.toPolynomial(F.Plus(F.Power(F.x, F.C2), F.Times(F.CN1, F.Power(F.y, F.C2))));
//		System.out.println(poly.toString());
//		// poly = MultivariatePolynomial.parse("x^2-1");
//		PolynomialFactorDecomposition<MultivariatePolynomial<IExpr>> result = PolynomialMethods.Factor(poly);
//		MultivariatePolynomial<IExpr> p = result.get(0);
//		IExpr first = formula.toExpr(p);
//		System.out.println(first.toString());
//		p = result.get(1);
//		IExpr second = formula.toExpr(p);
//		System.out.println(second.toString());
//		Assert.assertEquals(2, PolynomialMethods.Factor(poly).size());
//	}
}
