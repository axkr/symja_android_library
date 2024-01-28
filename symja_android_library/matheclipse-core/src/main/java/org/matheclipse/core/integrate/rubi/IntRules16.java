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
class IntRules16 { 
  public static IAST RULES = List( 
IIntegrate(321,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D2),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Times(Power(Times(Sqrt(a),Sqrt(c),Rt(Times(CN1,d,Power(c,CN1)),C2)),CN1),EllipticF(ArcSin(Times(Rt(Times(CN1,d,Power(c,CN1)),C2),x)),Times(b,c,Power(Times(a,d),CN1)))),x),And(FreeQ(List(a,b,c,d),x),NegQ(Times(d,Power(c,CN1))),GtQ(c,C0),GtQ(a,C0),Not(And(NegQ(Times(b,Power(a,CN1))),SimplerSqrtQ(Times(CN1,b,Power(a,CN1)),Times(CN1,d,Power(c,CN1)))))))),
IIntegrate(322,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D2),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Times(CN1,Power(Times(Sqrt(c),Rt(Times(CN1,d,Power(c,CN1)),C2),Sqrt(Subtract(a,Times(b,c,Power(d,CN1))))),CN1),EllipticF(ArcCos(Times(Rt(Times(CN1,d,Power(c,CN1)),C2),x)),Times(b,c,Power(Subtract(Times(b,c),Times(a,d)),CN1)))),x),And(FreeQ(List(a,b,c,d),x),NegQ(Times(d,Power(c,CN1))),GtQ(c,C0),GtQ(Subtract(a,Times(b,c,Power(d,CN1))),C0)))),
IIntegrate(323,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D2),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Star(Times(Sqrt(Plus(C1,Times(d,Power(c,CN1),Sqr(x)))),Power(Plus(c,Times(d,Sqr(x))),CN1D2)),Integrate(Power(Times(Sqrt(Plus(a,Times(b,Sqr(x)))),Sqrt(Plus(C1,Times(d,Power(c,CN1),Sqr(x))))),CN1),x)),x),And(FreeQ(List(a,b,c,d),x),Not(GtQ(c,C0))))),
IIntegrate(324,Integrate(Times(Sqrt(Plus(a_,Times(b_DEFAULT,Sqr(x_)))),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Plus(Simp(Star(a,Integrate(Power(Times(Sqrt(Plus(a,Times(b,Sqr(x)))),Sqrt(Plus(c,Times(d,Sqr(x))))),CN1),x)),x),Simp(Star(b,Integrate(Times(Sqr(x),Power(Times(Sqrt(Plus(a,Times(b,Sqr(x)))),Sqrt(Plus(c,Times(d,Sqr(x))))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d),x),PosQ(Times(d,Power(c,CN1))),PosQ(Times(b,Power(a,CN1)))))),
IIntegrate(325,Integrate(Times(Sqrt(Plus(a_,Times(b_DEFAULT,Sqr(x_)))),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Plus(Simp(Times(x,Sqrt(Plus(a,Times(b,Sqr(x)))),Power(Plus(c,Times(d,Sqr(x))),CN1D2)),x),Simp(Times(Sqrt(Times(CN2,a)),x,Power(Times(d,Sqr(x)),CN1D2),EllipticE(ArcSin(Times(Sqrt(Times(C2,c)),Power(Plus(c,Times(d,Sqr(x))),CN1D2))),C1D2)),x)),And(FreeQ(List(a,b,c,d),x),PosQ(Times(d,Power(c,CN1))),EqQ(Plus(Times(b,c),Times(a,d)),C0),LtQ(a,C0),GtQ(c,C0)))),
IIntegrate(326,Integrate(Times(Sqrt(Plus(a_,Times(b_DEFAULT,Sqr(x_)))),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Subtract(Simp(Star(Times(b,Power(d,CN1)),Integrate(Times(Sqrt(Plus(c,Times(d,Sqr(x)))),Power(Plus(a,Times(b,Sqr(x))),CN1D2)),x)),x),Simp(Star(Times(Subtract(Times(b,c),Times(a,d)),Power(d,CN1)),Integrate(Power(Times(Sqrt(Plus(a,Times(b,Sqr(x)))),Sqrt(Plus(c,Times(d,Sqr(x))))),CN1),x)),x)),And(FreeQ(List(a,b,c,d),x),PosQ(Times(d,Power(c,CN1))),NegQ(Times(b,Power(a,CN1)))))),
IIntegrate(327,Integrate(Times(Sqrt(Plus(a_,Times(b_DEFAULT,Sqr(x_)))),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Times(Sqrt(a),Power(Times(Sqrt(c),Rt(Times(CN1,d,Power(c,CN1)),C2)),CN1),EllipticE(ArcSin(Times(Rt(Times(CN1,d,Power(c,CN1)),C2),x)),Times(b,c,Power(Times(a,d),CN1)))),x),And(FreeQ(List(a,b,c,d),x),NegQ(Times(d,Power(c,CN1))),GtQ(c,C0),GtQ(a,C0)))),
IIntegrate(328,Integrate(Times(Sqrt(Plus(a_,Times(b_DEFAULT,Sqr(x_)))),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Times(CN1,Sqrt(Subtract(a,Times(b,c,Power(d,CN1)))),Power(Times(Sqrt(c),Rt(Times(CN1,d,Power(c,CN1)),C2)),CN1),EllipticE(ArcCos(Times(Rt(Times(CN1,d,Power(c,CN1)),C2),x)),Times(b,c,Power(Subtract(Times(b,c),Times(a,d)),CN1)))),x),And(FreeQ(List(a,b,c,d),x),NegQ(Times(d,Power(c,CN1))),GtQ(c,C0),GtQ(Subtract(a,Times(b,c,Power(d,CN1))),C0)))),
IIntegrate(329,Integrate(Times(Sqrt(Plus(a_,Times(b_DEFAULT,Sqr(x_)))),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Star(Times(a,Sqrt(Subtract(C1,Times(Sqr(b),Power(x,C4),Power(a,CN2)))),Power(Times(Sqrt(Plus(a,Times(b,Sqr(x)))),Sqrt(Plus(c,Times(d,Sqr(x))))),CN1)),Integrate(Times(Sqrt(Plus(C1,Times(b,Sqr(x),Power(a,CN1)))),Power(Subtract(C1,Times(b,Sqr(x),Power(a,CN1))),CN1D2)),x)),x),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),Not(And(LtQ(Times(a,c),C0),GtQ(Times(a,b),C0)))))),
IIntegrate(330,Integrate(Times(Sqrt(Plus(a_,Times(b_DEFAULT,Sqr(x_)))),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Star(Times(Sqrt(Plus(a,Times(b,Sqr(x)))),Power(Plus(C1,Times(b,Power(a,CN1),Sqr(x))),CN1D2)),Integrate(Times(Sqrt(Plus(C1,Times(b,Power(a,CN1),Sqr(x)))),Power(Plus(c,Times(d,Sqr(x))),CN1D2)),x)),x),And(FreeQ(List(a,b,c,d),x),NegQ(Times(d,Power(c,CN1))),GtQ(c,C0),Not(GtQ(a,C0))))),
IIntegrate(331,Integrate(Times(Sqrt(Plus(a_,Times(b_DEFAULT,Sqr(x_)))),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Star(Times(Sqrt(Plus(C1,Times(d,Power(c,CN1),Sqr(x)))),Power(Plus(c,Times(d,Sqr(x))),CN1D2)),Integrate(Times(Sqrt(Plus(a,Times(b,Sqr(x)))),Power(Plus(C1,Times(d,Power(c,CN1),Sqr(x))),CN1D2)),x)),x),And(FreeQ(List(a,b,c,d),x),NegQ(Times(d,Power(c,CN1))),Not(GtQ(c,C0))))),
IIntegrate(332,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times(Power(Plus(a,Times(b,Sqr(x))),p),Power(Plus(c,Times(d,Sqr(x))),q)),x),x),And(FreeQ(List(a,b,c,d,q),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),IGtQ(p,C0)))),
IIntegrate(333,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Simp(Times(Power(a,p),Power(c,q),x,AppellF1(C1D2,Negate(p),Negate(q),QQ(3L,2L),Times(CN1,b,Sqr(x),Power(a,CN1)),Times(CN1,d,Sqr(x),Power(c,CN1)))),x),And(FreeQ(List(a,b,c,d,p,q),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),Or(IntegerQ(p),GtQ(a,C0)),Or(IntegerQ(q),GtQ(c,C0))))),
IIntegrate(334,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Simp(Star(Times(Power(a,IntPart(p)),Power(Plus(a,Times(b,Sqr(x))),FracPart(p)),Power(Power(Plus(C1,Times(b,Sqr(x),Power(a,CN1))),FracPart(p)),CN1)),Integrate(Times(Power(Plus(C1,Times(b,Sqr(x),Power(a,CN1))),p),Power(Plus(c,Times(d,Sqr(x))),q)),x)),x),And(FreeQ(List(a,b,c,d,p,q),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),Not(Or(IntegerQ(p),GtQ(a,C0)))))),
IIntegrate(335,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(e,x),m),Power(Plus(Times(a,c),Times(b,d,Power(x,C4))),p)),x),And(FreeQ(List(a,b,c,d,e,m,p),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),Or(IntegerQ(p),And(GtQ(a,C0),GtQ(c,C0)))))),
IIntegrate(336,Integrate(Times(Power(x_,C3),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Times(Power(Plus(a,Times(b,Sqr(x))),Plus(p,C1)),Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Power(Times(C4,b,d,Plus(p,C1)),CN1)),x),And(FreeQ(List(a,b,c,d,p),x),EqQ(Plus(Times(b,c),Times(a,d)),C0)))),
IIntegrate(337,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Times(CN1,Power(Times(e,x),Plus(m,C1)),Power(Plus(a,Times(b,Sqr(x))),Plus(p,C1)),Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Power(Times(C4,a,c,e,Plus(p,C1)),CN1)),x),And(FreeQ(List(a,b,c,d,e,m,p),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),EqQ(Plus(m,Times(C4,p),C5),C0)))),
IIntegrate(338,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Star(C1D2,Subst(Integrate(Times(Power(x,Times(C1D2,Subtract(m,C1))),Power(Plus(a,Times(b,x)),p),Power(Plus(c,Times(d,x)),p)),x),x,Sqr(x))),x),And(FreeQ(List(a,b,c,d,p),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),IntegerQ(Times(C1D2,Subtract(m,C1)))))),
IIntegrate(339,Integrate(Times(Power(Times(e_DEFAULT,x_),m_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(e,x),Plus(m,C1)),Power(Plus(a,Times(b,Sqr(x))),p),Power(Plus(c,Times(d,Sqr(x))),p),Power(Times(e,Plus(m,C1)),CN1)),x),Simp(Star(Times(C4,b,d,p,Power(Times(Power(e,C4),Plus(m,C1)),CN1)),Integrate(Times(Power(Times(e,x),Plus(m,C4)),Power(Plus(a,Times(b,Sqr(x))),Subtract(p,C1)),Power(Plus(c,Times(d,Sqr(x))),Subtract(p,C1))),x)),x)),And(FreeQ(List(a,b,c,d,e,m),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),GtQ(p,C0),LtQ(m,CN1)))),
IIntegrate(340,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(e,x),Plus(m,C1)),Power(Plus(a,Times(b,Sqr(x))),p),Power(Plus(c,Times(d,Sqr(x))),p),Power(Times(e,Plus(m,Times(C4,p),C1)),CN1)),x),Simp(Star(Times(C4,a,c,p,Power(Plus(m,Times(C4,p),C1),CN1)),Integrate(Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Sqr(x))),Subtract(p,C1)),Power(Plus(c,Times(d,Sqr(x))),Subtract(p,C1))),x)),x)),And(FreeQ(List(a,b,c,d,e,m),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),GtQ(p,C0),NeQ(Plus(m,Times(C4,p),C1),C0),IntegerQ(Times(C2,m)))))
  );
}
