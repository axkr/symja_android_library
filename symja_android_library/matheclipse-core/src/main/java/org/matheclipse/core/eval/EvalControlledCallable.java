package org.matheclipse.core.eval;

import java.util.concurrent.Callable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apfloat.ApfloatInterruptedException;
import org.matheclipse.core.basic.Config;
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
  private static final Logger LOGGER = LogManager.getLogger(EvalControlledCallable.class);
 
  protected final EvalEngine fEngine;
  private IExpr fExpr;

  public EvalControlledCallable(EvalEngine engine) {
    fEngine = engine;
  }

  public void setExpr(IExpr fExpr) {
    this.fExpr = fExpr;
  }

  @Override
  public IExpr call() {
    EvalEngine.remove();
    EvalEngine.setReset(fEngine);
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
        int iterationLimit = fEngine.getIterationLimit();
        Errors.printMessage(S.$IterationLimit, "itlim",
            F.list(iterationLimit < 0 ? F.CInfinity : F.ZZ(iterationLimit), fExpr), fEngine);
        temp = F.Hold(fExpr);
      } catch (final RecursionLimitExceeded e) {
        // Recursion depth of `1` exceeded during evaluation of `2`.
        int recursionLimit = fEngine.getRecursionLimit();
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
      LOGGER.error("EvalControlledCallable.call() failed", se);
    } catch (final RuntimeException re) {
      Throwable me = re.getCause();
      if (me instanceof MathException) {
        Validate.printException(buf, me);
      } else {
        Validate.printException(buf, re);
      }
      LOGGER.error(buf);
    } catch (final Exception | OutOfMemoryError | StackOverflowError e) {
      LOGGER.debug("EvalControlledCallable.call() failed", e);
      Validate.printException(buf, e);
      LOGGER.error(buf);
    }
    return S.$Aborted;
  }

  public void cancel() {
    // fEngine.stopRequest();
    try {
      Thread.sleep(Config.TIME_CONSTRAINED_SLEEP_MILLISECONDS);
    } catch (InterruptedException e) {
      //
    }
    // if (fThread.isAlive()) {
    // call the deprecated method as last possible exit
    // fThread.stop();
    // }
  }
}
