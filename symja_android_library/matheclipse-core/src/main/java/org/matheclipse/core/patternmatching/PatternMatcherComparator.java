package org.matheclipse.core.patternmatching;

import java.io.Serializable;
import java.util.Comparator;

public class PatternMatcherComparator implements Comparator<IPatternMatcher>, Serializable {
	public final static PatternMatcherComparator CONS = new PatternMatcherComparator();

	@Override
	public int compare(IPatternMatcher o1, IPatternMatcher o2) {
		return o1.equivalent(o2);
	}

}
