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
class IntRules12 { 
  public static IAST RULES = List( 
IIntegrate(241,Integrate(Times(x_,Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Times(Power(Plus(a,Times(b,Sqr(x))),Plus(p,C1)),Power(Times(C2,b,Plus(p,C1)),CN1)),x),And(FreeQ(list(a,b,p),x),NeQ(p,CN1)))),
IIntegrate(242,Integrate(Times(Power(Times(c_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Times(Power(Times(c,x),Plus(m,C1)),Power(Plus(a,Times(b,Sqr(x))),Plus(p,C1)),Power(Times(a,c,Plus(m,C1)),CN1)),x),And(FreeQ(List(a,b,c,m,p),x),EqQ(Plus(m,Times(C2,p),C3),C0),NeQ(m,CN1)))),
IIntegrate(243,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Star(C1D2,Subst(Integrate(Times(Power(x,Times(C1D2,Subtract(m,C1))),Power(Plus(a,Times(b,x)),p)),x),x,Sqr(x))),x),And(FreeQ(List(a,b,m,p),x),IntegerQ(Times(C1D2,Subtract(m,C1)))))),
IIntegrate(244,Integrate(Times(Power(Times(c_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times(Power(Times(c,x),m),Power(Plus(a,Times(b,Sqr(x))),p)),x),x),And(FreeQ(List(a,b,c,m),x),IGtQ(p,C0)))),
IIntegrate(245,Integrate(Times(Power(x_,m_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Power(Plus(a,Times(b,Sqr(x))),Plus(p,C1)),Power(Times(a,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,Plus(m,Times(C2,Plus(p,C1)),C1),Power(Times(a,Plus(m,C1)),CN1)),Integrate(Times(Power(x,Plus(m,C2)),Power(Plus(a,Times(b,Sqr(x))),p)),x)),x)),And(FreeQ(List(a,b,m,p),x),ILtQ(Simplify(Plus(Times(C1D2,Plus(m,C1)),p,C1)),C0),NeQ(m,CN1)))),
IIntegrate(246,Integrate(Times(Power(Times(c_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(Times(c,x),Plus(m,C1)),Power(Plus(a,Times(b,Sqr(x))),Plus(p,C1)),Power(Times(a,c,C2,Plus(p,C1)),CN1)),x),Simp(Star(Times(Plus(m,Times(C2,p),C3),Power(Times(a,C2,Plus(p,C1)),CN1)),Integrate(Times(Power(Times(c,x),m),Power(Plus(a,Times(b,Sqr(x))),Plus(p,C1))),x)),x)),And(FreeQ(List(a,b,c,m,p),x),ILtQ(Simplify(Plus(Times(C1D2,Plus(m,C1)),p,C1)),C0),NeQ(p,CN1)))),
IIntegrate(247,Integrate(Times(Power(Times(c_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(c,x),Plus(m,C1)),Power(Plus(a,Times(b,Sqr(x))),p),Power(Times(c,Plus(m,C1)),CN1)),x),Simp(Star(Times(C2,b,p,Power(Times(Sqr(c),Plus(m,C1)),CN1)),Integrate(Times(Power(Times(c,x),Plus(m,C2)),Power(Plus(a,Times(b,Sqr(x))),Subtract(p,C1))),x)),x)),And(FreeQ(list(a,b,c),x),GtQ(p,C0),LtQ(m,CN1),Not(ILtQ(Times(C1D2,Plus(m,Times(C2,p),C3)),C0)),IntBinomialQ(a,b,c,C2,m,p,x)))),
IIntegrate(248,Integrate(Times(Power(Times(c_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(c,x),Plus(m,C1)),Power(Plus(a,Times(b,Sqr(x))),p),Power(Times(c,Plus(m,Times(C2,p),C1)),CN1)),x),Simp(Star(Times(C2,a,p,Power(Plus(m,Times(C2,p),C1),CN1)),Integrate(Times(Power(Times(c,x),m),Power(Plus(a,Times(b,Sqr(x))),Subtract(p,C1))),x)),x)),And(FreeQ(List(a,b,c,m),x),GtQ(p,C0),NeQ(Plus(m,Times(C2,p),C1),C0),IntBinomialQ(a,b,c,C2,m,p,x)))),
IIntegrate(249,Integrate(Times(Sqrt(Times(c_DEFAULT,x_)),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),QQ(-5L,4L))),x_Symbol),
    Condition(Simp(Star(Times(Sqrt(Times(c,x)),Power(Plus(C1,Times(a,Power(Times(b,Sqr(x)),CN1))),C1D4),Power(Times(b,Power(Plus(a,Times(b,Sqr(x))),C1D4)),CN1)),Integrate(Power(Times(Sqr(x),Power(Plus(C1,Times(a,Power(Times(b,Sqr(x)),CN1))),QQ(5L,4L))),CN1),x)),x),And(FreeQ(list(a,b,c),x),PosQ(Times(b,Power(a,CN1)))))),
