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
public class IntRules0 { 

	public static void initialize() {
		Initializer.init();
	}

	private static class Initializer  {

		private static void init() {
IIntegrate(1,Int(Times(u_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Int(Times(u,Power(Times(b,Power(x,n)),p)),x),And(FreeQ(List(a,b,n,p),x),EqQ(a,C0))));
IIntegrate(2,Int(Times(u_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Int(Times(u,Power(a,p)),x),And(FreeQ(List(a,b,n,p),x),EqQ(b,C0))));
IIntegrate(3,Int(Times(u_DEFAULT,Power(Plus(a_,Times(c_DEFAULT,Power(x_,j_DEFAULT)),Times(b_DEFAULT,Power(x_,n_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Int(Times(u,Power(Plus(Times(b,Power(x,n)),Times(c,Power(x,Times(C2,n)))),p)),x),And(FreeQ(List(a,b,c,n,p),x),EqQ(j,Times(C2,n)),EqQ(a,C0))));
IIntegrate(4,Int(Times(u_DEFAULT,Power(Plus(a_DEFAULT,Times(c_DEFAULT,Power(x_,j_DEFAULT)),Times(b_DEFAULT,Power(x_,n_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Int(Times(u,Power(Plus(a,Times(c,Power(x,Times(C2,n)))),p)),x),And(FreeQ(List(a,b,c,n,p),x),EqQ(j,Times(C2,n)),EqQ(b,C0))));
IIntegrate(5,Int(Times(u_DEFAULT,Power(Plus(a_DEFAULT,Times(c_DEFAULT,Power(x_,j_DEFAULT)),Times(b_DEFAULT,Power(x_,n_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Int(Times(u,Power(Plus(a,Times(b,Power(x,n))),p)),x),And(FreeQ(List(a,b,c,n,p),x),EqQ(j,Times(C2,n)),EqQ(c,C0))));
IIntegrate(6,Int(Times(u_DEFAULT,Power(Plus(w_DEFAULT,Times(a_DEFAULT,v_),Times(b_DEFAULT,v_)),p_DEFAULT)),x_Symbol),
    Condition(Int(Times(u,Power(Plus(Times(Plus(a,b),v),w),p)),x),And(FreeQ(List(a,b),x),Not(FreeQ(v,x)))));
IIntegrate(7,Int(Times(u_DEFAULT,Power($p("§px"),p_)),x_Symbol),
    Condition(Int(Times(u,Power($s("§px"),Simplify(p))),x),And(PolyQ($s("§px"),x),Not(RationalQ(p)),FreeQ(p,x),RationalQ(Simplify(p)))));
IIntegrate(8,Int(a_,x_Symbol),
    Condition(Simp(Times(a,x),x),FreeQ(a,x)));
IIntegrate(9,Int(Times(a_,Plus(b_,Times(c_DEFAULT,x_))),x_Symbol),
    Condition(Simp(Times(a,Sqr(Plus(b,Times(c,x))),Power(Times(C2,c),-1)),x),FreeQ(List(a,b,c),x)));
IIntegrate(10,Int(Negate(u_),x_Symbol),
    Dist(Identity(CN1),Int(u,x),x));
IIntegrate(11,Int(Times(Complex(C0,a_),u_),x_Symbol),
    Condition(Dist(Complex(Identity(C0),a),Int(u,x),x),And(FreeQ(a,x),EqQ(Sqr(a),C1))));
IIntegrate(12,Int(Times(a_,u_),x_Symbol),
    Condition(Dist(a,Int(u,x),x),And(FreeQ(a,x),Not(MatchQ(u,Condition(Times(b_,v_),FreeQ(b,x)))))));
IIntegrate(13,Int(u_,x_Symbol),
    Condition(Simp(IntSum(u,x),x),SumQ(u)));
IIntegrate(14,Int(Times(u_,Power(Times(c_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Int(ExpandIntegrand(Times(Power(Times(c,x),m),u),x),x),And(FreeQ(List(c,m),x),SumQ(u),Not(LinearQ(u,x)),Not(MatchQ(u,Condition(Plus(a_,Times(b_DEFAULT,v_)),And(FreeQ(List(a,b),x),InverseFunctionQ(v))))))));
IIntegrate(15,Int(Times(u_DEFAULT,Power(Times(a_DEFAULT,Power(x_,n_)),m_)),x_Symbol),
    Condition(Dist(Times(Power(a,IntPart(m)),Power(Times(a,Power(x,n)),FracPart(m)),Power(Power(x,Times(n,FracPart(m))),-1)),Int(Times(u,Power(x,Times(m,n))),x),x),And(FreeQ(List(a,m,n),x),Not(IntegerQ(m)))));
IIntegrate(16,Int(Times(u_DEFAULT,Power(v_,m_DEFAULT),Power(Times(b_,v_),n_)),x_Symbol),
    Condition(Dist(Power(Power(b,m),-1),Int(Times(u,Power(Times(b,v),Plus(m,n))),x),x),And(FreeQ(List(b,n),x),IntegerQ(m))));
IIntegrate(17,Int(Times(u_DEFAULT,Power(Times(a_DEFAULT,v_),m_),Power(Times(b_DEFAULT,v_),n_)),x_Symbol),
    Condition(Dist(Times(Power(a,Plus(m,C1D2)),Power(b,Plus(n,Negate(C1D2))),Sqrt(Times(b,v)),Power(Times(a,v),CN1D2)),Int(Times(u,Power(v,Plus(m,n))),x),x),And(FreeQ(List(a,b,m),x),Not(IntegerQ(m)),IGtQ(Plus(n,C1D2),C0),IntegerQ(Plus(m,n)))));
IIntegrate(18,Int(Times(u_DEFAULT,Power(Times(a_DEFAULT,v_),m_),Power(Times(b_DEFAULT,v_),n_)),x_Symbol),
    Condition(Dist(Times(Power(a,Plus(m,Negate(C1D2))),Power(b,Plus(n,C1D2)),Sqrt(Times(a,v)),Power(Times(b,v),CN1D2)),Int(Times(u,Power(v,Plus(m,n))),x),x),And(FreeQ(List(a,b,m),x),Not(IntegerQ(m)),ILtQ(Plus(n,Negate(C1D2)),C0),IntegerQ(Plus(m,n)))));
IIntegrate(19,Int(Times(u_DEFAULT,Power(Times(a_DEFAULT,v_),m_),Power(Times(b_DEFAULT,v_),n_)),x_Symbol),
    Condition(Dist(Times(Power(a,Plus(m,n)),Power(Times(b,v),n),Power(Power(Times(a,v),n),-1)),Int(Times(u,Power(v,Plus(m,n))),x),x),And(FreeQ(List(a,b,m,n),x),Not(IntegerQ(m)),Not(IntegerQ(n)),IntegerQ(Plus(m,n)))));
IIntegrate(20,Int(Times(u_DEFAULT,Power(Times(a_DEFAULT,v_),m_),Power(Times(b_DEFAULT,v_),n_)),x_Symbol),
    Condition(Dist(Times(Power(b,IntPart(n)),Power(Times(b,v),FracPart(n)),Power(Times(Power(a,IntPart(n)),Power(Times(a,v),FracPart(n))),-1)),Int(Times(u,Power(Times(a,v),Plus(m,n))),x),x),And(FreeQ(List(a,b,m,n),x),Not(IntegerQ(m)),Not(IntegerQ(n)),Not(IntegerQ(Plus(m,n))))));
IIntegrate(21,Int(Times(u_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,v_)),m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,v_)),n_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Times(b,Power(d,-1)),m),Int(Times(u,Power(Plus(c,Times(d,v)),Plus(m,n))),x),x),And(FreeQ(List(a,b,c,d,n),x),EqQ(Plus(Times(b,c),Times(CN1,a,d)),C0),IntegerQ(m),Or(Not(IntegerQ(n)),SimplerQ(Plus(c,Times(d,x)),Plus(a,Times(b,x)))))));
IIntegrate(22,Int(Times(u_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,v_)),m_),Power(Plus(c_,Times(d_DEFAULT,v_)),n_)),x_Symbol),
    Condition(Dist(Power(Times(b,Power(d,-1)),m),Int(Times(u,Power(Plus(c,Times(d,v)),Plus(m,n))),x),x),And(FreeQ(List(a,b,c,d,m,n),x),EqQ(Plus(Times(b,c),Times(CN1,a,d)),C0),GtQ(Times(b,Power(d,-1)),C0),Not(Or(IntegerQ(m),IntegerQ(n))))));
IIntegrate(23,Int(Times(u_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,v_)),m_),Power(Plus(c_,Times(d_DEFAULT,v_)),n_)),x_Symbol),
    Condition(Dist(Times(Power(Plus(a,Times(b,v)),m),Power(Power(Plus(c,Times(d,v)),m),-1)),Int(Times(u,Power(Plus(c,Times(d,v)),Plus(m,n))),x),x),And(FreeQ(List(a,b,c,d,m,n),x),EqQ(Plus(Times(b,c),Times(CN1,a,d)),C0),Not(Or(IntegerQ(m),IntegerQ(n),GtQ(Times(b,Power(d,-1)),C0))))));
IIntegrate(24,Int(Times(u_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,v_)),m_),Plus(A_DEFAULT,Times(B_DEFAULT,v_),Times(C_DEFAULT,Sqr(v_)))),x_Symbol),
    Condition(Dist(Power(b,-2),Int(Times(u,Power(Plus(a,Times(b,v)),Plus(m,C1)),Simp(Plus(Times(b,BSymbol),Times(CN1,a,CSymbol),Times(b,CSymbol,v)),x)),x),x),And(FreeQ(List(a,b,ASymbol,BSymbol,CSymbol),x),EqQ(Plus(Times(ASymbol,Sqr(b)),Times(CN1,a,b,BSymbol),Times(Sqr(a),CSymbol)),C0),LeQ(m,CN1))));
IIntegrate(25,Int(Times(u_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_DEFAULT))),m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Power(x_,q_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Times(d,Power(a,-1)),p),Int(Times(u,Power(Plus(a,Times(b,Power(x,n))),Plus(m,p)),Power(Power(x,Times(n,p)),-1)),x),x),And(FreeQ(List(a,b,c,d,m,n),x),EqQ(q,Negate(n)),IntegerQ(p),EqQ(Plus(Times(a,c),Times(CN1,b,d)),C0),Not(And(IntegerQ(m),NegQ(n))))));
IIntegrate(26,Int(Times(u_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_DEFAULT))),m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Power(x_,j_))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Times(CN1,Sqr(b),Power(d,-1)),m),Int(Times(u,Power(Power(Plus(a,Times(CN1,b,Power(x,n))),m),-1)),x),x),And(FreeQ(List(a,b,c,d,m,n,p),x),EqQ(j,Times(C2,n)),EqQ(p,Negate(m)),EqQ(Plus(Times(Sqr(b),c),Times(Sqr(a),d)),C0),GtQ(a,C0),LtQ(d,C0))));
IIntegrate(27,Int(Times(u_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Int(Times(u,Cancel(Times(Power(Plus(Times(C1D2,b),Times(c,x)),Times(C2,p)),Power(Power(c,p),-1)))),x),And(FreeQ(List(a,b,c),x),EqQ(Plus(Sqr(b),Times(CN1,C4,a,c)),C0),IntegerQ(p))));
IIntegrate(28,Int(Times(u_DEFAULT,Power(Plus(a_,Times(c_DEFAULT,Power(x_,$p("n2",true))),Times(b_DEFAULT,Power(x_,n_))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Power(c,p),-1),Int(Times(u,Power(Plus(Times(C1D2,b),Times(c,Power(x,n))),Times(C2,p))),x),x),And(FreeQ(List(a,b,c,n),x),EqQ($s("n2"),Times(C2,n)),EqQ(Plus(Sqr(b),Times(CN1,C4,a,c)),C0),IntegerQ(p))));
IIntegrate(29,Int(Power(x_,-1),x_Symbol),
    Simp(Log(x),x));
IIntegrate(30,Int(Power(x_,m_DEFAULT),x_Symbol),
    Condition(Simp(Times(Power(x,Plus(m,C1)),Power(Plus(m,C1),-1)),x),And(FreeQ(m,x),NeQ(m,CN1))));
IIntegrate(31,Int(Power(Plus(a_,Times(b_DEFAULT,x_)),-1),x_Symbol),
    Condition(Simp(Times(Log(RemoveContent(Plus(a,Times(b,x)),x)),Power(b,-1)),x),FreeQ(List(a,b),x)));
IIntegrate(32,Int(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),m_),x_Symbol),
    Condition(Simp(Times(Power(Plus(a,Times(b,x)),Plus(m,C1)),Power(Times(b,Plus(m,C1)),-1)),x),And(FreeQ(List(a,b,m),x),NeQ(m,CN1))));
IIntegrate(33,Int(Power(Plus(a_DEFAULT,Times(b_DEFAULT,u_)),m_),x_Symbol),
    Condition(Dist(Power(Coefficient(u,x,C1),-1),Subst(Int(Power(Plus(a,Times(b,x)),m),x),x,u),x),And(FreeQ(List(a,b,m),x),LinearQ(u,x),NeQ(u,x))));
IIntegrate(34,Int(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),m_DEFAULT),Plus(c_,Times(d_DEFAULT,x_))),x_Symbol),
    Condition(Simp(Times(d,x,Power(Plus(a,Times(b,x)),Plus(m,C1)),Power(Times(b,Plus(m,C2)),-1)),x),And(FreeQ(List(a,b,c,d,m),x),EqQ(Plus(Times(a,d),Times(CN1,b,c,Plus(m,C2))),C0))));
