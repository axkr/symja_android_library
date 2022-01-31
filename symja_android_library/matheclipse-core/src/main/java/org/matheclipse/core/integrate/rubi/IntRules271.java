package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Coefficient;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Cosh;
import static org.matheclipse.core.expression.F.Coth;
import static org.matheclipse.core.expression.F.Csch;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sech;
import static org.matheclipse.core.expression.F.Simplify;
import static org.matheclipse.core.expression.F.Sinh;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Tanh;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.v_;
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
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.BinomialMatchQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.BinomialQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandToSum;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FracPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Integral;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LinearMatchQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LinearQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Unintegrable;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules271 {
  public static IAST RULES =
      List(
          IIntegrate(5421,
              Integrate(
                  Power(Coth(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_)))),
                      n_DEFAULT),
                  x_Symbol),
              Condition(
                  Simp(Integral(Power(Coth(Plus(a, Times(b, x), Times(c, Sqr(x)))), n),
                      x), x),
                  FreeQ(List(a, b, c, n), x))),
          IIntegrate(5422,
              Integrate(
                  Times(
                      Plus(d_DEFAULT, Times(e_DEFAULT, x_)), Tanh(Plus(a_DEFAULT,
                          Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(
                              e, Log(Cosh(
                                  Plus(a, Times(b, x), Times(c, Sqr(x))))),
                              Power(Times(C2, c), CN1)),
                          x),
                      Dist(Times(Subtract(Times(C2, c, d), Times(b, e)), Power(Times(C2, c), CN1)),
                          Integrate(Tanh(Plus(a, Times(b, x), Times(c, Sqr(x)))), x), x)),
                  FreeQ(List(a, b, c, d, e), x))),
          IIntegrate(5423,
              Integrate(
                  Times(
                      Coth(Plus(a_DEFAULT, Times(b_DEFAULT, x_),
                          Times(c_DEFAULT, Sqr(x_)))),
                      Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(Times(e, Log(Sinh(Plus(a, Times(b, x), Times(c, Sqr(x))))),
                          Power(Times(C2, c), CN1)), x),
                      Dist(Times(Subtract(Times(C2, c, d), Times(b, e)), Power(Times(C2, c), CN1)),
                          Integrate(Coth(Plus(a, Times(b, x), Times(c, Sqr(x)))), x), x)),
                  FreeQ(List(a, b, c, d, e), x))),
          IIntegrate(5424,
              Integrate(
                  Times(
                      Power(Plus(d_DEFAULT,
                          Times(e_DEFAULT, x_)), m_DEFAULT),
                      Power(
                          Tanh(Plus(a_DEFAULT, Times(b_DEFAULT, x_),
                              Times(c_DEFAULT, Sqr(x_)))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Simp(
                      Integral(Times(Power(Plus(d, Times(e, x)), m),
                          Power(Tanh(Plus(a, Times(b, x), Times(c, Sqr(x)))), n)), x),
                      x),
                  FreeQ(List(a, b, c, d, e, m, n), x))),
          IIntegrate(5425,
              Integrate(Times(
                  Power(Coth(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_)))),
                      n_DEFAULT),
                  Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_DEFAULT)), x_Symbol),
              Condition(Simp(Integral(Times(Power(Plus(d, Times(e, x)), m),
                  Power(Coth(Plus(a, Times(b, x), Times(c, Sqr(x)))), n)), x), x),
                  FreeQ(List(a, b, c, d, e, m, n), x))),
          IIntegrate(5426,
              Integrate(Times(Power(u_, m_DEFAULT),
                  Power(Sech(v_), n_DEFAULT)), x_Symbol),
              Condition(
                  Integrate(Times(Power(ExpandToSum(u, x), m),
                      Power(Sech(ExpandToSum(v, x)), n)), x),
                  And(FreeQ(List(m,
                      n), x), LinearQ(List(u, v),
                          x),
                      Not(LinearMatchQ(List(u, v), x))))),
          IIntegrate(5427,
              Integrate(Times(Power(Csch(
                  v_), n_DEFAULT), Power(u_,
                      m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(Times(Power(ExpandToSum(u, x), m),
                      Power(Csch(ExpandToSum(v, x)), n)), x),
                  And(FreeQ(List(m,
                      n), x), LinearQ(List(u, v),
                          x),
                      Not(LinearMatchQ(List(u, v), x))))),
          IIntegrate(5428,
              Integrate(
                  Power(
                      Plus(
                          a_DEFAULT, Times(b_DEFAULT, Sech(Plus(c_DEFAULT,
                              Times(d_DEFAULT, Power(x_, n_)))))),
                      p_DEFAULT),
                  x_Symbol),
              Condition(
                  Dist(Power(n, CN1),
                      Subst(
                          Integrate(
                              Times(
                                  Power(x, Subtract(Power(n, CN1),
                                      C1)),
                                  Power(Plus(a, Times(b, Sech(Plus(c, Times(d, x))))), p)),
                              x),
                          x, Power(x, n)),
                      x),
                  And(FreeQ(List(a, b, c, d, p), x), IGtQ(Power(n, CN1), C0), IntegerQ(p)))),
          IIntegrate(5429,
              Integrate(
                  Power(
                      Plus(a_DEFAULT,
                          Times(Csch(Plus(c_DEFAULT, Times(d_DEFAULT, Power(x_, n_)))), b_DEFAULT)),
                      p_DEFAULT),
                  x_Symbol),
              Condition(
                  Dist(Power(n, CN1),
                      Subst(
                          Integrate(Times(Power(x, Subtract(Power(n, CN1), C1)),
                              Power(Plus(a, Times(b, Csch(Plus(c, Times(d, x))))), p)), x),
                          x, Power(x, n)),
                      x),
                  And(FreeQ(List(a, b, c, d, p), x), IGtQ(Power(n, CN1), C0), IntegerQ(p)))),
          IIntegrate(5430,
              Integrate(
                  Power(
                      Plus(a_DEFAULT,
                          Times(b_DEFAULT, Sech(Plus(c_DEFAULT, Times(d_DEFAULT, Power(x_, n_)))))),
                      p_DEFAULT),
                  x_Symbol),
              Condition(
                  Unintegrable(Power(Plus(a, Times(b, Sech(Plus(c, Times(d, Power(x, n)))))), p),
                      x),
                  FreeQ(List(a, b, c, d, n, p), x))),
          IIntegrate(5431,
              Integrate(
                  Power(
                      Plus(
                          a_DEFAULT, Times(Csch(Plus(c_DEFAULT, Times(d_DEFAULT, Power(x_, n_)))),
                              b_DEFAULT)),
                      p_DEFAULT),
                  x_Symbol),
              Condition(
                  Unintegrable(Power(Plus(a, Times(b, Csch(Plus(c, Times(d, Power(x, n)))))),
                      p), x),
                  FreeQ(List(a, b, c, d, n, p), x))),
          IIntegrate(5432,
              Integrate(
                  Power(
                      Plus(
                          a_DEFAULT, Times(b_DEFAULT, Sech(Plus(c_DEFAULT,
                              Times(d_DEFAULT, Power(u_, n_)))))),
                      p_DEFAULT),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Coefficient(u, x,
                          C1), CN1),
                      Subst(
                          Integrate(Power(Plus(a, Times(b, Sech(Plus(c, Times(d, Power(x, n)))))),
                              p), x),
                          x, u),
                      x),
                  And(FreeQ(List(a, b, c, d, n, p), x), LinearQ(u, x), NeQ(u, x)))),
          IIntegrate(5433,
              Integrate(
                  Power(
                      Plus(
                          a_DEFAULT, Times(Csch(Plus(c_DEFAULT, Times(d_DEFAULT, Power(u_, n_)))),
                              b_DEFAULT)),
                      p_DEFAULT),
                  x_Symbol),
              Condition(Dist(Power(Coefficient(u, x, C1), CN1),
                  Subst(Integrate(Power(Plus(a, Times(b, Csch(Plus(c, Times(d, Power(x, n)))))), p),
                      x), x, u),
                  x), And(FreeQ(List(a, b, c, d, n, p), x), LinearQ(u, x), NeQ(u, x)))),
          IIntegrate(5434,
              Integrate(Power(Plus(a_DEFAULT, Times(b_DEFAULT, Sech(u_))),
                  p_DEFAULT), x_Symbol),
              Condition(
                  Integrate(Power(Plus(a, Times(b, Sech(ExpandToSum(u, x)))),
                      p), x),
                  And(FreeQ(List(a, b, p), x), BinomialQ(u, x), Not(BinomialMatchQ(u, x))))),
          IIntegrate(5435,
              Integrate(Power(Plus(a_DEFAULT, Times(Csch(u_), b_DEFAULT)),
                  p_DEFAULT), x_Symbol),
              Condition(
                  Integrate(Power(Plus(a, Times(b, Csch(ExpandToSum(u, x)))),
                      p), x),
                  And(FreeQ(List(a, b, p), x), BinomialQ(u, x), Not(BinomialMatchQ(u, x))))),
          IIntegrate(5436,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  b_DEFAULT, Sech(Plus(c_DEFAULT, Times(d_DEFAULT,
                                      Power(x_, n_)))))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(n, CN1),
                      Subst(
                          Integrate(
                              Times(Power(x, Subtract(Simplify(Times(Plus(m, C1), Power(n, CN1))),
                                  C1)), Power(Plus(a, Times(b, Sech(Plus(c, Times(d, x))))),
                                      p)),
                              x),
                          x, Power(x, n)),
                      x),
                  And(FreeQ(List(a, b, c, d, m, n,
                      p), x), IGtQ(Simplify(Times(Plus(m, C1), Power(n, CN1))),
                          C0),
                      IntegerQ(p)))),
          IIntegrate(5437,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(Csch(Plus(c_DEFAULT, Times(d_DEFAULT, Power(x_, n_)))),
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
                                  Power(Plus(a, Times(b, Csch(Plus(c, Times(d, x))))), p)),
                              x),
                          x, Power(x, n)),
                      x),
                  And(FreeQ(List(a, b, c, d, m, n,
                      p), x), IGtQ(Simplify(Times(Plus(m, C1), Power(n, CN1))),
                          C0),
                      IntegerQ(p)))),
          IIntegrate(5438,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  b_DEFAULT, Sech(Plus(c_DEFAULT, Times(d_DEFAULT,
                                      Power(x_, n_)))))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(Times(Power(x, m),
                      Power(Plus(a, Times(b, Sech(Plus(c, Times(d, Power(x, n)))))), p)), x),
                  FreeQ(List(a, b, c, d, m, n, p), x))),
          IIntegrate(5439,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(Csch(Plus(c_DEFAULT, Times(d_DEFAULT, Power(x_, n_)))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      Power(x_, m_DEFAULT)),
                  x_Symbol),
              Condition(Unintegrable(Times(Power(x, m),
                  Power(Plus(a, Times(b, Csch(Plus(c, Times(d, Power(x, n)))))), p)), x), FreeQ(
                      List(a, b, c, d, m, n, p), x))),
          IIntegrate(5440,
              Integrate(
                  Times(
                      Power(Times(e_,
                          x_), m_DEFAULT),
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  b_DEFAULT, Sech(Plus(c_DEFAULT, Times(d_DEFAULT,
                                      Power(x_, n_)))))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(e, IntPart(m)), Power(Times(e, x), FracPart(m)),
                          Power(Power(x, FracPart(m)), CN1)),
                      Integrate(
                          Times(Power(x, m),
                              Power(Plus(a, Times(b, Sech(Plus(c, Times(d, Power(x, n)))))), p)),
                          x),
                      x),
                  FreeQ(List(a, b, c, d, e, m, n, p), x))));
}
