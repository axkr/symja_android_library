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
public class IntRules69 { 

	public static void initialize() {
		Initializer.init();
	}

	private static class Initializer  {

		private static void init() {
IIntegrate(3451,Int(Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_DEFAULT),x_Symbol),
    Condition(Unintegrable(Power(Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),n),x),FreeQ(List(a,b,c,n),x)));
IIntegrate(3452,Int(Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_DEFAULT),x_Symbol),
    Condition(Unintegrable(Power(Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),n),x),FreeQ(List(a,b,c,n),x)));
IIntegrate(3453,Int(Power(Sin(v_),n_DEFAULT),x_Symbol),
    Condition(Int(Power(Sin(ExpandToSum(v,x)),n),x),And(IGtQ(n,C0),QuadraticQ(v,x),Not(QuadraticMatchQ(v,x)))));
IIntegrate(3454,Int(Power(Cos(v_),n_DEFAULT),x_Symbol),
    Condition(Int(Power(Cos(ExpandToSum(v,x)),n),x),And(IGtQ(n,C0),QuadraticQ(v,x),Not(QuadraticMatchQ(v,x)))));
IIntegrate(3455,Int(Times(Plus(d_,Times(e_DEFAULT,x_)),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Negate(Simp(Times(e,Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(Subtract(Times(C2,c,d),Times(b,e)),C0))));
IIntegrate(3456,Int(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Plus(d_,Times(e_DEFAULT,x_))),x_Symbol),
    Condition(Simp(Times(e,Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(Subtract(Times(C2,c,d),Times(b,e)),C0))));
IIntegrate(3457,Int(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Negate(Simp(Times(e,Power(Plus(d,Times(e,x)),Subtract(m,C1)),Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),x)),Dist(Times(Sqr(e),Subtract(m,C1),Power(Times(C2,c),CN1)),Int(Times(Power(Plus(d,Times(e,x)),Subtract(m,C2)),Cos(Plus(a,Times(b,x),Times(c,Sqr(x))))),x),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(Subtract(Times(C2,c,d),Times(b,e)),C0),GtQ(m,C1))));
IIntegrate(3458,Int(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Subtract(Simp(Times(e,Power(Plus(d,Times(e,x)),Subtract(m,C1)),Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),x),Dist(Times(Sqr(e),Subtract(m,C1),Power(Times(C2,c),CN1)),Int(Times(Power(Plus(d,Times(e,x)),Subtract(m,C2)),Sin(Plus(a,Times(b,x),Times(c,Sqr(x))))),x),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(Subtract(Times(C2,c,d),Times(b,e)),C0),GtQ(m,C1))));
IIntegrate(3459,Int(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(e,Plus(m,C1)),CN1)),x),Dist(Times(C2,c,Power(Times(Sqr(e),Plus(m,C1)),CN1)),Int(Times(Power(Plus(d,Times(e,x)),Plus(m,C2)),Cos(Plus(a,Times(b,x),Times(c,Sqr(x))))),x),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(Subtract(Times(C2,c,d),Times(b,e)),C0),LtQ(m,CN1))));
IIntegrate(3460,Int(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(e,Plus(m,C1)),CN1)),x),Dist(Times(C2,c,Power(Times(Sqr(e),Plus(m,C1)),CN1)),Int(Times(Power(Plus(d,Times(e,x)),Plus(m,C2)),Sin(Plus(a,Times(b,x),Times(c,Sqr(x))))),x),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(Subtract(Times(C2,c,d),Times(b,e)),C0),LtQ(m,CN1))));
IIntegrate(3461,Int(Times(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Negate(Simp(Times(e,Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),x)),Dist(Times(Subtract(Times(C2,c,d),Times(b,e)),Power(Times(C2,c),CN1)),Int(Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),x),x)),And(FreeQ(List(a,b,c,d,e),x),NeQ(Subtract(Times(C2,c,d),Times(b,e)),C0))));
IIntegrate(3462,Int(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Plus(d_DEFAULT,Times(e_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Times(e,Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),x),Dist(Times(Subtract(Times(C2,c,d),Times(b,e)),Power(Times(C2,c),CN1)),Int(Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),x),x)),And(FreeQ(List(a,b,c,d,e),x),NeQ(Subtract(Times(C2,c,d),Times(b,e)),C0))));
IIntegrate(3463,Int(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Negate(Simp(Times(e,Power(Plus(d,Times(e,x)),Subtract(m,C1)),Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),x)),Dist(Times(Sqr(e),Subtract(m,C1),Power(Times(C2,c),CN1)),Int(Times(Power(Plus(d,Times(e,x)),Subtract(m,C2)),Cos(Plus(a,Times(b,x),Times(c,Sqr(x))))),x),x),Negate(Dist(Times(Subtract(Times(b,e),Times(C2,c,d)),Power(Times(C2,c),CN1)),Int(Times(Power(Plus(d,Times(e,x)),Subtract(m,C1)),Sin(Plus(a,Times(b,x),Times(c,Sqr(x))))),x),x))),And(FreeQ(List(a,b,c,d,e),x),NeQ(Subtract(Times(b,e),Times(C2,c,d)),C0),GtQ(m,C1))));
IIntegrate(3464,Int(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Plus(Simp(Times(e,Power(Plus(d,Times(e,x)),Subtract(m,C1)),Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),x),Negate(Dist(Times(Sqr(e),Subtract(m,C1),Power(Times(C2,c),CN1)),Int(Times(Power(Plus(d,Times(e,x)),Subtract(m,C2)),Sin(Plus(a,Times(b,x),Times(c,Sqr(x))))),x),x)),Negate(Dist(Times(Subtract(Times(b,e),Times(C2,c,d)),Power(Times(C2,c),CN1)),Int(Times(Power(Plus(d,Times(e,x)),Subtract(m,C1)),Cos(Plus(a,Times(b,x),Times(c,Sqr(x))))),x),x))),And(FreeQ(List(a,b,c,d,e),x),NeQ(Subtract(Times(b,e),Times(C2,c,d)),C0),GtQ(m,C1))));
IIntegrate(3465,Int(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(e,Plus(m,C1)),CN1)),x),Negate(Dist(Times(C2,c,Power(Times(Sqr(e),Plus(m,C1)),CN1)),Int(Times(Power(Plus(d,Times(e,x)),Plus(m,C2)),Cos(Plus(a,Times(b,x),Times(c,Sqr(x))))),x),x)),Negate(Dist(Times(Subtract(Times(b,e),Times(C2,c,d)),Power(Times(Sqr(e),Plus(m,C1)),CN1)),Int(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Cos(Plus(a,Times(b,x),Times(c,Sqr(x))))),x),x))),And(FreeQ(List(a,b,c,d,e),x),NeQ(Subtract(Times(b,e),Times(C2,c,d)),C0),LtQ(m,CN1))));
IIntegrate(3466,Int(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(e,Plus(m,C1)),CN1)),x),Dist(Times(C2,c,Power(Times(Sqr(e),Plus(m,C1)),CN1)),Int(Times(Power(Plus(d,Times(e,x)),Plus(m,C2)),Sin(Plus(a,Times(b,x),Times(c,Sqr(x))))),x),x),Dist(Times(Subtract(Times(b,e),Times(C2,c,d)),Power(Times(Sqr(e),Plus(m,C1)),CN1)),Int(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Sin(Plus(a,Times(b,x),Times(c,Sqr(x))))),x),x)),And(FreeQ(List(a,b,c,d,e),x),NeQ(Subtract(Times(b,e),Times(C2,c,d)),C0),LtQ(m,CN1))));
IIntegrate(3467,Int(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT),Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_)),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Plus(d,Times(e,x)),m),Power(Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),n),x),x),And(FreeQ(List(a,b,c,d,e,m),x),IGtQ(n,C1))));
IIntegrate(3468,Int(Times(Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Plus(d,Times(e,x)),m),Power(Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),n),x),x),And(FreeQ(List(a,b,c,d,e,m),x),IGtQ(n,C1))));
IIntegrate(3469,Int(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT),Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(d,Times(e,x)),m),Power(Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),n)),x),FreeQ(List(a,b,c,d,e,m,n),x)));
IIntegrate(3470,Int(Times(Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(d,Times(e,x)),m),Power(Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),n)),x),FreeQ(List(a,b,c,d,e,m,n),x)));
IIntegrate(3471,Int(Times(Power(u_,m_DEFAULT),Power(Sin(v_),n_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(ExpandToSum(u,x),m),Power(Sin(ExpandToSum(v,x)),n)),x),And(FreeQ(m,x),IGtQ(n,C0),LinearQ(u,x),QuadraticQ(v,x),Not(And(LinearMatchQ(u,x),QuadraticMatchQ(v,x))))));
IIntegrate(3472,Int(Times(Power(Cos(v_),n_DEFAULT),Power(u_,m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(ExpandToSum(u,x),m),Power(Cos(ExpandToSum(v,x)),n)),x),And(FreeQ(m,x),IGtQ(n,C0),LinearQ(u,x),QuadraticQ(v,x),Not(And(LinearMatchQ(u,x),QuadraticMatchQ(v,x))))));
IIntegrate(3473,Int(Power(Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Subtract(Simp(Times(b,Power(Times(b,Tan(Plus(c,Times(d,x)))),Subtract(n,C1)),Power(Times(d,Subtract(n,C1)),CN1)),x),Dist(Sqr(b),Int(Power(Times(b,Tan(Plus(c,Times(d,x)))),Subtract(n,C2)),x),x)),And(FreeQ(List(b,c,d),x),GtQ(n,C1))));
IIntegrate(3474,Int(Power(Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(b,Tan(Plus(c,Times(d,x)))),Plus(n,C1)),Power(Times(b,d,Plus(n,C1)),CN1)),x),Dist(Power(b,CN2),Int(Power(Times(b,Tan(Plus(c,Times(d,x)))),Plus(n,C2)),x),x)),And(FreeQ(List(b,c,d),x),LtQ(n,CN1))));
IIntegrate(3475,Int($($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),x_Symbol),
    Condition(Negate(Simp(Times(Log(RemoveContent(Cos(Plus(c,Times(d,x))),x)),Power(d,CN1)),x)),FreeQ(List(c,d),x)));
