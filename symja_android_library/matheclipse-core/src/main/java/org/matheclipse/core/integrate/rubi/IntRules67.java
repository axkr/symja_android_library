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
public class IntRules67 { 

	public static void initialize() {
		Initializer.init();
	}

	private static class Initializer  {

		private static void init() {
IIntegrate(3351,Int(Sin(Times(d_DEFAULT,Sqr(Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),x_Symbol),
    Condition(Simp(Times(Sqrt(CPiHalf),FresnelS(Times(Sqrt(Times(C2,Power(Pi,CN1))),Rt(d,C2),Plus(e,Times(f,x)))),Power(Times(f,Rt(d,C2)),CN1)),x),FreeQ(List(d,e,f),x)));
IIntegrate(3352,Int(Cos(Times(d_DEFAULT,Sqr(Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),x_Symbol),
    Condition(Simp(Times(Sqrt(CPiHalf),FresnelC(Times(Sqrt(Times(C2,Power(Pi,CN1))),Rt(d,C2),Plus(e,Times(f,x)))),Power(Times(f,Rt(d,C2)),CN1)),x),FreeQ(List(d,e,f),x)));
IIntegrate(3353,Int(Sin(Plus(c_,Times(d_DEFAULT,Sqr(Plus(e_DEFAULT,Times(f_DEFAULT,x_)))))),x_Symbol),
    Condition(Plus(Dist(Sin(c),Int(Cos(Times(d,Sqr(Plus(e,Times(f,x))))),x),x),Dist(Cos(c),Int(Sin(Times(d,Sqr(Plus(e,Times(f,x))))),x),x)),FreeQ(List(c,d,e,f),x)));
IIntegrate(3354,Int(Cos(Plus(c_,Times(d_DEFAULT,Sqr(Plus(e_DEFAULT,Times(f_DEFAULT,x_)))))),x_Symbol),
    Condition(Subtract(Dist(Cos(c),Int(Cos(Times(d,Sqr(Plus(e,Times(f,x))))),x),x),Dist(Sin(c),Int(Sin(Times(d,Sqr(Plus(e,Times(f,x))))),x),x)),FreeQ(List(c,d,e,f),x)));
IIntegrate(3355,Int(Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))),x_Symbol),
    Condition(Subtract(Dist(Times(C1D2,CI),Int(Exp(Subtract(Times(CN1,c,CI),Times(d,CI,Power(Plus(e,Times(f,x)),n)))),x),x),Dist(Times(C1D2,CI),Int(Exp(Plus(Times(c,CI),Times(d,CI,Power(Plus(e,Times(f,x)),n)))),x),x)),And(FreeQ(List(c,d,e,f),x),IGtQ(n,C2))));
IIntegrate(3356,Int(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))),x_Symbol),
    Condition(Plus(Dist(C1D2,Int(Exp(Subtract(Times(CN1,c,CI),Times(d,CI,Power(Plus(e,Times(f,x)),n)))),x),x),Dist(C1D2,Int(Exp(Plus(Times(c,CI),Times(d,CI,Power(Plus(e,Times(f,x)),n)))),x),x)),And(FreeQ(List(c,d,e,f),x),IGtQ(n,C2))));
IIntegrate(3357,Int(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))))),p_),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(Plus(e,Times(f,x)),n)))))),p),x),x),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C1),IGtQ(n,C1))));
IIntegrate(3358,Int(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))),b_DEFAULT)),p_),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(Plus(e,Times(f,x)),n)))))),p),x),x),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C1),IGtQ(n,C1))));
IIntegrate(3359,Int(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))))),p_DEFAULT),x_Symbol),
    Condition(Negate(Dist(Power(f,CN1),Subst(Int(Times(Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(Power(x,n),CN1)))))),p),Power(x,CN2)),x),x,Power(Plus(e,Times(f,x)),CN1)),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),ILtQ(n,C0),EqQ(n,CN2))));
IIntegrate(3360,Int(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Negate(Dist(Power(f,CN1),Subst(Int(Times(Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(Power(x,n),CN1)))))),p),Power(x,CN2)),x),x,Power(Plus(e,Times(f,x)),CN1)),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),ILtQ(n,C0),EqQ(n,CN2))));
IIntegrate(3361,Int(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))))),p_DEFAULT),x_Symbol),
    Condition(Dist(Power(Times(n,f),CN1),Subst(Int(Times(Power(x,Subtract(Power(n,CN1),C1)),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),p)),x),x,Power(Plus(e,Times(f,x)),n)),x),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),IntegerQ(Power(n,CN1)))));
