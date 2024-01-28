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
class IntRules137 { 
  public static IAST RULES = List( 
IIntegrate(2741,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(d,x),Plus(m,C1)),Plus(a,Times(b,Log(Times(c,Power(x,n))))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Times(b,n,Power(Times(d,x),Plus(m,C1)),Power(Times(d,Sqr(Plus(m,C1))),CN1)),x)),And(FreeQ(List(a,b,c,d,m,n),x),NeQ(m,CN1)))),
IIntegrate(2742,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(d,x),Plus(m,C1)),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,n,p,Power(Plus(m,C1),CN1)),Integrate(Times(Power(Times(d,x),m),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),Subtract(p,C1))),x)),x)),And(FreeQ(List(a,b,c,d,m,n),x),NeQ(m,CN1),GtQ(p,C0)))),
IIntegrate(2743,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(d,x),Plus(m,C1)),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),Plus(p,C1)),Power(Times(b,d,n,Plus(p,C1)),CN1)),x),Simp(Star(Times(Plus(m,C1),Power(Times(b,n,Plus(p,C1)),CN1)),Integrate(Times(Power(Times(d,x),m),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),Plus(p,C1))),x)),x)),And(FreeQ(List(a,b,c,d,m,n),x),NeQ(m,CN1),LtQ(p,CN1)))),
IIntegrate(2744,Integrate(Times(Power(Log(Times(c_DEFAULT,Power(x_,n_))),CN1),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(n,CN1),Subst(Integrate(Power(Log(Times(c,x)),CN1),x),x,Power(x,n))),x),And(FreeQ(list(c,m,n),x),EqQ(m,Subtract(n,C1))))),
IIntegrate(2745,Integrate(Times(Power(Log(Times(c_DEFAULT,Power(x_,n_))),CN1),Power(Times(d_,x_),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(Times(d,x),m),Power(Power(x,m),CN1)),Integrate(Times(Power(x,m),Power(Log(Times(c,Power(x,n))),CN1)),x)),x),And(FreeQ(List(c,d,m,n),x),EqQ(m,Subtract(n,C1))))),
IIntegrate(2746,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,x_)),b_DEFAULT)),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Power(c,Plus(m,C1)),CN1),Subst(Integrate(Times(Exp(Times(Plus(m,C1),x)),Power(Plus(a,Times(b,x)),p)),x),x,Log(Times(c,x)))),x),And(FreeQ(List(a,b,c,p),x),IntegerQ(m)))),
IIntegrate(2747,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(Times(d,x),Plus(m,C1)),Power(Times(d,n,Power(Times(c,Power(x,n)),Times(Plus(m,C1),Power(n,CN1)))),CN1)),Subst(Integrate(Times(Exp(Times(Plus(m,C1),Power(n,CN1),x)),Power(Plus(a,Times(b,x)),p)),x),x,Log(Times(c,Power(x,n))))),x),FreeQ(List(a,b,c,d,m,n,p),x))),
IIntegrate(2748,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(Times(d_DEFAULT,Power(x_,q_)),m_)),x_Symbol),
    Condition(Simp(Star(Times(Power(Times(d,Power(x,q)),m),Power(Power(x,Times(m,q)),CN1)),Integrate(Times(Power(x,Times(m,q)),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p)),x)),x),FreeQ(List(a,b,c,d,m,n,p,q),x))),
IIntegrate(2749,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(Times($p("d1",true),Power(x_,$p("q1"))),$p("m1")),Power(Times($p("d2",true),Power(x_,$p("q2"))),$p("m2"))),x_Symbol),
    Condition(Simp(Star(Times(Power(Times($s("d1"),Power(x,$s("q1"))),$s("m1")),Power(Times($s("d2"),Power(x,$s("q2"))),$s("m2")),Power(Power(x,Plus(Times($s("m1"),$s("q1")),Times($s("m2"),$s("q2")))),CN1)),Integrate(Times(Power(x,Plus(Times($s("m1"),$s("q1")),Times($s("m2"),$s("q2")))),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p)),x)),x),FreeQ(List(a,b,c,$s("d1"),$s("d2"),$s("m1"),$s("m2"),n,p,$s("q1"),$s("q2")),x))),
IIntegrate(2750,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,Power(x_,r_DEFAULT))),q_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Power(Plus(d,Times(e,Power(x,r))),q),x))),Subtract(Simp(Star(Plus(a,Times(b,Log(Times(c,Power(x,n))))),u),x),Simp(Star(Times(b,n),Integrate(SimplifyIntegrand(Times(u,Power(x,CN1)),x),x)),x))),And(FreeQ(List(a,b,c,d,e,n,r),x),IGtQ(q,C0)))),
IIntegrate(2751,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,Power(x_,r_DEFAULT))),q_)),x_Symbol),
    Condition(Subtract(Simp(Times(x,Power(Plus(d,Times(e,Power(x,r))),Plus(q,C1)),Plus(a,Times(b,Log(Times(c,Power(x,n))))),Power(d,CN1)),x),Simp(Star(Times(b,n,Power(d,CN1)),Integrate(Power(Plus(d,Times(e,Power(x,r))),Plus(q,C1)),x)),x)),And(FreeQ(List(a,b,c,d,e,n,q,r),x),EqQ(Plus(Times(r,Plus(q,C1)),C1),C0)))),
