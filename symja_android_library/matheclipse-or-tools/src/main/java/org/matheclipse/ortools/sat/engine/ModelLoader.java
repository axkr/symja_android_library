package org.matheclipse.ortools.sat.engine;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.matheclipse.ortools.sat.ConstraintProto;
import org.matheclipse.ortools.sat.CpModelProto;
import org.matheclipse.ortools.sat.CpObjectiveProto;
import org.matheclipse.ortools.sat.DecisionStrategyProto;
import org.matheclipse.ortools.sat.ElementConstraintProto;
import org.matheclipse.ortools.sat.FloatObjectiveProto;
import org.matheclipse.ortools.sat.IntervalConstraintProto;
import org.matheclipse.ortools.sat.LinearArgumentProto;
import org.matheclipse.ortools.sat.LinearExpressionProto;
import org.matheclipse.ortools.sat.TableConstraintProto;
import org.matheclipse.ortools.util.Domain;

/**
 * Translates a (validated) {@link CpModelProto} into the variables and propagators of the Java
 * engine which replaces the C++ CP-SAT solver. Linear expressions with more than one variable
 * inside other constraints are replaced by auxiliary variables, products of more than two factors
 * are decomposed into binary products, and unary linear constraints restrict the domains
 * directly.
 */
final class ModelLoader {
  final CpModelProto model;
  final DomainStore s = new DomainStore();
  final int numModelVars;
  /** false if the model was found infeasible while loading. */
  boolean feasible = true;

  /** Linear constraints usable by the LP relaxation: vars, coeffs, lower, upper (no holes). */
  final List<LinearRow> rows = new ArrayList<>();

  /** Objective (inner, minimized): null if the model has no objective. */
  LinearPropagator objective;
  int[] objVars = new int[0];
  long[] objCoeffs = new long[0];
  /** displayed objective = scale * (inner + offset) */
  double objScale = 1;
  double objOffset;
  /** For a floating point objective: displayed = sum(floatCoeffs[i] * value) + floatOffset. */
  boolean floatObjective;
  double[] floatCoeffs;
  double floatOffset;
  boolean floatMaximize;
  /** Coefficient of each engine variable in the (minimized) objective. */
  long[] objCoefOfVar;

  final List<Strategy> strategies = new ArrayList<>();
  int[] hintVars = new int[0];
  long[] hintValues = new long[0];
  int[] assumptions = new int[0];

  /** Number of lexicographic symmetry breaking constraints added. */
  int numSymmetryConstraints;

  private final Map<Integer, SchedulingPropagators.Interval> intervals = new LinkedHashMap<>();
  private int trueVar = -1;

  /** A linear row lower &lt;= sum coeffs * vars &lt;= upper of the LP relaxation. */
  static final class LinearRow {
    final int[] vars;
    final long[] coeffs;
    final long lower;
    final long upper;

    LinearRow(int[] vars, long[] coeffs, long lower, long upper) {
      this.vars = vars;
      this.coeffs = coeffs;
      this.lower = lower;
      this.upper = upper;
    }
  }

  /** A decision strategy over affine views. */
  static final class Strategy {
    final Affine[] exprs;
    final DecisionStrategyProto.VariableSelectionStrategy varSel;
    final DecisionStrategyProto.DomainReductionStrategy domRed;

    Strategy(Affine[] exprs, DecisionStrategyProto.VariableSelectionStrategy varSel,
        DecisionStrategyProto.DomainReductionStrategy domRed) {
      this.exprs = exprs;
      this.varSel = varSel;
      this.domRed = domRed;
    }
  }

  ModelLoader(CpModelProto model, boolean breakSymmetries) {
    this.model = model;
    this.numModelVars = model.getVariablesCount();
    for (int v = 0; v < numModelVars; v++) {
      s.newVar(SolutionChecker.domainOf(model.getVariables(v)));
    }
    for (int c = 0; c < model.getConstraintsCount() && feasible; c++) {
      ConstraintProto ct = model.getConstraints(c);
      if (ct.getConstraintCase() == ConstraintProto.ConstraintCase.INTERVAL) {
        loadInterval(c, ct);
      }
    }
    for (int c = 0; c < model.getConstraintsCount() && feasible; c++) {
      ConstraintProto ct = model.getConstraints(c);
      if (ct.getConstraintCase() != ConstraintProto.ConstraintCase.INTERVAL) {
        load(ct);
      }
    }
    if (feasible && breakSymmetries) {
      int[][] columns = SymmetryDetector.detectColumns(model);
      if (columns != null) {
        for (int j = 0; j + 1 < columns.length; j++) {
          s.addPropagator(new SymmetryDetector.LexGreaterEqual(columns[j], columns[j + 1]));
        }
        numSymmetryConstraints = columns.length - 1;
      }
    }
    if (feasible) {
      loadObjective();
      loadStrategies();
      loadHintsAndAssumptions();
    }
    objCoefOfVar = new long[s.numVars];
    for (int i = 0; i < objVars.length; i++) {
      objCoefOfVar[objVars[i]] += objCoeffs[i];
    }
    s.finishLoading();
  }

