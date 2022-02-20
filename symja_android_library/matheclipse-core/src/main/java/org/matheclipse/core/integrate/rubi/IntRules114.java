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
class IntRules114 { 
  public static IAST RULES = List( 
IIntegrate(2281,Integrate(Times(u_DEFAULT,Power(Times(a_DEFAULT,Power(F_,v_)),n_)),x_Symbol),
    Condition(Dist(Times(Power(Times(a,Power(FSymbol,v)),n),Power(Power(FSymbol,Times(n,v)),CN1)),Integrate(Times(u,Power(FSymbol,Times(n,v))),x),x),And(FreeQ(list(FSymbol,a,n),x),Not(IntegerQ(n))))),
IIntegrate(2282,Integrate(u_,x_Symbol),
    Condition(With(list(Set(v,FunctionOfExponential(u,x))),Dist(Times(v,Power(D(v,x),CN1)),Subst(Integrate(Times(FunctionOfExponentialFunction(u,x),Power(x,CN1)),x),x,v),x)),And(FunctionOfExponentialQ(u,x),Not(MatchQ(u,Condition(Times(w_,Power(Times(a_DEFAULT,Power(v_,n_)),m_)),And(FreeQ(list(a,m,n),x),IntegerQ(Times(m,n)))))),Not(MatchQ(u,Condition(Times(Exp(Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x)))),$(F_,v_)),And(FreeQ(list(a,b,c),x),InverseFunctionQ(F(x))))))))),
IIntegrate(2283,Integrate(Times(u_DEFAULT,Power(Plus(Times(a_DEFAULT,Power(F_,v_)),Times(b_DEFAULT,Power(F_,w_))),n_)),x_Symbol),
    Condition(Integrate(Times(u,Power(FSymbol,Times(n,v)),Power(Plus(a,Times(b,Power(FSymbol,ExpandToSum(Subtract(w,v),x)))),n)),x),And(FreeQ(List(FSymbol,a,b,n),x),ILtQ(n,C0),LinearQ(list(v,w),x)))),
IIntegrate(2284,Integrate(Times(u_DEFAULT,Power(Plus(Times(a_DEFAULT,Power(F_,v_)),Times(b_DEFAULT,Power(G_,w_))),n_)),x_Symbol),
    Condition(Integrate(Times(u,Power(FSymbol,Times(n,v)),Power(Plus(a,Times(b,Exp(ExpandToSum(Subtract(Times(Log(GSymbol),w),Times(Log(FSymbol),v)),x)))),n)),x),And(FreeQ(List(FSymbol,GSymbol,a,b,n),x),ILtQ(n,C0),LinearQ(list(v,w),x)))),
IIntegrate(2285,Integrate(Times(u_DEFAULT,Power(Plus(Times(a_DEFAULT,Power(F_,v_)),Times(b_DEFAULT,Power(F_,w_))),n_)),x_Symbol),
    Condition(Dist(Times(Power(Plus(Times(a,Power(FSymbol,v)),Times(b,Power(FSymbol,w))),n),Power(Times(Power(FSymbol,Times(n,v)),Power(Plus(a,Times(b,Power(FSymbol,ExpandToSum(Subtract(w,v),x)))),n)),CN1)),Integrate(Times(u,Power(FSymbol,Times(n,v)),Power(Plus(a,Times(b,Power(FSymbol,ExpandToSum(Subtract(w,v),x)))),n)),x),x),And(FreeQ(List(FSymbol,a,b,n),x),Not(IntegerQ(n)),LinearQ(list(v,w),x)))),
IIntegrate(2286,Integrate(Times(u_DEFAULT,Power(Plus(Times(a_DEFAULT,Power(F_,v_)),Times(b_DEFAULT,Power(G_,w_))),n_)),x_Symbol),
    Condition(Dist(Times(Power(Plus(Times(a,Power(FSymbol,v)),Times(b,Power(GSymbol,w))),n),Power(Times(Power(FSymbol,Times(n,v)),Power(Plus(a,Times(b,Exp(ExpandToSum(Subtract(Times(Log(GSymbol),w),Times(Log(FSymbol),v)),x)))),n)),CN1)),Integrate(Times(u,Power(FSymbol,Times(n,v)),Power(Plus(a,Times(b,Exp(ExpandToSum(Subtract(Times(Log(GSymbol),w),Times(Log(FSymbol),v)),x)))),n)),x),x),And(FreeQ(List(FSymbol,GSymbol,a,b,n),x),Not(IntegerQ(n)),LinearQ(list(v,w),x)))),
