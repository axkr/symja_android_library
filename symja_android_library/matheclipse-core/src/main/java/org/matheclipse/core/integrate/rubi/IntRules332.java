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
class IntRules332 { 
  public static IAST RULES = List( 
IIntegrate(6641,Integrate(Times(ArcTanh(Times(a_DEFAULT,x_)),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_DEFAULT))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Dist(C1D2,Integrate(Times(Log(Plus(C1,Times(a,x))),Power(Plus(c,Times(d,Power(x,n))),CN1)),x),x),x),Simp(Dist(C1D2,Integrate(Times(Log(Subtract(C1,Times(a,x))),Power(Plus(c,Times(d,Power(x,n))),CN1)),x),x),x)),And(FreeQ(list(a,c,d),x),IntegerQ(n),Not(And(EqQ(n,C2),EqQ(Plus(Times(Sqr(a),c),d),C0)))))),
IIntegrate(6642,Integrate(Times(ArcCoth(Times(a_DEFAULT,x_)),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_DEFAULT))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Dist(C1D2,Integrate(Times(Log(Plus(C1,Power(Times(a,x),CN1))),Power(Plus(c,Times(d,Power(x,n))),CN1)),x),x),x),Simp(Dist(C1D2,Integrate(Times(Log(Subtract(C1,Power(Times(a,x),CN1))),Power(Plus(c,Times(d,Power(x,n))),CN1)),x),x),x)),And(FreeQ(list(a,c,d),x),IntegerQ(n),Not(And(EqQ(n,C2),EqQ(Plus(Times(Sqr(a),c),d),C0)))))),
IIntegrate(6643,Integrate(Times(ArcTanh(Times(c_DEFAULT,Power(x_,n_DEFAULT))),Log(Times(d_DEFAULT,Power(x_,m_DEFAULT))),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Simp(Dist(C1D2,Integrate(Times(Log(Times(d,Power(x,m))),Log(Plus(C1,Times(c,Power(x,n)))),Power(x,CN1)),x),x),x),Simp(Dist(C1D2,Integrate(Times(Log(Times(d,Power(x,m))),Log(Subtract(C1,Times(c,Power(x,n)))),Power(x,CN1)),x),x),x)),FreeQ(List(c,d,m,n),x))),
IIntegrate(6644,Integrate(Times(ArcCoth(Times(c_DEFAULT,Power(x_,n_DEFAULT))),Log(Times(d_DEFAULT,Power(x_,m_DEFAULT))),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Simp(Dist(C1D2,Integrate(Times(Log(Times(d,Power(x,m))),Log(Plus(C1,Power(Times(c,Power(x,n)),CN1))),Power(x,CN1)),x),x),x),Simp(Dist(C1D2,Integrate(Times(Log(Times(d,Power(x,m))),Log(Subtract(C1,Power(Times(c,Power(x,n)),CN1))),Power(x,CN1)),x),x),x)),FreeQ(List(c,d,m,n),x))),
IIntegrate(6645,Integrate(Times(Log(Times(d_DEFAULT,Power(x_,m_DEFAULT))),Plus(Times(ArcTanh(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT),a_),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Simp(Dist(a,Integrate(Times(Log(Times(d,Power(x,m))),Power(x,CN1)),x),x),x),Simp(Dist(b,Integrate(Times(Log(Times(d,Power(x,m))),ArcTanh(Times(c,Power(x,n))),Power(x,CN1)),x),x),x)),FreeQ(List(a,b,c,d,m,n),x))),
IIntegrate(6646,Integrate(Times(Log(Times(d_DEFAULT,Power(x_,m_DEFAULT))),Plus(Times(ArcCoth(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT),a_),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Simp(Dist(a,Integrate(Times(Log(Times(d,Power(x,m))),Power(x,CN1)),x),x),x),Simp(Dist(b,Integrate(Times(Log(Times(d,Power(x,m))),ArcCoth(Times(c,Power(x,n))),Power(x,CN1)),x),x),x)),FreeQ(List(a,b,c,d,m,n),x))),
IIntegrate(6647,Integrate(Times(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT)),Plus(d_DEFAULT,Times(Log(Plus(f_DEFAULT,Times(g_DEFAULT,Sqr(x_)))),e_DEFAULT))),x_Symbol),
    Condition(Plus(Simp(Times(x,Plus(d,Times(e,Log(Plus(f,Times(g,Sqr(x)))))),Plus(a,Times(b,ArcTanh(Times(c,x))))),x),Negate(Simp(Dist(Times(b,c),Integrate(Times(x,Plus(d,Times(e,Log(Plus(f,Times(g,Sqr(x)))))),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1)),x),x),x)),Negate(Simp(Dist(Times(C2,e,g),Integrate(Times(Sqr(x),Plus(a,Times(b,ArcTanh(Times(c,x)))),Power(Plus(f,Times(g,Sqr(x))),CN1)),x),x),x))),FreeQ(List(a,b,c,d,e,f,g),x))),