  // ---------------------------------------------------------------------------------- helpers

  private static int[] ints(List<Integer> l) {
    int[] r = new int[l.size()];
    for (int i = 0; i < r.length; i++) {
      r[i] = l.get(i);
    }
    return r;
  }

  private static long[] longs(List<Long> l) {
    long[] r = new long[l.size()];
    for (int i = 0; i < r.length; i++) {
      r[i] = l.get(i);
    }
    return r;
  }

  private int trueLiteral() {
    if (trueVar < 0) {
      trueVar = s.newVar(new Domain(1));
    }
    return trueVar;
  }

  static int negated(int lit) {
    return -lit - 1;
  }

  private static int[] negatedAll(int[] lits) {
    int[] r = new int[lits.length];
    for (int i = 0; i < lits.length; i++) {
      r[i] = negated(lits[i]);
    }
    return r;
  }

  private void infeasible() {
    feasible = false;
  }

  /** The current domain bounds of a merged linear expression. */
  private long[] bounds(int[] vars, long[] coeffs, long offset) {
    long min = offset;
    long max = offset;
    for (int i = 0; i < vars.length; i++) {
      long c = coeffs[i];
      if (c >= 0) {
        min += c * s.lb[vars[i]];
        max += c * s.ub[vars[i]];
      } else {
        min += c * s.ub[vars[i]];
        max += c * s.lb[vars[i]];
      }
    }
    return new long[] {min, max};
  }

  /** Merges duplicate variables and drops zero coefficients; returns {vars, coeffs}. */
  private static Object[] merge(int[] vars, long[] coeffs) {
    Map<Integer, Long> m = new LinkedHashMap<>();
    for (int i = 0; i < vars.length; i++) {
      m.merge(vars[i], coeffs[i], Long::sum);
    }
    List<Integer> vs = new ArrayList<>();
    List<Long> cs = new ArrayList<>();
    for (Map.Entry<Integer, Long> e : m.entrySet()) {
      if (e.getValue() != 0) {
        vs.add(e.getKey());
        cs.add(e.getValue());
      }
    }
    return new Object[] {ints(vs), longs(cs)};
  }

  /** Returns an affine view of a linear expression, creating an auxiliary variable if needed. */
  Affine affine(LinearExpressionProto e) {
    int[] vars = new int[e.getVarsCount()];
    long[] coeffs = new long[e.getVarsCount()];
    long offset = e.getOffset();
    for (int i = 0; i < vars.length; i++) {
      int ref = e.getVars(i);
      long c = e.getCoeffs(i);
      if (ref < 0) {
        vars[i] = negated(ref);
        coeffs[i] = -c;
      } else {
        vars[i] = ref;
        coeffs[i] = c;
      }
    }
    Object[] mc = merge(vars, coeffs);
    vars = (int[]) mc[0];
    coeffs = (long[]) mc[1];
    if (vars.length == 0) {
      return Affine.constant(offset);
    }
    if (vars.length == 1) {
      return new Affine(vars[0], coeffs[0], offset);
    }
    long[] b = bounds(vars, coeffs, offset);
    int y = s.newVar(new Domain(b[0], b[1]));
    int[] lv = new int[vars.length + 1];
    long[] lc = new long[vars.length + 1];
    System.arraycopy(vars, 0, lv, 0, vars.length);
    System.arraycopy(coeffs, 0, lc, 0, vars.length);
    lv[vars.length] = y;
    lc[vars.length] = -1;
    addLinear(lv, lc, new Domain(-offset), new int[0]);
    return new Affine(y, 1, 0);
  }

