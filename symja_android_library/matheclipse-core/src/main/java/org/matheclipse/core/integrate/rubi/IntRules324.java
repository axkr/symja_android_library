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
class IntRules324 { 
  public static IAST RULES = List( 
IIntegrate(6481,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT)),p_),Power(Plus(d_,Times(e_DEFAULT,x_)),q_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(d,Times(e,x)),Plus(q,C1)),Power(Plus(a,Times(b,ArcCoth(Times(c,x)))),p),Power(Times(e,Plus(q,C1)),CN1)),x),Simp(Star(Times(b,c,p,Power(Times(e,Plus(q,C1)),CN1)),Integrate(ExpandIntegrand(Power(Plus(a,Times(b,ArcCoth(Times(c,x)))),Subtract(p,C1)),Times(Power(Plus(d,Times(e,x)),Plus(q,C1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1)),x),x)),x)),And(FreeQ(List(a,b,c,d,e),x),IGtQ(p,C1),IntegerQ(q),NeQ(q,CN1)))),
IIntegrate(6482,Integrate(Times(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(Log(Plus(d,Times(e,x))),Plus(a,Times(b,ArcTanh(Times(c,Power(x,n))))),Power(e,CN1)),x),Simp(Star(Times(b,c,n,Power(e,CN1)),Integrate(Times(Power(x,Subtract(n,C1)),Log(Plus(d,Times(e,x))),Power(Subtract(C1,Times(Sqr(c),Power(x,Times(C2,n)))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,n),x),IntegerQ(n)))),
IIntegrate(6483,Integrate(Times(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(Log(Plus(d,Times(e,x))),Plus(a,Times(b,ArcCoth(Times(c,Power(x,n))))),Power(e,CN1)),x),Simp(Star(Times(b,c,n,Power(e,CN1)),Integrate(Times(Power(x,Subtract(n,C1)),Log(Plus(d,Times(e,x))),Power(Subtract(C1,Times(Sqr(c),Power(x,Times(C2,n)))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,n),x),IntegerQ(n)))),
IIntegrate(6484,Integrate(Times(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(With(list(Set(k,Denominator(n))),Simp(Star(k,Subst(Integrate(Times(Power(x,Subtract(k,C1)),Plus(a,Times(b,ArcTanh(Times(c,Power(x,Times(k,n)))))),Power(Plus(d,Times(e,Power(x,k))),CN1)),x),x,Power(x,Power(k,CN1)))),x)),And(FreeQ(List(a,b,c,d,e),x),FractionQ(n)))),
IIntegrate(6485,Integrate(Times(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(With(list(Set(k,Denominator(n))),Simp(Star(k,Subst(Integrate(Times(Power(x,Subtract(k,C1)),Plus(a,Times(b,ArcCoth(Times(c,Power(x,Times(k,n)))))),Power(Plus(d,Times(e,Power(x,k))),CN1)),x),x,Power(x,Power(k,CN1)))),x)),And(FreeQ(List(a,b,c,d,e),x),FractionQ(n)))),
IIntegrate(6486,Integrate(Times(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Plus(a,Times(b,ArcTanh(Times(c,Power(x,n))))),Power(Times(e,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,c,n,Power(Times(e,Plus(m,C1)),CN1)),Integrate(Times(Power(x,Subtract(n,C1)),Power(Plus(d,Times(e,x)),Plus(m,C1)),Power(Subtract(C1,Times(Sqr(c),Power(x,Times(C2,n)))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,m,n),x),NeQ(m,CN1)))),
IIntegrate(6487,Integrate(Times(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Plus(a,Times(b,ArcCoth(Times(c,Power(x,n))))),Power(Times(e,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,c,n,Power(Times(e,Plus(m,C1)),CN1)),Integrate(Times(Power(x,Subtract(n,C1)),Power(Plus(d,Times(e,x)),Plus(m,C1)),Power(Subtract(C1,Times(Sqr(c),Power(x,Times(C2,n)))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,m,n),x),NeQ(m,CN1)))),
IIntegrate(6488,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),Power(Plus(d_,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Plus(a,Times(b,ArcTanh(Times(c,Power(x,n))))),p),Power(Plus(d,Times(e,x)),m),x),x),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(p,C1),IGtQ(m,C0)))),
IIntegrate(6489,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),Power(Plus(d_,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Plus(a,Times(b,ArcCoth(Times(c,Power(x,n))))),p),Power(Plus(d,Times(e,x)),m),x),x),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(p,C1),IGtQ(m,C0)))),
IIntegrate(6490,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(d,Times(e,x)),m),Power(Plus(a,Times(b,ArcTanh(Times(c,Power(x,n))))),p)),x),FreeQ(List(a,b,c,d,e,m,n,p),x))),
IIntegrate(6491,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(d,Times(e,x)),m),Power(Plus(a,Times(b,ArcCoth(Times(c,Power(x,n))))),p)),x),FreeQ(List(a,b,c,d,e,m,n,p),x))),
IIntegrate(6492,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Subtract(Simp(Star(Times(f,Power(e,CN1)),Integrate(Times(Power(Times(f,x),Subtract(m,C1)),Power(Plus(a,Times(b,ArcTanh(Times(c,x)))),p)),x)),x),Simp(Star(Times(d,f,Power(e,CN1)),Integrate(Times(Power(Times(f,x),Subtract(m,C1)),Power(Plus(a,Times(b,ArcTanh(Times(c,x)))),p),Power(Plus(d,Times(e,x)),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),EqQ(Subtract(Times(Sqr(c),Sqr(d)),Sqr(e)),C0),GtQ(m,C0)))),
IIntegrate(6493,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Subtract(Simp(Star(Times(f,Power(e,CN1)),Integrate(Times(Power(Times(f,x),Subtract(m,C1)),Power(Plus(a,Times(b,ArcCoth(Times(c,x)))),p)),x)),x),Simp(Star(Times(d,f,Power(e,CN1)),Integrate(Times(Power(Times(f,x),Subtract(m,C1)),Power(Plus(a,Times(b,ArcCoth(Times(c,x)))),p),Power(Plus(d,Times(e,x)),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),EqQ(Subtract(Times(Sqr(c),Sqr(d)),Sqr(e)),C0),GtQ(m,C0)))),
IIntegrate(6494,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(x_,CN1),Power(Plus(d_,Times(e_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(a,Times(b,ArcTanh(Times(c,x)))),p),Log(Subtract(C2,Times(C2,Power(Plus(C1,Times(e,x,Power(d,CN1))),CN1)))),Power(d,CN1)),x),Simp(Star(Times(b,c,p,Power(d,CN1)),Integrate(Times(Power(Plus(a,Times(b,ArcTanh(Times(c,x)))),Subtract(p,C1)),Log(Subtract(C2,Times(C2,Power(Plus(C1,Times(e,x,Power(d,CN1))),CN1)))),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e),x),IGtQ(p,C0),EqQ(Subtract(Times(Sqr(c),Sqr(d)),Sqr(e)),C0)))),
IIntegrate(6495,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(x_,CN1),Power(Plus(d_,Times(e_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(a,Times(b,ArcCoth(Times(c,x)))),p),Log(Subtract(C2,Times(C2,Power(Plus(C1,Times(e,x,Power(d,CN1))),CN1)))),Power(d,CN1)),x),Simp(Star(Times(b,c,p,Power(d,CN1)),Integrate(Times(Power(Plus(a,Times(b,ArcCoth(Times(c,x)))),Subtract(p,C1)),Log(Subtract(C2,Times(C2,Power(Plus(C1,Times(e,x,Power(d,CN1))),CN1)))),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e),x),IGtQ(p,C0),EqQ(Subtract(Times(Sqr(c),Sqr(d)),Sqr(e)),C0)))),
IIntegrate(6496,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Times(f_DEFAULT,x_),m_),Power(Plus(d_,Times(e_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Subtract(Simp(Star(Power(d,CN1),Integrate(Times(Power(Times(f,x),m),Power(Plus(a,Times(b,ArcTanh(Times(c,x)))),p)),x)),x),Simp(Star(Times(e,Power(Times(d,f),CN1)),Integrate(Times(Power(Times(f,x),Plus(m,C1)),Power(Plus(a,Times(b,ArcTanh(Times(c,x)))),p),Power(Plus(d,Times(e,x)),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),EqQ(Subtract(Times(Sqr(c),Sqr(d)),Sqr(e)),C0),LtQ(m,CN1)))),
IIntegrate(6497,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Times(f_DEFAULT,x_),m_),Power(Plus(d_,Times(e_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Subtract(Simp(Star(Power(d,CN1),Integrate(Times(Power(Times(f,x),m),Power(Plus(a,Times(b,ArcCoth(Times(c,x)))),p)),x)),x),Simp(Star(Times(e,Power(Times(d,f),CN1)),Integrate(Times(Power(Times(f,x),Plus(m,C1)),Power(Plus(a,Times(b,ArcCoth(Times(c,x)))),p),Power(Plus(d,Times(e,x)),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),EqQ(Subtract(Times(Sqr(c),Sqr(d)),Sqr(e)),C0),LtQ(m,CN1)))),
IIntegrate(6498,Integrate(Times(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),q_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times(Power(Times(f,x),m),Power(Plus(d,Times(e,x)),q)),x))),Subtract(Simp(Star(Plus(a,Times(b,ArcTanh(Times(c,x)))),u),x),Simp(Star(Times(b,c),Integrate(SimplifyIntegrand(Times(u,Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1)),x),x)),x))),And(FreeQ(List(a,b,c,d,e,f,q),x),NeQ(q,CN1),IntegerQ(Times(C2,m)),Or(And(IGtQ(m,C0),IGtQ(q,C0)),And(ILtQ(Plus(m,q,C1),C0),LtQ(Times(m,q),C0)))))),
IIntegrate(6499,Integrate(Times(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),q_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times(Power(Times(f,x),m),Power(Plus(d,Times(e,x)),q)),x))),Subtract(Simp(Star(Plus(a,Times(b,ArcCoth(Times(c,x)))),u),x),Simp(Star(Times(b,c),Integrate(SimplifyIntegrand(Times(u,Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1)),x),x)),x))),And(FreeQ(List(a,b,c,d,e,f,q),x),NeQ(q,CN1),IntegerQ(Times(C2,m)),Or(And(IGtQ(m,C0),IGtQ(q,C0)),And(ILtQ(Plus(m,q,C1),C0),LtQ(Times(m,q),C0)))))),
IIntegrate(6500,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT)),p_),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),q_)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times(Power(Times(f,x),m),Power(Plus(d,Times(e,x)),q)),x))),Subtract(Simp(Star(Power(Plus(a,Times(b,ArcTanh(Times(c,x)))),p),u),x),Simp(Star(Times(b,c,p),Integrate(ExpandIntegrand(Power(Plus(a,Times(b,ArcTanh(Times(c,x)))),Subtract(p,C1)),Times(u,Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1)),x),x)),x))),And(FreeQ(List(a,b,c,d,e,f,q),x),IGtQ(p,C1),EqQ(Subtract(Times(Sqr(c),Sqr(d)),Sqr(e)),C0),IntegersQ(m,q),NeQ(m,CN1),NeQ(q,CN1),ILtQ(Plus(m,q,C1),C0),LtQ(Times(m,q),C0))))
  );
}