IIntegrate(3362,Int(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Dist(Power(Times(n,f),CN1),Subst(Int(Times(Power(x,Subtract(Power(n,CN1),C1)),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,x))))),p)),x),x,Power(Plus(e,Times(f,x)),n)),x),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),IntegerQ(Power(n,CN1)))));
IIntegrate(3363,Int(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))))),p_DEFAULT),x_Symbol),
    Condition(Module(List(Set(k,Denominator(n))),Dist(Times(k,Power(f,CN1)),Subst(Int(Times(Power(x,Subtract(k,C1)),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(x,Times(k,n))))))),p)),x),x,Power(Plus(e,Times(f,x)),Power(k,CN1))),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),FractionQ(n))));
IIntegrate(3364,Int(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Module(List(Set(k,Denominator(n))),Dist(Times(k,Power(f,CN1)),Subst(Int(Times(Power(x,Subtract(k,C1)),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(x,Times(k,n))))))),p)),x),x,Power(Plus(e,Times(f,x)),Power(k,CN1))),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),FractionQ(n))));
IIntegrate(3365,Int(Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))),x_Symbol),
    Condition(Subtract(Dist(Times(C1D2,CI),Int(Exp(Subtract(Times(CN1,c,CI),Times(d,CI,Power(Plus(e,Times(f,x)),n)))),x),x),Dist(Times(C1D2,CI),Int(Exp(Plus(Times(c,CI),Times(d,CI,Power(Plus(e,Times(f,x)),n)))),x),x)),FreeQ(List(c,d,e,f,n),x)));
IIntegrate(3366,Int(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))),x_Symbol),
    Condition(Plus(Dist(C1D2,Int(Exp(Subtract(Times(CN1,c,CI),Times(d,CI,Power(Plus(e,Times(f,x)),n)))),x),x),Dist(C1D2,Int(Exp(Plus(Times(c,CI),Times(d,CI,Power(Plus(e,Times(f,x)),n)))),x),x)),FreeQ(List(c,d,e,f,n),x)));
IIntegrate(3367,Int(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))))),p_),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(Plus(e,Times(f,x)),n)))))),p),x),x),And(FreeQ(List(a,b,c,d,e,f,n),x),IGtQ(p,C1))));
IIntegrate(3368,Int(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))),b_DEFAULT)),p_),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(Plus(e,Times(f,x)),n)))))),p),x),x),And(FreeQ(List(a,b,c,d,e,f,n),x),IGtQ(p,C1))));
IIntegrate(3369,Int(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))))),p_),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(Plus(e,Times(f,x)),n)))))),p),x),FreeQ(List(a,b,c,d,e,f,n,p),x)));
IIntegrate(3370,Int(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))),b_DEFAULT)),p_),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(Plus(e,Times(f,x)),n)))))),p),x),FreeQ(List(a,b,c,d,e,f,n,p),x)));
IIntegrate(3371,Int(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(u_,n_)))))),p_DEFAULT),x_Symbol),
    Condition(Int(Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(ExpandToSum(u,x),n)))))),p),x),And(FreeQ(List(a,b,c,d,n,p),x),LinearQ(u,x),Not(LinearMatchQ(u,x)))));
IIntegrate(3372,Int(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(u_,n_)))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Int(Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(ExpandToSum(u,x),n)))))),p),x),And(FreeQ(List(a,b,c,d,n,p),x),LinearQ(u,x),Not(LinearMatchQ(u,x)))));
IIntegrate(3373,Int(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(u_))),p_DEFAULT),x_Symbol),
    Condition(Int(Power(Plus(a,Times(b,Sin(ExpandToSum(u,x)))),p),x),And(FreeQ(List(a,b,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x)))));
