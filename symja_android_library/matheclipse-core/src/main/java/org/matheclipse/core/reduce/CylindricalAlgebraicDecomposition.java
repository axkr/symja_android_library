package org.matheclipse.core.reduce;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.basic.MachineProfile;
import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.CompareUtil;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.integrate.IntegrateTimeBudget;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.polynomials.RealRootIsolation;
import org.matheclipse.core.polynomials.RealRootIsolation.RootInterval;
import edu.jas.arith.BigRational;
import edu.jas.poly.GenPolynomial;
import edu.jas.ufd.GCDFactory;

/**
 * A cylindrical algebraic decomposition of the real solutions of a formula of polynomial equations
 * and inequalities (Collins, with the projection of McCallum).
 *
 * <p>
 * <b>Projection.</b> The polynomials of the formula are split into irreducible factors, and those
 * in the last variable are replaced by polynomials in the variables before it: the coefficients
 * down to a constant one, the discriminants and the pairwise resultants. Their real roots tell
 * where the roots of the polynomials above appear, disappear or meet.
 *
 * <p>
 * <b>Lifting.</b> Over one sample point of a cell the real roots of the polynomials of the next
 * variable divide its line into sections, the roots, and sectors, the intervals between them. A
 * formula which is decided by the variables so far ends the descent. A sector has a rational sample
 * point. A section may have an algebraic one, which is a defining polynomial with a value of 200
 * digits: the roots over it are those of an iterated resultant, whose roots over the rationals are
 * isolated exactly, and which of them belong to the fibre is told by the value of the polynomial.
 *
 * <p>
 * <b>The formula.</b> The boundary of a sector is written as a root of its polynomial, by radicals
 * for the degrees 1 and 2 and as <code>Root</code> above. Sectors with the same formula merge across
 * a section with the same solutions, which closes <code>-1&lt;x&lt;1</code> to
 * <code>-1&lt;=x&lt;=1</code> for the closed disk.
 *
 * <p>
 * A sign which can't be told, or a polynomial which vanishes over a cell of positive dimension,
 * declines the decomposition.
 */
public final class CylindricalAlgebraicDecomposition {

  /** The precision of the values of algebraic sample points. */
  private static final MathContext PRECISION = new MathContext(200, RoundingMode.HALF_EVEN);

  /** A value below this fraction of its terms is 0. */
  private static final BigDecimal ZERO_BOUND = new BigDecimal("1e-120");

  /** A value above this fraction of its terms isn't 0; in between nothing can be told. */
  private static final BigDecimal NONZERO_BOUND = new BigDecimal("1e-60");

  /** The digits of an algebraic sample point in a formula which is tested at it. */
  private static final MathContext SAMPLE_PRECISION = new MathContext(190);

  /** The digits of the test of a formula at a sample point. */
  private static final int COMPARISON_DIGITS = 100;

  /** A difference at a sample point below this is 0: a root of a root loses digits. */
  private static final double COMPARISON_TOLERANCE = 1.0e-35;

  /** The time of one decomposition. */
  private static final long MAX_MILLIS = 30000L;

  /** The number of cells of one decomposition. */
  private static final int MAX_CELLS = 40000;

  /** The degree of a polynomial in one variable whose roots are isolated. */
  private static final int MAX_DEGREE = 256;

  /** The degree of a polynomial in one variable up to which it is split into irreducible factors. */
  private static final int MAX_FACTOR_DEGREE = 12;

  /** The formula is not of the shape, or a sign can't be told. */
  private static final class Declined extends RuntimeException {
    private static final long serialVersionUID = 1L;

    Declined() {
      super(null, null, false, false);
    }
  }

  /** A polynomial in the variables, with a value at a point. */
  private final class Polynomial {
    final IExpr expr;
    final IRational[] coefficients;
    final int[][] exponents;
    /** The index of the last variable of the polynomial. */
    final int level;

    Polynomial(IExpr expr) {
      this.expr = expr;
      IExpr rules = engine.evaluate(F.CoefficientRules(expr, variables));
      if (!rules.isList()) {
        throw new Declined();
      }
      IAST list = (IAST) rules;
      coefficients = new IRational[list.argSize()];
      exponents = new int[list.argSize()][];
      int last = -1;
      for (int i = 1; i < list.size(); i++) {
        IExpr rule = list.get(i);
        if (!rule.isRuleAST() || !rule.first().isList() || !rule.second().isRational()) {
          throw new Declined();
        }
        coefficients[i - 1] = (IRational) rule.second();
        IAST powers = (IAST) rule.first();
        exponents[i - 1] = new int[powers.argSize()];
        for (int j = 1; j < powers.size(); j++) {
          int power = powers.get(j).toIntDefault();
          if (power < 0) {
            throw new Declined();
          }
          exponents[i - 1][j - 1] = power;
          if (power > 0) {
            last = Math.max(last, j - 1);
          }
        }
      }
      level = last;
    }

    /** The sign at a point whose coordinates up to {@link #level} are known. */
    int sign(Coordinate[] point) {
      boolean exact = true;
      for (int i = 0; i <= level; i++) {
        exact &= point[i].rational != null;
      }
      return exact ? exactSign(point) : approximateSign(point);
    }

    private int exactSign(Coordinate[] point) {
      IRational sum = F.C0;
      for (int t = 0; t < coefficients.length; t++) {
        IRational term = coefficients[t];
        for (int i = 0; i <= level; i++) {
          if (exponents[t][i] != 0) {
            term = term.multiply(point[i].rational.powerRational(exponents[t][i]));
          }
        }
        sum = sum.add(term);
      }
      return sum.toBigNumerator().signum();
    }

