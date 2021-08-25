package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class IntRules32 { 
  public static IAST RULES = List( 
IIntegrate(641,Integrate(Times(Plus(d_,Times(e_DEFAULT,x_)),Power(Plus(a_,Times(c_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(e,Power(Plus(a,Times(c,Sqr(x))),Plus(p,C1)),Power(Times(C2,c,Plus(p,C1)),CN1)),x),Dist(d,Integrate(Power(Plus(a,Times(c,Sqr(x))),p),x),x)),And(FreeQ(List(a,c,d,e,p),x),NeQ(p,CN1)))),
IIntegrate(642,Integrate(Times(Power(Plus(d_,Times(e_DEFAULT,x_)),m_),Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Dist(Times(Power(e,m),Power(Power(c,Times(C1D2,m)),CN1)),Integrate(Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Plus(p,Times(C1D2,m))),x),x),And(FreeQ(List(a,b,c,d,e,p),x),EqQ(Subtract(Sqr(b),Times(C4,a,c)),C0),Not(IntegerQ(p)),EqQ(Subtract(Times(C2,c,d),Times(b,e)),C0),IntegerQ(Times(C1D2,m))))),
IIntegrate(643,Integrate(Times(Power(Plus(d_,Times(e_DEFAULT,x_)),m_),Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Dist(Times(Power(e,Subtract(m,C1)),Power(Power(c,Times(C1D2,Subtract(m,C1))),CN1)),Integrate(Times(Plus(d,Times(e,x)),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Plus(p,Times(C1D2,Subtract(m,C1))))),x),x),And(FreeQ(List(a,b,c,d,e,p),x),EqQ(Subtract(Sqr(b),Times(C4,a,c)),C0),Not(IntegerQ(p)),EqQ(Subtract(Times(C2,c,d),Times(b,e)),C0),IntegerQ(Times(C1D2,Subtract(m,C1)))))),
IIntegrate(644,Integrate(Times(Power(Plus(d_,Times(e_DEFAULT,x_)),m_),Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Dist(Times(Power(Plus(a,Times(b,x),Times(c,Sqr(x))),p),Power(Power(Plus(d,Times(e,x)),Times(C2,p)),CN1)),Integrate(Power(Plus(d,Times(e,x)),Plus(m,Times(C2,p))),x),x),And(FreeQ(List(a,b,c,d,e,m,p),x),EqQ(Subtract(Sqr(b),Times(C4,a,c)),C0),Not(IntegerQ(p)),EqQ(Subtract(Times(C2,c,d),Times(b,e)),C0),Not(IntegerQ(m))))),
IIntegrate(645,Integrate(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_),Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Dist(Times(Power(Plus(a,Times(b,x),Times(c,Sqr(x))),FracPart(p)),Power(Times(Power(c,IntPart(p)),Power(Plus(Times(C1D2,b),Times(c,x)),Times(C2,FracPart(p)))),CN1)),Integrate(ExpandLinearProduct(Power(Plus(Times(C1D2,b),Times(c,x)),Times(C2,p)),Power(Plus(d,Times(e,x)),m),Times(C1D2,b),c,x),x),x),And(FreeQ(List(a,b,c,d,e,m,p),x),EqQ(Subtract(Sqr(b),Times(C4,a,c)),C0),Not(IntegerQ(p)),NeQ(Subtract(Times(C2,c,d),Times(b,e)),C0),IGtQ(m,C0),EqQ(Plus(m,Times(CN1,C2,p),C1),C0)))),
IIntegrate(646,Integrate(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_),Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Dist(Times(Power(Plus(a,Times(b,x),Times(c,Sqr(x))),FracPart(p)),Power(Times(Power(c,IntPart(p)),Power(Plus(Times(C1D2,b),Times(c,x)),Times(C2,FracPart(p)))),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),m),Power(Plus(Times(C1D2,b),Times(c,x)),Times(C2,p))),x),x),And(FreeQ(List(a,b,c,d,e,m,p),x),EqQ(Subtract(Sqr(b),Times(C4,a,c)),C0),Not(IntegerQ(p)),NeQ(Subtract(Times(C2,c,d),Times(b,e)),C0)))),
IIntegrate(647,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Power(e,p),CN1),Integrate(Times(Power(Times(e,x),Plus(m,p)),Power(Plus(b,Times(c,x)),p)),x),x),And(FreeQ(List(b,c,e,m),x),IntegerQ(p)))),
IIntegrate(648,Integrate(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Times(e,Power(Plus(d,Times(e,x)),Subtract(m,C1)),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Plus(p,C1)),Power(Times(c,Plus(p,C1)),CN1)),x),And(FreeQ(List(a,b,c,d,e,m,p),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),EqQ(Plus(Times(c,Sqr(d)),Times(CN1,b,d,e),Times(a,Sqr(e))),C0),Not(IntegerQ(p)),EqQ(Plus(m,p),C0)))),
IIntegrate(649,Integrate(Times(Power(Plus(d_,Times(e_DEFAULT,x_)),m_),Power(Plus(a_,Times(c_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Times(e,Power(Plus(d,Times(e,x)),Subtract(m,C1)),Power(Plus(a,Times(c,Sqr(x))),Plus(p,C1)),Power(Times(c,Plus(p,C1)),CN1)),x),And(FreeQ(List(a,c,d,e,m,p),x),EqQ(Plus(Times(c,Sqr(d)),Times(a,Sqr(e))),C0),Not(IntegerQ(p)),EqQ(Plus(m,p),C0)))),
IIntegrate(650,Integrate(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Times(e,Power(Plus(d,Times(e,x)),m),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Plus(p,C1)),Power(Times(Plus(p,C1),Subtract(Times(C2,c,d),Times(b,e))),CN1)),x),And(FreeQ(List(a,b,c,d,e,m,p),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),EqQ(Plus(Times(c,Sqr(d)),Times(CN1,b,d,e),Times(a,Sqr(e))),C0),Not(IntegerQ(p)),EqQ(Plus(m,Times(C2,p),C2),C0)))),
IIntegrate(651,Integrate(Times(Power(Plus(d_,Times(e_DEFAULT,x_)),m_),Power(Plus(a_,Times(c_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Times(e,Power(Plus(d,Times(e,x)),m),Power(Plus(a,Times(c,Sqr(x))),Plus(p,C1)),Power(Times(C2,c,d,Plus(p,C1)),CN1)),x),And(FreeQ(List(a,c,d,e,m,p),x),EqQ(Plus(Times(c,Sqr(d)),Times(a,Sqr(e))),C0),Not(IntegerQ(p)),EqQ(Plus(m,Times(C2,p),C2),C0)))),
IIntegrate(652,Integrate(Times(Sqr(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Subtract(Simp(Times(e,Plus(d,Times(e,x)),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Plus(p,C1)),Power(Times(c,Plus(p,C1)),CN1)),x),Dist(Times(Sqr(e),Plus(p,C2),Power(Times(c,Plus(p,C1)),CN1)),Integrate(Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Plus(p,C1)),x),x)),And(FreeQ(List(a,b,c,d,e,p),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),EqQ(Plus(Times(c,Sqr(d)),Times(CN1,b,d,e),Times(a,Sqr(e))),C0),Not(IntegerQ(p)),LtQ(p,CN1)))),
IIntegrate(653,Integrate(Times(Sqr(Plus(d_,Times(e_DEFAULT,x_))),Power(Plus(a_,Times(c_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Subtract(Simp(Times(e,Plus(d,Times(e,x)),Power(Plus(a,Times(c,Sqr(x))),Plus(p,C1)),Power(Times(c,Plus(p,C1)),CN1)),x),Dist(Times(Sqr(e),Plus(p,C2),Power(Times(c,Plus(p,C1)),CN1)),Integrate(Power(Plus(a,Times(c,Sqr(x))),Plus(p,C1)),x),x)),And(FreeQ(List(a,c,d,e,p),x),EqQ(Plus(Times(c,Sqr(d)),Times(a,Sqr(e))),C0),Not(IntegerQ(p)),LtQ(p,CN1)))),
IIntegrate(654,Integrate(Times(Power(Plus(d_,Times(e_DEFAULT,x_)),m_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Integrate(Times(Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Plus(m,p)),Power(Power(Plus(Times(a,Power(d,CN1)),Times(c,x,Power(e,CN1))),m),CN1)),x),And(FreeQ(List(a,b,c,d,e),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),EqQ(Plus(Times(c,Sqr(d)),Times(CN1,b,d,e),Times(a,Sqr(e))),C0),Not(IntegerQ(p)),IntegerQ(m),RationalQ(p),Or(LtQ(C0,Negate(m),p),LtQ(p,Negate(m),C0)),NeQ(m,C2),NeQ(m,CN1)))),
IIntegrate(655,Integrate(Times(Power(Plus(d_,Times(e_DEFAULT,x_)),m_),Power(Plus(a_,Times(c_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Dist(Times(Power(d,Times(C2,m)),Power(Power(a,m),CN1)),Integrate(Times(Power(Plus(a,Times(c,Sqr(x))),Plus(m,p)),Power(Power(Subtract(d,Times(e,x)),m),CN1)),x),x),And(FreeQ(List(a,c,d,e,m,p),x),EqQ(Plus(Times(c,Sqr(d)),Times(a,Sqr(e))),C0),Not(IntegerQ(p)),IntegerQ(m),RationalQ(p),Or(LtQ(C0,Negate(m),p),LtQ(p,Negate(m),C0)),NeQ(m,C2),NeQ(m,CN1)))),
IIntegrate(656,Integrate(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(e,Power(Plus(d,Times(e,x)),Subtract(m,C1)),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Plus(p,C1)),Power(Times(c,Plus(m,Times(C2,p),C1)),CN1)),x),Dist(Times(Simplify(Plus(m,p)),Subtract(Times(C2,c,d),Times(b,e)),Power(Times(c,Plus(m,Times(C2,p),C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Subtract(m,C1)),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),p)),x),x)),And(FreeQ(List(a,b,c,d,e,m,p),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),EqQ(Plus(Times(c,Sqr(d)),Times(CN1,b,d,e),Times(a,Sqr(e))),C0),Not(IntegerQ(p)),IGtQ(Simplify(Plus(m,p)),C0)))),
IIntegrate(657,Integrate(Times(Power(Plus(d_,Times(e_DEFAULT,x_)),m_),Power(Plus(a_,Times(c_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(e,Power(Plus(d,Times(e,x)),Subtract(m,C1)),Power(Plus(a,Times(c,Sqr(x))),Plus(p,C1)),Power(Times(c,Plus(m,Times(C2,p),C1)),CN1)),x),Dist(Times(C2,c,d,Simplify(Plus(m,p)),Power(Times(c,Plus(m,Times(C2,p),C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Subtract(m,C1)),Power(Plus(a,Times(c,Sqr(x))),p)),x),x)),And(FreeQ(List(a,c,d,e,m,p),x),EqQ(Plus(Times(c,Sqr(d)),Times(a,Sqr(e))),C0),Not(IntegerQ(p)),IGtQ(Simplify(Plus(m,p)),C0)))),
IIntegrate(658,Integrate(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(e,Power(Plus(d,Times(e,x)),m),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Plus(p,C1)),Power(Times(Plus(m,p,C1),Subtract(Times(C2,c,d),Times(b,e))),CN1)),x)),Dist(Times(c,Simplify(Plus(m,Times(C2,p),C2)),Power(Times(Plus(m,p,C1),Subtract(Times(C2,c,d),Times(b,e))),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),p)),x),x)),And(FreeQ(List(a,b,c,d,e,m,p),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),EqQ(Plus(Times(c,Sqr(d)),Times(CN1,b,d,e),Times(a,Sqr(e))),C0),Not(IntegerQ(p)),ILtQ(Simplify(Plus(m,Times(C2,p),C2)),C0)))),
IIntegrate(659,Integrate(Times(Power(Plus(d_,Times(e_DEFAULT,x_)),m_),Power(Plus(a_,Times(c_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(e,Power(Plus(d,Times(e,x)),m),Power(Plus(a,Times(c,Sqr(x))),Plus(p,C1)),Power(Times(C2,c,d,Plus(m,p,C1)),CN1)),x)),Dist(Times(Simplify(Plus(m,Times(C2,p),C2)),Power(Times(C2,d,Plus(m,p,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Power(Plus(a,Times(c,Sqr(x))),p)),x),x)),And(FreeQ(List(a,c,d,e,m,p),x),EqQ(Plus(Times(c,Sqr(d)),Times(a,Sqr(e))),C0),Not(IntegerQ(p)),ILtQ(Simplify(Plus(m,Times(C2,p),C2)),C0)))),
IIntegrate(660,Integrate(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),CN1D2),Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Dist(Times(C2,e),Subst(Integrate(Power(Plus(Times(C2,c,d),Times(CN1,b,e),Times(Sqr(e),Sqr(x))),CN1),x),x,Times(Sqrt(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Plus(d,Times(e,x)),CN1D2))),x),And(FreeQ(List(a,b,c,d,e),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),EqQ(Plus(Times(c,Sqr(d)),Times(CN1,b,d,e),Times(a,Sqr(e))),C0))))
  );
}
