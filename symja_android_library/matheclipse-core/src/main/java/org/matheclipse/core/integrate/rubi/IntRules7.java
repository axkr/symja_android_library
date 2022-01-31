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
class IntRules7 {
  public static IAST RULES =
      List(
          IIntegrate(141,
              Integrate(
                  Times(Power(Plus(a_DEFAULT, Times(b_DEFAULT, u_)), m_DEFAULT),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, u_)), n_DEFAULT), Power(
                          Plus(e_, Times(f_DEFAULT, u_)), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Coefficient(u, x,
                          C1), CN1),
                      Subst(
                          Integrate(
                              Times(
                                  Power(Plus(a, Times(b, x)), m), Power(Plus(c, Times(d, x)),
                                      n),
                                  Power(Plus(e, Times(f, x)), p)),
                              x),
                          x, u),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n, p), x), LinearQ(u, x), NeQ(u, x)))),
          IIntegrate(142,
              Integrate(Times(Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), m_DEFAULT),
                  Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), n_DEFAULT), Plus(e_, Times(f_DEFAULT,
                      x_)),
                  Plus(g_DEFAULT, Times(h_DEFAULT, x_))), x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Times(
                              Power(Plus(a, Times(b,
                                  x)), m),
                              Power(Plus(c, Times(d,
                                  x)), n),
                              Plus(e, Times(f, x)), Plus(g, Times(h, x))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, h), x), Or(IGtQ(m, C0), IntegersQ(m, n))))),
          IIntegrate(143,
              Integrate(
                  Times(Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), m_),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), n_DEFAULT),
                      Plus(e_, Times(f_DEFAULT, x_)), Plus(g_DEFAULT, Times(h_DEFAULT, x_))),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(
                              Plus(Times(Sqr(b), d, e, g), Times(CN1, Sqr(a), d, f, h, m),
                                  Times(CN1, a, b,
                                      Subtract(Times(d, Plus(Times(f, g), Times(e, h))), Times(c, f,
                                          h, Plus(m, C1)))),
                                  Times(
                                      b, f, h, Subtract(Times(b, c), Times(a, d)), Plus(m, C1), x)),
                              Power(Plus(a, Times(b, x)), Plus(m, C1)),
                              Power(Plus(c, Times(d, x)), Plus(n,
                                  C1)),
                              Power(
                                  Times(Sqr(b), d, Subtract(Times(b, c), Times(a, d)), Plus(m, C1)),
                                  CN1)),
                          x),
                      Dist(
                          Times(
                              Plus(
                                  Times(a, d, f, h, m),
                                  Times(b,
                                      Subtract(Times(d, Plus(Times(f, g), Times(e, h))),
                                          Times(c, f, h, Plus(m, C2))))),
                              Power(Times(Sqr(b), d), CN1)),
                          Integrate(
                              Times(
                                  Power(Plus(a, Times(b, x)),
                                      Plus(m, C1)),
                                  Power(Plus(c, Times(d, x)), n)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, h, m, n), x), EqQ(Plus(m, n,
                      C2), C0), NeQ(m, CN1), Not(
                          And(SumSimplerQ(n, C1), Not(SumSimplerQ(m, C1))))))),
          IIntegrate(144,
              Integrate(
                  Times(Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), m_),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT,
                          x_)), n_),
                      Plus(e_, Times(f_DEFAULT, x_)), Plus(g_DEFAULT, Times(h_DEFAULT, x_))),
                  x_Symbol),
              Condition(Subtract(Simp(Times(Plus(Times(Sqr(b), c, d, e, g, Plus(n, C1)),
                  Times(Sqr(a), c, d, f, h, Plus(n, C1)),
                  Times(a, b,
                      Subtract(
                          Plus(Times(Sqr(d), e, g, Plus(m, C1)), Times(Sqr(c), f, h, Plus(m, C1))),
                          Times(c, d, Plus(Times(f, g), Times(e, h)), Plus(m, n, C2)))),
                  Times(
                      Plus(Times(Sqr(a), Sqr(d), f, h, Plus(n, C1)),
                          Times(CN1, a, b, Sqr(d), Plus(Times(f, g), Times(e, h)), Plus(n,
                              C1)),
                          Times(Sqr(b), Plus(Times(Sqr(c), f, h, Plus(m, C1)),
                              Times(CN1, c, d, Plus(Times(f, g), Times(e, h)), Plus(m, C1)),
                              Times(Sqr(d), e, g, Plus(m, n, C2))))),
                      x)),
                  Power(Plus(a, Times(b, x)), Plus(m, C1)),
                  Power(Plus(c, Times(d, x)), Plus(n, C1)),
                  Power(
                      Times(b, d, Sqr(Subtract(Times(b, c), Times(a, d))), Plus(m, C1),
                          Plus(n, C1)),
                      CN1)),
                  x),
                  Dist(Times(
                      Plus(Times(Sqr(a), Sqr(d), f, h, Plus(C2, Times(C3, n), Sqr(n))),
                          Times(a, b, d, Plus(n, C1),
                              Subtract(Times(C2, c, f, h, Plus(m, C1)),
                                  Times(d, Plus(Times(f, g), Times(e, h)), Plus(m, n, C3)))),
                          Times(Sqr(b),
                              Plus(Times(Sqr(c), f, h, Plus(C2, Times(C3, m), Sqr(m))),
                                  Times(CN1, c, d, Plus(Times(f, g), Times(e, h)), Plus(m, C1),
                                      Plus(m, n, C3)),
                                  Times(Sqr(d), e, g,
                                      Plus(C6, Sqr(m), Times(C5, n), Sqr(n),
                                          Times(m, Plus(Times(C2, n), C5))))))),
                      Power(Times(b, d, Sqr(Subtract(Times(b, c), Times(a, d))), Plus(m, C1),
                          Plus(n, C1)), CN1)),
                      Integrate(
                          Times(Power(Plus(a, Times(b, x)), Plus(m, C1)),
                              Power(Plus(c, Times(d, x)), Plus(n, C1))),
                          x),
                      x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, h), x), LtQ(m, CN1), LtQ(n, CN1)))),
          IIntegrate(145,
              Integrate(Times(Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), m_),
                  Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), n_DEFAULT), Plus(e_, Times(f_DEFAULT,
                      x_)),
                  Plus(g_DEFAULT, Times(h_DEFAULT, x_))), x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(
                              Plus(Times(Power(b, C3), c, e, g, Plus(m, C2)),
                                  Times(CN1, Power(a, C3), d, f, h, Plus(n, C2)),
                                  Times(CN1, Sqr(a), b,
                                      Subtract(Times(c, f, h, m),
                                          Times(d, Plus(Times(f, g), Times(e, h)),
                                              Plus(m, n, C3)))),
                                  Times(
                                      CN1, a, Sqr(b),
                                      Plus(
                                          Times(c, Plus(Times(f, g), Times(e, h))),
                                          Times(d, e, g, Plus(Times(C2, m), n, C4)))),
                                  Times(b, Plus(Times(Sqr(a), d, f, h, Subtract(m, n)),
                                      Times(CN1, a, b,
                                          Subtract(Times(C2, c, f, h, Plus(m, C1)),
                                              Times(d, Plus(Times(f, g), Times(e, h)),
                                                  Plus(n, C1)))),
                                      Times(Sqr(b),
                                          Subtract(
                                              Times(c, Plus(Times(f, g), Times(e, h)), Plus(m, C1)),
                                              Times(d, e, g, Plus(m, n, C2))))),
                                      x)),
                              Power(Plus(a, Times(b, x)), Plus(m, C1)),
                              Power(Plus(c, Times(d, x)), Plus(n, C1)),
                              Power(Times(Sqr(b), Sqr(Subtract(Times(b, c), Times(a, d))),
                                  Plus(m, C1), Plus(m, C2)), CN1)),
                          x),
                      Dist(Subtract(Times(f, h, Power(b, CN2)),
                          Times(d, Plus(m, n, C3), Plus(Times(Sqr(a), d, f, h, Subtract(m, n)),
                              Times(CN1, a, b,
                                  Subtract(Times(C2, c, f, h, Plus(m, C1)),
                                      Times(d, Plus(Times(f, g), Times(e, h)), Plus(n, C1)))),
                              Times(Sqr(b),
                                  Subtract(Times(c, Plus(Times(f, g), Times(e, h)), Plus(m, C1)),
                                      Times(d, e, g, Plus(m, n, C2))))),
                              Power(Times(Sqr(b), Sqr(Subtract(Times(b, c), Times(a, d))),
                                  Plus(m, C1), Plus(m, C2)), CN1))),
                          Integrate(
                              Times(
                                  Power(Plus(a, Times(b, x)),
                                      Plus(m, C2)),
                                  Power(Plus(c, Times(d, x)), n)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, h, m, n), x), Or(LtQ(m, CN2),
                      And(EqQ(Plus(m, n, C3), C0), Not(LtQ(n, CN2))))))),
          IIntegrate(146,
              Integrate(
                  Times(Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), m_),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), n_DEFAULT),
                      Plus(e_, Times(f_DEFAULT, x_)), Plus(g_DEFAULT, Times(h_DEFAULT, x_))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              Plus(Times(Sqr(a), d, f, h, Plus(n, C2)), Times(Sqr(b), d, e, g,
                                  Plus(m, n, C3)),
                                  Times(a, b,
                                      Subtract(Times(c, f, h, Plus(m, C1)),
                                          Times(d, Plus(Times(f, g), Times(e, h)),
                                              Plus(m, n, C3)))),
                                  Times(
                                      b, f, h, Subtract(Times(b, c), Times(a, d)), Plus(m, C1), x)),
                              Power(Plus(a, Times(b, x)), Plus(m, C1)), Power(Plus(c, Times(d, x)),
                                  Plus(n, C1)),
                              Power(Times(Sqr(b), d, Subtract(Times(b, c), Times(a, d)),
                                  Plus(m, C1), Plus(m, n, C3)), CN1)),
                          x),
                      Dist(Times(
                          Plus(Times(Sqr(a), Sqr(d), f, h, Plus(n, C1), Plus(n, C2)),
                              Times(a, b, d, Plus(n, C1),
                                  Subtract(Times(C2, c, f, h, Plus(m, C1)),
                                      Times(d, Plus(Times(f, g), Times(e, h)), Plus(m, n, C3)))),
                              Times(Sqr(b),
                                  Plus(Times(Sqr(c), f, h, Plus(m, C1), Plus(m, C2)),
                                      Times(CN1, c, d, Plus(Times(f, g), Times(e, h)), Plus(m, C1),
                                          Plus(m, n, C3)),
                                      Times(Sqr(d), e, g, Plus(m, n, C2), Plus(m, n, C3))))),
                          Power(
                              Times(Sqr(b), d, Subtract(Times(b, c), Times(a, d)), Plus(m, C1),
                                  Plus(m, n, C3)),
                              CN1)),
                          Integrate(
                              Times(
                                  Power(Plus(a, Times(b, x)),
                                      Plus(m, C1)),
                                  Power(Plus(c, Times(d, x)), n)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, h, m, n), x),
                      Or(And(GeQ(m, CN2), LtQ(m, CN1)), SumSimplerQ(m, C1)), NeQ(m, CN1),
                      NeQ(Plus(m, n, C3), C0)))),
          IIntegrate(147,
              Integrate(Times(
                  Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), m_DEFAULT),
                  Power(Plus(c_DEFAULT,
                      Times(d_DEFAULT, x_)), n_DEFAULT),
                  Plus(e_, Times(f_DEFAULT, x_)), Plus(g_DEFAULT, Times(h_DEFAULT, x_))), x_Symbol),
              Condition(
                  Plus(Negate(
                      Simp(
                          Times(
                              Subtract(
                                  Subtract(
                                      Plus(Times(a, d, f, h, Plus(n, C2)),
                                          Times(b, c, f, h, Plus(m, C2))),
                                      Times(b, d, Plus(Times(f, g), Times(e, h)), Plus(m, n, C3))),
                                  Times(b, d, f, h, Plus(m, n, C2), x)),
                              Power(Plus(
                                  a, Times(b, x)),
                                  Plus(m, C1)),
                              Power(Plus(c, Times(d, x)), Plus(n, C1)),
                              Power(Times(Sqr(b), Sqr(d), Plus(m, n, C2), Plus(m, n, C3)), CN1)),
                          x)),
                      Dist(
                          Times(Plus(Times(Sqr(a), Sqr(d), f, h, Plus(n, C1), Plus(n, C2)),
                              Times(a, b, d, Plus(n, C1),
                                  Subtract(Times(C2, c, f, h, Plus(m, C1)),
                                      Times(d, Plus(Times(f, g), Times(e, h)), Plus(m, n, C3)))),
                              Times(Sqr(b),
                                  Plus(Times(Sqr(c), f, h, Plus(m, C1), Plus(m, C2)),
                                      Times(CN1, c, d, Plus(Times(f, g), Times(e, h)), Plus(m, C1),
                                          Plus(m, n, C3)),
                                      Times(Sqr(d), e, g, Plus(m, n, C2), Plus(m, n, C3))))),
                              Power(Times(Sqr(b), Sqr(d), Plus(m, n, C2), Plus(m, n, C3)), CN1)),
                          Integrate(
                              Times(Power(Plus(a, Times(b, x)), m),
                                  Power(Plus(c, Times(d, x)), n)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, h, m, n), x), NeQ(Plus(m, n, C2), C0),
                      NeQ(Plus(m, n, C3), C0)))),
          IIntegrate(148,
              Integrate(
                  Times(Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), m_),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), n_),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), p_),
                      Plus(g_DEFAULT, Times(h_DEFAULT, x_))),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(Times(Power(Plus(a, Times(b, x)), m),
                          Power(Plus(c, Times(d, x)), n), Power(Plus(e, Times(f, x)), p), Plus(g,
                              Times(h, x))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, h, m), x),
                      Or(IntegersQ(m, n, p), And(IGtQ(n, C0), IGtQ(p, C0)))))),
          IIntegrate(149,
              Integrate(
                  Times(Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), m_),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), n_),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), p_), Plus(g_DEFAULT,
                          Times(h_DEFAULT, x_))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(Times(Subtract(Times(b, g), Times(a, h)),
                          Power(Plus(a, Times(b, x)), Plus(m, C1)), Power(Plus(c, Times(d, x)), n),
                          Power(Plus(e, Times(f, x)), Plus(p, C1)), Power(Times(b,
                              Subtract(Times(b, e), Times(a, f)), Plus(m, C1)), CN1)),
                          x),
                      Dist(
                          Power(Times(b, Subtract(Times(b, e), Times(a,
                              f)), Plus(m,
                                  C1)),
                              CN1),
                          Integrate(Times(Power(Plus(a, Times(b, x)), Plus(m, C1)),
                              Power(Plus(c, Times(d, x)), Subtract(n, C1)),
                              Power(Plus(e, Times(f, x)), p),
                              Simp(Plus(
                                  Times(b, c, Subtract(Times(f, g), Times(e, h)), Plus(m, C1)),
                                  Times(Subtract(Times(b, g), Times(a, h)),
                                      Plus(Times(d, e, n), Times(c, f, Plus(p, C1)))),
                                  Times(d,
                                      Plus(
                                          Times(b, Subtract(Times(f, g), Times(e, h)), Plus(m, C1)),
                                          Times(f, Subtract(Times(b, g), Times(a, h)),
                                              Plus(n, p, C1))),
                                      x)),
                                  x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, h,
                      p), x), LtQ(m,
                          CN1),
                      GtQ(n, C0), IntegerQ(m)))),
          IIntegrate(150,
              Integrate(
                  Times(Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), m_),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT,
                          x_)), n_),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), p_), Plus(g_DEFAULT,
                          Times(h_DEFAULT, x_))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(Times(Subtract(Times(b, g), Times(a, h)),
                          Power(Plus(a, Times(b, x)), Plus(m, C1)), Power(Plus(c, Times(d, x)), n),
                          Power(Plus(e, Times(f, x)), Plus(p, C1)), Power(Times(b,
                              Subtract(Times(b, e), Times(a, f)), Plus(m, C1)), CN1)),
                          x),
                      Dist(
                          Power(Times(b, Subtract(Times(b, e), Times(a,
                              f)), Plus(m,
                                  C1)),
                              CN1),
                          Integrate(Times(Power(Plus(a, Times(b, x)), Plus(m, C1)),
                              Power(Plus(c, Times(d, x)), Subtract(n, C1)),
                              Power(Plus(e, Times(f, x)), p),
                              Simp(Plus(
                                  Times(b, c, Subtract(Times(f, g), Times(e, h)), Plus(m, C1)),
                                  Times(Subtract(Times(b, g), Times(a, h)),
                                      Plus(Times(d, e, n), Times(c, f, Plus(p, C1)))),
                                  Times(d,
                                      Plus(
                                          Times(b, Subtract(Times(f, g), Times(e, h)), Plus(m, C1)),
                                          Times(f, Subtract(Times(b, g), Times(a, h)),
                                              Plus(n, p, C1))),
                                      x)),
                                  x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, h,
                      p), x), LtQ(m, CN1), GtQ(n, C0), IntegersQ(Times(C2, m), Times(C2, n),
                          Times(C2, p))))),
          IIntegrate(151,
              Integrate(
                  Times(Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), m_),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), n_),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), p_),
                      Plus(g_DEFAULT, Times(h_DEFAULT, x_))),
                  x_Symbol),
              Condition(
                  Plus(Simp(
                      Times(Subtract(Times(b, g), Times(a, h)), Power(Plus(a,
                          Times(b, x)), Plus(m, C1)), Power(Plus(c, Times(d, x)), Plus(n, C1)),
                          Power(Plus(e, Times(f, x)), Plus(p, C1)),
                          Power(Times(Plus(m, C1), Subtract(Times(b, c), Times(a, d)), Subtract(
                              Times(b, e), Times(a, f))), CN1)),
                      x),
                      Dist(
                          Power(
                              Times(
                                  Plus(m, C1), Subtract(Times(b, c), Times(a, d)), Subtract(
                                      Times(b, e), Times(a, f))),
                              CN1),
                          Integrate(
                              Times(Power(Plus(a, Times(b, x)), Plus(m, C1)),
                                  Power(Plus(c, Times(d, x)), n), Power(Plus(e,
                                      Times(f, x)), p),
                                  Simp(
                                      Subtract(
                                          Subtract(
                                              Times(
                                                  Plus(Times(a, d, f, g),
                                                      Times(CN1, b, Plus(Times(d, e), Times(c, f)),
                                                          g),
                                                      Times(b, c, e, h)),
                                                  Plus(m, C1)),
                                              Times(Subtract(Times(b, g), Times(a, h)),
                                                  Plus(Times(d, e, Plus(n, C1)),
                                                      Times(c, f, Plus(p, C1))))),
                                          Times(d, f, Subtract(Times(b, g), Times(a, h)),
                                              Plus(m, n, p, C3), x)),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, h, n, p), x), LtQ(m, CN1), IntegerQ(m)))),
          IIntegrate(152,
              Integrate(
                  Times(Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), m_),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), n_),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), p_), Plus(g_DEFAULT,
                          Times(h_DEFAULT, x_))),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Subtract(Times(b, g), Times(a, h)),
                              Power(Plus(a, Times(b, x)), Plus(m, C1)),
                              Power(Plus(c, Times(d, x)), Plus(n, C1)),
                              Power(Plus(e, Times(f, x)), Plus(p, C1)),
                              Power(
                                  Times(Plus(m, C1), Subtract(Times(b, c), Times(a, d)),
                                      Subtract(Times(b, e), Times(a, f))),
                                  CN1)),
                          x),
                      Dist(
                          Power(
                              Times(
                                  Plus(m, C1), Subtract(Times(b, c), Times(a, d)), Subtract(
                                      Times(b, e), Times(a, f))),
                              CN1),
                          Integrate(
                              Times(Power(Plus(a, Times(b, x)), Plus(m, C1)),
                                  Power(Plus(c, Times(d, x)), n), Power(Plus(e,
                                      Times(f, x)), p),
                                  Simp(
                                      Subtract(
                                          Subtract(
                                              Times(
                                                  Plus(Times(a, d, f, g),
                                                      Times(CN1, b, Plus(Times(d, e), Times(c, f)),
                                                          g),
                                                      Times(b, c, e, h)),
                                                  Plus(m, C1)),
                                              Times(Subtract(Times(b, g), Times(a, h)),
                                                  Plus(Times(d, e, Plus(n, C1)),
                                                      Times(c, f, Plus(p, C1))))),
                                          Times(d, f, Subtract(Times(b, g), Times(a, h)),
                                              Plus(m, n, p, C3), x)),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, h, n,
                      p), x), LtQ(m,
                          CN1),
                      IntegersQ(Times(C2, m), Times(C2, n), Times(C2, p))))),
          IIntegrate(153,
              Integrate(
                  Times(Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), m_),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), n_),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), p_), Plus(g_DEFAULT,
                          Times(h_DEFAULT, x_))),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(h, Power(Plus(a, Times(b, x)), m),
                              Power(Plus(c, Times(d, x)), Plus(n, C1)),
                              Power(Plus(e, Times(f, x)), Plus(p,
                                  C1)),
                              Power(Times(d, f, Plus(m, n, p, C2)), CN1)),
                          x),
                      Dist(Power(Times(d, f, Plus(m, n, p, C2)), CN1),
                          Integrate(Times(
                              Power(Plus(a, Times(b, x)),
                                  Subtract(m, C1)),
                              Power(Plus(c, Times(d, x)), n), Power(Plus(e, Times(f, x)), p), Simp(
                                  Plus(Times(a, d, f, g, Plus(m, n, p, C2)),
                                      Times(CN1, h,
                                          Plus(Times(b, c, e, m),
                                              Times(a,
                                                  Plus(Times(d, e, Plus(n, C1)),
                                                      Times(c, f, Plus(p, C1)))))),
                                      Times(
                                          Plus(Times(b, d, f, g, Plus(m, n, p, C2)),
                                              Times(h,
                                                  Subtract(Times(a, d, f, m),
                                                      Times(b,
                                                          Plus(Times(d, e, Plus(m, n, C1)),
                                                              Times(c, f, Plus(m, p, C1))))))),
                                          x)),
                                  x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, h, n, p), x), GtQ(m, C0),
                      NeQ(Plus(m, n, p, C2), C0), IntegerQ(m)))),
          IIntegrate(154,
              Integrate(Times(Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), m_),
                  Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), n_),
                  Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), p_), Plus(g_DEFAULT,
                      Times(h_DEFAULT, x_))),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(h, Power(Plus(a, Times(b, x)), m),
                              Power(Plus(c, Times(d, x)), Plus(n, C1)),
                              Power(Plus(e, Times(f, x)), Plus(p, C1)), Power(
                                  Times(d, f, Plus(m, n, p, C2)), CN1)),
                          x),
                      Dist(
                          Power(Times(d, f,
                              Plus(m, n, p, C2)), CN1),
                          Integrate(
                              Times(Power(Plus(a, Times(b, x)), Subtract(m, C1)),
                                  Power(Plus(c, Times(d,
                                      x)), n),
                                  Power(Plus(e, Times(f, x)), p),
                                  Simp(Plus(Times(a, d, f, g, Plus(m, n, p, C2)),
                                      Times(CN1, h,
                                          Plus(Times(b, c, e, m),
                                              Times(a, Plus(Times(d, e, Plus(n, C1)),
                                                  Times(c, f, Plus(p, C1)))))),
                                      Times(
                                          Plus(Times(b, d, f, g, Plus(m, n, p, C2)),
                                              Times(h,
                                                  Subtract(Times(a, d, f, m),
                                                      Times(b,
                                                          Plus(Times(d, e, Plus(m, n, C1)),
                                                              Times(c, f, Plus(m, p, C1))))))),
                                          x)),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, h, n, p), x), GtQ(m, C0),
                      NeQ(Plus(m, n, p,
                          C2), C0),
                      IntegersQ(Times(C2, m), Times(C2, n), Times(C2, p))))),
          IIntegrate(155,
              Integrate(
                  Times(Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), m_),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT,
                          x_)), n_),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), p_),
                      Plus(g_DEFAULT, Times(h_DEFAULT, x_))),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Subtract(Times(b, g), Times(a, h)),
                              Power(Plus(a, Times(b, x)), Plus(m, C1)),
                              Power(Plus(c, Times(d, x)), Plus(n, C1)),
                              Power(Plus(e, Times(f, x)), Plus(p, C1)),
                              Power(
                                  Times(Plus(m, C1), Subtract(Times(b, c), Times(a, d)),
                                      Subtract(Times(b, e), Times(a, f))),
                                  CN1)),
                          x),
                      Dist(
                          Power(
                              Times(
                                  Plus(m, C1), Subtract(Times(b, c), Times(a, d)), Subtract(
                                      Times(b, e), Times(a, f))),
                              CN1),
                          Integrate(
                              Times(Power(Plus(a, Times(b, x)), Plus(m, C1)),
                                  Power(Plus(c, Times(d, x)), n), Power(Plus(e,
                                      Times(f, x)), p),
                                  Simp(
                                      Subtract(
                                          Subtract(
                                              Times(
                                                  Plus(Times(a, d, f, g),
                                                      Times(CN1, b, Plus(Times(d, e), Times(c, f)),
                                                          g),
                                                      Times(b, c, e, h)),
                                                  Plus(m, C1)),
                                              Times(Subtract(Times(b, g), Times(a, h)),
                                                  Plus(Times(d, e, Plus(n, C1)),
                                                      Times(c, f, Plus(p, C1))))),
                                          Times(d, f, Subtract(Times(b, g), Times(a, h)),
                                              Plus(m, n, p, C3), x)),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, h, n, p), x), ILtQ(Plus(m, n, p, C2), C0),
                      NeQ(m, CN1), Or(
                          SumSimplerQ(m, C1), And(
                              Not(And(NeQ(n, CN1),
                                  SumSimplerQ(n, C1))),
                              Not(And(NeQ(p, CN1), SumSimplerQ(p, C1)))))))),
          IIntegrate(156,
              Integrate(
                  Times(Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), CN1),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT,
                          x_)), CN1),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), p_), Plus(g_DEFAULT,
                          Times(h_DEFAULT, x_))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(
                          Times(
                              Subtract(Times(b, g), Times(a, h)), Power(
                                  Subtract(Times(b, c), Times(a, d)), CN1)),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), p),
                                  Power(Plus(a, Times(b, x)), CN1)),
                              x),
                          x),
                      Dist(
                          Times(
                              Subtract(Times(d, g), Times(c, h)),
                              Power(Subtract(Times(b, c), Times(a, d)), CN1)),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), p),
                                  Power(Plus(c, Times(d, x)), CN1)),
                              x),
                          x)),
                  FreeQ(List(a, b, c, d, e, f, g, h), x))),
          IIntegrate(157,
              Integrate(
                  Times(Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), CN1),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), n_),
                      Power(Plus(e_DEFAULT,
                          Times(f_DEFAULT, x_)), p_),
                      Plus(g_DEFAULT, Times(h_DEFAULT, x_))),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(Times(h, Power(b, CN1)),
                          Integrate(
                              Times(Power(Plus(c, Times(d, x)), n),
                                  Power(Plus(e, Times(f, x)), p)),
                              x),
                          x),
                      Dist(
                          Times(Subtract(Times(b, g), Times(a,
                              h)), Power(b,
                                  CN1)),
                          Integrate(Times(Power(Plus(c, Times(d, x)), n), Power(Plus(e, Times(f,
                              x)), p), Power(Plus(a, Times(b, x)),
                                  CN1)),
                              x),
                          x)),
                  FreeQ(List(a, b, c, d, e, f, g, h, n, p), x))),
          IIntegrate(158, Integrate(Times(Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), CN1D2),
              Power(Plus(c_, Times(d_DEFAULT, x_)), CN1D2), Power(Plus(e_, Times(f_DEFAULT, x_)),
                  CN1D2),
              Plus(g_DEFAULT, Times(h_DEFAULT, x_))), x_Symbol), Condition(
                  Plus(
                      Dist(Times(h, Power(f, CN1)),
                          Integrate(Times(Sqrt(Plus(e, Times(f, x))),
                              Power(Times(Sqrt(Plus(a, Times(b, x))), Sqrt(Plus(c, Times(d, x)))),
                                  CN1)),
                              x),
                          x),
                      Dist(Times(Subtract(Times(f, g), Times(e, h)), Power(f, CN1)),
                          Integrate(
                              Power(
                                  Times(Sqrt(Plus(a, Times(b, x))), Sqrt(Plus(c, Times(d, x))),
                                      Sqrt(Plus(e, Times(f, x)))),
                                  CN1),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, h), x),
                      SimplerQ(Plus(a, Times(b, x)), Plus(e, Times(f, x))), SimplerQ(
                          Plus(c, Times(d, x)), Plus(e, Times(f, x)))))),
          IIntegrate(159,
              Integrate(
                  Times(Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), m_),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), n_),
                      Power(Plus(e_DEFAULT,
                          Times(f_DEFAULT, x_)), p_),
                      Plus(g_DEFAULT, Times(h_DEFAULT, x_))),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(Times(h, Power(b, CN1)),
                          Integrate(
                              Times(Power(Plus(a, Times(b, x)), Plus(m, C1)),
                                  Power(Plus(c, Times(d, x)), n), Power(Plus(e, Times(f, x)), p)),
                              x),
                          x),
                      Dist(
                          Times(Subtract(Times(b, g), Times(a, h)), Power(b,
                              CN1)),
                          Integrate(
                              Times(Power(Plus(a, Times(b, x)), m), Power(Plus(c, Times(d, x)), n),
                                  Power(Plus(e, Times(f, x)), p)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, h, m, n, p), x),
                      Or(SumSimplerQ(m, C1),
                          And(Not(SumSimplerQ(n, C1)), Not(SumSimplerQ(p, C1))))))),
          IIntegrate(160,
              Integrate(
                  Times(Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), m_), Sqrt(Plus(c_DEFAULT,
                      Times(d_DEFAULT, x_))), Sqrt(Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                      Sqrt(Plus(g_DEFAULT, Times(h_DEFAULT, x_)))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(Times(Power(Plus(a, Times(b, x)), Plus(m, C1)),
                          Sqrt(Plus(c, Times(d, x))), Sqrt(Plus(e, Times(f, x))),
                          Sqrt(Plus(g, Times(h, x))), Power(Times(b, Plus(m, C1)), CN1)), x),
                      Dist(Power(Times(C2, b, Plus(m, C1)), CN1),
                          Integrate(Times(Power(Plus(a, Times(b, x)), Plus(m, C1)),
                              Simp(Plus(Times(d, e, g), Times(c, f, g), Times(c, e, h),
                                  Times(C2, Plus(Times(d, f, g), Times(d, e, h), Times(c, f, h)),
                                      x),
                                  Times(C3, d, f, h, Sqr(x))), x),
                              Power(Times(Sqrt(Plus(c, Times(d, x))), Sqrt(Plus(e, Times(f, x))),
                                  Sqrt(Plus(g, Times(h, x)))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, h, m), x), IntegerQ(Times(C2, m)),
                      LtQ(m, CN1)))));
}
