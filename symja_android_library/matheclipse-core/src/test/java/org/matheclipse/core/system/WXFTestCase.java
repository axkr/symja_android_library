package org.matheclipse.core.system;

/**
 * Tests for the Java port of the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi - rule-based integrator</a>.
 * 
 */
public class WXFTestCase extends AbstractTestCase {
	public WXFTestCase(String name) {
		super(name);
	}

	public void testBinarySerialize() {
		check("BinarySerialize(42)", //
				"{56,58,67,42}");
		check("BinarySerialize(-42)", //
				"{56,58,67,214}");
		check("BinarySerialize(Plot)", //
				"{56,58,115,4,80,108,111,116}");
		check("BinarySerialize(\"hello!\")", //
				"{56,58,83,6,104,101,108,108,111,33}");
		check("BinarySerialize({})", //
				"{56,58,102,0,115,4,76,105,115,116}");
		check("BinarySerialize(f( ))", //
				"{56,58,102,0,115,8,71,108,111,98,97,108,96,102}");
		check("BinarySerialize(f(g))", //
				"{56,58,102,1,115,8,71,108,111,98,97,108,96,102,115,8,71,108,111,98,97,108,96,103}");
		check("BinarySerialize(f(g(x,y)))", //
				"{56,58,102,1,115,8,71,108,111,98,97,108,96,102,102,2,115,8,71,108,111,98,97,108,\n"
						+ "96,103,115,8,71,108,111,98,97,108,96,120,115,8,71,108,111,98,97,108,96,121}");
		check("BinarySerialize(f(g,2))", //
				"{56,58,102,2,115,8,71,108,111,98,97,108,96,102,115,8,71,108,111,98,97,108,96,103,\n" + 
				"67,2}");
	}
	
	public void testBinaryDeserialize() {
		check("BinaryDeserialize({56,58,67,42})", //
				"42");
		check("BinaryDeserialize({56,58,67,214})", //
				"-42");
		check("BinaryDeserialize({56,58,115,4,80,108,111,116})", //
				"Plot");
		check("BinaryDeserialize({56,58,83,6,104,101,108,108,111,33})", //
				"hello!");
		check("BinaryDeserialize({56,58,102,0,115,4,76,105,115,116})", //
				"{}");
		check("BinaryDeserialize({56,58,102,0,115,8,71,108,111,98,97,108,96,102})", //
				"f()");
		check("BinaryDeserialize({56,58,102,1,115,8,71,108,111,98,97,108,96,102,115,8,71,108,111,98,97,108,96,103})", //
				"f(g)");
		check("BinaryDeserialize({56,58,102,1,115,8,71,108,111,98,97,108,96,102,102,2,115,8,71,108,111,98,97,108,96,103,115,8,71,108,111,98,97,108,96,120,115,8,71,108,111,98,97,108,96,121})", //
				"f(g(x,y))");
		check("BinaryDeserialize({56,58,102,2,115,8,71,108,111,98,97,108,96,102,115,8,71,108,111,98,97,108,96,103,67,2})", //
				"f(g,2)");
	}
}
