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
class IntRules255 { 
  public static IAST RULES = List( 
IIntegrate(5101,Integrate(Times(Power(Cot(w_),n_DEFAULT),Sin(v_)),x_Symbol),
    Condition(Plus(Integrate(Times(Cos(v),Power(Cot(w),Plus(n,CN1))),x),Simp(Dist(Sin(Subtract(v,w)),Integrate(Times(Csc(w),Power(Cot(w),Plus(n,CN1))),x),x),x)),And(GtQ(n,C0),FreeQ(Subtract(v,w),x),NeQ(w,v)))),
IIntegrate(5102,Integrate(Times(Cos(v_),Power(Tan(w_),n_DEFAULT)),x_Symbol),
    Condition(Subtract(Integrate(Times(Sin(v),Power(Tan(w),Plus(n,CN1))),x),Simp(Dist(Sin(Subtract(v,w)),Integrate(Times(Sec(w),Power(Tan(w),Plus(n,CN1))),x),x),x)),And(GtQ(n,C0),FreeQ(Subtract(v,w),x),NeQ(w,v)))),
IIntegrate(5103,Integrate(Times(Power(Sec(w_),n_DEFAULT),Sin(v_)),x_Symbol),
    Condition(Plus(Simp(Dist(Cos(Subtract(v,w)),Integrate(Times(Tan(w),Power(Sec(w),Plus(n,CN1))),x),x),x),Simp(Dist(Sin(Subtract(v,w)),Integrate(Power(Sec(w),Plus(n,CN1)),x),x),x)),And(GtQ(n,C0),FreeQ(Subtract(v,w),x),NeQ(w,v)))),
IIntegrate(5104,Integrate(Times(Cos(v_),Power(Csc(w_),n_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Dist(Cos(Subtract(v,w)),Integrate(Times(Cot(w),Power(Csc(w),Plus(n,CN1))),x),x),x),Simp(Dist(Sin(Subtract(v,w)),Integrate(Power(Csc(w),Plus(n,CN1)),x),x),x)),And(GtQ(n,C0),FreeQ(Subtract(v,w),x),NeQ(w,v)))),
IIntegrate(5105,Integrate(Times(Power(Csc(w_),n_DEFAULT),Sin(v_)),x_Symbol),
    Condition(Plus(Simp(Dist(Sin(Subtract(v,w)),Integrate(Times(Cot(w),Power(Csc(w),Plus(n,CN1))),x),x),x),Simp(Dist(Cos(Subtract(v,w)),Integrate(Power(Csc(w),Plus(n,CN1)),x),x),x)),And(GtQ(n,C0),FreeQ(Subtract(v,w),x),NeQ(w,v)))),
IIntegrate(5106,Integrate(Times(Cos(v_),Power(Sec(w_),n_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Dist(Negate(Sin(Subtract(v,w))),Integrate(Times(Tan(w),Power(Sec(w),Plus(n,CN1))),x),x),x),Simp(Dist(Cos(Subtract(v,w)),Integrate(Power(Sec(w),Plus(n,CN1)),x),x),x)),And(GtQ(n,C0),FreeQ(Subtract(v,w),x),NeQ(w,v)))),
IIntegrate(5107,Integrate(Times(Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(b,C1D2,Sin(Plus(Times(C2,c),Times(C2,d,x))))),n)),x),FreeQ(List(a,b,c,d,e,f,m,n),x))),
IIntegrate(5108,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(Sin(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),n_)),x_Symbol),
    Condition(Simp(Dist(Power(Power(C2,n),CN1),Integrate(Times(Power(x,m),Power(Subtract(Plus(Times(C2,a),b),Times(b,Cos(Plus(Times(C2,c),Times(C2,d,x))))),n)),x),x),x),And(FreeQ(List(a,b,c,d),x),NeQ(Plus(a,b),C0),IGtQ(m,C0),ILtQ(n,C0),Or(EqQ(n,CN1),And(EqQ(m,C1),EqQ(n,CN2)))))),
