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
public class IntRules31 { 
  public static IAST RULES = List( 
IIntegrate(621,Int(Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),CN1D2),x_Symbol),
    Condition(Dist(C2,Subst(Int(Power(Subtract(Times(C4,c),Sqr(x)),CN1),x),x,Times(Plus(b,Times(C2,c,x)),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),CN1D2))),x),And(FreeQ(List(a,b,c),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0)))),
IIntegrate(622,Int(Power(Plus(Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_),x_Symbol),
    Condition(Dist(Times(Power(Plus(Times(b,x),Times(c,Sqr(x))),p),Power(Power(Times(CN1,c,Plus(Times(b,x),Times(c,Sqr(x))),Power(b,CN2)),p),CN1)),Int(Power(Subtract(Times(CN1,c,x,Power(b,CN1)),Times(Sqr(c),Sqr(x),Power(b,CN2))),p),x),x),And(FreeQ(List(b,c),x),RationalQ(p),LessEqual(C3,Denominator(p),C4)))),
IIntegrate(623,Int(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_),x_Symbol),
    Condition(With(List(Set(d,Denominator(p))),Condition(Dist(Times(d,Sqrt(Sqr(Plus(b,Times(C2,c,x)))),Power(Plus(b,Times(C2,c,x)),CN1)),Subst(Int(Times(Power(x,Subtract(Times(d,Plus(p,C1)),C1)),Power(Plus(Sqr(b),Times(CN1,C4,a,c),Times(C4,c,Power(x,d))),CN1D2)),x),x,Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Power(d,CN1))),x),LessEqual(C3,d,C4))),And(FreeQ(List(a,b,c),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),RationalQ(p)))),
IIntegrate(624,Int(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_),x_Symbol),
    Condition(With(List(Set(q,Rt(Subtract(Sqr(b),Times(C4,a,c)),C2))),Negate(Simp(Times(Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Plus(p,C1)),Hypergeometric2F1(Negate(p),Plus(p,C1),Plus(p,C2),Times(Plus(b,q,Times(C2,c,x)),Power(Times(C2,q),CN1))),Power(Times(q,Plus(p,C1),Power(Times(Subtract(Subtract(q,b),Times(C2,c,x)),Power(Times(C2,q),CN1)),Plus(p,C1))),CN1)),x))),And(FreeQ(List(a,b,c,p),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),Not(IntegerQ(Times(C4,p)))))),
IIntegrate(625,Int(Power(Plus(a_DEFAULT,Times(b_DEFAULT,u_),Times(c_DEFAULT,Sqr(u_))),p_),x_Symbol),
    Condition(Dist(Power(Coefficient(u,x,C1),CN1),Subst(Int(Power(Plus(a,Times(b,x),Times(c,Sqr(x))),p),x),x,u),x),And(FreeQ(List(a,b,c,p),x),LinearQ(u,x),NeQ(u,x)))),
