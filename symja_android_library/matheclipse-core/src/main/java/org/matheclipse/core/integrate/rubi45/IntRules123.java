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
public class IntRules123 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Times(Power(ArcSinh(Times(Power(x_,p_),a_DEFAULT)),pn_DEFAULT),Power(x_,CN1)),x_Symbol),
    Condition(Times(Power(p,CN1),Subst(Int(Times(Power(x,pn),Coth(x)),x),x,ArcSinh(Times(a,Power(x,p))))),And(And(FreeQ(List(a,p),x),IntegerQ(pn)),Greater(pn,C0)))),
ISetDelayed(Int(Times(Power(ArcCosh(Times(Power(x_,p_),a_DEFAULT)),pn_DEFAULT),Power(x_,CN1)),x_Symbol),
    Condition(Times(Power(p,CN1),Subst(Int(Times(Power(x,pn),Tanh(x)),x),x,ArcCosh(Times(a,Power(x,p))))),And(And(FreeQ(List(a,p),x),IntegerQ(pn)),Greater(pn,C0)))),
ISetDelayed(Int(Times(Power(ArcSinh(Times(Power(Plus(Times(Power(x_,pn_DEFAULT),b_DEFAULT),a_DEFAULT),CN1),c_DEFAULT)),m_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Int(Times(u,Power(ArcCsch(Plus(Times(a,Power(c,CN1)),Times(b,Power(x,pn),Power(c,CN1)))),m)),x),FreeQ(List(a,b,c,pn,m),x))),
ISetDelayed(Int(Times(Power(ArcCosh(Times(Power(Plus(Times(Power(x_,pn_DEFAULT),b_DEFAULT),a_DEFAULT),CN1),c_DEFAULT)),m_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Int(Times(u,Power(ArcSech(Plus(Times(a,Power(c,CN1)),Times(b,Power(x,pn),Power(c,CN1)))),m)),x),FreeQ(List(a,b,c,pn,m),x))),
ISetDelayed(Int(Power(f_,Times(Power(ArcSinh(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pn_DEFAULT),c_DEFAULT)),x_Symbol),
    Condition(Times(Power(b,CN1),Subst(Int(Times(Power(f,Times(c,Power(x,pn))),Cosh(x)),x),x,ArcSinh(Plus(a,Times(b,x))))),And(FreeQ(List(a,b,c,f),x),PositiveIntegerQ(pn)))),
ISetDelayed(Int(Power(f_,Times(Power(ArcCosh(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pn_DEFAULT),c_DEFAULT)),x_Symbol),
    Condition(Times(Power(b,CN1),Subst(Int(Times(Power(f,Times(c,Power(x,pn))),Sinh(x)),x),x,ArcCosh(Plus(a,Times(b,x))))),And(FreeQ(List(a,b,c,f),x),PositiveIntegerQ(pn)))),
ISetDelayed(Int(Times(Power(f_,Times(Power(ArcSinh(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pn_DEFAULT),c_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Times(Power(b,CN1),Subst(Int(Times(Power(Plus(Times(CN1,a,Power(b,CN1)),Times(Sinh(x),Power(b,CN1))),m),Power(f,Times(c,Power(x,pn))),Cosh(x)),x),x,ArcSinh(Plus(a,Times(b,x))))),And(FreeQ(List(a,b,c,f),x),PositiveIntegerQ(m,pn)))),
ISetDelayed(Int(Times(Power(f_,Times(Power(ArcCosh(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pn_DEFAULT),c_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Times(Power(b,CN1),Subst(Int(Times(Power(Plus(Times(CN1,a,Power(b,CN1)),Times(Cosh(x),Power(b,CN1))),m),Power(f,Times(c,Power(x,pn))),Sinh(x)),x),x,ArcCosh(Plus(a,Times(b,x))))),And(FreeQ(List(a,b,c,f),x),PositiveIntegerQ(m,pn)))),
ISetDelayed(Int(ArcSinh(u_),x_Symbol),
    Condition(Plus(Times(x,ArcSinh(u)),Times(CN1,Int(SimplifyIntegrand(Times(x,D(u,x),Power(Plus(C1,Sqr(u)),CN1D2)),x),x))),And(InverseFunctionFreeQ(u,x),Not(FunctionOfExponentialQ(u,x))))),
ISetDelayed(Int(ArcCosh(u_),x_Symbol),
    Condition(Plus(Times(x,ArcCosh(u)),Times(CN1,Int(SimplifyIntegrand(Times(x,D(u,x),Power(Times(Sqrt(Plus(CN1,u)),Sqrt(Plus(C1,u))),CN1)),x),x))),And(InverseFunctionFreeQ(u,x),Not(FunctionOfExponentialQ(u,x))))),
ISetDelayed(Int(Times(Plus(Times(ArcSinh(u_),b_DEFAULT),a_DEFAULT),Power(Plus(Times(x_,pd_DEFAULT),c_DEFAULT),m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Power(Plus(c,Times(pd,x)),Plus(m,C1)),Plus(a,Times(b,ArcSinh(u))),Power(Times(pd,Plus(m,C1)),CN1)),Times(CN1,b,Power(Times(pd,Plus(m,C1)),CN1),Int(SimplifyIntegrand(Times(Power(Plus(c,Times(pd,x)),Plus(m,C1)),D(u,x),Power(Plus(C1,Sqr(u)),CN1D2)),x),x))),And(And(And(And(FreeQ(List(a,b,c,pd,m),x),NonzeroQ(Plus(m,C1))),InverseFunctionFreeQ(u,x)),Not(FunctionOfQ(Power(Plus(c,Times(pd,x)),Plus(m,C1)),u,x))),Not(FunctionOfExponentialQ(u,x))))),
ISetDelayed(Int(Times(Plus(Times(ArcCosh(u_),b_DEFAULT),a_DEFAULT),Power(Plus(Times(x_,pd_DEFAULT),c_DEFAULT),m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Power(Plus(c,Times(pd,x)),Plus(m,C1)),Plus(a,Times(b,ArcCosh(u))),Power(Times(pd,Plus(m,C1)),CN1)),Times(CN1,b,Power(Times(pd,Plus(m,C1)),CN1),Int(SimplifyIntegrand(Times(Power(Plus(c,Times(pd,x)),Plus(m,C1)),D(u,x),Power(Times(Sqrt(Plus(CN1,u)),Sqrt(Plus(C1,u))),CN1)),x),x))),And(And(And(And(FreeQ(List(a,b,c,pd,m),x),NonzeroQ(Plus(m,C1))),InverseFunctionFreeQ(u,x)),Not(FunctionOfQ(Power(Plus(c,Times(pd,x)),Plus(m,C1)),u,x))),Not(FunctionOfExponentialQ(u,x))))),
ISetDelayed(Int(Times(Plus(Times(ArcSinh(u_),b_DEFAULT),a_DEFAULT),v_),x_Symbol),
    Condition(Module(List(Set(w,Block(List(Set($s("§showsteps"),False),Set($s("§stepcounter"),Null)),Int(v,x)))),Condition(Plus(Dist(Plus(a,Times(b,ArcSinh(u))),w,x),Times(CN1,b,Int(SimplifyIntegrand(Times(w,D(u,x),Power(Plus(C1,Sqr(u)),CN1D2)),x),x))),InverseFunctionFreeQ(w,x))),And(And(FreeQ(List(a,b),x),InverseFunctionFreeQ(u,x)),Not(MatchQ(v,Condition(Power(Plus(c_DEFAULT,Times(pd_DEFAULT,x)),m_DEFAULT),FreeQ(List(c,pd,m),x))))))),
ISetDelayed(Int(Times(Plus(Times(ArcCosh(u_),b_DEFAULT),a_DEFAULT),v_),x_Symbol),
    Condition(Module(List(Set(w,Block(List(Set($s("§showsteps"),False),Set($s("§stepcounter"),Null)),Int(v,x)))),Condition(Plus(Dist(Plus(a,Times(b,ArcCosh(u))),w,x),Times(CN1,b,Int(SimplifyIntegrand(Times(w,D(u,x),Power(Times(Sqrt(Plus(CN1,u)),Sqrt(Plus(C1,u))),CN1)),x),x))),InverseFunctionFreeQ(w,x))),And(And(FreeQ(List(a,b),x),InverseFunctionFreeQ(u,x)),Not(MatchQ(v,Condition(Power(Plus(c_DEFAULT,Times(pd_DEFAULT,x)),m_DEFAULT),FreeQ(List(c,pd,m),x))))))),
ISetDelayed(Int(Power(E,Times(ArcSinh(u_),pn_DEFAULT)),x_Symbol),
    Condition(Int(Power(Plus(u,Sqrt(Plus(C1,Sqr(u)))),pn),x),And(IntegerQ(pn),PolynomialQ(u,x)))),
ISetDelayed(Int(Times(Power(E,Times(ArcSinh(u_),pn_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(x,m),Power(Plus(u,Sqrt(Plus(C1,Sqr(u)))),pn)),x),And(And(RationalQ(m),IntegerQ(pn)),PolynomialQ(u,x)))),
ISetDelayed(Int(Power(E,Times(ArcCosh(u_),pn_DEFAULT)),x_Symbol),
    Condition(Int(Power(Plus(u,Times(Sqrt(Plus(CN1,u)),Sqrt(Plus(C1,u)))),pn),x),And(IntegerQ(pn),PolynomialQ(u,x)))),
ISetDelayed(Int(Times(Power(E,Times(ArcCosh(u_),pn_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(x,m),Power(Plus(u,Times(Sqrt(Plus(CN1,u)),Sqrt(Plus(C1,u)))),pn)),x),And(And(RationalQ(m),IntegerQ(pn)),PolynomialQ(u,x))))
  );
}