IIntegrate(3476,Int(Power(Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Dist(Times(b,Power(d,CN1)),Subst(Int(Times(Power(x,n),Power(Plus(Sqr(b),Sqr(x)),CN1)),x),x,Times(b,Tan(Plus(c,Times(d,x))))),x),And(FreeQ(List(b,c,d,n),x),Not(IntegerQ(n)))));
IIntegrate(3477,Int(Sqr(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Plus(Simp(Times(Subtract(Sqr(a),Sqr(b)),x),x),Dist(Times(C2,a,b),Int(Tan(Plus(c,Times(d,x))),x),x),Simp(Times(Sqr(b),Tan(Plus(c,Times(d,x))),Power(d,CN1)),x)),FreeQ(List(a,b,c,d),x)));
IIntegrate(3478,Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Plus(Simp(Times(b,Power(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))),Subtract(n,C1)),Power(Times(d,Subtract(n,C1)),CN1)),x),Dist(Times(C2,a),Int(Power(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))),Subtract(n,C1)),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Sqr(a),Sqr(b)),C0),GtQ(n,C1))));
IIntegrate(3479,Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Plus(Simp(Times(a,Power(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))),n),Power(Times(C2,b,d,n),CN1)),x),Dist(Power(Times(C2,a),CN1),Int(Power(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))),Plus(n,C1)),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Sqr(a),Sqr(b)),C0),LtQ(n,C0))));
IIntegrate(3480,Int(Sqrt(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Dist(Times(CN2,b,Power(d,CN1)),Subst(Int(Power(Subtract(Times(C2,a),Sqr(x)),CN1),x),x,Sqrt(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))))),x),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Sqr(a),Sqr(b)),C0))));
IIntegrate(3481,Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Negate(Dist(Times(b,Power(d,CN1)),Subst(Int(Times(Power(Plus(a,x),Subtract(n,C1)),Power(Subtract(a,x),CN1)),x),x,Times(b,Tan(Plus(c,Times(d,x))))),x)),And(FreeQ(List(a,b,c,d,n),x),EqQ(Plus(Sqr(a),Sqr(b)),C0))));
IIntegrate(3482,Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Plus(Simp(Times(b,Power(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))),Subtract(n,C1)),Power(Times(d,Subtract(n,C1)),CN1)),x),Int(Times(Plus(Sqr(a),Negate(Sqr(b)),Times(C2,a,b,Tan(Plus(c,Times(d,x))))),Power(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))),Subtract(n,C2))),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Plus(Sqr(a),Sqr(b)),C0),GtQ(n,C1))));
IIntegrate(3483,Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Plus(Simp(Times(b,Power(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))),Plus(n,C1)),Power(Times(d,Plus(n,C1),Plus(Sqr(a),Sqr(b))),CN1)),x),Dist(Power(Plus(Sqr(a),Sqr(b)),CN1),Int(Times(Subtract(a,Times(b,Tan(Plus(c,Times(d,x))))),Power(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))),Plus(n,C1))),x),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Plus(Sqr(a),Sqr(b)),C0),LtQ(n,CN1))));
IIntegrate(3484,Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1),x_Symbol),
    Condition(Plus(Simp(Times(a,x,Power(Plus(Sqr(a),Sqr(b)),CN1)),x),Dist(Times(b,Power(Plus(Sqr(a),Sqr(b)),CN1)),Int(Times(Subtract(b,Times(a,Tan(Plus(c,Times(d,x))))),Power(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))),CN1)),x),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Plus(Sqr(a),Sqr(b)),C0))));
