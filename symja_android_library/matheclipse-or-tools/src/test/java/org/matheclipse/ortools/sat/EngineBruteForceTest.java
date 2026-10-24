package org.matheclipse.ortools.sat;

import static org.matheclipse.ortools.sat.BruteForceTestSupport.maybeEnforce;
import static org.matheclipse.ortools.sat.BruteForceTestSupport.randomAff;
import static org.matheclipse.ortools.sat.BruteForceTestSupport.randomDomain;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.matheclipse.ortools.sat.BruteForceTestSupport.Aff;
import org.matheclipse.ortools.sat.BruteForceTestSupport.Checker;
import org.matheclipse.ortools.util.Domain;

/**
 * Random small models of every constraint kind, solved by enumeration and by optimization and
 * compared with brute force enumeration against checkers written independently of the engine.
 */
public class EngineBruteForceTest {
  private int verified;
  private int withSolutions;

  private void run(CpModel model, Checker checker, Random r) {
    long count = BruteForceTestSupport.verify(model, checker, r);
    if (count >= 0) {
      verified++;
    }
    if (count > 0) {
      withSolutions++;
    }
  }

  @org.junit.jupiter.api.AfterEach
  public void enoughCases() {
    assertTrue(verified >= 20, "only " + verified + " models verified");
    assertTrue(withSolutions >= 5, "only " + withSolutions + " feasible models");
  }

  private static IntVar[] intVars(CpModel model, Random r, int count, int lo, int hi) {
    IntVar[] vars = new IntVar[count];
    for (int i = 0; i < count; i++) {
      vars[i] = model.newIntVarFromDomain(randomDomain(r, lo, hi), "x" + i);
    }
    return vars;
  }

  private static Literal[] literals(CpModel model, Random r, int count) {
    Literal[] lits = new Literal[count];
    for (int i = 0; i < count; i++) {
      BoolVar b = model.newBoolVar("b" + i);
      lits[i] = r.nextInt(3) == 0 ? b.not() : b;
    }
    return lits;
  }

  private static boolean litValue(Literal l, long[] v) {
    int i = l.getIndex();
    return i >= 0 ? v[i] == 1 : v[-i - 1] == 0;
  }

  @Test
  public void testLinear() {
    linear(new Random(1), 150);
  }

  private void linear(Random r, int iterations) {
    for (int iter = 0; iter < iterations; iter++) {
      CpModel model = new CpModel();
      IntVar[] x = intVars(model, r, 2 + r.nextInt(3), -3, 4);
      List<Checker> checks = new ArrayList<>();
      for (int c = 0; c < 1 + r.nextInt(3); c++) {
        int k = 1 + r.nextInt(x.length);
        LinearExprBuilder e = LinearExpr.newBuilder();
        long[] coeffs = new long[x.length];
        for (int t = 0; t < k; t++) {
          int i = r.nextInt(x.length);
          long co = r.nextInt(7) - 3;
          coeffs[i] += co;
          e.addTerm(x[i], co);
        }
        long off = r.nextInt(5) - 2;
        e.add(off);
        Domain d = randomDomain(r, -8, 8);
        Constraint ct = model.addLinearExpressionInDomain(e, d);
        final long offset = off;
        checks.add(maybeEnforce(r, model, ct, v -> {
          long sum = offset;
          for (int i = 0; i < x.length; i++) {
            sum += coeffs[i] * v[x[i].getIndex()];
          }
          return d.contains(sum);
        }));
      }
      run(model, v -> checks.stream().allMatch(ch -> ch.ok(v)), r);
    }
  }

  @Test
  public void testBooleanConstraints() {
    booleanConstraints(new Random(2), 200);
  }

  private void booleanConstraints(Random r, int iterations) {
    for (int iter = 0; iter < iterations; iter++) {
      CpModel model = new CpModel();
      Literal[] lits = literals(model, r, 3 + r.nextInt(3));
      List<Checker> checks = new ArrayList<>();
      for (int c = 0; c < 1 + r.nextInt(3); c++) {
        List<Literal> args = new ArrayList<>();
        for (int t = 0; t < 1 + r.nextInt(4); t++) {
          Literal l = lits[r.nextInt(lits.length)];
          args.add(r.nextInt(4) == 0 ? l.not() : l);
        }
        int kind = r.nextInt(5);
        Constraint ct;
        Checker inner;
        switch (kind) {
          case 0:
            ct = model.addBoolOr(args);
            inner = v -> args.stream().anyMatch(l -> litValue(l, v));
            break;
          case 1:
            ct = model.addBoolAnd(args);
            inner = v -> args.stream().allMatch(l -> litValue(l, v));
            break;
          case 2:
            ct = model.addAtMostOne(args);
            inner = v -> args.stream().filter(l -> litValue(l, v)).count() <= 1;
            break;
          case 3:
            ct = model.addExactlyOne(args);
            inner = v -> args.stream().filter(l -> litValue(l, v)).count() == 1;
            break;
          default:
            ct = model.addBoolXor(args);
            inner = v -> args.stream().filter(l -> litValue(l, v)).count() % 2 == 1;
            break;
        }
        checks.add(maybeEnforce(r, model, ct, inner));
      }
      if (r.nextBoolean()) {
        model.addImplication(lits[0], lits[1]);
        checks.add(v -> !litValue(lits[0], v) || litValue(lits[1], v));
      }
      run(model, v -> checks.stream().allMatch(ch -> ch.ok(v)), r);
    }
  }

