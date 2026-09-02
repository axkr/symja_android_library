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
class IntRules310 { 
  public static IAST RULES = List( 
IIntegrate(6201,Integrate(Power(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),x_Symbol),
    Condition(Simp(Dist(Power(Times(b,c),CN1),Subst(Integrate(Times(Power(x,n),Cosh(Plus(Times(CN1,a,Power(b,CN1)),Times(x,Power(b,CN1))))),x),x,Plus(a,Times(b,ArcSinh(Times(c,x))))),x),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(6202,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(x_,CN1)),x_Symbol),
    Condition(Simp(Dist(Power(b,CN1),Subst(Integrate(Times(Power(x,n),Coth(Plus(Times(CN1,a,Power(b,CN1)),Times(x,Power(b,CN1))))),x),x,Plus(a,Times(b,ArcSinh(Times(c,x))))),x),x),And(FreeQ(list(a,b,c),x),IGtQ(n,C0)))),
IIntegrate(6203,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(d,x),Plus(m,C1)),Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),n),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,c,n,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(d,x),Plus(m,C1)),Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),Plus(n,CN1)),Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),x)),And(FreeQ(List(a,b,c,d,m),x),IGtQ(n,C0),NeQ(m,CN1)))),
IIntegrate(6204,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),n),Power(Plus(m,C1),CN1)),x),Simp(Dist(Times(b,c,n,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),Plus(n,CN1)),Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),x)),And(FreeQ(list(a,b,c),x),IGtQ(m,C0),GtQ(n,C0)))),
IIntegrate(6205,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,m),Sqrt(Plus(C1,Times(Sqr(c),Sqr(x)))),Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),Plus(n,C1)),Power(Times(b,c,Plus(n,C1)),CN1)),x),Simp(Dist(Power(Times(Sqr(b),Power(c,Plus(m,C1)),Plus(n,C1)),CN1),Subst(Integrate(ExpandTrigReduce(Power(x,Plus(n,C1)),Times(Power(Sinh(Plus(Times(CN1,a,Power(b,CN1)),Times(x,Power(b,CN1)))),Plus(m,CN1)),Plus(m,Times(Plus(m,C1),Sqr(Sinh(Plus(Times(CN1,a,Power(b,CN1)),Times(x,Power(b,CN1)))))))),x),x),x,Plus(a,Times(b,ArcSinh(Times(c,x))))),x),x)),And(FreeQ(list(a,b,c),x),IGtQ(m,C0),GeQ(n,CN2),LtQ(n,CN1)))),
IIntegrate(6206,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,m),Sqrt(Plus(C1,Times(Sqr(c),Sqr(x)))),Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),Plus(n,C1)),Power(Times(b,c,Plus(n,C1)),CN1)),x),Negate(Simp(Dist(Times(c,Plus(m,C1),Power(Times(b,Plus(n,C1)),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),Plus(n,C1)),Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),x)),Negate(Simp(Dist(Times(m,Power(Times(b,c,Plus(n,C1)),CN1)),Integrate(Times(Power(x,Plus(m,CN1)),Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),Plus(n,C1)),Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),x))),And(FreeQ(list(a,b,c),x),IGtQ(m,C0),LtQ(n,CN2)))),
IIntegrate(6207,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(Times(b,Power(c,Plus(m,C1))),CN1),Subst(Integrate(Times(Power(x,n),Power(Sinh(Plus(Times(CN1,a,Power(b,CN1)),Times(x,Power(b,CN1)))),m),Cosh(Plus(Times(CN1,a,Power(b,CN1)),Times(x,Power(b,CN1))))),x),x,Plus(a,Times(b,ArcSinh(Times(c,x))))),x),x),And(FreeQ(List(a,b,c,n),x),IGtQ(m,C0)))),
IIntegrate(6208,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(d,x),m),Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),n)),x),FreeQ(List(a,b,c,d,m,n),x))),
IIntegrate(6209,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),CN1),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Times(Power(Times(b,c),CN1),Simp(Times(Sqrt(Plus(C1,Times(Sqr(c),Sqr(x)))),Power(Plus(d,Times(e,Sqr(x))),CN1D2)),x),Log(Plus(a,Times(b,ArcSinh(Times(c,x)))))),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d))))),
IIntegrate(6210,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Times(Power(Times(b,c,Plus(n,C1)),CN1),Simp(Times(Sqrt(Plus(C1,Times(Sqr(c),Sqr(x)))),Power(Plus(d,Times(e,Sqr(x))),CN1D2)),x),Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),Plus(n,C1))),x),And(FreeQ(List(a,b,c,d,e,n),x),EqQ(e,Times(Sqr(c),d)),NeQ(n,CN1)))),
IIntegrate(6211,Integrate(Times(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Power(Plus(d,Times(e,Sqr(x))),p),x))),Subtract(Simp(Dist(Plus(a,Times(b,ArcSinh(Times(c,x)))),u,x),x),Simp(Dist(Times(b,c),Integrate(SimplifyIntegrand(Times(u,Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),x),x))),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),IGtQ(p,C0)))),
IIntegrate(6212,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Sqrt(Plus(d_,Times(e_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Simp(Times(x,Sqrt(Plus(d,Times(e,Sqr(x)))),C1D2,Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),n)),x),Simp(Dist(Times(C1D2,Simp(Times(Sqrt(Plus(d,Times(e,Sqr(x)))),Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x)),Integrate(Times(Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),n),Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),x),Negate(Simp(Dist(Times(b,c,C1D2,n,Simp(Times(Sqrt(Plus(d,Times(e,Sqr(x)))),Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x)),Integrate(Times(x,Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),Plus(n,CN1))),x),x),x))),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),GtQ(n,C0)))),
IIntegrate(6213,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(x,Power(Plus(d,Times(e,Sqr(x))),p),Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),n),Power(Plus(Times(C2,p),C1),CN1)),x),Simp(Dist(Times(C2,d,p,Power(Plus(Times(C2,p),C1),CN1)),Integrate(Times(Power(Plus(d,Times(e,Sqr(x))),Plus(p,CN1)),Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),n)),x),x),x),Negate(Simp(Dist(Times(b,c,n,Power(Plus(Times(C2,p),C1),CN1),Simp(Times(Power(Plus(d,Times(e,Sqr(x))),p),Power(Power(Plus(C1,Times(Sqr(c),Sqr(x))),p),CN1)),x)),Integrate(Times(x,Power(Plus(C1,Times(Sqr(c),Sqr(x))),Plus(p,CN1D2)),Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),Plus(n,CN1))),x),x),x))),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),GtQ(n,C0),GtQ(p,C0)))),
IIntegrate(6214,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),QQ(-3L,2L))),x_Symbol),
    Condition(Subtract(Simp(Times(x,Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),n),Power(Times(d,Sqrt(Plus(d,Times(e,Sqr(x))))),CN1)),x),Simp(Dist(Times(b,c,n,Power(d,CN1),Simp(Times(Sqrt(Plus(C1,Times(Sqr(c),Sqr(x)))),Power(Plus(d,Times(e,Sqr(x))),CN1D2)),x)),Integrate(Times(x,Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),Plus(n,CN1)),Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),GtQ(n,C0)))),
