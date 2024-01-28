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
class IntRules136 { 
  public static IAST RULES = List( 
IIntegrate(2721,Integrate(Times(u_DEFAULT,Power(Plus(Times(a_DEFAULT,Power(F_,v_)),Times(b_DEFAULT,Power(F_,w_))),n_)),x_Symbol),
    Condition(Integrate(Times(u,Power(FSymbol,Times(n,v)),Power(Plus(a,Times(b,Power(FSymbol,ExpandToSum(Subtract(w,v),x)))),n)),x),And(FreeQ(List(FSymbol,a,b,n),x),ILtQ(n,C0),LinearQ(list(v,w),x)))),
IIntegrate(2722,Integrate(Times(u_DEFAULT,Power(Plus(Times(a_DEFAULT,Power(F_,v_)),Times(b_DEFAULT,Power(G_,w_))),n_)),x_Symbol),
    Condition(Integrate(Times(u,Power(FSymbol,Times(n,v)),Power(Plus(a,Times(b,Exp(ExpandToSum(Subtract(Times(Log(GSymbol),w),Times(Log(FSymbol),v)),x)))),n)),x),And(FreeQ(List(FSymbol,GSymbol,a,b,n),x),ILtQ(n,C0),LinearQ(list(v,w),x)))),
IIntegrate(2723,Integrate(Times(u_DEFAULT,Power(Plus(Times(a_DEFAULT,Power(F_,v_)),Times(b_DEFAULT,Power(F_,w_))),n_)),x_Symbol),
    Condition(Simp(Star(Times(Power(Plus(Times(a,Power(FSymbol,v)),Times(b,Power(FSymbol,w))),n),Power(Times(Power(FSymbol,Times(n,v)),Power(Plus(a,Times(b,Power(FSymbol,ExpandToSum(Subtract(w,v),x)))),n)),CN1)),Integrate(Times(u,Power(FSymbol,Times(n,v)),Power(Plus(a,Times(b,Power(FSymbol,ExpandToSum(Subtract(w,v),x)))),n)),x)),x),And(FreeQ(List(FSymbol,a,b,n),x),Not(IntegerQ(n)),LinearQ(list(v,w),x)))),
IIntegrate(2724,Integrate(Times(u_DEFAULT,Power(Plus(Times(a_DEFAULT,Power(F_,v_)),Times(b_DEFAULT,Power(G_,w_))),n_)),x_Symbol),
    Condition(Simp(Star(Times(Power(Plus(Times(a,Power(FSymbol,v)),Times(b,Power(GSymbol,w))),n),Power(Times(Power(FSymbol,Times(n,v)),Power(Plus(a,Times(b,Exp(ExpandToSum(Subtract(Times(Log(GSymbol),w),Times(Log(FSymbol),v)),x)))),n)),CN1)),Integrate(Times(u,Power(FSymbol,Times(n,v)),Power(Plus(a,Times(b,Exp(ExpandToSum(Subtract(Times(Log(GSymbol),w),Times(Log(FSymbol),v)),x)))),n)),x)),x),And(FreeQ(List(FSymbol,GSymbol,a,b,n),x),Not(IntegerQ(n)),LinearQ(list(v,w),x)))),
IIntegrate(2725,Integrate(Times(u_DEFAULT,Power(F_,v_),Power(G_,w_)),x_Symbol),
    Condition(With(list(Set(z,Plus(Times(v,Log(FSymbol)),Times(w,Log(GSymbol))))),Condition(Integrate(Times(u,NormalizeIntegrand(Exp(z),x)),x),Or(BinomialQ(z,x),And(PolynomialQ(z,x),LeQ(Exponent(z,x),C2))))),FreeQ(list(FSymbol,GSymbol),x))),
IIntegrate(2726,Integrate(Times(y_DEFAULT,Power(F_,u_),Plus(v_,w_)),x_Symbol),
    Condition(With(list(Set(z,Times(v,y,Power(Times(Log(FSymbol),D(u,x)),CN1)))),Condition(Simp(Times(Power(FSymbol,u),z),x),EqQ(D(z,x),Times(w,y)))),FreeQ(FSymbol,x))),
IIntegrate(2727,Integrate(Times(Power(F_,u_),Power(v_,n_DEFAULT),w_),x_Symbol),
    Condition(With(list(Set(z,Plus(Times(Log(FSymbol),v,D(u,x)),Times(Plus(n,C1),D(v,x))))),Condition(Simp(Times(Coefficient(w,x,Exponent(w,x)),Power(Coefficient(z,x,Exponent(z,x)),CN1),Power(FSymbol,u),Power(v,Plus(n,C1))),x),And(EqQ(Exponent(w,x),Exponent(z,x)),EqQ(Times(w,Coefficient(z,x,Exponent(z,x))),Times(z,Coefficient(w,x,Exponent(w,x))))))),And(FreeQ(list(FSymbol,n),x),PolynomialQ(u,x),PolynomialQ(v,x),PolynomialQ(w,x)))),
IIntegrate(2728,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(F_,Times(c_DEFAULT,Sqrt(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),CN1D2))))),n_DEFAULT),Power(Plus(A_DEFAULT,Times(B_DEFAULT,x_),Times(C_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Simp(Star(Times(C2,e,g,Power(Times(CSymbol,Subtract(Times(e,f),Times(d,g))),CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,Power(FSymbol,Times(c,x)))),n),Power(x,CN1)),x),x,Times(Sqrt(Plus(d,Times(e,x))),Power(Plus(f,Times(g,x)),CN1D2)))),x),And(FreeQ(List(a,b,c,d,e,f,g,ASymbol,BSymbol,CSymbol,FSymbol),x),EqQ(Subtract(Times(CSymbol,d,f),Times(ASymbol,e,g)),C0),EqQ(Subtract(Times(BSymbol,e,g),Times(CSymbol,Plus(Times(e,f),Times(d,g)))),C0),IGtQ(n,C0)))),
IIntegrate(2729,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(F_,Times(c_DEFAULT,Sqrt(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),CN1D2))))),n_DEFAULT),Power(Plus(A_,Times(C_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Simp(Star(Times(C2,e,g,Power(Times(CSymbol,Subtract(Times(e,f),Times(d,g))),CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,Power(FSymbol,Times(c,x)))),n),Power(x,CN1)),x),x,Times(Sqrt(Plus(d,Times(e,x))),Power(Plus(f,Times(g,x)),CN1D2)))),x),And(FreeQ(List(a,b,c,d,e,f,g,ASymbol,CSymbol,FSymbol),x),EqQ(Subtract(Times(CSymbol,d,f),Times(ASymbol,e,g)),C0),EqQ(Plus(Times(e,f),Times(d,g)),C0),IGtQ(n,C0)))),
IIntegrate(2730,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(F_,Times(c_DEFAULT,Sqrt(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),CN1D2))))),n_),Power(Plus(A_DEFAULT,Times(B_DEFAULT,x_),Times(C_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(a,Times(b,Power(FSymbol,Times(c,Sqrt(Plus(d,Times(e,x))),Power(Plus(f,Times(g,x)),CN1D2))))),n),Power(Plus(ASymbol,Times(BSymbol,x),Times(CSymbol,Sqr(x))),CN1)),x),And(FreeQ(List(a,b,c,d,e,f,g,ASymbol,BSymbol,CSymbol,FSymbol,n),x),EqQ(Subtract(Times(CSymbol,d,f),Times(ASymbol,e,g)),C0),EqQ(Subtract(Times(BSymbol,e,g),Times(CSymbol,Plus(Times(e,f),Times(d,g)))),C0),Not(IGtQ(n,C0))))),
IIntegrate(2731,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(F_,Times(c_DEFAULT,Sqrt(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),CN1D2))))),n_),Power(Plus(A_,Times(C_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(a,Times(b,Power(FSymbol,Times(c,Sqrt(Plus(d,Times(e,x))),Power(Plus(f,Times(g,x)),CN1D2))))),n),Power(Plus(ASymbol,Times(CSymbol,Sqr(x))),CN1)),x),And(FreeQ(List(a,b,c,d,e,f,g,ASymbol,CSymbol,FSymbol,n),x),EqQ(Subtract(Times(CSymbol,d,f),Times(ASymbol,e,g)),C0),EqQ(Plus(Times(e,f),Times(d,g)),C0),Not(IGtQ(n,C0))))),
IIntegrate(2732,Integrate(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),x_Symbol),
    Condition(Subtract(Simp(Times(x,Log(Times(c,Power(x,n)))),x),Simp(Times(n,x),x)),FreeQ(list(c,n),x))),
