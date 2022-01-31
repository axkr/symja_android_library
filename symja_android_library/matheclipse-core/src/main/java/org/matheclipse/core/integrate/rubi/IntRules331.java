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
class IntRules331 {
  public static IAST RULES =
      List(
          IIntegrate(6621,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(
                          Times(c_DEFAULT, ProductLog(
                              Times(a_DEFAULT, Power(x_, n_)))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Negate(
                      Subst(
                          Integrate(
                              Times(
                                  Power(Times(c, ProductLog(Times(a, Power(Power(x, n), CN1)))),
                                      p),
                                  Power(Power(x, Plus(m, C2)), CN1)),
                              x),
                          x, Power(x, CN1))),
                  And(FreeQ(List(a, c, p), x), ILtQ(n, C0), IntegerQ(m), NeQ(m, CN1)))),
          IIntegrate(6622,
              Integrate(
                  Power(
                      Plus(d_, Times(d_DEFAULT, ProductLog(Plus(a_DEFAULT, Times(b_DEFAULT, x_))))),
                      CN1),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(
                          Plus(a, Times(b,
                              x)),
                          Power(Times(b, d, ProductLog(Plus(a, Times(b, x)))), CN1)),
                      x),
                  FreeQ(List(a, b, d), x))),
          IIntegrate(6623,
              Integrate(
                  Times(ProductLog(Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                      Power(
                          Plus(d_,
                              Times(d_DEFAULT, ProductLog(Plus(a_DEFAULT, Times(b_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Subtract(Simp(Times(d, x), x),
                      Integrate(Power(Plus(d, Times(d, ProductLog(Plus(a, Times(b, x))))), CN1),
                          x)),
                  FreeQ(List(a, b, d), x))),
          IIntegrate(6624,
              Integrate(
                  Times(
                      Power(Times(c_DEFAULT,
                          ProductLog(Plus(a_DEFAULT, Times(b_DEFAULT, x_)))), p_),
                      Power(
                          Plus(d_, Times(d_DEFAULT, ProductLog(
                              Plus(a_DEFAULT, Times(b_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(c, Plus(a, Times(b, x)),
                              Power(Times(c, ProductLog(
                                  Plus(a, Times(b, x)))), Subtract(p,
                                      C1)),
                              Power(Times(b, d), CN1)),
                          x),
                      Dist(Times(c, p),
                          Integrate(
                              Times(
                                  Power(Times(c, ProductLog(Plus(a, Times(b, x)))),
                                      Subtract(p, C1)),
                                  Power(Plus(d, Times(d, ProductLog(Plus(a, Times(b, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d), x), GtQ(p, C0)))),
          IIntegrate(6625,
              Integrate(
                  Times(
                      Power(ProductLog(
                          Plus(a_DEFAULT, Times(b_DEFAULT, x_))), CN1),
                      Power(
                          Plus(
                              d_, Times(d_DEFAULT, ProductLog(Plus(a_DEFAULT,
                                  Times(b_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(ExpIntegralEi(ProductLog(
                          Plus(a, Times(b, x)))), Power(Times(b, d),
                              CN1)),
                      x),
                  FreeQ(List(a, b, d), x))),
          IIntegrate(6626,
              Integrate(
                  Times(
                      Power(
                          Times(c_DEFAULT,
                              ProductLog(Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                          CN1D2),
                      Power(
                          Plus(d_,
                              Times(d_DEFAULT, ProductLog(Plus(a_DEFAULT, Times(b_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(
                          Rt(Times(Pi,
                              c), C2),
                          Erfi(
                              Times(
                                  Sqrt(Times(c, ProductLog(Plus(a, Times(b, x))))), Power(Rt(c, C2),
                                      CN1))),
                          Power(Times(b, c, d), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d), x), PosQ(c)))),
          IIntegrate(6627,
              Integrate(
                  Times(
                      Power(
                          Times(c_DEFAULT,
                              ProductLog(Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                          CN1D2),
                      Power(
                          Plus(d_,
                              Times(d_DEFAULT, ProductLog(Plus(a_DEFAULT, Times(b_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(
                          Rt(Times(CN1, Pi,
                              c), C2),
                          Erf(Times(Sqrt(Times(c, ProductLog(Plus(a, Times(b, x))))),
                              Power(Rt(Negate(c), C2), CN1))),
                          Power(Times(b, c, d), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d), x), NegQ(c)))),
          IIntegrate(6628,
              Integrate(
                  Times(
                      Power(Times(c_DEFAULT,
                          ProductLog(Plus(a_DEFAULT, Times(b_DEFAULT, x_)))), p_),
                      Power(
                          Plus(
                              d_, Times(d_DEFAULT, ProductLog(Plus(a_DEFAULT,
                                  Times(b_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Plus(a, Times(b, x)),
                              Power(Times(c,
                                  ProductLog(Plus(a, Times(b, x)))), p),
                              Power(Times(b, d, Plus(p, C1)), CN1)),
                          x),
                      Dist(
                          Power(Times(c,
                              Plus(p, C1)), CN1),
                          Integrate(
                              Times(
                                  Power(Times(c, ProductLog(Plus(a, Times(b, x)))), Plus(p,
                                      C1)),
                                  Power(Plus(d, Times(d, ProductLog(Plus(a, Times(b, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d), x), LtQ(p, CN1)))),
          IIntegrate(6629,
              Integrate(
                  Times(
                      Power(
                          Times(c_DEFAULT,
                              ProductLog(Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                          p_DEFAULT),
                      Power(
                          Plus(d_,
                              Times(d_DEFAULT, ProductLog(Plus(a_DEFAULT, Times(b_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(
                          Gamma(Plus(p, C1), Negate(ProductLog(
                              Plus(a, Times(b, x))))),
                          Power(Times(c, ProductLog(Plus(a, Times(b, x)))), p),
                          Power(Times(b, d, Power(Negate(ProductLog(Plus(a, Times(b, x)))), p)),
                              CN1)),
                      x),
                  FreeQ(List(a, b, c, d, p), x))),
          IIntegrate(6630,
              Integrate(
                  Times(
                      Power(Plus(e_DEFAULT,
                          Times(f_DEFAULT, x_)), m_DEFAULT),
                      Power(Plus(d_, Times(d_DEFAULT, ProductLog(Plus(a_, Times(b_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Power(b,
                          Plus(m, C1)), CN1),
                      Subst(
                          Integrate(
                              ExpandIntegrand(
                                  Power(Plus(d, Times(d,
                                      ProductLog(x))), CN1),
                                  Power(Plus(Times(b, e), Times(CN1, a, f), Times(f, x)), m), x),
                              x),
                          x, Plus(a, Times(b, x))),
                      x),
                  And(FreeQ(List(a, b, d, e, f), x), IGtQ(m, C0)))),
          IIntegrate(6631,
              Integrate(
                  Times(
                      Power(Plus(e_DEFAULT,
                          Times(f_DEFAULT, x_)), m_DEFAULT),
                      Power(Times(c_DEFAULT,
                          ProductLog(Plus(a_, Times(b_DEFAULT, x_)))), p_DEFAULT),
                      Power(Plus(d_, Times(d_DEFAULT, ProductLog(Plus(a_, Times(b_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Dist(Power(Power(b, Plus(m, C1)), CN1),
                      Subst(
                          Integrate(
                              ExpandIntegrand(
                                  Times(
                                      Power(Times(c, ProductLog(x)), p),
                                      Power(Plus(d, Times(d, ProductLog(x))), CN1)),
                                  Power(Plus(Times(b, e), Times(CN1, a, f), Times(f, x)), m), x),
                              x),
                          x, Plus(a, Times(b, x))),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, p), x), IGtQ(m, C0)))),
          IIntegrate(6632,
              Integrate(
                  Power(Plus(d_, Times(d_DEFAULT, ProductLog(Times(a_DEFAULT, Power(x_, n_))))),
                      CN1),
                  x_Symbol),
              Condition(
                  Negate(
                      Subst(
                          Integrate(
                              Power(
                                  Times(Sqr(x),
                                      Plus(d,
                                          Times(d, ProductLog(Times(a, Power(Power(x, n), CN1)))))),
                                  CN1),
                              x),
                          x, Power(x, CN1))),
                  And(FreeQ(List(a, d), x), ILtQ(n, C0)))),
          IIntegrate(6633,
              Integrate(
                  Times(
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
                      Times(c, x,
                          Power(Times(c, ProductLog(Times(a, Power(x, n)))),
                              Subtract(p, C1)),
                          Power(d, CN1)),
                      x),
                  And(FreeQ(List(a, c, d, n, p), x), EqQ(Times(n, Subtract(p, C1)), CN1)))),
          IIntegrate(6634,
              Integrate(
                  Times(Power(ProductLog(Times(a_DEFAULT, Power(x_, n_DEFAULT))), p_DEFAULT),
                      Power(
                          Plus(d_,
                              Times(d_DEFAULT, ProductLog(Times(a_DEFAULT, Power(x_, n_DEFAULT))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(Power(a, p),
                          ExpIntegralEi(Times(CN1, p, ProductLog(Times(a, Power(x, n))))), Power(
                              Times(d, n), CN1)),
                      x),
                  And(FreeQ(List(a, d), x), IntegerQ(p), EqQ(Times(n, p), CN1)))),
          IIntegrate(6635,
              Integrate(
                  Times(
                      Power(Times(c_DEFAULT,
                          ProductLog(Times(a_DEFAULT, Power(x_, n_DEFAULT)))), p_),
                      Power(
                          Plus(d_,
                              Times(d_DEFAULT, ProductLog(Times(a_DEFAULT, Power(x_, n_DEFAULT))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(
                          Rt(Times(Pi, c,
                              n), C2),
                          Erfi(
                              Times(Sqrt(Times(c, ProductLog(Times(a, Power(x, n))))),
                                  Power(Rt(Times(c, n), C2), CN1))),
                          Power(Times(d, n, Power(a, Power(n, CN1)), Power(c, Power(n, CN1))),
                              CN1)),
                      x),
                  And(FreeQ(List(a, c, d), x), IntegerQ(Power(n, CN1)),
                      EqQ(p, Subtract(C1D2, Power(n, CN1))), PosQ(Times(c, n))))),
          IIntegrate(6636,
              Integrate(
                  Times(
                      Power(Times(c_DEFAULT,
                          ProductLog(Times(a_DEFAULT, Power(x_, n_DEFAULT)))), p_),
                      Power(
                          Plus(d_,
                              Times(d_DEFAULT, ProductLog(Times(a_DEFAULT, Power(x_, n_DEFAULT))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(Rt(Times(CN1, Pi, c, n), C2),
                          Erf(Times(Sqrt(Times(c, ProductLog(Times(a, Power(x, n))))),
                              Power(Rt(Times(CN1, c, n), C2), CN1))),
                          Power(Times(d, n, Power(a, Power(n, CN1)), Power(c, Power(n, CN1))),
                              CN1)),
                      x),
                  And(FreeQ(List(a, c, d), x), IntegerQ(Power(n, CN1)),
                      EqQ(p, Subtract(C1D2, Power(n, CN1))), NegQ(Times(c, n))))),
          IIntegrate(6637,
              Integrate(
                  Times(
                      Power(
                          Times(c_DEFAULT, ProductLog(
                              Times(a_DEFAULT, Power(x_, n_DEFAULT)))),
                          p_DEFAULT),
                      Power(
                          Plus(d_,
                              Times(d_DEFAULT, ProductLog(Times(a_DEFAULT, Power(x_, n_DEFAULT))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(c, x,
                              Power(Times(c, ProductLog(
                                  Times(a, Power(x, n)))), Subtract(p,
                                      C1)),
                              Power(d, CN1)),
                          x),
                      Dist(
                          Times(c, Plus(Times(n, Subtract(p, C1)), C1)), Integrate(
                              Times(
                                  Power(Times(c, ProductLog(Times(a, Power(x, n)))),
                                      Subtract(p, C1)),
                                  Power(Plus(d, Times(d, ProductLog(Times(a, Power(x, n))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, c, d), x), GtQ(n, C0),
                      GtQ(Plus(Times(n, Subtract(p, C1)), C1), C0)))),
          IIntegrate(6638,
              Integrate(
                  Times(
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
                          Times(
                              x, Power(Times(c,
                                  ProductLog(Times(a, Power(x, n)))), p),
                              Power(Times(d, Plus(Times(n, p), C1)), CN1)),
                          x),
                      Dist(Power(Times(c, Plus(Times(n, p), C1)), CN1),
                          Integrate(
                              Times(Power(Times(c, ProductLog(Times(a, Power(x, n)))), Plus(p, C1)),
                                  Power(Plus(d, Times(d, ProductLog(Times(a, Power(x, n))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, c, d), x), GtQ(n, C0), LtQ(Plus(Times(n, p), C1), C0)))),
          IIntegrate(6639,
              Integrate(
                  Times(
                      Power(Times(c_DEFAULT,
                          ProductLog(Times(a_DEFAULT, Power(x_, n_)))), p_DEFAULT),
                      Power(
                          Plus(d_, Times(d_DEFAULT, ProductLog(
                              Times(a_DEFAULT, Power(x_, n_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Negate(
                      Subst(
                          Integrate(
                              Times(
                                  Power(Times(c, ProductLog(Times(a, Power(Power(x, n), CN1)))), p),
                                  Power(
                                      Times(Sqr(x),
                                          Plus(d,
                                              Times(d,
                                                  ProductLog(Times(a, Power(Power(x, n), CN1)))))),
                                      CN1)),
                              x),
                          x, Power(x, CN1))),
                  And(FreeQ(List(a, c, d, p), x), ILtQ(n, C0)))),
          IIntegrate(6640,
              Integrate(
                  Times(
                      Power(x_, m_DEFAULT), Power(Plus(d_,
                          Times(d_DEFAULT, ProductLog(Times(a_DEFAULT, x_)))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              Power(x, Plus(m,
                                  C1)),
                              Power(Times(d, Plus(m, C1), ProductLog(Times(a, x))), CN1)),
                          x),
                      Dist(Times(m, Power(Plus(m, C1), CN1)),
                          Integrate(Times(Power(x, m),
                              Power(Times(ProductLog(Times(a, x)),
                                  Plus(d, Times(d, ProductLog(Times(a, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, d), x), GtQ(m, C0)))));
}
