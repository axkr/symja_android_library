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
public class IntRules64 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Sqr($($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),-1),x_Symbol),
    Condition(Times(Power(a,-1),Int(Sqr(Cos(Plus(c,Times(d,x)))),x)),And(FreeQ(List(a,b,c,d),x),ZeroQ(Plus(a,Negate(b)))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Sqr($($s("§cot"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),-1),x_Symbol),
    Condition(Times(Power(a,-1),Int(Sqr(Sin(Plus(c,Times(d,x)))),x)),And(FreeQ(List(a,b,c,d),x),ZeroQ(Plus(a,Negate(b)))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Sqr($($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),-1),x_Symbol),
    Condition(Plus(Times(x,Power(Plus(a,Negate(b)),-1)),Times(CN1,b,Power(Plus(a,Negate(b)),-1),Int(Times(Sqr(Sec(Plus(c,Times(d,x)))),Power(Plus(a,Times(b,Sqr(Tan(Plus(c,Times(d,x)))))),-1)),x))),And(FreeQ(List(a,b,c,d),x),NonzeroQ(Plus(a,Negate(b)))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Sqr($($s("§cot"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),-1),x_Symbol),
    Condition(Plus(Times(x,Power(Plus(a,Negate(b)),-1)),Times(CN1,b,Power(Plus(a,Negate(b)),-1),Int(Times(Sqr(Csc(Plus(c,Times(d,x)))),Power(Plus(a,Times(b,Sqr(Cot(Plus(c,Times(d,x)))))),-1)),x))),And(FreeQ(List(a,b,c,d),x),NonzeroQ(Plus(a,Negate(b)))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Sqr($($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),p_),x_Symbol),
    Condition(Module(List(Set(e,FreeFactors(Tan(Plus(c,Times(d,x))),x))),Times(e,Power(d,-1),Subst(Int(Times(Power(Plus(a,Times(b,Sqr(e),Sqr(x))),p),Power(Plus(C1,Times(Sqr(e),Sqr(x))),-1)),x),x,Times(Tan(Plus(c,Times(d,x))),Power(e,-1))))),And(FreeQ(List(a,b,c,d,p),x),NonzeroQ(Plus(p,C1))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Sqr($($s("§cot"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),p_),x_Symbol),
    Condition(Module(List(Set(e,FreeFactors(Cot(Plus(c,Times(d,x))),x))),Times(CN1,e,Power(d,-1),Subst(Int(Times(Power(Plus(a,Times(b,Sqr(e),Sqr(x))),p),Power(Plus(C1,Times(Sqr(e),Sqr(x))),-1)),x),x,Times(Cot(Plus(c,Times(d,x))),Power(e,-1))))),And(FreeQ(List(a,b,c,d,p),x),NonzeroQ(Plus(p,C1))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Power($($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),n_))),p_),x_Symbol),
    Condition(Times(Power(d,-1),Subst(Int(Times(Power(Plus(a,Times(b,Power(x,n))),p),Power(Plus(C1,Sqr(x)),-1)),x),x,Tan(Plus(c,Times(d,x))))),FreeQ(List(a,b,c,d,n,p),x))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Power($($s("§cot"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),n_))),p_),x_Symbol),
    Condition(Times(CN1,Power(d,-1),Subst(Int(Times(Power(Plus(a,Times(b,Power(x,n))),p),Power(Plus(C1,Sqr(x)),-1)),x),x,Cot(Plus(c,Times(d,x))))),FreeQ(List(a,b,c,d,n,p),x))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power(Times(e_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),n_))),p_),Power($($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),m_)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Tan(Plus(c,Times(d,x))),x))),Times(Power(f,Plus(m,C1)),Power(d,-1),Subst(Int(Times(Power(x,m),Power(Plus(a,Times(b,Power(Times(e,f,x),n))),p),Power(Power(Plus(C1,Times(Sqr(f),Sqr(x))),Plus(Times(C1D2,m),C1)),-1)),x),x,Times(Tan(Plus(c,Times(d,x))),Power(f,-1))))),And(FreeQ(List(a,b,c,d,e,n,p),x),IntegerQ(Times(C1D2,m))))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power(Times(e_DEFAULT,$($s("§cot"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),n_))),p_),Power($($s("§cos"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),m_)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Cot(Plus(c,Times(d,x))),x))),Times(CN1,Power(f,Plus(m,C1)),Power(d,-1),Subst(Int(Times(Power(x,m),Power(Plus(a,Times(b,Power(Times(e,f,x),n))),p),Power(Power(Plus(C1,Times(Sqr(f),Sqr(x))),Plus(Times(C1D2,m),C1)),-1)),x),x,Times(Cot(Plus(c,Times(d,x))),Power(f,-1))))),And(FreeQ(List(a,b,c,d,e,n,p),x),IntegerQ(Times(C1D2,m))))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power($($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),n_))),p_DEFAULT),Power($($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),m_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Cos(Plus(c,Times(d,x))),x))),Times(CN1,f,Power(d,-1),Subst(Int(Times(Power(Plus(C1,Times(CN1,Sqr(f),Sqr(x))),Times(C1D2,Plus(m,Negate(C1)))),Power(ExpandToSum(Plus(Times(a,Power(Times(f,x),n)),Times(b,Power(Plus(C1,Times(CN1,Sqr(f),Sqr(x))),Times(C1D2,n)))),x),p),Power(Power(Times(f,x),Times(n,p)),-1)),x),x,Times(Cos(Plus(c,Times(d,x))),Power(f,-1))))),And(And(And(FreeQ(List(a,b,c,d),x),OddQ(m)),EvenQ(n)),IntegerQ(p)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power($($s("§cot"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),n_))),p_DEFAULT),Power($($s("§cos"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),m_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Sin(Plus(c,Times(d,x))),x))),Times(f,Power(d,-1),Subst(Int(Times(Power(Plus(C1,Times(CN1,Sqr(f),Sqr(x))),Times(C1D2,Plus(m,Negate(C1)))),Power(ExpandToSum(Plus(Times(a,Power(Times(f,x),n)),Times(b,Power(Plus(C1,Times(CN1,Sqr(f),Sqr(x))),Times(C1D2,n)))),x),p),Power(Power(Times(f,x),Times(n,p)),-1)),x),x,Times(Sin(Plus(c,Times(d,x))),Power(f,-1))))),And(And(And(FreeQ(List(a,b,c,d),x),OddQ(m)),EvenQ(n)),IntegerQ(p)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power(Times(e_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),n_))),p_DEFAULT),Power($($s("§cos"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),m_)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Tan(Plus(c,Times(d,x))),x))),Times(f,Power(d,-1),Subst(Int(Times(Power(Plus(a,Times(b,Power(Times(e,f,x),n))),p),Power(Power(Plus(C1,Times(Sqr(f),Sqr(x))),Plus(Times(C1D2,m),C1)),-1)),x),x,Times(Tan(Plus(c,Times(d,x))),Power(f,-1))))),And(FreeQ(List(a,b,c,d,e,n,p),x),EvenQ(m)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power(Times(e_DEFAULT,$($s("§cot"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),n_))),p_DEFAULT),Power($($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),m_)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Cot(Plus(c,Times(d,x))),x))),Times(CN1,f,Power(d,-1),Subst(Int(Times(Power(Plus(a,Times(b,Power(Times(e,f,x),n))),p),Power(Power(Plus(C1,Times(Sqr(f),Sqr(x))),Plus(Times(C1D2,m),C1)),-1)),x),x,Times(Cot(Plus(c,Times(d,x))),Power(f,-1))))),And(FreeQ(List(a,b,c,d,e,n,p),x),EvenQ(m)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power($($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),n_))),p_DEFAULT),Power($($s("§cos"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),m_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Sin(Plus(c,Times(d,x))),x))),Times(f,Power(d,-1),Subst(Int(Times(Power(Plus(C1,Times(CN1,Sqr(f),Sqr(x))),Times(C1D2,Plus(m,Times(CN1,n,p),Negate(C1)))),Power(ExpandToSum(Plus(Times(b,Power(Times(f,x),n)),Times(a,Power(Plus(C1,Times(CN1,Sqr(f),Sqr(x))),Times(C1D2,n)))),x),p)),x),x,Times(Sin(Plus(c,Times(d,x))),Power(f,-1))))),And(And(And(FreeQ(List(a,b,c,d),x),OddQ(m)),EvenQ(n)),IntegerQ(p)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power($($s("§cot"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),n_))),p_DEFAULT),Power($($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),m_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Cos(Plus(c,Times(d,x))),x))),Times(CN1,f,Power(d,-1),Subst(Int(Times(Power(Plus(C1,Times(CN1,Sqr(f),Sqr(x))),Times(C1D2,Plus(m,Times(CN1,n,p),Negate(C1)))),Power(ExpandToSum(Plus(Times(b,Power(Times(f,x),n)),Times(a,Power(Plus(C1,Times(CN1,Sqr(f),Sqr(x))),Times(C1D2,n)))),x),p)),x),x,Times(Cos(Plus(c,Times(d,x))),Power(f,-1))))),And(And(And(FreeQ(List(a,b,c,d),x),OddQ(m)),EvenQ(n)),IntegerQ(p)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power(Times(e_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),n_))),p_),Power($($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),m_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Tan(Plus(c,Times(d,x))),x))),Times(f,Power(d,-1),Subst(Int(Times(Power(Times(f,x),m),Power(Plus(a,Times(b,Power(Times(e,f,x),n))),p),Power(Plus(C1,Times(Sqr(f),Sqr(x))),-1)),x),x,Times(Tan(Plus(c,Times(d,x))),Power(f,-1))))),FreeQ(List(a,b,c,d,e,m,n,p),x))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power(Times(e_DEFAULT,$($s("§cot"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),n_))),p_),Power($($s("§cot"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),m_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Cot(Plus(c,Times(d,x))),x))),Times(CN1,f,Power(d,-1),Subst(Int(Times(Power(Times(f,x),m),Power(Plus(a,Times(b,Power(Times(e,f,x),n))),p),Power(Plus(C1,Times(Sqr(f),Sqr(x))),-1)),x),x,Times(Cot(Plus(c,Times(d,x))),Power(f,-1))))),FreeQ(List(a,b,c,d,e,m,n,p),x)))
  );
}