    private int approximateSign(Coordinate[] point) {
      BigDecimal sum = BigDecimal.ZERO;
      BigDecimal scale = BigDecimal.ZERO;
      for (int t = 0; t < coefficients.length; t++) {
        BigDecimal term = decimal(coefficients[t]);
        for (int i = 0; i <= level; i++) {
          if (exponents[t][i] != 0) {
            term = term.multiply(point[i].value.pow(exponents[t][i], PRECISION), PRECISION);
          }
        }
        sum = sum.add(term, PRECISION);
        scale = scale.max(term.abs());
      }
      BigDecimal size = sum.abs();
      if (size.compareTo(scale.multiply(ZERO_BOUND, PRECISION)) <= 0) {
        return 0;
      }
      if (size.compareTo(scale.multiply(NONZERO_BOUND, PRECISION)) <= 0) {
        throw new Declined();
      }
      return sum.signum();
    }
  }

  private static BigDecimal decimal(IRational number) {
    return new BigDecimal(number.toBigNumerator())
        .divide(new BigDecimal(number.toBigDenominator()), PRECISION);
  }

  /** One coordinate of a sample point. */
  private static final class Coordinate {
    /** The value of a rational coordinate, or <code>null</code>. */
    final IRational rational;
    final BigDecimal value;
    /** The polynomial of an algebraic coordinate, which has it as a root. */
    final IExpr defining;
    /** The coordinate as an expression, as far as it is written yet. */
    IExpr closedForm = F.NIL;
    /** Whether the coordinate is that of a section, not of a sector. */
    boolean section;

    Coordinate(IRational rational) {
      this.rational = rational;
      this.value = decimal(rational);
      this.defining = null;
    }

    Coordinate(BigDecimal value, IExpr defining) {
      this.rational = null;
      this.value = value;
      this.defining = defining;
    }
  }

  /** A real root in a fibre: the polynomial it is a root of. */
  private static final class FibreRoot {
    final Coordinate coordinate;
    final Polynomial polynomial;

    FibreRoot(Coordinate coordinate, Polynomial polynomial) {
      this.coordinate = coordinate;
      this.polynomial = polynomial;
    }
  }

  /** A section or a sector of a fibre. */
  private static final class Cell {
    final boolean section;
    final Coordinate sample;
    FibreRoot root;
    /** 1 or 0 for a formula decided by the variables so far, -1 with {@link #children}. */
    int truth;
    List<Cell> children;

    Cell(boolean section, Coordinate sample) {
      this.section = section;
      this.sample = sample;
    }
  }

  /** The formula: a tree of junctions with atoms as leaves. */
  private interface Node {
    /** 1 for true, 0 for false, -1 if the variables after <code>level</code> are needed. */
    int truth(Coordinate[] point, int level);
  }

  private static final class Atom implements Node {
    final Polynomial polynomial;
    final int head;

    Atom(Polynomial polynomial, int head) {
      this.polynomial = polynomial;
      this.head = head;
    }

    @Override
    public int truth(Coordinate[] point, int level) {
      if (polynomial.level > level) {
        return -1;
      }
      int sign = polynomial.level < 0 ? constantSign() : polynomial.sign(point);
      boolean result;
      switch (head) {
        case EQUAL:
          result = sign == 0;
          break;
        case UNEQUAL:
          result = sign != 0;
          break;
        case LESS:
          result = sign < 0;
          break;
        case LESS_EQUAL:
          result = sign <= 0;
          break;
        case GREATER:
          result = sign > 0;
          break;
        default:
          result = sign >= 0;
          break;
      }
      return result ? 1 : 0;
    }

    private int constantSign() {
      return polynomial.coefficients.length == 0 ? 0
          : polynomial.coefficients[0].toBigNumerator().signum();
    }
  }

  private static final class Junction implements Node {
    final boolean conjunction;
    final List<Node> args;

    Junction(boolean conjunction, List<Node> args) {
      this.conjunction = conjunction;
      this.args = args;
    }

    @Override
    public int truth(Coordinate[] point, int level) {
      boolean unknown = false;
      for (Node arg : args) {
        int value = arg.truth(point, level);
        if (value < 0) {
          unknown = true;
        } else if ((value == 1) != conjunction) {
          return value;
        }
      }
      return unknown ? -1 : (conjunction ? 1 : 0);
    }
  }

  private static final class Negation implements Node {
    final Node arg;

    Negation(Node arg) {
      this.arg = arg;
    }

    @Override
    public int truth(Coordinate[] point, int level) {
      int value = arg.truth(point, level);
      return value < 0 ? -1 : 1 - value;
    }
  }

  private static final class Constant implements Node {
    final int value;

    Constant(int value) {
      this.value = value;
    }

    @Override
    public int truth(Coordinate[] point, int level) {
      return value;
    }
  }

  private static final int EQUAL = 0;
  private static final int UNEQUAL = 1;
  private static final int LESS = 2;
  private static final int LESS_EQUAL = 3;
  private static final int GREATER = 4;
  private static final int GREATER_EQUAL = 5;

  private final EvalEngine engine;
  private final IAST variables;
  private final int n;
  private final List<List<Polynomial>> levels;
  private int cellCount;
  private boolean openFormula;

  private CylindricalAlgebraicDecomposition(IAST variables, EvalEngine engine) {
    this.variables = variables;
    this.engine = engine;
    this.n = variables.argSize();
    this.levels = new ArrayList<List<Polynomial>>(n);
    for (int i = 0; i < n; i++) {
      levels.add(new ArrayList<Polynomial>());
    }
  }

