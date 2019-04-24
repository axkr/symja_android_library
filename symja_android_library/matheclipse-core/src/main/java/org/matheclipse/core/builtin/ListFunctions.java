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

import javax.annotation.Nonnull;

import org.matheclipse.core.basic.Config;
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
import org.matheclipse.core.eval.interfaces.IArrayFunction;
import org.matheclipse.core.eval.util.ISequence;
import org.matheclipse.core.eval.util.Iterator;
import org.matheclipse.core.eval.util.LevelSpec;
import org.matheclipse.core.eval.util.LevelSpecification;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.eval.util.Sequence;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Comparators;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IIterator;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.reflection.system.Product;
import org.matheclipse.core.reflection.system.Sum;
import org.matheclipse.core.visit.VisitorLevelSpecification;
import org.matheclipse.core.visit.VisitorRemoveLevelSpecification;
import org.matheclipse.parser.client.math.MathException;

public final class ListFunctions {
	/**
	 * 
	 * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation in static
	 * initializer</a>
	 */
	private static class Initializer {

		private static void init() {
			F.Accumulate.setEvaluator(new Accumulate());
			F.Append.setEvaluator(new Append());
			F.AppendTo.setEvaluator(new AppendTo());
			F.Array.setEvaluator(new Array());
			F.ArrayPad.setEvaluator(new ArrayPad());
			F.Cases.setEvaluator(new Cases());
			F.Catenate.setEvaluator(new Catenate());
			F.Commonest.setEvaluator(new Commonest());
			F.Complement.setEvaluator(new Complement());
			F.Composition.setEvaluator(new Composition());
			F.ComposeList.setEvaluator(new ComposeList());
			F.ConstantArray.setEvaluator(new ConstantArray());
			F.Count.setEvaluator(new Count());
			F.Delete.setEvaluator(new Delete());
			F.DeleteDuplicates.setEvaluator(new DeleteDuplicates());
			F.DeleteCases.setEvaluator(new DeleteCases());
			F.Drop.setEvaluator(new Drop());
			F.Extract.setEvaluator(new Extract());
			F.First.setEvaluator(new First());
			F.Fold.setEvaluator(new Fold());
			F.FoldList.setEvaluator(new FoldList());
			F.Gather.setEvaluator(new Gather());
			F.Insert.setEvaluator(new Insert());
			F.Intersection.setEvaluator(new Intersection());
			F.Join.setEvaluator(new Join());
			F.Last.setEvaluator(new Last());
			F.Length.setEvaluator(new Length());
			F.LevelQ.setEvaluator(new LevelQ());
			F.Level.setEvaluator(new Level());
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
			F.ReplaceAll.setEvaluator(new ReplaceAll());
			F.ReplacePart.setEvaluator(new ReplacePart());
			F.Riffle.setEvaluator(new Riffle());
			F.RotateLeft.setEvaluator(new RotateLeft());
			F.RotateRight.setEvaluator(new RotateRight());
			F.Select.setEvaluator(new Select());
			F.Split.setEvaluator(new Split());
			F.SplitBy.setEvaluator(new SplitBy());
			F.Subdivide.setEvaluator(new Subdivide());
			F.Table.setEvaluator(new Table());
			F.Take.setEvaluator(new Take());
			F.Tally.setEvaluator(new Tally());
			F.Total.setEvaluator(new Total());
			F.Union.setEvaluator(new Union());

		}
	}

	private static interface IPositionConverter<T> {
		/**
		 * Convert the integer position number >= 0 into an object
		 *
		 * @param position
		 *            which should be converted to an object
		 * @return
		 */
		T toObject(int position);

		/**
		 * Convert the object into an integer number >= 0
		 *
		 * @param position
		 *            the object which should be converted
		 * @return -1 if the conversion is not possible
		 */
		int toInt(T position);
	}

	public static class MultipleConstArrayFunction implements IArrayFunction {
		final IExpr fConstantExpr;

		public MultipleConstArrayFunction(final IExpr expr) {
			fConstantExpr = expr;
		}

		@Override
		public IExpr evaluate(final IExpr[] index) {
			return fConstantExpr;
		}
	}

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

	/**
	 * Table structure generator (i.e. lists, vectors, matrices, tensors)
	 */
	public static class TableGenerator {

		final List<? extends IIterator<IExpr>> fIterList;

		final IExpr fDefaultValue;

		final IAST fPrototypeList;

		final IArrayFunction fFunction;

		int fIndex;

		private IExpr[] fCurrentIndex;

		public TableGenerator(final List<? extends IIterator<IExpr>> iterList, final IAST prototypeList,
				final IArrayFunction function) {
			this(iterList, prototypeList, function, (IExpr) null);
		}

		public TableGenerator(final List<? extends IIterator<IExpr>> iterList, final IAST prototypeList,
				final IArrayFunction function, IExpr defaultValue) {
			fIterList = iterList;
			fPrototypeList = prototypeList;
			fFunction = function;
			fIndex = 0;
			fCurrentIndex = new IExpr[iterList.size()];
			fDefaultValue = defaultValue;
		}

		public IExpr table() {
			if (fIndex < fIterList.size()) {
				final IIterator<IExpr> iter = fIterList.get(fIndex);

				if (iter.setUp()) {
					try {
						final int index = fIndex++;
						if (fPrototypeList.head().equals(F.Plus) || fPrototypeList.head().equals(F.Times)) {
							if (iter.hasNext()) {
								fCurrentIndex[index] = iter.next();
								IExpr temp = table();
								if (temp == null) {
									temp = fDefaultValue;
								}
								if (temp.isNumber()) {
									if (fPrototypeList.head().equals(F.Plus)) {
										return tablePlus(temp, iter, index);
									} else {
										return tableTimes(temp, iter, index);
									}
								} else {
									return createGenericTable(iter, index, iter.allocHint(), temp, null);
								}
							}
							return fDefaultValue;
						}
						return createGenericTable(iter, index, iter.allocHint(), null, null);
					} finally {
						--fIndex;
						iter.tearDown();
					}
				}
				return fDefaultValue;

			}
			return fFunction.evaluate(fCurrentIndex);
		}

		public IExpr tableThrow() {
			if (fIndex < fIterList.size()) {
				final IIterator<IExpr> iter = fIterList.get(fIndex);

				try {
					if (iter.setUpThrow()) {
						final int index = fIndex++;
						if (fPrototypeList.head().equals(F.Plus) || fPrototypeList.head().equals(F.Times)) {
							if (iter.hasNext()) {
								fCurrentIndex[index] = iter.next();
								IExpr temp = table();
								if (temp == null) {
									temp = fDefaultValue;
								}
								if (temp.isNumber()) {
									if (fPrototypeList.head().equals(F.Plus)) {
										return tablePlus(temp, iter, index);
									} else {
										return tableTimes(temp, iter, index);
									}
								} else {
									return createGenericTable(iter, index, iter.allocHint(), temp, null);
								}
							}
						}
						return createGenericTable(iter, index, iter.allocHint(), null, null);
					}
				} finally {
					--fIndex;
					iter.tearDown();
				}

				return fDefaultValue;

			}
			return fFunction.evaluate(fCurrentIndex);
		}

		private IExpr tablePlus(IExpr temp, final IIterator<IExpr> iter, final int index) {
			INumber num;
			int counter = 0;
			num = (INumber) temp;
			while (iter.hasNext()) {
				fCurrentIndex[index] = iter.next();
				temp = table();
				if (temp == null) {
					temp = fDefaultValue;
				}
				if (temp.isNumber()) {
					num = (INumber) num.plus(temp);
				} else {
					return createGenericTable(iter, index, iter.allocHint() - counter, num, temp);
				}
				counter++;
			}
			return num;
		}

		private IExpr tableTimes(IExpr temp, final IIterator<IExpr> iter, final int index) {
			INumber num;
			int counter = 0;
			num = (INumber) temp;
			while (iter.hasNext()) {
				fCurrentIndex[index] = iter.next();
				temp = table();
				if (temp == null) {
					temp = fDefaultValue;
				}
				if (temp.isNumber()) {
					num = (INumber) num.times(temp);
				} else {
					return createGenericTable(iter, index, iter.allocHint() - counter, num, temp);
				}
				counter++;
			}
			return num;
		}

		/**
		 * 
		 * @param iter
		 *            the current Iterator index
		 * @param index
		 *            index
		 * @return
		 */
		private IExpr createGenericTable(final IIterator<IExpr> iter, final int index, final int allocationHint,
				IExpr arg1, IExpr arg2) {
			final IASTAppendable result = fPrototypeList
					.copyHead(fPrototypeList.size() + (allocationHint > 0 ? allocationHint : 0));
			result.appendArgs(fPrototypeList);
			if (arg1 != null) {
				result.append(arg1);
			}
			if (arg2 != null) {
				result.append(arg2);
			}
			while (iter.hasNext()) {
				fCurrentIndex[index] = iter.next();
				IExpr temp = table();
				if (temp == null) {
					result.append(fDefaultValue);
				} else {
					result.append(temp);
				}
			}
			return result;
		}
	}

	private static class PositionConverter implements IPositionConverter<IExpr> {
		@Override
		public IExpr toObject(final int i) {
			if (i < 3) {
				switch (i) {
				case 0:
					return F.C0;
				case 1:
					return F.C1;
				case 2:
					return F.C2;
				}
			}
			return F.integer(i);
		}

		@Override
		public int toInt(final IExpr position) {
			int val = position.toIntDefault(Integer.MIN_VALUE);
			if (val<0) {
				return -1;
			}
			return val;
		}
	}

