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
class IntRules358 { 
  public static IAST RULES = List( 
IIntegrate(7161,Integrate(PolyLog(n_,Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),p_DEFAULT))),x_Symbol),
    Condition(Plus(Simp(Times(x,PolyLog(n,Times(c,Power(Plus(a,Times(b,x)),p)))),x),Negate(Simp(Dist(p,Integrate(PolyLog(Plus(n,CN1),Times(c,Power(Plus(a,Times(b,x)),p))),x),x),x)),Simp(Dist(Times(a,p),Integrate(Times(PolyLog(Plus(n,CN1),Times(c,Power(Plus(a,Times(b,x)),p))),Power(Plus(a,Times(b,x)),CN1)),x),x),x)),And(FreeQ(List(a,b,c,p),x),GtQ(n,C0)))),
IIntegrate(7162,Integrate(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),CN1),PolyLog(C2,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(Plus(Simp(Times(Log(Subtract(Subtract(C1,Times(a,c)),Times(b,c,x))),PolyLog(C2,Times(c,Plus(a,Times(b,x)))),Power(e,CN1)),x),Simp(Dist(Times(b,Power(e,CN1)),Integrate(Times(Sqr(Log(Subtract(Subtract(C1,Times(a,c)),Times(b,c,x)))),Power(Plus(a,Times(b,x)),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(Times(c,Subtract(Times(b,d),Times(a,e))),e),C0)))),
IIntegrate(7163,Integrate(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),CN1),PolyLog(C2,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(Plus(Simp(Times(Log(Plus(d,Times(e,x))),PolyLog(C2,Times(c,Plus(a,Times(b,x)))),Power(e,CN1)),x),Simp(Dist(Times(b,Power(e,CN1)),Integrate(Times(Log(Plus(d,Times(e,x))),Log(Subtract(Subtract(C1,Times(a,c)),Times(b,c,x))),Power(Plus(a,Times(b,x)),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e),x),NeQ(Plus(Times(c,Subtract(Times(b,d),Times(a,e))),e),C0)))),
IIntegrate(7164,Integrate(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT),PolyLog(C2,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),PolyLog(C2,Times(c,Plus(a,Times(b,x)))),Power(Times(e,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,Power(Times(e,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Log(Subtract(Subtract(C1,Times(a,c)),Times(b,c,x))),Power(Plus(a,Times(b,x)),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,m),x),NeQ(m,CN1)))),
IIntegrate(7165,Integrate(Times(Power(x_,m_DEFAULT),PolyLog(n_,Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),p_DEFAULT)))),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Subtract(Power(a,Plus(m,C1)),Times(Power(b,Plus(m,C1)),Power(x,Plus(m,C1)))),PolyLog(n,Times(c,Power(Plus(a,Times(b,x)),p))),Power(Times(Plus(m,C1),Power(b,Plus(m,C1))),CN1)),x),Simp(Dist(Times(p,Power(Times(Plus(m,C1),Power(b,m)),CN1)),Integrate(ExpandIntegrand(PolyLog(Plus(n,CN1),Times(c,Power(Plus(a,Times(b,x)),p))),Times(Subtract(Power(a,Plus(m,C1)),Times(Power(b,Plus(m,C1)),Power(x,Plus(m,C1)))),Power(Plus(a,Times(b,x)),CN1)),x),x),x),x)),And(FreeQ(List(a,b,c,p),x),GtQ(n,C0),IntegerQ(m),NeQ(m,CN1)))),
IIntegrate(7166,Integrate(Times(Plus(g_DEFAULT,Times(Log(Times(f_DEFAULT,Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),n_DEFAULT))),h_DEFAULT)),PolyLog(C2,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(Plus(Simp(Times(x,Plus(g,Times(h,Log(Times(f,Power(Plus(d,Times(e,x)),n))))),PolyLog(C2,Times(c,Plus(a,Times(b,x))))),x),Simp(Dist(b,Integrate(Times(Plus(g,Times(h,Log(Times(f,Power(Plus(d,Times(e,x)),n))))),Log(Subtract(Subtract(C1,Times(a,c)),Times(b,c,x))),ExpandIntegrand(Times(x,Power(Plus(a,Times(b,x)),CN1)),x)),x),x),x),Negate(Simp(Dist(Times(e,h,n),Integrate(Times(PolyLog(C2,Times(c,Plus(a,Times(b,x)))),ExpandIntegrand(Times(x,Power(Plus(d,Times(e,x)),CN1)),x)),x),x),x))),FreeQ(List(a,b,c,d,e,f,g,h,n),x))),
IIntegrate(7167,Integrate(Times(Log(Plus(C1,Times(e_DEFAULT,x_))),Power(x_,CN1),PolyLog(C2,Times(c_DEFAULT,x_))),x_Symbol),
    Condition(Simp(Times(C1D2,CN1,Sqr(PolyLog(C2,Times(c,x)))),x),And(FreeQ(list(c,e),x),EqQ(Plus(c,e),C0)))),
IIntegrate(7168,Integrate(Times(Plus(Times(Log(Plus(C1,Times(e_DEFAULT,x_))),h_DEFAULT),g_),Power(x_,CN1),PolyLog(C2,Times(c_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Dist(g,Integrate(Times(PolyLog(C2,Times(c,x)),Power(x,CN1)),x),x),x),Simp(Dist(h,Integrate(Times(Log(Plus(C1,Times(e,x))),PolyLog(C2,Times(c,x)),Power(x,CN1)),x),x),x)),And(FreeQ(List(c,e,g,h),x),EqQ(Plus(c,e),C0)))),
IIntegrate(7169,Integrate(Times(Plus(g_DEFAULT,Times(Log(Times(f_DEFAULT,Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),n_DEFAULT))),h_DEFAULT)),Power(x_,m_DEFAULT),PolyLog(C2,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Plus(g,Times(h,Log(Times(f,Power(Plus(d,Times(e,x)),n))))),PolyLog(C2,Times(c,Plus(a,Times(b,x)))),Power(Plus(m,C1),CN1)),x),Simp(Dist(Times(b,Power(Plus(m,C1),CN1)),Integrate(ExpandIntegrand(Times(Plus(g,Times(h,Log(Times(f,Power(Plus(d,Times(e,x)),n))))),Log(Subtract(Subtract(C1,Times(a,c)),Times(b,c,x)))),Times(Power(x,Plus(m,C1)),Power(Plus(a,Times(b,x)),CN1)),x),x),x),x),Negate(Simp(Dist(Times(e,h,n,Power(Plus(m,C1),CN1)),Integrate(ExpandIntegrand(PolyLog(C2,Times(c,Plus(a,Times(b,x)))),Times(Power(x,Plus(m,C1)),Power(Plus(d,Times(e,x)),CN1)),x),x),x),x))),And(FreeQ(List(a,b,c,d,e,f,g,h,n),x),IntegerQ(m),NeQ(m,CN1)))),
IIntegrate(7170,Integrate(Times(Plus(g_DEFAULT,Times(Log(Times(f_DEFAULT,Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),n_DEFAULT))),h_DEFAULT)),$p("§px"),PolyLog(C2,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(With(list(Set(u,IntHide($s("§px"),x))),Plus(Simp(Times(u,Plus(g,Times(h,Log(Times(f,Power(Plus(d,Times(e,x)),n))))),PolyLog(C2,Times(c,Plus(a,Times(b,x))))),x),Simp(Dist(b,Integrate(ExpandIntegrand(Times(Plus(g,Times(h,Log(Times(f,Power(Plus(d,Times(e,x)),n))))),Log(Subtract(Subtract(C1,Times(a,c)),Times(b,c,x)))),Times(u,Power(Plus(a,Times(b,x)),CN1)),x),x),x),x),Negate(Simp(Dist(Times(e,h,n),Integrate(ExpandIntegrand(PolyLog(C2,Times(c,Plus(a,Times(b,x)))),Times(u,Power(Plus(d,Times(e,x)),CN1)),x),x),x),x)))),And(FreeQ(List(a,b,c,d,e,f,g,h,n),x),PolyQ($s("§px"),x)))),
IIntegrate(7171,Integrate(Times(Plus(g_DEFAULT,Times(Log(Plus(C1,Times(e_DEFAULT,x_))),h_DEFAULT)),$p("§px"),Power(x_,m_),PolyLog(C2,Times(c_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Dist(Coeff($s("§px"),x,Plus(Negate(m),CN1)),Integrate(Times(Plus(g,Times(h,Log(Plus(C1,Times(e,x))))),PolyLog(C2,Times(c,x)),Power(x,CN1)),x),x),x),Integrate(Times(Power(x,m),Subtract($s("§px"),Times(Coeff($s("§px"),x,Plus(Negate(m),CN1)),Power(x,Plus(Negate(m),CN1)))),Plus(g,Times(h,Log(Plus(C1,Times(e,x))))),PolyLog(C2,Times(c,x))),x)),And(FreeQ(List(c,e,g,h),x),PolyQ($s("§px"),x),ILtQ(m,C0),EqQ(Plus(c,e),C0),NeQ(Coeff($s("§px"),x,Plus(Negate(m),CN1)),C0)))),
IIntegrate(7172,Integrate(Times(Plus(g_DEFAULT,Times(Log(Times(f_DEFAULT,Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),n_DEFAULT))),h_DEFAULT)),$p("§px"),Power(x_,m_DEFAULT),PolyLog(C2,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times(Power(x,m),$s("§px")),x))),Plus(Simp(Times(u,Plus(g,Times(h,Log(Times(f,Power(Plus(d,Times(e,x)),n))))),PolyLog(C2,Times(c,Plus(a,Times(b,x))))),x),Simp(Dist(b,Integrate(ExpandIntegrand(Times(Plus(g,Times(h,Log(Times(f,Power(Plus(d,Times(e,x)),n))))),Log(Subtract(Subtract(C1,Times(a,c)),Times(b,c,x)))),Times(u,Power(Plus(a,Times(b,x)),CN1)),x),x),x),x),Negate(Simp(Dist(Times(e,h,n),Integrate(ExpandIntegrand(PolyLog(C2,Times(c,Plus(a,Times(b,x)))),Times(u,Power(Plus(d,Times(e,x)),CN1)),x),x),x),x)))),And(FreeQ(List(a,b,c,d,e,f,g,h,n),x),PolyQ($s("§px"),x),IntegerQ(m)))),
IIntegrate(7173,Integrate(Times(Plus(g_DEFAULT,Times(Log(Times(f_DEFAULT,Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),n_DEFAULT))),h_DEFAULT)),$p("§px",true),Power(x_,m_),PolyLog(C2,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(Unintegrable(Times($s("§px"),Power(x,m),Plus(g,Times(h,Log(Times(f,Power(Plus(d,Times(e,x)),n))))),PolyLog(C2,Times(c,Plus(a,Times(b,x))))),x),And(FreeQ(List(a,b,c,d,e,f,g,h,m,n),x),PolyQ($s("§px"),x)))),
IIntegrate(7174,Integrate(PolyLog(n_,Times(d_DEFAULT,Power(Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),p_DEFAULT))),x_Symbol),
    Condition(Simp(Times(PolyLog(Plus(n,C1),Times(d,Power(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),p))),Power(Times(b,c,p,Log(FSymbol)),CN1)),x),FreeQ(List(FSymbol,a,b,c,d,n,p),x))),
