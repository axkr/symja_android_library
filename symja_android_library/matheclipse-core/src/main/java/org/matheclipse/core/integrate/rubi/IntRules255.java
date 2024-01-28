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
class IntRules255 { 
  public static IAST RULES = List( 
IIntegrate(5101,Integrate(Times(Sqr(Csc(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),Power(Plus(Times(Sqr(Cot(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),b_DEFAULT),c_),CN1),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(C2,Integrate(Times(Power(Plus(f,Times(g,x)),m),Power(Plus(b,c,Times(Subtract(b,c),Cos(Plus(Times(C2,d),Times(C2,e,x))))),CN1)),x)),x),And(FreeQ(List(b,c,d,e,f,g),x),IGtQ(m,C0)))),
IIntegrate(5102,Integrate(Times(Sqr(Csc(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),Power(Plus(Times(Sqr(Csc(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),a_DEFAULT),Times(Sqr(Cot(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),b_DEFAULT),c_DEFAULT),CN1),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(C2,Integrate(Times(Power(Plus(f,Times(g,x)),m),Power(Plus(Times(C2,a),b,c,Times(Subtract(b,c),Cos(Plus(Times(C2,d),Times(C2,e,x))))),CN1)),x)),x),And(FreeQ(List(a,b,c,d,e,f,g),x),IGtQ(m,C0),NeQ(Plus(a,b),C0),NeQ(Plus(a,c),C0)))),
IIntegrate(5103,Integrate(Times(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),Power(Plus(a_,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN2),Plus(A_,Times(B_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Plus(Simp(Times(CN1,BSymbol,Plus(e,Times(f,x)),Cos(Plus(c,Times(d,x))),Power(Times(a,d,Plus(a,Times(b,Sin(Plus(c,Times(d,x)))))),CN1)),x),Simp(Star(Times(BSymbol,f,Power(Times(a,d),CN1)),Integrate(Times(Cos(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,f,ASymbol,BSymbol),x),EqQ(Subtract(Times(a,ASymbol),Times(b,BSymbol)),C0)))),
IIntegrate(5104,Integrate(Times(Power(Plus(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_),CN2),Plus(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),B_DEFAULT),A_),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Simp(Times(BSymbol,Plus(e,Times(f,x)),Sin(Plus(c,Times(d,x))),Power(Times(a,d,Plus(a,Times(b,Cos(Plus(c,Times(d,x)))))),CN1)),x),Simp(Star(Times(BSymbol,f,Power(Times(a,d),CN1)),Integrate(Times(Sin(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,x))))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,f,ASymbol,BSymbol),x),EqQ(Subtract(Times(a,ASymbol),Times(b,BSymbol)),C0)))),
IIntegrate(5105,Integrate(Times(Sqr(x_),Power(Plus(Times(Cos(Times(a_DEFAULT,x_)),d_DEFAULT,x_),Times(c_DEFAULT,Sin(Times(a_DEFAULT,x_)))),CN2)),x_Symbol),
    Condition(Plus(Simp(Times(x,Power(Times(a,d,Sin(Times(a,x)),Plus(Times(c,Sin(Times(a,x))),Times(d,x,Cos(Times(a,x))))),CN1)),x),Simp(Star(Power(d,CN2),Integrate(Power(Sin(Times(a,x)),CN2),x)),x)),And(FreeQ(list(a,c,d),x),EqQ(Plus(Times(a,c),d),C0)))),
IIntegrate(5106,Integrate(Times(Sqr(x_),Power(Plus(Times(Cos(Times(a_DEFAULT,x_)),c_DEFAULT),Times(d_DEFAULT,x_,Sin(Times(a_DEFAULT,x_)))),CN2)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,x,Power(Times(a,d,Cos(Times(a,x)),Plus(Times(c,Cos(Times(a,x))),Times(d,x,Sin(Times(a,x))))),CN1)),x),Simp(Star(Power(d,CN2),Integrate(Power(Cos(Times(a,x)),CN2),x)),x)),And(FreeQ(list(a,c,d),x),EqQ(Subtract(Times(a,c),d),C0)))),
IIntegrate(5107,Integrate(Times(Sqr(Sin(Times(a_DEFAULT,x_))),Power(Plus(Times(Cos(Times(a_DEFAULT,x_)),d_DEFAULT,x_),Times(c_DEFAULT,Sin(Times(a_DEFAULT,x_)))),CN2)),x_Symbol),
    Condition(Plus(Simp(Power(Times(Sqr(d),x),CN1),x),Simp(Times(Sin(Times(a,x)),Power(Times(a,d,x,Plus(Times(d,x,Cos(Times(a,x))),Times(c,Sin(Times(a,x))))),CN1)),x)),And(FreeQ(list(a,c,d),x),EqQ(Plus(Times(a,c),d),C0)))),
IIntegrate(5108,Integrate(Times(Sqr(Cos(Times(a_DEFAULT,x_))),Power(Plus(Times(Cos(Times(a_DEFAULT,x_)),c_DEFAULT),Times(d_DEFAULT,x_,Sin(Times(a_DEFAULT,x_)))),CN2)),x_Symbol),
    Condition(Subtract(Simp(Power(Times(Sqr(d),x),CN1),x),Simp(Times(Cos(Times(a,x)),Power(Times(a,d,x,Plus(Times(d,x,Sin(Times(a,x))),Times(c,Cos(Times(a,x))))),CN1)),x)),And(FreeQ(list(a,c,d),x),EqQ(Subtract(Times(a,c),d),C0)))),
IIntegrate(5109,Integrate(Times(Power(Times(b_DEFAULT,x_),m_),Power(Sin(Times(a_DEFAULT,x_)),n_),Power(Plus(Times(Cos(Times(a_DEFAULT,x_)),d_DEFAULT,x_),Times(c_DEFAULT,Sin(Times(a_DEFAULT,x_)))),CN2)),x_Symbol),
    Condition(Subtract(Simp(Times(b,Power(Times(b,x),Subtract(m,C1)),Power(Sin(Times(a,x)),Subtract(n,C1)),Power(Times(a,d,Plus(Times(c,Sin(Times(a,x))),Times(d,x,Cos(Times(a,x))))),CN1)),x),Simp(Star(Times(Sqr(b),Subtract(n,C1),Power(d,CN2)),Integrate(Times(Power(Times(b,x),Subtract(m,C2)),Power(Sin(Times(a,x)),Subtract(n,C2))),x)),x)),And(FreeQ(List(a,b,c,d,m,n),x),EqQ(Plus(Times(a,c),d),C0),EqQ(m,Subtract(C2,n))))),
IIntegrate(5110,Integrate(Times(Power(Cos(Times(a_DEFAULT,x_)),n_),Power(Times(b_DEFAULT,x_),m_),Power(Plus(Times(Cos(Times(a_DEFAULT,x_)),c_DEFAULT),Times(d_DEFAULT,x_,Sin(Times(a_DEFAULT,x_)))),CN2)),x_Symbol),
    Condition(Subtract(Simp(Times(CN1,b,Power(Times(b,x),Subtract(m,C1)),Power(Cos(Times(a,x)),Subtract(n,C1)),Power(Times(a,d,Plus(Times(c,Cos(Times(a,x))),Times(d,x,Sin(Times(a,x))))),CN1)),x),Simp(Star(Times(Sqr(b),Subtract(n,C1),Power(d,CN2)),Integrate(Times(Power(Times(b,x),Subtract(m,C2)),Power(Cos(Times(a,x)),Subtract(n,C2))),x)),x)),And(FreeQ(List(a,b,c,d,m,n),x),EqQ(Subtract(Times(a,c),d),C0),EqQ(m,Subtract(C2,n))))),
IIntegrate(5111,Integrate(Times(Power(Csc(Times(a_DEFAULT,x_)),n_DEFAULT),Power(Times(b_DEFAULT,x_),m_DEFAULT),Power(Plus(Times(Cos(Times(a_DEFAULT,x_)),d_DEFAULT,x_),Times(c_DEFAULT,Sin(Times(a_DEFAULT,x_)))),CN2)),x_Symbol),
    Condition(Plus(Simp(Times(b,Power(Times(b,x),Subtract(m,C1)),Power(Csc(Times(a,x)),Plus(n,C1)),Power(Times(a,d,Plus(Times(c,Sin(Times(a,x))),Times(d,x,Cos(Times(a,x))))),CN1)),x),Simp(Star(Times(Sqr(b),Plus(n,C1),Power(d,CN2)),Integrate(Times(Power(Times(b,x),Subtract(m,C2)),Power(Csc(Times(a,x)),Plus(n,C2))),x)),x)),And(FreeQ(List(a,b,c,d,m,n),x),EqQ(Plus(Times(a,c),d),C0),EqQ(m,Plus(n,C2))))),
IIntegrate(5112,Integrate(Times(Power(Times(b_DEFAULT,x_),m_DEFAULT),Power(Sec(Times(a_DEFAULT,x_)),n_DEFAULT),Power(Plus(Times(Cos(Times(a_DEFAULT,x_)),c_DEFAULT),Times(d_DEFAULT,x_,Sin(Times(a_DEFAULT,x_)))),CN2)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,b,Power(Times(b,x),Subtract(m,C1)),Power(Sec(Times(a,x)),Plus(n,C1)),Power(Times(a,d,Plus(Times(c,Cos(Times(a,x))),Times(d,x,Sin(Times(a,x))))),CN1)),x),Simp(Star(Times(Sqr(b),Plus(n,C1),Power(d,CN2)),Integrate(Times(Power(Times(b,x),Subtract(m,C2)),Power(Sec(Times(a,x)),Plus(n,C2))),x)),x)),And(FreeQ(List(a,b,c,d,m,n),x),EqQ(Subtract(Times(a,c),d),C0),EqQ(m,Plus(n,C2))))),
IIntegrate(5113,Integrate(Times(Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),p_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sin(Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Sin(Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),n_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(a,m),Power(c,m)),Integrate(Times(Power(Plus(g,Times(h,x)),p),Power(Cos(Plus(e,Times(f,x))),Times(C2,m)),Power(Plus(c,Times(d,Sin(Plus(e,Times(f,x))))),Subtract(n,m))),x)),x),And(FreeQ(List(a,b,c,d,e,f,g,h),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),EqQ(Subtract(Sqr(a),Sqr(b)),C0),IntegerQ(m),IGtQ(Subtract(n,m),C0)))),
IIntegrate(5114,Integrate(Times(Power(Plus(Times(Cos(Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT),a_),m_DEFAULT),Power(Plus(Times(Cos(Plus(e_DEFAULT,Times(f_DEFAULT,x_))),d_DEFAULT),c_),n_DEFAULT),Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(a,m),Power(c,m)),Integrate(Times(Power(Plus(g,Times(h,x)),p),Power(Sin(Plus(e,Times(f,x))),Times(C2,m)),Power(Plus(c,Times(d,Cos(Plus(e,Times(f,x))))),Subtract(n,m))),x)),x),And(FreeQ(List(a,b,c,d,e,f,g,h),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),EqQ(Subtract(Sqr(a),Sqr(b)),C0),IntegerQ(m),IGtQ(Subtract(n,m),C0)))),
IIntegrate(5115,Integrate(Times(Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),p_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sin(Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),m_),Power(Plus(c_,Times(d_DEFAULT,Sin(Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),n_)),x_Symbol),
    Condition(Simp(Star(Times(Power(a,IntPart(m)),Power(c,IntPart(m)),Power(Plus(a,Times(b,Sin(Plus(e,Times(f,x))))),FracPart(m)),Power(Plus(c,Times(d,Sin(Plus(e,Times(f,x))))),FracPart(m)),Power(Power(Cos(Plus(e,Times(f,x))),Times(C2,FracPart(m))),CN1)),Integrate(Times(Power(Plus(g,Times(h,x)),p),Power(Cos(Plus(e,Times(f,x))),Times(C2,m)),Power(Plus(c,Times(d,Sin(Plus(e,Times(f,x))))),Subtract(n,m))),x)),x),And(FreeQ(List(a,b,c,d,e,f,g,h),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),EqQ(Subtract(Sqr(a),Sqr(b)),C0),IntegerQ(p),IntegerQ(Times(C2,m)),IGeQ(Subtract(n,m),C0)))),
IIntegrate(5116,Integrate(Times(Power(Plus(Times(Cos(Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT),a_),m_),Power(Plus(Times(Cos(Plus(e_DEFAULT,Times(f_DEFAULT,x_))),d_DEFAULT),c_),n_),Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(a,IntPart(m)),Power(c,IntPart(m)),Power(Plus(a,Times(b,Cos(Plus(e,Times(f,x))))),FracPart(m)),Power(Plus(c,Times(d,Cos(Plus(e,Times(f,x))))),FracPart(m)),Power(Power(Sin(Plus(e,Times(f,x))),Times(C2,FracPart(m))),CN1)),Integrate(Times(Power(Plus(g,Times(h,x)),p),Power(Sin(Plus(e,Times(f,x))),Times(C2,m)),Power(Plus(c,Times(d,Cos(Plus(e,Times(f,x))))),Subtract(n,m))),x)),x),And(FreeQ(List(a,b,c,d,e,f,g,h),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),EqQ(Subtract(Sqr(a),Sqr(b)),C0),IntegerQ(p),IntegerQ(Times(C2,m)),IGeQ(Subtract(n,m),C0)))),
IIntegrate(5117,Integrate(Times(Power(Sec(v_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Tan(v_))),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Power(Plus(Times(a,Cos(v)),Times(b,Sin(v))),n),x),And(FreeQ(list(a,b),x),IntegerQ(Times(C1D2,Subtract(m,C1))),EqQ(Plus(m,n),C0)))),
IIntegrate(5118,Integrate(Times(Power(Csc(v_),m_DEFAULT),Power(Plus(Times(Cot(v_),b_DEFAULT),a_),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Power(Plus(Times(b,Cos(v)),Times(a,Sin(v))),n),x),And(FreeQ(list(a,b),x),IntegerQ(Times(C1D2,Subtract(m,C1))),EqQ(Plus(m,n),C0)))),
IIntegrate(5119,Integrate(Times(u_DEFAULT,Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),m_DEFAULT),Power(Sin(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(u,Times(Power(Sin(Plus(a,Times(b,x))),m),Power(Sin(Plus(c,Times(d,x))),n)),x),x),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C0),IGtQ(n,C0)))),
IIntegrate(5120,Integrate(Times(Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),m_DEFAULT),Power(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),n_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(u,Times(Power(Cos(Plus(a,Times(b,x))),m),Power(Cos(Plus(c,Times(d,x))),n)),x),x),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C0),IGtQ(n,C0))))
  );
}