  @Test
  public void testAllDifferent() {
    allDifferent(new Random(3), 150);
  }

  private void allDifferent(Random r, int iterations) {
    for (int iter = 0; iter < iterations; iter++) {
      CpModel model = new CpModel();
      IntVar[] x = intVars(model, r, 3 + r.nextInt(2), 0, 4);
      Aff[] es = new Aff[2 + r.nextInt(3)];
      LinearArgument[] args = new LinearArgument[es.length];
      for (int i = 0; i < es.length; i++) {
        es[i] = randomAff(r, x, true);
        args[i] = es[i].expr;
      }
      Constraint ct = model.addAllDifferent(args);
      Checker check = maybeEnforce(r, model, ct, v -> {
        Set<Long> seen = new HashSet<>();
        for (Aff e : es) {
          if (!seen.add(e.eval(v))) {
            return false;
          }
        }
        return true;
      });
      run(model, check, r);
    }
  }

  @Test
  public void testElement() {
    element(new Random(4), 150);
  }

  private void element(Random r, int iterations) {
    for (int iter = 0; iter < iterations; iter++) {
      CpModel model = new CpModel();
      IntVar[] x = intVars(model, r, 3 + r.nextInt(2), -2, 3);
      Aff index = randomAff(r, x, false);
      Aff target = randomAff(r, x, true);
      Constraint ct;
      Checker inner;
      if (r.nextBoolean()) {
        long[] values = new long[2 + r.nextInt(3)];
        for (int i = 0; i < values.length; i++) {
          values[i] = r.nextInt(7) - 3;
        }
        ct = model.addElement(index.expr, values, target.expr);
        inner = v -> {
          long i = index.eval(v);
          return i >= 0 && i < values.length && values[(int) i] == target.eval(v);
        };
      } else {
        Aff[] es = new Aff[2 + r.nextInt(3)];
        LinearArgument[] args = new LinearArgument[es.length];
        for (int i = 0; i < es.length; i++) {
          es[i] = randomAff(r, x, true);
          args[i] = es[i].expr;
        }
        ct = model.addElement(index.expr, args, target.expr);
        inner = v -> {
          long i = index.eval(v);
          return i >= 0 && i < es.length && es[(int) i].eval(v) == target.eval(v);
        };
      }
      run(model, maybeEnforce(r, model, ct, inner), r);
    }
  }

  @Test
  public void testTable() {
    table(new Random(5), 150);
  }

  private void table(Random r, int iterations) {
    for (int iter = 0; iter < iterations; iter++) {
      CpModel model = new CpModel();
      IntVar[] x = intVars(model, r, 2 + r.nextInt(2), -1, 3);
      int arity = 2 + r.nextInt(2);
      Aff[] cols = new Aff[arity];
      LinearArgument[] args = new LinearArgument[arity];
      for (int i = 0; i < arity; i++) {
        cols[i] = randomAff(r, x, false);
        args[i] = cols[i].expr;
      }
      boolean negated = r.nextBoolean();
      TableConstraint ct =
          negated ? model.addForbiddenAssignments(args) : model.addAllowedAssignments(args);
      Set<List<Long>> tuples = new HashSet<>();
      int numTuples = r.nextInt(8);
      for (int t = 0; t < numTuples; t++) {
        long[] tuple = new long[arity];
        List<Long> key = new ArrayList<>();
        for (int i = 0; i < arity; i++) {
          tuple[i] = r.nextInt(7) - 2;
          key.add(tuple[i]);
        }
        tuples.add(key);
        ct.addTuple(tuple);
      }
      Checker inner = v -> {
        List<Long> key = new ArrayList<>();
        for (Aff c : cols) {
          key.add(c.eval(v));
        }
        return tuples.contains(key) != negated;
      };
      run(model, maybeEnforce(r, model, ct, inner), r);
    }
  }

  @Test
  public void testLinMaxMinAbs() {
    linMaxMinAbs(new Random(6), 150);
  }

