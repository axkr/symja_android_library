package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *  
 */
public class UtilityFunctions13 { 
  public static IAST RULES = List( 
ISetDelayed(265,TrigSimplifyAux(Times(u_DEFAULT,Power($($s("§cot"),v_),n_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power($($s("§csc"),v_),n_DEFAULT))),CN1))),
    Condition(Times(u,Power(Cos(v),n),Power(Plus(b,Times(a,Power(Sin(v),n))),CN1)),And(IGtQ(n,C0),NonsumQ(a)))),
ISetDelayed(266,TrigSimplifyAux(Times(u_DEFAULT,Power(Plus(Times(a_DEFAULT,Power($($s("§sec"),v_),n_DEFAULT)),Times(b_DEFAULT,Power($($s("§tan"),v_),n_DEFAULT))),p_DEFAULT))),
    Condition(Times(u,Power(Sec(v),Times(n,p)),Power(Plus(a,Times(b,Power(Sin(v),n))),p)),IntegersQ(n,p))),
ISetDelayed(267,TrigSimplifyAux(Times(u_DEFAULT,Power(Plus(Times(b_DEFAULT,Power($($s("§cot"),v_),n_DEFAULT)),Times(a_DEFAULT,Power($($s("§csc"),v_),n_DEFAULT))),p_DEFAULT))),
    Condition(Times(u,Power(Csc(v),Times(n,p)),Power(Plus(a,Times(b,Power(Cos(v),n))),p)),IntegersQ(n,p))),
ISetDelayed(268,TrigSimplifyAux(Times(u_DEFAULT,Power(Plus(Times(b_DEFAULT,Power($($s("§sin"),v_),n_DEFAULT)),Times(a_DEFAULT,Power($($s("§tan"),v_),n_DEFAULT))),p_DEFAULT))),
    Condition(Times(u,Power(Tan(v),Times(n,p)),Power(Plus(a,Times(b,Power(Cos(v),n))),p)),IntegersQ(n,p))),
ISetDelayed(269,TrigSimplifyAux(Times(u_DEFAULT,Power(Plus(Times(b_DEFAULT,Power($($s("§cos"),v_),n_DEFAULT)),Times(a_DEFAULT,Power($($s("§cot"),v_),n_DEFAULT))),p_DEFAULT))),
    Condition(Times(u,Power(Cot(v),Times(n,p)),Power(Plus(a,Times(b,Power(Sin(v),n))),p)),IntegersQ(n,p))),
ISetDelayed(270,TrigSimplifyAux(Times(u_DEFAULT,Power($($s("§cos"),v_),m_DEFAULT),Power(Plus(a_DEFAULT,Times(c_DEFAULT,Power($($s("§sec"),v_),n_DEFAULT)),Times(b_DEFAULT,Power($($s("§tan"),v_),n_DEFAULT))),p_DEFAULT))),
    Condition(Times(u,Power(Cos(v),Subtract(m,Times(n,p))),Power(Plus(c,Times(b,Power(Sin(v),n)),Times(a,Power(Cos(v),n))),p)),IntegersQ(m,n,p))),
ISetDelayed(271,TrigSimplifyAux(Times(u_DEFAULT,Power($($s("§sec"),v_),m_DEFAULT),Power(Plus(a_DEFAULT,Times(c_DEFAULT,Power($($s("§sec"),v_),n_DEFAULT)),Times(b_DEFAULT,Power($($s("§tan"),v_),n_DEFAULT))),p_DEFAULT))),
    Condition(Times(u,Power(Sec(v),Plus(m,Times(n,p))),Power(Plus(c,Times(b,Power(Sin(v),n)),Times(a,Power(Cos(v),n))),p)),IntegersQ(m,n,p))),
ISetDelayed(272,TrigSimplifyAux(Times(u_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power($($s("§cot"),v_),n_DEFAULT)),Times(c_DEFAULT,Power($($s("§csc"),v_),n_DEFAULT))),p_DEFAULT),Power($($s("§sin"),v_),m_DEFAULT))),
    Condition(Times(u,Power(Sin(v),Subtract(m,Times(n,p))),Power(Plus(c,Times(b,Power(Cos(v),n)),Times(a,Power(Sin(v),n))),p)),IntegersQ(m,n,p))),
ISetDelayed(273,TrigSimplifyAux(Times(u_DEFAULT,Power($($s("§csc"),v_),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power($($s("§cot"),v_),n_DEFAULT)),Times(c_DEFAULT,Power($($s("§csc"),v_),n_DEFAULT))),p_DEFAULT))),
    Condition(Times(u,Power(Csc(v),Plus(m,Times(n,p))),Power(Plus(c,Times(b,Power(Cos(v),n)),Times(a,Power(Sin(v),n))),p)),IntegersQ(m,n,p))),
