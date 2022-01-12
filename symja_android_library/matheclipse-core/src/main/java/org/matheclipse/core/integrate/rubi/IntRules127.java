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
class IntRules127 { 
  public static IAST RULES = List( 
IIntegrate(2541,Integrate(Times(Power(x_,CN1),Power(Plus(Times(Power(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),q_),b_DEFAULT),Times(a_DEFAULT,Power(x_,m_DEFAULT))),CN1),Plus(Times(Power(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),r_DEFAULT),e_DEFAULT),Times(d_DEFAULT,Power(x_,m_DEFAULT)))),x_Symbol),
    Condition(Simp(Times(e,Log(Plus(Times(a,Power(x,m)),Times(b,Power(Log(Times(c,Power(x,n))),q)))),Power(Times(b,n,q),CN1)),x),And(FreeQ(List(a,b,c,d,e,m,n,q,r),x),EqQ(r,Subtract(q,C1)),EqQ(Subtract(Times(a,e,m),Times(b,d,n,q)),C0)))),
IIntegrate(2542,Integrate(Times(Power(x_,CN1),Power(Plus(Times(Power(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),q_),b_DEFAULT),Times(a_DEFAULT,Power(x_,m_DEFAULT))),CN1),Plus(Times(Power(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),r_DEFAULT),e_DEFAULT),u_,Times(d_DEFAULT,Power(x_,m_DEFAULT)))),x_Symbol),
    Condition(Plus(Simp(Times(e,Log(Plus(Times(a,Power(x,m)),Times(b,Power(Log(Times(c,Power(x,n))),q)))),Power(Times(b,n,q),CN1)),x),Integrate(Times(u,Power(Times(x,Plus(Times(a,Power(x,m)),Times(b,Power(Log(Times(c,Power(x,n))),q)))),CN1)),x)),And(FreeQ(List(a,b,c,d,e,m,n,q,r),x),EqQ(r,Subtract(q,C1)),EqQ(Subtract(Times(a,e,m),Times(b,d,n,q)),C0)))),
IIntegrate(2543,Integrate(Times(Power(x_,CN1),Power(Plus(Times(Power(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),q_),b_DEFAULT),Times(a_DEFAULT,Power(x_,m_DEFAULT))),CN1),Plus(Times(Power(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),r_DEFAULT),e_DEFAULT),Times(d_DEFAULT,Power(x_,m_DEFAULT)))),x_Symbol),
    Condition(Subtract(Simp(Times(e,Log(Plus(Times(a,Power(x,m)),Times(b,Power(Log(Times(c,Power(x,n))),q)))),Power(Times(b,n,q),CN1)),x),Dist(Times(Subtract(Times(a,e,m),Times(b,d,n,q)),Power(Times(b,n,q),CN1)),Integrate(Times(Power(x,Subtract(m,C1)),Power(Plus(Times(a,Power(x,m)),Times(b,Power(Log(Times(c,Power(x,n))),q))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,m,n,q,r),x),EqQ(r,Subtract(q,C1)),NeQ(Subtract(Times(a,e,m),Times(b,d,n,q)),C0)))),
IIntegrate(2544,Integrate(Times(Power(x_,CN1),Power(Plus(Times(Power(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),q_),b_DEFAULT),Times(a_DEFAULT,Power(x_,m_DEFAULT))),p_DEFAULT),Plus(Times(Power(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),r_DEFAULT),e_DEFAULT),Times(d_DEFAULT,Power(x_,m_DEFAULT)))),x_Symbol),
    Condition(Simp(Times(e,Power(Plus(Times(a,Power(x,m)),Times(b,Power(Log(Times(c,Power(x,n))),q))),Plus(p,C1)),Power(Times(b,n,q,Plus(p,C1)),CN1)),x),And(FreeQ(List(a,b,c,d,e,m,n,p,q,r),x),EqQ(r,Subtract(q,C1)),NeQ(p,CN1),EqQ(Subtract(Times(a,e,m),Times(b,d,n,q)),C0)))),