IIntegrate(35,Int(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),-1),Power(Plus(c_,Times(d_DEFAULT,x_)),-1)),x_Symbol),
    Condition(Int(Power(Plus(Times(a,c),Times(b,d,Sqr(x))),-1),x),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Times(b,c),Times(a,d)),C0))));
IIntegrate(36,Int(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),-1),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),-1)),x_Symbol),
    Condition(Plus(Dist(Times(b,Power(Plus(Times(b,c),Times(CN1,a,d)),-1)),Int(Power(Plus(a,Times(b,x)),-1),x),x),Negate(Dist(Times(d,Power(Plus(Times(b,c),Times(CN1,a,d)),-1)),Int(Power(Plus(c,Times(d,x)),-1),x),x))),And(FreeQ(List(a,b,c,d),x),NeQ(Plus(Times(b,c),Times(CN1,a,d)),C0))));
IIntegrate(37,Int(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),m_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_)),x_Symbol),
    Condition(Simp(Times(Power(Plus(a,Times(b,x)),Plus(m,C1)),Power(Plus(c,Times(d,x)),Plus(n,C1)),Power(Times(Plus(Times(b,c),Times(CN1,a,d)),Plus(m,C1)),-1)),x),And(FreeQ(List(a,b,c,d,m,n),x),NeQ(Plus(Times(b,c),Times(CN1,a,d)),C0),EqQ(Plus(m,n,C2),C0),NeQ(m,CN1))));
