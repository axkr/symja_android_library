package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IAST;
import com.google.common.base.Supplier;

/** 
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class IntRules308 { 
  public static IAST RULES = List( 
IIntegrate(6161,Integrate(Times(Power(x_,m_DEFAULT),Power(Sinh(v_),p_DEFAULT),Power(Sinh(w_),q_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(x,m),Times(Power(Sinh(v),p),Power(Sinh(w),q)),x),x),And(IGtQ(m,C0),IGtQ(p,C0),IGtQ(q,C0),Or(And(PolynomialQ(v,x),PolynomialQ(w,x)),And(BinomialQ(list(v,w),x),IndependentQ(Cancel(Times(v,Power(w,CN1))),x)))))),
IIntegrate(6162,Integrate(Times(Power(Cosh(v_),p_DEFAULT),Power(Cosh(w_),q_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(x,m),Times(Power(Cosh(v),p),Power(Cosh(w),q)),x),x),And(IGtQ(m,C0),IGtQ(p,C0),IGtQ(q,C0),Or(And(PolynomialQ(v,x),PolynomialQ(w,x)),And(BinomialQ(list(v,w),x),IndependentQ(Cancel(Times(v,Power(w,CN1))),x)))))),
IIntegrate(6163,Integrate(Times(Power(Cosh(w_),p_DEFAULT),u_DEFAULT,Power(Sinh(v_),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(Power(C2,p),CN1),Integrate(Times(u,Power(Sinh(Times(C2,v)),p)),x),x),x),And(EqQ(w,v),IntegerQ(p)))),
IIntegrate(6164,Integrate(Times(Power(Cosh(w_),q_DEFAULT),Power(Sinh(v_),p_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Times(Power(Sinh(v),p),Power(Cosh(w),q)),x),x),And(IGtQ(p,C0),IGtQ(q,C0),Or(And(PolynomialQ(v,x),PolynomialQ(w,x)),And(BinomialQ(list(v,w),x),IndependentQ(Cancel(Times(v,Power(w,CN1))),x)))))),
IIntegrate(6165,Integrate(Times(Power(Cosh(w_),q_DEFAULT),Power(x_,m_DEFAULT),Power(Sinh(v_),p_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(x,m),Times(Power(Sinh(v),p),Power(Cosh(w),q)),x),x),And(IGtQ(m,C0),IGtQ(p,C0),IGtQ(q,C0),Or(And(PolynomialQ(v,x),PolynomialQ(w,x)),And(BinomialQ(list(v,w),x),IndependentQ(Cancel(Times(v,Power(w,CN1))),x)))))),
IIntegrate(6166,Integrate(Times(Sinh(v_),Power(Tanh(w_),n_DEFAULT)),x_Symbol),
    Condition(Subtract(Integrate(Times(Cosh(v),Power(Tanh(w),Plus(n,CN1))),x),Simp(Dist(Cosh(Subtract(v,w)),Integrate(Times(Sech(w),Power(Tanh(w),Plus(n,CN1))),x),x),x)),And(GtQ(n,C0),NeQ(w,v),FreeQ(Subtract(v,w),x)))),
IIntegrate(6167,Integrate(Times(Cosh(v_),Power(Coth(w_),n_DEFAULT)),x_Symbol),
    Condition(Plus(Integrate(Times(Sinh(v),Power(Coth(w),Plus(n,CN1))),x),Simp(Dist(Cosh(Subtract(v,w)),Integrate(Times(Csch(w),Power(Coth(w),Plus(n,CN1))),x),x),x)),And(GtQ(n,C0),NeQ(w,v),FreeQ(Subtract(v,w),x)))),
IIntegrate(6168,Integrate(Times(Power(Coth(w_),n_DEFAULT),Sinh(v_)),x_Symbol),
    Condition(Plus(Integrate(Times(Cosh(v),Power(Coth(w),Plus(n,CN1))),x),Simp(Dist(Sinh(Subtract(v,w)),Integrate(Times(Csch(w),Power(Coth(w),Plus(n,CN1))),x),x),x)),And(GtQ(n,C0),NeQ(w,v),FreeQ(Subtract(v,w),x)))),
IIntegrate(6169,Integrate(Times(Cosh(v_),Power(Tanh(w_),n_DEFAULT)),x_Symbol),
    Condition(Subtract(Integrate(Times(Sinh(v),Power(Tanh(w),Plus(n,CN1))),x),Simp(Dist(Sinh(Subtract(v,w)),Integrate(Times(Sech(w),Power(Tanh(w),Plus(n,CN1))),x),x),x)),And(GtQ(n,C0),NeQ(w,v),FreeQ(Subtract(v,w),x)))),
IIntegrate(6170,Integrate(Times(Power(Sech(w_),n_DEFAULT),Sinh(v_)),x_Symbol),
    Condition(Plus(Simp(Dist(Cosh(Subtract(v,w)),Integrate(Times(Tanh(w),Power(Sech(w),Plus(n,CN1))),x),x),x),Simp(Dist(Sinh(Subtract(v,w)),Integrate(Power(Sech(w),Plus(n,CN1)),x),x),x)),And(GtQ(n,C0),NeQ(w,v),FreeQ(Subtract(v,w),x)))),
IIntegrate(6171,Integrate(Times(Cosh(v_),Power(Csch(w_),n_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Dist(Cosh(Subtract(v,w)),Integrate(Times(Coth(w),Power(Csch(w),Plus(n,CN1))),x),x),x),Simp(Dist(Sinh(Subtract(v,w)),Integrate(Power(Csch(w),Plus(n,CN1)),x),x),x)),And(GtQ(n,C0),NeQ(w,v),FreeQ(Subtract(v,w),x)))),
IIntegrate(6172,Integrate(Times(Power(Csch(w_),n_DEFAULT),Sinh(v_)),x_Symbol),
    Condition(Plus(Simp(Dist(Sinh(Subtract(v,w)),Integrate(Times(Coth(w),Power(Csch(w),Plus(n,CN1))),x),x),x),Simp(Dist(Cosh(Subtract(v,w)),Integrate(Power(Csch(w),Plus(n,CN1)),x),x),x)),And(GtQ(n,C0),NeQ(w,v),FreeQ(Subtract(v,w),x)))),
IIntegrate(6173,Integrate(Times(Cosh(v_),Power(Sech(w_),n_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Dist(Sinh(Subtract(v,w)),Integrate(Times(Tanh(w),Power(Sech(w),Plus(n,CN1))),x),x),x),Simp(Dist(Cosh(Subtract(v,w)),Integrate(Power(Sech(w),Plus(n,CN1)),x),x),x)),And(GtQ(n,C0),NeQ(w,v),FreeQ(Subtract(v,w),x)))),
IIntegrate(6174,Integrate(Times(Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(b,C1D2,Sinh(Plus(Times(C2,c),Times(C2,d,x))))),n)),x),FreeQ(List(a,b,c,d,e,f,m,n),x))),
IIntegrate(6175,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),n_)),x_Symbol),
    Condition(Simp(Dist(Power(Power(C2,n),CN1),Integrate(Times(Power(x,m),Power(Plus(Times(C2,a),Negate(b),Times(b,Cosh(Plus(Times(C2,c),Times(C2,d,x))))),n)),x),x),x),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(a,b),C0),IGtQ(m,C0),ILtQ(n,C0),Or(EqQ(n,CN1),And(EqQ(m,C1),EqQ(n,CN2)))))),
IIntegrate(6176,Integrate(Times(Power(Plus(Times(Sqr(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),b_DEFAULT),a_),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(Power(C2,n),CN1),Integrate(Times(Power(x,m),Power(Plus(Times(C2,a),b,Times(b,Cosh(Plus(Times(C2,c),Times(C2,d,x))))),n)),x),x),x),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(a,b),C0),IGtQ(m,C0),ILtQ(n,C0),Or(EqQ(n,CN1),And(EqQ(m,C1),EqQ(n,CN2)))))),
IIntegrate(6177,Integrate(Times(Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_DEFAULT,Times(Sqr(Cosh(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),b_DEFAULT),Times(c_DEFAULT,Sqr(Sinh(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))))),CN1)),x_Symbol),
    Condition(Simp(Dist(C2,Integrate(Times(Power(Plus(f,Times(g,x)),m),Power(Plus(Times(C2,a),b,Negate(c),Times(Plus(b,c),Cosh(Plus(Times(C2,d),Times(C2,e,x))))),CN1)),x),x),x),And(FreeQ(List(a,b,c,d,e,f,g),x),IGtQ(m,C0),NeQ(Plus(a,b),C0),NeQ(Plus(a,c),C0)))),
IIntegrate(6178,Integrate(Times(Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),m_DEFAULT),Sqr(Sech(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),Power(Plus(b_,Times(c_DEFAULT,Sqr(Tanh(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))))),CN1)),x_Symbol),
    Condition(Simp(Dist(C2,Integrate(Times(Power(Plus(f,Times(g,x)),m),Power(Plus(b,Negate(c),Times(Plus(b,c),Cosh(Plus(Times(C2,d),Times(C2,e,x))))),CN1)),x),x),x),And(FreeQ(List(b,c,d,e,f,g),x),IGtQ(m,C0)))),
IIntegrate(6179,Integrate(Times(Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),m_DEFAULT),Sqr(Sech(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),Power(Plus(b_DEFAULT,Times(a_DEFAULT,Sqr(Sech(Plus(d_DEFAULT,Times(e_DEFAULT,x_))))),Times(c_DEFAULT,Sqr(Tanh(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))))),CN1)),x_Symbol),
    Condition(Simp(Dist(C2,Integrate(Times(Power(Plus(f,Times(g,x)),m),Power(Plus(Times(C2,a),b,Negate(c),Times(Plus(b,c),Cosh(Plus(Times(C2,d),Times(C2,e,x))))),CN1)),x),x),x),And(FreeQ(List(a,b,c,d,e,f,g),x),IGtQ(m,C0),NeQ(Plus(a,b),C0),NeQ(Plus(a,c),C0)))),
IIntegrate(6180,Integrate(Times(Sqr(Csch(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),Power(Plus(Times(Sqr(Coth(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),b_DEFAULT),c_),CN1),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(C2,Integrate(Times(Power(Plus(f,Times(g,x)),m),Power(Plus(b,Negate(c),Times(Plus(b,c),Cosh(Plus(Times(C2,d),Times(C2,e,x))))),CN1)),x),x),x),And(FreeQ(List(b,c,d,e,f,g),x),IGtQ(m,C0))))
  );
}
