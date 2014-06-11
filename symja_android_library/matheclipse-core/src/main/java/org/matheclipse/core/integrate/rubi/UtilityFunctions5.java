package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctions.*;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
/** 
 * UtilityFunctions rules from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *  
 */
public class UtilityFunctions5 { 
  public static IAST RULES = List( 
ISetDelayed(SubstForFractionalPowerOfLinear(u_,x_Symbol),
    Module(List(Set($s("lst"),FractionalPowerOfLinear(u,C1,False,x)),n,a,b,$s("tmp")),If(Or(FalseQ($s("lst")),FalseQ(Part($s("lst"),C2))),False,CompoundExpression(CompoundExpression(CompoundExpression(CompoundExpression(CompoundExpression(Set(n,Part($s("lst"),C1)),Set(a,Coefficient(Part($s("lst"),C2),x,C0))),Set(b,Coefficient(Part($s("lst"),C2),x,C1))),Set($s("tmp"),Times(Power(x,Plus(n,Times(CN1,C1))),SubstForFractionalPower(u,Part($s("lst"),C2),n,Plus(Times(CN1,a,Power(b,CN1)),Times(Power(x,n),Power(b,CN1))),x)))),Set($s("tmp"),SplitFreeFactors(Regularize($s("tmp"),x),x))),List(Part($s("tmp"),C2),n,Part($s("lst"),C2),Times(Part($s("tmp"),C1),Power(b,CN1))))))),
ISetDelayed(FractionalPowerOfLinear(u_,n_,v_,x_),
    If(Or(AtomQ(u),FreeQ(u,x)),List(n,v),If(CalculusQ(u),False,If(And(And(FractionalPowerQ(u),LinearQ(Part(u,C1),x)),Or(FalseQ(v),ZeroQ(Plus(Part(u,C1),Times(CN1,v))))),List(LCM(Denominator(Part(u,C2)),n),Part(u,C1)),Catch(Module(List(Set($s("lst"),List(n,v))),CompoundExpression(Scan(Function(If(FalseQ(Set($s("lst"),FractionalPowerOfLinear(Slot1,Part($s("lst"),C1),Part($s("lst"),C2),x))),Throw(False))),u),$s("lst")))))))),
ISetDelayed(SubstForFractionalPowerOfQuotientOfLinears(u_,x_Symbol),
    Module(List(Set($s("lst"),FractionalPowerOfQuotientOfLinears(u,C1,False,x)),n,a,b,c,d,$s("tmp")),If(Or(FalseQ($s("lst")),FalseQ(Part($s("lst"),C2))),False,CompoundExpression(CompoundExpression(CompoundExpression(CompoundExpression(CompoundExpression(CompoundExpression(CompoundExpression(Set(n,Part($s("lst"),C1)),Set($s("tmp"),Part($s("lst"),C2))),Set($s("lst"),QuotientOfLinearsParts($s("tmp"),x))),Set(a,Part($s("lst"),C1))),Set(b,Part($s("lst"),C2))),Set(c,Part($s("lst"),C3))),Set(d,Part($s("lst"),C4))),If(ZeroQ(d),False,CompoundExpression(CompoundExpression(Set($s("lst"),Times(Power(x,Plus(n,Times(CN1,C1))),SubstForFractionalPower(u,$s("tmp"),n,Times(Plus(Times(CN1,a),Times(c,Power(x,n))),Power(Plus(b,Times(CN1,d,Power(x,n))),CN1)),x),Power(Power(Plus(b,Times(CN1,d,Power(x,n))),C2),CN1))),Set($s("lst"),SplitFreeFactors(Regularize($s("lst"),x),x))),List(Part($s("lst"),C2),n,$s("tmp"),Times(Part($s("lst"),C1),Plus(Times(b,c),Times(CN1,a,d)))))))))),
ISetDelayed(SubstForFractionalPowerQ(u_,v_,x_Symbol),
    If(Or(AtomQ(u),FreeQ(u,x)),True,If(FractionalPowerQ(u),SubstForFractionalPowerAuxQ(u,v,x),Catch(CompoundExpression(Scan(Function(If(Not(SubstForFractionalPowerQ(Slot1,v,x)),Throw(False))),u),True))))),
ISetDelayed(SubstForFractionalPowerAuxQ(u_,v_,x_),
    If(AtomQ(u),False,If(And(FractionalPowerQ(u),ZeroQ(Plus(Part(u,C1),Times(CN1,v)))),True,Catch(CompoundExpression(Scan(Function(If(SubstForFractionalPowerAuxQ(Slot1,v,x),Throw(True))),u),False))))),
ISetDelayed(FractionalPowerOfQuotientOfLinears(u_,n_,v_,x_),
    If(Or(AtomQ(u),FreeQ(u,x)),List(n,v),If(CalculusQ(u),False,If(And(And(FractionalPowerQ(u),QuotientOfLinearsQ(Part(u,C1),x)),Or(FalseQ(v),ZeroQ(Plus(Part(u,C1),Times(CN1,v))))),List(LCM(Denominator(Part(u,C2)),n),Part(u,C1)),Catch(Module(List(Set($s("lst"),List(n,v))),CompoundExpression(Scan(Function(If(FalseQ(Set($s("lst"),FractionalPowerOfQuotientOfLinears(Slot1,Part($s("lst"),C1),Part($s("lst"),C2),x))),Throw(False))),u),$s("lst")))))))),
ISetDelayed(SubstForInverseFunctionOfLinear(u_,x_Symbol),
    Module(List(Set($s("tmp"),InverseFunctionOfLinear(u,x)),h,a,b),If(FalseQ($s("tmp")),False,CompoundExpression(CompoundExpression(CompoundExpression(Set(h,InverseFunction(Head($s("tmp")))),Set(a,Coefficient(Part($s("tmp"),C1),x,C0))),Set(b,Coefficient(Part($s("tmp"),C1),x,C1))),List(Times(SubstForInverseFunction(u,$s("tmp"),Plus(Times(CN1,a,Power(b,CN1)),Times($(h,x),Power(b,CN1))),x),D($(h,x),x)),$s("tmp"),b))))),
ISetDelayed(InverseFunctionOfLinear(u_,x_Symbol),
    If(Or(AtomQ(u),CalculusQ(u)),False,If(And(InverseFunctionQ(u),LinearQ(Part(u,C1),x)),u,Module(List($s("tmp")),Catch(CompoundExpression(Scan(Function(If(NotFalseQ(Set($s("tmp"),InverseFunctionOfLinear(Slot1,x))),Throw($s("tmp")))),u),False)))))),
ISetDelayed(SubstForInverseFunctionOfQuotientOfLinears(u_,x_Symbol),
    Module(List(Set($s("tmp"),InverseFunctionOfQuotientOfLinears(u,x)),h,a,b,c,d,$s("lst")),If(FalseQ($s("tmp")),False,CompoundExpression(CompoundExpression(CompoundExpression(CompoundExpression(CompoundExpression(CompoundExpression(Set(h,InverseFunction(Head($s("tmp")))),Set($s("lst"),QuotientOfLinearsParts(Part($s("tmp"),C1),x))),Set(a,Part($s("lst"),C1))),Set(b,Part($s("lst"),C2))),Set(c,Part($s("lst"),C3))),Set(d,Part($s("lst"),C4))),List(Times(SubstForInverseFunction(u,$s("tmp"),Times(Plus(Times(CN1,a),Times(c,$(h,x))),Power(Plus(b,Times(CN1,d,$(h,x))),CN1)),x),D($(h,x),x),Power(Power(Plus(b,Times(CN1,d,$(h,x))),C2),CN1)),$s("tmp"),Plus(Times(b,c),Times(CN1,a,d))))))),
ISetDelayed(InverseFunctionOfQuotientOfLinears(u_,x_Symbol),
    If(Or(AtomQ(u),CalculusQ(u)),False,If(And(InverseFunctionQ(u),QuotientOfLinearsQ(Part(u,C1),x)),u,Module(List($s("tmp")),Catch(CompoundExpression(Scan(Function(If(NotFalseQ(Set($s("tmp"),InverseFunctionOfQuotientOfLinears(Slot1,x))),Throw($s("tmp")))),u),False)))))),
ISetDelayed(SubstForFractionalPower(u_,v_,n_,w_,x_Symbol),
    If(AtomQ(u),If(SameQ(u,x),w,u),If(And(FractionalPowerQ(u),ZeroQ(Plus(Part(u,C1),Times(CN1,v)))),Power(x,Times(n,Part(u,C2))),Map(Function(SubstForFractionalPower(Slot1,v,n,w,x)),u)))),
ISetDelayed(SubstForInverseFunction(u_,v_,x_Symbol),
    SubstForInverseFunction(u,v,Times(Plus(Times(CN1,Coefficient(Part(v,C1),x,C0)),$(InverseFunction(Head(v)),x)),Power(Coefficient(Part(v,C1),x,C1),CN1)),x)),
ISetDelayed(SubstForInverseFunction(u_,v_,w_,x_Symbol),
    If(AtomQ(u),If(SameQ(u,x),w,u),If(And(SameQ(Head(u),Head(v)),ZeroQ(Plus(Part(u,C1),Times(CN1,Part(v,C1))))),x,Map(Function(SubstForInverseFunction(Slot1,v,w,x)),u)))),
ISetDelayed(SubstForInverseLinear(u_,x_Symbol),
    Module(List(Set($s("lst"),FunctionOfInverseLinear(u,x)),a,b),If(FalseQ($s("lst")),False,CompoundExpression(CompoundExpression(Set(a,Part($s("lst"),C1)),Set(b,Part($s("lst"),C2))),List(RegularizeSubst(u,x,Plus(Times(CN1,a,Power(b,CN1)),Power(Times(b,x),CN1))),Plus(a,Times(b,x)),b))))),
ISetDelayed(DerivativeDivides(u_,v_,x_Symbol),
    If(If(IntPolynomialQ(u,x),And(IntPolynomialQ(v,x),Equal(Exponent(v,x),Plus(Exponent(u,x),Times(CN1,C1)))),EasyDQ(u,x)),Module(List(Set(w,Block(List(Set($s("ShowSteps"),False)),D(u,x)))),If(ZeroQ(w),False,CompoundExpression(Set(w,Simplify(Times(v,Power(w,CN1)))),If(FreeQ(w,x),w,False)))),False)),
ISetDelayed(EasyDQ(u_,x_Symbol),
    If(Or(Or(AtomQ(u),FreeQ(u,x)),Equal(Length(u),C0)),True,If(CalculusQ(u),False,If(Equal(Length(u),C1),EasyDQ(Part(u,C1),x),If(And(RationalFunctionQ(u,x),SameQ(RationalFunctionExponents(u,x),List(C1,C1))),True,If(ProductQ(u),If(FreeQ(First(u),x),EasyDQ(Rest(u),x),If(FreeQ(Rest(u),x),EasyDQ(First(u),x),False)),If(SumQ(u),And(EasyDQ(First(u),x),EasyDQ(Rest(u),x)),If(Equal(Length(u),C2),If(FreeQ(Part(u,C1),x),EasyDQ(Part(u,C2),x),If(FreeQ(Part(u,C2),x),EasyDQ(Part(u,C1),x),False)),False)))))))),
ISetDelayed(Rt(Times(Power(u_,w_),v_DEFAULT),$p(n,IntegerHead)),
    Condition(Module(List(Set(m,Numerator(NumericFactor(w)))),Condition(Times(Rt(v,n),Power(Rt(Power(u,Times(w,Power(m,CN1))),Times(n,Power(GCD(m,n),CN1))),Times(m,Power(GCD(m,n),CN1)))),Greater(m,C1))),Not(NegativeOrZeroQ(v)))),
ISetDelayed(Rt(Times(Power(v_,m_),u_),$p(n,IntegerHead)),
    Condition(Times(Rt(Times(CN1,u),n),Power(Rt(Times(CN1,Power(v,Times(CN1,m))),n),CN1)),And(And(RationalQ(m),Less(m,C0)),NegativeQ(u)))),
ISetDelayed(Rt(Times(u_,v_),$p(n,IntegerHead)),
    Condition(Times(Rt(u,n),Rt(v,n)),Or(OddQ(n),And(Not(NegativeOrZeroQ(u)),Not(NegativeOrZeroQ(v)))))),
ISetDelayed(Rt(Times(Power(Plus(Times(Power(Sin(v_),C2),b_DEFAULT),a_),m_DEFAULT),u_DEFAULT),$p(n,IntegerHead)),
    Condition(Rt(Times(u,Power(Times(a,Power(Cos(v),C2)),m)),n),ZeroQ(Plus(a,b)))),
ISetDelayed(Rt(Times(Power(Plus(Times(Power(Cos(v_),C2),b_DEFAULT),a_),m_DEFAULT),u_DEFAULT),$p(n,IntegerHead)),
    Condition(Rt(Times(u,Power(Times(a,Power(Sin(v),C2)),m)),n),ZeroQ(Plus(a,b)))),
ISetDelayed(Rt(Times(Power(Plus(Times(Power(Sinh(v_),C2),b_DEFAULT),a_),m_DEFAULT),u_DEFAULT),$p(n,IntegerHead)),
    Condition(Rt(Times(u,Power(Times(a,Power(Cosh(v),C2)),m)),n),ZeroQ(Plus(a,Times(CN1,b))))),
ISetDelayed(Rt(Times(Power(Plus(Times(Power(Cosh(v_),C2),b_DEFAULT),a_),m_DEFAULT),u_DEFAULT),$p(n,IntegerHead)),
    Condition(Rt(Times(u,Power(Times(b,Power(Sinh(v),C2)),m)),n),ZeroQ(Plus(a,b)))),
ISetDelayed(Rt(u_,$p(n,IntegerHead)),
    Module(List(Set(v,Simplify(u))),CompoundExpression(If(Less(LeafCount(Together(v)),LeafCount(v)),Set(v,Together(v))),If(UnsameQ(v,u),Rt(v,n),Power(u,Power(n,CN1)))))),
ISetDelayed(Mods(u_),
    If(Less(LeafCount(Plus(Rational(C1,C2),Times(CN1,u))),LeafCount(u)),Times(ArcCot(Tan(Times(Pi,Plus(Rational(C1,C2),Times(CN1,u))))),Power(Pi,CN1)),Times(ArcTan(Tan(Times(Pi,u))),Power(Pi,CN1)))),
ISetDelayed(Mods(n_),
    Condition(Plus(Rational(C1,C2),Times(CN1,Rational(C1,C2),Mod(Plus(C1,Times(CN1,C2,n)),C2))),NumericQ(n)))
  );
}
