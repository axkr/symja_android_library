package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.ArcCosh;
import static org.matheclipse.core.expression.F.ArcSinh;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.PolynomialQ;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.g_DEFAULT;
import static org.matheclipse.core.expression.F.h_DEFAULT;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.f;
import static org.matheclipse.core.expression.S.g;
import static org.matheclipse.core.expression.S.h;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FracPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ILtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntHide;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntegersQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.InverseFunctionFreeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RationalFunctionQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SimplifyIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SumQ;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules292 {
  public static IAST RULES =
      List(
          IIntegrate(5841,
              Integrate(
                  Times(
                      Log(Times(h_DEFAULT,
                          Power(Plus(f_DEFAULT, Times(g_DEFAULT, x_)), m_DEFAULT))),
                      Power(
                          Plus(a_DEFAULT,
                              Times(ArcCosh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          n_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Negate(d), IntPart(p)),
                          Power(Plus(d, Times(e, Sqr(x))), FracPart(p)),
                          Power(
                              Times(Power(Plus(C1, Times(c, x)), FracPart(p)),
                                  Power(Plus(CN1, Times(c, x)), FracPart(p))),
                              CN1)),
                      Integrate(Times(Log(Times(h, Power(Plus(f, Times(g, x)), m))),
                          Power(Plus(C1, Times(c, x)), p), Power(Plus(CN1, Times(c, x)), p),
                          Power(Plus(a, Times(b, ArcCosh(Times(c, x)))), n)), x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, h, m, n), x), EqQ(Plus(Times(Sqr(c), d), e),
                      C0), IntegerQ(Subtract(p, C1D2))))),
          IIntegrate(5842,
              Integrate(
                  Times(
                      Log(Times(h_DEFAULT,
                          Power(Plus(f_DEFAULT, Times(g_DEFAULT, x_)), m_DEFAULT))),
                      Power(Plus(a_DEFAULT,
                          Times(ArcCosh(Times(c_DEFAULT, x_)), b_DEFAULT)), n_DEFAULT),
                      Power(Plus($p("d1"), Times($p("e1", true), x_)), p_), Power(
                          Plus($p("d2"), Times($p("e2", true), x_)), p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Times(CN1, $s("d1"), $s("d2")), IntPart(p)),
                          Power(Plus($s("d1"), Times($s("e1"), x)), FracPart(p)),
                          Power(Plus($s("d2"), Times($s("e2"), x)), FracPart(p)),
                          Power(
                              Times(Power(Plus(C1, Times(c, x)), FracPart(p)),
                                  Power(Plus(CN1, Times(c, x)), FracPart(p))),
                              CN1)),
                      Integrate(
                          Times(Log(Times(h, Power(Plus(f, Times(g, x)), m))),
                              Power(Plus(C1, Times(c, x)), p), Power(Plus(CN1, Times(c, x)), p),
                              Power(Plus(a, Times(b, ArcCosh(Times(c, x)))), n)),
                          x),
                      x),
                  And(FreeQ(
                      List(a, b, c, $s("d1"), $s("e1"), $s("d2"), $s("e2"), f, g, h, m, n), x),
                      EqQ(Subtract($s("e1"), Times(c, $s(
                          "d1"))), C0),
                      EqQ(Plus($s("e2"), Times(c, $s("d2"))), C0), IntegerQ(Subtract(p,
                          C1D2)),
                      Not(And(GtQ($s("d1"), C0), LtQ($s("d2"), C0)))))),
          IIntegrate(5843,
              Integrate(
                  Times(Plus(a_DEFAULT, Times(ArcSinh(Times(c_DEFAULT, x_)), b_DEFAULT)),
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
                      Subtract(Dist(Plus(a, Times(b, ArcSinh(Times(c, x)))), u, x),
                          Dist(Times(b, c),
                              Integrate(Dist(Power(Plus(C1, Times(Sqr(c), Sqr(x))), CN1D2), u, x),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, f, g), x), ILtQ(Plus(m, C1D2), C0)))),
          IIntegrate(5844,
              Integrate(
                  Times(Plus(a_DEFAULT, Times(ArcCosh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                      Power(Plus(d_, Times(e_DEFAULT, x_)), m_),
                      Power(Plus(f_, Times(g_DEFAULT, x_)), m_)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(u,
                              IntHide(
                                  Times(Power(Plus(d, Times(e, x)), m),
                                      Power(Plus(f, Times(g, x)), m)),
                                  x))),
                      Subtract(
                          Dist(Plus(a,
                              Times(b, ArcCosh(Times(c, x)))), u, x),
                          Dist(Times(b, c),
                              Integrate(Dist(Power(
                                  Times(Sqrt(Plus(C1, Times(c, x))), Sqrt(Plus(CN1, Times(c, x)))),
                                  CN1), u, x), x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, f, g), x), ILtQ(Plus(m, C1D2), C0)))),
          IIntegrate(5845,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcSinh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          n_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, x_)), m_DEFAULT), Power(
                          Plus(f_, Times(g_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Power(Plus(a, Times(b, ArcSinh(Times(c, x)))), n), Times(
                              Power(Plus(d, Times(e, x)), m), Power(Plus(f, Times(g, x)), m)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, n), x), IntegerQ(m)))),
          IIntegrate(5846,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCosh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          n_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, x_)), m_DEFAULT), Power(
                          Plus(f_, Times(g_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Power(Plus(a, Times(b,
                              ArcCosh(Times(c, x)))), n),
                          Times(Power(Plus(d, Times(e, x)), m), Power(Plus(f, Times(g, x)), m)), x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, n), x), IntegerQ(m)))),
          IIntegrate(5847,
              Integrate(
                  Times(Plus(a_DEFAULT, Times(ArcSinh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                      u_),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(v,
                          IntHide(u, x))),
                      Condition(
                          Subtract(
                              Dist(Plus(a,
                                  Times(b, ArcSinh(Times(c, x)))), v, x),
                              Dist(Times(b, c),
                                  Integrate(
                                      SimplifyIntegrand(
                                          Times(v, Power(Plus(C1, Times(Sqr(c), Sqr(x))),
                                              CN1D2)),
                                          x),
                                      x),
                                  x)),
                          InverseFunctionFreeQ(v, x))),
                  FreeQ(List(a, b, c), x))),
          IIntegrate(5848,
              Integrate(
                  Times(Plus(a_DEFAULT, Times(ArcCosh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                      u_),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(v,
                          IntHide(u, x))),
                      Condition(
                          Subtract(Dist(Plus(a, Times(b, ArcCosh(Times(c, x)))), v, x),
                              Dist(
                                  Times(b, c, Sqrt(Subtract(C1, Times(Sqr(c), Sqr(x)))),
                                      Power(
                                          Times(
                                              Sqrt(Plus(CN1, Times(c, x))),
                                              Sqrt(Plus(C1, Times(c, x)))),
                                          CN1)),
                                  Integrate(
                                      SimplifyIntegrand(
                                          Times(v,
                                              Power(Subtract(C1, Times(Sqr(c), Sqr(x))), CN1D2)),
                                          x),
                                      x),
                                  x)),
                          InverseFunctionFreeQ(v, x))),
                  FreeQ(List(a, b, c), x))),
          IIntegrate(5849,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcSinh(Times(c_DEFAULT, x_)), b_DEFAULT)), n_),
                      $p("§px"), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(u,
                              ExpandIntegrand(
                                  Times(
                                      $s("§px"), Power(Plus(d, Times(e,
                                          Sqr(x))), p),
                                      Power(Plus(a, Times(b, ArcSinh(Times(c, x)))), n)),
                                  x))),
                      Condition(Integrate(u, x), SumQ(u))),
                  And(FreeQ(List(a, b, c, d, e,
                      n), x), PolynomialQ($s("§px"),
                          x),
                      EqQ(e, Times(Sqr(c), d)), IntegerQ(Subtract(p, C1D2))))),
          IIntegrate(5850,
              Integrate(
                  Times(Power(Plus(a_DEFAULT, Times(ArcCosh(Times(c_DEFAULT, x_)), b_DEFAULT)), n_),
                      $p("§px"), Power(Plus($p("d1"), Times($p("e1", true), x_)), p_),
                      Power(Plus($p("d2"), Times($p("e2", true), x_)), p_)),
                  x_Symbol),
              Condition(With(List(Set(u,
                  ExpandIntegrand(Times($s("§px"), Power(Plus($s("d1"), Times($s("e1"), x)), p),
                      Power(Plus($s("d2"), Times($s("e2"), x)), p),
                      Power(Plus(a, Times(b, ArcCosh(Times(c, x)))), n)), x))),
                  Condition(Integrate(u, x), SumQ(u))),
                  And(FreeQ(List(a, b, c, $s("d1"), $s("e1"), $s("d2"), $s("e2"), n), x),
                      PolynomialQ($s("§px"), x), EqQ(Subtract($s("e1"),
                          Times(c, $s("d1"))), C0),
                      EqQ(Plus($s("e2"), Times(c, $s("d2"))), C0), IntegerQ(Subtract(p, C1D2))))),
          IIntegrate(5851,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcSinh(Times(c_DEFAULT, x_)),
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
                                      Power(Plus(a, Times(b, ArcSinh(Times(c, x)))), n)),
                                  x))),
                      Condition(Integrate(u, x), SumQ(u))),
                  And(FreeQ(List(a, b, c, d, e, f, g), x), PolynomialQ($s("§px"), x), EqQ(e, Times(
                      Sqr(c), d)), IGtQ(Plus(p, C1D2),
                          C0),
                      IntegersQ(m, n)))),
          IIntegrate(5852,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCosh(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          n_DEFAULT),
                      $p("§px", true),
                      Power(
                          Plus(f_,
                              Times(g_DEFAULT, Power(Plus($p("d1"), Times($p("e1", true), x_)), p_),
                                  Power(Plus($p("d2"), Times($p("e2", true), x_)), p_))),
                          m_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(u,
                              ExpandIntegrand(
                                  Times($s("§px"),
                                      Power(
                                          Plus(f,
                                              Times(g, Power(Plus($s("d1"), Times($s("e1"), x)), p),
                                                  Power(Plus($s("d2"), Times($s("e2"), x)), p))),
                                          m),
                                      Power(Plus(a, Times(b, ArcCosh(Times(c, x)))), n)),
                                  x))),
                      Condition(Integrate(u, x), SumQ(u))),
                  And(FreeQ(List(a, b, c, $s("d1"), $s("e1"), $s("d2"), $s("e2"), f, g), x),
                      PolynomialQ($s("§px"), x), EqQ(Subtract($s("e1"), Times(c, $s("d1"))), C0),
                      EqQ(Plus($s("e2"), Times(c, $s("d2"))), C0), IGtQ(Plus(p, C1D2),
                          C0),
                      IntegersQ(m, n)))),
          IIntegrate(5853,
              Integrate(Times(Power(ArcSinh(
                  Times(c_DEFAULT, x_)), n_DEFAULT), $p(
                      "§rfx")),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(u, ExpandIntegrand(Power(ArcSinh(Times(c, x)), n), $s("§rfx"), x))),
                      Condition(Integrate(u, x), SumQ(u))),
                  And(FreeQ(c, x), RationalFunctionQ($s("§rfx"), x), IGtQ(n, C0)))),
          IIntegrate(5854,
              Integrate(Times(Power(ArcCosh(
                  Times(c_DEFAULT, x_)), n_DEFAULT), $p(
                      "§rfx")),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(u, ExpandIntegrand(Power(ArcCosh(Times(c, x)), n), $s("§rfx"),
                          x))),
                      Condition(Integrate(u, x), SumQ(u))),
                  And(FreeQ(c, x), RationalFunctionQ($s("§rfx"), x), IGtQ(n, C0)))),
          IIntegrate(5855,
              Integrate(
                  Times(
                      Power(Plus(Times(ArcSinh(Times(c_DEFAULT, x_)), b_DEFAULT), a_),
                          n_DEFAULT),
                      $p("§rfx")),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(Times($s("§rfx"),
                          Power(Plus(a, Times(b, ArcSinh(Times(c, x)))), n)), x),
                      x),
                  And(FreeQ(List(a, b, c), x), RationalFunctionQ($s("§rfx"), x), IGtQ(n, C0)))),
          IIntegrate(5856,
              Integrate(
                  Times(
                      Power(Plus(Times(ArcCosh(Times(c_DEFAULT, x_)), b_DEFAULT),
                          a_), n_DEFAULT),
                      $p("§rfx")),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(Times($s("§rfx"),
                          Power(Plus(a, Times(b, ArcCosh(Times(c, x)))), n)), x),
                      x),
                  And(FreeQ(List(a, b, c), x), RationalFunctionQ($s("§rfx"), x), IGtQ(n, C0)))),
          IIntegrate(5857,
              Integrate(
                  Times(
                      Power(ArcSinh(Times(c_DEFAULT, x_)), n_DEFAULT), $p("§rfx"), Power(
                          Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  With(List(Set(u,
                      ExpandIntegrand(Times(Power(Plus(d, Times(e, Sqr(x))), p),
                          Power(ArcSinh(Times(c, x)), n)), $s("§rfx"), x))),
                      Condition(Integrate(u, x), SumQ(u))),
                  And(FreeQ(List(c, d, e), x), RationalFunctionQ($s(
                      "§rfx"), x), IGtQ(n,
                          C0),
                      EqQ(e, Times(Sqr(c), d)), IntegerQ(Subtract(p, C1D2))))),
          IIntegrate(5858,
              Integrate(Times(Power(ArcCosh(Times(c_DEFAULT, x_)), n_DEFAULT), $p("§rfx"),
                  Power(Plus($p("d1"), Times($p("e1", true), x_)), p_),
                  Power(Plus($p("d2"), Times($p("e2", true), x_)), p_)), x_Symbol),
              Condition(
                  With(
                      List(Set(u,
                          ExpandIntegrand(Times(Power(Plus($s("d1"), Times($s("e1"), x)), p),
                              Power(Plus($s("d2"), Times($s("e2"), x)),
                                  p),
                              Power(ArcCosh(Times(c, x)), n)), $s("§rfx"), x))),
                      Condition(Integrate(u, x), SumQ(u))),
                  And(FreeQ(List(c, $s("d1"), $s("e1"), $s("d2"), $s("e2")), x),
                      RationalFunctionQ($s("§rfx"), x), IGtQ(n, C0),
                      EqQ(Subtract($s("e1"), Times(c, $s("d1"))), C0), EqQ(Plus($s("e2"),
                          Times(c, $s("d2"))), C0),
                      IntegerQ(Subtract(p, C1D2))))),
          IIntegrate(5859,
              Integrate(
                  Times(
                      Power(Plus(Times(ArcSinh(Times(c_DEFAULT, x_)), b_DEFAULT),
                          a_), n_DEFAULT),
                      $p("§rfx"), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Power(Plus(d,
                              Times(e, Sqr(x))), p),
                          Times($s("§rfx"), Power(Plus(a, Times(b, ArcSinh(Times(c, x)))), n)), x),
                      x),
                  And(FreeQ(List(a, b, c, d, e), x), RationalFunctionQ($s(
                      "§rfx"), x), IGtQ(n,
                          C0),
                      EqQ(e, Times(Sqr(c), d)), IntegerQ(Subtract(p, C1D2))))),
          IIntegrate(5860,
              Integrate(
                  Times(Power(Plus(Times(ArcCosh(Times(c_DEFAULT, x_)), b_DEFAULT), a_), n_DEFAULT),
                      $p("§rfx"), Power(Plus($p("d1"), Times($p("e1", true), x_)), p_),
                      Power(Plus($p("d2"), Times($p("e2", true), x_)), p_)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Times(Power(Plus($s("d1"), Times($s("e1"), x)), p),
                              Power(Plus($s("d2"), Times($s("e2"), x)), p)),
                          Times($s("§rfx"), Power(Plus(a, Times(b, ArcCosh(Times(c, x)))), n)), x),
                      x),
                  And(FreeQ(List(a, b, c, $s("d1"), $s("e1"), $s("d2"), $s("e2")), x),
                      RationalFunctionQ($s("§rfx"), x), IGtQ(n, C0),
                      EqQ(Subtract($s("e1"), Times(c, $s("d1"))), C0),
                      EqQ(Plus($s("e2"), Times(c, $s("d2"))), C0), IntegerQ(Subtract(p, C1D2))))));
}
