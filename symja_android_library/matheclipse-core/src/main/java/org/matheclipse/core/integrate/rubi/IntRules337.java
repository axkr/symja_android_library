package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *  
 */
public class IntRules337 { 
  public static IAST RULES = List( 
IIntegrate(6740,Int(u_,x_Symbol),
    With(List(Set(v,NormalizeIntegrand(u,x))),Condition(Int(v,x),UnsameQ(v,u)))),
IIntegrate(6741,Int(u_,x_Symbol),
    With(List(Set(v,ExpandIntegrand(u,x))),Condition(Int(v,x),SumQ(v)))),
IIntegrate(6742,Int(Times(u_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,m_DEFAULT))),p_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_DEFAULT))),q_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(Plus(a,Times(b,Power(x,m))),p),Power(Plus(c,Times(d,Power(x,n))),q),Power(Power(x,Times(m,p)),CN1)),Int(Times(u,Power(x,Times(m,p))),x),x),And(FreeQ(List(a,b,c,d,m,n,p,q),x),EqQ(Plus(a,d),C0),EqQ(Plus(b,c),C0),EqQ(Plus(m,n),C0),EqQ(Plus(p,q),C0)))),
IIntegrate(6743,Int(Times(u_,Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(c_DEFAULT,Power(x_,$p("n2",true)))),p_)),x_Symbol),
    Condition(Dist(Times(Sqrt(Plus(a,Times(b,Power(x,n)),Times(c,Power(x,Times(C2,n))))),Power(Times(Power(Times(C4,c),Subtract(p,C1D2)),Plus(b,Times(C2,c,Power(x,n)))),CN1)),Int(Times(u,Power(Plus(b,Times(C2,c,Power(x,n))),Times(C2,p))),x),x),And(FreeQ(List(a,b,c,n,p),x),EqQ($s("n2"),Times(C2,n)),EqQ(Subtract(Sqr(b),Times(C4,a,c)),C0),IntegerQ(Subtract(p,C1D2))))),
IIntegrate(6744,Int(u_,x_Symbol),
    With(List(Set($s("lst"),SubstForFractionalPowerOfLinear(u,x))),Condition(Dist(Times(Part($s("lst"),C2),Part($s("lst"),C4)),Subst(Int(Part($s("lst"),C1),x),x,Power(Part($s("lst"),C3),Power(Part($s("lst"),C2),CN1))),x),Not(FalseQ($s("lst"))))))
  );
}
