package org.matheclipse.core.builtin;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/** Filter models and the recurrences which run them on data. */
public final class FilterDesign {

  /** The largest order of a filter model. */
  private static final int MAX_ORDER = 64;

  private FilterDesign() {}

  /**
   * <code>ButterworthFilterModel(spec)</code> and <code>ButterworthFilterModel(spec, s)</code> - the
   * Butterworth filter as a <code>TransferFunctionModel</code>. <code>spec</code> is the order
   * <code>n</code> (a lowpass with unit cutoff, in machine numbers), <code>{n, wc}</code>,
   * <code>{"Lowpass" | "Highpass", n, wc}</code> or
   * <code>{"Bandpass" | "Bandstop", n, {w1, w2}}</code>.
   *
   * <p>
   * With the negated poles <code>u(k)</code> of the normalized filter on the unit circle, the lowpass
   * is <code>wc^n/Product(s + wc u(k))</code>, the highpass <code>s^n/Product(s + wc/u(k))</code>, the
   * bandpass <code>(b s)^n/Product(s^2 + b u(k) s + w0^2)</code> and the bandstop
   * <code>(s^2 + w0^2)^n/Product(s^2 + (b/u(k)) s + w0^2)</code>, where <code>b = w2 - w1</code> and
   * <code>w0^2 = w1 w2</code>.
   */
  public static final class ButterworthFilterModel extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr s = ast.isAST2() ? ast.arg2() : S.s;
      if (!s.isVariable()) {
        return F.NIL;
      }
      IExpr spec = ast.arg1();
      String kind = "Lowpass";
      int n;
      IExpr cutoff;
      boolean numeric;
      if (spec.isInteger()) {
        n = spec.toIntDefault();
        cutoff = F.C1;
        numeric = true;
      } else if (spec.isList2()) {
        n = spec.first().toIntDefault();
        cutoff = spec.second();
        numeric = false;
      } else if (spec.isList3() && spec.first().isString()) {
        kind = spec.first().toString();
        n = spec.second().toIntDefault();
        cutoff = spec.getAt(3);
        numeric = false;
      } else {
        return F.NIL;
      }
      boolean band = kind.equals("Bandpass") || kind.equals("Bandstop");
      if (n < 1 || n > MAX_ORDER || cutoff.isList() != band || (band && !cutoff.isList2())
          || !(band || kind.equals("Lowpass") || kind.equals("Highpass"))) {
        return F.NIL;
      }
      numeric |= cutoff.has(x -> x.isInexactNumber(), false);
      IExpr[] u = new IExpr[n];
      for (int k = 1; k <= n; k++) {
        // u(k) = -Exp(I Pi (2 k + n - 1)/(2 n))
        IExpr pole = engine.evaluate(F.ComplexExpand(
            F.Negate(F.Exp(F.Times(F.CI, S.Pi, F.QQ(2 * k + n - 1, 2 * n))))));
        u[k - 1] = numeric ? engine.evalN(pole) : pole;
      }
      IExpr num;
      IASTAppendable den = F.TimesAlloc(n + 1);
      final IExpr nn = F.ZZ(n);
      if (band) {
        IExpr b = engine.evaluate(F.Subtract(cutoff.second(), cutoff.first()));
        IExpr w0Squared = engine.evaluate(F.Times(cutoff.first(), cutoff.second()));
        IExpr sSquared = F.Sqr(s);
        boolean pass = kind.equals("Bandpass");
        num = pass ? F.Power(F.Times(b, s), nn) : F.Power(F.Plus(w0Squared, sSquared), nn);
        for (IExpr uk : u) {
          den.append(F.Plus(w0Squared, F.Times(b, pass ? uk : F.Power(uk, F.CN1), s), sSquared));
        }
      } else if (kind.equals("Highpass")) {
        num = F.Power(s, nn);
        for (IExpr uk : u) {
          den.append(F.Plus(s, F.Times(cutoff, F.Power(uk, F.CN1))));
        }
      } else {
        IExpr gain = engine.evaluate(F.Power(cutoff, nn));
        if (numeric) {
          gain = engine.evalN(gain);
        }
        num = engine.evaluate(F.Numerator(gain));
        den.append(engine.evaluate(F.Denominator(gain)));
        for (IExpr uk : u) {
          den.append(F.Plus(s, F.Times(cutoff, uk)));
        }
      }
      return F.binaryAST2(S.TransferFunctionModel,
          F.list(F.list(F.list(engine.evaluate(num))),
              F.list(F.list(engine.evaluate(den.oneIdentity1())))),
          s);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  /**
   * <code>RecurrenceFilter({alpha, beta}, x)</code> - the output <code>y</code> of the recurrence
   * <code>alpha(0) y(n) + alpha(1) y(n-1) + ... == beta(0) x(n) + beta(1) x(n-1) + ...</code>,
   * starting from zero history.
   *
   * <p>
   * <code>RecurrenceFilter(tf, x)</code> - the same for a sampled
   * <code>TransferFunctionModel</code> with one input and one output: the coefficients are those of
   * its denominator and numerator, by descending powers of the variable.
   */
  public static final class RecurrenceFilter extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.arg2().isList()) {
        return F.NIL;
      }
      IAST data = (IAST) ast.arg2();
      IExpr filter = ast.arg1();
      if (filter.isAST(S.TransferFunctionModel)) {
        return ofModel(filter, ast, engine);
      }
      if (!filter.isList2() || !filter.first().isList() || !filter.second().isList()
          || filter.first().argSize() == 0 || filter.first().first().isZero()) {
        return F.NIL;
      }
      IAST alpha = (IAST) filter.first();
      IAST beta = (IAST) filter.second();
      final int n = data.argSize();
      IExpr[] y = new IExpr[n];
      for (int i = 0; i < n; i++) {
        engine.checkTimeLimit();
        IASTAppendable sum = F.PlusAlloc(alpha.argSize() + beta.argSize());
        for (int j = 0; j < beta.argSize() && j <= i; j++) {
          sum.append(F.Times(beta.get(j + 1), data.get(i - j + 1)));
        }
        for (int k = 1; k < alpha.argSize() && k <= i; k++) {
          sum.append(F.Times(F.CN1, alpha.get(k + 1), y[i - k]));
        }
        y[i] = engine.evaluate(F.Divide(sum.oneIdentity0(), alpha.first()));
      }
      return F.mapRange(0, n, i -> y[i]);
    }

    /** The filter of a model: <code>{alpha, beta}</code> from its coefficients. */
    private static IExpr ofModel(IExpr model, IAST ast, EvalEngine engine) {
      IExpr[] element = ControlSystems.singleElement(model, engine);
      if (element == null) {
        return F.NIL;
      }
      IExpr numerator = engine.evaluate(F.CoefficientList(F.Expand(element[0]), element[2]));
      IExpr denominator = engine.evaluate(F.CoefficientList(F.Expand(element[1]), element[2]));
      if (!numerator.isList() || !denominator.isList()
          || numerator.argSize() > denominator.argSize()) {
        return F.NIL;
      }
      int order = denominator.argSize();
      IAST alpha = F.mapRange(0, order, i -> denominator.getAt(order - i));
      int shift = order - numerator.argSize();
      IAST beta = F.mapRange(0, order, i -> i < shift ? F.C0 : numerator.getAt(order - i));
      return F.binaryAST2(S.RecurrenceFilter, F.list(alpha, beta), ast.arg2());
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }
}
