package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.ArcTan;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D3;
import static org.matheclipse.core.expression.F.C1DSqrt3;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.C5;
import static org.matheclipse.core.expression.F.C9;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D3;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.CN4;
import static org.matheclipse.core.expression.F.CSqrt3;
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
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.QQ;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.g_;
import static org.matheclipse.core.expression.F.g_DEFAULT;
import static org.matheclipse.core.expression.F.h_DEFAULT;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.q_;
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
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.q;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FracPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ILtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntegersQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules50 {
  public static IAST RULES =
      List(
          IIntegrate(1001,
              Integrate(
                  Times(Power(Plus(g_DEFAULT, Times(h_DEFAULT, x_)), m_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, x_),
                          Times(c_DEFAULT, Sqr(x_))), p_),
                      Power(Plus(d_, Times(f_DEFAULT, Sqr(x_))), q_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Plus(a, Times(b, x), Times(c, Sqr(x))), FracPart(p)),
                          Power(
                              Times(
                                  Power(Times(C4, c), IntPart(p)), Power(Plus(b, Times(C2, c, x)),
                                      Times(C2, FracPart(p)))),
                              CN1)),
                      Integrate(
                          Times(Power(Plus(g, Times(h, x)), m),
                              Power(Plus(b, Times(C2, c, x)), Times(C2, p)),
                              Power(Plus(d, Times(f, Sqr(x))), q)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, f, g, h, m, p, q), x),
                      EqQ(Subtract(Sqr(b), Times(C4, a, c)), C0)))),
          IIntegrate(1002,
              Integrate(
                  Times(Power(Plus(g_, Times(h_DEFAULT, x_)), m_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_),
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_), Times(f_DEFAULT, Sqr(x_))),
                          m_DEFAULT)),
                  x_Symbol),
              Condition(Integrate(
                  Times(Power(Plus(Times(d, g, Power(a, CN1)), Times(f, h, x, Power(c, CN1))), m),
                      Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(m, p))),
                  x),
                  And(FreeQ(List(a, b, c, d, e, f, g, h, p), x),
                      EqQ(Plus(Times(c, Sqr(g)), Times(CN1, b, g, h),
                          Times(a, Sqr(h))), C0),
                      EqQ(Plus(Times(Sqr(c), d, Sqr(g)), Times(CN1, a, c, e, g, h),
                          Times(Sqr(a), f, Sqr(h))), C0),
                      IntegerQ(m)))),
          IIntegrate(1003,
              Integrate(
                  Times(Power(Plus(g_, Times(h_DEFAULT, x_)), m_DEFAULT),
                      Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_),
                      Power(
                          Plus(d_DEFAULT, Times(e_DEFAULT, x_),
                              Times(f_DEFAULT, Sqr(x_))),
                          m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          Power(Plus(Times(d, g, Power(a, CN1)),
                              Times(f, h, x, Power(c, CN1))), m),
                          Power(Plus(a, Times(c, Sqr(x))), Plus(m, p))),
                      x),
                  And(FreeQ(List(a, c, d, e, f, g, h, p), x),
                      EqQ(Plus(Times(c, Sqr(g)),
                          Times(a, Sqr(h))), C0),
                      EqQ(Plus(Times(Sqr(c), d, Sqr(g)), Times(CN1, a, c, e, g, h),
                          Times(Sqr(a), f, Sqr(h))), C0),
                      IntegerQ(m)))),
          IIntegrate(1004,
              Integrate(Times(Power(Plus(g_, Times(h_DEFAULT, x_)), m_DEFAULT),
                  Power(Plus(a_, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_), Power(
                      Plus(d_DEFAULT, Times(f_DEFAULT, Sqr(x_))), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          Power(Plus(Times(d, g, Power(a, CN1)),
                              Times(f, h, x, Power(c, CN1))), m),
                          Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(m, p))),
                      x),
                  And(FreeQ(List(a, b, c, d, f, g, h, p), x),
                      EqQ(Plus(Times(c, Sqr(g)), Times(CN1, b, g, h), Times(a, Sqr(h))), C0),
                      EqQ(Plus(Times(Sqr(c), d, Sqr(g)), Times(Sqr(a), f, Sqr(h))), C0),
                      IntegerQ(m)))),
          IIntegrate(1005,
              Integrate(
                  Times(Power(Plus(g_, Times(h_DEFAULT, x_)), m_DEFAULT),
                      Power(Plus(a_, Times(c_DEFAULT,
                          Sqr(x_))), p_),
                      Power(Plus(d_DEFAULT, Times(f_DEFAULT, Sqr(x_))), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          Power(Plus(Times(d, g, Power(a, CN1)),
                              Times(f, h, x, Power(c, CN1))), m),
                          Power(Plus(a, Times(c, Sqr(x))), Plus(m, p))),
                      x),
                  And(FreeQ(List(a, c, d, f, g, h, p), x),
                      EqQ(Plus(Times(c, Sqr(g)), Times(a, Sqr(h))), C0),
                      EqQ(Plus(Times(Sqr(c), d, Sqr(g)), Times(Sqr(a), f, Sqr(h))), C0),
                      IntegerQ(m)))),
          IIntegrate(1006, Integrate(Times(Power(x_, p_),
              Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_), Power(
                  Plus(Times(e_DEFAULT, x_), Times(f_DEFAULT, Sqr(x_))), q_)),
              x_Symbol),
              Condition(
                  Integrate(
                      Times(Power(Plus(Times(a, Power(e, CN1)), Times(c, x, Power(f, CN1))), p),
                          Power(Plus(Times(e, x), Times(f, Sqr(x))), Plus(p, q))),
                      x),
                  And(FreeQ(List(a, b, c, e, f, q), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      EqQ(Plus(Times(c, Sqr(e)), Times(CN1, b, e, f), Times(a, Sqr(f))), C0),
                      IntegerQ(p)))),
          IIntegrate(1007,
              Integrate(
                  Times(Power(x_, p_), Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_), Power(Plus(
                      Times(e_DEFAULT, x_), Times(f_DEFAULT, Sqr(x_))), q_)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          Power(Plus(Times(a, Power(e, CN1)), Times(c, x, Power(f, CN1))),
                              p),
                          Power(Plus(Times(e, x), Times(f, Sqr(x))), Plus(p, q))),
                      x),
                  And(FreeQ(List(a, c, e, f, q), x), EqQ(Plus(Times(c, Sqr(
                      e)), Times(a,
                          Sqr(f))),
                      C0), IntegerQ(p)))),
          IIntegrate(1008,
              Integrate(
                  Times(Plus(g_, Times(h_DEFAULT, x_)),
                      Power(Plus(a_,
                          Times(c_DEFAULT, Sqr(x_))), CN1D3),
                      Power(Plus(d_, Times(f_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(Plus(
                  Simp(
                      Times(CSqrt3, h,
                          ArcTan(
                              Subtract(C1DSqrt3,
                                  Times(Power(C2, QQ(2L, 3L)),
                                      Power(Subtract(C1, Times(C3, h, x, Power(g, CN1))),
                                          QQ(2L, 3L)),
                                      Power(
                                          Times(CSqrt3,
                                              Power(Plus(C1, Times(C3, h, x, Power(g, CN1))),
                                                  C1D3)),
                                          CN1)))),
                          Power(Times(Power(C2, QQ(2L, 3L)), Power(a, C1D3), f), CN1)),
                      x),
                  Negate(
                      Simp(
                          Times(C3, h,
                              Log(Plus(
                                  Power(Subtract(C1, Times(C3, h, x, Power(g, CN1))), QQ(2L, 3L)),
                                  Times(Power(C2, C1D3),
                                      Power(Plus(C1, Times(C3, h, x, Power(g, CN1))), C1D3)))),
                              Power(Times(Power(C2, QQ(5L, 3L)), Power(a, C1D3), f), CN1)),
                          x)),
                  Simp(
                      Times(h, Log(Plus(d, Times(f, Sqr(x)))),
                          Power(Times(Power(C2, QQ(5L, 3L)), Power(a, C1D3), f), CN1)),
                      x)),
                  And(FreeQ(List(a, c, d, f, g, h), x), EqQ(Plus(Times(c, d), Times(C3, a,
                      f)), C0), EqQ(Plus(Times(c, Sqr(g)),
                          Times(C9, a, Sqr(h))), C0),
                      GtQ(a, C0)))),
          IIntegrate(1009,
              Integrate(
                  Times(Plus(g_, Times(h_DEFAULT, x_)),
                      Power(Plus(a_,
                          Times(c_DEFAULT, Sqr(x_))), CN1D3),
                      Power(Plus(d_, Times(f_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Plus(C1, Times(c, Sqr(x), Power(a, CN1))), C1D3),
                          Power(Plus(a, Times(c, Sqr(x))), CN1D3)),
                      Integrate(
                          Times(Plus(g, Times(h, x)),
                              Power(Times(Power(Plus(C1, Times(c, Sqr(x), Power(a, CN1))), C1D3),
                                  Plus(d, Times(f, Sqr(x)))), CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, c, d, f, g, h), x), EqQ(Plus(Times(c, d), Times(C3, a, f)),
                      C0), EqQ(Plus(Times(c, Sqr(g)), Times(C9, a, Sqr(h))), C0),
                      Not(GtQ(a, C0))))),
          IIntegrate(1010,
              Integrate(
                  Times(Plus(g_, Times(h_DEFAULT, x_)),
                      Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_), Power(
                          Plus(d_, Times(f_DEFAULT, Sqr(x_))), q_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(g,
                          Integrate(Times(Power(Plus(a, Times(c, Sqr(x))), p),
                              Power(Plus(d, Times(f, Sqr(x))), q)), x),
                          x),
                      Dist(h,
                          Integrate(
                              Times(x, Power(Plus(a, Times(c, Sqr(x))), p),
                                  Power(Plus(d, Times(f, Sqr(x))), q)),
                              x),
                          x)),
                  FreeQ(List(a, c, d, f, g, h, p, q), x))),
          IIntegrate(1011,
              Integrate(
                  Times(Plus(g_DEFAULT, Times(h_DEFAULT, x_)),
                      Power(Plus(a_, Times(b_DEFAULT, x_), Times(c_DEFAULT,
                          Sqr(x_))), p_),
                      Power(Plus(d_, Times(e_DEFAULT, x_), Times(f_DEFAULT, Sqr(x_))), q_)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Times(Power(Plus(a, Times(b, x), Times(c, Sqr(x))), p),
                              Power(Plus(d, Times(e, x), Times(f, Sqr(x))), q),
                              Plus(g, Times(h, x))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, h), x),
                      NeQ(Subtract(Sqr(b), Times(C4, a,
                          c)), C0),
                      NeQ(Subtract(Sqr(e), Times(C4, d, f)), C0), IGtQ(p, C0), IntegerQ(q)))),
          IIntegrate(1012,
              Integrate(Times(Plus(g_DEFAULT, Times(h_DEFAULT, x_)),
                  Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_), Power(Plus(d_,
                      Times(e_DEFAULT, x_), Times(f_DEFAULT, Sqr(x_))), q_)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Times(Power(Plus(a, Times(c, Sqr(x))), p),
                              Power(Plus(d, Times(e, x), Times(f, Sqr(x))), q), Plus(g,
                                  Times(h, x))),
                          x),
                      x),
                  And(FreeQ(List(a, c, d, e, f, g, h), x),
                      NeQ(Subtract(Sqr(e),
                          Times(C4, d, f)), C0),
                      IntegersQ(p, q), Or(GtQ(p, C0), GtQ(q, C0))))),
          IIntegrate(1013,
              Integrate(
                  Times(Plus(g_DEFAULT, Times(h_DEFAULT, x_)),
                      Power(Plus(a_, Times(b_DEFAULT, x_), Times(c_DEFAULT,
                          Sqr(x_))), p_),
                      Power(Plus(d_, Times(e_DEFAULT, x_), Times(f_DEFAULT, Sqr(x_))), q_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              Subtract(Subtract(Times(g, b), Times(C2, a, h)),
                                  Times(Subtract(Times(b, h), Times(C2, g, c)), x)),
                              Power(Plus(a, Times(b, x), Times(c,
                                  Sqr(x))), Plus(p,
                                      C1)),
                              Power(Plus(d, Times(e, x), Times(f, Sqr(x))), q), Power(Times(
                                  Subtract(Sqr(b), Times(C4, a, c)), Plus(p, C1)), CN1)),
                          x),
                      Dist(
                          Power(Times(Subtract(Sqr(b), Times(C4, a,
                              c)), Plus(p,
                                  C1)),
                              CN1),
                          Integrate(
                              Times(Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p, C1)),
                                  Power(Plus(d, Times(e, x), Times(f, Sqr(x))), Subtract(q,
                                      C1)),
                                  Simp(Subtract(
                                      Plus(Times(e, q, Subtract(Times(g, b), Times(C2, a, h))),
                                          Times(
                                              CN1, d, Subtract(Times(b, h), Times(C2, g, c)),
                                              Plus(Times(C2, p), C3)),
                                          Times(Subtract(
                                              Times(C2, f, q,
                                                  Subtract(Times(g, b), Times(C2, a, h))),
                                              Times(e, Subtract(Times(b, h), Times(C2, g, c)),
                                                  Plus(Times(C2, p), q, C3))),
                                              x)),
                                      Times(
                                          f, Subtract(Times(b, h), Times(C2, g, c)), Plus(
                                              Times(C2, p), Times(C2, q), C3),
                                          Sqr(x))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, h), x),
                      NeQ(Subtract(Sqr(b), Times(C4, a,
                          c)), C0),
                      NeQ(Subtract(Sqr(e), Times(C4, d, f)), C0), LtQ(p, CN1), GtQ(q, C0)))),
          IIntegrate(1014,
              Integrate(
                  Times(Plus(g_DEFAULT, Times(h_DEFAULT, x_)),
                      Power(Plus(a_, Times(c_DEFAULT,
                          Sqr(x_))), p_),
                      Power(Plus(d_, Times(e_DEFAULT, x_), Times(f_DEFAULT, Sqr(x_))), q_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Subtract(Times(a, h), Times(g, c, x)),
                              Power(Plus(a, Times(c, Sqr(x))), Plus(p, C1)),
                              Power(Plus(d, Times(e, x), Times(f, Sqr(x))),
                                  q),
                              Power(Times(C2, a, c, Plus(p, C1)), CN1)),
                          x),
                      Dist(
                          Times(C2, Power(Times(C4, a, c, Plus(p, C1)),
                              CN1)),
                          Integrate(
                              Times(
                                  Power(Plus(a, Times(c,
                                      Sqr(x))), Plus(p,
                                          C1)),
                                  Power(Plus(d, Times(e, x), Times(f, Sqr(x))), Subtract(q, C1)),
                                  Simp(Plus(Times(g, c, d, Plus(Times(C2, p), C3)),
                                      Times(CN1, a, h, e, q),
                                      Times(Subtract(Times(g, c, e, Plus(Times(C2, p), q, C3)),
                                          Times(a, C2, h, f, q)), x),
                                      Times(g, c, f, Plus(Times(C2, p), Times(C2, q), C3), Sqr(x))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, c, d, e, f, g, h), x), NeQ(Subtract(Sqr(e),
                      Times(C4, d, f)), C0), LtQ(p,
                          CN1),
                      GtQ(q, C0)))),
          IIntegrate(1015, Integrate(Times(Plus(g_DEFAULT, Times(h_DEFAULT, x_)),
              Power(Plus(a_, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))),
                  p_),
              Power(Plus(d_, Times(f_DEFAULT, Sqr(x_))), q_)), x_Symbol), Condition(
                  Subtract(Simp(Times(
                      Subtract(Subtract(Times(g, b), Times(C2, a, h)),
                          Times(Subtract(Times(b, h), Times(C2, g, c)), x)),
                      Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p, C1)),
                      Power(Plus(d, Times(f, Sqr(x))), q), Power(
                          Times(Subtract(Sqr(b), Times(C4, a, c)), Plus(p, C1)), CN1)),
                      x),
                      Dist(
                          Power(Times(Subtract(Sqr(b), Times(C4, a, c)),
                              Plus(p, C1)), CN1),
                          Integrate(Times(
                              Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p, C1)),
                              Power(Plus(d, Times(f, Sqr(x))), Subtract(q, C1)),
                              Simp(Subtract(
                                  Plus(
                                      Times(CN1, d, Subtract(Times(b, h), Times(C2, g, c)),
                                          Plus(Times(C2, p), C3)),
                                      Times(C2, f, q, Subtract(Times(g, b), Times(C2, a, h)), x)),
                                  Times(f, Subtract(Times(b, h), Times(C2, g, c)),
                                      Plus(Times(C2, p), Times(C2, q), C3), Sqr(x))),
                                  x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, f, g, h), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)),
                      C0), LtQ(p, CN1), GtQ(q, C0)))),
          IIntegrate(1016,
              Integrate(
                  Times(Plus(g_DEFAULT, Times(h_DEFAULT, x_)), Power(
                      Plus(a_, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_),
                      Power(Plus(d_, Times(e_DEFAULT, x_), Times(f_DEFAULT, Sqr(x_))), q_)),
                  x_Symbol),
              Condition(Plus(Simp(Times(
                  Power(Plus(a, Times(b, x), Times(c, Sqr(
                      x))), Plus(p, C1)),
                  Power(Plus(d, Times(e, x), Times(f, Sqr(x))), Plus(q, C1)),
                  Plus(
                      Times(
                          g, c,
                          Subtract(Times(C2, a, c, e), Times(b, Plus(Times(c, d), Times(a, f))))),
                      Times(Subtract(Times(g, b), Times(a, h)), Subtract(Plus(Times(C2, Sqr(c), d),
                          Times(Sqr(b), f)), Times(c, Plus(Times(b, e), Times(C2, a, f))))),
                      Times(c, Subtract(
                          Times(g,
                              Subtract(Plus(Times(C2, Sqr(c), d), Times(Sqr(b), f)),
                                  Times(c, Plus(Times(b, e), Times(C2, a, f))))),
                          Times(h, Plus(Times(b, c, d), Times(CN1, C2, a, c, e), Times(a, b, f)))),
                          x)),
                  Power(
                      Times(
                          Subtract(Sqr(b), Times(C4, a,
                              c)),
                          Subtract(Sqr(Subtract(Times(c, d), Times(a, f))),
                              Times(Subtract(Times(b, d), Times(a, e)),
                                  Subtract(Times(c, e), Times(b, f)))),
                          Plus(p, C1)),
                      CN1)),
                  x),
                  Dist(
                      Power(
                          Times(
                              Subtract(Sqr(b), Times(C4, a,
                                  c)),
                              Subtract(
                                  Sqr(Subtract(Times(c, d),
                                      Times(a, f))),
                                  Times(
                                      Subtract(Times(b, d), Times(a,
                                          e)),
                                      Subtract(Times(c, e), Times(b, f)))),
                              Plus(p, C1)),
                          CN1),
                      Integrate(Times(Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p, C1)),
                          Power(Plus(d, Times(e, x), Times(f, Sqr(x))), q),
                          Simp(
                              Subtract(
                                  Subtract(Subtract(
                                      Plus(Times(Subtract(Times(b, h), Times(C2, g, c)), Subtract(
                                          Sqr(Subtract(Times(c, d), Times(a, f))),
                                          Times(Subtract(Times(b, d), Times(a, e)),
                                              Subtract(Times(c, e), Times(b, f)))),
                                          Plus(p, C1)),
                                          Times(Plus(Times(Sqr(b), g, f),
                                              Times(CN1, b,
                                                  Plus(Times(h, c, d), Times(g, c, e), Times(a, h,
                                                      f))),
                                              Times(C2,
                                                  Subtract(
                                                      Times(g, c,
                                                          Subtract(Times(c, d), Times(a, f))),
                                                      Times(a, CN1, h, c, e)))),
                                              Subtract(
                                                  Times(a, f, Plus(p, C1)), Times(c, d,
                                                      Plus(p, C2))))),
                                      Times(e,
                                          Plus(
                                              Times(g, c,
                                                  Subtract(
                                                      Times(C2, a, c, e), Times(b, Plus(Times(c, d),
                                                          Times(a, f))))),
                                              Times(Subtract(Times(g, b), Times(a, h)),
                                                  Subtract(
                                                      Plus(Times(C2, Sqr(c), d), Times(Sqr(b), f)),
                                                      Times(c, Plus(Times(b, e),
                                                          Times(C2, a, f)))))),
                                          Plus(p, q, C2))),
                                      Times(
                                          Subtract(Times(C2, f, Plus(
                                              Times(g, c,
                                                  Subtract(Times(C2, a, c, e),
                                                      Times(b, Plus(Times(c, d), Times(a, f))))),
                                              Times(Subtract(Times(g, b), Times(a, h)), Subtract(
                                                  Plus(Times(C2, Sqr(c), d), Times(Sqr(b), f)),
                                                  Times(c, Plus(Times(b, e), Times(C2, a, f)))))),
                                              Plus(p, q, C2)),
                                              Times(
                                                  Plus(Times(Sqr(b), g, f),
                                                      Times(CN1, b,
                                                          Plus(Times(h, c, d), Times(g, c, e),
                                                              Times(a, h, f))),
                                                      Times(C2,
                                                          Subtract(
                                                              Times(g, c,
                                                                  Subtract(Times(c, d),
                                                                      Times(a, f))),
                                                              Times(a, CN1, h, c, e)))),
                                                  Subtract(Times(b, f, Plus(p, C1)),
                                                      Times(c, e, Plus(Times(C2, p), q, C4))))),
                                          x)),
                                  Times(c, f,
                                      Plus(Times(Sqr(b), g, f),
                                          Times(CN1, b,
                                              Plus(Times(h, c, d), Times(g, c, e), Times(a, h, f))),
                                          Times(C2,
                                              Plus(Times(g, c, Subtract(Times(c, d), Times(a, f))),
                                                  Times(a, h, c, e)))),
                                      Plus(Times(C2, p), Times(C2, q), C5), Sqr(x))),
                              x)),
                          x),
                      x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, h, q), x),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      NeQ(Subtract(Sqr(e), Times(C4, d, f)), C0), LtQ(p, CN1),
                      NeQ(Subtract(Sqr(Subtract(Times(c, d), Times(a, f))),
                          Times(Subtract(Times(b, d), Times(a, e)),
                              Subtract(Times(c, e), Times(b, f)))),
                          C0),
                      Not(And(Not(IntegerQ(p)), ILtQ(q, CN1)))))),
          IIntegrate(1017,
              Integrate(
                  Times(Plus(g_DEFAULT, Times(h_DEFAULT, x_)),
                      Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_), Power(Plus(d_,
                          Times(e_DEFAULT, x_), Times(f_DEFAULT, Sqr(x_))),
                          q_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Plus(a, Times(c, Sqr(x))), Plus(p, C1)),
                              Power(Plus(d, Times(e, x), Times(f, Sqr(x))), Plus(q,
                                  C1)),
                              Plus(Times(g, c, C2, a, c, e),
                                  Times(
                                      CN1, a, h, Subtract(Times(C2, Sqr(c), d),
                                          Times(c, C2, a, f))),
                                  Times(c,
                                      Subtract(
                                          Times(g,
                                              Subtract(Times(C2, Sqr(c), d), Times(c, C2, a, f))),
                                          Times(h, CN2, a, c, e)),
                                      x)),
                              Power(Times(CN4, a, c,
                                  Plus(Times(a, c,
                                      Sqr(e)), Sqr(Subtract(Times(c, d), Times(a, f)))),
                                  Plus(p, C1)), CN1)),
                          x),
                      Dist(
                          Power(
                              Times(CN4, a, c,
                                  Plus(Times(a, c,
                                      Sqr(e)), Sqr(Subtract(Times(c, d), Times(a, f)))),
                                  Plus(p, C1)),
                              CN1),
                          Integrate(
                              Times(Power(Plus(a, Times(c, Sqr(x))), Plus(p, C1)),
                                  Power(Plus(d, Times(e, x),
                                      Times(f, Sqr(x))), q),
                                  Simp(Subtract(
                                      Subtract(Subtract(
                                          Plus(
                                              Times(CN2, g, c,
                                                  Subtract(Sqr(Subtract(Times(c, d), Times(a, f))),
                                                      Times(CN1, a, e, c, e)),
                                                  Plus(p, C1)),
                                              Times(C2,
                                                  Subtract(
                                                      Times(g, c,
                                                          Subtract(Times(c, d), Times(a, f))),
                                                      Times(a, CN1, h, c, e)),
                                                  Subtract(Times(a, f, Plus(p, C1)),
                                                      Times(c, d, Plus(p, C2))))),
                                          Times(e, Plus(Times(g, c, C2, a, c, e),
                                              Times(CN1, a, h,
                                                  Subtract(Times(C2, Sqr(c), d), Times(c, Plus(C2),
                                                      a, f)))),
                                              Plus(p, q, C2))),
                                          Times(
                                              Subtract(
                                                  Times(
                                                      C2, f,
                                                      Plus(
                                                          Times(g, c, C2, a, c, e),
                                                          Times(
                                                              CN1, a, h,
                                                              Subtract(Times(C2, Sqr(c), d),
                                                                  Times(c, Plus(C2), a, f)))),
                                                      Plus(p, q, C2)),
                                                  Times(C2,
                                                      Subtract(
                                                          Times(g, c,
                                                              Subtract(Times(c, d), Times(a, f))),
                                                          Times(a, CN1, h, c, e)),
                                                      CN1, c, e, Plus(Times(C2, p), q, C4))),
                                              x)),
                                      Times(c, f, C2,
                                          Subtract(Times(g, c, Subtract(Times(c, d), Times(a, f))),
                                              Times(a, CN1, h, c, e)),
                                          Plus(Times(C2, p), Times(C2, q), C5), Sqr(x))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, c, d, e, f, g, h, q), x),
                      NeQ(Subtract(Sqr(e),
                          Times(C4, d, f)), C0),
                      LtQ(p, CN1),
                      NeQ(Plus(Times(a, c, Sqr(e)), Sqr(Subtract(Times(c, d), Times(a, f)))),
                          C0),
                      Not(And(Not(IntegerQ(p)), ILtQ(q, CN1)))))),
          IIntegrate(1018,
              Integrate(
                  Times(Plus(g_DEFAULT, Times(h_DEFAULT, x_)),
                      Power(Plus(a_, Times(b_DEFAULT, x_),
                          Times(c_DEFAULT, Sqr(x_))), p_),
                      Power(Plus(d_, Times(f_DEFAULT, Sqr(x_))), q_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p, C1)),
                              Power(Plus(d, Times(f,
                                  Sqr(x))), Plus(q,
                                      C1)),
                              Plus(Times(g, c, CN1, b, Plus(Times(c, d), Times(a, f))), Times(
                                  Subtract(Times(g, b), Times(a,
                                      h)),
                                  Subtract(Plus(Times(C2, Sqr(c), d), Times(Sqr(b), f)),
                                      Times(c, C2, a, f))),
                                  Times(c,
                                      Subtract(
                                          Times(g,
                                              Subtract(Plus(Times(C2, Sqr(c), d), Times(Sqr(b), f)),
                                                  Times(c, C2, a, f))),
                                          Times(h, Plus(Times(b, c, d), Times(a, b, f)))),
                                      x)),
                              Power(
                                  Times(Subtract(Sqr(b), Times(C4, a, c)),
                                      Plus(Times(Sqr(b), d, f),
                                          Sqr(Subtract(Times(c, d), Times(a, f)))),
                                      Plus(p, C1)),
                                  CN1)),
                          x),
                      Dist(
                          Power(
                              Times(
                                  Subtract(Sqr(b), Times(C4, a,
                                      c)),
                                  Plus(Times(Sqr(b), d,
                                      f), Sqr(Subtract(Times(c, d), Times(a, f)))),
                                  Plus(p, C1)),
                              CN1),
                          Integrate(
                              Times(Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p, C1)),
                                  Power(Plus(d, Times(f, Sqr(x))), q),
                                  Simp(
                                      Subtract(
                                          Subtract(Plus(
                                              Times(Subtract(Times(b, h), Times(C2, g, c)),
                                                  Subtract(Sqr(Subtract(Times(c, d), Times(a, f))),
                                                      Times(b, d, CN1, b, f)),
                                                  Plus(p, C1)),
                                              Times(
                                                  Plus(Times(Sqr(b), g, f),
                                                      Times(CN1, b,
                                                          Plus(Times(h, c, d), Times(a, h, f))),
                                                      Times(C2, g, c,
                                                          Subtract(Times(c, d), Times(a, f)))),
                                                  Subtract(
                                                      Times(a, f, Plus(p, C1)), Times(c, d,
                                                          Plus(p, C2))))),
                                              Times(Subtract(
                                                  Times(C2, f,
                                                      Plus(
                                                          Times(g, c, CN1, b,
                                                              Plus(Times(c, d), Times(a, f))),
                                                          Times(Subtract(Times(g, b), Times(a, h)),
                                                              Subtract(
                                                                  Plus(Times(C2, Sqr(c), d),
                                                                      Times(Sqr(b), f)),
                                                                  Times(c, C2, a, f)))),
                                                      Plus(p, q, C2)),
                                                  Times(
                                                      Plus(Times(Sqr(b), g, f),
                                                          Times(CN1, b,
                                                              Plus(Times(h, c, d), Times(a, h, f))),
                                                          Times(C2, g, c,
                                                              Subtract(Times(c, d), Times(a, f)))),
                                                      b, f, Plus(p, C1))),
                                                  x)),
                                          Times(c, f, Plus(Times(Sqr(b), g, f),
                                              Times(CN1, b, Plus(Times(h, c, d), Times(a, h, f))),
                                              Times(C2, g, c, Subtract(Times(c, d), Times(a, f)))),
                                              Plus(Times(C2, p), Times(C2, q), C5), Sqr(x))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, f, g, h, q), x),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0), LtQ(p, CN1),
                      NeQ(Plus(Times(Sqr(b), d, f), Sqr(Subtract(Times(c, d), Times(a, f)))),
                          C0),
                      Not(And(Not(IntegerQ(p)), ILtQ(q, CN1)))))),
          IIntegrate(1019,
              Integrate(Times(Plus(g_DEFAULT, Times(h_DEFAULT, x_)),
                  Power(Plus(a_, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_), Power(Plus(
                      d_, Times(e_DEFAULT, x_), Times(f_DEFAULT, Sqr(x_))),
                      q_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(h, Power(Plus(a, Times(b, x), Times(c, Sqr(x))), p),
                              Power(Plus(d, Times(e, x), Times(f, Sqr(x))), Plus(q,
                                  C1)),
                              Power(Times(C2, f, Plus(p, q, C1)), CN1)),
                          x),
                      Dist(Power(Times(C2, f, Plus(p, q, C1)), CN1), Integrate(Times(
                          Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Subtract(p, C1)),
                          Power(Plus(d, Times(e, x), Times(f, Sqr(x))), q),
                          Simp(Plus(Times(h, p, Subtract(Times(b, d), Times(a, e))),
                              Times(a, Subtract(Times(h, e), Times(C2, g, f)), Plus(p, q, C1)),
                              Times(Plus(Times(C2, h, p, Subtract(Times(c, d), Times(a, f))),
                                  Times(b, Subtract(Times(h, e), Times(C2, g, f)), Plus(p, q, C1))),
                                  x),
                              Times(Plus(Times(h, p, Subtract(Times(c, e), Times(b, f))),
                                  Times(c, Subtract(Times(h, e), Times(C2, g, f)), Plus(p, q, C1))),
                                  Sqr(x))),
                              x)),
                          x), x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, h, q), x),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      NeQ(Subtract(Sqr(e), Times(C4, d, f)), C0), GtQ(p, C0),
                      NeQ(Plus(p, q, C1), C0)))),
          IIntegrate(1020,
              Integrate(
                  Times(Plus(g_DEFAULT, Times(h_DEFAULT, x_)),
                      Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_),
                      Power(Plus(d_, Times(e_DEFAULT, x_), Times(f_DEFAULT, Sqr(x_))), q_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(h, Power(Plus(a, Times(c, Sqr(x))), p),
                              Power(Plus(d, Times(e, x), Times(f, Sqr(x))), Plus(q,
                                  C1)),
                              Power(Times(C2, f, Plus(p, q, C1)), CN1)),
                          x),
                      Dist(
                          Power(Times(C2, f,
                              Plus(p, q, C1)), CN1),
                          Integrate(Times(Power(Plus(a, Times(c, Sqr(x))), Subtract(p, C1)),
                              Power(Plus(d, Times(e, x), Times(f, Sqr(x))), q),
                              Simp(Subtract(
                                  Subtract(
                                      Subtract(Times(a, h, e, p),
                                          Times(a, Subtract(Times(h, e), Times(C2, g, f)),
                                              Plus(p, q, C1))),
                                      Times(C2, h, p, Subtract(Times(c, d), Times(a, f)), x)),
                                  Times(Plus(Times(h, c, e, p),
                                      Times(c, Subtract(Times(h, e), Times(C2, g, f)),
                                          Plus(p, q, C1))),
                                      Sqr(x))),
                                  x)),
                              x),
                          x)),
                  And(FreeQ(List(a, c, d, e, f, g, h, q), x),
                      NeQ(Subtract(Sqr(e), Times(C4, d, f)), C0), GtQ(p, C0),
                      NeQ(Plus(p, q, C1), C0)))));
}
