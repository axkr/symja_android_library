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
class IntRules253 { 
  public static IAST RULES = List( 
IIntegrate(5061,Integrate(Times(Power(Plus(Times(Csc(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_),CN1),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power($(F_,Plus(c_DEFAULT,Times(d_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Plus(e,Times(f,x)),m),Sin(Plus(c,Times(d,x))),Power(F(Plus(c,Times(d,x))),n),Power(Plus(b,Times(a,Sin(Plus(c,Times(d,x))))),CN1)),x),And(FreeQ(List(a,b,c,d,e,f),x),TrigQ(FSymbol),IntegersQ(m,n)))),
IIntegrate(5062,Integrate(Times(Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sec(Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1),Power($(F_,Plus(c_DEFAULT,Times(d_DEFAULT,x_))),n_DEFAULT),Power($(G_,Plus(c_DEFAULT,Times(d_DEFAULT,x_))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Plus(e,Times(f,x)),m),Cos(Plus(c,Times(d,x))),Power(F(Plus(c,Times(d,x))),n),Power(G(Plus(c,Times(d,x))),p),Power(Plus(b,Times(a,Cos(Plus(c,Times(d,x))))),CN1)),x),And(FreeQ(List(a,b,c,d,e,f),x),TrigQ(FSymbol),TrigQ(GSymbol),IntegersQ(m,n,p)))),
IIntegrate(5063,Integrate(Times(Power(Plus(Times(Csc(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_),CN1),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power($(F_,Plus(c_DEFAULT,Times(d_DEFAULT,x_))),n_DEFAULT),Power($(G_,Plus(c_DEFAULT,Times(d_DEFAULT,x_))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Plus(e,Times(f,x)),m),Sin(Plus(c,Times(d,x))),Power(F(Plus(c,Times(d,x))),n),Power(G(Plus(c,Times(d,x))),p),Power(Plus(b,Times(a,Sin(Plus(c,Times(d,x))))),CN1)),x),And(FreeQ(List(a,b,c,d,e,f),x),TrigQ(FSymbol),TrigQ(GSymbol),IntegersQ(m,n,p)))),
IIntegrate(5064,Integrate(Times(Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT),Power(Sin(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),q_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Power(C2,Plus(p,q)),CN1),Integrate(ExpandIntegrand(Power(Subtract(Times(CI,Power(Exp(Times(CI,Plus(c,Times(d,x)))),CN1)),Times(CI,Exp(Times(CI,Plus(c,Times(d,x)))))),q),Power(Subtract(Times(CI,Power(Exp(Times(CI,Plus(a,Times(b,x)))),CN1)),Times(CI,Exp(Times(CI,Plus(a,Times(b,x)))))),p),x),x)),x),And(FreeQ(List(a,b,c,d,q),x),IGtQ(p,C0),Not(IntegerQ(q))))),
IIntegrate(5065,Integrate(Times(Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT),Power(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),q_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Power(C2,Plus(p,q)),CN1),Integrate(ExpandIntegrand(Power(Plus(Exp(Times(CN1,CI,Plus(c,Times(d,x)))),Exp(Times(CI,Plus(c,Times(d,x))))),q),Power(Plus(Exp(Times(CN1,CI,Plus(a,Times(b,x)))),Exp(Times(CI,Plus(a,Times(b,x))))),p),x),x)),x),And(FreeQ(List(a,b,c,d,q),x),IGtQ(p,C0),Not(IntegerQ(q))))),
IIntegrate(5066,Integrate(Times(Power(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),q_DEFAULT),Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Power(C2,Plus(p,q)),CN1),Integrate(ExpandIntegrand(Power(Plus(Exp(Times(CN1,CI,Plus(c,Times(d,x)))),Exp(Times(CI,Plus(c,Times(d,x))))),q),Power(Subtract(Times(CI,Power(Exp(Times(CI,Plus(a,Times(b,x)))),CN1)),Times(CI,Exp(Times(CI,Plus(a,Times(b,x)))))),p),x),x)),x),And(FreeQ(List(a,b,c,d,q),x),IGtQ(p,C0),Not(IntegerQ(q))))),
IIntegrate(5067,Integrate(Times(Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT),Power(Sin(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),q_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Power(C2,Plus(p,q)),CN1),Integrate(ExpandIntegrand(Power(Subtract(Times(CI,Power(Exp(Times(CI,Plus(c,Times(d,x)))),CN1)),Times(CI,Exp(Times(CI,Plus(c,Times(d,x)))))),q),Power(Plus(Exp(Times(CN1,CI,Plus(a,Times(b,x)))),Exp(Times(CI,Plus(a,Times(b,x))))),p),x),x)),x),And(FreeQ(List(a,b,c,d,q),x),IGtQ(p,C0),Not(IntegerQ(q))))),
IIntegrate(5068,Integrate(Times(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Tan(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Integrate(Plus(Power(Times(Exp(Times(CI,Plus(a,Times(b,x)))),C2),CN1),Times(CN1,C1D2,Exp(Times(CI,Plus(a,Times(b,x))))),Negate(Power(Times(Exp(Times(CI,Plus(a,Times(b,x)))),Plus(C1,Exp(Times(C2,CI,Plus(c,Times(d,x)))))),CN1)),Times(Exp(Times(CI,Plus(a,Times(b,x)))),Power(Plus(C1,Exp(Times(C2,CI,Plus(c,Times(d,x))))),CN1))),x),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(b),Sqr(d)),C0)))),
IIntegrate(5069,Integrate(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Cot(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Integrate(Subtract(Subtract(Plus(Times(CI,Power(Times(Exp(Times(CI,Plus(a,Times(b,x)))),C2),CN1)),Times(CI,C1D2,Exp(Times(CI,Plus(a,Times(b,x)))))),Times(CI,Power(Times(Exp(Times(CI,Plus(a,Times(b,x)))),Subtract(C1,Exp(Times(C2,CI,Plus(c,Times(d,x)))))),CN1))),Times(CI,Exp(Times(CI,Plus(a,Times(b,x)))),Power(Subtract(C1,Exp(Times(C2,CI,Plus(c,Times(d,x))))),CN1))),x),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(b),Sqr(d)),C0)))),
IIntegrate(5070,Integrate(Times(Cot(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Integrate(Subtract(Plus(Times(C1D2,CN1,Exp(Times(CN1,CI,Plus(a,Times(b,x))))),Times(C1D2,Exp(Times(CI,Plus(a,Times(b,x))))),Power(Times(Exp(Times(CI,Plus(a,Times(b,x)))),Subtract(C1,Exp(Times(C2,CI,Plus(c,Times(d,x)))))),CN1)),Times(Exp(Times(CI,Plus(a,Times(b,x)))),Power(Subtract(C1,Exp(Times(C2,CI,Plus(c,Times(d,x))))),CN1))),x),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(b),Sqr(d)),C0)))),
IIntegrate(5071,Integrate(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Tan(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Integrate(Plus(Times(CN1,CI,Power(Times(Exp(Times(CI,Plus(a,Times(b,x)))),C2),CN1)),Times(CN1,CI,C1D2,Exp(Times(CI,Plus(a,Times(b,x))))),Times(CI,Power(Times(Exp(Times(CI,Plus(a,Times(b,x)))),Plus(C1,Exp(Times(C2,CI,Plus(c,Times(d,x)))))),CN1)),Times(CI,Exp(Times(CI,Plus(a,Times(b,x)))),Power(Plus(C1,Exp(Times(C2,CI,Plus(c,Times(d,x))))),CN1))),x),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(b),Sqr(d)),C0)))),
IIntegrate(5072,Integrate(Power(Sin(Times(a_DEFAULT,Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1))),n_DEFAULT),x_Symbol),
    Condition(Simp(Star(Negate(Power(d,CN1)),Subst(Integrate(Times(Power(Sin(Times(a,x)),n),Power(x,CN2)),x),x,Power(Plus(c,Times(d,x)),CN1))),x),And(FreeQ(list(a,c,d),x),IGtQ(n,C0)))),