  Affine[] affines(List<LinearExpressionProto> exprs) {
    Affine[] r = new Affine[exprs.size()];
    for (int i = 0; i < r.length; i++) {
      r[i] = affine(exprs.get(i));
    }
    return r;
  }

  static Affine literalView(int lit) {
    return lit >= 0 ? new Affine(lit, 1, 0) : new Affine(negated(lit), -1, 1);
  }

  static Affine refView(int ref) {
    return ref >= 0 ? new Affine(ref, 1, 0) : new Affine(negated(ref), -1, 0);
  }

  /** Adds {@code sum(coeffs * vars) in domain} with enforcement literals. */
  void addLinear(int[] vars, long[] coeffs, Domain domain, int[] enforcement) {
    Object[] mc = merge(vars, coeffs);
    vars = (int[]) mc[0];
    coeffs = (long[]) mc[1];
    if (vars.length == 0) {
      if (!domain.contains(0)) {
        addFalseUnderEnforcement(enforcement);
      }
      return;
    }
    if (vars.length == 1 && enforcement.length == 0) {
      if (!s.intersect(vars[0], domain.inverseMultiplicationBy(coeffs[0]))) {
        infeasible();
      }
      return;
    }
    if (enforcement.length == 0 && !domain.isEmpty()) {
      long lo = domain.min();
      long hi = domain.max();
      rows.add(new LinearRow(vars, coeffs, lo, hi));
    } else if (enforcement.length >= 1 && !domain.isEmpty()) {
      addBigMRows(vars, coeffs, domain.min(), domain.max(), enforcement);
    }
    s.addPropagator(new LinearPropagator(vars, coeffs, domain, enforcement));
  }

  // linear relaxation of an enforced linear constraint: lo - M_lo * (sum(1 - e)) <= expr etc.
  private void addBigMRows(int[] vars, long[] coeffs, long lo, long hi, int[] enforcement) {
    long[] b = bounds(vars, coeffs, 0);
    for (int side = 0; side < 2; side++) {
      long rhs = side == 0 ? hi : lo;
      if (side == 0 && (hi == Long.MAX_VALUE || hi >= b[1])) {
        continue;
      }
      if (side == 1 && (lo == Long.MIN_VALUE || lo <= b[0])) {
        continue;
      }
      // side 0: expr <= hi + M * sum(not e) with M = b[1] - hi
      // side 1: expr >= lo - M * sum(not e) with M = lo - b[0]
      long bigM = side == 0 ? b[1] - rhs : rhs - b[0];
      int n = vars.length + enforcement.length;
      int[] rv = new int[n];
      long[] rc = new long[n];
      System.arraycopy(vars, 0, rv, 0, vars.length);
      System.arraycopy(coeffs, 0, rc, 0, vars.length);
      long constant = 0;
      for (int k = 0; k < enforcement.length; k++) {
        int lit = enforcement[k];
        // not e == 1 - x for a positive literal, == x for a negative literal
        int v = DomainStore.litVar(lit);
        long sign = side == 0 ? -bigM : bigM; // move M * (not e) to the left side
        rv[vars.length + k] = v;
        if (lit >= 0) {
          // M * (1 - x)
          rc[vars.length + k] = -sign;
          constant += sign;
        } else {
          rc[vars.length + k] = sign;
        }
      }
      Object[] mc = merge(rv, rc);
      int[] mv = (int[]) mc[0];
      long[] mcf = (long[]) mc[1];
      if (side == 0) {
        rows.add(new LinearRow(mv, mcf, Long.MIN_VALUE, rhs - constant));
      } else {
        rows.add(new LinearRow(mv, mcf, rhs - constant, Long.MAX_VALUE));
      }
    }
  }

  /** The enforcement literals cannot all be true. */
  private void addFalseUnderEnforcement(int[] enforcement) {
    if (enforcement.length == 0) {
      infeasible();
      return;
    }
    if (enforcement.length == 1) {
      if (!s.setLit(enforcement[0], false)) {
        infeasible();
      }
      return;
    }
    s.addPropagator(new BoolPropagators.Clause(negatedAll(enforcement)));
  }

