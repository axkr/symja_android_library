package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class IntRules196 { 
  public static IAST RULES = List( 
IIntegrate(3921,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(u_,n_)))),b_DEFAULT)),p_DEFAULT),Power(v_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(v,x),m),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(ExpandToSum(u,x),n)))))),p)),x),And(FreeQ(List(a,b,c,d,m,n,p),x),LinearQ(u,x),LinearQ(v,x),Not(And(LinearMatchQ(u,x),LinearMatchQ(v,x)))))),
IIntegrate(3922,Integrate(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),Power(x_,m_DEFAULT),Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Times(Power(Sin(Plus(a,Times(b,Power(x,n)))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),CN1)),x),And(FreeQ(List(a,b,m,n,p),x),EqQ(m,Subtract(n,C1)),NeQ(p,CN1)))),
IIntegrate(3923,Integrate(Times(Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT),Power(x_,m_DEFAULT),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))))),x_Symbol),
    Condition(Simp(Times(CN1,Power(Cos(Plus(a,Times(b,Power(x,n)))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),CN1)),x),And(FreeQ(List(a,b,m,n,p),x),EqQ(m,Subtract(n,C1)),NeQ(p,CN1)))),
IIntegrate(3924,Integrate(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),Power(x_,m_DEFAULT),Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,Negate(n),C1)),Power(Sin(Plus(a,Times(b,Power(x,n)))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),CN1)),x),Simp(Star(Times(Plus(m,Negate(n),C1),Power(Times(b,n,Plus(p,C1)),CN1)),Integrate(Times(Power(x,Subtract(m,n)),Power(Sin(Plus(a,Times(b,Power(x,n)))),Plus(p,C1))),x)),x)),And(FreeQ(list(a,b,p),x),LtQ(C0,n,Plus(m,C1)),NeQ(p,CN1)))),
IIntegrate(3925,Integrate(Times(Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT),Power(x_,m_DEFAULT),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))))),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(x,Plus(m,Negate(n),C1)),Power(Cos(Plus(a,Times(b,Power(x,n)))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),CN1)),x),Simp(Star(Times(Plus(m,Negate(n),C1),Power(Times(b,n,Plus(p,C1)),CN1)),Integrate(Times(Power(x,Subtract(m,n)),Power(Cos(Plus(a,Times(b,Power(x,n)))),Plus(p,C1))),x)),x)),And(FreeQ(list(a,b,p),x),LtQ(C0,n,Plus(m,C1)),NeQ(p,CN1)))),
IIntegrate(3926,Integrate(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Integrate(Sin(Times(Sqr(Plus(b,Times(C2,c,x))),Power(Times(C4,c),CN1))),x),And(FreeQ(list(a,b,c),x),EqQ(Subtract(Sqr(b),Times(C4,a,c)),C0)))),
IIntegrate(3927,Integrate(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Integrate(Cos(Times(Sqr(Plus(b,Times(C2,c,x))),Power(Times(C4,c),CN1))),x),And(FreeQ(list(a,b,c),x),EqQ(Subtract(Sqr(b),Times(C4,a,c)),C0)))),
IIntegrate(3928,Integrate(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Subtract(Simp(Star(Cos(Times(Subtract(Sqr(b),Times(C4,a,c)),Power(Times(C4,c),CN1))),Integrate(Sin(Times(Sqr(Plus(b,Times(C2,c,x))),Power(Times(C4,c),CN1))),x)),x),Simp(Star(Sin(Times(Subtract(Sqr(b),Times(C4,a,c)),Power(Times(C4,c),CN1))),Integrate(Cos(Times(Sqr(Plus(b,Times(C2,c,x))),Power(Times(C4,c),CN1))),x)),x)),And(FreeQ(list(a,b,c),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0)))),
IIntegrate(3929,Integrate(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Plus(Simp(Star(Cos(Times(Subtract(Sqr(b),Times(C4,a,c)),Power(Times(C4,c),CN1))),Integrate(Cos(Times(Sqr(Plus(b,Times(C2,c,x))),Power(Times(C4,c),CN1))),x)),x),Simp(Star(Sin(Times(Subtract(Sqr(b),Times(C4,a,c)),Power(Times(C4,c),CN1))),Integrate(Sin(Times(Sqr(Plus(b,Times(C2,c,x))),Power(Times(C4,c),CN1))),x)),x)),And(FreeQ(list(a,b,c),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0)))),
IIntegrate(3930,Integrate(Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),n),x),x),And(FreeQ(list(a,b,c),x),IGtQ(n,C1)))),
IIntegrate(3931,Integrate(Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),n),x),x),And(FreeQ(list(a,b,c),x),IGtQ(n,C1)))),
IIntegrate(3932,Integrate(Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_DEFAULT),x_Symbol),
    Condition(Unintegrable(Power(Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),n),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(3933,Integrate(Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_DEFAULT),x_Symbol),
    Condition(Unintegrable(Power(Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),n),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(3934,Integrate(Power(Sin(v_),n_DEFAULT),x_Symbol),
    Condition(Integrate(Power(Sin(ExpandToSum(v,x)),n),x),And(IGtQ(n,C0),QuadraticQ(v,x),Not(QuadraticMatchQ(v,x))))),
IIntegrate(3935,Integrate(Power(Cos(v_),n_DEFAULT),x_Symbol),
    Condition(Integrate(Power(Cos(ExpandToSum(v,x)),n),x),And(IGtQ(n,C0),QuadraticQ(v,x),Not(QuadraticMatchQ(v,x))))),
IIntegrate(3936,Integrate(Times(Plus(d_,Times(e_DEFAULT,x_)),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Simp(Times(CN1,e,Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(Subtract(Times(C2,c,d),Times(b,e)),C0)))),
IIntegrate(3937,Integrate(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Plus(d_,Times(e_DEFAULT,x_))),x_Symbol),
    Condition(Simp(Times(e,Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(Subtract(Times(C2,c,d),Times(b,e)),C0)))),
IIntegrate(3938,Integrate(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Simp(Times(CN1,e,Power(Plus(d,Times(e,x)),Subtract(m,C1)),Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),x),Simp(Star(Times(Sqr(e),Subtract(m,C1),Power(Times(C2,c),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Subtract(m,C2)),Cos(Plus(a,Times(b,x),Times(c,Sqr(x))))),x)),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(Subtract(Times(C2,c,d),Times(b,e)),C0),GtQ(m,C1)))),
IIntegrate(3939,Integrate(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Subtract(Simp(Times(e,Power(Plus(d,Times(e,x)),Subtract(m,C1)),Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),x),Simp(Star(Times(Sqr(e),Subtract(m,C1),Power(Times(C2,c),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Subtract(m,C2)),Sin(Plus(a,Times(b,x),Times(c,Sqr(x))))),x)),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(Subtract(Times(C2,c,d),Times(b,e)),C0),GtQ(m,C1)))),
IIntegrate(3940,Integrate(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(e,Plus(m,C1)),CN1)),x),Simp(Star(Times(C2,c,Power(Times(Sqr(e),Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Plus(m,C2)),Cos(Plus(a,Times(b,x),Times(c,Sqr(x))))),x)),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(Subtract(Times(C2,c,d),Times(b,e)),C0),LtQ(m,CN1))))
  );
}
