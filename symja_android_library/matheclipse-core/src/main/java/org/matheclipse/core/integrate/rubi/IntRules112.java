package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *  
 */
public class IntRules112 { 

	public static void initialize() {
		Initializer.init();
	}

	private static class Initializer  {

		private static void init() {
IIntegrate(5601,Int(Times(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Tanh(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Int(Plus(Negate(Power(Times(Exp(Plus(a,Times(b,x))),C2),-1)),Times(C1D2,Exp(Plus(a,Times(b,x)))),Power(Times(Exp(Plus(a,Times(b,x))),Plus(C1,Exp(Times(C2,Plus(c,Times(d,x)))))),-1),Times(CN1,Exp(Plus(a,Times(b,x))),Power(Plus(C1,Exp(Times(C2,Plus(c,Times(d,x))))),-1))),x),And(FreeQ(List(a,b,c,d),x),NeQ(Plus(Sqr(b),Negate(Sqr(d))),C0))));
IIntegrate(5602,Int(Times(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Coth(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Int(Plus(Power(Times(Exp(Plus(a,Times(b,x))),C2),-1),Times(C1D2,Exp(Plus(a,Times(b,x)))),Negate(Power(Times(Exp(Plus(a,Times(b,x))),Plus(C1,Negate(Exp(Times(C2,Plus(c,Times(d,x))))))),-1)),Times(CN1,Exp(Plus(a,Times(b,x))),Power(Plus(C1,Negate(Exp(Times(C2,Plus(c,Times(d,x)))))),-1))),x),And(FreeQ(List(a,b,c,d),x),NeQ(Plus(Sqr(b),Negate(Sqr(d))),C0))));
IIntegrate(5603,Int(Times(Coth(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Int(Plus(Negate(Power(Times(Exp(Plus(a,Times(b,x))),C2),-1)),Times(C1D2,Exp(Plus(a,Times(b,x)))),Power(Times(Exp(Plus(a,Times(b,x))),Plus(C1,Negate(Exp(Times(C2,Plus(c,Times(d,x))))))),-1),Times(CN1,Exp(Plus(a,Times(b,x))),Power(Plus(C1,Negate(Exp(Times(C2,Plus(c,Times(d,x)))))),-1))),x),And(FreeQ(List(a,b,c,d),x),NeQ(Plus(Sqr(b),Negate(Sqr(d))),C0))));
IIntegrate(5604,Int(Times(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Tanh(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Int(Plus(Power(Times(Exp(Plus(a,Times(b,x))),C2),-1),Times(C1D2,Exp(Plus(a,Times(b,x)))),Negate(Power(Times(Exp(Plus(a,Times(b,x))),Plus(C1,Exp(Times(C2,Plus(c,Times(d,x)))))),-1)),Times(CN1,Exp(Plus(a,Times(b,x))),Power(Plus(C1,Exp(Times(C2,Plus(c,Times(d,x))))),-1))),x),And(FreeQ(List(a,b,c,d),x),NeQ(Plus(Sqr(b),Negate(Sqr(d))),C0))));
IIntegrate(5605,Int(Power(Sinh(Times(a_DEFAULT,Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),-1))),n_DEFAULT),x_Symbol),
    Condition(Negate(Dist(Power(d,-1),Subst(Int(Times(Power(Sinh(Times(a,x)),n),Power(x,-2)),x),x,Power(Plus(c,Times(d,x)),-1)),x)),And(FreeQ(List(a,c,d),x),IGtQ(n,C0))));
IIntegrate(5606,Int(Power(Cosh(Times(a_DEFAULT,Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),-1))),n_DEFAULT),x_Symbol),
    Condition(Negate(Dist(Power(d,-1),Subst(Int(Times(Power(Cosh(Times(a,x)),n),Power(x,-2)),x),x,Power(Plus(c,Times(d,x)),-1)),x)),And(FreeQ(List(a,c,d),x),IGtQ(n,C0))));
IIntegrate(5607,Int(Power(Sinh(Times(e_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),-1))),n_DEFAULT),x_Symbol),
    Condition(Negate(Dist(Power(d,-1),Subst(Int(Times(Power(Sinh(Plus(Times(b,e,Power(d,-1)),Times(CN1,e,Plus(Times(b,c),Times(CN1,a,d)),x,Power(d,-1)))),n),Power(x,-2)),x),x,Power(Plus(c,Times(d,x)),-1)),x)),And(FreeQ(List(a,b,c,d),x),IGtQ(n,C0),NeQ(Plus(Times(b,c),Times(CN1,a,d)),C0))));
IIntegrate(5608,Int(Power(Cosh(Times(e_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),-1))),n_DEFAULT),x_Symbol),
    Condition(Negate(Dist(Power(d,-1),Subst(Int(Times(Power(Cosh(Plus(Times(b,e,Power(d,-1)),Times(CN1,e,Plus(Times(b,c),Times(CN1,a,d)),x,Power(d,-1)))),n),Power(x,-2)),x),x,Power(Plus(c,Times(d,x)),-1)),x)),And(FreeQ(List(a,b,c,d),x),IGtQ(n,C0),NeQ(Plus(Times(b,c),Times(CN1,a,d)),C0))));
IIntegrate(5609,Int(Power(Sinh(u_),n_DEFAULT),x_Symbol),
    Condition(With(List(Set($s("lst"),QuotientOfLinearsParts(u,x))),Int(Power(Sinh(Times(Plus(Part($s("lst"),C1),Times(Part($s("lst"),C2),x)),Power(Plus(Part($s("lst"),C3),Times(Part($s("lst"),C4),x)),-1))),n),x)),And(IGtQ(n,C0),QuotientOfLinearsQ(u,x))));
IIntegrate(5610,Int(Power(Cosh(u_),n_DEFAULT),x_Symbol),
    Condition(With(List(Set($s("lst"),QuotientOfLinearsParts(u,x))),Int(Power(Cosh(Times(Plus(Part($s("lst"),C1),Times(Part($s("lst"),C2),x)),Power(Plus(Part($s("lst"),C3),Times(Part($s("lst"),C4),x)),-1))),n),x)),And(IGtQ(n,C0),QuotientOfLinearsQ(u,x))));
IIntegrate(5611,Int(Times(u_DEFAULT,Power(Sinh(v_),p_DEFAULT),Power(Sinh(w_),q_DEFAULT)),x_Symbol),
    Condition(Int(Times(u,Power(Sinh(v),Plus(p,q))),x),EqQ(w,v)));
IIntegrate(5612,Int(Times(Power(Cosh(v_),p_DEFAULT),Power(Cosh(w_),q_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Int(Times(u,Power(Cosh(v),Plus(p,q))),x),EqQ(w,v)));
IIntegrate(5613,Int(Times(Power(Sinh(v_),p_DEFAULT),Power(Sinh(w_),q_DEFAULT)),x_Symbol),
    Condition(Int(ExpandTrigReduce(Times(Power(Sinh(v),p),Power(Sinh(w),q)),x),x),And(IGtQ(p,C0),IGtQ(q,C0),Or(And(PolynomialQ(v,x),PolynomialQ(w,x)),And(BinomialQ(List(v,w),x),IndependentQ(Cancel(Times(v,Power(w,-1))),x))))));
IIntegrate(5614,Int(Times(Power(Cosh(v_),p_DEFAULT),Power(Cosh(w_),q_DEFAULT)),x_Symbol),
    Condition(Int(ExpandTrigReduce(Times(Power(Cosh(v),p),Power(Cosh(w),q)),x),x),And(IGtQ(p,C0),IGtQ(q,C0),Or(And(PolynomialQ(v,x),PolynomialQ(w,x)),And(BinomialQ(List(v,w),x),IndependentQ(Cancel(Times(v,Power(w,-1))),x))))));
IIntegrate(5615,Int(Times(Power(x_,m_DEFAULT),Power(Sinh(v_),p_DEFAULT),Power(Sinh(w_),q_DEFAULT)),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(x,m),Times(Power(Sinh(v),p),Power(Sinh(w),q)),x),x),And(IGtQ(m,C0),IGtQ(p,C0),IGtQ(q,C0),Or(And(PolynomialQ(v,x),PolynomialQ(w,x)),And(BinomialQ(List(v,w),x),IndependentQ(Cancel(Times(v,Power(w,-1))),x))))));
IIntegrate(5616,Int(Times(Power(Cosh(v_),p_DEFAULT),Power(Cosh(w_),q_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(x,m),Times(Power(Cosh(v),p),Power(Cosh(w),q)),x),x),And(IGtQ(m,C0),IGtQ(p,C0),IGtQ(q,C0),Or(And(PolynomialQ(v,x),PolynomialQ(w,x)),And(BinomialQ(List(v,w),x),IndependentQ(Cancel(Times(v,Power(w,-1))),x))))));
IIntegrate(5617,Int(Times(Power(Cosh(w_),p_DEFAULT),u_DEFAULT,Power(Sinh(v_),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Power(C2,p),-1),Int(Times(u,Power(Sinh(Times(C2,v)),p)),x),x),And(EqQ(w,v),IntegerQ(p))));
IIntegrate(5618,Int(Times(Power(Cosh(w_),q_DEFAULT),Power(Sinh(v_),p_DEFAULT)),x_Symbol),
    Condition(Int(ExpandTrigReduce(Times(Power(Sinh(v),p),Power(Cosh(w),q)),x),x),And(IGtQ(p,C0),IGtQ(q,C0),Or(And(PolynomialQ(v,x),PolynomialQ(w,x)),And(BinomialQ(List(v,w),x),IndependentQ(Cancel(Times(v,Power(w,-1))),x))))));
IIntegrate(5619,Int(Times(Power(Cosh(w_),q_DEFAULT),Power(x_,m_DEFAULT),Power(Sinh(v_),p_DEFAULT)),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(x,m),Times(Power(Sinh(v),p),Power(Cosh(w),q)),x),x),And(IGtQ(m,C0),IGtQ(p,C0),IGtQ(q,C0),Or(And(PolynomialQ(v,x),PolynomialQ(w,x)),And(BinomialQ(List(v,w),x),IndependentQ(Cancel(Times(v,Power(w,-1))),x))))));
IIntegrate(5620,Int(Times(Sinh(v_),Power(Tanh(w_),n_DEFAULT)),x_Symbol),
    Condition(Plus(Int(Times(Cosh(v),Power(Tanh(w),Plus(n,Negate(C1)))),x),Negate(Dist(Cosh(Plus(v,Negate(w))),Int(Times(Sech(w),Power(Tanh(w),Plus(n,Negate(C1)))),x),x))),And(GtQ(n,C0),NeQ(w,v),FreeQ(Plus(v,Negate(w)),x))));
IIntegrate(5621,Int(Times(Cosh(v_),Power(Coth(w_),n_DEFAULT)),x_Symbol),
    Condition(Plus(Int(Times(Sinh(v),Power(Coth(w),Plus(n,Negate(C1)))),x),Dist(Cosh(Plus(v,Negate(w))),Int(Times(Csch(w),Power(Coth(w),Plus(n,Negate(C1)))),x),x)),And(GtQ(n,C0),NeQ(w,v),FreeQ(Plus(v,Negate(w)),x))));
IIntegrate(5622,Int(Times(Power(Coth(w_),n_DEFAULT),Sinh(v_)),x_Symbol),
    Condition(Plus(Int(Times(Cosh(v),Power(Coth(w),Plus(n,Negate(C1)))),x),Dist(Sinh(Plus(v,Negate(w))),Int(Times(Csch(w),Power(Coth(w),Plus(n,Negate(C1)))),x),x)),And(GtQ(n,C0),NeQ(w,v),FreeQ(Plus(v,Negate(w)),x))));
IIntegrate(5623,Int(Times(Cosh(v_),Power(Tanh(w_),n_DEFAULT)),x_Symbol),
    Condition(Plus(Int(Times(Sinh(v),Power(Tanh(w),Plus(n,Negate(C1)))),x),Negate(Dist(Sinh(Plus(v,Negate(w))),Int(Times(Sech(w),Power(Tanh(w),Plus(n,Negate(C1)))),x),x))),And(GtQ(n,C0),NeQ(w,v),FreeQ(Plus(v,Negate(w)),x))));
IIntegrate(5624,Int(Times(Power(Sech(w_),n_DEFAULT),Sinh(v_)),x_Symbol),
    Condition(Plus(Dist(Cosh(Plus(v,Negate(w))),Int(Times(Tanh(w),Power(Sech(w),Plus(n,Negate(C1)))),x),x),Dist(Sinh(Plus(v,Negate(w))),Int(Power(Sech(w),Plus(n,Negate(C1))),x),x)),And(GtQ(n,C0),NeQ(w,v),FreeQ(Plus(v,Negate(w)),x))));
IIntegrate(5625,Int(Times(Cosh(v_),Power(Csch(w_),n_DEFAULT)),x_Symbol),
    Condition(Plus(Dist(Cosh(Plus(v,Negate(w))),Int(Times(Coth(w),Power(Csch(w),Plus(n,Negate(C1)))),x),x),Dist(Sinh(Plus(v,Negate(w))),Int(Power(Csch(w),Plus(n,Negate(C1))),x),x)),And(GtQ(n,C0),NeQ(w,v),FreeQ(Plus(v,Negate(w)),x))));
IIntegrate(5626,Int(Times(Power(Csch(w_),n_DEFAULT),Sinh(v_)),x_Symbol),
    Condition(Plus(Dist(Sinh(Plus(v,Negate(w))),Int(Times(Coth(w),Power(Csch(w),Plus(n,Negate(C1)))),x),x),Dist(Cosh(Plus(v,Negate(w))),Int(Power(Csch(w),Plus(n,Negate(C1))),x),x)),And(GtQ(n,C0),NeQ(w,v),FreeQ(Plus(v,Negate(w)),x))));
IIntegrate(5627,Int(Times(Cosh(v_),Power(Sech(w_),n_DEFAULT)),x_Symbol),
    Condition(Plus(Dist(Sinh(Plus(v,Negate(w))),Int(Times(Tanh(w),Power(Sech(w),Plus(n,Negate(C1)))),x),x),Dist(Cosh(Plus(v,Negate(w))),Int(Power(Sech(w),Plus(n,Negate(C1))),x),x)),And(GtQ(n,C0),NeQ(w,v),FreeQ(Plus(v,Negate(w)),x))));
IIntegrate(5628,Int(Times(Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(C1D2,b,Sinh(Plus(Times(C2,c),Times(C2,d,x))))),n)),x),FreeQ(List(a,b,c,d,e,f,m,n),x)));
IIntegrate(5629,Int(Times(Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),n_)),x_Symbol),
    Condition(Dist(Power(Power(C2,n),-1),Int(Times(Power(x,m),Power(Plus(Times(C2,a),Negate(b),Times(b,Cosh(Plus(Times(C2,c),Times(C2,d,x))))),n)),x),x),And(FreeQ(List(a,b,c,d),x),NeQ(Plus(a,Negate(b)),C0),IGtQ(m,C0),ILtQ(n,C0),Or(EqQ(n,CN1),And(EqQ(m,C1),EqQ(n,CN2))))));
