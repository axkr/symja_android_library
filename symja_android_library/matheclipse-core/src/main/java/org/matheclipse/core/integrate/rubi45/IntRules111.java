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
public class IntRules111 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Times(Power(x_,CN1),Power(ArcSin(Times(a_DEFAULT,Power(x_,p_))),n_DEFAULT)),x_Symbol),
    Condition(Times(Power(p,CN1),Subst(Int(Times(Power(x,n),Cot(x)),x),x,ArcSin(Times(a,Power(x,p))))),And(And(FreeQ(List(a,p),x),IntegerQ(n)),Greater(n,C0)))),
ISetDelayed(Int(Times(Power(x_,CN1),Power(ArcCos(Times(a_DEFAULT,Power(x_,p_))),n_DEFAULT)),x_Symbol),
    Condition(Times(CN1,Power(p,CN1),Subst(Int(Times(Power(x,n),Tan(x)),x),x,ArcCos(Times(a,Power(x,p))))),And(And(FreeQ(List(a,p),x),IntegerQ(n)),Greater(n,C0)))),
ISetDelayed(Int(Times(u_DEFAULT,Power(ArcSin(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),CN1))),m_DEFAULT)),x_Symbol),
    Condition(Int(Times(u,Power(ArcCsc(Plus(Times(a,Power(c,CN1)),Times(b,Power(x,n),Power(c,CN1)))),m)),x),FreeQ(List(a,b,c,n,m),x))),
ISetDelayed(Int(Times(u_DEFAULT,Power(ArcCos(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),CN1))),m_DEFAULT)),x_Symbol),
    Condition(Int(Times(u,Power(ArcSec(Plus(Times(a,Power(c,CN1)),Times(b,Power(x,n),Power(c,CN1)))),m)),x),FreeQ(List(a,b,c,n,m),x))),
ISetDelayed(Int(Times(u_DEFAULT,Power(f_,Times(c_DEFAULT,Power(ArcSin(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)))),x_Symbol),
    Condition(Times(Power(b,CN1),Subst(Int(Times(ReplaceAll(u,Rule(x,Plus(Times(CN1,a,Power(b,CN1)),Times(Sin(x),Power(b,CN1))))),Power(f,Times(c,Power(x,n))),Cos(x)),x),x,ArcSin(Plus(a,Times(b,x))))),And(FreeQ(List(a,b,c,f),x),PositiveIntegerQ(n)))),
ISetDelayed(Int(Times(u_DEFAULT,Power(f_,Times(c_DEFAULT,Power(ArcCos(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)))),x_Symbol),
    Condition(Times(CN1,Power(b,CN1),Subst(Int(Times(ReplaceAll(u,Rule(x,Plus(Times(CN1,a,Power(b,CN1)),Times(Cos(x),Power(b,CN1))))),Power(f,Times(c,Power(x,n))),Sin(x)),x),x,ArcCos(Plus(a,Times(b,x))))),And(FreeQ(List(a,b,c,f),x),PositiveIntegerQ(n)))),
ISetDelayed(Int(ArcSin(u_),x_Symbol),
    Condition(Plus(Times(x,ArcSin(u)),Times(CN1,Int(SimplifyIntegrand(Times(x,D(u,x),Power(Plus(C1,Times(CN1,Sqr(u))),CN1D2)),x),x))),And(InverseFunctionFreeQ(u,x),Not(FunctionOfExponentialQ(u,x))))),
ISetDelayed(Int(ArcCos(u_),x_Symbol),
    Condition(Plus(Times(x,ArcCos(u)),Int(SimplifyIntegrand(Times(x,D(u,x),Power(Plus(C1,Times(CN1,Sqr(u))),CN1D2)),x),x)),And(InverseFunctionFreeQ(u,x),Not(FunctionOfExponentialQ(u,x))))),
ISetDelayed(Int(Times(Plus(a_DEFAULT,Times(b_DEFAULT,ArcSin(u_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Plus(a,Times(b,ArcSin(u))),Power(Times(d,Plus(m,C1)),CN1)),Times(CN1,b,Power(Times(d,Plus(m,C1)),CN1),Int(SimplifyIntegrand(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),D(u,x),Power(Plus(C1,Times(CN1,Sqr(u))),CN1D2)),x),x))),And(And(And(And(FreeQ(List(a,b,c,d,m),x),NonzeroQ(Plus(m,C1))),InverseFunctionFreeQ(u,x)),Not(FunctionOfQ(Power(Plus(c,Times(d,x)),Plus(m,C1)),u,x))),Not(FunctionOfExponentialQ(u,x))))),
ISetDelayed(Int(Times(Plus(a_DEFAULT,Times(b_DEFAULT,ArcCos(u_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Plus(a,Times(b,ArcCos(u))),Power(Times(d,Plus(m,C1)),CN1)),Times(b,Power(Times(d,Plus(m,C1)),CN1),Int(SimplifyIntegrand(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),D(u,x),Power(Plus(C1,Times(CN1,Sqr(u))),CN1D2)),x),x))),And(And(And(And(FreeQ(List(a,b,c,d,m),x),NonzeroQ(Plus(m,C1))),InverseFunctionFreeQ(u,x)),Not(FunctionOfQ(Power(Plus(c,Times(d,x)),Plus(m,C1)),u,x))),Not(FunctionOfExponentialQ(u,x))))),
ISetDelayed(Int(Times(v_,Plus(a_DEFAULT,Times(b_DEFAULT,ArcSin(u_)))),x_Symbol),
    Condition(Module(List(Set(w,Block(List(Set($s("§showsteps"),False),Set($s("§stepcounter"),Null)),Int(v,x)))),Condition(Plus(Dist(Plus(a,Times(b,ArcSin(u))),w,x),Times(CN1,b,Int(SimplifyIntegrand(Times(w,D(u,x),Power(Plus(C1,Times(CN1,Sqr(u))),CN1D2)),x),x))),InverseFunctionFreeQ(w,x))),And(And(FreeQ(List(a,b),x),InverseFunctionFreeQ(u,x)),Not(MatchQ(v,Condition(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x)),m_DEFAULT),FreeQ(List(c,d,m),x))))))),
ISetDelayed(Int(Times(v_,Plus(a_DEFAULT,Times(b_DEFAULT,ArcCos(u_)))),x_Symbol),
    Condition(Module(List(Set(w,Block(List(Set($s("§showsteps"),False),Set($s("§stepcounter"),Null)),Int(v,x)))),Condition(Plus(Dist(Plus(a,Times(b,ArcCos(u))),w,x),Times(b,Int(SimplifyIntegrand(Times(w,D(u,x),Power(Plus(C1,Times(CN1,Sqr(u))),CN1D2)),x),x))),InverseFunctionFreeQ(w,x))),And(And(FreeQ(List(a,b),x),InverseFunctionFreeQ(u,x)),Not(MatchQ(v,Condition(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x)),m_DEFAULT),FreeQ(List(c,d,m),x)))))))
  );
}
