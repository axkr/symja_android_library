package org.matheclipse.core.builtin;

import static org.matheclipse.core.expression.F.List;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.IllegalArgument;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.ISequence;
import org.matheclipse.core.eval.util.Iterator;
import org.matheclipse.core.eval.util.LevelSpec;
import org.matheclipse.core.eval.util.LevelSpecification;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.eval.util.Sequence;
import org.matheclipse.core.eval.util.TableGenerator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Comparators;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.generic.MultipleArrayFunction;
import org.matheclipse.core.generic.MultipleConstArrayFunction;
import org.matheclipse.core.generic.PositionConverter;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.generic.UnaryRangeFunction;
import org.matheclipse.core.generic.interfaces.IIterator;
import org.matheclipse.core.generic.interfaces.IPositionConverter;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.patternmatching.PatternMatcher;
import org.matheclipse.core.visit.VisitorLevelSpecification;
import org.matheclipse.core.visit.VisitorRemoveLevelSpecification;
import org.matheclipse.parser.client.math.MathException;

public final class ListFunctions {
	static {
		F.Array.setEvaluator(new Array());
		F.Cases.setEvaluator(new Cases());
		F.Complement.setEvaluator(new Complement());
		F.Composition.setEvaluator(new Composition());
		F.ConstantArray.setEvaluator(new ConstantArray());
		F.Count.setEvaluator(new Count());
		F.DeleteDuplicates.setEvaluator(new DeleteDuplicates());
		F.DeleteCases.setEvaluator(new DeleteCases());
		F.Drop.setEvaluator(new Drop());
		F.Extract.setEvaluator(new Extract());
		F.First.setEvaluator(new First());
		F.Intersection.setEvaluator(new Intersection());
		F.Join.setEvaluator(new Join());
		F.Last.setEvaluator(new Last());
		F.Length.setEvaluator(new Length());
		F.LevelQ.setEvaluator(new LevelQ());
		F.Level.setEvaluator(new Level());
		F.Most.setEvaluator(new Most());
		F.Position.setEvaluator(new Position());
		F.Range.setEvaluator(new Range());
		F.Rest.setEvaluator(new Rest());
		F.ReplacePart.setEvaluator(new ReplacePart());
		F.Select.setEvaluator(new Select());
		F.Split.setEvaluator(new Split());
		F.SplitBy.setEvaluator(new SplitBy());
		F.Take.setEvaluator(new Take());
		F.Union.setEvaluator(new Union());
	}

	/**
	 * Array structure generator
	 * 
	 * <p>
	 * See the online Symja function reference: <a href=
	 * "https://bitbucket.org/axelclk/symja_android_library/wiki/Symbols/Array">Array</a>
	 * </p>
	 *
	 */
	private final static class Array extends AbstractCoreFunctionEvaluator {

		public static class ArrayIterator implements IIterator<IExpr> {
			int fCurrent;

			final int fFrom;

			final int fTo;

			public ArrayIterator(final int to) {
				this(1, to);
			}

			public ArrayIterator(final int from, final int length) {
				fFrom = from;
				fCurrent = from;
				fTo = from + length - 1;
			}

			@Override
			public boolean setUp() {
				return true;
			}

			@Override
			public void tearDown() {
				fCurrent = fFrom;
			}

			@Override
			public boolean hasNext() {
				return fCurrent <= fTo;
			}