  private void linMaxMinAbs(Random r, int iterations) {
    for (int iter = 0; iter < iterations; iter++) {
      CpModel model = new CpModel();
      IntVar[] x = intVars(model, r, 3, -3, 3);
      Aff target = randomAff(r, x, false);
      int kind = r.nextInt(3);
      Constraint ct;
      Checker inner;
      if (kind == 2) {
        Aff e = randomAff(r, x, false);
        ct = model.addAbsEquality(target.expr, e.expr);
        inner = v -> target.eval(v) == Math.abs(e.eval(v));
      } else {
        Aff[] es = new Aff[1 + r.nextInt(3)];
        LinearArgument[] args = new LinearArgument[es.length];
        for (int i = 0; i < es.length; i++) {
          es[i] = randomAff(r, x, true);
          args[i] = es[i].expr;
        }
        boolean max = kind == 0;
        ct = max ? model.addMaxEquality(target.expr, args)
            : model.addMinEquality(target.expr, args);
        inner = v -> {
          long best = max ? Long.MIN_VALUE : Long.MAX_VALUE;
          for (Aff e : es) {
            best = max ? Math.max(best, e.eval(v)) : Math.min(best, e.eval(v));
          }
          return target.eval(v) == best;
        };
      }
      run(model, maybeEnforce(r, model, ct, inner), r);
    }
  }

  @Test
  public void testProductDivisionModulo() {
    productDivisionModulo(new Random(7), 200);
  }

  private void productDivisionModulo(Random r, int iterations) {
    for (int iter = 0; iter < iterations; iter++) {
      CpModel model = new CpModel();
      IntVar[] x = intVars(model, r, 3, -3, 4);
      Aff target = randomAff(r, x, false);
      int kind = r.nextInt(3);
      Constraint ct;
      Checker inner;
      if (kind == 0) {
        Aff[] es = new Aff[1 + r.nextInt(3)];
        LinearArgument[] args = new LinearArgument[es.length];
        for (int i = 0; i < es.length; i++) {
          es[i] = randomAff(r, x, true);
          args[i] = es[i].expr;
        }
        ct = model.addMultiplicationEquality(target.expr, args);
        inner = v -> {
          long p = 1;
          for (Aff e : es) {
            p *= e.eval(v);
          }
          return target.eval(v) == p;
        };
      } else {
        Aff num = randomAff(r, x, true);
        // a denominator which cannot be zero
        IntVar dv = model.newIntVarFromDomain(
            r.nextBoolean() ? Domain.fromValues(new long[] {-2, -1, 1, 3}) : new Domain(1, 3),
            "d");
        Aff den = new Aff(dv, 1, 0);
        if (kind == 1) {
          ct = model.addDivisionEquality(target.expr, num.expr, den.expr);
          inner = v -> target.eval(v) == num.eval(v) / den.eval(v);
        } else {
          IntVar mv = model.newIntVar(1, 3, "m");
          Aff mod = new Aff(mv, 1, 0);
          ct = model.addModuloEquality(target.expr, num.expr, mod.expr);
          inner = v -> target.eval(v) == num.eval(v) % mod.eval(v);
        }
      }
      run(model, maybeEnforce(r, model, ct, inner), r);
    }
  }

  /** An interval with start, size, end expressions and an optional presence literal. */
  private static final class Iv {
    IntervalVar interval;
    IntVar start;
    IntVar size;
    long fixedSize = -1;
    BoolVar presence;
    IntVar endVar;

    /** A present interval satisfies start + size == end. */
    boolean consistent(long[] v) {
      return !present(v) || endVar == null || v[endVar.getIndex()] == start(v) + size(v);
    }

    long start(long[] v) {
      return v[start.getIndex()];
    }

    long size(long[] v) {
      return fixedSize >= 0 ? fixedSize : v[size.getIndex()];
    }

    long end(long[] v) {
      return start(v) + size(v);
    }

    boolean present(long[] v) {
      return presence == null || v[presence.getIndex()] == 1;
    }
  }

  private static Iv randomInterval(CpModel model, Random r, int horizon, int maxSize) {
    Iv iv = new Iv();
    iv.start = model.newIntVar(0, horizon, "s");
    boolean optional = r.nextInt(3) == 0;
    if (optional) {
      iv.presence = model.newBoolVar("p");
    }
    if (r.nextBoolean()) {
      iv.fixedSize = r.nextInt(maxSize + 1);
      iv.interval = optional
          ? model.newOptionalFixedSizeIntervalVar(iv.start, iv.fixedSize, iv.presence, "i")
          : model.newFixedSizeIntervalVar(iv.start, iv.fixedSize, "i");
    } else {
      iv.size = model.newIntVar(0, maxSize, "d");
      IntVar end = model.newIntVar(0, horizon + maxSize, "e");
      iv.endVar = end;
      iv.interval = optional
          ? model.newOptionalIntervalVar(iv.start, iv.size, end, iv.presence, "i")
          : model.newIntervalVar(iv.start, iv.size, end, "i");
    }
    return iv;
  }

