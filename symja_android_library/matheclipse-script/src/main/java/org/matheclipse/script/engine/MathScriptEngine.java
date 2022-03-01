package org.matheclipse.script.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import javax.script.AbstractScriptEngine;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apfloat.ApfloatRuntimeException;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.core.eval.exception.AbortException;
import org.matheclipse.core.eval.exception.FailedException;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.math.MathException;

public class MathScriptEngine extends AbstractScriptEngine {
  @Override
  public Object get(String key) {
    if ("EVAL_ENGINE".equals(key)) {
      return fEngine;
    }
    return super.get(key);
  }

  private static final Logger LOGGER = LogManager.getLogger();
  public static final String RETURN_OBJECT = "RETURN_OBJECT";
  private static volatile boolean initialized = false;

  private EvalUtilities fUtility = null;
  private EvalEngine fEngine;
  private String fDecimalFormat;

  public MathScriptEngine() {
    this(new EvalEngine(true));
  }

  /**
   * This constructor is needed for external JUnit tests
   * 
   * @param engine
   */
  @Deprecated
  public MathScriptEngine(EvalEngine engine) {
    fEngine = engine;
    if (!initialized) {
      initialized = true;
      try {
        // try if we can dynamically call method IOInit#init() from Maven module matheclipse-io
        Class<?> clazz = Class.forName("org.matheclipse.io.IOInit");
        Method initMethod = clazz.getDeclaredMethod("init");
        initMethod.invoke(null);
      } catch (ReflectiveOperationException e) {
        LOGGER.log(Level.INFO, "Method IOInit#init() not called.", e);
      }
    }
    fUtility = new EvalUtilities(fEngine, false, false);
    fEngine.setRecursionLimit(Config.DEFAULT_RECURSION_LIMIT);
    fEngine.setIterationLimit(Config.DEFAULT_ITERATION_LIMIT);
  }

  @Override
  public Bindings createBindings() {
    return null;
  }

  @Override
  public Object eval(final Reader reader, final ScriptContext context) throws ScriptException {
    final BufferedReader f = new BufferedReader(reader);
    final StringBuilder buff = new StringBuilder(1024);
    String line;
    try {
      while ((line = f.readLine()) != null) {
        buff.append(line);
        buff.append('\n');
      }
      return eval(buff.toString());
    } catch (final IOException e) {
      LOGGER.catching(e);
    }
    return null;
  }

  @Override
  public Object eval(final String script, final ScriptContext context) {
    boolean relaxedSyntax = false;
    final Object enableStackTraceBoolean = get("PRINT_STACKTRACE");
    Level stackLogLevel = Boolean.TRUE.equals(enableStackTraceBoolean) ? Level.ERROR : Level.DEBUG;

    try {
      // first assign the EvalEngine to the current thread:
      EvalEngine.setReset(fEngine);

      // final Bindings bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);
      // ISymbol symbol;
      // for (Map.Entry<String, Object> currEntry : bindings.entrySet()) {
      // symbol = F.userSymbol(currEntry.getKey(), fEngine);
      // symbol.pushLocalVariable(Object2Expr.convert(currEntry.getValue()));
      // list.add(symbol);
      // }

      final Object decimalFormat = get("DECIMAL_FORMAT");
      if (decimalFormat instanceof String) {
        fDecimalFormat = (String) decimalFormat;
      }

      final Object relaxedSyntaxBoolean = get("RELAXED_SYNTAX");
      if (Boolean.TRUE.equals(relaxedSyntaxBoolean)) {
        relaxedSyntax = true;
        fEngine.setRelaxedSyntax(relaxedSyntax);
      }

      boolean disableHistory = true;
      final Object enableHistoryBoolean = get("ENABLE_HISTORY");
      if (Boolean.TRUE.equals(enableHistoryBoolean)) {
        disableHistory = false;
        fEngine.setOutListDisabled(disableHistory, (short) 100);
      }

      // evaluate an expression
      final Object stepwise = get("STEPWISE");
      IExpr result;
      String trimmedScript = script.trim();
      if (trimmedScript.length() == 0) {
        return "";
      }
      if (Boolean.TRUE.equals(stepwise)) {
        result = fUtility.evalTrace(script, null);
      } else {
        result = fUtility.evaluate(script);
      }
      final Object returnType = context.getAttribute("RETURN_OBJECT");
      if ((returnType != null) && returnType.equals(Boolean.TRUE)) {
        // return the object "as is"
        return result;
      } else {
        // return the object as String representation
        if (result != null) {
          return printResult(result, relaxedSyntax);
        }
        return "";
      }

    } catch (final AbortException e) {
      LOGGER.log(stackLogLevel, "Aborted", e);
      return printResult(S.$Aborted, relaxedSyntax);
    } catch (final FailedException e) {
      LOGGER.log(stackLogLevel, "Failed", e);
      return printResult(S.$Failed, relaxedSyntax);
    } catch (final MathException e) { // catches parser errors as well
      LOGGER.log(stackLogLevel, "evaluation failed", e);
      return e.getMessage();
    } catch (final ApfloatRuntimeException e) {
      LOGGER.log(stackLogLevel, "ApFloat error", e);
      // catch parser errors here
      return "Apfloat: " + e.getMessage();
    } catch (final Exception e) {
      LOGGER.log(stackLogLevel, "Exception", e);
      return "Exception: " + e.getMessage();
    } catch (final OutOfMemoryError e) {
      LOGGER.log(stackLogLevel, "Out of memory", e);
      return "OutOfMemoryError";
    } catch (final StackOverflowError e) {
      LOGGER.log(stackLogLevel, "Stack overflow", e);
      return "StackOverflowError";
    } finally {
      // if (list.size() > 0) {
      // for (int i = 0; i < list.size(); i++) {
      // list.get(i).popLocalVariable();
      // }
      // }
      EvalEngine.remove();
    }
  }

  private String printResult(IExpr result, boolean relaxedSyntax) {
    if (result.equals(S.Null)) {
      return "";
    }
    final StringWriter buf = new StringWriter();
    EvalEngine engine = EvalEngine.get();
    OutputFormFactory off;

    if (fDecimalFormat != null) {
      int significantFigures = engine.getSignificantFigures();
      off = OutputFormFactory.get(relaxedSyntax, false, significantFigures - 1,
          significantFigures + 1);
    } else {
      off = OutputFormFactory.get(relaxedSyntax);
    }
    if (off.convert(buf, result)) {
      // print the result in the console
      return buf.toString();
    }
    if (Config.FUZZ_TESTING) {
      throw new NullPointerException();
    }
    return "ScriptEngine: ERROR-IN-OUTPUTFORM";
  }

  @Override
  public ScriptEngineFactory getFactory() {
    return new MathScriptEngineFactory();
  }
}
