package org.matheclipse.core.visit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.IdentityHashMap;
import java.util.Random;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.system.ExprEvaluatorTestCase;
import org.matheclipse.core.system.TestTags;

/**
 * The scope checks of {@link ModuleReplaceAll} give the same renamings with and without its
 * subtree-symbol index.
 */
public class ModuleReplaceAllScopeIndexTest extends ExprEvaluatorTestCase {

  private static final String[] SYMBOLS = {"a", "b", "c", "x", "y", "t", "f", "g", "h"};

  private static final String[] PATTERN_LHS = {"g(x_)", "g(x_, y_)", "g(x_) /; x > a",
      "g(t:h(y_))", "g(x_h)", "g(x__)", "h(y_, a)", "f(c_.)", "g(x_?(# > b &))"};

  /** The substitutions of the scoping constructs the generated bodies are checked with. */
  private IdentityHashMap<ISymbol, IExpr>[] variableSets() {
    @SuppressWarnings("unchecked")
    IdentityHashMap<ISymbol, IExpr>[] sets = new IdentityHashMap[4];
    for (int i = 0; i < sets.length; i++) {
      sets[i] = new IdentityHashMap<ISymbol, IExpr>();
    }
    // With({a = x + b}, ...)
    sets[0].put(F.symbol("a"), evaluator.parse("x + b"));
    // Module({a, b}, ...)
    sets[1].put(F.symbol("a"), F.symbol("a$1"));
    sets[1].put(F.symbol("b"), F.symbol("b$1"));
    // With({a = 1, f = t}, ...)
    sets[2].put(F.symbol("a"), F.C1);
    sets[2].put(F.symbol("f"), F.symbol("t"));
    // Module({x, h}, ...)
    sets[3].put(F.symbol("x"), F.symbol("x$1"));
    sets[3].put(F.symbol("h"), F.symbol("h$1"));
    return sets;
  }

  /** Substitute the <code>variables</code> into <code>body</code> as Module and With do. */
  private static String substitute(IExpr body, IdentityHashMap<ISymbol, IExpr> variables,
      boolean useScopeIndex) {
    IExpr result = body.accept(
        ModuleReplaceAll.scopingConstruct(variables, EvalEngine.get(), "$1", useScopeIndex));
    return result.orElse(body).toString();
  }

  /** @return the result with the index */
  private static String checkSame(IExpr body, IdentityHashMap<ISymbol, IExpr> variables) {
    String result = substitute(body, variables, true);
    assertEquals(substitute(body, variables, false), result, body.toString());
    return result;
  }

  @Test
  public void testDocumentedRenamings() {
    check("With({a = 1}, Hold(f(t_) := t + a))", //
        "Hold(f(t$_):=t$+1)");
    check("With({a = 1}, Hold(Function(x, a*x)))", //
        "Hold(Function(x$,1*x$))");
    IdentityHashMap<ISymbol, IExpr>[] sets = variableSets();
    checkSame(evaluator.parse("Hold(f(x_) := x + a)"), sets[0]);
    checkSame(evaluator.parse("Hold(h(y_) = z)"), sets[3]);
    checkSame(evaluator.parse("Hold(g(a_f) := a)"), sets[2]);
  }

  @Test
  public void testGeneratedBodies() {
    Random random = new Random(20261029L);
    IdentityHashMap<ISymbol, IExpr>[] sets = variableSets();
    int renamedScopes = 0;
    for (int i = 0; i < 400; i++) {
      IExpr body = evaluator.parse("Hold(" + generate(random, 4) + ")");
      for (IdentityHashMap<ISymbol, IExpr> variables : sets) {
        String result = checkSame(body, variables);
        if (result.matches("(?s).*[a-z]\\$[^0-9].*|(?s).*[a-z]\\$")) {
          // a name$ of a nested scope reached by the substitution
          renamedScopes++;
        }
      }
    }
    // the bodies exercise the renaming, not only the plain substitution
    assertTrue(renamedScopes > 200, "renamed scopes: " + renamedScopes);
  }

  /** A subtree shared by two places of the body, numbered once. */
  @Test
  public void testSharedSubtree() {
    IExpr rule = evaluator.parse("g(x_) :> (x + a)");
    IExpr body = F.Hold(F.List(rule, F.Function(F.symbol("y"), F.List(rule, F.symbol("y")))));
    for (IdentityHashMap<ISymbol, IExpr> variables : variableSets()) {
      checkSame(body, variables);
    }
  }

