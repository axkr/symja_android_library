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
class IntRules81 {
  public static IAST RULES =
      List(
          IIntegrate(1621,
              Integrate(
                  Times(
                      $p("§px"), Power(Plus(a_DEFAULT,
                          Times(b_DEFAULT, x_)), m_),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set($s("§qx"), PolynomialQuotient($s("§px"), Plus(a, Times(b, x)),
                              x)),
                          Set($s("R"), PolynomialRemainder($s("§px"), Plus(a, Times(b, x)), x))),
                      Plus(
                          Simp(
                              Times($s("R"), Power(Plus(a, Times(b, x)), Plus(m, C1)),
                                  Power(Plus(c, Times(d, x)), Plus(n, C1)),
                                  Power(Times(Plus(m, C1), Subtract(Times(b, c), Times(a, d))),
                                      CN1)),
                              x),
                          Dist(
                              Power(Times(Plus(m, C1),
                                  Subtract(Times(b, c), Times(a, d))), CN1),
                              Integrate(
                                  Times(Power(Plus(a, Times(b, x)), Plus(m, C1)),
                                      Power(Plus(c, Times(d, x)), n),
                                      ExpandToSum(
                                          Subtract(Times(Plus(m, C1),
                                              Subtract(Times(b, c), Times(a, d)), $s("§qx")),
                                              Times(d, $s("R"), Plus(m, n, C2))),
                                          x)),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, n), x), PolyQ($s("§px"), x), ILtQ(m, CN1),
                      GtQ(Expon($s("§px"), x), C2)))),
          IIntegrate(1622,
              Integrate(
                  Times(
                      $p("§px"), Power(Plus(a_DEFAULT,
                          Times(b_DEFAULT, x_)), m_),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set($s("§qx"), PolynomialQuotient($s("§px"), Plus(a, Times(b, x)), x)),
                          Set($s("R"), PolynomialRemainder($s("§px"), Plus(a, Times(b, x)), x))),
                      Plus(
                          Simp(
                              Times($s("R"), Power(Plus(a, Times(b, x)), Plus(m, C1)),
                                  Power(Plus(c, Times(d, x)), Plus(n, C1)),
                                  Power(Times(Plus(m, C1), Subtract(Times(b, c), Times(a, d))),
                                      CN1)),
                              x),
                          Dist(Power(Times(Plus(m, C1), Subtract(Times(b, c), Times(a, d))), CN1),
                              Integrate(
                                  Times(Power(Plus(a, Times(b, x)), Plus(m, C1)),
                                      Power(Plus(c, Times(d, x)), n),
                                      ExpandToSum(Subtract(Times(Plus(m, C1),
                                          Subtract(Times(b, c), Times(a, d)), $s("§qx")),
                                          Times(d, $s("R"), Plus(m, n, C2))), x)),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, n), x), PolyQ($s("§px"), x), LtQ(m, CN1),
                      GtQ(Expon($s("§px"), x), C2)))),
          IIntegrate(1623,
              Integrate(Times($p("§px"), Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), m_DEFAULT),
                  Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), n_DEFAULT)), x_Symbol),
              Condition(
                  With(
                      List(
                          Set(q, Expon($s("§px"),
                              x)),
                          Set(k, Coeff($s("§px"), x, Expon($s("§px"), x)))),
                      Condition(
                          Plus(
                              Simp(
                                  Times(k, Power(Plus(a, Times(b, x)), Plus(m, q)),
                                      Power(Plus(c, Times(d, x)), Plus(n,
                                          C1)),
                                      Power(Times(d, Power(b, q), Plus(m, n, q, C1)), CN1)),
                                  x),
                              Dist(Power(Times(d, Power(b, q), Plus(m, n, q, C1)), CN1), Integrate(
                                  Times(Power(Plus(a, Times(b, x)), m),
                                      Power(Plus(c, Times(d, x)), n),
                                      ExpandToSum(Subtract(
                                          Subtract(
                                              Times(d, Power(b, q), Plus(m, n, q, C1), $s("§px")),
                                              Times(d, k, Plus(m, n, q, C1),
                                                  Power(Plus(a, Times(b, x)), q))),
                                          Times(k, Subtract(Times(b, c), Times(a, d)), Plus(m, q),
                                              Power(Plus(a, Times(b, x)), Subtract(q, C1)))),
                                          x)),
                                  x), x)),
                          NeQ(Plus(m, n, q, C1), C0))),
                  And(FreeQ(List(a, b, c, d, m, n), x), PolyQ($s("§px"), x),
                      GtQ(Expon($s("§px"), x), C2)))),
          IIntegrate(1624,
              Integrate(Times($p("§pq"), Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_DEFAULT),
                  Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))),
                      p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(Power(Plus(d, Times(e, x)), Plus(m, C1)),
                          PolynomialQuotient($s("§pq"), Plus(d, Times(e, x)),
                              x),
                          Power(Plus(a, Times(b, x), Times(c, Sqr(x))), p)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, m, p), x), PolyQ($s("§pq"), x), EqQ(
                      PolynomialRemainder($s("§pq"), Plus(d, Times(e, x)), x), C0)))),
          IIntegrate(1625,
              Integrate(
                  Times(
                      $p("§pq"), Power(Plus(d_, Times(e_DEFAULT, x_)), m_DEFAULT), Power(
                          Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          Power(Plus(d, Times(e,
                              x)), Plus(m,
                                  C1)),
                          PolynomialQuotient($s("§pq"), Plus(d, Times(e, x)),
                              x),
                          Power(Plus(a, Times(c, Sqr(x))), p)),
                      x),
                  And(FreeQ(List(a, c, d, e, m, p), x), PolyQ($s(
                      "§pq"), x), EqQ(PolynomialRemainder($s("§pq"), Plus(d, Times(e, x)), x),
                          C0)))),
          IIntegrate(1626,
              Integrate(
                  Times($p("§p2"), Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_DEFAULT),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(With(
                  List(Set(f, Coeff($s("§p2"), x, C0)), Set(g, Coeff($s("§p2"), x, C1)),
                      Set(h, Coeff($s("§p2"), x, C2))),
                  Condition(
                      Simp(Times(h, Power(Plus(d, Times(e, x)), Plus(m, C1)),
                          Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p, C1)),
                          Power(Times(c, e, Plus(m, Times(C2, p), C3)), CN1)), x),
                      And(EqQ(
                          Subtract(
                              Plus(Times(b, e, h, Plus(m, p, C2)), Times(C2, c, d, h, Plus(p, C1))),
                              Times(c, e, g, Plus(m, Times(C2, p), C3))),
                          C0),
                          EqQ(Subtract(
                              Plus(Times(b, d, h, Plus(p, C1)), Times(a, e, h, Plus(m, C1))),
                              Times(c, e, f, Plus(m, Times(C2, p), C3))), C0)))),
                  And(FreeQ(List(a, b, c, d, e, m, p), x), PolyQ($s("§p2"), x, C2),
                      NeQ(Plus(m, Times(C2, p), C3), C0)))),
          IIntegrate(1627,
              Integrate(
                  Times($p("§p2"), Power(Plus(d_, Times(e_DEFAULT, x_)), m_DEFAULT),
                      Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(f, Coeff($s("§p2"), x, C0)), Set(g, Coeff($s("§p2"), x, C1)),
                          Set(h, Coeff($s("§p2"), x, C2))),
                      Condition(
                          Simp(
                              Times(h, Power(Plus(d, Times(e, x)), Plus(m, C1)),
                                  Power(Plus(a, Times(c, Sqr(x))), Plus(p, C1)),
                                  Power(Times(c, e, Plus(m, Times(C2, p), C3)), CN1)),
                              x),
                          And(EqQ(Subtract(Times(C2, d, h, Plus(p, C1)),
                              Times(e, g, Plus(m, Times(C2, p), C3))), C0),
                              EqQ(Subtract(Times(a, h, Plus(m, C1)),
                                  Times(c, f, Plus(m, Times(C2, p), C3))), C0)))),
                  And(FreeQ(List(a, c, d, e, m, p), x), PolyQ($s("§p2"), x, C2),
                      NeQ(Plus(m, Times(C2, p), C3), C0)))),
          IIntegrate(1628,
              Integrate(
                  Times(
                      $p("§pq"), Power(Plus(d_DEFAULT,
                          Times(e_DEFAULT, x_)), m_DEFAULT),
                      Power(
                          Plus(a_DEFAULT, Times(b_DEFAULT, x_),
                              Times(c_DEFAULT, Sqr(x_))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(Integrate(
                  ExpandIntegrand(
                      Times(Power(Plus(d, Times(e, x)), m), $s("§pq"),
                          Power(Plus(a, Times(b, x), Times(c, Sqr(x))), p)),
                      x),
                  x), And(FreeQ(List(a, b, c, d, e, m), x), PolyQ($s("§pq"), x), IGtQ(p, CN2)))),
          IIntegrate(1629,
              Integrate(
                  Times(
                      $p("§pq"), Power(Plus(d_, Times(e_DEFAULT,
                          x_)), m_DEFAULT),
                      Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Times(Power(Plus(d, Times(e, x)), m), $s("§pq"),
                              Power(Plus(a, Times(c, Sqr(x))), p)),
                          x),
                      x),
                  And(FreeQ(List(a, c, d, e, m), x), PolyQ($s("§pq"), x), IGtQ(p, CN2)))),
          IIntegrate(
              1630, Integrate(Times($p("§pq"),
                  Power(Plus(d_DEFAULT, Times(e_DEFAULT,
                      x_)), m_DEFAULT),
                  Power(Plus(a_, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_)), x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Plus(a, Times(b, x), Times(c, Sqr(x))), FracPart(p)),
                          Power(
                              Times(
                                  Power(Times(C4, c), IntPart(p)), Power(Plus(b, Times(C2, c, x)),
                                      Times(C2, FracPart(p)))),
                              CN1)),
                      Integrate(Times(Power(Plus(d, Times(e, x)), m), $s("§pq"),
                          Power(Plus(b, Times(C2, c, x)), Times(C2, p))), x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, m, p), x), PolyQ($s("§pq"), x),
                      EqQ(Subtract(Sqr(b), Times(C4, a, c)), C0)))),
          IIntegrate(1631,
              Integrate(
                  Times($p("§pq"), Power(Times(e_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      e,
                      Integrate(Times(Power(Times(e, x), Subtract(m, C1)),
                          PolynomialQuotient($s("§pq"), Plus(b, Times(c, x)), x),
                          Power(Plus(Times(b, x), Times(c, Sqr(x))), Plus(p, C1))), x),
                      x),
                  And(FreeQ(List(b, c, e, m, p), x), PolyQ($s("§pq"), x), EqQ(
                      PolynomialRemainder($s("§pq"), Plus(b, Times(c, x)), x), C0)))),
          IIntegrate(1632,
              Integrate(Times($p("§pq"), Power(Plus(d_, Times(e_DEFAULT, x_)), m_DEFAULT),
                  Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))),
                      p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Times(d, e),
                      Integrate(Times(Power(Plus(d, Times(e, x)), Subtract(m, C1)),
                          PolynomialQuotient($s("§pq"), Plus(Times(a, e), Times(c, d, x)), x),
                          Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p, C1))), x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, m, p), x), PolyQ($s("§pq"), x),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      EqQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))), C0),
                      EqQ(PolynomialRemainder($s("§pq"), Plus(Times(a, e), Times(c, d, x)), x),
                          C0)))),
          IIntegrate(1633,
              Integrate(
                  Times($p("§pq"), Power(Plus(d_, Times(e_DEFAULT, x_)), m_DEFAULT),
                      Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Times(d, e),
                      Integrate(
                          Times(Power(Plus(d, Times(e, x)), Subtract(m, C1)),
                              PolynomialQuotient($s("§pq"), Plus(Times(a, e), Times(c, d, x)), x),
                              Power(Plus(a, Times(c, Sqr(x))), Plus(p, C1))),
                          x),
                      x),
                  And(FreeQ(List(a, c, d, e, m, p), x), PolyQ($s("§pq"), x),
                      EqQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), C0), EqQ(
                          PolynomialRemainder($s("§pq"), Plus(Times(a, e), Times(c, d, x)),
                              x),
                          C0)))),
          IIntegrate(1634,
              Integrate(
                  Times(
                      $p("§pq"), Power(Plus(d_DEFAULT,
                          Times(e_DEFAULT, x_)), m_DEFAULT),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(QSymbol, PolynomialQuotient($s("§pq"),
                              Plus(Times(a, e), Times(c, d, x)), x)),
                          Set(f,
                              PolynomialRemainder($s("§pq"), Plus(Times(a, e), Times(c, d, x)),
                                  x))),
                      Plus(Simp(Times(
                          f, Subtract(Times(C2, c, d), Times(b, e)), Power(Plus(d, Times(e, x)), m),
                          Power(Plus(a, Times(b, x),
                              Times(c, Sqr(x))), Plus(p, C1)),
                          Power(Times(e, Plus(p, C1), Subtract(Sqr(b), Times(C4, a, c))), CN1)), x),
                          Dist(Power(Times(Plus(p, C1), Subtract(Sqr(b), Times(C4, a, c))), CN1),
                              Integrate(Times(Power(Plus(d, Times(e, x)), Subtract(m, C1)),
                                  Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p, C1)),
                                  ExpandToSum(Subtract(
                                      Times(d, e, Plus(p, C1), Subtract(Sqr(b), Times(C4, a, c)),
                                          QSymbol),
                                      Times(f, Subtract(Times(C2, c, d), Times(b, e)),
                                          Plus(m, Times(C2, p), C2))),
                                      x)),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e), x), PolyQ($s("§pq"), x),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      EqQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a,
                          Sqr(e))), C0),
                      ILtQ(Plus(p, C1D2), C0), GtQ(m, C0)))),
          IIntegrate(1635,
              Integrate(Times($p("§pq"), Power(Plus(d_, Times(e_DEFAULT, x_)), m_DEFAULT),
                  Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_)), x_Symbol),
              Condition(
                  With(
                      List(
                          Set(QSymbol,
                              PolynomialQuotient($s("§pq"), Plus(Times(a, e), Times(c, d, x)), x)),
                          Set(f,
                              PolynomialRemainder($s("§pq"), Plus(Times(a, e), Times(c, d, x)),
                                  x))),
                      Plus(Negate(Simp(Times(d, f, Power(Plus(d, Times(e, x)), m),
                          Power(Plus(a, Times(c, Sqr(x))),
                              Plus(p, C1)),
                          Power(Times(C2, a, e, Plus(p, C1)), CN1)), x)), Dist(
                              Times(d, Power(Times(C2, a, Plus(p, C1)), CN1)),
                              Integrate(Times(Power(Plus(d, Times(e, x)), Subtract(m, C1)),
                                  Power(Plus(a, Times(c, Sqr(x))), Plus(p, C1)),
                                  ExpandToSum(Plus(Times(C2, a, e, Plus(p, C1), QSymbol),
                                      Times(f, Plus(m, Times(C2, p), C2))), x)),
                                  x),
                              x))),
                  And(FreeQ(List(a, c, d, e), x), PolyQ($s("§pq"), x),
                      EqQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), C0), ILtQ(Plus(p, C1D2), C0),
                      GtQ(m, C0)))),
          IIntegrate(1636,
              Integrate(
                  Times(
                      $p("§pq"), Power(Plus(d_DEFAULT,
                          Times(e_DEFAULT, x_)), m_),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Power(Plus(a, Times(b, x),
                              Times(c, Sqr(x))), p),
                          Times(Power(Plus(d, Times(e, x)), m), $s("§pq")), x),
                      x),
                  And(FreeQ(List(a, b, c, d, e), x), PolyQ($s("§pq"), x),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      EqQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))),
                          C0),
                      EqQ(Plus(m, Expon($s("§pq"), x), Times(C2, p), C1), C0), ILtQ(m, C0)))),
          IIntegrate(1637,
              Integrate(
                  Times($p("§pq"), Power(Plus(d_, Times(e_DEFAULT, x_)), m_),
                      Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Power(Plus(a, Times(c, Sqr(x))), p), Times(Power(Plus(d, Times(e, x)), m),
                              $s("§pq")),
                          x),
                      x),
                  And(FreeQ(List(a, c, d, e), x), PolyQ($s("§pq"), x),
                      EqQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))),
                          C0),
                      EqQ(Plus(m, Expon($s("§pq"), x), Times(C2, p), C1), C0), ILtQ(m, C0)))),
          IIntegrate(1638,
              Integrate(
                  Times(
                      $p("§pq"), Power(Plus(d_DEFAULT,
                          Times(e_DEFAULT, x_)), m_DEFAULT),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  With(List(
                      Set(q, Expon($s("§pq"),
                          x)),
                      Set(f, Coeff($s("§pq"), x, Expon($s("§pq"), x)))),
                      Condition(
                          Plus(
                              Simp(
                                  Times(f, Power(Plus(d, Times(e, x)), Subtract(Plus(m, q), C1)),
                                      Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p, C1)),
                                      Power(
                                          Times(c, Power(e, Subtract(q, C1)),
                                              Plus(m, q, Times(C2, p), C1)),
                                          CN1)),
                                  x),
                              Dist(Power(Times(c, Power(e, q), Plus(m, q, Times(C2, p), C1)), CN1),
                                  Integrate(Times(Power(Plus(d, Times(e, x)), m),
                                      Power(Plus(a, Times(b, x), Times(c, Sqr(x))), p),
                                      ExpandToSum(Plus(
                                          Times(c, Power(e, q), Plus(m, q, Times(C2, p), C1),
                                              $s("§pq")),
                                          Times(
                                              CN1, c, f, Plus(m, q, Times(C2, p), C1),
                                              Power(Plus(d, Times(e, x)), q)),
                                          Times(e, f, Plus(m, p, q),
                                              Power(Plus(d, Times(e, x)), Subtract(q, C2)),
                                              Plus(Times(b, d), Times(CN1, C2, a, e),
                                                  Times(Subtract(Times(C2, c, d), Times(b, e)),
                                                      x)))),
                                          x)),
                                      x),
                                  x)),
                          NeQ(Plus(m, q, Times(C2, p), C1), C0))),
                  And(FreeQ(List(a, b, c, d, e, m, p), x), PolyQ($s("§pq"), x),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0), EqQ(Plus(Times(c, Sqr(d)),
                          Times(CN1, b, d, e), Times(a, Sqr(e))), C0)))),
          IIntegrate(1639,
              Integrate(
                  Times($p("§pq"), Power(Plus(d_, Times(e_DEFAULT, x_)), m_DEFAULT),
                      Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(q, Expon($s("§pq"),
                              x)),
                          Set(f, Coeff($s("§pq"), x, Expon($s("§pq"), x)))),
                      Condition(
                          Plus(
                              Simp(
                                  Times(f, Power(Plus(d, Times(e, x)), Subtract(Plus(m, q), C1)),
                                      Power(Plus(a, Times(c, Sqr(x))), Plus(p, C1)),
                                      Power(
                                          Times(c, Power(e, Subtract(q, C1)),
                                              Plus(m, q, Times(C2, p), C1)),
                                          CN1)),
                                  x),
                              Dist(
                                  Power(Times(c, Power(e, q),
                                      Plus(m, q, Times(C2, p), C1)), CN1),
                                  Integrate(Times(Power(Plus(d, Times(e, x)), m),
                                      Power(Plus(a, Times(c, Sqr(x))), p),
                                      ExpandToSum(Subtract(
                                          Subtract(
                                              Times(c, Power(e, q), Plus(m, q, Times(C2, p), C1),
                                                  $s("§pq")),
                                              Times(c, f, Plus(m, q, Times(C2, p), C1),
                                                  Power(Plus(d, Times(e, x)), q))),
                                          Times(C2, e, f, Plus(m, p, q),
                                              Power(Plus(d, Times(e, x)), Subtract(q, C2)),
                                              Subtract(Times(a, e), Times(c, d, x)))),
                                          x)),
                                      x),
                                  x)),
                          NeQ(Plus(m, q, Times(C2, p), C1), C0))),
                  And(FreeQ(List(a, c, d, e, m, p), x), PolyQ($s("§pq"),
                      x), EqQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), C0),
                      Not(IGtQ(m, C0))))),
          IIntegrate(1640,
              Integrate(Times(
                  $p("§pq"), Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_DEFAULT),
                  Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))),
                      p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(Times(Power(Plus(d, Times(e, x)), Plus(m, p)),
                      Power(Plus(Times(a, Power(d, CN1)), Times(c, x, Power(e, CN1))), p),
                      $s("§pq")), x),
                  And(FreeQ(List(a, b, c, d, e, m), x), PolyQ($s("§pq"), x),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      EqQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))), C0),
                      IntegerQ(p)))));
}
