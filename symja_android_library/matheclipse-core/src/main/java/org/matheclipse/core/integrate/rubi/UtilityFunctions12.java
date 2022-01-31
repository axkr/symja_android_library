package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$;
import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$rubi;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.AtomQ;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D3;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Cancel;
import static org.matheclipse.core.expression.F.CompoundExpression;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Equal;
import static org.matheclipse.core.expression.F.Expand;
import static org.matheclipse.core.expression.F.Exponent;
import static org.matheclipse.core.expression.F.Factor;
import static org.matheclipse.core.expression.F.FactorSquareFree;
import static org.matheclipse.core.expression.F.First;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.Function;
import static org.matheclipse.core.expression.F.Greater;
import static org.matheclipse.core.expression.F.ISetDelayed;
import static org.matheclipse.core.expression.F.If;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.LeafCount;
import static org.matheclipse.core.expression.F.Less;
import static org.matheclipse.core.expression.F.LessEqual;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Map;
import static org.matheclipse.core.expression.F.MatchQ;
import static org.matheclipse.core.expression.F.Module;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.OddQ;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Part;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.PolynomialQ;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.QQ;
import static org.matheclipse.core.expression.F.SameQ;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Simplify;
import static org.matheclipse.core.expression.F.Slot1;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.TimeConstrained;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.Together;
import static org.matheclipse.core.expression.F.UnsameQ;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.u_DEFAULT;
import static org.matheclipse.core.expression.F.v_;
import static org.matheclipse.core.expression.F.w_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.Plus;
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
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.AbsorbMinusSign;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.BinomialMatchQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.BinomialQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EveryQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandToSum;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FactorNumericGcd;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FalseQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FixSimplify;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FractionalPowerOfSquareQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FractionalPowerSubexpressionQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntegerPowerQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LinearQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.MergeMonomials;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.MinimumMonomialExponent;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.MonomialQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NormalizeIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NormalizeIntegrandAux;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NormalizeIntegrandFactor;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NormalizeIntegrandFactorBase;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NormalizeLeadTermSigns;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NormalizePowerOfLinear;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NormalizeSumFactors;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NormalizeTogether;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NumericFactor;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PowerQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ProductQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RationalQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SignOfFactor;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SimplifyIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SimplifyTerm;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SmartSimplify;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.StopFunctionQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SubstForExpn;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SumQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.TogetherSimplify;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.TrinomialMatchQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.TrinomialQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.UnifySum;
import org.matheclipse.core.interfaces.IAST;

