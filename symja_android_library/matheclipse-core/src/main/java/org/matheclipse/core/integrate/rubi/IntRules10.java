package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *  
 */
public class IntRules10 { 
  public static IAST RULES = List( 
IIntegrate(251,Int(Times(Power(Plus($p("a1"),Times($p("b1",true),Power(x_,n_))),p_),Power(Plus($p("a2"),Times($p("b2",true),Power(x_,n_))),p_)),x_Symbol),
    Condition(Negate(Subst(Int(Times(Power(Plus($s("a1"),Times($s("b1"),Power(Power(x,n),CN1))),p),Power(Plus($s("a2"),Times($s("b2"),Power(Power(x,n),CN1))),p),Power(x,CN2)),x),x,Power(x,CN1))),And(FreeQ(List($s("a1"),$s("b1"),$s("a2"),$s("b2"),p),x),EqQ(Plus(Times($s("a2"),$s("b1")),Times($s("a1"),$s("b2"))),C0),ILtQ(Times(C2,n),C0)))),
IIntegrate(252,Int(Times(Power(Plus($p("a1"),Times($p("b1",true),Power(x_,n_))),p_),Power(Plus($p("a2"),Times($p("b2",true),Power(x_,n_))),p_)),x_Symbol),
    Condition(With(List(Set(k,Denominator(Times(C2,n)))),Dist(k,Subst(Int(Times(Power(x,Subtract(k,C1)),Power(Plus($s("a1"),Times($s("b1"),Power(x,Times(k,n)))),p),Power(Plus($s("a2"),Times($s("b2"),Power(x,Times(k,n)))),p)),x),x,Power(x,Power(k,CN1))),x)),And(FreeQ(List($s("a1"),$s("b1"),$s("a2"),$s("b2"),p),x),EqQ(Plus(Times($s("a2"),$s("b1")),Times($s("a1"),$s("b2"))),C0),FractionQ(Times(C2,n))))),
IIntegrate(253,Int(Times(Power(Plus($p("a1",true),Times($p("b1",true),Power(x_,n_))),p_),Power(Plus($p("a2",true),Times($p("b2",true),Power(x_,n_))),p_)),x_Symbol),
    Condition(Dist(Times(Power(Plus($s("a1"),Times($s("b1"),Power(x,n))),FracPart(p)),Power(Plus($s("a2"),Times($s("b2"),Power(x,n))),FracPart(p)),Power(Power(Plus(Times($s("a1"),$s("a2")),Times($s("b1"),$s("b2"),Power(x,Times(C2,n)))),FracPart(p)),CN1)),Int(Power(Plus(Times($s("a1"),$s("a2")),Times($s("b1"),$s("b2"),Power(x,Times(C2,n)))),p),x),x),And(FreeQ(List($s("a1"),$s("b1"),$s("a2"),$s("b2"),n,p),x),EqQ(Plus(Times($s("a2"),$s("b1")),Times($s("a1"),$s("b2"))),C0),Not(IntegerQ(p))))),
IIntegrate(254,Int(Power(Plus(a_,Times(b_DEFAULT,Power(Times(c_DEFAULT,Power(x_,q_DEFAULT)),n_))),p_DEFAULT),x_Symbol),
    Condition(Dist(Times(x,Power(Power(Times(c,Power(x,q)),Power(q,CN1)),CN1)),Subst(Int(Power(Plus(a,Times(b,Power(x,Times(n,q)))),p),x),x,Power(Times(c,Power(x,q)),Power(q,CN1))),x),And(FreeQ(List(a,b,c,n,p,q),x),IntegerQ(Times(n,q)),NeQ(x,Power(Times(c,Power(x,q)),Power(q,CN1)))))),
IIntegrate(255,Int(Power(Plus(a_,Times(b_DEFAULT,Power(Times(c_DEFAULT,Power(x_,q_DEFAULT)),n_))),p_DEFAULT),x_Symbol),
    Condition(With(List(Set(k,Denominator(n))),Subst(Int(Power(Plus(a,Times(b,Power(c,n),Power(x,Times(n,q)))),p),x),Power(x,Power(k,CN1)),Times(Power(Times(c,Power(x,q)),Power(k,CN1)),Power(Times(Power(c,Power(k,CN1)),Power(Power(x,Power(k,CN1)),Subtract(q,C1))),CN1)))),And(FreeQ(List(a,b,c,p,q),x),FractionQ(n)))),
IIntegrate(256,Int(Power(Plus(a_,Times(b_DEFAULT,Power(Times(c_DEFAULT,Power(x_,q_DEFAULT)),n_))),p_DEFAULT),x_Symbol),
    Condition(Subst(Int(Power(Plus(a,Times(b,Power(c,n),Power(x,Times(n,q)))),p),x),Power(x,Times(n,q)),Times(Power(Times(c,Power(x,q)),n),Power(Power(c,n),CN1))),And(FreeQ(List(a,b,c,n,p,q),x),Not(RationalQ(n))))),
IIntegrate(257,Int(Power(Plus(a_,Times(b_DEFAULT,Power(Times(d_DEFAULT,Power(x_,q_DEFAULT)),n_))),p_DEFAULT),x_Symbol),
    Condition(Negate(Subst(Int(Times(Power(Plus(a,Times(b,Power(Times(d,Power(Power(x,q),CN1)),n))),p),Power(x,CN2)),x),x,Power(x,CN1))),And(FreeQ(List(a,b,d,n,p),x),ILtQ(q,C0)))),
IIntegrate(258,Int(Power(Plus(a_,Times(b_DEFAULT,Power(Times(d_DEFAULT,Power(x_,q_DEFAULT)),n_))),p_DEFAULT),x_Symbol),
    Condition(With(List(Set(s,Denominator(q))),Dist(s,Subst(Int(Times(Power(x,Subtract(s,C1)),Power(Plus(a,Times(b,Power(Times(d,Power(x,Times(q,s))),n))),p)),x),x,Power(x,Power(s,CN1))),x)),And(FreeQ(List(a,b,d,n,p),x),FractionQ(q)))),
IIntegrate(259,Int(Times(Power(Times(c_DEFAULT,x_),m_DEFAULT),Power(Plus($p("a1"),Times($p("b1",true),Power(x_,n_))),p_),Power(Plus($p("a2"),Times($p("b2",true),Power(x_,n_))),p_)),x_Symbol),
    Condition(Int(Times(Power(Times(c,x),m),Power(Plus(Times($s("a1"),$s("a2")),Times($s("b1"),$s("b2"),Power(x,Times(C2,n)))),p)),x),And(FreeQ(List($s("a1"),$s("b1"),$s("a2"),$s("b2"),c,m,n,p),x),EqQ(Plus(Times($s("a2"),$s("b1")),Times($s("a1"),$s("b2"))),C0),Or(IntegerQ(p),And(GtQ($s("a1"),C0),GtQ($s("a2"),C0)))))),
IIntegrate(260,Int(Times(Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),CN1)),x_Symbol),
    Condition(Simp(Times(Log(RemoveContent(Plus(a,Times(b,Power(x,n))),x)),Power(Times(b,n),CN1)),x),And(FreeQ(List(a,b,m,n),x),EqQ(m,Subtract(n,C1))))),