  @Test
  public void testManyBoundNames() {
    StringBuilder body = new StringBuilder("a");
    for (int i = 0; i < 70; i++) {
      body.insert(0, "g(p" + i + "_) :> (").append(" + p" + i + ")");
    }
    checkSame(evaluator.parse("Hold(" + body + ")"), variableSets()[2]);
  }

  @Test
  public void testDeepChains() {
    for (int width : new int[] {0, 30}) {
      checkSame(evaluator.parse("Hold(" + functionChain(300, width) + ")"), variableSets()[2]);
      checkSame(evaluator.parse("Hold(" + ruleChain(300, width) + ")"), variableSets()[2]);
    }
  }

  /**
   * Prints the time of the substitution into nested rules and functions with and without the index;
   * with the index it grows linearly with the nesting depth.
   */
  @Test
  @Tag(TestTags.SLOW)
  public void testDeepChainTiming() {
    for (int depth : new int[] {50, 200}) {
      for (int width : new int[] {0, 1000}) {
        for (String body : new String[] {functionChain(depth, width), ruleChain(depth, width)}) {
          IExpr expr = evaluator.parse(body);
          IdentityHashMap<ISymbol, IExpr> variables = variableSets()[2];
          long[] nanos = {Long.MAX_VALUE, Long.MAX_VALUE};
          for (int run = 0; run < 3; run++) {
            for (int k = 0; k < 2; k++) {
              long start = System.nanoTime();
              substitute(expr, variables, k == 1);
              nanos[k] = Math.min(nanos[k], System.nanoTime() - start);
            }
          }
          System.out.printf("%s depth %d width %d: walk %.2f ms, index %.2f ms%n",
              body.startsWith("Function") ? "Function" : "Rule", depth, width, nanos[0] / 1e6,
              nanos[1] / 1e6);
        }
      }
    }
  }

  /** <code>Function(x1, x1 + s(...) + Function(x2, ...))</code> with a list of width symbols. */
  private static String functionChain(int depth, int width) {
    StringBuilder buf = new StringBuilder("a");
    for (int i = depth; i > 0; i--) {
      buf.insert(0, "Function(x" + i + ", x" + i + " + " + wide(width) + "+ ").append(")");
    }
    return buf.toString();
  }

  /** <code>g(x1_) :> (x1 + s(...) + (g(x2_) :> ...))</code> with a list of width symbols. */
  private static String ruleChain(int depth, int width) {
    StringBuilder buf = new StringBuilder("a");
    for (int i = depth; i > 0; i--) {
      buf.insert(0, "g(x" + i + "_) :> (x" + i + " + " + wide(width) + "+ ").append(")");
    }
    return buf.toString();
  }

  private static String wide(int width) {
    if (width == 0) {
      return "";
    }
    StringBuilder buf = new StringBuilder("s(");
    for (int i = 0; i < width; i++) {
      buf.append(i == 0 ? "" : ",").append("u").append(i % 50);
    }
    return buf.append(")").toString();
  }

  private static String generate(Random random, int depth) {
    if (depth == 0) {
      return SYMBOLS[random.nextInt(SYMBOLS.length)];
    }
    String lhs = PATTERN_LHS[random.nextInt(PATTERN_LHS.length)];
    switch (random.nextInt(10)) {
      case 0:
        return "f(" + generate(random, depth - 1) + ", " + generate(random, depth - 1) + ")";
      case 1:
        return lhs + " :> " + paren(generate(random, depth - 1));
      case 2:
        return lhs + " -> " + paren(generate(random, depth - 1));
      case 3:
        return lhs + " := " + paren(generate(random, depth - 1));
      case 4:
        return lhs + " = " + paren(generate(random, depth - 1));
      case 5:
        return "Function(x, " + generate(random, depth - 1) + ")";
      case 6:
        return "Function({x, y}, " + generate(random, depth - 1) + ")";
      case 7:
        return "Module({y, a}, " + generate(random, depth - 1) + ")";
      case 8:
        return "With({b = a}, " + generate(random, depth - 1) + ")";
      default:
        return "{" + generate(random, depth - 1) + ", <|k -> " + generate(random, depth - 1)
            + "|>}";
    }
  }

  private static String paren(String expr) {
    return "(" + expr + ")";
  }
}
