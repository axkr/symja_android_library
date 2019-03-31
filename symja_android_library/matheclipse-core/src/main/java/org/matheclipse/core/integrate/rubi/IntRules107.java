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
public class IntRules107 { 

	public static void initialize() {
		Initializer.init();
	}

	private static class Initializer  {

		private static void init() {
IIntegrate(5351,Int(Times(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(Times(e_DEFAULT,x_),m_)),x_Symbol),
    Condition(Negate(Dist(Times(Power(Times(e,x),m),Power(Power(x,CN1),m)),Subst(Int(Times(Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(Power(x,n),CN1)))))),p),Power(Power(x,Plus(m,C2)),CN1)),x),x,Power(x,CN1)),x)),And(FreeQ(List(a,b,c,d,e,m),x),IntegerQ(p),ILtQ(n,C0),Not(RationalQ(m)))));
IIntegrate(5352,Int(Times(Power(x_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(k,Denominator(n))),Dist(k,Subst(Int(Times(Power(x,Subtract(Times(k,Plus(m,C1)),C1)),Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(x,Times(k,n))))))),p)),x),x,Power(x,Power(k,CN1))),x)),And(FreeQ(List(a,b,c,d,m),x),IntegerQ(p),FractionQ(n))));
IIntegrate(5353,Int(Times(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(k,Denominator(n))),Dist(k,Subst(Int(Times(Power(x,Subtract(Times(k,Plus(m,C1)),C1)),Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(x,Times(k,n))))))),p)),x),x,Power(x,Power(k,CN1))),x)),And(FreeQ(List(a,b,c,d,m),x),IntegerQ(p),FractionQ(n))));
IIntegrate(5354,Int(Times(Power(Times(e_,x_),m_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Int(Times(Power(x,m),Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(x,n)))))),p)),x),x),And(FreeQ(List(a,b,c,d,e,m),x),IntegerQ(p),FractionQ(n))));
IIntegrate(5355,Int(Times(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(Times(e_,x_),m_)),x_Symbol),
    Condition(Dist(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Int(Times(Power(x,m),Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(x,n)))))),p)),x),x),And(FreeQ(List(a,b,c,d,e,m),x),IntegerQ(p),FractionQ(n))));
IIntegrate(5356,Int(Times(Power(x_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Plus(m,C1),CN1),Subst(Int(Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(x,Simplify(Times(n,Power(Plus(m,C1),CN1))))))))),p),x),x,Power(x,Plus(m,C1))),x),And(FreeQ(List(a,b,c,d,m,n),x),IntegerQ(p),NeQ(m,CN1),IGtQ(Simplify(Times(n,Power(Plus(m,C1),CN1))),C0),Not(IntegerQ(n)))));
IIntegrate(5357,Int(Times(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Plus(m,C1),CN1),Subst(Int(Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(x,Simplify(Times(n,Power(Plus(m,C1),CN1))))))))),p),x),x,Power(x,Plus(m,C1))),x),And(FreeQ(List(a,b,c,d,m,n),x),IntegerQ(p),NeQ(m,CN1),IGtQ(Simplify(Times(n,Power(Plus(m,C1),CN1))),C0),Not(IntegerQ(n)))));
IIntegrate(5358,Int(Times(Power(Times(e_,x_),m_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Int(Times(Power(x,m),Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(x,n)))))),p)),x),x),And(FreeQ(List(a,b,c,d,e,m,n),x),IntegerQ(p),NeQ(m,CN1),IGtQ(Simplify(Times(n,Power(Plus(m,C1),CN1))),C0),Not(IntegerQ(n)))));
IIntegrate(5359,Int(Times(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(Times(e_,x_),m_)),x_Symbol),
    Condition(Dist(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Int(Times(Power(x,m),Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(x,n)))))),p)),x),x),And(FreeQ(List(a,b,c,d,e,m,n),x),IntegerQ(p),NeQ(m,CN1),IGtQ(Simplify(Times(n,Power(Plus(m,C1),CN1))),C0),Not(IntegerQ(n)))));
