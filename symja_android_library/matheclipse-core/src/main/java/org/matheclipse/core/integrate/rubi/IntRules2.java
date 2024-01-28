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
class IntRules2 { 
  public static IAST RULES = List( 
IIntegrate(41,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),QQ(-3L,2L)),Power(Plus(c_,Times(d_DEFAULT,x_)),QQ(-3L,2L))),x_Symbol),
    Condition(Simp(Times(x,Power(Times(a,c,Sqrt(Plus(a,Times(b,x))),Sqrt(Plus(c,Times(d,x)))),CN1)),x),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Times(b,c),Times(a,d)),C0)))),
IIntegrate(42,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),m_),Power(Plus(c_,Times(d_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,x,Power(Plus(a,Times(b,x)),Plus(m,C1)),Power(Plus(c,Times(d,x)),Plus(m,C1)),Power(Times(C2,a,c,Plus(m,C1)),CN1)),x),Simp(Star(Times(Plus(Times(C2,m),C3),Power(Times(C2,a,c,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(a,Times(b,x)),Plus(m,C1)),Power(Plus(c,Times(d,x)),Plus(m,C1))),x)),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),ILtQ(Plus(m,QQ(3L,2L)),C0)))),
IIntegrate(43,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),CN1D2),Power(Plus(c_,Times(d_DEFAULT,x_)),CN1D2)),x_Symbol),
    Condition(Simp(Times(ArcCosh(Times(b,x,Power(a,CN1))),Power(Times(b,Sqrt(Times(d,Power(b,CN1)))),CN1)),x),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),GtQ(a,C0),GtQ(Times(d,Power(b,CN1)),C0)))),
IIntegrate(44,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),CN1D2),Power(Plus(c_,Times(d_DEFAULT,x_)),CN1D2)),x_Symbol),
    Condition(Simp(Star(Times(C2,Power(Times(b,Sqrt(c)),CN1)),Subst(Integrate(Power(Subtract(C2,Times(Sqr(x),Power(a,CN1))),CN1D2),x),x,Sqrt(Plus(a,Times(b,x))))),x),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),GtQ(c,C0)))),
IIntegrate(45,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),CN1D2),Power(Plus(c_,Times(d_DEFAULT,x_)),CN1D2)),x_Symbol),
    Condition(Simp(Star(C2,Subst(Integrate(Power(Subtract(b,Times(d,Sqr(x))),CN1),x),x,Times(Sqrt(Plus(a,Times(b,x))),Power(Plus(c,Times(d,x)),CN1D2)))),x),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),Not(GtQ(c,C0))))),
IIntegrate(46,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),m_),Power(Plus(c_,Times(d_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Simp(Star(Times(Power(Plus(a,Times(b,x)),FracPart(m)),Power(Plus(c,Times(d,x)),FracPart(m)),Power(Power(Plus(Times(a,c),Times(b,d,Sqr(x))),FracPart(m)),CN1)),Integrate(Power(Plus(Times(a,c),Times(b,d,Sqr(x))),m),x)),x),And(FreeQ(List(a,b,c,d,m),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),Not(IntegerQ(Times(C2,m)))))),
IIntegrate(47,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),CN1),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Subtract(Simp(Star(Times(b,Power(Subtract(Times(b,c),Times(a,d)),CN1)),Integrate(Power(Plus(a,Times(b,x)),CN1),x)),x),Simp(Star(Times(d,Power(Subtract(Times(b,c),Times(a,d)),CN1)),Integrate(Power(Plus(c,Times(d,x)),CN1),x)),x)),FreeQ(List(a,b,c,d),x))),
IIntegrate(48,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),m_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_)),x_Symbol),
    Condition(Simp(Times(Power(Plus(a,Times(b,x)),Plus(m,C1)),Power(Plus(c,Times(d,x)),Plus(n,C1)),Power(Times(Subtract(Times(b,c),Times(a,d)),Plus(m,C1)),CN1)),x),And(FreeQ(List(a,b,c,d,m,n),x),EqQ(Plus(m,n,C2),C0),NeQ(m,CN1)))),
