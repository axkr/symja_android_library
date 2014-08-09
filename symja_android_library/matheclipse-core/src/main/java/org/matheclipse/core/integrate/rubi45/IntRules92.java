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
public class IntRules92 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Sin(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT)),x_Symbol),
    Condition(Int(Sin(Times(Sqr(Plus(b,Times(C2,c,x))),Power(Times(C4,c),CN1))),x),And(FreeQ(List(a,b,c),x),ZeroQ(Plus(Sqr(b),Times(CN1,C4,a,c)))))),
ISetDelayed(Int(Cos(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT)),x_Symbol),
    Condition(Int(Cos(Times(Sqr(Plus(b,Times(C2,c,x))),Power(Times(C4,c),CN1))),x),And(FreeQ(List(a,b,c),x),ZeroQ(Plus(Sqr(b),Times(CN1,C4,a,c)))))),
ISetDelayed(Int(Sin(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Cos(Times(Plus(Sqr(b),Times(CN1,C4,a,c)),Power(Times(C4,c),CN1))),Int(Sin(Times(Sqr(Plus(b,Times(C2,c,x))),Power(Times(C4,c),CN1))),x)),Times(CN1,Sin(Times(Plus(Sqr(b),Times(CN1,C4,a,c)),Power(Times(C4,c),CN1))),Int(Cos(Times(Sqr(Plus(b,Times(C2,c,x))),Power(Times(C4,c),CN1))),x))),And(FreeQ(List(a,b,c),x),NonzeroQ(Plus(Sqr(b),Times(CN1,C4,a,c)))))),
ISetDelayed(Int(Cos(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Cos(Times(Plus(Sqr(b),Times(CN1,C4,a,c)),Power(Times(C4,c),CN1))),Int(Cos(Times(Sqr(Plus(b,Times(C2,c,x))),Power(Times(C4,c),CN1))),x)),Times(Sin(Times(Plus(Sqr(b),Times(CN1,C4,a,c)),Power(Times(C4,c),CN1))),Int(Sin(Times(Sqr(Plus(b,Times(C2,c,x))),Power(Times(C4,c),CN1))),x))),And(FreeQ(List(a,b,c),x),NonzeroQ(Plus(Sqr(b),Times(CN1,C4,a,c)))))),
ISetDelayed(Int(Power(Sin(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT)),pn_),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),pn),x),x),And(And(FreeQ(List(a,b,c),x),IntegerQ(pn)),Greater(pn,C1)))),
ISetDelayed(Int(Power(Cos(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT)),pn_),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),pn),x),x),And(And(FreeQ(List(a,b,c),x),IntegerQ(pn)),Greater(pn,C1)))),
ISetDelayed(Int(Power(Sin(v_),pn_DEFAULT),x_Symbol),
    Condition(Int(Power(Sin(ExpandToSum(v,x)),pn),x),And(And(PositiveIntegerQ(pn),QuadraticQ(v,x)),Not(QuadraticMatchQ(v,x))))),
ISetDelayed(Int(Power(Cos(v_),pn_DEFAULT),x_Symbol),
    Condition(Int(Power(Cos(ExpandToSum(v,x)),pn),x),And(And(PositiveIntegerQ(pn),QuadraticQ(v,x)),Not(QuadraticMatchQ(v,x))))),
ISetDelayed(Int(Times(Plus(Times(x_,pe_DEFAULT),pd_),Sin(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT))),x_Symbol),
    Condition(Times(CN1,pe,Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),And(FreeQ(List(a,b,c,pd,pe),x),ZeroQ(Plus(Times(C2,c,pd),Times(CN1,b,pe)))))),
ISetDelayed(Int(Times(Cos(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT)),Plus(Times(x_,pe_DEFAULT),pd_)),x_Symbol),
    Condition(Times(pe,Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),And(FreeQ(List(a,b,c,pd,pe),x),ZeroQ(Plus(Times(C2,c,pd),Times(CN1,b,pe)))))),
ISetDelayed(Int(Times(Plus(Times(x_,pe_DEFAULT),pd_DEFAULT),Sin(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT))),x_Symbol),
    Condition(Plus(Times(CN1,pe,Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),Times(Plus(Times(C2,c,pd),Times(CN1,b,pe)),Power(Times(C2,c),CN1),Int(Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),x))),And(FreeQ(List(a,b,c,pd,pe),x),NonzeroQ(Plus(Times(C2,c,pd),Times(CN1,b,pe)))))),
ISetDelayed(Int(Times(Cos(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT)),Plus(Times(x_,pe_DEFAULT),pd_DEFAULT)),x_Symbol),
    Condition(Plus(Times(pe,Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),Times(Plus(Times(C2,c,pd),Times(CN1,b,pe)),Power(Times(C2,c),CN1),Int(Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),x))),And(FreeQ(List(a,b,c,pd,pe),x),NonzeroQ(Plus(Times(C2,c,pd),Times(CN1,b,pe)))))),