IIntegrate(38,Int(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),m_),Power(Plus(c_,Times(d_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Plus(Simp(Times(x,Power(Plus(a,Times(b,x)),m),Power(Plus(c,Times(d,x)),m),Power(Plus(Times(C2,m),C1),-1)),x),Dist(Times(C2,a,c,m,Power(Plus(Times(C2,m),C1),-1)),Int(Times(Power(Plus(a,Times(b,x)),Plus(m,Negate(C1))),Power(Plus(c,Times(d,x)),Plus(m,Negate(C1)))),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),IGtQ(Plus(m,C1D2),C0))));
IIntegrate(39,Int(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),QQ(-3L,2L)),Power(Plus(c_,Times(d_DEFAULT,x_)),QQ(-3L,2L))),x_Symbol),
    Condition(Simp(Times(x,Power(Times(a,c,Sqrt(Plus(a,Times(b,x))),Sqrt(Plus(c,Times(d,x)))),-1)),x),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Times(b,c),Times(a,d)),C0))));
IIntegrate(40,Int(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),m_),Power(Plus(c_,Times(d_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(x,Power(Plus(a,Times(b,x)),Plus(m,C1)),Power(Plus(c,Times(d,x)),Plus(m,C1)),Power(Times(C2,a,c,Plus(m,C1)),-1)),x)),Dist(Times(Plus(Times(C2,m),C3),Power(Times(C2,a,c,Plus(m,C1)),-1)),Int(Times(Power(Plus(a,Times(b,x)),Plus(m,C1)),Power(Plus(c,Times(d,x)),Plus(m,C1))),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),ILtQ(Plus(m,QQ(3L,2L)),C0))));
IIntegrate(41,Int(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Int(Power(Plus(Times(a,c),Times(b,d,Sqr(x))),m),x),And(FreeQ(List(a,b,c,d,m),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),Or(IntegerQ(m),And(GtQ(a,C0),GtQ(c,C0))))));
IIntegrate(42,Int(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),m_),Power(Plus(c_,Times(d_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Dist(Times(Power(Plus(a,Times(b,x)),FracPart(m)),Power(Plus(c,Times(d,x)),FracPart(m)),Power(Power(Plus(Times(a,c),Times(b,d,Sqr(x))),FracPart(m)),-1)),Int(Power(Plus(Times(a,c),Times(b,d,Sqr(x))),m),x),x),And(FreeQ(List(a,b,c,d,m),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),Not(IntegerQ(Times(C2,m))))));
IIntegrate(43,Int(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),m_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_DEFAULT)),x_Symbol),
    Condition(Int(ExpandIntegrand(Times(Power(Plus(a,Times(b,x)),m),Power(Plus(c,Times(d,x)),n)),x),x),And(FreeQ(List(a,b,c,d,n),x),NeQ(Plus(Times(b,c),Times(CN1,a,d)),C0),IGtQ(m,C0),Or(Not(IntegerQ(n)),And(EqQ(c,C0),LeQ(Plus(Times(C7,m),Times(C4,n),C4),C0)),LtQ(Plus(Times(C9,m),Times(C5,Plus(n,C1))),C0),GtQ(Plus(m,n,C2),C0)))));
