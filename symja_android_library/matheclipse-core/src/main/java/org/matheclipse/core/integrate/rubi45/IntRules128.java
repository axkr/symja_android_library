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
public class IntRules128 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Erf(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Plus(a,Times(b,x)),Erf(Plus(a,Times(b,x))),Power(b,CN1)),Power(Times(b,Sqrt(Pi),Power(E,Sqr(Plus(a,Times(b,x))))),CN1)),FreeQ(List(a,b),x))),
ISetDelayed(Int(Erfc(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Plus(a,Times(b,x)),Erfc(Plus(a,Times(b,x))),Power(b,CN1)),Times(CN1,Power(Times(b,Sqrt(Pi),Power(E,Sqr(Plus(a,Times(b,x))))),CN1))),FreeQ(List(a,b),x))),
ISetDelayed(Int(Erfi(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Plus(a,Times(b,x)),Erfi(Plus(a,Times(b,x))),Power(b,CN1)),Times(CN1,Power(E,Sqr(Plus(a,Times(b,x)))),Power(Times(b,Sqrt(Pi)),CN1))),FreeQ(List(a,b),x))),
ISetDelayed(Int(Times(Erf(Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Times(C2,b,x,Power(Pi,CN1D2),HypergeometricPFQ(List(C1D2,C1D2),List(QQ(3L,2L),QQ(3L,2L)),Times(CN1,Sqr(b),Sqr(x)))),FreeQ(b,x))),
ISetDelayed(Int(Times(Erfc(Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Log(x),Times(CN1,Int(Times(Erf(Times(b,x)),Power(x,CN1)),x))),FreeQ(b,x))),
ISetDelayed(Int(Times(Erfi(Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Times(C2,b,x,Power(Pi,CN1D2),HypergeometricPFQ(List(C1D2,C1D2),List(QQ(3L,2L),QQ(3L,2L)),Times(Sqr(b),Sqr(x)))),FreeQ(b,x))),
ISetDelayed(Int(Times(Erf(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),Erf(Plus(a,Times(b,x))),Power(Plus(m,C1),CN1)),Times(CN1,C2,b,Power(Times(Sqrt(Pi),Plus(m,C1)),CN1),Int(Times(Power(x,Plus(m,C1)),Power(Power(E,Sqr(Plus(a,Times(b,x)))),CN1)),x))),And(FreeQ(List(a,b,m),x),NonzeroQ(Plus(m,C1))))),
ISetDelayed(Int(Times(Erfc(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),Erfc(Plus(a,Times(b,x))),Power(Plus(m,C1),CN1)),Times(C2,b,Power(Times(Sqrt(Pi),Plus(m,C1)),CN1),Int(Times(Power(x,Plus(m,C1)),Power(Power(E,Sqr(Plus(a,Times(b,x)))),CN1)),x))),And(FreeQ(List(a,b,m),x),NonzeroQ(Plus(m,C1))))),
ISetDelayed(Int(Times(Erfi(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),Erfi(Plus(a,Times(b,x))),Power(Plus(m,C1),CN1)),Times(CN1,C2,b,Power(Times(Sqrt(Pi),Plus(m,C1)),CN1),Int(Times(Power(x,Plus(m,C1)),Power(E,Sqr(Plus(a,Times(b,x))))),x))),And(FreeQ(List(a,b,m),x),NonzeroQ(Plus(m,C1))))),
ISetDelayed(Int(Times(Erf(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),Power(E,Plus(Times(Sqr(x_),pd_DEFAULT),c_DEFAULT)),x_),x_Symbol),
    Condition(Plus(Times(Power(E,Plus(c,Times(pd,Sqr(x)))),Erf(Plus(a,Times(b,x))),Power(Times(C2,pd),CN1)),Times(CN1,b,Power(Times(pd,Sqrt(Pi)),CN1),Int(Power(E,Plus(Times(CN1,Sqr(a)),c,Times(CN1,C2,a,b,x),Times(CN1,Plus(Sqr(b),Times(CN1,pd)),Sqr(x)))),x))),FreeQ(List(a,b,c,pd),x))),
ISetDelayed(Int(Times(Erfc(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),Power(E,Plus(Times(Sqr(x_),pd_DEFAULT),c_DEFAULT)),x_),x_Symbol),
    Condition(Plus(Times(Power(E,Plus(c,Times(pd,Sqr(x)))),Erfc(Plus(a,Times(b,x))),Power(Times(C2,pd),CN1)),Times(b,Power(Times(pd,Sqrt(Pi)),CN1),Int(Power(E,Plus(Times(CN1,Sqr(a)),c,Times(CN1,C2,a,b,x),Times(CN1,Plus(Sqr(b),Times(CN1,pd)),Sqr(x)))),x))),FreeQ(List(a,b,c,pd),x))),
ISetDelayed(Int(Times(Erfi(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),Power(E,Plus(Times(Sqr(x_),pd_DEFAULT),c_DEFAULT)),x_),x_Symbol),
    Condition(Plus(Times(Power(E,Plus(c,Times(pd,Sqr(x)))),Erfi(Plus(a,Times(b,x))),Power(Times(C2,pd),CN1)),Times(CN1,b,Power(Times(pd,Sqrt(Pi)),CN1),Int(Power(E,Plus(Sqr(a),c,Times(C2,a,b,x),Times(Plus(Sqr(b),pd),Sqr(x)))),x))),FreeQ(List(a,b,c,pd),x))),
ISetDelayed(Int(Times(Erf(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),Power(E,Plus(Times(Sqr(x_),pd_DEFAULT),c_DEFAULT)),Power(x_,m_)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,Times(CN1,C1))),Power(E,Plus(c,Times(pd,Sqr(x)))),Erf(Plus(a,Times(b,x))),Power(Times(C2,pd),CN1)),Times(CN1,b,Power(Times(pd,Sqrt(Pi)),CN1),Int(Times(Power(x,Plus(m,Times(CN1,C1))),Power(E,Plus(Times(CN1,Sqr(a)),c,Times(CN1,C2,a,b,x),Times(CN1,Plus(Sqr(b),Times(CN1,pd)),Sqr(x))))),x)),Times(CN1,Plus(m,Times(CN1,C1)),Power(Times(C2,pd),CN1),Int(Times(Power(x,Plus(m,Times(CN1,C2))),Power(E,Plus(c,Times(pd,Sqr(x)))),Erf(Plus(a,Times(b,x)))),x))),And(And(FreeQ(List(a,b,c,pd),x),IntegerQ(m)),Greater(m,C1)))),
ISetDelayed(Int(Times(Erfc(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),Power(E,Plus(Times(Sqr(x_),pd_DEFAULT),c_DEFAULT)),Power(x_,m_)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,Times(CN1,C1))),Power(E,Plus(c,Times(pd,Sqr(x)))),Erfc(Plus(a,Times(b,x))),Power(Times(C2,pd),CN1)),Times(b,Power(Times(pd,Sqrt(Pi)),CN1),Int(Times(Power(x,Plus(m,Times(CN1,C1))),Power(E,Plus(Times(CN1,Sqr(a)),c,Times(CN1,C2,a,b,x),Times(CN1,Plus(Sqr(b),Times(CN1,pd)),Sqr(x))))),x)),Times(CN1,Plus(m,Times(CN1,C1)),Power(Times(C2,pd),CN1),Int(Times(Power(x,Plus(m,Times(CN1,C2))),Power(E,Plus(c,Times(pd,Sqr(x)))),Erfc(Plus(a,Times(b,x)))),x))),And(And(FreeQ(List(a,b,c,pd),x),IntegerQ(m)),Greater(m,C1)))),
ISetDelayed(Int(Times(Erfi(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),Power(E,Plus(Times(Sqr(x_),pd_DEFAULT),c_DEFAULT)),Power(x_,m_)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,Times(CN1,C1))),Power(E,Plus(c,Times(pd,Sqr(x)))),Erfi(Plus(a,Times(b,x))),Power(Times(C2,pd),CN1)),Times(CN1,b,Power(Times(pd,Sqrt(Pi)),CN1),Int(Times(Power(x,Plus(m,Times(CN1,C1))),Power(E,Plus(Sqr(a),c,Times(C2,a,b,x),Times(Plus(Sqr(b),pd),Sqr(x))))),x)),Times(CN1,Plus(m,Times(CN1,C1)),Power(Times(C2,pd),CN1),Int(Times(Power(x,Plus(m,Times(CN1,C2))),Power(E,Plus(c,Times(pd,Sqr(x)))),Erfi(Plus(a,Times(b,x)))),x))),And(And(FreeQ(List(a,b,c,pd),x),IntegerQ(m)),Greater(m,C1)))),
ISetDelayed(Int(Times(Erf(Times(b_DEFAULT,x_)),Power(E,Plus(Times(Sqr(x_),pd_DEFAULT),c_DEFAULT)),Power(x_,CN1)),x_Symbol),
    Condition(Times(C2,b,Power(E,c),x,Power(Pi,CN1D2),HypergeometricPFQ(List(C1D2,C1),List(QQ(3L,2L),QQ(3L,2L)),Times(pd,Sqr(x)))),And(FreeQ(b,x),ZeroQ(Plus(pd,Times(CN1,Sqr(b))))))),