  @Test
  public void testNoOverlap() {
    noOverlap(new Random(8), 100);
  }

  private void noOverlap(Random r, int iterations) {
    for (int iter = 0; iter < iterations; iter++) {
      CpModel model = new CpModel();
      Iv[] ivs = new Iv[2 + r.nextInt(2)];
      IntervalVar[] arr = new IntervalVar[ivs.length];
      for (int i = 0; i < ivs.length; i++) {
        ivs[i] = randomInterval(model, r, 4, 2);
        arr[i] = ivs[i].interval;
      }
      Constraint ct = model.addNoOverlap(arr);
      Checker inner = v -> {
        for (int i = 0; i < ivs.length; i++) {
          for (int j = i + 1; j < ivs.length; j++) {
            if (!ivs[i].present(v) || !ivs[j].present(v)) {
              continue;
            }
            if (!(ivs[i].end(v) <= ivs[j].start(v) || ivs[j].end(v) <= ivs[i].start(v))) {
              return false;
            }
          }
        }
        return true;
      };
      Checker sched = maybeEnforce(r, model, ct, inner);
      run(model, v -> sched.ok(v) && java.util.Arrays.stream(ivs).allMatch(iv -> iv.consistent(v)), r);
    }
  }

  // two intervals [a1, a2) and [b1, b2) overlap; a point overlaps a positive interval strictly
  // inside it, two points never overlap
  private static boolean overlap1d(long a1, long a2, long b1, long b2) {
    if (a1 == a2 && b1 == b2) {
      return false;
    }
    if (a1 == a2) {
      return b1 < a1 && a1 < b2;
    }
    if (b1 == b2) {
      return a1 < b1 && b1 < a2;
    }
    return a1 < b2 && b1 < a2;
  }

  @Test
  public void testNoOverlap2D() {
    noOverlap2D(new Random(9), 100);
  }

  private void noOverlap2D(Random r, int iterations) {
    for (int iter = 0; iter < iterations; iter++) {
      CpModel model = new CpModel();
      int n = 2 + r.nextInt(2);
      Iv[] xs = new Iv[n];
      Iv[] ys = new Iv[n];
      NoOverlap2dConstraint ct = model.addNoOverlap2D();
      for (int i = 0; i < n; i++) {
        xs[i] = randomInterval(model, r, 2, 2);
        ys[i] = randomInterval(model, r, 2, 2);
        ct.addRectangle(xs[i].interval, ys[i].interval);
      }
      Checker inner = v -> {
        for (int i = 0; i < n; i++) {
          for (int j = i + 1; j < n; j++) {
            if (!xs[i].present(v) || !ys[i].present(v) || !xs[j].present(v)
                || !ys[j].present(v)) {
              continue;
            }
            if (overlap1d(xs[i].start(v), xs[i].end(v), xs[j].start(v), xs[j].end(v))
                && overlap1d(ys[i].start(v), ys[i].end(v), ys[j].start(v), ys[j].end(v))) {
              return false;
            }
          }
        }
        return true;
      };
      Checker sched = maybeEnforce(r, model, ct, inner);
      run(model, v -> sched.ok(v) && java.util.Arrays.stream(xs).allMatch(iv -> iv.consistent(v)) && java.util.Arrays.stream(ys).allMatch(iv -> iv.consistent(v)), r);
    }
  }

  @Test
  public void testCumulative() {
    cumulative(new Random(10), 60);
  }

  private void cumulative(Random r, int iterations) {
    for (int iter = 0; iter < iterations; iter++) {
      CpModel model = new CpModel();
      int n = 2 + r.nextInt(2);
      Iv[] ivs = new Iv[n];
      Aff[] demands = new Aff[n];
      IntVar capVar = model.newIntVar(-1, 3, "cap");
      Aff capacity = r.nextBoolean() ? new Aff(capVar, 1, 0) : new Aff(null, 0, 1 + r.nextInt(2));
      CumulativeConstraint ct = model.addCumulative(capacity.expr);
      for (int i = 0; i < n; i++) {
        ivs[i] = randomInterval(model, r, 3, 2);
        if (r.nextBoolean()) {
          demands[i] = new Aff(null, 0, r.nextInt(3));
        } else {
          demands[i] = new Aff(model.newIntVar(0, 2, "dem"), 1, 0);
        }
        ct.addDemand(ivs[i].interval, demands[i].expr);
      }
      Checker inner = v -> {
        long cap = capacity.eval(v);
        if (cap < 0) {
          return false;
        }
        for (long t = -1; t <= 6; t++) {
          long load = 0;
          for (int i = 0; i < n; i++) {
            if (ivs[i].present(v) && ivs[i].start(v) <= t && t < ivs[i].end(v)) {
              load += demands[i].eval(v);
            }
          }
          if (load > cap) {
            return false;
          }
        }
        return true;
      };
      Checker sched = maybeEnforce(r, model, ct, inner);
      run(model, v -> sched.ok(v) && java.util.Arrays.stream(ivs).allMatch(iv -> iv.consistent(v)), r);
    }
  }

