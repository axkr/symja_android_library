package org.matheclipse.core.system;

import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.form.output.StringBufferWriter;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.ast.ASTNode;

public class EvalEngineTestCase extends SpecialTestCase {

	public EvalEngineTestCase(String name) {
		super(name);
	}

	public static IExpr dynamicMethodCall(IAST ast) {
		StringBuilder buf = new StringBuilder();
		for (IExpr expr : ast) {
			buf.append(expr.toString());
			buf.append(' ');
		}
		return F.stringx(buf.toString());
	}

	public void testDynamicMethodCall() {
		ISymbol sym = F.method("__TEST__", "org.matheclipse.core.system", "EvalEngineTestCase", "dynamicMethodCall");
		IExpr result = F.eval(F.$(sym, F.C1, F.C0));
		assertEquals(result.toString(), "1 0 ");
	}

	public void testEvalTrace() {
		try {

			EvalEngine engine = EvalEngine.get();
			Parser parser = new Parser();
			ASTNode node = parser.parse("(3*x)^2 + (5*x)^2");
			// convert ASTNode to an IExpr node
			IExpr expr = AST2Expr.CONST.convert(node);
			IExpr result = null;

			StringBufferWriter buf;
			if (expr != null) {
				// Step 1
				result = engine.evalTrace(expr, null, F.List());
				if (result != null) {
					buf = new StringBufferWriter();
					buf.setIgnoreNewLine(true);
					// convert the result back to output form
					OutputFormFactory.get().convert(buf, result);
					assertEquals(buf.toString(), "{{(3*x)^2,3^2*x^2,{3^2,9},9*x^2},{(5*x)^2,5^2*x^2,{5^2,25},25*x^2},25*x^2+9*x^2,(25+9)*Times[x^2],{25+9,34},34*x^2}");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Test extension with Package[] function
	 */
	public void testPackage001() {
		check("Package[ \"PackageName\", {Test}, {\n" + "   Test[x_,y_]:=TestIntern[x,y],\n"
				+ "   TestIntern[x_,y_] := {x,y,mySymbol}, \n" + "   mySymbol = 4711 \n" + " } ]", "");
		check("Test[a,b]", "{a,b,4711}");
		// the TestIntern rule should not be visible outside the package:
		check("TestIntern[a,b]", "TestIntern[a,b]");
		// the mySymbol constant should not be visible outside the package:
		check("mySymbol", "mySymbol");
		check("Test[a,b]", "{a,b,4711}");
		// a new EvalEngine sees the new defined rules:
		EvalEngine engine = new EvalEngine();
		check(engine, false, "Test[a,b]", "{a,b,4711}", false);
		// this should give an syntax error:
		// check("@1TestIntern[a,b]","");
	}

}