ISetDelayed(274,TrigSimplifyAux(Times(u_DEFAULT,Power(Plus(Times(a_DEFAULT,Power($($s("§csc"),v_),m_DEFAULT)),Times(b_DEFAULT,Power($($s("§sin"),v_),n_DEFAULT))),p_DEFAULT))),
    Condition(If(And(EqQ(Subtract(Plus(m,n),C2),C0),EqQ(Plus(a,b),C0)),Times(u,Power(Times(a,Sqr(Cos(v)),Power(Power(Sin(v),m),CN1)),p)),Times(u,Power(Times(Plus(a,Times(b,Power(Sin(v),Plus(m,n)))),Power(Power(Sin(v),m),CN1)),p))),IntegersQ(m,n))),
ISetDelayed(275,TrigSimplifyAux(Times(u_DEFAULT,Power(Plus(Times(b_DEFAULT,Power($($s("§cos"),v_),n_DEFAULT)),Times(a_DEFAULT,Power($($s("§sec"),v_),m_DEFAULT))),p_DEFAULT))),
    Condition(If(And(EqQ(Subtract(Plus(m,n),C2),C0),EqQ(Plus(a,b),C0)),Times(u,Power(Times(a,Sqr(Sin(v)),Power(Power(Cos(v),m),CN1)),p)),Times(u,Power(Times(Plus(a,Times(b,Power(Cos(v),Plus(m,n)))),Power(Power(Cos(v),m),CN1)),p))),IntegersQ(m,n))),
ISetDelayed(276,TrigSimplifyAux(u_),
    u),
ISetDelayed(277,TrigSimplifyAux(Times(u_DEFAULT,Power(Times(c_DEFAULT,Power($($s("§tan"),v_),n_DEFAULT),Power($($s("§tan"),w_),n_DEFAULT)),p_DEFAULT))),
    Condition(Times(u,Power(Power(Plus(Negate(c),Times(c,$($s("§sec"),w))),n),p)),And(IntegerQ(n),EqQ(w,Times(C2,v))))),
ISetDelayed(278,ContentFactor($p("expn")),
    TimeConstrained(ContentFactorAux($s("expn")),$s("§$timelimit"),$s("expn"))),
ISetDelayed(279,ContentFactorAux($p("expn")),
    If(AtomQ($s("expn")),$s("expn"),If(IntegerPowerQ($s("expn")),If(And(SumQ(Part($s("expn"),C1)),Less(NumericFactor(Part($s("expn"),C1,C1)),C0)),Times(Power(CN1,Part($s("expn"),C2)),Power(ContentFactorAux(Negate(Part($s("expn"),C1))),Part($s("expn"),C2))),Power(ContentFactorAux(Part($s("expn"),C1)),Part($s("expn"),C2))),If(ProductQ($s("expn")),Module(List(Set($s("num"),C1),$s("tmp")),CompoundExpression(Set($s("tmp"),Map(Function(If(And(SumQ(Slot1),Less(NumericFactor(Part(Slot1,C1)),C0)),CompoundExpression(Set($s("num"),Negate($s("num"))),ContentFactorAux(Negate(Slot1))),ContentFactorAux(Slot1))),$s("expn"))),Times($s("num"),UnifyNegativeBaseFactors($s("tmp"))))),If(SumQ($s("expn")),With(List(Set($s("lst"),CommonFactors(Apply(List,$s("expn"))))),If(Or(SameQ(Part($s("lst"),C1),C1),SameQ(Part($s("lst"),C1),CN1)),$s("expn"),Times(Part($s("lst"),C1),Apply(Plus,Rest($s("lst")))))),$s("expn")))))),
ISetDelayed(280,UnifyNegativeBaseFactors(Times(u_DEFAULT,Power(Negate(v_),m_),Power(v_,n_DEFAULT))),
    Condition(UnifyNegativeBaseFactors(Times(Power(CN1,n),u,Power(Negate(v),Plus(m,n)))),IntegerQ(n))),
ISetDelayed(281,UnifyNegativeBaseFactors(u_),
    u),
