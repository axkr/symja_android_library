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
class IntRules274 {
  public static IAST RULES =
      List(
          IIntegrate(5481,
              Integrate(
                  Times(
                      Power(Cosh(Plus(d_DEFAULT,
                          Times(e_DEFAULT, x_))), n_),
                      Power(F_, Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(b, c, Log(FSymbol), Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                              Power(Cosh(Plus(d, Times(e, x))), Plus(n,
                                  C2)),
                              Power(Times(Sqr(e), Plus(n, C1), Plus(n, C2)), CN1)),
                          x),
                      Dist(
                          Times(Subtract(Times(Sqr(e), Sqr(Plus(n, C2))),
                              Times(Sqr(b), Sqr(c), Sqr(Log(FSymbol)))),
                              Power(Times(Sqr(e), Plus(n, C1), Plus(n, C2)), CN1)),
                          Integrate(
                              Times(Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                                  Power(Cosh(Plus(d, Times(e, x))), Plus(n, C2))),
                              x),
                          x),
                      Negate(
                          Simp(
                              Times(Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                                  Sinh(Plus(d, Times(e, x))),
                                  Power(Cosh(Plus(d, Times(e, x))), Plus(n, C1)),
                                  Power(Times(e, Plus(n, C1)), CN1)),
                              x))),
                  And(FreeQ(List(FSymbol, a, b, c, d,
                      e), x), NeQ(
                          Subtract(Times(Sqr(e), Sqr(Plus(n, C2))),
                              Times(Sqr(b), Sqr(c), Sqr(Log(FSymbol)))),
                          C0),
                      LtQ(n, CN1), NeQ(n, CN2)))),
          IIntegrate(5482,
              Integrate(
                  Times(
                      Power(F_, Times(c_DEFAULT, Plus(a_DEFAULT,
                          Times(b_DEFAULT, x_)))),
                      Power(Sinh(Plus(d_DEFAULT, Times(e_DEFAULT, x_))), n_)),
                  x_Symbol),
              Condition(
                  Dist(Times(Exp(Times(n, Plus(d, Times(e, x)))),
                      Power(Sinh(Plus(d, Times(e, x))), n), Power(Power(
                          Plus(CN1, Exp(Times(C2, Plus(d, Times(e, x))))), n), CN1)),
                      Integrate(
                          Times(Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                              Power(Plus(CN1,
                                  Exp(Times(C2, Plus(d, Times(e, x))))), n),
                              Power(Exp(Times(n, Plus(d, Times(e, x)))), CN1)),
                          x),
                      x),
                  And(FreeQ(List(FSymbol, a, b, c, d, e, n), x), Not(IntegerQ(n))))),
          IIntegrate(5483,
              Integrate(
                  Times(
                      Power(Cosh(Plus(d_DEFAULT,
                          Times(e_DEFAULT, x_))), n_),
                      Power(F_, Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_))))),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Exp(Times(n, Plus(d,
                              Times(e, x)))),
                          Power(Cosh(Plus(d, Times(e, x))), n), Power(Power(
                              Plus(C1, Exp(Times(C2, Plus(d, Times(e, x))))), n), CN1)),
                      Integrate(
                          Times(Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                              Power(Plus(C1, Exp(Times(C2, Plus(d, Times(e, x))))), n),
                              Power(Exp(Times(n, Plus(d, Times(e, x)))), CN1)),
                          x),
                      x),
                  And(FreeQ(List(FSymbol, a, b, c, d, e, n), x), Not(IntegerQ(n))))),
          IIntegrate(5484,
              Integrate(
                  Times(
                      Power(F_, Times(c_DEFAULT, Plus(a_DEFAULT,
                          Times(b_DEFAULT, x_)))),
                      Power(Tanh(Plus(d_DEFAULT, Times(e_DEFAULT, x_))), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Times(Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                              Power(Plus(CN1,
                                  Exp(Times(C2, Plus(d, Times(e, x))))), n),
                              Power(Power(Plus(C1, Exp(Times(C2, Plus(d, Times(e, x))))), n), CN1)),
                          x),
                      x),
                  And(FreeQ(List(FSymbol, a, b, c, d, e), x), IntegerQ(n)))),
          IIntegrate(5485,
              Integrate(Times(
                  Power(Coth(Plus(d_DEFAULT, Times(e_DEFAULT, x_))), n_DEFAULT), Power(F_, Times(
                      c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_))))),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Times(
                              Power(FSymbol, Times(c, Plus(a,
                                  Times(b, x)))),
                              Power(Plus(C1,
                                  Exp(Times(C2, Plus(d, Times(e, x))))), n),
                              Power(Power(Plus(CN1, Exp(Times(C2, Plus(d, Times(e, x))))), n),
                                  CN1)),
                          x),
                      x),
                  And(FreeQ(List(FSymbol, a, b, c, d, e), x), IntegerQ(n)))),
          IIntegrate(5486,
              Integrate(
                  Times(
                      Power(F_, Times(c_DEFAULT, Plus(a_DEFAULT,
                          Times(b_DEFAULT, x_)))),
                      Power(Sech(Plus(d_DEFAULT, Times(e_DEFAULT, x_))), n_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(Simp(
                          Times(b, c, Log(FSymbol), Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                              Power(Sech(Plus(d, Times(e, x))), n),
                              Power(Subtract(Times(Sqr(e), Sqr(n)),
                                  Times(Sqr(b), Sqr(c), Sqr(Log(FSymbol)))), CN1)),
                          x)),
                      Dist(
                          Times(Sqr(e), n, Plus(n, C1),
                              Power(
                                  Subtract(Times(Sqr(e), Sqr(n)),
                                      Times(Sqr(b), Sqr(c), Sqr(Log(FSymbol)))),
                                  CN1)),
                          Integrate(Times(Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                              Power(Sech(Plus(d, Times(e, x))), Plus(n, C2))), x),
                          x),
                      Negate(
                          Simp(Times(e, n, Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                              Power(Sech(Plus(d, Times(e, x))), Plus(n, C1)),
                              Sinh(Plus(d, Times(e, x))),
                              Power(Subtract(Times(Sqr(e), Sqr(n)),
                                  Times(Sqr(b), Sqr(c), Sqr(Log(FSymbol)))), CN1)),
                              x))),
                  And(FreeQ(List(FSymbol, a, b, c, d,
                      e), x), NeQ(
                          Plus(Times(Sqr(e), Sqr(
                              n)), Times(Sqr(b), Sqr(c),
                                  Sqr(Log(FSymbol)))),
                          C0),
                      LtQ(n, CN1)))),
          IIntegrate(5487,
              Integrate(
                  Times(
                      Power(Csch(Plus(d_DEFAULT, Times(e_DEFAULT, x_))), n_),
                      Power(F_, Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(b, c, Log(FSymbol),
                                  Power(FSymbol, Times(c, Plus(a,
                                      Times(b, x)))),
                                  Power(Csch(
                                      Plus(d, Times(e, x))), n),
                                  Power(Subtract(Times(Sqr(e), Sqr(n)),
                                      Times(Sqr(b), Sqr(c), Sqr(Log(FSymbol)))), CN1)),
                              x)),
                      Negate(
                          Dist(
                              Times(Sqr(e), n, Plus(n, C1),
                                  Power(
                                      Subtract(
                                          Times(Sqr(e), Sqr(n)), Times(Sqr(b), Sqr(c),
                                              Sqr(Log(FSymbol)))),
                                      CN1)),
                              Integrate(
                                  Times(
                                      Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                                      Power(Csch(Plus(d, Times(e, x))), Plus(n, C2))),
                                  x),
                              x)),
                      Negate(
                          Simp(Times(e, n, Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                              Power(Csch(Plus(d, Times(e, x))), Plus(n, C1)),
                              Cosh(Plus(d, Times(e, x))),
                              Power(Subtract(Times(Sqr(e), Sqr(n)),
                                  Times(Sqr(b), Sqr(c), Sqr(Log(FSymbol)))), CN1)),
                              x))),
                  And(FreeQ(List(FSymbol, a, b, c, d, e), x),
                      NeQ(Plus(Times(Sqr(e), Sqr(n)), Times(Sqr(b), Sqr(c), Sqr(Log(FSymbol)))),
                          C0),
                      LtQ(n, CN1)))),
          IIntegrate(5488,
              Integrate(
                  Times(Power(F_, Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                      Power(Sech(Plus(d_DEFAULT, Times(e_DEFAULT, x_))), n_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(Times(b, c, Log(FSymbol), Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                          Power(Sech(Plus(d, Times(e, x))), Subtract(n, C2)),
                          Power(Times(Sqr(e), Subtract(n, C1), Subtract(n, C2)), CN1)), x),
                      Simp(
                          Times(Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                              Power(Sech(Plus(d, Times(e, x))), Subtract(n, C1)),
                              Sinh(Plus(d, Times(e, x))), Power(Times(e, Subtract(n, C1)), CN1)),
                          x)),
                  And(FreeQ(List(FSymbol, a, b, c, d, e,
                      n), x), EqQ(
                          Subtract(
                              Times(Sqr(e), Sqr(Subtract(n, C2))), Times(Sqr(b), Sqr(c),
                                  Sqr(Log(FSymbol)))),
                          C0),
                      NeQ(n, C1), NeQ(n, C2)))),
          IIntegrate(5489,
              Integrate(Times(
                  Power(Csch(Plus(d_DEFAULT, Times(e_DEFAULT, x_))), n_),
                  Power(F_, Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_))))), x_Symbol),
              Condition(
                  Subtract(Negate(
                      Simp(Times(b, c, Log(FSymbol), Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                          Power(Csch(Plus(d, Times(e, x))), Subtract(n, C2)),
                          Power(Times(Sqr(e), Subtract(n, C1), Subtract(n, C2)), CN1)), x)),
                      Simp(
                          Times(Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                              Power(Csch(Plus(d, Times(e, x))), Subtract(n, C1)),
                              Cosh(Plus(d, Times(e, x))), Power(Times(e, Subtract(n, C1)), CN1)),
                          x)),
                  And(FreeQ(List(FSymbol, a, b, c, d, e, n), x),
                      EqQ(Subtract(Times(Sqr(e), Sqr(Subtract(n, C2))),
                          Times(Sqr(b), Sqr(c), Sqr(Log(FSymbol)))), C0),
                      NeQ(n, C1), NeQ(n, C2)))),
          IIntegrate(5490,
              Integrate(
                  Times(
                      Power(F_, Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                      Power(Sech(Plus(d_DEFAULT, Times(e_DEFAULT, x_))), n_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(Times(b, c, Log(FSymbol), Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                          Power(Sech(Plus(d, Times(e, x))), Subtract(n, C2)),
                          Power(Times(Sqr(e), Subtract(n, C1), Subtract(n, C2)), CN1)), x),
                      Dist(
                          Times(
                              Subtract(Times(Sqr(e), Sqr(Subtract(n, C2))),
                                  Times(Sqr(b), Sqr(c), Sqr(Log(FSymbol)))),
                              Power(Times(Sqr(e), Subtract(n, C1), Subtract(n, C2)), CN1)),
                          Integrate(Times(Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                              Power(Sech(Plus(d, Times(e, x))), Subtract(n, C2))), x),
                          x),
                      Simp(
                          Times(Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                              Power(Sech(Plus(d, Times(e, x))), Subtract(n, C1)),
                              Sinh(Plus(d, Times(e, x))), Power(Times(e, Subtract(n, C1)), CN1)),
                          x)),
                  And(FreeQ(List(FSymbol, a, b, c, d,
                      e), x), NeQ(
                          Subtract(
                              Times(Sqr(e), Sqr(Subtract(n, C2))), Times(Sqr(b), Sqr(c),
                                  Sqr(Log(FSymbol)))),
                          C0),
                      GtQ(n, C1), NeQ(n, C2)))),
          IIntegrate(5491,
              Integrate(
                  Times(
                      Power(Csch(Plus(d_DEFAULT, Times(e_DEFAULT, x_))), n_), Power(F_, Times(
                          c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(b, c, Log(FSymbol),
                                  Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                                  Power(Csch(Plus(d,
                                      Times(e, x))), Subtract(n,
                                          C2)),
                                  Power(Times(Sqr(e), Subtract(n, C1), Subtract(n, C2)), CN1)),
                              x)),
                      Negate(Dist(
                          Times(
                              Subtract(Times(Sqr(e), Sqr(Subtract(n, C2))),
                                  Times(Sqr(b), Sqr(c), Sqr(Log(FSymbol)))),
                              Power(Times(Sqr(e), Subtract(n, C1), Subtract(n, C2)), CN1)),
                          Integrate(
                              Times(Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                                  Power(Csch(Plus(d, Times(e, x))), Subtract(n, C2))),
                              x),
                          x)),
                      Negate(
                          Simp(
                              Times(Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                                  Power(Csch(Plus(d, Times(e, x))), Subtract(n, C1)),
                                  Cosh(Plus(d, Times(e, x))), Power(Times(e, Subtract(n, C1)),
                                      CN1)),
                              x))),
                  And(FreeQ(List(FSymbol, a, b, c, d,
                      e), x), NeQ(
                          Subtract(Times(Sqr(e), Sqr(Subtract(n, C2))),
                              Times(Sqr(b), Sqr(c), Sqr(Log(FSymbol)))),
                          C0),
                      GtQ(n, C1), NeQ(n, C2)))),
          IIntegrate(5492,
              Integrate(
                  Times(
                      Power(F_, Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                      Power(Sech(Plus(d_DEFAULT, Times(e_DEFAULT, x_))), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(Power(C2, n), Exp(Times(n, Plus(d, Times(e, x)))),
                          Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                          Hypergeometric2F1(n,
                              Plus(Times(C1D2,
                                  n), Times(b, c, Log(FSymbol), Power(Times(C2, e), CN1))),
                              Plus(C1, Times(C1D2, n),
                                  Times(b, c, Log(FSymbol), Power(Times(C2, e), CN1))),
                              Negate(Exp(Times(C2, Plus(d, Times(e, x)))))),
                          Power(Plus(Times(e, n), Times(b, c, Log(FSymbol))), CN1)),
                      x),
                  And(FreeQ(List(FSymbol, a, b, c, d, e), x), IntegerQ(n)))),
          IIntegrate(5493,
              Integrate(Times(
                  Power(Csch(Plus(d_DEFAULT, Times(e_DEFAULT, x_))), n_DEFAULT), Power(F_, Times(
                      c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_))))),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(Power(CN2, n), Exp(Times(n, Plus(d, Times(e, x)))),
                          Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                          Hypergeometric2F1(n,
                              Plus(Times(C1D2,
                                  n), Times(b, c, Log(FSymbol), Power(Times(C2, e), CN1))),
                              Plus(
                                  C1, Times(C1D2, n), Times(b, c, Log(FSymbol), Power(Times(C2, e),
                                      CN1))),
                              Exp(Times(C2, Plus(d, Times(e, x))))),
                          Power(Plus(Times(e, n), Times(b, c, Log(FSymbol))), CN1)),
                      x),
                  And(FreeQ(List(FSymbol, a, b, c, d, e), x), IntegerQ(n)))),
          IIntegrate(5494,
              Integrate(
                  Times(
                      Power(F_, Times(c_DEFAULT, Plus(a_DEFAULT,
                          Times(b_DEFAULT, x_)))),
                      Power(Sech(Plus(d_DEFAULT, Times(e_DEFAULT, x_))), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Plus(C1, Exp(Times(C2, Plus(d, Times(e, x))))), n),
                          Power(Sech(Plus(d, Times(e, x))), n),
                          Power(Exp(Times(n, Plus(d, Times(e, x)))), CN1)),
                      Integrate(
                          SimplifyIntegrand(
                              Times(Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                                  Exp(Times(n, Plus(d, Times(e, x)))),
                                  Power(Power(Plus(C1, Exp(Times(C2, Plus(d, Times(e, x))))), n),
                                      CN1)),
                              x),
                          x),
                      x),
                  And(FreeQ(List(FSymbol, a, b, c, d, e), x), Not(IntegerQ(n))))),
          IIntegrate(5495,
              Integrate(
                  Times(
                      Power(Csch(Plus(d_DEFAULT,
                          Times(e_DEFAULT, x_))), n_DEFAULT),
                      Power(F_, Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_))))),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Subtract(C1, Exp(Times(CN2, Plus(d, Times(e, x))))), n),
                          Power(Csch(
                              Plus(d, Times(e, x))), n),
                          Power(Exp(Times(CN1, n, Plus(d, Times(e, x)))), CN1)),
                      Integrate(
                          SimplifyIntegrand(
                              Times(Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                                  Power(
                                      Times(Exp(Times(n, Plus(d, Times(e, x)))),
                                          Power(Subtract(C1, Exp(Times(CN2, Plus(d, Times(e, x))))),
                                              n)),
                                      CN1)),
                              x),
                          x),
                      x),
                  And(FreeQ(List(FSymbol, a, b, c, d, e), x), Not(IntegerQ(n))))),
          IIntegrate(5496,
              Integrate(
                  Times(Power(F_, Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                      Power(Plus(f_, Times(g_DEFAULT, Sinh(Plus(d_DEFAULT, Times(e_DEFAULT, x_))))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(C2, n), Power(f,
                          n)),
                      Integrate(
                          Times(Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                              Power(
                                  Cosh(
                                      Subtract(Plus(Times(C1D2, d), Times(C1D2, e, x)),
                                          Times(f, Pi, Power(Times(C4, g), CN1)))),
                                  Times(C2, n))),
                          x),
                      x),
                  And(FreeQ(List(FSymbol, a, b, c, d, e, f, g), x), EqQ(Plus(Sqr(f), Sqr(g)), C0),
                      ILtQ(n, C0)))),
          IIntegrate(5497,
              Integrate(
                  Times(
                      Power(
                          Plus(Times(Cosh(Plus(d_DEFAULT, Times(e_DEFAULT, x_))), g_DEFAULT), f_),
                          n_DEFAULT),
                      Power(F_, Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_))))),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(C2, n), Power(g,
                          n)),
                      Integrate(
                          Times(
                              Power(FSymbol, Times(c, Plus(a, Times(b,
                                  x)))),
                              Power(Cosh(Plus(Times(C1D2, d), Times(C1D2, e, x))), Times(C2, n))),
                          x),
                      x),
                  And(FreeQ(List(FSymbol, a, b, c, d, e, f, g), x), EqQ(Subtract(f, g), C0),
                      ILtQ(n, C0)))),
          IIntegrate(5498,
              Integrate(
                  Times(
                      Power(
                          Plus(Times(Cosh(Plus(d_DEFAULT, Times(e_DEFAULT, x_))), g_DEFAULT), f_),
                          n_DEFAULT),
                      Power(F_, Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_))))),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(C2, n), Power(g,
                          n)),
                      Integrate(
                          Times(Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                              Power(Sinh(Plus(Times(C1D2, d), Times(C1D2, e, x))), Times(C2, n))),
                          x),
                      x),
                  And(FreeQ(List(FSymbol, a, b, c, d, e, f, g), x), EqQ(Plus(f,
                      g), C0), ILtQ(n,
                          C0)))),
          IIntegrate(5499,
              Integrate(
                  Times(Power(Cosh(Plus(d_DEFAULT, Times(e_DEFAULT, x_))), m_DEFAULT),
                      Power(F_, Times(c_DEFAULT,
                          Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                      Power(Plus(f_, Times(g_DEFAULT, Sinh(Plus(d_DEFAULT, Times(e_DEFAULT, x_))))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(g, n),
                      Integrate(
                          Times(
                              Power(FSymbol, Times(c,
                                  Plus(a, Times(b, x)))),
                              Power(
                                  Tanh(Subtract(Plus(Times(C1D2, d), Times(C1D2, e, x)),
                                      Times(f, Pi, Power(Times(C4, g), CN1)))),
                                  m)),
                          x),
                      x),
                  And(FreeQ(List(FSymbol, a, b, c, d, e, f, g), x), EqQ(Plus(Sqr(f),
                      Sqr(g)), C0), IntegersQ(m,
                          n),
                      EqQ(Plus(m, n), C0)))),
          IIntegrate(5500,
              Integrate(Times(
                  Power(Plus(Times(Cosh(Plus(d_DEFAULT, Times(e_DEFAULT, x_))), g_DEFAULT), f_),
                      n_DEFAULT),
                  Power(F_, Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                  Power(Sinh(Plus(d_DEFAULT, Times(e_DEFAULT, x_))), m_DEFAULT)), x_Symbol),
              Condition(
                  Dist(Power(g, n),
                      Integrate(Times(Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                          Power(Tanh(Plus(Times(C1D2, d), Times(C1D2, e, x))), m)), x),
                      x),
                  And(FreeQ(List(FSymbol, a, b, c, d, e, f, g), x), EqQ(Subtract(f, g), C0),
                      IntegersQ(m, n), EqQ(Plus(m, n), C0)))));
}