IIntegrate(49,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),m_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times(Power(Plus(a,Times(b,x)),m),Power(Plus(c,Times(d,x)),n)),x),x),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C0),IGtQ(Plus(m,n,C2),C0)))),
IIntegrate(50,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),m_),Power(Plus(c_,Times(d_DEFAULT,x_)),n_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(Times(a,c),Times(b,d,Sqr(x))),m),Power(Times(C2,d,m),CN1)),x),Simp(Star(a,Integrate(Power(Plus(Times(a,c),Times(b,d,Sqr(x))),n),x)),x)),And(FreeQ(List(a,b,c,d,m,n),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),EqQ(Subtract(m,n),C1),GtQ(m,C0),Or(IntegerQ(m),And(GtQ(a,C0),GtQ(c,C0)))))),
IIntegrate(51,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),m_),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(a,Times(b,x)),Plus(m,C1)),Power(Plus(c,Times(d,x)),n),Power(Times(b,Plus(m,C1)),CN1)),x),Simp(Star(Times(d,n,Power(Times(b,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(a,Times(b,x)),Plus(m,C1)),Power(Plus(c,Times(d,x)),Subtract(n,C1))),x)),x)),And(FreeQ(List(a,b,c,d,n),x),ILtQ(m,CN1),FractionQ(n),GtQ(n,C0)))),
IIntegrate(52,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),m_),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(a,Times(b,x)),Plus(m,C1)),Power(Plus(c,Times(d,x)),Plus(n,C1)),Power(Times(Subtract(Times(b,c),Times(a,d)),Plus(m,C1)),CN1)),x),Simp(Star(Times(d,Plus(m,n,C2),Power(Times(Subtract(Times(b,c),Times(a,d)),Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(a,Times(b,x)),Plus(m,C1)),Power(Plus(c,Times(d,x)),n)),x)),x)),And(FreeQ(List(a,b,c,d,n),x),ILtQ(m,CN1),FractionQ(n),LtQ(n,C0)))),
IIntegrate(53,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),m_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times(Power(Plus(a,Times(b,x)),m),Power(Plus(c,Times(d,x)),n)),x),x),And(FreeQ(List(a,b,c,d,n),x),IGtQ(m,C0),Or(Not(IntegerQ(n)),And(EqQ(c,C0),LeQ(Plus(Times(C7,m),Times(C4,n),C4),C0)),LtQ(Plus(Times(C9,m),Times(C5,Plus(n,C1))),C0),GtQ(Plus(m,n,C2),C0))))),
IIntegrate(54,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),m_),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times(Power(Plus(a,Times(b,x)),m),Power(Plus(c,Times(d,x)),n)),x),x),And(FreeQ(List(a,b,c,d),x),ILtQ(m,C0),IntegerQ(n),Not(And(IGtQ(n,C0),LtQ(Plus(m,n,C2),C0)))))),
IIntegrate(55,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),m_),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(a,Times(b,x)),Plus(m,C1)),Power(Plus(c,Times(d,x)),Plus(n,C1)),Power(Times(Subtract(Times(b,c),Times(a,d)),Plus(m,C1)),CN1)),x),Simp(Star(Times(d,Simplify(Plus(m,n,C2)),Power(Times(Subtract(Times(b,c),Times(a,d)),Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(a,Times(b,x)),Simplify(Plus(m,C1))),Power(Plus(c,Times(d,x)),n)),x)),x)),And(FreeQ(List(a,b,c,d,m,n),x),ILtQ(Simplify(Plus(m,n,C2)),C0),NeQ(m,CN1),Not(And(LtQ(m,CN1),LtQ(n,CN1),Or(EqQ(a,C0),And(NeQ(c,C0),LtQ(Subtract(m,n),C0),IntegerQ(n))))),Or(SumSimplerQ(m,C1),Not(SumSimplerQ(n,C1)))))),
IIntegrate(56,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),QQ(-9L,4L)),Power(Plus(c_,Times(d_DEFAULT,x_)),CN1D4)),x_Symbol),
    Condition(Subtract(Simp(Times(CN4,Power(Times(C5,b,Power(Plus(a,Times(b,x)),QQ(5L,4L)),Power(Plus(c,Times(d,x)),C1D4)),CN1)),x),Simp(Star(Times(d,Power(Times(C5,b),CN1)),Integrate(Power(Times(Power(Plus(a,Times(b,x)),QQ(5L,4L)),Power(Plus(c,Times(d,x)),QQ(5L,4L))),CN1),x)),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),NegQ(Times(Sqr(a),Sqr(b)))))),
