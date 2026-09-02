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
class IntRules307 { 
  public static IAST RULES = List( 
IIntegrate(6141,Integrate(Times(Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sech(Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1),Power($(F_,Plus(c_DEFAULT,Times(d_DEFAULT,x_))),n_DEFAULT),Power($(G_,Plus(c_DEFAULT,Times(d_DEFAULT,x_))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Plus(e,Times(f,x)),m),Cosh(Plus(c,Times(d,x))),Power(F(Plus(c,Times(d,x))),n),Power(G(Plus(c,Times(d,x))),p),Power(Plus(b,Times(a,Cosh(Plus(c,Times(d,x))))),CN1)),x),And(FreeQ(List(a,b,c,d,e,f),x),HyperbolicQ(FSymbol),HyperbolicQ(GSymbol),IntegersQ(m,n,p)))),
IIntegrate(6142,Integrate(Times(Power(Plus(Times(Csch(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_),CN1),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power($(F_,Plus(c_DEFAULT,Times(d_DEFAULT,x_))),n_DEFAULT),Power($(G_,Plus(c_DEFAULT,Times(d_DEFAULT,x_))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Plus(e,Times(f,x)),m),Sinh(Plus(c,Times(d,x))),Power(F(Plus(c,Times(d,x))),n),Power(G(Plus(c,Times(d,x))),p),Power(Plus(b,Times(a,Sinh(Plus(c,Times(d,x))))),CN1)),x),And(FreeQ(List(a,b,c,d,e,f),x),HyperbolicQ(FSymbol),HyperbolicQ(GSymbol),IntegersQ(m,n,p)))),
IIntegrate(6143,Integrate(Times(Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT),Power(Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),q_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(Power(C2,Plus(p,q)),CN1),Integrate(ExpandIntegrand(Power(Plus(Negate(Exp(Subtract(Negate(c),Times(d,x)))),Exp(Plus(c,Times(d,x)))),q),Power(Plus(Negate(Exp(Subtract(Negate(a),Times(b,x)))),Exp(Plus(a,Times(b,x)))),p),x),x),x),x),And(FreeQ(List(a,b,c,d,q),x),IGtQ(p,C0),Not(IntegerQ(q))))),
IIntegrate(6144,Integrate(Times(Power(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT),Power(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),q_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(Power(C2,Plus(p,q)),CN1),Integrate(ExpandIntegrand(Power(Plus(Exp(Subtract(Negate(c),Times(d,x))),Exp(Plus(c,Times(d,x)))),q),Power(Plus(Exp(Subtract(Negate(a),Times(b,x))),Exp(Plus(a,Times(b,x)))),p),x),x),x),x),And(FreeQ(List(a,b,c,d,q),x),IGtQ(p,C0),Not(IntegerQ(q))))),
IIntegrate(6145,Integrate(Times(Power(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),q_DEFAULT),Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(Power(C2,Plus(p,q)),CN1),Integrate(ExpandIntegrand(Power(Plus(Exp(Subtract(Negate(c),Times(d,x))),Exp(Plus(c,Times(d,x)))),q),Power(Plus(Negate(Exp(Subtract(Negate(a),Times(b,x)))),Exp(Plus(a,Times(b,x)))),p),x),x),x),x),And(FreeQ(List(a,b,c,d,q),x),IGtQ(p,C0),Not(IntegerQ(q))))),
IIntegrate(6146,Integrate(Times(Power(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT),Power(Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),q_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(Power(C2,Plus(p,q)),CN1),Integrate(ExpandIntegrand(Power(Plus(Negate(Exp(Subtract(Negate(c),Times(d,x)))),Exp(Plus(c,Times(d,x)))),q),Power(Plus(Exp(Subtract(Negate(a),Times(b,x))),Exp(Plus(a,Times(b,x)))),p),x),x),x),x),And(FreeQ(List(a,b,c,d,q),x),IGtQ(p,C0),Not(IntegerQ(q))))),
IIntegrate(6147,Integrate(Times(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Tanh(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Integrate(Subtract(Plus(Times(C1D2,CN1,Exp(Negate(Plus(a,Times(b,x))))),Times(C1D2,Exp(Plus(a,Times(b,x)))),Power(Times(Exp(Plus(a,Times(b,x))),Plus(C1,Exp(Times(C2,Plus(c,Times(d,x)))))),CN1)),Times(Exp(Plus(a,Times(b,x))),Power(Plus(C1,Exp(Times(C2,Plus(c,Times(d,x))))),CN1))),x),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(b),Sqr(d)),C0)))),
IIntegrate(6148,Integrate(Times(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Coth(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Integrate(Subtract(Subtract(Plus(Power(Times(Exp(Plus(a,Times(b,x))),C2),CN1),Times(C1D2,Exp(Plus(a,Times(b,x))))),Power(Times(Exp(Plus(a,Times(b,x))),Subtract(C1,Exp(Times(C2,Plus(c,Times(d,x)))))),CN1)),Times(Exp(Plus(a,Times(b,x))),Power(Subtract(C1,Exp(Times(C2,Plus(c,Times(d,x))))),CN1))),x),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(b),Sqr(d)),C0)))),
IIntegrate(6149,Integrate(Times(Coth(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Integrate(Subtract(Plus(Times(C1D2,CN1,Exp(Negate(Plus(a,Times(b,x))))),Times(C1D2,Exp(Plus(a,Times(b,x)))),Power(Times(Exp(Plus(a,Times(b,x))),Subtract(C1,Exp(Times(C2,Plus(c,Times(d,x)))))),CN1)),Times(Exp(Plus(a,Times(b,x))),Power(Subtract(C1,Exp(Times(C2,Plus(c,Times(d,x))))),CN1))),x),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(b),Sqr(d)),C0)))),
IIntegrate(6150,Integrate(Times(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Tanh(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Integrate(Subtract(Subtract(Plus(Power(Times(Exp(Plus(a,Times(b,x))),C2),CN1),Times(C1D2,Exp(Plus(a,Times(b,x))))),Power(Times(Exp(Plus(a,Times(b,x))),Plus(C1,Exp(Times(C2,Plus(c,Times(d,x)))))),CN1)),Times(Exp(Plus(a,Times(b,x))),Power(Plus(C1,Exp(Times(C2,Plus(c,Times(d,x))))),CN1))),x),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(b),Sqr(d)),C0)))),
IIntegrate(6151,Integrate(Power(Sinh(Times(a_DEFAULT,Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1))),n_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Negate(Power(d,CN1)),Subst(Integrate(Times(Power(Sinh(Times(a,x)),n),Power(x,CN2)),x),x,Power(Plus(c,Times(d,x)),CN1)),x),x),And(FreeQ(list(a,c,d),x),IGtQ(n,C0)))),
IIntegrate(6152,Integrate(Power(Cosh(Times(a_DEFAULT,Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1))),n_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Negate(Power(d,CN1)),Subst(Integrate(Times(Power(Cosh(Times(a,x)),n),Power(x,CN2)),x),x,Power(Plus(c,Times(d,x)),CN1)),x),x),And(FreeQ(list(a,c,d),x),IGtQ(n,C0)))),
IIntegrate(6153,Integrate(Power(Sinh(Times(e_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1))),n_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Negate(Power(d,CN1)),Subst(Integrate(Times(Power(Sinh(Subtract(Times(b,e,Power(d,CN1)),Times(e,Subtract(Times(b,c),Times(a,d)),x,Power(d,CN1)))),n),Power(x,CN2)),x),x,Power(Plus(c,Times(d,x)),CN1)),x),x),And(FreeQ(List(a,b,c,d),x),IGtQ(n,C0),NeQ(Subtract(Times(b,c),Times(a,d)),C0)))),
IIntegrate(6154,Integrate(Power(Cosh(Times(e_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1))),n_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Negate(Power(d,CN1)),Subst(Integrate(Times(Power(Cosh(Subtract(Times(b,e,Power(d,CN1)),Times(e,Subtract(Times(b,c),Times(a,d)),x,Power(d,CN1)))),n),Power(x,CN2)),x),x,Power(Plus(c,Times(d,x)),CN1)),x),x),And(FreeQ(List(a,b,c,d),x),IGtQ(n,C0),NeQ(Subtract(Times(b,c),Times(a,d)),C0)))),
IIntegrate(6155,Integrate(Power(Sinh(u_),n_DEFAULT),x_Symbol),
    Condition(With(list(Set($s("lst"),QuotientOfLinearsParts(u,x))),Integrate(Power(Sinh(Times(Plus(Part($s("lst"),C1),Times(Part($s("lst"),C2),x)),Power(Plus(Part($s("lst"),C3),Times(Part($s("lst"),C4),x)),CN1))),n),x)),And(IGtQ(n,C0),QuotientOfLinearsQ(u,x)))),
