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
public class IntRules68 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Sqr(Plus(a_,Times(b_DEFAULT,$($s("§sec"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT)))))),x_Symbol),
    Condition(Plus(Times(Sqr(a),x),Times(C2,a,b,Int(Sec(Plus(c,Times(pd,x))),x)),Times(Sqr(b),Int(Sqr(Sec(Plus(c,Times(pd,x)))),x))),FreeQ(List(a,b,c,pd),x))),
ISetDelayed(Int(Sqr(Plus(a_,Times(b_DEFAULT,$($s("§csc"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT)))))),x_Symbol),
    Condition(Plus(Times(Sqr(a),x),Times(C2,a,b,Int(Csc(Plus(c,Times(pd,x))),x)),Times(Sqr(b),Int(Sqr(Csc(Plus(c,Times(pd,x)))),x))),FreeQ(List(a,b,c,pd),x))),
ISetDelayed(Int(Sqrt(Plus(a_,Times(b_DEFAULT,$($s("§sec"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT)))))),x_Symbol),
    Condition(Times(C2,b,Power(pd,CN1),Subst(Int(Power(Plus(a,Sqr(x)),CN1),x),x,Times(b,Tan(Plus(c,Times(pd,x))),Power(Plus(a,Times(b,Sec(Plus(c,Times(pd,x))))),CN1D2)))),And(FreeQ(List(a,b,c,pd),x),ZeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))))),