IIntegrate(3485,Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Dist(Times(b,Power(d,CN1)),Subst(Int(Times(Power(Plus(a,x),n),Power(Plus(Sqr(b),Sqr(x)),CN1)),x),x,Times(b,Tan(Plus(c,Times(d,x))))),x),And(FreeQ(List(a,b,c,d,n),x),NeQ(Plus(Sqr(a),Sqr(b)),C0))));
IIntegrate(3486,Int(Times(Power(Times(d_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_DEFAULT),Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))))),x_Symbol),
    Condition(Plus(Simp(Times(b,Power(Times(d,Sec(Plus(e,Times(f,x)))),m),Power(Times(f,m),CN1)),x),Dist(a,Int(Power(Times(d,Sec(Plus(e,Times(f,x)))),m),x),x)),And(FreeQ(List(a,b,d,e,f,m),x),Or(IntegerQ(Times(C2,m)),NeQ(Plus(Sqr(a),Sqr(b)),C0)))));
IIntegrate(3487,Int(Times(Power($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),m_),Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),n_)),x_Symbol),
    Condition(Dist(Power(Times(Power(a,Subtract(m,C2)),b,f),CN1),Subst(Int(Times(Power(Subtract(a,x),Subtract(Times(C1D2,m),C1)),Power(Plus(a,x),Subtract(Plus(n,Times(C1D2,m)),C1))),x),x,Times(b,Tan(Plus(e,Times(f,x))))),x),And(FreeQ(List(a,b,e,f,n),x),EqQ(Plus(Sqr(a),Sqr(b)),C0),IntegerQ(Times(C1D2,m)))));