IIntegrate(3374,Int(Power(Plus(a_DEFAULT,Times(Cos(u_),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Int(Power(Plus(a,Times(b,Cos(ExpandToSum(u,x)))),p),x),And(FreeQ(List(a,b,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x)))));
IIntegrate(3375,Int(Times(Power(x_,CN1),Sin(Times(d_DEFAULT,Power(x_,n_)))),x_Symbol),
    Condition(Simp(Times(SinIntegral(Times(d,Power(x,n))),Power(n,CN1)),x),FreeQ(List(d,n),x)));
IIntegrate(3376,Int(Times(Cos(Times(d_DEFAULT,Power(x_,n_))),Power(x_,CN1)),x_Symbol),
    Condition(Simp(Times(CosIntegral(Times(d,Power(x,n))),Power(n,CN1)),x),FreeQ(List(d,n),x)));
IIntegrate(3377,Int(Times(Power(x_,CN1),Sin(Plus(c_,Times(d_DEFAULT,Power(x_,n_))))),x_Symbol),
    Condition(Plus(Dist(Sin(c),Int(Times(Cos(Times(d,Power(x,n))),Power(x,CN1)),x),x),Dist(Cos(c),Int(Times(Sin(Times(d,Power(x,n))),Power(x,CN1)),x),x)),FreeQ(List(c,d,n),x)));
IIntegrate(3378,Int(Times(Cos(Plus(c_,Times(d_DEFAULT,Power(x_,n_)))),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Dist(Cos(c),Int(Times(Cos(Times(d,Power(x,n))),Power(x,CN1)),x),x),Dist(Sin(c),Int(Times(Sin(Times(d,Power(x,n))),Power(x,CN1)),x),x)),FreeQ(List(c,d,n),x)));
IIntegrate(3379,Int(Times(Power(x_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(n,CN1),Subst(Int(Times(Power(x,Subtract(Simplify(Times(Plus(m,C1),Power(n,CN1))),C1)),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),p)),x),x,Power(x,n)),x),And(FreeQ(List(a,b,c,d,m,n,p),x),IntegerQ(Simplify(Times(Plus(m,C1),Power(n,CN1)))),Or(EqQ(p,C1),EqQ(m,Subtract(n,C1)),And(IntegerQ(p),GtQ(Simplify(Times(Plus(m,C1),Power(n,CN1))),C0))))));
IIntegrate(3380,Int(Times(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(n,CN1),Subst(Int(Times(Power(x,Subtract(Simplify(Times(Plus(m,C1),Power(n,CN1))),C1)),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,x))))),p)),x),x,Power(x,n)),x),And(FreeQ(List(a,b,c,d,m,n,p),x),IntegerQ(Simplify(Times(Plus(m,C1),Power(n,CN1)))),Or(EqQ(p,C1),EqQ(m,Subtract(n,C1)),And(IntegerQ(p),GtQ(Simplify(Times(Plus(m,C1),Power(n,CN1))),C0))))));
IIntegrate(3381,Int(Times(Power(Times(e_,x_),m_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Int(Times(Power(x,m),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(x,n)))))),p)),x),x),And(FreeQ(List(a,b,c,d,e,m,n,p),x),IntegerQ(Simplify(Times(Plus(m,C1),Power(n,CN1)))))));
IIntegrate(3382,Int(Times(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(Times(e_,x_),m_)),x_Symbol),
    Condition(Dist(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Int(Times(Power(x,m),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(x,n)))))),p)),x),x),And(FreeQ(List(a,b,c,d,e,m,n,p),x),IntegerQ(Simplify(Times(Plus(m,C1),Power(n,CN1)))))));
IIntegrate(3383,Int(Times(Power(x_,m_DEFAULT),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_))))),x_Symbol),
    Condition(Dist(Times(C2,Power(n,CN1)),Subst(Int(Sin(Plus(a,Times(b,Sqr(x)))),x),x,Power(x,Times(C1D2,n))),x),And(FreeQ(List(a,b,m,n),x),EqQ(m,Subtract(Times(C1D2,n),C1)))));
