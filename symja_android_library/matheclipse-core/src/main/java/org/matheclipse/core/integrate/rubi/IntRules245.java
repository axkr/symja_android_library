package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class IntRules245 { 
  public static IAST RULES = List( 
IIntegrate(4901,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(b,p,Power(Plus(d,Times(e,Sqr(x))),Plus(q,C1)),Power(Plus(a,Times(b,ArcCot(Times(c,x)))),Subtract(p,C1)),Power(Times(C4,c,d,Sqr(Plus(q,C1))),CN1)),x)),Dist(Times(Plus(Times(C2,q),C3),Power(Times(C2,d,Plus(q,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,Sqr(x))),Plus(q,C1)),Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p)),x),x),Negate(Dist(Times(Sqr(b),p,Subtract(p,C1),Power(Times(C4,Sqr(Plus(q,C1))),CN1)),Integrate(Times(Power(Plus(d,Times(e,Sqr(x))),q),Power(Plus(a,Times(b,ArcCot(Times(c,x)))),Subtract(p,C2))),x),x)),Negate(Simp(Times(x,Power(Plus(d,Times(e,Sqr(x))),Plus(q,C1)),Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p),Power(Times(C2,d,Plus(q,C1)),CN1)),x))),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),LtQ(q,CN1),GtQ(p,C1),NeQ(q,QQ(-3L,2L))))),
IIntegrate(4902,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(d,Times(e,Sqr(x))),Plus(q,C1)),Power(Plus(a,Times(b,ArcTan(Times(c,x)))),Plus(p,C1)),Power(Times(b,c,d,Plus(p,C1)),CN1)),x),Dist(Times(C2,c,Plus(q,C1),Power(Times(b,Plus(p,C1)),CN1)),Integrate(Times(x,Power(Plus(d,Times(e,Sqr(x))),q),Power(Plus(a,Times(b,ArcTan(Times(c,x)))),Plus(p,C1))),x),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),LtQ(q,CN1),LtQ(p,CN1)))),
IIntegrate(4903,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(Power(Plus(d,Times(e,Sqr(x))),Plus(q,C1)),Power(Plus(a,Times(b,ArcCot(Times(c,x)))),Plus(p,C1)),Power(Times(b,c,d,Plus(p,C1)),CN1)),x)),Dist(Times(C2,c,Plus(q,C1),Power(Times(b,Plus(p,C1)),CN1)),Integrate(Times(x,Power(Plus(d,Times(e,Sqr(x))),q),Power(Plus(a,Times(b,ArcCot(Times(c,x)))),Plus(p,C1))),x),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),LtQ(q,CN1),LtQ(p,CN1)))),
IIntegrate(4904,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Dist(Times(Power(d,q),Power(c,CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),p),Power(Power(Cos(x),Times(C2,Plus(q,C1))),CN1)),x),x,ArcTan(Times(c,x))),x),And(FreeQ(List(a,b,c,d,e,p),x),EqQ(e,Times(Sqr(c),d)),ILtQ(Times(C2,Plus(q,C1)),C0),Or(IntegerQ(q),GtQ(d,C0))))),
IIntegrate(4905,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Dist(Times(Power(d,Plus(q,C1D2)),Sqrt(Plus(C1,Times(Sqr(c),Sqr(x)))),Power(Plus(d,Times(e,Sqr(x))),CN1D2)),Integrate(Times(Power(Plus(C1,Times(Sqr(c),Sqr(x))),q),Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p)),x),x),And(FreeQ(List(a,b,c,d,e,p),x),EqQ(e,Times(Sqr(c),d)),ILtQ(Times(C2,Plus(q,C1)),C0),Not(Or(IntegerQ(q),GtQ(d,C0)))))),
IIntegrate(4906,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Negate(Dist(Times(Power(d,q),Power(c,CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),p),Power(Power(Sin(x),Times(C2,Plus(q,C1))),CN1)),x),x,ArcCot(Times(c,x))),x)),And(FreeQ(List(a,b,c,d,e,p),x),EqQ(e,Times(Sqr(c),d)),ILtQ(Times(C2,Plus(q,C1)),C0),IntegerQ(q)))),
IIntegrate(4907,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Negate(Dist(Times(Power(d,Plus(q,C1D2)),x,Sqrt(Times(Plus(C1,Times(Sqr(c),Sqr(x))),Power(Times(Sqr(c),Sqr(x)),CN1))),Power(Plus(d,Times(e,Sqr(x))),CN1D2)),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),p),Power(Power(Sin(x),Times(C2,Plus(q,C1))),CN1)),x),x,ArcCot(Times(c,x))),x)),And(FreeQ(List(a,b,c,d,e,p),x),EqQ(e,Times(Sqr(c),d)),ILtQ(Times(C2,Plus(q,C1)),C0),Not(IntegerQ(q))))),
IIntegrate(4908,Integrate(Times(ArcTan(Times(c_DEFAULT,x_)),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Subtract(Dist(Times(C1D2,CI),Integrate(Times(Log(Subtract(C1,Times(CI,c,x))),Power(Plus(d,Times(e,Sqr(x))),CN1)),x),x),Dist(Times(C1D2,CI),Integrate(Times(Log(Plus(C1,Times(CI,c,x))),Power(Plus(d,Times(e,Sqr(x))),CN1)),x),x)),FreeQ(List(c,d,e),x))),
IIntegrate(4909,Integrate(Times(ArcCot(Times(c_DEFAULT,x_)),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Subtract(Dist(Times(C1D2,CI),Integrate(Times(Log(Subtract(C1,Times(CI,Power(Times(c,x),CN1)))),Power(Plus(d,Times(e,Sqr(x))),CN1)),x),x),Dist(Times(C1D2,CI),Integrate(Times(Log(Plus(C1,Times(CI,Power(Times(c,x),CN1)))),Power(Plus(d,Times(e,Sqr(x))),CN1)),x),x)),FreeQ(List(c,d,e),x))),
IIntegrate(4910,Integrate(Times(Plus(Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT),a_),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Plus(Dist(a,Integrate(Power(Plus(d,Times(e,Sqr(x))),CN1),x),x),Dist(b,Integrate(Times(ArcTan(Times(c,x)),Power(Plus(d,Times(e,Sqr(x))),CN1)),x),x)),FreeQ(List(a,b,c,d,e),x))),
IIntegrate(4911,Integrate(Times(Plus(Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT),a_),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Plus(Dist(a,Integrate(Power(Plus(d,Times(e,Sqr(x))),CN1),x),x),Dist(b,Integrate(Times(ArcCot(Times(c,x)),Power(Plus(d,Times(e,Sqr(x))),CN1)),x),x)),FreeQ(List(a,b,c,d,e),x))),
IIntegrate(4912,Integrate(Times(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(With(List(Set(u,IntHide(Power(Plus(d,Times(e,Sqr(x))),q),x))),Subtract(Dist(Plus(a,Times(b,ArcTan(Times(c,x)))),u,x),Dist(Times(b,c),Integrate(Times(u,Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1)),x),x))),And(FreeQ(List(a,b,c,d,e),x),Or(IntegerQ(q),ILtQ(Plus(q,C1D2),C0))))),
IIntegrate(4913,Integrate(Times(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(With(List(Set(u,IntHide(Power(Plus(d,Times(e,Sqr(x))),q),x))),Plus(Dist(Plus(a,Times(b,ArcCot(Times(c,x)))),u,x),Dist(Times(b,c),Integrate(Times(u,Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1)),x),x))),And(FreeQ(List(a,b,c,d,e),x),Or(IntegerQ(q),ILtQ(Plus(q,C1D2),C0))))),
IIntegrate(4914,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p),Power(Plus(d,Times(e,Sqr(x))),q),x),x),And(FreeQ(List(a,b,c,d,e),x),IntegerQ(q),IGtQ(p,C0)))),
IIntegrate(4915,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p),Power(Plus(d,Times(e,Sqr(x))),q),x),x),And(FreeQ(List(a,b,c,d,e),x),IntegerQ(q),IGtQ(p,C0)))),
IIntegrate(4916,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Times(f_DEFAULT,x_),m_),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Subtract(Dist(Times(Sqr(f),Power(e,CN1)),Integrate(Times(Power(Times(f,x),Subtract(m,C2)),Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p)),x),x),Dist(Times(d,Sqr(f),Power(e,CN1)),Integrate(Times(Power(Times(f,x),Subtract(m,C2)),Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p),Power(Plus(d,Times(e,Sqr(x))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),GtQ(p,C0),GtQ(m,C1)))),
IIntegrate(4917,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Times(f_DEFAULT,x_),m_),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Subtract(Dist(Times(Sqr(f),Power(e,CN1)),Integrate(Times(Power(Times(f,x),Subtract(m,C2)),Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p)),x),x),Dist(Times(d,Sqr(f),Power(e,CN1)),Integrate(Times(Power(Times(f,x),Subtract(m,C2)),Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p),Power(Plus(d,Times(e,Sqr(x))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),GtQ(p,C0),GtQ(m,C1)))),
IIntegrate(4918,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Times(f_DEFAULT,x_),m_),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Subtract(Dist(Power(d,CN1),Integrate(Times(Power(Times(f,x),m),Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p)),x),x),Dist(Times(e,Power(Times(d,Sqr(f)),CN1)),Integrate(Times(Power(Times(f,x),Plus(m,C2)),Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p),Power(Plus(d,Times(e,Sqr(x))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),GtQ(p,C0),LtQ(m,CN1)))),
IIntegrate(4919,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Times(f_DEFAULT,x_),m_),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Subtract(Dist(Power(d,CN1),Integrate(Times(Power(Times(f,x),m),Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p)),x),x),Dist(Times(e,Power(Times(d,Sqr(f)),CN1)),Integrate(Times(Power(Times(f,x),Plus(m,C2)),Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p),Power(Plus(d,Times(e,Sqr(x))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),GtQ(p,C0),LtQ(m,CN1)))),
IIntegrate(4920,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),x_,Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Subtract(Negate(Simp(Times(CI,Power(Plus(a,Times(b,ArcTan(Times(c,x)))),Plus(p,C1)),Power(Times(b,e,Plus(p,C1)),CN1)),x)),Dist(Power(Times(c,d),CN1),Integrate(Times(Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p),Power(Subtract(CI,Times(c,x)),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),IGtQ(p,C0))))
  );
}
