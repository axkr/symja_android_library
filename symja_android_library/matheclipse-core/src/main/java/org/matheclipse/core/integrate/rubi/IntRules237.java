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
public class IntRules237 { 
  public static IAST RULES = List( 
IIntegrate(4741,Int(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Subst(Int(Times(Power(Plus(a,Times(b,x)),n),Cos(x),Power(Plus(Times(c,d),Times(e,Sin(x))),CN1)),x),x,ArcSin(Times(c,x))),And(FreeQ(List(a,b,c,d,e),x),IGtQ(n,C0)))),
IIntegrate(4742,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Negate(Subst(Int(Times(Power(Plus(a,Times(b,x)),n),Sin(x),Power(Plus(Times(c,d),Times(e,Cos(x))),CN1)),x),x,ArcCos(Times(c,x)))),And(FreeQ(List(a,b,c,d,e),x),IGtQ(n,C0)))),
IIntegrate(4743,Int(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n),Power(Times(e,Plus(m,C1)),CN1)),x),Dist(Times(b,c,n,Power(Times(e,Plus(m,C1)),CN1)),Int(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),Subtract(n,C1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x)),And(FreeQ(List(a,b,c,d,e,m),x),IGtQ(n,C0),NeQ(m,CN1)))),
IIntegrate(4744,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n),Power(Times(e,Plus(m,C1)),CN1)),x),Dist(Times(b,c,n,Power(Times(e,Plus(m,C1)),CN1)),Int(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),Subtract(n,C1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x)),And(FreeQ(List(a,b,c,d,e,m),x),IGtQ(n,C0),NeQ(m,CN1)))),
IIntegrate(4745,Int(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(Plus(d_,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Int(ExpandIntegrand(Times(Power(Plus(d,Times(e,x)),m),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n)),x),x),And(FreeQ(List(a,b,c,d,e),x),IGtQ(m,C0),LtQ(n,CN1)))),
IIntegrate(4746,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(Plus(d_,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Int(ExpandIntegrand(Times(Power(Plus(d,Times(e,x)),m),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n)),x),x),And(FreeQ(List(a,b,c,d,e),x),IGtQ(m,C0),LtQ(n,CN1)))),
IIntegrate(4747,Int(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Power(c,Plus(m,C1)),CN1),Subst(Int(Times(Power(Plus(a,Times(b,x)),n),Cos(x),Power(Plus(Times(c,d),Times(e,Sin(x))),m)),x),x,ArcSin(Times(c,x))),x),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(m,C0)))),
IIntegrate(4748,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Negate(Dist(Power(Power(c,Plus(m,C1)),CN1),Subst(Int(Times(Power(Plus(a,Times(b,x)),n),Sin(x),Power(Plus(Times(c,d),Times(e,Cos(x))),m)),x),x,ArcCos(Times(c,x))),x)),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(m,C0)))),
IIntegrate(4749,Int(Times(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),$p("§px")),x_Symbol),
    Condition(With(List(Set(u,IntHide(ExpandExpression($s("§px"),x),x))),Subtract(Dist(Plus(a,Times(b,ArcSin(Times(c,x)))),u,x),Dist(Times(b,c),Int(SimplifyIntegrand(Times(u,Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),x))),And(FreeQ(List(a,b,c),x),PolynomialQ($s("§px"),x)))),
IIntegrate(4750,Int(Times(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),$p("§px")),x_Symbol),
    Condition(With(List(Set(u,IntHide(ExpandExpression($s("§px"),x),x))),Plus(Dist(Plus(a,Times(b,ArcCos(Times(c,x)))),u,x),Dist(Times(b,c),Int(SimplifyIntegrand(Times(u,Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),x))),And(FreeQ(List(a,b,c),x),PolynomialQ($s("§px"),x)))),
IIntegrate(4751,Int(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),$p("§px")),x_Symbol),
    Condition(Int(ExpandIntegrand(Times($s("§px"),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n)),x),x),And(FreeQ(List(a,b,c,n),x),PolynomialQ($s("§px"),x)))),
IIntegrate(4752,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),$p("§px")),x_Symbol),
    Condition(Int(ExpandIntegrand(Times($s("§px"),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n)),x),x),And(FreeQ(List(a,b,c,n),x),PolynomialQ($s("§px"),x)))),
