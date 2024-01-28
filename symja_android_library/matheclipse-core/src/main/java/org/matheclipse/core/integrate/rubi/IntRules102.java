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
class IntRules102 { 
  public static IAST RULES = List( 
IIntegrate(2041,Integrate(Times(Power(Plus($p("e1"),Times($p("f1",true),Power(x_,$p("n2",true)))),r_DEFAULT),Power(Plus($p("e2"),Times($p("f2",true),Power(x_,$p("n2",true)))),r_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_))),q_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(Plus($s("e1"),Times($s("f1"),Power(x,Times(C1D2,n)))),FracPart(r)),Power(Plus($s("e2"),Times($s("f2"),Power(x,Times(C1D2,n)))),FracPart(r)),Power(Power(Plus(Times($s("e1"),$s("e2")),Times($s("f1"),$s("f2"),Power(x,n))),FracPart(r)),CN1)),Integrate(Times(Power(Plus(a,Times(b,Power(x,n))),p),Power(Plus(c,Times(d,Power(x,n))),q),Power(Plus(Times($s("e1"),$s("e2")),Times($s("f1"),$s("f2"),Power(x,n))),r)),x)),x),And(FreeQ(List(a,b,c,d,$s("e1"),$s("f1"),$s("e2"),$s("f2"),n,p,q,r),x),EqQ($s("n2"),Times(C1D2,n)),EqQ(Plus(Times($s("e2"),$s("f1")),Times($s("e1"),$s("f2"))),C0)))),
IIntegrate(2042,Integrate(Times(u_DEFAULT,Power(Times(c_DEFAULT,Power(Times(d_,Plus(a_DEFAULT,Times(b_DEFAULT,x_))),q_)),p_)),x_Symbol),
    Condition(Simp(Star(Times(Power(Times(c,Power(Times(d,Plus(a,Times(b,x))),q)),p),Power(Power(Plus(a,Times(b,x)),Times(p,q)),CN1)),Integrate(Times(u,Power(Plus(a,Times(b,x)),Times(p,q))),x)),x),And(FreeQ(List(a,b,c,d,q,p),x),Not(IntegerQ(q)),Not(IntegerQ(p))))),
IIntegrate(2043,Integrate(Times(u_DEFAULT,Power(Times(c_DEFAULT,Power(Times(d_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),n_)),q_)),p_)),x_Symbol),
    Condition(Simp(Star(Times(Power(Times(c,Power(Times(d,Power(Plus(a,Times(b,x)),n)),q)),p),Power(Power(Plus(a,Times(b,x)),Times(n,p,q)),CN1)),Integrate(Times(u,Power(Plus(a,Times(b,x)),Times(n,p,q))),x)),x),And(FreeQ(List(a,b,c,d,n,q,p),x),Not(IntegerQ(q)),Not(IntegerQ(p))))),
IIntegrate(2044,Integrate(Times(u_DEFAULT,Power(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),q_)),p_)),x_Symbol),
    Condition(Simp(Star(Simp(Times(Power(Times(c,Power(Plus(a,Times(b,Power(x,n))),q)),p),Power(Power(Plus(a,Times(b,Power(x,n))),Times(p,q)),CN1))),Integrate(Times(u,Power(Plus(a,Times(b,Power(x,n))),Times(p,q))),x)),x),And(FreeQ(List(a,b,c,n,p,q),x),GeQ(a,C0)))),
IIntegrate(2045,Integrate(Times(u_DEFAULT,Power(Times(c_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_DEFAULT))),q_)),p_)),x_Symbol),
    Condition(Simp(Star(Simp(Times(Power(Times(c,Power(Plus(a,Times(b,Power(x,n))),q)),p),Power(Power(Plus(C1,Times(b,Power(x,n),Power(a,CN1))),Times(p,q)),CN1))),Integrate(Times(u,Power(Plus(C1,Times(b,Power(x,n),Power(a,CN1))),Times(p,q))),x)),x),And(FreeQ(List(a,b,c,n,p,q),x),Not(GeQ(a,C0))))),
IIntegrate(2046,Integrate(Times(u_DEFAULT,Power(Times(e_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),q_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_DEFAULT))),q_DEFAULT)),p_)),x_Symbol),
    Condition(Integrate(Times(u,Power(Times(e,Power(Times(d,Power(b,CN1)),q),Power(Plus(a,Times(b,Power(x,n))),Times(C2,q))),p)),x),And(FreeQ(List(a,b,c,d,e,n,p),x),IntegerQ(q),EqQ(Subtract(Times(b,c),Times(a,d)),C0)))),
IIntegrate(2047,Integrate(Times(u_DEFAULT,Power(Times(e_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),q_),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_DEFAULT))),q_)),p_)),x_Symbol),
    Condition(Integrate(Times(u,Power(Times(e,Power(Plus(Times(CN1,Sqr(a),d,Power(b,CN1)),Times(b,d,Power(x,Times(C2,n)))),q)),p)),x),And(FreeQ(List(a,b,c,d,e,n,p),x),IntegerQ(q),EqQ(Plus(Times(b,c),Times(a,d)),C0)))),
