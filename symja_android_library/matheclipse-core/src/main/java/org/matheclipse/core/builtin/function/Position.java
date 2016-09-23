package org.matheclipse.core.builtin.function;

import static org.matheclipse.core.expression.F.List;

import java.util.function.Predicate;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.util.LevelSpec;
import org.matheclipse.core.eval.util.LevelSpecification;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.PositionConverter;
import org.matheclipse.core.generic.interfaces.IPositionConverter;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.PatternMatcher;

/**
 * Position(list, pattern) - return the positions where the pattern occurs in
 * list.
 *
 */
public class Position extends AbstractCoreFunctionEvaluator {

	/**
	 * Add the positions to the <code>resultCollection</code> where the matching
	 * expressions appear in <code>list</code>. The
	 * <code>positionConverter</code> converts the <code>int</code> position
	 * into an object for the <code>resultCollection</code>.
	 * 
	 * @param list
	 * @param prototypeList
	 * @param resultCollection
	 * @param level
	 * @param matcher
	 * @param positionConverter
	 * @param headOffset
	 * @return
	 */
	public static IAST position(final IAST list, final IAST prototypeList, final IAST resultCollection,
			final LevelSpec level, final Predicate<? super IExpr> matcher,
			final IPositionConverter<? extends IExpr> positionConverter, int headOffset) {
		int minDepth = 0;
		level.incCurrentLevel();
		IAST clone = null;
		final int size = list.size();
		for (int i = headOffset; i < size; i++) {
			if (matcher.test(list.get(i))) {
				if (level.isInRange()) {
					clone = prototypeList.clone();
					IExpr IExpr = positionConverter.toObject(i);
					clone.append(IExpr);
					resultCollection.append(clone);
				}
			} else if (list.get(i).isAST()) {
				// clone = (INestedList<IExpr>) prototypeList.clone();
				clone = prototypeList.clone();
				clone.append(positionConverter.toObject(i));
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

		final IAST cloneList = List();
		final IAST resultList = List();
		int headOffset = 1;
		if (level.isIncludeHeads()) {
			headOffset = 0;
		}
		position(list, cloneList, resultList, level, matcher, pos, headOffset);
		return resultList;
	}

	public Position() {
		// default ctor
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (ast.isAST1()) {
			return F.operatorFormAST1(ast);
		}
		Validate.checkRange(ast, 3, 4);

		final IExpr arg1 = engine.evaluate(ast.arg1());
		if (arg1.isAST()) {
			final IExpr arg2 = engine.evalPattern(ast.arg2());
			if (ast.isAST2()) {
				final LevelSpec level = new LevelSpec(0, Integer.MAX_VALUE);
				return position((IAST) arg1, arg2, level);
			}
			if (ast.isAST3()) {
				final Options options = new Options(ast.topHead(), ast, 2, engine);
				IExpr option = options.getOption("Heads");
				if (option.isPresent()) {
					if (option.isTrue()) {
						final LevelSpec level = new LevelSpec(0, Integer.MAX_VALUE, true);
						return position((IAST) arg1, arg2, level);
					}
					if (option.isFalse()) {
						final LevelSpec level = new LevelSpec(0, Integer.MAX_VALUE, false);
						return position((IAST) arg1, arg2, level);
					}
					return F.NIL;
				}
				final IExpr arg3 = engine.evaluate(ast.arg3());
				final LevelSpec level = new LevelSpecification(arg3, true);
				return position((IAST) arg1, arg2, level);
			}
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.NHOLDALL);
	}
}
