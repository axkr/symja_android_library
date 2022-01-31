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
class IntRules164 {
  public static IAST RULES = List(
      IIntegrate(3281,
          Integrate(
              Times(
                  Power(
                      Plus(a_DEFAULT,
                          Times(
                              Power($($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                                  n_DEFAULT),
                              b_DEFAULT),
                          Times(
                              Power(
                                  $($s("§cos"), Plus(d_DEFAULT,
                                      Times(e_DEFAULT, x_))),
                                  $p("n2", true)),
                              c_DEFAULT)),
                      p_DEFAULT),
                  Power($($s("§tan"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))), m_)),
              x_Symbol),
          Condition(
              Dist(Power(Times(Power(C4, p), Power(c, p)), CN1),
                  Integrate(
                      Times(Power(Tan(Plus(d, Times(e, x))), m),
                          Power(Plus(b, Times(C2, c, Power(Cos(Plus(d, Times(e, x))), n))),
                              Times(C2, p))),
                      x),
                  x),
              And(FreeQ(List(a, b, c, d, e, m, n), x), EqQ($s("n2"), Times(C2, n)),
                  Not(IntegerQ(Times(C1D2, Subtract(m, C1)))), EqQ(
                      Subtract(Sqr(b), Times(C4, a, c)), C0),
                  IntegerQ(p)))),
      IIntegrate(3282,
          Integrate(
              Times(
                  Power($($s("§cot"),
                      Plus(d_DEFAULT, Times(e_DEFAULT, x_))), m_),
                  Power(
                      Plus(a_DEFAULT,
                          Times(b_DEFAULT,
                              Power($($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                                  n_DEFAULT)),
                          Times(c_DEFAULT,
                              Power($($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                                  $p("n2", true)))),
                      p_)),
              x_Symbol),
          Condition(
              Dist(
                  Times(
                      Power(
                          Plus(
                              a, Times(b, Power(Sin(Plus(d, Times(e,
                                  x))), n)),
                              Times(c, Power(Sin(Plus(d, Times(e, x))), Times(C2, n)))),
                          p),
                      Power(Power(Plus(b, Times(C2, c, Power(Sin(Plus(d, Times(e, x))), n))),
                          Times(C2, p)), CN1)),
                  Integrate(
                      Times(Power(Cot(Plus(d, Times(e, x))), m),
                          Power(
                              Plus(b, Times(C2, c, Power(Sin(Plus(d, Times(e, x))), n))), Times(C2,
                                  p))),
                      x),
                  x),
              And(FreeQ(List(a, b, c, d, e, m, n, p), x), EqQ($s("n2"), Times(C2, n)),
                  Not(IntegerQ(Times(C1D2,
                      Subtract(m, C1)))),
                  EqQ(Subtract(Sqr(b), Times(C4, a, c)), C0), Not(IntegerQ(p))))),
      IIntegrate(3283,
          Integrate(
              Times(
                  Power(
                      Plus(a_DEFAULT,
                          Times(
                              Power($($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                                  n_DEFAULT),
                              b_DEFAULT),
                          Times(
                              Power($($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                                  $p("n2", true)),
                              c_DEFAULT)),
                      p_),
                  Power($($s("§tan"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))), m_)),
              x_Symbol),
          Condition(
              Dist(
                  Times(
                      Power(
                          Plus(
                              a, Times(b, Power(Cos(Plus(d, Times(e,
                                  x))), n)),
                              Times(c, Power(Cos(Plus(d, Times(e, x))), Times(C2, n)))),
                          p),
                      Power(Power(Plus(b, Times(C2, c, Power(Cos(Plus(d, Times(e, x))), n))),
                          Times(C2, p)), CN1)),
                  Integrate(Times(Power(Tan(Plus(d, Times(e, x))), m),
                      Power(Plus(b, Times(C2, c, Power(Cos(Plus(d, Times(e, x))), n))),
                          Times(C2, p))),
                      x),
                  x),
              And(FreeQ(List(a, b, c, d, e, m, n, p), x), EqQ($s("n2"), Times(C2, n)),
                  Not(IntegerQ(Times(C1D2, Subtract(m, C1)))), EqQ(
                      Subtract(Sqr(b), Times(C4, a, c)), C0),
                  Not(IntegerQ(p))))),
      IIntegrate(3284,
          Integrate(
              Times(
                  Power($($s("§cot"),
                      Plus(d_DEFAULT, Times(e_DEFAULT, x_))), m_DEFAULT),
                  Power(
                      Plus(a_,
                          Times(b_DEFAULT, Power(
                              $($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))), n_)),
                          Times(c_DEFAULT,
                              Power($($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                                  $p("n2")))),
                      p_DEFAULT)),
              x_Symbol),
          Condition(
              Module(
                  List(Set(f,
                      FreeFactors(Cot(Plus(d, Times(e, x))), x))),
                  Negate(
                      Dist(Times(Power(f, Plus(m, C1)), Power(e, CN1)),
                          Subst(
                              Integrate(
                                  Times(Power(x, m),
                                      Power(ExpandToSum(
                                          Plus(c,
                                              Times(b,
                                                  Power(Plus(C1, Times(Sqr(f), Sqr(x))),
                                                      Times(C1D2, n))),
                                              Times(a, Power(Plus(C1, Times(Sqr(f), Sqr(x))), n))),
                                          x), p),
                                      Power(Power(Plus(C1, Times(Sqr(f), Sqr(x))),
                                          Plus(Times(n, p), C1)), CN1)),
                                  x),
                              x, Times(Cot(Plus(d, Times(e, x))), Power(f, CN1))),
                          x))),
              And(FreeQ(List(a, b, c, d, e,
                  m), x), EqQ($s("n2"),
                      Times(C2, n)),
                  IntegerQ(Times(C1D2, n)), IntegerQ(p)))),
      IIntegrate(3285,
          Integrate(
              Times(
                  Power(
                      Plus(
                          Times(Power($($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))), n_),
                              b_DEFAULT),
                          Times(
                              Power($($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                                  $p("n2")),
                              c_DEFAULT),
                          a_),
                      p_DEFAULT),
                  Power($($s("§tan"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))), m_DEFAULT)),
              x_Symbol),
          Condition(
              Module(
                  List(Set(f,
                      FreeFactors(Tan(Plus(d, Times(e, x))), x))),
                  Dist(Times(Power(f, Plus(m, C1)), Power(e, CN1)),
                      Subst(
                          Integrate(
                              Times(
                                  Power(x, m),
                                  Power(
                                      ExpandToSum(
                                          Plus(c,
                                              Times(b,
                                                  Power(Plus(C1, Times(Sqr(f), Sqr(x))),
                                                      Times(C1D2, n))),
                                              Times(a, Power(Plus(C1, Times(Sqr(f), Sqr(x))), n))),
                                          x),
                                      p),
                                  Power(
                                      Power(Plus(C1, Times(Sqr(f), Sqr(x))), Plus(Times(n, p), C1)),
                                      CN1)),
                              x),
                          x, Times(Tan(Plus(d, Times(e, x))), Power(f, CN1))),
                      x)),
              And(FreeQ(List(a, b, c, d, e,
                  m), x), EqQ($s("n2"),
                      Times(C2, n)),
                  IntegerQ(Times(C1D2, n)), IntegerQ(p)))),
      IIntegrate(3286,
          Integrate(
              Times(
                  Power($($s("§cot"),
                      Plus(d_DEFAULT, Times(e_DEFAULT, x_))), m_DEFAULT),
                  Power(
                      Plus(a_DEFAULT,
                          Times(b_DEFAULT,
                              Power($($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                                  n_DEFAULT)),
                          Times(c_DEFAULT,
                              Power($($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                                  $p("n2", true)))),
                      p_DEFAULT)),
              x_Symbol),
          Condition(
              Integrate(
                  ExpandTrig(
                      Times(
                          Power(Subtract(C1, Sqr($($s("§sin"), Plus(d, Times(e, x))))), Times(C1D2,
                              m)),
                          Power(
                              Plus(a, Times(b, Power($($s("§sin"), Plus(d, Times(e, x))), n)),
                                  Times(c, Power($($s("§sin"), Plus(d, Times(e, x))),
                                      Times(C2, n)))),
                              p),
                          Power(Power($($s("§sin"), Plus(d, Times(e, x))), m), CN1)),
                      x),
                  x),
              And(FreeQ(List(a, b, c, d, e), x), EqQ($s("n2"), Times(C2, n)),
                  IntegerQ(Times(C1D2, m)), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                  IntegersQ(n, p)))),
      IIntegrate(3287,
          Integrate(
              Times(
                  Power(
                      Plus(a_DEFAULT,
                          Times(
                              Power($($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                                  n_DEFAULT),
                              b_DEFAULT),
                          Times(
                              Power(
                                  $($s("§cos"), Plus(d_DEFAULT,
                                      Times(e_DEFAULT, x_))),
                                  $p("n2", true)),
                              c_DEFAULT)),
                      p_DEFAULT),
                  Power($($s("§tan"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))), m_DEFAULT)),
              x_Symbol),
          Condition(
              Integrate(
                  ExpandTrig(
                      Times(
                          Power(Subtract(C1, Sqr($($s("§cos"), Plus(d, Times(e, x))))), Times(C1D2,
                              m)),
                          Power(
                              Plus(a, Times(b, Power($($s("§cos"), Plus(d, Times(e, x))), n)),
                                  Times(c, Power($($s("§cos"), Plus(d, Times(e, x))),
                                      Times(C2, n)))),
                              p),
                          Power(Power($($s("§cos"), Plus(d, Times(e, x))), m), CN1)),
                      x),
                  x),
              And(FreeQ(List(a, b, c, d, e), x), EqQ($s("n2"), Times(C2, n)),
                  IntegerQ(Times(C1D2, m)), NeQ(Subtract(Sqr(b),
                      Times(C4, a, c)), C0),
                  IntegersQ(n, p)))),
      IIntegrate(3288,
          Integrate(
              Times(
                  Plus(A_, Times(B_DEFAULT,
                      $($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))))),
                  Power(
                      Plus(a_,
                          Times(b_DEFAULT, $($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_)))),
                          Times(c_DEFAULT,
                              Sqr($($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_)))))),
                      n_)),
              x_Symbol),
          Condition(
              Dist(
                  Power(Times(Power(C4, n),
                      Power(c, n)), CN1),
                  Integrate(
                      Times(
                          Plus(ASymbol, Times(BSymbol, Sin(Plus(d,
                              Times(e, x))))),
                          Power(Plus(b, Times(C2, c, Sin(Plus(d, Times(e, x))))), Times(C2, n))),
                      x),
                  x),
              And(FreeQ(List(a, b, c, d, e, ASymbol,
                  BSymbol), x), EqQ(Subtract(Sqr(b), Times(C4, a, c)),
                      C0),
                  IntegerQ(n)))),
      IIntegrate(3289,
          Integrate(
              Times(
                  Power(
                      Plus(
                          Times($($s("§cos"),
                              Plus(d_DEFAULT, Times(e_DEFAULT, x_))), b_DEFAULT),
                          Times(Sqr($($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_)))),
                              c_DEFAULT),
                          a_),
                      n_),
                  Plus(Times($($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))), B_DEFAULT), A_)),
              x_Symbol),
          Condition(
              Dist(
                  Power(Times(Power(C4, n),
                      Power(c, n)), CN1),
                  Integrate(
                      Times(
                          Plus(ASymbol, Times(BSymbol, Cos(Plus(d, Times(e, x))))), Power(Plus(b,
                              Times(C2, c, Cos(Plus(d, Times(e, x))))), Times(C2, n))),
                      x),
                  x),
              And(FreeQ(List(a, b, c, d, e, ASymbol,
                  BSymbol), x), EqQ(Subtract(Sqr(b), Times(C4, a, c)),
                      C0),
                  IntegerQ(n)))),
      IIntegrate(3290,
          Integrate(
              Times(
                  Plus(A_, Times(B_DEFAULT, $($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))))),
                  Power(
                      Plus(a_,
                          Times(b_DEFAULT, $($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_)))),
                          Times(
                              c_DEFAULT, Sqr($($s("§sin"), Plus(d_DEFAULT,
                                  Times(e_DEFAULT, x_)))))),
                      n_)),
              x_Symbol),
          Condition(
              Dist(
                  Times(
                      Power(
                          Plus(a, Times(b, Sin(Plus(d, Times(e, x)))),
                              Times(c, Sqr(Sin(Plus(d, Times(e, x)))))),
                          n),
                      Power(
                          Power(Plus(b, Times(C2, c, Sin(Plus(d, Times(e, x))))),
                              Times(C2, n)),
                          CN1)),
                  Integrate(
                      Times(
                          Plus(ASymbol, Times(BSymbol, Sin(Plus(d,
                              Times(e, x))))),
                          Power(Plus(b, Times(C2, c, Sin(Plus(d, Times(e, x))))), Times(C2, n))),
                      x),
                  x),
              And(FreeQ(List(a, b, c, d, e, ASymbol,
                  BSymbol), x), EqQ(Subtract(Sqr(b), Times(C4, a, c)),
                      C0),
                  Not(IntegerQ(n))))),
      IIntegrate(3291,
          Integrate(
              Times(
                  Power(
                      Plus(
                          Times($($s("§cos"),
                              Plus(d_DEFAULT, Times(e_DEFAULT, x_))), b_DEFAULT),
                          Times(Sqr($($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_)))),
                              c_DEFAULT),
                          a_),
                      n_),
                  Plus(Times($($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))), B_DEFAULT), A_)),
              x_Symbol),
          Condition(
              Dist(
                  Times(
                      Power(Plus(a, Times(b, Cos(Plus(d, Times(e, x)))),
                          Times(c, Sqr(Cos(Plus(d, Times(e, x)))))), n),
                      Power(Power(Plus(b, Times(C2, c, Cos(Plus(d, Times(e, x))))), Times(C2, n)),
                          CN1)),
                  Integrate(
                      Times(
                          Plus(ASymbol, Times(BSymbol, Cos(Plus(d,
                              Times(e, x))))),
                          Power(Plus(b, Times(C2, c, Cos(Plus(d, Times(e, x))))), Times(C2, n))),
                      x),
                  x),
              And(FreeQ(List(a, b, c, d, e, ASymbol, BSymbol), x),
                  EqQ(Subtract(Sqr(b), Times(C4, a, c)), C0), Not(IntegerQ(n))))),
      IIntegrate(3292,
          Integrate(
              Times(
                  Plus(A_, Times(B_DEFAULT,
                      $($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))))),
                  Power(
                      Plus(a_DEFAULT,
                          Times(b_DEFAULT, $($s("§sin"),
                              Plus(d_DEFAULT, Times(e_DEFAULT, x_)))),
                          Times(
                              c_DEFAULT, Sqr($($s("§sin"), Plus(d_DEFAULT,
                                  Times(e_DEFAULT, x_)))))),
                      CN1)),
              x_Symbol),
          Condition(
              Module(
                  List(Set(q,
                      Rt(Subtract(Sqr(b), Times(C4, a, c)), C2))),
                  Plus(
                      Dist(
                          Plus(BSymbol,
                              Times(
                                  Subtract(Times(b, BSymbol), Times(C2, ASymbol, c)), Power(q,
                                      CN1))),
                          Integrate(Power(Plus(b, q, Times(C2, c, Sin(Plus(d, Times(e, x))))), CN1),
                              x),
                          x),
                      Dist(
                          Subtract(BSymbol,
                              Times(
                                  Subtract(Times(b, BSymbol), Times(C2, ASymbol, c)),
                                  Power(q, CN1))),
                          Integrate(
                              Power(Plus(b, Negate(q), Times(C2, c, Sin(Plus(d, Times(e, x))))),
                                  CN1),
                              x),
                          x))),
              And(FreeQ(List(a, b, c, d, e, ASymbol, BSymbol), x),
                  NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0)))),
      IIntegrate(3293,
          Integrate(
              Times(
                  Power(
                      Plus(a_DEFAULT,
                          Times($($s("§cos"),
                              Plus(d_DEFAULT, Times(e_DEFAULT, x_))), b_DEFAULT),
                          Times(
                              Sqr($($s("§cos"),
                                  Plus(d_DEFAULT, Times(e_DEFAULT, x_)))),
                              c_DEFAULT)),
                      CN1),
                  Plus(Times($($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))), B_DEFAULT), A_)),
              x_Symbol),
          Condition(
              Module(
                  List(Set(q,
                      Rt(Subtract(Sqr(b), Times(C4, a, c)), C2))),
                  Plus(
                      Dist(
                          Plus(BSymbol,
                              Times(
                                  Subtract(Times(b, BSymbol), Times(C2, ASymbol, c)), Power(q,
                                      CN1))),
                          Integrate(Power(Plus(b, q, Times(C2, c, Cos(Plus(d, Times(e, x))))), CN1),
                              x),
                          x),
                      Dist(
                          Subtract(BSymbol,
                              Times(
                                  Subtract(Times(b, BSymbol), Times(C2, ASymbol, c)), Power(q,
                                      CN1))),
                          Integrate(
                              Power(Plus(b, Negate(q), Times(C2, c, Cos(Plus(d, Times(e, x))))),
                                  CN1),
                              x),
                          x))),
              And(FreeQ(List(a, b, c, d, e, ASymbol, BSymbol), x),
                  NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0)))),
      IIntegrate(3294,
          Integrate(
              Times(
                  Plus(A_, Times(B_DEFAULT,
                      $($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))))),
                  Power(
                      Plus(a_DEFAULT,
                          Times(b_DEFAULT, $($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_)))),
                          Times(c_DEFAULT,
                              Sqr($($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_)))))),
                      n_)),
              x_Symbol),
          Condition(
              Integrate(
                  ExpandTrig(
                      Times(Plus(ASymbol, Times(BSymbol, $($s("§sin"), Plus(d, Times(e, x))))),
                          Power(
                              Plus(a, Times(b, $($s("§sin"), Plus(d, Times(e, x)))),
                                  Times(c, Sqr($($s("§sin"), Plus(d, Times(e, x)))))),
                              n)),
                      x),
                  x),
              And(FreeQ(List(a, b, c, d, e, ASymbol,
                  BSymbol), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)),
                      C0),
                  IntegerQ(n)))),
      IIntegrate(3295,
          Integrate(
              Times(
                  Power(
                      Plus(a_DEFAULT,
                          Times($($s("§cos"),
                              Plus(d_DEFAULT, Times(e_DEFAULT, x_))), b_DEFAULT),
                          Times(
                              Sqr($($s("§cos"),
                                  Plus(d_DEFAULT, Times(e_DEFAULT, x_)))),
                              c_DEFAULT)),
                      n_),
                  Plus(Times($($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))), B_DEFAULT), A_)),
              x_Symbol),
          Condition(
              Integrate(
                  ExpandTrig(
                      Times(Plus(ASymbol, Times(BSymbol, $($s("§cos"), Plus(d, Times(e, x))))),
                          Power(
                              Plus(
                                  a, Times(b, $($s("§cos"), Plus(d, Times(e, x)))), Times(c,
                                      Sqr($($s("§cos"), Plus(d, Times(e, x)))))),
                              n)),
                      x),
                  x),
              And(FreeQ(List(a, b, c, d, e, ASymbol, BSymbol), x),
                  NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0), IntegerQ(n)))),
      IIntegrate(3296,
          Integrate(
              Times(
                  Power(Plus(c_DEFAULT,
                      Times(d_DEFAULT, x_)), m_DEFAULT),
                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
              x_Symbol),
          Condition(
              Plus(
                  Negate(
                      Simp(
                          Times(Power(Plus(c, Times(d, x)), m), Cos(Plus(e, Times(f, x))),
                              Power(f, CN1)),
                          x)),
                  Dist(
                      Times(d, m, Power(f,
                          CN1)),
                      Integrate(
                          Times(
                              Power(Plus(c, Times(d, x)), Subtract(m, C1)), Cos(
                                  Plus(e, Times(f, x)))),
                          x),
                      x)),
              And(FreeQ(List(c, d, e, f), x), GtQ(m, C0)))),
      IIntegrate(3297,
          Integrate(Times(Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_),
              $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))), x_Symbol),
          Condition(
              Subtract(
                  Simp(Times(Power(Plus(c, Times(d, x)), Plus(m, C1)), Sin(Plus(e, Times(f, x))),
                      Power(Times(d, Plus(m, C1)), CN1)), x),
                  Dist(Times(f, Power(Times(d, Plus(m, C1)), CN1)),
                      Integrate(Times(Power(Plus(c, Times(d, x)), Plus(m, C1)),
                          Cos(Plus(e, Times(f, x)))), x),
                      x)),
              And(FreeQ(List(c, d, e, f), x), LtQ(m, CN1)))),
      IIntegrate(3298,
          Integrate(
              Times(
                  Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), CN1), $($s("§sin"), Plus(e_DEFAULT,
                      Times(Complex(C0, $p("fz")), f_DEFAULT, x_)))),
              x_Symbol),
          Condition(
              Simp(Times(CI,
                  SinhIntegral(Plus(Times(c, f, $s("fz"), Power(d, CN1)), Times(f, $s("fz"), x))),
                  Power(d, CN1)), x),
              And(FreeQ(List(c, d, e, f, $s("fz")), x),
                  EqQ(Subtract(Times(d, e), Times(c, f, $s("fz"), CI)), C0)))),
      IIntegrate(3299,
          Integrate(Times(Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), CN1),
              $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))), x_Symbol),
          Condition(Simp(Times(SinIntegral(Plus(e, Times(f, x))), Power(d, CN1)), x),
              And(FreeQ(List(c, d, e, f), x), EqQ(Subtract(Times(d, e), Times(c, f)), C0)))),
      IIntegrate(3300,
          Integrate(
              Times(Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), CN1),
                  $($s("§sin"), Plus(e_DEFAULT, Times(Complex(C0, $p("fz")), f_DEFAULT, x_)))),
              x_Symbol),
          Condition(
              Simp(Times(
                  CoshIntegral(
                      Subtract(Times(CN1, c, f, $s("fz"), Power(d, CN1)), Times(f, $s("fz"), x))),
                  Power(d, CN1)), x),
              And(FreeQ(List(c, d, e, f, $s("fz")), x),
                  EqQ(Subtract(Times(d, Subtract(e, CPiHalf)), Times(c, f, $s("fz"), CI)), C0),
                  NegQ(Times(c, f, $s("fz"), Power(d, CN1)), C0)))));
}
