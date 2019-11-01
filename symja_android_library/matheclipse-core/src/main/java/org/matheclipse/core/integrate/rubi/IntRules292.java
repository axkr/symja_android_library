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
public class IntRules292 { 
  public static IAST RULES = List( 
IIntegrate(5841,Int(Times(Log(Times(h_DEFAULT,Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),m_DEFAULT))),Power(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Dist(Times(Power(Negate(d),IntPart(p)),Power(Plus(d,Times(e,Sqr(x))),FracPart(p)),Power(Times(Power(Plus(C1,Times(c,x)),FracPart(p)),Power(Plus(CN1,Times(c,x)),FracPart(p))),CN1)),Int(Times(Log(Times(h,Power(Plus(f,Times(g,x)),m))),Power(Plus(C1,Times(c,x)),p),Power(Plus(CN1,Times(c,x)),p),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),n)),x),x),And(FreeQ(List(a,b,c,d,e,f,g,h,m,n),x),EqQ(Plus(Times(Sqr(c),d),e),C0),IntegerQ(Subtract(p,C1D2))))),
IIntegrate(5842,Int(Times(Log(Times(h_DEFAULT,Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),m_DEFAULT))),Power(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus($p("d1"),Times($p("e1",true),x_)),p_),Power(Plus($p("d2"),Times($p("e2",true),x_)),p_)),x_Symbol),
    Condition(Dist(Times(Power(Times(CN1,$s("d1"),$s("d2")),IntPart(p)),Power(Plus($s("d1"),Times($s("e1"),x)),FracPart(p)),Power(Plus($s("d2"),Times($s("e2"),x)),FracPart(p)),Power(Times(Power(Plus(C1,Times(c,x)),FracPart(p)),Power(Plus(CN1,Times(c,x)),FracPart(p))),CN1)),Int(Times(Log(Times(h,Power(Plus(f,Times(g,x)),m))),Power(Plus(C1,Times(c,x)),p),Power(Plus(CN1,Times(c,x)),p),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),n)),x),x),And(FreeQ(List(a,b,c,$s("d1"),$s("e1"),$s("d2"),$s("e2"),f,g,h,m,n),x),EqQ(Subtract($s("e1"),Times(c,$s("d1"))),C0),EqQ(Plus($s("e2"),Times(c,$s("d2"))),C0),IntegerQ(Subtract(p,C1D2)),Not(And(GtQ($s("d1"),C0),LtQ($s("d2"),C0)))))),
IIntegrate(5843,Int(Times(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,x_)),m_),Power(Plus(f_,Times(g_DEFAULT,x_)),m_)),x_Symbol),
    Condition(With(List(Set(u,IntHide(Times(Power(Plus(d,Times(e,x)),m),Power(Plus(f,Times(g,x)),m)),x))),Subtract(Dist(Plus(a,Times(b,ArcSinh(Times(c,x)))),u,x),Dist(Times(b,c),Int(Dist(Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1D2),u,x),x),x))),And(FreeQ(List(a,b,c,d,e,f,g),x),ILtQ(Plus(m,C1D2),C0)))),
IIntegrate(5844,Int(Times(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,x_)),m_),Power(Plus(f_,Times(g_DEFAULT,x_)),m_)),x_Symbol),
    Condition(With(List(Set(u,IntHide(Times(Power(Plus(d,Times(e,x)),m),Power(Plus(f,Times(g,x)),m)),x))),Subtract(Dist(Plus(a,Times(b,ArcCosh(Times(c,x)))),u,x),Dist(Times(b,c),Int(Dist(Power(Times(Sqrt(Plus(C1,Times(c,x))),Sqrt(Plus(CN1,Times(c,x)))),CN1),u,x),x),x))),And(FreeQ(List(a,b,c,d,e,f,g),x),ILtQ(Plus(m,C1D2),C0)))),
