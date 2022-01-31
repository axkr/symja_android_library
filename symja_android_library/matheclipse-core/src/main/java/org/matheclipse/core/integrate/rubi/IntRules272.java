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
class IntRules272 {
  public static IAST RULES =
      List(
          IIntegrate(5441,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  Csch(Plus(c_DEFAULT, Times(d_DEFAULT,
                                      Power(x_, n_)))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      Power(Times(e_, x_), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(e, IntPart(m)), Power(Times(e, x),
                              FracPart(m)),
                          Power(Power(x, FracPart(m)), CN1)),
                      Integrate(
                          Times(Power(x, m),
                              Power(Plus(a, Times(b, Csch(Plus(c, Times(d, Power(x, n)))))), p)),
                          x),
                      x),
                  FreeQ(List(a, b, c, d, e, m, n, p), x))),
          IIntegrate(5442,
              Integrate(
                  Times(
                      Power(Times(e_,
                          x_), m_DEFAULT),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, Sech(u_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          Power(Times(e,
                              x), m),
                          Power(Plus(a, Times(b, Sech(ExpandToSum(u, x)))), p)),
                      x),
                  And(FreeQ(List(a, b, e, m, p), x), BinomialQ(u, x), Not(BinomialMatchQ(u, x))))),
          IIntegrate(5443,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(Csch(u_), b_DEFAULT)), p_DEFAULT),
                      Power(Times(e_, x_), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          Power(Times(e, x), m), Power(Plus(a, Times(b, Csch(ExpandToSum(u, x)))),
                              p)),
                      x),
                  And(FreeQ(List(a, b, e, m, p), x), BinomialQ(u, x), Not(BinomialMatchQ(u, x))))),
          IIntegrate(5444,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(Sech(Plus(a_DEFAULT,
                          Times(b_DEFAULT, Power(x_, n_DEFAULT)))), p_),
                      Sinh(Plus(a_DEFAULT, Times(b_DEFAULT, Power(x_, n_DEFAULT))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(Times(Power(x, Plus(m, Negate(n), C1)),
                              Power(Sech(Plus(a, Times(b, Power(x, n)))), Subtract(p, C1)),
                              Power(Times(b, n, Subtract(p, C1)), CN1)), x)),
                      Dist(Times(Plus(m, Negate(n), C1), Power(Times(b, n, Subtract(p, C1)), CN1)),
                          Integrate(Times(
                              Power(x, Subtract(m, n)), Power(Sech(Plus(a, Times(b, Power(x, n)))),
                                  Subtract(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, p), x), IntegerQ(n), GeQ(Subtract(m, n), C0), NeQ(p, C1)))),
          IIntegrate(5445,
              Integrate(
                  Times(
                      Cosh(Plus(a_DEFAULT, Times(b_DEFAULT,
                          Power(x_, n_DEFAULT)))),
                      Power(Csch(
                          Plus(a_DEFAULT, Times(b_DEFAULT, Power(x_, n_DEFAULT)))), p_),
                      Power(x_, m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(Times(Power(x, Plus(m, Negate(n), C1)),
                              Power(Csch(Plus(a, Times(b, Power(x, n)))), Subtract(p, C1)), Power(
                                  Times(b, n, Subtract(p, C1)), CN1)),
                              x)),
                      Dist(
                          Times(Plus(m, Negate(
                              n), C1), Power(Times(b, n, Subtract(p, C1)),
                                  CN1)),
                          Integrate(
                              Times(Power(x, Subtract(m, n)),
                                  Power(Csch(Plus(a, Times(b, Power(x, n)))), Subtract(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, p), x), IntegerQ(n), GeQ(Subtract(m, n), C0), NeQ(p, C1)))),
          IIntegrate(5446,
              Integrate(
                  Times(Cosh(Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT), Power(Sinh(
                          Plus(a_DEFAULT, Times(b_DEFAULT, x_))), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Plus(c, Times(d, x)), m),
                              Power(Sinh(Plus(a, Times(b, x))),
                                  Plus(n, C1)),
                              Power(Times(b, Plus(n, C1)), CN1)),
                          x),
                      Dist(
                          Times(d, m, Power(Times(b, Plus(n, C1)),
                              CN1)),
                          Integrate(
                              Times(
                                  Power(Plus(c, Times(d, x)), Subtract(m, C1)), Power(
                                      Sinh(Plus(a, Times(b, x))), Plus(n, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, n), x), IGtQ(m, C0), NeQ(n, CN1)))),
          IIntegrate(5447,
              Integrate(
                  Times(
                      Power(Cosh(Plus(a_DEFAULT,
                          Times(b_DEFAULT, x_))), n_DEFAULT),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT), Sinh(
                          Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Plus(c, Times(d, x)), m),
                              Power(Cosh(Plus(a, Times(b, x))), Plus(n,
                                  C1)),
                              Power(Times(b, Plus(n, C1)), CN1)),
                          x),
                      Dist(
                          Times(d, m, Power(Times(b, Plus(n, C1)),
                              CN1)),
                          Integrate(
                              Times(Power(Plus(c, Times(d, x)), Subtract(m, C1)),
                                  Power(Cosh(Plus(a, Times(b, x))), Plus(n, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, n), x), IGtQ(m, C0), NeQ(n, CN1)))),
          IIntegrate(5448,
              Integrate(
                  Times(Power(Cosh(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), p_DEFAULT),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT,
                          x_)), m_DEFAULT),
                      Power(Sinh(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandTrigReduce(
                          Power(Plus(c,
                              Times(d, x)), m),
                          Times(Power(Sinh(Plus(a, Times(b, x))), n),
                              Power(Cosh(Plus(a, Times(b, x))), p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, m), x), IGtQ(n, C0), IGtQ(p, C0)))),
          IIntegrate(5449,
              Integrate(
                  Times(Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT),
                      Power(Sinh(Plus(a_DEFAULT,
                          Times(b_DEFAULT, x_))), n_DEFAULT),
                      Power(Tanh(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Integrate(
                          Times(Power(Plus(c, Times(d, x)), m),
                              Power(Sinh(Plus(a, Times(b, x))), n), Power(
                                  Tanh(Plus(a, Times(b, x))), Subtract(p, C2))),
                          x),
                      Integrate(
                          Times(Power(Plus(c, Times(d, x)), m),
                              Power(Sinh(Plus(a, Times(b, x))), Subtract(n, C2)), Power(
                                  Tanh(Plus(a, Times(b, x))), p)),
                          x)),
                  And(FreeQ(List(a, b, c, d, m), x), IGtQ(n, C0), IGtQ(p, C0)))),
          IIntegrate(5450,
              Integrate(Times(Power(Cosh(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), n_DEFAULT),
                  Power(Coth(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), p_DEFAULT),
                  Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT)), x_Symbol),
              Condition(
                  Plus(
                      Integrate(
                          Times(Power(Plus(c, Times(d, x)), m),
                              Power(Cosh(Plus(a, Times(b, x))), n), Power(
                                  Coth(Plus(a, Times(b, x))), Subtract(p, C2))),
                          x),
                      Integrate(Times(Power(Plus(c, Times(d, x)), m),
                          Power(Cosh(Plus(a, Times(b, x))), Subtract(n, C2)), Power(
                              Coth(Plus(a, Times(b, x))), p)),
                          x)),
                  And(FreeQ(List(a, b, c, d, m), x), IGtQ(n, C0), IGtQ(p, C0)))),
          IIntegrate(5451,
              Integrate(
                  Times(Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT),
                      Power(Sech(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), n_DEFAULT), Power(Tanh(
                          Plus(a_DEFAULT, Times(b_DEFAULT, x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(
                                  Power(Plus(c, Times(d, x)), m), Power(Sech(Plus(a, Times(b, x))),
                                      n),
                                  Power(Times(b, n), CN1)),
                              x)),
                      Dist(Times(d, m, Power(Times(b, n), CN1)),
                          Integrate(
                              Times(Power(Plus(c, Times(d, x)), Subtract(m, C1)),
                                  Power(Sech(Plus(a, Times(b, x))), n)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, n), x), EqQ(p, C1), GtQ(m, C0)))),
          IIntegrate(5452,
              Integrate(
                  Times(
                      Power(Coth(Plus(a_DEFAULT, Times(b_DEFAULT,
                          x_))), p_DEFAULT),
                      Power(Csch(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), n_DEFAULT), Power(
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(Times(Power(Plus(c, Times(d, x)), m),
                              Power(Csch(Plus(a, Times(b, x))), n), Power(Times(b, n), CN1)),
                              x)),
                      Dist(Times(d, m, Power(Times(b, n), CN1)),
                          Integrate(Times(Power(Plus(c, Times(d, x)), Subtract(m, C1)),
                              Power(Csch(Plus(a, Times(b, x))), n)), x),
                          x)),
                  And(FreeQ(List(a, b, c, d, n), x), EqQ(p, C1), GtQ(m, C0)))),
          IIntegrate(5453,
              Integrate(
                  Times(Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT),
                      Sqr(Sech(Plus(a_DEFAULT, Times(b_DEFAULT, x_)))), Power(Tanh(
                          Plus(a_DEFAULT, Times(b_DEFAULT, x_))), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Plus(c, Times(d, x)), m),
                              Power(Tanh(Plus(a, Times(b, x))),
                                  Plus(n, C1)),
                              Power(Times(b, Plus(n, C1)), CN1)),
                          x),
                      Dist(Times(d, m, Power(Times(b, Plus(n, C1)), CN1)),
                          Integrate(
                              Times(Power(Plus(c, Times(d, x)), Subtract(m, C1)),
                                  Power(Tanh(Plus(a, Times(b, x))), Plus(n, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, n), x), IGtQ(m, C0), NeQ(n, CN1)))),
          IIntegrate(5454,
              Integrate(
                  Times(
                      Power(Coth(Plus(a_DEFAULT, Times(b_DEFAULT,
                          x_))), n_DEFAULT),
                      Sqr(Csch(Plus(a_DEFAULT, Times(b_DEFAULT, x_)))), Power(
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(Power(Plus(c, Times(d, x)), m),
                                  Power(Coth(Plus(a, Times(b, x))),
                                      Plus(n, C1)),
                                  Power(Times(b, Plus(n, C1)), CN1)),
                              x)),
                      Dist(
                          Times(d, m, Power(Times(b, Plus(n, C1)),
                              CN1)),
                          Integrate(
                              Times(Power(Plus(c, Times(d, x)), Subtract(m, C1)), Power(
                                  Coth(Plus(a, Times(b, x))), Plus(n, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, n), x), IGtQ(m, C0), NeQ(n, CN1)))),
          IIntegrate(5455,
              Integrate(
                  Times(Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT),
                      Sech(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), Power(
                          Tanh(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), p_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Integrate(
                          Times(
                              Power(Plus(c, Times(d, x)), m), Sech(Plus(a, Times(b, x))), Power(
                                  Tanh(Plus(a, Times(b, x))), Subtract(p, C2))),
                          x),
                      Integrate(
                          Times(Power(Plus(c, Times(d, x)), m),
                              Power(Sech(Plus(a, Times(b, x))), C3),
                              Power(Tanh(Plus(a, Times(b, x))), Subtract(p, C2))),
                          x)),
                  And(FreeQ(List(a, b, c, d, m), x), IGtQ(Times(C1D2, p), C0)))),
          IIntegrate(5456,
              Integrate(Times(Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT),
                  Power(Sech(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), n_DEFAULT),
                  Power(Tanh(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), p_)), x_Symbol),
              Condition(Subtract(
                  Integrate(
                      Times(Power(Plus(c, Times(d, x)), m), Power(Sech(Plus(a, Times(b, x))), n),
                          Power(Tanh(Plus(a, Times(b, x))), Subtract(p, C2))),
                      x),
                  Integrate(Times(Power(Plus(c, Times(d, x)), m),
                      Power(Sech(Plus(a, Times(b, x))), Plus(n, C2)),
                      Power(Tanh(Plus(a, Times(b, x))), Subtract(p, C2))), x)),
                  And(FreeQ(List(a, b, c, d, m, n), x), IGtQ(Times(C1D2, p), C0)))),
          IIntegrate(5457,
              Integrate(
                  Times(Power(Coth(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), p_),
                      Csch(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), Power(
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Integrate(
                          Times(
                              Power(Plus(c, Times(d, x)), m), Csch(Plus(a, Times(b, x))), Power(
                                  Coth(Plus(a, Times(b, x))), Subtract(p, C2))),
                          x),
                      Integrate(
                          Times(Power(Plus(c, Times(d, x)), m),
                              Power(Csch(Plus(a, Times(b, x))), C3), Power(
                                  Coth(Plus(a, Times(b, x))), Subtract(p, C2))),
                          x)),
                  And(FreeQ(List(a, b, c, d, m), x), IGtQ(Times(C1D2, p), C0)))),
          IIntegrate(5458,
              Integrate(
                  Times(Power(Coth(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), p_),
                      Power(Csch(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), n_DEFAULT), Power(
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Integrate(
                          Times(Power(Plus(c, Times(d, x)), m),
                              Power(Csch(Plus(a, Times(b, x))), n), Power(
                                  Coth(Plus(a, Times(b, x))), Subtract(p, C2))),
                          x),
                      Integrate(
                          Times(Power(Plus(c, Times(d, x)), m),
                              Power(Csch(Plus(a,
                                  Times(b, x))), Plus(n,
                                      C2)),
                              Power(Coth(Plus(a, Times(b, x))), Subtract(p, C2))),
                          x)),
                  And(FreeQ(List(a, b, c, d, m, n), x), IGtQ(Times(C1D2, p), C0)))),
          IIntegrate(5459,
              Integrate(Times(Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT),
                  Power(Sech(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), n_DEFAULT), Power(Tanh(
                      Plus(a_DEFAULT, Times(b_DEFAULT, x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(u,
                          IntHide(Times(Power(Sech(Plus(a, Times(b, x))), n),
                              Power(Tanh(Plus(a, Times(b, x))), p)), x))),
                      Subtract(Dist(Power(Plus(c, Times(d, x)), m), u, x),
                          Dist(Times(d, m),
                              Integrate(Times(Power(Plus(c, Times(d, x)), Subtract(m, C1)), u),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, n,
                      p), x), IGtQ(m, C0), Or(IntegerQ(Times(C1D2, n)),
                          IntegerQ(Times(C1D2, Subtract(p, C1))))))),
          IIntegrate(5460,
              Integrate(
                  Times(Power(Coth(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), p_DEFAULT),
                      Power(Csch(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), n_DEFAULT),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(u,
                          IntHide(Times(Power(Csch(Plus(a, Times(b, x))), n),
                              Power(Coth(Plus(a, Times(b, x))), p)), x))),
                      Subtract(Dist(Power(Plus(c, Times(d, x)), m), u, x),
                          Dist(Times(d, m),
                              Integrate(Times(Power(Plus(c, Times(d, x)), Subtract(m, C1)), u), x),
                              x))),
                  And(FreeQ(List(a, b, c, d, n, p), x), IGtQ(m, C0),
                      Or(IntegerQ(Times(C1D2, n)), IntegerQ(Times(C1D2, Subtract(p, C1))))))));
}
