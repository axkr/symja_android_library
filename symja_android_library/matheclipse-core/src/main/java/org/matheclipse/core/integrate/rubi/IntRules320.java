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
class IntRules320 { 
  public static IAST RULES = List( 
IIntegrate(6401,Integrate(Times(Erf(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(e,x),Plus(m,C1)),Erf(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(e,Plus(m,C1)),CN1)),x),Dist(Times(C2,b,d,n,Power(Times(CSqrtPi,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(e,x),m),Power(Exp(Sqr(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,m,n),x),NeQ(m,CN1)))),
IIntegrate(6402,Integrate(Times(Erfc(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(e,x),Plus(m,C1)),Erfc(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(e,Plus(m,C1)),CN1)),x),Dist(Times(C2,b,d,n,Power(Times(CSqrtPi,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(e,x),m),Power(Exp(Sqr(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,m,n),x),NeQ(m,CN1)))),
IIntegrate(6403,Integrate(Times(Erfi(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(e,x),Plus(m,C1)),Erfi(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(e,Plus(m,C1)),CN1)),x),Dist(Times(C2,b,d,n,Power(Times(CSqrtPi,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(e,x),m),Exp(Sqr(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))))),x),x)),And(FreeQ(List(a,b,c,d,e,m,n),x),NeQ(m,CN1)))),
IIntegrate(6404,Integrate(Times(Erf(Times(b_DEFAULT,x_)),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Subtract(Dist(Times(C1D2,CI),Integrate(Times(Exp(Subtract(Times(CN1,CI,c),Times(CI,d,Sqr(x)))),Erf(Times(b,x))),x),x),Dist(Times(C1D2,CI),Integrate(Times(Exp(Plus(Times(CI,c),Times(CI,d,Sqr(x)))),Erf(Times(b,x))),x),x)),And(FreeQ(list(b,c,d),x),EqQ(Sqr(d),Negate(Power(b,C4)))))),
IIntegrate(6405,Integrate(Times(Erfc(Times(b_DEFAULT,x_)),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Subtract(Dist(Times(C1D2,CI),Integrate(Times(Exp(Subtract(Times(CN1,CI,c),Times(CI,d,Sqr(x)))),Erfc(Times(b,x))),x),x),Dist(Times(C1D2,CI),Integrate(Times(Exp(Plus(Times(CI,c),Times(CI,d,Sqr(x)))),Erfc(Times(b,x))),x),x)),And(FreeQ(list(b,c,d),x),EqQ(Sqr(d),Negate(Power(b,C4)))))),
IIntegrate(6406,Integrate(Times(Erfi(Times(b_DEFAULT,x_)),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Subtract(Dist(Times(C1D2,CI),Integrate(Times(Exp(Subtract(Times(CN1,CI,c),Times(CI,d,Sqr(x)))),Erfi(Times(b,x))),x),x),Dist(Times(C1D2,CI),Integrate(Times(Exp(Plus(Times(CI,c),Times(CI,d,Sqr(x)))),Erfi(Times(b,x))),x),x)),And(FreeQ(list(b,c,d),x),EqQ(Sqr(d),Negate(Power(b,C4)))))),
IIntegrate(6407,Integrate(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erf(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Dist(C1D2,Integrate(Times(Exp(Subtract(Times(CN1,CI,c),Times(CI,d,Sqr(x)))),Erf(Times(b,x))),x),x),Dist(C1D2,Integrate(Times(Exp(Plus(Times(CI,c),Times(CI,d,Sqr(x)))),Erf(Times(b,x))),x),x)),And(FreeQ(list(b,c,d),x),EqQ(Sqr(d),Negate(Power(b,C4)))))),
IIntegrate(6408,Integrate(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erfc(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Dist(C1D2,Integrate(Times(Exp(Subtract(Times(CN1,CI,c),Times(CI,d,Sqr(x)))),Erfc(Times(b,x))),x),x),Dist(C1D2,Integrate(Times(Exp(Plus(Times(CI,c),Times(CI,d,Sqr(x)))),Erfc(Times(b,x))),x),x)),And(FreeQ(list(b,c,d),x),EqQ(Sqr(d),Negate(Power(b,C4)))))),
IIntegrate(6409,Integrate(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erfi(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Dist(C1D2,Integrate(Times(Exp(Subtract(Times(CN1,CI,c),Times(CI,d,Sqr(x)))),Erfi(Times(b,x))),x),x),Dist(C1D2,Integrate(Times(Exp(Plus(Times(CI,c),Times(CI,d,Sqr(x)))),Erfi(Times(b,x))),x),x)),And(FreeQ(list(b,c,d),x),EqQ(Sqr(d),Negate(Power(b,C4)))))),
IIntegrate(6410,Integrate(Times(Erf(Times(b_DEFAULT,x_)),Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Subtract(Dist(C1D2,Integrate(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erf(Times(b,x))),x),x),Dist(C1D2,Integrate(Times(Exp(Subtract(Negate(c),Times(d,Sqr(x)))),Erf(Times(b,x))),x),x)),And(FreeQ(list(b,c,d),x),EqQ(Sqr(d),Power(b,C4))))),
IIntegrate(6411,Integrate(Times(Erfc(Times(b_DEFAULT,x_)),Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Subtract(Dist(C1D2,Integrate(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erfc(Times(b,x))),x),x),Dist(C1D2,Integrate(Times(Exp(Subtract(Negate(c),Times(d,Sqr(x)))),Erfc(Times(b,x))),x),x)),And(FreeQ(list(b,c,d),x),EqQ(Sqr(d),Power(b,C4))))),
IIntegrate(6412,Integrate(Times(Erfi(Times(b_DEFAULT,x_)),Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Subtract(Dist(C1D2,Integrate(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erfi(Times(b,x))),x),x),Dist(C1D2,Integrate(Times(Exp(Subtract(Negate(c),Times(d,Sqr(x)))),Erfi(Times(b,x))),x),x)),And(FreeQ(list(b,c,d),x),EqQ(Sqr(d),Power(b,C4))))),
IIntegrate(6413,Integrate(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erf(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Dist(C1D2,Integrate(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erf(Times(b,x))),x),x),Dist(C1D2,Integrate(Times(Exp(Subtract(Negate(c),Times(d,Sqr(x)))),Erf(Times(b,x))),x),x)),And(FreeQ(list(b,c,d),x),EqQ(Sqr(d),Power(b,C4))))),
IIntegrate(6414,Integrate(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erfc(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Dist(C1D2,Integrate(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erfc(Times(b,x))),x),x),Dist(C1D2,Integrate(Times(Exp(Subtract(Negate(c),Times(d,Sqr(x)))),Erfc(Times(b,x))),x),x)),And(FreeQ(list(b,c,d),x),EqQ(Sqr(d),Power(b,C4))))),
IIntegrate(6415,Integrate(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erfi(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Dist(C1D2,Integrate(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erfi(Times(b,x))),x),x),Dist(C1D2,Integrate(Times(Exp(Subtract(Negate(c),Times(d,Sqr(x)))),Erfi(Times(b,x))),x),x)),And(FreeQ(list(b,c,d),x),EqQ(Sqr(d),Power(b,C4))))),
IIntegrate(6416,Integrate($(F_,Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT)),f_DEFAULT)),x_Symbol),
    Condition(Dist(Power(e,CN1),Subst(Integrate(F(Times(f,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),x),x,Plus(d,Times(e,x))),x),And(FreeQ(List(a,b,c,d,e,f,n),x),MemberQ(List(Erf,Erfc,Erfi,FresnelS,FresnelC,ExpIntegralEi,SinIntegral,CosIntegral,SinhIntegral,CoshIntegral),FSymbol)))),
