package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.CN4;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Denominator;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.PolynomialQuotient;
import static org.matheclipse.core.expression.F.PolynomialRemainder;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.Unequal;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.q_;
import static org.matheclipse.core.expression.F.q_DEFAULT;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.f;
import static org.matheclipse.core.expression.S.k;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.q;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Coeff;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandToSum;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FractionQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
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
class IntRules63 {
  public static IAST RULES =
      List(
          IIntegrate(1261,
              Integrate(
                  Times(Power(Times(f_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Sqr(x_)), Times(c_DEFAULT, Power(x_, C4))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(Times(Power(Times(f, x), m),
                          Power(Plus(d, Times(e, Sqr(x))), q), Power(
                              Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, q), x), NeQ(Subtract(Sqr(b),
                      Times(C4, a, c)), C0), IGtQ(p,
                          C0),
                      IGtQ(q, CN2)))),
          IIntegrate(1262,
              Integrate(
                  Times(Power(Times(f_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_DEFAULT), Power(Plus(a_,
                          Times(c_DEFAULT, Power(x_, C4))), p_DEFAULT)),
                  x_Symbol),
              Condition(Integrate(
                  ExpandIntegrand(Times(Power(Times(f, x), m), Power(Plus(d, Times(e, Sqr(x))), q),
                      Power(Plus(a, Times(c, Power(x, C4))), p)), x),
                  x),
                  And(FreeQ(List(a, c, d, e, f, m, q), x), IGtQ(p, C0), IGtQ(q, CN2)))),
          IIntegrate(1263,
              Integrate(
                  Times(Power(Times(f_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(d_,
                          Times(e_DEFAULT, Sqr(x_))), q_),
                      Power(
                          Plus(a_, Times(b_DEFAULT, Sqr(
                              x_)), Times(c_DEFAULT,
                                  Power(x_, C4))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set($s("§qx"),
                              PolynomialQuotient(
                                  Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))),
                                      p),
                                  Plus(d, Times(e, Sqr(x))), x)),
                          Set($s("R"),
                              Coeff(
                                  PolynomialRemainder(
                                      Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), p),
                                      Plus(d, Times(e, Sqr(x))), x),
                                  x, C0))),
                      Plus(
                          Negate(Simp(Times($s("R"), Power(Times(f, x), Plus(m, C1)),
                              Power(Plus(d, Times(e, Sqr(x))), Plus(q, C1)),
                              Power(Times(C2, d, f, Plus(q, C1)), CN1)), x)),
                          Dist(Times(f, Power(Times(C2, d, Plus(q, C1)), CN1)),
                              Integrate(
                                  Times(Power(Times(f, x), Subtract(m, C1)),
                                      Power(Plus(d, Times(e, Sqr(x))), Plus(q, C1)),
                                      ExpandToSum(
                                          Plus(Times(C2, d, Plus(q, C1), x, $s("§qx")),
                                              Times($s("R"), Plus(m, Times(C2, q), C3), x)),
                                          x)),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, f), x), NeQ(Subtract(Sqr(b),
                      Times(C4, a, c)), C0), IGtQ(p,
                          C0),
                      LtQ(q, CN1), GtQ(m, C0)))),
          IIntegrate(1264,
              Integrate(Times(Power(Times(f_DEFAULT, x_), m_DEFAULT),
                  Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_),
                  Power(Plus(a_, Times(c_DEFAULT, Power(x_, C4))), p_DEFAULT)), x_Symbol),
              Condition(
                  With(
                      List(
                          Set($s("§qx"),
                              PolynomialQuotient(
                                  Power(Plus(a, Times(c, Power(x, C4))), p), Plus(d,
                                      Times(e, Sqr(x))),
                                  x)),
                          Set($s("R"),
                              Coeff(
                                  PolynomialRemainder(Power(Plus(a, Times(c, Power(x, C4))), p),
                                      Plus(d, Times(e, Sqr(x))), x),
                                  x, C0))),
                      Plus(
                          Negate(
                              Simp(Times($s("R"), Power(Times(f, x), Plus(m, C1)),
                                  Power(Plus(d, Times(e, Sqr(x))), Plus(q, C1)),
                                  Power(Times(C2, d, f, Plus(q, C1)), CN1)), x)),
                          Dist(Times(f, Power(Times(C2, d, Plus(q, C1)), CN1)),
                              Integrate(
                                  Times(Power(Times(f, x), Subtract(m, C1)),
                                      Power(Plus(d, Times(e, Sqr(x))), Plus(q, C1)),
                                      ExpandToSum(
                                          Plus(Times(C2, d, Plus(q, C1), x, $s("§qx")),
                                              Times($s("R"), Plus(m, Times(C2, q), C3), x)),
                                          x)),
                                  x),
                              x))),
                  And(FreeQ(List(a, c, d, e, f), x), IGtQ(p, C0), LtQ(q, CN1), GtQ(m, C0)))),
          IIntegrate(1265,
              Integrate(
                  Times(Power(Times(f_DEFAULT, x_), m_),
                      Power(Plus(d_,
                          Times(e_DEFAULT, Sqr(x_))), q_DEFAULT),
                      Power(
                          Plus(a_, Times(b_DEFAULT, Sqr(x_)),
                              Times(c_DEFAULT, Power(x_, C4))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(List(
                      Set($s("§qx"),
                          PolynomialQuotient(
                              Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), p), Times(f,
                                  x),
                              x)),
                      Set($s("R"),
                          PolynomialRemainder(
                              Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), p),
                              Times(f, x), x))),
                      Plus(Simp(Times($s("R"), Power(Times(f, x), Plus(m, C1)),
                          Power(Plus(d, Times(e, Sqr(x))),
                              Plus(q, C1)),
                          Power(Times(d, f, Plus(m, C1)), CN1)), x),
                          Dist(
                              Power(Times(d, Sqr(f),
                                  Plus(m, C1)), CN1),
                              Integrate(Times(Power(Times(f, x), Plus(m, C2)),
                                  Power(Plus(d, Times(e, Sqr(x))), q),
                                  ExpandToSum(
                                      Subtract(Times(d, f, Plus(m, C1), $s("§qx"), Power(x, CN1)),
                                          Times(e, $s("R"), Plus(m, Times(C2, q), C3))),
                                      x)),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, f, q), x), NeQ(Subtract(Sqr(b),
                      Times(C4, a, c)), C0), IGtQ(p,
                          C0),
                      LtQ(m, CN1)))),
          IIntegrate(1266,
              Integrate(Times(Power(Times(f_DEFAULT, x_), m_),
                  Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_DEFAULT),
                  Power(Plus(a_, Times(c_DEFAULT, Power(x_, C4))), p_DEFAULT)), x_Symbol),
              Condition(With(List(Set(
                  $s("§qx"),
                  PolynomialQuotient(Power(Plus(a, Times(c, Power(x, C4))), p), Times(f, x), x)),
                  Set($s(
                      "R"),
                      PolynomialRemainder(Power(Plus(a, Times(c, Power(x, C4))), p), Times(f, x),
                          x))),
                  Plus(
                      Simp(Times($s("R"), Power(Times(f, x), Plus(m, C1)),
                          Power(Plus(d, Times(e, Sqr(x))),
                              Plus(q, C1)),
                          Power(Times(d, f, Plus(m, C1)), CN1)), x),
                      Dist(Power(Times(d, Sqr(f), Plus(m, C1)), CN1), Integrate(Times(
                          Power(Times(f, x), Plus(m, C2)), Power(Plus(d, Times(e, Sqr(x))), q),
                          ExpandToSum(Subtract(Times(d, f, Plus(m, C1), $s("§qx"), Power(x, CN1)),
                              Times(e, $s("R"), Plus(m, Times(C2, q), C3))), x)),
                          x), x))),
                  And(FreeQ(List(a, c, d, e, f, q), x), IGtQ(p, C0), LtQ(m, CN1)))),
          IIntegrate(1267,
              Integrate(
                  Times(Power(Times(f_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(d_,
                          Times(e_DEFAULT, Sqr(x_))), q_DEFAULT),
                      Power(
                          Plus(a_, Times(b_DEFAULT, Sqr(x_)),
                              Times(c_DEFAULT, Power(x_, C4))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(c, p),
                              Power(Times(f, x), Subtract(Plus(m, Times(C4, p)), C1)),
                              Power(Plus(d, Times(e,
                                  Sqr(x))), Plus(q,
                                      C1)),
                              Power(
                                  Times(
                                      e, Power(f, Subtract(Times(C4, p),
                                          C1)),
                                      Plus(m, Times(C4, p), Times(C2, q), C1)),
                                  CN1)),
                          x),
                      Dist(Power(Times(e, Plus(m, Times(C4, p), Times(C2, q), C1)), CN1),
                          Integrate(
                              Times(Power(Times(f, x), m), Power(Plus(d,
                                  Times(e, Sqr(x))), q), ExpandToSum(
                                      Subtract(
                                          Times(e, Plus(m, Times(C4, p), Times(C2, q), C1),
                                              Subtract(
                                                  Power(Plus(a, Times(b, Sqr(x)),
                                                      Times(c, Power(x, C4))), p),
                                                  Times(Power(c, p), Power(x, Times(C4, p))))),
                                          Times(d, Power(c, p), Subtract(Plus(m, Times(C4, p)), C1),
                                              Power(x, Subtract(Times(C4, p), C2)))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, m, q), x),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0), IGtQ(p, C0), Not(IntegerQ(
                          q)),
                      NeQ(Plus(m, Times(C4, p), Times(C2, q), C1), C0)))),
          IIntegrate(1268,
              Integrate(
                  Times(Power(Times(f_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT,
                          Sqr(x_))), q_DEFAULT),
                      Power(Plus(a_, Times(c_DEFAULT, Power(x_, C4))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(c, p),
                              Power(Times(f, x), Subtract(Plus(m, Times(C4, p)), C1)),
                              Power(Plus(d, Times(e, Sqr(x))), Plus(q,
                                  C1)),
                              Power(
                                  Times(e, Power(f, Subtract(Times(C4, p), C1)),
                                      Plus(m, Times(C4, p), Times(C2, q), C1)),
                                  CN1)),
                          x),
                      Dist(Power(Times(e, Plus(m, Times(C4, p), Times(C2, q), C1)), CN1),
                          Integrate(
                              Times(Power(Times(f, x), m), Power(Plus(d, Times(e, Sqr(x))), q),
                                  ExpandToSum(Subtract(
                                      Times(e, Plus(m, Times(C4, p), Times(C2, q), C1),
                                          Subtract(Power(Plus(a, Times(c, Power(x, C4))), p),
                                              Times(Power(c, p), Power(x, Times(C4, p))))),
                                      Times(d, Power(c, p), Subtract(Plus(m, Times(C4, p)), C1),
                                          Power(x, Subtract(Times(C4, p), C2)))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, c, d, e, f, m, q), x), IGtQ(p, C0), Not(IntegerQ(
                      q)), NeQ(Plus(m, Times(C4, p), Times(C2, q), C1),
                          C0)))),
          IIntegrate(1269,
              Integrate(
                  Times(Power(Times(f_DEFAULT, x_), m_),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Sqr(x_)), Times(c_DEFAULT, Power(x_, C4))),
                          p_)),
                  x_Symbol),
              Condition(With(List(Set(k, Denominator(m))),
                  Dist(Times(k, Power(f, CN1)), Subst(
                      Integrate(Times(Power(x, Subtract(Times(k, Plus(m, C1)), C1)),
                          Power(Plus(d, Times(e, Power(x, Times(C2, k)), Power(f, CN2))), q),
                          Power(Plus(a, Times(b, Power(x, Times(C2, k)), Power(Power(f, k), CN1)),
                              Times(c, Power(x, Times(C4, k)), Power(f, CN4))), p)),
                          x),
                      x, Power(Times(f, x), Power(k, CN1))), x)),
                  And(FreeQ(List(a, b, c, d, e, f, p,
                      q), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)),
                          C0),
                      FractionQ(m), IntegerQ(p)))),
          IIntegrate(1270,
              Integrate(
                  Times(Power(Times(f_DEFAULT, x_), m_),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_DEFAULT),
                      Power(Plus(a_, Times(c_DEFAULT, Power(x_, C4))), p_)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(k,
                          Denominator(m))),
                      Dist(Times(k, Power(f, CN1)),
                          Subst(
                              Integrate(Times(Power(x, Subtract(Times(k, Plus(m, C1)), C1)),
                                  Power(Plus(d, Times(e, Power(x, Times(C2, k)), Power(f, CN1))),
                                      q),
                                  Power(Plus(a, Times(c, Power(x, Times(C4, k)), Power(f, CN1))),
                                      p)),
                                  x),
                              x, Power(Times(f, x), Power(k, CN1))),
                          x)),
                  And(FreeQ(List(a, c, d, e, f, p, q), x), FractionQ(m), IntegerQ(p)))),
          IIntegrate(1271,
              Integrate(
                  Times(
                      Power(Times(f_DEFAULT, x_), m_DEFAULT), Plus(d_, Times(
                          e_DEFAULT, Sqr(x_))),
                      Power(
                          Plus(a_, Times(b_DEFAULT, Sqr(x_)),
                              Times(c_DEFAULT, Power(x_, C4))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Times(f, x), Plus(m, C1)),
                              Power(Plus(a, Times(b, Sqr(
                                  x)), Times(c,
                                      Power(x, C4))),
                                  p),
                              Plus(
                                  Times(d, Plus(m, Times(C4, p), C3)), Times(e, Plus(m, C1), Sqr(
                                      x))),
                              Power(Times(f, Plus(m, C1), Plus(m, Times(C4, p), C3)), CN1)),
                          x),
                      Dist(
                          Times(C2, p,
                              Power(Times(Sqr(f), Plus(m, C1), Plus(m, Times(C4, p), C3)), CN1)),
                          Integrate(Times(Power(Times(f, x), Plus(m, C2)),
                              Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))),
                                  Subtract(p, C1)),
                              Simp(
                                  Plus(Times(C2, a, e, Plus(m, C1)),
                                      Times(CN1, b, d, Plus(m, Times(C4, p), C3)),
                                      Times(Subtract(Times(b, e, Plus(m, C1)),
                                          Times(C2, c, d, Plus(m, Times(C4, p), C3))), Sqr(x))),
                                  x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      GtQ(p, C0), LtQ(m, CN1), Unequal(Plus(m, Times(C4,
                          p), C3), C0),
                      IntegerQ(Times(C2, p)), Or(IntegerQ(p), IntegerQ(m))))),
          IIntegrate(1272,
              Integrate(
                  Times(
                      Power(Times(f_DEFAULT, x_), m_DEFAULT), Plus(d_, Times(e_DEFAULT,
                          Sqr(x_))),
                      Power(Plus(a_, Times(c_DEFAULT, Power(x_, C4))), p_DEFAULT)),
                  x_Symbol),
              Condition(Plus(
                  Simp(Times(
                      Power(Times(f, x), Plus(m, C1)), Power(Plus(a, Times(c, Power(x, C4))), p),
                      Plus(Times(d, Plus(m, Times(C4, p), C3)), Times(e, Plus(m, C1), Sqr(x))),
                      Power(Times(f, Plus(m, C1), Plus(m, Times(C4, p), C3)), CN1)), x),
                  Dist(
                      Times(C4, p,
                          Power(Times(Sqr(f), Plus(m, C1), Plus(m, Times(C4, p), C3)), CN1)),
                      Integrate(Times(Power(Times(f, x), Plus(m, C2)),
                          Power(Plus(a, Times(c, Power(x, C4))), Subtract(p, C1)),
                          Subtract(Times(a, e, Plus(m, C1)),
                              Times(c, d, Plus(m, Times(C4, p), C3), Sqr(x)))),
                          x),
                      x)),
                  And(FreeQ(List(a, c, d, e, f), x), GtQ(p, C0), LtQ(m, CN1),
                      Unequal(Plus(m, Times(C4, p), C3),
                          C0),
                      IntegerQ(Times(C2, p)), Or(IntegerQ(p), IntegerQ(m))))),
          IIntegrate(1273,
              Integrate(
                  Times(Power(Times(f_DEFAULT, x_), m_DEFAULT), Plus(d_, Times(e_DEFAULT, Sqr(x_))),
                      Power(Plus(a_, Times(b_DEFAULT, Sqr(x_)), Times(c_DEFAULT, Power(x_, C4))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(Times(Power(Times(f, x), Plus(m, C1)),
                          Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), p),
                          Plus(
                              Times(b, e, C2, p), Times(c, d, Plus(m, Times(C4, p), C3)), Times(c,
                                  e, Plus(Times(C4, p), m, C1), Sqr(x))),
                          Power(
                              Times(c, f, Plus(Times(C4, p), m, C1),
                                  Plus(m, Times(C4, p), C3)),
                              CN1)),
                          x),
                      Dist(
                          Times(C2, p,
                              Power(
                                  Times(c, Plus(Times(C4, p), m, C1),
                                      Plus(m, Times(C4, p), C3)),
                                  CN1)),
                          Integrate(
                              Times(
                                  Power(Times(f,
                                      x), m),
                                  Power(
                                      Plus(a, Times(b, Sqr(
                                          x)), Times(c,
                                              Power(x, C4))),
                                      Subtract(p, C1)),
                                  Simp(Plus(Times(C2, a, c, d, Plus(m, Times(C4, p), C3)),
                                      Times(CN1, a, b, e, Plus(m, C1)),
                                      Times(
                                          Subtract(
                                              Plus(Times(C2, a, c, e, Plus(Times(C4, p), m, C1)),
                                                  Times(b, c, d, Plus(m, Times(C4, p), C3))),
                                              Times(Sqr(b), e, Plus(m, Times(C2, p), C1))),
                                          Sqr(x))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, m), x),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0), GtQ(p, C0),
                      NeQ(Plus(Times(C4, p), m, C1), C0), NeQ(Plus(m, Times(C4,
                          p), C3), C0),
                      IntegerQ(Times(C2, p)), Or(IntegerQ(p), IntegerQ(m))))),
          IIntegrate(1274,
              Integrate(
                  Times(
                      Power(Times(f_DEFAULT, x_), m_DEFAULT), Plus(d_, Times(e_DEFAULT,
                          Sqr(x_))),
                      Power(Plus(a_, Times(c_DEFAULT, Power(x_, C4))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Times(f, x), Plus(m, C1)),
                              Power(Plus(a, Times(c, Power(x, C4))), p),
                              Plus(
                                  Times(c, d, Plus(m, Times(C4, p), C3)), Times(c, e,
                                      Plus(Times(C4, p), m, C1), Sqr(x))),
                              Power(
                                  Times(c, f, Plus(Times(C4, p), m, C1),
                                      Plus(m, Times(C4, p), C3)),
                                  CN1)),
                          x),
                      Dist(
                          Times(C4, a, p,
                              Power(Times(Plus(Times(C4, p), m, C1), Plus(m, Times(C4, p), C3)),
                                  CN1)),
                          Integrate(Times(Power(Times(f, x), m),
                              Power(Plus(a, Times(c, Power(x, C4))), Subtract(p, C1)),
                              Simp(Plus(Times(d, Plus(m, Times(C4, p), C3)),
                                  Times(e, Plus(Times(C4, p), m, C1), Sqr(x))), x)),
                              x),
                          x)),
                  And(FreeQ(List(a, c, d, e, f, m), x), GtQ(p, C0),
                      NeQ(Plus(Times(C4, p), m, C1), C0), NeQ(Plus(m, Times(C4, p),
                          C3), C0),
                      IntegerQ(Times(C2, p)), Or(IntegerQ(p), IntegerQ(m))))),
          IIntegrate(1275,
              Integrate(
                  Times(
                      Power(Times(f_DEFAULT, x_), m_DEFAULT), Plus(d_, Times(e_DEFAULT,
                          Sqr(x_))),
                      Power(
                          Plus(a_, Times(b_DEFAULT, Sqr(x_)),
                              Times(c_DEFAULT, Power(x_, C4))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              f, Power(Times(f, x), Subtract(m,
                                  C1)),
                              Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), Plus(p, C1)),
                              Subtract(Subtract(Times(b, d), Times(C2, a, e)),
                                  Times(Subtract(Times(b, e), Times(C2, c, d)), Sqr(x))),
                              Power(Times(C2, Plus(p, C1), Subtract(Sqr(b), Times(C4, a, c))),
                                  CN1)),
                          x),
                      Dist(
                          Times(Sqr(f),
                              Power(Times(C2, Plus(p, C1), Subtract(Sqr(b), Times(C4, a, c))),
                                  CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Subtract(m, C2)),
                                  Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))),
                                      Plus(p, C1)),
                                  Simp(
                                      Subtract(
                                          Times(Subtract(m, C1),
                                              Subtract(Times(b, d), Times(C2, a, e))),
                                          Times(Plus(Times(C4, p), C4, m, C1),
                                              Subtract(Times(b, e), Times(C2, c, d)), Sqr(x))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      LtQ(p, CN1), GtQ(m, C1), IntegerQ(Times(C2,
                          p)),
                      Or(IntegerQ(p), IntegerQ(m))))),
          IIntegrate(1276,
              Integrate(Times(Power(Times(f_DEFAULT, x_), m_DEFAULT),
                  Plus(d_, Times(e_DEFAULT, Sqr(x_))), Power(
                      Plus(a_, Times(c_DEFAULT, Power(x_, C4))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(Times(f, Power(Times(f, x), Subtract(m, C1)),
                          Power(Plus(a, Times(c, Power(x, C4))), Plus(p, C1)),
                          Subtract(Times(a, e), Times(c, d, Sqr(x))),
                          Power(Times(C4, a, c, Plus(p, C1)), CN1)), x),
                      Dist(Times(Sqr(f), Power(Times(C4, a, c, Plus(p, C1)), CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Subtract(m, C2)),
                                  Power(Plus(a, Times(c, Power(x, C4))), Plus(p, C1)),
                                  Subtract(Times(a, e, Subtract(m, C1)),
                                      Times(c, d, Plus(Times(C4, p), C4, m, C1), Sqr(x)))),
                              x),
                          x)),
                  And(FreeQ(List(a, c, d, e, f), x), LtQ(p, CN1), GtQ(m, C1), IntegerQ(Times(C2,
                      p)), Or(IntegerQ(p),
                          IntegerQ(m))))),
          IIntegrate(1277,
              Integrate(
                  Times(Power(Times(f_DEFAULT, x_), m_DEFAULT), Plus(d_, Times(e_DEFAULT, Sqr(x_))),
                      Power(
                          Plus(a_, Times(b_DEFAULT, Sqr(x_)),
                              Times(c_DEFAULT, Power(x_, C4))),
                          p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(
                                  Power(Times(f, x), Plus(m,
                                      C1)),
                                  Power(
                                      Plus(a, Times(b, Sqr(
                                          x)), Times(c,
                                              Power(x, C4))),
                                      Plus(p, C1)),
                                  Plus(Times(d, Subtract(Sqr(b), Times(C2, a, c))),
                                      Times(CN1, a, b, e), Times(Subtract(Times(b, d),
                                          Times(C2, a, e)), c, Sqr(x))),
                                  Power(
                                      Times(C2, a, f, Plus(p, C1),
                                          Subtract(Sqr(b), Times(C4, a, c))),
                                      CN1)),
                              x)),
                      Dist(
                          Power(Times(C2, a, Plus(p, C1),
                              Subtract(Sqr(b), Times(C4, a, c))), CN1),
                          Integrate(
                              Times(
                                  Power(Times(f,
                                      x), m),
                                  Power(
                                      Plus(a, Times(b, Sqr(x)), Times(c,
                                          Power(x, C4))),
                                      Plus(p, C1)),
                                  Simp(Plus(
                                      Times(d,
                                          Subtract(
                                              Times(Sqr(b), Plus(m, Times(C2, Plus(p, C1)), C1)),
                                              Times(C2, a, c,
                                                  Plus(m, Times(C4, Plus(p, C1)), C1)))),
                                      Times(CN1, a, b, e, Plus(m, C1)),
                                      Times(
                                          c, Plus(m, Times(C2, Plus(Times(C2, p), C3)),
                                              C1),
                                          Subtract(Times(b, d), Times(C2, a, e)), Sqr(x))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, m), x),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0), LtQ(p, CN1), IntegerQ(Times(C2,
                          p)),
                      Or(IntegerQ(p), IntegerQ(m))))),
          IIntegrate(1278,
              Integrate(
                  Times(
                      Power(Times(f_DEFAULT, x_), m_DEFAULT), Plus(d_, Times(e_DEFAULT,
                          Sqr(x_))),
                      Power(Plus(a_, Times(c_DEFAULT, Power(x_, C4))), p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(Power(Times(f, x), Plus(m, C1)),
                                  Power(Plus(a, Times(c, Power(x, C4))), Plus(p, C1)),
                                  Plus(d, Times(e, Sqr(x))), Power(Times(C4, a, f, Plus(p, C1)),
                                      CN1)),
                              x)),
                      Dist(
                          Power(Times(C4, a,
                              Plus(p, C1)), CN1),
                          Integrate(
                              Times(Power(Times(f, x), m),
                                  Power(Plus(a, Times(c, Power(x, C4))), Plus(p, C1)),
                                  Simp(
                                      Plus(Times(d, Plus(m, Times(C4, Plus(p, C1)), C1)),
                                          Times(e, Plus(m, Times(C2, Plus(Times(C2, p), C3)), C1),
                                              Sqr(x))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, c, d, e, f, m), x), LtQ(p, CN1), IntegerQ(Times(C2,
                      p)), Or(IntegerQ(p),
                          IntegerQ(m))))),
          IIntegrate(1279,
              Integrate(
                  Times(
                      Power(Times(f_DEFAULT, x_), m_DEFAULT), Plus(d_, Times(e_DEFAULT,
                          Sqr(x_))),
                      Power(Plus(a_, Times(b_DEFAULT, Sqr(x_)), Times(c_DEFAULT, Power(x_, C4))),
                          p_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(e, f, Power(Times(f, x), Subtract(m, C1)),
                              Power(Plus(a, Times(b, Sqr(x)), Times(c,
                                  Power(x, C4))), Plus(p,
                                      C1)),
                              Power(Times(c, Plus(m, Times(C4, p), C3)), CN1)),
                          x),
                      Dist(Times(Sqr(f), Power(Times(c, Plus(m, Times(C4, p), C3)), CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Subtract(m, C2)),
                                  Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), p),
                                  Simp(
                                      Plus(Times(a, e, Subtract(m, C1)),
                                          Times(Subtract(Times(b, e, Plus(m, Times(C2, p), C1)),
                                              Times(c, d, Plus(m, Times(C4, p), C3))), Sqr(x))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, p), x),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0), GtQ(m, C1),
                      NeQ(Plus(m, Times(C4,
                          p), C3), C0),
                      IntegerQ(Times(C2, p)), Or(IntegerQ(p), IntegerQ(m))))),
          IIntegrate(1280,
              Integrate(
                  Times(
                      Power(Times(f_DEFAULT, x_), m_DEFAULT), Plus(d_, Times(e_DEFAULT,
                          Sqr(x_))),
                      Power(Plus(a_, Times(c_DEFAULT, Power(x_, C4))), p_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(Times(e, f, Power(Times(f, x), Subtract(m, C1)),
                          Power(Plus(a, Times(c, Power(x, C4))), Plus(p, C1)),
                          Power(Times(c, Plus(m, Times(C4, p), C3)), CN1)), x),
                      Dist(Times(Sqr(f), Power(Times(c, Plus(m, Times(C4, p), C3)), CN1)),
                          Integrate(Times(Power(Times(f, x), Subtract(m, C2)),
                              Power(Plus(a, Times(c, Power(x, C4))), p),
                              Subtract(Times(a, e, Subtract(m, C1)),
                                  Times(c, d, Plus(m, Times(C4, p), C3), Sqr(x)))),
                              x),
                          x)),
                  And(FreeQ(List(a, c, d, e, f, p), x), GtQ(m, C1),
                      NeQ(Plus(m, Times(C4, p), C3), C0), IntegerQ(Times(C2, p)),
                      Or(IntegerQ(p), IntegerQ(m))))));
}