  /**
   * <code>Reduce(formula, variables, Reals)</code>
   *
   * @param formula a formula of polynomial equations and inequalities in <code>variables</code>
   * @param variables the variables, the first one with constant bounds
   * @return {@link F#NIL} if the formula is not of that kind, or the decomposition was declined
   */
  public static IExpr reduce(IExpr formula, IAST variables, EvalEngine engine) {
    try {
      return IntegrateTimeBudget.runWithin(
          () -> new CylindricalAlgebraicDecomposition(variables, engine).decompose(formula),
          MachineProfile.millis(MAX_MILLIS));
    } catch (Declined declined) {
      return F.NIL;
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return F.NIL;
    }
  }

  private IExpr decompose(IExpr formula) {
    IExpr polynomialForm = QuadraticQE.polynomialForm(formula, engine);
    if (polynomialForm.isNIL()) {
      return F.NIL;
    }
    List<Polynomial> atoms = new ArrayList<Polynomial>();
    Node root = node(polynomialForm, atoms);
    openFormula = isOpen(root);
    for (Polynomial atom : atoms) {
      addFactors(atom.expr);
    }
    project();
    Coordinate[] point = new Coordinate[n];
    int truth = root.truth(point, -1);
    if (truth >= 0) {
      return truth == 1 ? S.True : S.False;
    }
    return engine.evaluate(formula(lift(0, point, root), 0, point, new boolean[n]));
  }

  /** Whether the solutions are an open set: only strict relations, joined without a negation. */
  private static boolean isOpen(Node node) {
    if (node instanceof Atom) {
      int head = ((Atom) node).head;
      return head == LESS || head == GREATER || head == UNEQUAL;
    }
    if (node instanceof Junction) {
      for (Node arg : ((Junction) node).args) {
        if (!isOpen(arg)) {
          return false;
        }
      }
      return true;
    }
    return node instanceof Constant;
  }

  private Node node(IExpr expr, List<Polynomial> atoms) {
    if (expr.isTrue()) {
      return new Constant(1);
    }
    if (expr.isFalse()) {
      return new Constant(0);
    }
    if (expr.isAnd() || expr.isOr()) {
      List<Node> args = new ArrayList<Node>(expr.argSize());
      for (IExpr arg : (IAST) expr) {
        args.add(node(arg, atoms));
      }
      return new Junction(expr.isAnd(), args);
    }
    if (expr.isNot()) {
      return new Negation(node(expr.first(), atoms));
    }
    int head = relation(expr);
    if (head < 0 || expr.argSize() != 2) {
      throw new Declined();
    }
    IExpr expanded = engine.evaluate(F.Expand(F.Subtract(expr.first(), expr.second())));
    Polynomial polynomial = new Polynomial(expanded);
    atoms.add(polynomial);
    return new Atom(polynomial, head);
  }

  private static int relation(IExpr expr) {
    if (expr.isEqual()) {
      return EQUAL;
    }
    if (expr.isAST(S.Unequal)) {
      return UNEQUAL;
    }
    if (expr.isAST(S.Less)) {
      return LESS;
    }
    if (expr.isAST(S.LessEqual)) {
      return LESS_EQUAL;
    }
    if (expr.isAST(S.Greater)) {
      return GREATER;
    }
    if (expr.isAST(S.GreaterEqual)) {
      return GREATER_EQUAL;
    }
    return -1;
  }

  /** Add the irreducible factors of the polynomial to the levels of their last variables. */
  private void addFactors(IExpr polynomial) {
    if (polynomial.isNumber()) {
      return;
    }
    IExpr list = engine.evaluate(F.unaryAST1(S.FactorList, polynomial));
    if (!list.isList()) {
      throw new Declined();
    }
    for (IExpr pair : (IAST) list) {
      IExpr factor = pair.first();
      if (factor.isNumber()) {
        continue;
      }
      IExpr expanded = engine.evaluate(F.Expand(factor));
      Polynomial candidate = new Polynomial(expanded);
      if (candidate.level < 0) {
        continue;
      }
      List<Polynomial> level = levels.get(candidate.level);
      boolean known = false;
      for (Polynomial other : level) {
        if (other.expr.equals(expanded)
            || engine.evaluate(F.Expand(F.Plus(other.expr, expanded))).isZero()) {
          known = true;
          break;
        }
      }
      if (!known) {
        level.add(candidate);
      }
    }
  }

  private void project() {
    for (int k = n - 1; k >= 1; k--) {
      IExpr variable = variables.get(k + 1);
      List<Polynomial> level = new ArrayList<Polynomial>(levels.get(k));
      for (int i = 0; i < level.size(); i++) {
        engine.checkTimeLimit();
        IExpr polynomial = level.get(i).expr;
        IExpr list = engine.evaluate(F.CoefficientList(polynomial, variable));
        if (!list.isList() || list.argSize() < 2) {
          throw new Declined();
        }
        // from the leading coefficient down to one which can't vanish
        for (int c = list.argSize(); c >= 1; c--) {
          IExpr coefficient = list.get(c);
          if (coefficient.isNumber() && !coefficient.isZero()) {
            break;
          }
          addFactors(coefficient);
        }
        if (list.argSize() > 2) {
          // the discriminant, times the leading coefficient which is in the projection anyway
          addFactors(resultant(polynomial, engine.evaluate(F.D(polynomial, variable)), variable));
        }
        for (int j = i + 1; j < level.size(); j++) {
          addFactors(resultant(polynomial, level.get(j).expr, variable));
        }
      }
    }
  }

