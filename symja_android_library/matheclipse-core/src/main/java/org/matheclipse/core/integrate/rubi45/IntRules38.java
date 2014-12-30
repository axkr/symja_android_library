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
ISetDelayed(Int(Sqr(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Plus(Times(Plus(Times(C2,Sqr(a)),Sqr(b)),C1D2,x),Times(CN1,Sqr(b),Cos(Plus(c,Times(d,x))),Sin(Plus(c,Times(d,x))),Power(Times(C2,d),CN1)),Times(C2,a,b,Int(Sin(Plus(c,Times(d,x))),x))),FreeQ(List(a,b,c,d),x))),
ISetDelayed(Int(Sqr(Plus(a_,Times(b_DEFAULT,$($s("§cos"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Plus(Times(Plus(Times(C2,Sqr(a)),Sqr(b)),C1D2,x),Times(Sqr(b),Sin(Plus(c,Times(d,x))),Cos(Plus(c,Times(d,x))),Power(Times(C2,d),CN1)),Times(C2,a,b,Int(Cos(Plus(c,Times(d,x))),x))),FreeQ(List(a,b,c,d),x))),
ISetDelayed(Int(Sqrt(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Times(CN2,b,Cos(Plus(c,Times(d,x))),Power(Times(d,Sqrt(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))))),CN1)),And(FreeQ(List(a,b,c,d),x),ZeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))))),
ISetDelayed(Int(Sqrt(Plus(a_,Times(b_DEFAULT,$($s("§cos"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Times(C2,b,Sin(Plus(c,Times(d,x))),Power(Times(d,Sqrt(Plus(a,Times(b,Cos(Plus(c,Times(d,x))))))),CN1)),And(FreeQ(List(a,b,c,d),x),ZeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Plus(Times(CN1,b,Cos(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),Plus(n,Times(CN1,C1))),Power(Times(d,n),CN1)),Times(a,Plus(Times(C2,n),Times(CN1,C1)),Power(n,CN1),Int(Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),Plus(n,Times(CN1,C1))),x))),And(And(And(And(FreeQ(List(a,b,c,d),x),ZeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),RationalQ(n)),Greater(n,C1)),IntegerQ(Times(C2,n))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§cos"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Plus(Times(b,Sin(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,x))))),Plus(n,Times(CN1,C1))),Power(Times(d,n),CN1)),Times(a,Plus(Times(C2,n),Times(CN1,C1)),Power(n,CN1),Int(Power(Plus(a,Times(b,Cos(Plus(c,Times(d,x))))),Plus(n,Times(CN1,C1))),x))),And(And(And(And(FreeQ(List(a,b,c,d),x),ZeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),RationalQ(n)),Greater(n,C1)),IntegerQ(Times(C2,n))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1),x_Symbol),
    Condition(Times(CN1,Cos(Plus(c,Times(d,x))),Power(Times(d,Plus(b,Times(a,Sin(Plus(c,Times(d,x)))))),CN1)),And(FreeQ(List(a,b,c,d),x),ZeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§cos"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1),x_Symbol),
    Condition(Times(Sin(Plus(c,Times(d,x))),Power(Times(d,Plus(b,Times(a,Cos(Plus(c,Times(d,x)))))),CN1)),And(FreeQ(List(a,b,c,d),x),ZeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1D2),x_Symbol),
    Condition(Times(CN2,Power(d,CN1),Subst(Int(Power(Plus(Times(C2,a),Times(CN1,Sqr(x))),CN1),x),x,Times(b,Cos(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),CN1D2)))),And(FreeQ(List(a,b,c,d),x),ZeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§cos"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1D2),x_Symbol),
    Condition(Times(C2,Power(d,CN1),Subst(Int(Power(Plus(Times(C2,a),Times(CN1,Sqr(x))),CN1),x),x,Times(b,Sin(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,x))))),CN1D2)))),And(FreeQ(List(a,b,c,d),x),ZeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Plus(Times(b,Cos(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),n),Power(Times(a,d,Plus(Times(C2,n),C1)),CN1)),Times(Plus(n,C1),Power(Times(a,Plus(Times(C2,n),C1)),CN1),Int(Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),Plus(n,C1)),x))),And(And(And(And(FreeQ(List(a,b,c,d),x),ZeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),RationalQ(n)),Less(n,CN1)),IntegerQ(Times(C2,n))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§cos"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Plus(Times(CN1,b,Sin(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,x))))),n),Power(Times(a,d,Plus(Times(C2,n),C1)),CN1)),Times(Plus(n,C1),Power(Times(a,Plus(Times(C2,n),C1)),CN1),Int(Power(Plus(a,Times(b,Cos(Plus(c,Times(d,x))))),Plus(n,C1)),x))),And(And(And(And(FreeQ(List(a,b,c,d),x),ZeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),RationalQ(n)),Less(n,CN1)),IntegerQ(Times(C2,n))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Times(a,CSqrt2,Cos(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),n),Power(Times(b,d,Plus(Times(C2,n),C1),Sqrt(Times(Plus(a,Times(CN1,b,Sin(Plus(c,Times(d,x))))),Power(a,CN1)))),CN1),Hypergeometric2F1(C1D2,Plus(n,C1D2),Plus(n,QQ(3L,2L)),Times(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),Power(Times(C2,a),CN1)))),And(And(FreeQ(List(a,b,c,d,n),x),ZeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),Not(IntegerQ(Times(C2,n)))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§cos"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Times(CN1,a,CSqrt2,Sin(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,x))))),n),Power(Times(b,d,Plus(Times(C2,n),C1),Sqrt(Times(Plus(a,Times(CN1,b,Cos(Plus(c,Times(d,x))))),Power(a,CN1)))),CN1),Hypergeometric2F1(C1D2,Plus(n,C1D2),Plus(n,QQ(3L,2L)),Times(Plus(a,Times(b,Cos(Plus(c,Times(d,x))))),Power(Times(C2,a),CN1)))),And(And(FreeQ(List(a,b,c,d,n),x),ZeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),Not(IntegerQ(Times(C2,n)))))),
