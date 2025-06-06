package org.matheclipse.core.reflection.system.rules;

import static org.matheclipse.core.expression.F.*;
import org.matheclipse.core.interfaces.IAST;

/**
 * <p>Generated by <code>org.matheclipse.core.preprocessor.RulePreprocessor</code>.</p>
 * <p>See GIT repository at: <a href="https://github.com/axkr/symja_android_library">github.com/axkr/symja_android_library under the tools directory</a>.</p>
 */
public class WhittakerMRules {
  final public static IAST RULES = List(
    // WhittakerM(n_,m_,0):=0/;Re(m)>-1/2
    ISetDelayed(WhittakerM(n_,m_,C0),
      Condition(C0,Greater(Re(m),CN1D2))),
    // WhittakerM(n_,m_,0):=ComplexInfinity/;Re(m)<-1/2
    ISetDelayed(WhittakerM(n_,m_,C0),
      Condition(CComplexInfinity,Less(Re(m),CN1D2)))
  );
}
