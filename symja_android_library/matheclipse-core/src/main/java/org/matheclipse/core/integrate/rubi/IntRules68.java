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
public class IntRules68 { 

	public static void initialize() {
		Initializer.init();
	}

	private static class Initializer  {

		private static void init() {
IIntegrate(3401,Int(Times(Power(Times(e_DEFAULT,x_),m_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(With(List(Set(k,Denominator(m))),Dist(Times(k,Power(e,CN1)),Subst(Int(Times(Power(x,Subtract(Times(k,Plus(m,C1)),C1)),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(x,Times(k,n)),Power(Power(e,n),CN1)))))),p)),x),x,Power(Times(e,x),Power(k,CN1))),x)),And(FreeQ(List(a,b,c,d,e),x),IntegerQ(p),IGtQ(n,C0),FractionQ(m))));
IIntegrate(3402,Int(Times(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(Times(e_DEFAULT,x_),m_)),x_Symbol),
    Condition(With(List(Set(k,Denominator(m))),Dist(Times(k,Power(e,CN1)),Subst(Int(Times(Power(x,Subtract(Times(k,Plus(m,C1)),C1)),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(x,Times(k,n)),Power(Power(e,n),CN1)))))),p)),x),x,Power(Times(e,x),Power(k,CN1))),x)),And(FreeQ(List(a,b,c,d,e),x),IntegerQ(p),IGtQ(n,C0),FractionQ(m))));