IIntegrate(5360,Int(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_))))),x_Symbol),
    Condition(Subtract(Dist(C1D2,Int(Times(Power(Times(e,x),m),Exp(Plus(c,Times(d,Power(x,n))))),x),x),Dist(C1D2,Int(Times(Power(Times(e,x),m),Exp(Subtract(Negate(c),Times(d,Power(x,n))))),x),x)),FreeQ(List(c,d,e,m,n),x)));
IIntegrate(5361,Int(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Plus(Dist(C1D2,Int(Times(Power(Times(e,x),m),Exp(Plus(c,Times(d,Power(x,n))))),x),x),Dist(C1D2,Int(Times(Power(Times(e,x),m),Exp(Subtract(Negate(c),Times(d,Power(x,n))))),x),x)),FreeQ(List(c,d,e,m,n),x)));
IIntegrate(5362,Int(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_)),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Times(e,x),m),Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(x,n)))))),p),x),x),And(FreeQ(List(a,b,c,d,e,m,n),x),IGtQ(p,C0))));
IIntegrate(5363,Int(Times(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Times(e,x),m),Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(x,n)))))),p),x),x),And(FreeQ(List(a,b,c,d,e,m,n),x),IGtQ(p,C0))));
IIntegrate(5364,Int(Times(Power(x_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(u_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Power(Coefficient(u,x,C1),Plus(m,C1)),CN1),Subst(Int(Times(Power(Subtract(x,Coefficient(u,x,C0)),m),Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(x,n)))))),p)),x),x,u),x),And(FreeQ(List(a,b,c,d,n,p),x),LinearQ(u,x),NeQ(u,x),IntegerQ(m))));
IIntegrate(5365,Int(Times(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(u_,n_)))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Power(Coefficient(u,x,C1),Plus(m,C1)),CN1),Subst(Int(Times(Power(Subtract(x,Coefficient(u,x,C0)),m),Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(x,n)))))),p)),x),x,u),x),And(FreeQ(List(a,b,c,d,n,p),x),LinearQ(u,x),NeQ(u,x),IntegerQ(m))));
IIntegrate(5366,Int(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(u_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(u,n)))))),p)),x),And(FreeQ(List(a,b,c,d,e,m,n,p),x),LinearQ(u,x))));
IIntegrate(5367,Int(Times(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(u_,n_)))),b_DEFAULT)),p_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(u,n)))))),p)),x),And(FreeQ(List(a,b,c,d,e,m,n,p),x),LinearQ(u,x))));
IIntegrate(5368,Int(Times(Power(Times(e_,x_),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(u_))),p_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Sinh(ExpandToSum(u,x)))),p)),x),And(FreeQ(List(a,b,e,m,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x)))));
IIntegrate(5369,Int(Times(Power(Plus(a_DEFAULT,Times(Cosh(u_),b_DEFAULT)),p_DEFAULT),Power(Times(e_,x_),m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Cosh(ExpandToSum(u,x)))),p)),x),And(FreeQ(List(a,b,e,m,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x)))));
IIntegrate(5370,Int(Times(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),Power(x_,m_DEFAULT),Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Times(Power(Sinh(Plus(a,Times(b,Power(x,n)))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),CN1)),x),And(FreeQ(List(a,b,m,n,p),x),EqQ(m,Subtract(n,C1)),NeQ(p,CN1))));
IIntegrate(5371,Int(Times(Power(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_DEFAULT),Power(x_,m_DEFAULT),Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))))),x_Symbol),
    Condition(Simp(Times(Power(Cosh(Plus(a,Times(b,Power(x,n)))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),CN1)),x),And(FreeQ(List(a,b,m,n,p),x),EqQ(m,Subtract(n,C1)),NeQ(p,CN1))));