  /**
   * The resultant over the integers, up to a number: only its zeros matter here.
   *
   * @param polynomial a polynomial with rational coefficients
   * @param other a polynomial with rational coefficients in <code>variable</code>
   */
  @SuppressWarnings("unchecked")
  private IExpr resultant(IExpr polynomial, IExpr other, IExpr variable) {
    if (polynomial.isFree(variable)) {
      return polynomial;
    }
    VariablesSet symbols = new VariablesSet(polynomial);
    symbols.addVarList(other);
    IAST all = symbols.getVarList();
    IASTAppendable order = F.ListAlloc(all.argSize());
    for (int i = 1; i < all.size(); i++) {
      if (!all.get(i).equals(variable)) {
        order.append(all.get(i));
      }
    }
    // the last variable is the one which is eliminated
    order.append(variable);
    try {
      JASConvert<BigRational> jas = new JASConvert<BigRational>(order, BigRational.ZERO);
      GenPolynomial<BigRational> p = jas.expr2JAS(polynomial, false);
      GenPolynomial<BigRational> q = jas.expr2JAS(other, false);
      if (p == null || q == null) {
        throw new Declined();
      }
      if (p.isZERO() || q.isZERO()) {
        return F.C0;
      }
      GenPolynomial<edu.jas.arith.BigInteger> a =
          (GenPolynomial<edu.jas.arith.BigInteger>) jas.factorTerms(p)[2];
      GenPolynomial<edu.jas.arith.BigInteger> b =
          (GenPolynomial<edu.jas.arith.BigInteger>) jas.factorTerms(q)[2];
      return jas.integerPoly2Expr(
          GCDFactory.getImplementation(edu.jas.arith.BigInteger.ZERO).resultant(a, b));
    } catch (JASConversionException | ClassCastException ex) {
      throw new Declined();
    }
  }

  private List<Cell> lift(int k, Coordinate[] point, Node root) {
    engine.checkTimeLimit();
    List<FibreRoot> roots = fibre(k, point);
    List<Cell> cells = new ArrayList<Cell>(2 * roots.size() + 1);
    if (roots.isEmpty()) {
      cells.add(new Cell(false, new Coordinate(F.C0)));
    } else {
      cells.add(new Cell(false, new Coordinate(below(roots.get(0).coordinate.value))));
      for (int i = 0; i < roots.size(); i++) {
        Cell section = new Cell(true, roots.get(i).coordinate);
        section.sample.section = true;
        section.root = roots.get(i);
        cells.add(section);
        BigDecimal value = roots.get(i).coordinate.value;
        cells.add(new Cell(false, new Coordinate(i + 1 < roots.size()
            ? between(value, roots.get(i + 1).coordinate.value)
            : above(value))));
      }
    }
    if ((cellCount += cells.size()) > MAX_CELLS) {
      throw new Declined();
    }
    // the sectors first: they decide whether the sections are needed at all
    for (int i = 0; i < cells.size(); i += 2) {
      liftCell(cells.get(i), k, point, root);
    }
    for (int i = 1; i < cells.size(); i += 2) {
      Cell cell = cells.get(i);
      if (openFormula && isEmpty(cells.get(i - 1)) && isEmpty(cells.get(i + 1))) {
        // a solution here would have a neighbourhood of solutions, and that reaches the sectors
        cell.truth = 0;
      } else {
        liftCell(cell, k, point, root);
      }
    }
    point[k] = null;
    return cells;
  }

  private void liftCell(Cell cell, int k, Coordinate[] point, Node root) {
    point[k] = cell.sample;
    cell.truth = root.truth(point, k);
    if (cell.truth < 0) {
      if (k == n - 1) {
        throw new Declined();
      }
      cell.children = lift(k + 1, point, root);
    }
  }

  private static boolean isEmpty(Cell cell) {
    if (cell.children == null) {
      return cell.truth == 0;
    }
    for (Cell child : cell.children) {
      if (!isEmpty(child)) {
        return false;
      }
    }
    return true;
  }

  private static IRational below(BigDecimal value) {
    return F.ZZ(value.setScale(0, RoundingMode.FLOOR).toBigInteger().subtract(BigInteger.ONE));
  }

  private static IRational above(BigDecimal value) {
    return F.ZZ(value.setScale(0, RoundingMode.CEILING).toBigInteger().add(BigInteger.ONE));
  }

  /** A simple rational number between two numbers: the one with the smallest power of 2 below. */
  private static IRational between(BigDecimal lower, BigDecimal upper) {
    BigInteger zero = BigInteger.ZERO;
    if (lower.signum() < 0 && upper.signum() > 0) {
      return F.ZZ(zero);
    }
    BigInteger denominator = BigInteger.ONE;
    for (int i = 0; i < 600; i++) {
      BigDecimal scaled = lower.multiply(new BigDecimal(denominator), PRECISION);
      BigInteger next = scaled.setScale(0, RoundingMode.FLOOR).toBigInteger().add(BigInteger.ONE);
      BigDecimal candidate = new BigDecimal(next).divide(new BigDecimal(denominator), PRECISION);
      if (candidate.compareTo(lower) > 0 && candidate.compareTo(upper) < 0) {
        return F.QQ(F.ZZ(next), F.ZZ(denominator));
      }
      denominator = denominator.shiftLeft(1);
    }
    throw new Declined();
  }

  /** The real roots of the polynomials of the level, over the sample point, in ascending order. */
  private List<FibreRoot> fibre(int k, Coordinate[] point) {
    List<FibreRoot> roots = new ArrayList<FibreRoot>();
    for (Polynomial polynomial : levels.get(k)) {
      for (Coordinate coordinate : rootsOf(polynomial, k, point)) {
        roots.add(new FibreRoot(coordinate, polynomial));
      }
    }
    roots.sort((a, b) -> a.coordinate.value.compareTo(b.coordinate.value));
    List<FibreRoot> distinct = new ArrayList<FibreRoot>(roots.size());
    for (FibreRoot root : roots) {
      if (distinct.isEmpty()) {
        distinct.add(root);
        continue;
      }
      FibreRoot last = distinct.get(distinct.size() - 1);
      BigDecimal distance = root.coordinate.value.subtract(last.coordinate.value).abs();
      BigDecimal scale = root.coordinate.value.abs().max(BigDecimal.ONE);
      if (distance.compareTo(scale.multiply(ZERO_BOUND, PRECISION)) <= 0) {
        if (last.coordinate.rational == null && root.coordinate.rational != null) {
          distinct.set(distinct.size() - 1, root);
        }
      } else if (distance.compareTo(scale.multiply(NONZERO_BOUND, PRECISION)) <= 0) {
        throw new Declined();
      } else {
        distinct.add(root);
      }
    }
    return distinct;
  }