IIntegrate(250,Integrate(Times(Power(Times(c_DEFAULT,x_),m_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),QQ(-5L,4L))),x_Symbol),
    Condition(Subtract(Simp(Times(C2,c,Power(Times(c,x),Subtract(m,C1)),Power(Times(b,Subtract(Times(C2,m),C3),Power(Plus(a,Times(b,Sqr(x))),C1D4)),CN1)),x),Simp(Star(Times(C2,a,Sqr(c),Subtract(m,C1),Power(Times(b,Subtract(Times(C2,m),C3)),CN1)),Integrate(Times(Power(Times(c,x),Subtract(m,C2)),Power(Plus(a,Times(b,Sqr(x))),QQ(-5L,4L))),x)),x)),And(FreeQ(list(a,b,c),x),PosQ(Times(b,Power(a,CN1))),IntegerQ(Times(C2,m)),GtQ(m,QQ(3L,2L))))),
IIntegrate(251,Integrate(Times(Power(Times(c_DEFAULT,x_),m_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),QQ(-5L,4L))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(c,x),Plus(m,C1)),Power(Times(a,c,Plus(m,C1),Power(Plus(a,Times(b,Sqr(x))),C1D4)),CN1)),x),Simp(Star(Times(b,Plus(Times(C2,m),C1),Power(Times(C2,a,Sqr(c),Plus(m,C1)),CN1)),Integrate(Times(Power(Times(c,x),Plus(m,C2)),Power(Plus(a,Times(b,Sqr(x))),QQ(-5L,4L))),x)),x)),And(FreeQ(list(a,b,c),x),PosQ(Times(b,Power(a,CN1))),IntegerQ(Times(C2,m)),LtQ(m,CN1)))),
IIntegrate(252,Integrate(Times(Power(Times(c_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Subtract(Simp(Times(c,Power(Times(c,x),Subtract(m,C1)),Power(Plus(a,Times(b,Sqr(x))),Plus(p,C1)),Power(Times(C2,b,Plus(p,C1)),CN1)),x),Simp(Star(Times(Sqr(c),Subtract(m,C1),Power(Times(C2,b,Plus(p,C1)),CN1)),Integrate(Times(Power(Times(c,x),Subtract(m,C2)),Power(Plus(a,Times(b,Sqr(x))),Plus(p,C1))),x)),x)),And(FreeQ(list(a,b,c),x),LtQ(p,CN1),GtQ(m,C1),Not(ILtQ(Times(C1D2,Plus(m,Times(C2,p),C3)),C0)),IntBinomialQ(a,b,c,C2,m,p,x)))),
IIntegrate(253,Integrate(Times(Power(Times(c_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(Times(c,x),Plus(m,C1)),Power(Plus(a,Times(b,Sqr(x))),Plus(p,C1)),Power(Times(C2,a,c,Plus(p,C1)),CN1)),x),Simp(Star(Times(Plus(m,Times(C2,p),C3),Power(Times(C2,a,Plus(p,C1)),CN1)),Integrate(Times(Power(Times(c,x),m),Power(Plus(a,Times(b,Sqr(x))),Plus(p,C1))),x)),x)),And(FreeQ(List(a,b,c,m),x),LtQ(p,CN1),IntBinomialQ(a,b,c,C2,m,p,x)))),
IIntegrate(254,Integrate(Times(Power(x_,m_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Integrate(PolynomialDivide(Power(x,m),Plus(a,Times(b,Sqr(x))),x),x),And(FreeQ(list(a,b),x),IGtQ(m,C3)))),
IIntegrate(255,Integrate(Times(Sqrt(Times(c_,x_)),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D4)),x_Symbol),
    Condition(Subtract(Simp(Times(x,Sqrt(Times(c,x)),Power(Plus(a,Times(b,Sqr(x))),CN1D4)),x),Simp(Star(Times(C1D2,a),Integrate(Times(Sqrt(Times(c,x)),Power(Plus(a,Times(b,Sqr(x))),QQ(-5L,4L))),x)),x)),And(FreeQ(list(a,b,c),x),PosQ(Times(b,Power(a,CN1)))))),
IIntegrate(256,Integrate(Times(Sqrt(Times(c_,x_)),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D4)),x_Symbol),
    Condition(Plus(Simp(Times(c,Power(Plus(a,Times(b,Sqr(x))),QQ(3L,4L)),Power(Times(b,Sqrt(Times(c,x))),CN1)),x),Simp(Star(Times(a,Sqr(c),Power(Times(C2,b),CN1)),Integrate(Power(Times(Power(Times(c,x),QQ(3L,2L)),Power(Plus(a,Times(b,Sqr(x))),C1D4)),CN1),x)),x)),And(FreeQ(list(a,b,c),x),NegQ(Times(b,Power(a,CN1)))))),
IIntegrate(257,Integrate(Times(Power(Times(c_DEFAULT,x_),QQ(-3L,2L)),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D4)),x_Symbol),
    Condition(Subtract(Simp(Times(CN2,Power(Times(c,Sqrt(Times(c,x)),Power(Plus(a,Times(b,Sqr(x))),C1D4)),CN1)),x),Simp(Star(Times(b,Power(c,CN2)),Integrate(Times(Sqrt(Times(c,x)),Power(Plus(a,Times(b,Sqr(x))),QQ(-5L,4L))),x)),x)),And(FreeQ(list(a,b,c),x),PosQ(Times(b,Power(a,CN1)))))),
IIntegrate(258,Integrate(Times(Power(Times(c_DEFAULT,x_),QQ(-3L,2L)),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D4)),x_Symbol),
    Condition(Simp(Star(Times(Sqrt(Times(c,x)),Power(Plus(C1,Times(a,Power(Times(b,Sqr(x)),CN1))),C1D4),Power(Times(Sqr(c),Power(Plus(a,Times(b,Sqr(x))),C1D4)),CN1)),Integrate(Power(Times(Sqr(x),Power(Plus(C1,Times(a,Power(Times(b,Sqr(x)),CN1))),C1D4)),CN1),x)),x),And(FreeQ(list(a,b,c),x),NegQ(Times(b,Power(a,CN1)))))),
IIntegrate(259,Integrate(Times(Sqrt(x_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Star(Times(CN2,Power(Times(Sqrt(a),Power(Times(CN1,b,Power(a,CN1)),QQ(3L,4L))),CN1)),Subst(Integrate(Times(Sqrt(Subtract(C1,Times(C2,Sqr(x)))),Power(Subtract(C1,Sqr(x)),CN1D2)),x),x,Times(Sqrt(Subtract(C1,Times(Sqrt(Times(CN1,b,Power(a,CN1))),x))),C1DSqrt2))),x),And(FreeQ(list(a,b),x),GtQ(Times(CN1,b,Power(a,CN1)),C0),GtQ(a,C0)))),
IIntegrate(260,Integrate(Times(Sqrt(x_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Star(Times(Sqrt(Plus(C1,Times(b,Sqr(x),Power(a,CN1)))),Power(Plus(a,Times(b,Sqr(x))),CN1D2)),Integrate(Times(Sqrt(x),Power(Plus(C1,Times(b,Sqr(x),Power(a,CN1))),CN1D2)),x)),x),And(FreeQ(list(a,b),x),GtQ(Times(CN1,b,Power(a,CN1)),C0),Not(GtQ(a,C0)))))
  );
}
