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
class IntRules1 { 
  public static IAST RULES = List( 
IIntegrate(21,Integrate(Times(Power(x_,m_DEFAULT),Power(Times(a_DEFAULT,Power(x_,n_)),p_)),x_Symbol),
    Condition(Simp(Star(Power(Times(n,Power(a,Subtract(Simplify(Times(Plus(m,C1),Power(n,CN1))),C1))),CN1),Subst(Integrate(Power(Times(a,x),Subtract(Plus(Simplify(Times(Plus(m,C1),Power(n,CN1))),p),C1)),x),x,Power(x,n))),x),And(FreeQ(List(a,m,n,p),x),IntegerQ(Simplify(Times(Plus(m,C1),Power(n,CN1))))))),
IIntegrate(22,Integrate(Times(Power(x_,m_DEFAULT),Power(Times(a_DEFAULT,Power(x_,n_DEFAULT)),p_)),x_Symbol),
    Condition(Simp(Star(Power(Power(a,Times(m,Power(n,CN1))),CN1),Integrate(Power(Times(a,Power(x,n)),Plus(p,Times(m,Power(n,CN1)))),x)),x),And(FreeQ(List(a,m,n,p),x),IntegerQ(Times(m,Power(n,CN1))),LtQ(Times(p,m,Power(n,CN1)),C0)))),
IIntegrate(23,Integrate(Times(Power(x_,m_DEFAULT),Power(Times(a_DEFAULT,Power(x_,n_DEFAULT)),p_)),x_Symbol),
    Condition(Simp(Star(Times(Power(Times(a,Power(x,n)),p),Power(Power(x,Times(n,p)),CN1)),Integrate(Power(x,Plus(m,Times(n,p))),x)),x),FreeQ(List(a,m,n,p),x))),
IIntegrate(24,Integrate(a_,x_Symbol),
    Condition(Simp(Times(a,x),x),FreeQ(a,x))),
IIntegrate(25,Integrate(Negate($p("§fx")),x_Symbol),
    Simp(Star(Identity(CN1),Integrate($s("§fx"),x)),x)),
IIntegrate(26,Integrate(Times(Complex(C0,a_),$p("§fx")),x_Symbol),
    Condition(Simp(Star(Complex(Identity(C0),a),Integrate($s("§fx"),x)),x),And(FreeQ(a,x),EqQ(Sqr(a),C1)))),
IIntegrate(27,Integrate(Times(a_,$p("§fx")),x_Symbol),
    Condition(Simp(Star(a,Integrate($s("§fx"),x)),x),And(FreeQ(a,x),Not(MatchQ($s("§fx"),Condition(Times(b_,$p("§gx")),FreeQ(b,x))))))),
IIntegrate(28,Integrate(Times(u_DEFAULT,Power(Times(a_DEFAULT,x_),m_DEFAULT),Power(Times(b_DEFAULT,Power(x_,i_DEFAULT)),p_DEFAULT),Power(Times(c_DEFAULT,Power(x_,j_DEFAULT)),q_DEFAULT),Power(Times(d_DEFAULT,Power(x_,k_DEFAULT)),r_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(Times(b,Power(x,i)),p),Power(Times(c,Power(x,j)),q),Power(Times(d,Power(x,k)),r),Power(Power(Times(a,x),Plus(Times(i,p),Times(j,q),Times(k,r))),CN1)),Integrate(Times(u,Power(Times(a,x),Plus(m,Times(i,p),Times(j,q),Times(k,r)))),x)),x),FreeQ(List(a,b,c,d,i,j,k,m,p,q,r),x))),
IIntegrate(29,Integrate(Times(u_DEFAULT,Power(Times(a_DEFAULT,x_),m_DEFAULT),Power(Times(b_DEFAULT,Power(x_,i_DEFAULT)),p_DEFAULT),Power(Times(c_DEFAULT,Power(x_,j_DEFAULT)),q_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(Times(b,Power(x,i)),p),Power(Times(c,Power(x,j)),q),Power(Power(Times(a,x),Plus(Times(i,p),Times(j,q))),CN1)),Integrate(Times(u,Power(Times(a,x),Plus(m,Times(i,p),Times(j,q)))),x)),x),FreeQ(List(a,b,c,i,j,m,p,q),x))),
IIntegrate(30,Integrate(Times(u_DEFAULT,Power(Times(a_DEFAULT,x_),m_DEFAULT),Power(Times(b_DEFAULT,Power(x_,i_DEFAULT)),p_)),x_Symbol),
    Condition(Simp(Star(Times(Power(b,IntPart(p)),Power(Times(b,Power(x,i)),FracPart(p)),Power(Times(Power(a,Times(i,IntPart(p))),Power(Times(a,x),Times(i,FracPart(p)))),CN1)),Integrate(Times(u,Power(Times(a,x),Plus(m,Times(i,p)))),x)),x),And(FreeQ(List(a,b,i,m,p),x),IntegerQ(i),Not(IntegerQ(p))))),
IIntegrate(31,Integrate(Times(u_DEFAULT,Power(Times(a_DEFAULT,x_),m_DEFAULT),Power(Times(b_DEFAULT,Power(x_,i_DEFAULT)),p_)),x_Symbol),
    Condition(Simp(Star(Times(Power(Times(b,Power(x,i)),p),Power(Power(Times(a,x),Times(i,p)),CN1)),Integrate(Times(u,Power(Times(a,x),Plus(m,Times(i,p)))),x)),x),And(FreeQ(List(a,b,i,m,p),x),Not(IntegerQ(p))))),
