package org.matheclipse.core.builtin;

import static org.matheclipse.core.expression.F.List;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import org.hipparchus.linear.FieldMatrix;
import org.hipparchus.linear.RealMatrix;
import org.hipparchus.stat.StatUtils;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.IllegalArgument;
import org.matheclipse.core.eval.exception.NoEvalException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractMatrix1Expr;
import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.eval.util.ISequence;
import org.matheclipse.core.eval.util.Iterator;
import org.matheclipse.core.eval.util.LevelSpec;
import org.matheclipse.core.eval.util.LevelSpecification;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.eval.util.Sequence;
import org.matheclipse.core.eval.util.TableGenerator;
import org.matheclipse.core.expression.ASTRange;
import org.matheclipse.core.expression.ASTRealMatrix;
import org.matheclipse.core.expression.ASTRealVector;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.BinaryMap;
import org.matheclipse.core.generic.Comparators;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.generic.MultipleArrayFunction;
import org.matheclipse.core.generic.MultipleConstArrayFunction;
import org.matheclipse.core.generic.PositionConverter;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.generic.UnaryArrayFunction;
import org.matheclipse.core.generic.UnaryRangeFunction;
import org.matheclipse.core.generic.interfaces.IIterator;
import org.matheclipse.core.generic.interfaces.IPositionConverter;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.patternmatching.PatternMatcher;
import org.matheclipse.core.reflection.system.Product;
import org.matheclipse.core.reflection.system.Sum;
import org.matheclipse.core.visit.VisitorLevelSpecification;
import org.matheclipse.core.visit.VisitorRemoveLevelSpecification;
import org.matheclipse.parser.client.math.MathException;

public final class ListFunctions {
	static {
		F.Append.setEvaluator(new Append());
		F.AppendTo.setEvaluator(new AppendTo());
		F.Array.setEvaluator(new Array());
		F.Cases.setEvaluator(new Cases());
		F.Catenate.setEvaluator(new Catenate());
		F.Commonest.setEvaluator(new Commonest());
		F.Complement.setEvaluator(new Complement());
		F.Composition.setEvaluator(new Composition());
		F.ConstantArray.setEvaluator(new ConstantArray());
		F.Count.setEvaluator(new Count());
		F.Covariance.setEvaluator(new Covariance());
		F.DeleteDuplicates.setEvaluator(new DeleteDuplicates());
		F.DeleteCases.setEvaluator(new DeleteCases());
		F.Drop.setEvaluator(new Drop());
		F.Extract.setEvaluator(new Extract());
		F.First.setEvaluator(new First());
		F.Fold.setEvaluator(new Fold());
		F.FoldList.setEvaluator(new FoldList());
		F.Gather.setEvaluator(new Gather());
		F.Intersection.setEvaluator(new Intersection());
		F.Join.setEvaluator(new Join());
		F.Kurtosis.setEvaluator(new Kurtosis());
		F.Last.setEvaluator(new Last());
		F.Length.setEvaluator(new Length());
		F.LevelQ.setEvaluator(new LevelQ());
		F.Level.setEvaluator(new Level());
		F.Mean.setEvaluator(new Mean());
		F.Median.setEvaluator(new Median());
		F.Most.setEvaluator(new Most());
		F.Nearest.setEvaluator(new Nearest());
		F.PadLeft.setEvaluator(new PadLeft());
		F.PadRight.setEvaluator(new PadRight());
		F.Position.setEvaluator(new Position());
		F.Prepend.setEvaluator(new Prepend());
		F.PrependTo.setEvaluator(new PrependTo());
		F.Range.setEvaluator(new Range());
		F.Rest.setEvaluator(new Rest());
		F.Reverse.setEvaluator(new Reverse());
		F.ReplacePart.setEvaluator(new ReplacePart());
		F.Riffle.setEvaluator(new Riffle());
		F.RotateLeft.setEvaluator(new RotateLeft());
		F.RotateRight.setEvaluator(new RotateRight());
		F.Select.setEvaluator(new Select());
		F.Skewness.setEvaluator(new Skewness());
		F.Split.setEvaluator(new Split());
		F.SplitBy.setEvaluator(new SplitBy());
		F.Table.setEvaluator(new Table());
		F.Take.setEvaluator(new Take());
		F.Tally.setEvaluator(new Tally());
		F.Total.setEvaluator(new Total());
		F.Union.setEvaluator(new Union());
		F.Variance.setEvaluator(new Variance());
	}

