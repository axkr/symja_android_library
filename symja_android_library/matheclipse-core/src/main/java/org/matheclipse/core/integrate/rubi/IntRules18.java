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
class IntRules18 { 
  public static IAST RULES = List( 
IIntegrate(361,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),CN1)),x_Symbol),
    Condition(With(List(Set($s("mn"),Simplify(Subtract(m,n)))),Subtract(Simp(Times(Power(x,Plus($s("mn"),C1)),Power(Times(b,Plus($s("mn"),C1)),CN1)),x),Dist(Times(a,Power(b,CN1)),Integrate(Times(Power(x,$s("mn")),Power(Plus(a,Times(b,Power(x,n))),CN1)),x),x))),And(FreeQ(List(a,b,m,n),x),FractionQ(Simplify(Times(Plus(m,C1),Power(n,CN1)))),SumSimplerQ(m,Negate(n))))),
IIntegrate(362,Integrate(Times(Power(x_,m_),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Power(Times(a,Plus(m,C1)),CN1)),x),Dist(Times(b,Power(a,CN1)),Integrate(Times(Power(x,Simplify(Plus(m,n))),Power(Plus(a,Times(b,Power(x,n))),CN1)),x),x)),And(FreeQ(List(a,b,m,n),x),FractionQ(Simplify(Times(Plus(m,C1),Power(n,CN1)))),SumSimplerQ(m,n)))),
IIntegrate(363,Integrate(Times(Power(Times(c_,x_),m_),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),CN1)),x_Symbol),
    Condition(Dist(Times(Power(c,IntPart(m)),Power(Times(c,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Power(x,n))),CN1)),x),x),And(FreeQ(List(a,b,c,m,n),x),FractionQ(Simplify(Times(Plus(m,C1),Power(n,CN1)))),Or(SumSimplerQ(m,n),SumSimplerQ(m,Negate(n)))))),