IIntegrate(6417,Integrate(Times(Power(Plus(g_,Times(h_DEFAULT,x_)),m_DEFAULT),$(F_,Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT)),f_DEFAULT))),x_Symbol),
    Condition(Dist(Power(e,CN1),Subst(Integrate(Times(Power(Times(g,x,Power(d,CN1)),m),F(Times(f,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x),x,Plus(d,Times(e,x))),x),And(FreeQ(List(a,b,c,d,e,f,g,m,n),x),EqQ(Subtract(Times(e,f),Times(d,g)),C0),MemberQ(List(Erf,Erfc,Erfi,FresnelS,FresnelC,ExpIntegralEi,SinIntegral,CosIntegral,SinhIntegral,CoshIntegral),FSymbol)))),
IIntegrate(6418,Integrate(FresnelS(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Times(Plus(a,Times(b,x)),FresnelS(Plus(a,Times(b,x))),Power(b,CN1)),x),Simp(Times(Cos(Times(C1D2,Pi,Sqr(Plus(a,Times(b,x))))),Power(Times(b,Pi),CN1)),x)),FreeQ(list(a,b),x))),
IIntegrate(6419,Integrate(FresnelC(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),FresnelC(Plus(a,Times(b,x))),Power(b,CN1)),x),Simp(Times(Sin(Times(C1D2,Pi,Sqr(Plus(a,Times(b,x))))),Power(Times(b,Pi),CN1)),x)),FreeQ(list(a,b),x))),
IIntegrate(6420,Integrate(Sqr(FresnelS(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),Sqr(FresnelS(Plus(a,Times(b,x)))),Power(b,CN1)),x),Dist(C2,Integrate(Times(Plus(a,Times(b,x)),Sin(Times(C1D2,Pi,Sqr(Plus(a,Times(b,x))))),FresnelS(Plus(a,Times(b,x)))),x),x)),FreeQ(list(a,b),x)))
  );
}
