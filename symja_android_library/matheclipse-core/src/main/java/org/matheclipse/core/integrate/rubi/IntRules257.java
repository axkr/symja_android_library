package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.ArcCot;
import static org.matheclipse.core.expression.F.ArcTan;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C1D4;
import static org.matheclipse.core.expression.F.CI;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.Greater;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.s_DEFAULT;
import static org.matheclipse.core.expression.F.u_DEFAULT;
import static org.matheclipse.core.expression.F.v_;
import static org.matheclipse.core.expression.F.w_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.Pi;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.f;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.s;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.w;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
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
