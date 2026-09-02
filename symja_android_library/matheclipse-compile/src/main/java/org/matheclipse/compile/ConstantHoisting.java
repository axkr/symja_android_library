package org.matheclipse.compile;

import java.util.LinkedHashMap;
import java.util.Map;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Lifts the large constant lists out of an expression which is about to be compiled, so that each
 * of them is written into the generated source once instead of once per use.
 *
 * <p>
 * A constant list reaches the code generator's symbolic fallback, which rebuilds it in place -
 * <code>F.list(F.ZZ(183L), F.ZZ(156L), ...)</code> - every single time it is mentioned. A lookup
 * table of a few hundred numbers indexed from a handful of places therefore appears in the
 * generated method dozens of times over, and the method runs into the 64 KB limit the class file
 * format puts on it. Replacing each list with a symbol which stands for a
 * <code>private static final</code> field leaves one copy, built once when the generated class is
 * loaded rather than on every call.
 *
 * <p>
 * Only a list whose entries are numbers, or lists of numbers, is lifted, and only above
 * {@link #LEAF_THRESHOLD}. That keeps the lists which the code generator reads as syntax rather
 * than as data - the local variables of a <code>Module</code>, the iterator of a <code>Do</code> -
 * out of it, since those mention symbols, and it leaves a short literal where it is, where a field
 * would be more source rather than less.
 */
public final class ConstantHoisting {

  /**
   * The number of leaves a constant list needs before it is lifted into a field.
   *
   * <p>
   * The number itself is not critical - it sits well above the literals which appear in an
   * expression written out by hand and well below the lookup tables which cause the problem.
   */
  public static final int LEAF_THRESHOLD = 16;

  /** What {@link ConstantHoisting#hoist(IExpr)} found. */
  public static final class Result {
    private final IExpr expression;
    private final Map<ISymbol, IExpr> constants;

    private Result(IExpr expression, Map<ISymbol, IExpr> constants) {
      this.expression = expression;
      this.constants = constants;
    }

    /** The expression, with each lifted list replaced by the symbol which stands for it. */
    public IExpr expression() {
      return expression;
    }

    /**
     * The lifted lists, keyed by the symbol which replaced them, in the order they were found. Empty
     * if there was nothing to lift.
     */
    public Map<ISymbol, IExpr> constants() {
      return constants;
    }
  }

  private ConstantHoisting() {}

  /**
   * Replace the large constant lists in <code>expression</code> by symbols standing for them.
   *
   * <p>
   * Two occurrences of the same list share one symbol, which is the point of the exercise: the
   * eight lookups of a permutation table in a noise function are the same table eight times.
   */
  public static Result hoist(IExpr expression) {
    ConstantHoisting hoisting = new ConstantHoisting();
    IExpr result = hoisting.replace(expression);
    return new Result(result, hoisting.constants);
  }

  /** The lifted lists, keyed by the symbol which replaced them. */
  private final Map<ISymbol, IExpr> constants = new LinkedHashMap<>();

  /** The symbol already handed out for a list, so that equal lists share one field. */
  private final Map<IExpr, ISymbol> symbols = new LinkedHashMap<>();

  private int counter = 1;

  private IExpr replace(IExpr expression) {
    if (!expression.isAST()) {
      return expression;
    }
    if (isLiftable(expression)) {
      return symbolFor(expression);
    }

    IAST ast = (IAST) expression;
    IASTMutable result = F.NIL;
    for (int i = 1; i < ast.size(); i++) {
      IExpr argument = ast.get(i);
      IExpr replaced = replace(argument);
      if (replaced != argument) {
        if (result.isNIL()) {
          result = ast.copy();
        }
        result.set(i, replaced);
      }
    }
    return result.isPresent() ? result : ast;
  }

  private ISymbol symbolFor(IExpr constant) {
    ISymbol symbol = symbols.get(constant);
    if (symbol == null) {
      // `$` keeps the name clear of anything written by hand; nothing parses this symbol back, it
      // only ever travels from here to the code generator
      symbol = F.Dummy("constant$" + counter++);
      symbols.put(constant, symbol);
      constants.put(symbol, constant);
    }
    return symbol;
  }

  /**
   * The rank of the primitive array a lifted constant can also be written as: 1 for a list of
   * numbers, 2 for a rectangular list of lists of numbers, 0 for anything else.
   *
   * <p>
   * A constant which has one is emitted twice, as an {@link IExpr} and as a
   * <code>double[]</code>. Reading a table through the array is what lets a lookup compile to an
   * array access instead of an evaluation of <code>Part</code>, which is the difference between
   * nanoseconds and microseconds per lookup.
   */
  public static int numericArrayRank(IExpr constant) {
    if (!constant.isList()) {
      return 0;
    }
    IAST list = (IAST) constant;
    if (list.argSize() == 0) {
      return 0;
    }
    if (list.arg1().isNumber()) {
      for (int i = 2; i < list.size(); i++) {
        if (!list.get(i).isNumber()) {
          return 0;
        }
      }
      return 1;
    }
    if (!list.arg1().isList()) {
      return 0;
    }
    int columns = ((IAST) list.arg1()).argSize();
    for (int i = 1; i < list.size(); i++) {
      IExpr row = list.get(i);
      if (!row.isList() || ((IAST) row).argSize() != columns) {
        return 0;
      }
      IAST rowList = (IAST) row;
      for (int j = 1; j < rowList.size(); j++) {
        if (!rowList.get(j).isNumber()) {
          return 0;
        }
      }
    }
    return 2;
  }

  /** Whether every number in a constant is an integer. */
  public static boolean isIntegerArray(IExpr constant) {
    if (constant.isList()) {
      IAST list = (IAST) constant;
      for (int i = 1; i < list.size(); i++) {
        if (!isIntegerArray(list.get(i))) {
          return false;
        }
      }
      return true;
    }
    return constant.isInteger();
  }

  /**
   * Whether <code>expression</code> is a numeric computation once the reads of the lifted constant
   * tables in it are taken for the numbers they produce.
   *
   * <p>
   * <code>isNumericFunction</code> answers <code>false</code> for anything containing
   * <code>Part</code>, which is right in general and wrong for a read of a table of numbers. This
   * asks the same question with those reads replaced by a number.
   *
   * @param constants the symbols which stand for lifted constant tables
   * @param numericVariables the variables which count as numeric, as
   *        <code>isNumericFunction</code> takes them
   */
  public static boolean isNumericWithConstantReads(IExpr expression, Map<ISymbol, IExpr> constants,
      java.util.function.Function<IExpr, String> numericVariables) {
    if (constants.isEmpty()) {
      return false;
    }
    IExpr probe = replaceConstantReads(expression, constants);
    return probe != expression && probe.isNumericFunction(numericVariables);
  }

  /** Replace each read of a lifted constant table by a number, leaving everything else alone. */
  private static IExpr replaceConstantReads(IExpr expression, Map<ISymbol, IExpr> constants) {
    if (!expression.isAST()) {
      return expression;
    }
    IAST ast = (IAST) expression;
    if (ast.isAST(org.matheclipse.core.expression.S.Part) && ast.argSize() >= 2) {
      IExpr base = ast.arg1();
      while (base.isAST(org.matheclipse.core.expression.S.Part) && base.argSize() >= 2) {
        base = ((IAST) base).arg1();
      }
      if (base.isSymbol() && constants.containsKey(base)) {
        return F.C0;
      }
    }
    IASTMutable result = F.NIL;
    for (int i = 1; i < ast.size(); i++) {
      IExpr argument = ast.get(i);
      IExpr replaced = replaceConstantReads(argument, constants);
      if (replaced != argument) {
        if (result.isNIL()) {
          result = ast.copy();
        }
        result.set(i, replaced);
      }
    }
    return result.isPresent() ? result : ast;
  }

  /**
   * The Java array initializer for a constant whose {@link #numericArrayRank(IExpr)} is not zero.
   */
  public static String arrayInitializer(IExpr constant) {
    StringBuilder buf = new StringBuilder();
    appendArray(buf, constant);
    return buf.toString();
  }

  private static void appendArray(StringBuilder buf, IExpr expression) {
    if (!expression.isList()) {
      buf.append(expression.evalf());
      return;
    }
    IAST list = (IAST) expression;
    buf.append('{');
    for (int i = 1; i < list.size(); i++) {
      if (i > 1) {
        buf.append(", ");
      }
      appendArray(buf, list.get(i));
    }
    buf.append('}');
  }

  private static boolean isLiftable(IExpr expression) {
    return isConstantList(expression) && expression.leafCount() >= LEAF_THRESHOLD;
  }

  /** A list of numbers, or of lists of numbers, however deeply nested. */
  private static boolean isConstantList(IExpr expression) {
    if (!expression.isList()) {
      return false;
    }
    IAST list = (IAST) expression;
    for (int i = 1; i < list.size(); i++) {
      IExpr element = list.get(i);
      if (!element.isNumber() && !isConstantList(element)) {
        return false;
      }
    }
    return true;
  }
}
