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
public class IntRules127 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Times(Plus(Times(ArcSech(u_),b_DEFAULT),a_DEFAULT),v_),x_Symbol),
    Condition(Module(List(Set(w,Block(List(Set($s("§showsteps"),False),Set($s("§stepcounter"),Null)),Int(v,x)))),Condition(Plus(Dist(Plus(a,Times(b,ArcSech(u))),w,x),Times(b,Sqrt(Plus(C1,Times(CN1,Sqr(u)))),Power(Times(u,Sqrt(Plus(CN1,Power(u,CN1))),Sqrt(Plus(C1,Power(u,CN1)))),CN1),Int(SimplifyIntegrand(Times(w,D(u,x),Power(Times(u,Sqrt(Plus(C1,Times(CN1,Sqr(u))))),CN1)),x),x))),InverseFunctionFreeQ(w,x))),And(And(FreeQ(List(a,b),x),InverseFunctionFreeQ(u,x)),Not(MatchQ(v,Condition(Power(Plus(c_DEFAULT,Times(pd_DEFAULT,x)),m_DEFAULT),FreeQ(List(c,pd,m),x))))))),
ISetDelayed(Int(Times(Plus(Times(ArcCsch(u_),b_DEFAULT),a_DEFAULT),v_),x_Symbol),
    Condition(Module(List(Set(w,Block(List(Set($s("§showsteps"),False),Set($s("§stepcounter"),Null)),Int(v,x)))),Condition(Plus(Dist(Plus(a,Times(b,ArcCsch(u))),w,x),Times(CN1,b,u,Power(Times(CN1,Sqr(u)),CN1D2),Int(SimplifyIntegrand(Times(w,D(u,x),Power(Times(u,Sqrt(Plus(CN1,Times(CN1,Sqr(u))))),CN1)),x),x))),InverseFunctionFreeQ(w,x))),And(And(FreeQ(List(a,b),x),InverseFunctionFreeQ(u,x)),Not(MatchQ(v,Condition(Power(Plus(c_DEFAULT,Times(pd_DEFAULT,x)),m_DEFAULT),FreeQ(List(c,pd,m),x)))))))
  );
}
