package org.matheclipse.core.reflection.system.rulesets;

import static org.matheclipse.core.expression.F.*;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.patternmatching.Matcher;

/**
 * <p>Generated by <code>org.matheclipse.core.preprocessor.RulePreprocessor</code>.</p>
 * <p>See GIT repository at: <a href="https://github.com/axkr/symja_android_library">github.com/axkr/symja_android_library under the tools directory</a>.</p>
 */
public interface SeriesCoefficientRules {
public static Matcher init1() {
  Matcher matcher = new Matcher();    // SeriesCoefficient(Fibonacci(x_),{x_Symbol,0,n_?NotListQ}):=Piecewise({{-((-I*Pi-ArcCsch(2))^n+(I*Pi-ArcCsch(2))^n-2*ArcCsch(2)^n)/(2*Sqrt(5)*n!),n>=1}},0)/;FreeQ(n,x)
matcher.caseOf(SeriesCoefficient(Fibonacci(x_),list(x_Symbol,C0,PatternTest(n_,NotListQ))),
      Condition(Piecewise(list(list(Times(CN1,Plus(Power(Subtract(Times(CNI,Pi),ArcCsch(C2)),n),Power(Subtract(Times(CI,Pi),ArcCsch(C2)),n),Times(CN2,Power(ArcCsch(C2),n))),Power(Times(C2,CSqrt5,Factorial(n)),CN1)),GreaterEqual(n,C1))),C0),FreeQ(n,x)));
    // SeriesCoefficient(HarmonicNumber(x_),{x_Symbol,a_,n_?NotListQ}):=Piecewise({{HarmonicNumber(a),n==0},{(-1)^(1+n)*Zeta(1+n,1+a),n>=1}},0)/;FreeQ(a,x)&&FreeQ(n,x)
matcher.caseOf(SeriesCoefficient(HarmonicNumber(x_),list(x_Symbol,a_,PatternTest(n_,NotListQ))),
      Condition(Piecewise(list(list(HarmonicNumber(a),Equal(n,C0)),list(Times(Power(CN1,Plus(C1,n)),Zeta(Plus(C1,n),Plus(C1,a))),GreaterEqual(n,C1))),C0),And(FreeQ(a,x),FreeQ(n,x))));
    // SeriesCoefficient(BernoulliB(m_,x_),{x_Symbol,a_,n_?NotListQ}):=Piecewise({{(BernoulliB(m-n,a)*Pochhammer(1+m-n,n))/n!,n>=0&&m>=n}},0)/;FreeQ({a,m,n},x)
matcher.caseOf(SeriesCoefficient(BernoulliB(m_,x_),list(x_Symbol,a_,PatternTest(n_,NotListQ))),
      Condition(Piecewise(list(list(Times(BernoulliB(Subtract(m,n),a),Power(Factorial(n),CN1),Pochhammer(Plus(C1,m,Negate(n)),n)),And(GreaterEqual(n,C0),GreaterEqual(m,n)))),C0),FreeQ(list(a,m,n),x)));
    // SeriesCoefficient(x_/(-1+a_^x_),{x_Symbol,0,n_?NotListQ}):=Piecewise({{BernoulliB(n)/(n!*Log(a)^(1-n)),n>=0}},0)/;FreeQ(a,x)&&FreeQ(n,x)
matcher.caseOf(SeriesCoefficient(Times(Power(Plus(CN1,Power(a_,x_)),CN1),x_),list(x_Symbol,C0,PatternTest(n_,NotListQ))),
      Condition(Piecewise(list(list(Times(BernoulliB(n),Power(Factorial(n),CN1),Power(Log(a),Plus(CN1,n))),GreaterEqual(n,C0))),C0),And(FreeQ(a,x),FreeQ(n,x))));
    // SeriesCoefficient(Cos(x_),{x_Symbol,0,n_?NotListQ}):=Piecewise({{((1+(-1)^n)*I^n)/(2*n!),n>=0}},0)/;FreeQ(n,x)
matcher.caseOf(SeriesCoefficient(Cos(x_),list(x_Symbol,C0,PatternTest(n_,NotListQ))),
      Condition(Piecewise(list(list(Times(Plus(C1,Power(CN1,n)),Power(CI,n),Power(Times(C2,Factorial(n)),CN1)),GreaterEqual(n,C0))),C0),FreeQ(n,x)));
    // SeriesCoefficient(Cos(x_),{x_Symbol,Pi/2,n_?NotListQ}):=Piecewise({{((-1)*I*(-1+(-1)^n)*I^n)/(2*n!),n>=0}},0)/;FreeQ(n,x)
matcher.caseOf(SeriesCoefficient(Cos(x_),list(x_Symbol,CPiHalf,PatternTest(n_,NotListQ))),
      Condition(Piecewise(list(list(Times(CN1,CI,Plus(CN1,Power(CN1,n)),Power(CI,n),Power(Times(C2,Factorial(n)),CN1)),GreaterEqual(n,C0))),C0),FreeQ(n,x)));
    // SeriesCoefficient(Sin(x_),{x_Symbol,0,n_?NotListQ}):=Piecewise({{(I*(-1+(-1)^n)*I^n)/(2*n!),n>=0}},0)/;FreeQ(n,x)
matcher.caseOf(SeriesCoefficient(Sin(x_),list(x_Symbol,C0,PatternTest(n_,NotListQ))),
      Condition(Piecewise(list(list(Times(CI,Plus(CN1,Power(CN1,n)),Power(CI,n),Power(Times(C2,Factorial(n)),CN1)),GreaterEqual(n,C0))),C0),FreeQ(n,x)));
    // SeriesCoefficient(Sin(x_),{x_Symbol,Pi/2,n_?NotListQ}):=Piecewise({{((1+(-1)^n)*I^n)/(2*n!),n>=0}},0)/;FreeQ(n,x)
matcher.caseOf(SeriesCoefficient(Sin(x_),list(x_Symbol,CPiHalf,PatternTest(n_,NotListQ))),
      Condition(Piecewise(list(list(Times(Plus(C1,Power(CN1,n)),Power(CI,n),Power(Times(C2,Factorial(n)),CN1)),GreaterEqual(n,C0))),C0),FreeQ(n,x)));
    // SeriesCoefficient(Tan(x_),{x_Symbol,0,n_?NotListQ}):=Piecewise({{((-1+(-1)^n)*I^(1+n)*2^n*(-1+2^(1+n))*BernoulliB(1+n))/(1+n)!,n>=1}},0)/;FreeQ(n,x)
matcher.caseOf(SeriesCoefficient(Tan(x_),list(x_Symbol,C0,PatternTest(n_,NotListQ))),
      Condition(Piecewise(list(list(Times(Plus(CN1,Power(CN1,n)),Power(CI,Plus(C1,n)),Power(C2,n),Plus(CN1,Power(C2,Plus(C1,n))),BernoulliB(Plus(C1,n)),Power(Factorial(Plus(C1,n)),CN1)),GreaterEqual(n,C1))),C0),FreeQ(n,x)));
    // SeriesCoefficient(Tan(x_),{x_Symbol,Pi/2,n_?NotListQ}):=Piecewise({{-1,n==-1},{((-1+(-1)^n)*I^(1+n)*2^n*BernoulliB(1+n))/(1+n)!,n>=0}},0)/;FreeQ(n,x)
matcher.caseOf(SeriesCoefficient(Tan(x_),list(x_Symbol,CPiHalf,PatternTest(n_,NotListQ))),
      Condition(Piecewise(list(list(CN1,Equal(n,CN1)),list(Times(Plus(CN1,Power(CN1,n)),Power(CI,Plus(C1,n)),Power(C2,n),BernoulliB(Plus(C1,n)),Power(Factorial(Plus(C1,n)),CN1)),GreaterEqual(n,C0))),C0),FreeQ(n,x)));
    // SeriesCoefficient(Cot(x_),{x_Symbol,0,n_?NotListQ}):=Piecewise({{1,n==-1},{((-1)*I*(-1+(-1)^n)*(2*I)^n*BernoulliB(1+n))/(1+n)!,n>=0}},0)/;FreeQ(n,x)
matcher.caseOf(SeriesCoefficient(Cot(x_),list(x_Symbol,C0,PatternTest(n_,NotListQ))),
      Condition(Piecewise(list(list(C1,Equal(n,CN1)),list(Times(CN1,CI,Plus(CN1,Power(CN1,n)),Power(Times(C2,CI),n),BernoulliB(Plus(C1,n)),Power(Factorial(Plus(C1,n)),CN1)),GreaterEqual(n,C0))),C0),FreeQ(n,x)));
    // SeriesCoefficient(Cot(x_),{x_Symbol,Pi/2,n_?NotListQ}):=Piecewise({{((-1)*I*(-1+(-1)^n)*(-1+2^(1+n))*(2*I)^n*BernoulliB(1+n))/(1+n)!,n>=1}},0)/;FreeQ(n,x)
matcher.caseOf(SeriesCoefficient(Cot(x_),list(x_Symbol,CPiHalf,PatternTest(n_,NotListQ))),
      Condition(Piecewise(list(list(Times(CN1,CI,Plus(CN1,Power(CN1,n)),Plus(CN1,Power(C2,Plus(C1,n))),Power(Times(C2,CI),n),BernoulliB(Plus(C1,n)),Power(Factorial(Plus(C1,n)),CN1)),GreaterEqual(n,C1))),C0),FreeQ(n,x)));
    // SeriesCoefficient(Cosh(x_),{x_Symbol,0,n_?NotListQ}):=Piecewise({{1/n!,Mod(n,2)==0&&n>=0}},0)/;FreeQ(n,x)
matcher.caseOf(SeriesCoefficient(Cosh(x_),list(x_Symbol,C0,PatternTest(n_,NotListQ))),
      Condition(Piecewise(list(list(Power(Factorial(n),CN1),And(Equal(Mod(n,C2),C0),GreaterEqual(n,C0)))),C0),FreeQ(n,x)));
    // SeriesCoefficient(Coth(x_),{x_Symbol,0,n_?NotListQ}):=Piecewise({{1,n==-1},{(2^(1+n)*BernoulliB(1+n))/(1+n)!,n>=0&&Mod(n,2)==1}},0)/;FreeQ(n,x)
matcher.caseOf(SeriesCoefficient(Coth(x_),list(x_Symbol,C0,PatternTest(n_,NotListQ))),
      Condition(Piecewise(list(list(C1,Equal(n,CN1)),list(Times(Power(C2,Plus(C1,n)),BernoulliB(Plus(C1,n)),Power(Factorial(Plus(C1,n)),CN1)),And(GreaterEqual(n,C0),Equal(Mod(n,C2),C1)))),C0),FreeQ(n,x)));
    // SeriesCoefficient(Sinh(x_),{x_Symbol,0,n_?NotListQ}):=Piecewise({{1/n!,Mod(n,2)==1&&n>=0}},0)/;FreeQ(n,x)
matcher.caseOf(SeriesCoefficient(Sinh(x_),list(x_Symbol,C0,PatternTest(n_,NotListQ))),
      Condition(Piecewise(list(list(Power(Factorial(n),CN1),And(Equal(Mod(n,C2),C1),GreaterEqual(n,C0)))),C0),FreeQ(n,x)));
    // SeriesCoefficient(Tanh(x_),{x_Symbol,0,n_?NotListQ}):=Piecewise({{((-1+2^(1+n))*2^(1+n)*BernoulliB(1+n))/(1+n)!,Mod(n,2)==1&&n>=1}},0)/;FreeQ(n,x)
matcher.caseOf(SeriesCoefficient(Tanh(x_),list(x_Symbol,C0,PatternTest(n_,NotListQ))),
      Condition(Piecewise(list(list(Times(Plus(CN1,Power(C2,Plus(C1,n))),Power(C2,Plus(C1,n)),BernoulliB(Plus(C1,n)),Power(Factorial(Plus(C1,n)),CN1)),And(Equal(Mod(n,C2),C1),GreaterEqual(n,C1)))),C0),FreeQ(n,x)));
    // SeriesCoefficient(ArcCos(x_),{x_Symbol,0,n_?NotListQ}):=Piecewise({{Pi/2,n==0},{-Pochhammer(1/2,1/2*(-1+n))/(n*(1/2*(-1+n))!),n>0&&Mod(n,2)==1}},0)/;FreeQ(n,x)
matcher.caseOf(SeriesCoefficient(ArcCos(x_),list(x_Symbol,C0,PatternTest(n_,NotListQ))),
      Condition(Piecewise(list(list(CPiHalf,Equal(n,C0)),list(Times(CN1,Power(Times(n,Factorial(Times(C1D2,Plus(CN1,n)))),CN1),Pochhammer(C1D2,Times(C1D2,Plus(CN1,n)))),And(Greater(n,C0),Equal(Mod(n,C2),C1)))),C0),FreeQ(n,x)));
    // SeriesCoefficient(ArcCot(x_),{x_Symbol,0,n_?NotListQ}):=Piecewise({{I^(1+n)/n,n>0&&Mod(n,2)==1},{Pi/2,n==0}},0)/;FreeQ(n,x)
matcher.caseOf(SeriesCoefficient(ArcCot(x_),list(x_Symbol,C0,PatternTest(n_,NotListQ))),
      Condition(Piecewise(list(list(Times(Power(CI,Plus(C1,n)),Power(n,CN1)),And(Greater(n,C0),Equal(Mod(n,C2),C1))),list(CPiHalf,Equal(n,C0))),C0),FreeQ(n,x)));
    // SeriesCoefficient(ArcSin(x_),{x_Symbol,0,n_?NotListQ}):=Piecewise({{Pochhammer(1/2,1/2*(-1+n))/(n*(1/2*(-1+n))!),Mod(n,2)==1&&n>=0}},0)/;FreeQ(n,x)
matcher.caseOf(SeriesCoefficient(ArcSin(x_),list(x_Symbol,C0,PatternTest(n_,NotListQ))),
      Condition(Piecewise(list(list(Times(Power(Times(n,Factorial(Times(C1D2,Plus(CN1,n)))),CN1),Pochhammer(C1D2,Times(C1D2,Plus(CN1,n)))),And(Equal(Mod(n,C2),C1),GreaterEqual(n,C0)))),C0),FreeQ(n,x)));
    // SeriesCoefficient(ArcTan(x_),{x_Symbol,0,n_?NotListQ}):=Piecewise({{1/(I^(1-n)*n),Mod(n,2)==1&&n>=0}},0)/;FreeQ(n,x)
matcher.caseOf(SeriesCoefficient(ArcTan(x_),list(x_Symbol,C0,PatternTest(n_,NotListQ))),
      Condition(Piecewise(list(list(Times(Power(CI,Plus(CN1,n)),Power(n,CN1)),And(Equal(Mod(n,C2),C1),GreaterEqual(n,C0)))),C0),FreeQ(n,x)));
    // SeriesCoefficient(ArcCosh(x_),{x_Symbol,0,n_?NotListQ}):=Piecewise({{I*1/2*Pi,n==0},{((-1)*I*Pochhammer(1/2,1/2*(-1+n)))/(n*(1/2*(-1+n))!),n>=1&&Mod(n,2)==1}},0)/;FreeQ(n,x)
matcher.caseOf(SeriesCoefficient(ArcCosh(x_),list(x_Symbol,C0,PatternTest(n_,NotListQ))),
      Condition(Piecewise(list(list(Times(CI,C1D2,Pi),Equal(n,C0)),list(Times(CN1,CI,Power(Times(n,Factorial(Times(C1D2,Plus(CN1,n)))),CN1),Pochhammer(C1D2,Times(C1D2,Plus(CN1,n)))),And(GreaterEqual(n,C1),Equal(Mod(n,C2),C1)))),C0),FreeQ(n,x)));
    // SeriesCoefficient(ArcSinh(x_),{x_Symbol,0,n_?NotListQ}):=Piecewise({{Pochhammer(1/2,1/2*(-1+n))/(I^(1-n)*n*(1/2*(-1+n))!),Mod(n,2)==1&&n>=0}},0)/;FreeQ(n,x)
matcher.caseOf(SeriesCoefficient(ArcSinh(x_),list(x_Symbol,C0,PatternTest(n_,NotListQ))),
      Condition(Piecewise(list(list(Times(Power(CI,Plus(CN1,n)),Power(Times(n,Factorial(Times(C1D2,Plus(CN1,n)))),CN1),Pochhammer(C1D2,Times(C1D2,Plus(CN1,n)))),And(Equal(Mod(n,C2),C1),GreaterEqual(n,C0)))),C0),FreeQ(n,x)));
    // SeriesCoefficient(ArcTanh(x_),{x_Symbol,0,n_?NotListQ}):=Piecewise({{1/n,Mod(n,2)==1&&n>=0}},0)/;FreeQ(n,x)
matcher.caseOf(SeriesCoefficient(ArcTanh(x_),list(x_Symbol,C0,PatternTest(n_,NotListQ))),
      Condition(Piecewise(list(list(Power(n,CN1),And(Equal(Mod(n,C2),C1),GreaterEqual(n,C0)))),C0),FreeQ(n,x)));
    // SeriesCoefficient(Csc(x_),{x_Symbol,0,n_?NotListQ}):=Piecewise({{1,n==-1},{((-1)*I*2*I^n*(-1+2^n)*BernoulliB(1+n))/(1+n)!,n>=0&&Mod(n,2)==1}},0)/;FreeQ(n,x)
matcher.caseOf(SeriesCoefficient(Csc(x_),list(x_Symbol,C0,PatternTest(n_,NotListQ))),
      Condition(Piecewise(list(list(C1,Equal(n,CN1)),list(Times(CN1,CI,C2,Power(CI,n),Plus(CN1,Power(C2,n)),BernoulliB(Plus(C1,n)),Power(Factorial(Plus(C1,n)),CN1)),And(GreaterEqual(n,C0),Equal(Mod(n,C2),C1)))),C0),FreeQ(n,x)));
    // SeriesCoefficient(Sec(x_),{x_Symbol,0,n_?NotListQ}):=Piecewise({{(I^n*EulerE(n))/n!,Mod(n,2)==0&&n>=0}},0)/;FreeQ(n,x)
matcher.caseOf(SeriesCoefficient(Sec(x_),list(x_Symbol,C0,PatternTest(n_,NotListQ))),
      Condition(Piecewise(list(list(Times(Power(CI,n),EulerE(n),Power(Factorial(n),CN1)),And(Equal(Mod(n,C2),C0),GreaterEqual(n,C0)))),C0),FreeQ(n,x)));
    // SeriesCoefficient(Csch(x_),{x_Symbol,0,n_?NotListQ}):=Piecewise({{1,n==-1},{((-1)*2*(-1+2^n)*BernoulliB(1+n))/(1+n)!,n>=0&&Mod(n,2)==1}},0)/;FreeQ(n,x)
matcher.caseOf(SeriesCoefficient(Csch(x_),list(x_Symbol,C0,PatternTest(n_,NotListQ))),
      Condition(Piecewise(list(list(C1,Equal(n,CN1)),list(Times(CN1,C2,Plus(CN1,Power(C2,n)),BernoulliB(Plus(C1,n)),Power(Factorial(Plus(C1,n)),CN1)),And(GreaterEqual(n,C0),Equal(Mod(n,C2),C1)))),C0),FreeQ(n,x)));
    // SeriesCoefficient(Sech(x_),{x_Symbol,0,n_?NotListQ}):=Piecewise({{EulerE(n)/n!,n>=0}},0)/;FreeQ(n,x)
matcher.caseOf(SeriesCoefficient(Sech(x_),list(x_Symbol,C0,PatternTest(n_,NotListQ))),
      Condition(Piecewise(list(list(Times(EulerE(n),Power(Factorial(n),CN1)),GreaterEqual(n,C0))),C0),FreeQ(n,x)));
    // SeriesCoefficient(Cos(x_),{x_Symbol,a_,n_?NotListQ}):=Piecewise({{Cos(a+1/2*n*Pi)/n!,n>=0}},0)/;FreeQ(a,x)&&FreeQ(n,x)
matcher.caseOf(SeriesCoefficient(Cos(x_),list(x_Symbol,a_,PatternTest(n_,NotListQ))),
      Condition(Piecewise(list(list(Times(Cos(Plus(a,Times(C1D2,n,Pi))),Power(Factorial(n),CN1)),GreaterEqual(n,C0))),C0),And(FreeQ(a,x),FreeQ(n,x))));
    // SeriesCoefficient(Sin(x_),{x_Symbol,a_,n_?NotListQ}):=Piecewise({{Sin(a+1/2*n*Pi)/n!,n>=0}},0)/;FreeQ(a,x)&&FreeQ(n,x)
matcher.caseOf(SeriesCoefficient(Sin(x_),list(x_Symbol,a_,PatternTest(n_,NotListQ))),
      Condition(Piecewise(list(list(Times(Power(Factorial(n),CN1),Sin(Plus(a,Times(C1D2,n,Pi)))),GreaterEqual(n,C0))),C0),And(FreeQ(a,x),FreeQ(n,x))));
    // SeriesCoefficient(Cosh(x_),{x_Symbol,a_,n_?NotListQ}):=Piecewise({{Cosh(a)/n!,Mod(n,2)==0&&n>=0},{Sinh(a)/n!,Mod(n,2)==1&&n>=0}},0)/;FreeQ(a,x)&&FreeQ(n,x)
matcher.caseOf(SeriesCoefficient(Cosh(x_),list(x_Symbol,a_,PatternTest(n_,NotListQ))),
      Condition(Piecewise(list(list(Times(Cosh(a),Power(Factorial(n),CN1)),And(Equal(Mod(n,C2),C0),GreaterEqual(n,C0))),list(Times(Power(Factorial(n),CN1),Sinh(a)),And(Equal(Mod(n,C2),C1),GreaterEqual(n,C0)))),C0),And(FreeQ(a,x),FreeQ(n,x))));
    // SeriesCoefficient(Sinh(x_),{x_Symbol,a_,n_?NotListQ}):=Piecewise({{Cosh(a)/n!,Mod(n,2)==1&&n>=0},{Sinh(a)/n!,Mod(n,2)==0&&n>=0}},0)/;FreeQ(a,x)&&FreeQ(n,x)
matcher.caseOf(SeriesCoefficient(Sinh(x_),list(x_Symbol,a_,PatternTest(n_,NotListQ))),
      Condition(Piecewise(list(list(Times(Cosh(a),Power(Factorial(n),CN1)),And(Equal(Mod(n,C2),C1),GreaterEqual(n,C0))),list(Times(Power(Factorial(n),CN1),Sinh(a)),And(Equal(Mod(n,C2),C0),GreaterEqual(n,C0)))),C0),And(FreeQ(a,x),FreeQ(n,x))));
    // SeriesCoefficient(ArcCot(x_),{x_Symbol,a_,n_?NotListQ}):=Piecewise({{(I*((-I-a)^(-n)-1/(I-a)^n))/(2*n),n>0},{1/2*I*(Log((-I+a)/a)-Log((I+a)/a)),n==0}},0)/;FreeQ(a,x)&&FreeQ(n,x)
matcher.caseOf(SeriesCoefficient(ArcCot(x_),list(x_Symbol,a_,PatternTest(n_,NotListQ))),
      Condition(Piecewise(list(list(Times(CI,Subtract(Power(Subtract(CNI,a),Negate(n)),Power(Subtract(CI,a),Negate(n))),Power(Times(C2,n),CN1)),Greater(n,C0)),list(Times(C1D2,CI,Subtract(Log(Times(Power(a,CN1),Plus(CNI,a))),Log(Times(Power(a,CN1),Plus(CI,a))))),Equal(n,C0))),C0),And(FreeQ(a,x),FreeQ(n,x))));
    // SeriesCoefficient(ArcTan(x_),{x_Symbol,a_,n_?NotListQ}):=Piecewise({{((-1)*I*((-I-a)^(-n)-1/(I-a)^n))/(2*n),n>0},{1/2*I*(Log(1-I*a)-Log(1+I*a)),n==0}},0)/;FreeQ(a,x)&&FreeQ(n,x)
matcher.caseOf(SeriesCoefficient(ArcTan(x_),list(x_Symbol,a_,PatternTest(n_,NotListQ))),
      Condition(Piecewise(list(list(Times(CN1,CI,Subtract(Power(Subtract(CNI,a),Negate(n)),Power(Subtract(CI,a),Negate(n))),Power(Times(C2,n),CN1)),Greater(n,C0)),list(Times(C1D2,CI,Subtract(Log(Plus(C1,Times(CNI,a))),Log(Plus(C1,Times(CI,a))))),Equal(n,C0))),C0),And(FreeQ(a,x),FreeQ(n,x))));
    // SeriesCoefficient(ArcCoth(x_),{x_Symbol,a_,n_?NotListQ}):=Piecewise({{(-1/(-1-a)^n+(1-a)^(-n))/(2*n),n>0},{1/2*(Log(1+1/a)-Log((-1+a)/a)),n==0}},0)/;FreeQ(a,x)&&FreeQ(n,x)
matcher.caseOf(SeriesCoefficient(ArcCoth(x_),list(x_Symbol,a_,PatternTest(n_,NotListQ))),
      Condition(Piecewise(list(list(Times(Plus(Negate(Power(Subtract(CN1,a),Negate(n))),Power(Subtract(C1,a),Negate(n))),Power(Times(C2,n),CN1)),Greater(n,C0)),list(Times(C1D2,Subtract(Log(Plus(C1,Power(a,CN1))),Log(Times(Power(a,CN1),Plus(CN1,a))))),Equal(n,C0))),C0),And(FreeQ(a,x),FreeQ(n,x))));
    // SeriesCoefficient(ArcTanh(x_),{x_Symbol,a_,n_?NotListQ}):=Piecewise({{((-1)^n*((-1+a)^(-n)-1/(1+a)^n))/(2*n),n>=1},{1/2*(-Log(1-a)+Log(1+a)),n==0}},0)/;FreeQ(a,x)&&FreeQ(n,x)
matcher.caseOf(SeriesCoefficient(ArcTanh(x_),list(x_Symbol,a_,PatternTest(n_,NotListQ))),
      Condition(Piecewise(list(list(Times(Power(CN1,n),Subtract(Power(Plus(CN1,a),Negate(n)),Power(Plus(C1,a),Negate(n))),Power(Times(C2,n),CN1)),GreaterEqual(n,C1)),list(Times(C1D2,Plus(Negate(Log(Subtract(C1,a))),Log(Plus(C1,a)))),Equal(n,C0))),C0),And(FreeQ(a,x),FreeQ(n,x))));
    // SeriesCoefficient(ArcCos(x_),{x_Symbol,a_,1}):=-1/Sqrt(1-a^2)/;FreeQ(a,x)
matcher.caseOf(SeriesCoefficient(ArcCos(x_),list(x_Symbol,a_,C1)),
      Condition(Negate(Power(Subtract(C1,Sqr(a)),CN1D2)),FreeQ(a,x)));
    // SeriesCoefficient(ArcCot(x_),{x_Symbol,a_,1}):=-1/(1+a^2)/;FreeQ(a,x)
matcher.caseOf(SeriesCoefficient(ArcCot(x_),list(x_Symbol,a_,C1)),
      Condition(Negate(Power(Plus(C1,Sqr(a)),CN1)),FreeQ(a,x)));
    // SeriesCoefficient(ArcSin(x_),{x_Symbol,a_,1}):=1/Sqrt(1-a^2)/;FreeQ(a,x)
matcher.caseOf(SeriesCoefficient(ArcSin(x_),list(x_Symbol,a_,C1)),
      Condition(Power(Subtract(C1,Sqr(a)),CN1D2),FreeQ(a,x)));
    // SeriesCoefficient(ArcTan(x_),{x_Symbol,a_,1}):=1/(1+a^2)/;FreeQ(a,x)
matcher.caseOf(SeriesCoefficient(ArcTan(x_),list(x_Symbol,a_,C1)),
      Condition(Power(Plus(C1,Sqr(a)),CN1),FreeQ(a,x)));
    // SeriesCoefficient(ArcCsc(x_),{x_Symbol,a_,1}):=-1/(Sqrt(1-1/a^2)*a^2)/;FreeQ(a,x)
matcher.caseOf(SeriesCoefficient(ArcCsc(x_),list(x_Symbol,a_,C1)),
      Condition(Negate(Power(Times(Sqrt(Subtract(C1,Power(a,CN2))),Sqr(a)),CN1)),FreeQ(a,x)));
    // SeriesCoefficient(ArcSec(x_),{x_Symbol,a_,1}):=1/(Sqrt(1-1/a^2)*a^2)/;FreeQ(a,x)
matcher.caseOf(SeriesCoefficient(ArcSec(x_),list(x_Symbol,a_,C1)),
      Condition(Power(Times(Sqrt(Subtract(C1,Power(a,CN2))),Sqr(a)),CN1),FreeQ(a,x)));
    // SeriesCoefficient(Log(b_.+c_.*x_),{x_Symbol,a_,n_?NotListQ}):=If(c===1,Piecewise({{(-1)^(1+n)/((a+b)^n*n),n>=1},{Log(a+b),n==0}},0),Piecewise({{-(-c/(b+a*c))^n/n,n>0},{Log(b+a*c),n==0}},0))/;FreeQ({a,b,c,n},x)
matcher.caseOf(SeriesCoefficient(Log(Plus(b_DEFAULT,Times(c_DEFAULT,x_))),list(x_Symbol,a_,PatternTest(n_,NotListQ))),
      Condition(If(SameQ(c,C1),Piecewise(list(list(Times(Power(CN1,Plus(C1,n)),Power(Times(Power(Plus(a,b),n),n),CN1)),GreaterEqual(n,C1)),list(Log(Plus(a,b)),Equal(n,C0))),C0),Piecewise(list(list(Times(CN1,Power(Times(CN1,c,Power(Plus(b,Times(a,c)),CN1)),n),Power(n,CN1)),Greater(n,C0)),list(Log(Plus(b,Times(a,c))),Equal(n,C0))),C0)),FreeQ(List(a,b,c,n),x)));
    // SeriesCoefficient(ProductLog(x_),{x_Symbol,0,n_?NotListQ}):=Piecewise({{1/((-n)^(1-n)*n!),n>=1}},0)/;FreeQ(n,x)
matcher.caseOf(SeriesCoefficient(ProductLog(x_),list(x_Symbol,C0,PatternTest(n_,NotListQ))),
      Condition(Piecewise(list(list(Times(Power(Negate(n),Plus(CN1,n)),Power(Factorial(n),CN1)),GreaterEqual(n,C1))),C0),FreeQ(n,x)));
    // SeriesCoefficient(PolyGamma(0,x_),{x_Symbol,0,n_?NotListQ}):=Piecewise({{-1,n==-1},{-EulerGamma,n==0},{(-1)^(1+n)*Zeta(1+n),n>=1}},0)/;FreeQ(n,x)
matcher.caseOf(SeriesCoefficient(PolyGamma(C0,x_),list(x_Symbol,C0,PatternTest(n_,NotListQ))),
      Condition(Piecewise(list(list(CN1,Equal(n,CN1)),list(Negate(EulerGamma),Equal(n,C0)),list(Times(Power(CN1,Plus(C1,n)),Zeta(Plus(C1,n))),GreaterEqual(n,C1))),C0),FreeQ(n,x)));
    // SeriesCoefficient(PolyLog(k_,x_),{x_Symbol,0,n_?NotListQ}):=Piecewise({{n^(-k),n>=1}},0)/;FreeQ(k,x)&&FreeQ(n,x)
matcher.caseOf(SeriesCoefficient(PolyLog(k_,x_),list(x_Symbol,C0,PatternTest(n_,NotListQ))),
      Condition(Piecewise(list(list(Power(n,Negate(k)),GreaterEqual(n,C1))),C0),And(FreeQ(k,x),FreeQ(n,x))));
    // SeriesCoefficient(ChebyshevT(k_,x_),{x_Symbol,0,n_?NotListQ}):=Piecewise({{((-1/2)^n*Sqrt(Pi)*Gamma(1/2+n)*Pochhammer(-k,n)*Pochhammer(k,n))/(n!*Gamma(1/2*(1-k+n))*Gamma(1/2*(1+k+n))*Pochhammer(1/2,n)),n>=0}},0)/;FreeQ(k,x)&&FreeQ(n,x)
matcher.caseOf(SeriesCoefficient(ChebyshevT(k_,x_),list(x_Symbol,C0,PatternTest(n_,NotListQ))),
      Condition(Piecewise(list(list(Times(Power(CN1D2,n),CSqrtPi,Gamma(Plus(C1D2,n)),Power(Times(Factorial(n),Gamma(Times(C1D2,Plus(C1,Negate(k),n))),Gamma(Times(C1D2,Plus(C1,k,n))),Pochhammer(C1D2,n)),CN1),Pochhammer(Negate(k),n),Pochhammer(k,n)),GreaterEqual(n,C0))),C0),And(FreeQ(k,x),FreeQ(n,x))));
    // SeriesCoefficient(ChebyshevU(k_,x_),{x_Symbol,0,n_?NotListQ}):=Piecewise({{((-1/2)^n*Sqrt(Pi)*Gamma(3/2+n)*Pochhammer(-k,n)*Pochhammer(2+k,n))/(n!*Gamma(1/2*(1-k+n))*Gamma(1/2*(3+k+n))*Pochhammer(3/2,n)),n>=0}},0)/;FreeQ(k,x)&&FreeQ(n,x)
matcher.caseOf(SeriesCoefficient(ChebyshevU(k_,x_),list(x_Symbol,C0,PatternTest(n_,NotListQ))),
      Condition(Piecewise(list(list(Times(Power(CN1D2,n),CSqrtPi,Gamma(Plus(QQ(3L,2L),n)),Power(Times(Factorial(n),Gamma(Times(C1D2,Plus(C1,Negate(k),n))),Gamma(Times(C1D2,Plus(C3,k,n))),Pochhammer(QQ(3L,2L),n)),CN1),Pochhammer(Negate(k),n),Pochhammer(Plus(C2,k),n)),GreaterEqual(n,C0))),C0),And(FreeQ(k,x),FreeQ(n,x))));
    // SeriesCoefficient(EllipticE(x_),{x_Symbol,0,n_?NotListQ}):=Piecewise({{(-Gamma(-1/2+n)*Gamma(1/2+n))/(4*Gamma(1+n)^2),n>=0}},0)/;FreeQ(n,x)
matcher.caseOf(SeriesCoefficient(EllipticE(x_),list(x_Symbol,C0,PatternTest(n_,NotListQ))),
      Condition(Piecewise(list(list(Times(CN1,Gamma(Plus(CN1D2,n)),Gamma(Plus(C1D2,n)),Power(Times(C4,Sqr(Gamma(Plus(C1,n)))),CN1)),GreaterEqual(n,C0))),C0),FreeQ(n,x)));
    // SeriesCoefficient(EllipticK(x_),{x_Symbol,0,n_?NotListQ}):=Piecewise({{Gamma(1/2+n)^2/(2*Gamma(1+n)^2),n>=0}},0)/;FreeQ(n,x)
matcher.caseOf(SeriesCoefficient(EllipticK(x_),list(x_Symbol,C0,PatternTest(n_,NotListQ))),
      Condition(Piecewise(list(list(Times(Sqr(Gamma(Plus(C1D2,n))),Power(Times(C2,Sqr(Gamma(Plus(C1,n)))),CN1)),GreaterEqual(n,C0))),C0),FreeQ(n,x)));
return matcher;
}
}