ISetDelayed(282,CommonFactors($p("lst")),
    Module(List($s("lst1"),$s("lst2"),$s("lst3"),$s("lst4"),$s("common"),$s("base"),$s("num")),CompoundExpression(Set($s("lst1"),Map($rubi("NonabsurdNumberFactors"),$s("lst"))),Set($s("lst2"),Map($rubi("AbsurdNumberFactors"),$s("lst"))),Set($s("num"),Apply($rubi("AbsurdNumberGCD"),$s("lst2"))),Set($s("common"),$s("num")),Set($s("lst2"),Map(Function(Times(Slot1,Power($s("num"),CN1))),$s("lst2"))),While(True,CompoundExpression(Set($s("lst3"),Map($rubi("LeadFactor"),$s("lst1"))),If(Apply(SameQ,$s("lst3")),CompoundExpression(Set($s("common"),Times($s("common"),Part($s("lst3"),C1))),Set($s("lst1"),Map($rubi("RemainingFactors"),$s("lst1")))),If(And(EveryQ(Function(And(LogQ(Slot1),IntegerQ(First(Slot1)),Greater(First(Slot1),C0))),$s("lst3")),EveryQ($rubi("RationalQ"),Set($s("lst4"),Map(Function(FullSimplify(Times(Slot1,Power(First($s("lst3")),CN1)))),$s("lst3"))))),CompoundExpression(Set($s("num"),Apply(GCD,$s("lst4"))),Set($s("common"),Times($s("common"),Log(Power(Part(First($s("lst3")),C1),$s("num"))))),Set($s("lst2"),Map2(Function(Times(Slot1,Slot2,Power($s("num"),CN1))),$s("lst2"),$s("lst4"))),Set($s("lst1"),Map($rubi("RemainingFactors"),$s("lst1")))),CompoundExpression(Set($s("lst4"),Map($rubi("LeadDegree"),$s("lst1"))),If(And(Apply(SameQ,Map($rubi("LeadBase"),$s("lst1"))),EveryQ($rubi("RationalQ"),$s("lst4"))),CompoundExpression(Set($s("num"),Smallest($s("lst4"))),Set($s("base"),LeadBase(Part($s("lst1"),C1))),If(Unequal($s("num"),C0),Set($s("common"),Times($s("common"),Power($s("base"),$s("num"))))),Set($s("lst2"),Map2(Function(Times(Slot1,Power($s("base"),Subtract(Slot2,$s("num"))))),$s("lst2"),$s("lst4"))),Set($s("lst1"),Map($rubi("RemainingFactors"),$s("lst1")))),If(And(Equal(Length($s("lst1")),C2),EqQ(Plus(LeadBase(Part($s("lst1"),C1)),LeadBase(Part($s("lst1"),C2))),C0),NeQ(Part($s("lst1"),C1),C1),IntegerQ(Part($s("lst4"),C1)),FractionQ(Part($s("lst4"),C2))),CompoundExpression(Set($s("num"),Min($s("lst4"))),Set($s("base"),LeadBase(Part($s("lst1"),C2))),If(Unequal($s("num"),C0),Set($s("common"),Times($s("common"),Power($s("base"),$s("num"))))),Set($s("lst2"),List(Times(Part($s("lst2"),C1),Power(CN1,Part($s("lst4"),C1))),Part($s("lst2"),C2))),Set($s("lst2"),Map2(Function(Times(Slot1,Power($s("base"),Subtract(Slot2,$s("num"))))),$s("lst2"),$s("lst4"))),Set($s("lst1"),Map($rubi("RemainingFactors"),$s("lst1")))),If(And(Equal(Length($s("lst1")),C2),EqQ(Plus(LeadBase(Part($s("lst1"),C1)),LeadBase(Part($s("lst1"),C2))),C0),NeQ(Part($s("lst1"),C2),C1),IntegerQ(Part($s("lst4"),C2)),FractionQ(Part($s("lst4"),C1))),CompoundExpression(Set($s("num"),Min($s("lst4"))),Set($s("base"),LeadBase(Part($s("lst1"),C1))),If(Unequal($s("num"),C0),Set($s("common"),Times($s("common"),Power($s("base"),$s("num"))))),Set($s("lst2"),List(Part($s("lst2"),C1),Times(Part($s("lst2"),C2),Power(CN1,Part($s("lst4"),C2))))),Set($s("lst2"),Map2(Function(Times(Slot1,Power($s("base"),Subtract(Slot2,$s("num"))))),$s("lst2"),$s("lst4"))),Set($s("lst1"),Map($rubi("RemainingFactors"),$s("lst1")))),CompoundExpression(Set($s("num"),MostMainFactorPosition($s("lst3"))),Set($s("lst2"),ReplacePart($s("lst2"),Times(Part($s("lst3"),$s("num")),Part($s("lst2"),$s("num"))),$s("num"))),Set($s("lst1"),ReplacePart($s("lst1"),RemainingFactors(Part($s("lst1"),$s("num"))),$s("num")))))))))),If(EveryQ(Function(SameQ(Slot1,C1)),$s("lst1")),Return(Prepend($s("lst2"),$s("common"))))))))),
ISetDelayed(283,MostMainFactorPosition($p("lst",List)),
    Module(List(Set($s("§factor"),C1),Set($s("num"),C1),$s("ii")),CompoundExpression(Do(If(Greater(FactorOrder(Part($s("lst"),$s("ii")),$s("§factor")),C0),CompoundExpression(Set($s("§factor"),Part($s("lst"),$s("ii"))),Set($s("num"),$s("ii")))),List($s("ii"),Length($s("lst")))),$s("num")))),
