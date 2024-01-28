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
class IntRules307 { 
  public static IAST RULES = List( 
IIntegrate(6141,Integrate(Power(Sinh(Times(e_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1))),n_DEFAULT),x_Symbol),
    Condition(Simp(Star(Negate(Power(d,CN1)),Subst(Integrate(Times(Power(Sinh(Subtract(Times(b,e,Power(d,CN1)),Times(e,Subtract(Times(b,c),Times(a,d)),x,Power(d,CN1)))),n),Power(x,CN2)),x),x,Power(Plus(c,Times(d,x)),CN1))),x),And(FreeQ(List(a,b,c,d),x),IGtQ(n,C0),NeQ(Subtract(Times(b,c),Times(a,d)),C0)))),
IIntegrate(6142,Integrate(Power(Cosh(Times(e_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1))),n_DEFAULT),x_Symbol),
    Condition(Simp(Star(Negate(Power(d,CN1)),Subst(Integrate(Times(Power(Cosh(Subtract(Times(b,e,Power(d,CN1)),Times(e,Subtract(Times(b,c),Times(a,d)),x,Power(d,CN1)))),n),Power(x,CN2)),x),x,Power(Plus(c,Times(d,x)),CN1))),x),And(FreeQ(List(a,b,c,d),x),IGtQ(n,C0),NeQ(Subtract(Times(b,c),Times(a,d)),C0)))),
IIntegrate(6143,Integrate(Power(Sinh(u_),n_DEFAULT),x_Symbol),
    Condition(With(list(Set($s("lst"),QuotientOfLinearsParts(u,x))),Integrate(Power(Sinh(Times(Plus(Part($s("lst"),C1),Times(Part($s("lst"),C2),x)),Power(Plus(Part($s("lst"),C3),Times(Part($s("lst"),C4),x)),CN1))),n),x)),And(IGtQ(n,C0),QuotientOfLinearsQ(u,x)))),
IIntegrate(6144,Integrate(Power(Cosh(u_),n_DEFAULT),x_Symbol),
    Condition(With(list(Set($s("lst"),QuotientOfLinearsParts(u,x))),Integrate(Power(Cosh(Times(Plus(Part($s("lst"),C1),Times(Part($s("lst"),C2),x)),Power(Plus(Part($s("lst"),C3),Times(Part($s("lst"),C4),x)),CN1))),n),x)),And(IGtQ(n,C0),QuotientOfLinearsQ(u,x)))),
