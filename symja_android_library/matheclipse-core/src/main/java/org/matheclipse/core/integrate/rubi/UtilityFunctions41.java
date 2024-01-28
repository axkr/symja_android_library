package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class UtilityFunctions41 { 
  public static IAST RULES = List( 
ISetDelayed(574,FixInertTrigFunction(Times(Power($($s("§csc"),v_),n_DEFAULT),u_DEFAULT,w_),x_),
    Condition(Times(Power($($s("§sin"),v),Negate(n)),FixInertTrigFunction(Times(u,w),x)),And(PowerOfInertTrigSumQ(w,$s("§tan"),x),IntegerQ(n)))),
ISetDelayed(575,FixInertTrigFunction(Times(Power($($s("§tan"),v_),n_DEFAULT),u_DEFAULT,w_),x_),
    Condition(Times(Power($($s("§cot"),v),Negate(n)),FixInertTrigFunction(Times(u,w),x)),And(PowerOfInertTrigSumQ(w,$s("§cot"),x),IntegerQ(n)))),
ISetDelayed(576,FixInertTrigFunction(Times(Power($($s("§sin"),v_),n_DEFAULT),u_DEFAULT,w_),x_),
    Condition(Times(Power($($s("§csc"),v),Negate(n)),FixInertTrigFunction(Times(u,w),x)),And(PowerOfInertTrigSumQ(w,$s("§cot"),x),IntegerQ(n)))),
ISetDelayed(577,FixInertTrigFunction(Times(Power($($s("§sin"),v_),n_DEFAULT),u_DEFAULT,w_),x_),
    Condition(Times(Power($($s("§csc"),v),Negate(n)),FixInertTrigFunction(Times(u,w),x)),And(PowerOfInertTrigSumQ(w,$s("§cot"),x),IntegerQ(n)))),
ISetDelayed(578,FixInertTrigFunction(Times(Power($($s("§sec"),v_),n_DEFAULT),u_DEFAULT,w_),x_),
    Condition(Times(Power($($s("§cos"),v),Negate(n)),FixInertTrigFunction(Times(u,w),x)),And(PowerOfInertTrigSumQ(w,$s("§cot"),x),IntegerQ(n)))),
ISetDelayed(579,FixInertTrigFunction(Times(Power($($s("§cos"),v_),n_DEFAULT),u_DEFAULT,w_),x_),
    Condition(Times(Power($($s("§sec"),v),Negate(n)),FixInertTrigFunction(Times(u,w),x)),And(PowerOfInertTrigSumQ(w,$s("§sec"),x),IntegerQ(n)))),
ISetDelayed(580,FixInertTrigFunction(Times(Power($($s("§cot"),v_),n_DEFAULT),u_DEFAULT,w_),x_),
    Condition(Times(Power($($s("§tan"),v),Negate(n)),FixInertTrigFunction(Times(u,w),x)),And(PowerOfInertTrigSumQ(w,$s("§sec"),x),IntegerQ(n)))),
ISetDelayed(581,FixInertTrigFunction(Times(Power($($s("§csc"),v_),n_DEFAULT),u_DEFAULT,w_),x_),
    Condition(Times(Power($($s("§sin"),v),Negate(n)),FixInertTrigFunction(Times(u,w),x)),And(PowerOfInertTrigSumQ(w,$s("§sec"),x),IntegerQ(n)))),
ISetDelayed(582,FixInertTrigFunction(Times(Power($($s("§sin"),v_),n_DEFAULT),u_DEFAULT,w_),x_),
    Condition(Times(Power($($s("§csc"),v),Negate(n)),FixInertTrigFunction(Times(u,w),x)),And(PowerOfInertTrigSumQ(w,$s("§csc"),x),IntegerQ(n)))),
ISetDelayed(583,FixInertTrigFunction(Times(Power($($s("§tan"),v_),n_DEFAULT),u_DEFAULT,w_),x_),
    Condition(Times(Power($($s("§cot"),v),Negate(n)),FixInertTrigFunction(Times(u,w),x)),And(PowerOfInertTrigSumQ(w,$s("§csc"),x),IntegerQ(n)))),
ISetDelayed(584,FixInertTrigFunction(Times(Power($($s("§sec"),v_),n_DEFAULT),u_DEFAULT,w_),x_),
    Condition(Times(Power($($s("§cos"),v),Negate(n)),FixInertTrigFunction(Times(u,w),x)),And(PowerOfInertTrigSumQ(w,$s("§csc"),x),IntegerQ(n)))),
ISetDelayed(585,FixInertTrigFunction(Times(Power($($s("§tan"),v_),m_DEFAULT),Power(Plus(Times($($s("§sin"),v_),a_DEFAULT),Times($($s("§cos"),v_),b_DEFAULT)),n_DEFAULT),u_DEFAULT),x_),
    Condition(Times(Power($($s("§sin"),v),m),Power($($s("§cos"),v),Negate(m)),FixInertTrigFunction(Times(u,Power(Plus(Times(a,$($s("§sin"),v)),Times(b,$($s("§cos"),v))),n)),x)),And(FreeQ(list(a,b,n),x),IntegerQ(m)))),
ISetDelayed(586,FixInertTrigFunction(Times(Power($($s("§cot"),v_),m_DEFAULT),Power(Plus(Times($($s("§sin"),v_),a_DEFAULT),Times($($s("§cos"),v_),b_DEFAULT)),n_DEFAULT),u_DEFAULT),x_),
    Condition(Times(Power($($s("§cos"),v),m),Power($($s("§sin"),v),Negate(m)),FixInertTrigFunction(Times(u,Power(Plus(Times(a,$($s("§sin"),v)),Times(b,$($s("§cos"),v))),n)),x)),And(FreeQ(list(a,b,n),x),IntegerQ(m)))),
ISetDelayed(587,FixInertTrigFunction(Times(Power($($s("§sec"),v_),m_DEFAULT),Power(Plus(Times($($s("§sin"),v_),a_DEFAULT),Times($($s("§cos"),v_),b_DEFAULT)),n_DEFAULT),u_DEFAULT),x_),
    Condition(Times(Power($($s("§cos"),v),Negate(m)),FixInertTrigFunction(Times(u,Power(Plus(Times(a,$($s("§sin"),v)),Times(b,$($s("§cos"),v))),n)),x)),And(FreeQ(list(a,b,n),x),IntegerQ(m)))),
ISetDelayed(588,FixInertTrigFunction(Times(Power($($s("§csc"),v_),m_DEFAULT),Power(Plus(Times($($s("§sin"),v_),a_DEFAULT),Times($($s("§cos"),v_),b_DEFAULT)),n_DEFAULT),u_DEFAULT),x_),
    Condition(Times(Power($($s("§sin"),v),Negate(m)),FixInertTrigFunction(Times(u,Power(Plus(Times(a,$($s("§sin"),v)),Times(b,$($s("§cos"),v))),n)),x)),And(FreeQ(list(a,b,n),x),IntegerQ(m)))),
ISetDelayed(589,FixInertTrigFunction(Times(Power($(f_,v_),m_DEFAULT),Plus(A_DEFAULT,Times($(g_,v_),B_DEFAULT),Times(Sqr($(g_,v_)),C_DEFAULT))),x_),
    Condition(Times(Power($(g,v),Negate(m)),Plus(ASymbol,Times(BSymbol,$(g,v)),Times(CSymbol,Sqr($(g,v))))),And(FreeQ(list(ASymbol,BSymbol,CSymbol),x),IntegerQ(m),Or(InertReciprocalQ(f,g),InertReciprocalQ(g,f))))),
ISetDelayed(590,FixInertTrigFunction(Times(Power($(f_,v_),m_DEFAULT),Plus(A_DEFAULT,Times(Sqr($(g_,v_)),C_DEFAULT))),x_),
    Condition(Times(Power($(g,v),Negate(m)),Plus(ASymbol,Times(CSymbol,Sqr($(g,v))))),And(FreeQ(list(ASymbol,CSymbol),x),IntegerQ(m),Or(InertReciprocalQ(f,g),InertReciprocalQ(g,f))))),
ISetDelayed(591,FixInertTrigFunction(Times(Power($(f_,v_),m_DEFAULT),Power(Plus(a_DEFAULT,Times($(g_,v_),b_DEFAULT)),n_DEFAULT),Plus(A_DEFAULT,Times($(g_,v_),B_DEFAULT),Times(Sqr($(g_,v_)),C_DEFAULT))),x_),
    Condition(Times(Power($(g,v),Negate(m)),Plus(ASymbol,Times(BSymbol,$(g,v)),Times(CSymbol,Sqr($(g,v)))),Power(Plus(a,Times(b,$(g,v))),n)),And(FreeQ(List(a,b,ASymbol,BSymbol,CSymbol,n),x),IntegerQ(m),Or(InertReciprocalQ(f,g),InertReciprocalQ(g,f))))),
ISetDelayed(592,FixInertTrigFunction(Times(Power($(f_,v_),m_DEFAULT),Power(Plus(a_DEFAULT,Times($(g_,v_),b_DEFAULT)),n_DEFAULT),Plus(A_DEFAULT,Times(Sqr($(g_,v_)),C_DEFAULT))),x_),
    Condition(Times(Power($(g,v),Negate(m)),Plus(ASymbol,Times(CSymbol,Sqr($(g,v)))),Power(Plus(a,Times(b,$(g,v))),n)),And(FreeQ(List(a,b,ASymbol,CSymbol,n),x),IntegerQ(m),Or(InertReciprocalQ(f,g),InertReciprocalQ(g,f))))),
ISetDelayed(593,FixInertTrigFunction(u_,x_),
    u)
  );
}
