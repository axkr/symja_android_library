package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *  
 */
public class UtilityFunctions28 { 
  public static IAST RULES = List( 
ISetDelayed(631,UnifyInertTrigFunction(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times(c_DEFAULT,$($s("§cot"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_))),p_DEFAULT),Power(Times(d_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_DEFAULT)),x_),
    Condition(Times(Power(Times(CN1,d,$($s("§cos"),Plus(e,CPiHalf,Times(f,x)))),m),Power(Plus(a,Times(b,Power(Times(CN1,c,$($s("§tan"),Plus(e,CPiHalf,Times(f,x)))),n))),p)),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),Not(And(EqQ(a,C0),IntegerQ(p)))))),
ISetDelayed(632,UnifyInertTrigFunction(Times(Power(Times(d_DEFAULT,$($s("§cot"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times(c_DEFAULT,$($s("§cot"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_))),p_DEFAULT)),x_),
    Condition(Times(Power(Times(CN1,d,$($s("§tan"),Plus(e,CPiHalf,Times(f,x)))),m),Power(Plus(a,Times(b,Power(Times(CN1,c,$($s("§tan"),Plus(e,CPiHalf,Times(f,x)))),n))),p)),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),Not(And(EqQ(a,C0),IntegerQ(p)))))),
ISetDelayed(633,UnifyInertTrigFunction(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times(c_DEFAULT,$($s("§cot"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_))),p_DEFAULT),Power(Times(d_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_DEFAULT)),x_),
    Condition(Times(Power(Times(CN1,d,$($s("§cot"),Plus(e,CPiHalf,Times(f,x)))),m),Power(Plus(a,Times(b,Power(Times(CN1,c,$($s("§tan"),Plus(e,CPiHalf,Times(f,x)))),n))),p)),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),Not(And(EqQ(a,C0),IntegerQ(p)))))),
ISetDelayed(634,UnifyInertTrigFunction(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times(c_DEFAULT,$($s("§cot"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_))),p_DEFAULT),Power(Times(d_DEFAULT,$($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_DEFAULT)),x_),
    Condition(Times(Power(Times(CN1,d,$($s("§sec"),Plus(e,CPiHalf,Times(f,x)))),m),Power(Plus(a,Times(b,Power(Times(CN1,c,$($s("§tan"),Plus(e,CPiHalf,Times(f,x)))),n))),p)),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),Not(And(EqQ(a,C0),IntegerQ(p)))))),
ISetDelayed(635,UnifyInertTrigFunction(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times(c_DEFAULT,$($s("§cot"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_))),p_DEFAULT),Power(Times(d_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_DEFAULT)),x_),
    Condition(Times(Power(Times(d,$($s("§csc"),Plus(e,CPiHalf,Times(f,x)))),m),Power(Plus(a,Times(b,Power(Times(CN1,c,$($s("§tan"),Plus(e,CPiHalf,Times(f,x)))),n))),p)),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),Not(And(EqQ(a,C0),IntegerQ(p)))))),
ISetDelayed(636,UnifyInertTrigFunction(Power(Plus(a_DEFAULT,Times(b_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),n_DEFAULT),x_),
    Condition(Power(Plus(a,Times(b,$($s("§csc"),Plus(e,CPiHalf,Times(f,x))))),n),FreeQ(List(a,b,e,f,n),x))),
ISetDelayed(637,UnifyInertTrigFunction(Times(Power(Times(g_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),p_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),m_DEFAULT)),x_),
    Condition(Times(Power(Times(g,$($s("§csc"),Plus(e,CPiHalf,Times(f,x)))),p),Power(Plus(a,Times(b,$($s("§csc"),Plus(e,CPiHalf,Times(f,x))))),m)),FreeQ(List(a,b,e,f,g,m,p),x))),
