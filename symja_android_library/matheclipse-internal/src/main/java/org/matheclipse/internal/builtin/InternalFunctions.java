package org.matheclipse.internal.builtin;

import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.ContextPath;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IBuiltInSymbol;

/**
 * The registry of the <code>Internal`</code> functions. Each function is a
 * {@link org.matheclipse.core.expression.ContextBuiltInSymbol} which the parser finds by its
 * context-qualified name once {@link #initialize()} has registered it.
 */
public class InternalFunctions {

  /** The context <code>Internal`</code> all functions of this module live in. */
  public static final Context INTERNAL = new Context("Internal`");

  private static final class Initializer {
    private static void init() {
      // System` built-ins which are also known under an Internal` name
      ContextPath.registerSystemAlias("Internal`RealValuedNumberQ", S.RealValuedNumberQ);
      ContextPath.registerSystemAlias("Internal`RiccatiSolve", S.RiccatiSolve);
      BagFunctions.init();
      ScopingFunctions.init();
      PredicateFunctions.init();
      StructureFunctions.init();
      NumberFunctions.init();
    }
  }

  /**
   * Create the function <code>Internal`name</code> and register it with the parser.
   *
   * @param name the name without the context
   * @param attributes the attributes, for example
   *        {@link org.matheclipse.core.interfaces.ISymbol#HOLDALL}
   * @param evaluator the evaluator
   * @return the symbol of the function
   */
  static IBuiltInSymbol define(String name, int attributes, IFunctionEvaluator evaluator) {
    IBuiltInSymbol symbol = F.contextFunction(INTERNAL, name, attributes, evaluator);
    ContextPath.registerSystemAlias(INTERNAL.getContextName() + name, symbol);
    return symbol;
  }

  /** Register the <code>Internal`</code> functions; the first call does the work. */
  public static void initialize() {
    InitializerHolder.touch();
  }

  /** Runs {@link Initializer#init()} exactly once, when this class is first used. */
  private static final class InitializerHolder {
    static {
      Initializer.init();
    }

    static void touch() {
      // loading the class runs the initializer
    }
  }

  private InternalFunctions() {}
}
