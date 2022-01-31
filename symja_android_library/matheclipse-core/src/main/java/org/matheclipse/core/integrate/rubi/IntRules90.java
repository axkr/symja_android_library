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
class IntRules90 {
  public static IAST RULES = List(
      IIntegrate(1801,
          Integrate(Times($p("§p2"), Power(Times(c_DEFAULT, x_), m_DEFAULT),
              Power(Plus(a_, Times(b_DEFAULT, Sqr(x_))), p_DEFAULT)), x_Symbol),
          Condition(With(
              List(Set(f, Coeff($s("§p2"), x, C0)), Set(g, Coeff($s("§p2"), x, C1)),
                  Set(h, Coeff($s("§p2"), x, C2))),
              Condition(
                  Simp(Times(h, Power(Times(c, x), Plus(m, C1)),
                      Power(Plus(a, Times(b, Sqr(x))), Plus(p, C1)),
                      Power(Times(b, c, Plus(m, Times(C2, p), C3)), CN1)), x),
                  And(EqQ(g, C0),
                      EqQ(Subtract(Times(a, h, Plus(m, C1)),
                          Times(b, f, Plus(m, Times(C2, p), C3))), C0)))),
              And(FreeQ(List(a, b, c, m, p), x), PolyQ($s("§p2"), x, C2), NeQ(m, CN1)))),
      IIntegrate(1802,
          Integrate(
              Times($p("§pq"), Power(Times(c_DEFAULT, x_), m_DEFAULT),
                  Power(Plus(a_, Times(b_DEFAULT, Sqr(x_))), p_DEFAULT)),
              x_Symbol),
          Condition(
              Integrate(
                  ExpandIntegrand(
                      Times(Power(Times(c,
                          x), m), $s(
                              "§pq"),
                          Power(Plus(a, Times(b, Sqr(x))), p)),
                      x),
                  x),
              And(FreeQ(List(a, b, c, m), x), PolyQ($s("§pq"), x), IGtQ(p, CN2)))),
      IIntegrate(1803,
          Integrate(Times($p("§pq"), Power(x_, m_), Power(Plus(a_, Times(b_DEFAULT, Sqr(x_))), p_)),
              x_Symbol),
          Condition(
              With(
                  List(
                      Set(ASymbol, Coeff($s("§pq"), x,
                          C0)),
                      Set(QSymbol,
                          PolynomialQuotient(Subtract($s("§pq"), Coeff($s("§pq"), x,
                              C0)), Sqr(
                                  x),
                              x))),
                  Plus(Simp(Times(ASymbol, Power(x, Plus(m, C1)),
                      Power(Plus(a, Times(b, Sqr(x))), Plus(p, C1)), Power(Times(a, Plus(m, C1)),
                          CN1)),
                      x),
                      Dist(Power(Times(a, Plus(m, C1)), CN1),
                          Integrate(
                              Times(Power(x, Plus(m, C2)), Power(Plus(a, Times(b, Sqr(x))), p),
                                  Subtract(Times(a, Plus(m, C1), QSymbol),
                                      Times(ASymbol, b, Plus(m, Times(C2, Plus(p, C1)), C1)))),
                              x),
                          x))),
              And(FreeQ(List(a, b), x), PolyQ($s("§pq"), Sqr(x)), IntegerQ(Times(C1D2, m)),
                  ILtQ(Plus(Times(C1D2, Plus(m, C1)),
                      p), C0),
                  LtQ(Plus(m, Expon($s("§pq"), x), Times(C2, p), C1), C0)))),
      IIntegrate(1804,
          Integrate(Times($p("§pq"), Power(Times(c_DEFAULT, x_), m_DEFAULT),
              Power(Plus(a_, Times(b_DEFAULT, Sqr(x_))), p_)), x_Symbol),
          Condition(
              With(
                  List(
                      Set(QSymbol, PolynomialQuotient($s("§pq"), Plus(a, Times(b, Sqr(x))),
                          x)),
                      Set(f,
                          Coeff(PolynomialRemainder($s("§pq"), Plus(a, Times(b, Sqr(x))), x), x,
                              C0)),
                      Set(g,
                          Coeff(
                              PolynomialRemainder($s("§pq"), Plus(a, Times(b, Sqr(x))), x), x,
                              C1))),
                  Plus(
                      Simp(Times(Power(Times(c, x), m),
                          Power(Plus(a, Times(b, Sqr(x))), Plus(p, C1)),
                          Subtract(Times(a, g), Times(b, f, x)),
                          Power(Times(C2, a, b, Plus(p, C1)), CN1)), x),
                      Dist(Times(c, Power(Times(C2, a, b, Plus(p, C1)), CN1)), Integrate(Times(
                          Power(Times(c, x), Subtract(m, C1)),
                          Power(Plus(a, Times(b, Sqr(x))), Plus(p, C1)),
                          ExpandToSum(Plus(Times(C2, a, b, Plus(p, C1), x, QSymbol),
                              Times(CN1, a, g, m), Times(b, f, Plus(m, Times(C2, p), C3), x)), x)),
                          x), x))),
              And(FreeQ(List(a, b, c), x), PolyQ($s("§pq"), x), LtQ(p, CN1), GtQ(m, C0)))),
      IIntegrate(1805,
          Integrate(Times($p("§pq"), Power(Times(c_DEFAULT, x_), m_DEFAULT),
              Power(Plus(a_, Times(b_DEFAULT, Sqr(x_))), p_)), x_Symbol),
          Condition(
              With(
                  List(
                      Set(QSymbol,
                          PolynomialQuotient(
                              Times(Power(Times(c, x),
                                  m), $s("§pq")),
                              Plus(a, Times(b, Sqr(x))), x)),
                      Set(f,
                          Coeff(
                              PolynomialRemainder(Times(Power(Times(c, x), m), $s("§pq")),
                                  Plus(a, Times(b, Sqr(x))), x),
                              x, C0)),
                      Set(g,
                          Coeff(
                              PolynomialRemainder(
                                  Times(Power(Times(c, x), m), $s("§pq")), Plus(a,
                                      Times(b, Sqr(x))),
                                  x),
                              x, C1))),
                  Plus(
                      Simp(
                          Times(Subtract(Times(a, g), Times(b, f, x)),
                              Power(Plus(a, Times(b, Sqr(x))), Plus(p,
                                  C1)),
                              Power(Times(C2, a, b, Plus(p, C1)), CN1)),
                          x),
                      Dist(Power(Times(C2, a, Plus(p, C1)), CN1), Integrate(Times(
                          Power(Times(c, x), m), Power(Plus(a, Times(b, Sqr(x))), Plus(p, C1)),
                          ExpandToSum(Plus(
                              Times(C2, a, Plus(p, C1), QSymbol, Power(Power(Times(c, x), m), CN1)),
                              Times(f, Plus(Times(C2, p), C3), Power(Power(Times(c, x), m), CN1))),
                              x)),
                          x), x))),
              And(FreeQ(List(a, b, c), x), PolyQ($s("§pq"), x), LtQ(p, CN1), ILtQ(m, C0)))),
      IIntegrate(1806,
          Integrate(
              Times(
                  $p("§pq"), Power(Times(c_DEFAULT, x_), m_DEFAULT), Power(
                      Plus(a_, Times(b_DEFAULT, Sqr(x_))), p_)),
              x_Symbol),
          Condition(With(
              List(Set(QSymbol, PolynomialQuotient($s("§pq"), Plus(a, Times(b, Sqr(x))), x)),
                  Set(f,
                      Coeff(PolynomialRemainder($s("§pq"), Plus(a, Times(b, Sqr(x))), x), x, C0)),
                  Set(g, Coeff(PolynomialRemainder($s("§pq"), Plus(a, Times(b, Sqr(x))), x), x,
                      C1))),
              Plus(
                  Negate(
                      Simp(
                          Times(Power(Times(c, x), Plus(m, C1)), Plus(f, Times(g, x)),
                              Power(Plus(a, Times(b, Sqr(x))), Plus(p, C1)), Power(
                                  Times(C2, a, c, Plus(p, C1)), CN1)),
                          x)),
                  Dist(Power(Times(C2, a, Plus(p, C1)), CN1),
                      Integrate(Times(Power(Times(c, x), m),
                          Power(Plus(a, Times(b, Sqr(x))), Plus(p, C1)),
                          ExpandToSum(Plus(Times(C2, a, Plus(p, C1), QSymbol),
                              Times(f, Plus(m, Times(C2, p), C3)),
                              Times(g, Plus(m, Times(C2, p), C4), x)), x)),
                          x),
                      x))),
              And(FreeQ(List(a, b, c, m), x), PolyQ($s("§pq"), x), LtQ(p, CN1), Not(GtQ(m, C0))))),
      IIntegrate(1807,
          Integrate(
              Times(
                  $p("§pq"), Power(Times(c_DEFAULT,
                      x_), m_),
                  Power(Plus(a_, Times(b_DEFAULT, Sqr(x_))), p_)),
              x_Symbol),
          Condition(
              With(
                  List(
                      Set(QSymbol, PolynomialQuotient($s("§pq"), Times(c, x), x)), Set($s("R"),
                          PolynomialRemainder($s("§pq"), Times(c, x), x))),
                  Plus(
                      Simp(Times($s("R"), Power(Times(c, x), Plus(m, C1)),
                          Power(Plus(a, Times(b, Sqr(x))), Plus(p, C1)),
                          Power(Times(a, c, Plus(m, C1)), CN1)), x),
                      Dist(Power(Times(a, c, Plus(m, C1)), CN1),
                          Integrate(
                              Times(Power(Times(c, x), Plus(m, C1)),
                                  Power(Plus(a, Times(b, Sqr(x))), p),
                                  ExpandToSum(Subtract(Times(a, c, Plus(m, C1), QSymbol),
                                      Times(b, $s("R"), Plus(m, Times(C2, p), C3), x)), x)),
                              x),
                          x))),
              And(FreeQ(List(a, b, c, p), x), PolyQ($s("§pq"), x), LtQ(m, CN1),
                  Or(IntegerQ(Times(C2, p)), NeQ(Expon($s("§pq"), x), C1))))),
      IIntegrate(1808,
          Integrate(
              Times(
                  $p("§pq"), Power(Times(c_DEFAULT, x_), m_DEFAULT), Power(
                      Plus(a_, Times(b_DEFAULT, Sqr(x_))), p_DEFAULT)),
              x_Symbol),
          Condition(
              With(List(Set(q, Expon($s("§pq"), x))),
                  Condition(
                      Plus(Dist(Times(Coeff($s("§pq"), x, q), Power(Power(c, q), CN1)),
                          Integrate(
                              Times(Power(Times(c, x), Plus(m, q)),
                                  Power(Plus(a, Times(b, Sqr(x))), p)),
                              x),
                          x),
                          Dist(
                              Power(Power(c,
                                  q), CN1),
                              Integrate(
                                  Times(Power(Times(c, x), m), Power(Plus(a, Times(b, Sqr(x))), p),
                                      ExpandToSum(
                                          Subtract(Times(Power(c, q), $s("§pq")),
                                              Times(Coeff($s("§pq"), x, q), Power(Times(c, x), q))),
                                          x)),
                                  x),
                              x)),
                      Or(EqQ(q, C1), EqQ(Plus(m, q, Times(C2, p), C1), C0)))),
              And(FreeQ(List(a, b, c, m, p), x), PolyQ($s("§pq"), x),
                  Not(And(IGtQ(m, C0), ILtQ(Plus(p, C1D2), C0)))))),
      IIntegrate(1809,
          Integrate(
              Times(
                  $p("§pq"), Power(Times(c_DEFAULT,
                      x_), m_DEFAULT),
                  Power(Plus(a_, Times(b_DEFAULT, Sqr(x_))), p_)),
              x_Symbol),
          Condition(With(
              List(Set(q, Expon($s("§pq"), x)), Set(f, Coeff($s("§pq"), x, Expon($s("§pq"), x)))),
              Condition(
                  Plus(
                      Simp(
                          Times(f, Power(Times(c, x), Subtract(Plus(m, q), C1)),
                              Power(Plus(a, Times(b,
                                  Sqr(x))), Plus(p,
                                      C1)),
                              Power(
                                  Times(b, Power(c, Subtract(q,
                                      C1)), Plus(m, q, Times(C2, p),
                                          C1)),
                                  CN1)),
                          x),
                      Dist(Power(Times(b, Plus(m, q, Times(C2, p), C1)), CN1), Integrate(Times(
                          Power(Times(c, x), m), Power(Plus(a, Times(b, Sqr(x))), p),
                          ExpandToSum(
                              Subtract(
                                  Subtract(Times(b, Plus(m, q, Times(C2, p), C1), $s("§pq")),
                                      Times(b, f, Plus(m, q, Times(C2, p), C1), Power(x, q))),
                                  Times(a, f, Subtract(Plus(m, q), C1), Power(x, Subtract(q, C2)))),
                              x)),
                          x), x)),
                  And(GtQ(q, C1), NeQ(Plus(m, q, Times(C2, p), C1), C0)))),
              And(FreeQ(List(a, b, c, m, p), x), PolyQ($s("§pq"), x),
                  Or(Not(IGtQ(m, C0)), IGtQ(Plus(p, C1D2), CN1))))),
      IIntegrate(1810,
          Integrate(Times($p("§pq"), Power(Plus(a_, Times(b_DEFAULT, Sqr(x_))), p_DEFAULT)),
              x_Symbol),
          Condition(
              Integrate(ExpandIntegrand(Times($s("§pq"), Power(Plus(a, Times(b, Sqr(x))), p)), x),
                  x),
              And(FreeQ(List(a, b), x), PolyQ($s("§pq"), x), IGtQ(p, CN2)))),
      IIntegrate(1811,
          Integrate(Times($p("§pq"), Power(Plus(a_, Times(b_DEFAULT, Sqr(x_))), p_)), x_Symbol),
          Condition(
              Integrate(Times(x, PolynomialQuotient($s("§pq"), x, x),
                  Power(Plus(a, Times(b, Sqr(x))), p)), x),
              And(FreeQ(List(a, b, p), x), PolyQ($s("§pq"), x), EqQ(Coeff($s("§pq"), x, C0), C0),
                  Not(MatchQ($s("§pq"), Condition(Times(Power(x, m_DEFAULT), u_DEFAULT),
                      IntegerQ(m))))))),
      IIntegrate(1812,
          Integrate(Times($p("§px"),
              Power(Plus(a_, Times(b_DEFAULT, Sqr(x_))), p_DEFAULT)), x_Symbol),
          Condition(
              Integrate(
                  Times(
                      PolynomialQuotient($s("§px"), Plus(a, Times(b, Sqr(x))),
                          x),
                      Power(Plus(a, Times(b, Sqr(x))), Plus(p, C1))),
                  x),
              And(FreeQ(List(a, b, p), x), PolyQ($s(
                  "§px"), x), EqQ(PolynomialRemainder($s("§px"), Plus(a, Times(b, Sqr(x))), x),
                      C0)))),
      IIntegrate(1813,
          Integrate(Times($p("§pq"), Power(Plus(a_, Times(b_DEFAULT, Sqr(x_))), p_)), x_Symbol),
          Condition(
              With(
                  List(Set(ASymbol, Coeff($s("§pq"), x, C0)),
                      Set(QSymbol,
                          PolynomialQuotient(Subtract($s("§pq"), Coeff($s("§pq"), x, C0)), Sqr(x),
                              x))),
                  Plus(
                      Simp(
                          Times(
                              ASymbol, x, Power(Plus(a, Times(b, Sqr(x))), Plus(p, C1)), Power(a,
                                  CN1)),
                          x),
                      Dist(Power(a, CN1),
                          Integrate(
                              Times(Sqr(x), Power(Plus(a, Times(b, Sqr(x))), p),
                                  Subtract(Times(a, QSymbol),
                                      Times(ASymbol, b, Plus(Times(C2, p), C3)))),
                              x),
                          x))),
              And(FreeQ(List(a, b), x), PolyQ($s("§pq"), Sqr(x)), ILtQ(Plus(p, C1D2), C0),
                  LtQ(Plus(Expon($s("§pq"), x), Times(C2, p), C1), C0)))),
      IIntegrate(1814,
          Integrate(Times($p("§pq"),
              Power(Plus(a_, Times(b_DEFAULT, Sqr(x_))), p_)), x_Symbol),
          Condition(
              With(
                  List(Set(QSymbol, PolynomialQuotient($s("§pq"), Plus(a, Times(b, Sqr(x))), x)),
                      Set(f,
                          Coeff(PolynomialRemainder($s("§pq"), Plus(a, Times(b, Sqr(x))), x), x,
                              C0)),
                      Set(g,
                          Coeff(PolynomialRemainder($s("§pq"), Plus(a, Times(b, Sqr(x))), x), x,
                              C1))),
                  Plus(
                      Simp(
                          Times(Subtract(Times(a, g), Times(b, f, x)),
                              Power(Plus(a, Times(b, Sqr(x))),
                                  Plus(p, C1)),
                              Power(Times(C2, a, b, Plus(p, C1)), CN1)),
                          x),
                      Dist(
                          Power(Times(C2, a,
                              Plus(p, C1)), CN1),
                          Integrate(
                              Times(Power(Plus(a, Times(b, Sqr(x))), Plus(p, C1)),
                                  ExpandToSum(
                                      Plus(Times(C2, a, Plus(p, C1), QSymbol),
                                          Times(f, Plus(Times(C2, p), C3))),
                                      x)),
                              x),
                          x))),
              And(FreeQ(List(a, b), x), PolyQ($s("§pq"), x), LtQ(p, CN1)))),
      IIntegrate(1815,
          Integrate(Times($p("§pq"),
              Power(Plus(a_, Times(b_DEFAULT, Sqr(x_))), p_)), x_Symbol),
          Condition(
              With(
                  List(
                      Set(q, Expon($s("§pq"),
                          x)),
                      Set(e, Coeff($s("§pq"), x, Expon($s("§pq"), x)))),
                  Plus(
                      Simp(
                          Times(e, Power(x, Subtract(q, C1)),
                              Power(Plus(a, Times(b,
                                  Sqr(x))), Plus(p,
                                      C1)),
                              Power(Times(b, Plus(q, Times(C2, p), C1)), CN1)),
                          x),
                      Dist(Power(Times(b, Plus(q, Times(C2, p), C1)), CN1),
                          Integrate(
                              Times(Power(Plus(a, Times(b, Sqr(x))),
                                  p),
                                  ExpandToSum(
                                      Subtract(
                                          Subtract(Times(b, Plus(q, Times(C2, p), C1), $s("§pq")),
                                              Times(a, e, Subtract(q, C1),
                                                  Power(x, Subtract(q, C2)))),
                                          Times(b, e, Plus(q, Times(C2, p), C1), Power(x, q))),
                                      x)),
                              x),
                          x))),
              And(FreeQ(List(a, b, p), x), PolyQ($s("§pq"), x), Not(LeQ(p, CN1))))),
      IIntegrate(1816,
          Integrate(
              Times(Power(x_, m_DEFAULT),
                  Power(Plus(a_,
                      Times(c_DEFAULT, Power(x_, n_DEFAULT))), QQ(-3L, 2L)),
                  Plus(e_, Times(h_DEFAULT, Power(x_, n_DEFAULT)),
                      Times(f_DEFAULT, Power(x_, q_DEFAULT)),
                      Times(g_DEFAULT, Power(x_, r_DEFAULT)))),
              x_Symbol),
          Condition(
              Negate(Simp(Times(
                  Subtract(Plus(Times(C2, a, g), Times(C4, a, h, Power(x, Times(C1D4, n)))),
                      Times(C2, c, f, Power(x, Times(C1D2, n)))),
                  Power(Times(a, c, n, Sqrt(Plus(a, Times(c, Power(x, n))))), CN1)), x)),
              And(FreeQ(List(a, c, e, f, g, h, m, n), x), EqQ(q, Times(C1D4, n)),
                  EqQ(r, Times(C1D4, C3, n)), EqQ(Plus(Times(C4, m), Negate(n), C4), C0),
                  EqQ(Plus(Times(c, e), Times(a, h)), C0)))),
      IIntegrate(1817, Integrate(Times(Power(Times(d_, x_), m_DEFAULT),
          Power(Plus(a_, Times(c_DEFAULT, Power(x_, n_DEFAULT))), QQ(-3L, 2L)), Plus(e_,
              Times(h_DEFAULT, Power(x_, n_DEFAULT)), Times(f_DEFAULT, Power(x_, q_DEFAULT)), Times(
                  g_DEFAULT, Power(x_, r_DEFAULT)))),
          x_Symbol),
          Condition(
              Dist(Times(Power(Times(d, x), m), Power(Power(x, m), CN1)),
                  Integrate(
                      Times(Power(x, m),
                          Plus(e, Times(f, Power(x, Times(C1D4, n))),
                              Times(g, Power(x, Times(C1D4, C3, n))), Times(h, Power(x, n))),
                          Power(Plus(a, Times(c, Power(x, n))), QQ(-3L, 2L))),
                      x),
                  x),
              And(FreeQ(List(a, c, d, e, f, g, h, m, n), x),
                  EqQ(Plus(Times(C4, m), Negate(n), C4), C0), EqQ(q, Times(C1D4, n)), EqQ(r, Times(
                      C1D4, C3, n)),
                  EqQ(Plus(Times(c, e), Times(a, h)), C0)))),
      IIntegrate(1818,
          Integrate(
              Times(
                  $p("§pq"), Power(Times(c_DEFAULT,
                      x_), m_),
                  Power(Plus(a_, Times(b_DEFAULT, x_)), p_)),
              x_Symbol),
          Condition(
              With(
                  List(Set(n,
                      Denominator(p))),
                  Dist(Times(n, Power(b, CN1)),
                      Subst(
                          Integrate(Times(Power(x, Subtract(Plus(Times(n, p), n), C1)),
                              Power(Plus(Times(CN1, a, c, Power(b, CN1)),
                                  Times(c, Power(x, n), Power(b, CN1))), m),
                              ReplaceAll($s("§pq"),
                                  Rule(x,
                                      Plus(Times(CN1, a, Power(b, CN1)),
                                          Times(Power(x, n), Power(b, CN1)))))),
                              x),
                          x, Power(Plus(a, Times(b, x)), Power(n, CN1))),
                      x)),
              And(FreeQ(List(a, b, c, m), x), PolyQ($s("§pq"), x), FractionQ(p), ILtQ(m, CN1)))),
      IIntegrate(1819,
          Integrate(
              Times(
                  $p("§pq"), Power(x_, m_DEFAULT), Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))),
                      p_DEFAULT)),
              x_Symbol),
          Condition(
              Dist(
                  Power(Plus(m,
                      C1), CN1),
                  Subst(
                      Integrate(
                          Times(SubstFor(Power(x, Plus(m, C1)), $s("§pq"), x),
                              Power(
                                  Plus(
                                      a,
                                      Times(b,
                                          Power(x, Simplify(Times(n, Power(Plus(m, C1), CN1)))))),
                                  p)),
                          x),
                      x, Power(x, Plus(m, C1))),
                  x),
              And(FreeQ(List(a, b, m, n, p), x), NeQ(m, CN1),
                  IGtQ(Simplify(Times(n, Power(Plus(m, C1), CN1))), C0),
                  PolyQ($s("§pq"), Power(x, Plus(m, C1)))))),
      IIntegrate(1820,
          Integrate(Times($p("§pq"), Power(Times(c_DEFAULT, x_), m_DEFAULT),
              Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_DEFAULT))), p_DEFAULT)), x_Symbol),
          Condition(
              Integrate(ExpandIntegrand(
                  Times(Power(Times(c, x), m), $s("§pq"), Power(Plus(a, Times(b, Power(x, n))), p)),
                  x), x),
              And(FreeQ(List(a, b, c, m, n), x), PolyQ($s("§pq"), x),
                  Or(IGtQ(p, C0), EqQ(n, C1))))));
}
