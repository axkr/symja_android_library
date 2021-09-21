package org.matheclipse.script.engine;

import java.util.ArrayList;
import java.util.List;
import javax.script.ScriptEngine;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.expression.F;

public class MathScriptEngineFactory implements javax.script.ScriptEngineFactory {

  static {
    F.initSymbols(null, null, true);
  }

  public MathScriptEngineFactory() {
    Config.SERVER_MODE = false;
  }

  @Override
  public String getEngineName() {
    return "MathEclipse Script Engine";
  }

  @Override
  public String getEngineVersion() {
    return "1.0.0";
  }

  @Override
  public List<String> getExtensions() {
    final ArrayList<String> extList = new ArrayList<String>();
    extList.add("m");
    extList.add("nb");
    return extList;
  }

  @Override
  public String getLanguageName() {
    return "MathEclipse Script Language";
  }

  @Override
  public String getLanguageVersion() {
    return "1.0.0";
  }

  @Override
  public String getMethodCallSyntax(String obj, String m, String... args) {
    StringBuilder callSyntax = new StringBuilder(obj);
    callSyntax.append(".").append(m).append("(");
    for (int i = 0; i < args.length; i++) {
      callSyntax.append(args[i]);
      if (i < args.length - 2) {
        callSyntax.append(", ");
      }
    }
    callSyntax.append(")");
    return callSyntax.toString();
  }

  @Override
  public List<String> getMimeTypes() {
    final ArrayList<String> extList = new ArrayList<String>();
    extList.add("code/matheclipse");
    return extList;
  }

  @Override
  public List<String> getNames() {
    final ArrayList<String> extList = new ArrayList<String>();
    extList.add("matheclipse script");
    return extList;
  }

  @Override
  public String getOutputStatement(final String toDisplay) {
    return "Print[" + toDisplay + "]";
  }

  @Override
  public Object getParameter(final String key) {
    if (key.equals(ScriptEngine.ENGINE)) {
      return getEngineName();
    } else if (key.equals(ScriptEngine.ENGINE_VERSION)) {
      return getEngineVersion();
    } else if (key.equals(ScriptEngine.NAME)) {
      return getNames();
    } else if (key.equals(ScriptEngine.LANGUAGE)) {
      return getLanguageName();
    } else if (key.equals(ScriptEngine.LANGUAGE_VERSION)) {
      return getLanguageVersion();
    } else {
      return null;
    }
  }

  @Override
  public String getProgram(String... statements) {
    final StringBuilder retval = new StringBuilder();
    final int len = statements.length;
    for (int i = 0; i < len; i++) {
      if (i == len - 1) {
        retval.append(statements[i] + "\n");
      } else {
        retval.append(statements[i] + ";\n");
      }
    }
    return retval.toString();
  }

  @Override
  public ScriptEngine getScriptEngine() {
    return new MathScriptEngine();
  }
}
