package org.matheclipse.core.eval;

import java.io.StringWriter;
import java.util.concurrent.Callable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.exception.IterationLimitExceeded;
import org.matheclipse.core.eval.exception.RecursionLimitExceeded;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;

public class EvalControlledCallable implements Callable<IExpr> {

  private static final Logger LOGGER = LogManager.getLogger();

  protected final EvalEngine fEngine;
  private IExpr fExpr;

  public EvalControlledCallable(EvalEngine engine) {
    fEngine = engine;
  }

  public void setExpr(IExpr fExpr) {
    this.fExpr = fExpr;
  }

  @Override
  public IExpr call() throws Exception {
    EvalEngine.remove();
    EvalEngine.setReset(fEngine);
    final StringWriter buf = new StringWriter();
    try {
      //      fEngine.reset();<>
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
        IOFunctions.printMessage(
            S.$IterationLimit,
            "itlim",
            F.List(iterationLimit < 0 ? F.CInfinity : F.ZZ(iterationLimit), fExpr),
            fEngine);
        temp = F.Hold(fExpr);
      } catch (final RecursionLimitExceeded e) {
        // Recursion depth of `1` exceeded during evaluation of `2`.
        int recursionLimit = fEngine.getRecursionLimit();
        IOFunctions.printMessage(
            S.$RecursionLimit,
            "reclim2",
            F.List(recursionLimit < 0 ? F.CInfinity : F.ZZ(recursionLimit), fExpr),
            fEngine);
        temp = F.Hold(fExpr);
      }
      //			IExpr temp = fEngine.evaluate(fExpr);
      if (!fEngine.isOutListDisabled()) {
        fEngine.addInOut(fExpr, temp);
      }
      return temp;
    } catch (org.matheclipse.core.eval.exception.TimeoutException e) {
      return S.$Aborted;
    } catch (final SyntaxError se) {
      String msg = se.getMessage();
      System.err.println(msg);
      System.err.println();
      System.err.flush();
    } catch (final RuntimeException re) {
      Throwable me = re.getCause();
      if (me instanceof MathException) {
        Validate.printException(buf, me);
      } else {
        Validate.printException(buf, re);
      }
      System.err.println(buf.toString());
      System.err.flush();
    } catch (final Exception e) {
      if (Config.SHOW_STACKTRACE) {
        e.printStackTrace();
      }
      Validate.printException(buf, e);
      System.err.println(buf.toString());
      System.err.flush();
    } catch (final OutOfMemoryError e) {
      Validate.printException(buf, e);
      System.err.println(buf.toString());
      System.err.flush();
    } catch (final StackOverflowError e) {
      if (Config.SHOW_STACKTRACE) {
        e.printStackTrace();
      }
      Validate.printException(buf, e);
      System.err.println(buf.toString());
      System.err.flush();
    }
    return S.$Aborted;
  }

  public void cancel() {
    fEngine.stopRequest();
  }
}
