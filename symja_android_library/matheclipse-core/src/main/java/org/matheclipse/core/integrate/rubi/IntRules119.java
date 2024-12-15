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
class IntRules119 { 
  public static IAST RULES = List( 
IIntegrate(2381,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),PolyLog(k_,Times(e_DEFAULT,Power(x_,q_DEFAULT)))),x_Symbol),
    Condition(Plus(Negate(Simp(Times(b,n,x,PolyLog(k,Times(e,Power(x,q)))),x)),Negate(Dist(q,Integrate(Times(PolyLog(Subtract(k,C1),Times(e,Power(x,q))),Plus(a,Times(b,Log(Times(c,Power(x,n)))))),x),x)),Dist(Times(b,n,q),Integrate(PolyLog(Subtract(k,C1),Times(e,Power(x,q))),x),x),Simp(Times(x,PolyLog(k,Times(e,Power(x,q))),Plus(a,Times(b,Log(Times(c,Power(x,n)))))),x)),And(FreeQ(List(a,b,c,e,n,q),x),IGtQ(k,C0)))),
IIntegrate(2382,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),PolyLog(k_,Times(e_DEFAULT,Power(x_,q_DEFAULT)))),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p),PolyLog(k,Times(e,Power(x,q)))),x),FreeQ(List(a,b,c,e,n,p,q),x))),
IIntegrate(2383,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(x_,CN1),PolyLog(k_,Times(e_DEFAULT,Power(x_,q_DEFAULT)))),x_Symbol),
    Condition(Subtract(Simp(Times(PolyLog(Plus(k,C1),Times(e,Power(x,q))),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p),Power(q,CN1)),x),Dist(Times(b,n,p,Power(q,CN1)),Integrate(Times(PolyLog(Plus(k,C1),Times(e,Power(x,q))),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),Subtract(p,C1)),Power(x,CN1)),x),x)),And(FreeQ(List(a,b,c,e,k,n,q),x),GtQ(p,C0)))),
IIntegrate(2384,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(x_,CN1),PolyLog(k_,Times(e_DEFAULT,Power(x_,q_DEFAULT)))),x_Symbol),
    Condition(Subtract(Simp(Times(PolyLog(k,Times(e,Power(x,q))),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),CN1)),x),Dist(Times(q,Power(Times(b,n,Plus(p,C1)),CN1)),Integrate(Times(PolyLog(Subtract(k,C1),Times(e,Power(x,q))),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),Plus(p,C1)),Power(x,CN1)),x),x)),And(FreeQ(List(a,b,c,e,k,n,q),x),LtQ(p,CN1)))),
IIntegrate(2385,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),Power(Times(d_DEFAULT,x_),m_DEFAULT),PolyLog(k_,Times(e_DEFAULT,Power(x_,q_DEFAULT)))),x_Symbol),
    Condition(Plus(Negate(Simp(Times(b,n,Power(Times(d,x),Plus(m,C1)),PolyLog(k,Times(e,Power(x,q))),Power(Times(d,Sqr(Plus(m,C1))),CN1)),x)),Negate(Dist(Times(q,Power(Plus(m,C1),CN1)),Integrate(Times(Power(Times(d,x),m),PolyLog(Subtract(k,C1),Times(e,Power(x,q))),Plus(a,Times(b,Log(Times(c,Power(x,n)))))),x),x)),Dist(Times(b,n,q,Power(Plus(m,C1),CN2)),Integrate(Times(Power(Times(d,x),m),PolyLog(Subtract(k,C1),Times(e,Power(x,q)))),x),x),Simp(Times(Power(Times(d,x),Plus(m,C1)),PolyLog(k,Times(e,Power(x,q))),Plus(a,Times(b,Log(Times(c,Power(x,n))))),Power(Times(d,Plus(m,C1)),CN1)),x)),And(FreeQ(List(a,b,c,d,e,m,n,q),x),IGtQ(k,C0)))),
