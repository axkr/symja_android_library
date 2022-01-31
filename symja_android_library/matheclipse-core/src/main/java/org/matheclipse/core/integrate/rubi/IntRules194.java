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
class IntRules194 {
  public static IAST RULES =
      List(
          IIntegrate(3881,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cot"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                              e_DEFAULT),
                          m_),
                      Plus(
                          Times($($s("§csc"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                              b_DEFAULT),
                          a_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Negate(
                          Simp(
                              Times(
                                  e, Power(Times(e, Cot(Plus(c, Times(d, x)))), Subtract(m,
                                      C1)),
                                  Plus(Times(a, m),
                                      Times(b, Subtract(m, C1), Csc(Plus(c, Times(d, x))))),
                                  Power(Times(d, m, Subtract(m, C1)), CN1)),
                              x)),
                      Dist(
                          Times(Sqr(e), Power(m,
                              CN1)),
                          Integrate(
                              Times(
                                  Power(Times(e, Cot(Plus(c, Times(d, x)))), Subtract(m,
                                      C2)),
                                  Plus(Times(a, m), Times(b, Subtract(m, C1),
                                      Csc(Plus(c, Times(d, x)))))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), GtQ(m, C1)))),
          IIntegrate(3882,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cot"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                              e_DEFAULT),
                          m_),
                      Plus(
                          Times($($s("§csc"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                              b_DEFAULT),
                          a_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Negate(
                          Simp(
                              Times(Power(Times(e, Cot(Plus(c, Times(d, x)))), Plus(m, C1)),
                                  Plus(a, Times(b, Csc(Plus(c, Times(d, x))))),
                                  Power(Times(d, e, Plus(m, C1)), CN1)),
                              x)),
                      Dist(Power(Times(Sqr(e), Plus(m, C1)), CN1),
                          Integrate(
                              Times(Power(Times(e, Cot(Plus(c, Times(d, x)))), Plus(m, C2)),
                                  Plus(Times(a, Plus(m, C1)), Times(b, Plus(m, C2),
                                      Csc(Plus(c, Times(d, x)))))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), LtQ(m, CN1)))),
          IIntegrate(3883,
              Integrate(
                  Times(
                      Power($($s("§cot"),
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_))), CN1),
                      Plus(
                          Times($($s("§csc"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                              b_DEFAULT),
                          a_)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          Plus(b, Times(a,
                              Sin(Plus(c, Times(d, x))))),
                          Power(Cos(Plus(c, Times(d, x))), CN1)),
                      x),
                  FreeQ(List(a, b, c, d), x))),
          IIntegrate(3884,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cot"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                              e_DEFAULT),
                          m_DEFAULT),
                      Plus(
                          Times($($s("§csc"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                              b_DEFAULT),
                          a_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(a, Integrate(Power(Times(e, Cot(Plus(c, Times(d, x)))), m),
                          x), x),
                      Dist(b,
                          Integrate(
                              Times(Power(Times(e, Cot(Plus(c, Times(d, x)))), m),
                                  Csc(Plus(c, Times(d, x)))),
                              x),
                          x)),
                  FreeQ(List(a, b, c, d, e, m), x))),
          IIntegrate(3885,
              Integrate(
                  Times(Power($($s("§cot"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))), m_DEFAULT),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          n_)),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(
                          Times(
                              Power(CN1, Times(C1D2,
                                  Subtract(m, C1))),
                              Power(Times(d, Power(b, Subtract(m, C1))), CN1)),
                          Subst(
                              Integrate(
                                  Times(Power(Subtract(Sqr(b), Sqr(x)), Times(C1D2,
                                      Subtract(m, C1))), Power(Plus(a, x),
                                          n),
                                      Power(x, CN1)),
                                  x),
                              x, Times(b, Csc(Plus(c, Times(d, x))))),
                          x)),
                  And(FreeQ(List(a, b, c, d, n), x), IntegerQ(Times(C1D2,
                      Subtract(m, C1))), NeQ(Subtract(Sqr(a), Sqr(b)),
                          C0)))),
          IIntegrate(3886,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cot"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                              e_DEFAULT),
                          m_),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          n_)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Power(Times(e, Cot(
                              Plus(c, Times(d, x)))), m),
                          Power(Plus(a, Times(b, Csc(Plus(c, Times(d, x))))), n), x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, m), x), IGtQ(n, C0)))),
          IIntegrate(3887,
              Integrate(
                  Times(
                      Power($($s("§cot"),
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_))), m_DEFAULT),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(CN2, Power(a, Plus(Times(C1D2, m), n, C1D2)), Power(d,
                          CN1)),
                      Subst(
                          Integrate(
                              Times(Power(x, m),
                                  Power(Plus(C2, Times(a, Sqr(x))),
                                      Subtract(Plus(Times(C1D2, m), n), C1D2)),
                                  Power(Plus(C1, Times(a, Sqr(x))), CN1)),
                              x),
                          x,
                          Times(
                              Cot(Plus(c,
                                  Times(d, x))),
                              Power(Plus(a, Times(b, Csc(Plus(c, Times(d, x))))), CN1D2))),
                      x),
                  And(FreeQ(List(a, b, c,
                      d), x), EqQ(Subtract(Sqr(a), Sqr(b)),
                          C0),
                      IntegerQ(Times(C1D2, m)), IntegerQ(Subtract(n, C1D2))))),
          IIntegrate(3888,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cot"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                              e_DEFAULT),
                          m_),
                      Power(
                          Plus(Times($($s("§csc"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                              b_DEFAULT), a_),
                          n_)),
                  x_Symbol),
              Condition(Dist(Times(Power(a, Times(C2, n)), Power(Power(e, Times(C2, n)), CN1)),
                  Integrate(
                      Times(Power(Times(e, Cot(Plus(c, Times(d, x)))), Plus(m, Times(C2, n))),
                          Power(Power(Plus(Negate(a), Times(b, Csc(Plus(c, Times(d, x))))), n),
                              CN1)),
                      x),
                  x),
                  And(FreeQ(List(a, b, c, d, e, m), x), EqQ(Subtract(Sqr(a),
                      Sqr(b)), C0), ILtQ(n,
                          C0)))),
          IIntegrate(3889,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cot"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                              e_DEFAULT),
                          m_),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          n_)),
                  x_Symbol),
              Condition(
                  Negate(
                      Simp(
                          Times(Power(C2, Plus(m, n, C1)),
                              Power(Times(e, Cot(
                                  Plus(c, Times(d, x)))), Plus(m, C1)),
                              Power(Plus(a, Times(b, Csc(Plus(c, Times(d, x))))), n),
                              Power(Times(a, Power(Plus(a, Times(b, Csc(Plus(c, Times(d, x))))),
                                  CN1)), Plus(m, n, C1)),
                              AppellF1(Times(C1D2, Plus(m, C1)), Plus(m, n), C1,
                                  Times(C1D2, Plus(m, C3)),
                                  Times(CN1, Subtract(a, Times(b, Csc(Plus(c, Times(d, x))))),
                                      Power(Plus(a, Times(b, Csc(Plus(c, Times(d, x))))), CN1)),
                                  Times(
                                      Subtract(a, Times(b, Csc(Plus(c, Times(d, x))))), Power(Plus(
                                          a, Times(b, Csc(Plus(c, Times(d, x))))), CN1))),
                              Power(Times(d, e, Plus(m, C1)), CN1)),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, m,
                      n), x), EqQ(Subtract(Sqr(a), Sqr(b)),
                          C0),
                      Not(IntegerQ(n))))),
          IIntegrate(3890,
              Integrate(
                  Times(
                      Sqrt(Times($($s("§cot"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                          e_DEFAULT)),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(
                          Power(a, CN1), Integrate(Sqrt(Times(e, Cot(Plus(c, Times(d, x))))),
                              x),
                          x),
                      Dist(
                          Times(b, Power(a,
                              CN1)),
                          Integrate(
                              Times(
                                  Sqrt(Times(e, Cot(
                                      Plus(c, Times(d, x))))),
                                  Power(Plus(b, Times(a, Sin(Plus(c, Times(d, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), NeQ(Subtract(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(3891,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cot"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                              e_DEFAULT),
                          m_),
                      Power(
                          Plus(Times($($s("§csc"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                              b_DEFAULT), a_),
                          CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Dist(
                              Times(Sqr(e), Power(b,
                                  CN2)),
                              Integrate(
                                  Times(
                                      Power(Times(e, Cot(Plus(c, Times(d, x)))), Subtract(m,
                                          C2)),
                                      Subtract(a, Times(b, Csc(Plus(c, Times(d, x)))))),
                                  x),
                              x)),
                      Dist(Times(Sqr(e), Subtract(Sqr(a), Sqr(b)), Power(b, CN2)),
                          Integrate(
                              Times(Power(Times(e, Cot(Plus(c, Times(d, x)))), Subtract(m, C2)),
                                  Power(Plus(a, Times(b, Csc(Plus(c, Times(d, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), NeQ(Subtract(Sqr(a),
                      Sqr(b)), C0), IGtQ(Subtract(m, C1D2),
                          C0)))),
          IIntegrate(3892,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cot"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                              e_DEFAULT),
                          CN1D2),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(
                          Power(a, CN1), Integrate(
                              Power(Times(e, Cot(Plus(c, Times(d, x)))), CN1D2), x),
                          x),
                      Dist(
                          Times(b, Power(a,
                              CN1)),
                          Integrate(
                              Power(
                                  Times(
                                      Sqrt(Times(e, Cot(Plus(c, Times(d, x))))), Plus(b,
                                          Times(a, Sin(Plus(c, Times(d, x)))))),
                                  CN1),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), NeQ(Subtract(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(3893,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cot"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                              e_DEFAULT),
                          m_),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(
                          Power(Subtract(Sqr(a),
                              Sqr(b)), CN1),
                          Integrate(
                              Times(
                                  Power(Times(e,
                                      Cot(Plus(c, Times(d, x)))), m),
                                  Subtract(a, Times(b, Csc(Plus(c, Times(d, x)))))),
                              x),
                          x),
                      Dist(
                          Times(Sqr(b), Power(Times(Sqr(e), Subtract(Sqr(a), Sqr(b))),
                              CN1)),
                          Integrate(
                              Times(Power(Times(e, Cot(Plus(c, Times(d, x)))), Plus(m, C2)), Power(
                                  Plus(a, Times(b, Csc(Plus(c, Times(d, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), NeQ(Subtract(Sqr(a),
                      Sqr(b)), C0), ILtQ(Plus(m, C1D2),
                          C0)))),
          IIntegrate(3894,
              Integrate(
                  Times(Sqr($($s("§cot"), Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          n_)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          Plus(CN1, Sqr(Csc(Plus(c, Times(d, x))))), Power(
                              Plus(a, Times(b, Csc(Plus(c, Times(d, x))))), n)),
                      x),
                  And(FreeQ(List(a, b, c, d, n), x), NeQ(Subtract(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(3895,
              Integrate(Times(Power($($s("§cot"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))), m_),
                  Power(
                      Plus(Times($($s("§csc"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))), b_DEFAULT),
                          a_),
                      n_)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Power(Plus(a, Times(b,
                              Csc(Plus(c, Times(d, x))))), n),
                          Power(Plus(CN1, Sqr(Csc(Plus(c, Times(d, x))))), Times(C1D2, m)), x),
                      x),
                  And(FreeQ(List(a, b, c, d, n), x), NeQ(Subtract(Sqr(a), Sqr(
                      b)), C0), IGtQ(Times(C1D2,
                          m), C0),
                      IntegerQ(Subtract(n, C1D2))))),
          IIntegrate(3896,
              Integrate(
                  Times(Power($($s("§cot"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))), m_),
                      Power(
                          Plus(Times($($s("§csc"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                              b_DEFAULT), a_),
                          n_)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Power(Plus(a, Times(b, Csc(Plus(c, Times(d, x))))), n), Power(Plus(CN1,
                              Sqr(Sec(Plus(c, Times(d, x))))), Times(CN1, C1D2, m)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, n), x), NeQ(Subtract(Sqr(a), Sqr(
                      b)), C0), ILtQ(Times(C1D2,
                          m), C0),
                      IntegerQ(Subtract(n, C1D2)), EqQ(m, CN2)))),
          IIntegrate(3897,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cot"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))), e_DEFAULT),
                          m_),
                      Power(
                          Plus(Times($($s("§csc"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                              b_DEFAULT), a_),
                          n_)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Power(Times(e, Cot(Plus(c, Times(d, x)))), m), Power(
                              Plus(a, Times(b, Csc(Plus(c, Times(d, x))))), n),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, m), x), NeQ(Subtract(Sqr(a),
                      Sqr(b)), C0), IGtQ(n,
                          C0)))),
          IIntegrate(3898,
              Integrate(
                  Times(Power($($s("§cot"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))), m_DEFAULT),
                      Power(
                          Plus(Times($($s("§csc"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                              b_DEFAULT), a_),
                          n_)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(Power(Cos(Plus(c, Times(d, x))), m),
                          Power(Plus(b,
                              Times(a, Sin(Plus(c, Times(d, x))))), n),
                          Power(Power(Sin(Plus(c, Times(d, x))), Plus(m, n)), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d), x), NeQ(Subtract(Sqr(a), Sqr(b)), C0), IntegerQ(n),
                      IntegerQ(m), Or(IntegerQ(Times(C1D2, m)), LeQ(m, C1))))),
          IIntegrate(3899,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times($($s("§csc"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                                  b_DEFAULT)),
                          n_DEFAULT),
                      Power(
                          Times($($s("§cot"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                              e_DEFAULT),
                          m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(
                          Power(Times(e,
                              Cot(Plus(c, Times(d, x)))), m),
                          Power(Plus(a, Times(b, Csc(Plus(c, Times(d, x))))), n)),
                      x),
                  FreeQ(List(a, b, c, d, e, m, n), x))),
          IIntegrate(3900,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cot"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                              e_DEFAULT),
                          m_),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  $($s("§sec"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Times(e, Cot(Plus(c, Times(d, x)))), m),
                          Power(Tan(Plus(c, Times(d, x))), m)),
                      Integrate(Times(Power(Plus(a, Times(b, Sec(Plus(c, Times(d, x))))), n),
                          Power(Power(Tan(Plus(c, Times(d, x))), m), CN1)), x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, m, n), x), Not(IntegerQ(m))))));
}
