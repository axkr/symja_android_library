package org.matheclipse.core.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * The order in which <code>TraditionalForm</code> writes the terms of a sum, as a textbook does:
 * <code>x^3 + x^2 + x + 1</code> rather than the canonical <code>1 + x + x^2 + x^3</code>.
 *
 * <ol>
 * <li>Numbers come last.</li>
 * <li>An absolute value is a variable of its own which comes before the symbols:
 * <code>|x| + x^2</code>.</li>
 * <li>The terms are compared by the symbols in alphabetical order, the higher degree first. The
 * degree of a symbol is its exponent where it is a factor of the term, and 1 where it only occurs
 * in the argument of a function. A symbolic exponent counts as higher than a numeric one, so
 * <code>x^n</code> precedes <code>x^2</code>; <code>sin(x) cos(y)</code> precedes <code>x</code>
 * and <code>y cos(x)</code> precedes <code>x sin(x)</code>.</li>
 * <li>Of terms of equal degrees, a term without a function comes first: <code>5 x + cos(9 x)</code>.
 * </li>
 * <li>Functions without a fixed rank, such as a user-defined <code>f(x)</code>, come first by
 * their name. The others follow a fixed order: <code>exp</code>, <code>log</code>, the
 * trigonometric, the hyperbolic and some special functions. An inverse function takes the place
 * of its function, just before it (<code>sin^-1(x) + cos^-1(x) + cos(x)</code>). The same
 * function is ordered by its arguments: <code>cos(x) + cos(2 x)</code>. A power of a function is
 * ordered as the function: <code>x + sin^2(x)</code>.</li>
 * <li>Then the higher powers of the factors which are symbols: <code>x sin(x) + sin(x)</code>.</li>
 * </ol>
 * A sum with a complex coefficient keeps its canonical order. The factors of a product which are
 * functions are written in the same order of functions: <code>sin(x) cos(y)</code>.
 */
public final class TraditionalSumOrder {

  /**
   * The rank of a function which is written in a fixed order; an inverse function has the rank of
   * its function minus one half, as twice the rank.
   */
  private static final Map<ISymbol, Integer> FUNCTION_RANK = new IdentityHashMap<>();

  /** The functions which are variables of their own and come before the symbols. */
  private static final Map<ISymbol, Boolean> VARIABLE_FUNCTIONS = new IdentityHashMap<>();

  static {
    ISymbol[] ranked = {S.Exp, S.Log, //
        S.Sin, S.Cos, S.Tan, S.Cot, S.Csc, S.Sec, //
        S.Sinh, S.Cosh, S.Tanh, S.Coth, S.Csch, S.Sech, //
        S.Gamma, S.BesselJ, S.BesselY, S.BesselI, S.BesselK};
    ISymbol[][] inverse = {{S.ArcSin, S.Sin}, {S.ArcCos, S.Cos}, {S.ArcTan, S.Tan},
        {S.ArcCot, S.Cot}, {S.ArcCsc, S.Csc}, {S.ArcSec, S.Sec}, {S.ArcSinh, S.Sinh},
        {S.ArcCosh, S.Cosh}, {S.ArcTanh, S.Tanh}, {S.ArcCoth, S.Coth}, {S.ArcCsch, S.Csch},
        {S.ArcSech, S.Sech}};
    for (int i = 0; i < ranked.length; i++) {
      FUNCTION_RANK.put(ranked[i], 2 * i + 1);
    }
    for (ISymbol[] pair : inverse) {
      FUNCTION_RANK.put(pair[0], FUNCTION_RANK.get(pair[1]) - 1);
    }
    VARIABLE_FUNCTIONS.put(S.Abs, Boolean.TRUE);
  }

  /** A term of the sum split into its symbols with their degrees and its function factors. */
  private static final class Term {
    final IExpr expr;
    /** The symbols which are factors of the term mapped to their exponents. */
    final Map<IExpr, IExpr> powers = new HashMap<>();
    /** The symbols which occur in the argument of a function factor. */
    final List<IExpr> inFunctions = new ArrayList<>();
    /** The factors which are variables of their own, mapped to their exponents. */
    final Map<IExpr, IExpr> variableFunctions = new HashMap<>();
    /** The factors which are no powers of a symbol, in their display order. */
    final List<IExpr> functions = new ArrayList<>();
    boolean complex;

