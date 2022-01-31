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
class IntRules279 {
  public static IAST RULES =
      List(
          IIntegrate(5581,
              Integrate(
                  Times(
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT,
                          x_)), m_DEFAULT),
                      Power(Sinh(
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_))), p_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT,
                          Sinh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))))), CN1),
                      Power(Tanh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Power(b, CN1),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Sinh(Plus(c, Times(d, x))), Subtract(p, C1)),
                                  Power(Tanh(Plus(c, Times(d, x))), n)),
                              x),
                          x),
                      Dist(
                          Times(a, Power(b,
                              CN1)),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Sinh(Plus(c, Times(d,
                                      x))), Subtract(p, C1)),
                                  Power(Tanh(Plus(c, Times(d, x))), n),
                                  Power(Plus(a, Times(b, Sinh(Plus(c, Times(d, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(m, C0), IGtQ(n, C0), IGtQ(p, C0)))),
          IIntegrate(5582,
              Integrate(
                  Times(
                      Power(Cosh(Plus(c_DEFAULT,
                          Times(d_DEFAULT, x_))), p_DEFAULT),
                      Power(Coth(
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_))), n_DEFAULT),
                      Power(
                          Plus(Times(Cosh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), b_DEFAULT),
                              a_),
                          CN1),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Power(b, CN1),
                          Integrate(Times(Power(Plus(e, Times(f, x)), m),
                              Power(Cosh(Plus(c, Times(d, x))), Subtract(p, C1)),
                              Power(Coth(Plus(c, Times(d, x))), n)), x),
                          x),
                      Dist(Times(a, Power(b, CN1)),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Cosh(Plus(c, Times(d, x))), Subtract(p, C1)),
                                  Power(Coth(Plus(c, Times(d, x))), n),
                                  Power(Plus(a, Times(b, Cosh(Plus(c, Times(d, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(m, C0), IGtQ(n, C0), IGtQ(p, C0)))),
          IIntegrate(5583,
              Integrate(
                  Times(Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT),
                      Power(Sech(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), p_DEFAULT),
                      Power(
                          Plus(a_, Times(b_DEFAULT,
                              Sinh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))))),
                          CN1),
                      Power(Tanh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Power(b, CN1),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Sech(Plus(c, Times(d, x))), Plus(p, C1)),
                                  Power(Tanh(Plus(c, Times(d, x))), Subtract(n, C1))),
                              x),
                          x),
                      Dist(Times(a, Power(b, CN1)),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Sech(Plus(c, Times(d, x))), Plus(p, C1)),
                                  Power(Tanh(Plus(c, Times(d, x))), Subtract(n, C1)),
                                  Power(Plus(a, Times(b, Sinh(Plus(c, Times(d, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(m, C0), IGtQ(n, C0), IGtQ(p, C0)))),
          IIntegrate(5584,
              Integrate(
                  Times(Power(Coth(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), n_DEFAULT),
                      Power(Csch(
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_))), p_DEFAULT),
                      Power(
                          Plus(Times(Cosh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), b_DEFAULT),
                              a_),
                          CN1),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Power(b, CN1),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Csch(Plus(c, Times(d, x))), Plus(p, C1)),
                                  Power(Coth(Plus(c, Times(d, x))), Subtract(n, C1))),
                              x),
                          x),
                      Dist(Times(a, Power(b, CN1)),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Csch(Plus(c, Times(d, x))), Plus(p, C1)),
                                  Power(Coth(Plus(c, Times(d, x))), Subtract(n,
                                      C1)),
                                  Power(Plus(a, Times(b, Cosh(Plus(c, Times(d, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(m, C0), IGtQ(n, C0), IGtQ(p, C0)))),
          IIntegrate(5585,
              Integrate(
                  Times(
                      Power(Cosh(Plus(c_DEFAULT, Times(d_DEFAULT,
                          x_))), p_DEFAULT),
                      Power(Coth(Plus(c_DEFAULT,
                          Times(d_DEFAULT, x_))), n_DEFAULT),
                      Power(Plus(e_DEFAULT,
                          Times(f_DEFAULT, x_)), m_DEFAULT),
                      Power(
                          Plus(a_, Times(b_DEFAULT, Sinh(
                              Plus(c_DEFAULT, Times(d_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Power(a, CN1),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Cosh(Plus(c, Times(d, x))), p),
                                  Power(Coth(Plus(c, Times(d, x))), n)),
                              x),
                          x),
                      Dist(
                          Times(b, Power(a,
                              CN1)),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Cosh(Plus(c, Times(d, x))), Plus(p, C1)),
                                  Power(Coth(Plus(c, Times(d, x))), Subtract(n, C1)),
                                  Power(Plus(a, Times(b, Sinh(Plus(c, Times(d, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(m, C0), IGtQ(n, C0), IGtQ(p, C0)))),
          IIntegrate(5586,
              Integrate(
                  Times(
                      Power(Plus(
                          Times(Cosh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), b_DEFAULT), a_), CN1),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT),
                      Power(Sinh(Plus(c_DEFAULT,
                          Times(d_DEFAULT, x_))), p_DEFAULT),
                      Power(Tanh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Power(a, CN1),
                          Integrate(Times(Power(Plus(e, Times(f, x)), m),
                              Power(Sinh(Plus(c, Times(d, x))),
                                  p),
                              Power(Tanh(Plus(c, Times(d, x))), n)), x),
                          x),
                      Dist(
                          Times(b, Power(a,
                              CN1)),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Sinh(Plus(c, Times(d, x))), Plus(p, C1)),
                                  Power(Tanh(Plus(c, Times(d, x))), Subtract(n, C1)), Power(Plus(a,
                                      Times(b, Cosh(Plus(c, Times(d, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(m, C0), IGtQ(n, C0), IGtQ(p, C0)))),
          IIntegrate(5587,
              Integrate(
                  Times(Power(Coth(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), n_DEFAULT),
                      Power(Csch(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), p_DEFAULT),
                      Power(Plus(e_DEFAULT,
                          Times(f_DEFAULT, x_)), m_DEFAULT),
                      Power(
                          Plus(a_, Times(b_DEFAULT, Sinh(
                              Plus(c_DEFAULT, Times(d_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Power(a, CN1),
                          Integrate(Times(Power(Plus(e, Times(f, x)), m),
                              Power(Csch(Plus(c, Times(d, x))),
                                  p),
                              Power(Coth(Plus(c, Times(d, x))), n)), x),
                          x),
                      Dist(
                          Times(b, Power(a,
                              CN1)),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Csch(Plus(c, Times(d, x))), Subtract(p, C1)),
                                  Power(Coth(Plus(c, Times(d, x))), n),
                                  Power(Plus(a, Times(b, Sinh(Plus(c, Times(d, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(m, C0), IGtQ(n, C0), IGtQ(p, C0)))),
          IIntegrate(5588,
              Integrate(
                  Times(
                      Power(Plus(
                          Times(Cosh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), b_DEFAULT), a_), CN1),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT),
                      Power(Sech(Plus(c_DEFAULT,
                          Times(d_DEFAULT, x_))), p_DEFAULT),
                      Power(Tanh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Power(a, CN1),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Sech(Plus(c, Times(d, x))), p),
                                  Power(Tanh(Plus(c, Times(d, x))), n)),
                              x),
                          x),
                      Dist(Times(b, Power(a, CN1)),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Sech(Plus(c, Times(d, x))), Subtract(p, C1)),
                                  Power(Tanh(Plus(c, Times(d, x))), n), Power(Plus(a,
                                      Times(b, Cosh(Plus(c, Times(d, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(m, C0), IGtQ(n, C0), IGtQ(p, C0)))),
          IIntegrate(5589,
              Integrate(
                  Times(
                      Power(Csch(Plus(c_DEFAULT, Times(d_DEFAULT,
                          x_))), n_DEFAULT),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT,
                          x_)), m_DEFAULT),
                      Power(Sech(
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_))), p_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Sinh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Power(a, CN1),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Sech(
                                      Plus(c, Times(d, x))), p),
                                  Power(Csch(Plus(c, Times(d, x))), n)),
                              x),
                          x),
                      Dist(Times(b, Power(a, CN1)), Integrate(Times(Power(Plus(e, Times(f, x)), m),
                          Power(Sech(Plus(c, Times(d, x))), p),
                          Power(Csch(Plus(c, Times(d, x))), Subtract(n, C1)), Power(
                              Plus(a, Times(b, Sinh(Plus(c, Times(d, x))))), CN1)),
                          x), x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(m, C0), IGtQ(n, C0), IGtQ(p, C0)))),
          IIntegrate(5590,
              Integrate(
                  Times(
                      Power(Csch(
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_))), p_DEFAULT),
                      Power(Plus(
                          Times(Cosh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), b_DEFAULT), a_), CN1),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT,
                          x_)), m_DEFAULT),
                      Power(Sech(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Power(a, CN1),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Csch(Plus(c, Times(d, x))), p),
                                  Power(Sech(Plus(c, Times(d, x))), n)),
                              x),
                          x),
                      Dist(Times(b, Power(a, CN1)),
                          Integrate(Times(Power(Plus(e, Times(f, x)), m),
                              Power(Csch(Plus(c, Times(d, x))), p),
                              Power(Sech(Plus(c, Times(d, x))), Subtract(n, C1)), Power(Plus(a,
                                  Times(b, Cosh(Plus(c, Times(d, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(m, C0), IGtQ(n, C0), IGtQ(p, C0)))),
          IIntegrate(5591,
              Integrate(
                  Times(Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT),
                      Power(Plus(a_,
                          Times(b_DEFAULT, Sinh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))))), CN1),
                      Power($(F_, Plus(c_DEFAULT,
                          Times(d_DEFAULT, x_))), n_DEFAULT),
                      Power($(G_, Plus(c_DEFAULT, Times(d_DEFAULT, x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(Power(Plus(e, Times(f, x)), m), Power(F(Plus(c, Times(d, x))), n),
                          Power(G(
                              Plus(c, Times(d, x))), p),
                          Power(Plus(a, Times(b, Sinh(Plus(c, Times(d, x))))), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n, p), x), HyperbolicQ(FSymbol),
                      HyperbolicQ(GSymbol)))),
          IIntegrate(5592,
              Integrate(
                  Times(
                      Power(Plus(
                          Times(Cosh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), b_DEFAULT), a_), CN1),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT),
                      Power($(F_, Plus(c_DEFAULT,
                          Times(d_DEFAULT, x_))), n_DEFAULT),
                      Power($(G_, Plus(c_DEFAULT, Times(d_DEFAULT, x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(Times(Power(Plus(e, Times(f, x)), m),
                      Power(F(Plus(c, Times(d, x))), n), Power(G(Plus(c, Times(d, x))),
                          p),
                      Power(Plus(a, Times(b, Cosh(Plus(c, Times(d, x))))), CN1)), x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n,
                      p), x), HyperbolicQ(
                          FSymbol),
                      HyperbolicQ(GSymbol)))),
          IIntegrate(5593,
              Integrate(
                  Times(Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT),
                      Power(
                          Plus(a_, Times(b_DEFAULT,
                              Sech(Plus(c_DEFAULT, Times(d_DEFAULT, x_))))),
                          CN1),
                      Power($(F_, Plus(c_DEFAULT, Times(d_DEFAULT, x_))), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(Power(Plus(e, Times(f, x)), m), Cosh(Plus(c, Times(d, x))),
                          Power(F(Plus(c, Times(d, x))), n), Power(
                              Plus(b, Times(a, Cosh(Plus(c, Times(d, x))))), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f), x), HyperbolicQ(FSymbol), IntegersQ(m, n)))),
          IIntegrate(5594,
              Integrate(Times(
                  Power(Plus(Times(Csch(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), b_DEFAULT), a_),
                      CN1),
                  Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT),
                  Power($(F_, Plus(c_DEFAULT, Times(d_DEFAULT, x_))), n_DEFAULT)), x_Symbol),
              Condition(Integrate(Times(Power(Plus(e, Times(f, x)), m), Sinh(Plus(c, Times(d, x))),
                  Power(F(Plus(c, Times(d, x))),
                      n),
                  Power(Plus(b, Times(a, Sinh(Plus(c, Times(d, x))))), CN1)), x),
                  And(FreeQ(List(a, b, c, d, e, f), x), HyperbolicQ(FSymbol), IntegersQ(m, n)))),
          IIntegrate(5595,
              Integrate(
                  Times(
                      Power(Plus(e_DEFAULT,
                          Times(f_DEFAULT, x_)), m_DEFAULT),
                      Power(Plus(a_,
                          Times(b_DEFAULT, Sech(Plus(c_DEFAULT, Times(d_DEFAULT, x_))))), CN1),
                      Power($(F_, Plus(c_DEFAULT,
                          Times(d_DEFAULT, x_))), n_DEFAULT),
                      Power($(G_, Plus(c_DEFAULT, Times(d_DEFAULT, x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(Times(Power(Plus(e, Times(f, x)), m), Cosh(Plus(c, Times(d, x))),
                      Power(F(Plus(c, Times(d, x))), n), Power(G(Plus(c, Times(d, x))),
                          p),
                      Power(Plus(b, Times(a, Cosh(Plus(c, Times(d, x))))), CN1)), x),
                  And(FreeQ(List(a, b, c, d, e,
                      f), x), HyperbolicQ(
                          FSymbol),
                      HyperbolicQ(GSymbol), IntegersQ(m, n, p)))),
          IIntegrate(5596,
              Integrate(
                  Times(
                      Power(
                          Plus(Times(Csch(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), b_DEFAULT), a_),
                          CN1),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT),
                      Power($(F_, Plus(c_DEFAULT,
                          Times(d_DEFAULT, x_))), n_DEFAULT),
                      Power($(G_, Plus(c_DEFAULT, Times(d_DEFAULT, x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(Power(Plus(e, Times(f, x)), m), Sinh(Plus(c, Times(d, x))),
                          Power(F(Plus(c, Times(d, x))), n), Power(G(
                              Plus(c, Times(d, x))), p),
                          Power(Plus(b, Times(a, Sinh(Plus(c, Times(d, x))))), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e,
                      f), x), HyperbolicQ(
                          FSymbol),
                      HyperbolicQ(GSymbol), IntegersQ(m, n, p)))),
          IIntegrate(5597,
              Integrate(
                  Times(
                      Power(Sinh(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), p_DEFAULT), Power(Sinh(
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(Power(C2, Plus(p, q)), CN1),
                      Integrate(
                          ExpandIntegrand(
                              Power(Plus(Negate(Exp(Subtract(Negate(c), Times(d, x)))),
                                  Exp(Plus(c, Times(d, x)))), q),
                              Power(Plus(Negate(Exp(Subtract(Negate(a), Times(b, x)))),
                                  Exp(Plus(a, Times(b, x)))), p),
                              x),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, q), x), IGtQ(p, C0), Not(IntegerQ(q))))),
          IIntegrate(5598,
              Integrate(
                  Times(
                      Power(Cosh(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), p_DEFAULT), Power(Cosh(
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Power(C2,
                          Plus(p, q)), CN1),
                      Integrate(
                          ExpandIntegrand(
                              Power(
                                  Plus(Exp(Subtract(Negate(c), Times(d, x))),
                                      Exp(Plus(c, Times(d, x)))),
                                  q),
                              Power(
                                  Plus(
                                      Exp(Subtract(Negate(a), Times(b, x))), Exp(
                                          Plus(a, Times(b, x)))),
                                  p),
                              x),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, q), x), IGtQ(p, C0), Not(IntegerQ(q))))),
          IIntegrate(5599,
              Integrate(
                  Times(
                      Power(Cosh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), q_DEFAULT), Power(Sinh(
                          Plus(a_DEFAULT, Times(b_DEFAULT, x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Power(C2,
                          Plus(p, q)), CN1),
                      Integrate(
                          ExpandIntegrand(
                              Power(
                                  Plus(Exp(Subtract(Negate(c), Times(d, x))),
                                      Exp(Plus(c, Times(d, x)))),
                                  q),
                              Power(
                                  Plus(
                                      Negate(Exp(
                                          Subtract(Negate(a), Times(b, x)))),
                                      Exp(Plus(a, Times(b, x)))),
                                  p),
                              x),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, q), x), IGtQ(p, C0), Not(IntegerQ(q))))),
          IIntegrate(5600,
              Integrate(Times(Power(Cosh(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), p_DEFAULT),
                  Power(Sinh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), q_DEFAULT)), x_Symbol),
              Condition(
                  Dist(Power(Power(C2, Plus(p, q)), CN1), Integrate(ExpandIntegrand(
                      Power(Plus(Negate(Exp(Subtract(Negate(c), Times(d, x)))),
                          Exp(Plus(c, Times(d, x)))), q),
                      Power(Plus(Exp(Subtract(Negate(a), Times(b, x))), Exp(Plus(a, Times(b, x)))),
                          p),
                      x), x), x),
                  And(FreeQ(List(a, b, c, d, q), x), IGtQ(p, C0), Not(IntegerQ(q))))));
}
