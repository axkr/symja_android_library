package org.matheclipse.core.polynomials;

import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

import edu.jas.arith.BigRational;
import edu.jas.poly.GenPolynomial;

public class PartialFractionGenerator implements IPartialFractionGenerator {
	IAST result;
	JASConvert<BigRational> jas;

	public PartialFractionGenerator() {
		this.result = F.Plus();
	}

	public void setJAS(JASConvert<BigRational> jas) {
		this.jas = jas;
	}

	@Override
	public IAST getResult() {
		return result;
	}

	@Override
	public void addNonFractionalPart(GenPolynomial<BigRational> genPolynomial) {
		IExpr temp = F.eval(jas.rationalPoly2Expr(genPolynomial));
		if (temp.isAST()) {
			((IAST) temp).addEvalFlags(IAST.IS_DECOMPOSED_PARTIAL_FRACTION);
		}
		result.add(temp);
	}

	@Override
	public void addSinglePartialFraction(GenPolynomial<BigRational> genPolynomial, GenPolynomial<BigRational> Di_1, int j) {
		IExpr temp;
		Object[] objects = jas.factorTerms(genPolynomial);
		java.math.BigInteger gcd = (java.math.BigInteger) objects[0];
		java.math.BigInteger lcm = (java.math.BigInteger) objects[1];
		GenPolynomial<edu.jas.arith.BigInteger> poly = (GenPolynomial<edu.jas.arith.BigInteger>) objects[2];
		if (j == 1) {
			temp = F.eval(F.Times(F.integer(gcd), jas.integerPoly2Expr(poly),
					F.Power(jas.rationalPoly2Expr(Di_1.multiply(BigRational.valueOf(lcm))), F.CN1)));
		} else {
			temp = F.eval(F.Times(F.integer(gcd), jas.integerPoly2Expr(poly), F.Power(F.integer(lcm), F.integer(-1L)),
					F.Power(jas.rationalPoly2Expr(Di_1), F.integer(j * (-1L)))));
		}
		if (!temp.isZero()) {
			if (temp.isAST()) {
				((IAST) temp).addEvalFlags(IAST.IS_DECOMPOSED_PARTIAL_FRACTION);
			}
			result.add(temp);
		}
	}
}