IIntegrate(2287,Integrate(Times(u_DEFAULT,Power(F_,v_),Power(G_,w_)),x_Symbol),
    Condition(With(list(Set(z,Plus(Times(v,Log(FSymbol)),Times(w,Log(GSymbol))))),Condition(Integrate(Times(u,NormalizeIntegrand(Exp(z),x)),x),Or(BinomialQ(z,x),And(PolynomialQ(z,x),LeQ(Exponent(z,x),C2))))),FreeQ(list(FSymbol,GSymbol),x))),
IIntegrate(2288,Integrate(Times(y_DEFAULT,Power(F_,u_),Plus(v_,w_)),x_Symbol),
    Condition(With(list(Set(z,Times(v,y,Power(Times(Log(FSymbol),D(u,x)),CN1)))),Condition(Simp(Times(Power(FSymbol,u),z),x),EqQ(D(z,x),Times(w,y)))),FreeQ(FSymbol,x))),
IIntegrate(2289,Integrate(Times(Power(F_,u_),Power(v_,n_DEFAULT),w_),x_Symbol),
    Condition(With(list(Set(z,Plus(Times(Log(FSymbol),v,D(u,x)),Times(Plus(n,C1),D(v,x))))),Condition(Simp(Times(Coefficient(w,x,Exponent(w,x)),Power(FSymbol,u),Power(v,Plus(n,C1)),Power(Coefficient(z,x,Exponent(z,x)),CN1)),x),And(EqQ(Exponent(w,x),Exponent(z,x)),EqQ(Times(w,Coefficient(z,x,Exponent(z,x))),Times(z,Coefficient(w,x,Exponent(w,x))))))),And(FreeQ(list(FSymbol,n),x),PolynomialQ(u,x),PolynomialQ(v,x),PolynomialQ(w,x)))),
