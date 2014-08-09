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
public class IntRules38 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Sqr(Plus(Times($($s("§sin"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_)),x_Symbol),
    Condition(Plus(Times(Plus(Times(C2,Sqr(a)),Sqr(b)),C1D2,x),Times(CN1,Sqr(b),Cos(Plus(c,Times(pd,x))),Sin(Plus(c,Times(pd,x))),Power(Times(C2,pd),CN1)),Times(C2,a,b,Int(Sin(Plus(c,Times(pd,x))),x))),FreeQ(List(a,b,c,pd),x))),
ISetDelayed(Int(Sqr(Plus(Times($($s("§cos"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_)),x_Symbol),
    Condition(Plus(Times(Plus(Times(C2,Sqr(a)),Sqr(b)),C1D2,x),Times(Sqr(b),Sin(Plus(c,Times(pd,x))),Cos(Plus(c,Times(pd,x))),Power(Times(C2,pd),CN1)),Times(C2,a,b,Int(Cos(Plus(c,Times(pd,x))),x))),FreeQ(List(a,b,c,pd),x))),
ISetDelayed(Int(Sqrt(Plus(Times($($s("§sin"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_)),x_Symbol),
    Condition(Times(CN2,b,Cos(Plus(c,Times(pd,x))),Power(Times(pd,Sqrt(Plus(a,Times(b,Sin(Plus(c,Times(pd,x))))))),CN1)),And(FreeQ(List(a,b,c,pd),x),ZeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))))),
ISetDelayed(Int(Sqrt(Plus(Times($($s("§cos"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_)),x_Symbol),
    Condition(Times(C2,b,Sin(Plus(c,Times(pd,x))),Power(Times(pd,Sqrt(Plus(a,Times(b,Cos(Plus(c,Times(pd,x))))))),CN1)),And(FreeQ(List(a,b,c,pd),x),ZeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))))),
ISetDelayed(Int(Power(Plus(Times($($s("§sin"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),pn_),x_Symbol),
    Condition(Plus(Times(CN1,b,Cos(Plus(c,Times(pd,x))),Power(Plus(a,Times(b,Sin(Plus(c,Times(pd,x))))),Plus(pn,Times(CN1,C1))),Power(Times(pd,pn),CN1)),Times(a,Plus(Times(C2,pn),Times(CN1,C1)),Power(pn,CN1),Int(Power(Plus(a,Times(b,Sin(Plus(c,Times(pd,x))))),Plus(pn,Times(CN1,C1))),x))),And(And(And(And(FreeQ(List(a,b,c,pd),x),ZeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),RationalQ(pn)),Greater(pn,C1)),IntegerQ(Times(C2,pn))))),
ISetDelayed(Int(Power(Plus(Times($($s("§cos"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),pn_),x_Symbol),
    Condition(Plus(Times(b,Sin(Plus(c,Times(pd,x))),Power(Plus(a,Times(b,Cos(Plus(c,Times(pd,x))))),Plus(pn,Times(CN1,C1))),Power(Times(pd,pn),CN1)),Times(a,Plus(Times(C2,pn),Times(CN1,C1)),Power(pn,CN1),Int(Power(Plus(a,Times(b,Cos(Plus(c,Times(pd,x))))),Plus(pn,Times(CN1,C1))),x))),And(And(And(And(FreeQ(List(a,b,c,pd),x),ZeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),RationalQ(pn)),Greater(pn,C1)),IntegerQ(Times(C2,pn))))),
ISetDelayed(Int(Power(Plus(Times($($s("§sin"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),CN1),x_Symbol),
    Condition(Times(CN1,Cos(Plus(c,Times(pd,x))),Power(Times(pd,Plus(b,Times(a,Sin(Plus(c,Times(pd,x)))))),CN1)),And(FreeQ(List(a,b,c,pd),x),ZeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))))),
ISetDelayed(Int(Power(Plus(Times($($s("§cos"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),CN1),x_Symbol),
    Condition(Times(Sin(Plus(c,Times(pd,x))),Power(Times(pd,Plus(b,Times(a,Cos(Plus(c,Times(pd,x)))))),CN1)),And(FreeQ(List(a,b,c,pd),x),ZeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))))),
ISetDelayed(Int(Power(Plus(Times($($s("§sin"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),CN1D2),x_Symbol),
    Condition(Times(CN2,Power(pd,CN1),Subst(Int(Power(Plus(Times(C2,a),Times(CN1,Sqr(x))),CN1),x),x,Times(b,Cos(Plus(c,Times(pd,x))),Power(Plus(a,Times(b,Sin(Plus(c,Times(pd,x))))),CN1D2)))),And(FreeQ(List(a,b,c,pd),x),ZeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))))),
ISetDelayed(Int(Power(Plus(Times($($s("§cos"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),CN1D2),x_Symbol),
    Condition(Times(C2,Power(pd,CN1),Subst(Int(Power(Plus(Times(C2,a),Times(CN1,Sqr(x))),CN1),x),x,Times(b,Sin(Plus(c,Times(pd,x))),Power(Plus(a,Times(b,Cos(Plus(c,Times(pd,x))))),CN1D2)))),And(FreeQ(List(a,b,c,pd),x),ZeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))))),
ISetDelayed(Int(Power(Plus(Times($($s("§sin"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),pn_),x_Symbol),
    Condition(Plus(Times(b,Cos(Plus(c,Times(pd,x))),Power(Plus(a,Times(b,Sin(Plus(c,Times(pd,x))))),pn),Power(Times(a,pd,Plus(Times(C2,pn),C1)),CN1)),Times(Plus(pn,C1),Power(Times(a,Plus(Times(C2,pn),C1)),CN1),Int(Power(Plus(a,Times(b,Sin(Plus(c,Times(pd,x))))),Plus(pn,C1)),x))),And(And(And(And(FreeQ(List(a,b,c,pd),x),ZeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),RationalQ(pn)),Less(pn,CN1)),IntegerQ(Times(C2,pn))))),
ISetDelayed(Int(Power(Plus(Times($($s("§cos"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),pn_),x_Symbol),
    Condition(Plus(Times(CN1,b,Sin(Plus(c,Times(pd,x))),Power(Plus(a,Times(b,Cos(Plus(c,Times(pd,x))))),pn),Power(Times(a,pd,Plus(Times(C2,pn),C1)),CN1)),Times(Plus(pn,C1),Power(Times(a,Plus(Times(C2,pn),C1)),CN1),Int(Power(Plus(a,Times(b,Cos(Plus(c,Times(pd,x))))),Plus(pn,C1)),x))),And(And(And(And(FreeQ(List(a,b,c,pd),x),ZeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),RationalQ(pn)),Less(pn,CN1)),IntegerQ(Times(C2,pn))))),
ISetDelayed(Int(Power(Plus(Times($($s("§sin"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),pn_),x_Symbol),
    Condition(Times(a,CSqrt2,Cos(Plus(c,Times(pd,x))),Power(Plus(a,Times(b,Sin(Plus(c,Times(pd,x))))),pn),Power(Times(b,pd,Plus(Times(C2,pn),C1),Sqrt(Times(Plus(a,Times(CN1,b,Sin(Plus(c,Times(pd,x))))),Power(a,CN1)))),CN1),Hypergeometric2F1(C1D2,Plus(pn,C1D2),Plus(pn,QQ(3L,2L)),Times(Plus(a,Times(b,Sin(Plus(c,Times(pd,x))))),Power(Times(C2,a),CN1)))),And(And(FreeQ(List(a,b,c,pd,pn),x),ZeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),Not(IntegerQ(Times(C2,pn)))))),
ISetDelayed(Int(Power(Plus(Times($($s("§cos"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),pn_),x_Symbol),
    Condition(Times(CN1,a,CSqrt2,Sin(Plus(c,Times(pd,x))),Power(Plus(a,Times(b,Cos(Plus(c,Times(pd,x))))),pn),Power(Times(b,pd,Plus(Times(C2,pn),C1),Sqrt(Times(Plus(a,Times(CN1,b,Cos(Plus(c,Times(pd,x))))),Power(a,CN1)))),CN1),Hypergeometric2F1(C1D2,Plus(pn,C1D2),Plus(pn,QQ(3L,2L)),Times(Plus(a,Times(b,Cos(Plus(c,Times(pd,x))))),Power(Times(C2,a),CN1)))),And(And(FreeQ(List(a,b,c,pd,pn),x),ZeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),Not(IntegerQ(Times(C2,pn)))))),
ISetDelayed(Int(Sqrt(Plus(Times($($s("§sin"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_)),x_Symbol),
    Condition(Times(CN2,Sqrt(Plus(a,b)),Power(pd,CN1),EllipticE(Plus(Times(C1D4,Pi),Times(CN1,C1D2,Plus(c,Times(pd,x)))),Times(C2,b,Power(Plus(a,b),CN1)))),And(And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),PositiveQ(Plus(a,b))))),
ISetDelayed(Int(Sqrt(Plus(Times($($s("§cos"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_)),x_Symbol),
    Condition(Times(C2,Sqrt(Plus(a,b)),Power(pd,CN1),EllipticE(Times(C1D2,Plus(c,Times(pd,x))),Times(C2,b,Power(Plus(a,b),CN1)))),And(And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),PositiveQ(Plus(a,b))))),
ISetDelayed(Int(Sqrt(Plus(Times($($s("§sin"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_)),x_Symbol),
    Condition(Times(C2,Sqrt(Plus(a,Times(CN1,b))),Power(pd,CN1),EllipticE(Plus(Times(C1D4,Pi),Times(C1D2,Plus(c,Times(pd,x)))),Times(CN2,b,Power(Plus(a,Times(CN1,b)),CN1)))),And(And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),PositiveQ(Plus(a,Times(CN1,b)))))),
ISetDelayed(Int(Sqrt(Plus(Times($($s("§cos"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_)),x_Symbol),
    Condition(Times(C2,Sqrt(Plus(a,Times(CN1,b))),Power(pd,CN1),EllipticE(Plus(Times(C1D2,Pi),Times(C1D2,Plus(c,Times(pd,x)))),Times(CN2,b,Power(Plus(a,Times(CN1,b)),CN1)))),And(And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),PositiveQ(Plus(a,Times(CN1,b)))))),
ISetDelayed(Int(Sqrt(Plus(Times($($s("§sin"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_)),x_Symbol),
    Condition(Times(Sqrt(Plus(a,Times(b,Sin(Plus(c,Times(pd,x)))))),Power(Times(Plus(a,Times(b,Sin(Plus(c,Times(pd,x))))),Power(Plus(a,b),CN1)),CN1D2),Int(Sqrt(Plus(Times(a,Power(Plus(a,b),CN1)),Times(b,Power(Plus(a,b),CN1),Sin(Plus(c,Times(pd,x)))))),x)),And(And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),Not(PositiveQ(Plus(a,b)))))),
ISetDelayed(Int(Sqrt(Plus(Times($($s("§cos"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_)),x_Symbol),
    Condition(Times(Sqrt(Plus(a,Times(b,Cos(Plus(c,Times(pd,x)))))),Power(Times(Plus(a,Times(b,Cos(Plus(c,Times(pd,x))))),Power(Plus(a,b),CN1)),CN1D2),Int(Sqrt(Plus(Times(a,Power(Plus(a,b),CN1)),Times(b,Power(Plus(a,b),CN1),Cos(Plus(c,Times(pd,x)))))),x)),And(And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),Not(PositiveQ(Plus(a,b)))))),
ISetDelayed(Int(Power(Plus(Times($($s("§sin"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),pn_),x_Symbol),
    Condition(Plus(Times(CN1,b,Cos(Plus(c,Times(pd,x))),Power(Plus(a,Times(b,Sin(Plus(c,Times(pd,x))))),Plus(pn,Times(CN1,C1))),Power(Times(pd,pn),CN1)),Times(Power(pn,CN1),Int(Times(Power(Plus(a,Times(b,Sin(Plus(c,Times(pd,x))))),Plus(pn,Times(CN1,C2))),Simp(Plus(Times(Sqr(a),pn),Times(Sqr(b),Plus(pn,Times(CN1,C1))),Times(a,b,Plus(Times(C2,pn),Times(CN1,C1)),Sin(Plus(c,Times(pd,x))))),x)),x))),And(And(And(And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),RationalQ(pn)),Greater(pn,C1)),IntegerQ(Times(C2,pn))))),
ISetDelayed(Int(Power(Plus(Times($($s("§cos"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),pn_),x_Symbol),
    Condition(Plus(Times(b,Sin(Plus(c,Times(pd,x))),Power(Plus(a,Times(b,Cos(Plus(c,Times(pd,x))))),Plus(pn,Times(CN1,C1))),Power(Times(pd,pn),CN1)),Times(Power(pn,CN1),Int(Times(Power(Plus(a,Times(b,Cos(Plus(c,Times(pd,x))))),Plus(pn,Times(CN1,C2))),Simp(Plus(Times(Sqr(a),pn),Times(Sqr(b),Plus(pn,Times(CN1,C1))),Times(a,b,Plus(Times(C2,pn),Times(CN1,C1)),Cos(Plus(c,Times(pd,x))))),x)),x))),And(And(And(And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),RationalQ(pn)),Greater(pn,C1)),IntegerQ(Times(C2,pn))))),
ISetDelayed(Int(Power(Plus(Times($($s("§sin"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),CN1),x_Symbol),
    Condition(Module(List(Set(pe,FreeFactors(Tan(Times(C1D2,Plus(c,Times(pd,x)))),x))),Times(C2,pe,Power(pd,CN1),Subst(Int(Power(Plus(a,Times(C2,b,pe,x),Times(a,Sqr(pe),Sqr(x))),CN1),x),x,Times(Tan(Times(C1D2,Plus(c,Times(pd,x)))),Power(pe,CN1))))),And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))))),
ISetDelayed(Int(Power(Plus(Times($($s("§cos"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),CN1),x_Symbol),
    Condition(Module(List(Set(pe,FreeFactors(Tan(Times(C1D2,Plus(c,Times(pd,x)))),x))),Times(C2,pe,Power(pd,CN1),Subst(Int(Power(Plus(a,b,Times(Plus(a,Times(CN1,b)),Sqr(pe),Sqr(x))),CN1),x),x,Times(Tan(Times(C1D2,Plus(c,Times(pd,x)))),Power(pe,CN1))))),And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))))),
ISetDelayed(Int(Power(Plus(Times($($s("§sin"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),CN1D2),x_Symbol),
    Condition(Times(CN2,Power(Times(pd,Sqrt(Plus(a,b))),CN1),EllipticF(Plus(Times(C1D4,Pi),Times(CN1,C1D2,Plus(c,Times(pd,x)))),Times(C2,b,Power(Plus(a,b),CN1)))),And(And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),PositiveQ(Plus(a,b))))),
ISetDelayed(Int(Power(Plus(Times($($s("§cos"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),CN1D2),x_Symbol),
    Condition(Times(C2,Power(Times(pd,Sqrt(Plus(a,b))),CN1),EllipticF(Times(C1D2,Plus(c,Times(pd,x))),Times(C2,b,Power(Plus(a,b),CN1)))),And(And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),PositiveQ(Plus(a,b))))),
ISetDelayed(Int(Power(Plus(Times($($s("§sin"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),CN1D2),x_Symbol),
    Condition(Times(C2,Power(Times(pd,Sqrt(Plus(a,Times(CN1,b)))),CN1),EllipticF(Plus(Times(C1D4,Pi),Times(C1D2,Plus(c,Times(pd,x)))),Times(CN2,b,Power(Plus(a,Times(CN1,b)),CN1)))),And(And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),PositiveQ(Plus(a,Times(CN1,b)))))),
ISetDelayed(Int(Power(Plus(Times($($s("§cos"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),CN1D2),x_Symbol),
    Condition(Times(C2,Power(Times(pd,Sqrt(Plus(a,Times(CN1,b)))),CN1),EllipticF(Plus(Times(C1D2,Pi),Times(C1D2,Plus(c,Times(pd,x)))),Times(CN2,b,Power(Plus(a,Times(CN1,b)),CN1)))),And(And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),PositiveQ(Plus(a,Times(CN1,b)))))),
ISetDelayed(Int(Power(Plus(Times($($s("§sin"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),CN1D2),x_Symbol),
    Condition(Times(Sqrt(Times(Plus(a,Times(b,Sin(Plus(c,Times(pd,x))))),Power(Plus(a,b),CN1))),Power(Plus(a,Times(b,Sin(Plus(c,Times(pd,x))))),CN1D2),Int(Power(Plus(Times(a,Power(Plus(a,b),CN1)),Times(b,Power(Plus(a,b),CN1),Sin(Plus(c,Times(pd,x))))),CN1D2),x)),And(And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),Not(PositiveQ(Plus(a,b)))))),
ISetDelayed(Int(Power(Plus(Times($($s("§cos"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),CN1D2),x_Symbol),
    Condition(Times(Sqrt(Times(Plus(a,Times(b,Cos(Plus(c,Times(pd,x))))),Power(Plus(a,b),CN1))),Power(Plus(a,Times(b,Cos(Plus(c,Times(pd,x))))),CN1D2),Int(Power(Plus(Times(a,Power(Plus(a,b),CN1)),Times(b,Power(Plus(a,b),CN1),Cos(Plus(c,Times(pd,x))))),CN1D2),x)),And(And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),Not(PositiveQ(Plus(a,b)))))),
ISetDelayed(Int(Power(Plus(Times($($s("§sin"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),pn_),x_Symbol),
    Condition(Plus(Times(CN1,b,Cos(Plus(c,Times(pd,x))),Power(Plus(a,Times(b,Sin(Plus(c,Times(pd,x))))),Plus(pn,C1)),Power(Times(pd,Plus(pn,C1),Plus(Sqr(a),Times(CN1,Sqr(b)))),CN1)),Times(Power(Times(Plus(pn,C1),Plus(Sqr(a),Times(CN1,Sqr(b)))),CN1),Int(Times(Power(Plus(a,Times(b,Sin(Plus(c,Times(pd,x))))),Plus(pn,C1)),Simp(Plus(Times(a,Plus(pn,C1)),Times(CN1,b,Plus(pn,C2),Sin(Plus(c,Times(pd,x))))),x)),x))),And(And(And(And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),RationalQ(pn)),Less(pn,CN1)),IntegerQ(Times(C2,pn))))),
ISetDelayed(Int(Power(Plus(Times($($s("§cos"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),pn_),x_Symbol),
    Condition(Plus(Times(b,Sin(Plus(c,Times(pd,x))),Power(Plus(a,Times(b,Cos(Plus(c,Times(pd,x))))),Plus(pn,C1)),Power(Times(pd,Plus(pn,C1),Plus(Sqr(a),Times(CN1,Sqr(b)))),CN1)),Times(Power(Times(Plus(pn,C1),Plus(Sqr(a),Times(CN1,Sqr(b)))),CN1),Int(Times(Power(Plus(a,Times(b,Cos(Plus(c,Times(pd,x))))),Plus(pn,C1)),Simp(Plus(Times(a,Plus(pn,C1)),Times(CN1,b,Plus(pn,C2),Cos(Plus(c,Times(pd,x))))),x)),x))),And(And(And(And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),RationalQ(pn)),Less(pn,CN1)),IntegerQ(Times(C2,pn))))),
ISetDelayed(Int(Power(Plus(Times($($s("§sin"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),pn_),x_Symbol),
    Condition(Times(Power(Plus(a,Times(b,Sin(Plus(c,Times(pd,x))))),Plus(pn,C1)),Sqrt(Times(b,Plus(C1,Times(CN1,Sin(Plus(c,Times(pd,x))))),Power(Plus(a,b),CN1))),Sqrt(Times(CN1,b,Plus(C1,Sin(Plus(c,Times(pd,x)))),Power(Plus(a,Times(CN1,b)),CN1))),Power(Times(b,pd,Plus(pn,C1),Cos(Plus(c,Times(pd,x)))),CN1),AppellF1(Plus(pn,C1),C1D2,C1D2,Plus(pn,C2),Times(Plus(a,Times(b,Sin(Plus(c,Times(pd,x))))),Power(Plus(a,Times(CN1,b)),CN1)),Times(Plus(a,Times(b,Sin(Plus(c,Times(pd,x))))),Power(Plus(a,b),CN1)))),And(And(FreeQ(List(a,b,c,pd,pn),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),Not(IntegerQ(Times(C2,pn)))))),
ISetDelayed(Int(Power(Plus(Times($($s("§cos"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),pn_),x_Symbol),
    Condition(Times(CN1,Power(Plus(a,Times(b,Cos(Plus(c,Times(pd,x))))),Plus(pn,C1)),Sqrt(Times(b,Plus(C1,Times(CN1,Cos(Plus(c,Times(pd,x))))),Power(Plus(a,b),CN1))),Sqrt(Times(CN1,b,Plus(C1,Cos(Plus(c,Times(pd,x)))),Power(Plus(a,Times(CN1,b)),CN1))),Power(Times(b,pd,Plus(pn,C1),Sin(Plus(c,Times(pd,x)))),CN1),AppellF1(Plus(pn,C1),C1D2,C1D2,Plus(pn,C2),Times(Plus(a,Times(b,Cos(Plus(c,Times(pd,x))))),Power(Plus(a,Times(CN1,b)),CN1)),Times(Plus(a,Times(b,Cos(Plus(c,Times(pd,x))))),Power(Plus(a,b),CN1)))),And(And(FreeQ(List(a,b,c,pd,pn),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),Not(IntegerQ(Times(C2,pn)))))),
ISetDelayed(Int(Power(Plus(Times($($s("§cos"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),$($s("§sin"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),pn_),x_Symbol),
    Condition(Int(Power(Plus(a,Times(b,C1D2,Sin(Plus(Times(C2,c),Times(C2,pd,x))))),pn),x),FreeQ(List(a,b,c,pd,pn),x)))
  );
}
