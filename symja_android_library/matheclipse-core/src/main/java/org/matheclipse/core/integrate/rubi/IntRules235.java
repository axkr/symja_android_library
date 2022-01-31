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
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.Hypergeometric2F1;
import static org.matheclipse.core.expression.F.HypergeometricPFQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.QQ;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
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
import static org.matheclipse.core.expression.S.f;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FracPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules235 {
  public static IAST RULES =
      List(
          IIntegrate(4701,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcSin(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          n_DEFAULT),
                      Power(Times(f_DEFAULT, x_), m_), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))),
                          p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Times(f, x), Plus(m, C1)),
                              Power(Plus(d, Times(e,
                                  Sqr(x))), Plus(p,
                                      C1)),
                              Power(Plus(a,
                                  Times(b, ArcSin(Times(c, x)))), n),
                              Power(Times(d, f, Plus(m, C1)), CN1)),
                          x),
                      Dist(
                          Times(
                              Sqr(c), Plus(m, Times(C2,
                                  p), C3),
                              Power(Times(Sqr(f), Plus(m, C1)), CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Plus(m, C2)),
                                  Power(Plus(d, Times(e,
                                      Sqr(x))), p),
                                  Power(Plus(a, Times(b, ArcSin(Times(c, x)))), n)),
                              x),
                          x),
                      Negate(
                          Dist(Times(b, c, n, Power(d, IntPart(p)),
                              Power(Plus(d, Times(e, Sqr(x))), FracPart(p)),
                              Power(
                                  Times(f, Plus(m, C1),
                                      Power(Subtract(C1, Times(Sqr(c), Sqr(x))), FracPart(p))),
                                  CN1)),
                              Integrate(Times(Power(Times(f, x), Plus(m, C1)),
                                  Power(Subtract(C1, Times(Sqr(c), Sqr(x))), Plus(p, C1D2)),
                                  Power(Plus(a, Times(b, ArcSin(Times(c, x)))), Subtract(n, C1))),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, f, p), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      GtQ(n, C0), LtQ(m, CN1), IntegerQ(m)))),
          IIntegrate(4702,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCos(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          n_DEFAULT),
                      Power(Times(f_DEFAULT,
                          x_), m_),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Times(f, x), Plus(m, C1)),
                              Power(Plus(d, Times(e,
                                  Sqr(x))), Plus(p,
                                      C1)),
                              Power(Plus(a,
                                  Times(b, ArcCos(Times(c, x)))), n),
                              Power(Times(d, f, Plus(m, C1)), CN1)),
                          x),
                      Dist(
                          Times(
                              Sqr(c), Plus(m, Times(C2,
                                  p), C3),
                              Power(Times(Sqr(f), Plus(m, C1)), CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Plus(m, C2)),
                                  Power(Plus(d, Times(e, Sqr(x))), p), Power(
                                      Plus(a, Times(b, ArcCos(Times(c, x)))), n)),
                              x),
                          x),
                      Dist(
                          Times(b, c, n, Power(d, IntPart(p)),
                              Power(Plus(d, Times(e, Sqr(x))), FracPart(p)),
                              Power(
                                  Times(f, Plus(m, C1),
                                      Power(Subtract(C1, Times(Sqr(c), Sqr(x))), FracPart(p))),
                                  CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Plus(m, C1)),
                                  Power(Subtract(C1, Times(Sqr(c), Sqr(x))), Plus(p, C1D2)),
                                  Power(Plus(a, Times(b, ArcCos(Times(c, x)))), Subtract(n, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, p), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      GtQ(n, C0), LtQ(m, CN1), IntegerQ(m)))),
          IIntegrate(4703,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcSin(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          n_DEFAULT),
                      Power(Times(f_DEFAULT,
                          x_), m_),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(f, Power(Times(f, x), Subtract(m, C1)),
                              Power(Plus(d, Times(e, Sqr(x))), Plus(p, C1)),
                              Power(Plus(a,
                                  Times(b, ArcSin(Times(c, x)))), n),
                              Power(Times(C2, e, Plus(p, C1)), CN1)),
                          x),
                      Negate(Dist(
                          Times(Sqr(f), Subtract(m, C1), Power(Times(C2, e, Plus(p, C1)), CN1)),
                          Integrate(Times(Power(Times(f, x), Subtract(m, C2)),
                              Power(Plus(d, Times(e, Sqr(x))), Plus(p, C1)),
                              Power(Plus(a, Times(b, ArcSin(Times(c, x)))), n)), x),
                          x)),
                      Dist(
                          Times(b, f, n, Power(d, IntPart(p)),
                              Power(Plus(d, Times(e, Sqr(x))), FracPart(p)),
                              Power(
                                  Times(C2, c, Plus(p, C1),
                                      Power(Subtract(C1, Times(Sqr(c), Sqr(x))), FracPart(p))),
                                  CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Subtract(m, C1)),
                                  Power(Subtract(C1, Times(Sqr(c), Sqr(x))), Plus(p, C1D2)),
                                  Power(Plus(a, Times(b, ArcSin(Times(c, x)))), Subtract(n, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      GtQ(n, C0), LtQ(p, CN1), GtQ(m, C1)))),
          IIntegrate(4704,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCos(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          n_DEFAULT),
                      Power(Times(f_DEFAULT,
                          x_), m_),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Plus(Simp(Times(f, Power(Times(f, x), Subtract(m, C1)), Power(Plus(d,
                      Times(e, Sqr(x))), Plus(p, C1)), Power(Plus(a, Times(b, ArcCos(Times(c, x)))),
                          n),
                      Power(Times(C2, e, Plus(p, C1)), CN1)), x),
                      Negate(
                          Dist(
                              Times(Sqr(f), Subtract(m, C1), Power(Times(C2, e, Plus(p, C1)), CN1)),
                              Integrate(
                                  Times(Power(Times(f, x), Subtract(m, C2)),
                                      Power(Plus(d, Times(e, Sqr(x))), Plus(p, C1)), Power(Plus(a,
                                          Times(b, ArcCos(Times(c, x)))), n)),
                                  x),
                              x)),
                      Negate(Dist(
                          Times(b, f, n, Power(d, IntPart(p)),
                              Power(Plus(d, Times(e, Sqr(x))), FracPart(p)),
                              Power(Times(C2, c, Plus(p, C1),
                                  Power(Subtract(C1, Times(Sqr(c), Sqr(x))), FracPart(p))), CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Subtract(m, C1)),
                                  Power(Subtract(C1, Times(Sqr(c), Sqr(x))), Plus(p, C1D2)),
                                  Power(Plus(a, Times(b, ArcCos(Times(c, x)))), Subtract(n, C1))),
                              x),
                          x))),
                  And(FreeQ(List(a, b, c, d, e, f), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      GtQ(n, C0), LtQ(p, CN1), GtQ(m, C1)))),
          IIntegrate(4705,
              Integrate(Times(
                  Power(Plus(a_DEFAULT, Times(ArcSin(Times(c_DEFAULT, x_)),
                      b_DEFAULT)), n_DEFAULT),
                  Power(Times(f_DEFAULT, x_), m_), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(Power(Times(f, x), Plus(m, C1)),
                                  Power(Plus(d, Times(e, Sqr(x))), Plus(p, C1)),
                                  Power(Plus(a, Times(b, ArcSin(Times(c, x)))),
                                      n),
                                  Power(Times(C2, d, f, Plus(p, C1)), CN1)),
                              x)),
                      Dist(
                          Times(Plus(m, Times(C2,
                              p), C3), Power(Times(C2, d, Plus(p, C1)),
                                  CN1)),
                          Integrate(
                              Times(Power(Times(f, x), m),
                                  Power(Plus(d, Times(e, Sqr(x))), Plus(p, C1)), Power(
                                      Plus(a, Times(b, ArcSin(Times(c, x)))), n)),
                              x),
                          x),
                      Dist(
                          Times(b, c, n, Power(d, IntPart(p)),
                              Power(Plus(d, Times(e, Sqr(x))), FracPart(p)),
                              Power(
                                  Times(C2, f, Plus(p, C1),
                                      Power(Subtract(C1, Times(Sqr(c), Sqr(x))), FracPart(p))),
                                  CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Plus(m, C1)),
                                  Power(Subtract(C1, Times(Sqr(c), Sqr(x))), Plus(p,
                                      C1D2)),
                                  Power(Plus(a, Times(b, ArcSin(Times(c, x)))), Subtract(n, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, m), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      GtQ(n, C0), LtQ(p, CN1), Not(
                          GtQ(m, C1)),
                      Or(IntegerQ(m), IntegerQ(p), EqQ(n, C1))))),
          IIntegrate(4706,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(ArcCos(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          n_DEFAULT),
                      Power(Times(f_DEFAULT, x_), m_),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(Power(Times(f, x), Plus(m, C1)),
                                  Power(Plus(d, Times(e, Sqr(x))), Plus(p, C1)),
                                  Power(Plus(a,
                                      Times(b, ArcCos(Times(c, x)))), n),
                                  Power(Times(C2, d, f, Plus(p, C1)), CN1)),
                              x)),
                      Dist(
                          Times(Plus(m, Times(C2,
                              p), C3), Power(Times(C2, d, Plus(p, C1)),
                                  CN1)),
                          Integrate(
                              Times(Power(Times(f, x), m),
                                  Power(Plus(d, Times(e, Sqr(x))), Plus(p,
                                      C1)),
                                  Power(Plus(a, Times(b, ArcCos(Times(c, x)))), n)),
                              x),
                          x),
                      Negate(
                          Dist(
                              Times(b, c, n, Power(d, IntPart(p)),
                                  Power(Plus(d, Times(e, Sqr(x))), FracPart(p)),
                                  Power(
                                      Times(C2, f, Plus(p, C1),
                                          Power(Subtract(C1, Times(Sqr(c), Sqr(x))), FracPart(p))),
                                      CN1)),
                              Integrate(Times(Power(Times(f, x), Plus(m, C1)),
                                  Power(Subtract(C1, Times(Sqr(c), Sqr(x))), Plus(p, C1D2)),
                                  Power(Plus(a, Times(b, ArcCos(Times(c, x)))), Subtract(n, C1))),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, f, m), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      GtQ(n, C0), LtQ(p, CN1), Not(GtQ(m, C1)), Or(IntegerQ(m), IntegerQ(p),
                          EqQ(n, C1))))),
          IIntegrate(4707,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcSin(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          n_DEFAULT),
                      Power(Times(f_DEFAULT,
                          x_), m_),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(f, Power(Times(f, x), Subtract(m, C1)),
                              Sqrt(Plus(d, Times(e, Sqr(x)))),
                              Power(Plus(a,
                                  Times(b, ArcSin(Times(c, x)))), n),
                              Power(Times(e, m), CN1)),
                          x),
                      Dist(
                          Times(Sqr(f), Subtract(m, C1), Power(Times(Sqr(c), m),
                              CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Subtract(m, C2)),
                                  Power(Plus(a,
                                      Times(b, ArcSin(Times(c, x)))), n),
                                  Power(Plus(d, Times(e, Sqr(x))), CN1D2)),
                              x),
                          x),
                      Dist(
                          Times(
                              b, f, n, Sqrt(Subtract(C1, Times(Sqr(c), Sqr(x)))), Power(
                                  Times(c, m, Sqrt(Plus(d, Times(e, Sqr(x))))), CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Subtract(m, C1)),
                                  Power(Plus(a, Times(b, ArcSin(Times(c, x)))), Subtract(n, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), EqQ(Plus(Times(Sqr(c), d),
                      e), C0), GtQ(n,
                          C0),
                      GtQ(m, C1), IntegerQ(m)))),
          IIntegrate(4708,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCos(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          n_DEFAULT),
                      Power(Times(f_DEFAULT,
                          x_), m_),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(f, Power(Times(f, x), Subtract(m, C1)),
                              Sqrt(Plus(d, Times(e, Sqr(x)))),
                              Power(Plus(a,
                                  Times(b, ArcCos(Times(c, x)))), n),
                              Power(Times(e, m), CN1)),
                          x),
                      Dist(
                          Times(Sqr(f), Subtract(m, C1), Power(Times(Sqr(c), m),
                              CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Subtract(m, C2)),
                                  Power(Plus(a, Times(b, ArcCos(Times(c, x)))),
                                      n),
                                  Power(Plus(d, Times(e, Sqr(x))), CN1D2)),
                              x),
                          x),
                      Negate(
                          Dist(
                              Times(b, f, n, Sqrt(Subtract(C1, Times(Sqr(c), Sqr(x)))), Power(
                                  Times(c, m, Sqrt(Plus(d, Times(e, Sqr(x))))), CN1)),
                              Integrate(
                                  Times(Power(Times(f, x), Subtract(m, C1)),
                                      Power(Plus(a, Times(b, ArcCos(Times(c, x)))),
                                          Subtract(n, C1))),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, f), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      GtQ(n, C0), GtQ(m, C1), IntegerQ(m)))),
          IIntegrate(4709,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcSin(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          n_DEFAULT),
                      Power(x_, m_), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Dist(Power(Times(Power(c, Plus(m, C1)), Sqrt(d)), CN1),
                      Subst(
                          Integrate(Times(Power(Plus(a, Times(b, x)), n), Power(Sin(x), m)),
                              x),
                          x, ArcSin(Times(c, x))),
                      x),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d), e), C0), GtQ(d, C0),
                      IGtQ(n, C0), IntegerQ(m)))),
          IIntegrate(4710,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCos(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          n_DEFAULT),
                      Power(x_, m_), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(
                          Power(Times(Power(c, Plus(m, C1)),
                              Sqrt(d)), CN1),
                          Subst(
                              Integrate(Times(Power(Plus(a, Times(b, x)), n), Power(Cos(x), m)), x),
                              x, ArcCos(Times(c, x))),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d), e), C0), GtQ(d, C0),
                      IGtQ(n, C0), IntegerQ(m)))),
          IIntegrate(4711,
              Integrate(Times(Plus(a_DEFAULT, Times(ArcSin(Times(c_DEFAULT, x_)), b_DEFAULT)),
                  Power(Times(f_DEFAULT, x_), m_), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))),
                      CN1D2)),
                  x_Symbol),
              Condition(
                  Subtract(Simp(
                      Times(Power(Times(f, x), Plus(m, C1)), Plus(a, Times(b, ArcSin(Times(c, x)))),
                          Hypergeometric2F1(C1D2, Times(C1D2, Plus(C1, m)),
                              Times(C1D2, Plus(C3, m)), Times(Sqr(c), Sqr(x))),
                          Power(Times(Sqrt(d), f, Plus(m, C1)), CN1)),
                      x),
                      Simp(
                          Times(
                              b, c, Power(Times(f, x), Plus(m,
                                  C2)),
                              HypergeometricPFQ(
                                  List(C1, Plus(C1, Times(C1D2, m)), Plus(C1, Times(C1D2, m))),
                                  List(Plus(QQ(3L, 2L), Times(C1D2, m)), Plus(C2,
                                      Times(C1D2, m))),
                                  Times(Sqr(c), Sqr(x))),
                              Power(Times(Sqrt(d), Sqr(f), Plus(m, C1), Plus(m, C2)), CN1)),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, m), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      GtQ(d, C0), Not(IntegerQ(m))))),
          IIntegrate(4712,
              Integrate(
                  Times(Plus(a_DEFAULT, Times(ArcCos(Times(c_DEFAULT, x_)), b_DEFAULT)), Power(
                      Times(f_DEFAULT, x_), m_), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Plus(Simp(
                      Times(Power(Times(f, x), Plus(m, C1)), Plus(a, Times(b, ArcCos(Times(c, x)))),
                          Hypergeometric2F1(C1D2, Times(C1D2, Plus(C1, m)),
                              Times(C1D2, Plus(C3, m)), Times(Sqr(c), Sqr(x))),
                          Power(Times(Sqrt(d), f, Plus(m, C1)), CN1)),
                      x),
                      Simp(
                          Times(
                              b, c, Power(Times(f, x), Plus(m,
                                  C2)),
                              HypergeometricPFQ(
                                  List(C1, Plus(C1, Times(C1D2, m)), Plus(C1, Times(C1D2, m))),
                                  List(Plus(QQ(3L, 2L), Times(C1D2, m)), Plus(C2,
                                      Times(C1D2, m))),
                                  Times(Sqr(c), Sqr(x))),
                              Power(Times(Sqrt(d), Sqr(f), Plus(m, C1), Plus(m, C2)), CN1)),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, m), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      GtQ(d, C0), Not(IntegerQ(m))))),
          IIntegrate(4713,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcSin(Times(c_DEFAULT, x_)), b_DEFAULT)), n_DEFAULT),
                      Power(Times(f_DEFAULT,
                          x_), m_),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Sqrt(Subtract(C1, Times(Sqr(c), Sqr(x)))), Power(
                              Plus(d, Times(e, Sqr(x))), CN1D2)),
                      Integrate(
                          Times(Power(Times(f, x), m),
                              Power(Plus(a, Times(b, ArcSin(Times(c, x)))), n),
                              Power(Subtract(C1, Times(Sqr(c), Sqr(x))), CN1D2)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m), x), EqQ(Plus(Times(Sqr(c),
                      d), e), C0), GtQ(n, C0), Not(GtQ(d,
                          C0)),
                      Or(IntegerQ(m), EqQ(n, C1))))),
          IIntegrate(4714,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcCos(Times(c_DEFAULT, x_)), b_DEFAULT)), n_DEFAULT),
                      Power(Times(f_DEFAULT,
                          x_), m_),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Sqrt(Subtract(C1, Times(Sqr(c), Sqr(x)))), Power(
                              Plus(d, Times(e, Sqr(x))), CN1D2)),
                      Integrate(
                          Times(Power(Times(f, x), m),
                              Power(Plus(a, Times(b, ArcCos(Times(c, x)))), n), Power(
                                  Subtract(C1, Times(Sqr(c), Sqr(x))), CN1D2)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      GtQ(n, C0), Not(GtQ(d, C0)), Or(IntegerQ(m), EqQ(n, C1))))),
          IIntegrate(4715,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcSin(Times(c_DEFAULT, x_)), b_DEFAULT)), n_DEFAULT),
                      Power(Times(f_DEFAULT,
                          x_), m_),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(f, Power(Times(f, x), Subtract(m, C1)),
                              Power(Plus(d, Times(e,
                                  Sqr(x))), Plus(p,
                                      C1)),
                              Power(Plus(a,
                                  Times(b, ArcSin(Times(c, x)))), n),
                              Power(Times(e, Plus(m, Times(C2, p), C1)), CN1)),
                          x),
                      Dist(
                          Times(
                              Sqr(f), Subtract(m, C1), Power(
                                  Times(Sqr(c), Plus(m, Times(C2, p), C1)), CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Subtract(m, C2)),
                                  Power(Plus(d, Times(e, Sqr(x))), p), Power(
                                      Plus(a, Times(b, ArcSin(Times(c, x)))), n)),
                              x),
                          x),
                      Dist(
                          Times(b, f, n, Power(d, IntPart(p)),
                              Power(Plus(d, Times(e, Sqr(x))), FracPart(p)),
                              Power(
                                  Times(c, Plus(m, Times(C2, p), C1),
                                      Power(Subtract(C1, Times(Sqr(c), Sqr(x))), FracPart(p))),
                                  CN1)),
                          Integrate(Times(Power(Times(f, x), Subtract(m, C1)),
                              Power(Subtract(C1, Times(Sqr(c), Sqr(x))), Plus(p, C1D2)),
                              Power(Plus(a, Times(b, ArcSin(Times(c, x)))), Subtract(n, C1))), x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, p), x), EqQ(Plus(Times(Sqr(c), d), e),
                      C0), GtQ(n, C0), GtQ(m, C1), NeQ(Plus(m, Times(C2, p), C1), C0),
                      IntegerQ(m)))),
          IIntegrate(4716,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCos(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          n_DEFAULT),
                      Power(Times(f_DEFAULT, x_), m_),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(f, Power(Times(f, x), Subtract(m, C1)),
                              Power(Plus(d, Times(e,
                                  Sqr(x))), Plus(p,
                                      C1)),
                              Power(Plus(a, Times(b, ArcCos(Times(c, x)))), n),
                              Power(Times(e, Plus(m, Times(C2, p), C1)), CN1)),
                          x),
                      Dist(
                          Times(
                              Sqr(f), Subtract(m, C1), Power(
                                  Times(Sqr(c), Plus(m, Times(C2, p), C1)), CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Subtract(m, C2)),
                                  Power(Plus(d, Times(e,
                                      Sqr(x))), p),
                                  Power(Plus(a, Times(b, ArcCos(Times(c, x)))), n)),
                              x),
                          x),
                      Negate(
                          Dist(
                              Times(b, f, n, Power(d, IntPart(p)),
                                  Power(Plus(d, Times(e, Sqr(x))), FracPart(p)),
                                  Power(
                                      Times(c, Plus(m, Times(C2, p), C1),
                                          Power(Subtract(C1, Times(Sqr(c), Sqr(x))), FracPart(p))),
                                      CN1)),
                              Integrate(
                                  Times(Power(Times(f, x), Subtract(m, C1)),
                                      Power(Subtract(C1, Times(Sqr(c), Sqr(x))), Plus(p, C1D2)),
                                      Power(Plus(a, Times(b, ArcCos(Times(c, x)))),
                                          Subtract(n, C1))),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, f, p), x), EqQ(Plus(Times(Sqr(c),
                      d), e), C0), GtQ(n, C0), GtQ(m, C1), NeQ(Plus(m, Times(C2, p),
                          C1), C0),
                      IntegerQ(m)))),
          IIntegrate(4717,
              Integrate(
                  Times(Power(Plus(a_DEFAULT, Times(ArcSin(Times(c_DEFAULT, x_)), b_DEFAULT)), n_),
                      Power(Times(f_DEFAULT,
                          x_), m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Times(f, x), m), Sqrt(Subtract(C1, Times(Sqr(c), Sqr(x)))),
                              Power(Plus(d, Times(e, Sqr(x))), p),
                              Power(Plus(a, Times(b, ArcSin(Times(c, x)))), Plus(n,
                                  C1)),
                              Power(Times(b, c, Plus(n, C1)), CN1)),
                          x),
                      Dist(
                          Times(f, m, Power(d, IntPart(p)),
                              Power(Plus(d, Times(e, Sqr(x))), FracPart(p)),
                              Power(
                                  Times(b, c, Plus(n, C1),
                                      Power(Subtract(C1, Times(Sqr(c), Sqr(x))), FracPart(p))),
                                  CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Subtract(m, C1)),
                                  Power(Subtract(C1, Times(Sqr(c), Sqr(x))), Subtract(p, C1D2)),
                                  Power(Plus(a, Times(b, ArcSin(Times(c, x)))), Plus(n, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, m, p), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      LtQ(n, CN1), EqQ(Plus(m, Times(C2, p), C1), C0)))),
          IIntegrate(4718,
              Integrate(
                  Times(Power(Plus(a_DEFAULT, Times(ArcCos(Times(c_DEFAULT, x_)), b_DEFAULT)), n_),
                      Power(Times(f_DEFAULT,
                          x_), m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(Power(Times(f, x), m),
                                  Sqrt(Subtract(C1, Times(Sqr(c), Sqr(x)))),
                                  Power(Plus(d, Times(e, Sqr(x))), p),
                                  Power(Plus(a, Times(b, ArcCos(Times(c, x)))), Plus(n, C1)), Power(
                                      Times(b, c, Plus(n, C1)), CN1)),
                              x)),
                      Dist(
                          Times(f, m, Power(d, IntPart(p)),
                              Power(Plus(d, Times(e, Sqr(x))), FracPart(p)),
                              Power(
                                  Times(b, c, Plus(n, C1),
                                      Power(Subtract(C1, Times(Sqr(c), Sqr(x))), FracPart(p))),
                                  CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Subtract(m, C1)),
                                  Power(Subtract(C1, Times(Sqr(c), Sqr(x))), Subtract(p, C1D2)),
                                  Power(Plus(a, Times(b, ArcCos(Times(c, x)))), Plus(n, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, m, p), x), EqQ(Plus(Times(Sqr(c), d),
                      e), C0), LtQ(n,
                          CN1),
                      EqQ(Plus(m, Times(C2, p), C1), C0)))),
          IIntegrate(4719,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(ArcSin(Times(c_DEFAULT, x_)),
                          b_DEFAULT)), n_),
                      Power(Times(f_DEFAULT,
                          x_), m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(Times(Power(Times(f, x), m),
                          Power(Plus(a, Times(b, ArcSin(Times(c, x)))), Plus(n, C1)), Power(
                              Times(b, c, Sqrt(d), Plus(n, C1)), CN1)),
                          x),
                      Dist(Times(f, m, Power(Times(b, c, Sqrt(d), Plus(n, C1)), CN1)),
                          Integrate(
                              Times(
                                  Power(Times(f, x), Subtract(m, C1)), Power(Plus(a,
                                      Times(b, ArcSin(Times(c, x)))), Plus(n, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, m), x), EqQ(Plus(Times(Sqr(c), d),
                      e), C0), LtQ(n,
                          CN1),
                      GtQ(d, C0)))),
          IIntegrate(4720,
              Integrate(
                  Times(Power(Plus(a_DEFAULT, Times(ArcCos(Times(c_DEFAULT, x_)), b_DEFAULT)), n_),
                      Power(
                          Times(f_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(Simp(Times(Power(Times(f, x), m),
                          Power(Plus(a, Times(b, ArcCos(Times(c, x)))), Plus(n, C1)),
                          Power(Times(b, c, Sqrt(d), Plus(n, C1)), CN1)), x)),
                      Dist(Times(f, m, Power(Times(b, c, Sqrt(d), Plus(n, C1)), CN1)),
                          Integrate(Times(Power(Times(f, x), Subtract(m, C1)),
                              Power(Plus(a, Times(b, ArcCos(Times(c, x)))), Plus(n, C1))), x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, m), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      LtQ(n, CN1), GtQ(d, C0)))));
}
