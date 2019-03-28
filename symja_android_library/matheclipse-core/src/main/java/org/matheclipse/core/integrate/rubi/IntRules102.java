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
public class IntRules102 { 

	public static void initialize() {
		Initializer.init();
	}

	private static class Initializer  {

		private static void init() {
IIntegrate(5101,Int(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Negate(Subst(Int(Times(Power(Plus(C1,Times(CN1,CI,x,Power(a,-1))),Times(C1D2,Plus(Times(CI,n),C1))),Power(Times(Power(x,Plus(m,C2)),Power(Plus(C1,Times(CI,x,Power(a,-1))),Times(C1D2,Plus(Times(CI,n),Negate(C1)))),Sqrt(Plus(C1,Times(Sqr(x),Power(a,-2))))),-1)),x),x,Power(x,-1))),And(FreeQ(a,x),IntegerQ(Times(C1D2,Plus(Times(CI,n),Negate(C1)))),IntegerQ(m))));
IIntegrate(5102,Int(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),x_Symbol),
    Condition(Negate(Subst(Int(Times(Power(Plus(C1,Times(CN1,CI,x,Power(a,-1))),Times(C1D2,CI,n)),Power(Times(Sqr(x),Power(Plus(C1,Times(CI,x,Power(a,-1))),Times(C1D2,CI,n))),-1)),x),x,Power(x,-1))),And(FreeQ(List(a,n),x),Not(IntegerQ(Times(CI,n))))));
IIntegrate(5103,Int(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Negate(Subst(Int(Times(Power(Plus(C1,Times(CN1,CI,x,Power(a,-1))),Times(C1D2,n)),Power(Times(Power(x,Plus(m,C2)),Power(Plus(C1,Times(CI,x,Power(a,-1))),Times(C1D2,n))),-1)),x),x,Power(x,-1))),And(FreeQ(List(a,n),x),Not(IntegerQ(Times(CI,n))),IntegerQ(m))));
IIntegrate(5104,Int(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_)),Power(x_,m_)),x_Symbol),
    Condition(Negate(Dist(Times(Power(x,m),Power(Power(x,-1),m)),Subst(Int(Times(Power(Plus(C1,Times(CN1,CI,x,Power(a,-1))),Times(C1D2,Plus(Times(CI,n),C1))),Power(Times(Power(x,Plus(m,C2)),Power(Plus(C1,Times(CI,x,Power(a,-1))),Times(C1D2,Plus(Times(CI,n),Negate(C1)))),Sqrt(Plus(C1,Times(Sqr(x),Power(a,-2))))),-1)),x),x,Power(x,-1)),x)),And(FreeQ(List(a,m),x),IntegerQ(Times(C1D2,Plus(Times(CI,n),Negate(C1)))),Not(IntegerQ(m)))));
IIntegrate(5105,Int(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(x_,m_)),x_Symbol),
    Condition(Negate(Subst(Int(Times(Power(Plus(C1,Times(CN1,CI,x,Power(a,-1))),Times(C1D2,n)),Power(Times(Power(x,Plus(m,C2)),Power(Plus(C1,Times(CI,x,Power(a,-1))),Times(C1D2,n))),-1)),x),x,Power(x,-1))),And(FreeQ(List(a,m,n),x),Not(IntegerQ(Times(C1D2,CI,n))),Not(IntegerQ(m)))));
IIntegrate(5106,Int(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,x_)),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(d,p),Int(Times(u,Power(x,p),Power(Plus(C1,Times(c,Power(Times(d,x),-1))),p),Exp(Times(n,ArcCot(Times(a,x))))),x),x),And(FreeQ(List(a,c,d,n),x),EqQ(Plus(Times(Sqr(a),Sqr(c)),Sqr(d)),C0),Not(IntegerQ(Times(C1D2,CI,n))),IntegerQ(p))));
IIntegrate(5107,Int(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,x_)),p_)),x_Symbol),
    Condition(Dist(Times(Power(Plus(c,Times(d,x)),p),Power(Times(Power(x,p),Power(Plus(C1,Times(c,Power(Times(d,x),-1))),p)),-1)),Int(Times(u,Power(x,p),Power(Plus(C1,Times(c,Power(Times(d,x),-1))),p),Exp(Times(n,ArcCot(Times(a,x))))),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Plus(Times(Sqr(a),Sqr(c)),Sqr(d)),C0),Not(IntegerQ(Times(C1D2,CI,n))),Not(IntegerQ(p)))));
