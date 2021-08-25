package org.matheclipse.script.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;

import javax.script.AbstractScriptEngine;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;

import org.apfloat.ApfloatRuntimeException;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.core.eval.exception.AbortException;
import org.matheclipse.core.eval.exception.FailedException;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;

public class MathScriptEngine extends AbstractScriptEngine {
  public static final String RETURN_OBJECT = "RETURN_OBJECT";

  private EvalUtilities fUtility;
  private EvalEngine fEngine;
  private String fDecimalFormat;

  // static {
  // run the static groovy code for the MathEclipse domain specific language
  // DSL groovyDSL = new DSL();
  // }

  public MathScriptEngine() {
    this(new EvalEngine());
  }

  public MathScriptEngine(EvalEngine engine) {
    // get the thread local evaluation engine
    fEngine = engine;
    // fEngine.setRecursionLimit(256);
    // fEngine.setIterationLimit(256);
    fUtility = new EvalUtilities(fEngine, false, false);
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
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public Object eval(final String script, final ScriptContext context) throws ScriptException {
    // final ArrayList<ISymbol> list = new ArrayList<ISymbol>();
    boolean relaxedSyntax = false;
    boolean showStackTrace = Config.SHOW_STACKTRACE;
    final Object enableStackTraceBoolean = get("PRINT_STACKTRACE");
    if (Boolean.TRUE.equals(enableStackTraceBoolean)) {
      showStackTrace = true;
    }

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
      if (showStackTrace) {
        e.printStackTrace();
      }
      try {
        return printResult(S.$Aborted, relaxedSyntax);
      } catch (IOException e1) {
        if (showStackTrace) {
          e.printStackTrace();
        }
        return e1.getMessage();
      }
    } catch (final FailedException e) {
      if (showStackTrace) {
        e.printStackTrace();
      }
      try {
        return printResult(S.$Failed, relaxedSyntax);
      } catch (IOException e1) {
        if (Config.DEBUG) {
          e.printStackTrace();
        }
        return e1.getMessage();
      }
    } catch (final SyntaxError e) {
      if (showStackTrace) {
        e.printStackTrace();
      }
      // catch parser errors here
      return e.getMessage();
    } catch (final MathException e) {
      if (showStackTrace) {
        e.printStackTrace();
      }
      return e.getMessage();
    } catch (final ApfloatRuntimeException e) {
      if (showStackTrace) {
        e.printStackTrace();
      }
      // catch parser errors here
      return "Apfloat: " + e.getMessage();
    } catch (final Exception e) {
      // if (e instanceof ExceptionContextProvider) {
      // if (Config.DEBUG) {
      // e.printStackTrace();
      // }
      // return e.getMessage();
      // }
      if (showStackTrace) {
        e.printStackTrace();
      }
      return "Exception: " + e.getMessage();
    } catch (final OutOfMemoryError e) {
      if (showStackTrace) {
        e.printStackTrace();
      }
      return "OutOfMemoryError";
    } catch (final StackOverflowError e) {
      if (showStackTrace) {
        e.printStackTrace();
      }
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

  private String printResult(IExpr result, boolean relaxedSyntax) throws IOException {
    if (result.equals(S.Null)) {
      return "";
    }
    final StringWriter buf = new StringWriter();
    EvalEngine engine = EvalEngine.get();
    OutputFormFactory off;

    if (fDecimalFormat != null) {
      int significantFigures = engine.getSignificantFigures();
      off =
          OutputFormFactory.get(
              relaxedSyntax, false, significantFigures - 1, significantFigures + 1);
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