ISetDelayed(Int(Times(Power(Plus(Times(x_,pe_DEFAULT),pd_DEFAULT),m_),Sin(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT))),x_Symbol),
    Condition(Plus(Times(CN1,pe,Power(Plus(pd,Times(pe,x)),Plus(m,Times(CN1,C1))),Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),Times(Sqr(pe),Plus(m,Times(CN1,C1)),Power(Times(C2,c),CN1),Int(Times(Power(Plus(pd,Times(pe,x)),Plus(m,Times(CN1,C2))),Cos(Plus(a,Times(b,x),Times(c,Sqr(x))))),x))),And(And(And(FreeQ(List(a,b,c,pd,pe),x),RationalQ(m)),Greater(m,C1)),ZeroQ(Plus(Times(b,pe),Times(CN1,C2,c,pd)))))),
ISetDelayed(Int(Times(Cos(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT)),Power(Plus(Times(x_,pe_DEFAULT),pd_DEFAULT),m_)),x_Symbol),
    Condition(Plus(Times(pe,Power(Plus(pd,Times(pe,x)),Plus(m,Times(CN1,C1))),Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),Times(CN1,Sqr(pe),Plus(m,Times(CN1,C1)),Power(Times(C2,c),CN1),Int(Times(Power(Plus(pd,Times(pe,x)),Plus(m,Times(CN1,C2))),Sin(Plus(a,Times(b,x),Times(c,Sqr(x))))),x))),And(And(And(FreeQ(List(a,b,c,pd,pe),x),RationalQ(m)),Greater(m,C1)),ZeroQ(Plus(Times(b,pe),Times(CN1,C2,c,pd)))))),
ISetDelayed(Int(Times(Power(Plus(Times(x_,pe_DEFAULT),pd_DEFAULT),m_),Sin(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT))),x_Symbol),
    Condition(Plus(Times(CN1,pe,Power(Plus(pd,Times(pe,x)),Plus(m,Times(CN1,C1))),Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),Times(CN1,Plus(Times(b,pe),Times(CN1,C2,c,pd)),Power(Times(C2,c),CN1),Int(Times(Power(Plus(pd,Times(pe,x)),Plus(m,Times(CN1,C1))),Sin(Plus(a,Times(b,x),Times(c,Sqr(x))))),x)),Times(Sqr(pe),Plus(m,Times(CN1,C1)),Power(Times(C2,c),CN1),Int(Times(Power(Plus(pd,Times(pe,x)),Plus(m,Times(CN1,C2))),Cos(Plus(a,Times(b,x),Times(c,Sqr(x))))),x))),And(And(And(FreeQ(List(a,b,c,pd,pe),x),RationalQ(m)),Greater(m,C1)),NonzeroQ(Plus(Times(b,pe),Times(CN1,C2,c,pd)))))),
ISetDelayed(Int(Times(Cos(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT)),Power(Plus(Times(x_,pe_DEFAULT),pd_DEFAULT),m_)),x_Symbol),
    Condition(Plus(Times(pe,Power(Plus(pd,Times(pe,x)),Plus(m,Times(CN1,C1))),Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),Times(CN1,Plus(Times(b,pe),Times(CN1,C2,c,pd)),Power(Times(C2,c),CN1),Int(Times(Power(Plus(pd,Times(pe,x)),Plus(m,Times(CN1,C1))),Cos(Plus(a,Times(b,x),Times(c,Sqr(x))))),x)),Times(CN1,Sqr(pe),Plus(m,Times(CN1,C1)),Power(Times(C2,c),CN1),Int(Times(Power(Plus(pd,Times(pe,x)),Plus(m,Times(CN1,C2))),Sin(Plus(a,Times(b,x),Times(c,Sqr(x))))),x))),And(And(And(FreeQ(List(a,b,c,pd,pe),x),RationalQ(m)),Greater(m,C1)),NonzeroQ(Plus(Times(b,pe),Times(CN1,C2,c,pd)))))),
ISetDelayed(Int(Times(Power(Plus(Times(x_,pe_DEFAULT),pd_DEFAULT),m_),Sin(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT))),x_Symbol),
    Condition(Plus(Times(Power(Plus(pd,Times(pe,x)),Plus(m,C1)),Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(pe,Plus(m,C1)),CN1)),Times(CN1,C2,c,Power(Times(Sqr(pe),Plus(m,C1)),CN1),Int(Times(Power(Plus(pd,Times(pe,x)),Plus(m,C2)),Cos(Plus(a,Times(b,x),Times(c,Sqr(x))))),x))),And(And(And(FreeQ(List(a,b,c,pd,pe),x),RationalQ(m)),Less(m,CN1)),ZeroQ(Plus(Times(b,pe),Times(CN1,C2,c,pd)))))),
