package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$;
import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.D;
import static org.matheclipse.core.expression.F.Denominator;
import static org.matheclipse.core.expression.F.F_;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.FullSimplify;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.MatchQ;
import static org.matheclipse.core.expression.F.MemberQ;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Part;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.PolyLog;
import static org.matheclipse.core.expression.F.PolynomialQ;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Simplify;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.g_DEFAULT;
import static org.matheclipse.core.expression.F.h_;
import static org.matheclipse.core.expression.F.h_DEFAULT;
import static org.matheclipse.core.expression.F.i_DEFAULT;
import static org.matheclipse.core.expression.F.j_DEFAULT;
import static org.matheclipse.core.expression.F.k_;
import static org.matheclipse.core.expression.F.k_DEFAULT;
import static org.matheclipse.core.expression.F.l_DEFAULT;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.q_;
import static org.matheclipse.core.expression.F.q_DEFAULT;
import static org.matheclipse.core.expression.F.r_DEFAULT;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.u_DEFAULT;
import static org.matheclipse.core.expression.F.v_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.ArcCos;
import static org.matheclipse.core.expression.S.ArcCosh;
import static org.matheclipse.core.expression.S.ArcCot;
import static org.matheclipse.core.expression.S.ArcCoth;
import static org.matheclipse.core.expression.S.ArcSin;
import static org.matheclipse.core.expression.S.ArcSinh;
import static org.matheclipse.core.expression.S.ArcTan;
import static org.matheclipse.core.expression.S.ArcTanh;
import static org.matheclipse.core.expression.S.CSymbol;
import static org.matheclipse.core.expression.S.FSymbol;
import static org.matheclipse.core.expression.S.True;
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
import static org.matheclipse.core.expression.S.l;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.q;
import static org.matheclipse.core.expression.S.r;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.AlgebraicFunctionQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.BinomialMatchQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.BinomialQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandToSum;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Expon;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.F;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FractionQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ILtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntHide;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntegralFreeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LinearMatchQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LinearQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PolyQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RationalFunctionExponents;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RationalFunctionQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SimplifyIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Unintegrable;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules122 {
  public static IAST RULES =
      List(
          IIntegrate(2441,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  Log(Times(c_DEFAULT,
                                      Power(Plus(d_, Times(e_DEFAULT, x_)), n_DEFAULT))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      Power(
                          Plus(f_DEFAULT,
                              Times(
                                  Log(Times(
                                      h_DEFAULT, Power(Plus(i_DEFAULT, Times(j_DEFAULT, x_)),
                                          m_DEFAULT))),
                                  g_DEFAULT)),
                          q_DEFAULT),
                      Power(Plus(k_DEFAULT, Times(l_DEFAULT, x_)), r_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(
                          Power(Plus(k,
                              Times(l, x)), r),
                          Power(Plus(a,
                              Times(b, Log(Times(c, Power(Plus(d, Times(e, x)), n))))), p),
                          Power(Plus(f, Times(g, Log(Times(h, Power(Plus(i, Times(j, x)), m))))),
                              q)),
                      x),
                  FreeQ(List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, p, q, r), x))),
          IIntegrate(2442,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  Log(Times(c_DEFAULT, Power(Plus(d_, Times(e_DEFAULT, x_)),
                                      n_DEFAULT))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(f_,
                          Times(g_DEFAULT, x_)), CN1),
                      PolyLog(k_, Plus(h_, Times(i_DEFAULT, x_)))),
                  x_Symbol),
              Condition(
                  Dist(Power(g, CN1),
                      Subst(
                          Integrate(Times(PolyLog(k, Times(h, x, Power(d, CN1))),
                              Power(Plus(a, Times(b, Log(Times(c, Power(x, n))))), p), Power(x,
                                  CN1)),
                              x),
                          x, Plus(d, Times(e, x))),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, h, i, k, n), x),
                      EqQ(Subtract(Times(e, f), Times(d, g)), C0), EqQ(Subtract(Times(g, h),
                          Times(f, i)), C0),
                      IGtQ(p, C0)))),
          IIntegrate(2443,
              Integrate(
                  Times(
                      Plus(a_DEFAULT,
                          Times(Log(Times(c_DEFAULT, Power(Plus(d_, Times(e_DEFAULT, x_)),
                              n_DEFAULT))), b_DEFAULT)),
                      $p("§px", true), $(F_, Times(f_DEFAULT,
                          Plus(g_DEFAULT, Times(h_DEFAULT, x_))))),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(u, IntHide(Times($s("§px"), F(Times(f, Plus(g, Times(h, x))))), x))),
                      Subtract(
                          Dist(Plus(a,
                              Times(b, Log(Times(c, Power(Plus(d, Times(e, x)), n))))), u, x),
                          Dist(Times(b, e, n),
                              Integrate(
                                  SimplifyIntegrand(Times(u, Power(Plus(d, Times(e, x)), CN1)), x),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, f, g, h, n), x), PolynomialQ($s("§px"), x),
                      MemberQ(
                          List(ArcSin, ArcCos, ArcTan, ArcCot, ArcSinh, ArcCosh, ArcTanh, ArcCoth),
                          FSymbol)))),
          IIntegrate(2444,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(Log(Times(c_DEFAULT, Power(v_, n_DEFAULT))), b_DEFAULT)),
                          p_DEFAULT),
                      u_DEFAULT),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(u, Power(Plus(a, Times(b, Log(Times(c, Power(ExpandToSum(v, x), n))))),
                          p)),
                      x),
                  And(FreeQ(List(a, b, c, n, p), x), LinearQ(v, x), Not(LinearMatchQ(v,
                      x)), Not(
                          And(EqQ(n, C1),
                              MatchQ(Times(c, v),
                                  Condition(Times(e_DEFAULT, Plus(f_, Times(g_DEFAULT, x))),
                                      FreeQ(List(e, f, g), x)))))))),
          IIntegrate(2445,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(
                              Log(Times(c_DEFAULT,
                                  Power(
                                      Times(d_DEFAULT,
                                          Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT)),
                                      n_))),
                              b_DEFAULT)),
                          p_DEFAULT),
                      u_DEFAULT),
                  x_Symbol),
              Condition(
                  Subst(
                      Integrate(
                          Times(u,
                              Power(Plus(a,
                                  Times(b, Log(Times(c, Power(d, n), Power(Plus(e, Times(f, x)),
                                      Times(m, n)))))),
                                  p)),
                          x),
                      Times(c, Power(d, n), Power(Plus(e, Times(f,
                          x)), Times(m,
                              n))),
                      Times(c, Power(Times(d, Power(Plus(e, Times(f, x)), m)), n))),
                  And(FreeQ(List(a, b, c, d, e, f, m, n, p), x), Not(IntegerQ(n)),
                      Not(And(EqQ(d, C1),
                          EqQ(m, C1))),
                      IntegralFreeQ(
                          IntHide(Times(u,
                              Power(
                                  Plus(a,
                                      Times(b,
                                          Log(Times(c, Power(d, n),
                                              Power(Plus(e, Times(f, x)), Times(m, n)))))),
                                  p)),
                              x))))),
          IIntegrate(2446,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  Log(Times(c_DEFAULT,
                                      Power(Times(d_DEFAULT,
                                          Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT)),
                                          n_))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      $p("§afx")),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times($s("§afx"),
                          Power(
                              Plus(a,
                                  Times(b,
                                      Log(Times(c,
                                          Power(Times(d, Power(Plus(e, Times(f, x)), m)), n))))),
                              p)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n,
                      p), x), AlgebraicFunctionQ($s("§afx"), x,
                          True)))),
          IIntegrate(
              2447, Integrate(Times(Log(u_),
                  Power($p("§pq"), m_DEFAULT)), x_Symbol),
              Condition(
                  With(
                      List(
                          Set(CSymbol,
                              FullSimplify(Times(Power($s("§pq"), m), Subtract(C1, u),
                                  Power(D(u, x), CN1))))),
                      Condition(
                          Simp(Times(CSymbol,
                              PolyLog(C2, Subtract(C1, u))), x),
                          FreeQ(CSymbol, x))),
                  And(IntegerQ(m), PolyQ($s("§pq"), x), RationalFunctionQ(u, x), LeQ(Part(
                      RationalFunctionExponents(u, x), C2), Expon($s("§pq"), x))))),
          IIntegrate(2448,
              Integrate(
                  Log(Times(c_DEFAULT, Power(Plus(d_, Times(e_DEFAULT, Power(x_, n_))),
                      p_DEFAULT))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(Times(x,
                          Log(Times(c, Power(Plus(d, Times(e, Power(x, n))), p)))), x),
                      Dist(Times(e, n, p),
                          Integrate(Times(Power(x, n), Power(Plus(d, Times(e, Power(x, n))), CN1)),
                              x),
                          x)),
                  FreeQ(List(c, d, e, n, p), x))),
          IIntegrate(2449,
              Integrate(
                  Power(
                      Plus(a_DEFAULT,
                          Times(
                              Log(Times(
                                  c_DEFAULT, Power(Plus(d_, Times(e_DEFAULT, Power(x_, CN1))),
                                      p_DEFAULT))),
                              b_DEFAULT)),
                      q_),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(
                              Plus(e, Times(d,
                                  x)),
                              Power(
                                  Plus(a,
                                      Times(b,
                                          Log(Times(c, Power(Plus(d, Times(e, Power(x, CN1))),
                                              p))))),
                                  q),
                              Power(d, CN1)),
                          x),
                      Dist(
                          Times(b, e, p, q, Power(d,
                              CN1)),
                          Integrate(
                              Times(
                                  Power(Plus(a,
                                      Times(b,
                                          Log(Times(c, Power(Plus(d, Times(e, Power(x, CN1))),
                                              p))))),
                                      Subtract(q, C1)),
                                  Power(x, CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, p), x), IGtQ(q, C0)))),
          IIntegrate(2450,
              Integrate(
                  Power(
                      Plus(a_DEFAULT,
                          Times(
                              Log(Times(
                                  c_DEFAULT, Power(Plus(d_, Times(e_DEFAULT, Power(x_, n_))),
                                      p_DEFAULT))),
                              b_DEFAULT)),
                      q_),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(x,
                              Power(
                                  Plus(a,
                                      Times(
                                          b, Log(Times(c, Power(Plus(d, Times(e, Power(x, n))),
                                              p))))),
                                  q)),
                          x),
                      Dist(Times(b, e, n, p, q),
                          Integrate(
                              Times(Power(x, n),
                                  Power(
                                      Plus(a,
                                          Times(
                                              b,
                                              Log(Times(c,
                                                  Power(Plus(d, Times(e, Power(x, n))), p))))),
                                      Subtract(q, C1)),
                                  Power(Plus(d, Times(e, Power(x, n))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, n, p), x), IGtQ(q, C0),
                      Or(EqQ(q, C1), IntegerQ(n))))),
          IIntegrate(2451,
              Integrate(
                  Power(
                      Plus(a_DEFAULT,
                          Times(
                              Log(Times(c_DEFAULT,
                                  Power(Plus(d_, Times(e_DEFAULT, Power(x_, n_))), p_DEFAULT))),
                              b_DEFAULT)),
                      q_),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(k,
                          Denominator(n))),
                      Dist(k,
                          Subst(
                              Integrate(
                                  Times(
                                      Power(x, Subtract(k,
                                          C1)),
                                      Power(Plus(a,
                                          Times(b,
                                              Log(Times(c,
                                                  Power(Plus(d, Times(e, Power(x, Times(k, n)))),
                                                      p))))),
                                          q)),
                                  x),
                              x, Power(x, Power(k, CN1))),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, p, q), x), FractionQ(n)))),
          IIntegrate(2452,
              Integrate(
                  Power(
                      Plus(a_DEFAULT,
                          Times(
                              Log(Times(
                                  c_DEFAULT, Power(Plus(d_, Times(e_DEFAULT, Power(x_, n_))),
                                      p_DEFAULT))),
                              b_DEFAULT)),
                      q_),
                  x_Symbol),
              Condition(Unintegrable(
                  Power(Plus(a, Times(b, Log(Times(c, Power(Plus(d, Times(e, Power(x, n))), p))))),
                      q),
                  x), FreeQ(List(a, b, c, d, e, n, p, q), x))),
          IIntegrate(2453,
              Integrate(
                  Power(
                      Plus(a_DEFAULT, Times(Log(Times(c_DEFAULT, Power(v_, p_DEFAULT))),
                          b_DEFAULT)),
                      q_DEFAULT),
                  x_Symbol),
              Condition(
                  Integrate(Power(Plus(a, Times(b, Log(Times(c, Power(ExpandToSum(v, x), p))))),
                      q), x),
                  And(FreeQ(List(a, b, c, p, q), x), BinomialQ(v, x), Not(BinomialMatchQ(v, x))))),
          IIntegrate(2454,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  Log(Times(
                                      c_DEFAULT, Power(Plus(d_, Times(e_DEFAULT, Power(x_, n_))),
                                          p_DEFAULT))),
                                  b_DEFAULT)),
                          q_DEFAULT),
                      Power(x_, m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(n, CN1),
                      Subst(Integrate(
                          Times(Power(x, Subtract(Simplify(Times(Plus(m, C1), Power(n, CN1))), C1)),
                              Power(
                                  Plus(a, Times(b, Log(Times(c, Power(Plus(d, Times(e, x)), p))))),
                                  q)),
                          x), x, Power(x, n)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, m, n, p, q), x),
                      IntegerQ(Simplify(Times(Plus(m, C1), Power(n,
                          CN1)))),
                      Or(GtQ(Times(Plus(m, C1), Power(n, CN1)), C0), IGtQ(q, C0)), Not(
                          And(EqQ(q, C1), ILtQ(n, C0), IGtQ(m, C0)))))),
          IIntegrate(2455,
              Integrate(
                  Times(
                      Plus(a_DEFAULT,
                          Times(
                              Log(Times(
                                  c_DEFAULT, Power(Plus(d_, Times(e_DEFAULT, Power(x_, n_))),
                                      p_DEFAULT))),
                              b_DEFAULT)),
                      Power(Times(f_DEFAULT, x_), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Times(f, x), Plus(m, C1)),
                              Plus(
                                  a,
                                  Times(b,
                                      Log(Times(c, Power(Plus(d, Times(e, Power(x, n))), p))))),
                              Power(Times(f, Plus(m, C1)), CN1)),
                          x),
                      Dist(Times(b, e, n, p, Power(Times(f, Plus(m, C1)), CN1)),
                          Integrate(
                              Times(
                                  Power(x, Subtract(n, C1)), Power(Times(f, x), Plus(m,
                                      C1)),
                                  Power(Plus(d, Times(e, Power(x, n))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, m, n, p), x), NeQ(m, CN1)))),
          IIntegrate(2456,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(
                              Log(Times(c_DEFAULT,
                                  Power(Plus(d_, Times(e_DEFAULT, Power(x_, n_))), p_DEFAULT))),
                              b_DEFAULT)),
                          q_DEFAULT),
                      Power(Times(f_, x_), m_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Times(f,
                          x), m), Power(Power(x, m),
                              CN1)),
                      Integrate(
                          Times(Power(x, m),
                              Power(
                                  Plus(a,
                                      Times(b,
                                          Log(Times(c, Power(Plus(d, Times(e, Power(x, n))), p))))),
                                  q)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n, p, q), x),
                      IntegerQ(Simplify(Times(Plus(m, C1), Power(n,
                          CN1)))),
                      Or(GtQ(Times(Plus(m, C1), Power(n, CN1)), C0), IGtQ(q, C0))))),
          IIntegrate(2457,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  Log(Times(
                                      c_DEFAULT,
                                      Power(Plus(d_, Times(e_DEFAULT, Power(x_, n_))), p_DEFAULT))),
                                  b_DEFAULT)),
                          q_),
                      Power(Times(f_DEFAULT, x_), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              Power(Times(f, x), Plus(m,
                                  C1)),
                              Power(
                                  Plus(a,
                                      Times(
                                          b, Log(Times(c, Power(Plus(d, Times(e, Power(x, n))),
                                              p))))),
                                  q),
                              Power(Times(f, Plus(m, C1)), CN1)),
                          x),
                      Dist(
                          Times(b, e, n, p, q, Power(Times(Power(f, n), Plus(m, C1)),
                              CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Plus(m, n)),
                                  Power(
                                      Plus(a,
                                          Times(b,
                                              Log(Times(c, Power(Plus(d, Times(e, Power(x, n))),
                                                  p))))),
                                      Subtract(q, C1)),
                                  Power(Plus(d, Times(e, Power(x, n))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, m, p), x), IGtQ(q, C1), IntegerQ(n),
                      NeQ(m, CN1)))),
          IIntegrate(2458,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  Log(Times(c_DEFAULT,
                                      Power(Plus(d_, Times(e_DEFAULT, Power(x_, n_))), p_DEFAULT))),
                                  b_DEFAULT)),
                          q_),
                      Power(x_, m_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(k,
                          Denominator(n))),
                      Dist(k,
                          Subst(
                              Integrate(
                                  Times(
                                      Power(x, Subtract(Times(k, Plus(m, C1)),
                                          C1)),
                                      Power(Plus(a,
                                          Times(b, Log(Times(c,
                                              Power(Plus(d, Times(e, Power(x, Times(k, n)))),
                                                  p))))),
                                          q)),
                                  x),
                              x, Power(x, Power(k, CN1))),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, m, p, q), x), FractionQ(n)))),
          IIntegrate(2459,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  Log(Times(
                                      c_DEFAULT,
                                      Power(Plus(d_, Times(e_DEFAULT, Power(x_, n_))), p_DEFAULT))),
                                  b_DEFAULT)),
                          q_DEFAULT),
                      Power(Times(f_, x_), m_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Times(f,
                          x), m), Power(Power(x, m),
                              CN1)),
                      Integrate(
                          Times(Power(x, m),
                              Power(Plus(a,
                                  Times(b, Log(Times(c, Power(Plus(d, Times(e, Power(x, n))),
                                      p))))),
                                  q)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, p, q), x), FractionQ(n)))),
          IIntegrate(2460,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  Log(Times(
                                      c_DEFAULT, Power(Plus(d_, Times(e_DEFAULT, Power(x_, n_))),
                                          p_DEFAULT))),
                                  b_DEFAULT)),
                          q_DEFAULT),
                      Power(Times(f_DEFAULT, x_), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(Power(Times(f, x), m),
                          Power(
                              Plus(a,
                                  Times(b,
                                      Log(Times(c, Power(Plus(d, Times(e, Power(x, n))), p))))),
                              q)),
                      x),
                  FreeQ(List(a, b, c, d, e, f, m, n, p, q), x))));
}