IIntegrate(44,Int(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),m_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_DEFAULT)),x_Symbol),
    Condition(Int(ExpandIntegrand(Times(Power(Plus(a,Times(b,x)),m),Power(Plus(c,Times(d,x)),n)),x),x),And(FreeQ(List(a,b,c,d),x),NeQ(Plus(Times(b,c),Times(CN1,a,d)),C0),ILtQ(m,C0),IntegerQ(n),Not(And(IGtQ(n,C0),LtQ(Plus(m,n,C2),C0))))));
IIntegrate(45,Int(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),m_),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(a,Times(b,x)),Plus(m,C1)),Power(Plus(c,Times(d,x)),Plus(n,C1)),Power(Times(Plus(Times(b,c),Times(CN1,a,d)),Plus(m,C1)),-1)),x),Negate(Dist(Times(d,Simplify(Plus(m,n,C2)),Power(Times(Plus(Times(b,c),Times(CN1,a,d)),Plus(m,C1)),-1)),Int(Times(Power(Plus(a,Times(b,x)),Simplify(Plus(m,C1))),Power(Plus(c,Times(d,x)),n)),x),x))),And(FreeQ(List(a,b,c,d,m,n),x),NeQ(Plus(Times(b,c),Times(CN1,a,d)),C0),ILtQ(Simplify(Plus(m,n,C2)),C0),NeQ(m,CN1),Not(And(LtQ(m,CN1),LtQ(n,CN1),Or(EqQ(a,C0),And(NeQ(c,C0),LtQ(Plus(m,Negate(n)),C0),IntegerQ(n))))),Or(SumSimplerQ(m,C1),Not(SumSimplerQ(n,C1))))));
IIntegrate(46,Int(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),QQ(-9L,4L)),Power(Plus(c_,Times(d_DEFAULT,x_)),CN1D4)),x_Symbol),
    Condition(Plus(Simp(Times(CN4,Power(Times(C5,b,Power(Plus(a,Times(b,x)),QQ(5L,4L)),Power(Plus(c,Times(d,x)),C1D4)),-1)),x),Negate(Dist(Times(d,Power(Times(C5,b),-1)),Int(Power(Times(Power(Plus(a,Times(b,x)),QQ(5L,4L)),Power(Plus(c,Times(d,x)),QQ(5L,4L))),-1),x),x))),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),NegQ(Times(Sqr(a),Sqr(b))))));