	/**
	 * 
	 * <p>
	 * See the online Symja function reference:
	 * <a href= "https://bitbucket.org/axelclk/symja_android_library/wiki/Symbols/Append">Append</a>
	 * </p>
	 *
	 */
	private final static class Append extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);
			IExpr arg1 = engine.evaluate(ast.arg1());
			IAST arg1AST = Validate.checkASTType(arg1, engine);
			if (arg1AST == null) {
				return F.NIL;
			}
			IExpr arg2 = engine.evaluate(ast.arg2());
			return arg1AST.appendClone(arg2);
		}

	}

	/**
	 * 
	 * <p>
	 * See the online Symja function reference:
	 * <a href= "https://bitbucket.org/axelclk/symja_android_library/wiki/Symbols/AppendTo">AppendTo</a>
	 * </p>
	 *
	 */
	private final static class AppendTo extends AbstractCoreFunctionEvaluator {

		static class AppendToFunction implements Function<IExpr, IExpr> {
			private final IExpr value;

			public AppendToFunction(final IExpr value) {
				this.value = value;
			}

			@Override
			public IExpr apply(final IExpr symbolValue) {
				if (!symbolValue.isAST()) {
					return null;
				}
				return ((IAST) symbolValue).appendClone(value);
			}

		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);
			ISymbol sym = Validate.checkSymbolType(ast, 1, engine);
			if (sym == null) {
				return F.NIL;
			}
			IExpr arg2 = engine.evaluate(ast.arg2());
			Function<IExpr, IExpr> function = new AppendToFunction(arg2);
			IExpr[] results = sym.reassignSymbolValue(function, F.AppendTo, engine);
			if (results != null) {
				return results[1];
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDFIRST);
		}
	}

	/**
	 * Array structure generator
	 * 
	 * <p>
	 * See the online Symja function reference:
	 * <a href= "https://bitbucket.org/axelclk/symja_android_library/wiki/Symbols/Array">Array</a>
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
	 * See the online Symja function reference:
	 * <a href= "https://bitbucket.org/axelclk/symja_android_library/wiki/Symbols/Cases"> Cases</a>
	 * </p>
	 */
	private final static class Cases extends AbstractCoreFunctionEvaluator {
		/**
		 * StopException will be thrown, if maximum number of Cases results are reached
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

	private final static class Catenate extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			if (ast.arg1().isList()) {
				IAST list = (IAST) ast.arg1();
				int size = 1;
				for (int i = 1; i < list.size(); i++) {
					if (!list.get(i).isList()) {
						return F.NIL;
					}
					size += list.size() - 1;
				}
				IAST resultList = F.ast(F.List, size, false);
				for (int i = 1; i < list.size(); i++) {
					resultList.appendArgs((IAST) list.get(i));
				}
				return resultList;
			}
			return F.NIL;
		}

	}

	private final static class Commonest extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);
			IAST list = Validate.checkListType(ast, 1);

			int n = -1;
			if (ast.isAST2()) {
				n = Validate.checkIntType(ast.arg2());
			}

			IAST tallyResult = Tally.tally1Arg(list);
			EvalAttributes.sort(tallyResult, new Comparator<IExpr>() {
				@Override
				public int compare(IExpr o1, IExpr o2) {
					return ((IAST) o2).arg2().compareTo(((IAST) o1).arg2());
				}
			});

			IAST result = F.List();
			if (tallyResult.size() > 1) {
				if (n == -1) {
					IInteger max = (IInteger) ((IAST) tallyResult.arg1()).arg2();
					result.append(((IAST) tallyResult.arg1()).arg1());
					for (int i = 2; i < tallyResult.size(); i++) {
						if (max.equals(((IAST) tallyResult.get(i)).arg2())) {
							result.append(((IAST) tallyResult.get(i)).arg1());
						} else {
							break;
						}
					}
				} else {
					int counter = 0;
					for (int i = 1; i < tallyResult.size(); i++) {
						if (counter < n) {
							result.append(((IAST) tallyResult.get(i)).arg1());
							counter++;
						} else {
							break;
						}
					}
				}
			}
			return result;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
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
						return F.constantArray(ast.arg1(), indx1);
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
	 * Count the number of elements in an expression which match the given pattern.
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

	/**
	 * Compute the covariance.
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Covariance">Covariance</a>
	 * 
	 */
	private final static class Covariance extends AbstractMatrix1Expr {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);
			if (ast.size() == 2) {
				return super.evaluate(ast, engine);
			}

			if (ast.size() == 3) {
				final IAST arg1 = (IAST) ast.arg1();
				final IAST arg2 = (IAST) ast.arg2();
				return evaluateArg2(arg1, arg2, engine);
			}
			return F.NIL;
		}

		private IExpr evaluateArg2(final IAST arg1, final IAST arg2, EvalEngine engine) {
			try {
				// if (engine.isApfloat()) {
				// FieldMatrix<IExpr> arg1FieldMatrix =
				// Convert.list2Matrix(arg1);
				// if (arg1FieldMatrix != null) {
				// FieldMatrix<IExpr> arg2FieldMatrix =
				// Convert.list2Matrix(arg2);
				// if (arg1FieldMatrix != null) {
				// return matrixEval2(arg1FieldMatrix, arg2FieldMatrix);
				// }
				// }
				// return F.NIL;
				// }
				int arg1Length = arg1.isVector();
				if (arg1Length > 1) {
					int arg2Length = arg2.isVector();
					if (arg1Length == arg2Length) {
						try {
							double[] arg1DoubleArray = arg1.toDoubleVector();
							double[] arg2DoubleArray = arg2.toDoubleVector();
							org.hipparchus.stat.correlation.Covariance cov = new org.hipparchus.stat.correlation.Covariance();
							return F.num(cov.covariance(arg1DoubleArray, arg2DoubleArray, true));
						} catch (Exception ex) {
							//
						}
						return vectorCovarianceSymbolic(arg1, arg2, arg1Length);
					}
				}
			} catch (final WrongArgumentType e) {
				// WrongArgumentType occurs in list2RealMatrix(),
				// if the matrix elements aren't pure numerical values
			} catch (final IndexOutOfBoundsException e) {
				if (Config.SHOW_STACKTRACE) {
					e.printStackTrace();
				}
			}
			return F.NIL;
		}

		public static IExpr vectorCovarianceSymbolic(final IAST arg1, final IAST arg2, int arg1Length) {
			if (arg1Length == 2) {
				return F.Times(F.C1D2, F.Subtract(arg1.arg1(), arg1.arg2()),
						F.Subtract(F.Conjugate(arg2.arg1()), F.Conjugate(arg2.arg2())));
			}
			IAST num1 = arg1.apply(F.Plus);
			IExpr factor = F.integer(-1 * (arg1.size() - 2));
			IAST v1 = F.Plus();
			for (int i = 1; i < arg1.size(); i++) {
				v1.append(F.Times(F.CN1, num1.setAtClone(i, F.Times(factor, arg1.get(i))), F.Conjugate(arg2.get(i))));
			}
			return F.Divide(v1, F.integer((arg1.size() - 1) * (arg1.size() - 2)));
		}

		@Override
		public IExpr matrixEval(FieldMatrix<IExpr> matrix) {
			return F.NIL;
		}

		@Override
		public IExpr numericEval(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);
			if (ast.size() == 2) {
				return super.numericEval(ast, engine);
			}
			if (ast.size() == 3) {
				final IAST arg1 = (IAST) ast.arg1();
				final IAST arg2 = (IAST) ast.arg2();
				return evaluateArg2(arg1, arg2, engine);
			}
			return F.NIL;
		}

		@Override
		public IExpr realMatrixEval(RealMatrix matrix) {
			org.hipparchus.stat.correlation.Covariance cov = new org.hipparchus.stat.correlation.Covariance(matrix);
			return new ASTRealMatrix(cov.getCovarianceMatrix(), false);
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
	 * Drop(list,n) - delete the first n arguments from the list. Negative n counts from the end.
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
		 * Drop (remove) the list elements according to the <code>sequenceSpecifications</code> for the list indexes.
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
		 * Traverse all <code>list</code> element's and filter out the elements in the given <code>positions</code>
		 * list.
		 * 
		 * @param list
		 * @param positions
		 * @param positionConverter
		 *            the <code>positionConverter</code> creates an <code>int</code> value from the given position
		 *            objects in <code>positions</code>.
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

	private final static class Fold extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 4);
			return evaluateNestList(ast, engine);
		}

		public static IExpr evaluateNestList(final IAST ast, EvalEngine engine) {

			try {
				IExpr temp = engine.evaluate(ast.arg3());
				if (temp.isAST()) {
					final IAST list = (IAST) temp;
					IExpr arg1 = engine.evaluate(ast.arg1());
					IExpr arg2 = engine.evaluate(ast.arg2());
					return list.args().foldLeft(new BinaryMap(arg1), arg2);
				}
			} catch (final ArithmeticException e) {

			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	private final static class FoldList extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 4);

			return evaluateNestList(ast, List(), engine);
		}

		public static IExpr evaluateNestList(final IAST ast, final IAST resultList, EvalEngine engine) {

			try {
				IExpr temp = engine.evaluate(ast.arg3());
				if (temp.isAST()) {
					final IAST list = (IAST) temp;
					IExpr arg1 = engine.evaluate(ast.arg1());
					IExpr arg2 = engine.evaluate(ast.arg2());
					foldLeft(arg2, list, 1, list.size(), new BinaryMap(arg1), resultList);
					return resultList;
				}
			} catch (final ArithmeticException e) {

			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	private final static class Gather extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			int size = ast.size();
			if (ast.arg1().isAST()) {
				IAST arg1 = (IAST) ast.arg1();
				java.util.Map<IExpr, IAST> map;
				if (size > 2) {
					IExpr arg2 = ast.arg2();
					map = new TreeMap<IExpr, IAST>(new Comparators.BinaryHeadComparator(arg2));
				} else {
					map = new TreeMap<IExpr, IAST>();
				}
				for (int i = 1; i < arg1.size(); i++) {
					IAST list = map.get(arg1.get(i));
					if (list == null) {
						list = F.List();
						list.append(arg1.get(i));
						map.put(arg1.get(i), list);
					} else {
						list.append(arg1.get(i));
					}
				}

				IAST result = F.List();
				for (Map.Entry<IExpr, IAST> entry : map.entrySet()) {
					result.append(entry.getValue());
				}
				return result;
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	/**
	 * Intersection of 2 sets
	 * 
	 * See: <a href= "http://en.wikipedia.org/wiki/Intersection_(set_theory)">Intersection (set theory)</a>
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
			IExpr head = null;
			IAST temp;
			for (int i = 1; i < astSize; i++) {
				temp = (IAST) ast.get(i);
				size += temp.size() - 1;
				if (head == null) {
					head = temp.head();
				} else {
					if (!head.equals(temp.head())) {
						engine.printMessage("Join: Heads " + head.toString() + " and " + temp.head().toString()
								+ " are expected to be the same.");
						return F.NIL;
					}
				}

			}
			final IAST result = F.ast(head, size, false);
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
				// throws MathException if Level isn't defined correctly
				new VisitorLevelSpecification(null, arg1, false);
				return F.True;
			} catch (MathException me) {
				// thrown in VisitorLevelSpecification ctor
			}
			return F.False;
		}

	}

	/**
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Arithmetic_mean">Arithmetic mean</a>
	 */
	private final static class Mean extends AbstractTrigArg1 {

		@Override
		public IExpr evaluateArg1(final IExpr arg1) {
			if (arg1.isRealVector()) {
				return F.num(StatUtils.mean(arg1.toDoubleVector()));
			}
			if (arg1.isList()) {
				final IAST list = (IAST) arg1;
				return F.Times(list.apply(F.Plus), F.Power(F.integer(list.size() - 1), F.CN1));
			}

			if (arg1.isAST()) {
				IAST dist = (IAST) arg1;
				if (dist.head().isSymbol()) {
					ISymbol head = (ISymbol) dist.head();
					if (arg1.isAST1()) {
						if (head.equals(F.BernoulliDistribution)) {
							return dist.arg1();
						} else if (head.equals(F.PoissonDistribution)) {
							return dist.arg1();
						}
					} else if (arg1.isAST2()) {
						if (head.equals(F.BinomialDistribution)) {
							return F.Times(dist.arg1(), dist.arg2());
						} else if (head.equals(F.NormalDistribution)) {
							return dist.arg1();
						}
					} else if (arg1.isAST3()) {
						IExpr n = dist.arg1();
						IExpr nSucc = dist.arg2();
						IExpr nTot = dist.arg3();
						if (head.equals(F.HypergeometricDistribution)) {
							return F.Divide(F.Times(n, nSucc), nTot);
						}
					}
				}
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.NOATTRIBUTE);
		}

	}

	/**
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Median">Median</a>
	 */
	private final static class Median extends AbstractTrigArg1 {

		@Override
		public IExpr evaluateArg1(final IExpr arg1) {
			if (arg1.isRealVector()) {
				return F.num(StatUtils.percentile(arg1.toDoubleVector(), 50));
			}
			if (arg1.isList()) {
				final IAST list = (IAST) arg1;
				if (list.size() > 1) {
					final IAST sortedList = list.copy();
					EvalAttributes.sort(sortedList);
					int size = sortedList.size();
					if ((size & 0x00000001) == 0x00000001) {
						// odd number of elements
						size = size / 2;
						return F.Times(F.Plus(sortedList.get(size), sortedList.get(size + 1)), F.C1D2);
					} else {
						return sortedList.get(size / 2);
					}
				}
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.NOATTRIBUTE);
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

	private final static class Nearest extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 4);

			if (ast.arg1().isAST()) {
				if (ast.size() == 3 && ast.arg2().isNumber()) {
					IAST listArg1 = (IAST) ast.arg1();
					if (listArg1.size() > 1) {
						INumber arg2 = (INumber) ast.arg2();
						// Norm() is the default distance function for numeric
						// data
						IExpr distanceFunction = F.Function(F.Norm(F.Subtract(F.Slot1, F.Slot2)));
						return numericalNearest(listArg1, arg2, distanceFunction, engine);
					}
				}
			}

			return F.NIL;
		}

		/**
		 * Gives the list of elements from <code>inputList</code> to which x is nearest.
		 * 
		 * @param inputList
		 * @param x
		 * @param engine
		 * @return the list of elements from <code>inputList</code> to which x is nearest
		 */
		private static IAST numericalNearest(IAST inputList, INumber x, IExpr distanceFunction, EvalEngine engine) {
			try {
				IAST nearest = null;
				IExpr distance = F.NIL;
				IAST temp;
				for (int i = 1; i < inputList.size(); i++) {
					temp = F.ast(distanceFunction);
					temp.append(x);
					temp.append(inputList.get(i));
					if (nearest == null) {
						nearest = F.List();
						nearest.append(inputList.get(i));
						distance = temp;
					} else {
						IExpr comparisonResult = engine.evaluate(F.Greater(distance, temp));
						if (comparisonResult.isTrue()) {
							nearest = F.List();
							nearest.append(inputList.get(i));
							distance = temp;
						} else if (comparisonResult.isFalse()) {
							if (engine.evalTrue(F.Equal(distance, temp))) {
								nearest.append(inputList.get(i));
							}
							continue;
						} else {
							// undefined
							return F.NIL;
						}
					}
				}
				return nearest;
			} catch (ClassCastException cce) {
			} catch (RuntimeException rex) {
			}
			return F.NIL;
		}
	}

	private final static class Kurtosis extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);
			if (ast.arg1().isList()) {
				IAST list = (IAST) ast.arg1();
				return F.Divide(F.CentralMoment(list, F.C4), F.Power(F.CentralMoment(list, F.C2), F.C2));
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	private final static class PadLeft extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 4);

			if (ast.isAST1()) {
				if (ast.arg1().isListOfLists()) {
					IAST list = (IAST) ast.arg1();
					int maxSize = -1;
					for (int i = 1; i < list.size(); i++) {
						IAST subList = (IAST) list.get(i);
						if (subList.size() > maxSize) {
							maxSize = subList.size();
						}
					}
					if (maxSize > 0) {
						IAST result = F.ListAlloc(list.size());
						for (int i = 1; i < list.size(); i++) {
							result.append(padLeftAtom(list.getAST(i), maxSize - 1, F.C0));
						}
						return result;
					}
				}
				return ast.arg1();
			}
			int n = Validate.checkIntType(ast, 2);
			if (ast.arg1().isAST()) {
				IAST arg1 = (IAST) ast.arg1();
				if (ast.size() > 3) {
					if (ast.arg3().isList()) {
						IAST arg3 = (IAST) ast.arg3();
						return padLeftAST(arg1, n, arg3);
					} else {
						return padLeftAtom(arg1, n, ast.arg3());
					}
				} else {
					return padLeftAtom(arg1, n, F.C0);
				}
			}
			return F.NIL;
		}

		public static IExpr padLeftAtom(IAST ast, int n, IExpr atom) {
			int length = n - ast.size() + 1;
			if (length > 0) {
				IAST result = ast.copyHead();
				for (int i = 0; i < length; i++) {
					result.append(atom);
				}
				result.appendArgs(ast);
				return result;
			}
			if (n > 0 && n < ast.size()) {
				return ast.copyFrom(ast.size() - n);
			}
			return ast;
		}

		public static IAST padLeftAST(IAST ast, int n, IAST arg2) {
			int length = n - ast.size() + 1;
			if (length > 0) {

				IAST result = ast.copyHead();
				if (arg2.size() < 2) {
					return ast;
				}
				int j = 1;
				if ((arg2.size() - 1) < n) {
					int temp = n % (arg2.size() - 1);
					j = arg2.size() - temp;
				}
				for (int i = 0; i < length; i++) {
					if (j < arg2.size()) {
						result.append(arg2.get(j++));
					} else {
						j = 1;
						result.append(arg2.get(j++));
					}
				}
				result.appendArgs(ast);
				return result;
			}
			return ast;
		}
	}

	private final static class PadRight extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 4);

			if (ast.isAST1()) {
				if (ast.arg1().isListOfLists()) {
					IAST list = (IAST) ast.arg1();
					int maxSize = -1;
					for (int i = 1; i < list.size(); i++) {
						IAST subList = (IAST) list.get(i);
						if (subList.size() > maxSize) {
							maxSize = subList.size();
						}
					}
					if (maxSize > 0) {
						IAST result = F.ListAlloc(list.size());
						for (int i = 1; i < list.size(); i++) {
							result.append(padRightAtom(list.getAST(i), maxSize - 1, F.C0));
						}
						return result;
					}
				}
				return ast.arg1();
			}

			int n = Validate.checkIntType(ast, 2);
			if (ast.arg1().isAST()) {
				IAST arg1 = (IAST) ast.arg1();
				if (ast.size() > 3) {
					if (ast.arg3().isList()) {
						IAST arg3 = (IAST) ast.arg3();
						return padRightAST(arg1, n, arg3);
					} else {
						return padRightAtom(arg1, n, ast.arg3());
					}
				} else {
					return padRightAtom(arg1, n, F.C0);
				}
			}
			return F.NIL;
		}

		public static IExpr padRightAtom(IAST ast, int n, IExpr atom) {
			int length = n - ast.size() + 1;
			if (length > 0) {
				IAST result = ast.copyHead();
				result.appendArgs(ast);
				for (int i = 0; i < length; i++) {
					result.append(atom);
				}
				return result;
			}
			if (n > 0 && n < ast.size()) {
				return ast.copyUntil(n + 1);
			}
			return ast;
		}

		public static IAST padRightAST(IAST ast, int n, IAST arg2) {
			int length = n - ast.size() + 1;
			if (length > 0) {
				IAST result = ast.copyHead();
				result.appendArgs(ast);
				if (arg2.size() < 2) {
					return ast;
				}
				int j = 1;
				for (int i = 0; i < length; i++) {
					if (j < arg2.size()) {
						result.append(arg2.get(j++));
					} else {
						j = 1;
						result.append(arg2.get(j++));
					}
				}
				return result;
			}
			return ast;
		}
	}

	/**
	 * Position(list, pattern) - return the positions where the pattern occurs in list.
	 *
	 */
	private final static class Position extends AbstractCoreFunctionEvaluator {

		/**
		 * Add the positions to the <code>resultCollection</code> where the matching expressions appear in
		 * <code>list</code>. The <code>positionConverter</code> converts the <code>int</code> position into an object
		 * for the <code>resultCollection</code>.
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
				if (list.get(i).isAST()) {
					// clone = (INestedList<IExpr>) prototypeList.clone();
					clone = prototypeList.clone();
					clone.append(positionConverter.toObject(i));
					position((IAST) list.get(i), clone, resultCollection, level, matcher, positionConverter,
							headOffset);
					if (level.getCurrentDepth() < minDepth) {
						minDepth = level.getCurrentDepth();
					}
				}
				if (matcher.test(list.get(i))) {
					if (level.isInRange()) {
						clone = prototypeList.clone();
						IExpr IExpr = positionConverter.toObject(i);
						clone.append(IExpr);
						resultCollection.append(clone);
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

	private final static class Prepend extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);
			IExpr arg1 = engine.evaluate(ast.arg1());
			IAST arg1AST = Validate.checkASTType(arg1, engine);
			if (arg1AST == null) {
				return F.NIL;
			}
			IExpr arg2 = engine.evaluate(ast.arg2());
			return arg1AST.appendAtClone(1, arg2);
		}

	}

	private final static class PrependTo extends AbstractCoreFunctionEvaluator {

		static class PrependToFunction implements Function<IExpr, IExpr> {
			private final IExpr value;

			public PrependToFunction(final IExpr value) {
				this.value = value;
			}

			@Override
			public IExpr apply(final IExpr symbolValue) {
				if (!symbolValue.isAST()) {
					return F.NIL;
				}
				return ((IAST) symbolValue).appendAtClone(1, value);
			}

		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);
			ISymbol sym = Validate.checkSymbolType(ast, 1, engine);
			if (sym == null) {
				return F.NIL;
			}
			IExpr arg2 = engine.evaluate(ast.arg2());
			Function<IExpr, IExpr> function = new PrependToFunction(arg2);
			IExpr[] results = sym.reassignSymbolValue(function, F.PrependTo, engine);
			if (results != null) {
				return results[1];
			}

			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDFIRST);
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
	 * Return the <i>rest</i> of a given list, i.e. a sublist with all elements from list[[2]]...list[[n]]
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

	private final static class Reverse extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST functionList, EvalEngine engine) {
			if (functionList.size() != 2) {
				return F.NIL;
			}
			if (!functionList.arg1().isAtom()) {
				final IAST result = F.ast(functionList.arg1().head());
				((IAST) functionList.arg1()).args().reverse(result.args());
				return result;
			}
			return F.NIL;
		}

	}

	private final static class Riffle extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			IExpr arg1 = engine.evaluate(ast.arg1());
			IExpr arg2 = engine.evaluate(ast.arg2());
			if (arg1.isAST()) {
				IAST list = (IAST) arg1;
				if (arg2.isAST()) {
					return riffleAST(list, (IAST) arg2);
				} else {
					return riffleAtom(list, arg2);
				}
			}
			return F.NIL;
		}

		public static IExpr riffleAtom(IAST arg1, IExpr arg2) {
			if (arg1.size() < 2) {
				return arg1;
			}
			IAST result = arg1.copyHead();
			for (int i = 1; i < arg1.size() - 1; i++) {
				result.append(arg1.get(i));
				result.append(arg2);
			}
			result.append(arg1.get(arg1.size() - 1));
			return result;
		}

		public static IAST riffleAST(IAST arg1, IAST arg2) {
			if (arg1.size() < 2) {
				return arg1;
			}
			IAST result = arg1.copyHead();
			if (arg2.size() < 2) {
				return arg1;
			}
			int j = 1;
			for (int i = 1; i < arg1.size() - 1; i++) {
				result.append(arg1.get(i));
				if (j < arg2.size()) {
					result.append(arg2.get(j++));
				} else {
					j = 1;
					result.append(arg2.get(j++));
				}
			}
			result.append(arg1.get(arg1.size() - 1));
			if (j < arg2.size()) {
				result.append(arg2.get(j++));
			}
			return result;
		}
	}

	private final static class RotateLeft extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			IExpr arg1 = engine.evaluate(ast.arg1());
			if (arg1.isAST()) {
				final IAST result = F.ast(arg1.head());
				if (ast.isAST1()) {
					ASTRange range = ((IAST) arg1).args();
					range.rotateLeft(result, 1);
					// Rotating.rotateLeft((IAST) list.arg1(), result, 2, 1);
					return result;
				} else {
					IExpr arg2 = engine.evaluate(ast.arg2());
					if (arg2.isInteger()) {
						int n = Validate.checkIntType(arg2);

						ASTRange range = ((IAST) arg1).args();
						range.rotateLeft(result, n);
						return result;
					}
				}

			}
			return F.NIL;
		}

	}

	private final static class RotateRight extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			IExpr arg1 = engine.evaluate(ast.arg1());
			if (arg1.isAST()) {
				final IAST result = F.ast(arg1.head());
				if (ast.isAST1()) {
					ASTRange range = ((IAST) arg1).args();
					range.rotateRight(result, 1);
					// Rotating.rotateRight((IAST) list.arg1(), result, 1, 1);
					return result;
				} else {
					IExpr arg2 = engine.evaluate(ast.arg2());
					if (arg2.isInteger()) {
						int n = Validate.checkIntType(arg2);
						ASTRange range = ((IAST) arg1).args();
						range.rotateRight(result, n);
						return result;
					}
				}

			}
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

	/**
	 * <code>Skewness(list)</code> gives Pearson's moment coefficient of skewness for <code>list</code> (a measure for
	 * estimating the symmetry of a distribution).
	 *
	 */
	private final static class Skewness extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);
			if (ast.arg1().isList()) {
				IAST list = (IAST) ast.arg1();
				return F.Divide(F.CentralMoment(list, F.C3), F.Power(F.CentralMoment(list, F.C2), F.C3D2));
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

	/**
	 * Table structure generator (i.e. lists, vectors, matrices, tensors)
	 */
	public static class Table extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3);

			return evaluateTable(ast, List(), List(), engine);
		}

		/**
		 * Generate a table from standard iterator notation.
		 * 
		 * @param ast
		 * @param resultList
		 *            the result list to which the generated expressions should be appended.
		 * @param defaultValue
		 *            the default value used in the iterator
		 * @param engine
		 *            the current evaluation engine
		 * @return <code>F.NIL</code> if no evaluation is possible
		 */
		protected static IExpr evaluateTable(final IAST ast, final IAST resultList, IExpr defaultValue,
				EvalEngine engine) {
			try {
				if (ast.size() > 2) {
					final List<IIterator<IExpr>> iterList = new ArrayList<IIterator<IExpr>>();
					for (int i = 2; i < ast.size(); i++) {
						if (ast.get(i).isList()) {
							iterList.add(Iterator.create((IAST) ast.get(i), engine));
						} else {
							iterList.add(Iterator.create(F.List(ast.get(i)), engine));
						}
					}

					final TableGenerator generator = new TableGenerator(iterList, resultList,
							new UnaryArrayFunction(engine, ast.arg1()), defaultValue);
					return generator.table();
				}
			} catch (final ClassCastException e) {
				// the iterators are generated only from IASTs
			} catch (final NoEvalException e) {
			}
			return F.NIL;
		}
		
		protected static IExpr evaluateTableThrow(final IAST ast, final IAST resultList, IExpr defaultValue,
				EvalEngine engine) {
			try {
				if (ast.size() > 2) {
					final List<IIterator<IExpr>> iterList = new ArrayList<IIterator<IExpr>>();
					for (int i = 2; i < ast.size(); i++) {
						if (ast.get(i).isList()) {
							iterList.add(Iterator.create((IAST) ast.get(i), engine));
						} else {
							iterList.add(Iterator.create(F.List(ast.get(i)), engine));
						}
					}

					final TableGenerator generator = new TableGenerator(iterList, resultList,
							new UnaryArrayFunction(engine, ast.arg1()), defaultValue);
					return generator.tableThrow();
				}
			} catch (final ClassCastException e) {
				// the iterators are generated only from IASTs
			} catch (final NoEvalException e) {
			}
			return F.NIL;
		}

		/**
		 * Evaluate only the last iterator in <code>ast</code> (i.e. <code>ast.get(ast.size() - 1)</code>) for
		 * <code>Sum()</code> or <code>Product()</code> function calls.
		 * 
		 * @param expr
		 * @param iter
		 *            the iterator function
		 * @param resultList
		 *            the result list to which the generated expressions should be appended.
		 * @param defaultValue
		 *            the default value used if the iterator is invalid
		 * @return <code>F.NIL</code> if no evaluation is possible
		 * @see Product
		 * @see Sum
		 */
		protected static IExpr evaluateLast(final IExpr expr, final IIterator<IExpr> iter, final IAST resultList,
				IExpr defaultValue) {
			try {
				final List<IIterator<IExpr>> iterList = new ArrayList<IIterator<IExpr>>();
				iterList.add(iter);

				final TableGenerator generator = new TableGenerator(iterList, resultList,
						new UnaryArrayFunction(EvalEngine.get(), expr), defaultValue);
				return generator.table();
			} catch (final ClassCastException e) {
				// the iterators are generated only from IASTs
			} catch (final NoEvalException e) {
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

		/**
		 * Determine all local variables of the iterators starting with index <code>2</code>.
		 * 
		 * @param ast
		 * @return
		 */
		public IAST determineIteratorVariables(final IAST ast) {
			IAST variableList = F.List();
			for (int i = 2; i < ast.size(); i++) {
				if (ast.get(i).isVariable()) {
					variableList.append(ast.get(i));
				} else {
					if (ast.get(i).isList()) {
						IAST list = (IAST) ast.get(i);
						if (list.size() >= 2) {
							if (list.arg1().isVariable()) {
								variableList.append(list.arg1());
							}
						}
					}
				}
			}
			return variableList;
		}

		/**
		 * Determine all local variables of the iterators starting with index <code>2</code> in the given
		 * <code>ast</code>.
		 * 
		 * @param ast
		 * @return
		 */
		public VariablesSet determineIteratorExprVariables(final IAST ast) {
			VariablesSet variableList = new VariablesSet();
			for (int i = 2; i < ast.size(); i++) {
				if (ast.get(i).isVariable()) {
					variableList.add((ISymbol) ast.get(i));
				} else {
					if (ast.get(i).isList()) {
						IAST list = (IAST) ast.get(i);
						if (list.size() >= 2) {
							if (list.arg1().isVariable()) {
								variableList.add((ISymbol) list.arg1());
							}
						}
					}
				}
			}
			return variableList;
		}

		/**
		 * Disable the <code>Reap() and Sow()</code> mode temporary and evaluate an expression for the given &quot;local
		 * variables list&quot;. If evaluation is not possible return the input object.
		 * 
		 * @param expr
		 *            the expression which should be evaluated
		 * @param localVariablesList
		 *            a list of symbols which should be used as local variables inside the block
		 * @return the evaluated object
		 */
		public static IExpr evalBlockWithoutReap(IExpr expr, IAST localVariablesList) {
			EvalEngine engine = EvalEngine.get();
			IAST reapList = engine.getReapList();
			boolean quietMode = engine.isQuietMode();
			try {
				engine.setQuietMode(true);
				engine.setReapList(null);
				return engine.evalBlock(expr, localVariablesList);
			} catch (RuntimeException rex) {
				// ignore
			} finally {
				engine.setReapList(reapList);
				engine.setQuietMode(quietMode);
			}
			return expr;
		}
	}

	private final static class Tally extends AbstractEvaluator {

		private static IAST createResultList(java.util.Map<IExpr, Integer> map) {
			IAST result = F.List();
			for (java.util.Map.Entry<IExpr, Integer> entry : map.entrySet()) {
				result.append(F.List(entry.getKey(), F.integer(entry.getValue())));
			}
			return result;
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);
			IAST list = Validate.checkListType(ast, 1);

			int size = ast.size();

			if (size == 2) {
				return tally1Arg(list);
			} else if (size == 3) {
				BiPredicate<IExpr, IExpr> biPredicate = Predicates.isBinaryTrue(ast.arg2());
				return tally2Args(list, biPredicate);
			}

			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

		public static IAST tally1Arg(IAST list) {
			java.util.Map<IExpr, Integer> map = new LinkedHashMap<IExpr, Integer>();
			for (int i = 1; i < list.size(); i++) {
				Integer value = map.get(list.get(i));
				if (value == null) {
					map.put(list.get(i), Integer.valueOf(1));
				} else {
					map.put(list.get(i), Integer.valueOf(value + 1));
				}
			}
			return createResultList(map);
		}

		private static IAST tally2Args(IAST list, BiPredicate<IExpr, IExpr> biPredicate) {
			java.util.Map<IExpr, Integer> map = new LinkedHashMap<IExpr, Integer>();
			boolean evaledTrue;
			for (int i = 1; i < list.size(); i++) {
				evaledTrue = false;
				for (java.util.Map.Entry<IExpr, Integer> entry : map.entrySet()) {
					if (biPredicate.test(entry.getKey(), list.get(i))) {
						evaledTrue = true;
						map.put(entry.getKey(), Integer.valueOf(entry.getValue() + 1));
						break;
					}
				}
				if (!evaledTrue) {
					map.put(list.get(i), Integer.valueOf(1));
				}
			}
			return createResultList(map);
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
		 * Take the list elements according to the <code>sequenceSpecifications</code> for the list indexes.
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
			int size = list.size();
			sequ.setListSize(size);
			final IAST resultList = list.copyHead(10 > size ? size : 10);
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
	 * Sums up all elements of a list.
	 * 
	 */
	private final static class Total extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			VisitorLevelSpecification level = null;
			Function<IExpr, IExpr> tf = Functors.apply(F.Plus);
			if (ast.isAST2()) {
				level = new VisitorLevelSpecification(tf, ast.arg2(), false);
				// increment level because we select only subexpressions
			} else {
				level = new VisitorLevelSpecification(tf, 1, false);
			}

			if (ast.arg1().isAST()) {
				// increment level because we select only subexpressions
				level.incCurrentLevel();
				return ast.arg1().accept(level);
			}

			return F.NIL;
		}

	}

	/**
	 * Union of two sets. See <a href="http://en.wikipedia.org/wiki/Union_(set_theory)">Union (set theory)</a>
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

	/**
	 * Compute the variance for a list of elements
	 */
	private final static class Variance extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			if (ast.arg1().isAST()) {
				IAST arg1 = (IAST) ast.arg1();
				int dim = arg1.isVector();
				if (dim >= 0) {
					if (arg1.isRealVector()) {
						return F.num(StatUtils.variance(arg1.toDoubleVector()));
					}
					return Covariance.vectorCovarianceSymbolic(arg1, arg1, dim);
				}
				int[] matrixDimensions = arg1.isMatrix();
				if (matrixDimensions != null) {
					if (arg1.isRealMatrix()) {
						double[][] matrix = arg1.toDoubleMatrix();
						matrix = Convert.toDoubleTransposed(matrix);
						double[] result = new double[matrixDimensions[1]];
						for (int i = 0; i < matrix.length; i++) {
							result[i] = StatUtils.variance(matrix[i]);
						}
						return new ASTRealVector(result, false);
					}
					return F.NIL;
				}

				if (arg1.isAST()) {
					IAST dist = (IAST) arg1;
					if (dist.head().isSymbol()) {
						ISymbol head = (ISymbol) dist.head();
						if (arg1.isAST1()) {
							if (head.equals(F.BernoulliDistribution)) {
							} else if (head.equals(F.PoissonDistribution)) {
							}
						} else if (arg1.isAST2()) {
							if (head.equals(F.BinomialDistribution)) {
							} else if (head.equals(F.NormalDistribution)) {
							}
						} else if (arg1.isAST3()) {
							IExpr n = dist.arg1();
							IExpr nSucc = dist.arg2();
							IExpr nTot = dist.arg3();
							if (head.equals(F.HypergeometricDistribution)) {
							}
						}
					}
				}
			}
			return F.NIL;
		}

	}

	/**
	 * Fold the list from <code>start</code> index including to <code>end</code> index excluding into the
	 * <code>resultCollection</code>. If the <i>binaryFunction</i> returns <code>null</code>, the left element will be
	 * added to the result list, otherwise the result will be <i>folded</i> again with the next element in the list.
	 * 
	 * @param list
	 * @param start
	 * @param end
	 * @param binaryFunction
	 * @param resultCollection
	 */
	public static IAST foldLeft(final IExpr expr, final IAST list, final int start, final int end,
			final BiFunction<IExpr, IExpr, ? extends IExpr> binaryFunction, final IAST resultCollection) {
		if (start < end) {
			IExpr elem = expr;
			resultCollection.append(elem);
			for (int i = start; i < end; i++) {

				elem = binaryFunction.apply(elem, list.get(i));
				resultCollection.append(elem);
			}

		}
		return resultCollection;
	}

	final static ListFunctions CONST = new ListFunctions();

	public static ListFunctions initialize() {
		return CONST;
	}

	private ListFunctions() {

	}
}