ISetDelayed(Int(Times(Cos(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT)),Power(Plus(Times(x_,pe_DEFAULT),pd_DEFAULT),m_)),x_Symbol),
    Condition(Plus(Times(Power(Plus(pd,Times(pe,x)),Plus(m,C1)),Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(pe,Plus(m,C1)),CN1)),Times(C2,c,Power(Times(Sqr(pe),Plus(m,C1)),CN1),Int(Times(Power(Plus(pd,Times(pe,x)),Plus(m,C2)),Sin(Plus(a,Times(b,x),Times(c,Sqr(x))))),x))),And(And(And(FreeQ(List(a,b,c,pd,pe),x),RationalQ(m)),Less(m,CN1)),ZeroQ(Plus(Times(b,pe),Times(CN1,C2,c,pd)))))),
ISetDelayed(Int(Times(Power(Plus(Times(x_,pe_DEFAULT),pd_DEFAULT),m_),Sin(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT))),x_Symbol),
    Condition(Plus(Times(Power(Plus(pd,Times(pe,x)),Plus(m,C1)),Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(pe,Plus(m,C1)),CN1)),Times(CN1,Plus(Times(b,pe),Times(CN1,C2,c,pd)),Power(Times(Sqr(pe),Plus(m,C1)),CN1),Int(Times(Power(Plus(pd,Times(pe,x)),Plus(m,C1)),Cos(Plus(a,Times(b,x),Times(c,Sqr(x))))),x)),Times(CN1,C2,c,Power(Times(Sqr(pe),Plus(m,C1)),CN1),Int(Times(Power(Plus(pd,Times(pe,x)),Plus(m,C2)),Cos(Plus(a,Times(b,x),Times(c,Sqr(x))))),x))),And(And(And(FreeQ(List(a,b,c,pd,pe),x),RationalQ(m)),Less(m,CN1)),NonzeroQ(Plus(Times(b,pe),Times(CN1,C2,c,pd)))))),
ISetDelayed(Int(Times(Cos(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT)),Power(Plus(Times(x_,pe_DEFAULT),pd_DEFAULT),m_)),x_Symbol),
    Condition(Plus(Times(Power(Plus(pd,Times(pe,x)),Plus(m,C1)),Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(pe,Plus(m,C1)),CN1)),Times(Plus(Times(b,pe),Times(CN1,C2,c,pd)),Power(Times(Sqr(pe),Plus(m,C1)),CN1),Int(Times(Power(Plus(pd,Times(pe,x)),Plus(m,C1)),Sin(Plus(a,Times(b,x),Times(c,Sqr(x))))),x)),Times(C2,c,Power(Times(Sqr(pe),Plus(m,C1)),CN1),Int(Times(Power(Plus(pd,Times(pe,x)),Plus(m,C2)),Sin(Plus(a,Times(b,x),Times(c,Sqr(x))))),x))),And(And(And(FreeQ(List(a,b,c,pd,pe),x),RationalQ(m)),Less(m,CN1)),NonzeroQ(Plus(Times(b,pe),Times(CN1,C2,c,pd)))))),
ISetDelayed(Int(Times(Power(Plus(Times(x_,pe_DEFAULT),pd_DEFAULT),m_DEFAULT),Sin(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT))),x_Symbol),
    Condition($(Defer($s("Int")),Times(Power(Plus(pd,Times(pe,x)),m),Sin(Plus(a,Times(b,x),Times(c,Sqr(x))))),x),FreeQ(List(a,b,c,pd,pe,m),x))),
ISetDelayed(Int(Times(Cos(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT)),Power(Plus(Times(x_,pe_DEFAULT),pd_DEFAULT),m_DEFAULT)),x_Symbol),
    Condition($(Defer($s("Int")),Times(Power(Plus(pd,Times(pe,x)),m),Cos(Plus(a,Times(b,x),Times(c,Sqr(x))))),x),FreeQ(List(a,b,c,pd,pe,m),x))),