IIntegrate(5108,Int(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Power(x_,-1))),p_DEFAULT)),x_Symbol),
    Condition(Negate(Dist(Power(c,p),Subst(Int(Times(Power(Plus(C1,Times(d,x,Power(c,-1))),p),Power(Plus(C1,Times(CN1,CI,x,Power(a,-1))),Times(C1D2,CI,n)),Power(Times(Sqr(x),Power(Plus(C1,Times(CI,x,Power(a,-1))),Times(C1D2,CI,n))),-1)),x),x,Power(x,-1)),x)),And(FreeQ(List(a,c,d,n,p),x),EqQ(Plus(Sqr(c),Times(Sqr(a),Sqr(d))),C0),Not(IntegerQ(Times(C1D2,CI,n))),Or(IntegerQ(p),GtQ(c,C0)))));
IIntegrate(5109,Int(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Power(x_,-1))),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Negate(Dist(Power(c,p),Subst(Int(Times(Power(Plus(C1,Times(d,x,Power(c,-1))),p),Power(Plus(C1,Times(CN1,CI,x,Power(a,-1))),Times(C1D2,CI,n)),Power(Times(Power(x,Plus(m,C2)),Power(Plus(C1,Times(CI,x,Power(a,-1))),Times(C1D2,CI,n))),-1)),x),x,Power(x,-1)),x)),And(FreeQ(List(a,c,d,m,n,p),x),EqQ(Plus(Sqr(c),Times(Sqr(a),Sqr(d))),C0),Not(IntegerQ(Times(C1D2,CI,n))),Or(IntegerQ(p),GtQ(c,C0)),IntegerQ(m))));
IIntegrate(5110,Int(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Power(x_,-1))),p_)),x_Symbol),
    Condition(Dist(Times(Power(Plus(c,Times(d,Power(x,-1))),p),Power(Power(Plus(C1,Times(d,Power(Times(c,x),-1))),p),-1)),Int(Times(Power(Plus(C1,Times(d,Power(Times(c,x),-1))),p),Exp(Times(n,ArcCot(Times(a,x))))),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Plus(Sqr(c),Times(Sqr(a),Sqr(d))),C0),Not(IntegerQ(Times(C1D2,CI,n))),Not(Or(IntegerQ(p),GtQ(c,C0))))));
IIntegrate(5111,Int(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Power(x_,-1))),p_DEFAULT),Power(x_,m_)),x_Symbol),
    Condition(Negate(Dist(Times(Power(c,p),Power(x,m),Power(Power(x,-1),m)),Subst(Int(Times(Power(Plus(C1,Times(d,x,Power(c,-1))),p),Power(Plus(C1,Times(CN1,CI,x,Power(a,-1))),Times(C1D2,CI,n)),Power(Times(Power(x,Plus(m,C2)),Power(Plus(C1,Times(CI,x,Power(a,-1))),Times(C1D2,CI,n))),-1)),x),x,Power(x,-1)),x)),And(FreeQ(List(a,c,d,m,n,p),x),EqQ(Plus(Sqr(c),Times(Sqr(a),Sqr(d))),C0),Not(IntegerQ(Times(C1D2,CI,n))),Or(IntegerQ(p),GtQ(c,C0)),Not(IntegerQ(m)))));
IIntegrate(5112,Int(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,-1))),p_)),x_Symbol),
    Condition(Dist(Times(Power(Plus(c,Times(d,Power(x,-1))),p),Power(Power(Plus(C1,Times(d,Power(Times(c,x),-1))),p),-1)),Int(Times(u,Power(Plus(C1,Times(d,Power(Times(c,x),-1))),p),Exp(Times(n,ArcCot(Times(a,x))))),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Plus(Sqr(c),Times(Sqr(a),Sqr(d))),C0),Not(IntegerQ(Times(C1D2,CI,n))),Not(Or(IntegerQ(p),GtQ(c,C0))))));
IIntegrate(5113,Int(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),-1)),x_Symbol),
    Condition(Negate(Simp(Times(Exp(Times(n,ArcCot(Times(a,x)))),Power(Times(a,c,n),-1)),x)),And(FreeQ(List(a,c,d,n),x),EqQ(d,Times(Sqr(a),c)))));