IIntegrate(5372,Int(Times(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),Power(x_,m_DEFAULT),Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,Negate(n),C1)),Power(Sinh(Plus(a,Times(b,Power(x,n)))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),CN1)),x),Dist(Times(Plus(m,Negate(n),C1),Power(Times(b,n,Plus(p,C1)),CN1)),Int(Times(Power(x,Subtract(m,n)),Power(Sinh(Plus(a,Times(b,Power(x,n)))),Plus(p,C1))),x),x)),And(FreeQ(List(a,b,p),x),LtQ(C0,n,Plus(m,C1)),NeQ(p,CN1))));
IIntegrate(5373,Int(Times(Power(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT),Power(x_,m_DEFAULT),Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,Negate(n),C1)),Power(Cosh(Plus(a,Times(b,Power(x,n)))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),CN1)),x),Dist(Times(Plus(m,Negate(n),C1),Power(Times(b,n,Plus(p,C1)),CN1)),Int(Times(Power(x,Subtract(m,n)),Power(Cosh(Plus(a,Times(b,Power(x,n)))),Plus(p,C1))),x),x)),And(FreeQ(List(a,b,p),x),LtQ(C0,n,Plus(m,C1)),NeQ(p,CN1))));
IIntegrate(5374,Int(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Subtract(Dist(C1D2,Int(Exp(Plus(a,Times(b,x),Times(c,Sqr(x)))),x),x),Dist(C1D2,Int(Exp(Subtract(Subtract(Negate(a),Times(b,x)),Times(c,Sqr(x)))),x),x)),FreeQ(List(a,b,c),x)));
IIntegrate(5375,Int(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Plus(Dist(C1D2,Int(Exp(Plus(a,Times(b,x),Times(c,Sqr(x)))),x),x),Dist(C1D2,Int(Exp(Subtract(Subtract(Negate(a),Times(b,x)),Times(c,Sqr(x)))),x),x)),FreeQ(List(a,b,c),x)));
IIntegrate(5376,Int(Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Sinh(Plus(a,Times(b,x),Times(c,Sqr(x)))),n),x),x),And(FreeQ(List(a,b,c),x),IGtQ(n,C1))));
IIntegrate(5377,Int(Power(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Cosh(Plus(a,Times(b,x),Times(c,Sqr(x)))),n),x),x),And(FreeQ(List(a,b,c),x),IGtQ(n,C1))));
IIntegrate(5378,Int(Power(Sinh(v_),n_DEFAULT),x_Symbol),
    Condition(Int(Power(Sinh(ExpandToSum(v,x)),n),x),And(IGtQ(n,C0),QuadraticQ(v,x),Not(QuadraticMatchQ(v,x)))));
IIntegrate(5379,Int(Power(Cosh(v_),n_DEFAULT),x_Symbol),
    Condition(Int(Power(Cosh(ExpandToSum(v,x)),n),x),And(IGtQ(n,C0),QuadraticQ(v,x),Not(QuadraticMatchQ(v,x)))));
