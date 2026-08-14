package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

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