IIntegrate(5114,Int(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),QQ(-3L,2L))),x_Symbol),
    Condition(Negate(Simp(Times(Plus(n,Times(CN1,a,x)),Exp(Times(n,ArcCot(Times(a,x)))),Power(Times(a,c,Plus(Sqr(n),C1),Sqrt(Plus(c,Times(d,Sqr(x))))),-1)),x)),And(FreeQ(List(a,c,d,n),x),EqQ(d,Times(Sqr(a),c)),Not(IntegerQ(Times(C1D2,Plus(Times(CI,n),Negate(C1))))))));
IIntegrate(5115,Int(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(Plus(n,Times(C2,a,Plus(p,C1),x)),Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcCot(Times(a,x)))),Power(Times(a,c,Plus(Sqr(n),Times(C4,Sqr(Plus(p,C1))))),-1)),x)),Dist(Times(C2,Plus(p,C1),Plus(Times(C2,p),C3),Power(Times(c,Plus(Sqr(n),Times(C4,Sqr(Plus(p,C1))))),-1)),Int(Times(Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcCot(Times(a,x))))),x),x)),And(FreeQ(List(a,c,d,n),x),EqQ(d,Times(Sqr(a),c)),LtQ(p,CN1),NeQ(p,QQ(-3L,2L)),NeQ(Plus(Sqr(n),Times(C4,Sqr(Plus(p,C1)))),C0),Not(And(IntegerQ(p),IntegerQ(Times(C1D2,CI,n)))),Not(And(Not(IntegerQ(p)),IntegerQ(Times(C1D2,Plus(Times(CI,n),Negate(C1)))))))));
IIntegrate(5116,Int(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),x_,Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),QQ(-3L,2L))),x_Symbol),
    Condition(Negate(Simp(Times(Plus(C1,Times(a,n,x)),Exp(Times(n,ArcCot(Times(a,x)))),Power(Times(Sqr(a),c,Plus(Sqr(n),C1),Sqrt(Plus(c,Times(d,Sqr(x))))),-1)),x)),And(FreeQ(List(a,c,d,n),x),EqQ(d,Times(Sqr(a),c)),Not(IntegerQ(Times(C1D2,Plus(Times(CI,n),Negate(C1))))))));
