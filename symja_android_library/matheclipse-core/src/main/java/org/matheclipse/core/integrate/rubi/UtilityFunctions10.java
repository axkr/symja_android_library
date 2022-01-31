package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;

/**
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 * 
 */
class UtilityFunctions10 {
  public static IAST RULES = List(
      ISetDelayed(142, RationalFunctionFactors(u_, x_Symbol),
          If(ProductQ(u), Map(Function(If(RationalFunctionQ(Slot1, x), Slot1, C1)), u),
              If(RationalFunctionQ(u, x), u, C1))),
      ISetDelayed(143, NonrationalFunctionFactors(u_, x_Symbol),
          If(ProductQ(u), Map(Function(If(RationalFunctionQ(Slot1, x), C1, Slot1)), u),
              If(RationalFunctionQ(u, x), C1, u))),
      ISetDelayed(144, RationalFunctionExponents(u_, x_Symbol),
          If(PolynomialQ(u, x), List(Exponent(u, x), C0),
              If(IntegerPowerQ(u), If(Greater(Part(u, C2), C0),
                  Times(Part(u, C2), RationalFunctionExponents(Part(u, C1), x)),
                  Times(CN1, Part(u, C2), Reverse(RationalFunctionExponents(Part(u, C1), x)))),
                  If(ProductQ(u),
                      Plus(
                          RationalFunctionExponents(First(u), x),
                          RationalFunctionExponents(Rest(u), x)),
                      If(SumQ(u),
                          With(List(Set(v, Together(u))),
                              If(SumQ(v),
                                  Module(List($s("lst1"), $s("lst2")),
                                      CompoundExpression(
                                          Set($s("lst1"), RationalFunctionExponents(First(u), x)),
                                          Set($s("lst2"), RationalFunctionExponents(Rest(u), x)),
                                          List(
                                              Max(Plus(Part($s("lst1"), C1), Part($s("lst2"), C2)),
                                                  Plus(Part($s("lst2"), C1), Part($s("lst1"), C2))),
                                              Plus(Part($s("lst1"), C2), Part($s("lst2"), C2))))),
                                  RationalFunctionExponents(v, x))),
                          List(C0, C0)))))),
      ISetDelayed(
          145, RationalFunctionExpand(Times(u_,
              Power(v_, n_)), x_Symbol),
          Condition(
              With(
                  List(Set(w,
                      RationalFunctionExpand(u, x))),
                  If(SumQ(w), Map(Function(Times(Slot1, Power(v, n))), w), Times(w, Power(v, n)))),
              And(FractionQ(n), UnsameQ(v, x)))),
      ISetDelayed(146, RationalFunctionExpand(u_, x_Symbol), Module(List(v, w), CompoundExpression(
          Set(v, ExpandIntegrand(u, x)),
          If(And(UnsameQ(v, u), Not(MatchQ(u, Condition(
              Times(Power(x, m_DEFAULT), Power(Plus(c_, Times(d_DEFAULT, x)), p_),
                  Power(Plus(a_, Times(b_DEFAULT, Power(x, n_))), CN1)),
              And(FreeQ(List(a, b, c, d, p), x), IntegersQ(m, n), Equal(m, Subtract(n, C1))))))), v,
              CompoundExpression(Set(v, ExpandIntegrand(RationalFunctionFactors(u, x), x)),
                  Set(w, NonrationalFunctionFactors(u, x)),
                  If(SumQ(v), Map(Function(Times(Slot1, w)), v), Times(v, w))))))),
      ISetDelayed(147, PolyGCD(u_, v_, x_Symbol), NonfreeFactors(PolynomialGCD(u, v), x)),
      ISetDelayed(148, AlgebraicFunctionQ(u_, x_Symbol, Optional($p("flag"), False)), If(
          Or(AtomQ(u), FreeQ(u, x)), True,
          If(And(PowerQ(u), Or(RationalQ(Part(u, C2)), And($s("flag"), FreeQ(Part(u, C2), x)))),
              AlgebraicFunctionQ(Part(u, C1), x, $s("flag")),
              If(Or(ProductQ(u), SumQ(u)),
                  Catch(CompoundExpression(Scan(
                      Function(If(Not(AlgebraicFunctionQ(Slot1, x, $s("flag"))), Throw(False))), u),
                      True)),
                  If(ListQ(u),
                      If(SameQ(u, List()), True,
                          If(AlgebraicFunctionQ(First(u), x, $s("flag")),
                              AlgebraicFunctionQ(Rest(u), x, $s("flag")), False)),
                      False))))),
      ISetDelayed(149, QuotientOfLinearsQ(u_, x_Symbol), If(ListQ(u),
          Catch(CompoundExpression(
              Scan(Function(If(Not(QuotientOfLinearsQ(Slot1, x)), Throw(False))), u), True)),
          And(QuotientOfLinearsP(u, x),
              $(Function(And(NeQ(Part(Slot1, C2), C0), NeQ(Part(Slot1, C4), C0))),
                  QuotientOfLinearsParts(u, x))))),
      ISetDelayed(
          150, QuotientOfLinearsP(Times(a_, u_),
              x_),
          Condition(QuotientOfLinearsP(u, x), FreeQ(a, x))),
      ISetDelayed(151, QuotientOfLinearsP(Plus(a_, u_), x_),
          Condition(QuotientOfLinearsP(u, x), FreeQ(a, x))),
      ISetDelayed(152, QuotientOfLinearsP(Power(u_,
          CN1), x_), QuotientOfLinearsP(u, x)),
      ISetDelayed(153, QuotientOfLinearsP(u_,
          x_), Condition(True, LinearQ(u, x))),
      ISetDelayed(
          154, QuotientOfLinearsP(Times(u_, Power(v_,
              CN1)), x_),
          Condition(True, And(LinearQ(u, x), LinearQ(v, x)))),
      ISetDelayed(155, QuotientOfLinearsP(u_, x_), Or(SameQ(u, x), FreeQ(u, x))),
      ISetDelayed(156, QuotientOfLinearsParts(Times(a_, u_), x_),
          Condition(Apply(Function(List(Times(a, Slot1), Times(a, Slot2), Slot(C3), Slot(C4))),
              QuotientOfLinearsParts(u, x)), FreeQ(a, x))),
      ISetDelayed(157, QuotientOfLinearsParts(Plus(a_, u_), x_),
          Condition(Apply(Function(List(Plus(Slot1, Times(a, Slot(C3))),
              Plus(Slot2, Times(a, Slot(C4))), Slot(C3), Slot(C4))), QuotientOfLinearsParts(u, x)),
              FreeQ(a, x))),
      ISetDelayed(158, QuotientOfLinearsParts(Power(u_, CN1), x_),
          Apply(Function(List(Slot(C3), Slot(C4), Slot1, Slot2)), QuotientOfLinearsParts(u, x))),
      ISetDelayed(159, QuotientOfLinearsParts(u_, x_),
          Condition(List(Coefficient(u, x, C0), Coefficient(u, x, C1), C1, C0), LinearQ(u, x))),
      ISetDelayed(160, QuotientOfLinearsParts(Times(u_, Power(v_, CN1)), x_),
          Condition(List(Coefficient(u, x, C0), Coefficient(u, x, C1), Coefficient(v, x, C0),
              Coefficient(v, x, C1)), And(LinearQ(u, x), LinearQ(v, x)))),
      ISetDelayed(161, QuotientOfLinearsParts(u_, x_), If(SameQ(u, x), List(C0, C1, C1, C0), If(
          FreeQ(u, x), List(u, C0, C1, C0),
          CompoundExpression(Print($str("QuotientOfLinearsParts error!")), List(u, C0, C1, C0))))));
}
