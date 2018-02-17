package org.matheclipse.core.eval.util;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.ISymbol;

import junit.framework.TestCase;

public class ArraySetTest extends TestCase {

	public ArraySetTest(String name) {
		super(name);
	}

	public void test001() {
		ArraySet<ISymbol> set = new ArraySet<ISymbol>();

		assertTrue(set.isEmpty());
		set.add(F.z);
		assertFalse(set.isEmpty());
		set.add(F.x);
		assertFalse(set.isEmpty());
		set.add(F.y);
		assertTrue(set.contains(F.x));
		assertFalse(set.contains(F.a));
		
		assertEquals(F.x, set.get(0));
		assertEquals(F.y, set.get(1));
		assertEquals(F.z, set.get(2));
		assertEquals(null, set.get(3));
		
		for (ISymbol iSymbol : set) {
			System.out.println(iSymbol.toString());
		}
	}
}
