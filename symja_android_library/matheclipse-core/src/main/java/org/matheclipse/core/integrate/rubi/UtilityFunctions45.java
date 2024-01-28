package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class UtilityFunctions45 { 
  public static IAST RULES = List( 
ISetDelayed(652,UnifyInertTrigFunction(Power(Plus(a_DEFAULT,Times($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT)),n_DEFAULT),x_),
    Condition(Power(Plus(a,Times(b,$($s("§csc"),Plus(e,CPiHalf,Times(f,x))))),n),FreeQ(List(a,b,e,f,n),x))),
ISetDelayed(653,UnifyInertTrigFunction(Times(Power(Plus(a_,Times($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT)),m_DEFAULT),Power(Times($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),g_DEFAULT),p_DEFAULT)),x_),
    Condition(Times(Power(Times(g,$($s("§csc"),Plus(e,CPiHalf,Times(f,x)))),p),Power(Plus(a,Times(b,$($s("§csc"),Plus(e,CPiHalf,Times(f,x))))),m)),FreeQ(List(a,b,e,f,g,m,p),x))),
ISetDelayed(654,UnifyInertTrigFunction(Times(Power(Plus(a_,Times($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT)),m_DEFAULT),Power(Times($($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),g_DEFAULT),p_DEFAULT)),x_),
    Condition(Times(Power(Times(g,$($s("§cos"),Plus(e,Times(CN1,C1D2,Pi),Times(f,x)))),p),Power(Subtract(a,Times(b,$($s("§csc"),Plus(e,Times(CN1,C1D2,Pi),Times(f,x))))),m)),FreeQ(List(a,b,e,f,g,m,p),x))),
ISetDelayed(655,UnifyInertTrigFunction(Times(Power(Plus(a_,Times($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT)),m_DEFAULT),Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),g_DEFAULT),p_DEFAULT)),x_),
    Condition(Times(Power(Times(g,$($s("§sec"),Plus(e,Times(CN1,C1D2,Pi),Times(f,x)))),p),Power(Subtract(a,Times(b,$($s("§csc"),Plus(e,Times(CN1,C1D2,Pi),Times(f,x))))),m)),FreeQ(List(a,b,e,f,g,m,p),x))),
ISetDelayed(656,UnifyInertTrigFunction(Times(Power(Plus(a_,Times($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT)),m_DEFAULT),Power(Times($($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),g_DEFAULT),p_DEFAULT)),x_),
    Condition(Times(Power(Times(CN1,g,$($s("§cot"),Plus(e,CPiHalf,Times(f,x)))),p),Power(Plus(a,Times(b,$($s("§csc"),Plus(e,CPiHalf,Times(f,x))))),m)),FreeQ(List(a,b,e,f,g,m,p),x))),
ISetDelayed(657,UnifyInertTrigFunction(Times(Power(Plus(a_DEFAULT,Times($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT)),m_DEFAULT),Power(Plus(c_DEFAULT,Times($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),d_DEFAULT)),n_DEFAULT)),x_),
    Condition(Times(Power(Plus(a,Times(b,$($s("§csc"),Plus(e,CPiHalf,Times(f,x))))),m),Power(Plus(c,Times(d,$($s("§csc"),Plus(e,CPiHalf,Times(f,x))))),n)),FreeQ(List(a,b,c,d,e,f,m,n),x))),
ISetDelayed(658,UnifyInertTrigFunction(Times(Power(Plus(a_DEFAULT,Times($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT)),m_DEFAULT),Power(Plus(c_DEFAULT,Times($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),d_DEFAULT)),n_DEFAULT),Power(Times($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),g_DEFAULT),p_DEFAULT)),x_),
    Condition(Times(Power(Times(g,$($s("§csc"),Plus(e,CPiHalf,Times(f,x)))),p),Power(Plus(a,Times(b,$($s("§csc"),Plus(e,CPiHalf,Times(f,x))))),m),Power(Plus(c,Times(d,$($s("§csc"),Plus(e,CPiHalf,Times(f,x))))),n)),FreeQ(List(a,b,c,d,e,f,g,m,n,p),x))),
