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
class IntRules249 {
  public static IAST RULES =
      List(
          IIntegrate(4981,
              Integrate(Times(
                  Power(Plus(a_DEFAULT, Times(ArcCot(Times(c_DEFAULT, x_)), b_DEFAULT)), p_DEFAULT),
                  Power(Times(f_DEFAULT, x_), m_DEFAULT), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))),
                      q_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(u,
                              ExpandIntegrand(Power(Plus(a, Times(b, ArcCot(Times(c, x)))), p),
                                  Times(Power(Times(f, x), m), Power(Plus(d, Times(e, Sqr(x))), q)),
                                  x))),
                      Condition(Integrate(u, x), SumQ(u))),
                  And(FreeQ(List(a, b, c, d, e, f, m), x), IntegerQ(q), IGtQ(p, C0),
                      Or(And(EqQ(p, C1), GtQ(q, C0)), IntegerQ(m))))),
          IIntegrate(4982,
              Integrate(
                  Times(Plus(Times(ArcTan(Times(c_DEFAULT, x_)), b_DEFAULT), a_),
                      Power(Times(f_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(a,
                          Integrate(
                              Times(Power(Times(f, x), m), Power(Plus(d, Times(e, Sqr(x))), q)), x),
                          x),
                      Dist(b,
                          Integrate(Times(Power(Times(f, x), m),
                              Power(Plus(d, Times(e, Sqr(x))), q), ArcTan(Times(c, x))), x),
                          x)),
                  FreeQ(List(a, b, c, d, e, f, m, q), x))),
          IIntegrate(4983,
              Integrate(
                  Times(Plus(Times(ArcCot(Times(c_DEFAULT, x_)), b_DEFAULT), a_),
                      Power(Times(f_DEFAULT, x_), m_DEFAULT), Power(
                          Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(a,
                          Integrate(Times(Power(Times(f,
                              x), m), Power(Plus(d, Times(e, Sqr(x))),
                                  q)),
                              x),
                          x),
                      Dist(b,
                          Integrate(Times(Power(Times(f, x), m),
                              Power(Plus(d, Times(e, Sqr(x))), q), ArcCot(Times(c, x))), x),
                          x)),
                  FreeQ(List(a, b, c, d, e, f, m, q), x))),
          IIntegrate(4984,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcTan(Times(c_DEFAULT, x_)), b_DEFAULT)), p_DEFAULT),
                      Power(Plus(f_, Times(g_DEFAULT, x_)), m_DEFAULT), Power(
                          Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Times(
                              Power(Plus(a, Times(b, ArcTan(Times(c, x)))),
                                  p),
                              Power(Plus(d, Times(e, Sqr(x))), CN1)),
                          Power(Plus(f, Times(g, x)), m), x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g), x), IGtQ(p, C0), EqQ(e, Times(Sqr(c),
                      d)), IGtQ(m,
                          C0)))),
          IIntegrate(4985,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcCot(Times(c_DEFAULT, x_)), b_DEFAULT)), p_DEFAULT),
                      Power(Plus(f_, Times(g_DEFAULT, x_)), m_DEFAULT), Power(
                          Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Times(Power(Plus(a, Times(b, ArcCot(Times(c, x)))), p),
                              Power(Plus(d, Times(e, Sqr(x))), CN1)),
                          Power(Plus(f, Times(g, x)), m), x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g), x), IGtQ(p, C0), EqQ(e, Times(Sqr(c),
                      d)), IGtQ(m,
                          C0)))),
          IIntegrate(4986,
              Integrate(
                  Times(ArcTanh(u_),
                      Power(
                          Plus(a_DEFAULT,
                              Times(ArcTan(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(C1D2,
                          Integrate(
                              Times(Log(Plus(C1, u)),
                                  Power(Plus(a,
                                      Times(b, ArcTan(Times(c, x)))), p),
                                  Power(Plus(d, Times(e, Sqr(x))), CN1)),
                              x),
                          x),
                      Dist(C1D2,
                          Integrate(Times(Log(Subtract(C1, u)),
                              Power(Plus(a, Times(b, ArcTan(Times(c, x)))),
                                  p),
                              Power(Plus(d, Times(e, Sqr(x))), CN1)), x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), IGtQ(p, C0), EqQ(e, Times(Sqr(c), d)),
                      EqQ(Subtract(
                          Sqr(u), Sqr(Subtract(C1, Times(C2, CI,
                              Power(Plus(CI, Times(c, x)), CN1))))),
                          C0)))),
          IIntegrate(4987,
              Integrate(
                  Times(ArcCoth(u_),
                      Power(
                          Plus(a_DEFAULT,
                              Times(ArcCot(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(C1D2,
                          Integrate(
                              Times(Log(SimplifyIntegrand(Plus(C1, Power(u, CN1)), x)),
                                  Power(Plus(a,
                                      Times(b, ArcCot(Times(c, x)))), p),
                                  Power(Plus(d, Times(e, Sqr(x))), CN1)),
                              x),
                          x),
                      Dist(C1D2,
                          Integrate(
                              Times(Log(SimplifyIntegrand(Subtract(C1, Power(u, CN1)), x)),
                                  Power(Plus(a,
                                      Times(b, ArcCot(Times(c, x)))), p),
                                  Power(Plus(d, Times(e, Sqr(x))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), IGtQ(p, C0), EqQ(e, Times(Sqr(c),
                      d)), EqQ(
                          Subtract(
                              Sqr(u), Sqr(Subtract(C1, Times(C2, CI,
                                  Power(Plus(CI, Times(c, x)), CN1))))),
                          C0)))),
          IIntegrate(4988,
              Integrate(
                  Times(ArcTanh(u_),
                      Power(
                          Plus(a_DEFAULT,
                              Times(ArcTan(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(C1D2,
                          Integrate(
                              Times(Log(Plus(C1, u)),
                                  Power(Plus(a,
                                      Times(b, ArcTan(Times(c, x)))), p),
                                  Power(Plus(d, Times(e, Sqr(x))), CN1)),
                              x),
                          x),
                      Dist(C1D2,
                          Integrate(Times(Log(Subtract(C1, u)),
                              Power(Plus(a, Times(b, ArcTan(Times(c, x)))),
                                  p),
                              Power(Plus(d, Times(e, Sqr(x))), CN1)), x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), IGtQ(p, C0), EqQ(e, Times(Sqr(c),
                      d)), EqQ(
                          Subtract(Sqr(u),
                              Sqr(Subtract(C1, Times(C2, CI,
                                  Power(Subtract(CI, Times(c, x)), CN1))))),
                          C0)))),
          IIntegrate(4989,
              Integrate(
                  Times(ArcCoth(u_),
                      Power(
                          Plus(a_DEFAULT,
                              Times(ArcCot(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(C1D2,
                          Integrate(
                              Times(Log(SimplifyIntegrand(Plus(C1, Power(u, CN1)), x)),
                                  Power(Plus(a,
                                      Times(b, ArcCot(Times(c, x)))), p),
                                  Power(Plus(d, Times(e, Sqr(x))), CN1)),
                              x),
                          x),
                      Dist(C1D2,
                          Integrate(Times(Log(SimplifyIntegrand(Subtract(C1, Power(u, CN1)), x)),
                              Power(Plus(a, Times(b, ArcCot(Times(c, x)))),
                                  p),
                              Power(Plus(d, Times(e, Sqr(x))), CN1)), x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), IGtQ(p, C0), EqQ(e, Times(Sqr(c), d)),
                      EqQ(Subtract(Sqr(u),
                          Sqr(Subtract(C1, Times(C2, CI, Power(Subtract(CI, Times(c, x)), CN1))))),
                          C0)))),
          IIntegrate(4990,
              Integrate(
                  Times(
                      Log(Plus(f_,
                          Times(g_DEFAULT, x_))),
                      Power(
                          Plus(a_DEFAULT,
                              Times(ArcTan(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Plus(a, Times(b, ArcTan(Times(c, x)))), Plus(p, C1)),
                              Log(Plus(f, Times(g, x))), Power(Times(b, c, d, Plus(p, C1)), CN1)),
                          x),
                      Dist(Times(g, Power(Times(b, c, d, Plus(p, C1)), CN1)),
                          Integrate(
                              Times(Power(Plus(a, Times(b, ArcTan(Times(c, x)))), Plus(p, C1)),
                                  Power(Plus(f, Times(g, x)), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g), x), IGtQ(p, C0), EqQ(e, Times(Sqr(c), d)),
                      EqQ(Plus(Times(Sqr(c), Sqr(f)), Sqr(g)), C0)))),
          IIntegrate(4991,
              Integrate(
                  Times(Log(Plus(f_, Times(g_DEFAULT, x_))),
                      Power(
                          Plus(a_DEFAULT,
                              Times(ArcCot(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              Power(Plus(a, Times(b, ArcCot(Times(c, x)))), Plus(p, C1)),
                              Log(Plus(f, Times(g, x))), Power(Times(b, c, d, Plus(p, C1)), CN1)),
                          x),
                      Dist(Times(g, Power(Times(b, c, d, Plus(p, C1)), CN1)),
                          Integrate(
                              Times(Power(Plus(a, Times(b, ArcCot(Times(c, x)))), Plus(p, C1)),
                                  Power(Plus(f, Times(g, x)), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g), x), IGtQ(p, C0), EqQ(e, Times(Sqr(c), d)),
                      EqQ(Plus(Times(Sqr(c), Sqr(f)), Sqr(g)), C0)))),
          IIntegrate(4992,
              Integrate(
                  Times(Log(u_),
                      Power(
                          Plus(a_DEFAULT,
                              Times(ArcTan(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              CI, Power(Plus(a,
                                  Times(b, ArcTan(Times(c, x)))), p),
                              PolyLog(C2, Subtract(C1, u)), Power(Times(C2, c, d), CN1)),
                          x),
                      Dist(Times(C1D2, b, p, CI),
                          Integrate(
                              Times(Power(Plus(a, Times(b, ArcTan(Times(c, x)))), Subtract(p, C1)),
                                  PolyLog(C2, Subtract(C1, u)), Power(Plus(d, Times(e, Sqr(x))),
                                      CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), IGtQ(p, C0), EqQ(e, Times(Sqr(c),
                      d)), EqQ(
                          Subtract(
                              Sqr(Subtract(C1, u)), Sqr(Subtract(C1, Times(C2, CI,
                                  Power(Plus(CI, Times(c, x)), CN1))))),
                          C0)))),
          IIntegrate(4993,
              Integrate(
                  Times(Log(u_),
                      Power(
                          Plus(a_DEFAULT,
                              Times(ArcCot(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(
                              CI, Power(Plus(a,
                                  Times(b, ArcCot(Times(c, x)))), p),
                              PolyLog(C2, Subtract(C1, u)), Power(Times(C2, c, d), CN1)),
                          x),
                      Dist(Times(C1D2, b, p, CI),
                          Integrate(
                              Times(Power(Plus(a, Times(b, ArcCot(Times(c, x)))), Subtract(p, C1)),
                                  PolyLog(C2, Subtract(C1, u)),
                                  Power(Plus(d, Times(e, Sqr(x))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), IGtQ(p, C0), EqQ(e, Times(Sqr(c), d)),
                      EqQ(Subtract(Sqr(Subtract(C1, u)),
                          Sqr(Subtract(C1, Times(C2, CI, Power(Plus(CI, Times(c, x)), CN1))))),
                          C0)))),
          IIntegrate(4994,
              Integrate(
                  Times(Log(u_),
                      Power(
                          Plus(a_DEFAULT,
                              Times(ArcTan(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(
                                  CI, Power(Plus(a,
                                      Times(b, ArcTan(Times(c, x)))), p),
                                  PolyLog(C2, Subtract(C1, u)), Power(Times(C2, c, d), CN1)),
                              x)),
                      Dist(Times(C1D2, b, p, CI),
                          Integrate(
                              Times(Power(Plus(a, Times(b, ArcTan(Times(c, x)))), Subtract(p, C1)),
                                  PolyLog(C2, Subtract(C1, u)), Power(Plus(d, Times(e, Sqr(x))),
                                      CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), IGtQ(p, C0), EqQ(e, Times(Sqr(c),
                      d)), EqQ(
                          Subtract(Sqr(Subtract(C1, u)),
                              Sqr(Subtract(C1, Times(C2, CI,
                                  Power(Subtract(CI, Times(c, x)), CN1))))),
                          C0)))),
          IIntegrate(4995,
              Integrate(
                  Times(Log(u_),
                      Power(
                          Plus(a_DEFAULT,
                              Times(ArcCot(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Negate(
                          Simp(
                              Times(
                                  CI, Power(Plus(a,
                                      Times(b, ArcCot(Times(c, x)))), p),
                                  PolyLog(C2, Subtract(C1, u)), Power(Times(C2, c, d), CN1)),
                              x)),
                      Dist(Times(C1D2, b, p, CI), Integrate(
                          Times(
                              Power(Plus(a, Times(b, ArcCot(Times(c, x)))), Subtract(p, C1)),
                              PolyLog(C2, Subtract(C1, u)), Power(Plus(d, Times(e, Sqr(x))), CN1)),
                          x), x)),
                  And(FreeQ(List(a, b, c, d, e), x), IGtQ(p, C0), EqQ(e, Times(Sqr(c), d)),
                      EqQ(Subtract(Sqr(Subtract(C1, u)),
                          Sqr(Subtract(C1, Times(C2, CI, Power(Subtract(CI, Times(c, x)), CN1))))),
                          C0)))),
          IIntegrate(4996,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcTan(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1), PolyLog(k_, u_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(
                                  CI, Power(Plus(a, Times(b, ArcTan(Times(c, x)))), p), PolyLog(
                                      Plus(k, C1), u),
                                  Power(Times(C2, c, d), CN1)),
                              x)),
                      Dist(Times(C1D2, b, p, CI),
                          Integrate(
                              Times(
                                  Power(Plus(a, Times(b, ArcTan(Times(c, x)))), Subtract(p, C1)),
                                  PolyLog(Plus(k, C1), u), Power(Plus(d, Times(e, Sqr(x))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, k), x), IGtQ(p, C0), EqQ(e, Times(Sqr(c),
                      d)), EqQ(
                          Subtract(
                              Sqr(u), Sqr(Subtract(C1, Times(C2, CI,
                                  Power(Plus(CI, Times(c, x)), CN1))))),
                          C0)))),
          IIntegrate(4997,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCot(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1), PolyLog(k_, u_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Negate(
                          Simp(
                              Times(CI, Power(Plus(a, Times(b, ArcCot(Times(c, x)))), p),
                                  PolyLog(Plus(k, C1), u), Power(Times(C2, c, d), CN1)),
                              x)),
                      Dist(Times(C1D2, b, p, CI),
                          Integrate(
                              Times(
                                  Power(Plus(a, Times(b, ArcCot(Times(c, x)))), Subtract(p, C1)),
                                  PolyLog(Plus(k, C1), u), Power(Plus(d, Times(e, Sqr(x))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, k), x), IGtQ(p, C0), EqQ(e, Times(Sqr(c), d)),
                      EqQ(Subtract(Sqr(u),
                          Sqr(Subtract(C1, Times(C2, CI, Power(Plus(CI, Times(c, x)), CN1))))),
                          C0)))),
          IIntegrate(4998,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcTan(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1), PolyLog(k_, u_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              CI, Power(Plus(a, Times(b,
                                  ArcTan(Times(c, x)))), p),
                              PolyLog(Plus(k, C1), u), Power(Times(C2, c, d), CN1)),
                          x),
                      Dist(Times(C1D2, b, p, CI),
                          Integrate(
                              Times(Power(Plus(a, Times(b, ArcTan(Times(c, x)))), Subtract(p, C1)),
                                  PolyLog(Plus(k, C1), u), Power(Plus(d, Times(e, Sqr(x))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, k), x), IGtQ(p, C0), EqQ(e, Times(Sqr(c), d)),
                      EqQ(Subtract(Sqr(u),
                          Sqr(Subtract(C1, Times(C2, CI, Power(Subtract(CI, Times(c, x)), CN1))))),
                          C0)))),
          IIntegrate(4999,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCot(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1), PolyLog(k_, u_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(
                              CI, Power(Plus(a, Times(b, ArcCot(Times(c, x)))), p), PolyLog(Plus(k,
                                  C1), u),
                              Power(Times(C2, c, d), CN1)),
                          x),
                      Dist(Times(C1D2, b, p, CI),
                          Integrate(
                              Times(Power(Plus(a, Times(b, ArcCot(Times(c, x)))), Subtract(p, C1)),
                                  PolyLog(Plus(k, C1), u), Power(Plus(d, Times(e, Sqr(x))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, k), x), IGtQ(p, C0), EqQ(e, Times(Sqr(c), d)),
                      EqQ(Subtract(Sqr(u),
                          Sqr(Subtract(C1, Times(C2, CI, Power(Subtract(CI, Times(c, x)), CN1))))),
                          C0)))),
          IIntegrate(5000,
              Integrate(
                  Times(Power(Plus(a_DEFAULT, Times(ArcCot(Times(c_DEFAULT, x_)), b_DEFAULT)), CN1),
                      Power(Plus(a_DEFAULT, Times(ArcTan(Times(c_DEFAULT, x_)), b_DEFAULT)), CN1),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Simp(Times(
                      Plus(Negate(Log(Plus(a, Times(b, ArcCot(Times(c, x)))))),
                          Log(Plus(a, Times(b, ArcTan(Times(c, x)))))),
                      Power(Times(b, c, d,
                          Plus(Times(C2, a), Times(b, ArcCot(Times(c, x))),
                              Times(b, ArcTan(Times(c, x))))),
                          CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(e, Times(Sqr(c), d))))));
}
