package org.matheclipse.core.eval.interfaces;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInexactNumber;

/** Common interface for built-in Symja functions. */
public interface IFunctionEvaluator extends IEvaluator {

  /**
   * The function is allowed to have 0 arguments and the head must be the built-in symbol name of
   * the function.
   */
  int[] ARGS_0_0 = new int[] {0, 0};

  /**
   * The function is allowed to have 0 or 1 argument and the head must be the built-in symbol name
   * of the function.
   */
  int[] ARGS_0_1 = new int[] {0, 1};

  /**
   * The function is allowed to have 0 or 1 argument and the head must be the built-in symbol name
   * of the function or can be an operator form.
   */
  int[] ARGS_0_1_0 = new int[] {0, 1, 0};

  /**
   * The function is allowed to have 0 or 2 arguments and the head must be the built-in symbol name
   * of the function.
   */
  int[] ARGS_0_2 = new int[] {0, 2};

  /**
   * The function is allowed to have 0 or 3 arguments and the head must be the built-in symbol name
   * of the function.
   */
  int[] ARGS_0_3 = new int[] {0, 3};

  /**
   * The function is allowed to have 0 up to 2 arguments and the head must be the built-in symbol
   * name of the function or can be an operator form.
   */
  int[] ARGS_0_2_0 = new int[] {0, 2, 0};

  /**
   * The function is allowed to have exactly 1 argument and the head must be the built-in symbol
   * name of the function.
   */
  int[] ARGS_1_1 = new int[] {1, 1};

  /**
   * The function is allowed to have 1 or 2 arguments and the head must be the built-in symbol name
   * of the function.
   */
  int[] ARGS_1_2 = new int[] {1, 2};
  /**
   * The function is allowed to have 1 or 2 arguments and the head must be the built-in symbol name
   * of the function or can be an operator form.
   */
  int[] ARGS_1_2_0 = new int[] {1, 2, 0};

  /**
   * The function is allowed to have 1 or 2 arguments and the head must be the built-in symbol name
   * of the function or can call {@link F#operatorForm1Append(IAST)}.
   */
  int[] ARGS_1_2_1 = new int[] {1, 2, 1};

  /**
   * The function is allowed to have 1 or 2 arguments and the head must be the built-in symbol name
   * of the function or can call {@link F#operatorForm2Prepend(IAST, int[], EvalEngine)}.
   */
  int[] ARGS_1_2_2 = new int[] {1, 2, 2};

  int[] ARGS_1_5 = new int[] {1, 5};

  /**
   * The function is allowed to have 1 or 5 arguments and the head must be the built-in symbol name
   * of the function or can be an {@link F#operatorForm1Append(IAST)}.
   */
  int[] ARGS_1_5_1 = new int[] {1, 5, 1};

  /**
   * The function is allowed to have exactly 2 arguments and the head must be the built-in symbol
   * name of the function.
   */
  int[] ARGS_2_2 = new int[] {2, 2};

  /**
   * The function is allowed to have 2 arguments and the head must be the built-in symbol name of
   * the function or can call {@link F#operatorForm1Append(IAST)}.
   */
  int[] ARGS_2_2_1 = new int[] {2, 2, 1};

  /**
   * The function is allowed to have 1 or 3 arguments and the head must be the built-in symbol name
   * of the function.
   */
  int[] ARGS_1_3 = new int[] {1, 3};

  /**
   * The function is allowed to have 1 or 3 arguments and the head must be the built-in symbol name
   * of the function or can be an operator form.
   */
  int[] ARGS_1_3_0 = new int[] {1, 3, 0};

  /**
   * The function is allowed to have 1 or 3 arguments and the head must be the built-in symbol name
   * of the function or can call {@link F#operatorForm1Append(IAST)}.
   */
  int[] ARGS_1_3_1 = new int[] {1, 3, 1};

  /**
   * The function is allowed to have 1 or 3 arguments and the head must be the built-in symbol name
   * of the function or can call {@link F#operatorForm2Prepend(IAST, int[], EvalEngine)}.
   */
  int[] ARGS_1_3_2 = new int[] {1, 3, 2};

  /**
   * The function is allowed to have 1 or 4 arguments and the head must be the built-in symbol name
   * of the function.
   */
  int[] ARGS_1_4 = new int[] {1, 4};

  /**
   * The function is allowed to have 1 or 4 arguments and the head must be the built-in symbol name
   * of the function or can be an operator form.
   */
  int[] ARGS_1_4_0 = new int[] {1, 4, 0};

  /**
   * The function is allowed to have 1 or 4 arguments and the head must be the built-in symbol name
   * of the function or can be an {@link F#operatorForm1Append(IAST)}.
   */
  int[] ARGS_1_4_1 = new int[] {1, 4, 1};

  /**
   * The function is allowed to have 1 or 4 arguments and the head must be the built-in symbol name
   * of the function or can be an {@link F#operatorForm2Prepend(IAST, int[], EvalEngine)}.
   */
  int[] ARGS_1_4_2 = new int[] {1, 4, 2};

  /**
   * The function is allowed to have 2 or 3 arguments and the head must be the built-in symbol name
   * of the function.
   */
  int[] ARGS_2_3 = new int[] {2, 3};

