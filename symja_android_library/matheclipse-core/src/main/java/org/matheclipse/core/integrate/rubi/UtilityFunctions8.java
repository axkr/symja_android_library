package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;

/**
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class UtilityFunctions8 {
  public static IAST RULES = List(
      ISetDelayed(102, Coeff($p("expr"), $p("form")),
          Coefficient(Together($s("expr")), $s("form"))),
      ISetDelayed(103, Coeff($p("expr"), $p("form"), n_), With(
          List(Set($s("coef1"), Coefficient($s("expr"), $s("form"), n)),
              Set($s("coef2"), Coefficient(Together($s("expr")), $s("form"), n))),
          If(SameQ(Simplify(Subtract($s("coef1"), $s("coef2"))), C0), $s("coef1"), $s("coef2")))),
      ISetDelayed(104, LeadTerm(u_), If(SumQ(u), First(u), u)),
      ISetDelayed(105, RemainingTerms(u_), If(SumQ(u), Rest(u), C0)),
      ISetDelayed(106, LeadFactor(u_),
          If(ProductQ(u), LeadFactor(First(u)),
              If(And(ComplexNumberQ(u), SameQ(Re(u), C0)),
                  If(SameQ(Im(u), C1), u, LeadFactor(Im(u))), u))),
      ISetDelayed(107, RemainingFactors(u_),
          If(ProductQ(u), Times(RemainingFactors(First(u)), Rest(u)),
              If(And(ComplexNumberQ(u), SameQ(Re(u), C0)),
                  If(SameQ(Im(u), C1), C1, Times(CI, RemainingFactors(Im(u)))), C1))),
      ISetDelayed(108, LeadBase(u_),
          With(List(Set(v, LeadFactor(u))), If(PowerQ(v), Part(v, C1), v))),
      ISetDelayed(109, LeadDegree(u_),
          With(List(Set(v, LeadFactor(u))), If(PowerQ(v), Part(v, C2), C1))),
      ISetDelayed(110, $($s("§numer"), Power($p(m, Integer),
          $p(n, Rational))), Condition(C1,
              Less(n, C0))),
      ISetDelayed(111, $($s("§numer"), Times(u_, v_)),
          Times($($s("§numer"), u), $($s("§numer"), v))),
      ISetDelayed(112, $($s("§numer"), u_), Numerator(u)), ISetDelayed(113,
          $($s("§denom"), Power($p(m, Integer),
              $p(n, Rational))),
          Condition(Power(m, Negate(n)), Less(n, C0))),
      ISetDelayed(114, $($s("§denom"), Times(u_, v_)), Times($($s(
          "§denom"), u), $($s("§denom"),
              v))),
      ISetDelayed(115, $($s(
          "§denom"), u_), Denominator(
              u)),
      ISetDelayed(116, LinearQ(u_, x_Symbol),
          If(ListQ(u),
              Catch(
                  CompoundExpression(Scan(Function(If(PolyQ(Slot1, x, C1), Null, Throw(False))), u),
                      True)),
              PolyQ(u, x, C1))),
      ISetDelayed(
          117, QuadraticProductQ(u_, x_Symbol), And(
              ProductQ(NonfreeFactors(u,
                  x)),
              Catch(
                  CompoundExpression(
                      Scan(Function(If(MatchQ(Slot1,
                          Condition(Power($p("§pm"), m_DEFAULT),
                              And(PolyQ($s("§pm"), x), LessEqual(Expon($s("§pm"), x), C2),
                                  IntegerQ(m)))),
                          Null, Throw(False))), NonfreeFactors(u, x)),
                      True)))),
      ISetDelayed(
          118, PowerOfLinearQ(Power(u_, m_DEFAULT), x_Symbol),
          And(FreeQ(m, x), PolynomialQ(u, x),
              If(IntegerQ(m),
                  MatchQ(FactorSquareFree(u),
                      Condition(Power(w_, n_DEFAULT), And(FreeQ(n, x), LinearQ(w, x)))),
                  LinearQ(u, x)))),
      ISetDelayed(119, QuadraticQ(u_, x_Symbol),
          If(ListQ(u),
              Catch(CompoundExpression(
                  Scan(Function(If(Not(QuadraticQ(Slot1, x)), Throw(False))), u), True)),
              And(PolyQ(u, x, C2),
                  Not(And(SameQ(Coefficient(u, x, C0), C0), SameQ(Coefficient(u, x, C1), C0)))))),
      ISetDelayed(
          120, LinearPairQ(u_, v_, x_Symbol),
          And(LinearQ(u, x), LinearQ(v, x), NeQ(u, x),
              EqQ(Subtract(Times(Coefficient(u, x, C0), Coefficient(v, x, C1)),
                  Times(Coefficient(u, x, C1), Coefficient(v, x, C0))), C0))),
      ISetDelayed(121, MonomialQ(u_, x_Symbol),
          If(ListQ(u),
              Catch(CompoundExpression(
                  Scan(Function(If(MonomialQ(Slot1, x), Null, Throw(False))), u), True)),
              MatchQ(u, Condition(Times(a_DEFAULT, Power(x, n_DEFAULT)), FreeQ(List(a, n), x))))));
}
