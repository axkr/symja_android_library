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
ISetDelayed(494,SmartDenominator(Power(u_,n_)),
    Condition(SmartNumerator(Power(u,Negate(n))),And(RationalQ(n),Less(n,C0)))),
ISetDelayed(495,SmartDenominator(Times(u_,v_)),
    Times(SmartDenominator(u),SmartDenominator(v))),
ISetDelayed(496,SmartDenominator(u_),
    Denominator(u)),
ISetDelayed(497,SubstFor(w_,v_,u_,x_),
    SimplifyIntegrand(Times(w,SubstFor(v,u,x)),x)),
ISetDelayed(498,SubstFor(v_,u_,x_),
    If(AtomQ(v),Subst(u,v,x),If(Not(InertTrigFreeQ(u)),SubstFor(v,ActivateTrig(u),x),If(NeQ(FreeFactors(v,x),C1),SubstFor(NonfreeFactors(v,x),u,Times(x,Power(FreeFactors(v,x),CN1))),Switch(Head(v),Sin,SubstForTrig(u,x,Sqrt(Subtract(C1,Sqr(x))),Part(v,C1),x),Cos,SubstForTrig(u,Sqrt(Subtract(C1,Sqr(x))),x,Part(v,C1),x),Tan,SubstForTrig(u,Times(x,Power(Plus(C1,Sqr(x)),CN1D2)),Power(Plus(C1,Sqr(x)),CN1D2),Part(v,C1),x),Cot,SubstForTrig(u,Power(Plus(C1,Sqr(x)),CN1D2),Times(x,Power(Plus(C1,Sqr(x)),CN1D2)),Part(v,C1),x),Sec,SubstForTrig(u,Power(Subtract(C1,Sqr(x)),CN1D2),Power(x,CN1),Part(v,C1),x),Csc,SubstForTrig(u,Power(x,CN1),Power(Subtract(C1,Sqr(x)),CN1D2),Part(v,C1),x),Sinh,SubstForHyperbolic(u,x,Sqrt(Plus(C1,Sqr(x))),Part(v,C1),x),Cosh,SubstForHyperbolic(u,Sqrt(Plus(CN1,Sqr(x))),x,Part(v,C1),x),Tanh,SubstForHyperbolic(u,Times(x,Power(Subtract(C1,Sqr(x)),CN1D2)),Power(Subtract(C1,Sqr(x)),CN1D2),Part(v,C1),x),Coth,SubstForHyperbolic(u,Power(Plus(CN1,Sqr(x)),CN1D2),Times(x,Power(Plus(CN1,Sqr(x)),CN1D2)),Part(v,C1),x),Sech,SubstForHyperbolic(u,Power(Plus(CN1,Sqr(x)),CN1D2),Power(x,CN1),Part(v,C1),x),Csch,SubstForHyperbolic(u,Power(x,CN1),Power(Plus(C1,Sqr(x)),CN1D2),Part(v,C1),x),$b(),SubstForAux(u,v,x)))))),
ISetDelayed(499,SubstForAux(u_,v_,x_),
    If(SameQ(u,v),x,If(AtomQ(u),If(And(PowerQ(v),FreeQ(Part(v,C2),x),EqQ(u,Part(v,C1))),Power(x,Simplify(Power(Part(v,C2),CN1))),u),If(And(PowerQ(u),FreeQ(Part(u,C2),x)),If(EqQ(Part(u,C1),v),Power(x,Part(u,C2)),If(And(PowerQ(v),FreeQ(Part(v,C2),x),EqQ(Part(u,C1),Part(v,C1))),Power(x,Simplify(Times(Part(u,C2),Power(Part(v,C2),CN1)))),Power(SubstForAux(Part(u,C1),v,x),Part(u,C2)))),If(And(ProductQ(u),NeQ(FreeFactors(u,x),C1)),Times(FreeFactors(u,x),SubstForAux(NonfreeFactors(u,x),v,x)),If(And(ProductQ(u),ProductQ(v)),SubstForAux(First(u),First(v),x),Map(Function(SubstForAux(Slot1,v,x)),u))))))),
