package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class UtilityFunctions39 { 
  public static IAST RULES = List( 
ISetDelayed(534,FixInertTrigFunction(Times(u_DEFAULT,Power(Times(a_,Plus(b_,v_)),n_)),x_),
    Condition(FixInertTrigFunction(Times(u,Power(Plus(Times(a,b),Times(a,v)),n)),x),And(FreeQ(list(a,b,n),x),Not(FreeQ(v,x))))),
ISetDelayed(535,FixInertTrigFunction(Times(Power($($s("§csc"),v_),m_DEFAULT),Power(Times($($s("§sin"),w_),c_DEFAULT),n_DEFAULT)),x_),
    Condition(Times(Power($($s("§sin"),v),Negate(m)),Power(Times(c,$($s("§sin"),w)),n)),And(FreeQ(list(c,n),x),IntegerQ(m)))),
ISetDelayed(536,FixInertTrigFunction(Times(Power($($s("§sec"),v_),m_DEFAULT),Power(Times($($s("§cos"),w_),c_DEFAULT),n_DEFAULT)),x_),
    Condition(Times(Power($($s("§cos"),v),Negate(m)),Power(Times(c,$($s("§cos"),w)),n)),And(FreeQ(list(c,n),x),IntegerQ(m)))),
ISetDelayed(537,FixInertTrigFunction(Times(Power($($s("§cot"),v_),m_DEFAULT),Power(Times($($s("§tan"),w_),c_DEFAULT),n_DEFAULT)),x_),
    Condition(Times(Power($($s("§tan"),v),Negate(m)),Power(Times(c,$($s("§tan"),w)),n)),And(FreeQ(list(c,n),x),IntegerQ(m)))),
ISetDelayed(538,FixInertTrigFunction(Times(Power($($s("§tan"),v_),m_DEFAULT),Power(Times($($s("§cot"),w_),c_DEFAULT),n_DEFAULT)),x_),
    Condition(Times(Power($($s("§cot"),v),Negate(m)),Power(Times(c,$($s("§cot"),w)),n)),And(FreeQ(list(c,n),x),IntegerQ(m)))),
ISetDelayed(539,FixInertTrigFunction(Times(Power($($s("§cos"),v_),m_DEFAULT),Power(Times($($s("§sec"),w_),c_DEFAULT),n_DEFAULT)),x_),
    Condition(Times(Power($($s("§sec"),v),Negate(m)),Power(Times(c,$($s("§sec"),w)),n)),And(FreeQ(list(c,n),x),IntegerQ(m)))),
ISetDelayed(540,FixInertTrigFunction(Times(Power($($s("§sin"),v_),m_DEFAULT),Power(Times($($s("§csc"),w_),c_DEFAULT),n_DEFAULT)),x_),
    Condition(Times(Power($($s("§csc"),v),Negate(m)),Power(Times(c,$($s("§csc"),w)),n)),And(FreeQ(list(c,n),x),IntegerQ(m)))),
ISetDelayed(541,FixInertTrigFunction(Times(Power($($s("§sec"),v_),m_DEFAULT),Power(Times($($s("§sin"),w_),c_DEFAULT),n_DEFAULT)),x_),
    Condition(Times(Power($($s("§cos"),v),Negate(m)),Power(Times(c,$($s("§sin"),w)),n)),And(FreeQ(list(c,n),x),IntegerQ(m)))),
ISetDelayed(542,FixInertTrigFunction(Times(Power($($s("§csc"),v_),m_DEFAULT),Power(Times($($s("§cos"),w_),c_DEFAULT),n_DEFAULT)),x_),
    Condition(Times(Power($($s("§sin"),v),Negate(m)),Power(Times(c,$($s("§cos"),w)),n)),And(FreeQ(list(c,n),x),IntegerQ(m)))),
ISetDelayed(543,FixInertTrigFunction(Times(Power($($s("§cos"),v_),m_DEFAULT),Power(Times($($s("§tan"),w_),c_DEFAULT),n_DEFAULT)),x_),
    Condition(Times(Power($($s("§sec"),v),Negate(m)),Power(Times(c,$($s("§tan"),w)),n)),And(FreeQ(list(c,n),x),IntegerQ(m)))),
ISetDelayed(544,FixInertTrigFunction(Times(Power($($s("§sin"),v_),m_DEFAULT),Power(Times($($s("§cot"),w_),c_DEFAULT),n_DEFAULT)),x_),
    Condition(Times(Power($($s("§csc"),v),Negate(m)),Power(Times(c,$($s("§cot"),w)),n)),And(FreeQ(list(c,n),x),IntegerQ(m)))),
ISetDelayed(545,FixInertTrigFunction(Times(Power($($s("§sin"),v_),m_DEFAULT),Power(Times($($s("§sec"),w_),c_DEFAULT),n_DEFAULT)),x_),
    Condition(Times(Power($($s("§csc"),v),Negate(m)),Power(Times(c,$($s("§sec"),w)),n)),And(FreeQ(list(c,n),x),IntegerQ(m)))),
ISetDelayed(546,FixInertTrigFunction(Times(Power($($s("§cos"),v_),m_DEFAULT),Power(Times($($s("§csc"),w_),c_DEFAULT),n_DEFAULT)),x_),
    Condition(Times(Power($($s("§sec"),v),Negate(m)),Power(Times(c,$($s("§csc"),w)),n)),And(FreeQ(list(c,n),x),IntegerQ(m)))),
ISetDelayed(547,FixInertTrigFunction(Times(Power($($s("§cot"),v_),m_DEFAULT),Power(Times($($s("§sin"),w_),c_DEFAULT),n_DEFAULT)),x_),
    Condition(Times(Power($($s("§tan"),v),Negate(m)),Power(Times(c,$($s("§sin"),w)),n)),And(FreeQ(list(c,n),x),IntegerQ(m)))),
ISetDelayed(548,FixInertTrigFunction(Times(Power($($s("§tan"),v_),m_DEFAULT),Power(Times($($s("§cos"),w_),c_DEFAULT),n_DEFAULT)),x_),
    Condition(Times(Power($($s("§cot"),v),Negate(m)),Power(Times(c,$($s("§cos"),w)),n)),And(FreeQ(list(c,n),x),IntegerQ(m)))),
ISetDelayed(549,FixInertTrigFunction(Times(Power($($s("§csc"),v_),m_DEFAULT),Power(Times($($s("§tan"),w_),c_DEFAULT),n_DEFAULT)),x_),
    Condition(Times(Power($($s("§sin"),v),Negate(m)),Power(Times(c,$($s("§tan"),w)),n)),And(FreeQ(list(c,n),x),IntegerQ(m)))),
ISetDelayed(550,FixInertTrigFunction(Times(Power($($s("§sec"),v_),m_DEFAULT),Power(Times($($s("§cot"),w_),c_DEFAULT),n_DEFAULT)),x_),
    Condition(Times(Power($($s("§cos"),v),Negate(m)),Power(Times(c,$($s("§cot"),w)),n)),And(FreeQ(list(c,n),x),IntegerQ(m)))),
ISetDelayed(551,FixInertTrigFunction(Times(Power($($s("§cot"),v_),m_DEFAULT),Power(Times($($s("§sec"),w_),c_DEFAULT),n_DEFAULT)),x_),
    Condition(Times(Power($($s("§tan"),v),Negate(m)),Power(Times(c,$($s("§sec"),w)),n)),And(FreeQ(list(c,n),x),IntegerQ(m)))),
ISetDelayed(552,FixInertTrigFunction(Times(Power($($s("§tan"),v_),m_DEFAULT),Power(Times($($s("§csc"),w_),c_DEFAULT),n_DEFAULT)),x_),
    Condition(Times(Power($($s("§cot"),v),Negate(m)),Power(Times(c,$($s("§csc"),w)),n)),And(FreeQ(list(c,n),x),IntegerQ(m)))),
ISetDelayed(553,FixInertTrigFunction(Times(Power($($s("§sec"),v_),m_DEFAULT),Power($($s("§sec"),w_),n_DEFAULT)),x_),
    Condition(Times(Power($($s("§cos"),v),Negate(m)),Power($($s("§cos"),w),Negate(n))),IntegersQ(m,n)))
  );
}
