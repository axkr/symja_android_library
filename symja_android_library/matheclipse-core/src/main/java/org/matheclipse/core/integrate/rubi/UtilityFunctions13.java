package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;

/**
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 * 
 */
class UtilityFunctions13 {
  public static IAST RULES =
      List(
          ISetDelayed(202, Simp(u_, x_),
              TimeConstrained(NormalizeSumFactors(SimpHelp(u, x)), $s("§$timelimit"), u)),
          ISetDelayed(203, SimpHelp(Exp(Times(u_DEFAULT, Plus(Times(Log(a_), v_DEFAULT), w_))),
              x_), Times(Power(a, Times(u, v)), SimpHelp(Exp(Times(u, w)), x))),
          ISetDelayed(
              204, SimpHelp(u_, x_), If(
                  Or(AtomQ(u), StopFunctionQ(
                      u)),
                  u,
                  If(FreeQ(u, x),
                      With(
                          List(Set(v, SmartSimplify(u))), If(LessEqual(LeafCount(v), LeafCount(u)),
                              v, u)),
                      If(ProductQ(u),
                          If(And(EqQ(First(u), C1D2), MatchQ(Rest(u),
                              Condition(Plus(a_DEFAULT, Times(n_, Pi), Times(b_DEFAULT, v_)),
                                  And(FreeQ(List(a, b), x), Not(FreeQ(v, x)), EqQ(Sqr(n), C1D4))))),
                              If(MatchQ(Rest(u),
                                  Condition(
                                      Plus(Times(n_, Pi), Times(b_DEFAULT, v_)),
                                      And(FreeQ(b, x), Not(FreeQ(v, x)), EqQ(Sqr(n), C1D4)))),
                                  Map(Function(Times(C1D2, Slot1)), Rest(u)), If(
                                      MatchQ(Rest(u),
                                          Condition(
                                              Plus(Times(m_, a_DEFAULT), Times(n_, Pi),
                                                  Times(p_, b_DEFAULT, v_)),
                                              And(FreeQ(List(a, b), x), Not(FreeQ(v, x)),
                                                  IntegersQ(Times(C1D2, m), Times(C1D2, p))))),
                                      Map(Function(Times(C1D2, Slot1)), Rest(u)), u)),
                              Module(List(Set(v, FreeFactors(u, x)), Set(w, NonfreeFactors(u, x))),
                                  CompoundExpression(
                                      Set(v,
                                          Times(NumericFactor(v),
                                              SmartSimplify(Times(NonnumericFactors(v), Sqr(x))),
                                              Power(x, CN2))),
                                      Set(w,
                                          If(ProductQ(w), Map(Function(SimpHelp(Slot1, x)), w),
                                              SimpHelp(w, x))),
                                      Set(w, FactorNumericGcd(w)), Set(v, MergeFactors(v, w)),
                                      If(ProductQ(v), Map(Function(SimpFixFactor(Slot1, x)), v),
                                          v)))),
                          If(SumQ(u), If(
                              MatchQ(u,
                                  Condition(Plus(a_DEFAULT, Times(n_, Pi), Times(b_DEFAULT, x)),
                                      And(FreeQ(List(a, b), x), EqQ(Sqr(n), QQ(1L, 16L))))),
                              u,
                              If(And(PolynomialQ(u, x), LessEqual(Exponent(u, x), C0)),
                                  SimpHelp(Coefficient(u, x, C0), x),
                                  If(And(PolynomialQ(u, x), Equal(Exponent(u, x), C1),
                                      SameQ(Coefficient(u, x, C0), C0)),
                                      Times(SimpHelp(Coefficient(u, x, C1), x), x),
                                      Module(List(Set(v, C0), Set(w, C0)), CompoundExpression(
                                          Scan(Function(If(FreeQ(Slot1, x), Set(v, Plus(Slot1, v)),
                                              Set(w, Plus(Slot1, w)))), u),
                                          Set(v, SmartSimplify(v)),
                                          Set(w, If(SumQ(w), Map(Function(SimpHelp(Slot1, x)), w),
                                              SimpHelp(w, x))),
                                          Plus(v, w)))))),
                              If(TrigQ(u),
                                  With(
                                      List(Set(v,
                                          SimpHelp(Part(u, C1), x))),
                                      If(And(LinearQ(v, x), MatchQ(
                                          Coefficient(v, x, C0),
                                          Condition(Plus(
                                              Times(m_DEFAULT,
                                                  Plus(Times(n_DEFAULT, Pi), r_DEFAULT)),
                                              s_DEFAULT), RationalQ(m, n)))),
                                          NormalizeTrig(Head(u), Coefficient(v, x, C0),
                                              Coefficient(v, x, C1), x),
                                          $(Head(u), v))),
                                  If(HyperbolicQ(u),
                                      With(List(Set(v, SimpHelp(Part(u, C1), x))), If(
                                          And(LinearQ(v, x),
                                              MatchQ(Coefficient(v, x, C0), Condition(
                                                  Plus(Times(m_DEFAULT,
                                                      Plus(Times(n_DEFAULT, Complex(C0, $p("nz")),
                                                          Pi), r_DEFAULT)),
                                                      s_DEFAULT),
                                                  RationalQ(m, n, $s("nz"))))),
                                          NormalizeHyperbolic(Head(u), Coefficient(v, x, C0),
                                              Coefficient(v, x, C1), x),
                                          $(Head(u), v))),
                                      Map(Function(SimpHelp(Slot1, x)), u)))))))),
          ISetDelayed(205,
              NormalizeTrig($p("func"),
                  Plus(Times(m_DEFAULT, Plus(Times(Pi, n_DEFAULT), r_DEFAULT)), s_DEFAULT), b_, x_),
              Condition(
                  If(And(Equal(Times(m, n), C1D4), NegQ(b)),
                      Switch($s("func"), Sin,
                          Cos(Subtract(
                              Subtract(Subtract(Times(C1D4, Pi), Times(m, r)), s), Times(b, x))),
                          Cos,
                          Sin(Subtract(
                              Subtract(Subtract(Times(C1D4, Pi), Times(m, r)), s), Times(b, x))),
                          Tan,
                          Cot(Subtract(
                              Subtract(Subtract(Times(C1D4, Pi), Times(m, r)), s), Times(b, x))),
                          Cot,
                          Tan(Subtract(Subtract(Subtract(Times(C1D4, Pi), Times(m, r)), s),
                              Times(b, x))),
                          Sec, Csc(
                              Subtract(Subtract(Subtract(Times(C1D4, Pi), Times(m, r)), s),
                                  Times(b, x))),
                          Csc, Sec(
                              Subtract(Subtract(Subtract(Times(C1D4, Pi), Times(m, r)), s),
                                  Times(b, x)))),
                      If(Equal(Times(m, n), CN1D4),
                          If(PosQ(b),
                              Switch($s("func"), Sin,
                                  Negate(Cos(Plus(Times(C1D4, Pi), Times(m, r), s,
                                      Times(b, x)))),
                                  Cos, Sin(Plus(Times(C1D4, Pi), Times(m, r), s, Times(b, x))), Tan,
                                  Negate(Cot(Plus(Times(C1D4, Pi), Times(m, r), s, Times(b, x)))),
                                  Cot,
                                  Negate(Tan(Plus(Times(C1D4, Pi), Times(m, r), s,
                                      Times(b, x)))),
                                  Sec, Csc(Plus(Times(C1D4, Pi), Times(m, r), s, Times(b, x))), Csc,
                                  Negate(Sec(Plus(Times(C1D4, Pi), Times(m, r), s, Times(b, x))))),
                              Switch($s("func"), Sin,
                                  Negate(
                                      Sin(Subtract(
                                          Subtract(Subtract(Times(C1D4, Pi),
                                              Times(m, r)), s),
                                          Times(b, x)))),
                                  Cos, Cos(
                                      Subtract(Subtract(Subtract(Times(C1D4, Pi), Times(m, r)), s),
                                          Times(b, x))),
                                  Tan,
                                  Negate(Tan(
                                      Subtract(Subtract(Subtract(Times(C1D4, Pi), Times(m, r)), s),
                                          Times(b, x)))),
                                  Cot,
                                  Negate(Cot(
                                      Subtract(Subtract(Subtract(Times(C1D4, Pi), Times(m, r)), s),
                                          Times(b, x)))),
                                  Sec,
                                  Sec(Subtract(
                                      Subtract(Subtract(Times(C1D4, Pi), Times(m, r)), s),
                                      Times(b, x))),
                                  Csc,
                                  Negate(Csc(
                                      Subtract(Subtract(Subtract(Times(C1D4, Pi), Times(m, r)), s),
                                          Times(b, x)))))),
                          $($s("func"), Plus(Times(m, n, Pi), Times(m, r), s, Times(b, x))))),
                  RationalQ(m, n))),
          ISetDelayed(206, NormalizeHyperbolic(
              $p("func"),
              Plus(
                  Times(m_DEFAULT, Plus(Times(Pi, Complex(C0, $p("nz")), n_DEFAULT), r_DEFAULT)),
                  s_DEFAULT),
              b_, x_),
              Condition(If(And(Equal(Times(m, n, $s("nz")), C1D4), NegQ(b)),
                  Switch($s("func"), Sinh,
                      Times(CI, Cosh(Subtract(
                          Subtract(Subtract(Times(CI, C1D4, Pi), Times(m, r)), s), Times(b, x)))),
                      Cosh,
                      Times(CN1, CI, Sinh(Subtract(
                          Subtract(Subtract(Times(CI, C1D4, Pi), Times(m, r)), s), Times(b, x)))),
                      Tanh,
                      Negate(Coth(Subtract(
                          Subtract(Subtract(Times(CI, C1D4, Pi), Times(m, r)), s), Times(b, x)))),
                      Coth,
                      Negate(Tanh(Subtract(Subtract(Subtract(Times(CI, C1D4, Pi), Times(m, r)), s),
                          Times(b, x)))),
                      Sech,
                      Times(CI,
                          Csch(
                              Subtract(Subtract(Subtract(Times(CI, C1D4, Pi), Times(m, r)), s),
                                  Times(b, x)))),
                      Csch,
                      Times(CN1, CI,
                          Sech(Subtract(Subtract(Subtract(Times(CI, C1D4, Pi), Times(m, r)), s),
                              Times(b, x))))),
                  If(Equal(Times(m, n, $s("nz")), CN1D4),
                      If(PosQ(b),
                          Switch($s("func"), Sinh,
                              Times(CN1, CI,
                                  Cosh(Plus(Times(CI, C1D4, Pi), Times(m, r), s, Times(b, x)))),
                              Cosh,
                              Times(
                                  CN1, CI, Sinh(Plus(Times(CI, C1D4, Pi), Times(m, r), s,
                                      Times(b, x)))),
                              Tanh, Coth(Plus(Times(CI, C1D4, Pi), Times(m, r), s, Times(b,
                                  x))),
                              Coth, Tanh(Plus(Times(CI, C1D4, Pi), Times(m, r), s, Times(b, x))),
                              Sech,
                              Times(CI, Csch(Plus(Times(CI, C1D4, Pi), Times(m, r), s,
                                  Times(b, x)))),
                              Csch,
                              Times(
                                  CI, Sech(Plus(Times(CI, C1D4, Pi), Times(m, r), s,
                                      Times(b, x))))),
                          Switch($s("func"), Sinh,
                              Negate(
                                  Sinh(
                                      Subtract(
                                          Subtract(Subtract(Times(CI, C1D4, Pi), Times(m, r)),
                                              s),
                                          Times(b, x)))),
                              Cosh,
                              Cosh(Subtract(Subtract(Subtract(Times(CI, C1D4, Pi), Times(m, r)), s),
                                  Times(b, x))),
                              Tanh,
                              Negate(
                                  Tanh(
                                      Subtract(
                                          Subtract(Subtract(Times(CI, C1D4, Pi), Times(m, r)), s),
                                          Times(b, x)))),
                              Coth,
                              Negate(
                                  Coth(
                                      Subtract(
                                          Subtract(Subtract(Times(CI, C1D4, Pi), Times(m, r)),
                                              s),
                                          Times(b, x)))),
                              Sech,
                              Sech(Subtract(Subtract(Subtract(Times(CI, C1D4, Pi), Times(m, r)), s),
                                  Times(b, x))),
                              Csch,
                              Negate(Csch(
                                  Subtract(Subtract(Subtract(Times(CI, C1D4, Pi), Times(m, r)), s),
                                      Times(b, x)))))),
                      $($s("func"), Plus(Times(m, n, $s("nz"), CI, Pi), Times(m, r), s,
                          Times(b, x))))),
                  RationalQ(m, n, $s("nz")))),
          ISetDelayed(
              207, FractionalPowerOfSquareQ(u_), If(
                  AtomQ(u), False, If(
                      And(FractionalPowerQ(u),
                          MatchQ(
                              Part(u, C1), Condition(Times(a_DEFAULT, Sqr(
                                  Plus(b_, c_))), NonsumQ(
                                      a)))),
                      Part(u, C1),
                      Module(List($s("tmp")),
                          Catch(CompoundExpression(Scan(Function(
                              If(Not(FalseQ(Set($s("tmp"), FractionalPowerOfSquareQ(Slot1)))),
                                  Throw($s("tmp")))),
                              u), False)))))),
          ISetDelayed(208, FractionalPowerSubexpressionQ(u_, v_, w_),
              If(AtomQ(u), False,
                  If(And(FractionalPowerQ(u), GtQ(Times(Part(u, C1), Power(w, CN1)), C0)),
                      And(Not(SameQ(Part(u, C1), v)), Less(LeafCount(w), Times(C3, LeafCount(v)))),
                      Catch(CompoundExpression(Scan(
                          Function(If(FractionalPowerSubexpressionQ(Slot1, v, w), Throw(True))), u),
                          False))))),
          ISetDelayed(209,
              FixSimplify(
                  Times(
                      Complex(C0, a_), u_DEFAULT, Power(Plus(Times(Complex(C0, b_), v_DEFAULT), w_),
                          n_DEFAULT))),
              Condition(
                  Times(
                      Power(CN1, Times(C1D2,
                          Plus(n, C1))),
                      a, u, FixSimplify(Power(Subtract(Times(b, v), Times(CI, w)), n))),
                  OddQ(n))),
          ISetDelayed(
              210, FixSimplify(Times(Power(u_, m_DEFAULT), Power(v_, n_),
                  w_DEFAULT)),
              Condition(
                  With(
                      List(
                          Set(z,
                              Simplify(Times(Power(u, Times(m, Power(GCD(m, n), CN1))),
                                  Power(v, Times(n, Power(GCD(m, n), CN1))))))),
                      Condition(
                          FixSimplify(Times(w,
                              Power(z, GCD(m, n)))),
                          Or(AbsurdNumberQ(z), SqrtNumberSumQ(z)))),
                  And(RationalQ(m), FractionQ(n), SqrtNumberSumQ(u), SqrtNumberSumQ(v), GtQ(u, C0),
                      GtQ(v, C0)))),
          ISetDelayed(
              211, FixSimplify(Times(Power(u_, m_DEFAULT), Power(v_, n_),
                  w_DEFAULT)),
              Condition(
                  With(
                      List(Set(z,
                          Simplify(Times(Power(u, Times(m, Power(GCD(m, Negate(n)), CN1))),
                              Power(v, Times(n, Power(GCD(m, Negate(n)), CN1))))))),
                      Condition(
                          FixSimplify(Times(w, Power(z, GCD(m, Negate(n))))), Or(AbsurdNumberQ(z),
                              SqrtNumberSumQ(z)))),
                  And(RationalQ(m), FractionQ(n), SqrtNumberSumQ(u), SqrtNumberSumQ(Power(v, CN1)),
                      GtQ(u, C0), GtQ(v, C0)))),
          ISetDelayed(
              212, FixSimplify(Times(Power(u_, m_DEFAULT), Power(v_, n_),
                  w_DEFAULT)),
              Condition(
                  With(
                      List(
                          Set(z,
                              Simplify(Times(Power(Negate(u), Times(m, Power(GCD(m, n), CN1))),
                                  Power(v, Times(n, Power(GCD(m, n), CN1))))))),
                      Condition(
                          FixSimplify(Times(CN1, w,
                              Power(z, GCD(m, n)))),
                          Or(AbsurdNumberQ(z), SqrtNumberSumQ(z)))),
                  And(IntegerQ(m), FractionQ(n), SqrtNumberSumQ(u), SqrtNumberSumQ(v), LtQ(u, C0),
                      GtQ(v, C0)))),
          ISetDelayed(
              213, FixSimplify(Times(Power(u_, m_DEFAULT), Power(v_, n_),
                  w_DEFAULT)),
              Condition(
                  With(
                      List(
                          Set(z,
                              Simplify(Times(
                                  Power(Negate(u), Times(m, Power(GCD(m, Negate(n)), CN1))), Power(
                                      v, Times(n, Power(GCD(m, Negate(n)), CN1))))))),
                      Condition(
                          FixSimplify(Times(CN1, w, Power(z, GCD(m, Negate(n))))), Or(
                              AbsurdNumberQ(z), SqrtNumberSumQ(z)))),
                  And(IntegerQ(m), FractionQ(n), SqrtNumberSumQ(u), SqrtNumberSumQ(Power(v, CN1)),
                      LtQ(u, C0), GtQ(v, C0)))),
          ISetDelayed(214,
              FixSimplify(Times(Power(a_, m_),
                  Power(Plus(u_, Times(Power(b_, n_), v_DEFAULT)), p_DEFAULT), w_DEFAULT)),
              Condition(
                  With(
                      List(Set(c, Simplify(Times(Power(a, Times(m, Power(p, CN1))), Power(b, n))))),
                      Condition(
                          FixSimplify(Times(w,
                              Power(Plus(Times(Power(a, Times(m, Power(p, CN1))), u), Times(c, v)),
                                  p))),
                          RationalQ(c))),
                  And(RationalQ(a, b, m, n), Greater(a, C0), Greater(b, C0), IGtQ(p, C0)))),
          ISetDelayed(215,
              FixSimplify(
                  Times(Power(a_, m_DEFAULT),
                      Plus(Times(Power(a_, n_), u_DEFAULT),
                          Times(Power(b_, p_DEFAULT), v_DEFAULT)),
                      w_DEFAULT)),
              Condition(
                  With(
                      List(Set(z,
                          Times(w, Power(a, Plus(m, n)), Plus(u,
                              Times(Power(CN1, p), Power(a, Subtract(p, n)), v))))),
                      Condition(
                          FixSimplify(z), Not(
                              MatchQ(z,
                                  Condition(
                                      Times($p("ww", true), Power($p("aa"), $p("mm")),
                                          Plus($p("uu"),
                                              Times(Power($p("bb"), $p("nn")), $p("vv", true)))),
                                      And(RationalQ($s("aa"), $s("bb"), $s("mm"), $s("nn")),
                                          Greater($s("aa"), C0), Greater($s("bb"), C0),
                                          RationalQ(Simplify(Times(Power($s("aa"), $s("mm")),
                                              Power($s("bb"), $s("nn"))))))))))),
                  And(RationalQ(m), FractionQ(n), IntegerQ(p), Greater(Subtract(p, n), C0),
                      SameQ(Plus(a, b), C0)))),
          ISetDelayed(216,
              FixSimplify(Times(Power(Plus(a_,
                  b_), m_DEFAULT), Power(Plus(c_, d_),
                      n_),
                  w_DEFAULT)),
              Condition(
                  With(
                      List(Set(q,
                          Simplify(Times(b, Power(d, CN1))))),
                      Condition(
                          FixSimplify(Times(w, Power(q, m),
                              Power(Plus(c, d), Plus(m, n)))),
                          FreeQ(q, Plus))),
                  And(IntegerQ(m), Not(IntegerQ(n)), EqQ(Subtract(Times(b, c), Times(a, d)), C0)))),
          ISetDelayed(217,
              FixSimplify(
                  Times(
                      Power(
                          Plus(
                              Times(Power(a_, m_DEFAULT), u_DEFAULT), Times(Power(a_, n_DEFAULT),
                                  v_DEFAULT)),
                          t_DEFAULT),
                      w_DEFAULT)),
              Condition(
                  FixSimplify(Times(Power(a, Times(m, t)), w,
                      Power(Plus(u, Times(Power(a, Subtract(n, m)), v)), t))),
                  And(Not(RationalQ(a)), IntegerQ(t), RationalQ(m, n),
                      And(Less(C0, m), LessEqual(m, n))))),
          ISetDelayed(218, FixSimplify(Times(w_DEFAULT,
              Power(Plus(Times(Power(a_, m_DEFAULT), u_DEFAULT),
                  Times(Power(a_, n_DEFAULT), v_DEFAULT), Times(Power(a_, p_DEFAULT), z_DEFAULT)),
                  t_DEFAULT))),
              Condition(
                  FixSimplify(Times(Power(a, Times(m, t)), w,
                      Power(Plus(u, Times(Power(a, Subtract(n, m)), v),
                          Times(Power(a, Subtract(p, m)), z)), t))),
                  And(Not(RationalQ(a)), IntegerQ(t), RationalQ(m, n, p),
                      And(Less(C0, m), LessEqual(m, n), LessEqual(n, p))))),
          ISetDelayed(219, FixSimplify(Times(w_DEFAULT, Power(
              Plus(Times(Power(a_, m_DEFAULT), u_DEFAULT), Times(Power(a_, n_DEFAULT), v_DEFAULT),
                  Times(Power(a_, q_DEFAULT), y_DEFAULT), Times(Power(a_, p_DEFAULT), z_DEFAULT)),
              t_DEFAULT))),
              Condition(
                  FixSimplify(Times(Power(a, Times(m, t)), w,
                      Power(Plus(u, Times(Power(a, Subtract(n, m)), v),
                          Times(Power(a, Subtract(p, m)), z), Times(Power(a, Subtract(q, m)), y)),
                          t))),
                  And(Not(RationalQ(a)), IntegerQ(t), RationalQ(m, n, p),
                      And(Less(C0, m), LessEqual(m, n), LessEqual(n, p), LessEqual(p, q))))),
          ISetDelayed(220,
              FixSimplify(
                  Times(
                      Plus(u_DEFAULT, Times(b_DEFAULT, Sqrt(v_)), Times(c_DEFAULT, Sqrt(v_)),
                          Times(d_DEFAULT, Sqrt(v_)), Times(a_DEFAULT, Sqrt($p(v, Plus)))),
                      w_DEFAULT)),
              FixSimplify(Times(w, Plus(u, Times(FixSimplify(Plus(a, b, c, d)), Sqrt(v)))))));
}