IIntegrate(3488,Int(Times(Power(Times(d_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),n_)),x_Symbol),
    Condition(Simp(Times(b,Power(Times(d,Sec(Plus(e,Times(f,x)))),m),Power(Plus(a,Times(b,Tan(Plus(e,Times(f,x))))),n),Power(Times(a,f,m),CN1)),x),And(FreeQ(List(a,b,d,e,f,m,n),x),EqQ(Plus(Sqr(a),Sqr(b)),C0),EqQ(Simplify(Plus(m,n)),C0))));
IIntegrate(3489,Int(Times($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),CN1D2)),x_Symbol),
    Condition(Dist(Times(CN2,a,Power(Times(b,f),CN1)),Subst(Int(Power(Subtract(C2,Times(a,Sqr(x))),CN1),x),x,Times(Sec(Plus(e,Times(f,x))),Power(Plus(a,Times(b,Tan(Plus(e,Times(f,x))))),CN1D2))),x),And(FreeQ(List(a,b,e,f),x),EqQ(Plus(Sqr(a),Sqr(b)),C0))));
IIntegrate(3490,Int(Times(Power(Times(d_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),n_)),x_Symbol),
    Condition(Plus(Simp(Times(b,Power(Times(d,Sec(Plus(e,Times(f,x)))),m),Power(Plus(a,Times(b,Tan(Plus(e,Times(f,x))))),n),Power(Times(a,f,m),CN1)),x),Dist(Times(a,Power(Times(C2,Sqr(d)),CN1)),Int(Times(Power(Times(d,Sec(Plus(e,Times(f,x)))),Plus(m,C2)),Power(Plus(a,Times(b,Tan(Plus(e,Times(f,x))))),Subtract(n,C1))),x),x)),And(FreeQ(List(a,b,d,e,f),x),EqQ(Plus(Sqr(a),Sqr(b)),C0),EqQ(Plus(Times(C1D2,m),n),C0),GtQ(n,C0))));