IIntegrate(5109,Integrate(Times(Power(Plus(Times(Sqr(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),b_DEFAULT),a_),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(Power(C2,n),CN1),Integrate(Times(Power(x,m),Power(Plus(Times(C2,a),b,Times(b,Cos(Plus(Times(C2,c),Times(C2,d,x))))),n)),x),x),x),And(FreeQ(List(a,b,c,d),x),NeQ(Plus(a,b),C0),IGtQ(m,C0),ILtQ(n,C0),Or(EqQ(n,CN1),And(EqQ(m,C1),EqQ(n,CN2)))))),
IIntegrate(5110,Integrate(Times(Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_DEFAULT,Times(Sqr(Cos(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),b_DEFAULT),Times(c_DEFAULT,Sqr(Sin(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))))),CN1)),x_Symbol),
    Condition(Simp(Dist(C2,Integrate(Times(Power(Plus(f,Times(g,x)),m),Power(Plus(Times(C2,a),b,c,Times(Subtract(b,c),Cos(Plus(Times(C2,d),Times(C2,e,x))))),CN1)),x),x),x),And(FreeQ(List(a,b,c,d,e,f,g),x),IGtQ(m,C0),NeQ(Plus(a,b),C0),NeQ(Plus(a,c),C0)))),
IIntegrate(5111,Integrate(Times(Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),m_DEFAULT),Sqr(Sec(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),Power(Plus(b_,Times(c_DEFAULT,Sqr(Tan(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))))),CN1)),x_Symbol),
    Condition(Simp(Dist(C2,Integrate(Times(Power(Plus(f,Times(g,x)),m),Power(Plus(b,c,Times(Subtract(b,c),Cos(Plus(Times(C2,d),Times(C2,e,x))))),CN1)),x),x),x),And(FreeQ(List(b,c,d,e,f,g),x),IGtQ(m,C0)))),
IIntegrate(5112,Integrate(Times(Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),m_DEFAULT),Sqr(Sec(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),Power(Plus(b_DEFAULT,Times(a_DEFAULT,Sqr(Sec(Plus(d_DEFAULT,Times(e_DEFAULT,x_))))),Times(c_DEFAULT,Sqr(Tan(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))))),CN1)),x_Symbol),
    Condition(Simp(Dist(C2,Integrate(Times(Power(Plus(f,Times(g,x)),m),Power(Plus(Times(C2,a),b,c,Times(Subtract(b,c),Cos(Plus(Times(C2,d),Times(C2,e,x))))),CN1)),x),x),x),And(FreeQ(List(a,b,c,d,e,f,g),x),IGtQ(m,C0),NeQ(Plus(a,b),C0),NeQ(Plus(a,c),C0)))),
