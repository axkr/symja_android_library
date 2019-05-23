package org.matheclipse.core.system;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Test F.symjify()
 */
public class SymjifyTestCase extends AbstractTestCase {
	public SymjifyTestCase(String name) {
		super(name);
	}

	public void test001() {
		IExpr expr = F.symjify("(a+(b+c))");
		assertEquals(expr.fullFormString(), "Plus(a, b, c)");
		assertEquals(expr.toString(), "a+b+c");
	}
	
	public void test002() {
		IExpr expr = F.symjify(new int[] {1,2,3});
		assertEquals(expr.fullFormString(), "List(1, 2, 3)");
		assertEquals(expr.toString(), "{1,2,3}");
	} 

	public void test003() {
		IExpr expr = F.symjify(new double[] {1.0,2.1,3.5});
		assertEquals(expr.fullFormString(), "List(1.0, 2.1, 3.5)");
		assertEquals(expr.toString(), "{1.0,2.1,3.5}");
	} 
	
	public void test004() {
		IExpr expr = F.symjify(new double[][] {{1.0,2.1,3.5},{1.1,2.2,3.6}});
		assertEquals(expr.fullFormString(), "$realmatrix(List(1.0, 2.1, 3.5), List(1.1, 2.2, 3.6))");
		assertEquals(expr.toString(), "\n{{1.0,2.1,3.5},\n" + 
				" {1.1,2.2,3.6}}");
	} 
	
	public void test005() {
		IExpr expr = F.symjify(new boolean[][] {{true,false},{false,true}});
		assertEquals(expr.fullFormString(), "List(List(True, False), List(False, True))");
		assertEquals(expr.toString(), "{{True,False},{False,True}}");
	} 
}
