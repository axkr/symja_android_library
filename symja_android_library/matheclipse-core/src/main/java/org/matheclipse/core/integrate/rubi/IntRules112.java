package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Denominator;
import static org.matheclipse.core.expression.F.Expand;
import static org.matheclipse.core.expression.F.F_;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.FullSimplify;
import static org.matheclipse.core.expression.F.G_;
import static org.matheclipse.core.expression.F.Hypergeometric2F1;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Numerator;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Simplify;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.g_DEFAULT;
import static org.matheclipse.core.expression.F.h_DEFAULT;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.q_DEFAULT;
import static org.matheclipse.core.expression.F.r_DEFAULT;
import static org.matheclipse.core.expression.F.s_DEFAULT;
import static org.matheclipse.core.expression.F.t_DEFAULT;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.v_;
import static org.matheclipse.core.expression.F.w_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.FSymbol;
import static org.matheclipse.core.expression.S.GSymbol;
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
import static org.matheclipse.core.expression.S.q;
import static org.matheclipse.core.expression.S.r;
import static org.matheclipse.core.expression.S.s;
import static org.matheclipse.core.expression.S.t;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.w;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandToSum;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ILtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntHide;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntegersQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LinearMatchQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LinearQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.QuadraticMatchQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.QuadraticQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RationalQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SumQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Unintegrable;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules112 {
  public static IAST RULES = List(
      IIntegrate(2241,
          Integrate(
              Times(
                  Power(F_, Plus(a_DEFAULT, Times(b_DEFAULT, x_),
                      Times(c_DEFAULT, Sqr(x_)))),
                  Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_)),
              x_Symbol),
          Condition(
              Plus(
                  Simp(
                      Times(e, Power(Plus(d, Times(e, x)), Subtract(m, C1)),
                          Power(FSymbol, Plus(a, Times(b, x), Times(c, Sqr(x)))), Power(
                              Times(C2, c, Log(FSymbol)), CN1)),
                      x),
                  Negate(Dist(
                      Times(Subtract(Times(b, e), Times(C2, c, d)), Power(Times(C2, c), CN1)),
                      Integrate(Times(Power(Plus(d, Times(e, x)), Subtract(m, C1)),
                          Power(FSymbol, Plus(a, Times(b, x), Times(c, Sqr(x))))), x),
                      x)),
                  Negate(
                      Dist(Times(Subtract(m, C1), Sqr(e), Power(Times(C2, c, Log(FSymbol)), CN1)),
                          Integrate(Times(Power(Plus(d, Times(e, x)), Subtract(m, C2)),
                              Power(FSymbol, Plus(a, Times(b, x), Times(c, Sqr(x))))), x),
                          x))),
              And(FreeQ(List(FSymbol, a, b, c, d, e), x),
                  NeQ(Subtract(Times(b, e), Times(C2, c, d)), C0), GtQ(m, C1)))),
      IIntegrate(2242,
          Integrate(
              Times(
                  Power(F_, Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_)))),
                  Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_)),
              x_Symbol),
          Condition(
              Plus(
                  Simp(
                      Times(Power(Plus(d, Times(e, x)), Plus(m, C1)),
                          Power(FSymbol, Plus(a, Times(b, x),
                              Times(c, Sqr(x)))),
                          Power(Times(e, Plus(m, C1)), CN1)),
                      x),
                  Negate(Dist(Times(C2, c, Log(FSymbol), Power(Times(Sqr(e), Plus(m, C1)), CN1)),
                      Integrate(Times(Power(Plus(d, Times(e, x)), Plus(m, C2)),
                          Power(FSymbol, Plus(a, Times(b, x), Times(c, Sqr(x))))), x),
                      x)),
                  Negate(Dist(
                      Times(Subtract(Times(b, e), Times(C2, c, d)), Log(FSymbol),
                          Power(Times(Sqr(e), Plus(m, C1)), CN1)),
                      Integrate(Times(Power(Plus(d, Times(e, x)), Plus(m, C1)),
                          Power(FSymbol, Plus(a, Times(b, x), Times(c, Sqr(x))))), x),
                      x))),
              And(FreeQ(List(FSymbol, a, b, c, d, e), x),
                  NeQ(Subtract(Times(b, e), Times(C2, c, d)), C0), LtQ(m, CN1)))),
      IIntegrate(2243,
          Integrate(
              Times(Power(F_, Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_)))),
                  Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_DEFAULT)),
              x_Symbol),
          Condition(
              Unintegrable(
                  Times(Power(FSymbol, Plus(a, Times(b, x), Times(c, Sqr(x)))),
                      Power(Plus(d, Times(e, x)), m)),
                  x),
              FreeQ(List(FSymbol, a, b, c, d, e, m), x))),
      IIntegrate(
          2244, Integrate(Times(Power(F_, v_),
              Power(u_, m_DEFAULT)), x_Symbol),
          Condition(
              Integrate(Times(Power(ExpandToSum(u, x), m),
                  Power(FSymbol, ExpandToSum(v, x))), x),
              And(FreeQ(List(FSymbol,
                  m), x), LinearQ(u, x), QuadraticQ(v,
                      x),
                  Not(And(LinearMatchQ(u, x), QuadraticMatchQ(v, x)))))),
      IIntegrate(2245,
          Integrate(
              Times(Power(F_, Times(e_DEFAULT, Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                  Power(Plus(a_DEFAULT, Times(b_DEFAULT, Power(F_, v_))), p_),
                  Power(x_, m_DEFAULT)),
              x_Symbol),
          Condition(
              With(
                  List(
                      Set(u,
                          IntHide(Times(Power(FSymbol, Times(e, Plus(c, Times(d, x)))),
                              Power(Plus(a, Times(b, Power(FSymbol, v))), p)), x))),
                  Subtract(Dist(Power(x, m), u, x),
                      Dist(m, Integrate(Times(Power(x, Subtract(m, C1)), u), x), x))),
              And(FreeQ(List(FSymbol, a, b, c, d, e), x), EqQ(v, Times(C2, e,
                  Plus(c, Times(d, x)))), GtQ(m,
                      C0),
                  ILtQ(p, C0)))),
      IIntegrate(2246,
          Integrate(
              Times(
                  Power(
                      Power(F_, Times(e_DEFAULT,
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                      n_DEFAULT),
                  Power(Plus(a_,
                      Times(b_DEFAULT,
                          Power(Power(F_, Times(e_DEFAULT, Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                              n_DEFAULT))),
                      p_DEFAULT)),
              x_Symbol),
          Condition(
              Dist(Power(Times(d, e, n, Log(FSymbol)), CN1),
                  Subst(
                      Integrate(Power(Plus(a, Times(b, x)), p), x), x, Power(
                          Power(FSymbol, Times(e, Plus(c, Times(d, x)))), n)),
                  x),
              FreeQ(List(FSymbol, a, b, c, d, e, n, p), x))),
      IIntegrate(2247,
          Integrate(
              Times(
                  Power(
                      Plus(a_,
                          Times(b_DEFAULT,
                              Power(
                                  Power(F_,
                                      Times(e_DEFAULT, Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                                  n_DEFAULT))),
                      p_DEFAULT),
                  Power(
                      Power(G_, Times(h_DEFAULT, Plus(f_DEFAULT,
                          Times(g_DEFAULT, x_)))),
                      m_DEFAULT)),
              x_Symbol),
          Condition(
              Dist(
                  Times(Power(Power(GSymbol, Times(h, Plus(f, Times(g, x)))), m),
                      Power(Power(Power(FSymbol, Times(e, Plus(c, Times(d, x)))), n), CN1)),
                  Integrate(
                      Times(Power(Power(FSymbol, Times(e, Plus(c, Times(d, x)))), n),
                          Power(
                              Plus(a,
                                  Times(b,
                                      Power(Power(FSymbol, Times(e, Plus(c, Times(d, x)))), n))),
                              p)),
                      x),
                  x),
              And(FreeQ(List(FSymbol, GSymbol, a, b, c, d, e, f, g, h, m, n, p), x),
                  EqQ(Times(d, e, n, Log(FSymbol)), Times(g, h, m, Log(GSymbol)))))),
      IIntegrate(2248,
          Integrate(
              Times(
                  Power(Plus(a_,
                      Times(b_DEFAULT, Power(F_, Times(e_DEFAULT, Plus(c_DEFAULT,
                          Times(d_DEFAULT, x_)))))),
                      p_DEFAULT),
                  Power(G_, Times(h_DEFAULT, Plus(f_DEFAULT, Times(g_DEFAULT, x_))))),
              x_Symbol),
          Condition(
              With(
                  List(
                      Set(m,
                          FullSimplify(Times(g, h, Log(GSymbol),
                              Power(Times(d, e, Log(FSymbol)), CN1))))),
                  Condition(
                      Dist(
                          Times(Denominator(m),
                              Power(GSymbol, Subtract(Times(f, h), Times(c, g, h, Power(d, CN1)))),
                              Power(Times(d, e, Log(FSymbol)), CN1)),
                          Subst(
                              Integrate(
                                  Times(
                                      Power(x, Subtract(Numerator(m), C1)),
                                      Power(Plus(a, Times(b, Power(x, Denominator(m)))), p)),
                                  x),
                              x, Power(FSymbol,
                                  Times(e, Plus(c, Times(d, x)), Power(Denominator(m), CN1)))),
                          x),
                      Or(LeQ(m, CN1), GeQ(m, C1)))),
              FreeQ(List(FSymbol, GSymbol, a, b, c, d, e, f, g, h, p), x))),
      IIntegrate(2249,
          Integrate(
              Times(
                  Power(Plus(a_,
                      Times(b_DEFAULT, Power(F_, Times(e_DEFAULT, Plus(c_DEFAULT,
                          Times(d_DEFAULT, x_)))))),
                      p_DEFAULT),
                  Power(G_, Times(h_DEFAULT, Plus(f_DEFAULT, Times(g_DEFAULT, x_))))),
              x_Symbol),
          Condition(
              With(List(Set(m,
                  FullSimplify(Times(d, e, Log(FSymbol), Power(Times(g, h, Log(GSymbol)), CN1))))),
                  Condition(
                      Dist(Times(Denominator(m), Power(Times(g, h, Log(GSymbol)), CN1)),
                          Subst(
                              Integrate(
                                  Times(
                                      Power(x, Subtract(Denominator(m), C1)),
                                      Power(Plus(a, Times(b,
                                          Power(FSymbol,
                                              Subtract(Times(c, e), Times(d, e, f, Power(g, CN1)))),
                                          Power(x, Numerator(m)))), p)),
                                  x),
                              x,
                              Power(
                                  GSymbol, Times(h, Plus(f, Times(g, x)),
                                      Power(Denominator(m), CN1)))),
                          x),
                      Or(LtQ(m, CN1), GtQ(m, C1)))),
              FreeQ(List(FSymbol, GSymbol, a, b, c, d, e, f, g, h, p), x))),
      IIntegrate(2250,
          Integrate(
              Times(
                  Power(
                      Plus(a_,
                          Times(b_DEFAULT, Power(F_, Times(e_DEFAULT, Plus(c_DEFAULT,
                              Times(d_DEFAULT, x_)))))),
                      p_DEFAULT),
                  Power(G_, Times(h_DEFAULT, Plus(f_DEFAULT, Times(g_DEFAULT, x_))))),
              x_Symbol),
          Condition(
              Integrate(
                  Expand(
                      Times(Power(GSymbol, Times(h, Plus(f, Times(g, x)))),
                          Power(Plus(a, Times(b, Power(FSymbol, Times(e, Plus(c, Times(d, x)))))),
                              p)),
                      x),
                  x),
              And(FreeQ(List(FSymbol, GSymbol, a, b, c, d, e, f, g, h), x), IGtQ(p, C0)))),
      IIntegrate(2251,
          Integrate(
              Times(
                  Power(
                      Plus(a_,
                          Times(
                              b_DEFAULT, Power(F_, Times(e_DEFAULT, Plus(c_DEFAULT,
                                  Times(d_DEFAULT, x_)))))),
                      p_),
                  Power(G_, Times(h_DEFAULT, Plus(f_DEFAULT, Times(g_DEFAULT, x_))))),
              x_Symbol),
          Condition(
              Simp(
                  Times(
                      Power(a, p), Power(GSymbol, Times(h,
                          Plus(f, Times(g, x)))),
                      Hypergeometric2F1(Negate(p),
                          Times(g, h, Log(GSymbol), Power(Times(d, e, Log(FSymbol)),
                              CN1)),
                          Plus(Times(g, h, Log(GSymbol),
                              Power(Times(d, e, Log(FSymbol)), CN1)), C1),
                          Simplify(
                              Times(
                                  CN1, b, Power(FSymbol, Times(e,
                                      Plus(c, Times(d, x)))),
                                  Power(a, CN1)))),
                      Power(Times(g, h, Log(GSymbol)), CN1)),
                  x),
              And(FreeQ(List(FSymbol, GSymbol, a, b, c, d, e, f, g, h, p), x),
                  Or(ILtQ(p, C0), GtQ(a, C0))))),
      IIntegrate(2252,
          Integrate(
              Times(
                  Power(
                      Plus(a_,
                          Times(
                              b_DEFAULT, Power(F_, Times(e_DEFAULT, Plus(c_DEFAULT,
                                  Times(d_DEFAULT, x_)))))),
                      p_),
                  Power(G_, Times(h_DEFAULT, Plus(f_DEFAULT, Times(g_DEFAULT, x_))))),
              x_Symbol),
          Condition(
              Dist(
                  Times(
                      Power(Plus(a,
                          Times(b, Power(FSymbol, Times(e, Plus(c, Times(d, x)))))), p),
                      Power(
                          Power(Plus(C1,
                              Times(b, Power(a, CN1),
                                  Power(FSymbol, Times(e, Plus(c, Times(d, x)))))),
                              p),
                          CN1)),
                  Integrate(
                      Times(
                          Power(GSymbol, Times(h,
                              Plus(f, Times(g, x)))),
                          Power(
                              Plus(C1,
                                  Times(
                                      b, Power(FSymbol, Times(e, Plus(c, Times(d, x)))), Power(a,
                                          CN1))),
                              p)),
                      x),
                  x),
              And(FreeQ(List(FSymbol, GSymbol, a, b, c, d, e, f, g, h,
                  p), x), Not(
                      Or(ILtQ(p, C0), GtQ(a, C0)))))),
      IIntegrate(2253,
          Integrate(
              Times(
                  Power(Plus(a_,
                      Times(b_DEFAULT, Power(F_, Times(e_DEFAULT, v_)))), p_),
                  Power(G_, Times(h_DEFAULT, u_))),
              x_Symbol),
          Condition(
              Integrate(
                  Times(Power(GSymbol, Times(h, ExpandToSum(u, x))), Power(
                      Plus(a, Times(b, Power(FSymbol, Times(e, ExpandToSum(v, x))))), p)),
                  x),
              And(FreeQ(List(FSymbol, GSymbol, a, b, e, h,
                  p), x), LinearQ(List(u, v),
                      x),
                  Not(LinearMatchQ(List(u, v), x))))),
      IIntegrate(2254,
          Integrate(
              Times(Power(Plus(a_DEFAULT, Times(b_DEFAULT, Power(F_, u_))), p_DEFAULT),
                  Power(Plus(c_DEFAULT, Times(d_DEFAULT, Power(F_, v_))), q_DEFAULT), Power(
                      Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT)),
              x_Symbol),
          Condition(
              With(
                  List(
                      Set(w,
                          ExpandIntegrand(
                              Power(Plus(e,
                                  Times(f, x)), m),
                              Times(
                                  Power(Plus(a, Times(b,
                                      Power(FSymbol, u))), p),
                                  Power(Plus(c, Times(d, Power(FSymbol, v))), q)),
                              x))),
                  Condition(Integrate(w, x), SumQ(w))),
              And(FreeQ(List(FSymbol, a, b, c, d, e, f, m), x), IntegersQ(p, q), LinearQ(List(u, v),
                  x), RationalQ(Simplify(Times(u, Power(v, CN1))))))),
      IIntegrate(2255,
          Integrate(
              Times(
                  Power(
                      Plus(a_,
                          Times(
                              b_DEFAULT, Power(F_, Times(e_DEFAULT, Plus(c_DEFAULT,
                                  Times(d_DEFAULT, x_)))))),
                      p_DEFAULT),
                  Power(G_, Times(h_DEFAULT, Plus(f_DEFAULT,
                      Times(g_DEFAULT, x_)))),
                  Power($p("H"), Times(t_DEFAULT, Plus(r_DEFAULT, Times(s_DEFAULT, x_))))),
              x_Symbol),
          Condition(
              With(
                  List(
                      Set(m,
                          FullSimplify(
                              Times(Plus(Times(g, h, Log(
                                  GSymbol)), Times(s, t, Log($s("H")))),
                                  Power(Times(d, e, Log(FSymbol)), CN1))))),
                  Condition(
                      Dist(
                          Times(Denominator(m),
                              Power(GSymbol, Subtract(Times(f, h), Times(c, g, h, Power(d, CN1)))),
                              Power($s("H"), Subtract(Times(r, t), Times(c, s, t, Power(d, CN1)))),
                              Power(Times(d, e, Log(FSymbol)), CN1)),
                          Subst(
                              Integrate(Times(Power(x, Subtract(Numerator(m), C1)),
                                  Power(Plus(a, Times(b, Power(x, Denominator(m)))), p)), x),
                              x,
                              Power(
                                  FSymbol, Times(e, Plus(c, Times(d, x)),
                                      Power(Denominator(m), CN1)))),
                          x),
                      RationalQ(m))),
              FreeQ(List(FSymbol, GSymbol, $s("H"), a, b, c, d, e, f, g, h, r, s, t, p), x))),
      IIntegrate(2256,
          Integrate(
              Times(
                  Power(Plus(a_,
                      Times(b_DEFAULT, Power(F_, Times(e_DEFAULT, Plus(c_DEFAULT,
                          Times(d_DEFAULT, x_)))))),
                      p_DEFAULT),
                  Power(G_, Times(h_DEFAULT, Plus(f_DEFAULT,
                      Times(g_DEFAULT, x_)))),
                  Power($p("H"), Times(t_DEFAULT, Plus(r_DEFAULT, Times(s_DEFAULT, x_))))),
              x_Symbol),
          Condition(
              Dist(Power(GSymbol, Times(Subtract(f, Times(c, g, Power(d, CN1))), h)),
                  Integrate(
                      Times(Power($s("H"), Times(t, Plus(r, Times(s, x)))),
                          Power(
                              Plus(b,
                                  Times(a,
                                      Power(Power(FSymbol, Times(e, Plus(c, Times(d, x)))), CN1))),
                              p)),
                      x),
                  x),
              And(FreeQ(List(FSymbol, GSymbol, $s("H"), a, b, c, d, e, f, g, h, r, s, t), x),
                  EqQ(Plus(Times(d, e, p, Log(FSymbol)),
                      Times(g, h, Log(GSymbol))), C0),
                  IntegerQ(p)))),
      IIntegrate(2257,
          Integrate(
              Times(
                  Power(
                      Plus(a_,
                          Times(b_DEFAULT, Power(F_, Times(e_DEFAULT, Plus(c_DEFAULT,
                              Times(d_DEFAULT, x_)))))),
                      p_DEFAULT),
                  Power(G_, Times(h_DEFAULT, Plus(f_DEFAULT,
                      Times(g_DEFAULT, x_)))),
                  Power($p("H"), Times(t_DEFAULT, Plus(r_DEFAULT, Times(s_DEFAULT, x_))))),
              x_Symbol),
          Condition(
              Integrate(
                  Expand(
                      Times(Power(GSymbol, Times(h, Plus(f, Times(g, x)))),
                          Power($s("H"), Times(t,
                              Plus(r, Times(s, x)))),
                          Power(
                              Plus(a, Times(b, Power(FSymbol,
                                  Times(e, Plus(c, Times(d, x)))))),
                              p)),
                      x),
                  x),
              And(FreeQ(List(FSymbol, GSymbol, $s("H"), a, b, c, d, e, f, g, h, r, s, t), x),
                  IGtQ(p, C0)))),
      IIntegrate(2258,
          Integrate(
              Times(
                  Power(
                      Plus(a_,
                          Times(
                              b_DEFAULT, Power(F_, Times(e_DEFAULT,
                                  Plus(c_DEFAULT, Times(d_DEFAULT, x_)))))),
                      p_),
                  Power(G_, Times(h_DEFAULT, Plus(f_DEFAULT,
                      Times(g_DEFAULT, x_)))),
                  Power($p("H"), Times(t_DEFAULT, Plus(r_DEFAULT, Times(s_DEFAULT, x_))))),
              x_Symbol),
          Condition(
              Simp(
                  Times(Power(a, p), Power(GSymbol, Times(h, Plus(f, Times(g, x)))),
                      Power($s("H"), Times(t, Plus(r, Times(s, x)))),
                      Hypergeometric2F1(Negate(p),
                          Times(Plus(Times(g, h, Log(GSymbol)), Times(s, t, Log($s("H")))),
                              Power(Times(d, e, Log(FSymbol)), CN1)),
                          Plus(Times(Plus(Times(g, h, Log(GSymbol)), Times(s, t, Log($s("H")))),
                              Power(Times(d, e, Log(FSymbol)), CN1)), C1),
                          Simplify(
                              Times(CN1, b, Power(FSymbol, Times(e, Plus(c, Times(d, x)))),
                                  Power(a, CN1)))),
                      Power(Plus(Times(g, h, Log(GSymbol)), Times(s, t, Log($s("H")))), CN1)),
                  x),
              And(FreeQ(List(FSymbol, GSymbol, $s("H"), a, b, c, d, e, f, g, h, r, s, t), x),
                  ILtQ(p, C0)))),
      IIntegrate(2259,
          Integrate(
              Times(
                  Power(
                      Plus(a_,
                          Times(b_DEFAULT, Power(F_, Times(e_DEFAULT,
                              Plus(c_DEFAULT, Times(d_DEFAULT, x_)))))),
                      p_),
                  Power(G_, Times(h_DEFAULT, Plus(f_DEFAULT,
                      Times(g_DEFAULT, x_)))),
                  Power($p("H"), Times(t_DEFAULT, Plus(r_DEFAULT, Times(s_DEFAULT, x_))))),
              x_Symbol),
          Condition(
              Simp(
                  Times(
                      Power(GSymbol, Times(h, Plus(f,
                          Times(g, x)))),
                      Power($s("H"), Times(t, Plus(r, Times(s, x)))),
                      Power(Plus(a, Times(b,
                          Power(FSymbol, Times(e, Plus(c, Times(d, x)))))), p),
                      Hypergeometric2F1(Negate(p),
                          Times(
                              Plus(Times(g, h, Log(GSymbol)), Times(s, t,
                                  Log($s("H")))),
                              Power(Times(d, e, Log(FSymbol)), CN1)),
                          Plus(
                              Times(
                                  Plus(Times(g, h, Log(GSymbol)), Times(s, t, Log($s("H")))), Power(
                                      Times(d, e, Log(FSymbol)), CN1)),
                              C1),
                          Simplify(
                              Times(
                                  CN1, b, Power(FSymbol, Times(e,
                                      Plus(c, Times(d, x)))),
                                  Power(a, CN1)))),
                      Power(
                          Times(
                              Plus(Times(g, h, Log(
                                  GSymbol)), Times(s, t,
                                      Log($s("H")))),
                              Power(Times(
                                  Plus(a, Times(b, Power(FSymbol, Times(e, Plus(c, Times(d, x)))))),
                                  Power(a, CN1)), p)),
                          CN1)),
                  x),
              And(FreeQ(List(FSymbol, GSymbol, $s("H"), a, b, c, d, e, f, g, h, r, s, t,
                  p), x), Not(
                      IntegerQ(p))))),
      IIntegrate(2260,
          Integrate(Times(Power(Plus(a_, Times(b_DEFAULT, Power(F_, Times(e_DEFAULT, v_)))), p_),
              Power(G_, Times(h_DEFAULT, u_)), Power($p("H"), Times(t_DEFAULT, w_))), x_Symbol),
          Condition(
              Integrate(Times(Power(GSymbol, Times(h, ExpandToSum(u, x))),
                  Power($s("H"), Times(t, ExpandToSum(w, x))),
                  Power(Plus(a, Times(b, Power(FSymbol, Times(e, ExpandToSum(v, x))))), p)), x),
              And(FreeQ(List(FSymbol, GSymbol, $s("H"), a, b, e, h, t, p), x),
                  LinearQ(List(u, v, w), x), Not(LinearMatchQ(List(u, v, w), x))))));
}