IIntegrate(2048,Integrate(Times(u_DEFAULT,Power(Times(e_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),Plus(c_,Times(d_DEFAULT,Power(x_,n_DEFAULT)))),p_)),x_Symbol),
    Condition(Integrate(Times(u,Power(Plus(Times(a,c,e),Times(Plus(Times(b,c),Times(a,d)),e,Power(x,n)),Times(b,d,e,Power(x,Times(C2,n)))),p)),x),FreeQ(List(a,b,c,d,e,n,p),x))),
IIntegrate(2049,Integrate(Times(u_DEFAULT,Power(Times(e_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_DEFAULT))),CN1)),p_)),x_Symbol),
    Condition(Simp(Star(Power(Times(b,e,Power(d,CN1)),p),Integrate(u,x)),x),And(FreeQ(List(a,b,c,d,e,n,p),x),EqQ(Subtract(Times(b,c),Times(a,d)),C0)))),
IIntegrate(2050,Integrate(Times(u_DEFAULT,Power(Times(e_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_DEFAULT))),CN1)),p_)),x_Symbol),
    Condition(Integrate(Times(u,Power(Plus(Times(a,e),Times(b,e,Power(x,n))),p),Power(Power(Plus(c,Times(d,Power(x,n))),p),CN1)),x),And(FreeQ(List(a,b,c,d,e,n,p),x),GtQ(Times(b,d,e),C0),GtQ(Subtract(c,Times(a,d,Power(b,CN1))),C0)))),
IIntegrate(2051,Integrate(Power(Times(e_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_DEFAULT))),CN1)),p_),x_Symbol),
    Condition(With(list(Set(q,Denominator(p))),Simp(Star(Times(q,e,Subtract(Times(b,c),Times(a,d)),Power(n,CN1)),Subst(Integrate(Times(Power(x,Subtract(Times(q,Plus(p,C1)),C1)),Power(Plus(Times(CN1,a,e),Times(c,Power(x,q))),Subtract(Power(n,CN1),C1)),Power(Power(Subtract(Times(b,e),Times(d,Power(x,q))),Plus(Power(n,CN1),C1)),CN1)),x),x,Power(Times(e,Plus(a,Times(b,Power(x,n))),Power(Plus(c,Times(d,Power(x,n))),CN1)),Power(q,CN1)))),x)),And(FreeQ(List(a,b,c,d,e),x),FractionQ(p),IntegerQ(Power(n,CN1))))),
IIntegrate(2052,Integrate(Times(Power(x_,m_DEFAULT),Power(Times(e_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)),Power(Plus(c_,Times(d_DEFAULT,x_)),CN1)),p_)),x_Symbol),
    Condition(With(list(Set(q,Denominator(p))),Simp(Star(Times(q,e,Subtract(Times(b,c),Times(a,d))),Subst(Integrate(Times(Power(x,Subtract(Times(q,Plus(p,C1)),C1)),Power(Plus(Times(CN1,a,e),Times(c,Power(x,q))),m),Power(Power(Subtract(Times(b,e),Times(d,Power(x,q))),Plus(m,C2)),CN1)),x),x,Power(Times(e,Plus(a,Times(b,x)),Power(Plus(c,Times(d,x)),CN1)),Power(q,CN1)))),x)),And(FreeQ(List(a,b,c,d,e,m),x),FractionQ(p),IntegerQ(m)))),
IIntegrate(2053,Integrate(Times(Power(x_,m_DEFAULT),Power(Times(e_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_DEFAULT))),CN1)),p_)),x_Symbol),
    Condition(Simp(Star(Power(n,CN1),Subst(Integrate(Times(Power(x,Subtract(Simplify(Times(Plus(m,C1),Power(n,CN1))),C1)),Power(Times(e,Plus(a,Times(b,x)),Power(Plus(c,Times(d,x)),CN1)),p)),x),x,Power(x,n))),x),And(FreeQ(List(a,b,c,d,e,m,n,p),x),IntegerQ(Simplify(Times(Plus(m,C1),Power(n,CN1))))))),
IIntegrate(2054,Integrate(Times(Power(Times(f_,x_),m_),Power(Times(e_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_DEFAULT))),CN1)),p_)),x_Symbol),
    Condition(Simp(Star(Simp(Times(Power(Times(c,x),m),Power(Power(x,m),CN1))),Integrate(Times(Power(x,m),Power(Times(e,Plus(a,Times(b,Power(x,n))),Power(Plus(c,Times(d,Power(x,n))),CN1)),p)),x)),x),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),IntegerQ(Simplify(Times(Plus(m,C1),Power(n,CN1))))))),