IIntegrate(2290,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(F_,Times(c_DEFAULT,Sqrt(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),CN1D2))))),n_DEFAULT),Power(Plus(A_DEFAULT,Times(B_DEFAULT,x_),Times(C_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Dist(Times(C2,e,g,Power(Times(CSymbol,Subtract(Times(e,f),Times(d,g))),CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,Power(FSymbol,Times(c,x)))),n),Power(x,CN1)),x),x,Times(Sqrt(Plus(d,Times(e,x))),Power(Plus(f,Times(g,x)),CN1D2))),x),And(FreeQ(List(a,b,c,d,e,f,g,ASymbol,BSymbol,CSymbol,FSymbol),x),EqQ(Subtract(Times(CSymbol,d,f),Times(ASymbol,e,g)),C0),EqQ(Subtract(Times(BSymbol,e,g),Times(CSymbol,Plus(Times(e,f),Times(d,g)))),C0),IGtQ(n,C0)))),
IIntegrate(2291,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(F_,Times(c_DEFAULT,Sqrt(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),CN1D2))))),n_DEFAULT),Power(Plus(A_,Times(C_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Dist(Times(C2,e,g,Power(Times(CSymbol,Subtract(Times(e,f),Times(d,g))),CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,Power(FSymbol,Times(c,x)))),n),Power(x,CN1)),x),x,Times(Sqrt(Plus(d,Times(e,x))),Power(Plus(f,Times(g,x)),CN1D2))),x),And(FreeQ(List(a,b,c,d,e,f,g,ASymbol,CSymbol,FSymbol),x),EqQ(Subtract(Times(CSymbol,d,f),Times(ASymbol,e,g)),C0),EqQ(Plus(Times(e,f),Times(d,g)),C0),IGtQ(n,C0)))),
IIntegrate(2292,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(F_,Times(c_DEFAULT,Sqrt(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),CN1D2))))),n_),Power(Plus(A_DEFAULT,Times(B_DEFAULT,x_),Times(C_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(a,Times(b,Power(FSymbol,Times(c,Sqrt(Plus(d,Times(e,x))),Power(Plus(f,Times(g,x)),CN1D2))))),n),Power(Plus(ASymbol,Times(BSymbol,x),Times(CSymbol,Sqr(x))),CN1)),x),And(FreeQ(List(a,b,c,d,e,f,g,ASymbol,BSymbol,CSymbol,FSymbol,n),x),EqQ(Subtract(Times(CSymbol,d,f),Times(ASymbol,e,g)),C0),EqQ(Subtract(Times(BSymbol,e,g),Times(CSymbol,Plus(Times(e,f),Times(d,g)))),C0),Not(IGtQ(n,C0))))),
IIntegrate(2293,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(F_,Times(c_DEFAULT,Sqrt(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),CN1D2))))),n_),Power(Plus(A_,Times(C_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(a,Times(b,Power(FSymbol,Times(c,Sqrt(Plus(d,Times(e,x))),Power(Plus(f,Times(g,x)),CN1D2))))),n),Power(Plus(ASymbol,Times(CSymbol,Sqr(x))),CN1)),x),And(FreeQ(List(a,b,c,d,e,f,g,ASymbol,CSymbol,FSymbol,n),x),EqQ(Subtract(Times(CSymbol,d,f),Times(ASymbol,e,g)),C0),EqQ(Plus(Times(e,f),Times(d,g)),C0),Not(IGtQ(n,C0))))),
IIntegrate(2294,Integrate(Times(Plus(A_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),n_DEFAULT))),B_DEFAULT)),Power(Plus(Times(Log(Times(c_DEFAULT,Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT),a_),CN1D2)),x_Symbol),
    Condition(Plus(Simp(Times(BSymbol,Plus(d,Times(e,x)),Sqrt(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,x)),n)))))),Power(Times(b,e),CN1)),x),Dist(Times(Subtract(Times(C2,ASymbol,b),Times(BSymbol,Plus(Times(C2,a),Times(b,n)))),Power(Times(C2,b),CN1)),Integrate(Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,x)),n))))),CN1D2),x),x)),FreeQ(List(a,b,c,d,e,ASymbol,BSymbol,n),x))),
IIntegrate(2295,Integrate(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),x_Symbol),
    Condition(Subtract(Simp(Times(x,Log(Times(c,Power(x,n)))),x),Simp(Times(n,x),x)),FreeQ(list(c,n),x))),
IIntegrate(2296,Integrate(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Subtract(Simp(Times(x,Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p)),x),Dist(Times(b,n,p),Integrate(Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),Subtract(p,C1)),x),x)),And(FreeQ(List(a,b,c,n),x),GtQ(p,C0),IntegerQ(Times(C2,p))))),
IIntegrate(2297,Integrate(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_),x_Symbol),
    Condition(Subtract(Simp(Times(x,Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),CN1)),x),Dist(Power(Times(b,n,Plus(p,C1)),CN1),Integrate(Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),Plus(p,C1)),x),x)),And(FreeQ(List(a,b,c,n),x),LtQ(p,CN1),IntegerQ(Times(C2,p))))),
IIntegrate(2298,Integrate(Power(Log(Times(c_DEFAULT,x_)),CN1),x_Symbol),
    Condition(Simp(Times(LogIntegral(Times(c,x)),Power(c,CN1)),x),FreeQ(c,x))),
IIntegrate(2299,Integrate(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_),x_Symbol),
    Condition(Dist(Power(Times(n,Power(c,Power(n,CN1))),CN1),Subst(Integrate(Times(Exp(Times(x,Power(n,CN1))),Power(Plus(a,Times(b,x)),p)),x),x,Log(Times(c,Power(x,n)))),x),And(FreeQ(List(a,b,c,p),x),IntegerQ(Power(n,CN1))))),
IIntegrate(2300,Integrate(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_),x_Symbol),
    Condition(Dist(Times(x,Power(Times(n,Power(Times(c,Power(x,n)),Power(n,CN1))),CN1)),Subst(Integrate(Times(Exp(Times(x,Power(n,CN1))),Power(Plus(a,Times(b,x)),p)),x),x,Log(Times(c,Power(x,n)))),x),FreeQ(List(a,b,c,n,p),x)))
  );
}