ISetDelayed(638,UnifyInertTrigFunction(Times(Power(Plus(a_,Times(b_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),m_DEFAULT),Power(Times(g_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),p_DEFAULT)),x_),
    Condition(Times(Power(Times(g,$($s("§cos"),Plus(e,Times(CN1,C1D2,Pi),Times(f,x)))),p),Power(Subtract(a,Times(b,$($s("§csc"),Plus(e,Times(CN1,C1D2,Pi),Times(f,x))))),m)),FreeQ(List(a,b,e,f,g,m,p),x))),
ISetDelayed(639,UnifyInertTrigFunction(Times(Power(Times(g_DEFAULT,$($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),p_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),m_DEFAULT)),x_),
    Condition(Times(Power(Times(g,$($s("§sec"),Plus(e,Times(CN1,C1D2,Pi),Times(f,x)))),p),Power(Subtract(a,Times(b,$($s("§csc"),Plus(e,Times(CN1,C1D2,Pi),Times(f,x))))),m)),FreeQ(List(a,b,e,f,g,m,p),x))),
ISetDelayed(640,UnifyInertTrigFunction(Times(Power(Plus(a_,Times(b_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),m_DEFAULT),Power(Times(g_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),p_DEFAULT)),x_),
    Condition(Times(Power(Times(CN1,g,$($s("§cot"),Plus(e,CPiHalf,Times(f,x)))),p),Power(Plus(a,Times(b,$($s("§csc"),Plus(e,CPiHalf,Times(f,x))))),m)),FreeQ(List(a,b,e,f,g,m,p),x))),
ISetDelayed(641,UnifyInertTrigFunction(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),m_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),n_DEFAULT)),x_),
    Condition(Times(Power(Plus(a,Times(b,$($s("§csc"),Plus(e,CPiHalf,Times(f,x))))),m),Power(Plus(c,Times(d,$($s("§csc"),Plus(e,CPiHalf,Times(f,x))))),n)),FreeQ(List(a,b,c,d,e,f,m,n),x))),
ISetDelayed(642,UnifyInertTrigFunction(Times(Power(Times(g_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),p_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),m_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),n_DEFAULT)),x_),
    Condition(Times(Power(Times(g,$($s("§csc"),Plus(e,CPiHalf,Times(f,x)))),p),Power(Plus(a,Times(b,$($s("§csc"),Plus(e,CPiHalf,Times(f,x))))),m),Power(Plus(c,Times(d,$($s("§csc"),Plus(e,CPiHalf,Times(f,x))))),n)),FreeQ(List(a,b,c,d,e,f,g,m,n,p),x))),
ISetDelayed(643,UnifyInertTrigFunction(Times(Power(Times(g_DEFAULT,$($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),p_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),m_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),n_DEFAULT)),x_),
    Condition(Times(Power(Times(g,$($s("§sin"),Plus(e,CPiHalf,Times(f,x)))),p),Power(Plus(a,Times(b,$($s("§csc"),Plus(e,CPiHalf,Times(f,x))))),m),Power(Plus(c,Times(d,$($s("§csc"),Plus(e,CPiHalf,Times(f,x))))),n)),FreeQ(List(a,b,c,d,e,f,g,m,n,p),x))),
ISetDelayed(644,UnifyInertTrigFunction(Times(Plus(A_DEFAULT,Times(B_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),Power(Times(d_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),m_DEFAULT)),x_),
    Condition(Times(Power(Plus(a,Times(b,$($s("§csc"),Plus(e,CPiHalf,Times(f,x))))),m),Power(Times(d,$($s("§csc"),Plus(e,CPiHalf,Times(f,x)))),n),Plus(ASymbol,Times(BSymbol,$($s("§csc"),Plus(e,CPiHalf,Times(f,x)))))),FreeQ(List(a,b,d,e,f,ASymbol,BSymbol,m,n),x))),
ISetDelayed(645,UnifyInertTrigFunction(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),m_DEFAULT),Power(Plus(A_DEFAULT,Times(B_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),p_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),n_DEFAULT)),x_),
    Condition(Times(Power(Plus(a,Times(b,$($s("§csc"),Plus(e,CPiHalf,Times(f,x))))),m),Power(Plus(c,Times(d,$($s("§csc"),Plus(e,CPiHalf,Times(f,x))))),n),Power(Plus(ASymbol,Times(BSymbol,$($s("§csc"),Plus(e,CPiHalf,Times(f,x))))),p)),FreeQ(List(a,b,c,d,e,f,ASymbol,BSymbol,m,n,p),x))),
ISetDelayed(646,UnifyInertTrigFunction(Times(Plus(A_DEFAULT,Times(B_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),Times(C_DEFAULT,Sqr($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))))),Power(Plus(a_DEFAULT,Times(b_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),m_DEFAULT)),x_),
    Condition(Times(Power(Plus(a,Times(b,$($s("§csc"),Plus(e,CPiHalf,Times(f,x))))),m),Plus(ASymbol,Times(BSymbol,$($s("§csc"),Plus(e,CPiHalf,Times(f,x)))),Times(C,Sqr($($s("§csc"),Plus(e,CPiHalf,Times(f,x))))))),FreeQ(List(a,b,e,f,ASymbol,BSymbol,C,m),x))),
