package org.matheclipse.core.reflection.system;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;

/**
 * <pre>
 * TuringMachine(rule, init, t)
 * </pre>
 *
 * <blockquote>
 * <p>
 * the evolution of a Turing machine for <code>t</code> steps, as a list of
 * <code>{{state, position, offset}, tape}</code> for the steps <code>0, 1, ..., t</code>.
 * <code>position</code> is the head's cell in the reported tape and <code>offset</code> how far the
 * head has moved from its starting cell.
 * </p>
 * </blockquote>
 *
 * <p>
 * The rule is a rule number <code>n</code> (two states and two colors), <code>{n, s}</code> (two
 * colors) or <code>{n, s, k}</code>, or a list of rules <code>{state, color} -&gt; {state', color',
 * offset}</code>. The digits of <code>n</code> in base <code>2 s k</code>, least significant first,
 * are the transitions for <code>{s, 0}, {s, 1}, ..., {s, k-1}, {s-1, 0}, ..., {1, k-1}</code>; a
 * digit <code>d</code> writes the color <code>Mod(Quotient(d, 2), k)</code>, goes to the state
 * <code>Quotient(d, 2 k) + 1</code> and moves right for odd and left for even <code>d</code>.
 * Without a matching transition the machine stays where it is.
 *
 * <p>
 * The initial condition is <code>{state, tape}</code>, where the state may be given as
 * <code>{state}</code> or <code>{state, position}</code>. A plain list of cells is a cyclic tape;
 * <code>{cells, background}</code> is an infinite tape with the cells to the right of the head's
 * start (counted from <code>position</code>) on a constant or periodic background, and the reported
 * tape is the region the head visited together with the given cells.
 *
 * <p>
 * <code>TuringMachine(rule, init)</code> and the operator form <code>TuringMachine(rule)(init)</code>
 * make one step and return just the new <code>{{state, position, offset}, tape}</code>.
 */
public class TuringMachine extends AbstractFunctionEvaluator {

  /** The transition table of a numbered machine, or the explicit rules of a machine. */
  private static final class MachineRule {
    /** The explicit rules, or {@link F#NIL} for a numbered machine. */
    final IExpr rules;
    final int states;
    final int colors;
    /** Per transition: new state, new color, offset. */
    final int[][] transitions;

    MachineRule(IExpr rules) {
      this.rules = rules;
      this.states = 0;
      this.colors = 0;
      this.transitions = null;
    }

    MachineRule(BigInteger number, int states, int colors) {
      this.rules = F.NIL;
      this.states = states;
      this.colors = colors;
      final int count = states * colors;
      final BigInteger base = BigInteger.valueOf(2L * count);
      this.transitions = new int[count][];
      BigInteger n = number;
      for (int i = 0; i < count; i++) {
        BigInteger[] qr = n.divideAndRemainder(base);
        int digit = qr[1].intValue();
        n = qr[0];
        transitions[i] = new int[] {(digit / (2 * colors)) % states + 1, (digit / 2) % colors,
            (digit & 1) == 1 ? 1 : -1};
      }
    }

    /**
     * The transition for <code>state</code> reading <code>color</code> as <code>{state', color',
     * offset}</code> with a machine integer offset, or {@link F#NIL} if no rule applies.
     */
    IAST apply(IExpr state, IExpr color, EvalEngine engine) {
      if (rules.isPresent()) {
        IExpr result = engine.evaluate(F.Replace(F.list(state, color), rules));
        if (result.isList3()) {
          int offset = ((IAST) result).arg3().toIntDefault();
          if (F.isPresent(offset)) {
            return (IAST) result;
          }
        }
        return F.NIL;
      }
      int s = state.toIntDefault();
      int c = color.toIntDefault();
      if (!state.isInteger() || !color.isInteger() || s < 1 || s > states || c < 0
          || c >= colors) {
        return F.NIL;
      }
      int[] t = transitions[(states - s) * colors + c];
      return F.list(F.ZZ(t[0]), F.ZZ(t[1]), F.ZZ(t[2]));
    }
  }

