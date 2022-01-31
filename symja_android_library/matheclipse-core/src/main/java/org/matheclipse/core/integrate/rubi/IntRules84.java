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
class IntRules84 {
  public static IAST RULES = List(
      IIntegrate(1681,
          Integrate(Times(
              $p("§px"), Power(x_, m_), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1),
              Power(Plus(a_, Times(b_DEFAULT, Sqr(x_)), Times(c_DEFAULT, Power(x_, C4))), CN1D2)),
              x_Symbol),
          Condition(
              With(
                  List(
                      Set(ASymbol, Coeff($s("§px"), x, C0)), Set(BSymbol, Coeff($s("§px"), x,
                          C2)),
                      Set(CSymbol, Coeff($s("§px"), x, C4))),
                  Subtract(
                      Simp(
                          Times(CSymbol, Power(x, Subtract(m, C1)),
                              Sqrt(Plus(a, Times(b, Sqr(x)),
                                  Times(c, Power(x, C4)))),
                              Power(Times(c, e, Plus(m, C1)), CN1)),
                          x),
                      Dist(Power(Times(c, e, Plus(m, C1)), CN1), Integrate(
                          Times(
                              Power(x, Subtract(m,
                                  C2)),
                              Power(
                                  Times(
                                      Plus(d, Times(e, Sqr(
                                          x))),
                                      Sqrt(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))))),
                                  CN1),
                              Simp(
                                  Subtract(
                                      Subtract(Times(a, CSymbol, d, Subtract(m, C1)),
                                          Times(Subtract(Times(ASymbol, c, e, Plus(m, C1)),
                                              Times(CSymbol,
                                                  Plus(Times(a, e, Subtract(m, C1)),
                                                      Times(b, d, m)))),
                                              Sqr(x))),
                                      Times(
                                          Subtract(Times(BSymbol, c, e, Plus(m, C1)),
                                              Times(CSymbol,
                                                  Plus(Times(b, e, m), Times(c, d, Plus(m, C1))))),
                                          Power(x, C4))),
                                  x)),
                          x), x))),
              And(FreeQ(List(a, b, c, d, e), x), PolyQ($s("§px"), Sqr(x), C2), NeQ(Subtract(Sqr(b),
                  Times(C4, a, c)), C0), IGtQ(Times(C1D2, m),
                      C0)))),
      IIntegrate(1682,
          Integrate(
              Times(
                  $p("§px"), Power(x_, m_), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1), Power(
                      Plus(a_, Times(c_DEFAULT, Power(x_, C4))), CN1D2)),
              x_Symbol),
          Condition(
              With(
                  List(
                      Set(ASymbol, Coeff($s(
                          "§px"), x, C0)),
                      Set(BSymbol, Coeff($s("§px"), x, C2)), Set(CSymbol, Coeff($s("§px"), x, C4))),
                  Subtract(
                      Simp(
                          Times(CSymbol, Power(x, Subtract(m, C1)),
                              Sqrt(Plus(a, Times(c, Power(x, C4)))),
                              Power(Times(c, e, Plus(m, C1)), CN1)),
                          x),
                      Dist(Power(Times(c, e, Plus(m, C1)), CN1), Integrate(Times(
                          Power(x, Subtract(m, C2)),
                          Power(Times(Plus(d, Times(e, Sqr(x))),
                              Sqrt(Plus(a, Times(c, Power(x, C4))))), CN1),
                          Simp(
                              Subtract(
                                  Subtract(Times(a, CSymbol, d, Subtract(m, C1)),
                                      Times(Subtract(Times(ASymbol, c, e, Plus(m, C1)),
                                          Times(CSymbol, a, e, Subtract(m, C1))), Sqr(x))),
                                  Times(Subtract(Times(BSymbol, c, e, Plus(m, C1)),
                                      Times(CSymbol, c, d, Plus(m, C1))), Power(x, C4))),
                              x)),
                          x), x))),
              And(FreeQ(List(a, c, d, e), x), PolyQ($s("§px"), Sqr(x), C2),
                  IGtQ(Times(C1D2, m), C0)))),
      IIntegrate(1683,
          Integrate(Times($p("§px"), Power(x_, m_), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1),
              Power(Plus(a_, Times(b_DEFAULT, Sqr(x_)), Times(c_DEFAULT, Power(x_, C4))), CN1D2)),
              x_Symbol),
          Condition(
              With(
                  List(Set(ASymbol, Coeff($s("§px"), x, C0)), Set(BSymbol, Coeff($s("§px"), x, C2)),
                      Set(CSymbol, Coeff($s("§px"), x, C4))),
                  Plus(Simp(Times(ASymbol, Power(x, Plus(m, C1)),
                      Sqrt(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4)))), Power(
                          Times(a, d, Plus(m, C1)), CN1)),
                      x),
                      Dist(
                          Power(Times(a, d,
                              Plus(m, C1)), CN1),
                          Integrate(
                              Times(
                                  Power(x, Plus(m,
                                      C2)),
                                  Power(
                                      Times(
                                          Plus(d, Times(e,
                                              Sqr(x))),
                                          Sqrt(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))))),
                                      CN1),
                                  Simp(Subtract(
                                      Plus(
                                          Times(a, BSymbol, d, Plus(m,
                                              C1)),
                                          Times(CN1, ASymbol,
                                              Plus(Times(a, e, Plus(m, C1)),
                                                  Times(b, d, Plus(m, C2)))),
                                          Times(Subtract(Times(a, CSymbol, d, Plus(m, C1)),
                                              Times(ASymbol,
                                                  Plus(Times(b, e, Plus(m, C2)),
                                                      Times(c, d, Plus(m, C3))))),
                                              Sqr(x))),
                                      Times(ASymbol, c, e, Plus(m, C3), Power(x, C4))), x)),
                              x),
                          x))),
              And(FreeQ(List(a, b, c, d, e), x), PolyQ($s("§px"), Sqr(x), C2), NeQ(
                  Subtract(Sqr(b), Times(C4, a, c)), C0), ILtQ(Times(C1D2, m), C0)))),
      IIntegrate(1684,
          Integrate(
              Times(
                  $p("§px"), Power(x_, m_), Power(Plus(d_,
                      Times(e_DEFAULT, Sqr(x_))), CN1),
                  Power(Plus(a_, Times(c_DEFAULT, Power(x_, C4))), CN1D2)),
              x_Symbol),
          Condition(
              With(
                  List(
                      Set(ASymbol, Coeff($s(
                          "§px"), x, C0)),
                      Set(BSymbol, Coeff($s("§px"), x, C2)), Set(CSymbol, Coeff($s("§px"), x, C4))),
                  Plus(
                      Simp(Times(ASymbol, Power(x, Plus(m, C1)),
                          Sqrt(Plus(a, Times(c, Power(x, C4)))),
                          Power(Times(a, d, Plus(m, C1)), CN1)), x),
                      Dist(Power(Times(a, d, Plus(m, C1)), CN1), Integrate(
                          Times(Power(x, Plus(m, C2)),
                              Power(Times(Plus(d, Times(e, Sqr(x))),
                                  Sqrt(Plus(a, Times(c, Power(x, C4))))), CN1),
                              Simp(Subtract(
                                  Plus(Times(a, BSymbol, d, Plus(m, C1)),
                                      Times(CN1, ASymbol, a, e, Plus(m, C1)),
                                      Times(Subtract(Times(a, CSymbol, d, Plus(m, C1)),
                                          Times(ASymbol, c, d, Plus(m, C3))), Sqr(x))),
                                  Times(ASymbol, c, e, Plus(m, C3), Power(x, C4))), x)),
                          x), x))),
              And(FreeQ(List(a, c, d, e), x), PolyQ($s("§px"), Sqr(x), C2),
                  ILtQ(Times(C1D2, m), C0)))),
      IIntegrate(1685,
          Integrate(Times($p("§px"), x_, Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_DEFAULT),
              Power(Plus(a_, Times(b_DEFAULT, Sqr(x_)), Times(c_DEFAULT, Power(x_, C4))),
                  p_DEFAULT)),
              x_Symbol),
          Condition(
              Dist(C1D2,
                  Subst(Integrate(
                      Times(ReplaceAll($s("§px"), Rule(x, Sqrt(x))), Power(Plus(d, Times(e, x)), q),
                          Power(Plus(a, Times(b, x), Times(c, Sqr(x))), p)),
                      x), x, Sqr(x)),
                  x),
              And(FreeQ(List(a, b, c, d, e, p, q), x), PolyQ($s("§px"), Sqr(x))))),
      IIntegrate(1686,
          Integrate(
              Times($p("§pr"), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_DEFAULT),
                  Power(Plus(a_, Times(b_DEFAULT, Sqr(x_)), Times(c_DEFAULT, Power(x_, C4))), p_)),
              x_Symbol),
          Condition(
              Integrate(
                  Times(x, PolynomialQuotient($s("§pr"), x, x), Power(Plus(d, Times(e, Sqr(x))), q),
                      Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), p)),
                  x),
              And(FreeQ(List(a, b, c, d, e, p, q), x), PolyQ($s("§pr"), x),
                  EqQ(PolynomialRemainder($s("§pr"), x, x), C0),
                  Not(MatchQ($s("§pr"), Condition(Times(Power(x, m_DEFAULT), u_DEFAULT),
                      IntegerQ(m))))))),
      IIntegrate(1687,
          Integrate(
              Times(
                  $p("§pr"), Power(Plus(d_,
                      Times(e_DEFAULT, Sqr(x_))), q_DEFAULT),
                  Power(Plus(a_, Times(b_DEFAULT, Sqr(x_)), Times(c_DEFAULT, Power(x_, C4))), p_)),
              x_Symbol),
          Condition(
              Module(List(Set(r, Expon($s("§pr"), x)), k),
                  Plus(
                      Integrate(Times(
                          Sum(Times(Coeff($s("§pr"), x, Times(C2, k)), Power(x, Times(C2, k))),
                              List(k, C0, Times(C1D2, r))),
                          Power(Plus(d, Times(e, Sqr(x))), q), Power(
                              Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), p)),
                          x),
                      Integrate(
                          Times(x,
                              Sum(Times(Coeff($s("§pr"), x, Plus(Times(C2, k), C1)),
                                  Power(x, Times(C2, k))),
                                  List(k, C0, Times(C1D2, Subtract(r, C1)))),
                              Power(Plus(d, Times(e, Sqr(x))), q),
                              Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), p)),
                          x))),
              And(FreeQ(List(a, b, c, d, e, p, q), x), PolyQ($s("§pr"), x),
                  Not(PolyQ($s("§pr"), Sqr(x)))))),
      IIntegrate(1688,
          Integrate(
              Times($p("§px"), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_DEFAULT),
                  Power(
                      Plus(a_, Times(b_DEFAULT, Sqr(x_)),
                          Times(c_DEFAULT, Power(x_, C4))),
                      p_DEFAULT)),
              x_Symbol),
          Condition(
              Integrate(
                  Times(
                      $s("§px"), Power(Plus(d, Times(e,
                          Sqr(x))), Plus(p,
                              q)),
                      Power(Plus(Times(a, Power(d, CN1)), Times(c, Sqr(x), Power(e, CN1))), p)),
                  x),
              And(FreeQ(List(a, b, c, d, e, q), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                  EqQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))), C0),
                  IntegerQ(p),
                  Or(PolyQ($s("§px"), Sqr(x)),
                      MatchQ($s("§px"),
                          Condition(Power(Plus(f_, Times(g_DEFAULT, Sqr(x))), r_DEFAULT),
                              FreeQ(List(f, g, r), x))))))),
      IIntegrate(1689,
          Integrate(
              Times(
                  $p("§px"), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_DEFAULT), Power(
                      Plus(a_, Times(c_DEFAULT, Power(x_, C4))), p_DEFAULT)),
              x_Symbol),
          Condition(
              Integrate(
                  Times(
                      $s("§px"), Power(Plus(d, Times(e, Sqr(x))), Plus(p,
                          q)),
                      Power(Plus(Times(a, Power(d, CN1)), Times(c, Sqr(x), Power(e, CN1))), p)),
                  x),
              And(FreeQ(List(a, c, d, e, q), x), EqQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), C0),
                  IntegerQ(p), Or(
                      PolyQ($s("§px"), Sqr(
                          x)),
                      MatchQ($s("§px"),
                          Condition(
                              Power(Plus(f_, Times(g_DEFAULT, Sqr(x))),
                                  r_DEFAULT),
                              FreeQ(List(f, g, r), x))))))),
      IIntegrate(1690,
          Integrate(
              Times(
                  $p("§px"), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_DEFAULT), Power(Plus(a_,
                      Times(b_DEFAULT, Sqr(x_)), Times(c_DEFAULT, Power(x_, C4))),
                      p_)),
              x_Symbol),
          Condition(Dist(Times(
              Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), FracPart(p)),
              Power(Times(Power(Plus(d, Times(e, Sqr(x))), FracPart(p)),
                  Power(Plus(Times(a, Power(d, CN1)), Times(c, Sqr(x), Power(e, CN1))),
                      FracPart(p))),
                  CN1)),
              Integrate(
                  Times($s("§px"), Power(Plus(d, Times(e, Sqr(x))), Plus(p, q)), Power(Plus(
                      Times(a, Power(d, CN1)), Times(c, Sqr(x), Power(e, CN1))), p)),
                  x),
              x),
              And(FreeQ(List(a, b, c, d, e, p, q), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                  EqQ(Plus(Times(c, Sqr(
                      d)), Times(CN1, b, d,
                          e),
                      Times(a, Sqr(e))), C0),
                  Not(IntegerQ(
                      p)),
                  Or(PolyQ($s("§px"), Sqr(x)),
                      MatchQ($s("§px"),
                          Condition(
                              Power(Plus(f_, Times(g_DEFAULT, Sqr(x))),
                                  r_DEFAULT),
                              FreeQ(List(f, g, r), x))))))),
      IIntegrate(1691,
          Integrate(
              Times(
                  $p("§px"), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_DEFAULT), Power(
                      Plus(a_, Times(c_DEFAULT, Power(x_, C4))), p_)),
              x_Symbol),
          Condition(Dist(
              Times(Power(Plus(a, Times(c, Power(x, C4))), FracPart(p)),
                  Power(Times(Power(Plus(d, Times(e, Sqr(x))), FracPart(p)),
                      Power(Plus(Times(a, Power(d, CN1)), Times(c, Sqr(x), Power(e, CN1))),
                          FracPart(p))),
                      CN1)),
              Integrate(
                  Times(
                      $s("§px"), Power(Plus(d, Times(e, Sqr(x))), Plus(p, q)),
                      Power(Plus(Times(a, Power(d, CN1)), Times(c, Sqr(x), Power(e, CN1))), p)),
                  x),
              x),
              And(FreeQ(List(a, c, d, e, p, q), x),
                  EqQ(Plus(Times(c, Sqr(d)), Times(a,
                      Sqr(e))), C0),
                  Not(IntegerQ(
                      p)),
                  Or(PolyQ($s("§px"), Sqr(x)),
                      MatchQ($s("§px"),
                          Condition(
                              Power(Plus(f_,
                                  Times(g_DEFAULT, Sqr(x))), r_DEFAULT),
                              FreeQ(List(f, g, r), x))))))),
      IIntegrate(1692,
          Integrate(
              Times($p("§px"), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_DEFAULT),
                  Power(
                      Plus(a_, Times(b_DEFAULT, Sqr(x_)),
                          Times(c_DEFAULT, Power(x_, C4))),
                      p_DEFAULT)),
              x_Symbol),
          Condition(
              Integrate(
                  ExpandIntegrand(
                      Times(
                          $s("§px"), Power(Plus(d, Times(e, Sqr(x))), q), Power(
                              Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), p)),
                      x),
                  x),
              And(FreeQ(List(a, b, c, d, e, q), x), PolyQ($s("§px"), Sqr(x)),
                  NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                  NeQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e),
                      Times(a, Sqr(e))), C0),
                  IntegerQ(p)))),
      IIntegrate(1693,
          Integrate(
              Times(
                  $p("§px"), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_DEFAULT), Power(
                      Plus(a_, Times(c_DEFAULT, Power(x_, C4))), p_DEFAULT)),
              x_Symbol),
          Condition(
              Integrate(
                  ExpandIntegrand(
                      Times($s("§px"), Power(Plus(d, Times(e, Sqr(x))), q),
                          Power(Plus(a, Times(c, Power(x, C4))), p)),
                      x),
                  x),
              And(FreeQ(List(a, c, d, e, q), x), PolyQ($s("§px"), Sqr(
                  x)), NeQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))),
                      C0),
                  IntegerQ(p)))),
      IIntegrate(1694,
          Integrate(
              Times(
                  $p("§p4x"), Power(Plus(d_,
                      Times(e_DEFAULT, Sqr(x_))), q_),
                  Power(Plus(a_, Times(b_DEFAULT, Sqr(
                      x_)), Times(c_DEFAULT,
                          Power(x_, C4))),
                      CN1D2)),
              x_Symbol),
          Condition(
              With(
                  List(
                      Set(ASymbol, Coeff($s(
                          "§p4x"), x, C0)),
                      Set(BSymbol, Coeff($s("§p4x"), x,
                          C2)),
                      Set(CSymbol, Coeff($s("§p4x"), x, C4))),
                  Plus(
                      Simp(
                          Times(CSymbol, x, Power(Plus(d, Times(e, Sqr(x))), q),
                              Sqrt(Plus(a, Times(b, Sqr(x)),
                                  Times(c, Power(x, C4)))),
                              Power(Times(c, Plus(Times(C2, q), C3)), CN1)),
                          x),
                      Dist(
                          Power(Times(c,
                              Plus(Times(C2, q), C3)), CN1),
                          Integrate(Times(Power(Plus(d, Times(e, Sqr(x))), Subtract(q, C1)),
                              Simp(Plus(Times(ASymbol, c, d, Plus(Times(C2, q), C3)),
                                  Times(CN1, a, CSymbol, d),
                                  Times(Subtract(
                                      Times(c, Plus(Times(BSymbol, d), Times(ASymbol, e)),
                                          Plus(Times(C2, q), C3)),
                                      Times(CSymbol,
                                          Plus(Times(C2, b, d), Times(a, e), Times(C2, a, e, q)))),
                                      Sqr(x)),
                                  Times(Subtract(Times(BSymbol, c, e, Plus(Times(C2, q), C3)),
                                      Times(C2, CSymbol,
                                          Plus(Times(b, e), Times(CN1, c, d, q), Times(b, e, q)))),
                                      Power(x, C4))),
                                  x),
                              Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), CN1D2)), x),
                          x))),
              And(FreeQ(List(a, b, c, d, e), x), PolyQ($s("§p4x"), Sqr(x)),
                  EqQ(Expon($s("§p4x"), x), C4), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                  NeQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e),
                      Times(a, Sqr(e))), C0),
                  IGtQ(q, C0)))),
      IIntegrate(1695,
          Integrate(
              Times($p("§p4x"), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_),
                  Power(Plus(a_, Times(c_DEFAULT, Power(x_, C4))), CN1D2)),
              x_Symbol),
          Condition(
              With(
                  List(
                      Set(ASymbol, Coeff($s(
                          "§p4x"), x, C0)),
                      Set(BSymbol, Coeff($s("§p4x"), x, C2)), Set(CSymbol,
                          Coeff($s("§p4x"), x, C4))),
                  Plus(
                      Simp(
                          Times(CSymbol, x, Power(Plus(d, Times(e, Sqr(x))), q),
                              Sqrt(Plus(a,
                                  Times(c, Power(x, C4)))),
                              Power(Times(c, Plus(Times(C2, q), C3)), CN1)),
                          x),
                      Dist(
                          Power(Times(c, Plus(Times(C2, q), C3)), CN1), Integrate(Times(
                              Power(Plus(d, Times(e,
                                  Sqr(x))), Subtract(q,
                                      C1)),
                              Simp(
                                  Plus(Times(ASymbol, c, d, Plus(Times(C2, q), C3)),
                                      Times(CN1, a, CSymbol, d),
                                      Times(
                                          Subtract(
                                              Times(c, Plus(Times(BSymbol, d), Times(ASymbol, e)),
                                                  Plus(Times(C2, q), C3)),
                                              Times(a, CSymbol, e, Plus(Times(C2, q), C1))),
                                          Sqr(x)),
                                      Times(Plus(Times(BSymbol, c, e, Plus(Times(C2, q), C3)),
                                          Times(C2, c, CSymbol, d, q)), Power(x, C4))),
                                  x),
                              Power(Plus(a, Times(c, Power(x, C4))), CN1D2)), x),
                          x))),
              And(FreeQ(List(a, c, d, e), x), PolyQ($s("§p4x"), Sqr(x)),
                  EqQ(Expon($s("§p4x"), x), C4), NeQ(Plus(Times(c, Sqr(d)),
                      Times(a, Sqr(e))), C0),
                  IGtQ(q, C0)))),
      IIntegrate(1696,
          Integrate(
              Times($p("§p4x"), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_),
                  Power(Plus(a_, Times(b_DEFAULT, Sqr(x_)), Times(c_DEFAULT, Power(x_, C4))),
                      CN1D2)),
              x_Symbol),
          Condition(
              With(
                  List(Set(ASymbol, Coeff($s("§p4x"), x, C0)),
                      Set(BSymbol, Coeff($s("§p4x"), x, C2)), Set(CSymbol,
                          Coeff($s("§p4x"), x, C4))),
                  Plus(
                      Negate(
                          Simp(
                              Times(
                                  Plus(Times(CSymbol, Sqr(d)), Times(CN1, BSymbol, d, e),
                                      Times(ASymbol, Sqr(e))),
                                  x, Power(Plus(d, Times(e, Sqr(x))), Plus(q, C1)),
                                  Sqrt(Plus(a, Times(b, Sqr(x)),
                                      Times(c, Power(x, C4)))),
                                  Power(Times(C2, d, Plus(q, C1),
                                      Plus(Times(c, Sqr(d)), Times(CN1, b, d, e),
                                          Times(a, Sqr(e)))),
                                      CN1)),
                              x)),
                      Dist(
                          Power(
                              Times(
                                  C2, d, Plus(q, C1), Plus(Times(c, Sqr(d)), Times(CN1, b, d, e),
                                      Times(a, Sqr(e)))),
                              CN1),
                          Integrate(Times(Power(Plus(d, Times(e, Sqr(x))), Plus(q, C1)), Simp(Plus(
                              Times(a, d, Subtract(Times(CSymbol, d), Times(BSymbol, e))),
                              Times(ASymbol,
                                  Plus(Times(a, Sqr(e), Plus(Times(C2, q), C3)),
                                      Times(C2, d, Subtract(Times(c, d), Times(b, e)),
                                          Plus(q, C1)))),
                              Times(CN1, C2,
                                  Subtract(
                                      Times(Subtract(Times(BSymbol, d), Times(ASymbol, e)),
                                          Subtract(Times(b, e, Plus(q, C2)),
                                              Times(c, d, Plus(q, C1)))),
                                      Times(CSymbol, d,
                                          Plus(Times(b, d), Times(a, e, Plus(q, C1))))),
                                  Sqr(x)),
                              Times(c,
                                  Plus(Times(CSymbol, Sqr(d)), Times(CN1, BSymbol, d, e),
                                      Times(ASymbol, Sqr(e))),
                                  Plus(Times(C2, q), C5), Power(x, C4))),
                              x), Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), CN1D2)),
                              x),
                          x))),
              And(FreeQ(List(a, b, c, d, e), x), PolyQ($s("§p4x"), Sqr(x)),
                  LeQ(Expon($s("§p4x"), x), C4), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                  NeQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e),
                      Times(a, Sqr(e))), C0),
                  ILtQ(q, CN1)))),
      IIntegrate(1697,
          Integrate(
              Times(
                  $p("§p4x"), Power(Plus(d_,
                      Times(e_DEFAULT, Sqr(x_))), q_),
                  Power(Plus(a_, Times(c_DEFAULT, Power(x_, C4))), CN1D2)),
              x_Symbol),
          Condition(
              With(
                  List(
                      Set(ASymbol, Coeff($s(
                          "§p4x"), x, C0)),
                      Set(BSymbol, Coeff($s("§p4x"), x,
                          C2)),
                      Set(CSymbol, Coeff($s("§p4x"), x, C4))),
                  Plus(
                      Negate(
                          Simp(Times(
                              Plus(Times(CSymbol, Sqr(d)), Times(CN1, BSymbol, d, e),
                                  Times(ASymbol, Sqr(e))),
                              x, Power(Plus(d, Times(e, Sqr(x))), Plus(q, C1)),
                              Sqrt(Plus(a, Times(c, Power(x, C4)))),
                              Power(Times(C2, d, Plus(q, C1),
                                  Plus(Times(c, Sqr(d)), Times(a, Sqr(e)))), CN1)),
                              x)),
                      Dist(
                          Power(
                              Times(C2, d, Plus(q, C1),
                                  Plus(Times(c, Sqr(d)), Times(a, Sqr(e)))),
                              CN1),
                          Integrate(Times(Power(Plus(d, Times(e, Sqr(x))), Plus(q, C1)),
                              Simp(Plus(Times(a, d, Subtract(Times(CSymbol, d), Times(BSymbol, e))),
                                  Times(ASymbol,
                                      Plus(Times(a, Sqr(e), Plus(Times(C2, q), C3)),
                                          Times(C2, c, Sqr(d), Plus(q, C1)))),
                                  Times(C2, d,
                                      Plus(Times(BSymbol, c, d), Times(CN1, ASymbol, c, e),
                                          Times(a, CSymbol, e)),
                                      Plus(q, C1), Sqr(x)),
                                  Times(c,
                                      Plus(Times(CSymbol, Sqr(d)), Times(CN1, BSymbol, d, e),
                                          Times(ASymbol, Sqr(e))),
                                      Plus(Times(C2, q), C5), Power(x, C4))),
                                  x),
                              Power(Plus(a, Times(c, Power(x, C4))), CN1D2)), x),
                          x))),
              And(FreeQ(List(a, c, d, e), x), PolyQ($s("§p4x"), Sqr(x)),
                  LeQ(Expon($s(
                      "§p4x"), x), C4),
                  NeQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), C0), ILtQ(q, CN1)))),
      IIntegrate(1698,
          Integrate(
              Times(Plus(A_, Times(B_DEFAULT, Sqr(x_))),
                  Power(Plus(d_,
                      Times(e_DEFAULT, Sqr(x_))), CN1),
                  Power(Plus(a_, Times(b_DEFAULT, Sqr(x_)), Times(c_DEFAULT, Power(x_, C4))),
                      CN1D2)),
              x_Symbol),
          Condition(
              Dist(ASymbol,
                  Subst(
                      Integrate(
                          Power(Subtract(d, Times(Subtract(Times(b, d), Times(C2, a, e)), Sqr(x))),
                              CN1),
                          x),
                      x, Times(x, Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), CN1D2))),
                  x),
              And(FreeQ(List(a, b, c, d, e, ASymbol, BSymbol), x),
                  NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                  NeQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a,
                      Sqr(e))), C0),
                  EqQ(Subtract(Times(c, Sqr(
                      d)), Times(a,
                          Sqr(e))),
                      C0),
                  EqQ(Plus(Times(BSymbol, d), Times(ASymbol, e)), C0)))),
      IIntegrate(1699,
          Integrate(Times(Plus(A_, Times(B_DEFAULT, Sqr(x_))),
              Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1), Power(
                  Plus(a_, Times(c_DEFAULT, Power(x_, C4))), CN1D2)),
              x_Symbol),
          Condition(
              Dist(ASymbol,
                  Subst(
                      Integrate(Power(Plus(d, Times(C2, a, e, Sqr(x))),
                          CN1), x),
                      x, Times(x, Power(Plus(a, Times(c, Power(x, C4))), CN1D2))),
                  x),
              And(FreeQ(List(a, c, d, e, ASymbol, BSymbol), x),
                  NeQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), C0),
                  EqQ(Subtract(Times(c, Sqr(d)), Times(a, Sqr(e))),
                      C0),
                  EqQ(Plus(Times(BSymbol, d), Times(ASymbol, e)), C0)))),
      IIntegrate(1700,
          Integrate(
              Times(Plus(A_, Times(B_DEFAULT, Sqr(x_))),
                  Power(Plus(d_,
                      Times(e_DEFAULT, Sqr(x_))), CN1),
                  Power(Plus(a_, Times(b_DEFAULT, Sqr(x_)), Times(c_DEFAULT, Power(x_, C4))),
                      CN1D2)),
              x_Symbol),
          Condition(
              Subtract(
                  Dist(
                      Times(Plus(Times(BSymbol, d), Times(ASymbol,
                          e)), Power(Times(C2, d, e),
                              CN1)),
                      Integrate(Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), CN1D2), x),
                      x),
                  Dist(
                      Times(Subtract(Times(BSymbol, d), Times(ASymbol, e)),
                          Power(Times(C2, d, e), CN1)),
                      Integrate(Times(Subtract(d, Times(e, Sqr(x))),
                          Power(Times(Plus(d, Times(e, Sqr(x))),
                              Sqrt(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))))), CN1)),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, d, e, ASymbol, BSymbol), x),
                  NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                  NeQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))), C0),
                  EqQ(Subtract(Times(c, Sqr(d)), Times(a, Sqr(e))), C0),
                  NeQ(Plus(Times(BSymbol, d), Times(ASymbol, e)), C0)))));
}