  @Test
  public void testCircuit() {
    circuit(new Random(11), 80);
  }

  private void circuit(Random r, int iterations) {
    for (int iter = 0; iter < iterations; iter++) {
      CpModel model = new CpModel();
      int n = 2 + r.nextInt(3);
      CircuitConstraint ct = model.addCircuit();
      List<int[]> arcs = new ArrayList<>();
      List<Literal> lits = new ArrayList<>();
      for (int t = 0; t < n; t++) {
        for (int h = 0; h < n; h++) {
          if (r.nextInt(4) == 0) {
            continue;
          }
          BoolVar l = model.newBoolVar("a");
          arcs.add(new int[] {t, h});
          lits.add(l);
          ct.addArc(t, h, l);
        }
      }
      Checker inner = v -> {
        Set<Integer> nodes = new HashSet<>();
        Map<Integer, Integer> next = new HashMap<>();
        Map<Integer, Integer> inCount = new HashMap<>();
        for (int k = 0; k < arcs.size(); k++) {
          nodes.add(arcs.get(k)[0]);
          nodes.add(arcs.get(k)[1]);
          if (litValue(lits.get(k), v)) {
            if (next.containsKey(arcs.get(k)[0])) {
              return false;
            }
            next.put(arcs.get(k)[0], arcs.get(k)[1]);
            inCount.merge(arcs.get(k)[1], 1, Integer::sum);
          }
        }
        Set<Integer> inCycle = new HashSet<>();
        for (int node : nodes) {
          if (!next.containsKey(node) || inCount.getOrDefault(node, 0) != 1) {
            return false;
          }
          if (next.get(node) != node) {
            inCycle.add(node);
          }
        }
        if (inCycle.isEmpty()) {
          return true;
        }
        int start = inCycle.iterator().next();
        int cur = start;
        int steps = 0;
        do {
          cur = next.get(cur);
          steps++;
        } while (cur != start && steps <= nodes.size());
        return cur == start && steps == inCycle.size();
      };
      if (BruteForceTestSupport.searchSpace(model) > 300000) {
        continue;
      }
      run(model, maybeEnforce(r, model, ct, inner), r);
    }
  }

  @Test
  public void testRoutes() {
    routes(new Random(12), 60);
  }

  private void routes(Random r, int iterations) {
    for (int iter = 0; iter < iterations; iter++) {
      CpModel model = new CpModel();
      int n = 2 + r.nextInt(3);
      MultipleCircuitConstraint ct = model.addMultipleCircuit();
      List<int[]> arcs = new ArrayList<>();
      List<Literal> lits = new ArrayList<>();
      for (int t = 0; t < n; t++) {
        for (int h = 0; h < n; h++) {
          if ((t == 0 && h == 0) || (t != h && r.nextInt(4) == 0)) {
            continue;
          }
          BoolVar l = model.newBoolVar("a");
          arcs.add(new int[] {t, h});
          lits.add(l);
          ct.addArc(t, h, l);
        }
      }
      Checker inner = v -> {
        int[] out = new int[n];
        int[] in = new int[n];
        int[] next = new int[n];
        java.util.Arrays.fill(next, -1);
        for (int k = 0; k < arcs.size(); k++) {
          if (litValue(lits.get(k), v)) {
            int t = arcs.get(k)[0];
            int h = arcs.get(k)[1];
            out[t]++;
            in[h]++;
            if (t != 0) {
              next[t] = h;
            }
          }
        }
        if (in[0] != out[0]) {
          return false;
        }
        for (int node = 1; node < n; node++) {
          if (in[node] != 1 || out[node] != 1) {
            return false;
          }
        }
        // no cycle avoiding the depot: from every node the depot (or a self loop) is reached
        for (int node = 1; node < n; node++) {
          if (next[node] == node) {
            continue;
          }
          int cur = node;
          int steps = 0;
          while (cur != 0 && steps <= n) {
            cur = next[cur];
            steps++;
          }
          if (cur != 0) {
            return false;
          }
        }
        return true;
      };
      if (BruteForceTestSupport.searchSpace(model) > 300000 || !model.validate().isEmpty()) {
        continue;
      }
      run(model, maybeEnforce(r, model, ct, inner), r);
    }
  }

  @Test
  public void testAutomaton() {
    automaton(new Random(13), 100);
  }

