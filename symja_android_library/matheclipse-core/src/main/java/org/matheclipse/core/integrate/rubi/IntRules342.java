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
class IntRules342 { 
  public static IAST RULES = List( 
IIntegrate(6841,Integrate(Times(Plus(a_DEFAULT,Times(ArcTanh(u_),b_DEFAULT)),v_),x_Symbol),
    Condition(With(list(Set(w,IntHide(v,x))),Condition(Subtract(Simp(Dist(Plus(a,Times(b,ArcTanh(u))),w,x),x),Simp(Dist(b,Integrate(SimplifyIntegrand(Times(w,D(u,x),Power(Subtract(C1,Sqr(u)),CN1)),x),x),x),x)),InverseFunctionFreeQ(w,x))),And(FreeQ(list(a,b),x),InverseFunctionFreeQ(u,x),Not(MatchQ(v,Condition(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x)),m_DEFAULT),FreeQ(list(c,d,m),x)))),FalseQ(FunctionOfLinear(Times(v,Plus(a,Times(b,ArcTanh(u)))),x))))),
IIntegrate(6842,Integrate(Times(Plus(a_DEFAULT,Times(ArcCoth(u_),b_DEFAULT)),v_),x_Symbol),
    Condition(With(list(Set(w,IntHide(v,x))),Condition(Subtract(Simp(Dist(Plus(a,Times(b,ArcCoth(u))),w,x),x),Simp(Dist(b,Integrate(SimplifyIntegrand(Times(w,D(u,x),Power(Subtract(C1,Sqr(u)),CN1)),x),x),x),x)),InverseFunctionFreeQ(w,x))),And(FreeQ(list(a,b),x),InverseFunctionFreeQ(u,x),Not(MatchQ(v,Condition(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x)),m_DEFAULT),FreeQ(list(c,d,m),x)))),FalseQ(FunctionOfLinear(Times(v,Plus(a,Times(b,ArcCoth(u)))),x))))),
IIntegrate(6843,Integrate(ArcSech(Times(c_DEFAULT,x_)),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcSech(Times(c,x))),x),Simp(Dist(Times(Sqrt(Plus(C1,Times(c,x))),Sqrt(Power(Plus(C1,Times(c,x)),CN1))),Integrate(Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2),x),x),x)),FreeQ(c,x))),
IIntegrate(6844,Integrate(ArcCsch(Times(c_DEFAULT,x_)),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcCsch(Times(c,x))),x),Simp(Dist(Power(c,CN1),Integrate(Power(Times(x,Sqrt(Plus(C1,Power(Times(Sqr(c),Sqr(x)),CN1)))),CN1),x),x),x)),FreeQ(c,x))),
IIntegrate(6845,Integrate(Power(Plus(a_DEFAULT,Times(ArcSech(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),x_Symbol),
    Condition(Simp(Dist(Negate(Power(c,CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),n),Sech(x),Tanh(x)),x),x,ArcSech(Times(c,x))),x),x),And(FreeQ(List(a,b,c,n),x),IGtQ(n,C0)))),
IIntegrate(6846,Integrate(Power(Plus(a_DEFAULT,Times(ArcCsch(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),x_Symbol),
    Condition(Simp(Dist(Negate(Power(c,CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),n),Csch(x),Coth(x)),x),x,ArcCsch(Times(c,x))),x),x),And(FreeQ(List(a,b,c,n),x),IGtQ(n,C0)))),