ISetDelayed(Int(Sqrt(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Times(CN2,Sqrt(Plus(a,b)),Power(d,CN1),EllipticE(Plus(Times(C1D4,Pi),Times(CN1,C1D2,Plus(c,Times(d,x)))),Times(C2,b,Power(Plus(a,b),CN1)))),And(And(FreeQ(List(a,b,c,d),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),PositiveQ(Plus(a,b))))),
ISetDelayed(Int(Sqrt(Plus(a_,Times(b_DEFAULT,$($s("§cos"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Times(C2,Sqrt(Plus(a,b)),Power(d,CN1),EllipticE(Times(C1D2,Plus(c,Times(d,x))),Times(C2,b,Power(Plus(a,b),CN1)))),And(And(FreeQ(List(a,b,c,d),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),PositiveQ(Plus(a,b))))),
ISetDelayed(Int(Sqrt(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Times(C2,Sqrt(Plus(a,Times(CN1,b))),Power(d,CN1),EllipticE(Plus(Times(C1D4,Pi),Times(C1D2,Plus(c,Times(d,x)))),Times(CN2,b,Power(Plus(a,Times(CN1,b)),CN1)))),And(And(FreeQ(List(a,b,c,d),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),PositiveQ(Plus(a,Times(CN1,b)))))),
ISetDelayed(Int(Sqrt(Plus(a_,Times(b_DEFAULT,$($s("§cos"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Times(C2,Sqrt(Plus(a,Times(CN1,b))),Power(d,CN1),EllipticE(Plus(Times(C1D2,Pi),Times(C1D2,Plus(c,Times(d,x)))),Times(CN2,b,Power(Plus(a,Times(CN1,b)),CN1)))),And(And(FreeQ(List(a,b,c,d),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),PositiveQ(Plus(a,Times(CN1,b)))))),
ISetDelayed(Int(Sqrt(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Times(Sqrt(Plus(a,Times(b,Sin(Plus(c,Times(d,x)))))),Power(Times(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),Power(Plus(a,b),CN1)),CN1D2),Int(Sqrt(Plus(Times(a,Power(Plus(a,b),CN1)),Times(b,Power(Plus(a,b),CN1),Sin(Plus(c,Times(d,x)))))),x)),And(And(FreeQ(List(a,b,c,d),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),Not(PositiveQ(Plus(a,b)))))),
ISetDelayed(Int(Sqrt(Plus(a_,Times(b_DEFAULT,$($s("§cos"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Times(Sqrt(Plus(a,Times(b,Cos(Plus(c,Times(d,x)))))),Power(Times(Plus(a,Times(b,Cos(Plus(c,Times(d,x))))),Power(Plus(a,b),CN1)),CN1D2),Int(Sqrt(Plus(Times(a,Power(Plus(a,b),CN1)),Times(b,Power(Plus(a,b),CN1),Cos(Plus(c,Times(d,x)))))),x)),And(And(FreeQ(List(a,b,c,d),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),Not(PositiveQ(Plus(a,b)))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Plus(Times(CN1,b,Cos(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),Plus(n,Times(CN1,C1))),Power(Times(d,n),CN1)),Times(Power(n,CN1),Int(Times(Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),Plus(n,Times(CN1,C2))),Simp(Plus(Times(Sqr(a),n),Times(Sqr(b),Plus(n,Times(CN1,C1))),Times(a,b,Plus(Times(C2,n),Times(CN1,C1)),Sin(Plus(c,Times(d,x))))),x)),x))),And(And(And(And(FreeQ(List(a,b,c,d),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),RationalQ(n)),Greater(n,C1)),IntegerQ(Times(C2,n))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§cos"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Plus(Times(b,Sin(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,x))))),Plus(n,Times(CN1,C1))),Power(Times(d,n),CN1)),Times(Power(n,CN1),Int(Times(Power(Plus(a,Times(b,Cos(Plus(c,Times(d,x))))),Plus(n,Times(CN1,C2))),Simp(Plus(Times(Sqr(a),n),Times(Sqr(b),Plus(n,Times(CN1,C1))),Times(a,b,Plus(Times(C2,n),Times(CN1,C1)),Cos(Plus(c,Times(d,x))))),x)),x))),And(And(And(And(FreeQ(List(a,b,c,d),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),RationalQ(n)),Greater(n,C1)),IntegerQ(Times(C2,n))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1),x_Symbol),
    Condition(Module(List(Set(e,FreeFactors(Tan(Times(C1D2,Plus(c,Times(d,x)))),x))),Times(C2,e,Power(d,CN1),Subst(Int(Power(Plus(a,Times(C2,b,e,x),Times(a,Sqr(e),Sqr(x))),CN1),x),x,Times(Tan(Times(C1D2,Plus(c,Times(d,x)))),Power(e,CN1))))),And(FreeQ(List(a,b,c,d),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§cos"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1),x_Symbol),
    Condition(Module(List(Set(e,FreeFactors(Tan(Times(C1D2,Plus(c,Times(d,x)))),x))),Times(C2,e,Power(d,CN1),Subst(Int(Power(Plus(a,b,Times(Plus(a,Times(CN1,b)),Sqr(e),Sqr(x))),CN1),x),x,Times(Tan(Times(C1D2,Plus(c,Times(d,x)))),Power(e,CN1))))),And(FreeQ(List(a,b,c,d),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1D2),x_Symbol),
    Condition(Times(CN2,Power(Times(d,Sqrt(Plus(a,b))),CN1),EllipticF(Plus(Times(C1D4,Pi),Times(CN1,C1D2,Plus(c,Times(d,x)))),Times(C2,b,Power(Plus(a,b),CN1)))),And(And(FreeQ(List(a,b,c,d),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),PositiveQ(Plus(a,b))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§cos"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1D2),x_Symbol),
    Condition(Times(C2,Power(Times(d,Sqrt(Plus(a,b))),CN1),EllipticF(Times(C1D2,Plus(c,Times(d,x))),Times(C2,b,Power(Plus(a,b),CN1)))),And(And(FreeQ(List(a,b,c,d),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),PositiveQ(Plus(a,b))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1D2),x_Symbol),
    Condition(Times(C2,Power(Times(d,Sqrt(Plus(a,Times(CN1,b)))),CN1),EllipticF(Plus(Times(C1D4,Pi),Times(C1D2,Plus(c,Times(d,x)))),Times(CN2,b,Power(Plus(a,Times(CN1,b)),CN1)))),And(And(FreeQ(List(a,b,c,d),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),PositiveQ(Plus(a,Times(CN1,b)))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§cos"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1D2),x_Symbol),
    Condition(Times(C2,Power(Times(d,Sqrt(Plus(a,Times(CN1,b)))),CN1),EllipticF(Plus(Times(C1D2,Pi),Times(C1D2,Plus(c,Times(d,x)))),Times(CN2,b,Power(Plus(a,Times(CN1,b)),CN1)))),And(And(FreeQ(List(a,b,c,d),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),PositiveQ(Plus(a,Times(CN1,b)))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1D2),x_Symbol),
    Condition(Times(Sqrt(Times(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),Power(Plus(a,b),CN1))),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),CN1D2),Int(Power(Plus(Times(a,Power(Plus(a,b),CN1)),Times(b,Power(Plus(a,b),CN1),Sin(Plus(c,Times(d,x))))),CN1D2),x)),And(And(FreeQ(List(a,b,c,d),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),Not(PositiveQ(Plus(a,b)))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§cos"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1D2),x_Symbol),
    Condition(Times(Sqrt(Times(Plus(a,Times(b,Cos(Plus(c,Times(d,x))))),Power(Plus(a,b),CN1))),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,x))))),CN1D2),Int(Power(Plus(Times(a,Power(Plus(a,b),CN1)),Times(b,Power(Plus(a,b),CN1),Cos(Plus(c,Times(d,x))))),CN1D2),x)),And(And(FreeQ(List(a,b,c,d),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),Not(PositiveQ(Plus(a,b)))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Plus(Times(CN1,b,Cos(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),Plus(n,C1)),Power(Times(d,Plus(n,C1),Plus(Sqr(a),Times(CN1,Sqr(b)))),CN1)),Times(Power(Times(Plus(n,C1),Plus(Sqr(a),Times(CN1,Sqr(b)))),CN1),Int(Times(Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),Plus(n,C1)),Simp(Plus(Times(a,Plus(n,C1)),Times(CN1,b,Plus(n,C2),Sin(Plus(c,Times(d,x))))),x)),x))),And(And(And(And(FreeQ(List(a,b,c,d),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),RationalQ(n)),Less(n,CN1)),IntegerQ(Times(C2,n))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§cos"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Plus(Times(b,Sin(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,x))))),Plus(n,C1)),Power(Times(d,Plus(n,C1),Plus(Sqr(a),Times(CN1,Sqr(b)))),CN1)),Times(Power(Times(Plus(n,C1),Plus(Sqr(a),Times(CN1,Sqr(b)))),CN1),Int(Times(Power(Plus(a,Times(b,Cos(Plus(c,Times(d,x))))),Plus(n,C1)),Simp(Plus(Times(a,Plus(n,C1)),Times(CN1,b,Plus(n,C2),Cos(Plus(c,Times(d,x))))),x)),x))),And(And(And(And(FreeQ(List(a,b,c,d),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),RationalQ(n)),Less(n,CN1)),IntegerQ(Times(C2,n))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Times(Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),Plus(n,C1)),Sqrt(Times(b,Plus(C1,Times(CN1,Sin(Plus(c,Times(d,x))))),Power(Plus(a,b),CN1))),Sqrt(Times(CN1,b,Plus(C1,Sin(Plus(c,Times(d,x)))),Power(Plus(a,Times(CN1,b)),CN1))),Power(Times(b,d,Plus(n,C1),Cos(Plus(c,Times(d,x)))),CN1),AppellF1(Plus(n,C1),C1D2,C1D2,Plus(n,C2),Times(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),Power(Plus(a,Times(CN1,b)),CN1)),Times(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),Power(Plus(a,b),CN1)))),And(And(FreeQ(List(a,b,c,d,n),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),Not(IntegerQ(Times(C2,n)))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§cos"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Times(CN1,Power(Plus(a,Times(b,Cos(Plus(c,Times(d,x))))),Plus(n,C1)),Sqrt(Times(b,Plus(C1,Times(CN1,Cos(Plus(c,Times(d,x))))),Power(Plus(a,b),CN1))),Sqrt(Times(CN1,b,Plus(C1,Cos(Plus(c,Times(d,x)))),Power(Plus(a,Times(CN1,b)),CN1))),Power(Times(b,d,Plus(n,C1),Sin(Plus(c,Times(d,x)))),CN1),AppellF1(Plus(n,C1),C1D2,C1D2,Plus(n,C2),Times(Plus(a,Times(b,Cos(Plus(c,Times(d,x))))),Power(Plus(a,Times(CN1,b)),CN1)),Times(Plus(a,Times(b,Cos(Plus(c,Times(d,x))))),Power(Plus(a,b),CN1)))),And(And(FreeQ(List(a,b,c,d,n),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),Not(IntegerQ(Times(C2,n)))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),$($s("§cos"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Int(Power(Plus(a,Times(b,C1D2,Sin(Plus(Times(C2,c),Times(C2,d,x))))),n),x),FreeQ(List(a,b,c,d,n),x)))
  );
}