  private void addClause(int[] lits) {
    if (lits.length == 0) {
      infeasible();
      return;
    }
    if (lits.length == 1) {
      if (!s.setLit(lits[0], true)) {
        infeasible();
      }
      return;
    }
    s.addPropagator(new BoolPropagators.Clause(lits));
    int[] vars = new int[lits.length];
    long[] coeffs = new long[lits.length];
    long constant = 0;
    for (int i = 0; i < lits.length; i++) {
      vars[i] = DomainStore.litVar(lits[i]);
      coeffs[i] = lits[i] >= 0 ? 1 : -1;
      constant += lits[i] >= 0 ? 0 : 1;
    }
    Object[] mc = merge(vars, coeffs);
    rows.add(new LinearRow((int[]) mc[0], (long[]) mc[1], 1 - constant, Long.MAX_VALUE));
  }

  /** sum(lits) in [lo, hi] with enforcement, as a linear constraint. */
  private void addLiteralSum(int[] lits, long lo, long hi, int[] enforcement) {
    int[] vars = new int[lits.length];
    long[] coeffs = new long[lits.length];
    long constant = 0;
    for (int i = 0; i < lits.length; i++) {
      vars[i] = DomainStore.litVar(lits[i]);
      if (lits[i] >= 0) {
        coeffs[i] = 1;
      } else {
        coeffs[i] = -1;
        constant++;
      }
    }
    Domain d = lo == Long.MIN_VALUE ? Domain.lowerOrEqual(hi - constant)
        : new Domain(lo - constant, hi - constant);
    addLinear(vars, coeffs, d, enforcement);
  }

  private void addAtMostOne(int[] lits, boolean exactly, int[] enforcement) {
    if (enforcement.length > 0 || hasDuplicateVariables(lits)) {
      addLiteralSum(lits, exactly ? 1 : Long.MIN_VALUE, 1, enforcement);
      return;
    }
    if (lits.length == 0) {
      if (exactly) {
        infeasible();
      }
      return;
    }
    s.addPropagator(new BoolPropagators.AtMostOne(lits, exactly));
    int[] vars = new int[lits.length];
    long[] coeffs = new long[lits.length];
    long constant = 0;
    for (int i = 0; i < lits.length; i++) {
      vars[i] = DomainStore.litVar(lits[i]);
      coeffs[i] = lits[i] >= 0 ? 1 : -1;
      constant += lits[i] >= 0 ? 0 : 1;
    }
    rows.add(new LinearRow(vars, coeffs, exactly ? 1 - constant : Long.MIN_VALUE, 1 - constant));
  }

  private static boolean hasDuplicateVariables(int[] lits) {
    java.util.HashSet<Integer> seen = new java.util.HashSet<>();
    for (int l : lits) {
      if (!seen.add(DomainStore.litVar(l))) {
        return true;
      }
    }
    return false;
  }

  // ---------------------------------------------------------------------------------- constraints

  private void loadInterval(int index, ConstraintProto ct) {
    IntervalConstraintProto iv = ct.getInterval();
    int[] presence = ints(ct.getEnforcementLiteralList());
    Affine start = affine(iv.getStart());
    Affine size = affine(iv.getSize());
    Affine end = affine(iv.getEnd());
    intervals.put(index, new SchedulingPropagators.Interval(start, size, end, presence));
    // present => start + size == end
    List<Integer> vars = new ArrayList<>();
    List<Long> coeffs = new ArrayList<>();
    long constant = start.offset + size.offset - end.offset;
    if (start.var >= 0) {
      vars.add(start.var);
      coeffs.add(start.coeff);
    }
    if (size.var >= 0) {
      vars.add(size.var);
      coeffs.add(size.coeff);
    }
    if (end.var >= 0) {
      vars.add(end.var);
      coeffs.add(-end.coeff);
    }
    addLinear(ints(vars), longs(coeffs), new Domain(-constant), presence);
    // present => size >= 0
    if (size.var >= 0) {
      addLinear(new int[] {size.var}, new long[] {size.coeff},
          Domain.greaterOrEqual(-size.offset), presence);
    } else if (size.offset < 0) {
      addFalseUnderEnforcement(presence);
    }
  }

  private SchedulingPropagators.Interval[] intervalsOf(List<Integer> indices) {
    SchedulingPropagators.Interval[] r = new SchedulingPropagators.Interval[indices.size()];
    for (int i = 0; i < r.length; i++) {
      r[i] = intervals.get(indices.get(i));
    }
    return r;
  }

