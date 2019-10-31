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
public class IntRules25 { 
  public static IAST RULES = List( 
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
    Condition(Plus(Simp(Times(e,Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Plus(p,C1)),Power(Times(C2,c,Plus(p,C1)),CN1)),x),Dist(Times(Subtract(Times(C2,c,d),Times(b,e)),Power(Times(C2,c),CN1)),Int(Power(Plus(a,Times(b,x),Times(c,Sqr(x))),p),x),x)),And(FreeQ(List(a,b,c,d,e,p),x),NeQ(Subtract(Times(C2,c,d),Times(b,e)),C0),NeQ(p,CN1)))),
IIntegrate(641,Int(Times(Plus(d_,Times(e_DEFAULT,x_)),Power(Plus(a_,Times(c_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(e,Power(Plus(a,Times(c,Sqr(x))),Plus(p,C1)),Power(Times(C2,c,Plus(p,C1)),CN1)),x),Dist(d,Int(Power(Plus(a,Times(c,Sqr(x))),p),x),x)),And(FreeQ(List(a,c,d,e,p),x),NeQ(p,CN1)))),
IIntegrate(642,Int(Times(Power(Plus(d_,Times(e_DEFAULT,x_)),m_),Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Dist(Times(Power(e,m),Power(Power(c,Times(C1D2,m)),CN1)),Int(Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Plus(p,Times(C1D2,m))),x),x),And(FreeQ(List(a,b,c,d,e,p),x),EqQ(Subtract(Sqr(b),Times(C4,a,c)),C0),Not(IntegerQ(p)),EqQ(Subtract(Times(C2,c,d),Times(b,e)),C0),IntegerQ(Times(C1D2,m))))),
IIntegrate(643,Int(Times(Power(Plus(d_,Times(e_DEFAULT,x_)),m_),Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Dist(Times(Power(e,Subtract(m,C1)),Power(Power(c,Times(C1D2,Subtract(m,C1))),CN1)),Int(Times(Plus(d,Times(e,x)),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Plus(p,Times(C1D2,Subtract(m,C1))))),x),x),And(FreeQ(List(a,b,c,d,e,p),x),EqQ(Subtract(Sqr(b),Times(C4,a,c)),C0),Not(IntegerQ(p)),EqQ(Subtract(Times(C2,c,d),Times(b,e)),C0),IntegerQ(Times(C1D2,Subtract(m,C1)))))),
IIntegrate(644,Int(Times(Power(Plus(d_,Times(e_DEFAULT,x_)),m_),Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Dist(Times(Power(Plus(a,Times(b,x),Times(c,Sqr(x))),p),Power(Power(Plus(d,Times(e,x)),Times(C2,p)),CN1)),Int(Power(Plus(d,Times(e,x)),Plus(m,Times(C2,p))),x),x),And(FreeQ(List(a,b,c,d,e,m,p),x),EqQ(Subtract(Sqr(b),Times(C4,a,c)),C0),Not(IntegerQ(p)),EqQ(Subtract(Times(C2,c,d),Times(b,e)),C0),Not(IntegerQ(m))))),
IIntegrate(645,Int(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_),Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Dist(Times(Power(Plus(a,Times(b,x),Times(c,Sqr(x))),FracPart(p)),Power(Times(Power(c,IntPart(p)),Power(Plus(Times(C1D2,b),Times(c,x)),Times(C2,FracPart(p)))),CN1)),Int(ExpandLinearProduct(Power(Plus(Times(C1D2,b),Times(c,x)),Times(C2,p)),Power(Plus(d,Times(e,x)),m),Times(C1D2,b),c,x),x),x),And(FreeQ(List(a,b,c,d,e,m,p),x),EqQ(Subtract(Sqr(b),Times(C4,a,c)),C0),Not(IntegerQ(p)),NeQ(Subtract(Times(C2,c,d),Times(b,e)),C0),IGtQ(m,C0),EqQ(Plus(m,Times(CN1,C2,p),C1),C0)))),
IIntegrate(646,Int(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_),Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Dist(Times(Power(Plus(a,Times(b,x),Times(c,Sqr(x))),FracPart(p)),Power(Times(Power(c,IntPart(p)),Power(Plus(Times(C1D2,b),Times(c,x)),Times(C2,FracPart(p)))),CN1)),Int(Times(Power(Plus(d,Times(e,x)),m),Power(Plus(Times(C1D2,b),Times(c,x)),Times(C2,p))),x),x),And(FreeQ(List(a,b,c,d,e,m,p),x),EqQ(Subtract(Sqr(b),Times(C4,a,c)),C0),Not(IntegerQ(p)),NeQ(Subtract(Times(C2,c,d),Times(b,e)),C0)))),
IIntegrate(647,Int(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Power(e,p),CN1),Int(Times(Power(Times(e,x),Plus(m,p)),Power(Plus(b,Times(c,x)),p)),x),x),And(FreeQ(List(b,c,e,m),x),IntegerQ(p)))),
IIntegrate(648,Int(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Times(e,Power(Plus(d,Times(e,x)),Subtract(m,C1)),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Plus(p,C1)),Power(Times(c,Plus(p,C1)),CN1)),x),And(FreeQ(List(a,b,c,d,e,m,p),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),EqQ(Plus(Times(c,Sqr(d)),Times(CN1,b,d,e),Times(a,Sqr(e))),C0),Not(IntegerQ(p)),EqQ(Plus(m,p),C0)))),
IIntegrate(649,Int(Times(Power(Plus(d_,Times(e_DEFAULT,x_)),m_),Power(Plus(a_,Times(c_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Times(e,Power(Plus(d,Times(e,x)),Subtract(m,C1)),Power(Plus(a,Times(c,Sqr(x))),Plus(p,C1)),Power(Times(c,Plus(p,C1)),CN1)),x),And(FreeQ(List(a,c,d,e,m,p),x),EqQ(Plus(Times(c,Sqr(d)),Times(a,Sqr(e))),C0),Not(IntegerQ(p)),EqQ(Plus(m,p),C0)))),
IIntegrate(650,Int(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Times(e,Power(Plus(d,Times(e,x)),m),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Plus(p,C1)),Power(Times(Plus(p,C1),Subtract(Times(C2,c,d),Times(b,e))),CN1)),x),And(FreeQ(List(a,b,c,d,e,m,p),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),EqQ(Plus(Times(c,Sqr(d)),Times(CN1,b,d,e),Times(a,Sqr(e))),C0),Not(IntegerQ(p)),EqQ(Plus(m,Times(C2,p),C2),C0))))
  );
}
