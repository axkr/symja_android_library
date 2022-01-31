package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.Coefficient;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Erf;
import static org.matheclipse.core.expression.F.Exp;
import static org.matheclipse.core.expression.F.F_;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.If;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.LeafCount;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.PolynomialQ;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Simplify;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.g_DEFAULT;
import static org.matheclipse.core.expression.F.h_;
import static org.matheclipse.core.expression.F.i_DEFAULT;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.u_DEFAULT;
import static org.matheclipse.core.expression.F.v_;
import static org.matheclipse.core.expression.F.v_DEFAULT;
import static org.matheclipse.core.expression.F.w_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.F.z_;
import static org.matheclipse.core.expression.S.FSymbol;
import static org.matheclipse.core.expression.S.Pi;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.f;
import static org.matheclipse.core.expression.S.g;
import static org.matheclipse.core.expression.S.h;
import static org.matheclipse.core.expression.S.i;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.q;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.w;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.expression.S.z;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntHide;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LinearQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RationalQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Rt;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules113 {
  public static IAST RULES =
      List(
          IIntegrate(2261,
              Integrate(
                  Times(
                      Power(F_, Times(e_DEFAULT,
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                      Power(
                          Plus(
                              Times(b_DEFAULT,
                                  Power(F_, Times(e_DEFAULT,
                                      Plus(c_DEFAULT, Times(d_DEFAULT, x_))))),
                              Times(a_DEFAULT, Power(x_, n_DEFAULT))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              Power(
                                  Plus(Times(a, Power(x, n)), Times(b,
                                      Power(FSymbol, Times(e, Plus(c, Times(d, x)))))),
                                  Plus(p, C1)),
                              Power(Times(b, d, e, Plus(p, C1), Log(FSymbol)), CN1)),
                          x),
                      Dist(
                          Times(a, n, Power(Times(b, d, e, Log(FSymbol)),
                              CN1)),
                          Integrate(
                              Times(Power(x, Subtract(n, C1)),
                                  Power(
                                      Plus(Times(a, Power(x, n)),
                                          Times(b, Power(FSymbol, Times(e, Plus(c, Times(d, x)))))),
                                      p)),
                              x),
                          x)),
                  And(FreeQ(List(FSymbol, a, b, c, d, e, n, p), x), NeQ(p, CN1)))),
          IIntegrate(2262,
              Integrate(
                  Times(
                      Power(F_, Times(e_DEFAULT,
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                      Power(x_, m_DEFAULT),
                      Power(
                          Plus(
                              Times(b_DEFAULT,
                                  Power(F_, Times(e_DEFAULT,
                                      Plus(c_DEFAULT, Times(d_DEFAULT, x_))))),
                              Times(a_DEFAULT, Power(x_, n_DEFAULT))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(x, m),
                              Power(Plus(Times(a, Power(x, n)), Times(b,
                                  Power(FSymbol, Times(e, Plus(c, Times(d, x)))))), Plus(p,
                                      C1)),
                              Power(Times(b, d, e, Plus(p, C1), Log(FSymbol)), CN1)),
                          x),
                      Negate(
                          Dist(
                              Times(m, Power(Times(b, d, e, Plus(p, C1), Log(FSymbol)),
                                  CN1)),
                              Integrate(
                                  Times(Power(x, Subtract(m, C1)),
                                      Power(
                                          Plus(Times(a, Power(x, n)),
                                              Times(b,
                                                  Power(FSymbol, Times(e, Plus(c, Times(d, x)))))),
                                          Plus(p, C1))),
                                  x),
                              x)),
                      Negate(
                          Dist(Times(a, n, Power(Times(b, d, e, Log(FSymbol)), CN1)),
                              Integrate(
                                  Times(Power(x, Subtract(Plus(m, n), C1)),
                                      Power(
                                          Plus(Times(a, Power(x, n)),
                                              Times(b,
                                                  Power(FSymbol, Times(e, Plus(c, Times(d, x)))))),
                                          p)),
                                  x),
                              x))),
                  And(FreeQ(List(FSymbol, a, b, c, d, e, m, n, p), x), NeQ(p, CN1)))),
          IIntegrate(2263,
              Integrate(
                  Times(
                      Power(
                          Plus(
                              a_DEFAULT, Times(b_DEFAULT, Power(F_,
                                  u_)),
                              Times(c_DEFAULT, Power(F_, v_))),
                          CN1),
                      Power(Plus(f_DEFAULT, Times(g_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(q,
                          Rt(Subtract(Sqr(b), Times(C4, a, c)), C2))),
                      Subtract(
                          Dist(
                              Times(C2, c, Power(q,
                                  CN1)),
                              Integrate(
                                  Times(Power(Plus(f, Times(g, x)), m),
                                      Power(Plus(b, Negate(q), Times(C2, c, Power(FSymbol, u))),
                                          CN1)),
                                  x),
                              x),
                          Dist(Times(C2, c, Power(q, CN1)),
                              Integrate(Times(Power(Plus(f, Times(g, x)), m),
                                  Power(Plus(b, q, Times(C2, c, Power(FSymbol, u))), CN1)), x),
                              x))),
                  And(FreeQ(List(FSymbol, a, b, c, f, g), x), EqQ(v, Times(C2,
                      u)), LinearQ(u, x), NeQ(Subtract(Sqr(b), Times(C4, a, c)),
                          C0),
                      IGtQ(m, C0)))),
          IIntegrate(2264,
              Integrate(
                  Times(Power(F_, u_),
                      Power(
                          Plus(
                              a_DEFAULT, Times(b_DEFAULT, Power(F_,
                                  u_)),
                              Times(c_DEFAULT, Power(F_, v_))),
                          CN1),
                      Power(Plus(f_DEFAULT, Times(g_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(q,
                          Rt(Subtract(Sqr(b), Times(C4, a, c)), C2))),
                      Subtract(Dist(Times(C2, c, Power(q, CN1)),
                          Integrate(
                              Times(Power(Plus(f, Times(g, x)), m), Power(FSymbol, u),
                                  Power(Plus(b, Negate(q), Times(C2, c, Power(FSymbol, u))), CN1)),
                              x),
                          x),
                          Dist(Times(C2, c, Power(q, CN1)),
                              Integrate(
                                  Times(Power(Plus(f, Times(g, x)), m), Power(FSymbol, u),
                                      Power(Plus(b, q, Times(C2, c, Power(FSymbol, u))), CN1)),
                                  x),
                              x))),
                  And(FreeQ(List(FSymbol, a, b, c, f, g), x), EqQ(v, Times(C2, u)), LinearQ(u, x),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0), IGtQ(m, C0)))),
          IIntegrate(2265,
              Integrate(
                  Times(
                      Power(
                          Plus(
                              a_DEFAULT, Times(b_DEFAULT, Power(F_, u_)),
                              Times(c_DEFAULT, Power(F_, v_))),
                          CN1),
                      Plus(Times(i_DEFAULT,
                          Power(F_, u_)), h_),
                      Power(Plus(f_DEFAULT, Times(g_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(q,
                          Rt(Subtract(Sqr(b), Times(C4, a, c)), C2))),
                      Subtract(
                          Dist(Plus(Simplify(
                              Times(Subtract(Times(C2, c, h), Times(b, i)), Power(q, CN1))), i),
                              Integrate(
                                  Times(Power(Plus(f, Times(g, x)), m),
                                      Power(Plus(b, Negate(q), Times(C2, c, Power(FSymbol, u))),
                                          CN1)),
                                  x),
                              x),
                          Dist(
                              Subtract(
                                  Simplify(Times(Subtract(Times(C2, c, h), Times(b, i)),
                                      Power(q, CN1))),
                                  i),
                              Integrate(Times(Power(Plus(f, Times(g, x)), m),
                                  Power(Plus(b, q, Times(C2, c, Power(FSymbol, u))), CN1)), x),
                              x))),
                  And(FreeQ(List(FSymbol, a, b, c, f, g, h, i), x), EqQ(v, Times(C2, u)), LinearQ(u,
                      x), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0), IGtQ(m, C0)))),
          IIntegrate(2266,
              Integrate(
                  Times(
                      Power(
                          Plus(Times(b_DEFAULT, Power(F_, v_)), Times(a_DEFAULT,
                              Power(F_, Plus(c_DEFAULT, Times(d_DEFAULT, x_))))),
                          CN1),
                      Power(x_, m_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(u,
                              IntHide(Power(Plus(Times(a, Power(FSymbol, Plus(c, Times(d, x)))),
                                  Times(b, Power(FSymbol, v))), CN1), x))),
                      Subtract(Simp(Times(Power(x, m), u), x),
                          Dist(m, Integrate(Times(Power(x, Subtract(m, C1)), u), x), x))),
                  And(FreeQ(List(FSymbol, a, b, c,
                      d), x), EqQ(v,
                          Negate(Plus(c, Times(d, x)))),
                      GtQ(m, C0)))),
          IIntegrate(2267,
              Integrate(
                  Times(
                      Power(
                          Plus(a_, Times(b_DEFAULT, Power(F_, v_)),
                              Times(c_DEFAULT, Power(F_, w_))),
                          CN1),
                      u_),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(u, Power(FSymbol, v),
                          Power(
                              Plus(
                                  c, Times(a, Power(FSymbol, v)), Times(b,
                                      Power(FSymbol, Times(C2, v)))),
                              CN1)),
                      x),
                  And(FreeQ(List(FSymbol, a, b, c), x), EqQ(w, Negate(
                      v)), LinearQ(v, x), If(
                          RationalQ(Coefficient(v, x, C1)), GtQ(Coefficient(v, x,
                              C1), C0),
                          LtQ(LeafCount(v), LeafCount(w)))))),
          IIntegrate(2268,
              Integrate(
                  Times(
                      Power(F_,
                          Times(g_DEFAULT,
                              Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), n_DEFAULT))),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(Integrate(
                  ExpandIntegrand(Power(FSymbol, Times(g, Power(Plus(d, Times(e, x)), n))),
                      Power(Plus(a, Times(b, x), Times(c, Sqr(x))), CN1), x),
                  x), FreeQ(List(FSymbol, a, b, c, d, e, g, n), x))),
          IIntegrate(2269,
              Integrate(
                  Times(
                      Power(F_,
                          Times(g_DEFAULT,
                              Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), n_DEFAULT))),
                      Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Power(FSymbol, Times(g, Power(Plus(d, Times(e, x)), n))), Power(
                              Plus(a, Times(c, Sqr(x))), CN1),
                          x),
                      x),
                  FreeQ(List(FSymbol, a, c, d, e, g, n), x))),
          IIntegrate(2270,
              Integrate(
                  Times(
                      Power(F_,
                          Times(g_DEFAULT,
                              Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), n_DEFAULT))),
                      Power(u_, m_DEFAULT), Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_),
                          Times(c_, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Power(FSymbol, Times(g,
                              Power(Plus(d, Times(e, x)), n))),
                          Times(Power(u, m), Power(Plus(a, Times(b, x), Times(c, Sqr(x))), CN1)),
                          x),
                      x),
                  And(FreeQ(List(FSymbol, a, b, c, d, e, g, n), x), PolynomialQ(u, x),
                      IntegerQ(m)))),
          IIntegrate(2271,
              Integrate(
                  Times(
                      Power(F_,
                          Times(g_DEFAULT,
                              Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), n_DEFAULT))),
                      Power(u_, m_DEFAULT), Power(Plus(a_, Times(c_, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(Power(FSymbol, Times(g, Power(Plus(d, Times(e, x)), n))),
                          Times(Power(u, m), Power(Plus(a, Times(c, Sqr(x))), CN1)), x),
                      x),
                  And(FreeQ(List(FSymbol, a, c, d, e, g, n), x), PolynomialQ(u, x), IntegerQ(m)))),
          IIntegrate(2272, Integrate(
              Power(F_, Times(Power(x_, CN2), Plus(a_DEFAULT, Times(b_DEFAULT, Power(x_, C4))))),
              x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Sqrt(Pi),
                              Exp(Times(C2, Sqrt(Times(CN1, a, Log(FSymbol))),
                                  Sqrt(Times(CN1, b, Log(FSymbol))))),
                              Erf(Times(
                                  Plus(Sqrt(Times(CN1, a, Log(FSymbol))),
                                      Times(Sqrt(Times(CN1, b, Log(FSymbol))), Sqr(x))),
                                  Power(x, CN1))),
                              Power(Times(C4, Sqrt(Times(CN1, b, Log(FSymbol)))), CN1)),
                          x),
                      Simp(
                          Times(
                              Sqrt(Pi), Exp(
                                  Times(CN2, Sqrt(Times(CN1, a, Log(FSymbol))),
                                      Sqrt(Times(CN1, b, Log(FSymbol))))),
                              Erf(Times(Subtract(Sqrt(Times(CN1, a, Log(FSymbol))),
                                  Times(Sqrt(Times(CN1, b, Log(FSymbol))), Sqr(x))),
                                  Power(x, CN1))),
                              Power(Times(C4, Sqrt(Times(CN1, b, Log(FSymbol)))), CN1)),
                          x)),
                  FreeQ(List(FSymbol, a, b), x))),
          IIntegrate(2273,
              Integrate(Times(Power(x_, m_DEFAULT), Power(Plus(Exp(x_), Power(x_, m_DEFAULT)), n_)),
                  x_Symbol),
              Condition(Plus(
                  Negate(Simp(
                      Times(Power(Plus(Exp(x), Power(x, m)), Plus(n, C1)), Power(Plus(n, C1), CN1)),
                      x)),
                  Dist(m,
                      Integrate(
                          Times(Power(x, Subtract(m, C1)), Power(Plus(Exp(x), Power(x, m)), n)), x),
                      x),
                  Integrate(Power(Plus(Exp(x), Power(x, m)), Plus(n, C1)), x)),
                  And(RationalQ(m, n), GtQ(m, C0), LtQ(n, C0), NeQ(n, CN1)))),
          IIntegrate(2274,
              Integrate(
                  Times(
                      u_DEFAULT, Power(F_, Times(a_DEFAULT,
                          Plus(Times(Log(z_), b_DEFAULT), v_DEFAULT)))),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(u, Power(FSymbol, Times(a, v)), Power(z, Times(a, b, Log(FSymbol)))),
                      x),
                  FreeQ(List(FSymbol, a, b), x))),
          IIntegrate(2275,
              Integrate(
                  Power(F_,
                      Times(
                          Plus(a_DEFAULT,
                              Times(Sqr(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT)))), b_DEFAULT)),
                          d_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(x, Power(Times(n, Power(Times(c, Power(x, n)), Power(n, CN1))),
                          CN1)),
                      Subst(
                          Integrate(
                              Exp(Plus(
                                  Times(a, d, Log(FSymbol)), Times(x, Power(n,
                                      CN1)),
                                  Times(b, d, Log(FSymbol), Sqr(x)))),
                              x),
                          x, Log(Times(c, Power(x, n)))),
                      x),
                  FreeQ(List(FSymbol, a, b, c, d, n), x))),
          IIntegrate(2276,
              Integrate(
                  Times(
                      Power(F_,
                          Times(
                              Plus(a_DEFAULT,
                                  Times(Sqr(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT)))),
                                      b_DEFAULT)),
                              d_DEFAULT)),
                      Power(Times(e_DEFAULT, x_), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Times(e, x), Plus(m, C1)),
                          Power(
                              Times(e, n,
                                  Power(Times(c, Power(x, n)), Times(Plus(m, C1), Power(n, CN1)))),
                              CN1)),
                      Subst(
                          Integrate(
                              Exp(Plus(Times(a, d, Log(FSymbol)),
                                  Times(Plus(m, C1), x, Power(n, CN1)), Times(b, d, Log(FSymbol),
                                      Sqr(x)))),
                              x),
                          x, Log(Times(c, Power(x, n)))),
                      x),
                  FreeQ(List(FSymbol, a, b, c, d, e, m, n), x))),
          IIntegrate(2277,
              Integrate(
                  Power(F_,
                      Times(
                          Sqr(Plus(a_DEFAULT,
                              Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))), b_DEFAULT))),
                          d_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Power(
                          FSymbol, Plus(Times(Sqr(a), d),
                              Times(C2, a, b, d, Log(Times(c, Power(x, n)))), Times(Sqr(b), d,
                                  Sqr(Log(Times(c, Power(x, n))))))),
                      x),
                  FreeQ(List(FSymbol, a, b, c, d, n), x))),
          IIntegrate(2278,
              Integrate(
                  Times(
                      Power(F_,
                          Times(
                              Sqr(Plus(a_DEFAULT,
                                  Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))), b_DEFAULT))),
                              d_DEFAULT)),
                      Power(Times(e_DEFAULT, x_), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          Power(Times(e,
                              x), m),
                          Power(FSymbol,
                              Plus(Times(Sqr(a), d), Times(C2, a, b, d, Log(Times(c, Power(x, n)))),
                                  Times(Sqr(b), d, Sqr(Log(Times(c, Power(x, n)))))))),
                      x),
                  FreeQ(List(FSymbol, a, b, c, d, e, m, n), x))),
          IIntegrate(2279,
              Integrate(
                  Log(Plus(a_,
                      Times(b_DEFAULT,
                          Power(Power(F_, Times(e_DEFAULT, Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                              n_DEFAULT)))),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Times(d, e, n,
                          Log(FSymbol)), CN1),
                      Subst(
                          Integrate(Times(Log(Plus(a, Times(b, x))), Power(x, CN1)), x), x, Power(
                              Power(FSymbol, Times(e, Plus(c, Times(d, x)))), n)),
                      x),
                  And(FreeQ(List(FSymbol, a, b, c, d, e, n), x), GtQ(a, C0)))),
          IIntegrate(2280,
              Integrate(
                  Log(Plus(a_,
                      Times(b_DEFAULT,
                          Power(
                              Power(F_, Times(e_DEFAULT, Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                              n_DEFAULT)))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              x, Log(
                                  Plus(a,
                                      Times(b,
                                          Power(Power(FSymbol, Times(e, Plus(c, Times(d, x)))),
                                              n))))),
                          x),
                      Dist(Times(b, d, e, n, Log(FSymbol)), Integrate(Times(x,
                          Power(Power(FSymbol, Times(e, Plus(c, Times(d, x)))), n),
                          Power(
                              Plus(a,
                                  Times(b,
                                      Power(Power(FSymbol, Times(e, Plus(c, Times(d, x)))), n))),
                              CN1)),
                          x), x)),
                  And(FreeQ(List(FSymbol, a, b, c, d, e, n), x), Not(GtQ(a, C0))))));
}