IIntegrate(5845,Int(Times(Power(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,x_)),m_DEFAULT),Power(Plus(f_,Times(g_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Int(ExpandIntegrand(Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),n),Times(Power(Plus(d,Times(e,x)),m),Power(Plus(f,Times(g,x)),m)),x),x),And(FreeQ(List(a,b,c,d,e,f,g,n),x),IntegerQ(m)))),
IIntegrate(5846,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,x_)),m_DEFAULT),Power(Plus(f_,Times(g_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Int(ExpandIntegrand(Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),n),Times(Power(Plus(d,Times(e,x)),m),Power(Plus(f,Times(g,x)),m)),x),x),And(FreeQ(List(a,b,c,d,e,f,g,n),x),IntegerQ(m)))),
IIntegrate(5847,Int(Times(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),u_),x_Symbol),
    Condition(With(List(Set(v,IntHide(u,x))),Condition(Subtract(Dist(Plus(a,Times(b,ArcSinh(Times(c,x)))),v,x),Dist(Times(b,c),Int(SimplifyIntegrand(Times(v,Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),x)),InverseFunctionFreeQ(v,x))),FreeQ(List(a,b,c),x))),
IIntegrate(5848,Int(Times(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),u_),x_Symbol),
    Condition(With(List(Set(v,IntHide(u,x))),Condition(Subtract(Dist(Plus(a,Times(b,ArcCosh(Times(c,x)))),v,x),Dist(Times(b,c,Sqrt(Subtract(C1,Times(Sqr(c),Sqr(x)))),Power(Times(Sqrt(Plus(CN1,Times(c,x))),Sqrt(Plus(C1,Times(c,x)))),CN1)),Int(SimplifyIntegrand(Times(v,Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),x)),InverseFunctionFreeQ(v,x))),FreeQ(List(a,b,c),x))),
IIntegrate(5849,Int(Times(Power(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),$p("§px"),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(With(List(Set(u,ExpandIntegrand(Times($s("§px"),Power(Plus(d,Times(e,Sqr(x))),p),Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),n)),x))),Condition(Int(u,x),SumQ(u))),And(FreeQ(List(a,b,c,d,e,n),x),PolynomialQ($s("§px"),x),EqQ(e,Times(Sqr(c),d)),IntegerQ(Subtract(p,C1D2))))),
IIntegrate(5850,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),$p("§px"),Power(Plus($p("d1"),Times($p("e1",true),x_)),p_),Power(Plus($p("d2"),Times($p("e2",true),x_)),p_)),x_Symbol),
    Condition(With(List(Set(u,ExpandIntegrand(Times($s("§px"),Power(Plus($s("d1"),Times($s("e1"),x)),p),Power(Plus($s("d2"),Times($s("e2"),x)),p),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),n)),x))),Condition(Int(u,x),SumQ(u))),And(FreeQ(List(a,b,c,$s("d1"),$s("e1"),$s("d2"),$s("e2"),n),x),PolynomialQ($s("§px"),x),EqQ(Subtract($s("e1"),Times(c,$s("d1"))),C0),EqQ(Plus($s("e2"),Times(c,$s("d2"))),C0),IntegerQ(Subtract(p,C1D2))))),
IIntegrate(5851,Int(Times(Power(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),$p("§px",true),Power(Plus(f_,Times(g_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_))),m_DEFAULT)),x_Symbol),
    Condition(With(List(Set(u,ExpandIntegrand(Times($s("§px"),Power(Plus(f,Times(g,Power(Plus(d,Times(e,Sqr(x))),p))),m),Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),n)),x))),Condition(Int(u,x),SumQ(u))),And(FreeQ(List(a,b,c,d,e,f,g),x),PolynomialQ($s("§px"),x),EqQ(e,Times(Sqr(c),d)),IGtQ(Plus(p,C1D2),C0),IntegersQ(m,n)))),
IIntegrate(5852,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),$p("§px",true),Power(Plus(f_,Times(g_DEFAULT,Power(Plus($p("d1"),Times($p("e1",true),x_)),p_),Power(Plus($p("d2"),Times($p("e2",true),x_)),p_))),m_DEFAULT)),x_Symbol),
    Condition(With(List(Set(u,ExpandIntegrand(Times($s("§px"),Power(Plus(f,Times(g,Power(Plus($s("d1"),Times($s("e1"),x)),p),Power(Plus($s("d2"),Times($s("e2"),x)),p))),m),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),n)),x))),Condition(Int(u,x),SumQ(u))),And(FreeQ(List(a,b,c,$s("d1"),$s("e1"),$s("d2"),$s("e2"),f,g),x),PolynomialQ($s("§px"),x),EqQ(Subtract($s("e1"),Times(c,$s("d1"))),C0),EqQ(Plus($s("e2"),Times(c,$s("d2"))),C0),IGtQ(Plus(p,C1D2),C0),IntegersQ(m,n)))),
IIntegrate(5853,Int(Times(Power(ArcSinh(Times(c_DEFAULT,x_)),n_DEFAULT),$p("§rfx")),x_Symbol),
    Condition(With(List(Set(u,ExpandIntegrand(Power(ArcSinh(Times(c,x)),n),$s("§rfx"),x))),Condition(Int(u,x),SumQ(u))),And(FreeQ(c,x),RationalFunctionQ($s("§rfx"),x),IGtQ(n,C0)))),