IIntegrate(6847,Integrate(Times(Plus(a_DEFAULT,Times(ArcSech(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(x_,CN1)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Plus(a,Times(b,ArcCosh(Times(x,Power(c,CN1))))),Power(x,CN1)),x),x,Power(x,CN1))),FreeQ(list(a,b,c),x))),
IIntegrate(6848,Integrate(Times(Plus(a_DEFAULT,Times(ArcCsch(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(x_,CN1)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Plus(a,Times(b,ArcSinh(Times(x,Power(c,CN1))))),Power(x,CN1)),x),x,Power(x,CN1))),FreeQ(list(a,b,c),x))),
IIntegrate(6849,Integrate(Times(Plus(a_DEFAULT,Times(ArcSech(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(d,x),Plus(m,C1)),Plus(a,Times(b,ArcSech(Times(c,x)))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,Sqrt(Plus(C1,Times(c,x))),Power(Plus(m,C1),CN1),Sqrt(Power(Plus(C1,Times(c,x)),CN1))),Integrate(Times(Power(Times(d,x),m),Power(Times(Sqrt(Subtract(C1,Times(c,x))),Sqrt(Plus(C1,Times(c,x)))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1)))),
IIntegrate(6850,Integrate(Times(Plus(a_DEFAULT,Times(ArcCsch(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(d,x),Plus(m,C1)),Plus(a,Times(b,ArcCsch(Times(c,x)))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,d,Power(Times(c,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(d,x),Plus(m,CN1)),Power(Plus(C1,Power(Times(Sqr(c),Sqr(x)),CN1)),CN1D2)),x),x),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1)))),
IIntegrate(6851,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSech(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Negate(Power(Power(c,Plus(m,C1)),CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),n),Power(Sech(x),Plus(m,C1)),Tanh(x)),x),x,ArcSech(Times(c,x))),x),x),And(FreeQ(list(a,b,c),x),IntegerQ(n),IntegerQ(m),Or(GtQ(n,C0),LtQ(m,CN1))))),
IIntegrate(6852,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCsch(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Negate(Power(Power(c,Plus(m,C1)),CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),n),Power(Csch(x),Plus(m,C1)),Coth(x)),x),x,ArcCsch(Times(c,x))),x),x),And(FreeQ(list(a,b,c),x),IntegerQ(n),IntegerQ(m),Or(GtQ(n,C0),LtQ(m,CN1))))),
IIntegrate(6853,Integrate(Times(Plus(a_DEFAULT,Times(ArcSech(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Plus(Simp(Times(Plus(a,Times(b,ArcSech(Times(c,x)))),Log(Plus(C1,Times(Subtract(e,Sqrt(Plus(Times(CN1,Sqr(c),Sqr(d)),Sqr(e)))),Power(Times(c,d,Exp(ArcSech(Times(c,x)))),CN1)))),Power(e,CN1)),x),Simp(Times(Plus(a,Times(b,ArcSech(Times(c,x)))),Log(Plus(C1,Times(Plus(e,Sqrt(Plus(Times(CN1,Sqr(c),Sqr(d)),Sqr(e)))),Power(Times(c,d,Exp(ArcSech(Times(c,x)))),CN1)))),Power(e,CN1)),x),Negate(Simp(Times(Plus(a,Times(b,ArcSech(Times(c,x)))),Log(Plus(C1,Power(Exp(Times(C2,ArcSech(Times(c,x)))),CN1))),Power(e,CN1)),x)),Simp(Dist(Times(b,Power(e,CN1)),Integrate(Times(Sqrt(Times(Subtract(C1,Times(c,x)),Power(Plus(C1,Times(c,x)),CN1))),Log(Plus(C1,Times(Subtract(e,Sqrt(Plus(Times(CN1,Sqr(c),Sqr(d)),Sqr(e)))),Power(Times(c,d,Exp(ArcSech(Times(c,x)))),CN1)))),Power(Times(x,Subtract(C1,Times(c,x))),CN1)),x),x),x),Simp(Dist(Times(b,Power(e,CN1)),Integrate(Times(Sqrt(Times(Subtract(C1,Times(c,x)),Power(Plus(C1,Times(c,x)),CN1))),Log(Plus(C1,Times(Plus(e,Sqrt(Plus(Times(CN1,Sqr(c),Sqr(d)),Sqr(e)))),Power(Times(c,d,Exp(ArcSech(Times(c,x)))),CN1)))),Power(Times(x,Subtract(C1,Times(c,x))),CN1)),x),x),x),Negate(Simp(Dist(Times(b,Power(e,CN1)),Integrate(Times(Sqrt(Times(Subtract(C1,Times(c,x)),Power(Plus(C1,Times(c,x)),CN1))),Log(Plus(C1,Power(Exp(Times(C2,ArcSech(Times(c,x)))),CN1))),Power(Times(x,Subtract(C1,Times(c,x))),CN1)),x),x),x))),FreeQ(List(a,b,c,d,e),x))),
IIntegrate(6854,Integrate(Times(Plus(a_DEFAULT,Times(ArcSech(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Plus(a,Times(b,ArcSech(Times(c,x)))),Power(Times(e,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,Sqrt(Plus(C1,Times(c,x))),Power(Times(e,Plus(m,C1)),CN1),Sqrt(Power(Plus(C1,Times(c,x)),CN1))),Integrate(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Power(Times(x,Sqrt(Subtract(C1,Times(Sqr(c),Sqr(x))))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,m),x),NeQ(m,CN1)))),
IIntegrate(6855,Integrate(Times(Plus(a_DEFAULT,Times(ArcCsch(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Plus(Simp(Times(Plus(a,Times(b,ArcCsch(Times(c,x)))),Log(Subtract(C1,Times(Subtract(e,Sqrt(Plus(Times(Sqr(c),Sqr(d)),Sqr(e)))),Exp(ArcCsch(Times(c,x))),Power(Times(c,d),CN1)))),Power(e,CN1)),x),Simp(Times(Plus(a,Times(b,ArcCsch(Times(c,x)))),Log(Subtract(C1,Times(Plus(e,Sqrt(Plus(Times(Sqr(c),Sqr(d)),Sqr(e)))),Exp(ArcCsch(Times(c,x))),Power(Times(c,d),CN1)))),Power(e,CN1)),x),Negate(Simp(Times(Plus(a,Times(b,ArcCsch(Times(c,x)))),Log(Subtract(C1,Exp(Times(C2,ArcCsch(Times(c,x)))))),Power(e,CN1)),x)),Simp(Dist(Times(b,Power(Times(c,e),CN1)),Integrate(Times(Log(Subtract(C1,Times(Subtract(e,Sqrt(Plus(Times(Sqr(c),Sqr(d)),Sqr(e)))),Exp(ArcCsch(Times(c,x))),Power(Times(c,d),CN1)))),Power(Times(Sqr(x),Sqrt(Plus(C1,Power(Times(Sqr(c),Sqr(x)),CN1)))),CN1)),x),x),x),Simp(Dist(Times(b,Power(Times(c,e),CN1)),Integrate(Times(Log(Subtract(C1,Times(Plus(e,Sqrt(Plus(Times(Sqr(c),Sqr(d)),Sqr(e)))),Exp(ArcCsch(Times(c,x))),Power(Times(c,d),CN1)))),Power(Times(Sqr(x),Sqrt(Plus(C1,Power(Times(Sqr(c),Sqr(x)),CN1)))),CN1)),x),x),x),Negate(Simp(Dist(Times(b,Power(Times(c,e),CN1)),Integrate(Times(Log(Subtract(C1,Exp(Times(C2,ArcCsch(Times(c,x)))))),Power(Times(Sqr(x),Sqrt(Plus(C1,Power(Times(Sqr(c),Sqr(x)),CN1)))),CN1)),x),x),x))),FreeQ(List(a,b,c,d,e),x))),
IIntegrate(6856,Integrate(Times(Plus(a_DEFAULT,Times(ArcCsch(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Plus(a,Times(b,ArcCsch(Times(c,x)))),Power(Times(e,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,Power(Times(c,e,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Power(Times(Sqr(x),Sqrt(Plus(C1,Power(Times(Sqr(c),Sqr(x)),CN1)))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,m),x),NeQ(m,CN1)))),
IIntegrate(6857,Integrate(Times(Plus(a_DEFAULT,Times(ArcSech(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Power(Plus(d,Times(e,Sqr(x))),p),x))),Plus(Simp(Dist(Plus(a,Times(b,ArcSech(Times(c,x)))),u,x),x),Simp(Dist(Times(b,Sqrt(Plus(C1,Times(c,x))),Sqrt(Power(Plus(C1,Times(c,x)),CN1))),Integrate(SimplifyIntegrand(Times(u,Power(Times(x,Sqrt(Subtract(C1,Times(c,x))),Sqrt(Plus(C1,Times(c,x)))),CN1)),x),x),x),x))),And(FreeQ(List(a,b,c,d,e),x),Or(IGtQ(p,C0),ILtQ(Plus(p,C1D2),C0))))),
IIntegrate(6858,Integrate(Times(Plus(a_DEFAULT,Times(ArcCsch(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Power(Plus(d,Times(e,Sqr(x))),p),x))),Subtract(Simp(Dist(Plus(a,Times(b,ArcCsch(Times(c,x)))),u,x),x),Simp(Dist(Times(b,c,x,Power(Times(CN1,Sqr(c),Sqr(x)),CN1D2)),Integrate(SimplifyIntegrand(Times(u,Power(Times(x,Sqrt(Subtract(CN1,Times(Sqr(c),Sqr(x))))),CN1)),x),x),x),x))),And(FreeQ(List(a,b,c,d,e),x),Or(IGtQ(p,C0),ILtQ(Plus(p,C1D2),C0))))),
IIntegrate(6859,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSech(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Power(Plus(e,Times(d,Sqr(x))),p),Power(Plus(a,Times(b,ArcCosh(Times(x,Power(c,CN1))))),n),Power(Power(x,Times(C2,Plus(p,C1))),CN1)),x),x,Power(x,CN1))),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(n,C0),IntegerQ(p)))),
IIntegrate(6860,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCsch(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Power(Plus(e,Times(d,Sqr(x))),p),Power(Plus(a,Times(b,ArcSinh(Times(x,Power(c,CN1))))),n),Power(Power(x,Times(C2,Plus(p,C1))),CN1)),x),x,Power(x,CN1))),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(n,C0),IntegerQ(p))))
  );
}