IIntegrate(2386,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(Times(d_DEFAULT,x_),m_DEFAULT),PolyLog(k_,Times(e_DEFAULT,Power(x_,q_DEFAULT)))),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(d,x),m),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p),PolyLog(k,Times(e,Power(x,q)))),x),FreeQ(List(a,b,c,d,e,m,n,p,q),x))),
IIntegrate(2387,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),$p("§px",true),Power($(F_,Times(d_DEFAULT,Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times($s("§px"),Power(F(Times(d,Plus(e,Times(f,x)))),m)),x))),Subtract(Dist(Plus(a,Times(b,Log(Times(c,Power(x,n))))),u,x),Dist(Times(b,n),Integrate(Dist(Power(x,CN1),u,x),x),x))),And(FreeQ(List(a,b,c,d,e,f,n),x),PolynomialQ($s("§px"),x),IGtQ(m,C0),MemberQ(List(ArcSin,ArcCos,ArcSinh,ArcCosh),FSymbol)))),
IIntegrate(2388,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),$p("§px",true),$(F_,Times(d_DEFAULT,Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times($s("§px"),F(Times(d,Plus(e,Times(f,x))))),x))),Subtract(Dist(Plus(a,Times(b,Log(Times(c,Power(x,n))))),u,x),Dist(Times(b,n),Integrate(Dist(Power(x,CN1),u,x),x),x))),And(FreeQ(List(a,b,c,d,e,f,n),x),PolynomialQ($s("§px"),x),MemberQ(List(ArcTan,ArcCot,ArcTanh,ArcCoth),FSymbol)))),
IIntegrate(2389,Integrate(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Dist(Power(e,CN1),Subst(Integrate(Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p),x),x,Plus(d,Times(e,x))),x),FreeQ(List(a,b,c,d,e,n,p),x))),
IIntegrate(2390,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(Plus(f_,Times(g_DEFAULT,x_)),q_DEFAULT)),x_Symbol),
    Condition(Dist(Power(e,CN1),Subst(Integrate(Times(Power(Times(f,x,Power(d,CN1)),q),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p)),x),x,Plus(d,Times(e,x))),x),And(FreeQ(List(a,b,c,d,e,f,g,n,p,q),x),EqQ(Subtract(Times(e,f),Times(d,g)),C0)))),
IIntegrate(2391,Integrate(Times(Log(Times(c_DEFAULT,Plus(d_,Times(e_DEFAULT,Power(x_,n_DEFAULT))))),Power(x_,CN1)),x_Symbol),
    Condition(Negate(Simp(Times(PolyLog(C2,Times(CN1,c,e,Power(x,n))),Power(n,CN1)),x)),And(FreeQ(List(c,d,e,n),x),EqQ(Times(c,d),C1)))),