IIntegrate(2545,Integrate(Times(Power(x_,CN1),Power(Plus(Times(Power(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),q_),b_DEFAULT),Times(a_DEFAULT,Power(x_,m_DEFAULT))),p_DEFAULT),Plus(Times(Power(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),r_DEFAULT),e_DEFAULT),Times(d_DEFAULT,Power(x_,m_DEFAULT)))),x_Symbol),
    Condition(Subtract(Simp(Times(e,Power(Plus(Times(a,Power(x,m)),Times(b,Power(Log(Times(c,Power(x,n))),q))),Plus(p,C1)),Power(Times(b,n,q,Plus(p,C1)),CN1)),x),Dist(Times(Subtract(Times(a,e,m),Times(b,d,n,q)),Power(Times(b,n,q),CN1)),Integrate(Times(Power(x,Subtract(m,C1)),Power(Plus(Times(a,Power(x,m)),Times(b,Power(Log(Times(c,Power(x,n))),q))),p)),x),x)),And(FreeQ(List(a,b,c,d,e,m,n,p,q,r),x),EqQ(r,Subtract(q,C1)),NeQ(p,CN1),NeQ(Subtract(Times(a,e,m),Times(b,d,n,q)),C0)))),
IIntegrate(2546,Integrate(Times(Power(x_,CN1),Power(Plus(Times(Power(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),q_),b_DEFAULT),Times(a_DEFAULT,Power(x_,m_DEFAULT))),CN2),Plus(Times(Power(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),q_DEFAULT),f_DEFAULT),Times(d_DEFAULT,Power(x_,m_DEFAULT)),Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),e_DEFAULT,Power(x_,m_DEFAULT)))),x_Symbol),
    Condition(Simp(Times(d,Log(Times(c,Power(x,n))),Power(Times(a,n,Plus(Times(a,Power(x,m)),Times(b,Power(Log(Times(c,Power(x,n))),q)))),CN1)),x),And(FreeQ(List(a,b,c,d,e,f,m,n,q),x),EqQ(Plus(Times(e,n),Times(d,m)),C0),EqQ(Plus(Times(a,f),Times(b,d,Subtract(q,C1))),C0)))),
IIntegrate(2547,Integrate(Times(Plus(Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),e_DEFAULT),d_),Power(Plus(Times(Power(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),q_),b_DEFAULT),Times(a_DEFAULT,x_)),CN2)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(e,Log(Times(c,Power(x,n))),Power(Times(a,Plus(Times(a,x),Times(b,Power(Log(Times(c,Power(x,n))),q)))),CN1)),x)),Dist(Times(Plus(d,Times(e,n)),Power(a,CN1)),Integrate(Power(Times(x,Plus(Times(a,x),Times(b,Power(Log(Times(c,Power(x,n))),q)))),CN1),x),x)),And(FreeQ(List(a,b,c,d,e,n,q),x),EqQ(Plus(d,Times(e,n,q)),C0)))),
IIntegrate(2548,Integrate(Log(u_),x_Symbol),
    Condition(Subtract(Simp(Times(x,Log(u)),x),Integrate(SimplifyIntegrand(Times(x,D(u,x),Power(u,CN1)),x),x)),InverseFunctionFreeQ(u,x))),
IIntegrate(2549,Integrate(Log(u_),x_Symbol),
    Condition(Subtract(Simp(Times(x,Log(u)),x),Integrate(SimplifyIntegrand(Times(x,Simplify(Times(D(u,x),Power(u,CN1)))),x),x)),ProductQ(u))),
