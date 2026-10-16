package org.matheclipse.core.eval;

import java.util.concurrent.Callable;
import org.apfloat.ApfloatInterruptedException;
import org.matheclipse.core.eval.exception.IterationLimitExceeded;
import org.matheclipse.core.eval.exception.RecursionLimitExceeded;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;
import edu.jas.kern.PreemptingException;

public class EvalControlledCallable implements Callable<IExpr> {

  protected final EvalEngine fEngine;
  private IExpr fExpr;
  /** the precision of the input, see {@link #setNumericPrecision(long)} */
  private long fNumericPrecision = -1;

  public EvalControlledCallable(EvalEngine engine) {
    fEngine = engine;
  }

  public void setExpr(IExpr fExpr) {
    this.fExpr = fExpr;
  }

  /**
   * The numeric precision the parser determined for the expression, which the engine reset in
   * {@link #call()} would otherwise forget.
   */
  public void setNumericPrecision(long numericPrecision) {
    this.fNumericPrecision = numericPrecision;
  }

  @Override
  public IExpr call() {
    EvalEngine.remove();
    EvalEngine.setReset(fEngine);
    if (fNumericPrecision > fEngine.getNumericPrecision()) {
      // the reset forgot the precision the parser took from the input: 4.5`30 in BesselJ(1/3,
      // 4.5`30) gives a 30-digit result
      fEngine.setNumericPrecision(fNumericPrecision);
    }
    final StringBuilder buf = new StringBuilder();
    try {
      // fEngine.reset();<>
      IExpr preRead = S.$PreRead.assignedValue();
      IExpr temp;
      try {
        if (preRead != null && preRead.isPresent()) {
          temp = fEngine.evaluate(F.unaryAST1(preRead, fExpr));
        } else {
          temp = fEngine.evaluate(fExpr);
        }
      } catch (final IterationLimitExceeded e) {
        // Iteration limit of `1` exceeded.
        int iterationLimit = e.getIterationLimit();
        Errors.printMessage(S.$IterationLimit, "itlim",
            F.list(iterationLimit < 0 ? F.CInfinity : F.ZZ(iterationLimit), fExpr), fEngine);
        temp = F.Hold(fExpr);
      } catch (final RecursionLimitExceeded e) {
        // Recursion depth of `1` exceeded during evaluation of `2`.
        int recursionLimit = e.getRecursionLimit();
        Errors.printMessage(S.$RecursionLimit, "reclim2",
            F.list(recursionLimit < 0 ? F.CInfinity : F.ZZ(recursionLimit), fExpr), fEngine);
        temp = F.Hold(fExpr);
      }
      // IExpr temp = fEngine.evaluate(fExpr);
      if (!fEngine.isOutListDisabled()) {
        fEngine.addInOut(fExpr, temp);
      }
      return temp;
    } catch (PreemptingException | ApfloatInterruptedException
        | org.matheclipse.core.eval.exception.TimeoutException e) {
      return S.$Aborted;
    } catch (final SyntaxError se) {
      // LOGGER.error("EvalControlledCallable.call() failed", se);
    } catch (final RuntimeException re) {
      Throwable me = re.getCause();
      if (me instanceof MathException) {
        Validate.printException(buf, me);
      } else {
        Validate.printException(buf, re);
      }
      // LOGGER.error(buf);
    } catch (final Exception | OutOfMemoryError | StackOverflowError e) {
      // LOGGER.debug("EvalControlledCallable.call() failed", e);
      Validate.printException(buf, e);
      // LOGGER.error(buf);
    }
    return S.$Aborted;
  }

  // public void cancel() {
  // fEngine.stopRequest();
  // try {
  // Thread.sleep(Config.TIME_CONSTRAINED_SLEEP_MILLISECONDS);
  // } catch (InterruptedException e) {
  // //
  // }
  // if (fThread.isAlive()) {
  // call the deprecated method as last possible exit
  // fThread.stop();
  // }
  // }
}