  /**
   * The function is allowed to have 2 or 3 arguments and the head must be the built-in symbol name
   * of the function or can be an operator form.
   */
  int[] ARGS_2_3_0 = new int[] {2, 3, 0};

  /**
   * The function is allowed to have 2 or 3 arguments and the head must be the built-in symbol name
   * of the function or can call {@link F#operatorForm1Append(IAST)}.
   */
  int[] ARGS_2_3_1 = new int[] {2, 3, 1};

  /**
   * The function is allowed to have 2 or 3 arguments and the head must be the built-in symbol name
   * of the function or can call {@link F#operatorForm2Prepend(IAST, int[], EvalEngine)}.
   */
  int[] ARGS_2_3_2 = new int[] {2, 3, 2};

  /**
   * The function is allowed to have 2 or 4 arguments and the head must be the built-in symbol name
   * of the function.
   */
  int[] ARGS_2_4 = new int[] {2, 4};

  /**
   * The function is allowed to have 2 or 4 arguments and the head must be the built-in symbol name
   * of the function or can call {@link F#operatorForm1Append(IAST)}
   */
  int[] ARGS_2_4_1 = new int[] {2, 4, 1};

  /**
   * The function is allowed to have 2 or 4 arguments and the head must be the built-in symbol name
   * of the function or can be an {@link F#operatorForm2Prepend(IAST, int[], EvalEngine)}.
   */
  int[] ARGS_2_4_2 = new int[] {2, 4, 2};

  /**
   * The function is allowed to have 3 arguments and the head must be the built-in symbol name of
   * the function.
   */
  int[] ARGS_3_3 = new int[] {3, 3};

  /**
   * The function is allowed to have 3 or 4 arguments and the head must be the built-in symbol name
   * of the function.
   */
  int[] ARGS_3_4 = new int[] {3, 4};

  /**
   * The function is allowed to have 3 or 5 arguments and the head must be the built-in symbol name
   * of the function.
   */
  int[] ARGS_3_5 = new int[] {3, 5};

  /**
   * The function is allowed to have 3 or 6 arguments and the head must be the built-in symbol name
   * of the function.
   */
  int[] ARGS_3_6 = new int[] {3, 6};

  /**
   * The function is allowed to have 4 arguments and the head must be the built-in symbol name of
   * the function.
   */
  int[] ARGS_4_4 = new int[] {4, 4};

  /**
   * The function is allowed to have 5 arguments and the head must be the built-in symbol name of
   * the function.
   */
  int[] ARGS_5_5 = new int[] {5, 5};

  /**
   * The function is allowed to have 6 arguments and the head must be the built-in symbol name of
   * the function.
   */
  int[] ARGS_6_6 = new int[] {6, 6};

  /**
   * The function is allowed to have between <code>0</code> and {@link Integer#MAX_VALUE} arguments
   * and the head must be the built-in symbol name of the function.
   */
  int[] ARGS_0_INFINITY = new int[] {0, Integer.MAX_VALUE};

  /**
   * The function is allowed to have between <code>1</code> and {@link Integer#MAX_VALUE} arguments
   * and the head must be the built-in symbol name of the function.
   */
  int[] ARGS_1_INFINITY = new int[] {1, Integer.MAX_VALUE};

  /**
   * The function is allowed to have 0 or n arguments and the head must be the built-in symbol name
   * of the function or can be an operator form.
   */
  int[] ARGS_1_INFINITY_0 = new int[] {1, Integer.MAX_VALUE, 0};

  /**
   * The function is allowed to have 1 or n arguments and the head must be the built-in symbol name
   * of the function or can call {@link F#operatorForm1Append(IAST)}
   */
  int[] ARGS_1_INFINITY_1 = new int[] {1, Integer.MAX_VALUE, 1};

  /**
   * The function is allowed to have between <code>2</code> and {@link Integer#MAX_VALUE} arguments
   * and the head must be the built-in symbol name of the function.
   */
  int[] ARGS_2_INFINITY = new int[] {2, Integer.MAX_VALUE};

  /**
   * The function is allowed to have between <code>3</code> and {@link Integer#MAX_VALUE} arguments
   * and the head must be the built-in symbol name of the function.
   */
  int[] ARGS_3_INFINITY = new int[] {3, Integer.MAX_VALUE};

  /**
   * <b>Symbolic evaluation</b> of a function. The method {@link IAST#head()} contains the
   * <i>head</i> (i.e. the function symbol) of this abstract syntax tree (AST).
   *
   * <p>
   * From <code>ast.get(1)</code> to <code>ast.get(n)</code> the <code>ast</code> contains the first
   * to n-th argument of the function (alternatively you get the first to fifth argument with the
   * methods {@link IAST#arg1()}, {@link IAST#arg2()}, {@link IAST#arg3()} ... {@link IAST#arg5()}.
   *
   * <p>
   * <b>Example:</b> the expression <code>Binomial(n,m)</code> is represented as AST with <code>
   * ast.head() &lt;=&gt; F.Binomial</code>, <code>ast.arg1() &lt;=&gt; n</code> and
   * <code>ast.arg2() &lt;=&gt; m</code>
   *
   * <p>
   * If necessary use the methods from the <code>Validate</code> class to check the number or types
   * of arguments in the evaluate method.
   *
   * <p>
   * <b>Note:</b> if the symbolic evaluation isn't possible or no result is found the evaluate
   * method returns with a {@link F#NIL} value without throwing an exception!
   *
   * @param ast the abstract syntax tree (AST) which should be evaluated
   * @param engine the users current evaluation engine
   * @return the evaluated object or <code>F#NIL</code>, if evaluation isn't possible
   * @see org.matheclipse.core.eval.exception.Validate
   */
  public IExpr evaluate(IAST ast, final EvalEngine engine);