    Term(IExpr expr) {
      this.expr = expr;
      IAST factors = expr.isTimes() ? (IAST) expr : F.list(expr);
      for (IExpr factor : factors) {
        if (factor.isNumber()) {
          complex |= factor.isComplex() || factor.isComplexNumeric();
          continue;
        }
        if (factor.isNumericFunction(true)) {
          // a constant such as Pi or Sqrt(2) is part of the coefficient
          continue;
        }
        IExpr base = factor.isPower() ? factor.base() : factor;
        IExpr exponent = factor.isPower() ? factor.exponent() : F.C1;
        if (base.isSymbol() && !base.isConstantAttribute()) {
          powers.put(base, exponent);
        } else if (isVariableFunction(base)) {
          variableFunctions.put(base, exponent);
        } else {
          functions.add(factor);
          for (IExpr symbol : symbolsOf(factor)) {
            if (!inFunctions.contains(symbol)) {
              inFunctions.add(symbol);
            }
          }
        }
      }
      functions.sort(TraditionalSumOrder::compareFunctions);
    }

    boolean isNumber() {
      return powers.isEmpty() && functions.isEmpty() && variableFunctions.isEmpty();
    }

    /** The degree of the symbol: its exponent as a factor, else 1 inside a function, else 0. */
    IExpr degree(IExpr symbol) {
      IExpr exponent = powers.get(symbol);
      if (exponent != null) {
        return exponent;
      }
      return inFunctions.contains(symbol) ? F.C1 : F.C0;
    }
  }

  private TraditionalSumOrder() {}

  private static boolean isVariableFunction(IExpr base) {
    return base.isAST() && VARIABLE_FUNCTIONS.containsKey(base.head());
  }

  /** Whether the factor of a product is a function, or a power of one, rather than a variable. */
  private static boolean isFunctionFactor(IExpr factor) {
    if (factor.isNumber() || factor.isNumericFunction(true)) {
      return false;
    }
    IExpr base = factor.isPower() ? factor.base() : factor;
    return !(base.isSymbol() && !base.isConstantAttribute()) && !isVariableFunction(base);
  }

  /**
   * The product with its factors which are functions in the order <code>TraditionalForm</code>
   * writes them; the other factors keep their places.
   *
   * @param times a product
   * @return the product in that order, or the product itself if it is already in that order
   */
  public static IAST orderFactors(IAST times) {
    List<Integer> positions = new ArrayList<>();
    List<IExpr> functions = new ArrayList<>();
    for (int i = 1; i < times.size(); i++) {
      if (isFunctionFactor(times.get(i))) {
        positions.add(i);
        functions.add(times.get(i));
      }
    }
    if (functions.size() < 2) {
      return times;
    }
    functions.sort(TraditionalSumOrder::compareFunctions);
    IASTAppendable result = null;
    for (int j = 0; j < positions.size(); j++) {
      int position = positions.get(j);
      if (times.get(position) != functions.get(j)) {
        if (result == null) {
          result = times.copyAppendable();
        }
        result.set(position, functions.get(j));
      }
    }
    return result == null ? times : result;
  }

  /**
   * The terms of the sum in the order <code>TraditionalForm</code> writes them.
   *
   * @param plus a sum
   * @return a list of the terms
   */
  public static IAST order(IAST plus) {
    List<Term> terms = new ArrayList<>(plus.argSize());
    TreeSet<IExpr> symbols = new TreeSet<>();
    TreeSet<IExpr> variableFunctions = new TreeSet<>();
    for (IExpr arg : plus) {
      Term term = new Term(arg);
      if (term.complex) {
        return plus.setAtCopy(0, S.List);
      }
      symbols.addAll(term.powers.keySet());
      symbols.addAll(term.inFunctions);
      variableFunctions.addAll(term.variableFunctions.keySet());
      terms.add(term);
    }
    final IExpr[] symbolOrder = symbols.toArray(new IExpr[0]);
    final IExpr[] variableFunctionOrder = variableFunctions.toArray(new IExpr[0]);
    terms.sort((t1, t2) -> compareTerms(t1, t2, symbolOrder, variableFunctionOrder));
    IASTAppendable result = F.ListAlloc(terms.size());
    for (Term term : terms) {
      result.append(term.expr);
    }
    return result;
  }