IIntegrate(364,Integrate(Times(Power(Times(c_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Simp(Times(Power(a,p),Power(Times(c,x),Plus(m,C1)),Hypergeometric2F1(Negate(p),Times(Plus(m,C1),Power(n,CN1)),Plus(Times(Plus(m,C1),Power(n,CN1)),C1),Times(CN1,b,Power(x,n),Power(a,CN1))),Power(Times(c,Plus(m,C1)),CN1)),x),And(FreeQ(List(a,b,c,m,n,p),x),Not(IGtQ(p,C0)),Or(ILtQ(p,C0),GtQ(a,C0))))),
IIntegrate(365,Integrate(Times(Power(Times(c_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Dist(Times(Power(a,IntPart(p)),Power(Plus(a,Times(b,Power(x,n))),FracPart(p)),Power(Power(Plus(C1,Times(b,Power(x,n),Power(a,CN1))),FracPart(p)),CN1)),Integrate(Times(Power(Times(c,x),m),Power(Plus(C1,Times(b,Power(x,n),Power(a,CN1))),p)),x),x),And(FreeQ(List(a,b,c,m,n,p),x),Not(IGtQ(p,C0)),Not(Or(ILtQ(p,C0),GtQ(a,C0)))))),
IIntegrate(366,Integrate(Times(Power(Times(c_DEFAULT,x_),m_DEFAULT),Power(Plus($p("a1"),Times($p("b1",true),Power(x_,n_))),p_),Power(Plus($p("a2"),Times($p("b2",true),Power(x_,n_))),p_)),x_Symbol),
    Condition(Dist(Times(Power(Plus($s("a1"),Times($s("b1"),Power(x,n))),FracPart(p)),Power(Plus($s("a2"),Times($s("b2"),Power(x,n))),FracPart(p)),Power(Power(Plus(Times($s("a1"),$s("a2")),Times($s("b1"),$s("b2"),Power(x,Times(C2,n)))),FracPart(p)),CN1)),Integrate(Times(Power(Times(c,x),m),Power(Plus(Times($s("a1"),$s("a2")),Times($s("b1"),$s("b2"),Power(x,Times(C2,n)))),p)),x),x),And(FreeQ(List($s("a1"),$s("b1"),$s("a2"),$s("b2"),c,m,n,p),x),EqQ(Plus(Times($s("a2"),$s("b1")),Times($s("a1"),$s("b2"))),C0),Not(IntegerQ(p))))),
IIntegrate(367,Integrate(Times(Power(Times(d_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(Times(c_,x_),n_))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(c,CN1),Subst(Integrate(Times(Power(Times(d,x,Power(c,CN1)),m),Power(Plus(a,Times(b,Power(x,n))),p)),x),x,Times(c,x)),x),FreeQ(List(a,b,c,d,m,n,p),x))),
IIntegrate(368,Integrate(Times(Power(Times(d_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(Times(c_DEFAULT,Power(x_,q_)),n_))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(Times(d,x),Plus(m,C1)),Power(Times(d,Power(Power(Times(c,Power(x,q)),Power(q,CN1)),Plus(m,C1))),CN1)),Subst(Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Power(x,Times(n,q)))),p)),x),x,Power(Times(c,Power(x,q)),Power(q,CN1))),x),And(FreeQ(List(a,b,c,d,m,n,p,q),x),IntegerQ(Times(n,q)),NeQ(x,Power(Times(c,Power(x,q)),Power(q,CN1)))))),
IIntegrate(369,Integrate(Times(Power(Times(d_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(Times(c_DEFAULT,Power(x_,q_)),n_))),p_DEFAULT)),x_Symbol),
    Condition(With(List(Set(k,Denominator(n))),Subst(Integrate(Times(Power(Times(d,x),m),Power(Plus(a,Times(b,Power(c,n),Power(x,Times(n,q)))),p)),x),Power(x,Power(k,CN1)),Times(Power(Times(c,Power(x,q)),Power(k,CN1)),Power(Times(Power(c,Power(k,CN1)),Power(Power(x,Power(k,CN1)),Subtract(q,C1))),CN1)))),And(FreeQ(List(a,b,c,d,m,p,q),x),FractionQ(n)))),
IIntegrate(370,Integrate(Times(Power(Times(d_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(Times(c_DEFAULT,Power(x_,q_)),n_))),p_DEFAULT)),x_Symbol),
    Condition(Subst(Integrate(Times(Power(Times(d,x),m),Power(Plus(a,Times(b,Power(c,n),Power(x,Times(n,q)))),p)),x),Power(x,Times(n,q)),Times(Power(Times(c,Power(x,q)),n),Power(Power(c,n),CN1))),And(FreeQ(List(a,b,c,d,m,n,p,q),x),Not(RationalQ(n))))),
