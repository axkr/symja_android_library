package org.matheclipse.core.system;

import java.util.HashMap;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import junit.framework.TestCase;

public class FindRootScriptTestCase extends TestCase {
	private static final ScriptEngineManager scriptManager = new ScriptEngineManager();

	public FindRootScriptTestCase(String name) {
		super(name);
	}

	public void testEvalNewton() {

		ScriptEngine meEngine = scriptManager.getEngineByExtension("m");
		HashMap<String, Double> map = new HashMap<String, Double>();

		map.clear();
		map.put("$K", Double.valueOf(10000));
		map.put("$g", Double.valueOf(0.0));
		map.put("$n", Double.valueOf(120.0));
		map.put("$Z", Double.valueOf(12.0));
		map.put("$R", Double.valueOf(100.0));
		map.put("$AA", Double.valueOf(0.0526));
		map.put("$d", Double.valueOf(0.0));
		map.put("$vn", Double.valueOf(0.0));
		map.put("$EAj", Double.valueOf(0.0));
		map.put("$zj", Double.valueOf(0.0)); 
		map.put("$sz", Double.valueOf(0.0));
		String result = eval(meEngine,"FindRoot[Exp[$x]==Pi^3,{$x,-1,10}]", map);
		assertEquals(result, "{$x->3.4341896575482007}");
		result = eval(meEngine,
				"FindRoot[(($K*(1+p-$g)^($n/$Z))/(1+$AA))+(Sum[(($R*(1+$d)^(Floor[i/$Z]))/(1+$AA))*(1+p-$g)^(($n-i-$vn)/$Z),{i,0,$n-1}])+(Sum[($EAj*(1+p-$g)^(($n-$zj)/$Z))/(1+$AA),{j,1,$sz}]) - 30199, {p, 0, 0.1}]",
				map);
		assertEquals(result, "{p->0.049997093938224005}");
		map.clear();
		map.put("$g", Double.valueOf(0.0));
		map.put("$n", Double.valueOf(120.0));
		map.put("$Z", Double.valueOf(12.0));
		map.put("$AA", Double.valueOf(0.0526));
		map.put("$res", Double.valueOf(15474));
		result = eval(meEngine,"FindRoot[(($K*(1+p-$g)^($n/$Z))/(1+$AA)) - $res, {p, 0, 0.1}]", map);
		assertEquals(result, "{p->0.04999346433486659}");
	}

	public void testEvalBisection() {

		ScriptEngine meEngine = scriptManager.getEngineByExtension("m");
		HashMap<String, Double> map = new HashMap<String, Double>();

		map.clear();
		map.put("$K", Double.valueOf(10000));
		map.put("$g", Double.valueOf(0.0));
		map.put("$n", Double.valueOf(120.0));
		map.put("$Z", Double.valueOf(12.0));
		map.put("$R", Double.valueOf(100.0));
		map.put("$AA", Double.valueOf(0.0526));
		map.put("$d", Double.valueOf(0.0));
		map.put("$vn", Double.valueOf(0.0));
		map.put("$EAj", Double.valueOf(0.0));
		map.put("$zj", Double.valueOf(0.0)); 
		map.put("$sz", Double.valueOf(0.0));
		String result = eval(meEngine,"FindRoot[Exp[$x]==Pi^3,{$x,-1,10},Bisection]", map);
		assertEquals(result, "{$x->3.434189647436142}");
		result = eval(meEngine,
				"FindRoot[(($K*(1+p-$g)^($n/$Z))/(1+$AA))+(Sum[(($R*(1+$d)^(Floor[i/$Z]))/(1+$AA))*(1+p-$g)^(($n-i-$vn)/$Z),{i,0,$n-1}])+(Sum[($EAj*(1+p-$g)^(($n-$zj)/$Z))/(1+$AA),{j,1,$sz}]) - 30199, {p, 0, 0.1},Bisection]",
				map);
		assertEquals(result, "{p->0.04999732971191407}");
		map.clear();
		map.put("$g", Double.valueOf(0.0));
		map.put("$n", Double.valueOf(120.0));
		map.put("$Z", Double.valueOf(12.0));
		map.put("$AA", Double.valueOf(0.0526));
		map.put("$res", Double.valueOf(15474));
		result = eval(meEngine,"FindRoot[(($K*(1+p-$g)^($n/$Z))/(1+$AA)) - $res, {p, 0, 0.1},Bisection]", map);
		assertEquals(result, "{p->0.04999351501464844}");
	}
	
	public String eval(ScriptEngine meEngine,String formula, Map<String, Double> arguments) {

		Bindings bindings = meEngine.getBindings(ScriptContext.ENGINE_SCOPE);

		// bef�lle ScriptEngine Argumente
		bindings.putAll(arguments);

		// Evaluiere Argumente gegen die Formel
		String result = "";
		try {
			result = (String) meEngine.eval(formula);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
		return result;
	}
}
