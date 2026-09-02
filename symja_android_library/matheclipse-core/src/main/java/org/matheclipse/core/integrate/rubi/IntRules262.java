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
class IntRules262 { 
  public static IAST RULES = List( 
IIntegrate(5241,Integrate(Times(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),x_,Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(d,Times(e,Sqr(x))),Plus(p,C1)),Plus(a,Times(b,ArcCos(Times(c,x)))),Power(Times(C2,e,Plus(p,C1)),CN1)),x),Simp(Dist(Times(b,c,Power(Times(C2,e,Plus(p,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,Sqr(x))),Plus(p,C1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),x)),And(FreeQ(List(a,b,c,d,e,p),x),NeQ(Plus(Times(Sqr(c),d),e),C0),NeQ(p,CN1)))),
IIntegrate(5242,Integrate(Times(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times(Power(Times(f,x),m),Power(Plus(d,Times(e,Sqr(x))),p)),x))),Subtract(Simp(Dist(Plus(a,Times(b,ArcSin(Times(c,x)))),u,x),x),Simp(Dist(Times(b,c),Integrate(SimplifyIntegrand(Times(u,Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),x),x))),And(FreeQ(List(a,b,c,d,e,f,m),x),NeQ(Plus(Times(Sqr(c),d),e),C0),IntegerQ(p),Or(GtQ(p,C0),And(IGtQ(Times(C1D2,Plus(m,CN1)),C0),LeQ(Plus(m,p),C0)))))),
IIntegrate(5243,Integrate(Times(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times(Power(Times(f,x),m),Power(Plus(d,Times(e,Sqr(x))),p)),x))),Plus(Simp(Dist(Plus(a,Times(b,ArcCos(Times(c,x)))),u,x),x),Simp(Dist(Times(b,c),Integrate(SimplifyIntegrand(Times(u,Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),x),x))),And(FreeQ(List(a,b,c,d,e,f,m),x),NeQ(Plus(Times(Sqr(c),d),e),C0),IntegerQ(p),Or(GtQ(p,C0),And(IGtQ(Times(C1D2,Plus(m,CN1)),C0),LeQ(Plus(m,p),C0)))))),
IIntegrate(5244,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n),Times(Power(Times(f,x),m),Power(Plus(d,Times(e,Sqr(x))),p)),x),x),And(FreeQ(List(a,b,c,d,e,f),x),NeQ(Plus(Times(Sqr(c),d),e),C0),IGtQ(n,C0),IntegerQ(p),IntegerQ(m)))),
IIntegrate(5245,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n),Times(Power(Times(f,x),m),Power(Plus(d,Times(e,Sqr(x))),p)),x),x),And(FreeQ(List(a,b,c,d,e,f),x),NeQ(Plus(Times(Sqr(c),d),e),C0),IGtQ(n,C0),IntegerQ(p),IntegerQ(m)))),
IIntegrate(5246,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(f,x),m),Power(Plus(d,Times(e,Sqr(x))),p),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n)),x),FreeQ(List(a,b,c,d,e,f,m,n,p),x))),
IIntegrate(5247,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(f,x),m),Power(Plus(d,Times(e,Sqr(x))),p),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n)),x),FreeQ(List(a,b,c,d,e,f,m,n,p),x))),
IIntegrate(5248,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Times(h_DEFAULT,x_),m_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,x_)),p_),Power(Plus(f_,Times(g_DEFAULT,x_)),q_)),x_Symbol),
    Condition(Simp(Dist(Power(Times(CN1,Sqr(d),g,Power(e,CN1)),q),Integrate(Times(Power(Times(h,x),m),Power(Plus(d,Times(e,x)),Subtract(p,q)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),q),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n)),x),x),x),And(FreeQ(List(a,b,c,d,e,f,g,h,m,n),x),EqQ(Plus(Times(e,f),Times(d,g)),C0),EqQ(Subtract(Times(Sqr(c),Sqr(d)),Sqr(e)),C0),HalfIntegerQ(p,q),GeQ(Subtract(p,q),C0),GtQ(d,C0),LtQ(Times(g,Power(e,CN1)),C0)))),
IIntegrate(5249,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Times(h_DEFAULT,x_),m_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,x_)),p_),Power(Plus(f_,Times(g_DEFAULT,x_)),q_)),x_Symbol),
    Condition(Simp(Dist(Power(Times(CN1,Sqr(d),g,Power(e,CN1)),q),Integrate(Times(Power(Times(h,x),m),Power(Plus(d,Times(e,x)),Subtract(p,q)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),q),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n)),x),x),x),And(FreeQ(List(a,b,c,d,e,f,g,h,m,n),x),EqQ(Plus(Times(e,f),Times(d,g)),C0),EqQ(Subtract(Times(Sqr(c),Sqr(d)),Sqr(e)),C0),HalfIntegerQ(p,q),GeQ(Subtract(p,q),C0),GtQ(d,C0),LtQ(Times(g,Power(e,CN1)),C0)))),
