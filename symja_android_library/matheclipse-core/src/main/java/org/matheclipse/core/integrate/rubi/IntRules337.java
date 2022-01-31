package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Part;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Surd;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.UnsameQ;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.q_DEFAULT;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.u_DEFAULT;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.Integer;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.q;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FalseQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NormalizeIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SubstForFractionalPowerOfLinear;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SumQ;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules337 {
  public static IAST RULES =
      List(
          IIntegrate(6740, Integrate(u_, x_Symbol),
              With(
                  List(Set(v,
                      NormalizeIntegrand(u, x))),
                  Condition(Integrate(v, x), UnsameQ(v, u)))),
          IIntegrate(
              6741, Integrate(u_, x_Symbol), With(List(Set(v, ExpandIntegrand(u, x))),
                  Condition(Integrate(v, x), SumQ(v)))),
          IIntegrate(6742,
              Integrate(
                  Times(u_DEFAULT,
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT,
                          Power(x_, m_DEFAULT))), p_DEFAULT),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, Power(x_, n_DEFAULT))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Plus(a, Times(b, Power(x, m))), p),
                          Power(Plus(c,
                              Times(d, Power(x, n))), q),
                          Power(Power(x, Times(m, p)), CN1)),
                      Integrate(Times(u, Power(x, Times(m, p))), x), x),
                  And(FreeQ(List(a, b, c, d, m, n, p, q), x), EqQ(Plus(a, d), C0), EqQ(Plus(b,
                      c), C0), EqQ(Plus(m, n),
                          C0),
                      EqQ(Plus(p, q), C0)))),
          IIntegrate(6743,
              Integrate(
                  Times(u_,
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_DEFAULT)),
                          Times(c_DEFAULT, Power(x_, $p("n2", true)))), p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Sqrt(Plus(a, Times(b, Power(x, n)), Times(c, Power(x, Times(C2, n))))),
                          Power(Times(Power(Times(C4, c), Subtract(p, C1D2)),
                              Plus(b, Times(C2, c, Power(x, n)))), CN1)),
                      Integrate(Times(u, Power(Plus(b, Times(C2, c, Power(x, n))), Times(C2, p))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, n, p), x), EqQ($s("n2"),
                      Times(C2, n)), EqQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      IntegerQ(Subtract(p, C1D2))))),
          IIntegrate(6744, Integrate(u_, x_Symbol),
              With(
                  List(Set($s("lst"),
                      SubstForFractionalPowerOfLinear(u, x))),
                  Condition(
                      Dist(Times(Part($s("lst"), C2), Part($s("lst"), C4)),
                          Subst(Integrate(Part($s("lst"), C1), x), x,
                              Power(Part($s("lst"), C3), Power(Part($s("lst"), C2), CN1))),
                          x),
                      Not(FalseQ($s("lst")))))),
          IIntegrate(6745, Integrate(Power(Surd(x_, $p(n, Integer)), p_DEFAULT), x_Symbol),
              Condition(Times(n, x, Power(Surd(x, n), p), Power(Plus(n, p), CN1)),
                  And(FreeQ(p, x), GtQ(n, C0)))),
          IIntegrate(6746,
              Integrate(Times(Power(x_, m_), Power(Surd(x_, $p(n, Integer)), p_DEFAULT)), x_Symbol),
              Condition(
                  Times(Power(x, Plus(C1, m)), Power(Surd(x, n), p),
                      Power(Plus(C1, m, Times(p, Power(n, CN1))), CN1)),
                  And(FreeQ(List(m, p), x), GtQ(n, C0)))));
}
