package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class UtilityFunctions22 { 
  public static IAST RULES = List( 
ISetDelayed(273,TrigSimplifyAux(Times(Power($($s("§csc"),v_),m_DEFAULT),Power(Plus(a_DEFAULT,Times(Power($($s("§cot"),v_),n_DEFAULT),b_DEFAULT),Times(Power($($s("§csc"),v_),n_DEFAULT),c_DEFAULT)),p_DEFAULT),u_DEFAULT)),
    Condition(Times(u,Power(Csc(v),Plus(m,Times(n,p))),Power(Plus(c,Times(b,Power(Cos(v),n)),Times(a,Power(Sin(v),n))),p)),IntegersQ(m,n,p))),
ISetDelayed(274,TrigSimplifyAux(Times(Power(Plus(Times(Power($($s("§csc"),v_),m_DEFAULT),a_DEFAULT),Times(Power($($s("§sin"),v_),n_DEFAULT),b_DEFAULT)),p_DEFAULT),u_DEFAULT)),
    Condition(If(And(EqQ(Subtract(Plus(m,n),C2),C0),EqQ(Plus(a,b),C0)),Times(u,Power(Times(a,Sqr(Cos(v)),Power(Power(Sin(v),m),CN1)),p)),Times(u,Power(Times(Plus(a,Times(b,Power(Sin(v),Plus(m,n)))),Power(Power(Sin(v),m),CN1)),p))),IntegersQ(m,n))),
ISetDelayed(275,TrigSimplifyAux(Times(Power(Plus(Times(Power($($s("§sec"),v_),m_DEFAULT),a_DEFAULT),Times(Power($($s("§cos"),v_),n_DEFAULT),b_DEFAULT)),p_DEFAULT),u_DEFAULT)),
    Condition(If(And(EqQ(Subtract(Plus(m,n),C2),C0),EqQ(Plus(a,b),C0)),Times(u,Power(Times(a,Sqr(Sin(v)),Power(Power(Cos(v),m),CN1)),p)),Times(u,Power(Times(Plus(a,Times(b,Power(Cos(v),Plus(m,n)))),Power(Power(Cos(v),m),CN1)),p))),IntegersQ(m,n))),
ISetDelayed(276,TrigSimplifyAux(u_),
    u),
ISetDelayed(277,TrigSimplifyAux(Times(Power(Times(Power($($s("§tan"),v_),n_DEFAULT),Power($($s("§tan"),w_),n_DEFAULT),c_DEFAULT),p_DEFAULT),u_DEFAULT)),
    Condition(Times(u,Power(Power(Plus(Negate(c),Times(c,$($s("§sec"),w))),n),p)),And(IntegerQ(n),EqQ(w,Times(C2,v))))),
ISetDelayed(278,IntPart(Times(m_,u_),Optional(n_,C1)),
    Condition(IntPart(u,Times(m,n)),RationalQ(m))),
ISetDelayed(279,IntPart(u_,Optional(n_,C1)),
    If(RationalQ(u),IntegerPart(Times(n,u)),If(SumQ(u),Map(Function(IntPart(Slot1,n)),u),C0))),
ISetDelayed(280,FracPart(Times(m_,u_),Optional(n_,C1)),
    Condition(FracPart(u,Times(m,n)),RationalQ(m))),
ISetDelayed(281,FracPart(u_,Optional(n_,C1)),
    If(RationalQ(u),FractionalPart(Times(n,u)),If(SumQ(u),Map(Function(FracPart(Slot1,n)),u),Times(n,u)))),
ISetDelayed(282,ContentFactor($p("expn")),
    TimeConstrained(ContentFactorAux($s("expn")),$s("§$timelimit"),$s("expn"))),
ISetDelayed(283,ContentFactorAux($p("expn")),
    If(AtomQ($s("expn")),$s("expn"),If(IntegerPowerQ($s("expn")),If(And(SumQ(Part($s("expn"),C1)),Less(NumericFactor(Part($s("expn"),C1,C1)),C0)),Times(Power(CN1,Part($s("expn"),C2)),Power(ContentFactorAux(Negate(Part($s("expn"),C1))),Part($s("expn"),C2))),Power(ContentFactorAux(Part($s("expn"),C1)),Part($s("expn"),C2))),If(ProductQ($s("expn")),Module(list(Set($s("num"),C1),$s("tmp")),CompoundExpression(Set($s("tmp"),Map(Function(If(And(SumQ(Slot1),Less(NumericFactor(Part(Slot1,C1)),C0)),CompoundExpression(Set($s("num"),Negate($s("num"))),ContentFactorAux(Negate(Slot1))),ContentFactorAux(Slot1))),$s("expn"))),Times($s("num"),UnifyNegativeBaseFactors($s("tmp"))))),If(SumQ($s("expn")),With(list(Set($s("lst"),CommonFactors(Apply(List,$s("expn"))))),If(Or(SameQ(Part($s("lst"),C1),C1),SameQ(Part($s("lst"),C1),CN1)),$s("expn"),Times(Part($s("lst"),C1),Apply(Plus,Rest($s("lst")))))),$s("expn")))))),
ISetDelayed(284,UnifyNegativeBaseFactors(Times(u_DEFAULT,Power(Negate(v_),m_),Power(v_,n_DEFAULT))),
    Condition(UnifyNegativeBaseFactors(Times(Power(CN1,n),u,Power(Negate(v),Plus(m,n)))),IntegerQ(n))),
ISetDelayed(285,UnifyNegativeBaseFactors(u_),
    u),
ISetDelayed(286,CommonFactors($p("lst")),
    Module(List($s("lst1"),$s("lst2"),$s("lst3"),$s("lst4"),$s("common"),$s("base"),$s("num")),CompoundExpression(Set($s("lst1"),Map($rubi("NonabsurdNumberFactors"),$s("lst"))),Set($s("lst2"),Map($rubi("AbsurdNumberFactors"),$s("lst"))),Set($s("num"),Apply($rubi("AbsurdNumberGCD"),$s("lst2"))),Set($s("common"),$s("num")),Set($s("lst2"),Map(Function(Times(Slot1,Power($s("num"),CN1))),$s("lst2"))),While(True,CompoundExpression(Set($s("lst3"),Map($rubi("LeadFactor"),$s("lst1"))),If(Apply(SameQ,$s("lst3")),CompoundExpression(Set($s("common"),Times($s("common"),Part($s("lst3"),C1))),Set($s("lst1"),Map($rubi("RemainingFactors"),$s("lst1")))),If(And(EveryQ(Function(And(LogQ(Slot1),IntegerQ(First(Slot1)),Greater(First(Slot1),C0))),$s("lst3")),EveryQ($rubi("RationalQ"),Set($s("lst4"),Map(Function(FullSimplify(Times(Slot1,Power(First($s("lst3")),CN1)))),$s("lst3"))))),CompoundExpression(Set($s("num"),Apply(GCD,$s("lst4"))),Set($s("common"),Times($s("common"),Log(Power(Part(First($s("lst3")),C1),$s("num"))))),Set($s("lst2"),Map2(Function(Times(Slot1,Slot2,Power($s("num"),CN1))),$s("lst2"),$s("lst4"))),Set($s("lst1"),Map($rubi("RemainingFactors"),$s("lst1")))),CompoundExpression(Set($s("lst4"),Map($rubi("LeadDegree"),$s("lst1"))),If(And(Apply(SameQ,Map($rubi("LeadBase"),$s("lst1"))),EveryQ($rubi("RationalQ"),$s("lst4"))),CompoundExpression(Set($s("num"),Smallest($s("lst4"))),Set($s("base"),LeadBase(Part($s("lst1"),C1))),If(Unequal($s("num"),C0),Set($s("common"),Times($s("common"),Power($s("base"),$s("num"))))),Set($s("lst2"),Map2(Function(Times(Slot1,Power($s("base"),Subtract(Slot2,$s("num"))))),$s("lst2"),$s("lst4"))),Set($s("lst1"),Map($rubi("RemainingFactors"),$s("lst1")))),If(And(Equal(Length($s("lst1")),C2),EqQ(Plus(LeadBase(Part($s("lst1"),C1)),LeadBase(Part($s("lst1"),C2))),C0),NeQ(Part($s("lst1"),C1),C1),IntegerQ(Part($s("lst4"),C1)),FractionQ(Part($s("lst4"),C2))),CompoundExpression(Set($s("num"),Min($s("lst4"))),Set($s("base"),LeadBase(Part($s("lst1"),C2))),If(Unequal($s("num"),C0),Set($s("common"),Times($s("common"),Power($s("base"),$s("num"))))),Set($s("lst2"),list(Times(Part($s("lst2"),C1),Power(CN1,Part($s("lst4"),C1))),Part($s("lst2"),C2))),Set($s("lst2"),Map2(Function(Times(Slot1,Power($s("base"),Subtract(Slot2,$s("num"))))),$s("lst2"),$s("lst4"))),Set($s("lst1"),Map($rubi("RemainingFactors"),$s("lst1")))),If(And(Equal(Length($s("lst1")),C2),EqQ(Plus(LeadBase(Part($s("lst1"),C1)),LeadBase(Part($s("lst1"),C2))),C0),NeQ(Part($s("lst1"),C2),C1),IntegerQ(Part($s("lst4"),C2)),FractionQ(Part($s("lst4"),C1))),CompoundExpression(Set($s("num"),Min($s("lst4"))),Set($s("base"),LeadBase(Part($s("lst1"),C1))),If(Unequal($s("num"),C0),Set($s("common"),Times($s("common"),Power($s("base"),$s("num"))))),Set($s("lst2"),list(Part($s("lst2"),C1),Times(Part($s("lst2"),C2),Power(CN1,Part($s("lst4"),C2))))),Set($s("lst2"),Map2(Function(Times(Slot1,Power($s("base"),Subtract(Slot2,$s("num"))))),$s("lst2"),$s("lst4"))),Set($s("lst1"),Map($rubi("RemainingFactors"),$s("lst1")))),CompoundExpression(Set($s("num"),MostMainFactorPosition($s("lst3"))),Set($s("lst2"),ReplacePart($s("lst2"),Times(Part($s("lst3"),$s("num")),Part($s("lst2"),$s("num"))),$s("num"))),Set($s("lst1"),ReplacePart($s("lst1"),RemainingFactors(Part($s("lst1"),$s("num"))),$s("num")))))))))),If(EveryQ(Function(SameQ(Slot1,C1)),$s("lst1")),Return(Prepend($s("lst2"),$s("common"))))))))),
ISetDelayed(287,MostMainFactorPosition($p("lst",List)),
    Module(list(Set($s("§factor"),C1),Set($s("num"),C1),$s("ii")),CompoundExpression(Do(If(Greater(FactorOrder(Part($s("lst"),$s("ii")),$s("§factor")),C0),CompoundExpression(Set($s("§factor"),Part($s("lst"),$s("ii"))),Set($s("num"),$s("ii")))),list($s("ii"),Length($s("lst")))),$s("num"))))
  );
}
