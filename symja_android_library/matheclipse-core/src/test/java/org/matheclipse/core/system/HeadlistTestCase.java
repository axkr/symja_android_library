package org.matheclipse.core.system;

import junit.framework.TestCase;

import org.matheclipse.core.expression.AST;
import org.matheclipse.core.expression.IntegerSym;
import org.matheclipse.core.expression.Symbol;
import org.matheclipse.core.interfaces.IAST;

public class HeadlistTestCase  extends TestCase {

	public void testMap(){
		IAST lst = AST.newInstance(null);
		lst.setHeader(new Symbol("f"));
		assertEquals(lst.toString(), "f[]");

		lst.add(IntegerSym.valueOf(2));
		assertEquals(lst.toString(), "f[2]");

		lst.add(new Symbol("a"));
		lst.add(new Symbol("b"));
		assertEquals(lst.toString(), "f[2, a, b]");

//		IAST lstMap = lst.map(new Symbol("g"));
//		assertEquals(lstMap.toString(), "f[g[2], g[a], g[b]]");


		IAST lstApply = lst.apply(new Symbol("Times"));
		assertEquals(lstApply.toString(), "Times[2, a, b]");
	}

}
