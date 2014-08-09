package org.matheclipse.core.integrate.rubi45;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi45.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi45.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/** 
 * IndefiniteIntegrationRules from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *  
 */
public class IntRules81 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Times(Power(Times($($s("§sin"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),c_DEFAULT),m_DEFAULT),Power(Times($($s("§csc"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pd_DEFAULT),pn_DEFAULT),u_),x_Symbol),
    Condition(Times(Power(Times(c,Sin(Plus(a,Times(b,x)))),m),Power(Times(pd,Csc(Plus(a,Times(b,x)))),m),Int(Times(ActivateTrig(u),Power(Times(pd,Csc(Plus(a,Times(b,x)))),Plus(pn,Times(CN1,m)))),x)),And(FreeQ(List(a,b,c,pd,m,pn),x),KnownSecantIntegrandQ(u,x)))),
ISetDelayed(Int(Times(Power(Times($($s("§cos"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),c_DEFAULT),m_DEFAULT),Power(Times($($s("§sec"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pd_DEFAULT),pn_DEFAULT),u_),x_Symbol),
    Condition(Times(Power(Times(c,Cos(Plus(a,Times(b,x)))),m),Power(Times(pd,Sec(Plus(a,Times(b,x)))),m),Int(Times(ActivateTrig(u),Power(Times(pd,Sec(Plus(a,Times(b,x)))),Plus(pn,Times(CN1,m)))),x)),And(FreeQ(List(a,b,c,pd,m,pn),x),KnownSecantIntegrandQ(u,x)))),
ISetDelayed(Int(Times(Power(Times($($s("§tan"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),c_DEFAULT),m_DEFAULT),Power(Times($($s("§sec"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pd_DEFAULT),pn_DEFAULT),u_),x_Symbol),
    Condition(Times(Power(Times(c,Tan(Plus(a,Times(b,x)))),m),Power(Times(pd,Csc(Plus(a,Times(b,x)))),m),Power(Power(Times(pd,Sec(Plus(a,Times(b,x)))),m),CN1),Int(Times(ActivateTrig(u),Power(Times(pd,Sec(Plus(a,Times(b,x)))),Plus(m,pn)),Power(Power(Times(pd,Csc(Plus(a,Times(b,x)))),m),CN1)),x)),And(And(FreeQ(List(a,b,c,pd,m,pn),x),KnownSecantIntegrandQ(u,x)),Not(IntegerQ(m))))),
ISetDelayed(Int(Times(Power(Times($($s("§tan"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),c_DEFAULT),m_DEFAULT),Power(Times($($s("§csc"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pd_DEFAULT),pn_DEFAULT),u_),x_Symbol),
    Condition(Times(Power(Times(c,Tan(Plus(a,Times(b,x)))),m),Power(Times(pd,Csc(Plus(a,Times(b,x)))),m),Power(Power(Times(pd,Sec(Plus(a,Times(b,x)))),m),CN1),Int(Times(ActivateTrig(u),Power(Times(pd,Sec(Plus(a,Times(b,x)))),m),Power(Power(Times(pd,Csc(Plus(a,Times(b,x)))),Plus(m,Times(CN1,pn))),CN1)),x)),And(And(FreeQ(List(a,b,c,pd,m,pn),x),KnownSecantIntegrandQ(u,x)),Not(IntegerQ(m))))),
ISetDelayed(Int(Times(Power(Times($($s("§cot"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),c_DEFAULT),m_DEFAULT),Power(Times($($s("§sec"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pd_DEFAULT),pn_DEFAULT),u_),x_Symbol),
    Condition(Times(Power(Times(c,Cot(Plus(a,Times(b,x)))),m),Power(Times(pd,Sec(Plus(a,Times(b,x)))),m),Power(Power(Times(pd,Csc(Plus(a,Times(b,x)))),m),CN1),Int(Times(ActivateTrig(u),Power(Times(pd,Csc(Plus(a,Times(b,x)))),m),Power(Power(Times(pd,Sec(Plus(a,Times(b,x)))),Plus(m,Times(CN1,pn))),CN1)),x)),And(And(FreeQ(List(a,b,c,pd,m,pn),x),KnownSecantIntegrandQ(u,x)),Not(IntegerQ(m))))),
ISetDelayed(Int(Times(Power(Times($($s("§cot"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),c_DEFAULT),m_DEFAULT),Power(Times($($s("§csc"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pd_DEFAULT),pn_DEFAULT),u_),x_Symbol),
    Condition(Times(Power(Times(c,Cot(Plus(a,Times(b,x)))),m),Power(Times(pd,Sec(Plus(a,Times(b,x)))),m),Power(Power(Times(pd,Csc(Plus(a,Times(b,x)))),m),CN1),Int(Times(ActivateTrig(u),Power(Times(pd,Csc(Plus(a,Times(b,x)))),Plus(m,pn)),Power(Power(Times(pd,Sec(Plus(a,Times(b,x)))),m),CN1)),x)),And(And(FreeQ(List(a,b,c,pd,m,pn),x),KnownSecantIntegrandQ(u,x)),Not(IntegerQ(m))))),
ISetDelayed(Int(Times(Power(Times($($s("§sin"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),c_DEFAULT),m_DEFAULT),u_),x_Symbol),
    Condition(Times(Power(Times(c,Csc(Plus(a,Times(b,x)))),m),Power(Times(c,Sin(Plus(a,Times(b,x)))),m),Int(Times(ActivateTrig(u),Power(Power(Times(c,Csc(Plus(a,Times(b,x)))),m),CN1)),x)),And(And(FreeQ(List(a,b,c,m),x),Not(IntegerQ(m))),KnownSecantIntegrandQ(u,x)))),
ISetDelayed(Int(Times(Power(Times($($s("§cos"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),c_DEFAULT),m_DEFAULT),u_),x_Symbol),
    Condition(Times(Power(Times(c,Cos(Plus(a,Times(b,x)))),m),Power(Times(c,Sec(Plus(a,Times(b,x)))),m),Int(Times(ActivateTrig(u),Power(Power(Times(c,Sec(Plus(a,Times(b,x)))),m),CN1)),x)),And(And(FreeQ(List(a,b,c,m),x),Not(IntegerQ(m))),KnownSecantIntegrandQ(u,x)))),
ISetDelayed(Int(Times(Power(Times($($s("§tan"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),c_DEFAULT),m_DEFAULT),u_),x_Symbol),
    Condition(Times(Power(Times(c,Tan(Plus(a,Times(b,x)))),m),Power(Times(c,Csc(Plus(a,Times(b,x)))),m),Power(Power(Times(c,Sec(Plus(a,Times(b,x)))),m),CN1),Int(Times(ActivateTrig(u),Power(Times(c,Sec(Plus(a,Times(b,x)))),m),Power(Power(Times(c,Csc(Plus(a,Times(b,x)))),m),CN1)),x)),And(And(FreeQ(List(a,b,c,m),x),Not(IntegerQ(m))),KnownSecantIntegrandQ(u,x)))),
ISetDelayed(Int(Times(Power(Times($($s("§cot"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),c_DEFAULT),m_DEFAULT),u_),x_Symbol),
    Condition(Times(Power(Times(c,Cot(Plus(a,Times(b,x)))),m),Power(Times(c,Sec(Plus(a,Times(b,x)))),m),Power(Power(Times(c,Csc(Plus(a,Times(b,x)))),m),CN1),Int(Times(ActivateTrig(u),Power(Times(c,Csc(Plus(a,Times(b,x)))),m),Power(Power(Times(c,Sec(Plus(a,Times(b,x)))),m),CN1)),x)),And(And(FreeQ(List(a,b,c,m),x),Not(IntegerQ(m))),KnownSecantIntegrandQ(u,x)))),
ISetDelayed(Int(Times(Plus(Times($($s("§cos"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pb_DEFAULT),pa_),Power(Times($($s("§sec"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),c_DEFAULT),pn_DEFAULT),u_),x_Symbol),
    Condition(Times(c,Int(Times(ActivateTrig(u),Power(Times(c,Sec(Plus(a,Times(b,x)))),Plus(pn,Times(CN1,C1))),Plus(pb,Times(pa,Sec(Plus(a,Times(b,x)))))),x)),And(FreeQ(List(a,b,c,pa,pb,pn),x),KnownSecantIntegrandQ(u,x)))),
ISetDelayed(Int(Times(Plus(Times($($s("§sin"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pb_DEFAULT),pa_),Power(Times($($s("§csc"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),c_DEFAULT),pn_DEFAULT),u_),x_Symbol),
    Condition(Times(c,Int(Times(ActivateTrig(u),Power(Times(c,Csc(Plus(a,Times(b,x)))),Plus(pn,Times(CN1,C1))),Plus(pb,Times(pa,Csc(Plus(a,Times(b,x)))))),x)),And(FreeQ(List(a,b,c,pa,pb,pn),x),KnownSecantIntegrandQ(u,x)))),
ISetDelayed(Int(Times(Plus(Times($($s("§cos"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pb_DEFAULT),pa_),u_),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus(pb,Times(pa,Sec(Plus(a,Times(b,x))))),Power(Sec(Plus(a,Times(b,x))),CN1)),x),And(FreeQ(List(a,b,pa,pb),x),KnownSecantIntegrandQ(u,x)))),
ISetDelayed(Int(Times(Plus(Times($($s("§sin"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pb_DEFAULT),pa_),u_),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus(pb,Times(pa,Csc(Plus(a,Times(b,x))))),Power(Csc(Plus(a,Times(b,x))),CN1)),x),And(FreeQ(List(a,b,pa,pb),x),KnownSecantIntegrandQ(u,x)))),
ISetDelayed(Int(Times(Plus(Times($($s("§cos"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pb_DEFAULT),Times(Sqr($($s("§cos"),Plus(Times(b_DEFAULT,x_),a_DEFAULT))),pc_DEFAULT),pa_DEFAULT),Power(Times($($s("§sec"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),c_DEFAULT),pn_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Times(Sqr(c),Int(Times(ActivateTrig(u),Power(Times(c,Sec(Plus(a,Times(b,x)))),Plus(pn,Times(CN1,C2))),Plus(pc,Times(pb,Sec(Plus(a,Times(b,x)))),Times(pa,Sqr(Sec(Plus(a,Times(b,x))))))),x)),And(FreeQ(List(a,b,c,pa,pb,pc,pn),x),KnownSecantIntegrandQ(u,x)))),
ISetDelayed(Int(Times(Plus(Times($($s("§sin"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pb_DEFAULT),Times(Sqr($($s("§sin"),Plus(Times(b_DEFAULT,x_),a_DEFAULT))),pc_DEFAULT),pa_DEFAULT),Power(Times($($s("§csc"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),c_DEFAULT),pn_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Times(Sqr(c),Int(Times(ActivateTrig(u),Power(Times(c,Csc(Plus(a,Times(b,x)))),Plus(pn,Times(CN1,C2))),Plus(pc,Times(pb,Csc(Plus(a,Times(b,x)))),Times(pa,Sqr(Csc(Plus(a,Times(b,x))))))),x)),And(FreeQ(List(a,b,c,pa,pb,pc,pn),x),KnownSecantIntegrandQ(u,x)))),
ISetDelayed(Int(Times(Plus(Times(Sqr($($s("§cos"),Plus(Times(b_DEFAULT,x_),a_DEFAULT))),pc_DEFAULT),pa_),Power(Times($($s("§sec"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),c_DEFAULT),pn_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Times(Sqr(c),Int(Times(ActivateTrig(u),Power(Times(c,Sec(Plus(a,Times(b,x)))),Plus(pn,Times(CN1,C2))),Plus(pc,Times(pa,Sqr(Sec(Plus(a,Times(b,x))))))),x)),And(FreeQ(List(a,b,c,pa,pc,pn),x),KnownSecantIntegrandQ(u,x)))),
ISetDelayed(Int(Times(Plus(Times(Sqr($($s("§sin"),Plus(Times(b_DEFAULT,x_),a_DEFAULT))),pc_DEFAULT),pa_),Power(Times($($s("§csc"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),c_DEFAULT),pn_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Times(Sqr(c),Int(Times(ActivateTrig(u),Power(Times(c,Csc(Plus(a,Times(b,x)))),Plus(pn,Times(CN1,C2))),Plus(pc,Times(pa,Sqr(Csc(Plus(a,Times(b,x))))))),x)),And(FreeQ(List(a,b,c,pa,pc,pn),x),KnownSecantIntegrandQ(u,x)))),
ISetDelayed(Int(Times(Plus(Times($($s("§cos"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pb_DEFAULT),Times(Sqr($($s("§cos"),Plus(Times(b_DEFAULT,x_),a_DEFAULT))),pc_DEFAULT),pa_DEFAULT),u_),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus(pc,Times(pb,Sec(Plus(a,Times(b,x)))),Times(pa,Sqr(Sec(Plus(a,Times(b,x)))))),Power(Sec(Plus(a,Times(b,x))),CN2)),x),And(FreeQ(List(a,b,pa,pb,pc),x),KnownSecantIntegrandQ(u,x)))),
ISetDelayed(Int(Times(Plus(Times($($s("§sin"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pb_DEFAULT),Times(Sqr($($s("§sin"),Plus(Times(b_DEFAULT,x_),a_DEFAULT))),pc_DEFAULT),pa_DEFAULT),u_),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus(pc,Times(pb,Csc(Plus(a,Times(b,x)))),Times(pa,Sqr(Csc(Plus(a,Times(b,x)))))),Power(Csc(Plus(a,Times(b,x))),CN2)),x),And(FreeQ(List(a,b,pa,pb,pc),x),KnownSecantIntegrandQ(u,x)))),
ISetDelayed(Int(Times(Plus(Times(Sqr($($s("§cos"),Plus(Times(b_DEFAULT,x_),a_DEFAULT))),pc_DEFAULT),pa_),u_),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus(pc,Times(pa,Sqr(Sec(Plus(a,Times(b,x)))))),Power(Sec(Plus(a,Times(b,x))),CN2)),x),And(FreeQ(List(a,b,pa,pc),x),KnownSecantIntegrandQ(u,x)))),
ISetDelayed(Int(Times(Plus(Times(Sqr($($s("§sin"),Plus(Times(b_DEFAULT,x_),a_DEFAULT))),pc_DEFAULT),pa_),u_),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus(pc,Times(pa,Sqr(Csc(Plus(a,Times(b,x)))))),Power(Csc(Plus(a,Times(b,x))),CN2)),x),And(FreeQ(List(a,b,pa,pc),x),KnownSecantIntegrandQ(u,x)))),
ISetDelayed(Int(Times(Plus(Times(Power($($s("§sec"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pn_DEFAULT),pa_DEFAULT),Times(Power($($s("§sec"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),$p("n1")),pb_DEFAULT),Times(Power($($s("§sec"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),$p("n2")),pc_DEFAULT)),u_),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Power(Sec(Plus(a,Times(b,x))),pn),Plus(pa,Times(pb,Sec(Plus(a,Times(b,x)))),Times(pc,Sqr(Sec(Plus(a,Times(b,x))))))),x),And(And(FreeQ(List(a,b,pa,pb,pc,pn),x),ZeroQ(Plus($s("n1"),Times(CN1,pn),Times(CN1,C1)))),ZeroQ(Plus($s("n2"),Times(CN1,pn),Times(CN1,C2)))))),
ISetDelayed(Int(Times(Plus(Times(Power($($s("§csc"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pn_DEFAULT),pa_DEFAULT),Times(Power($($s("§csc"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),$p("n1")),pb_DEFAULT),Times(Power($($s("§csc"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),$p("n2")),pc_DEFAULT)),u_),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Power(Csc(Plus(a,Times(b,x))),pn),Plus(pa,Times(pb,Csc(Plus(a,Times(b,x)))),Times(pc,Sqr(Csc(Plus(a,Times(b,x))))))),x),And(And(FreeQ(List(a,b,pa,pb,pc,pn),x),ZeroQ(Plus($s("n1"),Times(CN1,pn),Times(CN1,C1)))),ZeroQ(Plus($s("n2"),Times(CN1,pn),Times(CN1,C2))))))
  );
}
