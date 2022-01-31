package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.ArcCoth;
import static org.matheclipse.core.expression.F.ArcTan;
import static org.matheclipse.core.expression.F.ArcTanh;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.CI;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Csch;
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
import static org.matheclipse.core.expression.F.QQ;
import static org.matheclipse.core.expression.F.Sech;
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
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RemoveContent;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules297 {
  public static IAST RULES =
      List(
          IIntegrate(5941,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCoth(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(Times(f_DEFAULT,
                          x_), m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, x_)), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(Power(Plus(a, Times(b, ArcCoth(Times(c, x)))), p),
                          Times(Power(Times(f, x), m), Power(Plus(d, Times(e, x)), q)), x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f,
                      m), x), IGtQ(p,
                          C0),
                      IntegerQ(q), Or(GtQ(q, C0), NeQ(a, C0), IntegerQ(m))))),
          IIntegrate(5942,
              Integrate(
                  Times(
                      Plus(a_DEFAULT, Times(ArcTanh(Times(c_DEFAULT, x_)), b_DEFAULT)), Power(
                          Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_DEFAULT)),
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
                          Integrate(Times(Power(Plus(d, Times(e, Sqr(x))), Subtract(q, C1)),
                              Plus(a, Times(b, ArcTanh(Times(c, x))))), x),
                          x),
                      Simp(Times(x, Power(Plus(d, Times(e, Sqr(x))), q),
                          Plus(a, Times(b, ArcTanh(Times(c, x)))), Power(Plus(Times(C2, q), C1),
                              CN1)),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      GtQ(q, C0)))),
          IIntegrate(5943,
              Integrate(Times(
                  Plus(a_DEFAULT, Times(ArcCoth(Times(c_DEFAULT, x_)), b_DEFAULT)),
                  Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_DEFAULT)), x_Symbol),
              Condition(Plus(Simp(Times(b, Power(
                  Plus(d, Times(e, Sqr(x))), q),
                  Power(Times(C2, c, q, Plus(Times(C2, q), C1)), CN1)), x),
                  Dist(
                      Times(C2, d, q, Power(Plus(Times(C2, q), C1),
                          CN1)),
                      Integrate(
                          Times(
                              Power(Plus(d, Times(e, Sqr(x))), Subtract(q, C1)), Plus(a,
                                  Times(b, ArcCoth(Times(c, x))))),
                          x),
                      x),
                  Simp(
                      Times(x, Power(Plus(d, Times(e, Sqr(x))), q),
                          Plus(a, Times(b, ArcCoth(Times(c, x)))), Power(Plus(Times(C2, q), C1),
                              CN1)),
                      x)),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      GtQ(q, C0)))),
          IIntegrate(5944,
              Integrate(
                  Times(Power(Plus(a_DEFAULT, Times(ArcTanh(Times(c_DEFAULT, x_)), b_DEFAULT)), p_),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(b, p, Power(Plus(d, Times(e, Sqr(x))), q),
                              Power(Plus(a, Times(b, ArcTanh(Times(c, x)))), Subtract(p,
                                  C1)),
                              Power(Times(C2, c, q, Plus(Times(C2, q), C1)), CN1)),
                          x),
                      Dist(
                          Times(C2, d, q, Power(Plus(Times(C2, q), C1),
                              CN1)),
                          Integrate(
                              Times(
                                  Power(Plus(d, Times(e,
                                      Sqr(x))), Subtract(q,
                                          C1)),
                                  Power(Plus(a, Times(b, ArcTanh(Times(c, x)))), p)),
                              x),
                          x),
                      Negate(Dist(
                          Times(Sqr(b), d, p, Subtract(p, C1),
                              Power(Times(C2, q, Plus(Times(C2, q), C1)), CN1)),
                          Integrate(
                              Times(Power(Plus(d, Times(e, Sqr(x))), Subtract(q, C1)),
                                  Power(Plus(a, Times(b, ArcTanh(Times(c, x)))), Subtract(p, C2))),
                              x),
                          x)),
                      Simp(
                          Times(x, Power(Plus(d, Times(e, Sqr(x))), q),
                              Power(Plus(a, Times(b, ArcTanh(Times(c, x)))),
                                  p),
                              Power(Plus(Times(C2, q), C1), CN1)),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d),
                      e), C0), GtQ(q,
                          C0),
                      GtQ(p, C1)))),
          IIntegrate(5945,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(ArcCoth(Times(c_DEFAULT, x_)),
                          b_DEFAULT)), p_),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(b, p, Power(Plus(d, Times(e, Sqr(x))), q),
                              Power(Plus(a, Times(b, ArcCoth(Times(c, x)))), Subtract(p,
                                  C1)),
                              Power(Times(C2, c, q, Plus(Times(C2, q), C1)), CN1)),
                          x),
                      Dist(
                          Times(C2, d, q, Power(Plus(Times(C2, q), C1),
                              CN1)),
                          Integrate(
                              Times(
                                  Power(Plus(d, Times(e,
                                      Sqr(x))), Subtract(q,
                                          C1)),
                                  Power(Plus(a, Times(b, ArcCoth(Times(c, x)))), p)),
                              x),
                          x),
                      Negate(Dist(
                          Times(Sqr(b), d, p, Subtract(p, C1),
                              Power(Times(C2, q, Plus(Times(C2, q), C1)), CN1)),
                          Integrate(
                              Times(Power(Plus(d, Times(e, Sqr(x))), Subtract(q, C1)),
                                  Power(Plus(a, Times(b, ArcCoth(Times(c, x)))), Subtract(p, C2))),
                              x),
                          x)),
                      Simp(Times(x, Power(Plus(d, Times(e, Sqr(x))), q),
                          Power(Plus(a, Times(b, ArcCoth(Times(c, x)))),
                              p),
                          Power(Plus(Times(C2, q), C1), CN1)), x)),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d), e), C0), GtQ(q, C0),
                      GtQ(p, C1)))),
          IIntegrate(5946,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcTanh(Times(c_DEFAULT, x_)), b_DEFAULT)), CN1),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(
                          Log(RemoveContent(Plus(a, Times(b, ArcTanh(Times(c, x)))), x)), Power(
                              Times(b, c, d), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d), e), C0)))),
          IIntegrate(5947,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcCoth(Times(c_DEFAULT, x_)), b_DEFAULT)), CN1),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(
                          Log(RemoveContent(Plus(a, Times(b, ArcCoth(Times(c, x)))), x)), Power(
                              Times(b, c, d), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d), e), C0)))),
          IIntegrate(5948,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(ArcTanh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(Power(Plus(a, Times(b, ArcTanh(Times(c, x)))), Plus(p, C1)),
                          Power(Times(b, c, d, Plus(p, C1)), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, p), x), EqQ(Plus(Times(Sqr(c), d),
                      e), C0), NeQ(p,
                          CN1)))),
          IIntegrate(5949,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(ArcCoth(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(
                          Power(Plus(a, Times(b, ArcCoth(Times(c, x)))),
                              Plus(p, C1)),
                          Power(Times(b, c, d, Plus(p, C1)), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, p), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      NeQ(p, CN1)))),
          IIntegrate(5950,
              Integrate(Times(
                  Plus(a_DEFAULT, Times(ArcTanh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                  Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1D2)), x_Symbol),
              Condition(Plus(
                  Simp(
                      Times(CN2, Plus(a, Times(b, ArcTanh(Times(c, x)))),
                          ArcTan(Times(Sqrt(Subtract(C1, Times(c, x))),
                              Power(Plus(C1, Times(c, x)), CN1D2))),
                          Power(Times(c, Sqrt(d)), CN1)),
                      x),
                  Negate(Simp(
                      Times(
                          CI, b,
                          PolyLog(C2,
                              Times(CN1, CI, Sqrt(Subtract(C1, Times(c, x))),
                                  Power(Plus(C1, Times(c, x)), CN1D2))),
                          Power(Times(c, Sqrt(d)), CN1)),
                      x)),
                  Simp(Times(CI, b,
                      PolyLog(C2,
                          Times(CI, Sqrt(Subtract(C1, Times(c, x))),
                              Power(Plus(C1, Times(c, x)), CN1D2))),
                      Power(Times(c, Sqrt(d)), CN1)), x)),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      GtQ(d, C0)))),
          IIntegrate(5951,
              Integrate(
                  Times(
                      Plus(a_DEFAULT, Times(ArcCoth(Times(c_DEFAULT, x_)),
                          b_DEFAULT)),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Plus(Simp(Times(CN2, Plus(a, Times(b, ArcCoth(Times(c, x)))),
                      ArcTan(Times(Sqrt(Subtract(C1, Times(c, x))),
                          Power(Plus(C1, Times(c, x)), CN1D2))),
                      Power(Times(c, Sqrt(d)), CN1)), x), Negate(
                          Simp(Times(CI, b,
                              PolyLog(C2,
                                  Times(CN1, CI, Sqrt(Subtract(C1, Times(c, x))),
                                      Power(Plus(C1, Times(c, x)), CN1D2))),
                              Power(Times(c, Sqrt(d)), CN1)), x)),
                      Simp(
                          Times(CI, b,
                              PolyLog(C2,
                                  Times(
                                      CI, Sqrt(Subtract(C1, Times(c, x))), Power(
                                          Plus(C1, Times(c, x)), CN1D2))),
                              Power(Times(c, Sqrt(d)), CN1)),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      GtQ(d, C0)))),
          IIntegrate(5952,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcTanh(Times(c_DEFAULT, x_)), b_DEFAULT)), p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Times(c,
                          Sqrt(d)), CN1),
                      Subst(Integrate(Times(Power(Plus(a, Times(b, x)), p), Sech(x)), x), x,
                          ArcTanh(Times(c, x))),
                      x),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      IGtQ(p, C0), GtQ(d, C0)))),
          IIntegrate(5953,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCoth(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(
                          Times(
                              x, Sqrt(Subtract(C1,
                                  Power(Times(Sqr(c), Sqr(x)), CN1))),
                              Power(Plus(d, Times(e, Sqr(x))), CN1D2)),
                          Subst(Integrate(Times(Power(Plus(a, Times(b, x)), p), Csch(x)), x), x,
                              ArcCoth(Times(c, x))),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      IGtQ(p, C0), GtQ(d, C0)))),
          IIntegrate(5954,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcTanh(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Sqrt(Subtract(C1,
                              Times(Sqr(c), Sqr(x)))),
                          Power(Plus(d, Times(e, Sqr(x))), CN1D2)),
                      Integrate(
                          Times(
                              Power(Plus(a, Times(b, ArcTanh(Times(c, x)))), p), Power(
                                  Subtract(C1, Times(Sqr(c), Sqr(x))), CN1D2)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d),
                      e), C0), IGtQ(p,
                          C0),
                      Not(GtQ(d, C0))))),
          IIntegrate(5955,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCoth(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Sqrt(Subtract(C1, Times(Sqr(c), Sqr(x)))), Power(
                          Plus(d, Times(e, Sqr(x))), CN1D2)),
                      Integrate(
                          Times(
                              Power(Plus(a, Times(b, ArcCoth(Times(c, x)))), p), Power(
                                  Subtract(C1, Times(Sqr(c), Sqr(x))), CN1D2)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      IGtQ(p, C0), Not(GtQ(d, C0))))),
          IIntegrate(5956,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(ArcTanh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN2)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(x, Power(Plus(a, Times(b, ArcTanh(Times(c, x)))), p),
                              Power(Times(C2, d, Plus(d, Times(e, Sqr(x)))), CN1)),
                          x),
                      Negate(
                          Dist(Times(C1D2, b, c, p),
                              Integrate(
                                  Times(x,
                                      Power(Plus(a, Times(b, ArcTanh(Times(c, x)))),
                                          Subtract(p, C1)),
                                      Power(Plus(d, Times(e, Sqr(x))), CN2)),
                                  x),
                              x)),
                      Simp(
                          Times(
                              Power(Plus(a, Times(b, ArcTanh(Times(c, x)))), Plus(p,
                                  C1)),
                              Power(Times(C2, b, c, Sqr(d), Plus(p, C1)), CN1)),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      GtQ(p, C0)))),
          IIntegrate(5957,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(ArcCoth(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN2)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(x, Power(Plus(a, Times(b, ArcCoth(Times(c, x)))), p),
                              Power(Times(C2, d, Plus(d, Times(e, Sqr(x)))), CN1)),
                          x),
                      Negate(
                          Dist(Times(C1D2, b, c, p),
                              Integrate(
                                  Times(x,
                                      Power(Plus(a, Times(b, ArcCoth(Times(c, x)))),
                                          Subtract(p, C1)),
                                      Power(Plus(d, Times(e, Sqr(x))), CN2)),
                                  x),
                              x)),
                      Simp(
                          Times(
                              Power(Plus(a, Times(b, ArcCoth(Times(c, x)))), Plus(p,
                                  C1)),
                              Power(Times(C2, b, c, Sqr(d), Plus(p, C1)), CN1)),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d),
                      e), C0), GtQ(p,
                          C0)))),
          IIntegrate(5958,
              Integrate(
                  Times(
                      Plus(a_DEFAULT, Times(ArcTanh(Times(c_DEFAULT,
                          x_)), b_DEFAULT)),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), QQ(-3L, 2L))),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(Times(b, Power(Times(c, d, Sqrt(Plus(d, Times(e, Sqr(x))))), CN1)),
                              x)),
                      Simp(
                          Times(
                              x, Plus(a, Times(b,
                                  ArcTanh(Times(c, x)))),
                              Power(Times(d, Sqrt(Plus(d, Times(e, Sqr(x))))), CN1)),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d), e), C0)))),
          IIntegrate(5959,
              Integrate(
                  Times(
                      Plus(a_DEFAULT, Times(ArcCoth(Times(c_DEFAULT, x_)),
                          b_DEFAULT)),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), QQ(-3L, 2L))),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(Times(b, Power(Times(c, d, Sqrt(Plus(d, Times(e, Sqr(x))))), CN1)),
                              x)),
                      Simp(
                          Times(x, Plus(a, Times(b, ArcCoth(Times(c, x)))),
                              Power(Times(d, Sqrt(Plus(d, Times(e, Sqr(x))))), CN1)),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d), e), C0)))),
          IIntegrate(5960,
              Integrate(
                  Times(
                      Plus(a_DEFAULT, Times(ArcTanh(Times(c_DEFAULT, x_)),
                          b_DEFAULT)),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(Simp(Times(b, Power(Plus(d, Times(e, Sqr(x))), Plus(q, C1)),
                          Power(Times(C4, c, d, Sqr(Plus(q, C1))), CN1)), x)),
                      Dist(
                          Times(Plus(Times(C2, q), C3), Power(Times(C2, d, Plus(q, C1)), CN1)),
                          Integrate(Times(Power(Plus(d, Times(e, Sqr(x))), Plus(q, C1)),
                              Plus(a, Times(b, ArcTanh(Times(c, x))))), x),
                          x),
                      Negate(Simp(Times(x, Power(Plus(d, Times(e, Sqr(x))), Plus(q, C1)),
                          Plus(a, Times(b, ArcTanh(Times(c, x)))),
                          Power(Times(C2, d, Plus(q, C1)), CN1)), x))),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      LtQ(q, CN1), NeQ(q, QQ(-3L, 2L))))));
}