IIntegrate(6215,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,x,Power(Plus(d,Times(e,Sqr(x))),Plus(p,C1)),Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),n),Power(Times(C2,d,Plus(p,C1)),CN1)),x),Simp(Dist(Times(Plus(Times(C2,p),C3),Power(Times(C2,d,Plus(p,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,Sqr(x))),Plus(p,C1)),Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),n)),x),x),x),Simp(Dist(Times(b,c,n,Power(Times(C2,Plus(p,C1)),CN1),Simp(Times(Power(Plus(d,Times(e,Sqr(x))),p),Power(Power(Plus(C1,Times(Sqr(c),Sqr(x))),p),CN1)),x)),Integrate(Times(x,Power(Plus(C1,Times(Sqr(c),Sqr(x))),Plus(p,C1D2)),Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),Plus(n,CN1))),x),x),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),GtQ(n,C0),LtQ(p,CN1),NeQ(p,QQ(-3L,2L))))),
IIntegrate(6216,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Simp(Dist(Power(Times(c,d),CN1),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),n),Sech(x)),x),x,ArcSinh(Times(c,x))),x),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),IGtQ(n,C0)))),
IIntegrate(6217,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Simp(Times(Sqrt(Plus(C1,Times(Sqr(c),Sqr(x)))),Power(Plus(d,Times(e,Sqr(x))),p)),x),Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),Plus(n,C1)),Power(Times(b,c,Plus(n,C1)),CN1)),x),Simp(Dist(Times(c,Plus(Times(C2,p),C1),Power(Times(b,Plus(n,C1)),CN1),Simp(Times(Power(Plus(d,Times(e,Sqr(x))),p),Power(Power(Plus(C1,Times(Sqr(c),Sqr(x))),p),CN1)),x)),Integrate(Times(x,Power(Plus(C1,Times(Sqr(c),Sqr(x))),Plus(p,CN1D2)),Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),Plus(n,C1))),x),x),x)),And(FreeQ(List(a,b,c,d,e,p),x),EqQ(e,Times(Sqr(c),d)),LtQ(n,CN1)))),
IIntegrate(6218,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(Power(Times(b,c),CN1),Simp(Times(Power(Plus(d,Times(e,Sqr(x))),p),Power(Power(Plus(C1,Times(Sqr(c),Sqr(x))),p),CN1)),x)),Subst(Integrate(Times(Power(x,n),Power(Cosh(Plus(Times(CN1,a,Power(b,CN1)),Times(x,Power(b,CN1)))),Plus(Times(C2,p),C1))),x),x,Plus(a,Times(b,ArcSinh(Times(c,x))))),x),x),And(FreeQ(List(a,b,c,d,e,n),x),EqQ(e,Times(Sqr(c),d)),IGtQ(Times(C2,p),C0)))),
IIntegrate(6219,Integrate(Times(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Power(Plus(d,Times(e,Sqr(x))),p),x))),Subtract(Simp(Dist(Plus(a,Times(b,ArcSinh(Times(c,x)))),u,x),x),Simp(Dist(Times(b,c),Integrate(SimplifyIntegrand(Times(u,Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),x),x))),And(FreeQ(List(a,b,c,d,e),x),NeQ(e,Times(Sqr(c),d)),Or(IGtQ(p,C0),ILtQ(Plus(p,C1D2),C0))))),
IIntegrate(6220,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),n),Power(Plus(d,Times(e,Sqr(x))),p),x),x),And(FreeQ(List(a,b,c,d,e,n),x),NeQ(e,Times(Sqr(c),d)),IntegerQ(p),Or(Greater(p,C0),IGtQ(n,C0)))))
  );
}