IIntegrate(57,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),m_),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(a,Times(b,x)),Plus(m,C1)),Power(Plus(c,Times(d,x)),n),Power(Times(b,Plus(m,C1)),CN1)),x),Simp(Star(Times(d,n,Power(Times(b,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(a,Times(b,x)),Plus(m,C1)),Power(Plus(c,Times(d,x)),Subtract(n,C1))),x)),x)),And(FreeQ(List(a,b,c,d),x),GtQ(n,C0),LtQ(m,CN1),Not(And(IntegerQ(n),Not(IntegerQ(m)))),Not(And(ILeQ(Plus(m,n,C2),C0),Or(FractionQ(m),GeQ(Plus(Times(C2,n),m,C1),C0)))),IntLinearQ(a,b,c,d,m,n,x)))),
IIntegrate(58,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),QQ(-5L,4L)),Power(Plus(c_,Times(d_DEFAULT,x_)),CN1D4)),x_Symbol),
    Condition(Plus(Simp(Times(CN2,Power(Times(b,Power(Plus(a,Times(b,x)),C1D4),Power(Plus(c,Times(d,x)),C1D4)),CN1)),x),Simp(Star(c,Integrate(Power(Times(Power(Plus(a,Times(b,x)),QQ(5L,4L)),Power(Plus(c,Times(d,x)),QQ(5L,4L))),CN1),x)),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),NegQ(Times(Sqr(a),Sqr(b)))))),
IIntegrate(59,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),m_),Power(Plus(c_,Times(d_DEFAULT,x_)),n_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(a,Times(b,x)),Plus(m,C1)),Power(Plus(c,Times(d,x)),n),Power(Times(b,Plus(m,n,C1)),CN1)),x),Simp(Star(Times(C2,c,n,Power(Plus(m,n,C1),CN1)),Integrate(Times(Power(Plus(a,Times(b,x)),m),Power(Plus(c,Times(d,x)),Subtract(n,C1))),x)),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),IGtQ(Plus(m,C1D2),C0),IGtQ(Plus(n,C1D2),C0),LtQ(m,n)))),
IIntegrate(60,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),m_),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(a,Times(b,x)),Plus(m,C1)),Power(Plus(c,Times(d,x)),n),Power(Times(b,Plus(m,n,C1)),CN1)),x),Simp(Star(Times(n,Subtract(Times(b,c),Times(a,d)),Power(Times(b,Plus(m,n,C1)),CN1)),Integrate(Times(Power(Plus(a,Times(b,x)),m),Power(Plus(c,Times(d,x)),Subtract(n,C1))),x)),x)),And(FreeQ(List(a,b,c,d),x),GtQ(n,C0),NeQ(Plus(m,n,C1),C0),Not(And(IGtQ(m,C0),Or(Not(IntegerQ(n)),And(GtQ(m,C0),LtQ(Subtract(m,n),C0))))),Not(ILtQ(Plus(m,n,C2),C0)),IntLinearQ(a,b,c,d,m,n,x))))
  );
}