ISetDelayed(284,FactorOrder(u_,v_),
    If(SameQ(u,C1),If(SameQ(v,C1),C0,CN1),If(SameQ(v,C1),C1,Order(u,v)))),
ISetDelayed(285,Smallest($p("num1"),$p("num2")),
    If(Greater($s("num1"),C0),If(Greater($s("num2"),C0),Min($s("num1"),$s("num2")),C0),If(Greater($s("num2"),C0),C0,Max($s("num1"),$s("num2"))))),
ISetDelayed(286,Smallest($p("lst",List)),
    Module(List(Set($s("num"),Part($s("lst"),C1))),CompoundExpression(Scan(Function(Set($s("num"),Smallest($s("num"),Slot1))),Rest($s("lst"))),$s("num")))),
ISetDelayed(287,MonomialFactor(u_,x_Symbol),
    If(AtomQ(u),If(SameQ(u,x),List(C1,C1),List(C0,u)),If(PowerQ(u),If(IntegerQ(Part(u,C2)),With(List(Set($s("lst"),MonomialFactor(Part(u,C1),x))),List(Times(Part($s("lst"),C1),Part(u,C2)),Power(Part($s("lst"),C2),Part(u,C2)))),If(And(SameQ(Part(u,C1),x),FreeQ(Part(u,C2),x)),List(Part(u,C2),C1),List(C0,u))),If(ProductQ(u),With(List(Set($s("lst1"),MonomialFactor(First(u),x)),Set($s("lst2"),MonomialFactor(Rest(u),x))),List(Plus(Part($s("lst1"),C1),Part($s("lst2"),C1)),Times(Part($s("lst1"),C2),Part($s("lst2"),C2)))),If(SumQ(u),Module(List($s("lst"),$s("deg")),CompoundExpression(Set($s("lst"),Map(Function(MonomialFactor(Slot1,x)),Apply(List,u))),Set($s("deg"),Part($s("lst"),C1,C1)),Scan(Function(Set($s("deg"),MinimumDegree($s("deg"),Part(Slot1,C1)))),Rest($s("lst"))),If(Or(EqQ($s("deg"),C0),And(RationalQ($s("deg")),Less($s("deg"),C0))),List(C0,u),List($s("deg"),Apply(Plus,Map(Function(Times(Power(x,Subtract(Part(Slot1,C1),$s("deg"))),Part(Slot1,C2))),$s("lst"))))))),List(C0,u)))))),
ISetDelayed(288,MinimumDegree($p("deg1"),$p("deg2")),
    If(RationalQ($s("deg1")),If(RationalQ($s("deg2")),Min($s("deg1"),$s("deg2")),$s("deg1")),If(RationalQ($s("deg2")),$s("deg2"),With(List(Set($s("deg"),Simplify(Subtract($s("deg1"),$s("deg2"))))),If(RationalQ($s("deg")),If(Greater($s("deg"),C0),$s("deg2"),$s("deg1")),If(OrderedQ(List($s("deg1"),$s("deg2"))),$s("deg1"),$s("deg2"))))))),
ISetDelayed(289,ConstantFactor(u_,x_Symbol),
    If(FreeQ(u,x),List(u,C1),If(AtomQ(u),List(C1,u),If(And(PowerQ(u),FreeQ(Part(u,C2),x)),Module(List(Set($s("lst"),ConstantFactor(Part(u,C1),x)),$s("tmp")),If(IntegerQ(Part(u,C2)),List(Power(Part($s("lst"),C1),Part(u,C2)),Power(Part($s("lst"),C2),Part(u,C2))),CompoundExpression(Set($s("tmp"),PositiveFactors(Part($s("lst"),C1))),If(SameQ($s("tmp"),C1),List(C1,u),List(Power($s("tmp"),Part(u,C2)),Power(Times(NonpositiveFactors(Part($s("lst"),C1)),Part($s("lst"),C2)),Part(u,C2))))))),If(ProductQ(u),With(List(Set($s("lst"),Map(Function(ConstantFactor(Slot1,x)),Apply(List,u)))),List(Apply(Times,Map(First,$s("lst"))),Apply(Times,Map(Function(Part(Slot1,C2)),$s("lst"))))),If(SumQ(u),With(List(Set($s("lst1"),Map(Function(ConstantFactor(Slot1,x)),Apply(List,u)))),If(Apply(SameQ,Map(Function(Part(Slot1,C2)),$s("lst1"))),List(Apply(Plus,Map(First,$s("lst1"))),Part($s("lst1"),C1,C2)),With(List(Set($s("lst2"),CommonFactors(Map(First,$s("lst1"))))),List(First($s("lst2")),Apply(Plus,Map2(Times,Rest($s("lst2")),Map(Function(Part(Slot1,C2)),$s("lst1")))))))),List(C1,u)))))))
  );
}
