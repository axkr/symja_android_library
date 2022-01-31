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
class IntRules269 {
  public static IAST RULES =
      List(
          IIntegrate(5381,
              Integrate(
                  Times(Cosh(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_)))),
                      Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(
                          e, Sinh(Plus(a, Times(b, x), Times(c, Sqr(x)))), Power(Times(C2, c),
                              CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e), x),
                      EqQ(Subtract(Times(b, e), Times(C2, c, d)), C0)))),
          IIntegrate(5382,
              Integrate(
                  Times(
                      Plus(d_DEFAULT, Times(e_DEFAULT, x_)),
                      Sinh(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(Times(e, Cosh(Plus(a, Times(b, x), Times(c, Sqr(x)))),
                          Power(Times(C2, c), CN1)), x),
                      Dist(Times(Subtract(Times(b, e), Times(C2, c, d)), Power(Times(C2, c),
                          CN1)), Integrate(Sinh(Plus(a, Times(b, x), Times(c, Sqr(x)))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x),
                      NeQ(Subtract(Times(b, e), Times(C2, c, d)), C0)))),
          IIntegrate(5383,
              Integrate(Times(
                  Cosh(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_)))),
                  Plus(d_DEFAULT, Times(e_DEFAULT, x_))), x_Symbol),
              Condition(
                  Subtract(Simp(Times(
                      e, Sinh(Plus(a, Times(b, x), Times(c, Sqr(x)))), Power(Times(C2, c), CN1)),
                      x),
                      Dist(Times(Subtract(Times(b, e), Times(C2, c, d)), Power(Times(C2, c), CN1)),
                          Integrate(Cosh(Plus(a, Times(b, x), Times(c, Sqr(x)))), x), x)),
                  And(FreeQ(List(a, b, c, d, e), x),
                      NeQ(Subtract(Times(b, e), Times(C2, c, d)), C0)))),
          IIntegrate(5384,
              Integrate(
                  Times(Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_), Sinh(Plus(a_DEFAULT,
                      Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))))),
                  x_Symbol),
              Condition(Subtract(
                  Simp(
                      Times(e, Power(Plus(d, Times(e, x)), Subtract(m, C1)),
                          Cosh(Plus(a, Times(b, x), Times(c, Sqr(x)))), Power(Times(C2, c), CN1)),
                      x),
                  Dist(Times(Sqr(e), Subtract(m, C1), Power(Times(C2, c), CN1)),
                      Integrate(
                          Times(Power(Plus(d, Times(e, x)), Subtract(m, C2)),
                              Cosh(Plus(a, Times(b, x), Times(c, Sqr(x))))),
                          x),
                      x)),
                  And(FreeQ(List(a, b, c, d, e), x), GtQ(m, C1),
                      EqQ(Subtract(Times(b, e), Times(C2, c, d)), C0)))),
          IIntegrate(5385,
              Integrate(
                  Times(Cosh(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_)))),
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(e, Power(Plus(d, Times(e, x)), Subtract(m, C1)),
                              Sinh(Plus(a, Times(b, x), Times(c, Sqr(x)))), Power(Times(C2, c),
                                  CN1)),
                          x),
                      Dist(
                          Times(Sqr(e), Subtract(m, C1), Power(Times(C2, c),
                              CN1)),
                          Integrate(Times(Power(Plus(d, Times(e, x)), Subtract(m, C2)),
                              Sinh(Plus(a, Times(b, x), Times(c, Sqr(x))))), x),
                          x)),
                  And(FreeQ(List(a, b, c, d,
                      e), x), GtQ(m,
                          C1),
                      EqQ(Subtract(Times(b, e), Times(C2, c, d)), C0)))),
          IIntegrate(5386,
              Integrate(
                  Times(Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_),
                      Sinh(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(e, Power(Plus(d, Times(e, x)), Subtract(m, C1)),
                              Cosh(Plus(a, Times(b, x),
                                  Times(c, Sqr(x)))),
                              Power(Times(C2, c), CN1)),
                          x),
                      Negate(Dist(Times(Sqr(e), Subtract(m, C1), Power(Times(C2, c), CN1)),
                          Integrate(Times(Power(Plus(d, Times(e, x)), Subtract(m, C2)),
                              Cosh(Plus(a, Times(b, x), Times(c, Sqr(x))))), x),
                          x)),
                      Negate(Dist(
                          Times(Subtract(Times(b, e), Times(C2, c, d)), Power(Times(C2, c), CN1)),
                          Integrate(Times(Power(Plus(d, Times(e, x)), Subtract(m, C1)),
                              Sinh(Plus(a, Times(b, x), Times(c, Sqr(x))))), x),
                          x))),
                  And(FreeQ(List(a, b, c, d,
                      e), x), GtQ(m,
                          C1),
                      NeQ(Subtract(Times(b, e), Times(C2, c, d)), C0)))),
          IIntegrate(5387,
              Integrate(
                  Times(
                      Cosh(Plus(a_DEFAULT, Times(b_DEFAULT, x_),
                          Times(c_DEFAULT, Sqr(x_)))),
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(e, Power(Plus(d, Times(e, x)), Subtract(m, C1)),
                              Sinh(Plus(a, Times(b, x),
                                  Times(c, Sqr(x)))),
                              Power(Times(C2, c), CN1)),
                          x),
                      Negate(
                          Dist(Times(Sqr(e), Subtract(m, C1), Power(Times(C2, c), CN1)),
                              Integrate(
                                  Times(Power(Plus(d, Times(e, x)), Subtract(m, C2)),
                                      Sinh(Plus(a, Times(b, x), Times(c, Sqr(x))))),
                                  x),
                              x)),
                      Negate(Dist(
                          Times(Subtract(Times(b, e), Times(C2, c, d)), Power(Times(C2, c), CN1)),
                          Integrate(Times(Power(Plus(d, Times(e, x)), Subtract(m, C1)),
                              Cosh(Plus(a, Times(b, x), Times(c, Sqr(x))))), x),
                          x))),
                  And(FreeQ(List(a, b, c, d,
                      e), x), GtQ(m,
                          C1),
                      NeQ(Subtract(Times(b, e), Times(C2, c, d)), C0)))),
          IIntegrate(5388,
              Integrate(
                  Times(
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_), Sinh(Plus(a_DEFAULT,
                          Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(Times(Power(Plus(d, Times(e, x)), Plus(m, C1)),
                          Sinh(Plus(a, Times(b, x), Times(c, Sqr(x)))),
                          Power(Times(e, Plus(m, C1)), CN1)), x),
                      Dist(Times(C2, c, Power(Times(Sqr(e), Plus(m, C1)), CN1)),
                          Integrate(
                              Times(Power(Plus(d, Times(e, x)), Plus(m, C2)),
                                  Cosh(Plus(a, Times(b, x), Times(c, Sqr(x))))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d,
                      e), x), LtQ(m,
                          CN1),
                      EqQ(Subtract(Times(b, e), Times(C2, c, d)), C0)))),
          IIntegrate(5389,
              Integrate(
                  Times(
                      Cosh(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_)))), Power(
                          Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Plus(d, Times(e, x)), Plus(m, C1)),
                              Cosh(Plus(a, Times(b, x), Times(c, Sqr(x)))), Power(
                                  Times(e, Plus(m, C1)), CN1)),
                          x),
                      Dist(Times(C2, c, Power(Times(Sqr(e), Plus(m, C1)), CN1)),
                          Integrate(Times(Power(Plus(d, Times(e, x)), Plus(m, C2)),
                              Sinh(Plus(a, Times(b, x), Times(c, Sqr(x))))), x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), LtQ(m, CN1),
                      EqQ(Subtract(Times(b, e), Times(C2, c, d)), C0)))),
          IIntegrate(5390,
              Integrate(
                  Times(
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT,
                          x_)), m_),
                      Sinh(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Plus(d, Times(e, x)), Plus(m, C1)),
                              Sinh(Plus(a, Times(b, x),
                                  Times(c, Sqr(x)))),
                              Power(Times(e, Plus(m, C1)), CN1)),
                          x),
                      Negate(
                          Dist(Times(C2, c, Power(Times(Sqr(e), Plus(m, C1)), CN1)),
                              Integrate(Times(Power(Plus(d, Times(e, x)), Plus(m, C2)),
                                  Cosh(Plus(a, Times(b, x), Times(c, Sqr(x))))), x),
                              x)),
                      Negate(Dist(
                          Times(Subtract(Times(b, e), Times(C2, c, d)),
                              Power(Times(Sqr(e), Plus(m, C1)), CN1)),
                          Integrate(Times(Power(Plus(d, Times(e, x)), Plus(m, C1)),
                              Cosh(Plus(a, Times(b, x), Times(c, Sqr(x))))), x),
                          x))),
                  And(FreeQ(List(a, b, c, d,
                      e), x), LtQ(m,
                          CN1),
                      NeQ(Subtract(Times(b, e), Times(C2, c, d)), C0)))),
          IIntegrate(5391,
              Integrate(
                  Times(
                      Cosh(Plus(a_DEFAULT, Times(b_DEFAULT, x_),
                          Times(c_DEFAULT, Sqr(x_)))),
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Plus(d, Times(e, x)), Plus(m, C1)),
                              Cosh(Plus(a, Times(b, x),
                                  Times(c, Sqr(x)))),
                              Power(Times(e, Plus(m, C1)), CN1)),
                          x),
                      Negate(
                          Dist(Times(C2, c, Power(Times(Sqr(e), Plus(m, C1)), CN1)),
                              Integrate(
                                  Times(
                                      Power(Plus(d, Times(e, x)), Plus(m, C2)), Sinh(
                                          Plus(a, Times(b, x), Times(c, Sqr(x))))),
                                  x),
                              x)),
                      Negate(Dist(
                          Times(Subtract(Times(b, e), Times(C2, c, d)),
                              Power(Times(Sqr(e), Plus(m, C1)), CN1)),
                          Integrate(Times(Power(Plus(d, Times(e, x)), Plus(m, C1)),
                              Sinh(Plus(a, Times(b, x), Times(c, Sqr(x))))), x),
                          x))),
                  And(FreeQ(List(a, b, c, d, e), x), LtQ(m, CN1),
                      NeQ(Subtract(Times(b, e), Times(C2, c, d)), C0)))),
          IIntegrate(5392,
              Integrate(
                  Times(
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT,
                          x_)), m_DEFAULT),
                      Sinh(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))))),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(Power(Plus(d, Times(e, x)), m),
                          Sinh(Plus(a, Times(b, x), Times(c, Sqr(x))))),
                      x),
                  FreeQ(List(a, b, c, d, e, m), x))),
          IIntegrate(5393,
              Integrate(
                  Times(
                      Cosh(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_)))), Power(
                          Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(Power(Plus(d, Times(e, x)), m),
                          Cosh(Plus(a, Times(b, x), Times(c, Sqr(x))))),
                      x),
                  FreeQ(List(a, b, c, d, e, m), x))),
          IIntegrate(5394,
              Integrate(
                  Times(Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_DEFAULT),
                      Power(Sinh(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_)))),
                          n_)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandTrigReduce(Power(Plus(d, Times(e, x)), m),
                          Power(Sinh(Plus(a, Times(b, x), Times(c, Sqr(x)))), n), x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, m), x), IGtQ(n, C1)))),
          IIntegrate(5395,
              Integrate(Times(
                  Power(Cosh(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_)))), n_),
                  Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_DEFAULT)), x_Symbol),
              Condition(
                  Integrate(ExpandTrigReduce(
                      Power(Plus(d, Times(e, x)), m),
                      Power(Cosh(Plus(a, Times(b, x), Times(c, Sqr(x)))), n), x), x),
                  And(FreeQ(List(a, b, c, d, e, m), x), IGtQ(n, C1)))),
          IIntegrate(5396,
              Integrate(Times(Power(u_, m_DEFAULT), Power(Sinh(v_), n_DEFAULT)), x_Symbol),
              Condition(
                  Integrate(Times(Power(ExpandToSum(u, x), m), Power(Sinh(ExpandToSum(v, x)), n)),
                      x),
                  And(FreeQ(m, x), IGtQ(n, C0), LinearQ(u, x), QuadraticQ(v, x),
                      Not(And(LinearMatchQ(u, x), QuadraticMatchQ(v, x)))))),
          IIntegrate(5397,
              Integrate(Times(Power(Cosh(v_), n_DEFAULT),
                  Power(u_, m_DEFAULT)), x_Symbol),
              Condition(
                  Integrate(Times(Power(ExpandToSum(u, x), m),
                      Power(Cosh(ExpandToSum(v, x)), n)), x),
                  And(FreeQ(m, x), IGtQ(n, C0), LinearQ(u, x), QuadraticQ(v, x),
                      Not(And(LinearMatchQ(u, x), QuadraticMatchQ(v, x)))))),
          IIntegrate(5398,
              Integrate(
                  Times(
                      Power(u_, m_DEFAULT),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, Tanh(v_))), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          Power(ExpandToSum(u, x),
                              m),
                          Power(Plus(a, Times(b, Tanh(ExpandToSum(v, x)))), n)),
                      x),
                  And(FreeQ(List(a, b, m, n), x), LinearQ(List(u, v), x),
                      Not(LinearMatchQ(List(u, v), x))))),
          IIntegrate(5399,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(Coth(v_), b_DEFAULT)), n_DEFAULT),
                      Power(u_, m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          Power(ExpandToSum(u, x),
                              m),
                          Power(Plus(a, Times(b, Coth(ExpandToSum(v, x)))), n)),
                      x),
                  And(FreeQ(List(a, b, m,
                      n), x), LinearQ(List(u, v),
                          x),
                      Not(LinearMatchQ(List(u, v), x))))),
          IIntegrate(5400,
              Integrate(Power(
                  Plus(a_DEFAULT,
                      Times(b_DEFAULT, Tanh(Plus(c_DEFAULT, Times(d_DEFAULT, Power(x_, n_)))))),
                  p_DEFAULT), x_Symbol),
              Condition(
                  Dist(
                      Power(n, CN1),
                      Subst(
                          Integrate(Times(Power(x, Subtract(Power(n, CN1), C1)),
                              Power(Plus(a, Times(b, Tanh(Plus(c, Times(d, x))))), p)), x),
                          x, Power(x, n)),
                      x),
                  And(FreeQ(List(a, b, c, d, p), x), IGtQ(Power(n, CN1), C0), IntegerQ(p)))));
}
