package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Coefficient;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Coth;
import static org.matheclipse.core.expression.F.Csch;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sech;
import static org.matheclipse.core.expression.F.Simplify;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Tanh;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.q_DEFAULT;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.q;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.BinomialMatchQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.BinomialQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandToSum;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FracPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Integral;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LinearQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RationalQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
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
