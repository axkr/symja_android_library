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
SetDelayed(SubstForFractionalPowerOfLinear($p(u),$p(x,SymbolHead)),
    Module(List(Set($s("lst"),FractionalPowerOfLinear(u,C1,False,x)),n,a,b,$s("tmp")),If(Or(FalseQ($s("lst")),FalseQ(Part($s("lst"),C2))),False,CompoundExpression(CompoundExpression(CompoundExpression(CompoundExpression(CompoundExpression(Set(n,Part($s("lst"),C1)),Set(a,Coefficient(Part($s("lst"),C2),x,C0))),Set(b,Coefficient(Part($s("lst"),C2),x,C1))),Set($s("tmp"),Times(Power(x,Plus(n,Times(CN1,C1))),SubstForFractionalPower(u,Part($s("lst"),C2),n,Plus(Times(Times(CN1,a),Power(b,CN1)),Times(Power(x,n),Power(b,CN1))),x)))),Set($s("tmp"),SplitFreeFactors(Regularize($s("tmp"),x),x))),List(Part($s("tmp"),C2),n,Part($s("lst"),C2),Times(Part($s("tmp"),C1),Power(b,CN1))))))),
SetDelayed(FractionalPowerOfLinear($p(u),$p(n),$p(v),$p(x)),
    If(Or(AtomQ(u),FreeQ(u,x)),List(n,v),If(CalculusQ(u),False,If(And(And(FractionalPowerQ(u),LinearQ(Part(u,C1),x)),Or(FalseQ(v),ZeroQ(Plus(Part(u,C1),Times(CN1,v))))),List(LCM(Denominator(Part(u,C2)),n),Part(u,C1)),Catch(Module(List(Set($s("lst"),List(n,v))),CompoundExpression(Scan(Function(If(FalseQ(Set($s("lst"),FractionalPowerOfLinear(Slot1,Part($s("lst"),C1),Part($s("lst"),C2),x))),Throw(False))),u),$s("lst")))))))),
SetDelayed(SubstForFractionalPowerOfQuotientOfLinears($p(u),$p(x,SymbolHead)),
    Module(List(Set($s("lst"),FractionalPowerOfQuotientOfLinears(u,C1,False,x)),n,a,b,c,d,$s("tmp")),If(Or(FalseQ($s("lst")),FalseQ(Part($s("lst"),C2))),False,CompoundExpression(CompoundExpression(CompoundExpression(CompoundExpression(CompoundExpression(CompoundExpression(CompoundExpression(Set(n,Part($s("lst"),C1)),Set($s("tmp"),Part($s("lst"),C2))),Set($s("lst"),QuotientOfLinearsParts($s("tmp"),x))),Set(a,Part($s("lst"),C1))),Set(b,Part($s("lst"),C2))),Set(c,Part($s("lst"),C3))),Set(d,Part($s("lst"),C4))),If(ZeroQ(d),False,CompoundExpression(CompoundExpression(Set($s("lst"),Times(Power(x,Plus(n,Times(CN1,C1))),Times(SubstForFractionalPower(u,$s("tmp"),n,Times(Plus(Times(CN1,a),Times(c,Power(x,n))),Power(Plus(b,Times(CN1,Times(d,Power(x,n)))),CN1)),x),Power(Power(Plus(b,Times(CN1,Times(d,Power(x,n)))),C2),CN1)))),Set($s("lst"),SplitFreeFactors(Regularize($s("lst"),x),x))),List(Part($s("lst"),C2),n,$s("tmp"),Times(Part($s("lst"),C1),Plus(Times(b,c),Times(CN1,Times(a,d))))))))))),
SetDelayed(SubstForFractionalPowerQ($p(u),$p(v),$p(x,SymbolHead)),
    If(Or(AtomQ(u),FreeQ(u,x)),True,If(FractionalPowerQ(u),SubstForFractionalPowerAuxQ(u,v,x),Catch(CompoundExpression(Scan(Function(If(Not(SubstForFractionalPowerQ(Slot1,v,x)),Throw(False))),u),True))))),
SetDelayed(SubstForFractionalPowerAuxQ($p(u),$p(v),$p(x)),
    If(AtomQ(u),False,If(And(FractionalPowerQ(u),ZeroQ(Plus(Part(u,C1),Times(CN1,v)))),True,Catch(CompoundExpression(Scan(Function(If(SubstForFractionalPowerAuxQ(Slot1,v,x),Throw(True))),u),False))))),
SetDelayed(FractionalPowerOfQuotientOfLinears($p(u),$p(n),$p(v),$p(x)),
    If(Or(AtomQ(u),FreeQ(u,x)),List(n,v),If(CalculusQ(u),False,If(And(And(FractionalPowerQ(u),QuotientOfLinearsQ(Part(u,C1),x)),Or(FalseQ(v),ZeroQ(Plus(Part(u,C1),Times(CN1,v))))),List(LCM(Denominator(Part(u,C2)),n),Part(u,C1)),Catch(Module(List(Set($s("lst"),List(n,v))),CompoundExpression(Scan(Function(If(FalseQ(Set($s("lst"),FractionalPowerOfQuotientOfLinears(Slot1,Part($s("lst"),C1),Part($s("lst"),C2),x))),Throw(False))),u),$s("lst")))))))),
