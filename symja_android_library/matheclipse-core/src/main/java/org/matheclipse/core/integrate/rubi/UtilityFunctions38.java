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
ISetDelayed(689,FixIntRule($p("§rule")),
    If(AtomQ(Part($s("§rule"),C1,C1,CN1)),FixIntRule($s("§rule"),Part($s("§rule"),C1,C1,CN1)),If(And(SameQ(Head(Part($s("§rule"),C1,C1,CN1)),Pattern),AtomQ(Part($s("§rule"),C1,C1,CN1,C1))),FixIntRule($s("§rule"),Part($s("§rule"),C1,C1,CN1,C1)),Print($str("Invalid integration rule: "),Part($s("§rule"),C1,C1,CN1))))),
ISetDelayed(690,FixIntRule(RuleDelayed($p("lhs"),$(F_,$(G_,$p("§list"),$(F_,Plus(u_,v_),$p("test2"))),$p("test1"))),x_),
    Condition(ReplacePart(RuleDelayed($s("lhs"),F(G($s("§list"),F(Plus(u,v),$s("test2"))),$s("test1"))),list(Rule(List(C2,C1,C2,C1,C1),FixRhsIntRule(u,x)),Rule(List(C2,C1,C2,C1,C2),FixRhsIntRule(v,x)))),And(SameQ(FSymbol,Condition),Or(SameQ(GSymbol,With),SameQ(GSymbol,Module),SameQ(GSymbol,Block))))),
ISetDelayed(691,FixIntRule(RuleDelayed($p("lhs"),$(G_,$p("§list"),$(F_,Plus(u_,v_),$p("test2")))),x_),
    Condition(ReplacePart(RuleDelayed($s("lhs"),G($s("§list"),F(Plus(u,v),$s("test2")))),list(Rule(List(C2,C2,C1,C1),FixRhsIntRule(u,x)),Rule(List(C2,C2,C1,C2),FixRhsIntRule(v,x)))),And(SameQ(FSymbol,Condition),Or(SameQ(GSymbol,With),SameQ(GSymbol,Module),SameQ(GSymbol,Block))))),
ISetDelayed(692,FixIntRule(RuleDelayed($p("lhs"),$(F_,$(G_,$p("§list"),Plus(u_,v_)),$p("test"))),x_),
    Condition(ReplacePart(RuleDelayed($s("lhs"),F(G($s("§list"),Plus(u,v)),$s("test"))),list(Rule(List(C2,C1,C2,C1),FixRhsIntRule(u,x)),Rule(List(C2,C1,C2,C2),FixRhsIntRule(v,x)))),And(SameQ(FSymbol,Condition),Or(SameQ(GSymbol,With),SameQ(GSymbol,Module),SameQ(GSymbol,Block))))),
ISetDelayed(693,FixIntRule(RuleDelayed($p("lhs"),$(G_,$p("§list"),Plus(u_,v_))),x_),
    Condition(ReplacePart(RuleDelayed($s("lhs"),G($s("§list"),Plus(u,v))),list(Rule(list(C2,C2,C1),FixRhsIntRule(u,x)),Rule(list(C2,C2,C2),FixRhsIntRule(v,x)))),Or(SameQ(GSymbol,With),SameQ(GSymbol,Module),SameQ(GSymbol,Block)))),
ISetDelayed(694,FixIntRule(RuleDelayed($p("lhs"),$(F_,Plus(u_,v_),$p("test"))),x_),
    Condition(ReplacePart(RuleDelayed($s("lhs"),F(Plus(u,v),$s("test"))),list(Rule(list(C2,C1,C1),FixRhsIntRule(u,x)),Rule(list(C2,C1,C2),FixRhsIntRule(v,x)))),SameQ(FSymbol,Condition))),
ISetDelayed(695,FixIntRule(RuleDelayed($p("lhs"),Plus(u_,v_)),x_),
    ReplacePart(RuleDelayed($s("lhs"),Plus(u,v)),list(Rule(list(C2,C1),FixRhsIntRule(u,x)),Rule(list(C2,C2),FixRhsIntRule(v,x))))),
ISetDelayed(696,FixIntRule(RuleDelayed($p("lhs"),$(F_,$(G_,$p("list1"),$(F_,$($p("H"),$p("list2"),u_),$p("test2"))),$p("test1"))),x_),
    Condition(ReplacePart(RuleDelayed($s("lhs"),F(G($s("list1"),F(H($s("list2"),u),$s("test2"))),$s("test1"))),Rule(List(C2,C1,C2,C1,C2),FixRhsIntRule(u,x))),And(SameQ(FSymbol,Condition),Or(SameQ(GSymbol,With),SameQ(GSymbol,Module),SameQ(GSymbol,Block)),Or(SameQ($s("H"),With),SameQ($s("H"),Module),SameQ($s("H"),Block))))),