IIntegrate(3403,Int(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_)),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Times(e,x),m),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(x,n)))))),p),x),x),And(FreeQ(List(a,b,c,d,e,m),x),IGtQ(p,C1),IGtQ(n,C0))));
IIntegrate(3404,Int(Times(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Times(e,x),m),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(x,n)))))),p),x),x),And(FreeQ(List(a,b,c,d,e,m),x),IGtQ(p,C1),IGtQ(n,C0))));
IIntegrate(3405,Int(Times(Power(x_,m_DEFAULT),Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,n),Cos(Plus(a,Times(b,Power(x,n)))),Power(Sin(Plus(a,Times(b,Power(x,n)))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),CN1)),x),Dist(Times(Plus(p,C2),Power(Plus(p,C1),CN1)),Int(Times(Power(x,m),Power(Sin(Plus(a,Times(b,Power(x,n)))),Plus(p,C2))),x),x),Negate(Simp(Times(n,Power(Sin(Plus(a,Times(b,Power(x,n)))),Plus(p,C2)),Power(Times(Sqr(b),Sqr(n),Plus(p,C1),Plus(p,C2)),CN1)),x))),And(FreeQ(List(a,b,m,n),x),EqQ(Plus(m,Times(CN1,C2,n),C1),C0),LtQ(p,CN1),NeQ(p,CN2))));
IIntegrate(3406,Int(Times(Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(Power(x,n),Sin(Plus(a,Times(b,Power(x,n)))),Power(Cos(Plus(a,Times(b,Power(x,n)))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),CN1)),x)),Dist(Times(Plus(p,C2),Power(Plus(p,C1),CN1)),Int(Times(Power(x,m),Power(Cos(Plus(a,Times(b,Power(x,n)))),Plus(p,C2))),x),x),Negate(Simp(Times(n,Power(Cos(Plus(a,Times(b,Power(x,n)))),Plus(p,C2)),Power(Times(Sqr(b),Sqr(n),Plus(p,C1),Plus(p,C2)),CN1)),x))),And(FreeQ(List(a,b,m,n),x),EqQ(Plus(m,Times(CN1,C2,n),C1),C0),LtQ(p,CN1),NeQ(p,CN2))));
IIntegrate(3407,Int(Times(Power(x_,m_DEFAULT),Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,Negate(n),C1)),Cos(Plus(a,Times(b,Power(x,n)))),Power(Sin(Plus(a,Times(b,Power(x,n)))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),CN1)),x),Dist(Times(Plus(p,C2),Power(Plus(p,C1),CN1)),Int(Times(Power(x,m),Power(Sin(Plus(a,Times(b,Power(x,n)))),Plus(p,C2))),x),x),Dist(Times(Plus(m,Negate(n),C1),Plus(m,Times(CN1,C2,n),C1),Power(Times(Sqr(b),Sqr(n),Plus(p,C1),Plus(p,C2)),CN1)),Int(Times(Power(x,Subtract(m,Times(C2,n))),Power(Sin(Plus(a,Times(b,Power(x,n)))),Plus(p,C2))),x),x),Negate(Simp(Times(Plus(m,Negate(n),C1),Power(x,Plus(m,Times(CN1,C2,n),C1)),Power(Sin(Plus(a,Times(b,Power(x,n)))),Plus(p,C2)),Power(Times(Sqr(b),Sqr(n),Plus(p,C1),Plus(p,C2)),CN1)),x))),And(FreeQ(List(a,b),x),LtQ(p,CN1),NeQ(p,CN2),IGtQ(n,C0),IGtQ(m,Subtract(Times(C2,n),C1)))));
IIntegrate(3408,Int(Times(Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(Power(x,Plus(m,Negate(n),C1)),Sin(Plus(a,Times(b,Power(x,n)))),Power(Cos(Plus(a,Times(b,Power(x,n)))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),CN1)),x)),Dist(Times(Plus(p,C2),Power(Plus(p,C1),CN1)),Int(Times(Power(x,m),Power(Cos(Plus(a,Times(b,Power(x,n)))),Plus(p,C2))),x),x),Dist(Times(Plus(m,Negate(n),C1),Plus(m,Times(CN1,C2,n),C1),Power(Times(Sqr(b),Sqr(n),Plus(p,C1),Plus(p,C2)),CN1)),Int(Times(Power(x,Subtract(m,Times(C2,n))),Power(Cos(Plus(a,Times(b,Power(x,n)))),Plus(p,C2))),x),x),Negate(Simp(Times(Plus(m,Negate(n),C1),Power(x,Plus(m,Times(CN1,C2,n),C1)),Power(Cos(Plus(a,Times(b,Power(x,n)))),Plus(p,C2)),Power(Times(Sqr(b),Sqr(n),Plus(p,C1),Plus(p,C2)),CN1)),x))),And(FreeQ(List(a,b),x),LtQ(p,CN1),NeQ(p,CN2),IGtQ(n,C0),IGtQ(m,Subtract(Times(C2,n),C1)))));
IIntegrate(3409,Int(Times(Power(x_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Negate(Subst(Int(Times(Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(Power(x,n),CN1)))))),p),Power(Power(x,Plus(m,C2)),CN1)),x),x,Power(x,CN1))),And(FreeQ(List(a,b,c,d),x),IGtQ(p,C0),ILtQ(n,C0),IntegerQ(m),EqQ(n,CN2))));
IIntegrate(3410,Int(Times(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Negate(Subst(Int(Times(Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(Power(x,n),CN1)))))),p),Power(Power(x,Plus(m,C2)),CN1)),x),x,Power(x,CN1))),And(FreeQ(List(a,b,c,d),x),IGtQ(p,C0),ILtQ(n,C0),IntegerQ(m),EqQ(n,CN2))));
IIntegrate(3411,Int(Times(Power(Times(e_DEFAULT,x_),m_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(With(List(Set(k,Denominator(m))),Negate(Dist(Times(k,Power(e,CN1)),Subst(Int(Times(Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(Times(Power(e,n),Power(x,Times(k,n))),CN1)))))),p),Power(Power(x,Plus(Times(k,Plus(m,C1)),C1)),CN1)),x),x,Power(Power(Times(e,x),Power(k,CN1)),CN1)),x))),And(FreeQ(List(a,b,c,d,e),x),IGtQ(p,C0),ILtQ(n,C0),FractionQ(m))));
IIntegrate(3412,Int(Times(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(Times(e_DEFAULT,x_),m_)),x_Symbol),
    Condition(With(List(Set(k,Denominator(m))),Negate(Dist(Times(k,Power(e,CN1)),Subst(Int(Times(Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(Times(Power(e,n),Power(x,Times(k,n))),CN1)))))),p),Power(Power(x,Plus(Times(k,Plus(m,C1)),C1)),CN1)),x),x,Power(Power(Times(e,x),Power(k,CN1)),CN1)),x))),And(FreeQ(List(a,b,c,d,e),x),IGtQ(p,C0),ILtQ(n,C0),FractionQ(m))));