IIntegrate(5630,Int(Times(Power(Plus(Times(Sqr(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),b_DEFAULT),a_),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Power(C2,n),-1),Int(Times(Power(x,m),Power(Plus(Times(C2,a),b,Times(b,Cosh(Plus(Times(C2,c),Times(C2,d,x))))),n)),x),x),And(FreeQ(List(a,b,c,d),x),NeQ(Plus(a,Negate(b)),C0),IGtQ(m,C0),ILtQ(n,C0),Or(EqQ(n,CN1),And(EqQ(m,C1),EqQ(n,CN2))))));
IIntegrate(5631,Int(Times(Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_DEFAULT,Times(Sqr(Cosh(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),b_DEFAULT),Times(c_DEFAULT,Sqr(Sinh(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))))),-1)),x_Symbol),
    Condition(Dist(C2,Int(Times(Power(Plus(f,Times(g,x)),m),Power(Plus(Times(C2,a),b,Negate(c),Times(Plus(b,c),Cosh(Plus(Times(C2,d),Times(C2,e,x))))),-1)),x),x),And(FreeQ(List(a,b,c,d,e,f,g),x),IGtQ(m,C0),NeQ(Plus(a,b),C0),NeQ(Plus(a,c),C0))));
IIntegrate(5632,Int(Times(Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),m_DEFAULT),Sqr(Sech(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),Power(Plus(b_,Times(c_DEFAULT,Sqr(Tanh(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))))),-1)),x_Symbol),
    Condition(Dist(C2,Int(Times(Power(Plus(f,Times(g,x)),m),Power(Plus(b,Negate(c),Times(Plus(b,c),Cosh(Plus(Times(C2,d),Times(C2,e,x))))),-1)),x),x),And(FreeQ(List(b,c,d,e,f,g),x),IGtQ(m,C0))));