IIntegrate(6145,Integrate(Times(u_DEFAULT,Power(Sinh(v_),p_DEFAULT),Power(Sinh(w_),q_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(u,Power(Sinh(v),Plus(p,q))),x),EqQ(w,v))),
IIntegrate(6146,Integrate(Times(Power(Cosh(v_),p_DEFAULT),Power(Cosh(w_),q_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Integrate(Times(u,Power(Cosh(v),Plus(p,q))),x),EqQ(w,v))),
IIntegrate(6147,Integrate(Times(Power(Sinh(v_),p_DEFAULT),Power(Sinh(w_),q_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Times(Power(Sinh(v),p),Power(Sinh(w),q)),x),x),And(IGtQ(p,C0),IGtQ(q,C0),Or(And(PolynomialQ(v,x),PolynomialQ(w,x)),And(BinomialQ(list(v,w),x),IndependentQ(Cancel(Times(v,Power(w,CN1))),x)))))),
IIntegrate(6148,Integrate(Times(Power(Cosh(v_),p_DEFAULT),Power(Cosh(w_),q_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Times(Power(Cosh(v),p),Power(Cosh(w),q)),x),x),And(IGtQ(p,C0),IGtQ(q,C0),Or(And(PolynomialQ(v,x),PolynomialQ(w,x)),And(BinomialQ(list(v,w),x),IndependentQ(Cancel(Times(v,Power(w,CN1))),x)))))),
IIntegrate(6149,Integrate(Times(Power(x_,m_DEFAULT),Power(Sinh(v_),p_DEFAULT),Power(Sinh(w_),q_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(x,m),Times(Power(Sinh(v),p),Power(Sinh(w),q)),x),x),And(IGtQ(m,C0),IGtQ(p,C0),IGtQ(q,C0),Or(And(PolynomialQ(v,x),PolynomialQ(w,x)),And(BinomialQ(list(v,w),x),IndependentQ(Cancel(Times(v,Power(w,CN1))),x)))))),
IIntegrate(6150,Integrate(Times(Power(Cosh(v_),p_DEFAULT),Power(Cosh(w_),q_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(x,m),Times(Power(Cosh(v),p),Power(Cosh(w),q)),x),x),And(IGtQ(m,C0),IGtQ(p,C0),IGtQ(q,C0),Or(And(PolynomialQ(v,x),PolynomialQ(w,x)),And(BinomialQ(list(v,w),x),IndependentQ(Cancel(Times(v,Power(w,CN1))),x)))))),
IIntegrate(6151,Integrate(Times(Power(Cosh(w_),p_DEFAULT),u_DEFAULT,Power(Sinh(v_),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Power(C2,p),CN1),Integrate(Times(u,Power(Sinh(Times(C2,v)),p)),x)),x),And(EqQ(w,v),IntegerQ(p)))),
IIntegrate(6152,Integrate(Times(Power(Cosh(w_),q_DEFAULT),Power(Sinh(v_),p_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Times(Power(Sinh(v),p),Power(Cosh(w),q)),x),x),And(IGtQ(p,C0),IGtQ(q,C0),Or(And(PolynomialQ(v,x),PolynomialQ(w,x)),And(BinomialQ(list(v,w),x),IndependentQ(Cancel(Times(v,Power(w,CN1))),x)))))),
IIntegrate(6153,Integrate(Times(Power(Cosh(w_),q_DEFAULT),Power(x_,m_DEFAULT),Power(Sinh(v_),p_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(x,m),Times(Power(Sinh(v),p),Power(Cosh(w),q)),x),x),And(IGtQ(m,C0),IGtQ(p,C0),IGtQ(q,C0),Or(And(PolynomialQ(v,x),PolynomialQ(w,x)),And(BinomialQ(list(v,w),x),IndependentQ(Cancel(Times(v,Power(w,CN1))),x)))))),
IIntegrate(6154,Integrate(Times(Sinh(v_),Power(Tanh(w_),n_DEFAULT)),x_Symbol),
    Condition(Subtract(Integrate(Times(Cosh(v),Power(Tanh(w),Subtract(n,C1))),x),Simp(Star(Cosh(Subtract(v,w)),Integrate(Times(Sech(w),Power(Tanh(w),Subtract(n,C1))),x)),x)),And(GtQ(n,C0),NeQ(w,v),FreeQ(Subtract(v,w),x)))),
IIntegrate(6155,Integrate(Times(Cosh(v_),Power(Coth(w_),n_DEFAULT)),x_Symbol),
    Condition(Plus(Integrate(Times(Sinh(v),Power(Coth(w),Subtract(n,C1))),x),Simp(Star(Cosh(Subtract(v,w)),Integrate(Times(Csch(w),Power(Coth(w),Subtract(n,C1))),x)),x)),And(GtQ(n,C0),NeQ(w,v),FreeQ(Subtract(v,w),x)))),
IIntegrate(6156,Integrate(Times(Power(Coth(w_),n_DEFAULT),Sinh(v_)),x_Symbol),
    Condition(Plus(Integrate(Times(Cosh(v),Power(Coth(w),Subtract(n,C1))),x),Simp(Star(Sinh(Subtract(v,w)),Integrate(Times(Csch(w),Power(Coth(w),Subtract(n,C1))),x)),x)),And(GtQ(n,C0),NeQ(w,v),FreeQ(Subtract(v,w),x)))),
IIntegrate(6157,Integrate(Times(Cosh(v_),Power(Tanh(w_),n_DEFAULT)),x_Symbol),
    Condition(Subtract(Integrate(Times(Sinh(v),Power(Tanh(w),Subtract(n,C1))),x),Simp(Star(Sinh(Subtract(v,w)),Integrate(Times(Sech(w),Power(Tanh(w),Subtract(n,C1))),x)),x)),And(GtQ(n,C0),NeQ(w,v),FreeQ(Subtract(v,w),x)))),
IIntegrate(6158,Integrate(Times(Power(Sech(w_),n_DEFAULT),Sinh(v_)),x_Symbol),
    Condition(Plus(Simp(Star(Cosh(Subtract(v,w)),Integrate(Times(Tanh(w),Power(Sech(w),Subtract(n,C1))),x)),x),Simp(Star(Sinh(Subtract(v,w)),Integrate(Power(Sech(w),Subtract(n,C1)),x)),x)),And(GtQ(n,C0),NeQ(w,v),FreeQ(Subtract(v,w),x)))),
IIntegrate(6159,Integrate(Times(Cosh(v_),Power(Csch(w_),n_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Star(Cosh(Subtract(v,w)),Integrate(Times(Coth(w),Power(Csch(w),Subtract(n,C1))),x)),x),Simp(Star(Sinh(Subtract(v,w)),Integrate(Power(Csch(w),Subtract(n,C1)),x)),x)),And(GtQ(n,C0),NeQ(w,v),FreeQ(Subtract(v,w),x)))),
IIntegrate(6160,Integrate(Times(Power(Csch(w_),n_DEFAULT),Sinh(v_)),x_Symbol),
    Condition(Plus(Simp(Star(Sinh(Subtract(v,w)),Integrate(Times(Coth(w),Power(Csch(w),Subtract(n,C1))),x)),x),Simp(Star(Cosh(Subtract(v,w)),Integrate(Power(Csch(w),Subtract(n,C1)),x)),x)),And(GtQ(n,C0),NeQ(w,v),FreeQ(Subtract(v,w),x))))
  );
}