IIntegrate(3413,Int(Times(Power(Times(e_DEFAULT,x_),m_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Negate(Dist(Times(Power(Times(e,x),m),Power(Power(x,CN1),m)),Subst(Int(Times(Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(Power(x,n),CN1)))))),p),Power(Power(x,Plus(m,C2)),CN1)),x),x,Power(x,CN1)),x)),And(FreeQ(List(a,b,c,d,e,m),x),IGtQ(p,C0),ILtQ(n,C0),Not(RationalQ(m)))));
IIntegrate(3414,Int(Times(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(Times(e_DEFAULT,x_),m_)),x_Symbol),
    Condition(Negate(Dist(Times(Power(Times(e,x),m),Power(Power(x,CN1),m)),Subst(Int(Times(Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(Power(x,n),CN1)))))),p),Power(Power(x,Plus(m,C2)),CN1)),x),x,Power(x,CN1)),x)),And(FreeQ(List(a,b,c,d,e,m),x),IGtQ(p,C0),ILtQ(n,C0),Not(RationalQ(m)))));
IIntegrate(3415,Int(Times(Power(x_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(k,Denominator(n))),Dist(k,Subst(Int(Times(Power(x,Subtract(Times(k,Plus(m,C1)),C1)),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(x,Times(k,n))))))),p)),x),x,Power(x,Power(k,CN1))),x)),And(FreeQ(List(a,b,c,d,m),x),IntegerQ(p),FractionQ(n))));
IIntegrate(3416,Int(Times(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(k,Denominator(n))),Dist(k,Subst(Int(Times(Power(x,Subtract(Times(k,Plus(m,C1)),C1)),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(x,Times(k,n))))))),p)),x),x,Power(x,Power(k,CN1))),x)),And(FreeQ(List(a,b,c,d,m),x),IntegerQ(p),FractionQ(n))));
IIntegrate(3417,Int(Times(Power(Times(e_,x_),m_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Int(Times(Power(x,m),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(x,n)))))),p)),x),x),And(FreeQ(List(a,b,c,d,e,m),x),IntegerQ(p),FractionQ(n))));
IIntegrate(3418,Int(Times(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(Times(e_,x_),m_)),x_Symbol),
    Condition(Dist(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Int(Times(Power(x,m),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(x,n)))))),p)),x),x),And(FreeQ(List(a,b,c,d,e,m),x),IntegerQ(p),FractionQ(n))));
IIntegrate(3419,Int(Times(Power(x_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Plus(m,C1),CN1),Subst(Int(Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(x,Simplify(Times(n,Power(Plus(m,C1),CN1))))))))),p),x),x,Power(x,Plus(m,C1))),x),And(FreeQ(List(a,b,c,d,m,n),x),IntegerQ(p),NeQ(m,CN1),IGtQ(Simplify(Times(n,Power(Plus(m,C1),CN1))),C0),Not(IntegerQ(n)))));
IIntegrate(3420,Int(Times(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Plus(m,C1),CN1),Subst(Int(Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(x,Simplify(Times(n,Power(Plus(m,C1),CN1))))))))),p),x),x,Power(x,Plus(m,C1))),x),And(FreeQ(List(a,b,c,d,m,n),x),IntegerQ(p),NeQ(m,CN1),IGtQ(Simplify(Times(n,Power(Plus(m,C1),CN1))),C0),Not(IntegerQ(n)))));
IIntegrate(3421,Int(Times(Power(Times(e_,x_),m_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Int(Times(Power(x,m),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(x,n)))))),p)),x),x),And(FreeQ(List(a,b,c,d,e,m,n),x),IntegerQ(p),NeQ(m,CN1),IGtQ(Simplify(Times(n,Power(Plus(m,C1),CN1))),C0),Not(IntegerQ(n)))));
IIntegrate(3422,Int(Times(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(Times(e_,x_),m_)),x_Symbol),
    Condition(Dist(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Int(Times(Power(x,m),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(x,n)))))),p)),x),x),And(FreeQ(List(a,b,c,d,e,m,n),x),IntegerQ(p),NeQ(m,CN1),IGtQ(Simplify(Times(n,Power(Plus(m,C1),CN1))),C0),Not(IntegerQ(n)))));
