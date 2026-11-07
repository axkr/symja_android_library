package org.matheclipse.internal.builtin;

import java.util.ArrayList;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.ISetEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.internal.InternalFunction;
import org.matheclipse.internal.expression.BagExpr;

/**
 * Expression bags: <code>Internal`Bag</code>, <code>Internal`StuffBag</code>,
 * <code>Internal`BagPart</code> and <code>Internal`BagLength</code>. A bag collects results in
 * place, without the copy that <code>AppendTo</code> makes of a list.
 */
public class BagFunctions {

  /** The head of every bag, set by {@link #init()}. */
  static IBuiltInSymbol bagSymbol;

  /**
   * <code>Internal`Bag()</code> - a new empty bag.
   *
   * <p>
   * <code>Internal`Bag({e1, e2, ...})</code> - a new bag holding the elements <code>e1, e2, ...
   * </code>.
   */
  private static final class Bag extends InternalFunction {
    Bag() {
      super(0, 1);
    }

    @Override
    protected IExpr evaluateArgs(IAST ast, EvalEngine engine) {
      ArrayList<IExpr> elements = new ArrayList<>();
      if (ast.argSize() == 1) {
        if (!ast.arg1().isList()) {
          return F.NIL;
        }
        ((IAST) ast.arg1()).forEach((IExpr x) -> elements.add(x));
      }
      return new BagExpr(bagSymbol, elements);
    }
  }

  /**
   * <code>Internal`StuffBag(bag, e)</code> - append <code>e</code> to <code>bag</code>.
   *
   * <p>
   * <code>Internal`StuffBag(bag, e, n)</code> - append the parts of <code>e</code> at level
   * <code>n</code>, one by one; level <code>0</code> is <code>e</code> itself.
   */
  private static final class StuffBag extends InternalFunction {
    StuffBag() {
      super(2, 3);
    }

    @Override
    protected IExpr evaluateArgs(IAST ast, EvalEngine engine) {
      if (!(ast.arg1() instanceof BagExpr)) {
        return F.NIL;
      }
      ArrayList<IExpr> elements = ((BagExpr) ast.arg1()).toData();
      if (ast.argSize() == 2) {
        elements.add(ast.arg2());
        return S.Null;
      }
      int level = ast.arg3().toIntDefault();
      if (level < 0) {
        return F.NIL;
      }
      if (level == 0) {
        elements.add(ast.arg2());
        return S.Null;
      }
      IExpr parts = engine.evaluate(F.binaryAST2(S.Level, ast.arg2(), F.list(F.ZZ(level))));
      if (!parts.isList()) {
        return F.NIL;
      }
      ((IAST) parts).forEach((IExpr x) -> elements.add(x));
      return S.Null;
    }
  }

  /** <code>Internal`BagLength(bag)</code> - the number of elements in <code>bag</code>. */
  private static final class BagLength extends InternalFunction {
    BagLength() {
      super(1, 1);
    }

    @Override
    protected IExpr evaluateArgs(IAST ast, EvalEngine engine) {
      if (ast.arg1() instanceof BagExpr) {
        return F.ZZ(((BagExpr) ast.arg1()).length());
      }
      return F.NIL;
    }
  }

  /**
   * <code>Internal`BagPart(bag, i)</code> - the <code>i</code>-th element of <code>bag</code>,
   * counted from the end if <code>i</code> is negative.
   *
   * <p>
   * <code>Internal`BagPart(bag, All)</code> - the elements of <code>bag</code> as a list. Any
   * other part specification is read as by <code>Part</code> of that list.
   *
   * <p>
   * <code>Internal`BagPart(bag, i) = value</code> - replace the <code>i</code>-th element.
   */
  private static final class BagPart extends InternalFunction implements ISetEvaluator {
    BagPart() {
      super(2, Integer.MAX_VALUE);
    }

    @Override
    protected IExpr evaluateArgs(IAST ast, EvalEngine engine) {
      if (!(ast.arg1() instanceof BagExpr)) {
        return F.NIL;
      }
      ArrayList<IExpr> elements = ((BagExpr) ast.arg1()).toData();
      if (ast.argSize() == 2) {
        IExpr spec = ast.arg2();
        if (spec == S.All) {
          return F.List(elements.toArray(new IExpr[0]));
        }
        if (spec.isInteger()) {
          int index = index(spec, elements.size());
          if (index < 0) {
            // Part `1` of `2` does not exist.
            return Errors.printMessage(S.Part, "partw", F.list(spec, ast.arg1()), engine);
          }
          return elements.get(index);
        }
      }
      IASTAppendable part = F.ast(S.Part, ast.argSize());
      part.append(F.List(elements.toArray(new IExpr[0])));
      for (int i = 2; i < ast.size(); i++) {
        part.append(ast.get(i));
      }
      return engine.evaluate(part);
    }

    @Override
    public IExpr evaluateSet(IExpr leftHandSide, IExpr rightHandSide,
        IBuiltInSymbol builtinSymbol, EvalEngine engine) {
      if (!leftHandSide.isAST2()) {
        return F.NIL;
      }
      IExpr bag = engine.evaluate(leftHandSide.first());
      IExpr spec = engine.evaluate(leftHandSide.second());
      if (!(bag instanceof BagExpr) || !spec.isInteger()) {
        return F.NIL;
      }
      ArrayList<IExpr> elements = ((BagExpr) bag).toData();
      int index = index(spec, elements.size());
      if (index < 0) {
        // Part `1` of `2` does not exist.
        Errors.printMessage(S.Part, "partw", F.list(spec, bag), engine);
        return rightHandSide;
      }
      IExpr value =
          builtinSymbol == S.SetDelayed ? rightHandSide : engine.evaluate(rightHandSide);
      elements.set(index, value);
      return builtinSymbol == S.SetDelayed ? S.Null : value;
    }

    /** The zero-based position of the part <code>spec</code>, or <code>-1</code>. */
    private static int index(IExpr spec, int size) {
      int i = spec.toIntDefault(0);
      if (i > 0 && i <= size) {
        return i - 1;
      }
      if (i < 0 && -i <= size) {
        return size + i;
      }
      return -1;
    }
  }

  static void init() {
    bagSymbol = InternalFunctions.define("Bag", ISymbol.NOATTRIBUTE, new Bag());
    InternalFunctions.define("StuffBag", ISymbol.NOATTRIBUTE, new StuffBag());
    InternalFunctions.define("BagLength", ISymbol.NOATTRIBUTE, new BagLength());
    InternalFunctions.define("BagPart", ISymbol.NOATTRIBUTE, new BagPart());
  }

  private BagFunctions() {}
}
