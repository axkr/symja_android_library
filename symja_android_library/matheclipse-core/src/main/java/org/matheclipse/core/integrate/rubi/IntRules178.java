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
class IntRules178 {
  public static IAST RULES =
      List(
          IIntegrate(3561,
              Integrate(
                  Times(
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§tan"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Power(
                          Plus(c_DEFAULT,
                              Times(d_DEFAULT,
                                  $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              d, Power(Plus(a, Times(b, Tan(
                                  Plus(e, Times(f, x))))), m),
                              Power(Plus(c, Times(d, Tan(Plus(e, Times(f, x))))), Plus(n, C1)),
                              Power(Times(f, Plus(n, C1), Plus(Sqr(c), Sqr(d))), CN1)),
                          x),
                      Dist(
                          Power(Times(a, Plus(Sqr(c), Sqr(d)),
                              Plus(n, C1)), CN1),
                          Integrate(
                              Times(Power(Plus(a, Times(b, Tan(Plus(e, Times(f, x))))), m),
                                  Power(Plus(c, Times(d, Tan(Plus(e, Times(f, x))))), Plus(n, C1)),
                                  Simp(
                                      Plus(Times(b, d,
                                          m), Times(CN1, a, c, Plus(n, C1)),
                                          Times(a, d, Plus(m, n, C1), Tan(Plus(e, Times(f, x))))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, m), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0), EqQ(Plus(Sqr(a), Sqr(b)), C0),
                      NeQ(Plus(Sqr(c),
                          Sqr(d)), C0),
                      LtQ(n, CN1), Or(IntegerQ(n), IntegersQ(Times(C2, m), Times(C2, n)))))),
          IIntegrate(3562,
              Integrate(
                  Times(
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§tan"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Power(
                          Plus(c_DEFAULT,
                              Times(d_DEFAULT,
                                  $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Times(a, Power(Subtract(Times(a, c), Times(b, d)), CN1)),
                          Integrate(Power(Plus(a, Times(b, Tan(Plus(e, Times(f, x))))), m), x), x),
                      Dist(Times(d, Power(Subtract(Times(a, c), Times(b, d)), CN1)),
                          Integrate(Times(Power(Plus(a, Times(b, Tan(Plus(e, Times(f, x))))), m),
                              Plus(b, Times(a, Tan(Plus(e, Times(f, x))))), Power(
                                  Plus(c, Times(d, Tan(Plus(e, Times(f, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, m), x),
                      NeQ(Subtract(Times(b, c), Times(a,
                          d)), C0),
                      EqQ(Plus(Sqr(a), Sqr(b)), C0), NeQ(Plus(Sqr(c), Sqr(d)), C0)))),
          IIntegrate(3563,
              Integrate(
                  Times(
                      Sqrt(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))))),
                      Sqrt(
                          Plus(c_DEFAULT,
                              Times(
                                  d_DEFAULT, $($s("§tan"), Plus(e_DEFAULT,
                                      Times(f_DEFAULT, x_))))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(
                          Times(Subtract(Times(a, c), Times(b,
                              d)), Power(a,
                                  CN1)),
                          Integrate(
                              Times(Sqrt(Plus(a, Times(b, Tan(Plus(e, Times(f, x)))))),
                                  Power(Plus(c, Times(d, Tan(Plus(e, Times(f, x))))), CN1D2)),
                              x),
                          x),
                      Dist(Times(d, Power(a, CN1)),
                          Integrate(
                              Times(Sqrt(Plus(a, Times(b, Tan(Plus(e, Times(f, x)))))),
                                  Plus(b, Times(a, Tan(Plus(e, Times(f, x))))),
                                  Power(Plus(c, Times(d, Tan(Plus(e, Times(f, x))))), CN1D2)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), NeQ(Subtract(Times(b, c), Times(a,
                      d)), C0), EqQ(Plus(Sqr(a),
                          Sqr(b)), C0),
                      NeQ(Plus(Sqr(c), Sqr(d)), C0)))),
          IIntegrate(3564,
              Integrate(
                  Times(
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§tan"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Power(
                          Plus(c_DEFAULT,
                              Times(d_DEFAULT,
                                  $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Dist(Times(a, b, Power(f, CN1)),
                      Subst(
                          Integrate(Times(Power(Plus(a, x), Subtract(m, C1)),
                              Power(Plus(c, Times(d, x, Power(b, CN1))),
                                  n),
                              Power(Plus(Sqr(b), Times(a, x)), CN1)), x),
                          x, Times(b, Tan(Plus(e, Times(f, x))))),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0), EqQ(Plus(Sqr(a),
                          Sqr(b)), C0),
                      NeQ(Plus(Sqr(c), Sqr(d)), C0)))),
          IIntegrate(3565,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT, $($s("§tan"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Power(
                          Plus(c_DEFAULT,
                              Times(d_DEFAULT,
                                  $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(Times(Sqr(Subtract(Times(b, c), Times(a, d))),
                          Power(Plus(a, Times(b, Tan(Plus(e, Times(f, x))))), Subtract(m, C2)),
                          Power(Plus(c, Times(d, Tan(Plus(e, Times(f, x))))), Plus(n, C1)),
                          Power(Times(d, f, Plus(n, C1), Plus(Sqr(c), Sqr(d))), CN1)), x),
                      Dist(
                          Power(Times(d, Plus(n, C1),
                              Plus(Sqr(c), Sqr(d))), CN1),
                          Integrate(
                              Times(
                                  Power(
                                      Plus(a, Times(b, Tan(Plus(e, Times(f, x))))),
                                      Subtract(m, C3)),
                                  Power(Plus(c, Times(d, Tan(Plus(e, Times(f, x))))), Plus(n, C1)),
                                  Simp(Subtract(
                                      Subtract(
                                          Plus(
                                              Times(Sqr(a), d,
                                                  Subtract(Times(b, d, Subtract(m, C2)),
                                                      Times(a, c, Plus(n, C1)))),
                                              Times(b, Subtract(Times(b, c), Times(C2, a, d)),
                                                  Plus(Times(b, c, Subtract(m, C2)),
                                                      Times(a, d, Plus(n, C1))))),
                                          Times(d, Plus(n, C1), Plus(
                                              Times(C3, Sqr(a), b, c), Times(CN1, Power(b, C3), c),
                                              Times(CN1, Power(a, C3), d), Times(C3, a, Sqr(b), d)),
                                              Tan(Plus(e, Times(f, x))))),
                                      Times(b,
                                          Subtract(
                                              Times(a, d, Subtract(Times(C2, b, c), Times(a, d)),
                                                  Subtract(Plus(m, n), C1)),
                                              Times(Sqr(b),
                                                  Subtract(Times(Sqr(c), Subtract(m, C2)),
                                                      Times(Sqr(d), Plus(n, C1))))),
                                          Sqr(Tan(Plus(e, Times(f, x)))))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      NeQ(Plus(Sqr(a), Sqr(b)), C0), NeQ(Plus(Sqr(c),
                          Sqr(d)), C0),
                      GtQ(m, C2), LtQ(n, CN1), IntegerQ(Times(C2, m))))),
          IIntegrate(3566,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT, $($s("§tan"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Power(Plus(c_DEFAULT,
                          Times(d_DEFAULT, $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Sqr(b),
                              Power(Plus(a, Times(b,
                                  Tan(Plus(e, Times(f, x))))), Subtract(m,
                                      C2)),
                              Power(Plus(c, Times(d, Tan(Plus(e, Times(f, x))))), Plus(n,
                                  C1)),
                              Power(Times(d, f, Subtract(Plus(m, n), C1)), CN1)),
                          x),
                      Dist(Power(Times(d, Subtract(Plus(m, n), C1)), CN1),
                          Integrate(Times(
                              Power(Plus(a, Times(b, Tan(Plus(e, Times(f, x))))), Subtract(m, C3)),
                              Power(Plus(c, Times(d, Tan(Plus(e, Times(f, x))))), n),
                              Simp(Subtract(
                                  Plus(Times(Power(a, C3), d, Subtract(Plus(m, n), C1)),
                                      Times(CN1, Sqr(b),
                                          Plus(Times(b, c, Subtract(m, C2)),
                                              Times(a, d, Plus(C1, n)))),
                                      Times(b, d, Subtract(Plus(m, n), C1),
                                          Subtract(Times(C3, Sqr(a)), Sqr(b)),
                                          Tan(Plus(e, Times(f, x))))),
                                  Times(Sqr(b),
                                      Subtract(Times(b, c, Subtract(m, C2)),
                                          Times(a, d,
                                              Subtract(Plus(Times(C3, m), Times(C2, n)), C4))),
                                      Sqr(Tan(Plus(e, Times(f, x)))))),
                                  x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, n), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0), NeQ(Plus(Sqr(a), Sqr(b)), C0),
                      NeQ(Plus(Sqr(c), Sqr(d)), C0), IntegerQ(Times(C2, m)), GtQ(m, C2),
                      Or(GeQ(n,
                          CN1), IntegerQ(m)),
                      Not(And(IGtQ(n, C2), Or(Not(IntegerQ(m)), And(EqQ(c, C0), NeQ(a, C0)))))))),
          IIntegrate(3567,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT, $($s("§tan"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Power(
                          Plus(c_DEFAULT, Times(d_DEFAULT, $($s("§tan"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Subtract(Times(b, c), Times(a, d)),
                              Power(Plus(a, Times(b, Tan(Plus(e, Times(f, x))))), Plus(m,
                                  C1)),
                              Power(Plus(c, Times(d,
                                  Tan(Plus(e, Times(f, x))))), Subtract(n,
                                      C1)),
                              Power(Times(f, Plus(m, C1), Plus(Sqr(a), Sqr(b))), CN1)),
                          x),
                      Dist(
                          Power(Times(Plus(m, C1), Plus(Sqr(a), Sqr(b))), CN1), Integrate(
                              Times(
                                  Power(Plus(a, Times(b,
                                      Tan(Plus(e, Times(f, x))))), Plus(m,
                                          C1)),
                                  Power(
                                      Plus(c, Times(d,
                                          Tan(Plus(e, Times(f, x))))),
                                      Subtract(n, C2)),
                                  Simp(
                                      Subtract(
                                          Subtract(
                                              Plus(Times(a, Sqr(c), Plus(m, C1)),
                                                  Times(a, Sqr(d), Subtract(n, C1)),
                                                  Times(b, c, d, Plus(m, Negate(n), C2))),
                                              Times(
                                                  Subtract(Subtract(Times(b, Sqr(c)),
                                                      Times(C2, a, c, d)), Times(b, Sqr(d))),
                                                  Plus(m, C1), Tan(Plus(e, Times(f, x))))),
                                          Times(d, Subtract(Times(b, c), Times(a, d)), Plus(m, n),
                                              Sqr(Tan(Plus(e, Times(f, x)))))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      NeQ(Plus(Sqr(a), Sqr(b)), C0), NeQ(Plus(Sqr(c),
                          Sqr(d)), C0),
                      LtQ(m, CN1), LtQ(C1, n, C2), IntegerQ(Times(C2, m))))),
          IIntegrate(3568,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT, $($s("§tan"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Power(Plus(
                          c_DEFAULT,
                          Times(d_DEFAULT, $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(b, Power(Plus(a, Times(b, Tan(Plus(e, Times(f, x))))), Plus(m, C1)),
                              Power(Plus(c, Times(d, Tan(Plus(e, Times(f, x))))), n), Power(
                                  Times(f, Plus(m, C1), Plus(Sqr(a), Sqr(b))), CN1)),
                          x),
                      Dist(Power(Times(Plus(m, C1), Plus(Sqr(a), Sqr(b))), CN1),
                          Integrate(Times(
                              Power(Plus(a, Times(b, Tan(Plus(e, Times(f, x))))), Plus(m, C1)),
                              Power(Plus(c, Times(d, Tan(Plus(e, Times(f, x))))), Subtract(n, C1)),
                              Simp(
                                  Subtract(
                                      Subtract(Subtract(Times(a, c, Plus(m, C1)), Times(b, d, n)),
                                          Times(Subtract(Times(b, c), Times(a, d)), Plus(m, C1),
                                              Tan(Plus(e, Times(f, x))))),
                                      Times(b, d, Plus(m, n, C1), Sqr(Tan(Plus(e, Times(f, x)))))),
                                  x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      NeQ(Plus(Sqr(a), Sqr(
                          b)), C0),
                      NeQ(Plus(Sqr(c),
                          Sqr(d)), C0),
                      LtQ(m, CN1), GtQ(n, C0), IntegerQ(Times(C2, m))))),
          IIntegrate(3569,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT, $($s("§tan"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Power(
                          Plus(c_DEFAULT,
                              Times(d_DEFAULT,
                                  $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Sqr(b),
                              Power(Plus(a, Times(b,
                                  Tan(Plus(e, Times(f, x))))), Plus(m,
                                      C1)),
                              Power(Plus(c, Times(d,
                                  Tan(Plus(e, Times(f, x))))), Plus(n,
                                      C1)),
                              Power(
                                  Times(
                                      f, Plus(m, C1), Plus(Sqr(a), Sqr(
                                          b)),
                                      Subtract(Times(b, c), Times(a, d))),
                                  CN1)),
                          x),
                      Dist(
                          Power(
                              Times(
                                  Plus(m, C1), Plus(Sqr(
                                      a), Sqr(b)),
                                  Subtract(Times(b, c), Times(a, d))),
                              CN1),
                          Integrate(Times(
                              Power(Plus(a, Times(b, Tan(Plus(e, Times(f, x))))), Plus(m, C1)),
                              Power(Plus(c, Times(d, Tan(Plus(e, Times(f, x))))), n),
                              Simp(
                                  Subtract(
                                      Subtract(
                                          Subtract(Times(a, Subtract(Times(b, c), Times(a, d)),
                                              Plus(m, C1)), Times(Sqr(b), d, Plus(m, n, C2))),
                                          Times(b, Subtract(Times(b, c), Times(a, d)), Plus(m, C1),
                                              Tan(Plus(e, Times(f, x))))),
                                      Times(Sqr(b), d, Plus(m, n, C2),
                                          Sqr(Tan(Plus(e, Times(f, x)))))),
                                  x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, n), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0), NeQ(Plus(Sqr(a), Sqr(b)), C0),
                      NeQ(Plus(Sqr(c), Sqr(
                          d)), C0),
                      IntegerQ(Times(C2, m)), LtQ(m, CN1), Or(LtQ(n,
                          C0), IntegerQ(m)),
                      Not(And(ILtQ(n, CN1), Or(Not(IntegerQ(m)), And(EqQ(c, C0), NeQ(a, C0)))))))),
          IIntegrate(3570,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT, $($s("§tan"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Power(
                          Plus(c_DEFAULT, Times(d_DEFAULT, $($s("§tan"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(b,
                              Power(Plus(a, Times(b,
                                  Tan(Plus(e, Times(f, x))))), Subtract(m, C1)),
                              Power(Plus(c, Times(d, Tan(Plus(e, Times(f, x))))),
                                  n),
                              Power(Times(f, Subtract(Plus(m, n), C1)), CN1)),
                          x),
                      Dist(
                          Power(Subtract(Plus(m, n),
                              C1), CN1),
                          Integrate(
                              Times(
                                  Power(
                                      Plus(a, Times(b,
                                          Tan(Plus(e, Times(f, x))))),
                                      Subtract(m, C2)),
                                  Power(Plus(c, Times(d, Tan(Plus(e, Times(f, x))))),
                                      Subtract(n, C1)),
                                  Simp(Plus(Times(Sqr(a), c, Subtract(Plus(m, n), C1)),
                                      Times(CN1, b,
                                          Plus(Times(b, c, Subtract(m, C1)), Times(a, d, n))),
                                      Times(
                                          Subtract(Plus(Times(C2, a, b, c), Times(Sqr(a), d)),
                                              Times(Sqr(b), d)),
                                          Subtract(Plus(m, n), C1), Tan(Plus(e, Times(f, x)))),
                                      Times(b,
                                          Plus(Times(b, c, n),
                                              Times(a, d, Subtract(Plus(Times(C2, m), n), C2))),
                                          Sqr(Tan(Plus(e, Times(f, x)))))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      NeQ(Plus(Sqr(a), Sqr(b)), C0), NeQ(Plus(Sqr(c),
                          Sqr(d)), C0),
                      GtQ(m, C1), GtQ(n, C0), IntegerQ(Times(C2, n))))),
          IIntegrate(3571,
              Integrate(
                  Times(
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§tan"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          CN1),
                      Power(
                          Plus(c_DEFAULT,
                              Times(d_DEFAULT, $($s("§tan"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(Times(Subtract(Times(a, c), Times(b, d)), x,
                          Power(Times(Plus(Sqr(a), Sqr(b)), Plus(Sqr(c), Sqr(d))), CN1)), x),
                      Dist(
                          Times(Sqr(b),
                              Power(
                                  Times(Subtract(Times(b, c), Times(a, d)),
                                      Plus(Sqr(a), Sqr(b))),
                                  CN1)),
                          Integrate(
                              Times(
                                  Subtract(b, Times(a, Tan(Plus(e, Times(f, x))))), Power(Plus(a,
                                      Times(b, Tan(Plus(e, Times(f, x))))), CN1)),
                              x),
                          x),
                      Negate(
                          Dist(
                              Times(
                                  Sqr(d),
                                  Power(Times(
                                      Subtract(Times(b, c), Times(a, d)), Plus(Sqr(c), Sqr(d))),
                                      CN1)),
                              Integrate(Times(
                                  Subtract(d, Times(c, Tan(Plus(e, Times(f, x))))), Power(Plus(c,
                                      Times(d, Tan(Plus(e, Times(f, x))))), CN1)),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, f), x), NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      NeQ(Plus(Sqr(a), Sqr(b)), C0), NeQ(Plus(Sqr(c), Sqr(d)), C0)))),
          IIntegrate(3572,
              Integrate(
                  Times(
                      Sqrt(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT,
                                  $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))))),
                      Power(
                          Plus(c_DEFAULT,
                              Times(d_DEFAULT, $($s("§tan"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(
                          Power(Plus(Sqr(c),
                              Sqr(d)), CN1),
                          Integrate(
                              Times(
                                  Simp(
                                      Plus(Times(a, c), Times(b, d),
                                          Times(Subtract(Times(b, c), Times(a, d)),
                                              Tan(Plus(e, Times(f, x))))),
                                      x),
                                  Power(Plus(a, Times(b, Tan(Plus(e, Times(f, x))))), CN1D2)),
                              x),
                          x),
                      Dist(
                          Times(
                              d, Subtract(Times(b, c), Times(a,
                                  d)),
                              Power(Plus(Sqr(c), Sqr(d)), CN1)),
                          Integrate(
                              Times(Plus(C1, Sqr(Tan(Plus(e, Times(f, x))))),
                                  Power(Times(Sqrt(Plus(a, Times(b, Tan(Plus(e, Times(f, x)))))),
                                      Plus(c, Times(d, Tan(Plus(e, Times(f, x)))))),
                                      CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d), x), NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      NeQ(Plus(Sqr(a), Sqr(b)), C0), NeQ(Plus(Sqr(c), Sqr(d)), C0)))),
          IIntegrate(3573,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT,
                                  $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          QQ(3L, 2L)),
                      Power(
                          Plus(c_DEFAULT,
                              Times(d_DEFAULT,
                                  $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(
                          Power(Plus(Sqr(c),
                              Sqr(d)), CN1),
                          Integrate(
                              Times(
                                  Simp(
                                      Plus(Times(Sqr(a), c), Times(CN1, Sqr(b), c),
                                          Times(C2, a, b, d),
                                          Times(Plus(Times(C2, a, b, c), Times(CN1, Sqr(a), d),
                                              Times(Sqr(b), d)),
                                              Tan(Plus(e, Times(f, x))))),
                                      x),
                                  Power(Plus(a, Times(b, Tan(Plus(e, Times(f, x))))), CN1D2)),
                              x),
                          x),
                      Dist(
                          Times(
                              Sqr(Subtract(Times(b, c),
                                  Times(a, d))),
                              Power(Plus(Sqr(c), Sqr(d)), CN1)),
                          Integrate(
                              Times(Plus(C1, Sqr(Tan(Plus(e, Times(f, x))))),
                                  Power(Times(Sqrt(Plus(a, Times(b, Tan(Plus(e, Times(f, x)))))),
                                      Plus(c, Times(d, Tan(Plus(e, Times(f, x)))))),
                                      CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), NeQ(Subtract(Times(b, c), Times(a,
                      d)), C0), NeQ(Plus(Sqr(a),
                          Sqr(b)), C0),
                      NeQ(Plus(Sqr(c), Sqr(d)), C0)))),
          IIntegrate(3574,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT, $($s("§tan"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Power(
                          Plus(c_DEFAULT,
                              Times(d_DEFAULT, $($s("§tan"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(Power(Plus(Sqr(c), Sqr(d)), CN1),
                          Integrate(
                              Times(
                                  Power(Plus(a,
                                      Times(b, Tan(Plus(e, Times(f, x))))), m),
                                  Subtract(c, Times(d, Tan(Plus(e, Times(f, x)))))),
                              x),
                          x),
                      Dist(
                          Times(Sqr(d), Power(Plus(Sqr(c), Sqr(d)),
                              CN1)),
                          Integrate(Times(Power(Plus(a, Times(b, Tan(Plus(e, Times(f, x))))), m),
                              Plus(C1, Sqr(Tan(Plus(e, Times(f, x))))), Power(
                                  Plus(c, Times(d, Tan(Plus(e, Times(f, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, m), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0), NeQ(Plus(Sqr(a), Sqr(b)), C0),
                      NeQ(Plus(Sqr(c), Sqr(d)), C0), Not(IntegerQ(m))))),
          IIntegrate(3575,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT, $($s("§tan"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Power(
                          Plus(c_,
                              Times(d_DEFAULT,
                                  $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set($s("ff"),
                          FreeFactors(Tan(Plus(e, Times(f, x))), x))),
                      Dist(Times($s("ff"), Power(f, CN1)),
                          Subst(
                              Integrate(Times(Power(Plus(a, Times(b, $s("ff"), x)), m),
                                  Power(Plus(c, Times(d, $s("ff"), x)), n), Power(Plus(C1,
                                      Times(Sqr($s("ff")), Sqr(x))), CN1)),
                                  x),
                              x, Times(Tan(Plus(e, Times(f, x))), Power($s("ff"), CN1))),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, m, n), x),
                      NeQ(Subtract(Times(b, c), Times(a,
                          d)), C0),
                      NeQ(Plus(Sqr(a), Sqr(b)), C0), NeQ(Plus(Sqr(c), Sqr(d)), C0)))),
          IIntegrate(3576,
              Integrate(
                  Times(
                      Power(
                          Times(
                              d_DEFAULT, Power($($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  CN1)),
                          n_),
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT,
                                  $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(d, m),
                      Integrate(
                          Times(
                              Power(Plus(b, Times(a, Cot(Plus(e, Times(f, x))))), m), Power(Times(d,
                                  Cot(Plus(e, Times(f, x)))), Subtract(n, m))),
                          x),
                      x),
                  And(FreeQ(List(a, b, d, e, f, n), x), Not(IntegerQ(n)), IntegerQ(m)))),
          IIntegrate(3577,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times($($s("§cot"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT)),
                          m_DEFAULT),
                      Power(Times(Power($($s("§cot"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), CN1),
                          d_DEFAULT), n_)),
                  x_Symbol),
              Condition(
                  Dist(Power(d, m),
                      Integrate(
                          Times(
                              Power(Plus(b, Times(a,
                                  Tan(Plus(e, Times(f, x))))), m),
                              Power(Times(d, Tan(Plus(e, Times(f, x)))), Subtract(n, m))),
                          x),
                      x),
                  And(FreeQ(List(a, b, d, e, f, n), x), Not(IntegerQ(n)), IntegerQ(m)))),
          IIntegrate(3578,
              Integrate(
                  Times(
                      Power(
                          Times(c_DEFAULT,
                              Power(
                                  Times(d_DEFAULT,
                                      $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                                  p_)),
                          n_),
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT, $($s("§tan"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(c, IntPart(
                              n)),
                          Power(Times(c,
                              Power(Times(d, Tan(Plus(e, Times(f, x)))), p)), FracPart(n)),
                          Power(
                              Power(Times(d, Tan(Plus(e, Times(f, x)))),
                                  Times(p, FracPart(n))),
                              CN1)),
                      Integrate(
                          Times(
                              Power(Plus(a, Times(b,
                                  Tan(Plus(e, Times(f, x))))), m),
                              Power(Times(d, Tan(Plus(e, Times(f, x)))), Times(n, p))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n, p), x), Not(IntegerQ(n)),
                      Not(IntegerQ(m))))),
          IIntegrate(3579,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times($($s("§cot"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT)),
                          m_DEFAULT),
                      Power(
                          Times(c_DEFAULT,
                              Power(
                                  Times($($s("§cot"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                      d_DEFAULT),
                                  p_)),
                          n_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(c, IntPart(
                              n)),
                          Power(Times(c, Power(Times(d, Cot(Plus(e, Times(f, x)))), p)),
                              FracPart(n)),
                          Power(
                              Power(Times(d, Cot(Plus(e, Times(f, x)))),
                                  Times(p, FracPart(n))),
                              CN1)),
                      Integrate(
                          Times(Power(Plus(a, Times(b, Cot(Plus(e, Times(f, x))))), m),
                              Power(Times(d, Cot(Plus(e, Times(f, x)))), Times(n, p))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n, p), x), Not(IntegerQ(n)),
                      Not(IntegerQ(m))))),
          IIntegrate(3580,
              Integrate(
                  Times(
                      Power(
                          Times(g_DEFAULT, $($s("§tan"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          p_DEFAULT),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§tan"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Power(Plus(c_,
                          Times(d_DEFAULT, $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Unintegrable(Times(Power(Times(g, Tan(Plus(e, Times(f, x)))), p),
                      Power(Plus(a, Times(b, Tan(Plus(e, Times(f, x))))), m),
                      Power(Plus(c, Times(d, Tan(Plus(e, Times(f, x))))), n)), x),
                  FreeQ(List(a, b, c, d, e, f, g, m, n, p), x))));
}
