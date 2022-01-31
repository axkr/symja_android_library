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
class IntRules270 {
  public static IAST RULES =
      List(
          IIntegrate(5401,
              Integrate(
                  Power(
                      Plus(
                          a_DEFAULT, Times(Coth(Plus(c_DEFAULT, Times(d_DEFAULT, Power(x_, n_)))),
                              b_DEFAULT)),
                      p_DEFAULT),
                  x_Symbol),
              Condition(
                  Dist(Power(n, CN1),
                      Subst(
                          Integrate(
                              Times(
                                  Power(x, Subtract(Power(n, CN1),
                                      C1)),
                                  Power(Plus(a, Times(b, Coth(Plus(c, Times(d, x))))), p)),
                              x),
                          x, Power(x, n)),
                      x),
                  And(FreeQ(List(a, b, c, d, p), x), IGtQ(Power(n, CN1), C0), IntegerQ(p)))),
          IIntegrate(5402,
              Integrate(
                  Power(
                      Plus(
                          a_DEFAULT, Times(b_DEFAULT, Tanh(Plus(c_DEFAULT,
                              Times(d_DEFAULT, Power(x_, n_)))))),
                      p_DEFAULT),
                  x_Symbol),
              Condition(
                  Simp(
                      Integral(Power(Plus(a, Times(b, Tanh(Plus(c, Times(d, Power(x, n)))))), p),
                          x),
                      x),
                  FreeQ(List(a, b, c, d, n, p), x))),
          IIntegrate(5403,
              Integrate(
                  Power(
                      Plus(a_DEFAULT, Times(Coth(Plus(c_DEFAULT, Times(d_DEFAULT, Power(x_, n_)))),
                          b_DEFAULT)),
                      p_DEFAULT),
                  x_Symbol),
              Condition(
                  Simp(
                      Integral(Power(Plus(a, Times(b, Coth(Plus(c, Times(d, Power(x, n)))))), p),
                          x),
                      x),
                  FreeQ(List(a, b, c, d, n, p), x))),
          IIntegrate(5404,
              Integrate(
                  Power(
                      Plus(
                          a_DEFAULT, Times(b_DEFAULT, Tanh(Plus(c_DEFAULT,
                              Times(d_DEFAULT, Power(u_, n_)))))),
                      p_DEFAULT),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Coefficient(u, x,
                          C1), CN1),
                      Subst(
                          Integrate(Power(Plus(a, Times(b, Tanh(Plus(c, Times(d, Power(x, n)))))),
                              p), x),
                          x, u),
                      x),
                  And(FreeQ(List(a, b, c, d, n, p), x), LinearQ(u, x), NeQ(u, x)))),
          IIntegrate(5405,
              Integrate(
                  Power(
                      Plus(
                          a_DEFAULT, Times(Coth(Plus(c_DEFAULT, Times(d_DEFAULT, Power(u_, n_)))),
                              b_DEFAULT)),
                      p_DEFAULT),
                  x_Symbol),
              Condition(Dist(Power(Coefficient(u, x, C1), CN1),
                  Subst(Integrate(Power(Plus(a, Times(b, Coth(Plus(c, Times(d, Power(x, n)))))), p),
                      x), x, u),
                  x), And(FreeQ(List(a, b, c, d, n, p), x), LinearQ(u, x), NeQ(u, x)))),
          IIntegrate(5406,
              Integrate(Power(Plus(a_DEFAULT, Times(b_DEFAULT, Tanh(u_))),
                  p_DEFAULT), x_Symbol),
              Condition(
                  Integrate(Power(Plus(a, Times(b, Tanh(ExpandToSum(u, x)))),
                      p), x),
                  And(FreeQ(List(a, b, p), x), BinomialQ(u, x), Not(BinomialMatchQ(u, x))))),
          IIntegrate(5407,
              Integrate(Power(Plus(a_DEFAULT, Times(Coth(u_), b_DEFAULT)),
                  p_DEFAULT), x_Symbol),
              Condition(
                  Integrate(Power(Plus(a, Times(b, Coth(ExpandToSum(u, x)))),
                      p), x),
                  And(FreeQ(List(a, b, p), x), BinomialQ(u, x), Not(BinomialMatchQ(u, x))))),
          IIntegrate(5408,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  b_DEFAULT,
                                  Tanh(Plus(c_DEFAULT, Times(d_DEFAULT, Power(x_, n_)))))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(n, CN1),
                      Subst(
                          Integrate(
                              Times(Power(x, Subtract(Simplify(Times(Plus(m, C1), Power(n, CN1))),
                                  C1)), Power(Plus(a, Times(b, Tanh(Plus(c, Times(d, x))))),
                                      p)),
                              x),
                          x, Power(x, n)),
                      x),
                  And(FreeQ(List(a, b, c, d, m, n,
                      p), x), IGtQ(Simplify(Times(Plus(m, C1), Power(n, CN1))),
                          C0),
                      IntegerQ(p)))),
          IIntegrate(5409,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  Coth(Plus(c_DEFAULT, Times(d_DEFAULT,
                                      Power(x_, n_)))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      Power(x_, m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(n, CN1),
                      Subst(
                          Integrate(
                              Times(
                                  Power(x, Subtract(Simplify(Times(Plus(m, C1), Power(n, CN1))),
                                      C1)),
                                  Power(Plus(a, Times(b, Coth(Plus(c, Times(d, x))))), p)),
                              x),
                          x, Power(x, n)),
                      x),
                  And(FreeQ(List(a, b, c, d, m, n,
                      p), x), IGtQ(Simplify(Times(Plus(m, C1), Power(n, CN1))),
                          C0),
                      IntegerQ(p)))),
          IIntegrate(5410,
              Integrate(
                  Times(
                      Power(x_, m_DEFAULT), Sqr(Tanh(
                          Plus(c_DEFAULT, Times(d_DEFAULT, Power(x_, n_)))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(
                                  Power(x, Plus(m, Negate(n), C1)), Tanh(Plus(c,
                                      Times(d, Power(x, n)))),
                                  Power(Times(d, n), CN1)),
                              x)),
                      Dist(
                          Times(Plus(m, Negate(
                              n), C1), Power(Times(d, n),
                                  CN1)),
                          Integrate(
                              Times(Power(x, Subtract(m, n)), Tanh(Plus(c, Times(d, Power(x, n))))),
                              x),
                          x),
                      Integrate(Power(x, m), x)),
                  FreeQ(List(c, d, m, n), x))),
          IIntegrate(5411,
              Integrate(
                  Times(
                      Sqr(Coth(
                          Plus(c_DEFAULT, Times(d_DEFAULT, Power(x_, n_))))),
                      Power(x_, m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(Power(x, Plus(m, Negate(n), C1)),
                                  Coth(Plus(c, Times(d, Power(x, n)))), Power(Times(d, n), CN1)),
                              x)),
                      Dist(
                          Times(Plus(m, Negate(
                              n), C1), Power(Times(d, n),
                                  CN1)),
                          Integrate(
                              Times(Power(x, Subtract(m,
                                  n)), Coth(
                                      Plus(c, Times(d, Power(x, n))))),
                              x),
                          x),
                      Integrate(Power(x, m), x)),
                  FreeQ(List(c, d, m, n), x))),
          IIntegrate(5412,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT, Tanh(Plus(c_DEFAULT, Times(d_DEFAULT,
                                  Power(x_, n_)))))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Simp(
                      Integral(
                          Times(Power(x, m),
                              Power(Plus(a, Times(b, Tanh(Plus(c, Times(d, Power(x, n)))))), p)),
                          x),
                      x),
                  FreeQ(List(a, b, c, d, m, n, p), x))),
          IIntegrate(5413,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(Coth(Plus(c_DEFAULT, Times(d_DEFAULT, Power(x_, n_)))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      Power(x_, m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Simp(
                      Integral(
                          Times(
                              Power(x, m), Power(Plus(a,
                                  Times(b, Coth(Plus(c, Times(d, Power(x, n)))))),
                                  p)),
                          x),
                      x),
                  FreeQ(List(a, b, c, d, m, n, p), x))),
          IIntegrate(5414,
              Integrate(
                  Times(
                      Power(Times(e_,
                          x_), m_DEFAULT),
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT, Tanh(Plus(c_DEFAULT, Times(d_DEFAULT,
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
                              Power(Plus(a, Times(b, Tanh(Plus(c, Times(d, Power(x, n)))))), p)),
                          x),
                      x),
                  FreeQ(List(a, b, c, d, e, m, n, p), x))),
          IIntegrate(5415,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  Coth(Plus(c_DEFAULT, Times(d_DEFAULT,
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
                              Power(Plus(a, Times(b, Coth(Plus(c, Times(d, Power(x, n)))))), p)),
                          x),
                      x),
                  FreeQ(List(a, b, c, d, e, m, n, p), x))),
          IIntegrate(5416,
              Integrate(
                  Times(
                      Power(Times(e_, x_), m_DEFAULT), Power(Plus(a_DEFAULT,
                          Times(b_DEFAULT, Tanh(u_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          Power(Times(e, x), m), Power(Plus(a, Times(b, Tanh(ExpandToSum(u, x)))),
                              p)),
                      x),
                  And(FreeQ(List(a, b, e, m, p), x), BinomialQ(u, x), Not(BinomialMatchQ(u, x))))),
          IIntegrate(5417,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(Coth(u_), b_DEFAULT)), p_DEFAULT),
                      Power(Times(e_, x_), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          Power(Times(e, x), m), Power(Plus(a, Times(b, Coth(ExpandToSum(u, x)))),
                              p)),
                      x),
                  And(FreeQ(List(a, b, e, m, p), x), BinomialQ(u, x), Not(BinomialMatchQ(u, x))))),
          IIntegrate(5418,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(Sech(Plus(a_DEFAULT,
                          Times(b_DEFAULT, Power(x_, n_DEFAULT)))), p_DEFAULT),
                      Power(
                          Tanh(Plus(a_DEFAULT, Times(b_DEFAULT,
                              Power(x_, n_DEFAULT)))),
                          q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(Power(x, Plus(m, Negate(n), C1)),
                                  Power(Sech(Plus(a, Times(b, Power(x, n)))),
                                      p),
                                  Power(Times(b, n, p), CN1)),
                              x)),
                      Dist(
                          Times(Plus(m, Negate(
                              n), C1), Power(Times(b, n, p),
                                  CN1)),
                          Integrate(
                              Times(Power(x, Subtract(m, n)),
                                  Power(Sech(Plus(a, Times(b, Power(x, n)))), p)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, p), x), RationalQ(m), IntegerQ(n), GeQ(Subtract(m,
                      n), C0), EqQ(q,
                          C1)))),
          IIntegrate(5419,
              Integrate(
                  Times(Power(Coth(Plus(a_DEFAULT, Times(b_DEFAULT, Power(x_, n_DEFAULT)))),
                      q_DEFAULT),
                      Power(Csch(Plus(a_DEFAULT,
                          Times(b_DEFAULT, Power(x_, n_DEFAULT)))), p_DEFAULT),
                      Power(x_, m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(Power(x, Plus(m, Negate(n), C1)),
                                  Power(Csch(Plus(a, Times(b, Power(x, n)))),
                                      p),
                                  Power(Times(b, n, p), CN1)),
                              x)),
                      Dist(Times(Plus(m, Negate(n), C1), Power(Times(b, n, p), CN1)),
                          Integrate(Times(Power(x, Subtract(m, n)),
                              Power(Csch(Plus(a, Times(b, Power(x, n)))), p)), x),
                          x)),
                  And(FreeQ(List(a, b, p), x), RationalQ(m), IntegerQ(n), GeQ(Subtract(m, n), C0),
                      EqQ(q, C1)))),
          IIntegrate(5420, Integrate(Power(
              Tanh(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_)))), n_DEFAULT),
              x_Symbol),
              Condition(
                  Simp(Integral(Power(Tanh(Plus(a, Times(b, x), Times(c, Sqr(x)))), n), x), x),
                  FreeQ(List(a, b, c, n), x))));
}