			@Override
			public IExpr next() {
				return F.integer(fCurrent++);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			try {
				IAST resultList;
				if (ast.size() == 5) {
					resultList = F.ast(ast.arg4());
				} else {
					resultList = F.List();
				}
				if (ast.size() >= 3 && (ast.size() <= 5)) {
					int indx1, indx2;
					final List<ArrayIterator> iterList = new ArrayList<ArrayIterator>();
					if (ast.size() >= 4) {
						if (ast.arg2().isInteger() && ast.arg3().isInteger()) {
							indx1 = Validate.checkIntType(ast, 3);
							indx2 = Validate.checkIntType(ast, 2);
							iterList.add(new ArrayIterator(indx1, indx2));
						} else if (ast.arg2().isList() && ast.arg3().isInteger()) {
							final IAST dimIter = (IAST) ast.arg2(); // dimensions
							indx1 = Validate.checkIntType(ast, 3);
							for (int i = 1; i < dimIter.size(); i++) {
								indx2 = Validate.checkIntType(dimIter, i);
								iterList.add(new ArrayIterator(indx1, indx2));
							}
						} else if (ast.arg2().isList() && ast.arg3().isList()) {
							final IAST dimIter = (IAST) ast.arg2(); // dimensions
							final IAST originIter = (IAST) ast.arg3(); // origins
							if (dimIter.size() != originIter.size()) {
								engine.printMessage(dimIter.toString() + " and " + originIter.toString()
										+ " should have the same length.");
								return F.NIL;
							}
							for (int i = 1; i < dimIter.size(); i++) {
								indx1 = Validate.checkIntType(originIter, i);
								indx2 = Validate.checkIntType(dimIter, i);
								iterList.add(new ArrayIterator(indx1, indx2));
							}
						}
					} else if (ast.size() >= 3 && ast.arg2().isInteger()) {
						indx1 = Validate.checkIntType(ast, 2);
						iterList.add(new ArrayIterator(indx1));
					} else if (ast.size() >= 3 && ast.arg2().isList()) {
						final IAST dimIter = (IAST) ast.arg2();
						for (int i = 1; i < dimIter.size(); i++) {
							indx1 = Validate.checkIntType(dimIter, i);
							iterList.add(new ArrayIterator(indx1));
						}
					}

					if (iterList.size() > 0) {
						final IAST list = F.ast(ast.arg1());
						final TableGenerator generator = new TableGenerator(iterList, resultList,
								new MultipleArrayFunction(engine, list));
						return generator.table();
					}

				}
			} catch (final ClassCastException e) {
				// the iterators are generated only from IASTs
			} catch (final ArithmeticException e) {
				// the toInt() function throws ArithmeticExceptions
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	/**
	 * <p>
	 * See the online Symja function reference: <a href=
	 * "https://bitbucket.org/axelclk/symja_android_library/wiki/Symbols/Cases">
	 * Cases</a>
	 * </p>
	 */
	private final static class Cases extends AbstractCoreFunctionEvaluator {
		/**
		 * StopException will be thrown, if maximum number of Cases results are
		 * reached
		 *
		 */
		@SuppressWarnings("serial")
		private static class StopException extends MathException {
			public StopException() {
				super("Stop Cases() evaluation");
			}
		}

		private static class CasesPatternMatcherFunctor implements Function<IExpr, IExpr> {
			protected final PatternMatcher matcher;
			protected IAST resultCollection;
			final int maximumResults;
			private int resultsCounter;

			/**
			 * 
			 * @param matcher
			 *            the pattern-matcher
			 * @param resultCollection
			 * @param maximumResults
			 *            maximum number of results. -1 for for no limitation
			 */
			public CasesPatternMatcherFunctor(final PatternMatcher matcher, IAST resultCollection, int maximumResults) {
				this.matcher = matcher;
				this.resultCollection = resultCollection;
				this.maximumResults = maximumResults;
				this.resultsCounter = 0;
			}

			@Override
			public IExpr apply(final IExpr arg) throws StopException {
				if (matcher.test(arg)) {
					resultCollection.append(arg);
					if (maximumResults >= 0) {
						resultsCounter++;
						if (resultsCounter >= maximumResults) {
							throw new StopException();
						}
					}
				}
				return F.NIL;
			}

		}

		private static class CasesRulesFunctor implements Function<IExpr, IExpr> {
			protected final Function<IExpr, IExpr> function;
			protected IAST resultCollection;
			final int maximumResults;
			private int resultsCounter;

			/**
			 * 
			 * @param function
			 *            the funtion which should determine the results
			 * @param resultCollection
			 * @param maximumResults
			 *            maximum number of results. -1 for for no limitation
			 */
			public CasesRulesFunctor(final Function<IExpr, IExpr> function, IAST resultCollection, int maximumResults) {
				this.function = function;
				this.resultCollection = resultCollection;
				this.maximumResults = maximumResults;
			}

			@Override
			public IExpr apply(final IExpr arg) throws StopException {
				IExpr temp = function.apply(arg);
				if (temp.isPresent()) {
					resultCollection.append(temp);
					if (maximumResults >= 0) {
						resultsCounter++;
						if (resultsCounter >= maximumResults) {
							throw new StopException();
						}
					}
				}
				return F.NIL;
			}

		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST1()) {
				return F.operatorFormAST1(ast);
			}
			Validate.checkRange(ast, 3, 5);

			final IExpr arg1 = engine.evaluate(ast.arg1());
			if (arg1.isAST()) {
				final IExpr arg2 = engine.evalPattern(ast.arg2());
				if (ast.isAST3() || ast.size() == 5) {
					final IExpr arg3 = engine.evaluate(ast.arg3());
					int maximumResults = -1;
					if (ast.size() == 5) {
						maximumResults = Validate.checkIntType(ast, 4);
					}
					IAST result = F.List();
					if (arg2.isRuleAST()) {
						try {
							Function<IExpr, IExpr> function = Functors.rules((IAST) arg2);
							CasesRulesFunctor crf = new CasesRulesFunctor(function, result, maximumResults);
							VisitorLevelSpecification level = new VisitorLevelSpecification(crf, arg3, false);
							arg1.accept(level);

						} catch (StopException se) {
							// reached maximum number of results
						}
						return result;
					}

					try {
						final PatternMatcher matcher = new PatternMatcher(arg2);
						CasesPatternMatcherFunctor cpmf = new CasesPatternMatcherFunctor(matcher, result,
								maximumResults);
						VisitorLevelSpecification level = new VisitorLevelSpecification(cpmf, arg3, false);
						arg1.accept(level);
					} catch (StopException se) {
						// reached maximum number of results
					}
					return result;
				} else {
					return cases((IAST) arg1, arg2);
				}
			}
			return F.List();
		}

		public static IAST cases(final IAST ast, final IExpr pattern) {
			if (pattern.isRuleAST()) {
				Function<IExpr, IExpr> function = Functors.rules((IAST) pattern);
				IAST[] results = ast.filter(function);
				return results[0];
			}
			final PatternMatcher matcher = new PatternMatcher(pattern);
			return ast.filter(F.List(), matcher);
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	private final static class Complement extends AbstractFunctionEvaluator {

		public Complement() {
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			if (!ast.arg1().isAtom() && !ast.arg2().isAtom()) {

				final IAST arg1 = ((IAST) ast.arg1());
				final IAST arg2 = ((IAST) ast.arg2());
				return complement(arg1, arg2);
			}
			return F.NIL;
		}

		public static IExpr complement(final IAST arg1, final IAST arg2) {
			IAST result = F.List();
			Set<IExpr> set2 = arg2.asSet();
			Set<IExpr> set3 = new HashSet<IExpr>();
			for (int i = 1; i < arg1.size(); i++) {
				IExpr temp = arg1.get(i);
				if (!set2.contains(temp)) {
					set3.add(temp);
				}
			}
			for (IExpr expr : set3) {
				result.append(expr);
			}
			EvalAttributes.sort(result);
			return result;
		}
	}

	private final static class Composition extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.head().equals(F.Composition)) {
				return F.NIL;
			}
			if (ast.head().isAST()) {

				IAST headList = (IAST) ast.head();
				if (headList.size() > 1) {
					IAST inner = F.ast(headList.get(1));
					IAST result = inner;
					IAST temp;
					for (int i = 2; i < headList.size(); i++) {
						temp = F.ast(headList.get(i));
						inner.append(temp);
						inner = temp;
					}
					for (int i = 1; i < ast.size(); i++) {
						inner.append(ast.get(i));
					}
					return result;
				}

			}
			return F.NIL;
		}

	}