SetDelayed(SubstForInverseFunctionOfLinear($p(u),$p(x,SymbolHead)),
    Module(List(Set($s("tmp"),InverseFunctionOfLinear(u,x)),h,a,b),If(FalseQ($s("tmp")),False,CompoundExpression(CompoundExpression(CompoundExpression(Set(h,InverseFunction(Head($s("tmp")))),Set(a,Coefficient(Part($s("tmp"),C1),x,C0))),Set(b,Coefficient(Part($s("tmp"),C1),x,C1))),List(Times(SubstForInverseFunction(u,$s("tmp"),Plus(Times(Times(CN1,a),Power(b,CN1)),Times($(h,x),Power(b,CN1))),x),D($(h,x),x)),$s("tmp"),b))))),
SetDelayed(InverseFunctionOfLinear($p(u),$p(x,SymbolHead)),
    If(Or(AtomQ(u),CalculusQ(u)),False,If(And(InverseFunctionQ(u),LinearQ(Part(u,C1),x)),u,Module(List($s("tmp")),Catch(CompoundExpression(Scan(Function(If(NotFalseQ(Set($s("tmp"),InverseFunctionOfLinear(Slot1,x))),Throw($s("tmp")))),u),False)))))),
SetDelayed(SubstForInverseFunctionOfQuotientOfLinears($p(u),$p(x,SymbolHead)),
    Module(List(Set($s("tmp"),InverseFunctionOfQuotientOfLinears(u,x)),h,a,b,c,d,$s("lst")),If(FalseQ($s("tmp")),False,CompoundExpression(CompoundExpression(CompoundExpression(CompoundExpression(CompoundExpression(CompoundExpression(Set(h,InverseFunction(Head($s("tmp")))),Set($s("lst"),QuotientOfLinearsParts(Part($s("tmp"),C1),x))),Set(a,Part($s("lst"),C1))),Set(b,Part($s("lst"),C2))),Set(c,Part($s("lst"),C3))),Set(d,Part($s("lst"),C4))),List(Times(SubstForInverseFunction(u,$s("tmp"),Times(Plus(Times(CN1,a),Times(c,$(h,x))),Power(Plus(b,Times(CN1,Times(d,$(h,x)))),CN1)),x),Times(D($(h,x),x),Power(Power(Plus(b,Times(CN1,Times(d,$(h,x)))),C2),CN1))),$s("tmp"),Plus(Times(b,c),Times(CN1,Times(a,d)))))))),
SetDelayed(InverseFunctionOfQuotientOfLinears($p(u),$p(x,SymbolHead)),
    If(Or(AtomQ(u),CalculusQ(u)),False,If(And(InverseFunctionQ(u),QuotientOfLinearsQ(Part(u,C1),x)),u,Module(List($s("tmp")),Catch(CompoundExpression(Scan(Function(If(NotFalseQ(Set($s("tmp"),InverseFunctionOfQuotientOfLinears(Slot1,x))),Throw($s("tmp")))),u),False)))))),
SetDelayed(SubstForFractionalPower($p(u),$p(v),$p(n),$p(w),$p(x,SymbolHead)),
    If(AtomQ(u),If(SameQ(u,x),w,u),If(And(FractionalPowerQ(u),ZeroQ(Plus(Part(u,C1),Times(CN1,v)))),Power(x,Times(n,Part(u,C2))),Map(Function(SubstForFractionalPower(Slot1,v,n,w,x)),u)))),
SetDelayed(SubstForInverseFunction($p(u),$p(v),$p(x,SymbolHead)),
    SubstForInverseFunction(u,v,Times(Plus(Times(CN1,Coefficient(Part(v,C1),x,C0)),$(InverseFunction(Head(v)),x)),Power(Coefficient(Part(v,C1),x,C1),CN1)),x)),
SetDelayed(SubstForInverseFunction($p(u),$p(v),$p(w),$p(x,SymbolHead)),
    If(AtomQ(u),If(SameQ(u,x),w,u),If(And(SameQ(Head(u),Head(v)),ZeroQ(Plus(Part(u,C1),Times(CN1,Part(v,C1))))),x,Map(Function(SubstForInverseFunction(Slot1,v,w,x)),u)))),
SetDelayed(SubstForInverseLinear($p(u),$p(x,SymbolHead)),
    Module(List(Set($s("lst"),FunctionOfInverseLinear(u,x)),a,b),If(FalseQ($s("lst")),False,CompoundExpression(CompoundExpression(Set(a,Part($s("lst"),C1)),Set(b,Part($s("lst"),C2))),List(RegularizeSubst(u,x,Plus(Times(Times(CN1,a),Power(b,CN1)),Power(Times(b,x),CN1))),Plus(a,Times(b,x)),b))))),
