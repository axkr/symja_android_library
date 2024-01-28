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
class IntRules238 { 
  public static IAST RULES = List( 
IIntegrate(4761,Integrate(Times(Power(Times($($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),c_DEFAULT),n_DEFAULT),u_DEFAULT,Plus(A_DEFAULT,Times(B_DEFAULT,$($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Times(C_DEFAULT,Sqr($($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))))),x_Symbol),
    Condition(Simp(Star(Sqr(c),Integrate(Times(ActivateTrig(u),Power(Times(c,Csc(Plus(a,Times(b,x)))),Subtract(n,C2)),Plus(CSymbol,Times(BSymbol,Csc(Plus(a,Times(b,x)))),Times(ASymbol,Sqr(Csc(Plus(a,Times(b,x))))))),x)),x),And(FreeQ(List(a,b,c,ASymbol,BSymbol,CSymbol,n),x),KnownSecantIntegrandQ(u,x)))),
IIntegrate(4762,Integrate(Times(u_DEFAULT,Plus(Times(Sqr($($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),C_DEFAULT),A_),Power(Times(c_DEFAULT,$($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Sqr(c),Integrate(Times(ActivateTrig(u),Power(Times(c,Sec(Plus(a,Times(b,x)))),Subtract(n,C2)),Plus(CSymbol,Times(ASymbol,Sqr(Sec(Plus(a,Times(b,x))))))),x)),x),And(FreeQ(List(a,b,c,ASymbol,CSymbol,n),x),KnownSecantIntegrandQ(u,x)))),
IIntegrate(4763,Integrate(Times(Power(Times($($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),c_DEFAULT),n_DEFAULT),u_DEFAULT,Plus(A_,Times(C_DEFAULT,Sqr($($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))))),x_Symbol),
    Condition(Simp(Star(Sqr(c),Integrate(Times(ActivateTrig(u),Power(Times(c,Csc(Plus(a,Times(b,x)))),Subtract(n,C2)),Plus(CSymbol,Times(ASymbol,Sqr(Csc(Plus(a,Times(b,x))))))),x)),x),And(FreeQ(List(a,b,c,ASymbol,CSymbol,n),x),KnownSecantIntegrandQ(u,x)))),
IIntegrate(4764,Integrate(Times(Plus(A_DEFAULT,Times($($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),B_DEFAULT),Times(Sqr($($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),C_DEFAULT)),u_),x_Symbol),
    Condition(Integrate(Times(ActivateTrig(u),Plus(CSymbol,Times(BSymbol,Sec(Plus(a,Times(b,x)))),Times(ASymbol,Sqr(Sec(Plus(a,Times(b,x)))))),Power(Sec(Plus(a,Times(b,x))),CN2)),x),And(FreeQ(List(a,b,ASymbol,BSymbol,CSymbol),x),KnownSecantIntegrandQ(u,x)))),
IIntegrate(4765,Integrate(Times(u_,Plus(A_DEFAULT,Times(B_DEFAULT,$($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Times(C_DEFAULT,Sqr($($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))))),x_Symbol),
    Condition(Integrate(Times(ActivateTrig(u),Plus(CSymbol,Times(BSymbol,Csc(Plus(a,Times(b,x)))),Times(ASymbol,Sqr(Csc(Plus(a,Times(b,x)))))),Power(Csc(Plus(a,Times(b,x))),CN2)),x),And(FreeQ(List(a,b,ASymbol,BSymbol,CSymbol),x),KnownSecantIntegrandQ(u,x)))),
IIntegrate(4766,Integrate(Times(Plus(Times(Sqr($($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),C_DEFAULT),A_),u_),x_Symbol),
    Condition(Integrate(Times(ActivateTrig(u),Plus(CSymbol,Times(ASymbol,Sqr(Sec(Plus(a,Times(b,x)))))),Power(Sec(Plus(a,Times(b,x))),CN2)),x),And(FreeQ(List(a,b,ASymbol,CSymbol),x),KnownSecantIntegrandQ(u,x)))),
IIntegrate(4767,Integrate(Times(u_,Plus(A_,Times(C_DEFAULT,Sqr($($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))))),x_Symbol),
    Condition(Integrate(Times(ActivateTrig(u),Plus(CSymbol,Times(ASymbol,Sqr(Csc(Plus(a,Times(b,x)))))),Power(Csc(Plus(a,Times(b,x))),CN2)),x),And(FreeQ(List(a,b,ASymbol,CSymbol),x),KnownSecantIntegrandQ(u,x)))),
IIntegrate(4768,Integrate(Times(u_,Plus(Times(A_DEFAULT,Power($($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),Times(B_DEFAULT,Power($($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),$p("n1"))),Times(C_DEFAULT,Power($($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),$p("n2"))))),x_Symbol),
    Condition(Integrate(Times(ActivateTrig(u),Power(Sec(Plus(a,Times(b,x))),n),Plus(ASymbol,Times(BSymbol,Sec(Plus(a,Times(b,x)))),Times(CSymbol,Sqr(Sec(Plus(a,Times(b,x))))))),x),And(FreeQ(List(a,b,ASymbol,BSymbol,CSymbol,n),x),EqQ($s("n1"),Plus(n,C1)),EqQ($s("n2"),Plus(n,C2))))),
IIntegrate(4769,Integrate(Times(Plus(Times(Power($($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),A_DEFAULT),Times(Power($($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),$p("n1")),B_DEFAULT),Times(Power($($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),$p("n2")),C_DEFAULT)),u_),x_Symbol),
    Condition(Integrate(Times(ActivateTrig(u),Power(Csc(Plus(a,Times(b,x))),n),Plus(ASymbol,Times(BSymbol,Csc(Plus(a,Times(b,x)))),Times(CSymbol,Sqr(Csc(Plus(a,Times(b,x))))))),x),And(FreeQ(List(a,b,ASymbol,BSymbol,CSymbol,n),x),EqQ($s("n1"),Plus(n,C1)),EqQ($s("n2"),Plus(n,C2))))),
IIntegrate(4770,Integrate(Times($($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Sin(Plus(a,Negate(c),Times(Subtract(b,d),x))),Power(Times(C2,Subtract(b,d)),CN1)),x),Simp(Times(Sin(Plus(a,c,Times(Plus(b,d),x))),Power(Times(C2,Plus(b,d)),CN1)),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(b),Sqr(d)),C0)))),
IIntegrate(4771,Integrate(Times($($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),$($s("§cos"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(Sin(Plus(a,Negate(c),Times(Subtract(b,d),x))),Power(Times(C2,Subtract(b,d)),CN1)),x),Simp(Times(Sin(Plus(a,c,Times(Plus(b,d),x))),Power(Times(C2,Plus(b,d)),CN1)),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(b),Sqr(d)),C0)))),
IIntegrate(4772,Integrate(Times($($s("§cos"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),$($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(CN1,Cos(Plus(a,Negate(c),Times(Subtract(b,d),x))),Power(Times(C2,Subtract(b,d)),CN1)),x),Simp(Times(Cos(Plus(a,c,Times(Plus(b,d),x))),Power(Times(C2,Plus(b,d)),CN1)),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(b),Sqr(d)),C0)))),
IIntegrate(4773,Integrate(Times(Sqr($($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Times(g_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),p_)),x_Symbol),
    Condition(Plus(Simp(Star(C1D2,Integrate(Power(Times(g,Sin(Plus(c,Times(d,x)))),p),x)),x),Simp(Star(C1D2,Integrate(Times(Cos(Plus(c,Times(d,x))),Power(Times(g,Sin(Plus(c,Times(d,x)))),p)),x)),x)),And(FreeQ(List(a,b,c,d,g),x),EqQ(Subtract(Times(b,c),Times(a,d)),C0),EqQ(Times(d,Power(b,CN1)),C2),IGtQ(Times(C1D2,p),C0)))),
IIntegrate(4774,Integrate(Times(Sqr($($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Times(g_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),p_)),x_Symbol),
    Condition(Subtract(Simp(Star(C1D2,Integrate(Power(Times(g,Sin(Plus(c,Times(d,x)))),p),x)),x),Simp(Star(C1D2,Integrate(Times(Cos(Plus(c,Times(d,x))),Power(Times(g,Sin(Plus(c,Times(d,x)))),p)),x)),x)),And(FreeQ(List(a,b,c,d,g),x),EqQ(Subtract(Times(b,c),Times(a,d)),C0),EqQ(Times(d,Power(b,CN1)),C2),IGtQ(Times(C1D2,p),C0)))),
IIntegrate(4775,Integrate(Times(Power(Times($($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),e_DEFAULT),m_DEFAULT),Power($($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(C2,p),Power(Power(e,p),CN1)),Integrate(Times(Power(Times(e,Cos(Plus(a,Times(b,x)))),Plus(m,p)),Power(Sin(Plus(a,Times(b,x))),p)),x)),x),And(FreeQ(List(a,b,c,d,e,m),x),EqQ(Subtract(Times(b,c),Times(a,d)),C0),EqQ(Times(d,Power(b,CN1)),C2),IntegerQ(p)))),
IIntegrate(4776,Integrate(Times(Power(Times(f_DEFAULT,$($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT),Power($($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(C2,p),Power(Power(f,p),CN1)),Integrate(Times(Power(Cos(Plus(a,Times(b,x))),p),Power(Times(f,Sin(Plus(a,Times(b,x)))),Plus(n,p))),x)),x),And(FreeQ(List(a,b,c,d,f,n),x),EqQ(Subtract(Times(b,c),Times(a,d)),C0),EqQ(Times(d,Power(b,CN1)),C2),IntegerQ(p)))),
IIntegrate(4777,Integrate(Times(Power(Times($($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),e_DEFAULT),m_),Power(Times(g_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),p_)),x_Symbol),
    Condition(Simp(Times(Sqr(e),Power(Times(e,Cos(Plus(a,Times(b,x)))),Subtract(m,C2)),Power(Times(g,Sin(Plus(c,Times(d,x)))),Plus(p,C1)),Power(Times(C2,b,g,Plus(p,C1)),CN1)),x),And(FreeQ(List(a,b,c,d,e,g,m,p),x),EqQ(Subtract(Times(b,c),Times(a,d)),C0),EqQ(Times(d,Power(b,CN1)),C2),Not(IntegerQ(p)),EqQ(Subtract(Plus(m,p),C1),C0)))),
IIntegrate(4778,Integrate(Times(Power(Times(e_DEFAULT,$($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_),Power(Times(g_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),p_)),x_Symbol),
    Condition(Simp(Times(CN1,Sqr(e),Power(Times(e,Sin(Plus(a,Times(b,x)))),Subtract(m,C2)),Power(Times(g,Sin(Plus(c,Times(d,x)))),Plus(p,C1)),Power(Times(C2,b,g,Plus(p,C1)),CN1)),x),And(FreeQ(List(a,b,c,d,e,g,m,p),x),EqQ(Subtract(Times(b,c),Times(a,d)),C0),EqQ(Times(d,Power(b,CN1)),C2),Not(IntegerQ(p)),EqQ(Subtract(Plus(m,p),C1),C0)))),
IIntegrate(4779,Integrate(Times(Power(Times($($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),e_DEFAULT),m_DEFAULT),Power(Times(g_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),p_)),x_Symbol),
    Condition(Simp(Times(CN1,Power(Times(e,Cos(Plus(a,Times(b,x)))),m),Power(Times(g,Sin(Plus(c,Times(d,x)))),Plus(p,C1)),Power(Times(b,g,m),CN1)),x),And(FreeQ(List(a,b,c,d,e,g,m,p),x),EqQ(Subtract(Times(b,c),Times(a,d)),C0),EqQ(Times(d,Power(b,CN1)),C2),Not(IntegerQ(p)),EqQ(Plus(m,Times(C2,p),C2),C0)))),
IIntegrate(4780,Integrate(Times(Power(Times(e_DEFAULT,$($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_DEFAULT),Power(Times(g_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),p_)),x_Symbol),
    Condition(Simp(Times(Power(Times(e,Sin(Plus(a,Times(b,x)))),m),Power(Times(g,Sin(Plus(c,Times(d,x)))),Plus(p,C1)),Power(Times(b,g,m),CN1)),x),And(FreeQ(List(a,b,c,d,e,g,m,p),x),EqQ(Subtract(Times(b,c),Times(a,d)),C0),EqQ(Times(d,Power(b,CN1)),C2),Not(IntegerQ(p)),EqQ(Plus(m,Times(C2,p),C2),C0))))
  );
}
