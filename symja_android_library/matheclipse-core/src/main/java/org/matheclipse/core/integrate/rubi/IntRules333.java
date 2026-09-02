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
class IntRules333 { 
  public static IAST RULES = List( 
IIntegrate(6661,Integrate(Times(Sqr(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT))),Plus(d_DEFAULT,Times(Log(Plus(f_,Times(g_DEFAULT,Sqr(x_)))),e_DEFAULT)),x_),x_Symbol),
    Condition(Plus(Simp(Times(Plus(f,Times(g,Sqr(x))),Plus(d,Times(e,Log(Plus(f,Times(g,Sqr(x)))))),Sqr(Plus(a,Times(b,ArcTanh(Times(c,x))))),Power(Times(C2,g),CN1)),x),Negate(Simp(Times(e,Sqr(x),C1D2,Sqr(Plus(a,Times(b,ArcTanh(Times(c,x)))))),x)),Simp(Dist(Times(b,Power(c,CN1)),Integrate(Times(Plus(d,Times(e,Log(Plus(f,Times(g,Sqr(x)))))),Plus(a,Times(b,ArcTanh(Times(c,x))))),x),x),x),Simp(Dist(Times(b,c,e),Integrate(Times(Sqr(x),Plus(a,Times(b,ArcTanh(Times(c,x)))),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,f,g),x),EqQ(Plus(Times(Sqr(c),f),g),C0)))),
IIntegrate(6662,Integrate(Times(Sqr(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT))),Plus(d_DEFAULT,Times(Log(Plus(f_,Times(g_DEFAULT,Sqr(x_)))),e_DEFAULT)),x_),x_Symbol),
    Condition(Plus(Simp(Times(Plus(f,Times(g,Sqr(x))),Plus(d,Times(e,Log(Plus(f,Times(g,Sqr(x)))))),Sqr(Plus(a,Times(b,ArcCoth(Times(c,x))))),Power(Times(C2,g),CN1)),x),Negate(Simp(Times(e,Sqr(x),C1D2,Sqr(Plus(a,Times(b,ArcCoth(Times(c,x)))))),x)),Simp(Dist(Times(b,Power(c,CN1)),Integrate(Times(Plus(d,Times(e,Log(Plus(f,Times(g,Sqr(x)))))),Plus(a,Times(b,ArcCoth(Times(c,x))))),x),x),x),Simp(Dist(Times(b,c,e),Integrate(Times(Sqr(x),Plus(a,Times(b,ArcCoth(Times(c,x)))),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,f,g),x),EqQ(Plus(Times(Sqr(c),f),g),C0)))),
IIntegrate(6663,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Unintegrable(Times(u,Power(Plus(a,Times(b,ArcTanh(Times(c,x)))),p)),x),And(FreeQ(List(a,b,c,p),x),Or(EqQ(u,C1),MatchQ(u,Condition(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x)),q_DEFAULT),FreeQ(list(d,e,q),x))),MatchQ(u,Condition(Times(Power(Times(f_DEFAULT,x),m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x)),q_DEFAULT)),FreeQ(List(d,e,f,m,q),x))),MatchQ(u,Condition(Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x))),q_DEFAULT),FreeQ(list(d,e,q),x))),MatchQ(u,Condition(Times(Power(Times(f_DEFAULT,x),m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x))),q_DEFAULT)),FreeQ(List(d,e,f,m,q),x))))))),
IIntegrate(6664,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Unintegrable(Times(u,Power(Plus(a,Times(b,ArcCoth(Times(c,x)))),p)),x),And(FreeQ(List(a,b,c,p),x),Or(EqQ(u,C1),MatchQ(u,Condition(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x)),q_DEFAULT),FreeQ(list(d,e,q),x))),MatchQ(u,Condition(Times(Power(Times(f_DEFAULT,x),m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x)),q_DEFAULT)),FreeQ(List(d,e,f,m,q),x))),MatchQ(u,Condition(Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x))),q_DEFAULT),FreeQ(list(d,e,q),x))),MatchQ(u,Condition(Times(Power(Times(f_DEFAULT,x),m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x))),q_DEFAULT)),FreeQ(List(d,e,f,m,q),x))))))),
IIntegrate(6665,Integrate(Power(Plus(a_DEFAULT,Times(ArcTanh(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Power(d,CN1),Subst(Integrate(Power(Plus(a,Times(b,ArcTanh(x))),p),x),x,Plus(c,Times(d,x))),x),x),And(FreeQ(List(a,b,c,d),x),IGtQ(p,C0)))),
IIntegrate(6666,Integrate(Power(Plus(a_DEFAULT,Times(ArcCoth(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Power(d,CN1),Subst(Integrate(Power(Plus(a,Times(b,ArcCoth(x))),p),x),x,Plus(c,Times(d,x))),x),x),And(FreeQ(List(a,b,c,d),x),IGtQ(p,C0)))),
IIntegrate(6667,Integrate(Power(Plus(a_DEFAULT,Times(ArcTanh(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,ArcTanh(Plus(c,Times(d,x))))),p),x),And(FreeQ(List(a,b,c,d,p),x),Not(IGtQ(p,C0))))),
IIntegrate(6668,Integrate(Power(Plus(a_DEFAULT,Times(ArcCoth(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,ArcCoth(Plus(c,Times(d,x))))),p),x),And(FreeQ(List(a,b,c,d,p),x),Not(IGtQ(p,C0))))),
IIntegrate(6669,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(d,CN1),Subst(Integrate(Times(Power(Times(f,x,Power(d,CN1)),m),Power(Plus(a,Times(b,ArcTanh(x))),p)),x),x,Plus(c,Times(d,x))),x),x),And(FreeQ(List(a,b,c,d,e,f,m),x),EqQ(Subtract(Times(d,e),Times(c,f)),C0),IGtQ(p,C0)))),
IIntegrate(6670,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(d,CN1),Subst(Integrate(Times(Power(Times(f,x,Power(d,CN1)),m),Power(Plus(a,Times(b,ArcCoth(x))),p)),x),x,Plus(c,Times(d,x))),x),x),And(FreeQ(List(a,b,c,d,e,f,m),x),EqQ(Subtract(Times(d,e),Times(c,f)),C0),IGtQ(p,C0)))),
IIntegrate(6671,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Power(Plus(a,Times(b,ArcTanh(Plus(c,Times(d,x))))),p),Power(Times(f,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,d,p,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Power(Plus(a,Times(b,ArcTanh(Plus(c,Times(d,x))))),Plus(p,CN1)),Power(Subtract(C1,Sqr(Plus(c,Times(d,x)))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),ILtQ(m,CN1)))),
IIntegrate(6672,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Power(Plus(a,Times(b,ArcCoth(Plus(c,Times(d,x))))),p),Power(Times(f,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,d,p,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Power(Plus(a,Times(b,ArcCoth(Plus(c,Times(d,x))))),Plus(p,CN1)),Power(Subtract(C1,Sqr(Plus(c,Times(d,x)))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),ILtQ(m,CN1)))),
IIntegrate(6673,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(d,CN1),Subst(Integrate(Times(Power(Plus(Times(Subtract(Times(d,e),Times(c,f)),Power(d,CN1)),Times(f,x,Power(d,CN1))),m),Power(Plus(a,Times(b,ArcTanh(x))),p)),x),x,Plus(c,Times(d,x))),x),x),And(FreeQ(List(a,b,c,d,e,f,m),x),IGtQ(p,C0)))),
IIntegrate(6674,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(d,CN1),Subst(Integrate(Times(Power(Plus(Times(Subtract(Times(d,e),Times(c,f)),Power(d,CN1)),Times(f,x,Power(d,CN1))),m),Power(Plus(a,Times(b,ArcCoth(x))),p)),x),x,Plus(c,Times(d,x))),x),x),And(FreeQ(List(a,b,c,d,e,f,m),x),IGtQ(p,C0)))),
IIntegrate(6675,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(b,ArcTanh(Plus(c,Times(d,x))))),p)),x),And(FreeQ(List(a,b,c,d,e,f,m,p),x),Not(IGtQ(p,C0))))),
IIntegrate(6676,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(b,ArcCoth(Plus(c,Times(d,x))))),p)),x),And(FreeQ(List(a,b,c,d,e,f,m,p),x),Not(IGtQ(p,C0))))),
IIntegrate(6677,Integrate(Times(ArcTanh(Plus(c_,Times(d_DEFAULT,x_))),Power(Plus(e_,Times(f_DEFAULT,Power(x_,n_DEFAULT))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Dist(C1D2,Integrate(Times(Log(Plus(C1,c,Times(d,x))),Power(Plus(e,Times(f,Power(x,n))),CN1)),x),x),x),Simp(Dist(C1D2,Integrate(Times(Log(Subtract(Subtract(C1,c),Times(d,x))),Power(Plus(e,Times(f,Power(x,n))),CN1)),x),x),x)),And(FreeQ(List(c,d,e,f),x),RationalQ(n)))),
IIntegrate(6678,Integrate(Times(ArcCoth(Plus(c_,Times(d_DEFAULT,x_))),Power(Plus(e_,Times(f_DEFAULT,Power(x_,n_DEFAULT))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Dist(C1D2,Integrate(Times(Log(Times(Plus(C1,c,Times(d,x)),Power(Plus(c,Times(d,x)),CN1))),Power(Plus(e,Times(f,Power(x,n))),CN1)),x),x),x),Simp(Dist(C1D2,Integrate(Times(Log(Times(Plus(CN1,c,Times(d,x)),Power(Plus(c,Times(d,x)),CN1))),Power(Plus(e,Times(f,Power(x,n))),CN1)),x),x),x)),And(FreeQ(List(c,d,e,f),x),RationalQ(n)))),
IIntegrate(6679,Integrate(Times(ArcTanh(Plus(c_,Times(d_DEFAULT,x_))),Power(Plus(e_,Times(f_DEFAULT,Power(x_,n_))),CN1)),x_Symbol),
    Condition(Unintegrable(Times(ArcTanh(Plus(c,Times(d,x))),Power(Plus(e,Times(f,Power(x,n))),CN1)),x),And(FreeQ(List(c,d,e,f,n),x),Not(RationalQ(n))))),
IIntegrate(6680,Integrate(Times(ArcCoth(Plus(c_,Times(d_DEFAULT,x_))),Power(Plus(e_,Times(f_DEFAULT,Power(x_,n_))),CN1)),x_Symbol),
    Condition(Unintegrable(Times(ArcCoth(Plus(c,Times(d,x))),Power(Plus(e,Times(f,Power(x,n))),CN1)),x),And(FreeQ(List(c,d,e,f,n),x),Not(RationalQ(n)))))
  );
}
