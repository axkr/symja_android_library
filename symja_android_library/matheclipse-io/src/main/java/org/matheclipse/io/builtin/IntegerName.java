package org.matheclipse.io.builtin;

import java.util.Locale;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IStringX;
import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.text.RuleBasedNumberFormat;

/**
 * <pre>
 * <code>IntegerName(integer-number)
 * </code>
 * </pre>
 * 
 * <p>
 * gives the spoken number string of <code>integer-number</code> in language <code>English</code>.
 * </p>
 * 
 * <pre>
 * <code>IntegerName(integer-number, &quot;language&quot;)
 * </code>
 * </pre>
 * 
 * <p>
 * gives the spoken number string of <code>integer-number</code> in language <code>language</code>.
 * </p>
 * 
 * <p>
 * See
 * </p>
 * <ul>
 * <li><a href="https://en.wikipedia.org/wiki/Integer">Wikipedia - Integer</a></li>
 * </ul>
 * <h3>Examples</h3>
 * 
 * <pre>
 * <code>&gt;&gt; IntegerName(0) 
 * zero
 * 
 * &gt;&gt; IntegerName(42) 
 * forty-two
 * 
 * &gt;&gt; IntegerName(-42)
 * minus forty-two
 * 
 * &gt;&gt; IntegerName(-123007,&quot;German&quot;)
 * minus einhundertdreiundzwanzigtausendsieben
 *              
 * &gt;&gt; IntegerName(123007,&quot;Spanish&quot;) 
 * ciento veintitrés mil siete
 * </code>
 * </pre>
 */
public class IntegerName extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    if (ast.arg1().isList()) {
      return ((IAST) ast.arg1()).mapThread(ast, 1);
    }
    if (arg1.isInteger()) {
      NumberFormat formatter = null;
      try {
        long value = ((IInteger) arg1).toLong();
        if (value != Integer.MIN_VALUE && ast.isAST1()) {
          formatter = new RuleBasedNumberFormat(RuleBasedNumberFormat.SPELLOUT);
          String textNumber = formatter.format(value);
          if (textNumber != null) {
            return F.stringx(textNumber);
          }
        }
        IStringX language = F.stringx("English");
        IStringX qual = F.stringx("Words");
        if (ast.isAST2()) {
          if (!ast.arg2().isString()) {
            return F.NIL;
          }
          IStringX arg2 = (IStringX) ast.arg2();
          if (arg2.isString("Dutch") || arg2.isString("Finnish") || arg2.isString("English")
              || arg2.isString("Esperanto") || arg2.isString("French") || arg2.isString("German")
              || arg2.isString("Hungarian") || arg2.isString("Italian") || arg2.isString("Latin")
              || arg2.isString("Polish") || arg2.isString("Portuguese") || arg2.isString("Romanian")
              || arg2.isString("Russian") || arg2.isString("Spanish") || arg2.isString("Swedish")
              || arg2.isString("Tongan") || arg2.isString("Turkish")) {
            language = arg2;
          } else {
            qual = arg2;
          }
        }

        if (qual.isString("Words")) {
          if (language.isString("Dutch")) {
            formatter = new RuleBasedNumberFormat(new Locale("nl"), RuleBasedNumberFormat.SPELLOUT);
          } else if (language.isString("English")) {
            formatter = new RuleBasedNumberFormat(Locale.ENGLISH, RuleBasedNumberFormat.SPELLOUT);
          } else if (language.isString("Esperanto")) {
            formatter = new RuleBasedNumberFormat(new Locale("eo"), RuleBasedNumberFormat.SPELLOUT);
          } else if (language.isString("Finnish")) {
            formatter = new RuleBasedNumberFormat(new Locale("fi"), RuleBasedNumberFormat.SPELLOUT);
          } else if (language.isString("French")) {
            formatter = new RuleBasedNumberFormat(Locale.FRENCH, RuleBasedNumberFormat.SPELLOUT);
          } else if (language.isString("German")) {
            formatter = new RuleBasedNumberFormat(Locale.GERMAN, RuleBasedNumberFormat.SPELLOUT);
          } else if (language.isString("Hungarian")) {
            formatter = new RuleBasedNumberFormat(new Locale("hu"), RuleBasedNumberFormat.SPELLOUT);
          } else if (language.isString("Italian")) {
            formatter = new RuleBasedNumberFormat(Locale.ITALIAN, RuleBasedNumberFormat.SPELLOUT);
          } else if (language.isString("Latin")) {
            formatter =
                new RuleBasedNumberFormat(new Locale("vai"), RuleBasedNumberFormat.SPELLOUT);
          } else if (language.isString("Polish")) {
            formatter = new RuleBasedNumberFormat(new Locale("pl"), RuleBasedNumberFormat.SPELLOUT);
          } else if (language.isString("Portuguese")) {
            formatter = new RuleBasedNumberFormat(new Locale("pt"), RuleBasedNumberFormat.SPELLOUT);
          } else if (language.isString("Romanian")) {
            formatter = new RuleBasedNumberFormat(new Locale("ro"), RuleBasedNumberFormat.SPELLOUT);
          } else if (language.isString("Russian")) {
            formatter = new RuleBasedNumberFormat(new Locale("ru"), RuleBasedNumberFormat.SPELLOUT);
          } else if (language.isString("Spanish")) {
            formatter = new RuleBasedNumberFormat(new Locale("es"), RuleBasedNumberFormat.SPELLOUT);
          } else if (language.isString("Swedish")) {
            formatter = new RuleBasedNumberFormat(new Locale("sv"), RuleBasedNumberFormat.SPELLOUT);
          } else if (language.isString("Tongan")) {
            formatter = new RuleBasedNumberFormat(new Locale("sv"), RuleBasedNumberFormat.SPELLOUT);
          } else if (language.isString("Turkish")) {
            formatter = new RuleBasedNumberFormat(new Locale("tr"), RuleBasedNumberFormat.SPELLOUT);
          }
          if (formatter != null) {
            String textNumber = formatter.format(value);
            if (textNumber != null) {
              return F.stringx(textNumber);
            }
          }
        }
      } catch (Exception ex) {
        // `1`.
        throw new ArgumentTypeException("error", F.List(F.stringx(ex.getMessage())));
      }
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }
}
