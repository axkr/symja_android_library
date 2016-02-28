package org.matheclipse.script.engine;

import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngine;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.expression.F;

public class MathScriptEngineFactory implements javax.script.ScriptEngineFactory {

	public MathScriptEngineFactory() {
		F.initSymbols(null, null, false);
		Config.SERVER_MODE = false;
	}

	public String getEngineName() {
		return "MathEclipse Script Engine";
	}

	public String getEngineVersion() {
		return "1.0.0";
	}

	public List<String> getExtensions() {
		final ArrayList<String> extList = new ArrayList<String>();
		extList.add("m");
		extList.add("nb");
		return extList;
	}

	public String getLanguageName() {
		return "MathEclipse Script Language";
	}

	public String getLanguageVersion() {
		return "1.0.0";
	}

	public String getMethodCallSyntax(String obj, String m, String... args) {
		String callSyntax = obj;
		callSyntax += "." + m + "(";
		for (int i = 0; i < args.length; i++) {
			callSyntax += args[i];
			if (i < args.length - 2) {
				callSyntax += ", ";
			}
		}
		callSyntax += ")";
		return callSyntax;
	}

	public List<String> getMimeTypes() {
		final ArrayList<String> extList = new ArrayList<String>();
		extList.add("code/matheclipse");
		return extList;
	}

	public List<String> getNames() {
		final ArrayList<String> extList = new ArrayList<String>();
		extList.add("matheclipse script");
		return extList;
	}

	public String getOutputStatement(final String toDisplay) {
		return "Print[" + toDisplay + "]";
	}

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

	public String getProgram(String... statements) {
		final StringBuffer retval = new StringBuffer();
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

	public ScriptEngine getScriptEngine() {
		return new MathScriptEngine();
	}

}
