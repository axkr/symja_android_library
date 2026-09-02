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
class IntRules196 { 
  public static IAST RULES = List( 
IIntegrate(3921,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(x,n)))))),p)),x),FreeQ(List(a,b,c,d,e,m,n,p),x))),
IIntegrate(3922,Integrate(Times(Power(Times(e_,x_),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(u_))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Sin(ExpandToSum(u,x)))),p)),x),And(FreeQ(List(a,b,e,m,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x))))),
IIntegrate(3923,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cos(u_),b_DEFAULT)),p_DEFAULT),Power(Times(e_,x_),m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Cos(ExpandToSum(u,x)))),p)),x),And(FreeQ(List(a,b,e,m,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x))))),
IIntegrate(3924,Integrate(Times(Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(Times(n,f),CN1),Subst(Integrate(ExpandIntegrand(Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),p),Times(Power(x,Plus(Power(n,CN1),CN1)),Power(Plus(g,Times(CN1,e,h,Power(f,CN1)),Times(h,Power(x,Power(n,CN1)),Power(f,CN1))),m)),x),x),x,Power(Plus(e,Times(f,x)),n)),x),x),And(FreeQ(List(a,b,c,d,e,f,g,h,m),x),IGtQ(p,C0),IntegerQ(Power(n,CN1))))),
IIntegrate(3925,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))),b_DEFAULT)),p_DEFAULT),Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(Times(n,f),CN1),Subst(Integrate(ExpandIntegrand(Power(Plus(a,Times(b,Cos(Plus(c,Times(d,x))))),p),Times(Power(x,Plus(Power(n,CN1),CN1)),Power(Plus(g,Times(CN1,e,h,Power(f,CN1)),Times(h,Power(x,Power(n,CN1)),Power(f,CN1))),m)),x),x),x,Power(Plus(e,Times(f,x)),n)),x),x),And(FreeQ(List(a,b,c,d,e,f,g,h,m),x),IGtQ(p,C0),IntegerQ(Power(n,CN1))))),
IIntegrate(3926,Integrate(Times(Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Module(list(Set(k,If(FractionQ(n),Denominator(n),C1))),Simp(Dist(Times(k,Power(Power(f,Plus(m,C1)),CN1)),Subst(Integrate(ExpandIntegrand(Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(x,Times(k,n))))))),p),Times(Power(x,Plus(k,CN1)),Power(Plus(Times(f,g),Times(CN1,e,h),Times(h,Power(x,k))),m)),x),x),x,Power(Plus(e,Times(f,x)),Power(k,CN1))),x),x)),And(FreeQ(List(a,b,c,d,e,f,g,h),x),IGtQ(p,C0),IGtQ(m,C0)))),
IIntegrate(3927,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))),b_DEFAULT)),p_DEFAULT),Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Module(list(Set(k,If(FractionQ(n),Denominator(n),C1))),Simp(Dist(Times(k,Power(Power(f,Plus(m,C1)),CN1)),Subst(Integrate(ExpandIntegrand(Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(x,Times(k,n))))))),p),Times(Power(x,Plus(k,CN1)),Power(Plus(Times(f,g),Times(CN1,e,h),Times(h,Power(x,k))),m)),x),x),x,Power(Plus(e,Times(f,x)),Power(k,CN1))),x),x)),And(FreeQ(List(a,b,c,d,e,f,g,h),x),IGtQ(p,C0),IGtQ(m,C0)))),
IIntegrate(3928,Integrate(Times(Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(f,CN1),Subst(Integrate(Times(Power(Times(h,x,Power(f,CN1)),m),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(x,n)))))),p)),x),x,Plus(e,Times(f,x))),x),x),And(FreeQ(List(a,b,c,d,e,f,g,h,m),x),IGtQ(p,C0),EqQ(Subtract(Times(f,g),Times(e,h)),C0)))),
IIntegrate(3929,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))),b_DEFAULT)),p_DEFAULT),Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(f,CN1),Subst(Integrate(Times(Power(Times(h,x,Power(f,CN1)),m),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(x,n)))))),p)),x),x,Plus(e,Times(f,x))),x),x),And(FreeQ(List(a,b,c,d,e,f,g,h,m),x),IGtQ(p,C0),EqQ(Subtract(Times(f,g),Times(e,h)),C0)))),
IIntegrate(3930,Integrate(Times(Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(g,Times(h,x)),m),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(Plus(e,Times(f,x)),n)))))),p)),x),FreeQ(List(a,b,c,d,e,f,g,h,m,n,p),x))),
IIntegrate(3931,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))),b_DEFAULT)),p_DEFAULT),Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(g,Times(h,x)),m),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(Plus(e,Times(f,x)),n)))))),p)),x),FreeQ(List(a,b,c,d,e,f,g,h,m,n,p),x))),
IIntegrate(3932,Integrate(Times(Power(v_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(u_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(v,x),m),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(ExpandToSum(u,x),n)))))),p)),x),And(FreeQ(List(a,b,c,d,m,n,p),x),LinearQ(u,x),LinearQ(v,x),Not(And(LinearMatchQ(u,x),LinearMatchQ(v,x)))))),
IIntegrate(3933,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(u_,n_)))),b_DEFAULT)),p_DEFAULT),Power(v_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(v,x),m),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(ExpandToSum(u,x),n)))))),p)),x),And(FreeQ(List(a,b,c,d,m,n,p),x),LinearQ(u,x),LinearQ(v,x),Not(And(LinearMatchQ(u,x),LinearMatchQ(v,x)))))),
IIntegrate(3934,Integrate(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),Power(x_,m_DEFAULT),Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Times(Power(Sin(Plus(a,Times(b,Power(x,n)))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),CN1)),x),And(FreeQ(List(a,b,m,n,p),x),EqQ(m,Plus(n,CN1)),NeQ(p,CN1)))),
IIntegrate(3935,Integrate(Times(Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT),Power(x_,m_DEFAULT),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))))),x_Symbol),
    Condition(Simp(Times(CN1,Power(Cos(Plus(a,Times(b,Power(x,n)))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),CN1)),x),And(FreeQ(List(a,b,m,n,p),x),EqQ(m,Plus(n,CN1)),NeQ(p,CN1)))),
