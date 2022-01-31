package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C1D4;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.CI;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.FresnelC;
import static org.matheclipse.core.expression.F.FresnelS;
import static org.matheclipse.core.expression.F.HypergeometricPFQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.QQ;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.Pi;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ILtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Unintegrable;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules322 {
  public static IAST RULES =
      List(
          IIntegrate(6441,
              Integrate(
                  Times(
                      Cos(Times(d_DEFAULT,
                          Sqr(x_))),
                      Power(FresnelC(Times(b_DEFAULT, x_)), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Pi, b, Power(Times(C2, d), CN1)), Subst(Integrate(Power(x, n), x), x,
                          FresnelC(Times(b, x))),
                      x),
                  And(FreeQ(List(b, d, n), x), EqQ(Sqr(d), Times(C1D4, Sqr(Pi), Power(b, C4)))))),
          IIntegrate(6442,
              Integrate(
                  Times(FresnelS(Times(b_DEFAULT, x_)),
                      Sin(Plus(c_, Times(d_DEFAULT, Sqr(x_))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(
                          Sin(c), Integrate(Times(Cos(Times(d, Sqr(x))), FresnelS(Times(b, x))), x),
                          x),
                      Dist(
                          Cos(c), Integrate(Times(Sin(Times(d, Sqr(x))), FresnelS(Times(b, x))),
                              x),
                          x)),
                  And(FreeQ(List(b, c, d), x), EqQ(Sqr(d), Times(C1D4, Sqr(Pi), Power(b, C4)))))),
          IIntegrate(6443,
              Integrate(
                  Times(Cos(Plus(c_,
                      Times(d_DEFAULT, Sqr(x_)))), FresnelC(
                          Times(b_DEFAULT, x_))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(
                          Cos(c), Integrate(Times(Cos(Times(d, Sqr(x))), FresnelC(Times(b, x))),
                              x),
                          x),
                      Dist(
                          Sin(c), Integrate(Times(Sin(Times(d, Sqr(x))), FresnelC(Times(b, x))),
                              x),
                          x)),
                  And(FreeQ(List(b, c, d), x), EqQ(Sqr(d), Times(C1D4, Sqr(Pi), Power(b, C4)))))),
          IIntegrate(6444,
              Integrate(
                  Times(Power(FresnelS(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), n_DEFAULT),
                      Sin(Plus(c_DEFAULT, Times(d_DEFAULT, Sqr(x_))))),
                  x_Symbol),
              Condition(
                  Unintegrable(Times(Power(FresnelS(Plus(a, Times(b, x))), n),
                      Sin(Plus(c, Times(d, Sqr(x))))), x),
                  FreeQ(List(a, b, c, d, n), x))),
          IIntegrate(6445,
              Integrate(Times(
                  Cos(Plus(c_DEFAULT, Times(d_DEFAULT, Sqr(x_)))),
                  Power(FresnelC(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), n_DEFAULT)), x_Symbol),
              Condition(
                  Unintegrable(
                      Times(Cos(Plus(c, Times(d, Sqr(x)))),
                          Power(FresnelC(Plus(a, Times(b, x))), n)),
                      x),
                  FreeQ(List(a, b, c, d, n), x))),
          IIntegrate(6446,
              Integrate(Times(Cos(Times(d_DEFAULT, Sqr(x_))),
                  FresnelS(Times(b_DEFAULT, x_))), x_Symbol),
              Condition(
                  Plus(
                      Simp(Times(FresnelC(Times(b, x)), FresnelS(Times(b, x)),
                          Power(Times(C2, b), CN1)), x),
                      Negate(
                          Simp(Times(QQ(1L, 8L), C1, CI, b, Sqr(x),
                              HypergeometricPFQ(List(C1, C1), List(QQ(3L, 2L), C2),
                                  Times(CN1, C1D2, CI, Sqr(b), Pi, Sqr(x)))),
                              x)),
                      Simp(
                          Times(QQ(1L, 8L), C1, CI, b, Sqr(x),
                              HypergeometricPFQ(List(C1, C1), List(QQ(3L, 2L), C2),
                                  Times(C1D2, C1, CI, Sqr(b), Pi, Sqr(x)))),
                          x)),
                  And(FreeQ(List(b, d), x), EqQ(Sqr(d), Times(C1D4, Sqr(Pi), Power(b, C4)))))),
          IIntegrate(6447,
              Integrate(Times(FresnelC(Times(b_DEFAULT, x_)),
                  Sin(Times(d_DEFAULT, Sqr(x_)))), x_Symbol),
              Condition(
                  Plus(
                      Simp(Times(b, Pi, FresnelC(Times(b, x)), FresnelS(Times(b, x)),
                          Power(Times(C4, d), CN1)), x),
                      Simp(
                          Times(QQ(1L, 8L), C1, CI, b, Sqr(x),
                              HypergeometricPFQ(List(C1, C1), List(QQ(3L, 2L), C2),
                                  Times(CN1, CI, d, Sqr(x)))),
                          x),
                      Negate(
                          Simp(Times(QQ(1L, 8L), C1, CI, b, Sqr(x),
                              HypergeometricPFQ(List(C1, C1), List(QQ(3L, 2L), C2),
                                  Times(CI, d, Sqr(x)))),
                              x))),
                  And(FreeQ(List(b, d), x), EqQ(Sqr(d), Times(C1D4, Sqr(Pi), Power(b, C4)))))),
          IIntegrate(6448,
              Integrate(Times(Cos(Plus(c_, Times(d_DEFAULT, Sqr(x_)))),
                  FresnelS(Times(b_DEFAULT, x_))), x_Symbol),
              Condition(
                  Subtract(
                      Dist(Cos(c),
                          Integrate(Times(Cos(Times(d, Sqr(x))), FresnelS(Times(b, x))), x), x),
                      Dist(
                          Sin(c), Integrate(Times(Sin(Times(d, Sqr(x))), FresnelS(Times(b, x))), x),
                          x)),
                  And(FreeQ(List(b, c, d), x), EqQ(Sqr(d), Times(C1D4, Sqr(Pi), Power(b, C4)))))),
          IIntegrate(6449,
              Integrate(
                  Times(FresnelC(Times(b_DEFAULT, x_)),
                      Sin(Plus(c_, Times(d_DEFAULT, Sqr(x_))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(Sin(c),
                          Integrate(Times(Cos(Times(d, Sqr(x))), FresnelC(Times(b, x))), x), x),
                      Dist(
                          Cos(c), Integrate(Times(Sin(Times(d, Sqr(x))), FresnelC(Times(b, x))), x),
                          x)),
                  And(FreeQ(List(b, c, d), x), EqQ(Sqr(d), Times(C1D4, Sqr(Pi), Power(b, C4)))))),
          IIntegrate(6450,
              Integrate(
                  Times(
                      Cos(Plus(c_DEFAULT, Times(d_DEFAULT,
                          Sqr(x_)))),
                      Power(FresnelS(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(Times(Cos(Plus(c, Times(d, Sqr(x)))),
                      Power(FresnelS(Plus(a, Times(b, x))), n)), x),
                  FreeQ(List(a, b, c, d, n), x))),
          IIntegrate(6451,
              Integrate(
                  Times(
                      Power(FresnelC(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), n_DEFAULT),
                      Sin(Plus(c_DEFAULT, Times(d_DEFAULT, Sqr(x_))))),
                  x_Symbol),
              Condition(
                  Unintegrable(Times(Power(FresnelC(Plus(a, Times(b, x))), n),
                      Sin(Plus(c, Times(d, Sqr(x))))), x),
                  FreeQ(List(a, b, c, d, n), x))),
          IIntegrate(6452,
              Integrate(
                  Times(FresnelS(Times(b_DEFAULT, x_)), x_,
                      Sin(Times(d_DEFAULT, Sqr(x_)))),
                  x_Symbol),
              Condition(
                  Plus(Negate(Simp(
                      Times(Cos(Times(d, Sqr(x))), FresnelS(Times(b, x)), Power(Times(C2, d), CN1)),
                      x)), Dist(Power(Times(C2, b, Pi), CN1),
                          Integrate(Sin(Times(C2, d, Sqr(x))), x), x)),
                  And(FreeQ(List(b, d), x), EqQ(Sqr(d), Times(C1D4, Sqr(Pi), Power(b, C4)))))),
          IIntegrate(6453,
              Integrate(Times(Cos(Times(d_DEFAULT, Sqr(x_))), FresnelC(Times(b_DEFAULT, x_)), x_),
                  x_Symbol),
              Condition(Subtract(Simp(
                  Times(Sin(Times(d, Sqr(x))), FresnelC(Times(b, x)), Power(Times(C2, d), CN1)), x),
                  Dist(Times(b, Power(Times(C4, d), CN1)), Integrate(Sin(Times(C2, d, Sqr(x))), x),
                      x)),
                  And(FreeQ(List(b, d), x), EqQ(Sqr(d), Times(C1D4, Sqr(Pi), Power(b, C4)))))),
          IIntegrate(6454,
              Integrate(
                  Times(
                      FresnelS(Times(b_DEFAULT, x_)), Power(x_,
                          m_),
                      Sin(Times(d_DEFAULT, Sqr(x_)))),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(Power(x, Subtract(m, C1)), Cos(Times(d, Sqr(x))),
                                  FresnelS(Times(b, x)), Power(Times(C2, d), CN1)),
                              x)),
                      Dist(Times(Subtract(m, C1), Power(Times(C2, d), CN1)),
                          Integrate(
                              Times(Power(x, Subtract(m, C2)), Cos(Times(d, Sqr(x))),
                                  FresnelS(Times(b, x))),
                              x),
                          x),
                      Dist(Power(Times(C2, b, Pi), CN1),
                          Integrate(Times(Power(x, Subtract(m, C1)), Sin(Times(C2, d, Sqr(x)))), x),
                          x)),
                  And(FreeQ(List(b, d), x), EqQ(Sqr(d), Times(C1D4, Sqr(Pi), Power(b, C4))),
                      IGtQ(m, C1)))),
          IIntegrate(6455, Integrate(
              Times(Cos(Times(d_DEFAULT, Sqr(x_))), FresnelC(Times(b_DEFAULT, x_)), Power(x_, m_)),
              x_Symbol),
              Condition(
                  Plus(
                      Simp(Times(Power(x, Subtract(m, C1)), Sin(Times(d, Sqr(x))),
                          FresnelC(Times(b, x)), Power(Times(C2, d), CN1)), x),
                      Negate(Dist(Times(Subtract(m, C1), Power(Times(C2, d), CN1)),
                          Integrate(Times(Power(x, Subtract(m, C2)), Sin(Times(d, Sqr(x))),
                              FresnelC(Times(b, x))), x),
                          x)),
                      Negate(
                          Dist(Times(b, Power(Times(C4, d), CN1)),
                              Integrate(Times(Power(x, Subtract(m, C1)), Sin(Times(C2, d, Sqr(x)))),
                                  x),
                              x))),
                  And(FreeQ(List(b, d), x), EqQ(Sqr(d), Times(C1D4, Sqr(Pi), Power(b, C4))),
                      IGtQ(m, C1)))),
          IIntegrate(6456, Integrate(
              Times(FresnelS(Times(b_DEFAULT, x_)), Power(x_, m_), Sin(Times(d_DEFAULT, Sqr(x_)))),
              x_Symbol),
              Condition(
                  Plus(
                      Simp(Times(Power(x, Plus(m, C1)), Sin(Times(d, Sqr(x))),
                          FresnelS(Times(b, x)), Power(Plus(m, C1), CN1)), x),
                      Negate(Dist(Times(C2, d, Power(Plus(m, C1), CN1)),
                          Integrate(Times(Power(x, Plus(m, C2)), Cos(Times(d, Sqr(x))),
                              FresnelS(Times(b, x))), x),
                          x)),
                      Dist(
                          Times(d, Power(Times(Pi, b, Plus(m, C1)), CN1)),
                          Integrate(Times(Power(x, Plus(m, C1)), Cos(Times(C2, d, Sqr(x)))), x), x),
                      Negate(
                          Simp(Times(d, Power(x, Plus(m, C2)),
                              Power(Times(Pi, b, Plus(m, C1), Plus(m, C2)), CN1)), x))),
                  And(FreeQ(List(b, d), x), EqQ(Sqr(d), Times(C1D4, Sqr(Pi), Power(b, C4))),
                      ILtQ(m, CN2)))),
          IIntegrate(6457,
              Integrate(
                  Times(Cos(Times(d_DEFAULT, Sqr(x_))), FresnelC(Times(b_DEFAULT, x_)),
                      Power(x_, m_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(x, Plus(m, C1)), Cos(Times(d, Sqr(x))), FresnelC(Times(b, x)),
                              Power(Plus(m, C1), CN1)),
                          x),
                      Dist(Times(C2, d, Power(Plus(m, C1), CN1)),
                          Integrate(Times(Power(x, Plus(m, C2)), Sin(Times(d, Sqr(x))),
                              FresnelC(Times(b, x))), x),
                          x),
                      Negate(
                          Dist(Times(b, Power(Times(C2, Plus(m, C1)), CN1)),
                              Integrate(Times(Power(x, Plus(m, C1)), Cos(Times(C2, d, Sqr(x)))),
                                  x),
                              x)),
                      Negate(
                          Simp(
                              Times(b, Power(x, Plus(m, C2)),
                                  Power(Times(C2, Plus(m, C1), Plus(m, C2)), CN1)),
                              x))),
                  And(FreeQ(List(b, d), x), EqQ(Sqr(d), Times(C1D4, Sqr(Pi), Power(b, C4))),
                      ILtQ(m, CN2)))),
          IIntegrate(6458,
              Integrate(
                  Times(Power(FresnelS(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), n_DEFAULT),
                      Power(Times(e_DEFAULT, x_), m_DEFAULT), Sin(
                          Plus(c_DEFAULT, Times(d_DEFAULT, Sqr(x_))))),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(Power(Times(e, x), m), Power(FresnelS(Plus(a, Times(b, x))), n),
                          Sin(Plus(c, Times(d, Sqr(x))))),
                      x),
                  FreeQ(List(a, b, c, d, e, m, n), x))),
          IIntegrate(6459,
              Integrate(Times(Cos(Plus(c_DEFAULT, Times(d_DEFAULT, Sqr(x_)))),
                  Power(FresnelC(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), n_DEFAULT),
                  Power(Times(e_DEFAULT, x_), m_DEFAULT)), x_Symbol),
              Condition(
                  Unintegrable(Times(Power(Times(e, x), m), Cos(Plus(c, Times(d, Sqr(x)))),
                      Power(FresnelC(Plus(a, Times(b, x))), n)), x),
                  FreeQ(List(a, b, c, d, e, m, n), x))),
          IIntegrate(6460,
              Integrate(Times(Cos(Times(d_DEFAULT, Sqr(x_))), FresnelS(Times(b_DEFAULT, x_)), x_),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(Times(Sin(Times(d, Sqr(x))), FresnelS(Times(b, x)),
                          Power(Times(C2, d), CN1)), x),
                      Dist(Power(Times(Pi, b), CN1), Integrate(Sqr(Sin(Times(d, Sqr(x)))), x), x)),
                  And(FreeQ(List(b, d), x), EqQ(Sqr(d), Times(C1D4, Sqr(Pi), Power(b, C4)))))));
}
