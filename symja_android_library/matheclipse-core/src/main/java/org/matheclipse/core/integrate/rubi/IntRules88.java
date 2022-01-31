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
class IntRules88 {
  public static IAST RULES =
      List(
          IIntegrate(1761,
              Integrate(
                  Times($p("§pq"), Power(x_, m_DEFAULT),
                      Power(
                          Plus(
                              a_, Times(c_DEFAULT, Power(x_,
                                  $p("n2", true))),
                              Times(b_DEFAULT, Power(x_, n_))),
                          p_)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(q,
                          Expon($s("§pq"), x))),
                      Negate(Subst(Integrate(Times(
                          ExpandToSum(
                              Times(Power(x, q), ReplaceAll($s("§pq"), Rule(x, Power(x, CN1)))), x),
                          Power(Plus(a, Times(b, Power(Power(x, n), CN1)),
                              Times(c, Power(Power(x, Times(C2, n)), CN1))), p),
                          Power(Power(x, Plus(m, q, C2)), CN1)), x), x, Power(x, CN1)))),
                  And(FreeQ(List(a, b, c, p), x), EqQ($s("n2"), Times(C2, n)), PolyQ($s("§pq"),
                      x), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0), ILtQ(n, C0),
                      IntegerQ(m)))),
          IIntegrate(1762,
              Integrate(
                  Times(
                      $p("§pq"), Power(Times(d_DEFAULT,
                          x_), m_DEFAULT),
                      Power(
                          Plus(
                              a_, Times(c_DEFAULT, Power(x_,
                                  $p("n2", true))),
                              Times(b_DEFAULT, Power(x_, n_))),
                          p_)),
                  x_Symbol),
              Condition(
                  With(List(Set(g, Denominator(m)), Set(q, Expon($s("§pq"), x))),
                      Negate(
                          Dist(Times(g, Power(d, CN1)),
                              Subst(
                                  Integrate(
                                      Times(
                                          ExpandToSum(
                                              Times(
                                                  Power(x, Times(g,
                                                      q)),
                                                  ReplaceAll(
                                                      $s("§pq"),
                                                      Rule(x, Power(Times(d, Power(x, g)), CN1)))),
                                              x),
                                          Power(
                                              Plus(a, Times(b,
                                                  Power(Times(Power(d, n), Power(x, Times(g, n))),
                                                      CN1)),
                                                  Times(c,
                                                      Power(Times(Power(d, Times(C2, n)),
                                                          Power(x, Times(C2, g, n))), CN1))),
                                              p),
                                          Power(Power(x, Plus(Times(g, Plus(m, q, C1)), C1)), CN1)),
                                      x),
                                  x, Power(Power(Times(d, x), Power(g, CN1)), CN1)),
                              x))),
                  And(FreeQ(List(a, b, c, d, p), x), EqQ($s("n2"), Times(C2, n)),
                      PolyQ($s("§pq"), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0), ILtQ(n,
                          C0),
                      FractionQ(m)))),
          IIntegrate(1763,
              Integrate(
                  Times(
                      $p("§pq"), Power(Times(d_DEFAULT,
                          x_), m_),
                      Power(
                          Plus(
                              a_, Times(c_DEFAULT, Power(x_,
                                  $p("n2", true))),
                              Times(b_DEFAULT, Power(x_, n_))),
                          p_)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(q,
                          Expon($s("§pq"), x))),
                      Negate(
                          Dist(
                              Times(Power(Times(d,
                                  x), m), Power(Power(x, CN1),
                                      m)),
                              Subst(
                                  Integrate(
                                      Times(
                                          ExpandToSum(
                                              Times(Power(x, q),
                                                  ReplaceAll($s("§pq"), Rule(x, Power(x, CN1)))),
                                              x),
                                          Power(Plus(a, Times(b, Power(Power(x, n), CN1)),
                                              Times(c, Power(Power(x, Times(C2, n)), CN1))), p),
                                          Power(Power(x, Plus(m, q, C2)), CN1)),
                                      x),
                                  x, Power(x, CN1)),
                              x))),
                  And(FreeQ(List(a, b, c, d, m, p), x), EqQ($s("n2"), Times(C2, n)),
                      PolyQ($s("§pq"), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0), ILtQ(n,
                          C0),
                      Not(RationalQ(m))))),
          IIntegrate(1764,
              Integrate(
                  Times($p("§pq"), Power(x_, m_DEFAULT),
                      Power(
                          Plus(
                              a_, Times(c_DEFAULT, Power(x_,
                                  $p("n2", true))),
                              Times(b_DEFAULT, Power(x_, n_))),
                          p_)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(g,
                          Denominator(n))),
                      Dist(g,
                          Subst(
                              Integrate(Times(Power(x, Subtract(Times(g, Plus(m, C1)), C1)),
                                  ReplaceAll($s("§pq"), Rule(x, Power(x, g))),
                                  Power(Plus(a, Times(b, Power(x, Times(g, n))),
                                      Times(c, Power(x, Times(C2, g, n)))), p)),
                                  x),
                              x, Power(x, Power(g, CN1))),
                          x)),
                  And(FreeQ(List(a, b, c, m, p), x), EqQ($s("n2"), Times(C2, n)),
                      PolyQ($s("§pq"), x), NeQ(Subtract(Sqr(b),
                          Times(C4, a, c)), C0),
                      FractionQ(n)))),
          IIntegrate(1765,
              Integrate(
                  Times(
                      $p("§pq"), Power(Times(d_,
                          x_), m_),
                      Power(
                          Plus(
                              a_, Times(c_DEFAULT, Power(x_, $p("n2", true))),
                              Times(b_DEFAULT, Power(x_, n_))),
                          p_)),
                  x_Symbol),
              Condition(
                  Dist(Times(Power(d, Subtract(m, C1D2)), Sqrt(Times(d, x)), Power(x, CN1D2)),
                      Integrate(Times(Power(x, m), $s("§pq"),
                          Power(Plus(a, Times(b, Power(x, n)), Times(c, Power(x, Times(C2, n)))),
                              p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, p), x), EqQ($s("n2"), Times(C2, n)),
                      PolyQ($s("§pq"), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)),
                          C0),
                      FractionQ(n), IGtQ(Plus(m, C1D2), C0)))),
          IIntegrate(1766,
              Integrate(
                  Times(
                      $p("§pq"), Power(Times(d_,
                          x_), m_),
                      Power(
                          Plus(
                              a_, Times(c_DEFAULT, Power(x_,
                                  $p("n2", true))),
                              Times(b_DEFAULT, Power(x_, n_))),
                          p_)),
                  x_Symbol),
              Condition(
                  Dist(Times(Power(d, Plus(m, C1D2)), Sqrt(x), Power(Times(d, x), CN1D2)),
                      Integrate(Times(Power(x, m), $s("§pq"),
                          Power(Plus(a, Times(b, Power(x, n)), Times(c, Power(x, Times(C2, n)))),
                              p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, p), x), EqQ($s("n2"), Times(C2, n)),
                      PolyQ($s("§pq"), x), NeQ(Subtract(Sqr(b),
                          Times(C4, a, c)), C0),
                      FractionQ(n), ILtQ(Subtract(m, C1D2), C0)))),
          IIntegrate(1767,
              Integrate(
                  Times(
                      $p("§pq"), Power(Times(d_,
                          x_), m_),
                      Power(
                          Plus(
                              a_, Times(c_DEFAULT, Power(x_, $p("n2", true))), Times(b_DEFAULT,
                                  Power(x_, n_))),
                          p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Times(d,
                          x), m), Power(Power(x, m),
                              CN1)),
                      Integrate(
                          Times(Power(x, m), $s("§pq"),
                              Power(
                                  Plus(a, Times(b, Power(x,
                                      n)), Times(c,
                                          Power(x, Times(C2, n)))),
                                  p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, m, p), x), EqQ($s("n2"), Times(C2, n)),
                      PolyQ($s("§pq"), x), NeQ(Subtract(Sqr(b),
                          Times(C4, a, c)), C0),
                      FractionQ(n)))),
          IIntegrate(1768,
              Integrate(
                  Times(
                      $p("§pq"), Power(x_, m_DEFAULT), Power(Plus(a_,
                          Times(c_DEFAULT, Power(x_, $p("n2",
                              true))),
                          Times(b_DEFAULT, Power(x_, n_))), p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Plus(m,
                          C1), CN1),
                      Subst(
                          Integrate(
                              Times(
                                  ReplaceAll(SubstFor(Power(x, n), $s("§pq"), x),
                                      Rule(x,
                                          Power(x, Simplify(Times(n, Power(Plus(m, C1), CN1)))))),
                                  Power(
                                      Plus(a,
                                          Times(b,
                                              Power(x,
                                                  Simplify(Times(n, Power(Plus(m, C1), CN1))))),
                                          Times(c,
                                              Power(x,
                                                  Simplify(
                                                      Times(C2, n, Power(Plus(m, C1), CN1)))))),
                                      p)),
                              x),
                          x, Power(x, Plus(m, C1))),
                      x),
                  And(FreeQ(List(a, b, c, m, n, p), x), EqQ($s("n2"), Times(C2, n)),
                      PolyQ($s("§pq"), Power(x, n)), NeQ(Subtract(Sqr(b), Times(C4, a,
                          c)), C0),
                      IntegerQ(Simplify(Times(n, Power(Plus(m, C1), CN1)))), Not(IntegerQ(n))))),
          IIntegrate(1769,
              Integrate(
                  Times(
                      $p("§pq"), Power(Times(d_,
                          x_), m_),
                      Power(
                          Plus(
                              a_, Times(c_DEFAULT, Power(x_, $p("n2", true))), Times(b_DEFAULT,
                                  Power(x_, n_))),
                          p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Times(d,
                          x), m), Power(Power(x, m),
                              CN1)),
                      Integrate(
                          Times(Power(x, m), $s("§pq"),
                              Power(
                                  Plus(a, Times(b, Power(x, n)),
                                      Times(c, Power(x, Times(C2, n)))),
                                  p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, m, p), x), EqQ($s("n2"), Times(C2, n)),
                      PolyQ($s("§pq"), Power(x, n)), NeQ(Subtract(Sqr(b), Times(C4, a,
                          c)), C0),
                      IntegerQ(Simplify(Times(n, Power(Plus(m, C1), CN1)))), Not(IntegerQ(n))))),
          IIntegrate(1770,
              Integrate(
                  Times(
                      $p("§pq"), Power(Times(d_DEFAULT,
                          x_), m_DEFAULT),
                      Power(
                          Plus(
                              a_, Times(b_DEFAULT, Power(x_, n_DEFAULT)),
                              Times(c_DEFAULT, Power(x_, $p("n2", true)))),
                          CN1)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(q,
                          Rt(Subtract(Sqr(b), Times(C4, a, c)), C2))),
                      Subtract(
                          Dist(Times(C2, c, Power(q, CN1)),
                              Integrate(
                                  Times(Power(Times(d, x), m), $s("§pq"),
                                      Power(Plus(b, Negate(q), Times(C2, c, Power(x, n))), CN1)),
                                  x),
                              x),
                          Dist(Times(C2, c, Power(q, CN1)),
                              Integrate(
                                  Times(
                                      Power(Times(d, x), m), $s("§pq"),
                                      Power(Plus(b, q, Times(C2, c, Power(x, n))), CN1)),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, m, n), x), EqQ($s("n2"), Times(C2, n)), PolyQ(
                      $s("§pq"), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0)))),
          IIntegrate(1771,
              Integrate(
                  Times(
                      $p("§pq"), Power(Times(d_DEFAULT,
                          x_), m_DEFAULT),
                      Power(
                          Plus(a_, Times(b_DEFAULT, Power(x_, n_DEFAULT)), Times(c_DEFAULT,
                              Power(x_, $p("n2", true)))),
                          p_)),
                  x_Symbol),
              Condition(
                  Integrate(ExpandIntegrand(
                      Times(Power(Times(d, x), m), $s("§pq"),
                          Power(Plus(a, Times(b, Power(x, n)), Times(c, Power(x, Times(C2, n)))),
                              p)),
                      x), x),
                  And(FreeQ(List(a, b, c, d, m, n), x), EqQ($s("n2"), Times(C2,
                      n)), PolyQ($s("§pq"),
                          x),
                      ILtQ(Plus(p, C1), C0)))),
          IIntegrate(1772,
              Integrate(
                  Times($p("§pq"), Power(Times(d_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_DEFAULT)),
                          Times(c_DEFAULT, Power(x_, $p("n2", true)))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times($s("§pq"), Power(Times(d, x), m),
                          Power(Plus(a, Times(b, Power(x, n)), Times(c, Power(x, Times(C2, n)))),
                              p)),
                      x),
                  And(FreeQ(List(a, b, c, d, m, n, p), x), EqQ($s("n2"), Times(C2, n)),
                      Or(PolyQ($s("§pq"), x), PolyQ($s("§pq"), Power(x, n)))))),
          IIntegrate(1773,
              Integrate(
                  Times($p("§pq"), Power(u_, m_DEFAULT),
                      Power(
                          Plus(
                              a_, Times(c_DEFAULT, Power(v_,
                                  $p("n2", true))),
                              Times(b_DEFAULT, Power(v_, n_))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Times(Power(u, m), Power(Times(Coefficient(v, x, C1), Power(v, m)), CN1)),
                      Subst(Integrate(Times(Power(x, m), SubstFor(v, $s("§pq"), x),
                          Power(Plus(a, Times(b, Power(x, n)), Times(c, Power(x, Times(C2, n)))),
                              p)),
                          x), x, v),
                      x),
                  And(FreeQ(List(a, b, c, m, n, p), x), EqQ($s("n2"), Times(C2,
                      n)), LinearPairQ(u, v,
                          x),
                      PolyQ($s("§pq"), Power(v, n))))),
          IIntegrate(1774,
              Integrate(
                  Times($p("§pq"),
                      Power(
                          Plus(
                              a_, Times(b_DEFAULT, Power(x_, n_DEFAULT)),
                              Times(c_DEFAULT, Power(x_, $p("n2", true)))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(Times($s("§pq"),
                          Power(Plus(a, Times(b, Power(x, n)), Times(c, Power(x, Times(C2, n)))),
                              p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, n), x), EqQ($s("n2"), Times(C2,
                      n)), PolyQ($s("§pq"),
                          x),
                      IGtQ(p, C0)))),
          IIntegrate(1775,
              Integrate(
                  Times(
                      Power(
                          Plus(a_, Times(b_DEFAULT, Power(x_, n_DEFAULT)),
                              Times(c_DEFAULT, Power(x_, $p("n2", true)))),
                          p_DEFAULT),
                      Plus(
                          d_, Times(e_DEFAULT, Power(x_,
                              n_DEFAULT)),
                          Times(f_DEFAULT, Power(x_, $p("n2", true))))),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(d, x,
                          Power(Plus(a, Times(b, Power(x, n)), Times(c, Power(x, Times(C2, n)))),
                              Plus(p, C1)),
                          Power(a, CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, n, p), x), EqQ($s("n2"), Times(C2, n)),
                      EqQ(Subtract(Times(a, e),
                          Times(b, d, Plus(Times(n, Plus(p, C1)), C1))), C0),
                      EqQ(Subtract(Times(a, f), Times(c, d, Plus(Times(C2, n, Plus(p, C1)), C1))),
                          C0)))),
          IIntegrate(1776,
              Integrate(
                  Times(
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_DEFAULT)),
                          Times(c_DEFAULT, Power(x_, $p("n2", true)))), p_DEFAULT),
                      Plus(d_, Times(f_DEFAULT, Power(x_, $p("n2", true))))),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(d, x,
                          Power(
                              Plus(a, Times(b, Power(x,
                                  n)), Times(c,
                                      Power(x, Times(C2, n)))),
                              Plus(p, C1)),
                          Power(a, CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, f, n, p), x), EqQ($s("n2"), Times(C2, n)),
                      EqQ(Plus(Times(n, Plus(p, C1)),
                          C1), C0),
                      EqQ(Plus(Times(c, d), Times(a, f)), C0)))),
          IIntegrate(1777,
              Integrate(
                  Times($p("§pq"),
                      Power(
                          Plus(
                              a_, Times(b_DEFAULT, Power(x_, n_DEFAULT)), Times(c_DEFAULT,
                                  Power(x_, $p("n2", true)))),
                          p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(
                              Plus(a, Times(b, Power(x, n)), Times(c, Power(x, Times(C2, n)))),
                              FracPart(p)),
                          Power(
                              Times(Power(Times(C4, c), IntPart(p)),
                                  Power(Plus(b, Times(C2, c, Power(x, n))),
                                      Times(C2, FracPart(p)))),
                              CN1)),
                      Integrate(
                          Times($s("§pq"),
                              Power(Plus(b, Times(C2, c, Power(x, n))), Times(C2, p))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, n, p), x), EqQ($s("n2"), Times(C2, n)),
                      PolyQ($s("§pq"), x), EqQ(Subtract(Sqr(b), Times(C4, a, c)),
                          C0),
                      Not(IntegerQ(Times(C2, p)))))),
          IIntegrate(1778,
              Integrate(
                  Times($p("§pq"),
                      Power(
                          Plus(a_, Times(b_DEFAULT, Power(x_, n_DEFAULT)),
                              Times(c_DEFAULT, Power(x_, $p("n2", true)))),
                          p_)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          x, PolynomialQuotient($s(
                              "§pq"), x, x),
                          Power(Plus(a, Times(b, Power(x, n)), Times(c, Power(x, Times(C2, n)))),
                              p)),
                      x),
                  And(FreeQ(List(a, b, c, n, p), x), EqQ($s("n2"), Times(C2, n)),
                      PolyQ($s("§pq"), x), EqQ(Coeff($s("§pq"), x, C0), C0),
                      Not(MatchQ($s("§pq"), Condition(Times(Power(x, m_DEFAULT), u_DEFAULT),
                          IntegerQ(m))))))),
          IIntegrate(1779,
              Integrate(
                  Times(
                      Power(
                          Plus(
                              a_, Times(c_DEFAULT, Power(x_,
                                  $p("n2", true))),
                              Times(b_DEFAULT, Power(x_, n_))),
                          p_DEFAULT),
                      Plus(d_, Times(f_DEFAULT, Power(x_, $p("n2", true))),
                          Times(g_DEFAULT, Power(x_,
                              $p("n3", true))),
                          Times(e_DEFAULT, Power(x_, n_)))),
                  x_Symbol),
              Condition(
                  Simp(Times(x,
                      Plus(Times(a, d, Plus(n, C1)),
                          Times(Subtract(Times(a, e), Times(b, d, Plus(Times(n, Plus(p, C1)), C1))),
                              Power(x, n))),
                      Power(
                          Plus(a, Times(b, Power(x, n)), Times(c,
                              Power(x, Times(C2, n)))),
                          Plus(p, C1)),
                      Power(Times(Sqr(a), Plus(n, C1)), CN1)), x),
                  And(FreeQ(List(a, b, c, d, e, f, g, n, p), x), EqQ($s("n2"), Times(C2, n)),
                      EqQ($s("n3"), Times(C3, n)), NeQ(Subtract(Sqr(b),
                          Times(C4, a, c)), C0),
                      EqQ(Subtract(Times(Sqr(a), g, Plus(n, C1)),
                          Times(c, Plus(Times(n, Plus(Times(C2, p), C3)), C1), Subtract(Times(a, e),
                              Times(b, d, Plus(Times(n, Plus(p, C1)), C1))))),
                          C0),
                      EqQ(Subtract(
                          Subtract(
                              Times(Sqr(a), f, Plus(n, C1)),
                              Times(a, c, d, Plus(n, C1), Plus(Times(C2, n, Plus(p, C1)), C1))),
                          Times(b, Plus(Times(n, Plus(p, C2)), C1), Subtract(Times(a, e),
                              Times(b, d, Plus(Times(n, Plus(p, C1)), C1))))),
                          C0)))),
          IIntegrate(1780,
              Integrate(Times(
                  Plus(d_, Times(f_DEFAULT, Power(x_, $p("n2", true))),
                      Times(g_DEFAULT, Power(x_, $p("n3", true)))),
                  Power(Plus(a_, Times(c_DEFAULT, Power(x_, $p("n2", true))),
                      Times(b_DEFAULT, Power(x_, n_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Simp(Times(d, x,
                      Subtract(Times(a, Plus(n, C1)),
                          Times(b, Plus(Times(n, Plus(p, C1)), C1), Power(x, n))),
                      Power(Plus(a, Times(b, Power(x, n)), Times(c, Power(x, Times(C2, n)))),
                          Plus(p, C1)),
                      Power(Times(Sqr(a), Plus(n, C1)), CN1)), x),
                  And(FreeQ(List(a, b, c, d, f, g, n, p), x), EqQ($s("n2"), Times(C2, n)),
                      EqQ($s("n3"), Times(C3, n)), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      EqQ(Plus(Times(Sqr(a), g, Plus(n, C1)),
                          Times(c, b, d, Plus(Times(n, Plus(Times(C2, p), C3)), C1),
                              Plus(Times(n, Plus(p, C1)), C1))),
                          C0),
                      EqQ(Plus(Times(Sqr(a), f, Plus(n, C1)),
                          Times(CN1, a, c, d, Plus(n, C1), Plus(Times(C2, n, Plus(p, C1)), C1)),
                          Times(Sqr(b), d, Plus(Times(n, Plus(p, C2)), C1),
                              Plus(Times(n, Plus(p, C1)), C1))),
                          C0)))));
}