ISetDelayed(Int(Sqrt(Plus(a_,Times(b_DEFAULT,$($s("§csc"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT)))))),x_Symbol),
    Condition(Times(CN2,b,Power(pd,CN1),Subst(Int(Power(Plus(a,Sqr(x)),CN1),x),x,Times(b,Cot(Plus(c,Times(pd,x))),Power(Plus(a,Times(b,Csc(Plus(c,Times(pd,x))))),CN1D2)))),And(FreeQ(List(a,b,c,pd),x),ZeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§sec"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),pn_),x_Symbol),
    Condition(Plus(Times(Sqr(b),Tan(Plus(c,Times(pd,x))),Power(Plus(a,Times(b,Sec(Plus(c,Times(pd,x))))),Plus(pn,Times(CN1,C2))),Power(Times(pd,Plus(pn,Times(CN1,C1))),CN1)),Times(a,Power(Plus(pn,Times(CN1,C1)),CN1),Int(Times(Plus(Times(a,Plus(pn,Times(CN1,C1))),Times(b,Plus(Times(C3,pn),Times(CN1,C4)),Sec(Plus(c,Times(pd,x))))),Power(Plus(a,Times(b,Sec(Plus(c,Times(pd,x))))),Plus(pn,Times(CN1,C2)))),x))),And(And(And(And(FreeQ(List(a,b,c,pd),x),ZeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),RationalQ(pn)),Greater(pn,C1)),IntegerQ(Times(C2,pn))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§csc"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),pn_),x_Symbol),
    Condition(Plus(Times(CN1,Sqr(b),Cot(Plus(c,Times(pd,x))),Power(Plus(a,Times(b,Csc(Plus(c,Times(pd,x))))),Plus(pn,Times(CN1,C2))),Power(Times(pd,Plus(pn,Times(CN1,C1))),CN1)),Times(a,Power(Plus(pn,Times(CN1,C1)),CN1),Int(Times(Plus(Times(a,Plus(pn,Times(CN1,C1))),Times(b,Plus(Times(C3,pn),Times(CN1,C4)),Csc(Plus(c,Times(pd,x))))),Power(Plus(a,Times(b,Csc(Plus(c,Times(pd,x))))),Plus(pn,Times(CN1,C2)))),x))),And(And(And(And(FreeQ(List(a,b,c,pd),x),ZeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),RationalQ(pn)),Greater(pn,C1)),IntegerQ(Times(C2,pn))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§sec"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),CN1D2),x_Symbol),
    Condition(Plus(Times(Power(a,CN1),Int(Sqrt(Plus(a,Times(b,Sec(Plus(c,Times(pd,x)))))),x)),Times(CN1,b,Power(a,CN1),Int(Times(Sec(Plus(c,Times(pd,x))),Power(Plus(a,Times(b,Sec(Plus(c,Times(pd,x))))),CN1D2)),x))),And(FreeQ(List(a,b,c,pd),x),ZeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§csc"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),CN1D2),x_Symbol),
    Condition(Plus(Times(Power(a,CN1),Int(Sqrt(Plus(a,Times(b,Csc(Plus(c,Times(pd,x)))))),x)),Times(CN1,b,Power(a,CN1),Int(Times(Csc(Plus(c,Times(pd,x))),Power(Plus(a,Times(b,Csc(Plus(c,Times(pd,x))))),CN1D2)),x))),And(FreeQ(List(a,b,c,pd),x),ZeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§sec"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),pn_),x_Symbol),
    Condition(Plus(Times(Tan(Plus(c,Times(pd,x))),Power(Plus(a,Times(b,Sec(Plus(c,Times(pd,x))))),pn),Power(Times(pd,Plus(Times(C2,pn),C1)),CN1)),Times(Power(Times(Sqr(a),Plus(Times(C2,pn),C1)),CN1),Int(Times(Plus(Times(a,Plus(Times(C2,pn),C1)),Times(CN1,b,Plus(pn,C1),Sec(Plus(c,Times(pd,x))))),Power(Plus(a,Times(b,Sec(Plus(c,Times(pd,x))))),Plus(pn,C1))),x))),And(And(And(And(FreeQ(List(a,b,c,pd),x),ZeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),RationalQ(pn)),LessEqual(pn,CN1)),IntegerQ(Times(C2,pn))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§csc"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),pn_),x_Symbol),
    Condition(Plus(Times(CN1,Cot(Plus(c,Times(pd,x))),Power(Plus(a,Times(b,Csc(Plus(c,Times(pd,x))))),pn),Power(Times(pd,Plus(Times(C2,pn),C1)),CN1)),Times(Power(Times(Sqr(a),Plus(Times(C2,pn),C1)),CN1),Int(Times(Plus(Times(a,Plus(Times(C2,pn),C1)),Times(CN1,b,Plus(pn,C1),Csc(Plus(c,Times(pd,x))))),Power(Plus(a,Times(b,Csc(Plus(c,Times(pd,x))))),Plus(pn,C1))),x))),And(And(And(And(FreeQ(List(a,b,c,pd),x),ZeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),RationalQ(pn)),LessEqual(pn,CN1)),IntegerQ(Times(C2,pn))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§sec"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),pn_),x_Symbol),
    Condition(Times(C2,a,Power(Plus(a,Times(b,Sec(Plus(c,Times(pd,x))))),pn),Tan(Times(C1D2,Plus(c,Times(pd,x)))),Power(Plus(C1,Times(CN1,Sqr(Tan(Times(C1D2,Plus(c,Times(pd,x))))))),pn),Power(Times(pd,Plus(a,Times(pn,Plus(a,Times(CN1,b))))),CN1),AppellF1(Plus(Times(pn,Plus(a,Times(CN1,b)),Power(Times(C2,a),CN1)),C1D2),pn,C1,Plus(Times(pn,Plus(a,Times(CN1,b)),Power(Times(C2,a),CN1)),QQ(3L,2L)),Sqr(Tan(Times(C1D2,Plus(c,Times(pd,x))))),Times(CN1,Sqr(Tan(Times(C1D2,Plus(c,Times(pd,x)))))))),And(And(FreeQ(List(a,b,c,pd,pn),x),ZeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),Not(IntegerQ(Times(C2,pn)))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§csc"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),pn_),x_Symbol),
    Condition(Times(CN2,a,Power(Plus(a,Times(b,Csc(Plus(c,Times(pd,x))))),pn),Cot(Plus(Times(C1D2,Plus(c,Times(pd,x))),Times(C1D4,Pi))),Power(Plus(C1,Times(CN1,Sqr(Cot(Plus(Times(C1D2,Plus(c,Times(pd,x))),Times(C1D4,Pi)))))),pn),Power(Times(pd,Plus(a,Times(pn,Plus(a,Times(CN1,b))))),CN1),AppellF1(Plus(Times(pn,Plus(a,Times(CN1,b)),Power(Times(C2,a),CN1)),C1D2),pn,C1,Plus(Times(pn,Plus(a,Times(CN1,b)),Power(Times(C2,a),CN1)),QQ(3L,2L)),Sqr(Cot(Plus(Times(C1D2,Plus(c,Times(pd,x))),Times(C1D4,Pi)))),Times(CN1,Sqr(Cot(Plus(Times(C1D2,Plus(c,Times(pd,x))),Times(C1D4,Pi))))))),And(And(FreeQ(List(a,b,c,pd,pn),x),ZeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),Not(IntegerQ(Times(C2,pn)))))),
ISetDelayed(Int(Sqrt(Plus(a_,Times(b_DEFAULT,$($s("§sec"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT)))))),x_Symbol),
    Condition(Times(Sqrt(Cos(Plus(c,Times(pd,x)))),Sqrt(Plus(a,Times(b,Sec(Plus(c,Times(pd,x)))))),Power(Plus(b,Times(a,Cos(Plus(c,Times(pd,x))))),CN1D2),Int(Times(Sqrt(Plus(b,Times(a,Cos(Plus(c,Times(pd,x)))))),Power(Cos(Plus(c,Times(pd,x))),CN1D2)),x)),And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))))),
ISetDelayed(Int(Sqrt(Plus(a_,Times(b_DEFAULT,$($s("§csc"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT)))))),x_Symbol),
    Condition(Times(Sqrt(Sin(Plus(c,Times(pd,x)))),Sqrt(Plus(a,Times(b,Csc(Plus(c,Times(pd,x)))))),Power(Plus(b,Times(a,Sin(Plus(c,Times(pd,x))))),CN1D2),Int(Times(Sqrt(Plus(b,Times(a,Sin(Plus(c,Times(pd,x)))))),Power(Sin(Plus(c,Times(pd,x))),CN1D2)),x)),And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§sec"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),QQ(3L,2L)),x_Symbol),
    Condition(Plus(Int(Times(Plus(Sqr(a),Times(b,Plus(Times(C2,a),Times(CN1,b)),Sec(Plus(c,Times(pd,x))))),Power(Plus(a,Times(b,Sec(Plus(c,Times(pd,x))))),CN1D2)),x),Times(Sqr(b),Int(Times(Sec(Plus(c,Times(pd,x))),Plus(C1,Sec(Plus(c,Times(pd,x)))),Power(Plus(a,Times(b,Sec(Plus(c,Times(pd,x))))),CN1D2)),x))),And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§csc"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),QQ(3L,2L)),x_Symbol),
    Condition(Plus(Int(Times(Plus(Sqr(a),Times(b,Plus(Times(C2,a),Times(CN1,b)),Csc(Plus(c,Times(pd,x))))),Power(Plus(a,Times(b,Csc(Plus(c,Times(pd,x))))),CN1D2)),x),Times(Sqr(b),Int(Times(Csc(Plus(c,Times(pd,x))),Plus(C1,Csc(Plus(c,Times(pd,x)))),Power(Plus(a,Times(b,Csc(Plus(c,Times(pd,x))))),CN1D2)),x))),And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§sec"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),pn_),x_Symbol),
    Condition(Plus(Times(Sqr(b),Tan(Plus(c,Times(pd,x))),Power(Plus(a,Times(b,Sec(Plus(c,Times(pd,x))))),Plus(pn,Times(CN1,C2))),Power(Times(pd,Plus(pn,Times(CN1,C1))),CN1)),Times(Power(Plus(pn,Times(CN1,C1)),CN1),Int(Times(Simp(Plus(Times(Power(a,C3),Plus(pn,Times(CN1,C1))),Times(b,Plus(Times(Sqr(b),Plus(pn,Times(CN1,C2))),Times(C3,Sqr(a),Plus(pn,Times(CN1,C1)))),Sec(Plus(c,Times(pd,x)))),Times(a,Sqr(b),Plus(Times(C3,pn),Times(CN1,C4)),Sqr(Sec(Plus(c,Times(pd,x)))))),x),Power(Plus(a,Times(b,Sec(Plus(c,Times(pd,x))))),Plus(pn,Times(CN1,C3)))),x))),And(And(And(And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),RationalQ(pn)),Greater(pn,C2)),IntegerQ(Times(C2,pn))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§csc"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),pn_),x_Symbol),
    Condition(Plus(Times(CN1,Sqr(b),Cot(Plus(c,Times(pd,x))),Power(Plus(a,Times(b,Csc(Plus(c,Times(pd,x))))),Plus(pn,Times(CN1,C2))),Power(Times(pd,Plus(pn,Times(CN1,C1))),CN1)),Times(Power(Plus(pn,Times(CN1,C1)),CN1),Int(Times(Simp(Plus(Times(Power(a,C3),Plus(pn,Times(CN1,C1))),Times(b,Plus(Times(Sqr(b),Plus(pn,Times(CN1,C2))),Times(C3,Sqr(a),Plus(pn,Times(CN1,C1)))),Csc(Plus(c,Times(pd,x)))),Times(a,Sqr(b),Plus(Times(C3,pn),Times(CN1,C4)),Sqr(Csc(Plus(c,Times(pd,x)))))),x),Power(Plus(a,Times(b,Csc(Plus(c,Times(pd,x))))),Plus(pn,Times(CN1,C3)))),x))),And(And(And(And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),RationalQ(pn)),Greater(pn,C2)),IntegerQ(Times(C2,pn))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§sec"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),CN1),x_Symbol),
    Condition(Plus(Times(x,Power(a,CN1)),Times(CN1,Power(a,CN1),Int(Power(Plus(C1,Times(a,Power(b,CN1),Cos(Plus(c,Times(pd,x))))),CN1),x))),And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§csc"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),CN1),x_Symbol),
    Condition(Plus(Times(x,Power(a,CN1)),Times(CN1,Power(a,CN1),Int(Power(Plus(C1,Times(a,Power(b,CN1),Sin(Plus(c,Times(pd,x))))),CN1),x))),And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§sec"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),CN1D2),x_Symbol),
    Condition(Times(Sqrt(Plus(b,Times(a,Cos(Plus(c,Times(pd,x)))))),Power(Times(Sqrt(Cos(Plus(c,Times(pd,x)))),Sqrt(Plus(a,Times(b,Sec(Plus(c,Times(pd,x))))))),CN1),Int(Times(Sqrt(Cos(Plus(c,Times(pd,x)))),Power(Plus(b,Times(a,Cos(Plus(c,Times(pd,x))))),CN1D2)),x)),And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§csc"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),CN1D2),x_Symbol),
    Condition(Times(Sqrt(Plus(b,Times(a,Sin(Plus(c,Times(pd,x)))))),Power(Times(Sqrt(Sin(Plus(c,Times(pd,x)))),Sqrt(Plus(a,Times(b,Csc(Plus(c,Times(pd,x))))))),CN1),Int(Times(Sqrt(Sin(Plus(c,Times(pd,x)))),Power(Plus(b,Times(a,Sin(Plus(c,Times(pd,x))))),CN1D2)),x)),And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§sec"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),pn_),x_Symbol),
    Condition(Plus(Times(CN1,Sqr(b),Tan(Plus(c,Times(pd,x))),Power(Plus(a,Times(b,Sec(Plus(c,Times(pd,x))))),Plus(pn,C1)),Power(Times(a,pd,Plus(pn,C1),Plus(Sqr(a),Times(CN1,Sqr(b)))),CN1)),Times(Power(Times(a,Plus(pn,C1),Plus(Sqr(a),Times(CN1,Sqr(b)))),CN1),Int(Times(Simp(Plus(Times(Plus(Sqr(a),Times(CN1,Sqr(b))),Plus(pn,C1)),Times(CN1,a,b,Plus(pn,C1),Sec(Plus(c,Times(pd,x)))),Times(Sqr(b),Plus(pn,C2),Sqr(Sec(Plus(c,Times(pd,x)))))),x),Power(Plus(a,Times(b,Sec(Plus(c,Times(pd,x))))),Plus(pn,C1))),x))),And(And(And(And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),RationalQ(pn)),Less(pn,CN1)),IntegerQ(Times(C2,pn))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§csc"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),pn_),x_Symbol),
    Condition(Plus(Times(Sqr(b),Cot(Plus(c,Times(pd,x))),Power(Plus(a,Times(b,Csc(Plus(c,Times(pd,x))))),Plus(pn,C1)),Power(Times(a,pd,Plus(pn,C1),Plus(Sqr(a),Times(CN1,Sqr(b)))),CN1)),Times(Power(Times(a,Plus(pn,C1),Plus(Sqr(a),Times(CN1,Sqr(b)))),CN1),Int(Times(Simp(Plus(Times(Plus(Sqr(a),Times(CN1,Sqr(b))),Plus(pn,C1)),Times(CN1,a,b,Plus(pn,C1),Csc(Plus(c,Times(pd,x)))),Times(Sqr(b),Plus(pn,C2),Sqr(Csc(Plus(c,Times(pd,x)))))),x),Power(Plus(a,Times(b,Csc(Plus(c,Times(pd,x))))),Plus(pn,C1))),x))),And(And(And(And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),RationalQ(pn)),Less(pn,CN1)),IntegerQ(Times(C2,pn))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§sec"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),pn_),x_Symbol),
    Condition($(Defer($s("Int")),Power(Plus(a,Times(b,Sec(Plus(c,Times(pd,x))))),pn),x),And(And(FreeQ(List(a,b,c,pd,pn),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),Not(IntegerQ(Times(C2,pn)))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§csc"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),pn_),x_Symbol),
    Condition($(Defer($s("Int")),Power(Plus(a,Times(b,Csc(Plus(c,Times(pd,x))))),pn),x),And(And(FreeQ(List(a,b,c,pd,pn),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),Not(IntegerQ(Times(C2,pn))))))
  );
}