IIntegrate(2733,Integrate(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Subtract(Simp(Times(x,Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p)),x),Simp(Star(Times(b,n,p),Integrate(Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),Subtract(p,C1)),x)),x)),And(FreeQ(List(a,b,c,n),x),GtQ(p,C0),IntegerQ(Times(C2,p))))),
IIntegrate(2734,Integrate(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_),x_Symbol),
    Condition(Subtract(Simp(Times(x,Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),CN1)),x),Simp(Star(Power(Times(b,n,Plus(p,C1)),CN1),Integrate(Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),Plus(p,C1)),x)),x)),And(FreeQ(List(a,b,c,n),x),LtQ(p,CN1),IntegerQ(Times(C2,p))))),
IIntegrate(2735,Integrate(Power(Log(Times(c_DEFAULT,x_)),CN1),x_Symbol),
    Condition(Simp(Times(LogIntegral(Times(c,x)),Power(c,CN1)),x),FreeQ(c,x))),
IIntegrate(2736,Integrate(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_),x_Symbol),
    Condition(Simp(Star(Power(Times(n,Power(c,Power(n,CN1))),CN1),Subst(Integrate(Times(Exp(Times(x,Power(n,CN1))),Power(Plus(a,Times(b,x)),p)),x),x,Log(Times(c,Power(x,n))))),x),And(FreeQ(List(a,b,c,p),x),IntegerQ(Power(n,CN1))))),
IIntegrate(2737,Integrate(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_),x_Symbol),
    Condition(Simp(Star(Times(x,Power(Times(n,Power(Times(c,Power(x,n)),Power(n,CN1))),CN1)),Subst(Integrate(Times(Exp(Times(x,Power(n,CN1))),Power(Plus(a,Times(b,x)),p)),x),x,Log(Times(c,Power(x,n))))),x),FreeQ(List(a,b,c,n,p),x))),
IIntegrate(2738,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),Power(x_,CN1)),x_Symbol),
    Condition(Simp(Times(Sqr(Plus(a,Times(b,Log(Times(c,Power(x,n)))))),Power(Times(C2,b,n),CN1)),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(2739,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(x_,CN1)),x_Symbol),
    Condition(Simp(Star(Power(Times(b,n),CN1),Subst(Integrate(Power(x,p),x),x,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),x),FreeQ(List(a,b,c,n,p),x))),
IIntegrate(2740,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Simp(Times(b,Power(Times(d,x),Plus(m,C1)),Log(Times(c,Power(x,n))),Power(Times(d,Plus(m,C1)),CN1)),x),And(FreeQ(List(a,b,c,d,m,n),x),NeQ(m,CN1),EqQ(Subtract(Times(a,Plus(m,C1)),Times(b,n)),C0))))
  );
}
