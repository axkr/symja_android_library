package org.matheclipse.compile;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The normalized value of the <code>CompilationOptions</code> option of <code>Compile</code> and
 * <code>CompilePrint</code>.
 *
 * <p>
 * The value is <code>Automatic</code>, or a rule or list of rules over the three settings below,
 * each of which takes <code>True</code>, <code>False</code> or <code>Automatic</code>:
 *
 * <table>
 * <caption>settings and their values</caption>
 * <tr>
 * <th>setting
 * <th>default
 * <th>meaning
 * <tr>
 * <td><code>"ExpressionOptimization"</code>
 * <td>Automatic
 * <td>optimize the expression to avoid computing the same thing twice
 * <tr>
 * <td><code>"InlineCompiledFunctions"</code>
 * <td>Automatic
 * <td>expand a call to another compiled function into this one
 * <tr>
 * <td><code>"InlineExternalDefinitions"</code>
 * <td>Automatic
 * <td>expand the definitions of other symbols into this one
 * </table>
 *
 * <p>
 * Only <code>"InlineCompiledFunctions"</code> is acted on, by {@link InlineDefinitions}. Symja has
 * no way to call another compiled function from generated code, so <code>Automatic</code> inlines
 * every such call; setting it to <code>False</code> leaves the call in place, and the generated
 * source then does not compile.
 *
 * <p>
 * <code>"ExpressionOptimization"</code> and <code>"InlineExternalDefinitions"</code> are read and
 * stored, but nothing acts on them - the first needs a common subexpression pass over the generated
 * code, the second needs the definitions of a symbol to be turned into an inlinable body, which for
 * anything but a compiled function means dealing with patterns and conditions.
 */
public final class CompilationOptions {

  private static final String EXPRESSION_OPTIMIZATION = "ExpressionOptimization";
  private static final String INLINE_COMPILED_FUNCTIONS = "InlineCompiledFunctions";
  private static final String INLINE_EXTERNAL_DEFINITIONS = "InlineExternalDefinitions";

  /** The settings of <code>Automatic</code>, which is the default value of the option. */
  public static final CompilationOptions DEFAULT = new CompilationOptions();

  private IExpr expressionOptimization = S.Automatic;
  private IExpr inlineCompiledFunctions = S.Automatic;
  private IExpr inlineExternalDefinitions = S.Automatic;

  /** The canonical expression of {@link #toExpr()}, built once on demand. */
  private IAST expr = null;

  private CompilationOptions() {}

  private CompilationOptions(CompilationOptions other) {
    this.expressionOptimization = other.expressionOptimization;
    this.inlineCompiledFunctions = other.inlineCompiledFunctions;
    this.inlineExternalDefinitions = other.inlineExternalDefinitions;
  }

  /**
   * Normalize the value written for the <code>CompilationOptions</code> option.
   *
   * @param setting the value of the option, as the evaluator received it
   * @param ast the <code>Compile</code> or <code>CompilePrint</code> expression, whose head names
   *        the symbol any message is reported for
   * @param engine the evaluation engine
   * @return the settings the value asks for; {@link #DEFAULT} if it asks for nothing else. Never
   *         <code>null</code> - a value which cannot be read is reported and ignored.
   */
  public static CompilationOptions parse(IExpr setting, IAST ast, EvalEngine engine) {
    if (setting == null || !setting.isPresent() || setting == S.Automatic) {
      return DEFAULT;
    }
    CompilationOptions result = new CompilationOptions(DEFAULT);
    if (!result.apply(setting, ast, engine)) {
      printInvalidSetting(setting, ast, engine);
    }
    return result.equals(DEFAULT) ? DEFAULT : result;
  }

  private boolean apply(IExpr setting, IAST ast, EvalEngine engine) {
    if (setting.isRuleAST()) {
      applyRule((IAST) setting, ast, engine);
      return true;
    }
    if (setting.isList()) {
      IAST list = (IAST) setting;
      for (int i = 1; i < list.size(); i++) {
        IExpr element = list.get(i);
        if (element.isRuleAST()) {
          applyRule((IAST) element, ast, engine);
        } else {
          printInvalidSetting(element, ast, engine);
        }
      }
      return true;
    }
    return false;
  }

