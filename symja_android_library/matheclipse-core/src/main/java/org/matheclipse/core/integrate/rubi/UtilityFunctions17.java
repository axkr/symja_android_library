package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$rubi;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.Abs;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.Apply;
import static org.matheclipse.core.expression.F.AtomQ;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Catch;
import static org.matheclipse.core.expression.F.Coefficient;
import static org.matheclipse.core.expression.F.CompoundExpression;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Do;
import static org.matheclipse.core.expression.F.Equal;
import static org.matheclipse.core.expression.F.Exponent;
import static org.matheclipse.core.expression.F.First;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.FullSimplify;
import static org.matheclipse.core.expression.F.Function;
import static org.matheclipse.core.expression.F.Greater;
import static org.matheclipse.core.expression.F.ISetDelayed;
import static org.matheclipse.core.expression.F.If;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Length;
import static org.matheclipse.core.expression.F.Less;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Map;
import static org.matheclipse.core.expression.F.Max;
import static org.matheclipse.core.expression.F.Min;
import static org.matheclipse.core.expression.F.Module;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Order;
import static org.matheclipse.core.expression.F.OrderedQ;
import static org.matheclipse.core.expression.F.Part;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.PolynomialQuotient;
import static org.matheclipse.core.expression.F.PolynomialRemainder;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Prepend;
import static org.matheclipse.core.expression.F.ReplaceAll;
import static org.matheclipse.core.expression.F.ReplacePart;
import static org.matheclipse.core.expression.F.Rest;
import static org.matheclipse.core.expression.F.Return;
import static org.matheclipse.core.expression.F.Rule;
import static org.matheclipse.core.expression.F.SameQ;
import static org.matheclipse.core.expression.F.Scan;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Sign;
import static org.matheclipse.core.expression.F.Simplify;
import static org.matheclipse.core.expression.F.Slot1;
import static org.matheclipse.core.expression.F.Slot2;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Throw;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.Together;
import static org.matheclipse.core.expression.F.Unequal;
import static org.matheclipse.core.expression.F.UnsameQ;
import static org.matheclipse.core.expression.F.While;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.u_DEFAULT;
import static org.matheclipse.core.expression.F.v_;
import static org.matheclipse.core.expression.F.w_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.False;
import static org.matheclipse.core.expression.S.First;
import static org.matheclipse.core.expression.S.GCD;
import static org.matheclipse.core.expression.S.List;
import static org.matheclipse.core.expression.S.Max;
import static org.matheclipse.core.expression.S.Min;
import static org.matheclipse.core.expression.S.Plus;
import static org.matheclipse.core.expression.S.SameQ;
import static org.matheclipse.core.expression.S.Times;
import static org.matheclipse.core.expression.S.True;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.w;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.BinomialQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.CommonFactors;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ConstantFactor;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EveryQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandToSum;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExponentIn;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExponentInAux;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FactorOrder;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FractionQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FreeFactors;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FreeTerms;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LeadBase;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LogQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Map2;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.MinimumDegree;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.MonomialFactor;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.MostMainFactorPosition;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NegQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NonfreeFactors;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NonfreeTerms;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NonpositiveFactors;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PolynomialDivide;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PolynomialInAuxQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PolynomialInQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PolynomialInSubst;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PolynomialInSubstAux;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PositiveFactors;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PowerQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ProductQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RationalQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RemainingFactors;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Smallest;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SumQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.UnifyNegativeBaseFactors;
import org.matheclipse.core.interfaces.IAST;

