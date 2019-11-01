package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *  
 */
public class UtilityFunctions39 { 
  public static IAST RULES = List( 
ISetDelayed(708,FixRhsIntRule(Times(a_,u_),x_),
    Condition(Dist(a,u,x),MemberQ(List(Integrate,$rubi("Subst")),Head(Unevaluated(u))))),
ISetDelayed(709,FixRhsIntRule(u_,x_),
    If(And(SameQ(Head(Unevaluated(u)),$rubi("Dist")),Equal(Length(Unevaluated(u)),C2)),Insert(Unevaluated(u),x,C3),If(MemberQ(List(Integrate,$rubi("Unintegrable"),$rubi("CannotIntegrate"),$rubi("Subst"),$rubi("Simp"),$rubi("Dist")),Head(Unevaluated(u))),u,Simp(u,x))))
  );
}