IIntegrate(5073,Integrate(Power(Cos(Times(a_DEFAULT,Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1))),n_DEFAULT),x_Symbol),
    Condition(Simp(Star(Negate(Power(d,CN1)),Subst(Integrate(Times(Power(Cos(Times(a,x)),n),Power(x,CN2)),x),x,Power(Plus(c,Times(d,x)),CN1))),x),And(FreeQ(list(a,c,d),x),IGtQ(n,C0)))),
IIntegrate(5074,Integrate(Power(Sin(Times(e_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1))),n_DEFAULT),x_Symbol),
    Condition(Simp(Star(Negate(Power(d,CN1)),Subst(Integrate(Times(Power(Sin(Subtract(Times(b,e,Power(d,CN1)),Times(e,Subtract(Times(b,c),Times(a,d)),x,Power(d,CN1)))),n),Power(x,CN2)),x),x,Power(Plus(c,Times(d,x)),CN1))),x),And(FreeQ(List(a,b,c,d),x),IGtQ(n,C0),NeQ(Subtract(Times(b,c),Times(a,d)),C0)))),
IIntegrate(5075,Integrate(Power(Cos(Times(e_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1))),n_DEFAULT),x_Symbol),
    Condition(Simp(Star(Negate(Power(d,CN1)),Subst(Integrate(Times(Power(Cos(Subtract(Times(b,e,Power(d,CN1)),Times(e,Subtract(Times(b,c),Times(a,d)),x,Power(d,CN1)))),n),Power(x,CN2)),x),x,Power(Plus(c,Times(d,x)),CN1))),x),And(FreeQ(List(a,b,c,d),x),IGtQ(n,C0),NeQ(Subtract(Times(b,c),Times(a,d)),C0)))),