IIntegrate(2752,Integrate(Times(Log(Times(c_DEFAULT,x_)),Power(Plus(d_,Times(e_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Simp(Times(CN1,Power(e,CN1),PolyLog(C2,Subtract(C1,Times(c,x)))),x),And(FreeQ(list(c,d,e),x),EqQ(Plus(e,Times(c,d)),C0)))),
IIntegrate(2753,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Plus(Simp(Times(Plus(a,Times(b,Log(Times(CN1,c,d,Power(e,CN1))))),Log(Plus(d,Times(e,x))),Power(e,CN1)),x),Simp(Star(b,Integrate(Times(Log(Times(CN1,e,x,Power(d,CN1))),Power(Plus(d,Times(e,x)),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e),x),GtQ(Times(CN1,c,d,Power(e,CN1)),C0)))),
IIntegrate(2754,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(Log(Plus(C1,Times(e,x,Power(d,CN1)))),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p),Power(e,CN1)),x),Simp(Star(Times(b,n,p,Power(e,CN1)),Integrate(Times(Log(Plus(C1,Times(e,x,Power(d,CN1)))),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),Subtract(p,C1)),Power(x,CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(p,C0)))),
IIntegrate(2755,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,x_)),CN2)),x_Symbol),
    Condition(Subtract(Simp(Times(x,Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p),Power(Times(d,Plus(d,Times(e,x))),CN1)),x),Simp(Star(Times(b,n,p,Power(d,CN1)),Integrate(Times(Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),Subtract(p,C1)),Power(Plus(d,Times(e,x)),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,n,p),x),GtQ(p,C0)))),
IIntegrate(2756,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,x_)),q_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(d,Times(e,x)),Plus(q,C1)),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p),Power(Times(e,Plus(q,C1)),CN1)),x),Simp(Star(Times(b,n,p,Power(Times(e,Plus(q,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Plus(q,C1)),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),Subtract(p,C1)),Power(x,CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,n,p,q),x),GtQ(p,C0),NeQ(q,CN1),Or(EqQ(p,C1),And(IntegersQ(Times(C2,p),Times(C2,q)),Not(IGtQ(q,C0))),And(EqQ(p,C2),NeQ(q,C1)))))),
IIntegrate(2757,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_),Power(Plus(d_,Times(e_DEFAULT,x_)),q_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(x,Power(Plus(d,Times(e,x)),q),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),CN1)),x),Negate(Simp(Star(Times(Plus(q,C1),Power(Times(b,n,Plus(p,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),q),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),Plus(p,C1))),x)),x)),Simp(Star(Times(d,q,Power(Times(b,n,Plus(p,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Subtract(q,C1)),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),Plus(p,C1))),x)),x)),And(FreeQ(List(a,b,c,d,e,n),x),LtQ(p,CN1),GtQ(q,C0)))),
IIntegrate(2758,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(x,Power(Plus(d,Times(e,Sqr(x))),q),Plus(a,Times(b,Log(Times(c,Power(x,n))))),Power(Plus(Times(C2,q),C1),CN1)),x),Negate(Simp(Star(Times(b,n,Power(Plus(Times(C2,q),C1),CN1)),Integrate(Power(Plus(d,Times(e,Sqr(x))),q),x)),x)),Simp(Star(Times(C2,d,q,Power(Plus(Times(C2,q),C1),CN1)),Integrate(Times(Power(Plus(d,Times(e,Sqr(x))),Subtract(q,C1)),Plus(a,Times(b,Log(Times(c,Power(x,n)))))),x)),x)),And(FreeQ(List(a,b,c,d,e,n),x),GtQ(q,C0)))),
IIntegrate(2759,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),QQ(-3L,2L))),x_Symbol),
    Condition(Subtract(Simp(Times(x,Plus(a,Times(b,Log(Times(c,Power(x,n))))),Power(Times(d,Sqrt(Plus(d,Times(e,Sqr(x))))),CN1)),x),Simp(Star(Times(b,n,Power(d,CN1)),Integrate(Power(Plus(d,Times(e,Sqr(x))),CN1D2),x)),x)),FreeQ(List(a,b,c,d,e,n),x))),
IIntegrate(2760,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,x,Power(Plus(d,Times(e,Sqr(x))),Plus(q,C1)),Plus(a,Times(b,Log(Times(c,Power(x,n))))),Power(Times(C2,d,Plus(q,C1)),CN1)),x),Simp(Star(Times(Plus(Times(C2,q),C3),Power(Times(C2,d,Plus(q,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,Sqr(x))),Plus(q,C1)),Plus(a,Times(b,Log(Times(c,Power(x,n)))))),x)),x),Simp(Star(Times(b,n,Power(Times(C2,d,Plus(q,C1)),CN1)),Integrate(Power(Plus(d,Times(e,Sqr(x))),Plus(q,C1)),x)),x)),And(FreeQ(List(a,b,c,d,e,n),x),LtQ(q,CN1))))
  );
}