IIntegrate(5117,Int(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),x_,Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(Plus(Times(C2,Plus(p,C1)),Times(CN1,a,n,x)),Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcCot(Times(a,x)))),Power(Times(Sqr(a),c,Plus(Sqr(n),Times(C4,Sqr(Plus(p,C1))))),-1)),x),Dist(Times(n,Plus(Times(C2,p),C3),Power(Times(a,c,Plus(Sqr(n),Times(C4,Sqr(Plus(p,C1))))),-1)),Int(Times(Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcCot(Times(a,x))))),x),x)),And(FreeQ(List(a,c,d,n),x),EqQ(d,Times(Sqr(a),c)),LeQ(p,CN1),NeQ(p,QQ(-3L,2L)),NeQ(Plus(Sqr(n),Times(C4,Sqr(Plus(p,C1)))),C0),Not(And(IntegerQ(p),IntegerQ(Times(C1D2,CI,n)))),Not(And(Not(IntegerQ(p)),IntegerQ(Times(C1D2,Plus(Times(CI,n),Negate(C1)))))))));
IIntegrate(5118,Int(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),Sqr(x_),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Times(Plus(n,Times(C2,Plus(p,C1),a,x)),Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcCot(Times(a,x)))),Power(Times(Power(a,3),c,Sqr(n),Plus(Sqr(n),C1)),-1)),x),And(FreeQ(List(a,c,d,n),x),EqQ(d,Times(Sqr(a),c)),EqQ(Plus(Sqr(n),Times(CN1,C2,Plus(p,C1))),C0),NeQ(Plus(Sqr(n),C1),C0))));
IIntegrate(5119,Int(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),Sqr(x_),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(Plus(n,Times(C2,Plus(p,C1),a,x)),Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcCot(Times(a,x)))),Power(Times(Power(a,3),c,Plus(Sqr(n),Times(C4,Sqr(Plus(p,C1))))),-1)),x),Dist(Times(Plus(Sqr(n),Times(CN1,C2,Plus(p,C1))),Power(Times(Sqr(a),c,Plus(Sqr(n),Times(C4,Sqr(Plus(p,C1))))),-1)),Int(Times(Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcCot(Times(a,x))))),x),x)),And(FreeQ(List(a,c,d,n),x),EqQ(d,Times(Sqr(a),c)),LeQ(p,CN1),NeQ(Plus(Sqr(n),Times(CN1,C2,Plus(p,C1))),C0),NeQ(Plus(Sqr(n),Times(C4,Sqr(Plus(p,C1)))),C0),Not(And(IntegerQ(p),IntegerQ(Times(C1D2,CI,n)))),Not(And(Not(IntegerQ(p)),IntegerQ(Times(C1D2,Plus(Times(CI,n),Negate(C1)))))))));
IIntegrate(5120,Int(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(x_,m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Negate(Dist(Times(Power(c,p),Power(Power(a,Plus(m,C1)),-1)),Subst(Int(Times(Exp(Times(n,x)),Power(Cot(x),Plus(m,Times(C2,Plus(p,C1)))),Power(Power(Cos(x),Times(C2,Plus(p,C1))),-1)),x),x,ArcCot(Times(a,x))),x)),And(FreeQ(List(a,c,d,n),x),EqQ(d,Times(Sqr(a),c)),IntegerQ(m),LeQ(C3,m,Times(CN2,Plus(p,C1))),IntegerQ(p))));
IIntegrate(5121,Int(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(d,p),Int(Times(u,Power(x,Times(C2,p)),Power(Plus(C1,Power(Times(Sqr(a),Sqr(x)),-1)),p),Exp(Times(n,ArcCot(Times(a,x))))),x),x),And(FreeQ(List(a,c,d,n),x),EqQ(d,Times(Sqr(a),c)),Not(IntegerQ(Times(C1D2,CI,n))),IntegerQ(p))));
IIntegrate(5122,Int(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Dist(Times(Power(Plus(c,Times(d,Sqr(x))),p),Power(Times(Power(x,Times(C2,p)),Power(Plus(C1,Power(Times(Sqr(a),Sqr(x)),-1)),p)),-1)),Int(Times(u,Power(x,Times(C2,p)),Power(Plus(C1,Power(Times(Sqr(a),Sqr(x)),-1)),p),Exp(Times(n,ArcCot(Times(a,x))))),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(d,Times(Sqr(a),c)),Not(IntegerQ(Times(C1D2,CI,n))),Not(IntegerQ(p)))));
IIntegrate(5123,Int(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,-2))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(c,p),Power(Power(Times(CI,a),Times(C2,p)),-1)),Int(Times(u,Power(Plus(CN1,Times(CI,a,x)),Plus(p,Times(CN1,C1D2,CI,n))),Power(Plus(C1,Times(CI,a,x)),Plus(p,Times(C1D2,CI,n))),Power(Power(x,Times(C2,p)),-1)),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(c,Times(Sqr(a),d)),Not(IntegerQ(Times(C1D2,CI,n))),Or(IntegerQ(p),GtQ(c,C0)),IntegersQ(Times(C2,p),Plus(p,Times(C1D2,CI,n))))));
IIntegrate(5124,Int(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Power(x_,-2))),p_DEFAULT)),x_Symbol),
    Condition(Negate(Dist(Power(c,p),Subst(Int(Times(Power(Plus(C1,Times(CN1,CI,x,Power(a,-1))),Plus(p,Times(C1D2,CI,n))),Power(Plus(C1,Times(CI,x,Power(a,-1))),Plus(p,Times(CN1,C1D2,CI,n))),Power(x,-2)),x),x,Power(x,-1)),x)),And(FreeQ(List(a,c,d,n,p),x),EqQ(c,Times(Sqr(a),d)),Not(IntegerQ(Times(C1D2,CI,n))),Or(IntegerQ(p),GtQ(c,C0)),Not(And(IntegerQ(Times(C2,p)),IntegerQ(Plus(p,Times(C1D2,CI,n))))))));
