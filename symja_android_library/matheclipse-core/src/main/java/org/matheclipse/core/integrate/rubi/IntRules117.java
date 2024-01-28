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
class IntRules117 { 
  public static IAST RULES = List( 
IIntegrate(2341,Integrate(Times($p("§pq"),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times($s("§pq"),Power(Plus(a,Times(b,Sqr(x))),p)),x),x),And(FreeQ(list(a,b),x),PolyQ($s("§pq"),x),IGtQ(p,CN2)))),
IIntegrate(2342,Integrate(Times($p("§pq"),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Integrate(Times(x,PolynomialQuotient($s("§pq"),x,x),Power(Plus(a,Times(b,Sqr(x))),p)),x),And(FreeQ(list(a,b,p),x),PolyQ($s("§pq"),x),EqQ(Coeff($s("§pq"),x,C0),C0),Not(MatchQ($s("§pq"),Condition(Times(Power(x,m_DEFAULT),u_DEFAULT),IntegerQ(m))))))),
IIntegrate(2343,Integrate(Times($p("§px"),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(PolynomialQuotient($s("§px"),Plus(a,Times(b,Sqr(x))),x),Power(Plus(a,Times(b,Sqr(x))),Plus(p,C1))),x),And(FreeQ(list(a,b,p),x),PolyQ($s("§px"),x),EqQ(PolynomialRemainder($s("§px"),Plus(a,Times(b,Sqr(x))),x),C0)))),
IIntegrate(2344,Integrate(Times($p("§pq"),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(With(list(Set(ASymbol,Coeff($s("§pq"),x,C0)),Set(QSymbol,PolynomialQuotient(Subtract($s("§pq"),Coeff($s("§pq"),x,C0)),Sqr(x),x))),Plus(Simp(Times(ASymbol,x,Power(Plus(a,Times(b,Sqr(x))),Plus(p,C1)),Power(a,CN1)),x),Simp(Star(Power(a,CN1),Integrate(Times(Sqr(x),Power(Plus(a,Times(b,Sqr(x))),p),Subtract(Times(a,QSymbol),Times(ASymbol,b,Plus(Times(C2,p),C3)))),x)),x))),And(FreeQ(list(a,b),x),PolyQ($s("§pq"),Sqr(x)),ILtQ(Plus(p,C1D2),C0),LtQ(Plus(Expon($s("§pq"),x),Times(C2,p),C1),C0)))),
IIntegrate(2345,Integrate(Times($p("§pq"),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(With(list(Set(QSymbol,PolynomialQuotient($s("§pq"),Plus(a,Times(b,Sqr(x))),x)),Set(f,Coeff(PolynomialRemainder($s("§pq"),Plus(a,Times(b,Sqr(x))),x),x,C0)),Set(g,Coeff(PolynomialRemainder($s("§pq"),Plus(a,Times(b,Sqr(x))),x),x,C1))),Plus(Simp(Times(Subtract(Times(a,g),Times(b,f,x)),Power(Plus(a,Times(b,Sqr(x))),Plus(p,C1)),Power(Times(C2,a,b,Plus(p,C1)),CN1)),x),Simp(Star(Power(Times(C2,a,Plus(p,C1)),CN1),Integrate(Times(Power(Plus(a,Times(b,Sqr(x))),Plus(p,C1)),ExpandToSum(Plus(Times(C2,a,Plus(p,C1),QSymbol),Times(f,Plus(Times(C2,p),C3))),x)),x)),x))),And(FreeQ(list(a,b),x),PolyQ($s("§pq"),x),LtQ(p,CN1)))),
IIntegrate(2346,Integrate(Times($p("§pq"),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(With(list(Set(q,Expon($s("§pq"),x)),Set(e,Coeff($s("§pq"),x,Expon($s("§pq"),x)))),Plus(Simp(Times(e,Power(x,Subtract(q,C1)),Power(Plus(a,Times(b,Sqr(x))),Plus(p,C1)),Power(Times(b,Plus(q,Times(C2,p),C1)),CN1)),x),Simp(Star(Power(Times(b,Plus(q,Times(C2,p),C1)),CN1),Integrate(Times(Power(Plus(a,Times(b,Sqr(x))),p),ExpandToSum(Subtract(Subtract(Times(b,Plus(q,Times(C2,p),C1),$s("§pq")),Times(a,e,Subtract(q,C1),Power(x,Subtract(q,C2)))),Times(b,e,Plus(q,Times(C2,p),C1),Power(x,q))),x)),x)),x))),And(FreeQ(list(a,b,p),x),PolyQ($s("§pq"),x),Not(LeQ(p,CN1))))),
IIntegrate(2347,Integrate(Times($p("§px"),Power(Plus(c_,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Plus(e_,Times(f_DEFAULT,x_)),n_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(PolynomialQuotient($s("§px"),Plus(c,Times(d,x)),x),Power(Plus(c,Times(d,x)),Plus(m,C1)),Power(Plus(e,Times(f,x)),n),Power(Plus(a,Times(b,Sqr(x))),p)),x),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),PolynomialQ($s("§px"),x),EqQ(PolynomialRemainder($s("§px"),Plus(c,Times(d,x)),x),C0)))),
IIntegrate(2348,Integrate(Times($p("§px"),Power(Plus(c_,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Plus(e_,Times(f_DEFAULT,x_)),n_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times($s("§px"),Power(Plus(c,Times(d,x)),m),Power(Plus(e,Times(f,x)),n),Power(Plus(a,Times(b,Sqr(x))),p)),x),x),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),PolyQ($s("§px"),x),Or(IntegerQ(p),And(IntegerQ(Times(C2,p)),IntegerQ(m),ILtQ(n,C0))),Not(And(IGtQ(m,C0),IGtQ(n,C0)))))),
IIntegrate(2349,Integrate(Times($p("§px"),Power(Plus(c_,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Plus(e_,Times(f_DEFAULT,x_)),n_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Plus(Integrate(Times(PolynomialQuotient($s("§px"),Plus(c,Times(d,x)),x),Power(Plus(c,Times(d,x)),Plus(m,C1)),Power(Plus(e,Times(f,x)),n),Power(Plus(a,Times(b,Sqr(x))),p)),x),Simp(Star(PolynomialRemainder($s("§px"),Plus(c,Times(d,x)),x),Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Plus(e,Times(f,x)),n),Power(Plus(a,Times(b,Sqr(x))),p)),x)),x)),And(FreeQ(List(a,b,c,d,e,f,n,p),x),PolynomialQ($s("§px"),x),LtQ(m,C0),Not(IntegerQ(n)),IntegersQ(Times(C2,m),Times(C2,n),Times(C2,p))))),
IIntegrate(2350,Integrate(Times($p("§px"),Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,x_)),n_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(PolynomialQuotient($s("§px"),Plus(c,Times(d,x)),x),Power(Times(e,x),m),Power(Plus(c,Times(d,x)),Plus(n,C1)),Power(Plus(a,Times(b,Sqr(x))),p)),x),And(FreeQ(List(a,b,c,d,e,m,n,p),x),PolynomialQ($s("§px"),x),EqQ(PolynomialRemainder($s("§px"),Plus(c,Times(d,x)),x),C0)))),
IIntegrate(2351,Integrate(Times($p("§px"),Power(x_,CN1),Power(Plus(c_,Times(d_DEFAULT,x_)),n_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Plus(Integrate(Times(PolynomialQuotient($s("§px"),x,x),Power(Plus(c,Times(d,x)),n),Power(Plus(a,Times(b,Sqr(x))),p)),x),Simp(Star(PolynomialRemainder($s("§px"),x,x),Integrate(Times(Power(Plus(c,Times(d,x)),n),Power(Plus(a,Times(b,Sqr(x))),p),Power(x,CN1)),x)),x)),And(FreeQ(List(a,b,c,d,n,p),x),PolynomialQ($s("§px"),x)))),
IIntegrate(2352,Integrate(Times($p("§px"),Power(Times(e_DEFAULT,x_),m_),Power(Plus(c_,Times(d_DEFAULT,x_)),CN1D2),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(With(list(Set($s("§px0"),Coefficient($s("§px"),x,C0))),Plus(Simp(Times($s("§px0"),Power(Times(e,x),Plus(m,C1)),Sqrt(Plus(c,Times(d,x))),Sqrt(Plus(a,Times(b,Sqr(x)))),Power(Times(a,c,e,Plus(m,C1)),CN1)),x),Simp(Star(Power(Times(C2,a,c,e,Plus(m,C1)),CN1),Integrate(Times(Power(Times(e,x),Plus(m,C1)),Power(Times(Sqrt(Plus(c,Times(d,x))),Sqrt(Plus(a,Times(b,Sqr(x))))),CN1),ExpandToSum(Subtract(Times(C2,a,c,Plus(m,C1),Subtract($s("§px"),$s("§px0")),Power(x,CN1)),Times($s("§px0"),Plus(Times(a,d,Plus(Times(C2,m),C3)),Times(C2,b,c,Plus(m,C2),x),Times(b,d,Plus(Times(C2,m),C5),Sqr(x))))),x)),x)),x))),And(FreeQ(List(a,b,c,d,e),x),PolynomialQ($s("§px"),x),LtQ(m,CN1)))),
IIntegrate(2353,Integrate(Times($p("§px"),Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,x_)),n_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times($s("§px"),Power(Times(e,x),m),Power(Plus(c,Times(d,x)),n),Power(Plus(a,Times(b,Sqr(x))),p)),x),x),And(FreeQ(List(a,b,c,d,e,m,n,p),x),PolyQ($s("§px"),x),Or(IntegerQ(p),And(IntegerQ(Times(C2,p)),IntegerQ(m),ILtQ(n,C0)))))),
IIntegrate(2354,Integrate(Times($p("§px"),Power(Times(e_DEFAULT,x_),m_),Power(Plus(c_,Times(d_DEFAULT,x_)),n_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(With(list(Set(k,Denominator(m))),Simp(Star(Times(k,Power(e,CN1)),Subst(Integrate(Times(ReplaceAll($s("§px"),Rule(x,Times(Power(x,k),Power(e,CN1)))),Power(x,Subtract(Times(k,Plus(m,C1)),C1)),Power(Plus(c,Times(d,Power(x,k),Power(e,CN1))),n),Power(Plus(a,Times(b,Power(x,Times(C2,k)),Power(e,CN2))),p)),x),x,Power(Times(e,x),Power(k,CN1)))),x)),And(FreeQ(List(a,b,c,d,e,n,p),x),PolyQ($s("§px"),x),FractionQ(m)))),
IIntegrate(2355,Integrate(Times($p("§px"),Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,x_)),n_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Plus(Integrate(Times(PolynomialQuotient($s("§px"),Plus(c,Times(d,x)),x),Power(Times(e,x),m),Power(Plus(c,Times(d,x)),Plus(n,C1)),Power(Plus(a,Times(b,Sqr(x))),p)),x),Simp(Star(PolynomialRemainder($s("§px"),Plus(c,Times(d,x)),x),Integrate(Times(Power(Times(e,x),m),Power(Plus(c,Times(d,x)),n),Power(Plus(a,Times(b,Sqr(x))),p)),x)),x)),And(FreeQ(List(a,b,c,d,e,m,p),x),PolynomialQ($s("§px"),x),LtQ(n,C0)))),
IIntegrate(2356,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_,Times(c_DEFAULT,Power(x_,n_DEFAULT))),QQ(-3L,2L)),Plus(e_,Times(h_DEFAULT,Power(x_,n_DEFAULT)),Times(f_DEFAULT,Power(x_,q_DEFAULT)),Times(g_DEFAULT,Power(x_,r_DEFAULT)))),x_Symbol),
    Condition(Simp(Times(CN1,Subtract(Plus(Times(C2,a,g),Times(C4,a,h,Power(x,Times(C1D4,n)))),Times(C2,c,f,Power(x,Times(C1D2,n)))),Power(Times(a,c,n,Sqrt(Plus(a,Times(c,Power(x,n))))),CN1)),x),And(FreeQ(List(a,c,e,f,g,h,m,n),x),EqQ(q,Times(C1D4,n)),EqQ(r,Times(C3,C1D4,n)),EqQ(Plus(Times(C4,m),Negate(n),C4),C0),EqQ(Plus(Times(c,e),Times(a,h)),C0)))),
IIntegrate(2357,Integrate(Times(Power(Times(d_,x_),m_DEFAULT),Power(Plus(a_,Times(c_DEFAULT,Power(x_,n_DEFAULT))),QQ(-3L,2L)),Plus(e_,Times(h_DEFAULT,Power(x_,n_DEFAULT)),Times(f_DEFAULT,Power(x_,q_DEFAULT)),Times(g_DEFAULT,Power(x_,r_DEFAULT)))),x_Symbol),
    Condition(Simp(Star(Times(Power(Times(d,x),m),Power(Power(x,m),CN1)),Integrate(Times(Power(x,m),Plus(e,Times(f,Power(x,Times(C1D4,n))),Times(g,Power(x,Times(C1D4,C3,n))),Times(h,Power(x,n))),Power(Plus(a,Times(c,Power(x,n))),QQ(-3L,2L))),x)),x),And(FreeQ(List(a,c,d,e,f,g,h,m,n),x),EqQ(Plus(Times(C4,m),Negate(n),C4),C0),EqQ(q,Times(C1D4,n)),EqQ(r,Times(C3,C1D4,n)),EqQ(Plus(Times(c,e),Times(a,h)),C0)))),
IIntegrate(2358,Integrate(Times($p("§pq"),Power(Times(c_DEFAULT,x_),m_),Power(Plus(a_,Times(b_DEFAULT,x_)),p_)),x_Symbol),
    Condition(With(list(Set(n,Denominator(p))),Simp(Star(Times(n,Power(b,CN1)),Subst(Integrate(Times(Power(x,Subtract(Plus(Times(n,p),n),C1)),Power(Plus(Times(CN1,a,c,Power(b,CN1)),Times(c,Power(x,n),Power(b,CN1))),m),ReplaceAll($s("§pq"),Rule(x,Plus(Times(CN1,a,Power(b,CN1)),Times(Power(x,n),Power(b,CN1)))))),x),x,Power(Plus(a,Times(b,x)),Power(n,CN1)))),x)),And(FreeQ(List(a,b,c,m),x),PolyQ($s("§pq"),x),FractionQ(p),ILtQ(m,CN1)))),
IIntegrate(2359,Integrate(Times($p("§pq"),Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Plus(m,C1),CN1),Subst(Integrate(Times(SubstFor(Power(x,Plus(m,C1)),$s("§pq"),x),Power(Plus(a,Times(b,Power(x,Simplify(Times(n,Power(Plus(m,C1),CN1)))))),p)),x),x,Power(x,Plus(m,C1)))),x),And(FreeQ(List(a,b,m,n,p),x),NeQ(m,CN1),IGtQ(Simplify(Times(n,Power(Plus(m,C1),CN1))),C0),PolyQ($s("§pq"),Power(x,Plus(m,C1)))))),
IIntegrate(2360,Integrate(Times($p("§pq"),Power(Times(c_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times(Power(Times(c,x),m),$s("§pq"),Power(Plus(a,Times(b,Power(x,n))),p)),x),x),And(FreeQ(List(a,b,c,m,n),x),PolyQ($s("§pq"),x),Or(IGtQ(p,C0),EqQ(n,C1)))))
  );
}