IIntegrate(261,Int(Times(Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Simp(Times(Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),CN1)),x),And(FreeQ(List(a,b,m,n,p),x),EqQ(m,Subtract(n,C1)),NeQ(p,CN1)))),
IIntegrate(262,Int(Times(Power(x_,m_DEFAULT),Power(Plus($p("a1"),Times($p("b1",true),Power(x_,n_DEFAULT))),p_),Power(Plus($p("a2"),Times($p("b2",true),Power(x_,n_DEFAULT))),p_)),x_Symbol),
    Condition(Simp(Times(Power(Plus($s("a1"),Times($s("b1"),Power(x,n))),Plus(p,C1)),Power(Plus($s("a2"),Times($s("b2"),Power(x,n))),Plus(p,C1)),Power(Times(C2,$s("b1"),$s("b2"),n,Plus(p,C1)),CN1)),x),And(FreeQ(List($s("a1"),$s("b1"),$s("a2"),$s("b2"),m,n,p),x),EqQ(Plus(Times($s("a2"),$s("b1")),Times($s("a1"),$s("b2"))),C0),EqQ(m,Subtract(Times(C2,n),C1)),NeQ(p,CN1)))),
IIntegrate(263,Int(Times(Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Int(Times(Power(x,Plus(m,Times(n,p))),Power(Plus(b,Times(a,Power(Power(x,n),CN1))),p)),x),And(FreeQ(List(a,b,m,n),x),IntegerQ(p),NegQ(n)))),
IIntegrate(264,Int(Times(Power(Times(c_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Simp(Times(Power(Times(c,x),Plus(m,C1)),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Power(Times(a,c,Plus(m,C1)),CN1)),x),And(FreeQ(List(a,b,c,m,n,p),x),EqQ(Plus(Times(Plus(m,C1),Power(n,CN1)),p,C1),C0),NeQ(m,CN1)))),
IIntegrate(265,Int(Times(Power(Times(c_DEFAULT,x_),m_DEFAULT),Power(Plus($p("a1"),Times($p("b1",true),Power(x_,n_))),p_),Power(Plus($p("a2"),Times($p("b2",true),Power(x_,n_))),p_)),x_Symbol),
    Condition(Simp(Times(Power(Times(c,x),Plus(m,C1)),Power(Plus($s("a1"),Times($s("b1"),Power(x,n))),Plus(p,C1)),Power(Plus($s("a2"),Times($s("b2"),Power(x,n))),Plus(p,C1)),Power(Times($s("a1"),$s("a2"),c,Plus(m,C1)),CN1)),x),And(FreeQ(List($s("a1"),$s("b1"),$s("a2"),$s("b2"),c,m,n,p),x),EqQ(Plus(Times($s("a2"),$s("b1")),Times($s("a1"),$s("b2"))),C0),EqQ(Plus(Times(Plus(m,C1),Power(Times(C2,n),CN1)),p,C1),C0),NeQ(m,CN1)))),
IIntegrate(266,Int(Times(Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Dist(Power(n,CN1),Subst(Int(Times(Power(x,Subtract(Simplify(Times(Plus(m,C1),Power(n,CN1))),C1)),Power(Plus(a,Times(b,x)),p)),x),x,Power(x,n)),x),And(FreeQ(List(a,b,m,n,p),x),IntegerQ(Simplify(Times(Plus(m,C1),Power(n,CN1))))))),
IIntegrate(267,Int(Times(Power(x_,m_DEFAULT),Power(Plus($p("a1"),Times($p("b1",true),Power(x_,n_))),p_),Power(Plus($p("a2"),Times($p("b2",true),Power(x_,n_))),p_)),x_Symbol),
    Condition(Dist(Power(n,CN1),Subst(Int(Times(Power(x,Subtract(Simplify(Times(Plus(m,C1),Power(n,CN1))),C1)),Power(Plus($s("a1"),Times($s("b1"),x)),p),Power(Plus($s("a2"),Times($s("b2"),x)),p)),x),x,Power(x,n)),x),And(FreeQ(List($s("a1"),$s("b1"),$s("a2"),$s("b2"),m,n,p),x),EqQ(Plus(Times($s("a2"),$s("b1")),Times($s("a1"),$s("b2"))),C0),IntegerQ(Simplify(Times(Plus(m,C1),Power(Times(C2,n),CN1))))))),
IIntegrate(268,Int(Times(Power(Times(c_,x_),m_),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Dist(Times(Power(c,IntPart(m)),Power(Times(c,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Int(Times(Power(x,m),Power(Plus(a,Times(b,Power(x,n))),p)),x),x),And(FreeQ(List(a,b,c,m,n,p),x),IntegerQ(Simplify(Times(Plus(m,C1),Power(n,CN1))))))),
IIntegrate(269,Int(Times(Power(Times(c_,x_),m_),Power(Plus($p("a1"),Times($p("b1",true),Power(x_,n_))),p_),Power(Plus($p("a2"),Times($p("b2",true),Power(x_,n_))),p_)),x_Symbol),
    Condition(Dist(Times(Power(c,IntPart(m)),Power(Times(c,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Int(Times(Power(x,m),Power(Plus($s("a1"),Times($s("b1"),Power(x,n))),p),Power(Plus($s("a2"),Times($s("b2"),Power(x,n))),p)),x),x),And(FreeQ(List($s("a1"),$s("b1"),$s("a2"),$s("b2"),c,m,n,p),x),EqQ(Plus(Times($s("a2"),$s("b1")),Times($s("a1"),$s("b2"))),C0),IntegerQ(Simplify(Times(Plus(m,C1),Power(Times(C2,n),CN1))))))),
IIntegrate(270,Int(Times(Power(Times(c_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_DEFAULT)),x_Symbol),
    Condition(Int(ExpandIntegrand(Times(Power(Times(c,x),m),Power(Plus(a,Times(b,Power(x,n))),p)),x),x),And(FreeQ(List(a,b,c,m,n),x),IGtQ(p,C0)))),
IIntegrate(271,Int(Times(Power(x_,m_),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Power(Times(a,Plus(m,C1)),CN1)),x),Dist(Times(b,Plus(m,Times(n,Plus(p,C1)),C1),Power(Times(a,Plus(m,C1)),CN1)),Int(Times(Power(x,Plus(m,n)),Power(Plus(a,Times(b,Power(x,n))),p)),x),x)),And(FreeQ(List(a,b,m,n,p),x),ILtQ(Simplify(Plus(Times(Plus(m,C1),Power(n,CN1)),p,C1)),C0),NeQ(m,CN1)))),
IIntegrate(272,Int(Times(Power(x_,m_),Power(Plus($p("a1"),Times($p("b1",true),Power(x_,n_))),p_),Power(Plus($p("a2"),Times($p("b2",true),Power(x_,n_))),p_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Power(Plus($s("a1"),Times($s("b1"),Power(x,n))),Plus(p,C1)),Power(Plus($s("a2"),Times($s("b2"),Power(x,n))),Plus(p,C1)),Power(Times($s("a1"),$s("a2"),Plus(m,C1)),CN1)),x),Dist(Times($s("b1"),$s("b2"),Plus(m,Times(C2,n,Plus(p,C1)),C1),Power(Times($s("a1"),$s("a2"),Plus(m,C1)),CN1)),Int(Times(Power(x,Plus(m,Times(C2,n))),Power(Plus($s("a1"),Times($s("b1"),Power(x,n))),p),Power(Plus($s("a2"),Times($s("b2"),Power(x,n))),p)),x),x)),And(FreeQ(List($s("a1"),$s("b1"),$s("a2"),$s("b2"),m,n,p),x),EqQ(Plus(Times($s("a2"),$s("b1")),Times($s("a1"),$s("b2"))),C0),ILtQ(Simplify(Plus(Times(Plus(m,C1),Power(Times(C2,n),CN1)),p,C1)),C0),NeQ(m,CN1)))),
IIntegrate(273,Int(Times(Power(Times(c_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(Power(Times(c,x),Plus(m,C1)),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Power(Times(a,c,n,Plus(p,C1)),CN1)),x)),Dist(Times(Plus(m,Times(n,Plus(p,C1)),C1),Power(Times(a,n,Plus(p,C1)),CN1)),Int(Times(Power(Times(c,x),m),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1))),x),x)),And(FreeQ(List(a,b,c,m,n,p),x),ILtQ(Simplify(Plus(Times(Plus(m,C1),Power(n,CN1)),p,C1)),C0),NeQ(p,CN1)))),
IIntegrate(274,Int(Times(Power(Times(c_DEFAULT,x_),m_DEFAULT),Power(Plus($p("a1"),Times($p("b1",true),Power(x_,n_))),p_),Power(Plus($p("a2"),Times($p("b2",true),Power(x_,n_))),p_)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(Power(Times(c,x),Plus(m,C1)),Power(Plus($s("a1"),Times($s("b1"),Power(x,n))),Plus(p,C1)),Power(Plus($s("a2"),Times($s("b2"),Power(x,n))),Plus(p,C1)),Power(Times(C2,$s("a1"),$s("a2"),c,n,Plus(p,C1)),CN1)),x)),Dist(Times(Plus(m,Times(C2,n,Plus(p,C1)),C1),Power(Times(C2,$s("a1"),$s("a2"),n,Plus(p,C1)),CN1)),Int(Times(Power(Times(c,x),m),Power(Plus($s("a1"),Times($s("b1"),Power(x,n))),Plus(p,C1)),Power(Plus($s("a2"),Times($s("b2"),Power(x,n))),Plus(p,C1))),x),x)),And(FreeQ(List($s("a1"),$s("b1"),$s("a2"),$s("b2"),c,m,n,p),x),EqQ(Plus(Times($s("a2"),$s("b1")),Times($s("a1"),$s("b2"))),C0),ILtQ(Simplify(Plus(Times(Plus(m,C1),Power(Times(C2,n),CN1)),p,C1)),C0),NeQ(p,CN1)))),
IIntegrate(275,Int(Times(Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(With(List(Set(k,GCD(Plus(m,C1),n))),Condition(Dist(Power(k,CN1),Subst(Int(Times(Power(x,Subtract(Times(Plus(m,C1),Power(k,CN1)),C1)),Power(Plus(a,Times(b,Power(x,Times(n,Power(k,CN1))))),p)),x),x,Power(x,k)),x),Unequal(k,C1))),And(FreeQ(List(a,b,p),x),IGtQ(n,C0),IntegerQ(m))))
  );
}
