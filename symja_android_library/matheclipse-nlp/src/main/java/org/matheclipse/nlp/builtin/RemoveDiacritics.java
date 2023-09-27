package org.matheclipse.nlp.builtin;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import com.ibm.icu.text.Transliterator;

/**
 *
 *
 * <pre>
 * <code>RemoveDiacritics(&quot;string&quot;)
 * </code>
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * replace characters with diacritics with characters without diacritics.
 *
 * </blockquote>
 *
 * <p>
 * See:
 *
 * <ul>
 * <li><a href="https://en.wikipedia.org/wiki/Diacritic">Wikipedia - Diacritic</a>
 * </ul>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * <code>&gt;&gt; RemoveDiacritics(&quot;éèáàâ&quot;)
 * eeaaa
 * </code>
 * </pre>
 */

public class RemoveDiacritics extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (!(ast.arg1() instanceof IStringX)) {
      return F.NIL;
    }
    String str = ast.arg1().toString();
    Transliterator transform = Transliterator.getInstance("NFD; [:Nonspacing Mark:] Remove; NFC");
    String value = transform.transliterate(str);
    return F.$str(value);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }
}
