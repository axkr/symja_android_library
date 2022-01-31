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
class IntRules197 {
  public static IAST RULES =
      List(
          IIntegrate(3941,
              Integrate(
                  Times(
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          m_),
                      Power(
                          Plus(Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              d_DEFAULT), c_),
                          n_)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          Power(Plus(b, Times(a,
                              Sin(Plus(e, Times(f, x))))), m),
                          Power(Plus(d, Times(c, Sin(Plus(e,
                              Times(f, x))))), n),
                          Power(Power(Sin(Plus(e, Times(f, x))), Plus(m, n)), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n), x),
                      NeQ(Subtract(Times(b, c),
                          Times(a, d)), C0),
                      IntegerQ(m), IntegerQ(n), LeQ(CN2, Plus(m, n), C0)))),
          IIntegrate(3942,
              Integrate(
                  Times(
                      Power(
                          Plus(Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              b_DEFAULT), a_),
                          m_),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  d_DEFAULT),
                              c_),
                          n_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Sqrt(Plus(d, Times(c, Sin(Plus(e, Times(f, x)))))),
                          Sqrt(Plus(a,
                              Times(b, Csc(Plus(e, Times(f, x)))))),
                          Power(
                              Times(Sqrt(Plus(b, Times(a, Sin(Plus(e, Times(f, x)))))), Sqrt(
                                  Plus(c, Times(d, Csc(Plus(e, Times(f, x))))))),
                              CN1)),
                      Integrate(
                          Times(Power(Plus(b, Times(a, Sin(Plus(e, Times(f, x))))), m),
                              Power(Plus(d, Times(c,
                                  Sin(Plus(e, Times(f, x))))), n),
                              Power(Power(Sin(Plus(e, Times(f, x))), Plus(m, n)), CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0), IntegerQ(Plus(m,
                          C1D2)),
                      IntegerQ(Plus(n, C1D2)), LeQ(CN2, Plus(m, n), C0)))),
          IIntegrate(3943,
              Integrate(
                  Times(
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          m_),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  d_DEFAULT),
                              c_),
                          n_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Sin(Plus(e, Times(f, x))), Plus(m, n)),
                          Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), m),
                          Power(Plus(c, Times(d, Csc(Plus(e, Times(f, x))))), n),
                          Power(
                              Times(Power(Plus(b, Times(a, Sin(Plus(e, Times(f, x))))), m),
                                  Power(Plus(d, Times(c, Sin(Plus(e, Times(f, x))))), n)),
                              CN1)),
                      Integrate(
                          Times(Power(Plus(b, Times(a, Sin(Plus(e, Times(f, x))))), m),
                              Power(Plus(d, Times(c, Sin(Plus(e, Times(f, x))))), n), Power(Power(
                                  Sin(Plus(e, Times(f, x))), Simplify(Plus(m, n))),
                                  CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n), x),
                      NeQ(Subtract(Times(b, c),
                          Times(a, d)), C0),
                      EqQ(Plus(m, n), C0), Not(IntegerQ(Times(C2, m)))))),
          IIntegrate(3944,
              Integrate(
                  Times(Power(
                      Plus(Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), b_DEFAULT),
                          a_),
                      m_),
                      Power(Plus(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), d_DEFAULT),
                          c_), n_)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandTrig(
                          Power(Plus(a, Times(b,
                              $($s("§csc"), Plus(e, Times(f, x))))), m),
                          Power(Plus(c, Times(d, $($s("§csc"), Plus(e, Times(f, x))))), n), x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n), x), IGtQ(n, C0)))),
          IIntegrate(3945,
              Integrate(
                  Times(Power(
                      Plus(Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), b_DEFAULT),
                          a_),
                      m_DEFAULT),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  d_DEFAULT),
                              c_),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(
                          Power(Plus(a,
                              Times(b, Csc(Plus(e, Times(f, x))))), m),
                          Power(Plus(c, Times(d, Csc(Plus(e, Times(f, x))))), n)),
                      x),
                  FreeQ(List(a, b, c, d, e, f, m, n), x))),
          IIntegrate(3946,
              Integrate(
                  Times(
                      Power(
                          Times(
                              d_DEFAULT, Power($($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  CN1)),
                          n_),
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT,
                                  $($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(d, m),
                      Integrate(
                          Times(
                              Power(Plus(b, Times(a,
                                  Cos(Plus(e, Times(f, x))))), m),
                              Power(Times(d, Cos(Plus(e, Times(f, x)))), Subtract(n, m))),
                          x),
                      x),
                  And(FreeQ(List(a, b, d, e, f, n), x), Not(IntegerQ(n)), IntegerQ(m)))),
          IIntegrate(3947,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT)),
                          m_DEFAULT),
                      Power(Times(Power($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), CN1),
                          d_DEFAULT), n_)),
                  x_Symbol),
              Condition(
                  Dist(Power(d, m),
                      Integrate(
                          Times(
                              Power(Plus(b, Times(a, Sin(
                                  Plus(e, Times(f, x))))), m),
                              Power(Times(d, Sin(Plus(e, Times(f, x)))), Subtract(n, m))),
                          x),
                      x),
                  And(FreeQ(List(a, b, d, e, f, n), x), Not(IntegerQ(n)), IntegerQ(m)))),
          IIntegrate(3948,
              Integrate(
                  Times(
                      Power(
                          Times(c_DEFAULT,
                              Power(
                                  Times(d_DEFAULT,
                                      $($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                                  p_)),
                          n_),
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT,
                                  $($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(c, IntPart(
                              n)),
                          Power(Times(c, Power(Times(d, Sec(Plus(e, Times(f, x)))), p)),
                              FracPart(n)),
                          Power(Power(Times(d, Sec(Plus(e, Times(f, x)))), Times(p, FracPart(n))),
                              CN1)),
                      Integrate(
                          Times(
                              Power(Plus(a, Times(b, Sec(Plus(e, Times(f, x))))), m), Power(
                                  Times(d, Sec(Plus(e, Times(f, x)))), Times(n, p))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n, p), x), Not(IntegerQ(n))))),
          IIntegrate(3949,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT)),
                          m_DEFAULT),
                      Power(
                          Times(c_DEFAULT,
                              Power(
                                  Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                      d_DEFAULT),
                                  p_)),
                          n_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(c, IntPart(
                              n)),
                          Power(Times(c, Power(Times(d, Csc(Plus(e, Times(f, x)))), p)),
                              FracPart(n)),
                          Power(Power(Times(d, Csc(Plus(e, Times(f, x)))), Times(p, FracPart(n))),
                              CN1)),
                      Integrate(
                          Times(
                              Power(Plus(a, Times(b, Cos(Plus(e, Times(f, x))))), m), Power(
                                  Times(d, Cos(Plus(e, Times(f, x)))), Times(n, p))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n, p), x), Not(IntegerQ(n))))),
          IIntegrate(3950,
              Integrate(
                  Times(
                      $($s("§csc"), Plus(e_DEFAULT,
                          Times(f_DEFAULT, x_))),
                      Power(Plus(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), b_DEFAULT),
                          a_), m_),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  d_DEFAULT),
                              c_),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(b, Cot(Plus(e, Times(f, x))),
                          Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), m),
                          Power(Plus(c,
                              Times(d, Csc(Plus(e, Times(f, x))))), n),
                          Power(Times(a, f, Plus(Times(C2, m), C1)), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n), x),
                      EqQ(Plus(Times(b, c), Times(a,
                          d)), C0),
                      EqQ(Subtract(Sqr(a), Sqr(
                          b)), C0),
                      EqQ(Plus(m, n, C1), C0), NeQ(Plus(Times(C2, m), C1), C0)))),
          IIntegrate(3951,
              Integrate(
                  Times(
                      $($s("§csc"), Plus(e_DEFAULT,
                          Times(f_DEFAULT, x_))),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          m_),
                      Power(Plus(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), d_DEFAULT),
                          c_), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(b, Cot(Plus(e, Times(f, x))),
                              Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), m),
                              Power(Plus(c,
                                  Times(d, Csc(Plus(e, Times(f, x))))), n),
                              Power(Times(a, f, Plus(Times(C2, m), C1)), CN1)),
                          x),
                      Dist(
                          Times(Plus(m, n, C1), Power(Times(a, Plus(Times(C2, m), C1)),
                              CN1)),
                          Integrate(
                              Times(Csc(Plus(e, Times(f, x))),
                                  Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), Plus(m,
                                      C1)),
                                  Power(Plus(c, Times(d, Csc(Plus(e, Times(f, x))))), n)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, m, n), x),
                      EqQ(Plus(Times(b, c), Times(a, d)), C0), EqQ(Subtract(Sqr(a), Sqr(b)), C0),
                      ILtQ(Plus(m, n, C1), C0), NeQ(Plus(Times(C2,
                          m), C1), C0),
                      Not(LtQ(n,
                          C0)),
                      Not(And(IGtQ(Plus(n, C1D2), C0), LtQ(Plus(n, C1D2), Negate(Plus(m, n)))))))),
          IIntegrate(3952,
              Integrate(
                  Times(
                      $($s("§csc"), Plus(e_DEFAULT,
                          Times(f_DEFAULT, x_))),
                      Power(Plus(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), b_DEFAULT),
                          a_), CN1D2),
                      Sqrt(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  d_DEFAULT),
                              c_))),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(a, c, Log(Plus(C1, Times(b, Csc(Plus(e, Times(f, x))), Power(a, CN1)))),
                          Cot(Plus(e, Times(f, x))), Power(Times(b, f,
                              Sqrt(Plus(a, Times(b, Csc(Plus(e, Times(f, x)))))), Sqrt(
                                  Plus(c, Times(d, Csc(Plus(e, Times(f, x))))))),
                              CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f), x), EqQ(Plus(Times(b, c), Times(a, d)), C0),
                      EqQ(Subtract(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(3953,
              Integrate(
                  Times(
                      $($s("§csc"), Plus(e_DEFAULT,
                          Times(f_DEFAULT, x_))),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          m_DEFAULT),
                      Sqrt(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  d_DEFAULT),
                              c_))),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(C2, a, c, Cot(Plus(e, Times(f, x))),
                          Power(Plus(a,
                              Times(b, Csc(Plus(e, Times(f, x))))), m),
                          Power(
                              Times(b, f, Plus(Times(C2, m), C1), Sqrt(
                                  Plus(c, Times(d, Csc(Plus(e, Times(f, x))))))),
                              CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m), x), EqQ(Plus(Times(b, c), Times(a,
                      d)), C0), EqQ(Subtract(Sqr(a), Sqr(b)), C0), NeQ(m, Negate(
                          C1D2))))),
          IIntegrate(3954,
              Integrate(
                  Times(
                      $($s("§csc"), Plus(e_DEFAULT,
                          Times(f_DEFAULT, x_))),
                      Power(
                          Plus(Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              b_DEFAULT), a_),
                          m_),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  d_DEFAULT),
                              c_),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(C2, a, c, Cot(Plus(e, Times(f, x))),
                              Power(Plus(a,
                                  Times(b, Csc(Plus(e, Times(f, x))))), m),
                              Power(Plus(c, Times(d, Csc(Plus(e, Times(f, x))))), Subtract(n,
                                  C1)),
                              Power(Times(b, f, Plus(Times(C2, m), C1)), CN1)),
                          x),
                      Dist(
                          Times(
                              d, Subtract(Times(C2, n), C1),
                              Power(Times(b, Plus(Times(C2, m), C1)), CN1)),
                          Integrate(
                              Times(Csc(Plus(e, Times(f, x))),
                                  Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), Plus(m, C1)),
                                  Power(Plus(c, Times(d, Csc(Plus(e, Times(f, x))))),
                                      Subtract(n, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), EqQ(Plus(Times(b, c), Times(a, d)), C0),
                      EqQ(Subtract(Sqr(a), Sqr(b)), C0), IGtQ(Subtract(n,
                          C1D2), C0),
                      LtQ(m, Negate(C1D2))))),
          IIntegrate(3955,
              Integrate(
                  Times(
                      $($s("§csc"), Plus(e_DEFAULT,
                          Times(f_DEFAULT, x_))),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          m_DEFAULT),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  d_DEFAULT),
                              c_),
                          n_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(d, Cot(Plus(e, Times(f, x))),
                                  Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), m),
                                  Power(
                                      Plus(c, Times(d, Csc(Plus(e, Times(f, x))))), Subtract(n,
                                          C1)),
                                  Power(Times(f, Plus(m, n)), CN1)),
                              x)),
                      Dist(Times(c, Subtract(Times(C2, n), C1), Power(Plus(m, n), CN1)),
                          Integrate(
                              Times(Csc(Plus(e, Times(f, x))),
                                  Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), m),
                                  Power(Plus(c, Times(d, Csc(Plus(e, Times(f, x))))),
                                      Subtract(n, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, m), x), EqQ(Plus(Times(b, c), Times(a, d)), C0),
                      EqQ(Subtract(Sqr(a), Sqr(b)), C0), IGtQ(Subtract(n, C1D2), C0),
                      Not(LtQ(m, Negate(C1D2))),
                      Not(And(IGtQ(Subtract(m, C1D2), C0), LtQ(m, n)))))),
          IIntegrate(3956,
              Integrate(
                  Times(
                      $($s("§csc"), Plus(e_DEFAULT,
                          Times(f_DEFAULT, x_))),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          CN1D2),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  d_DEFAULT),
                              c_),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(
                              CN2, d, Cot(Plus(e,
                                  Times(f, x))),
                              Power(Plus(c, Times(d, Csc(Plus(e, Times(f, x))))), Subtract(n,
                                  C1)),
                              Power(
                                  Times(
                                      f, Subtract(Times(C2, n), C1), Sqrt(Plus(a,
                                          Times(b, Csc(Plus(e, Times(f, x))))))),
                                  CN1)),
                          x),
                      Dist(
                          Times(
                              C2, c, Subtract(Times(C2, n), C1),
                              Power(Subtract(Times(C2, n), C1), CN1)),
                          Integrate(
                              Times(Csc(Plus(e, Times(f, x))),
                                  Power(Plus(c, Times(d, Csc(Plus(e, Times(f, x))))), Subtract(n,
                                      C1)),
                                  Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), CN1D2)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), EqQ(Plus(Times(b, c),
                      Times(a, d)), C0), EqQ(Subtract(Sqr(a), Sqr(b)),
                          C0),
                      IGtQ(n, C0)))),
          IIntegrate(3957,
              Integrate(
                  Times(
                      $($s("§csc"), Plus(e_DEFAULT,
                          Times(f_DEFAULT, x_))),
                      Power(
                          Plus(Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              b_DEFAULT), a_),
                          m_),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  d_DEFAULT),
                              c_),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(C2, a, c, Cot(Plus(e, Times(f, x))),
                              Power(Plus(a,
                                  Times(b, Csc(Plus(e, Times(f, x))))), m),
                              Power(Plus(c, Times(d, Csc(Plus(e, Times(f, x))))), Subtract(n,
                                  C1)),
                              Power(Times(b, f, Plus(Times(C2, m), C1)), CN1)),
                          x),
                      Dist(
                          Times(
                              d, Subtract(Times(C2, n), C1),
                              Power(Times(b, Plus(Times(C2, m), C1)), CN1)),
                          Integrate(
                              Times(Csc(Plus(e, Times(f, x))),
                                  Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), Plus(m, C1)),
                                  Power(Plus(c, Times(d, Csc(Plus(e, Times(f, x))))),
                                      Subtract(n, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), EqQ(Plus(Times(b, c), Times(a, d)), C0),
                      EqQ(Subtract(Sqr(a), Sqr(b)), C0), IGtQ(n, C0), LtQ(m,
                          Negate(C1D2)),
                      IntegerQ(Times(C2, m))))),
          IIntegrate(3958,
              Integrate(
                  Times(
                      $($s("§csc"), Plus(e_DEFAULT,
                          Times(f_DEFAULT, x_))),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          m_DEFAULT),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  d_DEFAULT),
                              c_),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(Times(CN1, a, c), m),
                      Integrate(
                          ExpandTrig(
                              Times($($s("§csc"), Plus(e, Times(f, x))),
                                  Power($($s("§cot"), Plus(e, Times(f, x))), Times(C2, m))),
                              Power(Plus(c, Times(d, $($s("§csc"), Plus(e, Times(f, x))))),
                                  Subtract(n, m)),
                              x),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, n), x), EqQ(Plus(Times(b, c), Times(a, d)), C0),
                      EqQ(Subtract(Sqr(a), Sqr(
                          b)), C0),
                      IntegersQ(m, n), GeQ(Subtract(n, m), C0), GtQ(Times(m, n), C0)))),
          IIntegrate(3959,
              Integrate(
                  Times(
                      $($s("§csc"), Plus(e_DEFAULT,
                          Times(f_DEFAULT, x_))),
                      Power(
                          Plus(Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              b_DEFAULT), a_),
                          m_),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  d_DEFAULT),
                              c_),
                          m_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Times(CN1, a, c), Plus(m, C1D2)), Cot(Plus(e, Times(f, x))),
                          Power(
                              Times(Sqrt(Plus(a, Times(b, Csc(Plus(e, Times(f, x)))))),
                                  Sqrt(Plus(c, Times(d, Csc(Plus(e, Times(f, x))))))),
                              CN1)),
                      Integrate(
                          Times(
                              Csc(Plus(e,
                                  Times(f, x))),
                              Power(Cot(Plus(e, Times(f, x))), Times(C2, m))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f), x), EqQ(Plus(Times(b, c), Times(a,
                      d)), C0), EqQ(Subtract(Sqr(a),
                          Sqr(b)), C0),
                      IntegerQ(Plus(m, C1D2))))),
          IIntegrate(3960,
              Integrate(
                  Times(
                      $($s("§csc"), Plus(e_DEFAULT,
                          Times(f_DEFAULT, x_))),
                      Power(
                          Plus(Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              b_DEFAULT), a_),
                          m_),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  d_DEFAULT),
                              c_),
                          n_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(b, Cot(Plus(e, Times(f, x))),
                              Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), m),
                              Power(Plus(c,
                                  Times(d, Csc(Plus(e, Times(f, x))))), n),
                              Power(Times(a, f, Plus(Times(C2, m), C1)), CN1)),
                          x),
                      Dist(Times(Plus(m, n, C1), Power(Times(a, Plus(Times(C2, m), C1)), CN1)),
                          Integrate(Times(Csc(Plus(e, Times(f, x))),
                              Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), Plus(m, C1)),
                              Power(Plus(c, Times(d, Csc(Plus(e, Times(f, x))))), n)), x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, n), x), EqQ(Plus(Times(b, c), Times(a, d)), C0),
                      EqQ(Subtract(Sqr(a), Sqr(b)), C0),
                      Or(And(ILtQ(m, C0), ILtQ(Subtract(n, C1D2), C0)), And(
                          ILtQ(Subtract(m, C1D2), C0), ILtQ(Subtract(n, C1D2), C0), LtQ(m, n)))))));
}