  private void applyRule(IAST rule, IAST ast, EvalEngine engine) {
    IExpr name = rule.arg1();
    IExpr value = rule.arg2();
    if (!name.isString()) {
      // Option name `2` not found in defaults for `1`.
      Errors.printMessage(ast.topHead(), "optnf", F.list(S.CompilationOptions, name), engine);
      return;
    }
    switch (name.toString()) {
      case EXPRESSION_OPTIMIZATION:
        expressionOptimization = readSetting(name, value, expressionOptimization, ast, engine);
        return;
      case INLINE_COMPILED_FUNCTIONS:
        inlineCompiledFunctions = readSetting(name, value, inlineCompiledFunctions, ast, engine);
        return;
      case INLINE_EXTERNAL_DEFINITIONS:
        inlineExternalDefinitions =
            readSetting(name, value, inlineExternalDefinitions, ast, engine);
        return;
      default:
        // Option name `2` not found in defaults for `1`.
        Errors.printMessage(ast.topHead(), "optnf", F.list(S.CompilationOptions, name), engine);
        return;
    }
  }

  /**
   * The <code>True</code>, <code>False</code> or <code>Automatic</code> written for setting
   * <code>name</code>, or <code>currentValue</code> unchanged if it is none of them.
   */
  private static IExpr readSetting(IExpr name, IExpr value, IExpr currentValue, IAST ast,
      EvalEngine engine) {
    if (value.isTrue() || value.isFalse() || value == S.Automatic) {
      return value;
    }
    // Value of option `1` -> `2` should be True, False or Automatic.
    Errors.printMessage(ast.topHead(), "opttfa", F.list(name, value), engine);
    return currentValue;
  }

  private static void printInvalidSetting(IExpr setting, IAST ast, EvalEngine engine) {
    // Value of option CompilationOptions -> `1` should be ...
    Errors.printMessage(ast.topHead(), "cfco", F.list(setting), engine);
  }

  /**
   * Whether a call to another compiled function is expanded into this one. <code>Automatic</code>
   * inlines, because a call which is left in place cannot be generated - see the note on this
   * class.
   */
  public boolean isInlineCompiledFunctions() {
    return !inlineCompiledFunctions.isFalse();
  }

  /**
   * Whether the definitions of other symbols are expanded into this one.
   *
   * <p>
   * Nothing reads this yet - see the note on this class.
   */
  public IExpr getInlineExternalDefinitions() {
    return inlineExternalDefinitions;
  }

  /**
   * Whether the expression is optimized to avoid computing the same thing twice.
   *
   * <p>
   * Nothing reads this yet - see the note on this class.
   */
  public IExpr getExpressionOptimization() {
    return expressionOptimization;
  }

  /** These settings as a list of rules, in a fixed order, whatever form they were written in. */
  public IAST toExpr() {
    if (expr == null) {
      expr = F.List( //
          F.Rule(F.stringx(EXPRESSION_OPTIMIZATION), expressionOptimization), //
          F.Rule(F.stringx(INLINE_COMPILED_FUNCTIONS), inlineCompiledFunctions), //
          F.Rule(F.stringx(INLINE_EXTERNAL_DEFINITIONS), inlineExternalDefinitions));
    }
    return expr;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof CompilationOptions)) {
      return false;
    }
    CompilationOptions other = (CompilationOptions) obj;
    return expressionOptimization.equals(other.expressionOptimization) //
        && inlineCompiledFunctions.equals(other.inlineCompiledFunctions) //
        && inlineExternalDefinitions.equals(other.inlineExternalDefinitions);
  }

  @Override
  public int hashCode() {
    int hash = expressionOptimization.hashCode();
    hash = 31 * hash + inlineCompiledFunctions.hashCode();
    hash = 31 * hash + inlineExternalDefinitions.hashCode();
    return hash;
  }

  @Override
  public String toString() {
    return toExpr().toString();
  }
}