IIntegrate(5113,Integrate(Times(Sqr(Csc(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),Power(Plus(Times(Sqr(Cot(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),b_DEFAULT),c_),CN1),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(C2,Integrate(Times(Power(Plus(f,Times(g,x)),m),Power(Plus(b,c,Times(Subtract(b,c),Cos(Plus(Times(C2,d),Times(C2,e,x))))),CN1)),x),x),x),And(FreeQ(List(b,c,d,e,f,g),x),IGtQ(m,C0)))),
IIntegrate(5114,Integrate(Times(Sqr(Csc(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),Power(Plus(Times(Sqr(Csc(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),a_DEFAULT),Times(Sqr(Cot(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),b_DEFAULT),c_DEFAULT),CN1),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(C2,Integrate(Times(Power(Plus(f,Times(g,x)),m),Power(Plus(Times(C2,a),b,c,Times(Subtract(b,c),Cos(Plus(Times(C2,d),Times(C2,e,x))))),CN1)),x),x),x),And(FreeQ(List(a,b,c,d,e,f,g),x),IGtQ(m,C0),NeQ(Plus(a,b),C0),NeQ(Plus(a,c),C0)))),
IIntegrate(5115,Integrate(Times(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),Power(Plus(a_,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN2),Plus(A_,Times(B_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Plus(Simp(Times(CN1,BSymbol,Plus(e,Times(f,x)),Cos(Plus(c,Times(d,x))),Power(Times(a,d,Plus(a,Times(b,Sin(Plus(c,Times(d,x)))))),CN1)),x),Simp(Dist(Times(BSymbol,f,Power(Times(a,d),CN1)),Integrate(Times(Cos(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,f,ASymbol,BSymbol),x),EqQ(Subtract(Times(a,ASymbol),Times(b,BSymbol)),C0)))),
IIntegrate(5116,Integrate(Times(Power(Plus(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_),CN2),Plus(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),B_DEFAULT),A_),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Simp(Times(BSymbol,Plus(e,Times(f,x)),Sin(Plus(c,Times(d,x))),Power(Times(a,d,Plus(a,Times(b,Cos(Plus(c,Times(d,x)))))),CN1)),x),Simp(Dist(Times(BSymbol,f,Power(Times(a,d),CN1)),Integrate(Times(Sin(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,x))))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,f,ASymbol,BSymbol),x),EqQ(Subtract(Times(a,ASymbol),Times(b,BSymbol)),C0)))),
IIntegrate(5117,Integrate(Times(Sqr(x_),Power(Plus(Times(Cos(Times(a_DEFAULT,x_)),d_DEFAULT,x_),Times(c_DEFAULT,Sin(Times(a_DEFAULT,x_)))),CN2)),x_Symbol),
    Condition(Plus(Simp(Times(x,Power(Times(a,d,Sin(Times(a,x)),Plus(Times(c,Sin(Times(a,x))),Times(d,x,Cos(Times(a,x))))),CN1)),x),Simp(Dist(Power(d,CN2),Integrate(Power(Sin(Times(a,x)),CN2),x),x),x)),And(FreeQ(list(a,c,d),x),EqQ(Plus(Times(a,c),d),C0)))),
IIntegrate(5118,Integrate(Times(Sqr(x_),Power(Plus(Times(Cos(Times(a_DEFAULT,x_)),c_DEFAULT),Times(d_DEFAULT,x_,Sin(Times(a_DEFAULT,x_)))),CN2)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,x,Power(Times(a,d,Cos(Times(a,x)),Plus(Times(c,Cos(Times(a,x))),Times(d,x,Sin(Times(a,x))))),CN1)),x),Simp(Dist(Power(d,CN2),Integrate(Power(Cos(Times(a,x)),CN2),x),x),x)),And(FreeQ(list(a,c,d),x),EqQ(Subtract(Times(a,c),d),C0)))),
IIntegrate(5119,Integrate(Times(Sqr(Sin(Times(a_DEFAULT,x_))),Power(Plus(Times(Cos(Times(a_DEFAULT,x_)),d_DEFAULT,x_),Times(c_DEFAULT,Sin(Times(a_DEFAULT,x_)))),CN2)),x_Symbol),
    Condition(Plus(Simp(Power(Times(Sqr(d),x),CN1),x),Simp(Times(Sin(Times(a,x)),Power(Times(a,d,x,Plus(Times(d,x,Cos(Times(a,x))),Times(c,Sin(Times(a,x))))),CN1)),x)),And(FreeQ(list(a,c,d),x),EqQ(Plus(Times(a,c),d),C0)))),
IIntegrate(5120,Integrate(Times(Sqr(Cos(Times(a_DEFAULT,x_))),Power(Plus(Times(Cos(Times(a_DEFAULT,x_)),c_DEFAULT),Times(d_DEFAULT,x_,Sin(Times(a_DEFAULT,x_)))),CN2)),x_Symbol),
    Condition(Subtract(Simp(Power(Times(Sqr(d),x),CN1),x),Simp(Times(Cos(Times(a,x)),Power(Times(a,d,x,Plus(Times(d,x,Sin(Times(a,x))),Times(c,Cos(Times(a,x))))),CN1)),x)),And(FreeQ(list(a,c,d),x),EqQ(Subtract(Times(a,c),d),C0))))
  );
}
