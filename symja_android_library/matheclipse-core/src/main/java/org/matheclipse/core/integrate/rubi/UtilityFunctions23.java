package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;

/**
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 * 
 */
class UtilityFunctions23 {
  public static IAST RULES =
      List(
          ISetDelayed(
              396, FunctionOfHyperbolic(u_, v_, x_), If(
                  AtomQ(u), If(SameQ(u,
                      x), False, v),
                  If(And(HyperbolicQ(u), LinearQ(Part(u, C1), x)),
                      If(SameQ(v, Null), Part(u, C1),
                          With(
                              List(Set(a, Coefficient(v, x, C0)), Set(b, Coefficient(v, x, C1)),
                                  Set(c, Coefficient(Part(u, C1), x,
                                      C0)),
                                  Set(d, Coefficient(Part(u, C1), x, C1))),
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
                          Module(List(Set(w, v)), Catch(CompoundExpression(Scan(Function(
                              If(FalseQ(Set(w, FunctionOfHyperbolic(Slot1, w, x))), Throw(False))),
                              u), w))))))),
          ISetDelayed(397, FunctionOfQ(v_, u_, x_Symbol, Optional($p("§pureflag"), False)),
              If(FreeQ(u, x), False, If(AtomQ(v), True, If(Not(InertTrigFreeQ(u)),
                  FunctionOfQ(v, ActivateTrig(u), x, $s("§pureflag")),
                  If(And(ProductQ(v), NeQ(FreeFactors(v, x), C1)),
                      FunctionOfQ(NonfreeFactors(v, x), u, x, $s("§pureflag")),
                      If($s("§pureflag"), Switch(Head(v), Sin,
                          PureFunctionOfSinQ(u, Part(v, C1), x), Cos,
                          PureFunctionOfCosQ(u, Part(v, C1), x), Tan,
                          PureFunctionOfTanQ(u, Part(v, C1), x), Cot,
                          PureFunctionOfCotQ(u, Part(v, C1), x), Sec,
                          PureFunctionOfCosQ(u, Part(v, C1), x), Csc,
                          PureFunctionOfSinQ(u, Part(v, C1), x), Sinh,
                          PureFunctionOfSinhQ(u, Part(v, C1), x), Cosh,
                          PureFunctionOfCoshQ(u, Part(v, C1), x), Tanh,
                          PureFunctionOfTanhQ(u, Part(v, C1), x), Coth,
                          PureFunctionOfCothQ(u, Part(v, C1), x), Sech,
                          PureFunctionOfCoshQ(u, Part(v, C1), x), Csch,
                          PureFunctionOfSinhQ(u, Part(v, C1), x), $b(),
                          UnsameQ(FunctionOfExpnQ(u, v, x), False)),
                          Switch(Head(v), Sin, FunctionOfSinQ(u, Part(v, C1), x), Cos,
                              FunctionOfCosQ(u, Part(v, C1), x), Tan,
                              FunctionOfTanQ(u, Part(v, C1), x), Cot,
                              FunctionOfTanQ(u, Part(v, C1), x), Sec,
                              FunctionOfCosQ(u, Part(v, C1), x), Csc,
                              FunctionOfSinQ(u, Part(v, C1), x), Sinh,
                              FunctionOfSinhQ(u, Part(v, C1), x), Cosh,
                              FunctionOfCoshQ(u, Part(v, C1), x), Tanh,
                              FunctionOfTanhQ(u, Part(v, C1), x), Coth,
                              FunctionOfTanhQ(u, Part(v, C1), x), Sech,
                              FunctionOfCoshQ(u, Part(v, C1), x), Csch,
                              FunctionOfSinhQ(u, Part(v, C1), x), $b(),
                              UnsameQ(FunctionOfExpnQ(u, v, x), False)))))))),
          ISetDelayed(
              398, FunctionOfExpnQ(u_, v_, x_), If(
                  SameQ(u, v), C1, If(
                      AtomQ(u), If(SameQ(u,
                          x), False, C0),
                      If(CalculusQ(u), False, If(And(PowerQ(u), FreeQ(Part(u, C2), x)), If(
                          EqQ(Part(u, C1), v), If(IntegerQ(Part(u, C2)), Part(u, C2), C1),
                          If(And(PowerQ(v), FreeQ(Part(v, C2), x), EqQ(Part(u, C1), Part(v, C1))),
                              If(RationalQ(Part(v, C2)), If(And(RationalQ(Part(u, C2)),
                                  IntegerQ(Times(Part(u, C2), Power(Part(v, C2), CN1))),
                                  Or(Greater(Part(v, C2), C0), Less(Part(u, C2), C0))),
                                  Times(Part(u, C2), Power(Part(v, C2), CN1)), False),
                                  If(IntegerQ(
                                      Simplify(Times(Part(u, C2), Power(Part(v, C2), CN1)))),
                                      Simplify(Times(Part(u, C2), Power(Part(v, C2), CN1))),
                                      False)),
                              FunctionOfExpnQ(Part(u, C1), v, x))),
                          If(And(ProductQ(u), NeQ(FreeFactors(u, x), C1)),
                              FunctionOfExpnQ(NonfreeFactors(u, x), v, x),
                              If(And(ProductQ(u), ProductQ(v)), Module(
                                  List(Set($s("deg1"), FunctionOfExpnQ(First(u), First(v), x)),
                                      $s("deg2")),
                                  If(SameQ($s("deg1"), False), False, CompoundExpression(
                                      Set($s("deg2"), FunctionOfExpnQ(Rest(u), Rest(v), x)),
                                      If(And(SameQ($s("deg1"), $s("deg2")), FreeQ(
                                          Simplify(Times(u, Power(Power(v, $s("deg1")), CN1))), x)),
                                          $s("deg1"), False)))),
                                  With(
                                      List(Set($s("lst"),
                                          Map(Function(FunctionOfExpnQ(Slot1, v, x)),
                                              Apply(List, u)))),
                                      If(MemberQ($s("lst"), False), False,
                                          Apply(GCD, $s("lst"))))))))))),
          ISetDelayed(399, PureFunctionOfSinQ(u_, v_, x_),
              If(AtomQ(u), UnsameQ(u, x),
                  If(CalculusQ(u), False,
                      If(And(TrigQ(u), EqQ(Part(u, C1), v)),
                          Or(SameQ(Head(u), Sin), SameQ(Head(u), Csc)),
                          Catch(CompoundExpression(
                              Scan(Function(If(Not(PureFunctionOfSinQ(Slot1, v, x)), Throw(False))),
                                  u),
                              True)))))),
          ISetDelayed(400, PureFunctionOfCosQ(u_, v_, x_), If(AtomQ(u), UnsameQ(u, x), If(
              CalculusQ(u), False,
              If(And(TrigQ(u), EqQ(Part(u, C1), v)), Or(SameQ(Head(u), Cos), SameQ(Head(u), Sec)),
                  Catch(CompoundExpression(
                      Scan(Function(If(Not(PureFunctionOfCosQ(Slot1, v, x)), Throw(False))), u),
                      True)))))),
          ISetDelayed(401, PureFunctionOfTanQ(u_, v_, x_),
              If(AtomQ(u), UnsameQ(u, x),
                  If(CalculusQ(u), False,
                      If(And(TrigQ(u), EqQ(Part(u, C1), v)),
                          Or(SameQ(Head(u), Tan), SameQ(Head(u), Cot)),
                          Catch(
                              CompoundExpression(
                                  Scan(
                                      Function(
                                          If(Not(PureFunctionOfTanQ(Slot1, v, x)), Throw(False))),
                                      u),
                                  True)))))),
          ISetDelayed(402, PureFunctionOfCotQ(u_, v_, x_), If(AtomQ(u), UnsameQ(u, x),
              If(CalculusQ(u), False, If(And(TrigQ(u), EqQ(Part(u, C1), v)), SameQ(Head(u), Cot),
                  Catch(CompoundExpression(
                      Scan(Function(If(Not(PureFunctionOfCotQ(Slot1, v, x)), Throw(False))), u),
                      True)))))),
          ISetDelayed(403, FunctionOfSinQ(u_, v_, x_),
              If(AtomQ(u), UnsameQ(u, x),
                  If(CalculusQ(u), False,
                      If(And(TrigQ(u), IntegerQuotientQ(Part(u, C1), v)),
                          If(OddQuotientQ(Part(u, C1), v),
                              Or(SameQ(Head(u), Sin), SameQ(Head(u), Csc)),
                              Or(SameQ(Head(u), Cos), SameQ(Head(u), Sec))),
                          If(And(IntegerPowerQ(u), TrigQ(Part(u, C1)),
                              IntegerQuotientQ(Part(u, C1, C1), v)),
                              If(EvenQ(Part(u, C2)), True, FunctionOfSinQ(Part(u, C1), v, x)), If(
                                  ProductQ(u), If(
                                      And(SameQ(Head(Part(u, C1)), Cos), SameQ(Head(
                                          Part(u, C2)), Sin), EqQ(Part(u, C1, C1), Times(C1D2, v)),
                                          EqQ(Part(u, C2, C1), Times(C1D2, v))),
                                      FunctionOfSinQ(Drop(u,
                                          C2), v, x),
                                      Module(
                                          List($s(
                                              "lst")),
                                          CompoundExpression(
                                              Set($s("lst"), FindTrigFactor(Sin, Csc, u, v,
                                                  False)),
                                              If(And(ListQ($s("lst")),
                                                  EvenQuotientQ(Part($s("lst"), C1), v)),
                                                  FunctionOfSinQ(Times(Cos(v), Part($s("lst"),
                                                      C2)), v, x),
                                                  CompoundExpression(
                                                      Set($s("lst"), FindTrigFactor(Cos, Sec, u, v,
                                                          False)),
                                                      If(And(ListQ($s("lst")),
                                                          OddQuotientQ(Part($s("lst"), C1), v)),
                                                          FunctionOfSinQ(Times(Cos(v),
                                                              Part($s("lst"), C2)), v, x),
                                                          CompoundExpression(
                                                              Set($s("lst"),
                                                                  FindTrigFactor(Tan, Cot, u, v,
                                                                      True)),
                                                              If(ListQ($s("lst")),
                                                                  FunctionOfSinQ(Times(Cos(v),
                                                                      Part($s("lst"), C2)), v, x),
                                                                  Catch(
                                                                      CompoundExpression(
                                                                          Scan(Function(If(
                                                                              Not(FunctionOfSinQ(
                                                                                  Slot1, v, x)),
                                                                              Throw(False))), u),
                                                                          True)))))))))),
                                  Catch(CompoundExpression(Scan(
                                      Function(If(Not(FunctionOfSinQ(Slot1, v, x)), Throw(False))),
                                      u), True)))))))),
          ISetDelayed(404, FunctionOfCosQ(u_, v_, x_),
              If(AtomQ(u), UnsameQ(u, x),
                  If(CalculusQ(u), False,
                      If(And(TrigQ(u), IntegerQuotientQ(Part(u, C1), v)),
                          Or(SameQ(Head(u), Cos), SameQ(Head(u),
                              Sec)),
                          If(And(
                              IntegerPowerQ(u), TrigQ(Part(u,
                                  C1)),
                              IntegerQuotientQ(Part(u, C1, C1), v)),
                              If(EvenQ(Part(u,
                                  C2)), True, FunctionOfCosQ(Part(u, C1), v,
                                      x)),
                              If(ProductQ(u),
                                  Module(
                                      List($s(
                                          "lst")),
                                      CompoundExpression(
                                          Set($s("lst"), FindTrigFactor(Sin, Csc, u, v,
                                              False)),
                                          If(ListQ($s("lst")),
                                              FunctionOfCosQ(Times(Sin(v),
                                                  Part($s("lst"), C2)), v, x),
                                              CompoundExpression(
                                                  Set($s("lst"), FindTrigFactor(Tan, Cot, u, v,
                                                      True)),
                                                  If(ListQ($s("lst")),
                                                      FunctionOfCosQ(Times(Sin(v),
                                                          Part($s("lst"), C2)), v, x),
                                                      Catch(CompoundExpression(Scan(Function(
                                                          If(Not(FunctionOfCosQ(Slot1, v, x)),
                                                              Throw(False))),
                                                          u), True))))))),
                                  Catch(CompoundExpression(Scan(
                                      Function(If(Not(FunctionOfCosQ(Slot1, v, x)), Throw(False))),
                                      u), True)))))))),
          ISetDelayed(
              405, FunctionOfTanQ(u_, v_, x_), If(
                  AtomQ(u), UnsameQ(u, x), If(
                      CalculusQ(u), False, If(
                          And(TrigQ(u), IntegerQuotientQ(Part(u, C1),
                              v)),
                          Or(SameQ(Head(u), Tan), SameQ(Head(
                              u), Cot), EvenQuotientQ(Part(u, C1),
                                  v)),
                          If(And(PowerQ(u), EvenQ(Part(u, C2)), TrigQ(Part(u, C1)),
                              IntegerQuotientQ(Part(u, C1, C1), v)), True,
                              If(And(PowerQ(u), EvenQ(Part(u, C2)), SumQ(Part(u, C1))),
                                  FunctionOfTanQ(Expand(Sqr(Part(u, C1))), v, x), If(
                                      ProductQ(u),
                                      Module(
                                          List(Set($s("lst"),
                                              ReapList(Scan(Function(
                                                  If(Not(FunctionOfTanQ(Slot1, v, x)), Sow(Slot1))),
                                                  u)))),
                                          If(SameQ($s("lst"), List()), True,
                                              And(Equal(Length($s("lst")), C2),
                                                  OddTrigPowerQ(Part($s("lst"), C1), v, x),
                                                  OddTrigPowerQ(Part($s("lst"), C2), v, x)))),
                                      Catch(CompoundExpression(Scan(
                                          Function(
                                              If(Not(FunctionOfTanQ(Slot1, v, x)), Throw(False))),
                                          u), True))))))))),
          ISetDelayed(
              406, OddTrigPowerQ(u_, v_, x_), If(MemberQ(List(Sin, Cos, Sec, Csc), Head(u)),
                  OddQuotientQ(Part(u,
                      C1), v),
                  If(PowerQ(u), And(OddQ(Part(u,
                      C2)), OddTrigPowerQ(Part(u, C1), v,
                          x)),
                      If(ProductQ(u),
                          If(NeQ(FreeFactors(u, x), C1), OddTrigPowerQ(NonfreeFactors(u, x), v, x),
                              Module(
                                  List(
                                      Set($s("lst"),
                                          ReapList(Scan(
                                              Function(
                                                  If(Not(FunctionOfTanQ(Slot1, v, x)), Sow(Slot1))),
                                              u)))),
                                  If(SameQ($s("lst"), List()), True,
                                      And(Equal(Length($s("lst")), C1),
                                          OddTrigPowerQ(Part($s("lst"), C1), v, x))))),
                          If(SumQ(u), Catch(CompoundExpression(
                              Scan(Function(If(Not(OddTrigPowerQ(Slot1, v, x)), Throw(False))), u),
                              True)), False))))),
          ISetDelayed(
              407, FunctionOfTanWeight(u_, v_, x_), If(
                  AtomQ(u), C0, If(
                      CalculusQ(u), C0, If(
                          And(TrigQ(u), IntegerQuotientQ(Part(u, C1),
                              v)),
                          If(And(SameQ(Head(u), Tan), EqQ(Part(u, C1),
                              v)), C1, If(And(SameQ(Head(u), Cot), EqQ(Part(u, C1), v)), CN1,
                                  C0)),
                          If(And(
                              PowerQ(u), EvenQ(Part(u, C2)), TrigQ(Part(u,
                                  C1)),
                              IntegerQuotientQ(Part(u, C1, C1), v)),
                              If(Or(SameQ(Head(Part(u, C1)),
                                  Tan), SameQ(Head(Part(u, C1)), Cos),
                                  SameQ(Head(Part(u, C1)), Sec)), C1, CN1),
                              If(ProductQ(u),
                                  If(Catch(CompoundExpression(Scan(
                                      Function(If(Not(FunctionOfTanQ(Slot1, v, x)), Throw(False))),
                                      u), True)),
                                      Apply(Plus,
                                          Map(Function(FunctionOfTanWeight(Slot1, v, x)),
                                              Apply(List, u))),
                                      C0),
                                  Apply(Plus,
                                      Map(Function(FunctionOfTanWeight(Slot1, v, x)),
                                          Apply(List, u))))))))),
          ISetDelayed(408, FunctionOfTrigQ(u_, v_, x_Symbol),
              If(AtomQ(u), UnsameQ(u, x),
                  If(CalculusQ(u), False,
                      If(And(TrigQ(u), IntegerQuotientQ(Part(u, C1), v)), True,
                          Catch(
                              CompoundExpression(
                                  Scan(
                                      Function(If(Not(FunctionOfTrigQ(Slot1, v, x)), Throw(False))),
                                      u),
                                  True)))))),
          ISetDelayed(409, PureFunctionOfSinhQ(u_, v_, x_),
              If(AtomQ(u), UnsameQ(u, x),
                  If(CalculusQ(u), False,
                      If(And(HyperbolicQ(u), EqQ(Part(u, C1), v)),
                          Or(SameQ(Head(u), Sinh), SameQ(Head(u), Csch)),
                          Catch(
                              CompoundExpression(Scan(
                                  Function(If(Not(PureFunctionOfSinhQ(Slot1, v, x)), Throw(False))),
                                  u), True)))))),
          ISetDelayed(410, PureFunctionOfCoshQ(u_, v_, x_), If(AtomQ(u), UnsameQ(u, x),
              If(CalculusQ(u), False, If(And(HyperbolicQ(u), EqQ(Part(u, C1), v)),
                  Or(SameQ(Head(u), Cosh), SameQ(Head(u), Sech)),
                  Catch(CompoundExpression(
                      Scan(Function(If(Not(PureFunctionOfCoshQ(Slot1, v, x)), Throw(False))), u),
                      True)))))),
          ISetDelayed(411, PureFunctionOfTanhQ(u_, v_, x_),
              If(AtomQ(u), UnsameQ(u, x),
                  If(CalculusQ(u), False,
                      If(And(HyperbolicQ(u), EqQ(Part(u, C1), v)),
                          Or(SameQ(Head(u), Tanh), SameQ(Head(u), Coth)),
                          Catch(
                              CompoundExpression(
                                  Scan(
                                      Function(
                                          If(Not(PureFunctionOfTanhQ(Slot1, v, x)), Throw(False))),
                                      u),
                                  True)))))),
          ISetDelayed(412, PureFunctionOfCothQ(u_, v_, x_), If(AtomQ(u), UnsameQ(u, x), If(
              CalculusQ(u), False,
              If(And(HyperbolicQ(u), EqQ(Part(u, C1), v)), SameQ(Head(u), Coth),
                  Catch(CompoundExpression(
                      Scan(Function(If(Not(PureFunctionOfCothQ(Slot1, v, x)), Throw(False))), u),
                      True)))))),
          ISetDelayed(
              413, FunctionOfSinhQ(u_, v_, x_), If(
                  AtomQ(u), UnsameQ(u, x), If(
                      CalculusQ(u), False, If(
                          And(HyperbolicQ(u), IntegerQuotientQ(Part(u, C1), v)),
                          If(OddQuotientQ(Part(u, C1), v),
                              Or(SameQ(Head(u),
                                  Sinh),
                                  SameQ(Head(u), Csch)),
                              Or(SameQ(Head(u), Cosh), SameQ(Head(u), Sech))),
                          If(And(IntegerPowerQ(u), HyperbolicQ(Part(u, C1)),
                              IntegerQuotientQ(Part(u, C1, C1), v)),
                              If(EvenQ(Part(u, C2)), True, FunctionOfSinhQ(Part(u, C1), v, x)), If(
                                  ProductQ(u), If(
                                      And(SameQ(Head(Part(u, C1)), Cosh),
                                          SameQ(Head(Part(u, C2)), Sinh),
                                          EqQ(Part(u, C1, C1), Times(C1D2, v)),
                                          EqQ(Part(u, C2, C1), Times(C1D2, v))),
                                      FunctionOfSinhQ(Drop(u,
                                          C2), v, x),
                                      Module(
                                          List($s(
                                              "lst")),
                                          CompoundExpression(
                                              Set($s("lst"), FindTrigFactor(Sinh, Csch, u, v,
                                                  False)),
                                              If(And(ListQ($s("lst")),
                                                  EvenQuotientQ(Part($s("lst"), C1), v)),
                                                  FunctionOfSinhQ(Times(Cosh(v), Part($s("lst"),
                                                      C2)), v, x),
                                                  CompoundExpression(
                                                      Set($s("lst"), FindTrigFactor(Cosh, Sech, u,
                                                          v, False)),
                                                      If(And(ListQ($s("lst")),
                                                          OddQuotientQ(Part($s("lst"), C1), v)),
                                                          FunctionOfSinhQ(
                                                              Times(Cosh(v), Part($s("lst"), C2)),
                                                              v, x),
                                                          CompoundExpression(Set($s("lst"),
                                                              FindTrigFactor(Tanh, Coth, u, v,
                                                                  True)),
                                                              If(ListQ($s("lst")),
                                                                  FunctionOfSinhQ(Times(Cosh(v),
                                                                      Part($s("lst"), C2)), v, x),
                                                                  Catch(
                                                                      CompoundExpression(
                                                                          Scan(Function(If(
                                                                              Not(FunctionOfSinhQ(
                                                                                  Slot1, v, x)),
                                                                              Throw(False))), u),
                                                                          True)))))))))),
                                  Catch(CompoundExpression(Scan(
                                      Function(If(Not(FunctionOfSinhQ(Slot1, v, x)), Throw(False))),
                                      u), True)))))))),
          ISetDelayed(414, FunctionOfCoshQ(u_, v_, x_),
              If(AtomQ(u), UnsameQ(u, x),
                  If(CalculusQ(u), False,
                      If(And(HyperbolicQ(u), IntegerQuotientQ(Part(u, C1), v)),
                          Or(SameQ(Head(u), Cosh), SameQ(Head(u),
                              Sech)),
                          If(And(
                              IntegerPowerQ(u), HyperbolicQ(Part(u,
                                  C1)),
                              IntegerQuotientQ(Part(u, C1, C1), v)),
                              If(EvenQ(Part(u,
                                  C2)), True, FunctionOfCoshQ(Part(u, C1), v,
                                      x)),
                              If(ProductQ(u),
                                  Module(
                                      List($s(
                                          "lst")),
                                      CompoundExpression(
                                          Set($s("lst"), FindTrigFactor(Sinh, Csch, u, v,
                                              False)),
                                          If(ListQ($s("lst")),
                                              FunctionOfCoshQ(Times(Sinh(v),
                                                  Part($s("lst"), C2)), v, x),
                                              CompoundExpression(
                                                  Set($s("lst"), FindTrigFactor(Tanh, Coth, u, v,
                                                      True)),
                                                  If(ListQ($s("lst")),
                                                      FunctionOfCoshQ(
                                                          Times(Sinh(v),
                                                              Part($s("lst"), C2)),
                                                          v, x),
                                                      Catch(CompoundExpression(Scan(Function(
                                                          If(Not(FunctionOfCoshQ(Slot1, v, x)),
                                                              Throw(False))),
                                                          u), True))))))),
                                  Catch(CompoundExpression(Scan(
                                      Function(If(Not(FunctionOfCoshQ(Slot1, v, x)), Throw(False))),
                                      u), True)))))))),
          ISetDelayed(
              415, FunctionOfTanhQ(u_, v_, x_), If(
                  AtomQ(u), UnsameQ(u, x), If(
                      CalculusQ(u), False, If(
                          And(HyperbolicQ(u), IntegerQuotientQ(Part(u, C1),
                              v)),
                          Or(SameQ(Head(u), Tanh), SameQ(Head(
                              u), Coth), EvenQuotientQ(Part(u, C1),
                                  v)),
                          If(And(PowerQ(u), EvenQ(Part(u, C2)), HyperbolicQ(Part(u, C1)),
                              IntegerQuotientQ(Part(u, C1, C1), v)), True,
                              If(And(PowerQ(u), EvenQ(Part(u, C2)), SumQ(Part(u, C1))),
                                  FunctionOfTanhQ(Expand(Sqr(Part(u, C1))), v, x),
                                  If(ProductQ(u),
                                      With(
                                          List(Set($s("lst"),
                                              ReapList(Scan(
                                                  Function(If(Not(FunctionOfTanhQ(Slot1, v, x)),
                                                      Sow(Slot1))),
                                                  u)))),
                                          If(SameQ($s("lst"), List()), True,
                                              And(Equal(Length($s("lst")), C2),
                                                  OddHyperbolicPowerQ(Part($s("lst"),
                                                      C1), v, x),
                                                  OddHyperbolicPowerQ(Part($s("lst"), C2), v, x)))),
                                      Catch(CompoundExpression(Scan(
                                          Function(
                                              If(Not(FunctionOfTanhQ(Slot1, v, x)), Throw(False))),
                                          u), True))))))))));
}
