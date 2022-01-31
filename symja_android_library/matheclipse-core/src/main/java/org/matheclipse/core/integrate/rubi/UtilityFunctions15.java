package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$;
import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$rubi;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.AtomQ;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.Cot;
import static org.matheclipse.core.expression.F.Csc;
import static org.matheclipse.core.expression.F.First;
import static org.matheclipse.core.expression.F.Greater;
import static org.matheclipse.core.expression.F.GreaterEqual;
import static org.matheclipse.core.expression.F.Head;
import static org.matheclipse.core.expression.F.ISetDelayed;
import static org.matheclipse.core.expression.F.If;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Less;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Map;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Part;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Rest;
import static org.matheclipse.core.expression.F.SameQ;
import static org.matheclipse.core.expression.F.Sec;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Tan;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.UnsameQ;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.u_DEFAULT;
import static org.matheclipse.core.expression.F.v_;
import static org.matheclipse.core.expression.F.v_DEFAULT;
import static org.matheclipse.core.expression.F.w_DEFAULT;
import static org.matheclipse.core.expression.F.z_;
import static org.matheclipse.core.expression.S.If;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.w;
import static org.matheclipse.core.expression.S.z;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ActivateTrig;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.InertTrigQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.MergeFactor;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.MergeFactors;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.MergeableFactorQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PowerQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ProductQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RationalQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SumQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.TrigSimplify;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.TrigSimplifyAux;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.TrigSimplifyQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.TrigSimplifyRecur;
import org.matheclipse.core.interfaces.IAST;

