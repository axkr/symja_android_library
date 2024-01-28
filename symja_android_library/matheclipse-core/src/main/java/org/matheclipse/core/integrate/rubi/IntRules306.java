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
class IntRules306 { 
  public static IAST RULES = List( 
IIntegrate(6121,Integrate(Times(Power(Coth(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),n_DEFAULT),Power(Csch(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Star(Power(a,CN1),Integrate(Times(Power(Plus(e,Times(f,x)),m),Power(Csch(Plus(c,Times(d,x))),p),Power(Coth(Plus(c,Times(d,x))),n)),x)),x),Simp(Star(Times(b,Power(a,CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),m),Power(Csch(Plus(c,Times(d,x))),Subtract(p,C1)),Power(Coth(Plus(c,Times(d,x))),n),Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,x))))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0),IGtQ(n,C0),IGtQ(p,C0)))),
IIntegrate(6122,Integrate(Times(Power(Plus(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_),CN1),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power(Sech(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),p_DEFAULT),Power(Tanh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Star(Power(a,CN1),Integrate(Times(Power(Plus(e,Times(f,x)),m),Power(Sech(Plus(c,Times(d,x))),p),Power(Tanh(Plus(c,Times(d,x))),n)),x)),x),Simp(Star(Times(b,Power(a,CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),m),Power(Sech(Plus(c,Times(d,x))),Subtract(p,C1)),Power(Tanh(Plus(c,Times(d,x))),n),Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,x))))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0),IGtQ(n,C0),IGtQ(p,C0)))),
IIntegrate(6123,Integrate(Times(Power(Csch(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),n_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power(Sech(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),p_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Star(Power(a,CN1),Integrate(Times(Power(Plus(e,Times(f,x)),m),Power(Sech(Plus(c,Times(d,x))),p),Power(Csch(Plus(c,Times(d,x))),n)),x)),x),Simp(Star(Times(b,Power(a,CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),m),Power(Sech(Plus(c,Times(d,x))),p),Power(Csch(Plus(c,Times(d,x))),Subtract(n,C1)),Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,x))))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0),IGtQ(n,C0),IGtQ(p,C0)))),
IIntegrate(6124,Integrate(Times(Power(Csch(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),p_DEFAULT),Power(Plus(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_),CN1),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power(Sech(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Star(Power(a,CN1),Integrate(Times(Power(Plus(e,Times(f,x)),m),Power(Csch(Plus(c,Times(d,x))),p),Power(Sech(Plus(c,Times(d,x))),n)),x)),x),Simp(Star(Times(b,Power(a,CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),m),Power(Csch(Plus(c,Times(d,x))),p),Power(Sech(Plus(c,Times(d,x))),Subtract(n,C1)),Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,x))))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0),IGtQ(n,C0),IGtQ(p,C0)))),
IIntegrate(6125,Integrate(Times(Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1),Power($(F_,Plus(c_DEFAULT,Times(d_DEFAULT,x_))),n_DEFAULT),Power($(G_,Plus(c_DEFAULT,Times(d_DEFAULT,x_))),p_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(e,Times(f,x)),m),Power(F(Plus(c,Times(d,x))),n),Power(G(Plus(c,Times(d,x))),p),Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,x))))),CN1)),x),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),HyperbolicQ(FSymbol),HyperbolicQ(GSymbol)))),
IIntegrate(6126,Integrate(Times(Power(Plus(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_),CN1),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power($(F_,Plus(c_DEFAULT,Times(d_DEFAULT,x_))),n_DEFAULT),Power($(G_,Plus(c_DEFAULT,Times(d_DEFAULT,x_))),p_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(e,Times(f,x)),m),Power(F(Plus(c,Times(d,x))),n),Power(G(Plus(c,Times(d,x))),p),Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,x))))),CN1)),x),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),HyperbolicQ(FSymbol),HyperbolicQ(GSymbol)))),
IIntegrate(6127,Integrate(Times(Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sech(Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1),Power($(F_,Plus(c_DEFAULT,Times(d_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Plus(e,Times(f,x)),m),Cosh(Plus(c,Times(d,x))),Power(F(Plus(c,Times(d,x))),n),Power(Plus(b,Times(a,Cosh(Plus(c,Times(d,x))))),CN1)),x),And(FreeQ(List(a,b,c,d,e,f),x),HyperbolicQ(FSymbol),IntegersQ(m,n)))),
IIntegrate(6128,Integrate(Times(Power(Plus(Times(Csch(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_),CN1),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power($(F_,Plus(c_DEFAULT,Times(d_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Plus(e,Times(f,x)),m),Sinh(Plus(c,Times(d,x))),Power(F(Plus(c,Times(d,x))),n),Power(Plus(b,Times(a,Sinh(Plus(c,Times(d,x))))),CN1)),x),And(FreeQ(List(a,b,c,d,e,f),x),HyperbolicQ(FSymbol),IntegersQ(m,n)))),
IIntegrate(6129,Integrate(Times(Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sech(Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1),Power($(F_,Plus(c_DEFAULT,Times(d_DEFAULT,x_))),n_DEFAULT),Power($(G_,Plus(c_DEFAULT,Times(d_DEFAULT,x_))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Plus(e,Times(f,x)),m),Cosh(Plus(c,Times(d,x))),Power(F(Plus(c,Times(d,x))),n),Power(G(Plus(c,Times(d,x))),p),Power(Plus(b,Times(a,Cosh(Plus(c,Times(d,x))))),CN1)),x),And(FreeQ(List(a,b,c,d,e,f),x),HyperbolicQ(FSymbol),HyperbolicQ(GSymbol),IntegersQ(m,n,p)))),
IIntegrate(6130,Integrate(Times(Power(Plus(Times(Csch(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_),CN1),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power($(F_,Plus(c_DEFAULT,Times(d_DEFAULT,x_))),n_DEFAULT),Power($(G_,Plus(c_DEFAULT,Times(d_DEFAULT,x_))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Plus(e,Times(f,x)),m),Sinh(Plus(c,Times(d,x))),Power(F(Plus(c,Times(d,x))),n),Power(G(Plus(c,Times(d,x))),p),Power(Plus(b,Times(a,Sinh(Plus(c,Times(d,x))))),CN1)),x),And(FreeQ(List(a,b,c,d,e,f),x),HyperbolicQ(FSymbol),HyperbolicQ(GSymbol),IntegersQ(m,n,p)))),
IIntegrate(6131,Integrate(Times(Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT),Power(Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),q_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Power(C2,Plus(p,q)),CN1),Integrate(ExpandIntegrand(Power(Plus(Negate(Exp(Subtract(Negate(c),Times(d,x)))),Exp(Plus(c,Times(d,x)))),q),Power(Plus(Negate(Exp(Subtract(Negate(a),Times(b,x)))),Exp(Plus(a,Times(b,x)))),p),x),x)),x),And(FreeQ(List(a,b,c,d,q),x),IGtQ(p,C0),Not(IntegerQ(q))))),
IIntegrate(6132,Integrate(Times(Power(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT),Power(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),q_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Power(C2,Plus(p,q)),CN1),Integrate(ExpandIntegrand(Power(Plus(Exp(Subtract(Negate(c),Times(d,x))),Exp(Plus(c,Times(d,x)))),q),Power(Plus(Exp(Subtract(Negate(a),Times(b,x))),Exp(Plus(a,Times(b,x)))),p),x),x)),x),And(FreeQ(List(a,b,c,d,q),x),IGtQ(p,C0),Not(IntegerQ(q))))),
IIntegrate(6133,Integrate(Times(Power(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),q_DEFAULT),Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Power(C2,Plus(p,q)),CN1),Integrate(ExpandIntegrand(Power(Plus(Exp(Subtract(Negate(c),Times(d,x))),Exp(Plus(c,Times(d,x)))),q),Power(Plus(Negate(Exp(Subtract(Negate(a),Times(b,x)))),Exp(Plus(a,Times(b,x)))),p),x),x)),x),And(FreeQ(List(a,b,c,d,q),x),IGtQ(p,C0),Not(IntegerQ(q))))),
IIntegrate(6134,Integrate(Times(Power(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT),Power(Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),q_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Power(C2,Plus(p,q)),CN1),Integrate(ExpandIntegrand(Power(Plus(Negate(Exp(Subtract(Negate(c),Times(d,x)))),Exp(Plus(c,Times(d,x)))),q),Power(Plus(Exp(Subtract(Negate(a),Times(b,x))),Exp(Plus(a,Times(b,x)))),p),x),x)),x),And(FreeQ(List(a,b,c,d,q),x),IGtQ(p,C0),Not(IntegerQ(q))))),
IIntegrate(6135,Integrate(Times(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Tanh(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Integrate(Subtract(Plus(Times(C1D2,CN1,Exp(Negate(Plus(a,Times(b,x))))),Times(C1D2,Exp(Plus(a,Times(b,x)))),Power(Times(Exp(Plus(a,Times(b,x))),Plus(C1,Exp(Times(C2,Plus(c,Times(d,x)))))),CN1)),Times(Exp(Plus(a,Times(b,x))),Power(Plus(C1,Exp(Times(C2,Plus(c,Times(d,x))))),CN1))),x),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(b),Sqr(d)),C0)))),
IIntegrate(6136,Integrate(Times(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Coth(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Integrate(Subtract(Subtract(Plus(Power(Times(Exp(Plus(a,Times(b,x))),C2),CN1),Times(C1D2,Exp(Plus(a,Times(b,x))))),Power(Times(Exp(Plus(a,Times(b,x))),Subtract(C1,Exp(Times(C2,Plus(c,Times(d,x)))))),CN1)),Times(Exp(Plus(a,Times(b,x))),Power(Subtract(C1,Exp(Times(C2,Plus(c,Times(d,x))))),CN1))),x),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(b),Sqr(d)),C0)))),
IIntegrate(6137,Integrate(Times(Coth(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Integrate(Subtract(Plus(Times(C1D2,CN1,Exp(Negate(Plus(a,Times(b,x))))),Times(C1D2,Exp(Plus(a,Times(b,x)))),Power(Times(Exp(Plus(a,Times(b,x))),Subtract(C1,Exp(Times(C2,Plus(c,Times(d,x)))))),CN1)),Times(Exp(Plus(a,Times(b,x))),Power(Subtract(C1,Exp(Times(C2,Plus(c,Times(d,x))))),CN1))),x),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(b),Sqr(d)),C0)))),
IIntegrate(6138,Integrate(Times(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Tanh(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Integrate(Subtract(Subtract(Plus(Power(Times(Exp(Plus(a,Times(b,x))),C2),CN1),Times(C1D2,Exp(Plus(a,Times(b,x))))),Power(Times(Exp(Plus(a,Times(b,x))),Plus(C1,Exp(Times(C2,Plus(c,Times(d,x)))))),CN1)),Times(Exp(Plus(a,Times(b,x))),Power(Plus(C1,Exp(Times(C2,Plus(c,Times(d,x))))),CN1))),x),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(b),Sqr(d)),C0)))),
IIntegrate(6139,Integrate(Power(Sinh(Times(a_DEFAULT,Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1))),n_DEFAULT),x_Symbol),
    Condition(Simp(Star(Negate(Power(d,CN1)),Subst(Integrate(Times(Power(Sinh(Times(a,x)),n),Power(x,CN2)),x),x,Power(Plus(c,Times(d,x)),CN1))),x),And(FreeQ(list(a,c,d),x),IGtQ(n,C0)))),
IIntegrate(6140,Integrate(Power(Cosh(Times(a_DEFAULT,Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1))),n_DEFAULT),x_Symbol),
    Condition(Simp(Star(Negate(Power(d,CN1)),Subst(Integrate(Times(Power(Cosh(Times(a,x)),n),Power(x,CN2)),x),x,Power(Plus(c,Times(d,x)),CN1))),x),And(FreeQ(list(a,c,d),x),IGtQ(n,C0))))
  );
}
