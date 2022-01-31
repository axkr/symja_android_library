package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$;
import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$rubi;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.C5;
import static org.matheclipse.core.expression.F.CI;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Denominator;
import static org.matheclipse.core.expression.F.Equal;
import static org.matheclipse.core.expression.F.Exponent;
import static org.matheclipse.core.expression.F.First;
import static org.matheclipse.core.expression.F.FractionalPart;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.Function;
import static org.matheclipse.core.expression.F.GCD;
import static org.matheclipse.core.expression.F.Greater;
import static org.matheclipse.core.expression.F.GreaterEqual;
import static org.matheclipse.core.expression.F.ISetDelayed;
import static org.matheclipse.core.expression.F.If;
import static org.matheclipse.core.expression.F.Im;
import static org.matheclipse.core.expression.F.IntegerPart;
import static org.matheclipse.core.expression.F.LeafCount;
import static org.matheclipse.core.expression.F.Less;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.ListQ;
import static org.matheclipse.core.expression.F.Map;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.NumberQ;
import static org.matheclipse.core.expression.F.Optional;
import static org.matheclipse.core.expression.F.Part;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Re;
import static org.matheclipse.core.expression.F.Rest;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Slot1;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.Together;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.ZZ;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.b_;
import static org.matheclipse.core.expression.F.h_;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.u_DEFAULT;
import static org.matheclipse.core.expression.F.v_DEFAULT;
import static org.matheclipse.core.expression.F.w_DEFAULT;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.False;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.h;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.w;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ContentFactor;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Expon;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FracPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FractionQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FreeFactors;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FreeTerms;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GeneralizedTrinomialParts;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntegersQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NegQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NonfreeFactors;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NonfreeTerms;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NonnumericFactors;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NumericFactor;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PowerQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ProductQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RationalQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RemoveContent;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RemoveContentAux;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SumQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.TrinomialParts;
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
