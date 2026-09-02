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
class IntRules289 { 
  public static IAST RULES = List( 
IIntegrate(5781,Integrate(Times(Plus(a_DEFAULT,Times(ArcSec(Times(c_DEFAULT,x_)),b_DEFAULT)),u_),x_Symbol),
    Condition(With(list(Set(v,IntHide(u,x))),Condition(Subtract(Simp(Dist(Plus(a,Times(b,ArcSec(Times(c,x)))),v,x),x),Simp(Dist(Times(b,Power(c,CN1)),Integrate(SimplifyIntegrand(Times(v,Power(Times(Sqr(x),Sqrt(Subtract(C1,Power(Times(Sqr(c),Sqr(x)),CN1)))),CN1)),x),x),x),x)),InverseFunctionFreeQ(v,x))),FreeQ(list(a,b,c),x))),
IIntegrate(5782,Integrate(Times(Plus(a_DEFAULT,Times(ArcCsc(Times(c_DEFAULT,x_)),b_DEFAULT)),u_),x_Symbol),
    Condition(With(list(Set(v,IntHide(u,x))),Condition(Plus(Simp(Dist(Plus(a,Times(b,ArcCsc(Times(c,x)))),v,x),x),Simp(Dist(Times(b,Power(c,CN1)),Integrate(SimplifyIntegrand(Times(v,Power(Times(Sqr(x),Sqrt(Subtract(C1,Power(Times(Sqr(c),Sqr(x)),CN1)))),CN1)),x),x),x),x)),InverseFunctionFreeQ(v,x))),FreeQ(list(a,b,c),x))),
IIntegrate(5783,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSec(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Unintegrable(Times(u,Power(Plus(a,Times(b,ArcSec(Times(c,x)))),n)),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(5784,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCsc(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Unintegrable(Times(u,Power(Plus(a,Times(b,ArcCsc(Times(c,x)))),n)),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(5785,Integrate(ArcSec(Plus(c_,Times(d_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(c,Times(d,x)),ArcSec(Plus(c,Times(d,x))),Power(d,CN1)),x),Integrate(Power(Times(Plus(c,Times(d,x)),Sqrt(Subtract(C1,Power(Plus(c,Times(d,x)),CN2)))),CN1),x)),FreeQ(list(c,d),x))),
IIntegrate(5786,Integrate(ArcCsc(Plus(c_,Times(d_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Times(Plus(c,Times(d,x)),ArcCsc(Plus(c,Times(d,x))),Power(d,CN1)),x),Integrate(Power(Times(Plus(c,Times(d,x)),Sqrt(Subtract(C1,Power(Plus(c,Times(d,x)),CN2)))),CN1),x)),FreeQ(list(c,d),x))),
IIntegrate(5787,Integrate(Power(Plus(a_DEFAULT,Times(ArcSec(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Power(d,CN1),Subst(Integrate(Power(Plus(a,Times(b,ArcSec(x))),p),x),x,Plus(c,Times(d,x))),x),x),And(FreeQ(List(a,b,c,d),x),IGtQ(p,C0)))),
IIntegrate(5788,Integrate(Power(Plus(a_DEFAULT,Times(ArcCsc(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Power(d,CN1),Subst(Integrate(Power(Plus(a,Times(b,ArcCsc(x))),p),x),x,Plus(c,Times(d,x))),x),x),And(FreeQ(List(a,b,c,d),x),IGtQ(p,C0)))),
IIntegrate(5789,Integrate(Power(Plus(a_DEFAULT,Times(ArcSec(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,ArcSec(Plus(c,Times(d,x))))),p),x),And(FreeQ(List(a,b,c,d,p),x),Not(IGtQ(p,C0))))),
IIntegrate(5790,Integrate(Power(Plus(a_DEFAULT,Times(ArcCsc(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,ArcCsc(Plus(c,Times(d,x))))),p),x),And(FreeQ(List(a,b,c,d,p),x),Not(IGtQ(p,C0))))),
IIntegrate(5791,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSec(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(d,CN1),Subst(Integrate(Times(Power(Times(f,x,Power(d,CN1)),m),Power(Plus(a,Times(b,ArcSec(x))),p)),x),x,Plus(c,Times(d,x))),x),x),And(FreeQ(List(a,b,c,d,e,f,m),x),EqQ(Subtract(Times(d,e),Times(c,f)),C0),IGtQ(p,C0)))),
IIntegrate(5792,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCsc(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(d,CN1),Subst(Integrate(Times(Power(Times(f,x,Power(d,CN1)),m),Power(Plus(a,Times(b,ArcCsc(x))),p)),x),x,Plus(c,Times(d,x))),x),x),And(FreeQ(List(a,b,c,d,e,f,m),x),EqQ(Subtract(Times(d,e),Times(c,f)),C0),IGtQ(p,C0)))),
IIntegrate(5793,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSec(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(Power(d,Plus(m,C1)),CN1),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),p),Sec(x),Tan(x),Power(Plus(Times(d,e),Times(CN1,c,f),Times(f,Sec(x))),m)),x),x,ArcSec(Plus(c,Times(d,x)))),x),x),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),IntegerQ(m)))),
IIntegrate(5794,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCsc(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Negate(Power(Power(d,Plus(m,C1)),CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),p),Csc(x),Cot(x),Power(Plus(Times(d,e),Times(CN1,c,f),Times(f,Csc(x))),m)),x),x,ArcCsc(Plus(c,Times(d,x)))),x),x),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),IntegerQ(m)))),
IIntegrate(5795,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSec(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(d,CN1),Subst(Integrate(Times(Power(Plus(Times(Subtract(Times(d,e),Times(c,f)),Power(d,CN1)),Times(f,x,Power(d,CN1))),m),Power(Plus(a,Times(b,ArcSec(x))),p)),x),x,Plus(c,Times(d,x))),x),x),And(FreeQ(List(a,b,c,d,e,f,m),x),IGtQ(p,C0)))),
IIntegrate(5796,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCsc(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(d,CN1),Subst(Integrate(Times(Power(Plus(Times(Subtract(Times(d,e),Times(c,f)),Power(d,CN1)),Times(f,x,Power(d,CN1))),m),Power(Plus(a,Times(b,ArcCsc(x))),p)),x),x,Plus(c,Times(d,x))),x),x),And(FreeQ(List(a,b,c,d,e,f,m),x),IGtQ(p,C0)))),
IIntegrate(5797,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSec(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(b,ArcSec(Plus(c,Times(d,x))))),p)),x),And(FreeQ(List(a,b,c,d,e,f,m,p),x),Not(IGtQ(p,C0))))),
IIntegrate(5798,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCsc(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(b,ArcCsc(Plus(c,Times(d,x))))),p)),x),And(FreeQ(List(a,b,c,d,e,f,m,p),x),Not(IGtQ(p,C0))))),
IIntegrate(5799,Integrate(Times(Power(ArcSec(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),CN1))),m_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Integrate(Times(u,Power(ArcCos(Plus(Times(a,Power(c,CN1)),Times(b,Power(x,n),Power(c,CN1)))),m)),x),FreeQ(List(a,b,c,n,m),x))),
IIntegrate(5800,Integrate(Times(Power(ArcCsc(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),CN1))),m_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Integrate(Times(u,Power(ArcSin(Plus(Times(a,Power(c,CN1)),Times(b,Power(x,n),Power(c,CN1)))),m)),x),FreeQ(List(a,b,c,n,m),x)))
  );
}