IIntegrate(3384,Int(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Dist(Times(C2,Power(n,CN1)),Subst(Int(Cos(Plus(a,Times(b,Sqr(x)))),x),x,Power(x,Times(C1D2,n))),x),And(FreeQ(List(a,b,m,n),x),EqQ(m,Subtract(Times(C1D2,n),C1)))));
IIntegrate(3385,Int(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_))))),x_Symbol),
    Condition(Plus(Negate(Simp(Times(Power(e,Subtract(n,C1)),Power(Times(e,x),Plus(m,Negate(n),C1)),Cos(Plus(c,Times(d,Power(x,n)))),Power(Times(d,n),CN1)),x)),Dist(Times(Power(e,n),Plus(m,Negate(n),C1),Power(Times(d,n),CN1)),Int(Times(Power(Times(e,x),Subtract(m,n)),Cos(Plus(c,Times(d,Power(x,n))))),x),x)),And(FreeQ(List(c,d,e),x),IGtQ(n,C0),LtQ(n,Plus(m,C1)))));
IIntegrate(3386,Int(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(e,Subtract(n,C1)),Power(Times(e,x),Plus(m,Negate(n),C1)),Sin(Plus(c,Times(d,Power(x,n)))),Power(Times(d,n),CN1)),x),Dist(Times(Power(e,n),Plus(m,Negate(n),C1),Power(Times(d,n),CN1)),Int(Times(Power(Times(e,x),Subtract(m,n)),Sin(Plus(c,Times(d,Power(x,n))))),x),x)),And(FreeQ(List(c,d,e),x),IGtQ(n,C0),LtQ(n,Plus(m,C1)))));
IIntegrate(3387,Int(Times(Power(Times(e_DEFAULT,x_),m_),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_))))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(e,x),Plus(m,C1)),Sin(Plus(c,Times(d,Power(x,n)))),Power(Times(e,Plus(m,C1)),CN1)),x),Dist(Times(d,n,Power(Times(Power(e,n),Plus(m,C1)),CN1)),Int(Times(Power(Times(e,x),Plus(m,n)),Cos(Plus(c,Times(d,Power(x,n))))),x),x)),And(FreeQ(List(c,d,e),x),IGtQ(n,C0),LtQ(m,CN1))));
IIntegrate(3388,Int(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),Power(Times(e_DEFAULT,x_),m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(e,x),Plus(m,C1)),Cos(Plus(c,Times(d,Power(x,n)))),Power(Times(e,Plus(m,C1)),CN1)),x),Dist(Times(d,n,Power(Times(Power(e,n),Plus(m,C1)),CN1)),Int(Times(Power(Times(e,x),Plus(m,n)),Sin(Plus(c,Times(d,Power(x,n))))),x),x)),And(FreeQ(List(c,d,e),x),IGtQ(n,C0),LtQ(m,CN1))));
IIntegrate(3389,Int(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_))))),x_Symbol),
    Condition(Subtract(Dist(Times(C1D2,CI),Int(Times(Power(Times(e,x),m),Exp(Subtract(Times(CN1,c,CI),Times(d,CI,Power(x,n))))),x),x),Dist(Times(C1D2,CI),Int(Times(Power(Times(e,x),m),Exp(Plus(Times(c,CI),Times(d,CI,Power(x,n))))),x),x)),And(FreeQ(List(c,d,e,m),x),IGtQ(n,C0))));
IIntegrate(3390,Int(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Plus(Dist(C1D2,Int(Times(Power(Times(e,x),m),Exp(Subtract(Times(CN1,c,CI),Times(d,CI,Power(x,n))))),x),x),Dist(C1D2,Int(Times(Power(Times(e,x),m),Exp(Plus(Times(c,CI),Times(d,CI,Power(x,n))))),x),x)),And(FreeQ(List(c,d,e,m),x),IGtQ(n,C0))));
IIntegrate(3391,Int(Times(Power(x_,m_DEFAULT),Sqr(Sin(Plus(a_DEFAULT,Times(C1D2,b_DEFAULT,Power(x_,n_)))))),x_Symbol),
    Condition(Subtract(Dist(C1D2,Int(Power(x,m),x),x),Dist(C1D2,Int(Times(Power(x,m),Cos(Plus(Times(C2,a),Times(b,Power(x,n))))),x),x)),FreeQ(List(a,b,m,n),x)));
