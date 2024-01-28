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
class IntRules262 { 
  public static IAST RULES = List( 
IIntegrate(5241,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Power(Plus(a,Times(b,x)),n),Sin(x),Power(Plus(Times(c,d),Times(e,Cos(x))),CN1)),x),x,ArcCos(Times(c,x)))),And(FreeQ(List(a,b,c,d,e),x),IGtQ(n,C0)))),
IIntegrate(5242,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n),Power(Times(e,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,c,n,Power(Times(e,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),Subtract(n,C1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x)),x)),And(FreeQ(List(a,b,c,d,e,m),x),IGtQ(n,C0),NeQ(m,CN1)))),
IIntegrate(5243,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n),Power(Times(e,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,c,n,Power(Times(e,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),Subtract(n,C1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x)),x)),And(FreeQ(List(a,b,c,d,e,m),x),IGtQ(n,C0),NeQ(m,CN1)))),
IIntegrate(5244,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(Plus(d_,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times(Power(Plus(d,Times(e,x)),m),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n)),x),x),And(FreeQ(List(a,b,c,d,e),x),IGtQ(m,C0),LtQ(n,CN1)))),
IIntegrate(5245,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(Plus(d_,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times(Power(Plus(d,Times(e,x)),m),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n)),x),x),And(FreeQ(List(a,b,c,d,e),x),IGtQ(m,C0),LtQ(n,CN1)))),
IIntegrate(5246,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Power(c,Plus(m,C1)),CN1),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),n),Cos(x),Power(Plus(Times(c,d),Times(e,Sin(x))),m)),x),x,ArcSin(Times(c,x)))),x),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(m,C0)))),
IIntegrate(5247,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Negate(Power(Power(c,Plus(m,C1)),CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),n),Sin(x),Power(Plus(Times(c,d),Times(e,Cos(x))),m)),x),x,ArcCos(Times(c,x)))),x),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(m,C0)))),
IIntegrate(5248,Integrate(Times(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),$p("§px")),x_Symbol),
    Condition(With(list(Set(u,IntHide(ExpandExpression($s("§px"),x),x))),Subtract(Simp(Star(Plus(a,Times(b,ArcSin(Times(c,x)))),u),x),Simp(Star(Times(b,c),Integrate(SimplifyIntegrand(Times(u,Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x)),x))),And(FreeQ(list(a,b,c),x),PolynomialQ($s("§px"),x)))),
IIntegrate(5249,Integrate(Times(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),$p("§px")),x_Symbol),
    Condition(With(list(Set(u,IntHide(ExpandExpression($s("§px"),x),x))),Plus(Simp(Star(Plus(a,Times(b,ArcCos(Times(c,x)))),u),x),Simp(Star(Times(b,c),Integrate(SimplifyIntegrand(Times(u,Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x)),x))),And(FreeQ(list(a,b,c),x),PolynomialQ($s("§px"),x)))),
IIntegrate(5250,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),$p("§px")),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times($s("§px"),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n)),x),x),And(FreeQ(List(a,b,c,n),x),PolynomialQ($s("§px"),x)))),
IIntegrate(5251,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),$p("§px")),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times($s("§px"),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n)),x),x),And(FreeQ(List(a,b,c,n),x),PolynomialQ($s("§px"),x)))),
IIntegrate(5252,Integrate(Times(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),$p("§px"),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times($s("§px"),Power(Plus(d,Times(e,x)),m)),x))),Subtract(Simp(Star(Plus(a,Times(b,ArcSin(Times(c,x)))),u),x),Simp(Star(Times(b,c),Integrate(SimplifyIntegrand(Times(u,Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x)),x))),And(FreeQ(List(a,b,c,d,e,m),x),PolynomialQ($s("§px"),x)))),
IIntegrate(5253,Integrate(Times(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),$p("§px"),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times($s("§px"),Power(Plus(d,Times(e,x)),m)),x))),Plus(Simp(Star(Plus(a,Times(b,ArcCos(Times(c,x)))),u),x),Simp(Star(Times(b,c),Integrate(SimplifyIntegrand(Times(u,Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x)),x))),And(FreeQ(List(a,b,c,d,e,m),x),PolynomialQ($s("§px"),x)))),
IIntegrate(5254,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(Plus(d_,Times(e_DEFAULT,x_)),m_),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),p_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times(Power(Plus(f,Times(g,x)),p),Power(Plus(d,Times(e,x)),m)),x))),Subtract(Simp(Star(Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n),u),x),Simp(Star(Times(b,c,n),Integrate(SimplifyIntegrand(Times(u,Power(Plus(a,Times(b,ArcSin(Times(c,x)))),Subtract(n,C1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x)),x))),And(FreeQ(List(a,b,c,d,e,f,g),x),IGtQ(n,C0),IGtQ(p,C0),ILtQ(m,C0),LtQ(Plus(m,p,C1),C0)))),
IIntegrate(5255,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(Plus(d_,Times(e_DEFAULT,x_)),m_),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),p_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times(Power(Plus(f,Times(g,x)),p),Power(Plus(d,Times(e,x)),m)),x))),Plus(Simp(Star(Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n),u),x),Simp(Star(Times(b,c,n),Integrate(SimplifyIntegrand(Times(u,Power(Plus(a,Times(b,ArcCos(Times(c,x)))),Subtract(n,C1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x)),x))),And(FreeQ(List(a,b,c,d,e,f,g),x),IGtQ(n,C0),IGtQ(p,C0),ILtQ(m,C0),LtQ(Plus(m,p,C1),C0)))),
IIntegrate(5256,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(Plus(d_,Times(e_DEFAULT,x_)),CN2),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_),Times(h_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times(Power(Plus(f,Times(g,x),Times(h,Sqr(x))),p),Power(Plus(d,Times(e,x)),CN2)),x))),Subtract(Simp(Star(Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n),u),x),Simp(Star(Times(b,c,n),Integrate(SimplifyIntegrand(Times(u,Power(Plus(a,Times(b,ArcSin(Times(c,x)))),Subtract(n,C1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x)),x))),And(FreeQ(List(a,b,c,d,e,f,g,h),x),IGtQ(n,C0),IGtQ(p,C0),EqQ(Subtract(Times(e,g),Times(C2,d,h)),C0)))),
IIntegrate(5257,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(Plus(d_,Times(e_DEFAULT,x_)),CN2),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_),Times(h_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times(Power(Plus(f,Times(g,x),Times(h,Sqr(x))),p),Power(Plus(d,Times(e,x)),CN2)),x))),Plus(Simp(Star(Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n),u),x),Simp(Star(Times(b,c,n),Integrate(SimplifyIntegrand(Times(u,Power(Plus(a,Times(b,ArcCos(Times(c,x)))),Subtract(n,C1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x)),x))),And(FreeQ(List(a,b,c,d,e,f,g,h),x),IGtQ(n,C0),IGtQ(p,C0),EqQ(Subtract(Times(e,g),Times(C2,d,h)),C0)))),
IIntegrate(5258,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),$p("§px"),Power(Plus(d_,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times($s("§px"),Power(Plus(d,Times(e,x)),m),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n)),x),x),And(FreeQ(List(a,b,c,d,e),x),PolynomialQ($s("§px"),x),IGtQ(n,C0),IntegerQ(m)))),
IIntegrate(5259,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),$p("§px"),Power(Plus(d_,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times($s("§px"),Power(Plus(d,Times(e,x)),m),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n)),x),x),And(FreeQ(List(a,b,c,d,e),x),PolynomialQ($s("§px"),x),IGtQ(n,C0),IntegerQ(m)))),
IIntegrate(5260,Integrate(Times(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(f_,Times(g_DEFAULT,x_)),m_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times(Power(Plus(f,Times(g,x)),m),Power(Plus(d,Times(e,Sqr(x))),p)),x))),Subtract(Simp(Star(Plus(a,Times(b,ArcSin(Times(c,x)))),u),x),Simp(Star(Times(b,c),Integrate(Star(Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2),u),x)),x))),And(FreeQ(List(a,b,c,d,e,f,g),x),EqQ(Plus(Times(Sqr(c),d),e),C0),IGtQ(m,C0),ILtQ(Plus(p,C1D2),C0),GtQ(d,C0),Or(LtQ(m,Subtract(Times(CN2,p),C1)),GtQ(m,C3)))))
  );
}
