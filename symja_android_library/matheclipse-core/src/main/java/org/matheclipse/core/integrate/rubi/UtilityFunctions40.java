package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class UtilityFunctions40 { 
  public static IAST RULES = List( 
ISetDelayed(554,FixInertTrigFunction(Times(Power($($s("§csc"),v_),m_DEFAULT),Power($($s("§csc"),w_),n_DEFAULT)),x_),
    Condition(Times(Power($($s("§sin"),v),Negate(m)),Power($($s("§sin"),w),Negate(n))),IntegersQ(m,n))),
ISetDelayed(555,FixInertTrigFunction(Times(Power($($s("§tan"),v_),m_DEFAULT),Power(Plus(a_,Times($($s("§sin"),w_),b_DEFAULT)),n_DEFAULT),u_),x_),
    Condition(Times(Power($($s("§sin"),v),m),Power(Power($($s("§cos"),v),m),CN1),FixInertTrigFunction(Times(u,Power(Plus(a,Times(b,$($s("§sin"),w))),n)),x)),And(FreeQ(list(a,b,n),x),IntegerQ(m)))),
ISetDelayed(556,FixInertTrigFunction(Times(Power($($s("§cot"),v_),m_DEFAULT),Power(Plus(a_,Times($($s("§sin"),w_),b_DEFAULT)),n_DEFAULT),u_),x_),
    Condition(Times(Power($($s("§cos"),v),m),Power(Power($($s("§sin"),v),m),CN1),FixInertTrigFunction(Times(u,Power(Plus(a,Times(b,$($s("§sin"),w))),n)),x)),And(FreeQ(list(a,b,n),x),IntegerQ(m)))),
ISetDelayed(557,FixInertTrigFunction(Times(Power($($s("§tan"),v_),m_DEFAULT),Power(Plus(a_,Times($($s("§cos"),w_),b_DEFAULT)),n_DEFAULT),u_),x_),
    Condition(Times(Power($($s("§sin"),v),m),Power(Power($($s("§cos"),v),m),CN1),FixInertTrigFunction(Times(u,Power(Plus(a,Times(b,$($s("§cos"),w))),n)),x)),And(FreeQ(list(a,b,n),x),IntegerQ(m)))),
ISetDelayed(558,FixInertTrigFunction(Times(Power($($s("§cot"),v_),m_DEFAULT),Power(Plus(a_,Times($($s("§cos"),w_),b_DEFAULT)),n_DEFAULT),u_),x_),
    Condition(Times(Power($($s("§cos"),v),m),Power(Power($($s("§sin"),v),m),CN1),FixInertTrigFunction(Times(u,Power(Plus(a,Times(b,$($s("§cos"),w))),n)),x)),And(FreeQ(list(a,b,n),x),IntegerQ(m)))),
ISetDelayed(559,FixInertTrigFunction(Times(Power($($s("§cot"),v_),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times($($s("§sin"),w_),c_DEFAULT),p_DEFAULT))),n_DEFAULT)),x_),
    Condition(Times(Power($($s("§tan"),v),Negate(m)),Power(Plus(a,Times(b,Power(Times(c,$($s("§sin"),w)),p))),n)),And(FreeQ(List(a,b,c,n,p),x),IntegerQ(m)))),
ISetDelayed(560,FixInertTrigFunction(Times(Power($($s("§tan"),v_),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times($($s("§cos"),w_),c_DEFAULT),p_DEFAULT))),n_DEFAULT)),x_),
    Condition(Times(Power($($s("§cot"),v),Negate(m)),Power(Plus(a,Times(b,Power(Times(c,$($s("§cos"),w)),p))),n)),And(FreeQ(List(a,b,c,n,p),x),IntegerQ(m)))),
ISetDelayed(561,FixInertTrigFunction(Times(Power(Times(Power($($s("§sin"),v_),n_DEFAULT),c_DEFAULT),p_DEFAULT),u_DEFAULT,w_),x_),
    Condition(Times(Power(Times(c,Power($($s("§sin"),v),n)),p),FixInertTrigFunction(Times(u,w),x)),And(FreeQ(list(c,p),x),PowerOfInertTrigSumQ(w,$s("§sin"),x)))),
ISetDelayed(562,FixInertTrigFunction(Times(Power(Times(Power($($s("§cos"),v_),n_DEFAULT),c_DEFAULT),p_DEFAULT),u_DEFAULT,w_),x_),
    Condition(Times(Power(Times(c,Power($($s("§cos"),v),n)),p),FixInertTrigFunction(Times(u,w),x)),And(FreeQ(list(c,p),x),PowerOfInertTrigSumQ(w,$s("§cos"),x)))),