ISetDelayed(659,UnifyInertTrigFunction(Times(Power(Plus(a_DEFAULT,Times($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT)),m_DEFAULT),Power(Plus(c_DEFAULT,Times($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),d_DEFAULT)),n_DEFAULT),Power(Times($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),g_DEFAULT),p_DEFAULT)),x_),
    Condition(Times(Power(Times(g,$($s("§sin"),Plus(e,CPiHalf,Times(f,x)))),p),Power(Plus(a,Times(b,$($s("§csc"),Plus(e,CPiHalf,Times(f,x))))),m),Power(Plus(c,Times(d,$($s("§csc"),Plus(e,CPiHalf,Times(f,x))))),n)),FreeQ(List(a,b,c,d,e,f,g,m,n,p),x))),
ISetDelayed(660,UnifyInertTrigFunction(Times(Power(Plus(a_DEFAULT,Times($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT)),m_DEFAULT),Plus(A_DEFAULT,Times($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),B_DEFAULT)),Power(Times($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),d_DEFAULT),n_DEFAULT)),x_),
    Condition(Times(Power(Plus(a,Times(b,$($s("§csc"),Plus(e,CPiHalf,Times(f,x))))),m),Power(Times(d,$($s("§csc"),Plus(e,CPiHalf,Times(f,x)))),n),Plus(ASymbol,Times(BSymbol,$($s("§csc"),Plus(e,CPiHalf,Times(f,x)))))),FreeQ(List(a,b,d,e,f,ASymbol,BSymbol,m,n),x))),
ISetDelayed(661,UnifyInertTrigFunction(Times(Power(Plus(a_DEFAULT,Times($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT)),m_DEFAULT),Power(Plus(A_DEFAULT,Times($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),B_DEFAULT)),p_DEFAULT),Power(Plus(c_DEFAULT,Times($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),d_DEFAULT)),n_DEFAULT)),x_),
    Condition(Times(Power(Plus(a,Times(b,$($s("§csc"),Plus(e,CPiHalf,Times(f,x))))),m),Power(Plus(c,Times(d,$($s("§csc"),Plus(e,CPiHalf,Times(f,x))))),n),Power(Plus(ASymbol,Times(BSymbol,$($s("§csc"),Plus(e,CPiHalf,Times(f,x))))),p)),FreeQ(List(a,b,c,d,e,f,ASymbol,BSymbol,m,n,p),x))),
ISetDelayed(662,UnifyInertTrigFunction(Times(Power(Plus(a_DEFAULT,Times($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT)),m_DEFAULT),Plus(A_DEFAULT,Times($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),B_DEFAULT),Times(Sqr($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),C_DEFAULT))),x_),
    Condition(Times(Power(Plus(a,Times(b,$($s("§csc"),Plus(e,CPiHalf,Times(f,x))))),m),Plus(ASymbol,Times(BSymbol,$($s("§csc"),Plus(e,CPiHalf,Times(f,x)))),Times(CSymbol,Sqr($($s("§csc"),Plus(e,CPiHalf,Times(f,x))))))),FreeQ(List(a,b,e,f,ASymbol,BSymbol,CSymbol,m),x))),
ISetDelayed(663,UnifyInertTrigFunction(Times(Power(Plus(a_DEFAULT,Times($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT)),m_DEFAULT),Plus(A_DEFAULT,Times(Sqr($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),C_DEFAULT))),x_),
    Condition(Times(Power(Plus(a,Times(b,$($s("§csc"),Plus(e,CPiHalf,Times(f,x))))),m),Plus(ASymbol,Times(CSymbol,Sqr($($s("§csc"),Plus(e,CPiHalf,Times(f,x))))))),FreeQ(List(a,b,e,f,ASymbol,CSymbol,m),x))),