IIntegrate(5854,Int(Times(Power(ArcCosh(Times(c_DEFAULT,x_)),n_DEFAULT),$p("§rfx")),x_Symbol),
    Condition(With(List(Set(u,ExpandIntegrand(Power(ArcCosh(Times(c,x)),n),$s("§rfx"),x))),Condition(Int(u,x),SumQ(u))),And(FreeQ(c,x),RationalFunctionQ($s("§rfx"),x),IGtQ(n,C0)))),
IIntegrate(5855,Int(Times(Power(Plus(Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT),a_),n_DEFAULT),$p("§rfx")),x_Symbol),
    Condition(Int(ExpandIntegrand(Times($s("§rfx"),Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),n)),x),x),And(FreeQ(List(a,b,c),x),RationalFunctionQ($s("§rfx"),x),IGtQ(n,C0)))),
IIntegrate(5856,Int(Times(Power(Plus(Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT),a_),n_DEFAULT),$p("§rfx")),x_Symbol),
    Condition(Int(ExpandIntegrand(Times($s("§rfx"),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),n)),x),x),And(FreeQ(List(a,b,c),x),RationalFunctionQ($s("§rfx"),x),IGtQ(n,C0)))),
IIntegrate(5857,Int(Times(Power(ArcSinh(Times(c_DEFAULT,x_)),n_DEFAULT),$p("§rfx"),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(With(List(Set(u,ExpandIntegrand(Times(Power(Plus(d,Times(e,Sqr(x))),p),Power(ArcSinh(Times(c,x)),n)),$s("§rfx"),x))),Condition(Int(u,x),SumQ(u))),And(FreeQ(List(c,d,e),x),RationalFunctionQ($s("§rfx"),x),IGtQ(n,C0),EqQ(e,Times(Sqr(c),d)),IntegerQ(Subtract(p,C1D2))))),
IIntegrate(5858,Int(Times(Power(ArcCosh(Times(c_DEFAULT,x_)),n_DEFAULT),$p("§rfx"),Power(Plus($p("d1"),Times($p("e1",true),x_)),p_),Power(Plus($p("d2"),Times($p("e2",true),x_)),p_)),x_Symbol),
    Condition(With(List(Set(u,ExpandIntegrand(Times(Power(Plus($s("d1"),Times($s("e1"),x)),p),Power(Plus($s("d2"),Times($s("e2"),x)),p),Power(ArcCosh(Times(c,x)),n)),$s("§rfx"),x))),Condition(Int(u,x),SumQ(u))),And(FreeQ(List(c,$s("d1"),$s("e1"),$s("d2"),$s("e2")),x),RationalFunctionQ($s("§rfx"),x),IGtQ(n,C0),EqQ(Subtract($s("e1"),Times(c,$s("d1"))),C0),EqQ(Plus($s("e2"),Times(c,$s("d2"))),C0),IntegerQ(Subtract(p,C1D2))))),
IIntegrate(5859,Int(Times(Power(Plus(Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT),a_),n_DEFAULT),$p("§rfx"),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Int(ExpandIntegrand(Power(Plus(d,Times(e,Sqr(x))),p),Times($s("§rfx"),Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),n)),x),x),And(FreeQ(List(a,b,c,d,e),x),RationalFunctionQ($s("§rfx"),x),IGtQ(n,C0),EqQ(e,Times(Sqr(c),d)),IntegerQ(Subtract(p,C1D2))))),
IIntegrate(5860,Int(Times(Power(Plus(Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT),a_),n_DEFAULT),$p("§rfx"),Power(Plus($p("d1"),Times($p("e1",true),x_)),p_),Power(Plus($p("d2"),Times($p("e2",true),x_)),p_)),x_Symbol),
    Condition(Int(ExpandIntegrand(Times(Power(Plus($s("d1"),Times($s("e1"),x)),p),Power(Plus($s("d2"),Times($s("e2"),x)),p)),Times($s("§rfx"),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),n)),x),x),And(FreeQ(List(a,b,c,$s("d1"),$s("e1"),$s("d2"),$s("e2")),x),RationalFunctionQ($s("§rfx"),x),IGtQ(n,C0),EqQ(Subtract($s("e1"),Times(c,$s("d1"))),C0),EqQ(Plus($s("e2"),Times(c,$s("d2"))),C0),IntegerQ(Subtract(p,C1D2)))))
  );
}