	/**
	 * Array structure generator for constant (i,j) value.
	 */
	private final static class ConstantArray extends AbstractEvaluator {

		public static class ArrayIterator implements IIterator<IExpr> {
			int fCurrent;

			final int fFrom;

			final int fTo;

			public ArrayIterator(final int to) {
				this(1, to);
			}

			public ArrayIterator(final int from, final int length) {
				fFrom = from;
				fCurrent = from;
				fTo = from + length - 1;
			}

			@Override
			public boolean setUp() {
				return true;
			}

			@Override
			public void tearDown() {
				fCurrent = fFrom;
			}

			@Override
			public boolean hasNext() {
				return fCurrent <= fTo;
			}

			@Override
			public IExpr next() {
				return F.integer(fCurrent++);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			return evaluateArray(ast, List());
		}

		public static IExpr evaluateArray(final IAST ast, IAST resultList) {
			try {
				if ((ast.size() >= 3) && (ast.size() <= 5)) {
					int indx1, indx2;
					final List<ArrayIterator> iterList = new ArrayList<ArrayIterator>();
					if ((ast.isAST2()) && (ast.arg2().isInteger())) {
						indx1 = Validate.checkIntType(ast, 2);
						iterList.add(new ArrayIterator(indx1));
					} else if ((ast.isAST2()) && ast.arg2().isList()) {
						final IAST dimIter = (IAST) ast.arg2();
						for (int i = 1; i < dimIter.size(); i++) {
							indx1 = Validate.checkIntType(dimIter, i);
							iterList.add(new ArrayIterator(indx1));
						}
					} else if (ast.size() >= 4) {
						if (ast.arg2().isInteger() && ast.arg3().isInteger()) {
							indx1 = Validate.checkIntType(ast, 3);
							indx2 = Validate.checkIntType(ast, 2);
							iterList.add(new ArrayIterator(indx1, indx2));
						} else if (ast.arg2().isList() && ast.arg3().isList()) {
							final IAST dimIter = (IAST) ast.arg2(); // dimensions
							final IAST originIter = (IAST) ast.arg3(); // origins
							for (int i = 1; i < dimIter.size(); i++) {
								indx1 = Validate.checkIntType(originIter, i);
								indx2 = Validate.checkIntType(dimIter, i);
								iterList.add(new ArrayIterator(indx1, indx2));
							}
						}
					}

					if (iterList.size() > 0) {
						if (ast.size() == 5) {
							resultList = F.ast(ast.arg4());
						}
						final IExpr constantExpr = ast.arg1();
						final TableGenerator generator = new TableGenerator(iterList, resultList,
								new MultipleConstArrayFunction(constantExpr));
						return generator.table();
					}

				}
			} catch (final ClassCastException e) {
				// the iterators are generated only from IASTs
			} catch (final ArithmeticException e) {
				// the toInt() function throws ArithmeticExceptions
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	/**
	 * Count the number of elements in an expression which match the given
	 * pattern.
	 * 
	 */
	private final static class Count extends AbstractCoreFunctionEvaluator {
		private static class CountFunctor implements Function<IExpr, IExpr> {
			protected final IPatternMatcher matcher;
			protected int counter;

			/**
			 * @return the counter
			 */
			public int getCounter() {
				return counter;
			}

			public CountFunctor(final IPatternMatcher patternMatcher) {
				this.matcher = patternMatcher; // new PatternMatcher(pattern);
				counter = 0;
			}

			@Override
			public IExpr apply(final IExpr arg) {
				if (matcher.test(arg)) {
					counter++;
				}
				return F.NIL;
			}

		}

		public Count() {
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3, 4);

			final IExpr arg1 = engine.evaluate(ast.arg1());

			final VisitorLevelSpecification level;
			CountFunctor mf = new CountFunctor(engine.evalPatternMatcher(ast.arg2()));
			if (ast.isAST3()) {
				final IExpr arg3 = engine.evaluate(ast.arg3());
				level = new VisitorLevelSpecification(mf, arg3, false);
			} else {
				level = new VisitorLevelSpecification(mf, 1);
			}
			arg1.accept(level);
			return F.integer(mf.getCounter());
		}

	}

	private final static class DeleteCases extends AbstractCoreFunctionEvaluator {

		private static class DeleteCasesPatternMatcherFunctor implements Function<IExpr, IExpr> {
			private final IPatternMatcher matcher;

			/**
			 * 
			 * @param matcher
			 *            the pattern-matcher
			 */
			public DeleteCasesPatternMatcherFunctor(final IPatternMatcher matcher) {
				this.matcher = matcher;
			}

			@Override
			public IExpr apply(final IExpr arg) {
				if (matcher.test(arg)) {
					return F.Null;
				}
				return F.NIL;
			}

		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3, 5);

			final IExpr arg1 = engine.evaluate(ast.arg1());
			if (arg1.isAST()) {
				final IPatternMatcher matcher = engine.evalPatternMatcher(ast.arg2());
				if (ast.isAST3() || ast.size() == 5) {
					final IExpr arg3 = engine.evaluate(ast.arg3());
					int maximumRemoveOperations = -1;
					if (ast.size() == 5) {
						maximumRemoveOperations = Validate.checkIntType(ast, 4);
					}
					IAST arg1RemoveClone = ((IAST) arg1).clone();

					try {
						DeleteCasesPatternMatcherFunctor cpmf = new DeleteCasesPatternMatcherFunctor(matcher);
						VisitorRemoveLevelSpecification level = new VisitorRemoveLevelSpecification(cpmf, arg3,
								maximumRemoveOperations, false);
						arg1RemoveClone.accept(level);
						if (level.getRemovedCounter() == 0) {
							return arg1;
						}
						return arg1RemoveClone;
					} catch (VisitorRemoveLevelSpecification.StopException se) {
						// reached maximum number of results
					}
					return arg1RemoveClone;
				} else {
					return deleteCases((IAST) arg1, matcher);
				}
			}
			return F.NIL;
		}

		public static IAST deleteCases(final IAST ast, final IPatternMatcher matcher) {
			// final IPatternMatcher matcher = new PatternMatcher(pattern);
			IAST[] results = ast.filter(matcher);
			return results[1];

		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	/**
	 * Delete duplicate values from a list.
	 */
	private final static class DeleteDuplicates extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			IExpr test = F.Equal;
			if (ast.isAST2()) {
				test = ast.arg2();
			}
			if (ast.arg1().isList()) {
				IAST list = (IAST) ast.arg1();
				final IAST result = F.List();
				IExpr temp;
				boolean evaledTrue;
				BiPredicate<IExpr, IExpr> biPredicate = Predicates.isBinaryTrue(test);
				for (int i = 1; i < list.size(); i++) {
					temp = list.get(i);
					evaledTrue = false;
					for (int j = 1; j < result.size(); j++) {
						if (biPredicate.test(result.get(j), temp)) {
							evaledTrue = true;
							break;
						}
					}
					if (evaledTrue) {
						continue;
					}
					result.append(temp);
				}
				return result;
			}
			return F.NIL;
		}

	}

