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
import static org.matheclipse.core.expression.F.CoefficientList;
import static org.matheclipse.core.expression.F.CompoundExpression;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Expand;
import static org.matheclipse.core.expression.F.Exponent;
import static org.matheclipse.core.expression.F.F_;
import static org.matheclipse.core.expression.F.First;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.Function;
import static org.matheclipse.core.expression.F.Greater;
import static org.matheclipse.core.expression.F.ISetDelayed;
import static org.matheclipse.core.expression.F.If;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Length;
import static org.matheclipse.core.expression.F.Less;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Map;
import static org.matheclipse.core.expression.F.MemberQ;
import static org.matheclipse.core.expression.F.Module;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Part;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.PolynomialQ;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Prepend;
import static org.matheclipse.core.expression.F.ReplaceAll;
import static org.matheclipse.core.expression.F.Rest;
import static org.matheclipse.core.expression.F.Rule;
import static org.matheclipse.core.expression.F.SameQ;
import static org.matheclipse.core.expression.F.Select;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Simplify;
import static org.matheclipse.core.expression.F.Slot1;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Sum;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.TrigExpand;
import static org.matheclipse.core.expression.F.TrigReduce;
import static org.matheclipse.core.expression.F.TrigToExp;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_;
import static org.matheclipse.core.expression.F.list;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.u_DEFAULT;
import static org.matheclipse.core.expression.F.v_;
import static org.matheclipse.core.expression.F.v_DEFAULT;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.Cosh;
import static org.matheclipse.core.expression.S.FSymbol;
import static org.matheclipse.core.expression.S.Indeterminate;
import static org.matheclipse.core.expression.S.List;
import static org.matheclipse.core.expression.S.Plus;
import static org.matheclipse.core.expression.S.Sinh;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.w;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Distrib;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandAlgebraicFunction;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandLinearProduct;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandToSum;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandTrigExpand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandTrigReduce;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandTrigReduceAux;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandTrigToExp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.F;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FreeFactors;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntegerPowerQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.KernelSubst;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NonfreeFactors;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NormalizeTrigReduce;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ProductQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RationalQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SimplifyIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SimplifyTerm;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SumQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.UnifySum;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.UnifyTerm;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.UnifyTerms;
import org.matheclipse.core.interfaces.IAST;

/**
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 * 
 */
