package org.matheclipse.core.system;

import org.matheclipse.core.system.AbstractTestCase;

/**
 * Tests for combinatorial functions
 * 
 */
public class CombinatoricTestCase extends AbstractTestCase {
	public CombinatoricTestCase(String name) {
		super(name);
	}

	/**
	 * Test combinatorial functions
	 */
	public void testCombinatoric() {
		check("KOrderlessPartitions[a+b+b+c+d,3]",
				"{{a,2*b,d+c},{a,c+2*b,d},{2*b+a,c,d},{a,d+2*b,c},{2*b+a,d,c},{a,c,d+2*b},{c+a,2*b,d},{a,d+c,\n"
						+ "2*b},{c+a,d,2*b},{a,d,c+2*b},{d+a,2*b,c},{d+a,c,2*b},{2*b,a,d+c},{2*b,c+a,d},{2*b,d+a,c},{\n"
						+ "2*b,c,d+a},{c+2*b,a,d},{2*b,d+c,a},{c+2*b,d,a},{2*b,d,c+a},{d+2*b,a,c},{d+2*b,c,a},{c,a,d+\n"
						+ "2*b},{c,2*b+a,d},{c,d+a,2*b},{c,2*b,d+a},{c,d+2*b,a},{c,d,2*b+a},{d+c,a,2*b},{d+c,\n"
						+ "2*b,a},{d,a,c+2*b},{d,2*b+a,c},{d,c+a,2*b},{d,2*b,c+a},{d,c+2*b,a},{d,c,2*b+a}}");

		check("KPartitions[{a,b,c,d,e},3]",
				"{{{a},{b},{c,d,e}},{{a},{b,c},{d,e}},{{a},{b,c,d},{e}},{{a,b},{c},{d,e}},{{a,b},{c,d},{e}},{{a,b,c},{d},{e}}}");

		check("IntegerPartitions[3]", "{{3},{2,1},{1,1,1}}");
		check("IntegerPartitions[5]", "{{5},{4,1},{3,2},{3,1,1},{2,2,1},{2,1,1,1},{1,1,1,1,1}}");
		check("IntegerPartitions[10]", "{{10},{9,1},{8,2},{8,1,1},{7,3},{7,2,1},{7,1,1,1},{6,4},{6,3,1},{6,2,2},{6,2,1,1},{\n"
				+ "6,1,1,1,1},{5,5},{5,4,1},{5,3,2},{5,3,1,1},{5,2,2,1},{5,2,1,1,1},{5,1,1,1,1,1},{\n"
				+ "4,4,2},{4,4,1,1},{4,3,3},{4,3,2,1},{4,3,1,1,1},{4,2,2,2},{4,2,2,1,1},{4,2,1,1,1,\n"
				+ "1},{4,1,1,1,1,1,1},{3,3,3,1},{3,3,2,2},{3,3,2,1,1},{3,3,1,1,1,1},{3,2,2,2,1},{3,\n"
				+ "2,2,1,1,1},{3,2,1,1,1,1,1},{3,1,1,1,1,1,1,1},{2,2,2,2,2},{2,2,2,2,1,1},{2,2,2,1,\n"
				+ "1,1,1},{2,2,1,1,1,1,1,1},{2,1,1,1,1,1,1,1,1},{1,1,1,1,1,1,1,1,1,1}}");

		check("Permutations[{1,1,1}]", "{{1,1,1}}");
		check("Permutations[{2,1,0}]", "{{2,1,0},{2,0,1},{1,2,0},{1,0,2},{0,2,1},{0,1,2}}");
		check("Permutations[{1,2,2,3}]", "{{1,2,2,3},{1,2,3,2},{1,3,2,2},{2,1,2,3},{2,1,3,2},{2,2,1,3},{2,2,3,1},{2,3,1,2},{\n"
				+ "2,3,2,1},{3,1,2,2},{3,2,1,2},{3,2,2,1}}");
		check("Partition[{a,b,c,d,e,f,g},3,2]", "{{a,b,c},{c,d,e},{e,f,g}}");

	}
}
