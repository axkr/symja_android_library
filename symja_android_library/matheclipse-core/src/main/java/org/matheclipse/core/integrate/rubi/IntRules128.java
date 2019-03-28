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
public class IntRules128 { 

	public static void initialize() {
		Initializer.init();
	}

	private static class Initializer  {

		private static void init() {
IIntegrate(6401,Int(Times(Erf(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(e,x),Plus(m,C1)),Erf(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(e,Plus(m,C1)),-1)),x),Negate(Dist(Times(C2,b,d,n,Power(Times(Sqrt(Pi),Plus(m,C1)),-1)),Int(Times(Power(Times(e,x),m),Power(Exp(Sqr(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),-1)),x),x))),And(FreeQ(List(a,b,c,d,e,m,n),x),NeQ(m,CN1))));
IIntegrate(6402,Int(Times(Erfc(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(e,x),Plus(m,C1)),Erfc(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(e,Plus(m,C1)),-1)),x),Dist(Times(C2,b,d,n,Power(Times(Sqrt(Pi),Plus(m,C1)),-1)),Int(Times(Power(Times(e,x),m),Power(Exp(Sqr(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),-1)),x),x)),And(FreeQ(List(a,b,c,d,e,m,n),x),NeQ(m,CN1))));
IIntegrate(6403,Int(Times(Erfi(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(e,x),Plus(m,C1)),Erfi(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(e,Plus(m,C1)),-1)),x),Negate(Dist(Times(C2,b,d,n,Power(Times(Sqrt(Pi),Plus(m,C1)),-1)),Int(Times(Power(Times(e,x),m),Exp(Sqr(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))))),x),x))),And(FreeQ(List(a,b,c,d,e,m,n),x),NeQ(m,CN1))));
IIntegrate(6404,Int(Times(Erf(Times(b_DEFAULT,x_)),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Dist(Times(C1D2,CI),Int(Times(Exp(Plus(Times(CN1,CI,c),Times(CN1,CI,d,Sqr(x)))),Erf(Times(b,x))),x),x),Negate(Dist(Times(C1D2,CI),Int(Times(Exp(Plus(Times(CI,c),Times(CI,d,Sqr(x)))),Erf(Times(b,x))),x),x))),And(FreeQ(List(b,c,d),x),EqQ(Sqr(d),Negate(Power(b,4))))));
IIntegrate(6405,Int(Times(Erfc(Times(b_DEFAULT,x_)),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Dist(Times(C1D2,CI),Int(Times(Exp(Plus(Times(CN1,CI,c),Times(CN1,CI,d,Sqr(x)))),Erfc(Times(b,x))),x),x),Negate(Dist(Times(C1D2,CI),Int(Times(Exp(Plus(Times(CI,c),Times(CI,d,Sqr(x)))),Erfc(Times(b,x))),x),x))),And(FreeQ(List(b,c,d),x),EqQ(Sqr(d),Negate(Power(b,4))))));
IIntegrate(6406,Int(Times(Erfi(Times(b_DEFAULT,x_)),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Dist(Times(C1D2,CI),Int(Times(Exp(Plus(Times(CN1,CI,c),Times(CN1,CI,d,Sqr(x)))),Erfi(Times(b,x))),x),x),Negate(Dist(Times(C1D2,CI),Int(Times(Exp(Plus(Times(CI,c),Times(CI,d,Sqr(x)))),Erfi(Times(b,x))),x),x))),And(FreeQ(List(b,c,d),x),EqQ(Sqr(d),Negate(Power(b,4))))));
IIntegrate(6407,Int(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erf(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Dist(C1D2,Int(Times(Exp(Plus(Times(CN1,CI,c),Times(CN1,CI,d,Sqr(x)))),Erf(Times(b,x))),x),x),Dist(C1D2,Int(Times(Exp(Plus(Times(CI,c),Times(CI,d,Sqr(x)))),Erf(Times(b,x))),x),x)),And(FreeQ(List(b,c,d),x),EqQ(Sqr(d),Negate(Power(b,4))))));
IIntegrate(6408,Int(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erfc(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Dist(C1D2,Int(Times(Exp(Plus(Times(CN1,CI,c),Times(CN1,CI,d,Sqr(x)))),Erfc(Times(b,x))),x),x),Dist(C1D2,Int(Times(Exp(Plus(Times(CI,c),Times(CI,d,Sqr(x)))),Erfc(Times(b,x))),x),x)),And(FreeQ(List(b,c,d),x),EqQ(Sqr(d),Negate(Power(b,4))))));
IIntegrate(6409,Int(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erfi(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Dist(C1D2,Int(Times(Exp(Plus(Times(CN1,CI,c),Times(CN1,CI,d,Sqr(x)))),Erfi(Times(b,x))),x),x),Dist(C1D2,Int(Times(Exp(Plus(Times(CI,c),Times(CI,d,Sqr(x)))),Erfi(Times(b,x))),x),x)),And(FreeQ(List(b,c,d),x),EqQ(Sqr(d),Negate(Power(b,4))))));
IIntegrate(6410,Int(Times(Erf(Times(b_DEFAULT,x_)),Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Dist(C1D2,Int(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erf(Times(b,x))),x),x),Negate(Dist(C1D2,Int(Times(Exp(Plus(Negate(c),Times(CN1,d,Sqr(x)))),Erf(Times(b,x))),x),x))),And(FreeQ(List(b,c,d),x),EqQ(Sqr(d),Power(b,4)))));
IIntegrate(6411,Int(Times(Erfc(Times(b_DEFAULT,x_)),Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Dist(C1D2,Int(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erfc(Times(b,x))),x),x),Negate(Dist(C1D2,Int(Times(Exp(Plus(Negate(c),Times(CN1,d,Sqr(x)))),Erfc(Times(b,x))),x),x))),And(FreeQ(List(b,c,d),x),EqQ(Sqr(d),Power(b,4)))));
IIntegrate(6412,Int(Times(Erfi(Times(b_DEFAULT,x_)),Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Dist(C1D2,Int(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erfi(Times(b,x))),x),x),Negate(Dist(C1D2,Int(Times(Exp(Plus(Negate(c),Times(CN1,d,Sqr(x)))),Erfi(Times(b,x))),x),x))),And(FreeQ(List(b,c,d),x),EqQ(Sqr(d),Power(b,4)))));
IIntegrate(6413,Int(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erf(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Dist(C1D2,Int(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erf(Times(b,x))),x),x),Dist(C1D2,Int(Times(Exp(Plus(Negate(c),Times(CN1,d,Sqr(x)))),Erf(Times(b,x))),x),x)),And(FreeQ(List(b,c,d),x),EqQ(Sqr(d),Power(b,4)))));
IIntegrate(6414,Int(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erfc(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Dist(C1D2,Int(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erfc(Times(b,x))),x),x),Dist(C1D2,Int(Times(Exp(Plus(Negate(c),Times(CN1,d,Sqr(x)))),Erfc(Times(b,x))),x),x)),And(FreeQ(List(b,c,d),x),EqQ(Sqr(d),Power(b,4)))));
IIntegrate(6415,Int(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erfi(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Dist(C1D2,Int(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erfi(Times(b,x))),x),x),Dist(C1D2,Int(Times(Exp(Plus(Negate(c),Times(CN1,d,Sqr(x)))),Erfi(Times(b,x))),x),x)),And(FreeQ(List(b,c,d),x),EqQ(Sqr(d),Power(b,4)))));
IIntegrate(6416,Int($(F_,Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT)),f_DEFAULT)),x_Symbol),
    Condition(Dist(Power(e,-1),Subst(Int(F(Times(f,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),x),x,Plus(d,Times(e,x))),x),And(FreeQ(List(a,b,c,d,e,f,n),x),MemberQ(List(Erf,Erfc,Erfi,FresnelS,FresnelC,ExpIntegralEi,SinIntegral,CosIntegral,SinhIntegral,CoshIntegral),FSymbol))));
