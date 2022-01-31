package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.j_DEFAULT;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.q_DEFAULT;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.v_;
import static org.matheclipse.core.expression.F.w_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.F.z_;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.f;
import static org.matheclipse.core.expression.S.j;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.q;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.w;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.expression.S.z;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.BinomialDegree;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.BinomialMatchQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.BinomialQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandToSum;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GeneralizedTrinomialDegree;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GeneralizedTrinomialMatchQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GeneralizedTrinomialQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LinearMatchQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LinearQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PolyQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.QuadraticMatchQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.QuadraticQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.TrinomialMatchQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.TrinomialQ;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules99 {
  public static IAST RULES =
      List(
          IIntegrate(1981, Integrate(Power(u_, p_), x_Symbol),
              Condition(Integrate(Power(ExpandToSum(u, x), p), x),
                  And(FreeQ(p, x), QuadraticQ(u, x), Not(QuadraticMatchQ(u, x))))),
          IIntegrate(1982, Integrate(Times(Power(u_, m_DEFAULT), Power(v_, p_DEFAULT)), x_Symbol),
              Condition(Integrate(Times(Power(ExpandToSum(u, x), m), Power(ExpandToSum(v, x), p)),
                  x),
                  And(FreeQ(List(m, p), x), LinearQ(u, x), QuadraticQ(v, x),
                      Not(And(LinearMatchQ(u, x), QuadraticMatchQ(v, x)))))),
          IIntegrate(1983,
              Integrate(Times(Power(u_, m_DEFAULT), Power(v_, n_DEFAULT), Power(w_, p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(Times(Power(ExpandToSum(u, x), m), Power(ExpandToSum(v, x), n),
                      Power(ExpandToSum(w, x), p)), x),
                  And(FreeQ(List(m, n, p), x), LinearQ(List(u, v), x), QuadraticQ(w, x),
                      Not(And(LinearMatchQ(List(u, v), x), QuadraticMatchQ(w, x)))))),
          IIntegrate(
              1984, Integrate(Times(Power(u_, p_DEFAULT),
                  Power(v_, q_DEFAULT)), x_Symbol),
              Condition(
                  Integrate(Times(Power(ExpandToSum(u, x), p),
                      Power(ExpandToSum(v, x), q)), x),
                  And(FreeQ(List(p, q), x), QuadraticQ(List(u, v), x),
                      Not(QuadraticMatchQ(List(u, v), x))))),
          IIntegrate(1985,
              Integrate(Times(Power(u_, p_DEFAULT), Power(v_, q_DEFAULT), Power(z_, m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(Times(Power(ExpandToSum(z, x), m), Power(ExpandToSum(u, x), p),
                      Power(ExpandToSum(v, x), q)), x),
                  And(FreeQ(List(m, p, q), x), LinearQ(z, x), QuadraticQ(List(u, v), x),
                      Not(And(LinearMatchQ(z, x), QuadraticMatchQ(List(u, v), x)))))),
          IIntegrate(1986, Integrate(Times($p("§pq"), Power(u_, p_DEFAULT)), x_Symbol),
              Condition(Integrate(Times($s("§pq"), Power(ExpandToSum(u, x), p)), x),
                  And(FreeQ(p, x), PolyQ($s("§pq"), x), QuadraticQ(u, x),
                      Not(QuadraticMatchQ(u, x))))),
          IIntegrate(1987,
              Integrate(Times($p("§pq"), Power(u_, m_DEFAULT), Power(v_, p_DEFAULT)), x_Symbol),
              Condition(Integrate(
                  Times(Power(ExpandToSum(u, x), m), $s("§pq"), Power(ExpandToSum(v, x), p)), x),
                  And(FreeQ(List(m, p), x), PolyQ($s("§pq"), x), LinearQ(u, x), QuadraticQ(v, x),
                      Not(And(LinearMatchQ(u, x), QuadraticMatchQ(v, x)))))),
          IIntegrate(1988, Integrate(Power(u_, p_), x_Symbol),
              Condition(
                  Integrate(Power(ExpandToSum(u, x), p), x),
                  And(FreeQ(p, x), TrinomialQ(u, x), Not(TrinomialMatchQ(u, x))))),
          IIntegrate(1989,
              Integrate(Times(Power(u_, p_DEFAULT), Power(Times(d_DEFAULT, x_), m_DEFAULT)),
                  x_Symbol),
              Condition(Integrate(Times(Power(Times(d, x), m), Power(ExpandToSum(u, x), p)), x),
                  And(FreeQ(List(d, m, p), x), TrinomialQ(u, x), Not(TrinomialMatchQ(u, x))))),
          IIntegrate(1990, Integrate(Times(Power(u_, q_DEFAULT), Power(v_, p_DEFAULT)), x_Symbol),
              Condition(
                  Integrate(Times(Power(ExpandToSum(u, x), q), Power(ExpandToSum(v, x), p)), x),
                  And(FreeQ(List(p, q), x), BinomialQ(u, x), TrinomialQ(v, x),
                      Not(And(BinomialMatchQ(u, x), TrinomialMatchQ(v, x)))))),
          IIntegrate(1991, Integrate(Times(Power(u_, q_DEFAULT), Power(v_, p_DEFAULT)), x_Symbol),
              Condition(
                  Integrate(Times(Power(ExpandToSum(u, x), q), Power(ExpandToSum(v, x), p)), x),
                  And(FreeQ(List(p, q), x), BinomialQ(u, x), BinomialQ(v, x),
                      Not(And(BinomialMatchQ(u, x), BinomialMatchQ(v, x)))))),
          IIntegrate(1992,
              Integrate(
                  Times(
                      Power(u_, p_DEFAULT), Power(Times(f_DEFAULT, x_), m_DEFAULT),
                      Power(z_, q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(Power(Times(f, x), m), Power(ExpandToSum(z, x), q),
                          Power(ExpandToSum(u, x), p)),
                      x),
                  And(FreeQ(List(f, m, p, q), x), BinomialQ(z, x), TrinomialQ(u, x),
                      Not(And(BinomialMatchQ(z, x), TrinomialMatchQ(u, x)))))),
          IIntegrate(1993,
              Integrate(
                  Times(
                      Power(u_, p_DEFAULT), Power(Times(f_DEFAULT, x_), m_DEFAULT), Power(z_,
                          q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(Times(Power(Times(f, x), m), Power(ExpandToSum(z, x), q),
                      Power(ExpandToSum(u, x), p)), x),
                  And(FreeQ(List(f, m, p, q), x), BinomialQ(z, x), BinomialQ(u, x),
                      Not(And(BinomialMatchQ(z, x), BinomialMatchQ(u, x)))))),
          IIntegrate(1994, Integrate(Times($p("§pq"), Power(u_, p_DEFAULT)), x_Symbol),
              Condition(
                  Integrate(Times($s("§pq"), Power(ExpandToSum(u, x), p)), x),
                  And(FreeQ(p, x), PolyQ($s("§pq"), x), TrinomialQ(u, x),
                      Not(TrinomialMatchQ(u, x))))),
          IIntegrate(1995, Integrate(
              Times($p("§pq"), Power(u_, p_DEFAULT), Power(Times(d_DEFAULT, x_), m_DEFAULT)),
              x_Symbol),
              Condition(
                  Integrate(Times(Power(Times(d, x), m), $s("§pq"), Power(ExpandToSum(u, x), p)),
                      x),
                  And(FreeQ(List(d, m, p), x), PolyQ($s("§pq"), x), TrinomialQ(u, x),
                      Not(TrinomialMatchQ(u, x))))),
          IIntegrate(1996, Integrate(Power(u_, p_), x_Symbol),
              Condition(Integrate(Power(ExpandToSum(u, x), p), x),
                  And(FreeQ(p, x), GeneralizedTrinomialQ(u, x),
                      Not(GeneralizedTrinomialMatchQ(u, x))))),
          IIntegrate(1997,
              Integrate(Times(Power(u_, p_DEFAULT), Power(Times(d_DEFAULT, x_), m_DEFAULT)),
                  x_Symbol),
              Condition(Integrate(Times(Power(Times(d, x), m), Power(ExpandToSum(u, x), p)), x),
                  And(FreeQ(List(d, m, p), x), GeneralizedTrinomialQ(u, x),
                      Not(GeneralizedTrinomialMatchQ(u, x))))),
          IIntegrate(
              1998, Integrate(Times(Power(u_, p_DEFAULT),
                  z_), x_Symbol),
              Condition(Integrate(Times(ExpandToSum(z, x), Power(ExpandToSum(u, x), p)), x),
                  And(FreeQ(p, x), BinomialQ(z, x), GeneralizedTrinomialQ(u, x),
                      EqQ(Subtract(BinomialDegree(z, x), GeneralizedTrinomialDegree(u,
                          x)), C0),
                      Not(And(BinomialMatchQ(z, x), GeneralizedTrinomialMatchQ(u, x)))))),
          IIntegrate(1999,
              Integrate(
                  Times(Power(u_, p_DEFAULT), Power(Times(f_DEFAULT, x_), m_DEFAULT),
                      z_),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(Power(Times(f, x), m), ExpandToSum(z, x),
                          Power(ExpandToSum(u, x), p)),
                      x),
                  And(FreeQ(List(f, m, p), x), BinomialQ(z, x), GeneralizedTrinomialQ(u, x),
                      EqQ(Subtract(BinomialDegree(z, x), GeneralizedTrinomialDegree(u, x)), C0),
                      Not(And(BinomialMatchQ(z, x), GeneralizedTrinomialMatchQ(u, x)))))),
          IIntegrate(2000,
              Integrate(Power(Plus(Times(a_DEFAULT, Power(x_, j_DEFAULT)),
                  Times(b_DEFAULT, Power(x_, n_DEFAULT))), p_), x_Symbol),
              Condition(
                  Simp(Times(Power(Plus(Times(a, Power(x, j)), Times(b, Power(x, n))), Plus(p, C1)),
                      Power(Times(b, Subtract(n, j), Plus(p, C1), Power(x, Subtract(n, C1))), CN1)),
                      x),
                  And(FreeQ(List(a, b, j, n, p), x), Not(IntegerQ(p)), NeQ(n, j),
                      EqQ(Plus(Times(j, p), Negate(n), j, C1), C0)))));
}
