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
class IntRules313 { 
  public static IAST RULES = List( 
IIntegrate(6261,Integrate(Times(Log(Times(h_DEFAULT,Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),m_DEFAULT))),Power(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Subtract(Simp(Times(Log(Times(h,Power(Plus(f,Times(g,x)),m))),Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),Plus(n,C1)),Power(Times(b,c,Sqrt(d),Plus(n,C1)),CN1)),x),Simp(Star(Times(g,m,Power(Times(b,c,Sqrt(d),Plus(n,C1)),CN1)),Integrate(Times(Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),Plus(n,C1)),Power(Plus(f,Times(g,x)),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,f,g,h,m),x),EqQ(e,Times(Sqr(c),d)),GtQ(d,C0),IGtQ(n,C0)))),
IIntegrate(6262,Integrate(Times(Log(Times(h_DEFAULT,Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),m_DEFAULT))),Power(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Star(Simp(Times(Power(Plus(d,Times(e,Sqr(x))),p),Power(Power(Plus(C1,Times(Sqr(c),Sqr(x))),p),CN1))),Integrate(Times(Log(Times(h,Power(Plus(f,Times(g,x)),m))),Power(Plus(C1,Times(Sqr(c),Sqr(x))),p),Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),n)),x)),x),And(FreeQ(List(a,b,c,d,e,f,g,h,m,n),x),EqQ(e,Times(Sqr(c),d)),IntegerQ(Subtract(p,C1D2)),Not(GtQ(d,C0))))),
IIntegrate(6263,Integrate(Times(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,x_)),m_),Power(Plus(f_,Times(g_DEFAULT,x_)),m_)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times(Power(Plus(d,Times(e,x)),m),Power(Plus(f,Times(g,x)),m)),x))),Subtract(Simp(Star(Plus(a,Times(b,ArcSinh(Times(c,x)))),u),x),Simp(Star(Times(b,c),Integrate(Star(Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1D2),u),x)),x))),And(FreeQ(List(a,b,c,d,e,f,g),x),ILtQ(Plus(m,C1D2),C0)))),
IIntegrate(6264,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,x_)),m_DEFAULT),Power(Plus(f_,Times(g_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),n),Times(Power(Plus(d,Times(e,x)),m),Power(Plus(f,Times(g,x)),m)),x),x),And(FreeQ(List(a,b,c,d,e,f,g,n),x),IntegerQ(m)))),
IIntegrate(6265,Integrate(Times(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),u_),x_Symbol),
    Condition(With(list(Set(v,IntHide(u,x))),Condition(Subtract(Simp(Star(Plus(a,Times(b,ArcSinh(Times(c,x)))),v),x),Simp(Star(Times(b,c),Integrate(SimplifyIntegrand(Times(v,Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x)),x)),InverseFunctionFreeQ(v,x))),FreeQ(list(a,b,c),x))),
IIntegrate(6266,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),$p("§px"),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(With(list(Set(u,ExpandIntegrand(Times($s("§px"),Power(Plus(d,Times(e,Sqr(x))),p),Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),n)),x))),Condition(Integrate(u,x),SumQ(u))),And(FreeQ(List(a,b,c,d,e,n),x),PolynomialQ($s("§px"),x),EqQ(e,Times(Sqr(c),d)),IntegerQ(Subtract(p,C1D2))))),
IIntegrate(6267,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),$p("§px",true),Power(Plus(f_,Times(g_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_))),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,ExpandIntegrand(Times($s("§px"),Power(Plus(f,Times(g,Power(Plus(d,Times(e,Sqr(x))),p))),m),Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),n)),x))),Condition(Integrate(u,x),SumQ(u))),And(FreeQ(List(a,b,c,d,e,f,g),x),PolynomialQ($s("§px"),x),EqQ(e,Times(Sqr(c),d)),IGtQ(Plus(p,C1D2),C0),IntegersQ(m,n)))),
IIntegrate(6268,Integrate(Times(Power(ArcSinh(Times(c_DEFAULT,x_)),n_DEFAULT),$p("§rfx")),x_Symbol),
    Condition(With(list(Set(u,ExpandIntegrand(Power(ArcSinh(Times(c,x)),n),$s("§rfx"),x))),Condition(Integrate(u,x),SumQ(u))),And(FreeQ(c,x),RationalFunctionQ($s("§rfx"),x),IGtQ(n,C0)))),