IIntegrate(5380,Int(Times(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Simp(Times(e,Cosh(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(Subtract(Times(b,e),Times(C2,c,d)),C0))));
IIntegrate(5381,Int(Times(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Plus(d_DEFAULT,Times(e_DEFAULT,x_))),x_Symbol),
    Condition(Simp(Times(e,Sinh(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(Subtract(Times(b,e),Times(C2,c,d)),C0))));
IIntegrate(5382,Int(Times(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Subtract(Simp(Times(e,Cosh(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),x),Dist(Times(Subtract(Times(b,e),Times(C2,c,d)),Power(Times(C2,c),CN1)),Int(Sinh(Plus(a,Times(b,x),Times(c,Sqr(x)))),x),x)),And(FreeQ(List(a,b,c,d,e),x),NeQ(Subtract(Times(b,e),Times(C2,c,d)),C0))));
IIntegrate(5383,Int(Times(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Plus(d_DEFAULT,Times(e_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Simp(Times(e,Sinh(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),x),Dist(Times(Subtract(Times(b,e),Times(C2,c,d)),Power(Times(C2,c),CN1)),Int(Cosh(Plus(a,Times(b,x),Times(c,Sqr(x)))),x),x)),And(FreeQ(List(a,b,c,d,e),x),NeQ(Subtract(Times(b,e),Times(C2,c,d)),C0))));
IIntegrate(5384,Int(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_),Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Subtract(Simp(Times(e,Power(Plus(d,Times(e,x)),Subtract(m,C1)),Cosh(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),x),Dist(Times(Sqr(e),Subtract(m,C1),Power(Times(C2,c),CN1)),Int(Times(Power(Plus(d,Times(e,x)),Subtract(m,C2)),Cosh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x),x)),And(FreeQ(List(a,b,c,d,e),x),GtQ(m,C1),EqQ(Subtract(Times(b,e),Times(C2,c,d)),C0))));
IIntegrate(5385,Int(Times(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Subtract(Simp(Times(e,Power(Plus(d,Times(e,x)),Subtract(m,C1)),Sinh(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),x),Dist(Times(Sqr(e),Subtract(m,C1),Power(Times(C2,c),CN1)),Int(Times(Power(Plus(d,Times(e,x)),Subtract(m,C2)),Sinh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x),x)),And(FreeQ(List(a,b,c,d,e),x),GtQ(m,C1),EqQ(Subtract(Times(b,e),Times(C2,c,d)),C0))));
IIntegrate(5386,Int(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_),Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Simp(Times(e,Power(Plus(d,Times(e,x)),Subtract(m,C1)),Cosh(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),x),Negate(Dist(Times(Sqr(e),Subtract(m,C1),Power(Times(C2,c),CN1)),Int(Times(Power(Plus(d,Times(e,x)),Subtract(m,C2)),Cosh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x),x)),Negate(Dist(Times(Subtract(Times(b,e),Times(C2,c,d)),Power(Times(C2,c),CN1)),Int(Times(Power(Plus(d,Times(e,x)),Subtract(m,C1)),Sinh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x),x))),And(FreeQ(List(a,b,c,d,e),x),GtQ(m,C1),NeQ(Subtract(Times(b,e),Times(C2,c,d)),C0))));
IIntegrate(5387,Int(Times(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Plus(Simp(Times(e,Power(Plus(d,Times(e,x)),Subtract(m,C1)),Sinh(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),x),Negate(Dist(Times(Sqr(e),Subtract(m,C1),Power(Times(C2,c),CN1)),Int(Times(Power(Plus(d,Times(e,x)),Subtract(m,C2)),Sinh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x),x)),Negate(Dist(Times(Subtract(Times(b,e),Times(C2,c,d)),Power(Times(C2,c),CN1)),Int(Times(Power(Plus(d,Times(e,x)),Subtract(m,C1)),Cosh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x),x))),And(FreeQ(List(a,b,c,d,e),x),GtQ(m,C1),NeQ(Subtract(Times(b,e),Times(C2,c,d)),C0))));
IIntegrate(5388,Int(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_),Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Sinh(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(e,Plus(m,C1)),CN1)),x),Dist(Times(C2,c,Power(Times(Sqr(e),Plus(m,C1)),CN1)),Int(Times(Power(Plus(d,Times(e,x)),Plus(m,C2)),Cosh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x),x)),And(FreeQ(List(a,b,c,d,e),x),LtQ(m,CN1),EqQ(Subtract(Times(b,e),Times(C2,c,d)),C0))));
IIntegrate(5389,Int(Times(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Cosh(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(e,Plus(m,C1)),CN1)),x),Dist(Times(C2,c,Power(Times(Sqr(e),Plus(m,C1)),CN1)),Int(Times(Power(Plus(d,Times(e,x)),Plus(m,C2)),Sinh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x),x)),And(FreeQ(List(a,b,c,d,e),x),LtQ(m,CN1),EqQ(Subtract(Times(b,e),Times(C2,c,d)),C0))));
IIntegrate(5390,Int(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_),Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Sinh(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(e,Plus(m,C1)),CN1)),x),Negate(Dist(Times(C2,c,Power(Times(Sqr(e),Plus(m,C1)),CN1)),Int(Times(Power(Plus(d,Times(e,x)),Plus(m,C2)),Cosh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x),x)),Negate(Dist(Times(Subtract(Times(b,e),Times(C2,c,d)),Power(Times(Sqr(e),Plus(m,C1)),CN1)),Int(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Cosh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x),x))),And(FreeQ(List(a,b,c,d,e),x),LtQ(m,CN1),NeQ(Subtract(Times(b,e),Times(C2,c,d)),C0))));
IIntegrate(5391,Int(Times(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Cosh(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(e,Plus(m,C1)),CN1)),x),Negate(Dist(Times(C2,c,Power(Times(Sqr(e),Plus(m,C1)),CN1)),Int(Times(Power(Plus(d,Times(e,x)),Plus(m,C2)),Sinh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x),x)),Negate(Dist(Times(Subtract(Times(b,e),Times(C2,c,d)),Power(Times(Sqr(e),Plus(m,C1)),CN1)),Int(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Sinh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x),x))),And(FreeQ(List(a,b,c,d,e),x),LtQ(m,CN1),NeQ(Subtract(Times(b,e),Times(C2,c,d)),C0))));
IIntegrate(5392,Int(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT),Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(d,Times(e,x)),m),Sinh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x),FreeQ(List(a,b,c,d,e,m),x)));
IIntegrate(5393,Int(Times(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(d,Times(e,x)),m),Cosh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x),FreeQ(List(a,b,c,d,e,m),x)));
IIntegrate(5394,Int(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT),Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_)),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Plus(d,Times(e,x)),m),Power(Sinh(Plus(a,Times(b,x),Times(c,Sqr(x)))),n),x),x),And(FreeQ(List(a,b,c,d,e,m),x),IGtQ(n,C1))));
IIntegrate(5395,Int(Times(Power(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Plus(d,Times(e,x)),m),Power(Cosh(Plus(a,Times(b,x),Times(c,Sqr(x)))),n),x),x),And(FreeQ(List(a,b,c,d,e,m),x),IGtQ(n,C1))));
IIntegrate(5396,Int(Times(Power(u_,m_DEFAULT),Power(Sinh(v_),n_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(ExpandToSum(u,x),m),Power(Sinh(ExpandToSum(v,x)),n)),x),And(FreeQ(m,x),IGtQ(n,C0),LinearQ(u,x),QuadraticQ(v,x),Not(And(LinearMatchQ(u,x),QuadraticMatchQ(v,x))))));
IIntegrate(5397,Int(Times(Power(Cosh(v_),n_DEFAULT),Power(u_,m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(ExpandToSum(u,x),m),Power(Cosh(ExpandToSum(v,x)),n)),x),And(FreeQ(m,x),IGtQ(n,C0),LinearQ(u,x),QuadraticQ(v,x),Not(And(LinearMatchQ(u,x),QuadraticMatchQ(v,x))))));
IIntegrate(5398,Int(Times(Power(u_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Tanh(v_))),n_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(ExpandToSum(u,x),m),Power(Plus(a,Times(b,Tanh(ExpandToSum(v,x)))),n)),x),And(FreeQ(List(a,b,m,n),x),LinearQ(List(u,v),x),Not(LinearMatchQ(List(u,v),x)))));
IIntegrate(5399,Int(Times(Power(Plus(a_DEFAULT,Times(Coth(v_),b_DEFAULT)),n_DEFAULT),Power(u_,m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(ExpandToSum(u,x),m),Power(Plus(a,Times(b,Coth(ExpandToSum(v,x)))),n)),x),And(FreeQ(List(a,b,m,n),x),LinearQ(List(u,v),x),Not(LinearMatchQ(List(u,v),x)))));
IIntegrate(5400,Int(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Tanh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT),x_Symbol),
    Condition(Dist(Power(n,CN1),Subst(Int(Times(Power(x,Subtract(Power(n,CN1),C1)),Power(Plus(a,Times(b,Tanh(Plus(c,Times(d,x))))),p)),x),x,Power(x,n)),x),And(FreeQ(List(a,b,c,d,p),x),IGtQ(Power(n,CN1),C0),IntegerQ(p))));
  }
}
}