IIntegrate(626,Int(Times(Power(Plus(d_,Times(e_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Plus(d,Times(e,x)),Plus(m,p)),Power(Plus(Times(a,Power(d,CN1)),Times(c,x,Power(e,CN1))),p)),x),And(FreeQ(List(a,b,c,d,e,m),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),EqQ(Plus(Times(c,Sqr(d)),Times(CN1,b,d,e),Times(a,Sqr(e))),C0),IntegerQ(p)))),
IIntegrate(627,Int(Times(Power(Plus(d_,Times(e_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_,Times(c_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Plus(d,Times(e,x)),Plus(m,p)),Power(Plus(Times(a,Power(d,CN1)),Times(c,x,Power(e,CN1))),p)),x),And(FreeQ(List(a,c,d,e,m,p),x),EqQ(Plus(Times(c,Sqr(d)),Times(a,Sqr(e))),C0),Or(IntegerQ(p),And(GtQ(a,C0),GtQ(d,C0),IntegerQ(Plus(m,p))))))),
IIntegrate(628,Int(Times(Plus(d_,Times(e_DEFAULT,x_)),Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Simp(Times(d,Log(RemoveContent(Plus(a,Times(b,x),Times(c,Sqr(x))),x)),Power(b,CN1)),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(Subtract(Times(C2,c,d),Times(b,e)),C0)))),
IIntegrate(629,Int(Times(Plus(d_,Times(e_DEFAULT,x_)),Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Times(d,Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Plus(p,C1)),Power(Times(b,Plus(p,C1)),CN1)),x),And(FreeQ(List(a,b,c,d,e,p),x),EqQ(Subtract(Times(C2,c,d),Times(b,e)),C0),NeQ(p,CN1)))),
IIntegrate(630,Int(Times(Plus(d_,Times(e_DEFAULT,x_)),Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Plus(d,Times(e,x)),Plus(p,C1)),Power(Plus(Times(a,Power(d,CN1)),Times(c,x,Power(e,CN1))),p)),x),And(FreeQ(List(a,b,c,d,e),x),NeQ(Subtract(Times(C2,c,d),Times(b,e)),C0),IGtQ(p,C0),EqQ(Plus(Times(c,Sqr(d)),Times(CN1,b,d,e),Times(a,Sqr(e))),C0)))),
IIntegrate(631,Int(Times(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Int(ExpandIntegrand(Times(Plus(d,Times(e,x)),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),p)),x),x),And(FreeQ(List(a,b,c,d,e),x),NeQ(Subtract(Times(C2,c,d),Times(b,e)),C0),IntegerQ(p),Or(GtQ(p,C0),EqQ(a,C0))))),
IIntegrate(632,Int(Times(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(With(List(Set(q,Rt(Subtract(Sqr(b),Times(C4,a,c)),C2))),Subtract(Dist(Times(Subtract(Times(c,d),Times(e,Subtract(Times(C1D2,b),Times(C1D2,q)))),Power(q,CN1)),Int(Power(Plus(Times(C1D2,b),Times(CN1,C1D2,q),Times(c,x)),CN1),x),x),Dist(Times(Subtract(Times(c,d),Times(e,Plus(Times(C1D2,b),Times(C1D2,q)))),Power(q,CN1)),Int(Power(Plus(Times(C1D2,b),Times(C1D2,q),Times(c,x)),CN1),x),x))),And(FreeQ(List(a,b,c,d,e),x),NeQ(Subtract(Times(C2,c,d),Times(b,e)),C0),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),NiceSqrtQ(Subtract(Sqr(b),Times(C4,a,c)))))),
IIntegrate(633,Int(Times(Plus(d_,Times(e_DEFAULT,x_)),Power(Plus(a_,Times(c_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(With(List(Set(q,Rt(Times(CN1,a,c),C2))),Plus(Dist(Plus(Times(C1D2,e),Times(c,d,Power(Times(C2,q),CN1))),Int(Power(Plus(Negate(q),Times(c,x)),CN1),x),x),Dist(Subtract(Times(C1D2,e),Times(c,d,Power(Times(C2,q),CN1))),Int(Power(Plus(q,Times(c,x)),CN1),x),x))),And(FreeQ(List(a,c,d,e),x),NiceSqrtQ(Times(CN1,a,c))))),
IIntegrate(634,Int(Times(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Plus(Dist(Times(Subtract(Times(C2,c,d),Times(b,e)),Power(Times(C2,c),CN1)),Int(Power(Plus(a,Times(b,x),Times(c,Sqr(x))),CN1),x),x),Dist(Times(e,Power(Times(C2,c),CN1)),Int(Times(Plus(b,Times(C2,c,x)),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e),x),NeQ(Subtract(Times(C2,c,d),Times(b,e)),C0),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),Not(NiceSqrtQ(Subtract(Sqr(b),Times(C4,a,c))))))),
IIntegrate(635,Int(Times(Plus(d_,Times(e_DEFAULT,x_)),Power(Plus(a_,Times(c_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Plus(Dist(d,Int(Power(Plus(a,Times(c,Sqr(x))),CN1),x),x),Dist(e,Int(Times(x,Power(Plus(a,Times(c,Sqr(x))),CN1)),x),x)),And(FreeQ(List(a,c,d,e),x),Not(NiceSqrtQ(Times(CN1,a,c)))))),
IIntegrate(636,Int(Times(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),QQ(-3L,2L))),x_Symbol),
    Condition(Simp(Times(CN2,Plus(Times(b,d),Times(CN1,C2,a,e),Times(Subtract(Times(C2,c,d),Times(b,e)),x)),Power(Times(Subtract(Sqr(b),Times(C4,a,c)),Sqrt(Plus(a,Times(b,x),Times(c,Sqr(x))))),CN1)),x),And(FreeQ(List(a,b,c,d,e),x),NeQ(Subtract(Times(C2,c,d),Times(b,e)),C0),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0)))),
IIntegrate(637,Int(Times(Plus(d_,Times(e_DEFAULT,x_)),Power(Plus(a_,Times(c_DEFAULT,Sqr(x_))),QQ(-3L,2L))),x_Symbol),
    Condition(Simp(Times(Plus(Times(CN1,a,e),Times(c,d,x)),Power(Times(a,c,Sqrt(Plus(a,Times(c,Sqr(x))))),CN1)),x),FreeQ(List(a,c,d,e),x))),
IIntegrate(638,Int(Times(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(Times(b,d),Times(CN1,C2,a,e),Times(Subtract(Times(C2,c,d),Times(b,e)),x)),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Plus(p,C1)),Power(Times(Plus(p,C1),Subtract(Sqr(b),Times(C4,a,c))),CN1)),x),Dist(Times(Plus(Times(C2,p),C3),Subtract(Times(C2,c,d),Times(b,e)),Power(Times(Plus(p,C1),Subtract(Sqr(b),Times(C4,a,c))),CN1)),Int(Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Plus(p,C1)),x),x)),And(FreeQ(List(a,b,c,d,e),x),NeQ(Subtract(Times(C2,c,d),Times(b,e)),C0),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),LtQ(p,CN1),NeQ(p,QQ(-3L,2L))))),
IIntegrate(639,Int(Times(Plus(d_,Times(e_DEFAULT,x_)),Power(Plus(a_,Times(c_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(Subtract(Times(a,e),Times(c,d,x)),Power(Plus(a,Times(c,Sqr(x))),Plus(p,C1)),Power(Times(C2,a,c,Plus(p,C1)),CN1)),x),Dist(Times(d,Plus(Times(C2,p),C3),Power(Times(C2,a,Plus(p,C1)),CN1)),Int(Power(Plus(a,Times(c,Sqr(x))),Plus(p,C1)),x),x)),And(FreeQ(List(a,c,d,e),x),LtQ(p,CN1),NeQ(p,QQ(-3L,2L))))),
IIntegrate(640,Int(Times(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(e,Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Plus(p,C1)),Power(Times(C2,c,Plus(p,C1)),CN1)),x),Dist(Times(Subtract(Times(C2,c,d),Times(b,e)),Power(Times(C2,c),CN1)),Int(Power(Plus(a,Times(b,x),Times(c,Sqr(x))),p),x),x)),And(FreeQ(List(a,b,c,d,e,p),x),NeQ(Subtract(Times(C2,c,d),Times(b,e)),C0),NeQ(p,CN1))))
  );
}
