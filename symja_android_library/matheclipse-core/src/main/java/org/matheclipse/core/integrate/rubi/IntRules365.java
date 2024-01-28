package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.CannotIntegrate;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class IntRules365 { 
  public static IAST RULES = List( 
IIntegrate(7300,Integrate(u_,x_),
          CannotIntegrate(u, x))
//IIntegrate(7301,Int(e_,x_,$p("flag")),
//    Condition($($s("flag"),Integrate(e,x)),CompoundExpression(Message(MessageName(Integrate,$str("§oldflag")),$s("flag")),True)))
  );
}
