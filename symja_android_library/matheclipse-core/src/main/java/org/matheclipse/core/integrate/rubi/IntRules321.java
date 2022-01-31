package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C1D4;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.CI;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.Erf;
import static org.matheclipse.core.expression.F.Exp;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.FresnelC;
import static org.matheclipse.core.expression.F.FresnelS;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.Pi;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
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
class IntRules321 {
  public static IAST RULES =
      List(
          IIntegrate(6421,
              Integrate(Sqr(FresnelC(Plus(a_DEFAULT, Times(b_DEFAULT, x_)))), x_Symbol),
              Condition(Subtract(
                  Simp(Times(Plus(a, Times(b, x)), Sqr(FresnelC(Plus(a, Times(b, x)))),
                      Power(b, CN1)), x),
                  Dist(C2,
                      Integrate(Times(Plus(a, Times(b, x)),
                          Cos(Times(C1D2, Pi, Sqr(Plus(a, Times(b, x))))),
                          FresnelC(Plus(a, Times(b, x)))), x),
                      x)),
                  FreeQ(List(a, b), x))),
          IIntegrate(6422,
              Integrate(Power(FresnelS(Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                  n_), x_Symbol),
              Condition(
                  Unintegrable(Power(FresnelS(Plus(a, Times(b, x))),
                      n), x),
                  And(FreeQ(List(a, b, n), x), NeQ(n, C1), NeQ(n, C2)))),
          IIntegrate(6423,
              Integrate(Power(FresnelC(Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                  n_), x_Symbol),
              Condition(
                  Unintegrable(Power(FresnelC(Plus(a,
                      Times(b, x))), n), x),
                  And(FreeQ(List(a, b, n), x), NeQ(n, C1), NeQ(n, C2)))),
          IIntegrate(6424,
              Integrate(Times(FresnelS(Times(b_DEFAULT,
                  x_)), Power(x_,
                      CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(Times(C1D4, Plus(C1, CI)),
                          Integrate(
                              Times(Erf(Times(C1D2, Sqrt(Pi), Plus(C1, CI), b, x)),
                                  Power(x, CN1)),
                              x),
                          x),
                      Dist(
                          Times(C1D4, Subtract(C1,
                              CI)),
                          Integrate(
                              Times(
                                  Erf(Times(C1D2, Sqrt(Pi), Subtract(C1, CI), b, x)), Power(x,
                                      CN1)),
                              x),
                          x)),
                  FreeQ(b, x))),
          IIntegrate(6425,
              Integrate(Times(FresnelC(Times(b_DEFAULT, x_)), Power(x_, CN1)), x_Symbol), Condition(
                  Plus(
                      Dist(Times(C1D4, Subtract(C1, CI)),
                          Integrate(
                              Times(Erf(Times(C1D2, Sqrt(Pi), Plus(C1, CI), b, x)), Power(x, CN1)),
                              x),
                          x),
                      Dist(Times(C1D4, Plus(C1, CI)),
                          Integrate(Times(Erf(Times(C1D2, Sqrt(Pi), Subtract(C1, CI), b, x)),
                              Power(x, CN1)), x),
                          x)),
                  FreeQ(b, x))),
          IIntegrate(6426,
              Integrate(
                  Times(FresnelS(Times(b_DEFAULT, x_)),
                      Power(Times(d_DEFAULT, x_), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Times(d, x), Plus(m, C1)), FresnelS(Times(b, x)),
                              Power(Times(d, Plus(m, C1)), CN1)),
                          x),
                      Dist(Times(b, Power(Times(d, Plus(m, C1)), CN1)),
                          Integrate(Times(Power(Times(d, x), Plus(m, C1)),
                              Sin(Times(C1D2, Pi, Sqr(b), Sqr(x)))), x),
                          x)),
                  And(FreeQ(List(b, d, m), x), NeQ(m, CN1)))),
          IIntegrate(6427,
              Integrate(
                  Times(FresnelC(Times(b_DEFAULT,
                      x_)), Power(Times(d_DEFAULT, x_),
                          m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              Power(Times(d, x), Plus(m, C1)), FresnelC(Times(b,
                                  x)),
                              Power(Times(d, Plus(m, C1)), CN1)),
                          x),
                      Dist(
                          Times(b, Power(Times(d, Plus(m, C1)),
                              CN1)),
                          Integrate(
                              Times(
                                  Power(Times(d, x), Plus(m, C1)), Cos(
                                      Times(C1D2, Pi, Sqr(b), Sqr(x)))),
                              x),
                          x)),
                  And(FreeQ(List(b, d, m), x), NeQ(m, CN1)))),
          IIntegrate(6428,
              Integrate(
                  Times(
                      FresnelS(Plus(a_DEFAULT,
                          Times(b_DEFAULT, x_))),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              Power(Plus(c, Times(d, x)), Plus(m, C1)), FresnelS(Plus(a,
                                  Times(b, x))),
                              Power(Times(d, Plus(m, C1)), CN1)),
                          x),
                      Dist(Times(b, Power(Times(d, Plus(m, C1)), CN1)),
                          Integrate(
                              Times(
                                  Power(Plus(c, Times(d, x)), Plus(m, C1)), Sin(Times(C1D2, Pi,
                                      Sqr(Plus(a, Times(b, x)))))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d), x), IGtQ(m, C0)))),
          IIntegrate(6429,
              Integrate(
                  Times(
                      FresnelC(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), Power(
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              Power(Plus(c, Times(d, x)), Plus(m, C1)),
                              FresnelC(Plus(a, Times(b, x))), Power(Times(d, Plus(m, C1)), CN1)),
                          x),
                      Dist(Times(b, Power(Times(d, Plus(m, C1)), CN1)),
                          Integrate(
                              Times(
                                  Power(Plus(c, Times(d, x)), Plus(m, C1)), Cos(
                                      Times(C1D2, Pi, Sqr(Plus(a, Times(b, x)))))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d), x), IGtQ(m, C0)))),
          IIntegrate(6430,
              Integrate(Times(Sqr(FresnelS(Times(b_DEFAULT, x_))),
                  Power(x_, m_DEFAULT)), x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(x, Plus(m, C1)), Sqr(FresnelS(Times(b, x))),
                              Power(Plus(m, C1), CN1)),
                          x),
                      Dist(Times(C2, b, Power(Plus(m, C1), CN1)),
                          Integrate(
                              Times(Power(x, Plus(m, C1)), Sin(Times(C1D2, Pi, Sqr(b), Sqr(x))),
                                  FresnelS(Times(b, x))),
                              x),
                          x)),
                  And(FreeQ(b, x), IntegerQ(m), NeQ(m, CN1)))),
          IIntegrate(6431,
              Integrate(Times(Sqr(FresnelC(
                  Times(b_DEFAULT, x_))), Power(x_,
                      m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              Power(x, Plus(m, C1)), Sqr(FresnelC(Times(b, x))), Power(Plus(m, C1),
                                  CN1)),
                          x),
                      Dist(Times(C2, b, Power(Plus(m, C1), CN1)),
                          Integrate(
                              Times(Power(x, Plus(m, C1)), Cos(Times(C1D2, Pi, Sqr(b), Sqr(x))),
                                  FresnelC(Times(b, x))),
                              x),
                          x)),
                  And(FreeQ(b, x), IntegerQ(m), NeQ(m, CN1)))),
          IIntegrate(6432,
              Integrate(
                  Times(
                      Sqr(FresnelS(Plus(a_, Times(b_DEFAULT, x_)))), Power(
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Power(b,
                          Plus(m, C1)), CN1),
                      Subst(
                          Integrate(
                              ExpandIntegrand(
                                  Sqr(FresnelS(
                                      x)),
                                  Power(Plus(Times(b, c), Times(CN1, a, d), Times(d, x)), m), x),
                              x),
                          x, Plus(a, Times(b, x))),
                      x),
                  And(FreeQ(List(a, b, c, d), x), IGtQ(m, C0)))),
          IIntegrate(6433,
              Integrate(Times(
                  Sqr(FresnelC(Plus(a_, Times(b_DEFAULT, x_)))),
                  Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT)), x_Symbol),
              Condition(Dist(Power(Power(b, Plus(m, C1)), CN1),
                  Subst(
                      Integrate(ExpandIntegrand(Sqr(FresnelC(x)),
                          Power(Plus(Times(b, c), Times(CN1, a, d), Times(d, x)), m), x), x),
                      x, Plus(a, Times(b, x))),
                  x), And(FreeQ(List(a, b, c, d), x), IGtQ(m, C0)))),
          IIntegrate(6434,
              Integrate(
                  Times(Power(FresnelS(Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                      n_DEFAULT), Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(Power(Plus(c, Times(d, x)), m),
                          Power(FresnelS(Plus(a, Times(b, x))), n)),
                      x),
                  FreeQ(List(a, b, c, d, m, n), x))),
          IIntegrate(6435,
              Integrate(
                  Times(
                      Power(FresnelC(Plus(a_DEFAULT,
                          Times(b_DEFAULT, x_))), n_DEFAULT),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(Times(Power(Plus(c, Times(d, x)), m),
                      Power(FresnelC(Plus(a, Times(b, x))), n)), x),
                  FreeQ(List(a, b, c, d, m, n), x))),
          IIntegrate(6436,
              Integrate(
                  Times(
                      Exp(Plus(c_DEFAULT,
                          Times(d_DEFAULT, Sqr(x_)))),
                      FresnelS(Times(b_DEFAULT, x_))),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(Times(C1D4, Plus(C1, CI)),
                          Integrate(
                              Times(Exp(Plus(c, Times(d, Sqr(x)))),
                                  Erf(Times(C1D2, Sqrt(Pi), Plus(C1, CI), b, x))),
                              x),
                          x),
                      Dist(Times(C1D4, Subtract(C1, CI)),
                          Integrate(Times(Exp(Plus(c, Times(d, Sqr(x)))),
                              Erf(Times(C1D2, Sqrt(Pi), Subtract(C1, CI), b, x))), x),
                          x)),
                  And(FreeQ(List(b, c,
                      d), x), EqQ(Sqr(d),
                          Times(CN1, C1D4, Sqr(Pi), Power(b, C4)))))),
          IIntegrate(6437,
              Integrate(
                  Times(
                      Exp(Plus(c_DEFAULT,
                          Times(d_DEFAULT, Sqr(x_)))),
                      FresnelC(Times(b_DEFAULT, x_))),
                  x_Symbol),
              Condition(Plus(Dist(Times(C1D4, Subtract(C1, CI)),
                  Integrate(Times(Exp(Plus(c, Times(d, Sqr(x)))),
                      Erf(Times(C1D2, Sqrt(Pi), Plus(C1, CI), b, x))), x),
                  x),
                  Dist(Times(C1D4, Plus(C1, CI)),
                      Integrate(Times(Exp(Plus(c, Times(d, Sqr(x)))),
                          Erf(Times(C1D2, Sqrt(Pi), Subtract(C1, CI), b, x))), x),
                      x)),
                  And(FreeQ(List(b, c,
                      d), x), EqQ(Sqr(d),
                          Times(CN1, C1D4, Sqr(Pi), Power(b, C4)))))),
          IIntegrate(6438,
              Integrate(Times(Exp(Plus(c_DEFAULT, Times(d_DEFAULT, Sqr(x_)))),
                  Power(FresnelS(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), n_DEFAULT)), x_Symbol),
              Condition(
                  Unintegrable(Times(Exp(Plus(c, Times(d, Sqr(x)))),
                      Power(FresnelS(Plus(a, Times(b, x))), n)), x),
                  FreeQ(List(a, b, c, d, n), x))),
          IIntegrate(6439,
              Integrate(Times(Exp(Plus(c_DEFAULT, Times(d_DEFAULT, Sqr(x_)))),
                  Power(FresnelC(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), n_DEFAULT)), x_Symbol),
              Condition(
                  Unintegrable(Times(Exp(Plus(c, Times(d, Sqr(x)))),
                      Power(FresnelC(Plus(a, Times(b, x))), n)), x),
                  FreeQ(List(a, b, c, d, n), x))),
          IIntegrate(6440,
              Integrate(Times(Power(FresnelS(Times(b_DEFAULT, x_)), n_DEFAULT),
                  Sin(Times(d_DEFAULT, Sqr(x_)))), x_Symbol),
              Condition(
                  Dist(Times(Pi, b, Power(Times(C2, d), CN1)),
                      Subst(Integrate(Power(x, n), x), x, FresnelS(Times(b, x))), x),
                  And(FreeQ(List(b, d, n), x), EqQ(Sqr(d), Times(C1D4, Sqr(Pi), Power(b, C4)))))));
}