IIntegrate(3491,Int(Times(Power(Times(d_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),n_)),x_Symbol),
    Condition(Plus(Simp(Times(C2,Sqr(d),Power(Times(d,Sec(Plus(e,Times(f,x)))),Subtract(m,C2)),Power(Plus(a,Times(b,Tan(Plus(e,Times(f,x))))),Plus(n,C1)),Power(Times(b,f,Subtract(m,C2)),CN1)),x),Dist(Times(C2,Sqr(d),Power(a,CN1)),Int(Times(Power(Times(d,Sec(Plus(e,Times(f,x)))),Subtract(m,C2)),Power(Plus(a,Times(b,Tan(Plus(e,Times(f,x))))),Plus(n,C1))),x),x)),And(FreeQ(List(a,b,d,e,f),x),EqQ(Plus(Sqr(a),Sqr(b)),C0),EqQ(Plus(Times(C1D2,m),n),C0),LtQ(n,CN1))));
IIntegrate(3492,Int(Times(Power(Times(d_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_),Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),n_)),x_Symbol),
    Condition(Dist(Times(Power(Times(a,Power(d,CN1)),Times(C2,IntPart(n))),Power(Plus(a,Times(b,Tan(Plus(e,Times(f,x))))),FracPart(n)),Power(Subtract(a,Times(b,Tan(Plus(e,Times(f,x))))),FracPart(n)),Power(Power(Times(d,Sec(Plus(e,Times(f,x)))),Times(C2,FracPart(n))),CN1)),Int(Power(Power(Subtract(a,Times(b,Tan(Plus(e,Times(f,x))))),n),CN1),x),x),And(FreeQ(List(a,b,d,e,f,m,n),x),EqQ(Plus(Sqr(a),Sqr(b)),C0),EqQ(Simplify(Plus(Times(C1D2,m),n)),C0))));
