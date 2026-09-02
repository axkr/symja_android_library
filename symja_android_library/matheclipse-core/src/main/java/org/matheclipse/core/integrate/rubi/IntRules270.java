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
class IntRules270 { 
  public static IAST RULES = List( 
IIntegrate(5401,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_),Power(Plus(d_,Times(e_DEFAULT,x_)),q_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(d,Times(e,x)),Plus(q,C1)),Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p),Power(Times(e,Plus(q,C1)),CN1)),x),Simp(Dist(Times(b,c,p,Power(Times(e,Plus(q,C1)),CN1)),Integrate(ExpandIntegrand(Power(Plus(a,Times(b,ArcTan(Times(c,x)))),Plus(p,CN1)),Times(Power(Plus(d,Times(e,x)),Plus(q,C1)),Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1)),x),x),x),x)),And(FreeQ(List(a,b,c,d,e),x),IGtQ(p,C1),IntegerQ(q),NeQ(q,CN1)))),
IIntegrate(5402,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_),Power(Plus(d_,Times(e_DEFAULT,x_)),q_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(d,Times(e,x)),Plus(q,C1)),Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p),Power(Times(e,Plus(q,C1)),CN1)),x),Simp(Dist(Times(b,c,p,Power(Times(e,Plus(q,C1)),CN1)),Integrate(ExpandIntegrand(Power(Plus(a,Times(b,ArcCot(Times(c,x)))),Plus(p,CN1)),Times(Power(Plus(d,Times(e,x)),Plus(q,C1)),Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1)),x),x),x),x)),And(FreeQ(List(a,b,c,d,e),x),IGtQ(p,C1),IntegerQ(q),NeQ(q,CN1)))),
IIntegrate(5403,Integrate(Times(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(Log(Plus(d,Times(e,x))),Plus(a,Times(b,ArcTan(Times(c,Power(x,n))))),Power(e,CN1)),x),Simp(Dist(Times(b,c,n,Power(e,CN1)),Integrate(Times(Power(x,Plus(n,CN1)),Log(Plus(d,Times(e,x))),Power(Plus(C1,Times(Sqr(c),Power(x,Times(C2,n)))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,n),x),IntegerQ(n)))),
IIntegrate(5404,Integrate(Times(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Plus(Simp(Times(Log(Plus(d,Times(e,x))),Plus(a,Times(b,ArcCot(Times(c,Power(x,n))))),Power(e,CN1)),x),Simp(Dist(Times(b,c,n,Power(e,CN1)),Integrate(Times(Power(x,Plus(n,CN1)),Log(Plus(d,Times(e,x))),Power(Plus(C1,Times(Sqr(c),Power(x,Times(C2,n)))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,n),x),IntegerQ(n)))),
IIntegrate(5405,Integrate(Times(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(With(list(Set(k,Denominator(n))),Simp(Dist(k,Subst(Integrate(Times(Power(x,Plus(k,CN1)),Plus(a,Times(b,ArcTan(Times(c,Power(x,Times(k,n)))))),Power(Plus(d,Times(e,Power(x,k))),CN1)),x),x,Power(x,Power(k,CN1))),x),x)),And(FreeQ(List(a,b,c,d,e),x),FractionQ(n)))),
IIntegrate(5406,Integrate(Times(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(With(list(Set(k,Denominator(n))),Simp(Dist(k,Subst(Integrate(Times(Power(x,Plus(k,CN1)),Plus(a,Times(b,ArcCot(Times(c,Power(x,Times(k,n)))))),Power(Plus(d,Times(e,Power(x,k))),CN1)),x),x,Power(x,Power(k,CN1))),x),x)),And(FreeQ(List(a,b,c,d,e),x),FractionQ(n)))),
IIntegrate(5407,Integrate(Times(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Plus(a,Times(b,ArcTan(Times(c,Power(x,n))))),Power(Times(e,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,c,n,Power(Times(e,Plus(m,C1)),CN1)),Integrate(Times(Power(x,Plus(n,CN1)),Power(Plus(d,Times(e,x)),Plus(m,C1)),Power(Plus(C1,Times(Sqr(c),Power(x,Times(C2,n)))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,m,n),x),NeQ(m,CN1)))),
IIntegrate(5408,Integrate(Times(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Plus(a,Times(b,ArcCot(Times(c,Power(x,n))))),Power(Times(e,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,c,n,Power(Times(e,Plus(m,C1)),CN1)),Integrate(Times(Power(x,Plus(n,CN1)),Power(Plus(d,Times(e,x)),Plus(m,C1)),Power(Plus(C1,Times(Sqr(c),Power(x,Times(C2,n)))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,m,n),x),NeQ(m,CN1)))),
IIntegrate(5409,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),Power(Plus(d_,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Plus(a,Times(b,ArcTan(Times(c,Power(x,n))))),p),Power(Plus(d,Times(e,x)),m),x),x),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(p,C1),IGtQ(m,C0)))),
IIntegrate(5410,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),Power(Plus(d_,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Plus(a,Times(b,ArcCot(Times(c,Power(x,n))))),p),Power(Plus(d,Times(e,x)),m),x),x),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(p,C1),IGtQ(m,C0)))),
IIntegrate(5411,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(d,Times(e,x)),m),Power(Plus(a,Times(b,ArcTan(Times(c,Power(x,n))))),p)),x),FreeQ(List(a,b,c,d,e,m,n,p),x))),
IIntegrate(5412,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(d,Times(e,x)),m),Power(Plus(a,Times(b,ArcCot(Times(c,Power(x,n))))),p)),x),FreeQ(List(a,b,c,d,e,m,n,p),x))),
IIntegrate(5413,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Subtract(Simp(Dist(Times(f,Power(e,CN1)),Integrate(Times(Power(Times(f,x),Plus(m,CN1)),Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p)),x),x),x),Simp(Dist(Times(d,f,Power(e,CN1)),Integrate(Times(Power(Times(f,x),Plus(m,CN1)),Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p),Power(Plus(d,Times(e,x)),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),EqQ(Plus(Times(Sqr(c),Sqr(d)),Sqr(e)),C0),GtQ(m,C0)))),
IIntegrate(5414,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Subtract(Simp(Dist(Times(f,Power(e,CN1)),Integrate(Times(Power(Times(f,x),Plus(m,CN1)),Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p)),x),x),x),Simp(Dist(Times(d,f,Power(e,CN1)),Integrate(Times(Power(Times(f,x),Plus(m,CN1)),Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p),Power(Plus(d,Times(e,x)),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),EqQ(Plus(Times(Sqr(c),Sqr(d)),Sqr(e)),C0),GtQ(m,C0)))),
IIntegrate(5415,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(x_,CN1),Power(Plus(d_,Times(e_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p),Log(Subtract(C2,Times(C2,Power(Plus(C1,Times(e,x,Power(d,CN1))),CN1)))),Power(d,CN1)),x),Simp(Dist(Times(b,c,p,Power(d,CN1)),Integrate(Times(Power(Plus(a,Times(b,ArcTan(Times(c,x)))),Plus(p,CN1)),Log(Subtract(C2,Times(C2,Power(Plus(C1,Times(e,x,Power(d,CN1))),CN1)))),Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e),x),IGtQ(p,C0),EqQ(Plus(Times(Sqr(c),Sqr(d)),Sqr(e)),C0)))),
IIntegrate(5416,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(x_,CN1),Power(Plus(d_,Times(e_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p),Log(Subtract(C2,Times(C2,Power(Plus(C1,Times(e,x,Power(d,CN1))),CN1)))),Power(d,CN1)),x),Simp(Dist(Times(b,c,p,Power(d,CN1)),Integrate(Times(Power(Plus(a,Times(b,ArcCot(Times(c,x)))),Plus(p,CN1)),Log(Subtract(C2,Times(C2,Power(Plus(C1,Times(e,x,Power(d,CN1))),CN1)))),Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e),x),IGtQ(p,C0),EqQ(Plus(Times(Sqr(c),Sqr(d)),Sqr(e)),C0)))),
IIntegrate(5417,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Times(f_DEFAULT,x_),m_),Power(Plus(d_,Times(e_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Subtract(Simp(Dist(Power(d,CN1),Integrate(Times(Power(Times(f,x),m),Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p)),x),x),x),Simp(Dist(Times(e,Power(Times(d,f),CN1)),Integrate(Times(Power(Times(f,x),Plus(m,C1)),Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p),Power(Plus(d,Times(e,x)),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),EqQ(Plus(Times(Sqr(c),Sqr(d)),Sqr(e)),C0),LtQ(m,CN1)))),
IIntegrate(5418,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Times(f_DEFAULT,x_),m_),Power(Plus(d_,Times(e_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Subtract(Simp(Dist(Power(d,CN1),Integrate(Times(Power(Times(f,x),m),Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p)),x),x),x),Simp(Dist(Times(e,Power(Times(d,f),CN1)),Integrate(Times(Power(Times(f,x),Plus(m,C1)),Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p),Power(Plus(d,Times(e,x)),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),EqQ(Plus(Times(Sqr(c),Sqr(d)),Sqr(e)),C0),LtQ(m,CN1)))),
IIntegrate(5419,Integrate(Times(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),q_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times(Power(Times(f,x),m),Power(Plus(d,Times(e,x)),q)),x))),Subtract(Simp(Dist(Plus(a,Times(b,ArcTan(Times(c,x)))),u,x),x),Simp(Dist(Times(b,c),Integrate(SimplifyIntegrand(Times(u,Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1)),x),x),x),x))),And(FreeQ(List(a,b,c,d,e,f,q),x),NeQ(q,CN1),IntegerQ(Times(C2,m)),Or(And(IGtQ(m,C0),IGtQ(q,C0)),And(ILtQ(Plus(m,q,C1),C0),LtQ(Times(m,q),C0)))))),
IIntegrate(5420,Integrate(Times(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),q_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times(Power(Times(f,x),m),Power(Plus(d,Times(e,x)),q)),x))),Plus(Simp(Dist(Plus(a,Times(b,ArcCot(Times(c,x)))),u,x),x),Simp(Dist(Times(b,c),Integrate(SimplifyIntegrand(Times(u,Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1)),x),x),x),x))),And(FreeQ(List(a,b,c,d,e,f,q),x),NeQ(q,CN1),IntegerQ(Times(C2,m)),Or(And(IGtQ(m,C0),IGtQ(q,C0)),And(ILtQ(Plus(m,q,C1),C0),LtQ(Times(m,q),C0))))))
  );
}
