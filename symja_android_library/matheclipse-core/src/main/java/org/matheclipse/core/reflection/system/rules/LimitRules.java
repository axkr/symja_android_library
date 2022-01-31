package org.matheclipse.core.reflection.system.rules;

import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.Abs;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.ArcCos;
import static org.matheclipse.core.expression.F.ArcCosh;
import static org.matheclipse.core.expression.F.ArcCot;
import static org.matheclipse.core.expression.F.ArcCoth;
import static org.matheclipse.core.expression.F.ArcCsc;
import static org.matheclipse.core.expression.F.ArcCsch;
import static org.matheclipse.core.expression.F.ArcSec;
import static org.matheclipse.core.expression.F.ArcSech;
import static org.matheclipse.core.expression.F.ArcSin;
import static org.matheclipse.core.expression.F.ArcSinh;
import static org.matheclipse.core.expression.F.ArcTan;
import static org.matheclipse.core.expression.F.ArcTanh;
import static org.matheclipse.core.expression.F.BernoulliB;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C2Pi;
import static org.matheclipse.core.expression.F.CC;
import static org.matheclipse.core.expression.F.CI;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CNI;
import static org.matheclipse.core.expression.F.CNPiHalf;
import static org.matheclipse.core.expression.F.CPiHalf;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.ConditionalExpression;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.Cosh;
import static org.matheclipse.core.expression.F.Cot;
import static org.matheclipse.core.expression.F.Coth;
import static org.matheclipse.core.expression.F.Csch;
import static org.matheclipse.core.expression.F.DirectedInfinity;
import static org.matheclipse.core.expression.F.Element;
import static org.matheclipse.core.expression.F.Equal;
import static org.matheclipse.core.expression.F.EvenQ;
import static org.matheclipse.core.expression.F.Exp;
import static org.matheclipse.core.expression.F.Factorial;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.Gamma;
import static org.matheclipse.core.expression.F.Greater;
import static org.matheclipse.core.expression.F.HarmonicNumber;
import static org.matheclipse.core.expression.F.IInit;
import static org.matheclipse.core.expression.F.ISet;
import static org.matheclipse.core.expression.F.ISetDelayed;
import static org.matheclipse.core.expression.F.If;
import static org.matheclipse.core.expression.F.Less;
import static org.matheclipse.core.expression.F.Limit;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Noo;
import static org.matheclipse.core.expression.F.PatternTest;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Positive;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Rule;
import static org.matheclipse.core.expression.F.Sech;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Sinh;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Tan;
import static org.matheclipse.core.expression.F.Tanh;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.UnsameQ;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.oo;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.F.y_Symbol;
import static org.matheclipse.core.expression.F.z_;
import static org.matheclipse.core.expression.S.Direction;
import static org.matheclipse.core.expression.S.E;
import static org.matheclipse.core.expression.S.Indeterminate;
import static org.matheclipse.core.expression.S.Integer;
import static org.matheclipse.core.expression.S.Limit;
import static org.matheclipse.core.expression.S.NumberQ;
import static org.matheclipse.core.expression.S.Pi;
import static org.matheclipse.core.expression.S.RealNumberQ;
import static org.matheclipse.core.expression.S.Reals;
import static org.matheclipse.core.expression.S.Undefined;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.r;
import static org.matheclipse.core.expression.S.s;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.expression.S.z;
import org.matheclipse.core.interfaces.IAST;

/**
 * <p>
 * Generated by <code>org.matheclipse.core.preprocessor.RulePreprocessor</code>.
 * </p>
 * <p>
 * See GIT repository at:
 * <a href="https://github.com/axkr/symja_android_library">github.com/axkr/symja_android_library
 * under the tools directory</a>.
 * </p>
 */
public interface LimitRules {
  /**
   * <ul>
   * <li>index 0 - number of equal rules in <code>RULES</code></li>
   * </ul>
   */
  final public static int[] SIZES = {48, 21};