IIntegrate(5076,Integrate(Power(Sin(u_),n_DEFAULT),x_Symbol),
    Condition(Module(list(Set($s("lst"),QuotientOfLinearsParts(u,x))),Integrate(Power(Sin(Times(Plus(Part($s("lst"),C1),Times(Part($s("lst"),C2),x)),Power(Plus(Part($s("lst"),C3),Times(Part($s("lst"),C4),x)),CN1))),n),x)),And(IGtQ(n,C0),QuotientOfLinearsQ(u,x)))),
IIntegrate(5077,Integrate(Power(Cos(u_),n_DEFAULT),x_Symbol),
    Condition(Module(list(Set($s("lst"),QuotientOfLinearsParts(u,x))),Integrate(Power(Cos(Times(Plus(Part($s("lst"),C1),Times(Part($s("lst"),C2),x)),Power(Plus(Part($s("lst"),C3),Times(Part($s("lst"),C4),x)),CN1))),n),x)),And(IGtQ(n,C0),QuotientOfLinearsQ(u,x)))),
IIntegrate(5078,Integrate(Times(u_DEFAULT,Power(Sin(v_),p_DEFAULT),Power(Sin(w_),q_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(u,Power(Sin(v),Plus(p,q))),x),EqQ(w,v))),
IIntegrate(5079,Integrate(Times(Power(Cos(v_),p_DEFAULT),Power(Cos(w_),q_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Integrate(Times(u,Power(Cos(v),Plus(p,q))),x),EqQ(w,v))),
IIntegrate(5080,Integrate(Times(Power(Sin(v_),p_DEFAULT),Power(Sin(w_),q_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Times(Power(Sin(v),p),Power(Sin(w),q)),x),x),And(Or(And(PolynomialQ(v,x),PolynomialQ(w,x)),And(BinomialQ(list(v,w),x),IndependentQ(Cancel(Times(v,Power(w,CN1))),x))),IGtQ(p,C0),IGtQ(q,C0))))
  );
}
