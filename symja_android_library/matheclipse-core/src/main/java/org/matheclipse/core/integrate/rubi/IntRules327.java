package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Cosh;
import static org.matheclipse.core.expression.F.CoshIntegral;
import static org.matheclipse.core.expression.F.F_;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.Gamma;
import static org.matheclipse.core.expression.F.HypergeometricPFQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.MemberQ;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sinh;
import static org.matheclipse.core.expression.F.SinhIntegral;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.CoshIntegral;
import static org.matheclipse.core.expression.S.EulerGamma;
import static org.matheclipse.core.expression.S.SinhIntegral;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.f;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.F;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ILtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules327 {
  public static IAST RULES =
      List(
          IIntegrate(6541,
              Integrate(
                  Times(Cosh(Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                      CoshIntegral(Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(Times(Sinh(Plus(a, Times(b, x))), CoshIntegral(Plus(c, Times(d, x))),
                          Power(b, CN1)), x),
                      Dist(Times(d, Power(b, CN1)),
                          Integrate(Times(Sinh(Plus(a, Times(b, x))), Cosh(Plus(c, Times(d, x))),
                              Power(Plus(c, Times(d, x)), CN1)), x),
                          x)),
                  FreeQ(List(a, b, c, d), x))),
          IIntegrate(6542,
              Integrate(
                  Times(
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT,
                          x_)), m_DEFAULT),
                      Sinh(Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                      SinhIntegral(Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                  x_Symbol),
              Condition(Plus(
                  Simp(Times(Power(Plus(e, Times(f, x)), m), Cosh(Plus(a, Times(b, x))),
                      SinhIntegral(Plus(c, Times(d, x))), Power(b, CN1)), x),
                  Negate(Dist(Times(d, Power(b, CN1)),
                      Integrate(Times(Power(Plus(e, Times(f, x)), m), Cosh(Plus(a, Times(b, x))),
                          Sinh(Plus(c, Times(d, x))), Power(Plus(c, Times(d, x)), CN1)), x),
                      x)),
                  Negate(Dist(Times(f, m, Power(b, CN1)),
                      Integrate(Times(Power(Plus(e, Times(f, x)), Subtract(m, C1)),
                          Cosh(Plus(a, Times(b, x))), SinhIntegral(Plus(c, Times(d, x)))), x),
                      x))),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(m, C0)))),
          IIntegrate(6543,
              Integrate(
                  Times(
                      Cosh(Plus(a_DEFAULT, Times(b_DEFAULT,
                          x_))),
                      CoshIntegral(Plus(c_DEFAULT,
                          Times(d_DEFAULT, x_))),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(Plus(
                  Simp(Times(Power(Plus(e, Times(f, x)), m), Sinh(Plus(a, Times(b, x))),
                      CoshIntegral(Plus(c, Times(d, x))), Power(b, CN1)), x),
                  Negate(Dist(Times(d, Power(b, CN1)),
                      Integrate(Times(Power(Plus(e, Times(f, x)), m), Sinh(Plus(a, Times(b, x))),
                          Cosh(Plus(c, Times(d, x))), Power(Plus(c, Times(d, x)), CN1)), x),
                      x)),
                  Negate(
                      Dist(Times(f, m, Power(b, CN1)),
                          Integrate(
                              Times(
                                  Power(Plus(e, Times(f, x)), Subtract(m, C1)),
                                  Sinh(Plus(a, Times(b, x))), CoshIntegral(Plus(c, Times(d, x)))),
                              x),
                          x))),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(m, C0)))),
          IIntegrate(6544,
              Integrate(
                  Times(Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_),
                      Sinh(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), SinhIntegral(
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Plus(e, Times(f, x)), Plus(m, C1)),
                              Sinh(Plus(a, Times(b, x))), SinhIntegral(Plus(c,
                                  Times(d, x))),
                              Power(Times(f, Plus(m, C1)), CN1)),
                          x),
                      Negate(
                          Dist(Times(b, Power(Times(f, Plus(m, C1)), CN1)),
                              Integrate(Times(Power(Plus(e, Times(f, x)), Plus(m, C1)),
                                  Cosh(Plus(a, Times(b, x))), SinhIntegral(Plus(c, Times(d, x)))),
                                  x),
                              x)),
                      Negate(Dist(Times(d, Power(Times(f, Plus(m, C1)), CN1)),
                          Integrate(Times(Power(Plus(e, Times(f, x)), Plus(m, C1)),
                              Sinh(Plus(a, Times(b, x))), Sinh(Plus(c, Times(d, x))),
                              Power(Plus(c, Times(d, x)), CN1)), x),
                          x))),
                  And(FreeQ(List(a, b, c, d, e, f), x), ILtQ(m, CN1)))),
          IIntegrate(6545,
              Integrate(
                  Times(
                      Cosh(Plus(a_DEFAULT, Times(b_DEFAULT,
                          x_))),
                      CoshIntegral(Plus(c_DEFAULT,
                          Times(d_DEFAULT, x_))),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Plus(e, Times(f, x)), Plus(m, C1)),
                              Cosh(Plus(a, Times(b, x))), CoshIntegral(Plus(c,
                                  Times(d, x))),
                              Power(Times(f, Plus(m, C1)), CN1)),
                          x),
                      Negate(Dist(Times(b, Power(Times(f, Plus(m, C1)), CN1)),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), Plus(m, C1)),
                                  Sinh(Plus(a, Times(b, x))), CoshIntegral(Plus(c, Times(d, x)))),
                              x),
                          x)),
                      Negate(Dist(Times(d, Power(Times(f, Plus(m, C1)), CN1)),
                          Integrate(Times(Power(Plus(e, Times(f, x)), Plus(m, C1)),
                              Cosh(Plus(a, Times(b, x))), Cosh(Plus(c, Times(d, x))),
                              Power(Plus(c, Times(d, x)), CN1)), x),
                          x))),
                  And(FreeQ(List(a, b, c, d, e, f), x), ILtQ(m, CN1)))),
          IIntegrate(6546,
              Integrate(Times(Cosh(Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                  SinhIntegral(Plus(c_DEFAULT, Times(d_DEFAULT, x_)))), x_Symbol),
              Condition(
                  Subtract(
                      Simp(Times(Sinh(Plus(a, Times(b, x))), SinhIntegral(Plus(c, Times(d, x))),
                          Power(b, CN1)), x),
                      Dist(Times(d, Power(b, CN1)),
                          Integrate(Times(Sinh(Plus(a, Times(b, x))), Sinh(Plus(c, Times(d, x))),
                              Power(Plus(c, Times(d, x)), CN1)), x),
                          x)),
                  FreeQ(List(a, b, c, d), x))),
          IIntegrate(6547,
              Integrate(
                  Times(CoshIntegral(Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                      Sinh(Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Cosh(Plus(a, Times(b, x))), CoshIntegral(Plus(c, Times(d, x))),
                              Power(b, CN1)),
                          x),
                      Dist(
                          Times(d, Power(b,
                              CN1)),
                          Integrate(Times(Cosh(Plus(a, Times(b, x))), Cosh(Plus(c, Times(d, x))),
                              Power(Plus(c, Times(d, x)), CN1)), x),
                          x)),
                  FreeQ(List(a, b, c, d), x))),
          IIntegrate(6548,
              Integrate(
                  Times(
                      Cosh(Plus(a_DEFAULT, Times(b_DEFAULT,
                          x_))),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT),
                      SinhIntegral(Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                  x_Symbol),
              Condition(Plus(
                  Simp(Times(Power(Plus(e, Times(f, x)), m), Sinh(Plus(a, Times(b, x))),
                      SinhIntegral(Plus(c, Times(d, x))), Power(b, CN1)), x),
                  Negate(Dist(Times(d, Power(b, CN1)),
                      Integrate(Times(Power(Plus(e, Times(f, x)), m), Sinh(Plus(a, Times(b, x))),
                          Sinh(Plus(c, Times(d, x))), Power(Plus(c, Times(d, x)), CN1)), x),
                      x)),
                  Negate(
                      Dist(Times(f, m, Power(b, CN1)),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), Subtract(m, C1)),
                                  Sinh(Plus(a, Times(b, x))), SinhIntegral(Plus(c, Times(d, x)))),
                              x),
                          x))),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(m, C0)))),
          IIntegrate(6549,
              Integrate(
                  Times(
                      CoshIntegral(Plus(c_DEFAULT, Times(d_DEFAULT,
                          x_))),
                      Power(Plus(e_DEFAULT,
                          Times(f_DEFAULT, x_)), m_DEFAULT),
                      Sinh(Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                  x_Symbol),
              Condition(Plus(
                  Simp(Times(Power(Plus(e, Times(f, x)), m), Cosh(Plus(a, Times(b, x))),
                      CoshIntegral(Plus(c, Times(d, x))), Power(b, CN1)), x),
                  Negate(Dist(Times(d, Power(b, CN1)),
                      Integrate(Times(Power(Plus(e, Times(f, x)), m), Cosh(Plus(a, Times(b, x))),
                          Cosh(Plus(c, Times(d, x))), Power(Plus(c, Times(d, x)), CN1)), x),
                      x)),
                  Negate(
                      Dist(Times(f, m, Power(b, CN1)),
                          Integrate(Times(Power(Plus(e, Times(f, x)), Subtract(m, C1)),
                              Cosh(Plus(a, Times(b, x))), CoshIntegral(Plus(c, Times(d, x)))), x),
                          x))),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(m, C0)))),
          IIntegrate(6550,
              Integrate(
                  Times(
                      Cosh(Plus(a_DEFAULT, Times(b_DEFAULT,
                          x_))),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT),
                      SinhIntegral(Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                  x_Symbol),
              Condition(Plus(
                  Simp(Times(Power(Plus(e, Times(f, x)), Plus(m, C1)), Cosh(Plus(a, Times(b, x))),
                      SinhIntegral(Plus(c, Times(d, x))), Power(Times(f, Plus(m, C1)), CN1)), x),
                  Negate(Dist(Times(b, Power(Times(f, Plus(m, C1)), CN1)),
                      Integrate(Times(Power(Plus(e, Times(f, x)), Plus(m, C1)),
                          Sinh(Plus(a, Times(b, x))), SinhIntegral(Plus(c, Times(d, x)))), x),
                      x)),
                  Negate(Dist(Times(d, Power(Times(f, Plus(m, C1)), CN1)),
                      Integrate(Times(Power(Plus(e, Times(f, x)), Plus(m, C1)),
                          Cosh(Plus(a, Times(b, x))), Sinh(Plus(c, Times(d, x))),
                          Power(Plus(c, Times(d, x)), CN1)), x),
                      x))),
                  And(FreeQ(List(a, b, c, d, e, f), x), ILtQ(m, CN1)))),
          IIntegrate(6551,
              Integrate(
                  Times(CoshIntegral(Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                      Power(Plus(e_DEFAULT,
                          Times(f_DEFAULT, x_)), m_),
                      Sinh(Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Plus(e, Times(f, x)), Plus(m, C1)),
                              Sinh(Plus(a, Times(b, x))), CoshIntegral(Plus(c,
                                  Times(d, x))),
                              Power(Times(f, Plus(m, C1)), CN1)),
                          x),
                      Negate(
                          Dist(Times(b, Power(Times(f, Plus(m, C1)), CN1)),
                              Integrate(Times(Power(Plus(e, Times(f, x)), Plus(m, C1)),
                                  Cosh(Plus(a, Times(b, x))), CoshIntegral(Plus(c, Times(d, x)))),
                                  x),
                              x)),
                      Negate(
                          Dist(Times(d, Power(Times(f, Plus(m, C1)), CN1)),
                              Integrate(Times(Power(Plus(e, Times(f, x)), Plus(m, C1)),
                                  Sinh(Plus(a, Times(b, x))), Cosh(Plus(c, Times(d, x))),
                                  Power(Plus(c, Times(d, x)), CN1)), x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, f), x), ILtQ(m, CN1)))),
          IIntegrate(6552,
              Integrate(
                  SinhIntegral(
                      Times(
                          Plus(
                              a_DEFAULT, Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                                  b_DEFAULT)),
                          d_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(x,
                              SinhIntegral(Times(d,
                                  Plus(a, Times(b, Log(Times(c, Power(x, n)))))))),
                          x),
                      Dist(Times(b, d, n),
                          Integrate(
                              Times(Sinh(Times(d, Plus(a, Times(b, Log(Times(c, Power(x, n))))))),
                                  Power(Times(d, Plus(a, Times(b, Log(Times(c, Power(x, n)))))),
                                      CN1)),
                              x),
                          x)),
                  FreeQ(List(a, b, c, d, n), x))),
          IIntegrate(6553,
              Integrate(
                  CoshIntegral(
                      Times(
                          Plus(
                              a_DEFAULT, Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                                  b_DEFAULT)),
                          d_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(x, CoshIntegral(
                              Times(d, Plus(a, Times(b, Log(Times(c, Power(x, n)))))))),
                          x),
                      Dist(Times(b, d, n),
                          Integrate(
                              Times(
                                  Cosh(Times(d,
                                      Plus(a, Times(b, Log(Times(c, Power(x, n))))))),
                                  Power(Times(d, Plus(a, Times(b, Log(Times(c, Power(x, n)))))),
                                      CN1)),
                              x),
                          x)),
                  FreeQ(List(a, b, c, d, n), x))),
          IIntegrate(6554,
              Integrate(
                  Times(Power(x_, CN1),
                      $(F_,
                          Times(Plus(a_DEFAULT,
                              Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))), b_DEFAULT)),
                              d_DEFAULT))),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(n, CN1), Subst(F(Times(d,
                          Plus(a, Times(b, x)))), x, Log(
                              Times(c, Power(x, n)))),
                      x),
                  And(FreeQ(List(a, b, c, d, n), x),
                      MemberQ(List(SinhIntegral, CoshIntegral), x)))),
          IIntegrate(6555,
              Integrate(
                  Times(
                      Power(Times(e_DEFAULT,
                          x_), m_DEFAULT),
                      SinhIntegral(
                          Times(
                              Plus(
                                  a_DEFAULT, Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                                      b_DEFAULT)),
                              d_DEFAULT))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Times(e, x), Plus(m, C1)),
                              SinhIntegral(Times(d,
                                  Plus(a, Times(b, Log(Times(c, Power(x, n))))))),
                              Power(Times(e, Plus(m, C1)), CN1)),
                          x),
                      Dist(
                          Times(b, d, n, Power(Plus(m, C1),
                              CN1)),
                          Integrate(
                              Times(Power(Times(e, x), m),
                                  Sinh(Times(d,
                                      Plus(a, Times(b, Log(Times(c, Power(x, n))))))),
                                  Power(Times(d, Plus(a, Times(b, Log(Times(c, Power(x, n)))))),
                                      CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, m, n), x), NeQ(m, CN1)))),
          IIntegrate(6556,
              Integrate(
                  Times(
                      CoshIntegral(Times(
                          Plus(a_DEFAULT,
                              Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))), b_DEFAULT)),
                          d_DEFAULT)),
                      Power(Times(e_DEFAULT, x_), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Times(e, x), Plus(m, C1)),
                              CoshIntegral(Times(d,
                                  Plus(a, Times(b, Log(Times(c, Power(x, n))))))),
                              Power(Times(e, Plus(m, C1)), CN1)),
                          x),
                      Dist(
                          Times(b, d, n, Power(Plus(m, C1),
                              CN1)),
                          Integrate(
                              Times(Power(Times(e, x), m),
                                  Cosh(Times(d, Plus(a, Times(b, Log(Times(c, Power(x, n))))))),
                                  Power(Times(d, Plus(a, Times(b, Log(Times(c, Power(x, n)))))),
                                      CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, m, n), x), NeQ(m, CN1)))),
          IIntegrate(
              6557, Integrate(Gamma(n_,
                  Plus(a_DEFAULT, Times(b_DEFAULT, x_))), x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Plus(a, Times(b, x)), Gamma(n, Plus(a, Times(b, x))),
                              Power(b, CN1)),
                          x),
                      Simp(Times(Gamma(Plus(n, C1), Plus(a, Times(b, x))), Power(b, CN1)), x)),
                  FreeQ(List(a, b, n), x))),
          IIntegrate(6558,
              Integrate(Times(Gamma(C0, Times(b_DEFAULT, x_)),
                  Power(x_, CN1)), x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(b, x,
                              HypergeometricPFQ(List(C1, C1, C1), List(C2, C2, C2),
                                  Times(CN1, b, x))),
                          x),
                      Negate(Simp(Times(EulerGamma, Log(x)),
                          x)),
                      Negate(Simp(Times(C1D2, C1, Sqr(Log(Times(b, x)))), x))),
                  FreeQ(b, x))),
          IIntegrate(6559,
              Integrate(Times(Gamma(n_, Times(b_DEFAULT, x_)),
                  Power(x_, CN1)), x_Symbol),
              Condition(
                  Plus(Negate(Simp(Gamma(Subtract(n, C1), Times(b, x)), x)),
                      Dist(Subtract(n, C1),
                          Integrate(Times(Gamma(Subtract(n, C1), Times(b, x)), Power(x, CN1)), x),
                          x)),
                  And(FreeQ(b, x), IGtQ(n, C1)))),
          IIntegrate(6560,
              Integrate(Times(Gamma(n_, Times(b_DEFAULT, x_)), Power(x_, CN1)), x_Symbol),
              Condition(
                  Plus(Simp(Times(Gamma(n, Times(b, x)), Power(n, CN1)), x),
                      Dist(Power(n, CN1),
                          Integrate(Times(Gamma(Plus(n, C1), Times(b, x)), Power(x, CN1)), x), x)),
                  And(FreeQ(b, x), ILtQ(n, C0)))));
}
