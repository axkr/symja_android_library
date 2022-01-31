package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.ArcCosh;
import static org.matheclipse.core.expression.F.ArcCsch;
import static org.matheclipse.core.expression.F.ArcSech;
import static org.matheclipse.core.expression.F.ArcSinh;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.u_DEFAULT;
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
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ILtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntHide;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntegersQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.InverseFunctionFreeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SimplifyIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Unintegrable;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules315 {
  public static IAST RULES =
      List(
          IIntegrate(6301,
              Integrate(
                  Times(Plus(a_DEFAULT, Times(ArcSech(Times(c_DEFAULT, x_)), b_DEFAULT)),
                      Power(Times(f_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(u,
                              IntHide(
                                  Times(Power(Times(f, x), m),
                                      Power(Plus(d, Times(e, Sqr(x))), p)),
                                  x))),
                      Plus(Dist(Plus(a, Times(b, ArcSech(Times(c, x)))), u, x),
                          Dist(
                              Times(b, Sqrt(Plus(C1, Times(c, x))),
                                  Sqrt(Power(Plus(C1, Times(c, x)), CN1))),
                              Integrate(SimplifyIntegrand(Times(u,
                                  Power(Times(x, Sqrt(Subtract(C1, Times(c, x))),
                                      Sqrt(Plus(C1, Times(c, x)))), CN1)),
                                  x), x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, f, m, p), x), Or(
                      And(IGtQ(p, C0),
                          Not(And(ILtQ(Times(C1D2, Subtract(m, C1)), C0),
                              GtQ(Plus(m, Times(C2, p), C3), C0)))),
                      And(IGtQ(Times(C1D2, Plus(m, C1)), C0),
                          Not(And(ILtQ(p, C0), GtQ(Plus(m, Times(C2, p), C3), C0)))),
                      And(ILtQ(Times(C1D2, Plus(m, Times(C2, p), C1)), C0),
                          Not(ILtQ(Times(C1D2, Subtract(m, C1)), C0))))))),
          IIntegrate(6302,
              Integrate(
                  Times(Plus(a_DEFAULT, Times(ArcCsch(Times(c_DEFAULT, x_)), b_DEFAULT)),
                      Power(Times(f_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(u,
                              IntHide(
                                  Times(Power(Times(f,
                                      x), m), Power(Plus(d, Times(e, Sqr(x))),
                                          p)),
                                  x))),
                      Subtract(Dist(Plus(a, Times(b, ArcCsch(Times(c, x)))), u, x),
                          Dist(
                              Times(b, c, x, Power(Times(CN1, Sqr(c), Sqr(x)), CN1D2)),
                              Integrate(
                                  SimplifyIntegrand(Times(u,
                                      Power(Times(x, Sqrt(Subtract(CN1, Times(Sqr(c), Sqr(x))))),
                                          CN1)),
                                      x),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, f, m,
                      p), x), Or(
                          And(IGtQ(p, C0),
                              Not(And(ILtQ(Times(C1D2, Subtract(m, C1)), C0),
                                  GtQ(Plus(m, Times(C2, p), C3), C0)))),
                          And(IGtQ(Times(C1D2,
                              Plus(m, C1)), C0), Not(
                                  And(ILtQ(p, C0), GtQ(Plus(m, Times(C2, p), C3), C0)))),
                          And(ILtQ(Times(C1D2,
                              Plus(m, Times(C2, p), C1)), C0), Not(
                                  ILtQ(Times(C1D2, Subtract(m, C1)), C0))))))),
          IIntegrate(6303,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcSech(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          n_DEFAULT),
                      Power(x_, m_DEFAULT), Power(Plus(d_DEFAULT, Times(e_DEFAULT, Sqr(x_))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Negate(
                      Subst(
                          Integrate(Times(Power(Plus(e, Times(d, Sqr(x))), p),
                              Power(Plus(a, Times(b, ArcCosh(Times(x, Power(c, CN1))))), n), Power(
                                  Power(x, Plus(m, Times(C2, Plus(p, C1)))), CN1)),
                              x),
                          x, Power(x, CN1))),
                  And(FreeQ(List(a, b, c, d, e, n), x), IGtQ(n, C0), IntegersQ(m, p)))),
          IIntegrate(6304,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCsch(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          n_DEFAULT),
                      Power(x_, m_DEFAULT), Power(Plus(d_DEFAULT, Times(e_DEFAULT, Sqr(x_))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Negate(
                      Subst(
                          Integrate(
                              Times(Power(Plus(e, Times(d, Sqr(x))), p),
                                  Power(Plus(a, Times(b,
                                      ArcSinh(Times(x, Power(c, CN1))))), n),
                                  Power(Power(x, Plus(m, Times(C2, Plus(p, C1)))), CN1)),
                              x),
                          x, Power(x, CN1))),
                  And(FreeQ(List(a, b, c, d, e, n), x), IGtQ(n, C0), IntegersQ(m, p)))),
          IIntegrate(6305,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcSech(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          n_DEFAULT),
                      Power(x_, m_DEFAULT), Power(Plus(d_DEFAULT, Times(e_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(
                          Times(Sqrt(Sqr(
                              x)), Power(x,
                                  CN1)),
                          Subst(
                              Integrate(Times(Power(Plus(e, Times(d, Sqr(x))), p),
                                  Power(Plus(a, Times(b,
                                      ArcCosh(Times(x, Power(c, CN1))))), n),
                                  Power(Power(x, Plus(m, Times(C2, Plus(p, C1)))), CN1)),
                                  x),
                              x, Power(x, CN1)),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, n), x), IGtQ(n, C0),
                      EqQ(Plus(Times(Sqr(c), d), e), C0), IntegerQ(m), IntegerQ(Plus(p,
                          C1D2)),
                      GtQ(e, C0), LtQ(d, C0)))),
          IIntegrate(6306,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCsch(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          n_DEFAULT),
                      Power(x_, m_DEFAULT), Power(Plus(d_DEFAULT, Times(e_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(
                          Times(Sqrt(Sqr(
                              x)), Power(x,
                                  CN1)),
                          Subst(
                              Integrate(
                                  Times(Power(Plus(e, Times(d, Sqr(x))), p),
                                      Power(Plus(a, Times(b,
                                          ArcSinh(Times(x, Power(c, CN1))))), n),
                                      Power(Power(x, Plus(m, Times(C2, Plus(p, C1)))), CN1)),
                                  x),
                              x, Power(x, CN1)),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, n), x), IGtQ(n, C0),
                      EqQ(Subtract(e, Times(Sqr(c), d)), C0), IntegerQ(m), IntegerQ(Plus(p,
                          C1D2)),
                      GtQ(e, C0), LtQ(d, C0)))),
          IIntegrate(6307,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcSech(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          n_DEFAULT),
                      Power(x_, m_DEFAULT), Power(Plus(d_DEFAULT, Times(e_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(
                          Times(Sqrt(Plus(d, Times(e, Sqr(x)))), Power(
                              Times(x, Sqrt(Plus(e, Times(d, Power(x, CN2))))), CN1)),
                          Subst(
                              Integrate(
                                  Times(Power(Plus(e, Times(d, Sqr(x))), p),
                                      Power(Plus(a, Times(b, ArcCosh(Times(x, Power(c, CN1))))), n),
                                      Power(Power(x, Plus(m, Times(C2, Plus(p, C1)))), CN1)),
                                  x),
                              x, Power(x, CN1)),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, n), x), IGtQ(n, C0),
                      EqQ(Plus(Times(Sqr(c), d), e), C0), IntegerQ(m), IntegerQ(Plus(p, C1D2)), Not(
                          And(GtQ(e, C0), LtQ(d, C0)))))),
          IIntegrate(6308,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(ArcCsch(Times(c_DEFAULT, x_)),
                          b_DEFAULT)), n_DEFAULT),
                      Power(x_, m_DEFAULT), Power(Plus(d_DEFAULT, Times(e_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(
                          Times(
                              Sqrt(Plus(d, Times(e,
                                  Sqr(x)))),
                              Power(Times(x, Sqrt(Plus(e, Times(d, Power(x, CN2))))), CN1)),
                          Subst(
                              Integrate(
                                  Times(Power(Plus(e, Times(d, Sqr(x))), p),
                                      Power(Plus(a, Times(b,
                                          ArcSinh(Times(x, Power(c, CN1))))), n),
                                      Power(Power(x, Plus(m, Times(C2, Plus(p, C1)))), CN1)),
                                  x),
                              x, Power(x, CN1)),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, n), x), IGtQ(n, C0),
                      EqQ(Subtract(e, Times(Sqr(c), d)), C0), IntegerQ(m), IntegerQ(Plus(p,
                          C1D2)),
                      Not(And(GtQ(e, C0), LtQ(d, C0)))))),
          IIntegrate(6309,
              Integrate(
                  Times(Plus(a_DEFAULT, Times(ArcSech(Times(c_DEFAULT, x_)), b_DEFAULT)),
                      u_),
                  x_Symbol),
              Condition(
                  With(List(Set(v, IntHide(u, x))),
                      Condition(
                          Plus(
                              Dist(Plus(a,
                                  Times(b, ArcSech(Times(c, x)))), v, x),
                              Dist(
                                  Times(
                                      b, Sqrt(Subtract(C1, Times(Sqr(c), Sqr(x)))), Power(Times(c,
                                          x, Sqrt(Plus(CN1, Power(Times(c, x), CN1))), Sqrt(
                                              Plus(C1, Power(Times(c, x), CN1)))),
                                          CN1)),
                                  Integrate(SimplifyIntegrand(Times(v,
                                      Power(Times(x, Sqrt(Subtract(C1, Times(Sqr(c), Sqr(x))))),
                                          CN1)),
                                      x), x),
                                  x)),
                          InverseFunctionFreeQ(v, x))),
                  FreeQ(List(a, b, c), x))),
          IIntegrate(6310,
              Integrate(
                  Times(Plus(a_DEFAULT, Times(ArcCsch(Times(c_DEFAULT, x_)), b_DEFAULT)),
                      u_),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(v,
                          IntHide(u, x))),
                      Condition(
                          Plus(
                              Dist(Plus(a,
                                  Times(b, ArcCsch(Times(c, x)))), v, x),
                              Dist(Times(b, Power(c, CN1)),
                                  Integrate(
                                      SimplifyIntegrand(
                                          Times(v,
                                              Power(
                                                  Times(Sqr(x),
                                                      Sqrt(Plus(C1,
                                                          Power(Times(Sqr(c), Sqr(x)), CN1)))),
                                                  CN1)),
                                          x),
                                      x),
                                  x)),
                          InverseFunctionFreeQ(v, x))),
                  FreeQ(List(a, b, c), x))),
          IIntegrate(6311,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(ArcSech(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          n_DEFAULT),
                      u_DEFAULT),
                  x_Symbol),
              Condition(
                  Unintegrable(Times(u, Power(Plus(a, Times(b, ArcSech(Times(c, x)))), n)),
                      x),
                  FreeQ(List(a, b, c, n), x))),
          IIntegrate(6312,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCsch(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          n_DEFAULT),
                      u_DEFAULT),
                  x_Symbol),
              Condition(
                  Unintegrable(Times(u,
                      Power(Plus(a, Times(b, ArcCsch(Times(c, x)))), n)), x),
                  FreeQ(List(a, b, c, n), x))),
          IIntegrate(
              6313, Integrate(ArcSech(
                  Plus(c_, Times(d_DEFAULT, x_))), x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Plus(c, Times(d, x)), ArcSech(Plus(c, Times(d, x))),
                              Power(d, CN1)),
                          x),
                      Integrate(
                          Times(
                              Sqrt(
                                  Times(
                                      Subtract(Subtract(C1, c), Times(d,
                                          x)),
                                      Power(Plus(C1, c, Times(d, x)), CN1))),
                              Power(Subtract(Subtract(C1, c), Times(d, x)), CN1)),
                          x)),
                  FreeQ(List(c, d), x))),
          IIntegrate(
              6314, Integrate(ArcCsch(
                  Plus(c_, Times(d_DEFAULT, x_))), x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Plus(c, Times(d, x)), ArcCsch(Plus(c, Times(d, x))),
                              Power(d, CN1)),
                          x),
                      Integrate(
                          Power(Times(Plus(c, Times(d, x)),
                              Sqrt(Plus(C1, Power(Plus(c, Times(d, x)), CN2)))), CN1),
                          x)),
                  FreeQ(List(c, d), x))),
          IIntegrate(6315,
              Integrate(
                  Power(
                      Plus(a_DEFAULT, Times(ArcSech(Plus(c_, Times(d_DEFAULT, x_))),
                          b_DEFAULT)),
                      p_DEFAULT),
                  x_Symbol),
              Condition(
                  Dist(Power(d, CN1),
                      Subst(
                          Integrate(Power(Plus(a, Times(b, ArcSech(x))), p), x), x, Plus(c,
                              Times(d, x))),
                      x),
                  And(FreeQ(List(a, b, c, d), x), IGtQ(p, C0)))),
          IIntegrate(6316,
              Integrate(
                  Power(
                      Plus(a_DEFAULT, Times(ArcCsch(Plus(c_, Times(d_DEFAULT, x_))),
                          b_DEFAULT)),
                      p_DEFAULT),
                  x_Symbol),
              Condition(
                  Dist(Power(d, CN1),
                      Subst(
                          Integrate(Power(Plus(a, Times(b, ArcCsch(x))), p), x), x, Plus(c, Times(d,
                              x))),
                      x),
                  And(FreeQ(List(a, b, c, d), x), IGtQ(p, C0)))),
          IIntegrate(6317,
              Integrate(
                  Power(
                      Plus(a_DEFAULT, Times(ArcSech(Plus(c_, Times(d_DEFAULT, x_))),
                          b_DEFAULT)),
                      p_),
                  x_Symbol),
              Condition(
                  Unintegrable(Power(Plus(a, Times(b, ArcSech(Plus(c, Times(d, x))))),
                      p), x),
                  And(FreeQ(List(a, b, c, d, p), x), Not(IGtQ(p, C0))))),
          IIntegrate(6318,
              Integrate(
                  Power(Plus(a_DEFAULT, Times(ArcCsch(Plus(c_, Times(d_DEFAULT, x_))), b_DEFAULT)),
                      p_),
                  x_Symbol),
              Condition(
                  Unintegrable(Power(Plus(a, Times(b,
                      ArcCsch(Plus(c, Times(d, x))))), p), x),
                  And(FreeQ(List(a, b, c, d, p), x), Not(IGtQ(p, C0))))),
          IIntegrate(6319,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcSech(Plus(c_, Times(d_DEFAULT, x_))),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(d, CN1),
                      Subst(
                          Integrate(
                              Times(
                                  Power(Times(f, x,
                                      Power(d, CN1)), m),
                                  Power(Plus(a, Times(b, ArcSech(x))), p)),
                              x),
                          x, Plus(c, Times(d, x))),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m), x), EqQ(Subtract(Times(d, e),
                      Times(c, f)), C0), IGtQ(p,
                          C0)))),
          IIntegrate(6320,
              Integrate(
                  Times(Power(
                      Plus(a_DEFAULT, Times(ArcCsch(Plus(c_, Times(d_DEFAULT, x_))), b_DEFAULT)),
                      p_DEFAULT), Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(d, CN1),
                      Subst(
                          Integrate(Times(Power(Times(f, x, Power(d, CN1)), m),
                              Power(Plus(a, Times(b, ArcCsch(x))), p)), x),
                          x, Plus(c, Times(d, x))),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m), x),
                      EqQ(Subtract(Times(d, e), Times(c, f)), C0), IGtQ(p, C0)))));
}
