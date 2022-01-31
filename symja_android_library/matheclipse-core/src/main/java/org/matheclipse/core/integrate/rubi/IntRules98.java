package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Denominator;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.PolynomialQ;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.ReplaceAll;
import static org.matheclipse.core.expression.F.Rule;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.g_DEFAULT;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.q_DEFAULT;
import static org.matheclipse.core.expression.F.r_DEFAULT;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.v_;
import static org.matheclipse.core.expression.F.w_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.F.z_;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.g;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.q;
import static org.matheclipse.core.expression.S.r;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.w;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.expression.S.z;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.BinomialDegree;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.BinomialMatchQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.BinomialQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandToSum;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FractionQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GeneralizedBinomialMatchQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GeneralizedBinomialQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntegersQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PolyQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SimplifyIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules98 {
  public static IAST RULES =
      List(
          IIntegrate(1961,
              Integrate(
                  Times(Power(u_, r_DEFAULT),
                      Power(Times(e_DEFAULT,
                          Plus(a_DEFAULT, Times(b_DEFAULT, Power(x_, n_DEFAULT))), Power(Plus(c_,
                              Times(d_DEFAULT, Power(x_, n_DEFAULT))),
                              CN1)),
                          p_)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(q,
                          Denominator(p))),
                      Dist(
                          Times(q, e, Subtract(Times(b, c), Times(a, d)), Power(n,
                              CN1)),
                          Subst(
                              Integrate(
                                  SimplifyIntegrand(
                                      Times(
                                          Power(x, Subtract(Times(q, Plus(p, C1)),
                                              C1)),
                                          Power(
                                              Plus(Times(CN1, a, e), Times(c,
                                                  Power(x, q))),
                                              Subtract(Power(n, CN1), C1)),
                                          Power(
                                              ReplaceAll(
                                                  u,
                                                  Rule(
                                                      x,
                                                      Times(
                                                          Power(
                                                              Plus(
                                                                  Times(CN1, a, e), Times(c,
                                                                      Power(x, q))),
                                                              Power(n, CN1)),
                                                          Power(Power(
                                                              Subtract(Times(b, e),
                                                                  Times(d, Power(x, q))),
                                                              Power(n, CN1)), CN1)))),
                                              r),
                                          Power(Power(Subtract(Times(b, e), Times(d, Power(x, q))),
                                              Plus(Power(n, CN1), C1)), CN1)),
                                      x),
                                  x),
                              x,
                              Power(Times(e, Plus(a, Times(b, Power(x, n))), Power(
                                  Plus(c, Times(d, Power(x, n))), CN1)), Power(q,
                                      CN1))),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), PolynomialQ(u, x), FractionQ(p),
                      IntegerQ(Power(n, CN1)), IntegerQ(r)))),
          IIntegrate(1962,
              Integrate(
                  Times(Power(u_, r_DEFAULT), Power(x_, m_DEFAULT),
                      Power(
                          Times(
                              e_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, Power(x_,
                                  n_DEFAULT))),
                              Power(Plus(c_, Times(d_DEFAULT, Power(x_, n_DEFAULT))), CN1)),
                          p_)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(q,
                          Denominator(p))),
                      Dist(
                          Times(q, e, Subtract(Times(b, c), Times(a, d)), Power(n,
                              CN1)),
                          Subst(
                              Integrate(
                                  SimplifyIntegrand(
                                      Times(
                                          Power(x, Subtract(Times(q, Plus(p, C1)),
                                              C1)),
                                          Power(
                                              Plus(Times(CN1, a, e), Times(c, Power(x,
                                                  q))),
                                              Subtract(Times(Plus(m, C1), Power(n, CN1)), C1)),
                                          Power(
                                              ReplaceAll(
                                                  u,
                                                  Rule(
                                                      x,
                                                      Times(
                                                          Power(
                                                              Plus(
                                                                  Times(CN1, a, e), Times(c,
                                                                      Power(x, q))),
                                                              Power(n, CN1)),
                                                          Power(Power(
                                                              Subtract(Times(b, e),
                                                                  Times(d, Power(x, q))),
                                                              Power(n, CN1)), CN1)))),
                                              r),
                                          Power(
                                              Power(Subtract(Times(b, e), Times(d, Power(x, q))),
                                                  Plus(Times(Plus(m, C1), Power(n, CN1)), C1)),
                                              CN1)),
                                      x),
                                  x),
                              x,
                              Power(
                                  Times(e, Plus(a, Times(b, Power(x, n))),
                                      Power(Plus(c, Times(d, Power(x, n))), CN1)),
                                  Power(q, CN1))),
                          x)),
                  And(FreeQ(List(a, b, c, d,
                      e), x), PolynomialQ(u,
                          x),
                      FractionQ(p), IntegerQ(Power(n, CN1)), IntegersQ(m, r)))),
          IIntegrate(1963,
              Integrate(
                  Power(
                      Plus(a_DEFAULT, Times(b_DEFAULT,
                          Power(Times(c_DEFAULT, Power(x_, CN1)), n_))),
                      p_),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(c,
                          Subst(
                              Integrate(
                                  Times(Power(Plus(a, Times(b, Power(x, n))), p),
                                      Power(x, CN2)),
                                  x),
                              x, Times(c, Power(x, CN1))),
                          x)),
                  FreeQ(List(a, b, c, n, p), x))),
          IIntegrate(1964,
              Integrate(
                  Times(
                      Power(
                          Plus(
                              a_DEFAULT, Times(b_DEFAULT,
                                  Power(Times(c_DEFAULT, Power(x_, CN1)), n_))),
                          p_DEFAULT),
                      Power(x_, m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(
                          Power(c, Plus(m,
                              C1)),
                          Subst(
                              Integrate(
                                  Times(
                                      Power(Plus(a, Times(b, Power(x, n))),
                                          p),
                                      Power(Power(x, Plus(m, C2)), CN1)),
                                  x),
                              x, Times(c, Power(x, CN1))),
                          x)),
                  And(FreeQ(List(a, b, c, n, p), x), IntegerQ(m)))),
          IIntegrate(1965,
              Integrate(
                  Times(
                      Power(
                          Plus(
                              a_DEFAULT, Times(b_DEFAULT,
                                  Power(Times(c_DEFAULT, Power(x_, CN1)), n_))),
                          p_DEFAULT),
                      Power(Times(d_DEFAULT, x_), m_)),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(Times(c, Power(Times(d, x), m), Power(Times(c, Power(x, CN1)), m)),
                          Subst(
                              Integrate(
                                  Times(Power(Plus(a, Times(b, Power(x, n))), p),
                                      Power(Power(x, Plus(m, C2)), CN1)),
                                  x),
                              x, Times(c, Power(x, CN1))),
                          x)),
                  And(FreeQ(List(a, b, c, d, m, n, p), x), Not(IntegerQ(m))))),
          IIntegrate(1966,
              Integrate(
                  Power(
                      Plus(a_DEFAULT,
                          Times(c_DEFAULT, Power(Times(d_DEFAULT, Power(x_,
                              CN1)), $p("n2",
                                  true))),
                          Times(b_DEFAULT, Power(Times(d_DEFAULT, Power(x_, CN1)), n_))),
                      p_DEFAULT),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(d,
                          Subst(
                              Integrate(
                                  Times(
                                      Power(
                                          Plus(a, Times(b, Power(x, n)),
                                              Times(c, Power(x, Times(C2, n)))),
                                          p),
                                      Power(x, CN2)),
                                  x),
                              x, Times(d, Power(x, CN1))),
                          x)),
                  And(FreeQ(List(a, b, c, d, n, p), x), EqQ($s("n2"), Times(C2, n))))),
          IIntegrate(1967,
              Integrate(
                  Times(
                      Power(
                          Plus(a_,
                              Times(
                                  c_DEFAULT, Power(Times(d_DEFAULT, Power(x_,
                                      CN1)), $p("n2",
                                          true))),
                              Times(b_DEFAULT, Power(Times(d_DEFAULT, Power(x_, CN1)), n_))),
                          p_DEFAULT),
                      Power(x_, m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(
                          Power(d, Plus(m,
                              C1)),
                          Subst(
                              Integrate(
                                  Times(
                                      Power(
                                          Plus(a, Times(b, Power(x, n)),
                                              Times(c, Power(x, Times(C2, n)))),
                                          p),
                                      Power(Power(x, Plus(m, C2)), CN1)),
                                  x),
                              x, Times(d, Power(x, CN1))),
                          x)),
                  And(FreeQ(List(a, b, c, d, n, p), x), EqQ($s("n2"), Times(C2, n)), IntegerQ(m)))),
          IIntegrate(1968,
              Integrate(
                  Times(
                      Power(Plus(a_,
                          Times(c_DEFAULT, Power(Times(d_DEFAULT, Power(x_, CN1)),
                              $p("n2", true))),
                          Times(b_DEFAULT, Power(Times(d_DEFAULT, Power(x_, CN1)), n_))),
                          p_DEFAULT),
                      Power(Times(e_DEFAULT, x_), m_)),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(
                          Times(d, Power(Times(e,
                              x), m), Power(Times(d, Power(x, CN1)),
                                  m)),
                          Subst(
                              Integrate(
                                  Times(
                                      Power(Plus(a, Times(b, Power(x, n)),
                                          Times(c, Power(x, Times(C2, n)))), p),
                                      Power(Power(x, Plus(m, C2)), CN1)),
                                  x),
                              x, Times(d, Power(x, CN1))),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, m, n, p), x), EqQ($s("n2"), Times(C2, n)),
                      Not(IntegerQ(m))))),
          IIntegrate(1969,
              Integrate(
                  Power(Plus(a_DEFAULT,
                      Times(b_DEFAULT, Power(Times(d_DEFAULT, Power(x_, CN1)),
                          n_)),
                      Times(c_DEFAULT, Power(x_, $p("n2", true)))),
                      p_DEFAULT),
                  x_Symbol),
              Condition(
                  Negate(Dist(d,
                      Subst(Integrate(Times(Power(
                          Plus(a, Times(b, Power(x, n)),
                              Times(c, Power(x, Times(C2, n)), Power(Power(d, Times(C2, n)), CN1))),
                          p), Power(x, CN2)), x), x, Times(d, Power(x, CN1))),
                      x)),
                  And(FreeQ(List(a, b, c, d, n, p), x), EqQ($s("n2"), Times(CN2, n)),
                      IntegerQ(Times(C2, n))))),
          IIntegrate(1970,
              Integrate(
                  Times(
                      Power(x_, m_DEFAULT), Power(Plus(a_,
                          Times(b_DEFAULT, Power(Times(d_DEFAULT, Power(x_, CN1)),
                              n_)),
                          Times(c_DEFAULT, Power(x_, $p("n2", true)))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(
                          Power(d, Plus(m,
                              C1)),
                          Subst(
                              Integrate(
                                  Times(Power(
                                      Plus(a, Times(b, Power(x, n)),
                                          Times(c, Power(x, Times(C2, n)),
                                              Power(Power(d, Times(C2, n)), CN1))),
                                      p), Power(Power(x, Plus(m, C2)), CN1)),
                                  x),
                              x, Times(d, Power(x, CN1))),
                          x)),
                  And(FreeQ(List(a, b, c, d, n,
                      p), x), EqQ($s("n2"),
                          Times(CN2, n)),
                      IntegerQ(Times(C2, n)), IntegerQ(m)))),
          IIntegrate(1971,
              Integrate(
                  Times(
                      Power(Times(e_DEFAULT,
                          x_), m_),
                      Power(
                          Plus(a_, Times(b_DEFAULT, Power(Times(d_DEFAULT, Power(x_, CN1)),
                              n_)), Times(c_DEFAULT,
                                  Power(x_, $p("n2", true)))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Negate(Dist(
                      Times(d, Power(Times(e, x), m), Power(Times(d, Power(x, CN1)), m)), Subst(
                          Integrate(Times(
                              Power(Plus(a, Times(b, Power(x, n)),
                                  Times(c, Power(x, Times(C2, n)),
                                      Power(Power(d, Times(C2, n)), CN1))),
                                  p),
                              Power(Power(x, Plus(m, C2)), CN1)), x),
                          x, Times(d, Power(x, CN1))),
                      x)),
                  And(FreeQ(List(a, b, c, d, e, n, p), x), EqQ($s("n2"), Times(CN2, n)),
                      Not(IntegerQ(m)), IntegerQ(Times(C2, n))))),
          IIntegrate(1972, Integrate(Power(u_, p_), x_Symbol),
              Condition(
                  Integrate(Power(ExpandToSum(u, x), p), x),
                  And(FreeQ(p, x), BinomialQ(u, x), Not(BinomialMatchQ(u, x))))),
          IIntegrate(1973,
              Integrate(Times(Power(u_, p_DEFAULT), Power(Times(c_DEFAULT, x_), m_DEFAULT)),
                  x_Symbol),
              Condition(Integrate(Times(Power(Times(c, x), m), Power(ExpandToSum(u, x), p)), x),
                  And(FreeQ(List(c, m, p), x), BinomialQ(u, x), Not(BinomialMatchQ(u, x))))),
          IIntegrate(1974, Integrate(Times(Power(u_, p_DEFAULT), Power(v_, q_DEFAULT)), x_Symbol),
              Condition(
                  Integrate(Times(Power(ExpandToSum(u, x), p), Power(ExpandToSum(v, x), q)), x),
                  And(FreeQ(List(p, q), x), BinomialQ(List(u, v), x),
                      EqQ(Subtract(BinomialDegree(u, x), BinomialDegree(v, x)), C0),
                      Not(BinomialMatchQ(List(u, v), x))))),
          IIntegrate(1975,
              Integrate(
                  Times(
                      Power(u_, p_DEFAULT), Power(v_, q_DEFAULT),
                      Power(Times(e_DEFAULT, x_), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(Times(Power(Times(e, x), m), Power(ExpandToSum(u, x), p),
                      Power(ExpandToSum(v, x), q)), x),
                  And(FreeQ(List(e, m, p, q), x), BinomialQ(List(u, v), x),
                      EqQ(Subtract(BinomialDegree(u, x), BinomialDegree(v, x)), C0),
                      Not(BinomialMatchQ(List(u, v), x))))),
          IIntegrate(1976,
              Integrate(Times(Power(u_, m_DEFAULT), Power(v_, p_DEFAULT), Power(w_, q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(Times(Power(ExpandToSum(u, x), m), Power(ExpandToSum(v, x), p),
                      Power(ExpandToSum(w, x), q)), x),
                  And(FreeQ(List(m, p, q), x), BinomialQ(List(u, v, w), x),
                      EqQ(Subtract(BinomialDegree(u, x), BinomialDegree(v, x)), C0),
                      EqQ(Subtract(BinomialDegree(u, x), BinomialDegree(w, x)), C0),
                      Not(BinomialMatchQ(List(u, v, w), x))))),
          IIntegrate(1977,
              Integrate(Times(Power(u_, p_DEFAULT), Power(v_, q_DEFAULT),
                  Power(Times(g_DEFAULT, x_), m_DEFAULT), Power(z_, r_DEFAULT)), x_Symbol),
              Condition(
                  Integrate(Times(Power(Times(g, x), m), Power(ExpandToSum(u, x), p),
                      Power(ExpandToSum(v, x), q), Power(ExpandToSum(z, x), r)), x),
                  And(FreeQ(List(g, m, p, q, r), x), BinomialQ(List(u, v, z), x),
                      EqQ(Subtract(BinomialDegree(u, x), BinomialDegree(v, x)), C0),
                      EqQ(Subtract(BinomialDegree(u, x), BinomialDegree(z, x)), C0),
                      Not(BinomialMatchQ(List(u, v, z), x))))),
          IIntegrate(1978, Integrate(
              Times($p("§pq"), Power(u_, p_DEFAULT), Power(Times(c_DEFAULT, x_), m_DEFAULT)),
              x_Symbol),
              Condition(
                  Integrate(Times(Power(Times(c, x), m), $s("§pq"), Power(ExpandToSum(u, x), p)),
                      x),
                  And(FreeQ(List(c, m, p), x), PolyQ($s("§pq"), x), BinomialQ(u, x),
                      Not(BinomialMatchQ(u, x))))),
          IIntegrate(1979, Integrate(Power(u_, p_), x_Symbol),
              Condition(
                  Integrate(Power(ExpandToSum(u, x), p), x),
                  And(FreeQ(p, x), GeneralizedBinomialQ(u, x),
                      Not(GeneralizedBinomialMatchQ(u, x))))),
          IIntegrate(1980,
              Integrate(Times(Power(u_, p_DEFAULT), Power(Times(c_DEFAULT, x_), m_DEFAULT)),
                  x_Symbol),
              Condition(Integrate(Times(Power(Times(c, x), m), Power(ExpandToSum(u, x), p)), x),
                  And(FreeQ(List(c, m, p), x), GeneralizedBinomialQ(u, x),
                      Not(GeneralizedBinomialMatchQ(u, x))))));
}
