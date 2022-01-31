package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.ArcCoth;
import static org.matheclipse.core.expression.F.ArcTanh;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.PolyLog;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.g_DEFAULT;
import static org.matheclipse.core.expression.F.k_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.q_DEFAULT;
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
import static org.matheclipse.core.expression.S.k;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.q;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ILtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntHide;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Rt;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SimplifyIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SumQ;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules302 {
  public static IAST RULES =
      List(
          IIntegrate(6041,
              Integrate(
                  Times(
                      Plus(a_DEFAULT, Times(ArcCoth(
                          Times(c_DEFAULT, x_)), b_DEFAULT)),
                      Power(Times(f_DEFAULT,
                          x_), m_DEFAULT),
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT, Sqr(x_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(u,
                          IntHide(Times(Power(Times(f, x), m), Power(Plus(d, Times(e, Sqr(x))), q)),
                              x))),
                      Subtract(Dist(Plus(a, Times(b, ArcCoth(Times(c, x)))), u, x),
                          Dist(Times(b, c),
                              Integrate(
                                  SimplifyIntegrand(
                                      Times(u, Power(Subtract(C1, Times(Sqr(c), Sqr(x))), CN1)), x),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, f, m,
                      q), x), Or(
                          And(IGtQ(q, C0),
                              Not(And(ILtQ(Times(C1D2, Subtract(m, C1)), C0),
                                  GtQ(Plus(m, Times(C2, q), C3), C0)))),
                          And(IGtQ(Times(C1D2,
                              Plus(m, C1)), C0), Not(
                                  And(ILtQ(q, C0), GtQ(Plus(m, Times(C2, q), C3), C0)))),
                          And(ILtQ(Times(C1D2,
                              Plus(m, Times(C2, q), C1)), C0), Not(
                                  ILtQ(Times(C1D2, Subtract(m, C1)), C0))))))),
          IIntegrate(6042,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcTanh(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          p_DEFAULT),
                      x_, Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN2)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(
                          Power(Times(C4, Sqr(d),
                              Rt(Times(CN1, e, Power(d, CN1)), C2)), CN1),
                          Integrate(
                              Times(
                                  Power(Plus(a,
                                      Times(b, ArcTanh(Times(c, x)))), p),
                                  Power(
                                      Subtract(C1, Times(Rt(Times(CN1, e, Power(d, CN1)), C2),
                                          x)),
                                      CN2)),
                              x),
                          x),
                      Dist(Power(Times(C4, Sqr(d), Rt(Times(CN1, e, Power(d, CN1)), C2)), CN1),
                          Integrate(
                              Times(Power(Plus(a, Times(b, ArcTanh(Times(c, x)))), p),
                                  Power(Plus(C1, Times(Rt(Times(CN1, e, Power(d, CN1)), C2), x)),
                                      CN2)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), IGtQ(p, C0)))),
          IIntegrate(6043,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCoth(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          p_DEFAULT),
                      x_, Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN2)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(
                          Power(Times(C4, Sqr(d),
                              Rt(Times(CN1, e, Power(d, CN1)), C2)), CN1),
                          Integrate(Times(Power(Plus(a, Times(b, ArcCoth(Times(c, x)))), p),
                              Power(Subtract(C1, Times(Rt(Times(CN1, e, Power(d, CN1)), C2), x)),
                                  CN2)),
                              x),
                          x),
                      Dist(Power(Times(C4, Sqr(d), Rt(Times(CN1, e, Power(d, CN1)), C2)), CN1),
                          Integrate(
                              Times(Power(Plus(a, Times(b, ArcCoth(Times(c, x)))), p),
                                  Power(Plus(C1, Times(Rt(Times(CN1, e, Power(d, CN1)), C2), x)),
                                      CN2)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), IGtQ(p, C0)))),
          IIntegrate(6044,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(ArcTanh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          p_DEFAULT),
                      Power(Times(f_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(u,
                              ExpandIntegrand(Power(Plus(a, Times(b, ArcTanh(Times(c, x)))), p),
                                  Times(Power(Times(f, x), m),
                                      Power(Plus(d, Times(e, Sqr(x))), q)),
                                  x))),
                      Condition(Integrate(u, x), SumQ(u))),
                  And(FreeQ(List(a, b, c, d, e, f, m), x), IntegerQ(q), IGtQ(p, C0),
                      Or(GtQ(q, C0), IntegerQ(m))))),
          IIntegrate(6045,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcCoth(Times(c_DEFAULT, x_)), b_DEFAULT)), p_DEFAULT),
                      Power(Times(f_DEFAULT,
                          x_), m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(u,
                              ExpandIntegrand(Power(Plus(a, Times(b, ArcCoth(Times(c, x)))), p),
                                  Times(Power(Times(f, x), m),
                                      Power(Plus(d, Times(e, Sqr(x))), q)),
                                  x))),
                      Condition(Integrate(u, x), SumQ(u))),
                  And(FreeQ(List(a, b, c, d, e, f, m), x), IntegerQ(q), IGtQ(p, C0),
                      Or(GtQ(q, C0), IntegerQ(m))))),
          IIntegrate(6046,
              Integrate(
                  Times(Plus(Times(ArcTanh(Times(c_DEFAULT, x_)), b_DEFAULT), a_),
                      Power(Times(f_DEFAULT,
                          x_), m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(a,
                          Integrate(Times(Power(Times(f, x), m),
                              Power(Plus(d, Times(e, Sqr(x))), q)), x),
                          x),
                      Dist(b,
                          Integrate(
                              Times(
                                  Power(Times(f, x), m), Power(Plus(d, Times(e, Sqr(x))),
                                      q),
                                  ArcTanh(Times(c, x))),
                              x),
                          x)),
                  FreeQ(List(a, b, c, d, e, f, m, q), x))),
          IIntegrate(6047,
              Integrate(Times(Plus(Times(ArcCoth(Times(c_DEFAULT, x_)), b_DEFAULT), a_),
                  Power(Times(f_DEFAULT, x_), m_DEFAULT), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))),
                      q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(Dist(a,
                      Integrate(Times(Power(Times(f, x), m), Power(Plus(d, Times(e, Sqr(x))), q)),
                          x),
                      x),
                      Dist(b,
                          Integrate(
                              Times(Power(Times(f, x), m), Power(Plus(d, Times(e, Sqr(x))), q),
                                  ArcCoth(Times(c, x))),
                              x),
                          x)),
                  FreeQ(List(a, b, c, d, e, f, m, q), x))),
          IIntegrate(6048,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcTanh(Times(c_DEFAULT, x_)), b_DEFAULT)), p_DEFAULT),
                      Power(Plus(f_, Times(g_DEFAULT, x_)), m_DEFAULT), Power(
                          Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Times(
                              Power(Plus(a, Times(b, ArcTanh(Times(c, x)))), p), Power(
                                  Plus(d, Times(e, Sqr(x))), CN1)),
                          Power(Plus(f, Times(g, x)), m), x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g), x), IGtQ(p, C0), EqQ(Plus(Times(Sqr(c), d),
                      e), C0), IGtQ(m,
                          C0)))),
          IIntegrate(6049,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcCoth(Times(c_DEFAULT, x_)), b_DEFAULT)), p_DEFAULT),
                      Power(Plus(f_, Times(g_DEFAULT, x_)), m_DEFAULT), Power(
                          Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Times(Power(Plus(a, Times(b, ArcCoth(Times(c, x)))), p),
                              Power(Plus(d, Times(e, Sqr(x))), CN1)),
                          Power(Plus(f, Times(g, x)), m), x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g), x), IGtQ(p, C0), EqQ(Plus(Times(Sqr(c), d),
                      e), C0), IGtQ(m,
                          C0)))),
          IIntegrate(6050,
              Integrate(
                  Times(ArcTanh(u_),
                      Power(
                          Plus(a_DEFAULT,
                              Times(ArcTanh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Subtract(Dist(C1D2,
                      Integrate(Times(Log(Plus(C1, u)),
                          Power(Plus(a, Times(b, ArcTanh(Times(c, x)))),
                              p),
                          Power(Plus(d, Times(e, Sqr(x))), CN1)), x),
                      x),
                      Dist(C1D2,
                          Integrate(
                              Times(Log(Subtract(C1, u)),
                                  Power(Plus(a, Times(b, ArcTanh(Times(c, x)))),
                                      p),
                                  Power(Plus(d, Times(e, Sqr(x))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), IGtQ(p, C0),
                      EqQ(Plus(Times(Sqr(c), d),
                          e), C0),
                      EqQ(Subtract(Sqr(u),
                          Sqr(Subtract(C1, Times(C2, Power(Plus(C1, Times(c, x)), CN1))))),
                          C0)))),
          IIntegrate(6051,
              Integrate(
                  Times(ArcCoth(u_),
                      Power(
                          Plus(a_DEFAULT,
                              Times(ArcCoth(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Subtract(Dist(C1D2,
                      Integrate(Times(Log(SimplifyIntegrand(Plus(C1, Power(u, CN1)), x)),
                          Power(Plus(a, Times(b, ArcCoth(Times(c, x)))),
                              p),
                          Power(Plus(d, Times(e, Sqr(x))), CN1)), x),
                      x),
                      Dist(C1D2,
                          Integrate(Times(Log(SimplifyIntegrand(Subtract(C1, Power(u, CN1)), x)),
                              Power(Plus(a, Times(b, ArcCoth(Times(c, x)))),
                                  p),
                              Power(Plus(d, Times(e, Sqr(x))), CN1)), x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), IGtQ(p, C0),
                      EqQ(Plus(Times(Sqr(c), d), e), C0),
                      EqQ(Subtract(Sqr(u),
                          Sqr(Subtract(C1, Times(C2, Power(Plus(C1, Times(c, x)), CN1))))),
                          C0)))),
          IIntegrate(6052,
              Integrate(
                  Times(ArcTanh(u_),
                      Power(Plus(a_DEFAULT,
                          Times(ArcTanh(Times(c_DEFAULT, x_)), b_DEFAULT)), p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Subtract(Dist(C1D2,
                      Integrate(Times(Log(Plus(C1, u)),
                          Power(Plus(a, Times(b, ArcTanh(Times(c, x)))),
                              p),
                          Power(Plus(d, Times(e, Sqr(x))), CN1)), x),
                      x),
                      Dist(C1D2,
                          Integrate(Times(Log(Subtract(C1, u)),
                              Power(Plus(a, Times(b, ArcTanh(Times(c, x)))),
                                  p),
                              Power(Plus(d, Times(e, Sqr(x))), CN1)), x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), IGtQ(p, C0),
                      EqQ(Plus(Times(Sqr(c), d),
                          e), C0),
                      EqQ(Subtract(
                          Sqr(u), Sqr(Subtract(C1, Times(C2,
                              Power(Subtract(C1, Times(c, x)), CN1))))),
                          C0)))),
          IIntegrate(6053,
              Integrate(
                  Times(ArcCoth(u_),
                      Power(
                          Plus(a_DEFAULT,
                              Times(ArcCoth(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(C1D2,
                          Integrate(
                              Times(Log(SimplifyIntegrand(Plus(C1, Power(u, CN1)), x)),
                                  Power(Plus(a, Times(b, ArcCoth(Times(c, x)))),
                                      p),
                                  Power(Plus(d, Times(e, Sqr(x))), CN1)),
                              x),
                          x),
                      Dist(C1D2,
                          Integrate(
                              Times(Log(SimplifyIntegrand(Subtract(C1, Power(u, CN1)), x)),
                                  Power(Plus(a, Times(b, ArcCoth(Times(c, x)))),
                                      p),
                                  Power(Plus(d, Times(e, Sqr(x))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), IGtQ(p, C0),
                      EqQ(Plus(Times(Sqr(c), d),
                          e), C0),
                      EqQ(Subtract(Sqr(u),
                          Sqr(Subtract(C1, Times(C2, Power(Subtract(C1, Times(c, x)), CN1))))),
                          C0)))),
          IIntegrate(6054,
              Integrate(
                  Times(Log(Plus(f_, Times(g_DEFAULT, x_))),
                      Power(
                          Plus(a_DEFAULT,
                              Times(ArcTanh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              Power(Plus(a, Times(b, ArcTanh(Times(c, x)))), Plus(p, C1)),
                              Log(Plus(f, Times(g, x))), Power(Times(b, c, d, Plus(p, C1)), CN1)),
                          x),
                      Dist(
                          Times(g, Power(Times(b, c, d, Plus(p, C1)),
                              CN1)),
                          Integrate(
                              Times(Power(Plus(a, Times(b, ArcTanh(Times(c, x)))), Plus(p, C1)),
                                  Power(Plus(f, Times(g, x)), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g), x), IGtQ(p, C0),
                      EqQ(Plus(Times(Sqr(c), d),
                          e), C0),
                      EqQ(Subtract(Times(Sqr(c), Sqr(f)), Sqr(g)), C0)))),
          IIntegrate(6055,
              Integrate(
                  Times(Log(Plus(f_, Times(g_DEFAULT, x_))),
                      Power(
                          Plus(a_DEFAULT,
                              Times(ArcCoth(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              Power(Plus(a, Times(b, ArcCoth(Times(c, x)))), Plus(p, C1)),
                              Log(Plus(f, Times(g, x))), Power(Times(b, c, d, Plus(p, C1)), CN1)),
                          x),
                      Dist(
                          Times(g, Power(Times(b, c, d, Plus(p, C1)),
                              CN1)),
                          Integrate(
                              Times(
                                  Power(Plus(a, Times(b, ArcCoth(Times(c, x)))), Plus(p,
                                      C1)),
                                  Power(Plus(f, Times(g, x)), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g), x), IGtQ(p, C0),
                      EqQ(Plus(Times(Sqr(c), d),
                          e), C0),
                      EqQ(Subtract(Times(Sqr(c), Sqr(f)), Sqr(g)), C0)))),
          IIntegrate(6056,
              Integrate(
                  Times(Log(u_),
                      Power(
                          Plus(a_DEFAULT,
                              Times(ArcTanh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              Power(Plus(a,
                                  Times(b, ArcTanh(Times(c, x)))), p),
                              PolyLog(C2, Subtract(C1, u)), Power(Times(C2, c, d), CN1)),
                          x),
                      Dist(Times(C1D2, b, p),
                          Integrate(
                              Times(Power(Plus(a, Times(b, ArcTanh(Times(c, x)))), Subtract(p, C1)),
                                  PolyLog(C2, Subtract(C1, u)),
                                  Power(Plus(d, Times(e, Sqr(x))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), IGtQ(p, C0),
                      EqQ(Plus(Times(Sqr(c), d),
                          e), C0),
                      EqQ(Subtract(
                          Sqr(Subtract(C1, u)), Sqr(Subtract(C1,
                              Times(C2, Power(Plus(C1, Times(c, x)), CN1))))),
                          C0)))),
          IIntegrate(6057,
              Integrate(
                  Times(Log(u_),
                      Power(
                          Plus(a_DEFAULT,
                              Times(ArcCoth(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              Power(Plus(a,
                                  Times(b, ArcCoth(Times(c, x)))), p),
                              PolyLog(C2, Subtract(C1, u)), Power(Times(C2, c, d), CN1)),
                          x),
                      Dist(Times(C1D2, b, p),
                          Integrate(
                              Times(Power(Plus(a, Times(b, ArcCoth(Times(c, x)))), Subtract(p, C1)),
                                  PolyLog(C2, Subtract(C1, u)), Power(Plus(d, Times(e, Sqr(x))),
                                      CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), IGtQ(p, C0),
                      EqQ(Plus(Times(Sqr(c), d),
                          e), C0),
                      EqQ(Subtract(
                          Sqr(Subtract(C1, u)), Sqr(Subtract(C1,
                              Times(C2, Power(Plus(C1, Times(c, x)), CN1))))),
                          C0)))),
          IIntegrate(6058,
              Integrate(
                  Times(Log(u_),
                      Power(
                          Plus(a_DEFAULT,
                              Times(ArcTanh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(Power(Plus(a, Times(b, ArcTanh(Times(c, x)))), p), PolyLog(C2,
                                  Subtract(C1, u)), Power(Times(C2, c, d), CN1)),
                              x)),
                      Dist(Times(C1D2, b, p),
                          Integrate(
                              Times(Power(Plus(a, Times(b, ArcTanh(Times(c, x)))), Subtract(p, C1)),
                                  PolyLog(C2, Subtract(C1, u)), Power(Plus(d, Times(e, Sqr(x))),
                                      CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), IGtQ(p, C0),
                      EqQ(Plus(Times(Sqr(c), d),
                          e), C0),
                      EqQ(Subtract(
                          Sqr(Subtract(C1, u)), Sqr(Subtract(C1, Times(C2,
                              Power(Subtract(C1, Times(c, x)), CN1))))),
                          C0)))),
          IIntegrate(6059,
              Integrate(
                  Times(Log(u_),
                      Power(
                          Plus(a_DEFAULT,
                              Times(ArcCoth(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(
                                  Power(Plus(a, Times(b, ArcCoth(Times(c, x)))), p), PolyLog(C2,
                                      Subtract(C1, u)),
                                  Power(Times(C2, c, d), CN1)),
                              x)),
                      Dist(Times(C1D2, b, p),
                          Integrate(
                              Times(Power(Plus(a, Times(b, ArcCoth(Times(c, x)))), Subtract(p, C1)),
                                  PolyLog(C2, Subtract(C1, u)),
                                  Power(Plus(d, Times(e, Sqr(x))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), IGtQ(p, C0),
                      EqQ(Plus(Times(Sqr(c), d), e), C0),
                      EqQ(Subtract(Sqr(Subtract(C1, u)),
                          Sqr(Subtract(C1, Times(C2, Power(Subtract(C1, Times(c, x)), CN1))))),
                          C0)))),
          IIntegrate(6060,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcTanh(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1), PolyLog(k_, u_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(Power(Plus(a, Times(b, ArcTanh(Times(c, x)))), p),
                                  PolyLog(Plus(k, C1), u), Power(Times(C2, c, d), CN1)),
                              x)),
                      Dist(Times(C1D2, b, p),
                          Integrate(
                              Times(Power(Plus(a, Times(b, ArcTanh(Times(c, x)))), Subtract(p, C1)),
                                  PolyLog(Plus(k, C1), u), Power(Plus(d, Times(e, Sqr(x))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, k), x), IGtQ(p, C0),
                      EqQ(Plus(Times(Sqr(c), d), e), C0), EqQ(
                          Subtract(Sqr(u),
                              Sqr(Subtract(C1, Times(C2, Power(Plus(C1, Times(c, x)), CN1))))),
                          C0)))));
}