IIntegrate(6417,Int(Times(Power(Plus(g_,Times(h_DEFAULT,x_)),m_DEFAULT),$(F_,Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT)),f_DEFAULT))),x_Symbol),
    Condition(Dist(Power(e,-1),Subst(Int(Times(Power(Times(g,x,Power(d,-1)),m),F(Times(f,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x),x,Plus(d,Times(e,x))),x),And(FreeQ(List(a,b,c,d,e,f,g,m,n),x),EqQ(Plus(Times(e,f),Times(CN1,d,g)),C0),MemberQ(List(Erf,Erfc,Erfi,FresnelS,FresnelC,ExpIntegralEi,SinIntegral,CosIntegral,SinhIntegral,CoshIntegral),FSymbol))));
IIntegrate(6418,Int(FresnelS(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Times(Plus(a,Times(b,x)),FresnelS(Plus(a,Times(b,x))),Power(b,-1)),x),Simp(Times(Cos(Times(C1D2,Pi,Sqr(Plus(a,Times(b,x))))),Power(Times(b,Pi),-1)),x)),FreeQ(List(a,b),x)));
IIntegrate(6419,Int(FresnelC(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Times(Plus(a,Times(b,x)),FresnelC(Plus(a,Times(b,x))),Power(b,-1)),x),Negate(Simp(Times(Sin(Times(C1D2,Pi,Sqr(Plus(a,Times(b,x))))),Power(Times(b,Pi),-1)),x))),FreeQ(List(a,b),x)));
IIntegrate(6420,Int(Sqr(FresnelS(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(Plus(a,Times(b,x)),Sqr(FresnelS(Plus(a,Times(b,x)))),Power(b,-1)),x),Negate(Dist(C2,Int(Times(Plus(a,Times(b,x)),Sin(Times(C1D2,Pi,Sqr(Plus(a,Times(b,x))))),FresnelS(Plus(a,Times(b,x)))),x),x))),FreeQ(List(a,b),x)));
IIntegrate(6421,Int(Sqr(FresnelC(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(Plus(a,Times(b,x)),Sqr(FresnelC(Plus(a,Times(b,x)))),Power(b,-1)),x),Negate(Dist(C2,Int(Times(Plus(a,Times(b,x)),Cos(Times(C1D2,Pi,Sqr(Plus(a,Times(b,x))))),FresnelC(Plus(a,Times(b,x)))),x),x))),FreeQ(List(a,b),x)));
IIntegrate(6422,Int(Power(FresnelS(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_),x_Symbol),
    Condition(Unintegrable(Power(FresnelS(Plus(a,Times(b,x))),n),x),And(FreeQ(List(a,b,n),x),NeQ(n,C1),NeQ(n,C2))));
IIntegrate(6423,Int(Power(FresnelC(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_),x_Symbol),
    Condition(Unintegrable(Power(FresnelC(Plus(a,Times(b,x))),n),x),And(FreeQ(List(a,b,n),x),NeQ(n,C1),NeQ(n,C2))));
IIntegrate(6424,Int(Times(FresnelS(Times(b_DEFAULT,x_)),Power(x_,-1)),x_Symbol),
    Condition(Plus(Dist(Times(C1D4,Plus(C1,CI)),Int(Times(Erf(Times(C1D2,Sqrt(Pi),Plus(C1,CI),b,x)),Power(x,-1)),x),x),Dist(Times(C1D4,Plus(C1,Negate(CI))),Int(Times(Erf(Times(C1D2,Sqrt(Pi),Plus(C1,Negate(CI)),b,x)),Power(x,-1)),x),x)),FreeQ(b,x)));
IIntegrate(6425,Int(Times(FresnelC(Times(b_DEFAULT,x_)),Power(x_,-1)),x_Symbol),
    Condition(Plus(Dist(Times(C1D4,Plus(C1,Negate(CI))),Int(Times(Erf(Times(C1D2,Sqrt(Pi),Plus(C1,CI),b,x)),Power(x,-1)),x),x),Dist(Times(C1D4,Plus(C1,CI)),Int(Times(Erf(Times(C1D2,Sqrt(Pi),Plus(C1,Negate(CI)),b,x)),Power(x,-1)),x),x)),FreeQ(b,x)));
IIntegrate(6426,Int(Times(FresnelS(Times(b_DEFAULT,x_)),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(d,x),Plus(m,C1)),FresnelS(Times(b,x)),Power(Times(d,Plus(m,C1)),-1)),x),Negate(Dist(Times(b,Power(Times(d,Plus(m,C1)),-1)),Int(Times(Power(Times(d,x),Plus(m,C1)),Sin(Times(C1D2,Pi,Sqr(b),Sqr(x)))),x),x))),And(FreeQ(List(b,d,m),x),NeQ(m,CN1))));
IIntegrate(6427,Int(Times(FresnelC(Times(b_DEFAULT,x_)),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(d,x),Plus(m,C1)),FresnelC(Times(b,x)),Power(Times(d,Plus(m,C1)),-1)),x),Negate(Dist(Times(b,Power(Times(d,Plus(m,C1)),-1)),Int(Times(Power(Times(d,x),Plus(m,C1)),Cos(Times(C1D2,Pi,Sqr(b),Sqr(x)))),x),x))),And(FreeQ(List(b,d,m),x),NeQ(m,CN1))));
IIntegrate(6428,Int(Times(FresnelS(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),FresnelS(Plus(a,Times(b,x))),Power(Times(d,Plus(m,C1)),-1)),x),Negate(Dist(Times(b,Power(Times(d,Plus(m,C1)),-1)),Int(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Sin(Times(C1D2,Pi,Sqr(Plus(a,Times(b,x)))))),x),x))),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C0))));
IIntegrate(6429,Int(Times(FresnelC(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),FresnelC(Plus(a,Times(b,x))),Power(Times(d,Plus(m,C1)),-1)),x),Negate(Dist(Times(b,Power(Times(d,Plus(m,C1)),-1)),Int(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Cos(Times(C1D2,Pi,Sqr(Plus(a,Times(b,x)))))),x),x))),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C0))));
IIntegrate(6430,Int(Times(Sqr(FresnelS(Times(b_DEFAULT,x_))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Sqr(FresnelS(Times(b,x))),Power(Plus(m,C1),-1)),x),Negate(Dist(Times(C2,b,Power(Plus(m,C1),-1)),Int(Times(Power(x,Plus(m,C1)),Sin(Times(C1D2,Pi,Sqr(b),Sqr(x))),FresnelS(Times(b,x))),x),x))),And(FreeQ(b,x),IntegerQ(m),NeQ(m,CN1))));
IIntegrate(6431,Int(Times(Sqr(FresnelC(Times(b_DEFAULT,x_))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Sqr(FresnelC(Times(b,x))),Power(Plus(m,C1),-1)),x),Negate(Dist(Times(C2,b,Power(Plus(m,C1),-1)),Int(Times(Power(x,Plus(m,C1)),Cos(Times(C1D2,Pi,Sqr(b),Sqr(x))),FresnelC(Times(b,x))),x),x))),And(FreeQ(b,x),IntegerQ(m),NeQ(m,CN1))));
IIntegrate(6432,Int(Times(Sqr(FresnelS(Plus(a_,Times(b_DEFAULT,x_)))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Power(b,Plus(m,C1)),-1),Subst(Int(ExpandIntegrand(Sqr(FresnelS(x)),Power(Plus(Times(b,c),Times(CN1,a,d),Times(d,x)),m),x),x),x,Plus(a,Times(b,x))),x),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C0))));
IIntegrate(6433,Int(Times(Sqr(FresnelC(Plus(a_,Times(b_DEFAULT,x_)))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Power(b,Plus(m,C1)),-1),Subst(Int(ExpandIntegrand(Sqr(FresnelC(x)),Power(Plus(Times(b,c),Times(CN1,a,d),Times(d,x)),m),x),x),x,Plus(a,Times(b,x))),x),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C0))));
IIntegrate(6434,Int(Times(Power(FresnelS(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(c,Times(d,x)),m),Power(FresnelS(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,m,n),x)));
IIntegrate(6435,Int(Times(Power(FresnelC(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(c,Times(d,x)),m),Power(FresnelC(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,m,n),x)));
IIntegrate(6436,Int(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),FresnelS(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Dist(Times(C1D4,Plus(C1,CI)),Int(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erf(Times(C1D2,Sqrt(Pi),Plus(C1,CI),b,x))),x),x),Dist(Times(C1D4,Plus(C1,Negate(CI))),Int(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erf(Times(C1D2,Sqrt(Pi),Plus(C1,Negate(CI)),b,x))),x),x)),And(FreeQ(List(b,c,d),x),EqQ(Sqr(d),Times(CN1,C1D4,Sqr(Pi),Power(b,4))))));
IIntegrate(6437,Int(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),FresnelC(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Dist(Times(C1D4,Plus(C1,Negate(CI))),Int(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erf(Times(C1D2,Sqrt(Pi),Plus(C1,CI),b,x))),x),x),Dist(Times(C1D4,Plus(C1,CI)),Int(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erf(Times(C1D2,Sqrt(Pi),Plus(C1,Negate(CI)),b,x))),x),x)),And(FreeQ(List(b,c,d),x),EqQ(Sqr(d),Times(CN1,C1D4,Sqr(Pi),Power(b,4))))));
IIntegrate(6438,Int(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(FresnelS(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Exp(Plus(c,Times(d,Sqr(x)))),Power(FresnelS(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,n),x)));
IIntegrate(6439,Int(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(FresnelC(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Exp(Plus(c,Times(d,Sqr(x)))),Power(FresnelC(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,n),x)));
IIntegrate(6440,Int(Times(Power(FresnelS(Times(b_DEFAULT,x_)),n_DEFAULT),Sin(Times(d_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Dist(Times(Pi,b,Power(Times(C2,d),-1)),Subst(Int(Power(x,n),x),x,FresnelS(Times(b,x))),x),And(FreeQ(List(b,d,n),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,4))))));
IIntegrate(6441,Int(Times(Cos(Times(d_DEFAULT,Sqr(x_))),Power(FresnelC(Times(b_DEFAULT,x_)),n_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Pi,b,Power(Times(C2,d),-1)),Subst(Int(Power(x,n),x),x,FresnelC(Times(b,x))),x),And(FreeQ(List(b,d,n),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,4))))));
IIntegrate(6442,Int(Times(FresnelS(Times(b_DEFAULT,x_)),Sin(Plus(c_,Times(d_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Dist(Sin(c),Int(Times(Cos(Times(d,Sqr(x))),FresnelS(Times(b,x))),x),x),Dist(Cos(c),Int(Times(Sin(Times(d,Sqr(x))),FresnelS(Times(b,x))),x),x)),And(FreeQ(List(b,c,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,4))))));
IIntegrate(6443,Int(Times(Cos(Plus(c_,Times(d_DEFAULT,Sqr(x_)))),FresnelC(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Dist(Cos(c),Int(Times(Cos(Times(d,Sqr(x))),FresnelC(Times(b,x))),x),x),Negate(Dist(Sin(c),Int(Times(Sin(Times(d,Sqr(x))),FresnelC(Times(b,x))),x),x))),And(FreeQ(List(b,c,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,4))))));
IIntegrate(6444,Int(Times(Power(FresnelS(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Unintegrable(Times(Power(FresnelS(Plus(a,Times(b,x))),n),Sin(Plus(c,Times(d,Sqr(x))))),x),FreeQ(List(a,b,c,d,n),x)));
IIntegrate(6445,Int(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(FresnelC(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Cos(Plus(c,Times(d,Sqr(x)))),Power(FresnelC(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,n),x)));
IIntegrate(6446,Int(Times(Cos(Times(d_DEFAULT,Sqr(x_))),FresnelS(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Times(FresnelC(Times(b,x)),FresnelS(Times(b,x)),Power(Times(C2,b),-1)),x),Negate(Simp(Times(QQ(1L,8L),C1,CI,b,Sqr(x),HypergeometricPFQ(List(C1,C1),List(QQ(3L,2L),C2),Times(CN1,C1D2,CI,Sqr(b),Pi,Sqr(x)))),x)),Simp(Times(QQ(1L,8L),C1,CI,b,Sqr(x),HypergeometricPFQ(List(C1,C1),List(QQ(3L,2L),C2),Times(C1D2,C1,CI,Sqr(b),Pi,Sqr(x)))),x)),And(FreeQ(List(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,4))))));
IIntegrate(6447,Int(Times(FresnelC(Times(b_DEFAULT,x_)),Sin(Times(d_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Plus(Simp(Times(b,Pi,FresnelC(Times(b,x)),FresnelS(Times(b,x)),Power(Times(C4,d),-1)),x),Simp(Times(QQ(1L,8L),C1,CI,b,Sqr(x),HypergeometricPFQ(List(C1,C1),List(QQ(3L,2L),C2),Times(CN1,CI,d,Sqr(x)))),x),Negate(Simp(Times(QQ(1L,8L),C1,CI,b,Sqr(x),HypergeometricPFQ(List(C1,C1),List(QQ(3L,2L),C2),Times(CI,d,Sqr(x)))),x))),And(FreeQ(List(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,4))))));
IIntegrate(6448,Int(Times(Cos(Plus(c_,Times(d_DEFAULT,Sqr(x_)))),FresnelS(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Dist(Cos(c),Int(Times(Cos(Times(d,Sqr(x))),FresnelS(Times(b,x))),x),x),Negate(Dist(Sin(c),Int(Times(Sin(Times(d,Sqr(x))),FresnelS(Times(b,x))),x),x))),And(FreeQ(List(b,c,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,4))))));
IIntegrate(6449,Int(Times(FresnelC(Times(b_DEFAULT,x_)),Sin(Plus(c_,Times(d_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Dist(Sin(c),Int(Times(Cos(Times(d,Sqr(x))),FresnelC(Times(b,x))),x),x),Dist(Cos(c),Int(Times(Sin(Times(d,Sqr(x))),FresnelC(Times(b,x))),x),x)),And(FreeQ(List(b,c,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,4))))));
IIntegrate(6450,Int(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(FresnelS(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Cos(Plus(c,Times(d,Sqr(x)))),Power(FresnelS(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,n),x)));
  }
}
}
