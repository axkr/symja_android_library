package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.CI;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.Cosh;
import static org.matheclipse.core.expression.F.Erf;
import static org.matheclipse.core.expression.F.Erfc;
import static org.matheclipse.core.expression.F.Erfi;
import static org.matheclipse.core.expression.F.Exp;
import static org.matheclipse.core.expression.F.F_;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.FresnelC;
import static org.matheclipse.core.expression.F.FresnelS;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.MemberQ;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Sinh;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.g_;
import static org.matheclipse.core.expression.F.h_DEFAULT;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.CosIntegral;
import static org.matheclipse.core.expression.S.CoshIntegral;
import static org.matheclipse.core.expression.S.Erf;
import static org.matheclipse.core.expression.S.Erfc;
import static org.matheclipse.core.expression.S.Erfi;
import static org.matheclipse.core.expression.S.ExpIntegralEi;
import static org.matheclipse.core.expression.S.FSymbol;
import static org.matheclipse.core.expression.S.FresnelC;
import static org.matheclipse.core.expression.S.FresnelS;
import static org.matheclipse.core.expression.S.Pi;
import static org.matheclipse.core.expression.S.SinIntegral;
import static org.matheclipse.core.expression.S.SinhIntegral;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.f;
import static org.matheclipse.core.expression.S.g;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.F;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules320 {
  public static IAST RULES =
      List(
          IIntegrate(6401,
              Integrate(
                  Times(
                      Erf(Times(
                          Plus(
                              a_DEFAULT, Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                                  b_DEFAULT)),
                          d_DEFAULT)),
                      Power(Times(e_DEFAULT, x_), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Times(e, x), Plus(m, C1)),
                              Erf(Times(d,
                                  Plus(a, Times(b, Log(Times(c, Power(x, n))))))),
                              Power(Times(e, Plus(m, C1)), CN1)),
                          x),
                      Dist(
                          Times(C2, b, d, n, Power(Times(Sqrt(Pi), Plus(m, C1)),
                              CN1)),
                          Integrate(
                              Times(Power(Times(e, x), m),
                                  Power(
                                      Exp(Sqr(
                                          Times(d, Plus(a, Times(b, Log(Times(c, Power(x, n)))))))),
                                      CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, m, n), x), NeQ(m, CN1)))),
          IIntegrate(6402,
              Integrate(
                  Times(
                      Erfc(
                          Times(
                              Plus(a_DEFAULT, Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                                  b_DEFAULT)),
                              d_DEFAULT)),
                      Power(Times(e_DEFAULT, x_), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Times(e, x), Plus(m, C1)),
                              Erfc(Times(d,
                                  Plus(a, Times(b, Log(Times(c, Power(x, n))))))),
                              Power(Times(e, Plus(m, C1)), CN1)),
                          x),
                      Dist(Times(C2, b, d, n, Power(Times(Sqrt(Pi), Plus(m, C1)), CN1)),
                          Integrate(
                              Times(Power(Times(e, x), m),
                                  Power(
                                      Exp(Sqr(
                                          Times(d, Plus(a, Times(b, Log(Times(c, Power(x, n)))))))),
                                      CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, m, n), x), NeQ(m, CN1)))),
          IIntegrate(6403,
              Integrate(
                  Times(
                      Erfi(
                          Times(
                              Plus(
                                  a_DEFAULT,
                                  Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))), b_DEFAULT)),
                              d_DEFAULT)),
                      Power(Times(e_DEFAULT, x_), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Times(e, x), Plus(m, C1)),
                              Erfi(Times(d,
                                  Plus(a, Times(b, Log(Times(c, Power(x, n))))))),
                              Power(Times(e, Plus(m, C1)), CN1)),
                          x),
                      Dist(Times(C2, b, d, n, Power(Times(Sqrt(Pi), Plus(m, C1)), CN1)),
                          Integrate(
                              Times(Power(Times(e, x), m),
                                  Exp(Sqr(
                                      Times(d, Plus(a, Times(b, Log(Times(c, Power(x, n))))))))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, m, n), x), NeQ(m, CN1)))),
          IIntegrate(6404,
              Integrate(
                  Times(Erf(Times(b_DEFAULT, x_)),
                      Sin(Plus(c_DEFAULT, Times(d_DEFAULT, Sqr(x_))))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Times(C1D2, CI),
                          Integrate(
                              Times(Exp(Subtract(Times(CN1, CI, c), Times(CI, d, Sqr(x)))),
                                  Erf(Times(b, x))),
                              x),
                          x),
                      Dist(Times(C1D2, CI),
                          Integrate(
                              Times(Exp(Plus(Times(CI, c), Times(CI, d, Sqr(x)))),
                                  Erf(Times(b, x))),
                              x),
                          x)),
                  And(FreeQ(List(b, c, d), x), EqQ(Sqr(d), Negate(Power(b, C4)))))),
          IIntegrate(6405,
              Integrate(
                  Times(Erfc(Times(b_DEFAULT, x_)),
                      Sin(Plus(c_DEFAULT, Times(d_DEFAULT, Sqr(x_))))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Times(C1D2, CI),
                          Integrate(
                              Times(Exp(Subtract(Times(CN1, CI, c), Times(CI, d, Sqr(x)))),
                                  Erfc(Times(b, x))),
                              x),
                          x),
                      Dist(Times(C1D2, CI),
                          Integrate(
                              Times(Exp(Plus(Times(CI, c), Times(CI, d, Sqr(x)))),
                                  Erfc(Times(b, x))),
                              x),
                          x)),
                  And(FreeQ(List(b, c, d), x), EqQ(Sqr(d), Negate(Power(b, C4)))))),
          IIntegrate(6406,
              Integrate(
                  Times(Erfi(Times(b_DEFAULT, x_)),
                      Sin(Plus(c_DEFAULT, Times(d_DEFAULT, Sqr(x_))))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Times(C1D2, CI),
                          Integrate(Times(Exp(Subtract(Times(CN1, CI, c), Times(CI, d, Sqr(x)))),
                              Erfi(Times(b, x))), x),
                          x),
                      Dist(Times(C1D2, CI),
                          Integrate(Times(Exp(Plus(Times(CI, c), Times(CI, d, Sqr(x)))),
                              Erfi(Times(b, x))), x),
                          x)),
                  And(FreeQ(List(b, c, d), x), EqQ(Sqr(d), Negate(Power(b, C4)))))),
          IIntegrate(6407,
              Integrate(
                  Times(Cos(Plus(c_DEFAULT, Times(d_DEFAULT, Sqr(x_)))),
                      Erf(Times(b_DEFAULT, x_))),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(C1D2,
                          Integrate(
                              Times(Exp(Subtract(Times(CN1, CI, c), Times(CI, d, Sqr(x)))),
                                  Erf(Times(b, x))),
                              x),
                          x),
                      Dist(C1D2,
                          Integrate(
                              Times(Exp(Plus(Times(CI, c), Times(CI, d, Sqr(x)))),
                                  Erf(Times(b, x))),
                              x),
                          x)),
                  And(FreeQ(List(b, c, d), x), EqQ(Sqr(d), Negate(Power(b, C4)))))),
          IIntegrate(6408,
              Integrate(
                  Times(Cos(Plus(c_DEFAULT, Times(d_DEFAULT, Sqr(x_)))),
                      Erfc(Times(b_DEFAULT, x_))),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(C1D2,
                          Integrate(
                              Times(Exp(Subtract(Times(CN1, CI, c), Times(CI, d, Sqr(x)))),
                                  Erfc(Times(b, x))),
                              x),
                          x),
                      Dist(C1D2,
                          Integrate(
                              Times(Exp(Plus(Times(CI, c),
                                  Times(CI, d, Sqr(x)))), Erfc(
                                      Times(b, x))),
                              x),
                          x)),
                  And(FreeQ(List(b, c, d), x), EqQ(Sqr(d), Negate(Power(b, C4)))))),
          IIntegrate(6409,
              Integrate(
                  Times(Cos(Plus(c_DEFAULT, Times(d_DEFAULT, Sqr(x_)))),
                      Erfi(Times(b_DEFAULT, x_))),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(C1D2,
                          Integrate(
                              Times(Exp(Subtract(Times(CN1, CI, c), Times(CI, d, Sqr(x)))),
                                  Erfi(Times(b, x))),
                              x),
                          x),
                      Dist(C1D2,
                          Integrate(
                              Times(Exp(Plus(Times(CI, c), Times(CI, d, Sqr(x)))),
                                  Erfi(Times(b, x))),
                              x),
                          x)),
                  And(FreeQ(List(b, c, d), x), EqQ(Sqr(d), Negate(Power(b, C4)))))),
          IIntegrate(6410,
              Integrate(
                  Times(Erf(Times(b_DEFAULT, x_)),
                      Sinh(Plus(c_DEFAULT, Times(d_DEFAULT, Sqr(x_))))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(
                          C1D2, Integrate(Times(Exp(Plus(c, Times(d, Sqr(x)))), Erf(Times(b, x))),
                              x),
                          x),
                      Dist(C1D2,
                          Integrate(
                              Times(Exp(Subtract(Negate(c), Times(d, Sqr(x)))),
                                  Erf(Times(b, x))),
                              x),
                          x)),
                  And(FreeQ(List(b, c, d), x), EqQ(Sqr(d), Power(b, C4))))),
          IIntegrate(6411,
              Integrate(
                  Times(Erfc(Times(b_DEFAULT, x_)),
                      Sinh(Plus(c_DEFAULT, Times(d_DEFAULT, Sqr(x_))))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(C1D2,
                          Integrate(Times(Exp(Plus(c, Times(d, Sqr(x)))), Erfc(Times(b, x))),
                              x),
                          x),
                      Dist(C1D2,
                          Integrate(
                              Times(Exp(Subtract(Negate(c), Times(d, Sqr(x)))),
                                  Erfc(Times(b, x))),
                              x),
                          x)),
                  And(FreeQ(List(b, c, d), x), EqQ(Sqr(d), Power(b, C4))))),
          IIntegrate(6412,
              Integrate(
                  Times(Erfi(Times(b_DEFAULT, x_)),
                      Sinh(Plus(c_DEFAULT, Times(d_DEFAULT, Sqr(x_))))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(C1D2,
                          Integrate(Times(Exp(Plus(c, Times(d, Sqr(x)))), Erfi(Times(b, x))),
                              x),
                          x),
                      Dist(C1D2,
                          Integrate(
                              Times(Exp(Subtract(Negate(c), Times(d, Sqr(x)))), Erfi(Times(b, x))),
                              x),
                          x)),
                  And(FreeQ(List(b, c, d), x), EqQ(Sqr(d), Power(b, C4))))),
          IIntegrate(6413,
              Integrate(
                  Times(Cosh(Plus(c_DEFAULT, Times(d_DEFAULT, Sqr(x_)))),
                      Erf(Times(b_DEFAULT, x_))),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(
                          C1D2, Integrate(Times(Exp(Plus(c, Times(d, Sqr(x)))), Erf(Times(b, x))),
                              x),
                          x),
                      Dist(C1D2,
                          Integrate(
                              Times(Exp(Subtract(Negate(c), Times(d, Sqr(x)))),
                                  Erf(Times(b, x))),
                              x),
                          x)),
                  And(FreeQ(List(b, c, d), x), EqQ(Sqr(d), Power(b, C4))))),
          IIntegrate(6414,
              Integrate(
                  Times(Cosh(Plus(c_DEFAULT, Times(d_DEFAULT, Sqr(x_)))),
                      Erfc(Times(b_DEFAULT, x_))),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(C1D2,
                          Integrate(Times(Exp(Plus(c, Times(d, Sqr(x)))), Erfc(Times(b, x))),
                              x),
                          x),
                      Dist(C1D2,
                          Integrate(
                              Times(Exp(Subtract(Negate(c), Times(d, Sqr(x)))),
                                  Erfc(Times(b, x))),
                              x),
                          x)),
                  And(FreeQ(List(b, c, d), x), EqQ(Sqr(d), Power(b, C4))))),
          IIntegrate(6415,
              Integrate(
                  Times(Cosh(Plus(c_DEFAULT, Times(d_DEFAULT, Sqr(x_)))),
                      Erfi(Times(b_DEFAULT, x_))),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(C1D2,
                          Integrate(Times(Exp(Plus(c, Times(d, Sqr(x)))), Erfi(Times(b, x))),
                              x),
                          x),
                      Dist(C1D2,
                          Integrate(
                              Times(Exp(Subtract(Negate(c),
                                  Times(d, Sqr(x)))), Erfi(
                                      Times(b, x))),
                              x),
                          x)),
                  And(FreeQ(List(b, c, d), x), EqQ(Sqr(d), Power(b, C4))))),
          IIntegrate(6416,
              Integrate($(F_,
                  Times(Plus(a_DEFAULT,
                      Times(Log(Times(c_DEFAULT, Power(Plus(d_, Times(e_DEFAULT, x_)), n_DEFAULT))),
                          b_DEFAULT)),
                      f_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(e, CN1),
                      Subst(
                          Integrate(F(
                              Times(f, Plus(a, Times(b, Log(Times(c, Power(x, n))))))), x),
                          x, Plus(d, Times(e, x))),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, n), x),
                      MemberQ(
                          List(Erf, Erfc, Erfi, FresnelS, FresnelC, ExpIntegralEi, SinIntegral,
                              CosIntegral, SinhIntegral, CoshIntegral),
                          FSymbol)))),
          IIntegrate(6417,
              Integrate(
                  Times(Power(Plus(g_, Times(h_DEFAULT, x_)), m_DEFAULT), $(F_,
                      Times(
                          Plus(a_DEFAULT,
                              Times(Log(Times(c_DEFAULT,
                                  Power(Plus(d_, Times(e_DEFAULT, x_)), n_DEFAULT))), b_DEFAULT)),
                          f_DEFAULT))),
                  x_Symbol),
              Condition(
                  Dist(Power(e, CN1),
                      Subst(
                          Integrate(
                              Times(Power(Times(g, x, Power(d, CN1)), m),
                                  F(Times(f, Plus(a, Times(b, Log(Times(c, Power(x, n)))))))),
                              x),
                          x, Plus(d, Times(e, x))),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, m, n), x),
                      EqQ(Subtract(Times(e, f),
                          Times(d, g)), C0),
                      MemberQ(
                          List(Erf, Erfc, Erfi, FresnelS, FresnelC, ExpIntegralEi, SinIntegral,
                              CosIntegral, SinhIntegral, CoshIntegral),
                          FSymbol)))),
          IIntegrate(
              6418, Integrate(FresnelS(
                  Plus(a_DEFAULT, Times(b_DEFAULT, x_))), x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Plus(a, Times(b, x)), FresnelS(Plus(a, Times(b, x))),
                              Power(b, CN1)),
                          x),
                      Simp(
                          Times(Cos(Times(C1D2, Pi, Sqr(Plus(a, Times(b, x))))),
                              Power(Times(b, Pi), CN1)),
                          x)),
                  FreeQ(List(a, b), x))),
          IIntegrate(
              6419, Integrate(FresnelC(
                  Plus(a_DEFAULT, Times(b_DEFAULT, x_))), x_Symbol),
              Condition(
                  Subtract(
                      Simp(Times(Plus(a, Times(b, x)), FresnelC(Plus(a, Times(b, x))),
                          Power(b, CN1)), x),
                      Simp(
                          Times(
                              Sin(Times(C1D2, Pi, Sqr(Plus(a, Times(b, x))))), Power(Times(b, Pi),
                                  CN1)),
                          x)),
                  FreeQ(List(a, b), x))),
          IIntegrate(6420,
              Integrate(Sqr(FresnelS(Plus(a_DEFAULT, Times(b_DEFAULT, x_)))), x_Symbol),
              Condition(Subtract(
                  Simp(Times(Plus(a, Times(b, x)), Sqr(FresnelS(Plus(a, Times(b, x)))),
                      Power(b, CN1)), x),
                  Dist(C2,
                      Integrate(Times(Plus(a, Times(b, x)),
                          Sin(Times(C1D2, Pi, Sqr(Plus(a, Times(b, x))))),
                          FresnelS(Plus(a, Times(b, x)))), x),
                      x)),
                  FreeQ(List(a, b), x))));
}