  private void load(ConstraintProto ct) {
    int[] enf = ints(ct.getEnforcementLiteralList());
    switch (ct.getConstraintCase()) {
      case BOOL_OR: {
        int[] lits = ints(ct.getBoolOr().getLiteralsList());
        int[] clause = new int[lits.length + enf.length];
        System.arraycopy(lits, 0, clause, 0, lits.length);
        System.arraycopy(negatedAll(enf), 0, clause, lits.length, enf.length);
        addClause(clause);
        break;
      }
      case BOOL_AND: {
        int[] negEnf = negatedAll(enf);
        for (int lit : ct.getBoolAnd().getLiteralsList()) {
          int[] clause = new int[negEnf.length + 1];
          System.arraycopy(negEnf, 0, clause, 0, negEnf.length);
          clause[negEnf.length] = lit;
          addClause(clause);
        }
        break;
      }
      case AT_MOST_ONE:
        addAtMostOne(ints(ct.getAtMostOne().getLiteralsList()), false, enf);
        break;
      case EXACTLY_ONE:
        addAtMostOne(ints(ct.getExactlyOne().getLiteralsList()), true, enf);
        break;
      case BOOL_XOR:
        s.addPropagator(new BoolPropagators.Xor(ints(ct.getBoolXor().getLiteralsList()), enf));
        break;
      case LINEAR: {
        long[] d = longs(ct.getLinear().getDomainList());
        addLinear(ints(ct.getLinear().getVarsList()), longs(ct.getLinear().getCoeffsList()),
            Domain.fromFlatIntervals(d), enf);
        break;
      }
      case LIN_MAX: {
        LinearArgumentProto a = ct.getLinMax();
        Affine target = affine(a.getTarget());
        Affine[] exprs = affines(a.getExprsList());
        s.addPropagator(new IntPropagators.LinMax(target, exprs, enf));
        if (enf.length == 0) {
          // target >= expr for the LP relaxation
          for (Affine e : exprs) {
            addRowDifference(target, e);
          }
        }
        break;
      }
      case INT_PROD:
        loadProduct(ct.getIntProd(), enf);
        break;
      case INT_DIV: {
        LinearArgumentProto a = ct.getIntDiv();
        s.addPropagator(new IntPropagators.Division(affine(a.getTarget()), affine(a.getExprs(0)),
            affine(a.getExprs(1)), enf));
        break;
      }
      case INT_MOD: {
        LinearArgumentProto a = ct.getIntMod();
        s.addPropagator(new IntPropagators.Modulo(affine(a.getTarget()), affine(a.getExprs(0)),
            affine(a.getExprs(1)), enf));
        break;
      }
      case ALL_DIFF:
        s.addPropagator(new IntPropagators.AllDifferent(affines(ct.getAllDiff().getExprsList()),
            enf));
        break;
      case ELEMENT:
        loadElement(ct.getElement(), enf);
        break;
      case CIRCUIT:
        loadCircuit(ints(ct.getCircuit().getTailsList()), ints(ct.getCircuit().getHeadsList()),
            ints(ct.getCircuit().getLiteralsList()), enf, false);
        break;
      case ROUTES:
        loadCircuit(ints(ct.getRoutes().getTailsList()), ints(ct.getRoutes().getHeadsList()),
            ints(ct.getRoutes().getLiteralsList()), enf, true);
        break;
      case TABLE:
        loadTable(ct.getTable(), enf);
        break;
      case AUTOMATON: {
        org.matheclipse.ortools.sat.AutomatonConstraintProto a = ct.getAutomaton();
        Affine[] exprs;
        if (a.getVarsCount() > 0) {
          exprs = new Affine[a.getVarsCount()];
          for (int i = 0; i < exprs.length; i++) {
            exprs[i] = refView(a.getVars(i));
          }
        } else {
          exprs = affines(a.getExprsList());
        }
        s.addPropagator(new IntPropagators.Automaton(exprs, a.getStartingState(),
            longs(a.getFinalStatesList()), longs(a.getTransitionTailList()),
            longs(a.getTransitionHeadList()), longs(a.getTransitionLabelList()), enf));
        break;
      }
      case INVERSE: {
        // the legacy variable fields if f_direct is set, otherwise the expressions (the validator
        // rejects a constraint using both formats)
        org.matheclipse.ortools.sat.InverseConstraintProto inv = ct.getInverse();
        Affine[] f;
        Affine[] g;
        if (inv.getFDirectCount() > 0) {
          f = new Affine[inv.getFDirectCount()];
          g = new Affine[inv.getFInverseCount()];
          for (int i = 0; i < f.length; i++) {
            f[i] = refView(inv.getFDirect(i));
          }
          for (int i = 0; i < g.length; i++) {
            g[i] = refView(inv.getFInverse(i));
          }
        } else {
          f = affines(inv.getFExprDirectList());
          g = affines(inv.getFExprInverseList());
        }
        s.addPropagator(new IntPropagators.Inverse(f, g, enf));
        break;
      }
      case RESERVOIR: {
        org.matheclipse.ortools.sat.ReservoirConstraintProto r = ct.getReservoir();
        int[] actives = ints(r.getActiveLiteralsList());
        if (actives.length == 0) {
          actives = new int[r.getTimeExprsCount()];
          java.util.Arrays.fill(actives, trueLiteral());
        }
        s.addPropagator(new SchedulingPropagators.Reservoir(affines(r.getTimeExprsList()),
            affines(r.getLevelChangesList()), actives, r.getMinLevel(), r.getMaxLevel(), enf));
        break;
      }
      case NO_OVERLAP:
        s.addPropagator(new SchedulingPropagators.NoOverlap(
            intervalsOf(ct.getNoOverlap().getIntervalsList()), enf));
        break;
      case NO_OVERLAP_2D:
        s.addPropagator(new SchedulingPropagators.NoOverlap2D(
            intervalsOf(ct.getNoOverlap2D().getXIntervalsList()),
            intervalsOf(ct.getNoOverlap2D().getYIntervalsList()), enf));
        break;
      case CUMULATIVE: {
        org.matheclipse.ortools.sat.CumulativeConstraintProto cu = ct.getCumulative();
        s.addPropagator(new SchedulingPropagators.Cumulative(intervalsOf(cu.getIntervalsList()),
            affines(cu.getDemandsList()), affine(cu.getCapacity()), enf));
        break;
      }
      default:
        // CONSTRAINT_NOT_SET: always satisfied
        break;
    }
  }