IIntegrate(6156,Integrate(Power(Cosh(u_),n_DEFAULT),x_Symbol),
    Condition(With(list(Set($s("lst"),QuotientOfLinearsParts(u,x))),Integrate(Power(Cosh(Times(Plus(Part($s("lst"),C1),Times(Part($s("lst"),C2),x)),Power(Plus(Part($s("lst"),C3),Times(Part($s("lst"),C4),x)),CN1))),n),x)),And(IGtQ(n,C0),QuotientOfLinearsQ(u,x)))),
IIntegrate(6157,Integrate(Times(u_DEFAULT,Power(Sinh(v_),p_DEFAULT),Power(Sinh(w_),q_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(u,Power(Sinh(v),Plus(p,q))),x),EqQ(w,v))),
IIntegrate(6158,Integrate(Times(Power(Cosh(v_),p_DEFAULT),Power(Cosh(w_),q_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Integrate(Times(u,Power(Cosh(v),Plus(p,q))),x),EqQ(w,v))),
IIntegrate(6159,Integrate(Times(Power(Sinh(v_),p_DEFAULT),Power(Sinh(w_),q_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Times(Power(Sinh(v),p),Power(Sinh(w),q)),x),x),And(IGtQ(p,C0),IGtQ(q,C0),Or(And(PolynomialQ(v,x),PolynomialQ(w,x)),And(BinomialQ(list(v,w),x),IndependentQ(Cancel(Times(v,Power(w,CN1))),x)))))),
IIntegrate(6160,Integrate(Times(Power(Cosh(v_),p_DEFAULT),Power(Cosh(w_),q_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Times(Power(Cosh(v),p),Power(Cosh(w),q)),x),x),And(IGtQ(p,C0),IGtQ(q,C0),Or(And(PolynomialQ(v,x),PolynomialQ(w,x)),And(BinomialQ(list(v,w),x),IndependentQ(Cancel(Times(v,Power(w,CN1))),x))))))
  );
}
