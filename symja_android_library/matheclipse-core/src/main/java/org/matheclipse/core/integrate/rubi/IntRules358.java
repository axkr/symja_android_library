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
class IntRules358 { 
  public static IAST RULES = List( 
IIntegrate(7161,Integrate(Times(Plus(g_DEFAULT,Times(Log(Times(f_DEFAULT,Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),n_DEFAULT))),h_DEFAULT)),$p("§px",true),Power(x_,m_),PolyLog(C2,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(Unintegrable(Times($s("§px"),Power(x,m),Plus(g,Times(h,Log(Times(f,Power(Plus(d,Times(e,x)),n))))),PolyLog(C2,Times(c,Plus(a,Times(b,x))))),x),And(FreeQ(List(a,b,c,d,e,f,g,h,m,n),x),PolyQ($s("§px"),x)))),
IIntegrate(7162,Integrate(PolyLog(n_,Times(d_DEFAULT,Power(Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),p_DEFAULT))),x_Symbol),
    Condition(Simp(Times(PolyLog(Plus(n,C1),Times(d,Power(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),p))),Power(Times(b,c,p,Log(FSymbol)),CN1)),x),FreeQ(List(FSymbol,a,b,c,d,n,p),x))),
IIntegrate(7163,Integrate(Times(Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),PolyLog(n_,Times(d_DEFAULT,Power(Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),p_DEFAULT)))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(e,Times(f,x)),m),PolyLog(Plus(n,C1),Times(d,Power(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),p))),Power(Times(b,c,p,Log(FSymbol)),CN1)),x),Simp(Star(Times(f,m,Power(Times(b,c,p,Log(FSymbol)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Subtract(m,C1)),PolyLog(Plus(n,C1),Times(d,Power(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),p)))),x)),x)),And(FreeQ(List(FSymbol,a,b,c,d,e,f,n,p),x),GtQ(m,C0)))),
IIntegrate(7164,Integrate(Times(u_,PolyLog(n_,v_)),x_Symbol),
    Condition(With(list(Set(w,DerivativeDivides(v,Times(u,v),x))),Condition(Simp(Times(w,PolyLog(Plus(n,C1),v)),x),Not(FalseQ(w)))),FreeQ(n,x))),
IIntegrate(7165,Integrate(Times(Log(w_),u_,PolyLog(n_,v_)),x_Symbol),
    Condition(With(list(Set(z,DerivativeDivides(v,Times(u,v),x))),Condition(Subtract(Simp(Times(z,Log(w),PolyLog(Plus(n,C1),v)),x),Integrate(SimplifyIntegrand(Times(z,D(w,x),PolyLog(Plus(n,C1),v),Power(w,CN1)),x),x)),Not(FalseQ(z)))),And(FreeQ(n,x),InverseFunctionFreeQ(w,x)))),
IIntegrate(7166,Integrate(Power(Times(c_DEFAULT,ProductLog(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),p_),x_Symbol),
    Condition(Plus(Simp(Times(Plus(a,Times(b,x)),Power(Times(c,ProductLog(Plus(a,Times(b,x)))),p),Power(Times(b,Plus(p,C1)),CN1)),x),Simp(Star(Times(p,Power(Times(c,Plus(p,C1)),CN1)),Integrate(Times(Power(Times(c,ProductLog(Plus(a,Times(b,x)))),Plus(p,C1)),Power(Plus(C1,ProductLog(Plus(a,Times(b,x)))),CN1)),x)),x)),And(FreeQ(list(a,b,c),x),LtQ(p,CN1)))),
IIntegrate(7167,Integrate(Power(Times(c_DEFAULT,ProductLog(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),p_DEFAULT),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),Power(Times(c,ProductLog(Plus(a,Times(b,x)))),p),Power(b,CN1)),x),Simp(Star(p,Integrate(Times(Power(Times(c,ProductLog(Plus(a,Times(b,x)))),p),Power(Plus(C1,ProductLog(Plus(a,Times(b,x)))),CN1)),x)),x)),And(FreeQ(list(a,b,c),x),Not(LtQ(p,CN1))))),
IIntegrate(7168,Integrate(Times(Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power(Times(c_DEFAULT,ProductLog(Plus(a_,Times(b_DEFAULT,x_)))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Power(b,Plus(m,C1)),CN1),Subst(Integrate(ExpandIntegrand(Power(Times(c,ProductLog(x)),p),Power(Plus(Times(b,e),Times(CN1,a,f),Times(f,x)),m),x),x),x,Plus(a,Times(b,x)))),x),And(FreeQ(List(a,b,c,e,f,p),x),IGtQ(m,C0)))),
IIntegrate(7169,Integrate(Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_)))),p_DEFAULT),x_Symbol),
    Condition(Subtract(Simp(Times(x,Power(Times(c,ProductLog(Times(a,Power(x,n)))),p)),x),Simp(Star(Times(n,p),Integrate(Times(Power(Times(c,ProductLog(Times(a,Power(x,n)))),p),Power(Plus(C1,ProductLog(Times(a,Power(x,n)))),CN1)),x)),x)),And(FreeQ(List(a,c,n,p),x),Or(EqQ(Times(n,Subtract(p,C1)),CN1),And(IntegerQ(Subtract(p,C1D2)),EqQ(Times(n,Subtract(p,C1D2)),CN1)))))),