IIntegrate(5125,Int(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Power(x_,-2))),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Negate(Dist(Power(c,p),Subst(Int(Times(Power(Plus(C1,Times(CN1,CI,x,Power(a,-1))),Plus(p,Times(C1D2,CI,n))),Power(Plus(C1,Times(CI,x,Power(a,-1))),Plus(p,Times(CN1,C1D2,CI,n))),Power(Power(x,Plus(m,C2)),-1)),x),x,Power(x,-1)),x)),And(FreeQ(List(a,c,d,n,p),x),EqQ(c,Times(Sqr(a),d)),Not(IntegerQ(Times(C1D2,CI,n))),Or(IntegerQ(p),GtQ(c,C0)),Not(And(IntegerQ(Times(C2,p)),IntegerQ(Plus(p,Times(C1D2,CI,n))))),IntegerQ(m))));
IIntegrate(5126,Int(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Power(x_,-2))),p_DEFAULT),Power(x_,m_)),x_Symbol),
    Condition(Negate(Dist(Times(Power(c,p),Power(x,m),Power(Power(x,-1),m)),Subst(Int(Times(Power(Plus(C1,Times(CN1,CI,x,Power(a,-1))),Plus(p,Times(C1D2,CI,n))),Power(Plus(C1,Times(CI,x,Power(a,-1))),Plus(p,Times(CN1,C1D2,CI,n))),Power(Power(x,Plus(m,C2)),-1)),x),x,Power(x,-1)),x)),And(FreeQ(List(a,c,d,m,n,p),x),EqQ(c,Times(Sqr(a),d)),Not(IntegerQ(Times(C1D2,CI,n))),Or(IntegerQ(p),GtQ(c,C0)),Not(And(IntegerQ(Times(C2,p)),IntegerQ(Plus(p,Times(C1D2,CI,n))))),Not(IntegerQ(m)))));
IIntegrate(5127,Int(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,-2))),p_)),x_Symbol),
    Condition(Dist(Times(Power(Plus(c,Times(d,Power(x,-2))),p),Power(Power(Plus(C1,Power(Times(Sqr(a),Sqr(x)),-1)),p),-1)),Int(Times(u,Power(Plus(C1,Power(Times(Sqr(a),Sqr(x)),-1)),p),Exp(Times(n,ArcCot(Times(a,x))))),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(c,Times(Sqr(a),d)),Not(IntegerQ(Times(C1D2,CI,n))),Not(Or(IntegerQ(p),GtQ(c,C0))))));
IIntegrate(5128,Int(Times(Exp(Times(ArcCot(Times(c_DEFAULT,Plus(a_,Times(b_DEFAULT,x_)))),n_)),u_DEFAULT),x_Symbol),
    Condition(Dist(Power(CN1,Times(C1D2,CI,n)),Int(Times(u,Power(Exp(Times(n,ArcTan(Times(c,Plus(a,Times(b,x)))))),-1)),x),x),And(FreeQ(List(a,b,c),x),IntegerQ(Times(C1D2,CI,n)))));
