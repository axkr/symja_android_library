package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;

/**
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class UtilityFunctions24 {
  public static IAST RULES = List(
      ISetDelayed(
          416, OddHyperbolicPowerQ(u_, v_, x_), If(MemberQ(List(Sinh, Cosh, Sech, Csch), Head(u)),
              OddQuotientQ(Part(u,
                  C1), v),
              If(PowerQ(u), And(OddQ(Part(u,
                  C2)), OddHyperbolicPowerQ(Part(u, C1), v,
                      x)),
                  If(ProductQ(u),
                      If(NeQ(FreeFactors(u, x), C1),
                          OddHyperbolicPowerQ(NonfreeFactors(u, x), v, x),
                          With(
                              List(
                                  Set($s("lst"),
                                      ReapList(Scan(
                                          Function(
                                              If(Not(FunctionOfTanhQ(Slot1, v, x)), Sow(Slot1))),
                                          u)))),
                              If(SameQ($s("lst"), List()), True,
                                  And(Equal(Length($s("lst")), C1),
                                      OddHyperbolicPowerQ(Part($s("lst"), C1), v, x))))),
                      If(SumQ(u),
                          Catch(CompoundExpression(Scan(
                              Function(If(Not(OddHyperbolicPowerQ(Slot1, v, x)), Throw(False))), u),
                              True)),
                          False))))),
      ISetDelayed(
          417, FunctionOfTanhWeight(u_, v_, x_), If(
              AtomQ(u), C0, If(
                  CalculusQ(u), C0, If(
                      And(HyperbolicQ(u), IntegerQuotientQ(Part(u, C1),
                          v)),
                      If(And(SameQ(Head(
                          u), Tanh), EqQ(Part(u, C1),
                              v)),
                          C1, If(And(SameQ(Head(u), Coth), EqQ(Part(u, C1), v)), CN1, C0)),
                      If(And(
                          PowerQ(u), EvenQ(Part(u, C2)), HyperbolicQ(Part(u,
                              C1)),
                          IntegerQuotientQ(Part(u, C1, C1), v)),
                          If(Or(SameQ(Head(Part(u, C1)), Tanh), SameQ(Head(Part(u, C1)), Cosh),
                              SameQ(Head(Part(u, C1)), Sech)), C1, CN1),
                          If(ProductQ(u),
                              If(Catch(CompoundExpression(Scan(
                                  Function(If(Not(FunctionOfTanhQ(Slot1, v, x)), Throw(False))), u),
                                  True)),
                                  Apply(Plus,
                                      Map(Function(FunctionOfTanhWeight(Slot1, v, x)),
                                          Apply(List, u))),
                                  C0),
                              Apply(Plus,
                                  Map(Function(FunctionOfTanhWeight(Slot1, v, x)),
                                      Apply(List, u))))))))),
      ISetDelayed(418, FunctionOfHyperbolicQ(u_, v_, x_Symbol),
          If(AtomQ(u), UnsameQ(u, x),
              If(CalculusQ(u), False, If(And(HyperbolicQ(u), IntegerQuotientQ(Part(u, C1), v)),
                  True, Catch(CompoundExpression(
                      Scan(Function(If(FunctionOfHyperbolicQ(Slot1, v, x), Null, Throw(False))), u),
                      True)))))),
      ISetDelayed(419, FindTrigFactor($p("func1"), $p("func2"), u_, v_, $p("flag")),
          If(SameQ(u, C1), False, If(
              And(Or(SameQ(Head(LeadBase(u)), $s("func1")), SameQ(Head(LeadBase(u)), $s("func2"))),
                  OddQ(LeadDegree(u)), IntegerQuotientQ(Part(LeadBase(u), C1), v),
                  Or($s("flag"), NeQ(Part(LeadBase(u), C1), v))),
              List(Part(LeadBase(u), C1), RemainingFactors(u)),
              With(
                  List(Set($s("lst"),
                      FindTrigFactor($s("func1"), $s("func2"), RemainingFactors(u), v,
                          $s("flag")))),
                  If(AtomQ($s("lst")), False,
                      List(Part($s("lst"), C1), Times(LeadFactor(u), Part($s("lst"), C2)))))))),
      ISetDelayed(420, IntegerQuotientQ(u_, v_), IntegerQ(Simplify(Times(u, Power(v, CN1))))),
      ISetDelayed(421, OddQuotientQ(u_, v_), OddQ(Simplify(Times(u, Power(v, CN1))))),
      ISetDelayed(422, EvenQuotientQ(u_, v_), EvenQ(Simplify(Times(u, Power(v, CN1))))),
      ISetDelayed(423, FunctionOfDensePolynomialsQ(u_, x_Symbol),
          If(FreeQ(u, x), True, If(PolynomialQ(u, x), Greater(Length(Exponent(u, x, List)), C1),
              Catch(CompoundExpression(
                  Scan(Function(If(FunctionOfDensePolynomialsQ(Slot1, x), Null, Throw(False))), u),
                  True))))),
      ISetDelayed(424, FunctionOfLog(u_, x_Symbol),
          With(List(Set($s("lst"), FunctionOfLog(u, False, False, x))),
              If(Or(AtomQ($s("lst")), FalseQ(Part($s("lst"), C2))), False, $s("lst")))),
      ISetDelayed(425, FunctionOfLog(u_, v_, n_, x_),
          If(AtomQ(u), If(SameQ(u, x), False, List(u, v, n)),
              If(CalculusQ(u), False,
                  Module(List($s("lst")),
                      If(And(LogQ(u), ListQ(Set($s("lst"), BinomialParts(Part(u, C1), x))),
                          EqQ(Part($s("lst"), C1), C0)),
                          If(Or(FalseQ(v), SameQ(Part(u, C1), v)),
                              List(x, Part(u, C1), Part($s("lst"), C3)), False),
                          CompoundExpression(Set($s("lst"), List(C0, v, n)),
                              Catch(List(
                                  Map(Function(CompoundExpression(
                                      Set($s("lst"),
                                          FunctionOfLog(Slot1, Part($s("lst"), C2),
                                              Part($s("lst"), C3), x)),
                                      If(AtomQ($s("lst")), Throw(False), Part($s("lst"), C1)))), u),
                                  Part($s("lst"), C2), Part($s("lst"), C3))))))))),
      ISetDelayed(426, PowerVariableExpn(u_, m_, x_Symbol),
          If(IntegerQ(m),
              With(List(Set($s("lst"), PowerVariableDegree(u, m, C1, x))),
                  If(AtomQ($s("lst")), False,
                      List(
                          Times(Power(x, Times(m, Power(Part($s("lst"), C1), CN1))),
                              PowerVariableSubst(u, Part($s("lst"), C1), x)),
                          Part($s("lst"), C1), Part($s("lst"), C2)))),
              False)),
      ISetDelayed(427, PowerVariableDegree(u_, m_, c_, x_Symbol),
          If(FreeQ(u, x), List(m, c), If(Or(AtomQ(u), CalculusQ(u)), False,
              If(And(PowerQ(u), FreeQ(Times(Part(u, C1), Power(x, CN1)), x)),
                  If(Or(EqQ(m, C0),
                      And(SameQ(m, Part(u, C2)), SameQ(c, Times(Part(u, C1), Power(x, CN1))))),
                      List(Part(u, C2), Times(Part(u, C1), Power(x, CN1))),
                      If(And(IntegerQ(Part(u, C2)), IntegerQ(m), Greater(GCD(m, Part(u, C2)), C1),
                          SameQ(c, Times(Part(u, C1), Power(x, CN1)))),
                          List(GCD(m, Part(u, C2)), c), False)),
                  Catch(
                      Module(List(Set($s("lst"), List(m, c))),
                          CompoundExpression(Scan(Function(CompoundExpression(
                              Set($s("lst"),
                                  PowerVariableDegree(Slot1, Part($s("lst"), C1),
                                      Part($s("lst"), C2), x)),
                              If(AtomQ($s("lst")), Throw(False)))), u), $s("lst")))))))),
      ISetDelayed(428, PowerVariableSubst(u_, m_, x_Symbol),
          If(Or(FreeQ(u, x), AtomQ(u), CalculusQ(u)), u,
              If(And(PowerQ(u), FreeQ(Times(Part(u, C1), Power(x, CN1)), x)),
                  Power(x, Times(Part(u, C2), Power(m, CN1))),
                  Map(Function(PowerVariableSubst(Slot1, m, x)), u)))),
      ISetDelayed(429,
          EulerIntegrandQ(Power(Plus(Times(b_DEFAULT, Power(u_, n_)), Times(a_DEFAULT, x_)), p_),
              x_Symbol),
          Condition(True,
              And(FreeQ(List(a, b), x), IntegerQ(Plus(n, C1D2)), QuadraticQ(u, x),
                  Or(Not(RationalQ(p)), And(ILtQ(p, C0), Not(BinomialQ(u, x))))))),
      ISetDelayed(430,
          EulerIntegrandQ(
              Times(Power(v_, m_DEFAULT),
                  Power(Plus(Times(b_DEFAULT, Power(u_, n_)), Times(a_DEFAULT, x_)), p_)),
              x_Symbol),
          Condition(True,
              And(FreeQ(List(a, b), x), EqQ(u, v), IntegersQ(Times(C2, m), Plus(n, C1D2)),
                  QuadraticQ(u, x),
                  Or(Not(RationalQ(p)), And(ILtQ(p, C0), Not(BinomialQ(u, x))))))),
      ISetDelayed(431,
          EulerIntegrandQ(
              Times(Power(v_, m_DEFAULT),
                  Power(Plus(Times(b_DEFAULT, Power(u_, n_)), Times(a_DEFAULT, x_)), p_)),
              x_Symbol),
          Condition(True,
              And(FreeQ(List(a, b), x), EqQ(u, v), IntegersQ(Times(C2, m), Plus(n, C1D2)),
                  QuadraticQ(u, x),
                  Or(Not(RationalQ(p)), And(ILtQ(p, C0), Not(BinomialQ(u, x))))))),
      ISetDelayed(432, EulerIntegrandQ(Times(Power(u_, n_), Power(v_, p_)), x_Symbol),
          Condition(True,
              And(ILtQ(p, C0), IntegerQ(Plus(n, C1D2)), QuadraticQ(u, x), QuadraticQ(v, x),
                  Not(BinomialQ(v, x))))),
      ISetDelayed(433, EulerIntegrandQ(u_, x_Symbol), False),
      ISetDelayed(434, FunctionOfSquareRootOfQuadratic(u_, x_Symbol), If(
          MatchQ(u,
              Condition(Times(Power(x, m_DEFAULT),
                  Power(Plus(a_, Times(b_DEFAULT, Power(x, n_DEFAULT))), p_)),
                  FreeQ(List(a, b, m, n, p), x))),
          False,
          Module(List(Set($s("tmp"), FunctionOfSquareRootOfQuadratic(u, False, x))), If(
              Or(AtomQ($s("tmp")), FalseQ(Part($s("tmp"), C1))), False,
              CompoundExpression(Set($s("tmp"), Part($s("tmp"), C1)), Module(
                  List(Set(a, Coefficient($s("tmp"), x, C0)), Set(b, Coefficient($s("tmp"), x, C1)),
                      Set(c, Coefficient($s("tmp"), x, C2)), $s("§sqrt"), q, r),
                  If(Or(And(EqQ(a, C0), EqQ(b, C0)), EqQ(Subtract(Sqr(b), Times(C4, a, c)), C0)),
                      False,
                      If(PosQ(c),
                          CompoundExpression(
                              Set($s("§sqrt"), Rt(c, C2)), Set(
                                  q,
                                  Plus(
                                      Times(a, $s("§sqrt")), Times(b, x),
                                      Times($s("§sqrt"), Sqr(x)))),
                              Set(r, Plus(b, Times(C2, $s("§sqrt"), x))),
                              List(Simplify(Times(
                                  SquareRootOfQuadraticSubst(u, Times(q, Power(r, CN1)),
                                      Times(Plus(Negate(a), Sqr(x)), Power(r, CN1)), x),
                                  q, Power(r, CN2))), Simplify(
                                      Plus(Times($s("§sqrt"), x), Sqrt($s("tmp")))),
                                  C2)),
                          If(PosQ(a),
                              CompoundExpression(
                                  Set($s("§sqrt"), Rt(a,
                                      C2)),
                                  Set(q,
                                      Plus(
                                          Times(c, $s(
                                              "§sqrt")),
                                          Times(CN1, b, x), Times($s("§sqrt"), Sqr(x)))),
                                  Set(r, Subtract(c, Sqr(x))),
                                  List(
                                      Simplify(Times(
                                          SquareRootOfQuadraticSubst(u, Times(q, Power(r, CN1)),
                                              Times(Plus(Negate(b), Times(C2, $s("§sqrt"), x)),
                                                  Power(r, CN1)),
                                              x),
                                          q, Power(r, CN2))),
                                      Simplify(Times(Plus(Negate($s("§sqrt")), Sqrt($s("tmp"))),
                                          Power(x, CN1))),
                                      C1)),
                              CompoundExpression(
                                  Set($s("§sqrt"), Rt(Subtract(Sqr(b), Times(C4, a, c)), C2)),
                                  Set(r, Subtract(c, Sqr(x))),
                                  List(
                                      Simplify(Times(CN1, $s("§sqrt"),
                                          SquareRootOfQuadraticSubst(u,
                                              Times(CN1, $s("§sqrt"), x, Power(r, CN1)),
                                              Times(CN1,
                                                  Plus(Times(b, c), Times(c, $s("§sqrt")),
                                                      Times(Plus(Negate(b), $s("§sqrt")), Sqr(x))),
                                                  Power(Times(C2, c, r), CN1)),
                                              x),
                                          x, Power(r, CN2))),
                                      FullSimplify(Times(C2, c, Sqrt($s("tmp")),
                                          Power(Plus(b, Negate($s("§sqrt")), Times(C2, c, x)),
                                              CN1))),
                                      C3))))))))))),
      ISetDelayed(
          435, FunctionOfSquareRootOfQuadratic(u_, v_, x_Symbol), If(
              Or(AtomQ(u), FreeQ(u,
                  x)),
              List(v), If(
                  And(PowerQ(u), FreeQ(Part(u, C2),
                      x)),
                  If(And(FractionQ(Part(u, C2)), Equal(Denominator(Part(u, C2)), C2),
                      PolynomialQ(Part(u, C1), x), Equal(Exponent(Part(u, C1), x), C2)),
                      If(Or(FalseQ(v), SameQ(Part(u, C1), v)), List(
                          Part(u, C1)), False),
                      FunctionOfSquareRootOfQuadratic(Part(u, C1), v, x)),
                  If(Or(ProductQ(u), SumQ(u)),
                      Catch(
                          Module(List(Set($s("lst"), List(v))),
                              CompoundExpression(
                                  Scan(Function(CompoundExpression(
                                      Set($s("lst"),
                                          FunctionOfSquareRootOfQuadratic(Slot1,
                                              Part($s("lst"), C1), x)),
                                      If(AtomQ($s("lst")), Throw(False)))), u),
                                  $s("lst")))),
                      False)))));
}