IIntegrate(4753,Int(Times(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),$p("§px"),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(With(List(Set(u,IntHide(Times($s("§px"),Power(Plus(d,Times(e,x)),m)),x))),Subtract(Dist(Plus(a,Times(b,ArcSin(Times(c,x)))),u,x),Dist(Times(b,c),Int(SimplifyIntegrand(Times(u,Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),x))),And(FreeQ(List(a,b,c,d,e,m),x),PolynomialQ($s("§px"),x)))),
IIntegrate(4754,Int(Times(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),$p("§px"),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(With(List(Set(u,IntHide(Times($s("§px"),Power(Plus(d,Times(e,x)),m)),x))),Plus(Dist(Plus(a,Times(b,ArcCos(Times(c,x)))),u,x),Dist(Times(b,c),Int(SimplifyIntegrand(Times(u,Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),x))),And(FreeQ(List(a,b,c,d,e,m),x),PolynomialQ($s("§px"),x)))),
IIntegrate(4755,Int(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(Plus(d_,Times(e_DEFAULT,x_)),m_),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),p_DEFAULT)),x_Symbol),
    Condition(With(List(Set(u,IntHide(Times(Power(Plus(f,Times(g,x)),p),Power(Plus(d,Times(e,x)),m)),x))),Subtract(Dist(Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n),u,x),Dist(Times(b,c,n),Int(SimplifyIntegrand(Times(u,Power(Plus(a,Times(b,ArcSin(Times(c,x)))),Subtract(n,C1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),x))),And(FreeQ(List(a,b,c,d,e,f,g),x),IGtQ(n,C0),IGtQ(p,C0),ILtQ(m,C0),LtQ(Plus(m,p,C1),C0)))),
IIntegrate(4756,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(Plus(d_,Times(e_DEFAULT,x_)),m_),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),p_DEFAULT)),x_Symbol),
    Condition(With(List(Set(u,IntHide(Times(Power(Plus(f,Times(g,x)),p),Power(Plus(d,Times(e,x)),m)),x))),Plus(Dist(Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n),u,x),Dist(Times(b,c,n),Int(SimplifyIntegrand(Times(u,Power(Plus(a,Times(b,ArcCos(Times(c,x)))),Subtract(n,C1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),x))),And(FreeQ(List(a,b,c,d,e,f,g),x),IGtQ(n,C0),IGtQ(p,C0),ILtQ(m,C0),LtQ(Plus(m,p,C1),C0)))),
IIntegrate(4757,Int(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(Plus(d_,Times(e_DEFAULT,x_)),CN2),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_),Times(h_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(With(List(Set(u,IntHide(Times(Power(Plus(f,Times(g,x),Times(h,Sqr(x))),p),Power(Plus(d,Times(e,x)),CN2)),x))),Subtract(Dist(Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n),u,x),Dist(Times(b,c,n),Int(SimplifyIntegrand(Times(u,Power(Plus(a,Times(b,ArcSin(Times(c,x)))),Subtract(n,C1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),x))),And(FreeQ(List(a,b,c,d,e,f,g,h),x),IGtQ(n,C0),IGtQ(p,C0),EqQ(Subtract(Times(e,g),Times(C2,d,h)),C0)))),
IIntegrate(4758,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(Plus(d_,Times(e_DEFAULT,x_)),CN2),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_),Times(h_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(With(List(Set(u,IntHide(Times(Power(Plus(f,Times(g,x),Times(h,Sqr(x))),p),Power(Plus(d,Times(e,x)),CN2)),x))),Plus(Dist(Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n),u,x),Dist(Times(b,c,n),Int(SimplifyIntegrand(Times(u,Power(Plus(a,Times(b,ArcCos(Times(c,x)))),Subtract(n,C1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),x))),And(FreeQ(List(a,b,c,d,e,f,g,h),x),IGtQ(n,C0),IGtQ(p,C0),EqQ(Subtract(Times(e,g),Times(C2,d,h)),C0)))),
IIntegrate(4759,Int(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),$p("§px"),Power(Plus(d_,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Int(ExpandIntegrand(Times($s("§px"),Power(Plus(d,Times(e,x)),m),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n)),x),x),And(FreeQ(List(a,b,c,d,e),x),PolynomialQ($s("§px"),x),IGtQ(n,C0),IntegerQ(m)))),
IIntegrate(4760,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),$p("§px"),Power(Plus(d_,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Int(ExpandIntegrand(Times($s("§px"),Power(Plus(d,Times(e,x)),m),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n)),x),x),And(FreeQ(List(a,b,c,d,e),x),PolynomialQ($s("§px"),x),IGtQ(n,C0),IntegerQ(m))))
  );
}
