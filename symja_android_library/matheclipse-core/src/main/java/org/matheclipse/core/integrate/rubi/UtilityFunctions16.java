package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$;
import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.Apply;
import static org.matheclipse.core.expression.F.AtomQ;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CompoundExpression;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.Cot;
import static org.matheclipse.core.expression.F.Csc;
import static org.matheclipse.core.expression.F.Function;
import static org.matheclipse.core.expression.F.ISetDelayed;
import static org.matheclipse.core.expression.F.If;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Less;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Map;
import static org.matheclipse.core.expression.F.Module;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Part;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Rest;
import static org.matheclipse.core.expression.F.SameQ;
import static org.matheclipse.core.expression.F.Sec;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Slot1;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Tan;
import static org.matheclipse.core.expression.F.TimeConstrained;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.u_DEFAULT;
import static org.matheclipse.core.expression.F.v_;
import static org.matheclipse.core.expression.F.w_;
import static org.matheclipse.core.expression.S.List;
import static org.matheclipse.core.expression.S.Plus;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.w;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.CommonFactors;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ContentFactor;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ContentFactorAux;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntegerPowerQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntegersQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NonsumQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NumericFactor;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ProductQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SumQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.TrigSimplifyAux;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.UnifyNegativeBaseFactors;
import org.matheclipse.core.interfaces.IAST;