  private List<Coordinate> rootsOf(Polynomial polynomial, int k, Coordinate[] point) {
    IExpr restricted = polynomial.expr;
    boolean rationalPoint = true;
    for (int i = 0; i < k; i++) {
      if (point[i].rational != null) {
        restricted = F.subst(restricted, variables.get(i + 1), point[i].rational);
      } else {
        rationalPoint = false;
      }
    }
    restricted = engine.evaluate(F.Expand(restricted));
    IExpr variable = variables.get(k + 1);
    if (rationalPoint) {
      if (restricted.isZero()) {
        return nullified(k, point);
      }
      return univariateRoots(restricted, variable);
    }
    IExpr resultant = restricted;
    for (int i = k - 1; i >= 0; i--) {
      if (point[i].rational == null) {
        IExpr defining = point[i].defining;
        for (int j = 0; j < i; j++) {
          if (point[j].rational != null) {
            defining = F.subst(defining, variables.get(j + 1), point[j].rational);
          }
        }
        resultant = engine.evaluate(F.Expand(
            resultant(resultant, engine.evaluate(F.Expand(defining)), variables.get(i + 1))));
      }
    }
    if (resultant.isZero()) {
      return nullified(k, point);
    }
    List<Coordinate> result = new ArrayList<Coordinate>();
    Coordinate[] candidate = point.clone();
    for (Coordinate coordinate : univariateRoots(resultant, variable)) {
      candidate[k] = coordinate;
      if (polynomial.approximateSign(candidate) == 0) {
        result.add(coordinate.rational != null ? coordinate
            : new Coordinate(coordinate.value, polynomial.expr));
      }
    }
    return result;
  }

  /**
   * A polynomial which vanishes on the whole fibre over the point: it says nothing about the order
   * of the roots over a cell of positive dimension, the projection doesn't hold there. Over a cell
   * which is a point there is only that fibre.
   */
  private List<Coordinate> nullified(int k, Coordinate[] point) {
    for (int i = 0; i < k; i++) {
      if (!point[i].section) {
        throw new Declined();
      }
    }
    return new ArrayList<Coordinate>();
  }

  private List<Coordinate> univariateRoots(IExpr polynomial, IExpr variable) {
    List<Coordinate> roots = new ArrayList<Coordinate>();
    if (polynomial.isNumber()) {
      return roots;
    }
    // irreducible factors give rational roots and small defining polynomials, which a large
    // polynomial isn't worth: its roots only need the factors without a multiple root
    boolean large =
        engine.evaluate(F.Exponent(polynomial, variable)).toIntDefault() > MAX_FACTOR_DEGREE;
    IExpr list = engine
        .evaluate(F.unaryAST1(large ? S.FactorSquareFreeList : S.FactorList, polynomial));
    if (!list.isList()) {
      throw new Declined();
    }
    for (IExpr pair : (IAST) list) {
      IExpr factor = pair.first();
      if (factor.isNumber()) {
        continue;
      }
      IExpr coefficients = engine.evaluate(F.CoefficientList(factor, variable));
      if (!coefficients.isList() || coefficients.argSize() < 2
          || coefficients.argSize() > MAX_DEGREE) {
        throw new Declined();
      }
      IAST c = (IAST) coefficients;
      if (c.argSize() == 2) {
        if (!c.arg1().isRational() || !c.arg2().isRational()) {
          throw new Declined();
        }
        roots.add(new Coordinate(
            (IRational) engine.evaluate(F.Divide(F.Negate(c.arg1()), c.arg2()))));
        continue;
      }
      BigInteger[] integers = new BigInteger[c.argSize()];
      for (int i = 1; i < c.size(); i++) {
        if (!c.get(i).isInteger()) {
          throw new Declined();
        }
        integers[i - 1] = ((IRational) c.get(i)).toBigNumerator();
      }
      List<RootInterval> intervals = RealRootIsolation.isolate(integers);
      if (intervals == null) {
        throw new Declined();
      }
      IExpr defining = engine.evaluate(F.Expand(factor));
      for (RootInterval interval : intervals) {
        roots.add(new Coordinate(newton(integers, interval), defining));
      }
    }
    roots.sort((a, b) -> a.value.compareTo(b.value));
    return roots;
  }

  /** The root of a simple isolating interval with the digits of {@link #PRECISION}. */
  private static BigDecimal newton(BigInteger[] polynomial, RootInterval interval) {
    RootInterval narrow = RealRootIsolation.refine(polynomial, interval);
    BigDecimal denominator = new BigDecimal(narrow.denominator());
    BigDecimal x = new BigDecimal(narrow.lowerNumerator().add(narrow.upperNumerator()))
        .divide(denominator.multiply(BigDecimal.valueOf(2L)), PRECISION);
    if (narrow.isExact()) {
      return x;
    }
    BigDecimal[] p = new BigDecimal[polynomial.length];
    for (int i = 0; i < p.length; i++) {
      p[i] = new BigDecimal(polynomial[i]);
    }
    for (int iteration = 0; iteration < 12; iteration++) {
      BigDecimal value = BigDecimal.ZERO;
      BigDecimal derivative = BigDecimal.ZERO;
      for (int i = p.length - 1; i >= 0; i--) {
        derivative = derivative.multiply(x, PRECISION).add(value, PRECISION);
        value = value.multiply(x, PRECISION).add(p[i], PRECISION);
      }
      if (derivative.signum() == 0) {
        throw new Declined();
      }
      x = x.subtract(value.divide(derivative, PRECISION), PRECISION);
    }
    BigDecimal lower = new BigDecimal(narrow.lowerNumerator()).divide(denominator, PRECISION);
    BigDecimal upper = new BigDecimal(narrow.upperNumerator()).divide(denominator, PRECISION);
    if (x.compareTo(lower) < 0 || x.compareTo(upper) > 0) {
      throw new Declined();
    }
    return x;
  }

