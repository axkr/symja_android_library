package org.matheclipse.compile;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import org.matheclipse.core.eval.util.SourceCodeProperties;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Writes the Java source of an expression the code generator has no native case for - the
 * "symbolic fallback" - so that {@code F.eval(...)} of the generated source recomputes it exactly
 * as the uncompiled expression would.
 *
 * <p>
 * This replaces {@link IExpr#internalJavaString}, which was built to round-trip a
 * <i>hand-written</i> expression back into Java source for tests and code generation elsewhere in
 * the project. It writes every builtin symbol as {@code F.<Name>(...)}, on the assumption that a
 * same-named, same-arity factory method exists on {@link F} - which is true of the handful of
 * heads it was designed for, but not of most of the roughly 1300 (head, arity) combinations a
 * compiled expression can contain: {@code F} has no {@code NestList}, {@code FixedPointList} or
 * two-argument {@code Total} factory at all, and only a two-argument {@code Array}, so writing
 * those calls the same way produces Java source Janino cannot compile. This class checks, once,
 * which (name, arity) pairs actually have a matching {@link F} factory method, and falls back to
 * the fully general {@link F#function(IExpr, IExpr...)} constructor for everything else. It also
 * writes an unbound symbol - an argument to a {@code Function} the generator does not itself
 * inline, a compiled function's own parameter read outside a context {@link CompileFactory}
 * recognizes, a user-defined function called from a compiled body - as {@code F.symbol("name")}
 * rather than as a bare Java identifier, which is what produced "Unknown variable or type" for a
 * user function call and for the compiled loop variable of a {@code Sum} or {@code Table} the
 * generator does not compile natively.
 */
public final class SymbolicFormWriter {

  /** The exact-arity (name, argument count) pairs of a public static {@link F} factory method
   * whose parameters are all {@link IExpr}. */
  private static final Map<String, Set<Integer>> EXACT_ARITIES = new HashMap<>();

  /** The names of a public static {@link F} factory method of the form {@code (IExpr...)}, which
   * takes any number of arguments. */
  private static final Set<String> VARARGS_NAMES = new HashSet<>();

  static {
    for (Method m : F.class.getMethods()) {
      if (!Modifier.isStatic(m.getModifiers()) || !Modifier.isPublic(m.getModifiers())) {
        continue;
      }
      if (!IExpr.class.isAssignableFrom(m.getReturnType())) {
        continue;
      }
      String name = m.getName();
      if (name.isEmpty() || !Character.isUpperCase(name.charAt(0))) {
        // not the name of a Symja symbol (e.g. F.symjify, F.eval, F.list)
        continue;
      }
      Class<?>[] params = m.getParameterTypes();
      if (m.isVarArgs()) {
        if (params.length == 1 && params[0] == IExpr[].class) {
          VARARGS_NAMES.add(name);
        }
        continue;
      }
      boolean allIExpr = true;
      for (Class<?> param : params) {
        if (param != IExpr.class) {
          allIExpr = false;
          break;
        }
      }
      if (allIExpr) {
        EXACT_ARITIES.computeIfAbsent(name, k -> new HashSet<>()).add(params.length);
      }
    }
  }

  /** Whether {@code F} declares an all-{@link IExpr} factory method {@code name} of this
   * {@code arity}. */
  private static boolean hasFactory(String name, int arity) {
    return VARARGS_NAMES.contains(name) || EXACT_ARITIES.getOrDefault(name, java.util.Collections.emptySet())
        .contains(arity);
  }

  /**
   * Resolves a symbol to the Java source which reads it - a lifted constant field, a local
   * variable's {@code ExprTrie} entry, a numeric field, a statement placeholder - or
   * {@code null} to fall back to this writer's own default (a builtin symbol's {@code S.} field,
   * or {@code F.symbol("name")} for anything else).
   */
  private final Function<ISymbol, String> symbols;

  public SymbolicFormWriter(Function<ISymbol, String> symbols) {
    this.symbols = symbols;
  }

  /** The Java source of {@code expression}. */
  public String write(IExpr expression) {
    StringBuilder buf = new StringBuilder();
    write(buf, expression);
    return buf.toString();
  }

  private void write(StringBuilder buf, IExpr expr) {
    if (expr.isSymbol()) {
      writeSymbol(buf, (ISymbol) expr);
      return;
    }
    if (expr.isAST()) {
      writeAST(buf, (IAST) expr);
      return;
    }
    // numbers, strings and every other atom already write correctly through the existing
    // machinery (e.g. an IInteger becomes "F.ZZ(63L)"), which nothing here needs to duplicate
    buf.append(expr.internalJavaString(SourceCodeProperties.JAVA_FORM_PROPERTIES, -1, x -> null));
  }

  private void writeSymbol(StringBuilder buf, ISymbol symbol) {
    if (symbol == S.I) {
      // the imaginary unit is a constant, not a compiled variable
      buf.append("F.CI");
      return;
    }
    String resolved = symbols.apply(symbol);
    if (resolved != null) {
      buf.append(resolved);
      return;
    }
    if (symbol.isBuiltInSymbol()) {
      buf.append("S.").append(symbol.toString());
      return;
    }
    buf.append("F.symbol(\"").append(symbol.toString()).append("\")");
  }

  private void writeAST(StringBuilder buf, IAST ast) {
    IExpr head = ast.head();
    if ((head == S.Hold || head == S.HoldForm) && ast.isAST1()) {
      write(buf, ast.arg1());
      return;
    }
    int arity = ast.argSize();
    if (head.isBuiltInSymbol() && hasFactory(head.toString(), arity)) {
      buf.append("F.").append(head.toString()).append('(');
      writeArgs(buf, ast);
      buf.append(')');
      return;
    }
    // no matching factory method (or a non-symbol / user-defined head): the fully general AST
    // constructor always exists and always compiles, whatever the head and arity are
    buf.append("F.function(");
    write(buf, head);
    for (int i = 1; i <= arity; i++) {
      buf.append(',');
      write(buf, ast.get(i));
    }
    buf.append(')');
  }

  private void writeArgs(StringBuilder buf, IAST ast) {
    for (int i = 1; i <= ast.argSize(); i++) {
      if (i > 1) {
        buf.append(',');
      }
      write(buf, ast.get(i));
    }
  }
}