IIntegrate(3493,Int(Times(Power(Times(d_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),n_)),x_Symbol),
    Condition(Simp(Times(C2,b,Power(Times(d,Sec(Plus(e,Times(f,x)))),m),Power(Plus(a,Times(b,Tan(Plus(e,Times(f,x))))),Subtract(n,C1)),Power(Times(f,m),CN1)),x),And(FreeQ(List(a,b,d,e,f,m,n),x),EqQ(Plus(Sqr(a),Sqr(b)),C0),EqQ(Simplify(Subtract(Plus(Times(C1D2,m),n),C1)),C0))));
IIntegrate(3494,Int(Times(Power(Times(d_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),n_)),x_Symbol),
    Condition(Plus(Simp(Times(b,Power(Times(d,Sec(Plus(e,Times(f,x)))),m),Power(Plus(a,Times(b,Tan(Plus(e,Times(f,x))))),Subtract(n,C1)),Power(Times(f,Subtract(Plus(m,n),C1)),CN1)),x),Dist(Times(a,Subtract(Plus(m,Times(C2,n)),C2),Power(Subtract(Plus(m,n),C1),CN1)),Int(Times(Power(Times(d,Sec(Plus(e,Times(f,x)))),m),Power(Plus(a,Times(b,Tan(Plus(e,Times(f,x))))),Subtract(n,C1))),x),x)),And(FreeQ(List(a,b,d,e,f,m,n),x),EqQ(Plus(Sqr(a),Sqr(b)),C0),IGtQ(Simplify(Subtract(Plus(Times(C1D2,m),n),C1)),C0),Not(IntegerQ(n)))));
IIntegrate(3495,Int(Times(Sqrt(Times(d_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),Sqrt(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))))),x_Symbol),
    Condition(Dist(Times(CN4,b,Sqr(d),Power(f,CN1)),Subst(Int(Times(Sqr(x),Power(Plus(Sqr(a),Times(Sqr(d),Power(x,C4))),CN1)),x),x,Times(Sqrt(Plus(a,Times(b,Tan(Plus(e,Times(f,x)))))),Power(Times(d,Sec(Plus(e,Times(f,x)))),CN1D2))),x),And(FreeQ(List(a,b,d,e,f),x),EqQ(Plus(Sqr(a),Sqr(b)),C0))));
IIntegrate(3496,Int(Times(Power(Times(d_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_),Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),n_)),x_Symbol),
    Condition(Subtract(Simp(Times(C2,b,Power(Times(d,Sec(Plus(e,Times(f,x)))),m),Power(Plus(a,Times(b,Tan(Plus(e,Times(f,x))))),Subtract(n,C1)),Power(Times(f,m),CN1)),x),Dist(Times(Sqr(b),Subtract(Plus(m,Times(C2,n)),C2),Power(Times(Sqr(d),m),CN1)),Int(Times(Power(Times(d,Sec(Plus(e,Times(f,x)))),Plus(m,C2)),Power(Plus(a,Times(b,Tan(Plus(e,Times(f,x))))),Subtract(n,C2))),x),x)),And(FreeQ(List(a,b,d,e,f),x),EqQ(Plus(Sqr(a),Sqr(b)),C0),GtQ(n,C1),Or(And(IGtQ(Times(C1D2,n),C0),ILtQ(Subtract(m,C1D2),C0)),And(EqQ(n,C2),LtQ(m,C0)),And(LeQ(m,CN1),GtQ(Plus(m,n),C0)),And(ILtQ(m,C0),LtQ(Subtract(Plus(Times(C1D2,m),n),C1),C0),IntegerQ(n)),And(EqQ(n,QQ(3L,2L)),EqQ(m,Negate(C1D2)))),IntegerQ(Times(C2,m)))));
