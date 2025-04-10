package org.matheclipse.core.reflection.system.rules;

import static org.matheclipse.core.expression.F.*;
import org.matheclipse.core.interfaces.IAST;

/**
 * <p>Generated by <code>org.matheclipse.core.preprocessor.RulePreprocessor</code>.</p>
 * <p>See GIT repository at: <a href="https://github.com/axkr/symja_android_library">github.com/axkr/symja_android_library under the tools directory</a>.</p>
 */
public class ConjugateRules {
  /**
   * <ul>
   * <li>index 0 - number of equal rules in <code>RULES</code></li>
	 * </ul>
	 */
  final public static int[] SIZES = { 1, 2 };

  final public static IAST RULES = List(
    IInit(Conjugate, SIZES),
    // Conjugate(Undefined)=Undefined
    ISet(Conjugate(Undefined),
      Undefined, true),
    // Conjugate(Erf(x_)):=Erf(Conjugate(x))
    ISetDelayed(Conjugate(Erf(x_)),
      Erf(Conjugate(x))),
    // Conjugate(Erfc(x_)):=Erfc(Conjugate(x))
    ISetDelayed(Conjugate(Erfc(x_)),
      Erfc(Conjugate(x)))
  );
}
