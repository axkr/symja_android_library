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
class IntRules295 { 
  public static IAST RULES = List( 
IIntegrate(5901,Integrate(Power(Sinh(v_),n_DEFAULT),x_Symbol),
    Condition(Integrate(Power(Sinh(ExpandToSum(v,x)),n),x),And(IGtQ(n,C0),QuadraticQ(v,x),Not(QuadraticMatchQ(v,x))))),
IIntegrate(5902,Integrate(Power(Cosh(v_),n_DEFAULT),x_Symbol),
    Condition(Integrate(Power(Cosh(ExpandToSum(v,x)),n),x),And(IGtQ(n,C0),QuadraticQ(v,x),Not(QuadraticMatchQ(v,x))))),
IIntegrate(5903,Integrate(Times(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Simp(Times(e,Cosh(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(Subtract(Times(b,e),Times(C2,c,d)),C0)))),
IIntegrate(5904,Integrate(Times(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Plus(d_DEFAULT,Times(e_DEFAULT,x_))),x_Symbol),
    Condition(Simp(Times(e,Sinh(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(Subtract(Times(b,e),Times(C2,c,d)),C0)))),
IIntegrate(5905,Integrate(Times(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Subtract(Simp(Times(e,Cosh(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),x),Simp(Star(Times(Subtract(Times(b,e),Times(C2,c,d)),Power(Times(C2,c),CN1)),Integrate(Sinh(Plus(a,Times(b,x),Times(c,Sqr(x)))),x)),x)),And(FreeQ(List(a,b,c,d,e),x),NeQ(Subtract(Times(b,e),Times(C2,c,d)),C0)))),
IIntegrate(5906,Integrate(Times(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Plus(d_DEFAULT,Times(e_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Simp(Times(e,Sinh(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),x),Simp(Star(Times(Subtract(Times(b,e),Times(C2,c,d)),Power(Times(C2,c),CN1)),Integrate(Cosh(Plus(a,Times(b,x),Times(c,Sqr(x)))),x)),x)),And(FreeQ(List(a,b,c,d,e),x),NeQ(Subtract(Times(b,e),Times(C2,c,d)),C0)))),
IIntegrate(5907,Integrate(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_),Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Subtract(Simp(Times(e,Power(Plus(d,Times(e,x)),Subtract(m,C1)),Cosh(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),x),Simp(Star(Times(Sqr(e),Subtract(m,C1),Power(Times(C2,c),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Subtract(m,C2)),Cosh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x)),x)),And(FreeQ(List(a,b,c,d,e),x),GtQ(m,C1),EqQ(Subtract(Times(b,e),Times(C2,c,d)),C0)))),
IIntegrate(5908,Integrate(Times(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Subtract(Simp(Times(e,Power(Plus(d,Times(e,x)),Subtract(m,C1)),Sinh(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),x),Simp(Star(Times(Sqr(e),Subtract(m,C1),Power(Times(C2,c),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Subtract(m,C2)),Sinh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x)),x)),And(FreeQ(List(a,b,c,d,e),x),GtQ(m,C1),EqQ(Subtract(Times(b,e),Times(C2,c,d)),C0)))),
IIntegrate(5909,Integrate(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_),Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Simp(Times(e,Power(Plus(d,Times(e,x)),Subtract(m,C1)),Cosh(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),x),Negate(Simp(Star(Times(Subtract(Times(b,e),Times(C2,c,d)),Power(Times(C2,c),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Subtract(m,C1)),Sinh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x)),x)),Negate(Simp(Star(Times(Sqr(e),Subtract(m,C1),Power(Times(C2,c),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Subtract(m,C2)),Cosh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x)),x))),And(FreeQ(List(a,b,c,d,e),x),GtQ(m,C1),NeQ(Subtract(Times(b,e),Times(C2,c,d)),C0)))),
IIntegrate(5910,Integrate(Times(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Plus(Simp(Times(e,Power(Plus(d,Times(e,x)),Subtract(m,C1)),Sinh(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),x),Negate(Simp(Star(Times(Subtract(Times(b,e),Times(C2,c,d)),Power(Times(C2,c),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Subtract(m,C1)),Cosh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x)),x)),Negate(Simp(Star(Times(Sqr(e),Subtract(m,C1),Power(Times(C2,c),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Subtract(m,C2)),Sinh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x)),x))),And(FreeQ(List(a,b,c,d,e),x),GtQ(m,C1),NeQ(Subtract(Times(b,e),Times(C2,c,d)),C0)))),
IIntegrate(5911,Integrate(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_),Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Sinh(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(e,Plus(m,C1)),CN1)),x),Simp(Star(Times(C2,c,Power(Times(Sqr(e),Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Plus(m,C2)),Cosh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x)),x)),And(FreeQ(List(a,b,c,d,e),x),LtQ(m,CN1),EqQ(Subtract(Times(b,e),Times(C2,c,d)),C0)))),
IIntegrate(5912,Integrate(Times(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Cosh(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(e,Plus(m,C1)),CN1)),x),Simp(Star(Times(C2,c,Power(Times(Sqr(e),Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Plus(m,C2)),Sinh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x)),x)),And(FreeQ(List(a,b,c,d,e),x),LtQ(m,CN1),EqQ(Subtract(Times(b,e),Times(C2,c,d)),C0)))),
IIntegrate(5913,Integrate(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_),Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Sinh(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(e,Plus(m,C1)),CN1)),x),Negate(Simp(Star(Times(Subtract(Times(b,e),Times(C2,c,d)),Power(Times(Sqr(e),Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Cosh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x)),x)),Negate(Simp(Star(Times(C2,c,Power(Times(Sqr(e),Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Plus(m,C2)),Cosh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x)),x))),And(FreeQ(List(a,b,c,d,e),x),LtQ(m,CN1),NeQ(Subtract(Times(b,e),Times(C2,c,d)),C0)))),
IIntegrate(5914,Integrate(Times(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Cosh(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(e,Plus(m,C1)),CN1)),x),Negate(Simp(Star(Times(Subtract(Times(b,e),Times(C2,c,d)),Power(Times(Sqr(e),Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Sinh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x)),x)),Negate(Simp(Star(Times(C2,c,Power(Times(Sqr(e),Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Plus(m,C2)),Sinh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x)),x))),And(FreeQ(List(a,b,c,d,e),x),LtQ(m,CN1),NeQ(Subtract(Times(b,e),Times(C2,c,d)),C0)))),
IIntegrate(5915,Integrate(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT),Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(d,Times(e,x)),m),Sinh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x),FreeQ(List(a,b,c,d,e,m),x))),
IIntegrate(5916,Integrate(Times(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(d,Times(e,x)),m),Cosh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x),FreeQ(List(a,b,c,d,e,m),x))),
IIntegrate(5917,Integrate(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT),Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(Plus(d,Times(e,x)),m),Power(Sinh(Plus(a,Times(b,x),Times(c,Sqr(x)))),n),x),x),And(FreeQ(List(a,b,c,d,e,m),x),IGtQ(n,C1)))),
IIntegrate(5918,Integrate(Times(Power(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(Plus(d,Times(e,x)),m),Power(Cosh(Plus(a,Times(b,x),Times(c,Sqr(x)))),n),x),x),And(FreeQ(List(a,b,c,d,e,m),x),IGtQ(n,C1)))),
IIntegrate(5919,Integrate(Times(Power(u_,m_DEFAULT),Power(Sinh(v_),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(u,x),m),Power(Sinh(ExpandToSum(v,x)),n)),x),And(FreeQ(m,x),IGtQ(n,C0),LinearQ(u,x),QuadraticQ(v,x),Not(And(LinearMatchQ(u,x),QuadraticMatchQ(v,x)))))),
IIntegrate(5920,Integrate(Times(Power(Cosh(v_),n_DEFAULT),Power(u_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(u,x),m),Power(Cosh(ExpandToSum(v,x)),n)),x),And(FreeQ(m,x),IGtQ(n,C0),LinearQ(u,x),QuadraticQ(v,x),Not(And(LinearMatchQ(u,x),QuadraticMatchQ(v,x))))))
  );
}
