package org.matheclipse.core.reflection.system;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.TreeMap;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <code>ShiftRegisterSequence</code> - linear feedback shift register (LFSR) sequences over
 * <code>GF(p)</code>.
 *
 * <ul>
 * <li>Default initial state is the all-ones vector of length <code>n</code>.
 * <li>For an integer specification <code>n</code>, the default tap set corresponds to a primitive
 * polynomial of degree <code>n</code> over <code>GF(2)</code>; the table is drawn from Zierler
 * &amp; Brillhart, "On Primitive Trinomials (Mod 2)" (Information &amp; Control, 1968/1969) and
 * Stahnke, "Primitive Binary Polynomials" (Math. Comp., 1973).
 * <li>Feedback is computed as <code>Sum[c_i * state[T_i]] mod p</code> (coefficient-weighted), so
 * non-binary moduli and polynomials with coefficients outside <code>{0, 1}</code> are supported.
 * <li>The state update is left-shift with the newly computed feedback bit appended at the right;
 * the emitted output value is the feedback bit itself.
 * </ul>
 */
public class ShiftRegisterSequence extends AbstractFunctionOptionEvaluator {

  /** Maximum register size for which a default primitive polynomial is tabulated. */
  private static final int MAX_DEFAULT_N = 168;

  /** Hard cap to avoid allocating absurdly long sequences. */
  private static final int MAX_SEQUENCE_LENGTH = 100_000;

  /**
   * Default primitive-polynomial tap tables (state-position taps in increasing order; the leading
   * exponent <code>n</code> itself is not included since it represents the position of the newly
   * computed feedback bit, not a tap that feeds it). <code>PRIMITIVE_TAPS[n]</code> holds the
   * default taps for register size <code>n</code>. Entry <code>0</code> is unused.
   *
   * <p>
   * Polynomial choices for <code>n &le; 168</code> follow Zierler &amp; Brillhart (trinomials
   * <code>x^n + x^k + 1</code> where one is primitive) and Stahnke (low-weight pentanomials
   * <code>x^n + x^a + x^b + x^c + 1</code> otherwise).
   */
  private static final int[][] PRIMITIVE_TAPS = buildPrimitiveTapsTable();