  // target - e >= 0 as an LP row (for lin_max)
  private void addRowDifference(Affine target, Affine e) {
    List<Integer> vars = new ArrayList<>();
    List<Long> coeffs = new ArrayList<>();
    if (target.var >= 0) {
      vars.add(target.var);
      coeffs.add(target.coeff);
    }
    if (e.var >= 0) {
      vars.add(e.var);
      coeffs.add(-e.coeff);
    }
    Object[] mc = merge(ints(vars), longs(coeffs));
    int[] mv = (int[]) mc[0];
    if (mv.length > 0) {
      rows.add(new LinearRow(mv, (long[]) mc[1], e.offset - target.offset, Long.MAX_VALUE));
    }
  }

  private void loadProduct(LinearArgumentProto a, int[] enf) {
    Affine target = affine(a.getTarget());
    Affine[] exprs = affines(a.getExprsList());
    if (exprs.length == 0) {
      equality(target, Affine.constant(1), enf);
      return;
    }
    if (exprs.length == 1) {
      equality(target, exprs[0], enf);
      return;
    }
    Affine left = exprs[0];
    for (int i = 1; i < exprs.length - 1; i++) {
      long[] r = IntPropagators.Product.productRange(left.min(s), left.max(s), exprs[i].min(s),
          exprs[i].max(s));
      int p = s.newVar(new Domain(r[0], r[1]));
      Affine pv = new Affine(p, 1, 0);
      s.addPropagator(new IntPropagators.Product(pv, left, exprs[i], new int[0]));
      left = pv;
    }
    s.addPropagator(new IntPropagators.Product(target, left, exprs[exprs.length - 1], enf));
  }

  // a == b with enforcement
  private void equality(Affine a, Affine b, int[] enf) {
    List<Integer> vars = new ArrayList<>();
    List<Long> coeffs = new ArrayList<>();
    if (a.var >= 0) {
      vars.add(a.var);
      coeffs.add(a.coeff);
    }
    if (b.var >= 0) {
      vars.add(b.var);
      coeffs.add(-b.coeff);
    }
    addLinear(ints(vars), longs(coeffs), new Domain(b.offset - a.offset), enf);
  }

