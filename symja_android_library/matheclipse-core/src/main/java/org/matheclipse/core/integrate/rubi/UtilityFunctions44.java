package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class UtilityFunctions44 { 
  public static IAST RULES = List( 
ISetDelayed(632,UnifyInertTrigFunction(Power(Plus(a_DEFAULT,Times($($s("§cot"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT)),n_DEFAULT),x_),
    Condition(Power(Subtract(a,Times(b,$($s("§tan"),Plus(e,CPiHalf,Times(f,x))))),n),FreeQ(List(a,b,e,f,n),x))),
ISetDelayed(633,UnifyInertTrigFunction(Times(Power(Plus(a_,Times($($s("§cot"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT)),n_DEFAULT),Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),d_DEFAULT),m_DEFAULT)),x_),
    Condition(Times(Power(Times(d,$($s("§sec"),Plus(e,Times(CN1,C1D2,Pi),Times(f,x)))),m),Power(Subtract(a,Times(b,$($s("§tan"),Plus(e,Times(CN1,C1D2,Pi),Times(f,x))))),n)),FreeQ(List(a,b,d,e,f,m,n),x))),
ISetDelayed(634,UnifyInertTrigFunction(Times(Power(Plus(a_,Times($($s("§cot"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT)),n_DEFAULT),Power(Times($($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),d_DEFAULT),m_DEFAULT)),x_),
    Condition(Times(Power(Times(d,$($s("§cos"),Plus(e,Times(CN1,C1D2,Pi),Times(f,x)))),m),Power(Subtract(a,Times(b,$($s("§tan"),Plus(e,Times(CN1,C1D2,Pi),Times(f,x))))),n)),FreeQ(List(a,b,d,e,f,m,n),x))),
ISetDelayed(635,UnifyInertTrigFunction(Times(Power(Plus(a_,Times($($s("§cot"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT)),n_DEFAULT),Power(Times($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),d_DEFAULT),m_DEFAULT)),x_),
    Condition(Times(Power(Times(d,$($s("§sin"),Plus(e,CPiHalf,Times(f,x)))),m),Power(Subtract(a,Times(b,$($s("§tan"),Plus(e,CPiHalf,Times(f,x))))),n)),FreeQ(List(a,b,d,e,f,m,n),x))),
ISetDelayed(636,UnifyInertTrigFunction(Times(Power(Plus(a_,Times($($s("§cot"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT)),n_DEFAULT),Power(Times($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),d_DEFAULT),m_DEFAULT)),x_),
    Condition(Times(Power(Times(d,$($s("§csc"),Plus(e,CPiHalf,Times(f,x)))),m),Power(Subtract(a,Times(b,$($s("§tan"),Plus(e,CPiHalf,Times(f,x))))),n)),FreeQ(List(a,b,d,e,f,m,n),x))),
ISetDelayed(637,UnifyInertTrigFunction(Times(Power(Plus(a_DEFAULT,Times($($s("§cot"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT)),m_DEFAULT),Power(Plus(c_DEFAULT,Times($($s("§cot"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),d_DEFAULT)),n_DEFAULT)),x_),
    Condition(Times(Power(Subtract(a,Times(b,$($s("§tan"),Plus(e,CPiHalf,Times(f,x))))),m),Power(Subtract(c,Times(d,$($s("§tan"),Plus(e,CPiHalf,Times(f,x))))),n)),FreeQ(List(a,b,c,d,e,f,m,n),x))),
ISetDelayed(638,UnifyInertTrigFunction(Times(Power(Plus(a_DEFAULT,Times($($s("§cot"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT)),m_DEFAULT),Power(Plus(c_DEFAULT,Times($($s("§cot"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),d_DEFAULT)),n_DEFAULT),Power(Times($($s("§cot"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),g_DEFAULT),p_DEFAULT)),x_),
    Condition(Times(Power(Times(CN1,g,$($s("§tan"),Plus(e,CPiHalf,Times(f,x)))),p),Power(Subtract(a,Times(b,$($s("§tan"),Plus(e,CPiHalf,Times(f,x))))),m),Power(Subtract(c,Times(d,$($s("§tan"),Plus(e,CPiHalf,Times(f,x))))),n)),FreeQ(List(a,b,c,d,e,f,g,m,n,p),x))),
ISetDelayed(639,UnifyInertTrigFunction(Times(Power(Plus(a_DEFAULT,Times($($s("§cot"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT)),m_DEFAULT),Power(Plus(c_DEFAULT,Times($($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),d_DEFAULT)),n_DEFAULT),Power(Times($($s("§cot"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),g_DEFAULT),p_DEFAULT)),x_),
    Condition(Times(Power(Times(CN1,g,$($s("§tan"),Plus(e,CPiHalf,Times(f,x)))),p),Power(Subtract(a,Times(b,$($s("§tan"),Plus(e,CPiHalf,Times(f,x))))),m),Power(Subtract(c,Times(d,$($s("§cot"),Plus(e,CPiHalf,Times(f,x))))),n)),FreeQ(List(a,b,c,d,e,f,g,m,n,p),x))),
ISetDelayed(640,UnifyInertTrigFunction(Times(Power(Plus(a_DEFAULT,Times($($s("§cot"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT)),m_DEFAULT),Plus(A_DEFAULT,Times($($s("§cot"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),B_DEFAULT)),Power(Plus(c_DEFAULT,Times($($s("§cot"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),d_DEFAULT)),n_DEFAULT)),x_),
    Condition(Times(Power(Subtract(a,Times(b,$($s("§tan"),Plus(e,CPiHalf,Times(f,x))))),m),Power(Subtract(c,Times(d,$($s("§tan"),Plus(e,CPiHalf,Times(f,x))))),n),Subtract(ASymbol,Times(BSymbol,$($s("§tan"),Plus(e,CPiHalf,Times(f,x)))))),FreeQ(List(a,b,c,d,e,f,ASymbol,BSymbol,m,n),x))),
ISetDelayed(641,UnifyInertTrigFunction(Times(Power(Plus(a_DEFAULT,Times($($s("§cot"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT)),m_DEFAULT),Plus(A_DEFAULT,Times($($s("§cot"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),B_DEFAULT),Times(Sqr($($s("§cot"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),C_DEFAULT))),x_),
    Condition(Times(Power(Subtract(a,Times(b,$($s("§tan"),Plus(e,CPiHalf,Times(f,x))))),m),Plus(ASymbol,Times(CN1,BSymbol,$($s("§tan"),Plus(e,CPiHalf,Times(f,x)))),Times(CSymbol,Sqr($($s("§tan"),Plus(e,CPiHalf,Times(f,x))))))),FreeQ(List(a,b,e,f,ASymbol,BSymbol,CSymbol,m),x))),
ISetDelayed(642,UnifyInertTrigFunction(Times(Power(Plus(a_DEFAULT,Times($($s("§cot"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT)),m_DEFAULT),Plus(A_DEFAULT,Times(Sqr($($s("§cot"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),C_DEFAULT))),x_),
    Condition(Times(Power(Subtract(a,Times(b,$($s("§tan"),Plus(e,CPiHalf,Times(f,x))))),m),Plus(ASymbol,Times(CSymbol,Sqr($($s("§tan"),Plus(e,CPiHalf,Times(f,x))))))),FreeQ(List(a,b,e,f,ASymbol,CSymbol,m),x))),
ISetDelayed(643,UnifyInertTrigFunction(Times(Power(Plus(a_DEFAULT,Times($($s("§cot"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT)),m_DEFAULT),Plus(A_DEFAULT,Times($($s("§cot"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),B_DEFAULT),Times(Sqr($($s("§cot"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),C_DEFAULT)),Power(Plus(c_DEFAULT,Times($($s("§cot"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),d_DEFAULT)),n_DEFAULT)),x_),
    Condition(Times(Power(Subtract(a,Times(b,$($s("§tan"),Plus(e,CPiHalf,Times(f,x))))),m),Power(Subtract(c,Times(d,$($s("§tan"),Plus(e,CPiHalf,Times(f,x))))),n),Plus(ASymbol,Times(CN1,BSymbol,$($s("§tan"),Plus(e,CPiHalf,Times(f,x)))),Times(CSymbol,Sqr($($s("§tan"),Plus(e,CPiHalf,Times(f,x))))))),FreeQ(List(a,b,c,d,e,f,ASymbol,BSymbol,CSymbol,m,n),x))),
ISetDelayed(644,UnifyInertTrigFunction(Times(Power(Plus(a_DEFAULT,Times($($s("§cot"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT)),m_DEFAULT),Plus(A_DEFAULT,Times(Sqr($($s("§cot"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),C_DEFAULT)),Power(Plus(c_DEFAULT,Times($($s("§cot"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),d_DEFAULT)),n_DEFAULT)),x_),
    Condition(Times(Power(Subtract(a,Times(b,$($s("§tan"),Plus(e,CPiHalf,Times(f,x))))),m),Power(Subtract(c,Times(d,$($s("§tan"),Plus(e,CPiHalf,Times(f,x))))),n),Plus(ASymbol,Times(CSymbol,Sqr($($s("§tan"),Plus(e,CPiHalf,Times(f,x))))))),FreeQ(List(a,b,c,d,e,f,ASymbol,CSymbol,m,n),x))),
ISetDelayed(645,UnifyInertTrigFunction(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times($($s("§cot"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),c_DEFAULT),n_))),p_),x_),
    Condition(Power(Plus(a,Times(b,Power(Times(CN1,c,$($s("§tan"),Plus(e,CPiHalf,Times(f,x)))),n))),p),And(FreeQ(List(a,b,c,e,f,n,p),x),Not(And(EqQ(a,C0),IntegerQ(p)))))),
ISetDelayed(646,UnifyInertTrigFunction(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times($($s("§cot"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),c_DEFAULT),n_))),p_DEFAULT),Power(Times($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),d_DEFAULT),m_DEFAULT)),x_),
    Condition(Times(Power(Times(d,$($s("§sin"),Plus(e,CPiHalf,Times(f,x)))),m),Power(Plus(a,Times(b,Power(Times(CN1,c,$($s("§tan"),Plus(e,CPiHalf,Times(f,x)))),n))),p)),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),Not(And(EqQ(a,C0),IntegerQ(p)))))),
ISetDelayed(647,UnifyInertTrigFunction(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times($($s("§cot"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),c_DEFAULT),n_))),p_DEFAULT),Power(Times($($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),d_DEFAULT),m_DEFAULT)),x_),
    Condition(Times(Power(Times(CN1,d,$($s("§cos"),Plus(e,CPiHalf,Times(f,x)))),m),Power(Plus(a,Times(b,Power(Times(CN1,c,$($s("§tan"),Plus(e,CPiHalf,Times(f,x)))),n))),p)),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),Not(And(EqQ(a,C0),IntegerQ(p)))))),
ISetDelayed(648,UnifyInertTrigFunction(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times($($s("§cot"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),c_DEFAULT),n_))),p_DEFAULT),Power(Times($($s("§cot"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),d_DEFAULT),m_DEFAULT)),x_),
    Condition(Times(Power(Times(CN1,d,$($s("§tan"),Plus(e,CPiHalf,Times(f,x)))),m),Power(Plus(a,Times(b,Power(Times(CN1,c,$($s("§tan"),Plus(e,CPiHalf,Times(f,x)))),n))),p)),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),Not(And(EqQ(a,C0),IntegerQ(p)))))),
ISetDelayed(649,UnifyInertTrigFunction(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times($($s("§cot"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),c_DEFAULT),n_))),p_DEFAULT),Power(Times($($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),d_DEFAULT),m_DEFAULT)),x_),
    Condition(Times(Power(Times(CN1,d,$($s("§cot"),Plus(e,CPiHalf,Times(f,x)))),m),Power(Plus(a,Times(b,Power(Times(CN1,c,$($s("§tan"),Plus(e,CPiHalf,Times(f,x)))),n))),p)),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),Not(And(EqQ(a,C0),IntegerQ(p)))))),
ISetDelayed(650,UnifyInertTrigFunction(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times($($s("§cot"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),c_DEFAULT),n_))),p_DEFAULT),Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),d_DEFAULT),m_DEFAULT)),x_),
    Condition(Times(Power(Times(CN1,d,$($s("§sec"),Plus(e,CPiHalf,Times(f,x)))),m),Power(Plus(a,Times(b,Power(Times(CN1,c,$($s("§tan"),Plus(e,CPiHalf,Times(f,x)))),n))),p)),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),Not(And(EqQ(a,C0),IntegerQ(p)))))),
ISetDelayed(651,UnifyInertTrigFunction(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times($($s("§cot"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),c_DEFAULT),n_))),p_DEFAULT),Power(Times($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),d_DEFAULT),m_DEFAULT)),x_),
    Condition(Times(Power(Times(d,$($s("§csc"),Plus(e,CPiHalf,Times(f,x)))),m),Power(Plus(a,Times(b,Power(Times(CN1,c,$($s("§tan"),Plus(e,CPiHalf,Times(f,x)))),n))),p)),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),Not(And(EqQ(a,C0),IntegerQ(p))))))
  );
}
