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
class IntRules318 {
  public static IAST RULES =
      List(
          IIntegrate(6361,
              Integrate(
                  Times(
                      Erf(Plus(a_DEFAULT,
                          Times(b_DEFAULT, x_))),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Plus(c, Times(d, x)), Plus(m, C1)), Erf(Plus(a, Times(b, x))),
                              Power(Times(d, Plus(m, C1)), CN1)),
                          x),
                      Dist(Times(C2, b, Power(Times(Sqrt(Pi), d, Plus(m, C1)), CN1)),
                          Integrate(
                              Times(Power(Plus(c, Times(d, x)), Plus(m, C1)),
                                  Power(Exp(Sqr(Plus(a, Times(b, x)))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, m), x), NeQ(m, CN1)))),
          IIntegrate(6362,
              Integrate(
                  Times(
                      Erfc(Plus(a_DEFAULT,
                          Times(b_DEFAULT, x_))),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(
                              Power(Plus(c, Times(d, x)), Plus(m, C1)), Erfc(Plus(a, Times(b, x))),
                              Power(Times(d, Plus(m, C1)), CN1)),
                          x),
                      Dist(
                          Times(C2, b, Power(Times(Sqrt(Pi), d, Plus(m, C1)),
                              CN1)),
                          Integrate(
                              Times(Power(Plus(c, Times(d, x)), Plus(m, C1)),
                                  Power(Exp(Sqr(Plus(a, Times(b, x)))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, m), x), NeQ(m, CN1)))),
          IIntegrate(6363,
              Integrate(
                  Times(
                      Erfi(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), Power(
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              Power(Plus(c, Times(d, x)), Plus(m, C1)), Erfi(Plus(a, Times(b, x))),
                              Power(Times(d, Plus(m, C1)), CN1)),
                          x),
                      Dist(Times(C2, b, Power(Times(Sqrt(Pi), d, Plus(m, C1)), CN1)),
                          Integrate(Times(Power(Plus(c, Times(d, x)), Plus(m, C1)),
                              Exp(Sqr(Plus(a, Times(b, x))))), x),
                          x)),
                  And(FreeQ(List(a, b, c, d, m), x), NeQ(m, CN1)))),
          IIntegrate(6364,
              Integrate(Times(Sqr(Erf(
                  Times(b_DEFAULT, x_))), Power(x_,
                      m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              Power(x, Plus(m, C1)), Sqr(Erf(Times(b, x))), Power(Plus(m, C1),
                                  CN1)),
                          x),
                      Dist(
                          Times(C4, b, Power(Times(Sqrt(Pi), Plus(m, C1)),
                              CN1)),
                          Integrate(
                              Times(
                                  Power(x, Plus(m, C1)), Erf(Times(b,
                                      x)),
                                  Power(Exp(Times(Sqr(b), Sqr(x))), CN1)),
                              x),
                          x)),
                  And(FreeQ(b, x), Or(IGtQ(m, C0), ILtQ(Times(C1D2, Plus(m, C1)), C0))))),
          IIntegrate(6365,
              Integrate(Times(Sqr(Erfc(Times(b_DEFAULT, x_))), Power(x_, m_DEFAULT)), x_Symbol),
              Condition(
                  Plus(
                      Simp(Times(Power(x, Plus(m, C1)), Sqr(Erfc(Times(b, x))),
                          Power(Plus(m, C1), CN1)), x),
                      Dist(
                          Times(C4, b, Power(Times(Sqrt(Pi), Plus(m, C1)), CN1)),
                          Integrate(Times(Power(x, Plus(m, C1)), Erfc(Times(b, x)),
                              Power(Exp(Times(Sqr(b), Sqr(x))), CN1)), x),
                          x)),
                  And(FreeQ(b, x), Or(IGtQ(m, C0), ILtQ(Times(C1D2, Plus(m, C1)), C0))))),
          IIntegrate(6366,
              Integrate(Times(Sqr(Erfi(Times(b_DEFAULT, x_))),
                  Power(x_, m_DEFAULT)), x_Symbol),
              Condition(
                  Subtract(
                      Simp(Times(Power(x, Plus(m, C1)), Sqr(Erfi(Times(b, x))),
                          Power(Plus(m, C1), CN1)), x),
                      Dist(Times(C4, b, Power(Times(Sqrt(Pi), Plus(m, C1)), CN1)),
                          Integrate(
                              Times(Power(x, Plus(m, C1)), Exp(Times(Sqr(b), Sqr(x))),
                                  Erfi(Times(b, x))),
                              x),
                          x)),
                  And(FreeQ(b, x), Or(IGtQ(m, C0), ILtQ(Times(C1D2, Plus(m, C1)), C0))))),
          IIntegrate(6367,
              Integrate(
                  Times(
                      Sqr(Erf(Plus(a_,
                          Times(b_DEFAULT, x_)))),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Power(b,
                          Plus(m, C1)), CN1),
                      Subst(
                          Integrate(
                              ExpandIntegrand(
                                  Sqr(Erf(
                                      x)),
                                  Power(Plus(Times(b, c), Times(CN1, a, d), Times(d, x)), m), x),
                              x),
                          x, Plus(a, Times(b, x))),
                      x),
                  And(FreeQ(List(a, b, c, d), x), IGtQ(m, C0)))),
          IIntegrate(6368,
              Integrate(
                  Times(
                      Sqr(Erfc(Plus(a_, Times(b_DEFAULT, x_)))), Power(
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Power(b,
                          Plus(m, C1)), CN1),
                      Subst(
                          Integrate(
                              ExpandIntegrand(
                                  Sqr(Erfc(
                                      x)),
                                  Power(Plus(Times(b, c), Times(CN1, a, d), Times(d, x)), m), x),
                              x),
                          x, Plus(a, Times(b, x))),
                      x),
                  And(FreeQ(List(a, b, c, d), x), IGtQ(m, C0)))),
          IIntegrate(6369,
              Integrate(
                  Times(
                      Sqr(Erfi(Plus(a_, Times(b_DEFAULT, x_)))), Power(
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(Power(b, Plus(m, C1)), CN1),
                      Subst(
                          Integrate(
                              ExpandIntegrand(Sqr(Erfi(x)),
                                  Power(Plus(Times(b, c), Times(CN1, a, d), Times(d, x)), m), x),
                              x),
                          x, Plus(a, Times(b, x))),
                      x),
                  And(FreeQ(List(a, b, c, d), x), IGtQ(m, C0)))),
          IIntegrate(6370,
              Integrate(
                  Times(
                      Power(Erf(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), n_DEFAULT), Power(
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(Power(Plus(c, Times(d, x)), m),
                          Power(Erf(Plus(a, Times(b, x))), n)),
                      x),
                  FreeQ(List(a, b, c, d, m, n), x))),
          IIntegrate(6371,
              Integrate(
                  Times(
                      Power(Erfc(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), n_DEFAULT),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(Power(Plus(c, Times(d, x)), m),
                          Power(Erfc(Plus(a, Times(b, x))), n)),
                      x),
                  FreeQ(List(a, b, c, d, m, n), x))),
          IIntegrate(6372,
              Integrate(
                  Times(
                      Power(Erfi(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), n_DEFAULT), Power(
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(Unintegrable(
                  Times(Power(Plus(c, Times(d, x)), m), Power(Erfi(Plus(a, Times(b, x))), n)), x),
                  FreeQ(List(a, b, c, d, m, n), x))),
          IIntegrate(6373,
              Integrate(
                  Times(Exp(Plus(c_DEFAULT, Times(d_DEFAULT, Sqr(x_)))),
                      Power(Erf(Times(b_DEFAULT, x_)), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Exp(c), Sqrt(Pi), Power(Times(C2, b), CN1)),
                      Subst(Integrate(Power(x, n), x), x, Erf(Times(b, x))), x),
                  And(FreeQ(List(b, c, d, n), x), EqQ(d, Negate(Sqr(b)))))),
          IIntegrate(6374,
              Integrate(
                  Times(
                      Exp(Plus(c_DEFAULT, Times(d_DEFAULT, Sqr(x_)))),
                      Power(Erfc(Times(b_DEFAULT, x_)), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Negate(Dist(Times(Exp(c), Sqrt(Pi), Power(Times(C2, b), CN1)),
                      Subst(Integrate(Power(x, n), x), x, Erfc(Times(b, x))), x)),
                  And(FreeQ(List(b, c, d, n), x), EqQ(d, Negate(Sqr(b)))))),
          IIntegrate(6375,
              Integrate(
                  Times(Exp(Plus(c_DEFAULT, Times(d_DEFAULT, Sqr(x_)))),
                      Power(Erfi(Times(b_DEFAULT, x_)), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Exp(c), Sqrt(Pi), Power(Times(C2, b), CN1)), Subst(
                          Integrate(Power(x, n), x), x, Erfi(Times(b, x))),
                      x),
                  And(FreeQ(List(b, c, d, n), x), EqQ(d, Sqr(b))))),
          IIntegrate(6376,
              Integrate(
                  Times(Exp(Plus(c_DEFAULT,
                      Times(d_DEFAULT, Sqr(x_)))), Erf(
                          Times(b_DEFAULT, x_))),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(b, Exp(c), Sqr(x),
                          HypergeometricPFQ(List(C1, C1), List(QQ(3L, 2L), C2),
                              Times(Sqr(b), Sqr(x))),
                          Power(Pi, CN1D2)),
                      x),
                  And(FreeQ(List(b, c, d), x), EqQ(d, Sqr(b))))),
          IIntegrate(6377,
              Integrate(
                  Times(Exp(Plus(c_DEFAULT, Times(d_DEFAULT, Sqr(x_)))),
                      Erfc(Times(b_DEFAULT, x_))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Integrate(Exp(Plus(c, Times(d, Sqr(x)))), x), Integrate(
                          Times(Exp(Plus(c, Times(d, Sqr(x)))), Erf(Times(b, x))), x)),
                  And(FreeQ(List(b, c, d), x), EqQ(d, Sqr(b))))),
          IIntegrate(6378,
              Integrate(
                  Times(
                      Exp(Plus(c_DEFAULT, Times(d_DEFAULT, Sqr(x_)))), Erfi(Times(b_DEFAULT, x_))),
                  x_Symbol),
              Condition(
                  Simp(Times(b, Exp(c), Sqr(x),
                      HypergeometricPFQ(List(C1, C1), List(QQ(3L, 2L), C2),
                          Times(CN1, Sqr(b), Sqr(x))),
                      Power(Pi, CN1D2)), x),
                  And(FreeQ(List(b, c, d), x), EqQ(d, Negate(Sqr(b)))))),
          IIntegrate(6379,
              Integrate(Times(Exp(Plus(c_DEFAULT, Times(d_DEFAULT, Sqr(x_)))),
                  Power(Erf(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), n_DEFAULT)), x_Symbol),
              Condition(Unintegrable(
                  Times(Exp(Plus(c, Times(d, Sqr(x)))), Power(Erf(Plus(a, Times(b, x))), n)), x),
                  FreeQ(List(a, b, c, d, n), x))),
          IIntegrate(6380,
              Integrate(Times(Exp(Plus(c_DEFAULT, Times(d_DEFAULT, Sqr(x_)))),
                  Power(Erfc(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), n_DEFAULT)), x_Symbol),
              Condition(Unintegrable(
                  Times(Exp(Plus(c, Times(d, Sqr(x)))), Power(Erfc(Plus(a, Times(b, x))), n)), x),
                  FreeQ(List(a, b, c, d, n), x))));
}
