package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class IntRules348 { 
  public static IAST RULES = List( 
IIntegrate(6961,Integrate(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erf(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Star(C1D2,Integrate(Times(Exp(Subtract(Times(CN1,CI,c),Times(CI,d,Sqr(x)))),Erf(Times(b,x))),x)),x),Simp(Star(C1D2,Integrate(Times(Exp(Plus(Times(CI,c),Times(CI,d,Sqr(x)))),Erf(Times(b,x))),x)),x)),And(FreeQ(list(b,c,d),x),EqQ(Sqr(d),Negate(Power(b,C4)))))),
IIntegrate(6962,Integrate(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erfc(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Star(C1D2,Integrate(Times(Exp(Subtract(Times(CN1,CI,c),Times(CI,d,Sqr(x)))),Erfc(Times(b,x))),x)),x),Simp(Star(C1D2,Integrate(Times(Exp(Plus(Times(CI,c),Times(CI,d,Sqr(x)))),Erfc(Times(b,x))),x)),x)),And(FreeQ(list(b,c,d),x),EqQ(Sqr(d),Negate(Power(b,C4)))))),
IIntegrate(6963,Integrate(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erfi(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Star(C1D2,Integrate(Times(Exp(Subtract(Times(CN1,CI,c),Times(CI,d,Sqr(x)))),Erfi(Times(b,x))),x)),x),Simp(Star(C1D2,Integrate(Times(Exp(Plus(Times(CI,c),Times(CI,d,Sqr(x)))),Erfi(Times(b,x))),x)),x)),And(FreeQ(list(b,c,d),x),EqQ(Sqr(d),Negate(Power(b,C4)))))),
IIntegrate(6964,Integrate(Times(Erf(Times(b_DEFAULT,x_)),Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Subtract(Simp(Star(C1D2,Integrate(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erf(Times(b,x))),x)),x),Simp(Star(C1D2,Integrate(Times(Exp(Subtract(Negate(c),Times(d,Sqr(x)))),Erf(Times(b,x))),x)),x)),And(FreeQ(list(b,c,d),x),EqQ(Sqr(d),Power(b,C4))))),
IIntegrate(6965,Integrate(Times(Erfc(Times(b_DEFAULT,x_)),Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Subtract(Simp(Star(C1D2,Integrate(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erfc(Times(b,x))),x)),x),Simp(Star(C1D2,Integrate(Times(Exp(Subtract(Negate(c),Times(d,Sqr(x)))),Erfc(Times(b,x))),x)),x)),And(FreeQ(list(b,c,d),x),EqQ(Sqr(d),Power(b,C4))))),
IIntegrate(6966,Integrate(Times(Erfi(Times(b_DEFAULT,x_)),Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Subtract(Simp(Star(C1D2,Integrate(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erfi(Times(b,x))),x)),x),Simp(Star(C1D2,Integrate(Times(Exp(Subtract(Negate(c),Times(d,Sqr(x)))),Erfi(Times(b,x))),x)),x)),And(FreeQ(list(b,c,d),x),EqQ(Sqr(d),Power(b,C4))))),
IIntegrate(6967,Integrate(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erf(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Star(C1D2,Integrate(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erf(Times(b,x))),x)),x),Simp(Star(C1D2,Integrate(Times(Exp(Subtract(Negate(c),Times(d,Sqr(x)))),Erf(Times(b,x))),x)),x)),And(FreeQ(list(b,c,d),x),EqQ(Sqr(d),Power(b,C4))))),
IIntegrate(6968,Integrate(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erfc(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Star(C1D2,Integrate(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erfc(Times(b,x))),x)),x),Simp(Star(C1D2,Integrate(Times(Exp(Subtract(Negate(c),Times(d,Sqr(x)))),Erfc(Times(b,x))),x)),x)),And(FreeQ(list(b,c,d),x),EqQ(Sqr(d),Power(b,C4))))),
IIntegrate(6969,Integrate(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erfi(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Star(C1D2,Integrate(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erfi(Times(b,x))),x)),x),Simp(Star(C1D2,Integrate(Times(Exp(Subtract(Negate(c),Times(d,Sqr(x)))),Erfi(Times(b,x))),x)),x)),And(FreeQ(list(b,c,d),x),EqQ(Sqr(d),Power(b,C4))))),
IIntegrate(6970,Integrate($(F_,Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT)),f_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(e,CN1),Subst(Integrate(F(Times(f,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),x),x,Plus(d,Times(e,x)))),x),And(FreeQ(List(a,b,c,d,e,f,n),x),MemberQ(List(Erf,Erfc,Erfi,FresnelS,FresnelC,ExpIntegralEi,SinIntegral,CosIntegral,SinhIntegral,CoshIntegral),FSymbol)))),
IIntegrate(6971,Integrate(Times(Power(Plus(g_,Times(h_DEFAULT,x_)),m_DEFAULT),$(F_,Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT)),f_DEFAULT))),x_Symbol),
    Condition(Simp(Star(Power(e,CN1),Subst(Integrate(Times(Power(Times(g,x,Power(d,CN1)),m),F(Times(f,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x),x,Plus(d,Times(e,x)))),x),And(FreeQ(List(a,b,c,d,e,f,g,m,n),x),EqQ(Subtract(Times(e,f),Times(d,g)),C0),MemberQ(List(Erf,Erfc,Erfi,FresnelS,FresnelC,ExpIntegralEi,SinIntegral,CosIntegral,SinhIntegral,CoshIntegral),FSymbol)))),
IIntegrate(6972,Integrate(FresnelS(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Times(Plus(a,Times(b,x)),FresnelS(Plus(a,Times(b,x))),Power(b,CN1)),x),Simp(Times(Cos(Times(C1D2,Pi,Sqr(Plus(a,Times(b,x))))),Power(Times(b,Pi),CN1)),x)),FreeQ(list(a,b),x))),
IIntegrate(6973,Integrate(FresnelC(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),FresnelC(Plus(a,Times(b,x))),Power(b,CN1)),x),Simp(Times(Sin(Times(C1D2,Pi,Sqr(Plus(a,Times(b,x))))),Power(Times(b,Pi),CN1)),x)),FreeQ(list(a,b),x))),
IIntegrate(6974,Integrate(Sqr(FresnelS(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),Sqr(FresnelS(Plus(a,Times(b,x)))),Power(b,CN1)),x),Simp(Star(C2,Integrate(Times(Plus(a,Times(b,x)),Sin(Times(C1D2,Pi,Sqr(Plus(a,Times(b,x))))),FresnelS(Plus(a,Times(b,x)))),x)),x)),FreeQ(list(a,b),x))),
IIntegrate(6975,Integrate(Sqr(FresnelC(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),Sqr(FresnelC(Plus(a,Times(b,x)))),Power(b,CN1)),x),Simp(Star(C2,Integrate(Times(Plus(a,Times(b,x)),Cos(Times(C1D2,Pi,Sqr(Plus(a,Times(b,x))))),FresnelC(Plus(a,Times(b,x)))),x)),x)),FreeQ(list(a,b),x))),
IIntegrate(6976,Integrate(Power(FresnelS(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_),x_Symbol),
    Condition(Unintegrable(Power(FresnelS(Plus(a,Times(b,x))),n),x),And(FreeQ(list(a,b,n),x),NeQ(n,C1),NeQ(n,C2)))),
IIntegrate(6977,Integrate(Power(FresnelC(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_),x_Symbol),
    Condition(Unintegrable(Power(FresnelC(Plus(a,Times(b,x))),n),x),And(FreeQ(list(a,b,n),x),NeQ(n,C1),NeQ(n,C2)))),
IIntegrate(6978,Integrate(Times(FresnelS(Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Simp(Star(Times(C1D4,Plus(C1,CI)),Integrate(Times(Erf(Times(C1D2,CSqrtPi,Plus(C1,CI),b,x)),Power(x,CN1)),x)),x),Simp(Star(Times(C1D4,Subtract(C1,CI)),Integrate(Times(Erf(Times(C1D2,CSqrtPi,Subtract(C1,CI),b,x)),Power(x,CN1)),x)),x)),FreeQ(b,x))),
IIntegrate(6979,Integrate(Times(FresnelC(Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Simp(Star(Times(C1D4,Subtract(C1,CI)),Integrate(Times(Erf(Times(C1D2,CSqrtPi,Plus(C1,CI),b,x)),Power(x,CN1)),x)),x),Simp(Star(Times(C1D4,Plus(C1,CI)),Integrate(Times(Erf(Times(C1D2,CSqrtPi,Subtract(C1,CI),b,x)),Power(x,CN1)),x)),x)),FreeQ(b,x))),
IIntegrate(6980,Integrate(Times(FresnelS(Times(b_DEFAULT,x_)),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(d,x),Plus(m,C1)),FresnelS(Times(b,x)),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(d,x),Plus(m,C1)),Sin(Times(C1D2,Pi,Sqr(b),Sqr(x)))),x)),x)),And(FreeQ(list(b,d,m),x),NeQ(m,CN1))))
  );
}
