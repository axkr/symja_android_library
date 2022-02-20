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
class IntRules171 { 
  public static IAST RULES = List( 
IIntegrate(3421,Integrate(Times(Power(Times(e_,x_),m_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(x,n)))))),p)),x),x),And(FreeQ(List(a,b,c,d,e,m,n),x),IntegerQ(p),NeQ(m,CN1),IGtQ(Simplify(Times(n,Power(Plus(m,C1),CN1))),C0),Not(IntegerQ(n))))),
IIntegrate(3422,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(Times(e_,x_),m_)),x_Symbol),
    Condition(Dist(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(x,n)))))),p)),x),x),And(FreeQ(List(a,b,c,d,e,m,n),x),IntegerQ(p),NeQ(m,CN1),IGtQ(Simplify(Times(n,Power(Plus(m,C1),CN1))),C0),Not(IntegerQ(n))))),
IIntegrate(3423,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_))))),x_Symbol),
    Condition(Subtract(Dist(Times(C1D2,CI),Integrate(Times(Power(Times(e,x),m),Exp(Subtract(Times(CN1,c,CI),Times(d,CI,Power(x,n))))),x),x),Dist(Times(C1D2,CI),Integrate(Times(Power(Times(e,x),m),Exp(Plus(Times(c,CI),Times(d,CI,Power(x,n))))),x),x)),FreeQ(List(c,d,e,m,n),x))),
