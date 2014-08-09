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
public class IntRules54 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Times(Power(Times($($s("§tan"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),c_DEFAULT),m_DEFAULT),Power(Times($($s("§sin"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pd_DEFAULT),pn_DEFAULT),u_),x_Symbol),
    Condition(Times(Power(Times(c,Tan(Plus(a,Times(b,x)))),m),Power(Times(pd,Cos(Plus(a,Times(b,x)))),m),Power(Power(Times(pd,Sin(Plus(a,Times(b,x)))),m),CN1),Int(Times(ActivateTrig(u),Power(Times(pd,Sin(Plus(a,Times(b,x)))),Plus(m,pn)),Power(Power(Times(pd,Cos(Plus(a,Times(b,x)))),m),CN1)),x)),And(And(FreeQ(List(a,b,c,pd,m,pn),x),KnownSineIntegrandQ(u,x)),Not(IntegerQ(m))))),
ISetDelayed(Int(Times(Power(Times($($s("§tan"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),c_DEFAULT),m_DEFAULT),Power(Times($($s("§cos"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pd_DEFAULT),pn_DEFAULT),u_),x_Symbol),
    Condition(Times(Power(Times(c,Tan(Plus(a,Times(b,x)))),m),Power(Times(pd,Cos(Plus(a,Times(b,x)))),m),Power(Power(Times(pd,Sin(Plus(a,Times(b,x)))),m),CN1),Int(Times(ActivateTrig(u),Power(Times(pd,Sin(Plus(a,Times(b,x)))),m),Power(Power(Times(pd,Cos(Plus(a,Times(b,x)))),Plus(m,Times(CN1,pn))),CN1)),x)),And(And(FreeQ(List(a,b,c,pd,m,pn),x),KnownSineIntegrandQ(u,x)),Not(IntegerQ(m))))),
ISetDelayed(Int(Times(Power(Times($($s("§cot"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),c_DEFAULT),m_DEFAULT),Power(Times($($s("§sin"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pd_DEFAULT),pn_DEFAULT),u_),x_Symbol),
    Condition(Times(Power(Times(c,Cot(Plus(a,Times(b,x)))),m),Power(Times(pd,Sin(Plus(a,Times(b,x)))),m),Power(Power(Times(pd,Cos(Plus(a,Times(b,x)))),m),CN1),Int(Times(ActivateTrig(u),Power(Times(pd,Cos(Plus(a,Times(b,x)))),m),Power(Power(Times(pd,Sin(Plus(a,Times(b,x)))),Plus(m,Times(CN1,pn))),CN1)),x)),And(And(FreeQ(List(a,b,c,pd,m,pn),x),KnownSineIntegrandQ(u,x)),Not(IntegerQ(m))))),
ISetDelayed(Int(Times(Power(Times($($s("§cot"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),c_DEFAULT),m_DEFAULT),Power(Times($($s("§cos"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pd_DEFAULT),pn_DEFAULT),u_),x_Symbol),
    Condition(Times(Power(Times(c,Cot(Plus(a,Times(b,x)))),m),Power(Times(pd,Sin(Plus(a,Times(b,x)))),m),Power(Power(Times(pd,Cos(Plus(a,Times(b,x)))),m),CN1),Int(Times(ActivateTrig(u),Power(Times(pd,Cos(Plus(a,Times(b,x)))),Plus(m,pn)),Power(Power(Times(pd,Sin(Plus(a,Times(b,x)))),m),CN1)),x)),And(And(FreeQ(List(a,b,c,pd,m,pn),x),KnownSineIntegrandQ(u,x)),Not(IntegerQ(m))))),
ISetDelayed(Int(Times(Power(Times($($s("§sec"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),c_DEFAULT),m_DEFAULT),Power(Times($($s("§cos"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pd_DEFAULT),pn_DEFAULT),u_),x_Symbol),
    Condition(Times(Power(Times(c,Sec(Plus(a,Times(b,x)))),m),Power(Times(pd,Cos(Plus(a,Times(b,x)))),m),Int(Times(ActivateTrig(u),Power(Times(pd,Cos(Plus(a,Times(b,x)))),Plus(pn,Times(CN1,m)))),x)),And(FreeQ(List(a,b,c,pd,m,pn),x),KnownSineIntegrandQ(u,x)))),
ISetDelayed(Int(Times(Power(Times($($s("§sec"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),c_DEFAULT),m_DEFAULT),Power(Times($($s("§cos"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pd_DEFAULT),pn_DEFAULT),u_),x_Symbol),
    Condition(Times(Power(Times(c,Csc(Plus(a,Times(b,x)))),m),Power(Times(pd,Sin(Plus(a,Times(b,x)))),m),Int(Times(ActivateTrig(u),Power(Times(pd,Sin(Plus(a,Times(b,x)))),Plus(pn,Times(CN1,m)))),x)),And(FreeQ(List(a,b,c,pd,m,pn),x),KnownSineIntegrandQ(u,x)))),
ISetDelayed(Int(Times(Power(Times($($s("§tan"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),c_DEFAULT),m_DEFAULT),u_),x_Symbol),
    Condition(Times(Power(Times(c,Tan(Plus(a,Times(b,x)))),m),Power(Times(c,Cos(Plus(a,Times(b,x)))),m),Power(Power(Times(c,Sin(Plus(a,Times(b,x)))),m),CN1),Int(Times(ActivateTrig(u),Power(Times(c,Sin(Plus(a,Times(b,x)))),m),Power(Power(Times(c,Cos(Plus(a,Times(b,x)))),m),CN1)),x)),And(And(FreeQ(List(a,b,c,m),x),Not(IntegerQ(m))),KnownSineIntegrandQ(u,x)))),
ISetDelayed(Int(Times(Power(Times($($s("§cot"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),c_DEFAULT),m_DEFAULT),u_),x_Symbol),
    Condition(Times(Power(Times(c,Cot(Plus(a,Times(b,x)))),m),Power(Times(c,Sin(Plus(a,Times(b,x)))),m),Power(Power(Times(c,Cos(Plus(a,Times(b,x)))),m),CN1),Int(Times(ActivateTrig(u),Power(Times(c,Cos(Plus(a,Times(b,x)))),m),Power(Power(Times(c,Sin(Plus(a,Times(b,x)))),m),CN1)),x)),And(And(FreeQ(List(a,b,c,m),x),Not(IntegerQ(m))),KnownSineIntegrandQ(u,x)))),
ISetDelayed(Int(Times(Power(Times($($s("§sec"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),c_DEFAULT),m_DEFAULT),u_),x_Symbol),
    Condition(Times(Power(Times(c,Sec(Plus(a,Times(b,x)))),m),Power(Times(c,Cos(Plus(a,Times(b,x)))),m),Int(Times(ActivateTrig(u),Power(Power(Times(c,Cos(Plus(a,Times(b,x)))),m),CN1)),x)),And(And(FreeQ(List(a,b,c,m),x),Not(IntegerQ(m))),KnownSineIntegrandQ(u,x)))),
ISetDelayed(Int(Times(Power(Times($($s("§csc"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),c_DEFAULT),m_DEFAULT),u_),x_Symbol),
    Condition(Times(Power(Times(c,Csc(Plus(a,Times(b,x)))),m),Power(Times(c,Sin(Plus(a,Times(b,x)))),m),Int(Times(ActivateTrig(u),Power(Power(Times(c,Sin(Plus(a,Times(b,x)))),m),CN1)),x)),And(And(FreeQ(List(a,b,c,m),x),Not(IntegerQ(m))),KnownSineIntegrandQ(u,x)))),
ISetDelayed(Int(Times(Plus(Times($($s("§csc"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pb_DEFAULT),pa_),Power(Times($($s("§sin"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),c_DEFAULT),pn_DEFAULT),u_),x_Symbol),
    Condition(Times(c,Int(Times(ActivateTrig(u),Power(Times(c,Sin(Plus(a,Times(b,x)))),Plus(pn,Times(CN1,C1))),Plus(pb,Times(pa,Sin(Plus(a,Times(b,x)))))),x)),And(FreeQ(List(a,b,c,pa,pb,pn),x),KnownSineIntegrandQ(u,x)))),
ISetDelayed(Int(Times(Plus(Times($($s("§sec"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pb_DEFAULT),pa_),Power(Times($($s("§cos"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),c_DEFAULT),pn_DEFAULT),u_),x_Symbol),
    Condition(Times(c,Int(Times(ActivateTrig(u),Power(Times(c,Cos(Plus(a,Times(b,x)))),Plus(pn,Times(CN1,C1))),Plus(pb,Times(pa,Cos(Plus(a,Times(b,x)))))),x)),And(FreeQ(List(a,b,c,pa,pb,pn),x),KnownSineIntegrandQ(u,x)))),
ISetDelayed(Int(Times(Plus(Times($($s("§csc"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pb_DEFAULT),pa_),u_),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus(pb,Times(pa,Sin(Plus(a,Times(b,x))))),Power(Sin(Plus(a,Times(b,x))),CN1)),x),And(FreeQ(List(a,b,pa,pb),x),KnownSineIntegrandQ(u,x)))),
ISetDelayed(Int(Times(Plus(Times($($s("§sec"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pb_DEFAULT),pa_),u_),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus(pb,Times(pa,Cos(Plus(a,Times(b,x))))),Power(Cos(Plus(a,Times(b,x))),CN1)),x),And(FreeQ(List(a,b,pa,pb),x),KnownSineIntegrandQ(u,x)))),
ISetDelayed(Int(Times(Plus(Times($($s("§csc"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pb_DEFAULT),Times(Sqr($($s("§csc"),Plus(Times(b_DEFAULT,x_),a_DEFAULT))),pc_DEFAULT),pa_DEFAULT),Power(Times($($s("§sin"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),c_DEFAULT),pn_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Times(Sqr(c),Int(Times(ActivateTrig(u),Power(Times(c,Sin(Plus(a,Times(b,x)))),Plus(pn,Times(CN1,C2))),Plus(pc,Times(pb,Sin(Plus(a,Times(b,x)))),Times(pa,Sqr(Sin(Plus(a,Times(b,x))))))),x)),And(FreeQ(List(a,b,c,pa,pb,pc,pn),x),KnownSineIntegrandQ(u,x)))),
ISetDelayed(Int(Times(Plus(Times($($s("§sec"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pb_DEFAULT),Times(Sqr($($s("§sec"),Plus(Times(b_DEFAULT,x_),a_DEFAULT))),pc_DEFAULT),pa_DEFAULT),Power(Times($($s("§cos"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),c_DEFAULT),pn_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Times(Sqr(c),Int(Times(ActivateTrig(u),Power(Times(c,Cos(Plus(a,Times(b,x)))),Plus(pn,Times(CN1,C2))),Plus(pc,Times(pb,Cos(Plus(a,Times(b,x)))),Times(pa,Sqr(Cos(Plus(a,Times(b,x))))))),x)),And(FreeQ(List(a,b,c,pa,pb,pc,pn),x),KnownSineIntegrandQ(u,x)))),
ISetDelayed(Int(Times(Plus(Times(Sqr($($s("§csc"),Plus(Times(b_DEFAULT,x_),a_DEFAULT))),pc_DEFAULT),pa_),Power(Times($($s("§sin"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),c_DEFAULT),pn_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Times(Sqr(c),Int(Times(ActivateTrig(u),Power(Times(c,Sin(Plus(a,Times(b,x)))),Plus(pn,Times(CN1,C2))),Plus(pc,Times(pa,Sqr(Sin(Plus(a,Times(b,x))))))),x)),And(FreeQ(List(a,b,c,pa,pc,pn),x),KnownSineIntegrandQ(u,x)))),
ISetDelayed(Int(Times(Plus(Times(Sqr($($s("§sec"),Plus(Times(b_DEFAULT,x_),a_DEFAULT))),pc_DEFAULT),pa_),Power(Times($($s("§cos"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),c_DEFAULT),pn_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Times(Sqr(c),Int(Times(ActivateTrig(u),Power(Times(c,Cos(Plus(a,Times(b,x)))),Plus(pn,Times(CN1,C2))),Plus(pc,Times(pa,Sqr(Cos(Plus(a,Times(b,x))))))),x)),And(FreeQ(List(a,b,c,pa,pc,pn),x),KnownSineIntegrandQ(u,x)))),
ISetDelayed(Int(Times(Plus(Times($($s("§csc"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pb_DEFAULT),Times(Sqr($($s("§csc"),Plus(Times(b_DEFAULT,x_),a_DEFAULT))),pc_DEFAULT),pa_DEFAULT),u_),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus(pc,Times(pb,Sin(Plus(a,Times(b,x)))),Times(pa,Sqr(Sin(Plus(a,Times(b,x)))))),Power(Sin(Plus(a,Times(b,x))),CN2)),x),And(FreeQ(List(a,b,pa,pb,pc),x),KnownSineIntegrandQ(u,x)))),
ISetDelayed(Int(Times(Plus(Times($($s("§sec"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pb_DEFAULT),Times(Sqr($($s("§sec"),Plus(Times(b_DEFAULT,x_),a_DEFAULT))),pc_DEFAULT),pa_DEFAULT),u_),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus(pc,Times(pb,Cos(Plus(a,Times(b,x)))),Times(pa,Sqr(Cos(Plus(a,Times(b,x)))))),Power(Cos(Plus(a,Times(b,x))),CN2)),x),And(FreeQ(List(a,b,pa,pb,pc),x),KnownSineIntegrandQ(u,x)))),
ISetDelayed(Int(Times(Plus(Times(Sqr($($s("§csc"),Plus(Times(b_DEFAULT,x_),a_DEFAULT))),pc_DEFAULT),pa_),u_),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus(pc,Times(pa,Sqr(Sin(Plus(a,Times(b,x)))))),Power(Sin(Plus(a,Times(b,x))),CN2)),x),And(FreeQ(List(a,b,pa,pc),x),KnownSineIntegrandQ(u,x)))),
ISetDelayed(Int(Times(Plus(Times(Sqr($($s("§sec"),Plus(Times(b_DEFAULT,x_),a_DEFAULT))),pc_DEFAULT),pa_),u_),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus(pc,Times(pa,Sqr(Cos(Plus(a,Times(b,x)))))),Power(Cos(Plus(a,Times(b,x))),CN2)),x),And(FreeQ(List(a,b,pa,pc),x),KnownSineIntegrandQ(u,x)))),
ISetDelayed(Int(Times(Plus(Times($($s("§sin"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pb_DEFAULT),Times($($s("§csc"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pc_DEFAULT),pa_DEFAULT),u_),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus(pc,Times(pa,Sin(Plus(a,Times(b,x)))),Times(pb,Sqr(Sin(Plus(a,Times(b,x)))))),Power(Sin(Plus(a,Times(b,x))),CN1)),x),FreeQ(List(a,b,pa,pb,pc),x))),
ISetDelayed(Int(Times(Plus(Times($($s("§cos"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pb_DEFAULT),Times($($s("§sec"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pc_DEFAULT),pa_DEFAULT),u_),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus(pc,Times(pa,Cos(Plus(a,Times(b,x)))),Times(pb,Sqr(Cos(Plus(a,Times(b,x)))))),Power(Cos(Plus(a,Times(b,x))),CN1)),x),FreeQ(List(a,b,pa,pb,pc),x))),
ISetDelayed(Int(Times(Plus(Times(Power($($s("§sin"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pn_DEFAULT),pa_DEFAULT),Times(Power($($s("§sin"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),$p("n1")),pb_DEFAULT),Times(Power($($s("§sin"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),$p("n2")),pc_DEFAULT)),u_),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Power(Sin(Plus(a,Times(b,x))),pn),Plus(pa,Times(pb,Sin(Plus(a,Times(b,x)))),Times(pc,Sqr(Sin(Plus(a,Times(b,x))))))),x),And(And(FreeQ(List(a,b,pa,pb,pc,pn),x),ZeroQ(Plus($s("n1"),Times(CN1,pn),Times(CN1,C1)))),ZeroQ(Plus($s("n2"),Times(CN1,pn),Times(CN1,C2)))))),
ISetDelayed(Int(Times(Plus(Times(Power($($s("§cos"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pn_DEFAULT),pa_DEFAULT),Times(Power($($s("§cos"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),$p("n1")),pb_DEFAULT),Times(Power($($s("§cos"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),$p("n2")),pc_DEFAULT)),u_),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Power(Cos(Plus(a,Times(b,x))),pn),Plus(pa,Times(pb,Cos(Plus(a,Times(b,x)))),Times(pc,Sqr(Cos(Plus(a,Times(b,x))))))),x),And(And(FreeQ(List(a,b,pa,pb,pc,pn),x),ZeroQ(Plus($s("n1"),Times(CN1,pn),Times(CN1,C1)))),ZeroQ(Plus($s("n2"),Times(CN1,pn),Times(CN1,C2))))))
  );
}
