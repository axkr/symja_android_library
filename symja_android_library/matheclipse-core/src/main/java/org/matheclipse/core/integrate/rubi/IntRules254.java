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
class IntRules254 { 
  public static IAST RULES = List( 
IIntegrate(5081,Integrate(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Cot(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Integrate(Subtract(Subtract(Plus(Times(CI,Power(Times(Exp(Times(CI,Plus(a,Times(b,x)))),C2),CN1)),Times(CI,C1D2,Exp(Times(CI,Plus(a,Times(b,x)))))),Times(CI,Power(Times(Exp(Times(CI,Plus(a,Times(b,x)))),Subtract(C1,Exp(Times(C2,CI,Plus(c,Times(d,x)))))),CN1))),Times(CI,Exp(Times(CI,Plus(a,Times(b,x)))),Power(Subtract(C1,Exp(Times(C2,CI,Plus(c,Times(d,x))))),CN1))),x),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(b),Sqr(d)),C0)))),
IIntegrate(5082,Integrate(Times(Cot(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Integrate(Subtract(Plus(Times(C1D2,CN1,Exp(Times(CNI,Plus(a,Times(b,x))))),Times(C1D2,Exp(Times(CI,Plus(a,Times(b,x))))),Power(Times(Exp(Times(CI,Plus(a,Times(b,x)))),Subtract(C1,Exp(Times(C2,CI,Plus(c,Times(d,x)))))),CN1)),Times(Exp(Times(CI,Plus(a,Times(b,x)))),Power(Subtract(C1,Exp(Times(C2,CI,Plus(c,Times(d,x))))),CN1))),x),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(b),Sqr(d)),C0)))),
IIntegrate(5083,Integrate(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Tan(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Integrate(Plus(Times(CNI,Power(Times(Exp(Times(CI,Plus(a,Times(b,x)))),C2),CN1)),Times(CN1,CI,C1D2,Exp(Times(CI,Plus(a,Times(b,x))))),Times(CI,Power(Times(Exp(Times(CI,Plus(a,Times(b,x)))),Plus(C1,Exp(Times(C2,CI,Plus(c,Times(d,x)))))),CN1)),Times(CI,Exp(Times(CI,Plus(a,Times(b,x)))),Power(Plus(C1,Exp(Times(C2,CI,Plus(c,Times(d,x))))),CN1))),x),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(b),Sqr(d)),C0)))),
IIntegrate(5084,Integrate(Power(Sin(Times(a_DEFAULT,Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1))),n_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Negate(Power(d,CN1)),Subst(Integrate(Times(Power(Sin(Times(a,x)),n),Power(x,CN2)),x),x,Power(Plus(c,Times(d,x)),CN1)),x),x),And(FreeQ(list(a,c,d),x),IGtQ(n,C0)))),
IIntegrate(5085,Integrate(Power(Cos(Times(a_DEFAULT,Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1))),n_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Negate(Power(d,CN1)),Subst(Integrate(Times(Power(Cos(Times(a,x)),n),Power(x,CN2)),x),x,Power(Plus(c,Times(d,x)),CN1)),x),x),And(FreeQ(list(a,c,d),x),IGtQ(n,C0)))),
IIntegrate(5086,Integrate(Power(Sin(Times(e_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1))),n_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Negate(Power(d,CN1)),Subst(Integrate(Times(Power(Sin(Subtract(Times(b,e,Power(d,CN1)),Times(e,Subtract(Times(b,c),Times(a,d)),x,Power(d,CN1)))),n),Power(x,CN2)),x),x,Power(Plus(c,Times(d,x)),CN1)),x),x),And(FreeQ(List(a,b,c,d),x),IGtQ(n,C0),NeQ(Subtract(Times(b,c),Times(a,d)),C0)))),
IIntegrate(5087,Integrate(Power(Cos(Times(e_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1))),n_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Negate(Power(d,CN1)),Subst(Integrate(Times(Power(Cos(Subtract(Times(b,e,Power(d,CN1)),Times(e,Subtract(Times(b,c),Times(a,d)),x,Power(d,CN1)))),n),Power(x,CN2)),x),x,Power(Plus(c,Times(d,x)),CN1)),x),x),And(FreeQ(List(a,b,c,d),x),IGtQ(n,C0),NeQ(Subtract(Times(b,c),Times(a,d)),C0)))),
IIntegrate(5088,Integrate(Power(Sin(u_),n_DEFAULT),x_Symbol),
    Condition(Module(list(Set($s("lst"),QuotientOfLinearsParts(u,x))),Integrate(Power(Sin(Times(Plus(Part($s("lst"),C1),Times(Part($s("lst"),C2),x)),Power(Plus(Part($s("lst"),C3),Times(Part($s("lst"),C4),x)),CN1))),n),x)),And(IGtQ(n,C0),QuotientOfLinearsQ(u,x)))),
