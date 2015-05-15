package org.matheclipse.core.integrate.rubi45;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi45.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi45.UtilityFunctions.*;

import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *  
 */
public class IntRules54 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Times(u_,Power(Times(c_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_DEFAULT),Power(Times(d_DEFAULT,$($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(c,Tan(Plus(a,Times(b,x)))),m),Power(Times(d,Cos(Plus(a,Times(b,x)))),m),Power(Power(Times(d,Sin(Plus(a,Times(b,x)))),m),-1),Int(Times(ActivateTrig(u),Power(Times(d,Sin(Plus(a,Times(b,x)))),Plus(m,n)),Power(Power(Times(d,Cos(Plus(a,Times(b,x)))),m),-1)),x)),And(And(FreeQ(List(a,b,c,d,m,n),x),KnownSineIntegrandQ(u,x)),Not(IntegerQ(m))))),
ISetDelayed(Int(Times(u_,Power(Times(c_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_DEFAULT),Power(Times(d_DEFAULT,$($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(c,Tan(Plus(a,Times(b,x)))),m),Power(Times(d,Cos(Plus(a,Times(b,x)))),m),Power(Power(Times(d,Sin(Plus(a,Times(b,x)))),m),-1),Int(Times(ActivateTrig(u),Power(Times(d,Sin(Plus(a,Times(b,x)))),m),Power(Power(Times(d,Cos(Plus(a,Times(b,x)))),Plus(m,Negate(n))),-1)),x)),And(And(FreeQ(List(a,b,c,d,m,n),x),KnownSineIntegrandQ(u,x)),Not(IntegerQ(m))))),
ISetDelayed(Int(Times(u_,Power(Times(c_DEFAULT,$($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_DEFAULT),Power(Times(d_DEFAULT,$($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(c,Cot(Plus(a,Times(b,x)))),m),Power(Times(d,Sin(Plus(a,Times(b,x)))),m),Power(Power(Times(d,Cos(Plus(a,Times(b,x)))),m),-1),Int(Times(ActivateTrig(u),Power(Times(d,Cos(Plus(a,Times(b,x)))),m),Power(Power(Times(d,Sin(Plus(a,Times(b,x)))),Plus(m,Negate(n))),-1)),x)),And(And(FreeQ(List(a,b,c,d,m,n),x),KnownSineIntegrandQ(u,x)),Not(IntegerQ(m))))),
ISetDelayed(Int(Times(u_,Power(Times(c_DEFAULT,$($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_DEFAULT),Power(Times(d_DEFAULT,$($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(c,Cot(Plus(a,Times(b,x)))),m),Power(Times(d,Sin(Plus(a,Times(b,x)))),m),Power(Power(Times(d,Cos(Plus(a,Times(b,x)))),m),-1),Int(Times(ActivateTrig(u),Power(Times(d,Cos(Plus(a,Times(b,x)))),Plus(m,n)),Power(Power(Times(d,Sin(Plus(a,Times(b,x)))),m),-1)),x)),And(And(FreeQ(List(a,b,c,d,m,n),x),KnownSineIntegrandQ(u,x)),Not(IntegerQ(m))))),
ISetDelayed(Int(Times(u_,Power(Times(c_DEFAULT,$($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_DEFAULT),Power(Times(d_DEFAULT,$($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(c,Sec(Plus(a,Times(b,x)))),m),Power(Times(d,Cos(Plus(a,Times(b,x)))),m),Int(Times(ActivateTrig(u),Power(Times(d,Cos(Plus(a,Times(b,x)))),Plus(n,Negate(m)))),x)),And(FreeQ(List(a,b,c,d,m,n),x),KnownSineIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Power(Times(c_DEFAULT,$($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_DEFAULT),Power(Times(d_DEFAULT,$($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(c,Csc(Plus(a,Times(b,x)))),m),Power(Times(d,Sin(Plus(a,Times(b,x)))),m),Int(Times(ActivateTrig(u),Power(Times(d,Sin(Plus(a,Times(b,x)))),Plus(n,Negate(m)))),x)),And(FreeQ(List(a,b,c,d,m,n),x),KnownSineIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Power(Times(c_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(c,Tan(Plus(a,Times(b,x)))),m),Power(Times(c,Cos(Plus(a,Times(b,x)))),m),Power(Power(Times(c,Sin(Plus(a,Times(b,x)))),m),-1),Int(Times(ActivateTrig(u),Power(Times(c,Sin(Plus(a,Times(b,x)))),m),Power(Power(Times(c,Cos(Plus(a,Times(b,x)))),m),-1)),x)),And(And(FreeQ(List(a,b,c,m),x),Not(IntegerQ(m))),KnownSineIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Power(Times(c_DEFAULT,$($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(c,Cot(Plus(a,Times(b,x)))),m),Power(Times(c,Sin(Plus(a,Times(b,x)))),m),Power(Power(Times(c,Cos(Plus(a,Times(b,x)))),m),-1),Int(Times(ActivateTrig(u),Power(Times(c,Cos(Plus(a,Times(b,x)))),m),Power(Power(Times(c,Sin(Plus(a,Times(b,x)))),m),-1)),x)),And(And(FreeQ(List(a,b,c,m),x),Not(IntegerQ(m))),KnownSineIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Power(Times(c_DEFAULT,$($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(c,Sec(Plus(a,Times(b,x)))),m),Power(Times(c,Cos(Plus(a,Times(b,x)))),m),Int(Times(ActivateTrig(u),Power(Power(Times(c,Cos(Plus(a,Times(b,x)))),m),-1)),x)),And(And(FreeQ(List(a,b,c,m),x),Not(IntegerQ(m))),KnownSineIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Power(Times(c_DEFAULT,$($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(c,Csc(Plus(a,Times(b,x)))),m),Power(Times(c,Sin(Plus(a,Times(b,x)))),m),Int(Times(ActivateTrig(u),Power(Power(Times(c,Sin(Plus(a,Times(b,x)))),m),-1)),x)),And(And(FreeQ(List(a,b,c,m),x),Not(IntegerQ(m))),KnownSineIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Power(Times(c_DEFAULT,$($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT),Plus(A_,Times(B_DEFAULT,$($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Times(c,Int(Times(ActivateTrig(u),Power(Times(c,Sin(Plus(a,Times(b,x)))),Plus(n,Negate(C1))),Plus(BSymbol,Times(ASymbol,Sin(Plus(a,Times(b,x)))))),x)),And(FreeQ(List(a,b,c,ASymbol,BSymbol,n),x),KnownSineIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Power(Times(c_DEFAULT,$($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT),Plus(A_,Times(B_DEFAULT,$($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Times(c,Int(Times(ActivateTrig(u),Power(Times(c,Cos(Plus(a,Times(b,x)))),Plus(n,Negate(C1))),Plus(BSymbol,Times(ASymbol,Cos(Plus(a,Times(b,x)))))),x)),And(FreeQ(List(a,b,c,ASymbol,BSymbol,n),x),KnownSineIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Plus(A_,Times(B_DEFAULT,$($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus(BSymbol,Times(ASymbol,Sin(Plus(a,Times(b,x))))),Power(Sin(Plus(a,Times(b,x))),-1)),x),And(FreeQ(List(a,b,ASymbol,BSymbol),x),KnownSineIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Plus(A_,Times(B_DEFAULT,$($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus(BSymbol,Times(ASymbol,Cos(Plus(a,Times(b,x))))),Power(Cos(Plus(a,Times(b,x))),-1)),x),And(FreeQ(List(a,b,ASymbol,BSymbol),x),KnownSineIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_DEFAULT,Plus(A_DEFAULT,Times(B_DEFAULT,$($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Times(C_DEFAULT,Sqr($($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),Power(Times(c_DEFAULT,$($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Times(Sqr(c),Int(Times(ActivateTrig(u),Power(Times(c,Sin(Plus(a,Times(b,x)))),Plus(n,Negate(C2))),Plus(CSymbol,Times(BSymbol,Sin(Plus(a,Times(b,x)))),Times(ASymbol,Sqr(Sin(Plus(a,Times(b,x))))))),x)),And(FreeQ(List(a,b,c,ASymbol,BSymbol,CSymbol,n),x),KnownSineIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_DEFAULT,Plus(A_DEFAULT,Times(B_DEFAULT,$($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Times(C_DEFAULT,Sqr($($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),Power(Times(c_DEFAULT,$($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Times(Sqr(c),Int(Times(ActivateTrig(u),Power(Times(c,Cos(Plus(a,Times(b,x)))),Plus(n,Negate(C2))),Plus(CSymbol,Times(BSymbol,Cos(Plus(a,Times(b,x)))),Times(ASymbol,Sqr(Cos(Plus(a,Times(b,x))))))),x)),And(FreeQ(List(a,b,c,ASymbol,BSymbol,CSymbol,n),x),KnownSineIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_DEFAULT,Power(Times(c_DEFAULT,$($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT),Plus(A_,Times(C_DEFAULT,Sqr($($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))))),x_Symbol),
    Condition(Times(Sqr(c),Int(Times(ActivateTrig(u),Power(Times(c,Sin(Plus(a,Times(b,x)))),Plus(n,Negate(C2))),Plus(CSymbol,Times(ASymbol,Sqr(Sin(Plus(a,Times(b,x))))))),x)),And(FreeQ(List(a,b,c,ASymbol,CSymbol,n),x),KnownSineIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_DEFAULT,Power(Times(c_DEFAULT,$($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT),Plus(A_,Times(C_DEFAULT,Sqr($($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))))),x_Symbol),
    Condition(Times(Sqr(c),Int(Times(ActivateTrig(u),Power(Times(c,Cos(Plus(a,Times(b,x)))),Plus(n,Negate(C2))),Plus(CSymbol,Times(ASymbol,Sqr(Cos(Plus(a,Times(b,x))))))),x)),And(FreeQ(List(a,b,c,ASymbol,CSymbol,n),x),KnownSineIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Plus(A_DEFAULT,Times(B_DEFAULT,$($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Times(C_DEFAULT,Sqr($($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))))),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus(CSymbol,Times(BSymbol,Sin(Plus(a,Times(b,x)))),Times(ASymbol,Sqr(Sin(Plus(a,Times(b,x)))))),Power(Sin(Plus(a,Times(b,x))),-2)),x),And(FreeQ(List(a,b,ASymbol,BSymbol,CSymbol),x),KnownSineIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Plus(A_DEFAULT,Times(B_DEFAULT,$($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Times(C_DEFAULT,Sqr($($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))))),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus(CSymbol,Times(BSymbol,Cos(Plus(a,Times(b,x)))),Times(ASymbol,Sqr(Cos(Plus(a,Times(b,x)))))),Power(Cos(Plus(a,Times(b,x))),-2)),x),And(FreeQ(List(a,b,ASymbol,BSymbol,CSymbol),x),KnownSineIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Plus(A_,Times(C_DEFAULT,Sqr($($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))))),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus(CSymbol,Times(ASymbol,Sqr(Sin(Plus(a,Times(b,x)))))),Power(Sin(Plus(a,Times(b,x))),-2)),x),And(FreeQ(List(a,b,ASymbol,CSymbol),x),KnownSineIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Plus(A_,Times(C_DEFAULT,Sqr($($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))))),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus(CSymbol,Times(ASymbol,Sqr(Cos(Plus(a,Times(b,x)))))),Power(Cos(Plus(a,Times(b,x))),-2)),x),And(FreeQ(List(a,b,ASymbol,CSymbol),x),KnownSineIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Plus(A_DEFAULT,Times(B_DEFAULT,$($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Times(C_DEFAULT,$($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus(CSymbol,Times(ASymbol,Sin(Plus(a,Times(b,x)))),Times(BSymbol,Sqr(Sin(Plus(a,Times(b,x)))))),Power(Sin(Plus(a,Times(b,x))),-1)),x),FreeQ(List(a,b,ASymbol,BSymbol,CSymbol),x))),
ISetDelayed(Int(Times(u_,Plus(A_DEFAULT,Times(B_DEFAULT,$($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Times(C_DEFAULT,$($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus(CSymbol,Times(ASymbol,Cos(Plus(a,Times(b,x)))),Times(BSymbol,Sqr(Cos(Plus(a,Times(b,x)))))),Power(Cos(Plus(a,Times(b,x))),-1)),x),FreeQ(List(a,b,ASymbol,BSymbol,CSymbol),x))),
ISetDelayed(Int(Times(u_,Plus(Times(A_DEFAULT,Power($($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),Times(B_DEFAULT,Power($($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),$p("n1"))),Times(C_DEFAULT,Power($($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),$p("n2"))))),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Power(Sin(Plus(a,Times(b,x))),n),Plus(ASymbol,Times(BSymbol,Sin(Plus(a,Times(b,x)))),Times(CSymbol,Sqr(Sin(Plus(a,Times(b,x))))))),x),And(And(FreeQ(List(a,b,ASymbol,BSymbol,CSymbol,n),x),ZeroQ(Plus($s("n1"),Negate(n),Negate(C1)))),ZeroQ(Plus($s("n2"),Negate(n),Negate(C2)))))),
ISetDelayed(Int(Times(u_,Plus(Times(A_DEFAULT,Power($($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),Times(B_DEFAULT,Power($($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),$p("n1"))),Times(C_DEFAULT,Power($($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),$p("n2"))))),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Power(Cos(Plus(a,Times(b,x))),n),Plus(ASymbol,Times(BSymbol,Cos(Plus(a,Times(b,x)))),Times(CSymbol,Sqr(Cos(Plus(a,Times(b,x))))))),x),And(And(FreeQ(List(a,b,ASymbol,BSymbol,CSymbol,n),x),ZeroQ(Plus($s("n1"),Negate(n),Negate(C1)))),ZeroQ(Plus($s("n2"),Negate(n),Negate(C2))))))
  );
}
