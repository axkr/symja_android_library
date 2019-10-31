package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *  
 */
public class UtilityFunctions0 { 
  public static IAST RULES = List( 
ISetDelayed(1,IntHide(u_,x_Symbol),
    Block(List(Set($s("§$showsteps"),False),Set($s("§$stepcounter"),Null)),Int(u,x))),
ISetDelayed(2,EveryQ($p("func"),$p("lst")),
    Catch(CompoundExpression(Scan(Function(If($($s("func"),Slot1),Null,Throw(False))),$s("lst")),True))),
ISetDelayed(3,Map2($p("func"),$p("lst1"),$p("lst2")),
    Module(List($s("ii")),ReapList(Do(Sow($($s("func"),Part($s("lst1"),$s("ii")),Part($s("lst2"),$s("ii")))),List($s("ii"),Length($s("lst1"))))))),
ISetDelayed(4,ReapList(u_),
    With(List(Set($s("lst"),Part(Reap(u),C2))),If(SameQ($s("lst"),List()),$s("lst"),Part($s("lst"),C1)))),
ISetDelayed(5,HalfIntegerQ($ps("u")),
    SameQ(Scan(Function(If(And(SameQ(Head(Slot1),Rational),Equal(Denominator(Slot1),C2)),Null,Return(False))),List(u)),Null)),
ISetDelayed(6,RationalQ($ps("u")),
    SameQ(Scan(Function(If(Or(IntegerQ(Slot1),SameQ(Head(Slot1),Rational)),Null,Return(False))),List(u)),Null)),
ISetDelayed(7,FractionOrNegativeQ($ps("u")),
    SameQ(Scan(Function(If(Or(FractionQ(Slot1),And(IntegerQ(Slot1),Less(Slot1,C0))),Null,Return(False))),List(u)),Null))
  );
}
