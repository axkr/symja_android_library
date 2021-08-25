package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class IntRules281 { 
  public static IAST RULES = List( 
IIntegrate(5621,Integrate(Times(Cosh(v_),Power(Coth(w_),n_DEFAULT)),x_Symbol),
    Condition(Plus(Integrate(Times(Sinh(v),Power(Coth(w),Subtract(n,C1))),x),Dist(Cosh(Subtract(v,w)),Integrate(Times(Csch(w),Power(Coth(w),Subtract(n,C1))),x),x)),And(GtQ(n,C0),NeQ(w,v),FreeQ(Subtract(v,w),x)))),
IIntegrate(5622,Integrate(Times(Power(Coth(w_),n_DEFAULT),Sinh(v_)),x_Symbol),
    Condition(Plus(Integrate(Times(Cosh(v),Power(Coth(w),Subtract(n,C1))),x),Dist(Sinh(Subtract(v,w)),Integrate(Times(Csch(w),Power(Coth(w),Subtract(n,C1))),x),x)),And(GtQ(n,C0),NeQ(w,v),FreeQ(Subtract(v,w),x)))),
IIntegrate(5623,Integrate(Times(Cosh(v_),Power(Tanh(w_),n_DEFAULT)),x_Symbol),
    Condition(Subtract(Integrate(Times(Sinh(v),Power(Tanh(w),Subtract(n,C1))),x),Dist(Sinh(Subtract(v,w)),Integrate(Times(Sech(w),Power(Tanh(w),Subtract(n,C1))),x),x)),And(GtQ(n,C0),NeQ(w,v),FreeQ(Subtract(v,w),x)))),
IIntegrate(5624,Integrate(Times(Power(Sech(w_),n_DEFAULT),Sinh(v_)),x_Symbol),
    Condition(Plus(Dist(Cosh(Subtract(v,w)),Integrate(Times(Tanh(w),Power(Sech(w),Subtract(n,C1))),x),x),Dist(Sinh(Subtract(v,w)),Integrate(Power(Sech(w),Subtract(n,C1)),x),x)),And(GtQ(n,C0),NeQ(w,v),FreeQ(Subtract(v,w),x)))),
IIntegrate(5625,Integrate(Times(Cosh(v_),Power(Csch(w_),n_DEFAULT)),x_Symbol),
    Condition(Plus(Dist(Cosh(Subtract(v,w)),Integrate(Times(Coth(w),Power(Csch(w),Subtract(n,C1))),x),x),Dist(Sinh(Subtract(v,w)),Integrate(Power(Csch(w),Subtract(n,C1)),x),x)),And(GtQ(n,C0),NeQ(w,v),FreeQ(Subtract(v,w),x)))),
IIntegrate(5626,Integrate(Times(Power(Csch(w_),n_DEFAULT),Sinh(v_)),x_Symbol),
    Condition(Plus(Dist(Sinh(Subtract(v,w)),Integrate(Times(Coth(w),Power(Csch(w),Subtract(n,C1))),x),x),Dist(Cosh(Subtract(v,w)),Integrate(Power(Csch(w),Subtract(n,C1)),x),x)),And(GtQ(n,C0),NeQ(w,v),FreeQ(Subtract(v,w),x)))),
IIntegrate(5627,Integrate(Times(Cosh(v_),Power(Sech(w_),n_DEFAULT)),x_Symbol),
    Condition(Plus(Dist(Sinh(Subtract(v,w)),Integrate(Times(Tanh(w),Power(Sech(w),Subtract(n,C1))),x),x),Dist(Cosh(Subtract(v,w)),Integrate(Power(Sech(w),Subtract(n,C1)),x),x)),And(GtQ(n,C0),NeQ(w,v),FreeQ(Subtract(v,w),x)))),
IIntegrate(5628,Integrate(Times(Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(C1D2,b,Sinh(Plus(Times(C2,c),Times(C2,d,x))))),n)),x),FreeQ(List(a,b,c,d,e,f,m,n),x))),
IIntegrate(5629,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),n_)),x_Symbol),
    Condition(Dist(Power(Power(C2,n),CN1),Integrate(Times(Power(x,m),Power(Plus(Times(C2,a),Negate(b),Times(b,Cosh(Plus(Times(C2,c),Times(C2,d,x))))),n)),x),x),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(a,b),C0),IGtQ(m,C0),ILtQ(n,C0),Or(EqQ(n,CN1),And(EqQ(m,C1),EqQ(n,CN2)))))),
IIntegrate(5630,Integrate(Times(Power(Plus(Times(Sqr(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),b_DEFAULT),a_),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Power(C2,n),CN1),Integrate(Times(Power(x,m),Power(Plus(Times(C2,a),b,Times(b,Cosh(Plus(Times(C2,c),Times(C2,d,x))))),n)),x),x),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(a,b),C0),IGtQ(m,C0),ILtQ(n,C0),Or(EqQ(n,CN1),And(EqQ(m,C1),EqQ(n,CN2)))))),
IIntegrate(5631,Integrate(Times(Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_DEFAULT,Times(Sqr(Cosh(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),b_DEFAULT),Times(c_DEFAULT,Sqr(Sinh(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))))),CN1)),x_Symbol),
    Condition(Dist(C2,Integrate(Times(Power(Plus(f,Times(g,x)),m),Power(Plus(Times(C2,a),b,Negate(c),Times(Plus(b,c),Cosh(Plus(Times(C2,d),Times(C2,e,x))))),CN1)),x),x),And(FreeQ(List(a,b,c,d,e,f,g),x),IGtQ(m,C0),NeQ(Plus(a,b),C0),NeQ(Plus(a,c),C0)))),