/**
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class UtilityFunctions15 {
  public static IAST RULES = List(
      ISetDelayed(241, MergeFactors(u_, v_),
          If(ProductQ(u), MergeFactors(Rest(u), MergeFactors(First(u), v)),
              If(PowerQ(u),
                  If(MergeableFactorQ(Part(u, C1), Part(u, C2), v),
                      MergeFactor(Part(u, C1), Part(u, C2), v),
                      If(And(RationalQ(Part(u, C2)), Less(Part(u, C2), CN1),
                          MergeableFactorQ(Part(u, C1), CN1, v)),
                          MergeFactors(Power(Part(u, C1), Plus(Part(u, C2), C1)),
                              MergeFactor(Part(u, C1), CN1, v)),
                          Times(u, v))),
                  If(MergeableFactorQ(u, C1, v), MergeFactor(u, C1, v), Times(u, v))))),
      ISetDelayed(242, MergeFactor($p("bas"), $p("deg"), v_),
          If(SameQ($s("bas"), v), Power($s("bas"), Plus($s("deg"), C1)),
              If(PowerQ(v),
                  If(SameQ($s("bas"), Part(v, C1)), Power($s("bas"), Plus($s("deg"), Part(v, C2))),
                      Power(MergeFactor($s("bas"), Times($s("deg"), Power(Part(v, C2), CN1)),
                          Part(v, C1)), Part(v, C2))),
                  If(ProductQ(v),
                      If(MergeableFactorQ($s("bas"), $s("deg"), First(v)),
                          Times(MergeFactor($s("bas"), $s("deg"), First(v)), Rest(v)),
                          Times(First(v), MergeFactor($s("bas"), $s("deg"), Rest(v)))),
                      Plus(MergeFactor($s("bas"), $s("deg"), First(v)),
                          MergeFactor($s("bas"), $s("deg"), Rest(v))))))),
      ISetDelayed(
          243, MergeableFactorQ($p("bas"), $p("deg"), v_), If(
              SameQ($s("bas"), v), And(
                  RationalQ(Plus($s("deg"), C1)),
                  Or(GreaterEqual(Plus($s("deg"), C1), C0),
                      And(RationalQ($s("deg")), Greater($s("deg"), C0)))),
              If(PowerQ(v), If(SameQ($s("bas"), Part(v, C1)), And(
                  RationalQ(Plus($s("deg"), Part(v, C2))),
                  Or(GreaterEqual(Plus($s("deg"), Part(v, C2)), C0),
                      And(RationalQ($s("deg")), Greater($s("deg"), C0)))),
                  And(SumQ(Part(v, C1)), IntegerQ(Part(v, C2)),
                      Or(Not(IntegerQ($s("deg"))),
                          IntegerQ(Times($s("deg"), Power(Part(v, C2), CN1)))),
                      MergeableFactorQ($s("bas"), Times($s("deg"), Power(Part(v, C2), CN1)),
                          Part(v, C1)))),
                  If(ProductQ(v),
                      Or(MergeableFactorQ($s("bas"), $s("deg"),
                          First(v)), MergeableFactorQ($s("bas"), $s("deg"), Rest(v))),
                      And(SumQ(v), MergeableFactorQ($s("bas"), $s("deg"), First(
                          v)), MergeableFactorQ($s("bas"), $s("deg"),
                              Rest(v))))))),
      ISetDelayed(244, TrigSimplifyQ(u_), UnsameQ(ActivateTrig(u), TrigSimplify(
          u))),
      ISetDelayed(245, TrigSimplify(u_), ActivateTrig(TrigSimplifyRecur(u))),
      ISetDelayed(246, TrigSimplifyRecur(u_),
          If(AtomQ(u), u,
              If(SameQ(Head(u), If), u, TrigSimplifyAux(Map($rubi("TrigSimplifyRecur"), u))))),
      ISetDelayed(247,
          TrigSimplifyAux(Times(u_DEFAULT,
              Power(Plus(Times(a_DEFAULT, Power(v_, m_DEFAULT)),
                  Times(b_DEFAULT, Power(v_, n_DEFAULT))), p_))),
          Condition(
              Times(u, Power(v, Times(m, p)),
                  Power(TrigSimplifyAux(Plus(a, Times(b, Power(v, Subtract(n, m))))), p)),
              And(InertTrigQ(v), IntegerQ(p), RationalQ(m, n), Less(m, n)))),
      ISetDelayed(248,
          TrigSimplifyAux(Plus(Times(Sqr($($s("§cos"), u_)), a_DEFAULT),
              Times(Sqr($($s("§sin"), u_)), b_DEFAULT), v_DEFAULT)),
          Condition(Plus(a, v), SameQ(a, b))),
      ISetDelayed(249,
          TrigSimplifyAux(
              Plus(Times(Sqr($($s("§sec"), u_)), a_DEFAULT),
                  Times(Sqr($($s("§tan"), u_)), b_DEFAULT), v_DEFAULT)),
          Condition(Plus(a, v), SameQ(a, Negate(b)))),
      ISetDelayed(250,
          TrigSimplifyAux(Plus(
              Times(Sqr($($s("§csc"), u_)), a_DEFAULT), Times(Sqr($($s("§cot"), u_)), b_DEFAULT),
              v_DEFAULT)),
          Condition(Plus(a, v), SameQ(a, Negate(b)))),
      ISetDelayed(251,
          TrigSimplifyAux(
              Power(Plus(Times(Sqr($($s("§cos"), u_)), a_DEFAULT),
                  Times(Sqr($($s("§sin"), u_)), b_DEFAULT), v_DEFAULT), n_)),
          Power(Plus(Times(Subtract(b, a), Sqr(Sin(u))), a, v), n)),
      ISetDelayed(252,
          TrigSimplifyAux(Plus(u_, Times(Sqr($($s("§sin"), z_)), v_DEFAULT), w_DEFAULT)), Condition(
              Plus(Times(u, Sqr(Cos(z))), w), SameQ(u, Negate(v)))),
      ISetDelayed(253,
          TrigSimplifyAux(Plus(u_, Times(Sqr($($s("§cos"), z_)), v_DEFAULT),
              w_DEFAULT)),
          Condition(Plus(Times(u, Sqr(Sin(z))), w), SameQ(u, Negate(v)))),
      ISetDelayed(254,
          TrigSimplifyAux(Plus(u_, Times(Sqr($($s("§tan"), z_)), v_DEFAULT),
              w_DEFAULT)),
          Condition(Plus(Times(u, Sqr(Sec(z))), w), SameQ(u, v))),
      ISetDelayed(255,
          TrigSimplifyAux(Plus(u_, Times(Sqr($($s("§cot"), z_)), v_DEFAULT), w_DEFAULT)),
          Condition(Plus(Times(u, Sqr(Csc(z))), w), SameQ(u, v))),
      ISetDelayed(256,
          TrigSimplifyAux(Plus(u_, Times(Sqr($($s("§sec"), z_)), v_DEFAULT), w_DEFAULT)),
          Condition(Plus(Times(v, Sqr(Tan(z))), w), SameQ(u, Negate(v)))),
      ISetDelayed(257,
          TrigSimplifyAux(Plus(u_, Times(Sqr($($s("§csc"), z_)), v_DEFAULT), w_DEFAULT)),
          Condition(Plus(Times(v, Sqr(Cot(z))), w), SameQ(u, Negate(v)))),
      ISetDelayed(258,
          TrigSimplifyAux(Times(Sqr($($s("§sin"), v_)),
              Power(Plus(a_, Times($($s("§cos"), v_), b_DEFAULT)), CN1), u_DEFAULT)),
          Condition(Times(u, Subtract(Power(a, CN1), Times(Cos(v), Power(b, CN1)))),
              EqQ(Subtract(Sqr(a), Sqr(b)), C0))),
      ISetDelayed(259,
          TrigSimplifyAux(Times(Sqr($($s("§cos"), v_)),
              Power(Plus(a_, Times($($s("§sin"), v_), b_DEFAULT)), CN1), u_DEFAULT)),
          Condition(Times(u, Subtract(Power(a, CN1), Times(Sin(v), Power(b, CN1)))),
              EqQ(Subtract(Sqr(a), Sqr(b)), C0))));
}
