package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;

/**
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 * 
 */
class UtilityFunctions7 {
  public static IAST RULES =
      List(
          ISetDelayed(
              82, GeneralizedTrinomialParts(Times(u_,
                  Power(x_, m_DEFAULT)), x_Symbol),
              Condition(With(List(Set($s("lst"), GeneralizedTrinomialParts(u, x))),
                  Condition(
                      List(Part($s("lst"), C1), Part($s("lst"), C2), Part($s("lst"), C3),
                          Plus(m, Part($s("lst"), C4)), Plus(m, Part($s("lst"), C5))),
                      And(ListQ($s("lst")), NeQ(Plus(m, Part($s("lst"), C4)), C0),
                          NeQ(Plus(m, Part($s("lst"), C5)), C0)))),
                  FreeQ(m, x))),
          ISetDelayed(83, GeneralizedTrinomialParts(Times(u_, Power(x_, m_DEFAULT)), x_Symbol),
              Condition(
                  With(List(Set($s("lst"), TrinomialParts(u, x))),
                      Condition(
                          List(Part($s("lst"), C1), Part($s("lst"), C2), Part($s("lst"), C3),
                              Plus(m, Part($s("lst"), C4)), m),
                          And(ListQ($s("lst")), NeQ(Plus(m, Part($s("lst"), C4)), C0)))),
                  FreeQ(m, x))),
          ISetDelayed(84, GeneralizedTrinomialParts(u_, x_Symbol), False),
          ISetDelayed(85, IntPart(Times(m_, u_), Optional(n_, C1)),
              Condition(IntPart(u, Times(m, n)), RationalQ(m))),
          ISetDelayed(86, IntPart(u_, Optional(n_, C1)),
              If(RationalQ(u), IntegerPart(Times(n, u)),
                  If(SumQ(u), Map(Function(IntPart(Slot1, n)), u), C0))),
          ISetDelayed(87, FracPart(Times(m_, u_), Optional(n_, C1)),
              Condition(FracPart(u, Times(m, n)), RationalQ(m))),
          ISetDelayed(88, FracPart(u_, Optional(n_, C1)),
              If(RationalQ(u), FractionalPart(Times(n, u)),
                  If(SumQ(u), Map(Function(FracPart(Slot1, n)), u), Times(n, u)))),
          ISetDelayed(89, NumericFactor(u_), If(NumberQ(u),
              If(EqQ(Im(u), C0), u, If(EqQ(Re(u), C0), Im(u), C1)),
              If(PowerQ(u), If(And(RationalQ(Part(u, C1)), FractionQ(Part(u, C2))),
                  If(Greater(Part(u, C2),
                      C0), Power(Denominator(Part(u, C1)), CN1),
                      Power(Denominator(Power(Part(u, C1), CN1)), CN1)),
                  C1),
                  If(ProductQ(u), Map($rubi("NumericFactor"), u), If(SumQ(u), If(
                      Less(LeafCount(u), ZZ(50L)),
                      $(Function(If(SumQ(Slot1), C1, NumericFactor(Slot1))), ContentFactor(u)),
                      With(List(Set(m, NumericFactor(First(u))), Set(n, NumericFactor(Rest(u)))),
                          If(And(Less(m, C0), Less(n, C0)), Negate(GCD(Negate(m), Negate(n))),
                              GCD(m, n)))),
                      C1))))),
          ISetDelayed(
              90, NonnumericFactors(u_), If(NumberQ(u),
                  If(EqQ(Im(
                      u), C0), C1, If(EqQ(Re(u), C0), CI,
                          u)),
                  If(PowerQ(u),
                      If(And(RationalQ(Part(u, C1)), FractionQ(Part(u, C2))), Times(u,
                          Power(NumericFactor(u), CN1)), u),
                      If(ProductQ(u), Map($rubi(
                          "NonnumericFactors"), u), If(SumQ(u),
                              If(Less(LeafCount(u), ZZ(50L)),
                                  $(Function(If(SumQ(Slot1), u, NonnumericFactors(Slot1))),
                                      ContentFactor(u)),
                                  With(List(Set(n, NumericFactor(u))),
                                      Map(Function(Times(Slot1, Power(n, CN1))), u))),
                              u))))),
          ISetDelayed(91, RemoveContent(u_, x_Symbol),
              With(List(Set(v, NonfreeFactors(u, x))),
                  With(List(Set(w, Together(v))),
                      If(EqQ(FreeFactors(w, x), C1), RemoveContentAux(v, x),
                          RemoveContentAux(NonfreeFactors(w, x), x))))),
          ISetDelayed(92,
              RemoveContentAux(Plus(Times(Power(a_, m_), u_DEFAULT), Times(b_, v_DEFAULT)),
                  x_Symbol),
              Condition(If(Greater(m, C1),
                  RemoveContentAux(Subtract(Times(Power(a, Subtract(m, C1)), u), v), x),
                  RemoveContentAux(Subtract(u, Times(Power(a, Subtract(C1, m)), v)), x)),
                  And(IntegersQ(a, b), Equal(Plus(a, b), C0), RationalQ(m)))),
          ISetDelayed(93,
              RemoveContentAux(Plus(Times(Power(a_, m_DEFAULT), u_DEFAULT),
                  Times(Power(a_, n_DEFAULT), v_DEFAULT)), x_Symbol),
              Condition(RemoveContentAux(Plus(u, Times(Power(a, Subtract(n, m)), v)), x),
                  And(FreeQ(a, x), RationalQ(m, n), GreaterEqual(Subtract(n, m), C0)))),
          ISetDelayed(94,
              RemoveContentAux(Plus(Times(Power(a_, m_DEFAULT), u_DEFAULT),
                  Times(Power(a_, n_DEFAULT), v_DEFAULT), Times(Power(a_, p_DEFAULT), w_DEFAULT)),
                  x_Symbol),
              Condition(
                  RemoveContentAux(Plus(u, Times(Power(a, Subtract(n, m)), v),
                      Times(Power(a, Subtract(p, m)), w)), x),
                  And(FreeQ(a, x), RationalQ(m, n, p), GreaterEqual(Subtract(n, m), C0),
                      GreaterEqual(Subtract(p, m), C0)))),
          ISetDelayed(95, RemoveContentAux(u_, x_Symbol),
              If(And(SumQ(u), NegQ(First(u))), Negate(u), u)),
          ISetDelayed(96, FreeFactors(u_, x_),
              If(ProductQ(u), Map(Function(If(FreeQ(Slot1, x), Slot1, C1)), u),
                  If(FreeQ(u, x), u, C1))),
          ISetDelayed(97, NonfreeFactors(u_, x_),
              If(ProductQ(u), Map(Function(If(FreeQ(Slot1, x), C1, Slot1)), u),
                  If(FreeQ(u, x), C1, u))),
          ISetDelayed(98, FreeTerms(u_, x_),
              If(SumQ(u), Map(Function(If(FreeQ(Slot1, x), Slot1, C0)), u),
                  If(FreeQ(u, x), u, C0))),
          ISetDelayed(99, NonfreeTerms(u_, x_),
              If(SumQ(u), Map(Function(If(FreeQ(Slot1, x), C0, Slot1)), u),
                  If(FreeQ(u, x), C0, u))),
          ISetDelayed(100, Expon($p("expr"), $p("form")),
              Exponent(Together($s("expr")), $s("form"))),
          ISetDelayed(101, Expon($p("expr"), $p("form"), h_),
              Exponent(Together($s("expr")), $s("form"), h)));
}