IIntegrate(5632,Integrate(Times(Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),m_DEFAULT),Sqr(Sech(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),Power(Plus(b_,Times(c_DEFAULT,Sqr(Tanh(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))))),CN1)),x_Symbol),
    Condition(Dist(C2,Integrate(Times(Power(Plus(f,Times(g,x)),m),Power(Plus(b,Negate(c),Times(Plus(b,c),Cosh(Plus(Times(C2,d),Times(C2,e,x))))),CN1)),x),x),And(FreeQ(List(b,c,d,e,f,g),x),IGtQ(m,C0)))),
IIntegrate(5633,Integrate(Times(Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),m_DEFAULT),Sqr(Sech(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),Power(Plus(b_DEFAULT,Times(a_DEFAULT,Sqr(Sech(Plus(d_DEFAULT,Times(e_DEFAULT,x_))))),Times(c_DEFAULT,Sqr(Tanh(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))))),CN1)),x_Symbol),
    Condition(Dist(C2,Integrate(Times(Power(Plus(f,Times(g,x)),m),Power(Plus(Times(C2,a),b,Negate(c),Times(Plus(b,c),Cosh(Plus(Times(C2,d),Times(C2,e,x))))),CN1)),x),x),And(FreeQ(List(a,b,c,d,e,f,g),x),IGtQ(m,C0),NeQ(Plus(a,b),C0),NeQ(Plus(a,c),C0)))),
IIntegrate(5634,Integrate(Times(Sqr(Csch(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),Power(Plus(Times(Sqr(Coth(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),b_DEFAULT),c_),CN1),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Dist(C2,Integrate(Times(Power(Plus(f,Times(g,x)),m),Power(Plus(b,Negate(c),Times(Plus(b,c),Cosh(Plus(Times(C2,d),Times(C2,e,x))))),CN1)),x),x),And(FreeQ(List(b,c,d,e,f,g),x),IGtQ(m,C0)))),
IIntegrate(5635,Integrate(Times(Sqr(Csch(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),Power(Plus(Times(Sqr(Csch(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),a_DEFAULT),Times(Sqr(Coth(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),b_DEFAULT),c_DEFAULT),CN1),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Dist(C2,Integrate(Times(Power(Plus(f,Times(g,x)),m),Power(Plus(Times(C2,a),b,Negate(c),Times(Plus(b,c),Cosh(Plus(Times(C2,d),Times(C2,e,x))))),CN1)),x),x),And(FreeQ(List(a,b,c,d,e,f,g),x),IGtQ(m,C0),NeQ(Plus(a,b),C0),NeQ(Plus(a,c),C0)))),
IIntegrate(5636,Integrate(Times(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),Power(Plus(a_,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN2),Plus(A_,Times(B_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Subtract(Simp(Times(BSymbol,Plus(e,Times(f,x)),Cosh(Plus(c,Times(d,x))),Power(Times(a,d,Plus(a,Times(b,Sinh(Plus(c,Times(d,x)))))),CN1)),x),Dist(Times(BSymbol,f,Power(Times(a,d),CN1)),Integrate(Times(Cosh(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,x))))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,f,ASymbol,BSymbol),x),EqQ(Plus(Times(a,ASymbol),Times(b,BSymbol)),C0)))),
IIntegrate(5637,Integrate(Times(Power(Plus(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_),CN2),Plus(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),B_DEFAULT),A_),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Simp(Times(BSymbol,Plus(e,Times(f,x)),Sinh(Plus(c,Times(d,x))),Power(Times(a,d,Plus(a,Times(b,Cosh(Plus(c,Times(d,x)))))),CN1)),x),Dist(Times(BSymbol,f,Power(Times(a,d),CN1)),Integrate(Times(Sinh(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,x))))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,f,ASymbol,BSymbol),x),EqQ(Subtract(Times(a,ASymbol),Times(b,BSymbol)),C0)))),
IIntegrate(5638,Integrate(Times(Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,x_)),n_)))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Power(d,Plus(m,C1)),CN1),Subst(Integrate(Times(Power(Plus(Times(d,e),Times(CN1,c,f),Times(f,x)),m),Power(Sinh(Plus(a,Times(b,Power(x,n)))),p)),x),x,Plus(c,Times(d,x))),x),And(FreeQ(List(a,b,c,d,e,f,n),x),IGtQ(m,C0),RationalQ(p)))),
IIntegrate(5639,Integrate(Times(Power(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,x_)),n_)))),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Power(d,Plus(m,C1)),CN1),Subst(Integrate(Times(Power(Plus(Times(d,e),Times(CN1,c,f),Times(f,x)),m),Power(Cosh(Plus(a,Times(b,Power(x,n)))),p)),x),x,Plus(c,Times(d,x))),x),And(FreeQ(List(a,b,c,d,e,f,n),x),IGtQ(m,C0),RationalQ(p)))),
IIntegrate(5640,Integrate(Times(Power(Sech(v_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Tanh(v_))),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Power(Plus(Times(a,Cosh(v)),Times(b,Sinh(v))),n),x),And(FreeQ(List(a,b),x),IntegerQ(Times(C1D2,Subtract(m,C1))),EqQ(Plus(m,n),C0))))
  );
}
