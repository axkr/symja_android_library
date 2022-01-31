package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;

/**
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 * 
 */
class UtilityFunctions20 {
  public static IAST RULES =
      List(
          ISetDelayed(339,
              ExpandIntegrand(
                  Times(
                      Power(u_, m_DEFAULT), Power(Plus(a_DEFAULT,
                          Times(b_DEFAULT, Power(u_, n_DEFAULT)), Times(c_DEFAULT,
                              Power(u_, $p("n2", true)))),
                          p_)),
                  x_Symbol),
              Condition(
                  Module(List(q),
                      ReplaceAll(
                          ExpandIntegrand(Power(Times(Power(C4, p), Power(c, p)), CN1),
                              Times(Power(x, m),
                                  Power(Plus(b, Negate(q), Times(C2, c, Power(x, n))), p), Power(
                                      Plus(b, q, Times(C2, c, Power(x, n))), p)),
                              x),
                          List(Rule(q, Rt(Subtract(Sqr(b), Times(C4, a, c)), C2)), Rule(x, u)))),
                  And(FreeQ(List(a, b, c), x), IntegersQ(m, n, $s("n2")),
                      EqQ($s("n2"), Times(C2, n)), ILtQ(p, C0), Less(C0, m, Times(C2, n)),
                      Not(And(Equal(m, n),
                          Equal(p, CN1))),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0)))),
          ISetDelayed(340,
              ExpandIntegrand(
                  Times(
                      Plus(c_, Times(d_DEFAULT, Power(u_, n_DEFAULT))), Power(Plus(a_,
                          Times(b_DEFAULT, Power(u_, $p("n2", true)))), CN1)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(q,
                          Rt(Times(CN1, a, Power(b, CN1)), C2))),
                      Subtract(
                          Times(
                              CN1, Subtract(c, Times(d, q)), Power(
                                  Times(C2, b, q, Plus(q, Power(u, n))), CN1)),
                          Times(
                              Plus(c, Times(d, q)), Power(Times(C2, b, q, Subtract(q, Power(u, n))),
                                  CN1)))),
                  And(FreeQ(List(a, b, c, d, n), x), EqQ($s("n2"), Times(C2, n))))),
          ISetDelayed(341,
              ExpandIntegrand(
                  Times(
                      Plus(d_DEFAULT,
                          Times(e_DEFAULT,
                              Plus(f_DEFAULT, Times(g_DEFAULT, Power(u_, n_DEFAULT))))),
                      Power(
                          Plus(
                              a_DEFAULT, Times(b_DEFAULT, Power(u_,
                                  n_DEFAULT)),
                              Times(c_DEFAULT, Power(u_, $p("n2", true)))),
                          CN1)),
                  x_Symbol),
              Condition(
                  With(List(Set(q, Rt(Subtract(Sqr(b), Times(C4, a, c)), C2))),
                      With(
                          List(Set(r,
                              TogetherSimplify(Times(
                                  Subtract(Times(C2, c, Plus(d, Times(e, f))), Times(b, e, g)),
                                  Power(q, CN1))))),
                          Plus(
                              Times(Plus(Times(e, g), r),
                                  Power(Plus(b, Negate(q), Times(C2, c, Power(u, n))), CN1)),
                              Times(Subtract(Times(e, g), r),
                                  Power(Plus(b, q, Times(C2, c, Power(u, n))), CN1))))),
                  And(FreeQ(List(a, b, c, d, e, f, g, n), x), EqQ($s("n2"), Times(C2, n)),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0)))),
          ISetDelayed(342, ExpandIntegrand(Times(u_, Power(v_, CN1)), x_Symbol),
              Condition(PolynomialDivide(u, v, x),
                  And(PolynomialQ(u, x), PolynomialQ(v, x),
                      GreaterEqual(Exponent(u, x), Exponent(v, x))))),
          ISetDelayed(
              343, ExpandIntegrand(Times(u_,
                  Power(Times(a_DEFAULT, x_), p_)), x_Symbol),
              Condition(
                  ExpandToSum(Power(Times(a, x),
                      p), u, x),
                  And(Not(IntegerQ(p)), PolynomialQ(u, x)))),
          ISetDelayed(344, ExpandIntegrand(Times(u_DEFAULT, Power(v_, p_)), x_Symbol),
              Condition(ExpandIntegrand(NormalizeIntegrand(Power(v, p), x), u, x),
                  Not(IntegerQ(p)))),
          ISetDelayed(345, ExpandIntegrand(u_, x_Symbol),
              With(List(Set(v, ExpandExpression(u, x))), Condition(v, SumQ(v)))),
          ISetDelayed(346,
              ExpandIntegrand(Times(Power(u_, m_DEFAULT),
                  Power(Plus(a_, Times(b_DEFAULT, Power(u_, n_))), CN1)), x_Symbol),
              Condition(ExpandBinomial(a, b, m, n, u, x),
                  And(FreeQ(List(a, b), x), IntegersQ(m, n), Less(C0, m, n)))),
          ISetDelayed(347, ExpandIntegrand(u_, x_Symbol), u),
          ISetDelayed(348, ExpandExpression(u_, x_Symbol), Module(List(v, w), CompoundExpression(
              Set(v,
                  If(And(AlgebraicFunctionQ(u, x), Not(RationalFunctionQ(u, x))),
                      ExpandAlgebraicFunction(u, x), C0)),
              If(SumQ(v), ExpandCleanup(v, x), CompoundExpression(Set(v, SmartApart(u, x)), If(
                  SumQ(v), ExpandCleanup(v, x),
                  CompoundExpression(Set(v, SmartApart(RationalFunctionFactors(u, x), x, x)), If(
                      SumQ(v),
                      CompoundExpression(Set(w, NonrationalFunctionFactors(u, x)),
                          ExpandCleanup(Map(Function(Times(Slot1, w)), v), x)),
                      CompoundExpression(Set(v, Expand(u, x)),
                          If(SumQ(v), ExpandCleanup(v, x),
                              CompoundExpression(Set(v, Expand(u)),
                                  If(SumQ(v), ExpandCleanup(v, x), SimplifyTerm(u, x))))))))))))),
          ISetDelayed(349,
              ExpandCleanup(
                  Plus(u_, Times(v_, Power(Plus(a_, Times(b_DEFAULT, x_)), CN1)),
                      Times(w_, Power(Plus(c_, Times(d_DEFAULT, x_)), CN1))),
                  x_Symbol),
              Condition(
                  ExpandCleanup(
                      Plus(u,
                          Times(
                              Plus(Times(c, v), Times(a,
                                  w)),
                              Power(Plus(Times(a, c), Times(b, d, Sqr(x))), CN1))),
                      x),
                  And(FreeQ(List(a, b, c, d), x), EqQ(Plus(Times(b, c), Times(a, d)), C0),
                      EqQ(Plus(Times(d, v), Times(b, w)), C0)))),
          ISetDelayed(350, ExpandCleanup(u_, x_Symbol),
              Module(
                  List(Set(v,
                      CollectReciprocals(u, x))),
                  If(SumQ(v),
                      CompoundExpression(
                          Set(v, Map(Function(SimplifyTerm(Slot1, x)), v)), If(SumQ(v),
                              UnifySum(v, x), v)),
                      v))),
          ISetDelayed(351,
              CollectReciprocals(
                  Plus(
                      u_, Times(e_, Power(Plus(a_, Times(b_DEFAULT, x_)),
                          CN1)),
                      Times(f_, Power(Plus(c_, Times(d_DEFAULT, x_)), CN1))),
                  x_Symbol),
              Condition(
                  CollectReciprocals(
                      Plus(u,
                          Times(Plus(Times(c, e), Times(a, f)),
                              Power(Plus(Times(a, c), Times(b, d, Sqr(x))), CN1))),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f), x), EqQ(Plus(Times(b, c), Times(a, d)), C0),
                      EqQ(Plus(Times(d, e), Times(b, f)), C0)))),
          ISetDelayed(352,
              CollectReciprocals(Plus(u_, Times(e_, Power(Plus(a_, Times(b_DEFAULT, x_)), CN1)),
                  Times(f_, Power(Plus(c_, Times(d_DEFAULT, x_)), CN1))), x_Symbol),
              Condition(
                  CollectReciprocals(Plus(u,
                      Times(Plus(Times(d, e), Times(b, f)), x,
                          Power(Plus(Times(a, c), Times(b, d, Sqr(x))), CN1))),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f), x), EqQ(Plus(Times(b, c), Times(a, d)), C0),
                      EqQ(Plus(Times(c, e), Times(a, f)), C0)))),
          ISetDelayed(353, CollectReciprocals(u_, x_Symbol), u),
          ISetDelayed(
              354, ExpandBinomial(a_, b_, m_, n_, u_, x_Symbol), If(
                  And(OddQ(Times(n, Power(GCD(m, n), CN1))), PosQ(
                      Times(a, Power(b, CN1)))),
                  With(
                      List(
                          Set(g, GCD(m,
                              n)),
                          Set(r,
                              Numerator(Rt(Times(a, Power(b, CN1)),
                                  Times(n, Power(GCD(m, n), CN1))))),
                          Set(s, Denominator(Rt(Times(a, Power(b, CN1)),
                              Times(n, Power(GCD(m, n), CN1)))))),
                      Module(
                          List(k), If(
                              CoprimeQ(Plus(m,
                                  g), n),
                              Sum(Times(r,
                                  Power(Times(CN1, r, Power(s, CN1)), Times(m, Power(g, CN1))),
                                  Power(CN1, Times(CN2, k, m, Power(n, CN1))),
                                  Power(
                                      Times(a, n,
                                          Plus(r,
                                              Times(Power(CN1, Times(C2, k, g, Power(n, CN1))), s,
                                                  Power(u, g)))),
                                      CN1)),
                                  List(k, C1, Times(n, Power(g, CN1)))),
                              Sum(Times(r,
                                  Power(Times(CN1, r, Power(s, CN1)), Times(m, Power(g, CN1))),
                                  Power(CN1, Times(C2, k, Plus(m, g), Power(n, CN1))),
                                  Power(Times(a, n,
                                      Plus(Times(Power(CN1, Times(C2, k, g, Power(n, CN1))), r),
                                          Times(s, Power(u, g)))),
                                      CN1)),
                                  List(k, C1, Times(n, Power(g, CN1))))))),
                  With(
                      List(
                          Set(g, GCD(m,
                              n)),
                          Set(r,
                              Numerator(
                                  Rt(Times(CN1, a, Power(b, CN1)),
                                      Times(n, Power(GCD(m, n), CN1))))),
                          Set(s,
                              Denominator(
                                  Rt(Times(CN1, a, Power(b, CN1)),
                                      Times(n, Power(GCD(m, n), CN1)))))),
                      If(Equal(Times(n, Power(g, CN1)), C2),
                          Subtract(
                              Times(s, Power(Times(C2, b, Plus(r, Times(s, Power(u, g)))),
                                  CN1)),
                              Times(s, Power(Times(C2, b, Subtract(r, Times(s, Power(u, g)))),
                                  CN1))),
                          Module(
                              List(k), If(
                                  CoprimeQ(Plus(m,
                                      g), n),
                                  Sum(Times(r,
                                      Power(Times(r, Power(s, CN1)), Times(m, Power(g, CN1))),
                                      Power(CN1, Times(CN2, k, m, Power(n, CN1))),
                                      Power(Times(a, n,
                                          Subtract(r,
                                              Times(Power(CN1, Times(C2, k, g, Power(n, CN1))), s,
                                                  Power(u, g)))),
                                          CN1)),
                                      List(k, C1, Times(n, Power(g, CN1)))),
                                  Sum(Times(r,
                                      Power(Times(r, Power(s, CN1)), Times(m, Power(g, CN1))),
                                      Power(CN1, Times(C2, k, Plus(m, g), Power(n, CN1))),
                                      Power(Times(a, n,
                                          Subtract(
                                              Times(Power(CN1, Times(C2, k, g, Power(n, CN1))), r),
                                              Times(s, Power(u, g)))),
                                          CN1)),
                                      List(k, C1, Times(n, Power(g, CN1)))))))))),
          ISetDelayed(355, SmartApart(u_, x_Symbol),
              With(List(Set($s("alst"), MakeAssocList(u, x))),
                  With(
                      List(Set($s("tmp"),
                          KernelSubst(Apart(GensymSubst(u, x, $s("alst"))), x, $s("alst")))),
                      If(SameQ($s("tmp"), Indeterminate), u, $s("tmp"))))),
          ISetDelayed(356, SmartApart(u_, v_, x_Symbol),
              With(List(Set($s("alst"), MakeAssocList(u, x))),
                  With(
                      List(Set($s("tmp"),
                          KernelSubst(Apart(GensymSubst(u, x, $s("alst")), v), x, $s("alst")))),
                      If(SameQ($s("tmp"), Indeterminate), u, $s("tmp"))))),
          ISetDelayed(
              357, MakeAssocList(u_, x_Symbol, Optional($p("alst", List),
                  List())),
              If(AtomQ(u), $s("alst"),
                  If(IntegerPowerQ(u), MakeAssocList(Part(u, C1), x, $s("alst")),
                      If(Or(ProductQ(u), SumQ(u)),
                          MakeAssocList(Rest(u), x, MakeAssocList(First(u), x,
                              $s("alst"))),
                          If(FreeQ(u, x), With(
                              List(Set($s("tmp"),
                                  Select($s("alst"), Function(SameQ(Part(Slot1, C2), u)), C1))),
                              If(SameQ($s("tmp"), List()),
                                  Append($s("alst"), List(Unique($str("Rubi")), u)), $s("alst"))),
                              $s("alst")))))),
          ISetDelayed(
              358, GensymSubst(u_, x_Symbol, $p("alst",
                  List)),
              If(AtomQ(u), u,
                  If(IntegerPowerQ(u), Power(GensymSubst(Part(u, C1), x, $s("alst")), Part(u, C2)),
                      If(Or(ProductQ(u), SumQ(u)),
                          Map(Function(
                              GensymSubst(Slot1, x, $s("alst"))), u),
                          If(FreeQ(u, x),
                              With(
                                  List(Set($s("tmp"),
                                      Select($s("alst"), Function(SameQ(Part(Slot1, C2), u)), C1))),
                                  If(SameQ($s("tmp"), List()), u, Part($s("tmp"), C1, C1))),
                              u))))));
}