IIntegrate(6648,Integrate(Times(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT)),Plus(d_DEFAULT,Times(Log(Plus(f_DEFAULT,Times(g_DEFAULT,Sqr(x_)))),e_DEFAULT))),x_Symbol),
    Condition(Plus(Simp(Times(x,Plus(d,Times(e,Log(Plus(f,Times(g,Sqr(x)))))),Plus(a,Times(b,ArcCoth(Times(c,x))))),x),Negate(Simp(Dist(Times(b,c),Integrate(Times(x,Plus(d,Times(e,Log(Plus(f,Times(g,Sqr(x)))))),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1)),x),x),x)),Negate(Simp(Dist(Times(C2,e,g),Integrate(Times(Sqr(x),Plus(a,Times(b,ArcCoth(Times(c,x)))),Power(Plus(f,Times(g,Sqr(x))),CN1)),x),x),x))),FreeQ(List(a,b,c,d,e,f,g),x))),
IIntegrate(6649,Integrate(Times(ArcTanh(Times(c_DEFAULT,x_)),Log(Plus(f_DEFAULT,Times(g_DEFAULT,Sqr(x_)))),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Simp(Dist(Subtract(Subtract(Log(Plus(f,Times(g,Sqr(x)))),Log(Subtract(C1,Times(c,x)))),Log(Plus(C1,Times(c,x)))),Integrate(Times(ArcTanh(Times(c,x)),Power(x,CN1)),x),x),x),Negate(Simp(Dist(C1D2,Integrate(Times(Sqr(Log(Subtract(C1,Times(c,x)))),Power(x,CN1)),x),x),x)),Simp(Dist(C1D2,Integrate(Times(Sqr(Log(Plus(C1,Times(c,x)))),Power(x,CN1)),x),x),x)),And(FreeQ(list(c,f,g),x),EqQ(Plus(Times(Sqr(c),f),g),C0)))),
IIntegrate(6650,Integrate(Times(ArcCoth(Times(c_DEFAULT,x_)),Log(Plus(f_DEFAULT,Times(g_DEFAULT,Sqr(x_)))),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Simp(Dist(Subtract(Subtract(Subtract(Log(Plus(f,Times(g,Sqr(x)))),Log(Times(CN1,Sqr(c),Sqr(x)))),Log(Subtract(C1,Power(Times(c,x),CN1)))),Log(Plus(C1,Power(Times(c,x),CN1)))),Integrate(Times(ArcCoth(Times(c,x)),Power(x,CN1)),x),x),x),Integrate(Times(Log(Times(CN1,Sqr(c),Sqr(x))),ArcCoth(Times(c,x)),Power(x,CN1)),x),Simp(Dist(C1D2,Integrate(Times(Sqr(Log(Plus(C1,Power(Times(c,x),CN1)))),Power(x,CN1)),x),x),x),Negate(Simp(Dist(C1D2,Integrate(Times(Sqr(Log(Subtract(C1,Power(Times(c,x),CN1)))),Power(x,CN1)),x),x),x))),And(FreeQ(list(c,f,g),x),EqQ(Plus(Times(Sqr(c),f),g),C0)))),
IIntegrate(6651,Integrate(Times(Log(Plus(f_DEFAULT,Times(g_DEFAULT,Sqr(x_)))),Plus(Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT),a_),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Simp(Dist(a,Integrate(Times(Log(Plus(f,Times(g,Sqr(x)))),Power(x,CN1)),x),x),x),Simp(Dist(b,Integrate(Times(Log(Plus(f,Times(g,Sqr(x)))),ArcTanh(Times(c,x)),Power(x,CN1)),x),x),x)),FreeQ(List(a,b,c,f,g),x))),
IIntegrate(6652,Integrate(Times(Log(Plus(f_DEFAULT,Times(g_DEFAULT,Sqr(x_)))),Plus(Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT),a_),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Simp(Dist(a,Integrate(Times(Log(Plus(f,Times(g,Sqr(x)))),Power(x,CN1)),x),x),x),Simp(Dist(b,Integrate(Times(Log(Plus(f,Times(g,Sqr(x)))),ArcCoth(Times(c,x)),Power(x,CN1)),x),x),x)),FreeQ(List(a,b,c,f,g),x))),
IIntegrate(6653,Integrate(Times(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT)),Plus(Times(Log(Plus(f_DEFAULT,Times(g_DEFAULT,Sqr(x_)))),e_DEFAULT),d_),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Simp(Dist(d,Integrate(Times(Plus(a,Times(b,ArcTanh(Times(c,x)))),Power(x,CN1)),x),x),x),Simp(Dist(e,Integrate(Times(Log(Plus(f,Times(g,Sqr(x)))),Plus(a,Times(b,ArcTanh(Times(c,x)))),Power(x,CN1)),x),x),x)),FreeQ(List(a,b,c,d,e,f,g),x))),
IIntegrate(6654,Integrate(Times(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT)),Plus(Times(Log(Plus(f_DEFAULT,Times(g_DEFAULT,Sqr(x_)))),e_DEFAULT),d_),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Simp(Dist(d,Integrate(Times(Plus(a,Times(b,ArcCoth(Times(c,x)))),Power(x,CN1)),x),x),x),Simp(Dist(e,Integrate(Times(Log(Plus(f,Times(g,Sqr(x)))),Plus(a,Times(b,ArcCoth(Times(c,x)))),Power(x,CN1)),x),x),x)),FreeQ(List(a,b,c,d,e,f,g),x))),
IIntegrate(6655,Integrate(Times(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT)),Plus(d_DEFAULT,Times(Log(Plus(f_DEFAULT,Times(g_DEFAULT,Sqr(x_)))),e_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Plus(d,Times(e,Log(Plus(f,Times(g,Sqr(x)))))),Plus(a,Times(b,ArcTanh(Times(c,x)))),Power(Plus(m,C1),CN1)),x),Negate(Simp(Dist(Times(b,c,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Plus(d,Times(e,Log(Plus(f,Times(g,Sqr(x)))))),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1)),x),x),x)),Negate(Simp(Dist(Times(C2,e,g,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,C2)),Plus(a,Times(b,ArcTanh(Times(c,x)))),Power(Plus(f,Times(g,Sqr(x))),CN1)),x),x),x))),And(FreeQ(List(a,b,c,d,e,f,g),x),ILtQ(Times(C1D2,m),C0)))),
IIntegrate(6656,Integrate(Times(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT)),Plus(d_DEFAULT,Times(Log(Plus(f_DEFAULT,Times(g_DEFAULT,Sqr(x_)))),e_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Plus(d,Times(e,Log(Plus(f,Times(g,Sqr(x)))))),Plus(a,Times(b,ArcCoth(Times(c,x)))),Power(Plus(m,C1),CN1)),x),Negate(Simp(Dist(Times(b,c,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Plus(d,Times(e,Log(Plus(f,Times(g,Sqr(x)))))),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1)),x),x),x)),Negate(Simp(Dist(Times(C2,e,g,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,C2)),Plus(a,Times(b,ArcCoth(Times(c,x)))),Power(Plus(f,Times(g,Sqr(x))),CN1)),x),x),x))),And(FreeQ(List(a,b,c,d,e,f,g),x),ILtQ(Times(C1D2,m),C0)))),
IIntegrate(6657,Integrate(Times(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT)),Plus(d_DEFAULT,Times(Log(Plus(f_DEFAULT,Times(g_DEFAULT,Sqr(x_)))),e_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times(Power(x,m),Plus(d,Times(e,Log(Plus(f,Times(g,Sqr(x))))))),x))),Subtract(Simp(Dist(Plus(a,Times(b,ArcTanh(Times(c,x)))),u,x),x),Simp(Dist(Times(b,c),Integrate(ExpandIntegrand(Times(u,Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1)),x),x),x),x))),And(FreeQ(List(a,b,c,d,e,f,g),x),IGtQ(Times(C1D2,Plus(m,C1)),C0)))),
IIntegrate(6658,Integrate(Times(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT)),Plus(d_DEFAULT,Times(Log(Plus(f_DEFAULT,Times(g_DEFAULT,Sqr(x_)))),e_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times(Power(x,m),Plus(d,Times(e,Log(Plus(f,Times(g,Sqr(x))))))),x))),Subtract(Simp(Dist(Plus(a,Times(b,ArcCoth(Times(c,x)))),u,x),x),Simp(Dist(Times(b,c),Integrate(ExpandIntegrand(Times(u,Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1)),x),x),x),x))),And(FreeQ(List(a,b,c,d,e,f,g),x),IGtQ(Times(C1D2,Plus(m,C1)),C0)))),
IIntegrate(6659,Integrate(Times(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT)),Plus(d_DEFAULT,Times(Log(Plus(f_DEFAULT,Times(g_DEFAULT,Sqr(x_)))),e_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times(Power(x,m),Plus(a,Times(b,ArcTanh(Times(c,x))))),x))),Subtract(Simp(Dist(Plus(d,Times(e,Log(Plus(f,Times(g,Sqr(x)))))),u,x),x),Simp(Dist(Times(C2,e,g),Integrate(ExpandIntegrand(Times(x,u,Power(Plus(f,Times(g,Sqr(x))),CN1)),x),x),x),x))),And(FreeQ(List(a,b,c,d,e,f,g),x),IntegerQ(m),NeQ(m,CN1)))),
IIntegrate(6660,Integrate(Times(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT)),Plus(d_DEFAULT,Times(Log(Plus(f_DEFAULT,Times(g_DEFAULT,Sqr(x_)))),e_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times(Power(x,m),Plus(a,Times(b,ArcCoth(Times(c,x))))),x))),Subtract(Simp(Dist(Plus(d,Times(e,Log(Plus(f,Times(g,Sqr(x)))))),u,x),x),Simp(Dist(Times(C2,e,g),Integrate(ExpandIntegrand(Times(x,u,Power(Plus(f,Times(g,Sqr(x))),CN1)),x),x),x),x))),And(FreeQ(List(a,b,c,d,e,f,g),x),IntegerQ(m),NeQ(m,CN1))))
  );
}