  /**
   * <b>Numeric evaluation</b> of a function. The method <code>ast.get(0)</code> (or alternatively
   * <code>ast.head()</code>) contains the <i>head</i> (i.e. the function symbol) of this abstract
   * syntax tree (AST).
   *
   * <p>
   * From <code>ast.get(1)</code> to <code>ast.get(n)</code> the <code>ast</code> contains the first
   * to n-th argument of the function (alternatively you get the first to fifth argument with the
   * methods <code>arg1()</code>, <code>arg2()</code>,... <code>arg5()</code>).
   *
   * <p>
   * <b>Example:</b> the expression <code>Binomial(n,m)</code> is represented as AST with <code>
   * ast.head() &lt;=&gt; F.Binomial</code>, <code>ast.arg1() &lt;=&gt; n</code> and
   * <code>ast.arg2() &lt;=&gt; m</code>
   *
   * <p>
   * If necessary use the methods from the <code>Validate</code> class to check the number or types
   * of arguments in the evaluate method.
   *
   * <p>
   * <b>Note:</b> if the symbolic evaluation isn't possible or no result is found the evaluate
   * method returns with a <code>F#NIL</code> value without throwing an exception!
   *
   * @param ast the abstract syntax tree (AST) which should be evaluated
   * @param engine the users current evaluation engine
   * @return the evaluated object or <code>F#NIL</code>, if evaluation isn't possible
   * @see org.matheclipse.core.eval.exception.Validate
   * @see IExpr#head()
   * @see IAST#arg1()
   * @see IAST#arg2()
   * @see IAST#arg3()
   */
  public IExpr numericEval(IAST ast, final EvalEngine engine);

  /**
   * <b>Numeric evaluation</b> of a function with only {@link IInexactNumber} arguments. The method
   * <code>ast.get(0)</code> (or alternatively <code>ast.head()</code>) contains the <i>head</i>
   * (i.e. the function symbol) of this abstract syntax tree (AST).
   *
   * <p>
   * From <code>ast.get(1)</code> to <code>ast.get(n)</code> the <code>ast</code> contains the first
   * to n-th argument of the function (alternatively you get the first to fifth argument with the
   * methods <code>arg1()</code>, <code>arg2()</code>,... <code>arg5()</code>).
   *
   * <p>
   * <b>Example:</b> the expression <code>Binomial(n,m)</code> is represented as AST with <code>
   * ast.head() &lt;=&gt; F.Binomial</code>, <code>ast.arg1() &lt;=&gt; n</code> and
   * <code>ast.arg2() &lt;=&gt; m</code>
   *
   * @param ast the abstract syntax tree (AST) which contains only arguments of type
   *        {@link IInexactNumber}
   * @param engine the users current evaluation engine
   * @return the evaluated object or <code>F#NIL</code>, if evaluation isn't possible
   */
  default IExpr numericFunction(IAST ast, final EvalEngine engine) {
    return F.NIL;
  }

  /**
   * At index 0 return the &quot;from&quot;, at index 1 return the &quot;to&quot; number of
   * arguments, which are expected by this function. If the returned <code>int</code> array has
   * length 3, the function allows headers unequal to the built-in function name (i.e. the function
   * can be in &quot;operator form&quot;).
   *
   * @param ast the abstract syntax tree (AST) those arguments should be checked
   * @return <code>null</code> if no range for the number of arguments is specified.
   */
  default int[] expectedArgSize(IAST ast) {
    return null;
  }

  /**
   * Get the options in the order, in which they are predefined in the
   * {@link #setUp(org.matheclipse.core.interfaces.ISymbol)} method.
   * 
   * @return <code>null</code> if no option was defined for the built-in symbol
   */
  default IBuiltInSymbol[] getOptionSymbols() {
    return null;
  }

  /**
   * Print "argrx" message: `1` called with `2` arguments; `3` arguments are expected.
   *
   * @param ast
   * @param expected
   * @param engine
   * @return always {@link F#NIL}
   */
  default IExpr print(IAST ast, int expected, EvalEngine engine) {
    // `1` called with `2` arguments; `3` arguments are expected.
    return Errors.printMessage(ast.topHead(), "argrx",
        F.List(ast, F.ZZ(ast.argSize()), F.ZZ(expected)), engine);
  }

  /** {@inheritDoc} */
  @Override
  default int status() {
    return ImplementationStatus.FULL_SUPPORT;
  }
}
