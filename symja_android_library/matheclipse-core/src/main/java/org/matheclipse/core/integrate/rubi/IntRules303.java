package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class IntRules303 { 
  public static IAST RULES = List( 
IIntegrate(6061,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1),PolyLog(k_,u_)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(Power(Plus(a,Times(b,ArcCoth(Times(c,x)))),p),PolyLog(Plus(k,C1),u),Power(Times(C2,c,d),CN1)),x)),Dist(Times(C1D2,b,p),Integrate(Times(Power(Plus(a,Times(b,ArcCoth(Times(c,x)))),Subtract(p,C1)),PolyLog(Plus(k,C1),u),Power(Plus(d,Times(e,Sqr(x))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,k),x),IGtQ(p,C0),EqQ(Plus(Times(Sqr(c),d),e),C0),EqQ(Subtract(Sqr(u),Sqr(Subtract(C1,Times(C2,Power(Plus(C1,Times(c,x)),CN1))))),C0)))),
IIntegrate(6062,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1),PolyLog(k_,u_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(a,Times(b,ArcTanh(Times(c,x)))),p),PolyLog(Plus(k,C1),u),Power(Times(C2,c,d),CN1)),x),Dist(Times(C1D2,b,p),Integrate(Times(Power(Plus(a,Times(b,ArcTanh(Times(c,x)))),Subtract(p,C1)),PolyLog(Plus(k,C1),u),Power(Plus(d,Times(e,Sqr(x))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,k),x),IGtQ(p,C0),EqQ(Plus(Times(Sqr(c),d),e),C0),EqQ(Subtract(Sqr(u),Sqr(Subtract(C1,Times(C2,Power(Subtract(C1,Times(c,x)),CN1))))),C0)))),
IIntegrate(6063,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1),PolyLog(k_,u_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(a,Times(b,ArcCoth(Times(c,x)))),p),PolyLog(Plus(k,C1),u),Power(Times(C2,c,d),CN1)),x),Dist(Times(C1D2,b,p),Integrate(Times(Power(Plus(a,Times(b,ArcCoth(Times(c,x)))),Subtract(p,C1)),PolyLog(Plus(k,C1),u),Power(Plus(d,Times(e,Sqr(x))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,k),x),IGtQ(p,C0),EqQ(Plus(Times(Sqr(c),d),e),C0),EqQ(Subtract(Sqr(u),Sqr(Subtract(C1,Times(C2,Power(Subtract(C1,Times(c,x)),CN1))))),C0)))),
IIntegrate(6064,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT)),CN1),Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT)),CN1),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Simp(Times(Plus(Negate(Log(Plus(a,Times(b,ArcCoth(Times(c,x)))))),Log(Plus(a,Times(b,ArcTanh(Times(c,x)))))),Power(Times(Sqr(b),c,d,Subtract(ArcCoth(Times(c,x)),ArcTanh(Times(c,x)))),CN1)),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(Times(Sqr(c),d),e),C0)))),
IIntegrate(6065,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT)),m_DEFAULT),Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(a,Times(b,ArcCoth(Times(c,x)))),Plus(m,C1)),Power(Plus(a,Times(b,ArcTanh(Times(c,x)))),p),Power(Times(b,c,d,Plus(m,C1)),CN1)),x),Dist(Times(p,Power(Plus(m,C1),CN1)),Integrate(Times(Power(Plus(a,Times(b,ArcCoth(Times(c,x)))),Plus(m,C1)),Power(Plus(a,Times(b,ArcTanh(Times(c,x)))),Subtract(p,C1)),Power(Plus(d,Times(e,Sqr(x))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(Times(Sqr(c),d),e),C0),IGtQ(p,C0),IGeQ(m,p)))),
IIntegrate(6066,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT)),m_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(a,Times(b,ArcTanh(Times(c,x)))),Plus(m,C1)),Power(Plus(a,Times(b,ArcCoth(Times(c,x)))),p),Power(Times(b,c,d,Plus(m,C1)),CN1)),x),Dist(Times(p,Power(Plus(m,C1),CN1)),Integrate(Times(Power(Plus(a,Times(b,ArcTanh(Times(c,x)))),Plus(m,C1)),Power(Plus(a,Times(b,ArcCoth(Times(c,x)))),Subtract(p,C1)),Power(Plus(d,Times(e,Sqr(x))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(Times(Sqr(c),d),e),C0),IGtQ(p,C0),IGtQ(m,p)))),
IIntegrate(6067,Integrate(Times(ArcTanh(Times(a_DEFAULT,x_)),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_DEFAULT))),CN1)),x_Symbol),
    Condition(Subtract(Dist(C1D2,Integrate(Times(Log(Plus(C1,Times(a,x))),Power(Plus(c,Times(d,Power(x,n))),CN1)),x),x),Dist(C1D2,Integrate(Times(Log(Subtract(C1,Times(a,x))),Power(Plus(c,Times(d,Power(x,n))),CN1)),x),x)),And(FreeQ(List(a,c,d),x),IntegerQ(n),Not(And(EqQ(n,C2),EqQ(Plus(Times(Sqr(a),c),d),C0)))))),