/**
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class UtilityFunctions12 {
  public static IAST RULES =
      List(
          ISetDelayed(182, NormalizeIntegrandFactor(u_, x_Symbol),
              Module(
                  List($s("bas"), $s("deg"), $s(
                      "§min")),
                  If(And(PowerQ(u), FreeQ(Part(u, C2), x)),
                      CompoundExpression(
                          Set($s("bas"), NormalizeIntegrandFactorBase(Part(u,
                              C1), x)),
                          Set($s("deg"), Part(u, C2)),
                          If(And(IntegerQ($s("deg")), SumQ($s("bas")),
                              EveryQ(Function(MonomialQ(Slot1, x)), $s("bas"))),
                              CompoundExpression(
                                  Set($s("§min"), MinimumMonomialExponent($s("bas"), x)),
                                  Times(Power(x, Times($s("§min"), $s("deg"))),
                                      Power(Map(
                                          Function(Simplify(
                                              Times(Slot1, Power(Power(x, $s("§min")), CN1)))),
                                          $s("bas")), $s("deg")))),
                              Power($s("bas"), $s("deg")))),
                      If(And(PowerQ(u), FreeQ(Part(u, C1), x)),
                          Power(Part(u, C1), NormalizeIntegrandFactorBase(Part(u, C2), x)),
                          CompoundExpression(Set($s("bas"), NormalizeIntegrandFactorBase(u, x)), If(
                              And(SumQ($s("bas")),
                                  EveryQ(Function(MonomialQ(Slot1, x)), $s("bas"))),
                              CompoundExpression(
                                  Set($s("§min"), MinimumMonomialExponent($s("bas"), x)),
                                  Times(Power(x, $s("§min")),
                                      Map(Function(Times(Slot1, Power(Power(x, $s("§min")), CN1))),
                                          $s("bas")))),
                              $s("bas"))))))),
          ISetDelayed(183, NormalizeIntegrandFactorBase(Times(u_, Power(x_, m_DEFAULT)), x_Symbol),
              Condition(
                  NormalizeIntegrandFactorBase(Map(Function(Times(Power(x, m), Slot1)), u), x),
                  And(FreeQ(m, x), SumQ(u)))),
          ISetDelayed(184, NormalizeIntegrandFactorBase(u_, x_Symbol), If(BinomialQ(u, x),
              If(BinomialMatchQ(u, x), u, ExpandToSum(u, x)),
              If(TrinomialQ(u, x), If(TrinomialMatchQ(u, x), u, ExpandToSum(u, x)),
                  If(ProductQ(u), Map(Function(NormalizeIntegrandFactor(Slot1, x)), u),
                      If(And(PolynomialQ(u, x), LessEqual(Exponent(u, x), C4)), ExpandToSum(u, x),
                          If(SumQ(u),
                              With(List(Set(v, TogetherSimplify(u))), If(
                                  Or(SumQ(v),
                                      MatchQ(v,
                                          Condition(Times(Power(x, m_DEFAULT), w_),
                                              And(FreeQ(m, x), SumQ(w)))),
                                      Greater(LeafCount(v), Plus(LeafCount(u), C2))),
                                  UnifySum(u, x), NormalizeIntegrandFactorBase(v, x))),
                              Map(Function(NormalizeIntegrandFactor(Slot1, x)), u))))))),
          ISetDelayed(185, NormalizeTogether(u_), NormalizeLeadTermSigns(Together(u))),
          ISetDelayed(186, NormalizeLeadTermSigns(u_), With(
              List(Set($s("lst"), If(ProductQ(u), Map($rubi("SignOfFactor"), u), SignOfFactor(u)))),
              If(Equal(Part($s("lst"), C1), C1), Part($s("lst"), C2),
                  AbsorbMinusSign(Part($s("lst"), C2))))),
          ISetDelayed(187, AbsorbMinusSign(Times(u_DEFAULT,
              $p(v, Plus))), Times(u, CN1,
                  v)),
          ISetDelayed(188, AbsorbMinusSign(Times(u_DEFAULT, Power($p(v, Plus), m_))),
              Condition(Times(u, Power(Negate(v), m)), OddQ(m))),
          ISetDelayed(189, AbsorbMinusSign(u_), Negate(u)),
          ISetDelayed(190, NormalizeSumFactors(u_),
              If(Or(AtomQ(u), StopFunctionQ(u)), u,
                  If(ProductQ(u),
                      $(Function(Times(Part(Slot1, C1), Part(Slot1, C2))),
                          SignOfFactor(Map($rubi("NormalizeSumFactors"), u))),
                      Map($rubi("NormalizeSumFactors"), u)))),
          ISetDelayed(191, SignOfFactor(u_),
              If(Or(
                  And(RationalQ(u), Less(u, C0)), And(SumQ(u), Less(NumericFactor(First(u)), C0))),
                  List(CN1, Negate(u)),
                  If(And(IntegerPowerQ(u), SumQ(Part(u, C1)),
                      Less(NumericFactor(First(Part(u, C1))), C0)),
                      List(Power(CN1, Part(u, C2)), Power(Negate(Part(u, C1)), Part(u, C2))),
                      If(ProductQ(u), Map($rubi("SignOfFactor"), u), List(C1, u))))),
          ISetDelayed(192, NormalizePowerOfLinear(u_, x_Symbol),
              With(List(Set(v, FactorSquareFree(u))),
                  If(And(PowerQ(v), LinearQ(Part(v, C1), x), FreeQ(Part(v, C2), x)), Power(
                      ExpandToSum(Part(v, C1), x), Part(v, C2)), ExpandToSum(v, x)))),
          ISetDelayed(193,
              MergeMonomials(
                  Times(
                      u_DEFAULT, Power(Plus(a_DEFAULT, Times(b_DEFAULT,
                          x_)), m_DEFAULT),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Times(
                      u, Power(b, m), Power(Power(d,
                          m), CN1),
                      Power(Plus(c, Times(d, x)), Plus(m, n))),
                  And(FreeQ(List(a, b, c, d), x), IntegerQ(m),
                      EqQ(Subtract(Times(b, c), Times(a, d)), C0)))),
          ISetDelayed(194,
              MergeMonomials(Times(u_DEFAULT,
                  Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), m_DEFAULT),
                  Power(Times(c_DEFAULT, Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), n_DEFAULT)),
                      p_)),
                  x_Symbol),
              Condition(
                  Times(
                      u,
                      Power(Times(c, Power(Plus(a, Times(b, x)), n)),
                          Plus(Times(m, Power(n, CN1)), p)),
                      Power(Power(c, Times(m, Power(n, CN1))), CN1)),
                  And(FreeQ(List(a, b, c, m, n, p), x), IntegerQ(Times(m, Power(n, CN1)))))),
          ISetDelayed(195, MergeMonomials(Times(a_DEFAULT, Power(u_, m_)), x_Symbol),
              Condition(Times(a, Power(u, Simplify(m))), FreeQ(List(a, m), x))),
          ISetDelayed(196, MergeMonomials(u_, x_Symbol), If(LinearQ(u, x), Cancel(u), u)),
          ISetDelayed(197, SimplifyIntegrand(u_, x_Symbol), Module(List(v), CompoundExpression(
              Set(v, NormalizeLeadTermSigns(NormalizeIntegrandAux(Simplify(u), x))),
              If(Less(LeafCount(v), Times(QQ(4L, 5L), LeafCount(u))), v,
                  If(UnsameQ(v, NormalizeLeadTermSigns(u)), v, u))))),
          ISetDelayed(198, SimplifyTerm(u_, x_Symbol),
              Module(List(Set(v, Simplify(u)), w),
                  CompoundExpression(
                      Set(w, Together(v)), NormalizeIntegrand(
                          If(Less(LeafCount(v), LeafCount(w)), w, w), x)))),
          ISetDelayed(199, TogetherSimplify(u_),
              TimeConstrained(
                  With(
                      List(Set(v,
                          Together(Simplify(Together(u))))),
                      TimeConstrained(FixSimplify(v), Times(C1D3, $s("§$timelimit")), v)),
                  $s("§$timelimit"), u)),
          ISetDelayed(200, SmartSimplify(u_),
              TimeConstrained(
                  Module(List(v, w),
                      CompoundExpression(Set(v, Simplify(u)), Set(w, Factor(v)),
                          Set(v, If(Less(LeafCount(w), LeafCount(v)), w, v)),
                          Set(v,
                              If(And(Not(FalseQ(Set(w, FractionalPowerOfSquareQ(v)))),
                                  FractionalPowerSubexpressionQ(u, w, Expand(w))),
                                  SubstForExpn(v, w, Expand(w)), v)),
                          Set(v, FactorNumericGcd(v)),
                          TimeConstrained(FixSimplify(v), Times(C1D3, $s("§$timelimit")), v))),
                  $s("§$timelimit"), u)),
          ISetDelayed(201, SubstForExpn(u_, v_, w_),
              If(SameQ(u, v), w, If(AtomQ(u), u, Map(Function(SubstForExpn(Slot1, v, w)), u)))));
}
