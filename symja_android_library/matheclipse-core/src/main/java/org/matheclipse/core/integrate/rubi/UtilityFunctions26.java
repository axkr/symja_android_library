package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.ArcCot;
import static org.matheclipse.core.expression.F.ArcCoth;
import static org.matheclipse.core.expression.F.ArcTan;
import static org.matheclipse.core.expression.F.ArcTanh;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.CI;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CNI;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Cot;
import static org.matheclipse.core.expression.F.Coth;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.ISetDelayed;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Tan;
import static org.matheclipse.core.expression.F.Tanh;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ComplexFreeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RectifyCotangent;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RectifyTangent;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SimplifyAntiderivative;
import org.matheclipse.core.interfaces.IAST;

/**
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class UtilityFunctions26 {
  public static IAST RULES = List(
      ISetDelayed(455, SimplifyAntiderivative(Log(Plus(a_, Times(Cot(u_), b_DEFAULT))), x_Symbol),
          Condition(
              Subtract(Times(CN1, b, Power(a, CN1), SimplifyAntiderivative(u, x)),
                  SimplifyAntiderivative(Log(Sin(u)), x)),
              And(FreeQ(List(a, b), x), EqQ(Plus(Sqr(a), Sqr(b)), C0)))),
      ISetDelayed(456, SimplifyAntiderivative(ArcTan(Times(Tan(u_), a_DEFAULT)), x_Symbol),
          Condition(RectifyTangent(u, a, C1, x),
              And(FreeQ(a, x), GtQ(Sqr(a), C0), ComplexFreeQ(u)))),
      ISetDelayed(457, SimplifyAntiderivative(ArcCot(Times(Tan(u_), a_DEFAULT)), x_Symbol),
          Condition(RectifyTangent(u, a, CN1, x),
              And(FreeQ(a, x), GtQ(Sqr(a), C0), ComplexFreeQ(u)))),
      ISetDelayed(458, SimplifyAntiderivative(ArcCot(Times(Tanh(u_), a_DEFAULT)), x_Symbol),
          Condition(Negate(SimplifyAntiderivative(ArcTan(Times(a, Tanh(u))), x)),
              And(FreeQ(a, x), ComplexFreeQ(u)))),
      ISetDelayed(459, SimplifyAntiderivative(ArcTanh(Times(Tan(u_), a_DEFAULT)), x_Symbol),
          Condition(RectifyTangent(u, Times(CI, a), CNI, x),
              And(FreeQ(a, x), GtQ(Sqr(a), C0), ComplexFreeQ(u)))),
      ISetDelayed(460, SimplifyAntiderivative(ArcCoth(Times(Tan(u_), a_DEFAULT)), x_Symbol),
          Condition(RectifyTangent(u, Times(CI, a), CNI, x),
              And(FreeQ(a, x), GtQ(Sqr(a), C0), ComplexFreeQ(u)))),
      ISetDelayed(461, SimplifyAntiderivative(ArcTanh(Tanh(u_)), x_Symbol),
          SimplifyAntiderivative(u, x)),
      ISetDelayed(462, SimplifyAntiderivative(ArcCoth(Tanh(u_)), x_Symbol),
          SimplifyAntiderivative(u, x)),
      ISetDelayed(463, SimplifyAntiderivative(ArcCot(Times(Cot(u_), a_DEFAULT)), x_Symbol),
          Condition(RectifyCotangent(u, a, C1, x),
              And(FreeQ(a, x), GtQ(Sqr(a), C0), ComplexFreeQ(u)))),
      ISetDelayed(464, SimplifyAntiderivative(ArcTan(Times(Cot(u_), a_DEFAULT)), x_Symbol),
          Condition(RectifyCotangent(u, a, CN1, x),
              And(FreeQ(a, x), GtQ(Sqr(a), C0), ComplexFreeQ(u)))),
      ISetDelayed(465, SimplifyAntiderivative(ArcTan(Times(Coth(u_), a_DEFAULT)), x_Symbol),
          Condition(Negate(SimplifyAntiderivative(ArcTan(Times(Tanh(u), Power(a, CN1))), x)),
              And(FreeQ(a, x), ComplexFreeQ(u)))),
      ISetDelayed(466, SimplifyAntiderivative(ArcCoth(Times(Cot(u_), a_DEFAULT)), x_Symbol),
          Condition(RectifyCotangent(u, Times(CI, a), CI, x),
              And(FreeQ(a, x), GtQ(Sqr(a), C0), ComplexFreeQ(u)))),
      ISetDelayed(467, SimplifyAntiderivative(ArcTanh(Times(Cot(u_), a_DEFAULT)), x_Symbol),
          Condition(RectifyCotangent(u, Times(CI, a), CI, x),
              And(FreeQ(a, x), GtQ(Sqr(a), C0), ComplexFreeQ(u)))),
      ISetDelayed(468, SimplifyAntiderivative(ArcCoth(Coth(u_)), x_Symbol),
          SimplifyAntiderivative(u, x)),
      ISetDelayed(469, SimplifyAntiderivative(ArcTanh(Times(Coth(u_), a_DEFAULT)), x_Symbol),
          Condition(SimplifyAntiderivative(ArcTanh(Times(Tanh(u), Power(a, CN1))), x),
              And(FreeQ(a, x), ComplexFreeQ(u)))),
      ISetDelayed(470, SimplifyAntiderivative(ArcTanh(Coth(u_)), x_Symbol),
          SimplifyAntiderivative(u, x)),
      ISetDelayed(471,
          SimplifyAntiderivative(ArcTan(Times(Plus(a_, Times(Tan(u_), b_DEFAULT)), c_DEFAULT)),
              x_Symbol),
          Condition(RectifyTangent(u, Times(a, c), Times(b, c), C1, x),
              And(FreeQ(List(a, b, c), x), GtQ(Times(Sqr(a), Sqr(c)), C0),
                  GtQ(Times(Sqr(b), Sqr(c)), C0), ComplexFreeQ(u)))),
      ISetDelayed(472,
          SimplifyAntiderivative(ArcTanh(Times(Plus(a_, Times(Tan(u_), b_DEFAULT)), c_DEFAULT)),
              x_Symbol),
          Condition(RectifyTangent(u, Times(CI, a, c), Times(CI, b, c), CNI, x),
              And(FreeQ(List(a, b, c), x), GtQ(Times(Sqr(a), Sqr(c)), C0),
                  GtQ(Times(Sqr(b), Sqr(c)), C0), ComplexFreeQ(u)))),
      ISetDelayed(473,
          SimplifyAntiderivative(ArcTan(Times(Plus(a_, Times(Cot(u_), b_DEFAULT)), c_DEFAULT)),
              x_Symbol),
          Condition(RectifyCotangent(u, Times(a, c), Times(b, c), C1, x),
              And(FreeQ(List(a, b, c), x), GtQ(Times(Sqr(a), Sqr(c)), C0),
                  GtQ(Times(Sqr(b), Sqr(c)), C0), ComplexFreeQ(u)))),
      ISetDelayed(474,
          SimplifyAntiderivative(ArcTanh(Times(Plus(a_, Times(Cot(u_), b_DEFAULT)), c_DEFAULT)),
              x_Symbol),
          Condition(RectifyCotangent(u, Times(CI, a, c), Times(CI, b, c), CNI, x),
              And(FreeQ(List(a, b, c), x), GtQ(Times(Sqr(a), Sqr(c)), C0),
                  GtQ(Times(Sqr(b), Sqr(c)), C0), ComplexFreeQ(u)))));
}
