package org.matheclipse.core.interfaces;

import java.util.function.Predicate;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.S;

/**
 * Interface for <b>Built-In Symbols</b>, which are mapped directly to Java implementations.
 * <p>
 * An {@code IBuiltInSymbol} is a specialized {@link ISymbol} used for core system functions (like
 * {@link S#Sin}, {@link S#Plus}, {@link S#List}). Unlike standard user-defined symbols, which rely
 * on pattern-matching rules ({@link org.matheclipse.core.patternmatching.RulesData}), built-in
 * symbols delegate their evaluation to an {@link IFunctionEvaluator} written in Java. This provides
 * significantly higher performance for fundamental operations.
 * </p>
 *
 * <h3>1. Core Architecture</h3>
 * <p>
 * When the {@link EvalEngine} evaluates an AST like {@code Sin[x]}, it checks if the head
 * ({@link S#Sin}) is an {@code IBuiltInSymbol}. If so, it retrieves the associated
 * {@code IFunctionEvaluator} and calls its {@code evaluate()} method directly, bypassing the
 * rule-matching engine.
 * </p>
 *
 * <h3>2. The Evaluator Contract</h3>
 * <p>
 * The attached {@link IFunctionEvaluator} is responsible for:
 * </p>
 * <ul>
 * <li><b>Numeric Evaluation:</b> Computing results when arguments are numbers (e.g.,
 * {@code Sin[0.5]}).</li>
 * <li><b>Symbolic Simplification:</b> Applying algebraic rules (e.g., {@code Sin[Pi] -> 0}).</li>
 * <li><b>Attribute Handling:</b> Many built-in symbols have hardcoded attributes like
 * {@link S#Listable} or {@link S#NumericFunction}.</li>
 * </ul>
 *
 * <h3>3. Usage Examples</h3>
 *
 * <h4>Accessing Built-Ins</h4>
 * 
 * <pre>
 * // Standard symbols in the S class are built-ins
 * IBuiltInSymbol sin = S.Sin;
 *
 * // Check if it has an evaluator
 * if (sin.getEvaluator() != null) {
 *   // It maps to org.matheclipse.core.builtin.function.Sin
 * }
 * </pre>
 *
 * <h4>Accessing Interfaces implemented in Built-Ins</h4>
 * 
 * <pre>
 * // uses IFunctionEvaluator interface internally
 * ICDF evaluator = astFunction.headInstanceOf(ICDF.class);
 * if (evaluator != null) {
 *   return evaluator.inverseCDF(astFunction, arg, engine);
 * }
 * </pre>
 * 
 * <h4>Defining a Predicate Symbol</h4>
 * 
 * <pre>
 * // You can quickly define a symbol that acts as a boolean check
 * IBuiltInSymbol myCheck = (IBuiltInSymbol) F.Dummy("MyCheck");
 *
 * // Define behavior: MyCheck[x] is True if x is an Integer, else False
 * myCheck.setPredicateQ(expr -> expr.isInteger());
 * </pre>
 *
 * @see org.matheclipse.core.interfaces.ISymbol
 * @see org.matheclipse.core.eval.interfaces.IFunctionEvaluator
 * @see org.matheclipse.core.expression.S
 */
public interface IBuiltInSymbol extends ISymbol {

  /**
   * Get the current evaluator for this symbol
   *
   * @return the evaluator which is associated to this symbol or <code>null</code> if no evaluator
   *         is associated
   */
  public IFunctionEvaluator getEvaluator();

  public default IExpr evaluate(IAST ast, EvalEngine engine) {
    return this.getEvaluator().evaluate(ast, engine);
  }

  /** Set the current evaluator which is associated to this symbol */
  public void setEvaluator(IFunctionEvaluator module);

  /** Define a predicate as the current evaluator which is associated to this symbol */
  public void setPredicateQ(Predicate<IExpr> predicate);

  public default ISymbol mapToGlobal(EvalEngine engine) {
    return null;
  }
}
