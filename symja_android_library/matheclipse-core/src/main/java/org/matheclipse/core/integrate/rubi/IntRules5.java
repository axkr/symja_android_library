package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.ArcSin;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D4;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.CN1D4;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.CN4;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.EllipticE;
import static org.matheclipse.core.expression.F.EllipticF;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.QQ;
import static org.matheclipse.core.expression.F.Simplify;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
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
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntegersQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NegQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PosQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RationalQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Rt;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SimplerQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SumSimplerQ;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules5 {
  public static IAST RULES = List(
      IIntegrate(101,
          Integrate(Times(Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), m_DEFAULT),
              Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), n_DEFAULT), Power(
                  Plus(e_DEFAULT, Times(f_DEFAULT, x_)), p_DEFAULT)),
              x_Symbol),
          Condition(
              Subtract(
                  Simp(
                      Times(Power(Plus(a, Times(b, x)), m), Power(Plus(c, Times(d, x)), n),
                          Power(Plus(e, Times(f,
                              x)), Plus(p,
                                  C1)),
                          Power(Times(f, Plus(m, n, p, C1)), CN1)),
                      x),
                  Dist(Power(Times(f, Plus(m, n, p, C1)), CN1),
                      Integrate(
                          Times(Power(Plus(a, Times(b, x)), Subtract(m, C1)),
                              Power(Plus(c, Times(d, x)), Subtract(n, C1)),
                              Power(Plus(e, Times(f, x)), p),
                              Simp(
                                  Plus(Times(c, m, Subtract(Times(b, e), Times(a, f))),
                                      Times(a, n, Subtract(Times(d, e), Times(c, f))),
                                      Times(Plus(Times(d, m, Subtract(Times(b, e), Times(a, f))),
                                          Times(b, n, Subtract(Times(d, e), Times(c, f)))), x)),
                                  x)),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, d, e, f, p), x), GtQ(m, C0), GtQ(n, C0),
                  NeQ(Plus(m, n, p, C1), C0),
                  Or(IntegersQ(Times(C2, m), Times(C2, n), Times(C2, p)),
                      Or(IntegersQ(m, Plus(n, p)), IntegersQ(p, Plus(m, n))))))),
      IIntegrate(102,
          Integrate(
              Times(Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), m_),
                  Power(Plus(c_DEFAULT,
                      Times(d_DEFAULT, x_)), n_DEFAULT),
                  Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), p_DEFAULT)),
              x_Symbol),
          Condition(
              Plus(
                  Simp(
                      Times(b, Power(Plus(a, Times(b, x)), Subtract(m, C1)),
                          Power(Plus(c, Times(d, x)), Plus(n, C1)),
                          Power(Plus(e, Times(f,
                              x)), Plus(p,
                                  C1)),
                          Power(Times(d, f, Plus(m, n, p, C1)), CN1)),
                      x),
                  Dist(Power(Times(d, f, Plus(m, n, p, C1)), CN1),
                      Integrate(
                          Times(
                              Power(Plus(a, Times(b, x)),
                                  Subtract(m, C2)),
                              Power(Plus(c, Times(d, x)), n), Power(Plus(e, Times(f, x)), p), Simp(
                                  Plus(Times(Sqr(a), d, f, Plus(m, n, p, C1)), Times(CN1, b,
                                      Plus(Times(b, c, e, Subtract(m, C1)),
                                          Times(a,
                                              Plus(Times(d, e, Plus(n, C1)),
                                                  Times(c, f, Plus(p, C1)))))),
                                      Times(b,
                                          Subtract(Times(a, d, f, Plus(Times(C2, m), n, p)),
                                              Times(b,
                                                  Plus(Times(d, e, Plus(m, n)),
                                                      Times(c, f, Plus(m, p))))),
                                          x)),
                                  x)),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, d, e, f, n, p), x), GtQ(m, C1), NeQ(Plus(m, n, p,
                  C1), C0), IntegersQ(Times(C2, m), Times(C2, n),
                      Times(C2, p))))),
      IIntegrate(103,
          Integrate(
              Times(Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), m_),
                  Power(Plus(c_DEFAULT,
                      Times(d_DEFAULT, x_)), n_DEFAULT),
                  Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), p_DEFAULT)),
              x_Symbol),
          Condition(
              Plus(
                  Simp(
                      Times(b, Power(Plus(a, Times(b, x)), Plus(m, C1)),
                          Power(Plus(c, Times(d, x)), Plus(n, C1)),
                          Power(Plus(e, Times(f,
                              x)), Plus(p,
                                  C1)),
                          Power(
                              Times(
                                  Plus(m, C1), Subtract(Times(b, c), Times(a,
                                      d)),
                                  Subtract(Times(b, e), Times(a, f))),
                              CN1)),
                      x),
                  Dist(
                      Power(
                          Times(
                              Plus(m, C1), Subtract(Times(b, c), Times(a,
                                  d)),
                              Subtract(Times(b, e), Times(a, f))),
                          CN1),
                      Integrate(
                          Times(Power(Plus(a, Times(b, x)), Plus(m, C1)),
                              Power(Plus(c, Times(d, x)), n), Power(Plus(e, Times(f, x)), p),
                              Simp(Subtract(Subtract(Times(a, d, f, Plus(m, C1)),
                                  Times(b,
                                      Plus(Times(d, e, Plus(m, n, C2)),
                                          Times(c, f, Plus(m, p, C2))))),
                                  Times(b, d, f, Plus(m, n, p, C3), x)), x)),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, d, e, f, n,
                  p), x), LtQ(m, CN1), IntegerQ(
                      m),
                  Or(IntegerQ(n), IntegersQ(Times(C2, n), Times(C2, p)))))),
      IIntegrate(104,
          Integrate(
              Times(Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), m_),
                  Power(Plus(c_DEFAULT,
                      Times(d_DEFAULT, x_)), n_DEFAULT),
                  Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), p_DEFAULT)),
              x_Symbol),
          Condition(
              Plus(
                  Simp(
                      Times(b, Power(Plus(a, Times(b, x)), Plus(m, C1)),
                          Power(Plus(c, Times(d, x)), Plus(n, C1)),
                          Power(Plus(e, Times(f,
                              x)), Plus(p,
                                  C1)),
                          Power(
                              Times(
                                  Plus(m, C1), Subtract(Times(b, c), Times(a,
                                      d)),
                                  Subtract(Times(b, e), Times(a, f))),
                              CN1)),
                      x),
                  Dist(
                      Power(
                          Times(
                              Plus(m, C1), Subtract(Times(b, c), Times(a,
                                  d)),
                              Subtract(Times(b, e), Times(a, f))),
                          CN1),
                      Integrate(Times(Power(Plus(a, Times(b, x)), Plus(m, C1)),
                          Power(Plus(c, Times(d, x)), n), Power(Plus(e, Times(f, x)), p),
                          Simp(Subtract(
                              Subtract(Times(a, d, f, Plus(m, C1)),
                                  Times(b,
                                      Plus(Times(d, e, Plus(m, n, C2)),
                                          Times(c, f, Plus(m, p, C2))))),
                              Times(b, d, f, Plus(m, n, p, C3), x)), x)),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, d, e, f, n,
                  p), x), LtQ(m,
                      CN1),
                  IntegersQ(Times(C2, m), Times(C2, n), Times(C2, p))))),
      IIntegrate(105,
          Integrate(Times(Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), m_),
              Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), n_),
              Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), CN1)), x_Symbol),
          Condition(
              Subtract(
                  Dist(Times(b, Power(f, CN1)),
                      Integrate(Times(Power(Plus(a, Times(b, x)), Subtract(m, C1)),
                          Power(Plus(c, Times(d, x)), n)), x),
                      x),
                  Dist(
                      Times(Subtract(Times(b, e), Times(a, f)), Power(f, CN1)), Integrate(
                          Times(
                              Power(Plus(a, Times(b, x)), Subtract(m, C1)),
                              Power(Plus(c, Times(d, x)), n), Power(Plus(e, Times(f, x)), CN1)),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, d, e, f, m, n), x), IGtQ(Simplify(
                  Plus(m, n, C1)), C0), Or(
                      GtQ(m, C0), And(Not(RationalQ(
                          m)), Or(SumSimplerQ(m, CN1),
                              Not(SumSimplerQ(n, CN1)))))))),
      IIntegrate(106,
          Integrate(Times(Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), CN1),
              Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), CN1D2), Power(
                  Plus(e_DEFAULT, Times(f_DEFAULT, x_)), CN1D4)),
              x_Symbol),
          Condition(Dist(CN4,
              Subst(Integrate(Times(Sqr(x),
                  Power(Times(Subtract(Subtract(Times(b, e), Times(a, f)), Times(b, Power(x, C4))),
                      Sqrt(Plus(c, Times(CN1, d, e, Power(f, CN1)),
                          Times(d, Power(x, C4), Power(f, CN1))))),
                      CN1)),
                  x), x, Power(Plus(e, Times(f, x)), C1D4)),
              x),
              And(FreeQ(List(a, b, c, d, e,
                  f), x), GtQ(Times(CN1, f, Power(Subtract(Times(d, e), Times(c, f)), CN1)),
                      C0)))),
      IIntegrate(107,
          Integrate(
              Times(Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), CN1),
                  Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), CN1D2), Power(
                      Plus(e_DEFAULT, Times(f_DEFAULT, x_)), CN1D4)),
              x_Symbol),
          Condition(
              Dist(
                  Times(
                      Sqrt(
                          Times(
                              CN1, f, Plus(c, Times(d,
                                  x)),
                              Power(Subtract(Times(d, e), Times(c, f)), CN1))),
                      Power(Plus(c, Times(d, x)), CN1D2)),
                  Integrate(
                      Power(
                          Times(Plus(a, Times(b, x)),
                              Sqrt(Subtract(Times(CN1, c, f,
                                  Power(Subtract(Times(d, e), Times(c, f)), CN1)),
                                  Times(d, f, x, Power(Subtract(Times(d, e), Times(c, f)), CN1)))),
                              Power(Plus(e, Times(f, x)), C1D4)),
                          CN1),
                      x),
                  x),
              And(FreeQ(List(a, b, c, d, e, f), x),
                  Not(GtQ(Times(CN1, f, Power(Subtract(Times(d, e), Times(c, f)), CN1)), C0))))),
      IIntegrate(108,
          Integrate(
              Times(Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), CN1),
                  Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), CN1D2), Power(
                      Plus(e_DEFAULT, Times(f_DEFAULT, x_)), QQ(-3L, 4L))),
              x_Symbol),
          Condition(
              Dist(CN4,
                  Subst(Integrate(Power(
                      Times(Subtract(Subtract(Times(b, e), Times(a, f)), Times(b, Power(x, C4))),
                          Sqrt(Plus(c, Times(CN1, d, e, Power(f, CN1)),
                              Times(d, Power(x, C4), Power(f, CN1))))),
                      CN1), x), x, Power(Plus(e, Times(f, x)), C1D4)),
                  x),
              And(FreeQ(List(a, b, c, d, e, f), x), GtQ(
                  Times(CN1, f, Power(Subtract(Times(d, e), Times(c, f)), CN1)), C0)))),
      IIntegrate(109,
          Integrate(
              Times(Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), CN1),
                  Power(Plus(c_DEFAULT,
                      Times(d_DEFAULT, x_)), CN1D2),
                  Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), QQ(-3L, 4L))),
              x_Symbol),
          Condition(
              Dist(
                  Times(
                      Sqrt(
                          Times(
                              CN1, f, Plus(c, Times(d, x)), Power(
                                  Subtract(Times(d, e), Times(c, f)), CN1))),
                      Power(Plus(c, Times(d, x)), CN1D2)),
                  Integrate(
                      Power(Times(Plus(a, Times(b, x)),
                          Sqrt(Subtract(
                              Times(CN1, c, f, Power(Subtract(Times(d, e), Times(c, f)), CN1)),
                              Times(d, f, x, Power(Subtract(Times(d, e), Times(c, f)), CN1)))),
                          Power(Plus(e, Times(f, x)), QQ(3L, 4L))), CN1),
                      x),
                  x),
              And(FreeQ(List(a, b, c, d, e, f), x), Not(GtQ(Times(CN1, f,
                  Power(Subtract(Times(d, e), Times(c, f)), CN1)), C0))))),
      IIntegrate(110,
          Integrate(
              Times(Power(Times(b_DEFAULT, x_), CN1D2),
                  Power(Plus(c_, Times(d_DEFAULT, x_)),
                      CN1D2),
                  Sqrt(Plus(e_, Times(f_DEFAULT, x_)))),
              x_Symbol),
          Condition(
              Simp(
                  Times(C2, Sqrt(e), Rt(Times(CN1, b, Power(d, CN1)), C2),
                      EllipticE(ArcSin(Times(Sqrt(Times(b, x)),
                          Power(Times(Sqrt(c), Rt(Times(CN1, b, Power(d, CN1)), C2)), CN1))), Times(
                              c, f, Power(Times(d, e), CN1))),
                      Power(b, CN1)),
                  x),
              And(FreeQ(List(b, c, d, e, f), x), NeQ(Subtract(Times(d, e), Times(c, f)), C0),
                  GtQ(c, C0), GtQ(e, C0), Not(LtQ(Times(CN1, b, Power(d, CN1)), C0))))),
      IIntegrate(111,
          Integrate(
              Times(Power(Times(b_DEFAULT, x_), CN1D2),
                  Power(Plus(c_,
                      Times(d_DEFAULT, x_)), CN1D2),
                  Sqrt(Plus(e_, Times(f_DEFAULT, x_)))),
              x_Symbol),
          Condition(
              Dist(
                  Times(Sqrt(Times(CN1, b,
                      x)), Power(Times(b, x),
                          CN1D2)),
                  Integrate(
                      Times(Sqrt(Plus(e, Times(f, x))),
                          Power(Times(Sqrt(Times(CN1, b, x)), Sqrt(Plus(c, Times(d, x)))), CN1)),
                      x),
                  x),
              And(FreeQ(List(b, c, d, e, f), x), NeQ(Subtract(Times(d, e),
                  Times(c, f)), C0), GtQ(c,
                      C0),
                  GtQ(e, C0), LtQ(Times(CN1, b, Power(d, CN1)), C0)))),
      IIntegrate(112,
          Integrate(
              Times(Power(Times(b_DEFAULT, x_), CN1D2),
                  Power(Plus(c_,
                      Times(d_DEFAULT, x_)), CN1D2),
                  Sqrt(Plus(e_, Times(f_DEFAULT, x_)))),
              x_Symbol),
          Condition(
              Dist(
                  Times(
                      Sqrt(Plus(e, Times(f, x))), Sqrt(Plus(C1,
                          Times(d, x, Power(c, CN1)))),
                      Power(
                          Times(
                              Sqrt(Plus(c, Times(d, x))), Sqrt(
                                  Plus(C1, Times(f, x, Power(e, CN1))))),
                          CN1)),
                  Integrate(Times(Sqrt(Plus(C1, Times(f, x, Power(e, CN1)))),
                      Power(Times(Sqrt(Times(b, x)), Sqrt(Plus(C1, Times(d, x, Power(c, CN1))))),
                          CN1)),
                      x),
                  x),
              And(FreeQ(List(b, c, d, e, f), x), NeQ(Subtract(Times(d, e), Times(c, f)), C0),
                  Not(And(GtQ(c, C0), GtQ(e, C0)))))),
      IIntegrate(113,
          Integrate(
              Times(Power(Plus(a_, Times(b_DEFAULT, x_)), CN1D2),
                  Power(Plus(c_,
                      Times(d_DEFAULT, x_)), CN1D2),
                  Sqrt(Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
              x_Symbol),
          Condition(Simp(Times(C2,
              Rt(Times(CN1, Subtract(Times(b, e), Times(a, f)), Power(d, CN1)), C2),
              EllipticE(
                  ArcSin(Times(Sqrt(Plus(a, Times(b, x))),
                      Power(Rt(Times(CN1, Subtract(Times(b, c), Times(a, d)), Power(d, CN1)), C2),
                          CN1))),
                  Times(f, Subtract(Times(b, c), Times(a, d)),
                      Power(Times(d, Subtract(Times(b, e), Times(a, f))), CN1))),
              Power(b, CN1)), x),
              And(FreeQ(List(a, b, c, d, e, f), x),
                  GtQ(Times(b, Power(Subtract(Times(b, c), Times(a, d)),
                      CN1)), C0),
                  GtQ(Times(b, Power(Subtract(Times(b, e), Times(a, f)),
                      CN1)), C0),
                  Not(LtQ(Times(CN1, Subtract(Times(b, c), Times(a,
                      d)), Power(d,
                          CN1)),
                      C0)),
                  Not(And(
                      SimplerQ(Plus(c, Times(d,
                          x)), Plus(a,
                              Times(b, x))),
                      GtQ(Times(CN1, d, Power(Subtract(Times(b, c), Times(a, d)), CN1)), C0),
                      GtQ(Times(d,
                          Power(Subtract(Times(d, e), Times(c, f)), CN1)), C0),
                      Not(LtQ(Times(Subtract(Times(b, c), Times(a, d)), Power(b, CN1)), C0))))))),
      IIntegrate(114,
          Integrate(
              Times(
                  Power(Plus(a_, Times(b_DEFAULT,
                      x_)), CN1D2),
                  Power(Plus(c_,
                      Times(d_DEFAULT, x_)), CN1D2),
                  Sqrt(Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
              x_Symbol),
          Condition(
              Dist(
                  Times(
                      Sqrt(Plus(e,
                          Times(f, x))),
                      Sqrt(
                          Times(
                              b, Plus(c, Times(d,
                                  x)),
                              Power(Subtract(Times(b, c), Times(a, d)), CN1))),
                      Power(
                          Times(Sqrt(Plus(c, Times(d, x))),
                              Sqrt(Times(b, Plus(e, Times(f, x)), Power(
                                  Subtract(Times(b, e), Times(a, f)), CN1)))),
                          CN1)),
                  Integrate(
                      Times(
                          Sqrt(
                              Plus(
                                  Times(b, e, Power(Subtract(Times(b, e), Times(a, f)),
                                      CN1)),
                                  Times(b, f, x, Power(Subtract(Times(b, e), Times(a, f)), CN1)))),
                          Power(Times(Sqrt(Plus(a, Times(b, x))),
                              Sqrt(Plus(Times(b, c, Power(Subtract(Times(b, c), Times(a, d)), CN1)),
                                  Times(b, d, x, Power(Subtract(Times(b, c), Times(a, d)), CN1))))),
                              CN1)),
                      x),
                  x),
              And(FreeQ(List(a, b, c, d, e,
                  f), x), Not(
                      And(GtQ(Times(b, Power(Subtract(Times(b, c), Times(a, d)), CN1)), C0),
                          GtQ(Times(b, Power(Subtract(Times(b, e), Times(a, f)), CN1)), C0))),
                  Not(LtQ(Times(CN1, Subtract(Times(b, c), Times(a, d)), Power(d, CN1)), C0))))),
      IIntegrate(115,
          Integrate(
              Times(Power(Times(b_DEFAULT, x_), CN1D2),
                  Power(Plus(c_,
                      Times(d_DEFAULT, x_)), CN1D2),
                  Power(Plus(e_, Times(f_DEFAULT, x_)), CN1D2)),
              x_Symbol),
          Condition(
              Simp(
                  Times(C2, Rt(Times(CN1, b, Power(d, CN1)), C2),
                      EllipticF(
                          ArcSin(Times(Sqrt(Times(b, x)),
                              Power(Times(Sqrt(c), Rt(Times(CN1, b, Power(d, CN1)), C2)), CN1))),
                          Times(c, f, Power(Times(d, e), CN1))),
                      Power(Times(b, Sqrt(e)), CN1)),
                  x),
              And(FreeQ(List(b, c, d, e,
                  f), x), GtQ(c, C0), GtQ(e, C0), Or(
                      GtQ(Times(CN1, b,
                          Power(d, CN1)), C0),
                      LtQ(Times(CN1, b, Power(f, CN1)), C0))))),
      IIntegrate(116,
          Integrate(
              Times(Power(Times(b_DEFAULT, x_), CN1D2),
                  Power(Plus(c_, Times(d_DEFAULT, x_)),
                      CN1D2),
                  Power(Plus(e_, Times(f_DEFAULT, x_)), CN1D2)),
              x_Symbol),
          Condition(
              Simp(
                  Times(
                      C2, Rt(Times(CN1, b,
                          Power(d, CN1)), C2),
                      EllipticF(
                          ArcSin(
                              Times(Sqrt(Times(b, x)),
                                  Power(Times(Sqrt(c), Rt(Times(CN1, b, Power(d, CN1)), C2)),
                                      CN1))),
                          Times(c, f, Power(Times(d, e), CN1))),
                      Power(Times(b, Sqrt(e)), CN1)),
                  x),
              And(FreeQ(List(b, c, d, e,
                  f), x), GtQ(c, C0), GtQ(e, C0), Or(PosQ(Times(CN1, b, Power(d, CN1))),
                      NegQ(Times(CN1, b, Power(f, CN1))))))),
      IIntegrate(117,
          Integrate(Times(Power(Times(b_DEFAULT, x_), CN1D2),
              Power(Plus(c_, Times(d_DEFAULT, x_)), CN1D2),
              Power(Plus(e_, Times(f_DEFAULT, x_)), CN1D2)), x_Symbol),
          Condition(
              Dist(
                  Times(Sqrt(Plus(C1, Times(d, x, Power(c, CN1)))),
                      Sqrt(Plus(C1, Times(f, x, Power(e, CN1)))),
                      Power(Times(Sqrt(Plus(c, Times(d, x))), Sqrt(Plus(e, Times(f, x)))), CN1)),
                  Integrate(
                      Power(Times(Sqrt(Times(b, x)), Sqrt(Plus(C1, Times(d, x, Power(c, CN1)))),
                          Sqrt(Plus(C1, Times(f, x, Power(e, CN1))))), CN1),
                      x),
                  x),
              And(FreeQ(List(b, c, d, e, f), x), Not(And(GtQ(c, C0), GtQ(e, C0)))))),
      IIntegrate(118,
          Integrate(
              Times(Power(Plus(a_, Times(b_DEFAULT, x_)), CN1D2),
                  Power(Plus(c_,
                      Times(d_DEFAULT, x_)), CN1D2),
                  Power(Plus(e_, Times(f_DEFAULT, x_)), CN1D2)),
              x_Symbol),
          Condition(
              Simp(
                  Times(CN2, Sqrt(Times(d, Power(f, CN1))),
                      EllipticF(
                          ArcSin(Times(
                              Rt(Times(CN1, Subtract(Times(b, e), Times(a, f)), Power(f, CN1)), C2),
                              Power(Plus(a, Times(b, x)), CN1D2))),
                          Times(f, Subtract(Times(b, c), Times(a, d)),
                              Power(Times(d, Subtract(Times(b, e), Times(a, f))), CN1))),
                      Power(
                          Times(
                              d, Rt(
                                  Times(CN1, Subtract(Times(b, e), Times(a, f)),
                                      Power(f, CN1)),
                                  C2)),
                          CN1)),
                  x),
              And(FreeQ(List(a, b, c, d, e, f), x), GtQ(Times(d, Power(b, CN1)), C0),
                  GtQ(Times(f, Power(b, CN1)), C0), LeQ(c, Times(a, d,
                      Power(b, CN1))),
                  LeQ(e, Times(a, f, Power(b, CN1)))))),
      IIntegrate(119,
          Integrate(Times(Power(Plus(a_, Times(b_DEFAULT, x_)), CN1D2),
              Power(Plus(c_, Times(d_DEFAULT, x_)), CN1D2), Power(Plus(e_, Times(f_DEFAULT, x_)),
                  CN1D2)),
              x_Symbol),
          Condition(
              Simp(
                  Times(C2, Rt(Times(CN1, b, Power(d, CN1)), C2),
                      EllipticF(
                          ArcSin(Times(Sqrt(Plus(a, Times(b, x))),
                              Power(Times(Rt(Times(CN1, b, Power(d, CN1)), C2),
                                  Sqrt(Times(Subtract(Times(b, c), Times(a, d)), Power(b, CN1)))),
                                  CN1))),
                          Times(f, Subtract(Times(b, c), Times(a, d)),
                              Power(Times(d, Subtract(Times(b, e), Times(a, f))), CN1))),
                      Power(
                          Times(b, Sqrt(Times(Subtract(Times(b, e), Times(a, f)), Power(b, CN1)))),
                          CN1)),
                  x),
              And(FreeQ(List(a, b, c, d, e, f), x),
                  GtQ(Times(Subtract(Times(b, c), Times(a, d)), Power(b, CN1)), C0),
                  GtQ(Times(Subtract(Times(b, e), Times(a, f)), Power(b, CN1)), C0),
                  PosQ(Times(CN1, b, Power(d, CN1))),
                  Not(And(SimplerQ(Plus(c, Times(d, x)), Plus(a, Times(b, x))),
                      GtQ(Times(Subtract(Times(d, e), Times(c, f)), Power(d, CN1)), C0),
                      GtQ(Times(CN1, d, Power(b, CN1)), C0))),
                  Not(And(SimplerQ(Plus(c, Times(d, x)), Plus(a, Times(b, x))),
                      GtQ(Times(Plus(Times(CN1, b, e), Times(a, f)), Power(f, CN1)),
                          C0),
                      GtQ(Times(CN1, f, Power(b, CN1)), C0))),
                  Not(And(SimplerQ(Plus(e, Times(f, x)), Plus(a, Times(b, x))),
                      GtQ(Times(Plus(Times(CN1, d, e), Times(c, f)), Power(f, CN1)), C0),
                      GtQ(Times(Plus(Times(CN1, b, e), Times(a, f)), Power(f, CN1)), C0),
                      Or(PosQ(Times(CN1, f, Power(d, CN1))),
                          PosQ(Times(CN1, f, Power(b, CN1))))))))),
      IIntegrate(120,
          Integrate(Times(Power(Plus(a_, Times(b_DEFAULT, x_)), CN1D2),
              Power(Plus(c_, Times(d_DEFAULT, x_)), CN1D2), Power(Plus(e_, Times(f_DEFAULT, x_)),
                  CN1D2)),
              x_Symbol),
          Condition(
              Simp(
                  Times(C2, Rt(Times(CN1, b, Power(d, CN1)), C2),
                      EllipticF(
                          ArcSin(Times(Sqrt(Plus(a, Times(b, x))),
                              Power(Times(Rt(Times(CN1, b, Power(d, CN1)), C2),
                                  Sqrt(Times(Subtract(Times(b, c), Times(a, d)), Power(b, CN1)))),
                                  CN1))),
                          Times(f, Subtract(Times(b, c), Times(a, d)),
                              Power(Times(d, Subtract(Times(b, e), Times(a, f))), CN1))),
                      Power(
                          Times(b, Sqrt(Times(Subtract(Times(b, e), Times(a, f)), Power(b, CN1)))),
                          CN1)),
                  x),
              And(FreeQ(List(a, b, c, d, e, f), x),
                  GtQ(Times(b, Power(Subtract(Times(b, c), Times(a, d)), CN1)), C0),
                  GtQ(Times(b, Power(Subtract(Times(b, e), Times(a, f)), CN1)), C0),
                  SimplerQ(Plus(a, Times(b, x)), Plus(c, Times(d, x))),
                  SimplerQ(Plus(a, Times(b, x)), Plus(e, Times(f, x))),
                  Or(PosQ(Times(CN1, Subtract(Times(b, c), Times(a, d)), Power(d, CN1))),
                      NegQ(Times(CN1, Subtract(Times(b, e), Times(a, f)), Power(f, CN1))))))));
}
