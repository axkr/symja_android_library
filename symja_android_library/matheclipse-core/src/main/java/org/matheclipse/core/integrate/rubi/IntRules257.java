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
class IntRules257 {
  public static IAST RULES = List(
      IIntegrate(5141,
          Integrate(
              ArcTan(
                  Plus(
                      a_DEFAULT, Times(b_DEFAULT,
                          Power(f_, Plus(c_DEFAULT, Times(d_DEFAULT, x_)))))),
              x_Symbol),
          Condition(
              Subtract(
                  Dist(Times(C1D2, CI),
                      Integrate(
                          Log(Subtract(Subtract(C1, Times(CI, a)),
                              Times(CI, b, Power(f, Plus(c, Times(d, x)))))),
                          x),
                      x),
                  Dist(Times(C1D2, CI),
                      Integrate(
                          Log(Plus(C1, Times(CI, a), Times(CI, b, Power(f, Plus(c, Times(d, x)))))),
                          x),
                      x)),
              FreeQ(List(a, b, c, d, f), x))),
      IIntegrate(5142,
          Integrate(
              ArcCot(
                  Plus(
                      a_DEFAULT, Times(b_DEFAULT,
                          Power(f_, Plus(c_DEFAULT, Times(d_DEFAULT, x_)))))),
              x_Symbol),
          Condition(
              Subtract(
                  Dist(Times(C1D2, CI),
                      Integrate(
                          Log(Subtract(C1,
                              Times(
                                  CI, Power(Plus(a, Times(b, Power(f, Plus(c, Times(d, x))))),
                                      CN1)))),
                          x),
                      x),
                  Dist(Times(C1D2, CI),
                      Integrate(
                          Log(Plus(C1,
                              Times(
                                  CI,
                                  Power(Plus(a, Times(b, Power(f, Plus(c, Times(d, x))))), CN1)))),
                          x),
                      x)),
              FreeQ(List(a, b, c, d, f), x))),
      IIntegrate(5143,
          Integrate(
              Times(ArcTan(Plus(a_DEFAULT,
                  Times(b_DEFAULT, Power(f_, Plus(c_DEFAULT, Times(d_DEFAULT, x_)))))), Power(x_,
                      m_DEFAULT)),
              x_Symbol),
          Condition(
              Subtract(
                  Dist(Times(C1D2, CI),
                      Integrate(Times(Power(x, m),
                          Log(Subtract(Subtract(C1, Times(CI, a)),
                              Times(CI, b, Power(f, Plus(c, Times(d, x))))))),
                          x),
                      x),
                  Dist(Times(C1D2, CI),
                      Integrate(
                          Times(Power(x, m),
                              Log(Plus(C1, Times(CI, a),
                                  Times(CI, b, Power(f, Plus(c, Times(d, x))))))),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, d, f), x), IntegerQ(m), Greater(m, C0)))),
      IIntegrate(5144,
          Integrate(
              Times(
                  ArcCot(
                      Plus(
                          a_DEFAULT, Times(b_DEFAULT,
                              Power(f_, Plus(c_DEFAULT, Times(d_DEFAULT, x_)))))),
                  Power(x_, m_DEFAULT)),
              x_Symbol),
          Condition(
              Subtract(
                  Dist(Times(C1D2, CI),
                      Integrate(
                          Times(
                              Power(x, m), Log(
                                  Subtract(C1,
                                      Times(CI,
                                          Power(
                                              Plus(a, Times(b, Power(f,
                                                  Plus(c, Times(d, x))))),
                                              CN1))))),
                          x),
                      x),
                  Dist(Times(C1D2, CI),
                      Integrate(
                          Times(Power(x, m),
                              Log(Plus(C1,
                                  Times(CI,
                                      Power(Plus(a, Times(b, Power(f, Plus(c, Times(d, x))))),
                                          CN1))))),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, d, f), x), IntegerQ(m), Greater(m, C0)))),
      IIntegrate(5145,
          Integrate(
              Times(
                  Power(ArcTan(Times(
                      c_DEFAULT, Power(Plus(a_DEFAULT, Times(b_DEFAULT, Power(x_, n_DEFAULT))),
                          CN1))),
                      m_DEFAULT),
                  u_DEFAULT),
              x_Symbol),
          Condition(
              Integrate(
                  Times(u,
                      Power(
                          ArcCot(Plus(Times(a, Power(c, CN1)),
                              Times(b, Power(x, n), Power(c, CN1)))),
                          m)),
                  x),
              FreeQ(List(a, b, c, n, m), x))),
      IIntegrate(5146,
          Integrate(
              Times(
                  Power(
                      ArcCot(Times(
                          c_DEFAULT, Power(Plus(a_DEFAULT, Times(b_DEFAULT, Power(x_, n_DEFAULT))),
                              CN1))),
                      m_DEFAULT),
                  u_DEFAULT),
              x_Symbol),
          Condition(
              Integrate(
                  Times(u,
                      Power(
                          ArcTan(Plus(Times(a, Power(c, CN1)),
                              Times(b, Power(x, n), Power(c, CN1)))),
                          m)),
                  x),
              FreeQ(List(a, b, c, n, m), x))),
      IIntegrate(5147,
          Integrate(
              ArcTan(Times(c_DEFAULT, x_,
                  Power(Plus(a_DEFAULT, Times(b_DEFAULT, Sqr(x_))), CN1D2))),
              x_Symbol),
          Condition(
              Subtract(Simp(
                  Times(x, ArcTan(Times(c, x, Power(Plus(a, Times(b, Sqr(x))), CN1D2)))), x),
                  Dist(c, Integrate(Times(x, Power(Plus(a, Times(b, Sqr(x))), CN1D2)), x), x)),
              And(FreeQ(List(a, b, c), x), EqQ(Plus(b, Sqr(c)), C0)))),
      IIntegrate(5148,
          Integrate(
              ArcCot(Times(c_DEFAULT, x_,
                  Power(Plus(a_DEFAULT, Times(b_DEFAULT, Sqr(x_))), CN1D2))),
              x_Symbol),
          Condition(
              Plus(
                  Simp(Times(x, ArcCot(Times(c, x,
                      Power(Plus(a, Times(b, Sqr(x))), CN1D2)))), x),
                  Dist(c, Integrate(Times(x, Power(Plus(a, Times(b, Sqr(x))), CN1D2)), x), x)),
              And(FreeQ(List(a, b, c), x), EqQ(Plus(b, Sqr(c)), C0)))),
      IIntegrate(5149,
          Integrate(
              Times(
                  ArcTan(Times(c_DEFAULT, x_,
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, Sqr(x_))), CN1D2))),
                  Power(x_, CN1)),
              x_Symbol),
          Condition(
              Subtract(
                  Simp(
                      Times(ArcTan(Times(c, x, Power(Plus(a, Times(b, Sqr(x))), CN1D2))),
                          Log(x)),
                      x),
                  Dist(c, Integrate(Times(Log(x), Power(Plus(a, Times(b, Sqr(x))), CN1D2)), x), x)),
              And(FreeQ(List(a, b, c), x), EqQ(Plus(b, Sqr(c)), C0)))),
      IIntegrate(5150,
          Integrate(
              Times(
                  ArcCot(Times(c_DEFAULT, x_,
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, Sqr(x_))), CN1D2))),
                  Power(x_, CN1)),
              x_Symbol),
          Condition(
              Plus(
                  Simp(
                      Times(ArcCot(Times(c, x, Power(Plus(a, Times(b, Sqr(x))), CN1D2))),
                          Log(x)),
                      x),
                  Dist(c, Integrate(Times(Log(x), Power(Plus(a, Times(b, Sqr(x))), CN1D2)), x), x)),
              And(FreeQ(List(a, b, c), x), EqQ(Plus(b, Sqr(c)), C0)))),
      IIntegrate(5151,
          Integrate(
              Times(
                  ArcTan(Times(c_DEFAULT, x_,
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, Sqr(x_))), CN1D2))),
                  Power(Times(d_DEFAULT, x_), m_DEFAULT)),
              x_Symbol),
          Condition(
              Subtract(
                  Simp(Times(Power(Times(d, x), Plus(m, C1)),
                      ArcTan(Times(c, x, Power(Plus(a, Times(b, Sqr(x))), CN1D2))), Power(
                          Times(d, Plus(m, C1)), CN1)),
                      x),
                  Dist(
                      Times(c, Power(Times(d, Plus(m, C1)),
                          CN1)),
                      Integrate(
                          Times(
                              Power(Times(d, x), Plus(m,
                                  C1)),
                              Power(Plus(a, Times(b, Sqr(x))), CN1D2)),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, d, m), x), EqQ(Plus(b, Sqr(c)), C0), NeQ(m, CN1)))),
      IIntegrate(5152,
          Integrate(
              Times(
                  ArcCot(Times(c_DEFAULT, x_,
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, Sqr(x_))), CN1D2))),
                  Power(Times(d_DEFAULT, x_), m_DEFAULT)),
              x_Symbol),
          Condition(
              Plus(
                  Simp(
                      Times(Power(Times(d, x), Plus(m, C1)),
                          ArcCot(Times(c, x, Power(Plus(a, Times(b, Sqr(x))), CN1D2))), Power(
                              Times(d, Plus(m, C1)), CN1)),
                      x),
                  Dist(
                      Times(c, Power(Times(d, Plus(m, C1)),
                          CN1)),
                      Integrate(
                          Times(
                              Power(Times(d, x), Plus(m,
                                  C1)),
                              Power(Plus(a, Times(b, Sqr(x))), CN1D2)),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, d, m), x), EqQ(Plus(b, Sqr(c)), C0), NeQ(m, CN1)))),
      IIntegrate(5153,
          Integrate(
              Times(
                  Power(ArcTan(Times(c_DEFAULT, x_,
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, Sqr(x_))), CN1D2))), CN1),
                  Power(Plus(a_DEFAULT, Times(b_DEFAULT, Sqr(x_))), CN1D2)),
              x_Symbol),
          Condition(
              Simp(
                  Times(
                      C1, Log(ArcTan(Times(c, x, Power(Plus(a, Times(b, Sqr(x))), CN1D2)))), Power(
                          c, CN1)),
                  x),
              And(FreeQ(List(a, b, c), x), EqQ(Plus(b, Sqr(c)), C0)))),
      IIntegrate(5154,
          Integrate(
              Times(
                  Power(
                      ArcCot(Times(c_DEFAULT, x_, Power(Plus(a_DEFAULT, Times(b_DEFAULT, Sqr(x_))),
                          CN1D2))),
                      CN1),
                  Power(Plus(a_DEFAULT, Times(b_DEFAULT, Sqr(x_))), CN1D2)),
              x_Symbol),
          Condition(
              Negate(
                  Simp(
                      Times(
                          Log(ArcCot(Times(c, x, Power(Plus(a, Times(b, Sqr(x))), CN1D2)))), Power(
                              c, CN1)),
                      x)),
              And(FreeQ(List(a, b, c), x), EqQ(Plus(b, Sqr(c)), C0)))),
      IIntegrate(5155,
          Integrate(
              Times(
                  Power(
                      ArcTan(Times(c_DEFAULT, x_, Power(Plus(a_DEFAULT, Times(b_DEFAULT, Sqr(x_))),
                          CN1D2))),
                      m_DEFAULT),
                  Power(Plus(a_DEFAULT, Times(b_DEFAULT, Sqr(x_))), CN1D2)),
              x_Symbol),
          Condition(
              Simp(
                  Times(
                      Power(
                          ArcTan(Times(c, x, Power(Plus(a, Times(b, Sqr(x))), CN1D2))), Plus(m,
                              C1)),
                      Power(Times(c, Plus(m, C1)), CN1)),
                  x),
              And(FreeQ(List(a, b, c, m), x), EqQ(Plus(b, Sqr(c)), C0), NeQ(m, CN1)))),
      IIntegrate(5156,
          Integrate(
              Times(
                  Power(ArcCot(Times(c_DEFAULT, x_,
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, Sqr(x_))), CN1D2))), m_DEFAULT),
                  Power(Plus(a_DEFAULT, Times(b_DEFAULT, Sqr(x_))), CN1D2)),
              x_Symbol),
          Condition(
              Negate(
                  Simp(
                      Times(
                          Power(
                              ArcCot(Times(c, x, Power(Plus(a, Times(b, Sqr(x))), CN1D2))), Plus(m,
                                  C1)),
                          Power(Times(c, Plus(m, C1)), CN1)),
                      x)),
              And(FreeQ(List(a, b, c, m), x), EqQ(Plus(b, Sqr(c)), C0), NeQ(m, CN1)))),
      IIntegrate(5157,
          Integrate(
              Times(
                  Power(
                      ArcTan(
                          Times(
                              c_DEFAULT, x_, Power(Plus(a_DEFAULT, Times(b_DEFAULT, Sqr(x_))),
                                  CN1D2))),
                      m_DEFAULT),
                  Power(Plus(d_DEFAULT, Times(e_DEFAULT, Sqr(x_))), CN1D2)),
              x_Symbol),
          Condition(
              Dist(
                  Times(Sqrt(Plus(a, Times(b, Sqr(x)))), Power(Plus(d, Times(e, Sqr(x))),
                      CN1D2)),
                  Integrate(
                      Times(
                          Power(ArcTan(
                              Times(c, x, Power(Plus(a, Times(b, Sqr(x))), CN1D2))), m),
                          Power(Plus(a, Times(b, Sqr(x))), CN1D2)),
                      x),
                  x),
              And(FreeQ(List(a, b, c, d, e, m), x), EqQ(Plus(b, Sqr(c)), C0),
                  EqQ(Subtract(Times(b, d), Times(a, e)), C0)))),
      IIntegrate(5158,
          Integrate(
              Times(
                  Power(
                      ArcCot(
                          Times(
                              c_DEFAULT, x_, Power(Plus(a_DEFAULT, Times(b_DEFAULT, Sqr(x_))),
                                  CN1D2))),
                      m_DEFAULT),
                  Power(Plus(d_DEFAULT, Times(e_DEFAULT, Sqr(x_))), CN1D2)),
              x_Symbol),
          Condition(
              Dist(Times(Sqrt(Plus(a, Times(b, Sqr(x)))), Power(Plus(d, Times(e, Sqr(x))), CN1D2)),
                  Integrate(
                      Times(Power(ArcCot(Times(c, x, Power(Plus(a, Times(b, Sqr(x))), CN1D2))), m),
                          Power(Plus(a, Times(b, Sqr(x))), CN1D2)),
                      x),
                  x),
              And(FreeQ(List(a, b, c, d, e, m), x), EqQ(Plus(b, Sqr(c)), C0),
                  EqQ(Subtract(Times(b, d), Times(a, e)), C0)))),
      IIntegrate(5159,
          Integrate(Times(ArcTan(Plus(v_, Times(s_DEFAULT, Sqrt(w_)))), u_DEFAULT), x_Symbol),
          Condition(
              Plus(Dist(Times(C1D4, Pi, s), Integrate(u, x), x),
                  Dist(C1D2, Integrate(Times(u, ArcTan(v)), x), x)),
              And(EqQ(Sqr(s), C1), EqQ(w, Plus(Sqr(v), C1))))),
      IIntegrate(5160,
          Integrate(Times(ArcCot(Plus(v_, Times(s_DEFAULT, Sqrt(w_)))), u_DEFAULT), x_Symbol),
          Condition(
              Subtract(Dist(Times(C1D4, Pi, s), Integrate(u, x), x),
                  Dist(C1D2, Integrate(Times(u, ArcTan(v)), x), x)),
              And(EqQ(Sqr(s), C1), EqQ(w, Plus(Sqr(v), C1))))));
}