IIntegrate(5129,Int(Exp(Times(ArcCot(Times(c_DEFAULT,Plus(a_,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(Times(CI,c,Plus(a,Times(b,x))),Times(C1D2,CI,n)),Power(Plus(C1,Power(Times(CI,c,Plus(a,Times(b,x))),-1)),Times(C1D2,CI,n)),Power(Power(Plus(C1,Times(CI,a,c),Times(CI,b,c,x)),Times(C1D2,CI,n)),-1)),Int(Times(Power(Plus(C1,Times(CI,a,c),Times(CI,b,c,x)),Times(C1D2,CI,n)),Power(Power(Plus(CN1,Times(CI,a,c),Times(CI,b,c,x)),Times(C1D2,CI,n)),-1)),x),x),And(FreeQ(List(a,b,c,n),x),Not(IntegerQ(Times(C1D2,CI,n))))));
IIntegrate(5130,Int(Times(Exp(Times(ArcCoth(Times(c_DEFAULT,Plus(a_,Times(b_DEFAULT,x_)))),n_)),Power(x_,m_)),x_Symbol),
    Condition(Dist(Times(C4,Power(Times(Power(CI,m),n,Power(b,Plus(m,C1)),Power(c,Plus(m,C1))),-1)),Subst(Int(Times(Power(x,Times(C2,Power(Times(CI,n),-1))),Power(Plus(C1,Times(CI,a,c),Times(Plus(C1,Times(CN1,CI,a,c)),Power(x,Times(C2,Power(Times(CI,n),-1))))),m),Power(Power(Plus(CN1,Power(x,Times(C2,Power(Times(CI,n),-1)))),Plus(m,C2)),-1)),x),x,Times(Power(Plus(C1,Power(Times(CI,c,Plus(a,Times(b,x))),-1)),Times(C1D2,CI,n)),Power(Power(Plus(C1,Negate(Power(Times(CI,c,Plus(a,Times(b,x))),-1))),Times(C1D2,CI,n)),-1))),x),And(FreeQ(List(a,b,c),x),ILtQ(m,C0),LtQ(CN1,Times(CI,n),C1))));
IIntegrate(5131,Int(Times(Exp(Times(ArcCoth(Times(c_DEFAULT,Plus(a_,Times(b_DEFAULT,x_)))),n_DEFAULT)),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(Times(CI,c,Plus(a,Times(b,x))),Times(C1D2,CI,n)),Power(Plus(C1,Power(Times(CI,c,Plus(a,Times(b,x))),-1)),Times(C1D2,CI,n)),Power(Power(Plus(C1,Times(CI,a,c),Times(CI,b,c,x)),Times(C1D2,CI,n)),-1)),Int(Times(Power(Plus(d,Times(e,x)),m),Power(Plus(C1,Times(CI,a,c),Times(CI,b,c,x)),Times(C1D2,CI,n)),Power(Power(Plus(CN1,Times(CI,a,c),Times(CI,b,c,x)),Times(C1D2,CI,n)),-1)),x),x),And(FreeQ(List(a,b,c,d,e,m,n),x),Not(IntegerQ(Times(C1D2,CI,n))))));
IIntegrate(5132,Int(Times(Exp(Times(ArcCot(Plus(a_,Times(b_DEFAULT,x_))),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,x_),Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(Times(c,Power(Plus(C1,Sqr(a)),-1)),p),Power(Times(Plus(Times(CI,a),Times(CI,b,x)),Power(Plus(C1,Times(CI,a),Times(CI,b,x)),-1)),Times(C1D2,CI,n)),Power(Times(Plus(C1,Times(CI,a),Times(CI,b,x)),Power(Plus(Times(CI,a),Times(CI,b,x)),-1)),Times(C1D2,CI,n)),Power(Plus(C1,Times(CN1,CI,a),Times(CN1,CI,b,x)),Times(C1D2,CI,n)),Power(Power(Plus(CN1,Times(CI,a),Times(CI,b,x)),Times(C1D2,CI,n)),-1)),Int(Times(u,Power(Plus(C1,Times(CN1,CI,a),Times(CN1,CI,b,x)),Plus(p,Times(CN1,C1D2,CI,n))),Power(Plus(C1,Times(CI,a),Times(CI,b,x)),Plus(p,Times(C1D2,CI,n)))),x),x),And(FreeQ(List(a,b,c,d,e,n,p),x),Not(IntegerQ(Times(C1D2,CI,n))),EqQ(Plus(Times(b,d),Times(CN1,C2,a,e)),C0),EqQ(Plus(Times(Sqr(b),c),Times(CN1,e,Plus(C1,Sqr(a)))),C0),Or(IntegerQ(p),GtQ(Times(c,Power(Plus(C1,Sqr(a)),-1)),C0)))));
IIntegrate(5133,Int(Times(Exp(Times(ArcCot(Plus(a_,Times(b_DEFAULT,x_))),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,x_),Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(Plus(c,Times(d,x),Times(e,Sqr(x))),p),Power(Power(Plus(C1,Sqr(a),Times(C2,a,b,x),Times(Sqr(b),Sqr(x))),p),-1)),Int(Times(u,Power(Plus(C1,Sqr(a),Times(C2,a,b,x),Times(Sqr(b),Sqr(x))),p),Exp(Times(n,ArcCot(Times(a,x))))),x),x),And(FreeQ(List(a,b,c,d,e,n,p),x),Not(IntegerQ(Times(C1D2,CI,n))),EqQ(Plus(Times(b,d),Times(CN1,C2,a,e)),C0),EqQ(Plus(Times(Sqr(b),c),Times(CN1,e,Plus(C1,Sqr(a)))),C0),Not(Or(IntegerQ(p),GtQ(Times(c,Power(Plus(C1,Sqr(a)),-1)),C0))))));
IIntegrate(5134,Int(Times(Exp(Times(ArcCot(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),-1))),n_DEFAULT)),u_DEFAULT),x_Symbol),
    Condition(Int(Times(u,Exp(Times(n,ArcTan(Plus(Times(a,Power(c,-1)),Times(b,x,Power(c,-1))))))),x),FreeQ(List(a,b,c,n),x)));
