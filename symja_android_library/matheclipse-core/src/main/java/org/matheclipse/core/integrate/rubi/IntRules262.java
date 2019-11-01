package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *  
 */
public class IntRules262 { 
  public static IAST RULES = List( 
IIntegrate(5241,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCsc(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(x_,m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Negate(Subst(Int(Times(Power(Plus(e,Times(d,Sqr(x))),p),Power(Plus(a,Times(b,ArcSin(Times(x,Power(c,CN1))))),n),Power(Power(x,Plus(m,Times(C2,Plus(p,C1)))),CN1)),x),x,Power(x,CN1))),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(n,C0),IntegerQ(m),IntegerQ(p)))),
IIntegrate(5242,Int(Times(Power(Plus(a_DEFAULT,Times(ArcSec(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(x_,m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Negate(Dist(Times(Sqrt(Sqr(x)),Power(x,CN1)),Subst(Int(Times(Power(Plus(e,Times(d,Sqr(x))),p),Power(Plus(a,Times(b,ArcCos(Times(x,Power(c,CN1))))),n),Power(Power(x,Plus(m,Times(C2,Plus(p,C1)))),CN1)),x),x,Power(x,CN1)),x)),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(n,C0),EqQ(Plus(Times(Sqr(c),d),e),C0),IntegerQ(m),IntegerQ(Plus(p,C1D2)),GtQ(e,C0),LtQ(d,C0)))),
IIntegrate(5243,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCsc(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(x_,m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Negate(Dist(Times(Sqrt(Sqr(x)),Power(x,CN1)),Subst(Int(Times(Power(Plus(e,Times(d,Sqr(x))),p),Power(Plus(a,Times(b,ArcSin(Times(x,Power(c,CN1))))),n),Power(Power(x,Plus(m,Times(C2,Plus(p,C1)))),CN1)),x),x,Power(x,CN1)),x)),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(n,C0),EqQ(Plus(Times(Sqr(c),d),e),C0),IntegerQ(m),IntegerQ(Plus(p,C1D2)),GtQ(e,C0),LtQ(d,C0)))),
IIntegrate(5244,Int(Times(Power(Plus(a_DEFAULT,Times(ArcSec(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(x_,m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Negate(Dist(Times(Sqrt(Plus(d,Times(e,Sqr(x)))),Power(Times(x,Sqrt(Plus(e,Times(d,Power(x,CN2))))),CN1)),Subst(Int(Times(Power(Plus(e,Times(d,Sqr(x))),p),Power(Plus(a,Times(b,ArcCos(Times(x,Power(c,CN1))))),n),Power(Power(x,Plus(m,Times(C2,Plus(p,C1)))),CN1)),x),x,Power(x,CN1)),x)),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(n,C0),EqQ(Plus(Times(Sqr(c),d),e),C0),IntegerQ(m),IntegerQ(Plus(p,C1D2)),Not(And(GtQ(e,C0),LtQ(d,C0)))))),
IIntegrate(5245,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCsc(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(x_,m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Negate(Dist(Times(Sqrt(Plus(d,Times(e,Sqr(x)))),Power(Times(x,Sqrt(Plus(e,Times(d,Power(x,CN2))))),CN1)),Subst(Int(Times(Power(Plus(e,Times(d,Sqr(x))),p),Power(Plus(a,Times(b,ArcSin(Times(x,Power(c,CN1))))),n),Power(Power(x,Plus(m,Times(C2,Plus(p,C1)))),CN1)),x),x,Power(x,CN1)),x)),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(n,C0),EqQ(Plus(Times(Sqr(c),d),e),C0),IntegerQ(m),IntegerQ(Plus(p,C1D2)),Not(And(GtQ(e,C0),LtQ(d,C0)))))),
IIntegrate(5246,Int(Times(Plus(a_DEFAULT,Times(ArcSec(Times(c_DEFAULT,x_)),b_DEFAULT)),u_),x_Symbol),
    Condition(With(List(Set(v,IntHide(u,x))),Condition(Subtract(Dist(Plus(a,Times(b,ArcSec(Times(c,x)))),v,x),Dist(Times(b,Power(c,CN1)),Int(SimplifyIntegrand(Times(v,Power(Times(Sqr(x),Sqrt(Subtract(C1,Power(Times(Sqr(c),Sqr(x)),CN1)))),CN1)),x),x),x)),InverseFunctionFreeQ(v,x))),FreeQ(List(a,b,c),x))),
IIntegrate(5247,Int(Times(Plus(a_DEFAULT,Times(ArcCsc(Times(c_DEFAULT,x_)),b_DEFAULT)),u_),x_Symbol),
    Condition(With(List(Set(v,IntHide(u,x))),Condition(Plus(Dist(Plus(a,Times(b,ArcCsc(Times(c,x)))),v,x),Dist(Times(b,Power(c,CN1)),Int(SimplifyIntegrand(Times(v,Power(Times(Sqr(x),Sqrt(Subtract(C1,Power(Times(Sqr(c),Sqr(x)),CN1)))),CN1)),x),x),x)),InverseFunctionFreeQ(v,x))),FreeQ(List(a,b,c),x))),
IIntegrate(5248,Int(Times(Power(Plus(a_DEFAULT,Times(ArcSec(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Unintegrable(Times(u,Power(Plus(a,Times(b,ArcSec(Times(c,x)))),n)),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(5249,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCsc(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Unintegrable(Times(u,Power(Plus(a,Times(b,ArcCsc(Times(c,x)))),n)),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(5250,Int(ArcSec(Plus(c_,Times(d_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(c,Times(d,x)),ArcSec(Plus(c,Times(d,x))),Power(d,CN1)),x),Int(Power(Times(Plus(c,Times(d,x)),Sqrt(Subtract(C1,Power(Plus(c,Times(d,x)),CN2)))),CN1),x)),FreeQ(List(c,d),x))),
IIntegrate(5251,Int(ArcCsc(Plus(c_,Times(d_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Times(Plus(c,Times(d,x)),ArcCsc(Plus(c,Times(d,x))),Power(d,CN1)),x),Int(Power(Times(Plus(c,Times(d,x)),Sqrt(Subtract(C1,Power(Plus(c,Times(d,x)),CN2)))),CN1),x)),FreeQ(List(c,d),x))),
IIntegrate(5252,Int(Power(Plus(a_DEFAULT,Times(ArcSec(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Dist(Power(d,CN1),Subst(Int(Power(Plus(a,Times(b,ArcSec(x))),p),x),x,Plus(c,Times(d,x))),x),And(FreeQ(List(a,b,c,d),x),IGtQ(p,C0)))),
IIntegrate(5253,Int(Power(Plus(a_DEFAULT,Times(ArcCsc(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Dist(Power(d,CN1),Subst(Int(Power(Plus(a,Times(b,ArcCsc(x))),p),x),x,Plus(c,Times(d,x))),x),And(FreeQ(List(a,b,c,d),x),IGtQ(p,C0)))),
IIntegrate(5254,Int(Power(Plus(a_DEFAULT,Times(ArcSec(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,ArcSec(Plus(c,Times(d,x))))),p),x),And(FreeQ(List(a,b,c,d,p),x),Not(IGtQ(p,C0))))),
IIntegrate(5255,Int(Power(Plus(a_DEFAULT,Times(ArcCsc(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,ArcCsc(Plus(c,Times(d,x))))),p),x),And(FreeQ(List(a,b,c,d,p),x),Not(IGtQ(p,C0))))),
IIntegrate(5256,Int(Times(Power(Plus(a_DEFAULT,Times(ArcSec(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(d,CN1),Subst(Int(Times(Power(Times(f,x,Power(d,CN1)),m),Power(Plus(a,Times(b,ArcSec(x))),p)),x),x,Plus(c,Times(d,x))),x),And(FreeQ(List(a,b,c,d,e,f,m),x),EqQ(Subtract(Times(d,e),Times(c,f)),C0),IGtQ(p,C0)))),
IIntegrate(5257,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCsc(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(d,CN1),Subst(Int(Times(Power(Times(f,x,Power(d,CN1)),m),Power(Plus(a,Times(b,ArcCsc(x))),p)),x),x,Plus(c,Times(d,x))),x),And(FreeQ(List(a,b,c,d,e,f,m),x),EqQ(Subtract(Times(d,e),Times(c,f)),C0),IGtQ(p,C0)))),
IIntegrate(5258,Int(Times(Power(Plus(a_DEFAULT,Times(ArcSec(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Power(d,Plus(m,C1)),CN1),Subst(Int(Times(Power(Plus(a,Times(b,x)),p),Sec(x),Tan(x),Power(Plus(Times(d,e),Times(CN1,c,f),Times(f,Sec(x))),m)),x),x,ArcSec(Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),IntegerQ(m)))),
IIntegrate(5259,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCsc(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Negate(Dist(Power(Power(d,Plus(m,C1)),CN1),Subst(Int(Times(Power(Plus(a,Times(b,x)),p),Csc(x),Cot(x),Power(Plus(Times(d,e),Times(CN1,c,f),Times(f,Csc(x))),m)),x),x,ArcCsc(Plus(c,Times(d,x)))),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),IntegerQ(m)))),
IIntegrate(5260,Int(Times(Power(Plus(a_DEFAULT,Times(ArcSec(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(d,CN1),Subst(Int(Times(Power(Plus(Times(Subtract(Times(d,e),Times(c,f)),Power(d,CN1)),Times(f,x,Power(d,CN1))),m),Power(Plus(a,Times(b,ArcSec(x))),p)),x),x,Plus(c,Times(d,x))),x),And(FreeQ(List(a,b,c,d,e,f,m),x),IGtQ(p,C0))))
  );
}