IIntegrate(6269,Integrate(Times(Power(Plus(Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT),a_),n_DEFAULT),$p("§rfx")),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times($s("§rfx"),Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),n)),x),x),And(FreeQ(list(a,b,c),x),RationalFunctionQ($s("§rfx"),x),IGtQ(n,C0)))),
IIntegrate(6270,Integrate(Times(Power(ArcSinh(Times(c_DEFAULT,x_)),n_DEFAULT),$p("§rfx"),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(With(list(Set(u,ExpandIntegrand(Times(Power(Plus(d,Times(e,Sqr(x))),p),Power(ArcSinh(Times(c,x)),n)),$s("§rfx"),x))),Condition(Integrate(u,x),SumQ(u))),And(FreeQ(list(c,d,e),x),RationalFunctionQ($s("§rfx"),x),IGtQ(n,C0),EqQ(e,Times(Sqr(c),d)),IntegerQ(Subtract(p,C1D2))))),
IIntegrate(6271,Integrate(Times(Power(Plus(Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT),a_),n_DEFAULT),$p("§rfx"),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Plus(d,Times(e,Sqr(x))),p),Times($s("§rfx"),Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),n)),x),x),And(FreeQ(List(a,b,c,d,e),x),RationalFunctionQ($s("§rfx"),x),IGtQ(n,C0),EqQ(e,Times(Sqr(c),d)),IntegerQ(Subtract(p,C1D2))))),
IIntegrate(6272,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Unintegrable(Times(u,Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),n)),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(6273,Integrate(Power(Plus(a_DEFAULT,Times(ArcSinh(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),n_DEFAULT),x_Symbol),
    Condition(Simp(Star(Power(d,CN1),Subst(Integrate(Power(Plus(a,Times(b,ArcSinh(x))),n),x),x,Plus(c,Times(d,x)))),x),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(6274,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSinh(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),n_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(d,CN1),Subst(Integrate(Times(Power(Plus(Times(Subtract(Times(d,e),Times(c,f)),Power(d,CN1)),Times(f,x,Power(d,CN1))),m),Power(Plus(a,Times(b,ArcSinh(x))),n)),x),x,Plus(c,Times(d,x)))),x),FreeQ(List(a,b,c,d,e,f,m,n),x))),
IIntegrate(6275,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSinh(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),n_DEFAULT),Power(Plus(A_DEFAULT,Times(B_DEFAULT,x_),Times(C_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(d,CN1),Subst(Integrate(Times(Power(Plus(Times(CSymbol,Power(d,CN2)),Times(CSymbol,Power(d,CN2),Sqr(x))),p),Power(Plus(a,Times(b,ArcSinh(x))),n)),x),x,Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d,ASymbol,BSymbol,CSymbol,n,p),x),EqQ(Subtract(Times(BSymbol,Plus(C1,Sqr(c))),Times(C2,ASymbol,c,d)),C0),EqQ(Subtract(Times(C2,c,CSymbol),Times(BSymbol,d)),C0)))),
IIntegrate(6276,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSinh(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),n_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power(Plus(A_DEFAULT,Times(B_DEFAULT,x_),Times(C_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(d,CN1),Subst(Integrate(Times(Power(Plus(Times(Subtract(Times(d,e),Times(c,f)),Power(d,CN1)),Times(f,x,Power(d,CN1))),m),Power(Plus(Times(CSymbol,Power(d,CN2)),Times(CSymbol,Power(d,CN2),Sqr(x))),p),Power(Plus(a,Times(b,ArcSinh(x))),n)),x),x,Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d,e,f,ASymbol,BSymbol,CSymbol,m,n,p),x),EqQ(Subtract(Times(BSymbol,Plus(C1,Sqr(c))),Times(C2,ASymbol,c,d)),C0),EqQ(Subtract(Times(C2,c,CSymbol),Times(BSymbol,d)),C0)))),
IIntegrate(6277,Integrate(Sqrt(Plus(a_DEFAULT,Times(ArcSinh(Plus(c_,Times(d_DEFAULT,Sqr(x_)))),b_DEFAULT))),x_Symbol),
    Condition(Plus(Simp(Times(x,Sqrt(Plus(a,Times(b,ArcSinh(Plus(c,Times(d,Sqr(x)))))))),x),Negate(Simp(Times(CSqrtPi,x,Subtract(Cosh(Times(a,Power(Times(C2,b),CN1))),Times(c,Sinh(Times(a,Power(Times(C2,b),CN1))))),FresnelC(Times(Sqrt(Times(CN1,c,Power(Times(Pi,b),CN1))),Sqrt(Plus(a,Times(b,ArcSinh(Plus(c,Times(d,Sqr(x))))))))),Power(Times(Sqrt(Times(CN1,c,Power(b,CN1))),Plus(Cosh(Times(C1D2,ArcSinh(Plus(c,Times(d,Sqr(x)))))),Times(c,Sinh(Times(C1D2,ArcSinh(Plus(c,Times(d,Sqr(x))))))))),CN1)),x)),Simp(Times(CSqrtPi,x,Plus(Cosh(Times(a,Power(Times(C2,b),CN1))),Times(c,Sinh(Times(a,Power(Times(C2,b),CN1))))),FresnelS(Times(Sqrt(Times(CN1,c,Power(Times(Pi,b),CN1))),Sqrt(Plus(a,Times(b,ArcSinh(Plus(c,Times(d,Sqr(x))))))))),Power(Times(Sqrt(Times(CN1,c,Power(b,CN1))),Plus(Cosh(Times(C1D2,ArcSinh(Plus(c,Times(d,Sqr(x)))))),Times(c,Sinh(Times(C1D2,ArcSinh(Plus(c,Times(d,Sqr(x))))))))),CN1)),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Sqr(c),CN1)))),