SetDelayed(DerivativeDivides($p(u),$p(v),$p(x,SymbolHead)),
    If(If(IntPolynomialQ(u,x),And(IntPolynomialQ(v,x),Equal(Exponent(v,x),Plus(Exponent(u,x),Times(CN1,C1)))),EasyDQ(u,x)),Module(List(Set(w,Block(List(Set($s("ShowSteps"),False)),D(u,x)))),If(ZeroQ(w),False,CompoundExpression(Set(w,Simplify(Times(v,Power(w,CN1)))),If(FreeQ(w,x),w,False)))),False)),
SetDelayed(EasyDQ($p(u),$p(x,SymbolHead)),
    If(Or(Or(AtomQ(u),FreeQ(u,x)),Equal(Length(u),C0)),True,If(CalculusQ(u),False,If(Equal(Length(u),C1),EasyDQ(Part(u,C1),x),If(And(RationalFunctionQ(u,x),SameQ(RationalFunctionExponents(u,x),List(C1,C1))),True,If(ProductQ(u),If(FreeQ(First(u),x),EasyDQ(Rest(u),x),If(FreeQ(Rest(u),x),EasyDQ(First(u),x),False)),If(SumQ(u),And(EasyDQ(First(u),x),EasyDQ(Rest(u),x)),If(Equal(Length(u),C2),If(FreeQ(Part(u,C1),x),EasyDQ(Part(u,C2),x),If(FreeQ(Part(u,C2),x),EasyDQ(Part(u,C1),x),False)),False)))))))),
SetDelayed(Rt(Times(Power($p(u),$p(w)),$p(v,true)),$p(n,IntegerHead)),
    Condition(Module(List(Set(m,Numerator(NumericFactor(w)))),Condition(Times(Rt(v,n),Power(Rt(Power(u,Times(w,Power(m,CN1))),Times(n,Power(GCD(m,n),CN1))),Times(m,Power(GCD(m,n),CN1)))),Greater(m,C1))),Not(NegativeOrZeroQ(v)))),
SetDelayed(Rt(Times(Power($p(v),$p(m)),$p(u)),$p(n,IntegerHead)),
    Condition(Times(Rt(Times(CN1,u),n),Power(Rt(Times(CN1,Power(v,Times(CN1,m))),n),CN1)),And(And(RationalQ(m),Less(m,C0)),NegativeQ(u)))),
SetDelayed(Rt(Times($p(u),$p(v)),$p(n,IntegerHead)),
    Condition(Times(Rt(u,n),Rt(v,n)),Or(OddQ(n),And(Not(NegativeOrZeroQ(u)),Not(NegativeOrZeroQ(v)))))),
SetDelayed(Rt(Times(Power(Plus(Times(Power(Sin($p(v)),C2),$p(b,true)),$p(a)),$p(m,true)),$p(u,true)),$p(n,IntegerHead)),
    Condition(Rt(Times(u,Power(Times(a,Power(Cos(v),C2)),m)),n),ZeroQ(Plus(a,b)))),
SetDelayed(Rt(Times(Power(Plus(Times(Power(Cos($p(v)),C2),$p(b,true)),$p(a)),$p(m,true)),$p(u,true)),$p(n,IntegerHead)),
    Condition(Rt(Times(u,Power(Times(a,Power(Sin(v),C2)),m)),n),ZeroQ(Plus(a,b)))),
SetDelayed(Rt(Times(Power(Plus(Times(Power(Sinh($p(v)),C2),$p(b,true)),$p(a)),$p(m,true)),$p(u,true)),$p(n,IntegerHead)),
    Condition(Rt(Times(u,Power(Times(a,Power(Cosh(v),C2)),m)),n),ZeroQ(Plus(a,Times(CN1,b))))),
SetDelayed(Rt(Times(Power(Plus(Times(Power(Cosh($p(v)),C2),$p(b,true)),$p(a)),$p(m,true)),$p(u,true)),$p(n,IntegerHead)),
    Condition(Rt(Times(u,Power(Times(b,Power(Sinh(v),C2)),m)),n),ZeroQ(Plus(a,b)))),
SetDelayed(Rt($p(u),$p(n,IntegerHead)),
    Module(List(Set(v,Simplify(u))),CompoundExpression(If(Less(LeafCount(Together(v)),LeafCount(v)),Set(v,Together(v))),If(UnsameQ(v,u),Rt(v,n),Power(u,Power(n,CN1)))))),
SetDelayed(Mods($p(u)),
    If(Less(LeafCount(Plus(C1D2,Times(CN1,u))),LeafCount(u)),Times(ArcCot(Tan(Times(Pi,Plus(C1D2,Times(CN1,u))))),Power(Pi,CN1)),Times(ArcTan(Tan(Times(Pi,u))),Power(Pi,CN1)))),
SetDelayed(Mods($p(n)),
    Condition(Plus(C1D2,Times(CN1,Times(C1D2,Mod(Plus(C1,Times(CN1,Times(C2,n))),C2)))),NumericQ(n)))
  );
}
