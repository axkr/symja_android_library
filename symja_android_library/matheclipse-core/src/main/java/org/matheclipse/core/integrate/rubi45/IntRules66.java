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
ISetDelayed(Int(Times(u_,Power(Times(c_DEFAULT,$($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_DEFAULT),Power(Times(d_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(c,Cot(Plus(a,Times(b,x)))),m),Power(Times(d,Tan(Plus(a,Times(b,x)))),m),Int(Times(ActivateTrig(u),Power(Times(d,Tan(Plus(a,Times(b,x)))),Plus(n,Times(CN1,m)))),x)),And(FreeQ(List(a,b,c,d,m,n),x),KnownTangentIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Power(Times(c_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_DEFAULT),Power(Times(d_DEFAULT,$($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(c,Tan(Plus(a,Times(b,x)))),m),Power(Times(d,Cos(Plus(a,Times(b,x)))),m),Power(Power(Times(d,Sin(Plus(a,Times(b,x)))),m),CN1),Int(Times(ActivateTrig(u),Power(Times(d,Sin(Plus(a,Times(b,x)))),m),Power(Power(Times(d,Cos(Plus(a,Times(b,x)))),Plus(m,Times(CN1,n))),CN1)),x)),And(FreeQ(List(a,b,c,d,m,n),x),KnownCotangentIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Power(Times(c_DEFAULT,$($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(c,Cot(Plus(a,Times(b,x)))),m),Power(Times(c,Tan(Plus(a,Times(b,x)))),m),Int(Times(ActivateTrig(u),Power(Power(Times(c,Tan(Plus(a,Times(b,x)))),m),CN1)),x)),And(And(FreeQ(List(a,b,c,m),x),Not(IntegerQ(m))),KnownTangentIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Power(Times(c_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(c,Cot(Plus(a,Times(b,x)))),m),Power(Times(c,Tan(Plus(a,Times(b,x)))),m),Int(Times(ActivateTrig(u),Power(Power(Times(c,Cot(Plus(a,Times(b,x)))),m),CN1)),x)),And(And(FreeQ(List(a,b,c,m),x),Not(IntegerQ(m))),KnownCotangentIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Power(Times(c_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT),Plus($p("A"),Times($p("B",true),$($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Times(c,Int(Times(ActivateTrig(u),Power(Times(c,Tan(Plus(a,Times(b,x)))),Plus(n,Times(CN1,C1))),Plus($s("B"),Times($s("A"),Tan(Plus(a,Times(b,x)))))),x)),And(FreeQ(List(a,b,c,$s("A"),$s("B"),n),x),KnownTangentIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Power(Times(c_DEFAULT,$($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT),Plus($p("A"),Times($p("B",true),$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Times(c,Int(Times(ActivateTrig(u),Power(Times(c,Cot(Plus(a,Times(b,x)))),Plus(n,Times(CN1,C1))),Plus($s("B"),Times($s("A"),Cot(Plus(a,Times(b,x)))))),x)),And(FreeQ(List(a,b,c,$s("A"),$s("B"),n),x),KnownCotangentIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Plus($p("A"),Times($p("B",true),$($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus($s("B"),Times($s("A"),Tan(Plus(a,Times(b,x))))),Power(Tan(Plus(a,Times(b,x))),CN1)),x),And(FreeQ(List(a,b,$s("A"),$s("B")),x),KnownTangentIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Plus($p("A"),Times($p("B",true),$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus($s("B"),Times($s("A"),Cot(Plus(a,Times(b,x))))),Power(Cot(Plus(a,Times(b,x))),CN1)),x),And(FreeQ(List(a,b,$s("A"),$s("B")),x),KnownCotangentIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_DEFAULT,Plus($p("A",true),Times($p("B",true),$($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Times($p("C",true),Sqr($($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),Power(Times(c_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Times(Sqr(c),Int(Times(ActivateTrig(u),Power(Times(c,Tan(Plus(a,Times(b,x)))),Plus(n,Times(CN1,C2))),Plus($s("C"),Times($s("B"),Tan(Plus(a,Times(b,x)))),Times($s("A"),Sqr(Tan(Plus(a,Times(b,x))))))),x)),And(FreeQ(List(a,b,c,$s("A"),$s("B"),$s("C"),n),x),KnownTangentIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_DEFAULT,Plus($p("A",true),Times($p("B",true),$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Times($p("C",true),Sqr($($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),Power(Times(c_DEFAULT,$($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Times(Sqr(c),Int(Times(ActivateTrig(u),Power(Times(c,Cot(Plus(a,Times(b,x)))),Plus(n,Times(CN1,C2))),Plus($s("C"),Times($s("B"),Cot(Plus(a,Times(b,x)))),Times($s("A"),Sqr(Cot(Plus(a,Times(b,x))))))),x)),And(FreeQ(List(a,b,c,$s("A"),$s("B"),$s("C"),n),x),KnownCotangentIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_DEFAULT,Power(Times(c_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT),Plus($p("A"),Times($p("C",true),Sqr($($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))))),x_Symbol),
    Condition(Times(Sqr(c),Int(Times(ActivateTrig(u),Power(Times(c,Tan(Plus(a,Times(b,x)))),Plus(n,Times(CN1,C2))),Plus($s("C"),Times($s("A"),Sqr(Tan(Plus(a,Times(b,x))))))),x)),And(FreeQ(List(a,b,c,$s("A"),$s("C"),n),x),KnownTangentIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_DEFAULT,Power(Times(c_DEFAULT,$($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT),Plus($p("A"),Times($p("C",true),Sqr($($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))))),x_Symbol),
    Condition(Times(Sqr(c),Int(Times(ActivateTrig(u),Power(Times(c,Cot(Plus(a,Times(b,x)))),Plus(n,Times(CN1,C2))),Plus($s("C"),Times($s("A"),Sqr(Cot(Plus(a,Times(b,x))))))),x)),And(FreeQ(List(a,b,c,$s("A"),$s("C"),n),x),KnownCotangentIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Plus($p("A",true),Times($p("B",true),$($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Times($p("C",true),Sqr($($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))))),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus($s("C"),Times($s("B"),Tan(Plus(a,Times(b,x)))),Times($s("A"),Sqr(Tan(Plus(a,Times(b,x)))))),Power(Tan(Plus(a,Times(b,x))),CN2)),x),And(FreeQ(List(a,b,$s("A"),$s("B"),$s("C")),x),KnownTangentIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Plus($p("A",true),Times($p("B",true),$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Times($p("C",true),Sqr($($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))))),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus($s("C"),Times($s("B"),Cot(Plus(a,Times(b,x)))),Times($s("A"),Sqr(Cot(Plus(a,Times(b,x)))))),Power(Cot(Plus(a,Times(b,x))),CN2)),x),And(FreeQ(List(a,b,$s("A"),$s("B"),$s("C")),x),KnownCotangentIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Plus($p("A"),Times($p("C",true),Sqr($($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))))),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus($s("C"),Times($s("A"),Sqr(Tan(Plus(a,Times(b,x)))))),Power(Tan(Plus(a,Times(b,x))),CN2)),x),And(FreeQ(List(a,b,$s("A"),$s("C")),x),KnownTangentIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Plus($p("A"),Times($p("C",true),Sqr($($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))))),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus($s("C"),Times($s("A"),Sqr(Cot(Plus(a,Times(b,x)))))),Power(Cot(Plus(a,Times(b,x))),CN2)),x),And(FreeQ(List(a,b,$s("A"),$s("C")),x),KnownCotangentIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Plus($p("A",true),Times($p("B",true),$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Times($p("C",true),$($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus($s("C"),Times($s("A"),Tan(Plus(a,Times(b,x)))),Times($s("B"),Sqr(Tan(Plus(a,Times(b,x)))))),Power(Tan(Plus(a,Times(b,x))),CN1)),x),FreeQ(List(a,b,$s("A"),$s("B"),$s("C")),x))),
ISetDelayed(Int(Times(u_,Plus(Times($p("A",true),Power($($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),Times($p("B",true),Power($($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),$p("n1"))),Times($p("C",true),Power($($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),$p("n2"))))),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Power(Tan(Plus(a,Times(b,x))),n),Plus($s("A"),Times($s("B"),Tan(Plus(a,Times(b,x)))),Times($s("C"),Sqr(Tan(Plus(a,Times(b,x))))))),x),And(And(FreeQ(List(a,b,$s("A"),$s("B"),$s("C"),n),x),ZeroQ(Plus($s("n1"),Times(CN1,n),Times(CN1,C1)))),ZeroQ(Plus($s("n2"),Times(CN1,n),Times(CN1,C2)))))),
ISetDelayed(Int(Times(u_,Plus(Times($p("A",true),Power($($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),Times($p("B",true),Power($($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),$p("n1"))),Times($p("C",true),Power($($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),$p("n2"))))),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Power(Cot(Plus(a,Times(b,x))),n),Plus($s("A"),Times($s("B"),Cot(Plus(a,Times(b,x)))),Times($s("C"),Sqr(Cot(Plus(a,Times(b,x))))))),x),And(And(FreeQ(List(a,b,$s("A"),$s("B"),$s("C"),n),x),ZeroQ(Plus($s("n1"),Times(CN1,n),Times(CN1,C1)))),ZeroQ(Plus($s("n2"),Times(CN1,n),Times(CN1,C2))))))
  );
}
