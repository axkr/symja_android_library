package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.ArcCos;
import static org.matheclipse.core.expression.F.ArcSin;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.PolynomialQ;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.g_DEFAULT;
import static org.matheclipse.core.expression.F.h_DEFAULT;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_DEFAULT;
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
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandExpression;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ILtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntHide;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SimplifyIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules237 {
  public static IAST RULES =
      List(
          IIntegrate(4741,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcSin(Times(c_DEFAULT, x_)), b_DEFAULT)), n_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, x_)), CN1)),
                  x_Symbol),
              Condition(Subst(Integrate(
                  Times(Power(Plus(a, Times(b,
                      x)), n), Cos(x), Power(Plus(Times(c, d), Times(e, Sin(x))),
                          CN1)),
                  x), x, ArcSin(Times(c, x))), And(FreeQ(List(a, b, c, d, e), x),
                      IGtQ(n, C0)))),
          IIntegrate(4742,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(ArcCos(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          n_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, x_)), CN1)),
                  x_Symbol),
              Condition(
                  Negate(
                      Subst(
                          Integrate(Times(Power(Plus(a, Times(b, x)), n), Sin(x),
                              Power(Plus(Times(c, d), Times(e, Cos(x))), CN1)), x),
                          x, ArcCos(Times(c, x)))),
                  And(FreeQ(List(a, b, c, d, e), x), IGtQ(n, C0)))),
          IIntegrate(4743,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(ArcSin(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          n_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(Times(Power(Plus(d, Times(e, x)), Plus(m, C1)),
                          Power(Plus(a, Times(b, ArcSin(Times(c, x)))),
                              n),
                          Power(Times(e, Plus(m, C1)), CN1)), x),
                      Dist(
                          Times(b, c, n, Power(Times(e, Plus(m, C1)), CN1)), Integrate(
                              Times(Power(Plus(d, Times(e, x)), Plus(m, C1)),
                                  Power(Plus(a, Times(b, ArcSin(Times(c, x)))), Subtract(n, C1)),
                                  Power(Subtract(C1, Times(Sqr(c), Sqr(x))), CN1D2)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, m), x), IGtQ(n, C0), NeQ(m, CN1)))),
          IIntegrate(4744,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(ArcCos(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          n_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Plus(d, Times(e, x)), Plus(m, C1)),
                              Power(Plus(a, Times(b, ArcCos(Times(c, x)))),
                                  n),
                              Power(Times(e, Plus(m, C1)), CN1)),
                          x),
                      Dist(Times(b, c, n, Power(Times(e, Plus(m, C1)), CN1)),
                          Integrate(
                              Times(Power(Plus(d, Times(e, x)), Plus(m, C1)),
                                  Power(Plus(a, Times(b, ArcCos(Times(c, x)))), Subtract(n,
                                      C1)),
                                  Power(Subtract(C1, Times(Sqr(c), Sqr(x))), CN1D2)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, m), x), IGtQ(n, C0), NeQ(m, CN1)))),
          IIntegrate(4745,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcSin(Times(c_DEFAULT, x_)), b_DEFAULT)), n_),
                      Power(Plus(d_, Times(e_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Times(
                              Power(Plus(d,
                                  Times(e, x)), m),
                              Power(Plus(a, Times(b, ArcSin(Times(c, x)))), n)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e), x), IGtQ(m, C0), LtQ(n, CN1)))),
          IIntegrate(4746,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcCos(Times(c_DEFAULT, x_)), b_DEFAULT)), n_),
                      Power(Plus(d_, Times(e_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Times(Power(Plus(d, Times(e, x)), m),
                              Power(Plus(a, Times(b, ArcCos(Times(c, x)))), n)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e), x), IGtQ(m, C0), LtQ(n, CN1)))),
          IIntegrate(4747,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(ArcSin(Times(c_DEFAULT, x_)),
                          b_DEFAULT)), n_),
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Power(c,
                          Plus(m, C1)), CN1),
                      Subst(
                          Integrate(
                              Times(
                                  Power(Plus(a, Times(b, x)), n), Cos(x), Power(
                                      Plus(Times(c, d), Times(e, Sin(x))), m)),
                              x),
                          x, ArcSin(Times(c, x))),
                      x),
                  And(FreeQ(List(a, b, c, d, e, n), x), IGtQ(m, C0)))),
          IIntegrate(4748,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(ArcCos(Times(c_DEFAULT, x_)),
                          b_DEFAULT)), n_),
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(
                          Power(Power(c,
                              Plus(m, C1)), CN1),
                          Subst(
                              Integrate(
                                  Times(
                                      Power(Plus(a, Times(b, x)), n), Sin(x), Power(
                                          Plus(Times(c, d), Times(e, Cos(x))), m)),
                                  x),
                              x, ArcCos(Times(c, x))),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, n), x), IGtQ(m, C0)))),
          IIntegrate(4749,
              Integrate(
                  Times(Plus(a_DEFAULT, Times(ArcSin(Times(c_DEFAULT, x_)),
                      b_DEFAULT)), $p(
                          "§px")),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(u,
                          IntHide(ExpandExpression($s("§px"), x), x))),
                      Subtract(
                          Dist(Plus(a,
                              Times(b, ArcSin(Times(c, x)))), u, x),
                          Dist(Times(b, c),
                              Integrate(
                                  SimplifyIntegrand(
                                      Times(u, Power(Subtract(C1, Times(Sqr(c), Sqr(x))), CN1D2)),
                                      x),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c), x), PolynomialQ($s("§px"), x)))),
          IIntegrate(4750,
              Integrate(
                  Times(Plus(a_DEFAULT, Times(ArcCos(Times(c_DEFAULT, x_)),
                      b_DEFAULT)), $p(
                          "§px")),
                  x_Symbol),
              Condition(
                  With(List(Set(u, IntHide(ExpandExpression($s("§px"), x), x))),
                      Plus(Dist(Plus(a, Times(b, ArcCos(Times(c, x)))), u, x),
                          Dist(Times(b, c),
                              Integrate(SimplifyIntegrand(
                                  Times(u, Power(Subtract(C1, Times(Sqr(c), Sqr(x))), CN1D2)), x),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c), x), PolynomialQ($s("§px"), x)))),
          IIntegrate(4751,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcSin(Times(c_DEFAULT, x_)), b_DEFAULT)), n_),
                      $p("§px")),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Times($s("§px"), Power(Plus(a, Times(b, ArcSin(Times(c, x)))), n)), x),
                      x),
                  And(FreeQ(List(a, b, c, n), x), PolynomialQ($s("§px"), x)))),
          IIntegrate(4752,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcCos(Times(c_DEFAULT, x_)), b_DEFAULT)), n_),
                      $p("§px")),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(Times($s("§px"),
                          Power(Plus(a, Times(b, ArcCos(Times(c, x)))), n)), x),
                      x),
                  And(FreeQ(List(a, b, c, n), x), PolynomialQ($s("§px"), x)))),
          IIntegrate(4753,
              Integrate(
                  Times(
                      Plus(a_DEFAULT, Times(ArcSin(Times(c_DEFAULT, x_)),
                          b_DEFAULT)),
                      $p("§px"), Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(u,
                          IntHide(Times($s("§px"), Power(Plus(d, Times(e, x)), m)), x))),
                      Subtract(
                          Dist(Plus(a,
                              Times(b, ArcSin(Times(c, x)))), u, x),
                          Dist(Times(b, c),
                              Integrate(
                                  SimplifyIntegrand(
                                      Times(u, Power(Subtract(C1, Times(Sqr(c), Sqr(x))),
                                          CN1D2)),
                                      x),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, m), x), PolynomialQ($s("§px"), x)))),
          IIntegrate(4754,
              Integrate(
                  Times(
                      Plus(a_DEFAULT, Times(ArcCos(Times(c_DEFAULT, x_)),
                          b_DEFAULT)),
                      $p("§px"), Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(u,
                          IntHide(Times($s("§px"), Power(Plus(d, Times(e, x)), m)), x))),
                      Plus(
                          Dist(Plus(a,
                              Times(b, ArcCos(Times(c, x)))), u, x),
                          Dist(Times(b, c),
                              Integrate(
                                  SimplifyIntegrand(
                                      Times(u, Power(Subtract(C1, Times(Sqr(c), Sqr(x))),
                                          CN1D2)),
                                      x),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, m), x), PolynomialQ($s("§px"), x)))),
          IIntegrate(4755,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(ArcSin(
                          Times(c_DEFAULT, x_)), b_DEFAULT)), n_),
                      Power(Plus(d_,
                          Times(e_DEFAULT, x_)), m_),
                      Power(Plus(f_DEFAULT, Times(g_DEFAULT, x_)), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(u,
                              IntHide(
                                  Times(
                                      Power(Plus(f,
                                          Times(g, x)), p),
                                      Power(Plus(d, Times(e, x)), m)),
                                  x))),
                      Subtract(
                          Dist(Power(Plus(a, Times(b, ArcSin(Times(c, x)))),
                              n), u, x),
                          Dist(Times(b, c, n),
                              Integrate(SimplifyIntegrand(
                                  Times(u,
                                      Power(Plus(a, Times(b, ArcSin(Times(c, x)))),
                                          Subtract(n, C1)),
                                      Power(Subtract(C1, Times(Sqr(c), Sqr(x))), CN1D2)),
                                  x), x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, f,
                      g), x), IGtQ(n,
                          C0),
                      IGtQ(p, C0), ILtQ(m, C0), LtQ(Plus(m, p, C1), C0)))),
          IIntegrate(4756,
              Integrate(
                  Times(Power(Plus(a_DEFAULT, Times(ArcCos(Times(c_DEFAULT, x_)), b_DEFAULT)), n_),
                      Power(Plus(d_,
                          Times(e_DEFAULT, x_)), m_),
                      Power(Plus(f_DEFAULT, Times(g_DEFAULT, x_)), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(u,
                              IntHide(
                                  Times(
                                      Power(Plus(f,
                                          Times(g, x)), p),
                                      Power(Plus(d, Times(e, x)), m)),
                                  x))),
                      Plus(
                          Dist(Power(Plus(a, Times(b, ArcCos(Times(c, x)))),
                              n), u, x),
                          Dist(Times(b, c, n),
                              Integrate(SimplifyIntegrand(Times(u,
                                  Power(Plus(a, Times(b, ArcCos(Times(c, x)))), Subtract(n, C1)),
                                  Power(Subtract(C1, Times(Sqr(c), Sqr(x))), CN1D2)), x), x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, f,
                      g), x), IGtQ(n,
                          C0),
                      IGtQ(p, C0), ILtQ(m, C0), LtQ(Plus(m, p, C1), C0)))),
          IIntegrate(4757,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(ArcSin(Times(c_DEFAULT, x_)),
                          b_DEFAULT)), n_),
                      Power(Plus(d_,
                          Times(e_DEFAULT, x_)), CN2),
                      Power(
                          Plus(f_DEFAULT, Times(g_DEFAULT, x_),
                              Times(h_DEFAULT, Sqr(x_))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(u,
                              IntHide(
                                  Times(
                                      Power(Plus(f, Times(g, x),
                                          Times(h, Sqr(x))), p),
                                      Power(Plus(d, Times(e, x)), CN2)),
                                  x))),
                      Subtract(
                          Dist(Power(Plus(a, Times(b, ArcSin(Times(c, x)))),
                              n), u, x),
                          Dist(Times(b, c, n),
                              Integrate(
                                  SimplifyIntegrand(Times(u,
                                      Power(Plus(a, Times(b, ArcSin(Times(c, x)))), Subtract(n,
                                          C1)),
                                      Power(Subtract(C1, Times(Sqr(c), Sqr(x))), CN1D2)), x),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, f, g, h), x), IGtQ(n, C0), IGtQ(p, C0),
                      EqQ(Subtract(Times(e, g), Times(C2, d, h)), C0)))),
          IIntegrate(4758,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(ArcCos(Times(c_DEFAULT, x_)),
                          b_DEFAULT)), n_),
                      Power(Plus(d_,
                          Times(e_DEFAULT, x_)), CN2),
                      Power(
                          Plus(f_DEFAULT, Times(g_DEFAULT, x_),
                              Times(h_DEFAULT, Sqr(x_))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(u,
                              IntHide(
                                  Times(
                                      Power(Plus(f, Times(g, x),
                                          Times(h, Sqr(x))), p),
                                      Power(Plus(d, Times(e, x)), CN2)),
                                  x))),
                      Plus(
                          Dist(Power(Plus(a, Times(b, ArcCos(Times(c, x)))),
                              n), u, x),
                          Dist(Times(b, c, n),
                              Integrate(
                                  SimplifyIntegrand(
                                      Times(u,
                                          Power(Plus(a, Times(b, ArcCos(Times(c, x)))),
                                              Subtract(n, C1)),
                                          Power(Subtract(C1, Times(Sqr(c), Sqr(x))), CN1D2)),
                                      x),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, f, g, h), x), IGtQ(n, C0), IGtQ(p, C0),
                      EqQ(Subtract(Times(e, g), Times(C2, d, h)), C0)))),
          IIntegrate(4759,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcSin(Times(c_DEFAULT, x_)), b_DEFAULT)), n_),
                      $p("§px"), Power(Plus(d_, Times(e_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Times($s("§px"), Power(Plus(d, Times(e, x)), m),
                              Power(Plus(a, Times(b, ArcSin(Times(c, x)))), n)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e), x), PolynomialQ($s("§px"), x), IGtQ(n, C0),
                      IntegerQ(m)))),
          IIntegrate(4760,
              Integrate(
                  Times(Power(Plus(a_DEFAULT, Times(ArcCos(Times(c_DEFAULT, x_)), b_DEFAULT)), n_),
                      $p("§px"), Power(Plus(d_, Times(e_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(ExpandIntegrand(Times($s("§px"), Power(Plus(d, Times(e, x)), m),
                      Power(Plus(a, Times(b, ArcCos(Times(c, x)))), n)), x), x),
                  And(FreeQ(List(a, b, c, d, e), x), PolynomialQ($s("§px"), x), IGtQ(n, C0),
                      IntegerQ(m)))));
}