IIntegrate(2550,Integrate(Times(Log(u_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(Log(Plus(a,Times(b,x))),Log(u),Power(b,CN1)),x),Dist(Power(b,CN1),Integrate(SimplifyIntegrand(Times(Log(Plus(a,Times(b,x))),D(u,x),Power(u,CN1)),x),x),x)),And(FreeQ(List(a,b),x),RationalFunctionQ(Times(D(u,x),Power(u,CN1)),x),Or(NeQ(a,C0),Not(And(BinomialQ(u,x),EqQ(Sqr(BinomialDegree(u,x)),C1))))))),
IIntegrate(2551,Integrate(Times(Log(u_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(a,Times(b,x)),Plus(m,C1)),Log(u),Power(Times(b,Plus(m,C1)),CN1)),x),Dist(Power(Times(b,Plus(m,C1)),CN1),Integrate(SimplifyIntegrand(Times(Power(Plus(a,Times(b,x)),Plus(m,C1)),D(u,x),Power(u,CN1)),x),x),x)),And(FreeQ(List(a,b,m),x),InverseFunctionFreeQ(u,x),NeQ(m,CN1)))),
IIntegrate(2552,Integrate(Times(Log(u_),Power($p("§qx"),CN1)),x_Symbol),
    Condition(With(List(Set(v,IntHide(Power($s("§qx"),CN1),x))),Subtract(Simp(Times(v,Log(u)),x),Integrate(SimplifyIntegrand(Times(v,D(u,x),Power(u,CN1)),x),x))),And(QuadraticQ($s("§qx"),x),InverseFunctionFreeQ(u,x)))),
IIntegrate(2553,Integrate(Times(Log(u_),Power(u_,Times(a_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(u,Times(a,x)),Power(a,CN1)),x),Integrate(SimplifyIntegrand(Times(x,Power(u,Subtract(Times(a,x),C1)),D(u,x)),x),x)),And(FreeQ(a,x),InverseFunctionFreeQ(u,x)))),
IIntegrate(2554,Integrate(Times(Log(u_),v_),x_Symbol),
    Condition(With(List(Set(w,IntHide(v,x))),Condition(Subtract(Dist(Log(u),w,x),Integrate(SimplifyIntegrand(Times(w,D(u,x),Power(u,CN1)),x),x)),InverseFunctionFreeQ(w,x))),InverseFunctionFreeQ(u,x))),
IIntegrate(2555,Integrate(Times(Log(u_),v_),x_Symbol),
    Condition(With(List(Set(w,IntHide(v,x))),Condition(Subtract(Dist(Log(u),w,x),Integrate(SimplifyIntegrand(Times(w,Simplify(Times(D(u,x),Power(u,CN1)))),x),x)),InverseFunctionFreeQ(w,x))),ProductQ(u))),
IIntegrate(2556,Integrate(Times(Log(v_),Log(w_)),x_Symbol),
    Condition(Plus(Simp(Times(x,Log(v),Log(w)),x),Negate(Integrate(SimplifyIntegrand(Times(x,Log(w),D(v,x),Power(v,CN1)),x),x)),Negate(Integrate(SimplifyIntegrand(Times(x,Log(v),D(w,x),Power(w,CN1)),x),x))),And(InverseFunctionFreeQ(v,x),InverseFunctionFreeQ(w,x)))),
IIntegrate(2557,Integrate(Times(Log(v_),Log(w_),u_),x_Symbol),
    Condition(With(List(Set(z,IntHide(u,x))),Condition(Plus(Dist(Times(Log(v),Log(w)),z,x),Negate(Integrate(SimplifyIntegrand(Times(z,Log(w),D(v,x),Power(v,CN1)),x),x)),Negate(Integrate(SimplifyIntegrand(Times(z,Log(v),D(w,x),Power(w,CN1)),x),x))),InverseFunctionFreeQ(z,x))),And(InverseFunctionFreeQ(v,x),InverseFunctionFreeQ(w,x)))),
IIntegrate(2558,Integrate(Power(f_,Times(Log(u_),a_DEFAULT)),x_Symbol),
    Condition(Integrate(Power(u,Times(a,Log(f))),x),FreeQ(List(a,f),x))),
IIntegrate(2559,Integrate(u_,x_Symbol),
    Condition(With(List(Set($s("lst"),FunctionOfLog(Cancel(Times(x,u)),x))),Condition(Dist(Power(Part($s("lst"),C3),CN1),Subst(Integrate(Part($s("lst"),C1),x),x,Log(Part($s("lst"),C2))),x),Not(FalseQ($s("lst"))))),NonsumQ(u))),
IIntegrate(2560,Integrate(Times(Log(Gamma(v_)),u_DEFAULT),x_Symbol),
    Plus(Dist(Subtract(Log(Gamma(v)),LogGamma(v)),Integrate(u,x),x),Integrate(Times(u,LogGamma(v)),x)))
  );
}
