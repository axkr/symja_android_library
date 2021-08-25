package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class IntRules212 { 
  public static IAST RULES = List( 
IIntegrate(4241,Integrate(Times(Power(Times($($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),c_DEFAULT),m_DEFAULT),u_),x_Symbol),
    Condition(Dist(Times(Power(Times(c,Cot(Plus(a,Times(b,x)))),m),Power(Times(c,Tan(Plus(a,Times(b,x)))),m)),Integrate(Times(ActivateTrig(u),Power(Power(Times(c,Tan(Plus(a,Times(b,x)))),m),CN1)),x),x),And(FreeQ(List(a,b,c,m),x),Not(IntegerQ(m)),KnownTangentIntegrandQ(u,x)))),
IIntegrate(4242,Integrate(Times(u_,Power(Times(c_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(Times(c,Cot(Plus(a,Times(b,x)))),m),Power(Times(c,Tan(Plus(a,Times(b,x)))),m)),Integrate(Times(ActivateTrig(u),Power(Power(Times(c,Cot(Plus(a,Times(b,x)))),m),CN1)),x),x),And(FreeQ(List(a,b,c,m),x),Not(IntegerQ(m)),KnownCotangentIntegrandQ(u,x)))),
IIntegrate(4243,Integrate(Times(Plus(Times($($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),B_DEFAULT),A_),u_,Power(Times(c_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Dist(c,Integrate(Times(ActivateTrig(u),Power(Times(c,Tan(Plus(a,Times(b,x)))),Subtract(n,C1)),Plus(BSymbol,Times(ASymbol,Tan(Plus(a,Times(b,x)))))),x),x),And(FreeQ(List(a,b,c,ASymbol,BSymbol,n),x),KnownTangentIntegrandQ(u,x)))),
IIntegrate(4244,Integrate(Times(Power(Times($($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),c_DEFAULT),n_DEFAULT),u_,Plus(A_,Times(B_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Dist(c,Integrate(Times(ActivateTrig(u),Power(Times(c,Cot(Plus(a,Times(b,x)))),Subtract(n,C1)),Plus(BSymbol,Times(ASymbol,Cot(Plus(a,Times(b,x)))))),x),x),And(FreeQ(List(a,b,c,ASymbol,BSymbol,n),x),KnownCotangentIntegrandQ(u,x)))),
IIntegrate(4245,Integrate(Times(Plus(Times($($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),B_DEFAULT),A_),u_),x_Symbol),
    Condition(Integrate(Times(ActivateTrig(u),Plus(BSymbol,Times(ASymbol,Tan(Plus(a,Times(b,x))))),Power(Tan(Plus(a,Times(b,x))),CN1)),x),And(FreeQ(List(a,b,ASymbol,BSymbol),x),KnownTangentIntegrandQ(u,x)))),
IIntegrate(4246,Integrate(Times(u_,Plus(A_,Times(B_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Integrate(Times(ActivateTrig(u),Plus(BSymbol,Times(ASymbol,Cot(Plus(a,Times(b,x))))),Power(Cot(Plus(a,Times(b,x))),CN1)),x),And(FreeQ(List(a,b,ASymbol,BSymbol),x),KnownCotangentIntegrandQ(u,x)))),
IIntegrate(4247,Integrate(Times(Plus(A_DEFAULT,Times($($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),B_DEFAULT),Times(Sqr($($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),C_DEFAULT)),u_DEFAULT,Power(Times(c_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Dist(Sqr(c),Integrate(Times(ActivateTrig(u),Power(Times(c,Tan(Plus(a,Times(b,x)))),Subtract(n,C2)),Plus(CSymbol,Times(BSymbol,Tan(Plus(a,Times(b,x)))),Times(ASymbol,Sqr(Tan(Plus(a,Times(b,x))))))),x),x),And(FreeQ(List(a,b,c,ASymbol,BSymbol,CSymbol,n),x),KnownTangentIntegrandQ(u,x)))),
IIntegrate(4248,Integrate(Times(Power(Times($($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),c_DEFAULT),n_DEFAULT),u_DEFAULT,Plus(A_DEFAULT,Times(B_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Times(C_DEFAULT,Sqr($($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))))),x_Symbol),
    Condition(Dist(Sqr(c),Integrate(Times(ActivateTrig(u),Power(Times(c,Cot(Plus(a,Times(b,x)))),Subtract(n,C2)),Plus(CSymbol,Times(BSymbol,Cot(Plus(a,Times(b,x)))),Times(ASymbol,Sqr(Cot(Plus(a,Times(b,x))))))),x),x),And(FreeQ(List(a,b,c,ASymbol,BSymbol,CSymbol,n),x),KnownCotangentIntegrandQ(u,x)))),
IIntegrate(4249,Integrate(Times(u_DEFAULT,Plus(Times(Sqr($($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),C_DEFAULT),A_),Power(Times(c_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Dist(Sqr(c),Integrate(Times(ActivateTrig(u),Power(Times(c,Tan(Plus(a,Times(b,x)))),Subtract(n,C2)),Plus(CSymbol,Times(ASymbol,Sqr(Tan(Plus(a,Times(b,x))))))),x),x),And(FreeQ(List(a,b,c,ASymbol,CSymbol,n),x),KnownTangentIntegrandQ(u,x)))),
IIntegrate(4250,Integrate(Times(Power(Times($($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),c_DEFAULT),n_DEFAULT),u_DEFAULT,Plus(A_,Times(C_DEFAULT,Sqr($($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))))),x_Symbol),
    Condition(Dist(Sqr(c),Integrate(Times(ActivateTrig(u),Power(Times(c,Cot(Plus(a,Times(b,x)))),Subtract(n,C2)),Plus(CSymbol,Times(ASymbol,Sqr(Cot(Plus(a,Times(b,x))))))),x),x),And(FreeQ(List(a,b,c,ASymbol,CSymbol,n),x),KnownCotangentIntegrandQ(u,x)))),
IIntegrate(4251,Integrate(Times(Plus(A_DEFAULT,Times($($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),B_DEFAULT),Times(Sqr($($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),C_DEFAULT)),u_),x_Symbol),
    Condition(Integrate(Times(ActivateTrig(u),Plus(CSymbol,Times(BSymbol,Tan(Plus(a,Times(b,x)))),Times(ASymbol,Sqr(Tan(Plus(a,Times(b,x)))))),Power(Tan(Plus(a,Times(b,x))),CN2)),x),And(FreeQ(List(a,b,ASymbol,BSymbol,CSymbol),x),KnownTangentIntegrandQ(u,x)))),
IIntegrate(4252,Integrate(Times(u_,Plus(A_DEFAULT,Times(B_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Times(C_DEFAULT,Sqr($($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))))),x_Symbol),
    Condition(Integrate(Times(ActivateTrig(u),Plus(CSymbol,Times(BSymbol,Cot(Plus(a,Times(b,x)))),Times(ASymbol,Sqr(Cot(Plus(a,Times(b,x)))))),Power(Cot(Plus(a,Times(b,x))),CN2)),x),And(FreeQ(List(a,b,ASymbol,BSymbol,CSymbol),x),KnownCotangentIntegrandQ(u,x)))),
IIntegrate(4253,Integrate(Times(Plus(Times(Sqr($($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),C_DEFAULT),A_),u_),x_Symbol),
    Condition(Integrate(Times(ActivateTrig(u),Plus(CSymbol,Times(ASymbol,Sqr(Tan(Plus(a,Times(b,x)))))),Power(Tan(Plus(a,Times(b,x))),CN2)),x),And(FreeQ(List(a,b,ASymbol,CSymbol),x),KnownTangentIntegrandQ(u,x)))),
IIntegrate(4254,Integrate(Times(u_,Plus(A_,Times(C_DEFAULT,Sqr($($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))))),x_Symbol),
    Condition(Integrate(Times(ActivateTrig(u),Plus(CSymbol,Times(ASymbol,Sqr(Cot(Plus(a,Times(b,x)))))),Power(Cot(Plus(a,Times(b,x))),CN2)),x),And(FreeQ(List(a,b,ASymbol,CSymbol),x),KnownCotangentIntegrandQ(u,x)))),
IIntegrate(4255,Integrate(Times(u_,Plus(A_DEFAULT,Times($($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),C_DEFAULT),Times(B_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Integrate(Times(ActivateTrig(u),Plus(CSymbol,Times(ASymbol,Tan(Plus(a,Times(b,x)))),Times(BSymbol,Sqr(Tan(Plus(a,Times(b,x)))))),Power(Tan(Plus(a,Times(b,x))),CN1)),x),FreeQ(List(a,b,ASymbol,BSymbol,CSymbol),x))),
IIntegrate(4256,Integrate(Times(u_,Plus(Times(A_DEFAULT,Power($($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),Times(B_DEFAULT,Power($($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),$p("n1"))),Times(C_DEFAULT,Power($($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),$p("n2"))))),x_Symbol),
    Condition(Integrate(Times(ActivateTrig(u),Power(Tan(Plus(a,Times(b,x))),n),Plus(ASymbol,Times(BSymbol,Tan(Plus(a,Times(b,x)))),Times(CSymbol,Sqr(Tan(Plus(a,Times(b,x))))))),x),And(FreeQ(List(a,b,ASymbol,BSymbol,CSymbol,n),x),EqQ($s("n1"),Plus(n,C1)),EqQ($s("n2"),Plus(n,C2))))),
IIntegrate(4257,Integrate(Times(Plus(Times(Power($($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),A_DEFAULT),Times(Power($($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),$p("n1")),B_DEFAULT),Times(Power($($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),$p("n2")),C_DEFAULT)),u_),x_Symbol),
    Condition(Integrate(Times(ActivateTrig(u),Power(Cot(Plus(a,Times(b,x))),n),Plus(ASymbol,Times(BSymbol,Cot(Plus(a,Times(b,x)))),Times(CSymbol,Sqr(Cot(Plus(a,Times(b,x))))))),x),And(FreeQ(List(a,b,ASymbol,BSymbol,CSymbol,n),x),EqQ($s("n1"),Plus(n,C1)),EqQ($s("n2"),Plus(n,C2))))),
IIntegrate(4258,Integrate(Times(Power(Times($($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),d_DEFAULT),n_DEFAULT),u_,Power(Times(c_DEFAULT,$($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(Times(c,Sin(Plus(a,Times(b,x)))),m),Power(Times(d,Csc(Plus(a,Times(b,x)))),m)),Integrate(Times(ActivateTrig(u),Power(Times(d,Csc(Plus(a,Times(b,x)))),Subtract(n,m))),x),x),And(FreeQ(List(a,b,c,d,m,n),x),KnownSecantIntegrandQ(u,x)))),
IIntegrate(4259,Integrate(Times(Power(Times($($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),c_DEFAULT),m_DEFAULT),u_,Power(Times(d_DEFAULT,$($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(Times(c,Cos(Plus(a,Times(b,x)))),m),Power(Times(d,Sec(Plus(a,Times(b,x)))),m)),Integrate(Times(ActivateTrig(u),Power(Times(d,Sec(Plus(a,Times(b,x)))),Subtract(n,m))),x),x),And(FreeQ(List(a,b,c,d,m,n),x),KnownSecantIntegrandQ(u,x)))),
IIntegrate(4260,Integrate(Times(u_,Power(Times(d_DEFAULT,$($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT),Power(Times(c_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(Times(c,Tan(Plus(a,Times(b,x)))),m),Power(Times(d,Csc(Plus(a,Times(b,x)))),m),Power(Power(Times(d,Sec(Plus(a,Times(b,x)))),m),CN1)),Integrate(Times(ActivateTrig(u),Power(Times(d,Sec(Plus(a,Times(b,x)))),Plus(m,n)),Power(Power(Times(d,Csc(Plus(a,Times(b,x)))),m),CN1)),x),x),And(FreeQ(List(a,b,c,d,m,n),x),KnownSecantIntegrandQ(u,x),Not(IntegerQ(m)))))
  );
}