IIntegrate(47,Int(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),m_),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(a,Times(b,x)),Plus(m,C1)),Power(Plus(c,Times(d,x)),n),Power(Times(b,Plus(m,C1)),-1)),x),Negate(Dist(Times(d,n,Power(Times(b,Plus(m,C1)),-1)),Int(Times(Power(Plus(a,Times(b,x)),Plus(m,C1)),Power(Plus(c,Times(d,x)),Plus(n,Negate(C1)))),x),x))),And(FreeQ(List(a,b,c,d),x),NeQ(Plus(Times(b,c),Times(CN1,a,d)),C0),GtQ(n,C0),LtQ(m,CN1),Not(And(IntegerQ(n),Not(IntegerQ(m)))),Not(And(ILeQ(Plus(m,n,C2),C0),Or(FractionQ(m),GeQ(Plus(Times(C2,n),m,C1),C0)))),IntLinearQ(a,b,c,d,m,n,x))));
IIntegrate(48,Int(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),QQ(-5L,4L)),Power(Plus(c_,Times(d_DEFAULT,x_)),CN1D4)),x_Symbol),
    Condition(Plus(Simp(Times(CN2,Power(Times(b,Power(Plus(a,Times(b,x)),C1D4),Power(Plus(c,Times(d,x)),C1D4)),-1)),x),Dist(c,Int(Power(Times(Power(Plus(a,Times(b,x)),QQ(5L,4L)),Power(Plus(c,Times(d,x)),QQ(5L,4L))),-1),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),NegQ(Times(Sqr(a),Sqr(b))))));
