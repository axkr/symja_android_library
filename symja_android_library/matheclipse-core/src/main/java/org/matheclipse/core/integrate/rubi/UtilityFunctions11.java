package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;

/**
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 * 
 */
class UtilityFunctions11 {
  public static IAST RULES =
      List(
          ISetDelayed(162, SubstForFractionalPowerOfQuotientOfLinears(u_, x_Symbol),
              Module(
                  List(Set($s("lst"),
                      FractionalPowerOfQuotientOfLinears(u, C1, False, x))),
                  If(Or(AtomQ($s(
                      "lst")), AtomQ(
                          Part($s("lst"), C2))),
                      False,
                      With(
                          List(Set(n, Part($s("lst"),
                              C1)), Set($s("tmp"),
                                  Part($s("lst"), C2))),
                          CompoundExpression(
                              Set($s("lst"), QuotientOfLinearsParts($s("tmp"),
                                  x)),
                              With(
                                  List(
                                      Set(a, Part($s("lst"), C1)), Set(b, Part($s(
                                          "lst"), C2)),
                                      Set(c, Part($s("lst"), C3)), Set(d, Part($s("lst"), C4))),
                                  If(EqQ(d, C0), False,
                                      CompoundExpression(
                                          Set($s("lst"),
                                              Simplify(Times(Power(x, Subtract(n, C1)),
                                                  SubstForFractionalPower(u, $s("tmp"), n,
                                                      Times(Plus(Negate(a), Times(c, Power(x, n))),
                                                          Power(Subtract(b, Times(d, Power(x, n))),
                                                              CN1)),
                                                      x),
                                                  Power(Subtract(b, Times(d, Power(x, n))), CN2)))),
                                          List(NonfreeFactors($s("lst"), x), n, $s("tmp"),
                                              Times(FreeFactors($s("lst"), x),
                                                  Subtract(Times(b, c), Times(a, d)))))))))))),
          ISetDelayed(163, SubstForFractionalPowerQ(u_, v_, x_Symbol),
              If(Or(AtomQ(u), FreeQ(u, x)), True,
                  If(FractionalPowerQ(u), SubstForFractionalPowerAuxQ(u, v, x),
                      Catch(CompoundExpression(
                          Scan(Function(
                              If(Not(SubstForFractionalPowerQ(Slot1, v, x)), Throw(False))), u),
                          True))))),
          ISetDelayed(164, SubstForFractionalPowerAuxQ(u_, v_, x_),
              If(AtomQ(u), False,
                  If(And(FractionalPowerQ(u), EqQ(Part(u, C1), v)), True, Catch(CompoundExpression(
                      Scan(Function(If(SubstForFractionalPowerAuxQ(Slot1, v, x), Throw(True))), u),
                      False))))),
          ISetDelayed(
              165, FractionalPowerOfQuotientOfLinears(u_, n_, v_, x_), If(
                  Or(AtomQ(u), FreeQ(u,
                      x)),
                  List(n, v), If(
                      CalculusQ(u), False, If(And(FractionalPowerQ(u),
                          QuotientOfLinearsQ(Part(u, C1), x), Not(LinearQ(Part(u,
                              C1), x)),
                          Or(FalseQ(v), EqQ(Part(u, C1), v))),
                          List(LCM(Denominator(Part(u, C2)), n), Part(u, C1)),
                          Catch(
                              Module(List(Set($s("lst"), List(n, v))),
                                  CompoundExpression(Scan(Function(If(
                                      AtomQ(Set($s("lst"),
                                          FractionalPowerOfQuotientOfLinears(Slot1,
                                              Part($s("lst"), C1), Part($s("lst"), C2), x))),
                                      Throw(False))), u), $s("lst")))))))),
          ISetDelayed(166, SubstForInverseFunctionOfQuotientOfLinears(u_, x_Symbol),
              With(
                  List(Set($s("tmp"),
                      InverseFunctionOfQuotientOfLinears(u, x))),
                  If(AtomQ($s("tmp")), False,
                      With(
                          List(
                              Set(h, InverseFunction(Head($s("tmp")))), Set($s("lst"),
                                  QuotientOfLinearsParts(Part($s("tmp"), C1), x))),
                          With(
                              List(
                                  Set(a, Part($s("lst"), C1)), Set(b, Part($s("lst"), C2)), Set(c,
                                      Part($s("lst"), C3)),
                                  Set(d, Part($s("lst"), C4))),
                              List(
                                  Times(
                                      SubstForInverseFunction(u, $s("tmp"),
                                          Times(Plus(Negate(a), Times(c, $(h, x))),
                                              Power(Subtract(b, Times(d, $(h, x))), CN1)),
                                          x),
                                      D($(h, x), x), Power(Subtract(b, Times(d, $(h, x))), CN2)),
                                  $s("tmp"), Subtract(Times(b, c), Times(a, d)))))))),
          ISetDelayed(167, InverseFunctionOfQuotientOfLinears(u_, x_Symbol),
              If(Or(AtomQ(u), CalculusQ(u), FreeQ(u, x)), False,
                  If(And(InverseFunctionQ(u), QuotientOfLinearsQ(Part(u, C1), x)), u,
                      Module(List($s("tmp")), Catch(CompoundExpression(Scan(Function(If(
                          Not(AtomQ(Set($s("tmp"), InverseFunctionOfQuotientOfLinears(Slot1, x)))),
                          Throw($s("tmp")))), u), False)))))),
          ISetDelayed(168, SubstForFractionalPower(u_, v_, n_, w_, x_Symbol),
              If(AtomQ(u), If(SameQ(u, x), w, u),
                  If(And(FractionalPowerQ(u), EqQ(Part(u, C1), v)), Power(x, Times(n, Part(u, C2))),
                      Map(Function(SubstForFractionalPower(Slot1, v, n, w, x)), u)))),
          ISetDelayed(169, SubstForInverseFunction(u_, v_, x_Symbol), SubstForInverseFunction(u, v,
              Times(Plus(Negate(Coefficient(Part(v, C1), x, C0)), $(InverseFunction(Head(v)), x)),
                  Power(Coefficient(Part(v, C1), x, C1), CN1)),
              x)),
          ISetDelayed(170, SubstForInverseFunction(u_, v_, w_, x_Symbol),
              If(AtomQ(u), If(SameQ(u, x), w, u),
                  If(And(SameQ(Head(u), Head(v)), EqQ(Part(u, C1), Part(v, C1))), x,
                      Map(Function(SubstForInverseFunction(Slot1, v, w, x)), u)))),
          ISetDelayed(171, AbsurdNumberQ(u_), RationalQ(
              u)),
          ISetDelayed(172, AbsurdNumberQ(Power(u_, v_)),
              And(RationalQ(u), Greater(u, C0), FractionQ(v))),
          ISetDelayed(173, AbsurdNumberQ(Times(u_, v_)), And(AbsurdNumberQ(u), AbsurdNumberQ(v))),
          ISetDelayed(174, AbsurdNumberFactors(u_),
              If(AbsurdNumberQ(u), u,
                  If(ProductQ(u), Map($rubi("AbsurdNumberFactors"), u), NumericFactor(u)))),
          ISetDelayed(
              175, NonabsurdNumberFactors(u_),
              If(AbsurdNumberQ(u), C1,
                  If(ProductQ(u), Map($rubi("NonabsurdNumberFactors"), u), NonnumericFactors(u)))),
          ISetDelayed(176, FactorAbsurdNumber(m_), If(RationalQ(m), FactorInteger(m), If(PowerQ(m),
              Map(Function(List(Part(Slot1, C1), Times(Part(Slot1, C2), Part(m, C2)))),
                  FactorInteger(Part(m, C1))),
              CombineExponents(Sort(Flatten(Map($rubi("FactorAbsurdNumber"), Apply(List, m)), C1),
                  Function(Less(Part(Slot1, C1), Part(Slot2, C1)))))))),
          ISetDelayed(177, CombineExponents($p("lst")),
              If(Less(Length($s("lst")), C2), $s("lst"),
                  If(Equal(Part($s("lst"), C1, C1), Part($s("lst"), C2, C1)),
                      CombineExponents(Prepend(Drop($s("lst"), C2),
                          List(Part($s("lst"), C1, C1),
                              Plus(Part($s("lst"), C1, C2), Part($s("lst"), C2, C2))))),
                      Prepend(CombineExponents(Rest($s("lst"))), First($s("lst")))))),
          ISetDelayed(
              178, AbsurdNumberGCD($ps("seq")),
              With(
                  List(Set($s("lst"), List($s("seq")))),
                  If(Equal(Length($s("lst")), C1), First($s("lst")),
                      AbsurdNumberGCDList(FactorAbsurdNumber(First($s("lst"))),
                          FactorAbsurdNumber(Apply($rubi("AbsurdNumberGCD"), Rest($s("lst")))))))),
          ISetDelayed(179, AbsurdNumberGCDList($p("lst1"), $p("lst2")), If(
              SameQ($s("lst1"), List()),
              Apply(Times,
                  Map(Function(Power(Part(Slot1, C1), Min(Part(Slot1, C2), C0))), $s("lst2"))),
              If(SameQ($s("lst2"), List()),
                  Apply(Times,
                      Map(Function(Power(Part(Slot1, C1), Min(Part(Slot1, C2), C0))), $s("lst1"))),
                  If(Equal(Part($s("lst1"), C1, C1), Part($s("lst2"), C1, C1)),
                      If(LessEqual(Part($s("lst1"), C1, C2), Part($s("lst2"), C1, C2)),
                          Times(Power(Part($s("lst1"), C1, C1), Part($s("lst1"), C1, C2)),
                              AbsurdNumberGCDList(Rest($s("lst1")), Rest($s("lst2")))),
                          Times(Power(Part($s("lst1"), C1, C1), Part($s("lst2"), C1, C2)),
                              AbsurdNumberGCDList(Rest($s("lst1")), Rest($s("lst2"))))),
                      If(Less(Part($s("lst1"), C1, C1), Part($s("lst2"), C1, C1)),
                          If(Less(Part($s("lst1"), C1, C2), C0),
                              Times(Power(Part($s("lst1"), C1, C1), Part($s("lst1"), C1, C2)),
                                  AbsurdNumberGCDList(Rest($s("lst1")), $s("lst2"))),
                              AbsurdNumberGCDList(Rest($s("lst1")), $s("lst2"))),
                          If(Less(Part($s("lst2"), C1, C2), C0),
                              Times(Power(Part($s("lst2"), C1, C1), Part($s("lst2"), C1, C2)),
                                  AbsurdNumberGCDList($s("lst1"), Rest($s("lst2")))),
                              AbsurdNumberGCDList($s("lst1"), Rest($s("lst2"))))))))),
          ISetDelayed(180, NormalizeIntegrand(u_, x_Symbol),
              With(List(Set(v, NormalizeLeadTermSigns(NormalizeIntegrandAux(u, x)))),
                  If(SameQ(v, NormalizeLeadTermSigns(u)), u, v))),
          ISetDelayed(181, NormalizeIntegrandAux(u_, x_Symbol),
              If(SumQ(u), Map(Function(NormalizeIntegrandAux(Slot1, x)), u),
                  If(ProductQ(MergeMonomials(u, x)),
                      Map(Function(NormalizeIntegrandFactor(Slot1, x)), MergeMonomials(u, x)),
                      NormalizeIntegrandFactor(MergeMonomials(u, x), x)))));
}
