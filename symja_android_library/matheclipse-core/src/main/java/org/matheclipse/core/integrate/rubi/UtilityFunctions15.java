package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class UtilityFunctions15 { 
  public static IAST RULES = List( 
ISetDelayed(160,SubstForInverseFunctionOfQuotientOfLinears(u_,x_Symbol),
    With(list(Set($s("tmp"),InverseFunctionOfQuotientOfLinears(u,x))),If(AtomQ($s("tmp")),False,With(list(Set(h,InverseFunction(Head($s("tmp")))),Set($s("lst"),QuotientOfLinearsParts(Part($s("tmp"),C1),x))),With(List(Set(a,Part($s("lst"),C1)),Set(b,Part($s("lst"),C2)),Set(c,Part($s("lst"),C3)),Set(d,Part($s("lst"),C4))),list(Times(SubstForInverseFunction(u,$s("tmp"),Times(Plus(Negate(a),Times(c,$(h,x))),Power(Subtract(b,Times(d,$(h,x))),CN1)),x),D($(h,x),x),Power(Subtract(b,Times(d,$(h,x))),CN2)),$s("tmp"),Subtract(Times(b,c),Times(a,d)))))))),
ISetDelayed(161,InverseFunctionOfQuotientOfLinears(u_,x_Symbol),
    If(Or(AtomQ(u),CalculusQ(u),FreeQ(u,x)),False,If(And(InverseFunctionQ(u),QuotientOfLinearsQ(Part(u,C1),x)),u,Module(list($s("tmp")),Catch(CompoundExpression(Scan(Function(If(Not(AtomQ(Set($s("tmp"),InverseFunctionOfQuotientOfLinears(Slot1,x)))),Throw($s("tmp")))),u),False)))))),
ISetDelayed(162,SubstForFractionalPower(u_,v_,n_,w_,x_Symbol),
    If(AtomQ(u),If(SameQ(u,x),w,u),If(And(FractionalPowerQ(u),EqQ(Part(u,C1),v)),Power(x,Times(n,Part(u,C2))),Map(Function(SubstForFractionalPower(Slot1,v,n,w,x)),u)))),
ISetDelayed(163,SubstForInverseFunction(u_,v_,x_Symbol),
    SubstForInverseFunction(u,v,Times(Plus(Negate(Coefficient(Part(v,C1),x,C0)),$(InverseFunction(Head(v)),x)),Power(Coefficient(Part(v,C1),x,C1),CN1)),x)),
ISetDelayed(164,SubstForInverseFunction(u_,v_,w_,x_Symbol),
    If(AtomQ(u),If(SameQ(u,x),w,u),If(And(SameQ(Head(u),Head(v)),EqQ(Part(u,C1),Part(v,C1))),x,Map(Function(SubstForInverseFunction(Slot1,v,w,x)),u)))),
ISetDelayed(165,AbsurdNumberQ(u_),
    RationalQ(u)),
ISetDelayed(166,AbsurdNumberQ(Power(u_,v_)),
    And(RationalQ(u),Greater(u,C0),FractionQ(v))),
ISetDelayed(167,AbsurdNumberQ(Times(u_,v_)),
    And(AbsurdNumberQ(u),AbsurdNumberQ(v))),
ISetDelayed(168,AbsurdNumberFactors(u_),
    If(AbsurdNumberQ(u),u,If(ProductQ(u),Map($rubi("AbsurdNumberFactors"),u),NumericFactor(u)))),
ISetDelayed(169,NonabsurdNumberFactors(u_),
    If(AbsurdNumberQ(u),C1,If(ProductQ(u),Map($rubi("NonabsurdNumberFactors"),u),NonnumericFactors(u)))),
ISetDelayed(170,FactorAbsurdNumber(m_),
    If(RationalQ(m),FactorInteger(m),If(PowerQ(m),Map(Function(list(Part(Slot1,C1),Times(Part(Slot1,C2),Part(m,C2)))),FactorInteger(Part(m,C1))),CombineExponents(Sort(Flatten(Map($rubi("FactorAbsurdNumber"),Apply(List,m)),C1),Function(Less(Part(Slot1,C1),Part(Slot2,C1)))))))),
ISetDelayed(171,CombineExponents($p("lst")),
    If(Less(Length($s("lst")),C2),$s("lst"),If(Equal(Part($s("lst"),C1,C1),Part($s("lst"),C2,C1)),CombineExponents(Prepend(Drop($s("lst"),C2),list(Part($s("lst"),C1,C1),Plus(Part($s("lst"),C1,C2),Part($s("lst"),C2,C2))))),Prepend(CombineExponents(Rest($s("lst"))),First($s("lst"))))))
  );
}