ISetDelayed(697,FixIntRule(RuleDelayed($p("lhs"),$(F_,$(G_,$p("list1"),$($p("H"),$p("list2"),u_)),$p("test1"))),x_),
    Condition(ReplacePart(RuleDelayed($s("lhs"),F(G($s("list1"),H($s("list2"),u)),$s("test1"))),Rule(List(C2,C1,C2,C2),FixRhsIntRule(u,x))),And(SameQ(FSymbol,Condition),Or(SameQ(GSymbol,With),SameQ(GSymbol,Module),SameQ(GSymbol,Block)),Or(SameQ($s("H"),With),SameQ($s("H"),Module),SameQ($s("H"),Block))))),
ISetDelayed(698,FixIntRule(RuleDelayed($p("lhs"),$(F_,$(G_,$p("§list"),$(F_,$($p("H"),$p("str1"),$p("str2"),$p("str3"),$($p("J"),u_)),$p("test2"))),$p("test1"))),x_),
    Condition(ReplacePart(RuleDelayed($s("lhs"),F(G($s("§list"),F(H($s("str1"),$s("str2"),$s("str3"),J(u)),$s("test2"))),$s("test1"))),Rule(List(C2,C1,C2,C1,C4,C1),FixRhsIntRule(u,x))),And(SameQ(FSymbol,Condition),Or(SameQ(GSymbol,With),SameQ(GSymbol,Module),SameQ(GSymbol,Block)),SameQ($s("H"),$s("§showstep")),SameQ($s("J"),Hold)))),
ISetDelayed(699,FixIntRule(RuleDelayed($p("lhs"),$(F_,$(G_,$p("§list"),$(F_,u_,$p("test2"))),$p("test1"))),x_),
    Condition(ReplacePart(RuleDelayed($s("lhs"),F(G($s("§list"),F(u,$s("test2"))),$s("test1"))),Rule(List(C2,C1,C2,C1),FixRhsIntRule(u,x))),And(SameQ(FSymbol,Condition),Or(SameQ(GSymbol,With),SameQ(GSymbol,Module),SameQ(GSymbol,Block))))),
ISetDelayed(700,FixIntRule(RuleDelayed($p("lhs"),$(G_,$p("§list"),$(F_,u_,$p("test2")))),x_),
    Condition(ReplacePart(RuleDelayed($s("lhs"),G($s("§list"),F(u,$s("test2")))),Rule(list(C2,C2,C1),FixRhsIntRule(u,x))),And(SameQ(FSymbol,Condition),Or(SameQ(GSymbol,With),SameQ(GSymbol,Module),SameQ(GSymbol,Block))))),
ISetDelayed(701,FixIntRule(RuleDelayed($p("lhs"),$(F_,$(G_,$p("§list"),u_),$p("test"))),x_),
    Condition(ReplacePart(RuleDelayed($s("lhs"),F(G($s("§list"),u),$s("test"))),Rule(list(C2,C1,C2),FixRhsIntRule(u,x))),And(SameQ(FSymbol,Condition),Or(SameQ(GSymbol,With),SameQ(GSymbol,Module),SameQ(GSymbol,Block))))),
ISetDelayed(702,FixIntRule(RuleDelayed($p("lhs"),$(G_,$p("§list"),u_)),x_),
    Condition(ReplacePart(RuleDelayed($s("lhs"),G($s("§list"),u)),Rule(list(C2,C2),FixRhsIntRule(u,x))),Or(SameQ(GSymbol,With),SameQ(GSymbol,Module),SameQ(GSymbol,Block)))),
ISetDelayed(703,FixIntRule(RuleDelayed($p("lhs"),$(F_,u_,$p("test"))),x_),
    Condition(ReplacePart(RuleDelayed($s("lhs"),F(u,$s("test"))),Rule(list(C2,C1),FixRhsIntRule(u,x))),SameQ(FSymbol,Condition))),
ISetDelayed(704,FixIntRule(RuleDelayed($p("lhs"),u_),x_),
    ReplacePart(RuleDelayed($s("lhs"),u),Rule(list(C2),FixRhsIntRule(u,x)))),
ISetDelayed(705,FixRhsIntRule(Plus(u_,v_),x_),
    Plus(FixRhsIntRule(u,x),FixRhsIntRule(v,x))),
ISetDelayed(706,FixRhsIntRule(Subtract(u_,v_),x_),
    Subtract(FixRhsIntRule(u,x),FixRhsIntRule(v,x))),
ISetDelayed(707,FixRhsIntRule(Negate(u_),x_),
    Negate(FixRhsIntRule(u,x)))
  );
}