IIntegrate(3423,Int(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_))))),x_Symbol),
    Condition(Subtract(Dist(Times(C1D2,CI),Int(Times(Power(Times(e,x),m),Exp(Subtract(Times(CN1,c,CI),Times(d,CI,Power(x,n))))),x),x),Dist(Times(C1D2,CI),Int(Times(Power(Times(e,x),m),Exp(Plus(Times(c,CI),Times(d,CI,Power(x,n))))),x),x)),FreeQ(List(c,d,e,m,n),x)));
IIntegrate(3424,Int(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Plus(Dist(C1D2,Int(Times(Power(Times(e,x),m),Exp(Subtract(Times(CN1,c,CI),Times(d,CI,Power(x,n))))),x),x),Dist(C1D2,Int(Times(Power(Times(e,x),m),Exp(Plus(Times(c,CI),Times(d,CI,Power(x,n))))),x),x)),FreeQ(List(c,d,e,m,n),x)));
IIntegrate(3425,Int(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_)),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Times(e,x),m),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(x,n)))))),p),x),x),And(FreeQ(List(a,b,c,d,e,m,n),x),IGtQ(p,C0))));
IIntegrate(3426,Int(Times(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Times(e,x),m),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(x,n)))))),p),x),x),And(FreeQ(List(a,b,c,d,e,m,n),x),IGtQ(p,C0))));
IIntegrate(3427,Int(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(x,n)))))),p)),x),FreeQ(List(a,b,c,d,e,m,n,p),x)));
IIntegrate(3428,Int(Times(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(x,n)))))),p)),x),FreeQ(List(a,b,c,d,e,m,n,p),x)));
IIntegrate(3429,Int(Times(Power(Times(e_,x_),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(u_))),p_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Sin(ExpandToSum(u,x)))),p)),x),And(FreeQ(List(a,b,e,m,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x)))));
IIntegrate(3430,Int(Times(Power(Plus(a_DEFAULT,Times(Cos(u_),b_DEFAULT)),p_DEFAULT),Power(Times(e_,x_),m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Cos(ExpandToSum(u,x)))),p)),x),And(FreeQ(List(a,b,e,m,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x)))));
IIntegrate(3431,Int(Times(Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Times(n,f),CN1),Subst(Int(ExpandIntegrand(Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),p),Times(Power(x,Subtract(Power(n,CN1),C1)),Power(Plus(g,Times(CN1,e,h,Power(f,CN1)),Times(h,Power(x,Power(n,CN1)),Power(f,CN1))),m)),x),x),x,Power(Plus(e,Times(f,x)),n)),x),And(FreeQ(List(a,b,c,d,e,f,g,h,m),x),IGtQ(p,C0),IntegerQ(Power(n,CN1)))));
IIntegrate(3432,Int(Times(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))),b_DEFAULT)),p_DEFAULT),Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Times(n,f),CN1),Subst(Int(ExpandIntegrand(Power(Plus(a,Times(b,Cos(Plus(c,Times(d,x))))),p),Times(Power(x,Subtract(Power(n,CN1),C1)),Power(Plus(g,Times(CN1,e,h,Power(f,CN1)),Times(h,Power(x,Power(n,CN1)),Power(f,CN1))),m)),x),x),x,Power(Plus(e,Times(f,x)),n)),x),And(FreeQ(List(a,b,c,d,e,f,g,h,m),x),IGtQ(p,C0),IntegerQ(Power(n,CN1)))));
IIntegrate(3433,Int(Times(Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(k,If(FractionQ(n),Denominator(n),C1))),Dist(Times(k,Power(Power(f,Plus(m,C1)),CN1)),Subst(Int(ExpandIntegrand(Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(x,Times(k,n))))))),p),Times(Power(x,Subtract(k,C1)),Power(Plus(Times(f,g),Times(CN1,e,h),Times(h,Power(x,k))),m)),x),x),x,Power(Plus(e,Times(f,x)),Power(k,CN1))),x)),And(FreeQ(List(a,b,c,d,e,f,g,h),x),IGtQ(p,C0),IGtQ(m,C0))));
IIntegrate(3434,Int(Times(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))),b_DEFAULT)),p_DEFAULT),Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(k,If(FractionQ(n),Denominator(n),C1))),Dist(Times(k,Power(Power(f,Plus(m,C1)),CN1)),Subst(Int(ExpandIntegrand(Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(x,Times(k,n))))))),p),Times(Power(x,Subtract(k,C1)),Power(Plus(Times(f,g),Times(CN1,e,h),Times(h,Power(x,k))),m)),x),x),x,Power(Plus(e,Times(f,x)),Power(k,CN1))),x)),And(FreeQ(List(a,b,c,d,e,f,g,h),x),IGtQ(p,C0),IGtQ(m,C0))));
IIntegrate(3435,Int(Times(Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(f,CN1),Subst(Int(Times(Power(Times(h,x,Power(f,CN1)),m),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(x,n)))))),p)),x),x,Plus(e,Times(f,x))),x),And(FreeQ(List(a,b,c,d,e,f,g,h,m),x),IGtQ(p,C0),EqQ(Subtract(Times(f,g),Times(e,h)),C0))));
IIntegrate(3436,Int(Times(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))),b_DEFAULT)),p_DEFAULT),Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(f,CN1),Subst(Int(Times(Power(Times(h,x,Power(f,CN1)),m),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(x,n)))))),p)),x),x,Plus(e,Times(f,x))),x),And(FreeQ(List(a,b,c,d,e,f,g,h,m),x),IGtQ(p,C0),EqQ(Subtract(Times(f,g),Times(e,h)),C0))));
IIntegrate(3437,Int(Times(Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(g,Times(h,x)),m),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(Plus(e,Times(f,x)),n)))))),p)),x),FreeQ(List(a,b,c,d,e,f,g,h,m,n,p),x)));
IIntegrate(3438,Int(Times(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))),b_DEFAULT)),p_DEFAULT),Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(g,Times(h,x)),m),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(Plus(e,Times(f,x)),n)))))),p)),x),FreeQ(List(a,b,c,d,e,f,g,h,m,n,p),x)));
IIntegrate(3439,Int(Times(Power(v_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(u_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(ExpandToSum(v,x),m),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(ExpandToSum(u,x),n)))))),p)),x),And(FreeQ(List(a,b,c,d,m,n,p),x),LinearQ(u,x),LinearQ(v,x),Not(And(LinearMatchQ(u,x),LinearMatchQ(v,x))))));
IIntegrate(3440,Int(Times(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(u_,n_)))),b_DEFAULT)),p_DEFAULT),Power(v_,m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(ExpandToSum(v,x),m),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(ExpandToSum(u,x),n)))))),p)),x),And(FreeQ(List(a,b,c,d,m,n,p),x),LinearQ(u,x),LinearQ(v,x),Not(And(LinearMatchQ(u,x),LinearMatchQ(v,x))))));
IIntegrate(3441,Int(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),Power(x_,m_DEFAULT),Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Times(Power(Sin(Plus(a,Times(b,Power(x,n)))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),CN1)),x),And(FreeQ(List(a,b,m,n,p),x),EqQ(m,Subtract(n,C1)),NeQ(p,CN1))));
IIntegrate(3442,Int(Times(Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT),Power(x_,m_DEFAULT),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))))),x_Symbol),
    Condition(Negate(Simp(Times(Power(Cos(Plus(a,Times(b,Power(x,n)))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),CN1)),x)),And(FreeQ(List(a,b,m,n,p),x),EqQ(m,Subtract(n,C1)),NeQ(p,CN1))));
