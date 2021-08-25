package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class IntRules306 { 
  public static IAST RULES = List( 
IIntegrate(6121,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power(Plus(A_DEFAULT,Times(B_DEFAULT,x_),Times(C_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(Dist(Power(d,CN1),Subst(Integrate(Times(Power(Plus(Times(Subtract(Times(d,e),Times(c,f)),Power(d,CN1)),Times(f,x,Power(d,CN1))),m),Power(Plus(Times(CN1,CSymbol,Power(d,CN2)),Times(CSymbol,Sqr(x),Power(d,CN2))),q),Power(Plus(a,Times(b,ArcTanh(x))),p)),x),x,Plus(c,Times(d,x))),x),And(FreeQ(List(a,b,c,d,e,f,ASymbol,BSymbol,CSymbol,m,p,q),x),EqQ(Plus(Times(BSymbol,Subtract(C1,Sqr(c))),Times(C2,ASymbol,c,d)),C0),EqQ(Subtract(Times(C2,c,CSymbol),Times(BSymbol,d)),C0)))),
IIntegrate(6122,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power(Plus(A_DEFAULT,Times(B_DEFAULT,x_),Times(C_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(Dist(Power(d,CN1),Subst(Integrate(Times(Power(Plus(Times(Subtract(Times(d,e),Times(c,f)),Power(d,CN1)),Times(f,x,Power(d,CN1))),m),Power(Plus(Times(CN1,CSymbol,Power(d,CN2)),Times(CSymbol,Sqr(x),Power(d,CN2))),q),Power(Plus(a,Times(b,ArcCoth(x))),p)),x),x,Plus(c,Times(d,x))),x),And(FreeQ(List(a,b,c,d,e,f,ASymbol,BSymbol,CSymbol,m,p,q),x),EqQ(Plus(Times(BSymbol,Subtract(C1,Sqr(c))),Times(C2,ASymbol,c,d)),C0),EqQ(Subtract(Times(C2,c,CSymbol),Times(BSymbol,d)),C0)))),
IIntegrate(6123,Integrate(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Plus(C1,Times(a,x)),Times(C1D2,Plus(n,C1))),Power(Times(Power(Subtract(C1,Times(a,x)),Times(C1D2,Subtract(n,C1))),Sqrt(Subtract(C1,Times(Sqr(a),Sqr(x))))),CN1)),x),And(FreeQ(a,x),IntegerQ(Times(C1D2,Subtract(n,C1)))))),
IIntegrate(6124,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(x,m),Power(Plus(C1,Times(a,x)),Times(C1D2,Plus(n,C1))),Power(Times(Power(Subtract(C1,Times(a,x)),Times(C1D2,Subtract(n,C1))),Sqrt(Subtract(C1,Times(Sqr(a),Sqr(x))))),CN1)),x),And(FreeQ(List(a,m),x),IntegerQ(Times(C1D2,Subtract(n,C1)))))),
IIntegrate(6125,Integrate(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),x_Symbol),
    Condition(Integrate(Times(Power(Plus(C1,Times(a,x)),Times(C1D2,n)),Power(Power(Subtract(C1,Times(a,x)),Times(C1D2,n)),CN1)),x),And(FreeQ(List(a,n),x),Not(IntegerQ(Times(C1D2,Subtract(n,C1))))))),
IIntegrate(6126,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(x,m),Power(Plus(C1,Times(a,x)),Times(C1D2,n)),Power(Power(Subtract(C1,Times(a,x)),Times(C1D2,n)),CN1)),x),And(FreeQ(List(a,m,n),x),Not(IntegerQ(Times(C1D2,Subtract(n,C1))))))),
IIntegrate(6127,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,x_)),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(c,n),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(p,n)),Power(Subtract(C1,Times(Sqr(a),Sqr(x))),Times(C1D2,n))),x),x),And(FreeQ(List(a,c,d,p),x),EqQ(Plus(Times(a,c),d),C0),IntegerQ(Times(C1D2,Subtract(n,C1))),IntegerQ(Times(C2,p))))),
IIntegrate(6128,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,x_)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(c,n),Integrate(Times(Power(Plus(e,Times(f,x)),m),Power(Plus(c,Times(d,x)),Subtract(p,n)),Power(Subtract(C1,Times(Sqr(a),Sqr(x))),Times(C1D2,n))),x),x),And(FreeQ(List(a,c,d,e,f,m,p),x),EqQ(Plus(Times(a,c),d),C0),IntegerQ(Times(C1D2,Subtract(n,C1))),Or(IntegerQ(p),EqQ(p,Times(C1D2,n)),EqQ(Subtract(Subtract(p,Times(C1D2,n)),C1),C0)),IntegerQ(Times(C2,p))))),
IIntegrate(6129,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,x_)),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(c,p),Integrate(Times(u,Power(Plus(C1,Times(d,x,Power(c,CN1))),p),Power(Plus(C1,Times(a,x)),Times(C1D2,n)),Power(Power(Subtract(C1,Times(a,x)),Times(C1D2,n)),CN1)),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Subtract(Times(Sqr(a),Sqr(c)),Sqr(d)),C0),Or(IntegerQ(p),GtQ(c,C0))))),
IIntegrate(6130,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,x_)),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(u,Power(Plus(c,Times(d,x)),p),Power(Plus(C1,Times(a,x)),Times(C1D2,n)),Power(Power(Subtract(C1,Times(a,x)),Times(C1D2,n)),CN1)),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Subtract(Times(Sqr(a),Sqr(c)),Sqr(d)),C0),Not(Or(IntegerQ(p),GtQ(c,C0)))))),
IIntegrate(6131,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN1))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(d,p),Integrate(Times(u,Power(Plus(C1,Times(c,x,Power(d,CN1))),p),Exp(Times(n,ArcTanh(Times(a,x)))),Power(Power(x,p),CN1)),x),x),And(FreeQ(List(a,c,d,n),x),EqQ(Subtract(Sqr(c),Times(Sqr(a),Sqr(d))),C0),IntegerQ(p)))),
IIntegrate(6132,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN1))),p_)),x_Symbol),
    Condition(Dist(Times(Power(CN1,Times(C1D2,n)),Power(c,p)),Integrate(Times(u,Power(Plus(C1,Times(d,Power(Times(c,x),CN1))),p),Power(Plus(C1,Power(Times(a,x),CN1)),Times(C1D2,n)),Power(Power(Subtract(C1,Power(Times(a,x),CN1)),Times(C1D2,n)),CN1)),x),x),And(FreeQ(List(a,c,d,p),x),EqQ(Subtract(Sqr(c),Times(Sqr(a),Sqr(d))),C0),Not(IntegerQ(p)),IntegerQ(Times(C1D2,n)),GtQ(c,C0)))),
