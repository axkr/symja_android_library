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
class IntRules365 { 
  public static IAST RULES = List( 
IIntegrate(7301,Integrate(Power(Plus(a_,Times(b_DEFAULT,Sqr(v_))),CN1),x_Symbol),
    Condition(Plus(Simp(Dist(Power(Times(C2,a),CN1),Integrate(Together(Power(Subtract(C1,Times(v,Power(Rt(Times(CN1,a,Power(b,CN1)),C2),CN1))),CN1)),x),x),x),Simp(Dist(Power(Times(C2,a),CN1),Integrate(Together(Power(Plus(C1,Times(v,Power(Rt(Times(CN1,a,Power(b,CN1)),C2),CN1))),CN1)),x),x),x)),FreeQ(list(a,b),x))),
IIntegrate(7302,Integrate(Power(Plus(a_,Times(b_DEFAULT,Power(v_,n_))),CN1),x_Symbol),
    Condition(Simp(Dist(Times(C2,Power(Times(a,n),CN1)),Sum(Integrate(Together(Power(Subtract(C1,Times(Sqr(v),Power(Times(Power(-1,Times(C4,k,Power(n,CN1))),Rt(Times(CN1,a,Power(b,CN1)),Times(C1D2,n))),CN1))),CN1)),x),list(k,C1,Times(C1D2,n))),x),x),And(FreeQ(list(a,b),x),IGtQ(Times(C1D2,n),C1)))),
IIntegrate(7303,Integrate(Power(Plus(a_,Times(b_DEFAULT,Power(v_,n_))),CN1),x_Symbol),
    Condition(Simp(Dist(Power(Times(a,n),CN1),Sum(Integrate(Together(Power(Subtract(C1,Times(v,Power(Times(Power(-1,Times(C2,k,Power(n,CN1))),Rt(Times(CN1,a,Power(b,CN1)),n)),CN1))),CN1)),x),list(k,C1,n)),x),x),And(FreeQ(list(a,b),x),IGtQ(Times(C1D2,Plus(n,CN1)),C0)))),
IIntegrate(7304,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Power(u_,n_DEFAULT))),CN1),v_),x_Symbol),
    Condition(Integrate(ReplaceAll(ExpandIntegrand(Times(PolynomialInSubst(v,u,x),Power(Plus(a,Times(b,Power(x,n))),CN1)),x),Rule(x,u)),x),And(FreeQ(list(a,b),x),IGtQ(n,C0),PolynomialInQ(v,u,x)))),
IIntegrate(7305,Integrate(u_,x_Symbol),
    With(list(Set(v,NormalizeIntegrand(u,x))),Condition(Integrate(v,x),UnsameQ(v,u)))),
IIntegrate(7306,Integrate(u_,x_Symbol),
    With(list(Set(v,ExpandIntegrand(u,x))),Condition(Integrate(v,x),SumQ(v)))),
IIntegrate(7307,Integrate(Times(u_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,m_DEFAULT))),p_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_DEFAULT))),q_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(Power(Plus(a,Times(b,Power(x,m))),p),Power(Plus(c,Times(d,Power(x,n))),q),Power(Power(x,Times(m,p)),CN1)),Integrate(Times(u,Power(x,Times(m,p))),x),x),x),And(FreeQ(List(a,b,c,d,m,n,p,q),x),EqQ(Plus(a,d),C0),EqQ(Plus(b,c),C0),EqQ(Plus(m,n),C0),EqQ(Plus(p,q),C0)))),
IIntegrate(7308,Integrate(Times(u_,Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(c_DEFAULT,Power(x_,$p("n2",true)))),p_)),x_Symbol),
    Condition(Simp(Dist(Times(Sqrt(Plus(a,Times(b,Power(x,n)),Times(c,Power(x,Times(C2,n))))),Power(Times(Power(Times(C4,c),Plus(p,CN1D2)),Plus(b,Times(C2,c,Power(x,n)))),CN1)),Integrate(Times(u,Power(Plus(b,Times(C2,c,Power(x,n))),Times(C2,p))),x),x),x),And(FreeQ(List(a,b,c,n,p),x),EqQ($s("n2"),Times(C2,n)),EqQ(Subtract(Sqr(b),Times(C4,a,c)),C0),IntegerQ(Plus(p,CN1D2))))),
IIntegrate(7309,Integrate(u_,x_Symbol),
    With(list(Set($s("lst"),SubstForFractionalPowerOfLinear(u,x))),Condition(Simp(Dist(Times(Part($s("lst"),C2),Part($s("lst"),C4)),Subst(Integrate(Part($s("lst"),C1),x),x,Power(Part($s("lst"),C3),Power(Part($s("lst"),C2),CN1))),x),x),Not(FalseQ($s("lst")))))),
IIntegrate(7312,Integrate(Power(Surd(x_,$p(n, Integer)),p_DEFAULT),x_Symbol),
    Condition(Times(n,x,Power(Surd(x,n),p),Power(Plus(n,p),CN1)),And(FreeQ(p,x),GtQ(n,C0)))),
IIntegrate(7313,Integrate(Times(Power(x_,m_),Power(Surd(x_,$p(n, Integer)),p_DEFAULT)),x_Symbol),
    Condition(Times(Power(x,Plus(C1,m)),Power(Surd(x,n),p),Power(Plus(C1,m,Times(p,Power(n,CN1))),CN1)),And(FreeQ(list(m,p),x),GtQ(n,C0))))
  );
}