ISetDelayed(Int(Times(Erfc(Times(b_DEFAULT,x_)),Power(E,Plus(Times(Sqr(x_),pd_DEFAULT),c_DEFAULT)),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Int(Times(Power(E,Plus(c,Times(pd,Sqr(x)))),Power(x,CN1)),x),Times(CN1,Int(Times(Power(E,Plus(c,Times(pd,Sqr(x)))),Erf(Times(b,x)),Power(x,CN1)),x))),And(FreeQ(b,x),ZeroQ(Plus(pd,Times(CN1,Sqr(b))))))),
ISetDelayed(Int(Times(Erfi(Times(b_DEFAULT,x_)),Power(E,Plus(Times(Sqr(x_),pd_DEFAULT),c_DEFAULT)),Power(x_,CN1)),x_Symbol),
    Condition(Times(C2,b,Power(E,c),x,Power(Pi,CN1D2),HypergeometricPFQ(List(C1D2,C1),List(QQ(3L,2L),QQ(3L,2L)),Times(pd,Sqr(x)))),And(FreeQ(b,x),ZeroQ(Plus(pd,Sqr(b)))))),
ISetDelayed(Int(Times(Erf(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),Power(E,Plus(Times(Sqr(x_),pd_DEFAULT),c_DEFAULT)),Power(x_,m_)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),Power(E,Plus(c,Times(pd,Sqr(x)))),Erf(Plus(a,Times(b,x))),Power(Plus(m,C1),CN1)),Times(CN1,C2,b,Power(Times(Plus(m,C1),Sqrt(Pi)),CN1),Int(Times(Power(x,Plus(m,C1)),Power(E,Plus(Times(CN1,Sqr(a)),c,Times(CN1,C2,a,b,x),Times(CN1,Plus(Sqr(b),Times(CN1,pd)),Sqr(x))))),x)),Times(CN1,C2,pd,Power(Plus(m,C1),CN1),Int(Times(Power(x,Plus(m,C2)),Power(E,Plus(c,Times(pd,Sqr(x)))),Erf(Plus(a,Times(b,x)))),x))),And(And(FreeQ(List(a,b,c,pd),x),IntegerQ(m)),Less(m,CN1)))),
ISetDelayed(Int(Times(Erfc(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),Power(E,Plus(Times(Sqr(x_),pd_DEFAULT),c_DEFAULT)),Power(x_,m_)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),Power(E,Plus(c,Times(pd,Sqr(x)))),Erfc(Plus(a,Times(b,x))),Power(Plus(m,C1),CN1)),Times(C2,b,Power(Times(Plus(m,C1),Sqrt(Pi)),CN1),Int(Times(Power(x,Plus(m,C1)),Power(E,Plus(Times(CN1,Sqr(a)),c,Times(CN1,C2,a,b,x),Times(CN1,Plus(Sqr(b),Times(CN1,pd)),Sqr(x))))),x)),Times(CN1,C2,pd,Power(Plus(m,C1),CN1),Int(Times(Power(x,Plus(m,C2)),Power(E,Plus(c,Times(pd,Sqr(x)))),Erfc(Plus(a,Times(b,x)))),x))),And(And(FreeQ(List(a,b,c,pd),x),IntegerQ(m)),Less(m,CN1)))),
ISetDelayed(Int(Times(Erfi(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),Power(E,Plus(Times(Sqr(x_),pd_DEFAULT),c_DEFAULT)),Power(x_,m_)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),Power(E,Plus(c,Times(pd,Sqr(x)))),Erfi(Plus(a,Times(b,x))),Power(Plus(m,C1),CN1)),Times(CN1,C2,b,Power(Times(Plus(m,C1),Sqrt(Pi)),CN1),Int(Times(Power(x,Plus(m,C1)),Power(E,Plus(Sqr(a),c,Times(C2,a,b,x),Times(Plus(Sqr(b),pd),Sqr(x))))),x)),Times(CN1,C2,pd,Power(Plus(m,C1),CN1),Int(Times(Power(x,Plus(m,C2)),Power(E,Plus(c,Times(pd,Sqr(x)))),Erfi(Plus(a,Times(b,x)))),x))),And(And(FreeQ(List(a,b,c,pd),x),IntegerQ(m)),Less(m,CN1)))),
ISetDelayed(Int(Sqr(Erf(Plus(Times(b_DEFAULT,x_),a_DEFAULT))),x_Symbol),
    Condition(Plus(Times(Plus(a,Times(b,x)),Sqr(Erf(Plus(a,Times(b,x)))),Power(b,CN1)),Times(CN1,C4,Power(Pi,CN1D2),Int(Times(Plus(a,Times(b,x)),Erf(Plus(a,Times(b,x))),Power(Power(E,Sqr(Plus(a,Times(b,x)))),CN1)),x))),FreeQ(List(a,b),x))),