  private static int compareTerms(Term t1, Term t2, IExpr[] symbolOrder,
      IExpr[] variableFunctionOrder) {
    if (t1.isNumber() != t2.isNumber()) {
      return t1.isNumber() ? 1 : -1;
    }
    for (IExpr variable : variableFunctionOrder) {
      int cmp = compareExponents(t1.variableFunctions.getOrDefault(variable, F.C0),
          t2.variableFunctions.getOrDefault(variable, F.C0));
      if (cmp != 0) {
        return cmp;
      }
    }
    for (IExpr symbol : symbolOrder) {
      int cmp = compareExponents(t1.degree(symbol), t2.degree(symbol));
      if (cmp != 0) {
        return cmp;
      }
    }
    boolean f1 = !t1.functions.isEmpty();
    boolean f2 = !t2.functions.isEmpty();
    if (f1 != f2) {
      return f1 ? 1 : -1;
    }
    int n = Math.min(t1.functions.size(), t2.functions.size());
    for (int i = 0; i < n; i++) {
      int cmp = compareFunctions(t1.functions.get(i), t2.functions.get(i));
      if (cmp != 0) {
        return cmp;
      }
    }
    if (t1.functions.size() != t2.functions.size()) {
      return t1.functions.size() > t2.functions.size() ? -1 : 1;
    }
    for (IExpr symbol : symbolOrder) {
      int cmp = compareExponents(t1.powers.getOrDefault(symbol, F.C0),
          t2.powers.getOrDefault(symbol, F.C0));
      if (cmp != 0) {
        return cmp;
      }
    }
    return t1.expr.compareTo(t2.expr);
  }

  /** The higher exponent first; a symbolic exponent before a numeric one. */
  private static int compareExponents(IExpr e1, IExpr e2) {
    boolean real1 = e1.isReal();
    boolean real2 = e2.isReal();
    if (real1 && real2) {
      if (((IReal) e1).isGT((IReal) e2)) {
        return -1;
      }
      return ((IReal) e1).isLT((IReal) e2) ? 1 : 0;
    }
    if (real1 != real2) {
      return real1 ? 1 : -1;
    }
    return e1.compareTo(e2);
  }

  /**
   * The functions without a fixed rank first, by their name, then the others in the order of their
   * rank; the same function by its arguments.
   */
  private static int compareFunctions(IExpr g1, IExpr g2) {
    IExpr h1 = functionHead(g1);
    IExpr h2 = functionHead(g2);
    Integer r1 = FUNCTION_RANK.get(h1);
    Integer r2 = FUNCTION_RANK.get(h2);
    if (r1 != null || r2 != null) {
      if (r1 == null) {
        return -1;
      }
      if (r2 == null) {
        return 1;
      }
      if (!r1.equals(r2)) {
        return r1 < r2 ? -1 : 1;
      }
    } else {
      int cmp = h1.compareTo(h2);
      if (cmp != 0) {
        return cmp;
      }
    }
    return g1.compareTo(g2);
  }

  /** The function a factor applies: <code>Exp</code> for a power of <code>E</code>. */
  private static IExpr functionHead(IExpr factor) {
    IExpr base = factor;
    if (factor.isPower()) {
      if (factor.base() == S.E) {
        return S.Exp;
      }
      base = factor.base();
    }
    return base.isAST() ? base.head() : base;
  }

  /** The symbols which are variables of the expression, in no particular order. */
  private static List<IExpr> symbolsOf(IExpr expr) {
    List<IExpr> result = new ArrayList<>();
    collectSymbols(expr, result);
    return result;
  }

  private static void collectSymbols(IExpr expr, List<IExpr> result) {
    if (expr.isSymbol()) {
      if (!expr.isConstantAttribute() && !result.contains(expr)) {
        result.add(expr);
      }
    } else if (expr.isAST()) {
      IAST ast = (IAST) expr;
      for (int i = 1; i < ast.size(); i++) {
        collectSymbols(ast.get(i), result);
      }
    }
  }
}
