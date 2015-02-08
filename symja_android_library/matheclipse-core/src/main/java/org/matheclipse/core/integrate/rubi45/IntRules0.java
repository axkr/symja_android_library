package org.matheclipse.core.integrate.rubi45;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi45.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi45.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/** 
 * IndefiniteIntegrationRules from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *  
 */
public class IntRules0 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Power(x_,-1),x_Symbol),
    Log(x)),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,x_)),-1),x_Symbol),
    Condition(Times(Log(RemoveContent(Plus(a,Times(b,x)),x)),Power(b,-1)),FreeQ(List(a,b),x))),
ISetDelayed(Int(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),m_),x_Symbol),
    Condition(Times(Power(Plus(a,Times(b,x)),Plus(m,C1)),Power(Times(b,Plus(m,C1)),-1)),And(FreeQ(List(a,b,m),x),NonzeroQ(Plus(m,C1))))),
ISetDelayed(Int(Power(Plus(a_DEFAULT,Times(b_DEFAULT,u_)),m_),x_Symbol),
    Condition(Times(Power(Coefficient(u,x,C1),-1),Subst(Int(Power(Plus(a,Times(b,x)),m),x),x,u)),And(And(FreeQ(List(a,b,m),x),LinearQ(u,x)),NonzeroQ(Plus(u,Negate(x)))))),
ISetDelayed(Int(Power(u_,m_),x_Symbol),
    Condition(Int(Power(ExpandToSum(u,x),m),x),And(And(FreeQ(m,x),LinearQ(u,x)),Not(LinearMatchQ(u,x)))))
  );
}