/**
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class UtilityFunctions16 {
  public static IAST RULES =
      List(
          ISetDelayed(260,
              TrigSimplifyAux(Times(Power($($s("§tan"), v_), n_DEFAULT),
                  Power(Plus(a_, Times(Power($($s("§tan"), v_), n_DEFAULT), b_DEFAULT)), CN1),
                  u_DEFAULT)),
              Condition(Times(u, Power(Plus(b, Times(a, Power(Cot(v), n))), CN1)),
                  And(IGtQ(n, C0), NonsumQ(a)))),
          ISetDelayed(261,
              TrigSimplifyAux(
                  Times(Power($($s("§cot"), v_), n_DEFAULT),
                      Power(Plus(a_, Times(Power($($s("§cot"), v_), n_DEFAULT), b_DEFAULT)), CN1),
                      u_DEFAULT)),
              Condition(
                  Times(u, Power(Plus(b, Times(a, Power(Tan(v), n))),
                      CN1)),
                  And(IGtQ(n, C0), NonsumQ(a)))),
          ISetDelayed(262,
              TrigSimplifyAux(Times(Power($($s("§sec"), v_), n_DEFAULT),
                  Power(Plus(a_, Times(Power($($s("§sec"), v_), n_DEFAULT), b_DEFAULT)), CN1),
                  u_DEFAULT)),
              Condition(Times(u, Power(Plus(b, Times(a, Power(Cos(v), n))), CN1)),
                  And(IGtQ(n, C0), NonsumQ(a)))),
          ISetDelayed(263,
              TrigSimplifyAux(Times(Power($($s("§csc"), v_), n_DEFAULT),
                  Power(Plus(a_, Times(Power($($s("§csc"), v_), n_DEFAULT), b_DEFAULT)), CN1),
                  u_DEFAULT)),
              Condition(Times(u, Power(Plus(b, Times(a, Power(Sin(v), n))), CN1)),
                  And(IGtQ(n, C0), NonsumQ(a)))),
          ISetDelayed(264,
              TrigSimplifyAux(Times(Power($($s("§tan"), v_), n_DEFAULT),
                  Power(Plus(a_, Times(Power($($s("§sec"), v_), n_DEFAULT), b_DEFAULT)),
                      CN1),
                  u_DEFAULT)),
              Condition(
                  Times(u, Power(Sin(v), n), Power(Plus(b, Times(a, Power(Cos(v), n))),
                      CN1)),
                  And(IGtQ(n, C0), NonsumQ(a)))),
          ISetDelayed(265,
              TrigSimplifyAux(
                  Times(Power($($s("§cot"), v_), n_DEFAULT),
                      Power(Plus(a_, Times(Power($($s("§csc"), v_), n_DEFAULT), b_DEFAULT)),
                          CN1),
                      u_DEFAULT)),
              Condition(
                  Times(u, Power(Cos(v), n), Power(Plus(b, Times(a, Power(Sin(v), n))),
                      CN1)),
                  And(IGtQ(n, C0), NonsumQ(a)))),
          ISetDelayed(266,
              TrigSimplifyAux(
                  Times(
                      Power(
                          Plus(
                              Times(Power($($s("§sec"), v_),
                                  n_DEFAULT), a_DEFAULT),
                              Times(Power($($s("§tan"), v_), n_DEFAULT), b_DEFAULT)),
                          p_DEFAULT),
                      u_DEFAULT)),
              Condition(
                  Times(u, Power(Sec(v), Times(n, p)), Power(Plus(a, Times(b, Power(Sin(v), n))),
                      p)),
                  IntegersQ(n, p))),
          ISetDelayed(267,
              TrigSimplifyAux(
                  Times(Power(
                      Plus(Times(Power($($s("§csc"), v_), n_DEFAULT), a_DEFAULT),
                          Times(Power($($s("§cot"), v_), n_DEFAULT), b_DEFAULT)),
                      p_DEFAULT), u_DEFAULT)),
              Condition(
                  Times(u, Power(Csc(v), Times(n, p)), Power(Plus(a, Times(b, Power(Cos(v), n))),
                      p)),
                  IntegersQ(n, p))),
          ISetDelayed(268,
              TrigSimplifyAux(
                  Times(
                      Power(
                          Plus(
                              Times(Power($($s("§tan"), v_),
                                  n_DEFAULT), a_DEFAULT),
                              Times(Power($($s("§sin"), v_), n_DEFAULT), b_DEFAULT)),
                          p_DEFAULT),
                      u_DEFAULT)),
              Condition(
                  Times(u, Power(Tan(v), Times(n, p)), Power(Plus(a, Times(b, Power(Cos(v), n))),
                      p)),
                  IntegersQ(n, p))),
          ISetDelayed(269,
              TrigSimplifyAux(
                  Times(Power(
                      Plus(Times(Power($($s("§cot"), v_), n_DEFAULT), a_DEFAULT),
                          Times(Power($($s("§cos"), v_), n_DEFAULT), b_DEFAULT)),
                      p_DEFAULT), u_DEFAULT)),
              Condition(
                  Times(u, Power(Cot(v), Times(n, p)), Power(Plus(a, Times(b, Power(Sin(v), n))),
                      p)),
                  IntegersQ(n, p))),
          ISetDelayed(270, TrigSimplifyAux(Times(Power($($s("§cos"), v_), m_DEFAULT),
              Power(Plus(a_DEFAULT, Times(Power($($s("§tan"), v_), n_DEFAULT), b_DEFAULT),
                  Times(Power($($s("§sec"), v_), n_DEFAULT), c_DEFAULT)), p_DEFAULT),
              u_DEFAULT)),
              Condition(Times(u, Power(Cos(v), Subtract(m, Times(n, p))),
                  Power(Plus(c, Times(b, Power(Sin(v), n)), Times(a, Power(Cos(v), n))), p)),
                  IntegersQ(m, n, p))),
          ISetDelayed(271, TrigSimplifyAux(Times(Power($($s("§sec"), v_), m_DEFAULT),
              Power(Plus(a_DEFAULT, Times(Power($($s("§tan"), v_), n_DEFAULT), b_DEFAULT),
                  Times(Power($($s("§sec"), v_), n_DEFAULT), c_DEFAULT)), p_DEFAULT),
              u_DEFAULT)),
              Condition(
                  Times(u, Power(Sec(v), Plus(m, Times(n, p))),
                      Power(Plus(c, Times(b, Power(Sin(v), n)), Times(a, Power(Cos(v), n))), p)),
                  IntegersQ(m, n, p))),
          ISetDelayed(272,
              TrigSimplifyAux(Times(Power($($s("§sin"), v_), m_DEFAULT),
                  Power(Plus(a_DEFAULT, Times(Power($($s("§cot"), v_), n_DEFAULT), b_DEFAULT),
                      Times(Power($($s("§csc"), v_), n_DEFAULT), c_DEFAULT)), p_DEFAULT),
                  u_DEFAULT)),
              Condition(
                  Times(
                      u, Power(Sin(v), Subtract(m, Times(n, p))), Power(Plus(c,
                          Times(b, Power(Cos(v), n)), Times(a, Power(Sin(v), n))), p)),
                  IntegersQ(m, n, p))),
          ISetDelayed(273,
              TrigSimplifyAux(
                  Times(
                      Power($($s("§csc"),
                          v_), m_DEFAULT),
                      Power(
                          Plus(
                              a_DEFAULT, Times(Power($($s("§cot"), v_),
                                  n_DEFAULT), b_DEFAULT),
                              Times(Power($($s("§csc"), v_), n_DEFAULT), c_DEFAULT)),
                          p_DEFAULT),
                      u_DEFAULT)),
              Condition(
                  Times(
                      u, Power(Csc(v), Plus(m, Times(n,
                          p))),
                      Power(Plus(c, Times(b, Power(Cos(v), n)), Times(a, Power(Sin(v), n))), p)),
                  IntegersQ(m, n, p))),
          ISetDelayed(274,
              TrigSimplifyAux(
                  Times(
                      Power(
                          Plus(
                              Times(Power($($s("§csc"), v_),
                                  m_DEFAULT), a_DEFAULT),
                              Times(Power($($s("§sin"), v_), n_DEFAULT), b_DEFAULT)),
                          p_DEFAULT),
                      u_DEFAULT)),
              Condition(If(
                  And(EqQ(Subtract(Plus(m, n), C2), C0), EqQ(Plus(a,
                      b), C0)),
                  Times(u, Power(Times(a, Sqr(Cos(v)), Power(Power(Sin(v), m), CN1)), p)),
                  Times(u,
                      Power(Times(Plus(a, Times(b, Power(Sin(v), Plus(m, n)))),
                          Power(Power(Sin(v), m), CN1)), p))),
                  IntegersQ(m, n))),
          ISetDelayed(275,
              TrigSimplifyAux(
                  Times(
                      Power(
                          Plus(
                              Times(Power($($s("§sec"), v_),
                                  m_DEFAULT), a_DEFAULT),
                              Times(Power($($s("§cos"), v_), n_DEFAULT), b_DEFAULT)),
                          p_DEFAULT),
                      u_DEFAULT)),
              Condition(If(And(EqQ(Subtract(Plus(m, n), C2), C0), EqQ(Plus(a, b), C0)),
                  Times(u, Power(Times(a, Sqr(Sin(v)), Power(Power(Cos(v), m), CN1)), p)),
                  Times(u,
                      Power(Times(Plus(a, Times(b, Power(Cos(v), Plus(m, n)))),
                          Power(Power(Cos(v), m), CN1)), p))),
                  IntegersQ(m, n))),
          ISetDelayed(276, TrigSimplifyAux(
              u_), u),
          ISetDelayed(277,
              TrigSimplifyAux(
                  Times(
                      Power(
                          Times(Power($($s("§tan"), v_), n_DEFAULT),
                              Power($($s("§tan"), w_), n_DEFAULT), c_DEFAULT),
                          p_DEFAULT),
                      u_DEFAULT)),
              Condition(
                  Times(u, Power(Power(Plus(Negate(c), Times(c, $($s("§sec"), w))), n),
                      p)),
                  And(IntegerQ(n), EqQ(w, Times(C2, v))))),
          ISetDelayed(
              278, ContentFactor($p(
                  "expn")),
              TimeConstrained(ContentFactorAux($s("expn")), $s("§$timelimit"), $s("expn"))),
          ISetDelayed(
              279, ContentFactorAux($p(
                  "expn")),
              If(AtomQ($s(
                  "expn")), $s("expn"), If(
                      IntegerPowerQ($s(
                          "expn")),
                      If(And(
                          SumQ(Part($s("expn"), C1)), Less(NumericFactor(Part($s("expn"), C1, C1)),
                              C0)),
                          Times(
                              Power(CN1, Part($s("expn"),
                                  C2)),
                              Power(
                                  ContentFactorAux(Negate(
                                      Part($s("expn"), C1))),
                                  Part($s("expn"), C2))),
                          Power(ContentFactorAux(Part($s("expn"), C1)), Part($s("expn"), C2))),
                      If(ProductQ($s("expn")),
                          Module(List(Set($s("num"), C1), $s("tmp")), CompoundExpression(
                              Set($s("tmp"),
                                  Map(Function(
                                      If(And(SumQ(Slot1), Less(NumericFactor(Part(Slot1, C1)), C0)),
                                          CompoundExpression(Set($s("num"), Negate($s("num"))),
                                              ContentFactorAux(Negate(Slot1))),
                                          ContentFactorAux(Slot1))),
                                      $s("expn"))),
                              Times($s("num"), UnifyNegativeBaseFactors($s("tmp"))))),
                          If(SumQ($s("expn")),
                              With(List(Set($s("lst"), CommonFactors(Apply(List, $s("expn"))))),
                                  If(Or(SameQ(Part($s("lst"), C1), C1),
                                      SameQ(Part($s("lst"), C1), CN1)), $s("expn"),
                                      Times(Part($s("lst"), C1), Apply(Plus, Rest($s("lst")))))),
                              $s("expn")))))));
}
