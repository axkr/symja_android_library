package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class UtilityFunctions14 { 
  public static IAST RULES = List( 
ISetDelayed(144,QuotientOfLinearsP(Times(a_,u_),x_),
    Condition(QuotientOfLinearsP(u,x),FreeQ(a,x))),
ISetDelayed(145,QuotientOfLinearsP(Plus(a_,u_),x_),
    Condition(QuotientOfLinearsP(u,x),FreeQ(a,x))),
ISetDelayed(146,QuotientOfLinearsP(Power(u_,CN1),x_),
    QuotientOfLinearsP(u,x)),
ISetDelayed(147,QuotientOfLinearsP(u_,x_),
    Condition(True,LinearQ(u,x))),
ISetDelayed(148,QuotientOfLinearsP(Times(u_,Power(v_,CN1)),x_),
    Condition(True,And(LinearQ(u,x),LinearQ(v,x)))),
ISetDelayed(149,QuotientOfLinearsP(u_,x_),
    Or(SameQ(u,x),FreeQ(u,x))),
ISetDelayed(150,QuotientOfLinearsParts(Times(a_,u_),x_),
    Condition(Apply(Function(List(Times(a,Slot1),Times(a,Slot2),Slot(C3),Slot(C4))),QuotientOfLinearsParts(u,x)),FreeQ(a,x))),
ISetDelayed(151,QuotientOfLinearsParts(Plus(a_,u_),x_),
    Condition(Apply(Function(List(Plus(Slot1,Times(a,Slot(C3))),Plus(Slot2,Times(a,Slot(C4))),Slot(C3),Slot(C4))),QuotientOfLinearsParts(u,x)),FreeQ(a,x))),
ISetDelayed(152,QuotientOfLinearsParts(Power(u_,CN1),x_),
    Apply(Function(List(Slot(C3),Slot(C4),Slot1,Slot2)),QuotientOfLinearsParts(u,x))),
ISetDelayed(153,QuotientOfLinearsParts(u_,x_),
    Condition(List(Coefficient(u,x,C0),Coefficient(u,x,C1),C1,C0),LinearQ(u,x))),
ISetDelayed(154,QuotientOfLinearsParts(Times(u_,Power(v_,CN1)),x_),
    Condition(List(Coefficient(u,x,C0),Coefficient(u,x,C1),Coefficient(v,x,C0),Coefficient(v,x,C1)),And(LinearQ(u,x),LinearQ(v,x)))),
ISetDelayed(155,QuotientOfLinearsParts(u_,x_),
    If(SameQ(u,x),List(C0,C1,C1,C0),If(FreeQ(u,x),List(u,C0,C1,C0),CompoundExpression(Print($str("QuotientOfLinearsParts error!")),List(u,C0,C1,C0))))),
ISetDelayed(156,SubstForFractionalPowerOfQuotientOfLinears(u_,x_Symbol),
    Module(list(Set($s("lst"),FractionalPowerOfQuotientOfLinears(u,C1,False,x))),If(Or(AtomQ($s("lst")),AtomQ(Part($s("lst"),C2))),False,With(list(Set(n,Part($s("lst"),C1)),Set($s("tmp"),Part($s("lst"),C2))),CompoundExpression(Set($s("lst"),QuotientOfLinearsParts($s("tmp"),x)),With(List(Set(a,Part($s("lst"),C1)),Set(b,Part($s("lst"),C2)),Set(c,Part($s("lst"),C3)),Set(d,Part($s("lst"),C4))),If(EqQ(d,C0),False,CompoundExpression(Set($s("lst"),Simplify(Times(Power(x,Subtract(n,C1)),SubstForFractionalPower(u,$s("tmp"),n,Times(Plus(Negate(a),Times(c,Power(x,n))),Power(Subtract(b,Times(d,Power(x,n))),CN1)),x),Power(Subtract(b,Times(d,Power(x,n))),CN2)))),List(NonfreeFactors($s("lst"),x),n,$s("tmp"),Times(FreeFactors($s("lst"),x),Subtract(Times(b,c),Times(a,d)))))))))))),
ISetDelayed(157,SubstForFractionalPowerQ(u_,v_,x_Symbol),
    If(Or(AtomQ(u),FreeQ(u,x)),True,If(FractionalPowerQ(u),SubstForFractionalPowerAuxQ(u,v,x),Catch(CompoundExpression(Scan(Function(If(Not(SubstForFractionalPowerQ(Slot1,v,x)),Throw(False))),u),True))))),
ISetDelayed(158,SubstForFractionalPowerAuxQ(u_,v_,x_),
    If(AtomQ(u),False,If(And(FractionalPowerQ(u),EqQ(Part(u,C1),v)),True,Catch(CompoundExpression(Scan(Function(If(SubstForFractionalPowerAuxQ(Slot1,v,x),Throw(True))),u),False))))),
ISetDelayed(159,FractionalPowerOfQuotientOfLinears(u_,n_,v_,x_),
    If(Or(AtomQ(u),FreeQ(u,x)),list(n,v),If(CalculusQ(u),False,If(And(FractionalPowerQ(u),QuotientOfLinearsQ(Part(u,C1),x),Not(LinearQ(Part(u,C1),x)),Or(FalseQ(v),EqQ(Part(u,C1),v))),list(LCM(Denominator(Part(u,C2)),n),Part(u,C1)),Catch(Module(list(Set($s("lst"),list(n,v))),CompoundExpression(Scan(Function(If(AtomQ(Set($s("lst"),FractionalPowerOfQuotientOfLinears(Slot1,Part($s("lst"),C1),Part($s("lst"),C2),x))),Throw(False))),u),$s("lst"))))))))
  );
}