IIntegrate(7175,Integrate(Times(Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),PolyLog(n_,Times(d_DEFAULT,Power(Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),p_DEFAULT)))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(e,Times(f,x)),m),PolyLog(Plus(n,C1),Times(d,Power(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),p))),Power(Times(b,c,p,Log(FSymbol)),CN1)),x),Simp(Dist(Times(f,m,Power(Times(b,c,p,Log(FSymbol)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,CN1)),PolyLog(Plus(n,C1),Times(d,Power(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),p)))),x),x),x)),And(FreeQ(List(FSymbol,a,b,c,d,e,f,n,p),x),GtQ(m,C0)))),
IIntegrate(7176,Integrate(PolyLog(C2,u_),x_Symbol),
    Condition(With(list(Set(v,SimplifyIntegrand(Times(x,D(u,x),Log(Subtract(C1,u)),Power(u,CN1)),x))),Plus(Times(x,PolyLog(C2,u)),Integrate(v,x))),With(list(Set(w,Together(u))),And(RationalFunctionQ(w,x),Not(FreeQ(w,x)),LessEqual(C0,Expon(Numerator(w),x),C2),LessEqual(C0,Expon(Denominator(w),x),C2))))),
IIntegrate(7177,Integrate(Times(u_,PolyLog(n_,v_)),x_Symbol),
    Condition(With(list(Set(w,DerivativeDivides(v,Times(u,v),x))),Condition(Simp(Times(w,PolyLog(Plus(n,C1),v)),x),Not(FalseQ(w)))),FreeQ(n,x))),
