package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.ArcCot;
import static org.matheclipse.core.expression.F.ArcTan;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.CI;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.PolyLog;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Set;
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
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.q_;
import static org.matheclipse.core.expression.F.q_DEFAULT;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.f;
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
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntegersQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SimplifyIntegrand;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules243 {
  public static IAST RULES =
      List(
          IIntegrate(4861,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(ArcCot(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          C3),
                      Power(Plus(d_, Times(e_DEFAULT, x_)), CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(Power(Plus(a, Times(b, ArcCot(Times(c, x)))), C3),
                                  Log(Times(C2, Power(Subtract(C1, Times(CI, c, x)), CN1))), Power(
                                      e, CN1)),
                              x)),
                      Simp(
                          Times(
                              Power(Plus(a, Times(b, ArcCot(Times(c, x)))), C3), Log(
                                  Times(C2, c, Plus(d, Times(e, x)),
                                      Power(Times(Plus(Times(c, d), Times(CI, e)),
                                          Subtract(C1, Times(CI, c, x))), CN1))),
                              Power(e, CN1)),
                          x),
                      Negate(
                          Simp(
                              Times(C3, CI, b, Sqr(Plus(a, Times(b, ArcCot(Times(c, x))))),
                                  PolyLog(C2,
                                      Subtract(C1,
                                          Times(C2, Power(Subtract(C1, Times(CI, c, x)), CN1)))),
                                  Power(Times(C2, e), CN1)),
                              x)),
                      Simp(
                          Times(
                              C3, CI, b, Sqr(Plus(a,
                                  Times(b, ArcCot(Times(c, x))))),
                              PolyLog(C2,
                                  Subtract(C1,
                                      Times(
                                          C2, c, Plus(d, Times(e,
                                              x)),
                                          Power(Times(Plus(Times(c, d), Times(CI, e)),
                                              Subtract(C1, Times(CI, c, x))), CN1)))),
                              Power(Times(C2, e), CN1)),
                          x),
                      Negate(Simp(Times(C3, Sqr(b), Plus(a, Times(b, ArcCot(Times(c, x)))),
                          PolyLog(C3,
                              Subtract(C1, Times(C2, Power(Subtract(C1, Times(CI, c, x)), CN1)))),
                          Power(Times(C2, e), CN1)), x)),
                      Simp(Times(C3, Sqr(b), Plus(a, Times(b, ArcCot(Times(c, x)))), PolyLog(C3,
                          Subtract(C1,
                              Times(C2, c, Plus(d, Times(e, x)),
                                  Power(Times(Plus(Times(c, d), Times(CI, e)),
                                      Subtract(C1, Times(CI, c, x))), CN1)))),
                          Power(Times(C2, e), CN1)), x),
                      Simp(Times(C3, CI, Power(b, C3),
                          PolyLog(C4,
                              Subtract(C1, Times(C2, Power(Subtract(C1, Times(CI, c, x)), CN1)))),
                          Power(Times(C4, e), CN1)), x),
                      Negate(
                          Simp(
                              Times(C3, CI, Power(b, C3),
                                  PolyLog(C4,
                                      Subtract(C1,
                                          Times(C2, c, Plus(d, Times(e, x)),
                                              Power(Times(Plus(Times(c, d), Times(CI, e)),
                                                  Subtract(C1, Times(CI, c, x))), CN1)))),
                                  Power(Times(C4, e), CN1)),
                              x))),
                  And(FreeQ(List(a, b, c, d,
                      e), x), NeQ(Plus(Times(Sqr(c), Sqr(d)), Sqr(e)),
                          C0)))),
          IIntegrate(4862,
              Integrate(Times(Plus(a_DEFAULT, Times(ArcTan(Times(c_DEFAULT, x_)), b_DEFAULT)),
                  Power(Plus(d_, Times(e_DEFAULT, x_)), q_DEFAULT)), x_Symbol),
              Condition(
                  Subtract(
                      Simp(Times(Power(Plus(d, Times(e, x)), Plus(q, C1)),
                          Plus(a, Times(b, ArcTan(Times(c, x)))),
                          Power(Times(e, Plus(q, C1)), CN1)), x),
                      Dist(Times(b, c, Power(Times(e, Plus(q, C1)), CN1)),
                          Integrate(Times(Power(Plus(d, Times(e, x)), Plus(q, C1)),
                              Power(Plus(C1, Times(Sqr(c), Sqr(x))), CN1)), x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, q), x), NeQ(q, CN1)))),
          IIntegrate(4863,
              Integrate(
                  Times(
                      Plus(a_DEFAULT, Times(ArcCot(Times(c_DEFAULT, x_)), b_DEFAULT)), Power(
                          Plus(d_, Times(e_DEFAULT, x_)), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Plus(d, Times(e, x)), Plus(q, C1)),
                              Plus(a, Times(b,
                                  ArcCot(Times(c, x)))),
                              Power(Times(e, Plus(q, C1)), CN1)),
                          x),
                      Dist(Times(b, c, Power(Times(e, Plus(q, C1)), CN1)),
                          Integrate(
                              Times(Power(Plus(d, Times(e, x)), Plus(q, C1)), Power(
                                  Plus(C1, Times(Sqr(c), Sqr(x))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, q), x), NeQ(q, CN1)))),
          IIntegrate(4864,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcTan(Times(c_DEFAULT, x_)), b_DEFAULT)), p_),
                      Power(Plus(d_, Times(e_DEFAULT, x_)), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Plus(d, Times(e, x)), Plus(q, C1)),
                              Power(Plus(a,
                                  Times(b, ArcTan(Times(c, x)))), p),
                              Power(Times(e, Plus(q, C1)), CN1)),
                          x),
                      Dist(
                          Times(b, c, p, Power(Times(e, Plus(q, C1)),
                              CN1)),
                          Integrate(
                              ExpandIntegrand(
                                  Power(Plus(a, Times(b,
                                      ArcTan(Times(c, x)))), Subtract(p,
                                          C1)),
                                  Times(Power(Plus(d, Times(e, x)), Plus(q, C1)),
                                      Power(Plus(C1, Times(Sqr(c), Sqr(x))), CN1)),
                                  x),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), IGtQ(p, C1), IntegerQ(q), NeQ(q, CN1)))),
          IIntegrate(4865,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcCot(Times(c_DEFAULT, x_)), b_DEFAULT)), p_),
                      Power(Plus(d_, Times(e_DEFAULT, x_)), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Plus(d, Times(e, x)), Plus(q, C1)),
                              Power(Plus(a, Times(b, ArcCot(Times(c, x)))),
                                  p),
                              Power(Times(e, Plus(q, C1)), CN1)),
                          x),
                      Dist(Times(b, c, p, Power(Times(e, Plus(q, C1)), CN1)),
                          Integrate(ExpandIntegrand(
                              Power(Plus(a, Times(b, ArcCot(Times(c, x)))), Subtract(p, C1)),
                              Times(Power(Plus(d, Times(e, x)), Plus(q, C1)),
                                  Power(Plus(C1, Times(Sqr(c), Sqr(x))), CN1)),
                              x), x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), IGtQ(p, C1), IntegerQ(q), NeQ(q, CN1)))),
          IIntegrate(4866,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcTan(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(Times(f_DEFAULT, x_), m_DEFAULT), Power(Plus(d_,
                          Times(e_DEFAULT, x_)), CN1)),
                  x_Symbol),
              Condition(
                  Subtract(Dist(Times(f, Power(e, CN1)),
                      Integrate(
                          Times(Power(Times(f, x), Subtract(m, C1)),
                              Power(Plus(a, Times(b, ArcTan(Times(c, x)))), p)),
                          x),
                      x),
                      Dist(Times(d, f, Power(e, CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Subtract(m, C1)),
                                  Power(Plus(a, Times(b, ArcTan(Times(c, x)))),
                                      p),
                                  Power(Plus(d, Times(e, x)), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(p, C0),
                      EqQ(Plus(Times(Sqr(c), Sqr(d)), Sqr(e)), C0), GtQ(m, C0)))),
          IIntegrate(4867,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCot(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(Times(f_DEFAULT,
                          x_), m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, x_)), CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Times(f, Power(e, CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Subtract(m, C1)),
                                  Power(Plus(a, Times(b, ArcCot(Times(c, x)))), p)),
                              x),
                          x),
                      Dist(
                          Times(d, f, Power(e,
                              CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Subtract(m, C1)),
                                  Power(Plus(a,
                                      Times(b, ArcCot(Times(c, x)))), p),
                                  Power(Plus(d, Times(e, x)), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(p, C0), EqQ(Plus(Times(Sqr(c), Sqr(d)),
                      Sqr(e)), C0), GtQ(m,
                          C0)))),
          IIntegrate(4868,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcTan(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(x_, CN1), Power(Plus(d_, Times(e_DEFAULT, x_)), CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Plus(a, Times(b, ArcTan(Times(c, x)))), p),
                              Log(Subtract(C2, Times(C2,
                                  Power(Plus(C1, Times(e, x, Power(d, CN1))), CN1)))),
                              Power(d, CN1)),
                          x),
                      Dist(
                          Times(b, c, p, Power(d,
                              CN1)),
                          Integrate(
                              Times(Power(Plus(a, Times(b, ArcTan(Times(c, x)))), Subtract(p,
                                  C1)), Log(
                                      Subtract(C2,
                                          Times(C2, Power(Plus(C1, Times(e, x, Power(d, CN1))),
                                              CN1)))),
                                  Power(Plus(C1, Times(Sqr(c), Sqr(x))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), IGtQ(p, C0),
                      EqQ(Plus(Times(Sqr(c), Sqr(d)), Sqr(e)), C0)))),
          IIntegrate(4869,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCot(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(x_, CN1), Power(Plus(d_, Times(e_DEFAULT, x_)), CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Plus(a, Times(b, ArcCot(Times(c, x)))), p),
                              Log(Subtract(C2, Times(C2,
                                  Power(Plus(C1, Times(e, x, Power(d, CN1))), CN1)))),
                              Power(d, CN1)),
                          x),
                      Dist(Times(b, c, p, Power(d, CN1)),
                          Integrate(
                              Times(Power(Plus(a, Times(b, ArcCot(Times(c, x)))), Subtract(p, C1)),
                                  Log(Subtract(C2, Times(C2,
                                      Power(Plus(C1, Times(e, x, Power(d, CN1))), CN1)))),
                                  Power(Plus(C1, Times(Sqr(c), Sqr(x))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), IGtQ(p, C0),
                      EqQ(Plus(Times(Sqr(c), Sqr(d)), Sqr(e)), C0)))),
          IIntegrate(4870,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcTan(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(Times(f_DEFAULT, x_), m_), Power(Plus(d_, Times(e_DEFAULT, x_)), CN1)),
                  x_Symbol),
              Condition(
                  Subtract(Dist(Power(d, CN1),
                      Integrate(
                          Times(Power(Times(f, x), m),
                              Power(Plus(a, Times(b, ArcTan(Times(c, x)))), p)),
                          x),
                      x),
                      Dist(Times(e, Power(Times(d, f), CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Plus(m, C1)),
                                  Power(Plus(a, Times(b, ArcTan(Times(c, x)))),
                                      p),
                                  Power(Plus(d, Times(e, x)), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(p, C0),
                      EqQ(Plus(Times(Sqr(c), Sqr(d)), Sqr(e)), C0), LtQ(m, CN1)))),
          IIntegrate(4871,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCot(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(Times(f_DEFAULT, x_), m_), Power(Plus(d_, Times(e_DEFAULT, x_)), CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Power(d, CN1),
                          Integrate(
                              Times(Power(Times(f, x), m),
                                  Power(Plus(a, Times(b, ArcCot(Times(c, x)))), p)),
                              x),
                          x),
                      Dist(
                          Times(e, Power(Times(d, f),
                              CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Plus(m, C1)),
                                  Power(Plus(a, Times(b, ArcCot(Times(c, x)))),
                                      p),
                                  Power(Plus(d, Times(e, x)), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(p, C0), EqQ(Plus(Times(Sqr(c), Sqr(d)),
                      Sqr(e)), C0), LtQ(m,
                          CN1)))),
          IIntegrate(4872,
              Integrate(
                  Times(Plus(a_DEFAULT, Times(ArcTan(Times(c_DEFAULT, x_)), b_DEFAULT)),
                      Power(Times(f_DEFAULT,
                          x_), m_DEFAULT),
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(u,
                          IntHide(Times(Power(Times(f, x), m), Power(Plus(d, Times(e, x)), q)),
                              x))),
                      Subtract(Dist(Plus(a, Times(b, ArcTan(Times(c, x)))), u, x),
                          Dist(Times(b, c),
                              Integrate(SimplifyIntegrand(
                                  Times(u, Power(Plus(C1, Times(Sqr(c), Sqr(x))), CN1)), x), x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, f, q), x), NeQ(q, CN1), IntegerQ(Times(C2, m)),
                      Or(And(IGtQ(m, C0), IGtQ(q,
                          C0)), And(ILtQ(Plus(m, q, C1), C0),
                              LtQ(Times(m, q), C0)))))),
          IIntegrate(4873,
              Integrate(
                  Times(Plus(a_DEFAULT, Times(ArcCot(Times(c_DEFAULT, x_)), b_DEFAULT)),
                      Power(Times(f_DEFAULT,
                          x_), m_DEFAULT),
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(u,
                          IntHide(Times(Power(Times(f, x), m), Power(Plus(d, Times(e, x)), q)),
                              x))),
                      Plus(
                          Dist(Plus(a,
                              Times(b, ArcCot(Times(c, x)))), u, x),
                          Dist(Times(b, c),
                              Integrate(SimplifyIntegrand(
                                  Times(u, Power(Plus(C1, Times(Sqr(c), Sqr(x))), CN1)), x), x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, f, q), x), NeQ(q, CN1), IntegerQ(Times(C2,
                      m)), Or(
                          And(IGtQ(m, C0), IGtQ(q,
                              C0)),
                          And(ILtQ(Plus(m, q, C1), C0), LtQ(Times(m, q), C0)))))),
          IIntegrate(4874,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(ArcTan(Times(c_DEFAULT, x_)),
                          b_DEFAULT)), p_),
                      Power(Times(f_DEFAULT,
                          x_), m_DEFAULT),
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), q_)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(u,
                              IntHide(
                                  Times(Power(Times(f, x), m),
                                      Power(Plus(d, Times(e, x)), q)),
                                  x))),
                      Subtract(Dist(Power(Plus(a, Times(b, ArcTan(Times(c, x)))), p), u, x),
                          Dist(Times(b, c, p),
                              Integrate(
                                  ExpandIntegrand(
                                      Power(Plus(a, Times(b, ArcTan(Times(c, x)))),
                                          Subtract(p, C1)),
                                      Times(u, Power(Plus(C1, Times(Sqr(c), Sqr(x))), CN1)), x),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, f, q), x), IGtQ(p, C1),
                      EqQ(Plus(Times(Sqr(c), Sqr(d)),
                          Sqr(e)), C0),
                      IntegersQ(m, q), NeQ(m, CN1), NeQ(q, CN1), ILtQ(Plus(m, q,
                          C1), C0),
                      LtQ(Times(m, q), C0)))),
          IIntegrate(4875,
              Integrate(
                  Times(Power(Plus(a_DEFAULT, Times(ArcCot(Times(c_DEFAULT, x_)), b_DEFAULT)), p_),
                      Power(Times(f_DEFAULT,
                          x_), m_DEFAULT),
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), q_)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(u,
                              IntHide(
                                  Times(Power(Times(f, x), m),
                                      Power(Plus(d, Times(e, x)), q)),
                                  x))),
                      Plus(
                          Dist(Power(Plus(a, Times(b, ArcCot(Times(c, x)))),
                              p), u, x),
                          Dist(Times(b, c, p),
                              Integrate(ExpandIntegrand(
                                  Power(Plus(a, Times(b, ArcCot(Times(c, x)))), Subtract(p, C1)),
                                  Times(u, Power(Plus(C1, Times(Sqr(c), Sqr(x))), CN1)), x), x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, f, q), x), IGtQ(p, C1),
                      EqQ(Plus(Times(Sqr(c), Sqr(d)), Sqr(e)), C0), IntegersQ(m, q), NeQ(m, CN1),
                      NeQ(q, CN1), ILtQ(Plus(m, q, C1), C0), LtQ(Times(m, q), C0)))),
          IIntegrate(4876,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcTan(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(Times(f_DEFAULT,
                          x_), m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, x_)), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Power(Plus(a, Times(b, ArcTan(Times(c, x)))), p), Times(
                              Power(Times(f, x), m), Power(Plus(d, Times(e, x)), q)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m), x), IGtQ(p, C0), IntegerQ(q),
                      Or(GtQ(q, C0), NeQ(a, C0), IntegerQ(m))))),
          IIntegrate(4877,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCot(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(Times(f_DEFAULT,
                          x_), m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, x_)), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Power(Plus(a, Times(b, ArcCot(Times(c, x)))), p), Times(
                              Power(Times(f, x), m), Power(Plus(d, Times(e, x)), q)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m), x), IGtQ(p, C0), IntegerQ(q),
                      Or(GtQ(q, C0), NeQ(a, C0), IntegerQ(m))))),
          IIntegrate(4878,
              Integrate(
                  Times(Plus(a_DEFAULT, Times(ArcTan(Times(c_DEFAULT, x_)), b_DEFAULT)),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(b, Power(Plus(d, Times(e, Sqr(x))), q),
                                  Power(Times(C2, c, q, Plus(Times(C2, q), C1)), CN1)),
                              x)),
                      Dist(
                          Times(C2, d, q, Power(Plus(Times(C2, q), C1),
                              CN1)),
                          Integrate(
                              Times(Power(Plus(d, Times(e, Sqr(x))), Subtract(q, C1)),
                                  Plus(a, Times(b, ArcTan(Times(c, x))))),
                              x),
                          x),
                      Simp(
                          Times(x, Power(Plus(d, Times(e, Sqr(x))), q), Plus(a,
                              Times(b, ArcTan(Times(c, x)))), Power(Plus(Times(C2, q), C1), CN1)),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(e, Times(Sqr(c), d)), GtQ(q, C0)))),
          IIntegrate(4879,
              Integrate(
                  Times(Plus(a_DEFAULT, Times(ArcCot(Times(c_DEFAULT, x_)), b_DEFAULT)),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(b, Power(Plus(d, Times(e, Sqr(x))), q),
                              Power(Times(C2, c, q, Plus(Times(C2, q), C1)), CN1)),
                          x),
                      Dist(
                          Times(C2, d, q, Power(Plus(Times(C2, q), C1),
                              CN1)),
                          Integrate(
                              Times(Power(Plus(d, Times(e, Sqr(x))), Subtract(q, C1)),
                                  Plus(a, Times(b, ArcCot(Times(c, x))))),
                              x),
                          x),
                      Simp(
                          Times(x, Power(Plus(d, Times(e, Sqr(x))), q),
                              Plus(a, Times(b, ArcCot(Times(c, x)))),
                              Power(Plus(Times(C2, q), C1), CN1)),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(e, Times(Sqr(c), d)), GtQ(q, C0)))),
          IIntegrate(4880,
              Integrate(
                  Times(Power(Plus(a_DEFAULT, Times(ArcTan(Times(c_DEFAULT, x_)), b_DEFAULT)), p_),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(b, p, Power(Plus(d, Times(e, Sqr(x))), q),
                                  Power(Plus(a, Times(b, ArcTan(Times(c, x)))), Subtract(p,
                                      C1)),
                                  Power(Times(C2, c, q, Plus(Times(C2, q), C1)), CN1)),
                              x)),
                      Dist(
                          Times(C2, d, q, Power(Plus(Times(C2, q), C1),
                              CN1)),
                          Integrate(
                              Times(
                                  Power(Plus(d, Times(e, Sqr(x))), Subtract(q,
                                      C1)),
                                  Power(Plus(a, Times(b, ArcTan(Times(c, x)))), p)),
                              x),
                          x),
                      Dist(
                          Times(Sqr(b), d, p, Subtract(p, C1),
                              Power(Times(C2, q, Plus(Times(C2, q), C1)), CN1)),
                          Integrate(
                              Times(Power(Plus(d, Times(e, Sqr(x))), Subtract(q, C1)),
                                  Power(Plus(a, Times(b, ArcTan(Times(c, x)))), Subtract(p, C2))),
                              x),
                          x),
                      Simp(Times(x, Power(Plus(d, Times(e, Sqr(x))), q),
                          Power(Plus(a, Times(b, ArcTan(Times(c, x)))), p),
                          Power(Plus(Times(C2, q), C1), CN1)), x)),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(e, Times(Sqr(c), d)), GtQ(q, C0),
                      GtQ(p, C1)))));
}