ISetDelayed(Int(Sqr(Erfc(Plus(Times(b_DEFAULT,x_),a_DEFAULT))),x_Symbol),
    Condition(Plus(Times(Plus(a,Times(b,x)),Sqr(Erfc(Plus(a,Times(b,x)))),Power(b,CN1)),Times(C4,Power(Pi,CN1D2),Int(Times(Plus(a,Times(b,x)),Erfc(Plus(a,Times(b,x))),Power(Power(E,Sqr(Plus(a,Times(b,x)))),CN1)),x))),FreeQ(List(a,b),x))),
ISetDelayed(Int(Sqr(Erfi(Plus(Times(b_DEFAULT,x_),a_DEFAULT))),x_Symbol),
    Condition(Plus(Times(Plus(a,Times(b,x)),Sqr(Erfi(Plus(a,Times(b,x)))),Power(b,CN1)),Times(CN1,C4,Power(Pi,CN1D2),Int(Times(Plus(a,Times(b,x)),Power(E,Sqr(Plus(a,Times(b,x)))),Erfi(Plus(a,Times(b,x)))),x))),FreeQ(List(a,b),x))),
ISetDelayed(Int(Times(Sqr(Erf(Times(b_DEFAULT,x_))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),Sqr(Erf(Times(b,x))),Power(Plus(m,C1),CN1)),Times(CN1,C4,b,Power(Times(Sqrt(Pi),Plus(m,C1)),CN1),Int(Times(Power(x,Plus(m,C1)),Power(E,Times(CN1,Sqr(b),Sqr(x))),Erf(Times(b,x))),x))),And(And(And(FreeQ(b,x),IntegerQ(m)),Unequal(m,CN1)),Or(Greater(m,C0),OddQ(m))))),
ISetDelayed(Int(Times(Sqr(Erfc(Times(b_DEFAULT,x_))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),Sqr(Erfc(Times(b,x))),Power(Plus(m,C1),CN1)),Times(C4,b,Power(Times(Sqrt(Pi),Plus(m,C1)),CN1),Int(Times(Power(x,Plus(m,C1)),Power(E,Times(CN1,Sqr(b),Sqr(x))),Erfc(Times(b,x))),x))),And(And(And(FreeQ(b,x),IntegerQ(m)),Unequal(Plus(m,C1),C0)),Or(Greater(m,C0),OddQ(m))))),
ISetDelayed(Int(Times(Sqr(Erfi(Times(b_DEFAULT,x_))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),Sqr(Erfi(Times(b,x))),Power(Plus(m,C1),CN1)),Times(CN1,C4,b,Power(Times(Sqrt(Pi),Plus(m,C1)),CN1),Int(Times(Power(x,Plus(m,C1)),Power(E,Times(Sqr(b),Sqr(x))),Erfi(Times(b,x))),x))),And(And(And(FreeQ(b,x),IntegerQ(m)),Unequal(Plus(m,C1),C0)),Or(Greater(m,C0),OddQ(m))))),
ISetDelayed(Int(Times(Sqr(Erf(Plus(Times(b_DEFAULT,x_),a_))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Times(Power(b,CN1),Subst(Int(Times(Power(Plus(Times(CN1,a,Power(b,CN1)),Times(x,Power(b,CN1))),m),Sqr(Erf(x))),x),x,Plus(a,Times(b,x)))),And(FreeQ(List(a,b),x),PositiveIntegerQ(m)))),
ISetDelayed(Int(Times(Sqr(Erfc(Plus(Times(b_DEFAULT,x_),a_))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Times(Power(b,CN1),Subst(Int(Times(Power(Plus(Times(CN1,a,Power(b,CN1)),Times(x,Power(b,CN1))),m),Sqr(Erfc(x))),x),x,Plus(a,Times(b,x)))),And(FreeQ(List(a,b),x),PositiveIntegerQ(m)))),
ISetDelayed(Int(Times(Sqr(Erfi(Plus(Times(b_DEFAULT,x_),a_))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Times(Power(b,CN1),Subst(Int(Times(Power(Plus(Times(CN1,a,Power(b,CN1)),Times(x,Power(b,CN1))),m),Sqr(Erfi(x))),x),x,Plus(a,Times(b,x)))),And(FreeQ(List(a,b),x),PositiveIntegerQ(m))))
  );
}
