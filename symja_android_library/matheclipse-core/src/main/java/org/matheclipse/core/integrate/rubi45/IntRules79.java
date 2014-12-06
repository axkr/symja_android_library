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
public class IntRules79 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Sqr($($s("§sec"),Plus(c_DEFAULT,Times($p(d,true),x_)))))),p_),x_Symbol),
    Condition(Int(Power(Times(CN1,a,Sqr(Tan(Plus(c,Times(d,x))))),p),x),And(FreeQ(List(a,b,c,d,p),x),ZeroQ(Plus(a,b))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Sqr($($s("§csc"),Plus(c_DEFAULT,Times($p(d,true),x_)))))),p_),x_Symbol),
    Condition(Int(Power(Times(CN1,a,Sqr(Cot(Plus(c,Times(d,x))))),p),x),And(FreeQ(List(a,b,c,d,p),x),ZeroQ(Plus(a,b))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Sqr($($s("§sec"),Plus(c_DEFAULT,Times($p(d,true),x_)))))),CN1),x_Symbol),
    Condition(Plus(Times(x,Power(a,CN1)),Times(CN1,b,Power(a,CN1),Int(Power(Plus(b,Times(a,Sqr(Cos(Plus(c,Times(d,x)))))),CN1),x))),And(FreeQ(List(a,b,c,d),x),NonzeroQ(Plus(a,b))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Sqr($($s("§csc"),Plus(c_DEFAULT,Times($p(d,true),x_)))))),CN1),x_Symbol),
    Condition(Plus(Times(x,Power(a,CN1)),Times(CN1,b,Power(a,CN1),Int(Power(Plus(b,Times(a,Sqr(Sin(Plus(c,Times(d,x)))))),CN1),x))),And(FreeQ(List(a,b,c,d),x),NonzeroQ(Plus(a,b))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Sqr($($s("§sec"),Plus(c_DEFAULT,Times($p(d,true),x_)))))),p_),x_Symbol),
    Condition(Times(Power(d,CN1),Subst(Int(Times(Power(Plus(a,b,Times(b,Sqr(x))),p),Power(Plus(C1,Sqr(x)),CN1)),x),x,Tan(Plus(c,Times(d,x))))),And(And(FreeQ(List(a,b,c,d,p),x),NonzeroQ(Plus(a,b))),NonzeroQ(Plus(p,C1))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Sqr($($s("§csc"),Plus(c_DEFAULT,Times($p(d,true),x_)))))),p_),x_Symbol),
    Condition(Times(CN1,Power(d,CN1),Subst(Int(Times(Power(Plus(a,b,Times(b,Sqr(x))),p),Power(Plus(C1,Sqr(x)),CN1)),x),x,Cot(Plus(c,Times(d,x))))),And(And(FreeQ(List(a,b,c,d,p),x),NonzeroQ(Plus(a,b))),NonzeroQ(Plus(p,C1))))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power($($s("§sec"),Plus(c_DEFAULT,Times($p(d,true),x_))),n_))),p_),Power($($s("§sin"),Plus(c_DEFAULT,Times($p(d,true),x_))),m_)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Tan(Plus(c,Times(d,x))),x))),Times(Power(f,Plus(m,C1)),Power(d,CN1),Subst(Int(Times(Power(x,m),Power(ExpandToSum(Plus(a,Times(b,Power(Plus(C1,Times(Sqr(f),Sqr(x))),Times(C1D2,n)))),x),p),Power(Power(Plus(C1,Times(Sqr(f),Sqr(x))),Plus(Times(C1D2,m),C1)),CN1)),x),x,Times(Tan(Plus(c,Times(d,x))),Power(f,CN1))))),And(And(FreeQ(List(a,b,c,d,p),x),EvenQ(m)),EvenQ(n)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power($($s("§csc"),Plus(c_DEFAULT,Times($p(d,true),x_))),n_))),p_),Power($($s("§cos"),Plus(c_DEFAULT,Times($p(d,true),x_))),m_)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Cot(Plus(c,Times(d,x))),x))),Times(CN1,Power(f,Plus(m,C1)),Power(d,CN1),Subst(Int(Times(Power(x,m),Power(ExpandToSum(Plus(a,Times(b,Power(Plus(C1,Times(Sqr(f),Sqr(x))),Times(C1D2,n)))),x),p),Power(Power(Plus(C1,Times(Sqr(f),Sqr(x))),Plus(Times(C1D2,m),C1)),CN1)),x),x,Times(Cot(Plus(c,Times(d,x))),Power(f,CN1))))),And(And(FreeQ(List(a,b,c,d,p),x),EvenQ(m)),EvenQ(n)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power($($s("§sec"),Plus(c_DEFAULT,Times($p(d,true),x_))),n_))),p_DEFAULT),Power($($s("§sin"),Plus(c_DEFAULT,Times($p(d,true),x_))),m_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Cos(Plus(c,Times(d,x))),x))),Times(CN1,f,Power(d,CN1),Subst(Int(Times(Power(Plus(C1,Times(CN1,Sqr(f),Sqr(x))),Times(C1D2,Plus(m,Times(CN1,C1)))),Power(Plus(b,Times(a,Power(Times(f,x),n))),p),Power(Power(Times(f,x),Times(n,p)),CN1)),x),x,Times(Cos(Plus(c,Times(d,x))),Power(f,CN1))))),And(And(FreeQ(List(a,b,c,d),x),OddQ(m)),IntegersQ(n,p)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power($($s("§csc"),Plus(c_DEFAULT,Times($p(d,true),x_))),n_))),p_DEFAULT),Power($($s("§cos"),Plus(c_DEFAULT,Times($p(d,true),x_))),m_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Sin(Plus(c,Times(d,x))),x))),Times(f,Power(d,CN1),Subst(Int(Times(Power(Plus(C1,Times(CN1,Sqr(f),Sqr(x))),Times(C1D2,Plus(m,Times(CN1,C1)))),Power(Plus(b,Times(a,Power(Times(f,x),n))),p),Power(Power(Times(f,x),Times(n,p)),CN1)),x),x,Times(Sin(Plus(c,Times(d,x))),Power(f,CN1))))),And(And(FreeQ(List(a,b,c,d),x),OddQ(m)),IntegersQ(n,p)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power($($s("§sec"),Plus(c_DEFAULT,Times($p(d,true),x_))),n_))),p_),Power($($s("§sec"),Plus(c_DEFAULT,Times($p(d,true),x_))),m_)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Tan(Plus(c,Times(d,x))),x))),Times(f,Power(d,CN1),Subst(Int(Times(Power(Plus(C1,Times(Sqr(f),Sqr(x))),Plus(Times(C1D2,m),Times(CN1,C1))),Power(ExpandToSum(Plus(a,Times(b,Power(Plus(C1,Times(Sqr(f),Sqr(x))),Times(C1D2,n)))),x),p)),x),x,Times(Tan(Plus(c,Times(d,x))),Power(f,CN1))))),And(And(FreeQ(List(a,b,c,d,p),x),EvenQ(m)),EvenQ(n)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power($($s("§csc"),Plus(c_DEFAULT,Times($p(d,true),x_))),n_))),p_),Power($($s("§csc"),Plus(c_DEFAULT,Times($p(d,true),x_))),m_)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Cot(Plus(c,Times(d,x))),x))),Times(CN1,f,Power(d,CN1),Subst(Int(Times(Power(Plus(C1,Times(Sqr(f),Sqr(x))),Plus(Times(C1D2,m),Times(CN1,C1))),Power(ExpandToSum(Plus(a,Times(b,Power(Plus(C1,Times(Sqr(f),Sqr(x))),Times(C1D2,n)))),x),p)),x),x,Times(Cot(Plus(c,Times(d,x))),Power(f,CN1))))),And(And(FreeQ(List(a,b,c,d,p),x),EvenQ(m)),EvenQ(n)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power($($s("§sec"),Plus(c_DEFAULT,Times($p(d,true),x_))),n_))),p_),Power($($s("§sec"),Plus(c_DEFAULT,Times($p(d,true),x_))),m_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Sin(Plus(c,Times(d,x))),x))),Times(f,Power(d,CN1),Subst(Int(Times(Power(ExpandToSum(Plus(b,Times(a,Power(Plus(C1,Times(CN1,Sqr(f),Sqr(x))),Times(C1D2,n)))),x),p),Power(Power(Plus(C1,Times(CN1,Sqr(f),Sqr(x))),Times(C1D2,Plus(m,Times(n,p),C1))),CN1)),x),x,Times(Sin(Plus(c,Times(d,x))),Power(f,CN1))))),And(And(And(FreeQ(List(a,b,c,d),x),OddQ(m)),EvenQ(n)),IntegerQ(p)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power($($s("§csc"),Plus(c_DEFAULT,Times($p(d,true),x_))),n_))),p_),Power($($s("§csc"),Plus(c_DEFAULT,Times($p(d,true),x_))),m_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Cos(Plus(c,Times(d,x))),x))),Times(CN1,f,Power(d,CN1),Subst(Int(Times(Power(ExpandToSum(Plus(b,Times(a,Power(Plus(C1,Times(CN1,Sqr(f),Sqr(x))),Times(C1D2,n)))),x),p),Power(Power(Plus(C1,Times(CN1,Sqr(f),Sqr(x))),Times(C1D2,Plus(m,Times(n,p),C1))),CN1)),x),x,Times(Cos(Plus(c,Times(d,x))),Power(f,CN1))))),And(And(And(FreeQ(List(a,b,c,d),x),OddQ(m)),EvenQ(n)),IntegerQ(p)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power($($s("§sec"),Plus(c_DEFAULT,Times($p(d,true),x_))),n_))),p_),Power($($s("§sec"),Plus(c_DEFAULT,Times($p(d,true),x_))),m_DEFAULT)),x_Symbol),
    Condition(Int(ExpandTrig(Times(Power($($s("§sec"),Plus(c,Times(d,x))),m),Power(Plus(a,Times(b,Power($($s("§sec"),Plus(c,Times(d,x))),n))),p)),x),x),And(FreeQ(List(a,b,c,d),x),IntegersQ(m,n,p)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power($($s("§csc"),Plus(c_DEFAULT,Times($p(d,true),x_))),n_))),p_),Power($($s("§csc"),Plus(c_DEFAULT,Times($p(d,true),x_))),m_DEFAULT)),x_Symbol),
    Condition(Int(ExpandTrig(Times(Power($($s("§csc"),Plus(c,Times(d,x))),m),Power(Plus(a,Times(b,Power($($s("§csc"),Plus(c,Times(d,x))),n))),p)),x),x),And(FreeQ(List(a,b,c,d),x),IntegersQ(m,n,p)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power($($s("§sec"),Plus(c_DEFAULT,Times($p(d,true),x_))),n_))),p_DEFAULT),Power($($s("§tan"),Plus(c_DEFAULT,Times($p(d,true),x_))),m_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Cos(Plus(c,Times(d,x))),x))),Times(CN1,Power(Times(d,Power(f,Plus(m,Times(n,p),Times(CN1,C1)))),CN1),Subst(Int(Times(Power(Plus(C1,Times(CN1,Sqr(f),Sqr(x))),Times(C1D2,Plus(m,Times(CN1,C1)))),Power(Plus(b,Times(a,Power(Times(f,x),n))),p),Power(Power(x,Plus(m,Times(n,p))),CN1)),x),x,Times(Cos(Plus(c,Times(d,x))),Power(f,CN1))))),And(And(And(FreeQ(List(a,b,c,d,n),x),OddQ(m)),IntegerQ(n)),IntegerQ(p)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power($($s("§csc"),Plus(c_DEFAULT,Times($p(d,true),x_))),n_))),p_DEFAULT),Power($($s("§cot"),Plus(c_DEFAULT,Times($p(d,true),x_))),m_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Sin(Plus(c,Times(d,x))),x))),Times(Power(Times(d,Power(f,Plus(m,Times(n,p),Times(CN1,C1)))),CN1),Subst(Int(Times(Power(Plus(C1,Times(CN1,Sqr(f),Sqr(x))),Times(C1D2,Plus(m,Times(CN1,C1)))),Power(Plus(b,Times(a,Power(Times(f,x),n))),p),Power(Power(x,Plus(m,Times(n,p))),CN1)),x),x,Times(Sin(Plus(c,Times(d,x))),Power(f,CN1))))),And(And(And(FreeQ(List(a,b,c,d,n),x),OddQ(m)),IntegerQ(n)),IntegerQ(p)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power($($s("§sec"),Plus(c_DEFAULT,Times($p(d,true),x_))),n_))),p_DEFAULT),Power($($s("§tan"),Plus(c_DEFAULT,Times($p(d,true),x_))),m_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Tan(Plus(c,Times(d,x))),x))),Times(Power(f,Plus(m,C1)),Power(d,CN1),Subst(Int(Times(Power(x,m),Power(ExpandToSum(Plus(a,Times(b,Power(Plus(C1,Times(Sqr(f),Sqr(x))),Times(C1D2,n)))),x),p),Power(Plus(C1,Times(Sqr(f),Sqr(x))),CN1)),x),x,Times(Tan(Plus(c,Times(d,x))),Power(f,CN1))))),And(And(FreeQ(List(a,b,c,d),x),EvenQ(m)),EvenQ(n)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power($($s("§csc"),Plus(c_DEFAULT,Times($p(d,true),x_))),n_))),p_DEFAULT),Power($($s("§cot"),Plus(c_DEFAULT,Times($p(d,true),x_))),m_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Cot(Plus(c,Times(d,x))),x))),Times(CN1,Power(f,Plus(m,C1)),Power(d,CN1),Subst(Int(Times(Power(x,m),Power(ExpandToSum(Plus(a,Times(b,Power(Plus(C1,Times(Sqr(f),Sqr(x))),Times(C1D2,n)))),x),p),Power(Plus(C1,Times(Sqr(f),Sqr(x))),CN1)),x),x,Times(Cot(Plus(c,Times(d,x))),Power(f,CN1))))),And(And(FreeQ(List(a,b,c,d),x),EvenQ(m)),EvenQ(n))))
  );
}
