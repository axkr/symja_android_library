package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class UtilityFunctions35 { 
  public static IAST RULES = List( 
ISetDelayed(468,SimplifyAntiderivative(Log(Power(f_,u_)),x_Symbol),
    Condition(Times(Log(f),SimplifyAntiderivative(u,x)),FreeQ(f,x))),
ISetDelayed(469,SimplifyAntiderivative(Log(Plus(a_,Times(Tan(u_),b_DEFAULT))),x_Symbol),
    Condition(Subtract(Times(b,Power(a,CN1),SimplifyAntiderivative(u,x)),SimplifyAntiderivative(Log(Cos(u)),x)),And(FreeQ(list(a,b),x),EqQ(Plus(Sqr(a),Sqr(b)),C0)))),
ISetDelayed(470,SimplifyAntiderivative(Log(Plus(a_,Times(Cot(u_),b_DEFAULT))),x_Symbol),
    Condition(Subtract(Times(CN1,b,Power(a,CN1),SimplifyAntiderivative(u,x)),SimplifyAntiderivative(Log(Sin(u)),x)),And(FreeQ(list(a,b),x),EqQ(Plus(Sqr(a),Sqr(b)),C0)))),
ISetDelayed(471,SimplifyAntiderivative(ArcTan(Times(Tan(u_),a_DEFAULT)),x_Symbol),
    Condition(RectifyTangent(u,a,C1,x),And(FreeQ(a,x),GtQ(Sqr(a),C0),ComplexFreeQ(u)))),
ISetDelayed(472,SimplifyAntiderivative(ArcCot(Times(Tan(u_),a_DEFAULT)),x_Symbol),
    Condition(RectifyTangent(u,a,CN1,x),And(FreeQ(a,x),GtQ(Sqr(a),C0),ComplexFreeQ(u)))),
ISetDelayed(473,SimplifyAntiderivative(ArcCot(Times(Tanh(u_),a_DEFAULT)),x_Symbol),
    Condition(Negate(SimplifyAntiderivative(ArcTan(Times(a,Tanh(u))),x)),And(FreeQ(a,x),ComplexFreeQ(u)))),
ISetDelayed(474,SimplifyAntiderivative(ArcTanh(Times(Tan(u_),a_DEFAULT)),x_Symbol),
    Condition(RectifyTangent(u,Times(CI,a),CNI,x),And(FreeQ(a,x),GtQ(Sqr(a),C0),ComplexFreeQ(u)))),
ISetDelayed(475,SimplifyAntiderivative(ArcCoth(Times(Tan(u_),a_DEFAULT)),x_Symbol),
    Condition(RectifyTangent(u,Times(CI,a),CNI,x),And(FreeQ(a,x),GtQ(Sqr(a),C0),ComplexFreeQ(u)))),
ISetDelayed(476,SimplifyAntiderivative(ArcTanh(Tanh(u_)),x_Symbol),
    SimplifyAntiderivative(u,x)),
ISetDelayed(477,SimplifyAntiderivative(ArcCoth(Tanh(u_)),x_Symbol),
    SimplifyAntiderivative(u,x)),
ISetDelayed(478,SimplifyAntiderivative(ArcCot(Times(Cot(u_),a_DEFAULT)),x_Symbol),
    Condition(RectifyCotangent(u,a,C1,x),And(FreeQ(a,x),GtQ(Sqr(a),C0),ComplexFreeQ(u)))),
ISetDelayed(479,SimplifyAntiderivative(ArcTan(Times(Cot(u_),a_DEFAULT)),x_Symbol),
    Condition(RectifyCotangent(u,a,CN1,x),And(FreeQ(a,x),GtQ(Sqr(a),C0),ComplexFreeQ(u)))),
ISetDelayed(480,SimplifyAntiderivative(ArcTan(Times(Coth(u_),a_DEFAULT)),x_Symbol),
    Condition(Negate(SimplifyAntiderivative(ArcTan(Times(Tanh(u),Power(a,CN1))),x)),And(FreeQ(a,x),ComplexFreeQ(u)))),
ISetDelayed(481,SimplifyAntiderivative(ArcCoth(Times(Cot(u_),a_DEFAULT)),x_Symbol),
    Condition(RectifyCotangent(u,Times(CI,a),CI,x),And(FreeQ(a,x),GtQ(Sqr(a),C0),ComplexFreeQ(u)))),
ISetDelayed(482,SimplifyAntiderivative(ArcTanh(Times(Cot(u_),a_DEFAULT)),x_Symbol),
    Condition(RectifyCotangent(u,Times(CI,a),CI,x),And(FreeQ(a,x),GtQ(Sqr(a),C0),ComplexFreeQ(u)))),
ISetDelayed(483,SimplifyAntiderivative(ArcCoth(Coth(u_)),x_Symbol),
    SimplifyAntiderivative(u,x)),
ISetDelayed(484,SimplifyAntiderivative(ArcTanh(Times(Coth(u_),a_DEFAULT)),x_Symbol),
    Condition(SimplifyAntiderivative(ArcTanh(Times(Tanh(u),Power(a,CN1))),x),And(FreeQ(a,x),ComplexFreeQ(u)))),
ISetDelayed(485,SimplifyAntiderivative(ArcTanh(Coth(u_)),x_Symbol),
    SimplifyAntiderivative(u,x)),
ISetDelayed(486,SimplifyAntiderivative(ArcTan(Times(Plus(a_,Times(Tan(u_),b_DEFAULT)),c_DEFAULT)),x_Symbol),
    Condition(RectifyTangent(u,Times(a,c),Times(b,c),C1,x),And(FreeQ(list(a,b,c),x),GtQ(Times(Sqr(a),Sqr(c)),C0),GtQ(Times(Sqr(b),Sqr(c)),C0),ComplexFreeQ(u)))),
ISetDelayed(487,SimplifyAntiderivative(ArcTanh(Times(Plus(a_,Times(Tan(u_),b_DEFAULT)),c_DEFAULT)),x_Symbol),
    Condition(RectifyTangent(u,Times(CI,a,c),Times(CI,b,c),CNI,x),And(FreeQ(list(a,b,c),x),GtQ(Times(Sqr(a),Sqr(c)),C0),GtQ(Times(Sqr(b),Sqr(c)),C0),ComplexFreeQ(u))))
  );
}
