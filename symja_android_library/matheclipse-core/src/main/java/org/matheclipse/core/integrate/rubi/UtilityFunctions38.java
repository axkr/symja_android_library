package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class UtilityFunctions38 { 
  public static IAST RULES = List( 
ISetDelayed(519,InverseFunctionOfLinear(u_,x_Symbol),
    If(Or(AtomQ(u),CalculusQ(u),FreeQ(u,x)),False,If(And(InverseFunctionQ(u),LinearQ(Part(u,C1),x)),u,Module(list($s("tmp")),Catch(CompoundExpression(Scan(Function(If(Not(AtomQ(Set($s("tmp"),InverseFunctionOfLinear(Slot1,x)))),Throw($s("tmp")))),u),False)))))),
ISetDelayed(520,TryPureTanSubst(u_,x_Symbol),
    Not(MatchQ(u,Condition($(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,$(G_,v_))))),And(FreeQ(list(a,b,c),x),MemberQ(List(ArcTan,ArcCot,ArcTanh,ArcCoth),FSymbol),MemberQ(List(Tan,Cot,Tanh,Coth),GSymbol),LinearQ(v,x)))))),
ISetDelayed(521,TryTanhSubst(u_,x_Symbol),
    And(FalseQ(FunctionOfLinear(u,x)),Not(MatchQ(u,Condition(Times(r_DEFAULT,Power(Plus(s_,t_),n_DEFAULT)),And(IntegerQ(n),Greater(n,C0))))),Not(MatchQ(u,Log(v_))),Not(MatchQ(u,Condition(Power(Plus(a_,Times(b_DEFAULT,Power($(f_,x),n_))),CN1),And(MemberQ(List(Sinh,Cosh,Sech,Csch),f),IntegerQ(n),Greater(n,C2))))),Not(MatchQ(u,Condition(Times($(f_,Times(m_DEFAULT,x)),$(g_,Times(n_DEFAULT,x))),And(IntegersQ(m,n),MemberQ(List(Sinh,Cosh,Sech,Csch),f),MemberQ(List(Sinh,Cosh,Sech,Csch),g))))),Not(MatchQ(u,Condition(Times(r_DEFAULT,Power(Times(a_DEFAULT,Power(s_,m_)),p_)),And(FreeQ(list(a,m,p),x),Not(And(SameQ(m,C2),Or(SameQ(s,Sech(x)),SameQ(s,Csch(x))))))))),SameQ(u,ExpandIntegrand(u,x)))),
ISetDelayed(522,TryPureTanhSubst(u_,x_Symbol),
    And(Not(MatchQ(u,Log(v_))),Not(MatchQ(u,Condition(ArcTanh(Times(a_DEFAULT,Tanh(v_))),FreeQ(a,x)))),Not(MatchQ(u,Condition(ArcTanh(Times(a_DEFAULT,Coth(v_))),FreeQ(a,x)))),Not(MatchQ(u,Condition(ArcCoth(Times(a_DEFAULT,Tanh(v_))),FreeQ(a,x)))),Not(MatchQ(u,Condition(ArcCoth(Times(a_DEFAULT,Coth(v_))),FreeQ(a,x)))),SameQ(u,ExpandIntegrand(u,x)))),
ISetDelayed(523,$($s("§substpower"),$p("§fx"),x_Symbol,$p(n, Integer)),
    If(AtomQ($s("§fx")),If(SameQ($s("§fx"),x),Power(x,n),$s("§fx")),If(And(PowerQ($s("§fx")),SameQ(Part($s("§fx"),C1),x),FreeQ(Part($s("§fx"),C2),x)),Power(x,Times(n,Part($s("§fx"),C2))),Map(Function($($s("§substpower"),Slot1,x,n)),$s("§fx"))))),
ISetDelayed(524,InertTrigQ(f_),
    MemberQ(List($s("§sin"),$s("§cos"),$s("§tan"),$s("§cot"),$s("§sec"),$s("§csc")),f)),
ISetDelayed(525,InertTrigQ(f_,g_),
    If(SameQ(f,g),InertTrigQ(f),Or(InertReciprocalQ(f,g),InertReciprocalQ(g,f)))),
ISetDelayed(526,InertTrigQ(f_,g_,h_),
    And(InertTrigQ(f,g),InertTrigQ(g,h))),
ISetDelayed(527,InertReciprocalQ(f_,g_),
    Or(And(SameQ(f,$s("§sin")),SameQ(g,$s("§csc"))),And(SameQ(f,$s("§cos")),SameQ(g,$s("§sec"))),And(SameQ(f,$s("§tan")),SameQ(g,$s("§cot"))))),
ISetDelayed(528,InertTrigFreeQ(u_),
    And(FreeQ(u,$s("§sin")),FreeQ(u,$s("§cos")),FreeQ(u,$s("§tan")),FreeQ(u,$s("§cot")),FreeQ(u,$s("§sec")),FreeQ(u,$s("§csc")))),
ISetDelayed(529,ActivateTrig(u_),
    ReplaceAll(u,List(Rule($s("§sin"),Sin),Rule($s("§cos"),Cos),Rule($s("§tan"),Tan),Rule($s("§cot"),Cot),Rule($s("§sec"),Sec),Rule($s("§csc"),Csc)))),
ISetDelayed(530,DeactivateTrig(Times(Power(Plus(a_DEFAULT,Times($($p("§trig"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT)),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_),
    Condition(Times(Power(Plus(c,Times(d,x)),m),Power(Plus(a,Times(b,DeactivateTrig($($s("§trig"),Plus(e,Times(f,x))),x))),n)),And(FreeQ(List(a,b,c,d,e,f,m,n),x),Or(TrigQ($s("§trig")),HyperbolicQ($s("§trig")))))),
ISetDelayed(531,DeactivateTrig(u_,x_),
    UnifyInertTrigFunction(FixInertTrigFunction(DeactivateTrigAux(u,x),x),x)),
ISetDelayed(532,DeactivateTrigAux(u_,x_),
    If(AtomQ(u),u,If(And(TrigQ(u),LinearQ(Part(u,C1),x)),With(list(Set(v,ExpandToSum(Part(u,C1),x))),Switch(Head(u),Sin,ReduceInertTrig($s("§sin"),v),Cos,ReduceInertTrig($s("§cos"),v),Tan,ReduceInertTrig($s("§tan"),v),Cot,ReduceInertTrig($s("§cot"),v),Sec,ReduceInertTrig($s("§sec"),v),Csc,ReduceInertTrig($s("§csc"),v))),If(And(HyperbolicQ(u),LinearQ(Part(u,C1),x)),With(list(Set(v,ExpandToSum(Times(CI,Part(u,C1)),x))),Switch(Head(u),Sinh,Times(CN1,CI,ReduceInertTrig($s("§sin"),v)),Cosh,ReduceInertTrig($s("§cos"),v),Tanh,Times(CN1,CI,ReduceInertTrig($s("§tan"),v)),Coth,Times(CI,ReduceInertTrig($s("§cot"),v)),Sech,ReduceInertTrig($s("§sec"),v),Csch,Times(CI,ReduceInertTrig($s("§csc"),v)))),Map(Function(DeactivateTrigAux(Slot1,x)),u))))),
ISetDelayed(533,FixInertTrigFunction(Times(a_,u_),x_),
    Condition(Times(a,FixInertTrigFunction(u,x)),FreeQ(a,x)))
  );
}
