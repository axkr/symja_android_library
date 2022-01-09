package org.matheclipse.script.engine;

import java.util.List;
import javax.script.ScriptEngine;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.expression.F;

public class MathScriptEngineFactory implements javax.script.ScriptEngineFactory {

  static {
    F.initSymbols();
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
    return List.of("m", "nb");
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
    return obj + "." + m + "(" + String.join(", ", args) + ")";
  }

  @Override
  public List<String> getMimeTypes() {
    return List.of("code/matheclipse");
  }

  @Override
  public List<String> getNames() {
    return List.of("matheclipse script");
  }

  @Override
  public String getOutputStatement(final String toDisplay) {
    return "Print[" + toDisplay + "]";
  }

  @Override
  public Object getParameter(final String key) {
    switch (key) {
      case ScriptEngine.ENGINE:
        return getEngineName();
      case ScriptEngine.ENGINE_VERSION:
        return getEngineVersion();
      case ScriptEngine.NAME:
        return getNames();
      case ScriptEngine.LANGUAGE:
        return getLanguageName();
      case ScriptEngine.LANGUAGE_VERSION:
        return getLanguageVersion();
      default:
        return null;
    }
  }

  @Override
  public String getProgram(String... statements) {
    return String.join(";\n", statements) + "\n";
  }

  @Override
  public ScriptEngine getScriptEngine() {
    return new MathScriptEngine();
  }
}