IIntegrate(5633,Int(Times(Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),m_DEFAULT),Sqr(Sech(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),Power(Plus(b_DEFAULT,Times(a_DEFAULT,Sqr(Sech(Plus(d_DEFAULT,Times(e_DEFAULT,x_))))),Times(c_DEFAULT,Sqr(Tanh(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))))),-1)),x_Symbol),
    Condition(Dist(C2,Int(Times(Power(Plus(f,Times(g,x)),m),Power(Plus(Times(C2,a),b,Negate(c),Times(Plus(b,c),Cosh(Plus(Times(C2,d),Times(C2,e,x))))),-1)),x),x),And(FreeQ(List(a,b,c,d,e,f,g),x),IGtQ(m,C0),NeQ(Plus(a,b),C0),NeQ(Plus(a,c),C0))));
IIntegrate(5634,Int(Times(Sqr(Csch(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),Power(Plus(Times(Sqr(Coth(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),b_DEFAULT),c_),-1),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Dist(C2,Int(Times(Power(Plus(f,Times(g,x)),m),Power(Plus(b,Negate(c),Times(Plus(b,c),Cosh(Plus(Times(C2,d),Times(C2,e,x))))),-1)),x),x),And(FreeQ(List(b,c,d,e,f,g),x),IGtQ(m,C0))));
IIntegrate(5635,Int(Times(Sqr(Csch(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),Power(Plus(Times(Sqr(Csch(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),a_DEFAULT),Times(Sqr(Coth(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),b_DEFAULT),c_DEFAULT),-1),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Dist(C2,Int(Times(Power(Plus(f,Times(g,x)),m),Power(Plus(Times(C2,a),b,Negate(c),Times(Plus(b,c),Cosh(Plus(Times(C2,d),Times(C2,e,x))))),-1)),x),x),And(FreeQ(List(a,b,c,d,e,f,g),x),IGtQ(m,C0),NeQ(Plus(a,b),C0),NeQ(Plus(a,c),C0))));
IIntegrate(5636,Int(Times(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),Power(Plus(a_,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),-2),Plus(A_,Times(B_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Plus(Simp(Times(BSymbol,Plus(e,Times(f,x)),Cosh(Plus(c,Times(d,x))),Power(Times(a,d,Plus(a,Times(b,Sinh(Plus(c,Times(d,x)))))),-1)),x),Negate(Dist(Times(BSymbol,f,Power(Times(a,d),-1)),Int(Times(Cosh(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,x))))),-1)),x),x))),And(FreeQ(List(a,b,c,d,e,f,ASymbol,BSymbol),x),EqQ(Plus(Times(a,ASymbol),Times(b,BSymbol)),C0))));
IIntegrate(5637,Int(Times(Power(Plus(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_),-2),Plus(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),B_DEFAULT),A_),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Times(BSymbol,Plus(e,Times(f,x)),Sinh(Plus(c,Times(d,x))),Power(Times(a,d,Plus(a,Times(b,Cosh(Plus(c,Times(d,x)))))),-1)),x),Negate(Dist(Times(BSymbol,f,Power(Times(a,d),-1)),Int(Times(Sinh(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,x))))),-1)),x),x))),And(FreeQ(List(a,b,c,d,e,f,ASymbol,BSymbol),x),EqQ(Plus(Times(a,ASymbol),Times(CN1,b,BSymbol)),C0))));
IIntegrate(5638,Int(Times(Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,x_)),n_)))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Power(d,Plus(m,C1)),-1),Subst(Int(Times(Power(Plus(Times(d,e),Times(CN1,c,f),Times(f,x)),m),Power(Sinh(Plus(a,Times(b,Power(x,n)))),p)),x),x,Plus(c,Times(d,x))),x),And(FreeQ(List(a,b,c,d,e,f,n),x),IGtQ(m,C0),RationalQ(p))));
IIntegrate(5639,Int(Times(Power(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,x_)),n_)))),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Power(d,Plus(m,C1)),-1),Subst(Int(Times(Power(Plus(Times(d,e),Times(CN1,c,f),Times(f,x)),m),Power(Cosh(Plus(a,Times(b,Power(x,n)))),p)),x),x,Plus(c,Times(d,x))),x),And(FreeQ(List(a,b,c,d,e,f,n),x),IGtQ(m,C0),RationalQ(p))));
IIntegrate(5640,Int(Times(Power(Sech(v_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Tanh(v_))),n_DEFAULT)),x_Symbol),
    Condition(Int(Power(Plus(Times(a,Cosh(v)),Times(b,Sinh(v))),n),x),And(FreeQ(List(a,b),x),IntegerQ(Times(C1D2,Plus(m,Negate(C1)))),EqQ(Plus(m,n),C0))));