IIntegrate(3424,Integrate(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Plus(Dist(C1D2,Integrate(Times(Power(Times(e,x),m),Exp(Subtract(Times(CN1,c,CI),Times(d,CI,Power(x,n))))),x),x),Dist(C1D2,Integrate(Times(Power(Times(e,x),m),Exp(Plus(Times(c,CI),Times(d,CI,Power(x,n))))),x),x)),FreeQ(List(c,d,e,m,n),x))),
IIntegrate(3425,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(Times(e,x),m),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(x,n)))))),p),x),x),And(FreeQ(List(a,b,c,d,e,m,n),x),IGtQ(p,C0)))),
IIntegrate(3426,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(Times(e,x),m),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(x,n)))))),p),x),x),And(FreeQ(List(a,b,c,d,e,m,n),x),IGtQ(p,C0)))),
IIntegrate(3427,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(x,n)))))),p)),x),FreeQ(List(a,b,c,d,e,m,n,p),x))),
IIntegrate(3428,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(x,n)))))),p)),x),FreeQ(List(a,b,c,d,e,m,n,p),x))),
IIntegrate(3429,Integrate(Times(Power(Times(e_,x_),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(u_))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Sin(ExpandToSum(u,x)))),p)),x),And(FreeQ(List(a,b,e,m,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x))))),
IIntegrate(3430,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cos(u_),b_DEFAULT)),p_DEFAULT),Power(Times(e_,x_),m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Cos(ExpandToSum(u,x)))),p)),x),And(FreeQ(List(a,b,e,m,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x))))),
IIntegrate(3431,Integrate(Times(Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Times(n,f),CN1),Subst(Integrate(ExpandIntegrand(Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),p),Times(Power(x,Subtract(Power(n,CN1),C1)),Power(Plus(g,Times(CN1,e,h,Power(f,CN1)),Times(h,Power(x,Power(n,CN1)),Power(f,CN1))),m)),x),x),x,Power(Plus(e,Times(f,x)),n)),x),And(FreeQ(List(a,b,c,d,e,f,g,h,m),x),IGtQ(p,C0),IntegerQ(Power(n,CN1))))),
IIntegrate(3432,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))),b_DEFAULT)),p_DEFAULT),Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Times(n,f),CN1),Subst(Integrate(ExpandIntegrand(Power(Plus(a,Times(b,Cos(Plus(c,Times(d,x))))),p),Times(Power(x,Subtract(Power(n,CN1),C1)),Power(Plus(g,Times(CN1,e,h,Power(f,CN1)),Times(h,Power(x,Power(n,CN1)),Power(f,CN1))),m)),x),x),x,Power(Plus(e,Times(f,x)),n)),x),And(FreeQ(List(a,b,c,d,e,f,g,h,m),x),IGtQ(p,C0),IntegerQ(Power(n,CN1))))),
IIntegrate(3433,Integrate(Times(Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Module(list(Set(k,If(FractionQ(n),Denominator(n),C1))),Dist(Times(k,Power(Power(f,Plus(m,C1)),CN1)),Subst(Integrate(ExpandIntegrand(Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(x,Times(k,n))))))),p),Times(Power(x,Subtract(k,C1)),Power(Plus(Times(f,g),Times(CN1,e,h),Times(h,Power(x,k))),m)),x),x),x,Power(Plus(e,Times(f,x)),Power(k,CN1))),x)),And(FreeQ(List(a,b,c,d,e,f,g,h),x),IGtQ(p,C0),IGtQ(m,C0)))),
IIntegrate(3434,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))),b_DEFAULT)),p_DEFAULT),Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Module(list(Set(k,If(FractionQ(n),Denominator(n),C1))),Dist(Times(k,Power(Power(f,Plus(m,C1)),CN1)),Subst(Integrate(ExpandIntegrand(Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(x,Times(k,n))))))),p),Times(Power(x,Subtract(k,C1)),Power(Plus(Times(f,g),Times(CN1,e,h),Times(h,Power(x,k))),m)),x),x),x,Power(Plus(e,Times(f,x)),Power(k,CN1))),x)),And(FreeQ(List(a,b,c,d,e,f,g,h),x),IGtQ(p,C0),IGtQ(m,C0)))),
IIntegrate(3435,Integrate(Times(Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(f,CN1),Subst(Integrate(Times(Power(Times(h,x,Power(f,CN1)),m),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(x,n)))))),p)),x),x,Plus(e,Times(f,x))),x),And(FreeQ(List(a,b,c,d,e,f,g,h,m),x),IGtQ(p,C0),EqQ(Subtract(Times(f,g),Times(e,h)),C0)))),
IIntegrate(3436,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))),b_DEFAULT)),p_DEFAULT),Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(f,CN1),Subst(Integrate(Times(Power(Times(h,x,Power(f,CN1)),m),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(x,n)))))),p)),x),x,Plus(e,Times(f,x))),x),And(FreeQ(List(a,b,c,d,e,f,g,h,m),x),IGtQ(p,C0),EqQ(Subtract(Times(f,g),Times(e,h)),C0)))),
IIntegrate(3437,Integrate(Times(Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(g,Times(h,x)),m),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(Plus(e,Times(f,x)),n)))))),p)),x),FreeQ(List(a,b,c,d,e,f,g,h,m,n,p),x))),
IIntegrate(3438,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))),b_DEFAULT)),p_DEFAULT),Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(g,Times(h,x)),m),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(Plus(e,Times(f,x)),n)))))),p)),x),FreeQ(List(a,b,c,d,e,f,g,h,m,n,p),x))),
IIntegrate(3439,Integrate(Times(Power(v_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(u_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(v,x),m),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(ExpandToSum(u,x),n)))))),p)),x),And(FreeQ(List(a,b,c,d,m,n,p),x),LinearQ(u,x),LinearQ(v,x),Not(And(LinearMatchQ(u,x),LinearMatchQ(v,x)))))),
IIntegrate(3440,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(u_,n_)))),b_DEFAULT)),p_DEFAULT),Power(v_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(v,x),m),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(ExpandToSum(u,x),n)))))),p)),x),And(FreeQ(List(a,b,c,d,m,n,p),x),LinearQ(u,x),LinearQ(v,x),Not(And(LinearMatchQ(u,x),LinearMatchQ(v,x))))))
  );
}