	/**
	 * Drop(list,n) - delete the first n arguments from the list. Negative n
	 * counts from the end.
	 * 
	 */
	private final static class Drop extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3);

			IAST evaledAST = (IAST) engine.evalAttributes(F.Drop, ast);
			if (!evaledAST.isPresent()) {
				evaledAST = ast;
			}
			final IExpr arg1 = evaledAST.arg1();
			try {
				if (arg1.isAST()) {
					final ISequence[] sequ = Sequence.createSequences(evaledAST, 2);
					final IAST list = (IAST) arg1;
					if (sequ != null) {
						final IAST resultList = list.clone();
						drop(resultList, 0, sequ);
						return resultList;
					}
				}
			} catch (final IllegalArgument e) {
				engine.printMessage(e.getMessage());
				return F.NIL;
			} catch (final Exception e) {
				if (Config.SHOW_STACKTRACE) {
					e.printStackTrace();
				}
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.NHOLDREST);
		}

		/**
		 * Drop (remove) the list elements according to the
		 * <code>sequenceSpecifications</code> for the list indexes.
		 * 
		 * @param list
		 * @param level
		 *            recursion level
		 * @param sequenceSpecifications
		 *            one or more ISequence specifications
		 * @return
		 */
		private static IAST drop(final IAST list, final int level, final ISequence[] sequenceSpecifications) {
			sequenceSpecifications[level].setListSize(list.size());
			final int newLevel = level + 1;
			int j = sequenceSpecifications[level].getStart();
			int end = sequenceSpecifications[level].getEnd();
			int step = sequenceSpecifications[level].getStep();
			if (step < 0) {
				end--;
				if (j < end || end <= 0) {
					throw new IllegalArgument("Cannot drop positions " + j + " through " + end + " in " + list);
					// return F.NIL;
				}
				for (int i = j; i >= end; i += step) {
					list.remove(j);
					j += step;
				}
			} else {
				if (j == 0) {
					throw new IllegalArgument("Cannot drop positions " + j + " through " + (end - 1) + " in " + list);
				}
				for (int i = j; i < end; i += step) {
					list.remove(j);
					j += step - 1;
				}
			}
			for (int j2 = 1; j2 < list.size(); j2++) {
				if (sequenceSpecifications.length > newLevel) {
					if (list.get(j2).isAST()) {
						final IAST tempList = ((IAST) list.get(j2)).clone();
						list.set(j2, drop(tempList, newLevel, sequenceSpecifications));
					} else {
						throw new IllegalArgument("Cannot execute drop for argument: " + list.get(j2).toString());
					}
				}
			}
			return list;
		}
	}

