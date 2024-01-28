package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class UtilityFunctions21 { 
  public static IAST RULES = List( 
ISetDelayed(253,TrigSimplifyAux(Plus(u_,Times(Sqr($($s("§cos"),z_)),v_DEFAULT),w_DEFAULT)),
    Condition(Plus(Times(u,Sqr(Sin(z))),w),SameQ(u,Negate(v)))),
ISetDelayed(254,TrigSimplifyAux(Plus(u_,Times(Sqr($($s("§tan"),z_)),v_DEFAULT),w_DEFAULT)),
    Condition(Plus(Times(u,Sqr(Sec(z))),w),SameQ(u,v))),
ISetDelayed(255,TrigSimplifyAux(Plus(u_,Times(Sqr($($s("§cot"),z_)),v_DEFAULT),w_DEFAULT)),
    Condition(Plus(Times(u,Sqr(Csc(z))),w),SameQ(u,v))),
ISetDelayed(256,TrigSimplifyAux(Plus(u_,Times(Sqr($($s("§sec"),z_)),v_DEFAULT),w_DEFAULT)),
    Condition(Plus(Times(v,Sqr(Tan(z))),w),SameQ(u,Negate(v)))),
ISetDelayed(257,TrigSimplifyAux(Plus(u_,Times(Sqr($($s("§csc"),z_)),v_DEFAULT),w_DEFAULT)),
    Condition(Plus(Times(v,Sqr(Cot(z))),w),SameQ(u,Negate(v)))),
ISetDelayed(258,TrigSimplifyAux(Times(Sqr($($s("§sin"),v_)),Power(Plus(a_,Times($($s("§cos"),v_),b_DEFAULT)),CN1),u_DEFAULT)),
    Condition(Times(u,Subtract(Power(a,CN1),Times(Cos(v),Power(b,CN1)))),EqQ(Subtract(Sqr(a),Sqr(b)),C0))),
ISetDelayed(259,TrigSimplifyAux(Times(Sqr($($s("§cos"),v_)),Power(Plus(a_,Times($($s("§sin"),v_),b_DEFAULT)),CN1),u_DEFAULT)),
    Condition(Times(u,Subtract(Power(a,CN1),Times(Sin(v),Power(b,CN1)))),EqQ(Subtract(Sqr(a),Sqr(b)),C0))),
ISetDelayed(260,TrigSimplifyAux(Times(Power($($s("§tan"),v_),n_DEFAULT),Power(Plus(a_,Times(Power($($s("§tan"),v_),n_DEFAULT),b_DEFAULT)),CN1),u_DEFAULT)),
    Condition(Times(u,Power(Plus(b,Times(a,Power(Cot(v),n))),CN1)),And(IGtQ(n,C0),NonsumQ(a)))),
ISetDelayed(261,TrigSimplifyAux(Times(Power($($s("§cot"),v_),n_DEFAULT),Power(Plus(a_,Times(Power($($s("§cot"),v_),n_DEFAULT),b_DEFAULT)),CN1),u_DEFAULT)),
    Condition(Times(u,Power(Plus(b,Times(a,Power(Tan(v),n))),CN1)),And(IGtQ(n,C0),NonsumQ(a)))),
ISetDelayed(262,TrigSimplifyAux(Times(Power($($s("§sec"),v_),n_DEFAULT),Power(Plus(a_,Times(Power($($s("§sec"),v_),n_DEFAULT),b_DEFAULT)),CN1),u_DEFAULT)),
    Condition(Times(u,Power(Plus(b,Times(a,Power(Cos(v),n))),CN1)),And(IGtQ(n,C0),NonsumQ(a)))),
ISetDelayed(263,TrigSimplifyAux(Times(Power($($s("§csc"),v_),n_DEFAULT),Power(Plus(a_,Times(Power($($s("§csc"),v_),n_DEFAULT),b_DEFAULT)),CN1),u_DEFAULT)),
    Condition(Times(u,Power(Plus(b,Times(a,Power(Sin(v),n))),CN1)),And(IGtQ(n,C0),NonsumQ(a)))),
ISetDelayed(264,TrigSimplifyAux(Times(Power($($s("§tan"),v_),n_DEFAULT),Power(Plus(a_,Times(Power($($s("§sec"),v_),n_DEFAULT),b_DEFAULT)),CN1),u_DEFAULT)),
    Condition(Times(u,Power(Sin(v),n),Power(Plus(b,Times(a,Power(Cos(v),n))),CN1)),And(IGtQ(n,C0),NonsumQ(a)))),
ISetDelayed(265,TrigSimplifyAux(Times(Power($($s("§cot"),v_),n_DEFAULT),Power(Plus(a_,Times(Power($($s("§csc"),v_),n_DEFAULT),b_DEFAULT)),CN1),u_DEFAULT)),
    Condition(Times(u,Power(Cos(v),n),Power(Plus(b,Times(a,Power(Sin(v),n))),CN1)),And(IGtQ(n,C0),NonsumQ(a)))),
ISetDelayed(266,TrigSimplifyAux(Times(Power(Plus(Times(Power($($s("§sec"),v_),n_DEFAULT),a_DEFAULT),Times(Power($($s("§tan"),v_),n_DEFAULT),b_DEFAULT)),p_DEFAULT),u_DEFAULT)),
    Condition(Times(u,Power(Sec(v),Times(n,p)),Power(Plus(a,Times(b,Power(Sin(v),n))),p)),IntegersQ(n,p))),
ISetDelayed(267,TrigSimplifyAux(Times(Power(Plus(Times(Power($($s("§csc"),v_),n_DEFAULT),a_DEFAULT),Times(Power($($s("§cot"),v_),n_DEFAULT),b_DEFAULT)),p_DEFAULT),u_DEFAULT)),
    Condition(Times(u,Power(Csc(v),Times(n,p)),Power(Plus(a,Times(b,Power(Cos(v),n))),p)),IntegersQ(n,p))),
ISetDelayed(268,TrigSimplifyAux(Times(Power(Plus(Times(Power($($s("§tan"),v_),n_DEFAULT),a_DEFAULT),Times(Power($($s("§sin"),v_),n_DEFAULT),b_DEFAULT)),p_DEFAULT),u_DEFAULT)),
    Condition(Times(u,Power(Tan(v),Times(n,p)),Power(Plus(a,Times(b,Power(Cos(v),n))),p)),IntegersQ(n,p))),
ISetDelayed(269,TrigSimplifyAux(Times(Power(Plus(Times(Power($($s("§cot"),v_),n_DEFAULT),a_DEFAULT),Times(Power($($s("§cos"),v_),n_DEFAULT),b_DEFAULT)),p_DEFAULT),u_DEFAULT)),
    Condition(Times(u,Power(Cot(v),Times(n,p)),Power(Plus(a,Times(b,Power(Sin(v),n))),p)),IntegersQ(n,p))),
ISetDelayed(270,TrigSimplifyAux(Times(Power($($s("§cos"),v_),m_DEFAULT),Power(Plus(a_DEFAULT,Times(Power($($s("§tan"),v_),n_DEFAULT),b_DEFAULT),Times(Power($($s("§sec"),v_),n_DEFAULT),c_DEFAULT)),p_DEFAULT),u_DEFAULT)),
    Condition(Times(u,Power(Cos(v),Subtract(m,Times(n,p))),Power(Plus(c,Times(b,Power(Sin(v),n)),Times(a,Power(Cos(v),n))),p)),IntegersQ(m,n,p))),
ISetDelayed(271,TrigSimplifyAux(Times(Power($($s("§sec"),v_),m_DEFAULT),Power(Plus(a_DEFAULT,Times(Power($($s("§tan"),v_),n_DEFAULT),b_DEFAULT),Times(Power($($s("§sec"),v_),n_DEFAULT),c_DEFAULT)),p_DEFAULT),u_DEFAULT)),
    Condition(Times(u,Power(Sec(v),Plus(m,Times(n,p))),Power(Plus(c,Times(b,Power(Sin(v),n)),Times(a,Power(Cos(v),n))),p)),IntegersQ(m,n,p))),
ISetDelayed(272,TrigSimplifyAux(Times(Power($($s("§sin"),v_),m_DEFAULT),Power(Plus(a_DEFAULT,Times(Power($($s("§cot"),v_),n_DEFAULT),b_DEFAULT),Times(Power($($s("§csc"),v_),n_DEFAULT),c_DEFAULT)),p_DEFAULT),u_DEFAULT)),
    Condition(Times(u,Power(Sin(v),Subtract(m,Times(n,p))),Power(Plus(c,Times(b,Power(Cos(v),n)),Times(a,Power(Sin(v),n))),p)),IntegersQ(m,n,p)))
  );
}