IIntegrate(6068,Integrate(Times(ArcCoth(Times(a_DEFAULT,x_)),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_DEFAULT))),CN1)),x_Symbol),
    Condition(Subtract(Dist(C1D2,Integrate(Times(Log(Plus(C1,Power(Times(a,x),CN1))),Power(Plus(c,Times(d,Power(x,n))),CN1)),x),x),Dist(C1D2,Integrate(Times(Log(Subtract(C1,Power(Times(a,x),CN1))),Power(Plus(c,Times(d,Power(x,n))),CN1)),x),x)),And(FreeQ(List(a,c,d),x),IntegerQ(n),Not(And(EqQ(n,C2),EqQ(Plus(Times(Sqr(a),c),d),C0)))))),
IIntegrate(6069,Integrate(Times(ArcTanh(Times(c_DEFAULT,Power(x_,n_DEFAULT))),Log(Times(d_DEFAULT,Power(x_,m_DEFAULT))),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Dist(C1D2,Integrate(Times(Log(Times(d,Power(x,m))),Log(Plus(C1,Times(c,Power(x,n)))),Power(x,CN1)),x),x),Dist(C1D2,Integrate(Times(Log(Times(d,Power(x,m))),Log(Subtract(C1,Times(c,Power(x,n)))),Power(x,CN1)),x),x)),FreeQ(List(c,d,m,n),x))),
IIntegrate(6070,Integrate(Times(ArcCoth(Times(c_DEFAULT,Power(x_,n_DEFAULT))),Log(Times(d_DEFAULT,Power(x_,m_DEFAULT))),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Dist(C1D2,Integrate(Times(Log(Times(d,Power(x,m))),Log(Plus(C1,Power(Times(c,Power(x,n)),CN1))),Power(x,CN1)),x),x),Dist(C1D2,Integrate(Times(Log(Times(d,Power(x,m))),Log(Subtract(C1,Power(Times(c,Power(x,n)),CN1))),Power(x,CN1)),x),x)),FreeQ(List(c,d,m,n),x))),
IIntegrate(6071,Integrate(Times(Log(Times(d_DEFAULT,Power(x_,m_DEFAULT))),Plus(Times(ArcTanh(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT),a_),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Dist(a,Integrate(Times(Log(Times(d,Power(x,m))),Power(x,CN1)),x),x),Dist(b,Integrate(Times(Log(Times(d,Power(x,m))),ArcTanh(Times(c,Power(x,n))),Power(x,CN1)),x),x)),FreeQ(List(a,b,c,d,m,n),x))),
IIntegrate(6072,Integrate(Times(Log(Times(d_DEFAULT,Power(x_,m_DEFAULT))),Plus(Times(ArcCoth(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT),a_),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Dist(a,Integrate(Times(Log(Times(d,Power(x,m))),Power(x,CN1)),x),x),Dist(b,Integrate(Times(Log(Times(d,Power(x,m))),ArcCoth(Times(c,Power(x,n))),Power(x,CN1)),x),x)),FreeQ(List(a,b,c,d,m,n),x))),
IIntegrate(6073,Integrate(Times(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT)),Plus(d_DEFAULT,Times(Log(Plus(f_DEFAULT,Times(g_DEFAULT,Sqr(x_)))),e_DEFAULT))),x_Symbol),
    Condition(Plus(Simp(Times(x,Plus(d,Times(e,Log(Plus(f,Times(g,Sqr(x)))))),Plus(a,Times(b,ArcTanh(Times(c,x))))),x),Negate(Dist(Times(b,c),Integrate(Times(x,Plus(d,Times(e,Log(Plus(f,Times(g,Sqr(x)))))),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1)),x),x)),Negate(Dist(Times(C2,e,g),Integrate(Times(Sqr(x),Plus(a,Times(b,ArcTanh(Times(c,x)))),Power(Plus(f,Times(g,Sqr(x))),CN1)),x),x))),FreeQ(List(a,b,c,d,e,f,g),x))),
IIntegrate(6074,Integrate(Times(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT)),Plus(d_DEFAULT,Times(Log(Plus(f_DEFAULT,Times(g_DEFAULT,Sqr(x_)))),e_DEFAULT))),x_Symbol),
    Condition(Plus(Simp(Times(x,Plus(d,Times(e,Log(Plus(f,Times(g,Sqr(x)))))),Plus(a,Times(b,ArcCoth(Times(c,x))))),x),Negate(Dist(Times(b,c),Integrate(Times(x,Plus(d,Times(e,Log(Plus(f,Times(g,Sqr(x)))))),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1)),x),x)),Negate(Dist(Times(C2,e,g),Integrate(Times(Sqr(x),Plus(a,Times(b,ArcCoth(Times(c,x)))),Power(Plus(f,Times(g,Sqr(x))),CN1)),x),x))),FreeQ(List(a,b,c,d,e,f,g),x))),
