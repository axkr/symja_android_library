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
class IntRules265 { 
  public static IAST RULES = List( 
IIntegrate(5301,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),$p("§px"),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(With(list(Set(u,ExpandIntegrand(Times($s("§px"),Power(Plus(d,Times(e,Sqr(x))),p),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n)),x))),Condition(Integrate(u,x),SumQ(u))),And(FreeQ(List(a,b,c,d,e,n),x),PolynomialQ($s("§px"),x),EqQ(Plus(Times(Sqr(c),d),e),C0),IntegerQ(Plus(p,CN1D2))))),
IIntegrate(5302,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),$p("§px",true),Power(Plus(f_,Times(g_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_))),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,ExpandIntegrand(Times($s("§px"),Power(Plus(f,Times(g,Power(Plus(d,Times(e,Sqr(x))),p))),m),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n)),x))),Condition(Integrate(u,x),SumQ(u))),And(FreeQ(List(a,b,c,d,e,f,g),x),PolynomialQ($s("§px"),x),EqQ(Plus(Times(Sqr(c),d),e),C0),IGtQ(Plus(p,C1D2),C0),IntegersQ(m,n)))),
IIntegrate(5303,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),$p("§px",true),Power(Plus(f_,Times(g_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_))),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,ExpandIntegrand(Times($s("§px"),Power(Plus(f,Times(g,Power(Plus(d,Times(e,Sqr(x))),p))),m),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n)),x))),Condition(Integrate(u,x),SumQ(u))),And(FreeQ(List(a,b,c,d,e,f,g),x),PolynomialQ($s("§px"),x),EqQ(Plus(Times(Sqr(c),d),e),C0),IGtQ(Plus(p,C1D2),C0),IntegersQ(m,n)))),
IIntegrate(5304,Integrate(Times(Power(ArcSin(Times(c_DEFAULT,x_)),n_DEFAULT),$p("§rfx")),x_Symbol),
    Condition(With(list(Set(u,ExpandIntegrand(Power(ArcSin(Times(c,x)),n),$s("§rfx"),x))),Condition(Integrate(u,x),SumQ(u))),And(FreeQ(c,x),RationalFunctionQ($s("§rfx"),x),IGtQ(n,C0)))),
IIntegrate(5305,Integrate(Times(Power(ArcCos(Times(c_DEFAULT,x_)),n_DEFAULT),$p("§rfx")),x_Symbol),
    Condition(With(list(Set(u,ExpandIntegrand(Power(ArcCos(Times(c,x)),n),$s("§rfx"),x))),Condition(Integrate(u,x),SumQ(u))),And(FreeQ(c,x),RationalFunctionQ($s("§rfx"),x),IGtQ(n,C0)))),
