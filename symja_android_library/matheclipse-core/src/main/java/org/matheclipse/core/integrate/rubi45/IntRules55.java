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
public class IntRules55 { 
  public static IAST RULES = List( 
ISetDelayed(Int($($s("§tan"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),x_Symbol),
    Condition(Times(CN1,Log(RemoveContent(Cos(Plus(a,Times(b,x))),x)),Power(b,CN1)),FreeQ(List(a,b),x))),
ISetDelayed(Int($($s("§cot"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),x_Symbol),
    Condition(Times(Log(RemoveContent(Sin(Plus(a,Times(b,x))),x)),Power(b,CN1)),FreeQ(List(a,b),x))),
ISetDelayed(Int(Power(Times($($s("§tan"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),c_DEFAULT),pn_),x_Symbol),
    Condition(Plus(Times(c,Power(Times(c,Tan(Plus(a,Times(b,x)))),Plus(pn,Times(CN1,C1))),Power(Times(b,Plus(pn,Times(CN1,C1))),CN1)),Times(CN1,Sqr(c),Int(Power(Times(c,Tan(Plus(a,Times(b,x)))),Plus(pn,Times(CN1,C2))),x))),And(And(FreeQ(List(a,b,c),x),RationalQ(pn)),Greater(pn,C1)))),
ISetDelayed(Int(Power(Times($($s("§cot"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),c_DEFAULT),pn_),x_Symbol),
    Condition(Plus(Times(CN1,c,Power(Times(c,Cot(Plus(a,Times(b,x)))),Plus(pn,Times(CN1,C1))),Power(Times(b,Plus(pn,Times(CN1,C1))),CN1)),Times(CN1,Sqr(c),Int(Power(Times(c,Cot(Plus(a,Times(b,x)))),Plus(pn,Times(CN1,C2))),x))),And(And(FreeQ(List(a,b,c),x),RationalQ(pn)),Greater(pn,C1)))),
ISetDelayed(Int(Power(Times($($s("§tan"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),c_DEFAULT),pn_),x_Symbol),
    Condition(Plus(Times(Power(Times(c,Tan(Plus(a,Times(b,x)))),Plus(pn,C1)),Power(Times(b,c,Plus(pn,C1)),CN1)),Times(CN1,Power(c,CN2),Int(Power(Times(c,Tan(Plus(a,Times(b,x)))),Plus(pn,C2)),x))),And(And(FreeQ(List(a,b,c),x),RationalQ(pn)),Less(pn,CN1)))),
ISetDelayed(Int(Power(Times($($s("§cot"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),c_DEFAULT),pn_),x_Symbol),
    Condition(Plus(Times(CN1,Power(Times(c,Cot(Plus(a,Times(b,x)))),Plus(pn,C1)),Power(Times(b,c,Plus(pn,C1)),CN1)),Times(CN1,Power(c,CN2),Int(Power(Times(c,Cot(Plus(a,Times(b,x)))),Plus(pn,C2)),x))),And(And(FreeQ(List(a,b,c),x),RationalQ(pn)),Less(pn,CN1)))),
ISetDelayed(Int(Sqrt(Times($($s("§tan"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),c_)),x_Symbol),
    Condition(Plus(Times(c,Power(Times(C2,CI),CN1),Int(Times(Plus(C1,Times(CI,Tan(Plus(a,Times(b,x))))),Power(Times(c,Tan(Plus(a,Times(b,x)))),CN1D2)),x)),Times(CN1,c,Power(Times(C2,CI),CN1),Int(Times(Plus(C1,Times(CN1,CI,Tan(Plus(a,Times(b,x))))),Power(Times(c,Tan(Plus(a,Times(b,x)))),CN1D2)),x))),And(FreeQ(List(a,b,c),x),MatchQ(c,Times(pd_DEFAULT,Complex(m_,pn_)))))),
ISetDelayed(Int(Sqrt(Times($($s("§tan"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),c_DEFAULT)),x_Symbol),
    Condition(Plus(Times(C1D2,c,Int(Times(Plus(C1,Tan(Plus(a,Times(b,x)))),Power(Times(c,Tan(Plus(a,Times(b,x)))),CN1D2)),x)),Times(CN1,C1D2,c,Int(Times(Plus(C1,Times(CN1,Tan(Plus(a,Times(b,x))))),Power(Times(c,Tan(Plus(a,Times(b,x)))),CN1D2)),x))),FreeQ(List(a,b,c),x))),
ISetDelayed(Int(Sqrt(Times($($s("§cot"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),c_)),x_Symbol),
    Condition(Plus(Times(c,Power(Times(C2,CI),CN1),Int(Times(Plus(C1,Times(CI,Cot(Plus(a,Times(b,x))))),Power(Times(c,Cot(Plus(a,Times(b,x)))),CN1D2)),x)),Times(CN1,c,Power(Times(C2,CI),CN1),Int(Times(Plus(C1,Times(CN1,CI,Cot(Plus(a,Times(b,x))))),Power(Times(c,Cot(Plus(a,Times(b,x)))),CN1D2)),x))),And(FreeQ(List(a,b,c),x),MatchQ(c,Times(pd_DEFAULT,Complex(m_,pn_)))))),
ISetDelayed(Int(Sqrt(Times($($s("§cot"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),c_DEFAULT)),x_Symbol),
    Condition(Plus(Times(C1D2,c,Int(Times(Plus(C1,Cot(Plus(a,Times(b,x)))),Power(Times(c,Cot(Plus(a,Times(b,x)))),CN1D2)),x)),Times(CN1,C1D2,c,Int(Times(Plus(C1,Times(CN1,Cot(Plus(a,Times(b,x))))),Power(Times(c,Cot(Plus(a,Times(b,x)))),CN1D2)),x))),FreeQ(List(a,b,c),x))),
ISetDelayed(Int(Power(Times($($s("§tan"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),c_DEFAULT),CN1D2),x_Symbol),
    Condition(Plus(Times(C1D2,Int(Times(Plus(C1,Times(CI,Tan(Plus(a,Times(b,x))))),Power(Times(c,Tan(Plus(a,Times(b,x)))),CN1D2)),x)),Times(C1D2,Int(Times(Plus(C1,Times(CN1,CI,Tan(Plus(a,Times(b,x))))),Power(Times(c,Tan(Plus(a,Times(b,x)))),CN1D2)),x))),And(FreeQ(List(a,b,c),x),MatchQ(c,Times(pd_DEFAULT,Complex(m_,pn_)))))),
ISetDelayed(Int(Power(Times($($s("§tan"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),c_DEFAULT),CN1D2),x_Symbol),
    Condition(Plus(Times(C1D2,Int(Times(Plus(C1,Tan(Plus(a,Times(b,x)))),Power(Times(c,Tan(Plus(a,Times(b,x)))),CN1D2)),x)),Times(C1D2,Int(Times(Plus(C1,Times(CN1,Tan(Plus(a,Times(b,x))))),Power(Times(c,Tan(Plus(a,Times(b,x)))),CN1D2)),x))),FreeQ(List(a,b,c),x))),
ISetDelayed(Int(Power(Times($($s("§cot"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),c_DEFAULT),CN1D2),x_Symbol),
    Condition(Plus(Times(C1D2,Int(Times(Plus(C1,Times(CI,Cot(Plus(a,Times(b,x))))),Power(Times(c,Cot(Plus(a,Times(b,x)))),CN1D2)),x)),Times(C1D2,Int(Times(Plus(C1,Times(CN1,CI,Cot(Plus(a,Times(b,x))))),Power(Times(c,Cot(Plus(a,Times(b,x)))),CN1D2)),x))),And(FreeQ(List(a,b,c),x),MatchQ(c,Times(pd_DEFAULT,Complex(m_,pn_)))))),
ISetDelayed(Int(Power(Times($($s("§cot"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),c_DEFAULT),CN1D2),x_Symbol),
    Condition(Plus(Times(C1D2,Int(Times(Plus(C1,Cot(Plus(a,Times(b,x)))),Power(Times(c,Cot(Plus(a,Times(b,x)))),CN1D2)),x)),Times(C1D2,Int(Times(Plus(C1,Times(CN1,Cot(Plus(a,Times(b,x))))),Power(Times(c,Cot(Plus(a,Times(b,x)))),CN1D2)),x))),FreeQ(List(a,b,c),x))),
ISetDelayed(Int(Power(Times($($s("§tan"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),c_DEFAULT),pn_),x_Symbol),
    Condition(Module(List(Set(k,Denominator(pn))),Times(k,c,Power(b,CN1),Subst(Int(Times(Power(x,Plus(Times(k,Plus(pn,C1)),Times(CN1,C1))),Power(Plus(Sqr(c),Power(x,Times(C2,k))),CN1)),x),x,Power(Times(c,Tan(Plus(a,Times(b,x)))),Power(k,CN1))))),And(And(FreeQ(List(a,b,c),x),RationalQ(pn)),Less(Less(CN1,pn),C1)))),
ISetDelayed(Int(Power(Times($($s("§cot"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),c_DEFAULT),pn_),x_Symbol),
    Condition(Module(List(Set(k,Denominator(pn))),Times(CN1,k,c,Power(b,CN1),Subst(Int(Times(Power(x,Plus(Times(k,Plus(pn,C1)),Times(CN1,C1))),Power(Plus(Sqr(c),Power(x,Times(C2,k))),CN1)),x),x,Power(Times(c,Cot(Plus(a,Times(b,x)))),Power(k,CN1))))),And(And(FreeQ(List(a,b,c),x),RationalQ(pn)),Less(Less(CN1,pn),C1)))),
ISetDelayed(Int(Power(Times($($s("§tan"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),c_DEFAULT),pn_),x_Symbol),
    Condition(Times(Power(Times(c,Tan(Plus(a,Times(b,x)))),Plus(pn,C1)),Power(Times(b,c,Plus(pn,C1)),CN1),Hypergeometric2F1(C1,Times(C1D2,Plus(pn,C1)),Times(C1D2,Plus(pn,C3)),Times(CN1,Sqr(Tan(Plus(a,Times(b,x))))))),And(FreeQ(List(a,b,c,pn),x),Not(IntegerQ(pn))))),
ISetDelayed(Int(Power(Times($($s("§cot"),Plus(Times(b_DEFAULT,x_),a_DEFAULT)),c_DEFAULT),pn_),x_Symbol),
    Condition(Times(CN1,Power(Times(c,Cot(Plus(a,Times(b,x)))),Plus(pn,C1)),Power(Times(b,c,Plus(pn,C1)),CN1),Hypergeometric2F1(C1,Times(C1D2,Plus(pn,C1)),Times(C1D2,Plus(pn,C3)),Times(CN1,Sqr(Cot(Plus(a,Times(b,x))))))),And(FreeQ(List(a,b,c,pn),x),Not(IntegerQ(pn)))))
  );
}