	private final static class Extract extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3, 4);

			if (ast.arg1().isAST() && ast.arg2().isList()) {
				IAST arg1 = (IAST) ast.arg1();
				IAST arg2 = (IAST) ast.arg2();
				if (arg2.isListOfLists()) {
					IAST result = F.List();
					final int arg2Size = arg2.size();
					for (int i = 1; i < arg2Size; i++) {
						IExpr temp = extract(arg1, arg2.getAST(i));
						if (!temp.isPresent()) {
							return F.NIL;
						}
						result.append(temp);
					}
					return result;
				}
				return extract(arg1, arg2);
			}
			return F.NIL;
		}

		private static IExpr extract(final IAST list, final IAST position) {
			final PositionConverter converter = new PositionConverter();
			if ((position.size() > 1) && (position.arg1().isSignedNumber())) {
				return extract(list, position, converter, 1);
			} else {
				// construct an array
				// final IAST resultList = List();
				// NestedFinding.position(list, resultList, pos, 1);
				// return resultList;
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.NHOLDREST);
		}

		/**
		 * Traverse all <code>list</code> element's and filter out the elements
		 * in the given <code>positions</code> list.
		 * 
		 * @param list
		 * @param positions
		 * @param positionConverter
		 *            the <code>positionConverter</code> creates an
		 *            <code>int</code> value from the given position objects in
		 *            <code>positions</code>.
		 * @param headOffsez
		 */
		private static IExpr extract(final IAST list, final IAST positions,
				final IPositionConverter<? super IExpr> positionConverter, int headOffset) {
			int p = 0;
			IAST temp = list;
			int posSize = positions.size() - 1;
			IExpr expr = list;
			for (int i = headOffset; i <= posSize; i++) {
				p = positionConverter.toInt(positions.get(i));
				if (!temp.isPresent() || temp.size() <= p || p < 0) {
					return F.NIL;
				}
				expr = temp.get(p);
				if (expr.isAST()) {
					temp = (IAST) expr;
				} else {
					if (i < positions.size()) {
						temp = F.NIL;
					}
				}
			}
			return expr;
		}
	}

	private final static class First extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() == 2) {
				IExpr arg1 = engine.evaluate(ast.arg1());
				if (arg1.isAST()) {
					final IAST sublist = (IAST) arg1;

					if (sublist.size() > 1) {
						return sublist.arg1();
					}
				}
				engine.printMessage("First: Nonatomic expression expected");
				return F.NIL;
			}
			return Validate.checkSize(ast, 2);
		}
	}

	/**
	 * Intersection of 2 sets
	 * 
	 * See: <a href=
	 * "http://en.wikipedia.org/wiki/Intersection_(set_theory)">Intersection
	 * (set theory)</a>
	 */
	private final static class Intersection extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			if (ast.isAST1() && ast.arg1().isAST()) {
				final IAST result = F.List();
				IAST arg1 = (IAST) ast.arg1();
				Set<IExpr> set = arg1.asSet();
				for (IExpr IExpr : set) {
					result.append(IExpr);
				}
				EvalAttributes.sort(result, Comparators.ExprComparator.CONS);
				return result;
			}

			if (ast.arg1().isAST() && ast.arg2().isAST()) {
				IAST arg1AST = ((IAST) ast.arg1());
				IAST arg2AST = ((IAST) ast.arg2());
				final IAST result = F.List();
				return intersection(arg1AST, arg2AST, result);
			}

			return F.NIL;
		}

		/**
		 * Create the (ordered) intersection set from both ASTs.
		 * 
		 * @param ast1
		 *            first AST set
		 * @param ast2
		 *            second AST set
		 * @param result
		 *            the AST where the elements of the union should be appended
		 * @return
		 */
		public static IExpr intersection(IAST ast1, IAST ast2, final IAST result) {
			Set<IExpr> set1 = new HashSet<IExpr>(ast1.size() + ast1.size() / 10);
			Set<IExpr> set2 = new HashSet<IExpr>(ast2.size() + ast1.size() / 10);
			Set<IExpr> resultSet = new TreeSet<IExpr>();
			int size = ast1.size();
			for (int i = 1; i < size; i++) {
				set1.add(ast1.get(i));
			}
			size = ast2.size();
			for (int i = 1; i < size; i++) {
				set2.add(ast2.get(i));
			}
			for (IExpr expr : set1) {
				if (set2.contains(expr)) {
					resultSet.add(expr);
				}
			}
			for (IExpr expr : resultSet) {
				result.append(expr);
			}
			return result;
		}
	}

	private final static class Join extends AbstractFunctionEvaluator {

		public Join() {
			super();
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3);
			if (ast.args().any(PredicateQ.ATOMQ)) {
				return F.NIL;
			}

			int astSize = ast.size();
			int size = 0;
			for (int i = 1; i < astSize; i++) {
				size += ((IAST) ast.get(i)).size() - 1;
			}
			final IAST result = F.ListAlloc(size);
			for (int i = 1; i < ast.size(); i++) {
				result.appendArgs((IAST) ast.get(i));
			}
			return result;
		}
	}

	/**
	 * Last(list) - get the last element of a list.
	 */
	private final static class Last extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() == 2) {
				IExpr arg1 = engine.evaluate(ast.arg1());
				if (arg1.isAST()) {
					final IAST list = (IAST) arg1;
					if (list.size() > 1) {
						return list.last();
					}
				}
				engine.printMessage("Last: Nonatomic expression expected");
				return F.NIL;
			}
			return Validate.checkSize(ast, 2);
		}
	}

	private final static class Length extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			IExpr arg1 = engine.evaluate(ast.arg1());
			if (arg1.isAST()) {
				return F.integer(((IAST) arg1).size() - 1);
			}
			return F.C0;
		}

	}

	private final static class Level extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3, 5);

			int lastIndex = ast.size() - 1;
			boolean heads = false;
			final Options options = new Options(ast.topHead(), ast, lastIndex, engine);
			IExpr option = options.getOption("Heads");
			if (option.isPresent()) {
				lastIndex--;
				if (option.isTrue()) {
					heads = true;
				}
			} else {
				Validate.checkRange(ast, 3, 4);
			}

			if (!ast.arg1().isAtom()) {
				final IAST arg1 = (IAST) ast.arg1();
				IAST resultList;
				if (lastIndex != 3) {
					resultList = List();
				} else {
					resultList = F.ast(ast.get(lastIndex));
				}

				final VisitorLevelSpecification level = new VisitorLevelSpecification(
						Functors.collect(resultList.args()), ast.arg2(), heads);
				arg1.accept(level);

				return resultList;
			}
			return F.NIL;
		}

	}

	private final static class LevelQ extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			IExpr arg1 = engine.evaluate(ast.arg1());
			try {
				VisitorLevelSpecification vls = new VisitorLevelSpecification(null, arg1, false);
				return F.True;
			} catch (MathException me) {

			}
			return F.False;
		}

	}

	private final static class Most extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);
			IExpr arg1 = engine.evaluate(ast.arg1());
			if (arg1.isAST() && ((IAST) arg1).size() > 1) {
				return ((IAST) arg1).removeAtClone(((IAST) arg1).size() - 1);
			}
			engine.printMessage("Most: Nonatomic expression expected");
			return F.NIL;
		}

	}

	/**
	 * Position(list, pattern) - return the positions where the pattern occurs
	 * in list.
	 *
	 */
	private final static class Position extends AbstractCoreFunctionEvaluator {

		/**
		 * Add the positions to the <code>resultCollection</code> where the
		 * matching expressions appear in <code>list</code>. The
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
					position((IAST) list.get(i), clone, resultCollection, level, matcher, positionConverter,
							headOffset);
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

	private final static class Range extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST1() && ast.arg1().isInteger()) {
				try {
					int size = ((IInteger) ast.arg1()).toInt();
					if (size >= 0) {
						IAST result = F.ListAlloc(size);
						for (int i = 1; i <= size; i++) {
							result.append(F.integer(i));
						}
						return result;
					}
					return F.List();
				} catch (final ArithmeticException ae) {
				}

				return F.NIL;
			}
			return evaluateTable(ast, List(), engine);
		}

		public IExpr evaluateTable(final IAST ast, final IAST resultList, EvalEngine engine) {
			List<IIterator<IExpr>> iterList = null;
			try {
				if ((ast.size() > 1) && (ast.size() <= 4)) {
					iterList = new ArrayList<IIterator<IExpr>>();
					iterList.add(Iterator.create(ast, null, engine));

					final TableGenerator generator = new TableGenerator(iterList, resultList, new UnaryRangeFunction());
					return generator.table();
				}
			} catch (final ClassCastException e) {
				// the iterators are generated only from IASTs
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	private final static class ReplacePart extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3, 4);

			try {
				if (ast.isAST3()) {
					if (ast.arg3().isList()) {
						IExpr result = ast.arg1();
						for (IExpr subList : (IAST) ast.arg3()) {
							IExpr expr = result.replacePart(F.Rule(subList, ast.arg2()));
							if (expr.isPresent()) {
								result = expr;
							}
						}
						return result;
					}
					return ast.arg1().replacePart(F.Rule(ast.arg3(), ast.arg2())).orElse(ast.arg1());
				}
				if (ast.arg2().isList()) {
					IExpr result = ast.arg1();
					for (IExpr subList : (IAST) ast.arg2()) {
						if (subList.isRuleAST()) {
							IExpr expr = result.replacePart((IAST) subList);
							if (expr.isPresent()) {
								result = expr;
							}
						}
					}
					return result;
				}
				IExpr result = ast.arg1();
				if (ast.arg2().isRuleAST()) {
					return ast.arg1().replacePart((IAST) ast.arg2()).orElse(ast.arg1());
				}
				return result;
			} catch (WrongArgumentType wat) {
				engine.printMessage(wat.getMessage());
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}
	}

	/**
	 * Return the <i>rest</i> of a given list, i.e. a sublist with all elements
	 * from list[[2]]...list[[n]]
	 */
	private final static class Rest extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			IExpr arg1 = engine.evaluate(ast.arg1());
			if (arg1.isAST() && ((IAST) arg1).size() > 1) {
				return ((IAST) arg1).removeAtClone(1);
			}
			engine.printMessage("Rest: Nonatomic expression expected");
			return F.NIL;
		}

	}

	private final static class Select extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3, 4);

			int size = ast.size();
			if (ast.arg1().isAST()) {
				IAST list = (IAST) ast.arg1();
				IExpr predicateHead = ast.arg2();
				int allocSize = list.size() > 4 ? list.size() / 4 : 4;
				if (size == 3) {
					return list.filter(list.copyHead(allocSize), Predicates.isTrue(predicateHead));
				} else if ((size == 4) && ast.arg3().isInteger()) {
					final int resultLimit = Validate.checkIntType(ast, 3);
					return list.filter(list.copyHead(allocSize), Predicates.isTrue(predicateHead), resultLimit);
				}
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	private final static class Split extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			if (ast.arg1().isAST()) {
				IExpr predicateHead = F.Equal;
				if (ast.isAST2()) {
					predicateHead = ast.arg2();
				}
				BiPredicate<IExpr, IExpr> pred = Predicates.isBinaryTrue(predicateHead);
				IAST list = (IAST) ast.arg1();

				IAST result = F.List();
				if (list.size() > 1) {
					IExpr current = list.get(1);
					IAST temp = F.List();
					result.append(temp);
					temp.append(current);
					for (int i = 2; i < list.size(); i++) {
						if (pred.test(current, list.get(i))) {
						} else {
							temp = F.List();
							result.append(temp);
						}
						temp.append(list.get(i));
						current = list.get(i);
					}
				}
				return result;
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	private final static class SplitBy extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			if (ast.arg1().isAST()) {
				IAST functorList;
				if (ast.arg2().isList()) {
					functorList = (IAST) ast.arg2();
				} else {
					functorList = F.List(ast.arg2());
				}
				return splitByFunction(functorList, 1, (IAST) ast.arg1());
			}
			return F.NIL;
		}

		private IExpr splitByFunction(IAST functorList, int pos, IAST list) {
			if (pos >= functorList.size()) {
				return F.NIL;
			}
			IExpr functorHead = functorList.get(pos);
			Function<IExpr, IExpr> function = Functors.replaceArg(F.unaryAST1(functorHead, F.Slot1), 1);

			IAST result = F.List();
			if (list.size() > 1) {
				IExpr last = function.apply(list.get(1));
				IExpr current;
				IAST temp = F.List();

				temp.append(list.get(1));
				for (int i = 2; i < list.size(); i++) {
					current = function.apply(list.get(i));
					if (current.equals(last)) {
					} else {
						IExpr subList = splitByFunction(functorList, pos + 1, temp);
						if (subList.isPresent()) {
							result.append(subList);
						} else {
							result.append(temp);
						}
						temp = F.List();
					}
					temp.append(list.get(i));
					last = current;
				}
				IExpr subList = splitByFunction(functorList, pos + 1, temp);
				if (subList.isPresent()) {
					result.append(subList);
				} else {
					result.append(temp);
				}
			}
			return result;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	private final static class Take extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3);

			IAST evaledAST = (IAST) engine.evalAttributes(F.Take, ast);
			if (!evaledAST.isPresent()) {
				evaledAST = ast;
			}
			try {
				if (evaledAST.arg1().isAST()) {
					final ISequence[] sequ = Sequence.createSequences(evaledAST, 2);
					final IAST arg1 = (IAST) evaledAST.arg1();
					if (sequ != null) {
						return take(arg1, 0, sequ);
					}
				} else {
					engine.printMessage("Take: Nonatomic expression expected at position 1");
				}
			} catch (final IllegalArgument e) {
				engine.printMessage("Take: " + e.getMessage());
				return F.NIL;
			} catch (final Exception e) {
				if (Config.SHOW_STACKTRACE) {
					e.printStackTrace();
				}
			}

			return F.NIL;
		}

		/**
		 * Take the list elements according to the
		 * <code>sequenceSpecifications</code> for the list indexes.
		 * 
		 * @param list
		 * @param level
		 *            recursion level
		 * @param sequenceSpecifications
		 *            one or more ISequence specifications
		 * @return
		 */
		public IAST take(final IAST list, final int level, final ISequence[] sequenceSpecifications) {
			ISequence sequ = sequenceSpecifications[level];
			sequ.setListSize(list.size());
			final IAST resultList = list.copyHead();
			final int newLevel = level + 1;
			int start = sequ.getStart();
			int end = sequ.getEnd();
			int step = sequ.getStep();
			if (step < 0) {
				end--;
				if (start < end || end <= 0) {
					throw new IllegalArgument(
							"Cannot execute take positions " + start + " through " + end + " in " + list);
					// return F.NIL;
				}
				for (int i = start; i >= end; i += step) {
					if (sequenceSpecifications.length > newLevel) {
						if (list.get(i).isAST()) {
							resultList.append(take((IAST) list.get(i), newLevel, sequenceSpecifications));
						} else {
							throw new IllegalArgument("Cannot execute take for argument: " + list.get(i).toString());
						}
					} else {
						resultList.append(list.get(i));
					}
				}
			} else {
				if (start == 0) {
					return resultList;
				}
				for (int i = start; i < end; i += step) {
					if (sequenceSpecifications.length > newLevel) {
						if (list.get(i).isAST()) {
							resultList.append(take((IAST) list.get(i), newLevel, sequenceSpecifications));
						} else {
							throw new IllegalArgument("Cannot execute take for argument: " + list.get(i).toString());
						}
					} else {
						resultList.append(list.get(i));
					}
				}
			}
			return resultList;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.NHOLDREST);
		}
	}

	/**
	 * Union of two sets. See
	 * <a href="http://en.wikipedia.org/wiki/Union_(set_theory)">Union (set
	 * theory)</a>
	 */
	private final static class Union extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			if (ast.isAST1() && ast.arg1().isAST()) {
				final IAST result = F.List();
				IAST arg1 = (IAST) ast.arg1();
				Set<IExpr> set = arg1.asSet();
				for (IExpr IExpr : set) {
					result.append(IExpr);
				}
				EvalAttributes.sort(result, Comparators.ExprComparator.CONS);
				return result;
			}

			if (ast.arg1().isAST() && ast.arg2().isAST()) {
				IAST arg1AST = ((IAST) ast.arg1());
				IAST arg2AST = ((IAST) ast.arg2());
				final IAST result = F.List();
				return union(arg1AST, arg2AST, result);
			}
			return F.NIL;
		}

		/**
		 * Create the (ordered) union from both ASTs.
		 * 
		 * @param ast1
		 *            first AST set
		 * @param ast2
		 *            second AST set
		 * @param result
		 *            the AST where the elements of the union should be appended
		 * @return
		 */
		public static IExpr union(IAST ast1, IAST ast2, final IAST result) {
			Set<IExpr> resultSet = new TreeSet<IExpr>();
			int size = ast1.size();
			for (int i = 1; i < size; i++) {
				resultSet.add(ast1.get(i));
			}
			size = ast2.size();
			for (int i = 1; i < size; i++) {
				resultSet.add(ast2.get(i));
			}
			for (IExpr expr : resultSet) {
				result.append(expr);
			}
			return result;
		}

	}

	final static ListFunctions CONST = new ListFunctions();

	public static ListFunctions initialize() {
		return CONST;
	}

	private ListFunctions() {

	}
}