  private void automaton(Random r, int iterations) {
    for (int iter = 0; iter < iterations; iter++) {
      CpModel model = new CpModel();
      IntVar[] x = intVars(model, r, 3, 0, 2);
      Aff[] es = new Aff[2 + r.nextInt(2)];
      LinearArgument[] args = new LinearArgument[es.length];
      for (int i = 0; i < es.length; i++) {
        es[i] = randomAff(r, x, false);
        args[i] = es[i].expr;
      }
      int numStates = 2 + r.nextInt(2);
      long[] finals = {r.nextInt(numStates)};
      AutomatonConstraint ct = model.addAutomaton(args, 0, finals);
      Map<List<Long>, Long> delta = new HashMap<>();
      for (int q = 0; q < numStates; q++) {
        for (long label = -1; label <= 3; label++) {
          if (r.nextInt(2) == 0) {
            long head = r.nextInt(numStates);
            delta.put(java.util.Arrays.asList((long) q, label), head);
            ct.addTransition(q, (int) head, label);
          }
        }
      }
      Checker inner = v -> {
        long q = 0;
        for (Aff e : es) {
          Long next = delta.get(java.util.Arrays.asList(q, e.eval(v)));
          if (next == null) {
            return false;
          }
          q = next;
        }
        return q == finals[0];
      };
      run(model, maybeEnforce(r, model, ct, inner), r);
    }
  }

  @Test
  public void testReservoir() {
    reservoir(new Random(14), 80);
  }

  private void reservoir(Random r, int iterations) {
    for (int iter = 0; iter < iterations; iter++) {
      CpModel model = new CpModel();
      long minLevel = -r.nextInt(3);
      long maxLevel = r.nextInt(3);
      ReservoirConstraint ct = model.addReservoirConstraint(minLevel, maxLevel);
      int n = 2 + r.nextInt(2);
      IntVar[] times = new IntVar[n];
      long[] changes = new long[n];
      Literal[] actives = new Literal[n];
      for (int i = 0; i < n; i++) {
        times[i] = model.newIntVar(0, 3, "t");
        changes[i] = r.nextInt(5) - 2;
        if (r.nextBoolean()) {
          // also negated active literals (an upstream checker bug fixed in 10.0 treated them as
          // inactive when true)
          BoolVar a = model.newBoolVar("a");
          actives[i] = r.nextInt(3) == 0 ? a.not() : a;
          ct.addOptionalEvent(LinearExpr.affine(times[i], 1, 0), changes[i], actives[i]);
        } else {
          ct.addEvent(times[i], changes[i]);
        }
      }
      Checker inner = v -> {
        for (long t = 0; t <= 3; t++) {
          long level = 0;
          for (int i = 0; i < n; i++) {
            boolean active = actives[i] == null || litValue(actives[i], v);
            if (active && v[times[i].getIndex()] <= t) {
              level += changes[i];
            }
          }
          if (level < minLevel || level > maxLevel) {
            return false;
          }
        }
        return true;
      };
      run(model, maybeEnforce(r, model, ct, inner), r);
    }
  }

  @Test
  public void testInverse() {
    inverse(new Random(15), 60);
  }

  private void inverse(Random r, int iterations) {
    for (int iter = 0; iter < iterations; iter++) {
      CpModel model = new CpModel();
      int n = 2 + r.nextInt(2);
      IntVar[] f = new IntVar[n];
      IntVar[] g = new IntVar[n];
      for (int i = 0; i < n; i++) {
        f[i] = model.newIntVarFromDomain(randomDomain(r, -1, n), "f");
        g[i] = model.newIntVarFromDomain(randomDomain(r, -1, n), "g");
      }
      Constraint ct = model.addInverse(f, g);
      Checker inner = v -> {
        for (int i = 0; i < n; i++) {
          long j = v[f[i].getIndex()];
          if (j < 0 || j >= n || v[g[(int) j].getIndex()] != i) {
            return false;
          }
        }
        for (int j = 0; j < n; j++) {
          long i = v[g[j].getIndex()];
          if (i < 0 || i >= n || v[f[(int) i].getIndex()] != j) {
            return false;
          }
        }
        return true;
      };
      run(model, maybeEnforce(r, model, ct, inner), r);
    }
  }

  @Test
  public void testInverseExpressions() {
    inverseExpressions(new Random(115), 80);
  }

  /** A random expression: constant, affine view or sum of two affine views. */
  private static LinearArgument randomInverseExpr(Random r, IntVar[] vars, List<long[]> terms,
      long[] offset) {
    int kind = r.nextInt(6);
    LinearExprBuilder b = LinearExpr.newBuilder();
    long off;
    if (kind == 0) {
      off = r.nextInt(3);
      offset[0] = off;
      return LinearExpr.constant(off);
    }
    long[] coeffs = {-1, 1, 1, 2};
    int nt = kind == 5 ? 2 : 1;
    off = r.nextInt(5) - 2;
    for (int t = 0; t < nt; t++) {
      int var = r.nextInt(vars.length);
      long c = coeffs[r.nextInt(coeffs.length)];
      b.addTerm(vars[var], c);
      terms.add(new long[] {vars[var].getIndex(), c});
    }
    b.add(off);
    offset[0] = off;
    return b;
  }

