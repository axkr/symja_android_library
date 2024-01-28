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
class IntRules291 { 
  public static IAST RULES = List( 
IIntegrate(5821,Integrate(Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),x_Symbol),
    Condition(Subtract(Simp(Star(C1D2,Integrate(Exp(Plus(c,Times(d,Power(x,n)))),x)),x),Simp(Star(C1D2,Integrate(Exp(Subtract(Negate(c),Times(d,Power(x,n)))),x)),x)),And(FreeQ(list(c,d),x),IGtQ(n,C1)))),
IIntegrate(5822,Integrate(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),x_Symbol),
    Condition(Plus(Simp(Star(C1D2,Integrate(Exp(Plus(c,Times(d,Power(x,n)))),x)),x),Simp(Star(C1D2,Integrate(Exp(Subtract(Negate(c),Times(d,Power(x,n)))),x)),x)),And(FreeQ(list(c,d),x),IGtQ(n,C1)))),
IIntegrate(5823,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(x,n)))))),p),x),x),And(FreeQ(List(a,b,c,d),x),IGtQ(n,C1),IGtQ(p,C1)))),
IIntegrate(5824,Integrate(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(x,n)))))),p),x),x),And(FreeQ(List(a,b,c,d),x),IGtQ(n,C1),IGtQ(p,C1)))),
IIntegrate(5825,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(Power(x,n),CN1)))))),p),Power(x,CN2)),x),x,Power(x,CN1))),And(FreeQ(List(a,b,c,d),x),ILtQ(n,C0),IntegerQ(p)))),
IIntegrate(5826,Integrate(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(Power(x,n),CN1)))))),p),Power(x,CN2)),x),x,Power(x,CN1))),And(FreeQ(List(a,b,c,d),x),ILtQ(n,C0),IntegerQ(p)))),
IIntegrate(5827,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT),x_Symbol),
    Condition(Module(list(Set(k,Denominator(n))),Simp(Star(k,Subst(Integrate(Times(Power(x,Subtract(k,C1)),Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(x,Times(k,n))))))),p)),x),x,Power(x,Power(k,CN1)))),x)),And(FreeQ(List(a,b,c,d),x),FractionQ(n),IntegerQ(p)))),
IIntegrate(5828,Integrate(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Module(list(Set(k,Denominator(n))),Simp(Star(k,Subst(Integrate(Times(Power(x,Subtract(k,C1)),Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(x,Times(k,n))))))),p)),x),x,Power(x,Power(k,CN1)))),x)),And(FreeQ(List(a,b,c,d),x),FractionQ(n),IntegerQ(p)))),
IIntegrate(5829,Integrate(Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),x_Symbol),
    Condition(Subtract(Simp(Star(C1D2,Integrate(Exp(Plus(c,Times(d,Power(x,n)))),x)),x),Simp(Star(C1D2,Integrate(Exp(Subtract(Negate(c),Times(d,Power(x,n)))),x)),x)),FreeQ(list(c,d,n),x))),
IIntegrate(5830,Integrate(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),x_Symbol),
    Condition(Plus(Simp(Star(C1D2,Integrate(Exp(Plus(c,Times(d,Power(x,n)))),x)),x),Simp(Star(C1D2,Integrate(Exp(Subtract(Negate(c),Times(d,Power(x,n)))),x)),x)),FreeQ(list(c,d,n),x))),
IIntegrate(5831,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(x,n)))))),p),x),x),And(FreeQ(List(a,b,c,d,n),x),IGtQ(p,C0)))),
IIntegrate(5832,Integrate(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(x,n)))))),p),x),x),And(FreeQ(List(a,b,c,d,n),x),IGtQ(p,C0)))),
IIntegrate(5833,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(u_,n_)))))),p_DEFAULT),x_Symbol),
    Condition(Simp(Star(Power(Coefficient(u,x,C1),CN1),Subst(Integrate(Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(x,n)))))),p),x),x,u)),x),And(FreeQ(List(a,b,c,d,n),x),IntegerQ(p),LinearQ(u,x),NeQ(u,x)))),
IIntegrate(5834,Integrate(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(u_,n_)))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Star(Power(Coefficient(u,x,C1),CN1),Subst(Integrate(Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(x,n)))))),p),x),x,u)),x),And(FreeQ(List(a,b,c,d,n),x),IntegerQ(p),LinearQ(u,x),NeQ(u,x)))),
IIntegrate(5835,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(u_,n_)))))),p_),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(u,n)))))),p),x),And(FreeQ(List(a,b,c,d,n,p),x),LinearQ(u,x)))),
IIntegrate(5836,Integrate(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(u_,n_)))),b_DEFAULT)),p_),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(u,n)))))),p),x),And(FreeQ(List(a,b,c,d,n,p),x),LinearQ(u,x)))),
IIntegrate(5837,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(u_))),p_DEFAULT),x_Symbol),
    Condition(Integrate(Power(Plus(a,Times(b,Sinh(ExpandToSum(u,x)))),p),x),And(FreeQ(list(a,b,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x))))),
IIntegrate(5838,Integrate(Power(Plus(a_DEFAULT,Times(Cosh(u_),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Integrate(Power(Plus(a,Times(b,Cosh(ExpandToSum(u,x)))),p),x),And(FreeQ(list(a,b,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x))))),
IIntegrate(5839,Integrate(Times(Power(x_,CN1),Sinh(Times(d_DEFAULT,Power(x_,n_)))),x_Symbol),
    Condition(Simp(Times(SinhIntegral(Times(d,Power(x,n))),Power(n,CN1)),x),FreeQ(list(d,n),x))),
IIntegrate(5840,Integrate(Times(Cosh(Times(d_DEFAULT,Power(x_,n_))),Power(x_,CN1)),x_Symbol),
    Condition(Simp(Times(CoshIntegral(Times(d,Power(x,n))),Power(n,CN1)),x),FreeQ(list(d,n),x)))
  );
}