IIntegrate(5135,Int(ArcTan(Plus(a_,Times(b_DEFAULT,Power(x_,n_)))),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcTan(Plus(a,Times(b,Power(x,n))))),x),Negate(Dist(Times(b,n),Int(Times(Power(x,n),Power(Plus(C1,Sqr(a),Times(C2,a,b,Power(x,n)),Times(Sqr(b),Power(x,Times(C2,n)))),-1)),x),x))),FreeQ(List(a,b,n),x)));
IIntegrate(5136,Int(ArcCot(Plus(a_,Times(b_DEFAULT,Power(x_,n_)))),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcCot(Plus(a,Times(b,Power(x,n))))),x),Dist(Times(b,n),Int(Times(Power(x,n),Power(Plus(C1,Sqr(a),Times(C2,a,b,Power(x,n)),Times(Sqr(b),Power(x,Times(C2,n)))),-1)),x),x)),FreeQ(List(a,b,n),x)));
IIntegrate(5137,Int(Times(ArcTan(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),Power(x_,-1)),x_Symbol),
    Condition(Plus(Dist(Times(C1D2,CI),Int(Times(Log(Plus(C1,Times(CN1,CI,a),Times(CN1,CI,b,Power(x,n)))),Power(x,-1)),x),x),Negate(Dist(Times(C1D2,CI),Int(Times(Log(Plus(C1,Times(CI,a),Times(CI,b,Power(x,n)))),Power(x,-1)),x),x))),FreeQ(List(a,b,n),x)));
IIntegrate(5138,Int(Times(ArcCot(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),Power(x_,-1)),x_Symbol),
    Condition(Plus(Dist(Times(C1D2,CI),Int(Times(Log(Plus(C1,Times(CN1,CI,Power(Plus(a,Times(b,Power(x,n))),-1)))),Power(x,-1)),x),x),Negate(Dist(Times(C1D2,CI),Int(Times(Log(Plus(C1,Times(CI,Power(Plus(a,Times(b,Power(x,n))),-1)))),Power(x,-1)),x),x))),FreeQ(List(a,b,n),x)));