IIntegrate(49,Int(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),m_),Power(Plus(c_,Times(d_DEFAULT,x_)),n_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(a,Times(b,x)),Plus(m,C1)),Power(Plus(c,Times(d,x)),n),Power(Times(b,Plus(m,n,C1)),-1)),x),Dist(Times(C2,c,n,Power(Plus(m,n,C1),-1)),Int(Times(Power(Plus(a,Times(b,x)),m),Power(Plus(c,Times(d,x)),Plus(n,Negate(C1)))),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),IGtQ(Plus(m,C1D2),C0),IGtQ(Plus(n,C1D2),C0),LtQ(m,n))));
IIntegrate(50,Int(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),m_),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(a,Times(b,x)),Plus(m,C1)),Power(Plus(c,Times(d,x)),n),Power(Times(b,Plus(m,n,C1)),-1)),x),Dist(Times(n,Plus(Times(b,c),Times(CN1,a,d)),Power(Times(b,Plus(m,n,C1)),-1)),Int(Times(Power(Plus(a,Times(b,x)),m),Power(Plus(c,Times(d,x)),Plus(n,Negate(C1)))),x),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Plus(Times(b,c),Times(CN1,a,d)),C0),GtQ(n,C0),NeQ(Plus(m,n,C1),C0),Not(And(IGtQ(m,C0),Or(Not(IntegerQ(n)),And(GtQ(m,C0),LtQ(Plus(m,Negate(n)),C0))))),Not(ILtQ(Plus(m,n,C2),C0)),IntLinearQ(a,b,c,d,m,n,x))));
  }
}
}