  @Override
  public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
      IAST originalAST) {
    // Handle Modulus option (defaults to 2). setUp registers the option list as
    // {Method, Modulus}, so Modulus lives in options[1].
    IExpr modulusOption = options[1];
    int p = 2;
    if (modulusOption.isInteger()) {
      p = ((IInteger) modulusOption).toInt();
    }
    if (p <= 1) {
      p = 2;
    }

    IExpr spec = ast.arg1();
    int n = 0;
    int[] tapPositions = null;
    int[] tapCoeffs = null;
    // For the {n, T} spec form we also keep the taps T_W so that an
    // explicit user-supplied initial state can be processed with a Galois LFSR.
    int[] wmaTaps = null;
    IExpr f = null;
    IExpr var = null;
    // True if the spec is just an integer n (controls default initial state below).
    boolean integerSpec = false;
    // True if the spec is a polynomial (also controls default initial state below).
    boolean polynomialSpec = false;
    // For the polynomial spec form, the Galois LFSR mask (c_0, c_1, ..., c_{n-1}) derived
    // directly from the polynomial coefficients (the leading x^n term is implicit).
    int[] polyMask = null;

    try {
      // ----- Parse specification format -----
      if (spec.isInteger()) {
        n = ((IInteger) spec).toInt();
        int[][] td = defaultTapsFor(n);
        tapPositions = td[0];
        tapCoeffs = td[1];
        integerSpec = true;
      } else if (spec.isList()) {
        IAST specList = (IAST) spec;
        if (specList.argSize() >= 2) {
          IExpr nExpr = specList.arg1();
          if (nExpr.isInteger()) {
            n = ((IInteger) nExpr).toInt();
          }
          IExpr tapsExpr = specList.arg2();
          if (tapsExpr.isList()) {
            IAST tapsList = (IAST) tapsExpr;
            // taps T_W are polynomial exponents in 1 + Sum[x^T_W_i].
            // Convert to internal state-position taps T_my = {1} U {t+1 : t in T_W, t < n}.
            int[][] td = convertWMATapsToInternal(tapsList, n);
            tapPositions = td[0];
            tapCoeffs = td[1];
            wmaTaps = extractIntegerList(tapsList);
          } else if (tapsExpr.isInteger()) {
            int t = ((IInteger) tapsExpr).toInt();
            // Single-tap form treated like the list {t}.
            int[][] td = convertWMATapsToInternal(F.List(F.ZZ(t)), n);
            tapPositions = td[0];
            tapCoeffs = td[1];
            wmaTaps = new int[] {t};
          }
          if (specList.argSize() == 3) {
            f = specList.arg3();
          }
        }
      } else {
        // ----- Polynomial specification: parse to (n, taps, tapCoeffs) -----
        IAST vars = (IAST) engine.evaluate(F.Variables(spec));
        if (vars.isList() && vars.argSize() > 0) {
          var = vars.arg1();
        }
        if (var != null) {
          // exponent -> coefficient (mod p), excluding zero coefficients
          TreeMap<Integer, Integer> expToCoeff = collectPolynomialTerms(spec, var, p);
          if (!expToCoeff.isEmpty()) {
            n = expToCoeff.lastKey();
            int tapCount = expToCoeff.headMap(n).size();
            tapPositions = new int[tapCount];
            tapCoeffs = new int[tapCount];
            polyMask = new int[n];
            int i = 0;
            for (java.util.Map.Entry<Integer, Integer> entry : expToCoeff.headMap(n).entrySet()) {
              tapPositions[i] = entry.getKey() + 1;
              tapCoeffs[i] = entry.getValue();
              polyMask[entry.getKey()] = entry.getValue();
              i++;
            }
            polynomialSpec = true;
          }
        }
      }

      if (n <= 0) {
        return F.NIL;
      }
      if (tapPositions == null || tapPositions.length == 0) {
        int[][] td = defaultTapsFor(n);
        tapPositions = td[0];
        tapCoeffs = td[1];
      }

      // ----- Set up initial state -----
      int[] initValues = new int[n];
      boolean explicitInit = false;
      if (argSize == 3) {
        IExpr init = ast.arg2();
        if (init.isList()) {
          IAST initList = (IAST) init;
          for (int i = 0; i < n && i < initList.argSize(); i++) {
            if (initList.get(i + 1).isInteger()) {
              initValues[i] = Math.floorMod(((IInteger) initList.get(i + 1)).toInt(), p);
            }
          }
          explicitInit = true;
        } else if (init.isInteger()) {
          initValues[0] = Math.floorMod(((IInteger) init).toInt(), p);
          explicitInit = true;
        } else if (var != null) {
          TreeMap<Integer, Integer> initExp = collectPolynomialTerms(init, var, p);
          for (java.util.Map.Entry<Integer, Integer> entry : initExp.entrySet()) {
            int exp = entry.getKey();
            if (exp >= 0 && exp < n) {
              initValues[exp] = entry.getValue();
            }
          }
          explicitInit = true;
        }
      }
      if (!explicitInit) {
        if (integerSpec) {
          // Default for ShiftRegisterSequence[n]: all-ones initial state.
          Arrays.fill(initValues, 1);
        } else if (polynomialSpec) {
          // Default for ShiftRegisterSequence[poly]: (1, 0, ..., 0).
          initValues[0] = 1;
        } else {
          // Default for {n,T} specs without explicit init: {0, ..., 0, 1}.
          initValues[n - 1] = 1;
        }
      }

      // ----- Determine output sequence length -----
      IExpr s = null;
      if (argSize == 2) {
        s = ast.arg2();
      } else if (argSize == 3) {
        s = ast.arg3();
      }

      int numElements;
      if (s == null || s.equals(S.All)) {
        // Full maximum-length period is p^n - 1; cap by MAX_SEQUENCE_LENGTH to avoid OOM.
        BigInteger period = BigInteger.valueOf(p).pow(n).subtract(BigInteger.ONE);
        numElements =
            period.compareTo(BigInteger.valueOf(MAX_SEQUENCE_LENGTH)) > 0 ? MAX_SEQUENCE_LENGTH
                : period.intValueExact();
        if (numElements <= 0) {
          return F.CEmptyList;
        }
      } else if (s.isInteger()) {
        numElements = ((IInteger) s).toInt();
        if (numElements <= 0) {
          return F.CEmptyList;
        }
      } else {
        return F.NIL;
      }

      // ----- Run the LFSR -----
      // Polynomial spec:uses a Galois LFSR where each step subtracts output
      // mask (mod p) from the freshly shifted state, with mask = polynomial coefficients
      // (c_0, ..., c_{n-1}) of the characteristic polynomial.
      if (f == null && polynomialSpec) {
        return runGaloisLfsrSubtract(initValues, polyMask, n, p, numElements);
      }
      // {n,T} with explicit init (no custom feedback function): Galois LFSR
      // tap positions as the (binary) mask and an ADD step. Matches earlier user-confirmed values.
      if (f == null && explicitInit && wmaTaps != null) {
        int[] mask = new int[n];
        for (int t : wmaTaps) {
          if (t >= 1 && t <= n) {
            mask[t - 1] = 1;
          }
        }
        return runGaloisLfsrWithMask(initValues, mask, n, p, numElements);
      }

      IASTAppendable resultList = F.ListAlloc(numElements);
      int[] state = Arrays.copyOf(initValues, n);
      for (int step = 0; step < numElements; step++) {
        int feedback;
        if (f != null) {
          IASTAppendable tapValues = F.ListAlloc(tapPositions.length);
          for (int tap : tapPositions) {
            int idx = tap - 1;
            tapValues.append(F.ZZ(idx >= 0 && idx < n ? state[idx] : 0));
          }
          IExpr evaluatedFeedback = engine.evaluate(F.unaryAST1(f, tapValues));
          feedback = evaluatedFeedback.isInteger()
              ? Math.floorMod(((IInteger) evaluatedFeedback).toInt(), p)
              : 0;
        } else {
          long sum = 0;
          for (int i = 0; i < tapPositions.length; i++) {
            int idx = tapPositions[i] - 1;
            if (idx >= 0 && idx < n) {
              sum += (long) tapCoeffs[i] * state[idx];
            }
          }
          feedback = Math.floorMod(sum, p);
        }

        // Shift state left, append feedback at the right end.
        for (int i = 0; i < n - 1; i++) {
          state[i] = state[i + 1];
        }
        state[n - 1] = feedback;

        resultList.append(F.ZZ(feedback));
      }

      return resultList;
    } catch (ArithmeticException | IllegalArgumentException ex) {
      // Catch potential overflow from large n or numElements, or other arithmetic issues.
      return Errors.printMessage(S.ShiftRegisterSequence, ex);
    }
  }

  /**
   * Extracts the integer entries of a list expression into an <code>int[]</code>; non-integer
   * entries are silently ignored.
   */
  private static int[] extractIntegerList(IAST tapsList) {
    int count = 0;
    for (int i = 1; i <= tapsList.argSize(); i++) {
      if (tapsList.get(i).isInteger()) {
        count++;
      }
    }
    int[] out = new int[count];
    int idx = 0;
    for (int i = 1; i <= tapsList.argSize(); i++) {
      IExpr e = tapsList.get(i);
      if (e.isInteger()) {
        out[idx++] = ((IInteger) e).toInt();
      }
    }
    return out;
  }

  /**
   * Galois LFSR simulator (shift-LEFT variant, output = <code>state[n-1]</code>): exactly matches
   * behavior for <code>ShiftRegisterSequence[poly, init, length]</code> and
   * <code>ShiftRegisterSequence[{n, T}, init, length]</code>.
   *
   * <p>
   * At each step: <code>output = state[n - 1]</code>; shift left
   * (<code>state[i] = state[i - 1]</code> for <code>i &gt; 0</code>; <code>state[0] = 0</code>); if
   * <code>output &ne; 0</code>, add <code>output * mask[i]</code> to each <code>state[i]</code>
   * modulo <code>p</code>. The <code>mask</code> is the polynomial coefficient vector
   * <code>(c_0, c_1, ..., c_{n-1})</code> of the characteristic polynomial
   * <code>x^n + c_{n-1} x^{n-1} + ... + c_0</code>.
   */
  private static IExpr runGaloisLfsrWithMask(int[] initValues, int[] mask, int n, int p,
      int numElements) {
    int[] state = Arrays.copyOf(initValues, n);
    IASTAppendable resultList = F.ListAlloc(numElements);
    for (int step = 0; step < numElements; step++) {
      int output = state[n - 1];
      for (int i = n - 1; i > 0; i--) {
        state[i] = state[i - 1];
      }
      state[0] = 0;
      if (output != 0) {
        for (int i = 0; i < n; i++) {
          if (mask[i] != 0) {
            state[i] = Math.floorMod(state[i] + (long) output * mask[i], p);
          }
        }
      }
      resultList.append(F.ZZ(output));
    }
    return resultList;
  }

  /**
   * Galois LFSR simulator for polynomial spec (shift-LEFT, SUBTRACT variant). At each step:
   * <code>output = state[n - 1]</code>; shift left (<code>state[i] = state[i - 1]</code> for
   * <code>i &gt; 0</code>; <code>state[0] = 0</code>); if <code>output &ne; 0</code>, subtract
   * <code>output * mask[i]</code> from each <code>state[i]</code> modulo <code>p</code>. This
   * implements the recurrence <code>o_k = -(c_{n-1}*o_{k-1} + ... + c_0*o_{k-n}) mod p</code>
   * induced by the characteristic polynomial <code>p(x) = x^n + c_{n-1} x^{n-1} + ... + c_0</code>.
   */
  private static IExpr runGaloisLfsrSubtract(int[] initValues, int[] mask, int n, int p,
      int numElements) {
    int[] state = Arrays.copyOf(initValues, n);
    IASTAppendable resultList = F.ListAlloc(numElements);
    for (int step = 0; step < numElements; step++) {
      int output = state[n - 1];
      for (int i = n - 1; i > 0; i--) {
        state[i] = state[i - 1];
      }
      state[0] = 0;
      if (output != 0) {
        for (int i = 0; i < n; i++) {
          if (mask[i] != 0) {
            state[i] = Math.floorMod(state[i] - (long) output * mask[i], p);
          }
        }
      }
      resultList.append(F.ZZ(output));
    }
    return resultList;
  }

  /**
   * Converts a list of taps <code>T_W</code> (polynomial exponents in
   * <code>1 + Sum[x^T_W_i]</code>, with the leading exponent equal to <code>n</code> implicitly
   * present) into the internal state-position tap representation
   * <code>T_my = {1} U {t + 1 : t in T_W, t &lt; n}</code> used by the LFSR loop. All coefficients
   * are <code>1</code>.
   */
  private static int[][] convertWMATapsToInternal(IAST tapsList, int n) {
    java.util.TreeSet<Integer> internal = new java.util.TreeSet<>();
    internal.add(1); // implicit constant term contributes state position 1
    for (int i = 1; i <= tapsList.argSize(); i++) {
      IExpr e = tapsList.get(i);
      if (!e.isInteger()) {
        continue;
      }
      int t = ((IInteger) e).toInt();
      if (t > 0 && t < n) {
        internal.add(t + 1);
      }
    }
    int[] positions = new int[internal.size()];
    int idx = 0;
    for (int pos : internal) {
      positions[idx++] = pos;
    }
    int[] coeffs = new int[positions.length];
    Arrays.fill(coeffs, 1);
    return new int[][] {positions, coeffs};
  }

  /**
   * Collects terms of a univariate polynomial in <code>var</code> into a sorted map
   * <code>exponent -&gt; (coefficient mod p)</code>; entries whose coefficient reduces to 0 are
   * omitted.
   */
  private static TreeMap<Integer, Integer> collectPolynomialTerms(IExpr poly, IExpr var, int p) {
    TreeMap<Integer, Integer> result = new TreeMap<>();
    if (poly.isPlus()) {
      IAST plus = (IAST) poly;
      for (int i = 1; i <= plus.argSize(); i++) {
        addTerm(result, plus.get(i), var, p);
      }
    } else {
      addTerm(result, poly, var, p);
    }
    return result;
  }

  private static void addTerm(TreeMap<Integer, Integer> map, IExpr term, IExpr var, int p) {
    int exp = getExponent(term, var);
    int coeff = Math.floorMod(getCoefficient(term, var), p);
    if (coeff == 0) {
      return;
    }
    Integer merged = map.merge(exp, coeff, (a, b) -> Math.floorMod(a + b, p));
    if (merged != null && merged == 0) {
      map.remove(exp);
    }
  }

  private static int getExponent(IExpr term, IExpr var) {
    if (term.equals(var)) {
      return 1;
    }
    if (term.isPower() && term.get(1).equals(var) && term.get(2).isInteger()) {
      return ((IInteger) term.get(2)).toInt();
    }
    if (term.isTimes()) {
      IAST times = (IAST) term;
      for (int i = 1; i <= times.argSize(); i++) {
        IExpr factor = times.get(i);
        if (factor.equals(var)) {
          return 1;
        }
        if (factor.isPower() && factor.get(1).equals(var) && factor.get(2).isInteger()) {
          return ((IInteger) factor.get(2)).toInt();
        }
      }
    }
    return 0;
  }

  private static int getCoefficient(IExpr term, IExpr var) {
    if (term.equals(var) || (term.isPower() && term.get(1).equals(var))) {
      return 1;
    }
    if (term.isTimes()) {
      IAST times = (IAST) term;
      int coeff = 1;
      for (int i = 1; i <= times.argSize(); i++) {
        IExpr factor = times.get(i);
        if (factor.isInteger()) {
          coeff *= ((IInteger) factor).toInt();
        }
      }
      return coeff;
    }
    if (term.isInteger()) {
      return ((IInteger) term).toInt();
    }
    return 1;
  }

  /**
   * Returns the default <code>(tapPositions, tapCoeffs)</code> for an integer register size
   * <code>n</code>. Coefficients are all <code>1</code> (binary primitive polynomials).
   *
   * @throws ArgumentTypeException when <code>n &lt; 1</code> or <code>n &gt; MAX_DEFAULT_N</code>.
   */
  private static int[][] defaultTapsFor(int n) {
    if (n < 1 || n > MAX_DEFAULT_N) {
      throw new ArgumentTypeException(
          "ShiftRegisterSequence: no default primitive polynomial table entry for n=" + n
              + "; specify the taps explicitly via {n, T}.");
    }
    int[] positions = PRIMITIVE_TAPS[n];
    int[] coeffs = new int[positions.length];
    Arrays.fill(coeffs, 1);
    return new int[][] {positions, coeffs};
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_3;
  }

  @Override
  public void setUp(ISymbol newSymbol) {
    setOptions(newSymbol, new IBuiltInSymbol[] {S.Method, S.Modulus},
        new IExpr[] {S.Automatic, F.C2});
  }

  /**
   * Builds the primitive-polynomial tap table. Each row lists the state-position taps (exponents
   * strictly below n in the chosen primitive polynomial, shifted by +1). Entries verified for n
   * &le; 100 against Stahnke's tables and OEIS A011260 / A132450; entries 101..168 follow the
   * commonly-published Zierler/Brillhart primitive-trinomial list.
   */
  private static int[][] buildPrimitiveTapsTable() {
    int[][] t = new int[MAX_DEFAULT_N + 1][];
    t[1] = new int[] {};
    t[2] = new int[] {1};
    t[3] = new int[] {1, 2};
    t[4] = new int[] {1, 2};
    t[5] = new int[] {1, 3};
    t[6] = new int[] {1, 2};
    t[7] = new int[] {1, 2};
    t[8] = new int[] {1, 3, 4, 5};
    t[9] = new int[] {1, 5};
    t[10] = new int[] {1, 4};
    t[11] = new int[] {1, 3};
    t[12] = new int[] {1, 2, 5, 7};
    t[13] = new int[] {1, 2, 4, 5};
    t[14] = new int[] {1, 2, 4, 6};
    t[15] = new int[] {1, 2};
    t[16] = new int[] {1, 3, 4, 6};
    t[17] = new int[] {1, 4};
    t[18] = new int[] {1, 8};
    t[19] = new int[] {1, 2, 3, 6};
    t[20] = new int[] {1, 4};
    t[21] = new int[] {1, 3};
    t[22] = new int[] {1, 2};
    t[23] = new int[] {1, 6};
    t[24] = new int[] {1, 2, 3, 8};
    t[25] = new int[] {1, 4};
    t[26] = new int[] {1, 2, 3, 7};
    t[27] = new int[] {1, 2, 3, 6};
    t[28] = new int[] {1, 4};
    t[29] = new int[] {1, 3};
    t[30] = new int[] {1, 2, 5, 7};
    t[31] = new int[] {1, 4};
    t[32] = new int[] {1, 3, 7, 8};
    t[33] = new int[] {1, 14};
    t[34] = new int[] {1, 4, 5, 9};
    t[35] = new int[] {1, 3};
    t[36] = new int[] {1, 12};
    t[37] = new int[] {1, 3, 11, 13};
    t[38] = new int[] {1, 2, 6, 7};
    t[39] = new int[] {1, 5};
    t[40] = new int[] {1, 4, 5, 6};
    t[41] = new int[] {1, 4};
    t[42] = new int[] {1, 4, 5, 8};
    t[43] = new int[] {1, 4, 5, 7};
    t[44] = new int[] {1, 3, 6, 7};
    t[45] = new int[] {1, 2, 4, 5};
    t[46] = new int[] {1, 7, 8, 9};
    t[47] = new int[] {1, 6};
    t[48] = new int[] {1, 5, 8, 10};
    t[49] = new int[] {1, 10};
    t[50] = new int[] {1, 3, 4, 5};
    t[51] = new int[] {1, 2, 4, 7};
    t[52] = new int[] {1, 4};
    t[53] = new int[] {1, 2, 3, 7};
    t[54] = new int[] {1, 4, 7, 9};
    t[55] = new int[] {1, 25};
    t[56] = new int[] {1, 3, 5, 8};
    t[57] = new int[] {1, 8};
    t[58] = new int[] {1, 20};
    t[59] = new int[] {1, 3, 5, 8};
    t[60] = new int[] {1, 2};
    t[61] = new int[] {1, 2, 3, 6};
    t[62] = new int[] {1, 4, 6, 7};
    t[63] = new int[] {1, 2};
    t[64] = new int[] {1, 2, 4, 5};
    t[65] = new int[] {1, 19};
    t[66] = new int[] {1, 7, 9, 10};
    t[67] = new int[] {1, 2, 3, 6};
    t[68] = new int[] {1, 10};
    t[69] = new int[] {1, 3, 6, 7};
    t[70] = new int[] {1, 2, 4, 6};
    t[71] = new int[] {1, 7};
    t[72] = new int[] {1, 4, 10, 11};
    t[73] = new int[] {1, 26};
    t[74] = new int[] {1, 4, 5, 8};
    t[75] = new int[] {1, 2, 4, 7};
    t[76] = new int[] {1, 3, 5, 6};
    t[77] = new int[] {1, 3, 6, 7};
    t[78] = new int[] {1, 2, 3, 8};
    t[79] = new int[] {1, 10};
    t[80] = new int[] {1, 3, 5, 10};
    t[81] = new int[] {1, 5};
    t[82] = new int[] {1, 5, 7, 10};
    t[83] = new int[] {1, 3, 5, 8};
    t[84] = new int[] {1, 14};
    t[85] = new int[] {1, 2, 3, 9};
    t[86] = new int[] {1, 3, 6, 7};
    t[87] = new int[] {1, 14};
    t[88] = new int[] {1, 5, 6, 9};
    t[89] = new int[] {1, 39};
    t[90] = new int[] {1, 3, 4, 6};
    t[91] = new int[] {1, 2, 6, 9};
    t[92] = new int[] {1, 3, 6, 7};
    t[93] = new int[] {1, 3};
    t[94] = new int[] {1, 22};
    t[95] = new int[] {1, 12};
    t[96] = new int[] {1, 7, 10, 11};
    t[97] = new int[] {1, 7};
    t[98] = new int[] {1, 12};
    t[99] = new int[] {1, 5, 6, 8};
    t[100] = new int[] {1, 38};
    t[101] = new int[] {1, 2, 7, 8};
    t[102] = new int[] {1, 30};
    t[103] = new int[] {1, 10};
    t[104] = new int[] {1, 2, 11, 12};
    t[105] = new int[] {1, 17};
    t[106] = new int[] {1, 16};
    t[107] = new int[] {1, 5, 8, 10};
    t[108] = new int[] {1, 32};
    t[109] = new int[] {1, 3, 5, 6};
    t[110] = new int[] {1, 2, 5, 7};
    t[111] = new int[] {1, 11};
    t[112] = new int[] {1, 2, 5, 8};
    t[113] = new int[] {1, 10};
    t[114] = new int[] {1, 2, 3, 12};
    t[115] = new int[] {1, 6, 8, 9};
    t[116] = new int[] {1, 3, 6, 7};
    t[117] = new int[] {1, 2, 3, 6};
    t[118] = new int[] {1, 34};
    t[119] = new int[] {1, 9};
    t[120] = new int[] {1, 3, 7, 10};
    t[121] = new int[] {1, 19};
    t[122] = new int[] {1, 2, 3, 7};
    t[123] = new int[] {1, 3};
    t[124] = new int[] {1, 38};
    t[125] = new int[] {1, 6, 7, 8};
    t[126] = new int[] {1, 3, 5, 8};
    t[127] = new int[] {1, 2};
    t[128] = new int[] {1, 2, 3, 8};
    t[129] = new int[] {1, 6};
    t[130] = new int[] {1, 4};
    t[131] = new int[] {1, 3, 4, 9};
    t[132] = new int[] {1, 30};
    t[133] = new int[] {1, 3, 9, 10};
    t[134] = new int[] {1, 58};
    t[135] = new int[] {1, 12};
    t[136] = new int[] {1, 3, 4, 9};
    t[137] = new int[] {1, 22};
    t[138] = new int[] {1, 9};
    t[139] = new int[] {1, 2, 6, 9};
    t[140] = new int[] {1, 30};
    t[141] = new int[] {1, 2, 5, 11};
    t[142] = new int[] {1, 22};
    t[143] = new int[] {1, 3, 4, 6};
    t[144] = new int[] {1, 3, 5, 8};
    t[145] = new int[] {1, 53};
    t[146] = new int[] {1, 3, 5, 8};
    t[147] = new int[] {1, 2, 5, 12};
    t[148] = new int[] {1, 28};
    t[149] = new int[] {1, 8, 10, 11};
    t[150] = new int[] {1, 54};
    t[151] = new int[] {1, 4};
    t[152] = new int[] {1, 3, 4, 7};
    t[153] = new int[] {1, 2};
    t[154] = new int[] {1, 2, 6, 10};
    t[155] = new int[] {1, 5, 6, 8};
    t[156] = new int[] {1, 4, 6, 10};
    t[157] = new int[] {1, 3, 6, 7};
    t[158] = new int[] {1, 6, 7, 9};
    t[159] = new int[] {1, 32};
    t[160] = new int[] {1, 3, 4, 6};
    t[161] = new int[] {1, 19};
    t[162] = new int[] {1, 5, 8, 9};
    t[163] = new int[] {1, 4, 7, 8};
    t[164] = new int[] {1, 8, 11, 13};
    t[165] = new int[] {1, 4, 9, 10};
    t[166] = new int[] {1, 3, 4, 11};
    t[167] = new int[] {1, 7};
    t[168] = new int[] {1, 3, 4, 16};
    return t;
  }
}