ISetDelayed(563,FixInertTrigFunction(Times(Power(Times(Power($($s("§tan"),v_),n_DEFAULT),c_DEFAULT),p_DEFAULT),u_DEFAULT,w_),x_),
    Condition(Times(Power(Times(c,Power($($s("§tan"),v),n)),p),FixInertTrigFunction(Times(u,w),x)),And(FreeQ(list(c,p),x),PowerOfInertTrigSumQ(w,$s("§tan"),x)))),
ISetDelayed(564,FixInertTrigFunction(Times(Power(Times(Power($($s("§cot"),v_),n_DEFAULT),c_DEFAULT),p_DEFAULT),u_DEFAULT,w_),x_),
    Condition(Times(Power(Times(c,Power($($s("§cot"),v),n)),p),FixInertTrigFunction(Times(u,w),x)),And(FreeQ(list(c,p),x),PowerOfInertTrigSumQ(w,$s("§cot"),x)))),
ISetDelayed(565,FixInertTrigFunction(Times(Power(Times(Power($($s("§sec"),v_),n_DEFAULT),c_DEFAULT),p_DEFAULT),u_DEFAULT,w_),x_),
    Condition(Times(Power(Times(c,Power($($s("§sec"),v),n)),p),FixInertTrigFunction(Times(u,w),x)),And(FreeQ(list(c,p),x),PowerOfInertTrigSumQ(w,$s("§sec"),x)))),
ISetDelayed(566,FixInertTrigFunction(Times(Power(Times(Power($($s("§csc"),v_),n_DEFAULT),c_DEFAULT),p_DEFAULT),u_DEFAULT,w_),x_),
    Condition(Times(Power(Times(c,Power($($s("§csc"),v),n)),p),FixInertTrigFunction(Times(u,w),x)),And(FreeQ(list(c,p),x),PowerOfInertTrigSumQ(w,$s("§csc"),x)))),
ISetDelayed(567,FixInertTrigFunction(Times(Power($($s("§sec"),v_),n_DEFAULT),u_DEFAULT,w_),x_),
    Condition(Times(Power($($s("§cos"),v),Negate(n)),FixInertTrigFunction(Times(u,w),x)),And(PowerOfInertTrigSumQ(w,$s("§cos"),x),IntegerQ(n)))),
ISetDelayed(568,FixInertTrigFunction(Times(Power($($s("§csc"),v_),n_DEFAULT),u_DEFAULT,w_),x_),
    Condition(Times(Power($($s("§sin"),v),Negate(n)),FixInertTrigFunction(Times(u,w),x)),And(PowerOfInertTrigSumQ(w,$s("§sin"),x),IntegerQ(n)))),
ISetDelayed(569,FixInertTrigFunction(Times(Power($($s("§sec"),v_),n_DEFAULT),u_DEFAULT,w_),x_),
    Condition(Times(Power($($s("§cos"),v),Negate(n)),FixInertTrigFunction(Times(u,w),x)),And(PowerOfInertTrigSumQ(w,$s("§sin"),x),IntegerQ(n)))),
ISetDelayed(570,FixInertTrigFunction(Times(Power($($s("§csc"),v_),n_DEFAULT),u_DEFAULT,w_),x_),
    Condition(Times(Power($($s("§sin"),v),Negate(n)),FixInertTrigFunction(Times(u,w),x)),And(PowerOfInertTrigSumQ(w,$s("§cos"),x),IntegerQ(n)))),
ISetDelayed(571,FixInertTrigFunction(Times(Power($($s("§cot"),v_),n_DEFAULT),u_DEFAULT,w_),x_),
    Condition(Times(Power($($s("§tan"),v),Negate(n)),FixInertTrigFunction(Times(u,w),x)),And(PowerOfInertTrigSumQ(w,$s("§tan"),x),IntegerQ(n)))),
ISetDelayed(572,FixInertTrigFunction(Times(Power($($s("§cos"),v_),n_DEFAULT),u_DEFAULT,w_),x_),
    Condition(Times(Power($($s("§sec"),v),Negate(n)),FixInertTrigFunction(Times(u,w),x)),And(PowerOfInertTrigSumQ(w,$s("§tan"),x),IntegerQ(n)))),
ISetDelayed(573,FixInertTrigFunction(Times(Power($($s("§cos"),v_),n_),u_DEFAULT,w_),x_),
    Condition(Times(Power($($s("§sec"),v),Negate(n)),FixInertTrigFunction(Times(u,w),x)),And(PowerOfInertTrigSumQ(w,$s("§tan"),x),IntegerQ(n))))
  );
}
