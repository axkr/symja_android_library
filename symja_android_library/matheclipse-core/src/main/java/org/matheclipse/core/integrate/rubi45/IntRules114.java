package org.matheclipse.core.integrate.rubi45;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi45.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi45.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *  
 */
public class IntRules114 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Times(u_,ArcTan(v_),Log(w_)),x_Symbol),
    Condition(Module(List(Set(z,Block(List(Set($s("§showsteps"),False),Set($s("§stepcounter"),Null)),Int(u,x)))),Condition(Plus(Dist(Times(ArcTan(v),Log(w)),z,x),Negate(Int(SimplifyIntegrand(Times(z,Log(w),D(v,x),Power(Plus(C1,Sqr(v)),-1)),x),x)),Negate(Int(SimplifyIntegrand(Times(z,ArcTan(v),D(w,x),Power(w,-1)),x),x))),InverseFunctionFreeQ(z,x))),And(InverseFunctionFreeQ(v,x),InverseFunctionFreeQ(w,x)))),
ISetDelayed(Int(Times(u_,ArcCot(v_),Log(w_)),x_Symbol),
    Condition(Module(List(Set(z,Block(List(Set($s("§showsteps"),False),Set($s("§stepcounter"),Null)),Int(u,x)))),Condition(Plus(Dist(Times(ArcCot(v),Log(w)),z,x),Int(SimplifyIntegrand(Times(z,Log(w),D(v,x),Power(Plus(C1,Sqr(v)),-1)),x),x),Negate(Int(SimplifyIntegrand(Times(z,ArcCot(v),D(w,x),Power(w,-1)),x),x))),InverseFunctionFreeQ(z,x))),And(InverseFunctionFreeQ(v,x),InverseFunctionFreeQ(w,x))))
  );
}
