package org.matheclipse.compile;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The normalized value of the <code>RuntimeOptions</code> option of <code>Compile</code> and
 * <code>CompilePrint</code>.
 *
 * <p>
 * The option is written either as one of the names <code>"Speed"</code> and <code>"Quality"</code>,
 * as <code>Automatic</code>, or as a rule or list of rules over the six settings below; a list may
 * mix a leading name with rules which then override it. This class turns all of those forms into
 * one flat set of values, so that the rest of the module asks {@link #isEvaluateSymbolically()}
 * rather than picking the expression apart again.
 *
 * <p>
 * Only {@link #getRuntimeErrorHandler()} is an arbitrary expression; the other five settings are
 * booleans.
 *
 * <table>
 * <caption>settings and their values</caption>
 * <tr>
 * <th>setting
 * <th>default / <code>Automatic</code>
 * <th><code>"Speed"</code>
 * <th><code>"Quality"</code>
 * <tr>
 * <td><code>"CatchMachineIntegerOverflow"</code>
 * <td>True
 * <td>False
 * <td>True
 * <tr>
 * <td><code>"CatchMachineOverflow"</code>
 * <td>False
 * <td>False
 * <td>True
 * <tr>
 * <td><code>"CompareWithTolerance"</code>
 * <td>True
 * <td>True
 * <td>True
 * <tr>
 * <td><code>"EvaluateSymbolically"</code>
 * <td>True
 * <td>True
 * <td>True
 * <tr>
 * <td><code>"RuntimeErrorHandler"</code>
 * <td>Evaluate
 * <td>Evaluate
 * <td>Evaluate
 * <tr>
 * <td><code>"WarningMessages"</code>
 * <td>True
 * <td>True
 * <td>True
 * </table>
 *
 * <p>
 * An instance is immutable once {@link #parse(IExpr, IAST, EvalEngine)} has returned it. Parsing
 * never fails: an entry which is not understood is reported as a message and then skipped, so that
 * one bad setting does not discard the valid ones written beside it.
 *
 * <p>
 * <b>Note:</b> <code>"CatchMachineOverflow"</code> and <code>"CompareWithTolerance"</code> are read
 * and stored, but nothing acts on them: the first needs a guard around every real operation the
 * code generator emits, and the second a comparison which knows about a tolerance, and neither
 * exists yet. A value written for them is therefore accepted and has no effect. The other four
 * settings are honoured in {@link org.matheclipse.compile.builtin.CompilerFunctions}.
 */
public final class RuntimeOptions {

  private static final String CATCH_MACHINE_INTEGER_OVERFLOW = "CatchMachineIntegerOverflow";
  private static final String CATCH_MACHINE_OVERFLOW = "CatchMachineOverflow";
  private static final String COMPARE_WITH_TOLERANCE = "CompareWithTolerance";
  private static final String EVALUATE_SYMBOLICALLY = "EvaluateSymbolically";
  private static final String RUNTIME_ERROR_HANDLER = "RuntimeErrorHandler";
  private static final String WARNING_MESSAGES = "WarningMessages";

  /** The name which turns the runtime checks off. */
  private static final String SPEED = "Speed";

  /** The name which turns the runtime checks on. */
  private static final String QUALITY = "Quality";

  /**
   * The settings of <code>Automatic</code>, which is the default value of the option.
   *
   * <p>
   * This instance is shared, which is why the fields are only ever assigned through
   * {@link #parse(IExpr, IAST, EvalEngine)} on a private copy.
   */
  public static final RuntimeOptions DEFAULT = new RuntimeOptions();

  private boolean catchMachineIntegerOverflow = true;
  private boolean catchMachineOverflow = false;
  private boolean compareWithTolerance = true;
  private boolean evaluateSymbolically = true;
  private IExpr runtimeErrorHandler = S.Evaluate;
  private boolean warningMessages = true;

  /** The canonical expression of {@link #toExpr()}, built once on demand. */
  private IAST expr = null;

  private RuntimeOptions() {}

  private RuntimeOptions(RuntimeOptions other) {
    this.catchMachineIntegerOverflow = other.catchMachineIntegerOverflow;
    this.catchMachineOverflow = other.catchMachineOverflow;
    this.compareWithTolerance = other.compareWithTolerance;
    this.evaluateSymbolically = other.evaluateSymbolically;
    this.runtimeErrorHandler = other.runtimeErrorHandler;
    this.warningMessages = other.warningMessages;
  }

  /**
   * Normalize the value written for the <code>RuntimeOptions</code> option.
   *
   * @param setting the value of the option, as the evaluator received it
   * @param ast the <code>Compile</code> or <code>CompilePrint</code> expression, whose head names
   *        the symbol any message is reported for
   * @param engine the evaluation engine
   * @return the settings the value asks for; {@link #DEFAULT} if it asks for nothing else. Never
   *         <code>null</code> - a value which cannot be read is reported and ignored.
   */
  public static RuntimeOptions parse(IExpr setting, IAST ast, EvalEngine engine) {
    if (setting == null || !setting.isPresent() || setting == S.Automatic) {
      return DEFAULT;
    }
    RuntimeOptions result = new RuntimeOptions(DEFAULT);
    if (!result.apply(setting, ast, engine)) {
      // nothing of the value was understood - report it as a whole rather than leaving the user
      // with a message about one element of something they wrote as a unit
      printInvalidSetting(setting, ast, engine);
    }
    return result.equals(DEFAULT) ? DEFAULT : result;
  }

  /**
   * Apply one value on top of the settings already held.
   *
   * @return <code>true</code> if the value had a form this understands. A list which contains
   *         unusable elements still returns <code>true</code>: the elements report themselves.
   */
  private boolean apply(IExpr setting, IAST ast, EvalEngine engine) {
    if (setting.isString()) {
      return applyName(setting);
    }
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
        } else if (!element.isString() || !applyName(element)) {
          printInvalidSetting(element, ast, engine);
        }
      }
      return true;
    }
    return false;
  }

  /** Apply <code>"Speed"</code> or <code>"Quality"</code>. */
  private boolean applyName(IExpr name) {
    switch (name.toString()) {
      case SPEED:
        catchMachineIntegerOverflow = false;
        catchMachineOverflow = false;
        return true;
      case QUALITY:
        catchMachineIntegerOverflow = true;
        catchMachineOverflow = true;
        return true;
      default:
        return false;
    }
  }

  /** Apply a single <code>"name" -> value</code> rule. */
  private void applyRule(IAST rule, IAST ast, EvalEngine engine) {
    IExpr name = rule.arg1();
    IExpr value = rule.arg2();
    if (!name.isString()) {
      // Option name `2` not found in defaults for `1`.
      Errors.printMessage(ast.topHead(), "optnf", F.list(S.RuntimeOptions, name), engine);
      return;
    }
    switch (name.toString()) {
      case CATCH_MACHINE_INTEGER_OVERFLOW:
        catchMachineIntegerOverflow =
            readBoolean(name, value, catchMachineIntegerOverflow, ast, engine);
        return;
      case CATCH_MACHINE_OVERFLOW:
        catchMachineOverflow = readBoolean(name, value, catchMachineOverflow, ast, engine);
        return;
      case COMPARE_WITH_TOLERANCE:
        compareWithTolerance = readBoolean(name, value, compareWithTolerance, ast, engine);
        return;
      case EVALUATE_SYMBOLICALLY:
        evaluateSymbolically = readBoolean(name, value, evaluateSymbolically, ast, engine);
        return;
      case WARNING_MESSAGES:
        warningMessages = readBoolean(name, value, warningMessages, ast, engine);
        return;
      case RUNTIME_ERROR_HANDLER:
        // any expression goes: it is applied to the call which failed, and Evaluate - the default
        // - is what reproduces an ordinary evaluation of the result
        runtimeErrorHandler = value;
        return;
      default:
        // Option name `2` not found in defaults for `1`.
        Errors.printMessage(ast.topHead(), "optnf", F.list(S.RuntimeOptions, name), engine);
        return;
    }
  }

  /**
   * The boolean <code>value</code> written for setting <code>name</code>, or
   * <code>currentValue</code> unchanged if it is neither <code>True</code> nor <code>False</code>.
   */
  private static boolean readBoolean(IExpr name, IExpr value, boolean currentValue, IAST ast,
      EvalEngine engine) {
    if (value.isTrue()) {
      return true;
    }
    if (value.isFalse()) {
      return false;
    }
    // Value of option `1` -> `2` should be True or False.
    Errors.printMessage(ast.topHead(), "opttf", F.list(name, value), engine);
    return currentValue;
  }

  private static void printInvalidSetting(IExpr setting, IAST ast, EvalEngine engine) {
    // Value of option RuntimeOptions -> `1` should be ...
    Errors.printMessage(ast.topHead(), "cfro", F.list(setting), engine);
  }

  /** Whether an integer result which leaves the machine integer range is detected. */
  public boolean isCatchMachineIntegerOverflow() {
    return catchMachineIntegerOverflow;
  }

  /**
   * Whether a real result which overflows is detected as it is computed.
   *
   * <p>
   * Nothing reads this yet - see the note on this class.
   */
  public boolean isCatchMachineOverflow() {
    return catchMachineOverflow;
  }

  /**
   * Whether comparisons are carried out the way <code>SameQ</code> does them.
   *
   * <p>
   * Nothing reads this yet - see the note on this class.
   */
  public boolean isCompareWithTolerance() {
    return compareWithTolerance;
  }

  /** Whether a call which the compiled code cannot take is evaluated symbolically instead. */
  public boolean isEvaluateSymbolically() {
    return evaluateSymbolically;
  }

  /** Whether the compiled function reports its warnings. */
  public boolean isWarningMessages() {
    return warningMessages;
  }

  /** The expression applied to a call which ends in a fatal runtime error. */
  public IExpr getRuntimeErrorHandler() {
    return runtimeErrorHandler;
  }

  /**
   * These settings as a list of rules, in a fixed order, whatever form they were written in. This
   * is the form the settings are compared and printed in.
   */
  public IAST toExpr() {
    if (expr == null) {
      expr = F.List( //
          F.Rule(F.stringx(CATCH_MACHINE_INTEGER_OVERFLOW),
              F.booleSymbol(catchMachineIntegerOverflow)), //
          F.Rule(F.stringx(CATCH_MACHINE_OVERFLOW), F.booleSymbol(catchMachineOverflow)), //
          F.Rule(F.stringx(COMPARE_WITH_TOLERANCE), F.booleSymbol(compareWithTolerance)), //
          F.Rule(F.stringx(EVALUATE_SYMBOLICALLY), F.booleSymbol(evaluateSymbolically)), //
          F.Rule(F.stringx(RUNTIME_ERROR_HANDLER), runtimeErrorHandler), //
          F.Rule(F.stringx(WARNING_MESSAGES), F.booleSymbol(warningMessages)));
    }
    return expr;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof RuntimeOptions)) {
      return false;
    }
    RuntimeOptions other = (RuntimeOptions) obj;
    return catchMachineIntegerOverflow == other.catchMachineIntegerOverflow //
        && catchMachineOverflow == other.catchMachineOverflow //
        && compareWithTolerance == other.compareWithTolerance //
        && evaluateSymbolically == other.evaluateSymbolically //
        && warningMessages == other.warningMessages //
        && runtimeErrorHandler.equals(other.runtimeErrorHandler);
  }

  @Override
  public int hashCode() {
    int hash = runtimeErrorHandler.hashCode();
    hash = 31 * hash + (catchMachineIntegerOverflow ? 1 : 0);
    hash = 31 * hash + (catchMachineOverflow ? 1 : 0);
    hash = 31 * hash + (compareWithTolerance ? 1 : 0);
    hash = 31 * hash + (evaluateSymbolically ? 1 : 0);
    hash = 31 * hash + (warningMessages ? 1 : 0);
    return hash;
  }

  @Override
  public String toString() {
    return toExpr().toString();
  }
}