ISetDelayed(664,UnifyInertTrigFunction(Times(Power(Plus(a_DEFAULT,Times($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT)),m_DEFAULT),Plus(A_DEFAULT,Times($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),B_DEFAULT),Times(Sqr($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),C_DEFAULT)),Power(Times($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),d_DEFAULT),n_DEFAULT)),x_),
    Condition(Times(Power(Plus(a,Times(b,$($s("§csc"),Plus(e,CPiHalf,Times(f,x))))),m),Power(Times(d,$($s("§csc"),Plus(e,CPiHalf,Times(f,x)))),n),Plus(ASymbol,Times(BSymbol,$($s("§csc"),Plus(e,CPiHalf,Times(f,x)))),Times(CSymbol,Sqr($($s("§csc"),Plus(e,CPiHalf,Times(f,x))))))),FreeQ(List(a,b,d,e,f,ASymbol,BSymbol,CSymbol,m,n),x))),
ISetDelayed(665,UnifyInertTrigFunction(Times(Power(Plus(a_DEFAULT,Times($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT)),m_DEFAULT),Plus(A_DEFAULT,Times(Sqr($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),C_DEFAULT)),Power(Times($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),d_DEFAULT),n_DEFAULT)),x_),
    Condition(Times(Power(Plus(a,Times(b,$($s("§csc"),Plus(e,CPiHalf,Times(f,x))))),m),Power(Times(d,$($s("§csc"),Plus(e,CPiHalf,Times(f,x)))),n),Plus(ASymbol,Times(CSymbol,Sqr($($s("§csc"),Plus(e,CPiHalf,Times(f,x))))))),FreeQ(List(a,b,d,e,f,ASymbol,CSymbol,m,n),x))),
ISetDelayed(666,UnifyInertTrigFunction(u_,x_),
    u),
ISetDelayed(667,UnifyInertTrigFunction(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),c_DEFAULT),n_))),p_),x_),
    Condition(Power(Plus(a,Times(b,Power(Times(CN1,c,$($s("§sec"),Plus(e,CPiHalf,Times(f,x)))),n))),p),And(FreeQ(List(a,b,c,e,f,n,p),x),Not(And(EqQ(a,C0),IntegerQ(p)))))),
ISetDelayed(668,UnifyInertTrigFunction(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),c_DEFAULT),n_))),p_DEFAULT),Power(Times($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),d_DEFAULT),m_DEFAULT)),x_),
    Condition(Times(Power(Times(d,$($s("§sin"),Plus(e,CPiHalf,Times(f,x)))),m),Power(Plus(a,Times(b,Power(Times(CN1,c,$($s("§sec"),Plus(e,CPiHalf,Times(f,x)))),n))),p)),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),Not(And(EqQ(a,C0),IntegerQ(p)))))),
ISetDelayed(669,UnifyInertTrigFunction(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),c_DEFAULT),n_))),p_DEFAULT),Power(Times($($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),d_DEFAULT),m_DEFAULT)),x_),
    Condition(Times(Power(Times(CN1,d,$($s("§cos"),Plus(e,CPiHalf,Times(f,x)))),m),Power(Plus(a,Times(b,Power(Times(CN1,c,$($s("§sec"),Plus(e,CPiHalf,Times(f,x)))),n))),p)),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),Not(And(EqQ(a,C0),IntegerQ(p)))))),
ISetDelayed(670,UnifyInertTrigFunction(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),c_DEFAULT),n_))),p_DEFAULT),Power(Times($($s("§cot"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),d_DEFAULT),m_DEFAULT)),x_),
    Condition(Times(Power(Times(CN1,d,$($s("§tan"),Plus(e,CPiHalf,Times(f,x)))),m),Power(Plus(a,Times(b,Power(Times(CN1,c,$($s("§sec"),Plus(e,CPiHalf,Times(f,x)))),n))),p)),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),Not(And(EqQ(a,C0),IntegerQ(p)))))),
ISetDelayed(671,UnifyInertTrigFunction(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),c_DEFAULT),n_))),p_DEFAULT),Power(Times($($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),d_DEFAULT),m_DEFAULT)),x_),
    Condition(Times(Power(Times(CN1,d,$($s("§cot"),Plus(e,CPiHalf,Times(f,x)))),m),Power(Plus(a,Times(b,Power(Times(CN1,c,$($s("§sec"),Plus(e,CPiHalf,Times(f,x)))),n))),p)),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),Not(And(EqQ(a,C0),IntegerQ(p))))))
  );
}
