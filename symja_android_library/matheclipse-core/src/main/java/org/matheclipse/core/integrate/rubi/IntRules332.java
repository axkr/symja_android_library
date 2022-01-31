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
class IntRules332 {
  public static IAST RULES =
      List(
          IIntegrate(6641,
              Integrate(
                  Times(
                      Power(x_, CN1), Power(Plus(d_,
                          Times(d_DEFAULT, ProductLog(Times(a_DEFAULT, x_)))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Simp(Times(Log(ProductLog(Times(a, x))),
                      Power(d, CN1)), x),
                  FreeQ(List(a, d), x))),
          IIntegrate(6642,
              Integrate(
                  Times(
                      Power(x_, m_DEFAULT), Power(Plus(d_,
                          Times(d_DEFAULT, ProductLog(Times(a_DEFAULT, x_)))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(Times(Power(x, Plus(m, C1)),
                          Power(Times(d, Plus(m, C1)), CN1)), x),
                      Integrate(
                          Times(
                              Power(x, m), ProductLog(Times(a, x)), Power(
                                  Plus(d, Times(d, ProductLog(Times(a, x)))), CN1)),
                          x)),
                  And(FreeQ(List(a, d), x), LtQ(m, CN1)))),
          IIntegrate(6643,
              Integrate(
                  Times(
                      Power(x_, m_DEFAULT), Power(Plus(d_,
                          Times(d_DEFAULT, ProductLog(Times(a_DEFAULT, x_)))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(Power(x, m),
                          Gamma(Plus(m, C1), Times(CN1, Plus(m, C1), ProductLog(Times(a, x)))),
                          Power(
                              Times(a, d, Plus(m, C1), Exp(Times(m, ProductLog(Times(a, x)))),
                                  Power(Times(CN1, Plus(m, C1), ProductLog(Times(a, x))), m)),
                              CN1)),
                      x),
                  And(FreeQ(List(a, d, m), x), Not(IntegerQ(m))))),
          IIntegrate(6644,
              Integrate(
                  Times(Power(x_, CN1),
                      Power(
                          Plus(
                              d_, Times(d_DEFAULT, ProductLog(Times(a_DEFAULT,
                                  Power(x_, n_DEFAULT))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Simp(Times(Log(ProductLog(
                      Times(a, Power(x, n)))), Power(Times(d, n),
                          CN1)),
                      x),
                  FreeQ(List(a, d, n), x))),
          IIntegrate(6645,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(
                          Plus(d_, Times(d_DEFAULT, ProductLog(
                              Times(a_DEFAULT, Power(x_, n_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Negate(
                      Subst(
                          Integrate(
                              Power(
                                  Times(Power(x, Plus(m, C2)),
                                      Plus(d,
                                          Times(d, ProductLog(Times(a, Power(Power(x, n), CN1)))))),
                                  CN1),
                              x),
                          x, Power(x, CN1))),
                  And(FreeQ(List(a, d), x), IntegerQ(m), ILtQ(n, C0), NeQ(m, CN1)))),
          IIntegrate(6646,
              Integrate(
                  Times(Power(x_, CN1),
                      Power(Times(c_DEFAULT, ProductLog(Times(a_DEFAULT, Power(x_, n_DEFAULT)))),
                          p_DEFAULT),
                      Power(
                          Plus(d_,
                              Times(d_DEFAULT, ProductLog(Times(a_DEFAULT, Power(x_, n_DEFAULT))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(
                          Power(Times(c,
                              ProductLog(Times(a, Power(x, n)))), p),
                          Power(Times(d, n, p), CN1)),
                      x),
                  FreeQ(List(a, c, d, n, p), x))),
          IIntegrate(6647,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(
                          Times(c_DEFAULT, ProductLog(Times(a_DEFAULT, Power(x_, n_DEFAULT)))),
                          p_DEFAULT),
                      Power(
                          Plus(d_,
                              Times(d_DEFAULT, ProductLog(Times(a_DEFAULT, Power(x_, n_DEFAULT))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(c, Power(x, Plus(m, C1)),
                          Power(Times(c, ProductLog(Times(a, Power(x, n)))),
                              Subtract(p, C1)),
                          Power(Times(d, Plus(m, C1)), CN1)),
                      x),
                  And(FreeQ(List(a, c, d, m, n,
                      p), x), NeQ(m,
                          CN1),
                      EqQ(Plus(m, Times(n, Subtract(p, C1))), CN1)))),
          IIntegrate(6648,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(ProductLog(Times(a_DEFAULT, Power(x_, n_DEFAULT))), p_DEFAULT),
                      Power(
                          Plus(d_,
                              Times(d_DEFAULT, ProductLog(Times(a_DEFAULT, Power(x_, n_DEFAULT))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(Power(a, p),
                          ExpIntegralEi(Times(CN1, p,
                              ProductLog(Times(a, Power(x, n))))),
                          Power(Times(d, n), CN1)),
                      x),
                  And(FreeQ(List(a, d, m, n), x), IntegerQ(p), EqQ(Plus(m, Times(n, p)), CN1)))),
          IIntegrate(6649,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(Times(c_DEFAULT,
                          ProductLog(Times(a_DEFAULT, Power(x_, n_DEFAULT)))), p_),
                      Power(
                          Plus(d_,
                              Times(d_DEFAULT, ProductLog(Times(a_DEFAULT, Power(x_, n_DEFAULT))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(Power(a, Subtract(p, C1D2)), Power(c, Subtract(p, C1D2)),
                          Rt(Times(Pi, c, Power(Subtract(p, C1D2), CN1)), C2),
                          Erf(Times(Sqrt(Times(c, ProductLog(Times(a, Power(x, n))))),
                              Power(Rt(Times(c, Power(Subtract(p, C1D2), CN1)), C2), CN1))),
                          Power(Times(d, n), CN1)),
                      x),
                  And(FreeQ(List(a, c, d, m, n), x), NeQ(m, CN1), IntegerQ(Subtract(p, C1D2)),
                      EqQ(Plus(m,
                          Times(n, Subtract(p, C1D2))), CN1),
                      PosQ(Times(c, Power(Subtract(p, C1D2), CN1)))))),
          IIntegrate(6650,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(Times(c_DEFAULT,
                          ProductLog(Times(a_DEFAULT, Power(x_, n_DEFAULT)))), p_),
                      Power(
                          Plus(d_,
                              Times(d_DEFAULT, ProductLog(Times(a_DEFAULT, Power(x_, n_DEFAULT))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(Power(a, Subtract(p, C1D2)), Power(c, Subtract(p, C1D2)),
                          Rt(Times(CN1, Pi, c,
                              Power(Subtract(p, C1D2), CN1)), C2),
                          Erfi(
                              Times(Sqrt(Times(c, ProductLog(Times(a, Power(x, n))))),
                                  Power(Rt(Times(CN1, c, Power(Subtract(p, C1D2), CN1)), C2),
                                      CN1))),
                          Power(Times(d, n), CN1)),
                      x),
                  And(FreeQ(List(a, c, d, m, n), x), NeQ(m, CN1), IntegerQ(Subtract(p, C1D2)),
                      EqQ(Plus(m, Times(n, Subtract(p, C1D2))), CN1), NegQ(Times(c, Power(Subtract(
                          p, C1D2), CN1)))))),
          IIntegrate(6651,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(
                          Times(c_DEFAULT, ProductLog(
                              Times(a_DEFAULT, Power(x_, n_DEFAULT)))),
                          p_DEFAULT),
                      Power(
                          Plus(
                              d_, Times(d_DEFAULT, ProductLog(Times(a_DEFAULT,
                                  Power(x_, n_DEFAULT))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(c, Power(x, Plus(m, C1)),
                              Power(Times(c, ProductLog(
                                  Times(a, Power(x, n)))), Subtract(p,
                                      C1)),
                              Power(Times(d, Plus(m, C1)), CN1)),
                          x),
                      Dist(
                          Times(c, Plus(m, Times(n, Subtract(p, C1)), C1), Power(Plus(m, C1),
                              CN1)),
                          Integrate(Times(Power(x, m),
                              Power(Times(c, ProductLog(Times(a, Power(x, n)))), Subtract(p,
                                  C1)),
                              Power(Plus(d, Times(d, ProductLog(Times(a, Power(x, n))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, c, d, m, n, p), x), NeQ(m, CN1),
                      GtQ(Simplify(Plus(p, Times(Plus(m, C1), Power(n, CN1)))), C1)))),
          IIntegrate(6652,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(
                          Times(c_DEFAULT, ProductLog(
                              Times(a_DEFAULT, Power(x_, n_DEFAULT)))),
                          p_DEFAULT),
                      Power(
                          Plus(
                              d_, Times(d_DEFAULT, ProductLog(Times(a_DEFAULT,
                                  Power(x_, n_DEFAULT))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(x, Plus(m, C1)),
                              Power(Times(c,
                                  ProductLog(Times(a, Power(x, n)))), p),
                              Power(Times(d, Plus(m, Times(n, p), C1)), CN1)),
                          x),
                      Dist(
                          Times(Plus(m, C1), Power(Times(c, Plus(m, Times(n, p), C1)),
                              CN1)),
                          Integrate(Times(Power(x, m),
                              Power(Times(c, ProductLog(Times(a, Power(x, n)))), Plus(p,
                                  C1)),
                              Power(Plus(d, Times(d, ProductLog(Times(a, Power(x, n))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, c, d, m, n, p), x), NeQ(m, CN1),
                      LtQ(Simplify(Plus(p, Times(Plus(m, C1), Power(n, CN1)))), C0)))),
          IIntegrate(6653,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(Times(c_DEFAULT, ProductLog(
                          Times(a_DEFAULT, x_))), p_DEFAULT),
                      Power(Plus(d_, Times(d_DEFAULT, ProductLog(Times(a_DEFAULT, x_)))), CN1)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(Power(x, m),
                          Gamma(Plus(m, p, C1), Times(CN1, Plus(m,
                              C1), ProductLog(Times(a, x)))),
                          Power(Times(c,
                              ProductLog(Times(a, x))), p),
                          Power(
                              Times(
                                  a, d, Plus(m, C1), Exp(Times(m,
                                      ProductLog(Times(a, x)))),
                                  Power(
                                      Times(CN1, Plus(m,
                                          C1), ProductLog(Times(a, x))),
                                      Plus(m, p))),
                              CN1)),
                      x),
                  And(FreeQ(List(a, c, d, m, p), x), NeQ(m, CN1)))),
          IIntegrate(6654,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(
                          Times(c_DEFAULT, ProductLog(Times(a_DEFAULT, Power(x_,
                              n_DEFAULT)))),
                          p_DEFAULT),
                      Power(
                          Plus(
                              d_, Times(d_DEFAULT, ProductLog(Times(a_DEFAULT,
                                  Power(x_, n_DEFAULT))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Negate(
                      Subst(
                          Integrate(
                              Times(
                                  Power(Times(c, ProductLog(Times(a, Power(Power(x, n), CN1)))), p),
                                  Power(
                                      Times(Power(x, Plus(m, C2)),
                                          Plus(d,
                                              Times(d,
                                                  ProductLog(Times(a, Power(Power(x, n), CN1)))))),
                                      CN1)),
                              x),
                          x, Power(x, CN1))),
                  And(FreeQ(List(a, c, d, p), x), NeQ(m, CN1), IntegerQ(m), LtQ(n, C0)))),
          IIntegrate(6655, Integrate(u_, x_Symbol),
              Condition(
                  Subst(
                      Integrate(
                          SimplifyIntegrand(Times(Plus(x, C1), Exp(x),
                              SubstFor(ProductLog(x), u, x)), x),
                          x),
                      x, ProductLog(x)),
                  FunctionOfQ(ProductLog(x), u, x))),
          IIntegrate(
              6656, Integrate($($(Derivative(
                  n_), f_), x_), x_Symbol),
              Condition(Simp($($(Derivative(Subtract(n, C1)), f), x), x), FreeQ(List(f, n), x))),
          IIntegrate(6657,
              Integrate(
                  Times(
                      Power(
                          Times(c_DEFAULT,
                              Power(F_, Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                          p_DEFAULT),
                      $($(Derivative(n_), f_), x_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              Power(Times(c,
                                  Power(FSymbol, Plus(a, Times(b, x)))), p),
                              $($(Derivative(Subtract(n, C1)), f), x)),
                          x),
                      Dist(
                          Times(b, p, Log(
                              FSymbol)),
                          Integrate(
                              Times(
                                  Power(Times(c,
                                      Power(FSymbol, Plus(a, Times(b, x)))), p),
                                  $($(Derivative(Subtract(n, C1)), f), x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, f, FSymbol, p), x), IGtQ(n, C0)))),
          IIntegrate(6658,
              Integrate(
                  Times(
                      Power(
                          Times(c_DEFAULT,
                              Power(F_, Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                          p_DEFAULT),
                      $($(Derivative(n_), f_), x_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              Power(Times(c,
                                  Power(FSymbol, Plus(a, Times(b, x)))), p),
                              $($(Derivative(n), f), x), Power(Times(b, p, Log(FSymbol)), CN1)),
                          x),
                      Dist(
                          Power(Times(b, p,
                              Log(FSymbol)), CN1),
                          Integrate(
                              Times(
                                  Power(Times(c,
                                      Power(FSymbol, Plus(a, Times(b, x)))), p),
                                  $($(Derivative(Plus(n, C1)), f), x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, f, FSymbol, p), x), ILtQ(n, C0)))),
          IIntegrate(6659,
              Integrate(
                  Times(Sin(Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                      $($(Derivative(n_), f_), x_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Sin(Plus(a,
                              Times(b, x))), $($(Derivative(Subtract(n, C1)), f),
                                  x)),
                          x),
                      Dist(b,
                          Integrate(
                              Times(
                                  Cos(Plus(a,
                                      Times(b, x))),
                                  $($(Derivative(Subtract(n, C1)), f), x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, f), x), IGtQ(n, C0)))),
          IIntegrate(6660, Integrate(
              Times(Cos(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), $($(Derivative(n_), f_), x_)),
              x_Symbol),
              Condition(
                  Plus(Simp(
                      Times(Cos(Plus(a, Times(b, x))), $($(Derivative(Subtract(n, C1)), f), x)), x),
                      Dist(b,
                          Integrate(Times(Sin(Plus(a, Times(b, x))),
                              $($(Derivative(Subtract(n, C1)), f), x)), x),
                          x)),
                  And(FreeQ(List(a, b, f), x), IGtQ(n, C0)))));
}
