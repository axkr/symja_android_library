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
