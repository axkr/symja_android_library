package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctions.*;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
/** 
 * IndefiniteIntegrationRules rules from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *  
 */
public class IndefiniteIntegrationRules16 { 
  public static IAST RULES = List( 
SetDelayed(Int(u_,x_Symbol),
    Module(List(Set($s("lst"),SubstForFractionalPowerOfLinear(u,x))),Condition(Dist(Times(Part($s("lst"),C2),Part($s("lst"),C4)),Subst(Int(Part($s("lst"),C1),x),x,Power(Part($s("lst"),C3),Power(Part($s("lst"),C2),CN1)))),NotFalseQ($s("lst"))))),
SetDelayed(Int(Times(Power(Plus(Times(Power(v_,C2),b_DEFAULT),a_),CN1),u_DEFAULT),x_Symbol),
    Condition(Plus(Dist(Rational(C1,C2),Int(Times(u,Power(Plus(a,Times(b,Rt(Times(CN1,a,Power(b,CN1)),C2),v)),CN1)),x)),Dist(Rational(C1,C2),Int(Times(u,Power(Plus(a,Times(CN1,b,Rt(Times(CN1,a,Power(b,CN1)),C2),v)),CN1)),x))),FreeQ(List(a,b),x))),
SetDelayed(Int(Times(Power(Plus(Times(Power(v_,C2),b_DEFAULT),a_),m_),u_DEFAULT),x_Symbol),
    Condition(Dist(Power(a,m),Int(Times(u,Power(Plus(C1,Times(Rt(Times(CN1,b,Power(a,CN1)),C2),v)),m),Power(Plus(C1,Times(CN1,Rt(Times(CN1,b,Power(a,CN1)),C2),v)),m)),x)),And(And(FreeQ(List(a,b),x),IntIntegerQ(m)),Or(Less(m,CN1),And(Equal(m,CN1),PositiveQ(Times(CN1,b,Power(a,CN1)))))))),
SetDelayed(Int(Times(Power(f_,Plus(a_,v_)),Power(g_,Plus(b_,w_)),u_DEFAULT),x_Symbol),
    Condition(Dist(Times(Power(f,a),Power(g,b)),Int(Times(u,Power(f,v),Power(g,w)),x)),And(And(FreeQ(List(a,b,f,g),x),Not(MatchQ(v,Condition(Plus(c_,t_),FreeQ(c,x))))),Not(MatchQ(w,Condition(Plus(c_,t_),FreeQ(c,x))))))),
SetDelayed(Int(Times(Power(f_,Plus(a_,v_)),u_DEFAULT),x_Symbol),
    Condition(Dist(Power(f,a),Int(Times(u,Power(f,v)),x)),And(FreeQ(List(a,f),x),Not(MatchQ(v,Condition(Plus(b_,w_),FreeQ(b,x))))))),
SetDelayed(Int(u_,x_Symbol),
    Condition(Dist(C2,Subst(Int(Regularize(Times(SubstForTrig(u,Times(C2,x,Power(Plus(C1,Power(x,C2)),CN1)),Times(Plus(C1,Times(CN1,Power(x,C2))),Power(Plus(C1,Power(x,C2)),CN1)),x,x),Power(Plus(C1,Power(x,C2)),CN1)),x),x),x,Tan(Times(Rational(C1,C2),x)))),FunctionOfTrigQ(u,x,x))),
SetDelayed(Int(u_,x_Symbol),
    Condition(Dist(C2,Subst(Int(Regularize(Times(SubstForHyperbolic(u,Times(C2,x,Power(Plus(C1,Times(CN1,Power(x,C2))),CN1)),Times(Plus(C1,Power(x,C2)),Power(Plus(C1,Times(CN1,Power(x,C2))),CN1)),x,x),Power(Plus(C1,Times(CN1,Power(x,C2))),CN1)),x),x),x,Tanh(Times(Rational(C1,C2),x)))),FunctionOfHyperbolicQ(u,x,x))),
SetDelayed(Int(u_,x_Symbol),
    Module(List(Set($s("lst"),FunctionOfSquareRootOfQuadratic(u,x))),Condition(Dist(C2,Subst(Int(Part($s("lst"),C1),x),x,Part($s("lst"),C2))),Not(FalseQ($s("lst")))))),
SetDelayed(Int(u_,x_Symbol),
    Module(List(Set($s("lst"),FunctionOfSquareRootOfQuadratic(u,x))),Condition(Dist(C2,Subst(Int(Part($s("lst"),C1),x),x,Part($s("lst"),C2))),Not(FalseQ($s("lst")))))),
SetDelayed(Int(u_,x_Symbol),
    Module(List(Set($s("lst"),FunctionOfSquareRootOfQuadratic(u,x))),Condition(Dist(C2,Subst(Int(Part($s("lst"),C1),x),x,Part($s("lst"),C2))),Not(FalseQ($s("lst")))))),
SetDelayed(Int(Times(Power(Plus(C1,Times(CN1,Power(Plus(Times(b_DEFAULT,x_),a_DEFAULT),C2))),n_DEFAULT),u_),x_Symbol),
    Condition(Module(List(Set($s("tmp"),InverseFunctionOfLinear(u,x))),Condition(Dist(Power(b,CN1),Subst(Int(Regularize(Times(SubstForInverseFunction(u,$s("tmp"),x),Power(Cos(x),Plus(Times(C2,n),C1))),x),x),x,$s("tmp"))),And(NotFalseQ($s("tmp")),SameQ($s("tmp"),ArcSin(Plus(a,Times(b,x))))))),And(FreeQ(List(a,b),x),IntIntegerQ(Times(C2,n))))),
SetDelayed(Int(Times(Power(Plus(C1,Times(CN1,Power(Plus(Times(b_DEFAULT,x_),a_DEFAULT),C2))),n_DEFAULT),u_),x_Symbol),
    Condition(Module(List(Set($s("tmp"),InverseFunctionOfLinear(u,x))),Condition(Times(CN1,Dist(Power(b,CN1),Subst(Int(Regularize(Times(SubstForInverseFunction(u,$s("tmp"),x),Power(Sin(x),Plus(Times(C2,n),C1))),x),x),x,$s("tmp")))),And(NotFalseQ($s("tmp")),SameQ($s("tmp"),ArcCos(Plus(a,Times(b,x))))))),And(FreeQ(List(a,b),x),IntIntegerQ(Times(C2,n))))),
SetDelayed(Int(Times(Power(Plus(C1,Power(Plus(Times(b_DEFAULT,x_),a_DEFAULT),C2)),n_DEFAULT),u_),x_Symbol),
    Condition(Module(List(Set($s("tmp"),InverseFunctionOfLinear(u,x))),Condition(Dist(Power(b,CN1),Subst(Int(Regularize(Times(SubstForInverseFunction(u,$s("tmp"),x),Power(Cosh(x),Plus(Times(C2,n),C1))),x),x),x,$s("tmp"))),And(NotFalseQ($s("tmp")),SameQ($s("tmp"),ArcSinh(Plus(a,Times(b,x))))))),And(FreeQ(List(a,b),x),IntIntegerQ(Times(C2,n))))),
SetDelayed(Int(u_,x_Symbol),
    Condition(Module(List(Set($s("lst"),SubstForInverseFunctionOfLinear(u,x))),Condition(Dist(Power(Part($s("lst"),C3),CN1),Subst(Int(Part($s("lst"),C1),x),x,Part($s("lst"),C2))),NotFalseQ($s("lst")))),Not(NotIntegrableQ(u,x)))),
SetDelayed(Int(u_,x_Symbol),
    Condition(Module(List(Set($s("lst"),SubstForInverseFunctionOfQuotientOfLinears(u,x))),Condition(Dist(Part($s("lst"),C3),Subst(Int(Part($s("lst"),C1),x),x,Part($s("lst"),C2))),NotFalseQ($s("lst")))),Not(NotIntegrableQ(u,x))))
  );
}
