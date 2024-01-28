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
class IntRules288 { 
  public static IAST RULES = List( 
IIntegrate(5761,Integrate(Times(Plus(a_DEFAULT,Times(ArcSec(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times(Power(Times(f,x),m),Power(Plus(d,Times(e,Sqr(x))),p)),x))),Subtract(Simp(Star(Plus(a,Times(b,ArcSec(Times(c,x)))),u),x),Simp(Star(Times(b,c,x,Power(Times(Sqr(c),Sqr(x)),CN1D2)),Integrate(SimplifyIntegrand(Times(u,Power(Times(x,Sqrt(Subtract(Times(Sqr(c),Sqr(x)),C1))),CN1)),x),x)),x))),And(FreeQ(List(a,b,c,d,e,f,m,p),x),Or(And(IGtQ(p,C0),Not(And(ILtQ(Times(C1D2,Subtract(m,C1)),C0),GtQ(Plus(m,Times(C2,p),C3),C0)))),And(IGtQ(Times(C1D2,Plus(m,C1)),C0),Not(And(ILtQ(p,C0),GtQ(Plus(m,Times(C2,p),C3),C0)))),And(ILtQ(Times(C1D2,Plus(m,Times(C2,p),C1)),C0),Not(ILtQ(Times(C1D2,Subtract(m,C1)),C0))))))),
IIntegrate(5762,Integrate(Times(Plus(a_DEFAULT,Times(ArcCsc(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times(Power(Times(f,x),m),Power(Plus(d,Times(e,Sqr(x))),p)),x))),Plus(Simp(Star(Plus(a,Times(b,ArcCsc(Times(c,x)))),u),x),Simp(Star(Times(b,c,x,Power(Times(Sqr(c),Sqr(x)),CN1D2)),Integrate(SimplifyIntegrand(Times(u,Power(Times(x,Sqrt(Subtract(Times(Sqr(c),Sqr(x)),C1))),CN1)),x),x)),x))),And(FreeQ(List(a,b,c,d,e,f,m,p),x),Or(And(IGtQ(p,C0),Not(And(ILtQ(Times(C1D2,Subtract(m,C1)),C0),GtQ(Plus(m,Times(C2,p),C3),C0)))),And(IGtQ(Times(C1D2,Plus(m,C1)),C0),Not(And(ILtQ(p,C0),GtQ(Plus(m,Times(C2,p),C3),C0)))),And(ILtQ(Times(C1D2,Plus(m,Times(C2,p),C1)),C0),Not(ILtQ(Times(C1D2,Subtract(m,C1)),C0))))))),
IIntegrate(5763,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSec(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(x_,m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Power(Plus(e,Times(d,Sqr(x))),p),Power(Plus(a,Times(b,ArcCos(Times(x,Power(c,CN1))))),n),Power(Power(x,Plus(m,Times(C2,Plus(p,C1)))),CN1)),x),x,Power(x,CN1))),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(n,C0),IntegerQ(m),IntegerQ(p)))),
IIntegrate(5764,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCsc(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(x_,m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Power(Plus(e,Times(d,Sqr(x))),p),Power(Plus(a,Times(b,ArcSin(Times(x,Power(c,CN1))))),n),Power(Power(x,Plus(m,Times(C2,Plus(p,C1)))),CN1)),x),x,Power(x,CN1))),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(n,C0),IntegerQ(m),IntegerQ(p)))),
IIntegrate(5765,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSec(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(x_,m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Star(Times(CN1,Sqrt(Sqr(x)),Power(x,CN1)),Subst(Integrate(Times(Power(Plus(e,Times(d,Sqr(x))),p),Power(Plus(a,Times(b,ArcCos(Times(x,Power(c,CN1))))),n),Power(Power(x,Plus(m,Times(C2,Plus(p,C1)))),CN1)),x),x,Power(x,CN1))),x),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(n,C0),EqQ(Plus(Times(Sqr(c),d),e),C0),IntegerQ(m),IntegerQ(Plus(p,C1D2)),GtQ(e,C0),LtQ(d,C0)))),
IIntegrate(5766,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCsc(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(x_,m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Star(Times(CN1,Sqrt(Sqr(x)),Power(x,CN1)),Subst(Integrate(Times(Power(Plus(e,Times(d,Sqr(x))),p),Power(Plus(a,Times(b,ArcSin(Times(x,Power(c,CN1))))),n),Power(Power(x,Plus(m,Times(C2,Plus(p,C1)))),CN1)),x),x,Power(x,CN1))),x),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(n,C0),EqQ(Plus(Times(Sqr(c),d),e),C0),IntegerQ(m),IntegerQ(Plus(p,C1D2)),GtQ(e,C0),LtQ(d,C0)))),
IIntegrate(5767,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSec(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(x_,m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Star(Times(CN1,Sqrt(Plus(d,Times(e,Sqr(x)))),Power(Times(x,Sqrt(Plus(e,Times(d,Power(x,CN2))))),CN1)),Subst(Integrate(Times(Power(Plus(e,Times(d,Sqr(x))),p),Power(Plus(a,Times(b,ArcCos(Times(x,Power(c,CN1))))),n),Power(Power(x,Plus(m,Times(C2,Plus(p,C1)))),CN1)),x),x,Power(x,CN1))),x),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(n,C0),EqQ(Plus(Times(Sqr(c),d),e),C0),IntegerQ(m),IntegerQ(Plus(p,C1D2)),Not(And(GtQ(e,C0),LtQ(d,C0)))))),
IIntegrate(5768,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCsc(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(x_,m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Star(Times(CN1,Sqrt(Plus(d,Times(e,Sqr(x)))),Power(Times(x,Sqrt(Plus(e,Times(d,Power(x,CN2))))),CN1)),Subst(Integrate(Times(Power(Plus(e,Times(d,Sqr(x))),p),Power(Plus(a,Times(b,ArcSin(Times(x,Power(c,CN1))))),n),Power(Power(x,Plus(m,Times(C2,Plus(p,C1)))),CN1)),x),x,Power(x,CN1))),x),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(n,C0),EqQ(Plus(Times(Sqr(c),d),e),C0),IntegerQ(m),IntegerQ(Plus(p,C1D2)),Not(And(GtQ(e,C0),LtQ(d,C0)))))),
IIntegrate(5769,Integrate(Times(Plus(a_DEFAULT,Times(ArcSec(Times(c_DEFAULT,x_)),b_DEFAULT)),u_),x_Symbol),
    Condition(With(list(Set(v,IntHide(u,x))),Condition(Subtract(Simp(Star(Plus(a,Times(b,ArcSec(Times(c,x)))),v),x),Simp(Star(Times(b,Power(c,CN1)),Integrate(SimplifyIntegrand(Times(v,Power(Times(Sqr(x),Sqrt(Subtract(C1,Power(Times(Sqr(c),Sqr(x)),CN1)))),CN1)),x),x)),x)),InverseFunctionFreeQ(v,x))),FreeQ(list(a,b,c),x))),