  /** The time specification `1` must be an integer &gt;= 0. */
  private static final String TSPEC = "The time specification `1` must be an integer >= 0.";

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.argSize() == 2) {
      // also the operator form TuringMachine(rule)(init)
      return oneStep(ast.arg1(), ast.arg2(), engine);
    }
    if (ast.argSize() != 3) {
      return F.NIL;
    }
    int steps = ast.arg3().isInteger() ? ast.arg3().toIntDefault() : -1;
    if (steps < 0) {
      // The time specification `1` must be an integer >= 0.
      return Errors.printMessage(S.TuringMachine, "tspec", F.list(ast.arg3()), engine);
    }
    return run(ast.arg1(), ast.arg2(), steps, engine);
  }

  /**
   * One step of the machine: the last row of a one step evolution, with the tape written as
   * <code>{cells, background}</code> again for an infinite tape.
   */
  private static IExpr oneStep(IExpr rule, IExpr init, EvalEngine engine) {
    IExpr evolution = run(rule, init, 1, engine);
    if (!evolution.isList2()) {
      return F.NIL;
    }
    IExpr last = evolution.second();
    IExpr background = background(init);
    if (background.isPresent()) {
      return F.list(last.first(), F.list(last.second(), background));
    }
    return last;
  }

  private static IExpr run(IExpr ruleSpec, IExpr init, int steps, EvalEngine engine) {
    MachineRule rule = machineRule(ruleSpec);
    if (rule == null || !init.isList2()) {
      return F.NIL;
    }
    // the state and the head's 1-based start position, which may lie off the given cells
    IExpr stateSpec = init.first();
    IExpr state = stateSpec;
    int start = 1;
    if (stateSpec.isList1()) {
      state = stateSpec.first();
    } else if (stateSpec.isList2() && stateSpec.second().isInteger()) {
      state = stateSpec.first();
      start = stateSpec.second().toIntDefault();
      if (F.isNotPresent(start)) {
        return F.NIL;
      }
    }
    IExpr tape = init.second();
    if (background(init).isPresent()) {
      return runInfinite(rule, state, start, (IAST) tape.first(), tape.second(), steps, engine);
    }
    if (!tape.isList() || tape.argSize() == 0) {
      return F.NIL;
    }
    return runCyclic(rule, state, start, (IAST) tape, steps, engine);
  }

  /** A cyclic tape: the head wraps round at either end. */
  private static IExpr runCyclic(MachineRule rule, IExpr state, int start, IAST cells, int steps,
      EvalEngine engine) {
    final int length = cells.argSize();
    checkSize((steps + 1L) * (length + 4L));
    IASTAppendable tape = cells.copyAppendable();
    int position = Math.floorMod(start - 1, length) + 1;
    long offset = 0;
    IASTAppendable result = F.ListAlloc(steps + 1);
    result.append(row(state, position, offset, tape.copy()));
    for (int i = 0; i < steps; i++) {
      engine.checkTimeLimit();
      IAST transition = rule.apply(state, tape.get(position), engine);
      if (transition.isPresent()) {
        int move = transition.arg3().toIntDefault();
        tape.set(position, transition.arg2());
        state = transition.arg1();
        offset += move;
        position = (int) Math.floorMod(position - 1L + move, length) + 1;
      }
      result.append(row(state, position, offset, tape.copy()));
    }
    return result;
  }

  /**
   * An infinite tape. Cells are addressed by their offset from the head's starting cell; the
   * reported window spans every offset the head reached and the given cells.
   */
  private static IExpr runInfinite(MachineRule rule, IExpr state, int start, IAST cells,
      IExpr background, int steps, EvalEngine engine) {
    final IAST pattern = background.isList() ? (IAST) background : F.list(background);
    // the index in cells of the cell at offset 0
    final long origin = start - 1L;
    Map<Long, IExpr> written = new HashMap<Long, IExpr>();
    List<IExpr> states = new ArrayList<IExpr>(steps + 1);
    long[] offsets = new long[steps + 1];
    // the offset written in each step and the color written there
    long[] writeAt = new long[steps + 1];
    IExpr[] writeColor = new IExpr[steps + 1];
    long offset = 0;
    states.add(state);
    for (int i = 1; i <= steps; i++) {
      engine.checkTimeLimit();
      IExpr color = written.get(offset);
      if (color == null) {
        color = initialColor(cells, pattern, offset + origin);
      }
      IAST transition = rule.apply(state, color, engine);
      if (transition.isPresent()) {
        written.put(offset, transition.arg2());
        writeAt[i] = offset;
        writeColor[i] = transition.arg2();
        state = transition.arg1();
        offset += transition.arg3().toIntDefault();
      }
      states.add(state);
      offsets[i] = offset;
    }
    long lo = -origin;
    long hi = cells.argSize() - 1L - origin;
    for (long x : offsets) {
      lo = Math.min(lo, x);
      hi = Math.max(hi, x);
    }
    final long width = hi - lo + 1;
    checkSize((steps + 1L) * (width + 4L));
    IASTAppendable window = F.ListAlloc((int) width);
    for (long x = lo; x <= hi; x++) {
      window.append(initialColor(cells, pattern, x + origin));
    }
    IASTAppendable result = F.ListAlloc(steps + 1);
    for (int i = 0; i <= steps; i++) {
      if (writeColor[i] != null) {
        window.set((int) (writeAt[i] - lo) + 1, writeColor[i]);
      }
      result.append(row(states.get(i), (int) (offsets[i] - lo) + 1, offsets[i], window.copy()));
    }
    return result;
  }

  /**
   * The initial color of the cell with the index <code>i</code> into <code>cells</code>; outside
   * the given cells the background pattern repeats, aligned with the first given cell.
   */
  private static IExpr initialColor(IAST cells, IAST pattern, long i) {
    if (i >= 0 && i < cells.argSize()) {
      return cells.get((int) i + 1);
    }
    return pattern.get((int) Math.floorMod(i, (long) pattern.argSize()) + 1);
  }

  /**
   * The background of an initial condition <code>{state, {cells, background}}</code> - a color or
   * a non-empty list of colors repeated periodically -, or {@link F#NIL} for a cyclic tape.
   */
  private static IExpr background(IExpr init) {
    if (init.isList2() && init.second().isList2() && init.second().first().isList()) {
      IExpr background = init.second().second();
      if (!background.isList() || background.argSize() > 0) {
        return background;
      }
    }
    return F.NIL;
  }

  /**
   * The rule of a machine: explicit rules, or a rule number <code>n</code>, <code>{n, s}</code> or
   * <code>{n, s, k}</code>.
   *
   * @return <code>null</code> if <code>ruleSpec</code> is no rule
   */
  private static MachineRule machineRule(IExpr ruleSpec) {
    if (ruleSpec.isRuleAST()
        || (ruleSpec.isList() && ruleSpec.argSize() > 0
            && ((IAST) ruleSpec).forAll(x -> x.isRuleAST()))) {
      return new MachineRule(ruleSpec);
    }
    IExpr number = ruleSpec;
    int states = 2;
    int colors = 2;
    if (ruleSpec.isList2() || ruleSpec.isList3()) {
      number = ruleSpec.first();
      states = ruleSpec.second().toIntDefault();
      if (ruleSpec.isList3()) {
        colors = ((IAST) ruleSpec).arg3().toIntDefault();
      }
    }
    if (!number.isInteger() || number.isNegative() || states < 1 || colors < 1
        || (long) states * colors > Config.MAX_AST_SIZE) {
      return null;
    }
    return new MachineRule(((IInteger) number).toBigNumerator(), states, colors);
  }

  private static IAST row(IExpr state, int position, long offset, IAST tape) {
    return F.list(F.list(state, F.ZZ(position), F.ZZ(offset)), tape);
  }

  private static void checkSize(long size) {
    if (size > Config.MAX_AST_SIZE) {
      ASTElementLimitExceeded.throwIt(size);
    }
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_3_2;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.putMessage(IPatternMatcher.SET, "tspec", F.stringx(TSPEC));
    super.setUp(newSymbol);
  }
}