  private void loadElement(ElementConstraintProto el, int[] enf) {
    Affine index;
    Affine target;
    Affine[] exprs;
    if (el.getVarsCount() > 0) {
      index = refView(el.getIndex());
      target = refView(el.getTarget());
      exprs = new Affine[el.getVarsCount()];
      for (int i = 0; i < exprs.length; i++) {
        exprs[i] = refView(el.getVars(i));
      }
    } else {
      index = affine(el.getLinearIndex());
      target = affine(el.getLinearTarget());
      exprs = affines(el.getExprsList());
    }
    s.addPropagator(new IntPropagators.Element(index, exprs, target, enf));
  }

  private void loadTable(TableConstraintProto t, int[] enf) {
    Affine[] cols;
    if (t.getVarsCount() > 0) {
      cols = new Affine[t.getVarsCount()];
      for (int i = 0; i < cols.length; i++) {
        cols[i] = refView(t.getVars(i));
      }
    } else {
      cols = affines(t.getExprsList());
    }
    if (cols.length == 0) {
      return;
    }
    long[] values = longs(t.getValuesList());
    if (values.length == 0) {
      if (!t.getNegated()) {
        addFalseUnderEnforcement(enf);
      }
      return;
    }
    IntPropagators.Table table = new IntPropagators.Table(cols, values, t.getNegated(), enf);
    table.init(s);
    s.addPropagator(table);
  }

  private void loadCircuit(int[] tails, int[] heads, int[] lits, int[] enf, boolean routes) {
    int numNodes = 0;
    for (int i = 0; i < tails.length; i++) {
      numNodes = Math.max(numNodes, Math.max(tails[i], heads[i]) + 1);
    }
    List<List<Integer>> out = new ArrayList<>();
    List<List<Integer>> in = new ArrayList<>();
    for (int v = 0; v < numNodes; v++) {
      out.add(new ArrayList<>());
      in.add(new ArrayList<>());
    }
    boolean[] isNode = new boolean[numNodes];
    for (int i = 0; i < tails.length; i++) {
      out.get(tails[i]).add(lits[i]);
      in.get(heads[i]).add(lits[i]);
      isNode[tails[i]] = true;
      isNode[heads[i]] = true;
    }
    for (int v = 0; v < numNodes; v++) {
      if (!isNode[v]) {
        continue;
      }
      if (routes && v == 0) {
        // number of routes leaving the depot == number of routes entering it
        List<Integer> vars = new ArrayList<>();
        List<Long> coeffs = new ArrayList<>();
        long constant = 0;
        for (int lit : out.get(0)) {
          vars.add(DomainStore.litVar(lit));
          coeffs.add(lit >= 0 ? 1L : -1L);
          constant += lit >= 0 ? 0 : 1;
        }
        for (int lit : in.get(0)) {
          vars.add(DomainStore.litVar(lit));
          coeffs.add(lit >= 0 ? -1L : 1L);
          constant -= lit >= 0 ? 0 : 1;
        }
        addLinear(ints(vars), longs(coeffs), new Domain(-constant), enf);
        continue;
      }
      addAtMostOne(ints(out.get(v)), true, enf);
      addAtMostOne(ints(in.get(v)), true, enf);
    }
    if (routes) {
      s.addPropagator(new GraphPropagators.Routes(tails, heads, lits, enf));
    } else {
      s.addPropagator(new GraphPropagators.Circuit(tails, heads, lits, enf));
    }
  }

  // ---------------------------------------------------------------------------------- objective