class UtilityFunctions21 {
  public static IAST RULES =
      List(
          ISetDelayed(
              359, KernelSubst(u_, x_Symbol, $p("alst",
                  List)),
              If(AtomQ(u),
                  With(
                      list(Set($s("tmp"),
                          Select($s("alst"), Function(SameQ(Part(Slot1, C1), u)), C1))),
                      If(SameQ($s("tmp"), List()), u, Part($s("tmp"), C1, C2))),
                  If(IntegerPowerQ(u),
                      With(list(Set($s("tmp"), KernelSubst(Part(u, C1), x, $s("alst")))),
                          If(And(Less(Part(u, C2), C0), EqQ($s("tmp"), C0)), Indeterminate,
                              Power($s("tmp"), Part(u, C2)))),
                      If(Or(ProductQ(u), SumQ(u)),
                          Map(Function(KernelSubst(Slot1, x, $s("alst"))), u), u)))),
          ISetDelayed(360, ExpandAlgebraicFunction(Times($p(u, Plus), v_), x_Symbol), Condition(
              Map(Function(Times(Slot1, v)), u), Not(FreeQ(u, x)))),
          ISetDelayed(361,
              ExpandAlgebraicFunction(Times(Power($p(u, Plus), n_), v_DEFAULT), x_Symbol),
              Condition(
                  With(list(Set(w, Expand(Power(u, n), x))),
                      Condition(Map(Function(Times(Slot1, v)), w), SumQ(w))),
                  And(IGtQ(n, C0), Not(FreeQ(u, x))))),
          ISetDelayed(
              362, UnifySum(u_, x_Symbol), If(SumQ(u), Apply(Plus, UnifyTerms(Apply(List, u), x)),
                  SimplifyTerm(u, x))),
          ISetDelayed(
              363, UnifyTerms($p(
                  "lst"), x_),
              If(SameQ($s(
                  "lst"), List()), $s("lst"), UnifyTerm(First($s("lst")),
                      UnifyTerms(Rest($s("lst")), x), x))),
          ISetDelayed(
              364, UnifyTerm($p("term"), $p(
                  "lst"), x_),
              If(SameQ($s("lst"), List()), list($s("term")),
                  With(
                      list(
                          Set($s("tmp"),
                              Simplify(Times(First($s("lst")), Power($s("term"), CN1))))),
                      If(FreeQ($s("tmp"), x),
                          Prepend(Rest($s(
                              "lst")), Times(Plus(C1, $s("tmp")),
                                  $s("term"))),
                          Prepend(UnifyTerm($s("term"), Rest($s("lst")), x), First($s("lst"))))))),
          ISetDelayed(365, ExpandLinearProduct(v_, u_, a_, b_, x_Symbol),
              Condition(
                  Module(
                      list($s(
                          "lst")),
                      CompoundExpression(
                          Set($s("lst"),
                              CoefficientList(ReplaceAll(u, Rule(x,
                                  Times(Subtract(x, a), Power(b, CN1)))), x)),
                          Set($s("lst"), Map(Function(SimplifyTerm(Slot1,
                              x)), $s(
                                  "lst"))),
                          Module(list($s("ii")),
                              Sum(Times(v, Part($s("lst"), $s("ii")),
                                  Power(Plus(a, Times(b, x)), Subtract($s("ii"), C1))),
                                  list($s("ii"), C1, Length($s("lst"))))))),
                  And(FreeQ(list(a, b), x), PolynomialQ(u, x)))),
          ISetDelayed(366, ExpandTrigExpand(u_, F_, v_, m_, n_, x_Symbol),
              With(
                  list(
                      Set(w, ReplaceAll(Expand(Power(TrigExpand(F(Times(n, x))), m), x), Rule(x,
                          v)))),
                  If(SumQ(w), Map(Function(Times(u, Slot1)), w), Times(u, w)))),
          ISetDelayed(367, ExpandTrigReduce(u_, v_, x_Symbol),
              With(list(Set(w, ExpandTrigReduce(v, x))),
                  If(SumQ(w), Map(Function(Times(u, Slot1)), w), Times(u, w)))),
          ISetDelayed(368,
              ExpandTrigReduce(Times(Power($(F_, Plus(n_, v_DEFAULT)), m_DEFAULT), u_DEFAULT),
                  x_Symbol),
              Condition(
                  Module(list($s("nn")),
                      ReplaceAll(ExpandTrigReduce(Times(u, Power(F(Plus($s("nn"), v)), m)), x),
                          Rule($s("nn"), n))),
                  And(MemberQ(list(Sinh, Cosh), FSymbol), IntegerQ(m), RationalQ(n)))),
          ISetDelayed(369, ExpandTrigReduce(u_, x_Symbol), ExpandTrigReduceAux(u, x)),
          ISetDelayed(370, ExpandTrigReduceAux(u_, x_Symbol), With(
              list(Set(v, Expand(TrigReduce(u)))),
              If(SumQ(v), Map(Function(NormalizeTrigReduce(Slot1, x)), v),
                  NormalizeTrigReduce(v, x)))),
          ISetDelayed(371,
              NormalizeTrigReduce(Times(Power($(F_, u_), n_DEFAULT),
                  a_DEFAULT), x_Symbol),
              Condition(
                  Times(a, Power(F(ExpandToSum(u, x)),
                      n)),
                  And(FreeQ(list(FSymbol, a, n), x), PolynomialQ(u, x),
                      Greater(Exponent(u, x), C0)))),
          ISetDelayed(372, NormalizeTrigReduce(u_, x_Symbol), u),
          ISetDelayed(373, ExpandTrigToExp(u_, x_Symbol), ExpandTrigToExp(C1, u, x)),
          ISetDelayed(374, ExpandTrigToExp(u_, v_, x_Symbol),
              Module(list(Set(w, TrigToExp(v))),
                  CompoundExpression(
                      Set(w,
                          If(SumQ(w), Map(Function(SimplifyIntegrand(Times(u, Slot1), x)), w),
                              SimplifyIntegrand(Times(u, w), x))),
                      ExpandIntegrand(FreeFactors(w, x), NonfreeFactors(w, x), x)))),
          ISetDelayed(375, Distrib(u_, v_),
              If(SumQ(v), Map(Function(Times(u, Slot1)), v), Times(u, v))));
}
