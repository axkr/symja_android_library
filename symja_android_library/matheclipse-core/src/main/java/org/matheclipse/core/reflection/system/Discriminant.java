package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.C5;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.integer;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.ExprPolynomial;
import org.matheclipse.core.polynomials.ExprPolynomialRing;
import org.matheclipse.parser.client.SyntaxError;

/**
 * 
 * See <a href="http://en.wikipedia.org/wiki/Discriminant">Wikipedia -
 * Discriminant</a>
 */
public class Discriminant extends AbstractFunctionEvaluator {
	// b^2-4*a*c
 	private final static IExpr QUADRATIC = Plus(Power($s("b"), C2), Times(CN1, Times(Times(C4, $s("a")), $s("c"))));

	// b^2*c^2-4*a*c^3-4*b^3*d-27*a^2*d^2+18*a*b*c*d
 	private final static IExpr CUBIC = Plus(Plus(Plus(Plus(Times(Power($s("b"), C2), Power($s("c"), C2)), Times(CN1, Times(Times(C4,
 			$s("a")), Power($s("c"), C3)))), Times(CN1, Times(Times(C4, Power($s("b"), C3)), $s("d")))), Times(CN1, Times(Times(
 			integer(27L), Power($s("a"), C2)), Power($s("d"), C2)))), Times(Times(Times(Times(integer(18L), $s("a")), $s("b")), $s("c")),
 			$s("d")));

	// Page 405
	// http://books.google.com/books?id=-gGzjSnNnR0C&lpg=PA402&vq=quartic&hl=de&pg=PA405#v=snippet&q=quartic&f=false

	// 256*a0^3*a4^3-27*a0^2*a3^4-27*a1^4*a4^2+16*a0*a2^3*a4
	// -4*a0*a2^3*a3^2-4*a1^2*a2^3*a4-4*a1^3*a3^3+a1^2*a2^2*a3^2
	// -192*a0^2*a1*a3*a4^2-128a0^2*a2^2*a4^2
	// +144*a0^2*a2*a3^2*a4+144*a0*a1^2*a2*a4^2-6*a0*a1^2*a3^2*a4
	// -80*a0*a1*a2^2*a3*a4+18*a0*a1*a2*a3^3+18*a1^3*a2*a3*a4

