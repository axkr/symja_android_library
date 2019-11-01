package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *  
 */
public class UtilityFunctions21 { 
  public static IAST RULES = List( 
ISetDelayed(359,KernelSubst(u_,x_Symbol,$p("alst",List)),
    If(AtomQ(u),With(List(Set($s("tmp"),Select($s("alst"),Function(SameQ(Part(Slot1,C1),u)),C1))),If(SameQ($s("tmp"),List()),u,Part($s("tmp"),C1,C2))),If(IntegerPowerQ(u),With(List(Set($s("tmp"),KernelSubst(Part(u,C1),x,$s("alst")))),If(And(Less(Part(u,C2),C0),EqQ($s("tmp"),C0)),Indeterminate,Power($s("tmp"),Part(u,C2)))),If(Or(ProductQ(u),SumQ(u)),Map(Function(KernelSubst(Slot1,x,$s("alst"))),u),u)))),
ISetDelayed(360,ExpandAlgebraicFunction(Times($p(u,Plus),v_),x_Symbol),
    Condition(Map(Function(Times(Slot1,v)),u),Not(FreeQ(u,x)))),
ISetDelayed(361,ExpandAlgebraicFunction(Times(Power($p(u,Plus),n_),v_DEFAULT),x_Symbol),
    Condition(With(List(Set(w,Expand(Power(u,n),x))),Condition(Map(Function(Times(Slot1,v)),w),SumQ(w))),And(IGtQ(n,C0),Not(FreeQ(u,x))))),
ISetDelayed(362,UnifySum(u_,x_Symbol),
    If(SumQ(u),Apply(Plus,UnifyTerms(Apply(List,u),x)),SimplifyTerm(u,x))),
ISetDelayed(363,UnifyTerms($p("lst"),x_),
    If(SameQ($s("lst"),List()),$s("lst"),UnifyTerm(First($s("lst")),UnifyTerms(Rest($s("lst")),x),x))),
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
    If(SumQ(v),Map(Function(Times(u,Slot1)),v),Times(u,v)))
  );
}
