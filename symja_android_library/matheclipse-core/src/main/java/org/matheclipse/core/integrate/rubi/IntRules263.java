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
class IntRules263 {
  public static IAST RULES =
      List(
          IIntegrate(5261,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCsc(Plus(c_, Times(d_DEFAULT, x_))),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(d, CN1),
                      Subst(
                          Integrate(
                              Times(
                                  Power(
                                      Plus(Times(Subtract(Times(d, e), Times(c, f)), Power(d, CN1)),
                                          Times(f, x, Power(d, CN1))),
                                      m),
                                  Power(Plus(a, Times(b, ArcCsc(x))), p)),
                              x),
                          x, Plus(c, Times(d, x))),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m), x), IGtQ(p, C0)))),
          IIntegrate(5262,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcSec(Plus(c_, Times(d_DEFAULT, x_))),
                              b_DEFAULT)),
                          p_),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(
                          Power(Plus(e, Times(f, x)), m), Power(Plus(a,
                              Times(b, ArcSec(Plus(c, Times(d, x))))), p)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, p), x), Not(IGtQ(p, C0))))),
          IIntegrate(5263,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCsc(Plus(c_, Times(d_DEFAULT, x_))), b_DEFAULT)),
                          p_),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(
                          Power(Plus(e, Times(f,
                              x)), m),
                          Power(Plus(a, Times(b, ArcCsc(Plus(c, Times(d, x))))), p)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, p), x), Not(IGtQ(p, C0))))),
          IIntegrate(5264,
              Integrate(
                  Times(Power(
                      ArcSec(Times(c_DEFAULT,
                          Power(Plus(a_DEFAULT, Times(b_DEFAULT, Power(x_, n_DEFAULT))), CN1))),
                      m_DEFAULT), u_DEFAULT),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(u,
                          Power(
                              ArcCos(
                                  Plus(Times(a, Power(c, CN1)),
                                      Times(b, Power(x, n), Power(c, CN1)))),
                              m)),
                      x),
                  FreeQ(List(a, b, c, n, m), x))),
          IIntegrate(5265,
              Integrate(
                  Times(
                      Power(
                          ArcCsc(
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
                              ArcSin(
                                  Plus(
                                      Times(a, Power(c, CN1)), Times(b, Power(x, n),
                                          Power(c, CN1)))),
                              m)),
                      x),
                  FreeQ(List(a, b, c, n, m), x))),
          IIntegrate(5266,
              Integrate(
                  Times(u_DEFAULT,
                      Power(f_,
                          Times(Power(ArcSec(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), n_DEFAULT),
                              c_DEFAULT))),
                  x_Symbol),
              Condition(
                  Dist(Power(b, CN1),
                      Subst(
                          Integrate(
                              Times(
                                  ReplaceAll(u,
                                      Rule(x,
                                          Plus(Times(CN1, a, Power(b, CN1)),
                                              Times(Sec(x), Power(b, CN1))))),
                                  Power(f, Times(c, Power(x, n))), Sec(x), Tan(x)),
                              x),
                          x, ArcSec(Plus(a, Times(b, x)))),
                      x),
                  And(FreeQ(List(a, b, c, f), x), IGtQ(n, C0)))),
          IIntegrate(5267,
              Integrate(
                  Times(u_DEFAULT,
                      Power(f_,
                          Times(Power(ArcCsc(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), n_DEFAULT),
                              c_DEFAULT))),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(Power(b, CN1),
                          Subst(Integrate(Times(
                              ReplaceAll(u,
                                  Rule(x,
                                      Plus(Times(CN1, a, Power(b, CN1)),
                                          Times(Csc(x), Power(b, CN1))))),
                              Power(f, Times(c, Power(x, n))), Csc(x), Cot(x)), x), x, ArcCsc(
                                  Plus(a, Times(b, x)))),
                          x)),
                  And(FreeQ(List(a, b, c, f), x), IGtQ(n, C0)))),
          IIntegrate(
              5268, Integrate(ArcSec(
                  u_), x_Symbol),
              Condition(
                  Subtract(
                      Simp(Times(x,
                          ArcSec(u)), x),
                      Dist(Times(u, Power(Sqr(u), CN1D2)),
                          Integrate(
                              SimplifyIntegrand(
                                  Times(x, D(u, x),
                                      Power(Times(u, Sqrt(Subtract(Sqr(u), C1))), CN1)),
                                  x),
                              x),
                          x)),
                  And(InverseFunctionFreeQ(u, x), Not(FunctionOfExponentialQ(u, x))))),
          IIntegrate(
              5269, Integrate(ArcCsc(
                  u_), x_Symbol),
              Condition(
                  Plus(
                      Simp(Times(x,
                          ArcCsc(u)), x),
                      Dist(Times(u, Power(Sqr(u), CN1D2)),
                          Integrate(
                              SimplifyIntegrand(
                                  Times(x, D(u, x),
                                      Power(Times(u, Sqrt(Subtract(Sqr(u), C1))), CN1)),
                                  x),
                              x),
                          x)),
                  And(InverseFunctionFreeQ(u, x), Not(FunctionOfExponentialQ(u, x))))),
          IIntegrate(5270,
              Integrate(
                  Times(
                      Plus(a_DEFAULT, Times(ArcSec(u_), b_DEFAULT)),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(Subtract(
                  Simp(Times(Power(Plus(c, Times(d, x)), Plus(m, C1)), Plus(a, Times(b, ArcSec(u))),
                      Power(Times(d, Plus(m, C1)), CN1)), x),
                  Dist(Times(b, u, Power(Times(d, Plus(m, C1), Sqrt(Sqr(u))), CN1)),
                      Integrate(SimplifyIntegrand(Times(Power(Plus(c, Times(d, x)), Plus(m, C1)),
                          D(u, x), Power(Times(u, Sqrt(Subtract(Sqr(u), C1))), CN1)), x), x),
                      x)),
                  And(FreeQ(List(a, b, c, d, m), x), NeQ(m, CN1), InverseFunctionFreeQ(u, x),
                      Not(FunctionOfQ(Power(Plus(c, Times(d, x)), Plus(m, C1)), u,
                          x)),
                      Not(FunctionOfExponentialQ(u, x))))),
          IIntegrate(5271,
              Integrate(
                  Times(
                      Plus(a_DEFAULT, Times(ArcCsc(u_),
                          b_DEFAULT)),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(Times(Power(Plus(c, Times(d, x)), Plus(m, C1)),
                          Plus(a, Times(b, ArcCsc(u))), Power(Times(d, Plus(m, C1)), CN1)), x),
                      Dist(Times(b, u, Power(Times(d, Plus(m, C1), Sqrt(Sqr(u))), CN1)),
                          Integrate(
                              SimplifyIntegrand(Times(Power(Plus(c, Times(d, x)), Plus(m, C1)),
                                  D(u, x), Power(Times(u, Sqrt(Subtract(Sqr(u), C1))), CN1)), x),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, m), x), NeQ(m, CN1), InverseFunctionFreeQ(u, x),
                      Not(FunctionOfQ(Power(Plus(c, Times(d, x)), Plus(m, C1)), u,
                          x)),
                      Not(FunctionOfExponentialQ(u, x))))),
          IIntegrate(5272,
              Integrate(Times(Plus(a_DEFAULT, Times(ArcSec(u_), b_DEFAULT)), v_), x_Symbol),
              Condition(
                  With(List(Set(w, IntHide(v, x))),
                      Condition(Subtract(Dist(Plus(a, Times(b, ArcSec(u))), w, x),
                          Dist(Times(b, u, Power(Sqr(u), CN1D2)),
                              Integrate(SimplifyIntegrand(Times(w, D(u, x),
                                  Power(Times(u, Sqrt(Subtract(Sqr(u), C1))), CN1)), x), x),
                              x)),
                          InverseFunctionFreeQ(w, x))),
                  And(FreeQ(List(a, b), x), InverseFunctionFreeQ(u, x),
                      Not(MatchQ(v,
                          Condition(
                              Power(Plus(c_DEFAULT, Times(d_DEFAULT, x)), m_DEFAULT), FreeQ(
                                  List(c, d, m), x))))))),
          IIntegrate(5273,
              Integrate(Times(Plus(a_DEFAULT, Times(ArcCsc(u_), b_DEFAULT)),
                  v_), x_Symbol),
              Condition(
                  With(List(Set(w, IntHide(v, x))),
                      Condition(
                          Plus(Dist(Plus(a, Times(b, ArcCsc(u))), w, x),
                              Dist(Times(b, u, Power(Sqr(u), CN1D2)),
                                  Integrate(SimplifyIntegrand(Times(w, D(u, x),
                                      Power(Times(u, Sqrt(Subtract(Sqr(u), C1))), CN1)), x), x),
                                  x)),
                          InverseFunctionFreeQ(w, x))),
                  And(FreeQ(List(a, b), x), InverseFunctionFreeQ(u, x),
                      Not(MatchQ(v,
                          Condition(Power(Plus(c_DEFAULT, Times(d_DEFAULT, x)), m_DEFAULT),
                              FreeQ(List(c, d, m), x))))))),
          IIntegrate(5274,
              Integrate(
                  Times(
                      Power(u_, m_DEFAULT), Power(Plus(a_DEFAULT, Times(b_DEFAULT, Sinh(v_))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(Times(Power(ExpandToSum(u, x), m),
                      Power(Plus(a, Times(b, Sinh(ExpandToSum(v, x)))), n)), x),
                  And(FreeQ(List(a, b, m, n), x), LinearQ(List(u, v), x),
                      Not(LinearMatchQ(List(u, v), x))))),
          IIntegrate(5275,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(Cosh(v_), b_DEFAULT)), n_DEFAULT), Power(u_,
                          m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          Power(ExpandToSum(u,
                              x), m),
                          Power(Plus(a, Times(b, Cosh(ExpandToSum(v, x)))), n)),
                      x),
                  And(FreeQ(List(a, b, m,
                      n), x), LinearQ(List(u, v),
                          x),
                      Not(LinearMatchQ(List(u, v), x))))),
          IIntegrate(5276,
              Integrate(
                  Times(Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_DEFAULT),
                      Sinh(Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(Sinh(Plus(c, Times(d, x))),
                          Power(Plus(a, Times(b, Power(x, n))), p), x),
                      x),
                  And(FreeQ(List(a, b, c, d, n), x), IGtQ(p, C0)))),
          IIntegrate(5277,
              Integrate(
                  Times(
                      Cosh(Plus(c_DEFAULT,
                          Times(d_DEFAULT, x_))),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(Cosh(Plus(c, Times(d,
                          x))), Power(Plus(a,
                              Times(b, Power(x, n))), p),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, n), x), IGtQ(p, C0)))),
          IIntegrate(5278,
              Integrate(
                  Times(
                      Power(Plus(a_,
                          Times(b_DEFAULT, Power(x_, n_))), p_),
                      Sinh(Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(x, Plus(Negate(n), C1)),
                              Power(Plus(a, Times(b,
                                  Power(x, n))), Plus(p,
                                      C1)),
                              Sinh(Plus(c, Times(d, x))), Power(Times(b, n, Plus(p, C1)), CN1)),
                          x),
                      Negate(
                          Dist(Times(Plus(Negate(n), C1), Power(Times(b, n, Plus(p, C1)), CN1)),
                              Integrate(
                                  Times(Power(Plus(a, Times(b, Power(x, n))), Plus(p, C1)),
                                      Sinh(Plus(c, Times(d, x))), Power(Power(x, n), CN1)),
                                  x),
                              x)),
                      Negate(
                          Dist(Times(d, Power(Times(b, n, Plus(p, C1)), CN1)),
                              Integrate(Times(Power(x, Plus(Negate(n), C1)),
                                  Power(Plus(a, Times(b, Power(x, n))), Plus(p, C1)),
                                  Cosh(Plus(c, Times(d, x)))), x),
                              x))),
                  And(FreeQ(List(a, b, c, d), x), IntegerQ(p), IGtQ(n, C0), LtQ(p, CN1),
                      GtQ(n, C2)))),
          IIntegrate(5279,
              Integrate(
                  Times(
                      Cosh(Plus(c_DEFAULT,
                          Times(d_DEFAULT, x_))),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(x, Plus(Negate(n), C1)),
                              Power(Plus(a, Times(b,
                                  Power(x, n))), Plus(p,
                                      C1)),
                              Cosh(Plus(c, Times(d, x))), Power(Times(b, n, Plus(p, C1)), CN1)),
                          x),
                      Negate(
                          Dist(Times(Plus(Negate(n), C1), Power(Times(b, n, Plus(p, C1)), CN1)),
                              Integrate(
                                  Times(
                                      Power(Plus(a, Times(b, Power(x, n))), Plus(p, C1)),
                                      Cosh(Plus(c, Times(d, x))), Power(Power(x, n), CN1)),
                                  x),
                              x)),
                      Negate(
                          Dist(Times(d, Power(Times(b, n, Plus(p, C1)), CN1)),
                              Integrate(Times(Power(x, Plus(Negate(n), C1)),
                                  Power(Plus(a, Times(b, Power(x, n))), Plus(p, C1)),
                                  Sinh(Plus(c, Times(d, x)))), x),
                              x))),
                  And(FreeQ(List(a, b, c, d), x), IntegerQ(p), IGtQ(n, C0), LtQ(p, CN1),
                      GtQ(n, C2)))),
          IIntegrate(5280,
              Integrate(Times(Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_),
                  Sinh(Plus(c_DEFAULT, Times(d_DEFAULT, x_)))), x_Symbol),
              Condition(
                  Integrate(ExpandIntegrand(Sinh(Plus(c, Times(d, x))),
                      Power(Plus(a, Times(b, Power(x, n))), p), x), x),
                  And(FreeQ(List(a, b, c, d), x), ILtQ(p, C0), IGtQ(n, C0),
                      Or(EqQ(n, C2), EqQ(p, CN1))))));
}
