package org.matheclipse.core.builtin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.FailedException;
import org.matheclipse.core.eval.exception.RuleCreationError;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.ISetEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.Comparators;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class AttributeFunctions {
  private static final Logger LOGGER = LogManager.getLogger();

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
        IExpr arg1 = ast.arg1();
        // if (arg1.isList()) {
        // IAST list = (IAST) arg1;
        // if (list.exists(x -> !x.isSymbol())) {
        // return F.NIL;
        // }
        // final IASTAppendable result = F.ListAlloc(list.size());
        // for (int i = 1; i < list.size(); i++) {
        // IExpr temp = attributesList(list.get(i), ast, engine);
        // if (temp.isNIL()) {
        // return F.NIL;
        // }
        // result.append(temp);
        // }
        // return result;
        // }
        return attributesList(arg1, ast, engine);
      }

      return F.NIL;
    }

    @Override
    public IExpr evaluateSet(final IExpr leftHandSide, IExpr rightHandSide,
        IBuiltInSymbol builtinSymbol, EvalEngine engine) {
      if (leftHandSide.isAST(S.Attributes, 2)) {
        if (!leftHandSide.first().isSymbol()) {
          IOFunctions.printMessage(builtinSymbol, "setps", F.list(leftHandSide.first()), engine);
          return rightHandSide;
        }
        IExpr temp = engine.evaluate(F.SetAttributes(leftHandSide.first(), rightHandSide));
        if (temp.equals(S.Null)) {
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
      newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.LISTABLE);
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
      IAST list = ast.arg1().orNewList();
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
      if (sym.isProtected()) {
        IOFunctions.printMessage(S.ClearAttributes, "write", F.list(sym), EvalEngine.get());
        throw new FailedException();
      }
      if (attributes.isSymbol()) {
        ISymbol attribute = (ISymbol) attributes;
        if (!clearAttributes(sym, attribute)) {
          // `1` is not a known attribute.
          return IOFunctions.printMessage(S.ClearAttributes, "attnf", F.List(attribute), engine);
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
              return IOFunctions.printMessage(S.ClearAttributes, "attnf", F.List(attribute),
                  engine);
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
      int functionID = attribute.ordinal();
      if (functionID > ID.UNKNOWN) {
        switch (functionID) {
          case ID.Constant:
            sym.clearAttributes(ISymbol.CONSTANT);
            return true;
          case ID.Flat:
            sym.clearAttributes(ISymbol.FLAT);
            return true;
          case ID.Listable:
            sym.clearAttributes(ISymbol.LISTABLE);
            return true;
          case ID.OneIdentity:
            sym.clearAttributes(ISymbol.ONEIDENTITY);
            return true;
          case ID.Orderless:
            sym.clearAttributes(ISymbol.ORDERLESS);
            return true;
          case ID.HoldAll:
            sym.clearAttributes(ISymbol.HOLDALL);
            return true;
          case ID.HoldAllComplete:
            sym.clearAttributes(ISymbol.HOLDALLCOMPLETE);
            return true;
          case ID.HoldComplete:
            sym.clearAttributes(ISymbol.HOLDCOMPLETE);
            return true;
          case ID.HoldFirst:
            sym.clearAttributes(ISymbol.HOLDFIRST);
            return true;
          case ID.HoldRest:
            sym.clearAttributes(ISymbol.HOLDREST);
            return true;
          case ID.NHoldAll:
            sym.clearAttributes(ISymbol.NHOLDALL);
            return true;
          case ID.NHoldFirst:
            sym.clearAttributes(ISymbol.NHOLDFIRST);
            return true;
          case ID.NHoldRest:
            sym.clearAttributes(ISymbol.NHOLDREST);
            return true;
          case ID.NumericFunction:
            sym.clearAttributes(ISymbol.NUMERICFUNCTION);
            return true;
          case ID.SequenceHold:
            sym.clearAttributes(ISymbol.SEQUENCEHOLD);
            return true;
        }
      }
      return false;
    }
  }

  private static final class Protect extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IASTMutable mutable = ast.copyAST();
      for (int i = 1; i < ast.size(); i++) {
        IExpr x = Validate.checkIdentifierHoldPattern(ast.get(i), ast, engine);
        if (x.isNIL()) {
          return F.NIL;
        }
        mutable.set(i, x);
      }
      final IASTAppendable result = F.ListAlloc(mutable.size());
      mutable.forEach(x -> appendProtected(result, x));
      return result;
    }

    private static void appendProtected(final IASTAppendable result, IExpr x) {
      ISymbol symbol = (ISymbol) x;
      if (!symbol.isProtected()) {
        symbol.addAttributes(ISymbol.PROTECTED);
        result.append(x);
      }
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  private static final class Unprotect extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (Config.UNPROTECT_ALLOWED) {
        IASTMutable mutable = ast.copyAST();
        for (int i = 1; i < ast.size(); i++) {
          IExpr x = Validate.checkIdentifierHoldPattern(ast.get(i), ast, engine);
          if (x.isNIL()) {
            return F.NIL;
          }
          mutable.set(i, x);
        }

        final IASTAppendable result = F.ListAlloc(mutable.size());
        mutable.forEach(x -> appendUnprotected(result, x));
        return result;
      }
      LOGGER.log(engine.getLogLevel(),
          "Unprotect: not allowed. Set Config.UNPROTECT_ALLOWED if necessary");
      return F.NIL;
    }

    private static void appendUnprotected(final IASTAppendable result, IExpr x) {
      ISymbol symbol = (ISymbol) x;
      if (symbol.isProtected()) {
        symbol.clearAttributes(ISymbol.PROTECTED);
        result.append(x);
      }
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
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
      IAST list = ast.arg1().orNewList();
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
          if (((ISymbol) arg).isProtected()) {
            IOFunctions.printMessage(S.ClearAttributes, "write", F.list(arg), EvalEngine.get());
            throw new FailedException();
          }
          if (addAttributes(arg, attributes, ast, engine).isNIL()) {
            return F.NIL;
          }
        } else {
          // Argument `1` at position `2` is expected to be a symbol.
          return IOFunctions.printMessage(S.SetAttributes, "sym", F.List(arg, F.ZZ(i)), engine);
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
          return IOFunctions.printMessage(S.SetAttributes, "attnf", F.List(attribute), engine);
        }
      } else if (attributes.isList()) {
        final IAST lst = (IAST) attributes;
        // lst.forEach(x -> addAttributes(sym, (ISymbol) x));
        for (int i = 1; i < lst.size(); i++) {
          final ISymbol attribute = (ISymbol) lst.get(i);
          if (!addAttributes(sym, attribute)) {
            // `1` is not a known attribute.
            IOFunctions.printMessage(S.SetAttributes, "attnf", F.List(attribute), engine);
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
      if (sym.isProtected()) {
        IOFunctions.printMessage(S.SetAttributes, "write", F.list(sym), EvalEngine.get());
        throw new FailedException();
      }
      int functionID = attribute.ordinal();
      if (functionID > ID.UNKNOWN) {
        switch (functionID) {
          case ID.Constant:
            sym.addAttributes(ISymbol.CONSTANT);
            return true;
          case ID.Flat:
            sym.addAttributes(ISymbol.FLAT);
            return true;
          case ID.Listable:
            sym.addAttributes(ISymbol.LISTABLE);
            return true;
          case ID.Locked:
            sym.addAttributes(ISymbol.LOCKED);
            return true;
          case ID.OneIdentity:
            sym.addAttributes(ISymbol.ONEIDENTITY);
            return true;
          case ID.Orderless:
            sym.addAttributes(ISymbol.ORDERLESS);
            return true;
          case ID.HoldAll:
            sym.addAttributes(ISymbol.HOLDALL);
            return true;
          case ID.HoldAllComplete:
            sym.addAttributes(ISymbol.HOLDALLCOMPLETE);
            return true;
          case ID.HoldComplete:
            sym.addAttributes(ISymbol.HOLDCOMPLETE);
            return true;
          case ID.HoldFirst:
            sym.addAttributes(ISymbol.HOLDFIRST);
            return true;
          case ID.HoldRest:
            sym.addAttributes(ISymbol.HOLDREST);
            return true;
          case ID.NHoldAll:
            sym.addAttributes(ISymbol.NHOLDALL);
            return true;
          case ID.NHoldFirst:
            sym.addAttributes(ISymbol.NHOLDFIRST);
            return true;
          case ID.NHoldRest:
            sym.addAttributes(ISymbol.NHOLDREST);
            return true;
          case ID.NumericFunction:
            sym.addAttributes(ISymbol.NUMERICFUNCTION);
            return true;
          case ID.Protected:
            sym.addAttributes(ISymbol.PROTECTED);
            return true;
          case ID.ReadProtected:
            sym.addAttributes(ISymbol.READPROTECTED);
            return true;
          case ID.SequenceHold:
            sym.addAttributes(ISymbol.SEQUENCEHOLD);
            return true;
        }
      }
      return false;
    }
  }

  public static int getSymbolsAsAttributes(IAST listOfSymbols, EvalEngine engine) {
    int attributes = ISymbol.NOATTRIBUTE;
    for (int i = 1; i < listOfSymbols.size(); i++) {
      final IExpr arg = listOfSymbols.get(i);
      if (arg.isBuiltInSymbol()) {
        int functionID = ((IBuiltInSymbol) arg).ordinal();
        if (functionID > ID.UNKNOWN) {
          switch (functionID) {
            case ID.Constant:
              attributes |= ISymbol.CONSTANT;
              break;
            case ID.Flat:
              attributes |= ISymbol.FLAT;
              break;
            case ID.Listable:
              attributes |= ISymbol.LISTABLE;
              break;
            case ID.Locked:
              attributes |= ISymbol.LOCKED;
              break;
            case ID.OneIdentity:
              attributes |= ISymbol.ONEIDENTITY;
              break;
            case ID.Orderless:
              attributes |= ISymbol.ORDERLESS;
              break;
            case ID.HoldAll:
              attributes |= ISymbol.HOLDALL;
              break;
            case ID.HoldAllComplete:
              attributes |= ISymbol.HOLDALLCOMPLETE;
              break;
            case ID.HoldComplete:
              attributes |= ISymbol.HOLDCOMPLETE;
              break;
            case ID.HoldFirst:
              attributes |= ISymbol.HOLDFIRST;
              break;
            case ID.HoldRest:
              attributes |= ISymbol.HOLDREST;
              break;
            case ID.NHoldAll:
              attributes |= ISymbol.NHOLDALL;
              break;
            case ID.NHoldFirst:
              attributes |= ISymbol.NHOLDFIRST;
              break;
            case ID.NHoldRest:
              attributes |= ISymbol.NHOLDREST;
              break;
            case ID.NumericFunction:
              attributes |= ISymbol.NUMERICFUNCTION;
              break;
            case ID.Protected:
              attributes |= ISymbol.PROTECTED;
              break;
            case ID.ReadProtected:
              attributes |= ISymbol.READPROTECTED;
              break;
            case ID.SequenceHold:
              attributes |= ISymbol.SEQUENCEHOLD;
              break;
          }
        }
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
    return attributesList(symbol);
  }

  /**
   * Get the attrbutes of this <code>symbol</code> as symbolic constants in a list.
   *
   * @param symbol
   * @return
   */
  public static IAST attributesList(ISymbol symbol) {

    int attributes = symbol.getAttributes();
    IASTAppendable result = F.ListAlloc(Integer.bitCount(attributes));

    if ((attributes & ISymbol.CONSTANT) != ISymbol.NOATTRIBUTE) {
      result.append(S.Constant);
    }

    if ((attributes & ISymbol.FLAT) != ISymbol.NOATTRIBUTE) {
      result.append(S.Flat);
    }

    if ((attributes & ISymbol.HOLDALLCOMPLETE) == ISymbol.HOLDALLCOMPLETE) {
      result.append(S.HoldAllComplete);
    } else if ((attributes & ISymbol.HOLDCOMPLETE) == ISymbol.HOLDCOMPLETE) {
      result.append(S.HoldComplete);
    } else if ((attributes & ISymbol.HOLDALL) == ISymbol.HOLDALL) {
      result.append(S.HoldAll);
    } else {
      if ((attributes & ISymbol.HOLDFIRST) != ISymbol.NOATTRIBUTE) {
        result.append(S.HoldFirst);
      }

      if ((attributes & ISymbol.HOLDREST) != ISymbol.NOATTRIBUTE) {
        result.append(S.HoldRest);
      }
    }

    if ((attributes & ISymbol.LISTABLE) != ISymbol.NOATTRIBUTE) {
      result.append(S.Listable);
    }

    if ((attributes & ISymbol.NHOLDALL) == ISymbol.NHOLDALL) {
      result.append(S.NHoldAll);
    } else {
      if ((attributes & ISymbol.NHOLDFIRST) != ISymbol.NOATTRIBUTE) {
        result.append(S.NHoldFirst);
      }

      if ((attributes & ISymbol.NHOLDREST) != ISymbol.NOATTRIBUTE) {
        result.append(S.NHoldRest);
      }
    }

    if ((attributes & ISymbol.NUMERICFUNCTION) != ISymbol.NOATTRIBUTE) {
      result.append(S.NumericFunction);
    }

    if ((attributes & ISymbol.ONEIDENTITY) != ISymbol.NOATTRIBUTE) {
      result.append(S.OneIdentity);
    }

    if ((attributes & ISymbol.ORDERLESS) != ISymbol.NOATTRIBUTE) {
      result.append(S.Orderless);
    }

    if ((attributes & ISymbol.LOCKED) == ISymbol.LOCKED) {
      result.append(S.Locked);
      result.append(S.Protected);
    } else {
      if ((attributes & ISymbol.PROTECTED) != ISymbol.NOATTRIBUTE) {
        result.append(S.Protected);
      }
    }

    if ((attributes & ISymbol.SEQUENCEHOLD) == ISymbol.SEQUENCEHOLD
        && ((attributes & ISymbol.HOLDALLCOMPLETE) != ISymbol.HOLDALLCOMPLETE)) {
      result.append(S.SequenceHold);
    }
    result.sortInplace(Comparators.CANONICAL_COMPARATOR);
    return result;
  }

  public static void initialize() {
    Initializer.init();
  }

  private AttributeFunctions() {}
}
