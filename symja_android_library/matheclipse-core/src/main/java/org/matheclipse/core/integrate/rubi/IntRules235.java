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
class IntRules235 { 
  public static IAST RULES = List( 
IIntegrate(4701,Integrate(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),Power(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(x,Plus(m,Negate(n),C1)),Power(Csc(Plus(a,Times(b,Power(x,n)))),Subtract(p,C1)),Power(Times(b,n,Subtract(p,C1)),CN1)),x),Simp(Star(Times(Plus(m,Negate(n),C1),Power(Times(b,n,Subtract(p,C1)),CN1)),Integrate(Times(Power(x,Subtract(m,n)),Power(Csc(Plus(a,Times(b,Power(x,n)))),Subtract(p,C1))),x)),x)),And(FreeQ(list(a,b,p),x),IntegerQ(n),GeQ(Subtract(m,n),C0),NeQ(p,C1)))),
IIntegrate(4702,Integrate(Times(u_,Power(Times(d_DEFAULT,$($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT),Power(Times(c_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(Times(c,Tan(Plus(a,Times(b,x)))),m),Power(Times(d,Cos(Plus(a,Times(b,x)))),m),Power(Power(Times(d,Sin(Plus(a,Times(b,x)))),m),CN1)),Integrate(Times(ActivateTrig(u),Power(Times(d,Sin(Plus(a,Times(b,x)))),Plus(m,n)),Power(Power(Times(d,Cos(Plus(a,Times(b,x)))),m),CN1)),x)),x),And(FreeQ(List(a,b,c,d,m,n),x),KnownSineIntegrandQ(u,x),Not(IntegerQ(m))))),
IIntegrate(4703,Integrate(Times(Power(Times($($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),d_DEFAULT),n_DEFAULT),u_,Power(Times(c_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(Times(c,Tan(Plus(a,Times(b,x)))),m),Power(Times(d,Cos(Plus(a,Times(b,x)))),m),Power(Power(Times(d,Sin(Plus(a,Times(b,x)))),m),CN1)),Integrate(Times(ActivateTrig(u),Power(Times(d,Sin(Plus(a,Times(b,x)))),m),Power(Power(Times(d,Cos(Plus(a,Times(b,x)))),Subtract(m,n)),CN1)),x)),x),And(FreeQ(List(a,b,c,d,m,n),x),KnownSineIntegrandQ(u,x),Not(IntegerQ(m))))),
IIntegrate(4704,Integrate(Times(Power(Times($($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),c_DEFAULT),m_DEFAULT),u_,Power(Times(d_DEFAULT,$($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(Times(c,Cot(Plus(a,Times(b,x)))),m),Power(Times(d,Sin(Plus(a,Times(b,x)))),m),Power(Power(Times(d,Cos(Plus(a,Times(b,x)))),m),CN1)),Integrate(Times(ActivateTrig(u),Power(Times(d,Cos(Plus(a,Times(b,x)))),m),Power(Power(Times(d,Sin(Plus(a,Times(b,x)))),Subtract(m,n)),CN1)),x)),x),And(FreeQ(List(a,b,c,d,m,n),x),KnownSineIntegrandQ(u,x),Not(IntegerQ(m))))),
IIntegrate(4705,Integrate(Times(Power(Times($($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),c_DEFAULT),m_DEFAULT),Power(Times($($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),d_DEFAULT),n_DEFAULT),u_),x_Symbol),
    Condition(Simp(Star(Times(Power(Times(c,Cot(Plus(a,Times(b,x)))),m),Power(Times(d,Sin(Plus(a,Times(b,x)))),m),Power(Power(Times(d,Cos(Plus(a,Times(b,x)))),m),CN1)),Integrate(Times(ActivateTrig(u),Power(Times(d,Cos(Plus(a,Times(b,x)))),Plus(m,n)),Power(Power(Times(d,Sin(Plus(a,Times(b,x)))),m),CN1)),x)),x),And(FreeQ(List(a,b,c,d,m,n),x),KnownSineIntegrandQ(u,x),Not(IntegerQ(m))))),
IIntegrate(4706,Integrate(Times(Power(Times($($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),d_DEFAULT),n_DEFAULT),u_,Power(Times(c_DEFAULT,$($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(Times(c,Csc(Plus(a,Times(b,x)))),m),Power(Times(d,Sin(Plus(a,Times(b,x)))),m)),Integrate(Times(ActivateTrig(u),Power(Times(d,Sin(Plus(a,Times(b,x)))),Subtract(n,m))),x)),x),And(FreeQ(List(a,b,c,d,m,n),x),KnownSineIntegrandQ(u,x)))),
IIntegrate(4707,Integrate(Times(u_,Power(Times(c_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(Times(c,Tan(Plus(a,Times(b,x)))),m),Power(Times(c,Cos(Plus(a,Times(b,x)))),m),Power(Power(Times(c,Sin(Plus(a,Times(b,x)))),m),CN1)),Integrate(Times(ActivateTrig(u),Power(Times(c,Sin(Plus(a,Times(b,x)))),m),Power(Power(Times(c,Cos(Plus(a,Times(b,x)))),m),CN1)),x)),x),And(FreeQ(List(a,b,c,m),x),Not(IntegerQ(m)),KnownSineIntegrandQ(u,x)))),
IIntegrate(4708,Integrate(Times(Power(Times($($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),c_DEFAULT),m_DEFAULT),u_),x_Symbol),
    Condition(Simp(Star(Times(Power(Times(c,Cot(Plus(a,Times(b,x)))),m),Power(Times(c,Sin(Plus(a,Times(b,x)))),m),Power(Power(Times(c,Cos(Plus(a,Times(b,x)))),m),CN1)),Integrate(Times(ActivateTrig(u),Power(Times(c,Cos(Plus(a,Times(b,x)))),m),Power(Power(Times(c,Sin(Plus(a,Times(b,x)))),m),CN1)),x)),x),And(FreeQ(List(a,b,c,m),x),Not(IntegerQ(m)),KnownSineIntegrandQ(u,x)))),
IIntegrate(4709,Integrate(Times(u_,Power(Times(c_DEFAULT,$($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(Times(c,Sec(Plus(a,Times(b,x)))),m),Power(Times(c,Cos(Plus(a,Times(b,x)))),m)),Integrate(Times(ActivateTrig(u),Power(Power(Times(c,Cos(Plus(a,Times(b,x)))),m),CN1)),x)),x),And(FreeQ(List(a,b,c,m),x),Not(IntegerQ(m)),KnownSineIntegrandQ(u,x)))),
IIntegrate(4710,Integrate(Times(Power(Times($($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),c_DEFAULT),m_DEFAULT),u_),x_Symbol),
    Condition(Simp(Star(Times(Power(Times(c,Csc(Plus(a,Times(b,x)))),m),Power(Times(c,Sin(Plus(a,Times(b,x)))),m)),Integrate(Times(ActivateTrig(u),Power(Power(Times(c,Sin(Plus(a,Times(b,x)))),m),CN1)),x)),x),And(FreeQ(List(a,b,c,m),x),Not(IntegerQ(m)),KnownSineIntegrandQ(u,x)))),
IIntegrate(4711,Integrate(Times(Plus(Times($($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),B_DEFAULT),A_),u_,Power(Times(c_DEFAULT,$($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Simp(Star(c,Integrate(Times(ActivateTrig(u),Power(Times(c,Sin(Plus(a,Times(b,x)))),Subtract(n,C1)),Plus(BSymbol,Times(ASymbol,Sin(Plus(a,Times(b,x)))))),x)),x),And(FreeQ(List(a,b,c,ASymbol,BSymbol,n),x),KnownSineIntegrandQ(u,x)))),
IIntegrate(4712,Integrate(Times(Power(Times($($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),c_DEFAULT),n_DEFAULT),u_,Plus(A_,Times(B_DEFAULT,$($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Simp(Star(c,Integrate(Times(ActivateTrig(u),Power(Times(c,Cos(Plus(a,Times(b,x)))),Subtract(n,C1)),Plus(BSymbol,Times(ASymbol,Cos(Plus(a,Times(b,x)))))),x)),x),And(FreeQ(List(a,b,c,ASymbol,BSymbol,n),x),KnownSineIntegrandQ(u,x)))),
IIntegrate(4713,Integrate(Times(Plus(Times($($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),B_DEFAULT),A_),u_),x_Symbol),
    Condition(Integrate(Times(ActivateTrig(u),Plus(BSymbol,Times(ASymbol,Sin(Plus(a,Times(b,x))))),Power(Sin(Plus(a,Times(b,x))),CN1)),x),And(FreeQ(List(a,b,ASymbol,BSymbol),x),KnownSineIntegrandQ(u,x)))),
IIntegrate(4714,Integrate(Times(u_,Plus(A_,Times(B_DEFAULT,$($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Integrate(Times(ActivateTrig(u),Plus(BSymbol,Times(ASymbol,Cos(Plus(a,Times(b,x))))),Power(Cos(Plus(a,Times(b,x))),CN1)),x),And(FreeQ(List(a,b,ASymbol,BSymbol),x),KnownSineIntegrandQ(u,x)))),
IIntegrate(4715,Integrate(Times(Plus(A_DEFAULT,Times($($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),B_DEFAULT),Times(Sqr($($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),C_DEFAULT)),u_DEFAULT,Power(Times(c_DEFAULT,$($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Sqr(c),Integrate(Times(ActivateTrig(u),Power(Times(c,Sin(Plus(a,Times(b,x)))),Subtract(n,C2)),Plus(CSymbol,Times(BSymbol,Sin(Plus(a,Times(b,x)))),Times(ASymbol,Sqr(Sin(Plus(a,Times(b,x))))))),x)),x),And(FreeQ(List(a,b,c,ASymbol,BSymbol,CSymbol,n),x),KnownSineIntegrandQ(u,x)))),
IIntegrate(4716,Integrate(Times(Power(Times($($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),c_DEFAULT),n_DEFAULT),u_DEFAULT,Plus(A_DEFAULT,Times(B_DEFAULT,$($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Times(C_DEFAULT,Sqr($($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))))),x_Symbol),
    Condition(Simp(Star(Sqr(c),Integrate(Times(ActivateTrig(u),Power(Times(c,Cos(Plus(a,Times(b,x)))),Subtract(n,C2)),Plus(CSymbol,Times(BSymbol,Cos(Plus(a,Times(b,x)))),Times(ASymbol,Sqr(Cos(Plus(a,Times(b,x))))))),x)),x),And(FreeQ(List(a,b,c,ASymbol,BSymbol,CSymbol,n),x),KnownSineIntegrandQ(u,x)))),
IIntegrate(4717,Integrate(Times(u_DEFAULT,Plus(Times(Sqr($($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),C_DEFAULT),A_),Power(Times(c_DEFAULT,$($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Sqr(c),Integrate(Times(ActivateTrig(u),Power(Times(c,Sin(Plus(a,Times(b,x)))),Subtract(n,C2)),Plus(CSymbol,Times(ASymbol,Sqr(Sin(Plus(a,Times(b,x))))))),x)),x),And(FreeQ(List(a,b,c,ASymbol,CSymbol,n),x),KnownSineIntegrandQ(u,x)))),
IIntegrate(4718,Integrate(Times(Power(Times($($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),c_DEFAULT),n_DEFAULT),u_DEFAULT,Plus(A_,Times(C_DEFAULT,Sqr($($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))))),x_Symbol),
    Condition(Simp(Star(Sqr(c),Integrate(Times(ActivateTrig(u),Power(Times(c,Cos(Plus(a,Times(b,x)))),Subtract(n,C2)),Plus(CSymbol,Times(ASymbol,Sqr(Cos(Plus(a,Times(b,x))))))),x)),x),And(FreeQ(List(a,b,c,ASymbol,CSymbol,n),x),KnownSineIntegrandQ(u,x)))),
IIntegrate(4719,Integrate(Times(Plus(A_DEFAULT,Times($($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),B_DEFAULT),Times(Sqr($($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),C_DEFAULT)),u_),x_Symbol),
    Condition(Integrate(Times(ActivateTrig(u),Plus(CSymbol,Times(BSymbol,Sin(Plus(a,Times(b,x)))),Times(ASymbol,Sqr(Sin(Plus(a,Times(b,x)))))),Power(Sin(Plus(a,Times(b,x))),CN2)),x),And(FreeQ(List(a,b,ASymbol,BSymbol,CSymbol),x),KnownSineIntegrandQ(u,x)))),
IIntegrate(4720,Integrate(Times(u_,Plus(A_DEFAULT,Times(B_DEFAULT,$($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Times(C_DEFAULT,Sqr($($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))))),x_Symbol),
    Condition(Integrate(Times(ActivateTrig(u),Plus(CSymbol,Times(BSymbol,Cos(Plus(a,Times(b,x)))),Times(ASymbol,Sqr(Cos(Plus(a,Times(b,x)))))),Power(Cos(Plus(a,Times(b,x))),CN2)),x),And(FreeQ(List(a,b,ASymbol,BSymbol,CSymbol),x),KnownSineIntegrandQ(u,x))))
  );
}