IIntegrate(5089,Integrate(Power(Cos(u_),n_DEFAULT),x_Symbol),
    Condition(Module(list(Set($s("lst"),QuotientOfLinearsParts(u,x))),Integrate(Power(Cos(Times(Plus(Part($s("lst"),C1),Times(Part($s("lst"),C2),x)),Power(Plus(Part($s("lst"),C3),Times(Part($s("lst"),C4),x)),CN1))),n),x)),And(IGtQ(n,C0),QuotientOfLinearsQ(u,x)))),
IIntegrate(5090,Integrate(Times(u_DEFAULT,Power(Sin(v_),p_DEFAULT),Power(Sin(w_),q_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(u,Power(Sin(v),Plus(p,q))),x),EqQ(w,v))),
IIntegrate(5091,Integrate(Times(Power(Cos(v_),p_DEFAULT),Power(Cos(w_),q_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Integrate(Times(u,Power(Cos(v),Plus(p,q))),x),EqQ(w,v))),
IIntegrate(5092,Integrate(Times(Power(Sin(v_),p_DEFAULT),Power(Sin(w_),q_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Times(Power(Sin(v),p),Power(Sin(w),q)),x),x),And(Or(And(PolynomialQ(v,x),PolynomialQ(w,x)),And(BinomialQ(list(v,w),x),IndependentQ(Cancel(Times(v,Power(w,CN1))),x))),IGtQ(p,C0),IGtQ(q,C0)))),
IIntegrate(5093,Integrate(Times(Power(Cos(v_),p_DEFAULT),Power(Cos(w_),q_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Times(Power(Cos(v),p),Power(Cos(w),q)),x),x),And(Or(And(PolynomialQ(v,x),PolynomialQ(w,x)),And(BinomialQ(list(v,w),x),IndependentQ(Cancel(Times(v,Power(w,CN1))),x))),IGtQ(p,C0),IGtQ(q,C0)))),
IIntegrate(5094,Integrate(Times(Power(x_,m_DEFAULT),Power(Sin(v_),p_DEFAULT),Power(Sin(w_),q_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(x,m),Times(Power(Sin(v),p),Power(Sin(w),q)),x),x),And(IGtQ(m,C0),IGtQ(p,C0),IGtQ(q,C0),Or(And(PolynomialQ(v,x),PolynomialQ(w,x)),And(BinomialQ(list(v,w),x),IndependentQ(Cancel(Times(v,Power(w,CN1))),x)))))),
IIntegrate(5095,Integrate(Times(Power(Cos(v_),p_DEFAULT),Power(Cos(w_),q_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(x,m),Times(Power(Cos(v),p),Power(Cos(w),q)),x),x),And(IGtQ(m,C0),IGtQ(p,C0),IGtQ(q,C0),Or(And(PolynomialQ(v,x),PolynomialQ(w,x)),And(BinomialQ(list(v,w),x),IndependentQ(Cancel(Times(v,Power(w,CN1))),x)))))),
IIntegrate(5096,Integrate(Times(Power(Cos(w_),p_DEFAULT),u_DEFAULT,Power(Sin(v_),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(Power(C2,p),CN1),Integrate(Times(u,Power(Sin(Times(C2,v)),p)),x),x),x),And(EqQ(w,v),IntegerQ(p)))),
IIntegrate(5097,Integrate(Times(Power(Cos(w_),q_DEFAULT),Power(Sin(v_),p_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Times(Power(Sin(v),p),Power(Cos(w),q)),x),x),And(IGtQ(p,C0),IGtQ(q,C0),Or(And(PolynomialQ(v,x),PolynomialQ(w,x)),And(BinomialQ(list(v,w),x),IndependentQ(Cancel(Times(v,Power(w,CN1))),x)))))),
IIntegrate(5098,Integrate(Times(Power(Cos(w_),q_DEFAULT),Power(x_,m_DEFAULT),Power(Sin(v_),p_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(x,m),Times(Power(Sin(v),p),Power(Cos(w),q)),x),x),And(IGtQ(m,C0),IGtQ(p,C0),IGtQ(q,C0),Or(And(PolynomialQ(v,x),PolynomialQ(w,x)),And(BinomialQ(list(v,w),x),IndependentQ(Cancel(Times(v,Power(w,CN1))),x)))))),
IIntegrate(5099,Integrate(Times(Sin(v_),Power(Tan(w_),n_DEFAULT)),x_Symbol),
    Condition(Plus(Negate(Integrate(Times(Cos(v),Power(Tan(w),Plus(n,CN1))),x)),Simp(Dist(Cos(Subtract(v,w)),Integrate(Times(Sec(w),Power(Tan(w),Plus(n,CN1))),x),x),x)),And(GtQ(n,C0),FreeQ(Subtract(v,w),x),NeQ(w,v)))),
IIntegrate(5100,Integrate(Times(Cos(v_),Power(Cot(w_),n_DEFAULT)),x_Symbol),
    Condition(Plus(Negate(Integrate(Times(Sin(v),Power(Cot(w),Plus(n,CN1))),x)),Simp(Dist(Cos(Subtract(v,w)),Integrate(Times(Csc(w),Power(Cot(w),Plus(n,CN1))),x),x),x)),And(GtQ(n,C0),FreeQ(Subtract(v,w),x),NeQ(w,v))))
  );
}
