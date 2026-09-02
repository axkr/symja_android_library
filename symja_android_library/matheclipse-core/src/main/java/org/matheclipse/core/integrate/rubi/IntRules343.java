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
class IntRules343 { 
  public static IAST RULES = List( 
IIntegrate(6861,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSech(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Dist(Times(CN1,Sqrt(Sqr(x)),Power(x,CN1)),Subst(Integrate(Times(Power(Plus(e,Times(d,Sqr(x))),p),Power(Plus(a,Times(b,ArcCosh(Times(x,Power(c,CN1))))),n),Power(Power(x,Times(C2,Plus(p,C1))),CN1)),x),x,Power(x,CN1)),x),x),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(n,C0),EqQ(Plus(Times(Sqr(c),d),e),C0),IntegerQ(Plus(p,C1D2)),GtQ(e,C0),LtQ(d,C0)))),
IIntegrate(6862,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCsch(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Dist(Times(CN1,Sqrt(Sqr(x)),Power(x,CN1)),Subst(Integrate(Times(Power(Plus(e,Times(d,Sqr(x))),p),Power(Plus(a,Times(b,ArcSinh(Times(x,Power(c,CN1))))),n),Power(Power(x,Times(C2,Plus(p,C1))),CN1)),x),x,Power(x,CN1)),x),x),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(n,C0),EqQ(Subtract(e,Times(Sqr(c),d)),C0),IntegerQ(Plus(p,C1D2)),GtQ(e,C0),LtQ(d,C0)))),
IIntegrate(6863,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSech(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Dist(Times(CN1,Sqrt(Plus(d,Times(e,Sqr(x)))),Power(Times(x,Sqrt(Plus(e,Times(d,Power(x,CN2))))),CN1)),Subst(Integrate(Times(Power(Plus(e,Times(d,Sqr(x))),p),Power(Plus(a,Times(b,ArcCosh(Times(x,Power(c,CN1))))),n),Power(Power(x,Times(C2,Plus(p,C1))),CN1)),x),x,Power(x,CN1)),x),x),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(n,C0),EqQ(Plus(Times(Sqr(c),d),e),C0),IntegerQ(Plus(p,C1D2)),Not(And(GtQ(e,C0),LtQ(d,C0)))))),
IIntegrate(6864,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCsch(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Dist(Times(CN1,Sqrt(Plus(d,Times(e,Sqr(x)))),Power(Times(x,Sqrt(Plus(e,Times(d,Power(x,CN2))))),CN1)),Subst(Integrate(Times(Power(Plus(e,Times(d,Sqr(x))),p),Power(Plus(a,Times(b,ArcSinh(Times(x,Power(c,CN1))))),n),Power(Power(x,Times(C2,Plus(p,C1))),CN1)),x),x,Power(x,CN1)),x),x),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(n,C0),EqQ(Subtract(e,Times(Sqr(c),d)),C0),IntegerQ(Plus(p,C1D2)),Not(And(GtQ(e,C0),LtQ(d,C0)))))),
IIntegrate(6865,Integrate(Times(Plus(a_DEFAULT,Times(ArcSech(Times(c_DEFAULT,x_)),b_DEFAULT)),x_,Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(d,Times(e,Sqr(x))),Plus(p,C1)),Plus(a,Times(b,ArcSech(Times(c,x)))),Power(Times(C2,e,Plus(p,C1)),CN1)),x),Simp(Dist(Times(b,Sqrt(Plus(C1,Times(c,x))),Power(Times(C2,e,Plus(p,C1)),CN1),Sqrt(Power(Plus(C1,Times(c,x)),CN1))),Integrate(Times(Power(Plus(d,Times(e,Sqr(x))),Plus(p,C1)),Power(Times(x,Sqrt(Subtract(C1,Times(c,x))),Sqrt(Plus(C1,Times(c,x)))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,p),x),NeQ(p,CN1)))),
IIntegrate(6866,Integrate(Times(Plus(a_DEFAULT,Times(ArcCsch(Times(c_DEFAULT,x_)),b_DEFAULT)),x_,Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(d,Times(e,Sqr(x))),Plus(p,C1)),Plus(a,Times(b,ArcCsch(Times(c,x)))),Power(Times(C2,e,Plus(p,C1)),CN1)),x),Simp(Dist(Times(b,c,x,Power(Times(C2,e,Plus(p,C1),Sqrt(Times(CN1,Sqr(c),Sqr(x)))),CN1)),Integrate(Times(Power(Plus(d,Times(e,Sqr(x))),Plus(p,C1)),Power(Times(x,Sqrt(Subtract(CN1,Times(Sqr(c),Sqr(x))))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,p),x),NeQ(p,CN1)))),
IIntegrate(6867,Integrate(Times(Plus(a_DEFAULT,Times(ArcSech(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times(Power(Times(f,x),m),Power(Plus(d,Times(e,Sqr(x))),p)),x))),Plus(Simp(Dist(Plus(a,Times(b,ArcSech(Times(c,x)))),u,x),x),Simp(Dist(Times(b,Sqrt(Plus(C1,Times(c,x))),Sqrt(Power(Plus(C1,Times(c,x)),CN1))),Integrate(SimplifyIntegrand(Times(u,Power(Times(x,Sqrt(Subtract(C1,Times(c,x))),Sqrt(Plus(C1,Times(c,x)))),CN1)),x),x),x),x))),And(FreeQ(List(a,b,c,d,e,f,m,p),x),Or(And(IGtQ(p,C0),Not(And(ILtQ(Times(C1D2,Plus(m,CN1)),C0),GtQ(Plus(m,Times(C2,p),C3),C0)))),And(IGtQ(Times(C1D2,Plus(m,C1)),C0),Not(And(ILtQ(p,C0),GtQ(Plus(m,Times(C2,p),C3),C0)))),And(ILtQ(Times(C1D2,Plus(m,Times(C2,p),C1)),C0),Not(ILtQ(Times(C1D2,Plus(m,CN1)),C0))))))),
IIntegrate(6868,Integrate(Times(Plus(a_DEFAULT,Times(ArcCsch(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times(Power(Times(f,x),m),Power(Plus(d,Times(e,Sqr(x))),p)),x))),Subtract(Simp(Dist(Plus(a,Times(b,ArcCsch(Times(c,x)))),u,x),x),Simp(Dist(Times(b,c,x,Power(Times(CN1,Sqr(c),Sqr(x)),CN1D2)),Integrate(SimplifyIntegrand(Times(u,Power(Times(x,Sqrt(Subtract(CN1,Times(Sqr(c),Sqr(x))))),CN1)),x),x),x),x))),And(FreeQ(List(a,b,c,d,e,f,m,p),x),Or(And(IGtQ(p,C0),Not(And(ILtQ(Times(C1D2,Plus(m,CN1)),C0),GtQ(Plus(m,Times(C2,p),C3),C0)))),And(IGtQ(Times(C1D2,Plus(m,C1)),C0),Not(And(ILtQ(p,C0),GtQ(Plus(m,Times(C2,p),C3),C0)))),And(ILtQ(Times(C1D2,Plus(m,Times(C2,p),C1)),C0),Not(ILtQ(Times(C1D2,Plus(m,CN1)),C0))))))),
IIntegrate(6869,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSech(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(x_,m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Power(Plus(e,Times(d,Sqr(x))),p),Power(Plus(a,Times(b,ArcCosh(Times(x,Power(c,CN1))))),n),Power(Power(x,Plus(m,Times(C2,Plus(p,C1)))),CN1)),x),x,Power(x,CN1))),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(n,C0),IntegersQ(m,p)))),
IIntegrate(6870,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCsch(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(x_,m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Power(Plus(e,Times(d,Sqr(x))),p),Power(Plus(a,Times(b,ArcSinh(Times(x,Power(c,CN1))))),n),Power(Power(x,Plus(m,Times(C2,Plus(p,C1)))),CN1)),x),x,Power(x,CN1))),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(n,C0),IntegersQ(m,p)))),
IIntegrate(6871,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSech(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(x_,m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Dist(Times(CN1,Sqrt(Sqr(x)),Power(x,CN1)),Subst(Integrate(Times(Power(Plus(e,Times(d,Sqr(x))),p),Power(Plus(a,Times(b,ArcCosh(Times(x,Power(c,CN1))))),n),Power(Power(x,Plus(m,Times(C2,Plus(p,C1)))),CN1)),x),x,Power(x,CN1)),x),x),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(n,C0),EqQ(Plus(Times(Sqr(c),d),e),C0),IntegerQ(m),IntegerQ(Plus(p,C1D2)),GtQ(e,C0),LtQ(d,C0)))),
IIntegrate(6872,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCsch(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(x_,m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Dist(Times(CN1,Sqrt(Sqr(x)),Power(x,CN1)),Subst(Integrate(Times(Power(Plus(e,Times(d,Sqr(x))),p),Power(Plus(a,Times(b,ArcSinh(Times(x,Power(c,CN1))))),n),Power(Power(x,Plus(m,Times(C2,Plus(p,C1)))),CN1)),x),x,Power(x,CN1)),x),x),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(n,C0),EqQ(Subtract(e,Times(Sqr(c),d)),C0),IntegerQ(m),IntegerQ(Plus(p,C1D2)),GtQ(e,C0),LtQ(d,C0)))),
IIntegrate(6873,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSech(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(x_,m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Dist(Times(CN1,Sqrt(Plus(d,Times(e,Sqr(x)))),Power(Times(x,Sqrt(Plus(e,Times(d,Power(x,CN2))))),CN1)),Subst(Integrate(Times(Power(Plus(e,Times(d,Sqr(x))),p),Power(Plus(a,Times(b,ArcCosh(Times(x,Power(c,CN1))))),n),Power(Power(x,Plus(m,Times(C2,Plus(p,C1)))),CN1)),x),x,Power(x,CN1)),x),x),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(n,C0),EqQ(Plus(Times(Sqr(c),d),e),C0),IntegerQ(m),IntegerQ(Plus(p,C1D2)),Not(And(GtQ(e,C0),LtQ(d,C0)))))),
IIntegrate(6874,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCsch(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(x_,m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Dist(Times(CN1,Sqrt(Plus(d,Times(e,Sqr(x)))),Power(Times(x,Sqrt(Plus(e,Times(d,Power(x,CN2))))),CN1)),Subst(Integrate(Times(Power(Plus(e,Times(d,Sqr(x))),p),Power(Plus(a,Times(b,ArcSinh(Times(x,Power(c,CN1))))),n),Power(Power(x,Plus(m,Times(C2,Plus(p,C1)))),CN1)),x),x,Power(x,CN1)),x),x),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(n,C0),EqQ(Subtract(e,Times(Sqr(c),d)),C0),IntegerQ(m),IntegerQ(Plus(p,C1D2)),Not(And(GtQ(e,C0),LtQ(d,C0)))))),
IIntegrate(6875,Integrate(Times(Plus(a_DEFAULT,Times(ArcSech(Times(c_DEFAULT,x_)),b_DEFAULT)),u_),x_Symbol),
    Condition(With(list(Set(v,IntHide(u,x))),Condition(Plus(Simp(Dist(Plus(a,Times(b,ArcSech(Times(c,x)))),v,x),x),Simp(Dist(Times(b,Sqrt(Subtract(C1,Times(Sqr(c),Sqr(x)))),Power(Times(c,x,Sqrt(Plus(CN1,Power(Times(c,x),CN1))),Sqrt(Plus(C1,Power(Times(c,x),CN1)))),CN1)),Integrate(SimplifyIntegrand(Times(v,Power(Times(x,Sqrt(Subtract(C1,Times(Sqr(c),Sqr(x))))),CN1)),x),x),x),x)),InverseFunctionFreeQ(v,x))),FreeQ(list(a,b,c),x))),
