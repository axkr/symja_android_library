package org.matheclipse.core.reflection.system.rules;

import static org.matheclipse.core.expression.F.*;
import org.matheclipse.core.interfaces.IAST;

/**
 * <p>Generated by <code>org.matheclipse.core.preprocessor.RulePreprocessor</code>.</p>
 * <p>See GIT repository at: <a href="https://github.com/axkr/symja_android_library">github.com/axkr/symja_android_library under the tools directory</a>.</p>
 */
public class JacobiPRules {
  /**
   * <ul>
   * <li>index 0 - number of equal rules in <code>RULES</code></li>
	 * </ul>
	 */
  final public static int[] SIZES = { 0, 1 };

  final public static IAST RULES = List(
    IInit(JacobiP, SIZES),
    // JacobiP(0,a_,b_,z_):=1
    ISetDelayed(JacobiP(C0,a_,b_,z_),
      C1),
    // JacobiP(n_,0,0,z_):=LegendreP(n,z)
    ISetDelayed(JacobiP(n_,C0,C0,z_),
      LegendreP(n,z))
  );
}
