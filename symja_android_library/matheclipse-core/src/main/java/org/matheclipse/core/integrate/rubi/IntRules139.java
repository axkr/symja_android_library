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
class IntRules139 {
  public static IAST RULES =
      List(
          IIntegrate(2781,
              Integrate(
                  Times(
                      Power(
                          Times(d_DEFAULT,
                              $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          CN1D2),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          CN1D2)),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(
                          Times(CSqrt2, Power(Times(Sqrt(a), f),
                              CN1)),
                          Subst(
                              Integrate(Power(Subtract(C1, Sqr(x)),
                                  CN1D2), x),
                              x,
                              Times(b, Cos(Plus(e, Times(f, x))), Power(
                                  Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), CN1))),
                          x)),
                  And(FreeQ(List(a, b, d, e, f), x), EqQ(Subtract(Sqr(a), Sqr(
                      b)), C0), EqQ(d, Times(a,
                          Power(b, CN1))),
                      GtQ(a, C0)))),
          IIntegrate(2782,
              Integrate(
                  Times(
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          CN1D2),
                      Power(
                          Plus(c_DEFAULT,
                              Times(d_DEFAULT, $($s("§sin"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          CN1D2)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(CN2, a, Power(f,
                          CN1)),
                      Subst(
                          Integrate(
                              Power(Subtract(Times(C2, Sqr(b)),
                                  Times(Subtract(Times(a, c), Times(b, d)), Sqr(x))),
                                  CN1),
                              x),
                          x,
                          Times(b, Cos(Plus(e, Times(f, x))),
                              Power(
                                  Times(
                                      Sqrt(Plus(a, Times(b, Sin(Plus(e, Times(f, x)))))),
                                      Sqrt(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))))),
                                  CN1))),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f), x), NeQ(Subtract(Times(b, c), Times(a,
                      d)), C0), EqQ(Subtract(Sqr(a),
                          Sqr(b)), C0),
                      NeQ(Subtract(Sqr(c), Sqr(d)), C0)))),
          IIntegrate(2783,
              Integrate(
                  Times(
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Power(
                          Plus(c_DEFAULT,
                              Times(d_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(d, Cos(Plus(e, Times(f, x))),
                                  Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), m),
                                  Power(
                                      Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), Subtract(n,
                                          C1)),
                                  Power(Times(f, Plus(m, n)), CN1)),
                              x)),
                      Dist(
                          Power(Times(b,
                              Plus(m, n)), CN1),
                          Integrate(
                              Times(
                                  Power(Plus(a,
                                      Times(b, Sin(Plus(e, Times(f, x))))), m),
                                  Power(
                                      Plus(c, Times(d,
                                          Sin(Plus(e, Times(f, x))))),
                                      Subtract(n, C2)),
                                  Simp(
                                      Plus(
                                          Times(d,
                                              Plus(Times(a, c, m), Times(b, d, Subtract(n, C1)))),
                                          Times(b, Sqr(c), Plus(m, n)),
                                          Times(d,
                                              Plus(Times(a, d, m),
                                                  Times(b, c, Subtract(Plus(m, Times(C2, n)), C1))),
                                              Sin(Plus(e, Times(f, x))))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, m), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      EqQ(Subtract(Sqr(a), Sqr(b)), C0), NeQ(Subtract(Sqr(c),
                          Sqr(d)), C0),
                      GtQ(n, C1), IntegerQ(n)))),
          IIntegrate(2784,
              Integrate(
                  Times(
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Power(
                          Plus(c_DEFAULT,
                              Times(d_DEFAULT, $($s("§sin"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(a, m), Cos(Plus(e, Times(f, x))),
                          Power(
                              Times(f, Sqrt(Plus(C1, Sin(Plus(e, Times(f, x))))),
                                  Sqrt(Subtract(C1, Sin(Plus(e, Times(f, x)))))),
                              CN1)),
                      Subst(
                          Integrate(
                              Times(Power(Plus(C1, Times(b, x, Power(a, CN1))), Subtract(m, C1D2)),
                                  Power(Plus(c, Times(d, x)), n), Power(
                                      Subtract(C1, Times(b, x, Power(a, CN1))), CN1D2)),
                              x),
                          x, Sin(Plus(e, Times(f, x)))),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, n), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      EqQ(Subtract(Sqr(a),
                          Sqr(b)), C0),
                      NeQ(Subtract(Sqr(c), Sqr(d)), C0), IntegerQ(m)))),
          IIntegrate(2785,
              Integrate(
                  Times(
                      Power(
                          Times(d_DEFAULT,
                              $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          n_),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          m_)),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(
                          Times(b, Power(Times(d, Power(b, CN1)), n), Cos(Plus(e, Times(f, x))),
                              Power(
                                  Times(f, Sqrt(Plus(a, Times(b, Sin(Plus(e, Times(f, x)))))),
                                      Sqrt(Subtract(a, Times(b, Sin(Plus(e, Times(f, x))))))),
                                  CN1)),
                          Subst(
                              Integrate(Times(Power(Subtract(a, x), n),
                                  Power(Subtract(Times(C2, a), x), Subtract(m, C1D2)),
                                  Power(x, CN1D2)), x),
                              x, Subtract(a, Times(b, Sin(Plus(e, Times(f, x)))))),
                          x)),
                  And(FreeQ(List(a, b, d, e, f, m, n), x), EqQ(Subtract(Sqr(a), Sqr(
                      b)), C0), Not(IntegerQ(
                          m)),
                      GtQ(a, C0), GtQ(Times(d, Power(b, CN1)), C0)))),
          IIntegrate(2786,
              Integrate(
                  Times(
                      Power(
                          Times(d_DEFAULT, $($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          n_DEFAULT),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          m_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Times(d, Power(b, CN1)), IntPart(n)),
                          Power(Times(d, Sin(Plus(e, Times(f, x)))), FracPart(n)), Power(Power(
                              Times(b, Sin(Plus(e, Times(f, x)))), FracPart(n)), CN1)),
                      Integrate(
                          Times(
                              Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))),
                                  m),
                              Power(Times(b, Sin(Plus(e, Times(f, x)))), n)),
                          x),
                      x),
                  And(FreeQ(List(a, b, d, e, f, m, n), x), EqQ(Subtract(Sqr(a), Sqr(
                      b)), C0), Not(IntegerQ(
                          m)),
                      GtQ(a, C0), Not(GtQ(Times(d, Power(b, CN1)), C0))))),
          IIntegrate(2787,
              Integrate(
                  Times(
                      Power(
                          Times(d_DEFAULT, $($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          n_DEFAULT),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(a, IntPart(m)),
                          Power(Plus(a, Times(b,
                              Sin(Plus(e, Times(f, x))))), FracPart(
                                  m)),
                          Power(
                              Power(Plus(C1, Times(b, Sin(Plus(e, Times(f, x))), Power(a, CN1))),
                                  FracPart(m)),
                              CN1)),
                      Integrate(
                          Times(
                              Power(Plus(C1,
                                  Times(b, Sin(Plus(e, Times(f, x))), Power(a, CN1))), m),
                              Power(Times(d, Sin(Plus(e, Times(f, x)))), n)),
                          x),
                      x),
                  And(FreeQ(List(a, b, d, e, f, m, n), x), EqQ(Subtract(Sqr(a), Sqr(b)), C0),
                      Not(IntegerQ(m)), Not(GtQ(a, C0))))),
          IIntegrate(2788,
              Integrate(
                  Times(
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Power(
                          Plus(c_,
                              Times(d_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Sqr(a), Cos(Plus(e, Times(f, x))),
                          Power(
                              Times(f, Sqrt(Plus(a, Times(b, Sin(Plus(e, Times(f, x)))))),
                                  Sqrt(Subtract(a, Times(b, Sin(Plus(e, Times(f, x))))))),
                              CN1)),
                      Subst(
                          Integrate(Times(Power(Plus(a, Times(b, x)), Subtract(m, C1D2)),
                              Power(Plus(c, Times(d, x)), n), Power(Subtract(a, Times(b, x)),
                                  CN1D2)),
                              x),
                          x, Sin(Plus(e, Times(f, x)))),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      EqQ(Subtract(Sqr(a),
                          Sqr(b)), C0),
                      NeQ(Subtract(Sqr(c), Sqr(d)), C0), Not(IntegerQ(m))))),
          IIntegrate(2789,
              Integrate(
                  Times(
                      Power(
                          Times(b_DEFAULT,
                              $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          m_),
                      Sqr(Plus(
                          c_, Times(d_DEFAULT, $($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_))))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(
                          Times(C2, c, d, Power(b, CN1)), Integrate(Power(
                              Times(b, Sin(Plus(e, Times(f, x)))), Plus(m, C1)), x),
                          x),
                      Integrate(
                          Times(
                              Power(Times(b,
                                  Sin(Plus(e, Times(f, x)))), m),
                              Plus(Sqr(c), Times(Sqr(d), Sqr(Sin(Plus(e, Times(f, x))))))),
                          x)),
                  FreeQ(List(b, c, d, e, f, m), x))),
          IIntegrate(2790,
              Integrate(
                  Times(
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Sqr(Plus(c_DEFAULT, Times(d_DEFAULT, $($s("§sin"),
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_))))))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Negate(
                          Simp(
                              Times(
                                  Plus(
                                      Times(Sqr(b), Sqr(c)), Times(CN1, C2, a, b, c, d),
                                      Times(Sqr(a), Sqr(d))),
                                  Cos(Plus(e, Times(f, x))),
                                  Power(Plus(a, Times(b,
                                      Sin(Plus(e, Times(f, x))))), Plus(m, C1)),
                                  Power(Times(b, f, Plus(m, C1), Subtract(Sqr(a), Sqr(b))), CN1)),
                              x)),
                      Dist(
                          Power(Times(b, Plus(m, C1),
                              Subtract(Sqr(a), Sqr(b))), CN1),
                          Integrate(
                              Times(
                                  Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Plus(m, C1)),
                                  Simp(Plus(
                                      Times(b, Plus(m, C1),
                                          Subtract(Times(C2, b, c, d),
                                              Times(a, Plus(Sqr(c), Sqr(d))))),
                                      Times(
                                          Plus(Times(Sqr(a), Sqr(d)),
                                              Times(CN1, C2, a, b, c, d, Plus(m, C2)),
                                              Times(Sqr(b),
                                                  Plus(Times(Sqr(d), Plus(m, C1)),
                                                      Times(Sqr(c), Plus(m, C2))))),
                                          Sin(Plus(e, Times(f, x))))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), NeQ(Subtract(Times(b, c),
                      Times(a, d)), C0), NeQ(Subtract(Sqr(a), Sqr(b)),
                          C0),
                      LtQ(m, CN1)))),
          IIntegrate(2791,
              Integrate(
                  Times(
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Sqr(Plus(c_DEFAULT, Times(d_DEFAULT, $($s("§sin"),
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_))))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(
                                  Sqr(d), Cos(Plus(e,
                                      Times(f, x))),
                                  Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Plus(m,
                                      C1)),
                                  Power(Times(b, f, Plus(m, C2)), CN1)),
                              x)),
                      Dist(
                          Power(Times(b,
                              Plus(m, C2)), CN1),
                          Integrate(
                              Times(Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), m),
                                  Simp(Subtract(
                                      Times(b,
                                          Plus(Times(Sqr(d), Plus(m, C1)),
                                              Times(Sqr(c), Plus(m, C2)))),
                                      Times(d, Subtract(Times(a, d), Times(C2, b, c, Plus(m, C2))),
                                          Sin(Plus(e, Times(f, x))))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, m), x),
                      NeQ(Subtract(Times(b, c), Times(a,
                          d)), C0),
                      NeQ(Subtract(Sqr(a), Sqr(b)), C0), Not(LtQ(m, CN1))))),
          IIntegrate(2792,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT, $($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Power(
                          Plus(c_DEFAULT, Times(d_DEFAULT, $($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(
                                  Plus(Times(Sqr(b),
                                      Sqr(c)), Times(CN1, C2, a, b, c, d), Times(Sqr(a), Sqr(d))),
                                  Cos(Plus(e, Times(f, x))),
                                  Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Subtract(m,
                                      C2)),
                                  Power(Plus(c, Times(d,
                                      Sin(Plus(e, Times(f, x))))), Plus(n,
                                          C1)),
                                  Power(Times(d, f, Plus(n, C1), Subtract(Sqr(c), Sqr(d))), CN1)),
                              x)),
                      Dist(
                          Power(Times(d, Plus(n, C1),
                              Subtract(Sqr(c), Sqr(d))), CN1),
                          Integrate(
                              Times(
                                  Power(
                                      Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Subtract(m,
                                          C3)),
                                  Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), Plus(n, C1)),
                                  Simp(
                                      Plus(
                                          Times(
                                              b, Subtract(m, C2),
                                              Sqr(Subtract(Times(b, c), Times(a, d)))),
                                          Times(a, d, Plus(n, C1),
                                              Subtract(
                                                  Times(c, Plus(Sqr(a), Sqr(b))), Times(C2, a, b,
                                                      d))),
                                          Times(
                                              Subtract(
                                                  Times(b, Plus(n, C1),
                                                      Subtract(
                                                          Plus(Times(a, b, Sqr(c)),
                                                              Times(c, d, Plus(Sqr(a), Sqr(b)))),
                                                          Times(C3, a, b, Sqr(d)))),
                                                  Times(a, Plus(n, C2),
                                                      Sqr(Subtract(Times(b, c), Times(a, d))))),
                                              Sin(Plus(e, Times(f, x)))),
                                          Times(b,
                                              Plus(Times(Sqr(b), Subtract(Sqr(c), Sqr(d))),
                                                  Times(CN1, m,
                                                      Sqr(Subtract(Times(b, c), Times(a, d)))),
                                                  Times(d, n,
                                                      Subtract(Times(C2, a, b, c),
                                                          Times(d, Plus(Sqr(a), Sqr(b)))))),
                                              Sqr(Sin(Plus(e, Times(f, x)))))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      NeQ(Subtract(Sqr(a), Sqr(b)), C0), NeQ(Subtract(Sqr(c),
                          Sqr(d)), C0),
                      GtQ(m, C2), LtQ(n, CN1), Or(IntegerQ(m),
                          IntegersQ(Times(C2, m), Times(C2, n)))))),
          IIntegrate(2793,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT, $($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Power(
                          Plus(c_DEFAULT,
                              Times(d_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(Sqr(b), Cos(Plus(e, Times(f, x))),
                                  Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Subtract(m,
                                      C2)),
                                  Power(Plus(c, Times(d,
                                      Sin(Plus(e, Times(f, x))))), Plus(n,
                                          C1)),
                                  Power(Times(d, f, Plus(m, n)), CN1)),
                              x)),
                      Dist(
                          Power(Times(d,
                              Plus(m, n)), CN1),
                          Integrate(
                              Times(
                                  Power(
                                      Plus(a, Times(b,
                                          Sin(Plus(e, Times(f, x))))),
                                      Subtract(m, C3)),
                                  Power(Plus(c,
                                      Times(d, Sin(Plus(e, Times(f, x))))), n),
                                  Simp(
                                      Subtract(
                                          Subtract(
                                              Plus(Times(Power(a, C3), d, Plus(m, n)),
                                                  Times(Sqr(b),
                                                      Plus(Times(b, c, Subtract(m, C2)),
                                                          Times(a, d, Plus(n, C1))))),
                                              Times(b,
                                                  Subtract(
                                                      Subtract(Times(a, b, c),
                                                          Times(Sqr(b), d,
                                                              Subtract(Plus(m, n), C1))),
                                                      Times(C3, Sqr(a), d, Plus(m, n))),
                                                  Sin(Plus(e, Times(f, x))))),
                                          Times(Sqr(b), Subtract(Times(b, c, Subtract(m, C1)),
                                              Times(a, d,
                                                  Subtract(Plus(Times(C3, m), Times(C2, n)), C2))),
                                              Sqr(Sin(Plus(e, Times(f, x)))))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, n), x), NeQ(
                      Subtract(Times(b, c), Times(a, d)), C0), NeQ(Subtract(Sqr(a), Sqr(b)), C0),
                      NeQ(Subtract(Sqr(c), Sqr(d)), C0), GtQ(m, C2),
                      Or(IntegerQ(m), IntegersQ(Times(C2, m), Times(C2, n))), Not(And(IGtQ(n, C2),
                          Or(Not(IntegerQ(m)), And(EqQ(a, C0), NeQ(c, C0)))))))),
          IIntegrate(2794,
              Integrate(
                  Times(
                      Sqrt(Times(d_DEFAULT,
                          $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          QQ(-3L, 2L))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(CN2, a, d, Cos(Plus(e, Times(f, x))),
                              Power(
                                  Times(f, Subtract(Sqr(a), Sqr(b)),
                                      Sqrt(Plus(a, Times(b, Sin(Plus(e, Times(f, x)))))),
                                      Sqrt(Times(d, Sin(Plus(e, Times(f, x)))))),
                                  CN1)),
                          x),
                      Dist(Times(Sqr(d), Power(Subtract(Sqr(a), Sqr(b)), CN1)),
                          Integrate(
                              Times(
                                  Sqrt(Plus(a, Times(b, Sin(Plus(e, Times(f, x)))))), Power(Times(d,
                                      Sin(Plus(e, Times(f, x)))), QQ(-3L, 2L))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, d, e, f), x), NeQ(Subtract(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(2795,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          QQ(-3L, 2L)),
                      Sqrt(
                          Plus(c_,
                              Times(d_DEFAULT, $($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(
                          Times(Subtract(c, d), Power(Subtract(a, b),
                              CN1)),
                          Integrate(
                              Power(
                                  Times(
                                      Sqrt(Plus(a, Times(b, Sin(
                                          Plus(e, Times(f, x)))))),
                                      Sqrt(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))))),
                                  CN1),
                              x),
                          x),
                      Dist(Times(Subtract(Times(b, c), Times(a, d)), Power(Subtract(a, b), CN1)),
                          Integrate(Times(Plus(C1, Sin(Plus(e, Times(f, x)))),
                              Power(
                                  Times(
                                      Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))),
                                          QQ(3L, 2L)),
                                      Sqrt(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))))),
                                  CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), NeQ(Subtract(Times(b, c), Times(a,
                      d)), C0), NeQ(Subtract(Sqr(a),
                          Sqr(b)), C0),
                      NeQ(Subtract(Sqr(c), Sqr(d)), C0)))),
          IIntegrate(2796,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT, $($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Power(
                          Plus(c_DEFAULT,
                              Times(d_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(b, Cos(Plus(e, Times(f, x))),
                                  Power(Plus(a, Times(b,
                                      Sin(Plus(e, Times(f, x))))), Plus(m, C1)),
                                  Power(Plus(c, Times(d,
                                      Sin(Plus(e, Times(f, x))))), n),
                                  Power(Times(f, Plus(m, C1), Subtract(Sqr(a), Sqr(b))), CN1)),
                              x)),
                      Dist(
                          Power(Times(Plus(m, C1),
                              Subtract(Sqr(a), Sqr(b))), CN1),
                          Integrate(
                              Times(
                                  Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Plus(m,
                                      C1)),
                                  Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))),
                                      Subtract(n, C1)),
                                  Simp(Subtract(
                                      Plus(Times(a, c, Plus(m, C1)), Times(b, d, n),
                                          Times(
                                              Subtract(Times(a, d, Plus(m, C1)),
                                                  Times(b, c, Plus(m, C2))),
                                              Sin(Plus(e, Times(f, x))))),
                                      Times(b, d, Plus(m, n, C2), Sqr(Sin(Plus(e, Times(f, x)))))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      NeQ(Subtract(Sqr(a), Sqr(
                          b)), C0),
                      NeQ(Subtract(Sqr(c),
                          Sqr(d)), C0),
                      LtQ(m, CN1), LtQ(C0, n, C1), IntegersQ(Times(C2, m), Times(C2, n))))),
          IIntegrate(2797,
              Integrate(
                  Times(
                      Power(
                          Times(d_DEFAULT, $($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          QQ(3L, 2L)),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          QQ(-3L, 2L))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(
                          Times(d, Power(b,
                              CN1)),
                          Integrate(
                              Times(Sqrt(Times(d, Sin(Plus(e, Times(f, x))))),
                                  Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), CN1D2)),
                              x),
                          x),
                      Dist(
                          Times(a, d, Power(b,
                              CN1)),
                          Integrate(
                              Times(Sqrt(Times(d, Sin(Plus(e, Times(f, x))))), Power(Plus(a,
                                  Times(b, Sin(Plus(e, Times(f, x))))), QQ(-3L, 2L))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, d, e, f), x), NeQ(Subtract(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(2798,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          QQ(-3L, 2L)),
                      Power(
                          Plus(c_,
                              Times(d_DEFAULT, $($s("§sin"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          QQ(3L, 2L))),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(
                          Times(Sqr(d), Power(b,
                              CN2)),
                          Integrate(
                              Times(
                                  Sqrt(Plus(a, Times(b,
                                      Sin(Plus(e, Times(f, x)))))),
                                  Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), CN1D2)),
                              x),
                          x),
                      Dist(
                          Times(Subtract(Times(b, c), Times(a,
                              d)), Power(b,
                                  CN2)),
                          Integrate(
                              Times(
                                  Simp(Plus(Times(b, c), Times(a, d),
                                      Times(C2, b, d, Sin(Plus(e, Times(f, x))))), x),
                                  Power(
                                      Times(
                                          Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))),
                                              QQ(3L, 2L)),
                                          Sqrt(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))))),
                                      CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), NeQ(Subtract(Times(b, c), Times(a,
                      d)), C0), NeQ(Subtract(Sqr(a),
                          Sqr(b)), C0),
                      NeQ(Subtract(Sqr(c), Sqr(d)), C0)))),
          IIntegrate(2799,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT, $($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Power(
                          Plus(c_DEFAULT,
                              Times(d_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(Subtract(Times(b, c), Times(a, d)), Cos(Plus(e, Times(f, x))),
                                  Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Plus(m, C1)),
                                  Power(
                                      Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), Subtract(n,
                                          C1)),
                                  Power(Times(f, Plus(m, C1), Subtract(Sqr(a), Sqr(b))), CN1)),
                              x)),
                      Dist(
                          Power(Times(Plus(m, C1),
                              Subtract(Sqr(a), Sqr(b))), CN1),
                          Integrate(
                              Times(
                                  Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Plus(m,
                                      C1)),
                                  Power(
                                      Plus(c, Times(d,
                                          Sin(Plus(e, Times(f, x))))),
                                      Subtract(n, C2)),
                                  Simp(Subtract(Plus(
                                      Times(c, Subtract(Times(a, c), Times(b, d)), Plus(m, C1)),
                                      Times(d, Subtract(Times(b, c), Times(a, d)), Subtract(n, C1)),
                                      Times(Subtract(
                                          Times(d, Subtract(Times(a, c), Times(b, d)), Plus(m, C1)),
                                          Times(c, Subtract(Times(b, c), Times(a, d)),
                                              Plus(m, C2))),
                                          Sin(Plus(e, Times(f, x))))),
                                      Times(d, Subtract(Times(b, c), Times(a, d)), Plus(m, n, C1),
                                          Sqr(Sin(Plus(e, Times(f, x)))))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      NeQ(Subtract(Sqr(a), Sqr(b)), C0), NeQ(Subtract(Sqr(c),
                          Sqr(d)), C0),
                      LtQ(m, CN1), LtQ(C1, n, C2), IntegersQ(Times(C2, m), Times(C2, n))))),
          IIntegrate(2800,
              Integrate(
                  Times(
                      Power(
                          Times(d_DEFAULT,
                              $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          CN1D2),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          QQ(-3L, 2L))),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(C2, b, Cos(Plus(e, Times(f, x))),
                              Power(
                                  Times(f, Subtract(Sqr(a), Sqr(b)),
                                      Sqrt(Plus(a, Times(b,
                                          Sin(Plus(e, Times(f, x)))))),
                                      Sqrt(Times(d, Sin(Plus(e, Times(f, x)))))),
                                  CN1)),
                          x),
                      Dist(Times(d, Power(Subtract(Sqr(a), Sqr(b)), CN1)), Integrate(Times(
                          Plus(b, Times(a, Sin(Plus(e, Times(f, x))))),
                          Power(Times(Sqrt(Plus(a, Times(b, Sin(Plus(e, Times(f, x)))))),
                              Power(Times(d, Sin(Plus(e, Times(f, x)))), QQ(3L, 2L))), CN1)),
                          x), x)),
                  And(FreeQ(List(a, b, d, e, f), x), NeQ(Subtract(Sqr(a), Sqr(b)), C0)))));
}