	// 256*a^3*e^3-27*a^2*d^4-27*b^4*e^2+16*a*c^3*e
	// -4*a*c^3*d^2-4*b^2*c^3*e-4*b^3*d^3+b^2*c^2*d^2
	// -192*a^2*b*d*e^2-128a^2*c^2*e^2
	// +144*a^2*c*d^2*e+144*a*b^2*c*e^2-6*a*b^2*d^2*e
	// -80*a*b*c^2*d*e+18*a*b*c*d^3+18*b^3*c*d*e
	private final static IExpr QUARTIC = Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Times(Times(
			integer(256L), Power($s("a"), C3)), Power($s("e"), C3)), Times(CN1, Times(Times(integer(27L), Power($s("a"), C2)), Power(
			$s("d"), C4)))), Times(CN1, Times(Times(integer(27L), Power($s("b"), C4)), Power($s("e"), C2)))), Times(Times(Times(
			integer(16L), $s("a")), Power($s("c"), C3)), $s("e"))), Times(CN1, Times(Times(Times(C4, $s("a")), Power($s("c"), C3)),
			Power($s("d"), C2)))), Times(CN1, Times(Times(Times(C4, Power($s("b"), C2)), Power($s("c"), C3)), $s("e")))), Times(CN1,
			Times(Times(C4, Power($s("b"), C3)), Power($s("d"), C3)))), Times(Times(Power($s("b"), C2), Power($s("c"), C2)), Power(
			$s("d"), C2))), Times(CN1,
			Times(Times(Times(Times(integer(192L), Power($s("a"), C2)), $s("b")), $s("d")), Power($s("e"), C2)))), Times(CN1, Times(
			Times(Times(integer(128L), Power($s("a"), C2)), Power($s("c"), C2)), Power($s("e"), C2)))), Times(Times(Times(Times(
			integer(144L), Power($s("a"), C2)), $s("c")), Power($s("d"), C2)), $s("e"))), Times(Times(Times(
			Times(integer(144L), $s("a")), Power($s("b"), C2)), $s("c")), Power($s("e"), C2))), Times(CN1, Times(Times(Times(Times(
			integer(6L), $s("a")), Power($s("b"), C2)), Power($s("d"), C2)), $s("e")))), Times(CN1, Times(Times(Times(Times(Times(
			integer(80L), $s("a")), $s("b")), Power($s("c"), C2)), $s("d")), $s("e")))), Times(Times(Times(Times(integer(18L), $s("a")),
			$s("b")), $s("c")), Power($s("d"), C3))), Times(Times(Times(Times(integer(18L), Power($s("b"), C3)), $s("c")), $s("d")),
			$s("e")));

	// 3125*a0^4*a5^4-2500*a0^3*a1*a4*a5^3-3750*a0^3*a2*a3*a5^3+2000*a0^3*a2*a4^2*a5^2+2250*a0^3*a3^2*a4*a5^2
	// -1600*a0^3*a3*a4^3*a5+256*a0^3*a4^5+2000*a0^2*a1^2*a3*a5^3-50*a0^2*a1^2*a4^2*a5^2+2250*a0^2*a1*a2^2*a5^3
	// -2050*a0^2*a1*a2*a3*a4*a5^2+160*a0^2*a1*a2*a4^3*a5-900*a0^2*a1*a3^2*a5^2+1020*a0^2*a1*a3^2*a4^2*a5
	// -192*a0^2*a1*a3*a4^4-900*a0^2*a2^3*a4*a5^2+825*a0^2*a2^2*a3^2*a5^2+560*a0^2*a2^2*a3*a4^2*a5-128*a0^2*a2^2*a4^4
	// -630*a0^2*a2*a3^3*a4*a5+144*a0^2*a2*a3^2*a4^3+108*a0^2*a3^5*a5-27*a0^2*a3^4*a4^2-1600*a0*a1^3*a2*a5^3
	// +160*a0*a1^3*a3*a4*a5^2-36*a0*a1^3*a4^3*a5+1020*a0*a1^2*a2^2*a4*a5^2+560*a0*a1^2*a2*a3^2*a5^2
	// -746*a0*a1^2*a2*a3*a4^2*a5+144*a0*a1^2*a2*a4^4+24*a0*a1^2*a3^3*a4*a5-6*a0*a1^2*a3^2*a4^3
	// +356*a0*a1*a2^2*a3^2*a4*a5-80*a0*a1*a2^2*a3*a4^3-630*a0*a1*a2^3*a3*a5^2+24*a0*a1*a2^3*a3^2*a5
	// -72*a0*a1*a2*a3^4*a5+18*a0*a1*a2*a3^3*a4^2+108*a0*a2^5*a5^2-72*a0*a2^4*a3*a4*a5+16*a0*a2^4*a4^3
	// +16*a0*a2^3*a3^3*a5-4*a0*a2^3*a3^2*a4^2+256*a1^5*a5^3-192*a1^4*a2*a4*a5^2-128*a1^4*a3^2*a5^2
	// +144*a1^4*a3*a4^2*a5-27*a1^4*a4^4+144*a1^3*a2^2*a3*a5^2-6*a1^3*a2^2*a4^2*a5-80*a1^3*a2*a3^2*a4*a5
	// +18*a1^3*a2*a3*a4^3+16*a1^3*a3^4*a5-4*a1^3*a3^3*a4^2-27*a1^2*a2^4*a5^2+18*a1^2*a2^3*a3*a4*a5
	// -4*a1^2*a2^3*a4^3-4*a1^2*a2^2*a3^3*a5+a1^2*a2^2*a3^2*a4^2

	// 3125*a^4*f^4-2500*a^3*b*e*f^3-3750*a^3*c*d*f^3+2000*a^3*c*e^2*f^2+2250*a^3*d^2*e*f^2
	// -1600*a^3*d*e^3*f+256*a^3*e^5+2000*a^2*b^2*d*f^3-50*a^2*b^2*e^2*f^2+2250*a^2*b*c^2*f^3
	// -2050*a^2*b*c*d*e*f^2+160*a^2*b*c*e^3*f-900*a^2*b*d^2*f^2+1020*a^2*b*d^2*e^2*f
	// -192*a^2*b*d*e^4-900*a^2*c^3*e*f^2+825*a^2*c^2*d^2*f^2+560*a^2*c^2*d*e^2*f-128*a^2*c^2*e^4
	// -630*a^2*c*d^3*e*f+144*a^2*c*d^2*e^3+108*a^2*d^5*f-27*a^2*d^4*e^2-1600*a*b^3*c*f^3
	// +160*a*b^3*d*e*f^2-36*a*b^3*e^3*f+1020*a*b^2*c^2*e*f^2+560*a*b^2*c*d^2*f^2
	// -746*a*b^2*c*d*e^2*f+144*a*b^2*c*e^4+24*a*b^2*d^3*e*f-6*a*b^2*d^2*e^3
	// +356*a*b*c^2*d^2*e*f-80*a*b*c^2*d*e^3-630*a*b*c^3*d*f^2+24*a*b*c^3*d^2*f
	// -72*a*b*c*d^4*f+18*a*b*c*d^3*e^2+108*a*c^5*f^2-72*a*c^4*d*e*f+16*a*c^4*e^3
	// +16*a*c^3*d^3*f-4*a*c^3*d^2*e^2+256*b^5*f^3-192*b^4*c*e*f^2-128*b^4*d^2*f^2
	// +144*b^4*d*e^2*f-27*b^4*e^4+144*b^3*c^2*d*f^2-6*b^3*c^2*e^2*f-80*b^3*c*d^2*e*f
	// +18*b^3*c*d*e^3+16*b^3*d^4*f-4*b^3*d^3*e^2-27*b^2*c^4*f^2+18*b^2*c^3*d*e*f
	// -4*b^2*c^3*e^3-4*b^2*c^2*d^3*f+b^2*c^2*d^2*e^2
	private final static IExpr QUINTIC = Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(
			Plus(
					Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(
							Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Plus(Times(Times(integer(3125L),
									Power($s("a"), C4)), Power($s("f"), C4)), Times(CN1, Times(Times(Times(Times(integer(2500L), Power($s("a"), C3)),
									$s("b")), $s("e")), Power($s("f"), C3)))), Times(CN1, Times(Times(Times(
									Times(integer(3750L), Power($s("a"), C3)), $s("c")), $s("d")), Power($s("f"), C3)))), Times(Times(Times(Times(
									integer(2000L), Power($s("a"), C3)), $s("c")), Power($s("e"), C2)), Power($s("f"), C2))), Times(Times(Times(
									Times(integer(2250L), Power($s("a"), C3)), Power($s("d"), C2)), $s("e")), Power($s("f"), C2))), Times(CN1, Times(
									Times(Times(Times(integer(1600L), Power($s("a"), C3)), $s("d")), Power($s("e"), C3)), $s("f")))), Times(Times(
									integer(256L), Power($s("a"), C3)), Power($s("e"), C5))), Times(Times(Times(Times(integer(2000L), Power($s("a"),
									C2)), Power($s("b"), C2)), $s("d")), Power($s("f"), C3))), Times(CN1, Times(Times(Times(Times(integer(50L),
									Power($s("a"), C2)), Power($s("b"), C2)), Power($s("e"), C2)), Power($s("f"), C2)))), Times(Times(Times(Times(
									integer(2250L), Power($s("a"), C2)), $s("b")), Power($s("c"), C2)), Power($s("f"), C3))), Times(CN1, Times(Times(
									Times(Times(Times(Times(integer(2050L), Power($s("a"), C2)), $s("b")), $s("c")), $s("d")), $s("e")), Power(
									$s("f"), C2)))), Times(Times(Times(Times(Times(integer(160L), Power($s("a"), C2)), $s("b")), $s("c")), Power(
									$s("e"), C3)), $s("f"))), Times(CN1, Times(Times(Times(Times(integer(900L), Power($s("a"), C2)), $s("b")), Power(
									$s("d"), C2)), Power($s("f"), C2)))), Times(Times(Times(
									Times(Times(integer(1020L), Power($s("a"), C2)), $s("b")), Power($s("d"), C2)), Power($s("e"), C2)), $s("f"))),
									Times(CN1, Times(Times(Times(Times(integer(192L), Power($s("a"), C2)), $s("b")), $s("d")), Power($s("e"), C4)))),
									Times(CN1, Times(Times(Times(Times(integer(900L), Power($s("a"), C2)), Power($s("c"), C3)), $s("e")), Power(
											$s("f"), C2)))), Times(Times(Times(Times(integer(825L), Power($s("a"), C2)), Power($s("c"), C2)), Power(
									$s("d"), C2)), Power($s("f"), C2))), Times(Times(Times(Times(Times(integer(560L), Power($s("a"), C2)), Power(
									$s("c"), C2)), $s("d")), Power($s("e"), C2)), $s("f"))), Times(CN1, Times(Times(Times(integer(128L), Power(
									$s("a"), C2)), Power($s("c"), C2)), Power($s("e"), C4)))), Times(CN1, Times(Times(Times(Times(Times(
							integer(630L), Power($s("a"), C2)), $s("c")), Power($s("d"), C3)), $s("e")), $s("f")))), Times(Times(Times(Times(
							integer(144L), Power($s("a"), C2)), $s("c")), Power($s("d"), C2)), Power($s("e"), C3))), Times(Times(Times(
							integer(108L), Power($s("a"), C2)), Power($s("d"), C5)), $s("f"))), Times(CN1, Times(Times(Times(integer(27L), Power(
							$s("a"), C2)), Power($s("d"), C4)), Power($s("e"), C2)))), Times(CN1, Times(Times(Times(
							Times(integer(1600L), $s("a")), Power($s("b"), C3)), $s("c")), Power($s("f"), C3)))), Times(Times(Times(Times(Times(
							integer(160L), $s("a")), Power($s("b"), C3)), $s("d")), $s("e")), Power($s("f"), C2))), Times(CN1, Times(Times(Times(
							Times(integer(36L), $s("a")), Power($s("b"), C3)), Power($s("e"), C3)), $s("f")))), Times(Times(Times(Times(Times(
							integer(1020L), $s("a")), Power($s("b"), C2)), Power($s("c"), C2)), $s("e")), Power($s("f"), C2))), Times(Times(
							Times(Times(Times(integer(560L), $s("a")), Power($s("b"), C2)), $s("c")), Power($s("d"), C2)), Power($s("f"), C2))),
							Times(CN1, Times(Times(Times(Times(Times(Times(integer(746L), $s("a")), Power($s("b"), C2)), $s("c")), $s("d")),
									Power($s("e"), C2)), $s("f")))), Times(Times(Times(Times(integer(144L), $s("a")), Power($s("b"), C2)), $s("c")),
							Power($s("e"), C4))), Times(Times(Times(Times(Times(integer(24L), $s("a")), Power($s("b"), C2)), Power($s("d"), C3)),
							$s("e")), $s("f"))), Times(CN1, Times(Times(Times(Times(integer(6L), $s("a")), Power($s("b"), C2)),
							Power($s("d"), C2)), Power($s("e"), C3)))), Times(Times(Times(Times(Times(Times(integer(356L), $s("a")), $s("b")),
							Power($s("c"), C2)), Power($s("d"), C2)), $s("e")), $s("f"))), Times(CN1, Times(Times(Times(Times(Times(integer(80L),
							$s("a")), $s("b")), Power($s("c"), C2)), $s("d")), Power($s("e"), C3)))), Times(CN1, Times(Times(Times(Times(Times(
							integer(630L), $s("a")), $s("b")), Power($s("c"), C3)), $s("d")), Power($s("f"), C2)))), Times(Times(Times(Times(
							Times(integer(24L), $s("a")), $s("b")), Power($s("c"), C3)), Power($s("d"), C2)), $s("f"))), Times(CN1, Times(Times(
							Times(Times(Times(integer(72L), $s("a")), $s("b")), $s("c")), Power($s("d"), C4)), $s("f")))), Times(Times(Times(
							Times(Times(integer(18L), $s("a")), $s("b")), $s("c")), Power($s("d"), C3)), Power($s("e"), C2))), Times(Times(Times(
							integer(108L), $s("a")), Power($s("c"), C5)), Power($s("f"), C2))), Times(CN1, Times(Times(Times(Times(Times(
							integer(72L), $s("a")), Power($s("c"), C4)), $s("d")), $s("e")), $s("f")))), Times(Times(
							Times(integer(16L), $s("a")), Power($s("c"), C4)), Power($s("e"), C3))), Times(Times(Times(Times(integer(16L),
							$s("a")), Power($s("c"), C3)), Power($s("d"), C3)), $s("f"))), Times(CN1, Times(Times(Times(Times(C4, $s("a")),
							Power($s("c"), C3)), Power($s("d"), C2)), Power($s("e"), C2)))), Times(Times(integer(256L), Power($s("b"), C5)),
					Power($s("f"), C3))), Times(CN1, Times(Times(Times(Times(integer(192L), Power($s("b"), C4)), $s("c")), $s("e")), Power(
			$s("f"), C2)))), Times(CN1, Times(Times(Times(integer(128L), Power($s("b"), C4)), Power($s("d"), C2)), Power($s("f"), C2)))),
			Times(Times(Times(Times(integer(144L), Power($s("b"), C4)), $s("d")), Power($s("e"), C2)), $s("f"))), Times(CN1, Times(Times(
			integer(27L), Power($s("b"), C4)), Power($s("e"), C4)))), Times(Times(Times(Times(integer(144L), Power($s("b"), C3)), Power(
			$s("c"), C2)), $s("d")), Power($s("f"), C2))), Times(CN1, Times(Times(Times(Times(integer(6L), Power($s("b"), C3)), Power(
			$s("c"), C2)), Power($s("e"), C2)), $s("f")))), Times(CN1, Times(Times(Times(Times(Times(integer(80L), Power($s("b"), C3)),
			$s("c")), Power($s("d"), C2)), $s("e")), $s("f")))), Times(Times(Times(Times(integer(18L), Power($s("b"), C3)), $s("c")),
			$s("d")), Power($s("e"), C3))), Times(Times(Times(integer(16L), Power($s("b"), C3)), Power($s("d"), C4)), $s("f"))), Times(
			CN1, Times(Times(Times(C4, Power($s("b"), C3)), Power($s("d"), C3)), Power($s("e"), C2)))), Times(CN1, Times(Times(Times(
			integer(27L), Power($s("b"), C2)), Power($s("c"), C4)), Power($s("f"), C2)))), Times(Times(Times(Times(Times(integer(18L),
			Power($s("b"), C2)), Power($s("c"), C3)), $s("d")), $s("e")), $s("f"))), Times(CN1, Times(Times(
			Times(C4, Power($s("b"), C2)), Power($s("c"), C3)), Power($s("e"), C3)))), Times(CN1, Times(Times(Times(Times(C4, Power(
			$s("b"), C2)), Power($s("c"), C2)), Power($s("d"), C3)), $s("f")))), Times(Times(
			Times(Power($s("b"), C2), Power($s("c"), C2)), Power($s("d"), C2)), Power($s("e"), C2)));

	private ISymbol[] vars = { $s("a"), $s("b"), $s("c"), $s("d"), $s("e"), $s("f") };

	public Discriminant() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);
		IExpr arg2 = ast.arg2();
		if (!arg2.isSymbol()) {
			return F.NIL;
		} 
		IExpr expr = F.evalExpandAll(ast.arg1());
		try {
			ExprPolynomialRing ring = new ExprPolynomialRing(F.List(arg2));
		    ExprPolynomial poly = ring.create(expr);
		
		    long n = poly.degree();
		    if (n >= 2L && n <= 5L) {
		      IAST result = poly.coefficientList();
			  IAST rules = F.List();
			  for (int i = 1; i < result.size(); i++) {
				  rules.add(F.Rule(vars[i - 1], result.get(i)));
			  }
			  switch ((int) n) {
			  case 2:
				return QUADRATIC.replaceAll(rules);
			  case 3:
				return CUBIC.replaceAll(rules);
			  case 4:
				return QUARTIC.replaceAll(rules);
			  case 5:
				return QUINTIC.replaceAll(rules);
			  }
		    }
		    IExpr fN = poly.leadingBaseCoefficient();//coefficient(n);
	        ExprPolynomial polyDiff = poly.derivative();
		    // see: http://en.wikipedia.org/wiki/Discriminant#Discriminant_of_a_polynomial
	        return F.Divide(F.Times(F.Power(F.CN1, (n*(n-1)/2)),F.Resultant(poly.getExpr(), polyDiff.getExpr(), arg2)), fN);
		}catch(Exception ex){
			throw new WrongArgumentType(ast, expr, 1, "Polynomial expected!");
		}
		}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE);
		super.setUp(symbol);
	}
}