ISetDelayed(647,UnifyInertTrigFunction(Times(Plus(A_DEFAULT,Times(C_DEFAULT,Sqr($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))))),Power(Plus(a_DEFAULT,Times(b_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),m_DEFAULT)),x_),
    Condition(Times(Power(Plus(a,Times(b,$($s("§csc"),Plus(e,CPiHalf,Times(f,x))))),m),Plus(ASymbol,Times(C,Sqr($($s("§csc"),Plus(e,CPiHalf,Times(f,x))))))),FreeQ(List(a,b,e,f,ASymbol,C,m),x))),
ISetDelayed(648,UnifyInertTrigFunction(Times(Plus(A_DEFAULT,Times(B_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),Times(C_DEFAULT,Sqr($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))))),Power(Times(d_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),m_DEFAULT)),x_),
    Condition(Times(Power(Plus(a,Times(b,$($s("§csc"),Plus(e,CPiHalf,Times(f,x))))),m),Power(Times(d,$($s("§csc"),Plus(e,CPiHalf,Times(f,x)))),n),Plus(ASymbol,Times(BSymbol,$($s("§csc"),Plus(e,CPiHalf,Times(f,x)))),Times(C,Sqr($($s("§csc"),Plus(e,CPiHalf,Times(f,x))))))),FreeQ(List(a,b,d,e,f,ASymbol,BSymbol,C,m,n),x))),
ISetDelayed(649,UnifyInertTrigFunction(Times(Plus(A_DEFAULT,Times(C_DEFAULT,Sqr($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))))),Power(Times(d_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),m_DEFAULT)),x_),
    Condition(Times(Power(Plus(a,Times(b,$($s("§csc"),Plus(e,CPiHalf,Times(f,x))))),m),Power(Times(d,$($s("§csc"),Plus(e,CPiHalf,Times(f,x)))),n),Plus(ASymbol,Times(C,Sqr($($s("§csc"),Plus(e,CPiHalf,Times(f,x))))))),FreeQ(List(a,b,d,e,f,ASymbol,C,m,n),x))),
ISetDelayed(650,UnifyInertTrigFunction(u_,x_),
    u),
ISetDelayed(651,UnifyInertTrigFunction(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times(c_DEFAULT,$($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_))),p_),x_),
    Condition(Power(Plus(a,Times(b,Power(Times(CN1,c,$($s("§sec"),Plus(e,CPiHalf,Times(f,x)))),n))),p),And(FreeQ(List(a,b,c,e,f,n,p),x),Not(And(EqQ(a,C0),IntegerQ(p)))))),
ISetDelayed(652,UnifyInertTrigFunction(Times(Power(Times(d_DEFAULT,$($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times(c_DEFAULT,$($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_))),p_DEFAULT)),x_),
    Condition(Times(Power(Times(d,$($s("§sin"),Plus(e,CPiHalf,Times(f,x)))),m),Power(Plus(a,Times(b,Power(Times(CN1,c,$($s("§sec"),Plus(e,CPiHalf,Times(f,x)))),n))),p)),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),Not(And(EqQ(a,C0),IntegerQ(p)))))),
ISetDelayed(653,UnifyInertTrigFunction(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times(c_DEFAULT,$($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_))),p_DEFAULT),Power(Times(d_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_DEFAULT)),x_),
    Condition(Times(Power(Times(CN1,d,$($s("§cos"),Plus(e,CPiHalf,Times(f,x)))),m),Power(Plus(a,Times(b,Power(Times(CN1,c,$($s("§sec"),Plus(e,CPiHalf,Times(f,x)))),n))),p)),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),Not(And(EqQ(a,C0),IntegerQ(p)))))),
ISetDelayed(654,UnifyInertTrigFunction(Times(Power(Times(d_DEFAULT,$($s("§cot"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times(c_DEFAULT,$($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_))),p_DEFAULT)),x_),
    Condition(Times(Power(Times(CN1,d,$($s("§tan"),Plus(e,CPiHalf,Times(f,x)))),m),Power(Plus(a,Times(b,Power(Times(CN1,c,$($s("§sec"),Plus(e,CPiHalf,Times(f,x)))),n))),p)),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),Not(And(EqQ(a,C0),IntegerQ(p)))))),
ISetDelayed(655,UnifyInertTrigFunction(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times(c_DEFAULT,$($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_))),p_DEFAULT),Power(Times(d_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_DEFAULT)),x_),
    Condition(Times(Power(Times(CN1,d,$($s("§cot"),Plus(e,CPiHalf,Times(f,x)))),m),Power(Plus(a,Times(b,Power(Times(CN1,c,$($s("§sec"),Plus(e,CPiHalf,Times(f,x)))),n))),p)),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),Not(And(EqQ(a,C0),IntegerQ(p))))))
  );
}