	/**
	 * <pre>
	 * Accumulate(list)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * accumulate the values of <code>list</code> returning a new list.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Accumulate({1, 2, 3})
	 * {1,3,6}
	 * </pre>
	 */
	private final static class Accumulate extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			IExpr arg1 = ast.arg1();
			if (arg1.isAST()) {
				IAST list = (IAST) arg1;
				int size = list.size();
				IASTAppendable resultList = F.ast(list.head(), size, false);
				return foldLeft(null, list, 1, size, (x, y) -> F.binaryAST2(F.Plus, x, y), resultList);

			}
			return F.NIL;
		}

	}

	/**
	 * <pre>
	 * Append(expr, item)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>expr</code> with <code>item</code> appended to its leaves.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Append({1, 2, 3}, 4)    
	 * {1,2,3,4}
	 * </pre>
	 * <p>
	 * <code>Append</code> works on expressions with heads other than <code>List</code>:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Append(f(a, b), c)    
	 * f(a,b,c)
	 * </pre>
	 * <p>
	 * Unlike <code>Join</code>, <code>Append</code> does not flatten lists in <code>item</code>:<br />
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Append({a, b}, {c, d})    
	 * {a,b,{c,d}}
	 * </pre>
	 * <p>
	 * Nonatomic expression expected.<br />
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Append(a, b)     
	 * Append(a,b)
	 * </pre>
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
	 * <pre>
	 * AppendTo(s, item)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * append <code>item</code> to value of <code>s</code> and sets <code>s</code> to the result.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; s = {}    
	 * &gt;&gt; AppendTo(s, 1)    
	 * {1}    
	 * 
	 * &gt;&gt; s    
	 * {1}
	 * </pre>
	 * <p>
	 * 'Append' works on expressions with heads other than 'List':<br />
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; y = f()  
	 * &gt;&gt; AppendTo(y, x)    
	 * f(x)    
	 * 
	 * &gt;&gt; y    
	 * f(x)
	 * </pre>
	 * <p>
	 * {} is not a variable with a value, so its value cannot be changed.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; AppendTo({}, 1)     
	 * AppendTo({}, 1)
	 * </pre>
	 * <p>
	 * a is not a variable with a value, so its value cannot be changed.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; AppendTo(a, b)    
	 * AppendTo(a, b)
	 * </pre>
	 */
	private final static class AppendTo extends AbstractCoreFunctionEvaluator {

		private static class AppendToFunction implements Function<IExpr, IExpr> {
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
	 * <pre>
	 * Array(f, n)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the <code>n</code>-element list <code>{f(1), ..., f(n)}</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * Array(f, n, a)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the n-element list <code>{f(a), ..., f(a + n)}</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * Array(f, {n, m}, {a, b})
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns an <code>n</code>-by-<code>m</code> matrix created by applying <code>f</code> to indices ranging from
	 * <code>(a, b)</code> to <code>(a + n, b + m)</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * Array(f, dims, origins, h)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns an expression with the specified dimensions and index origins, with head <code>h</code> (instead of
	 * <code>List</code>).
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Array(f, 4)
	 * {f(1),f(2),f(3),f(4)}
	 * 
	 * &gt;&gt; Array(f, {2, 3})
	 * {{f(1,1),f(1,2),f(1,3)},{f(2,1),f(2,2),f(2,3)}} 
	 * 
	 * &gt;&gt; Array(f, {2, 3}, {4, 6})
	 * {{f(4,6),f(4,7),f(4,8)},{f(5,6),f(5,7),f(5,8)}}
	 * 
	 * &gt;&gt; Array(f, 4)
	 * {f(1), f(2), f(3), f(4)}
	 * 
	 * &gt;&gt; Array(f, {2, 3})
	 * {{f(1, 1), f(1, 2), f(1, 3)}, {f(2, 1), f(2, 2), f(2, 3)}}
	 * 
	 * &gt;&gt; Array(f, {2, 3}, 3)
	 * {{f(3, 3), f(3, 4), f(3, 5)}, {f(4, 3), f(4, 4), f(4, 5)}}
	 * 
	 * &gt;&gt; Array(f, {2, 3}, {4, 6})
	 * {{f(4,6),f(4,7),f(4,8)},{f(5,6),f(5,7),f(5,8)}}
	 * 
	 * &gt;&gt; Array(f, {2, 3}, 1, Plus)
	 * f(1,1)+f(1,2)+f(1,3)+f(2,1)+f(2,2)+f(2,3)
	 * </pre>
	 * <p>
	 * {2, 3} and {1, 2, 3} should have the same length.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Array(f, {2, 3}, {1, 2, 3})
	 * Array(f, {2, 3}, {1, 2, 3})
	 * </pre>
	 * <p>
	 * Single or list of non-negative integers expected at position 2.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Array(f, a)
	 * Array(f, a)
	 * </pre>
	 * <p>
	 * Single or list of non-negative integers expected at position 3.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Array(f, 2, b)
	 * Array(f, 2, b)
	 * </pre>
	 */
	private final static class Array extends AbstractCoreFunctionEvaluator {

		private static class MultipleArrayFunction implements IArrayFunction {
			final EvalEngine fEngine;

			final IAST fHeadAST;

			public MultipleArrayFunction(final EvalEngine engine, final IAST headAST) {
				fEngine = engine;
				fHeadAST = headAST;
			}

			@Override
			public IExpr evaluate(final IExpr[] index) {
				final IASTAppendable ast = fHeadAST.copyAppendable();
				return fEngine.evaluate(ast.appendArgs(0, index.length, i -> index[i]));
			}
		}

		private static class ArrayIterator implements IIterator<IExpr> {
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
	 * <pre>
	 * ArrayPad(list, n)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * adds <code>n</code> times <code>0</code> on the left and right of the <code>list</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * ArrayPad(list, {m,n})
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * adds <code>m</code> times <code>0</code> on the left and <code>n</code> times <code>0</code> on the right.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * ArrayPad(list, {m, n}, x)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * adds <code>m</code> times <code>x</code> on the left and <code>n</code> times <code>x</code> on the right.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; ArrayPad({a, b, c}, 1, x)
	 * {x,a,b,c,x}
	 * </pre>
	 */
	private final static class ArrayPad extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3, 4);

			if (ast.arg1().isAST()) {
				IAST arg1 = (IAST) ast.arg1();
				int m = -1;
				int n = -1;
				if (ast.arg2().isAST(F.List, 3)) {
					IAST list = (IAST) ast.arg2();
					m = list.arg1().toIntDefault(-1);
					n = list.arg2().toIntDefault(-1);
				} else {
					n = ast.arg2().toIntDefault(-1);
					m = n;
				}
				if (m > 0 && n > 0) {
					int[] dim = arg1.isMatrix();
					if (dim != null) {
						return arrayPadMatrixAtom(arg1, dim, m, n, ast.size() > 3 ? ast.arg3() : F.C0);
					}
					return arrayPadAtom(arg1, m, n, ast.size() > 3 ? ast.arg3() : F.C0);
				}
			}
			return F.NIL;
		}

		private static IExpr arrayPadMatrixAtom(IAST matrix, int[] dim, int m, int n, IExpr atom) {
			int columnDim = dim[1] + m + n;
			IASTAppendable result = matrix.copyHead(dim[0] + m + n);
			// prepend m rows
			result.appendArgs(0, m, i -> atom.constantArray(F.List, 0, columnDim));

			result.appendArgs(1, dim[0] + 1, i -> arrayPadAtom(matrix.getAST(i), m, n, atom));

			// append n rows
			result.appendArgs(0, n, i -> atom.constantArray(F.List, 0, columnDim));
			return result;
		}

		private static IExpr arrayPadAtom(IAST ast, int m, int n, IExpr atom) {
			IASTAppendable result = ast.copyHead();
			result.appendArgs(0, m, i -> atom);
			result.appendArgs(ast);
			result.appendArgs(0, n, i -> atom);
			return result;
		}

	}

	/**
	 * <pre>
	 * Cases(list, pattern)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the elements of <code>list</code> that match <code>pattern</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * Cases(list, pattern, ls)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the elements matching at levelspec <code>ls</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Cases({a, 1, 2.5, \"string\"}, _Integer|_Real)
	 * {1,2.5}
	 * 
	 * &gt;&gt; Cases(_Complex)[{1, 2I, 3, 4-I, 5}]
	 * {I*2,4-I}
	 * 
	 * &gt;&gt; Cases(1, 2)
	 * {}
	 * 
	 * &gt;&gt; Cases(f(1, 2), 2)
	 * {2}
	 * 
	 * &gt;&gt; Cases(f(f(1, 2), f(2)), 2)
	 * {}
	 * 
	 * &gt;&gt; Cases(f(f(1, 2), f(2)), 2, 2)
	 * {2,2}
	 * 
	 * &gt;&gt; Cases(f(f(1, 2), f(2), 2), 2, Infinity)
	 * {2,2,2}
	 * 
	 * &gt;&gt; Cases({1, f(2), f(3, 3, 3), 4, f(5, 5)}, f(x__) :&gt; Plus(x))
	 * {2,9,10}
	 * 
	 * &gt;&gt; Cases({1, f(2), f(3, 3, 3), 4, f(5, 5)}, f(x__) -&gt; Plus(x))
	 * {2, 3, 3, 3, 5, 5}
	 * </pre>
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
			protected final IPatternMatcher matcher;
			protected IASTAppendable resultCollection;
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
			public CasesPatternMatcherFunctor(final IPatternMatcher matcher, IASTAppendable resultCollection,
					int maximumResults) {
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
			protected IASTAppendable resultCollection;
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
			public CasesRulesFunctor(final Function<IExpr, IExpr> function, IASTAppendable resultCollection,
					int maximumResults) {
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
					IASTAppendable result = F.ListAlloc(8);
					if (arg2.isRuleAST()) {
						try {
							Function<IExpr, IExpr> function = Functors.rules((IAST) arg2, engine);
							CasesRulesFunctor crf = new CasesRulesFunctor(function, result, maximumResults);
							VisitorLevelSpecification level = new VisitorLevelSpecification(crf, arg3, false, engine);
							arg1.accept(level);

						} catch (StopException se) {
							// reached maximum number of results
						}
						return result;
					}

					try {
						final IPatternMatcher matcher = engine.evalPatternMatcher(arg2);
						CasesPatternMatcherFunctor cpmf = new CasesPatternMatcherFunctor(matcher, result,
								maximumResults);
						VisitorLevelSpecification level = new VisitorLevelSpecification(cpmf, arg3, false, engine);
						arg1.accept(level);
					} catch (StopException se) {
						// reached maximum number of results
					}
					return result;
				} else {
					return cases((IAST) arg1, arg2, engine);
				}
			}
			return F.List();
		}

		public static IAST cases(final IAST ast, final IExpr pattern, @Nonnull EvalEngine engine) {
			if (pattern.isRuleAST()) {
				Function<IExpr, IExpr> function = Functors.rules((IAST) pattern, engine);
				IAST[] results = ast.filter(function);
				return results[0];
			}
			final IPatternMatcher matcher = engine.evalPatternMatcher(pattern);
			return ast.filter(F.ListAlloc(), matcher);
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	/**
	 * <pre>
	 * Catenate({l1, l2, ...})
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * concatenates the lists <code>l1, l2, ...</code>
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Catenate({{1, 2, 3}, {4, 5}})
	 * {1, 2, 3, 4, 5}
	 * </pre>
	 */
	private final static class Catenate extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			if (ast.arg1().isList()) {
				IAST list = (IAST) ast.arg1();
				int[] size = { 1 };
				if (list.exists(x -> {
					if (!x.isList()) {
						return true;
					}
					size[0] += list.argSize();
					return false;
				})) {
					return F.NIL;
				}
				IASTAppendable resultList = F.ast(F.List, size[0], false);
				list.forEach(x -> resultList.appendArgs((IAST) x));
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

			IASTAppendable tallyResult = Tally.tally1Arg(list);
			EvalAttributes.sort(tallyResult, new Comparator<IExpr>() {
				@Override
				public int compare(IExpr o1, IExpr o2) {
					return o2.second().compareTo(o1.second());
				}
			});

			int size = tallyResult.size();
			if (size > 1) {
				if (n == -1) {
					IInteger max = (IInteger) ((IAST) tallyResult.arg1()).arg2();
					IASTAppendable result = F.ListAlloc(size);
					result.append(((IAST) tallyResult.arg1()).arg1());
					tallyResult.exists(x -> {
						if (max.equals(x.second())) {
							result.append(x.first());
							return false;
						}
						return true;
					}, 2);
					return result;
				} else {
					int counter = 0;
					IASTAppendable result = F.ListAlloc(size);
					for (int i = 1; i < size; i++) {
						if (counter < n) {
							result.append(((IAST) tallyResult.get(i)).arg1());
							counter++;
						} else {
							break;
						}
					}
					return result;
				}
			}
			return F.List();
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	/**
	 * <pre>
	 * Complement(set1, set2)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * get the complement set from <code>set1</code> and <code>set2</code>.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:<br />
	 * </p>
	 * <ul>
	 * <li><a href="https://en.wikipedia.org/wiki/Complement_(set_theory)">Wikipedia - Complement (set theory)</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Complement({1,2,3},{2,3,4})
	 * {1}
	 * 
	 * &gt;&gt; Complement({2,3,4},{1,2,3})
	 * {4}
	 * </pre>
	 */
	private final static class Complement extends AbstractFunctionEvaluator {

		public Complement() {
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3);

			if (ast.arg1().isAST() && ast.arg2().isAST()) {

				final IAST arg1 = (IAST) ast.arg1();
				final IAST arg2 = (IAST) ast.arg2();
				IAST result = complement(arg1, arg2);
				for (int i = 3; i < ast.size(); i++) {
					if (ast.get(i).isAST()) {
						result = complement(result, (IAST) ast.get(i));
					} else {
						return F.NIL;
					}
				}
				return result;
			}
			return F.NIL;
		}

		public static IAST complement(final IAST arg1, final IAST arg2) {

			Set<IExpr> set2 = arg2.asSet();
			Set<IExpr> set3 = new HashSet<IExpr>();
			arg1.forEach(x -> {
				if (!set2.contains(x)) {
					set3.add(x);
				}
			});
			IASTAppendable result = F.ListAlloc(set3.size());
			for (IExpr expr : set3) {
				result.append(expr);
			}
			EvalAttributes.sort(result);
			return result;
		}
	}

	/**
	 * <pre>
	 * Composition(sym1, sym2,...)[arg1, arg2,...]
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * creates a composition of the symbols applied at the arguments.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Composition(u, v, w)[x, y]
	 * u(v(w(x,y)))
	 * </pre>
	 */
	private final static class Composition extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.head().equals(F.Composition)) {
				return ast.remove(x -> x.equals(F.Identity));
			}
			if (ast.head().isAST()) {

				IAST headList = (IAST) ast.head();
				if (headList.size() > 1) {
					IASTAppendable inner = F.ast(headList.arg1());
					IAST result = inner;
					IASTAppendable temp;
					for (int i = 2; i < headList.size(); i++) {
						temp = F.ast(headList.get(i));
						inner.append(temp);
						inner = temp;
					}
					inner.appendArgs(ast);
					return result;
				}

			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.FLAT | ISymbol.ONEIDENTITY);
		}
	}

	/**
	 * <pre>
	 * ComposeList(list - of - symbols, variable)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * creates a list of compositions of the symbols applied at the argument <code>x</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; ComposeList({f,g,h}, x)
	 * {x,f(x),g(f(x)),h(g(f(x)))}
	 * </pre>
	 */
	private static class ComposeList extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			return evaluateComposeList(ast, F.ListAlloc(8));
		}

		public static IExpr evaluateComposeList(final IAST ast, final IASTAppendable resultList) {
			try {
				if ((ast.isAST2()) && (ast.arg1().isAST())) {
					// final EvalEngine engine = EvalEngine.get();
					final IAST list = (IAST) ast.arg1();
					final IAST constant = F.ast(ast.arg1());
					ListFunctions.foldLeft(ast.arg2(), list, 1, list.size(), (x, y) -> {
						final IASTAppendable a = constant.apply(y);
						a.append(x);
						return a;
					}, resultList);
					return resultList;
				}
			} catch (final ArithmeticException e) {

			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}
	}

	/**
	 * <pre>
	 * ConstantArray(expr, n)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns a list of <code>n</code> copies of <code>expr</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; ConstantArray(a, 3)
	 * {a, a, a}
	 * 
	 * &gt;&gt; ConstantArray(a, {2, 3})
	 * {{a, a, a}, {a, a, a}}
	 * </pre>
	 */
	private final static class ConstantArray extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			return evaluateArray(ast, List());
		}

		public static IExpr evaluateArray(final IAST ast, IAST resultList) {
			try {
				if ((ast.size() >= 3) && (ast.size() <= 5)) {
					int indx1, indx2;
					final List<ArrayIterator> iterList = new ArrayList<ArrayIterator>();
					final IExpr constantExpr = ast.arg1();
					if ((ast.isAST2()) && (ast.arg2().isInteger())) {
						indx1 = Validate.checkIntType(ast, 2);
						return constantExpr.constantArray(F.List, 0, indx1);
					} else if ((ast.isAST2()) && ast.arg2().isList()) {
						final IAST dimensions = (IAST) ast.arg2();
						int[] dim = new int[dimensions.size() - 1];
						for (int i = 1; i < dimensions.size(); i++) {
							indx1 = Validate.checkIntType(dimensions, i);
							dim[i - 1] = indx1;
						}
						return constantExpr.constantArray(F.List, 0, dim);
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
	 * <pre>
	 * Count(list, pattern)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the number of times <code>pattern</code> appears in <code>list</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * Count(list, pattern, ls)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * counts the elements matching at levelspec <code>ls</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Count({3, 7, 10, 7, 5, 3, 7, 10}, 3)
	 * 2
	 * 
	 * &gt;&gt; Count({{a, a}, {a, a, a}, a}, a, {2})
	 * 5
	 * </pre>
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
				this.matcher = patternMatcher;
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

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3, 4);

			final IExpr arg1 = engine.evaluate(ast.arg1());

			final VisitorLevelSpecification level;
			CountFunctor mf = new CountFunctor(engine.evalPatternMatcher(ast.arg2()));
			if (ast.isAST3()) {
				final IExpr arg3 = engine.evaluate(ast.arg3());
				level = new VisitorLevelSpecification(mf, arg3, false, engine);
			} else {
				level = new VisitorLevelSpecification(mf, 1);
			}
			arg1.accept(level);
			return F.integer(mf.getCounter());
		}

	}

	/**
	 * Delete(list,n) - delete the n-th argument from the list. Negative n counts from the end.
	 * 
	 */
	private static class Delete extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);
			final IExpr arg1 = engine.evaluate(ast.arg1());
			final IExpr arg2 = engine.evaluate(ast.arg2());
			if (arg1.isAST() && arg2.isInteger()) {
				final IAST list1 = (IAST) arg1;

				try {
					int indx = Validate.checkIntType(ast, 2, Integer.MIN_VALUE);
					if (indx < 0) {
						// negative n counts from the end
						indx = list1.size() + indx;
					}
					return list1.removeAtCopy(indx);
				} catch (final IndexOutOfBoundsException e) {
					if (Config.DEBUG) {
						e.printStackTrace();
					}
					return F.NIL;
				}

			}
			return F.NIL;
		}

	}

	/**
	 * <pre>
	 * DeleteCases(list, pattern)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the elements of <code>list</code> that do not match <code>pattern</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; DeleteCases({a, 1, 2.5, "string"}, _Integer|_Real)
	 * {a,"string"}
	 * 
	 * &gt;&gt; DeleteCases({a, b, 1, c, 2, 3}, _Symbol)
	 * {1,2,3}
	 * </pre>
	 */
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
					IASTAppendable arg1RemoveClone = ((IAST) arg1).copyAppendable();

					try {
						DeleteCasesPatternMatcherFunctor cpmf = new DeleteCasesPatternMatcherFunctor(matcher);
						VisitorRemoveLevelSpecification level = new VisitorRemoveLevelSpecification(cpmf, arg3,
								maximumRemoveOperations, false, engine);
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

				IExpr temp;
				boolean evaledTrue;
				BiPredicate<IExpr, IExpr> biPredicate = Predicates.isBinaryTrue(test);
				int size = list.size();
				final IASTAppendable result = F.ListAlloc(size);
				for (int i = 1; i < size; i++) {
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
	 * <pre>
	 * Drop(expr, n)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>expr</code> with the first <code>n</code> leaves removed.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Drop({a, b, c, d}, 3)
	 * {d}
	 * 
	 * &gt;&gt; Drop({a, b, c, d}, -2)
	 * {a,b}
	 * 
	 * &gt;&gt; Drop({a, b, c, d, e}, {2, -2})
	 * {a,e}
	 * </pre>
	 * <p>
	 * Drop a submatrix:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; A = Table(i*10 + j, {i, 4}, {j, 4})
	 * {{11,12,13,14},{21,22,23,24},{31,32,33,34},{41,42,43,44}}
	 * 
	 * &gt;&gt; Drop(A, {2, 3}, {2, 3})
	 * {{11,14},{41,44}}
	 * 
	 * &gt;&gt; Drop(Range(10), {-2, -6, -3})
	 * {1,2,3,4,5,7,8,10}
	 * 
	 * &gt;&gt; Drop(Range(10), {10, 1, -3})
	 * {2, 3, 5, 6, 8, 9}
	 * </pre>
	 * <p>
	 * Cannot drop positions -5 through -2 in {1, 2, 3, 4, 5, 6}.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Drop(Range(6), {-5, -2, -2}) 
	 * Drop({1, 2, 3, 4, 5, 6}, {-5, -2, -2})
	 * </pre>
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
						final IASTAppendable resultList = list.copyAppendable();
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
		private static IAST drop(final IASTAppendable list, final int level, final ISequence[] sequenceSpecifications) {
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
						final IASTAppendable tempList = ((IAST) list.get(j2)).copyAppendable();
						list.set(j2, drop(tempList, newLevel, sequenceSpecifications));
					} else {
						throw new IllegalArgument("Cannot execute drop for argument: " + list.get(j2).toString());
					}
				}
			}
			return list;
		}
	}

	/**
	 * <pre>
	 * Extract(expr, list)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * extracts parts of <code>expr</code> specified by <code>list</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * Extract(expr, {list1, list2, ...})'
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * extracts a list of parts.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * <p>
	 * <code>Extract(expr, i, j, ...)</code> is equivalent to <code>Part(expr, {i, j, ...})</code>.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Extract(a + b + c, {2})
	 * b
	 * 
	 * &gt;&gt; Extract({{a, b}, {c, d}}, {{1}, {2, 2}})
	 * {{a,b},d}
	 * </pre>
	 */
	private final static class Extract extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3, 4);

			if (ast.arg1().isAST() && ast.arg2().isList()) {
				IAST arg1 = (IAST) ast.arg1();
				IAST arg2 = (IAST) ast.arg2();
				if (arg2.isListOfLists()) {
					final int arg2Size = arg2.size();
					IASTAppendable result = F.ListAlloc(arg2Size);
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
			if ((position.size() > 1) && (position.arg1().isReal())) {
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
		 * @param headOffset
		 */
		private static IExpr extract(final IAST list, final IAST positions,
				final IPositionConverter<? super IExpr> positionConverter, int headOffset) {
			int p = 0;
			IAST temp = list;
			int posSize = positions.argSize();
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

	/**
	 * <pre>
	 * First(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the first element in <code>expr</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * <p>
	 * <code>First(expr)</code> is equivalent to <code>expr[[1]]</code>.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; First({a, b, c})
	 * a
	 * 
	 * &gt;&gt; First(a + b + c)
	 * a
	 * </pre>
	 * <p>
	 * Nonatomic expression expected.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; First(x)
	 * First(x)
	 * </pre>
	 */
	private final static class First extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() == 2 || ast.size() == 3) {
				IExpr arg1 = engine.evaluate(ast.arg1());
				if (arg1.isAST()) {
					final IAST sublist = (IAST) arg1;

					if (sublist.size() > 1) {
						return sublist.arg1();
					}
				}
				if (ast.size() == 3) {
					return engine.evaluate(ast.arg2());
				}
				engine.printMessage("First: Nonatomic expression expected");
			} else {
				engine.printMessage("First: unexpected number of arguments");
			}
			return F.NIL;
		}
	}

	private final static class Fold extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 4);
			return evaluateNestList(ast, engine);
		}

		private static IExpr evaluateNestList(final IAST ast, EvalEngine engine) {

			try {
				IExpr temp = engine.evaluate(ast.arg3());
				if (temp.isAST()) {
					final IAST list = (IAST) temp;
					IExpr arg1 = engine.evaluate(ast.arg1());
					IExpr arg2 = engine.evaluate(ast.arg2());
					return list.foldLeft((x, y) -> F.binaryAST2(arg1, x, y), arg2, 1);
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
			if (ast.size() == 3) {
				return evaluateNestList3(ast, engine);
			} else if (ast.size() == 4) {
				return evaluateNestList4(ast, engine);
			}
			return F.NIL;
		}

		private static IAST evaluateNestList3(final IAST ast, EvalEngine engine) {
			IExpr temp = engine.evaluate(ast.arg2());
			if (temp.isAST()) {
				IAST list = (IAST) temp;
				IExpr arg1 = engine.evaluate(ast.arg1());
				if (list.size() == 1 || list.size() == 2) {
					return list;
				}
				final IASTAppendable resultList = F.ast(list.head(), list.size(), false);
				IExpr arg2 = list.arg1();
				list = list.rest();
				return foldLeft(arg2, list, 1, list.size(), (x, y) -> F.binaryAST2(arg1, x, y), resultList);
			}
			return F.NIL;
		}

		private static IAST evaluateNestList4(final IAST ast, EvalEngine engine) {

			IExpr temp = engine.evaluate(ast.arg3());
			if (temp.isAST()) {
				final IAST list = (IAST) temp;
				IExpr arg1 = engine.evaluate(ast.arg1());
				IExpr arg2 = engine.evaluate(ast.arg2());
				if (list.size() == 1) {
					return F.unaryAST1(list.head(), arg2);
				}
				final IASTAppendable resultList = F.ast(list.head(), list.size(), false);
				return foldLeft(arg2, list, 1, list.size(), (x, y) -> F.binaryAST2(arg1, x, y), resultList);
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
				java.util.Map<IExpr, IASTAppendable> map;
				if (size > 2) {
					IExpr arg2 = ast.arg2();
					map = new TreeMap<IExpr, IASTAppendable>(new Comparators.BinaryHeadComparator(arg2));
				} else {
					map = new TreeMap<IExpr, IASTAppendable>();
				}
				for (int i = 1; i < arg1.size(); i++) {
					IASTAppendable list = map.get(arg1.get(i));
					if (list == null) {
						map.put(arg1.get(i), F.ListAlloc(arg1.get(i)));
					} else {
						list.append(arg1.get(i));
					}
				}

				IASTAppendable result = F.ListAlloc(map.size());
				for (Map.Entry<IExpr, IASTAppendable> entry : map.entrySet()) {
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
	 * <pre>
	 * Intersection(set1, set2, ...)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * get the intersection set from <code>set1</code> and <code>set2</code> &hellip;.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:<br />
	 * </p>
	 * <ul>
	 * <li><a href="http://en.wikipedia.org/wiki/Intersection_(set_theory)">Wikipedia - Intersection (set
	 * theory)</a></li>
	 * </ul>
	 */
	private final static class Intersection extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() > 1) {
				if (ast.isAST1()) {
					if (ast.arg1().isAST()) {
						IAST arg1 = (IAST) ast.arg1();
						Set<IExpr> set = arg1.asSet();
						final IASTAppendable result = F.ListAlloc(set.size());
						result.appendAll(set);
						EvalAttributes.sort(result, Comparators.ExprComparator.CONS);
						return result;
					}
					return F.NIL;
				}

				if (ast.arg1().isAST()) {
					IAST result = ((IAST) ast.arg1());
					for (int i = 2; i < ast.size(); i++) {
						if (!ast.get(i).isAST()) {
							return F.NIL;
						}
					}
					for (int i = 2; i < ast.size(); i++) {
						IAST expr = (IAST) ast.get(i);
						final IASTAppendable list = F.ListAlloc((result.size() + expr.size()) / 2);
						result = intersection(result, expr, list);
					}
					if (result.size() > 2) {
						EvalAttributes.sort((IASTMutable) result, Comparators.ExprComparator.CONS);
					}
					return result;
				}
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
		public static IAST intersection(IAST ast1, IAST ast2, final IASTAppendable result) {
			if (ast1.size() == 1 || ast2.size() == 1) {
				return F.CEmptyList;
			}
			Set<IExpr> set1 = new HashSet<IExpr>(ast1.size() + ast2.size() / 10);
			Set<IExpr> set2 = new HashSet<IExpr>(ast1.size() + ast2.size() / 10);
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
			result.appendAll(resultSet);
			return result;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.FLAT | ISymbol.ONEIDENTITY);
		}
	}

	private static class Insert extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 4);

			IExpr arg1 = engine.evaluate(ast.arg1());
			IAST arg1AST = Validate.checkASTType(arg1, engine);
			if (arg1AST == null) {
				return F.NIL;
			}
			IExpr arg2 = engine.evaluate(ast.arg2());
			IExpr arg3 = engine.evaluate(ast.arg3());
			if (arg3.isInteger()) {
				try {
					int i = Validate.checkIntType(arg3, Integer.MIN_VALUE);
					if (i < 0) {
						i = 1 + arg1AST.size() + i;
					}
					if (i > 0 && i < arg1AST.size()) {
						return arg1AST.appendAtClone(i, arg2);
					}
				} catch (final IndexOutOfBoundsException e) {
					if (Config.DEBUG) {
						e.printStackTrace();
					}
				}
			}
			return F.NIL;
		}

	}

	/**
	 * <pre>
	 * Join(l1, l2)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * concatenates the lists <code>l1</code> and <code>l2</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * <p>
	 * <code>Join</code> concatenates lists:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Join({a, b}, {c, d, e})
	 * {a,b,c,d,e}
	 * 
	 * &gt;&gt; Join({{a, b}, {c, d}}, {{1, 2}, {3, 4}})
	 * {{a,b},{c,d},{1,2},{3,4}}
	 * </pre>
	 * <p>
	 * The concatenated expressions may have any head:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Join(a + b, c + d, e + f)
	 * a+b+c+d+e+f
	 * </pre>
	 * <p>
	 * However, it must be the same for all expressions:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Join(a + b, c * d)
	 * Join(a+b,c*d)
	 * 
	 * &gt;&gt; Join(x, y)
	 * Join(x,y)
	 * 
	 * &gt;&gt; Join(x + y, z)
	 * Join(x+y,z)
	 * 
	 * &gt;&gt; Join(x + y, y * z, a)
	 * Join(x + y, y z, a)
	 * 
	 * &gt;&gt; Join(x, y + z, y * z)
	 * Join(x,y+z,y*z)
	 * </pre>
	 */
	private final static class Join extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3);

			if (ast.exists(x -> x.isAtom())) {
				return F.NIL;
			}

			int astSize = ast.size();
			int size = 0;
			IExpr head = null;
			IAST temp;
			for (int i = 1; i < astSize; i++) {
				temp = (IAST) ast.get(i);
				size += temp.argSize();
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
			final IASTAppendable result = F.ast(head, size, false);
			for (int i = 1; i < ast.size(); i++) {
				result.appendArgs((IAST) ast.get(i));
			}
			return result;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.FLAT | ISymbol.ONEIDENTITY);
		}
	}

	/**
	 * <pre>
	 * Last(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the last element in <code>expr</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * <p>
	 * <code>Last(expr)</code> is equivalent to <code>expr[[-1]]</code>.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Last({a, b, c})
	 * c
	 * </pre>
	 * <p>
	 * Nonatomic expression expected.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Last(x)
	 * Last(x)
	 * </pre>
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

	/**
	 * <pre>
	 * Length(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the number of leaves in <code>expr</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * <p>
	 * Length of a list:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Length({1, 2, 3})
	 * 3
	 * </pre>
	 * <p>
	 * 'Length' operates on the 'FullForm' of expressions:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Length(Exp(x))
	 * 2
	 * 
	 * &gt;&gt; FullForm(Exp(x))
	 * Power(E, x)
	 * </pre>
	 * <p>
	 * The length of atoms is 0:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Length(a)
	 * 0
	 * </pre>
	 * <p>
	 * Note that rational and complex numbers are atoms, although their 'FullForm' might suggest the opposite:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Length(1/3)
	 * 0
	 * 
	 * &gt;&gt; FullForm(1/3)
	 * Rational(1, 3)
	 * </pre>
	 */
	private final static class Length extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			IExpr arg1 = engine.evaluate(ast.arg1());
			if (arg1.isAST()) {
				return F.integer(((IAST) arg1).argSize());
			}
			return F.C0;
		}

	}

	/**
	 * <pre>
	 * Level(expr, levelspec)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * gives a list of all sub-expressions of <code>expr</code> at the level(s) specified by <code>levelspec</code>.
	 * </p>
	 * </blockquote>
	 * <p>
	 * Level uses standard level specifications:
	 * </p>
	 * 
	 * <pre>
	 * n
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * levels <code>1</code> through <code>n</code>
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * Infinity
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * all levels from level <code>1</code>
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * { n }
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * level <code>n</code> only
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * { m, n }
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * levels <code>m</code> through <code>n</code>
	 * </p>
	 * </blockquote>
	 * <p>
	 * Level 0 corresponds to the whole expression. A negative level <code>-n</code> consists of parts with depth
	 * <code>n</code>.
	 * </p>
	 * <h3>Examples</h3>
	 * <p>
	 * Level <code>-1</code> is the set of atoms in an expression:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Level(a + b ^ 3 * f(2 x ^ 2), {-1})
	 * {a,b,3,2,x,2}
	 * 
	 * &gt;&gt; Level({{{{a}}}}, 3)
	 * {{a},{{a}},{{{a}}}} 
	 * 
	 * &gt;&gt; Level({{{{a}}}}, -4)
	 * {{{{a}}}}
	 * 
	 * &gt;&gt; Level({{{{a}}}}, -5)
	 * {}
	 * 
	 * &gt;&gt; Level(h0(h1(h2(h3(a)))), {0, -1})
	 * {a,h3(a),h2(h3(a)),h1(h2(h3(a))),h0(h1(h2(h3(a))))}
	 * </pre>
	 * <p>
	 * Use the option <code>Heads -&gt; True</code> to include heads:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Level({{{{a}}}}, 3, Heads -&gt; True)
	 * {List,List,List,{a},{{a}},{{{a}}}} 
	 * 
	 * &gt;&gt; Level(x^2 + y^3, 3, Heads -&gt; True)
	 * {Plus,Power,x,2,x^2,Power,y,3,y^3} 
	 * 
	 * &gt;&gt; Level(a ^ 2 + 2 * b, {-1}, Heads -&gt; True)
	 * {Plus,Power,a,2,Times,2,b} 
	 * 
	 * &gt;&gt; Level(f(g(h))[x], {-1}, Heads -&gt; True)
	 * {f,g,h,x}
	 * 
	 * &gt;&gt; Level(f(g(h))[x], {-2, -1}, Heads -&gt; True)
	 * {f,g,h,g(h),x,f(g(h))[x]}
	 * </pre>
	 */
	private final static class Level extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3, 5);

			int lastIndex = ast.argSize();
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
				IASTAppendable resultList;
				if (lastIndex != 3) {
					resultList = F.ListAlloc(8);
				} else {
					resultList = F.ast(ast.get(lastIndex));
				}

				final VisitorLevelSpecification level = new VisitorLevelSpecification(x -> {
					resultList.append(x);
					return F.NIL;
				}, ast.arg2(), heads, engine);
				// Functors.collect(resultList.args()), ast.arg2(), heads);
				arg1.accept(level);

				return resultList;
			}
			return F.List();
		}

	}

	/**
	 * <pre>
	 * LevelQ(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * tests whether <code>expr</code> is a valid level specification.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; LevelQ(2)
	 * True
	 * 
	 * &gt;&gt; LevelQ({2, 4})
	 * True
	 * 
	 * &gt;&gt; LevelQ(Infinity)
	 * True
	 * 
	 * &gt;&gt; LevelQ(a + b)
	 * False
	 * </pre>
	 */
	private final static class LevelQ extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			IExpr arg1 = engine.evaluate(ast.arg1());
			try {
				// throws MathException if Level isn't defined correctly
				new VisitorLevelSpecification(null, arg1, false, engine);
				return F.True;
			} catch (MathException me) {
				// thrown in VisitorLevelSpecification ctor
			}
			return F.False;
		}

	}

	/**
	 * <pre>
	 * Most(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>expr</code> with the last element removed.
	 * </p>
	 * </blockquote>
	 * <p>
	 * <code>Most(expr)</code> is equivalent to <code>expr[[;;-2]]</code>.
	 * </p>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Most({a, b, c})
	 * {a,b}
	 * 
	 * &gt;&gt; Most(a + b + c)
	 * a+b
	 * </pre>
	 * <p>
	 * Nonatomic expression expected.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Most(x) 
	 * Most(x)
	 * </pre>
	 */
	private final static class Most extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);
			IExpr arg1 = engine.evaluate(ast.arg1());
			if (arg1.isAST() && ((IAST) arg1).size() > 1) {
				return ((IAST) arg1).removeAtCopy(((IAST) arg1).argSize());
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
				IASTAppendable nearest = null;
				IExpr distance = F.NIL;
				IASTAppendable temp;
				for (int i = 1; i < inputList.size(); i++) {
					temp = F.ast(distanceFunction);
					temp.append(x);
					temp.append(inputList.get(i));
					if (nearest == null) {
						nearest = F.ListAlloc(8);
						nearest.append(inputList.get(i));
						distance = temp;
					} else {
						IExpr comparisonResult = engine.evaluate(F.Greater(distance, temp));
						if (comparisonResult.isTrue()) {
							nearest = F.ListAlloc(8);
							nearest.append(inputList.get(i));
							distance = temp;
						} else if (comparisonResult.isFalse()) {
							if (F.Equal.ofQ(engine, distance, temp)) {
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

	/**
	 * <pre>
	 * PadLeft(list, n)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * pads <code>list</code> to length <code>n</code> by adding <code>0</code> on the left.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * PadLeft(list, n, x)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * pads <code>list</code> to length <code>n</code> by adding <code>x</code> on the left.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * PadLeft(list)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * turns the ragged list <code>list</code> into a regular list by adding '0' on the left.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; PadLeft({1, 2, 3}, 5)    
	 * {0,0,1,2,3}   
	 * 
	 * &gt;&gt; PadLeft(x(a, b, c), 5)    
	 * x(0,0,a,b,c)    
	 * 
	 * &gt;&gt; PadLeft({1, 2, 3}, 2)    
	 * {2, 3}    
	 * 
	 * &gt;&gt; PadLeft({{}, {1, 2}, {1, 2, 3}})    
	 * {{0,0,0},{0,1,2},{1,2,3}}
	 * </pre>
	 */
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
						IASTAppendable result = F.ListAlloc(list.size());
						final int mSize = maxSize - 1;
						return result.appendArgs(list.size(), i -> padLeftAtom(list.getAST(i), mSize, F.C0));
						// for (int i = 1; i < list.size(); i++) {
						// result.append(padLeftAtom(list.getAST(i), maxSize - 1, F.C0));
						// }
						// return result;
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
				IASTAppendable result = ast.copyHead();
				result.appendArgs(0, length, i -> atom);
				// for (int i = 0; i < length; i++) {
				// result.append(atom);
				// }
				result.appendArgs(ast);
				return result;
			}
			if (n > 0 && n < ast.size()) {
				return ast.removeFromStart(ast.size() - n);
			}
			return ast;
		}

		public static IAST padLeftAST(IAST ast, int n, IAST arg2) {
			int length = n - ast.size() + 1;
			if (length > 0) {
				IASTAppendable result = ast.copyHead();
				if (arg2.size() < 2) {
					return ast;
				}
				int j = 1;
				if ((arg2.argSize()) < n) {
					int temp = n % (arg2.argSize());
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

	/**
	 * <pre>
	 * PadRight(list, n)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * pads <code>list</code> to length <code>n</code> by adding <code>0</code> on the right.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * PadRight(list, n, x)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * pads <code>list</code> to length <code>n</code> by adding <code>x</code> on the right.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * PadRight(list)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * turns the ragged list <code>list</code> into a regular list by adding '0' on the right.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; PadRight({1, 2, 3}, 5)    
	 * {1,2,3,0,0}    
	 * 
	 * &gt;&gt; PadRight(x(a, b, c), 5)    
	 * x(a,b,c,0,0)  
	 * 
	 * &gt;&gt; PadRight({1, 2, 3}, 2)    
	 * {1,2}   
	 * 
	 * &gt;&gt; PadRight({{}, {1, 2}, {1, 2, 3}})    
	 * {{0,0,0},{1,2,0},{1,2,3}}
	 * </pre>
	 */
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
						IASTAppendable result = F.ListAlloc(list.size());
						final int mSize = maxSize;
						return result.appendArgs(list.size(), i -> padRightAtom(list.getAST(i), mSize - 1, F.C0));
						// for (int i = 1; i < list.size(); i++) {
						// result.append(padRightAtom(list.getAST(i), maxSize - 1, F.C0));
						// }
						// return result;
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
				IASTAppendable result = ast.copyHead();
				result.appendArgs(ast);
				return result.appendArgs(0, length, i -> atom);
			}
			if (n > 0 && n < ast.size()) {
				return ast.removeFromEnd(n + 1);
			}
			return ast;
		}

		public static IAST padRightAST(IAST ast, int n, IAST arg2) {
			int length = n - ast.size() + 1;
			if (length > 0) {
				IASTAppendable result = ast.copyHead();
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
	 * <pre>
	 * Position(expr, patt)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the list of positions for which <code>expr</code> matches <code>patt</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * Position(expr, patt, ls) 
	 * &gt; returns the positions on levels specified by levelspec `ls`.
	 * 
	 * ### Examples
	 * </pre>
	 * 
	 * <blockquote><blockquote>
	 * <p>
	 * Position({1, 2, 2, 1, 2, 3, 2}, 2) {{2},{3},{5},{7}}
	 * </p>
	 * </blockquote> </blockquote>
	 * <p>
	 * Find positions upto 3 levels deep
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Position({1 + Sin(x), x, (Tan(x) - y)^2}, x, 3)
	 * {{1,2,1},{2}}
	 * </pre>
	 * <p>
	 * Find all powers of x
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Position({1 + x^2, x y ^ 2,  4 y,  x ^ z}, x^_)
	 * {{1,2},{4}}
	 * </pre>
	 * <p>
	 * Use Position as an operator
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Position(_Integer)({1.5, 2, 2.5})
	 * {{2}}
	 * </pre>
	 */
	private final static class Position extends AbstractCoreFunctionEvaluator {

		/**
		 * Add the positions to the <code>resultCollection</code> where the matching expressions appear in
		 * <code>list</code>. The <code>positionConverter</code> converts the <code>int</code> position into an object
		 * for the <code>resultCollection</code>.
		 * 
		 * @param ast
		 * @param prototypeList
		 * @param resultCollection
		 * @param maxResults
		 *            the maximum number of results which should be returned in <code>resultCollection</code>
		 * @param level
		 * @param matcher
		 * @param positionConverter
		 * @param headOffset
		 * @return
		 */
		private static IAST position(final IAST ast, final IAST prototypeList, final IASTAppendable resultCollection,
				int maxResults, final LevelSpec level, final Predicate<? super IExpr> matcher,
				final IPositionConverter<? extends IExpr> positionConverter, int headOffset) {
			int minDepth = 0;
			level.incCurrentLevel();
			IASTAppendable clone = null;
			final int size = ast.size();
			for (int i = headOffset; i < size; i++) {
				if (ast.get(i).isAST()) {
					// clone = (INestedList<IExpr>) prototypeList.clone();
					clone = prototypeList.copyAppendable();
					clone.append(positionConverter.toObject(i));
					position((IAST) ast.get(i), clone, resultCollection, Integer.MAX_VALUE, level, matcher,
							positionConverter, headOffset);
					if (level.getCurrentDepth() < minDepth) {
						minDepth = level.getCurrentDepth();
					}
				}
				if (matcher.test(ast.get(i))) {
					if (level.isInRange()) {
						clone = prototypeList.copyAppendable();
						IExpr IExpr = positionConverter.toObject(i);
						clone.append(IExpr);
						if (maxResults >= resultCollection.size()) {
							resultCollection.append(clone);
						} else {
							break;
						}
					}
				}
			}
			level.setCurrentDepth(--minDepth);
			level.decCurrentLevel();
			return resultCollection;
		}

		/**
		 * 
		 * @param ast
		 * @param pattern
		 * @param level
		 * @param maxResults
		 *            the maximum number of results which should be returned in the resulting <code>List</code>
		 * @param engine
		 * @return a <code>F.List()</code> of result positions
		 */
		private static IAST position(final IAST ast, final IExpr pattern, final LevelSpec level, int maxResults,
				EvalEngine engine) {
			final IPatternMatcher matcher = engine.evalPatternMatcher(pattern);
			final PositionConverter positionConverter = new PositionConverter();

			final IAST cloneList = List();
			final IASTAppendable resultList = F.ListAlloc(8);
			int headOffset = 1;
			if (level.isIncludeHeads()) {
				headOffset = 0;
			}
			position(ast, cloneList, resultList, maxResults, level, matcher, positionConverter, headOffset);
			return resultList;
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST1()) {
				return F.operatorFormAST1(ast);
			}
			if (ast.size() < 3) {
				return F.NIL;
			}

			int maxResults = Integer.MAX_VALUE;
			if (ast.size() >= 5) {
				maxResults = ast.arg4().toIntDefault(Integer.MIN_VALUE);
				if (maxResults < 0) {
					engine.printMessage("Position: non-negative integer for maximum number of objects expected.");
					return F.NIL;
				}
			}
			final IExpr arg1 = engine.evaluate(ast.arg1());
			if (arg1.isAST()) {
				final IExpr arg2 = engine.evalPattern(ast.arg2());
				if (ast.isAST2()) {
					final LevelSpec level = new LevelSpec(0, Integer.MAX_VALUE);
					return position((IAST) arg1, arg2, level, Integer.MAX_VALUE, engine);
				}
				if (ast.size() >= 4) {
					final Options options = new Options(ast.topHead(), ast, 2, engine);
					IExpr option = options.getOption("Heads");
					if (option.isPresent()) {
						if (option.isTrue()) {
							final LevelSpec level = new LevelSpec(0, Integer.MAX_VALUE, true);
							return position((IAST) arg1, arg2, level, Integer.MAX_VALUE, engine);
						}
						if (option.isFalse()) {
							final LevelSpec level = new LevelSpec(0, Integer.MAX_VALUE, false);
							return position((IAST) arg1, arg2, level, maxResults, engine);
						}
						return F.NIL;
					}
					final IExpr arg3 = engine.evaluate(ast.arg3());
					final LevelSpec level = new LevelSpecification(arg3, true);
					return position((IAST) arg1, arg2, level, maxResults, engine);
				}
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.NHOLDALL);
		}
	}

	/**
	 * <pre>
	 * Prepend(expr, item)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>expr</code> with <code>item</code> prepended to its leaves.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * <p>
	 * <code>Prepend</code> is similar to <code>Append</code>, but adds <code>item</code> to the beginning of
	 * <code>expr</code>:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Prepend({2, 3, 4}, 1)    
	 * {1,2,3,4}
	 * </pre>
	 * <p>
	 * <code>Prepend</code> works on expressions with heads other than 'List':<br />
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Prepend(f(b, c), a)    
	 * f(a,b,c)
	 * </pre>
	 * <p>
	 * Unlike <code>Join</code>, <code>Prepend</code> does not flatten lists in <code>item</code>:<br />
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Prepend({c, d}, {a, b})  
	 * {{a,b},c,d}
	 * </pre>
	 * <p>
	 * Nonatomic expression expected.<br />
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Prepend(a, b)       
	 * Prepend(a,b)
	 * </pre>
	 */
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

	/**
	 * <pre>
	 * PrependTo(s, item)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * prepend <code>item</code> to value of <code>s</code> and sets <code>s</code> to the result.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * Assign s to a list    
	 * &gt;&gt; s = {1, 2, 4, 9}    
	 * {1,2,4,9}
	 * </pre>
	 * <p>
	 * Add a new value at the beginning of the list:<br />
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; PrependTo(s, 0)    
	 * {0,1,2,4,9}
	 * </pre>
	 * <p>
	 * The value assigned to s has changed:<br />
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; s    
	 * {0,1,2,4,9}
	 * </pre>
	 * <p>
	 * 'PrependTo' works with a head other than 'List':
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; y = f(a, b, c)    
	 * &gt;&gt; PrependTo(y, x)    
	 * f(x,a,b,c)  
	 * 
	 * &gt;&gt; y    
	 * f(x,a,b,c)
	 * </pre>
	 * <p>
	 * {a, b} is not a variable with a value, so its value cannot be changed.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; PrependTo({a, b}, 1)    
	 * PrependTo({a,b},1)
	 * </pre>
	 * <p>
	 * a is not a variable with a value, so its value cannot be changed.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; PrependTo(a, b)     
	 * PrependTo(a,b)
	 * </pre>
	 * 
	 * <pre>
	 * ```
	 * &gt;&gt; x = 1 + 2    
	 * 3
	 * </pre>
	 * <p>
	 * Nonatomic expression expected at position 1 in PrependTo
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; PrependTo(x, {3, 4})      
	 * PrependTo(x,{3,4})
	 * </pre>
	 */
	private final static class PrependTo extends AbstractCoreFunctionEvaluator {

		private static class PrependToFunction implements Function<IExpr, IExpr> {
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

	/**
	 * <pre>
	 * Range(n)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns a list of integers from <code>1</code> to <code>n</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * Range(a, b)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns a list of integers from <code>a</code> to <code>b</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Range(5)
	 * {1,2,3,4,5}
	 * 
	 * &gt;&gt; Range(-3, 2)
	 * {-3,-2,-1,0,1,2} 
	 * 
	 * &gt;&gt; Range(0, 2, 1/3)
	 * {0,1/3,2/3,1,4/3,5/3,2}
	 * </pre>
	 */
	private final static class Range extends AbstractEvaluator {
		private static class UnaryRangeFunction implements IArrayFunction {

			public UnaryRangeFunction() {
			}

			@Override
			public IExpr evaluate(final IExpr[] index) {
				return index[0];
			}
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST1() && ast.arg1().isReal()) {
				int size = ast.arg1().toIntDefault(Integer.MIN_VALUE);
				if (size != Integer.MIN_VALUE) {
					return range(size);
				}
				engine.printMessage("Range: argument " + ast.arg1()
						+ " is greater than Javas Integer.MAX_VALUE or no integer number.");
				return F.NIL;
			}
			return evaluateTable(ast, List(), engine);
		}

		/**
		 * 
		 * @param size
		 * @return <code>F.NIL</code> if <code>size > Integer.MAX_VALUE-3</code>
		 */
		public static IAST range(int size) {
			if (size > Integer.MAX_VALUE - 3) {
				EvalEngine.get().printMessage("Range: argument " + size + " is greater than Javas Integer.MAX_VALUE-3");
				return F.NIL;
			}
			return range(1, size + 1);
		}

		/**
		 * Range.of(2, 7) gives {2, 3, 4, 5, 6}
		 * 
		 * @param startInclusive
		 * @param endExclusive
		 * @return
		 */
		public static IAST range(int startInclusive, int endExclusive) {
			int size = endExclusive - startInclusive;
			if (size >= 0) {
				IASTAppendable result = F.ListAlloc(size + 1);
				return result.appendArgs(startInclusive, endExclusive, i -> F.integer(i));
			}
			return F.List();
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

	/**
	 * <pre>
	 * ReplaceAll(expr, i -&gt; new)
	 * </pre>
	 * <p>
	 * or
	 * </p>
	 * 
	 * <pre>
	 * expr /. i -&gt; new
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * replaces all <code>i</code> in <code>expr</code> with <code>new</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * ReplaceAll(expr, {i1 -&gt; new1, i2 -&gt; new2, ... } )
	 * </pre>
	 * <p>
	 * or
	 * </p>
	 * 
	 * <pre>
	 * expr /. {i1 -&gt; new1, i2 -&gt; new2, ... }
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * replaces all <code>i</code>s in <code>expr</code> with <code>new</code>s.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * &gt;&gt; f(a) + f(b) /. f(x_) -&gt; x^2
	 * a^2+b^2
	 * </pre>
	 */
	private static class ReplaceAll extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST1()) {
				return F.operatorFormAST1(ast);
			}
			if (ast.size() == 3) {
				try {
					IExpr arg1 = ast.arg1();
					IExpr arg2 = engine.evaluate(ast.arg2());

					if (arg2.isListOfRules()) {
						return arg1.replaceAll((IAST) arg2).orElse(arg1);
					} else if (arg2.isListOfLists()) {
						IAST list = (IAST) arg2;
						IASTAppendable result = F.ListAlloc(list.size());
						for (IExpr subList : list) {
							if (subList.isListOfRules()) {
								result.append(F.subst(arg1, (IAST) subList));
							} else {
								WrongArgumentType wat = new WrongArgumentType(ast, ast, -1,
										"List of rule expressions (x->y) expected: ");
								engine.printMessage(wat.getMessage());
							}
						}
						return result;
					} else if (arg2.isRuleAST()) {
						return F.subst(arg1, (IAST) arg2);
					} else {
						WrongArgumentType wat = new WrongArgumentType(ast, ast, -1,
								"Rule expression (x->y) expected: ");
						engine.printMessage(wat.getMessage());
					}
				} catch (WrongArgumentType wat) {
					if (Config.SHOW_STACKTRACE) {
						wat.printStackTrace();
					}
					engine.printMessage(wat.getMessage());
				}
				return F.NIL;
			}
			Validate.checkSize(ast, 3);
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDREST);
		}
	}

	/**
	 * <pre>
	 * ReplacePart(expr, i -&gt; new)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * replaces part <code>i</code> in <code>expr</code> with <code>new</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * ReplacePart(expr, {{i, j} -&gt; e1, {k, l} -&gt; e2})'
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * replaces parts <code>i</code> and <code>j</code> with <code>e1</code>, and parts <code>k</code> and
	 * <code>l</code> with <code>e2</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; ReplacePart({a, b, c}, 1 -&gt; t)
	 * {t,b,c}
	 * 
	 * &gt;&gt; ReplacePart({{a, b}, {c, d}}, {2, 1} -&gt; t)
	 * {{a,b},{t,d}}
	 * 
	 * &gt;&gt; ReplacePart({{a, b}, {c, d}}, {{2, 1} -&gt; t, {1, 1} -&gt; t})
	 * {{t,b},{t,d}}
	 * 
	 * &gt;&gt; ReplacePart({a, b, c}, {{1}, {2}} -&gt; t)
	 * {t,t,c}
	 * </pre>
	 * <p>
	 * Delayed rules are evaluated once for each replacement:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; n = 1
	 * &gt;&gt; ReplacePart({a, b, c, d}, {{1}, {3}} :&gt; n++)
	 * {1,b,2,d}
	 * </pre>
	 * <p>
	 * Non-existing parts are simply ignored:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; ReplacePart({a, b, c}, 4 -&gt; t)
	 * {a,b,c}
	 * </pre>
	 * <p>
	 * You can replace heads by replacing part <code>0</code>:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; ReplacePart({a, b, c}, 0 -&gt; Times)
	 * a*b*c
	 * </pre>
	 * <p>
	 * Negative part numbers count from the end:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; ReplacePart({a, b, c}, -1 -&gt; t)
	 * {a,b,t}
	 * </pre>
	 */
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
	 * <pre>
	 * Rest(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>expr</code> with the first element removed.
	 * </p>
	 * </blockquote>
	 * <p>
	 * <code>Rest(expr)</code> is equivalent to <code>expr[[2;;]]</code>.
	 * </p>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Rest({a, b, c})
	 * {b,c}
	 * 
	 * &gt;&gt; Rest(a + b + c)
	 * b+c
	 * </pre>
	 * <p>
	 * Nonatomic expression expected.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Rest(x)
	 * Rest(x)
	 * </pre>
	 */
	private final static class Rest extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() == 2) {
				IExpr arg1 = engine.evaluate(ast.arg1());
				if (arg1.isAST() && ((IAST) arg1).size() > 1) {
					return arg1.rest();
				}
				engine.printMessage("Rest: Nonatomic expression expected");
			}
			return F.NIL;
		}

	}

	/**
	 * <pre>
	 * Reverse(list)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * reverse the elements of the <code>list</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Reverse({1, 2, 3})
	 * {3,2,1}
	 * 
	 * &gt;&gt; Reverse(x(a,b,c))
	 * x(c,b,a)
	 * </pre>
	 */
	private final static class Reverse extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST functionList, EvalEngine engine) {
			if (functionList.size() != 2) {
				return F.NIL;
			}
			if (functionList.arg1().isAST()) {
				IAST list = (IAST) functionList.arg1();
				return reverse(list);
			}
			return F.NIL;
		}

	}

	/**
	 * <pre>
	 * Riffle(list1, list2)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * insert elements of <code>list2</code> between the elements of <code>list1</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Riffle({a, b, c}, x)
	 * {a,x,b,x,c}
	 * 
	 * &gt;&gt; Riffle({a, b, c}, {x, y, z})
	 * {a,x,b,y,c,z}
	 * </pre>
	 */
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

		public static IExpr riffleAtom(IAST arg1, final IExpr arg2) {
			if (arg1.size() < 2) {
				return arg1;
			}
			IASTAppendable result = arg1.copyHead();
			for (int i = 1; i < arg1.argSize(); i++) {
				result.append(arg1.get(i));
				result.append(arg2);
			}
			result.append(arg1.last());
			return result;
		}

		public static IAST riffleAST(IAST arg1, IAST arg2) {
			if (arg1.size() < 2) {
				return arg1;
			}
			IASTAppendable result = arg1.copyHead();
			if (arg2.size() < 2) {
				return arg1;
			}
			int j = 1;
			for (int i = 1; i < arg1.argSize(); i++) {
				result.append(arg1.get(i));
				if (j < arg2.size()) {
					result.append(arg2.get(j++));
				} else {
					j = 1;
					result.append(arg2.get(j++));
				}
			}
			result.append(arg1.last());
			if (j < arg2.size()) {
				result.append(arg2.get(j++));
			}
			return result;
		}
	}

	/**
	 * <pre>
	 * RotateLeft(list)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * rotates the items of <code>list</code> by one item to the left.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * RotateLeft(list, n)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * rotates the items of <code>list</code> by <code>n</code> items to the left.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; RotateLeft({1, 2, 3})
	 * {2,3,1}
	 * 
	 * &gt;&gt; RotateLeft(Range(10), 3)
	 * {4,5,6,7,8,9,10,1,2,3}
	 * 
	 * &gt;&gt; RotateLeft(x(a, b, c), 2)
	 * x(c,a,b)
	 * </pre>
	 */
	private final static class RotateLeft extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			IExpr arg1 = engine.evaluate(ast.arg1());
			if (arg1.isAST()) {
				final IASTAppendable result = F.ast(arg1.head());
				if (ast.isAST1()) {
					// ASTRange range = ((IAST) arg1).args();
					((IAST) arg1).rotateLeft(result, 1);
					// Rotating.rotateLeft((IAST) list.arg1(), result, 2, 1);
					return result;
				} else {
					IExpr arg2 = engine.evaluate(ast.arg2());
					if (arg2.isInteger()) {
						int n = Validate.checkIntType(arg2);

						// ASTRange range = ((IAST) arg1).args();
						((IAST) arg1).rotateLeft(result, n);
						return result;
					}
				}

			}
			return F.NIL;
		}

	}

	/**
	 * <pre>
	 * RotateRight(list)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * rotates the items of <code>list</code> by one item to the right.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * RotateRight(list, n)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * rotates the items of <code>list</code> by <code>n</code> items to the right.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; RotateRight({1, 2, 3})
	 * {3,1,2}
	 * 
	 * &gt;&gt; RotateRight(Range(10), 3)
	 * {8,9,10,1,2,3,4,5,6,7}
	 * 
	 * &gt;&gt; RotateRight(x(a, b, c), 2)
	 * x(b,c,a)
	 * </pre>
	 */
	private final static class RotateRight extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			IExpr arg1 = engine.evaluate(ast.arg1());
			if (arg1.isAST()) {
				final IASTAppendable result = F.ast(arg1.head());
				if (ast.isAST1()) {
					// ASTRange range = ((IAST) arg1).args();
					((IAST) arg1).rotateRight(result, 1);
					// Rotating.rotateRight((IAST) list.arg1(), result, 1, 1);
					return result;
				} else {
					IExpr arg2 = engine.evaluate(ast.arg2());
					if (arg2.isInteger()) {
						int n = Validate.checkIntType(arg2);
						// ASTRange range = ((IAST) arg1).args();
						((IAST) arg1).rotateRight(result, n);
						return result;
					}
				}

			}
			return F.NIL;
		}

	}

	/**
	 * <pre>
	 * Select({e1, e2, ...}, f)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns a list of the elements <code>ei</code> for which <code>f(ei)</code> returns <code>True</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * <p>
	 * Find numbers greater than zero:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Select({-3, 0, 1, 3, a}, #&gt;0&amp;)
	 * {1,3}
	 * </pre>
	 * <p>
	 * <code>Select</code> works on an expression with any head:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Select(f(a, 2, 3), NumberQ)
	 * f(2,3)
	 * </pre>
	 * <p>
	 * Nonatomic expression expected.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Select(a, True) 
	 * Select(a,True)
	 * </pre>
	 */
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
					return list.filter(list.copyHead(allocSize), x -> engine.evalTrue(F.unaryAST1(predicateHead, x)));
				} else if ((size == 4) && ast.arg3().isInteger()) {
					final int resultLimit = Validate.checkIntType(ast, 3);
					return list.filter(list.copyHead(allocSize), x -> engine.evalTrue(F.unaryAST1(predicateHead, x)),
							resultLimit);
				}
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	/**
	 * <pre>
	 * Split(list)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * splits <code>list</code> into collections of consecutive identical elements.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * Split(list, test)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * splits <code>list</code> based on whether the function <code>test</code> yields 'True' on consecutive elements.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Split({x, x, x, y, x, y, y, z})
	 * {{x,x,x},{y},{x},{y,y},{z}} 
	 * 
	 * &gt;&gt; Split({x, x, x, y, x, y, y, z}, x)
	 * {{x},{x},{x},{y},{x},{y},{y},{z}}
	 * </pre>
	 * <p>
	 * Split into increasing or decreasing runs of elements
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Split({1, 5, 6, 3, 6, 1, 6, 3, 4, 5, 4}, Less)
	 * {{1,5,6},{3,6},{1,6},{3,4,5},{4}} 
	 * 
	 * &gt;&gt; Split({1, 5, 6, 3, 6, 1, 6, 3, 4, 5, 4}, Greater)
	 * {{1},{5},{6,3},{6,1},{6,3},{4},{5,4}}
	 * </pre>
	 * <p>
	 * Split based on first element
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Split({x -&gt; a, x -&gt; y, 2 -&gt; a, z -&gt; c, z -&gt; a}, First(#1) === First(#2) &amp;)
	 * {{x-&gt;a,x-&gt;y},{2-&gt;a},{z-&gt;c,z-&gt;a}} 
	 * 
	 * &gt;&gt; Split({})
	 * {}
	 * </pre>
	 */
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

				IASTAppendable result = F.ListAlloc(8);
				if (list.size() > 1) {
					IExpr current = list.arg1();
					IASTAppendable temp = F.ListAlloc(8);
					result.append(temp);
					temp.append(current);
					for (int i = 2; i < list.size(); i++) {
						if (pred.test(current, list.get(i))) {
						} else {
							temp = F.ListAlloc(8);
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

	/**
	 * <pre>
	 * SplitBy(list, f)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * splits <code>list</code> into collections of consecutive elements that give the same result when <code>f</code>
	 * is applied.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; SplitBy(Range(1, 3, 1/3), Round) 
	 * {{1,4/3},{5/3,2,7/3},{8/3,3}}
	 * {{1, 4 / 3}, {5 / 3, 2, 7 / 3}, {8 / 3, 3}}
	 * 
	 * &gt;&gt; SplitBy({1, 2, 1, 1.2}, {Round, Identity})
	 * {{{1}},{{2}},{{1},{1.2}}} 
	 * 
	 * &gt;&gt; SplitBy(Tuples({1, 2}, 3), First)
	 * {{{1,1,1},{1,1,2},{1,2,1},{1,2,2}},{{2,1,1},{2,1,2},{2,2,1},{2,2,2}}}
	 * </pre>
	 */
	private final static class SplitBy extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			if (ast.arg1().isAST()) {
				return splitByFunction(ast.arg2().orNewList(), 1, (IAST) ast.arg1(), engine);
			}
			return F.NIL;
		}

		private IExpr splitByFunction(IAST functorList, int pos, IAST list, EvalEngine engine) {
			if (pos >= functorList.size()) {
				return F.NIL;
			}
			IExpr functorHead = functorList.get(pos);
			final Function<IExpr, IExpr> function = x -> engine.evaluate(F.unaryAST1(functorHead, x));

			IASTAppendable result = F.ListAlloc(8);
			if (list.size() > 1) {
				IExpr last = function.apply(list.arg1());
				IExpr current;
				IASTAppendable temp = F.ListAlloc(8);

				temp.append(list.arg1());
				for (int i = 2; i < list.size(); i++) {
					current = function.apply(list.get(i));
					if (current.equals(last)) {
					} else {
						IExpr subList = splitByFunction(functorList, pos + 1, temp, engine);
						if (subList.isPresent()) {
							result.append(subList);
						} else {
							result.append(temp);
						}
						temp = F.ListAlloc(8);
					}
					temp.append(list.get(i));
					last = current;
				}
				IExpr subList = splitByFunction(functorList, pos + 1, temp, engine);
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

	private final static class Subdivide extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if ((ast.size() > 1) && (ast.size() <= 4)) {
				if (ast.size() == 2) {
					int n = ast.arg1().toIntDefault(-1);
					if (n < 0 || n == 0) {
						engine.printMessage("Subdivide: argument 1 should be a positive integer.");
						return F.NIL;
					}
					return Range.range(0, n + 1).map(x -> x.divide(ast.arg1()), 1);
				}
				if (ast.size() == 3) {
					int n = ast.arg2().toIntDefault(-1);
					if (n < 0 || n == 0) {
						engine.printMessage("Subdivide: argument 2 should be a positive integer.");
						return F.NIL;
					}
					IAST factorList = Range.range(0, n + 1).map(x -> x.divide(ast.arg2()), 1);
					return ((IAST) factorList).map(x -> ast.arg1().times(x), 1);
				}
				if (ast.size() == 4) {
					if (ast.arg1().isList() && ast.arg2().isList()) {
						if (ast.arg1().size() != ast.arg2().size()) {
							return F.NIL;
						}
					}
					int n = ast.arg3().toIntDefault(-1);
					if (n < 0 || n == 0) {
						engine.printMessage("Subdivide: argument 3 should be a positive integer.");
						return F.NIL;
					}
					IAST factorList = Range.range(0, n + 1).map(x -> x.divide(ast.arg3()), 1);
					return ((IAST) factorList)
							.map(x -> ast.arg1().plus(ast.arg2().times(x).subtract(ast.arg1().times(x))), 1);
				}
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			// newSymbol.setAttributes(ISymbol.LISTABLE);
		}
	}

	/**
	 * <pre>
	 * Table(expr, {i, n})
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * evaluates <code>expr</code> with <code>i</code> ranging from <code>1</code> to <code>n</code>, returning a list
	 * of the results.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * Table(expr, {i, start, stop, step})
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * evaluates <code>expr</code> with <code>i</code> ranging from <code>start</code> to <code>stop</code>,
	 * incrementing by <code>step</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * Table(expr, {i, {e1, e2, ..., ei}})
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * evaluates <code>expr</code> with <code>i</code> taking on the values <code>e1, e2, ..., ei</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Table(x!, {x, 8})
	 * {1,2,6,24,120,720,5040,40320}
	 * 
	 * &gt;&gt; Table(x, {4})
	 * {x,x,x,x}
	 * 
	 * &gt;&gt; n=0
	 * &gt;&gt; Table(n= n + 1, {5})
	 * {1,2,3,4,5}
	 * 
	 * &gt;&gt; Table(i, {i, 4})
	 * {1,2,3,4}
	 * 
	 * &gt;&gt; Table(i, {i, 2, 5})
	 * {2,3,4,5}
	 * 
	 * &gt;&gt; Table(i, {i, 2, 6, 2})
	 * {2,4,6}
	 * 
	 * &gt;&gt; Table(i, {i, Pi, 2*Pi, Pi / 2})
	 * {Pi,3/2*Pi,2*Pi} 
	 * 
	 * &gt;&gt; Table(x^2, {x, {a, b, c}})
	 * {a^2,b^2,c^2}
	 * </pre>
	 * <p>
	 * <code>Table</code> supports multi-dimensional tables:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Table({i, j}, {i, {a, b}}, {j, 1, 2})
	 * {{{a,1},{a,2}},{{b,1},{b,2}}} 
	 * 
	 * &gt;&gt; Table(x, {x,0,1/3})
	 * {0}
	 * 
	 * &gt;&gt; Table(x, {x, -0.2, 3.9})
	 * {-0.2,0.8,1.8,2.8,3.8}
	 * </pre>
	 */
	public static class Table extends AbstractFunctionEvaluator {

		private static class UnaryArrayFunction implements IArrayFunction {
			final EvalEngine fEngine;

			final IExpr fValue;

			public UnaryArrayFunction(final EvalEngine engine, final IExpr value) {
				fEngine = engine;
				fValue = value;
			}

			@Override
			public IExpr evaluate(final IExpr[] index) {
				return fEngine.evaluate(fValue);
			}
		}

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
		 * Evaluate only the last iterator in <code>iter</code> for <code>Sum()</code> or <code>Product()</code>
		 * function calls.
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
			int size = ast.size();
			IASTAppendable variableList = F.ListAlloc(size);
			for (int i = 2; i < size; i++) {
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
					variableList.add(ast.get(i));
				} else {
					if (ast.get(i).isList()) {
						IAST list = (IAST) ast.get(i);
						if (list.size() >= 2) {
							if (list.arg1().isVariable()) {
								variableList.add(list.arg1());
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
			java.util.List<IExpr> reapList = engine.getReapList();
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

		private static IASTAppendable createResultList(java.util.Map<IExpr, Integer> map) {
			IASTAppendable result = F.ListAlloc(map.size());
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

		public static IASTAppendable tally1Arg(IAST list) {
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

	/**
	 * <pre>
	 * Take(expr, n)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>expr</code> with all but the first <code>n</code> leaves removed.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Take({a, b, c, d}, 3)
	 * {a,b,c}
	 * 
	 * &gt;&gt; Take({a, b, c, d}, -2)
	 * {c,d}
	 * 
	 * &gt;&gt; Take({a, b, c, d, e}, {2, -2})
	 * {b,c,d}
	 * </pre>
	 * <p>
	 * Take a submatrix:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; A = {{a, b, c}, {d, e, f}}
	 * &gt;&gt; Take(A, 2, 2)
	 * {{a,b},{d,e}}
	 * </pre>
	 * <p>
	 * Take a single column:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Take(A, All, {2})
	 * {{b},{e}}
	 * 
	 * &gt;&gt; Take(Range(10), {8, 2, -1})
	 * {8,7,6,5,4,3,2}
	 * 
	 * &gt;&gt; Take(Range(10), {-3, -7, -2})
	 * {8,6,4}
	 * </pre>
	 * <p>
	 * Cannot take positions <code>-5</code> through <code>-2</code> in <code>{1, 2, 3, 4, 5, 6}</code>.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Take(Range(6), {-5, -2, -2})
	 * Take({1, 2, 3, 4, 5, 6}, {-5, -2, -2})
	 * </pre>
	 * <p>
	 * Nonatomic expression expected at position <code>1</code> in <code>Take(l, {-1})</code>.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Take(l, {-1})
	 * Take(l,{-1})
	 * </pre>
	 * <p>
	 * Empty case
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Take({1, 2, 3, 4, 5}, {-1, -2})
	 * {}
	 * 
	 * &gt;&gt; Take({1, 2, 3, 4, 5}, {0, -1})
	 * {}
	 * 
	 * &gt;&gt; Take({1, 2, 3, 4, 5}, {1, 0})
	 * {}
	 * 
	 * &gt;&gt; Take({1, 2, 3, 4, 5}, {2, 1})
	 * {}
	 * 
	 * &gt;&gt; Take({1, 2, 3, 4, 5}, {1, 0, 2})
	 * {}
	 * </pre>
	 * <p>
	 * Cannot take positions <code>1</code> through <code>0</code> in <code>{1, 2, 3, 4, 5}</code>.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Take({1, 2, 3, 4, 5}, {1, 0, -1})
	 * Take({1, 2, 3, 4, 5}, {1, 0, -1})
	 * </pre>
	 */
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
			final IASTAppendable resultList = list.copyHead(10 > size ? size : 10);
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
	 * <pre>
	 * Total(list)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * adds all values in <code>list</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * Total(list, n)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * adds all values up to level <code>n</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * Total(list, {n})
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * totals only the values at level <code>{n}</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Total({1, 2, 3})
	 * 6
	 * 
	 * &gt;&gt; Total({{1, 2, 3}, {4, 5, 6}, {7, 8 ,9}})
	 * {12,15,18}
	 * 
	 * &gt;&gt; Total({{1, 2, 3}, {4, 5, 6}, {7, 8 ,9}}, 2)
	 * 45
	 * 
	 * &gt;&gt; Total({{1, 2, 3}, {4, 5, 6}, {7, 8 ,9}}, {2})
	 * {6,15,24}
	 * </pre>
	 */
	private final static class Total extends AbstractFunctionEvaluator {

		private static class TotalLevelSpecification extends VisitorLevelSpecification {
			public TotalLevelSpecification(final Function<IExpr, IExpr> function, final IExpr unevaledLevelExpr,
					boolean includeHeads, final EvalEngine engine) {
				super(function, unevaledLevelExpr, includeHeads, engine);
			}

			public TotalLevelSpecification(final Function<IExpr, IExpr> function, final int level,
					final boolean includeHeads) {
				super(function, level, includeHeads);
			}

			public IASTMutable createResult(IASTMutable ast, final IExpr x) {
				if (x.isAST()) {
					return ast.copy();
				}
				return ast.setAtCopy(0, F.Plus);
			}
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			VisitorLevelSpecification level = null;
			Function<IExpr, IExpr> tf = x -> x.isAST() ? ((IAST) x).setAtCopy(0, F.Plus) : x;

			if (ast.isAST2()) {
				level = new TotalLevelSpecification(tf, ast.arg2(), false, engine);
				// increment level because we select only subexpressions
			} else {
				level = new TotalLevelSpecification(tf, 1, false);
			}

			if (ast.arg1().isAST()) {
				// increment level because we select only subexpressions
				level.incCurrentLevel();
				IExpr temp = ast.arg1().accept(level);
				if (temp.isPresent()) {
					boolean te = engine.isThrowError();
					try {
						engine.setThrowError(true);
						return engine.evaluate(temp);
					} catch (MathException mex) {
						return F.NIL;
					} finally {
						engine.setThrowError(te);
					}
				}
			}

			return F.NIL;
		}

	}

	/**
	 * <pre>
	 * Union(set1, set2)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * get the union set from <code>set1</code> and <code>set2</code>.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:<br />
	 * </p>
	 * <ul>
	 * <li><a href="http://en.wikipedia.org/wiki/Union_(set_theory)">Wikipedia - Union (set theory)</a><br />
	 * </li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Union({1,2,3},{2,3,4})
	 * {1,2,3,4}
	 * </pre>
	 */
	private final static class Union extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() > 1) {
				if (ast.isAST1()) {
					if (ast.arg1().isAST()) {
						IAST arg1 = (IAST) ast.arg1();
						Set<IExpr> set = arg1.asSet();
						final IASTAppendable result = F.ListAlloc(set.size());
						for (IExpr IExpr : set) {
							result.append(IExpr);
						}
						EvalAttributes.sort(result, Comparators.ExprComparator.CONS);
						return result;
					}
					return F.NIL;
				}

				if (ast.arg1().isAST()) {
					IAST result = ((IAST) ast.arg1());
					for (int i = 2; i < ast.size(); i++) {
						if (!ast.get(i).isAST()) {
							return F.NIL;
						}
					}
					for (int i = 2; i < ast.size(); i++) {
						IAST expr = (IAST) ast.get(i);
						final IASTAppendable list = F.ListAlloc(result.size() + expr.size());
						result = union(result, expr, list);
					}
					if (result.size() > 2) {
						EvalAttributes.sort((IASTMutable) result, Comparators.ExprComparator.CONS);
					}
					return result;
				}
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
		public static IASTMutable union(IAST ast1, IAST ast2, final IASTAppendable result) {
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

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.FLAT | ISymbol.ONEIDENTITY);
		}
	}

	/**
	 * Fold the list from <code>start</code> index including to <code>end</code> index excluding into the
	 * <code>resultCollection</code>. If the <i>binaryFunction</i> returns <code>null</code>, the left element will be
	 * added to the result list, otherwise the result will be <i>folded</i> again with the next element in the list.
	 * 
	 * @param expr
	 *            initial value. If <code>null</code>use first element of list as initial value.
	 * @param list
	 * @param start
	 * @param end
	 * @param binaryFunction
	 * @param resultCollection
	 */
	public static IAST foldLeft(final IExpr expr, final IAST list, final int start, final int end,
			final BiFunction<IExpr, IExpr, ? extends IExpr> binaryFunction, final IASTAppendable resultCollection) {
		if (start < end) {
			IExpr elem;
			int from = start;
			if (expr != null) {
				elem = expr;
			} else {
				elem = list.get(from++);
			}
			resultCollection.append(elem);
			final IExpr[] temp = { elem };
			resultCollection.appendArgs(from, end, i -> {
				temp[0] = binaryFunction.apply(temp[0], list.get(i));
				return temp[0];
			});
		}
		return resultCollection;
	}

	/**
	 * Reverse the elements in the given <code>list</code>.
	 * 
	 * @param list
	 * @return
	 */
	public static IAST reverse(IAST list) {
		return list.reverse(F.ast(list.head(), list.size(), false));
	}

	public static void initialize() {
		Initializer.init();
	}

	private ListFunctions() {

	}
}
