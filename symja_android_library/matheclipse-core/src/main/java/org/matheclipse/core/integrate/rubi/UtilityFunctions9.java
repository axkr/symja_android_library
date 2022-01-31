package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.AtomQ;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.C5;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Catch;
import static org.matheclipse.core.expression.F.Coefficient;
import static org.matheclipse.core.expression.F.CompoundExpression;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Drop;
import static org.matheclipse.core.expression.F.Expand;
import static org.matheclipse.core.expression.F.FactorSquareFreeList;
import static org.matheclipse.core.expression.F.First;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.Function;
import static org.matheclipse.core.expression.F.GCD;
import static org.matheclipse.core.expression.F.Greater;
import static org.matheclipse.core.expression.F.ISetDelayed;
import static org.matheclipse.core.expression.F.If;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.ListQ;
import static org.matheclipse.core.expression.F.Map;
import static org.matheclipse.core.expression.F.MatchQ;
import static org.matheclipse.core.expression.F.Module;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Part;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.PolynomialQ;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Rest;
import static org.matheclipse.core.expression.F.SameQ;
import static org.matheclipse.core.expression.F.Scan;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Simplify;
import static org.matheclipse.core.expression.F.Slot1;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Throw;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.j_DEFAULT;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.q_DEFAULT;
import static org.matheclipse.core.expression.F.r_DEFAULT;
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
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.j;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.q;
import static org.matheclipse.core.expression.S.r;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.BinomialMatchQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.CubicMatchQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Expon;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GeneralizedBinomialMatchQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GeneralizedTrinomialMatchQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntegerPowerQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LinearMatchQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.MinimumMonomialExponent;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.MonomialExponent;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NonpolynomialTerms;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NormalizePseudoBinomial;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PerfectPowerTest;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PolynomialTermQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PolynomialTerms;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PosQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PowerOfLinearMatchQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ProductQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PseudoBinomialPairQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PseudoBinomialParts;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PseudoBinomialQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.QuadraticMatchQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.QuotientOfLinearsMatchQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RationalFunctionQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Rt;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SumQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.TrinomialMatchQ;
import org.matheclipse.core.interfaces.IAST;

