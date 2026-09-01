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
class IntRules319 { 
  public static IAST RULES = List( 
IIntegrate(6381,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Times(f_DEFAULT,x_),m_),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times(Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),n),Power(Plus(d,Times(e,Sqr(x))),CN1D2)),Times(Power(Times(f,x),m),Power(Plus(d,Times(e,Sqr(x))),Plus(p,C1D2))),x),x),And(FreeQ(List(a,b,c,d,e,f,m,n),x),EqQ(Plus(Times(Sqr(c),d),e),C0),IGtQ(Plus(p,C1D2),C0),Not(IGtQ(Times(C1D2,Plus(m,C1)),C0)),Or(EqQ(m,CN1),EqQ(m,CN2))))),
IIntegrate(6382,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Times(f_DEFAULT,x_),m_),Power(Plus($p("d1"),Times($p("e1",true),x_)),p_),Power(Plus($p("d2"),Times($p("e2",true),x_)),p_)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times(Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),n),Power(Times(Sqrt(Plus($s("d1"),Times($s("e1"),x))),Sqrt(Plus($s("d2"),Times($s("e2"),x)))),CN1)),Times(Power(Times(f,x),m),Power(Plus($s("d1"),Times($s("e1"),x)),Plus(p,C1D2)),Power(Plus($s("d2"),Times($s("e2"),x)),Plus(p,C1D2))),x),x),And(FreeQ(List(a,b,c,$s("d1"),$s("e1"),$s("d2"),$s("e2"),f,m,n),x),EqQ($s("e1"),Times(c,$s("d1"))),EqQ($s("e2"),Times(CN1,c,$s("d2"))),GtQ($s("d1"),C0),LtQ($s("d2"),C0),IGtQ(Plus(p,C1D2),C0),Not(IGtQ(Times(C1D2,Plus(m,C1)),C0)),Or(EqQ(m,CN1),EqQ(m,CN2))))),
IIntegrate(6383,Integrate(Times(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Times(f_DEFAULT,x_),m_DEFAULT),Plus(d_,Times(e_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Plus(Simp(Times(d,Power(Times(f,x),Plus(m,C1)),Plus(a,Times(b,ArcCosh(Times(c,x)))),Power(Times(f,Plus(m,C1)),CN1)),x),Simp(Times(e,Power(Times(f,x),Plus(m,C3)),Plus(a,Times(b,ArcCosh(Times(c,x)))),Power(Times(Power(f,C3),Plus(m,C3)),CN1)),x),Negate(Simp(Dist(Times(b,c,Power(Times(f,Plus(m,C1),Plus(m,C3)),CN1)),Integrate(Times(Power(Times(f,x),Plus(m,C1)),Plus(Times(d,Plus(m,C3)),Times(e,Plus(m,C1),Sqr(x))),Power(Times(Sqrt(Plus(C1,Times(c,x))),Sqrt(Plus(CN1,Times(c,x)))),CN1)),x),x),x))),And(FreeQ(List(a,b,c,d,e,f,m),x),NeQ(Plus(Times(Sqr(c),d),e),C0),NeQ(m,CN1),NeQ(m,CN3)))),
IIntegrate(6384,Integrate(Times(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),x_,Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(d,Times(e,Sqr(x))),Plus(p,C1)),Plus(a,Times(b,ArcCosh(Times(c,x)))),Power(Times(C2,e,Plus(p,C1)),CN1)),x),Simp(Dist(Times(b,c,Power(Times(C2,e,Plus(p,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,Sqr(x))),Plus(p,C1)),Power(Times(Sqrt(Plus(C1,Times(c,x))),Sqrt(Plus(CN1,Times(c,x)))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,p),x),NeQ(Plus(Times(Sqr(c),d),e),C0),NeQ(p,CN1)))),
IIntegrate(6385,Integrate(Times(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times(Power(Times(f,x),m),Power(Plus(d,Times(e,Sqr(x))),p)),x))),Subtract(Simp(Dist(Plus(a,Times(b,ArcCosh(Times(c,x)))),u,x),x),Simp(Dist(Times(b,c),Integrate(SimplifyIntegrand(Times(u,Power(Times(Sqrt(Plus(C1,Times(c,x))),Sqrt(Plus(CN1,Times(c,x)))),CN1)),x),x),x),x))),And(FreeQ(List(a,b,c,d,e,f,m),x),NeQ(Plus(Times(Sqr(c),d),e),C0),IntegerQ(p),Or(GtQ(p,C0),And(IGtQ(Times(C1D2,Plus(m,CN1)),C0),LeQ(Plus(m,p),C0)))))),
IIntegrate(6386,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),n),Times(Power(Times(f,x),m),Power(Plus(d,Times(e,Sqr(x))),p)),x),x),And(FreeQ(List(a,b,c,d,e,f),x),NeQ(Plus(Times(Sqr(c),d),e),C0),IGtQ(n,C0),IntegerQ(p),IntegerQ(m)))),
IIntegrate(6387,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(f,x),m),Power(Plus(d,Times(e,Sqr(x))),p),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),n)),x),FreeQ(List(a,b,c,d,e,f,m,n,p),x))),
IIntegrate(6388,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(Plus($p("d1"),Times($p("e1",true),x_)),p_DEFAULT),Power(Plus($p("d2"),Times($p("e2",true),x_)),p_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(f,x),m),Power(Plus($s("d1"),Times($s("e1"),x)),p),Power(Plus($s("d2"),Times($s("e2"),x)),p),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),n)),x),FreeQ(List(a,b,c,$s("d1"),$s("e1"),$s("d2"),$s("e2"),f,m,n,p),x))),
IIntegrate(6389,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Subst(Integrate(Times(Power(Plus(a,Times(b,x)),n),Sinh(x),Power(Plus(Times(c,d),Times(e,Cosh(x))),CN1)),x),x,ArcCosh(Times(c,x))),And(FreeQ(List(a,b,c,d,e),x),IGtQ(n,C0)))),
IIntegrate(6390,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),n),Power(Times(e,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,c,n,Power(Times(e,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),Plus(n,CN1)),Power(Times(Sqrt(Plus(CN1,Times(c,x))),Sqrt(Plus(C1,Times(c,x)))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,m),x),IGtQ(n,C0),NeQ(m,CN1)))),
IIntegrate(6391,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(Plus(d_,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times(Power(Plus(d,Times(e,x)),m),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),n)),x),x),And(FreeQ(List(a,b,c,d,e),x),IGtQ(m,C0),LtQ(n,CN1)))),
IIntegrate(6392,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(Power(c,Plus(m,C1)),CN1),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),n),Sinh(x),Power(Plus(Times(c,d),Times(e,Cosh(x))),m)),x),x,ArcCosh(Times(c,x))),x),x),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(m,C0)))),
IIntegrate(6393,Integrate(Times(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),$p("§px")),x_Symbol),
    Condition(With(list(Set(u,IntHide(ExpandExpression($s("§px"),x),x))),Subtract(Simp(Dist(Plus(a,Times(b,ArcCosh(Times(c,x)))),u,x),x),Simp(Dist(Times(b,c,Sqrt(Subtract(C1,Times(Sqr(c),Sqr(x)))),Power(Times(Sqrt(Plus(CN1,Times(c,x))),Sqrt(Plus(C1,Times(c,x)))),CN1)),Integrate(SimplifyIntegrand(Times(u,Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),x),x))),And(FreeQ(list(a,b,c),x),PolyQ($s("§px"),x)))),
IIntegrate(6394,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),$p("§px")),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times($s("§px"),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),n)),x),x),And(FreeQ(List(a,b,c,n),x),PolyQ($s("§px"),x)))),
IIntegrate(6395,Integrate(Times(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),$p("§px"),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times($s("§px"),Power(Plus(d,Times(e,x)),m)),x))),Subtract(Simp(Dist(Plus(a,Times(b,ArcCosh(Times(c,x)))),u,x),x),Simp(Dist(Times(b,c,Sqrt(Subtract(C1,Times(Sqr(c),Sqr(x)))),Power(Times(Sqrt(Plus(CN1,Times(c,x))),Sqrt(Plus(C1,Times(c,x)))),CN1)),Integrate(SimplifyIntegrand(Times(u,Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),x),x))),And(FreeQ(List(a,b,c,d,e,m),x),PolyQ($s("§px"),x)))),
IIntegrate(6396,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(Plus(d_,Times(e_DEFAULT,x_)),m_),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),p_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times(Power(Plus(f,Times(g,x)),p),Power(Plus(d,Times(e,x)),m)),x))),Subtract(Simp(Dist(Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),n),u,x),x),Simp(Dist(Times(b,c,n),Integrate(SimplifyIntegrand(Times(u,Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),Plus(n,CN1)),Power(Times(Sqrt(Plus(CN1,Times(c,x))),Sqrt(Plus(C1,Times(c,x)))),CN1)),x),x),x),x))),And(FreeQ(List(a,b,c,d,e,f,g),x),IGtQ(n,C0),IGtQ(p,C0),ILtQ(m,C0),LtQ(Plus(m,p,C1),C0)))),
IIntegrate(6397,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(Plus(d_,Times(e_DEFAULT,x_)),CN2),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_),Times(h_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times(Power(Plus(f,Times(g,x),Times(h,Sqr(x))),p),Power(Plus(d,Times(e,x)),CN2)),x))),Subtract(Simp(Dist(Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),n),u,x),x),Simp(Dist(Times(b,c,n),Integrate(SimplifyIntegrand(Times(u,Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),Plus(n,CN1)),Power(Times(Sqrt(Plus(CN1,Times(c,x))),Sqrt(Plus(C1,Times(c,x)))),CN1)),x),x),x),x))),And(FreeQ(List(a,b,c,d,e,f,g,h),x),IGtQ(n,C0),IGtQ(p,C0),EqQ(Subtract(Times(e,g),Times(C2,d,h)),C0)))),
IIntegrate(6398,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),$p("§px"),Power(Plus(d_,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times($s("§px"),Power(Plus(d,Times(e,x)),m),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),n)),x),x),And(FreeQ(List(a,b,c,d,e),x),PolyQ($s("§px"),x),IGtQ(n,C0),IntegerQ(m)))),
IIntegrate(6399,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(f_,Times(g_DEFAULT,x_)),m_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Dist(Times(Power(Negate(d),IntPart(p)),Power(Plus(d,Times(e,Sqr(x))),FracPart(p)),Power(Times(Power(Plus(CN1,Times(c,x)),FracPart(p)),Power(Plus(C1,Times(c,x)),FracPart(p))),CN1)),Integrate(Times(Power(Plus(f,Times(g,x)),m),Power(Plus(CN1,Times(c,x)),p),Power(Plus(C1,Times(c,x)),p),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),n)),x),x),x),And(FreeQ(List(a,b,c,d,e,f,g,n),x),EqQ(Plus(Times(Sqr(c),d),e),C0),IntegerQ(Plus(p,CN1D2)),IntegerQ(m)))),
IIntegrate(6400,Integrate(Times(Log(Times(h_DEFAULT,Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),m_DEFAULT))),Power(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Dist(Times(Power(Negate(d),IntPart(p)),Power(Plus(d,Times(e,Sqr(x))),FracPart(p)),Power(Times(Power(Plus(CN1,Times(c,x)),FracPart(p)),Power(Plus(C1,Times(c,x)),FracPart(p))),CN1)),Integrate(Times(Log(Times(h,Power(Plus(f,Times(g,x)),m))),Power(Plus(CN1,Times(c,x)),p),Power(Plus(C1,Times(c,x)),p),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),n)),x),x),x),And(FreeQ(List(a,b,c,d,e,f,g,h,m,n),x),EqQ(Plus(Times(Sqr(c),d),e),C0),IntegerQ(Plus(p,CN1D2)))))
  );
}
