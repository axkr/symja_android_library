package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.ArcCos;
import static org.matheclipse.core.expression.F.ArcSin;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CI;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.Cot;
import static org.matheclipse.core.expression.F.Csc;
import static org.matheclipse.core.expression.F.Exp;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sec;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Tan;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_;
import static org.matheclipse.core.expression.F.c_DEFAULT;
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
import static org.matheclipse.core.expression.F.u_DEFAULT;
import static org.matheclipse.core.expression.F.v_;
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
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandTrigReduce;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FracPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules230 {
  public static IAST RULES =
      List(
          IIntegrate(4601,
              Integrate(
                  Times(Power(Times(b_DEFAULT, x_), m_DEFAULT),
                      Power(Sec(
                          Times(a_DEFAULT, x_)), n_DEFAULT),
                      Power(
                          Plus(
                              Times(Cos(
                                  Times(a_DEFAULT, x_)), c_DEFAULT),
                              Times(d_DEFAULT, x_, Sin(Times(a_DEFAULT, x_)))),
                          CN2)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(b, Power(Times(b, x), Subtract(m, C1)),
                                  Power(Sec(Times(a, x)), Plus(n, C1)),
                                  Power(Times(a, d,
                                      Plus(Times(c, Cos(Times(a, x))),
                                          Times(d, x, Sin(Times(a, x))))),
                                      CN1)),
                              x)),
                      Dist(
                          Times(Sqr(b), Plus(n, C1), Power(d,
                              CN2)),
                          Integrate(
                              Times(
                                  Power(Times(b, x), Subtract(m, C2)), Power(Sec(Times(a, x)),
                                      Plus(n, C2))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, m, n), x), EqQ(Subtract(Times(a, c),
                      d), C0), EqQ(m,
                          Plus(n, C2))))),
          IIntegrate(4602,
              Integrate(
                  Times(
                      Power(Plus(g_DEFAULT,
                          Times(h_DEFAULT, x_)), p_DEFAULT),
                      Power(
                          Plus(a_, Times(b_DEFAULT,
                              Sin(Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_DEFAULT),
                      Power(Plus(c_, Times(d_DEFAULT, Sin(Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(a, m), Power(c,
                          m)),
                      Integrate(
                          Times(Power(Plus(g, Times(h, x)), p),
                              Power(Cos(Plus(e, Times(f, x))), Times(C2,
                                  m)),
                              Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), Subtract(n, m))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, h), x),
                      EqQ(Plus(Times(b, c),
                          Times(a, d)), C0),
                      EqQ(Subtract(Sqr(a), Sqr(b)), C0), IntegerQ(m), IGtQ(Subtract(n, m), C0)))),
          IIntegrate(4603,
              Integrate(
                  Times(
                      Power(Plus(Times(Cos(Plus(e_DEFAULT, Times(f_DEFAULT, x_))), b_DEFAULT), a_),
                          m_DEFAULT),
                      Power(Plus(Times(Cos(Plus(e_DEFAULT, Times(f_DEFAULT, x_))), d_DEFAULT),
                          c_), n_DEFAULT),
                      Power(Plus(g_DEFAULT, Times(h_DEFAULT, x_)), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(a, m), Power(c,
                          m)),
                      Integrate(
                          Times(Power(Plus(g, Times(h, x)), p),
                              Power(Sin(Plus(e, Times(f, x))), Times(C2,
                                  m)),
                              Power(Plus(c, Times(d, Cos(Plus(e, Times(f, x))))), Subtract(n, m))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, h), x),
                      EqQ(Plus(Times(b, c),
                          Times(a, d)), C0),
                      EqQ(Subtract(Sqr(a), Sqr(b)), C0), IntegerQ(m), IGtQ(Subtract(n, m), C0)))),
          IIntegrate(4604,
              Integrate(
                  Times(
                      Power(Plus(g_DEFAULT,
                          Times(h_DEFAULT, x_)), p_DEFAULT),
                      Power(
                          Plus(a_, Times(b_DEFAULT,
                              Sin(Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Power(Plus(c_, Times(d_DEFAULT, Sin(Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(a, IntPart(m)), Power(c, IntPart(m)),
                          Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), FracPart(m)),
                          Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), FracPart(
                              m)),
                          Power(Power(Cos(Plus(e, Times(f, x))), Times(C2, FracPart(m))), CN1)),
                      Integrate(
                          Times(Power(Plus(g, Times(h, x)), p),
                              Power(Cos(Plus(e, Times(f, x))), Times(C2,
                                  m)),
                              Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), Subtract(n, m))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, h), x),
                      EqQ(Plus(Times(b, c), Times(a,
                          d)), C0),
                      EqQ(Subtract(Sqr(a), Sqr(
                          b)), C0),
                      IntegerQ(p), IntegerQ(Times(C2, m)), IGeQ(Subtract(n, m), C0)))),
          IIntegrate(4605,
              Integrate(
                  Times(
                      Power(
                          Plus(Times(Cos(Plus(e_DEFAULT, Times(f_DEFAULT, x_))), b_DEFAULT), a_),
                          m_),
                      Power(
                          Plus(Times(Cos(Plus(e_DEFAULT, Times(f_DEFAULT, x_))), d_DEFAULT),
                              c_),
                          n_),
                      Power(Plus(g_DEFAULT, Times(h_DEFAULT, x_)), p_DEFAULT)),
                  x_Symbol),
              Condition(Dist(
                  Times(Power(a, IntPart(m)), Power(c, IntPart(m)),
                      Power(Plus(a, Times(b, Cos(Plus(e, Times(f, x))))), FracPart(m)),
                      Power(Plus(c, Times(d, Cos(Plus(e, Times(f, x))))), FracPart(m)),
                      Power(Power(Sin(Plus(e, Times(f, x))), Times(C2, FracPart(m))), CN1)),
                  Integrate(
                      Times(Power(Plus(g, Times(h, x)), p),
                          Power(Sin(Plus(e, Times(f, x))), Times(C2, m)),
                          Power(Plus(c, Times(d, Cos(Plus(e, Times(f, x))))), Subtract(n, m))),
                      x),
                  x),
                  And(FreeQ(List(a, b, c, d, e, f, g, h), x),
                      EqQ(Plus(Times(b, c), Times(a, d)), C0), EqQ(Subtract(Sqr(a), Sqr(b)), C0),
                      IntegerQ(p), IntegerQ(Times(C2, m)), IGeQ(Subtract(n, m), C0)))),
          IIntegrate(4606,
              Integrate(
                  Times(
                      Power(Sec(v_), m_DEFAULT), Power(Plus(a_, Times(b_DEFAULT, Tan(v_))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(Power(Plus(Times(a, Cos(v)), Times(b, Sin(v))),
                      n), x),
                  And(FreeQ(List(a, b), x), IntegerQ(Times(C1D2, Subtract(m, C1))),
                      EqQ(Plus(m, n), C0)))),
          IIntegrate(4607,
              Integrate(
                  Times(
                      Power(Csc(
                          v_), m_DEFAULT),
                      Power(Plus(Times(Cot(v_), b_DEFAULT), a_), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(Power(Plus(Times(b, Cos(v)), Times(a, Sin(v))),
                      n), x),
                  And(FreeQ(List(a, b), x), IntegerQ(Times(C1D2,
                      Subtract(m, C1))), EqQ(Plus(m, n),
                          C0)))),
          IIntegrate(4608,
              Integrate(
                  Times(
                      u_DEFAULT, Power(Sin(Plus(a_DEFAULT,
                          Times(b_DEFAULT, x_))), m_DEFAULT),
                      Power(Sin(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(ExpandTrigReduce(u,
                      Times(Power(Sin(Plus(a, Times(b, x))), m),
                          Power(Sin(Plus(c, Times(d, x))), n)),
                      x), x),
                  And(FreeQ(List(a, b, c, d), x), IGtQ(m, C0), IGtQ(n, C0)))),
          IIntegrate(4609,
              Integrate(
                  Times(
                      Power(Cos(Plus(a_DEFAULT,
                          Times(b_DEFAULT, x_))), m_DEFAULT),
                      Power(Cos(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), n_DEFAULT), u_DEFAULT),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandTrigReduce(u,
                          Times(
                              Power(Cos(Plus(a, Times(b, x))), m), Power(Cos(Plus(c, Times(d, x))),
                                  n)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d), x), IGtQ(m, C0), IGtQ(n, C0)))),
          IIntegrate(4610,
              Integrate(
                  Times(Sec(Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                      Sec(Plus(c_, Times(d_DEFAULT, x_)))),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Dist(
                              Csc(Times(Subtract(Times(b, c), Times(a, d)),
                                  Power(d, CN1))),
                              Integrate(Tan(Plus(a, Times(b, x))), x), x)),
                      Dist(
                          Csc(Times(Subtract(Times(b, c), Times(a, d)), Power(b, CN1))), Integrate(
                              Tan(Plus(c, Times(d, x))), x),
                          x)),
                  And(FreeQ(List(a, b, c, d), x), EqQ(Subtract(Sqr(b), Sqr(d)), C0),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0)))),
          IIntegrate(4611,
              Integrate(
                  Times(
                      Csc(Plus(a_DEFAULT,
                          Times(b_DEFAULT, x_))),
                      Csc(Plus(c_, Times(d_DEFAULT, x_)))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(
                          Csc(Times(Subtract(Times(b, c), Times(a, d)),
                              Power(b, CN1))),
                          Integrate(Cot(Plus(a, Times(b, x))), x), x),
                      Dist(
                          Csc(Times(Subtract(Times(b, c), Times(a, d)), Power(d, CN1))), Integrate(
                              Cot(Plus(c, Times(d, x))), x),
                          x)),
                  And(FreeQ(List(a, b, c, d), x), EqQ(Subtract(Sqr(b), Sqr(d)), C0),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0)))),
          IIntegrate(4612,
              Integrate(
                  Times(
                      Tan(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), Tan(
                          Plus(c_, Times(d_DEFAULT, x_)))),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(Simp(Times(b, x, Power(d, CN1)),
                          x)),
                      Dist(
                          Times(
                              b, Cos(Times(Subtract(Times(b, c), Times(a, d)),
                                  Power(d, CN1))),
                              Power(d, CN1)),
                          Integrate(Times(Sec(Plus(a, Times(b, x))), Sec(Plus(c, Times(d, x)))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d), x), EqQ(Subtract(Sqr(b), Sqr(d)), C0),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0)))),
          IIntegrate(4613,
              Integrate(
                  Times(
                      Cot(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), Cot(
                          Plus(c_, Times(d_DEFAULT, x_)))),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(Simp(Times(b, x, Power(d, CN1)),
                          x)),
                      Dist(
                          Cos(Times(Subtract(Times(b, c), Times(a, d)), Power(d,
                              CN1))),
                          Integrate(Times(Csc(Plus(a, Times(b, x))), Csc(Plus(c, Times(d, x)))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d), x), EqQ(Subtract(Sqr(b), Sqr(d)), C0),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0)))),
          IIntegrate(4614,
              Integrate(
                  Times(
                      u_DEFAULT, Power(Plus(Times(Cos(v_), a_DEFAULT), Times(b_DEFAULT, Sin(v_))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(Times(u, Power(Times(a,
                      Power(Exp(Times(a, v, Power(b, CN1))), CN1)), n)), x),
                  And(FreeQ(List(a, b, n), x), EqQ(Plus(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(4615,
              Integrate(
                  Sin(Times(
                      Sqr(Plus(
                          a_DEFAULT, Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                              b_DEFAULT))),
                      d_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Times(C1D2, CI),
                          Integrate(
                              Exp(Times(CN1, CI, d, Sqr(
                                  Plus(a, Times(b, Log(Times(c, Power(x, n)))))))),
                              x),
                          x),
                      Dist(Times(C1D2, CI),
                          Integrate(
                              Exp(Times(CI, d, Sqr(Plus(a, Times(b, Log(Times(c, Power(x, n)))))))),
                              x),
                          x)),
                  FreeQ(List(a, b, c, d, n), x))),
          IIntegrate(4616,
              Integrate(
                  Cos(Times(
                      Sqr(Plus(
                          a_DEFAULT, Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                              b_DEFAULT))),
                      d_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(C1D2,
                          Integrate(
                              Exp(Times(CN1, CI, d, Sqr(
                                  Plus(a, Times(b, Log(Times(c, Power(x, n)))))))),
                              x),
                          x),
                      Dist(C1D2,
                          Integrate(
                              Exp(Times(CI, d, Sqr(Plus(a, Times(b, Log(Times(c, Power(x, n)))))))),
                              x),
                          x)),
                  FreeQ(List(a, b, c, d, n), x))),
          IIntegrate(4617,
              Integrate(
                  Times(
                      Power(Times(e_DEFAULT,
                          x_), m_DEFAULT),
                      Sin(Times(
                          Sqr(Plus(
                              a_DEFAULT, Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                                  b_DEFAULT))),
                          d_DEFAULT))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Times(C1D2, CI),
                          Integrate(
                              Times(
                                  Power(Times(e,
                                      x), m),
                                  Power(
                                      Exp(Times(CI, d,
                                          Sqr(Plus(a, Times(b, Log(Times(c, Power(x, n)))))))),
                                      CN1)),
                              x),
                          x),
                      Dist(Times(C1D2, CI),
                          Integrate(
                              Times(
                                  Power(Times(e,
                                      x), m),
                                  Exp(Times(CI, d,
                                      Sqr(Plus(a, Times(b, Log(Times(c, Power(x, n))))))))),
                              x),
                          x)),
                  FreeQ(List(a, b, c, d, e, m, n), x))),
          IIntegrate(4618,
              Integrate(
                  Times(
                      Cos(Times(
                          Sqr(Plus(
                              a_DEFAULT,
                              Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))), b_DEFAULT))),
                          d_DEFAULT)),
                      Power(Times(e_DEFAULT, x_), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(C1D2,
                          Integrate(
                              Times(
                                  Power(Times(e,
                                      x), m),
                                  Power(
                                      Exp(Times(CI, d,
                                          Sqr(Plus(a, Times(b, Log(Times(c, Power(x, n)))))))),
                                      CN1)),
                              x),
                          x),
                      Dist(C1D2, Integrate(Times(Power(Times(e, x), m),
                          Exp(Times(CI, d, Sqr(Plus(a, Times(b, Log(Times(c, Power(x, n))))))))),
                          x), x)),
                  FreeQ(List(a, b, c, d, e, m, n), x))),
          IIntegrate(4619,
              Integrate(
                  Power(Plus(a_DEFAULT, Times(ArcSin(Times(c_DEFAULT, x_)), b_DEFAULT)),
                      n_DEFAULT),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(Times(x,
                          Power(Plus(a, Times(b, ArcSin(Times(c, x)))), n)), x),
                      Dist(Times(b, c, n),
                          Integrate(
                              Times(x,
                                  Power(Plus(a, Times(b, ArcSin(Times(c, x)))), Subtract(n,
                                      C1)),
                                  Power(Subtract(C1, Times(Sqr(c), Sqr(x))), CN1D2)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c), x), GtQ(n, C0)))),
          IIntegrate(4620, Integrate(
              Power(Plus(a_DEFAULT, Times(ArcCos(Times(c_DEFAULT, x_)), b_DEFAULT)), n_DEFAULT),
              x_Symbol),
              Condition(
                  Plus(Simp(Times(x, Power(Plus(a, Times(b, ArcCos(Times(c, x)))), n)), x),
                      Dist(Times(b, c, n),
                          Integrate(Times(x,
                              Power(Plus(a, Times(b, ArcCos(Times(c, x)))), Subtract(n, C1)),
                              Power(Subtract(C1, Times(Sqr(c), Sqr(x))), CN1D2)), x),
                          x)),
                  And(FreeQ(List(a, b, c), x), GtQ(n, C0)))));
}
