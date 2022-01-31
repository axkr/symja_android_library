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
class IntRules265 {
  public static IAST RULES =
      List(
          IIntegrate(5301,
              Integrate(
                  Power(
                      Plus(
                          a_DEFAULT, Times(Cosh(Plus(c_DEFAULT, Times(d_DEFAULT, Power(x_, n_)))),
                              b_DEFAULT)),
                      p_),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandTrigReduce(Power(
                          Plus(a, Times(b, Cosh(Plus(c, Times(d, Power(x, n)))))), p), x),
                      x),
                  And(FreeQ(List(a, b, c, d), x), IGtQ(n, C1), IGtQ(p, C1)))),
          IIntegrate(5302,
              Integrate(
                  Power(
                      Plus(
                          a_DEFAULT,
                          Times(b_DEFAULT, Sinh(Plus(c_DEFAULT, Times(d_DEFAULT, Power(x_, n_)))))),
                      p_DEFAULT),
                  x_Symbol),
              Condition(
                  Negate(
                      Subst(
                          Integrate(
                              Times(
                                  Power(
                                      Plus(a,
                                          Times(b,
                                              Sinh(Plus(c, Times(d, Power(Power(x, n), CN1)))))),
                                      p),
                                  Power(x, CN2)),
                              x),
                          x, Power(x, CN1))),
                  And(FreeQ(List(a, b, c, d), x), ILtQ(n, C0), IntegerQ(p)))),
          IIntegrate(5303,
              Integrate(
                  Power(
                      Plus(
                          a_DEFAULT, Times(Cosh(Plus(c_DEFAULT, Times(d_DEFAULT, Power(x_, n_)))),
                              b_DEFAULT)),
                      p_DEFAULT),
                  x_Symbol),
              Condition(
                  Negate(
                      Subst(
                          Integrate(
                              Times(
                                  Power(
                                      Plus(a,
                                          Times(b,
                                              Cosh(Plus(c, Times(d, Power(Power(x, n), CN1)))))),
                                      p),
                                  Power(x, CN2)),
                              x),
                          x, Power(x, CN1))),
                  And(FreeQ(List(a, b, c, d), x), ILtQ(n, C0), IntegerQ(p)))),
          IIntegrate(5304,
              Integrate(
                  Power(
                      Plus(
                          a_DEFAULT,
                          Times(b_DEFAULT, Sinh(Plus(c_DEFAULT, Times(d_DEFAULT, Power(x_, n_)))))),
                      p_DEFAULT),
                  x_Symbol),
              Condition(
                  Module(
                      List(Set(k,
                          Denominator(n))),
                      Dist(k,
                          Subst(
                              Integrate(
                                  Times(Power(x, Subtract(k, C1)),
                                      Power(
                                          Plus(a,
                                              Times(b,
                                                  Sinh(Plus(c, Times(d, Power(x, Times(k, n))))))),
                                          p)),
                                  x),
                              x, Power(x, Power(k, CN1))),
                          x)),
                  And(FreeQ(List(a, b, c, d), x), FractionQ(n), IntegerQ(p)))),
          IIntegrate(5305,
              Integrate(
                  Power(
                      Plus(
                          a_DEFAULT, Times(Cosh(Plus(c_DEFAULT, Times(d_DEFAULT, Power(x_, n_)))),
                              b_DEFAULT)),
                      p_DEFAULT),
                  x_Symbol),
              Condition(
                  Module(
                      List(Set(k,
                          Denominator(n))),
                      Dist(k,
                          Subst(
                              Integrate(
                                  Times(Power(x, Subtract(k, C1)),
                                      Power(
                                          Plus(a,
                                              Times(b,
                                                  Cosh(Plus(c, Times(d, Power(x, Times(k, n))))))),
                                          p)),
                                  x),
                              x, Power(x, Power(k, CN1))),
                          x)),
                  And(FreeQ(List(a, b, c, d), x), FractionQ(n), IntegerQ(p)))),
          IIntegrate(5306,
              Integrate(Sinh(
                  Plus(c_DEFAULT, Times(d_DEFAULT, Power(x_, n_)))), x_Symbol),
              Condition(
                  Subtract(
                      Dist(C1D2, Integrate(Exp(Plus(c, Times(d, Power(x, n)))), x), x), Dist(C1D2,
                          Integrate(Exp(Subtract(Negate(c), Times(d, Power(x, n)))), x), x)),
                  FreeQ(List(c, d, n), x))),
          IIntegrate(5307,
              Integrate(Cosh(
                  Plus(c_DEFAULT, Times(d_DEFAULT, Power(x_, n_)))), x_Symbol),
              Condition(
                  Plus(
                      Dist(C1D2, Integrate(Exp(Plus(c, Times(d, Power(x, n)))),
                          x), x),
                      Dist(C1D2, Integrate(Exp(Subtract(Negate(c), Times(d, Power(x, n)))), x), x)),
                  FreeQ(List(c, d, n), x))),
          IIntegrate(5308,
              Integrate(
                  Power(
                      Plus(
                          a_DEFAULT, Times(b_DEFAULT, Sinh(Plus(c_DEFAULT,
                              Times(d_DEFAULT, Power(x_, n_)))))),
                      p_),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandTrigReduce(Power(Plus(a, Times(b,
                          Sinh(Plus(c, Times(d, Power(x, n)))))), p), x),
                      x),
                  And(FreeQ(List(a, b, c, d, n), x), IGtQ(p, C0)))),
          IIntegrate(5309,
              Integrate(
                  Power(
                      Plus(a_DEFAULT, Times(Cosh(Plus(c_DEFAULT, Times(d_DEFAULT, Power(x_, n_)))),
                          b_DEFAULT)),
                      p_),
                  x_Symbol),
              Condition(Integrate(
                  ExpandTrigReduce(Power(Plus(a, Times(b, Cosh(Plus(c, Times(d, Power(x, n)))))),
                      p), x),
                  x), And(FreeQ(List(a, b, c, d, n), x), IGtQ(p, C0)))),
          IIntegrate(5310,
              Integrate(
                  Power(
                      Plus(
                          a_DEFAULT, Times(b_DEFAULT, Sinh(Plus(c_DEFAULT,
                              Times(d_DEFAULT, Power(u_, n_)))))),
                      p_DEFAULT),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Coefficient(u, x,
                          C1), CN1),
                      Subst(Integrate(
                          Power(Plus(a, Times(b, Sinh(Plus(c, Times(d, Power(x, n)))))), p), x), x,
                          u),
                      x),
                  And(FreeQ(List(a, b, c, d, n), x), IntegerQ(p), LinearQ(u, x), NeQ(u, x)))),
          IIntegrate(5311,
              Integrate(
                  Power(
                      Plus(
                          a_DEFAULT, Times(Cosh(Plus(c_DEFAULT, Times(d_DEFAULT, Power(u_, n_)))),
                              b_DEFAULT)),
                      p_DEFAULT),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Coefficient(u, x,
                          C1), CN1),
                      Subst(
                          Integrate(Power(Plus(a, Times(b, Cosh(Plus(c, Times(d, Power(x, n)))))),
                              p), x),
                          x, u),
                      x),
                  And(FreeQ(List(a, b, c, d, n), x), IntegerQ(p), LinearQ(u, x), NeQ(u, x)))),
          IIntegrate(5312,
              Integrate(
                  Power(
                      Plus(
                          a_DEFAULT, Times(b_DEFAULT, Sinh(Plus(c_DEFAULT,
                              Times(d_DEFAULT, Power(u_, n_)))))),
                      p_),
                  x_Symbol),
              Condition(
                  Unintegrable(Power(Plus(a, Times(b,
                      Sinh(Plus(c, Times(d, Power(u, n)))))), p), x),
                  And(FreeQ(List(a, b, c, d, n, p), x), LinearQ(u, x)))),
          IIntegrate(5313,
              Integrate(Power(
                  Plus(a_DEFAULT,
                      Times(Cosh(Plus(c_DEFAULT, Times(d_DEFAULT, Power(u_, n_)))), b_DEFAULT)),
                  p_), x_Symbol),
              Condition(
                  Unintegrable(Power(Plus(a, Times(b, Cosh(Plus(c, Times(d, Power(u, n)))))),
                      p), x),
                  And(FreeQ(List(a, b, c, d, n, p), x), LinearQ(u, x)))),
          IIntegrate(5314,
              Integrate(Power(Plus(a_DEFAULT, Times(b_DEFAULT, Sinh(u_))), p_DEFAULT), x_Symbol),
              Condition(
                  Integrate(Power(Plus(a, Times(b, Sinh(ExpandToSum(u, x)))), p), x), And(FreeQ(
                      List(a, b, p), x), BinomialQ(u, x), Not(BinomialMatchQ(u, x))))),
          IIntegrate(5315,
              Integrate(Power(Plus(a_DEFAULT, Times(Cosh(u_), b_DEFAULT)), p_DEFAULT), x_Symbol),
              Condition(
                  Integrate(Power(Plus(a, Times(b, Cosh(ExpandToSum(u, x)))), p), x), And(FreeQ(
                      List(a, b, p), x), BinomialQ(u, x), Not(BinomialMatchQ(u, x))))),
          IIntegrate(5316,
              Integrate(Times(Power(x_, CN1),
                  Sinh(Times(d_DEFAULT, Power(x_, n_)))), x_Symbol),
              Condition(
                  Simp(Times(SinhIntegral(Times(d, Power(x, n))),
                      Power(n, CN1)), x),
                  FreeQ(List(d, n), x))),
          IIntegrate(5317,
              Integrate(Times(Cosh(Times(d_DEFAULT, Power(x_, n_))),
                  Power(x_, CN1)), x_Symbol),
              Condition(
                  Simp(Times(CoshIntegral(Times(d, Power(x, n))),
                      Power(n, CN1)), x),
                  FreeQ(List(d, n), x))),
          IIntegrate(5318,
              Integrate(Times(Power(x_, CN1),
                  Sinh(Plus(c_, Times(d_DEFAULT, Power(x_, n_))))), x_Symbol),
              Condition(
                  Plus(
                      Dist(
                          Sinh(c), Integrate(Times(Cosh(Times(d, Power(x, n))), Power(x, CN1)),
                              x),
                          x),
                      Dist(
                          Cosh(c), Integrate(Times(Sinh(Times(d, Power(x, n))), Power(x, CN1)),
                              x),
                          x)),
                  FreeQ(List(c, d, n), x))),
          IIntegrate(5319,
              Integrate(Times(Cosh(Plus(c_,
                  Times(d_DEFAULT, Power(x_, n_)))), Power(x_,
                      CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(
                          Cosh(c), Integrate(Times(Cosh(Times(d,
                              Power(x, n))), Power(x,
                                  CN1)),
                              x),
                          x),
                      Dist(
                          Sinh(c), Integrate(Times(Sinh(Times(d,
                              Power(x, n))), Power(x,
                                  CN1)),
                              x),
                          x)),
                  FreeQ(List(c, d, n), x))),
          IIntegrate(5320,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  b_DEFAULT, Sinh(Plus(c_DEFAULT, Times(d_DEFAULT,
                                      Power(x_, n_)))))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(n, CN1), Subst(
                      Integrate(
                          Times(Power(x, Subtract(Simplify(Times(Plus(m, C1), Power(n, CN1))), C1)),
                              Power(Plus(a, Times(b, Sinh(Plus(c, Times(d, x))))), p)),
                          x),
                      x, Power(x, n)), x),
                  And(FreeQ(List(a, b, c, d, m, n, p), x),
                      IntegerQ(Simplify(Times(Plus(m, C1), Power(n, CN1)))),
                      Or(EqQ(p, C1), EqQ(m, Subtract(n, C1)), And(IntegerQ(p),
                          GtQ(Simplify(Times(Plus(m, C1), Power(n, CN1))), C0)))))));
}
