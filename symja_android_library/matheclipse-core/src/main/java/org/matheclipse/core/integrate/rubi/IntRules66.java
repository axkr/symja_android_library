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
class IntRules66 { 
  public static IAST RULES = List( 
IIntegrate(1321,Integrate(Times(Sqrt(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Power(Plus(d_,Times(f_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Star(Times(c,Power(f,CN1)),Integrate(Power(Plus(a,Times(b,x),Times(c,Sqr(x))),CN1D2),x)),x),Simp(Star(Power(f,CN1),Integrate(Times(Subtract(Subtract(Times(c,d),Times(a,f)),Times(b,f,x)),Power(Times(Sqrt(Plus(a,Times(b,x),Times(c,Sqr(x)))),Plus(d,Times(f,Sqr(x)))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,f),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0)))),
IIntegrate(1322,Integrate(Times(Sqrt(Plus(a_,Times(c_DEFAULT,Sqr(x_)))),Power(Plus(d_,Times(e_DEFAULT,x_),Times(f_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Star(Times(c,Power(f,CN1)),Integrate(Power(Plus(a,Times(c,Sqr(x))),CN1D2),x)),x),Simp(Star(Power(f,CN1),Integrate(Times(Plus(Times(c,d),Times(CN1,a,f),Times(c,e,x)),Power(Times(Sqrt(Plus(a,Times(c,Sqr(x)))),Plus(d,Times(e,x),Times(f,Sqr(x)))),CN1)),x)),x)),And(FreeQ(List(a,c,d,e,f),x),NeQ(Subtract(Sqr(e),Times(C4,d,f)),C0)))),
IIntegrate(1323,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),CN1D2),Power(Plus(d_,Times(e_DEFAULT,x_),Times(f_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(With(list(Set(r,Rt(Subtract(Sqr(b),Times(C4,a,c)),C2))),Simp(Star(Times(Sqrt(Plus(b,r,Times(C2,c,x))),Sqrt(Plus(Times(C2,a),Times(Plus(b,r),x))),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),CN1D2)),Integrate(Power(Times(Sqrt(Plus(b,r,Times(C2,c,x))),Sqrt(Plus(Times(C2,a),Times(Plus(b,r),x))),Sqrt(Plus(d,Times(e,x),Times(f,Sqr(x))))),CN1),x)),x)),And(FreeQ(List(a,b,c,d,e,f),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),NeQ(Subtract(Sqr(e),Times(C4,d,f)),C0)))),
IIntegrate(1324,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),CN1D2),Power(Plus(d_,Times(f_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(With(list(Set(r,Rt(Subtract(Sqr(b),Times(C4,a,c)),C2))),Simp(Star(Times(Sqrt(Plus(b,r,Times(C2,c,x))),Sqrt(Plus(Times(C2,a),Times(Plus(b,r),x))),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),CN1D2)),Integrate(Power(Times(Sqrt(Plus(b,r,Times(C2,c,x))),Sqrt(Plus(Times(C2,a),Times(Plus(b,r),x))),Sqrt(Plus(d,Times(f,Sqr(x))))),CN1),x)),x)),And(FreeQ(List(a,b,c,d,f),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0)))),
IIntegrate(1325,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_),Times(f_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(a,Times(b,x),Times(c,Sqr(x))),p),Power(Plus(d,Times(e,x),Times(f,Sqr(x))),q)),x),And(FreeQ(List(a,b,c,d,e,f,p,q),x),Not(IGtQ(p,C0)),Not(IGtQ(q,C0))))),
IIntegrate(1326,Integrate(Times(Power(Plus(a_,Times(c_DEFAULT,Sqr(x_))),p_),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_),Times(f_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(a,Times(c,Sqr(x))),p),Power(Plus(d,Times(e,x),Times(f,Sqr(x))),q)),x),And(FreeQ(List(a,c,d,e,f,p,q),x),Not(IGtQ(p,C0)),Not(IGtQ(q,C0))))),
IIntegrate(1327,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,u_),Times(c_DEFAULT,Sqr(u_))),p_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,u_),Times(f_DEFAULT,Sqr(u_))),q_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Coefficient(u,x,C1),CN1),Subst(Integrate(Times(Power(Plus(a,Times(b,x),Times(c,Sqr(x))),p),Power(Plus(d,Times(e,x),Times(f,Sqr(x))),q)),x),x,u)),x),And(FreeQ(List(a,b,c,d,e,f,p,q),x),LinearQ(u,x),NeQ(u,x)))),
IIntegrate(1328,Integrate(Times(Power(Plus(a_DEFAULT,Times(c_DEFAULT,Sqr(u_))),p_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,u_),Times(f_DEFAULT,Sqr(u_))),q_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Coefficient(u,x,C1),CN1),Subst(Integrate(Times(Power(Plus(a,Times(c,Sqr(x))),p),Power(Plus(d,Times(e,x),Times(f,Sqr(x))),q)),x),x,u)),x),And(FreeQ(List(a,c,d,e,f,p,q),x),LinearQ(u,x),NeQ(u,x)))),
IIntegrate(1329,Integrate(Times(Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_),Power(Plus(d_,Times(e_DEFAULT,x_),Times(f_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Simp(Star(Power(Times(c,Power(f,CN1)),p),Integrate(Times(Power(Plus(g,Times(h,x)),m),Power(Plus(d,Times(e,x),Times(f,Sqr(x))),Plus(p,q))),x)),x),And(FreeQ(List(a,b,c,d,e,f,g,h,p,q),x),EqQ(Subtract(Times(c,d),Times(a,f)),C0),EqQ(Subtract(Times(b,d),Times(a,e)),C0),Or(IntegerQ(p),GtQ(Times(c,Power(f,CN1)),C0)),Or(Not(IntegerQ(q)),LessEqual(LeafCount(Plus(d,Times(e,x),Times(f,Sqr(x)))),LeafCount(Plus(a,Times(b,x),Times(c,Sqr(x))))))))),
IIntegrate(1330,Integrate(Times(Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_),Power(Plus(d_,Times(e_DEFAULT,x_),Times(f_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Simp(Star(Times(Power(a,IntPart(p)),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),FracPart(p)),Power(Times(Power(d,IntPart(p)),Power(Plus(d,Times(e,x),Times(f,Sqr(x))),FracPart(p))),CN1)),Integrate(Times(Power(Plus(g,Times(h,x)),m),Power(Plus(d,Times(e,x),Times(f,Sqr(x))),Plus(p,q))),x)),x),And(FreeQ(List(a,b,c,d,e,f,g,h,p,q),x),EqQ(Subtract(Times(c,d),Times(a,f)),C0),EqQ(Subtract(Times(b,d),Times(a,e)),C0),Not(IntegerQ(p)),Not(IntegerQ(q)),Not(GtQ(Times(c,Power(f,CN1)),C0))))),
IIntegrate(1331,Integrate(Times(Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_),Times(f_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Power(c,p),CN1),Integrate(Times(Power(Plus(g,Times(h,x)),m),Power(Plus(Times(C1D2,b),Times(c,x)),Times(C2,p)),Power(Plus(d,Times(e,x),Times(f,Sqr(x))),q)),x)),x),And(FreeQ(List(a,b,c,d,e,f,g,h,m,q),x),EqQ(Subtract(Sqr(b),Times(C4,a,c)),C0),IntegerQ(p)))),
IIntegrate(1332,Integrate(Times(Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_DEFAULT),Power(Plus(d_DEFAULT,Times(f_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Power(c,p),CN1),Integrate(Times(Power(Plus(g,Times(h,x)),m),Power(Plus(Times(C1D2,b),Times(c,x)),Times(C2,p)),Power(Plus(d,Times(f,Sqr(x))),q)),x)),x),And(FreeQ(List(a,b,c,d,f,g,h,m,q),x),EqQ(Subtract(Sqr(b),Times(C4,a,c)),C0),IntegerQ(p)))),
IIntegrate(1333,Integrate(Times(Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_),Power(Plus(d_,Times(e_DEFAULT,x_),Times(f_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Simp(Star(Times(Power(Plus(a,Times(b,x),Times(c,Sqr(x))),FracPart(p)),Power(Times(Power(Times(C4,c),IntPart(p)),Power(Plus(b,Times(C2,c,x)),Times(C2,FracPart(p)))),CN1)),Integrate(Times(Power(Plus(g,Times(h,x)),m),Power(Plus(b,Times(C2,c,x)),Times(C2,p)),Power(Plus(d,Times(e,x),Times(f,Sqr(x))),q)),x)),x),And(FreeQ(List(a,b,c,d,e,f,g,h,m,p,q),x),EqQ(Subtract(Sqr(b),Times(C4,a,c)),C0),Not(IntegerQ(p))))),
IIntegrate(1334,Integrate(Times(Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_),Power(Plus(d_,Times(f_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Simp(Star(Times(Power(Plus(a,Times(b,x),Times(c,Sqr(x))),FracPart(p)),Power(Times(Power(Times(C4,c),IntPart(p)),Power(Plus(b,Times(C2,c,x)),Times(C2,FracPart(p)))),CN1)),Integrate(Times(Power(Plus(g,Times(h,x)),m),Power(Plus(b,Times(C2,c,x)),Times(C2,p)),Power(Plus(d,Times(f,Sqr(x))),q)),x)),x),And(FreeQ(List(a,b,c,d,f,g,h,m,p,q),x),EqQ(Subtract(Sqr(b),Times(C4,a,c)),C0),Not(IntegerQ(p))))),
IIntegrate(1335,Integrate(Times(Power(Plus(g_,Times(h_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_),Times(f_DEFAULT,Sqr(x_))),m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Plus(Times(d,g,Power(a,CN1)),Times(f,h,x,Power(c,CN1))),m),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Plus(m,p))),x),And(FreeQ(List(a,b,c,d,e,f,g,h,p),x),EqQ(Plus(Times(c,Sqr(g)),Times(CN1,b,g,h),Times(a,Sqr(h))),C0),EqQ(Plus(Times(Sqr(c),d,Sqr(g)),Times(CN1,a,c,e,g,h),Times(Sqr(a),f,Sqr(h))),C0),IntegerQ(m)))),
IIntegrate(1336,Integrate(Times(Power(Plus(g_,Times(h_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_,Times(c_DEFAULT,Sqr(x_))),p_),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_),Times(f_DEFAULT,Sqr(x_))),m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Plus(Times(d,g,Power(a,CN1)),Times(f,h,x,Power(c,CN1))),m),Power(Plus(a,Times(c,Sqr(x))),Plus(m,p))),x),And(FreeQ(List(a,c,d,e,f,g,h,p),x),EqQ(Plus(Times(c,Sqr(g)),Times(a,Sqr(h))),C0),EqQ(Plus(Times(Sqr(c),d,Sqr(g)),Times(CN1,a,c,e,g,h),Times(Sqr(a),f,Sqr(h))),C0),IntegerQ(m)))),
IIntegrate(1337,Integrate(Times(Power(Plus(g_,Times(h_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_),Power(Plus(d_DEFAULT,Times(f_DEFAULT,Sqr(x_))),m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Plus(Times(d,g,Power(a,CN1)),Times(f,h,x,Power(c,CN1))),m),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Plus(m,p))),x),And(FreeQ(List(a,b,c,d,f,g,h,p),x),EqQ(Plus(Times(c,Sqr(g)),Times(CN1,b,g,h),Times(a,Sqr(h))),C0),EqQ(Plus(Times(Sqr(c),d,Sqr(g)),Times(Sqr(a),f,Sqr(h))),C0),IntegerQ(m)))),
IIntegrate(1338,Integrate(Times(Power(Plus(g_,Times(h_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_,Times(c_DEFAULT,Sqr(x_))),p_),Power(Plus(d_DEFAULT,Times(f_DEFAULT,Sqr(x_))),m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Plus(Times(d,g,Power(a,CN1)),Times(f,h,x,Power(c,CN1))),m),Power(Plus(a,Times(c,Sqr(x))),Plus(m,p))),x),And(FreeQ(List(a,c,d,f,g,h,p),x),EqQ(Plus(Times(c,Sqr(g)),Times(a,Sqr(h))),C0),EqQ(Plus(Times(Sqr(c),d,Sqr(g)),Times(Sqr(a),f,Sqr(h))),C0),IntegerQ(m)))),
IIntegrate(1339,Integrate(Times(Power(x_,p_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_),Power(Plus(Times(e_DEFAULT,x_),Times(f_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Integrate(Times(Power(Plus(Times(a,Power(e,CN1)),Times(c,Power(f,CN1),x)),p),Power(Plus(Times(e,x),Times(f,Sqr(x))),Plus(p,q))),x),And(FreeQ(List(a,b,c,e,f,q),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),EqQ(Plus(Times(c,Sqr(e)),Times(CN1,b,e,f),Times(a,Sqr(f))),C0),IntegerQ(p)))),
IIntegrate(1340,Integrate(Times(Power(x_,p_),Power(Plus(a_,Times(c_DEFAULT,Sqr(x_))),p_),Power(Plus(Times(e_DEFAULT,x_),Times(f_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Integrate(Times(Power(Plus(Times(a,Power(e,CN1)),Times(c,Power(f,CN1),x)),p),Power(Plus(Times(e,x),Times(f,Sqr(x))),Plus(p,q))),x),And(FreeQ(List(a,c,e,f,q),x),EqQ(Plus(Times(c,Sqr(e)),Times(a,Sqr(f))),C0),IntegerQ(p))))
  );
}