  private void inverseExpressions(Random r, int iterations) {
    for (int iter = 0; iter < iterations; iter++) {
      CpModel model = new CpModel();
      int n = 2 + r.nextInt(2);
      IntVar[] vars = intVars(model, r, 2 + r.nextInt(3), -2, 4);
      List<List<long[]>> fTerms = new ArrayList<>();
      List<List<long[]>> gTerms = new ArrayList<>();
      long[] fOff = new long[n];
      long[] gOff = new long[n];
      Constraint ct = new Constraint(model.getBuilder());
      org.matheclipse.ortools.sat.InverseConstraintProto.Builder inv =
          ct.getBuilder().getInverseBuilder();
      for (int side = 0; side < 2; side++) {
        for (int i = 0; i < n; i++) {
          List<long[]> terms = new ArrayList<>();
          long[] off = new long[1];
          LinearArgument e = randomInverseExpr(r, vars, terms, off);
          LinearExpressionProto.Builder proto =
              model.getLinearExpressionProtoBuilderFromLinearArgument(e, false);
          if (side == 0) {
            inv.addFExprDirect(proto);
            fTerms.add(terms);
            fOff[i] = off[0];
          } else {
            inv.addFExprInverse(proto);
            gTerms.add(terms);
            gOff[i] = off[0];
          }
        }
      }
      Checker inner = v -> {
        long[] fv = new long[n];
        long[] gv = new long[n];
        for (int i = 0; i < n; i++) {
          fv[i] = fOff[i];
          for (long[] t : fTerms.get(i)) {
            fv[i] += t[1] * v[(int) t[0]];
          }
          gv[i] = gOff[i];
          for (long[] t : gTerms.get(i)) {
            gv[i] += t[1] * v[(int) t[0]];
          }
        }
        for (int i = 0; i < n; i++) {
          if (fv[i] < 0 || fv[i] >= n || gv[(int) fv[i]] != i) {
            return false;
          }
          if (gv[i] < 0 || gv[i] >= n || fv[(int) gv[i]] != i) {
            return false;
          }
        }
        return true;
      };
      run(model, maybeEnforce(r, model, ct, inner), r);
    }
  }

  @Test
  public void testMixed() {
    mixed(new Random(16), 100);
  }

  private void mixed(Random r, int iterations) {
    for (int iter = 0; iter < iterations; iter++) {
      CpModel model = new CpModel();
      IntVar[] x = intVars(model, r, 3 + r.nextInt(2), -2, 3);
      List<Checker> checks = new ArrayList<>();
      // all different + linear + max
      Aff a = randomAff(r, x, false);
      Aff b = randomAff(r, x, false);
      model.addAllDifferent(new LinearArgument[] {a.expr, b.expr});
      checks.add(v -> a.eval(v) != b.eval(v));
      long rhs = r.nextInt(5) - 2;
      model.addLessOrEqual(LinearExpr.sum(new IntVar[] {x[0], x[1]}), rhs);
      checks.add(v -> v[x[0].getIndex()] + v[x[1].getIndex()] <= rhs);
      Aff t = randomAff(r, x, false);
      Aff c = randomAff(r, x, true);
      model.addMaxEquality(t.expr, new LinearArgument[] {a.expr, c.expr});
      checks.add(v -> t.eval(v) == Math.max(a.eval(v), c.eval(v)));
      run(model, v -> checks.stream().allMatch(ch -> ch.ok(v)), r);
    }
  }

  /** More and other random models of every kind. */
  @Test
  @org.junit.jupiter.api.Tag("slow")
  public void testManyRandomModels() {
    for (long seed = 1000; seed < 1003; seed++) {
      linear(new Random(seed + 1), 4 * 150);
      booleanConstraints(new Random(seed + 2), 4 * 200);
      allDifferent(new Random(seed + 3), 4 * 150);
      element(new Random(seed + 4), 4 * 150);
      table(new Random(seed + 5), 4 * 150);
      linMaxMinAbs(new Random(seed + 6), 4 * 150);
      productDivisionModulo(new Random(seed + 7), 4 * 200);
      noOverlap(new Random(seed + 8), 4 * 100);
      noOverlap2D(new Random(seed + 9), 4 * 60);
      cumulative(new Random(seed + 10), 4 * 80);
      circuit(new Random(seed + 11), 4 * 80);
      routes(new Random(seed + 12), 4 * 60);
      automaton(new Random(seed + 13), 4 * 100);
      reservoir(new Random(seed + 14), 4 * 80);
      inverse(new Random(seed + 15), 4 * 60);
      mixed(new Random(seed + 16), 4 * 100);
      inverseExpressions(new Random(seed + 17), 4 * 80);
    }
  }

