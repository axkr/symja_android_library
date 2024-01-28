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
class IntRules343 { 
  public static IAST RULES = List( 
IIntegrate(6861,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSech(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(x_,m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Star(Times(CN1,Sqrt(Plus(d,Times(e,Sqr(x)))),Power(Times(x,Sqrt(Plus(e,Times(d,Power(x,CN2))))),CN1)),Subst(Integrate(Times(Power(Plus(e,Times(d,Sqr(x))),p),Power(Plus(a,Times(b,ArcCosh(Times(x,Power(c,CN1))))),n),Power(Power(x,Plus(m,Times(C2,Plus(p,C1)))),CN1)),x),x,Power(x,CN1))),x),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(n,C0),EqQ(Plus(Times(Sqr(c),d),e),C0),IntegerQ(m),IntegerQ(Plus(p,C1D2)),Not(And(GtQ(e,C0),LtQ(d,C0)))))),
IIntegrate(6862,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCsch(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(x_,m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Star(Times(CN1,Sqrt(Plus(d,Times(e,Sqr(x)))),Power(Times(x,Sqrt(Plus(e,Times(d,Power(x,CN2))))),CN1)),Subst(Integrate(Times(Power(Plus(e,Times(d,Sqr(x))),p),Power(Plus(a,Times(b,ArcSinh(Times(x,Power(c,CN1))))),n),Power(Power(x,Plus(m,Times(C2,Plus(p,C1)))),CN1)),x),x,Power(x,CN1))),x),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(n,C0),EqQ(Subtract(e,Times(Sqr(c),d)),C0),IntegerQ(m),IntegerQ(Plus(p,C1D2)),Not(And(GtQ(e,C0),LtQ(d,C0)))))),
IIntegrate(6863,Integrate(Times(Plus(a_DEFAULT,Times(ArcSech(Times(c_DEFAULT,x_)),b_DEFAULT)),u_),x_Symbol),
    Condition(With(list(Set(v,IntHide(u,x))),Condition(Plus(Simp(Star(Plus(a,Times(b,ArcSech(Times(c,x)))),v),x),Simp(Star(Times(b,Sqrt(Subtract(C1,Times(Sqr(c),Sqr(x)))),Power(Times(c,x,Sqrt(Plus(CN1,Power(Times(c,x),CN1))),Sqrt(Plus(C1,Power(Times(c,x),CN1)))),CN1)),Integrate(SimplifyIntegrand(Times(v,Power(Times(x,Sqrt(Subtract(C1,Times(Sqr(c),Sqr(x))))),CN1)),x),x)),x)),InverseFunctionFreeQ(v,x))),FreeQ(list(a,b,c),x))),
IIntegrate(6864,Integrate(Times(Plus(a_DEFAULT,Times(ArcCsch(Times(c_DEFAULT,x_)),b_DEFAULT)),u_),x_Symbol),
    Condition(With(list(Set(v,IntHide(u,x))),Condition(Plus(Simp(Star(Plus(a,Times(b,ArcCsch(Times(c,x)))),v),x),Simp(Star(Times(b,Power(c,CN1)),Integrate(SimplifyIntegrand(Times(v,Power(Times(Sqr(x),Sqrt(Plus(C1,Power(Times(Sqr(c),Sqr(x)),CN1)))),CN1)),x),x)),x)),InverseFunctionFreeQ(v,x))),FreeQ(list(a,b,c),x))),
