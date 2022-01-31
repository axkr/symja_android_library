package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.ArcCos;
import static org.matheclipse.core.expression.F.ArcSin;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Csc;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.QQ;
import static org.matheclipse.core.expression.F.Sec;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FracPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntHide;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SimplifyIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules232 {
  public static IAST RULES =
      List(
          IIntegrate(4641,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcSin(Times(c_DEFAULT, x_)), b_DEFAULT)), n_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(
                          Power(Plus(a, Times(b,
                              ArcSin(Times(c, x)))), Plus(n,
                                  C1)),
                          Power(Times(b, c, Sqrt(d), Plus(n, C1)), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, n), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      GtQ(d, C0), NeQ(n, CN1)))),
          IIntegrate(4642,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCos(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          n_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Negate(
                      Simp(
                          Times(Power(Plus(a, Times(b, ArcCos(Times(c, x)))), Plus(n, C1)),
                              Power(Times(b, c, Sqrt(d), Plus(n, C1)), CN1)),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, n), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      GtQ(d, C0), NeQ(n, CN1)))),
          IIntegrate(4643,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcSin(Times(c_DEFAULT, x_)), b_DEFAULT)), n_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Sqrt(Subtract(C1, Times(Sqr(c), Sqr(x)))), Power(
                              Plus(d, Times(e, Sqr(x))), CN1D2)),
                      Integrate(
                          Times(
                              Power(Plus(a,
                                  Times(b, ArcSin(Times(c, x)))), n),
                              Power(Subtract(C1, Times(Sqr(c), Sqr(x))), CN1D2)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e,
                      n), x), EqQ(Plus(Times(Sqr(c), d), e),
                          C0),
                      Not(GtQ(d, C0))))),
          IIntegrate(4644,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcCos(Times(c_DEFAULT, x_)), b_DEFAULT)), n_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Sqrt(Subtract(C1, Times(Sqr(c), Sqr(x)))), Power(
                              Plus(d, Times(e, Sqr(x))), CN1D2)),
                      Integrate(
                          Times(
                              Power(Plus(a,
                                  Times(b, ArcCos(Times(c, x)))), n),
                              Power(Subtract(C1, Times(Sqr(c), Sqr(x))), CN1D2)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e,
                      n), x), EqQ(Plus(Times(Sqr(c), d), e),
                          C0),
                      Not(GtQ(d, C0))))),
          IIntegrate(4645,
              Integrate(
                  Times(
                      Plus(a_DEFAULT, Times(ArcSin(Times(c_DEFAULT, x_)),
                          b_DEFAULT)),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(u,
                          IntHide(Power(Plus(d, Times(e, Sqr(x))), p), x))),
                      Subtract(Dist(Plus(a, Times(b, ArcSin(Times(c, x)))), u, x), Dist(Times(b, c),
                          Integrate(
                              SimplifyIntegrand(
                                  Times(u, Power(Subtract(C1, Times(Sqr(c), Sqr(x))), CN1D2)), x),
                              x),
                          x))),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d),
                      e), C0), IGtQ(p,
                          C0)))),
          IIntegrate(4646,
              Integrate(
                  Times(
                      Plus(a_DEFAULT, Times(ArcCos(Times(c_DEFAULT, x_)),
                          b_DEFAULT)),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(List(Set(u, IntHide(Power(Plus(d, Times(e, Sqr(x))), p), x))),
                      Plus(Dist(Plus(a, Times(b, ArcCos(Times(c, x)))), u, x),
                          Dist(Times(b, c),
                              Integrate(SimplifyIntegrand(
                                  Times(u, Power(Subtract(C1, Times(Sqr(c), Sqr(x))), CN1D2)), x),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      IGtQ(p, C0)))),
          IIntegrate(4647,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(ArcSin(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          n_DEFAULT),
                      Sqrt(Plus(d_, Times(e_DEFAULT, Sqr(x_))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(C1D2, x, Sqrt(Plus(d, Times(e, Sqr(x)))),
                              Power(Plus(a, Times(b, ArcSin(Times(c, x)))), n)),
                          x),
                      Dist(
                          Times(
                              Sqrt(Plus(d, Times(e,
                                  Sqr(x)))),
                              Power(Times(C2, Sqrt(Subtract(C1, Times(Sqr(c), Sqr(x))))), CN1)),
                          Integrate(
                              Times(
                                  Power(Plus(a, Times(b, ArcSin(Times(c, x)))), n), Power(
                                      Subtract(C1, Times(Sqr(c), Sqr(x))), CN1D2)),
                              x),
                          x),
                      Negate(
                          Dist(
                              Times(
                                  b, c, n, Sqrt(Plus(d, Times(e, Sqr(x)))),
                                  Power(Times(C2, Sqrt(Subtract(C1, Times(Sqr(c), Sqr(x))))), CN1)),
                              Integrate(Times(x,
                                  Power(Plus(a, Times(b, ArcSin(Times(c, x)))), Subtract(n, C1))),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      GtQ(n, C0)))),
          IIntegrate(4648,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(ArcCos(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          n_DEFAULT),
                      Sqrt(Plus(d_, Times(e_DEFAULT, Sqr(x_))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(
                              C1D2, x, Sqrt(Plus(d,
                                  Times(e, Sqr(x)))),
                              Power(Plus(a, Times(b, ArcCos(Times(c, x)))), n)),
                          x),
                      Dist(
                          Times(
                              Sqrt(Plus(d, Times(e,
                                  Sqr(x)))),
                              Power(Times(C2, Sqrt(Subtract(C1, Times(Sqr(c), Sqr(x))))), CN1)),
                          Integrate(
                              Times(
                                  Power(Plus(a, Times(b,
                                      ArcCos(Times(c, x)))), n),
                                  Power(Subtract(C1, Times(Sqr(c), Sqr(x))), CN1D2)),
                              x),
                          x),
                      Dist(
                          Times(
                              b, c, n, Sqrt(Plus(d, Times(e, Sqr(x)))), Power(Times(C2,
                                  Sqrt(Subtract(C1, Times(Sqr(c), Sqr(x))))), CN1)),
                          Integrate(
                              Times(
                                  x, Power(Plus(a, Times(b, ArcCos(Times(c, x)))),
                                      Subtract(n, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d),
                      e), C0), GtQ(n,
                          C0)))),
          IIntegrate(4649,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(ArcSin(Times(c_DEFAULT, x_)),
                          b_DEFAULT)), n_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(x, Power(Plus(d, Times(e, Sqr(x))), p),
                              Power(Plus(a,
                                  Times(b, ArcSin(Times(c, x)))), n),
                              Power(Plus(Times(C2, p), C1), CN1)),
                          x),
                      Dist(
                          Times(C2, d, p, Power(Plus(Times(C2, p), C1),
                              CN1)),
                          Integrate(
                              Times(
                                  Power(Plus(d, Times(e, Sqr(x))), Subtract(p, C1)), Power(
                                      Plus(a, Times(b, ArcSin(Times(c, x)))), n)),
                              x),
                          x),
                      Negate(
                          Dist(
                              Times(b, c, n, Power(d, IntPart(p)),
                                  Power(Plus(d, Times(e, Sqr(x))), FracPart(p)),
                                  Power(
                                      Times(Plus(Times(C2, p), C1),
                                          Power(Subtract(C1, Times(Sqr(c), Sqr(x))), FracPart(p))),
                                      CN1)),
                              Integrate(Times(x,
                                  Power(Subtract(C1, Times(Sqr(c), Sqr(x))), Subtract(p, C1D2)),
                                  Power(Plus(a, Times(b, ArcSin(Times(c, x)))), Subtract(n, C1))),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d), e), C0), GtQ(n, C0),
                      GtQ(p, C0)))),
          IIntegrate(4650,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCos(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          n_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(
                              x, Power(Plus(d, Times(e,
                                  Sqr(x))), p),
                              Power(Plus(a,
                                  Times(b, ArcCos(Times(c, x)))), n),
                              Power(Plus(Times(C2, p), C1), CN1)),
                          x),
                      Dist(Times(C2, d, p, Power(Plus(Times(C2, p), C1), CN1)),
                          Integrate(
                              Times(Power(Plus(d, Times(e, Sqr(x))), Subtract(p, C1)),
                                  Power(Plus(a, Times(b, ArcCos(Times(c, x)))), n)),
                              x),
                          x),
                      Dist(
                          Times(b, c, n, Power(d, IntPart(p)),
                              Power(Plus(d, Times(e, Sqr(x))), FracPart(p)),
                              Power(
                                  Times(Plus(Times(C2, p), C1),
                                      Power(Subtract(C1, Times(Sqr(c), Sqr(x))), FracPart(p))),
                                  CN1)),
                          Integrate(
                              Times(x,
                                  Power(Subtract(C1, Times(Sqr(c),
                                      Sqr(x))), Subtract(p,
                                          C1D2)),
                                  Power(Plus(a, Times(b, ArcCos(Times(c, x)))), Subtract(n, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d),
                      e), C0), GtQ(n,
                          C0),
                      GtQ(p, C0)))),
          IIntegrate(4651,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcSin(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          n_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), QQ(-3L, 2L))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              x, Power(Plus(a, Times(b, ArcSin(Times(c, x)))), n), Power(
                                  Times(d, Sqrt(Plus(d, Times(e, Sqr(x))))), CN1)),
                          x),
                      Dist(Times(b, c, n, Power(d, CN1D2)), Integrate(
                          Times(x, Power(Plus(a, Times(b, ArcSin(Times(c, x)))), Subtract(n, C1)),
                              Power(Plus(d, Times(e, Sqr(x))), CN1)),
                          x), x)),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d),
                      e), C0), GtQ(n,
                          C0),
                      GtQ(d, C0)))),
          IIntegrate(4652,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCos(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          n_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), QQ(-3L, 2L))),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(
                              x, Power(Plus(a, Times(b, ArcCos(Times(c, x)))), n), Power(
                                  Times(d, Sqrt(Plus(d, Times(e, Sqr(x))))), CN1)),
                          x),
                      Dist(
                          Times(b, c, n, Power(d,
                              CN1D2)),
                          Integrate(
                              Times(x,
                                  Power(Plus(a, Times(b, ArcCos(Times(c, x)))), Subtract(n, C1)),
                                  Power(Plus(d, Times(e, Sqr(x))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d),
                      e), C0), GtQ(n,
                          C0),
                      GtQ(d, C0)))),
          IIntegrate(4653,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcSin(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          n_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), QQ(-3L, 2L))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(x, Power(Plus(a, Times(b, ArcSin(Times(c, x)))), n),
                              Power(Times(d, Sqrt(Plus(d, Times(e, Sqr(x))))), CN1)),
                          x),
                      Dist(
                          Times(
                              b, c, n, Sqrt(Subtract(C1, Times(Sqr(c), Sqr(x)))), Power(
                                  Times(d, Sqrt(Plus(d, Times(e, Sqr(x))))), CN1)),
                          Integrate(
                              Times(x,
                                  Power(Plus(a, Times(b, ArcSin(Times(c, x)))), Subtract(n, C1)),
                                  Power(Subtract(C1, Times(Sqr(c), Sqr(x))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d),
                      e), C0), GtQ(n,
                          C0)))),
          IIntegrate(4654,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCos(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          n_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), QQ(-3L, 2L))),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(
                              x, Power(Plus(a, Times(b, ArcCos(Times(c, x)))), n), Power(
                                  Times(d, Sqrt(Plus(d, Times(e, Sqr(x))))), CN1)),
                          x),
                      Dist(
                          Times(
                              b, c, n, Sqrt(Subtract(C1, Times(Sqr(c), Sqr(x)))), Power(
                                  Times(d, Sqrt(Plus(d, Times(e, Sqr(x))))), CN1)),
                          Integrate(
                              Times(x,
                                  Power(Plus(a, Times(b, ArcCos(Times(c, x)))), Subtract(n,
                                      C1)),
                                  Power(Subtract(C1, Times(Sqr(c), Sqr(x))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      GtQ(n, C0)))),
          IIntegrate(4655,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcSin(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          n_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(x, Power(Plus(d, Times(e, Sqr(x))), Plus(p, C1)),
                                  Power(Plus(a, Times(b, ArcSin(Times(c, x)))),
                                      n),
                                  Power(Times(C2, d, Plus(p, C1)), CN1)),
                              x)),
                      Dist(
                          Times(Plus(Times(C2,
                              p), C3), Power(Times(C2, d, Plus(p, C1)),
                                  CN1)),
                          Integrate(
                              Times(
                                  Power(Plus(d, Times(e,
                                      Sqr(x))), Plus(p,
                                          C1)),
                                  Power(Plus(a, Times(b, ArcSin(Times(c, x)))), n)),
                              x),
                          x),
                      Dist(
                          Times(b, c, n, Power(d, IntPart(p)),
                              Power(Plus(d, Times(e, Sqr(x))), FracPart(p)),
                              Power(
                                  Times(C2, Plus(p, C1),
                                      Power(Subtract(C1, Times(Sqr(c), Sqr(x))), FracPart(p))),
                                  CN1)),
                          Integrate(
                              Times(x, Power(Subtract(C1, Times(Sqr(c), Sqr(x))), Plus(p, C1D2)),
                                  Power(Plus(a, Times(b, ArcSin(Times(c, x)))), Subtract(n, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d),
                      e), C0), GtQ(n,
                          C0),
                      LtQ(p, CN1), NeQ(p, QQ(-3L, 2L))))),
          IIntegrate(4656,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcCos(Times(c_DEFAULT, x_)), b_DEFAULT)), n_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(x, Power(Plus(d, Times(e, Sqr(x))), Plus(p, C1)),
                                  Power(Plus(a, Times(b, ArcCos(Times(c, x)))),
                                      n),
                                  Power(Times(C2, d, Plus(p, C1)), CN1)),
                              x)),
                      Dist(
                          Times(Plus(Times(C2, p), C3), Power(Times(C2, d, Plus(p, C1)),
                              CN1)),
                          Integrate(
                              Times(
                                  Power(Plus(d, Times(e, Sqr(x))), Plus(p,
                                      C1)),
                                  Power(Plus(a, Times(b, ArcCos(Times(c, x)))), n)),
                              x),
                          x),
                      Negate(
                          Dist(Times(b, c, n, Power(d, IntPart(p)),
                              Power(Plus(d, Times(e, Sqr(x))), FracPart(p)),
                              Power(
                                  Times(C2, Plus(p, C1),
                                      Power(Subtract(C1, Times(Sqr(c), Sqr(x))), FracPart(p))),
                                  CN1)),
                              Integrate(Times(x,
                                  Power(Subtract(C1, Times(Sqr(c), Sqr(x))), Plus(p, C1D2)),
                                  Power(Plus(a, Times(b, ArcCos(Times(c, x)))), Subtract(n, C1))),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d), e), C0), GtQ(n, C0),
                      LtQ(p, CN1), NeQ(p, QQ(-3L, 2L))))),
          IIntegrate(4657,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(ArcSin(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          n_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Times(c,
                          d), CN1),
                      Subst(Integrate(Times(Power(Plus(a, Times(b, x)), n), Sec(x)), x), x,
                          ArcSin(Times(c, x))),
                      x),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d),
                      e), C0), IGtQ(n,
                          C0)))),
          IIntegrate(4658,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(ArcCos(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          n_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(Power(Times(c, d), CN1),
                          Subst(Integrate(Times(Power(Plus(a, Times(b, x)), n), Csc(x)), x), x,
                              ArcCos(Times(c, x))),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      IGtQ(n, C0)))),
          IIntegrate(4659,
              Integrate(
                  Times(Power(Plus(a_DEFAULT, Times(ArcSin(Times(c_DEFAULT, x_)), b_DEFAULT)), n_),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Sqrt(Subtract(C1, Times(Sqr(c), Sqr(x)))),
                              Power(Plus(d, Times(e, Sqr(x))), p),
                              Power(Plus(a, Times(b, ArcSin(Times(c, x)))), Plus(n,
                                  C1)),
                              Power(Times(b, c, Plus(n, C1)), CN1)),
                          x),
                      Dist(
                          Times(c, Plus(Times(C2, p), C1), Power(d, IntPart(p)),
                              Power(Plus(d, Times(e, Sqr(x))), FracPart(p)),
                              Power(
                                  Times(b, Plus(n, C1),
                                      Power(Subtract(C1, Times(Sqr(c), Sqr(x))), FracPart(p))),
                                  CN1)),
                          Integrate(
                              Times(x,
                                  Power(Subtract(C1, Times(Sqr(c), Sqr(x))), Subtract(p, C1D2)),
                                  Power(Plus(a, Times(b, ArcSin(Times(c, x)))), Plus(n, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, p), x), EqQ(Plus(Times(Sqr(c), d),
                      e), C0), LtQ(n,
                          CN1)))),
          IIntegrate(4660,
              Integrate(
                  Times(Power(Plus(a_DEFAULT, Times(ArcCos(Times(c_DEFAULT, x_)), b_DEFAULT)), n_),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Negate(
                          Simp(
                              Times(Sqrt(Subtract(C1, Times(Sqr(c),
                                  Sqr(x)))), Power(Plus(d, Times(e, Sqr(x))), p), Power(Plus(a,
                                      Times(b, ArcCos(Times(c, x)))), Plus(n, C1)),
                                  Power(Times(b, c, Plus(n, C1)), CN1)),
                              x)),
                      Dist(
                          Times(
                              c, Plus(Times(C2, p), C1), Power(d, IntPart(p)),
                              Power(Plus(d, Times(e, Sqr(x))), FracPart(p)),
                              Power(
                                  Times(b, Plus(n, C1),
                                      Power(Subtract(C1, Times(Sqr(c), Sqr(x))), FracPart(p))),
                                  CN1)),
                          Integrate(Times(x,
                              Power(Subtract(C1, Times(Sqr(c), Sqr(x))), Subtract(p, C1D2)),
                              Power(Plus(a, Times(b, ArcCos(Times(c, x)))), Plus(n, C1))), x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, p), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      LtQ(n, CN1)))));
}
