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
class IntRules333 { 
  public static IAST RULES = List( 
IIntegrate(6661,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(d,CN1),Subst(Integrate(Times(Power(Plus(Times(Subtract(Times(d,e),Times(c,f)),Power(d,CN1)),Times(f,x,Power(d,CN1))),m),Power(Plus(a,Times(b,ArcTanh(x))),p)),x),x,Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d,e,f,m),x),IGtQ(p,C0)))),
IIntegrate(6662,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(d,CN1),Subst(Integrate(Times(Power(Plus(Times(Subtract(Times(d,e),Times(c,f)),Power(d,CN1)),Times(f,x,Power(d,CN1))),m),Power(Plus(a,Times(b,ArcCoth(x))),p)),x),x,Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d,e,f,m),x),IGtQ(p,C0)))),
IIntegrate(6663,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(b,ArcTanh(Plus(c,Times(d,x))))),p)),x),And(FreeQ(List(a,b,c,d,e,f,m,p),x),Not(IGtQ(p,C0))))),
IIntegrate(6664,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(b,ArcCoth(Plus(c,Times(d,x))))),p)),x),And(FreeQ(List(a,b,c,d,e,f,m,p),x),Not(IGtQ(p,C0))))),
IIntegrate(6665,Integrate(Times(ArcTanh(Plus(c_,Times(d_DEFAULT,x_))),Power(Plus(e_,Times(f_DEFAULT,Power(x_,n_DEFAULT))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Star(C1D2,Integrate(Times(Log(Plus(C1,c,Times(d,x))),Power(Plus(e,Times(f,Power(x,n))),CN1)),x)),x),Simp(Star(C1D2,Integrate(Times(Log(Subtract(Subtract(C1,c),Times(d,x))),Power(Plus(e,Times(f,Power(x,n))),CN1)),x)),x)),And(FreeQ(List(c,d,e,f),x),RationalQ(n)))),
IIntegrate(6666,Integrate(Times(ArcCoth(Plus(c_,Times(d_DEFAULT,x_))),Power(Plus(e_,Times(f_DEFAULT,Power(x_,n_DEFAULT))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Star(C1D2,Integrate(Times(Log(Times(Plus(C1,c,Times(d,x)),Power(Plus(c,Times(d,x)),CN1))),Power(Plus(e,Times(f,Power(x,n))),CN1)),x)),x),Simp(Star(C1D2,Integrate(Times(Log(Times(Plus(CN1,c,Times(d,x)),Power(Plus(c,Times(d,x)),CN1))),Power(Plus(e,Times(f,Power(x,n))),CN1)),x)),x)),And(FreeQ(List(c,d,e,f),x),RationalQ(n)))),
IIntegrate(6667,Integrate(Times(ArcTanh(Plus(c_,Times(d_DEFAULT,x_))),Power(Plus(e_,Times(f_DEFAULT,Power(x_,n_))),CN1)),x_Symbol),
    Condition(Unintegrable(Times(ArcTanh(Plus(c,Times(d,x))),Power(Plus(e,Times(f,Power(x,n))),CN1)),x),And(FreeQ(List(c,d,e,f,n),x),Not(RationalQ(n))))),
IIntegrate(6668,Integrate(Times(ArcCoth(Plus(c_,Times(d_DEFAULT,x_))),Power(Plus(e_,Times(f_DEFAULT,Power(x_,n_))),CN1)),x_Symbol),
    Condition(Unintegrable(Times(ArcCoth(Plus(c,Times(d,x))),Power(Plus(e,Times(f,Power(x,n))),CN1)),x),And(FreeQ(List(c,d,e,f,n),x),Not(RationalQ(n))))),
IIntegrate(6669,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(A_DEFAULT,Times(B_DEFAULT,x_),Times(C_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(d,CN1),Subst(Integrate(Times(Power(Plus(Times(CN1,CSymbol,Power(d,CN2)),Times(CSymbol,Power(d,CN2),Sqr(x))),q),Power(Plus(a,Times(b,ArcTanh(x))),p)),x),x,Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d,ASymbol,BSymbol,CSymbol,p,q),x),EqQ(Plus(Times(BSymbol,Subtract(C1,Sqr(c))),Times(C2,ASymbol,c,d)),C0),EqQ(Subtract(Times(C2,c,CSymbol),Times(BSymbol,d)),C0)))),
IIntegrate(6670,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(A_DEFAULT,Times(B_DEFAULT,x_),Times(C_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(d,CN1),Subst(Integrate(Times(Power(Plus(Times(CSymbol,Power(d,CN2)),Times(CSymbol,Power(d,CN2),Sqr(x))),q),Power(Plus(a,Times(b,ArcCoth(x))),p)),x),x,Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d,ASymbol,BSymbol,CSymbol,p,q),x),EqQ(Plus(Times(BSymbol,Subtract(C1,Sqr(c))),Times(C2,ASymbol,c,d)),C0),EqQ(Subtract(Times(C2,c,CSymbol),Times(BSymbol,d)),C0)))),
IIntegrate(6671,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power(Plus(A_DEFAULT,Times(B_DEFAULT,x_),Times(C_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(d,CN1),Subst(Integrate(Times(Power(Plus(Times(Subtract(Times(d,e),Times(c,f)),Power(d,CN1)),Times(f,x,Power(d,CN1))),m),Power(Plus(Times(CN1,CSymbol,Power(d,CN2)),Times(CSymbol,Power(d,CN2),Sqr(x))),q),Power(Plus(a,Times(b,ArcTanh(x))),p)),x),x,Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d,e,f,ASymbol,BSymbol,CSymbol,m,p,q),x),EqQ(Plus(Times(BSymbol,Subtract(C1,Sqr(c))),Times(C2,ASymbol,c,d)),C0),EqQ(Subtract(Times(C2,c,CSymbol),Times(BSymbol,d)),C0)))),
IIntegrate(6672,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power(Plus(A_DEFAULT,Times(B_DEFAULT,x_),Times(C_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(d,CN1),Subst(Integrate(Times(Power(Plus(Times(Subtract(Times(d,e),Times(c,f)),Power(d,CN1)),Times(f,x,Power(d,CN1))),m),Power(Plus(Times(CN1,CSymbol,Power(d,CN2)),Times(CSymbol,Power(d,CN2),Sqr(x))),q),Power(Plus(a,Times(b,ArcCoth(x))),p)),x),x,Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d,e,f,ASymbol,BSymbol,CSymbol,m,p,q),x),EqQ(Plus(Times(BSymbol,Subtract(C1,Sqr(c))),Times(C2,ASymbol,c,d)),C0),EqQ(Subtract(Times(C2,c,CSymbol),Times(BSymbol,d)),C0)))),
IIntegrate(6673,Integrate(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Plus(C1,Times(a,x)),Times(C1D2,Plus(n,C1))),Power(Times(Power(Subtract(C1,Times(a,x)),Times(C1D2,Subtract(n,C1))),Sqrt(Subtract(C1,Times(Sqr(a),Sqr(x))))),CN1)),x),And(FreeQ(a,x),IntegerQ(Times(C1D2,Subtract(n,C1)))))),
IIntegrate(6674,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Times(c_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(c,x),m),Power(Plus(C1,Times(a,x)),Times(C1D2,Plus(n,C1))),Power(Times(Power(Subtract(C1,Times(a,x)),Times(C1D2,Subtract(n,C1))),Sqrt(Subtract(C1,Times(Sqr(a),Sqr(x))))),CN1)),x),And(FreeQ(list(a,c,m),x),IntegerQ(Times(C1D2,Subtract(n,C1)))))),
IIntegrate(6675,Integrate(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),x_Symbol),
    Condition(Integrate(Times(Power(Plus(C1,Times(a,x)),Times(C1D2,n)),Power(Power(Subtract(C1,Times(a,x)),Times(C1D2,n)),CN1)),x),And(FreeQ(list(a,n),x),Not(IntegerQ(Times(C1D2,Subtract(n,C1))))))),
