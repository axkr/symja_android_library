/*
 *  Copyright 2017 TWO SIGMA OPEN SOURCE, LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.twosigma.beakerx.symjamma.evaluator;

import static com.twosigma.beakerx.evaluator.BaseEvaluator.INTERUPTED_MSG;
import static com.twosigma.beakerx.symjamma.evaluator.SymjaMMAStackTracePrettyPrinter.printStacktrace;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.builtin.GraphicsFunctions;
import org.matheclipse.core.eval.EvalControlledCallable;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import com.twosigma.beakerx.TryResult;
import com.twosigma.beakerx.jvm.object.SimpleEvaluationObject;
import com.twosigma.beakerx.symjamma.output.MarkdownNotebookOutput;
import com.twosigma.beakerx.symjamma.output.SVGImageNotebookOutput;

class SymjaMMACodeRunner implements Callable<TryResult> {
  private static final Logger LOGGER = LogManager.getLogger();

  public static final String SCRIPT_NAME = "script";
  SymjaMMAEvaluator symjammaEvaluator;
  private final String theCode;
  private final SimpleEvaluationObject theOutput;

  public SymjaMMACodeRunner(
      SymjaMMAEvaluator groovyEvaluator, String code, SimpleEvaluationObject out) {
    this.symjammaEvaluator = groovyEvaluator;
    theCode = code;
    theOutput = out;
  }

  private String createSVGOutput(IAST show) {
    StringBuilder svgData = new StringBuilder();
    if (show.isAST() && show.size() > 1 && show.first().isAST()) {
      StringBuilder buf = new StringBuilder(2048);
      GraphicsFunctions.graphicsToSVG((IAST) ((IAST) show).arg1(), svgData);
    }
    return svgData.toString();
  }

  public Object interpreter(
      ExprEvaluator fEvaluator, OutputFormFactory fOutputFactory, final String inputExpression) {
    IExpr result;
    final StringWriter buf = new StringWriter();
    try {
      String trimmedInput = inputExpression.trim();
      if (trimmedInput.length() >= 4 && trimmedInput.charAt(0) == '/') {
        Object meta = symjammaEvaluator.metaCommand(this, trimmedInput);
        if (meta != null) {
          return meta;
        }
      }
      if (symjammaEvaluator.fSeconds <= 0) {
        result = fEvaluator.eval(inputExpression);
      } else {
        result =
            fEvaluator.evaluateWithTimeout(
                inputExpression,
                symjammaEvaluator.fSeconds,
                TimeUnit.SECONDS,
                true,
                new EvalControlledCallable(fEvaluator.getEvalEngine()));
      }
      if (result != null) {
        if (result.equals(S.Null)) {
          return "Null";
        } else {
          return result;
        }
      }
    } catch (final Exception e) {
      LOGGER.error("SymjaMMACodeRunner.interpreter() failed", e);
    }
    return new MarkdownNotebookOutput(buf.toString());
  }

  @Override
  public TryResult call() {
    ClassLoader oldld = Thread.currentThread().getContextClassLoader();
    TryResult either;
    String scriptName = SCRIPT_NAME;
    try {
      Object result = null;
      theOutput.setOutputHandler();
      result = interpreter(symjammaEvaluator.fEvaluator, symjammaEvaluator.fOutputFactory, theCode);
      if (result instanceof IExpr) {
        if (result instanceof IStringX) {
          return TryResult.createResult(((IStringX) result).toString());
        } else if (((IExpr) result).isASTSizeGE(F.Show, 2)) {
          IAST show = (IAST) result;
          return TryResult.createResult(new SVGImageNotebookOutput(createSVGOutput(show)));
        } else {
          return symjammaEvaluator.printForm(this, result);
        }
      }
      return TryResult.createResult(result);

    } catch (Throwable e) {
      either = handleError(scriptName, e);
    } finally {
      theOutput.clrOutputHandler();
      Thread.currentThread().setContextClassLoader(oldld);
    }
    return either;
  }

  private TryResult handleError(String scriptName, Throwable e) {
    TryResult either;
    if (e instanceof InvocationTargetException) {
      e = ((InvocationTargetException) e).getTargetException();
    }

    if (e instanceof InterruptedException
        || e instanceof InvocationTargetException
        || e instanceof ThreadDeath) {
      either = TryResult.createError(INTERUPTED_MSG);
    } else {
      StringWriter sw = new StringWriter();
      // PrintWriter pw = new PrintWriter(sw);
      // StackTraceUtils.sanitize(e).printStackTrace(pw);
      String value = sw.toString();
      value = printStacktrace(scriptName, value);
      either = TryResult.createError(value);
    }
    return either;
  }

  // private Object runScript(Script script) {
  // symjammaEvaluator.getScriptBinding().setVariable(Evaluator.BEAKER_VARIABLE_NAME,
  // symjammaEvaluator.getBeakerX());
  // script.setBinding(symjammaEvaluator.getScriptBinding());
  // return script.run();
  // }

  private boolean canBeInstantiated(Class<?> parsedClass) {
    return !parsedClass.isEnum();
  }
}
