package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 * 
 */
class IntRules102 {
  public static IAST RULES =
      List(
          IIntegrate(2041,
              Integrate(
                  Times(Power(Times(e_, x_), m_DEFAULT),
                      Power(Plus(c_, Times(d_DEFAULT, Power(x_, n_DEFAULT))), q_DEFAULT),
                      Power(
                          Plus(Times(b_DEFAULT, Power(x_, k_DEFAULT)),
                              Times(a_DEFAULT, Power(x_, j_))),
                          p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(e, IntPart(m)), Power(Times(e,
                              x), FracPart(m)),
                          Power(Power(x, FracPart(m)), CN1)),
                      Integrate(
                          Times(Power(x, m),
                              Power(Plus(Times(a, Power(x, j)),
                                  Times(b, Power(x, k))), p),
                              Power(Plus(c, Times(d, Power(x, n))), q)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, j, k, m, n, p, q), x), Not(IntegerQ(p)), NeQ(k, j),
                      IntegerQ(Simplify(Times(j, Power(n, CN1)))),
                      IntegerQ(Simplify(Times(k,
                          Power(n, CN1)))),
                      NeQ(m, CN1), IntegerQ(Simplify(
                          Times(n, Power(Plus(m, C1), CN1)))),
                      Not(IntegerQ(n))))),
          IIntegrate(2042,
              Integrate(
                  Times(
                      Power(Times(e_DEFAULT,
                          x_), m_DEFAULT),
                      Power(
                          Plus(Times(a_DEFAULT, Power(x_, j_DEFAULT)),
                              Times(b_DEFAULT, Power(x_, $p("jn", true)))),
                          p_),
                      Power(Plus(c_, Times(d_DEFAULT, Power(x_, n_DEFAULT))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(e, IntPart(m)), Power(Times(e, x), FracPart(m)),
                          Power(Plus(Times(a, Power(x, j)), Times(b, Power(x, Plus(j, n)))),
                              FracPart(p)),
                          Power(Times(Power(x, Plus(FracPart(m), Times(j, FracPart(p)))),
                              Power(Plus(a, Times(b, Power(x, n))), FracPart(p))), CN1)),
                      Integrate(
                          Times(Power(x, Plus(m, Times(j, p))),
                              Power(Plus(a, Times(b, Power(x, n))), p),
                              Power(Plus(c, Times(d, Power(x, n))), q)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, j, m, n, p, q), x), EqQ($s("jn"), Plus(j, n)),
                      Not(IntegerQ(p)), NeQ(Subtract(Times(b, c),
                          Times(a, d)), C0),
                      Not(And(EqQ(n, C1), EqQ(j, C1)))))),
          IIntegrate(2043,
              Integrate(
                  Times($p("§pq"),
                      Power(
                          Plus(
                              Times(a_DEFAULT, Power(x_,
                                  j_DEFAULT)),
                              Times(b_DEFAULT, Power(x_, n_))),
                          p_)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(d,
                          Denominator(n))),
                      Dist(d,
                          Subst(
                              Integrate(
                                  Times(Power(x, Subtract(d, C1)),
                                      ReplaceAll(SubstFor(Power(x, n), $s("§pq"), x),
                                          Rule(x, Power(x, Times(d, n)))),
                                      Power(Plus(Times(a, Power(x, Times(d, j))),
                                          Times(b, Power(x, Times(d, n)))), p)),
                                  x),
                              x, Power(x, Power(d, CN1))),
                          x)),
                  And(FreeQ(List(a, b, j, n, p), x), PolyQ($s("§pq"), Power(x, n)),
                      Not(IntegerQ(p)), NeQ(n, j), RationalQ(j,
                          n),
                      IntegerQ(Times(j, Power(n, CN1))), LtQ(CN1, n, C1)))),
          IIntegrate(2044,
              Integrate(
                  Times($p("§pq"), Power(x_, m_DEFAULT),
                      Power(
                          Plus(
                              Times(a_DEFAULT, Power(x_, j_DEFAULT)), Times(b_DEFAULT,
                                  Power(x_, n_))),
                          p_)),
                  x_Symbol),
              Condition(
                  Dist(Power(n, CN1),
                      Subst(
                          Integrate(
                              Times(
                                  Power(x,
                                      Subtract(Simplify(Times(Plus(m, C1), Power(n, CN1))), C1)),
                                  SubstFor(Power(x, n), $s("§pq"), x),
                                  Power(
                                      Plus(Times(a, Power(x, Simplify(Times(j, Power(n, CN1))))),
                                          Times(b, x)),
                                      p)),
                              x),
                          x, Power(x, n)),
                      x),
                  And(FreeQ(List(a, b, j, m, n, p), x), PolyQ($s("§pq"), Power(x, n)),
                      Not(IntegerQ(p)), NeQ(n, j), IntegerQ(Simplify(
                          Times(j, Power(n, CN1)))),
                      IntegerQ(Simplify(Times(Plus(m, C1), Power(n, CN1))))))),
          IIntegrate(2045,
              Integrate(
                  Times($p("§pq"), Power(Times(c_, x_), m_DEFAULT),
                      Power(Plus(Times(a_DEFAULT, Power(x_, j_DEFAULT)),
                          Times(b_DEFAULT, Power(x_, n_))), p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(c, Times(Sign(m), Quotient(m, Sign(m)))),
                          Power(Times(c, x), Mod(m,
                              Sign(m))),
                          Power(Power(x, Mod(m, Sign(m))), CN1)),
                      Integrate(
                          Times(
                              Power(x, m), $s("§pq"), Power(Plus(Times(a, Power(x, j)),
                                  Times(b, Power(x, n))), p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, j, n, p), x), PolyQ($s("§pq"), Power(x, n)),
                      Not(IntegerQ(p)), NeQ(n, j), IntegerQ(Simplify(Times(j, Power(n, CN1)))),
                      IntegerQ(Simplify(
                          Times(Plus(m, C1), Power(n, CN1)))),
                      RationalQ(m), GtQ(Sqr(m), C1)))),
          IIntegrate(2046,
              Integrate(
                  Times(
                      $p("§pq"), Power(Times(c_,
                          x_), m_DEFAULT),
                      Power(
                          Plus(Times(a_DEFAULT, Power(x_, j_DEFAULT)),
                              Times(b_DEFAULT, Power(x_, n_))),
                          p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Times(c,
                          x), m), Power(Power(x, m),
                              CN1)),
                      Integrate(
                          Times(
                              Power(x, m), $s("§pq"), Power(Plus(Times(a, Power(x,
                                  j)), Times(b,
                                      Power(x, n))),
                                  p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, j, m, n, p), x), PolyQ($s("§pq"), Power(x, n)),
                      Not(IntegerQ(p)), NeQ(n, j), IntegerQ(Simplify(
                          Times(j, Power(n, CN1)))),
                      IntegerQ(Simplify(Times(Plus(m, C1), Power(n, CN1))))))),
          IIntegrate(2047,
              Integrate(
                  Times($p("§pq"), Power(x_, m_DEFAULT),
                      Power(
                          Plus(
                              Times(a_DEFAULT, Power(x_,
                                  j_DEFAULT)),
                              Times(b_DEFAULT, Power(x_, n_))),
                          p_)),
                  x_Symbol),
              Condition(
                  With(List(Set(g, GCD(Plus(m, C1), n))),
                      Condition(Dist(Power(g, CN1),
                          Subst(
                              Integrate(
                                  Times(Power(x, Subtract(Times(Plus(m, C1), Power(g, CN1)), C1)),
                                      ReplaceAll($s("§pq"), Rule(x, Power(x, Power(g, CN1)))),
                                      Power(Plus(Times(a, Power(x, Times(j, Power(g, CN1)))),
                                          Times(b, Power(x, Times(n, Power(g, CN1))))), p)),
                                  x),
                              x, Power(x, g)),
                          x), NeQ(g, C1))),
                  And(FreeQ(List(a, b, p), x), PolyQ($s("§pq"),
                      Power(x, n)), Not(IntegerQ(p)), IGtQ(j, C0), IGtQ(n, C0),
                      IGtQ(Times(j, Power(n, CN1)), C0), IntegerQ(m)))),
          IIntegrate(2048,
              Integrate(
                  Times(
                      $p("§pq"), Power(Times(c_DEFAULT,
                          x_), m_DEFAULT),
                      Power(
                          Plus(
                              Times(a_DEFAULT, Power(x_,
                                  j_DEFAULT)),
                              Times(b_DEFAULT, Power(x_, n_))),
                          p_)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(q, Expon($s("§pq"), x))), Condition(
                          With(List(Set($s("§pqq"), Coeff($s("§pq"), x, q))), Plus(
                              Integrate(Times(
                                  Power(Times(c, x), m),
                                  ExpandToSum(
                                      Subtract(Subtract($s("§pq"), Times($s("§pqq"), Power(x, q))),
                                          Times(a, $s("§pqq"), Plus(m, q, Negate(n), C1),
                                              Power(x, Subtract(q, n)),
                                              Power(Times(b, Plus(m, q, Times(n, p), C1)), CN1))),
                                      x),
                                  Power(Plus(Times(a, Power(x, j)), Times(b, Power(x, n))), p)), x),
                              Simp(Times($s("§pqq"), Power(Times(c, x), Plus(m, q, Negate(n), C1)),
                                  Power(Plus(Times(a, Power(x, j)), Times(b, Power(x, n))),
                                      Plus(p, C1)),
                                  Power(Times(b, Power(c, Plus(q, Negate(n), C1)),
                                      Plus(m, q, Times(n, p), C1)), CN1)),
                                  x))),
                          And(GtQ(q, Subtract(n, C1)), NeQ(Plus(m, q, Times(n, p), C1), C0),
                              Or(IntegerQ(Times(C2, p)),
                                  IntegerQ(Plus(p,
                                      Times(Plus(q, C1), Power(Times(C2, n), CN1)))))))),
                  And(FreeQ(List(a, b, c, m,
                      p), x), PolyQ($s("§pq"),
                          x),
                      Not(IntegerQ(p)), IGtQ(j, C0), IGtQ(n, C0), LtQ(j, n)))),
          IIntegrate(2049,
              Integrate(
                  Times($p("§pq"), Power(x_, m_DEFAULT),
                      Power(
                          Plus(
                              Times(a_DEFAULT, Power(x_,
                                  j_DEFAULT)),
                              Times(b_DEFAULT, Power(x_, n_))),
                          p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Plus(m,
                          C1), CN1),
                      Subst(
                          Integrate(
                              Times(
                                  ReplaceAll(
                                      SubstFor(Power(x,
                                          n), $s("§pq"), x),
                                      Rule(x,
                                          Power(x, Simplify(Times(n, Power(Plus(m, C1), CN1)))))),
                                  Power(
                                      Plus(
                                          Times(a,
                                              Power(x,
                                                  Simplify(Times(j, Power(Plus(m, C1), CN1))))),
                                          Times(b,
                                              Power(x,
                                                  Simplify(Times(n, Power(Plus(m, C1), CN1)))))),
                                      p)),
                              x),
                          x, Power(x, Plus(m, C1))),
                      x),
                  And(FreeQ(List(a, b, j, m, n, p), x), PolyQ($s("§pq"), Power(x, n)),
                      Not(IntegerQ(p)), NeQ(n, j), IntegerQ(Simplify(Times(j, Power(n,
                          CN1)))),
                      IntegerQ(Simplify(Times(n, Power(Plus(m, C1), CN1)))), Not(IntegerQ(n))))),
          IIntegrate(2050,
              Integrate(
                  Times($p("§pq"), Power(Times(c_, x_), m_),
                      Power(
                          Plus(Times(a_DEFAULT, Power(x_, j_DEFAULT)),
                              Times(b_DEFAULT, Power(x_, n_))),
                          p_)),
                  x_Symbol),
              Condition(
                  Dist(Times(Power(c, Times(Sign(m), Quotient(m, Sign(m)))),
                      Power(Times(c, x), Mod(m, Sign(m))), Power(Power(x, Mod(m, Sign(m))), CN1)),
                      Integrate(
                          Times(Power(x, m), $s("§pq"),
                              Power(Plus(Times(a, Power(x, j)), Times(b, Power(x, n))), p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, j, n, p), x), PolyQ($s("§pq"), Power(x, n)),
                      Not(IntegerQ(p)), NeQ(n, j), IntegerQ(Simplify(Times(j, Power(n, CN1)))),
                      IntegerQ(Simplify(Times(n, Power(Plus(m, C1), CN1)))), Not(IntegerQ(n)), GtQ(
                          Sqr(m), C1)))),
          IIntegrate(2051,
              Integrate(
                  Times($p("§pq"), Power(Times(c_, x_), m_),
                      Power(
                          Plus(Times(a_DEFAULT, Power(x_, j_DEFAULT)),
                              Times(b_DEFAULT, Power(x_, n_))),
                          p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Times(c,
                          x), m), Power(Power(x, m),
                              CN1)),
                      Integrate(Times(Power(x, m), $s("§pq"),
                          Power(Plus(Times(a, Power(x, j)), Times(b, Power(x, n))), p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, j, m, n, p), x), PolyQ($s("§pq"),
                      Power(x, n)), Not(IntegerQ(p)), NeQ(n, j),
                      IntegerQ(Simplify(Times(j, Power(n, CN1)))), IntegerQ(Simplify(
                          Times(n, Power(Plus(m, C1), CN1)))),
                      Not(IntegerQ(n))))),
          IIntegrate(2052,
              Integrate(
                  Times($p("§pq"), Power(Times(c_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(Times(a_DEFAULT, Power(x_, j_DEFAULT)),
                          Times(b_DEFAULT, Power(x_, n_))), p_)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Times(Power(Times(c, x), m), $s("§pq"), Power(
                              Plus(Times(a, Power(x, j)), Times(b, Power(x, n))), p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, j, m, n, p), x),
                      Or(PolyQ($s("§pq"), x), PolyQ($s("§pq"), Power(x, n))), Not(
                          IntegerQ(p)),
                      NeQ(n, j)))),
          IIntegrate(2053, Integrate(Times($p("§pq"),
              Power(Plus(Times(a_DEFAULT, Power(x_, j_DEFAULT)), Times(b_DEFAULT, Power(x_, n_))),
                  p_)),
              x_Symbol),
              Condition(
                  Integrate(ExpandIntegrand(Times($s("§pq"),
                      Power(Plus(Times(a, Power(x, j)), Times(b, Power(x, n))), p)), x), x),
                  And(FreeQ(List(a, b, j, n, p), x),
                      Or(PolyQ($s("§pq"), x), PolyQ($s("§pq"), Power(x, n))), Not(IntegerQ(p)),
                      NeQ(n, j)))),
          IIntegrate(2054, Integrate(Times(u_DEFAULT, Power(P_, p_), Power(Q_, q_)), x_Symbol),
              Condition(
                  Module(List(Set($s("§gcd"), PolyGCD(PSymbol, QSymbol, x))),
                      Condition(
                          Integrate(Times(u, Power($s("§gcd"), Plus(p, q)),
                              Power(PolynomialQuotient(PSymbol, $s("§gcd"), x), p),
                              Power(PolynomialQuotient(QSymbol, $s("§gcd"), x), q)), x),
                          NeQ($s("§gcd"), C1))),
                  And(IGtQ(p, C0), ILtQ(q, C0), PolyQ(PSymbol, x), PolyQ(QSymbol, x)))),
          IIntegrate(
              2055, Integrate(Times(u_DEFAULT, P_,
                  Power(Q_, q_)), x_Symbol),
              Condition(
                  Module(
                      List(Set($s("§gcd"),
                          PolyGCD(PSymbol, QSymbol, x))),
                      Condition(
                          Integrate(
                              Times(u, Power($s("§gcd"), Plus(q, C1)),
                                  PolynomialQuotient(PSymbol, $s(
                                      "§gcd"), x),
                                  Power(PolynomialQuotient(QSymbol, $s("§gcd"), x), q)),
                              x),
                          NeQ($s("§gcd"), C1))),
                  And(ILtQ(q, C0), PolyQ(PSymbol, x), PolyQ(QSymbol, x)))),
          IIntegrate(
              2056, Integrate(Times(u_DEFAULT,
                  Power(P_, p_DEFAULT)), x_Symbol),
              Condition(
                  With(
                      List(Set(m,
                          MinimumMonomialExponent(PSymbol, x))),
                      Dist(
                          Times(Power(PSymbol, FracPart(p)),
                              Power(
                                  Times(Power(x, Times(m, FracPart(p))),
                                      Power(Distrib(Power(Power(x, m), CN1), PSymbol),
                                          FracPart(p))),
                                  CN1)),
                          Integrate(
                              Times(
                                  u, Power(x, Times(m,
                                      p)),
                                  Power(Distrib(Power(Power(x, m), CN1), PSymbol), p)),
                              x),
                          x)),
                  And(FreeQ(p, x), Not(IntegerQ(p)), SumQ(PSymbol), EveryQ(
                      Function(BinomialQ(Slot1, x)), PSymbol),
                      Not(PolyQ(PSymbol, x, C2))))),
          IIntegrate(
              2057, Integrate(Power(P_,
                  p_), x_Symbol),
              Condition(
                  With(List(Set(u, Factor(ReplaceAll(PSymbol, Rule(x, Sqrt(x)))))),
                      Condition(
                          Integrate(ExpandIntegrand(Power(ReplaceAll(u, Rule(x, Sqr(x))), p), x),
                              x),
                          Not(SumQ(NonfreeFactors(u, x))))),
                  And(PolyQ(PSymbol, Sqr(x)), ILtQ(p, C0)))),
          IIntegrate(2058, Integrate(Power(P_, p_), x_Symbol),
              Condition(
                  With(List(Set(u, Factor(PSymbol))),
                      Condition(Integrate(ExpandIntegrand(Power(u, p), x), x),
                          Not(SumQ(NonfreeFactors(u, x))))),
                  And(PolyQ(PSymbol, x), ILtQ(p, C0)))),
          IIntegrate(
              2059, Integrate(Power(P_,
                  p_), x_Symbol),
              Condition(
                  With(
                      List(Set(u,
                          Factor(PSymbol))),
                      Condition(Integrate(Power(u, p), x), Not(SumQ(NonfreeFactors(u, x))))),
                  And(PolyQ(PSymbol, x), IntegerQ(p)))),
          IIntegrate(2060,
              Integrate(
                  Power(
                      Plus(
                          a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT,
                              Sqr(x_)),
                          Times(d_DEFAULT, Power(x_, C3))),
                      p_),
                  x_Symbol),
              Condition(
                  Dist(Power(Power(C3, p), CN1), Subst(
                      Integrate(
                          Power(Simp(Plus(Times(Subtract(Times(C3, a, c), Sqr(b)), Power(c, CN1)),
                              Times(Sqr(c), Power(x, C3), Power(b, CN1))), x), p),
                          x),
                      x, Plus(Times(c, Power(Times(C3, d), CN1)), x)), x),
                  And(FreeQ(List(a, b, c, d), x), IGtQ(p, C0),
                      EqQ(Subtract(Sqr(c), Times(C3, b, d)), C0)))));
}
