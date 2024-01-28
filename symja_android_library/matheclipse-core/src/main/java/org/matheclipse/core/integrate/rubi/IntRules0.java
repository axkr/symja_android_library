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
class IntRules0 { 
  public static IAST RULES = List( 
IIntegrate(1,Integrate(Times(u_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(u,Power(Times(b,Power(x,n)),p)),x),And(FreeQ(List(a,b,n,p),x),EqQ(a,C0)))),
IIntegrate(2,Integrate(Times(u_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(u,Power(a,p)),x),And(FreeQ(List(a,b,n,p),x),EqQ(b,C0)))),
IIntegrate(3,Integrate(Times(u_DEFAULT,Power(Plus(a_,Times(c_DEFAULT,Power(x_,j_DEFAULT)),Times(b_DEFAULT,Power(x_,n_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(u,Power(Plus(Times(b,Power(x,n)),Times(c,Power(x,Times(C2,n)))),p)),x),And(FreeQ(List(a,b,c,n,p),x),EqQ(j,Times(C2,n)),EqQ(a,C0)))),
IIntegrate(4,Integrate(Times(u_DEFAULT,Power(Plus(a_DEFAULT,Times(c_DEFAULT,Power(x_,j_DEFAULT)),Times(b_DEFAULT,Power(x_,n_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(u,Power(Plus(a,Times(c,Power(x,Times(C2,n)))),p)),x),And(FreeQ(List(a,b,c,n,p),x),EqQ(j,Times(C2,n)),EqQ(b,C0)))),
IIntegrate(5,Integrate(Times(u_DEFAULT,Power(Plus(a_DEFAULT,Times(c_DEFAULT,Power(x_,j_DEFAULT)),Times(b_DEFAULT,Power(x_,n_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(u,Power(Plus(a,Times(b,Power(x,n))),p)),x),And(FreeQ(List(a,b,c,n,p),x),EqQ(j,Times(C2,n)),EqQ(c,C0)))),
IIntegrate(6,Integrate(Times(u_DEFAULT,Power(Plus(v_DEFAULT,Times(a_DEFAULT,$p("§fx")),Times(b_DEFAULT,$p("§fx"))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(u,Power(Plus(v,Times(Plus(a,b),$s("§fx"))),p)),x),And(FreeQ(list(a,b),x),Not(FreeQ($s("§fx"),x))))),
IIntegrate(7,Integrate(Times(u_DEFAULT,Power($p("§px"),p_)),x_Symbol),
    Condition(Integrate(Times(u,Power($s("§px"),Simplify(p))),x),And(PolyQ($s("§px"),x),Not(RationalQ(p)),FreeQ(p,x),RationalQ(Simplify(p))))),
IIntegrate(8,Integrate(Times(u_DEFAULT,Power(x_,m_DEFAULT),Power(Times(a_DEFAULT,x_),p_)),x_Symbol),
    Condition(Simp(Star(Power(Power(a,m),CN1),Integrate(Times(u,Power(Times(a,x),Plus(m,p))),x)),x),And(FreeQ(list(a,m,p),x),IntegerQ(m)))),
IIntegrate(9,Integrate(Times(u_DEFAULT,Power($p("§px"),p_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(r,Expon($s("§px"),x,Min))),Condition(Simp(Star(Power(Power(e,Times(p,r)),CN1),Integrate(Times(u,Power(Times(e,x),Plus(m,Times(p,r))),Power(ExpandToSum(Times($s("§px"),Power(Power(x,r),CN1)),x),p)),x)),x),IGtQ(r,C0))),And(FreeQ(list(e,m),x),PolyQ($s("§px"),x),IntegerQ(p),Not(MonomialQ($s("§px"),x))))),
IIntegrate(10,Integrate(Times(u_DEFAULT,Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(Times(a_DEFAULT,Power(x_,r_DEFAULT)),Times(b_DEFAULT,Power(x_,s_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Power(e,Times(p,r)),CN1),Integrate(Times(u,Power(Times(e,x),Plus(m,Times(p,r))),Power(Plus(a,Times(b,Power(x,Subtract(s,r)))),p)),x)),x),And(FreeQ(List(a,b,e,m,r,s),x),IntegerQ(p),Or(IntegerQ(Times(p,r)),GtQ(e,C0)),PosQ(Subtract(s,r))))),
IIntegrate(11,Integrate(Times(u_DEFAULT,Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(Times(a_DEFAULT,Power(x_,r_DEFAULT)),Times(b_DEFAULT,Power(x_,s_DEFAULT)),Times(c_DEFAULT,Power(x_,t_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Power(e,Times(p,r)),CN1),Integrate(Times(u,Power(Times(e,x),Plus(m,Times(p,r))),Power(Plus(a,Times(b,Power(x,Subtract(s,r))),Times(c,Power(x,Subtract(t,r)))),p)),x)),x),And(FreeQ(List(a,b,c,e,m,r,s,t),x),IntegerQ(p),Or(IntegerQ(Times(p,r)),GtQ(e,C0)),PosQ(Subtract(s,r)),PosQ(Subtract(t,r))))),
IIntegrate(12,Integrate(Times(u_DEFAULT,Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(Times(d_DEFAULT,Power(x_,q_DEFAULT)),Times(a_DEFAULT,Power(x_,r_DEFAULT)),Times(b_DEFAULT,Power(x_,s_DEFAULT)),Times(c_DEFAULT,Power(x_,t_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Power(e,Times(p,r)),CN1),Integrate(Times(u,Power(Times(e,x),Plus(m,Times(p,r))),Power(Plus(a,Times(b,Power(x,Subtract(s,r))),Times(c,Power(x,Subtract(t,r))),Times(d,Power(x,Subtract(q,r)))),p)),x)),x),And(FreeQ(List(a,b,c,d,e,m,r,s,t,q),x),IntegerQ(p),Or(IntegerQ(Times(p,r)),GtQ(e,C0)),PosQ(Subtract(s,r)),PosQ(Subtract(t,r)),PosQ(Subtract(q,r))))),
IIntegrate(13,Integrate(Times(u_DEFAULT,Power(Times(v_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_DEFAULT))),$p("mm",true)),Power(Plus(c_,Times(d_DEFAULT,Power(x_,$p("n2",true)))),m_DEFAULT)),p_)),x_Symbol),
    Condition(Integrate(Times(u,Power(Times(v,Power(c,m),Power(Power(a,Times(C2,m)),CN1),Power(Subtract(a,Times(b,Power(x,n))),m)),p)),x),And(FreeQ(List(a,b,c,d,n,p),x),EqQ($s("n2"),Times(C2,n)),EqQ(Plus(Times(Sqr(b),c),Times(Sqr(a),d)),C0),IntegersQ(m,$s("mm")),EqQ(Plus(m,$s("mm")),C0)))),
IIntegrate(14,Integrate(Times(a_DEFAULT,Power(x_,CN1)),x_Symbol),
    Condition(Simp(Times(a,Log(x)),x),FreeQ(a,x))),
IIntegrate(15,Integrate(Times(a_DEFAULT,Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Times(a,Power(x,Plus(m,C1)),Power(Plus(m,C1),CN1)),x),And(FreeQ(list(a,m),x),NeQ(m,CN1)))),
IIntegrate(16,Integrate(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Simp(Times(c,Log(RemoveContent(Plus(a,Times(b,x)),x)),Power(b,CN1)),x),FreeQ(list(a,b,c),x))),
IIntegrate(17,Integrate(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Times(c,Power(Plus(a,Times(b,x)),Plus(m,C1)),Power(Times(b,Plus(m,C1)),CN1)),x),And(FreeQ(List(a,b,c,m),x),NeQ(m,CN1)))),
IIntegrate(18,Integrate(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,u_)),m_)),x_Symbol),
    Condition(Simp(Star(Power(D(u,x),CN1),Subst(Integrate(Times(c,Power(Plus(a,Times(b,x)),m)),x),x,u)),x),And(FreeQ(List(a,b,c,m),x),LinearQ(u,x),NeQ(u,x)))),
IIntegrate(19,Integrate(Power(Times(a_DEFAULT,Power(x_,CN1)),p_),x_Symbol),
    Condition(Simp(Times(CN1,a,Power(Times(a,Power(x,CN1)),Subtract(p,C1)),Power(Subtract(p,C1),CN1)),x),And(FreeQ(list(a,p),x),Not(IntegerQ(p))))),
IIntegrate(20,Integrate(Power(Times(a_DEFAULT,Power(x_,n_)),p_),x_Symbol),
    Condition(Simp(Star(Times(Power(Times(a,Power(x,n)),p),Power(Power(x,Times(n,p)),CN1)),Integrate(Power(x,Times(n,p)),x)),x),And(FreeQ(list(a,n,p),x),Not(IntegerQ(p)))))
  );
}