  /** The rules for the coordinates of sections, which are numbers in the bounds above them. */
  private IAST sectionRules(int k, Coordinate[] point, boolean[] section) {
    IASTAppendable rules = F.ListAlloc(k + 1);
    for (int i = 0; i < k; i++) {
      if (section[i]) {
        rules.append(F.Rule(variables.get(i + 1), point[i].closedForm));
      }
    }
    return rules;
  }

  /**
   * The root as a function of the variables before it. Over a sector of an earlier variable the
   * polynomial stays as it is; a section of one is replaced by its value, which may lower the
   * degree. Of the factors what remains then, the one with the root gives the expression.
   */
  private IExpr bound(FibreRoot root, int k, Coordinate[] point, boolean[] section) {
    if (root.coordinate.rational != null && k == 0) {
      return root.coordinate.rational;
    }
    IExpr restricted = root.polynomial.expr;
    IAST rules = sectionRules(k, point, section);
    boolean rationalSections = true;
    if (rules.argSize() > 0) {
      restricted = restricted.replaceAll(rules).orElse(restricted);
      for (int i = 0; i < k; i++) {
        rationalSections &= !section[i] || point[i].rational != null;
      }
    }
    restricted = engine.evaluate(F.Expand(restricted));
    IExpr variable = variables.get(k + 1);
    IExpr factor = restricted;
    if (rationalSections) {
      IExpr list = engine.evaluate(F.unaryAST1(S.FactorList, restricted));
      if (list.isList() && list.argSize() > 2) {
        factor = factorWithRoot((IAST) list, root, k, point);
      }
    }
    factor = engine.evaluate(F.Expand(factor));
    IExpr coefficients = engine.evaluate(F.CoefficientList(factor, variable));
    if (!coefficients.isList() || coefficients.argSize() < 2) {
      throw new Declined();
    }
    IAST c = (IAST) coefficients;
    if (c.argSize() == 2) {
      return engine.evaluate(F.Divide(F.Negate(c.arg1()), c.arg2()));
    }
    if (c.argSize() == 3) {
      return quadraticRoot(c, root, k, point);
    }
    if (!rationalSections) {
      throw new Declined();
    }
    return engine.evaluate(F.Root(F.Function(
        F.subst(factor, variable, F.Slot1)), F.ZZ(realRootIndex(factor, root, k, point))));
  }

  private IExpr factorWithRoot(IAST factors, FibreRoot root, int k, Coordinate[] point) {
    Coordinate[] sample = point.clone();
    sample[k] = root.coordinate;
    for (IExpr pair : factors) {
      IExpr factor = pair.first();
      if (factor.isNumber() || !isFree(factor)) {
        continue;
      }
      if (new Polynomial(engine.evaluate(F.Expand(factor))).approximateSign(filled(sample)) == 0) {
        return factor;
      }
    }
    throw new Declined();
  }

  /** Whether the expression is a polynomial with rational coefficients in the variables. */
  private boolean isFree(IExpr factor) {
    return factor.isFree(x -> x.isPower() && !x.exponent().isInteger(), true);
  }

  /** A point with the coordinates after the last known one at 0, for a value in the known ones. */
  private Coordinate[] filled(Coordinate[] point) {
    Coordinate[] result = point.clone();
    for (int i = 0; i < result.length; i++) {
      if (result[i] == null) {
        result[i] = new Coordinate(F.C0);
      }
    }
    return result;
  }

  /** <code>(-b+-Sqrt(b^2-4*a*c))/(2*a)</code> with the sign of the root at the sample point. */
  private IExpr quadraticRoot(IAST c, FibreRoot root, int k, Coordinate[] point) {
    IExpr a = c.arg3();
    IExpr discriminant = engine
        .evaluate(F.Expand(F.Subtract(F.Sqr(c.arg2()), F.Times(F.C4, c.arg1(), a))));
    IExpr center = engine.evaluate(F.Divide(F.Negate(c.arg2()), F.Times(F.C2, a)));
    IExpr radical;
    if (a.isNumber()) {
      // the radicand with its integer coefficients, and the square root of the denominator:
      // y^2==4-4*x^2 is y==Sqrt(4-4*x^2), 4*y^2==4-x^2 is y==Sqrt(4-x^2)/2
      IExpr radicand =
          engine.evaluate(F.Together(F.Divide(discriminant, F.Times(F.C4, F.Sqr(a)))));
      radical = F.Divide(F.Sqrt(engine.evaluate(F.Expand(F.Numerator(radicand)))),
          F.Sqrt(engine.evaluate(F.Denominator(radicand))));
    } else {
      radical = F.Times(F.C1D2,
          F.Sqrt(engine.evaluate(F.Together(F.Divide(discriminant, F.Sqr(a))))));
    }
    IExpr plus = engine.evaluate(F.Plus(center, radical));
    IExpr minus = engine.evaluate(F.Subtract(center, radical));
    double value = root.coordinate.value.doubleValue();
    double distancePlus = distance(plus, value, k, point);
    double distanceMinus = distance(minus, value, k, point);
    return distancePlus <= distanceMinus ? plus : minus;
  }