IIntegrate(6133,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN1))),p_)),x_Symbol),
    Condition(Integrate(Times(u,Power(Plus(c,Times(d,Power(x,CN1))),p),Power(Plus(C1,Times(a,x)),Times(C1D2,n)),Power(Power(Subtract(C1,Times(a,x)),Times(C1D2,n)),CN1)),x),And(FreeQ(List(a,c,d,p),x),EqQ(Subtract(Sqr(c),Times(Sqr(a),Sqr(d))),C0),Not(IntegerQ(p)),IntegerQ(Times(C1D2,n)),Not(GtQ(c,C0))))),
IIntegrate(6134,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN1))),p_)),x_Symbol),
    Condition(Dist(Times(Power(x,p),Power(Plus(c,Times(d,Power(x,CN1))),p),Power(Power(Plus(C1,Times(c,x,Power(d,CN1))),p),CN1)),Integrate(Times(u,Power(Plus(C1,Times(c,x,Power(d,CN1))),p),Exp(Times(n,ArcTanh(Times(a,x)))),Power(Power(x,p),CN1)),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Subtract(Sqr(c),Times(Sqr(a),Sqr(d))),C0),Not(IntegerQ(p))))),
IIntegrate(6135,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),QQ(-3L,2L))),x_Symbol),
    Condition(Simp(Times(Subtract(n,Times(a,x)),Exp(Times(n,ArcTanh(Times(a,x)))),Power(Times(a,c,Subtract(Sqr(n),C1),Sqrt(Plus(c,Times(d,Sqr(x))))),CN1)),x),And(FreeQ(List(a,c,d,n),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Not(IntegerQ(n))))),
IIntegrate(6136,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(n,Times(C2,a,Plus(p,C1),x)),Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcTanh(Times(a,x)))),Power(Times(a,c,Subtract(Sqr(n),Times(C4,Sqr(Plus(p,C1))))),CN1)),x),Dist(Times(C2,Plus(p,C1),Plus(Times(C2,p),C3),Power(Times(c,Subtract(Sqr(n),Times(C4,Sqr(Plus(p,C1))))),CN1)),Integrate(Times(Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcTanh(Times(a,x))))),x),x)),And(FreeQ(List(a,c,d,n),x),EqQ(Plus(Times(Sqr(a),c),d),C0),LtQ(p,CN1),Not(IntegerQ(n)),NeQ(Subtract(Sqr(n),Times(C4,Sqr(Plus(p,C1)))),C0),IntegerQ(Times(C2,p))))),
IIntegrate(6137,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Simp(Times(Exp(Times(n,ArcTanh(Times(a,x)))),Power(Times(a,c,n),CN1)),x),And(FreeQ(List(a,c,d,n),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Not(IntegerQ(Times(C1D2,n)))))),
IIntegrate(6138,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(c,p),Integrate(Times(Power(Subtract(C1,Times(Sqr(a),Sqr(x))),Subtract(p,Times(C1D2,n))),Power(Plus(C1,Times(a,x)),n)),x),x),And(FreeQ(List(a,c,d,p),x),EqQ(Plus(Times(Sqr(a),c),d),C0),IntegerQ(p),IGtQ(Times(C1D2,Plus(n,C1)),C0),Not(IntegerQ(Subtract(p,Times(C1D2,n))))))),
IIntegrate(6139,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(c,p),Integrate(Times(Power(Subtract(C1,Times(Sqr(a),Sqr(x))),Plus(p,Times(C1D2,n))),Power(Power(Subtract(C1,Times(a,x)),n),CN1)),x),x),And(FreeQ(List(a,c,d,p),x),EqQ(Plus(Times(Sqr(a),c),d),C0),IntegerQ(p),ILtQ(Times(C1D2,Subtract(n,C1)),C0),Not(IntegerQ(Subtract(p,Times(C1D2,n))))))),
IIntegrate(6140,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(c,p),Integrate(Times(Power(Subtract(C1,Times(a,x)),Subtract(p,Times(C1D2,n))),Power(Plus(C1,Times(a,x)),Plus(p,Times(C1D2,n)))),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Or(IntegerQ(p),GtQ(c,C0)))))
  );
}
