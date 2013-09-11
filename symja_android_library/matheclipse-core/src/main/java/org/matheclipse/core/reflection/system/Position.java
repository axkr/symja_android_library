package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.List;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.LevelSpec;
import org.matheclipse.core.eval.util.LevelSpecification;
import org.matheclipse.core.generic.PositionConverter;
import org.matheclipse.core.generic.interfaces.IPositionConverter;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.patternmatching.PatternMatcher;

import com.google.common.base.Predicate;

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

	/**
	 * Add the positions to the <code>resultCollection</code> where the matching expressions appear in <code>list</code>. The
	 * <code>positionConverter</code> converts the <code>int</code> position into an object for the <code>resultCollection</code>.
	 * 
	 * @param list
	 * @param prototypeList
	 * @param resultCollection
	 * @param level
	 * @param matcher
	 * @param positionConverter
	 * @param headOffset
	 */
	public static IAST position(final IAST list, final IAST prototypeList, final IAST resultCollection, final LevelSpec level,
			final Predicate<? super IExpr> matcher, final IPositionConverter<? extends IExpr> positionConverter, int headOffset) {
		int minDepth = 0;
		level.incCurrentLevel();
		IAST clone = null;
		final int size = list.size();
		for (int i = headOffset; i < size; i++) {
			if (matcher.apply(list.get(i))) {
				if (level.isInRange()) {
					clone = prototypeList.clone();
					IExpr IExpr = positionConverter.toObject(i);
					clone.add(IExpr);
					resultCollection.add(clone);
				}
			} else if (list.get(i).isAST()) {
				// clone = (INestedList<IExpr>) prototypeList.clone();
				clone = prototypeList.clone();
				clone.add(positionConverter.toObject(i));
				position((IAST) list.get(i), clone, resultCollection, level, matcher, positionConverter, headOffset);
				if (level.getCurrentDepth() < minDepth) {
					minDepth = level.getCurrentDepth();
				}
			}
		}
		level.setCurrentDepth(--minDepth);
		level.decCurrentLevel();
		return resultCollection;
	}

	public static IAST position(final IAST list, final IExpr pattern, final LevelSpec level) {
		final PatternMatcher matcher = new PatternMatcher(pattern);
		final PositionConverter pos = new PositionConverter();

		final IAST cloneList = list.copyHead();
		final IAST resultList = List();
		Position.position(list, cloneList, resultList, level, matcher, pos, 1);
		return resultList;
	}

}