IIntegrate(5770,Integrate(Times(Plus(a_DEFAULT,Times(ArcCsc(Times(c_DEFAULT,x_)),b_DEFAULT)),u_),x_Symbol),
    Condition(With(list(Set(v,IntHide(u,x))),Condition(Plus(Simp(Star(Plus(a,Times(b,ArcCsc(Times(c,x)))),v),x),Simp(Star(Times(b,Power(c,CN1)),Integrate(SimplifyIntegrand(Times(v,Power(Times(Sqr(x),Sqrt(Subtract(C1,Power(Times(Sqr(c),Sqr(x)),CN1)))),CN1)),x),x)),x)),InverseFunctionFreeQ(v,x))),FreeQ(list(a,b,c),x))),
IIntegrate(5771,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSec(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Unintegrable(Times(u,Power(Plus(a,Times(b,ArcSec(Times(c,x)))),n)),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(5772,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCsc(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Unintegrable(Times(u,Power(Plus(a,Times(b,ArcCsc(Times(c,x)))),n)),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(5773,Integrate(ArcSec(Plus(c_,Times(d_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(c,Times(d,x)),ArcSec(Plus(c,Times(d,x))),Power(d,CN1)),x),Integrate(Power(Times(Plus(c,Times(d,x)),Sqrt(Subtract(C1,Power(Plus(c,Times(d,x)),CN2)))),CN1),x)),FreeQ(list(c,d),x))),
IIntegrate(5774,Integrate(ArcCsc(Plus(c_,Times(d_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Times(Plus(c,Times(d,x)),ArcCsc(Plus(c,Times(d,x))),Power(d,CN1)),x),Integrate(Power(Times(Plus(c,Times(d,x)),Sqrt(Subtract(C1,Power(Plus(c,Times(d,x)),CN2)))),CN1),x)),FreeQ(list(c,d),x))),
IIntegrate(5775,Integrate(Power(Plus(a_DEFAULT,Times(ArcSec(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Star(Power(d,CN1),Subst(Integrate(Power(Plus(a,Times(b,ArcSec(x))),p),x),x,Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d),x),IGtQ(p,C0)))),
IIntegrate(5776,Integrate(Power(Plus(a_DEFAULT,Times(ArcCsc(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Star(Power(d,CN1),Subst(Integrate(Power(Plus(a,Times(b,ArcCsc(x))),p),x),x,Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d),x),IGtQ(p,C0)))),
IIntegrate(5777,Integrate(Power(Plus(a_DEFAULT,Times(ArcSec(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,ArcSec(Plus(c,Times(d,x))))),p),x),And(FreeQ(List(a,b,c,d,p),x),Not(IGtQ(p,C0))))),
IIntegrate(5778,Integrate(Power(Plus(a_DEFAULT,Times(ArcCsc(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,ArcCsc(Plus(c,Times(d,x))))),p),x),And(FreeQ(List(a,b,c,d,p),x),Not(IGtQ(p,C0))))),
IIntegrate(5779,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSec(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(d,CN1),Subst(Integrate(Times(Power(Times(f,x,Power(d,CN1)),m),Power(Plus(a,Times(b,ArcSec(x))),p)),x),x,Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d,e,f,m),x),EqQ(Subtract(Times(d,e),Times(c,f)),C0),IGtQ(p,C0)))),
IIntegrate(5780,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCsc(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(d,CN1),Subst(Integrate(Times(Power(Times(f,x,Power(d,CN1)),m),Power(Plus(a,Times(b,ArcCsc(x))),p)),x),x,Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d,e,f,m),x),EqQ(Subtract(Times(d,e),Times(c,f)),C0),IGtQ(p,C0))))
  );
}
