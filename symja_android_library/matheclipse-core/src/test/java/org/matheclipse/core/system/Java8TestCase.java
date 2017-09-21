package org.matheclipse.core.system;

import static org.matheclipse.core.expression.F.*;

import java.util.function.Consumer;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Tests for the Java port of the
 * <a href="http://www.apmaths.uwo.ca/~arich/">Rubi - rule-based integrator</a>.
 * 
 */
public class Java8TestCase extends AbstractTestCase {
	public Java8TestCase(String name) {
		super(name);
	}

	public void testForeach() {
		IAST ast = List(C10, a, b, c, d, e);
		Consumer<IExpr> action = (IExpr x) -> System.out.println(x);
		ast.forEach(action); 
	}
	
	public void testStream001() {
		IAST ast = List(C10, a, b, c, d, e);
		Consumer<IExpr> action = (IExpr x) -> System.out.println(x);
		ast.stream().forEach(action); 
		ast.stream(0,7).forEach(action);
	}

}