IIntegrate(6676,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),Power(Times(c_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(c,x),m),Power(Plus(C1,Times(a,x)),Times(C1D2,n)),Power(Power(Subtract(C1,Times(a,x)),Times(C1D2,n)),CN1)),x),And(FreeQ(List(a,c,m,n),x),Not(IntegerQ(Times(C1D2,Subtract(n,C1))))))),
IIntegrate(6677,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,x_)),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(c,n),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(p,n)),Power(Subtract(C1,Times(Sqr(a),Sqr(x))),Times(C1D2,n))),x)),x),And(FreeQ(List(a,c,d,p),x),EqQ(Plus(Times(a,c),d),C0),IntegerQ(Times(C1D2,Subtract(n,C1))),IntegerQ(Times(C2,p))))),
IIntegrate(6678,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,x_)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(c,n),Integrate(Times(Power(Plus(e,Times(f,x)),m),Power(Plus(c,Times(d,x)),Subtract(p,n)),Power(Subtract(C1,Times(Sqr(a),Sqr(x))),Times(C1D2,n))),x)),x),And(FreeQ(List(a,c,d,e,f,m,p),x),EqQ(Plus(Times(a,c),d),C0),IntegerQ(Times(C1D2,Subtract(n,C1))),Or(IntegerQ(p),EqQ(p,Times(C1D2,n)),EqQ(Subtract(Subtract(p,Times(C1D2,n)),C1),C0)),IntegerQ(Times(C2,p))))),
IIntegrate(6679,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,x_)),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(c,p),Integrate(Times(u,Power(Plus(C1,Times(d,x,Power(c,CN1))),p),Power(Plus(C1,Times(a,x)),Times(C1D2,n)),Power(Power(Subtract(C1,Times(a,x)),Times(C1D2,n)),CN1)),x)),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Subtract(Times(Sqr(a),Sqr(c)),Sqr(d)),C0),Or(IntegerQ(p),GtQ(c,C0))))),
IIntegrate(6680,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,x_)),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(u,Power(Plus(c,Times(d,x)),p),Power(Plus(C1,Times(a,x)),Times(C1D2,n)),Power(Power(Subtract(C1,Times(a,x)),Times(C1D2,n)),CN1)),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Subtract(Times(Sqr(a),Sqr(c)),Sqr(d)),C0),Not(Or(IntegerQ(p),GtQ(c,C0))))))
  );
}