ISetDelayed(500,SubstForTrig(u_,$p("§sin"),$p("§cos"),v_,x_),
    If(AtomQ(u),u,If(And(TrigQ(u),IntegerQuotientQ(Part(u,C1),v)),If(Or(SameQ(Part(u,C1),v),EqQ(Part(u,C1),v)),Switch(Head(u),Sin,$s("§sin"),Cos,$s("§cos"),Tan,Times($s("§sin"),Power($s("§cos"),CN1)),Cot,Times($s("§cos"),Power($s("§sin"),CN1)),Sec,Power($s("§cos"),CN1),Csc,Power($s("§sin"),CN1)),Map(Function(SubstForTrig(Slot1,$s("§sin"),$s("§cos"),v,x)),ReplaceAll(TrigExpand($(Head(u),Times(Simplify(Times(Part(u,C1),Power(v,CN1))),x))),Rule(x,v)))),If(And(ProductQ(u),SameQ(Head(Part(u,C1)),Cos),SameQ(Head(Part(u,C2)),Sin),EqQ(Part(u,C1,C1),Times(C1D2,v)),EqQ(Part(u,C2,C1),Times(C1D2,v))),Times(C1D2,$s("§sin"),SubstForTrig(Drop(u,C2),$s("§sin"),$s("§cos"),v,x)),Map(Function(SubstForTrig(Slot1,$s("§sin"),$s("§cos"),v,x)),u))))),
ISetDelayed(501,SubstForHyperbolic(u_,$p("§sinh"),$p("§cosh"),v_,x_),
    If(AtomQ(u),u,If(And(HyperbolicQ(u),IntegerQuotientQ(Part(u,C1),v)),If(Or(SameQ(Part(u,C1),v),EqQ(Part(u,C1),v)),Switch(Head(u),Sinh,$s("§sinh"),Cosh,$s("§cosh"),Tanh,Times($s("§sinh"),Power($s("§cosh"),CN1)),Coth,Times($s("§cosh"),Power($s("§sinh"),CN1)),Sech,Power($s("§cosh"),CN1),Csch,Power($s("§sinh"),CN1)),Map(Function(SubstForHyperbolic(Slot1,$s("§sinh"),$s("§cosh"),v,x)),ReplaceAll(TrigExpand($(Head(u),Times(Simplify(Times(Part(u,C1),Power(v,CN1))),x))),Rule(x,v)))),If(And(ProductQ(u),SameQ(Head(Part(u,C1)),Cosh),SameQ(Head(Part(u,C2)),Sinh),EqQ(Part(u,C1,C1),Times(C1D2,v)),EqQ(Part(u,C2,C1),Times(C1D2,v))),Times(C1D2,$s("§sinh"),SubstForHyperbolic(Drop(u,C2),$s("§sinh"),$s("§cosh"),v,x)),Map(Function(SubstForHyperbolic(Slot1,$s("§sinh"),$s("§cosh"),v,x)),u))))),
ISetDelayed(502,SubstForFractionalPowerOfLinear(u_,x_Symbol),
    With(list(Set($s("lst"),FractionalPowerOfLinear(u,C1,False,x))),If(Or(AtomQ($s("lst")),FalseQ(Part($s("lst"),C2))),False,With(list(Set(n,Part($s("lst"),C1)),Set(a,Coefficient(Part($s("lst"),C2),x,C0)),Set(b,Coefficient(Part($s("lst"),C2),x,C1))),With(list(Set($s("tmp"),Simplify(Times(Power(x,Subtract(n,C1)),SubstForFractionalPower(u,Part($s("lst"),C2),n,Plus(Times(CN1,a,Power(b,CN1)),Times(Power(x,n),Power(b,CN1))),x))))),List(NonfreeFactors($s("tmp"),x),n,Part($s("lst"),C2),Times(FreeFactors($s("tmp"),x),Power(b,CN1)))))))),
ISetDelayed(503,FractionalPowerOfLinear(u_,n_,v_,x_),
    If(Or(AtomQ(u),FreeQ(u,x)),list(n,v),If(CalculusQ(u),False,If(And(FractionalPowerQ(u),LinearQ(Part(u,C1),x),Or(FalseQ(v),EqQ(Part(u,C1),v))),list(LCM(Denominator(Part(u,C2)),n),Part(u,C1)),Catch(Module(list(Set($s("lst"),list(n,v))),CompoundExpression(Scan(Function(If(AtomQ(Set($s("lst"),FractionalPowerOfLinear(Slot1,Part($s("lst"),C1),Part($s("lst"),C2),x))),Throw(False))),u),$s("lst")))))))),
