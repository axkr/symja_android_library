package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.ExpIntegralEi;
import static org.matheclipse.core.expression.F.F_;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.PolynomialQ;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.g_DEFAULT;
import static org.matheclipse.core.expression.F.h_DEFAULT;
import static org.matheclipse.core.expression.F.i_DEFAULT;
import static org.matheclipse.core.expression.F.j_DEFAULT;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.u_DEFAULT;
import static org.matheclipse.core.expression.F.v_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.FSymbol;
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
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.BinomialMatchQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.BinomialQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandLinearProduct;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandToSum;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ILtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LinearMatchQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LinearQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NormalizePowerOfLinear;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PowerOfLinearMatchQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PowerOfLinearQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.QuadraticMatchQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.QuadraticQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Unintegrable;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules111 {
  public static IAST RULES =
      List(
          IIntegrate(2221,
              Integrate(
                  Times(
                      Power(F_,
                          Plus(
                              a_DEFAULT, Times(b_DEFAULT,
                                  Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), n_)))),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Plus(e, Times(f, x)), Plus(m, C1)),
                              Power(FSymbol, Plus(a,
                                  Times(b, Power(Plus(c, Times(d, x)), n)))),
                              Power(Times(f, Plus(m, C1)), CN1)),
                          x),
                      Dist(
                          Times(b, d, n, Log(FSymbol), Power(Times(f, Plus(m, C1)),
                              CN1)),
                          Integrate(Times(Power(Plus(e, Times(f, x)), Plus(m, C1)),
                              Power(Plus(c, Times(d, x)), Subtract(n, C1)), Power(FSymbol,
                                  Plus(a, Times(b, Power(Plus(c, Times(d, x)), n))))),
                              x),
                          x)),
                  And(FreeQ(List(FSymbol, a, b, c, d, e, f),
                      x), NeQ(Subtract(Times(d, e), Times(c, f)), C0), IGtQ(n, C2),
                      LtQ(m, CN1)))),
          IIntegrate(2222,
              Integrate(
                  Times(
                      Power(F_,
                          Plus(
                              a_DEFAULT, Times(b_DEFAULT,
                                  Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), CN1)))),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(
                          Times(d, Power(f,
                              CN1)),
                          Integrate(
                              Times(
                                  Power(FSymbol, Plus(a,
                                      Times(b, Power(Plus(c, Times(d, x)), CN1)))),
                                  Power(Plus(c, Times(d, x)), CN1)),
                              x),
                          x),
                      Dist(
                          Times(Subtract(Times(d, e), Times(c,
                              f)), Power(f,
                                  CN1)),
                          Integrate(
                              Times(
                                  Power(FSymbol, Plus(a, Times(b,
                                      Power(Plus(c, Times(d, x)), CN1)))),
                                  Power(Times(Plus(c, Times(d, x)), Plus(e, Times(f, x))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(FSymbol, a, b, c, d, e,
                      f), x), NeQ(Subtract(Times(d, e), Times(c, f)),
                          C0)))),
          IIntegrate(2223,
              Integrate(
                  Times(
                      Power(F_,
                          Plus(
                              a_DEFAULT, Times(b_DEFAULT,
                                  Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), CN1)))),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(
                              Power(Plus(e, Times(f,
                                  x)), Plus(m,
                                      C1)),
                              Power(FSymbol, Plus(a, Times(b, Power(Plus(c, Times(d, x)), CN1)))),
                              Power(Times(f, Plus(m, C1)), CN1)),
                          x),
                      Dist(Times(b, d, Log(FSymbol), Power(Times(f, Plus(m, C1)), CN1)),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), Plus(m, C1)),
                                  Power(FSymbol, Plus(a,
                                      Times(b, Power(Plus(c, Times(d, x)), CN1)))),
                                  Power(Plus(c, Times(d, x)), CN2)),
                              x),
                          x)),
                  And(FreeQ(List(FSymbol, a, b, c, d, e, f), x),
                      NeQ(Subtract(Times(d, e), Times(c, f)), C0), ILtQ(m, CN1)))),
          IIntegrate(2224,
              Integrate(Times(
                  Power(F_,
                      Plus(a_DEFAULT,
                          Times(b_DEFAULT, Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), n_)))),
                  Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), CN1)), x_Symbol),
              Condition(
                  Unintegrable(
                      Times(Power(FSymbol, Plus(a, Times(b, Power(Plus(c, Times(d, x)), n)))),
                          Power(Plus(e, Times(f, x)), CN1)),
                      x),
                  And(FreeQ(List(FSymbol, a, b, c, d, e, f, n), x),
                      NeQ(Subtract(Times(d, e), Times(c, f)), C0)))),
          IIntegrate(
              2225, Integrate(Times(Power(F_, v_),
                  Power(u_, m_DEFAULT)), x_Symbol),
              Condition(
                  Integrate(Times(Power(ExpandToSum(u, x), m),
                      Power(FSymbol, ExpandToSum(v, x))), x),
                  And(FreeQ(List(FSymbol,
                      m), x), LinearQ(u, x), BinomialQ(v,
                          x),
                      Not(And(LinearMatchQ(u, x), BinomialMatchQ(v, x)))))),
          IIntegrate(2226,
              Integrate(
                  Times(Power(F_,
                      Plus(a_DEFAULT, Times(b_DEFAULT, Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)),
                          n_)))),
                      u_),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandLinearProduct(
                          Power(FSymbol, Plus(a, Times(b, Power(Plus(c, Times(d, x)), n)))), u, c,
                          d, x),
                      x),
                  And(FreeQ(List(FSymbol, a, b, c, d, n), x), PolynomialQ(u, x)))),
          IIntegrate(2227,
              Integrate(Times(u_DEFAULT,
                  Power(F_, Plus(a_DEFAULT, Times(b_DEFAULT, v_)))), x_Symbol),
              Condition(
                  Integrate(Times(u,
                      Power(FSymbol, Plus(a, Times(b, NormalizePowerOfLinear(v, x))))), x),
                  And(FreeQ(List(FSymbol, a,
                      b), x), PolynomialQ(u,
                          x),
                      PowerOfLinearQ(v, x), Not(PowerOfLinearMatchQ(v, x))))),
          IIntegrate(2228,
              Integrate(
                  Times(
                      Power(F_,
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT, Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), CN1)))),
                      Power(Plus(e_DEFAULT,
                          Times(f_DEFAULT, x_)), CN1),
                      Power(Plus(g_DEFAULT, Times(h_DEFAULT, x_)), CN1)),
                  x_Symbol),
              Condition(Negate(Dist(
                  Times(d, Power(Times(f, Subtract(Times(d, g), Times(c, h))), CN1)),
                  Subst(
                      Integrate(Times(Power(FSymbol,
                          Plus(a, Times(CN1, b, h, Power(Subtract(Times(d, g), Times(c, h)), CN1)),
                              Times(d, b, x, Power(Subtract(Times(d, g), Times(c, h)), CN1)))),
                          Power(x, CN1)), x),
                      x, Times(Plus(g, Times(h, x)), Power(Plus(c, Times(d, x)), CN1))),
                  x)), And(
                      FreeQ(List(FSymbol, a, b, c, d, e,
                          f), x),
                      EqQ(Subtract(Times(d, e), Times(c, f)), C0)))),
          IIntegrate(2229,
              Integrate(
                  Times(
                      Power(F_,
                          Plus(e_DEFAULT,
                              Times(f_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_)),
                                  Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), CN1)))),
                      Power(Plus(g_DEFAULT, Times(h_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(FSymbol, Plus(e, Times(f, b, Power(d, CN1)))), Integrate(
                          Power(Plus(g, Times(h, x)), m), x),
                      x),
                  And(FreeQ(List(FSymbol, a, b, c, d, e, f, g, h,
                      m), x), EqQ(Subtract(Times(b, c), Times(a, d)),
                          C0)))),
          IIntegrate(2230,
              Integrate(
                  Times(
                      Power(F_,
                          Plus(e_DEFAULT,
                              Times(
                                  f_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT,
                                      x_)),
                                  Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), CN1)))),
                      Power(Plus(g_DEFAULT, Times(h_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(Power(Plus(g, Times(h, x)), m),
                          Power(FSymbol,
                              Subtract(Times(Plus(Times(d, e), Times(b, f)), Power(d, CN1)),
                                  Times(f, Subtract(Times(b, c), Times(a, d)),
                                      Power(Times(d, Plus(c, Times(d, x))), CN1))))),
                      x),
                  And(FreeQ(List(FSymbol, a, b, c, d, e, f, g, h, m), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)),
                          C0),
                      EqQ(Subtract(Times(d, g), Times(c, h)), C0)))),
          IIntegrate(2231,
              Integrate(
                  Times(
                      Power(F_,
                          Plus(e_DEFAULT,
                              Times(
                                  f_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT,
                                      x_)),
                                  Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), CN1)))),
                      Power(Plus(g_DEFAULT, Times(h_DEFAULT, x_)), CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(
                          Times(d, Power(h,
                              CN1)),
                          Integrate(
                              Times(
                                  Power(FSymbol,
                                      Plus(e,
                                          Times(f, Plus(a, Times(b, x)),
                                              Power(Plus(c, Times(d, x)), CN1)))),
                                  Power(Plus(c, Times(d, x)), CN1)),
                              x),
                          x),
                      Dist(
                          Times(Subtract(Times(d, g), Times(c,
                              h)), Power(h,
                                  CN1)),
                          Integrate(
                              Times(Power(FSymbol,
                                  Plus(e,
                                      Times(f, Plus(a, Times(b, x)),
                                          Power(Plus(c, Times(d, x)), CN1)))),
                                  Power(Times(Plus(c, Times(d, x)), Plus(g, Times(h, x))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(FSymbol, a, b, c, d, e, f, g, h), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)),
                          C0),
                      NeQ(Subtract(Times(d, g), Times(c, h)), C0)))),
          IIntegrate(2232,
              Integrate(
                  Times(Power(F_,
                      Plus(e_DEFAULT,
                          Times(f_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_)),
                              Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), CN1)))),
                      Power(Plus(g_DEFAULT, Times(h_DEFAULT, x_)), m_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Plus(g, Times(h, x)), Plus(m, C1)),
                              Power(FSymbol,
                                  Plus(e,
                                      Times(f, Plus(a, Times(b, x)),
                                          Power(Plus(c, Times(d, x)), CN1)))),
                              Power(Times(h, Plus(m, C1)), CN1)),
                          x),
                      Dist(
                          Times(
                              f, Subtract(Times(b, c), Times(a, d)), Log(FSymbol),
                              Power(Times(h, Plus(m, C1)), CN1)),
                          Integrate(
                              Times(Power(Plus(g, Times(h, x)), Plus(m, C1)),
                                  Power(FSymbol,
                                      Plus(e,
                                          Times(f, Plus(a, Times(b, x)),
                                              Power(Plus(c, Times(d, x)), CN1)))),
                                  Power(Plus(c, Times(d, x)), CN2)),
                              x),
                          x)),
                  And(FreeQ(List(FSymbol, a, b, c, d, e, f, g, h), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      NeQ(Subtract(Times(d, g), Times(c, h)), C0), ILtQ(m, CN1)))),
          IIntegrate(2233,
              Integrate(
                  Times(
                      Power(F_,
                          Plus(e_DEFAULT,
                              Times(f_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_)),
                                  Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), CN1)))),
                      Power(Plus(g_DEFAULT, Times(h_DEFAULT, x_)), CN1), Power(
                          Plus(i_DEFAULT, Times(j_DEFAULT, x_)), CN1)),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(Times(d, Power(Times(h, Subtract(Times(d, i), Times(c, j))), CN1)),
                          Subst(
                              Integrate(
                                  Times(
                                      Power(FSymbol,
                                          Subtract(
                                              Plus(e,
                                                  Times(f, Subtract(Times(b, i), Times(a, j)),
                                                      Power(Subtract(Times(d, i), Times(c, j)),
                                                          CN1))),
                                              Times(Subtract(Times(b, c), Times(a, d)), f, x,
                                                  Power(Subtract(Times(d, i), Times(c, j)), CN1)))),
                                      Power(x, CN1)),
                                  x),
                              x, Times(Plus(i, Times(j, x)), Power(Plus(c, Times(d, x)), CN1))),
                          x)),
                  And(FreeQ(List(FSymbol, a, b, c, d, e, f, g, h), x),
                      EqQ(Subtract(Times(d, g), Times(c, h)), C0)))),
          IIntegrate(2234,
              Integrate(Power(F_, Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_)))),
                  x_Symbol),
              Condition(
                  Dist(Power(FSymbol, Subtract(a, Times(Sqr(b), Power(Times(C4, c), CN1)))),
                      Integrate(
                          Power(FSymbol,
                              Times(Sqr(Plus(b, Times(C2, c, x))), Power(Times(C4, c), CN1))),
                          x),
                      x),
                  FreeQ(List(FSymbol, a, b, c), x))),
          IIntegrate(2235, Integrate(Power(F_, v_), x_Symbol),
              Condition(
                  Integrate(Power(FSymbol, ExpandToSum(v, x)), x), And(FreeQ(FSymbol, x),
                      QuadraticQ(v, x), Not(QuadraticMatchQ(v, x))))),
          IIntegrate(2236,
              Integrate(
                  Times(
                      Power(F_, Plus(a_DEFAULT, Times(b_DEFAULT, x_),
                          Times(c_DEFAULT, Sqr(x_)))),
                      Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(e, Power(FSymbol, Plus(a, Times(b, x), Times(c, Sqr(x)))),
                          Power(Times(C2, c, Log(FSymbol)), CN1)),
                      x),
                  And(FreeQ(List(FSymbol, a, b, c, d, e), x),
                      EqQ(Subtract(Times(b, e), Times(C2, c, d)), C0)))),
          IIntegrate(2237,
              Integrate(
                  Times(Power(F_, Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_)))),
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(e, Power(Plus(d, Times(e, x)), Subtract(m, C1)),
                              Power(FSymbol, Plus(a, Times(b, x), Times(c, Sqr(x)))), Power(
                                  Times(C2, c, Log(FSymbol)), CN1)),
                          x),
                      Dist(Times(Subtract(m, C1), Sqr(e), Power(Times(C2, c, Log(FSymbol)), CN1)),
                          Integrate(
                              Times(Power(Plus(d, Times(e, x)), Subtract(m, C2)), Power(FSymbol,
                                  Plus(a, Times(b, x), Times(c, Sqr(x))))),
                              x),
                          x)),
                  And(FreeQ(List(FSymbol, a, b, c, d, e), x),
                      EqQ(Subtract(Times(b, e), Times(C2, c, d)), C0), GtQ(m, C1)))),
          IIntegrate(2238,
              Integrate(
                  Times(
                      Power(F_, Plus(a_DEFAULT, Times(b_DEFAULT, x_),
                          Times(c_DEFAULT, Sqr(x_)))),
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), CN1)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(C1,
                          Power(FSymbol, Subtract(a,
                              Times(Sqr(b), Power(Times(C4, c), CN1)))),
                          ExpIntegralEi(Times(Sqr(Plus(b, Times(C2, c, x))), Log(FSymbol),
                              Power(Times(C4, c), CN1))),
                          Power(Times(C2, e), CN1)),
                      x),
                  And(FreeQ(List(FSymbol, a, b, c, d,
                      e), x), EqQ(Subtract(Times(b, e), Times(C2, c, d)),
                          C0)))),
          IIntegrate(2239,
              Integrate(
                  Times(
                      Power(F_, Plus(a_DEFAULT, Times(b_DEFAULT, x_),
                          Times(c_DEFAULT, Sqr(x_)))),
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Plus(d, Times(e, x)), Plus(m, C1)),
                              Power(FSymbol, Plus(a, Times(b, x), Times(c, Sqr(x)))), Power(
                                  Times(e, Plus(m, C1)), CN1)),
                          x),
                      Dist(Times(C2, c, Log(FSymbol), Power(Times(Sqr(e), Plus(m, C1)), CN1)),
                          Integrate(
                              Times(Power(Plus(d, Times(e, x)), Plus(m, C2)),
                                  Power(FSymbol, Plus(a, Times(b, x), Times(c, Sqr(x))))),
                              x),
                          x)),
                  And(FreeQ(List(FSymbol, a, b, c, d, e), x),
                      EqQ(Subtract(Times(b, e), Times(C2, c, d)), C0), LtQ(m, CN1)))),
          IIntegrate(2240,
              Integrate(
                  Times(Power(F_, Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_)))),
                      Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(Times(e, Power(FSymbol, Plus(a, Times(b, x), Times(c, Sqr(x)))),
                          Power(Times(C2, c, Log(FSymbol)), CN1)), x),
                      Dist(Times(Subtract(Times(b, e), Times(C2, c, d)), Power(Times(C2, c), CN1)),
                          Integrate(Power(FSymbol, Plus(a, Times(b, x), Times(c, Sqr(x)))), x), x)),
                  And(FreeQ(List(FSymbol, a, b, c, d, e), x),
                      NeQ(Subtract(Times(b, e), Times(C2, c, d)), C0)))));
}