IIntegrate(5139,Int(Times(ArcTan(Plus(a_,Times(b_DEFAULT,Power(x_,n_)))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),ArcTan(Plus(a,Times(b,Power(x,n)))),Power(Plus(m,C1),-1)),x),Negate(Dist(Times(b,n,Power(Plus(m,C1),-1)),Int(Times(Power(x,Plus(m,n)),Power(Plus(C1,Sqr(a),Times(C2,a,b,Power(x,n)),Times(Sqr(b),Power(x,Times(C2,n)))),-1)),x),x))),And(FreeQ(List(a,b),x),RationalQ(m,n),Unequal(Plus(m,C1),C0),Unequal(Plus(m,C1),n))));
IIntegrate(5140,Int(Times(ArcCot(Plus(a_,Times(b_DEFAULT,Power(x_,n_)))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),ArcCot(Plus(a,Times(b,Power(x,n)))),Power(Plus(m,C1),-1)),x),Dist(Times(b,n,Power(Plus(m,C1),-1)),Int(Times(Power(x,Plus(m,n)),Power(Plus(C1,Sqr(a),Times(C2,a,b,Power(x,n)),Times(Sqr(b),Power(x,Times(C2,n)))),-1)),x),x)),And(FreeQ(List(a,b),x),RationalQ(m,n),Unequal(Plus(m,C1),C0),Unequal(Plus(m,C1),n))));
IIntegrate(5141,Int(ArcTan(Plus(a_DEFAULT,Times(b_DEFAULT,Power(f_,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Plus(Dist(Times(C1D2,CI),Int(Log(Plus(C1,Times(CN1,CI,a),Times(CN1,CI,b,Power(f,Plus(c,Times(d,x)))))),x),x),Negate(Dist(Times(C1D2,CI),Int(Log(Plus(C1,Times(CI,a),Times(CI,b,Power(f,Plus(c,Times(d,x)))))),x),x))),FreeQ(List(a,b,c,d,f),x)));
IIntegrate(5142,Int(ArcCot(Plus(a_DEFAULT,Times(b_DEFAULT,Power(f_,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Plus(Dist(Times(C1D2,CI),Int(Log(Plus(C1,Times(CN1,CI,Power(Plus(a,Times(b,Power(f,Plus(c,Times(d,x))))),-1)))),x),x),Negate(Dist(Times(C1D2,CI),Int(Log(Plus(C1,Times(CI,Power(Plus(a,Times(b,Power(f,Plus(c,Times(d,x))))),-1)))),x),x))),FreeQ(List(a,b,c,d,f),x)));
IIntegrate(5143,Int(Times(ArcTan(Plus(a_DEFAULT,Times(b_DEFAULT,Power(f_,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Dist(Times(C1D2,CI),Int(Times(Power(x,m),Log(Plus(C1,Times(CN1,CI,a),Times(CN1,CI,b,Power(f,Plus(c,Times(d,x))))))),x),x),Negate(Dist(Times(C1D2,CI),Int(Times(Power(x,m),Log(Plus(C1,Times(CI,a),Times(CI,b,Power(f,Plus(c,Times(d,x))))))),x),x))),And(FreeQ(List(a,b,c,d,f),x),IntegerQ(m),Greater(m,C0))));
IIntegrate(5144,Int(Times(ArcCot(Plus(a_DEFAULT,Times(b_DEFAULT,Power(f_,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Dist(Times(C1D2,CI),Int(Times(Power(x,m),Log(Plus(C1,Times(CN1,CI,Power(Plus(a,Times(b,Power(f,Plus(c,Times(d,x))))),-1))))),x),x),Negate(Dist(Times(C1D2,CI),Int(Times(Power(x,m),Log(Plus(C1,Times(CI,Power(Plus(a,Times(b,Power(f,Plus(c,Times(d,x))))),-1))))),x),x))),And(FreeQ(List(a,b,c,d,f),x),IntegerQ(m),Greater(m,C0))));
IIntegrate(5145,Int(Times(Power(ArcTan(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),-1))),m_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Int(Times(u,Power(ArcCot(Plus(Times(a,Power(c,-1)),Times(b,Power(x,n),Power(c,-1)))),m)),x),FreeQ(List(a,b,c,n,m),x)));
IIntegrate(5146,Int(Times(Power(ArcCot(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),-1))),m_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Int(Times(u,Power(ArcTan(Plus(Times(a,Power(c,-1)),Times(b,Power(x,n),Power(c,-1)))),m)),x),FreeQ(List(a,b,c,n,m),x)));
IIntegrate(5147,Int(ArcTan(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcTan(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2)))),x),Negate(Dist(c,Int(Times(x,Power(Plus(a,Times(b,Sqr(x))),CN1D2)),x),x))),And(FreeQ(List(a,b,c),x),EqQ(Plus(b,Sqr(c)),C0))));
IIntegrate(5148,Int(ArcCot(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcCot(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2)))),x),Dist(c,Int(Times(x,Power(Plus(a,Times(b,Sqr(x))),CN1D2)),x),x)),And(FreeQ(List(a,b,c),x),EqQ(Plus(b,Sqr(c)),C0))));
IIntegrate(5149,Int(Times(ArcTan(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),Power(x_,-1)),x_Symbol),
    Condition(Plus(Simp(Times(ArcTan(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))),Log(x)),x),Negate(Dist(c,Int(Times(Log(x),Power(Plus(a,Times(b,Sqr(x))),CN1D2)),x),x))),And(FreeQ(List(a,b,c),x),EqQ(Plus(b,Sqr(c)),C0))));
IIntegrate(5150,Int(Times(ArcCot(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),Power(x_,-1)),x_Symbol),
    Condition(Plus(Simp(Times(ArcCot(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))),Log(x)),x),Dist(c,Int(Times(Log(x),Power(Plus(a,Times(b,Sqr(x))),CN1D2)),x),x)),And(FreeQ(List(a,b,c),x),EqQ(Plus(b,Sqr(c)),C0))));
  }
}
}
