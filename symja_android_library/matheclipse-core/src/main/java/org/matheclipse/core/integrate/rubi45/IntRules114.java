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
public class IntRules114 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Times(ArcTan(v_),Log(w_),u_),x_Symbol),
    Condition(Module(List(Set(z,Block(List(Set($s("§showsteps"),False),Set($s("§stepcounter"),Null)),Int(u,x)))),Condition(Plus(Dist(Times(ArcTan(v),Log(w)),z,x),Times(CN1,Int(SimplifyIntegrand(Times(z,Log(w),D(v,x),Power(Plus(C1,Sqr(v)),CN1)),x),x)),Times(CN1,Int(SimplifyIntegrand(Times(z,ArcTan(v),D(w,x),Power(w,CN1)),x),x))),InverseFunctionFreeQ(z,x))),And(InverseFunctionFreeQ(v,x),InverseFunctionFreeQ(w,x)))),
ISetDelayed(Int(Times(ArcCot(v_),Log(w_),u_),x_Symbol),
    Condition(Module(List(Set(z,Block(List(Set($s("§showsteps"),False),Set($s("§stepcounter"),Null)),Int(u,x)))),Condition(Plus(Dist(Times(ArcCot(v),Log(w)),z,x),Int(SimplifyIntegrand(Times(z,Log(w),D(v,x),Power(Plus(C1,Sqr(v)),CN1)),x),x),Times(CN1,Int(SimplifyIntegrand(Times(z,ArcCot(v),D(w,x),Power(w,CN1)),x),x))),InverseFunctionFreeQ(z,x))),And(InverseFunctionFreeQ(v,x),InverseFunctionFreeQ(w,x))))
  );
}