IIntegrate(2055,Integrate(Times(Power(u_,r_DEFAULT),Power(Times(e_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_DEFAULT))),CN1)),p_)),x_Symbol),
    Condition(With(list(Set(q,Denominator(p))),Simp(Star(Times(q,e,Subtract(Times(b,c),Times(a,d)),Power(n,CN1)),Subst(Integrate(SimplifyIntegrand(Times(Power(x,Subtract(Times(q,Plus(p,C1)),C1)),Power(Plus(Times(CN1,a,e),Times(c,Power(x,q))),Subtract(Power(n,CN1),C1)),Power(Power(Subtract(Times(b,e),Times(d,Power(x,q))),Plus(Power(n,CN1),C1)),CN1),Power(ReplaceAll(u,Rule(x,Times(Power(Plus(Times(CN1,a,e),Times(c,Power(x,q))),Power(n,CN1)),Power(Power(Subtract(Times(b,e),Times(d,Power(x,q))),Power(n,CN1)),CN1)))),r)),x),x),x,Power(Times(e,Plus(a,Times(b,Power(x,n))),Power(Plus(c,Times(d,Power(x,n))),CN1)),Power(q,CN1)))),x)),And(FreeQ(List(a,b,c,d,e),x),PolynomialQ(u,x),FractionQ(p),IntegerQ(Power(n,CN1)),IntegerQ(r)))),
IIntegrate(2056,Integrate(Times(Power(u_,r_DEFAULT),Power(x_,m_DEFAULT),Power(Times(e_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_DEFAULT))),CN1)),p_)),x_Symbol),
    Condition(With(list(Set(q,Denominator(p))),Simp(Star(Times(q,e,Subtract(Times(b,c),Times(a,d)),Power(n,CN1)),Subst(Integrate(SimplifyIntegrand(Times(Power(x,Subtract(Times(q,Plus(p,C1)),C1)),Power(Plus(Times(CN1,a,e),Times(c,Power(x,q))),Subtract(Times(Plus(m,C1),Power(n,CN1)),C1)),Power(Power(Subtract(Times(b,e),Times(d,Power(x,q))),Plus(Times(Plus(m,C1),Power(n,CN1)),C1)),CN1),Power(ReplaceAll(u,Rule(x,Times(Power(Plus(Times(CN1,a,e),Times(c,Power(x,q))),Power(n,CN1)),Power(Power(Subtract(Times(b,e),Times(d,Power(x,q))),Power(n,CN1)),CN1)))),r)),x),x),x,Power(Times(e,Plus(a,Times(b,Power(x,n))),Power(Plus(c,Times(d,Power(x,n))),CN1)),Power(q,CN1)))),x)),And(FreeQ(List(a,b,c,d,e),x),PolynomialQ(u,x),FractionQ(p),IntegerQ(Power(n,CN1)),IntegersQ(m,r)))),
IIntegrate(2057,Integrate(Times(u_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_))),CN1))),p_)),x_Symbol),
    Condition(Integrate(Times(u,Power(Times(Plus(b,Times(a,c),Times(a,d,Power(x,n))),Power(Plus(c,Times(d,Power(x,n))),CN1)),p)),x),FreeQ(List(a,b,c,d,n,p),x))),
IIntegrate(2058,Integrate(Times(u_DEFAULT,Power(Times(e_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),q_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_))),r_DEFAULT)),p_)),x_Symbol),
    Condition(Simp(Star(Simp(Times(Power(Times(e,Power(Plus(a,Times(b,Power(x,n))),q),Power(Plus(c,Times(d,Power(x,n))),r)),p),Power(Times(Power(Plus(a,Times(b,Power(x,n))),Times(p,q)),Power(Plus(c,Times(d,Power(x,n))),Times(p,r))),CN1))),Integrate(Times(u,Power(Plus(a,Times(b,Power(x,n))),Times(p,q)),Power(Plus(c,Times(d,Power(x,n))),Times(p,r))),x)),x),FreeQ(List(a,b,c,d,e,n,p,q,r),x))),
IIntegrate(2059,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times(c_DEFAULT,Power(x_,CN1)),n_))),p_),x_Symbol),
    Condition(Simp(Star(Negate(c),Subst(Integrate(Times(Power(Plus(a,Times(b,Power(x,n))),p),Power(x,CN2)),x),x,Times(c,Power(x,CN1)))),x),FreeQ(List(a,b,c,n,p),x))),
IIntegrate(2060,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times(c_DEFAULT,Power(x_,CN1)),n_))),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Negate(Power(c,Plus(m,C1))),Subst(Integrate(Times(Power(Plus(a,Times(b,Power(x,n))),p),Power(Power(x,Plus(m,C2)),CN1)),x),x,Times(c,Power(x,CN1)))),x),And(FreeQ(List(a,b,c,n,p),x),IntegerQ(m))))
  );
}
