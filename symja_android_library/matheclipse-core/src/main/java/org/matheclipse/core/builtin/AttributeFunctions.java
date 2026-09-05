package org.matheclipse.core.builtin;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.FailedException;
import org.matheclipse.core.eval.exception.RuleCreationError;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.ISetEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class AttributeFunctions {

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.Attributes.setEvaluator(new Attributes());
      S.ClearAttributes.setEvaluator(new ClearAttributes());
      S.SetAttributes.setEvaluator(new SetAttributes());
      S.Protect.setEvaluator(new Protect());
      S.Unprotect.setEvaluator(new Unprotect());
    }
  }

  /**
   *
   *
   * <pre>
   * Attributes(symbol)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the list of attributes which are assigned to <code>symbol</code>
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Attributes(Plus)
   * {Flat,Listable,OneIdentity,Orderless,NumericFunction}
   * </pre>
   */
  private static final class Attributes extends AbstractFunctionEvaluator implements ISetEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        IExpr expr = ast.arg1();
        IExpr x = Validate.checkIdentifierHoldPattern(expr, ast, engine);
        if (x.isNIL()) {
          return F.NIL;
        }
        ISymbol symbol = (ISymbol) x;
        return ISymbol.attributesList(symbol);
      }

      return F.NIL;
    }

    @Override
    public IExpr evaluateSet(final IExpr leftHandSide, IExpr rightHandSide,
        IBuiltInSymbol builtinSymbol, EvalEngine engine) {
      if (leftHandSide.isAST(S.Attributes, 2)) {
        if (!leftHandSide.first().isSymbol()) {
          Errors.printMessage(builtinSymbol, "setps", F.list(leftHandSide.first()), engine);
          return rightHandSide;
        }
        if (rightHandSide.isEmptyList() && leftHandSide.first().isSymbol()) {
          ISymbol sym = (ISymbol) leftHandSide.first();
          if (!engine.isPackageMode()) {
            if (Config.SERVER_MODE && (sym.toString().charAt(0) != '$')) {
              throw new RuleCreationError(sym);
            }
          }
          if (sym.hasProtectedAttribute()) {
            // Tag `1` in `2` is Protected.
            Errors.printMessage(S.ClearAttributes, "write", F.list(sym, leftHandSide),
                EvalEngine.get());
            throw new FailedException();
          }
          sym.clearAttributes(ISymbol.ALL_ATTRIBUTES);
        }
        IExpr temp = engine.evaluate(F.SetAttributes(leftHandSide.first(), rightHandSide));
        if (temp == S.Null) {
          return rightHandSide;
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.HOLDALL, Attribute.LISTABLE);
    }
  }

  /**
   *
   *
   * <pre>
   * ClearAttributes(symbol, attrib)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * removes <code>attrib</code> from <code>symbol</code>'s attributes.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; SetAttributes(f, Flat)
   * &gt;&gt; Attributes(f)
   * {Flat}
   *
   * &gt;&gt; ClearAttributes(f, Flat)
   * &gt;&gt; Attributes(f)
   * {}
   * </pre>
   *
   * <p>
   * Attributes that are not even set are simply ignored:
   *
   * <pre>
   * &gt;&gt; ClearAttributes({f}, {Flat})
   * &gt;&gt; Attributes(f)
   * {}
   * </pre>
   */
  private static final class ClearAttributes extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IAST list = ast.arg1().makeList();
      IExpr arg2 = engine.evaluate(ast.arg2());
      for (int i = 1; i < list.size(); i++) {
        IExpr temp = clearAttributes(list.get(i), arg2, ast, engine);
        if (temp.isNIL()) {
          return F.NIL;
        }
      }
      return S.Null;

      // if (ast.arg1().isSymbol()) {
      // IExpr arg2 = engine.evaluate(ast.arg2());
      // final ISymbol sym = ((ISymbol) ast.arg1());
      // return clearAttributes(sym, arg2, ast, engine);
      // }
      // return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    /**
     * Remove the attribute from the symbols existing attributes bit-set.
     *
     * @param expr
     * @param attributes
     * @param engine
     * @return {@link F#NIL} if <code>expr</code> is not a symbol
     */
    private IExpr clearAttributes(final IExpr expr, IExpr attributes, IAST ast, EvalEngine engine) {
      IExpr x = Validate.checkIdentifierHoldPattern(expr, ast, engine);
      if (x.isNIL()) {
        return F.NIL;
      }
      ISymbol sym = (ISymbol) x;
      if (!engine.isPackageMode()) {
        if (Config.SERVER_MODE && (sym.toString().charAt(0) != '$')) {
          throw new RuleCreationError(sym);
        }
      }
      if (sym.hasProtectedAttribute()) {
        // Tag `1` in `2` is Protected.
        Errors.printMessage(S.ClearAttributes, "write", F.list(sym, expr), EvalEngine.get());
        throw new FailedException();
      }
      if (attributes.isSymbol()) {
        ISymbol attribute = (ISymbol) attributes;
        if (!clearAttributes(sym, attribute)) {
          // `1` is not a known attribute.
          return Errors.printMessage(S.ClearAttributes, "attnf", F.List(attribute), engine);
        }
        return S.Null;
      } else {
        if (attributes.isList()) {
          final IAST lst = (IAST) attributes;
          // lst.forEach(x -> clearAttributes(sym, (ISymbol) x));
          for (int i = 1; i < lst.size(); i++) {
            if (!lst.get(i).isSymbol()) {
              continue;
            }
            ISymbol attribute = (ISymbol) lst.get(i);
            if (!clearAttributes(sym, attribute)) {
              // `1` is not a known attribute.
              return Errors.printMessage(S.ClearAttributes, "attnf", F.List(attribute), engine);
            }
          }
          return S.Null;
        }
      }
      return S.Null;
    }

    /**
     * Remove one single attribute from the symbols existing attributes bit-set.
     *
     * @param sym
     * @param attribute
     */
    private boolean clearAttributes(final ISymbol sym, ISymbol attribute) {
      Attribute known = Attribute.of(attribute);
      if (known == null || !known.isUserClearable()) {
        // not an attribute at all, or one which must not be removed - the caller reports "attnf"
        return false;
      }
      sym.clearAttributes(known.mask());
      return true;
    }
  }

  private static final class Protect extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IASTMutable mutable = ast.copyAST();
      for (int i = 1; i < ast.size(); i++) {
        IExpr expr = ast.get(i);
        IExpr x = Validate.checkIdentifierHoldPattern(expr, ast, engine);
        if (x.isNIL()) {
          return F.NIL;
        }
        mutable.set(i, x);
      }
      final IASTAppendable result = F.ListAlloc(mutable.size());
      mutable.forEach(x -> {
        if (x.isSymbol()) {
          appendProtected(result, (ISymbol) x);
        }
      });
      return result;
    }

    private static void appendProtected(final IASTAppendable result, ISymbol x) {
      ISymbol symbol = x;
      if (!symbol.hasProtectedAttribute()) {
        symbol.addAttributes(Attribute.PROTECTED);
        result.append(x);
      }
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.HOLDALL);
    }
  }

  private static final class Unprotect extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (Config.UNPROTECT_ALLOWED) {
        IASTMutable mutable = ast.copyAST();
        for (int i = 1; i < ast.size(); i++) {
          IExpr expr = ast.get(i);
          IExpr x = Validate.checkIdentifierHoldPattern(expr, ast, engine);
          if (x.isNIL()) {
            return F.NIL;
          }
          mutable.set(i, x);
        }
        final IASTAppendable result = F.ListAlloc(mutable.size());
        mutable.forEach(x -> appendUnprotected(result, x));
        return result;
      }
      return Errors.printMessage(ast.topHead(), "error",
          F.List("Unprotect not allowed. Set Config.UNPROTECT_ALLOWED on Java level if necessary."),
          engine);
    }

    private static void appendUnprotected(final IASTAppendable result, IExpr x) {
      ISymbol symbol = (ISymbol) x;
      if (symbol.hasProtectedAttribute()) {
        symbol.clearAttributes(Attribute.PROTECTED);
        result.append(x);
      }
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.HOLDALL);
    }
  }

  /**
   *
   *
   * <pre>
   * SetAttributes(symbol, attrib)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * adds <code>attrib</code> to <code>symbol</code>'s attributes.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; SetAttributes(f, Flat)
   * &gt;&gt; Attributes(f)
   * {Flat}
   * </pre>
   *
   * <p>
   * Multiple attributes can be set at the same time using lists:<br>
   *
   * <pre>
   * &gt;&gt; SetAttributes({f, g}, {Flat, Orderless})
   * &gt;&gt; Attributes(g)
   * {Flat, Orderless}
   * </pre>
   */
  private static final class SetAttributes extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IAST list = ast.arg1().makeList();
      return setSymbolsAttributes(list, ast.arg2(), ast, engine);

      // IExpr arg1 = ast.arg1();
      // IExpr arg2 = engine.evaluate(ast.arg2());
      // final ISymbol sym = ((ISymbol) ast.arg1());
      // return addAttributes(arg1, arg2, ast, engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    private static IExpr setSymbolsAttributes(IAST listOfSymbols, IExpr attributes, IAST ast,
        EvalEngine engine) {
      attributes = engine.evaluate(attributes);
      for (int i = 1; i < listOfSymbols.size(); i++) {
        final IExpr arg = listOfSymbols.get(i);
        if (arg.isSymbol()) {
          if (((ISymbol) arg).hasProtectedAttribute()) {
            Errors.printMessage(S.ClearAttributes, "write", F.list(arg), EvalEngine.get());
            throw new FailedException();
          }
          if (addAttributes(arg, attributes, ast, engine).isNIL()) {
            return F.NIL;
          }
        } else {
          // Argument `1` at position `2` is expected to be a symbol.
          return Errors.printMessage(S.SetAttributes, "sym", F.List(arg, F.ZZ(i)), engine);
        }
      }
      return S.Null;
    }

    /**
     * Add the attribute to the symbols existing attributes bit-set.
     *
     * @param expr
     * @param attributes
     * @param engine
     * @return {@link F#NIL} if <code>expr</code> is not a symbol
     */
    private static IExpr addAttributes(final IExpr expr, IExpr attributes, IAST ast,
        EvalEngine engine) {
      IExpr x = Validate.checkIdentifierHoldPattern(expr, ast, engine);
      if (x.isNIL()) {
        return F.NIL;
      }
      ISymbol sym = (ISymbol) x;
      if (!engine.isPackageMode()) {
        if (Config.SERVER_MODE && (sym.toString().charAt(0) != '$')) {
          throw new RuleCreationError(expr);
        }
      }
      if (attributes.isSymbol()) {
        ISymbol attribute = (ISymbol) attributes;
        if (!addAttributes(sym, attribute)) {
          // `1` is not a known attribute.
          return Errors.printMessage(S.SetAttributes, "attnf", F.List(attribute), engine);
        }
      } else if (attributes.isList()) {
        final IAST lst = (IAST) attributes;
        // lst.forEach(x -> addAttributes(sym, (ISymbol) x));
        for (int i = 1; i < lst.size(); i++) {
          final ISymbol attribute = (ISymbol) lst.get(i);
          if (!addAttributes(sym, attribute)) {
            // `1` is not a known attribute.
            Errors.printMessage(S.SetAttributes, "attnf", F.List(attribute), engine);
          }
        }
      }
      return S.Null;
    }

    /**
     * Add one single attribute to the symbols existing attributes bit-set.
     *
     * @param sym
     * @param attribute
     */
    private static boolean addAttributes(final ISymbol sym, ISymbol attribute) {
      if (sym.hasProtectedAttribute()) {
        Errors.printMessage(S.SetAttributes, "write", F.list(sym), EvalEngine.get());
        throw new FailedException();
      }
      Attribute known = Attribute.of(attribute);
      if (known == null) {
        // the caller reports "attnf"
        return false;
      }
      sym.addAttributes(known.mask());
      return true;
    }
  }

  public static int getSymbolsAsAttributes(IAST listOfSymbols, EvalEngine engine) {
    int attributes = ISymbol.NOATTRIBUTE;
    for (int i = 1; i < listOfSymbols.size(); i++) {
      Attribute known = Attribute.of(listOfSymbols.get(i));
      if (known != null) {
        // an argument which is not an attribute is silently ignored here - both callers
        // (Function's 3-argument form and Compile's RuntimeAttributes) pass no error context
        attributes = known.setIn(attributes);
      }
    }
    return attributes;
  }

  /**
   * Get the attributes of this <code>expr</code> as symbolic constants in a list.
   *
   * @param expr
   * @param ast
   * @param engine
   * @return {@link F#NIL} if <code>expr</code> is not a symbol
   */
  public static IAST attributesList(final IExpr expr, IAST ast, EvalEngine engine) {
    IExpr x = Validate.checkIdentifierHoldPattern(expr, ast, engine);
    if (x.isNIL()) {
      return F.NIL;
    }
    ISymbol symbol = (ISymbol) x;
    return ISymbol.attributesList(symbol);
  }

  public static void initialize() {
    Initializer.init();
  }

  private AttributeFunctions() {}
}