IIntegrate(3443,Int(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),Power(x_,m_DEFAULT),Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,Negate(n),C1)),Power(Sin(Plus(a,Times(b,Power(x,n)))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),CN1)),x),Dist(Times(Plus(m,Negate(n),C1),Power(Times(b,n,Plus(p,C1)),CN1)),Int(Times(Power(x,Subtract(m,n)),Power(Sin(Plus(a,Times(b,Power(x,n)))),Plus(p,C1))),x),x)),And(FreeQ(List(a,b,p),x),LtQ(C0,n,Plus(m,C1)),NeQ(p,CN1))));
IIntegrate(3444,Int(Times(Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT),Power(x_,m_DEFAULT),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))))),x_Symbol),
    Condition(Plus(Negate(Simp(Times(Power(x,Plus(m,Negate(n),C1)),Power(Cos(Plus(a,Times(b,Power(x,n)))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),CN1)),x)),Dist(Times(Plus(m,Negate(n),C1),Power(Times(b,n,Plus(p,C1)),CN1)),Int(Times(Power(x,Subtract(m,n)),Power(Cos(Plus(a,Times(b,Power(x,n)))),Plus(p,C1))),x),x)),And(FreeQ(List(a,b,p),x),LtQ(C0,n,Plus(m,C1)),NeQ(p,CN1))));