/**
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class UtilityFunctions9 {
  public static IAST RULES =
      List(
          ISetDelayed(122, MinimumMonomialExponent(u_, x_Symbol),
              Module(List(Set(n, MonomialExponent(First(u), x))),
                  CompoundExpression(
                      Scan(Function(If(PosQ(Subtract(n, MonomialExponent(Slot1, x))),
                          Set(n, MonomialExponent(Slot1, x)))), u),
                      n))),
          ISetDelayed(
              123, MonomialExponent(Times(a_DEFAULT,
                  Power(x_, n_DEFAULT)), x_Symbol),
              Condition(n, FreeQ(List(a, n), x))),
          ISetDelayed(124, LinearMatchQ(u_, x_Symbol),
              If(ListQ(u),
                  Catch(
                      CompoundExpression(Scan(
                          Function(If(Not(LinearMatchQ(Slot1, x)), Throw(False))), u),
                          True)),
                  MatchQ(u, Condition(Plus(a_DEFAULT, Times(b_DEFAULT, x)),
                      FreeQ(List(a, b), x))))),
          ISetDelayed(125, PowerOfLinearMatchQ(u_, x_Symbol),
              If(ListQ(u),
                  Catch(CompoundExpression(
                      Scan(Function(If(Not(PowerOfLinearMatchQ(Slot1, x)), Throw(False))), u),
                      True)),
                  MatchQ(u,
                      Condition(Power(Plus(a_DEFAULT, Times(b_DEFAULT, x)), m_DEFAULT),
                          FreeQ(List(a, b, m), x))))),
          ISetDelayed(126, QuadraticMatchQ(u_, x_Symbol),
              If(ListQ(u),
                  Catch(CompoundExpression(
                      Scan(Function(If(Not(QuadraticMatchQ(Slot1, x)), Throw(False))), u), True)),
                  Or(MatchQ(u,
                      Condition(Plus(a_DEFAULT, Times(b_DEFAULT, x), Times(c_DEFAULT, Sqr(x))),
                          FreeQ(List(a, b, c), x))),
                      MatchQ(u,
                          Condition(Plus(a_DEFAULT, Times(c_DEFAULT, Sqr(x))),
                              FreeQ(List(a, c), x)))))),
          ISetDelayed(127, CubicMatchQ(u_, x_Symbol),
              If(ListQ(u), Catch(CompoundExpression(
                  Scan(Function(If(Not(CubicMatchQ(Slot1, x)), Throw(False))), u), True)), Or(
                      MatchQ(u,
                          Condition(Plus(a_DEFAULT, Times(b_DEFAULT, x), Times(c_DEFAULT, Sqr(x)),
                              Times(d_DEFAULT, Power(x, C3))), FreeQ(List(a, b, c, d), x))),
                      MatchQ(u,
                          Condition(
                              Plus(a_DEFAULT, Times(b_DEFAULT, x), Times(d_DEFAULT, Power(x, C3))),
                              FreeQ(List(a, b, d), x))),
                      MatchQ(u,
                          Condition(Plus(a_DEFAULT, Times(c_DEFAULT, Sqr(x)),
                              Times(d_DEFAULT, Power(x, C3))), FreeQ(List(a, c, d), x))),
                      MatchQ(u,
                          Condition(Plus(a_DEFAULT, Times(d_DEFAULT, Power(x, C3))),
                              FreeQ(List(a, d), x)))))),
          ISetDelayed(128, BinomialMatchQ(u_, x_Symbol),
              If(ListQ(u),
                  Catch(
                      CompoundExpression(
                          Scan(Function(If(Not(BinomialMatchQ(Slot1, x)), Throw(False))),
                              u),
                          True)),
                  MatchQ(u,
                      Condition(
                          Plus(a_DEFAULT, Times(b_DEFAULT, Power(x, n_DEFAULT))), FreeQ(
                              List(a, b, n), x))))),
          ISetDelayed(129, GeneralizedBinomialMatchQ(u_, x_Symbol),
              If(ListQ(u),
                  Catch(
                      CompoundExpression(
                          Scan(Function(If(Not(GeneralizedBinomialMatchQ(Slot1, x)), Throw(False))),
                              u),
                          True)),
                  MatchQ(u,
                      Condition(
                          Plus(Times(a_DEFAULT,
                              Power(x, q_DEFAULT)), Times(b_DEFAULT, Power(x, n_DEFAULT))),
                          FreeQ(List(a, b, n, q), x))))),
          ISetDelayed(130, TrinomialMatchQ(u_, x_Symbol), If(ListQ(u),
              Catch(CompoundExpression(
                  Scan(Function(If(Not(TrinomialMatchQ(Slot1, x)), Throw(False))), u), True)),
              MatchQ(u,
                  Condition(Plus(a_DEFAULT, Times(b_DEFAULT, Power(x, n_DEFAULT)), Times(c_DEFAULT,
                      Power(x, j_DEFAULT))), And(FreeQ(List(a, b, c, n), x),
                          EqQ(Subtract(j, Times(C2, n)), C0)))))),
          ISetDelayed(131, GeneralizedTrinomialMatchQ(u_, x_Symbol),
              If(ListQ(u),
                  Catch(
                      CompoundExpression(
                          Scan(
                              Function(If(Not(GeneralizedTrinomialMatchQ(Slot1, x)), Throw(False))),
                              u),
                          True)),
                  MatchQ(u,
                      Condition(
                          Plus(Times(a_DEFAULT, Power(x, q_DEFAULT)),
                              Times(b_DEFAULT, Power(x,
                                  n_DEFAULT)),
                              Times(c_DEFAULT, Power(x, r_DEFAULT))),
                          And(FreeQ(List(a, b, c, n, q), x),
                              EqQ(Subtract(r, Subtract(Times(C2, n), q)), C0)))))),
          ISetDelayed(132, QuotientOfLinearsMatchQ(u_, x_Symbol),
              If(ListQ(u),
                  Catch(CompoundExpression(
                      Scan(Function(If(Not(QuotientOfLinearsMatchQ(Slot1, x)), Throw(False))), u),
                      True)),
                  MatchQ(u,
                      Condition(
                          Times(e_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x)),
                              Power(Plus(c_DEFAULT, Times(d_DEFAULT, x)), CN1)),
                          FreeQ(List(a, b, c, d, e), x))))),
          ISetDelayed(133, PolynomialTermQ(u_, x_Symbol),
              Or(FreeQ(u, x),
                  MatchQ(u,
                      Condition(Times(a_DEFAULT, Power(x, n_DEFAULT)),
                          And(FreeQ(a, x), IntegerQ(n), Greater(n, C0)))))),
          ISetDelayed(134, PolynomialTerms(u_, x_Symbol),
              Map(Function(If(PolynomialTermQ(Slot1, x), Slot1, C0)), u)),
          ISetDelayed(135, NonpolynomialTerms(u_, x_Symbol),
              Map(Function(If(PolynomialTermQ(Slot1, x), C0, Slot1)), u)),
          ISetDelayed(136, PseudoBinomialQ(u_, x_Symbol), ListQ(PseudoBinomialParts(u, x))),
          ISetDelayed(137, PseudoBinomialPairQ(u_, v_, x_Symbol),
              With(List(Set($s("lst1"), PseudoBinomialParts(u, x))),
                  If(AtomQ($s("lst1")), False,
                      With(List(Set($s("lst2"), PseudoBinomialParts(v, x))),
                          If(AtomQ($s("lst2")), False,
                              SameQ(Drop($s("lst1"), C2), Drop($s("lst2"), C2))))))),
          ISetDelayed(138, NormalizePseudoBinomial(u_, x_Symbol),
              With(
                  List(Set($s("lst"),
                      PseudoBinomialParts(u, x))),
                  Plus(
                      Part($s(
                          "lst"), C1),
                      Times(
                          Part($s(
                              "lst"), C2),
                          Power(
                              Plus(Part($s("lst"), C3), Times(Part($s("lst"), C4),
                                  x)),
                              Part($s("lst"), C5)))))),
          ISetDelayed(
              139, PseudoBinomialParts(u_, x_Symbol), If(
                  And(PolynomialQ(u, x), Greater(Expon(u, x),
                      C2)),
                  Module(
                      List(a, c, d, n), CompoundExpression(Set(n, Expon(u, x)),
                          Set(d, Rt(Coefficient(u, x, n),
                              n)),
                          Set(c,
                              Times(
                                  Coefficient(u, x, Subtract(n, C1)), Power(
                                      Times(n, Power(d, Subtract(n, C1))), CN1))),
                          Set(a, Simplify(Subtract(u, Power(Plus(c, Times(d, x)), n)))), If(And(
                              NeQ(a, C0), FreeQ(a, x)), List(a, C1, c, d, n),
                              False))),
                  False)),
          ISetDelayed(140, PerfectPowerTest(u_, x_Symbol),
              If(PolynomialQ(u, x),
                  Module(
                      List(Set($s("lst"), FactorSquareFreeList(u)), Set($s(
                          "§gcd"), C0), Set(v,
                              C1)),
                      CompoundExpression(
                          If(SameQ(Part($s(
                              "lst"), C1), List(C1,
                                  C1)),
                              Set($s("lst"), Rest($s("lst")))),
                          Scan(
                              Function(Set($s("§gcd"), GCD($s("§gcd"), Part(Slot1, C2)))), $s(
                                  "lst")),
                          If(Greater($s("§gcd"), C1),
                              CompoundExpression(
                                  Scan(
                                      Function(Set(v, Times(v,
                                          Power(Part(Slot1, C1),
                                              Times(Part(Slot1, C2), Power($s("§gcd"), CN1)))))),
                                      $s("lst")),
                                  Power(Expand(v), $s("§gcd"))),
                              False))),
                  False)),
          ISetDelayed(141, RationalFunctionQ(u_, x_Symbol), If(Or(AtomQ(u), FreeQ(u, x)), True,
              If(IntegerPowerQ(u), RationalFunctionQ(Part(u, C1), x), If(Or(ProductQ(u), SumQ(u)),
                  Catch(CompoundExpression(
                      Scan(Function(If(Not(RationalFunctionQ(Slot1, x)), Throw(False))), u), True)),
                  False)))));
}