ISetDelayed(Int(Times(Power(Plus(Times(x_,pe_DEFAULT),pd_DEFAULT),m_DEFAULT),Power(Sin(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT)),pn_)),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Plus(pd,Times(pe,x)),m),Power(Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),pn),x),x),And(And(FreeQ(List(a,b,c,pd,pe,m),x),IntegerQ(pn)),Greater(pn,C1)))),
ISetDelayed(Int(Times(Power(Cos(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT)),pn_),Power(Plus(Times(x_,pe_DEFAULT),pd_DEFAULT),m_DEFAULT)),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Plus(pd,Times(pe,x)),m),Power(Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),pn),x),x),And(And(FreeQ(List(a,b,c,pd,pe,m),x),IntegerQ(pn)),Greater(pn,C1)))),
ISetDelayed(Int(Times(Power(Sin(v_),pn_DEFAULT),Power(u_,m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(ExpandToSum(u,x),m),Power(Sin(ExpandToSum(v,x)),pn)),x),And(And(And(And(FreeQ(m,x),PositiveIntegerQ(pn)),LinearQ(u,x)),QuadraticQ(v,x)),Not(And(LinearMatchQ(u,x),QuadraticMatchQ(v,x)))))),
ISetDelayed(Int(Times(Power(Cos(v_),pn_DEFAULT),Power(u_,m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(ExpandToSum(u,x),m),Power(Cos(ExpandToSum(v,x)),pn)),x),And(And(And(And(FreeQ(m,x),PositiveIntegerQ(pn)),LinearQ(u,x)),QuadraticQ(v,x)),Not(And(LinearMatchQ(u,x),QuadraticMatchQ(v,x)))))),
ISetDelayed(Int(Power(Tan(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT)),pn_DEFAULT),x_Symbol),
    Condition($(Defer($s("Int")),Power(Tan(Plus(a,Times(b,x),Times(c,Sqr(x)))),pn),x),FreeQ(List(a,b,c,pn),x))),
ISetDelayed(Int(Power(Cot(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT)),pn_DEFAULT),x_Symbol),
    Condition($(Defer($s("Int")),Power(Cot(Plus(a,Times(b,x),Times(c,Sqr(x)))),pn),x),FreeQ(List(a,b,c,pn),x))),
ISetDelayed(Int(Times(Plus(Times(x_,pe_DEFAULT),pd_),Tan(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT))),x_Symbol),
    Condition(Times(CN1,pe,Log(Cos(Plus(a,Times(b,x),Times(c,Sqr(x))))),Power(Times(C2,c),CN1)),And(FreeQ(List(a,b,c,pd,pe),x),ZeroQ(Plus(Times(C2,c,pd),Times(CN1,b,pe)))))),
ISetDelayed(Int(Times(Cot(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT)),Plus(Times(x_,pe_DEFAULT),pd_)),x_Symbol),
    Condition(Times(pe,Log(Sin(Plus(a,Times(b,x),Times(c,Sqr(x))))),Power(Times(C2,c),CN1)),And(FreeQ(List(a,b,c,pd,pe),x),ZeroQ(Plus(Times(C2,c,pd),Times(CN1,b,pe)))))),
ISetDelayed(Int(Times(Plus(Times(x_,pe_DEFAULT),pd_DEFAULT),Tan(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT))),x_Symbol),
    Condition(Plus(Times(CN1,pe,Log(Cos(Plus(a,Times(b,x),Times(c,Sqr(x))))),Power(Times(C2,c),CN1)),Times(Plus(Times(C2,c,pd),Times(CN1,b,pe)),Power(Times(C2,c),CN1),Int(Tan(Plus(a,Times(b,x),Times(c,Sqr(x)))),x))),And(FreeQ(List(a,b,c,pd,pe),x),NonzeroQ(Plus(Times(C2,c,pd),Times(CN1,b,pe)))))),
ISetDelayed(Int(Times(Cot(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT)),Plus(Times(x_,pe_DEFAULT),pd_DEFAULT)),x_Symbol),
    Condition(Plus(Times(pe,Log(Sin(Plus(a,Times(b,x),Times(c,Sqr(x))))),Power(Times(C2,c),CN1)),Times(Plus(Times(C2,c,pd),Times(CN1,b,pe)),Power(Times(C2,c),CN1),Int(Cot(Plus(a,Times(b,x),Times(c,Sqr(x)))),x))),And(FreeQ(List(a,b,c,pd,pe),x),NonzeroQ(Plus(Times(C2,c,pd),Times(CN1,b,pe)))))),
ISetDelayed(Int(Times(Power(Plus(Times(x_,pe_DEFAULT),pd_DEFAULT),m_DEFAULT),Power(Tan(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT)),pn_DEFAULT)),x_Symbol),
    Condition($(Defer($s("Int")),Times(Power(Plus(pd,Times(pe,x)),m),Power(Tan(Plus(a,Times(b,x),Times(c,Sqr(x)))),pn)),x),FreeQ(List(a,b,c,pd,pe,m,pn),x))),
ISetDelayed(Int(Times(Power(Cot(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT)),pn_DEFAULT),Power(Plus(Times(x_,pe_DEFAULT),pd_DEFAULT),m_DEFAULT)),x_Symbol),
    Condition($(Defer($s("Int")),Times(Power(Plus(pd,Times(pe,x)),m),Power(Cot(Plus(a,Times(b,x),Times(c,Sqr(x)))),pn)),x),FreeQ(List(a,b,c,pd,pe,m,pn),x)))
  );
}