IIntegrate(3445,Int(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Int(Sin(Times(Sqr(Plus(b,Times(C2,c,x))),Power(Times(C4,c),CN1))),x),And(FreeQ(List(a,b,c),x),EqQ(Subtract(Sqr(b),Times(C4,a,c)),C0))));
IIntegrate(3446,Int(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Int(Cos(Times(Sqr(Plus(b,Times(C2,c,x))),Power(Times(C4,c),CN1))),x),And(FreeQ(List(a,b,c),x),EqQ(Subtract(Sqr(b),Times(C4,a,c)),C0))));
IIntegrate(3447,Int(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Subtract(Dist(Cos(Times(Subtract(Sqr(b),Times(C4,a,c)),Power(Times(C4,c),CN1))),Int(Sin(Times(Sqr(Plus(b,Times(C2,c,x))),Power(Times(C4,c),CN1))),x),x),Dist(Sin(Times(Subtract(Sqr(b),Times(C4,a,c)),Power(Times(C4,c),CN1))),Int(Cos(Times(Sqr(Plus(b,Times(C2,c,x))),Power(Times(C4,c),CN1))),x),x)),And(FreeQ(List(a,b,c),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0))));
IIntegrate(3448,Int(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Plus(Dist(Cos(Times(Subtract(Sqr(b),Times(C4,a,c)),Power(Times(C4,c),CN1))),Int(Cos(Times(Sqr(Plus(b,Times(C2,c,x))),Power(Times(C4,c),CN1))),x),x),Dist(Sin(Times(Subtract(Sqr(b),Times(C4,a,c)),Power(Times(C4,c),CN1))),Int(Sin(Times(Sqr(Plus(b,Times(C2,c,x))),Power(Times(C4,c),CN1))),x),x)),And(FreeQ(List(a,b,c),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0))));
IIntegrate(3449,Int(Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),n),x),x),And(FreeQ(List(a,b,c),x),IGtQ(n,C1))));
IIntegrate(3450,Int(Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),n),x),x),And(FreeQ(List(a,b,c),x),IGtQ(n,C1))));
  }
}
}
