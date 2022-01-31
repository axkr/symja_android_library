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
class IntRules222 {
  public static IAST RULES =
      List(
          IIntegrate(4441,
              Integrate(
                  Times(
                      Power(Cos(Plus(d_DEFAULT,
                          Times(e_DEFAULT, x_))), n_),
                      Power(F_, Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_))))),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Exp(Times(CI, n, Plus(d, Times(e, x)))),
                          Power(Cos(
                              Plus(d, Times(e, x))), n),
                          Power(Power(Plus(C1, Exp(Times(C2, CI, Plus(d, Times(e, x))))), n), CN1)),
                      Integrate(
                          Times(Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                              Power(Plus(C1,
                                  Exp(Times(C2, CI, Plus(d, Times(e, x))))), n),
                              Power(Exp(Times(CI, n, Plus(d, Times(e, x)))), CN1)),
                          x),
                      x),
                  And(FreeQ(List(FSymbol, a, b, c, d, e, n), x), Not(IntegerQ(n))))),
          IIntegrate(4442,
              Integrate(
                  Times(
                      Power(F_, Times(c_DEFAULT, Plus(a_DEFAULT,
                          Times(b_DEFAULT, x_)))),
                      Power(Tan(Plus(d_DEFAULT, Times(e_DEFAULT, x_))), n_DEFAULT)),
                  x_Symbol),
              Condition(Dist(Power(CI, n),
                  Integrate(ExpandIntegrand(
                      Times(Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                          Power(Subtract(C1, Exp(Times(C2, CI, Plus(d, Times(e, x))))), n),
                          Power(Power(Plus(C1, Exp(Times(C2, CI, Plus(d, Times(e, x))))), n), CN1)),
                      x), x),
                  x), And(FreeQ(List(FSymbol, a, b, c, d, e), x), IntegerQ(n)))),
          IIntegrate(4443,
              Integrate(
                  Times(
                      Power(Cot(Plus(d_DEFAULT,
                          Times(e_DEFAULT, x_))), n_DEFAULT),
                      Power(F_, Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_))))),
                  x_Symbol),
              Condition(
                  Dist(Power(CNI, n),
                      Integrate(
                          ExpandIntegrand(
                              Times(Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                                  Power(Plus(C1,
                                      Exp(Times(C2, CI, Plus(d, Times(e, x))))), n),
                                  Power(
                                      Power(Subtract(C1, Exp(Times(C2, CI, Plus(d, Times(e, x))))),
                                          n),
                                      CN1)),
                              x),
                          x),
                      x),
                  And(FreeQ(List(FSymbol, a, b, c, d, e), x), IntegerQ(n)))),
          IIntegrate(4444,
              Integrate(
                  Times(
                      Power(F_, Times(c_DEFAULT, Plus(a_DEFAULT,
                          Times(b_DEFAULT, x_)))),
                      Power(Sec(Plus(d_DEFAULT, Times(e_DEFAULT, x_))), n_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(b, c, Log(FSymbol), Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                              Power(Sec(Plus(d, Times(e, x))), n),
                              Power(
                                  Plus(Times(Sqr(e), Sqr(n)),
                                      Times(Sqr(b), Sqr(c), Sqr(Log(FSymbol)))),
                                  CN1)),
                          x),
                      Dist(
                          Times(Sqr(e), n, Plus(n, C1),
                              Power(
                                  Plus(
                                      Times(Sqr(e), Sqr(
                                          n)),
                                      Times(Sqr(b), Sqr(c), Sqr(Log(FSymbol)))),
                                  CN1)),
                          Integrate(Times(Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                              Power(Sec(Plus(d, Times(e, x))), Plus(n, C2))), x),
                          x),
                      Negate(
                          Simp(
                              Times(e, n, Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                                  Power(Sec(Plus(d, Times(e, x))), Plus(n, C1)),
                                  Sin(Plus(d, Times(e, x))),
                                  Power(Plus(Times(Sqr(e), Sqr(n)),
                                      Times(Sqr(b), Sqr(c), Sqr(Log(FSymbol)))), CN1)),
                              x))),
                  And(FreeQ(List(FSymbol, a, b, c, d, e), x),
                      NeQ(Plus(Times(Sqr(e), Sqr(n)),
                          Times(Sqr(b), Sqr(c), Sqr(Log(FSymbol)))), C0),
                      LtQ(n, CN1)))),
          IIntegrate(4445,
              Integrate(
                  Times(
                      Power(Csc(Plus(d_DEFAULT,
                          Times(e_DEFAULT, x_))), n_),
                      Power(F_, Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(b, c, Log(FSymbol), Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                              Power(Csc(Plus(d, Times(e, x))), n),
                              Power(
                                  Plus(Times(Sqr(e), Sqr(n)),
                                      Times(Sqr(b), Sqr(c), Sqr(Log(FSymbol)))),
                                  CN1)),
                          x),
                      Dist(
                          Times(Sqr(e), n, Plus(n, C1),
                              Power(Plus(Times(Sqr(e), Sqr(n)),
                                  Times(Sqr(b), Sqr(c), Sqr(Log(FSymbol)))), CN1)),
                          Integrate(Times(Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                              Power(Csc(Plus(d, Times(e, x))), Plus(n, C2))), x),
                          x),
                      Simp(
                          Times(e, n, Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                              Power(Csc(Plus(d, Times(e, x))), Plus(n, C1)),
                              Cos(Plus(d, Times(e, x))),
                              Power(Plus(Times(Sqr(e), Sqr(n)),
                                  Times(Sqr(b), Sqr(c), Sqr(Log(FSymbol)))), CN1)),
                          x)),
                  And(FreeQ(List(FSymbol, a, b, c, d,
                      e), x), NeQ(
                          Plus(Times(Sqr(e), Sqr(n)),
                              Times(Sqr(b), Sqr(c), Sqr(Log(FSymbol)))),
                          C0),
                      LtQ(n, CN1)))),
          IIntegrate(4446,
              Integrate(Times(Power(F_, Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                  Power(Sec(Plus(d_DEFAULT, Times(e_DEFAULT, x_))), n_)), x_Symbol),
              Condition(
                  Plus(
                      Negate(Simp(
                          Times(b, c, Log(FSymbol), Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                              Power(Sec(Plus(d, Times(e, x))), Subtract(n, C2)), Power(
                                  Times(Sqr(e), Subtract(n, C1), Subtract(n, C2)), CN1)),
                          x)),
                      Simp(
                          Times(Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                              Power(Sec(Plus(d, Times(e, x))), Subtract(n, C1)), Sin(Plus(d,
                                  Times(e, x))),
                              Power(Times(e, Subtract(n, C1)), CN1)),
                          x)),
                  And(FreeQ(List(FSymbol, a, b, c, d, e,
                      n), x), EqQ(
                          Plus(
                              Times(Sqr(b), Sqr(c), Sqr(Log(FSymbol))), Times(Sqr(e),
                                  Sqr(Subtract(n, C2)))),
                          C0),
                      NeQ(n, C1), NeQ(n, C2)))),
          IIntegrate(4447,
              Integrate(
                  Times(
                      Power(Csc(Plus(d_DEFAULT, Times(e_DEFAULT, x_))), n_),
                      Power(F_, Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(Simp(
                          Times(b, c, Log(FSymbol), Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                              Power(Csc(Plus(d, Times(e, x))), Subtract(n, C2)), Power(
                                  Times(Sqr(e), Subtract(n, C1), Subtract(n, C2)), CN1)),
                          x)),
                      Simp(
                          Times(Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                              Power(Csc(Plus(d, Times(e, x))), Subtract(n, C1)), Cos(Plus(d,
                                  Times(e, x))),
                              Power(Times(e, Subtract(n, C1)), CN1)),
                          x)),
                  And(FreeQ(List(FSymbol, a, b, c, d, e,
                      n), x), EqQ(
                          Plus(Times(Sqr(b), Sqr(c), Sqr(Log(FSymbol))),
                              Times(Sqr(e), Sqr(Subtract(n, C2)))),
                          C0),
                      NeQ(n, C1), NeQ(n, C2)))),
          IIntegrate(4448,
              Integrate(
                  Times(
                      Power(F_, Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_)))), Power(
                          Sec(Plus(d_DEFAULT, Times(e_DEFAULT, x_))), n_)),
                  x_Symbol),
              Condition(Plus(
                  Negate(
                      Simp(Times(b, c, Log(FSymbol), Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                          Power(Sec(Plus(d, Times(e, x))), Subtract(n, C2)),
                          Power(Times(Sqr(e), Subtract(n, C1), Subtract(n, C2)), CN1)), x)),
                  Dist(
                      Times(
                          Plus(Times(Sqr(e), Sqr(Subtract(n, C2))),
                              Times(Sqr(b), Sqr(c), Sqr(Log(FSymbol)))),
                          Power(Times(Sqr(e), Subtract(n, C1), Subtract(n, C2)), CN1)),
                      Integrate(Times(Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                          Power(Sec(Plus(d, Times(e, x))), Subtract(n, C2))), x),
                      x),
                  Simp(Times(Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                      Power(Sec(Plus(d, Times(e, x))), Subtract(n, C1)), Sin(
                          Plus(d, Times(e, x))),
                      Power(Times(e, Subtract(n, C1)), CN1)), x)),
                  And(FreeQ(List(FSymbol, a, b, c, d, e), x),
                      NeQ(Plus(Times(Sqr(b), Sqr(c), Sqr(Log(FSymbol))),
                          Times(Sqr(e), Sqr(Subtract(n, C2)))), C0),
                      GtQ(n, C1), NeQ(n, C2)))),
          IIntegrate(4449,
              Integrate(
                  Times(
                      Power(Csc(Plus(d_DEFAULT,
                          Times(e_DEFAULT, x_))), n_),
                      Power(F_, Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(Simp(
                          Times(b, c, Log(FSymbol), Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                              Power(Csc(Plus(d, Times(e, x))), Subtract(n, C2)),
                              Power(Times(Sqr(e), Subtract(n, C1), Subtract(n, C2)), CN1)),
                          x)),
                      Dist(
                          Times(
                              Plus(Times(Sqr(e), Sqr(Subtract(n, C2))),
                                  Times(Sqr(b), Sqr(c), Sqr(Log(FSymbol)))),
                              Power(Times(Sqr(e), Subtract(n, C1), Subtract(n, C2)), CN1)),
                          Integrate(Times(Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                              Power(Csc(Plus(d, Times(e, x))), Subtract(n, C2))), x),
                          x),
                      Negate(
                          Simp(
                              Times(Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                                  Power(Csc(Plus(d, Times(e, x))), Subtract(n, C1)),
                                  Cos(Plus(d, Times(e, x))), Power(Times(e, Subtract(n, C1)), CN1)),
                              x))),
                  And(FreeQ(List(FSymbol, a, b, c, d,
                      e), x), NeQ(
                          Plus(
                              Times(Sqr(b), Sqr(c), Sqr(Log(FSymbol))), Times(Sqr(e),
                                  Sqr(Subtract(n, C2)))),
                          C0),
                      GtQ(n, C1), NeQ(n, C2)))),
          IIntegrate(4450,
              Integrate(
                  Times(
                      Power(F_, Times(c_DEFAULT,
                          Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                      Power(
                          Sec(Plus(d_DEFAULT, Times(Pi, k_DEFAULT),
                              Times(e_DEFAULT, x_))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(Power(C2, n), Exp(Times(CI, k, n, Pi)),
                          Exp(Times(CI, n, Plus(d, Times(e, x)))),
                          Power(FSymbol, Times(c,
                              Plus(a, Times(b, x)))),
                          Hypergeometric2F1(n,
                              Subtract(
                                  Times(C1D2, n), Times(CI, b, c, Log(FSymbol),
                                      Power(Times(C2, e), CN1))),
                              Subtract(
                                  Plus(C1, Times(C1D2, n)), Times(CI, b, c, Log(FSymbol),
                                      Power(Times(C2, e), CN1))),
                              Times(CN1, Exp(Times(C2, CI, k, Pi)),
                                  Exp(Times(C2, CI, Plus(d, Times(e, x)))))),
                          Power(Plus(Times(CI, e, n), Times(b, c, Log(FSymbol))), CN1)),
                      x),
                  And(FreeQ(List(FSymbol, a, b, c, d, e), x), IntegerQ(Times(C4, k)),
                      IntegerQ(n)))),
          IIntegrate(4451,
              Integrate(
                  Times(Power(F_, Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_)))), Power(
                      Sec(Plus(d_DEFAULT, Times(e_DEFAULT, x_))), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(Power(C2, n), Exp(Times(CI, n, Plus(d, Times(e, x)))),
                          Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                          Hypergeometric2F1(n,
                              Subtract(Times(C1D2, n),
                                  Times(CI, b, c, Log(FSymbol), Power(Times(C2, e), CN1))),
                              Subtract(
                                  Plus(C1, Times(C1D2, n)), Times(CI, b, c, Log(FSymbol),
                                      Power(Times(C2, e), CN1))),
                              Negate(Exp(Times(C2, CI, Plus(d, Times(e, x)))))),
                          Power(Plus(Times(CI, e, n), Times(b, c, Log(FSymbol))), CN1)),
                      x),
                  And(FreeQ(List(FSymbol, a, b, c, d, e), x), IntegerQ(n)))),
          IIntegrate(4452,
              Integrate(Times(Power(
                  Csc(Plus(d_DEFAULT, Times(Pi, k_DEFAULT), Times(e_DEFAULT, x_))), n_DEFAULT),
                  Power(F_, Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_))))), x_Symbol),
              Condition(Simp(Times(Power(Times(CN2, CI), n), Exp(Times(CI, k, n, Pi)),
                  Exp(Times(CI, n, Plus(d, Times(e, x)))),
                  Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                  Power(Plus(Times(CI, e, n), Times(b, c, Log(FSymbol))), CN1),
                  Hypergeometric2F1(n,
                      Subtract(Times(C1D2, n),
                          Times(CI, b, c, Log(FSymbol), Power(Times(C2, e), CN1))),
                      Subtract(Plus(C1, Times(C1D2, n)),
                          Times(CI, b, c, Log(FSymbol), Power(Times(C2, e), CN1))),
                      Times(Exp(Times(C2, CI, k, Pi)), Exp(Times(C2, CI, Plus(d, Times(e, x))))))),
                  x),
                  And(FreeQ(List(FSymbol, a, b, c, d, e), x), IntegerQ(Times(C4, k)),
                      IntegerQ(n)))),
          IIntegrate(4453,
              Integrate(
                  Times(Power(Csc(Plus(d_DEFAULT, Times(e_DEFAULT, x_))), n_DEFAULT),
                      Power(F_, Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_))))),
                  x_Symbol),
              Condition(
                  Simp(Times(Power(Times(CN2, CI), n), Exp(Times(CI, n, Plus(d, Times(e, x)))),
                      Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                      Power(Plus(Times(CI, e, n), Times(b, c, Log(FSymbol))), CN1),
                      Hypergeometric2F1(n,
                          Subtract(Times(C1D2, n),
                              Times(CI, b, c, Log(FSymbol), Power(Times(C2, e), CN1))),
                          Subtract(Plus(C1, Times(C1D2, n)),
                              Times(CI, b, c, Log(FSymbol), Power(Times(C2, e), CN1))),
                          Exp(Times(C2, CI, Plus(d, Times(e, x)))))),
                      x),
                  And(FreeQ(List(FSymbol, a, b, c, d, e), x), IntegerQ(n)))),
          IIntegrate(4454,
              Integrate(
                  Times(
                      Power(F_, Times(c_DEFAULT, Plus(a_DEFAULT,
                          Times(b_DEFAULT, x_)))),
                      Power(Sec(Plus(d_DEFAULT, Times(e_DEFAULT, x_))), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(Plus(C1, Exp(
                              Times(C2, CI, Plus(d, Times(e, x))))), n),
                          Power(Sec(
                              Plus(d, Times(e, x))), n),
                          Power(Exp(Times(CI, n, Plus(d, Times(e, x)))), CN1)),
                      Integrate(
                          SimplifyIntegrand(
                              Times(Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                                  Exp(Times(CI, n,
                                      Plus(d, Times(e, x)))),
                                  Power(
                                      Power(Plus(C1, Exp(Times(C2, CI, Plus(d, Times(e, x))))), n),
                                      CN1)),
                              x),
                          x),
                      x),
                  And(FreeQ(List(FSymbol, a, b, c, d, e), x), Not(IntegerQ(n))))),
          IIntegrate(4455,
              Integrate(
                  Times(
                      Power(Csc(Plus(d_DEFAULT,
                          Times(e_DEFAULT, x_))), n_DEFAULT),
                      Power(F_, Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_))))),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Subtract(C1, Exp(Times(CN2, CI, Plus(d, Times(e, x))))), n),
                          Power(Csc(
                              Plus(d, Times(e, x))), n),
                          Power(Exp(Times(CN1, CI, n, Plus(d, Times(e, x)))), CN1)),
                      Integrate(
                          SimplifyIntegrand(
                              Times(
                                  Power(FSymbol, Times(c,
                                      Plus(a, Times(b, x)))),
                                  Power(
                                      Times(Exp(Times(CI, n, Plus(d, Times(e, x)))),
                                          Power(
                                              Subtract(C1,
                                                  Exp(Times(CN2, CI, Plus(d, Times(e, x))))),
                                              n)),
                                      CN1)),
                              x),
                          x),
                      x),
                  And(FreeQ(List(FSymbol, a, b, c, d, e), x), Not(IntegerQ(n))))),
          IIntegrate(4456,
              Integrate(Times(Power(F_, Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                  Power(Plus(f_, Times(g_DEFAULT, Sin(Plus(d_DEFAULT, Times(e_DEFAULT, x_))))),
                      n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(C2, n), Power(f, n)), Integrate(
                          Times(Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                              Power(Cos(Subtract(Plus(Times(C1D2, d), Times(C1D2, e, x)),
                                  Times(f, Pi, Power(Times(C4, g), CN1)))), Times(C2, n))),
                          x),
                      x),
                  And(FreeQ(List(FSymbol, a, b, c, d, e, f, g), x), EqQ(Subtract(Sqr(f),
                      Sqr(g)), C0), ILtQ(n,
                          C0)))),
          IIntegrate(4457,
              Integrate(
                  Times(
                      Power(
                          Plus(Times(Cos(Plus(d_DEFAULT, Times(e_DEFAULT, x_))), g_DEFAULT), f_),
                          n_DEFAULT),
                      Power(F_, Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_))))),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(C2, n), Power(f,
                          n)),
                      Integrate(
                          Times(
                              Power(FSymbol, Times(c, Plus(a, Times(b,
                                  x)))),
                              Power(Cos(Plus(Times(C1D2, d), Times(C1D2, e, x))), Times(C2, n))),
                          x),
                      x),
                  And(FreeQ(List(FSymbol, a, b, c, d, e, f, g), x), EqQ(Subtract(f, g), C0),
                      ILtQ(n, C0)))),
          IIntegrate(4458,
              Integrate(
                  Times(
                      Power(
                          Plus(Times(Cos(Plus(d_DEFAULT, Times(e_DEFAULT, x_))), g_DEFAULT), f_),
                          n_DEFAULT),
                      Power(F_, Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_))))),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(C2, n), Power(f,
                          n)),
                      Integrate(
                          Times(
                              Power(FSymbol, Times(c, Plus(a, Times(b,
                                  x)))),
                              Power(Sin(Plus(Times(C1D2, d), Times(C1D2, e, x))), Times(C2, n))),
                          x),
                      x),
                  And(FreeQ(List(FSymbol, a, b, c, d, e, f, g), x), EqQ(Plus(f,
                      g), C0), ILtQ(n,
                          C0)))),
          IIntegrate(4459,
              Integrate(
                  Times(
                      Power(Cos(Plus(d_DEFAULT,
                          Times(e_DEFAULT, x_))), m_DEFAULT),
                      Power(F_, Times(c_DEFAULT,
                          Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                      Power(
                          Plus(f_, Times(g_DEFAULT, Sin(Plus(d_DEFAULT,
                              Times(e_DEFAULT, x_))))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(g, n),
                      Integrate(
                          Times(
                              Power(FSymbol, Times(c,
                                  Plus(a, Times(b, x)))),
                              Power(
                                  Tan(Subtract(
                                      Subtract(Times(f, Pi, Power(Times(C4, g), CN1)),
                                          Times(C1D2, d)),
                                      Times(C1D2, e, x))),
                                  m)),
                          x),
                      x),
                  And(FreeQ(List(FSymbol, a, b, c, d, e, f, g), x),
                      EqQ(Subtract(Sqr(f), Sqr(g)), C0), IntegersQ(m, n), EqQ(Plus(m, n), C0)))),
          IIntegrate(4460,
              Integrate(Times(
                  Power(Plus(Times(Cos(Plus(d_DEFAULT, Times(e_DEFAULT, x_))), g_DEFAULT), f_),
                      n_DEFAULT),
                  Power(F_, Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                  Power(Sin(Plus(d_DEFAULT, Times(e_DEFAULT, x_))), m_DEFAULT)), x_Symbol),
              Condition(
                  Dist(Power(f, n),
                      Integrate(Times(Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                          Power(Tan(Plus(Times(C1D2, d), Times(C1D2, e, x))), m)), x),
                      x),
                  And(FreeQ(List(FSymbol, a, b, c, d, e, f, g), x), EqQ(Subtract(f, g), C0),
                      IntegersQ(m, n), EqQ(Plus(m, n), C0)))));
}