IIntegrate(7170,Integrate(Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_)))),p_DEFAULT),x_Symbol),
    Condition(Plus(Simp(Times(x,Power(Times(c,ProductLog(Times(a,Power(x,n)))),p),Power(Plus(Times(n,p),C1),CN1)),x),Simp(Star(Times(n,p,Power(Times(c,Plus(Times(n,p),C1)),CN1)),Integrate(Times(Power(Times(c,ProductLog(Times(a,Power(x,n)))),Plus(p,C1)),Power(Plus(C1,ProductLog(Times(a,Power(x,n)))),CN1)),x)),x)),And(FreeQ(list(a,c,n),x),Or(And(IntegerQ(p),EqQ(Times(n,Plus(p,C1)),CN1)),And(IntegerQ(Subtract(p,C1D2)),EqQ(Times(n,Plus(p,C1D2)),CN1)))))),
IIntegrate(7171,Integrate(Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_)))),p_DEFAULT),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Power(Times(c,ProductLog(Times(a,Power(Power(x,n),CN1)))),p),Power(x,CN2)),x),x,Power(x,CN1))),And(FreeQ(list(a,c,p),x),ILtQ(n,C0)))),
IIntegrate(7172,Integrate(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Power(Times(c,ProductLog(Times(a,Power(x,n)))),p),Power(Plus(m,C1),CN1)),x),Simp(Star(Times(n,p,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,m),Power(Times(c,ProductLog(Times(a,Power(x,n)))),p),Power(Plus(C1,ProductLog(Times(a,Power(x,n)))),CN1)),x)),x)),And(FreeQ(List(a,c,m,n,p),x),NeQ(m,CN1),Or(And(IntegerQ(Subtract(p,C1D2)),IGtQ(Times(C2,Simplify(Plus(p,Times(Plus(m,C1),Power(n,CN1))))),C0)),And(Not(IntegerQ(Subtract(p,C1D2))),IGtQ(Plus(Simplify(Plus(p,Times(Plus(m,C1),Power(n,CN1)))),C1),C0)))))),
IIntegrate(7173,Integrate(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Power(Times(c,ProductLog(Times(a,Power(x,n)))),p),Power(Plus(m,Times(n,p),C1),CN1)),x),Simp(Star(Times(n,p,Power(Times(c,Plus(m,Times(n,p),C1)),CN1)),Integrate(Times(Power(x,m),Power(Times(c,ProductLog(Times(a,Power(x,n)))),Plus(p,C1)),Power(Plus(C1,ProductLog(Times(a,Power(x,n)))),CN1)),x)),x)),And(FreeQ(List(a,c,m,n,p),x),Or(EqQ(m,CN1),And(IntegerQ(Subtract(p,C1D2)),ILtQ(Subtract(Simplify(Plus(p,Times(Plus(m,C1),Power(n,CN1)))),C1D2),C0)),And(Not(IntegerQ(Subtract(p,C1D2))),ILtQ(Simplify(Plus(p,Times(Plus(m,C1),Power(n,CN1)))),C0)))))),
IIntegrate(7174,Integrate(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,x_))),p_DEFAULT)),x_Symbol),
    Condition(Plus(Integrate(Times(Power(x,m),Power(Times(c,ProductLog(Times(a,x))),p),Power(Plus(C1,ProductLog(Times(a,x))),CN1)),x),Simp(Star(Power(c,CN1),Integrate(Times(Power(x,m),Power(Times(c,ProductLog(Times(a,x))),Plus(p,C1)),Power(Plus(C1,ProductLog(Times(a,x))),CN1)),x)),x)),FreeQ(list(a,c,m),x))),
IIntegrate(7175,Integrate(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_)))),p_DEFAULT)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Power(Times(c,ProductLog(Times(a,Power(Power(x,n),CN1)))),p),Power(Power(x,Plus(m,C2)),CN1)),x),x,Power(x,CN1))),And(FreeQ(list(a,c,p),x),ILtQ(n,C0),IntegerQ(m),NeQ(m,CN1)))),
IIntegrate(7176,Integrate(Power(Plus(d_,Times(d_DEFAULT,ProductLog(Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),CN1),x_Symbol),
    Condition(Simp(Times(Plus(a,Times(b,x)),Power(Times(b,d,ProductLog(Plus(a,Times(b,x)))),CN1)),x),FreeQ(list(a,b,d),x))),
IIntegrate(7177,Integrate(Times(ProductLog(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(d,x),x),Integrate(Power(Plus(d,Times(d,ProductLog(Plus(a,Times(b,x))))),CN1),x)),FreeQ(list(a,b,d),x))),
IIntegrate(7178,Integrate(Times(Power(Times(c_DEFAULT,ProductLog(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),p_),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(c,Plus(a,Times(b,x)),Power(Times(c,ProductLog(Plus(a,Times(b,x)))),Subtract(p,C1)),Power(Times(b,d),CN1)),x),Simp(Star(Times(c,p),Integrate(Times(Power(Times(c,ProductLog(Plus(a,Times(b,x)))),Subtract(p,C1)),Power(Plus(d,Times(d,ProductLog(Plus(a,Times(b,x))))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d),x),GtQ(p,C0)))),
IIntegrate(7179,Integrate(Times(Power(ProductLog(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),CN1),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),CN1)),x_Symbol),
    Condition(Simp(Times(ExpIntegralEi(ProductLog(Plus(a,Times(b,x)))),Power(Times(b,d),CN1)),x),FreeQ(list(a,b,d),x))),
IIntegrate(7180,Integrate(Times(Power(Times(c_DEFAULT,ProductLog(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),CN1D2),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),CN1)),x_Symbol),
    Condition(Simp(Times(Rt(Times(Pi,c),C2),Erfi(Times(Sqrt(Times(c,ProductLog(Plus(a,Times(b,x))))),Power(Rt(c,C2),CN1))),Power(Times(b,c,d),CN1)),x),And(FreeQ(List(a,b,c,d),x),PosQ(c))))
  );
}
