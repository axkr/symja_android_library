package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.List;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.generic.LevelSpecification;
import org.matheclipse.core.generic.PositionConverter;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.patternmatching.PatternMatcher;
import org.matheclipse.generic.nested.LevelSpec;
import org.matheclipse.generic.nested.NestedAlgorithms;

public class Position extends AbstractFunctionEvaluator {
	
	public Position() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 3, 4);
		
		if (ast.get(1).isAST()) {
			if (ast.size() == 3) {
				final LevelSpec level = new LevelSpec(0, Integer.MAX_VALUE);
				return position((IAST) ast.get(1), ast.get(2), level);
			}
			if (ast.size() == 4) {
				final LevelSpec level = new LevelSpecification(ast.get(3), false);
				return position((IAST) ast.get(1), ast.get(2), level);
			}
		}
		return null;
	}

	public static IAST position(final IAST list, final IExpr pattern, final LevelSpec level) {
		final PatternMatcher matcher = new PatternMatcher(pattern);
		final PositionConverter pos = new PositionConverter();

		final IAST cloneList = list.copyHead();
		final IAST resultList = List();
		NestedAlgorithms.position(list, cloneList, resultList, level, matcher, pos, 1);
		return resultList;
	}

}