IIntegrate(3392,Int(Times(Sqr(Cos(Plus(a_DEFAULT,Times(C1D2,b_DEFAULT,Power(x_,n_))))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Dist(C1D2,Int(Power(x,m),x),x),Dist(C1D2,Int(Times(Power(x,m),Cos(Plus(Times(C2,a),Times(b,Power(x,n))))),x),x)),FreeQ(List(a,b,m,n),x)));
IIntegrate(3393,Int(Times(Power(x_,m_DEFAULT),Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Power(Sin(Plus(a,Times(b,Power(x,n)))),p),Power(Plus(m,C1),CN1)),x),Dist(Times(b,n,p,Power(Plus(m,C1),CN1)),Int(Times(Power(Sin(Plus(a,Times(b,Power(x,n)))),Subtract(p,C1)),Cos(Plus(a,Times(b,Power(x,n))))),x),x)),And(FreeQ(List(a,b),x),IGtQ(p,C1),EqQ(Plus(m,n),C0),NeQ(n,C1),IntegerQ(n))));
IIntegrate(3394,Int(Times(Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Power(Cos(Plus(a,Times(b,Power(x,n)))),p),Power(Plus(m,C1),CN1)),x),Dist(Times(b,n,p,Power(Plus(m,C1),CN1)),Int(Times(Power(Cos(Plus(a,Times(b,Power(x,n)))),Subtract(p,C1)),Sin(Plus(a,Times(b,Power(x,n))))),x),x)),And(FreeQ(List(a,b),x),IGtQ(p,C1),EqQ(Plus(m,n),C0),NeQ(n,C1),IntegerQ(n))));
IIntegrate(3395,Int(Times(Power(x_,m_DEFAULT),Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(n,Power(Sin(Plus(a,Times(b,Power(x,n)))),p),Power(Times(Sqr(b),Sqr(n),Sqr(p)),CN1)),x),Dist(Times(Subtract(p,C1),Power(p,CN1)),Int(Times(Power(x,m),Power(Sin(Plus(a,Times(b,Power(x,n)))),Subtract(p,C2))),x),x),Negate(Simp(Times(Power(x,n),Cos(Plus(a,Times(b,Power(x,n)))),Power(Sin(Plus(a,Times(b,Power(x,n)))),Subtract(p,C1)),Power(Times(b,n,p),CN1)),x))),And(FreeQ(List(a,b,m,n),x),EqQ(Plus(m,Times(CN1,C2,n),C1),C0),GtQ(p,C1))));
IIntegrate(3396,Int(Times(Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(n,Power(Cos(Plus(a,Times(b,Power(x,n)))),p),Power(Times(Sqr(b),Sqr(n),Sqr(p)),CN1)),x),Dist(Times(Subtract(p,C1),Power(p,CN1)),Int(Times(Power(x,m),Power(Cos(Plus(a,Times(b,Power(x,n)))),Subtract(p,C2))),x),x),Simp(Times(Power(x,n),Sin(Plus(a,Times(b,Power(x,n)))),Power(Cos(Plus(a,Times(b,Power(x,n)))),Subtract(p,C1)),Power(Times(b,n,p),CN1)),x)),And(FreeQ(List(a,b,m,n),x),EqQ(Plus(m,Times(CN1,C2,n),C1),C0),GtQ(p,C1))));
IIntegrate(3397,Int(Times(Power(x_,m_DEFAULT),Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(Plus(m,Negate(n),C1),Power(x,Plus(m,Times(CN1,C2,n),C1)),Power(Sin(Plus(a,Times(b,Power(x,n)))),p),Power(Times(Sqr(b),Sqr(n),Sqr(p)),CN1)),x),Dist(Times(Subtract(p,C1),Power(p,CN1)),Int(Times(Power(x,m),Power(Sin(Plus(a,Times(b,Power(x,n)))),Subtract(p,C2))),x),x),Negate(Dist(Times(Plus(m,Negate(n),C1),Plus(m,Times(CN1,C2,n),C1),Power(Times(Sqr(b),Sqr(n),Sqr(p)),CN1)),Int(Times(Power(x,Subtract(m,Times(C2,n))),Power(Sin(Plus(a,Times(b,Power(x,n)))),p)),x),x)),Negate(Simp(Times(Power(x,Plus(m,Negate(n),C1)),Cos(Plus(a,Times(b,Power(x,n)))),Power(Sin(Plus(a,Times(b,Power(x,n)))),Subtract(p,C1)),Power(Times(b,n,p),CN1)),x))),And(FreeQ(List(a,b),x),GtQ(p,C1),IGtQ(n,C0),IGtQ(m,Subtract(Times(C2,n),C1)))));
IIntegrate(3398,Int(Times(Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Plus(m,Negate(n),C1),Power(x,Plus(m,Times(CN1,C2,n),C1)),Power(Cos(Plus(a,Times(b,Power(x,n)))),p),Power(Times(Sqr(b),Sqr(n),Sqr(p)),CN1)),x),Dist(Times(Subtract(p,C1),Power(p,CN1)),Int(Times(Power(x,m),Power(Cos(Plus(a,Times(b,Power(x,n)))),Subtract(p,C2))),x),x),Negate(Dist(Times(Plus(m,Negate(n),C1),Plus(m,Times(CN1,C2,n),C1),Power(Times(Sqr(b),Sqr(n),Sqr(p)),CN1)),Int(Times(Power(x,Subtract(m,Times(C2,n))),Power(Cos(Plus(a,Times(b,Power(x,n)))),p)),x),x)),Simp(Times(Power(x,Plus(m,Negate(n),C1)),Sin(Plus(a,Times(b,Power(x,n)))),Power(Cos(Plus(a,Times(b,Power(x,n)))),Subtract(p,C1)),Power(Times(b,n,p),CN1)),x)),And(FreeQ(List(a,b),x),GtQ(p,C1),IGtQ(n,C0),IGtQ(m,Subtract(Times(C2,n),C1)))));
IIntegrate(3399,Int(Times(Power(x_,m_DEFAULT),Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Power(Sin(Plus(a,Times(b,Power(x,n)))),p),Power(Plus(m,C1),CN1)),x),Dist(Times(Sqr(b),Sqr(n),p,Subtract(p,C1),Power(Times(Plus(m,C1),Plus(m,n,C1)),CN1)),Int(Times(Power(x,Plus(m,Times(C2,n))),Power(Sin(Plus(a,Times(b,Power(x,n)))),Subtract(p,C2))),x),x),Negate(Dist(Times(Sqr(b),Sqr(n),Sqr(p),Power(Times(Plus(m,C1),Plus(m,n,C1)),CN1)),Int(Times(Power(x,Plus(m,Times(C2,n))),Power(Sin(Plus(a,Times(b,Power(x,n)))),p)),x),x)),Negate(Simp(Times(b,n,p,Power(x,Plus(m,n,C1)),Cos(Plus(a,Times(b,Power(x,n)))),Power(Sin(Plus(a,Times(b,Power(x,n)))),Subtract(p,C1)),Power(Times(Plus(m,C1),Plus(m,n,C1)),CN1)),x))),And(FreeQ(List(a,b),x),GtQ(p,C1),IGtQ(n,C0),ILtQ(m,Plus(Times(CN2,n),C1)),NeQ(Plus(m,n,C1),C0))));
IIntegrate(3400,Int(Times(Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Power(Cos(Plus(a,Times(b,Power(x,n)))),p),Power(Plus(m,C1),CN1)),x),Dist(Times(Sqr(b),Sqr(n),p,Subtract(p,C1),Power(Times(Plus(m,C1),Plus(m,n,C1)),CN1)),Int(Times(Power(x,Plus(m,Times(C2,n))),Power(Cos(Plus(a,Times(b,Power(x,n)))),Subtract(p,C2))),x),x),Negate(Dist(Times(Sqr(b),Sqr(n),Sqr(p),Power(Times(Plus(m,C1),Plus(m,n,C1)),CN1)),Int(Times(Power(x,Plus(m,Times(C2,n))),Power(Cos(Plus(a,Times(b,Power(x,n)))),p)),x),x)),Simp(Times(b,n,p,Power(x,Plus(m,n,C1)),Sin(Plus(a,Times(b,Power(x,n)))),Power(Cos(Plus(a,Times(b,Power(x,n)))),Subtract(p,C1)),Power(Times(Plus(m,C1),Plus(m,n,C1)),CN1)),x)),And(FreeQ(List(a,b),x),GtQ(p,C1),IGtQ(n,C0),ILtQ(m,Plus(Times(CN2,n),C1)),NeQ(Plus(m,n,C1),C0))));
  }
}
}
