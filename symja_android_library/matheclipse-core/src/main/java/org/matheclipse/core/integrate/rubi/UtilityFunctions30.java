package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *  
 */
public class UtilityFunctions30 { 
  public static IAST RULES = List( 
ISetDelayed(681,SplitSum($p("func"),u_),
    If(SumQ(u),If(Not(AtomQ($($s("func"),First(u)))),List($($s("func"),First(u)),Rest(u)),With(List(Set($s("lst"),SplitSum($s("func"),Rest(u)))),If(AtomQ($s("lst")),False,List(Part($s("lst"),C1),Plus(First(u),Part($s("lst"),C2)))))),If(Not(AtomQ($($s("func"),u))),List($($s("func"),u),C0),False))),
ISetDelayed(682,IntSum(u_,x_Symbol),
    Plus(Simp(Times(FreeTerms(u,x),x),x),IntTerm(NonfreeTerms(u,x),x))),
ISetDelayed(683,IntTerm(u_,x_Symbol),
    Condition(Map(Function(IntTerm(Slot1,x)),u),SumQ(u))),
ISetDelayed(684,IntTerm(Times(c_DEFAULT,Power(v_,m_DEFAULT)),x_Symbol),
    Condition(With(List(Set(u,Cancel(v))),If(EqQ(m,CN1),Simp(Times(c,Log(RemoveContent(u,x)),Power(Coeff(u,x,C1),CN1)),x),If(And(EqQ(m,C1),EqQ(c,C1),SumQ(u)),IntSum(u,x),Simp(Times(c,Power(u,Plus(m,C1)),Power(Times(Coeff(u,x,C1),Plus(m,C1)),CN1)),x)))),And(FreeQ(List(c,m),x),LinearQ(v,x)))),
ISetDelayed(685,IntTerm(u_,x_Symbol),
    Dist(FreeFactors(u,x),Int(NonfreeFactors(u,x),x),x)),
ISetDelayed(686,RuleName($p("name")),
    CompoundExpression(AppendTo($s("§$rulenamelist"),$s("name")),Null)),
ISetDelayed(687,FixIntRules(),
    CompoundExpression(Set($($s("§downvalues"),Integrate),FixIntRules($($s("§downvalues"),Integrate))),Null)),
ISetDelayed(688,FixIntRules($p("rulelist")),
    Block(List(Integrate,$rubi("Subst"),$rubi("Simp"),$rubi("Dist")),CompoundExpression(SetAttributes(List($rubi("Simp"),$rubi("Dist"),Integrate,$rubi("Subst")),HoldAll),Map(Function(FixIntRule(Slot1,Part(Slot1,C1,C1,C2,C1))),$s("rulelist"))))),
ISetDelayed(689,FixIntRule($p("§rule")),
    If(AtomQ(Part($s("§rule"),C1,C1,CN1)),FixIntRule($s("§rule"),Part($s("§rule"),C1,C1,CN1)),If(And(SameQ(Head(Part($s("§rule"),C1,C1,CN1)),Pattern),AtomQ(Part($s("§rule"),C1,C1,CN1,C1))),FixIntRule($s("§rule"),Part($s("§rule"),C1,C1,CN1,C1)),Print($str("Invalid integration rule: "),Part($s("§rule"),C1,C1,CN1))))),
ISetDelayed(690,FixIntRule(RuleDelayed($p("lhs"),$(F_,$(G_,$p("§list"),$(F_,Plus(u_,v_),$p("test2"))),$p("test1"))),x_),
    Condition(ReplacePart(RuleDelayed($s("lhs"),F(G($s("§list"),F(Plus(u,v),$s("test2"))),$s("test1"))),List(Rule(List(C2,C1,C2,C1,C1),FixRhsIntRule(u,x)),Rule(List(C2,C1,C2,C1,C2),FixRhsIntRule(v,x)))),And(SameQ(FSymbol,Condition),Or(SameQ(GSymbol,With),SameQ(GSymbol,Module),SameQ(GSymbol,Block))))),
ISetDelayed(691,FixIntRule(RuleDelayed($p("lhs"),$(G_,$p("§list"),$(F_,Plus(u_,v_),$p("test2")))),x_),
    Condition(ReplacePart(RuleDelayed($s("lhs"),G($s("§list"),F(Plus(u,v),$s("test2")))),List(Rule(List(C2,C2,C1,C1),FixRhsIntRule(u,x)),Rule(List(C2,C2,C1,C2),FixRhsIntRule(v,x)))),And(SameQ(FSymbol,Condition),Or(SameQ(GSymbol,With),SameQ(GSymbol,Module),SameQ(GSymbol,Block))))),
ISetDelayed(692,FixIntRule(RuleDelayed($p("lhs"),$(F_,$(G_,$p("§list"),Plus(u_,v_)),$p("test"))),x_),
    Condition(ReplacePart(RuleDelayed($s("lhs"),F(G($s("§list"),Plus(u,v)),$s("test"))),List(Rule(List(C2,C1,C2,C1),FixRhsIntRule(u,x)),Rule(List(C2,C1,C2,C2),FixRhsIntRule(v,x)))),And(SameQ(FSymbol,Condition),Or(SameQ(GSymbol,With),SameQ(GSymbol,Module),SameQ(GSymbol,Block))))),
ISetDelayed(693,FixIntRule(RuleDelayed($p("lhs"),$(G_,$p("§list"),Plus(u_,v_))),x_),
    Condition(ReplacePart(RuleDelayed($s("lhs"),G($s("§list"),Plus(u,v))),List(Rule(List(C2,C2,C1),FixRhsIntRule(u,x)),Rule(List(C2,C2,C2),FixRhsIntRule(v,x)))),Or(SameQ(GSymbol,With),SameQ(GSymbol,Module),SameQ(GSymbol,Block)))),
ISetDelayed(694,FixIntRule(RuleDelayed($p("lhs"),$(F_,Plus(u_,v_),$p("test"))),x_),
    Condition(ReplacePart(RuleDelayed($s("lhs"),F(Plus(u,v),$s("test"))),List(Rule(List(C2,C1,C1),FixRhsIntRule(u,x)),Rule(List(C2,C1,C2),FixRhsIntRule(v,x)))),SameQ(FSymbol,Condition))),
ISetDelayed(695,FixIntRule(RuleDelayed($p("lhs"),Plus(u_,v_)),x_),
    ReplacePart(RuleDelayed($s("lhs"),Plus(u,v)),List(Rule(List(C2,C1),FixRhsIntRule(u,x)),Rule(List(C2,C2),FixRhsIntRule(v,x))))),
ISetDelayed(696,FixIntRule(RuleDelayed($p("lhs"),$(F_,$(G_,$p("list1"),$(F_,$($p("H"),$p("list2"),u_),$p("test2"))),$p("test1"))),x_),
    Condition(ReplacePart(RuleDelayed($s("lhs"),F(G($s("list1"),F(H($s("list2"),u),$s("test2"))),$s("test1"))),Rule(List(C2,C1,C2,C1,C2),FixRhsIntRule(u,x))),And(SameQ(FSymbol,Condition),Or(SameQ(GSymbol,With),SameQ(GSymbol,Module),SameQ(GSymbol,Block)),Or(SameQ($s("H"),With),SameQ($s("H"),Module),SameQ($s("H"),Block))))),
ISetDelayed(697,FixIntRule(RuleDelayed($p("lhs"),$(F_,$(G_,$p("list1"),$($p("H"),$p("list2"),u_)),$p("test1"))),x_),
    Condition(ReplacePart(RuleDelayed($s("lhs"),F(G($s("list1"),H($s("list2"),u)),$s("test1"))),Rule(List(C2,C1,C2,C2),FixRhsIntRule(u,x))),And(SameQ(FSymbol,Condition),Or(SameQ(GSymbol,With),SameQ(GSymbol,Module),SameQ(GSymbol,Block)),Or(SameQ($s("H"),With),SameQ($s("H"),Module),SameQ($s("H"),Block))))),
ISetDelayed(698,FixIntRule(RuleDelayed($p("lhs"),$(F_,$(G_,$p("§list"),$(F_,$($p("H"),$p("str1"),$p("str2"),$p("str3"),$($p("J"),u_)),$p("test2"))),$p("test1"))),x_),
    Condition(ReplacePart(RuleDelayed($s("lhs"),F(G($s("§list"),F(H($s("str1"),$s("str2"),$s("str3"),J(u)),$s("test2"))),$s("test1"))),Rule(List(C2,C1,C2,C1,C4,C1),FixRhsIntRule(u,x))),And(SameQ(FSymbol,Condition),Or(SameQ(GSymbol,With),SameQ(GSymbol,Module),SameQ(GSymbol,Block)),SameQ($s("H"),$s("§showstep")),SameQ($s("J"),Hold)))),
ISetDelayed(699,FixIntRule(RuleDelayed($p("lhs"),$(F_,$(G_,$p("§list"),$(F_,u_,$p("test2"))),$p("test1"))),x_),
    Condition(ReplacePart(RuleDelayed($s("lhs"),F(G($s("§list"),F(u,$s("test2"))),$s("test1"))),Rule(List(C2,C1,C2,C1),FixRhsIntRule(u,x))),And(SameQ(FSymbol,Condition),Or(SameQ(GSymbol,With),SameQ(GSymbol,Module),SameQ(GSymbol,Block))))),
ISetDelayed(700,FixIntRule(RuleDelayed($p("lhs"),$(G_,$p("§list"),$(F_,u_,$p("test2")))),x_),
    Condition(ReplacePart(RuleDelayed($s("lhs"),G($s("§list"),F(u,$s("test2")))),Rule(List(C2,C2,C1),FixRhsIntRule(u,x))),And(SameQ(FSymbol,Condition),Or(SameQ(GSymbol,With),SameQ(GSymbol,Module),SameQ(GSymbol,Block))))),
ISetDelayed(701,FixIntRule(RuleDelayed($p("lhs"),$(F_,$(G_,$p("§list"),u_),$p("test"))),x_),
    Condition(ReplacePart(RuleDelayed($s("lhs"),F(G($s("§list"),u),$s("test"))),Rule(List(C2,C1,C2),FixRhsIntRule(u,x))),And(SameQ(FSymbol,Condition),Or(SameQ(GSymbol,With),SameQ(GSymbol,Module),SameQ(GSymbol,Block))))),
ISetDelayed(702,FixIntRule(RuleDelayed($p("lhs"),$(G_,$p("§list"),u_)),x_),
    Condition(ReplacePart(RuleDelayed($s("lhs"),G($s("§list"),u)),Rule(List(C2,C2),FixRhsIntRule(u,x))),Or(SameQ(GSymbol,With),SameQ(GSymbol,Module),SameQ(GSymbol,Block)))),
ISetDelayed(703,FixIntRule(RuleDelayed($p("lhs"),$(F_,u_,$p("test"))),x_),
    Condition(ReplacePart(RuleDelayed($s("lhs"),F(u,$s("test"))),Rule(List(C2,C1),FixRhsIntRule(u,x))),SameQ(FSymbol,Condition)))
  );
}
