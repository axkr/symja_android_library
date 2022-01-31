package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$;
import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.$str;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.Apply;
import static org.matheclipse.core.expression.F.AtomQ;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Catch;
import static org.matheclipse.core.expression.F.Coefficient;
import static org.matheclipse.core.expression.F.CompoundExpression;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Equal;
import static org.matheclipse.core.expression.F.Exponent;
import static org.matheclipse.core.expression.F.First;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.Function;
import static org.matheclipse.core.expression.F.Greater;
import static org.matheclipse.core.expression.F.ISetDelayed;
import static org.matheclipse.core.expression.F.If;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.ListQ;
import static org.matheclipse.core.expression.F.Map;
import static org.matheclipse.core.expression.F.MatchQ;
import static org.matheclipse.core.expression.F.Max;
import static org.matheclipse.core.expression.F.Module;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Optional;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Part;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.PolynomialGCD;
import static org.matheclipse.core.expression.F.PolynomialQ;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Print;
import static org.matheclipse.core.expression.F.Rest;
import static org.matheclipse.core.expression.F.Reverse;
import static org.matheclipse.core.expression.F.SameQ;
import static org.matheclipse.core.expression.F.Scan;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Slot;
import static org.matheclipse.core.expression.F.Slot1;
import static org.matheclipse.core.expression.F.Slot2;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Throw;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.Together;
import static org.matheclipse.core.expression.F.UnsameQ;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.v_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.False;
import static org.matheclipse.core.expression.S.True;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.w;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.AlgebraicFunctionQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FractionQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntegerPowerQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntegersQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LinearQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NonfreeFactors;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NonrationalFunctionFactors;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PolyGCD;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PowerQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ProductQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.QuotientOfLinearsP;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.QuotientOfLinearsParts;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.QuotientOfLinearsQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RationalFunctionExpand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RationalFunctionExponents;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RationalFunctionFactors;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RationalFunctionQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RationalQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SumQ;
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
