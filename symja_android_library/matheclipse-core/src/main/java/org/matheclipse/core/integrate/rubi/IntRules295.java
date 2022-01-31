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
class IntRules295 {
  public static IAST RULES = List(
      IIntegrate(
          5901, Integrate(ArcCosh(
              u_), x_Symbol),
          Condition(
              Subtract(
                  Simp(Times(x,
                      ArcCosh(u)), x),
                  Integrate(
                      SimplifyIntegrand(
                          Times(x, D(u, x),
                              Power(Times(Sqrt(Plus(CN1, u)), Sqrt(Plus(C1, u))), CN1)),
                          x),
                      x)),
              And(InverseFunctionFreeQ(u, x), Not(FunctionOfExponentialQ(u, x))))),
      IIntegrate(5902,
          Integrate(
              Times(Plus(a_DEFAULT, Times(ArcSinh(u_), b_DEFAULT)),
                  Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT)),
              x_Symbol),
          Condition(
              Subtract(
                  Simp(
                      Times(Power(Plus(c, Times(d, x)), Plus(m, C1)), Plus(a, Times(b, ArcSinh(u))),
                          Power(Times(d, Plus(m, C1)), CN1)),
                      x),
                  Dist(Times(b, Power(Times(d, Plus(m, C1)), CN1)),
                      Integrate(
                          SimplifyIntegrand(
                              Times(Power(Plus(c, Times(d, x)), Plus(m, C1)), D(u, x),
                                  Power(Plus(C1, Sqr(u)), CN1D2)),
                              x),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, d, m), x), NeQ(m, CN1), InverseFunctionFreeQ(u, x),
                  Not(FunctionOfQ(Power(Plus(c, Times(d, x)), Plus(m, C1)), u, x)),
                  Not(FunctionOfExponentialQ(u, x))))),
      IIntegrate(5903,
          Integrate(
              Times(
                  Plus(a_DEFAULT, Times(ArcCosh(u_), b_DEFAULT)), Power(
                      Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT)),
              x_Symbol),
          Condition(
              Subtract(
                  Simp(
                      Times(
                          Power(Plus(c, Times(d, x)), Plus(m, C1)), Plus(a,
                              Times(b, ArcCosh(u))),
                          Power(Times(d, Plus(m, C1)), CN1)),
                      x),
                  Dist(
                      Times(b, Power(Times(d, Plus(m, C1)),
                          CN1)),
                      Integrate(
                          SimplifyIntegrand(
                              Times(
                                  Power(Plus(c, Times(d, x)), Plus(m, C1)), D(u, x),
                                  Power(Times(Sqrt(Plus(CN1, u)), Sqrt(Plus(C1, u))), CN1)),
                              x),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, d, m), x), NeQ(m, CN1), InverseFunctionFreeQ(u, x),
                  Not(FunctionOfQ(Power(Plus(c, Times(d, x)), Plus(m, C1)), u,
                      x)),
                  Not(FunctionOfExponentialQ(u, x))))),
      IIntegrate(5904,
          Integrate(Times(Plus(a_DEFAULT, Times(ArcSinh(u_), b_DEFAULT)),
              v_), x_Symbol),
          Condition(
              With(
                  List(Set(w,
                      IntHide(v, x))),
                  Condition(
                      Subtract(Dist(Plus(a, Times(b, ArcSinh(u))), w, x),
                          Dist(b,
                              Integrate(
                                  SimplifyIntegrand(
                                      Times(w, D(u, x), Power(Plus(C1, Sqr(u)), CN1D2)), x),
                                  x),
                              x)),
                      InverseFunctionFreeQ(w, x))),
              And(FreeQ(List(a, b), x), InverseFunctionFreeQ(u, x),
                  Not(MatchQ(v,
                      Condition(Power(Plus(c_DEFAULT, Times(d_DEFAULT, x)), m_DEFAULT),
                          FreeQ(List(c, d, m), x))))))),
      IIntegrate(5905,
          Integrate(Times(Plus(a_DEFAULT, Times(ArcCosh(u_), b_DEFAULT)), v_), x_Symbol),
          Condition(
              With(List(Set(w, IntHide(v, x))),
                  Condition(Subtract(Dist(Plus(a, Times(b, ArcCosh(u))), w, x),
                      Dist(b,
                          Integrate(SimplifyIntegrand(Times(w, D(u, x),
                              Power(Times(Sqrt(Plus(CN1, u)), Sqrt(Plus(C1, u))), CN1)), x), x),
                          x)),
                      InverseFunctionFreeQ(w, x))),
              And(FreeQ(List(a, b), x), InverseFunctionFreeQ(u, x),
                  Not(MatchQ(v,
                      Condition(Power(Plus(c_DEFAULT, Times(d_DEFAULT, x)), m_DEFAULT),
                          FreeQ(List(c, d, m), x))))))),
      IIntegrate(5906, Integrate(Exp(Times(ArcSinh(u_), n_DEFAULT)), x_Symbol),
          Condition(
              Integrate(Power(Plus(u, Sqrt(Plus(C1, Sqr(u)))), n), x), And(IntegerQ(n),
                  PolynomialQ(u, x)))),
      IIntegrate(5907,
          Integrate(Times(Exp(Times(ArcSinh(u_),
              n_DEFAULT)), Power(x_,
                  m_DEFAULT)),
              x_Symbol),
          Condition(
              Integrate(Times(Power(x, m), Power(Plus(u, Sqrt(Plus(C1, Sqr(u)))), n)),
                  x),
              And(RationalQ(m), IntegerQ(n), PolynomialQ(u, x)))),
      IIntegrate(
          5908, Integrate(Exp(
              Times(ArcCosh(u_), n_DEFAULT)), x_Symbol),
          Condition(
              Integrate(Power(Plus(u, Times(Sqrt(Plus(CN1, u)), Sqrt(Plus(C1, u)))),
                  n), x),
              And(IntegerQ(n), PolynomialQ(u, x)))),
      IIntegrate(5909,
          Integrate(Times(Exp(Times(ArcCosh(u_), n_DEFAULT)),
              Power(x_, m_DEFAULT)), x_Symbol),
          Condition(
              Integrate(
                  Times(Power(x, m),
                      Power(Plus(u, Times(Sqrt(Plus(CN1, u)), Sqrt(Plus(C1, u)))), n)),
                  x),
              And(RationalQ(m), IntegerQ(n), PolynomialQ(u, x)))),
      IIntegrate(5910,
          Integrate(
              Power(Plus(a_DEFAULT, Times(ArcTanh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                  p_DEFAULT),
              x_Symbol),
          Condition(
              Subtract(
                  Simp(Times(x,
                      Power(Plus(a, Times(b, ArcTanh(Times(c, x)))), p)), x),
                  Dist(Times(b, c, p),
                      Integrate(
                          Times(
                              x, Power(Plus(a, Times(b,
                                  ArcTanh(Times(c, x)))), Subtract(p,
                                      C1)),
                              Power(Subtract(C1, Times(Sqr(c), Sqr(x))), CN1)),
                          x),
                      x)),
              And(FreeQ(List(a, b, c), x), IGtQ(p, C0)))),
      IIntegrate(5911,
          Integrate(
              Power(Plus(a_DEFAULT, Times(ArcCoth(Times(c_DEFAULT, x_)), b_DEFAULT)),
                  p_DEFAULT),
              x_Symbol),
          Condition(
              Subtract(Simp(Times(x, Power(Plus(a, Times(b, ArcCoth(Times(c, x)))), p)), x),
                  Dist(Times(b, c, p),
                      Integrate(
                          Times(x, Power(Plus(a, Times(b, ArcCoth(Times(c, x)))), Subtract(p, C1)),
                              Power(Subtract(C1, Times(Sqr(c), Sqr(x))), CN1)),
                          x),
                      x)),
              And(FreeQ(List(a, b, c), x), IGtQ(p, C0)))),
      IIntegrate(5912,
          Integrate(
              Times(Plus(a_DEFAULT, Times(ArcTanh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                  Power(x_, CN1)),
              x_Symbol),
          Condition(
              Plus(
                  Simp(Times(a, Log(
                      x)), x),
                  Negate(Simp(Times(C1D2, b, PolyLog(C2, Times(CN1, c, x))), x)),
                  Simp(Times(C1D2, b, PolyLog(C2, Times(c, x))), x)),
              FreeQ(List(a, b, c), x))),
      IIntegrate(5913,
          Integrate(
              Times(Plus(a_DEFAULT, Times(ArcCoth(Times(c_DEFAULT, x_)),
                  b_DEFAULT)), Power(x_,
                      CN1)),
              x_Symbol),
          Condition(
              Plus(Simp(Times(a, Log(x)), x),
                  Simp(Times(C1D2, b, PolyLog(C2, Negate(Power(Times(c, x), CN1)))), x), Negate(
                      Simp(Times(C1D2, b, PolyLog(C2, Power(Times(c, x), CN1))), x))),
              FreeQ(List(a, b, c), x))),
      IIntegrate(5914,
          Integrate(
              Times(
                  Power(Plus(a_DEFAULT,
                      Times(ArcTanh(Times(c_DEFAULT, x_)), b_DEFAULT)), p_),
                  Power(x_, CN1)),
              x_Symbol),
          Condition(Subtract(
              Simp(Times(C2, Power(Plus(a, Times(b, ArcTanh(Times(c, x)))), p),
                  ArcTanh(Subtract(C1, Times(C2, Power(Subtract(C1, Times(c, x)), CN1))))), x),
              Dist(Times(C2, b, c, p),
                  Integrate(Times(Power(Plus(a, Times(b, ArcTanh(Times(c, x)))), Subtract(p, C1)),
                      ArcTanh(Subtract(C1, Times(C2, Power(Subtract(C1, Times(c, x)), CN1)))),
                      Power(Subtract(C1, Times(Sqr(c), Sqr(x))), CN1)), x),
                  x)),
              And(FreeQ(List(a, b, c), x), IGtQ(p, C1)))),
      IIntegrate(5915,
          Integrate(
              Times(
                  Power(Plus(a_DEFAULT,
                      Times(ArcCoth(Times(c_DEFAULT, x_)), b_DEFAULT)), p_),
                  Power(x_, CN1)),
              x_Symbol),
          Condition(
              Subtract(
                  Simp(
                      Times(
                          C2, Power(Plus(a, Times(b, ArcCoth(Times(c, x)))), p), ArcCoth(Subtract(
                              C1, Times(C2, Power(Subtract(C1, Times(c, x)), CN1))))),
                      x),
                  Dist(Times(C2, b, c, p),
                      Integrate(
                          Times(
                              Power(Plus(a, Times(b,
                                  ArcCoth(Times(c, x)))), Subtract(p,
                                      C1)),
                              ArcCoth(
                                  Subtract(C1, Times(C2, Power(Subtract(C1, Times(c, x)), CN1)))),
                              Power(Subtract(C1, Times(Sqr(c), Sqr(x))), CN1)),
                          x),
                      x)),
              And(FreeQ(List(a, b, c), x), IGtQ(p, C1)))),
      IIntegrate(5916,
          Integrate(
              Times(
                  Power(Plus(a_DEFAULT,
                      Times(ArcTanh(Times(c_DEFAULT, x_)), b_DEFAULT)), p_DEFAULT),
                  Power(Times(d_DEFAULT, x_), m_DEFAULT)),
              x_Symbol),
          Condition(
              Subtract(
                  Simp(Times(Power(Times(d, x), Plus(m, C1)),
                      Power(Plus(a, Times(b, ArcTanh(Times(c, x)))),
                          p),
                      Power(Times(d, Plus(m, C1)), CN1)), x),
                  Dist(Times(b, c, p, Power(Times(d, Plus(m, C1)), CN1)),
                      Integrate(
                          Times(Power(Times(d, x), Plus(m, C1)),
                              Power(Plus(a, Times(b, ArcTanh(Times(c, x)))), Subtract(p, C1)),
                              Power(Subtract(C1, Times(Sqr(c), Sqr(x))), CN1)),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, d, m), x), IGtQ(p, C0), Or(EqQ(p,
                  C1), IntegerQ(m)), NeQ(m,
                      CN1)))),
      IIntegrate(5917,
          Integrate(
              Times(
                  Power(Plus(a_DEFAULT,
                      Times(ArcCoth(Times(c_DEFAULT, x_)), b_DEFAULT)), p_DEFAULT),
                  Power(Times(d_DEFAULT, x_), m_DEFAULT)),
              x_Symbol),
          Condition(
              Subtract(
                  Simp(
                      Times(Power(Times(d, x), Plus(m, C1)),
                          Power(Plus(a, Times(b, ArcCoth(Times(c, x)))), p),
                          Power(Times(d, Plus(m, C1)), CN1)),
                      x),
                  Dist(Times(b, c, p, Power(Times(d, Plus(m, C1)), CN1)),
                      Integrate(Times(Power(Times(d, x), Plus(m, C1)),
                          Power(Plus(a, Times(b, ArcCoth(Times(c, x)))), Subtract(p, C1)), Power(
                              Subtract(C1, Times(Sqr(c), Sqr(x))), CN1)),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, d, m), x), IGtQ(p, C0), Or(EqQ(p,
                  C1), IntegerQ(m)), NeQ(m,
                      CN1)))),
      IIntegrate(5918,
          Integrate(
              Times(
                  Power(Plus(a_DEFAULT,
                      Times(ArcTanh(Times(c_DEFAULT, x_)), b_DEFAULT)), p_DEFAULT),
                  Power(Plus(d_, Times(e_DEFAULT, x_)), CN1)),
              x_Symbol),
          Condition(
              Plus(
                  Negate(
                      Simp(
                          Times(Power(Plus(a, Times(b, ArcTanh(Times(c, x)))), p),
                              Log(Times(C2,
                                  Power(Plus(C1, Times(e, x, Power(d, CN1))), CN1))),
                              Power(e, CN1)),
                          x)),
                  Dist(Times(b, c, p, Power(e, CN1)),
                      Integrate(
                          Times(Power(Plus(a, Times(b, ArcTanh(Times(c, x)))), Subtract(p, C1)),
                              Log(Times(C2, Power(Plus(C1, Times(e, x, Power(d, CN1))), CN1))),
                              Power(Subtract(C1, Times(Sqr(c), Sqr(x))), CN1)),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, d,
                  e), x), IGtQ(p,
                      C0),
                  EqQ(Subtract(Times(Sqr(c), Sqr(d)), Sqr(e)), C0)))),
      IIntegrate(5919,
          Integrate(
              Times(
                  Power(Plus(a_DEFAULT, Times(ArcCoth(Times(c_DEFAULT, x_)), b_DEFAULT)),
                      p_DEFAULT),
                  Power(Plus(d_, Times(e_DEFAULT, x_)), CN1)),
              x_Symbol),
          Condition(
              Plus(
                  Negate(
                      Simp(
                          Times(Power(Plus(a, Times(b, ArcCoth(Times(c, x)))), p),
                              Log(Times(C2,
                                  Power(Plus(C1, Times(e, x, Power(d, CN1))), CN1))),
                              Power(e, CN1)),
                          x)),
                  Dist(
                      Times(b, c, p, Power(e,
                          CN1)),
                      Integrate(
                          Times(
                              Power(Plus(a, Times(b,
                                  ArcCoth(Times(c, x)))), Subtract(p,
                                      C1)),
                              Log(Times(C2,
                                  Power(Plus(C1, Times(e, x, Power(d, CN1))), CN1))),
                              Power(Subtract(C1, Times(Sqr(c), Sqr(x))), CN1)),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, d, e), x), IGtQ(p, C0),
                  EqQ(Subtract(Times(Sqr(c), Sqr(d)), Sqr(e)), C0)))),
      IIntegrate(5920,
          Integrate(Times(Plus(a_DEFAULT, Times(ArcTanh(Times(c_DEFAULT, x_)), b_DEFAULT)),
              Power(Plus(d_, Times(e_DEFAULT, x_)), CN1)), x_Symbol),
          Condition(
              Plus(
                  Negate(
                      Simp(
                          Times(
                              Plus(a, Times(b, ArcTanh(Times(c, x)))), Log(Times(C2,
                                  Power(Plus(C1, Times(c, x)), CN1))),
                              Power(e, CN1)),
                          x)),
                  Dist(
                      Times(b, c, Power(e,
                          CN1)),
                      Integrate(
                          Times(
                              Log(Times(C2, Power(Plus(C1, Times(c, x)), CN1))), Power(
                                  Subtract(C1, Times(Sqr(c), Sqr(x))), CN1)),
                          x),
                      x),
                  Negate(
                      Dist(
                          Times(b, c, Power(e,
                              CN1)),
                          Integrate(Times(
                              Log(Times(C2, c, Plus(d, Times(e, x)),
                                  Power(Times(Plus(Times(c, d), e), Plus(C1, Times(c, x))), CN1))),
                              Power(Subtract(C1, Times(Sqr(c), Sqr(x))), CN1)), x),
                          x)),
                  Simp(Times(Plus(a, Times(b, ArcTanh(Times(c, x)))),
                      Log(Times(C2, c, Plus(d, Times(e, x)),
                          Power(Times(Plus(Times(c, d), e), Plus(C1, Times(c, x))), CN1))),
                      Power(e, CN1)), x)),
              And(FreeQ(List(a, b, c, d, e), x),
                  NeQ(Subtract(Times(Sqr(c), Sqr(d)), Sqr(e)), C0)))));
}