ISetDelayed(504,InverseFunctionOfLinear(u_,x_Symbol),
    If(Or(AtomQ(u),CalculusQ(u),FreeQ(u,x)),False,If(And(InverseFunctionQ(u),LinearQ(Part(u,C1),x)),u,Module(list($s("tmp")),Catch(CompoundExpression(Scan(Function(If(Not(AtomQ(Set($s("tmp"),InverseFunctionOfLinear(Slot1,x)))),Throw($s("tmp")))),u),False)))))),
ISetDelayed(505,TryPureTanSubst(u_,x_Symbol),
    Not(MatchQ(u,Condition($(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,$(G_,v_))))),And(FreeQ(list(a,b,c),x),MemberQ(List(ArcTan,ArcCot,ArcTanh,ArcCoth),FSymbol),MemberQ(List(Tan,Cot,Tanh,Coth),GSymbol),LinearQ(v,x)))))),
ISetDelayed(506,TryTanhSubst(u_,x_Symbol),
    And(FalseQ(FunctionOfLinear(u,x)),Not(MatchQ(u,Condition(Times(r_DEFAULT,Power(Plus(s_,t_),n_DEFAULT)),And(IntegerQ(n),Greater(n,C0))))),Not(MatchQ(u,Log(v_))),Not(MatchQ(u,Condition(Power(Plus(a_,Times(b_DEFAULT,Power($(f_,x),n_))),CN1),And(MemberQ(List(Sinh,Cosh,Sech,Csch),f),IntegerQ(n),Greater(n,C2))))),Not(MatchQ(u,Condition(Times($(f_,Times(m_DEFAULT,x)),$(g_,Times(n_DEFAULT,x))),And(IntegersQ(m,n),MemberQ(List(Sinh,Cosh,Sech,Csch),f),MemberQ(List(Sinh,Cosh,Sech,Csch),g))))),Not(MatchQ(u,Condition(Times(r_DEFAULT,Power(Times(a_DEFAULT,Power(s_,m_)),p_)),And(FreeQ(list(a,m,p),x),Not(And(SameQ(m,C2),Or(SameQ(s,Sech(x)),SameQ(s,Csch(x))))))))),SameQ(u,ExpandIntegrand(u,x)))),
ISetDelayed(507,TryPureTanhSubst(u_,x_Symbol),
    And(Not(MatchQ(u,Log(v_))),Not(MatchQ(u,Condition(ArcTanh(Times(a_DEFAULT,Tanh(v_))),FreeQ(a,x)))),Not(MatchQ(u,Condition(ArcTanh(Times(a_DEFAULT,Coth(v_))),FreeQ(a,x)))),Not(MatchQ(u,Condition(ArcCoth(Times(a_DEFAULT,Tanh(v_))),FreeQ(a,x)))),Not(MatchQ(u,Condition(ArcCoth(Times(a_DEFAULT,Coth(v_))),FreeQ(a,x)))),SameQ(u,ExpandIntegrand(u,x)))),
ISetDelayed(508,InertTrigQ(f_),
    MemberQ(List($s("§sin"),$s("§cos"),$s("§tan"),$s("§cot"),$s("§sec"),$s("§csc")),f)),
ISetDelayed(509,InertTrigQ(f_,g_),
    If(SameQ(f,g),InertTrigQ(f),Or(InertReciprocalQ(f,g),InertReciprocalQ(g,f)))),
ISetDelayed(510,InertTrigQ(f_,g_,h_),
    And(InertTrigQ(f,g),InertTrigQ(g,h))),
ISetDelayed(511,InertReciprocalQ(f_,g_),
    Or(And(SameQ(f,$s("§sin")),SameQ(g,$s("§csc"))),And(SameQ(f,$s("§cos")),SameQ(g,$s("§sec"))),And(SameQ(f,$s("§tan")),SameQ(g,$s("§cot"))))),
ISetDelayed(512,InertTrigFreeQ(u_),
    And(FreeQ(u,$s("§sin")),FreeQ(u,$s("§cos")),FreeQ(u,$s("§tan")),FreeQ(u,$s("§cot")),FreeQ(u,$s("§sec")),FreeQ(u,$s("§csc"))))
  );
}
