package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.ArcCoth;
import static org.matheclipse.core.expression.F.ArcTanh;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.MatchQ;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.g_DEFAULT;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.q_DEFAULT;
import static org.matheclipse.core.expression.F.u_DEFAULT;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.f;
import static org.matheclipse.core.expression.S.g;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.q;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ILtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntHide;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Unintegrable;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules304 {
  public static IAST RULES = List(
      IIntegrate(6081,
          Integrate(Times(Plus(a_DEFAULT, Times(ArcTanh(Times(c_DEFAULT, x_)), b_DEFAULT)),
              Plus(d_DEFAULT, Times(Log(Plus(f_DEFAULT, Times(g_DEFAULT, Sqr(x_)))),
                  e_DEFAULT)),
              Power(x_, m_DEFAULT)), x_Symbol),
          Condition(
              Plus(
                  Simp(
                      Times(Power(x, Plus(m, C1)),
                          Plus(d, Times(e, Log(Plus(f, Times(g, Sqr(x)))))), Plus(a,
                              Times(b, ArcTanh(Times(c, x)))),
                          Power(Plus(m, C1), CN1)),
                      x),
                  Negate(
                      Dist(Times(b, c, Power(Plus(m, C1), CN1)),
                          Integrate(
                              Times(Power(x, Plus(m, C1)),
                                  Plus(d, Times(e, Log(Plus(f, Times(g, Sqr(x)))))),
                                  Power(Subtract(C1, Times(Sqr(c), Sqr(x))), CN1)),
                              x),
                          x)),
                  Negate(
                      Dist(
                          Times(C2, e, g, Power(Plus(m, C1),
                              CN1)),
                          Integrate(
                              Times(
                                  Power(x, Plus(m, C2)), Plus(a, Times(b,
                                      ArcTanh(Times(c, x)))),
                                  Power(Plus(f, Times(g, Sqr(x))), CN1)),
                              x),
                          x))),
              And(FreeQ(List(a, b, c, d, e, f, g), x), ILtQ(Times(C1D2, m), C0)))),
      IIntegrate(6082,
          Integrate(Times(Plus(a_DEFAULT, Times(ArcCoth(Times(c_DEFAULT, x_)), b_DEFAULT)),
              Plus(d_DEFAULT, Times(Log(Plus(f_DEFAULT, Times(g_DEFAULT, Sqr(x_)))), e_DEFAULT)),
              Power(x_, m_DEFAULT)), x_Symbol),
          Condition(Plus(
              Simp(Times(Power(x, Plus(m, C1)), Plus(d, Times(e, Log(Plus(f, Times(g, Sqr(x)))))),
                  Plus(a, Times(b, ArcCoth(Times(c, x)))), Power(Plus(m, C1), CN1)), x),
              Negate(Dist(Times(b, c, Power(Plus(m, C1), CN1)),
                  Integrate(Times(Power(x, Plus(m, C1)),
                      Plus(d, Times(e, Log(Plus(f, Times(g, Sqr(x)))))),
                      Power(Subtract(C1, Times(Sqr(c), Sqr(x))), CN1)), x),
                  x)),
              Negate(
                  Dist(Times(C2, e, g, Power(Plus(m, C1), CN1)),
                      Integrate(
                          Times(
                              Power(x, Plus(m, C2)), Plus(a, Times(b,
                                  ArcCoth(Times(c, x)))),
                              Power(Plus(f, Times(g, Sqr(x))), CN1)),
                          x),
                      x))),
              And(FreeQ(List(a, b, c, d, e, f, g), x), ILtQ(Times(C1D2, m), C0)))),
      IIntegrate(6083,
          Integrate(
              Times(
                  Plus(a_DEFAULT, Times(ArcTanh(Times(c_DEFAULT, x_)),
                      b_DEFAULT)),
                  Plus(d_DEFAULT, Times(Log(Plus(f_DEFAULT, Times(g_DEFAULT, Sqr(x_)))),
                      e_DEFAULT)),
                  Power(x_, m_DEFAULT)),
              x_Symbol),
          Condition(
              With(
                  List(
                      Set(u,
                          IntHide(
                              Times(Power(x, m), Plus(d, Times(e, Log(Plus(f, Times(g, Sqr(x))))))),
                              x))),
                  Subtract(
                      Dist(Plus(a,
                          Times(b, ArcTanh(Times(c, x)))), u, x),
                      Dist(Times(b, c),
                          Integrate(
                              ExpandIntegrand(Times(u,
                                  Power(Subtract(C1, Times(Sqr(c), Sqr(x))), CN1)),
                                  x),
                              x),
                          x))),
              And(FreeQ(List(a, b, c, d, e, f, g), x), IGtQ(Times(C1D2, Plus(m, C1)), C0)))),
      IIntegrate(6084,
          Integrate(
              Times(
                  Plus(a_DEFAULT, Times(ArcCoth(Times(c_DEFAULT, x_)),
                      b_DEFAULT)),
                  Plus(d_DEFAULT, Times(Log(Plus(f_DEFAULT, Times(g_DEFAULT, Sqr(x_)))),
                      e_DEFAULT)),
                  Power(x_, m_DEFAULT)),
              x_Symbol),
          Condition(
              With(
                  List(
                      Set(u,
                          IntHide(
                              Times(Power(x, m), Plus(d, Times(e, Log(Plus(f, Times(g, Sqr(x))))))),
                              x))),
                  Subtract(
                      Dist(Plus(a,
                          Times(b, ArcCoth(Times(c, x)))), u, x),
                      Dist(Times(b, c),
                          Integrate(ExpandIntegrand(
                              Times(u, Power(Subtract(C1, Times(Sqr(c), Sqr(x))), CN1)), x), x),
                          x))),
              And(FreeQ(List(a, b, c, d, e, f, g), x), IGtQ(Times(C1D2, Plus(m, C1)), C0)))),
      IIntegrate(6085,
          Integrate(
              Times(
                  Plus(a_DEFAULT, Times(ArcTanh(Times(c_DEFAULT, x_)),
                      b_DEFAULT)),
                  Plus(d_DEFAULT, Times(Log(Plus(f_DEFAULT, Times(g_DEFAULT, Sqr(x_)))),
                      e_DEFAULT)),
                  Power(x_, m_DEFAULT)),
              x_Symbol),
          Condition(
              With(
                  List(
                      Set(u, IntHide(Times(Power(x, m), Plus(a, Times(b, ArcTanh(Times(c, x))))),
                          x))),
                  Subtract(Dist(Plus(d, Times(e, Log(Plus(f, Times(g, Sqr(x)))))), u, x),
                      Dist(Times(C2, e, g), Integrate(
                          ExpandIntegrand(Times(x, u, Power(Plus(f, Times(g, Sqr(x))), CN1)), x),
                          x), x))),
              And(FreeQ(List(a, b, c, d, e, f, g), x), IntegerQ(m), NeQ(m, CN1)))),
      IIntegrate(6086,
          Integrate(
              Times(Plus(a_DEFAULT, Times(ArcCoth(Times(c_DEFAULT, x_)), b_DEFAULT)),
                  Plus(d_DEFAULT, Times(Log(Plus(f_DEFAULT, Times(g_DEFAULT, Sqr(x_)))),
                      e_DEFAULT)),
                  Power(x_, m_DEFAULT)),
              x_Symbol),
          Condition(With(
              List(Set(u, IntHide(Times(Power(x, m), Plus(a, Times(b, ArcCoth(Times(c, x))))), x))),
              Subtract(Dist(Plus(d, Times(e, Log(Plus(f, Times(g, Sqr(x)))))), u, x), Dist(
                  Times(C2, e, g),
                  Integrate(ExpandIntegrand(Times(x, u, Power(Plus(f, Times(g, Sqr(x))), CN1)), x),
                      x),
                  x))),
              And(FreeQ(List(a, b, c, d, e, f, g), x), IntegerQ(m), NeQ(m, CN1)))),
      IIntegrate(6087,
          Integrate(
              Times(
                  Sqr(Plus(a_DEFAULT, Times(ArcTanh(Times(c_DEFAULT, x_)),
                      b_DEFAULT))),
                  Plus(d_DEFAULT, Times(Log(Plus(f_, Times(g_DEFAULT, Sqr(x_)))), e_DEFAULT)), x_),
              x_Symbol),
          Condition(
              Plus(
                  Simp(
                      Times(Plus(f, Times(g, Sqr(x))), Plus(d, Times(e, Log(
                          Plus(f, Times(g, Sqr(x)))))), Sqr(Plus(a,
                              Times(b, ArcTanh(Times(c, x))))),
                          Power(Times(C2, g), CN1)),
                      x),
                  Dist(Times(b, Power(c, CN1)),
                      Integrate(
                          Times(Plus(d, Times(e, Log(Plus(f, Times(g, Sqr(x)))))),
                              Plus(a, Times(b, ArcTanh(Times(c, x))))),
                          x),
                      x),
                  Dist(Times(b, c, e),
                      Integrate(
                          Times(Sqr(x), Plus(a, Times(b, ArcTanh(Times(c, x)))),
                              Power(Subtract(C1, Times(Sqr(c), Sqr(x))), CN1)),
                          x),
                      x),
                  Negate(
                      Simp(
                          Times(C1D2, e, Sqr(x),
                              Sqr(Plus(a, Times(b, ArcTanh(Times(c, x)))))),
                          x))),
              And(FreeQ(List(a, b, c, d, e, f, g), x), EqQ(Plus(Times(Sqr(c), f), g), C0)))),
      IIntegrate(6088,
          Integrate(
              Times(
                  Sqr(Plus(a_DEFAULT, Times(ArcCoth(Times(c_DEFAULT, x_)),
                      b_DEFAULT))),
                  Plus(d_DEFAULT, Times(Log(Plus(f_, Times(g_DEFAULT, Sqr(x_)))), e_DEFAULT)), x_),
              x_Symbol),
          Condition(
              Plus(
                  Simp(
                      Times(Plus(f, Times(g, Sqr(x))),
                          Plus(d, Times(e, Log(Plus(f, Times(g, Sqr(x)))))), Sqr(Plus(a,
                              Times(b, ArcCoth(Times(c, x))))),
                          Power(Times(C2, g), CN1)),
                      x),
                  Dist(
                      Times(b, Power(c,
                          CN1)),
                      Integrate(
                          Times(Plus(d, Times(e, Log(Plus(f, Times(g, Sqr(x)))))),
                              Plus(a, Times(b, ArcCoth(Times(c, x))))),
                          x),
                      x),
                  Dist(Times(b, c, e),
                      Integrate(
                          Times(Sqr(x), Plus(a, Times(b, ArcCoth(Times(c, x)))),
                              Power(Subtract(C1, Times(Sqr(c), Sqr(x))), CN1)),
                          x),
                      x),
                  Negate(
                      Simp(Times(C1D2, e, Sqr(x), Sqr(Plus(a, Times(b, ArcCoth(Times(c, x)))))),
                          x))),
              And(FreeQ(List(a, b, c, d, e, f, g), x), EqQ(Plus(Times(Sqr(c), f), g), C0)))),
      IIntegrate(6089,
          Integrate(
              Times(
                  Power(Plus(a_DEFAULT, Times(ArcTanh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                      p_DEFAULT),
                  u_DEFAULT),
              x_Symbol),
          Condition(
              Unintegrable(Times(u,
                  Power(Plus(a, Times(b, ArcTanh(Times(c, x)))), p)), x),
              And(FreeQ(List(a, b, c, p), x),
                  Or(EqQ(u, C1),
                      MatchQ(u,
                          Condition(Power(Plus(d_DEFAULT, Times(e_DEFAULT, x)), q_DEFAULT),
                              FreeQ(List(d, e, q), x))),
                      MatchQ(u,
                          Condition(
                              Times(Power(Times(f_DEFAULT, x), m_DEFAULT),
                                  Power(Plus(d_DEFAULT, Times(e_DEFAULT, x)), q_DEFAULT)),
                              FreeQ(List(d, e, f, m, q), x))),
                      MatchQ(u,
                          Condition(Power(Plus(d_DEFAULT, Times(e_DEFAULT, Sqr(x))), q_DEFAULT),
                              FreeQ(List(d, e, q), x))),
                      MatchQ(u,
                          Condition(
                              Times(Power(Times(f_DEFAULT, x), m_DEFAULT),
                                  Power(Plus(d_DEFAULT, Times(e_DEFAULT, Sqr(x))), q_DEFAULT)),
                              FreeQ(List(d, e, f, m, q), x))))))),
      IIntegrate(6090,
          Integrate(
              Times(
                  Power(Plus(a_DEFAULT, Times(ArcCoth(Times(c_DEFAULT, x_)), b_DEFAULT)),
                      p_DEFAULT),
                  u_DEFAULT),
              x_Symbol),
          Condition(
              Unintegrable(Times(u,
                  Power(Plus(a, Times(b, ArcCoth(Times(c, x)))), p)), x),
              And(FreeQ(List(a, b, c, p), x),
                  Or(EqQ(u, C1),
                      MatchQ(u,
                          Condition(Power(Plus(d_DEFAULT, Times(e_DEFAULT, x)), q_DEFAULT),
                              FreeQ(List(d, e, q), x))),
                      MatchQ(u,
                          Condition(
                              Times(
                                  Power(Times(f_DEFAULT, x), m_DEFAULT),
                                  Power(Plus(d_DEFAULT, Times(e_DEFAULT, x)), q_DEFAULT)),
                              FreeQ(List(d, e, f, m, q), x))),
                      MatchQ(u,
                          Condition(Power(Plus(d_DEFAULT, Times(e_DEFAULT, Sqr(x))), q_DEFAULT),
                              FreeQ(List(d, e, q), x))),
                      MatchQ(u,
                          Condition(
                              Times(Power(Times(f_DEFAULT, x), m_DEFAULT), Power(
                                  Plus(d_DEFAULT, Times(e_DEFAULT, Sqr(x))), q_DEFAULT)),
                              FreeQ(List(d, e, f, m, q), x))))))),
      IIntegrate(
          6091, Integrate(ArcTanh(
              Times(c_DEFAULT, Power(x_, n_))), x_Symbol),
          Condition(
              Subtract(
                  Simp(Times(x,
                      ArcTanh(Times(c, Power(x, n)))), x),
                  Dist(Times(c, n),
                      Integrate(
                          Times(
                              Power(x, n), Power(Subtract(C1,
                                  Times(Sqr(c), Power(x, Times(C2, n)))), CN1)),
                          x),
                      x)),
              FreeQ(List(c, n), x))),
      IIntegrate(
          6092, Integrate(ArcCoth(
              Times(c_DEFAULT, Power(x_, n_))), x_Symbol),
          Condition(
              Subtract(
                  Simp(Times(x,
                      ArcCoth(Times(c, Power(x, n)))), x),
                  Dist(Times(c, n),
                      Integrate(Times(
                          Power(x, n), Power(Subtract(C1, Times(Sqr(c), Power(x, Times(C2, n)))),
                              CN1)),
                          x),
                      x)),
              FreeQ(List(c, n), x))),
      IIntegrate(6093,
          Integrate(
              Power(
                  Plus(a_DEFAULT, Times(ArcTanh(Times(c_DEFAULT, Power(x_, n_))),
                      b_DEFAULT)),
                  p_DEFAULT),
              x_Symbol),
          Condition(
              Integrate(
                  ExpandIntegrand(
                      Power(
                          Subtract(
                              Plus(a, Times(C1D2, b, Log(Plus(C1,
                                  Times(c, Power(x, n)))))),
                              Times(C1D2, b, Log(Subtract(C1, Times(c, Power(x, n)))))),
                          p),
                      x),
                  x),
              And(FreeQ(List(a, b, c, n), x), IGtQ(p, C0), IntegerQ(n)))),
      IIntegrate(6094,
          Integrate(
              Power(
                  Plus(a_DEFAULT, Times(ArcCoth(Times(c_DEFAULT, Power(x_, n_))),
                      b_DEFAULT)),
                  p_DEFAULT),
              x_Symbol),
          Condition(
              Integrate(
                  ExpandIntegrand(
                      Power(
                          Subtract(
                              Plus(a, Times(C1D2, b,
                                  Log(Plus(C1, Power(Times(Power(x, n), c), CN1))))),
                              Times(C1D2, b, Log(Subtract(C1, Power(Times(Power(x, n), c), CN1))))),
                          p),
                      x),
                  x),
              And(FreeQ(List(a, b, c, n), x), IGtQ(p, C0), IntegerQ(n)))),
      IIntegrate(6095,
          Integrate(
              Times(
                  Power(Plus(a_DEFAULT, Times(ArcTanh(Times(c_DEFAULT, Power(x_, n_))), b_DEFAULT)),
                      p_DEFAULT),
                  Power(x_, CN1)),
              x_Symbol),
          Condition(
              Dist(Power(n, CN1),
                  Subst(
                      Integrate(
                          Times(Power(Plus(a,
                              Times(b, ArcTanh(Times(c, x)))), p), Power(x,
                                  CN1)),
                          x),
                      x, Power(x, n)),
                  x),
              And(FreeQ(List(a, b, c, n), x), IGtQ(p, C0)))),
      IIntegrate(6096,
          Integrate(
              Times(
                  Power(
                      Plus(a_DEFAULT, Times(ArcCoth(Times(c_DEFAULT, Power(x_, n_))),
                          b_DEFAULT)),
                      p_DEFAULT),
                  Power(x_, CN1)),
              x_Symbol),
          Condition(
              Dist(Power(n, CN1),
                  Subst(
                      Integrate(
                          Times(Power(Plus(a,
                              Times(b, ArcCoth(Times(c, x)))), p), Power(x,
                                  CN1)),
                          x),
                      x, Power(x, n)),
                  x),
              And(FreeQ(List(a, b, c, n), x), IGtQ(p, C0)))),
      IIntegrate(6097,
          Integrate(
              Times(
                  Plus(a_DEFAULT, Times(ArcTanh(Times(c_DEFAULT, Power(x_, n_))),
                      b_DEFAULT)),
                  Power(Times(d_DEFAULT, x_), m_DEFAULT)),
              x_Symbol),
          Condition(
              Subtract(
                  Simp(
                      Times(Power(Times(d, x), Plus(m, C1)),
                          Plus(a, Times(b, ArcTanh(Times(c, Power(x, n))))), Power(
                              Times(d, Plus(m, C1)), CN1)),
                      x),
                  Dist(Times(b, c, n, Power(Times(d, Plus(m, C1)), CN1)),
                      Integrate(
                          Times(
                              Power(x, Subtract(n, C1)), Power(Times(d, x), Plus(m,
                                  C1)),
                              Power(Subtract(C1, Times(Sqr(c), Power(x, Times(C2, n)))), CN1)),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, d, m, n), x), NeQ(m, CN1)))),
      IIntegrate(6098,
          Integrate(
              Times(
                  Plus(a_DEFAULT, Times(ArcCoth(Times(c_DEFAULT, Power(x_, n_))),
                      b_DEFAULT)),
                  Power(Times(d_DEFAULT, x_), m_DEFAULT)),
              x_Symbol),
          Condition(
              Subtract(
                  Simp(Times(Power(Times(d, x), Plus(m, C1)),
                      Plus(a, Times(b, ArcCoth(Times(c, Power(x, n))))), Power(
                          Times(d, Plus(m, C1)), CN1)),
                      x),
                  Dist(Times(b, c, n, Power(Times(d, Plus(m, C1)), CN1)),
                      Integrate(Times(Power(x, Subtract(n, C1)), Power(Times(d, x), Plus(m, C1)),
                          Power(Subtract(C1, Times(Sqr(c), Power(x, Times(C2, n)))), CN1)), x),
                      x)),
              And(FreeQ(List(a, b, c, d, m, n), x), NeQ(m, CN1)))),
      IIntegrate(6099,
          Integrate(
              Times(
                  Power(Plus(a_DEFAULT, Times(ArcTanh(Times(c_DEFAULT, Power(x_, n_))),
                      b_DEFAULT)), p_DEFAULT),
                  Power(Times(d_DEFAULT, x_), m_DEFAULT)),
              x_Symbol),
          Condition(
              Integrate(
                  ExpandIntegrand(
                      Times(Power(Times(d, x), m),
                          Power(
                              Subtract(
                                  Plus(a, Times(C1D2, b, Log(Plus(C1, Times(c, Power(x, n)))))),
                                  Times(C1D2, b, Log(Subtract(C1, Times(c, Power(x, n)))))),
                              p)),
                      x),
                  x),
              And(FreeQ(List(a, b, c, d, m, n), x), IGtQ(p, C0), IntegerQ(m), IntegerQ(n)))),
      IIntegrate(6100,
          Integrate(
              Times(
                  Power(
                      Plus(a_DEFAULT, Times(ArcCoth(Times(c_DEFAULT, Power(x_, n_))),
                          b_DEFAULT)),
                      p_DEFAULT),
                  Power(Times(d_DEFAULT, x_), m_DEFAULT)),
              x_Symbol),
          Condition(
              Integrate(
                  ExpandIntegrand(Times(Power(Times(d, x), m),
                      Power(
                          Subtract(
                              Plus(a,
                                  Times(C1D2, b, Log(Plus(C1, Power(Times(Power(x, n), c), CN1))))),
                              Times(C1D2, b, Log(Subtract(C1, Power(Times(Power(x, n), c), CN1))))),
                          p)),
                      x),
                  x),
              And(FreeQ(List(a, b, c, d, m, n), x), IGtQ(p, C0), IntegerQ(m), IntegerQ(n)))));
}
