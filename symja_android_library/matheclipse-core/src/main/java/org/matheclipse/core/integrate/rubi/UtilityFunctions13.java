package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class UtilityFunctions13 { 
  public static IAST RULES = List( 
ISetDelayed(133,PseudoBinomialParts(u_,x_Symbol),
    If(And(PolynomialQ(u,x),Greater(Expon(u,x),C2)),Module(List(a,c,d,n),CompoundExpression(Set(n,Expon(u,x)),Set(d,Rt(Coefficient(u,x,n),n)),Set(c,Times(Coefficient(u,x,Subtract(n,C1)),Power(Times(n,Power(d,Subtract(n,C1))),CN1))),Set(a,Simplify(Subtract(u,Power(Plus(c,Times(d,x)),n)))),If(And(NeQ(a,C0),FreeQ(a,x)),List(a,C1,c,d,n),False))),False)),
ISetDelayed(134,PerfectPowerTest(u_,x_Symbol),
    If(PolynomialQ(u,x),Module(list(Set($s("lst"),FactorSquareFreeList(u)),Set($s("§gcd"),C0),Set(v,C1)),CompoundExpression(If(SameQ(Part($s("lst"),C1),list(C1,C1)),Set($s("lst"),Rest($s("lst")))),Scan(Function(Set($s("§gcd"),GCD($s("§gcd"),Part(Slot1,C2)))),$s("lst")),If(Greater($s("§gcd"),C1),CompoundExpression(Scan(Function(Set(v,Times(v,Power(Part(Slot1,C1),Times(Part(Slot1,C2),Power($s("§gcd"),CN1)))))),$s("lst")),Power(Expand(v),$s("§gcd"))),False))),False)),
ISetDelayed(135,RationalFunctionQ(u_,x_Symbol),
    If(Or(AtomQ(u),FreeQ(u,x)),True,If(IntegerPowerQ(u),RationalFunctionQ(Part(u,C1),x),If(Or(ProductQ(u),SumQ(u)),Catch(CompoundExpression(Scan(Function(If(Not(RationalFunctionQ(Slot1,x)),Throw(False))),u),True)),False)))),
ISetDelayed(136,RationalFunctionFactors(u_,x_Symbol),
    If(ProductQ(u),Map(Function(If(RationalFunctionQ(Slot1,x),Slot1,C1)),u),If(RationalFunctionQ(u,x),u,C1))),
ISetDelayed(137,NonrationalFunctionFactors(u_,x_Symbol),
    If(ProductQ(u),Map(Function(If(RationalFunctionQ(Slot1,x),C1,Slot1)),u),If(RationalFunctionQ(u,x),C1,u))),
ISetDelayed(138,RationalFunctionExponents(u_,x_Symbol),
    If(PolynomialQ(u,x),list(Exponent(u,x),C0),If(IntegerPowerQ(u),If(Greater(Part(u,C2),C0),Times(Part(u,C2),RationalFunctionExponents(Part(u,C1),x)),Times(CN1,Part(u,C2),Reverse(RationalFunctionExponents(Part(u,C1),x)))),If(ProductQ(u),Plus(RationalFunctionExponents(First(u),x),RationalFunctionExponents(Rest(u),x)),If(SumQ(u),With(list(Set(v,Together(u))),If(SumQ(v),Module(list($s("lst1"),$s("lst2")),CompoundExpression(Set($s("lst1"),RationalFunctionExponents(First(u),x)),Set($s("lst2"),RationalFunctionExponents(Rest(u),x)),list(Max(Plus(Part($s("lst1"),C1),Part($s("lst2"),C2)),Plus(Part($s("lst2"),C1),Part($s("lst1"),C2))),Plus(Part($s("lst1"),C2),Part($s("lst2"),C2))))),RationalFunctionExponents(v,x))),list(C0,C0)))))),
ISetDelayed(139,RationalFunctionExpand(Times(u_,Power(v_,n_)),x_Symbol),
    Condition(With(list(Set(w,RationalFunctionExpand(u,x))),If(SumQ(w),Map(Function(Times(Slot1,Power(v,n))),w),Times(w,Power(v,n)))),And(FractionQ(n),UnsameQ(v,x)))),
ISetDelayed(140,RationalFunctionExpand(u_,x_Symbol),
    Module(list(v,w),CompoundExpression(Set(v,ExpandIntegrand(u,x)),If(And(UnsameQ(v,u),Not(MatchQ(u,Condition(Times(Power(x,m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,x)),p_),Power(Plus(a_,Times(b_DEFAULT,Power(x,n_))),CN1)),And(FreeQ(List(a,b,c,d,p),x),IntegersQ(m,n),Equal(m,Subtract(n,C1))))))),v,CompoundExpression(Set(v,ExpandIntegrand(RationalFunctionFactors(u,x),x)),Set(w,NonrationalFunctionFactors(u,x)),If(SumQ(v),Map(Function(Times(Slot1,w)),v),Times(v,w))))))),
ISetDelayed(141,PolyGCD(u_,v_,x_Symbol),
    NonfreeFactors(PolynomialGCD(u,v),x)),
ISetDelayed(142,AlgebraicFunctionQ(u_,x_Symbol,Optional($p("flag"),False)),
    If(Or(AtomQ(u),FreeQ(u,x)),True,If(And(PowerQ(u),Or(RationalQ(Part(u,C2)),And($s("flag"),FreeQ(Part(u,C2),x)))),AlgebraicFunctionQ(Part(u,C1),x,$s("flag")),If(Or(ProductQ(u),SumQ(u)),Catch(CompoundExpression(Scan(Function(If(Not(AlgebraicFunctionQ(Slot1,x,$s("flag"))),Throw(False))),u),True)),If(ListQ(u),If(SameQ(u,List()),True,If(AlgebraicFunctionQ(First(u),x,$s("flag")),AlgebraicFunctionQ(Rest(u),x,$s("flag")),False)),False))))),
ISetDelayed(143,QuotientOfLinearsQ(u_,x_Symbol),
    If(ListQ(u),Catch(CompoundExpression(Scan(Function(If(Not(QuotientOfLinearsQ(Slot1,x)),Throw(False))),u),True)),And(QuotientOfLinearsP(u,x),$(Function(And(NeQ(Part(Slot1,C2),C0),NeQ(Part(Slot1,C4),C0))),QuotientOfLinearsParts(u,x)))))
  );
}
