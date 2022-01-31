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
class IntRules210 {
  public static IAST RULES =
      List(
          IIntegrate(4201,
              Integrate(Power(
                  Plus(a_DEFAULT,
                      Times(Csc(Plus(c_DEFAULT, Times(d_DEFAULT, Power(u_, n_)))), b_DEFAULT)),
                  p_DEFAULT), x_Symbol),
              Condition(Dist(Power(Coefficient(u, x, C1), CN1),
                  Subst(Integrate(Power(Plus(a, Times(b, Csc(Plus(c, Times(d, Power(x, n)))))), p),
                      x), x, u),
                  x), And(FreeQ(List(a, b, c, d, n, p), x), LinearQ(u, x), NeQ(u, x)))),
          IIntegrate(4202,
              Integrate(Power(Plus(a_DEFAULT, Times(b_DEFAULT, Sec(u_))), p_DEFAULT), x_Symbol),
              Condition(
                  Integrate(Power(Plus(a, Times(b, Sec(ExpandToSum(u, x)))),
                      p), x),
                  And(FreeQ(List(a, b, p), x), BinomialQ(u, x), Not(BinomialMatchQ(u, x))))),
          IIntegrate(4203,
              Integrate(Power(Plus(a_DEFAULT, Times(Csc(u_), b_DEFAULT)),
                  p_DEFAULT), x_Symbol),
              Condition(
                  Integrate(Power(Plus(a, Times(b, Csc(ExpandToSum(u, x)))),
                      p), x),
                  And(FreeQ(List(a, b, p), x), BinomialQ(u, x), Not(BinomialMatchQ(u, x))))),
          IIntegrate(4204,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT,
                                  Sec(Plus(c_DEFAULT, Times(d_DEFAULT, Power(x_, n_)))))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(n, CN1),
                      Subst(
                          Integrate(
                              Times(Power(x, Subtract(Simplify(Times(Plus(m, C1), Power(n, CN1))),
                                  C1)), Power(Plus(a, Times(b, Sec(Plus(c, Times(d, x))))),
                                      p)),
                              x),
                          x, Power(x, n)),
                      x),
                  And(FreeQ(List(a, b, c, d, m, n,
                      p), x), IGtQ(Simplify(Times(Plus(m, C1), Power(n, CN1))),
                          C0),
                      IntegerQ(p)))),
          IIntegrate(4205,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  Csc(Plus(c_DEFAULT, Times(d_DEFAULT,
                                      Power(x_, n_)))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      Power(x_, m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(n, CN1),
                      Subst(
                          Integrate(
                              Times(Power(x, Subtract(Simplify(Times(Plus(m, C1), Power(n, CN1))),
                                  C1)), Power(Plus(a, Times(b, Csc(Plus(c, Times(d, x))))),
                                      p)),
                              x),
                          x, Power(x, n)),
                      x),
                  And(FreeQ(List(a, b, c, d, m, n, p), x), IGtQ(
                      Simplify(Times(Plus(m, C1), Power(n, CN1))), C0), IntegerQ(p)))),
          IIntegrate(4206,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  b_DEFAULT,
                                  Sec(Plus(c_DEFAULT, Times(d_DEFAULT, Power(x_, n_)))))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(Power(x, m), Power(
                          Plus(a, Times(b, Sec(Plus(c, Times(d, Power(x, n)))))), p)),
                      x),
                  FreeQ(List(a, b, c, d, m, n, p), x))),
          IIntegrate(4207,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(Csc(Plus(c_DEFAULT, Times(d_DEFAULT, Power(x_, n_)))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      Power(x_, m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(
                          Power(x, m), Power(Plus(a, Times(b, Csc(Plus(c, Times(d, Power(x, n)))))),
                              p)),
                      x),
                  FreeQ(List(a, b, c, d, m, n, p), x))),
          IIntegrate(4208,
              Integrate(
                  Times(
                      Power(Times(e_,
                          x_), m_DEFAULT),
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  b_DEFAULT, Sec(Plus(c_DEFAULT, Times(d_DEFAULT,
                                      Power(x_, n_)))))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(e, IntPart(m)), Power(Times(e,
                              x), FracPart(m)),
                          Power(Power(x, FracPart(m)), CN1)),
                      Integrate(
                          Times(Power(x, m),
                              Power(Plus(a, Times(b, Sec(Plus(c, Times(d, Power(x, n)))))), p)),
                          x),
                      x),
                  FreeQ(List(a, b, c, d, e, m, n, p), x))),
          IIntegrate(4209,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  Csc(Plus(c_DEFAULT, Times(d_DEFAULT,
                                      Power(x_, n_)))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      Power(Times(e_, x_), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(e, IntPart(m)), Power(Times(e, x), FracPart(m)),
                          Power(Power(x, FracPart(m)), CN1)),
                      Integrate(
                          Times(Power(x, m),
                              Power(Plus(a, Times(b, Csc(Plus(c, Times(d, Power(x, n)))))), p)),
                          x),
                      x),
                  FreeQ(List(a, b, c, d, e, m, n, p), x))),
          IIntegrate(4210,
              Integrate(
                  Times(
                      Power(Times(e_, x_), m_DEFAULT), Power(Plus(a_DEFAULT,
                          Times(b_DEFAULT, Sec(u_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(Power(Times(e, x), m),
                          Power(Plus(a, Times(b, Sec(ExpandToSum(u, x)))), p)),
                      x),
                  And(FreeQ(List(a, b, e, m, p), x), BinomialQ(u, x), Not(BinomialMatchQ(u, x))))),
          IIntegrate(4211,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(Csc(u_), b_DEFAULT)), p_DEFAULT),
                      Power(Times(e_, x_), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          Power(Times(e, x), m), Power(Plus(a, Times(b, Csc(ExpandToSum(u, x)))),
                              p)),
                      x),
                  And(FreeQ(List(a, b, e, m, p), x), BinomialQ(u, x), Not(BinomialMatchQ(u, x))))),
          IIntegrate(4212,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(Sec(Plus(a_DEFAULT,
                          Times(b_DEFAULT, Power(x_, n_DEFAULT)))), p_),
                      Sin(Plus(a_DEFAULT, Times(b_DEFAULT, Power(x_, n_DEFAULT))))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(x, Plus(m, Negate(n), C1)),
                              Power(Sec(Plus(a, Times(b, Power(x, n)))), Subtract(p, C1)),
                              Power(Times(b, n, Subtract(p, C1)), CN1)),
                          x),
                      Dist(
                          Times(Plus(m, Negate(n), C1), Power(Times(b, n, Subtract(p, C1)),
                              CN1)),
                          Integrate(Times(
                              Power(x, Subtract(m, n)), Power(Sec(Plus(a, Times(b, Power(x, n)))),
                                  Subtract(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, p), x), IntegerQ(n), GeQ(Subtract(m, n), C0), NeQ(p, C1)))),
          IIntegrate(4213,
              Integrate(
                  Times(
                      Cos(Plus(a_DEFAULT, Times(b_DEFAULT,
                          Power(x_, n_DEFAULT)))),
                      Power(Csc(
                          Plus(a_DEFAULT, Times(b_DEFAULT, Power(x_, n_DEFAULT)))), p_),
                      Power(x_, m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(Simp(Times(Power(x, Plus(m, Negate(n), C1)),
                          Power(Csc(Plus(a, Times(b, Power(x, n)))), Subtract(p, C1)), Power(
                              Times(b, n, Subtract(p, C1)), CN1)),
                          x)),
                      Dist(
                          Times(Plus(m, Negate(
                              n), C1), Power(Times(b, n, Subtract(p, C1)),
                                  CN1)),
                          Integrate(
                              Times(
                                  Power(x, Subtract(m,
                                      n)),
                                  Power(Csc(Plus(a, Times(b, Power(x, n)))), Subtract(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, p), x), IntegerQ(n), GeQ(Subtract(m, n), C0), NeQ(p, C1)))),
          IIntegrate(4214,
              Integrate(
                  Times(u_,
                      Power(
                          Times(d_DEFAULT, $($s("§sin"),
                              Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                          n_DEFAULT),
                      Power(
                          Times(c_DEFAULT, $($s("§tan"), Plus(a_DEFAULT,
                              Times(b_DEFAULT, x_)))),
                          m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Times(c, Tan(Plus(a, Times(b, x)))), m),
                          Power(Times(d,
                              Cos(Plus(a, Times(b, x)))), m),
                          Power(Power(Times(d, Sin(Plus(a, Times(b, x)))), m), CN1)),
                      Integrate(
                          Times(ActivateTrig(u),
                              Power(Times(d, Sin(
                                  Plus(a, Times(b, x)))), Plus(m,
                                      n)),
                              Power(Power(Times(d, Cos(Plus(a, Times(b, x)))), m), CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, m,
                      n), x), KnownSineIntegrandQ(u,
                          x),
                      Not(IntegerQ(m))))),
          IIntegrate(4215,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                              d_DEFAULT),
                          n_DEFAULT),
                      u_,
                      Power(
                          Times(c_DEFAULT, $($s("§tan"), Plus(a_DEFAULT,
                              Times(b_DEFAULT, x_)))),
                          m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Times(c, Tan(Plus(a, Times(b, x)))), m),
                          Power(Times(d,
                              Cos(Plus(a, Times(b, x)))), m),
                          Power(Power(Times(d, Sin(Plus(a, Times(b, x)))), m), CN1)),
                      Integrate(
                          Times(
                              ActivateTrig(u), Power(Times(d,
                                  Sin(Plus(a, Times(b, x)))), m),
                              Power(Power(Times(d, Cos(Plus(a, Times(b, x)))), Subtract(m, n)),
                                  CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, m,
                      n), x), KnownSineIntegrandQ(u,
                          x),
                      Not(IntegerQ(m))))),
          IIntegrate(4216,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cot"), Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                              c_DEFAULT),
                          m_DEFAULT),
                      u_,
                      Power(
                          Times(d_DEFAULT, $($s("§sin"), Plus(a_DEFAULT,
                              Times(b_DEFAULT, x_)))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Times(Power(Times(c, Cot(Plus(a, Times(b, x)))), m),
                      Power(Times(d, Sin(Plus(a, Times(b, x)))),
                          m),
                      Power(Power(Times(d, Cos(Plus(a, Times(b, x)))), m), CN1)),
                      Integrate(
                          Times(ActivateTrig(u), Power(Times(d, Cos(Plus(a, Times(b, x)))), m),
                              Power(Power(Times(d, Sin(Plus(a, Times(b, x)))), Subtract(m, n)),
                                  CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, m,
                      n), x), KnownSineIntegrandQ(u,
                          x),
                      Not(IntegerQ(m))))),
          IIntegrate(4217,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cot"), Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                              c_DEFAULT),
                          m_DEFAULT),
                      Power(
                          Times($($s("§cos"), Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                              d_DEFAULT),
                          n_DEFAULT),
                      u_),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Times(c, Cot(Plus(a, Times(b, x)))), m),
                          Power(Times(d,
                              Sin(Plus(a, Times(b, x)))), m),
                          Power(Power(Times(d, Cos(Plus(a, Times(b, x)))), m), CN1)),
                      Integrate(
                          Times(ActivateTrig(u),
                              Power(Times(d, Cos(Plus(a, Times(b, x)))), Plus(m,
                                  n)),
                              Power(Power(Times(d, Sin(Plus(a, Times(b, x)))), m), CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, m, n), x), KnownSineIntegrandQ(u, x),
                      Not(IntegerQ(m))))),
          IIntegrate(4218,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                              d_DEFAULT),
                          n_DEFAULT),
                      u_,
                      Power(Times(c_DEFAULT, $($s("§sec"), Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                          m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(Times(c,
                              Csc(Plus(a, Times(b, x)))), m),
                          Power(Times(d, Sin(Plus(a, Times(b, x)))), m)),
                      Integrate(
                          Times(
                              ActivateTrig(u), Power(Times(d, Sin(Plus(a, Times(b, x)))),
                                  Subtract(n, m))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, m, n), x), KnownSineIntegrandQ(u, x)))),
          IIntegrate(4219,
              Integrate(
                  Times(u_,
                      Power(
                          Times(c_DEFAULT, $($s("§tan"), Plus(a_DEFAULT,
                              Times(b_DEFAULT, x_)))),
                          m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Times(c, Tan(Plus(a, Times(b, x)))), m),
                          Power(Times(c,
                              Cos(Plus(a, Times(b, x)))), m),
                          Power(Power(Times(c, Sin(Plus(a, Times(b, x)))), m), CN1)),
                      Integrate(
                          Times(
                              ActivateTrig(u), Power(Times(c, Sin(Plus(a, Times(b, x)))), m), Power(
                                  Power(Times(c, Cos(Plus(a, Times(b, x)))), m), CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, m), x), Not(IntegerQ(m)), KnownSineIntegrandQ(u, x)))),
          IIntegrate(4220,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cot"), Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                              c_DEFAULT),
                          m_DEFAULT),
                      u_),
                  x_Symbol),
              Condition(
                  Dist(Times(Power(Times(c, Cot(Plus(a, Times(b, x)))), m),
                      Power(Times(c, Sin(Plus(a, Times(b, x)))), m),
                      Power(Power(Times(c, Cos(Plus(a, Times(b, x)))), m), CN1)),
                      Integrate(
                          Times(ActivateTrig(u), Power(Times(c, Cos(Plus(a, Times(b, x)))), m),
                              Power(Power(Times(c, Sin(Plus(a, Times(b, x)))), m), CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, m), x), Not(IntegerQ(m)), KnownSineIntegrandQ(u, x)))));
}
