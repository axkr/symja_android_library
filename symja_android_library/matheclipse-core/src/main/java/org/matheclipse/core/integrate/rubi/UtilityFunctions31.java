package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *  
 */
public class UtilityFunctions31 { 
  public static IAST RULES = List( 
ISetDelayed(704,FixIntRule(RuleDelayed($p("lhs"),u_),x_),
    ReplacePart(RuleDelayed($s("lhs"),u),Rule(List(C2),FixRhsIntRule(u,x)))),
ISetDelayed(705,FixRhsIntRule(Plus(u_,v_),x_),
    Plus(FixRhsIntRule(u,x),FixRhsIntRule(v,x))),
ISetDelayed(706,FixRhsIntRule(Subtract(u_,v_),x_),
    Subtract(FixRhsIntRule(u,x),FixRhsIntRule(v,x))),
ISetDelayed(707,FixRhsIntRule(Negate(u_),x_),
    Negate(FixRhsIntRule(u,x))),
ISetDelayed(708,FixRhsIntRule(Times(a_,u_),x_),
    Condition(Dist(a,u,x),MemberQ(List(Integrate,$rubi("Subst")),Head(Unevaluated(u))))),
ISetDelayed(709,FixRhsIntRule(u_,x_),
    If(And(SameQ(Head(Unevaluated(u)),$rubi("Dist")),Equal(Length(Unevaluated(u)),C2)),Insert(Unevaluated(u),x,C3),If(MemberQ(List(Integrate,$rubi("Unintegrable"),$rubi("CannotIntegrate"),$rubi("Subst"),$rubi("Simp"),$rubi("Dist")),Head(Unevaluated(u))),u,Simp(u,x))))
  );
}
