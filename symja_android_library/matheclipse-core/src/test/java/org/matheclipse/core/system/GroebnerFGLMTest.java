package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.polynomials.FGLMConversion;
import org.matheclipse.core.polynomials.GroebnerBasisJAS;
import edu.jas.arith.BigRational;
import edu.jas.gb.GroebnerBaseSeq;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.TermOrderByName;

/** The lexicographic basis of a zero-dimensional ideal from the graded one. */
public class GroebnerFGLMTest extends ExprEvaluatorTestCase {

  @Test
  public void testSameBasisAsBuchberger() {
    // the conversion and the algorithm of Buchberger give the same reduced basis
    EvalEngine engine = EvalEngine.get();
    String[] systems = {"{u^2+v-3, u+v^2-5}", "{u*v-2, u^2+v^2-7}", "{u^3-v, v^2-u-1}",
        "{2*u^2-3*v+1, 5*u*v-7}", "{u^2-2, v^2-3}", "{(u-1)^2, v-u}", "{u^2-1, u*v-1, v^2-1}"};
    for (String system : systems) {
      IAST polynomials = (IAST) engine.evaluate(F.Expand(engine.parse(system)));
      IAST variables = (IAST) engine.evaluate(engine.parse("{u, v}"));
      IAST converted = FGLMConversion.lexicographicBasis(polynomials, variables);
      assertEquals(true, converted.isPresent(), system);
      JASConvert<BigRational> jas = new JASConvert<BigRational>(variables, BigRational.ZERO,
          TermOrderByName.Lexicographic);
      List<GenPolynomial<BigRational>> input = new ArrayList<GenPolynomial<BigRational>>();
      for (IExpr polynomial : polynomials) {
        input.add(jas.expr2JAS(polynomial, false));
      }
      Set<GenPolynomial<BigRational>> expected = new HashSet<GenPolynomial<BigRational>>();
      for (GenPolynomial<BigRational> element : new GroebnerBaseSeq<BigRational>().GB(input)) {
        expected.add(element.monic());
      }
      Set<GenPolynomial<BigRational>> actual = new HashSet<GenPolynomial<BigRational>>();
      for (IExpr element : converted) {
        actual.add(jas.expr2JAS(element, false).monic());
      }
      assertEquals(expected, actual, system);
    }
  }

  @Test
  public void testResultOfGroebnerBasis() {
    check("GroebnerBasis({u^2-2, v^2-3}, {u,v})", //
        "{-3+v^2,-2+u^2}");
    check("GroebnerBasis({u^2+v^2-1, u-v}, {u,v})", //
        "{-1+2*v^2,u-v}");
    check("GroebnerBasis({u*v-1, u^2+v^2-4}, {u,v})", //
        "{1-4*v^2+v^4,u-4*v+v^3}");
  }

  @Test
  public void testNotZeroDimensional() {
    EvalEngine engine = EvalEngine.get();
    IAST variables = (IAST) engine.evaluate(engine.parse("{u, v}"));
    // a curve has infinitely many standard monomials
    assertEquals(true, FGLMConversion
        .lexicographicBasis((IAST) engine.parse("{u^2+v^2-1}"), variables).isNIL());
    // no solutions: the whole ring
    assertEquals(true, FGLMConversion
        .lexicographicBasis((IAST) engine.parse("{u^2-1, u^2-2}"), variables).isNIL());
    check("{GroebnerBasis({u^2+v^2-1}, {u,v}), GroebnerBasis({u^2-1, u^2-2}, {u,v})}", //
        "{{-1+u^2+v^2},{1}}");
  }

  @Test
  public void testCoefficientGrowth() {
    // large coefficients in the lexicographic basis, which the certificate has to carry
    check("g = GroebnerBasis({u^3+7*v^2-w-11, v^3-5*u*w+3, w^3+u*v-13*u+2}, {u,v,w}); "
        + "{Length(g), Exponent(First(g), w), "
        + "Union(Last /@ (PolynomialReduce(#, g, {u,v,w}) & /@ "
        + "{u^3+7*v^2-w-11, v^3-5*u*w+3, w^3+u*v-13*u+2}))}", //
        "{3,27,{0}}");
  }

  @Test
  public void testTimeConstrainedStopsBuchberger() {
    // a large graded basis over the rationals, which runs for much longer than this limit
    check("vs = {a1,a2,a3,a4,a5,a6,a7}; n = 7; "
        + "cyc = Append(Table(Total(Table(Times @@ Table(vs[[Mod(i+k, n, 1)]], {k, 0, d-1}), "
        + "{i, 1, n})), {d, 1, n-1}), (Times @@ vs)-1); "
        + "TimeConstrained(GroebnerBasis(cyc, vs, MonomialOrder->DegreeReverseLexicographic), 1)", //
        "$Aborted");
  }

  @Test
  public void testKeptTermOrders() {
    // the conversion is for the lexicographic order only
    assertEquals(true, TermOrderByName.DegreeReverseLexicographic
        .getEvord() != TermOrderByName.Lexicographic.getEvord());
    EvalEngine engine = EvalEngine.get();
    IAST variables = (IAST) engine.evaluate(engine.parse("{u, v}"));
    IAST graded = GroebnerBasisJAS.basis((IAST) engine.parse("{u^2+v, u*v-1}"), variables,
        TermOrderByName.DegreeReverseLexicographic);
    assertEquals(true, graded.isPresent());
  }
}