IIntegrate(6278,Integrate(Power(Plus(a_DEFAULT,Times(ArcSinh(Plus(c_,Times(d_DEFAULT,Sqr(x_)))),b_DEFAULT)),n_),x_Symbol),
    Condition(Plus(Simp(Times(x,Power(Plus(a,Times(b,ArcSinh(Plus(c,Times(d,Sqr(x)))))),n)),x),Negate(Simp(Times(C2,b,n,Sqrt(Plus(Times(C2,c,d,Sqr(x)),Times(Sqr(d),Power(x,C4)))),Power(Plus(a,Times(b,ArcSinh(Plus(c,Times(d,Sqr(x)))))),Subtract(n,C1)),Power(Times(d,x),CN1)),x)),Simp(Star(Times(C4,Sqr(b),n,Subtract(n,C1)),Integrate(Power(Plus(a,Times(b,ArcSinh(Plus(c,Times(d,Sqr(x)))))),Subtract(n,C2)),x)),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Sqr(c),CN1),GtQ(n,C1)))),
IIntegrate(6279,Integrate(Power(Plus(a_DEFAULT,Times(ArcSinh(Plus(c_,Times(d_DEFAULT,Sqr(x_)))),b_DEFAULT)),CN1),x_Symbol),
    Condition(Plus(Simp(Times(x,Subtract(Times(c,Cosh(Times(a,Power(Times(C2,b),CN1)))),Sinh(Times(a,Power(Times(C2,b),CN1)))),CoshIntegral(Times(Plus(a,Times(b,ArcSinh(Plus(c,Times(d,Sqr(x)))))),Power(Times(C2,b),CN1))),Power(Times(C2,b,Plus(Cosh(Times(C1D2,ArcSinh(Plus(c,Times(d,Sqr(x)))))),Times(c,Sinh(Times(C1D2,ArcSinh(Plus(c,Times(d,Sqr(x))))))))),CN1)),x),Simp(Times(x,Subtract(Cosh(Times(a,Power(Times(C2,b),CN1))),Times(c,Sinh(Times(a,Power(Times(C2,b),CN1))))),SinhIntegral(Times(Plus(a,Times(b,ArcSinh(Plus(c,Times(d,Sqr(x)))))),Power(Times(C2,b),CN1))),Power(Times(C2,b,Plus(Cosh(Times(C1D2,ArcSinh(Plus(c,Times(d,Sqr(x)))))),Times(c,Sinh(Times(C1D2,ArcSinh(Plus(c,Times(d,Sqr(x))))))))),CN1)),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Sqr(c),CN1)))),
IIntegrate(6280,Integrate(Power(Plus(a_DEFAULT,Times(ArcSinh(Plus(c_,Times(d_DEFAULT,Sqr(x_)))),b_DEFAULT)),CN1D2),x_Symbol),
    Condition(Plus(Simp(Times(Plus(c,C1),Sqrt(CPiHalf),x,Subtract(Cosh(Times(a,Power(Times(C2,b),CN1))),Sinh(Times(a,Power(Times(C2,b),CN1)))),Erfi(Times(Sqrt(Plus(a,Times(b,ArcSinh(Plus(c,Times(d,Sqr(x))))))),Power(Times(C2,b),CN1D2))),Power(Times(C2,Sqrt(b),Plus(Cosh(Times(C1D2,ArcSinh(Plus(c,Times(d,Sqr(x)))))),Times(c,Sinh(Times(C1D2,ArcSinh(Plus(c,Times(d,Sqr(x))))))))),CN1)),x),Simp(Times(Subtract(c,C1),Sqrt(CPiHalf),x,Plus(Cosh(Times(a,Power(Times(C2,b),CN1))),Sinh(Times(a,Power(Times(C2,b),CN1)))),Erf(Times(Sqrt(Plus(a,Times(b,ArcSinh(Plus(c,Times(d,Sqr(x))))))),Power(Times(C2,b),CN1D2))),Power(Times(C2,Sqrt(b),Plus(Cosh(Times(C1D2,ArcSinh(Plus(c,Times(d,Sqr(x)))))),Times(c,Sinh(Times(C1D2,ArcSinh(Plus(c,Times(d,Sqr(x))))))))),CN1)),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Sqr(c),CN1))))
  );
}