IIntegrate(3497,Int(Times(Power(Times(d_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_),Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),n_)),x_Symbol),
    Condition(Plus(Simp(Times(b,Power(Times(d,Sec(Plus(e,Times(f,x)))),m),Power(Plus(a,Times(b,Tan(Plus(e,Times(f,x))))),n),Power(Times(a,f,m),CN1)),x),Dist(Times(a,Plus(m,n),Power(Times(m,Sqr(d)),CN1)),Int(Times(Power(Times(d,Sec(Plus(e,Times(f,x)))),Plus(m,C2)),Power(Plus(a,Times(b,Tan(Plus(e,Times(f,x))))),Subtract(n,C1))),x),x)),And(FreeQ(List(a,b,d,e,f),x),EqQ(Plus(Sqr(a),Sqr(b)),C0),GtQ(n,C0),LtQ(m,CN1),IntegersQ(Times(C2,m),Times(C2,n)))));
IIntegrate(3498,Int(Times(Power(Times(d_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),n_)),x_Symbol),
    Condition(Plus(Simp(Times(b,Power(Times(d,Sec(Plus(e,Times(f,x)))),m),Power(Plus(a,Times(b,Tan(Plus(e,Times(f,x))))),Subtract(n,C1)),Power(Times(f,Subtract(Plus(m,n),C1)),CN1)),x),Dist(Times(a,Subtract(Plus(m,Times(C2,n)),C2),Power(Subtract(Plus(m,n),C1),CN1)),Int(Times(Power(Times(d,Sec(Plus(e,Times(f,x)))),m),Power(Plus(a,Times(b,Tan(Plus(e,Times(f,x))))),Subtract(n,C1))),x),x)),And(FreeQ(List(a,b,d,e,f,m),x),EqQ(Plus(Sqr(a),Sqr(b)),C0),GtQ(n,C0),NeQ(Subtract(Plus(m,n),C1),C0),IntegersQ(Times(C2,m),Times(C2,n)))));
IIntegrate(3499,Int(Times(Power(Times(d_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),QQ(3L,2L)),Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),CN1D2)),x_Symbol),
    Condition(Dist(Times(d,Sec(Plus(e,Times(f,x))),Power(Times(Sqrt(Subtract(a,Times(b,Tan(Plus(e,Times(f,x)))))),Sqrt(Plus(a,Times(b,Tan(Plus(e,Times(f,x))))))),CN1)),Int(Times(Sqrt(Times(d,Sec(Plus(e,Times(f,x))))),Sqrt(Subtract(a,Times(b,Tan(Plus(e,Times(f,x))))))),x),x),And(FreeQ(List(a,b,d,e,f),x),EqQ(Plus(Sqr(a),Sqr(b)),C0))));
IIntegrate(3500,Int(Times(Power(Times(d_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_),Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),n_)),x_Symbol),
    Condition(Subtract(Simp(Times(C2,Sqr(d),Power(Times(d,Sec(Plus(e,Times(f,x)))),Subtract(m,C2)),Power(Plus(a,Times(b,Tan(Plus(e,Times(f,x))))),Plus(n,C1)),Power(Times(b,f,Plus(m,Times(C2,n))),CN1)),x),Dist(Times(Sqr(d),Subtract(m,C2),Power(Times(Sqr(b),Plus(m,Times(C2,n))),CN1)),Int(Times(Power(Times(d,Sec(Plus(e,Times(f,x)))),Subtract(m,C2)),Power(Plus(a,Times(b,Tan(Plus(e,Times(f,x))))),Plus(n,C2))),x),x)),And(FreeQ(List(a,b,d,e,f,m),x),EqQ(Plus(Sqr(a),Sqr(b)),C0),LtQ(n,CN1),Or(And(ILtQ(Times(C1D2,n),C0),IGtQ(Subtract(m,C1D2),C0)),EqQ(n,CN2),IGtQ(Plus(m,n),C0),And(IntegersQ(n,Plus(m,C1D2)),GtQ(Plus(Times(C2,m),n,C1),C0))),IntegerQ(Times(C2,m)))));
  }
}
}
