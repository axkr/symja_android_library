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
class IntRules254 { 
  public static IAST RULES = List( 
IIntegrate(5081,Integrate(Times(Power(Cos(v_),p_DEFAULT),Power(Cos(w_),q_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Times(Power(Cos(v),p),Power(Cos(w),q)),x),x),And(Or(And(PolynomialQ(v,x),PolynomialQ(w,x)),And(BinomialQ(list(v,w),x),IndependentQ(Cancel(Times(v,Power(w,CN1))),x))),IGtQ(p,C0),IGtQ(q,C0)))),
IIntegrate(5082,Integrate(Times(Power(x_,m_DEFAULT),Power(Sin(v_),p_DEFAULT),Power(Sin(w_),q_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(x,m),Times(Power(Sin(v),p),Power(Sin(w),q)),x),x),And(IGtQ(m,C0),IGtQ(p,C0),IGtQ(q,C0),Or(And(PolynomialQ(v,x),PolynomialQ(w,x)),And(BinomialQ(list(v,w),x),IndependentQ(Cancel(Times(v,Power(w,CN1))),x)))))),
IIntegrate(5083,Integrate(Times(Power(Cos(v_),p_DEFAULT),Power(Cos(w_),q_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(x,m),Times(Power(Cos(v),p),Power(Cos(w),q)),x),x),And(IGtQ(m,C0),IGtQ(p,C0),IGtQ(q,C0),Or(And(PolynomialQ(v,x),PolynomialQ(w,x)),And(BinomialQ(list(v,w),x),IndependentQ(Cancel(Times(v,Power(w,CN1))),x)))))),
IIntegrate(5084,Integrate(Times(Power(Cos(w_),p_DEFAULT),u_DEFAULT,Power(Sin(v_),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Power(C2,p),CN1),Integrate(Times(u,Power(Sin(Times(C2,v)),p)),x)),x),And(EqQ(w,v),IntegerQ(p)))),
IIntegrate(5085,Integrate(Times(Power(Cos(w_),q_DEFAULT),Power(Sin(v_),p_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Times(Power(Sin(v),p),Power(Cos(w),q)),x),x),And(IGtQ(p,C0),IGtQ(q,C0),Or(And(PolynomialQ(v,x),PolynomialQ(w,x)),And(BinomialQ(list(v,w),x),IndependentQ(Cancel(Times(v,Power(w,CN1))),x)))))),
IIntegrate(5086,Integrate(Times(Power(Cos(w_),q_DEFAULT),Power(x_,m_DEFAULT),Power(Sin(v_),p_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(x,m),Times(Power(Sin(v),p),Power(Cos(w),q)),x),x),And(IGtQ(m,C0),IGtQ(p,C0),IGtQ(q,C0),Or(And(PolynomialQ(v,x),PolynomialQ(w,x)),And(BinomialQ(list(v,w),x),IndependentQ(Cancel(Times(v,Power(w,CN1))),x)))))),
IIntegrate(5087,Integrate(Times(Sin(v_),Power(Tan(w_),n_DEFAULT)),x_Symbol),
    Condition(Plus(Negate(Integrate(Times(Cos(v),Power(Tan(w),Subtract(n,C1))),x)),Simp(Star(Cos(Subtract(v,w)),Integrate(Times(Sec(w),Power(Tan(w),Subtract(n,C1))),x)),x)),And(GtQ(n,C0),FreeQ(Subtract(v,w),x),NeQ(w,v)))),
IIntegrate(5088,Integrate(Times(Cos(v_),Power(Cot(w_),n_DEFAULT)),x_Symbol),
    Condition(Plus(Negate(Integrate(Times(Sin(v),Power(Cot(w),Subtract(n,C1))),x)),Simp(Star(Cos(Subtract(v,w)),Integrate(Times(Csc(w),Power(Cot(w),Subtract(n,C1))),x)),x)),And(GtQ(n,C0),FreeQ(Subtract(v,w),x),NeQ(w,v)))),
IIntegrate(5089,Integrate(Times(Power(Cot(w_),n_DEFAULT),Sin(v_)),x_Symbol),
    Condition(Plus(Integrate(Times(Cos(v),Power(Cot(w),Subtract(n,C1))),x),Simp(Star(Sin(Subtract(v,w)),Integrate(Times(Csc(w),Power(Cot(w),Subtract(n,C1))),x)),x)),And(GtQ(n,C0),FreeQ(Subtract(v,w),x),NeQ(w,v)))),
IIntegrate(5090,Integrate(Times(Cos(v_),Power(Tan(w_),n_DEFAULT)),x_Symbol),
    Condition(Subtract(Integrate(Times(Sin(v),Power(Tan(w),Subtract(n,C1))),x),Simp(Star(Sin(Subtract(v,w)),Integrate(Times(Sec(w),Power(Tan(w),Subtract(n,C1))),x)),x)),And(GtQ(n,C0),FreeQ(Subtract(v,w),x),NeQ(w,v)))),
IIntegrate(5091,Integrate(Times(Power(Sec(w_),n_DEFAULT),Sin(v_)),x_Symbol),
    Condition(Plus(Simp(Star(Cos(Subtract(v,w)),Integrate(Times(Tan(w),Power(Sec(w),Subtract(n,C1))),x)),x),Simp(Star(Sin(Subtract(v,w)),Integrate(Power(Sec(w),Subtract(n,C1)),x)),x)),And(GtQ(n,C0),FreeQ(Subtract(v,w),x),NeQ(w,v)))),
IIntegrate(5092,Integrate(Times(Cos(v_),Power(Csc(w_),n_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Star(Cos(Subtract(v,w)),Integrate(Times(Cot(w),Power(Csc(w),Subtract(n,C1))),x)),x),Simp(Star(Sin(Subtract(v,w)),Integrate(Power(Csc(w),Subtract(n,C1)),x)),x)),And(GtQ(n,C0),FreeQ(Subtract(v,w),x),NeQ(w,v)))),
IIntegrate(5093,Integrate(Times(Power(Csc(w_),n_DEFAULT),Sin(v_)),x_Symbol),
    Condition(Plus(Simp(Star(Sin(Subtract(v,w)),Integrate(Times(Cot(w),Power(Csc(w),Subtract(n,C1))),x)),x),Simp(Star(Cos(Subtract(v,w)),Integrate(Power(Csc(w),Subtract(n,C1)),x)),x)),And(GtQ(n,C0),FreeQ(Subtract(v,w),x),NeQ(w,v)))),
IIntegrate(5094,Integrate(Times(Cos(v_),Power(Sec(w_),n_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Star(Negate(Sin(Subtract(v,w))),Integrate(Times(Tan(w),Power(Sec(w),Subtract(n,C1))),x)),x),Simp(Star(Cos(Subtract(v,w)),Integrate(Power(Sec(w),Subtract(n,C1)),x)),x)),And(GtQ(n,C0),FreeQ(Subtract(v,w),x),NeQ(w,v)))),
IIntegrate(5095,Integrate(Times(Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(b,C1D2,Sin(Plus(Times(C2,c),Times(C2,d,x))))),n)),x),FreeQ(List(a,b,c,d,e,f,m,n),x))),
IIntegrate(5096,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(Sin(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),n_)),x_Symbol),
    Condition(Simp(Star(Power(Power(C2,n),CN1),Integrate(Times(Power(x,m),Power(Subtract(Plus(Times(C2,a),b),Times(b,Cos(Plus(Times(C2,c),Times(C2,d,x))))),n)),x)),x),And(FreeQ(List(a,b,c,d),x),NeQ(Plus(a,b),C0),IGtQ(m,C0),ILtQ(n,C0),Or(EqQ(n,CN1),And(EqQ(m,C1),EqQ(n,CN2)))))),