/**
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class UtilityFunctions17 {
  public static IAST RULES =
      List(
          ISetDelayed(280,
              UnifyNegativeBaseFactors(
                  Times(u_DEFAULT, Power(Negate(v_), m_), Power(v_, n_DEFAULT))),
              Condition(
                  UnifyNegativeBaseFactors(Times(Power(CN1, n), u,
                      Power(Negate(v), Plus(m, n)))),
                  IntegerQ(n))),
          ISetDelayed(281, UnifyNegativeBaseFactors(
              u_), u),
          ISetDelayed(
              282, CommonFactors($p(
                  "lst")),
              Module(
                  List(
                      $s("lst1"), $s("lst2"), $s("lst3"), $s("lst4"), $s("common"), $s("base"), $s(
                          "num")),
                  CompoundExpression(
                      Set($s("lst1"), Map($rubi("NonabsurdNumberFactors"), $s("lst"))),
                      Set($s("lst2"), Map($rubi("AbsurdNumberFactors"), $s("lst"))),
                      Set($s("num"), Apply($rubi("AbsurdNumberGCD"), $s(
                          "lst2"))),
                      Set($s("common"), $s(
                          "num")),
                      Set($s("lst2"), Map(Function(Times(Slot1,
                          Power($s("num"), CN1))), $s(
                              "lst2"))),
                      While(True, CompoundExpression(
                          Set($s("lst3"), Map($rubi("LeadFactor"),
                              $s("lst1"))),
                          If(Apply(SameQ, $s("lst3")),
                              CompoundExpression(Set($s("common"),
                                  Times($s("common"), Part($s("lst3"), C1))),
                                  Set($s("lst1"), Map($rubi("RemainingFactors"), $s("lst1")))),
                              If(And(
                                  EveryQ(Function(And(LogQ(Slot1), IntegerQ(First(Slot1)),
                                      Greater(First(Slot1), C0))), $s("lst3")),
                                  EveryQ($rubi("RationalQ"),
                                      Set($s("lst4"), Map(Function(FullSimplify(
                                          Times(Slot1, Power(First($s("lst3")), CN1)))), $s(
                                              "lst3"))))),
                                  CompoundExpression(Set($s("num"), Apply(GCD, $s("lst4"))),
                                      Set($s("common"),
                                          Times($s("common"),
                                              Log(Power(Part(First($s("lst3")), C1), $s("num"))))),
                                      Set($s("lst2"), Map2(Function(
                                          Times(Slot1, Slot2, Power($s("num"), CN1))), $s("lst2"),
                                          $s("lst4"))),
                                      Set($s("lst1"), Map($rubi("RemainingFactors"), $s("lst1")))),
                                  CompoundExpression(
                                      Set($s("lst4"), Map($rubi("LeadDegree"),
                                          $s("lst1"))),
                                      If(And(Apply(SameQ, Map($rubi("LeadBase"), $s("lst1"))),
                                          EveryQ($rubi("RationalQ"), $s("lst4"))),
                                          CompoundExpression(Set($s("num"), Smallest($s("lst4"))),
                                              Set($s("base"), LeadBase(Part($s("lst1"), C1))),
                                              If(Unequal($s("num"), C0),
                                                  Set($s("common"),
                                                      Times($s("common"),
                                                          Power($s("base"), $s("num"))))),
                                              Set($s("lst2"),
                                                  Map2(
                                                      Function(Times(Slot1,
                                                          Power($s("base"),
                                                              Subtract(Slot2, $s("num"))))),
                                                      $s("lst2"), $s("lst4"))),
                                              Set($s("lst1"), Map($rubi("RemainingFactors"),
                                                  $s("lst1")))),
                                          If(And(
                                              Equal(Length($s("lst1")), C2), EqQ(
                                                  Plus(
                                                      LeadBase(Part($s("lst1"), C1)),
                                                      LeadBase(Part($s("lst1"), C2))),
                                                  C0),
                                              NeQ(Part($s("lst1"), C1), C1),
                                              IntegerQ(Part($s("lst4"), C1)),
                                              FractionQ(Part($s("lst4"), C2))),
                                              CompoundExpression(Set($s("num"), Min($s("lst4"))),
                                                  Set($s("base"), LeadBase(Part($s("lst1"), C2))),
                                                  If(Unequal($s("num"), C0),
                                                      Set($s("common"),
                                                          Times($s("common"),
                                                              Power($s("base"), $s("num"))))),
                                                  Set($s("lst2"),
                                                      List(
                                                          Times(Part($s("lst2"), C1),
                                                              Power(CN1, Part($s("lst4"), C1))),
                                                          Part($s("lst2"), C2))),
                                                  Set($s("lst2"),
                                                      Map2(
                                                          Function(Times(Slot1,
                                                              Power($s("base"),
                                                                  Subtract(Slot2, $s("num"))))),
                                                          $s("lst2"), $s("lst4"))),
                                                  Set($s(
                                                      "lst1"),
                                                      Map($rubi("RemainingFactors"), $s("lst1")))),
                                              If(And(Equal(Length($s("lst1")), C2),
                                                  EqQ(Plus(LeadBase(Part($s("lst1"), C1)),
                                                      LeadBase(Part($s("lst1"), C2))), C0),
                                                  NeQ(Part($s("lst1"), C2), C1),
                                                  IntegerQ(Part($s("lst4"), C2)),
                                                  FractionQ(Part($s("lst4"), C1))),
                                                  CompoundExpression(
                                                      Set($s("num"), Min($s("lst4"))),
                                                      Set($s("base"),
                                                          LeadBase(Part($s("lst1"), C1))),
                                                      If(Unequal($s("num"), C0),
                                                          Set($s("common"),
                                                              Times($s("common"),
                                                                  Power($s("base"), $s("num"))))),
                                                      Set($s("lst2"),
                                                          List(Part($s("lst2"), C1),
                                                              Times(Part($s("lst2"), C2), Power(CN1,
                                                                  Part($s("lst4"), C2))))),
                                                      Set($s("lst2"),
                                                          Map2(
                                                              Function(Times(Slot1,
                                                                  Power($s("base"),
                                                                      Subtract(Slot2, $s("num"))))),
                                                              $s("lst2"), $s("lst4"))),
                                                      Set($s("lst1"),
                                                          Map($rubi("RemainingFactors"),
                                                              $s("lst1")))),
                                                  CompoundExpression(
                                                      Set($s("num"),
                                                          MostMainFactorPosition($s("lst3"))),
                                                      Set($s("lst2"),
                                                          ReplacePart($s("lst2"),
                                                              Times(Part($s("lst3"), $s("num")),
                                                                  Part($s("lst2"), $s("num"))),
                                                              $s("num"))),
                                                      Set($s("lst1"),
                                                          ReplacePart($s("lst1"),
                                                              RemainingFactors(
                                                                  Part($s("lst1"), $s("num"))),
                                                              $s("num")))))))))),
                          If(EveryQ(Function(SameQ(Slot1, C1)), $s("lst1")),
                              Return(Prepend($s("lst2"), $s("common"))))))))),
          ISetDelayed(
              283, MostMainFactorPosition($p("lst",
                  List)),
              Module(List(Set($s("§factor"), C1), Set($s("num"), C1), $s("ii")),
                  CompoundExpression(
                      Do(If(Greater(FactorOrder(Part($s("lst"), $s("ii")), $s("§factor")), C0),
                          CompoundExpression(Set($s("§factor"), Part($s("lst"), $s("ii"))),
                              Set($s("num"), $s("ii")))),
                          List($s("ii"), Length($s("lst")))),
                      $s("num")))),
          ISetDelayed(
              284, FactorOrder(u_, v_), If(SameQ(u, C1), If(SameQ(v,
                  C1), C0, CN1), If(SameQ(v, C1), C1,
                      Order(u, v)))),
          ISetDelayed(285, Smallest($p("num1"), $p("num2")),
              If(Greater($s("num1"), C0),
                  If(Greater($s("num2"), C0), Min($s("num1"), $s("num2")), C0),
                  If(Greater($s("num2"), C0), C0, Max($s("num1"), $s("num2"))))),
          ISetDelayed(286, Smallest($p("lst", List)),
              Module(List(Set($s("num"), Part($s("lst"), C1))),
                  CompoundExpression(
                      Scan(Function(Set($s("num"), Smallest($s("num"), Slot1))), Rest($s("lst"))),
                      $s("num")))),
          ISetDelayed(287, MonomialFactor(u_, x_Symbol),
              If(AtomQ(u), If(SameQ(u, x), List(C1, C1), List(C0, u)),
                  If(PowerQ(u),
                      If(IntegerQ(Part(u, C2)),
                          With(List(Set($s("lst"), MonomialFactor(Part(u, C1), x))),
                              List(Times(Part($s("lst"), C1), Part(u, C2)),
                                  Power(Part($s("lst"), C2), Part(u, C2)))),
                          If(And(SameQ(Part(u, C1), x), FreeQ(Part(u, C2), x)),
                              List(Part(u, C2), C1), List(C0, u))),
                      If(ProductQ(u),
                          With(
                              List(
                                  Set($s("lst1"), MonomialFactor(First(u),
                                      x)),
                                  Set($s("lst2"), MonomialFactor(Rest(u), x))),
                              List(
                                  Plus(Part($s(
                                      "lst1"), C1), Part($s("lst2"),
                                          C1)),
                                  Times(Part($s("lst1"), C2), Part($s("lst2"), C2)))),
                          If(SumQ(u),
                              Module(List($s("lst"), $s("deg")),
                                  CompoundExpression(
                                      Set($s(
                                          "lst"),
                                          Map(Function(MonomialFactor(Slot1, x)), Apply(List, u))),
                                      Set($s("deg"), Part($s("lst"), C1, C1)),
                                      Scan(
                                          Function(
                                              Set($s("deg"),
                                                  MinimumDegree($s("deg"), Part(Slot1, C1)))),
                                          Rest($s("lst"))),
                                      If(Or(
                                          EqQ($s(
                                              "deg"), C0),
                                          And(RationalQ($s("deg")), Less($s("deg"), C0))),
                                          List(C0, u),
                                          List($s("deg"),
                                              Apply(
                                                  Plus, Map(
                                                      Function(
                                                          Times(
                                                              Power(x,
                                                                  Subtract(Part(Slot1, C1),
                                                                      $s("deg"))),
                                                              Part(Slot1, C2))),
                                                      $s("lst"))))))),
                              List(C0, u)))))),
          ISetDelayed(288, MinimumDegree($p("deg1"), $p("deg2")), If(RationalQ($s("deg1")),
              If(RationalQ($s("deg2")), Min($s("deg1"), $s("deg2")), $s("deg1")),
              If(RationalQ($s("deg2")), $s("deg2"),
                  With(
                      List(Set($s("deg"), Simplify(Subtract($s("deg1"), $s("deg2"))))),
                      If(RationalQ($s("deg")), If(Greater($s("deg"), C0), $s("deg2"),
                          $s("deg1")),
                          If(OrderedQ(List($s("deg1"), $s("deg2"))), $s("deg1"), $s("deg2"))))))),
          ISetDelayed(289, ConstantFactor(u_, x_Symbol), If(FreeQ(u, x), List(u, C1),
              If(AtomQ(u), List(C1, u), If(And(PowerQ(u), FreeQ(Part(u, C2), x)), Module(
                  List(Set($s("lst"), ConstantFactor(Part(u, C1), x)), $s("tmp")),
                  If(IntegerQ(Part(u, C2)),
                      List(Power(Part($s("lst"), C1), Part(u, C2)),
                          Power(Part($s("lst"), C2), Part(u, C2))),
                      CompoundExpression(Set($s("tmp"), PositiveFactors(Part($s("lst"), C1))),
                          If(SameQ($s("tmp"), C1), List(C1, u),
                              List(Power($s("tmp"), Part(u, C2)),
                                  Power(Times(NonpositiveFactors(Part($s("lst"), C1)),
                                      Part($s("lst"), C2)), Part(u, C2))))))),
                  If(ProductQ(u), With(
                      List(Set($s("lst"), Map(Function(ConstantFactor(Slot1, x)), Apply(List, u)))),
                      List(Apply(Times, Map(First, $s("lst"))),
                          Apply(Times, Map(Function(Part(Slot1, C2)), $s("lst"))))),
                      If(SumQ(u), With(
                          List(Set($s("lst1"),
                              Map(Function(ConstantFactor(Slot1, x)), Apply(List, u)))),
                          If(Apply(SameQ, Map(Function(Part(Slot1, C2)), $s("lst1"))),
                              List(Apply(Plus, Map(First, $s("lst1"))), Part($s("lst1"), C1, C2)),
                              With(List(Set($s("lst2"), CommonFactors(Map(First, $s("lst1"))))),
                                  List(First($s("lst2")),
                                      Apply(Plus,
                                          Map2(Times, Rest($s("lst2")),
                                              Map(Function(Part(Slot1, C2)), $s("lst1")))))))),
                          List(C1, u))))))),
          ISetDelayed(
              290, PositiveFactors(u_), If(
                  EqQ(u, C0), C1, If(
                      RationalQ(u), Abs(u), If(GtQ(u, C0), u,
                          If(ProductQ(u), Map($rubi("PositiveFactors"), u), C1))))),
          ISetDelayed(291, NonpositiveFactors(u_),
              If(EqQ(u, C0), u,
                  If(RationalQ(u), Sign(u),
                      If(GtQ(u, C0), C1,
                          If(ProductQ(u), Map($rubi("NonpositiveFactors"), u), u))))),
          ISetDelayed(
              292, PolynomialInQ(u_, v_, x_Symbol), PolynomialInAuxQ(u,
                  NonfreeFactors(NonfreeTerms(v, x), x), x)),
          ISetDelayed(
              293, PolynomialInAuxQ(u_, v_, x_), If(
                  SameQ(u, v), True, If(
                      AtomQ(u), UnsameQ(u, x), If(
                          PowerQ(u), If(
                              And(PowerQ(v), SameQ(Part(u, C1), Part(v,
                                  C1))),
                              IGtQ(Times(Part(u, C2), Power(Part(v, C2), CN1)), C0),
                              And(IGtQ(Part(u, C2), C0), PolynomialInAuxQ(Part(u, C1), v, x))),
                          If(Or(SumQ(u), ProductQ(u)),
                              Catch(CompoundExpression(
                                  Scan(Function(
                                      If(Not(PolynomialInAuxQ(Slot1, v, x)), Throw(False))), u),
                                  True)),
                              False))))),
          ISetDelayed(294, ExponentIn(u_, v_, x_Symbol),
              ExponentInAux(u, NonfreeFactors(NonfreeTerms(v, x), x), x)),
          ISetDelayed(295, ExponentInAux(u_, v_, x_),
              If(SameQ(u, v), C1,
                  If(AtomQ(u), C0,
                      If(PowerQ(u), If(And(PowerQ(v), SameQ(Part(u, C1), Part(v, C1))),
                          Times(Part(u, C2), Power(Part(v, C2), CN1)),
                          Times(Part(u, C2), ExponentInAux(Part(u, C1), v, x))),
                          If(ProductQ(u),
                              Apply(Plus,
                                  Map(Function(ExponentInAux(Slot1, v, x)), Apply(List, u))),
                              Apply(Max, Map(Function(ExponentInAux(Slot1, v, x)),
                                  Apply(List, u)))))))),
          ISetDelayed(296, PolynomialInSubst(u_, v_, x_Symbol), With(
              List(Set(w, NonfreeTerms(v, x))),
              ReplaceAll(PolynomialInSubstAux(u, NonfreeFactors(w, x), x),
                  List(Rule(x, Times(Subtract(x, FreeTerms(v, x)),
                      Power(FreeFactors(w, x), CN1))))))),
          ISetDelayed(297, PolynomialInSubstAux(u_, v_, x_),
              If(SameQ(u, v), x,
                  If(AtomQ(u), u,
                      If(PowerQ(u),
                          If(And(PowerQ(v), SameQ(Part(u, C1), Part(v, C1))),
                              Power(x, Times(Part(u, C2), Power(Part(v, C2),
                                  CN1))),
                              Power(PolynomialInSubstAux(Part(u, C1), v, x), Part(u, C2))),
                          Map(Function(PolynomialInSubstAux(Slot1, v, x)), u))))),
          ISetDelayed(298, PolynomialDivide(u_, v_, x_Symbol),
              Module(
                  List(Set($s("quo"), PolynomialQuotient(
                      u, v, x)), Set($s("rem"), PolynomialRemainder(u, v, x)), $s("free"),
                      $s("monomial")),
                  CompoundExpression(
                      Set($s("quo"),
                          Apply(Plus,
                              Map(Function(Simp(
                                  Together(
                                      Times(Coefficient($s("quo"), x, Slot1), Power(x, Slot1))),
                                  x)), Exponent($s("quo"), x, List)))),
                      Set($s("rem"), Together($s("rem"))),
                      Set($s("free"), FreeFactors($s("rem"), x)),
                      Set($s("rem"), NonfreeFactors($s("rem"), x)),
                      Set($s("monomial"), Power(x, Exponent($s("rem"), x, Min))),
                      If(NegQ(Coefficient($s("rem"), x, C0)),
                          Set($s("monomial"), Negate($s("monomial")))),
                      Set($s("rem"),
                          Apply(Plus,
                              Map(Function(Simp(Together(Times(Coefficient($s("rem"), x, Slot1),
                                  Power(x, Slot1), Power($s("monomial"), CN1))), x)),
                                  Exponent($s("rem"), x, List)))),
                      If(BinomialQ(v, x),
                          Plus($s("quo"),
                              Times($s("free"), $s("monomial"), $s("rem"),
                                  Power(ExpandToSum(v, x), CN1))),
                          Plus($s("quo"),
                              Times($s("free"), $s("monomial"), $s("rem"), Power(v, CN1))))))),
          ISetDelayed(299, PolynomialDivide(u_, v_, w_, x_Symbol),
              ReplaceAll(
                  PolynomialDivide(PolynomialInSubst(u, w, x), PolynomialInSubst(v, w, x), x),
                  List(Rule(x, w)))));
}
