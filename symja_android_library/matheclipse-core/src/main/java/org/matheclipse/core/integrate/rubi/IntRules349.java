package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IAST;
import com.google.common.base.Supplier;

/** 
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class IntRules349 { 
  public static IAST RULES = List( 
IIntegrate(6981,Integrate(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erfi(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Dist(C1D2,Integrate(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erfi(Times(b,x))),x),x),x),Simp(Dist(C1D2,Integrate(Times(Exp(Subtract(Negate(c),Times(d,Sqr(x)))),Erfi(Times(b,x))),x),x),x)),And(FreeQ(list(b,c,d),x),EqQ(Sqr(d),Power(b,C4))))),
IIntegrate(6982,Integrate($(F_,Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT)),f_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(e,CN1),Subst(Integrate(F(Times(f,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),x),x,Plus(d,Times(e,x))),x),x),And(FreeQ(List(a,b,c,d,e,f,n),x),MemberQ(List(Erf,Erfc,Erfi,FresnelS,FresnelC,ExpIntegralEi,SinIntegral,CosIntegral,SinhIntegral,CoshIntegral),FSymbol)))),
IIntegrate(6983,Integrate(Times(Power(Plus(g_,Times(h_DEFAULT,x_)),m_DEFAULT),$(F_,Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT)),f_DEFAULT))),x_Symbol),
    Condition(Simp(Dist(Power(e,CN1),Subst(Integrate(Times(Power(Times(g,x,Power(d,CN1)),m),F(Times(f,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x),x,Plus(d,Times(e,x))),x),x),And(FreeQ(List(a,b,c,d,e,f,g,m,n),x),EqQ(Subtract(Times(e,f),Times(d,g)),C0),MemberQ(List(Erf,Erfc,Erfi,FresnelS,FresnelC,ExpIntegralEi,SinIntegral,CosIntegral,SinhIntegral,CoshIntegral),FSymbol)))),
IIntegrate(6984,Integrate(FresnelS(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Times(Plus(a,Times(b,x)),FresnelS(Plus(a,Times(b,x))),Power(b,CN1)),x),Simp(Times(Cos(Times(C1D2,Pi,Sqr(Plus(a,Times(b,x))))),Power(Times(b,Pi),CN1)),x)),FreeQ(list(a,b),x))),
IIntegrate(6985,Integrate(FresnelC(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),FresnelC(Plus(a,Times(b,x))),Power(b,CN1)),x),Simp(Times(Sin(Times(C1D2,Pi,Sqr(Plus(a,Times(b,x))))),Power(Times(b,Pi),CN1)),x)),FreeQ(list(a,b),x))),
IIntegrate(6986,Integrate(Sqr(FresnelS(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),Sqr(FresnelS(Plus(a,Times(b,x)))),Power(b,CN1)),x),Simp(Dist(C2,Integrate(Times(Plus(a,Times(b,x)),Sin(Times(C1D2,Pi,Sqr(Plus(a,Times(b,x))))),FresnelS(Plus(a,Times(b,x)))),x),x),x)),FreeQ(list(a,b),x))),
IIntegrate(6987,Integrate(Sqr(FresnelC(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),Sqr(FresnelC(Plus(a,Times(b,x)))),Power(b,CN1)),x),Simp(Dist(C2,Integrate(Times(Plus(a,Times(b,x)),Cos(Times(C1D2,Pi,Sqr(Plus(a,Times(b,x))))),FresnelC(Plus(a,Times(b,x)))),x),x),x)),FreeQ(list(a,b),x))),
IIntegrate(6988,Integrate(Power(FresnelS(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_),x_Symbol),
    Condition(Unintegrable(Power(FresnelS(Plus(a,Times(b,x))),n),x),And(FreeQ(list(a,b,n),x),NeQ(n,C1),NeQ(n,C2)))),
IIntegrate(6989,Integrate(Power(FresnelC(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_),x_Symbol),
    Condition(Unintegrable(Power(FresnelC(Plus(a,Times(b,x))),n),x),And(FreeQ(list(a,b,n),x),NeQ(n,C1),NeQ(n,C2)))),
IIntegrate(6990,Integrate(Times(FresnelS(Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Simp(Dist(CC(1L,4L,1L,4L),Integrate(Times(Erf(Times(C1D2,CSqrtPi,CC(1L,1L,1L,1L),b,x)),Power(x,CN1)),x),x),x),Simp(Dist(CC(1L,4L,-1L,4L),Integrate(Times(Erf(Times(C1D2,CSqrtPi,CC(1L,1L,-1L,1L),b,x)),Power(x,CN1)),x),x),x)),FreeQ(b,x))),
IIntegrate(6991,Integrate(Times(FresnelC(Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Simp(Dist(CC(1L,4L,-1L,4L),Integrate(Times(Erf(Times(C1D2,CSqrtPi,CC(1L,1L,1L,1L),b,x)),Power(x,CN1)),x),x),x),Simp(Dist(CC(1L,4L,1L,4L),Integrate(Times(Erf(Times(C1D2,CSqrtPi,CC(1L,1L,-1L,1L),b,x)),Power(x,CN1)),x),x),x)),FreeQ(b,x))),
IIntegrate(6992,Integrate(Times(FresnelS(Times(b_DEFAULT,x_)),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(d,x),Plus(m,C1)),FresnelS(Times(b,x)),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(d,x),Plus(m,C1)),Sin(Times(C1D2,Pi,Sqr(b),Sqr(x)))),x),x),x)),And(FreeQ(list(b,d,m),x),NeQ(m,CN1)))),
IIntegrate(6993,Integrate(Times(FresnelC(Times(b_DEFAULT,x_)),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(d,x),Plus(m,C1)),FresnelC(Times(b,x)),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(d,x),Plus(m,C1)),Cos(Times(C1D2,Pi,Sqr(b),Sqr(x)))),x),x),x)),And(FreeQ(list(b,d,m),x),NeQ(m,CN1)))),
IIntegrate(6994,Integrate(Times(FresnelS(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),FresnelS(Plus(a,Times(b,x))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Sin(Times(C1D2,Pi,Sqr(Plus(a,Times(b,x)))))),x),x),x)),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C0)))),
IIntegrate(6995,Integrate(Times(FresnelC(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),FresnelC(Plus(a,Times(b,x))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Cos(Times(C1D2,Pi,Sqr(Plus(a,Times(b,x)))))),x),x),x)),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C0)))),
IIntegrate(6996,Integrate(Times(Sqr(FresnelS(Times(b_DEFAULT,x_))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Sqr(FresnelS(Times(b,x))),Power(Plus(m,C1),CN1)),x),Simp(Dist(Times(C2,b,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Sin(Times(C1D2,Pi,Sqr(b),Sqr(x))),FresnelS(Times(b,x))),x),x),x)),And(FreeQ(b,x),IntegerQ(m),NeQ(m,CN1)))),
IIntegrate(6997,Integrate(Times(Sqr(FresnelC(Times(b_DEFAULT,x_))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Sqr(FresnelC(Times(b,x))),Power(Plus(m,C1),CN1)),x),Simp(Dist(Times(C2,b,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Cos(Times(C1D2,Pi,Sqr(b),Sqr(x))),FresnelC(Times(b,x))),x),x),x)),And(FreeQ(b,x),IntegerQ(m),NeQ(m,CN1)))),
IIntegrate(6998,Integrate(Times(Sqr(FresnelS(Plus(a_,Times(b_DEFAULT,x_)))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(Power(b,Plus(m,C1)),CN1),Subst(Integrate(ExpandIntegrand(Sqr(FresnelS(x)),Power(Plus(Times(b,c),Times(CN1,a,d),Times(d,x)),m),x),x),x,Plus(a,Times(b,x))),x),x),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C0)))),
IIntegrate(6999,Integrate(Times(Sqr(FresnelC(Plus(a_,Times(b_DEFAULT,x_)))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(Power(b,Plus(m,C1)),CN1),Subst(Integrate(ExpandIntegrand(Sqr(FresnelC(x)),Power(Plus(Times(b,c),Times(CN1,a,d),Times(d,x)),m),x),x),x,Plus(a,Times(b,x))),x),x),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C0)))),
IIntegrate(7000,Integrate(Times(Power(FresnelS(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(c,Times(d,x)),m),Power(FresnelS(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,m,n),x)))
  );
}
