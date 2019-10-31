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
public class IntRules209 { 
  public static IAST RULES = List( 
IIntegrate(5226,Int(Times(Plus(a_DEFAULT,Times(ArcSec(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Plus(a,Times(b,ArcSec(Times(c,x)))),Power(Times(e,Plus(m,C1)),CN1)),x),Dist(Times(b,Power(Times(c,e,Plus(m,C1)),CN1)),Int(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Power(Times(Sqr(x),Sqrt(Subtract(C1,Power(Times(Sqr(c),Sqr(x)),CN1)))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,m),x),NeQ(m,CN1)))),
IIntegrate(5227,Int(Times(Plus(a_DEFAULT,Times(ArcCsc(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Plus(a,Times(b,ArcCsc(Times(c,x)))),Power(Times(e,Plus(m,C1)),CN1)),x),Dist(Times(b,Power(Times(c,e,Plus(m,C1)),CN1)),Int(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Power(Times(Sqr(x),Sqrt(Subtract(C1,Power(Times(Sqr(c),Sqr(x)),CN1)))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,m),x),NeQ(m,CN1)))),
IIntegrate(5228,Int(Times(Plus(a_DEFAULT,Times(ArcSec(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(With(List(Set(u,IntHide(Power(Plus(d,Times(e,Sqr(x))),p),x))),Subtract(Dist(Plus(a,Times(b,ArcSec(Times(c,x)))),u,x),Dist(Times(b,c,x,Power(Times(Sqr(c),Sqr(x)),CN1D2)),Int(SimplifyIntegrand(Times(u,Power(Times(x,Sqrt(Subtract(Times(Sqr(c),Sqr(x)),C1))),CN1)),x),x),x))),And(FreeQ(List(a,b,c,d,e),x),Or(IGtQ(p,C0),ILtQ(Plus(p,C1D2),C0))))),
IIntegrate(5229,Int(Times(Plus(a_DEFAULT,Times(ArcCsc(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(With(List(Set(u,IntHide(Power(Plus(d,Times(e,Sqr(x))),p),x))),Plus(Dist(Plus(a,Times(b,ArcCsc(Times(c,x)))),u,x),Dist(Times(b,c,x,Power(Times(Sqr(c),Sqr(x)),CN1D2)),Int(SimplifyIntegrand(Times(u,Power(Times(x,Sqrt(Subtract(Times(Sqr(c),Sqr(x)),C1))),CN1)),x),x),x))),And(FreeQ(List(a,b,c,d,e),x),Or(IGtQ(p,C0),ILtQ(Plus(p,C1D2),C0))))),
IIntegrate(5230,Int(Times(Power(Plus(a_DEFAULT,Times(ArcSec(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Negate(Subst(Int(Times(Power(Plus(e,Times(d,Sqr(x))),p),Power(Plus(a,Times(b,ArcCos(Times(x,Power(c,CN1))))),n),Power(Power(x,Times(C2,Plus(p,C1))),CN1)),x),x,Power(x,CN1))),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(n,C0),IntegerQ(p)))),
IIntegrate(5231,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCsc(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Negate(Subst(Int(Times(Power(Plus(e,Times(d,Sqr(x))),p),Power(Plus(a,Times(b,ArcSin(Times(x,Power(c,CN1))))),n),Power(Power(x,Times(C2,Plus(p,C1))),CN1)),x),x,Power(x,CN1))),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(n,C0),IntegerQ(p)))),
IIntegrate(5232,Int(Times(Power(Plus(a_DEFAULT,Times(ArcSec(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Negate(Dist(Times(Sqrt(Sqr(x)),Power(x,CN1)),Subst(Int(Times(Power(Plus(e,Times(d,Sqr(x))),p),Power(Plus(a,Times(b,ArcCos(Times(x,Power(c,CN1))))),n),Power(Power(x,Times(C2,Plus(p,C1))),CN1)),x),x,Power(x,CN1)),x)),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(n,C0),EqQ(Plus(Times(Sqr(c),d),e),C0),IntegerQ(Plus(p,C1D2)),GtQ(e,C0),LtQ(d,C0)))),
IIntegrate(5233,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCsc(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Negate(Dist(Times(Sqrt(Sqr(x)),Power(x,CN1)),Subst(Int(Times(Power(Plus(e,Times(d,Sqr(x))),p),Power(Plus(a,Times(b,ArcSin(Times(x,Power(c,CN1))))),n),Power(Power(x,Times(C2,Plus(p,C1))),CN1)),x),x,Power(x,CN1)),x)),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(n,C0),EqQ(Plus(Times(Sqr(c),d),e),C0),IntegerQ(Plus(p,C1D2)),GtQ(e,C0),LtQ(d,C0)))),
IIntegrate(5234,Int(Times(Power(Plus(a_DEFAULT,Times(ArcSec(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Negate(Dist(Times(Sqrt(Plus(d,Times(e,Sqr(x)))),Power(Times(x,Sqrt(Plus(e,Times(d,Power(x,CN2))))),CN1)),Subst(Int(Times(Power(Plus(e,Times(d,Sqr(x))),p),Power(Plus(a,Times(b,ArcCos(Times(x,Power(c,CN1))))),n),Power(Power(x,Times(C2,Plus(p,C1))),CN1)),x),x,Power(x,CN1)),x)),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(n,C0),EqQ(Plus(Times(Sqr(c),d),e),C0),IntegerQ(Plus(p,C1D2)),Not(And(GtQ(e,C0),LtQ(d,C0)))))),
IIntegrate(5235,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCsc(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Negate(Dist(Times(Sqrt(Plus(d,Times(e,Sqr(x)))),Power(Times(x,Sqrt(Plus(e,Times(d,Power(x,CN2))))),CN1)),Subst(Int(Times(Power(Plus(e,Times(d,Sqr(x))),p),Power(Plus(a,Times(b,ArcSin(Times(x,Power(c,CN1))))),n),Power(Power(x,Times(C2,Plus(p,C1))),CN1)),x),x,Power(x,CN1)),x)),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(n,C0),EqQ(Plus(Times(Sqr(c),d),e),C0),IntegerQ(Plus(p,C1D2)),Not(And(GtQ(e,C0),LtQ(d,C0)))))),
IIntegrate(5236,Int(Times(Plus(a_DEFAULT,Times(ArcSec(Times(c_DEFAULT,x_)),b_DEFAULT)),x_,Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(d,Times(e,Sqr(x))),Plus(p,C1)),Plus(a,Times(b,ArcSec(Times(c,x)))),Power(Times(C2,e,Plus(p,C1)),CN1)),x),Dist(Times(b,c,x,Power(Times(C2,e,Plus(p,C1),Sqrt(Times(Sqr(c),Sqr(x)))),CN1)),Int(Times(Power(Plus(d,Times(e,Sqr(x))),Plus(p,C1)),Power(Times(x,Sqrt(Subtract(Times(Sqr(c),Sqr(x)),C1))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,p),x),NeQ(p,CN1)))),
IIntegrate(5237,Int(Times(Plus(a_DEFAULT,Times(ArcCsc(Times(c_DEFAULT,x_)),b_DEFAULT)),x_,Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(d,Times(e,Sqr(x))),Plus(p,C1)),Plus(a,Times(b,ArcCsc(Times(c,x)))),Power(Times(C2,e,Plus(p,C1)),CN1)),x),Dist(Times(b,c,x,Power(Times(C2,e,Plus(p,C1),Sqrt(Times(Sqr(c),Sqr(x)))),CN1)),Int(Times(Power(Plus(d,Times(e,Sqr(x))),Plus(p,C1)),Power(Times(x,Sqrt(Subtract(Times(Sqr(c),Sqr(x)),C1))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,p),x),NeQ(p,CN1)))),
IIntegrate(5238,Int(Times(Plus(a_DEFAULT,Times(ArcSec(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(With(List(Set(u,IntHide(Times(Power(Times(f,x),m),Power(Plus(d,Times(e,Sqr(x))),p)),x))),Subtract(Dist(Plus(a,Times(b,ArcSec(Times(c,x)))),u,x),Dist(Times(b,c,x,Power(Times(Sqr(c),Sqr(x)),CN1D2)),Int(SimplifyIntegrand(Times(u,Power(Times(x,Sqrt(Subtract(Times(Sqr(c),Sqr(x)),C1))),CN1)),x),x),x))),And(FreeQ(List(a,b,c,d,e,f,m,p),x),Or(And(IGtQ(p,C0),Not(And(ILtQ(Times(C1D2,Subtract(m,C1)),C0),GtQ(Plus(m,Times(C2,p),C3),C0)))),And(IGtQ(Times(C1D2,Plus(m,C1)),C0),Not(And(ILtQ(p,C0),GtQ(Plus(m,Times(C2,p),C3),C0)))),And(ILtQ(Times(C1D2,Plus(m,Times(C2,p),C1)),C0),Not(ILtQ(Times(C1D2,Subtract(m,C1)),C0))))))),
IIntegrate(5239,Int(Times(Plus(a_DEFAULT,Times(ArcCsc(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(With(List(Set(u,IntHide(Times(Power(Times(f,x),m),Power(Plus(d,Times(e,Sqr(x))),p)),x))),Plus(Dist(Plus(a,Times(b,ArcCsc(Times(c,x)))),u,x),Dist(Times(b,c,x,Power(Times(Sqr(c),Sqr(x)),CN1D2)),Int(SimplifyIntegrand(Times(u,Power(Times(x,Sqrt(Subtract(Times(Sqr(c),Sqr(x)),C1))),CN1)),x),x),x))),And(FreeQ(List(a,b,c,d,e,f,m,p),x),Or(And(IGtQ(p,C0),Not(And(ILtQ(Times(C1D2,Subtract(m,C1)),C0),GtQ(Plus(m,Times(C2,p),C3),C0)))),And(IGtQ(Times(C1D2,Plus(m,C1)),C0),Not(And(ILtQ(p,C0),GtQ(Plus(m,Times(C2,p),C3),C0)))),And(ILtQ(Times(C1D2,Plus(m,Times(C2,p),C1)),C0),Not(ILtQ(Times(C1D2,Subtract(m,C1)),C0))))))),
IIntegrate(5240,Int(Times(Power(Plus(a_DEFAULT,Times(ArcSec(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(x_,m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Negate(Subst(Int(Times(Power(Plus(e,Times(d,Sqr(x))),p),Power(Plus(a,Times(b,ArcCos(Times(x,Power(c,CN1))))),n),Power(Power(x,Plus(m,Times(C2,Plus(p,C1)))),CN1)),x),x,Power(x,CN1))),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(n,C0),IntegerQ(m),IntegerQ(p)))),
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
    Condition(Subtract(Simp(Times(Plus(c,Times(d,x)),ArcSec(Plus(c,Times(d,x))),Power(d,CN1)),x),Int(Power(Times(Plus(c,Times(d,x)),Sqrt(Subtract(C1,Power(Plus(c,Times(d,x)),CN2)))),CN1),x)),FreeQ(List(c,d),x)))
  );
}