IIntegrate(5250,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Times(h_DEFAULT,x_),m_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,x_)),p_),Power(Plus(f_,Times(g_DEFAULT,x_)),q_)),x_Symbol),
    Condition(Simp(Dist(Times(Power(Times(CN1,Sqr(d),g,Power(e,CN1)),IntPart(q)),Power(Plus(d,Times(e,x)),FracPart(q)),Power(Plus(f,Times(g,x)),FracPart(q)),Power(Power(Subtract(C1,Times(Sqr(c),Sqr(x))),FracPart(q)),CN1)),Integrate(Times(Power(Times(h,x),m),Power(Plus(d,Times(e,x)),Subtract(p,q)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),q),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n)),x),x),x),And(FreeQ(List(a,b,c,d,e,f,g,h,m,n),x),EqQ(Plus(Times(e,f),Times(d,g)),C0),EqQ(Subtract(Times(Sqr(c),Sqr(d)),Sqr(e)),C0),HalfIntegerQ(p,q),GeQ(Subtract(p,q),C0)))),
IIntegrate(5251,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Times(h_DEFAULT,x_),m_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,x_)),p_),Power(Plus(f_,Times(g_DEFAULT,x_)),q_)),x_Symbol),
    Condition(Simp(Dist(Times(Power(Times(CN1,Sqr(d),g,Power(e,CN1)),IntPart(q)),Power(Plus(d,Times(e,x)),FracPart(q)),Power(Plus(f,Times(g,x)),FracPart(q)),Power(Power(Subtract(C1,Times(Sqr(c),Sqr(x))),FracPart(q)),CN1)),Integrate(Times(Power(Times(h,x),m),Power(Plus(d,Times(e,x)),Subtract(p,q)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),q),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n)),x),x),x),And(FreeQ(List(a,b,c,d,e,f,g,h,m,n),x),EqQ(Plus(Times(e,f),Times(d,g)),C0),EqQ(Subtract(Times(Sqr(c),Sqr(d)),Sqr(e)),C0),HalfIntegerQ(p,q),GeQ(Subtract(p,q),C0)))),
IIntegrate(5252,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Subst(Integrate(Times(Power(Plus(a,Times(b,x)),n),Cos(x),Power(Plus(Times(c,d),Times(e,Sin(x))),CN1)),x),x,ArcSin(Times(c,x))),And(FreeQ(List(a,b,c,d,e),x),IGtQ(n,C0)))),
IIntegrate(5253,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Power(Plus(a,Times(b,x)),n),Sin(x),Power(Plus(Times(c,d),Times(e,Cos(x))),CN1)),x),x,ArcCos(Times(c,x)))),And(FreeQ(List(a,b,c,d,e),x),IGtQ(n,C0)))),
IIntegrate(5254,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n),Power(Times(e,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,c,n,Power(Times(e,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),Plus(n,CN1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),x)),And(FreeQ(List(a,b,c,d,e,m),x),IGtQ(n,C0),NeQ(m,CN1)))),
IIntegrate(5255,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n),Power(Times(e,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,c,n,Power(Times(e,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),Plus(n,CN1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),x)),And(FreeQ(List(a,b,c,d,e,m),x),IGtQ(n,C0),NeQ(m,CN1)))),
IIntegrate(5256,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(Plus(d_,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times(Power(Plus(d,Times(e,x)),m),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n)),x),x),And(FreeQ(List(a,b,c,d,e),x),IGtQ(m,C0),LtQ(n,CN1)))),
IIntegrate(5257,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(Plus(d_,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times(Power(Plus(d,Times(e,x)),m),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n)),x),x),And(FreeQ(List(a,b,c,d,e),x),IGtQ(m,C0),LtQ(n,CN1)))),
IIntegrate(5258,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(Power(c,Plus(m,C1)),CN1),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),n),Cos(x),Power(Plus(Times(c,d),Times(e,Sin(x))),m)),x),x,ArcSin(Times(c,x))),x),x),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(m,C0)))),
IIntegrate(5259,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Negate(Power(Power(c,Plus(m,C1)),CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),n),Sin(x),Power(Plus(Times(c,d),Times(e,Cos(x))),m)),x),x,ArcCos(Times(c,x))),x),x),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(m,C0)))),
IIntegrate(5260,Integrate(Times(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),$p("§px")),x_Symbol),
    Condition(With(list(Set(u,IntHide(ExpandExpression($s("§px"),x),x))),Subtract(Simp(Dist(Plus(a,Times(b,ArcSin(Times(c,x)))),u,x),x),Simp(Dist(Times(b,c),Integrate(SimplifyIntegrand(Times(u,Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),x),x))),And(FreeQ(list(a,b,c),x),PolynomialQ($s("§px"),x))))
  );
}
