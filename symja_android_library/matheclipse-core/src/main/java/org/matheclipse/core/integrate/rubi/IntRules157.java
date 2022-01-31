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
class IntRules157 {
  public static IAST RULES =
      List(
          IIntegrate(3141,
              Integrate(
                  Times(
                      Plus(
                          A_DEFAULT, Times($($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                              B_DEFAULT)),
                      Power(
                          Plus(
                              Times(
                                  $($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))), b_DEFAULT),
                              a_,
                              Times(c_DEFAULT,
                                  $($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(
                          Plus(Times(BSymbol, c), Times(a, BSymbol,
                              Sin(Plus(d, Times(e, x))))),
                          Power(
                              Plus(
                                  a, Times(b, Cos(Plus(d, Times(e, x)))), Times(c,
                                      Sin(Plus(d, Times(e, x))))),
                              n),
                          Power(Times(a, e, Plus(n, C1)), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, ASymbol, BSymbol, n), x), NeQ(n, CN1),
                      EqQ(Subtract(Subtract(Sqr(a), Sqr(
                          b)), Sqr(
                              c)),
                          C0),
                      EqQ(Plus(Times(b, BSymbol, n), Times(a, ASymbol, Plus(n, C1))), C0)))),
          IIntegrate(3142,
              Integrate(
                  Times(
                      Power(
                          Plus(
                              Times(
                                  $($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))), b_DEFAULT),
                              a_,
                              Times(c_DEFAULT, $($s("§sin"),
                                  Plus(d_DEFAULT, Times(e_DEFAULT, x_))))),
                          n_DEFAULT),
                      Plus(A_DEFAULT,
                          Times($($s("§cos"),
                              Plus(d_DEFAULT, Times(e_DEFAULT, x_))), B_DEFAULT),
                          Times(C_DEFAULT, $($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_)))))),
                  x_Symbol),
              Condition(
                  Plus(Simp(Times(
                      Plus(
                          Times(BSymbol, c), Times(CN1, b, CSymbol), Times(CN1, a, CSymbol,
                              Cos(Plus(d, Times(e, x)))),
                          Times(a, BSymbol, Sin(Plus(d, Times(e, x))))),
                      Power(Plus(a, Times(b, Cos(Plus(d, Times(e, x)))),
                          Times(c, Sin(Plus(d, Times(e, x))))), n),
                      Power(Times(a, e, Plus(n, C1)), CN1)), x), Dist(
                          Times(
                              Plus(
                                  Times(Plus(Times(b, BSymbol), Times(c, CSymbol)), n),
                                  Times(a, ASymbol, Plus(n, C1))),
                              Power(Times(a, Plus(n, C1)), CN1)),
                          Integrate(
                              Power(
                                  Plus(a, Times(b, Cos(Plus(d, Times(e, x)))),
                                      Times(c, Sin(Plus(d, Times(e, x))))),
                                  n),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, ASymbol, BSymbol, CSymbol, n), x), NeQ(n, CN1),
                      EqQ(Subtract(Subtract(Sqr(a), Sqr(b)),
                          Sqr(c)), C0),
                      NeQ(Plus(
                          Times(Plus(Times(b, BSymbol),
                              Times(c, CSymbol)), n),
                          Times(a, ASymbol, Plus(n, C1))), C0)))),
          IIntegrate(3143,
              Integrate(
                  Times(
                      Power(
                          Plus(
                              Times($($s("§cos"),
                                  Plus(d_DEFAULT, Times(e_DEFAULT, x_))), b_DEFAULT),
                              a_,
                              Times(c_DEFAULT, $($s("§sin"),
                                  Plus(d_DEFAULT, Times(e_DEFAULT, x_))))),
                          n_DEFAULT),
                      Plus(
                          A_DEFAULT, Times(C_DEFAULT, $($s("§sin"),
                              Plus(d_DEFAULT, Times(e_DEFAULT, x_)))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(
                                  Plus(
                                      Times(b, CSymbol), Times(a, CSymbol,
                                          Cos(Plus(d, Times(e, x))))),
                                  Power(Plus(a, Times(b, Cos(Plus(d, Times(e, x)))),
                                      Times(c, Sin(Plus(d, Times(e, x))))), n),
                                  Power(Times(a, e, Plus(n, C1)), CN1)),
                              x)),
                      Dist(
                          Times(
                              Plus(Times(c, CSymbol, n), Times(a, ASymbol, Plus(n, C1))), Power(
                                  Times(a, Plus(n, C1)), CN1)),
                          Integrate(
                              Power(
                                  Plus(a, Times(b, Cos(Plus(d, Times(e, x)))),
                                      Times(c, Sin(Plus(d, Times(e, x))))),
                                  n),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, ASymbol, CSymbol, n), x), NeQ(n, CN1),
                      EqQ(Subtract(Subtract(Sqr(a), Sqr(
                          b)), Sqr(
                              c)),
                          C0),
                      NeQ(Plus(Times(c, CSymbol, n), Times(a, ASymbol, Plus(n, C1))), C0)))),
          IIntegrate(3144,
              Integrate(
                  Times(
                      Plus(
                          A_DEFAULT, Times($($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                              B_DEFAULT)),
                      Power(
                          Plus(
                              Times(
                                  $($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))), b_DEFAULT),
                              a_,
                              Times(c_DEFAULT, $($s("§sin"), Plus(d_DEFAULT,
                                  Times(e_DEFAULT, x_))))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(
                              Plus(Times(BSymbol, c), Times(a, BSymbol, Sin(Plus(d, Times(e, x))))),
                              Power(
                                  Plus(a, Times(b, Cos(Plus(d, Times(e, x)))),
                                      Times(c, Sin(Plus(d, Times(e, x))))),
                                  n),
                              Power(Times(a, e, Plus(n, C1)), CN1)),
                          x),
                      Dist(
                          Times(
                              Plus(Times(b, BSymbol, n), Times(a, ASymbol, Plus(n, C1))),
                              Power(Times(a, Plus(n, C1)), CN1)),
                          Integrate(
                              Power(Plus(a, Times(b, Cos(Plus(d, Times(e, x)))),
                                  Times(c, Sin(Plus(d, Times(e, x))))), n),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, ASymbol, BSymbol, n), x), NeQ(n, CN1),
                      EqQ(Subtract(Subtract(Sqr(a), Sqr(b)), Sqr(c)), C0), NeQ(Plus(Times(b,
                          BSymbol, n), Times(a, ASymbol, Plus(n, C1))), C0)))),
          IIntegrate(3145,
              Integrate(
                  Times(
                      Power(
                          Plus(
                              Times($($s("§cos"),
                                  Plus(d_DEFAULT, Times(e_DEFAULT, x_))), b_DEFAULT),
                              Times(c_DEFAULT, $($s("§sin"),
                                  Plus(d_DEFAULT, Times(e_DEFAULT, x_))))),
                          n_DEFAULT),
                      Plus(Times($($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))), B_DEFAULT),
                          Times(C_DEFAULT, $($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_)))))),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(Subtract(Times(c, BSymbol), Times(b, CSymbol)),
                          Power(Plus(Times(b, Cos(Plus(d, Times(e, x)))),
                              Times(c, Sin(Plus(d, Times(e, x))))), Plus(n, C1)),
                          Power(Times(e, Plus(n, C1), Plus(Sqr(b), Sqr(c))), CN1)),
                      x),
                  And(FreeQ(List(b, c, d, e, BSymbol, CSymbol), x), NeQ(n, CN1),
                      NeQ(Plus(Sqr(b), Sqr(c)), C0), EqQ(Plus(Times(b, BSymbol), Times(c, CSymbol)),
                          C0)))),
          IIntegrate(3146,
              Integrate(
                  Times(
                      Power(
                          Plus(
                              Times(
                                  $($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))), b_DEFAULT),
                              a_,
                              Times(c_DEFAULT, $($s("§sin"),
                                  Plus(d_DEFAULT, Times(e_DEFAULT, x_))))),
                          n_DEFAULT),
                      Plus(A_DEFAULT,
                          Times($($s("§cos"),
                              Plus(d_DEFAULT, Times(e_DEFAULT, x_))), B_DEFAULT),
                          Times(C_DEFAULT, $($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_)))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(
                              Plus(Times(BSymbol, c), Times(CN1, b, CSymbol),
                                  Times(CN1, a, CSymbol, Cos(Plus(d, Times(e, x)))), Times(a,
                                      BSymbol, Sin(Plus(d, Times(e, x))))),
                              Power(
                                  Plus(a, Times(b, Cos(Plus(d, Times(e, x)))),
                                      Times(c, Sin(Plus(d, Times(e, x))))),
                                  n),
                              Power(Times(a, e, Plus(n, C1)), CN1)),
                          x),
                      Dist(
                          Power(Times(a,
                              Plus(n, C1)), CN1),
                          Integrate(
                              Times(
                                  Power(
                                      Plus(a, Times(b, Cos(Plus(d, Times(e, x)))), Times(
                                          c, Sin(Plus(d, Times(e, x))))),
                                      Subtract(n, C1)),
                                  Simp(
                                      Plus(Times(a, Plus(Times(b, BSymbol), Times(c, CSymbol)), n),
                                          Times(Sqr(a), ASymbol, Plus(n, C1)),
                                          Times(Plus(Times(n,
                                              Plus(Times(Sqr(a), BSymbol),
                                                  Times(CN1, BSymbol, Sqr(c)),
                                                  Times(b, c, CSymbol))),
                                              Times(a, b, ASymbol, Plus(n, C1))),
                                              Cos(Plus(d, Times(e, x)))),
                                          Times(
                                              Plus(
                                                  Times(n,
                                                      Subtract(
                                                          Plus(Times(b, BSymbol, c),
                                                              Times(Sqr(a), CSymbol)),
                                                          Times(Sqr(b), CSymbol))),
                                                  Times(a, c, ASymbol, Plus(n, C1))),
                                              Sin(Plus(d, Times(e, x))))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, ASymbol, BSymbol,
                      CSymbol), x), GtQ(n,
                          C0),
                      NeQ(Subtract(Subtract(Sqr(a), Sqr(b)), Sqr(c)), C0)))),
          IIntegrate(3147,
              Integrate(
                  Times(
                      Power(
                          Plus(
                              Times($($s("§cos"),
                                  Plus(d_DEFAULT, Times(e_DEFAULT, x_))), b_DEFAULT),
                              a_,
                              Times(c_DEFAULT,
                                  $($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))))),
                          n_DEFAULT),
                      Plus(A_DEFAULT,
                          Times(C_DEFAULT, $($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_)))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(
                                  Plus(
                                      Times(b, CSymbol), Times(a, CSymbol,
                                          Cos(Plus(d, Times(e, x))))),
                                  Power(
                                      Plus(
                                          a, Times(b, Cos(Plus(d, Times(e, x)))), Times(c,
                                              Sin(Plus(d, Times(e, x))))),
                                      n),
                                  Power(Times(a, e, Plus(n, C1)), CN1)),
                              x)),
                      Dist(
                          Power(Times(a,
                              Plus(n, C1)), CN1),
                          Integrate(Times(
                              Power(Plus(
                                  a, Times(b, Cos(Plus(d, Times(e, x)))),
                                  Times(c, Sin(Plus(d, Times(e, x))))), Subtract(n, C1)),
                              Simp(Plus(Times(a, c, CSymbol, n),
                                  Times(Sqr(a), ASymbol, Plus(n, C1)),
                                  Times(
                                      Plus(Times(c, b, CSymbol, n),
                                          Times(a, b, ASymbol, Plus(n, C1))),
                                      Cos(Plus(d, Times(e, x)))),
                                  Times(
                                      Plus(Times(Sqr(a), CSymbol, n),
                                          Times(CN1, Sqr(b), CSymbol, n),
                                          Times(a, c, ASymbol, Plus(n, C1))),
                                      Sin(Plus(d, Times(e, x))))),
                                  x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, ASymbol,
                      CSymbol), x), GtQ(n,
                          C0),
                      NeQ(Subtract(Subtract(Sqr(a), Sqr(b)), Sqr(c)), C0)))),
          IIntegrate(3148,
              Integrate(
                  Times(
                      Plus(
                          A_DEFAULT, Times($($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                              B_DEFAULT)),
                      Power(
                          Plus(
                              Times($($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_,
                              Times(c_DEFAULT,
                                  $($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(
                              Plus(Times(BSymbol, c), Times(a, BSymbol, Sin(Plus(d, Times(e, x))))),
                              Power(
                                  Plus(a, Times(b, Cos(Plus(d, Times(e, x)))),
                                      Times(c, Sin(Plus(d, Times(e, x))))),
                                  n),
                              Power(Times(a, e, Plus(n, C1)), CN1)),
                          x),
                      Dist(Power(Times(a, Plus(n, C1)), CN1), Integrate(Times(
                          Power(Plus(a, Times(b, Cos(Plus(d, Times(e, x)))),
                              Times(c, Sin(Plus(d, Times(e, x))))), Subtract(n, C1)),
                          Simp(Plus(Times(a, b, BSymbol, n), Times(Sqr(a), ASymbol, Plus(n, C1)),
                              Times(
                                  Plus(Times(Sqr(a), BSymbol, n), Times(CN1, Sqr(c), BSymbol, n),
                                      Times(a, b, ASymbol, Plus(n, C1))),
                                  Cos(Plus(d, Times(e, x)))),
                              Times(Plus(Times(b, c, BSymbol, n),
                                  Times(a, c, ASymbol, Plus(n, C1))), Sin(Plus(d, Times(e, x))))),
                              x)),
                          x), x)),
                  And(FreeQ(List(a, b, c, d, e, ASymbol,
                      BSymbol), x), GtQ(n, C0), NeQ(Subtract(Subtract(Sqr(a), Sqr(b)), Sqr(c)),
                          C0)))),
          IIntegrate(3149,
              Integrate(
                  Times(
                      Power(
                          Plus(
                              Times($($s("§cos"),
                                  Plus(d_DEFAULT, Times(e_DEFAULT, x_))), b_DEFAULT),
                              a_,
                              Times(c_DEFAULT, $($s("§sin"),
                                  Plus(d_DEFAULT, Times(e_DEFAULT, x_))))),
                          CN1D2),
                      Plus(A_DEFAULT,
                          Times($($s("§cos"),
                              Plus(d_DEFAULT, Times(e_DEFAULT, x_))), B_DEFAULT),
                          Times(C_DEFAULT, $($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_)))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(Times(BSymbol, Power(b, CN1)),
                          Integrate(Sqrt(Plus(a, Times(b, Cos(Plus(d, Times(e, x)))),
                              Times(c, Sin(Plus(d, Times(e, x)))))), x),
                          x),
                      Dist(Times(Subtract(Times(ASymbol, b), Times(a, BSymbol)), Power(b, CN1)),
                          Integrate(
                              Power(Plus(a, Times(b, Cos(Plus(d, Times(e, x)))),
                                  Times(c, Sin(Plus(d, Times(e, x))))), CN1D2),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, ASymbol, BSymbol, CSymbol), x),
                      EqQ(Subtract(Times(BSymbol, c), Times(b, CSymbol)), C0), NeQ(
                          Subtract(Times(ASymbol, b), Times(a, BSymbol)), C0)))),
          IIntegrate(3150,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times($($s("§cos"),
                                  Plus(d_DEFAULT, Times(e_DEFAULT, x_))), b_DEFAULT),
                              Times(c_DEFAULT, $($s("§sin"),
                                  Plus(d_DEFAULT, Times(e_DEFAULT, x_))))),
                          CN2),
                      Plus(A_DEFAULT,
                          Times($($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))), B_DEFAULT),
                          Times(C_DEFAULT, $($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_)))))),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(
                          Plus(Times(c, BSymbol), Times(CN1, b, CSymbol),
                              Times(CN1, Subtract(Times(a, CSymbol), Times(c, ASymbol)),
                                  Cos(Plus(d, Times(e, x)))),
                              Times(Subtract(Times(a, BSymbol), Times(b, ASymbol)),
                                  Sin(Plus(d, Times(e, x))))),
                          Power(Times(e, Subtract(Subtract(Sqr(a), Sqr(b)), Sqr(c)),
                              Plus(a, Times(b, Cos(Plus(d, Times(e, x)))),
                                  Times(c, Sin(Plus(d, Times(e, x)))))),
                              CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, ASymbol, BSymbol, CSymbol), x),
                      NeQ(Subtract(Subtract(Sqr(a), Sqr(b)),
                          Sqr(c)), C0),
                      EqQ(Subtract(Subtract(Times(a, ASymbol), Times(b, BSymbol)),
                          Times(c, CSymbol)), C0)))),
          IIntegrate(3151,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times($($s("§cos"),
                                  Plus(d_DEFAULT, Times(e_DEFAULT, x_))), b_DEFAULT),
                              Times(c_DEFAULT, $($s("§sin"),
                                  Plus(d_DEFAULT, Times(e_DEFAULT, x_))))),
                          CN2),
                      Plus(
                          A_DEFAULT, Times(C_DEFAULT, $($s("§sin"),
                              Plus(d_DEFAULT, Times(e_DEFAULT, x_)))))),
                  x_Symbol),
              Condition(
                  Negate(
                      Simp(Times(
                          Plus(Times(b, CSymbol),
                              Times(Subtract(Times(a, CSymbol), Times(c, ASymbol)),
                                  Cos(Plus(d, Times(e, x)))),
                              Times(b, ASymbol, Sin(Plus(d, Times(e, x))))),
                          Power(
                              Times(e, Subtract(Subtract(Sqr(a), Sqr(b)), Sqr(c)),
                                  Plus(a, Times(b, Cos(Plus(d, Times(e, x)))),
                                      Times(c, Sin(Plus(d, Times(e, x)))))),
                              CN1)),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, ASymbol, CSymbol), x),
                      NeQ(Subtract(Subtract(Sqr(a), Sqr(b)),
                          Sqr(c)), C0),
                      EqQ(Subtract(Times(a, ASymbol), Times(c, CSymbol)), C0)))),
          IIntegrate(3152,
              Integrate(
                  Times(
                      Plus(
                          A_DEFAULT, Times($($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                              B_DEFAULT)),
                      Power(
                          Plus(a_DEFAULT,
                              Times($($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                                  b_DEFAULT),
                              Times(c_DEFAULT,
                                  $($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))))),
                          CN2)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(Plus(Times(c, BSymbol), Times(c, ASymbol, Cos(Plus(d, Times(e, x)))),
                          Times(Subtract(Times(a, BSymbol), Times(b, ASymbol)),
                              Sin(Plus(d, Times(e, x))))),
                          Power(Times(e, Subtract(Subtract(Sqr(a), Sqr(b)), Sqr(c)),
                              Plus(a, Times(b, Cos(Plus(d, Times(e, x)))),
                                  Times(c, Sin(Plus(d, Times(e, x)))))),
                              CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, ASymbol, BSymbol), x),
                      NeQ(Subtract(Subtract(Sqr(a), Sqr(b)), Sqr(c)), C0), EqQ(
                          Subtract(Times(a, ASymbol), Times(b, BSymbol)), C0)))),
          IIntegrate(3153,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times($($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                                  b_DEFAULT),
                              Times(c_DEFAULT, $($s("§sin"),
                                  Plus(d_DEFAULT, Times(e_DEFAULT, x_))))),
                          CN2),
                      Plus(A_DEFAULT, Times($($s("§cos"), Plus(d_DEFAULT,
                          Times(e_DEFAULT, x_))), B_DEFAULT), Times(C_DEFAULT, $($s("§sin"),
                              Plus(d_DEFAULT, Times(e_DEFAULT, x_)))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Plus(Times(c, BSymbol), Times(CN1, b, CSymbol),
                              Times(CN1, Subtract(Times(a, CSymbol), Times(c, ASymbol)),
                                  Cos(Plus(d, Times(e, x)))),
                              Times(Subtract(Times(a, BSymbol), Times(b, ASymbol)),
                                  Sin(Plus(d, Times(e, x))))),
                              Power(
                                  Times(e, Subtract(Subtract(Sqr(a), Sqr(b)), Sqr(c)),
                                      Plus(a, Times(b, Cos(Plus(d, Times(e, x)))),
                                          Times(c, Sin(Plus(d, Times(e, x)))))),
                                  CN1)),
                          x),
                      Dist(
                          Times(
                              Subtract(
                                  Subtract(Times(a, ASymbol), Times(b, BSymbol)), Times(c,
                                      CSymbol)),
                              Power(Subtract(Subtract(Sqr(a), Sqr(b)), Sqr(c)), CN1)),
                          Integrate(
                              Power(
                                  Plus(a, Times(b, Cos(Plus(d, Times(e, x)))),
                                      Times(c, Sin(Plus(d, Times(e, x))))),
                                  CN1),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, ASymbol, BSymbol, CSymbol), x),
                      NeQ(Subtract(Subtract(Sqr(a), Sqr(b)), Sqr(c)), C0), NeQ(
                          Subtract(Subtract(Times(a, ASymbol), Times(b, BSymbol)),
                              Times(c, CSymbol)),
                          C0)))),
          IIntegrate(3154,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times($($s("§cos"),
                                  Plus(d_DEFAULT, Times(e_DEFAULT, x_))), b_DEFAULT),
                              Times(c_DEFAULT, $($s("§sin"),
                                  Plus(d_DEFAULT, Times(e_DEFAULT, x_))))),
                          CN2),
                      Plus(A_DEFAULT, Times(C_DEFAULT, $($s("§sin"),
                          Plus(d_DEFAULT, Times(e_DEFAULT, x_)))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(
                                  Plus(Times(b, CSymbol), Times(Subtract(Times(a, CSymbol),
                                      Times(c, ASymbol)), Cos(Plus(d, Times(e, x)))), Times(b,
                                          ASymbol, Sin(Plus(d, Times(e, x))))),
                                  Power(Times(e, Subtract(Subtract(Sqr(a), Sqr(b)), Sqr(c)),
                                      Plus(a, Times(b, Cos(Plus(d, Times(e, x)))),
                                          Times(c, Sin(Plus(d, Times(e, x)))))),
                                      CN1)),
                              x)),
                      Dist(
                          Times(Subtract(Times(a, ASymbol), Times(c, CSymbol)),
                              Power(Subtract(Subtract(Sqr(a), Sqr(b)), Sqr(c)), CN1)),
                          Integrate(
                              Power(Plus(a, Times(b, Cos(Plus(d, Times(e, x)))),
                                  Times(c, Sin(Plus(d, Times(e, x))))), CN1),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, ASymbol, CSymbol), x),
                      NeQ(Subtract(Subtract(Sqr(a), Sqr(
                          b)), Sqr(
                              c)),
                          C0),
                      NeQ(Subtract(Times(a, ASymbol), Times(c, CSymbol)), C0)))),
          IIntegrate(3155,
              Integrate(
                  Times(
                      Plus(
                          A_DEFAULT, Times($($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                              B_DEFAULT)),
                      Power(Plus(a_DEFAULT,
                          Times($($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))), b_DEFAULT),
                          Times(c_DEFAULT, $($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))))),
                          CN2)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(
                              Plus(Times(c, BSymbol), Times(c, ASymbol, Cos(Plus(d, Times(e, x)))),
                                  Times(Subtract(Times(a, BSymbol), Times(b, ASymbol)),
                                      Sin(Plus(d, Times(e, x))))),
                              Power(
                                  Times(e, Subtract(Subtract(Sqr(a), Sqr(b)), Sqr(c)),
                                      Plus(a, Times(b, Cos(Plus(d, Times(e, x)))),
                                          Times(c, Sin(Plus(d, Times(e, x)))))),
                                  CN1)),
                          x),
                      Dist(
                          Times(
                              Subtract(Times(a, ASymbol), Times(b, BSymbol)), Power(
                                  Subtract(Subtract(Sqr(a), Sqr(b)), Sqr(c)), CN1)),
                          Integrate(
                              Power(
                                  Plus(
                                      a, Times(b, Cos(Plus(d, Times(e, x)))), Times(c,
                                          Sin(Plus(d, Times(e, x))))),
                                  CN1),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, ASymbol, BSymbol), x),
                      NeQ(Subtract(Subtract(Sqr(a), Sqr(
                          b)), Sqr(c)), C0),
                      NeQ(Subtract(Times(a, ASymbol), Times(b, BSymbol)), C0)))),
          IIntegrate(3156,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times($($s("§cos"), Plus(
                                  d_DEFAULT, Times(e_DEFAULT, x_))), b_DEFAULT),
                              Times(c_DEFAULT,
                                  $($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))))),
                          n_),
                      Plus(A_DEFAULT, Times($($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                          B_DEFAULT),
                          Times(C_DEFAULT, $($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_)))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(
                                  Plus(Times(c, BSymbol), Times(CN1, b, CSymbol),
                                      Times(CN1, Subtract(Times(a, CSymbol), Times(c, ASymbol)),
                                          Cos(Plus(d, Times(e, x)))),
                                      Times(Subtract(Times(a, BSymbol), Times(b, ASymbol)),
                                          Sin(Plus(d, Times(e, x))))),
                                  Power(
                                      Plus(
                                          a, Times(b, Cos(Plus(d, Times(e, x)))), Times(c,
                                              Sin(Plus(d, Times(e, x))))),
                                      Plus(n, C1)),
                                  Power(
                                      Times(
                                          e, Plus(n, C1), Subtract(Subtract(Sqr(a), Sqr(b)),
                                              Sqr(c))),
                                      CN1)),
                              x)),
                      Dist(
                          Power(Times(Plus(n, C1),
                              Subtract(Subtract(Sqr(a), Sqr(b)), Sqr(c))), CN1),
                          Integrate(
                              Times(
                                  Power(Plus(a, Times(b, Cos(Plus(d, Times(e, x)))),
                                      Times(c, Sin(Plus(d, Times(e, x))))), Plus(n, C1)),
                                  Simp(Plus(
                                      Times(Plus(n, C1),
                                          Subtract(Subtract(Times(a, ASymbol), Times(b, BSymbol)),
                                              Times(c, CSymbol))),
                                      Times(Plus(n, C2),
                                          Subtract(Times(a, BSymbol), Times(b, ASymbol)),
                                          Cos(Plus(d, Times(e, x)))),
                                      Times(Plus(n, C2),
                                          Subtract(Times(a, CSymbol), Times(c, ASymbol)),
                                          Sin(Plus(d, Times(e, x))))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, ASymbol, BSymbol,
                      CSymbol), x), LtQ(n, CN1), NeQ(Subtract(Subtract(Sqr(a), Sqr(b)), Sqr(c)),
                          C0),
                      NeQ(n, CN2)))),
          IIntegrate(3157, Integrate(
              Times(
                  Power(
                      Plus(a_DEFAULT,
                          Times($($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))), b_DEFAULT),
                          Times(c_DEFAULT, $($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))))),
                      n_),
                  Plus(
                      A_DEFAULT,
                      Times(C_DEFAULT, $($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_)))))),
              x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(
                              Plus(Times(b, CSymbol),
                                  Times(Subtract(Times(a, CSymbol),
                                      Times(c, ASymbol)), Cos(Plus(d, Times(e, x)))),
                                  Times(b, ASymbol, Sin(Plus(d, Times(e, x))))),
                              Power(
                                  Plus(
                                      a, Times(b, Cos(Plus(d, Times(e, x)))), Times(c,
                                          Sin(Plus(d, Times(e, x))))),
                                  Plus(n, C1)),
                              Power(
                                  Times(e, Plus(n, C1), Subtract(Subtract(Sqr(a), Sqr(b)), Sqr(c))),
                                  CN1)),
                          x),
                      Dist(
                          Power(Times(Plus(n, C1), Subtract(Subtract(Sqr(a), Sqr(b)), Sqr(c))),
                              CN1),
                          Integrate(Times(
                              Power(Plus(a, Times(b, Cos(Plus(d, Times(e, x)))), Times(c,
                                  Sin(Plus(d, Times(e, x))))), Plus(n, C1)),
                              Simp(Plus(
                                  Times(Plus(n, C1),
                                      Subtract(Times(a, ASymbol), Times(c, CSymbol))),
                                  Times(CN1, Plus(n, C2), b, ASymbol, Cos(Plus(d, Times(e, x)))),
                                  Times(Plus(n, C2), Subtract(Times(a, CSymbol), Times(c, ASymbol)),
                                      Sin(Plus(d, Times(e, x))))),
                                  x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, ASymbol, CSymbol), x), LtQ(n, CN1), NeQ(Subtract(
                      Subtract(Sqr(a), Sqr(b)), Sqr(c)), C0), NeQ(n,
                          CN2)))),
          IIntegrate(3158,
              Integrate(
                  Times(
                      Plus(
                          A_DEFAULT, Times($($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                              B_DEFAULT)),
                      Power(Plus(a_DEFAULT,
                          Times($($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))), b_DEFAULT),
                          Times(c_DEFAULT, $($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(
                                  Plus(Times(c, BSymbol),
                                      Times(c, ASymbol, Cos(Plus(d, Times(e, x)))),
                                      Times(Subtract(Times(a, BSymbol), Times(b, ASymbol)),
                                          Sin(Plus(d, Times(e, x))))),
                                  Power(
                                      Plus(a, Times(b, Cos(Plus(d, Times(e, x)))),
                                          Times(c, Sin(Plus(d, Times(e, x))))),
                                      Plus(n, C1)),
                                  Power(
                                      Times(e, Plus(n, C1), Subtract(Subtract(Sqr(a), Sqr(b)),
                                          Sqr(c))),
                                      CN1)),
                              x)),
                      Dist(
                          Power(Times(Plus(n, C1),
                              Subtract(Subtract(Sqr(a), Sqr(b)), Sqr(c))), CN1),
                          Integrate(Times(Power(Plus(
                              a, Times(b, Cos(Plus(d, Times(e, x)))), Times(c,
                                  Sin(Plus(d, Times(e, x))))),
                              Plus(n, C1)),
                              Simp(
                                  Subtract(
                                      Plus(
                                          Times(Plus(n, C1),
                                              Subtract(Times(a, ASymbol), Times(b, BSymbol))),
                                          Times(Plus(n, C2),
                                              Subtract(Times(a, BSymbol), Times(b, ASymbol)),
                                              Cos(Plus(d, Times(e, x))))),
                                      Times(Plus(n, C2), c, ASymbol, Sin(Plus(d, Times(e, x))))),
                                  x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, ASymbol, BSymbol), x), LtQ(n, CN1), NeQ(Subtract(
                      Subtract(Sqr(a), Sqr(b)), Sqr(c)), C0), NeQ(n,
                          CN2)))),
          IIntegrate(3159,
              Integrate(
                  Power(
                      Plus(a_DEFAULT,
                          Times(b_DEFAULT, $($s("§sec"), Plus(d_DEFAULT, Times(e_DEFAULT, x_)))),
                          Times(c_DEFAULT, $($s("§tan"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))))),
                      CN1),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          Cos(Plus(d,
                              Times(e, x))),
                          Power(
                              Plus(
                                  b, Times(a, Cos(Plus(d, Times(e, x)))), Times(c,
                                      Sin(Plus(d, Times(e, x))))),
                              CN1)),
                      x),
                  FreeQ(List(a, b, c, d, e), x))),
          IIntegrate(3160, Integrate(
              Power(Plus(a_DEFAULT,
                  Times($($s("§csc"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))), b_DEFAULT),
                  Times($($s("§cot"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))), c_DEFAULT)), CN1),
              x_Symbol),
              Condition(
                  Integrate(Times(Sin(Plus(d, Times(e, x))),
                      Power(Plus(b, Times(a, Sin(Plus(d, Times(e, x)))),
                          Times(c, Cos(Plus(d, Times(e, x))))), CN1)),
                      x),
                  FreeQ(List(a, b, c, d, e), x))));
}
