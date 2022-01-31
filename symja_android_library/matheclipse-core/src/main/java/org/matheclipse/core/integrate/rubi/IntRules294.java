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
class IntRules294 {
  public static IAST RULES =
      List(
          IIntegrate(5881,
              Integrate(
                  Power(
                      Plus(
                          a_DEFAULT, Times(ArcCosh(Plus(C1, Times(d_DEFAULT, Sqr(x_)))),
                              b_DEFAULT)),
                      CN1),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              x, Cosh(Times(a,
                                  Power(Times(C2, b), CN1))),
                              CoshIntegral(
                                  Times(
                                      Plus(a, Times(b, ArcCosh(Plus(C1, Times(d, Sqr(x)))))), Power(
                                          Times(C2, b), CN1))),
                              Power(Times(CSqrt2, b, Sqrt(Times(d, Sqr(x)))), CN1)),
                          x),
                      Simp(
                          Times(x, Sinh(Times(a, Power(Times(C2, b), CN1))),
                              SinhIntegral(
                                  Times(
                                      Plus(a, Times(b,
                                          ArcCosh(Plus(C1, Times(d, Sqr(x)))))),
                                      Power(Times(C2, b), CN1))),
                              Power(Times(CSqrt2, b, Sqrt(Times(d, Sqr(x)))), CN1)),
                          x)),
                  FreeQ(List(a, b, d), x))),
          IIntegrate(5882,
              Integrate(
                  Power(
                      Plus(
                          a_DEFAULT, Times(ArcCosh(Plus(CN1, Times(d_DEFAULT, Sqr(x_)))),
                              b_DEFAULT)),
                      CN1),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(
                                  x, Sinh(Times(a,
                                      Power(Times(C2, b), CN1))),
                                  CoshIntegral(
                                      Times(
                                          Plus(a, Times(b,
                                              ArcCosh(Plus(CN1, Times(d, Sqr(x)))))),
                                          Power(Times(C2, b), CN1))),
                                  Power(Times(CSqrt2, b, Sqrt(Times(d, Sqr(x)))), CN1)),
                              x)),
                      Simp(Times(x, Cosh(Times(a, Power(Times(C2, b), CN1))),
                          SinhIntegral(
                              Times(Plus(a, Times(b, ArcCosh(Plus(CN1, Times(d, Sqr(x)))))),
                                  Power(Times(C2, b), CN1))),
                          Power(Times(CSqrt2, b, Sqrt(Times(d, Sqr(x)))), CN1)),
                          x)),
                  FreeQ(List(a, b, d), x))),
          IIntegrate(5883,
              Integrate(Power(
                  Plus(a_DEFAULT, Times(ArcCosh(Plus(C1, Times(d_DEFAULT, Sqr(x_)))), b_DEFAULT)),
                  CN1D2), x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Sqrt(CPiHalf),
                              Subtract(
                                  Cosh(Times(a, Power(Times(C2, b),
                                      CN1))),
                                  Sinh(Times(a, Power(Times(C2, b), CN1)))),
                              Sinh(Times(C1D2, ArcCosh(Plus(C1, Times(d, Sqr(x)))))),
                              Erfi(
                                  Times(
                                      Sqrt(Plus(a, Times(b, ArcCosh(Plus(C1, Times(d, Sqr(x))))))),
                                      Power(Times(C2, b), CN1D2))),
                              Power(Times(Sqrt(b), d, x), CN1)),
                          x),
                      Simp(
                          Times(Sqrt(CPiHalf),
                              Plus(
                                  Cosh(Times(a, Power(Times(C2, b),
                                      CN1))),
                                  Sinh(Times(a, Power(Times(C2, b), CN1)))),
                              Sinh(Times(C1D2, ArcCosh(Plus(C1, Times(d, Sqr(x)))))),
                              Erf(Times(
                                  Sqrt(Plus(a, Times(b, ArcCosh(Plus(C1, Times(d, Sqr(x))))))),
                                  Power(Times(C2, b), CN1D2))),
                              Power(Times(Sqrt(b), d, x), CN1)),
                          x)),
                  FreeQ(List(a, b, d), x))),
          IIntegrate(5884,
              Integrate(
                  Power(
                      Plus(
                          a_DEFAULT, Times(ArcCosh(Plus(CN1, Times(d_DEFAULT, Sqr(x_)))),
                              b_DEFAULT)),
                      CN1D2),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Sqrt(CPiHalf),
                              Subtract(
                                  Cosh(Times(a, Power(Times(C2, b), CN1))), Sinh(Times(a,
                                      Power(Times(C2, b), CN1)))),
                              Cosh(Times(C1D2,
                                  ArcCosh(Plus(CN1, Times(d, Sqr(x)))))),
                              Erfi(
                                  Times(
                                      Sqrt(Plus(a, Times(b, ArcCosh(Plus(CN1, Times(d, Sqr(x))))))),
                                      Power(Times(C2, b), CN1D2))),
                              Power(Times(Sqrt(b), d, x), CN1)),
                          x),
                      Simp(
                          Times(Sqrt(CPiHalf),
                              Plus(
                                  Cosh(Times(a, Power(Times(C2, b),
                                      CN1))),
                                  Sinh(Times(a, Power(Times(C2, b), CN1)))),
                              Cosh(Times(C1D2, ArcCosh(Plus(CN1, Times(d, Sqr(x)))))),
                              Erf(Times(
                                  Sqrt(Plus(a, Times(b, ArcCosh(Plus(CN1, Times(d, Sqr(x))))))),
                                  Power(Times(C2, b), CN1D2))),
                              Power(Times(Sqrt(b), d, x), CN1)),
                          x)),
                  FreeQ(List(a, b, d), x))),
          IIntegrate(5885,
              Integrate(Power(
                  Plus(a_DEFAULT, Times(ArcCosh(Plus(C1, Times(d_DEFAULT, Sqr(x_)))), b_DEFAULT)),
                  QQ(-3L, 2L)), x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(Sqrt(Times(d, Sqr(x))), Sqrt(Plus(C2, Times(d, Sqr(x)))),
                                  Power(
                                      Times(b, d, x,
                                          Sqrt(Plus(a,
                                              Times(b, ArcCosh(Plus(C1, Times(d, Sqr(x)))))))),
                                      CN1)),
                              x)),
                      Negate(
                          Simp(
                              Times(Sqrt(CPiHalf),
                                  Plus(
                                      Cosh(Times(a, Power(Times(C2, b),
                                          CN1))),
                                      Sinh(Times(a, Power(Times(C2, b), CN1)))),
                                  Sinh(Times(C1D2, ArcCosh(Plus(C1, Times(d, Sqr(x)))))),
                                  Erf(Times(
                                      Sqrt(Plus(a, Times(b, ArcCosh(Plus(C1, Times(d, Sqr(x))))))),
                                      Power(Times(C2, b), CN1D2))),
                                  Power(Times(Power(b, QQ(3L, 2L)), d, x), CN1)),
                              x)),
                      Simp(
                          Times(
                              Sqrt(CPiHalf),
                              Subtract(
                                  Cosh(Times(a, Power(Times(C2, b),
                                      CN1))),
                                  Sinh(Times(a, Power(Times(C2, b), CN1)))),
                              Sinh(Times(C1D2, ArcCosh(Plus(C1, Times(d, Sqr(x)))))),
                              Erfi(Times(
                                  Sqrt(Plus(a, Times(b, ArcCosh(Plus(C1, Times(d, Sqr(x))))))),
                                  Power(Times(C2, b), CN1D2))),
                              Power(Times(Power(b, QQ(3L, 2L)), d, x), CN1)),
                          x)),
                  FreeQ(List(a, b, d), x))),
          IIntegrate(5886,
              Integrate(
                  Power(
                      Plus(
                          a_DEFAULT,
                          Times(ArcCosh(Plus(CN1, Times(d_DEFAULT, Sqr(x_)))), b_DEFAULT)),
                      QQ(-3L, 2L)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(Sqrt(Times(d, Sqr(x))), Sqrt(Plus(CN2, Times(d, Sqr(x)))),
                                  Power(
                                      Times(b, d, x,
                                          Sqrt(Plus(a,
                                              Times(b, ArcCosh(Plus(CN1, Times(d, Sqr(x)))))))),
                                      CN1)),
                              x)),
                      Simp(
                          Times(Sqrt(CPiHalf),
                              Plus(
                                  Cosh(Times(a, Power(Times(C2, b),
                                      CN1))),
                                  Sinh(Times(a, Power(Times(C2, b), CN1)))),
                              Cosh(Times(C1D2, ArcCosh(Plus(CN1, Times(d, Sqr(x)))))), Erf(
                                  Times(
                                      Sqrt(Plus(a, Times(b, ArcCosh(Plus(CN1, Times(d, Sqr(x))))))),
                                      Power(Times(C2, b), CN1D2))),
                              Power(Times(Power(b, QQ(3L, 2L)), d, x), CN1)),
                          x),
                      Simp(Times(Sqrt(CPiHalf),
                          Subtract(Cosh(Times(a, Power(Times(C2, b), CN1))),
                              Sinh(Times(a, Power(Times(C2, b), CN1)))),
                          Cosh(Times(C1D2, ArcCosh(Plus(CN1, Times(d, Sqr(x)))))),
                          Erfi(Times(Sqrt(Plus(a, Times(b, ArcCosh(Plus(CN1, Times(d, Sqr(x))))))),
                              Power(Times(C2, b), CN1D2))),
                          Power(Times(Power(b, QQ(3L, 2L)), d, x), CN1)), x)),
                  FreeQ(List(a, b, d), x))),
          IIntegrate(5887,
              Integrate(Power(
                  Plus(a_DEFAULT, Times(ArcCosh(Plus(C1, Times(d_DEFAULT, Sqr(x_)))), b_DEFAULT)),
                  CN2), x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(
                                  Sqrt(Times(d, Sqr(x))), Sqrt(Plus(C2,
                                      Times(d, Sqr(x)))),
                                  Power(
                                      Times(
                                          C2, b, d, x, Plus(a, Times(b, ArcCosh(
                                              Plus(C1, Times(d, Sqr(x))))))),
                                      CN1)),
                              x)),
                      Negate(
                          Simp(
                              Times(x, Sinh(Times(a, Power(Times(C2, b), CN1))),
                                  CoshIntegral(
                                      Times(Plus(a, Times(b, ArcCosh(Plus(C1, Times(d, Sqr(x)))))),
                                          Power(Times(C2, b), CN1))),
                                  Power(Times(C2, CSqrt2, Sqr(b), Sqrt(Times(d, Sqr(x)))), CN1)),
                              x)),
                      Simp(
                          Times(
                              x, Cosh(Times(a,
                                  Power(Times(C2, b), CN1))),
                              SinhIntegral(
                                  Times(
                                      Plus(a, Times(b, ArcCosh(Plus(C1, Times(d, Sqr(x)))))), Power(
                                          Times(C2, b), CN1))),
                              Power(Times(C2, CSqrt2, Sqr(b), Sqrt(Times(d, Sqr(x)))), CN1)),
                          x)),
                  FreeQ(List(a, b, d), x))),
          IIntegrate(5888,
              Integrate(Power(
                  Plus(a_DEFAULT, Times(ArcCosh(Plus(CN1, Times(d_DEFAULT, Sqr(x_)))), b_DEFAULT)),
                  CN2), x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(
                                  Sqrt(Times(d, Sqr(
                                      x))),
                                  Sqrt(Plus(CN2,
                                      Times(d, Sqr(x)))),
                                  Power(
                                      Times(C2, b, d, x, Plus(a, Times(b,
                                          ArcCosh(Plus(CN1, Times(d, Sqr(x))))))),
                                      CN1)),
                              x)),
                      Simp(
                          Times(
                              x, Cosh(Times(a,
                                  Power(Times(C2, b), CN1))),
                              CoshIntegral(
                                  Times(
                                      Plus(a, Times(b,
                                          ArcCosh(Plus(CN1, Times(d, Sqr(x)))))),
                                      Power(Times(C2, b), CN1))),
                              Power(Times(C2, CSqrt2, Sqr(b), Sqrt(Times(d, Sqr(x)))), CN1)),
                          x),
                      Negate(
                          Simp(
                              Times(x, Sinh(Times(a, Power(Times(C2, b), CN1))),
                                  SinhIntegral(
                                      Times(Plus(a, Times(b, ArcCosh(Plus(CN1, Times(d, Sqr(x)))))),
                                          Power(Times(C2, b), CN1))),
                                  Power(Times(C2, CSqrt2, Sqr(b), Sqrt(Times(d, Sqr(x)))), CN1)),
                              x))),
                  FreeQ(List(a, b, d), x))),
          IIntegrate(5889,
              Integrate(
                  Power(Plus(
                      a_DEFAULT, Times(ArcCosh(Plus(c_, Times(d_DEFAULT, Sqr(x_)))), b_DEFAULT)),
                      n_),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(x,
                                  Power(
                                      Plus(a, Times(b,
                                          ArcCosh(Plus(c, Times(d, Sqr(x)))))),
                                      Plus(n, C2)),
                                  Power(Times(C4, Sqr(b), Plus(n, C1), Plus(n, C2)), CN1)),
                              x)),
                      Dist(Power(Times(C4, Sqr(b), Plus(n, C1), Plus(n, C2)), CN1),
                          Integrate(Power(Plus(a, Times(b, ArcCosh(Plus(c, Times(d, Sqr(x)))))),
                              Plus(n, C2)), x),
                          x),
                      Simp(Times(Plus(Times(C2, c, Sqr(x)), Times(d, Power(x, C4))),
                          Power(Plus(a, Times(b, ArcCosh(Plus(c, Times(d, Sqr(x)))))), Plus(n, C1)),
                          Power(Times(C2, b, Plus(n, C1), x, Sqrt(Plus(CN1, c, Times(d, Sqr(x)))),
                              Sqrt(Plus(C1, c, Times(d, Sqr(x))))), CN1)),
                          x)),
                  And(FreeQ(List(a, b, c, d), x), EqQ(Sqr(c), C1), LtQ(n, CN1), NeQ(n, CN2)))),
          IIntegrate(5890,
              Integrate(Times(Power(ArcSinh(Times(a_DEFAULT, Power(x_, p_))), n_DEFAULT),
                  Power(x_, CN1)), x_Symbol),
              Condition(
                  Dist(Power(p, CN1),
                      Subst(Integrate(Times(Power(x, n), Coth(x)), x), x,
                          ArcSinh(Times(a, Power(x, p)))),
                      x),
                  And(FreeQ(List(a, p), x), IGtQ(n, C0)))),
          IIntegrate(5891,
              Integrate(
                  Times(Power(ArcCosh(
                      Times(a_DEFAULT, Power(x_, p_))), n_DEFAULT), Power(x_,
                          CN1)),
                  x_Symbol),
              Condition(
                  Dist(Power(p, CN1),
                      Subst(
                          Integrate(Times(Power(x,
                              n), Tanh(x)), x),
                          x, ArcCosh(Times(a, Power(x, p)))),
                      x),
                  And(FreeQ(List(a, p), x), IGtQ(n, C0)))),
          IIntegrate(5892,
              Integrate(
                  Times(
                      Power(
                          ArcSinh(Times(c_DEFAULT,
                              Power(Plus(a_DEFAULT, Times(b_DEFAULT, Power(x_, n_DEFAULT))), CN1))),
                          m_DEFAULT),
                      u_DEFAULT),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(u,
                          Power(
                              ArcCsch(
                                  Plus(Times(a, Power(c, CN1)),
                                      Times(b, Power(x, n), Power(c, CN1)))),
                              m)),
                      x),
                  FreeQ(List(a, b, c, n, m), x))),
          IIntegrate(5893,
              Integrate(
                  Times(
                      Power(
                          ArcCosh(
                              Times(c_DEFAULT,
                                  Power(Plus(a_DEFAULT, Times(b_DEFAULT, Power(x_, n_DEFAULT))),
                                      CN1))),
                          m_DEFAULT),
                      u_DEFAULT),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(u,
                          Power(
                              ArcSech(
                                  Plus(Times(a, Power(c, CN1)),
                                      Times(b, Power(x, n), Power(c, CN1)))),
                              m)),
                      x),
                  FreeQ(List(a, b, c, n, m), x))),
          IIntegrate(5894,
              Integrate(
                  Times(
                      Power(ArcSinh(Sqrt(Plus(CN1, Times(b_DEFAULT, Sqr(x_))))), n_DEFAULT), Power(
                          Plus(CN1, Times(b_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Sqrt(Times(b,
                          Sqr(x))), Power(Times(b, x),
                              CN1)),
                      Subst(
                          Integrate(Times(Power(ArcSinh(x), n),
                              Power(Plus(C1, Sqr(x)), CN1D2)), x),
                          x, Sqrt(Plus(CN1, Times(b, Sqr(x))))),
                      x),
                  FreeQ(List(b, n), x))),
          IIntegrate(5895,
              Integrate(
                  Times(
                      Power(ArcCosh(
                          Sqrt(Plus(C1, Times(b_DEFAULT, Sqr(x_))))), n_DEFAULT),
                      Power(Plus(C1, Times(b_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Sqrt(Plus(CN1, Sqrt(Plus(C1, Times(b, Sqr(x)))))),
                          Sqrt(Plus(C1, Sqrt(Plus(C1, Times(b, Sqr(x)))))), Power(Times(b, x),
                              CN1)),
                      Subst(
                          Integrate(
                              Times(Power(ArcCosh(x), n), Power(
                                  Times(Sqrt(Plus(CN1, x)), Sqrt(Plus(C1, x))), CN1)),
                              x),
                          x, Sqrt(Plus(C1, Times(b, Sqr(x))))),
                      x),
                  FreeQ(List(b, n), x))),
          IIntegrate(5896,
              Integrate(
                  Power(f_,
                      Times(
                          Power(ArcSinh(Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                              n_DEFAULT),
                          c_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(b, CN1),
                      Subst(
                          Integrate(Times(Power(f, Times(c, Power(x, n))), Cosh(x)),
                              x),
                          x, ArcSinh(Plus(a, Times(b, x)))),
                      x),
                  And(FreeQ(List(a, b, c, f), x), IGtQ(n, C0)))),
          IIntegrate(5897,
              Integrate(
                  Power(f_,
                      Times(
                          Power(ArcCosh(Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                              n_DEFAULT),
                          c_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(b, CN1),
                      Subst(
                          Integrate(Times(Power(f, Times(c, Power(x, n))),
                              Sinh(x)), x),
                          x, ArcCosh(Plus(a, Times(b, x)))),
                      x),
                  And(FreeQ(List(a, b, c, f), x), IGtQ(n, C0)))),
          IIntegrate(5898,
              Integrate(
                  Times(
                      Power(f_,
                          Times(
                              Power(ArcSinh(Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                                  n_DEFAULT),
                              c_DEFAULT)),
                      Power(x_, m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(b, CN1),
                      Subst(
                          Integrate(
                              Times(
                                  Power(
                                      Plus(Times(CN1, a, Power(b, CN1)),
                                          Times(Sinh(x), Power(b, CN1))),
                                      m),
                                  Power(f, Times(c, Power(x, n))), Cosh(x)),
                              x),
                          x, ArcSinh(Plus(a, Times(b, x)))),
                      x),
                  And(FreeQ(List(a, b, c, f), x), IGtQ(m, C0), IGtQ(n, C0)))),
          IIntegrate(5899,
              Integrate(
                  Times(
                      Power(f_,
                          Times(Power(ArcCosh(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), n_DEFAULT),
                              c_DEFAULT)),
                      Power(x_, m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(b, CN1),
                      Subst(
                          Integrate(Times(Power(
                              Plus(Times(CN1, a, Power(b, CN1)), Times(Cosh(x), Power(b, CN1))), m),
                              Power(f, Times(c, Power(x, n))), Sinh(x)), x),
                          x, ArcCosh(Plus(a, Times(b, x)))),
                      x),
                  And(FreeQ(List(a, b, c, f), x), IGtQ(m, C0), IGtQ(n, C0)))),
          IIntegrate(5900, Integrate(ArcSinh(u_), x_Symbol),
              Condition(
                  Subtract(Simp(Times(x, ArcSinh(u)), x), Integrate(
                      SimplifyIntegrand(Times(x, D(u, x), Power(Plus(C1, Sqr(u)), CN1D2)), x), x)),
                  And(InverseFunctionFreeQ(u, x), Not(FunctionOfExponentialQ(u, x))))));
}
