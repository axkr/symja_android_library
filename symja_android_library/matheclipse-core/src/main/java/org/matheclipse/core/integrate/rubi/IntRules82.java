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
class IntRules82 {
  public static IAST RULES =
      List(
          IIntegrate(1641,
              Integrate(
                  Times($p("§pq"), Power(Plus(d_, Times(e_DEFAULT, x_)), m_DEFAULT),
                      Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(Power(Plus(d, Times(e, x)), Plus(m, p)),
                          Power(Plus(Times(a, Power(d, CN1)),
                              Times(c, x, Power(e, CN1))), p),
                          $s("§pq")),
                      x),
                  And(FreeQ(List(a, c, d, e, m), x), PolyQ($s("§pq"), x), EqQ(Plus(Times(c, Sqr(d)),
                      Times(a, Sqr(e))), C0), IntegerQ(
                          p)))),
          IIntegrate(
              1642, Integrate(Times($p("§pq"),
                  Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_DEFAULT), Power(Plus(a_DEFAULT,
                      Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Dist(Times(Power(Plus(a, Times(b, x), Times(c, Sqr(x))), FracPart(p)),
                      Power(
                          Times(Power(Plus(d, Times(e, x)), FracPart(p)),
                              Power(Plus(Times(a, Power(d, CN1)), Times(c, x, Power(e, CN1))),
                                  FracPart(p))),
                          CN1)),
                      Integrate(Times(Power(Plus(d, Times(e, x)), Plus(m, p)),
                          Power(Plus(Times(a, Power(d, CN1)), Times(c, x, Power(e, CN1))),
                              p),
                          $s("§pq")), x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, m, p), x), PolyQ($s("§pq"), x),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      EqQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a,
                          Sqr(e))), C0),
                      Not(IntegerQ(p)), Not(IGtQ(m, C0))))),
          IIntegrate(1643,
              Integrate(
                  Times(
                      $p("§pq"), Power(Plus(d_,
                          Times(e_DEFAULT, x_)), m_DEFAULT),
                      Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Plus(a, Times(c, Sqr(x))), FracPart(p)),
                          Power(Times(Power(Plus(d, Times(e, x)), FracPart(p)),
                              Power(Plus(Times(a, Power(d, CN1)), Times(c, x, Power(e, CN1))),
                                  FracPart(p))),
                              CN1)),
                      Integrate(Times(Power(Plus(d, Times(e, x)), Plus(m, p)),
                          Power(Plus(Times(a, Power(d, CN1)), Times(c, x, Power(e, CN1))), p),
                          $s("§pq")), x),
                      x),
                  And(FreeQ(List(a, c, d, e, m, p), x), PolyQ($s("§pq"), x),
                      EqQ(Plus(Times(c, Sqr(d)), Times(a,
                          Sqr(e))), C0),
                      Not(IntegerQ(p)), Not(IGtQ(m, C0))))),
          IIntegrate(1644,
              Integrate(Times(
                  $p("§pq"), Power(Plus(d_DEFAULT, Times(e_DEFAULT,
                      x_)), m_DEFAULT),
                  Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(QSymbol, PolynomialQuotient($s("§pq"),
                              Plus(a, Times(b, x), Times(c, Sqr(x))), x)),
                          Set(f,
                              Coeff(
                                  PolynomialRemainder($s("§pq"),
                                      Plus(a, Times(b, x), Times(c, Sqr(x))), x),
                                  x, C0)),
                          Set(g, Coeff(PolynomialRemainder($s("§pq"),
                              Plus(a, Times(b, x), Times(c, Sqr(x))), x), x, C1))),
                      Plus(
                          Simp(
                              Times(Power(Plus(d, Times(e, x)), m),
                                  Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p, C1)),
                                  Plus(Times(f, b), Times(CN1, C2, a,
                                      g), Times(Subtract(Times(C2, c, f), Times(b, g)), x)),
                                  Power(Times(Plus(p, C1), Subtract(Sqr(b), Times(C4, a, c))),
                                      CN1)),
                              x),
                          Dist(
                              Power(Times(Plus(p, C1),
                                  Subtract(Sqr(b), Times(C4, a, c))), CN1),
                              Integrate(
                                  Times(
                                      Power(Plus(d, Times(e,
                                          x)), Subtract(m,
                                              C1)),
                                      Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p,
                                          C1)),
                                      ExpandToSum(Subtract(
                                          Subtract(
                                              Plus(Times(
                                                  Plus(p, C1), Subtract(Sqr(b), Times(C4, a, c)),
                                                  Plus(d, Times(e, x)), QSymbol),
                                                  Times(g,
                                                      Plus(Times(C2, a, e, m),
                                                          Times(b, d, Plus(Times(C2, p), C3))))),
                                              Times(f,
                                                  Plus(Times(b, e, m),
                                                      Times(C2, c, d, Plus(Times(C2, p), C3))))),
                                          Times(e, Subtract(Times(C2, c, f), Times(b, g)),
                                              Plus(m, Times(C2, p), C3), x)),
                                          x)),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e), x), PolyQ($s("§pq"), x),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      NeQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))), C0),
                      LtQ(p, CN1), GtQ(m, C0),
                      Or(IntegerQ(p), Not(IntegerQ(m)), Not(
                          RationalQ(a, b, c, d, e))),
                      Not(And(IGtQ(m, C0), RationalQ(a, b, c, d, e),
                          Or(IntegerQ(p), ILtQ(Plus(p, C1D2), C0))))))),
          IIntegrate(1645, Integrate(Times($p("§pq"),
              Power(Plus(d_, Times(e_DEFAULT, x_)), m_DEFAULT),
              Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_)), x_Symbol), Condition(
                  With(
                      List(
                          Set(QSymbol, PolynomialQuotient($s("§pq"), Plus(a, Times(c, Sqr(x))), x)),
                          Set(f,
                              Coeff(PolynomialRemainder($s("§pq"), Plus(a, Times(c, Sqr(x))), x), x,
                                  C0)),
                          Set(g,
                              Coeff(PolynomialRemainder($s("§pq"), Plus(a, Times(c, Sqr(x))), x), x,
                                  C1))),
                      Plus(
                          Simp(
                              Times(Power(Plus(d, Times(e, x)), m), Power(Plus(a, Times(c, Sqr(x))),
                                  Plus(p, C1)), Subtract(Times(a, g), Times(c, f, x)),
                                  Power(Times(C2, a, c, Plus(p, C1)), CN1)),
                              x),
                          Dist(
                              Power(Times(C2, a, c,
                                  Plus(p, C1)), CN1),
                              Integrate(
                                  Times(Power(Plus(d, Times(e, x)), Subtract(m, C1)),
                                      Power(Plus(a, Times(c, Sqr(x))), Plus(p, C1)),
                                      ExpandToSum(Plus(
                                          Times(C2, a, c, Plus(p, C1), Plus(d, Times(e, x)),
                                              QSymbol),
                                          Times(CN1, a, e, g, m),
                                          Times(c, d, f, Plus(Times(C2, p), C3)),
                                          Times(c, e, f, Plus(m, Times(C2, p), C3), x)), x)),
                                  x),
                              x))),
                  And(FreeQ(List(a, c, d, e), x), PolyQ($s("§pq"), x),
                      NeQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), C0), LtQ(p, CN1), GtQ(m, C0),
                      Not(And(IGtQ(m, C0), RationalQ(a, c, d, e),
                          Or(IntegerQ(p), ILtQ(Plus(p, C1D2), C0))))))),
          IIntegrate(1646,
              Integrate(
                  Times(
                      $p("§pq"), Power(Plus(d_DEFAULT,
                          Times(e_DEFAULT, x_)), m_DEFAULT),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(QSymbol,
                              PolynomialQuotient(Times(Power(Plus(d, Times(e, x)), m), $s("§pq")),
                                  Plus(a, Times(b, x), Times(c, Sqr(x))), x)),
                          Set(f,
                              Coeff(PolynomialRemainder(
                                  Times(Power(Plus(d, Times(e, x)), m), $s("§pq")),
                                  Plus(a, Times(b, x), Times(c, Sqr(x))), x), x, C0)),
                          Set(g,
                              Coeff(
                                  PolynomialRemainder(
                                      Times(Power(Plus(d, Times(e, x)), m), $s("§pq")), Plus(a,
                                          Times(b, x), Times(c, Sqr(x))),
                                      x),
                                  x, C1))),
                      Plus(
                          Simp(
                              Times(
                                  Plus(
                                      Times(b, f), Times(CN1, C2, a, g),
                                      Times(Subtract(Times(C2, c, f), Times(b, g)), x)),
                                  Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p,
                                      C1)),
                                  Power(Times(Plus(p, C1), Subtract(Sqr(b), Times(C4, a, c))),
                                      CN1)),
                              x),
                          Dist(Power(Times(Plus(p, C1), Subtract(Sqr(b), Times(C4, a, c))), CN1),
                              Integrate(
                                  Times(Power(Plus(d, Times(e, x)), m),
                                      Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p, C1)),
                                      ExpandToSum(Subtract(
                                          Times(Plus(p, C1), Subtract(Sqr(b), Times(C4, a, c)),
                                              QSymbol, Power(Power(Plus(d, Times(e, x)), m), CN1)),
                                          Times(Plus(Times(C2, p), C3),
                                              Subtract(Times(C2, c, f), Times(b, g)),
                                              Power(Power(Plus(d, Times(e, x)), m), CN1))),
                                          x)),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e), x), PolyQ($s("§pq"), x),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      NeQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))), C0),
                      LtQ(p, CN1), ILtQ(m, C0)))),
          IIntegrate(1647,
              Integrate(Times(
                  $p("§pq"), Power(Plus(d_, Times(e_DEFAULT, x_)), m_DEFAULT),
                  Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_)), x_Symbol),
              Condition(
                  With(
                      List(
                          Set(QSymbol,
                              PolynomialQuotient(
                                  Times(Power(Plus(d, Times(e, x)),
                                      m), $s("§pq")),
                                  Plus(a, Times(c, Sqr(x))), x)),
                          Set(f, Coeff(
                              PolynomialRemainder(Times(Power(Plus(d, Times(e, x)), m), $s("§pq")),
                                  Plus(a, Times(c, Sqr(x))), x),
                              x, C0)),
                          Set(g,
                              Coeff(
                                  PolynomialRemainder(
                                      Times(Power(Plus(d, Times(e, x)), m), $s("§pq")), Plus(a,
                                          Times(c, Sqr(x))),
                                      x),
                                  x, C1))),
                      Plus(
                          Simp(
                              Times(Subtract(Times(a, g), Times(c, f, x)),
                                  Power(Plus(a, Times(c,
                                      Sqr(x))), Plus(p,
                                          C1)),
                                  Power(Times(C2, a, c, Plus(p, C1)), CN1)),
                              x),
                          Dist(Power(Times(C2, a, c, Plus(p, C1)), CN1),
                              Integrate(Times(Power(Plus(d, Times(e, x)), m),
                                  Power(Plus(a, Times(c, Sqr(x))), Plus(p, C1)),
                                  ExpandToSum(Plus(
                                      Times(C2, a, c, Plus(p, C1), QSymbol,
                                          Power(Power(Plus(d, Times(e, x)), m), CN1)),
                                      Times(c, f, Plus(Times(C2, p), C3),
                                          Power(Power(Plus(d, Times(e, x)), m), CN1))),
                                      x)),
                                  x),
                              x))),
                  And(FreeQ(List(a, c, d, e), x), PolyQ($s("§pq"), x),
                      NeQ(Plus(Times(c, Sqr(d)),
                          Times(a, Sqr(e))), C0),
                      LtQ(p, CN1), ILtQ(m, C0)))),
          IIntegrate(1648,
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
                              Plus(a, Times(b, x), Times(c, Sqr(x))), x)),
                          Set(f,
                              Coeff(
                                  PolynomialRemainder($s("§pq"),
                                      Plus(a, Times(b, x), Times(c, Sqr(x))), x),
                                  x, C0)),
                          Set(g,
                              Coeff(PolynomialRemainder($s("§pq"),
                                  Plus(a, Times(b, x), Times(c, Sqr(x))), x), x, C1))),
                      Plus(
                          Simp(Times(Power(Plus(d, Times(e, x)), Plus(m, C1)),
                              Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p, C1)),
                              Plus(
                                  Times(f,
                                      Plus(Times(b, c, d), Times(CN1, Sqr(b), e),
                                          Times(C2, a, c, e))),
                                  Times(CN1, a, g, Subtract(Times(C2, c, d), Times(b, e))),
                                  Times(c,
                                      Subtract(Times(f, Subtract(Times(C2, c, d), Times(b, e))),
                                          Times(g, Subtract(Times(b, d), Times(C2, a, e)))),
                                      x)),
                              Power(Times(Plus(p, C1), Subtract(Sqr(b), Times(C4, a, c)),
                                  Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e)))),
                                  CN1)),
                              x),
                          Dist(
                              Power(
                                  Times(Plus(p, C1), Subtract(Sqr(b), Times(C4, a, c)),
                                      Plus(Times(c, Sqr(d)), Times(CN1, b, d, e),
                                          Times(a, Sqr(e)))),
                                  CN1),
                              Integrate(
                                  Times(Power(Plus(d, Times(e, x)), m),
                                      Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p, C1)),
                                      ExpandToSum(Plus(
                                          Times(Plus(p, C1), Subtract(Sqr(b), Times(C4, a, c)),
                                              Plus(Times(c, Sqr(d)), Times(CN1, b, d, e),
                                                  Times(a, Sqr(e))),
                                              QSymbol),
                                          Times(f,
                                              Subtract(
                                                  Subtract(
                                                      Plus(
                                                          Times(b, c, d, e,
                                                              Plus(Times(C2, p), Negate(m), C2)),
                                                          Times(Sqr(b), Sqr(e), Plus(p, m, C2))),
                                                      Times(C2, Sqr(c), Sqr(d),
                                                          Plus(Times(C2, p), C3))),
                                                  Times(C2, a, c, Sqr(e),
                                                      Plus(m, Times(C2, p), C3)))),
                                          Times(CN1, g, Subtract(
                                              Times(a, e,
                                                  Plus(Times(b, e), Times(CN1, C2, c, d, m),
                                                      Times(b, e, m))),
                                              Times(b, d,
                                                  Subtract(Plus(Times(C3, c, d), Times(CN1, b, e),
                                                      Times(C2, c, d, p)), Times(b, e, p))))),
                                          Times(c, e,
                                              Subtract(
                                                  Times(g, Subtract(Times(b, d), Times(C2, a, e))),
                                                  Times(f, Subtract(Times(C2, c, d), Times(b, e)))),
                                              Plus(m, Times(C2, p), C4), x)),
                                          x)),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, m), x), PolyQ($s("§pq"), x),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      NeQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))), C0),
                      LtQ(p, CN1),
                      Not(And(IGtQ(m, C0), RationalQ(a, b, c, d, e),
                          Or(IntegerQ(p), ILtQ(Plus(p, C1D2), C0))))))),
          IIntegrate(1649,
              Integrate(Times($p("§pq"), Power(Plus(d_, Times(e_DEFAULT, x_)), m_DEFAULT),
                  Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_)), x_Symbol),
              Condition(
                  With(
                      List(
                          Set(QSymbol, PolynomialQuotient($s("§pq"), Plus(a, Times(c, Sqr(x))), x)),
                          Set(f, Coeff(PolynomialRemainder($s("§pq"), Plus(a, Times(c, Sqr(x))), x),
                              x, C0)),
                          Set(g,
                              Coeff(PolynomialRemainder($s("§pq"), Plus(a, Times(c, Sqr(x))), x), x,
                                  C1))),
                      Plus(
                          Negate(
                              Simp(
                                  Times(Power(Plus(d, Times(e, x)), Plus(m, C1)),
                                      Power(Plus(a, Times(c, Sqr(x))), Plus(p,
                                          C1)),
                                      Plus(
                                          Times(a, Subtract(Times(e, f),
                                              Times(d, g))),
                                          Times(Plus(Times(c, d, f), Times(a, e, g)), x)),
                                      Power(
                                          Times(C2, a, Plus(p, C1),
                                              Plus(Times(c, Sqr(d)), Times(a, Sqr(e)))),
                                          CN1)),
                                  x)),
                          Dist(
                              Power(Times(C2, a, Plus(p, C1),
                                  Plus(Times(c, Sqr(d)), Times(a, Sqr(e)))), CN1),
                              Integrate(Times(Power(Plus(d, Times(e, x)), m),
                                  Power(Plus(a, Times(c, Sqr(x))), Plus(p, C1)),
                                  ExpandToSum(Plus(
                                      Times(C2, a, Plus(p, C1),
                                          Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), QSymbol),
                                      Times(c, Sqr(d), f, Plus(Times(C2, p), C3)),
                                      Times(CN1, a, e,
                                          Subtract(Times(d, g, m),
                                              Times(e, f, Plus(m, Times(C2, p), C3)))),
                                      Times(e, Plus(Times(c, d, f), Times(a, e, g)),
                                          Plus(m, Times(C2, p), C4), x)),
                                      x)),
                                  x),
                              x))),
                  And(FreeQ(List(a, c, d, e, m), x), PolyQ($s("§pq"), x),
                      NeQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), C0), LtQ(p, CN1),
                      Not(And(IGtQ(m, C0), RationalQ(a, c, d, e),
                          Or(IntegerQ(p), ILtQ(Plus(p, C1D2), C0))))))),
          IIntegrate(1650,
              Integrate(
                  Times(
                      $p("§pq"), Power(Plus(d_DEFAULT,
                          Times(e_DEFAULT, x_)), m_),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(QSymbol, PolynomialQuotient($s("§pq"), Plus(d, Times(e, x)),
                          x)), Set($s("R"),
                              PolynomialRemainder($s("§pq"), Plus(d, Times(e, x)), x))),
                      Plus(
                          Simp(
                              Times(e, $s("R"), Power(Plus(d, Times(e, x)), Plus(m, C1)),
                                  Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p, C1)),
                                  Power(
                                      Times(Plus(m, C1),
                                          Plus(Times(c, Sqr(d)), Times(CN1, b, d, e),
                                              Times(a, Sqr(e)))),
                                      CN1)),
                              x),
                          Dist(
                              Power(
                                  Times(Plus(m, C1),
                                      Plus(Times(c, Sqr(
                                          d)), Times(CN1, b, d,
                                              e),
                                          Times(a, Sqr(e)))),
                                  CN1),
                              Integrate(
                                  Times(Power(Plus(d, Times(e, x)), Plus(m, C1)),
                                      Power(Plus(a, Times(b, x), Times(c, Sqr(x))), p), ExpandToSum(
                                          Subtract(
                                              Subtract(
                                                  Plus(Times(Plus(m, C1),
                                                      Plus(Times(c, Sqr(d)), Times(CN1, b, d, e),
                                                          Times(a, Sqr(e))),
                                                      QSymbol), Times(c, d, $s("R"), Plus(m, C1))),
                                                  Times(b, e, $s("R"), Plus(m, p, C2))),
                                              Times(c, e, $s("R"), Plus(m, Times(C2, p), C3), x)),
                                          x)),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, p), x), PolyQ($s("§pq"), x),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      NeQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))), C0), LtQ(m,
                          CN1)))),
          IIntegrate(1651,
              Integrate(Times(
                  $p("§pq"), Power(Plus(d_, Times(e_DEFAULT, x_)), m_),
                  Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_)), x_Symbol),
              Condition(With(
                  List(
                      Set(QSymbol, PolynomialQuotient($s("§pq"), Plus(d, Times(
                          e, x)), x)),
                      Set($s("R"), PolynomialRemainder($s("§pq"), Plus(d, Times(e, x)), x))),
                  Plus(
                      Simp(
                          Times(e, $s("R"), Power(Plus(d, Times(e, x)), Plus(m, C1)),
                              Power(Plus(a, Times(c, Sqr(x))), Plus(p, C1)),
                              Power(Times(Plus(m, C1), Plus(Times(c, Sqr(d)), Times(a, Sqr(e)))),
                                  CN1)),
                          x),
                      Dist(
                          Power(Times(Plus(m, C1), Plus(Times(c, Sqr(
                              d)), Times(a,
                                  Sqr(e)))),
                              CN1),
                          Integrate(
                              Times(Power(Plus(d, Times(e, x)), Plus(m, C1)),
                                  Power(Plus(a, Times(c, Sqr(x))), p),
                                  ExpandToSum(Subtract(
                                      Plus(
                                          Times(Plus(m, C1),
                                              Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), QSymbol),
                                          Times(c, d, $s("R"), Plus(m, C1))),
                                      Times(c, e, $s("R"), Plus(m, Times(C2, p), C3), x)), x)),
                              x),
                          x))),
                  And(FreeQ(List(a, c, d, e, p), x), PolyQ($s("§pq"), x), NeQ(Plus(Times(c, Sqr(d)),
                      Times(a, Sqr(e))), C0), LtQ(m,
                          CN1)))),
          IIntegrate(1652,
              Integrate(
                  Times(
                      $p("§pq"), Power(x_, m_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Module(List(Set(q, Expon($s("§pq"), x)), k),
                      Plus(
                          Integrate(
                              Times(Power(x, m),
                                  Sum(Times(Coeff($s("§pq"), x, Times(C2, k)),
                                      Power(x, Times(C2, k))), List(k, C0, Times(C1D2, q))),
                                  Power(Plus(a, Times(b, Sqr(x))), p)),
                              x),
                          Integrate(Times(Power(x, Plus(m, C1)),
                              Sum(Times(Coeff($s("§pq"), x, Plus(Times(C2, k), C1)),
                                  Power(x, Times(C2, k))),
                                  List(k, C0, Times(C1D2, Subtract(q, C1)))),
                              Power(Plus(a, Times(b, Sqr(x))), p)), x))),
                  And(FreeQ(List(a, b, p), x), PolyQ($s("§pq"), x), Not(PolyQ($s("§pq"), Sqr(x))),
                      IGtQ(m, CN2), Not(IntegerQ(Times(C2, p)))))),
          IIntegrate(1653,
              Integrate(
                  Times(
                      $p("§pq"), Power(Plus(d_DEFAULT,
                          Times(e_DEFAULT, x_)), m_DEFAULT),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(q, Expon($s("§pq"), x)), Set(f,
                              Coeff($s("§pq"), x, Expon($s("§pq"), x)))),
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
                                  Integrate(
                                      Times(Power(Plus(d, Times(e, x)), m), Power(Plus(a, Times(b,
                                          x), Times(c, Sqr(x))), p), ExpandToSum(
                                              Subtract(
                                                  Subtract(
                                                      Times(c, Power(e, q), Plus(m, q, Times(C2, p),
                                                          C1), $s("§pq")),
                                                      Times(
                                                          c, f, Plus(m, q, Times(C2, p), C1), Power(
                                                              Plus(d, Times(e, x)), q))),
                                                  Times(f,
                                                      Power(Plus(d, Times(e, x)), Subtract(q, C2)),
                                                      Subtract(
                                                          Subtract(
                                                              Plus(Times(b, d, e, Plus(p, C1)),
                                                                  Times(a, Sqr(e),
                                                                      Subtract(Plus(m, q), C1))),
                                                              Times(c, Sqr(d),
                                                                  Plus(m, q, Times(C2, p), C1))),
                                                          Times(e,
                                                              Subtract(Times(C2, c, d),
                                                                  Times(b, e)),
                                                              Plus(m, q, p), x)))),
                                              x)),
                                      x),
                                  x)),
                          And(GtQ(q, C1), NeQ(Plus(m, q, Times(C2, p), C1), C0)))),
                  And(FreeQ(List(a, b, c, d, e, m, p), x), PolyQ($s("§pq"), x),
                      NeQ(Subtract(Sqr(b), Times(C4, a,
                          c)), C0),
                      NeQ(Plus(Times(c, Sqr(
                          d)), Times(CN1, b, d,
                              e),
                          Times(a, Sqr(e))), C0),
                      Not(And(
                          IGtQ(m, C0), RationalQ(a, b, c, d, e), Or(IntegerQ(p),
                              ILtQ(Plus(p, C1D2), C0))))))),
          IIntegrate(1654,
              Integrate(
                  Times(
                      $p("§pq"), Power(Plus(d_,
                          Times(e_DEFAULT, x_)), m_DEFAULT),
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
                                      Power(Plus(a, Times(c,
                                          Sqr(x))), Plus(p,
                                              C1)),
                                      Power(
                                          Times(
                                              c, Power(e, Subtract(q, C1)), Plus(m, q, Times(C2, p),
                                                  C1)),
                                          CN1)),
                                  x),
                              Dist(Power(Times(c, Power(e, q), Plus(m, q, Times(C2, p), C1)), CN1),
                                  Integrate(
                                      Times(Power(Plus(d, Times(e, x)), m),
                                          Power(Plus(
                                              a, Times(c, Sqr(x))), p),
                                          ExpandToSum(Subtract(Subtract(
                                              Times(c, Power(e, q), Plus(m, q, Times(C2, p), C1),
                                                  $s("§pq")),
                                              Times(c, f, Plus(
                                                  m, q, Times(C2, p), C1),
                                                  Power(Plus(d, Times(e, x)), q))),
                                              Times(f, Power(Plus(d, Times(e, x)), Subtract(q, C2)),
                                                  Subtract(Subtract(
                                                      Times(a, Sqr(e), Subtract(Plus(m, q), C1)),
                                                      Times(c, Sqr(d),
                                                          Plus(m, q, Times(C2, p), C1))),
                                                      Times(C2, c, d, e, Plus(m, q, p), x)))),
                                              x)),
                                      x),
                                  x)),
                          And(GtQ(q, C1), NeQ(Plus(m, q, Times(C2, p), C1), C0)))),
                  And(FreeQ(List(a, c, d, e, m, p), x), PolyQ($s("§pq"), x),
                      NeQ(Plus(Times(c, Sqr(
                          d)), Times(a,
                              Sqr(e))),
                          C0),
                      Not(And(EqQ(d, C0),
                          True)),
                      Not(And(IGtQ(m, C0), RationalQ(a, c, d, e),
                          Or(IntegerQ(p), ILtQ(Plus(p, C1D2), C0))))))),
          IIntegrate(1655,
              Integrate(
                  Times(
                      $p("§pq"), Power(Plus(d_DEFAULT,
                          Times(e_DEFAULT, x_)), m_DEFAULT),
                      Power(
                          Plus(a_DEFAULT, Times(b_DEFAULT, x_),
                              Times(c_DEFAULT, Sqr(x_))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(q,
                          Expon($s("§pq"), x))),
                      Plus(
                          Dist(
                              Times(Coeff($s(
                                  "§pq"), x, q), Power(Power(e, q),
                                      CN1)),
                              Integrate(Times(Power(Plus(d, Times(e, x)), Plus(m, q)),
                                  Power(Plus(a, Times(b, x), Times(c, Sqr(x))), p)), x),
                              x),
                          Dist(Power(Power(e, q), CN1),
                              Integrate(Times(Power(Plus(d, Times(e, x)), m),
                                  Power(Plus(a, Times(b, x), Times(c, Sqr(x))), p),
                                  ExpandToSum(Subtract(Times(Power(e, q), $s("§pq")),
                                      Times(Coeff($s("§pq"), x, q),
                                          Power(Plus(d, Times(e, x)), q))),
                                      x)),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, m, p), x), PolyQ($s("§pq"), x),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      NeQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))), C0), Not(
                          And(IGtQ(m, C0), RationalQ(a, b, c, d, e),
                              Or(IntegerQ(p), ILtQ(Plus(p, C1D2), C0))))))),
          IIntegrate(1656,
              Integrate(
                  Times(
                      $p("§pq"), Power(Plus(d_, Times(e_DEFAULT,
                          x_)), m_DEFAULT),
                      Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(q,
                          Expon($s("§pq"), x))),
                      Plus(
                          Dist(
                              Times(Coeff($s("§pq"), x, q), Power(Power(e, q),
                                  CN1)),
                              Integrate(
                                  Times(
                                      Power(Plus(d, Times(e, x)), Plus(m, q)), Power(
                                          Plus(a, Times(c, Sqr(x))), p)),
                                  x),
                              x),
                          Dist(Power(Power(e, q), CN1),
                              Integrate(Times(Power(Plus(d, Times(e, x)), m),
                                  Power(Plus(a, Times(c, Sqr(x))), p),
                                  ExpandToSum(Subtract(Times(Power(e, q), $s("§pq")),
                                      Times(Coeff($s("§pq"), x, q),
                                          Power(Plus(d, Times(e, x)), q))),
                                      x)),
                                  x),
                              x))),
                  And(FreeQ(List(a, c, d, e, m, p), x), PolyQ($s("§pq"), x),
                      NeQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), C0),
                      Not(And(IGtQ(m, C0), RationalQ(a, c, d, e),
                          Or(IntegerQ(p), ILtQ(Plus(p, C1D2), C0))))))),
          IIntegrate(1657,
              Integrate(
                  Times(
                      $p("§pq"), Power(Plus(a_, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(ExpandIntegrand(Times($s("§pq"),
                      Power(Plus(a, Times(b, x), Times(c, Sqr(x))), p)),
                      x), x),
                  And(FreeQ(List(a, b, c), x), PolyQ($s("§pq"), x), IGtQ(p, CN2)))),
          IIntegrate(1658,
              Integrate(
                  Times($p("§pq"),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Integrate(Times(x, PolynomialQuotient($s("§pq"), x, x),
                      Power(Plus(a, Times(b, x), Times(c, Sqr(x))), p)), x),
                  And(FreeQ(List(a, b, c, p), x), PolyQ($s("§pq"), x),
                      EqQ(Coeff($s("§pq"), x, C0), C0),
                      Not(MatchQ($s("§pq"), Condition(Times(Power(x, m_DEFAULT), u_DEFAULT),
                          IntegerQ(m))))))),
          IIntegrate(1659,
              Integrate(
                  Times($p("§pq"),
                      Power(Plus(a_, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Plus(a, Times(b, x), Times(c, Sqr(x))), FracPart(p)),
                          Power(
                              Times(Power(Times(C4, c), IntPart(p)),
                                  Power(Plus(b, Times(C2, c, x)), Times(C2, FracPart(p)))),
                              CN1)),
                      Integrate(Times($s("§pq"), Power(Plus(b, Times(C2, c,
                          x)), Times(C2,
                              p))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, p), x), PolyQ($s("§pq"), x),
                      EqQ(Subtract(Sqr(b), Times(C4, a, c)), C0)))),
          IIntegrate(1660,
              Integrate(
                  Times($p("§pq"),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(QSymbol,
                              PolynomialQuotient($s("§pq"), Plus(a, Times(b, x), Times(c, Sqr(x))),
                                  x)),
                          Set(f,
                              Coeff(PolynomialRemainder($s("§pq"),
                                  Plus(a, Times(b, x), Times(c, Sqr(x))), x), x, C0)),
                          Set(g,
                              Coeff(
                                  PolynomialRemainder($s("§pq"),
                                      Plus(a, Times(b, x), Times(c, Sqr(x))), x),
                                  x, C1))),
                      Plus(
                          Simp(
                              Times(
                                  Plus(Times(b, f), Times(
                                      CN1, C2, a, g),
                                      Times(Subtract(Times(C2, c, f), Times(b, g)), x)),
                                  Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p,
                                      C1)),
                                  Power(Times(Plus(p, C1), Subtract(Sqr(b), Times(C4, a, c))),
                                      CN1)),
                              x),
                          Dist(Power(Times(Plus(p, C1), Subtract(Sqr(b), Times(C4, a, c))), CN1),
                              Integrate(
                                  Times(Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p, C1)),
                                      ExpandToSum(Subtract(
                                          Times(Plus(p, C1), Subtract(Sqr(b), Times(C4, a, c)),
                                              QSymbol),
                                          Times(Plus(Times(C2, p), C3),
                                              Subtract(Times(C2, c, f), Times(b, g)))),
                                          x)),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c), x), PolyQ($s("§pq"), x),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0), LtQ(p, CN1)))));
}
