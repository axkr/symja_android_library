package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.C5;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.PolynomialQuotient;
import static org.matheclipse.core.expression.F.PolynomialRemainder;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.g_DEFAULT;
import static org.matheclipse.core.expression.F.m_;
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
import static org.matheclipse.core.expression.S.g;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandToSum;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ILtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntegersQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules47 {
  public static IAST RULES =
      List(
          IIntegrate(941,
              Integrate(
                  Times(
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT,
                          x_)), m_),
                      Sqrt(Plus(f_DEFAULT,
                          Times(g_DEFAULT, x_))),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))),
                          CN1D2)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(C2, e, Power(Plus(d, Times(e, x)), Subtract(m, C1)),
                              Sqrt(Plus(f, Times(g,
                                  x))),
                              Sqrt(Plus(a, Times(b, x),
                                  Times(c, Sqr(x)))),
                              Power(Times(c, Plus(Times(C2, m), C1)), CN1)),
                          x),
                      Dist(
                          Power(Times(c, Plus(Times(C2, m), C1)), CN1), Integrate(Times(
                              Power(Plus(d, Times(e, x)), Subtract(m,
                                  C2)),
                              Simp(Plus(
                                  Times(e,
                                      Plus(Times(b, d, f),
                                          Times(a,
                                              Plus(Times(d, g),
                                                  Times(C2, e, f, Subtract(m, C1)))))),
                                  Times(CN1, c, Sqr(d), f, Plus(Times(C2, m), C1)),
                                  Times(
                                      Plus(Times(a, Sqr(e), g, Subtract(Times(C2, m), C1)),
                                          Times(CN1, c, d,
                                              Plus(Times(C4, e, f, m),
                                                  Times(d, g, Plus(Times(C2, m), C1)))),
                                          Times(b, e,
                                              Plus(Times(C2, d, g),
                                                  Times(e, f, Subtract(Times(C2, m), C1))))),
                                      x),
                                  Times(e,
                                      Subtract(Times(C2, b, e, g, m),
                                          Times(c,
                                              Plus(Times(e, f),
                                                  Times(d, g, Subtract(Times(C4, m), C1))))),
                                      Sqr(x))),
                                  x),
                              Power(
                                  Times(Sqrt(Plus(f, Times(g, x))),
                                      Sqrt(Plus(a, Times(b, x), Times(c, Sqr(x))))),
                                  CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g), x),
                      NeQ(Subtract(Times(e, f), Times(d, g)), C0),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      NeQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))), C0),
                      IntegerQ(Times(C2, m)), GtQ(m, C1)))),
          IIntegrate(942,
              Integrate(
                  Times(Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_),
                      Sqrt(Plus(f_DEFAULT, Times(g_DEFAULT, x_))), Power(
                          Plus(a_, Times(c_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(C2, e, Power(Plus(d, Times(e, x)), Subtract(m, C1)),
                              Sqrt(Plus(f, Times(g,
                                  x))),
                              Sqrt(Plus(a,
                                  Times(c, Sqr(x)))),
                              Power(Times(c, Plus(Times(C2, m), C1)), CN1)),
                          x),
                      Dist(
                          Power(Times(c,
                              Plus(Times(C2, m), C1)), CN1),
                          Integrate(
                              Times(
                                  Power(Plus(d, Times(e, x)), Subtract(m, C2)), Simp(
                                      Subtract(
                                          Plus(
                                              Times(a, e,
                                                  Plus(Times(d,
                                                      g), Times(C2, e, f, Subtract(m, C1)))),
                                              Times(CN1, c, Sqr(d), f, Plus(Times(C2, m), C1)),
                                              Times(Subtract(
                                                  Times(a, Sqr(e), g, Subtract(Times(C2, m), C1)),
                                                  Times(c, d,
                                                      Plus(Times(C4, e, f, m),
                                                          Times(d, g, Plus(Times(C2, m), C1))))),
                                                  x)),
                                          Times(c, e,
                                              Plus(Times(e, f),
                                                  Times(d, g, Subtract(Times(C4, m), C1))),
                                              Sqr(x))),
                                      x),
                                  Power(Times(Sqrt(Plus(f, Times(g, x))),
                                      Sqrt(Plus(a, Times(c, Sqr(x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, c, d, e, f, g), x), NeQ(Subtract(Times(e, f), Times(d, g)), C0),
                      NeQ(Plus(Times(c, Sqr(
                          d)), Times(a,
                              Sqr(e))),
                          C0),
                      IntegerQ(Times(C2, m)), GtQ(m, C1)))),
          IIntegrate(943,
              Integrate(
                  Times(
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT,
                          x_)), CN1),
                      Sqrt(Plus(f_DEFAULT,
                          Times(g_DEFAULT, x_))),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))),
                          CN1D2)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(Times(g, Power(e, CN1)),
                          Integrate(Power(
                              Times(Sqrt(Plus(f, Times(g, x))),
                                  Sqrt(Plus(a, Times(b, x), Times(c, Sqr(x))))),
                              CN1), x),
                          x),
                      Dist(Times(Subtract(Times(e, f), Times(d, g)), Power(e, CN1)),
                          Integrate(
                              Power(
                                  Times(Plus(d, Times(e, x)), Sqrt(Plus(f, Times(g, x))),
                                      Sqrt(Plus(a, Times(b, x), Times(c, Sqr(x))))),
                                  CN1),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g), x),
                      NeQ(Subtract(Times(e, f), Times(d,
                          g)), C0),
                      NeQ(Subtract(Sqr(b), Times(C4, a,
                          c)), C0),
                      NeQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))), C0)))),
          IIntegrate(944,
              Integrate(
                  Times(Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), CN1),
                      Sqrt(Plus(f_DEFAULT,
                          Times(g_DEFAULT, x_))),
                      Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(Times(g, Power(e, CN1)),
                          Integrate(
                              Power(
                                  Times(Sqrt(Plus(f, Times(g, x))),
                                      Sqrt(Plus(a, Times(c, Sqr(x))))),
                                  CN1),
                              x),
                          x),
                      Dist(
                          Times(Subtract(Times(e, f), Times(d, g)), Power(e,
                              CN1)),
                          Integrate(Power(Times(Plus(d, Times(e, x)), Sqrt(Plus(f, Times(g, x))),
                              Sqrt(Plus(a, Times(c, Sqr(x))))), CN1), x),
                          x)),
                  And(FreeQ(List(a, c, d, e, f, g), x), NeQ(Subtract(Times(e, f),
                      Times(d, g)), C0), NeQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))),
                          C0)))),
          IIntegrate(945,
              Integrate(
                  Times(Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_),
                      Sqrt(Plus(f_DEFAULT, Times(g_DEFAULT, x_))),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))),
                          CN1D2)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(e, Power(Plus(d, Times(e, x)), Plus(m, C1)),
                              Sqrt(Plus(f, Times(g, x))),
                              Sqrt(Plus(a, Times(b, x), Times(c, Sqr(x)))),
                              Power(
                                  Times(Plus(m, C1),
                                      Plus(Times(c, Sqr(d)), Times(CN1, b, d, e),
                                          Times(a, Sqr(e)))),
                                  CN1)),
                          x),
                      Dist(
                          Power(
                              Times(
                                  C2, Plus(m, C1), Plus(Times(c, Sqr(d)), Times(CN1, b, d, e),
                                      Times(a, Sqr(e)))),
                              CN1),
                          Integrate(
                              Times(
                                  Power(Plus(d, Times(e,
                                      x)), Plus(m,
                                          C1)),
                                  Simp(Subtract(
                                      Subtract(
                                          Subtract(Times(C2, c, d, f, Plus(m, C1)),
                                              Times(e,
                                                  Plus(Times(a, g),
                                                      Times(b, f, Plus(Times(C2, m), C3))))),
                                          Times(C2,
                                              Subtract(Times(b, e, g, Plus(C2, m)),
                                                  Times(c,
                                                      Subtract(Times(d, g, Plus(m, C1)),
                                                          Times(e, f, Plus(m, C2))))),
                                              x)),
                                      Times(c, e, g, Plus(Times(C2, m), C5), Sqr(x))), x),
                                  Power(
                                      Times(Sqrt(Plus(f, Times(g, x))),
                                          Sqrt(Plus(a, Times(b, x), Times(c, Sqr(x))))),
                                      CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g), x),
                      NeQ(Subtract(Times(e, f), Times(d, g)), C0),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      NeQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a,
                          Sqr(e))), C0),
                      IntegerQ(Times(C2, m)), LeQ(m, CN2)))),
          IIntegrate(946,
              Integrate(
                  Times(Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_),
                      Sqrt(Plus(f_DEFAULT, Times(g_DEFAULT, x_))),
                      Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(e, Power(Plus(d, Times(e, x)), Plus(m, C1)),
                              Sqrt(Plus(f, Times(g, x))), Sqrt(Plus(a, Times(c, Sqr(x)))),
                              Power(
                                  Times(Plus(m, C1),
                                      Plus(Times(c, Sqr(d)), Times(a, Sqr(e)))),
                                  CN1)),
                          x),
                      Dist(
                          Power(
                              Times(C2, Plus(m, C1),
                                  Plus(Times(c, Sqr(d)), Times(a, Sqr(e)))),
                              CN1),
                          Integrate(
                              Times(Power(Plus(d, Times(e, x)), Plus(m, C1)),
                                  Simp(Subtract(
                                      Plus(Times(C2, c, d, f, Plus(m, C1)), Times(CN1, e, a, g),
                                          Times(C2, c,
                                              Subtract(Times(d, g, Plus(m, C1)),
                                                  Times(e, f, Plus(m, C2))),
                                              x)),
                                      Times(c, e, g, Plus(Times(C2, m), C5), Sqr(x))), x),
                                  Power(
                                      Times(Sqrt(Plus(f, Times(g, x))),
                                          Sqrt(Plus(a, Times(c, Sqr(x))))),
                                      CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, c, d, e, f, g), x), NeQ(Subtract(Times(e, f), Times(d, g)), C0),
                      NeQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), C0), IntegerQ(Times(C2, m)),
                      LeQ(m, CN2)))),
          IIntegrate(947,
              Integrate(
                  Times(Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_),
                      Power(Plus(f_DEFAULT, Times(g_DEFAULT, x_)), n_),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(Integrate(
                  ExpandIntegrand(
                      Times(Power(Plus(d, Times(e, x)), m), Power(Plus(f, Times(g, x)), n),
                          Power(Plus(a, Times(b, x), Times(c, Sqr(x))), p)),
                      x),
                  x),
                  And(FreeQ(List(a, b, c, d, e, f, g), x),
                      NeQ(Subtract(Times(e, f), Times(d, g)), C0),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      NeQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))), C0),
                      IGtQ(p, C0),
                      Or(IGtQ(m, C0),
                          And(EqQ(m, CN2), EqQ(p, C1),
                              EqQ(Subtract(Times(C2, c, d), Times(b, e)), C0)))))),
          IIntegrate(948,
              Integrate(
                  Times(Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_),
                      Power(Plus(f_DEFAULT, Times(g_DEFAULT, x_)), n_),
                      Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Times(Power(Plus(d, Times(e, x)), m), Power(Plus(f, Times(g, x)), n),
                              Power(Plus(a, Times(c, Sqr(x))), p)),
                          x),
                      x),
                  And(FreeQ(List(a, c, d, e, f, g), x), NeQ(Subtract(Times(e, f), Times(d, g)), C0),
                      NeQ(Plus(Times(c, Sqr(
                          d)), Times(a,
                              Sqr(e))),
                          C0),
                      IGtQ(p, C0), Or(IGtQ(m, C0), And(EqQ(m, CN2), EqQ(p, C1), EqQ(d, C0)))))),
          IIntegrate(949, Integrate(Times(Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_), Power(
              Plus(f_DEFAULT, Times(g_DEFAULT, x_)), n_),
              Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_DEFAULT)),
              x_Symbol),
              Condition(
                  With(
                      List(
                          Set($s("§qx"),
                              PolynomialQuotient(
                                  Power(Plus(a, Times(b, x), Times(c, Sqr(x))), p), Plus(d,
                                      Times(e, x)),
                                  x)),
                          Set($s("R"),
                              PolynomialRemainder(Power(Plus(a, Times(b, x), Times(c, Sqr(x))), p),
                                  Plus(d, Times(e, x)), x))),
                      Plus(
                          Simp(
                              Times($s("R"), Power(Plus(d, Times(e, x)), Plus(m, C1)),
                                  Power(Plus(f, Times(g, x)), Plus(n, C1)),
                                  Power(Times(Plus(m, C1), Subtract(Times(e, f), Times(d, g))),
                                      CN1)),
                              x),
                          Dist(Power(Times(Plus(m, C1), Subtract(Times(e, f), Times(d, g))), CN1),
                              Integrate(Times(Power(Plus(d, Times(e, x)), Plus(m, C1)),
                                  Power(Plus(f, Times(g, x)), n),
                                  ExpandToSum(Subtract(Times(Plus(m, C1),
                                      Subtract(Times(e, f), Times(d, g)), $s("§qx")),
                                      Times(g, $s("R"), Plus(m, n, C2))), x)),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, f, g), x),
                      NeQ(Subtract(Times(e, f), Times(d,
                          g)), C0),
                      NeQ(Subtract(Sqr(b), Times(C4, a,
                          c)), C0),
                      NeQ(Plus(Times(c, Sqr(
                          d)), Times(CN1, b, d,
                              e),
                          Times(a, Sqr(e))),
                          C0),
                      IGtQ(p, C0), LtQ(m, CN1)))),
          IIntegrate(950,
              Integrate(
                  Times(
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT,
                          x_)), m_),
                      Power(Plus(f_DEFAULT, Times(g_DEFAULT, x_)), n_), Power(
                          Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set($s("§qx"),
                          PolynomialQuotient(Power(Plus(a, Times(c, Sqr(x))), p),
                              Plus(d, Times(e, x)), x)),
                          Set($s("R"), PolynomialRemainder(Power(Plus(a, Times(c, Sqr(x))), p),
                              Plus(d, Times(e, x)), x))),
                      Plus(
                          Simp(
                              Times($s("R"), Power(Plus(d, Times(e, x)), Plus(m, C1)),
                                  Power(Plus(f, Times(g, x)), Plus(n, C1)),
                                  Power(Times(Plus(m, C1), Subtract(Times(e, f), Times(d, g))),
                                      CN1)),
                              x),
                          Dist(Power(Times(Plus(m, C1), Subtract(Times(e, f), Times(d, g))), CN1),
                              Integrate(Times(Power(Plus(d, Times(e, x)), Plus(m, C1)),
                                  Power(Plus(f, Times(g, x)), n),
                                  ExpandToSum(Subtract(Times(Plus(m, C1),
                                      Subtract(Times(e, f), Times(d, g)), $s("§qx")),
                                      Times(g, $s("R"), Plus(m, n, C2))), x)),
                                  x),
                              x))),
                  And(FreeQ(List(a, c, d, e, f, g), x), NeQ(Subtract(Times(e, f), Times(d, g)), C0),
                      NeQ(Plus(Times(c, Sqr(
                          d)), Times(a, Sqr(e))), C0),
                      IGtQ(p, C0), LtQ(m, CN1)))),
          IIntegrate(951,
              Integrate(
                  Times(
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT,
                          x_)), m_),
                      Power(Plus(f_DEFAULT, Times(g_DEFAULT, x_)), n_),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT,
                          Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(c, p), Power(Plus(d, Times(e, x)), Plus(m, Times(C2, p))),
                              Power(Plus(f, Times(g,
                                  x)), Plus(n,
                                      C1)),
                              Power(Times(g, Power(e, Times(C2, p)), Plus(m, n, Times(C2, p), C1)),
                                  CN1)),
                          x),
                      Dist(
                          Power(Times(g, Power(e, Times(C2,
                              p)), Plus(m, n, Times(C2, p),
                                  C1)),
                              CN1),
                          Integrate(
                              Times(
                                  Power(Plus(d, Times(e, x)), m), Power(Plus(
                                      f, Times(g, x)), n),
                                  ExpandToSum(
                                      Subtract(
                                          Times(g, Plus(m, n, Times(C2, p), C1),
                                              Subtract(
                                                  Times(Power(e, Times(C2, p)),
                                                      Power(Plus(a, Times(b, x), Times(c, Sqr(x))),
                                                          p)),
                                                  Times(Power(c, p),
                                                      Power(Plus(d, Times(e, x)), Times(C2, p))))),
                                          Times(Power(c, p), Subtract(Times(e, f), Times(d, g)),
                                              Plus(m, Times(C2, p)),
                                              Power(Plus(d, Times(e, x)),
                                                  Subtract(Times(C2, p), C1)))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g), x),
                      NeQ(Subtract(Times(e, f), Times(d, g)), C0), NeQ(Subtract(Sqr(b), Times(C4, a,
                          c)), C0),
                      NeQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))), C0),
                      IGtQ(p, C0), NeQ(Plus(m, n, Times(C2, p),
                          C1), C0),
                      Or(IntegerQ(n), Not(IntegerQ(m)))))),
          IIntegrate(952,
              Integrate(
                  Times(
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT,
                          x_)), m_),
                      Power(Plus(f_DEFAULT, Times(g_DEFAULT, x_)), n_), Power(
                          Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(c, p), Power(Plus(d, Times(e, x)), Plus(m, Times(C2, p))),
                              Power(Plus(f, Times(g, x)), Plus(n,
                                  C1)),
                              Power(
                                  Times(g, Power(e, Times(C2,
                                      p)), Plus(m, n, Times(C2, p),
                                          C1)),
                                  CN1)),
                          x),
                      Dist(
                          Power(Times(g, Power(e, Times(C2,
                              p)), Plus(m, n, Times(C2, p),
                                  C1)),
                              CN1),
                          Integrate(Times(Power(Plus(d, Times(e, x)), m),
                              Power(Plus(f, Times(g, x)), n), ExpandToSum(
                                  Subtract(
                                      Times(g, Plus(m, n, Times(C2, p), C1),
                                          Subtract(
                                              Times(Power(e, Times(C2, p)),
                                                  Power(Plus(a, Times(c, Sqr(x))), p)),
                                              Times(Power(c, p),
                                                  Power(Plus(d, Times(e, x)), Times(C2, p))))),
                                      Times(Power(c, p), Subtract(Times(e, f), Times(d, g)),
                                          Plus(m, Times(C2, p)),
                                          Power(Plus(d, Times(e, x)), Subtract(Times(C2, p), C1)))),
                                  x)),
                              x),
                          x)),
                  And(FreeQ(List(a, c, d, e, f, g), x), NeQ(Subtract(Times(e, f), Times(d, g)), C0),
                      NeQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), C0), IGtQ(p, C0),
                      NeQ(Plus(m, n, Times(C2, p), C1), C0), Or(IntegerQ(n), Not(IntegerQ(m)))))),
          IIntegrate(953, Integrate(Times(Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), CN1),
              Power(Plus(f_DEFAULT, Times(g_DEFAULT, x_)), n_), Power(Plus(a_DEFAULT,
                  Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_)),
              x_Symbol),
              Condition(Subtract(Dist(
                  Times(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))),
                      Power(Times(e, Subtract(Times(e, f), Times(d, g))), CN1)),
                  Integrate(Times(Power(Plus(f, Times(g, x)), Plus(n, C1)),
                      Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Subtract(p, C1)),
                      Power(Plus(d, Times(e, x)), CN1)), x),
                  x),
                  Dist(Power(Times(e, Subtract(Times(e, f), Times(d, g))), CN1),
                      Integrate(
                          Times(Power(Plus(f, Times(g, x)), n),
                              Subtract(Plus(Times(c, d, f), Times(CN1, b, e, f), Times(a, e, g)),
                                  Times(c, Subtract(Times(e, f), Times(d, g)), x)),
                              Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Subtract(p, C1))),
                          x),
                      x)),
                  And(FreeQ(List(a, b, c, d, e, f, g), x),
                      NeQ(Subtract(Times(e, f), Times(d,
                          g)), C0),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      NeQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a,
                          Sqr(e))), C0),
                      Not(IntegerQ(n)), Not(IntegerQ(p)), GtQ(p, C0), LtQ(n, CN1)))),
          IIntegrate(954,
              Integrate(
                  Times(Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), CN1),
                      Power(Plus(f_DEFAULT,
                          Times(g_DEFAULT, x_)), n_),
                      Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(
                          Times(
                              Plus(Times(c, Sqr(d)), Times(a, Sqr(e))),
                              Power(Times(e, Subtract(Times(e, f), Times(d, g))), CN1)),
                          Integrate(
                              Times(Power(Plus(f, Times(g, x)), Plus(n, C1)),
                                  Power(Plus(a, Times(c, Sqr(x))), Subtract(p, C1)), Power(
                                      Plus(d, Times(e, x)), CN1)),
                              x),
                          x),
                      Dist(Power(Times(e, Subtract(Times(e, f), Times(d, g))), CN1),
                          Integrate(Times(Power(Plus(f, Times(g, x)), n),
                              Subtract(Plus(Times(c, d, f), Times(a, e, g)),
                                  Times(c, Subtract(Times(e, f), Times(d, g)), x)),
                              Power(Plus(a, Times(c, Sqr(x))), Subtract(p, C1))), x),
                          x)),
                  And(FreeQ(List(a, c, d, e, f, g), x), NeQ(Subtract(Times(e, f), Times(d, g)), C0),
                      NeQ(Plus(Times(c, Sqr(d)), Times(a,
                          Sqr(e))), C0),
                      Not(IntegerQ(n)), Not(IntegerQ(p)), GtQ(p, C0), LtQ(n, CN1)))),
          IIntegrate(955,
              Integrate(
                  Times(Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), CN1),
                      Power(Plus(f_DEFAULT, Times(g_DEFAULT, x_)), n_), Power(Plus(a_DEFAULT,
                          Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))),
                          p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(
                          Times(e, Subtract(Times(e, f), Times(d, g)),
                              Power(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))),
                                  CN1)),
                          Integrate(Times(Power(Plus(f, Times(g, x)), Subtract(n, C1)),
                              Power(Plus(a, Times(b, x), Times(c, Sqr(x))),
                                  Plus(p, C1)),
                              Power(Plus(d, Times(e, x)), CN1)), x),
                          x),
                      Dist(
                          Power(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))), CN1),
                          Integrate(Times(Power(Plus(f, Times(g, x)), Subtract(n, C1)),
                              Subtract(
                                  Plus(Times(c, d, f), Times(CN1, b, e, f), Times(a, e, g)), Times(
                                      c, Subtract(Times(e, f), Times(d, g)), x)),
                              Power(Plus(a, Times(b, x), Times(c, Sqr(x))), p)), x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g), x),
                      NeQ(Subtract(Times(e, f), Times(d, g)), C0),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      NeQ(Plus(Times(c, Sqr(
                          d)), Times(CN1, b, d,
                              e),
                          Times(a, Sqr(e))), C0),
                      Not(IntegerQ(n)), Not(IntegerQ(p)), LtQ(p, CN1), GtQ(n, C0)))),
          IIntegrate(956,
              Integrate(
                  Times(Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), CN1),
                      Power(Plus(f_DEFAULT, Times(g_DEFAULT, x_)), n_), Power(
                          Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(
                          Times(e, Subtract(Times(e, f), Times(d, g)),
                              Power(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), CN1)),
                          Integrate(
                              Times(Power(Plus(f, Times(g, x)), Subtract(n, C1)),
                                  Power(Plus(a, Times(c, Sqr(x))),
                                      Plus(p, C1)),
                                  Power(Plus(d, Times(e, x)), CN1)),
                              x),
                          x),
                      Dist(Power(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), CN1),
                          Integrate(Times(Power(Plus(f, Times(g, x)), Subtract(n, C1)),
                              Subtract(Plus(Times(c, d, f), Times(a, e, g)),
                                  Times(c, Subtract(Times(e, f), Times(d, g)), x)),
                              Power(Plus(a, Times(c, Sqr(x))), p)), x),
                          x)),
                  And(FreeQ(List(a, c, d, e, f, g), x), NeQ(Subtract(Times(e, f), Times(d, g)), C0),
                      NeQ(Plus(Times(c, Sqr(
                          d)), Times(a,
                              Sqr(e))),
                          C0),
                      Not(IntegerQ(n)), Not(IntegerQ(p)), LtQ(p, CN1), GtQ(n, C0)))),
          IIntegrate(957,
              Integrate(
                  Times(Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), CN1),
                      Power(Plus(f_DEFAULT, Times(g_DEFAULT, x_)), n_),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))),
                          CN1D2)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Power(
                              Times(Sqrt(Plus(f, Times(g, x))),
                                  Sqrt(Plus(a, Times(b, x), Times(c, Sqr(x))))),
                              CN1),
                          Times(
                              Power(Plus(f, Times(g, x)),
                                  Plus(n, C1D2)),
                              Power(Plus(d, Times(e, x)), CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g), x),
                      NeQ(Subtract(Times(e, f), Times(d,
                          g)), C0),
                      NeQ(Subtract(Sqr(b), Times(C4, a,
                          c)), C0),
                      NeQ(Plus(Times(c, Sqr(
                          d)), Times(CN1, b, d,
                              e),
                          Times(a, Sqr(e))), C0),
                      IntegerQ(Plus(n, C1D2))))),
          IIntegrate(958,
              Integrate(
                  Times(Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), CN1),
                      Power(Plus(f_DEFAULT, Times(g_DEFAULT, x_)), n_), Power(
                          Plus(a_, Times(c_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Power(
                              Times(Sqrt(Plus(f, Times(g, x))),
                                  Sqrt(Plus(a, Times(c, Sqr(x))))),
                              CN1),
                          Times(
                              Power(Plus(f, Times(g, x)),
                                  Plus(n, C1D2)),
                              Power(Plus(d, Times(e, x)), CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, c, d, e, f, g), x), NeQ(Subtract(Times(e, f), Times(d,
                      g)), C0), NeQ(Plus(Times(c, Sqr(d)),
                          Times(a, Sqr(e))), C0),
                      IntegerQ(Plus(n, C1D2))))),
          IIntegrate(959,
              Integrate(
                  Times(Power(Times(g_DEFAULT, x_), n_DEFAULT),
                      Power(Plus(d_,
                          Times(e_DEFAULT, x_)), CN1),
                      Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Times(d, Power(Times(g, x), n), Power(Power(x, n), CN1)),
                          Integrate(
                              Times(
                                  Power(x, n), Power(Plus(a, Times(c, Sqr(x))), p), Power(
                                      Subtract(Sqr(d), Times(Sqr(e), Sqr(x))), CN1)),
                              x),
                          x),
                      Dist(Times(e, Power(Times(g, x), n), Power(Power(x, n), CN1)),
                          Integrate(
                              Times(Power(x, Plus(n, C1)), Power(Plus(a, Times(c, Sqr(x))), p),
                                  Power(Subtract(Sqr(d), Times(Sqr(e), Sqr(x))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, c, d, e, g, n, p), x),
                      NeQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), C0), Not(IntegerQ(p)),
                      Not(IntegersQ(n, Times(C2, p)))))),
          IIntegrate(960,
              Integrate(Times(Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_),
                  Power(Plus(f_DEFAULT, Times(g_DEFAULT, x_)), n_),
                  Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))),
                      p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(ExpandIntegrand(
                      Times(Power(Plus(d, Times(e, x)), m), Power(Plus(f, Times(g, x)), n),
                          Power(Plus(a, Times(b, x), Times(c, Sqr(x))), p)),
                      x), x),
                  And(FreeQ(List(a, b, c, d, e, f, g), x),
                      NeQ(Subtract(Times(e, f), Times(d, g)), C0),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      NeQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))), C0),
                      Or(IntegerQ(p), And(ILtQ(m, C0), ILtQ(n, C0))),
                      Not(Or(IGtQ(m, C0), IGtQ(n, C0)))))));
}