IIntegrate(371,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Power(v_,n_))),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(With(List(Set(c,Coefficient(v,x,C0)),Set(d,Coefficient(v,x,C1))),Condition(Dist(Power(Power(d,Plus(m,C1)),CN1),Subst(Integrate(SimplifyIntegrand(Times(Power(Subtract(x,c),m),Power(Plus(a,Times(b,Power(x,n))),p)),x),x),x,v),x),NeQ(c,C0))),And(FreeQ(List(a,b,n,p),x),LinearQ(v,x),IntegerQ(m)))),
IIntegrate(372,Integrate(Times(Power(u_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(v_,n_))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(u,m),Power(Times(Coefficient(v,x,C1),Power(v,m)),CN1)),Subst(Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Power(x,n))),p)),x),x,v),x),And(FreeQ(List(a,b,m,n,p),x),LinearPairQ(u,v,x)))),
IIntegrate(373,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_))),q_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times(Power(Plus(a,Times(b,Power(x,n))),p),Power(Plus(c,Times(d,Power(x,n))),q)),x),x),And(FreeQ(List(a,b,c,d,n),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),IGtQ(p,C0),IGtQ(q,C0)))),
IIntegrate(374,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_))),q_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(x,Times(n,Plus(p,q))),Power(Plus(b,Times(a,Power(Power(x,n),CN1))),p),Power(Plus(d,Times(c,Power(Power(x,n),CN1))),q)),x),And(FreeQ(List(a,b,c,d,n),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),IntegersQ(p,q),NegQ(n)))),
IIntegrate(375,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_))),q_DEFAULT)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Power(Plus(a,Times(b,Power(Power(x,n),CN1))),p),Power(Plus(c,Times(d,Power(Power(x,n),CN1))),q),Power(x,CN2)),x),x,Power(x,CN1))),And(FreeQ(List(a,b,c,d,p,q),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),ILtQ(n,C0)))),
IIntegrate(376,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_))),q_DEFAULT)),x_Symbol),
    Condition(With(List(Set(g,Denominator(n))),Dist(g,Subst(Integrate(Times(Power(x,Subtract(g,C1)),Power(Plus(a,Times(b,Power(x,Times(g,n)))),p),Power(Plus(c,Times(d,Power(x,Times(g,n)))),q)),x),x,Power(x,Power(g,CN1))),x)),And(FreeQ(List(a,b,c,d,p,q),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),FractionQ(n)))),
IIntegrate(377,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_))),CN1)),x_Symbol),
    Condition(Subst(Integrate(Power(Subtract(c,Times(Subtract(Times(b,c),Times(a,d)),Power(x,n))),CN1),x),x,Times(x,Power(Power(Plus(a,Times(b,Power(x,n))),Power(n,CN1)),CN1))),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),EqQ(Plus(Times(n,p),C1),C0),IntegerQ(n)))),
IIntegrate(378,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_))),q_DEFAULT)),x_Symbol),
    Condition(Subtract(Negate(Simp(Times(x,Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Power(Plus(c,Times(d,Power(x,n))),q),Power(Times(a,n,Plus(p,C1)),CN1)),x)),Dist(Times(c,q,Power(Times(a,Plus(p,C1)),CN1)),Integrate(Times(Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Power(Plus(c,Times(d,Power(x,n))),Subtract(q,C1))),x),x)),And(FreeQ(List(a,b,c,d,n,p),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),EqQ(Plus(Times(n,Plus(p,q,C1)),C1),C0),GtQ(q,C0),NeQ(p,CN1)))),
IIntegrate(379,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_))),q_)),x_Symbol),
    Condition(Simp(Times(Power(a,p),x,Hypergeometric2F1(Power(n,CN1),Negate(p),Plus(C1,Power(n,CN1)),Times(CN1,Subtract(Times(b,c),Times(a,d)),Power(x,n),Power(Times(a,Plus(c,Times(d,Power(x,n)))),CN1))),Power(Times(Power(c,Plus(p,C1)),Power(Plus(c,Times(d,Power(x,n))),Power(n,CN1))),CN1)),x),And(FreeQ(List(a,b,c,d,n,q),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),EqQ(Plus(Times(n,Plus(p,q,C1)),C1),C0),ILtQ(p,C0)))),
IIntegrate(380,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_))),q_)),x_Symbol),
    Condition(Simp(Times(x,Power(Plus(a,Times(b,Power(x,n))),p),Hypergeometric2F1(Power(n,CN1),Negate(p),Plus(C1,Power(n,CN1)),Times(CN1,Subtract(Times(b,c),Times(a,d)),Power(x,n),Power(Times(a,Plus(c,Times(d,Power(x,n)))),CN1))),Power(Times(c,Power(Times(c,Plus(a,Times(b,Power(x,n))),Power(Times(a,Plus(c,Times(d,Power(x,n)))),CN1)),p),Power(Plus(c,Times(d,Power(x,n))),Plus(Power(n,CN1),p))),CN1)),x),And(FreeQ(List(a,b,c,d,n,p,q),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),EqQ(Plus(Times(n,Plus(p,q,C1)),C1),C0))))
  );
}
