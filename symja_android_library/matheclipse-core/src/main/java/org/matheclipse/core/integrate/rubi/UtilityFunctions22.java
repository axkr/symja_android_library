package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;

/**
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 * 
 */
class UtilityFunctions22 {
  public static IAST RULES = List(
      ISetDelayed(376, Dist(u_, $(Defer($rubi("Dist")), v_, w_, x_), x_), Dist(Times(u, v), w,
          x)),
      ISetDelayed(377, Dist(u_, v_, x_),
          If(SameQ(u, C1), v,
              If(SameQ(u, C0),
                  CompoundExpression(
                      Print($str("*** Warning ***:  Dist[0,"), v, $str(","), x, $str("]")), C0),
                  If(And(Less(NumericFactor(u), C0), Greater(NumericFactor(Negate(u)), C0)),
                      Negate(Dist(Negate(u), v, x)),
                      If(SumQ(v), Map(Function(Dist(u, Slot1, x)), v),
                          If(IntegralFreeQ(v), Simp(Times(u, v), x),
                              With(
                                  List(Set(w, Times(Simp(Times(u, Sqr(x)), x), Power(x, CN2)))), If(
                                      And(UnsameQ(w, u), FreeQ(w, x), SameQ(w, Simp(w, x)),
                                          SameQ(w,
                                              Times(Simp(Times(w, Sqr(x)), x), Power(x, CN2)))),
                                      Dist(w, v, x),
                                      If(UnsameQ($s("§$showsteps"), True), Simp(Times(u, v), x),
                                          $(Defer($rubi("Dist")), u, v, x)))))))))),
      ISetDelayed(
          378, DistributeDegree(u_, m_), If(
              AtomQ(u), Power(u, m), If(
                  PowerQ(u), Power(Part(u, C1), Times(Part(u, C2), m)), If(ProductQ(u),
                      Map(Function(DistributeDegree(Slot1, m)), u), Power(u, m))))),
      ISetDelayed(379, FunctionOfLinear(u_, x_Symbol),
          With(
              List(Set($s("lst"),
                  FunctionOfLinear(u, False, False, x, False))),
              If(Or(
                  AtomQ($s("lst")), FalseQ(Part($s("lst"),
                      C1)),
                  And(SameQ(Part($s("lst"), C1), C0), SameQ(Part($s("lst"), C2), C1))), False,
                  List(
                      FunctionOfLinearSubst(u, Part($s(
                          "lst"), C1), Part($s("lst"),
                              C2),
                          x),
                      Part($s("lst"), C1), Part($s("lst"), C2))))),
      ISetDelayed(380, FunctionOfLinear(u_, a_, b_, x_, $p("flag")),
          If(FreeQ(u, x), List(a, b), If(CalculusQ(u), False,
              If(LinearQ(u, x), If(FalseQ(a), List(Coefficient(u, x, C0), Coefficient(u, x, C1)),
                  With(List(Set($s("lst"), CommonFactors(List(b, Coefficient(u, x, C1))))),
                      If(And(EqQ(Coefficient(u, x, C0), C0), Not($s("flag"))),
                          List(C0, Part($s("lst"), C1)),
                          If(EqQ(Subtract(Times(b, Coefficient(u, x, C0)),
                              Times(a, Coefficient(u, x, C1))), C0),
                              List(Times(a, Power(Part($s("lst"), C2), CN1)), Part($s("lst"), C1)),
                              List(C0, C1))))),
                  If(And(PowerQ(u), FreeQ(Part(u, C1), x)),
                      FunctionOfLinear(Times(Log(Part(u,
                          C1)), Part(u,
                              C2)),
                          a, b, x, False),
                      Module(
                          List($s(
                              "lst")),
                          If(And(ProductQ(u),
                              NeQ(Part(Set($s("lst"), MonomialFactor(u, x)), C1), C0)),
                              If(And(False, IntegerQ(Part($s("lst"), C1)),
                                  Unequal(Part($s("lst"), C1), CN1), FreeQ(Part($s("lst"), C2), x)),
                                  If(And(RationalQ(LeadFactor(Part($s("lst"), C2))),
                                      Less(LeadFactor(Part($s("lst"), C2)), C0)),
                                      FunctionOfLinear(
                                          Times(DivideDegreesOfFactors(Negate(Part($s("lst"), C2)),
                                              Part($s("lst"), C1)), x),
                                          a, b, x, False),
                                      FunctionOfLinear(Times(DivideDegreesOfFactors(
                                          Part($s("lst"), C2), Part($s("lst"), C1)), x), a, b, x,
                                          False)),
                                  False),
                              CompoundExpression(Set($s("lst"), List(a, b)),
                                  Catch(CompoundExpression(
                                      Scan(Function(CompoundExpression(
                                          Set($s("lst"),
                                              FunctionOfLinear(Slot1, Part($s("lst"), C1),
                                                  Part($s("lst"), C2), x, SumQ(u))),
                                          If(AtomQ($s("lst")), Throw(False)))), u),
                                      $s("lst"))))))))))),
      ISetDelayed(381, FunctionOfLinearSubst(u_, a_, b_, x_),
          If(FreeQ(u, x), u,
              If(LinearQ(u, x), Module(List(Set($s("tmp"), Coefficient(u, x, C1))),
                  CompoundExpression(Set($s("tmp"), If(SameQ($s("tmp"), b), C1,
                      Times($s("tmp"), Power(b, CN1)))), Plus(Coefficient(u, x, C0),
                          Times(CN1, a, $s("tmp")), Times($s("tmp"), x)))),
                  If(And(PowerQ(u), FreeQ(Part(u, C1),
                      x)), Exp(
                          FullSimplify(
                              FunctionOfLinearSubst(Times(Log(Part(u, C1)), Part(u, C2)), a, b,
                                  x))),
                      Module(List($s("lst")), If(
                          And(ProductQ(u), NeQ(Part(Set($s("lst"), MonomialFactor(u, x)), C1), C0)),
                          If(And(
                              RationalQ(LeadFactor(Part($s("lst"), C2))),
                              Less(LeadFactor(Part($s("lst"), C2)), C0)),
                              Negate(
                                  Power(
                                      FunctionOfLinearSubst(Times(DivideDegreesOfFactors(
                                          Negate(Part($s("lst"), C2)), Part($s("lst"), C1)), x), a,
                                          b, x),
                                      Part($s("lst"), C1))),
                              Power(FunctionOfLinearSubst(Times(
                                  DivideDegreesOfFactors(Part($s("lst"), C2), Part($s("lst"), C1)),
                                  x), a, b, x), Part($s("lst"), C1))),
                          Map(Function(FunctionOfLinearSubst(Slot1, a, b, x)), u))))))),
      ISetDelayed(382, DivideDegreesOfFactors(u_, n_),
          If(ProductQ(u),
              Map(Function(Power(LeadBase(Slot1), Times(LeadDegree(Slot1), Power(n, CN1)))), u),
              Power(LeadBase(u), Times(LeadDegree(u), Power(n, CN1))))),
      ISetDelayed(383, FunctionOfInverseLinear(u_, x_Symbol), FunctionOfInverseLinear(u, Null, x)),
      ISetDelayed(384, FunctionOfInverseLinear(u_, $p("lst"), x_),
          If(FreeQ(u, x), $s("lst"),
              If(SameQ(u, x), False,
                  If(QuotientOfLinearsQ(u, x),
                      With(List(Set($s("tmp"), Drop(QuotientOfLinearsParts(u, x), C2))),
                          If(SameQ(Part($s("tmp"), C2), C0), False,
                              If(SameQ($s("lst"), Null), $s("tmp"),
                                  If(EqQ(Subtract(Times(Part($s("lst"), C1), Part($s("tmp"), C2)),
                                      Times(Part($s("lst"), C2), Part($s("tmp"), C1))), C0), $s(
                                          "lst"),
                                      False)))),
                      If(CalculusQ(u), False, Module(List(Set($s("tmp"), $s("lst"))),
                          Catch(CompoundExpression(Scan(Function(If(
                              AtomQ(Set($s("tmp"), FunctionOfInverseLinear(Slot1, $s("tmp"), x))),
                              Throw(False))), u), $s("tmp"))))))))),
      ISetDelayed(385, FunctionOfExponentialQ(u_, x_Symbol), Block(
          List(Set($s("$base$"), Null), Set($s("$expon$"), Null), Set($s("§$exponflag$"), False)),
          And(FunctionOfExponentialTest(u, x), $s("§$exponflag$")))),
      ISetDelayed(386, FunctionOfExponential(u_, x_Symbol),
          Block(
              List(Set($s("$base$"), Null), Set($s("$expon$"), Null),
                  Set($s("§$exponflag$"), False)),
              CompoundExpression(FunctionOfExponentialTest(u, x),
                  Power($s("$base$"), $s("$expon$"))))),
      ISetDelayed(387, FunctionOfExponentialFunction(u_, x_Symbol),
          Block(
              List(
                  Set($s("$base$"), Null), Set($s(
                      "$expon$"), Null),
                  Set($s("§$exponflag$"), False)),
              CompoundExpression(
                  FunctionOfExponentialTest(u, x), SimplifyIntegrand(
                      FunctionOfExponentialFunctionAux(u, x), x)))),
      ISetDelayed(388, FunctionOfExponentialFunctionAux(u_, x_),
          If(AtomQ(u), u, If(And(PowerQ(u), FreeQ(Part(u, C1), x), LinearQ(Part(u, C2), x)),
              If(EqQ(Coefficient($s("$expon$"), x, C0), C0),
                  Times(Power(Part(u, C1), Coefficient(Part(u, C2), x, C0)), Power(x,
                      FullSimplify(Times(Log(Part(u, C1)), Coefficient(Part(u, C2), x, C1),
                          Power(Times(Log($s("$base$")), Coefficient($s("$expon$"), x, C1)),
                              CN1))))),
                  Power(x,
                      FullSimplify(
                          Times(Log(Part(u, C1)), Coefficient(Part(u, C2), x, C1),
                              Power(
                                  Times(Log($s("$base$")),
                                      Coefficient($s("$expon$"), x, C1)),
                                  CN1))))),
              If(And(HyperbolicQ(u), LinearQ(Part(u, C1), x)),
                  Module(List($s("tmp")), CompoundExpression(
                      Set($s("tmp"),
                          Power(x,
                              FullSimplify(Times(Coefficient(Part(u, C1), x, C1),
                                  Power(Times(Log($s("$base$")), Coefficient($s("$expon$"), x, C1)),
                                      CN1))))),
                      Switch(Head(u), Sinh,
                          Subtract(Times(C1D2, $s("tmp")), Power(Times(C2, $s("tmp")), CN1)), Cosh,
                          Plus(Times(C1D2, $s("tmp")), Power(Times(C2, $s("tmp")), CN1)), Tanh,
                          Times(Subtract($s("tmp"), Power($s("tmp"), CN1)),
                              Power(Plus($s("tmp"), Power($s("tmp"), CN1)), CN1)),
                          Coth,
                          Times(Plus($s("tmp"), Power($s("tmp"), CN1)),
                              Power(Subtract($s("tmp"), Power($s("tmp"), CN1)), CN1)),
                          Sech, Times(C2, Power(Plus($s("tmp"), Power($s("tmp"), CN1)), CN1)), Csch,
                          Times(C2, Power(Subtract($s("tmp"), Power($s("tmp"), CN1)), CN1))))),
                  If(And(PowerQ(u), FreeQ(Part(u, C1), x), SumQ(Part(u, C2))),
                      Times(
                          FunctionOfExponentialFunctionAux(Power(Part(u, C1), First(Part(u, C2))),
                              x),
                          FunctionOfExponentialFunctionAux(Power(Part(u, C1), Rest(Part(u, C2))),
                              x)),
                      Map(Function(FunctionOfExponentialFunctionAux(Slot1, x)), u)))))),
      ISetDelayed(
          389, FunctionOfExponentialTest(u_, x_), If(
              FreeQ(u, x), True, If(
                  Or(SameQ(u,
                      x), CalculusQ(u)),
                  False, If(
                      And(PowerQ(u), FreeQ(Part(u,
                          C1), x), LinearQ(Part(u, C2),
                              x)),
                      CompoundExpression(
                          Set($s(
                              "§$exponflag$"), True),
                          FunctionOfExponentialTestAux(Part(u, C1), Part(u, C2), x)),
                      If(And(HyperbolicQ(u), LinearQ(Part(u, C1), x)),
                          FunctionOfExponentialTestAux(E, Part(u, C1), x),
                          If(And(PowerQ(u), FreeQ(Part(u, C1), x), SumQ(Part(u, C2))), And(
                              FunctionOfExponentialTest(Power(Part(u, C1), First(Part(u, C2))), x),
                              FunctionOfExponentialTest(Power(Part(u, C1), Rest(Part(u, C2))), x)),
                              Catch(CompoundExpression(Scan(
                                  Function(
                                      If(Not(FunctionOfExponentialTest(Slot1, x)), Throw(False))),
                                  u), True)))))))),
      ISetDelayed(
          390, FunctionOfExponentialTestAux($p("base"), $p(
              "expon"), x_),
          If(SameQ($s("$base$"), Null),
              CompoundExpression(Set($s("$base$"), $s("base")), Set($s("$expon$"),
                  $s("expon")), True),
              Module(
                  List($s(
                      "tmp")),
                  CompoundExpression(
                      Set($s("tmp"),
                          FullSimplify(
                              Times(Log($s("base")), Coefficient($s("expon"), x, C1),
                                  Power(
                                      Times(Log($s("$base$")),
                                          Coefficient($s("$expon$"), x, C1)),
                                      CN1)))),
                      If(Not(RationalQ($s("tmp"))), False, If(
                          Or(EqQ(Coefficient($s("$expon$"), x, C0), C0), NeQ($s("tmp"),
                              FullSimplify(Times(Log($s("base")), Coefficient($s("expon"), x, C0),
                                  Power(Times(Log($s("$base$")), Coefficient($s("$expon$"), x, C0)),
                                      CN1))))),
                          CompoundExpression(
                              If(And(IGtQ($s("base"), C0), IGtQ($s("$base$"), C0),
                                  Less($s("base"), $s("$base$"))),
                                  CompoundExpression(Set($s("$base$"), $s("base")),
                                      Set($s("$expon$"), $s("expon")),
                                      Set($s("tmp"), Power($s("tmp"), CN1)))),
                              Set($s("$expon$"),
                                  Times(Coefficient($s("$expon$"), x, C1), x,
                                      Power(Denominator($s("tmp")), CN1))),
                              If(And(Less($s("tmp"), C0), NegQ(Coefficient($s("$expon$"), x, C1))),
                                  CompoundExpression(Set($s("$expon$"), Negate($s("$expon$"))),
                                      True),
                                  True)),
                          CompoundExpression(
                              If(And(IGtQ($s("base"), C0), IGtQ($s("$base$"), C0),
                                  Less($s("base"), $s("$base$"))),
                                  CompoundExpression(Set($s("$base$"), $s("base")),
                                      Set($s("$expon$"), $s("expon")),
                                      Set($s("tmp"), Power($s("tmp"), CN1)))),
                              Set($s("$expon$"),
                                  Times($s("$expon$"), Power(Denominator($s("tmp")), CN1))),
                              If(And(Less($s("tmp"), C0), NegQ(Coefficient($s("$expon$"), x, C1))),
                                  CompoundExpression(Set($s("$expon$"), Negate($s("$expon$"))),
                                      True),
                                  True)))))))),
      ISetDelayed(
          391, FunctionOfTrigOfLinearQ(u_, x_Symbol), If(
              MatchQ(u,
                  Condition(Times(Power(Plus(c_DEFAULT, Times(d_DEFAULT, x)), m_DEFAULT),
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT,
                                  $($p("§trig"), Plus(e_DEFAULT, Times(f_DEFAULT, x))))),
                          n_DEFAULT)),
                      And(FreeQ(List(a, b, c, d, e, f, m, n), x),
                          Or(TrigQ($s("§trig")), HyperbolicQ($s("§trig")))))),
              True,
              And(Not(MemberQ(List(Null, False),
                  FunctionOfTrig(u, Null, x))), AlgebraicTrigFunctionQ(u,
                      x)))),
      ISetDelayed(392, FunctionOfTrig(u_, x_Symbol),
          With(
              List(Set(v,
                  FunctionOfTrig(ActivateTrig(u), Null, x))),
              If(SameQ(v, Null), False, v))),
      ISetDelayed(
          393, FunctionOfTrig(u_, v_, x_), If(
              AtomQ(u), If(SameQ(u,
                  x), False, v),
              If(And(TrigQ(u), LinearQ(Part(u, C1), x)),
                  If(SameQ(v, Null), Part(u, C1),
                      With(
                          List(Set(a, Coefficient(v, x, C0)), Set(b, Coefficient(v, x, C1)),
                              Set(c, Coefficient(Part(u, C1), x, C0)), Set(d,
                                  Coefficient(Part(u, C1), x, C1))),
                          If(And(
                              EqQ(Subtract(Times(a, d),
                                  Times(b, c)), C0),
                              RationalQ(Times(b, Power(d, CN1)))),
                              Plus(Times(a, Power(Numerator(Times(b, Power(d, CN1))), CN1)),
                                  Times(b, x, Power(Numerator(Times(b, Power(d, CN1))), CN1))),
                              False))),
                  If(And(HyperbolicQ(u), LinearQ(Part(u, C1),
                      x)), If(
                          SameQ(v, Null), Times(CI, Part(u,
                              C1)),
                          With(
                              List(Set(a, Coefficient(v, x, C0)), Set(b, Coefficient(v, x, C1)),
                                  Set(c, Times(CI,
                                      Coefficient(Part(u, C1), x, C0))),
                                  Set(d, Times(CI, Coefficient(Part(u, C1), x, C1)))),
                              If(And(
                                  EqQ(Subtract(Times(a, d), Times(b, c)),
                                      C0),
                                  RationalQ(Times(b, Power(d, CN1)))),
                                  Plus(
                                      Times(a, Power(Numerator(Times(b, Power(d, CN1))),
                                          CN1)),
                                      Times(b, x, Power(Numerator(Times(b, Power(d, CN1))), CN1))),
                                  False))),
                      If(CalculusQ(u), False,
                          Module(List(Set(w, v)),
                              Catch(CompoundExpression(Scan(Function(
                                  If(FalseQ(Set(w, FunctionOfTrig(Slot1, w, x))), Throw(False))),
                                  u), w)))))))),
      ISetDelayed(394, AlgebraicTrigFunctionQ(u_, x_Symbol), If(AtomQ(u), True, If(
          And(TrigQ(u), LinearQ(Part(u, C1), x)), True,
          If(And(HyperbolicQ(u), LinearQ(Part(u, C1), x)), True,
              If(And(PowerQ(u), FreeQ(Part(u, C2), x)), AlgebraicTrigFunctionQ(Part(u, C1), x),
                  If(Or(ProductQ(u), SumQ(u)), Catch(CompoundExpression(
                      Scan(Function(If(Not(AlgebraicTrigFunctionQ(Slot1, x)), Throw(False))), u),
                      True)), False)))))),
      ISetDelayed(395, FunctionOfHyperbolic(u_, x_Symbol),
          With(List(Set(v, FunctionOfHyperbolic(u, Null, x))), If(SameQ(v, Null), False, v))));
}
