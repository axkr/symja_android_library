package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class UtilityFunctions28 { 
  public static IAST RULES = List( 
ISetDelayed(375,UnifyTerms($p("lst"),x_),
    If(SameQ($s("lst"),List()),$s("lst"),UnifyTerm(First($s("lst")),UnifyTerms(Rest($s("lst")),x),x))),
ISetDelayed(376,UnifyTerm($p("term"),$p("lst"),x_),
    If(SameQ($s("lst"),List()),list($s("term")),With(list(Set($s("tmp"),Simplify(Times(First($s("lst")),Power($s("term"),CN1))))),If(FreeQ($s("tmp"),x),Prepend(Rest($s("lst")),Times(Plus(C1,$s("tmp")),$s("term"))),Prepend(UnifyTerm($s("term"),Rest($s("lst")),x),First($s("lst"))))))),
ISetDelayed(377,ExpandLinearProduct(v_,u_,a_,b_,x_Symbol),
    Condition(Module(list($s("lst")),CompoundExpression(Set($s("lst"),CoefficientList(ReplaceAll(u,Rule(x,Times(Subtract(x,a),Power(b,CN1)))),x)),Set($s("lst"),Map(Function(SimplifyTerm(Slot1,x)),$s("lst"))),Module(list($s("ii")),Sum(Times(v,Part($s("lst"),$s("ii")),Power(Plus(a,Times(b,x)),Subtract($s("ii"),C1))),list($s("ii"),C1,Length($s("lst"))))))),And(FreeQ(list(a,b),x),PolynomialQ(u,x)))),
ISetDelayed(378,ExpandTrigExpand(u_,F_,v_,m_,n_,x_Symbol),
    With(list(Set(w,ReplaceAll(Expand(Power(TrigExpand(F(Times(n,x))),m),x),Rule(x,v)))),If(SumQ(w),Map(Function(Times(u,Slot1)),w),Times(u,w)))),
ISetDelayed(379,ExpandTrigReduce(u_,v_,x_Symbol),
    With(list(Set(w,ExpandTrigReduce(v,x))),If(SumQ(w),Map(Function(Times(u,Slot1)),w),Times(u,w)))),
ISetDelayed(380,ExpandTrigReduce(Times(Power($(F_,Plus(n_,v_DEFAULT)),m_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Module(list($s("nn")),ReplaceAll(ExpandTrigReduce(Times(u,Power(F(Plus($s("nn"),v)),m)),x),Rule($s("nn"),n))),And(MemberQ(list(Sinh,Cosh),FSymbol),IntegerQ(m),RationalQ(n)))),
ISetDelayed(381,ExpandTrigReduce(u_,x_Symbol),
    ExpandTrigReduceAux(u,x)),
ISetDelayed(382,ExpandTrigReduceAux(u_,x_Symbol),
    With(list(Set(v,Expand(TrigReduce(u)))),If(SumQ(v),Map(Function(NormalizeTrigReduce(Slot1,x)),v),NormalizeTrigReduce(v,x)))),
ISetDelayed(383,NormalizeTrigReduce(Times(Power($(F_,u_),n_DEFAULT),a_DEFAULT),x_Symbol),
    Condition(Times(a,Power(F(ExpandToSum(u,x)),n)),And(FreeQ(list(FSymbol,a,n),x),PolynomialQ(u,x),Greater(Exponent(u,x),C0)))),
ISetDelayed(384,NormalizeTrigReduce(u_,x_Symbol),
    u),
ISetDelayed(385,ExpandTrigToExp(u_,x_Symbol),
    ExpandTrigToExp(C1,u,x)),
ISetDelayed(386,ExpandTrigToExp(u_,v_,x_Symbol),
    Module(list(Set(w,TrigToExp(v))),CompoundExpression(Set(w,If(SumQ(w),Map(Function(SimplifyIntegrand(Times(u,Slot1),x)),w),SimplifyIntegrand(Times(u,w),x))),ExpandIntegrand(FreeFactors(w,x),NonfreeFactors(w,x),x)))),
ISetDelayed(387,Star(u_,v_),
    Condition(CompoundExpression(Print($str("*** Warning ***:  0*"),v,$str("]")),C0),EqQ(u,C0))),
ISetDelayed(388,Star(u_,v_),
    Condition(Map(Function(Star(u,Slot1)),v),SumQ(v))),
ISetDelayed(389,Star(u_,Star(v_,w_)),
    Star(Times(u,v),w)),
ISetDelayed(390,Star(u_,v_),
    Condition(Negate(Star(Negate(u),v)),And(Less(NumericFactor(u),C0),Greater(NumericFactor(Negate(u)),C0))))
  );
}