  private void loadObjective() {
    if (model.hasObjective()) {
      CpObjectiveProto obj = model.getObjective();
      Object[] mc = merge(ints(obj.getVarsList()), longs(obj.getCoeffsList()));
      objVars = (int[]) mc[0];
      objCoeffs = (long[]) mc[1];
      objOffset = obj.getOffset();
      objScale = obj.getScalingFactor() == 0 ? 1 : obj.getScalingFactor();
      Domain d = obj.getDomainCount() > 0 ? Domain.fromFlatIntervals(longs(obj.getDomainList()))
          : Domain.allValues();
      objective = new LinearPropagator(objVars, objCoeffs, d, new int[0]);
      s.addPropagator(objective);
    } else if (model.hasFloatingPointObjective()) {
      FloatObjectiveProto obj = model.getFloatingPointObjective();
      floatObjective = true;
      floatMaximize = obj.getMaximize();
      floatOffset = obj.getOffset();
      int n = obj.getVarsCount();
      floatCoeffs = new double[n];
      int[] vars = new int[n];
      for (int i = 0; i < n; i++) {
        int ref = obj.getVars(i);
        vars[i] = ref >= 0 ? ref : negated(ref);
        floatCoeffs[i] = ref >= 0 ? obj.getCoeffs(i) : -obj.getCoeffs(i);
      }
      double scale = floatScale(vars, floatCoeffs);
      long[] coeffs = new long[n];
      for (int i = 0; i < n; i++) {
        long c = Math.round(floatCoeffs[i] * scale);
        coeffs[i] = floatMaximize ? -c : c;
      }
      Object[] mc = merge(vars, coeffs);
      objVars = (int[]) mc[0];
      objCoeffs = (long[]) mc[1];
      objScale = (floatMaximize ? -1.0 : 1.0) / scale;
      objOffset = (floatMaximize ? -floatOffset : floatOffset) * scale;
      objective = new LinearPropagator(objVars, objCoeffs, Domain.allValues(), new int[0]);
      s.addPropagator(objective);
      // keep the original variable order for the exact evaluation of the displayed value
      floatVars = vars;
    }
  }

  int[] floatVars;

  // smallest power of 10 (up to 1e9) making all coefficients integral, otherwise a large factor
  private double floatScale(int[] vars, double[] coeffs) {
    double maxAbsTerm = 0;
    double maxAbsCoeff = 0;
    for (int i = 0; i < vars.length; i++) {
      long m = Math.max(Math.abs(s.lb[vars[i]]), Math.abs(s.ub[vars[i]]));
      maxAbsTerm += Math.abs(coeffs[i]) * m;
      maxAbsCoeff = Math.max(maxAbsCoeff, Math.abs(coeffs[i]));
    }
    double limit = (double) (1L << 52);
    double scale = 1;
    for (int k = 0; k <= 9; k++) {
      boolean exact = true;
      for (double c : coeffs) {
        double sc = c * scale;
        if (Math.abs(sc - Math.rint(sc)) > 1e-9 * Math.max(1, Math.abs(sc))) {
          exact = false;
          break;
        }
      }
      if (exact && maxAbsTerm * scale < limit) {
        return scale;
      }
      scale *= 10;
    }
    // approximate scaling
    if (maxAbsTerm == 0) {
      return 1;
    }
    return Math.max(1, Math.floor(limit / (16 * maxAbsTerm)));
  }

  /** The displayed objective value of a solution (values of the model variables). */
  double displayedObjective(long[] values) {
    if (floatObjective) {
      double sum = floatOffset;
      for (int i = 0; i < floatVars.length; i++) {
        sum += floatCoeffs[i] * values[floatVars[i]];
      }
      return sum;
    }
    long inner = innerObjective(values);
    return objScale * (inner + objOffset);
  }

  /** Converts an inner (minimized) objective bound to the displayed scale. */
  double displayedBound(double innerBound) {
    return objScale * (innerBound + objOffset);
  }

  long innerObjective(long[] values) {
    long inner = 0;
    for (int i = 0; i < objVars.length; i++) {
      inner += objCoeffs[i] * values[objVars[i]];
    }
    return inner;
  }

  private void loadStrategies() {
    for (DecisionStrategyProto ds : model.getSearchStrategyList()) {
      Affine[] exprs;
      if (ds.getVariablesCount() > 0) {
        exprs = new Affine[ds.getVariablesCount()];
        for (int i = 0; i < exprs.length; i++) {
          exprs[i] = refView(ds.getVariables(i));
        }
      } else {
        exprs = new Affine[ds.getExprsCount()];
        for (int i = 0; i < exprs.length; i++) {
          LinearExpressionProto e = ds.getExprs(i);
          exprs[i] = e.getVarsCount() == 0 ? Affine.constant(e.getOffset())
              : new Affine(e.getVars(0), e.getCoeffs(0), e.getOffset());
        }
      }
      strategies.add(new Strategy(exprs, ds.getVariableSelectionStrategy(),
          ds.getDomainReductionStrategy()));
    }
  }

  private void loadHintsAndAssumptions() {
    if (model.hasSolutionHint()) {
      hintVars = ints(model.getSolutionHint().getVarsList());
      hintValues = longs(model.getSolutionHint().getValuesList());
    }
    assumptions = ints(model.getAssumptionsList());
  }
}
