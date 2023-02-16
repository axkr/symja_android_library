package org.matheclipse.io.builtin;

import java.util.HashMap;
import java.util.Map;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import com.ibm.icu.text.Transliterator;


/**
 *
 *
 * <pre>
 * <code>Transliterate(&quot;string&quot;)
 * </code>
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * try converting the given string to a similar ASCII string
 *
 * </blockquote>
 *
 * <p>
 * See:
 *
 * <ul>
 * <li><a href="https://en.wikipedia.org/wiki/Transliteration">Wikipedia - Transliteration</a>
 * <li><a href=
 * "https://unicode-org.github.io/icu/userguide/transforms/general/">unicode-org.github.io - General
 * Transforms </a>
 * </ul>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * <code>&gt;&gt; Transliterate(&quot;Горбачёв, Михаил Сергеевич&quot;)
 * Gorbacev, Mihail Sergeevic
 * </code>
 * </pre>
 */
public class Transliterate extends AbstractFunctionEvaluator {
  /** Map English to Latin for the ICU <code>Transliterate()</code> function. */
  private static final Map<String, String> TRANSLITERATE_MAP = new HashMap<String, String>();

  static {
    TRANSLITERATE_MAP.put("English", "Latin");
  }


  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    if (arg1.isList()) {
      return ((IAST) arg1).mapThread(ast, 1);
    }
    if (!arg1.isString()) {
      return F.NIL;
    }
    if (ast.isAST2()) {
      IExpr arg2 = ast.arg2();
      if (arg2.isRuleAST()) {
        if (arg2.first().isString() //
            && arg2.second().isString()) {
          try {
            String str1 = mapToICU4J(arg2.first().toString());
            String str2 = mapToICU4J(arg2.second().toString());
            String str = ast.arg1().toString();
            Transliterator transform = Transliterator.getInstance(str1 + "-" + str2);
            String result = transform.transliterate(str);
            return F.$str(result);
          } catch (IllegalArgumentException iae) {

          }
        }
      } else if (arg2.isString()) {
        try {
          String str1 = "Latin";
          String str2 = mapToICU4J(arg2.toString());
          String str = ast.arg1().toString();
          Transliterator transform = Transliterator.getInstance(str1 + "-" + str2);
          String result = transform.transliterate(str);
          return F.$str(result);
        } catch (IllegalArgumentException iae) {

        }
      }
      return F.NIL;
    }
    if (ast.isAST1()) {
      String str = ast.arg1().toString();
      Transliterator transform = Transliterator.getInstance("Any-Latin");
      String latin = transform.transliterate(str);
      transform = Transliterator.getInstance("Latin-ASCII");
      // "NFD; [:Nonspacing Mark:] Remove; NFC."
      String ascii = transform.transliterate(latin);
      return F.$str(ascii);
    }
    return F.NIL;
  }

  private static String mapToICU4J(String language) {
    String temp = TRANSLITERATE_MAP.get(language);
    if (temp != null) {
      language = temp;
    }
    return language;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }

}