  final public static IAST RULES = List(IInit(Limit, SIZES),
      // Limit((Sqrt(2*Pi))^(1/x_)*(Sin(x_)/x_!)^(1/x_)*x_,x_Symbol->Infinity):=E
      ISetDelayed(Limit(
          Times(Power(Sqrt(C2Pi), Power(x_, CN1)),
              Power(Times(Power(Factorial(x_), CN1), Sin(x_)), Power(x_, CN1)), x_),
          Rule(x_Symbol, oo)), E),
      // Limit(x_/(x_!)^(1/x_),x_Symbol->Infinity):=E
      ISetDelayed(
          Limit(Times(Power(Factorial(x_), Negate(Power(x_, CN1))), x_), Rule(x_Symbol, oo)), E),
      // Limit(x_^(b_.+a_.*x_^n_.),x_Symbol->0):=With({r=ConditionalExpression(1,a∈Reals&&b>0&&n>0)},r/;r=!=Undefined)
      ISetDelayed(
          Limit(Power(x_, Plus(b_DEFAULT, Times(a_DEFAULT, Power(x_, n_DEFAULT)))),
              Rule(x_Symbol, C0)),
          With(
              List(Set(r,
                  ConditionalExpression(C1,
                      And(Element(a, Reals), Greater(b, C0), Greater(n, C0))))),
              Condition(r, UnsameQ(r, Undefined)))),
      // Limit(x_^(b_.+a_.*x_^n_.),x_Symbol->0):=With({r=ConditionalExpression(0,b∈Reals&&a>0&&n<0&&Cos(-n*Pi)>0&&Sin(-n*Pi)>0)},r/;r=!=Undefined)
      ISetDelayed(
          Limit(Power(x_, Plus(b_DEFAULT, Times(a_DEFAULT, Power(x_, n_DEFAULT)))),
              Rule(x_Symbol, C0)),
          With(
              List(Set(r,
                  ConditionalExpression(C0, And(Element(b, Reals), Greater(a, C0), Less(n, C0),
                      Greater(Cos(Times(CN1, n, Pi)), C0), Greater(Sin(Times(CN1, n, Pi)), C0))))),
              Condition(r, UnsameQ(r, Undefined)))),
      // Limit(x_^(b_.+a_.*x_^n_.),x_Symbol->Infinity):=With({r=ConditionalExpression(Infinity,b∈Reals&&a>0&&n>0)},r/;r=!=Undefined)
      ISetDelayed(
          Limit(Power(x_, Plus(b_DEFAULT, Times(a_DEFAULT, Power(x_, n_DEFAULT)))),
              Rule(x_Symbol, oo)),
          With(
              List(Set(r,
                  ConditionalExpression(oo,
                      And(Element(b, Reals), Greater(a, C0), Greater(n, C0))))),
              Condition(r, UnsameQ(r, Undefined)))),
      // Limit(x_^(a_.*x_^n_.),x_Symbol->Infinity):=With({r=ConditionalExpression(1,a∈Reals&&n<0)},r/;r=!=Undefined)
      ISetDelayed(Limit(Power(x_, Times(a_DEFAULT, Power(x_, n_DEFAULT))), Rule(x_Symbol, oo)),
          With(List(Set(r, ConditionalExpression(C1, And(Element(a, Reals), Less(n, C0))))),
              Condition(r, UnsameQ(r, Undefined)))),
      // Limit(x_^m_?RealNumberQ,x_Symbol->Infinity):=If(m<0,0,Infinity)
      ISetDelayed(Limit(Power(x_, PatternTest(m_, RealNumberQ)), Rule(x_Symbol, oo)),
          If(Less(m, C0), C0, oo)),
      // Limit(m_?NumberQ^x_,x_Symbol->Infinity):=If(m>1,Infinity,If(m==1,1,0))/;Positive(m)
      ISetDelayed(Limit(Power(PatternTest(m_, NumberQ), x_), Rule(x_Symbol, oo)),
          Condition(If(Greater(m, C1), oo, If(Equal(m, C1), C1, C0)), Positive(m))),
      // Limit(m_?NumberQ^(-x_),x_Symbol->Infinity):=0/;m>1
      ISetDelayed(Limit(Power(PatternTest(m_, NumberQ), Negate(x_)), Rule(x_Symbol, oo)),
          Condition(C0, Greater(m, C1))),
      // Limit(E^x_,x_Symbol->Infinity):=Infinity
      ISetDelayed(Limit(Exp(x_), Rule(x_Symbol, oo)), oo),
      // Limit(E^x_,x_Symbol->-Infinity):=0
      ISetDelayed(Limit(Exp(x_), Rule(x_Symbol, Noo)), C0),
      // Limit(Log(x_),x_Symbol->0)=-Infinity
      ISet(Limit(Log(x_), Rule(x_Symbol, C0)), Noo),
      // Limit(Log(x_),x_Symbol->Infinity)=Infinity
      ISet(Limit(Log(x_), Rule(x_Symbol, oo)), oo),
      // Limit(Log(x_),x_Symbol->-Infinity)=Infinity
      ISet(Limit(Log(x_), Rule(x_Symbol, Noo)), oo),
      // Limit((1+1/x_)^x_,x_Symbol->Infinity)=E
      ISet(Limit(Power(Plus(C1, Power(x_, CN1)), x_), Rule(x_Symbol, oo)), E),
      // Limit((1+a_./x_)^(b_.*x_),x_Symbol->-Infinity)=E^(a*b)/;FreeQ({a,b},x)
      ISet(Limit(Power(Plus(C1, Times(a_DEFAULT, Power(x_, CN1))), Times(b_DEFAULT, x_)),
          Rule(x_Symbol, Noo)), Condition(Exp(Times(a, b)), FreeQ(List(a, b), x))),
      // Limit((1+a_./x_)^(b_.*x_),x_Symbol->Infinity)=E^(a*b)/;FreeQ({a,b},x)
      ISet(Limit(Power(Plus(C1, Times(a_DEFAULT, Power(x_, CN1))), Times(b_DEFAULT, x_)),
          Rule(x_Symbol, oo)), Condition(Exp(Times(a, b)), FreeQ(List(a, b), x))),
      // Limit((1+x_)^(1/x_),x_Symbol->0)=E
      ISet(Limit(Power(Plus(C1, x_), Power(x_, CN1)), Rule(x_Symbol, C0)), E),
      // Limit(((a_.+x_)/(b_.+x_))^(c_.+x_),x_Symbol->-Infinity)=E^(a-b)/;FreeQ({a,b,c},x)
      ISet(
          Limit(Power(Times(Plus(a_DEFAULT, x_), Power(Plus(b_DEFAULT, x_), CN1)),
              Plus(c_DEFAULT, x_)), Rule(x_Symbol, Noo)),
          Condition(Exp(Subtract(a, b)), FreeQ(List(a, b, c), x))),
      // Limit(((a_.+x_)/(b_.+x_))^(c_.+x_),x_Symbol->Infinity)=E^(a-b)/;FreeQ({a,b,c},x)
      ISet(
          Limit(Power(Times(Plus(a_DEFAULT, x_), Power(Plus(b_DEFAULT, x_), CN1)),
              Plus(c_DEFAULT, x_)), Rule(x_Symbol, oo)),
          Condition(Exp(Subtract(a, b)), FreeQ(List(a, b, c), x))),
      // Limit(HarmonicNumber(y_Symbol,s_Integer),x_Symbol->Infinity):=With({v=s/2},((-1)^(v+1)*(2*Pi)^(2*v)*BernoulliB(2*v))/(2*(2*v)!))/;EvenQ(s)&&Positive(s)
      ISetDelayed(Limit(HarmonicNumber(y_Symbol, $p(s, Integer)), Rule(x_Symbol, oo)),
          Condition(
              With(List(Set(v, Times(C1D2, s))),
                  Times(Power(CN1, Plus(v, C1)), Power(C2Pi, Times(C2, v)),
                      BernoulliB(Times(C2, v)), Power(Times(C2, Factorial(Times(C2, v))), CN1))),
              And(EvenQ(s), Positive(s)))),
      // Limit(Tan(x_),x_Symbol->Pi/2):=Indeterminate
      ISetDelayed(Limit(Tan(x_), Rule(x_Symbol, CPiHalf)), Indeterminate),
      // Limit(Cot(x_),x_Symbol->0):=Indeterminate
      ISetDelayed(Limit(Cot(x_), Rule(x_Symbol, C0)), Indeterminate),
      // Limit(ArcCos(x_),x_Symbol->Infinity)=I*Infinity
      ISet(Limit(ArcCos(x_), Rule(x_Symbol, oo)), DirectedInfinity(CI)),
      // Limit(ArcCos(x_),x_Symbol->-Infinity)=-I*Infinity
      ISet(Limit(ArcCos(x_), Rule(x_Symbol, Noo)), DirectedInfinity(CNI)),
      // Limit(ArcCot(x_),x_Symbol->Infinity)=0
      ISet(Limit(ArcCot(x_), Rule(x_Symbol, oo)), C0),
      // Limit(ArcCot(x_),x_Symbol->-Infinity)=0
      ISet(Limit(ArcCot(x_), Rule(x_Symbol, Noo)), C0),
      // Limit(ArcCsc(x_),x_Symbol->Infinity)=0
      ISet(Limit(ArcCsc(x_), Rule(x_Symbol, oo)), C0),
      // Limit(ArcCsc(x_),x_Symbol->-Infinity)=0
      ISet(Limit(ArcCsc(x_), Rule(x_Symbol, Noo)), C0),
      // Limit(ArcSec(x_),x_Symbol->Infinity)=Pi/2
      ISet(Limit(ArcSec(x_), Rule(x_Symbol, oo)), CPiHalf),
      // Limit(ArcSec(x_),x_Symbol->-Infinity)=Pi/2
      ISet(Limit(ArcSec(x_), Rule(x_Symbol, Noo)), CPiHalf),
      // Limit(ArcSin(x_),x_Symbol->Infinity)=-I*Infinity
      ISet(Limit(ArcSin(x_), Rule(x_Symbol, oo)), DirectedInfinity(CNI)),
      // Limit(ArcSin(x_),x_Symbol->-Infinity)=I*Infinity
      ISet(Limit(ArcSin(x_), Rule(x_Symbol, Noo)), DirectedInfinity(CI)),
      // Limit(ArcTan(x_),x_Symbol->Infinity)=Pi/2
      ISet(Limit(ArcTan(x_), Rule(x_Symbol, oo)), CPiHalf),
      // Limit(ArcTan(x_),x_Symbol->-Infinity)=(-1)*1/2*Pi
      ISet(Limit(ArcTan(x_), Rule(x_Symbol, Noo)), CNPiHalf),
      // Limit(ArcCosh(x_),x_Symbol->Infinity)=Infinity
      ISet(Limit(ArcCosh(x_), Rule(x_Symbol, oo)), oo),
      // Limit(ArcCosh(x_),x_Symbol->-Infinity)=Infinity
      ISet(Limit(ArcCosh(x_), Rule(x_Symbol, Noo)), oo),
      // Limit(ArcCoth(x_),x_Symbol->Infinity)=0
      ISet(Limit(ArcCoth(x_), Rule(x_Symbol, oo)), C0),
      // Limit(ArcCoth(x_),x_Symbol->-Infinity)=0
      ISet(Limit(ArcCoth(x_), Rule(x_Symbol, Noo)), C0),
      // Limit(ArcCsch(x_),x_Symbol->Infinity)=0
      ISet(Limit(ArcCsch(x_), Rule(x_Symbol, oo)), C0),
      // Limit(ArcCsch(x_),x_Symbol->-Infinity)=0
      ISet(Limit(ArcCsch(x_), Rule(x_Symbol, Noo)), C0),
      // Limit(ArcSech(x_),x_Symbol->Infinity)=I*Pi/2
      ISet(Limit(ArcSech(x_), Rule(x_Symbol, oo)), Times(CC(0L, 1L, 1L, 2L), Pi)),
      // Limit(ArcSech(x_),x_Symbol->-Infinity)=I*Pi/2
      ISet(Limit(ArcSech(x_), Rule(x_Symbol, Noo)), Times(CC(0L, 1L, 1L, 2L), Pi)),
      // Limit(ArcSinh(x_),x_Symbol->Infinity)=Infinity
      ISet(Limit(ArcSinh(x_), Rule(x_Symbol, oo)), oo),
      // Limit(ArcSinh(x_),x_Symbol->-Infinity)=-Infinity
      ISet(Limit(ArcSinh(x_), Rule(x_Symbol, Noo)), Noo),
      // Limit(ArcTanh(x_),x_Symbol->Infinity)=-I*Pi/2
      ISet(Limit(ArcTanh(x_), Rule(x_Symbol, oo)), Times(CC(0L, 1L, -1L, 2L), Pi)),
      // Limit(ArcTanh(x_),x_Symbol->-Infinity)=I*Pi/2
      ISet(Limit(ArcTanh(x_), Rule(x_Symbol, Noo)), Times(CC(0L, 1L, 1L, 2L), Pi)),
      // Limit(Cosh(x_),x_Symbol->Infinity)=Infinity
      ISet(Limit(Cosh(x_), Rule(x_Symbol, oo)), oo),
      // Limit(Cosh(x_),x_Symbol->-Infinity)=Infinity
      ISet(Limit(Cosh(x_), Rule(x_Symbol, Noo)), oo),
      // Limit(Coth(x_),x_Symbol->Infinity)=1
      ISet(Limit(Coth(x_), Rule(x_Symbol, oo)), C1),
      // Limit(Coth(x_),x_Symbol->-Infinity)=-1
      ISet(Limit(Coth(x_), Rule(x_Symbol, Noo)), CN1),
      // Limit(Csch(x_),x_Symbol->Infinity)=0
      ISet(Limit(Csch(x_), Rule(x_Symbol, oo)), C0),
      // Limit(Csch(x_),x_Symbol->-Infinity)=0
      ISet(Limit(Csch(x_), Rule(x_Symbol, Noo)), C0),
      // Limit(Sech(x_),x_Symbol->Infinity)=0
      ISet(Limit(Sech(x_), Rule(x_Symbol, oo)), C0),
      // Limit(Sech(x_),x_Symbol->-Infinity)=0
      ISet(Limit(Sech(x_), Rule(x_Symbol, Noo)), C0),
      // Limit(Sinh(x_),x_Symbol->Infinity)=Infinity
      ISet(Limit(Sinh(x_), Rule(x_Symbol, oo)), oo),
      // Limit(Sinh(x_),x_Symbol->-Infinity)=-Infinity
      ISet(Limit(Sinh(x_), Rule(x_Symbol, Noo)), Noo),
      // Limit(Tanh(x_),x_Symbol->Infinity)=1
      ISet(Limit(Tanh(x_), Rule(x_Symbol, oo)), C1),
      // Limit(Tanh(x_),x_Symbol->-Infinity)=-1
      ISet(Limit(Tanh(x_), Rule(x_Symbol, Noo)), CN1),
      // Limit(Gamma(x_),x_Symbol->0,Direction->1)=-Infinity
      ISet(Limit(Gamma(x_), Rule(x_Symbol, C0), Rule(Direction, C1)), Noo),
      // Limit(Gamma(x_),x_Symbol->0,Direction->-1)=Infinity
      ISet(Limit(Gamma(x_), Rule(x_Symbol, C0), Rule(Direction, CN1)), oo),
      // Limit(Gamma(z_,x_),x_Symbol->Infinity)=0
      ISet(Limit(Gamma(z_, x_), Rule(x_Symbol, oo)), C0),
      // Limit(Gamma(z_,x_),x_Symbol->0):=Gamma(z)
      ISetDelayed(Limit(Gamma(z_, x_), Rule(x_Symbol, C0)), Gamma(z)),
      // Limit(x_/Abs(x_),x_Symbol->0,Direction->1):=-1
      ISetDelayed(Limit(Times(Power(Abs(x_), CN1), x_), Rule(x_Symbol, C0), Rule(Direction, C1)),
          CN1),
      // Limit(x_/Abs(x_),x_Symbol->0,Direction->-1):=1
      ISetDelayed(Limit(Times(Power(Abs(x_), CN1), x_), Rule(x_Symbol, C0), Rule(Direction, CN1)),
          C1),
      // Limit(Tan(x_),x_Symbol->Pi/2,Direction->1):=Infinity
      ISetDelayed(Limit(Tan(x_), Rule(x_Symbol, CPiHalf), Rule(Direction, C1)), oo),
      // Limit(Tan(x_),x_Symbol->Pi/2,Direction->-1):=-Infinity
      ISetDelayed(Limit(Tan(x_), Rule(x_Symbol, CPiHalf), Rule(Direction, CN1)), Negate(oo)),
      // Limit(Cot(x_),x_Symbol->0,Direction->1):=-Infinity
      ISetDelayed(Limit(Cot(x_), Rule(x_Symbol, C0), Rule(Direction, C1)), Negate(oo)),
      // Limit(Cot(x_),x_Symbol->0,Direction->-1):=Infinity
      ISetDelayed(Limit(Cot(x_), Rule(x_Symbol, C0), Rule(Direction, CN1)), oo));
}
