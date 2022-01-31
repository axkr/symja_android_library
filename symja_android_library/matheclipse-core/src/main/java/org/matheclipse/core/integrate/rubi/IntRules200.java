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
class IntRules200 {
  public static IAST RULES =
      List(
          IIntegrate(4001,
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
                      Plus(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              B_DEFAULT),
                          A_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(BSymbol, Cot(Plus(e, Times(f, x))),
                                  Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))),
                                      m),
                                  Power(Times(f, Plus(m, C1)), CN1)),
                              x)),
                      Dist(
                          Times(
                              Plus(Times(a, BSymbol, m), Times(ASymbol, b, Plus(m, C1))),
                              Power(Times(b, Plus(m, C1)), CN1)),
                          Integrate(
                              Times(Csc(Plus(e, Times(f, x))),
                                  Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), m)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, ASymbol, BSymbol, e, f, m), x),
                      NeQ(Subtract(Times(ASymbol, b), Times(a, BSymbol)), C0),
                      EqQ(Subtract(Sqr(a), Sqr(b)), C0),
                      NeQ(Plus(Times(a, BSymbol, m),
                          Times(ASymbol, b, Plus(m, C1))), C0),
                      Not(LtQ(m, Negate(C1D2)))))),
          IIntegrate(4002,
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
                      Plus(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              B_DEFAULT),
                          A_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(BSymbol, Cot(Plus(e, Times(f, x))),
                                  Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))),
                                      m),
                                  Power(Times(f, Plus(m, C1)), CN1)),
                              x)),
                      Dist(Power(Plus(m, C1), CN1),
                          Integrate(Times(Csc(Plus(e, Times(f, x))),
                              Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), Subtract(m, C1)),
                              Simp(Plus(Times(b, BSymbol, m), Times(a, ASymbol, Plus(m, C1)),
                                  Times(Plus(Times(a, BSymbol, m), Times(ASymbol, b, Plus(m, C1))),
                                      Csc(Plus(e, Times(f, x))))),
                                  x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, ASymbol, BSymbol, e, f), x),
                      NeQ(Subtract(Times(ASymbol, b), Times(a, BSymbol)), C0), NeQ(Subtract(Sqr(a),
                          Sqr(b)), C0),
                      GtQ(m, C0)))),
          IIntegrate(4003,
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
                      Plus(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              B_DEFAULT),
                          A_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(Subtract(Times(ASymbol, b), Times(a, BSymbol)),
                                  Cot(Plus(e,
                                      Times(f, x))),
                                  Power(Plus(a, Times(b,
                                      Csc(Plus(e, Times(f, x))))), Plus(m,
                                          C1)),
                                  Power(Times(f, Plus(m, C1), Subtract(Sqr(a), Sqr(b))), CN1)),
                              x)),
                      Dist(
                          Power(Times(Plus(m, C1),
                              Subtract(Sqr(a), Sqr(b))), CN1),
                          Integrate(
                              Times(Csc(Plus(e, Times(f, x))),
                                  Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), Plus(m, C1)),
                                  Simp(
                                      Subtract(
                                          Times(Subtract(Times(a, ASymbol), Times(b, BSymbol)),
                                              Plus(m, C1)),
                                          Times(Subtract(Times(ASymbol, b), Times(a, BSymbol)),
                                              Plus(m, C2), Csc(Plus(e, Times(f, x))))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, ASymbol, BSymbol, e, f), x),
                      NeQ(Subtract(Times(ASymbol, b), Times(a,
                          BSymbol)), C0),
                      NeQ(Subtract(Sqr(a), Sqr(b)), C0), LtQ(m, CN1)))),
          IIntegrate(4004,
              Integrate(Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                  Power(Plus(Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), b_DEFAULT),
                      a_), CN1D2),
                  Plus(Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), B_DEFAULT), A_)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(CN2, Subtract(Times(ASymbol, b), Times(a, BSymbol)),
                          Rt(Plus(a,
                              Times(b, BSymbol, Power(ASymbol, CN1))), C2),
                          Sqrt(
                              Times(
                                  b, Subtract(C1, Csc(Plus(e, Times(f, x)))), Power(Plus(a, b),
                                      CN1))),
                          Sqrt(Times(CN1, b, Plus(C1, Csc(Plus(e, Times(f, x)))),
                              Power(Subtract(a, b), CN1))),
                          EllipticE(
                              ArcSin(Times(Sqrt(Plus(a, Times(b, Csc(Plus(e, Times(f, x)))))),
                                  Power(Rt(Plus(a, Times(b, BSymbol, Power(ASymbol, CN1))), C2),
                                      CN1))),
                              Times(Plus(Times(a, ASymbol), Times(b, BSymbol)),
                                  Power(Subtract(Times(a, ASymbol), Times(b, BSymbol)), CN1))),
                          Power(Times(Sqr(b), f, Cot(Plus(e, Times(f, x)))), CN1)),
                      x),
                  And(FreeQ(List(a, b, e, f, ASymbol, BSymbol), x),
                      NeQ(Subtract(Sqr(a),
                          Sqr(b)), C0),
                      EqQ(Subtract(Sqr(ASymbol), Sqr(BSymbol)), C0)))),
          IIntegrate(4005,
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
                      Plus(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              B_DEFAULT),
                          A_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(Subtract(ASymbol, BSymbol),
                          Integrate(
                              Times(Csc(Plus(e, Times(f, x))),
                                  Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), CN1D2)),
                              x),
                          x),
                      Dist(BSymbol,
                          Integrate(
                              Times(
                                  Csc(Plus(e, Times(f, x))), Plus(C1, Csc(Plus(e,
                                      Times(f, x)))),
                                  Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), CN1D2)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, e, f, ASymbol, BSymbol), x),
                      NeQ(Subtract(Sqr(a),
                          Sqr(b)), C0),
                      NeQ(Subtract(Sqr(ASymbol), Sqr(BSymbol)), C0)))),
          IIntegrate(4006,
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
                      Plus(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              B_DEFAULT),
                          A_)),
                  x_Symbol),
              Condition(Simp(Times(C2, CSqrt2, ASymbol,
                  Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), m),
                  Subtract(ASymbol, Times(BSymbol, Csc(Plus(e, Times(f, x))))),
                  Sqrt(Times(Plus(ASymbol, Times(BSymbol, Csc(Plus(e, Times(f, x))))),
                      Power(ASymbol, CN1))),
                  AppellF1(C1D2, CN1D2, Negate(m), QQ(3L, 2L),
                      Times(Subtract(ASymbol, Times(BSymbol, Csc(Plus(e, Times(f, x))))),
                          Power(Times(C2, ASymbol), CN1)),
                      Times(b, Subtract(ASymbol, Times(BSymbol, Csc(Plus(e, Times(f, x))))),
                          Power(Plus(Times(ASymbol, b), Times(a, BSymbol)), CN1))),
                  Power(
                      Times(BSymbol, f, Cot(Plus(e, Times(f, x))),
                          Power(
                              Times(ASymbol, Plus(a, Times(b, Csc(Plus(e, Times(f, x))))),
                                  Power(Plus(Times(a, ASymbol), Times(b, BSymbol)), CN1)),
                              m)),
                      CN1)),
                  x),
                  And(FreeQ(List(a, b, ASymbol, BSymbol, e, f), x),
                      NeQ(Subtract(Times(ASymbol, b), Times(a, BSymbol)), C0),
                      NeQ(Subtract(Sqr(a),
                          Sqr(b)), C0),
                      EqQ(Subtract(Sqr(ASymbol), Sqr(BSymbol)), C0), Not(IntegerQ(Times(C2, m)))))),
          IIntegrate(4007,
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
                      Plus(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              B_DEFAULT),
                          A_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(
                          Times(Subtract(Times(ASymbol, b), Times(a,
                              BSymbol)), Power(b,
                                  CN1)),
                          Integrate(
                              Times(Csc(Plus(e, Times(f, x))),
                                  Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), m)),
                              x),
                          x),
                      Dist(
                          Times(BSymbol, Power(b,
                              CN1)),
                          Integrate(
                              Times(
                                  Csc(Plus(e,
                                      Times(f, x))),
                                  Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), Plus(m, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, ASymbol, BSymbol, e, f, m), x),
                      NeQ(Subtract(Times(ASymbol, b),
                          Times(a, BSymbol)), C0),
                      NeQ(Subtract(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(4008,
              Integrate(
                  Times(
                      Sqr($($s("§csc"),
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          m_),
                      Plus(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              B_DEFAULT),
                          A_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(Simp(Times(Subtract(Times(ASymbol, b), Times(a, BSymbol)),
                          Cot(Plus(e, Times(f, x))),
                          Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))),
                              m),
                          Power(Times(b, f, Plus(Times(C2, m), C1)), CN1)), x)),
                      Dist(Power(Times(Sqr(b), Plus(Times(C2, m), C1)), CN1),
                          Integrate(Times(Csc(Plus(e, Times(f, x))),
                              Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), Plus(m, C1)),
                              Simp(Plus(Times(ASymbol, b, m), Times(CN1, a, BSymbol, m),
                                  Times(b, BSymbol, Plus(Times(C2, m), C1),
                                      Csc(Plus(e, Times(f, x))))),
                                  x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, e, f, ASymbol, BSymbol), x),
                      NeQ(Subtract(Times(ASymbol, b), Times(a,
                          BSymbol)), C0),
                      EqQ(Subtract(Sqr(a), Sqr(b)), C0), LtQ(m, Negate(C1D2))))),
          IIntegrate(4009,
              Integrate(
                  Times(
                      Sqr($($s("§csc"),
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          m_),
                      Plus(Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), B_DEFAULT),
                          A_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(a, Subtract(Times(ASymbol, b), Times(a, BSymbol)),
                              Cot(Plus(e, Times(f, x))),
                              Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), Plus(m,
                                  C1)),
                              Power(Times(b, f, Plus(m, C1), Subtract(Sqr(a), Sqr(b))), CN1)),
                          x),
                      Dist(
                          Power(Times(b, Plus(m, C1),
                              Subtract(Sqr(a), Sqr(b))), CN1),
                          Integrate(
                              Times(
                                  Csc(Plus(e,
                                      Times(f, x))),
                                  Power(Plus(a, Times(b,
                                      Csc(Plus(e, Times(f, x))))), Plus(m,
                                          C1)),
                                  Simp(Subtract(
                                      Times(b, Subtract(Times(ASymbol, b), Times(a, BSymbol)),
                                          Plus(m, C1)),
                                      Times(
                                          Subtract(Times(a, ASymbol, b, Plus(m, C2)),
                                              Times(BSymbol,
                                                  Plus(Sqr(a), Times(Sqr(b), Plus(m, C1))))),
                                          Csc(Plus(e, Times(f, x))))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, e, f, ASymbol, BSymbol), x),
                      NeQ(Subtract(Times(ASymbol, b), Times(a, BSymbol)), C0), NeQ(Subtract(Sqr(a),
                          Sqr(b)), C0),
                      LtQ(m, CN1)))),
          IIntegrate(4010,
              Integrate(
                  Times(
                      Sqr($($s("§csc"),
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          m_),
                      Plus(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              B_DEFAULT),
                          A_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(BSymbol, Cot(Plus(e, Times(f, x))),
                                  Power(Plus(a, Times(b,
                                      Csc(Plus(e, Times(f, x))))), Plus(m,
                                          C1)),
                                  Power(Times(b, f, Plus(m, C2)), CN1)),
                              x)),
                      Dist(
                          Power(Times(b,
                              Plus(m, C2)), CN1),
                          Integrate(
                              Times(Csc(Plus(e, Times(f, x))),
                                  Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), m),
                                  Simp(
                                      Plus(Times(b, BSymbol, Plus(m, C1)),
                                          Times(Subtract(Times(ASymbol, b, Plus(m, C2)),
                                              Times(a, BSymbol)), Csc(Plus(e, Times(f, x))))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, e, f, ASymbol, BSymbol,
                      m), x), NeQ(Subtract(Times(ASymbol, b), Times(a, BSymbol)),
                          C0),
                      Not(LtQ(m, CN1))))),
          IIntegrate(4011,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              d_DEFAULT),
                          n_),
                      Power(Plus(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), b_DEFAULT),
                          a_), m_),
                      Plus(Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), B_DEFAULT),
                          A_)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(ASymbol, Cot(Plus(e, Times(f, x))),
                          Power(Plus(a, Times(b,
                              Csc(Plus(e, Times(f, x))))), m),
                          Power(Times(d, Csc(Plus(e, Times(f, x)))), n), Power(Times(f, n), CN1)),
                      x),
                  And(FreeQ(List(a, b, d, e, f, ASymbol, BSymbol, m, n), x),
                      NeQ(Subtract(Times(ASymbol, b), Times(a, BSymbol)), C0),
                      EqQ(Subtract(Sqr(a), Sqr(b)), C0), EqQ(Plus(m, n, C1), C0), EqQ(
                          Subtract(Times(a, ASymbol, m), Times(b, BSymbol, n)), C0)))),
          IIntegrate(4012,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              d_DEFAULT),
                          n_),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          m_),
                      Plus(Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), B_DEFAULT),
                          A_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(Subtract(Times(ASymbol, b), Times(a, BSymbol)),
                                  Cot(Plus(e, Times(f, x))),
                                  Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), m),
                                  Power(Times(d,
                                      Csc(Plus(e, Times(f, x)))), n),
                                  Power(Times(b, f, Plus(Times(C2, m), C1)), CN1)),
                              x)),
                      Dist(
                          Times(
                              Plus(Times(a, ASymbol, m), Times(b, BSymbol, Plus(m, C1))), Power(
                                  Times(Sqr(a), Plus(Times(C2, m), C1)), CN1)),
                          Integrate(
                              Times(
                                  Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), Plus(m,
                                      C1)),
                                  Power(Times(d, Csc(Plus(e, Times(f, x)))), n)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, d, e, f, ASymbol, BSymbol, n), x),
                      NeQ(Subtract(Times(ASymbol, b), Times(a, BSymbol)), C0), EqQ(Subtract(Sqr(a),
                          Sqr(b)), C0),
                      EqQ(Plus(m, n, C1), C0), LeQ(m, CN1)))),
          IIntegrate(4013,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              d_DEFAULT),
                          n_),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          m_),
                      Plus(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              B_DEFAULT),
                          A_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(ASymbol, Cot(Plus(e, Times(f, x))),
                              Power(Plus(a, Times(b,
                                  Csc(Plus(e, Times(f, x))))), m),
                              Power(Times(d, Csc(Plus(e, Times(f, x)))), n), Power(Times(f, n),
                                  CN1)),
                          x),
                      Dist(
                          Times(
                              Subtract(Times(a, ASymbol, m), Times(b, BSymbol, n)),
                              Power(Times(b, d, n), CN1)),
                          Integrate(
                              Times(
                                  Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), m),
                                  Power(Times(d, Csc(Plus(e, Times(f, x)))), Plus(n, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, d, e, f, ASymbol, BSymbol, m, n), x),
                      NeQ(Subtract(Times(ASymbol, b), Times(a, BSymbol)), C0),
                      EqQ(Subtract(Sqr(a), Sqr(
                          b)), C0),
                      EqQ(Plus(m, n, C1), C0), Not(LeQ(m, CN1))))),
          IIntegrate(4014,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              d_DEFAULT),
                          n_),
                      Sqrt(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_)),
                      Plus(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              B_DEFAULT),
                          A_)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(CN2, b, BSymbol, Cot(Plus(e, Times(f, x))),
                          Power(Times(d,
                              Csc(Plus(e, Times(f, x)))), n),
                          Power(
                              Times(
                                  f, Plus(Times(C2, n), C1), Sqrt(Plus(a,
                                      Times(b, Csc(Plus(e, Times(f, x))))))),
                              CN1)),
                      x),
                  And(FreeQ(List(a, b, d, e, f, ASymbol, BSymbol, n), x),
                      NeQ(Subtract(Times(ASymbol, b), Times(a,
                          BSymbol)), C0),
                      EqQ(Subtract(Sqr(a),
                          Sqr(b)), C0),
                      EqQ(Plus(Times(ASymbol, b, Plus(Times(C2, n), C1)), Times(C2, a, BSymbol, n)),
                          C0)))),
          IIntegrate(4015,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              d_DEFAULT),
                          n_),
                      Sqrt(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_)),
                      Plus(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              B_DEFAULT),
                          A_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(ASymbol, Sqr(b), Cot(Plus(e, Times(f, x))),
                              Power(Times(d,
                                  Csc(Plus(e, Times(f, x)))), n),
                              Power(
                                  Times(a, f, n, Sqrt(Plus(a,
                                      Times(b, Csc(Plus(e, Times(f, x))))))),
                                  CN1)),
                          x),
                      Dist(
                          Times(
                              Plus(
                                  Times(ASymbol, b, Plus(Times(C2, n), C1)),
                                  Times(C2, a, BSymbol, n)),
                              Power(Times(C2, a, d, n), CN1)),
                          Integrate(
                              Times(
                                  Sqrt(Plus(a, Times(b, Csc(Plus(e, Times(f, x)))))),
                                  Power(Times(d, Csc(Plus(e, Times(f, x)))), Plus(n, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, d, e, f, ASymbol, BSymbol), x),
                      NeQ(Subtract(Times(ASymbol, b), Times(a, BSymbol)), C0),
                      EqQ(Subtract(Sqr(a), Sqr(b)), C0), NeQ(
                          Plus(Times(ASymbol, b, Plus(Times(C2, n), C1)),
                              Times(C2, a, BSymbol, n)),
                          C0),
                      LtQ(n, C0)))),
          IIntegrate(4016,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              d_DEFAULT),
                          n_),
                      Sqrt(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_)),
                      Plus(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              B_DEFAULT),
                          A_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(
                              CN2, b, BSymbol, Cot(Plus(e, Times(f, x))), Power(Times(d,
                                  Csc(Plus(e, Times(f, x)))), n),
                              Power(
                                  Times(
                                      f, Plus(Times(C2, n), C1), Sqrt(Plus(a,
                                          Times(b, Csc(Plus(e, Times(f, x))))))),
                                  CN1)),
                          x),
                      Dist(
                          Times(
                              Plus(
                                  Times(ASymbol, b, Plus(Times(C2, n), C1)),
                                  Times(C2, a, BSymbol, n)),
                              Power(Times(b, Plus(Times(C2, n), C1)), CN1)),
                          Integrate(
                              Times(Sqrt(Plus(a, Times(b, Csc(Plus(e, Times(f, x)))))),
                                  Power(Times(d, Csc(Plus(e, Times(f, x)))), n)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, d, e, f, ASymbol, BSymbol, n), x),
                      NeQ(Subtract(Times(ASymbol, b), Times(a, BSymbol)), C0),
                      EqQ(Subtract(Sqr(a),
                          Sqr(b)), C0),
                      NeQ(Plus(Times(ASymbol, b, Plus(Times(C2, n), C1)),
                          Times(C2, a, BSymbol, n)), C0),
                      Not(LtQ(n, C0))))),
          IIntegrate(4017,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              d_DEFAULT),
                          n_),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          m_),
                      Plus(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              B_DEFAULT),
                          A_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              a, ASymbol, Cot(Plus(e,
                                  Times(f, x))),
                              Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), Subtract(m, C1)),
                              Power(Times(d, Csc(Plus(e, Times(f, x)))), n), Power(Times(f, n),
                                  CN1)),
                          x),
                      Dist(
                          Times(b, Power(Times(a, d, n),
                              CN1)),
                          Integrate(Times(
                              Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), Subtract(m, C1)),
                              Power(Times(d, Csc(Plus(e, Times(f, x)))), Plus(n, C1)),
                              Simp(Subtract(
                                  Subtract(Times(a, ASymbol, Subtract(Subtract(m, n), C1)),
                                      Times(b, BSymbol, n)),
                                  Times(Plus(Times(a, BSymbol, n), Times(ASymbol, b, Plus(m, n))),
                                      Csc(Plus(e, Times(f, x))))),
                                  x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, d, e, f, ASymbol, BSymbol), x),
                      NeQ(Subtract(Times(ASymbol, b), Times(a, BSymbol)), C0),
                      EqQ(Subtract(Sqr(a), Sqr(b)), C0), GtQ(m, C1D2), LtQ(n, CN1)))),
          IIntegrate(4018,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              d_DEFAULT),
                          n_),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          m_),
                      Plus(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              B_DEFAULT),
                          A_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(
                                  b, BSymbol, Cot(Plus(e,
                                      Times(f, x))),
                                  Power(
                                      Plus(a, Times(b,
                                          Csc(Plus(e, Times(f, x))))),
                                      Subtract(m, C1)),
                                  Power(Times(d,
                                      Csc(Plus(e, Times(f, x)))), n),
                                  Power(Times(f, Plus(m, n)), CN1)),
                              x)),
                      Dist(Power(Times(d, Plus(m, n)), CN1),
                          Integrate(
                              Times(
                                  Power(
                                      Plus(a, Times(b, Csc(
                                          Plus(e, Times(f, x))))),
                                      Subtract(m, C1)),
                                  Power(Times(d, Csc(Plus(e, Times(f, x)))), n),
                                  Simp(Plus(Times(a, ASymbol, d, Plus(m, n)),
                                      Times(BSymbol, b, d, n),
                                      Times(
                                          Plus(Times(ASymbol, b, d, Plus(m, n)),
                                              Times(a, BSymbol, d,
                                                  Subtract(Plus(Times(C2, m), n), C1))),
                                          Csc(Plus(e, Times(f, x))))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, d, e, f, ASymbol, BSymbol, n), x),
                      NeQ(Subtract(Times(ASymbol, b), Times(a, BSymbol)), C0), EqQ(Subtract(Sqr(a),
                          Sqr(b)), C0),
                      GtQ(m, C1D2), Not(LtQ(n, CN1))))),
          IIntegrate(4019,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              d_DEFAULT),
                          n_),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          m_),
                      Plus(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              B_DEFAULT),
                          A_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(d, Subtract(Times(ASymbol, b), Times(a, BSymbol)),
                              Cot(Plus(e, Times(f, x))),
                              Power(Plus(a, Times(b, Csc(
                                  Plus(e, Times(f, x))))), m),
                              Power(Times(d, Csc(
                                  Plus(e, Times(f, x)))), Subtract(n,
                                      C1)),
                              Power(Times(a, f, Plus(Times(C2, m), C1)), CN1)),
                          x),
                      Dist(Power(Times(a, b, Plus(Times(C2, m), C1)), CN1),
                          Integrate(
                              Times(
                                  Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), Plus(m, C1)),
                                  Power(Times(d, Csc(Plus(e, Times(f, x)))), Subtract(n, C1)),
                                  Simp(Subtract(
                                      Subtract(Times(ASymbol, a, d, Subtract(n, C1)),
                                          Times(BSymbol, b, d, Subtract(n, C1))),
                                      Times(d,
                                          Plus(Times(a, BSymbol, Plus(m, Negate(n), C1)),
                                              Times(ASymbol, b, Plus(m, n))),
                                          Csc(Plus(e, Times(f, x))))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, d, e, f, ASymbol, BSymbol), x),
                      NeQ(Subtract(Times(ASymbol, b), Times(a,
                          BSymbol)), C0),
                      EqQ(Subtract(Sqr(a), Sqr(b)), C0), LtQ(m, Negate(C1D2)), GtQ(n, C0)))),
          IIntegrate(4020,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              d_DEFAULT),
                          n_),
                      Power(
                          Plus(Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              b_DEFAULT), a_),
                          m_),
                      Plus(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              B_DEFAULT),
                          A_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Negate(
                          Simp(
                              Times(Subtract(Times(ASymbol, b), Times(a, BSymbol)),
                                  Cot(Plus(e, Times(f, x))),
                                  Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), m),
                                  Power(Times(d,
                                      Csc(Plus(e, Times(f, x)))), n),
                                  Power(Times(b, f, Plus(Times(C2, m), C1)), CN1)),
                              x)),
                      Dist(Power(Times(Sqr(a), Plus(Times(C2, m), C1)), CN1),
                          Integrate(Times(
                              Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), Plus(m, C1)),
                              Power(Times(d, Csc(Plus(e, Times(f, x)))), n),
                              Simp(Plus(Times(b, BSymbol, n),
                                  Times(CN1, a, ASymbol, Plus(Times(C2, m), n, C1)),
                                  Times(Subtract(Times(ASymbol, b), Times(a, BSymbol)),
                                      Plus(m, n, C1), Csc(Plus(e, Times(f, x))))),
                                  x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, d, e, f, ASymbol, BSymbol, n), x),
                      NeQ(Subtract(Times(ASymbol, b), Times(a, BSymbol)), C0),
                      EqQ(Subtract(Sqr(a), Sqr(b)), C0), LtQ(m, Negate(C1D2)), Not(GtQ(n, C0))))));
}
