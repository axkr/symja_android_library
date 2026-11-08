package org.matheclipse.internal.builtin;

import java.util.HashMap;
import java.util.Map;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.util.Iterator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.internal.InternalFunction;

/**
 * Scoping and clean-up constructs: <code>Internal`InheritedBlock</code>,
 * <code>Internal`LocalizedBlock</code>, <code>Internal`WithLocalSettings</code> and
 * <code>Internal`ConditionalValueBody</code>.
 */
public class ScopingFunctions {

  /**
   * <code>Internal`InheritedBlock({x, y = value, ...}, body)</code> - evaluate <code>body</code>
   * like <code>Block</code>, but the variables start with the values, definitions and attributes
   * they have outside. What <code>body</code> changes is undone at the end.
   */
  private static final class InheritedBlock extends InternalFunction {
    InheritedBlock() {
      super(2, 2);
    }

    @Override
    protected IExpr evaluateArgs(IAST ast, EvalEngine engine) {
      IAST variables = Validate.checkLocalVariableList(ast, 1, engine);
      if (variables.isNIL()) {
        return F.NIL;
      }
      return engine.evalBlock(() -> engine.evaluate(ast.arg2()), variables, true);
    }
  }

  /**
   * <code>Internal`LocalizedBlock({x, Subscript(a, 1), ...}, body)</code> - evaluate
   * <code>body</code> like <code>Block</code>, where a local variable may also be an indexed
   * expression such as <code>Subscript(a, 1)</code> or <code>a(1)</code>, optionally with an
   * initial value <code>Subscript(a, 1) = value</code>.
   *
   * <p>
   * An indexed variable is replaced by a fresh symbol where it is written in <code>body</code>,
   * and that symbol is replaced back in the result: unlike a symbol it is localized lexically.
   */
  private static final class LocalizedBlock extends InternalFunction {
    LocalizedBlock() {
      super(2, 2);
    }

    @Override
    protected IExpr evaluateArgs(IAST ast, EvalEngine engine) {
      if (!ast.arg1().isList()) {
        // Local variable specification `1` is not a List.
        return Errors.printMessage(ast.topHead(), "lvlist", F.list(ast.arg1()), engine);
      }
      IAST list = (IAST) ast.arg1();
      Map<IExpr, IExpr> forward = new HashMap<>();
      IASTAppendable variables = F.ListAlloc(list.argSize());
      for (int i = 1; i < list.size(); i++) {
        IExpr variable = list.get(i);
        boolean assignment = variable.isAST(S.Set, 3);
        IExpr name = assignment ? variable.first() : variable;
        if (name.isSymbol()) {
          variables.append(variable);
          continue;
        }
        if (!name.isAST() || name.isList()) {
          // Local variable specification `1` contains `2` which is not a symbol or an
          // assignment to a symbol.
          return Errors.printMessage(ast.topHead(), "lvsym", F.list(list, variable), engine);
        }
        IExpr local = forward.get(name);
        if (local == null) {
          IExpr head = name.topHead();
          String base = head.isSymbol() ? ((ISymbol) head).getSymbolName() : "local";
          local = F.Dummy(base + EvalEngine.uniqueName("$"));
          forward.put(name, local);
        }
        variables.append(assignment ? F.Set(local, variable.second()) : local);
      }
      if (forward.isEmpty()) {
        return engine.evalBlock(() -> engine.evaluate(ast.arg2()), variables);
      }
      IExpr body = F.subst(ast.arg2(), forward);
      IExpr result = engine.evalBlock(() -> engine.evaluate(body), variables);
      return Iterator.restoreLocalized(result, forward);
    }
  }

  /**
   * <code>Internal`WithLocalSettings(init, body, cleanup)</code> - evaluate <code>init</code>,
   * then <code>body</code>, and then <code>cleanup</code>, also when <code>body</code> is left by
   * <code>Abort</code>, <code>Throw</code>, <code>Return</code> or a time limit. The result is
   * that of <code>body</code>; an abort or throw goes on after the clean-up.
   */
  private static final class WithLocalSettings extends InternalFunction {
    WithLocalSettings() {
      super(3, 3);
    }

    @Override
    protected IExpr evaluateArgs(IAST ast, EvalEngine engine) {
      engine.evaluate(ast.arg1());
      boolean completed = false;
      try {
        IExpr result = engine.evaluate(ast.arg2());
        completed = true;
        return result;
      } finally {
        try {
          // a pending abort doesn't skip the clean-up
          engine.evalAbortProtected(ast.arg3());
        } catch (RuntimeException cleanupFailure) {
          // the exception which left the body goes first
          if (completed) {
            throw cleanupFailure;
          }
        }
      }
    }
  }

  /**
   * <code>Internal`ConditionalValueBody(f, {test1, test2, ...}, {{msg1, a1, ...}, ...}, body)
   * </code> - evaluate the tests in turn; at the first one which isn't <code>True</code> print
   * the message <code>f::msgi</code> with the arguments <code>ai, ...</code> and give
   * <code>$Failed</code>. If all tests give <code>True</code> evaluate <code>body</code>.
   */
  private static final class ConditionalValueBody extends InternalFunction {
    ConditionalValueBody() {
      super(4, 4);
    }

    @Override
    protected IExpr evaluateArgs(IAST ast, EvalEngine engine) {
      IExpr head = engine.evaluate(ast.arg1());
      IExpr tests = ast.arg2();
      IExpr messages = engine.evaluate(ast.arg3());
      if (!head.isSymbol() || !tests.isList() || !messages.isList()
          || tests.argSize() != messages.argSize()) {
        return F.NIL;
      }
      for (int i = 1; i < tests.size(); i++) {
        if (!engine.evaluate(((IAST) tests).get(i)).isTrue()) {
          IExpr message = ((IAST) messages).get(i);
          IAST spec = message.isList() ? (IAST) message : F.list(message);
          if (spec.argSize() > 0) {
            IExpr name = spec.arg1();
            String messageName = name.isString() ? name.toString()
                : name.isSymbol() ? ((ISymbol) name).getSymbolName() : null;
            if (messageName != null) {
              Errors.printMessage((ISymbol) head, messageName, spec.rest(), engine);
            }
          }
          return S.$Failed;
        }
      }
      return engine.evaluate(ast.arg4());
    }
  }

  static void init() {
    InternalFunctions.define("InheritedBlock", ISymbol.HOLDALL, new InheritedBlock());
    InternalFunctions.define("LocalizedBlock", ISymbol.HOLDALL, new LocalizedBlock());
    InternalFunctions.define("WithLocalSettings", ISymbol.HOLDALL, new WithLocalSettings());
    InternalFunctions.define("ConditionalValueBody", ISymbol.HOLDALL,
        new ConditionalValueBody());
  }

  private ScopingFunctions() {}
}
