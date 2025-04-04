package org.matheclipse.core.reflection.system.rules;

import static org.matheclipse.core.expression.F.*;
import org.matheclipse.core.interfaces.IAST;

/**
 * <p>Generated by <code>org.matheclipse.core.preprocessor.RulePreprocessor</code>.</p>
 * <p>See GIT repository at: <a href="https://github.com/axkr/symja_android_library">github.com/axkr/symja_android_library under the tools directory</a>.</p>
 */
public class Hypergeometric1F1Rules {
  /**
   * <ul>
   * <li>index 0 - number of equal rules in <code>RULES</code></li>
	 * </ul>
	 */
  final public static int[] SIZES = { 0, 2 };

  final public static IAST RULES = List(
    IInit(Hypergeometric1F1, SIZES),
    // Hypergeometric1F1(1/2,3/2,z_):=(Sqrt(Pi)*Erfi(Sqrt(z)))/(2*Sqrt(z))
    ISetDelayed(Hypergeometric1F1(C1D2,QQ(3L,2L),z_),
      Times(CSqrtPi,Power(Times(C2,Sqrt(z)),CN1),Erfi(Sqrt(z)))),
    // Hypergeometric1F1(1,1/2,z_):=1+E^z*Sqrt(Pi)*Sqrt(z)*Erf(Sqrt(z))
    ISetDelayed(Hypergeometric1F1(C1,C1D2,z_),
      Plus(C1,Times(Exp(z),CSqrtPi,Sqrt(z),Erf(Sqrt(z))))),
    // Hypergeometric1F1(a_Integer,b_,z_):=Module({n=-a},Sum(FactorialPower(-n,k,-1)/FunctionExpand(FactorialPower(b,k,-1))*z^k/k!,{k,0,n})/;a<0&&!TrueQ(b∈Integers&&b<=0&&b>a))
    ISetDelayed(Hypergeometric1F1($p(a, Integer),b_,z_),
      Module(list(Set(n,Negate(a))),Condition(Sum(Times(FactorialPower(Negate(n),k,CN1),Power(FunctionExpand(FactorialPower(b,k,CN1)),CN1),Power(z,k),Power(Factorial(k),CN1)),list(k,C0,n)),And(Less(a,C0),Not(TrueQ(And(Element(b,Integers),LessEqual(b,C0),Greater(b,a))))))))
  );
}