  private double distance(IExpr candidate, double value, int k, Coordinate[] point) {
    IExpr numeric = candidate;
    for (int i = 0; i < k; i++) {
      numeric = F.subst(numeric, variables.get(i + 1), F.num(point[i].value.doubleValue()));
    }
    IExpr difference = engine.evalQuiet(F.N(F.Abs(F.Subtract(numeric, F.num(value)))));
    double distance = difference.evalfNaN();
    return Double.isNaN(distance) ? Double.POSITIVE_INFINITY : distance;
  }

  /** The position of the root among the real roots of the factor over the sample point. */
  private int realRootIndex(IExpr factor, FibreRoot root, int k, Coordinate[] point) {
    Polynomial polynomial = new Polynomial(factor);
    int index = 0;
    for (Coordinate coordinate : rootsOf(polynomial, k, point)) {
      index++;
      if (coordinate.value.subtract(root.coordinate.value).abs().compareTo(NONZERO_BOUND) < 0) {
        return index;
      }
    }
    throw new Declined();
  }

  /** The formula of the cells of one fibre, in the variables up to <code>k</code>. */
  private IExpr formula(List<Cell> cells, int k, Coordinate[] point, boolean[] section) {
    int m = cells.size();
    IExpr[] inner = new IExpr[m];
    for (int i = 0; i < m; i += 2) {
      Cell cell = cells.get(i);
      inner[i] = innerFormula(cell, k, point, section);
    }
    for (int i = 1; i < m; i += 2) {
      Cell cell = cells.get(i);
      // a section with the solutions of a sector next to it needs no formula of its own
      if (isEmpty(cell)) {
        inner[i] = S.False;
        continue;
      }
      boolean left = joins(inner[i - 1], cells.get(i - 1), cell, k, point);
      boolean right = joins(inner[i + 1], cells.get(i + 1), cell, k, point);
      if (left && right) {
        // the simpler bounds, which the sector on that side has too
        inner[i] = inner[i + 1].leafCount() < inner[i - 1].leafCount() ? inner[i + 1] : inner[i - 1];
      } else if (left || right) {
        inner[i] = left ? inner[i - 1] : inner[i + 1];
      } else {
        inner[i] = innerFormula(cell, k, point, section);
      }
    }
    for (int i = 1; i < m; i += 2) {
      Cell cell = cells.get(i);
      if (cell.sample.closedForm.isNIL()) {
        cell.sample.closedForm = bound(cell.root, k, point, section);
      }
    }

    IExpr variable = variables.get(k + 1);
    IASTAppendable disjunction = F.ast(S.Or, m);
    int i = 0;
    while (i < m) {
      if (inner[i].isFalse()) {
        i++;
        continue;
      }
      Cell cell = cells.get(i);
      IExpr lower = F.NIL;
      boolean lowerClosed = false;
      IExpr body = inner[i];
      int j = i;
      if (cell.section) {
        if (i + 1 < m && !inner[i + 1].isFalse() && inner[i + 1].equals(inner[i])) {
          // the section is the closed end of the sector after it
          lower = cell.sample.closedForm;
          lowerClosed = true;
          body = inner[i + 1];
          j = i + 1;
        } else {
          disjunction.append(conjunction(F.Equal(variable, cell.sample.closedForm), inner[i]));
          i++;
          continue;
        }
      } else if (i > 0) {
        lower = cells.get(i - 1).sample.closedForm;
      }
      IExpr upper = F.NIL;
      boolean upperClosed = false;
      while (true) {
        int next = j + 1;
        if (next >= m) {
          j = next;
          break;
        }
        Cell boundary = cells.get(next);
        upper = boundary.sample.closedForm;
        if (inner[next].isFalse() || !inner[next].equals(body)) {
          j = next;
          upperClosed = false;
          break;
        }
        if (next + 1 < m && inner[next + 1].equals(body)) {
          j = next + 1;
          upper = F.NIL;
          continue;
        }
        upperClosed = true;
        j = next + 1;
        break;
      }
      disjunction.append(conjunction(range(variable, lower, lowerClosed, upper, upperClosed), body));
      i = j;
    }
    if (disjunction.argSize() == 0) {
      return S.False;
    }
    return disjunction.argSize() == 1 ? disjunction.arg1() : disjunction;
  }

  private static IExpr conjunction(IExpr condition, IExpr body) {
    if (condition.isTrue()) {
      return body;
    }
    return body.isTrue() ? condition : F.And(condition, body);
  }

  private static IExpr range(IExpr variable, IExpr lower, boolean lowerClosed, IExpr upper,
      boolean upperClosed) {
    if (lower.isNIL() && upper.isNIL()) {
      return S.True;
    }
    if (lower.isNIL()) {
      return upperClosed ? F.LessEqual(variable, upper) : F.Less(variable, upper);
    }
    if (upper.isNIL()) {
      return lowerClosed ? F.GreaterEqual(variable, lower) : F.Greater(variable, lower);
    }
    if (lowerClosed == upperClosed) {
      return lowerClosed ? F.LessEqual(lower, variable, upper) : F.Less(lower, variable, upper);
    }
    return F.ast(new IExpr[] {lower, lowerClosed ? S.LessEqual : S.Less, variable,
        upperClosed ? S.LessEqual : S.Less, upper}, S.Inequality);
  }

  /** The formula of a cell over the variables up to <code>k</code>. */
  private IExpr innerFormula(Cell cell, int k, Coordinate[] point, boolean[] section) {
    if (cell.truth >= 0) {
      return cell.truth == 1 ? S.True : S.False;
    }
    if (cell.section && cell.sample.closedForm.isNIL()) {
      cell.sample.closedForm = bound(cell.root, k, point, section);
    }
    point[k] = cell.sample;
    section[k] = cell.section;
    IExpr result = engine.evaluate(formula(cell.children, k + 1, point, section));
    point[k] = null;
    section[k] = false;
    return result;
  }

