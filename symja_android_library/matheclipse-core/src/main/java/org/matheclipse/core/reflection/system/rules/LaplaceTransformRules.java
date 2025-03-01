package org.matheclipse.core.reflection.system.rules;

import static org.matheclipse.core.expression.F.*;
import org.matheclipse.core.interfaces.IAST;

/**
 * <p>Generated by <code>org.matheclipse.core.preprocessor.RulePreprocessor</code>.</p>
 * <p>See GIT repository at: <a href="https://github.com/axkr/symja_android_library">github.com/axkr/symja_android_library under the tools directory</a>.</p>
 */
public class LaplaceTransformRules {
  /**
   * <ul>
   * <li>index 0 - number of equal rules in <code>RULES</code></li>
	 * </ul>
	 */
  final public static int[] SIZES = { 0, 13 };

  final public static IAST RULES = List(
    IInit(LaplaceTransform, SIZES),
    // LaplaceTransform(E^(b_.+c_.*t_)*a_.,t_,s_):=LaplaceTransform(a*E^b,t,-c+s)/;FreeQ({b,c,s},t)
    ISetDelayed(LaplaceTransform(Times(Exp(Plus(b_DEFAULT,Times(c_DEFAULT,t_))),a_DEFAULT),t_,s_),
      Condition(LaplaceTransform(Times(a,Exp(b)),t,Plus(Negate(c),s)),FreeQ(list(b,c,s),t))),
    // LaplaceTransform(Sqrt(t_),t_,s_):=Sqrt(Pi)/(2*s^(3/2))/;FreeQ(s,t)
    ISetDelayed(LaplaceTransform(Sqrt(t_),t_,s_),
      Condition(Times(CSqrtPi,Power(Times(C2,Power(s,QQ(3L,2L))),CN1)),FreeQ(s,t))),
    // LaplaceTransform(Sin(a_.*t_),t_,s_):=a/(s^2+a^2)/;FreeQ({a,s},t)&&FreeQ(a,s)
    ISetDelayed(LaplaceTransform(Sin(Times(a_DEFAULT,t_)),t_,s_),
      Condition(Times(a,Power(Plus(Sqr(s),Sqr(a)),CN1)),And(FreeQ(list(a,s),t),FreeQ(a,s)))),
    // LaplaceTransform(Cos(a_.*t_),t_,s_):=s/(s^2+a^2)/;FreeQ({a,s},t)&&FreeQ(a,s)
    ISetDelayed(LaplaceTransform(Cos(Times(a_DEFAULT,t_)),t_,s_),
      Condition(Times(Power(Plus(Sqr(s),Sqr(a)),CN1),s),And(FreeQ(list(a,s),t),FreeQ(a,s)))),
    // LaplaceTransform(Sinh(t_),t_,s_):=c/(-1+s^2)/;FreeQ(s,t)
    ISetDelayed(LaplaceTransform(Sinh(t_),t_,s_),
      Condition(Times(c,Power(Plus(CN1,Sqr(s)),CN1)),FreeQ(s,t))),
    // LaplaceTransform(Cosh(t_),t_,s_):=s/(-1+s^2)/;FreeQ(s,t)
    ISetDelayed(LaplaceTransform(Cosh(t_),t_,s_),
      Condition(Times(s,Power(Plus(CN1,Sqr(s)),CN1)),FreeQ(s,t))),
    // LaplaceTransform(Tanh(t_),t_,s_):=1/2*(-2/s-PolyGamma(0,s/4)+PolyGamma(0,1/4*(2+s)))/;FreeQ(s,t)
    ISetDelayed(LaplaceTransform(Tanh(t_),t_,s_),
      Condition(Times(C1D2,Plus(Times(CN1,C2,Power(s,CN1)),Negate(PolyGamma(C0,Times(C1D4,s))),PolyGamma(C0,Times(C1D4,Plus(C2,s))))),FreeQ(s,t))),
    // LaplaceTransform(DiracDelta(a_*t_),t_,s_):=1/Abs(a)/;FreeQ({a,s},t)
    ISetDelayed(LaplaceTransform(DiracDelta(Times(a_,t_)),t_,s_),
      Condition(Power(Abs(a),CN1),FreeQ(list(a,s),t))),
    // LaplaceTransform(E^t_,t_,s_):=1/(-1+s)/;FreeQ(s,t)
    ISetDelayed(LaplaceTransform(Exp(t_),t_,s_),
      Condition(Power(Plus(CN1,s),CN1),FreeQ(s,t))),
    // LaplaceTransform(Log(t_),t_,s_):=-(EulerGamma+Log(s))/s/;FreeQ(s,t)
    ISetDelayed(LaplaceTransform(Log(t_),t_,s_),
      Condition(Times(CN1,Power(s,CN1),Plus(EulerGamma,Log(s))),FreeQ(s,t))),
    // LaplaceTransform(Log(t_)^2,t_,s_):=(6*EulerGamma^2+Pi^2+6*Log(s)*(2*EulerGamma+Log(s)))/(6*s)/;FreeQ(s,t)
    ISetDelayed(LaplaceTransform(Sqr(Log(t_)),t_,s_),
      Condition(Times(Power(Times(C6,s),CN1),Plus(Times(C6,Sqr(EulerGamma)),Sqr(Pi),Times(C6,Log(s),Plus(Times(C2,EulerGamma),Log(s))))),FreeQ(s,t))),
    // LaplaceTransform(Erf(t_),t_,s_):=E^(s^2/4)*Erfc(s/2)/s/;FreeQ(s,t)
    ISetDelayed(LaplaceTransform(Erf(t_),t_,s_),
      Condition(Times(Exp(Times(C1D4,Sqr(s))),Power(s,CN1),Erfc(Times(C1D2,s))),FreeQ(s,t))),
    // LaplaceTransform(Erf(Sqrt(t_)),t_,s_):=1/(Sqrt(s+1)*s)/;FreeQ(s,t)
    ISetDelayed(LaplaceTransform(Erf(Sqrt(t_)),t_,s_),
      Condition(Power(Times(Sqrt(Plus(s,C1)),s),CN1),FreeQ(s,t))),
    // LaplaceTransform(UnitStep(a_.*t_),t_,s_):=Which(Sign(a)==1,1/s,Sign(a)==-1,0,True,0)/;FreeQ({a,s},t)
    ISetDelayed(LaplaceTransform(UnitStep(Times(a_DEFAULT,t_)),t_,s_),
      Condition(Which(Equal(Sign(a),C1),Power(s,CN1),Equal(Sign(a),CN1),C0,True,C0),FreeQ(list(a,s),t))),
    // LaplaceTransform(Derivative(1)[f_][t_],t_,s_):=-f(0)+s*LaplaceTransform(f(t),t,s)/;FreeQ(f,t)
    ISetDelayed(LaplaceTransform($($(Derivative(C1),f_),t_),t_,s_),
      Condition(Plus(Negate($(f,C0)),Times(s,LaplaceTransform($(f,t),t,s))),FreeQ(f,t))),
    // LaplaceTransform(Derivative(2)[f_][t_],t_,s_):=-s*f(0)+s^2*LaplaceTransform(f(t),t,s)-f'(0)/;FreeQ({f,s},t)
    ISetDelayed(LaplaceTransform($($(Derivative(C2),f_),t_),t_,s_),
      Condition(Plus(Times(CN1,s,$(f,C0)),Times(Sqr(s),LaplaceTransform($(f,t),t,s)),Negate($($(Derivative(C1),f),C0))),FreeQ(list(f,s),t)))
  );
}
