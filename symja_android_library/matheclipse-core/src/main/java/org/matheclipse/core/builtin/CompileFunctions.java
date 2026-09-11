package org.matheclipse.core.builtin;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * What <code>Compile</code> does when nothing compiles it.
 *
 * <p>
 * The compiler proper lives in <code>matheclipse-compile</code> and registers itself over these
 * evaluators when that module is on the class path. Without it, <code>Compile[vars, body]</code>
 * stays as it is - but two things about it must still be right, because a package which offers a
 * compiled function and an interpreted fallback depends on them:
 *
 * <ul>
 * <li>the body is <em>held</em>. Evaluated before the parameters have values it collapses into
 * nonsense - <code>Table[…, {i, 1, Length[payload]}]</code> with a symbolic <code>payload</code>
 * answers <code>{}</code> - and that nonsense is what the caller would keep.
 * <li>applying it works. <code>Compile[…][args]</code> evaluates the body with the arguments in
 * place of the parameters, which is what a compiled function does, only slower.
 * </ul>
 */
public class CompileFunctions {

  private static class Initializer {

    private static void init() {
      if (!Config.FUZZY_PARSER) {
        S.Compile.setEvaluator(new Compile());
      }
    }
  }

  private static class Compile extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr head = ast.head();
      if (head.isAST(S.Compile, 3)) {
        return apply((IAST) head, ast, engine);
      }
      // Compile[vars, body] itself is left as it is - held, and ready to be applied
      return F.NIL;
    }

    /**
     * Evaluate the body of <code>compiled</code> with the arguments of <code>call</code> in place
     * of its parameters.
     */
    private static IExpr apply(IAST compiled, IAST call, EvalEngine engine) {
      IExpr parameterSpec = compiled.arg1();
      if (!parameterSpec.isList()) {
        return F.NIL;
      }
      IAST parameters = (IAST) parameterSpec;
      if (parameters.argSize() != call.argSize()) {
        return F.NIL;
      }
      // Each parameter becomes a fresh local variable holding its argument, the way a compiled
      // function's argument is a variable of the compiled code. Substituting the values instead
      // put every argument into the body literally, and a Table over a list argument then walked
      // the whole list again at each of its steps: the WLJS notebook unmasks each WebSocket frame
      // with Table[BitXor[payload[[i]], ...], {i, Length[payload]}], and a frame of a few hundred
      // kilobytes kept the server busy for hours.
      java.util.Map<IExpr, IExpr> locals = new java.util.HashMap<IExpr, IExpr>();
      ISymbol[] variables = new ISymbol[parameters.argSize()];
      for (int i = 1; i < parameters.size(); i++) {
        ISymbol parameter = parameterName(parameters.get(i));
        if (parameter == null) {
          return F.NIL;
        }
        ISymbol local = F.Dummy(parameter.getSymbolName() + EvalEngine.uniqueName("$"));
        local.assignValue(engine.evaluate(call.get(i)), false);
        variables[i - 1] = local;
        locals.put(parameter, local);
      }
      try {
        return engine.evaluate(F.subst(compiled.arg2(), locals));
      } finally {
        // the locals are not reachable afterwards; releasing their values lets the arguments go
        for (ISymbol local : variables) {
          if (local != null) {
            local.clearValue(null);
          }
        }
      }
    }

    /**
     * The symbol a parameter declaration names: <code>x</code>, <code>{x, _Integer}</code> or
     * <code>{x, _Integer, 1}</code> all declare <code>x</code>.
     */
    private static ISymbol parameterName(IExpr declaration) {
      if (declaration.isSymbol()) {
        return (ISymbol) declaration;
      }
      if (declaration.isList() && declaration.size() > 1 && declaration.first().isSymbol()) {
        return (ISymbol) declaration.first();
      }
      return null;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.HOLDALL);
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private CompileFunctions() {}
}
