package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *  
 */
public class UtilityFunctions17 { 
  public static IAST RULES = List( 
ISetDelayed(364,UnifyTerm($p("term"),$p("lst"),x_),
    If(SameQ($s("lst"),List()),List($s("term")),With(List(Set($s("tmp"),Simplify(Times(First($s("lst")),Power($s("term"),CN1))))),If(FreeQ($s("tmp"),x),Prepend(Rest($s("lst")),Times(Plus(C1,$s("tmp")),$s("term"))),Prepend(UnifyTerm($s("term"),Rest($s("lst")),x),First($s("lst"))))))),
ISetDelayed(365,ExpandLinearProduct(v_,u_,a_,b_,x_Symbol),
    Condition(Module(List($s("lst")),CompoundExpression(Set($s("lst"),CoefficientList(ReplaceAll(u,Rule(x,Times(Subtract(x,a),Power(b,CN1)))),x)),Set($s("lst"),Map(Function(SimplifyTerm(Slot1,x)),$s("lst"))),Module(List($s("ii")),Sum(Times(v,Part($s("lst"),$s("ii")),Power(Plus(a,Times(b,x)),Subtract($s("ii"),C1))),List($s("ii"),C1,Length($s("lst"))))))),And(FreeQ(List(a,b),x),PolynomialQ(u,x)))),
ISetDelayed(366,ExpandTrigExpand(u_,F_,v_,m_,n_,x_Symbol),
    With(List(Set(w,ReplaceAll(Expand(Power(TrigExpand(F(Times(n,x))),m),x),Rule(x,v)))),If(SumQ(w),Map(Function(Times(u,Slot1)),w),Times(u,w)))),
ISetDelayed(367,ExpandTrigReduce(u_,v_,x_Symbol),
    With(List(Set(w,ExpandTrigReduce(v,x))),If(SumQ(w),Map(Function(Times(u,Slot1)),w),Times(u,w)))),
ISetDelayed(368,ExpandTrigReduce(Times(u_DEFAULT,Power($(F_,Plus(n_,v_DEFAULT)),m_DEFAULT)),x_Symbol),
    Condition(Module(List($s("nn")),ReplaceAll(ExpandTrigReduce(Times(u,Power(F(Plus($s("nn"),v)),m)),x),Rule($s("nn"),n))),And(MemberQ(List(Sinh,Cosh),FSymbol),IntegerQ(m),RationalQ(n)))),
ISetDelayed(369,ExpandTrigReduce(u_,x_Symbol),
    ExpandTrigReduceAux(u,x)),
ISetDelayed(370,ExpandTrigReduceAux(u_,x_Symbol),
    With(List(Set(v,Expand(TrigReduce(u)))),If(SumQ(v),Map(Function(NormalizeTrigReduce(Slot1,x)),v),NormalizeTrigReduce(v,x)))),
ISetDelayed(371,NormalizeTrigReduce(Times(a_DEFAULT,Power($(F_,u_),n_DEFAULT)),x_Symbol),
    Condition(Times(a,Power(F(ExpandToSum(u,x)),n)),And(FreeQ(List(FSymbol,a,n),x),PolynomialQ(u,x),Greater(Exponent(u,x),C0)))),
ISetDelayed(372,NormalizeTrigReduce(u_,x_Symbol),
    u),
ISetDelayed(373,ExpandTrigToExp(u_,x_Symbol),
    ExpandTrigToExp(C1,u,x)),
ISetDelayed(374,ExpandTrigToExp(u_,v_,x_Symbol),
    Module(List(Set(w,TrigToExp(v))),CompoundExpression(Set(w,If(SumQ(w),Map(Function(SimplifyIntegrand(Times(u,Slot1),x)),w),SimplifyIntegrand(Times(u,w),x))),ExpandIntegrand(FreeFactors(w,x),NonfreeFactors(w,x),x)))),
ISetDelayed(375,Distrib(u_,v_),
    If(SumQ(v),Map(Function(Times(u,Slot1)),v),Times(u,v))),
ISetDelayed(376,Dist(u_,$(Defer($rubi("Dist")),v_,w_,x_),x_),
    Dist(Times(u,v),w,x)),
ISetDelayed(377,Dist(u_,v_,x_),
    If(SameQ(u,C1),v,If(SameQ(u,C0),CompoundExpression(Print($str("*** Warning ***:  Dist[0,"),v,$str(","),x,$str("]")),C0),If(And(Less(NumericFactor(u),C0),Greater(NumericFactor(Negate(u)),C0)),Negate(Dist(Negate(u),v,x)),If(SumQ(v),Map(Function(Dist(u,Slot1,x)),v),If(IntegralFreeQ(v),Simp(Times(u,v),x),With(List(Set(w,Times(Simp(Times(u,Sqr(x)),x),Power(x,CN2)))),If(And(UnsameQ(w,u),FreeQ(w,x),SameQ(w,Simp(w,x)),SameQ(w,Times(Simp(Times(w,Sqr(x)),x),Power(x,CN2)))),Dist(w,v,x),If(UnsameQ($s("§$showsteps"),True),Simp(Times(u,v),x),$(Defer($rubi("Dist")),u,v,x)))))))))),
ISetDelayed(378,DistributeDegree(u_,m_),
    If(AtomQ(u),Power(u,m),If(PowerQ(u),Power(Part(u,C1),Times(Part(u,C2),m)),If(ProductQ(u),Map(Function(DistributeDegree(Slot1,m)),u),Power(u,m))))),
ISetDelayed(379,FunctionOfLinear(u_,x_Symbol),
    With(List(Set($s("lst"),FunctionOfLinear(u,False,False,x,False))),If(Or(AtomQ($s("lst")),FalseQ(Part($s("lst"),C1)),And(SameQ(Part($s("lst"),C1),C0),SameQ(Part($s("lst"),C2),C1))),False,List(FunctionOfLinearSubst(u,Part($s("lst"),C1),Part($s("lst"),C2),x),Part($s("lst"),C1),Part($s("lst"),C2))))),
ISetDelayed(380,FunctionOfLinear(u_,a_,b_,x_,$p("flag")),
    If(FreeQ(u,x),List(a,b),If(CalculusQ(u),False,If(LinearQ(u,x),If(FalseQ(a),List(Coefficient(u,x,C0),Coefficient(u,x,C1)),With(List(Set($s("lst"),CommonFactors(List(b,Coefficient(u,x,C1))))),If(And(EqQ(Coefficient(u,x,C0),C0),Not($s("flag"))),List(C0,Part($s("lst"),C1)),If(EqQ(Subtract(Times(b,Coefficient(u,x,C0)),Times(a,Coefficient(u,x,C1))),C0),List(Times(a,Power(Part($s("lst"),C2),CN1)),Part($s("lst"),C1)),List(C0,C1))))),If(And(PowerQ(u),FreeQ(Part(u,C1),x)),FunctionOfLinear(Times(Log(Part(u,C1)),Part(u,C2)),a,b,x,False),Module(List($s("lst")),If(And(ProductQ(u),NeQ(Part(Set($s("lst"),MonomialFactor(u,x)),C1),C0)),If(And(False,IntegerQ(Part($s("lst"),C1)),Unequal(Part($s("lst"),C1),CN1),FreeQ(Part($s("lst"),C2),x)),If(And(RationalQ(LeadFactor(Part($s("lst"),C2))),Less(LeadFactor(Part($s("lst"),C2)),C0)),FunctionOfLinear(Times(DivideDegreesOfFactors(Negate(Part($s("lst"),C2)),Part($s("lst"),C1)),x),a,b,x,False),FunctionOfLinear(Times(DivideDegreesOfFactors(Part($s("lst"),C2),Part($s("lst"),C1)),x),a,b,x,False)),False),CompoundExpression(Set($s("lst"),List(a,b)),Catch(CompoundExpression(Scan(Function(CompoundExpression(Set($s("lst"),FunctionOfLinear(Slot1,Part($s("lst"),C1),Part($s("lst"),C2),x,SumQ(u))),If(AtomQ($s("lst")),Throw(False)))),u),$s("lst"))))))))))),
ISetDelayed(381,FunctionOfLinearSubst(u_,a_,b_,x_),
    If(FreeQ(u,x),u,If(LinearQ(u,x),Module(List(Set($s("tmp"),Coefficient(u,x,C1))),CompoundExpression(Set($s("tmp"),If(SameQ($s("tmp"),b),C1,Times($s("tmp"),Power(b,CN1)))),Plus(Coefficient(u,x,C0),Times(CN1,a,$s("tmp")),Times($s("tmp"),x)))),If(And(PowerQ(u),FreeQ(Part(u,C1),x)),Exp(FullSimplify(FunctionOfLinearSubst(Times(Log(Part(u,C1)),Part(u,C2)),a,b,x))),Module(List($s("lst")),If(And(ProductQ(u),NeQ(Part(Set($s("lst"),MonomialFactor(u,x)),C1),C0)),If(And(RationalQ(LeadFactor(Part($s("lst"),C2))),Less(LeadFactor(Part($s("lst"),C2)),C0)),Negate(Power(FunctionOfLinearSubst(Times(DivideDegreesOfFactors(Negate(Part($s("lst"),C2)),Part($s("lst"),C1)),x),a,b,x),Part($s("lst"),C1))),Power(FunctionOfLinearSubst(Times(DivideDegreesOfFactors(Part($s("lst"),C2),Part($s("lst"),C1)),x),a,b,x),Part($s("lst"),C1))),Map(Function(FunctionOfLinearSubst(Slot1,a,b,x)),u))))))),
ISetDelayed(382,DivideDegreesOfFactors(u_,n_),
    If(ProductQ(u),Map(Function(Power(LeadBase(Slot1),Times(LeadDegree(Slot1),Power(n,CN1)))),u),Power(LeadBase(u),Times(LeadDegree(u),Power(n,CN1))))),
ISetDelayed(383,FunctionOfInverseLinear(u_,x_Symbol),
    FunctionOfInverseLinear(u,Null,x)),
ISetDelayed(384,FunctionOfInverseLinear(u_,$p("lst"),x_),
    If(FreeQ(u,x),$s("lst"),If(SameQ(u,x),False,If(QuotientOfLinearsQ(u,x),With(List(Set($s("tmp"),Drop(QuotientOfLinearsParts(u,x),C2))),If(SameQ(Part($s("tmp"),C2),C0),False,If(SameQ($s("lst"),Null),$s("tmp"),If(EqQ(Subtract(Times(Part($s("lst"),C1),Part($s("tmp"),C2)),Times(Part($s("lst"),C2),Part($s("tmp"),C1))),C0),$s("lst"),False)))),If(CalculusQ(u),False,Module(List(Set($s("tmp"),$s("lst"))),Catch(CompoundExpression(Scan(Function(If(AtomQ(Set($s("tmp"),FunctionOfInverseLinear(Slot1,$s("tmp"),x))),Throw(False))),u),$s("tmp"))))))))),
ISetDelayed(385,FunctionOfExponentialQ(u_,x_Symbol),
    Block(List(Set($s("$base$"),Null),Set($s("$expon$"),Null),Set($s("§$exponflag$"),False)),And(FunctionOfExponentialTest(u,x),$s("§$exponflag$"))))
  );
}
