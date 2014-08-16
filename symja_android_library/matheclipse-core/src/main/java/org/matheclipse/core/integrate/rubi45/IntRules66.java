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
public class IntRules66 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Times(u_,Power(Times(c_DEFAULT,$($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_DEFAULT),Power(Times(pd_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),pn_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(c,Cot(Plus(a,Times(b,x)))),m),Power(Times(pd,Tan(Plus(a,Times(b,x)))),m),Int(Times(ActivateTrig(u),Power(Times(pd,Tan(Plus(a,Times(b,x)))),Plus(pn,Times(CN1,m)))),x)),And(FreeQ(List(a,b,c,pd,m,pn),x),KnownTangentIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Power(Times(pd_DEFAULT,$($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),pn_DEFAULT),Power(Times(c_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(c,Tan(Plus(a,Times(b,x)))),m),Power(Times(pd,Cos(Plus(a,Times(b,x)))),m),Power(Power(Times(pd,Sin(Plus(a,Times(b,x)))),m),CN1),Int(Times(ActivateTrig(u),Power(Times(pd,Sin(Plus(a,Times(b,x)))),m),Power(Power(Times(pd,Cos(Plus(a,Times(b,x)))),Plus(m,Times(CN1,pn))),CN1)),x)),And(FreeQ(List(a,b,c,pd,m,pn),x),KnownCotangentIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Power(Times(c_DEFAULT,$($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(c,Cot(Plus(a,Times(b,x)))),m),Power(Times(c,Tan(Plus(a,Times(b,x)))),m),Int(Times(ActivateTrig(u),Power(Power(Times(c,Tan(Plus(a,Times(b,x)))),m),CN1)),x)),And(And(FreeQ(List(a,b,c,m),x),Not(IntegerQ(m))),KnownTangentIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Power(Times(c_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(c,Cot(Plus(a,Times(b,x)))),m),Power(Times(c,Tan(Plus(a,Times(b,x)))),m),Int(Times(ActivateTrig(u),Power(Power(Times(c,Cot(Plus(a,Times(b,x)))),m),CN1)),x)),And(And(FreeQ(List(a,b,c,m),x),Not(IntegerQ(m))),KnownCotangentIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Plus(pa_,Times(pb_DEFAULT,$($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),Power(Times(c_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),pn_DEFAULT)),x_Symbol),
    Condition(Times(c,Int(Times(ActivateTrig(u),Power(Times(c,Tan(Plus(a,Times(b,x)))),Plus(pn,Times(CN1,C1))),Plus(pb,Times(pa,Tan(Plus(a,Times(b,x)))))),x)),And(FreeQ(List(a,b,c,pa,pb,pn),x),KnownTangentIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Plus(pa_,Times(pb_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),Power(Times(c_DEFAULT,$($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),pn_DEFAULT)),x_Symbol),
    Condition(Times(c,Int(Times(ActivateTrig(u),Power(Times(c,Cot(Plus(a,Times(b,x)))),Plus(pn,Times(CN1,C1))),Plus(pb,Times(pa,Cot(Plus(a,Times(b,x)))))),x)),And(FreeQ(List(a,b,c,pa,pb,pn),x),KnownCotangentIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Plus(pa_,Times(pb_DEFAULT,$($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus(pb,Times(pa,Tan(Plus(a,Times(b,x))))),Power(Tan(Plus(a,Times(b,x))),CN1)),x),And(FreeQ(List(a,b,pa,pb),x),KnownTangentIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Plus(pa_,Times(pb_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus(pb,Times(pa,Cot(Plus(a,Times(b,x))))),Power(Cot(Plus(a,Times(b,x))),CN1)),x),And(FreeQ(List(a,b,pa,pb),x),KnownCotangentIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_DEFAULT,Plus(pa_DEFAULT,Times(pc_DEFAULT,Sqr($($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),Times(pb_DEFAULT,$($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),Power(Times(c_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),pn_DEFAULT)),x_Symbol),
    Condition(Times(Sqr(c),Int(Times(ActivateTrig(u),Power(Times(c,Tan(Plus(a,Times(b,x)))),Plus(pn,Times(CN1,C2))),Plus(pc,Times(pb,Tan(Plus(a,Times(b,x)))),Times(pa,Sqr(Tan(Plus(a,Times(b,x))))))),x)),And(FreeQ(List(a,b,c,pa,pb,pc,pn),x),KnownTangentIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_DEFAULT,Plus(pa_DEFAULT,Times(pc_DEFAULT,Sqr($($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),Times(pb_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),Power(Times(c_DEFAULT,$($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),pn_DEFAULT)),x_Symbol),
    Condition(Times(Sqr(c),Int(Times(ActivateTrig(u),Power(Times(c,Cot(Plus(a,Times(b,x)))),Plus(pn,Times(CN1,C2))),Plus(pc,Times(pb,Cot(Plus(a,Times(b,x)))),Times(pa,Sqr(Cot(Plus(a,Times(b,x))))))),x)),And(FreeQ(List(a,b,c,pa,pb,pc,pn),x),KnownCotangentIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_DEFAULT,Plus(pa_,Times(pc_DEFAULT,Sqr($($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),Power(Times(c_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),pn_DEFAULT)),x_Symbol),
    Condition(Times(Sqr(c),Int(Times(ActivateTrig(u),Power(Times(c,Tan(Plus(a,Times(b,x)))),Plus(pn,Times(CN1,C2))),Plus(pc,Times(pa,Sqr(Tan(Plus(a,Times(b,x))))))),x)),And(FreeQ(List(a,b,c,pa,pc,pn),x),KnownTangentIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_DEFAULT,Plus(pa_,Times(pc_DEFAULT,Sqr($($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),Power(Times(c_DEFAULT,$($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),pn_DEFAULT)),x_Symbol),
    Condition(Times(Sqr(c),Int(Times(ActivateTrig(u),Power(Times(c,Cot(Plus(a,Times(b,x)))),Plus(pn,Times(CN1,C2))),Plus(pc,Times(pa,Sqr(Cot(Plus(a,Times(b,x))))))),x)),And(FreeQ(List(a,b,c,pa,pc,pn),x),KnownCotangentIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Plus(pa_DEFAULT,Times(pc_DEFAULT,Sqr($($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),Times(pb_DEFAULT,$($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus(pc,Times(pb,Tan(Plus(a,Times(b,x)))),Times(pa,Sqr(Tan(Plus(a,Times(b,x)))))),Power(Tan(Plus(a,Times(b,x))),CN2)),x),And(FreeQ(List(a,b,pa,pb,pc),x),KnownTangentIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Plus(pa_DEFAULT,Times(pc_DEFAULT,Sqr($($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),Times(pb_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus(pc,Times(pb,Cot(Plus(a,Times(b,x)))),Times(pa,Sqr(Cot(Plus(a,Times(b,x)))))),Power(Cot(Plus(a,Times(b,x))),CN2)),x),And(FreeQ(List(a,b,pa,pb,pc),x),KnownCotangentIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Plus(pa_,Times(pc_DEFAULT,Sqr($($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))))),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus(pc,Times(pa,Sqr(Tan(Plus(a,Times(b,x)))))),Power(Tan(Plus(a,Times(b,x))),CN2)),x),And(FreeQ(List(a,b,pa,pc),x),KnownTangentIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Plus(pa_,Times(pc_DEFAULT,Sqr($($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))))),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus(pc,Times(pa,Sqr(Cot(Plus(a,Times(b,x)))))),Power(Cot(Plus(a,Times(b,x))),CN2)),x),And(FreeQ(List(a,b,pa,pc),x),KnownCotangentIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Plus(pa_DEFAULT,Times(pc_DEFAULT,$($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Times(pb_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus(pc,Times(pa,Tan(Plus(a,Times(b,x)))),Times(pb,Sqr(Tan(Plus(a,Times(b,x)))))),Power(Tan(Plus(a,Times(b,x))),CN1)),x),FreeQ(List(a,b,pa,pb,pc),x))),
ISetDelayed(Int(Times(u_,Plus(Times(pb_DEFAULT,Power($($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),$p("n1"))),Times(pc_DEFAULT,Power($($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),$p("n2"))),Times(pa_DEFAULT,Power($($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),pn_DEFAULT)))),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Power(Tan(Plus(a,Times(b,x))),pn),Plus(pa,Times(pb,Tan(Plus(a,Times(b,x)))),Times(pc,Sqr(Tan(Plus(a,Times(b,x))))))),x),And(And(FreeQ(List(a,b,pa,pb,pc,pn),x),ZeroQ(Plus($s("n1"),Times(CN1,pn),Times(CN1,C1)))),ZeroQ(Plus($s("n2"),Times(CN1,pn),Times(CN1,C2)))))),
ISetDelayed(Int(Times(u_,Plus(Times(pb_DEFAULT,Power($($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),$p("n1"))),Times(pc_DEFAULT,Power($($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),$p("n2"))),Times(pa_DEFAULT,Power($($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),pn_DEFAULT)))),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Power(Cot(Plus(a,Times(b,x))),pn),Plus(pa,Times(pb,Cot(Plus(a,Times(b,x)))),Times(pc,Sqr(Cot(Plus(a,Times(b,x))))))),x),And(And(FreeQ(List(a,b,pa,pb,pc,pn),x),ZeroQ(Plus($s("n1"),Times(CN1,pn),Times(CN1,C1)))),ZeroQ(Plus($s("n2"),Times(CN1,pn),Times(CN1,C2))))))
  );
}
