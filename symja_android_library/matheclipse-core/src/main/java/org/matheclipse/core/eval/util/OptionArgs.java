package org.matheclipse.core.eval.util;

import static org.matheclipse.core.expression.F.Options;
import static org.matheclipse.core.expression.F.ReplaceAll;

import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Managing <i>Options</i> used in evaluation of function symbols (i.e. <code>Modulus-&gt;n</code>
 * is an option which could be used for an integer <code>n</code> in a function like <code>
 * Factor(polynomial, Modulus-&gt;2)</code>.
 */
public class OptionArgs {
  private IAST fDefaultOptionsList;

  private IASTAppendable fCurrentOptionsList;

  private final EvalEngine fEngine;

  private int fLastPosition = -1;

  private int fInvalidPosition = -1;

  /**
   * Construct special <i>Options</i> used in evaluation of function symbols (i.e. <code>
   * Modulus-&gt;n</code> is an option which could be used for an integer <code>n</code> in a
   * function like <code>Factor(polynomial, Modulus-&gt;2)</code>.
   *
   * @param symbol the options symbol for determining &quot;default option values&quot;
   * @param currentOptionsList the AST where the option could be defined starting at position <code>
   *     startIndex</code>
   * @param startIndex the index from which to look for options defined in <code>currentOptionsList
   *     </code>
   * @param engine the evaluation engine
   */
  public OptionArgs(
      final ISymbol symbol,
      final IAST currentOptionsList,
      final int startIndex,
      final EvalEngine engine) {
    this(symbol, currentOptionsList, startIndex, engine, false);
  }

  /**
   * Construct special <i>Options</i> used in evaluation of function symbols (i.e. <code>
   * Modulus-&gt;n</code> is an option which could be used for an integer <code>n</code> in a
   * function like <code>Factor(polynomial, Modulus-&gt;2)</code>.
   *
   * @param symbol the options symbol for determining &quot;default option values&quot;
   * @param currentOptionsList the AST where the option could be defined starting at position <code>
   *     startIndex</code>
   * @param startIndex the index from which to look for options defined in <code>currentOptionsList
   *     </code>
   * @param engine the evaluation engine
   * @param evaluate do an extra evaluation step for each potential option argument
   */
  public OptionArgs(
      final ISymbol symbol,
      final IAST currentOptionsList,
      final int startIndex,
      final EvalEngine engine,
      boolean evaluate) {
    fEngine = engine;
    // get the List of pre-defined options:
    final IExpr temp = fEngine.evaluate(Options(symbol));
    if ((temp != null) && (temp instanceof IAST) && temp.isList()) {
      fDefaultOptionsList = (IAST) temp;
    } else {
      fDefaultOptionsList = null;
    }
    this.fCurrentOptionsList = null;

    if (currentOptionsList != null && startIndex < currentOptionsList.size()) {
      int size = currentOptionsList.size();
      int allocSize = size;
      for (int i = startIndex; i < size; i++) {
        IExpr arg = currentOptionsList.get(i);
        if (arg.isListOfRules(false)) {
          IAST listOfRules = (IAST) arg;
          allocSize += listOfRules.argSize();
        }
      }
      this.fCurrentOptionsList = F.ListAlloc(F.allocMin16(allocSize));
      for (int i = startIndex; i < size; i++) {
        IExpr arg = currentOptionsList.get(i);
        arg = evaluate ? engine.evaluate(arg) : arg;
        if (arg.isListOfRules(false)) {
          IAST listOfRules = (IAST) arg;
          this.fCurrentOptionsList.appendAll(listOfRules, 1, listOfRules.size());
        } else if (arg.isRule()) {
          this.fCurrentOptionsList.append(arg);
        } else {
          fInvalidPosition = i;
        }
      }
    }
  }

  /**
   * Return <code>true</code> if the &quot;invalid Options position&quot; is &gt; <code>-1</code>
   *
   * @return
   */
  public boolean isInvalidPosition() {
    return fInvalidPosition > -1;
  }

  /**
   * If the &quot;invalid Options position&quot; is &gt; <code>greaterThanPositon</code> return
   * <code>true</code>.
   *
   * @param greaterThanPositon
   * @return
   */
  public boolean isInvalidPosition(int greaterThanPositon) {
    return fInvalidPosition > greaterThanPositon;
  }

  /**
   * Get the value of the &quot;invalid Options position&quot;
   *
   * @return <code>-1</code> if there is no &quot;invalid Options position&quot;
   */
  public int getInvalidPosition() {
    return fInvalidPosition;
  }

  public boolean isInvalidPosition(IAST ast, int greaterThanPositon) {
    int invalidPosition =  getInvalidPosition();
    if (invalidPosition > greaterThanPositon) {
      // Options expected (instead of `1`) beyond position `2` in `3`. An option must be a
      // rule or a list of rules.
      IOFunctions.printMessage(
          ast.topHead(), "nonopt", F.List(ast.get(invalidPosition), F.ZZ(greaterThanPositon), ast), EvalEngine.get());
      return true;
    }
    return false;
  }

  /**
   * Construct special <i>Options</i> used in evaluation of function symbols (i.e. <code>
   * Modulus-&gt;n</code> is an option which could be used for an integer <code>n</code> in a
   * function like <code>Factor(polynomial, Modulus-&gt;2)</code>.
   *
   * @param symbol the options symbol for determining &quot;default option values&quot;
   * @param currentOptionsList the AST where the option could be defined starting at position <code>
   *     startIndex</code>
   * @param startIndex the index from which to look for options defined in <code>currentOptionsList
   *     </code>
   * @param endIndex the index up to which (exclusive) to look for options defined in <code>
   *     currentOptionsList
   *     </code>
   */
  public OptionArgs(
      final ISymbol symbol,
      final IAST currentOptionsList,
      final int startIndex,
      final int endIndex,
      final EvalEngine engine) {
    fEngine = engine;
    // get the List of pre-defined options:
    final IExpr temp = fEngine.evaluate(Options(symbol));
    if ((temp != null) && (temp instanceof IAST) && temp.isList()) {
      fDefaultOptionsList = (IAST) temp;
    } else {
      fDefaultOptionsList = null;
    }
    this.fCurrentOptionsList = null;

    if (currentOptionsList != null && startIndex < currentOptionsList.size()) {
      int size = currentOptionsList.size();
      this.fCurrentOptionsList = F.ListAlloc(size);
      for (int i = endIndex - 1; i >= startIndex; i--) {
        IExpr opt = currentOptionsList.get(i);
        if (opt.isRule()) {
          fLastPosition = i;
          this.fCurrentOptionsList.append(1, opt);
        } else {
          break;
        }
      }
    }
  }

  /**
   * Construct <i>Options</i> used in evaluation of function symbols (i.e. <code>Modulus-&gt;n
   * </code> is an option which could be used for an integer <code>n</code> in a function like
   * <code>Factor(polynomial, Modulus-&gt;2)</code>.
   *
   * @param symbol the options symbol for determining &quot;default option values&quot;
   * @param optionExpr the value which should be defined for the option
   */
  public OptionArgs(final ISymbol symbol, final IExpr optionExpr, final EvalEngine engine) {
    fEngine = engine;
    // get the List of pre-defined options:
    final IExpr temp = fEngine.evaluate(Options(symbol));
    if ((temp != null) && (temp instanceof IAST) && temp.isList()) {
      fDefaultOptionsList = (IAST) temp;
    } else {
      fDefaultOptionsList = null;
    }
    this.fCurrentOptionsList = F.ListAlloc();
    this.fCurrentOptionsList.append(optionExpr);
  }

  /**
   * Get the option from the internal options list and check if it's set to <code>S.True</code>.
   *
   * @param option the option
   * @return <code>true</code> if the option is set to <code>True</code> or <code>false</code>
   *     otherwise.
   */
  public boolean isTrue(final ISymbol option) {
    return getOption(option).isTrue();
  }

  /**
   * Get the option from the internal options list and check if it's set to <code>S.False</code>.
   *
   * @param option the option
   * @return <code>true</code> if the option is set to <code>False</code> or <code>false</code>
   *     otherwise.
   */
  public boolean isFalse(final ISymbol option) {
    return getOption(option).isFalse();
  }

  /**
   * If option 'Automatic' is set, return 'F.NIL'.
   *
   * @param option
   * @return
   */
  public IExpr getOptionAutomatic(final ISymbol option) {
    IExpr temp = getOption(option);
    if (temp == S.Automatic) {
      return F.NIL;
    }
    return temp;
  }

  /**
   * Get the option which ist set in the options argument <code>option -&gt; ...</code>.
   *
   * @param option
   * @return the found option value or <code>F.NIL</code> if the option is not available.
   */
  public IExpr getOption(final ISymbol option) {
    IAST[] rule = new IAST[1];
    if (fCurrentOptionsList != null) {
      try {
        if (fCurrentOptionsList.exists(
            x -> {
              if (x.isAST()) {
                IAST temp = (IAST) x;
                if (temp.isRuleAST() && temp.arg1().equals(option)) {
                  rule[0] = temp;
                  return true;
                }
              }
              return false;
            })) {
          return rule[0].arg2();
        }
      } catch (Exception e) {

      }
    }
    if (fDefaultOptionsList != null) {
      try {
        if (fDefaultOptionsList.exists(
            x -> {
              if (x.isAST()) {
                IAST temp = (IAST) x;
                if (temp.isRuleAST() && temp.arg1().equals(option)) {
                  rule[0] = temp;
                  return true;
                }
              }
              return false;
            },
            1)) {
          return rule[0].arg2();
        }
      } catch (Exception e) {

      }
    }
    return F.NIL;
  }

  /**
   * Get the last position which is not an option rule.
   *
   * @return <code>-1</code> if no options is found
   */
  public int getLastPosition() {
    return fLastPosition;
  }

  public IAST replaceAll(final IAST options) {
    if (fCurrentOptionsList != null) {
      return (IAST) fEngine.evaluate(ReplaceAll(options, fCurrentOptionsList));
    }
    if (fDefaultOptionsList != null) {
      return (IAST) fEngine.evaluate(ReplaceAll(options, fDefaultOptionsList));
    }
    return options;
  }

  /**
   * Print message &quot;nonopt&quot; and return {@link F#NIL}.
   *
   * <p><code>
   * Options expected (instead of `1`) beyond position `2` in `3`. An option must be a rule or a list of rules.
   * </code>
   *
   * @param ast
   * @param optionPosition
   * @param engine
   * @return
   */
  public IAST printNonopt(IAST ast, int optionPosition, EvalEngine engine) {
    // Options expected (instead of `1`) beyond position `2` in `3`. An option must be a rule or
    // a list of rules.
    return IOFunctions.printMessage(
        ast.topHead(),
        "nonopt",
        F.List(ast.get(fInvalidPosition), F.ZZ(optionPosition), ast),
        engine);
  }
}