IIntegrate(6865,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSech(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Unintegrable(Times(u,Power(Plus(a,Times(b,ArcSech(Times(c,x)))),n)),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(6866,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCsch(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Unintegrable(Times(u,Power(Plus(a,Times(b,ArcCsch(Times(c,x)))),n)),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(6867,Integrate(ArcSech(Plus(c_,Times(d_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Times(Plus(c,Times(d,x)),ArcSech(Plus(c,Times(d,x))),Power(d,CN1)),x),Integrate(Times(Sqrt(Times(Subtract(Subtract(C1,c),Times(d,x)),Power(Plus(C1,c,Times(d,x)),CN1))),Power(Subtract(Subtract(C1,c),Times(d,x)),CN1)),x)),FreeQ(list(c,d),x))),
IIntegrate(6868,Integrate(ArcCsch(Plus(c_,Times(d_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Times(Plus(c,Times(d,x)),ArcCsch(Plus(c,Times(d,x))),Power(d,CN1)),x),Integrate(Power(Times(Plus(c,Times(d,x)),Sqrt(Plus(C1,Power(Plus(c,Times(d,x)),CN2)))),CN1),x)),FreeQ(list(c,d),x))),
IIntegrate(6869,Integrate(Power(Plus(a_DEFAULT,Times(ArcSech(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Star(Power(d,CN1),Subst(Integrate(Power(Plus(a,Times(b,ArcSech(x))),p),x),x,Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d),x),IGtQ(p,C0)))),
IIntegrate(6870,Integrate(Power(Plus(a_DEFAULT,Times(ArcCsch(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Star(Power(d,CN1),Subst(Integrate(Power(Plus(a,Times(b,ArcCsch(x))),p),x),x,Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d),x),IGtQ(p,C0)))),
IIntegrate(6871,Integrate(Power(Plus(a_DEFAULT,Times(ArcSech(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,ArcSech(Plus(c,Times(d,x))))),p),x),And(FreeQ(List(a,b,c,d,p),x),Not(IGtQ(p,C0))))),
IIntegrate(6872,Integrate(Power(Plus(a_DEFAULT,Times(ArcCsch(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,ArcCsch(Plus(c,Times(d,x))))),p),x),And(FreeQ(List(a,b,c,d,p),x),Not(IGtQ(p,C0))))),
IIntegrate(6873,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSech(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(d,CN1),Subst(Integrate(Times(Power(Times(f,x,Power(d,CN1)),m),Power(Plus(a,Times(b,ArcSech(x))),p)),x),x,Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d,e,f,m),x),EqQ(Subtract(Times(d,e),Times(c,f)),C0),IGtQ(p,C0)))),
IIntegrate(6874,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCsch(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(d,CN1),Subst(Integrate(Times(Power(Times(f,x,Power(d,CN1)),m),Power(Plus(a,Times(b,ArcCsch(x))),p)),x),x,Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d,e,f,m),x),EqQ(Subtract(Times(d,e),Times(c,f)),C0),IGtQ(p,C0)))),
IIntegrate(6875,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSech(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Negate(Power(Power(d,Plus(m,C1)),CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),p),Sech(x),Tanh(x),Power(Plus(Times(d,e),Times(CN1,c,f),Times(f,Sech(x))),m)),x),x,ArcSech(Plus(c,Times(d,x))))),x),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),IntegerQ(m)))),
IIntegrate(6876,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCsch(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Negate(Power(Power(d,Plus(m,C1)),CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),p),Csch(x),Coth(x),Power(Plus(Times(d,e),Times(CN1,c,f),Times(f,Csch(x))),m)),x),x,ArcCsch(Plus(c,Times(d,x))))),x),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),IntegerQ(m)))),
IIntegrate(6877,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSech(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(d,CN1),Subst(Integrate(Times(Power(Plus(Times(Subtract(Times(d,e),Times(c,f)),Power(d,CN1)),Times(f,x,Power(d,CN1))),m),Power(Plus(a,Times(b,ArcSech(x))),p)),x),x,Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d,e,f,m),x),IGtQ(p,C0)))),
IIntegrate(6878,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCsch(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(d,CN1),Subst(Integrate(Times(Power(Plus(Times(Subtract(Times(d,e),Times(c,f)),Power(d,CN1)),Times(f,x,Power(d,CN1))),m),Power(Plus(a,Times(b,ArcCsch(x))),p)),x),x,Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d,e,f,m),x),IGtQ(p,C0)))),
IIntegrate(6879,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSech(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(b,ArcSech(Plus(c,Times(d,x))))),p)),x),And(FreeQ(List(a,b,c,d,e,f,m,p),x),Not(IGtQ(p,C0))))),
IIntegrate(6880,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCsch(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(b,ArcCsch(Plus(c,Times(d,x))))),p)),x),And(FreeQ(List(a,b,c,d,e,f,m,p),x),Not(IGtQ(p,C0)))))
  );
}
