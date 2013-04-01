package org.matheclipse.core.system;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import junit.framework.TestCase;

import org.matheclipse.core.eval.SystemNamespace;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.script.engine.MathScriptEngine;

public class ScriptEngineTestCase extends TestCase {
  public ScriptEngineTestCase() {
    super("ScriptEngineTestCase");
  }

  public void testScriptEngine() {

    SystemNamespace.DEFAULT.add("org.matheclipse.groovy.system");
    // SystemNamespace.DEFAULT.add("org.matheclipse.scala.system");
    SystemNamespace.DEFAULT.add("org.matheclipse.script.reflection");
    ScriptEngineManager scriptManager = new ScriptEngineManager();

    String stringResult = null;
    ScriptEngine engine_1 = scriptManager.getEngineByExtension("m");
    ScriptEngine engine_2 = scriptManager.getEngineByExtension("m");
    try {
      stringResult = (String) engine_1.eval("D[Sin[x]*Cos[x],x]");
      assertEquals("-Sin[x]^2+Cos[x]^2", stringResult);
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    try {
      stringResult = (String) engine_1.eval("Expand[(x+5)^3]");
      assertEquals("x^3+15*x^2+75*x+125", stringResult);
      stringResult = (String) engine_1.eval("Factor[" + stringResult + "]");
      assertEquals("(x+5)^3", stringResult);
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    try {
      engine_1.put("$x", new Boolean(true));
      engine_1.put("$y", new Boolean(true));
      stringResult = (String) engine_1.eval("$x && $y");
      assertEquals("True", stringResult);

      stringResult = (String) engine_2.eval("$x && $y");
      assertEquals("$x&&$y", stringResult);

      stringResult = (String) engine_1.eval("$x && $y");
      assertEquals("True", stringResult);

      engine_2.put("$x", new Boolean(false));
      stringResult = (String) engine_2.eval("$x && $y");
      assertEquals("False", stringResult);

    } catch (Exception ex) {
      ex.printStackTrace();
    }

    try {
      stringResult = (String) engine_1.eval("MyDepth[Sin[x]*Cos[x]]");
      assertEquals("3", stringResult);
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    try {// a script file test
      ArrayList<Object> row = new ArrayList<Object>();
      row.add("List"); // head of the expression
      row.add(Integer.valueOf(1));
      row.add(Integer.valueOf(2));
      row.add(Integer.valueOf(3));

      int[] intArr = { 3, 4, 11 };

      engine_1.put("$x", row);
      engine_1.put("$y", intArr);
      // the test.m file contains this script for matrix multiplication:
      // $m={$x, $y, {13, 7, 8}};
      // $m.$m

      ScriptContext context = engine_1.getContext();
      context.setAttribute(MathScriptEngine.RETURN_OBJECT, Boolean.TRUE,
          ScriptContext.ENGINE_SCOPE);
      Object objectResult = engine_1
          .eval(new FileReader(
              "C:\\galileo\\workspace\\symja\\matheclipse-core\\src\\test\\java\\test.m"));
      // print result for matrix multiplication: {{1,2,3}, {3, 4, 11},
      // {13, 7,
      // 8}}.{{1,2,3}, {3, 4, 11}, {13, 7, 8}}
      assertEquals("{{46, 31, 49}, {158, 99, 141}, {138, 110, 180}}",
          objectResult.toString());
      if (objectResult instanceof IExpr) {
        // decompose the matrix into rows
        IExpr expr = (IExpr) objectResult;
        // gives the head "List", because matrices are list of row-lists
        assertEquals("List", expr.head().toString());

        if (expr instanceof List) {
          // use java.util.List to print the rows
          List<IExpr> list = (List<IExpr>) expr;
          for (IExpr subExpr : list) {
            System.out.println(subExpr);
          }
          IExpr subExpr;
          // there's a difference between foreach and for loop
          // because the head is stored at index 0:
          for (int i = 0; i < list.size(); i++) {
            subExpr = list.get(i);
            System.out.println(subExpr);
          }
        }

        if (expr instanceof IAST) {
          // use org.matheclipse.core.interfaces.IAST to print the
          // rows
          IAST list = (IAST) expr;
          for (IExpr subExpr : list) {
            System.out.println(subExpr);
          }
          IExpr subExpr;
          // there's a difference between foreach and for loop
          // because the head is stored at index 0:
          for (int i = 0; i < list.size(); i++) {
            subExpr = list.get(i);
            System.out.println(subExpr);
          }
        }

      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }

  }
}
