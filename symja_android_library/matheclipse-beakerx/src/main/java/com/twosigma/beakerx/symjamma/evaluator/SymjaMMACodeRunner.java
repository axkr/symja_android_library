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

import com.twosigma.beakerx.TryResult;
import com.twosigma.beakerx.evaluator.Evaluator;
import com.twosigma.beakerx.jvm.object.SimpleEvaluationObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.matheclipse.core.eval.EvalControlledCallable;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.eval.exception.AbortException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;

import static com.twosigma.beakerx.evaluator.BaseEvaluator.INTERUPTED_MSG;
import static com.twosigma.beakerx.symjamma.evaluator.SymjaMMAStackTracePrettyPrinter.printStacktrace;

class SymjaMMACodeRunner implements Callable<TryResult> {

  public static final String SCRIPT_NAME = "script";
  private SymjaMMAEvaluator symjammaEvaluator;
  private final String theCode;
  private final SimpleEvaluationObject theOutput;

  public SymjaMMACodeRunner(SymjaMMAEvaluator groovyEvaluator, String code, SimpleEvaluationObject out) {
    this.symjammaEvaluator = groovyEvaluator;
    theCode = code;
    theOutput = out;
  }
  public String interpreter(ExprEvaluator fEvaluator, OutputFormFactory fOutputFactory, final String inputExpression) {
		IExpr result;
		final StringWriter buf = new StringWriter();
		try {
//			if (fSeconds <= 0) {
//				result = fEvaluator.eval(inputExpression);
//			} else {
				result = fEvaluator.evaluateWithTimeout(inputExpression, 60, TimeUnit.SECONDS, true,
						new EvalControlledCallable(fEvaluator.getEvalEngine()));
//			}
			if (result != null) {
//				return printResult(result);
				StringBuilder strBuffer = new StringBuilder();
				fOutputFactory.reset();
				fOutputFactory.convert(strBuffer, result);
				return strBuffer.toString();
			}
		} catch (final AbortException re) {
			re.printStackTrace();
//			try {
//				return printResult(F.$Aborted);
//			} catch (IOException e) {
//				Validate.printException(buf, e);
//				stderr.println(buf.toString());
//				stderr.flush();
//				return "";
//			}
		} catch (final SyntaxError se) {
			se.printStackTrace();
//			String msg = se.getMessage();
//			stderr.println(msg);
//			stderr.println();
//			stderr.flush();
//			return "";
		} catch (final RuntimeException re) {
			re.printStackTrace();
//			Throwable me = re.getCause();
//			if (me instanceof MathException) {
//				Validate.printException(buf, me);
//			} else {
//				Validate.printException(buf, re);
//			}
//			stderr.println(buf.toString());
//			stderr.flush();
//			return "";
		} catch (final Exception e) {
			e.printStackTrace();
//			Validate.printException(buf, e);
//			stderr.println(buf.toString());
//			stderr.flush();
//			return "";
//		} catch (final OutOfMemoryError e) {
//			Validate.printException(buf, e);
//			stderr.println(buf.toString());
//			stderr.flush();
//			return "";
//		} catch (final StackOverflowError e) {
//			Validate.printException(buf, e);
//			stderr.println(buf.toString());
//			stderr.flush();
//			return "";
		}
		return buf.toString();
	}
  
  @Override
  public TryResult call() {
    ClassLoader oldld = Thread.currentThread().getContextClassLoader();
    TryResult either;
    String scriptName = SCRIPT_NAME;
    try {
      Object result = null;
      theOutput.setOutputHandler();
//      Thread.currentThread().setContextClassLoader(symjammaEvaluator.getGroovyClassLoader());
//      scriptName += System.currentTimeMillis();
//      Class<?> parsedClass = interpreter(theCode);
//      if (canBeInstantiated(parsedClass)) {
//        Object instance = parsedClass.newInstance();
//        if (instance instanceof Script) {
//          result = runScript((Script) instance);
//        }
//      }
      result = interpreter(symjammaEvaluator.fEvaluator, symjammaEvaluator.fOutputFactory, theCode);
      either = TryResult.createResult(result);
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

    if (e instanceof InterruptedException || e instanceof InvocationTargetException || e instanceof ThreadDeath) {
      either = TryResult.createError(INTERUPTED_MSG);
    } else {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
//      StackTraceUtils.sanitize(e).printStackTrace(pw);
      String value = sw.toString();
      value = printStacktrace(scriptName, value);
      either = TryResult.createError(value);
    }
    return either;
  }

//  private Object runScript(Script script) {
//    symjammaEvaluator.getScriptBinding().setVariable(Evaluator.BEAKER_VARIABLE_NAME, symjammaEvaluator.getBeakerX());
//    script.setBinding(symjammaEvaluator.getScriptBinding());
//    return script.run();
//  }

  private boolean canBeInstantiated(Class<?> parsedClass) {
    return !parsedClass.isEnum();
  }

}
