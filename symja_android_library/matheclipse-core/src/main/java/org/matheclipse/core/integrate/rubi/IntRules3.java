package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.AtomQ;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.C5;
import static org.matheclipse.core.expression.F.C9;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Coefficient;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Denominator;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.Hypergeometric2F1;
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
import static org.matheclipse.core.expression.F.Simplify;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.With;
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
import static org.matheclipse.core.expression.F.u_;
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
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FracPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ILtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntLinearQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LinearQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RationalQ;
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
class IntRules3 {
  public static IAST RULES =
      List(
          IIntegrate(61,
              Integrate(Times(Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), m_),
                  Power(Plus(c_, Times(d_DEFAULT, x_)), m_)), x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Plus(a, Times(b, x)), m), Power(Plus(c, Times(d, x)), m),
                          Power(Power(Plus(Times(a, c), Times(Plus(Times(b, c), Times(a, d)), x),
                              Times(b, d, Sqr(x))), m), CN1)),
                      Integrate(
                          Power(
                              Plus(Times(a, c), Times(Plus(Times(b, c), Times(a, d)), x),
                                  Times(b, d, Sqr(x))),
                              m),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d), x), NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      LtQ(CN1, m, C0), LeQ(C3, Denominator(m), C4),
                      AtomQ(Plus(Times(b, c), Times(a, d)))))),
          IIntegrate(62,
              Integrate(
                  Times(Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), m_),
                      Power(Plus(c_, Times(d_DEFAULT, x_)), m_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(Plus(a, Times(b, x)), m), Power(Plus(c, Times(d, x)), m),
                          Power(Power(Times(Plus(a, Times(b, x)), Plus(c, Times(d, x))), m), CN1)),
                      Integrate(
                          Power(
                              Plus(Times(a, c), Times(Plus(Times(b, c), Times(a, d)), x),
                                  Times(b, d, Sqr(x))),
                              m),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d), x), NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      LtQ(CN1, m, C0), LeQ(C3, Denominator(m), C4)))),
          IIntegrate(63,
              Integrate(Times(Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), m_),
                  Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), n_)), x_Symbol),
              Condition(
                  With(List(Set(p, Denominator(m))),
                      Dist(Times(p, Power(b, CN1)),
                          Subst(
                              Integrate(Times(Power(x, Subtract(Times(p, Plus(m, C1)), C1)),
                                  Power(Plus(c, Times(CN1, a, d, Power(b, CN1)),
                                      Times(d, Power(x, p), Power(b, CN1))), n)),
                                  x),
                              x, Power(Plus(a, Times(b, x)), Power(p, CN1))),
                          x)),
                  And(FreeQ(List(a, b, c, d), x), NeQ(Subtract(Times(b, c), Times(a, d)), C0), LtQ(
                      CN1, m, C0), LeQ(CN1, n, C0), LeQ(Denominator(n), Denominator(m)),
                      IntLinearQ(a, b, c, d, m, n, x)))),
          IIntegrate(64,
              Integrate(
                  Times(Power(Times(b_DEFAULT, x_), m_), Power(Plus(c_, Times(d_DEFAULT, x_)), n_)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(Power(c, n), Power(Times(b, x), Plus(m, C1)),
                          Hypergeometric2F1(Negate(n), Plus(m, C1), Plus(m, C2),
                              Times(CN1, d, x, Power(c, CN1))),
                          Power(Times(b, Plus(m, C1)), CN1)),
                      x),
                  And(FreeQ(List(b, c, d, m, n), x), Not(IntegerQ(m)),
                      Or(IntegerQ(n),
                          And(GtQ(c, C0),
                              Not(And(EqQ(n, Negate(C1D2)), EqQ(Subtract(Sqr(c), Sqr(d)), C0), GtQ(
                                  Times(CN1, d, Power(Times(b, c), CN1)), C0)))))))),
          IIntegrate(65,
              Integrate(
                  Times(Power(Times(b_DEFAULT,
                      x_), m_), Power(Plus(c_, Times(d_DEFAULT, x_)),
                          n_)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(Power(Plus(c, Times(d, x)), Plus(n, C1)),
                          Hypergeometric2F1(Negate(m), Plus(n, C1), Plus(n, C2), Plus(C1,
                              Times(d, x, Power(c, CN1)))),
                          Power(
                              Times(d, Plus(n, C1),
                                  Power(Times(CN1, d, Power(Times(b, c), CN1)), m)),
                              CN1)),
                      x),
                  And(FreeQ(List(b, c, d, m, n), x), Not(IntegerQ(
                      n)), Or(IntegerQ(m),
                          GtQ(Times(CN1, d, Power(Times(b, c), CN1)), C0))))),
          IIntegrate(66,
              Integrate(
                  Times(Power(Times(b_DEFAULT, x_), m_),
                      Power(Plus(c_, Times(d_DEFAULT, x_)), n_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(c, IntPart(n)), Power(Plus(c, Times(d, x)), FracPart(
                          n)), Power(Power(Plus(C1, Times(d, x, Power(c, CN1))), FracPart(n)),
                              CN1)),
                      Integrate(
                          Times(
                              Power(Times(b,
                                  x), m),
                              Power(Plus(C1, Times(d, x, Power(c, CN1))), n)),
                          x),
                      x),
                  And(FreeQ(List(b, c, d, m, n), x), Not(IntegerQ(m)), Not(IntegerQ(n)),
                      Not(GtQ(c, C0)), Not(GtQ(Times(CN1, d, Power(Times(b, c), CN1)), C0)),
                      Or(And(RationalQ(m),
                          Not(And(EqQ(n, Negate(C1D2)), EqQ(Subtract(Sqr(c), Sqr(d)), C0)))),
                          Not(RationalQ(n)))))),
          IIntegrate(67,
              Integrate(
                  Times(Power(Times(b_DEFAULT,
                      x_), m_), Power(Plus(c_, Times(d_DEFAULT, x_)),
                          n_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Times(CN1, b, c, Power(d, CN1)), IntPart(m)),
                          Power(Times(b, x), FracPart(
                              m)),
                          Power(Power(Times(CN1, d, x, Power(c, CN1)), FracPart(m)), CN1)),
                      Integrate(
                          Times(Power(Times(CN1, d, x, Power(c, CN1)), m),
                              Power(Plus(c, Times(d, x)), n)),
                          x),
                      x),
                  And(FreeQ(List(b, c, d, m, n),
                      x), Not(IntegerQ(m)), Not(IntegerQ(n)), Not(GtQ(c, C0)),
                      Not(GtQ(Times(CN1, d, Power(Times(b, c), CN1)), C0))))),
          IIntegrate(68,
              Integrate(
                  Times(
                      Power(Plus(a_,
                          Times(b_DEFAULT, x_)), m_),
                      Power(Plus(c_, Times(d_DEFAULT, x_)), n_)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(Power(Subtract(Times(b, c), Times(a, d)), n),
                          Power(Plus(a, Times(b, x)), Plus(m, C1)),
                          Hypergeometric2F1(
                              Negate(n), Plus(m, C1), Plus(m, C2), Times(CN1, d,
                                  Plus(a, Times(b, x)), Power(Subtract(Times(b, c), Times(a, d)),
                                      CN1))),
                          Power(Times(Power(b, Plus(n, C1)), Plus(m, C1)), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, m), x), NeQ(Subtract(Times(b, c), Times(a,
                      d)), C0), Not(IntegerQ(
                          m)),
                      IntegerQ(n)))),
          IIntegrate(69,
              Integrate(Times(Power(Plus(a_, Times(b_DEFAULT, x_)), m_),
                  Power(Plus(c_, Times(d_DEFAULT, x_)), n_)), x_Symbol),
              Condition(
                  Simp(
                      Times(
                          Power(Plus(a, Times(b, x)), Plus(m, C1)), Hypergeometric2F1(Negate(n),
                              Plus(m, C1), Plus(m, C2), Times(CN1, d, Plus(a,
                                  Times(b, x)), Power(Subtract(Times(b, c), Times(a, d)), CN1))),
                          Power(
                              Times(b, Plus(m, C1),
                                  Power(Times(b, Power(Subtract(Times(b, c), Times(a, d)), CN1)),
                                      n)),
                              CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, m, n), x), NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      Not(IntegerQ(m)), Not(IntegerQ(n)),
                      GtQ(Times(b,
                          Power(Subtract(Times(b, c), Times(a, d)), CN1)), C0),
                      Or(RationalQ(m),
                          Not(And(
                              RationalQ(n), GtQ(
                                  Times(CN1, d, Power(Subtract(Times(b, c), Times(a, d)),
                                      CN1)),
                                  C0))))))),
          IIntegrate(70,
              Integrate(
                  Times(
                      Power(Plus(a_,
                          Times(b_DEFAULT, x_)), m_),
                      Power(Plus(c_, Times(d_DEFAULT, x_)), n_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Plus(c, Times(d, x)), FracPart(n)),
                          Power(
                              Times(
                                  Power(Times(b, Power(Subtract(Times(b, c), Times(a, d)), CN1)),
                                      IntPart(n)),
                                  Power(
                                      Times(b, Plus(c, Times(d, x)),
                                          Power(Subtract(Times(b, c), Times(a, d)), CN1)),
                                      FracPart(n))),
                              CN1)),
                      Integrate(Times(Power(Plus(a, Times(b, x)), m),
                          Power(Simp(
                              Plus(Times(b, c, Power(Subtract(Times(b, c), Times(a, d)), CN1)),
                                  Times(b, d, x, Power(Subtract(Times(b, c), Times(a, d)), CN1))),
                              x), n)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, m, n), x), NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      Not(IntegerQ(m)), Not(IntegerQ(
                          n)),
                      Or(RationalQ(m), Not(SimplerQ(Plus(n, C1), Plus(m, C1))))))),
          IIntegrate(71,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT,
                          u_)), m_DEFAULT),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, u_)), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Coefficient(u, x,
                          C1), CN1),
                      Subst(
                          Integrate(
                              Times(Power(Plus(a, Times(b, x)), m), Power(Plus(c, Times(d, x)), n)),
                              x),
                          x, u),
                      x),
                  And(FreeQ(List(a, b, c, d, m,
                      n), x), LinearQ(u,
                          x),
                      NeQ(Coefficient(u, x, C0), C0)))),
          IIntegrate(72,
              Integrate(
                  Times(Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), CN1),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), CN1),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(ExpandIntegrand(Times(Power(Plus(e, Times(f, x)), p),
                      Power(Times(Plus(a, Times(b, x)), Plus(c, Times(d, x))), CN1)), x), x),
                  And(FreeQ(List(a, b, c, d, e, f), x), IntegerQ(p)))),
          IIntegrate(73,
              Integrate(
                  Times(Power(Plus(a_, Times(b_DEFAULT, x_)), m_DEFAULT),
                      Power(Plus(c_, Times(d_DEFAULT, x_)), n_DEFAULT),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(Times(Power(Plus(Times(a, c), Times(b, d, Sqr(x))), m),
                      Power(Plus(e, Times(f, x)), p)), x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n, p), x), EqQ(Plus(Times(b, c), Times(a, d)),
                      C0), EqQ(n, m), IntegerQ(m)))),
          IIntegrate(74,
              Integrate(
                  Times(Plus(a_DEFAULT, Times(b_DEFAULT, x_)),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), n_DEFAULT),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(b, Power(Plus(c, Times(d, x)), Plus(n, C1)),
                          Power(Plus(e, Times(f, x)), Plus(p,
                              C1)),
                          Power(Times(d, f, Plus(n, p, C2)), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, n, p), x), NeQ(Plus(n, p, C2), C0),
                      EqQ(Subtract(Times(a, d, f, Plus(n, p, C2)),
                          Times(b, Plus(Times(d, e, Plus(n, C1)), Times(c, f, Plus(p, C1))))),
                          C0)))),
          IIntegrate(75,
              Integrate(
                  Times(
                      Power(Times(d_DEFAULT, x_), n_DEFAULT), Plus(a_, Times(b_DEFAULT,
                          x_)),
                      Power(Plus(e_, Times(f_DEFAULT, x_)), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(ExpandIntegrand(Times(Plus(a, Times(b, x)), Power(Times(d, x), n),
                      Power(Plus(e, Times(f, x)), p)), x), x),
                  And(FreeQ(List(a, b, d, e, f, n), x), IGtQ(p, C0),
                      EqQ(Plus(Times(b, e), Times(a, f)), C0), Not(And(ILtQ(Plus(n, p, C2), C0),
                          GtQ(Plus(n, Times(C2, p)), C0)))))),
          IIntegrate(76,
              Integrate(
                  Times(
                      Power(Times(d_DEFAULT, x_), n_DEFAULT), Plus(a_, Times(b_DEFAULT, x_)), Power(
                          Plus(e_, Times(f_DEFAULT, x_)), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Times(
                              Plus(a, Times(b, x)), Power(Times(d, x),
                                  n),
                              Power(Plus(e, Times(f, x)), p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, d, e, f, n), x), IGtQ(p, C0), Or(NeQ(n, CN1), EqQ(p, C1)),
                      NeQ(Plus(Times(b, e), Times(a, f)), C0),
                      Or(Not(IntegerQ(n)), LtQ(Plus(Times(C9, p), Times(C5, n)), C0),
                          GeQ(Plus(n, p, C1), C0),
                          And(GeQ(Plus(n, p, C2), C0), RationalQ(a, b, d, e, f))),
                      Or(NeQ(Plus(n, p, C3), C0), EqQ(p, C1))))),
          IIntegrate(77,
              Integrate(Times(Plus(a_DEFAULT, Times(b_DEFAULT, x_)),
                  Power(Plus(c_, Times(d_DEFAULT, x_)), n_DEFAULT),
                  Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), p_DEFAULT)), x_Symbol),
              Condition(
                  Integrate(ExpandIntegrand(Times(Plus(a, Times(b, x)),
                      Power(Plus(c, Times(d, x)), n), Power(Plus(e, Times(f, x)), p)), x), x),
                  And(FreeQ(List(a, b, c, d, e, f, n), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      Or(And(ILtQ(n, C0), ILtQ(p, C0)), EqQ(p, C1), And(IGtQ(p, C0),
                          Or(Not(IntegerQ(n)), LeQ(Plus(Times(C9, p), Times(C5, Plus(n, C2))), C0),
                              GeQ(Plus(n, p, C1), C0), And(GeQ(Plus(n, p, C2), C0),
                                  RationalQ(a, b, c, d, e, f)))))))),
          IIntegrate(78,
              Integrate(
                  Times(Plus(a_DEFAULT, Times(b_DEFAULT, x_)),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT,
                          x_)), n_DEFAULT),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(Negate(
                      Simp(
                          Times(Subtract(Times(b, e), Times(a, f)),
                              Power(Plus(c, Times(d, x)), Plus(n, C1)),
                              Power(Plus(e, Times(f, x)), Plus(p, C1)),
                              Power(Times(f, Plus(p, C1), Subtract(Times(c, f), Times(d, e))),
                                  CN1)),
                          x)),
                      Dist(
                          Times(
                              Subtract(Times(a, d, f, Plus(n, p, C2)),
                                  Times(b,
                                      Plus(Times(d, e, Plus(n, C1)), Times(c, f, Plus(p, C1))))),
                              Power(Times(f, Plus(p, C1), Subtract(Times(c, f), Times(d, e))),
                                  CN1)),
                          Integrate(
                              Times(Power(Plus(c, Times(d, x)), n),
                                  Power(Plus(e, Times(f, x)), Plus(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f,
                      n), x), LtQ(p, CN1), Or(
                          Not(LtQ(n,
                              CN1)),
                          IntegerQ(p),
                          Not(Or(IntegerQ(n),
                              Not(Or(EqQ(e, C0), Not(Or(EqQ(c, C0), LtQ(p, n))))))))))),
          IIntegrate(79,
              Integrate(
                  Times(Plus(a_DEFAULT, Times(b_DEFAULT, x_)),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), n_DEFAULT), Power(
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_)), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Negate(
                          Simp(Times(Subtract(Times(b, e), Times(a, f)),
                              Power(Plus(c, Times(d, x)), Plus(n, C1)),
                              Power(Plus(e, Times(f, x)), Plus(p, C1)),
                              Power(Times(f, Plus(p, C1), Subtract(Times(c, f), Times(d, e))),
                                  CN1)),
                              x)),
                      Dist(
                          Times(
                              Subtract(Times(a, d, f, Plus(n, p, C2)),
                                  Times(b, Plus(Times(d, e, Plus(n, C1)),
                                      Times(c, f, Plus(p, C1))))),
                              Power(Times(f, Plus(p, C1), Subtract(Times(c, f), Times(d, e))),
                                  CN1)),
                          Integrate(
                              Times(
                                  Power(Plus(c, Times(d, x)), n), Power(Plus(e, Times(f, x)),
                                      Simplify(Plus(p, C1)))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, n,
                      p), x), Not(
                          RationalQ(p)),
                      SumSimplerQ(p, C1)))),
          IIntegrate(80,
              Integrate(Times(Plus(a_DEFAULT, Times(b_DEFAULT, x_)),
                  Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), n_DEFAULT), Power(
                      Plus(e_DEFAULT, Times(f_DEFAULT, x_)), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(Simp(Times(b, Power(Plus(c, Times(d, x)), Plus(n, C1)),
                      Power(Plus(e, Times(f, x)), Plus(p, C1)), Power(Times(d, f, Plus(n, p, C2)),
                          CN1)),
                      x),
                      Dist(
                          Times(
                              Subtract(Times(a, d, f, Plus(n, p, C2)),
                                  Times(b,
                                      Plus(Times(d, e, Plus(n, C1)), Times(c, f, Plus(p, C1))))),
                              Power(Times(d, f, Plus(n, p, C2)), CN1)),
                          Integrate(
                              Times(Power(Plus(c, Times(d, x)), n), Power(Plus(e, Times(f, x)), p)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, n, p), x), NeQ(Plus(n, p, C2), C0)))));
}