  private static IExpr sampleValue(Coordinate coordinate) {
    if (coordinate.rational != null) {
      return coordinate.rational;
    }
    BigDecimal value = coordinate.value.round(SAMPLE_PRECISION);
    return F.QQ(F.ZZ(value.unscaledValue()), F.ZZ(BigInteger.TEN.pow(Math.max(0, value.scale()))))
        .multiply(F.ZZ(BigInteger.TEN.pow(Math.max(0, -value.scale()))));
  }

  /**
   * Whether a section is written with the formula of the sector next to it: it has its solutions,
   * and they have the same shape. The closed disk has a sector with an interval of
   * <code>y</code>; the section at <code>x==1</code> has the one point <code>y==0</code>, which is
   * the formula of the sector at that place, but written on its own.
   */
  private boolean joins(IExpr body, Cell sector, Cell section, int k, Coordinate[] point) {
    return !body.isFalse() && section.children != null
        && shape(section).equals(shape(sector)) && agrees(body, section, k, point);
  }

  /**
   * The form of the solutions over a cell: the runs of cells with the same solutions in the next
   * variable, with their ends: a point, or an interval which is open or closed at either end.
   */
  private static String shape(Cell cell) {
    if (cell.children == null) {
      return cell.truth == 1 ? "T" : "F";
    }
    StringBuilder result = new StringBuilder();
    List<Cell> cells = cell.children;
    int i = 0;
    while (i < cells.size()) {
      String part = shape(cells.get(i));
      if (isFalseShape(part)) {
        i++;
        continue;
      }
      int j = i;
      while (j + 1 < cells.size() && shape(cells.get(j + 1)).equals(part)) {
        j++;
      }
      if (i == j && cells.get(i).section) {
        result.append("p(").append(part).append(')');
      } else {
        result.append(cells.get(i).section ? '[' : '(').append(part)
            .append(cells.get(j).section ? ']' : ')');
      }
      i = j + 1;
    }
    return result.toString();
  }

  private static boolean isFalseShape(String shape) {
    return shape.isEmpty() || shape.equals("F");
  }

  /**
   * Whether the formula of a sector has the solutions of the section next to it: the sections and
   * sectors of the fibre over the section are tested at their sample points. Both the formula and
   * the fibre are the same over the whole cell of the section, so one sample of each part tells.
   */
  private boolean agrees(IExpr body, Cell boundary, int k, Coordinate[] point) {
    if (boundary.children == null) {
      return false;
    }
    IASTAppendable rules = F.ListAlloc(n);
    for (int i = 0; i < k; i++) {
      rules.append(F.Rule(variables.get(i + 1), sampleValue(point[i])));
    }
    rules.append(F.Rule(variables.get(k + 1), sampleValue(boundary.sample)));
    return agreesOn(body, boundary.children, k + 1, rules);
  }

  private boolean agreesOn(IExpr body, List<Cell> cells, int k, IAST rules) {
    for (Cell cell : cells) {
      IASTAppendable next = rules.copyAppendable();
      next.append(F.Rule(variables.get(k + 1), sampleValue(cell.sample)));
      if (cell.children != null) {
        if (!agreesOn(body, cell.children, k + 1, next)) {
          return false;
        }
        continue;
      }
      if (k != n - 1 || numericTruth(body, next) != cell.truth) {
        return false;
      }
    }
    return true;
  }

  /** The value of a formula at a point: 1, 0, or -1 if it can't be told. */
  private int numericTruth(IExpr formula, IAST rules) {
    if (formula.isTrue()) {
      return 1;
    }
    if (formula.isFalse()) {
      return 0;
    }
    if (formula.isAnd() || formula.isOr()) {
      boolean unknown = false;
      for (IExpr arg : (IAST) formula) {
        int truth = numericTruth(arg, rules);
        if (truth < 0) {
          unknown = true;
        } else if ((truth == 1) != formula.isAnd()) {
          return truth;
        }
      }
      return unknown ? -1 : (formula.isAnd() ? 1 : 0);
    }
    int head = relation(formula);
    if (head < 0) {
      return -1;
    }
    IAST chain = (IAST) formula;
    for (int i = 1; i < chain.argSize(); i++) {
      int sign = numericSign(F.Subtract(chain.get(i), chain.get(i + 1)), rules);
      if (sign == 2) {
        return -1;
      }
      boolean holds;
      switch (head) {
        case EQUAL:
          holds = sign == 0;
          break;
        case UNEQUAL:
          holds = sign != 0;
          break;
        case LESS:
          holds = sign < 0;
          break;
        case LESS_EQUAL:
          holds = sign <= 0;
          break;
        case GREATER:
          holds = sign > 0;
          break;
        default:
          holds = sign >= 0;
          break;
      }
      if (!holds) {
        return 0;
      }
    }
    return 1;
  }

  /** The sign of a difference at a point, or 2 if it has no real value. */
  private int numericSign(IExpr difference, IAST rules) {
    IExpr value = CompareUtil.evalPrecise(difference.replaceAll(rules).orElse(difference),
        COMPARISON_DIGITS, engine);
    if (!(value instanceof INumber)) {
      return 2;
    }
    double re = ((INumber) value).reDoubleValue();
    double im = ((INumber) value).imDoubleValue();
    if (Double.isNaN(re) || Double.isNaN(im) || Math.abs(im) > COMPARISON_TOLERANCE) {
      return 2;
    }
    return Math.abs(re) <= COMPARISON_TOLERANCE ? 0 : (re < 0.0 ? -1 : 1);
  }
}