IIntegrate(6075,Integrate(Times(ArcTanh(Times(c_DEFAULT,x_)),Log(Plus(f_DEFAULT,Times(g_DEFAULT,Sqr(x_)))),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Dist(Subtract(Subtract(Log(Plus(f,Times(g,Sqr(x)))),Log(Subtract(C1,Times(c,x)))),Log(Plus(C1,Times(c,x)))),Integrate(Times(ArcTanh(Times(c,x)),Power(x,CN1)),x),x),Negate(Dist(C1D2,Integrate(Times(Sqr(Log(Subtract(C1,Times(c,x)))),Power(x,CN1)),x),x)),Dist(C1D2,Integrate(Times(Sqr(Log(Plus(C1,Times(c,x)))),Power(x,CN1)),x),x)),And(FreeQ(List(c,f,g),x),EqQ(Plus(Times(Sqr(c),f),g),C0)))),
IIntegrate(6076,Integrate(Times(ArcCoth(Times(c_DEFAULT,x_)),Log(Plus(f_DEFAULT,Times(g_DEFAULT,Sqr(x_)))),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Dist(Subtract(Subtract(Subtract(Log(Plus(f,Times(g,Sqr(x)))),Log(Times(CN1,Sqr(c),Sqr(x)))),Log(Subtract(C1,Power(Times(c,x),CN1)))),Log(Plus(C1,Power(Times(c,x),CN1)))),Integrate(Times(ArcCoth(Times(c,x)),Power(x,CN1)),x),x),Negate(Dist(C1D2,Integrate(Times(Sqr(Log(Subtract(C1,Power(Times(c,x),CN1)))),Power(x,CN1)),x),x)),Dist(C1D2,Integrate(Times(Sqr(Log(Plus(C1,Power(Times(c,x),CN1)))),Power(x,CN1)),x),x),Integrate(Times(Log(Times(CN1,Sqr(c),Sqr(x))),ArcCoth(Times(c,x)),Power(x,CN1)),x)),And(FreeQ(List(c,f,g),x),EqQ(Plus(Times(Sqr(c),f),g),C0)))),
IIntegrate(6077,Integrate(Times(Log(Plus(f_DEFAULT,Times(g_DEFAULT,Sqr(x_)))),Plus(Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT),a_),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Dist(a,Integrate(Times(Log(Plus(f,Times(g,Sqr(x)))),Power(x,CN1)),x),x),Dist(b,Integrate(Times(Log(Plus(f,Times(g,Sqr(x)))),ArcTanh(Times(c,x)),Power(x,CN1)),x),x)),FreeQ(List(a,b,c,f,g),x))),
IIntegrate(6078,Integrate(Times(Log(Plus(f_DEFAULT,Times(g_DEFAULT,Sqr(x_)))),Plus(Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT),a_),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Dist(a,Integrate(Times(Log(Plus(f,Times(g,Sqr(x)))),Power(x,CN1)),x),x),Dist(b,Integrate(Times(Log(Plus(f,Times(g,Sqr(x)))),ArcCoth(Times(c,x)),Power(x,CN1)),x),x)),FreeQ(List(a,b,c,f,g),x))),
IIntegrate(6079,Integrate(Times(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT)),Plus(Times(Log(Plus(f_DEFAULT,Times(g_DEFAULT,Sqr(x_)))),e_DEFAULT),d_),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Dist(d,Integrate(Times(Plus(a,Times(b,ArcTanh(Times(c,x)))),Power(x,CN1)),x),x),Dist(e,Integrate(Times(Log(Plus(f,Times(g,Sqr(x)))),Plus(a,Times(b,ArcTanh(Times(c,x)))),Power(x,CN1)),x),x)),FreeQ(List(a,b,c,d,e,f,g),x))),
IIntegrate(6080,Integrate(Times(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT)),Plus(Times(Log(Plus(f_DEFAULT,Times(g_DEFAULT,Sqr(x_)))),e_DEFAULT),d_),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Dist(d,Integrate(Times(Plus(a,Times(b,ArcCoth(Times(c,x)))),Power(x,CN1)),x),x),Dist(e,Integrate(Times(Log(Plus(f,Times(g,Sqr(x)))),Plus(a,Times(b,ArcCoth(Times(c,x)))),Power(x,CN1)),x),x)),FreeQ(List(a,b,c,d,e,f,g),x)))
  );
}
