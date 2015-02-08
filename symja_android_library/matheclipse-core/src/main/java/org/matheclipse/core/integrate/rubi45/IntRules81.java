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
ISetDelayed(Int(Times(u_,Power(Times(c_DEFAULT,$($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_DEFAULT),Power(Times(d_DEFAULT,$($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(c,Sin(Plus(a,Times(b,x)))),m),Power(Times(d,Csc(Plus(a,Times(b,x)))),m),Int(Times(ActivateTrig(u),Power(Times(d,Csc(Plus(a,Times(b,x)))),Plus(n,Negate(m)))),x)),And(FreeQ(List(a,b,c,d,m,n),x),KnownSecantIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Power(Times(c_DEFAULT,$($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_DEFAULT),Power(Times(d_DEFAULT,$($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(c,Cos(Plus(a,Times(b,x)))),m),Power(Times(d,Sec(Plus(a,Times(b,x)))),m),Int(Times(ActivateTrig(u),Power(Times(d,Sec(Plus(a,Times(b,x)))),Plus(n,Negate(m)))),x)),And(FreeQ(List(a,b,c,d,m,n),x),KnownSecantIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Power(Times(c_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_DEFAULT),Power(Times(d_DEFAULT,$($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(c,Tan(Plus(a,Times(b,x)))),m),Power(Times(d,Csc(Plus(a,Times(b,x)))),m),Power(Power(Times(d,Sec(Plus(a,Times(b,x)))),m),-1),Int(Times(ActivateTrig(u),Power(Times(d,Sec(Plus(a,Times(b,x)))),Plus(m,n)),Power(Power(Times(d,Csc(Plus(a,Times(b,x)))),m),-1)),x)),And(And(FreeQ(List(a,b,c,d,m,n),x),KnownSecantIntegrandQ(u,x)),Not(IntegerQ(m))))),
ISetDelayed(Int(Times(u_,Power(Times(c_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_DEFAULT),Power(Times(d_DEFAULT,$($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(c,Tan(Plus(a,Times(b,x)))),m),Power(Times(d,Csc(Plus(a,Times(b,x)))),m),Power(Power(Times(d,Sec(Plus(a,Times(b,x)))),m),-1),Int(Times(ActivateTrig(u),Power(Times(d,Sec(Plus(a,Times(b,x)))),m),Power(Power(Times(d,Csc(Plus(a,Times(b,x)))),Plus(m,Negate(n))),-1)),x)),And(And(FreeQ(List(a,b,c,d,m,n),x),KnownSecantIntegrandQ(u,x)),Not(IntegerQ(m))))),
ISetDelayed(Int(Times(u_,Power(Times(c_DEFAULT,$($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_DEFAULT),Power(Times(d_DEFAULT,$($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(c,Cot(Plus(a,Times(b,x)))),m),Power(Times(d,Sec(Plus(a,Times(b,x)))),m),Power(Power(Times(d,Csc(Plus(a,Times(b,x)))),m),-1),Int(Times(ActivateTrig(u),Power(Times(d,Csc(Plus(a,Times(b,x)))),m),Power(Power(Times(d,Sec(Plus(a,Times(b,x)))),Plus(m,Negate(n))),-1)),x)),And(And(FreeQ(List(a,b,c,d,m,n),x),KnownSecantIntegrandQ(u,x)),Not(IntegerQ(m))))),
ISetDelayed(Int(Times(u_,Power(Times(c_DEFAULT,$($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_DEFAULT),Power(Times(d_DEFAULT,$($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(c,Cot(Plus(a,Times(b,x)))),m),Power(Times(d,Sec(Plus(a,Times(b,x)))),m),Power(Power(Times(d,Csc(Plus(a,Times(b,x)))),m),-1),Int(Times(ActivateTrig(u),Power(Times(d,Csc(Plus(a,Times(b,x)))),Plus(m,n)),Power(Power(Times(d,Sec(Plus(a,Times(b,x)))),m),-1)),x)),And(And(FreeQ(List(a,b,c,d,m,n),x),KnownSecantIntegrandQ(u,x)),Not(IntegerQ(m))))),
ISetDelayed(Int(Times(u_,Power(Times(c_DEFAULT,$($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(c,Csc(Plus(a,Times(b,x)))),m),Power(Times(c,Sin(Plus(a,Times(b,x)))),m),Int(Times(ActivateTrig(u),Power(Power(Times(c,Csc(Plus(a,Times(b,x)))),m),-1)),x)),And(And(FreeQ(List(a,b,c,m),x),Not(IntegerQ(m))),KnownSecantIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Power(Times(c_DEFAULT,$($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(c,Cos(Plus(a,Times(b,x)))),m),Power(Times(c,Sec(Plus(a,Times(b,x)))),m),Int(Times(ActivateTrig(u),Power(Power(Times(c,Sec(Plus(a,Times(b,x)))),m),-1)),x)),And(And(FreeQ(List(a,b,c,m),x),Not(IntegerQ(m))),KnownSecantIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Power(Times(c_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(c,Tan(Plus(a,Times(b,x)))),m),Power(Times(c,Csc(Plus(a,Times(b,x)))),m),Power(Power(Times(c,Sec(Plus(a,Times(b,x)))),m),-1),Int(Times(ActivateTrig(u),Power(Times(c,Sec(Plus(a,Times(b,x)))),m),Power(Power(Times(c,Csc(Plus(a,Times(b,x)))),m),-1)),x)),And(And(FreeQ(List(a,b,c,m),x),Not(IntegerQ(m))),KnownSecantIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Power(Times(c_DEFAULT,$($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(c,Cot(Plus(a,Times(b,x)))),m),Power(Times(c,Sec(Plus(a,Times(b,x)))),m),Power(Power(Times(c,Csc(Plus(a,Times(b,x)))),m),-1),Int(Times(ActivateTrig(u),Power(Times(c,Csc(Plus(a,Times(b,x)))),m),Power(Power(Times(c,Sec(Plus(a,Times(b,x)))),m),-1)),x)),And(And(FreeQ(List(a,b,c,m),x),Not(IntegerQ(m))),KnownSecantIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Power(Times(c_DEFAULT,$($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT),Plus($p("A"),Times($p("B",true),$($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Times(c,Int(Times(ActivateTrig(u),Power(Times(c,Sec(Plus(a,Times(b,x)))),Plus(n,Negate(C1))),Plus($s("B"),Times($s("A"),Sec(Plus(a,Times(b,x)))))),x)),And(FreeQ(List(a,b,c,$s("A"),$s("B"),n),x),KnownSecantIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Power(Times(c_DEFAULT,$($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT),Plus($p("A"),Times($p("B",true),$($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Times(c,Int(Times(ActivateTrig(u),Power(Times(c,Csc(Plus(a,Times(b,x)))),Plus(n,Negate(C1))),Plus($s("B"),Times($s("A"),Csc(Plus(a,Times(b,x)))))),x)),And(FreeQ(List(a,b,c,$s("A"),$s("B"),n),x),KnownSecantIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Plus($p("A"),Times($p("B",true),$($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus($s("B"),Times($s("A"),Sec(Plus(a,Times(b,x))))),Power(Sec(Plus(a,Times(b,x))),-1)),x),And(FreeQ(List(a,b,$s("A"),$s("B")),x),KnownSecantIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Plus($p("A"),Times($p("B",true),$($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus($s("B"),Times($s("A"),Csc(Plus(a,Times(b,x))))),Power(Csc(Plus(a,Times(b,x))),-1)),x),And(FreeQ(List(a,b,$s("A"),$s("B")),x),KnownSecantIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_DEFAULT,Plus($p("A",true),Times($p("B",true),$($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Times($p("C",true),Sqr($($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),Power(Times(c_DEFAULT,$($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Times(Sqr(c),Int(Times(ActivateTrig(u),Power(Times(c,Sec(Plus(a,Times(b,x)))),Plus(n,Negate(C2))),Plus($s("C"),Times($s("B"),Sec(Plus(a,Times(b,x)))),Times($s("A"),Sqr(Sec(Plus(a,Times(b,x))))))),x)),And(FreeQ(List(a,b,c,$s("A"),$s("B"),$s("C"),n),x),KnownSecantIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_DEFAULT,Plus($p("A",true),Times($p("B",true),$($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Times($p("C",true),Sqr($($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),Power(Times(c_DEFAULT,$($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Times(Sqr(c),Int(Times(ActivateTrig(u),Power(Times(c,Csc(Plus(a,Times(b,x)))),Plus(n,Negate(C2))),Plus($s("C"),Times($s("B"),Csc(Plus(a,Times(b,x)))),Times($s("A"),Sqr(Csc(Plus(a,Times(b,x))))))),x)),And(FreeQ(List(a,b,c,$s("A"),$s("B"),$s("C"),n),x),KnownSecantIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_DEFAULT,Power(Times(c_DEFAULT,$($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT),Plus($p("A"),Times($p("C",true),Sqr($($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))))),x_Symbol),
    Condition(Times(Sqr(c),Int(Times(ActivateTrig(u),Power(Times(c,Sec(Plus(a,Times(b,x)))),Plus(n,Negate(C2))),Plus($s("C"),Times($s("A"),Sqr(Sec(Plus(a,Times(b,x))))))),x)),And(FreeQ(List(a,b,c,$s("A"),$s("C"),n),x),KnownSecantIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_DEFAULT,Power(Times(c_DEFAULT,$($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT),Plus($p("A"),Times($p("C",true),Sqr($($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))))),x_Symbol),
    Condition(Times(Sqr(c),Int(Times(ActivateTrig(u),Power(Times(c,Csc(Plus(a,Times(b,x)))),Plus(n,Negate(C2))),Plus($s("C"),Times($s("A"),Sqr(Csc(Plus(a,Times(b,x))))))),x)),And(FreeQ(List(a,b,c,$s("A"),$s("C"),n),x),KnownSecantIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Plus($p("A",true),Times($p("B",true),$($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Times($p("C",true),Sqr($($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))))),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus($s("C"),Times($s("B"),Sec(Plus(a,Times(b,x)))),Times($s("A"),Sqr(Sec(Plus(a,Times(b,x)))))),Power(Sec(Plus(a,Times(b,x))),-2)),x),And(FreeQ(List(a,b,$s("A"),$s("B"),$s("C")),x),KnownSecantIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Plus($p("A",true),Times($p("B",true),$($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Times($p("C",true),Sqr($($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))))),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus($s("C"),Times($s("B"),Csc(Plus(a,Times(b,x)))),Times($s("A"),Sqr(Csc(Plus(a,Times(b,x)))))),Power(Csc(Plus(a,Times(b,x))),-2)),x),And(FreeQ(List(a,b,$s("A"),$s("B"),$s("C")),x),KnownSecantIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Plus($p("A"),Times($p("C",true),Sqr($($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))))),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus($s("C"),Times($s("A"),Sqr(Sec(Plus(a,Times(b,x)))))),Power(Sec(Plus(a,Times(b,x))),-2)),x),And(FreeQ(List(a,b,$s("A"),$s("C")),x),KnownSecantIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Plus($p("A"),Times($p("C",true),Sqr($($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))))),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus($s("C"),Times($s("A"),Sqr(Csc(Plus(a,Times(b,x)))))),Power(Csc(Plus(a,Times(b,x))),-2)),x),And(FreeQ(List(a,b,$s("A"),$s("C")),x),KnownSecantIntegrandQ(u,x)))),
ISetDelayed(Int(Times(u_,Plus(Times($p("A",true),Power($($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),Times($p("B",true),Power($($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),$p("n1"))),Times($p("C",true),Power($($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),$p("n2"))))),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Power(Sec(Plus(a,Times(b,x))),n),Plus($s("A"),Times($s("B"),Sec(Plus(a,Times(b,x)))),Times($s("C"),Sqr(Sec(Plus(a,Times(b,x))))))),x),And(And(FreeQ(List(a,b,$s("A"),$s("B"),$s("C"),n),x),ZeroQ(Plus($s("n1"),Negate(n),Negate(C1)))),ZeroQ(Plus($s("n2"),Negate(n),Negate(C2)))))),
ISetDelayed(Int(Times(u_,Plus(Times($p("A",true),Power($($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),Times($p("B",true),Power($($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),$p("n1"))),Times($p("C",true),Power($($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),$p("n2"))))),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Power(Csc(Plus(a,Times(b,x))),n),Plus($s("A"),Times($s("B"),Csc(Plus(a,Times(b,x)))),Times($s("C"),Sqr(Csc(Plus(a,Times(b,x))))))),x),And(And(FreeQ(List(a,b,$s("A"),$s("B"),$s("C"),n),x),ZeroQ(Plus($s("n1"),Negate(n),Negate(C1)))),ZeroQ(Plus($s("n2"),Negate(n),Negate(C2))))))
  );
}