IIntegrate(5097,Integrate(Times(Power(Plus(Times(Sqr(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),b_DEFAULT),a_),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Power(C2,n),CN1),Integrate(Times(Power(x,m),Power(Plus(Times(C2,a),b,Times(b,Cos(Plus(Times(C2,c),Times(C2,d,x))))),n)),x)),x),And(FreeQ(List(a,b,c,d),x),NeQ(Plus(a,b),C0),IGtQ(m,C0),ILtQ(n,C0),Or(EqQ(n,CN1),And(EqQ(m,C1),EqQ(n,CN2)))))),
IIntegrate(5098,Integrate(Times(Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_DEFAULT,Times(Sqr(Cos(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),b_DEFAULT),Times(c_DEFAULT,Sqr(Sin(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))))),CN1)),x_Symbol),
    Condition(Simp(Star(C2,Integrate(Times(Power(Plus(f,Times(g,x)),m),Power(Plus(Times(C2,a),b,c,Times(Subtract(b,c),Cos(Plus(Times(C2,d),Times(C2,e,x))))),CN1)),x)),x),And(FreeQ(List(a,b,c,d,e,f,g),x),IGtQ(m,C0),NeQ(Plus(a,b),C0),NeQ(Plus(a,c),C0)))),
IIntegrate(5099,Integrate(Times(Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),m_DEFAULT),Sqr(Sec(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),Power(Plus(b_,Times(c_DEFAULT,Sqr(Tan(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))))),CN1)),x_Symbol),
    Condition(Simp(Star(C2,Integrate(Times(Power(Plus(f,Times(g,x)),m),Power(Plus(b,c,Times(Subtract(b,c),Cos(Plus(Times(C2,d),Times(C2,e,x))))),CN1)),x)),x),And(FreeQ(List(b,c,d,e,f,g),x),IGtQ(m,C0)))),
IIntegrate(5100,Integrate(Times(Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),m_DEFAULT),Sqr(Sec(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),Power(Plus(b_DEFAULT,Times(a_DEFAULT,Sqr(Sec(Plus(d_DEFAULT,Times(e_DEFAULT,x_))))),Times(c_DEFAULT,Sqr(Tan(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))))),CN1)),x_Symbol),
    Condition(Simp(Star(C2,Integrate(Times(Power(Plus(f,Times(g,x)),m),Power(Plus(Times(C2,a),b,c,Times(Subtract(b,c),Cos(Plus(Times(C2,d),Times(C2,e,x))))),CN1)),x)),x),And(FreeQ(List(a,b,c,d,e,f,g),x),IGtQ(m,C0),NeQ(Plus(a,b),C0),NeQ(Plus(a,c),C0))))
  );
}
