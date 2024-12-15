package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IAST;
import com.google.common.base.Supplier;

/** 
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class IntRules337 { 
  public static IAST RULES = List( 
IIntegrate(6740,Integrate(u_,x_Symbol),
    With(list(Set(v,NormalizeIntegrand(u,x))),Condition(Integrate(v,x),UnsameQ(v,u)))),
IIntegrate(6741,Integrate(u_,x_Symbol),
    With(list(Set(v,ExpandIntegrand(u,x))),Condition(Integrate(v,x),SumQ(v)))),
IIntegrate(6742,Integrate(Times(u_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,m_DEFAULT))),p_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_DEFAULT))),q_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(Plus(a,Times(b,Power(x,m))),p),Power(Plus(c,Times(d,Power(x,n))),q),Power(Power(x,Times(m,p)),CN1)),Integrate(Times(u,Power(x,Times(m,p))),x),x),And(FreeQ(List(a,b,c,d,m,n,p,q),x),EqQ(Plus(a,d),C0),EqQ(Plus(b,c),C0),EqQ(Plus(m,n),C0),EqQ(Plus(p,q),C0)))),
IIntegrate(6743,Integrate(Times(u_,Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(c_DEFAULT,Power(x_,$p("n2",true)))),p_)),x_Symbol),
    Condition(Dist(Times(Sqrt(Plus(a,Times(b,Power(x,n)),Times(c,Power(x,Times(C2,n))))),Power(Times(Power(Times(C4,c),Subtract(p,C1D2)),Plus(b,Times(C2,c,Power(x,n)))),CN1)),Integrate(Times(u,Power(Plus(b,Times(C2,c,Power(x,n))),Times(C2,p))),x),x),And(FreeQ(List(a,b,c,n,p),x),EqQ($s("n2"),Times(C2,n)),EqQ(Subtract(Sqr(b),Times(C4,a,c)),C0),IntegerQ(Subtract(p,C1D2))))),
IIntegrate(6744,Integrate(u_,x_Symbol),
    With(list(Set($s("lst"),SubstForFractionalPowerOfLinear(u,x))),Condition(Dist(Times(Part($s("lst"),C2),Part($s("lst"),C4)),Subst(Integrate(Part($s("lst"),C1),x),x,Power(Part($s("lst"),C3),Power(Part($s("lst"),C2),CN1))),x),Not(FalseQ($s("lst")))))),
IIntegrate(6745,Integrate(Power(Surd(x_,$p(n, Integer)),p_DEFAULT),x_Symbol),
    Condition(Times(n,x,Power(Surd(x,n),p),Power(Plus(n,p),CN1)),And(FreeQ(p,x),GtQ(n,C0)))),
IIntegrate(6746,Integrate(Times(Power(x_,m_),Power(Surd(x_,$p(n, Integer)),p_DEFAULT)),x_Symbol),
    Condition(Times(Power(x,Plus(C1,m)),Power(Surd(x,n),p),Power(Plus(C1,m,Times(p,Power(n,CN1))),CN1)),And(FreeQ(list(m,p),x),GtQ(n,C0))))
  );
}