IIntegrate(5306,Integrate(Times(Power(Plus(Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT),a_),n_DEFAULT),$p("§rfx")),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times($s("§rfx"),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n)),x),x),And(FreeQ(list(a,b,c),x),RationalFunctionQ($s("§rfx"),x),IGtQ(n,C0)))),
IIntegrate(5307,Integrate(Times(Power(Plus(Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT),a_),n_DEFAULT),$p("§rfx")),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times($s("§rfx"),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n)),x),x),And(FreeQ(list(a,b,c),x),RationalFunctionQ($s("§rfx"),x),IGtQ(n,C0)))),
IIntegrate(5308,Integrate(Times(Power(ArcSin(Times(c_DEFAULT,x_)),n_DEFAULT),$p("§rfx"),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(With(list(Set(u,ExpandIntegrand(Times(Power(Plus(d,Times(e,Sqr(x))),p),Power(ArcSin(Times(c,x)),n)),$s("§rfx"),x))),Condition(Integrate(u,x),SumQ(u))),And(FreeQ(list(c,d,e),x),RationalFunctionQ($s("§rfx"),x),IGtQ(n,C0),EqQ(Plus(Times(Sqr(c),d),e),C0),IntegerQ(Plus(p,CN1D2))))),
IIntegrate(5309,Integrate(Times(Power(ArcCos(Times(c_DEFAULT,x_)),n_DEFAULT),$p("§rfx"),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(With(list(Set(u,ExpandIntegrand(Times(Power(Plus(d,Times(e,Sqr(x))),p),Power(ArcCos(Times(c,x)),n)),$s("§rfx"),x))),Condition(Integrate(u,x),SumQ(u))),And(FreeQ(list(c,d,e),x),RationalFunctionQ($s("§rfx"),x),IGtQ(n,C0),EqQ(Plus(Times(Sqr(c),d),e),C0),IntegerQ(Plus(p,CN1D2))))),
IIntegrate(5310,Integrate(Times(Power(Plus(Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT),a_),n_DEFAULT),$p("§rfx"),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Plus(d,Times(e,Sqr(x))),p),Times($s("§rfx"),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n)),x),x),And(FreeQ(List(a,b,c,d,e),x),RationalFunctionQ($s("§rfx"),x),IGtQ(n,C0),EqQ(Plus(Times(Sqr(c),d),e),C0),IntegerQ(Plus(p,CN1D2))))),
IIntegrate(5311,Integrate(Times(Power(Plus(Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT),a_),n_DEFAULT),$p("§rfx"),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Plus(d,Times(e,Sqr(x))),p),Times($s("§rfx"),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n)),x),x),And(FreeQ(List(a,b,c,d,e),x),RationalFunctionQ($s("§rfx"),x),IGtQ(n,C0),EqQ(Plus(Times(Sqr(c),d),e),C0),IntegerQ(Plus(p,CN1D2))))),
IIntegrate(5312,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Unintegrable(Times(u,Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n)),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(5313,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Unintegrable(Times(u,Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n)),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(5314,Integrate(Power(Plus(a_DEFAULT,Times(ArcSin(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),n_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Power(d,CN1),Subst(Integrate(Power(Plus(a,Times(b,ArcSin(x))),n),x),x,Plus(c,Times(d,x))),x),x),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(5315,Integrate(Power(Plus(a_DEFAULT,Times(ArcCos(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),n_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Power(d,CN1),Subst(Integrate(Power(Plus(a,Times(b,ArcCos(x))),n),x),x,Plus(c,Times(d,x))),x),x),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(5316,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),n_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(d,CN1),Subst(Integrate(Times(Power(Plus(Times(Subtract(Times(d,e),Times(c,f)),Power(d,CN1)),Times(f,x,Power(d,CN1))),m),Power(Plus(a,Times(b,ArcSin(x))),n)),x),x,Plus(c,Times(d,x))),x),x),FreeQ(List(a,b,c,d,e,f,m,n),x))),
IIntegrate(5317,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),n_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(d,CN1),Subst(Integrate(Times(Power(Plus(Times(Subtract(Times(d,e),Times(c,f)),Power(d,CN1)),Times(f,x,Power(d,CN1))),m),Power(Plus(a,Times(b,ArcCos(x))),n)),x),x,Plus(c,Times(d,x))),x),x),FreeQ(List(a,b,c,d,e,f,m,n),x))),
IIntegrate(5318,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),n_DEFAULT),Power(Plus(A_DEFAULT,Times(B_DEFAULT,x_),Times(C_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(d,CN1),Subst(Integrate(Times(Power(Plus(Times(CN1,CSymbol,Power(d,CN2)),Times(CSymbol,Power(d,CN2),Sqr(x))),p),Power(Plus(a,Times(b,ArcSin(x))),n)),x),x,Plus(c,Times(d,x))),x),x),And(FreeQ(List(a,b,c,d,ASymbol,BSymbol,CSymbol,n,p),x),EqQ(Plus(Times(BSymbol,Subtract(C1,Sqr(c))),Times(C2,ASymbol,c,d)),C0),EqQ(Subtract(Times(C2,c,CSymbol),Times(BSymbol,d)),C0)))),
IIntegrate(5319,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),n_DEFAULT),Power(Plus(A_DEFAULT,Times(B_DEFAULT,x_),Times(C_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(d,CN1),Subst(Integrate(Times(Power(Plus(Times(CN1,CSymbol,Power(d,CN2)),Times(CSymbol,Power(d,CN2),Sqr(x))),p),Power(Plus(a,Times(b,ArcCos(x))),n)),x),x,Plus(c,Times(d,x))),x),x),And(FreeQ(List(a,b,c,d,ASymbol,BSymbol,CSymbol,n,p),x),EqQ(Plus(Times(BSymbol,Subtract(C1,Sqr(c))),Times(C2,ASymbol,c,d)),C0),EqQ(Subtract(Times(C2,c,CSymbol),Times(BSymbol,d)),C0)))),
IIntegrate(5320,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),n_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power(Plus(A_DEFAULT,Times(B_DEFAULT,x_),Times(C_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(d,CN1),Subst(Integrate(Times(Power(Plus(Times(Subtract(Times(d,e),Times(c,f)),Power(d,CN1)),Times(f,x,Power(d,CN1))),m),Power(Plus(Times(CN1,CSymbol,Power(d,CN2)),Times(CSymbol,Power(d,CN2),Sqr(x))),p),Power(Plus(a,Times(b,ArcSin(x))),n)),x),x,Plus(c,Times(d,x))),x),x),And(FreeQ(List(a,b,c,d,e,f,ASymbol,BSymbol,CSymbol,m,n,p),x),EqQ(Plus(Times(BSymbol,Subtract(C1,Sqr(c))),Times(C2,ASymbol,c,d)),C0),EqQ(Subtract(Times(C2,c,CSymbol),Times(BSymbol,d)),C0))))
  );
}
