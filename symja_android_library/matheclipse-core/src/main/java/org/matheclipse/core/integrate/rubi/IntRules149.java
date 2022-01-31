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
class IntRules149 {
  public static IAST RULES =
      List(
          IIntegrate(2981,
              Integrate(
                  Times(
                      Sqrt(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))))),
                      Plus(
                          A_DEFAULT, Times(B_DEFAULT,
                              $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      Power(
                          Plus(c_DEFAULT,
                              Times(d_DEFAULT, $($s("§sin"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(CN2, b, BSymbol, Cos(Plus(e, Times(f, x))),
                              Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), Plus(n,
                                  C1)),
                              Power(
                                  Times(d, f, Plus(
                                      Times(C2, n), C3),
                                      Sqrt(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))))),
                                  CN1)),
                          x),
                      Dist(
                          Times(
                              Subtract(Times(ASymbol, b, d, Plus(Times(C2, n), C3)), Times(BSymbol,
                                  Subtract(Times(b, c), Times(C2, a, d, Plus(n, C1))))),
                              Power(Times(b, d, Plus(Times(C2, n), C3)), CN1)),
                          Integrate(Times(Sqrt(Plus(a, Times(b, Sin(Plus(e, Times(f, x)))))), Power(
                              Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), n)), x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol, n), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      EqQ(Subtract(Sqr(a),
                          Sqr(b)), C0),
                      NeQ(Subtract(Sqr(c), Sqr(d)), C0), Not(LtQ(n, CN1))))),
          IIntegrate(2982,
              Integrate(
                  Times(
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          CN1D2),
                      Plus(
                          A_DEFAULT, Times(B_DEFAULT,
                              $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      Power(
                          Plus(c_DEFAULT,
                              Times(d_DEFAULT, $($s("§sin"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          CN1D2)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(Times(Subtract(Times(ASymbol, b), Times(a, BSymbol)), Power(b, CN1)),
                          Integrate(Power(
                              Times(Sqrt(Plus(a, Times(b, Sin(Plus(e, Times(f, x)))))),
                                  Sqrt(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))))),
                              CN1), x),
                          x),
                      Dist(Times(BSymbol, Power(b, CN1)),
                          Integrate(
                              Times(
                                  Sqrt(Plus(a, Times(b,
                                      Sin(Plus(e, Times(f, x)))))),
                                  Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), CN1D2)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0), EqQ(Subtract(Sqr(a), Sqr(
                          b)), C0),
                      NeQ(Subtract(Sqr(c), Sqr(d)), C0)))),
          IIntegrate(2983,
              Integrate(
                  Times(
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT,
                                  x_))))),
                          m_),
                      Plus(
                          A_DEFAULT, Times(B_DEFAULT,
                              $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      Power(
                          Plus(c_DEFAULT,
                              Times(d_DEFAULT, $($s("§sin"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(BSymbol, Cos(Plus(e, Times(f, x))),
                                  Power(Plus(a, Times(b,
                                      Sin(Plus(e, Times(f, x))))), m),
                                  Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), n),
                                  Power(Times(f, Plus(m, n, C1)), CN1)),
                              x)),
                      Dist(
                          Power(Times(b,
                              Plus(m, n, C1)), CN1),
                          Integrate(
                              Times(Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), m),
                                  Power(
                                      Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), Subtract(n,
                                          C1)),
                                  Simp(
                                      Plus(Times(ASymbol, b, c, Plus(m, n, C1)),
                                          Times(BSymbol, Plus(Times(a, c, m), Times(b, d, n))),
                                          Times(
                                              Plus(Times(ASymbol, b, d, Plus(m, n, C1)),
                                                  Times(BSymbol,
                                                      Plus(Times(a, d, m), Times(b, c, n)))),
                                              Sin(Plus(e, Times(f, x))))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol, m), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      EqQ(Subtract(Sqr(a), Sqr(
                          b)), C0),
                      NeQ(Subtract(Sqr(c),
                          Sqr(d)), C0),
                      GtQ(n, C0), Or(IntegerQ(n), EqQ(Plus(m, C1D2), C0))))),
          IIntegrate(2984,
              Integrate(
                  Times(
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Plus(A_DEFAULT, Times(B_DEFAULT,
                          $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      Power(
                          Plus(c_DEFAULT,
                              Times(d_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Subtract(Times(BSymbol, c), Times(ASymbol, d)),
                              Cos(Plus(e, Times(f,
                                  x))),
                              Power(Plus(a, Times(b, Sin(
                                  Plus(e, Times(f, x))))), m),
                              Power(Plus(c, Times(d,
                                  Sin(Plus(e, Times(f, x))))), Plus(n,
                                      C1)),
                              Power(Times(f, Plus(n, C1), Subtract(Sqr(c), Sqr(d))), CN1)),
                          x),
                      Dist(Power(Times(b, Plus(n, C1), Subtract(Sqr(c), Sqr(d))), CN1),
                          Integrate(
                              Times(Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), m),
                                  Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), Plus(n, C1)),
                                  Simp(Plus(
                                      Times(ASymbol,
                                          Plus(Times(a, d, m), Times(b, c, Plus(n, C1)))),
                                      Times(CN1, BSymbol,
                                          Plus(Times(a, c, m), Times(b, d, Plus(n, C1)))),
                                      Times(b, Subtract(Times(BSymbol, c), Times(ASymbol, d)),
                                          Plus(m, n, C2), Sin(Plus(e, Times(f, x))))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol, m), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      EqQ(Subtract(Sqr(a), Sqr(b)), C0), NeQ(Subtract(Sqr(c),
                          Sqr(d)), C0),
                      LtQ(n, CN1), Or(IntegerQ(n), EqQ(Plus(m, C1D2), C0))))),
          IIntegrate(2985,
              Integrate(
                  Times(
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          CN1D2),
                      Plus(
                          A_DEFAULT, Times(B_DEFAULT,
                              $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      Power(
                          Plus(c_DEFAULT,
                              Times(d_DEFAULT, $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT,
                                  x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(
                          Times(Subtract(Times(ASymbol, b), Times(a,
                              BSymbol)), Power(Subtract(Times(b, c), Times(a, d)),
                                  CN1)),
                          Integrate(Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), CN1D2),
                              x),
                          x),
                      Dist(
                          Times(
                              Subtract(Times(BSymbol, c), Times(ASymbol, d)), Power(
                                  Subtract(Times(b, c), Times(a, d)), CN1)),
                          Integrate(
                              Times(
                                  Sqrt(Plus(a, Times(b, Sin(Plus(e, Times(f, x)))))), Power(Plus(c,
                                      Times(d, Sin(Plus(e, Times(f, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0), EqQ(Subtract(Sqr(a), Sqr(b)),
                          C0),
                      NeQ(Subtract(Sqr(c), Sqr(d)), C0)))),
          IIntegrate(2986,
              Integrate(
                  Times(
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Plus(
                          A_DEFAULT, Times(B_DEFAULT,
                              $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      Power(
                          Plus(c_DEFAULT,
                              Times(d_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(
                          Times(BSymbol, Power(d, CN1)), Integrate(
                              Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), m), x),
                          x),
                      Dist(
                          Times(Subtract(Times(BSymbol, c), Times(ASymbol,
                              d)), Power(d,
                                  CN1)),
                          Integrate(
                              Times(Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), m),
                                  Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol, m), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      EqQ(Subtract(Sqr(a), Sqr(
                          b)), C0),
                      NeQ(Subtract(Sqr(c), Sqr(d)), C0), NeQ(Plus(m, C1D2), C0)))),
          IIntegrate(2987,
              Integrate(
                  Times(
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_DEFAULT),
                      Plus(
                          A_DEFAULT, Times(B_DEFAULT,
                              $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      Power(
                          Plus(c_DEFAULT,
                              Times(d_DEFAULT, $($s("§sin"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(
                          Times(Subtract(Times(ASymbol, b), Times(a,
                              BSymbol)), Power(b,
                                  CN1)),
                          Integrate(
                              Times(Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), m),
                                  Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), n)),
                              x),
                          x),
                      Dist(Times(BSymbol, Power(b, CN1)),
                          Integrate(
                              Times(Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Plus(m,
                                  C1)), Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))),
                                      n)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol, m, n), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      EqQ(Subtract(Sqr(a), Sqr(b)), C0), NeQ(Subtract(Sqr(c),
                          Sqr(d)), C0),
                      NeQ(Plus(Times(ASymbol, b), Times(a, BSymbol)), C0)))),
          IIntegrate(2988,
              Integrate(
                  Times(
                      Sqr(Plus(
                          a_DEFAULT, Times(b_DEFAULT,
                              $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))))),
                      Plus(
                          A_DEFAULT, Times(B_DEFAULT,
                              $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      Power(
                          Plus(c_DEFAULT,
                              Times(d_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Subtract(Times(BSymbol, c), Times(ASymbol, d)),
                              Sqr(Subtract(Times(b, c), Times(a, d))), Cos(Plus(e, Times(f, x))),
                              Power(Plus(c, Times(d,
                                  Sin(Plus(e, Times(f, x))))), Plus(n,
                                      C1)),
                              Power(Times(f, Sqr(d), Plus(n, C1), Subtract(Sqr(c), Sqr(d))), CN1)),
                          x),
                      Dist(
                          Power(Times(Sqr(d), Plus(n, C1),
                              Subtract(Sqr(c), Sqr(d))), CN1),
                          Integrate(
                              Times(
                                  Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), Plus(n,
                                      C1)),
                                  Simp(
                                      Subtract(
                                          Subtract(
                                              Times(d, Plus(n, C1),
                                                  Subtract(
                                                      Times(
                                                          BSymbol, Sqr(Subtract(Times(b,
                                                              c),
                                                              Times(a, d)))),
                                                      Times(ASymbol, d, Subtract(Plus(
                                                          Times(Sqr(a), c), Times(Sqr(b), c)),
                                                          Times(C2, a, b, d))))),
                                              Times(
                                                  Plus(
                                                      Times(
                                                          Subtract(Times(BSymbol, c),
                                                              Times(ASymbol, d)),
                                                          Plus(Times(Sqr(a), Sqr(d), Plus(n, C2)),
                                                              Times(Sqr(b),
                                                                  Plus(Sqr(c),
                                                                      Times(Sqr(d),
                                                                          Plus(n, C1)))))),
                                                      Times(C2, a, b, d, Subtract(
                                                          Times(ASymbol, c, d, Plus(n, C2)),
                                                          Times(BSymbol,
                                                              Plus(Sqr(c),
                                                                  Times(Sqr(d), Plus(n, C1))))))),
                                                  Sin(Plus(e, Times(f, x))))),
                                          Times(Sqr(b), BSymbol, d, Plus(n, C1),
                                              Subtract(Sqr(c), Sqr(d)),
                                              Sqr(Sin(Plus(e, Times(f, x)))))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      NeQ(Subtract(Sqr(a), Sqr(b)), C0), NeQ(Subtract(Sqr(c),
                          Sqr(d)), C0),
                      LtQ(n, CN1)))),
          IIntegrate(2989,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Plus(
                          A_DEFAULT, Times(B_DEFAULT,
                              $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      Power(
                          Plus(c_DEFAULT,
                              Times(d_DEFAULT, $($s("§sin"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(Subtract(Times(b, c), Times(a, d)),
                                  Subtract(Times(BSymbol, c), Times(ASymbol, d)),
                                  Cos(Plus(e,
                                      Times(f, x))),
                                  Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Subtract(m,
                                      C1)),
                                  Power(Plus(c, Times(d,
                                      Sin(Plus(e, Times(f, x))))), Plus(n,
                                          C1)),
                                  Power(Times(d, f, Plus(n, C1), Subtract(Sqr(c), Sqr(d))), CN1)),
                              x)),
                      Dist(
                          Power(Times(d, Plus(n, C1), Subtract(Sqr(c), Sqr(d))), CN1), Integrate(
                              Times(
                                  Power(
                                      Plus(a, Times(b,
                                          Sin(Plus(e, Times(f, x))))),
                                      Subtract(m, C2)),
                                  Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), Plus(n, C1)),
                                  Simp(
                                      Plus(
                                          Times(b, Subtract(Times(b, c), Times(a, d)),
                                              Subtract(Times(BSymbol, c), Times(ASymbol,
                                                  d)),
                                              Subtract(m, C1)),
                                          Times(a, d,
                                              Subtract(Plus(Times(a, ASymbol, c), Times(b, BSymbol,
                                                  c)), Times(
                                                      Plus(Times(ASymbol, b), Times(a, BSymbol)),
                                                      d)),
                                              Plus(n, C1)),
                                          Times(
                                              Subtract(
                                                  Times(b,
                                                      Plus(
                                                          Times(b, d,
                                                              Subtract(Times(BSymbol, c),
                                                                  Times(ASymbol, d))),
                                                          Times(a,
                                                              Plus(Times(ASymbol, c, d),
                                                                  Times(BSymbol,
                                                                      Subtract(Sqr(c),
                                                                          Times(C2, Sqr(d))))))),
                                                      Plus(n, C1)),
                                                  Times(a, Subtract(Times(b, c), Times(a, d)),
                                                      Subtract(Times(BSymbol, c),
                                                          Times(ASymbol, d)),
                                                      Plus(n, C2))),
                                              Sin(Plus(e, Times(f, x)))),
                                          Times(b,
                                              Subtract(
                                                  Times(d,
                                                      Subtract(
                                                          Plus(Times(ASymbol, b, c),
                                                              Times(a, BSymbol, c)),
                                                          Times(a, ASymbol, d)),
                                                      Plus(m, n, C1)),
                                                  Times(b, BSymbol,
                                                      Plus(Times(Sqr(c), m),
                                                          Times(Sqr(d), Plus(n, C1))))),
                                              Sqr(Sin(Plus(e, Times(f, x)))))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      NeQ(Subtract(Sqr(a), Sqr(b)), C0), NeQ(Subtract(Sqr(c), Sqr(d)), C0), GtQ(m,
                          C1),
                      LtQ(n, CN1)))),
          IIntegrate(2990,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Plus(
                          A_DEFAULT, Times(B_DEFAULT,
                              $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
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
                              Times(
                                  b, BSymbol, Cos(Plus(e,
                                      Times(f, x))),
                                  Power(
                                      Plus(a, Times(b,
                                          Sin(Plus(e, Times(f, x))))),
                                      Subtract(m, C1)),
                                  Power(Plus(c, Times(d,
                                      Sin(Plus(e, Times(f, x))))), Plus(n,
                                          C1)),
                                  Power(Times(d, f, Plus(m, n, C1)), CN1)),
                              x)),
                      Dist(
                          Power(Times(d, Plus(m, n, C1)), CN1), Integrate(
                              Times(
                                  Power(
                                      Plus(a, Times(b,
                                          Sin(Plus(e, Times(f, x))))),
                                      Subtract(m, C2)),
                                  Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), n),
                                  Simp(
                                      Plus(Times(Sqr(a), ASymbol, d, Plus(m, n, C1)),
                                          Times(b, BSymbol,
                                              Plus(Times(b, c, Subtract(m, C1)),
                                                  Times(a, d, Plus(n, C1)))),
                                          Times(
                                              Subtract(
                                                  Times(a, d,
                                                      Plus(Times(C2, ASymbol, b),
                                                          Times(a, BSymbol)),
                                                      Plus(m, n, C1)),
                                                  Times(b, BSymbol,
                                                      Subtract(Times(a, c),
                                                          Times(b, d, Plus(m, n))))),
                                              Sin(Plus(e, Times(f, x)))),
                                          Times(b,
                                              Subtract(Times(ASymbol, b, d, Plus(m, n, C1)),
                                                  Times(BSymbol,
                                                      Subtract(Times(b, c, m),
                                                          Times(a, d, Plus(Times(C2, m), n))))),
                                              Sqr(Sin(Plus(e, Times(f, x)))))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol, n), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      NeQ(Subtract(Sqr(a), Sqr(
                          b)), C0),
                      NeQ(Subtract(Sqr(c), Sqr(d)), C0), GtQ(m, C1), Not(And(IGtQ(n, C1), Or(
                          Not(IntegerQ(m)), And(EqQ(a, C0), NeQ(c, C0)))))))),
          IIntegrate(2991,
              Integrate(
                  Times(
                      Power(
                          Times(b_DEFAULT, $($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          QQ(-3L, 2L)),
                      Plus(
                          A_DEFAULT, Times(B_DEFAULT,
                              $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      Sqrt(
                          Plus(c_,
                              Times(
                                  d_DEFAULT, $($s("§sin"), Plus(e_DEFAULT,
                                      Times(f_DEFAULT, x_))))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(
                          Times(BSymbol, d, Power(b, CN2)), Integrate(
                              Times(
                                  Sqrt(Times(b, Sin(Plus(e, Times(f, x))))),
                                  Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), CN1D2)),
                              x),
                          x),
                      Integrate(
                          Times(
                              Plus(Times(ASymbol, c),
                                  Times(
                                      Plus(Times(BSymbol, c), Times(ASymbol, d)),
                                      Sin(Plus(e, Times(f, x))))),
                              Power(Times(Power(Times(b, Sin(Plus(e, Times(f, x)))), QQ(3L, 2L)),
                                  Sqrt(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))))),
                                  CN1)),
                          x)),
                  And(FreeQ(List(b, c, d, e, f, ASymbol,
                      BSymbol), x), NeQ(Subtract(Sqr(c), Sqr(d)),
                          C0)))),
          IIntegrate(2992,
              Integrate(
                  Times(
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          QQ(-3L, 2L)),
                      Plus(
                          A_DEFAULT, Times(B_DEFAULT,
                              $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      Sqrt(
                          Plus(c_DEFAULT,
                              Times(d_DEFAULT, $($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(
                          Times(BSymbol, Power(b,
                              CN1)),
                          Integrate(
                              Times(
                                  Sqrt(Plus(c, Times(d,
                                      Sin(Plus(e, Times(f, x)))))),
                                  Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), CN1D2)),
                              x),
                          x),
                      Dist(
                          Times(Subtract(Times(ASymbol, b), Times(a,
                              BSymbol)), Power(b,
                                  CN1)),
                          Integrate(
                              Times(
                                  Sqrt(Plus(c, Times(d, Sin(Plus(e, Times(f, x)))))), Power(Plus(a,
                                      Times(b, Sin(Plus(e, Times(f, x))))),
                                      QQ(-3L, 2L))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0), NeQ(Subtract(Sqr(a), Sqr(
                          b)), C0),
                      NeQ(Subtract(Sqr(c), Sqr(d)), C0)))),
          IIntegrate(2993,
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
                          QQ(-3L, 2L)),
                      Plus(
                          A_DEFAULT,
                          Times(B_DEFAULT, $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(C2, Subtract(Times(ASymbol, b), Times(a, BSymbol)),
                              Cos(Plus(e,
                                  Times(f, x))),
                              Power(
                                  Times(f, Subtract(Sqr(a), Sqr(b)),
                                      Sqrt(Plus(a, Times(b,
                                          Sin(Plus(e, Times(f, x)))))),
                                      Sqrt(Times(d, Sin(Plus(e, Times(f, x)))))),
                                  CN1)),
                          x),
                      Dist(Times(d, Power(Subtract(Sqr(a), Sqr(b)), CN1)),
                          Integrate(
                              Times(
                                  Plus(Times(ASymbol, b), Times(CN1, a, BSymbol),
                                      Times(Subtract(Times(a, ASymbol), Times(b, BSymbol)),
                                          Sin(Plus(e, Times(f, x))))),
                                  Power(
                                      Times(Sqrt(Plus(a, Times(b, Sin(Plus(e, Times(f, x)))))),
                                          Power(Times(d, Sin(Plus(e, Times(f, x)))), QQ(3L, 2L))),
                                      CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, d, e, f, ASymbol, BSymbol), x),
                      NeQ(Subtract(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(2994,
              Integrate(
                  Times(
                      Power(
                          Times(b_DEFAULT, $($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          QQ(-3L, 2L)),
                      Plus(
                          A_,
                          Times(B_DEFAULT, $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      Power(Plus(
                          c_,
                          Times(d_DEFAULT, $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          CN1D2)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(
                          CN2, ASymbol, Subtract(c, d), Tan(Plus(e, Times(f, x))), Rt(Times(
                              Plus(c, d), Power(b, CN1)), C2),
                          Sqrt(
                              Times(
                                  c, Plus(C1, Csc(
                                      Plus(e, Times(f, x)))),
                                  Power(Subtract(c, d), CN1))),
                          Sqrt(
                              Times(
                                  c, Subtract(C1, Csc(Plus(e, Times(f,
                                      x)))),
                                  Power(Plus(c, d), CN1))),
                          EllipticE(
                              ArcSin(Times(Sqrt(Plus(c, Times(d, Sin(Plus(e, Times(f, x)))))),
                                  Power(Times(Sqrt(Times(b, Sin(Plus(e, Times(f, x))))),
                                      Rt(Times(Plus(c, d), Power(b, CN1)), C2)), CN1))),
                              Times(CN1, Plus(c, d), Power(Subtract(c, d), CN1))),
                          Power(Times(f, b, Sqr(c)), CN1)),
                      x),
                  And(FreeQ(List(b, c, d, e, f, ASymbol, BSymbol), x),
                      NeQ(Subtract(Sqr(c), Sqr(d)), C0), EqQ(ASymbol, BSymbol), PosQ(Times(Plus(c,
                          d), Power(b, CN1)))))),
          IIntegrate(2995,
              Integrate(
                  Times(
                      Power(
                          Times(b_DEFAULT, $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          QQ(-3L, 2L)),
                      Plus(
                          A_, Times(B_DEFAULT,
                              $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      Power(
                          Plus(c_,
                              Times(d_DEFAULT, $($s("§sin"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          CN1D2)),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(
                          Times(Sqrt(Times(CN1, b, Sin(Plus(e, Times(f, x))))), Power(
                              Times(b, Sin(Plus(e, Times(f, x)))), CN1D2)),
                          Integrate(
                              Times(
                                  Plus(ASymbol, Times(BSymbol, Sin(Plus(e, Times(f, x))))), Power(
                                      Times(
                                          Power(Times(CN1, b, Sin(Plus(e, Times(f, x)))),
                                              QQ(3L, 2L)),
                                          Sqrt(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))))),
                                      CN1)),
                              x),
                          x)),
                  And(FreeQ(List(b, c, d, e, f, ASymbol, BSymbol), x), NeQ(Subtract(Sqr(c), Sqr(d)),
                      C0), EqQ(ASymbol, BSymbol), NegQ(Times(Plus(c, d), Power(b, CN1)))))),
          IIntegrate(2996,
              Integrate(
                  Times(
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s(
                                  "§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          QQ(-3L, 2L)),
                      Plus(
                          A_, Times(B_DEFAULT,
                              $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      Power(
                          Plus(c_,
                              Times(d_DEFAULT, $($s("§sin"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          CN1D2)),
                  x_Symbol),
              Condition(Simp(Times(
                  CN2, ASymbol, Subtract(c, d), Plus(a, Times(b,
                      Sin(Plus(e, Times(f, x))))),
                  Sqrt(
                      Times(
                          Subtract(Times(b, c), Times(a, d)), Plus(C1, Sin(
                              Plus(e, Times(f, x)))),
                          Power(
                              Times(Subtract(c, d),
                                  Plus(a, Times(b, Sin(Plus(e, Times(f, x)))))),
                              CN1))),
                  Sqrt(Times(CN1, Subtract(Times(b, c), Times(a, d)),
                      Subtract(C1, Sin(Plus(e, Times(f, x)))),
                      Power(Times(Plus(c, d), Plus(a, Times(b, Sin(Plus(e, Times(f, x)))))), CN1))),
                  EllipticE(ArcSin(Times(Rt(Times(Plus(a, b), Power(Plus(c, d), CN1)), C2),
                      Sqrt(Plus(c, Times(d, Sin(Plus(e, Times(f, x)))))),
                      Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), CN1D2))), Times(
                          Subtract(a, b), Plus(c, d),
                          Power(Times(Plus(a, b), Subtract(c, d)), CN1))),
                  Power(
                      Times(f, Sqr(Subtract(Times(b, c), Times(a, d))),
                          Rt(Times(Plus(a, b), Power(Plus(c, d), CN1)), C2),
                          Cos(Plus(e, Times(f, x)))),
                      CN1)),
                  x),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      NeQ(Subtract(Sqr(a), Sqr(b)), C0), NeQ(Subtract(Sqr(c), Sqr(d)),
                          C0),
                      EqQ(ASymbol, BSymbol), PosQ(Times(Plus(a, b), Power(Plus(c, d), CN1)))))),
          IIntegrate(2997,
              Integrate(
                  Times(
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          QQ(-3L, 2L)),
                      Plus(
                          A_, Times(B_DEFAULT,
                              $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      Power(
                          Plus(c_,
                              Times(d_DEFAULT, $($s("§sin"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          CN1D2)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Sqrt(Subtract(Negate(c),
                              Times(d, Sin(Plus(e, Times(f, x)))))),
                          Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), CN1D2)),
                      Integrate(
                          Times(
                              Plus(ASymbol, Times(BSymbol,
                                  Sin(Plus(e, Times(f, x))))),
                              Power(
                                  Times(
                                      Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))),
                                          QQ(3L, 2L)),
                                      Sqrt(
                                          Subtract(Negate(c), Times(d,
                                              Sin(Plus(e, Times(f, x))))))),
                                  CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol), x),
                      NeQ(Subtract(Times(b, c),
                          Times(a, d)), C0),
                      NeQ(Subtract(Sqr(a), Sqr(b)), C0), NeQ(Subtract(Sqr(c), Sqr(d)),
                          C0),
                      EqQ(ASymbol, BSymbol), NegQ(Times(Plus(a, b), Power(Plus(c, d), CN1)))))),
          IIntegrate(2998,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          QQ(-3L, 2L)),
                      Plus(
                          A_DEFAULT, Times(B_DEFAULT,
                              $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      Power(
                          Plus(c_,
                              Times(d_DEFAULT, $($s("§sin"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          CN1D2)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(
                          Times(Subtract(ASymbol, BSymbol), Power(Subtract(a, b),
                              CN1)),
                          Integrate(
                              Power(
                                  Times(Sqrt(Plus(a, Times(b, Sin(
                                      Plus(e, Times(f, x)))))), Sqrt(Plus(c, Times(d,
                                          Sin(Plus(e, Times(f, x))))))),
                                  CN1),
                              x),
                          x),
                      Dist(
                          Times(
                              Subtract(Times(ASymbol, b), Times(a,
                                  BSymbol)),
                              Power(Subtract(a, b), CN1)),
                          Integrate(
                              Times(Plus(C1, Sin(Plus(e, Times(f, x)))),
                                  Power(
                                      Times(
                                          Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))),
                                              QQ(3L, 2L)),
                                          Sqrt(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))))),
                                      CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      NeQ(Subtract(Sqr(a), Sqr(b)), C0), NeQ(Subtract(Sqr(c), Sqr(d)),
                          C0),
                      NeQ(ASymbol, BSymbol)))),
          IIntegrate(2999,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Plus(
                          A_DEFAULT, Times(B_DEFAULT,
                              $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      Power(
                          Plus(c_DEFAULT,
                              Times(d_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Subtract(Times(BSymbol, a), Times(ASymbol, b)),
                              Cos(Plus(e, Times(f, x))),
                              Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Plus(m, C1)),
                              Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), n), Power(
                                  Times(f, Plus(m, C1), Subtract(Sqr(a), Sqr(b))), CN1)),
                          x),
                      Dist(
                          Power(Times(Plus(m, C1),
                              Subtract(Sqr(a), Sqr(b))), CN1),
                          Integrate(
                              Times(Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Plus(m,
                                  C1)), Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))),
                                      Subtract(n, C1)),
                                  Simp(
                                      Subtract(
                                          Plus(
                                              Times(
                                                  c, Subtract(Times(a, ASymbol), Times(b, BSymbol)),
                                                  Plus(m, C1)),
                                              Times(d, n,
                                                  Subtract(Times(ASymbol, b), Times(a, BSymbol))),
                                              Times(
                                                  Subtract(
                                                      Times(d,
                                                          Subtract(Times(a, ASymbol),
                                                              Times(b, BSymbol)),
                                                          Plus(m, C1)),
                                                      Times(c,
                                                          Subtract(Times(ASymbol, b),
                                                              Times(a, BSymbol)),
                                                          Plus(m, C2))),
                                                  Sin(Plus(e, Times(f, x))))),
                                          Times(d, Subtract(Times(ASymbol, b), Times(a, BSymbol)),
                                              Plus(m, n, C2), Sqr(Sin(Plus(e, Times(f, x)))))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      NeQ(Subtract(Sqr(a), Sqr(
                          b)), C0),
                      NeQ(Subtract(Sqr(c), Sqr(d)), C0), LtQ(m, CN1), GtQ(n, C0)))),
          IIntegrate(3000,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Plus(
                          A_DEFAULT, Times(B_DEFAULT,
                              $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      Power(Plus(
                          c_DEFAULT,
                          Times(d_DEFAULT, $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(Subtract(Times(ASymbol, Sqr(b)), Times(a, b, BSymbol)),
                                  Cos(Plus(e, Times(f, x))),
                                  Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Plus(m, C1)),
                                  Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), Plus(C1, n)),
                                  Power(
                                      Times(f, Plus(m, C1), Subtract(Times(b, c), Times(a, d)),
                                          Subtract(Sqr(a), Sqr(b))),
                                      CN1)),
                              x)),
                      Dist(
                          Power(Times(
                              Plus(m, C1), Subtract(Times(b, c), Times(a, d)),
                              Subtract(Sqr(a), Sqr(b))), CN1),
                          Integrate(Times(
                              Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Plus(m, C1)),
                              Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), n),
                              Simp(Subtract(
                                  Plus(
                                      Times(Subtract(Times(a, ASymbol), Times(b, BSymbol)),
                                          Subtract(Times(b, c), Times(a, d)), Plus(m, C1)),
                                      Times(b, d, Subtract(Times(ASymbol, b), Times(a, BSymbol)),
                                          Plus(m, n, C2)),
                                      Times(Subtract(Times(ASymbol, b), Times(a, BSymbol)),
                                          Subtract(Times(a, d, Plus(m, C1)),
                                              Times(b, c, Plus(m, C2))),
                                          Sin(Plus(e, Times(f, x))))),
                                  Times(b, d, Subtract(Times(ASymbol, b), Times(a, BSymbol)),
                                      Plus(m, n, C3), Sqr(Sin(Plus(e, Times(f, x)))))),
                                  x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol, n), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      NeQ(Subtract(Sqr(a), Sqr(b)), C0), NeQ(Subtract(Sqr(c), Sqr(d)), C0),
                      RationalQ(m), Less(m, CN1),
                      Or(And(EqQ(a, C0), IntegerQ(m), Not(IntegerQ(n))),
                          Not(And(IntegerQ(Times(C2, n)), LtQ(n, CN1),
                              Or(And(IntegerQ(n), Not(IntegerQ(m))), EqQ(a, C0)))))))));
}
