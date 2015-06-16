package org.matheclipse.core.integrate.rubi45;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi45.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi45.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *  
 */
public class IntRules15 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Times(Power(Plus(a_,Times(c_DEFAULT,Sqr(x_))),p_),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Power(Plus(a,Times(c,Sqr(x))),p),Power(Times(e,Plus(m,C1),Power(Plus(C1,Times(CN1,c,Plus(d,Times(e,x)),Power(Plus(Times(c,d),Times(CN1,e,Rt(Times(CN1,a,c),C2))),-1))),p),Power(Plus(C1,Times(CN1,c,Plus(d,Times(e,x)),Power(Plus(Times(c,d),Times(e,Rt(Times(CN1,a,c),C2))),-1))),p)),-1),AppellF1(Plus(m,C1),Negate(p),Negate(p),Plus(m,C2),Times(c,Plus(d,Times(e,x)),Power(Plus(Times(c,d),Times(CN1,e,Rt(Times(CN1,a,c),C2))),-1)),Times(c,Plus(d,Times(e,x)),Power(Plus(Times(c,d),Times(e,Rt(Times(CN1,a,c),C2))),-1)))),And(And(And(And(FreeQ(List(a,c,d,e,m,p),x),Not(IntegerQ(p))),NonzeroQ(Plus(Times(c,d),Times(CN1,e,Rt(Times(CN1,a,c),C2))))),NonzeroQ(Plus(Times(c,d),Times(e,Rt(Times(CN1,a,c),C2))))),Not(NegativeIntegerQ(m))))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,v_),Times(c_DEFAULT,Sqr(w_))),p_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,u_)),m_DEFAULT)),x_Symbol),
    Condition(Times(Power(Coefficient(u,x,C1),-1),Subst(Int(Times(Power(Plus(d,Times(e,x)),m),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),p)),x),x,u)),And(And(And(And(FreeQ(List(a,b,c,d,e,m,p),x),ZeroQ(Plus(u,Negate(v)))),ZeroQ(Plus(u,Negate(w)))),LinearQ(u,x)),NonzeroQ(Plus(u,Negate(x)))))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(c_DEFAULT,Sqr(w_))),p_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,u_)),m_DEFAULT)),x_Symbol),
    Condition(Times(Power(Coefficient(u,x,C1),-1),Subst(Int(Times(Power(Plus(d,Times(e,x)),m),Power(Plus(a,Times(c,Sqr(x))),p)),x),x,u)),And(And(And(FreeQ(List(a,c,d,e,m,p),x),ZeroQ(Plus(u,Negate(w)))),LinearQ(u,x)),NonzeroQ(Plus(u,Negate(x)))))),
ISetDelayed(Int(Times(Power(u_,m_DEFAULT),Power(v_,p_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(ExpandToSum(u,x),m),Power(ExpandToSum(v,x),p)),x),And(And(And(FreeQ(List(m,p),x),LinearQ(u,x)),QuadraticQ(v,x)),Not(And(LinearMatchQ(u,x),QuadraticMatchQ(v,x))))))
  );
}