IIntegrate(5641,Int(Times(Power(Csch(v_),m_DEFAULT),Power(Plus(Times(Coth(v_),b_DEFAULT),a_),n_DEFAULT)),x_Symbol),
    Condition(Int(Power(Plus(Times(b,Cosh(v)),Times(a,Sinh(v))),n),x),And(FreeQ(List(a,b),x),IntegerQ(Times(C1D2,Plus(m,Negate(C1)))),EqQ(Plus(m,n),C0))));
IIntegrate(5642,Int(Times(u_DEFAULT,Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),m_DEFAULT),Power(Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Int(ExpandTrigReduce(u,Times(Power(Sinh(Plus(a,Times(b,x))),m),Power(Sinh(Plus(c,Times(d,x))),n)),x),x),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C0),IGtQ(n,C0))));
IIntegrate(5643,Int(Times(Power(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),m_DEFAULT),Power(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),n_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Int(ExpandTrigReduce(u,Times(Power(Cosh(Plus(a,Times(b,x))),m),Power(Cosh(Plus(c,Times(d,x))),n)),x),x),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C0),IGtQ(n,C0))));
IIntegrate(5644,Int(Times(Sech(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Sech(Plus(c_,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Negate(Dist(Csch(Times(Plus(Times(b,c),Times(CN1,a,d)),Power(d,-1))),Int(Tanh(Plus(a,Times(b,x))),x),x)),Dist(Csch(Times(Plus(Times(b,c),Times(CN1,a,d)),Power(b,-1))),Int(Tanh(Plus(c,Times(d,x))),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Sqr(b),Negate(Sqr(d))),C0),NeQ(Plus(Times(b,c),Times(CN1,a,d)),C0))));
IIntegrate(5645,Int(Times(Csch(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Csch(Plus(c_,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Dist(Csch(Times(Plus(Times(b,c),Times(CN1,a,d)),Power(b,-1))),Int(Coth(Plus(a,Times(b,x))),x),x),Negate(Dist(Csch(Times(Plus(Times(b,c),Times(CN1,a,d)),Power(d,-1))),Int(Coth(Plus(c,Times(d,x))),x),x))),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Sqr(b),Negate(Sqr(d))),C0),NeQ(Plus(Times(b,c),Times(CN1,a,d)),C0))));
IIntegrate(5646,Int(Times(Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Tanh(Plus(c_,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(b,x,Power(d,-1)),x),Negate(Dist(Times(b,Cosh(Times(Plus(Times(b,c),Times(CN1,a,d)),Power(d,-1))),Power(d,-1)),Int(Times(Sech(Plus(a,Times(b,x))),Sech(Plus(c,Times(d,x)))),x),x))),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Sqr(b),Negate(Sqr(d))),C0),NeQ(Plus(Times(b,c),Times(CN1,a,d)),C0))));
IIntegrate(5647,Int(Times(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Coth(Plus(c_,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(b,x,Power(d,-1)),x),Dist(Cosh(Times(Plus(Times(b,c),Times(CN1,a,d)),Power(d,-1))),Int(Times(Csch(Plus(a,Times(b,x))),Csch(Plus(c,Times(d,x)))),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Sqr(b),Negate(Sqr(d))),C0),NeQ(Plus(Times(b,c),Times(CN1,a,d)),C0))));
IIntegrate(5648,Int(Times(u_DEFAULT,Power(Plus(Times(Cosh(v_),a_DEFAULT),Times(b_DEFAULT,Sinh(v_))),n_DEFAULT)),x_Symbol),
    Condition(Int(Times(u,Power(Times(a,Exp(Times(a,v,Power(b,-1)))),n)),x),And(FreeQ(List(a,b,n),x),EqQ(Plus(Sqr(a),Negate(Sqr(b))),C0))));
IIntegrate(5649,Int(Sinh(Times(Sqr(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT))),d_DEFAULT)),x_Symbol),
    Condition(Plus(Negate(Dist(C1D2,Int(Exp(Times(CN1,d,Sqr(Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x),x)),Dist(C1D2,Int(Exp(Times(d,Sqr(Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x),x)),FreeQ(List(a,b,c,d,n),x)));
IIntegrate(5650,Int(Cosh(Times(Sqr(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT))),d_DEFAULT)),x_Symbol),
    Condition(Plus(Dist(C1D2,Int(Exp(Times(CN1,d,Sqr(Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x),x),Dist(C1D2,Int(Exp(Times(d,Sqr(Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x),x)),FreeQ(List(a,b,c,d,n),x)));
  }
}
}
