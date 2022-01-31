package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.ArcCot;
import static org.matheclipse.core.expression.F.ArcTan;
import static org.matheclipse.core.expression.F.ArcTanh;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.CI;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Csc;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.PolyLog;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sec;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_;
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
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RationalQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules247 {
  public static IAST RULES =
      List(
          IIntegrate(4941,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(ArcCot(Times(c_DEFAULT, x_)),
                          b_DEFAULT)), p_),
                      Power(Times(f_DEFAULT, x_), m_), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))),
                          q_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(b, p, Power(Times(f, x), m),
                                  Power(Plus(d, Times(e,
                                      Sqr(x))), Plus(q,
                                          C1)),
                                  Power(Plus(a, Times(b, ArcCot(Times(c, x)))), Subtract(p,
                                      C1)),
                                  Power(Times(c, d, Sqr(m)), CN1)),
                              x)),
                      Dist(
                          Times(Sqr(f), Subtract(m, C1), Power(Times(Sqr(c), d, m),
                              CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Subtract(m, C2)),
                                  Power(Plus(d, Times(e, Sqr(x))), Plus(q,
                                      C1)),
                                  Power(Plus(a, Times(b, ArcCot(Times(c, x)))), p)),
                              x),
                          x),
                      Negate(
                          Dist(Times(Sqr(b), p, Subtract(p, C1), Power(m, CN2)),
                              Integrate(
                                  Times(Power(Times(f, x), m), Power(Plus(d, Times(e, Sqr(x))), q),
                                      Power(Plus(a, Times(b, ArcCot(Times(c, x)))),
                                          Subtract(p, C2))),
                                  x),
                              x)),
                      Negate(
                          Simp(Times(f, Power(Times(f, x), Subtract(m, C1)),
                              Power(Plus(d, Times(e, Sqr(x))), Plus(q, C1)),
                              Power(Plus(a, Times(b, ArcCot(Times(c, x)))),
                                  p),
                              Power(Times(Sqr(c), d, m), CN1)), x))),
                  And(FreeQ(List(a, b, c, d, e, f, m), x), EqQ(e, Times(Sqr(
                      c), d)), EqQ(Plus(m, Times(C2, q),
                          C2), C0),
                      LtQ(q, CN1), GtQ(p, C1)))),
          IIntegrate(4942,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(ArcTan(Times(c_DEFAULT, x_)),
                          b_DEFAULT)), p_),
                      Power(Times(f_DEFAULT,
                          x_), m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Times(f, x), m),
                              Power(Plus(d, Times(e, Sqr(x))), Plus(q, C1)),
                              Power(Plus(a, Times(b, ArcTan(Times(c, x)))),
                                  Plus(p, C1)),
                              Power(Times(b, c, d, Plus(p, C1)), CN1)),
                          x),
                      Dist(
                          Times(f, m, Power(Times(b, c, Plus(p, C1)),
                              CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Subtract(m, C1)),
                                  Power(Plus(d, Times(e, Sqr(x))), q),
                                  Power(Plus(a, Times(b, ArcTan(Times(c, x)))), Plus(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, m, q), x), EqQ(e, Times(Sqr(c), d)),
                      EqQ(Plus(m, Times(C2, q), C2), C0), LtQ(p, CN1)))),
          IIntegrate(4943,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(ArcCot(Times(c_DEFAULT, x_)),
                          b_DEFAULT)), p_),
                      Power(Times(f_DEFAULT,
                          x_), m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(Power(Times(f, x), m),
                                  Power(Plus(d, Times(e, Sqr(x))), Plus(q, C1)),
                                  Power(Plus(a, Times(b, ArcCot(Times(c, x)))), Plus(p, C1)),
                                  Power(Times(b, c, d, Plus(p, C1)), CN1)),
                              x)),
                      Dist(
                          Times(f, m, Power(Times(b, c, Plus(p, C1)),
                              CN1)),
                          Integrate(Times(Power(Times(f, x), Subtract(m, C1)),
                              Power(Plus(d, Times(e, Sqr(x))), q),
                              Power(Plus(a, Times(b, ArcCot(Times(c, x)))), Plus(p, C1))), x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, m, q), x), EqQ(e, Times(Sqr(c), d)),
                      EqQ(Plus(m, Times(C2, q), C2), C0), LtQ(p, CN1)))),
          IIntegrate(4944,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcTan(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(Times(f_DEFAULT,
                          x_), m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Times(f, x), Plus(m, C1)),
                              Power(Plus(d, Times(e, Sqr(x))), Plus(q, C1)),
                              Power(Plus(a, Times(b, ArcTan(Times(c, x)))),
                                  p),
                              Power(Times(d, f, Plus(m, C1)), CN1)),
                          x),
                      Dist(
                          Times(b, c, p, Power(Times(f, Plus(m, C1)),
                              CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Plus(m, C1)),
                                  Power(Plus(d, Times(e, Sqr(x))), q),
                                  Power(Plus(a, Times(b, ArcTan(Times(c, x)))), Subtract(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, m, q), x), EqQ(e, Times(Sqr(c), d)),
                      EqQ(Plus(m, Times(C2, q), C3), C0), GtQ(p, C0), NeQ(m, CN1)))),
          IIntegrate(4945,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCot(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(Times(f_DEFAULT,
                          x_), m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(Simp(Times(Power(Times(f, x), Plus(m, C1)),
                      Power(Plus(d, Times(e, Sqr(x))), Plus(q, C1)),
                      Power(Plus(a, Times(b, ArcCot(Times(c, x)))),
                          p),
                      Power(Times(d, f, Plus(m, C1)), CN1)), x), Dist(
                          Times(b, c, p, Power(Times(f, Plus(m, C1)), CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Plus(m, C1)),
                                  Power(Plus(d, Times(e, Sqr(x))), q),
                                  Power(Plus(a, Times(b, ArcCot(Times(c, x)))), Subtract(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, m, q), x), EqQ(e, Times(Sqr(
                      c), d)), EqQ(Plus(m, Times(C2, q),
                          C3), C0),
                      GtQ(p, C0), NeQ(m, CN1)))),
          IIntegrate(4946, Integrate(
              Times(
                  Plus(a_DEFAULT, Times(ArcTan(Times(c_DEFAULT, x_)), b_DEFAULT)), Power(
                      Times(f_DEFAULT, x_), m_),
                  Sqrt(Plus(d_, Times(e_DEFAULT, Sqr(x_))))),
              x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Times(f, x), Plus(m, C1)), Sqrt(Plus(d, Times(e, Sqr(x)))),
                              Plus(a, Times(b, ArcTan(Times(c, x)))), Power(Times(f, Plus(m, C2)),
                                  CN1)),
                          x),
                      Dist(Times(d, Power(Plus(m, C2), CN1)),
                          Integrate(
                              Times(Power(Times(f, x), m), Plus(a, Times(b, ArcTan(Times(c, x)))),
                                  Power(Plus(d, Times(e, Sqr(x))), CN1D2)),
                              x),
                          x),
                      Negate(
                          Dist(Times(b, c, d, Power(Times(f, Plus(m, C2)), CN1)),
                              Integrate(Times(Power(Times(f, x), Plus(m, C1)),
                                  Power(Plus(d, Times(e, Sqr(x))), CN1D2)), x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, f, m), x), EqQ(e, Times(Sqr(c), d)), NeQ(m, CN2)))),
          IIntegrate(4947, Integrate(
              Times(
                  Plus(a_DEFAULT, Times(ArcCot(Times(c_DEFAULT, x_)), b_DEFAULT)), Power(
                      Times(f_DEFAULT, x_), m_),
                  Sqrt(Plus(d_, Times(e_DEFAULT, Sqr(x_))))),
              x_Symbol),
              Condition(
                  Plus(
                      Simp(Times(Power(Times(f, x), Plus(m, C1)), Sqrt(Plus(d, Times(e, Sqr(x)))),
                          Plus(a, Times(b, ArcCot(Times(c, x)))),
                          Power(Times(f, Plus(m, C2)), CN1)), x),
                      Dist(Times(d, Power(Plus(m, C2), CN1)),
                          Integrate(
                              Times(Power(Times(f, x), m), Plus(a, Times(b, ArcCot(Times(c, x)))),
                                  Power(Plus(d, Times(e, Sqr(x))), CN1D2)),
                              x),
                          x),
                      Dist(
                          Times(b, c, d, Power(Times(f, Plus(m, C2)),
                              CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Plus(m, C1)),
                                  Power(Plus(d, Times(e, Sqr(x))), CN1D2)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, m), x), EqQ(e, Times(Sqr(c), d)), NeQ(m, CN2)))),
          IIntegrate(4948,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcTan(Times(c_DEFAULT, x_)), b_DEFAULT)), p_DEFAULT),
                      Power(Times(f_DEFAULT, x_), m_), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))),
                          q_)),
                  x_Symbol),
              Condition(Integrate(ExpandIntegrand(Times(Power(Times(f, x), m), Power(Plus(d, Times(
                  e, Sqr(x))), q), Power(Plus(a, Times(b, ArcTan(Times(c, x)))), p)), x), x), And(
                      FreeQ(List(a, b, c, d, e, f, m), x), EqQ(e, Times(Sqr(c),
                          d)),
                      IGtQ(p, C0), IGtQ(q, C1), Or(EqQ(p, C1), IntegerQ(m))))),
          IIntegrate(4949,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCot(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(Times(f_DEFAULT, x_), m_), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))),
                          q_)),
                  x_Symbol),
              Condition(
                  Integrate(ExpandIntegrand(Times(Power(Times(f, x), m),
                      Power(Plus(d, Times(e, Sqr(x))),
                          q),
                      Power(Plus(a, Times(b, ArcCot(Times(c, x)))), p)), x), x),
                  And(FreeQ(List(a, b, c, d, e, f, m), x), EqQ(e, Times(Sqr(c),
                      d)), IGtQ(p,
                          C0),
                      IGtQ(q, C1), Or(EqQ(p, C1), IntegerQ(m))))),
          IIntegrate(4950,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcTan(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(Times(f_DEFAULT,
                          x_), m_),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(Dist(d,
                      Integrate(Times(Power(Times(f, x), m),
                          Power(Plus(d, Times(e, Sqr(x))), Subtract(q, C1)),
                          Power(Plus(a, Times(b, ArcTan(Times(c, x)))), p)), x),
                      x),
                      Dist(Times(Sqr(c), d, Power(f, CN2)),
                          Integrate(
                              Times(Power(Times(f, x), Plus(m, C2)),
                                  Power(Plus(d, Times(e, Sqr(x))), Subtract(q, C1)), Power(
                                      Plus(a, Times(b, ArcTan(Times(c, x)))), p)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, m), x), EqQ(e, Times(Sqr(c), d)), GtQ(q, C0),
                      IGtQ(p, C0), Or(RationalQ(m), And(EqQ(p, C1), IntegerQ(q)))))),
          IIntegrate(4951,
              Integrate(Times(
                  Power(Plus(a_DEFAULT, Times(ArcCot(Times(c_DEFAULT, x_)), b_DEFAULT)), p_DEFAULT),
                  Power(Times(f_DEFAULT, x_), m_), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))),
                      q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(Dist(d,
                      Integrate(Times(Power(Times(f, x), m),
                          Power(Plus(d, Times(e, Sqr(x))), Subtract(q, C1)),
                          Power(Plus(a, Times(b, ArcCot(Times(c, x)))), p)), x),
                      x),
                      Dist(Times(Sqr(c), d, Power(f, CN2)),
                          Integrate(
                              Times(Power(Times(f, x), Plus(m, C2)),
                                  Power(Plus(d, Times(e, Sqr(x))), Subtract(q, C1)), Power(
                                      Plus(a, Times(b, ArcCot(Times(c, x)))), p)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, m), x), EqQ(e, Times(Sqr(c),
                      d)), GtQ(q, C0), IGtQ(p,
                          C0),
                      Or(RationalQ(m), And(EqQ(p, C1), IntegerQ(q)))))),
          IIntegrate(4952,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcTan(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(Times(f_DEFAULT,
                          x_), m_),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(f, Power(Times(f, x), Subtract(m, C1)),
                              Sqrt(Plus(d, Times(e, Sqr(x)))),
                              Power(Plus(a,
                                  Times(b, ArcTan(Times(c, x)))), p),
                              Power(Times(Sqr(c), d, m), CN1)),
                          x),
                      Negate(
                          Dist(
                              Times(b, f, p, Power(Times(c, m),
                                  CN1)),
                              Integrate(
                                  Times(Power(Times(f, x), Subtract(m, C1)),
                                      Power(Plus(a, Times(b,
                                          ArcTan(Times(c, x)))), Subtract(p,
                                              C1)),
                                      Power(Plus(d, Times(e, Sqr(x))), CN1D2)),
                                  x),
                              x)),
                      Negate(
                          Dist(
                              Times(Sqr(f), Subtract(m, C1), Power(Times(Sqr(c), m),
                                  CN1)),
                              Integrate(Times(Power(Times(f, x), Subtract(m, C2)),
                                  Power(Plus(a, Times(b, ArcTan(Times(c, x)))),
                                      p),
                                  Power(Plus(d, Times(e, Sqr(x))), CN1D2)), x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, f), x), EqQ(e, Times(Sqr(c), d)), GtQ(p, C0),
                      GtQ(m, C1)))),
          IIntegrate(4953,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCot(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(Times(f_DEFAULT,
                          x_), m_),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(f, Power(Times(f, x), Subtract(m, C1)),
                              Sqrt(Plus(d, Times(e, Sqr(x)))),
                              Power(Plus(a, Times(b, ArcCot(Times(c, x)))),
                                  p),
                              Power(Times(Sqr(c), d, m), CN1)),
                          x),
                      Dist(
                          Times(b, f, p, Power(Times(c, m),
                              CN1)),
                          Integrate(Times(Power(Times(f, x), Subtract(m, C1)),
                              Power(Plus(a, Times(b, ArcCot(Times(c, x)))), Subtract(p, C1)), Power(
                                  Plus(d, Times(e, Sqr(x))), CN1D2)),
                              x),
                          x),
                      Negate(
                          Dist(
                              Times(Sqr(f), Subtract(m, C1), Power(Times(Sqr(c), m),
                                  CN1)),
                              Integrate(Times(Power(Times(f, x), Subtract(m, C2)),
                                  Power(Plus(a, Times(b, ArcCot(Times(c, x)))), p),
                                  Power(Plus(d, Times(e, Sqr(x))), CN1D2)), x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, f), x), EqQ(e, Times(Sqr(c),
                      d)), GtQ(p,
                          C0),
                      GtQ(m, C1)))),
          IIntegrate(4954,
              Integrate(
                  Times(
                      Plus(a_DEFAULT, Times(ArcTan(Times(c_DEFAULT, x_)), b_DEFAULT)),
                      Power(x_, CN1), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(Times(CN2, Plus(a, Times(b, ArcTan(Times(c, x)))),
                          ArcTanh(Times(Sqrt(Plus(C1, Times(CI, c, x))),
                              Power(Subtract(C1, Times(CI, c, x)), CN1D2))),
                          Power(d, CN1D2)), x),
                      Simp(Times(CI, b,
                          PolyLog(C2,
                              Times(CN1, Sqrt(Plus(C1, Times(CI, c, x))),
                                  Power(Subtract(C1, Times(CI, c, x)), CN1D2))),
                          Power(d, CN1D2)), x),
                      Negate(Simp(Times(CI, b,
                          PolyLog(C2,
                              Times(Sqrt(Plus(C1, Times(CI, c, x))),
                                  Power(Subtract(C1, Times(CI, c, x)), CN1D2))),
                          Power(d, CN1D2)), x))),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(e, Times(Sqr(c), d)), GtQ(d, C0)))),
          IIntegrate(4955,
              Integrate(
                  Times(
                      Plus(a_DEFAULT, Times(ArcCot(Times(c_DEFAULT, x_)), b_DEFAULT)), Power(x_,
                          CN1),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(CN2, Plus(a, Times(b, ArcCot(Times(c, x)))),
                              ArcTanh(
                                  Times(Sqrt(Plus(C1, Times(CI, c, x))),
                                      Power(Subtract(C1, Times(CI, c, x)), CN1D2))),
                              Power(d, CN1D2)),
                          x),
                      Negate(
                          Simp(
                              Times(CI, b,
                                  PolyLog(C2,
                                      Times(CN1, Sqrt(Plus(C1, Times(CI, c, x))),
                                          Power(Subtract(C1, Times(CI, c, x)), CN1D2))),
                                  Power(d, CN1D2)),
                              x)),
                      Simp(
                          Times(CI, b,
                              PolyLog(C2,
                                  Times(
                                      Sqrt(Plus(C1, Times(CI, c, x))), Power(
                                          Subtract(C1, Times(CI, c, x)), CN1D2))),
                              Power(d, CN1D2)),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(e, Times(Sqr(c), d)), GtQ(d, C0)))),
          IIntegrate(4956,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(ArcTan(Times(c_DEFAULT, x_)),
                          b_DEFAULT)), p_),
                      Power(x_, CN1), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Dist(Power(d, CN1D2),
                      Subst(
                          Integrate(Times(Power(Plus(a, Times(b, x)), p), Csc(x)),
                              x),
                          x, ArcTan(Times(c, x))),
                      x),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(e, Times(Sqr(c), d)), IGtQ(p, C0),
                      GtQ(d, C0)))),
          IIntegrate(4957,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(ArcCot(Times(c_DEFAULT, x_)),
                          b_DEFAULT)), p_),
                      Power(x_, CN1), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(
                          Times(
                              c, x, Sqrt(Plus(C1,
                                  Power(Times(Sqr(c), Sqr(x)), CN1))),
                              Power(Plus(d, Times(e, Sqr(x))), CN1D2)),
                          Subst(Integrate(Times(Power(Plus(a, Times(b, x)), p), Sec(x)), x), x,
                              ArcCot(Times(c, x))),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(e, Times(Sqr(c), d)), IGtQ(p, C0),
                      GtQ(d, C0)))),
          IIntegrate(4958,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(ArcTan(Times(c_DEFAULT, x_)),
                          b_DEFAULT)), p_DEFAULT),
                      Power(x_, CN1), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Sqrt(Plus(C1,
                              Times(Sqr(c), Sqr(x)))),
                          Power(Plus(d, Times(e, Sqr(x))), CN1D2)),
                      Integrate(
                          Times(Power(Plus(a, Times(b, ArcTan(Times(c, x)))), p), Power(
                              Times(x, Sqrt(Plus(C1, Times(Sqr(c), Sqr(x))))), CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(e, Times(Sqr(c),
                      d)), IGtQ(p,
                          C0),
                      Not(GtQ(d, C0))))),
          IIntegrate(4959,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCot(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(x_, CN1), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Sqrt(Plus(C1,
                              Times(Sqr(c), Sqr(x)))),
                          Power(Plus(d, Times(e, Sqr(x))), CN1D2)),
                      Integrate(
                          Times(
                              Power(Plus(a, Times(b,
                                  ArcCot(Times(c, x)))), p),
                              Power(Times(x, Sqrt(Plus(C1, Times(Sqr(c), Sqr(x))))), CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(e, Times(Sqr(c),
                      d)), IGtQ(p,
                          C0),
                      Not(GtQ(d, C0))))),
          IIntegrate(4960,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcTan(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(x_, CN2), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Plus(Negate(Simp(Times(Sqrt(Plus(d, Times(e, Sqr(x)))),
                      Power(Plus(a, Times(b, ArcTan(Times(c, x)))), p), Power(Times(d, x), CN1)),
                      x)),
                      Dist(Times(b, c, p),
                          Integrate(
                              Times(Power(Plus(a, Times(b, ArcTan(Times(c, x)))), Subtract(p, C1)),
                                  Power(Times(x, Sqrt(Plus(d, Times(e, Sqr(x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(e, Times(Sqr(c), d)), GtQ(p, C0)))));
}
