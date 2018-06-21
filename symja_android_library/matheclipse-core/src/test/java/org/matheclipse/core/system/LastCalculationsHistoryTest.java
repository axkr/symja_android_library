package org.matheclipse.core.system;

import junit.framework.TestCase;

import org.matheclipse.core.eval.LastCalculationsHistory;
import org.matheclipse.core.expression.F;

public class LastCalculationsHistoryTest extends TestCase {

	public LastCalculationsHistoryTest(String name) {
		super(name);
	}

	public void testSystem001() {
		LastCalculationsHistory history = new LastCalculationsHistory(10);

		assertEquals(history.entry(-1), F.NIL);
		assertEquals(history.entry(9), F.NIL);
		history.add(F.ZZ(1));
		history.add(F.ZZ(2));
		history.add(F.ZZ(3));
		assertEquals(history.entry(0), F.NIL);
		assertEquals(history.entry(-1).toString(), "3");
		assertEquals(history.entry(3).toString(), "3");
		assertEquals(history.entry(12), F.NIL);
		assertEquals(history.entry(-12), F.NIL);
		history.add(F.ZZ(4));
		history.add(F.ZZ(5));
		history.add(F.ZZ(6));
		history.add(F.ZZ(7));
		history.add(F.ZZ(8));
		history.add(F.ZZ(9));
		history.add(F.ZZ(10));
		assertEquals(history.entry(-1).toString(), "10");
		assertEquals(history.entry(10).toString(), "10");
		history.add(F.ZZ(11));
		assertEquals(history.entry(-1).toString(), "11");
		assertEquals(history.entry(11).toString(), "11");
		assertEquals(history.entry(-2).toString(), "10");
		assertEquals(history.entry(10).toString(), "10");
		assertEquals(history.entry(-5).toString(), "7");
		assertEquals(history.entry(5).toString(), "5");
		history.add(F.ZZ(12));
		history.add(F.ZZ(13));
		history.add(F.ZZ(14));
		history.add(F.ZZ(15));
		history.add(F.ZZ(16));
		history.add(F.ZZ(17));
		assertEquals(history.entry(-1).toString(), "17");
		assertEquals(history.entry(11).toString(), "11");
		history.add(F.ZZ(18));
		history.add(F.ZZ(19));
		history.add(F.ZZ(20));
		assertEquals(history.entry(-1).toString(), "20");
		assertEquals(history.entry(11).toString(), "11");
		assertEquals(history.entry(10), F.NIL);
		history.add(F.ZZ(21));
		assertEquals(history.entry(-4).toString(), "18");
		assertEquals(history.entry(11), F.NIL);
		assertEquals(history.entry(10), F.NIL);
		history.add(F.ZZ(22));
		assertEquals(history.entry(22).toString(), "22");

	}
}
