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
class IntRules196 {
  public static IAST RULES =
      List(
          IIntegrate(3921,
              Integrate(
                  Times(
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          CN1D2),
                      Plus(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              d_DEFAULT),
                          c_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(c,
                          Integrate(Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), CN1D2),
                              x),
                          x),
                      Dist(d,
                          Integrate(
                              Times(
                                  Csc(Plus(e, Times(f, x))), Power(Plus(a,
                                      Times(b, Csc(Plus(e, Times(f, x))))), CN1D2)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), NeQ(Subtract(Times(b, c),
                      Times(a, d)), C0), NeQ(Subtract(Sqr(a), Sqr(b)),
                          C0)))),
          IIntegrate(3922,
              Integrate(
                  Times(
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          m_),
                      Plus(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              d_DEFAULT),
                          c_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(Subtract(Times(b, c), Times(a, d)), Cot(Plus(e, Times(f, x))),
                                  Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), m),
                                  Power(Times(b, f, Plus(Times(C2, m), C1)), CN1)),
                              x)),
                      Dist(Power(Times(Sqr(a), Plus(Times(C2, m), C1)), CN1),
                          Integrate(
                              Times(
                                  Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), Plus(m, C1)),
                                  Simp(Subtract(Times(a, c, Plus(Times(C2, m), C1)),
                                      Times(Subtract(Times(b, c), Times(a, d)), Plus(m, C1),
                                          Csc(Plus(e, Times(f, x))))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), NeQ(Subtract(Times(b, c), Times(a,
                      d)), C0), LtQ(m, CN1), EqQ(Subtract(Sqr(a),
                          Sqr(b)), C0),
                      IntegerQ(Times(C2, m))))),
          IIntegrate(3923,
              Integrate(
                  Times(
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          m_),
                      Plus(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              d_DEFAULT),
                          c_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(Times(b, Subtract(Times(b, c), Times(a, d)), Cot(Plus(e, Times(f, x))),
                          Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), Plus(m, C1)), Power(
                              Times(a, f, Plus(m, C1), Subtract(Sqr(a), Sqr(b))), CN1)),
                          x),
                      Dist(Power(Times(a, Plus(m, C1), Subtract(Sqr(a), Sqr(b))), CN1),
                          Integrate(
                              Times(
                                  Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), Plus(m, C1)),
                                  Simp(Plus(Times(c, Subtract(Sqr(a), Sqr(b)), Plus(m, C1)),
                                      Times(CN1, a, Subtract(Times(b, c), Times(a, d)), Plus(m, C1),
                                          Csc(Plus(e, Times(f, x)))),
                                      Times(b, Subtract(Times(b, c), Times(a, d)), Plus(m, C2),
                                          Sqr(Csc(Plus(e, Times(f, x)))))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), NeQ(Subtract(Times(b, c), Times(a,
                      d)), C0), LtQ(m, CN1), NeQ(Subtract(Sqr(a),
                          Sqr(b)), C0),
                      IntegerQ(Times(C2, m))))),
          IIntegrate(3924,
              Integrate(
                  Times(
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          m_),
                      Plus(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              d_DEFAULT),
                          c_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(
                          c, Integrate(Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), m),
                              x),
                          x),
                      Dist(d,
                          Integrate(
                              Times(
                                  Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))),
                                      m),
                                  Csc(Plus(e, Times(f, x)))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f,
                      m), x), NeQ(Subtract(Times(b, c), Times(a, d)),
                          C0),
                      Not(IntegerQ(Times(C2, m)))))),
          IIntegrate(3925,
              Integrate(
                  Times(
                      Sqrt(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_)),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  d_DEFAULT),
                              c_),
                          CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Power(c, CN1),
                          Integrate(Sqrt(Plus(a, Times(b, Csc(Plus(e, Times(f, x)))))), x), x),
                      Dist(Times(d, Power(c, CN1)),
                          Integrate(
                              Times(Csc(Plus(e, Times(f, x))),
                                  Sqrt(Plus(a, Times(b, Csc(Plus(e, Times(f, x)))))),
                                  Power(Plus(c, Times(d, Csc(Plus(e, Times(f, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), NeQ(Subtract(Times(b, c),
                      Times(a, d)), C0), Or(EqQ(Subtract(Sqr(a), Sqr(b)), C0),
                          EqQ(Subtract(Sqr(c), Sqr(d)), C0))))),
          IIntegrate(3926,
              Integrate(
                  Times(Sqrt(
                      Plus(Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), b_DEFAULT),
                          a_)),
                      Power(
                          Plus(Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              d_DEFAULT), c_),
                          CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(Times(a, Power(c, CN1)),
                          Integrate(Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), CN1D2),
                              x),
                          x),
                      Dist(Times(Subtract(Times(b, c), Times(a, d)), Power(c, CN1)),
                          Integrate(
                              Times(Csc(Plus(e, Times(f, x))),
                                  Power(Times(Sqrt(Plus(a, Times(b, Csc(Plus(e, Times(f, x)))))),
                                      Plus(c, Times(d, Csc(Plus(e, Times(f, x)))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), NeQ(Subtract(Times(b, c),
                      Times(a, d)), C0), NeQ(Subtract(Sqr(a), Sqr(b)),
                          C0),
                      NeQ(Subtract(Sqr(c), Sqr(d)), C0)))),
          IIntegrate(3927,
              Integrate(
                  Times(
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          QQ(3L, 2L)),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  d_DEFAULT),
                              c_),
                          CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(Times(a, Power(c, CN1)),
                          Integrate(Sqrt(Plus(a, Times(b, Csc(Plus(e, Times(f, x)))))), x), x),
                      Dist(Times(Subtract(Times(b, c), Times(a, d)), Power(c, CN1)),
                          Integrate(Times(Csc(Plus(e, Times(f, x))),
                              Sqrt(Plus(a, Times(b, Csc(Plus(e, Times(f, x)))))), Power(
                                  Plus(c, Times(d, Csc(Plus(e, Times(f, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), NeQ(Subtract(Times(b, c),
                      Times(a, d)), C0), Or(EqQ(Subtract(Sqr(a), Sqr(b)), C0),
                          EqQ(Subtract(Sqr(c), Sqr(d)), C0))))),
          IIntegrate(3928,
              Integrate(
                  Times(
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          QQ(3L, 2L)),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  d_DEFAULT),
                              c_),
                          CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(
                          Power(Times(c,
                              d), CN1),
                          Integrate(
                              Times(
                                  Plus(
                                      Times(Sqr(a), d), Times(Sqr(b), c, Csc(
                                          Plus(e, Times(f, x))))),
                                  Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), CN1D2)),
                              x),
                          x),
                      Dist(
                          Times(Sqr(Subtract(Times(b, c),
                              Times(a, d))), Power(Times(c, d),
                                  CN1)),
                          Integrate(
                              Times(Csc(Plus(e, Times(f, x))),
                                  Power(Times(Sqrt(Plus(a, Times(b, Csc(Plus(e, Times(f, x)))))),
                                      Plus(c, Times(d, Csc(Plus(e, Times(f, x)))))),
                                      CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), NeQ(Subtract(Times(b, c), Times(a, d)),
                      C0), NeQ(Subtract(Sqr(a), Sqr(b)), C0),
                      NeQ(Subtract(Sqr(c), Sqr(d)), C0)))),
          IIntegrate(3929,
              Integrate(
                  Times(
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
                          CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(
                          Power(Times(c,
                              Subtract(Times(b, c), Times(a, d))), CN1),
                          Integrate(
                              Times(
                                  Subtract(
                                      Subtract(Times(b, c), Times(a,
                                          d)),
                                      Times(b, d, Csc(Plus(e, Times(f, x))))),
                                  Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), CN1D2)),
                              x),
                          x),
                      Dist(Times(Sqr(d), Power(Times(c, Subtract(Times(b, c), Times(a, d))), CN1)),
                          Integrate(
                              Times(Csc(Plus(e, Times(f, x))),
                                  Sqrt(Plus(a, Times(b, Csc(Plus(e, Times(f, x)))))), Power(Plus(c,
                                      Times(d, Csc(Plus(e, Times(f, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), NeQ(Subtract(Times(b, c),
                      Times(a, d)), C0), Or(EqQ(Subtract(Sqr(a), Sqr(b)), C0),
                          EqQ(Subtract(Sqr(c), Sqr(d)), C0))))),
          IIntegrate(3930,
              Integrate(
                  Times(
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
                          CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Power(c, CN1),
                          Integrate(Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), CN1D2),
                              x),
                          x),
                      Dist(Times(d, Power(c, CN1)),
                          Integrate(
                              Times(Csc(Plus(e, Times(f, x))),
                                  Power(
                                      Times(Sqrt(Plus(a, Times(b, Csc(Plus(e, Times(f, x)))))),
                                          Plus(c, Times(d, Csc(Plus(e, Times(f, x)))))),
                                      CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), NeQ(Subtract(Times(b, c),
                      Times(a, d)), C0), NeQ(Subtract(Sqr(a), Sqr(b)),
                          C0)))),
          IIntegrate(3931,
              Integrate(Times(
                  Sqrt(Plus(Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), b_DEFAULT),
                      a_)),
                  Sqrt(
                      Plus(Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), d_DEFAULT),
                          c_))),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Sqrt(Plus(a, Times(b, Csc(Plus(e, Times(f, x)))))),
                          Sqrt(Plus(c,
                              Times(d, Csc(Plus(e, Times(f, x)))))),
                          Power(Cot(Plus(e, Times(f, x))), CN1)),
                      Integrate(Cot(Plus(e, Times(f, x))), x), x),
                  And(FreeQ(List(a, b, c, d, e, f), x), NeQ(Subtract(Times(b, c),
                      Times(a, d)), C0), EqQ(Subtract(Sqr(a), Sqr(b)),
                          C0),
                      EqQ(Subtract(Sqr(c), Sqr(d)), C0)))),
          IIntegrate(3932,
              Integrate(
                  Times(
                      Sqrt(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_)),
                      Sqrt(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  d_DEFAULT),
                              c_))),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(c,
                          Integrate(Times(Sqrt(Plus(a, Times(b, Csc(Plus(e, Times(f, x)))))),
                              Power(Plus(c, Times(d, Csc(Plus(e, Times(f, x))))), CN1D2)), x),
                          x),
                      Dist(d,
                          Integrate(
                              Times(Csc(Plus(e, Times(f, x))),
                                  Sqrt(Plus(a, Times(b, Csc(Plus(e, Times(f, x)))))), Power(Plus(c,
                                      Times(d, Csc(Plus(e, Times(f, x))))), CN1D2)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e,
                      f), x), NeQ(Subtract(Times(b, c), Times(a, d)),
                          C0)))),
          IIntegrate(3933,
              Integrate(
                  Times(
                      Sqrt(
                          Plus(Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              b_DEFAULT), a_)),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  d_DEFAULT),
                              c_),
                          CN1D2)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Power(c, CN1),
                          Integrate(
                              Times(
                                  Sqrt(Plus(a, Times(b,
                                      Csc(Plus(e, Times(f, x)))))),
                                  Sqrt(Plus(c, Times(d, Csc(Plus(e, Times(f, x))))))),
                              x),
                          x),
                      Dist(Times(d, Power(c, CN1)),
                          Integrate(
                              Times(Csc(Plus(e, Times(f, x))),
                                  Sqrt(Plus(a, Times(b, Csc(Plus(e, Times(f, x)))))),
                                  Power(Plus(c, Times(d, Csc(Plus(e, Times(f, x))))), CN1D2)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), NeQ(Subtract(Times(b, c), Times(a,
                      d)), C0), EqQ(Subtract(Sqr(a),
                          Sqr(b)), C0),
                      EqQ(Subtract(Sqr(c), Sqr(d)), C0)))),
          IIntegrate(3934,
              Integrate(
                  Times(
                      Sqrt(
                          Plus(Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              b_DEFAULT), a_)),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  d_DEFAULT),
                              c_),
                          CN1D2)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(CN2, a, Power(f,
                          CN1)),
                      Subst(
                          Integrate(Power(Plus(C1, Times(a, c, Sqr(x))),
                              CN1), x),
                          x,
                          Times(Cot(Plus(e, Times(f, x))),
                              Power(
                                  Times(Sqrt(Plus(a, Times(b, Csc(Plus(e, Times(f, x)))))),
                                      Sqrt(Plus(c, Times(d, Csc(Plus(e, Times(f, x))))))),
                                  CN1))),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f), x), NeQ(Subtract(Times(b, c), Times(a, d)),
                      C0), EqQ(Subtract(Sqr(a), Sqr(b)), C0),
                      NeQ(Subtract(Sqr(c), Sqr(d)), C0)))),
          IIntegrate(3935,
              Integrate(
                  Times(
                      Sqrt(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_)),
                      Power(Plus(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), d_DEFAULT),
                          c_), CN1D2)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(
                          Times(a, Power(c,
                              CN1)),
                          Integrate(
                              Times(
                                  Sqrt(Plus(c, Times(d, Csc(Plus(e, Times(f,
                                      x)))))),
                                  Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), CN1D2)),
                              x),
                          x),
                      Dist(Times(Subtract(Times(b, c), Times(a, d)), Power(c, CN1)),
                          Integrate(Times(Csc(Plus(e, Times(f, x))),
                              Power(Times(Sqrt(Plus(a, Times(b, Csc(Plus(e, Times(f, x)))))),
                                  Sqrt(Plus(c, Times(d, Csc(Plus(e, Times(f, x))))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), NeQ(Subtract(Times(b, c), Times(a, d)),
                      C0), NeQ(Subtract(Sqr(a), Sqr(b)), C0),
                      EqQ(Subtract(Sqr(c), Sqr(d)), C0)))),
          IIntegrate(3936,
              Integrate(
                  Times(
                      Sqrt(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_)),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  d_DEFAULT),
                              c_),
                          CN1D2)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(
                          C2, Plus(a, Times(b,
                              Csc(Plus(e, Times(f, x))))),
                          Sqrt(
                              Times(Subtract(Times(b, c), Times(a, d)),
                                  Plus(C1, Csc(Plus(e, Times(f, x)))),
                                  Power(
                                      Times(
                                          Subtract(c, d), Plus(a,
                                              Times(b, Csc(Plus(e, Times(f, x)))))),
                                      CN1))),
                          Sqrt(
                              Times(CN1, Subtract(Times(b, c), Times(a, d)),
                                  Subtract(C1, Csc(
                                      Plus(e, Times(f, x)))),
                                  Power(Times(Plus(c, d),
                                      Plus(a, Times(b, Csc(Plus(e, Times(f, x)))))), CN1))),
                          EllipticPi(Times(a, Plus(c, d), Power(Times(c, Plus(a, b)), CN1)),
                              ArcSin(Times(Rt(Times(Plus(a, b), Power(Plus(c, d), CN1)), C2),
                                  Sqrt(Plus(c, Times(d, Csc(Plus(e, Times(f, x)))))),
                                  Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), CN1D2))),
                              Times(Subtract(a, b), Plus(c, d),
                                  Power(Times(Plus(a, b), Subtract(c, d)), CN1))),
                          Power(
                              Times(
                                  c, f, Rt(Times(Plus(a, b), Power(Plus(c, d), CN1)),
                                      C2),
                                  Cot(Plus(e, Times(f, x)))),
                              CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f), x), NeQ(Subtract(Times(b, c), Times(a,
                      d)), C0), NeQ(Subtract(Sqr(a),
                          Sqr(b)), C0),
                      NeQ(Subtract(Sqr(c), Sqr(d)), C0)))),
          IIntegrate(3937,
              Integrate(
                  Times(
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          CN1D2),
                      Power(Plus(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), d_DEFAULT),
                          c_), CN1D2)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Cot(Plus(e, Times(f, x))),
                          Power(
                              Times(
                                  Sqrt(Plus(a, Times(b, Csc(Plus(e, Times(f, x)))))), Sqrt(Plus(c,
                                      Times(d, Csc(Plus(e, Times(f, x))))))),
                              CN1)),
                      Integrate(Power(Cot(Plus(e, Times(f, x))), CN1), x), x),
                  And(FreeQ(List(a, b, c, d, e, f), x), NeQ(Subtract(Times(b, c), Times(a, d)),
                      C0), EqQ(Subtract(Sqr(a), Sqr(b)), C0),
                      EqQ(Subtract(Sqr(c), Sqr(d)), C0)))),
          IIntegrate(3938,
              Integrate(
                  Times(
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
                          CN1D2)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Power(a, CN1),
                          Integrate(
                              Times(
                                  Sqrt(Plus(a, Times(b,
                                      Csc(Plus(e, Times(f, x)))))),
                                  Power(Plus(c, Times(d, Csc(Plus(e, Times(f, x))))), CN1D2)),
                              x),
                          x),
                      Dist(Times(b, Power(a, CN1)),
                          Integrate(
                              Times(Csc(Plus(e, Times(f, x))),
                                  Power(
                                      Times(Sqrt(Plus(a, Times(b, Csc(Plus(e, Times(f, x)))))),
                                          Sqrt(Plus(c, Times(d, Csc(Plus(e, Times(f, x))))))),
                                      CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e,
                      f), x), NeQ(Subtract(Times(b, c), Times(a, d)),
                          C0)))),
          IIntegrate(3939,
              Integrate(
                  Times(
                      Sqrt(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_)),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  d_DEFAULT),
                              c_),
                          QQ(-3L, 2L))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Power(c, CN1),
                          Integrate(
                              Times(
                                  Sqrt(Plus(a, Times(b,
                                      Csc(Plus(e, Times(f, x)))))),
                                  Power(Plus(c, Times(d, Csc(Plus(e, Times(f, x))))), CN1D2)),
                              x),
                          x),
                      Dist(
                          Times(d, Power(c,
                              CN1)),
                          Integrate(
                              Times(Csc(Plus(e, Times(f, x))),
                                  Sqrt(Plus(a, Times(b, Csc(Plus(e, Times(f, x)))))), Power(Plus(c,
                                      Times(d, Csc(Plus(e, Times(f, x))))),
                                      QQ(-3L, 2L))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), NeQ(Subtract(Times(b, c),
                      Times(a, d)), C0), NeQ(Subtract(Sqr(c), Sqr(d)),
                          C0)))),
          IIntegrate(3940,
              Integrate(
                  Times(
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
                  Dist(
                      Times(Sqr(a), Cot(Plus(e, Times(f, x))),
                          Power(Times(f, Sqrt(Plus(a, Times(b, Csc(Plus(e, Times(f, x)))))),
                              Sqrt(Subtract(a, Times(b, Csc(Plus(e, Times(f, x))))))), CN1)),
                      Subst(
                          Integrate(Times(Power(Plus(a, Times(b, x)), Subtract(m, C1D2)),
                              Power(Plus(c, Times(d, x)), n),
                              Power(Times(x, Sqrt(Subtract(a, Times(b, x)))), CN1)), x),
                          x, Csc(Plus(e, Times(f, x)))),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      EqQ(Subtract(Sqr(a), Sqr(b)), C0), NeQ(Subtract(Sqr(c), Sqr(d)), C0),
                      IntegerQ(Subtract(m, C1D2))))));
}