IIntegrate(3936,Integrate(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),Power(x_,m_DEFAULT),Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,Negate(n),C1)),Power(Sin(Plus(a,Times(b,Power(x,n)))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),CN1)),x),Simp(Dist(Times(Plus(m,Negate(n),C1),Power(Times(b,n,Plus(p,C1)),CN1)),Integrate(Times(Power(x,Subtract(m,n)),Power(Sin(Plus(a,Times(b,Power(x,n)))),Plus(p,C1))),x),x),x)),And(FreeQ(list(a,b,p),x),LtQ(C0,n,Plus(m,C1)),NeQ(p,CN1)))),
IIntegrate(3937,Integrate(Times(Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT),Power(x_,m_DEFAULT),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))))),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(x,Plus(m,Negate(n),C1)),Power(Cos(Plus(a,Times(b,Power(x,n)))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),CN1)),x),Simp(Dist(Times(Plus(m,Negate(n),C1),Power(Times(b,n,Plus(p,C1)),CN1)),Integrate(Times(Power(x,Subtract(m,n)),Power(Cos(Plus(a,Times(b,Power(x,n)))),Plus(p,C1))),x),x),x)),And(FreeQ(list(a,b,p),x),LtQ(C0,n,Plus(m,C1)),NeQ(p,CN1)))),
IIntegrate(3938,Integrate(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Integrate(Sin(Times(Sqr(Plus(b,Times(C2,c,x))),Power(Times(C4,c),CN1))),x),And(FreeQ(list(a,b,c),x),EqQ(Subtract(Sqr(b),Times(C4,a,c)),C0)))),
IIntegrate(3939,Integrate(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Integrate(Cos(Times(Sqr(Plus(b,Times(C2,c,x))),Power(Times(C4,c),CN1))),x),And(FreeQ(list(a,b,c),x),EqQ(Subtract(Sqr(b),Times(C4,a,c)),C0)))),
IIntegrate(3940,Integrate(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Subtract(Simp(Dist(Cos(Times(Subtract(Sqr(b),Times(C4,a,c)),Power(Times(C4,c),CN1))),Integrate(Sin(Times(Sqr(Plus(b,Times(C2,c,x))),Power(Times(C4,c),CN1))),x),x),x),Simp(Dist(Sin(Times(Subtract(Sqr(b),Times(C4,a,c)),Power(Times(C4,c),CN1))),Integrate(Cos(Times(Sqr(Plus(b,Times(C2,c,x))),Power(Times(C4,c),CN1))),x),x),x)),And(FreeQ(list(a,b,c),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0))))
  );
}