  /** The same models with all domains in the bounds-only representation (no value removal). */
  @Test
  public void testWithoutBitsets() {
    long old = org.matheclipse.ortools.sat.engine.EngineTestHooks.setBitsetLimit(0);
    try {
      linear(new Random(101), 40);
      booleanConstraints(new Random(102), 30);
      allDifferent(new Random(103), 30);
      element(new Random(104), 30);
      table(new Random(105), 40);
      linMaxMinAbs(new Random(106), 40);
      productDivisionModulo(new Random(107), 40);
      noOverlap(new Random(108), 30);
      cumulative(new Random(110), 12);
      automaton(new Random(113), 40);
      reservoir(new Random(114), 40);
      inverse(new Random(115), 40);
      inverseExpressions(new Random(116), 40);
    } finally {
      org.matheclipse.ortools.sat.engine.EngineTestHooks.setBitsetLimit(old);
    }
  }

  /**
   * Bin packing models with identical bins (where the engine breaks the symmetry of the bins) and
   * with slightly different bins (where it must not), compared with brute force.
   */
  @Test
  @SuppressFBWarnings(value = "DMI_RANDOM_USED_ONLY_ONCE",
      justification = "seeded generator reused in the loop")
  public void testIdenticalBinsOptimization() {
    Random r = new Random(17);
    for (int iter = 0; iter < 60; iter++) {
      int n = 3 + r.nextInt(4);
      int k = 2 + r.nextInt(2);
      long[] w = new long[n];
      long[] val = new long[n];
      for (int i = 0; i < n; i++) {
        w[i] = 1 + r.nextInt(6);
        val[i] = r.nextInt(9) - 1;
      }
      long[] cap = new long[k];
      long c = 3 + r.nextInt(6);
      boolean identical = r.nextInt(3) > 0;
      for (int b = 0; b < k; b++) {
        cap[b] = identical ? c : c + b;
      }
      boolean exactly = r.nextInt(4) == 0;
      CpModel model = new CpModel();
      BoolVar[][] x = new BoolVar[n][k];
      for (int i = 0; i < n; i++) {
        for (int b = 0; b < k; b++) {
          x[i][b] = model.newBoolVar("x");
        }
        if (exactly) {
          model.addExactlyOne(x[i]);
        } else {
          model.addAtMostOne(x[i]);
        }
      }
      for (int b = 0; b < k; b++) {
        LinearExprBuilder load = LinearExpr.newBuilder();
        for (int i = 0; i < n; i++) {
          load.addTerm(x[i][b], w[i]);
        }
        model.addLessOrEqual(load, cap[b]);
      }
      LinearExprBuilder obj = LinearExpr.newBuilder();
      for (int i = 0; i < n; i++) {
        for (int b = 0; b < k; b++) {
          obj.addTerm(x[i][b], val[i]);
        }
      }
      model.maximize(obj);
      // brute force over item -> bin (or no bin)
      long best = Long.MIN_VALUE;
      int[] choice = new int[n];
      while (true) {
        long[] load = new long[k];
        long v = 0;
        boolean ok = true;
        for (int i = 0; i < n; i++) {
          if (choice[i] < k) {
            load[choice[i]] += w[i];
            v += val[i];
          } else if (exactly) {
            ok = false;
          }
        }
        for (int b = 0; b < k; b++) {
          ok &= load[b] <= cap[b];
        }
        if (ok) {
          best = Math.max(best, v);
        }
        int t = 0;
        while (t < n && ++choice[t] > k) {
          choice[t] = 0;
          t++;
        }
        if (t == n) {
          break;
        }
      }
      int lex = new org.matheclipse.ortools.sat.engine.CpSatEngine(model.model(),
          SatParameters.getDefaultInstance()).numSymmetryBreakingConstraints();
      org.junit.jupiter.api.Assertions.assertEquals(identical ? k - 1 : 0, lex);
      CpSolver solver = new CpSolver();
      CpSolverStatus status = solver.solve(model);
      if (best == Long.MIN_VALUE) {
        org.junit.jupiter.api.Assertions.assertEquals(CpSolverStatus.INFEASIBLE, status);
      } else {
        org.junit.jupiter.api.Assertions.assertEquals(CpSolverStatus.OPTIMAL, status);
        org.junit.jupiter.api.Assertions.assertEquals((double) best, solver.objectiveValue(), 0);
      }
      verified++;
      withSolutions++;
    }
  }
}
