package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;

/**
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class UtilityFunctions6 {
  public static IAST RULES =
      List(
          ISetDelayed(62, SumSimplerQ(u_, v_),
              If(RationalQ(u, v),
                  If(Equal(v, C0), False,
                      If(Greater(v, C0), Less(u, CN1), GreaterEqual(u, Negate(v)))),
                  SumSimplerAuxQ(Expand(u), Expand(v)))),
          ISetDelayed(63, SumSimplerAuxQ(u_, v_),
              Condition(And(Or(RationalQ(First(v)), SumSimplerAuxQ(u, First(v))),
                  Or(RationalQ(Rest(v)), SumSimplerAuxQ(u, Rest(v)))), SumQ(v))),
          ISetDelayed(64, SumSimplerAuxQ(u_, v_),
              Condition(Or(SumSimplerAuxQ(First(u), v), SumSimplerAuxQ(Rest(u), v)), SumQ(u))),
          ISetDelayed(
              65, SumSimplerAuxQ(u_, v_),
              And(UnsameQ(v, C0), SameQ(NonnumericFactors(u), NonnumericFactors(v)),
                  Or(Less(Times(NumericFactor(u), Power(NumericFactor(v), CN1)), CN1D2),
                      And(Equal(Times(NumericFactor(u), Power(NumericFactor(v), CN1)), CN1D2),
                          Less(NumericFactor(u), C0))))),
          ISetDelayed(66, SimplerIntegrandQ(u_, v_, x_Symbol),
              Module(List(Set($s("lst"), CancelCommonFactors(u, v)), $s("u1"), $s("v1")),
                  CompoundExpression(Set($s("u1"), Part($s("lst"), C1)),
                      Set($s("v1"), Part($s("lst"), C2)),
                      If(Less(LeafCount($s("u1")), Times(QQ(3L, 4L), LeafCount($s("v1")))), True,
                          If(RationalFunctionQ($s("u1"), x),
                              If(RationalFunctionQ($s("v1"), x),
                                  Less(Apply(Plus, RationalFunctionExponents($s("u1"), x)),
                                      Apply(Plus, RationalFunctionExponents($s("v1"), x))),
                                  True),
                              False))))),
          ISetDelayed(67, CancelCommonFactors(u_, v_),
              If(ProductQ(u), If(ProductQ(v),
                  If(MemberQ(v, First(u)),
                      CancelCommonFactors(Rest(u), DeleteCases(v, First(u), C1, C1)),
                      $(Function(List(Times(First(u), Part(Slot1, C1)), Part(Slot1, C2))),
                          CancelCommonFactors(Rest(u), v))),
                  If(MemberQ(u, v), List(DeleteCases(u, v, C1, C1), C1), List(u, v))),
                  If(ProductQ(v),
                      If(MemberQ(v, u), List(C1, DeleteCases(v, u, C1, C1)), List(u, v)),
                      List(u, v)))),
          ISetDelayed(68, BinomialDegree(u_, x_Symbol), Part(BinomialParts(u,
              x), C3)),
          ISetDelayed(
              69, BinomialParts(u_, x_Symbol), If(
                  PolynomialQ(u, x),
                  If(Greater(Exponent(u, x), C0), With(List(Set($s("lst"), Exponent(u, x, List))),
                      If(Equal(Length($s("lst")), C1), List(C0, Coefficient(u, x, Exponent(u, x)),
                          Exponent(u, x)),
                          If(And(Equal(Length($s("lst")), C2), Equal(Part($s("lst"), C1), C0)),
                              List(Coefficient(u, x, C0), Coefficient(u, x, Exponent(u, x)),
                                  Exponent(u, x)),
                              False))),
                      False),
                  If(PowerQ(u),
                      If(And(SameQ(Part(u, C1), x), FreeQ(Part(u, C2), x)),
                          List(C0, C1, Part(u, C2)), False),
                      If(ProductQ(u), If(FreeQ(First(u), x), With(List(Set($s("lst2"),
                          BinomialParts(Rest(u), x))),
                          If(AtomQ($s("lst2")), False,
                              List(Times(First(u), Part($s("lst2"), C1)),
                                  Times(First(u), Part($s("lst2"), C2)), Part($s("lst2"), C3)))),
                          If(FreeQ(Rest(u), x), With(
                              List(Set($s("lst1"), BinomialParts(First(u), x))),
                              If(AtomQ($s("lst1")), False,
                                  List(Times(Rest(u), Part($s("lst1"), C1)),
                                      Times(Rest(u), Part($s("lst1"), C2)), Part($s("lst1"), C3)))),
                              With(List(Set($s("lst1"), BinomialParts(First(u), x))),
                                  If(AtomQ($s("lst1")), False, With(
                                      List(Set($s("lst2"), BinomialParts(Rest(u), x))),
                                      If(AtomQ($s("lst2")), False, With(
                                          List(Set(a, Part($s("lst1"), C1)), Set(b,
                                              Part($s("lst1"), C2)), Set(m, Part($s("lst1"), C3)),
                                              Set(c, Part($s("lst2"), C1)), Set(d, Part($s("lst2"),
                                                  C2)),
                                              Set(n, Part($s("lst2"), C3))),
                                          If(EqQ(a, C0), If(EqQ(c, C0),
                                              List(C0, Times(b, d), Plus(m, n)),
                                              If(EqQ(Plus(m, n), C0),
                                                  List(Times(b, d), Times(b, c), m), False)),
                                              If(EqQ(c, C0),
                                                  If(EqQ(Plus(m, n), C0),
                                                      List(Times(b, d), Times(a, d), n), False),
                                                  If(And(EqQ(m, n),
                                                      EqQ(Plus(Times(a, d), Times(b, c)), C0)),
                                                      List(Times(a, c), Times(b, d), Times(C2, m)),
                                                      False)))))))))),
                          If(SumQ(u), If(FreeQ(First(u), x), With(
                              List(Set($s("lst2"), BinomialParts(Rest(u), x))),
                              If(AtomQ($s("lst2")), False,
                                  List(Plus(First(u), Part($s("lst2"), C1)), Part($s("lst2"), C2),
                                      Part($s("lst2"), C3)))),
                              If(FreeQ(Rest(u), x),
                                  With(List(Set($s("lst1"), BinomialParts(First(u), x))), If(
                                      AtomQ($s("lst1")), False,
                                      List(Plus(Rest(u), Part($s("lst1"), C1)),
                                          Part($s("lst1"), C2), Part($s("lst1"), C3)))),
                                  With(List(Set($s("lst1"), BinomialParts(First(u), x))), If(
                                      AtomQ($s("lst1")), False,
                                      With(List(Set($s("lst2"), BinomialParts(Rest(u), x))), If(
                                          AtomQ($s("lst2")), False,
                                          If(EqQ(Part($s("lst1"), C3), Part($s("lst2"), C3)),
                                              List(Plus(Part($s("lst1"), C1), Part($s("lst2"), C1)),
                                                  Plus(Part($s("lst1"), C2), Part($s("lst2"), C2)),
                                                  Part($s("lst1"), C3)),
                                              False))))))),
                              False))))),
          ISetDelayed(70, TrinomialDegree(u_,
              x_Symbol), Part(TrinomialParts(u, x), C4)),
          ISetDelayed(71, TrinomialParts(u_, x_Symbol), If(PolynomialQ(u, x),
              With(
                  List(Set($s("lst"),
                      CoefficientList(u, x))),
                  If(Or(Less(Length($s("lst")), C3), EvenQ(Length($s("lst"))),
                      EqQ(Part($s("lst"), Times(C1D2, Plus(Length($s("lst")), C1))), C0)), False,
                      Catch(
                          CompoundExpression(
                              Scan(Function(If(EqQ(Slot1, C0), Null, Throw(False))),
                                  Drop(
                                      Drop(Drop($s("lst"),
                                          List(Times(C1D2, Plus(Length($s("lst")), C1)))), C1),
                                      CN1)),
                              List(First($s("lst")),
                                  Part($s("lst"), Times(C1D2, Plus(Length($s("lst")), C1))),
                                  Last($s("lst")),
                                  Times(C1D2, Subtract(Length($s("lst")), C1))))))),
              If(PowerQ(u), If(EqQ(Part(u, C2), C2),
                  With(List(Set($s("lst"), BinomialParts(Part(u, C1), x))),
                      If(Or(AtomQ($s("lst")), EqQ(Part($s("lst"), C1), C0)), False,
                          List(Sqr(Part($s("lst"), C1)), Times(C2, Part($s("lst"), C1),
                              Part($s("lst"), C2)), Sqr(Part($s("lst"), C2)),
                              Part($s("lst"), C3)))),
                  False),
                  If(ProductQ(u), If(
                      FreeQ(First(u), x), With(
                          List(Set($s("lst2"), TrinomialParts(Rest(u), x))),
                          If(AtomQ($s("lst2")), False,
                              List(
                                  Times(First(u), Part($s("lst2"), C1)), Times(First(u),
                                      Part($s("lst2"), C2)),
                                  Times(First(u), Part($s("lst2"), C3)), Part($s("lst2"), C4)))),
                      If(FreeQ(Rest(u), x), With(
                          List(Set($s("lst1"), TrinomialParts(First(u), x))), If(AtomQ($s("lst1")),
                              False, List(Times(Rest(u), Part($s("lst1"), C1)),
                                  Times(Rest(u), Part($s("lst1"), C2)),
                                  Times(Rest(u), Part($s("lst1"), C3)), Part($s("lst1"), C4)))),
                          With(List(Set($s("lst1"), BinomialParts(First(u), x))), If(
                              AtomQ($s("lst1")), False,
                              With(List(Set($s("lst2"), BinomialParts(Rest(u), x))),
                                  If(AtomQ($s("lst2")), False, With(List(
                                      Set(a, Part($s("lst1"), C1)), Set(b, Part($s("lst1"), C2)),
                                      Set(m, Part($s("lst1"), C3)), Set(c, Part($s("lst2"), C1)),
                                      Set(d, Part($s("lst2"), C2)), Set(n, Part($s("lst2"), C3))),
                                      If(And(EqQ(m, n), NeQ(Plus(Times(a, d), Times(b, c)), C0)),
                                          List(Times(a, c), Plus(Times(a, d), Times(b, c)),
                                              Times(b, d), m),
                                          False)))))))),
                      If(SumQ(u), If(FreeQ(First(u), x),
                          With(List(Set($s("lst2"), TrinomialParts(Rest(u), x))),
                              If(AtomQ($s("lst2")), False,
                                  List(Plus(First(u), Part($s("lst2"), C1)), Part($s("lst2"), C2),
                                      Part($s("lst2"), C3), Part($s("lst2"), C4)))),
                          If(FreeQ(Rest(u), x),
                              With(
                                  List(Set($s("lst1"),
                                      TrinomialParts(First(u), x))),
                                  If(AtomQ($s("lst1")), False,
                                      List(Plus(Rest(u), Part($s("lst1"), C1)),
                                          Part($s("lst1"), C2), Part($s("lst1"), C3),
                                          Part($s("lst1"), C4)))),
                              With(
                                  List(Set($s("lst1"),
                                      TrinomialParts(First(u), x))),
                                  If(AtomQ($s("lst1")),
                                      With(
                                          List(Set($s("lst3"),
                                              BinomialParts(First(u), x))),
                                          If(AtomQ($s("lst3")), False,
                                              With(
                                                  List(Set($s("lst2"),
                                                      TrinomialParts(Rest(u), x))),
                                                  If(AtomQ($s("lst2")), With(
                                                      List(Set(
                                                          $s("lst4"), BinomialParts(Rest(u), x))),
                                                      If(AtomQ($s("lst4")), False, If(
                                                          EqQ(Part($s("lst3"), C3),
                                                              Times(C2, Part($s("lst4"), C3))),
                                                          List(
                                                              Plus(Part($s("lst3"), C1),
                                                                  Part($s("lst4"), C1)),
                                                              Part($s("lst4"), C2),
                                                              Part($s("lst3"), C2),
                                                              Part($s("lst4"), C3)),
                                                          If(EqQ(Part($s("lst4"), C3),
                                                              Times(C2, Part($s("lst3"), C3))),
                                                              List(
                                                                  Plus(Part($s("lst3"), C1),
                                                                      Part($s("lst4"), C1)),
                                                                  Part($s("lst3"), C2),
                                                                  Part($s("lst4"), C2),
                                                                  Part($s("lst3"), C3)),
                                                              False)))),
                                                      If(And(
                                                          EqQ(Part($s("lst3"), C3), Part($s("lst2"),
                                                              C4)),
                                                          NeQ(Plus(Part($s("lst3"), C2),
                                                              Part($s("lst2"), C2)), C0)),
                                                          List(
                                                              Plus(
                                                                  Part($s("lst3"), C1),
                                                                  Part($s("lst2"), C1)),
                                                              Plus(Part($s("lst3"), C2),
                                                                  Part($s("lst2"), C2)),
                                                              Part($s("lst2"), C3),
                                                              Part($s("lst2"), C4)),
                                                          If(And(
                                                              EqQ(Part($s("lst3"), C3),
                                                                  Times(C2, Part($s("lst2"), C4))),
                                                              NeQ(Plus(Part($s("lst3"), C2),
                                                                  Part($s("lst2"), C3)), C0)),
                                                              List(
                                                                  Plus(Part($s("lst3"), C1),
                                                                      Part($s("lst2"), C1)),
                                                                  Part($s("lst2"), C2),
                                                                  Plus(Part($s("lst3"), C2),
                                                                      Part($s("lst2"), C3)),
                                                                  Part($s("lst2"), C4)),
                                                              False)))))),
                                      With(List(Set($s("lst2"), TrinomialParts(Rest(u), x))), If(
                                          AtomQ($s(
                                              "lst2")),
                                          With(
                                              List(Set($s("lst4"), BinomialParts(Rest(u), x))), If(
                                                  AtomQ($s("lst4")), False, If(
                                                      And(EqQ(Part($s("lst4"), C3),
                                                          Part($s("lst1"), C4)),
                                                          NeQ(Plus(Part($s("lst1"), C2),
                                                              Part($s("lst4"), C2)), C0)),
                                                      List(
                                                          Plus(
                                                              Part($s("lst1"), C1),
                                                              Part($s("lst4"), C1)),
                                                          Plus(
                                                              Part($s("lst1"), C2),
                                                              Part($s("lst4"), C2)),
                                                          Part($s("lst1"), C3),
                                                          Part($s("lst1"), C4)),
                                                      If(And(
                                                          EqQ(Part($s("lst4"), C3),
                                                              Times(C2, Part($s("lst1"), C4))),
                                                          NeQ(Plus(Part($s("lst1"), C3),
                                                              Part($s("lst4"), C2)), C0)),
                                                          List(
                                                              Plus(Part($s("lst1"), C1),
                                                                  Part($s("lst4"), C1)),
                                                              Part($s("lst1"), C2),
                                                              Plus(Part($s("lst1"), C3),
                                                                  Part($s("lst4"), C2)),
                                                              Part($s("lst1"), C4)),
                                                          False)))),
                                          If(And(EqQ(Part($s("lst1"), C4), Part($s("lst2"), C4)),
                                              NeQ(Plus(Part($s("lst1"), C2), Part($s("lst2"), C2)),
                                                  C0),
                                              NeQ(Plus(Part($s("lst1"), C3), Part($s("lst2"), C3)),
                                                  C0)),
                                              List(Plus(Part($s("lst1"), C1), Part($s("lst2"), C1)),
                                                  Plus(Part($s("lst1"), C2), Part($s("lst2"), C2)),
                                                  Plus(Part($s("lst1"), C3), Part($s("lst2"), C3)),
                                                  Part($s("lst1"), C4)),
                                              False))))))),
                          False))))),
          ISetDelayed(72, GeneralizedBinomialDegree(u_, x_Symbol),
              $(Function(Subtract(Part(Slot1, C3), Part(Slot1, C4))),
                  GeneralizedBinomialParts(u, x))),
          ISetDelayed(73,
              GeneralizedBinomialParts(
                  Plus(
                      Times(b_DEFAULT, Power(x_, n_DEFAULT)), Times(a_DEFAULT,
                          Power(x_, q_DEFAULT))),
                  x_Symbol),
              Condition(List(a, b, n, q), And(FreeQ(List(a, b, n, q), x), PosQ(Subtract(n, q))))),
          ISetDelayed(
              74, GeneralizedBinomialParts(Times(a_,
                  u_), x_Symbol),
              Condition(
                  With(List(Set($s("lst"), GeneralizedBinomialParts(u, x))),
                      Condition(
                          List(Times(a, Part($s("lst"), C1)), Times(a, Part($s("lst"), C2)),
                              Part($s("lst"), C3), Part($s("lst"), C4)),
                          ListQ($s("lst")))),
                  FreeQ(a, x))),
          ISetDelayed(
              75, GeneralizedBinomialParts(Times(u_,
                  Power(x_, m_DEFAULT)), x_Symbol),
              Condition(
                  With(
                      List(Set($s("lst"), GeneralizedBinomialParts(u, x))),
                      Condition(
                          List(Part($s("lst"), C1), Part($s("lst"), C2),
                              Plus(m, Part($s("lst"), C3)), Plus(m, Part($s("lst"), C4))),
                          And(ListQ($s("lst")), NeQ(Plus(m, Part($s("lst"), C3)), C0),
                              NeQ(Plus(m, Part($s("lst"), C4)), C0)))),
                  FreeQ(m, x))),
          ISetDelayed(76, GeneralizedBinomialParts(Times(u_, Power(x_, m_DEFAULT)), x_Symbol),
              Condition(With(List(Set($s("lst"), BinomialParts(u, x))),
                  Condition(List(Part($s("lst"), C1), Part($s("lst"), C2),
                      Plus(m, Part($s("lst"), C3)), m),
                      And(ListQ($s("lst")), NeQ(Plus(m, Part($s("lst"), C3)), C0)))),
                  FreeQ(m, x))),
          ISetDelayed(77, GeneralizedBinomialParts(u_,
              x_Symbol), False),
          ISetDelayed(78, GeneralizedTrinomialDegree(u_, x_Symbol),
              $(Function(Subtract(Part(Slot1, C4), Part(Slot1, C5))),
                  GeneralizedTrinomialParts(u, x))),
          ISetDelayed(79,
              GeneralizedTrinomialParts(
                  Plus(Times(b_DEFAULT, Power(x_, n_DEFAULT)),
                      Times(a_DEFAULT, Power(x_, q_DEFAULT)), Times(c_DEFAULT,
                          Power(x_, r_DEFAULT))),
                  x_Symbol),
              Condition(
                  List(a, b, c, n, q), And(FreeQ(List(a, b, c, n, q), x),
                      EqQ(r, Subtract(Times(C2, n), q))))),
          ISetDelayed(
              80, GeneralizedTrinomialParts(Times(a_,
                  u_), x_Symbol),
              Condition(
                  With(
                      List(Set($s("lst"),
                          GeneralizedTrinomialParts(u, x))),
                      Condition(
                          List(Times(a, Part($s("lst"), C1)), Times(a, Part($s("lst"), C2)),
                              Times(a, Part($s("lst"), C3)), Part($s("lst"), C4), Part($s("lst"),
                                  C5)),
                          ListQ($s("lst")))),
                  FreeQ(a, x))),
          ISetDelayed(81, GeneralizedTrinomialParts(u_, x_Symbol),
              Condition(With(List(Set($s("lst"), Expon(u, x, List))), If(
                  And(Equal(Length($s("lst")), C3), NeQ(Part($s("lst"), C0), C0),
                      EqQ(Part($s("lst"), C3),
                          Subtract(Times(C2, Part($s("lst"), C2)), Part($s("lst"), C1)))),
                  List(Coeff(u, x, Part($s("lst"), C1)), Coeff(u, x, Part($s("lst"), C2)),
                      Coeff(u, x, Part($s("lst"), C3)), Part($s("lst"), C2), Part($s("lst"), C1)),
                  False)), PolyQ(u, x))));
}
