package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.A_DEFAULT;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.B_DEFAULT;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.C_DEFAULT;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.D;
import static org.matheclipse.core.expression.F.ExpIntegralEi;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.MatchQ;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.PolyLog;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Simplify;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.g_;
import static org.matheclipse.core.expression.F.g_DEFAULT;
import static org.matheclipse.core.expression.F.h_DEFAULT;
import static org.matheclipse.core.expression.F.i_DEFAULT;
import static org.matheclipse.core.expression.F.j_DEFAULT;
import static org.matheclipse.core.expression.F.k_DEFAULT;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.q_DEFAULT;
import static org.matheclipse.core.expression.F.r_DEFAULT;
import static org.matheclipse.core.expression.F.s_DEFAULT;
import static org.matheclipse.core.expression.F.t_DEFAULT;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.u_DEFAULT;
import static org.matheclipse.core.expression.F.v_;
import static org.matheclipse.core.expression.F.v_DEFAULT;
import static org.matheclipse.core.expression.F.w_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.ASymbol;
import static org.matheclipse.core.expression.S.BSymbol;
import static org.matheclipse.core.expression.S.CSymbol;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.f;
import static org.matheclipse.core.expression.S.g;
import static org.matheclipse.core.expression.S.h;
import static org.matheclipse.core.expression.S.i;
import static org.matheclipse.core.expression.S.j;
import static org.matheclipse.core.expression.S.k;
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
import static org.matheclipse.core.expression.S.z;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.AlgebraicFunctionQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Coeff;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.DerivativeDivides;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandToSum;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FalseQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntegersQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.InverseFunctionFreeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LinearMatchQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LinearQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RationalFunctionQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SimplifyIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SumQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Unintegrable;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules125 {
  public static IAST RULES = List(
      IIntegrate(2501,
          Integrate(
              Times(
                  Power(
                      Log(Times(e_DEFAULT,
                          Power(Times(f_DEFAULT,
                              Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), p_DEFAULT), Power(Plus(
                                  c_DEFAULT, Times(d_DEFAULT, x_)),
                                  q_DEFAULT)),
                              r_DEFAULT))),
                      u_DEFAULT),
                  Power(
                      Plus(s_DEFAULT,
                          Times(Log(
                              Times(i_DEFAULT, Power(Plus(g_DEFAULT, Times(h_DEFAULT, x_)),
                                  n_DEFAULT))),
                              t_DEFAULT)),
                      m_DEFAULT),
                  Power(Plus(j_DEFAULT, Times(k_DEFAULT, x_)), CN1)),
              x_Symbol),
          Condition(
              Unintegrable(
                  Times(
                      Power(Log(Times(e,
                          Power(Times(f, Power(Plus(a, Times(b, x)), p),
                              Power(Plus(c, Times(d, x)), q)), r))),
                          u),
                      Power(Plus(s, Times(t, Log(Times(i, Power(Plus(g, Times(h, x)), n))))),
                          m),
                      Power(Plus(j, Times(k, x)), CN1)),
                  x),
              And(FreeQ(List(a, b, c, d, e, f, g, h, i, j, k, s, t, m, n, p, q, r,
                  u), x), NeQ(Subtract(Times(b, c), Times(a, d)),
                      C0)))),
      IIntegrate(2502,
          Integrate(
              Times(
                  Log(Times(
                      e_DEFAULT, Power(Plus(a_DEFAULT,
                          Times(b_DEFAULT, x_)), CN1),
                      Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                  u_),
              x_Symbol),
          Condition(
              With(
                  List(Set(g, Coeff(Simplify(Power(Times(u, Plus(a, Times(b, x))), CN1)), x, C0)),
                      Set(h, Coeff(Simplify(Power(Times(u, Plus(a, Times(b, x))), CN1)), x, C1))),
                  Condition(Negate(Dist(
                      Times(Subtract(b, Times(d, e)),
                          Power(Times(h, Subtract(Times(b, c), Times(a, d))), CN1)),
                      Subst(
                          Integrate(Times(Log(Times(e, x)), Power(Subtract(C1, Times(e, x)), CN1)),
                              x),
                          x, Times(Plus(c, Times(d, x)), Power(Plus(a, Times(b, x)), CN1))),
                      x)), EqQ(
                          Subtract(
                              Times(g, Subtract(b, Times(d, e))), Times(h,
                                  Subtract(a, Times(c, e)))),
                          C0))),
              And(FreeQ(List(a, b, c, d, e), x), NeQ(Subtract(Times(b, c),
                  Times(a, d)), C0), LinearQ(Simplify(Power(Times(u, Plus(a, Times(b, x))), CN1)),
                      x)))),
      IIntegrate(2503,
          Integrate(
              Times(
                  Power(Log(Times(e_DEFAULT,
                      Power(
                          Times(f_DEFAULT, Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), p_DEFAULT),
                              Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), q_DEFAULT)),
                          r_DEFAULT))),
                      s_DEFAULT),
                  u_),
              x_Symbol),
          Condition(
              With(
                  List(Set(g, Coeff(Simplify(Power(Times(u, Plus(a, Times(b, x))), CN1)), x, C0)),
                      Set(h, Coeff(Simplify(Power(Times(u, Plus(a, Times(b, x))), CN1)), x, C1))),
                  Condition(
                      Plus(
                          Negate(
                              Simp(
                                  Times(
                                      Power(
                                          Log(Times(e,
                                              Power(Times(f, Power(Plus(a, Times(b, x)), p),
                                                  Power(Plus(c, Times(d, x)), q)), r))),
                                          s),
                                      Log(Times(CN1, Subtract(Times(b, c), Times(a, d)),
                                          Plus(g, Times(h, x)),
                                          Power(Times(Subtract(Times(d, g), Times(c, h)),
                                              Plus(a, Times(b, x))), CN1))),
                                      Power(Subtract(Times(b, g), Times(a, h)), CN1)),
                                  x)),
                          Dist(
                              Times(p, r, s, Subtract(Times(b, c), Times(a, d)), Power(Subtract(
                                  Times(b, g), Times(a, h)),
                                  CN1)),
                              Integrate(Times(
                                  Power(
                                      Log(Times(e,
                                          Power(Times(f, Power(Plus(a, Times(b, x)), p),
                                              Power(Plus(c, Times(d, x)), q)), r))),
                                      Subtract(s, C1)),
                                  Log(Times(CN1, Subtract(Times(b, c), Times(a, d)),
                                      Plus(g, Times(h, x)),
                                      Power(Times(Subtract(Times(d, g), Times(c, h)),
                                          Plus(a, Times(b, x))), CN1))),
                                  Power(Times(Plus(a, Times(b, x)), Plus(c, Times(d, x))), CN1)),
                                  x),
                              x)),
                      And(NeQ(Subtract(Times(b, g),
                          Times(a, h)), C0), NeQ(Subtract(Times(d, g), Times(c, h)),
                              C0)))),
              And(FreeQ(List(a, b, c, d, e, f, p, q, r, s), x),
                  NeQ(Subtract(Times(b, c), Times(a, d)), C0), IGtQ(s, C0), EqQ(Plus(p, q), C0),
                  LinearQ(Simplify(Power(Times(u, Plus(a, Times(b, x))), CN1)), x)))),
      IIntegrate(2504,
          Integrate(
              Times(
                  Power(
                      Log(Times(e_DEFAULT,
                          Power(Times(f_DEFAULT,
                              Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), p_DEFAULT), Power(Plus(
                                  c_DEFAULT, Times(d_DEFAULT, x_)),
                                  q_DEFAULT)),
                              r_DEFAULT))),
                      CN1),
                  u_),
              x_Symbol),
          Condition(
              With(
                  List(Set(h,
                      Simplify(Times(u, Plus(a, Times(b, x)), Plus(c, Times(d, x)))))),
                  Condition(
                      Simp(Times(h,
                          Log(Log(Times(e,
                              Power(Times(f, Power(Plus(a, Times(b, x)), p),
                                  Power(Plus(c, Times(d, x)), q)), r)))),
                          Power(Times(p, r, Subtract(Times(b, c), Times(a, d))), CN1)), x),
                      FreeQ(h, x))),
              And(FreeQ(List(a, b, c, d, e, f, p, q, r), x),
                  NeQ(Subtract(Times(b, c), Times(a, d)), C0), EqQ(Plus(p, q), C0)))),
      IIntegrate(2505,
          Integrate(
              Times(
                  Power(
                      Log(Times(e_DEFAULT,
                          Power(
                              Times(f_DEFAULT,
                                  Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), p_DEFAULT),
                                  Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), q_DEFAULT)),
                              r_DEFAULT))),
                      s_DEFAULT),
                  u_),
              x_Symbol),
          Condition(
              With(
                  List(Set(h, Simplify(Times(u, Plus(a, Times(b,
                      x)), Plus(c,
                          Times(d, x)))))),
                  Condition(
                      Simp(Times(h,
                          Power(Log(Times(e,
                              Power(Times(f, Power(Plus(a, Times(b, x)), p),
                                  Power(Plus(c, Times(d, x)), q)), r))),
                              Plus(s, C1)),
                          Power(Times(p, r, Plus(s, C1), Subtract(Times(b, c), Times(a, d))), CN1)),
                          x),
                      FreeQ(h, x))),
              And(FreeQ(List(a, b, c, d, e, f, p, q, r, s),
                  x), NeQ(Subtract(Times(b, c), Times(a, d)), C0), EqQ(Plus(p, q), C0),
                  NeQ(s, CN1)))),
      IIntegrate(2506,
          Integrate(
              Times(Log(v_),
                  Power(Log(Times(e_DEFAULT,
                      Power(
                          Times(f_DEFAULT, Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), p_DEFAULT),
                              Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), q_DEFAULT)),
                          r_DEFAULT))),
                      s_DEFAULT),
                  u_),
              x_Symbol),
          Condition(
              With(
                  List(
                      Set(g,
                          Simplify(
                              Times(
                                  Subtract(v, C1), Plus(c, Times(d,
                                      x)),
                                  Power(Plus(a, Times(b, x)), CN1)))),
                      Set(h, Simplify(Times(u, Plus(a, Times(b, x)), Plus(c, Times(d, x)))))),
                  Condition(
                      Plus(
                          Negate(
                              Simp(
                                  Times(h, PolyLog(C2, Subtract(C1, v)),
                                      Power(
                                          Log(Times(e,
                                              Power(
                                                  Times(f, Power(Plus(a, Times(b, x)), p),
                                                      Power(Plus(c, Times(d, x)), q)),
                                                  r))),
                                          s),
                                      Power(Subtract(Times(b, c), Times(a, d)), CN1)),
                                  x)),
                          Dist(Times(h, p, r, s), Integrate(
                              Times(PolyLog(C2, Subtract(C1, v)),
                                  Power(
                                      Log(Times(e,
                                          Power(Times(f, Power(Plus(a, Times(b, x)), p),
                                              Power(Plus(c, Times(d, x)), q)), r))),
                                      Subtract(s, C1)),
                                  Power(Times(Plus(a, Times(b, x)), Plus(c, Times(d, x))), CN1)),
                              x), x)),
                      FreeQ(List(g, h), x))),
              And(FreeQ(List(a, b, c, d, e, f, p, q, r, s), x), NeQ(Subtract(Times(b, c),
                  Times(a, d)), C0), IGtQ(s,
                      C0),
                  EqQ(Plus(p, q), C0)))),
      IIntegrate(2507,
          Integrate(Times(Power(Log(
              Times(e_DEFAULT,
                  Power(
                      Times(f_DEFAULT, Power(Plus(a_DEFAULT, Times(
                          b_DEFAULT, x_)), p_DEFAULT), Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)),
                              q_DEFAULT)),
                      r_DEFAULT))),
              s_DEFAULT),
              Log(Times(i_DEFAULT,
                  Power(
                      Times(j_DEFAULT, Power(Plus(g_DEFAULT, Times(h_DEFAULT, x_)),
                          t_DEFAULT)),
                      u_DEFAULT))),
              v_), x_Symbol),
          Condition(With(
              List(Set(k, Simplify(Times(v, Plus(a, Times(b, x)), Plus(c, Times(d, x)))))),
              Condition(
                  Subtract(
                      Simp(
                          Times(k,
                              Log(Times(i, Power(Times(j, Power(Plus(g, Times(h, x)), t)), u))),
                              Power(
                                  Log(Times(e,
                                      Power(Times(f, Power(Plus(a, Times(b, x)), p),
                                          Power(Plus(c, Times(d, x)), q)), r))),
                                  Plus(s, C1)),
                              Power(
                                  Times(p, r, Plus(s, C1),
                                      Subtract(Times(b, c), Times(a, d))),
                                  CN1)),
                          x),
                      Dist(
                          Times(
                              k, h, t, u,
                              Power(
                                  Times(p, r, Plus(s, C1), Subtract(Times(b, c), Times(a, d))),
                                  CN1)),
                          Integrate(
                              Times(Power(
                                  Log(Times(e,
                                      Power(Times(f, Power(Plus(a, Times(b, x)), p),
                                          Power(Plus(c, Times(d, x)), q)), r))),
                                  Plus(s, C1)), Power(Plus(g, Times(h, x)), CN1)),
                              x),
                          x)),
                  FreeQ(k, x))),
              And(FreeQ(List(a, b, c, d, e, f, g, h, i, j, p, q, r, s, t, u),
                  x), NeQ(Subtract(Times(b, c), Times(a, d)), C0), EqQ(Plus(p, q), C0),
                  NeQ(s, CN1)))),
      IIntegrate(2508,
          Integrate(
              Times(
                  Power(
                      Log(Times(e_DEFAULT,
                          Power(
                              Times(f_DEFAULT,
                                  Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), p_DEFAULT),
                                  Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), q_DEFAULT)),
                              r_DEFAULT))),
                      s_DEFAULT),
                  u_, PolyLog(n_, v_)),
              x_Symbol),
          Condition(
              With(
                  List(
                      Set(g,
                          Simplify(Times(v, Plus(c, Times(d,
                              x)), Power(Plus(a, Times(b, x)),
                                  CN1)))),
                      Set(h, Simplify(Times(u, Plus(a, Times(b, x)), Plus(c, Times(d, x)))))),
                  Condition(
                      Subtract(
                          Simp(
                              Times(h, PolyLog(Plus(n, C1), v),
                                  Power(Log(Times(e,
                                      Power(Times(f, Power(Plus(a, Times(b,
                                          x)), p), Power(Plus(c, Times(d, x)),
                                              q)),
                                          r))),
                                      s),
                                  Power(Subtract(Times(b, c), Times(a, d)), CN1)),
                              x),
                          Dist(Times(h, p, r, s),
                              Integrate(
                                  Times(PolyLog(Plus(n, C1), v),
                                      Power(
                                          Log(Times(e,
                                              Power(Times(f, Power(Plus(a, Times(b, x)), p),
                                                  Power(Plus(c, Times(d, x)), q)), r))),
                                          Subtract(s, C1)),
                                      Power(Times(Plus(a, Times(b, x)), Plus(c, Times(d, x))),
                                          CN1)),
                                  x),
                              x)),
                      FreeQ(List(g, h), x))),
              And(FreeQ(List(a, b, c, d, e, f, n, p, q, r, s), x), NeQ(Subtract(Times(b, c),
                  Times(a, d)), C0), IGtQ(s,
                      C0),
                  EqQ(Plus(p, q), C0)))),
      IIntegrate(2509, Integrate(Times(
          Power(
              Log(Times(e_DEFAULT,
                  Power(Times(f_DEFAULT, Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), p_DEFAULT),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), q_DEFAULT)), r_DEFAULT))),
              s_DEFAULT),
          Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), m_DEFAULT),
          Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), n_DEFAULT)), x_Symbol),
          Condition(
              Subtract(
                  Simp(
                      Times(Power(Plus(a, Times(b, x)), Plus(m, C1)),
                          Power(Plus(c, Times(d,
                              x)), Plus(n,
                                  C1)),
                          Power(
                              Log(Times(e,
                                  Power(
                                      Times(
                                          f, Power(Plus(a,
                                              Times(b, x)), p),
                                          Power(Plus(c, Times(d, x)), q)),
                                      r))),
                              s),
                          Power(Times(Plus(m, C1), Subtract(Times(b, c), Times(a, d))), CN1)),
                      x),
                  Dist(
                      Times(p, r, s, Subtract(Times(b, c), Times(a, d)), Power(
                          Times(Plus(m, C1), Subtract(Times(b, c), Times(a, d))), CN1)),
                      Integrate(Times(Power(Plus(a, Times(b, x)), m),
                          Power(Plus(c, Times(d, x)), n),
                          Power(Log(Times(e,
                              Power(Times(f, Power(Plus(a, Times(b, x)), p),
                                  Power(Plus(c, Times(d, x)), q)), r))),
                              Subtract(s, C1))),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, d, e, f, m, n, p, q, r, s), x),
                  NeQ(Subtract(Times(b, c), Times(a, d)), C0), EqQ(Plus(p, q),
                      C0),
                  EqQ(Plus(m, n, C2), C0), NeQ(m, CN1), IGtQ(s, C0)))),
      IIntegrate(2510,
          Integrate(
              Times(
                  Power(
                      Log(Times(e_DEFAULT,
                          Power(
                              Times(f_DEFAULT,
                                  Power(Plus(a_DEFAULT, Times(b_DEFAULT,
                                      x_)), p_DEFAULT),
                                  Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), q_DEFAULT)),
                              r_DEFAULT))),
                      CN1),
                  Power(Plus(a_DEFAULT,
                      Times(b_DEFAULT, x_)), m_DEFAULT),
                  Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), n_DEFAULT)),
              x_Symbol),
          Condition(
              Simp(
                  Times(Power(Plus(a, Times(b, x)), Plus(m, C1)),
                      Power(Plus(c, Times(d,
                          x)), Plus(n,
                              C1)),
                      ExpIntegralEi(
                          Times(
                              Plus(m, C1), Log(
                                  Times(e,
                                      Power(
                                          Times(
                                              f, Power(Plus(a,
                                                  Times(b, x)), p),
                                              Power(Plus(c, Times(d, x)), q)),
                                          r))),
                              Power(Times(p, r), CN1))),
                      Power(
                          Times(p, r, Subtract(Times(b, c), Times(a, d)),
                              Power(
                                  Times(e,
                                      Power(
                                          Times(f, Power(Plus(a, Times(b, x)), p),
                                              Power(Plus(c, Times(d, x)), q)),
                                          r)),
                                  Times(Plus(m, C1), Power(Times(p, r), CN1)))),
                          CN1)),
                  x),
              And(FreeQ(List(a, b, c, d, e, f, m, n, p, q, r), x),
                  NeQ(Subtract(Times(b, c), Times(a, d)), C0), EqQ(Plus(p, q), C0), EqQ(Plus(m, n,
                      C2), C0),
                  NeQ(m, CN1)))),
      IIntegrate(2511,
          Integrate(
              Times(
                  Power(
                      Plus(
                          a_DEFAULT, Times(Log(Times(c_DEFAULT,
                              Sqrt(Plus(d_DEFAULT, Times(e_DEFAULT, x_))), Power(Plus(f_DEFAULT,
                                  Times(g_DEFAULT, x_)), CN1D2))),
                              b_DEFAULT)),
                      n_DEFAULT),
                  Power(Plus(A_DEFAULT, Times(B_DEFAULT, x_), Times(C_DEFAULT, Sqr(x_))), CN1)),
              x_Symbol),
          Condition(
              Dist(
                  Times(C2, e, g, Power(Times(CSymbol, Subtract(Times(e, f), Times(d, g))),
                      CN1)),
                  Subst(
                      Integrate(Times(Power(Plus(a, Times(b, Log(Times(c, x)))), n),
                          Power(x, CN1)), x),
                      x, Times(Sqrt(Plus(d, Times(e, x))), Power(Plus(f, Times(g, x)), CN1D2))),
                  x),
              And(FreeQ(List(a, b, c, d, e, f, g, ASymbol, BSymbol, CSymbol, n), x),
                  EqQ(Subtract(Times(CSymbol, d, f),
                      Times(ASymbol, e, g)), C0),
                  EqQ(Subtract(Times(BSymbol, e, g),
                      Times(CSymbol, Plus(Times(e, f), Times(d, g)))), C0)))),
      IIntegrate(2512,
          Integrate(
              Times(Power(Plus(a_DEFAULT,
                  Times(
                      Log(Times(c_DEFAULT, Sqrt(Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                          Power(Plus(f_DEFAULT, Times(g_DEFAULT, x_)), CN1D2))),
                      b_DEFAULT)),
                  n_DEFAULT),
                  Power(Plus(A_DEFAULT, Times(C_DEFAULT, Sqr(x_))), CN1)),
              x_Symbol),
          Condition(
              Dist(
                  Times(g, Power(Times(CSymbol, f),
                      CN1)),
                  Subst(Integrate(Times(Power(Plus(a, Times(b, Log(Times(c, x)))), n),
                      Power(x, CN1)), x), x, Times(Sqrt(Plus(d, Times(e, x))),
                          Power(Plus(f, Times(g, x)), CN1D2))),
                  x),
              And(FreeQ(List(a, b, c, d, e, f, g, ASymbol, CSymbol, n), x),
                  EqQ(Subtract(Times(CSymbol, d, f),
                      Times(ASymbol, e, g)), C0),
                  EqQ(Plus(Times(e, f), Times(d, g)), C0)))),
      IIntegrate(2513,
          Integrate(
              Times(
                  Log(Times(e_DEFAULT,
                      Power(Times(f_DEFAULT,
                          Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), p_DEFAULT), Power(
                              Plus(c_DEFAULT, Times(d_DEFAULT, x_)), q_DEFAULT)),
                          r_DEFAULT))),
                  $p("§rfx", true)),
              x_Symbol),
          Condition(
              Plus(
                  Dist(Times(p, r), Integrate(Times($s("§rfx"), Log(Plus(a, Times(b, x)))),
                      x), x),
                  Dist(Times(q, r), Integrate(Times($s("§rfx"), Log(Plus(c, Times(d, x)))),
                      x), x),
                  Negate(
                      Dist(Subtract(
                          Plus(Times(p, r, Log(Plus(a, Times(b, x)))),
                              Times(q, r, Log(Plus(c, Times(d, x))))),
                          Log(Times(e,
                              Power(Times(f, Power(Plus(a, Times(b, x)), p),
                                  Power(Plus(c, Times(d, x)), q)), r)))),
                          Integrate($s("§rfx"), x), x))),
              And(FreeQ(List(a, b, c, d, e, f, p, q, r), x), RationalFunctionQ($s("§rfx"), x),
                  NeQ(Subtract(Times(b, c),
                      Times(a, d)), C0),
                  Not(MatchQ($s("§rfx"),
                      Condition(
                          Times(
                              u_DEFAULT, Power(Plus(a,
                                  Times(b, x)), m_DEFAULT),
                              Power(Plus(c, Times(d, x)), n_DEFAULT)),
                          IntegersQ(m, n))))))),
      IIntegrate(2514,
          Integrate(
              Times(
                  Power(
                      Log(Times(e_DEFAULT,
                          Power(
                              Times(f_DEFAULT,
                                  Power(Plus(a_DEFAULT, Times(b_DEFAULT,
                                      x_)), p_DEFAULT),
                                  Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), q_DEFAULT)),
                              r_DEFAULT))),
                      s_DEFAULT),
                  $p("§rfx")),
              x_Symbol),
          Condition(
              With(
                  List(
                      Set(u,
                          ExpandIntegrand(Power(Log(Times(e,
                              Power(Times(f, Power(Plus(a, Times(b, x)), p),
                                  Power(Plus(c, Times(d, x)), q)), r))),
                              s), $s("§rfx"), x))),
                  Condition(Integrate(u, x), SumQ(u))),
              And(FreeQ(List(a, b, c, d, e, f, p, q, r,
                  s), x), RationalFunctionQ($s("§rfx"),
                      x),
                  IGtQ(s, C0)))),
      IIntegrate(2515,
          Integrate(
              Times(
                  Power(
                      Log(Times(e_DEFAULT,
                          Power(
                              Times(f_DEFAULT,
                                  Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), p_DEFAULT),
                                  Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), q_DEFAULT)),
                              r_DEFAULT))),
                      s_DEFAULT),
                  $p("§rfx")),
              x_Symbol),
          Condition(Unintegrable(Times($s("§rfx"),
              Power(Log(Times(e,
                  Power(Times(f, Power(Plus(a, Times(b, x)), p), Power(Plus(c, Times(d, x)), q)),
                      r))),
                  s)),
              x),
              And(FreeQ(List(a, b, c, d, e, f, p, q, r, s), x), RationalFunctionQ($s("§rfx"), x)))),
      IIntegrate(2516,
          Integrate(
              Times(
                  Power(
                      Log(Times(e_DEFAULT,
                          Power(Times(f_DEFAULT, Power(v_, p_DEFAULT), Power(w_, q_DEFAULT)),
                              r_DEFAULT))),
                      s_DEFAULT),
                  u_DEFAULT),
              x_Symbol),
          Condition(
              Integrate(
                  Times(u,
                      Power(
                          Log(Times(e,
                              Power(Times(f, Power(ExpandToSum(v, x), p),
                                  Power(ExpandToSum(w, x), q)),
                                  r))),
                          s)),
                  x),
              And(FreeQ(List(e, f, p, q, r, s), x), LinearQ(List(v, w), x),
                  Not(LinearMatchQ(List(v, w), x)), AlgebraicFunctionQ(u, x)))),
      IIntegrate(2517,
          Integrate(
              Times(
                  Power(Log(Times(e_DEFAULT,
                      Power(Times(f_DEFAULT, Plus(g_, Times(v_DEFAULT, Power(w_, CN1)))),
                          r_DEFAULT))),
                      s_DEFAULT),
                  u_DEFAULT),
              x_Symbol),
          Condition(
              Integrate(
                  Times(u,
                      Power(Log(Times(e,
                          Power(Times(f, ExpandToSum(Plus(v, Times(g, w)), x),
                              Power(ExpandToSum(w, x), CN1)), r))),
                          s)),
                  x),
              And(FreeQ(List(e, f, g, r, s), x), LinearQ(w, x), Or(FreeQ(v, x), LinearQ(v, x)),
                  AlgebraicFunctionQ(u, x)))),
      IIntegrate(
          2518, Integrate(Times(Log(v_),
              u_), x_Symbol),
          With(
              List(Set(w, DerivativeDivides(v, Times(u,
                  Subtract(C1, v)), x))),
              Condition(Simp(Times(w, PolyLog(C2, Subtract(C1, v))), x), Not(FalseQ(w))))),
      IIntegrate(2519,
          Integrate(Times(Log(v_), Plus(a_DEFAULT, Times(Log(u_), b_DEFAULT)),
              w_), x_Symbol),
          Condition(
              With(
                  List(Set(z,
                      DerivativeDivides(v, Times(w, Subtract(C1, v)), x))),
                  Condition(
                      Subtract(
                          Simp(Times(z, Plus(a, Times(b, Log(u))),
                              PolyLog(C2, Subtract(C1, v))), x),
                          Dist(b,
                              Integrate(
                                  SimplifyIntegrand(
                                      Times(z, PolyLog(C2, Subtract(C1, v)), D(u, x),
                                          Power(u, CN1)),
                                      x),
                                  x),
                              x)),
                      Not(FalseQ(z)))),
              And(FreeQ(List(a, b), x), InverseFunctionFreeQ(u, x)))),
      IIntegrate(2520,
          Integrate(
              Log(Times(Power(Log(Times(d_DEFAULT, Power(x_, n_DEFAULT))), p_DEFAULT), c_DEFAULT)),
              x_Symbol),
          Condition(
              Subtract(Simp(Times(x, Log(Times(c, Power(Log(Times(d, Power(x, n))), p)))), x),
                  Dist(Times(n, p), Integrate(Power(Log(Times(d, Power(x, n))), CN1), x), x)),
              FreeQ(List(c, d, n, p), x))));
}
