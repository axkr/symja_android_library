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
class IntRules239 {
  public static IAST RULES =
      List(
          IIntegrate(4781,
              Integrate(
                  Times(
                      Log(Times(h_DEFAULT,
                          Power(Plus(f_DEFAULT, Times(g_DEFAULT, x_)), m_DEFAULT))),
                      Power(
                          Plus(a_DEFAULT,
                              Times(ArcSin(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          n_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(d, IntPart(p)), Power(Plus(d, Times(e, Sqr(x))), FracPart(
                              p)),
                          Power(Power(Subtract(C1, Times(Sqr(c), Sqr(x))), FracPart(p)), CN1)),
                      Integrate(
                          Times(Log(Times(h, Power(Plus(f, Times(g, x)), m))),
                              Power(Subtract(C1, Times(Sqr(c), Sqr(x))), p), Power(
                                  Plus(a, Times(b, ArcSin(Times(c, x)))), n)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, h, m, n), x),
                      EqQ(Plus(Times(Sqr(c), d), e),
                          C0),
                      IntegerQ(Subtract(p, C1D2)), Not(GtQ(d, C0))))),
          IIntegrate(4782,
              Integrate(
                  Times(
                      Log(Times(h_DEFAULT,
                          Power(Plus(f_DEFAULT, Times(g_DEFAULT, x_)), m_DEFAULT))),
                      Power(
                          Plus(a_DEFAULT,
                              Times(ArcCos(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          n_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(d, IntPart(p)), Power(Plus(d, Times(e, Sqr(x))), FracPart(p)),
                          Power(Power(Subtract(C1, Times(Sqr(c), Sqr(x))), FracPart(p)), CN1)),
                      Integrate(
                          Times(Log(Times(h, Power(Plus(f, Times(g, x)), m))),
                              Power(Subtract(C1, Times(Sqr(c), Sqr(x))), p),
                              Power(Plus(a, Times(b, ArcCos(Times(c, x)))), n)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, h, m, n), x),
                      EqQ(Plus(Times(Sqr(c), d), e), C0), IntegerQ(Subtract(p,
                          C1D2)),
                      Not(GtQ(d, C0))))),
          IIntegrate(4783,
              Integrate(
                  Times(Plus(a_DEFAULT, Times(ArcSin(Times(c_DEFAULT, x_)), b_DEFAULT)),
                      Power(Plus(d_,
                          Times(e_DEFAULT, x_)), m_),
                      Power(Plus(f_, Times(g_DEFAULT, x_)), m_)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(u,
                          IntHide(
                              Times(Power(Plus(d, Times(e, x)), m), Power(Plus(f, Times(g, x)), m)),
                              x))),
                      Subtract(
                          Dist(Plus(a,
                              Times(b, ArcSin(Times(c, x)))), u, x),
                          Dist(Times(b, c),
                              Integrate(Dist(Power(Subtract(C1, Times(Sqr(c), Sqr(x))), CN1D2), u,
                                  x), x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, f, g), x), ILtQ(Plus(m, C1D2), C0)))),
          IIntegrate(4784,
              Integrate(
                  Times(Plus(a_DEFAULT, Times(ArcCos(Times(c_DEFAULT, x_)), b_DEFAULT)),
                      Power(Plus(d_, Times(e_DEFAULT, x_)),
                          m_),
                      Power(Plus(f_, Times(g_DEFAULT, x_)), m_)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(u,
                              IntHide(Times(Power(Plus(d, Times(e, x)), m),
                                  Power(Plus(f, Times(g, x)), m)), x))),
                      Plus(
                          Dist(Plus(a,
                              Times(b, ArcCos(Times(c, x)))), u, x),
                          Dist(Times(b, c),
                              Integrate(
                                  Dist(Power(Subtract(C1, Times(Sqr(c), Sqr(x))), CN1D2), u, x), x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, f, g), x), ILtQ(Plus(m, C1D2), C0)))),
          IIntegrate(4785,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcSin(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          n_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, x_)), m_DEFAULT), Power(
                          Plus(f_, Times(g_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(Times(Power(Plus(d, Times(e, x)), m),
                          Power(Plus(f, Times(g, x)), m), Power(
                              Plus(a, Times(b, ArcSin(Times(c, x)))), n)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, n), x), IntegerQ(m)))),
          IIntegrate(4786,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCos(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          n_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, x_)), m_DEFAULT), Power(
                          Plus(f_, Times(g_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Times(
                              Power(Plus(d, Times(e,
                                  x)), m),
                              Power(Plus(f, Times(g, x)), m), Power(
                                  Plus(a, Times(b, ArcCos(Times(c, x)))), n)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, n), x), IntegerQ(m)))),
          IIntegrate(4787,
              Integrate(
                  Times(Plus(a_DEFAULT, Times(ArcSin(Times(c_DEFAULT, x_)), b_DEFAULT)),
                      u_),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(v,
                          IntHide(u, x))),
                      Condition(
                          Subtract(
                              Dist(Plus(a,
                                  Times(b, ArcSin(Times(c, x)))), v, x),
                              Dist(Times(b, c),
                                  Integrate(SimplifyIntegrand(
                                      Times(v, Power(Subtract(C1, Times(Sqr(c), Sqr(x))), CN1D2)),
                                      x), x),
                                  x)),
                          InverseFunctionFreeQ(v, x))),
                  FreeQ(List(a, b, c), x))),
          IIntegrate(4788,
              Integrate(
                  Times(Plus(a_DEFAULT, Times(ArcCos(Times(c_DEFAULT, x_)), b_DEFAULT)),
                      u_),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(v,
                          IntHide(u, x))),
                      Condition(
                          Plus(Dist(Plus(a, Times(b, ArcCos(Times(c, x)))), v, x),
                              Dist(Times(b, c),
                                  Integrate(SimplifyIntegrand(
                                      Times(v, Power(Subtract(C1, Times(Sqr(c), Sqr(x))), CN1D2)),
                                      x), x),
                                  x)),
                          InverseFunctionFreeQ(v, x))),
                  FreeQ(List(a, b, c), x))),
          IIntegrate(4789,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(ArcSin(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          n_DEFAULT),
                      $p("§px"), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  With(List(Set(u,
                      ExpandIntegrand(Times($s("§px"), Power(Plus(d, Times(e, Sqr(x))), p),
                          Power(Plus(a, Times(b, ArcSin(Times(c, x)))), n)), x))),
                      Condition(Integrate(u, x), SumQ(u))),
                  And(FreeQ(List(a, b, c, d, e, n), x), PolynomialQ($s("§px"), x), EqQ(
                      Plus(Times(Sqr(c), d), e), C0), IntegerQ(Subtract(p, C1D2))))),
          IIntegrate(4790,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(ArcCos(Times(c_DEFAULT, x_)),
                          b_DEFAULT)), n_DEFAULT),
                      $p("§px"), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(u,
                              ExpandIntegrand(
                                  Times($s("§px"), Power(Plus(d, Times(e, Sqr(x))), p),
                                      Power(Plus(a, Times(b, ArcCos(Times(c, x)))), n)),
                                  x))),
                      Condition(Integrate(u, x), SumQ(u))),
                  And(FreeQ(List(a, b, c, d, e, n), x), PolynomialQ($s("§px"), x), EqQ(
                      Plus(Times(Sqr(c), d), e), C0), IntegerQ(Subtract(p, C1D2))))),
          IIntegrate(4791,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcSin(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          n_DEFAULT),
                      $p("§px", true),
                      Power(
                          Plus(f_,
                              Times(g_DEFAULT, Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_))),
                          m_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(u,
                              ExpandIntegrand(
                                  Times($s("§px"),
                                      Power(Plus(f, Times(g, Power(Plus(d, Times(e, Sqr(x))), p))),
                                          m),
                                      Power(Plus(a, Times(b, ArcSin(Times(c, x)))), n)),
                                  x))),
                      Condition(Integrate(u, x), SumQ(u))),
                  And(FreeQ(List(a, b, c, d, e, f, g), x), PolynomialQ($s("§px"), x),
                      EqQ(Plus(Times(Sqr(c), d), e), C0), IGtQ(Plus(p,
                          C1D2), C0),
                      IntegersQ(m, n)))),
          IIntegrate(4792,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCos(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          n_DEFAULT),
                      $p("§px", true),
                      Power(
                          Plus(f_,
                              Times(g_DEFAULT, Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_))),
                          m_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(u,
                              ExpandIntegrand(
                                  Times($s("§px"),
                                      Power(Plus(f, Times(g, Power(Plus(d, Times(e, Sqr(x))), p))),
                                          m),
                                      Power(Plus(a, Times(b, ArcCos(Times(c, x)))), n)),
                                  x))),
                      Condition(Integrate(u, x), SumQ(u))),
                  And(FreeQ(List(a, b, c, d, e, f, g), x), PolynomialQ($s("§px"), x),
                      EqQ(Plus(Times(Sqr(c), d), e), C0), IGtQ(Plus(p,
                          C1D2), C0),
                      IntegersQ(m, n)))),
          IIntegrate(4793,
              Integrate(Times(Power(ArcSin(
                  Times(c_DEFAULT, x_)), n_DEFAULT), $p(
                      "§rfx")),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(u, ExpandIntegrand(Power(ArcSin(Times(c, x)), n), $s("§rfx"), x))),
                      Condition(Integrate(u, x), SumQ(u))),
                  And(FreeQ(c, x), RationalFunctionQ($s("§rfx"), x), IGtQ(n, C0)))),
          IIntegrate(4794,
              Integrate(Times(Power(ArcCos(
                  Times(c_DEFAULT, x_)), n_DEFAULT), $p(
                      "§rfx")),
                  x_Symbol),
              Condition(
                  With(List(Set(u,
                      ExpandIntegrand(Power(ArcCos(Times(c, x)), n), $s("§rfx"), x))), Condition(
                          Integrate(u, x), SumQ(u))),
                  And(FreeQ(c, x), RationalFunctionQ($s("§rfx"), x), IGtQ(n, C0)))),
          IIntegrate(4795,
              Integrate(
                  Times(
                      Power(Plus(Times(ArcSin(Times(c_DEFAULT, x_)), b_DEFAULT), a_),
                          n_DEFAULT),
                      $p("§rfx")),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(Times($s("§rfx"),
                          Power(Plus(a, Times(b, ArcSin(Times(c, x)))), n)), x),
                      x),
                  And(FreeQ(List(a, b, c), x), RationalFunctionQ($s("§rfx"), x), IGtQ(n, C0)))),
          IIntegrate(4796,
              Integrate(
                  Times(Power(Plus(Times(ArcCos(Times(c_DEFAULT, x_)), b_DEFAULT), a_), n_DEFAULT),
                      $p("§rfx")),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(Times($s("§rfx"),
                          Power(Plus(a, Times(b, ArcCos(Times(c, x)))), n)), x),
                      x),
                  And(FreeQ(List(a, b, c), x), RationalFunctionQ($s("§rfx"), x), IGtQ(n, C0)))),
          IIntegrate(4797,
              Integrate(
                  Times(
                      Power(ArcSin(Times(c_DEFAULT, x_)), n_DEFAULT), $p("§rfx"), Power(
                          Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(u,
                              ExpandIntegrand(
                                  Times(
                                      Power(Plus(d, Times(e, Sqr(x))), p), Power(
                                          ArcSin(Times(c, x)), n)),
                                  $s("§rfx"), x))),
                      Condition(Integrate(u, x), SumQ(u))),
                  And(FreeQ(List(c, d, e), x), RationalFunctionQ($s("§rfx"), x), IGtQ(n, C0), EqQ(
                      Plus(Times(Sqr(c), d), e), C0), IntegerQ(Subtract(p, C1D2))))),
          IIntegrate(4798,
              Integrate(
                  Times(
                      Power(ArcCos(Times(c_DEFAULT, x_)), n_DEFAULT), $p("§rfx"), Power(
                          Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(u,
                              ExpandIntegrand(
                                  Times(Power(Plus(d, Times(e, Sqr(x))), p),
                                      Power(ArcCos(Times(c, x)), n)),
                                  $s("§rfx"), x))),
                      Condition(Integrate(u, x), SumQ(u))),
                  And(FreeQ(List(c, d, e), x), RationalFunctionQ($s("§rfx"), x), IGtQ(n,
                      C0), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      IntegerQ(Subtract(p, C1D2))))),
          IIntegrate(4799,
              Integrate(
                  Times(
                      Power(Plus(Times(ArcSin(Times(c_DEFAULT, x_)), b_DEFAULT),
                          a_), n_DEFAULT),
                      $p("§rfx"), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Power(Plus(d, Times(e, Sqr(x))), p), Times($s("§rfx"),
                              Power(Plus(a, Times(b, ArcSin(Times(c, x)))), n)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e), x), RationalFunctionQ($s("§rfx"),
                      x), IGtQ(n, C0), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      IntegerQ(Subtract(p, C1D2))))),
          IIntegrate(4800,
              Integrate(
                  Times(Power(Plus(Times(ArcCos(Times(c_DEFAULT, x_)), b_DEFAULT), a_), n_DEFAULT),
                      $p("§rfx"), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Integrate(ExpandIntegrand(Power(Plus(d, Times(e, Sqr(x))), p),
                      Times($s("§rfx"), Power(Plus(a, Times(b, ArcCos(Times(c, x)))), n)), x), x),
                  And(FreeQ(List(a, b, c, d, e), x), RationalFunctionQ($s("§rfx"), x), IGtQ(n, C0),
                      EqQ(Plus(Times(Sqr(c), d), e), C0), IntegerQ(Subtract(p, C1D2))))));
}