IIntegrate(6876,Integrate(Times(Plus(a_DEFAULT,Times(ArcCsch(Times(c_DEFAULT,x_)),b_DEFAULT)),u_),x_Symbol),
    Condition(With(list(Set(v,IntHide(u,x))),Condition(Plus(Simp(Dist(Plus(a,Times(b,ArcCsch(Times(c,x)))),v,x),x),Simp(Dist(Times(b,Power(c,CN1)),Integrate(SimplifyIntegrand(Times(v,Power(Times(Sqr(x),Sqrt(Plus(C1,Power(Times(Sqr(c),Sqr(x)),CN1)))),CN1)),x),x),x),x)),InverseFunctionFreeQ(v,x))),FreeQ(list(a,b,c),x))),
IIntegrate(6877,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSech(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Unintegrable(Times(u,Power(Plus(a,Times(b,ArcSech(Times(c,x)))),n)),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(6878,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCsch(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Unintegrable(Times(u,Power(Plus(a,Times(b,ArcCsch(Times(c,x)))),n)),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(6879,Integrate(ArcSech(Plus(c_,Times(d_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Times(Plus(c,Times(d,x)),ArcSech(Plus(c,Times(d,x))),Power(d,CN1)),x),Integrate(Times(Sqrt(Times(Subtract(Subtract(C1,c),Times(d,x)),Power(Plus(C1,c,Times(d,x)),CN1))),Power(Subtract(Subtract(C1,c),Times(d,x)),CN1)),x)),FreeQ(list(c,d),x))),
IIntegrate(6880,Integrate(ArcCsch(Plus(c_,Times(d_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Times(Plus(c,Times(d,x)),ArcCsch(Plus(c,Times(d,x))),Power(d,CN1)),x),Integrate(Power(Times(Plus(c,Times(d,x)),Sqrt(Plus(C1,Power(Plus(c,Times(d,x)),CN2)))),CN1),x)),FreeQ(list(c,d),x)))
  );
}
