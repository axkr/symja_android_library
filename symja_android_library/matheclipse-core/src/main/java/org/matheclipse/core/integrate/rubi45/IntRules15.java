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
public class IntRules15 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Times(Power(Plus(a_,Times(c_DEFAULT,Sqr(x_))),p_),Power(Plus(pd_DEFAULT,Times(x_,pe_DEFAULT)),m_DEFAULT)),x_Symbol),
    Condition(Times(Power(Plus(pd,Times(pe,x)),Plus(m,C1)),Power(Plus(a,Times(c,Sqr(x))),p),Power(Times(pe,Plus(m,C1),Power(Plus(C1,Times(CN1,c,Plus(pd,Times(pe,x)),Power(Plus(Times(c,pd),Times(CN1,pe,Rt(Times(CN1,a,c),C2))),CN1))),p),Power(Plus(C1,Times(CN1,c,Plus(pd,Times(pe,x)),Power(Plus(Times(c,pd),Times(pe,Rt(Times(CN1,a,c),C2))),CN1))),p)),CN1),AppellF1(Plus(m,C1),Times(CN1,p),Times(CN1,p),Plus(m,C2),Times(c,Plus(pd,Times(pe,x)),Power(Plus(Times(c,pd),Times(CN1,pe,Rt(Times(CN1,a,c),C2))),CN1)),Times(c,Plus(pd,Times(pe,x)),Power(Plus(Times(c,pd),Times(pe,Rt(Times(CN1,a,c),C2))),CN1)))),And(And(And(And(FreeQ(List(a,c,pd,pe,m,p),x),Not(IntegerQ(p))),NonzeroQ(Plus(Times(c,pd),Times(CN1,pe,Rt(Times(CN1,a,c),C2))))),NonzeroQ(Plus(Times(c,pd),Times(pe,Rt(Times(CN1,a,c),C2))))),Not(NegativeIntegerQ(m))))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,v_),Times(c_DEFAULT,Sqr(w_))),p_DEFAULT),Power(Plus(pd_DEFAULT,Times(u_,pe_DEFAULT)),m_DEFAULT)),x_Symbol),
    Condition(Times(Power(Coefficient(u,x,C1),CN1),Subst(Int(Times(Power(Plus(pd,Times(pe,x)),m),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),p)),x),x,u)),And(And(And(And(FreeQ(List(a,b,c,pd,pe,m,p),x),ZeroQ(Plus(u,Times(CN1,v)))),ZeroQ(Plus(u,Times(CN1,w)))),LinearQ(u,x)),NonzeroQ(Plus(u,Times(CN1,x)))))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(c_DEFAULT,Sqr(w_))),p_DEFAULT),Power(Plus(pd_DEFAULT,Times(u_,pe_DEFAULT)),m_DEFAULT)),x_Symbol),
    Condition(Times(Power(Coefficient(u,x,C1),CN1),Subst(Int(Times(Power(Plus(pd,Times(pe,x)),m),Power(Plus(a,Times(c,Sqr(x))),p)),x),x,u)),And(And(And(FreeQ(List(a,c,pd,pe,m,p),x),ZeroQ(Plus(u,Times(CN1,w)))),LinearQ(u,x)),NonzeroQ(Plus(u,Times(CN1,x)))))),
ISetDelayed(Int(Times(Power(u_,m_DEFAULT),Power(v_,p_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(ExpandToSum(u,x),m),Power(ExpandToSum(v,x),p)),x),And(And(And(FreeQ(List(m,p),x),LinearQ(u,x)),QuadraticQ(v,x)),Not(And(LinearMatchQ(u,x),QuadraticMatchQ(v,x))))))
  );
}
