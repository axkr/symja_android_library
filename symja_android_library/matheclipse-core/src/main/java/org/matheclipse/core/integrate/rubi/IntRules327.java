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
class IntRules327 { 
  public static IAST RULES = List( 
IIntegrate(6541,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT)),p_),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(d,Times(e,Sqr(x))),Plus(q,C1)),Power(Plus(a,Times(b,ArcCoth(Times(c,x)))),Plus(p,C1)),Power(Times(b,c,d,Plus(p,C1)),CN1)),x),Simp(Dist(Times(C2,c,Plus(q,C1),Power(Times(b,Plus(p,C1)),CN1)),Integrate(Times(x,Power(Plus(d,Times(e,Sqr(x))),q),Power(Plus(a,Times(b,ArcCoth(Times(c,x)))),Plus(p,C1))),x),x),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(Times(Sqr(c),d),e),C0),LtQ(q,CN1),LtQ(p,CN1)))),
IIntegrate(6542,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Simp(Dist(Times(Power(d,q),Power(c,CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),p),Power(Power(Cosh(x),Times(C2,Plus(q,C1))),CN1)),x),x,ArcTanh(Times(c,x))),x),x),And(FreeQ(List(a,b,c,d,e,p),x),EqQ(Plus(Times(Sqr(c),d),e),C0),ILtQ(Times(C2,Plus(q,C1)),C0),Or(IntegerQ(q),GtQ(d,C0))))),
IIntegrate(6543,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Simp(Dist(Times(Power(d,Plus(q,C1D2)),Sqrt(Subtract(C1,Times(Sqr(c),Sqr(x)))),Power(Plus(d,Times(e,Sqr(x))),CN1D2)),Integrate(Times(Power(Subtract(C1,Times(Sqr(c),Sqr(x))),q),Power(Plus(a,Times(b,ArcTanh(Times(c,x)))),p)),x),x),x),And(FreeQ(List(a,b,c,d,e,p),x),EqQ(Plus(Times(Sqr(c),d),e),C0),ILtQ(Times(C2,Plus(q,C1)),C0),Not(Or(IntegerQ(q),GtQ(d,C0)))))),
IIntegrate(6544,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Simp(Dist(Times(CN1,Power(Negate(d),q),Power(c,CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),p),Power(Power(Sinh(x),Times(C2,Plus(q,C1))),CN1)),x),x,ArcCoth(Times(c,x))),x),x),And(FreeQ(List(a,b,c,d,e,p),x),EqQ(Plus(Times(Sqr(c),d),e),C0),ILtQ(Times(C2,Plus(q,C1)),C0),IntegerQ(q)))),
IIntegrate(6545,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Simp(Dist(Times(CN1,Power(Negate(d),Plus(q,C1D2)),x,Sqrt(Times(Plus(Times(Sqr(c),Sqr(x)),CN1),Power(Times(Sqr(c),Sqr(x)),CN1))),Power(Plus(d,Times(e,Sqr(x))),CN1D2)),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),p),Power(Power(Sinh(x),Times(C2,Plus(q,C1))),CN1)),x),x,ArcCoth(Times(c,x))),x),x),And(FreeQ(List(a,b,c,d,e,p),x),EqQ(Plus(Times(Sqr(c),d),e),C0),ILtQ(Times(C2,Plus(q,C1)),C0),Not(IntegerQ(q))))),
IIntegrate(6546,Integrate(Times(ArcTanh(Times(c_DEFAULT,x_)),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Dist(C1D2,Integrate(Times(Log(Plus(C1,Times(c,x))),Power(Plus(d,Times(e,Sqr(x))),CN1)),x),x),x),Simp(Dist(C1D2,Integrate(Times(Log(Subtract(C1,Times(c,x))),Power(Plus(d,Times(e,Sqr(x))),CN1)),x),x),x)),FreeQ(list(c,d,e),x))),
IIntegrate(6547,Integrate(Times(ArcCoth(Times(c_DEFAULT,x_)),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Dist(C1D2,Integrate(Times(Log(Plus(C1,Power(Times(c,x),CN1))),Power(Plus(d,Times(e,Sqr(x))),CN1)),x),x),x),Simp(Dist(C1D2,Integrate(Times(Log(Subtract(C1,Power(Times(c,x),CN1))),Power(Plus(d,Times(e,Sqr(x))),CN1)),x),x),x)),FreeQ(list(c,d,e),x))),
IIntegrate(6548,Integrate(Times(Plus(Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT),a_),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Plus(Simp(Dist(a,Integrate(Power(Plus(d,Times(e,Sqr(x))),CN1),x),x),x),Simp(Dist(b,Integrate(Times(ArcTanh(Times(c,x)),Power(Plus(d,Times(e,Sqr(x))),CN1)),x),x),x)),FreeQ(List(a,b,c,d,e),x))),
IIntegrate(6549,Integrate(Times(Plus(Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT),a_),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Plus(Simp(Dist(a,Integrate(Power(Plus(d,Times(e,Sqr(x))),CN1),x),x),x),Simp(Dist(b,Integrate(Times(ArcCoth(Times(c,x)),Power(Plus(d,Times(e,Sqr(x))),CN1)),x),x),x)),FreeQ(List(a,b,c,d,e),x))),
IIntegrate(6550,Integrate(Times(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Power(Plus(d,Times(e,Sqr(x))),q),x))),Subtract(Simp(Dist(Plus(a,Times(b,ArcTanh(Times(c,x)))),u,x),x),Simp(Dist(Times(b,c),Integrate(SimplifyIntegrand(Times(u,Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1)),x),x),x),x))),And(FreeQ(List(a,b,c,d,e),x),Or(IntegerQ(q),ILtQ(Plus(q,C1D2),C0))))),
IIntegrate(6551,Integrate(Times(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Power(Plus(d,Times(e,Sqr(x))),q),x))),Subtract(Simp(Dist(Plus(a,Times(b,ArcCoth(Times(c,x)))),u,x),x),Simp(Dist(Times(b,c),Integrate(SimplifyIntegrand(Times(u,Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1)),x),x),x),x))),And(FreeQ(List(a,b,c,d,e),x),Or(IntegerQ(q),ILtQ(Plus(q,C1D2),C0))))),
IIntegrate(6552,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Plus(a,Times(b,ArcTanh(Times(c,x)))),p),Power(Plus(d,Times(e,Sqr(x))),q),x),x),And(FreeQ(List(a,b,c,d,e),x),IntegerQ(q),IGtQ(p,C0)))),
IIntegrate(6553,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Plus(a,Times(b,ArcCoth(Times(c,x)))),p),Power(Plus(d,Times(e,Sqr(x))),q),x),x),And(FreeQ(List(a,b,c,d,e),x),IntegerQ(q),IGtQ(p,C0)))),
IIntegrate(6554,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Times(f_DEFAULT,x_),m_),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Dist(Times(Sqr(f),Power(e,CN1)),Integrate(Times(Power(Times(f,x),Plus(m,CN2)),Power(Plus(a,Times(b,ArcTanh(Times(c,x)))),p)),x),x),x),Simp(Dist(Times(d,Sqr(f),Power(e,CN1)),Integrate(Times(Power(Times(f,x),Plus(m,CN2)),Power(Plus(a,Times(b,ArcTanh(Times(c,x)))),p),Power(Plus(d,Times(e,Sqr(x))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),GtQ(p,C0),GtQ(m,C1)))),
IIntegrate(6555,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Times(f_DEFAULT,x_),m_),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Dist(Times(Sqr(f),Power(e,CN1)),Integrate(Times(Power(Times(f,x),Plus(m,CN2)),Power(Plus(a,Times(b,ArcCoth(Times(c,x)))),p)),x),x),x),Simp(Dist(Times(d,Sqr(f),Power(e,CN1)),Integrate(Times(Power(Times(f,x),Plus(m,CN2)),Power(Plus(a,Times(b,ArcCoth(Times(c,x)))),p),Power(Plus(d,Times(e,Sqr(x))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),GtQ(p,C0),GtQ(m,C1)))),
IIntegrate(6556,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Times(f_DEFAULT,x_),m_),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Dist(Power(d,CN1),Integrate(Times(Power(Times(f,x),m),Power(Plus(a,Times(b,ArcTanh(Times(c,x)))),p)),x),x),x),Simp(Dist(Times(e,Power(Times(d,Sqr(f)),CN1)),Integrate(Times(Power(Times(f,x),Plus(m,C2)),Power(Plus(a,Times(b,ArcTanh(Times(c,x)))),p),Power(Plus(d,Times(e,Sqr(x))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),GtQ(p,C0),LtQ(m,CN1)))),
IIntegrate(6557,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Times(f_DEFAULT,x_),m_),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Dist(Power(d,CN1),Integrate(Times(Power(Times(f,x),m),Power(Plus(a,Times(b,ArcCoth(Times(c,x)))),p)),x),x),x),Simp(Dist(Times(e,Power(Times(d,Sqr(f)),CN1)),Integrate(Times(Power(Times(f,x),Plus(m,C2)),Power(Plus(a,Times(b,ArcCoth(Times(c,x)))),p),Power(Plus(d,Times(e,Sqr(x))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),GtQ(p,C0),LtQ(m,CN1)))),
IIntegrate(6558,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),x_,Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(a,Times(b,ArcTanh(Times(c,x)))),Plus(p,C1)),Power(Times(b,e,Plus(p,C1)),CN1)),x),Simp(Dist(Power(Times(c,d),CN1),Integrate(Times(Power(Plus(a,Times(b,ArcTanh(Times(c,x)))),p),Power(Subtract(C1,Times(c,x)),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(Times(Sqr(c),d),e),C0),IGtQ(p,C0)))),
IIntegrate(6559,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),x_,Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(a,Times(b,ArcCoth(Times(c,x)))),Plus(p,C1)),Power(Times(b,e,Plus(p,C1)),CN1)),x),Simp(Dist(Power(Times(c,d),CN1),Integrate(Times(Power(Plus(a,Times(b,ArcCoth(Times(c,x)))),p),Power(Subtract(C1,Times(c,x)),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(Times(Sqr(c),d),e),C0),IGtQ(p,C0)))),
IIntegrate(6560,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT)),p_),x_,Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(x,Power(Plus(a,Times(b,ArcTanh(Times(c,x)))),Plus(p,C1)),Power(Times(b,c,d,Plus(p,C1)),CN1)),x),Simp(Dist(Power(Times(b,c,d,Plus(p,C1)),CN1),Integrate(Power(Plus(a,Times(b,ArcTanh(Times(c,x)))),Plus(p,C1)),x),x),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(Times(Sqr(c),d),e),C0),Not(IGtQ(p,C0)),NeQ(p,CN1))))
  );
}