IIntegrate(2392,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Plus(d_,Times(e_DEFAULT,x_)))),b_DEFAULT)),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Simp(Times(Plus(a,Times(b,Log(Times(c,d)))),Log(x)),x),Dist(b,Integrate(Times(Log(Plus(C1,Times(e,x,Power(d,CN1)))),Power(x,CN1)),x),x)),And(FreeQ(List(a,b,c,d,e),x),GtQ(Times(c,d),C0)))),
IIntegrate(2393,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Plus(d_,Times(e_DEFAULT,x_)))),b_DEFAULT)),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Dist(Power(g,CN1),Subst(Integrate(Times(Plus(a,Times(b,Log(Plus(C1,Times(c,e,x,Power(g,CN1)))))),Power(x,CN1)),x),x,Plus(f,Times(g,x))),x),And(FreeQ(List(a,b,c,d,e,f,g),x),NeQ(Subtract(Times(e,f),Times(d,g)),C0),EqQ(Plus(g,Times(c,Subtract(Times(e,f),Times(d,g)))),C0)))),
IIntegrate(2394,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT)),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(Log(Times(e,Plus(f,Times(g,x)),Power(Subtract(Times(e,f),Times(d,g)),CN1))),Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,x)),n))))),Power(g,CN1)),x),Dist(Times(b,e,n,Power(g,CN1)),Integrate(Times(Log(Times(e,Plus(f,Times(g,x)),Power(Subtract(Times(e,f),Times(d,g)),CN1))),Power(Plus(d,Times(e,x)),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,f,g,n),x),NeQ(Subtract(Times(e,f),Times(d,g)),C0)))),
IIntegrate(2395,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT)),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),q_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(f,Times(g,x)),Plus(q,C1)),Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,x)),n))))),Power(Times(g,Plus(q,C1)),CN1)),x),Dist(Times(b,e,n,Power(Times(g,Plus(q,C1)),CN1)),Integrate(Times(Power(Plus(f,Times(g,x)),Plus(q,C1)),Power(Plus(d,Times(e,x)),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,f,g,n,q),x),NeQ(Subtract(Times(e,f),Times(d,g)),C0),NeQ(q,CN1)))),
IIntegrate(2396,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT)),p_),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(Log(Times(e,Plus(f,Times(g,x)),Power(Subtract(Times(e,f),Times(d,g)),CN1))),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,x)),n))))),p),Power(g,CN1)),x),Dist(Times(b,e,n,p,Power(g,CN1)),Integrate(Times(Log(Times(e,Plus(f,Times(g,x)),Power(Subtract(Times(e,f),Times(d,g)),CN1))),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,x)),n))))),Subtract(p,C1)),Power(Plus(d,Times(e,x)),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,f,g,n,p),x),NeQ(Subtract(Times(e,f),Times(d,g)),C0),IGtQ(p,C1)))),
IIntegrate(2397,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT)),p_),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),CN2)),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(d,Times(e,x)),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,x)),n))))),p),Power(Times(Subtract(Times(e,f),Times(d,g)),Plus(f,Times(g,x))),CN1)),x),Dist(Times(b,e,n,p,Power(Subtract(Times(e,f),Times(d,g)),CN1)),Integrate(Times(Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,x)),n))))),Subtract(p,C1)),Power(Plus(f,Times(g,x)),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,f,g,n),x),NeQ(Subtract(Times(e,f),Times(d,g)),C0),GtQ(p,C0)))),
IIntegrate(2398,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT)),p_),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),q_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(f,Times(g,x)),Plus(q,C1)),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,x)),n))))),p),Power(Times(g,Plus(q,C1)),CN1)),x),Dist(Times(b,e,n,p,Power(Times(g,Plus(q,C1)),CN1)),Integrate(Times(Power(Plus(f,Times(g,x)),Plus(q,C1)),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,x)),n))))),Subtract(p,C1)),Power(Plus(d,Times(e,x)),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,f,g,n,q),x),NeQ(Subtract(Times(e,f),Times(d,g)),C0),GtQ(p,C0),NeQ(q,CN1),IntegersQ(Times(C2,p),Times(C2,q)),Or(Not(IGtQ(q,C0)),And(EqQ(p,C2),NeQ(q,C1)))))),
IIntegrate(2399,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT)),CN1),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),q_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times(Power(Plus(f,Times(g,x)),q),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,x)),n))))),CN1)),x),x),And(FreeQ(List(a,b,c,d,e,f,g,n),x),NeQ(Subtract(Times(e,f),Times(d,g)),C0),IGtQ(q,C0)))),
IIntegrate(2400,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT)),p_),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),q_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Plus(d,Times(e,x)),Power(Plus(f,Times(g,x)),q),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,x)),n))))),Plus(p,C1)),Power(Times(b,e,n,Plus(p,C1)),CN1)),x),Negate(Dist(Times(Plus(q,C1),Power(Times(b,n,Plus(p,C1)),CN1)),Integrate(Times(Power(Plus(f,Times(g,x)),q),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,x)),n))))),Plus(p,C1))),x),x)),Dist(Times(q,Subtract(Times(e,f),Times(d,g)),Power(Times(b,e,n,Plus(p,C1)),CN1)),Integrate(Times(Power(Plus(f,Times(g,x)),Subtract(q,C1)),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,x)),n))))),Plus(p,C1))),x),x)),And(FreeQ(List(a,b,c,d,e,f,g,n),x),NeQ(Subtract(Times(e,f),Times(d,g)),C0),LtQ(p,CN1),GtQ(q,C0))))
  );
}
