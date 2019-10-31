package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *  
 */
public class UtilityFunctions25 { 
  public static IAST RULES = List( 
ISetDelayed(557,FixInertTrigFunction(Times(u_DEFAULT,w_,Power($($s("§cos"),v_),n_)),x_),
    Condition(Times(Power($($s("§sec"),v),Negate(n)),FixInertTrigFunction(Times(u,w),x)),And(PowerOfInertTrigSumQ(w,$s("§tan"),x),IntegerQ(n)))),
ISetDelayed(558,FixInertTrigFunction(Times(u_DEFAULT,w_,Power($($s("§csc"),v_),n_DEFAULT)),x_),
    Condition(Times(Power($($s("§sin"),v),Negate(n)),FixInertTrigFunction(Times(u,w),x)),And(PowerOfInertTrigSumQ(w,$s("§tan"),x),IntegerQ(n)))),
ISetDelayed(559,FixInertTrigFunction(Times(u_DEFAULT,w_,Power($($s("§tan"),v_),n_DEFAULT)),x_),
    Condition(Times(Power($($s("§cot"),v),Negate(n)),FixInertTrigFunction(Times(u,w),x)),And(PowerOfInertTrigSumQ(w,$s("§cot"),x),IntegerQ(n)))),
ISetDelayed(560,FixInertTrigFunction(Times(u_DEFAULT,w_,Power($($s("§sin"),v_),n_DEFAULT)),x_),
    Condition(Times(Power($($s("§csc"),v),Negate(n)),FixInertTrigFunction(Times(u,w),x)),And(PowerOfInertTrigSumQ(w,$s("§cot"),x),IntegerQ(n)))),
ISetDelayed(561,FixInertTrigFunction(Times(u_DEFAULT,w_,Power($($s("§sin"),v_),n_DEFAULT)),x_),
    Condition(Times(Power($($s("§csc"),v),Negate(n)),FixInertTrigFunction(Times(u,w),x)),And(PowerOfInertTrigSumQ(w,$s("§cot"),x),IntegerQ(n)))),
ISetDelayed(562,FixInertTrigFunction(Times(u_DEFAULT,w_,Power($($s("§sec"),v_),n_DEFAULT)),x_),
    Condition(Times(Power($($s("§cos"),v),Negate(n)),FixInertTrigFunction(Times(u,w),x)),And(PowerOfInertTrigSumQ(w,$s("§cot"),x),IntegerQ(n)))),
ISetDelayed(563,FixInertTrigFunction(Times(u_DEFAULT,w_,Power($($s("§cos"),v_),n_DEFAULT)),x_),
    Condition(Times(Power($($s("§sec"),v),Negate(n)),FixInertTrigFunction(Times(u,w),x)),And(PowerOfInertTrigSumQ(w,$s("§sec"),x),IntegerQ(n)))),
ISetDelayed(564,FixInertTrigFunction(Times(u_DEFAULT,w_,Power($($s("§cot"),v_),n_DEFAULT)),x_),
    Condition(Times(Power($($s("§tan"),v),Negate(n)),FixInertTrigFunction(Times(u,w),x)),And(PowerOfInertTrigSumQ(w,$s("§sec"),x),IntegerQ(n)))),
ISetDelayed(565,FixInertTrigFunction(Times(u_DEFAULT,w_,Power($($s("§csc"),v_),n_DEFAULT)),x_),
    Condition(Times(Power($($s("§sin"),v),Negate(n)),FixInertTrigFunction(Times(u,w),x)),And(PowerOfInertTrigSumQ(w,$s("§sec"),x),IntegerQ(n)))),
ISetDelayed(566,FixInertTrigFunction(Times(u_DEFAULT,w_,Power($($s("§sin"),v_),n_DEFAULT)),x_),
    Condition(Times(Power($($s("§csc"),v),Negate(n)),FixInertTrigFunction(Times(u,w),x)),And(PowerOfInertTrigSumQ(w,$s("§csc"),x),IntegerQ(n)))),
ISetDelayed(567,FixInertTrigFunction(Times(u_DEFAULT,w_,Power($($s("§tan"),v_),n_DEFAULT)),x_),
    Condition(Times(Power($($s("§cot"),v),Negate(n)),FixInertTrigFunction(Times(u,w),x)),And(PowerOfInertTrigSumQ(w,$s("§csc"),x),IntegerQ(n)))),
ISetDelayed(568,FixInertTrigFunction(Times(u_DEFAULT,w_,Power($($s("§sec"),v_),n_DEFAULT)),x_),
    Condition(Times(Power($($s("§cos"),v),Negate(n)),FixInertTrigFunction(Times(u,w),x)),And(PowerOfInertTrigSumQ(w,$s("§csc"),x),IntegerQ(n)))),
ISetDelayed(569,FixInertTrigFunction(Times(u_DEFAULT,Power(Plus(Times(b_DEFAULT,$($s("§cos"),v_)),Times(a_DEFAULT,$($s("§sin"),v_))),n_DEFAULT),Power($($s("§tan"),v_),m_DEFAULT)),x_),
    Condition(Times(Power($($s("§sin"),v),m),Power($($s("§cos"),v),Negate(m)),FixInertTrigFunction(Times(u,Power(Plus(Times(a,$($s("§sin"),v)),Times(b,$($s("§cos"),v))),n)),x)),And(FreeQ(List(a,b,n),x),IntegerQ(m)))),
ISetDelayed(570,FixInertTrigFunction(Times(u_DEFAULT,Power($($s("§cot"),v_),m_DEFAULT),Power(Plus(Times(b_DEFAULT,$($s("§cos"),v_)),Times(a_DEFAULT,$($s("§sin"),v_))),n_DEFAULT)),x_),
    Condition(Times(Power($($s("§cos"),v),m),Power($($s("§sin"),v),Negate(m)),FixInertTrigFunction(Times(u,Power(Plus(Times(a,$($s("§sin"),v)),Times(b,$($s("§cos"),v))),n)),x)),And(FreeQ(List(a,b,n),x),IntegerQ(m)))),
ISetDelayed(571,FixInertTrigFunction(Times(u_DEFAULT,Power($($s("§sec"),v_),m_DEFAULT),Power(Plus(Times(b_DEFAULT,$($s("§cos"),v_)),Times(a_DEFAULT,$($s("§sin"),v_))),n_DEFAULT)),x_),
    Condition(Times(Power($($s("§cos"),v),Negate(m)),FixInertTrigFunction(Times(u,Power(Plus(Times(a,$($s("§sin"),v)),Times(b,$($s("§cos"),v))),n)),x)),And(FreeQ(List(a,b,n),x),IntegerQ(m)))),
ISetDelayed(572,FixInertTrigFunction(Times(u_DEFAULT,Power($($s("§csc"),v_),m_DEFAULT),Power(Plus(Times(b_DEFAULT,$($s("§cos"),v_)),Times(a_DEFAULT,$($s("§sin"),v_))),n_DEFAULT)),x_),
    Condition(Times(Power($($s("§sin"),v),Negate(m)),FixInertTrigFunction(Times(u,Power(Plus(Times(a,$($s("§sin"),v)),Times(b,$($s("§cos"),v))),n)),x)),And(FreeQ(List(a,b,n),x),IntegerQ(m)))),
ISetDelayed(573,FixInertTrigFunction(Times(Power($(f_,v_),m_DEFAULT),Plus(A_DEFAULT,Times(B_DEFAULT,$(g_,v_)),Times(C_DEFAULT,Sqr($(g_,v_))))),x_),
    Condition(Times(Power($(g,v),Negate(m)),Plus(ASymbol,Times(BSymbol,$(g,v)),Times(C,Sqr($(g,v))))),And(FreeQ(List(ASymbol,BSymbol,C),x),IntegerQ(m),Or(InertReciprocalQ(f,g),InertReciprocalQ(g,f))))),
ISetDelayed(574,FixInertTrigFunction(Times(Power($(f_,v_),m_DEFAULT),Plus(A_DEFAULT,Times(C_DEFAULT,Sqr($(g_,v_))))),x_),
    Condition(Times(Power($(g,v),Negate(m)),Plus(ASymbol,Times(C,Sqr($(g,v))))),And(FreeQ(List(ASymbol,C),x),IntegerQ(m),Or(InertReciprocalQ(f,g),InertReciprocalQ(g,f))))),
ISetDelayed(575,FixInertTrigFunction(Times(Power($(f_,v_),m_DEFAULT),Plus(A_DEFAULT,Times(B_DEFAULT,$(g_,v_)),Times(C_DEFAULT,Sqr($(g_,v_)))),Power(Plus(a_DEFAULT,Times(b_DEFAULT,$(g_,v_))),n_DEFAULT)),x_),
    Condition(Times(Power($(g,v),Negate(m)),Plus(ASymbol,Times(BSymbol,$(g,v)),Times(C,Sqr($(g,v)))),Power(Plus(a,Times(b,$(g,v))),n)),And(FreeQ(List(a,b,ASymbol,BSymbol,C,n),x),IntegerQ(m),Or(InertReciprocalQ(f,g),InertReciprocalQ(g,f))))),
ISetDelayed(576,FixInertTrigFunction(Times(Power($(f_,v_),m_DEFAULT),Plus(A_DEFAULT,Times(C_DEFAULT,Sqr($(g_,v_)))),Power(Plus(a_DEFAULT,Times(b_DEFAULT,$(g_,v_))),n_DEFAULT)),x_),
    Condition(Times(Power($(g,v),Negate(m)),Plus(ASymbol,Times(C,Sqr($(g,v)))),Power(Plus(a,Times(b,$(g,v))),n)),And(FreeQ(List(a,b,ASymbol,C,n),x),IntegerQ(m),Or(InertReciprocalQ(f,g),InertReciprocalQ(g,f))))),
ISetDelayed(577,FixInertTrigFunction(u_,x_),
    u),
ISetDelayed(578,PowerOfInertTrigSumQ(u_,$p("func"),x_),
    Or(MatchQ(u,Condition(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times(c_DEFAULT,$($s("func"),w_)),n_DEFAULT))),p_DEFAULT),And(FreeQ(List(a,b,c,n,p),x),Not(And(EqQ(a,C0),Or(IntegerQ(p),EqQ(n,C1))))))),MatchQ(u,Condition(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times(d_DEFAULT,$($s("func"),w_)),p_DEFAULT)),Times(c_DEFAULT,Power(Times(d_DEFAULT,$($s("func"),w_)),q_DEFAULT))),n_DEFAULT),FreeQ(List(a,b,c,d,n,p,q),x))))),
ISetDelayed(579,ReduceInertTrig($p("func"),Plus(Times(m_DEFAULT,Plus(Times(Pi,n_DEFAULT),u_DEFAULT)),v_DEFAULT)),
    Condition(ReduceInertTrig($s("func"),Times(m,n),Plus(Times(m,u),v)),RationalQ(m,n))),
ISetDelayed(580,ReduceInertTrig($p("func"),Plus(v_DEFAULT,Times(m_DEFAULT,Complex(C0,$p("mz")),Plus(u_DEFAULT,Times(Pi,n_DEFAULT,Complex(C0,$p("nz"))))))),
    Condition(ReduceInertTrig($s("func"),Times(CN1,m,$s("mz"),n,$s("nz")),Plus(Times(m,$s("mz"),CI,u),v)),RationalQ(m,$s("mz"),n,$s("nz")))),
ISetDelayed(581,ReduceInertTrig($p("func"),u_),
    $($s("func"),u))
  );
}