IIntegrate(32,Integrate(Times(u_DEFAULT,Power(Times(c_DEFAULT,Power(x_,k_)),r_DEFAULT),Power(Times(a_DEFAULT,Power(x_,m_)),p_DEFAULT),Power(Times(b_DEFAULT,Power(x_,n_)),q_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(Times(a,Power(x,m)),p),Power(Times(b,Power(x,n)),q),Power(Times(c,Power(x,k)),r),Power(Power(x,Plus(Times(m,p),Times(n,q),Times(k,r))),CN1)),Integrate(Times(u,Power(x,Plus(Times(m,p),Times(n,q),Times(k,r)))),x)),x),FreeQ(List(a,b,c,m,n,k,p,q,r),x))),
IIntegrate(33,Integrate(Times(u_DEFAULT,Power(Times(a_DEFAULT,Power(x_,m_)),p_DEFAULT),Power(Times(b_DEFAULT,Power(x_,n_)),q_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(a,IntPart(p)),Power(b,IntPart(q)),Power(Times(a,Power(x,m)),FracPart(p)),Power(Times(b,Power(x,n)),FracPart(q)),Power(Power(x,Plus(Times(m,FracPart(p)),Times(n,FracPart(q)))),CN1)),Integrate(Times(u,Power(x,Plus(Times(m,p),Times(n,q)))),x)),x),FreeQ(List(a,b,m,n,p,q),x))),
IIntegrate(34,Integrate(Times(u_DEFAULT,Power(Times(a_DEFAULT,Power(x_,m_)),p_)),x_Symbol),
    Condition(Simp(Star(Times(Power(a,IntPart(p)),Power(Times(a,Power(x,m)),FracPart(p)),Power(Power(x,Times(m,FracPart(p))),CN1)),Integrate(Times(u,Power(x,Times(m,p))),x)),x),And(FreeQ(list(a,m,p),x),Not(IntegerQ(p))))),
IIntegrate(35,Integrate(Times(u_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,x_)),m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,x_)),n_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Times(b,Power(d,CN1)),m),Integrate(Times(u,Power(Plus(c,Times(d,x)),Plus(m,n))),x)),x),And(FreeQ(List(a,b,c,d,m,n),x),EqQ(Subtract(Times(b,c),Times(a,d)),C0),IntegerQ(m),Not(And(IntegerQ(n),SimplerQ(Plus(a,Times(b,x)),Plus(c,Times(d,x)))))))),
IIntegrate(36,Integrate(Times(u_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,x_)),m_),Power(Plus(c_,Times(d_DEFAULT,x_)),n_)),x_Symbol),
    Condition(Simp(Star(Power(Times(b,Power(d,CN1)),m),Integrate(Times(u,Power(Plus(c,Times(d,x)),Plus(m,n))),x)),x),And(FreeQ(List(a,b,c,d,m,n),x),EqQ(Subtract(Times(b,c),Times(a,d)),C0),GtQ(Times(b,Power(d,CN1)),C0),Not(SimplerQ(Plus(a,Times(b,x)),Plus(c,Times(d,x))))))),
IIntegrate(37,Integrate(Times(u_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,x_)),m_),Power(Plus(c_,Times(d_DEFAULT,x_)),n_)),x_Symbol),
    Condition(Simp(Star(Times(Power(Plus(a,Times(b,x)),m),Power(Power(Plus(c,Times(d,x)),m),CN1)),Integrate(Times(u,Power(Plus(c,Times(d,x)),Plus(m,n))),x)),x),And(FreeQ(List(a,b,c,d,m,n),x),EqQ(Subtract(Times(b,c),Times(a,d)),C0),Not(SimplerQ(Plus(a,Times(b,x)),Plus(c,Times(d,x))))))),
IIntegrate(38,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),m_DEFAULT),Plus(c_,Times(d_DEFAULT,x_))),x_Symbol),
    Condition(Simp(Times(d,x,Power(Plus(a,Times(b,x)),Plus(m,C1)),Power(Times(b,Plus(m,C2)),CN1)),x),And(FreeQ(List(a,b,c,d,m),x),EqQ(Subtract(Times(a,d),Times(b,c,Plus(m,C2))),C0)))),
IIntegrate(39,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Integrate(Power(Plus(Times(a,c),Times(b,d,Sqr(x))),m),x),And(FreeQ(List(a,b,c,d,m),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),Or(IntegerQ(m),And(GtQ(a,C0),GtQ(c,C0)))))),
IIntegrate(40,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),m_),Power(Plus(c_,Times(d_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Plus(Simp(Times(x,Power(Plus(a,Times(b,x)),m),Power(Plus(c,Times(d,x)),m),Power(Plus(Times(C2,m),C1),CN1)),x),Simp(Star(Times(C2,a,c,m,Power(Plus(Times(C2,m),C1),CN1)),Integrate(Times(Power(Plus(a,Times(b,x)),Subtract(m,C1)),Power(Plus(c,Times(d,x)),Subtract(m,C1))),x)),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),IGtQ(Plus(m,C1D2),C0))))
  );
}