IIntegrate(7178,Integrate(Times(Log(w_),u_,PolyLog(n_,v_)),x_Symbol),
    Condition(With(list(Set(z,DerivativeDivides(v,Times(u,v),x))),Condition(Subtract(Simp(Times(z,Log(w),PolyLog(Plus(n,C1),v)),x),Integrate(SimplifyIntegrand(Times(z,D(w,x),PolyLog(Plus(n,C1),v),Power(w,CN1)),x),x)),Not(FalseQ(z)))),And(FreeQ(n,x),InverseFunctionFreeQ(w,x)))),
IIntegrate(7179,Integrate(Power(Times(c_DEFAULT,ProductLog(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),p_),x_Symbol),
    Condition(Plus(Simp(Times(Plus(a,Times(b,x)),Power(Times(c,ProductLog(Plus(a,Times(b,x)))),p),Power(Times(b,Plus(p,C1)),CN1)),x),Simp(Dist(Times(p,Power(Times(c,Plus(p,C1)),CN1)),Integrate(Times(Power(Times(c,ProductLog(Plus(a,Times(b,x)))),Plus(p,C1)),Power(Plus(C1,ProductLog(Plus(a,Times(b,x)))),CN1)),x),x),x)),And(FreeQ(list(a,b,c),x),LtQ(p,CN1)))),
IIntegrate(7180,Integrate(Power(Times(c_DEFAULT,ProductLog(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),p_DEFAULT),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),Power(Times(c,ProductLog(Plus(a,Times(b,x)))),p),Power(b,CN1)),x),Simp(Dist(p,Integrate(Times(Power(Times(c,ProductLog(Plus(a,Times(b,x)))),p),Power(Plus(C1,ProductLog(Plus(a,Times(b,x)))),CN1)),x),x),x)),And(FreeQ(list(a,b,c),x),Not(LtQ(p,CN1)))))
  );
}
