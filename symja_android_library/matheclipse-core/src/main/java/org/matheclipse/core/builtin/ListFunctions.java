package org.matheclipse.core.builtin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import org.hipparchus.stat.StatUtils;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.ArithmeticUtil;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.eval.exception.AbortException;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.ArgumentTypeStopException;
import org.matheclipse.core.eval.exception.FlowControlException;
import org.matheclipse.core.eval.exception.NoEvalException;
import org.matheclipse.core.eval.exception.ResultException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.util.ISequence;
import org.matheclipse.core.eval.util.Iterator;
import org.matheclipse.core.eval.util.LevelSpec;
import org.matheclipse.core.eval.util.LevelSpecification;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.eval.util.Sequence;
import org.matheclipse.core.eval.util.positions.DeletePositions;
import org.matheclipse.core.eval.util.positions.InsertPositions;
import org.matheclipse.core.expression.ASTRealVector;
import org.matheclipse.core.expression.DefaultDict;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.DispatchExpr;
import org.matheclipse.core.generic.Comparators;
import org.matheclipse.core.generic.Comparators.SameTestComparator;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTDataset;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IExpr.COMPARE_TERNARY;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IIterator;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISparseArray;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.reflection.system.Product;
import org.matheclipse.core.reflection.system.Sum;
import org.matheclipse.core.visit.VisitorLevelSpecification;
import org.matheclipse.core.visit.VisitorRemoveLevelSpecification;
import org.matheclipse.core.visit.VisitorReplaceAll;

public final class ListFunctions {

  /**
   * See <a href="https://stackoverflow.com/a/4859279/24819">Get the indices of an array after
   * sorting?</a>
   */
  private static class LargestIndexComparator implements Comparator<Integer> {
    protected final IAST ast;
    protected EvalEngine engine;

    public LargestIndexComparator(IAST ast, EvalEngine engine) {
      this.ast = ast;
      this.engine = engine;
    }

    public Integer[] createIndexArray() {
      int size = ast.size();
      Integer[] indexes = new Integer[size - 1];
      for (int i = 1; i < size; i++) {
        indexes[i - 1] = i;
      }
      return indexes;
    }

    @Override
    public int compare(Integer index1, Integer index2) {
      IExpr arg1 = ast.get(index1);
      IExpr arg2 = ast.get(index2);
      if (arg1.isNumericFunction(false) && arg2.isNumericFunction(false)) {
        if (engine.evalGreater(arg1, arg2)) {
          return -1;
        }
        if (engine.evalLess(arg1, arg2)) {
          return 1;
        }
        if (engine.evalEqual(arg1, arg2)) {
          return 0;
        }
      }
      throw NoEvalException.CONST;
    }
  }

  private static final class SmallestIndexComparator extends LargestIndexComparator {
    public SmallestIndexComparator(IAST ast, EvalEngine engine) {
      super(ast, engine);
    }

    @Override
    public int compare(Integer index1, Integer index2) {
      IExpr arg1 = ast.get(index1);
      IExpr arg2 = ast.get(index2);
      if (arg1.isNumericFunction(false) && arg2.isNumericFunction(false)) {
        if (engine.evalLess(arg1, arg2)) {
          return -1;
        }
        if (engine.evalGreater(arg1, arg2)) {
          return 1;
        }
        if (engine.evalEqual(arg1, arg2)) {
          return 0;
        }
      }
      throw NoEvalException.CONST;
    }
  }

  private interface IVariablesFunction {
    public IExpr evaluate(final ISymbol[] variables, final IExpr[] index);
  }

  private static class TableFunction implements IVariablesFunction {
    final EvalEngine fEngine;

    final IExpr fValue;

    public TableFunction(final EvalEngine engine, final IExpr value) {
      fEngine = engine;
      fValue = value;
    }

    @Override
    public IExpr evaluate(final ISymbol[] variables, final IExpr[] index) {
      if (variables.length == 1) {
        if (variables[0] == null) {
          return fEngine.evaluate(fValue);
        }
        IExpr temp = F.subst(fValue, x -> x.equals(variables[0]), index[0]);
        return fEngine.evaluate(temp);
      }
      HashMap<ISymbol, IExpr> map = new HashMap<ISymbol, IExpr>();
      for (int i = 0; i < variables.length; i++) {
        ISymbol variable = variables[i];
        if (variable != null) {
          map.put(variable, index[i]);
        }
      }
      if (map.isEmpty()) {
        return fEngine.evaluate(fValue);
      }
      IExpr temp = map.size() == 0 ? fValue : F.subst(fValue, map);
      return fEngine.evaluate(temp);
    }

  }

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.Accumulate.setEvaluator(new Accumulate());
      S.Append.setEvaluator(new Append());
      S.AppendTo.setEvaluator(new AppendTo());
      S.Array.setEvaluator(new Array());
      S.ArrayPad.setEvaluator(new ArrayPad());
      S.Cases.setEvaluator(new Cases());
      S.Catenate.setEvaluator(new Catenate());
      S.Commonest.setEvaluator(new Commonest());
      S.Complement.setEvaluator(new Complement());
      S.Composition.setEvaluator(new Composition());
      S.ComposeList.setEvaluator(new ComposeList());
      S.ConstantArray.setEvaluator(new ConstantArray());
      S.Count.setEvaluator(new Count());
      S.CountDistinct.setEvaluator(new CountDistinct());
      S.Delete.setEvaluator(new Delete());
      S.DeleteDuplicates.setEvaluator(new DeleteDuplicates());
      S.DeleteDuplicatesBy.setEvaluator(new DeleteDuplicatesBy());
      S.DeleteMissing.setEvaluator(new DeleteMissing());
      S.DeleteCases.setEvaluator(new DeleteCases());
      S.Dispatch.setEvaluator(new Dispatch());
      S.DuplicateFreeQ.setEvaluator(new DuplicateFreeQ());
      S.Drop.setEvaluator(new Drop());
      S.Entropy.setEvaluator(new Entropy());
      S.Extract.setEvaluator(new Extract());
      S.First.setEvaluator(new First());
      S.FirstCase.setEvaluator(new FirstCase());
      S.FirstPosition.setEvaluator(new FirstPosition());
      S.Fold.setEvaluator(new Fold());
      S.FoldList.setEvaluator(new FoldList());
      S.Gather.setEvaluator(new Gather());
      S.GatherBy.setEvaluator(new GatherBy());
      S.GroupBy.setEvaluator(new GroupBy());
      S.Insert.setEvaluator(new Insert());
      S.Intersection.setEvaluator(new Intersection());
      S.Join.setEvaluator(new Join());
      S.Last.setEvaluator(new Last());
      S.Length.setEvaluator(new Length());
      S.LengthWhile.setEvaluator(new LengthWhile());
      S.LevelQ.setEvaluator(new LevelQ());
      S.Level.setEvaluator(new Level());
      S.Most.setEvaluator(new Most());
      S.Nearest.setEvaluator(new Nearest());
      S.NearestTo.setEvaluator(new NearestTo());
      S.PadLeft.setEvaluator(new PadLeft());
      S.PadRight.setEvaluator(new PadRight());
      S.Pick.setEvaluator(new Pick());
      S.Position.setEvaluator(new Position());
      S.Prepend.setEvaluator(new Prepend());
      S.PrependTo.setEvaluator(new PrependTo());
      S.Range.setEvaluator(new Range());
      S.RankedMax.setEvaluator(new RankedMax());
      S.RankedMin.setEvaluator(new RankedMin());
      S.Rest.setEvaluator(new Rest());
      S.Reverse.setEvaluator(new Reverse());
      S.Replace.setEvaluator(new Replace());
      S.ReplaceAll.setEvaluator(new ReplaceAll());
      S.ReplaceList.setEvaluator(new ReplaceList());
      S.ReplacePart.setEvaluator(new ReplacePart());
      S.ReplaceRepeated.setEvaluator(new ReplaceRepeated());
      S.RightComposition.setEvaluator(new RightComposition());
      S.Riffle.setEvaluator(new Riffle());
      S.RotateLeft.setEvaluator(new RotateLeft());
      S.RotateRight.setEvaluator(new RotateRight());
      S.Select.setEvaluator(new Select());
      S.SelectFirst.setEvaluator(new SelectFirst());
      S.Split.setEvaluator(new Split());
      S.SplitBy.setEvaluator(new SplitBy());
      S.Subdivide.setEvaluator(new Subdivide());
      S.Table.setEvaluator(new Table());
      S.Take.setEvaluator(new Take());
      S.TakeLargest.setEvaluator(new TakeLargest());
      S.TakeLargestBy.setEvaluator(new TakeLargestBy());
      S.TakeSmallest.setEvaluator(new TakeSmallest());
      S.TakeSmallestBy.setEvaluator(new TakeSmallestBy());
      S.TakeWhile.setEvaluator(new TakeWhile());
      S.Tally.setEvaluator(new Tally());
      S.Total.setEvaluator(new Total());
      S.Union.setEvaluator(new Union());
    }
  }

  private static class PositionConverter {
    /**
     * Convert the integer position number >= 0 into an object
     *
     * @param position which should be converted to an object
     * @return the Symja integer number representation
     */
    public IExpr toObject(final int position) {
      return F.ZZ(position);
    }

    /**
     * Convert the object into an integer number >= 0
     *
     * @param position the object which should be converted
     * @return -1 if the conversion is not possible
     */
    public int toInt(final IExpr position) {
      int val = position.toIntDefault();
      if (val < 0) {
        return -1;
      }
      return val;
    }
  }

  public static class MultipleConstArrayFunction implements IVariablesFunction {
    final IExpr fConstantExpr;

    public MultipleConstArrayFunction(final IExpr expr) {
      fConstantExpr = expr;
    }

    @Override
    public IExpr evaluate(final ISymbol[] variables, final IExpr[] index) {
      return fConstantExpr;
    }

  }

  private static class ArrayIterator implements IIterator<IExpr> {
    private final IInteger fOrigin;

    private final int fLength;

    private int fCounter;

    public ArrayIterator(final int to) {
      this(1, to);
    }

    public ArrayIterator(final int origin, final int length) {
      fOrigin = F.ZZ(origin);
      fLength = length;
      fCounter = 0;
    }

    @Override
    public boolean setUp() {
      return true;
    }

    @Override
    public void tearDown() {
      fCounter = 0;
    }

    @Override
    public boolean hasNext() {
      return fCounter < fLength;
    }

    @Override
    public IExpr next() {
      return fOrigin.add(F.ZZ(fCounter++));
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }

    @Override
    public int allocHint() {
      return fLength + 2;
    }
  }

  /** Table structure generator (i.e. lists, vectors, matrices, tensors) */
  public static class TableGenerator {

    private final List<? extends IIterator<IExpr>> fIterList;

    private final IExpr fDefaultValue;

    private final IAST fPrototypeList;

    private final IVariablesFunction fFunction;

    private int fIndex;

    private final IExpr[] fCurrentIndex;

    private final ISymbol[] fCurrentVariable;

    public TableGenerator(final List<? extends IIterator<IExpr>> iterList, final IAST prototypeList,
        final IVariablesFunction function) {
      this(iterList, prototypeList, function, F.NIL);
    }

    public TableGenerator(final List<? extends IIterator<IExpr>> iterList, final IAST prototypeList,
        final IVariablesFunction function, IExpr defaultValue) {
      fIterList = iterList;
      fPrototypeList = prototypeList;
      fFunction = function;
      fIndex = 0;
      fCurrentIndex = new IExpr[iterList.size()];
      fCurrentVariable = new ISymbol[iterList.size()];
      fDefaultValue = defaultValue;
    }

    public IExpr tableRecursive() {

      if (fIndex < fIterList.size()) {
        final IIterator<IExpr> iter = fIterList.get(fIndex);
        if (iter.setUp()) {
          try {
            final int index = fIndex++;
            if (fPrototypeList.head().equals(S.Plus) || fPrototypeList.head().equals(S.Times)) {
              if (iter.hasNext()) {
                fCurrentIndex[index] = iter.next();
                fCurrentVariable[index] = iter.getVariable();
                IExpr temp = tableRecursive();
                if (temp == null || temp.isNIL()) {
                  temp = fDefaultValue;
                }
                if (temp.isNumber()) {
                  if (fPrototypeList.head().equals(S.Plus)) {
                    return tablePlus((INumber) temp, iter, index);
                  } else {
                    return tableTimes((INumber) temp, iter, index);
                  }
                } else {
                  return createGenericTable(iter, index, iter.allocHint(), temp, null);
                }
              }
              if (iter.isInvalidNumeric()) {
                return fDefaultValue;
              }
              return F.NIL;
            }
            return createGenericTable(iter, index, iter.allocHint(), null, null);
          } finally {
            --fIndex;
            iter.tearDown();
          }
        }
        return fDefaultValue;
      }
      return fFunction.evaluate(fCurrentVariable, fCurrentIndex);
    }

    /**
     * Throws a {@link NoEvalException#CONST} flow control exception if the iterator's setup fails.
     * 
     * @return {@link F#NIL} if the iterator is empty
     * @throws FlowControlException
     */
    public IExpr tableThrowRecursive() throws FlowControlException {
      if (fIndex < fIterList.size()) {
        final IIterator<IExpr> iter = fIterList.get(fIndex);

        try {
          if (iter.setUpThrow()) {
            final int index = fIndex++;
            if (iter.hasNext()) {
              if (fPrototypeList.head().equals(S.Plus) || fPrototypeList.head().equals(S.Times)) {

                fCurrentIndex[index] = iter.next();
                fCurrentVariable[index] = iter.getVariable();
                IExpr temp = tableRecursive();
                if (temp == null || temp.isNIL()) {
                  temp = fDefaultValue;
                }
                if (temp.isNumber()) {
                  if (fPrototypeList.head().equals(S.Plus)) {
                    return tablePlus((INumber) temp, iter, index);
                  } else {
                    return tableTimes((INumber) temp, iter, index);
                  }
                } else {
                  // if (iter.isApproximationMode() && temp.isNumericFunction(true)) {
                  // INumber num = temp.evalNumber();
                  // if (num != null) {
                  // if (fPrototypeList.head().equals(S.Plus)) {
                  // return tablePlus(num, iter, index);
                  // } else {
                  // return tableTimes(num, iter, index);
                  // }
                  // }
                  // }
                  return createGenericTable(iter, index, iter.allocHint(), temp, null);
                }
              }
            } else {
              return F.NIL;
            }
            return createGenericTable(iter, index, iter.allocHint(), null, null);
          }
        } finally {
          --fIndex;
          iter.tearDown();
        }

        return fDefaultValue;
      }
      return fFunction.evaluate(fCurrentVariable, fCurrentIndex);
    }

    private IExpr tablePlus(INumber num, final IIterator<IExpr> iter, final int index) {
      int counter = 0;
      IExpr sumResult = num;
      while (iter.hasNext()) {
        fCurrentIndex[index] = iter.next();
        fCurrentVariable[index] = iter.getVariable();
        IExpr temp = tableRecursive();
        if (temp == null) {
          temp = fDefaultValue;
        }
        if (temp.isNumericFunction(true)) {
          // if (iter.isApproximationMode()) {
          // try {
          // Complex c = temp.evalfc();
          // if (c.isReal()) {
          // final double realPart = c.getReal();
          // if (F.isZero(realPart, 1.0e-12)) {
          // break;
          // }
          // sumResult = sumResult.add(c.getReal());
          // counter++;
          // continue;
          // }
          // if (F.isZero(c, 1.0e-6)) {
          // break;
          // }
          // sumResult = sumResult.plus(F.complexNum(c));
          // } catch (ArgumentTypeException ate) {
          // sumResult = sumResult.plus(temp);
          // }
          // } else {
          sumResult = sumResult.plus(temp);
          // }
        } else {
          return createGenericTable(iter, index, iter.allocHint() - counter, sumResult, temp);
        }
        counter++;
      }
      return sumResult;
    }

    private IExpr tableTimes(INumber num, final IIterator<IExpr> iter, final int index) {
      int counter = 0;
      IExpr productResult = num;
      while (iter.hasNext()) {
        fCurrentIndex[index] = iter.next();
        fCurrentVariable[index] = iter.getVariable();
        IExpr temp = tableRecursive();
        if (temp == null) {
          temp = fDefaultValue;
        }
        if (temp.isNumericFunction(true)) {
          // if (iter.isApproximationMode()) {
          // try {
          // Complex c = temp.evalfc();
          // if (c.isReal()) {
          // final double realPart = c.getReal();
          // if (F.isZero(realPart, 1.0e-12)) {
          // return F.C0;
          // }
          // if (F.isFuzzyEquals(realPart, 1.0, 1.0e-12)) {
          // break;
          // }
          // productResult = productResult.multiply(c.getReal());
          // counter++;
          // continue;
          // }
          // if (F.isZero(c, 1.0e-6)) {
          // return F.C0;
          // }
          // if (F.isFuzzyEquals(c, Complex.ONE, 1.0e-6)) {
          // break;
          // }
          // productResult = productResult.times(F.complexNum(c));
          // } catch (ArgumentTypeException ate) {
          // productResult = productResult.times(temp);
          // }
          // } else {
          if (temp.isZero()) {
            return F.C0;
          }
          productResult = productResult.times(temp);
          // }
        } else {
          return createGenericTable(iter, index, iter.allocHint() - counter, productResult, temp);
        }
        counter++;
      }
      return productResult;
    }

    /**
     * @param iter the current Iterator index
     * @param index index
     * @return
     */
    private IExpr createGenericTable(final IIterator<IExpr> iter, final int index,
        final int allocationHint, IExpr arg1, IExpr arg2) {
      final IASTAppendable result = fPrototypeList
          .copyHead(fPrototypeList.size() + (allocationHint > 0 ? allocationHint + 8 : 8));
      result.appendArgs(fPrototypeList);
      if (arg1 != null) {
        result.append(arg1);
      }
      if (arg2 != null) {
        result.append(arg2);
      }
      while (iter.hasNext()) {
        fCurrentIndex[index] = iter.next();
        fCurrentVariable[index] = iter.getVariable();
        IExpr temp = tableRecursive();
        if (temp == null || temp.isNIL()) {
          result.append(fDefaultValue);
        } else {
          result.append(temp);
        }
      }
      return result;
    }
  }

  /**
   *
   *
   * <pre>
   * Accumulate(list)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * accumulate the values of <code>list</code> returning a new list.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Accumulate({1, 2, 3})
   * {1,3,6}
   * </pre>
   */
  private static final class Accumulate extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isASTOrAssociation()) {
        IAST list = (IAST) arg1;
        int size = list.size();
        IASTAppendable resultList = F.ast(list.head(), size);
        return foldLeft(null, list, 1, size, (x, y) -> F.binaryAST2(S.Plus, x, y), resultList);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   *
   *
   * <pre>
   * Append(expr, item)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns <code>expr</code> with <code>item</code> appended to its leaves.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Append({1, 2, 3}, 4)
   * {1,2,3,4}
   * </pre>
   *
   * <p>
   * <code>Append</code> works on expressions with heads other than <code>List</code>:
   *
   * <pre>
   * &gt;&gt; Append(f(a, b), c)
   * f(a,b,c)
   * </pre>
   *
   * <p>
   * Unlike <code>Join</code>, <code>Append</code> does not flatten lists in <code>item</code>: <br>
   *
   * <pre>
   * &gt;&gt; Append({a, b}, {c, d})
   * {a,b,{c,d}}
   * </pre>
   *
   * <p>
   * Nonatomic expression expected.<br>
   *
   * <pre>
   * &gt;&gt; Append(a, b)
   * Append(a,b)
   * </pre>
   */
  private static final class Append extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr arg1 = engine.evaluate(ast.arg1());
      IAST arg1AST = Validate.checkASTOrAssociationType(ast, arg1, 1, engine);
      if (arg1AST.isNIL()) {
        return F.NIL;
      }
      IExpr arg2 = engine.evaluate(ast.arg2());
      if (arg1.isAssociation()) {
        if (arg2.isRuleAST() || arg2.isListOfRules() || arg2.isAssociation()) {
          IAssociation result = ((IAssociation) arg1).copy();
          result.appendRules((IAST) arg2);
          return result;
        } else {
          // The argument is not a rule or a list of rules.
          return Errors.printMessage(ast.topHead(), "invdt", F.CEmptyList, EvalEngine.get());
        }
      }
      return arg1AST.appendClone(arg2);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2_1;
    }
  }

  /**
   *
   *
   * <pre>
   * AppendTo(s, item)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * append <code>item</code> to value of <code>s</code> and sets <code>s</code> to the result.
   *
   * </blockquote>
   *
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
   *
   * <p>
   * 'Append' works on expressions with heads other than 'List':<br>
   *
   * <pre>
   * &gt;&gt; y = f()
   * &gt;&gt; AppendTo(y, x)
   * f(x)
   *
   * &gt;&gt; y
   * f(x)
   * </pre>
   *
   * <p>
   * {} is not a variable with a value, so its value cannot be changed.
   *
   * <pre>
   * &gt;&gt; AppendTo({}, 1)
   * AppendTo({}, 1)
   * </pre>
   *
   * <p>
   * a is not a variable with a value, so its value cannot be changed.
   *
   * <pre>
   * &gt;&gt; AppendTo(a, b)
   * AppendTo(a, b)
   * </pre>
   */
  private static final class AppendTo extends AbstractCoreFunctionEvaluator {

    private static class AppendToFunction implements Function<IExpr, IExpr> {
      private final IExpr value;

      public AppendToFunction(final IExpr value) {
        this.value = value;
      }

      @Override
      public IExpr apply(final IExpr symbolValue) {
        if (symbolValue.isAssociation()) {
          if (value.isRuleAST() || value.isListOfRules() || value.isAssociation()) {
            IAssociation result = ((IAssociation) symbolValue);
            result.appendRules((IAST) value);
            return result;
          } else {
            // The argument is not a rule or a list of rules.
            return Errors.printMessage(S.AppendTo, "invdt", F.CEmptyList, EvalEngine.get());
          }
        }
        if (!symbolValue.isASTOrAssociation()) {
          return F.NIL;
        }
        return ((IAST) symbolValue).appendClone(value);
      }
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isASTSizeGE(S.Part, 3) && arg1.first().isSymbol()) {
        ISymbol sym = (ISymbol) arg1.first();
        return assignPartTo(sym, (IAST) arg1, S.Append, ast, engine);
      }

      IExpr sym = Validate.checkIsVariable(ast, 1, engine);
      if (sym.isSymbol()) {
        IExpr arg2 = engine.evaluate(ast.arg2());
        Function<IExpr, IExpr> function = new AppendToFunction(arg2);
        IExpr[] results = ((ISymbol) sym).reassignSymbolValue(function, ast.topHead(), engine);
        if (results != null) {
          return results[1];
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDFIRST);
    }
  }

  /**
   *
   *
   * <pre>
   * Array(f, n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the <code>n</code>-element list <code>{f(1), ..., f(n)}</code>.
   *
   * </blockquote>
   *
   * <pre>
   * Array(f, n, a)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the n-element list <code>{f(a), ..., f(a + n)}</code>.
   *
   * </blockquote>
   *
   * <pre>
   * Array(f, {n, m}, {a, b})
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns an <code>n</code>-by-<code>m</code> matrix created by applying <code>f</code> to
   * indices ranging from <code>(a, b)</code> to <code>(a + n, b + m)</code>.
   *
   * </blockquote>
   *
   * <pre>
   * Array(f, dims, origins, h)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns an expression with the specified dimensions and index origins, with head <code>h
   * </code> (instead of <code>List</code>).
   *
   * </blockquote>
   *
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
   *
   * <p>
   * {2, 3} and {1, 2, 3} should have the same length.
   *
   * <pre>
   * &gt;&gt; Array(f, {2, 3}, {1, 2, 3})
   * Array(f, {2, 3}, {1, 2, 3})
   * </pre>
   *
   * <p>
   * Single or list of non-negative integers expected at position 2.
   *
   * <pre>
   * &gt;&gt; Array(f, a)
   * Array(f, a)
   * </pre>
   *
   * <p>
   * Single or list of non-negative integers expected at position 3.
   *
   * <pre>
   * &gt;&gt; Array(f, 2, b)
   * Array(f, 2, b)
   * </pre>
   */
  private static final class Array extends AbstractCoreFunctionEvaluator {

    private static class ExprArrayIterator implements IIterator<IExpr> {
      private final IExpr fOrigin;
      private int fCounter;
      private final int fLength;

      public ExprArrayIterator(final IExpr origin, final int length) {
        fOrigin = origin;
        this.fCounter = 0;
        this.fLength = length;
      }

      @Override
      public boolean setUp() {
        return true;
      }

      @Override
      public void tearDown() {
        this.fCounter = 0;
      }

      @Override
      public boolean hasNext() {
        return fCounter < fLength;
      }

      @Override
      public IExpr next() {
        if (fCounter >= fLength) {
          throw new NoSuchElementException();
        }
        return F.Plus(fOrigin, F.ZZ(fCounter++));
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }

      @Override
      public int allocHint() {
        return fLength + 2;
      }
    }

    private static class MultipleArrayFunction implements IVariablesFunction {
      final EvalEngine fEngine;

      final IAST fHeadAST;

      public MultipleArrayFunction(final EvalEngine engine, final IAST headAST) {
        fEngine = engine;
        fHeadAST = headAST;
      }

      @Override
      public IExpr evaluate(final ISymbol[] variables, final IExpr[] index) {
        final IASTAppendable ast = fHeadAST.copyAppendable(index.length);
        return fEngine.evaluate(ast.appendArgs(0, index.length, i -> index[i]));
      }

    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        IAST prototypeList;
        IExpr head = S.List;
        if (ast.size() == 5) {
          head = ast.arg4();
          prototypeList = F.ast(head);
        } else {
          prototypeList = F.CEmptyList;
        }
        final IExpr arg1 = ast.arg1();
        final IExpr arg2 = ast.arg2();
        final List<IIterator<IExpr>> iterList = new ArrayList<IIterator<IExpr>>();
        if (ast.size() >= 4) {
          final IExpr arg3 = ast.arg3();
          if (arg2.isInteger() && !arg3.isList()) {
            int length = arg2.toIntDefault();
            if (length <= 0) {
              // Single or list of non-negative machine-sized integers expected at position `1` of
              // `2`.
              return Errors.printMessage(ast.topHead(), "ilsmn", F.list(F.C2, ast), engine);
            }
            IExpr indexOrigin = arg3;

            int origin = indexOrigin.toIntDefault();
            if (F.isPresent(origin)) {
              iterList.add(new ArrayIterator(origin, length));
            } else {
              iterList.add(new ExprArrayIterator(indexOrigin, length));
            }
          } else if (arg2.isList() && arg3.isInteger()) {
            final IAST dimIter = (IAST) arg2; // dimensions
            int indx1 = Validate.checkIntType(ast, 3, Integer.MIN_VALUE + 1);
            for (int i = 1; i < dimIter.size(); i++) {
              int indx2 = Validate.checkIntType(dimIter, i);
              iterList.add(new ArrayIterator(indx1, indx2));
            }
          } else if (arg2.isInteger() && arg3.isList2()) {
            int n = arg2.toIntDefault();
            if (n <= 0) {
              // Single or list of non-negative machine-sized integers expected at position `1` of
              // `2`.
              return Errors.printMessage(ast.topHead(), "ilsmn", F.list(F.C2, ast), engine);
            }
            final IAST interval = (IAST) arg3;
            final IExpr from = interval.arg1();
            final IExpr to = interval.arg2();
            final IExpr subdivideResult = engine.evaluate(F.Subdivide(from, to, F.ZZ(n - 1)));
            if (subdivideResult.isList()) {
              IAST list = (IAST) subdivideResult;
              if (head != S.List) {
                list = list.setAtCopy(0, head);
              }
              return list.mapThread(F.unaryAST1(arg1, F.Slot1), 1);
            }
            return F.NIL;
          } else if (arg2.isList() && arg3.isList()) {
            final IAST dimIter = (IAST) arg2; // dimensions
            final IAST originIter = (IAST) arg3; // origins
            if (dimIter.size() != originIter.size()) {
              // `1` and `2` should have the same length.
              return Errors.printMessage(ast.topHead(), "plen", F.list(dimIter, originIter),
                  engine);
            }
            for (int i = 1; i < dimIter.size(); i++) {
              int indx1 = Validate.checkIntType(originIter, i);
              int indx2 = Validate.checkIntType(dimIter, i);
              iterList.add(new ArrayIterator(indx1, indx2));
            }
          }
        } else if (ast.size() >= 3 && arg2.isInteger()) {
          int indx1 = Validate.checkIntType(ast, 2);
          iterList.add(new ArrayIterator(indx1));
        } else if (ast.size() >= 3 && arg2.isList()) {
          final IAST dimIter = (IAST) arg2;
          for (int i = 1; i < dimIter.size(); i++) {
            int indx1 = Validate.checkIntType(dimIter, i);
            iterList.add(new ArrayIterator(indx1));
          }
        }

        if (iterList.size() > 0) {
          final IAST list = F.ast(arg1);
          final TableGenerator generator =
              new TableGenerator(iterList, prototypeList, new MultipleArrayFunction(engine, list));
          return generator.tableRecursive();
        }

      } catch (final ClassCastException | ArithmeticException e) {
        // ClassCastException: the iterators are generated only from IASTs
        // ArithmeticException: the toInt() function throws ArithmeticExceptions
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_4;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  /**
   *
   *
   * <pre>
   * ArrayPad(list, n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * adds <code>n</code> times <code>0</code> on the left and right of the <code>list</code>.
   *
   * </blockquote>
   *
   * <pre>
   * ArrayPad(list, {m,n})
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * adds <code>m</code> times <code>0</code> on the left and <code>n</code> times <code>0</code> on
   * the right.
   *
   * </blockquote>
   *
   * <pre>
   * ArrayPad(list, {m, n}, x)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * adds <code>m</code> times <code>x</code> on the left and <code>n</code> times <code>x</code> on
   * the right.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; ArrayPad({a, b, c}, 1, x)
   * {x,a,b,c,x}
   * </pre>
   */
  private static final class ArrayPad extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isAST()) {
        IAST arg1 = (IAST) ast.arg1();
        int m = -1;
        int n = -1;
        if (ast.arg2().isList2()) {
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

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }

    private static IExpr arrayPadMatrixAtom(IAST matrix, int[] dim, int m, int n, IExpr atom) {
      long columnDim = (long) dim[1] + (long) m + n;
      if (Config.MAX_AST_SIZE < columnDim) {
        ASTElementLimitExceeded.throwIt(columnDim);
      }
      long rowDim = dim[0] + m + n;
      if (Config.MAX_AST_SIZE < rowDim) {
        ASTElementLimitExceeded.throwIt(rowDim);
      }

      IASTAppendable result = matrix.copyHead((int) rowDim);
      // prepend m rows
      result.appendArgs(0, m, i -> atom.constantArray(S.List, 0, (int) columnDim));

      result.appendArgs(1, dim[0] + 1, i -> arrayPadAtom(matrix.getAST(i), m, n, atom));

      // append n rows
      result.appendArgs(0, n, i -> atom.constantArray(S.List, 0, (int) columnDim));
      return result;
    }

    private static IExpr arrayPadAtom(IAST ast, int m, int n, IExpr atom) {
      long intialCapacity = (long) m + (long) n + ast.argSize();
      if (Config.MAX_AST_SIZE < intialCapacity) {
        ASTElementLimitExceeded.throwIt(intialCapacity);
      }
      IASTAppendable result = ast.copyHead((int) intialCapacity);
      result.appendArgs(0, m, i -> atom);
      result.appendArgs(ast);
      result.appendArgs(0, n, i -> atom);
      return result;
    }
  }

  /**
   *
   *
   * <pre>
   * Cases(list, pattern)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the elements of <code>list</code> that match <code>pattern</code>.
   *
   * </blockquote>
   *
   * <pre>
   * Cases(list, pattern, ls)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the elements matching at levelspec <code>ls</code>.
   *
   * </blockquote>
   *
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
  private static final class Cases extends AbstractCoreFunctionOptionEvaluator {

    private static class CasesPatternMatcherFunctor implements Function<IExpr, IExpr> {
      protected final IPatternMatcher matcher;
      protected IASTAppendable resultCollection;
      final int maximumResults;
      private int resultsCounter;

      /**
       * @param matcher the pattern-matcher
       * @param resultCollection
       * @param maximumResults maximum number of results. -1 for for no limitation
       */
      public CasesPatternMatcherFunctor(final IPatternMatcher matcher,
          IASTAppendable resultCollection, int maximumResults) {
        this.matcher = matcher;
        this.resultCollection = resultCollection;
        this.maximumResults = maximumResults;
        this.resultsCounter = 0;
      }

      @Override
      public IExpr apply(final IExpr arg) throws AbortException {
        if (matcher.test(arg)) {
          resultCollection.append(arg);
          if (maximumResults >= 0) {
            resultsCounter++;
            if (resultsCounter >= maximumResults) {
              throw AbortException.ABORTED;
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
       * @param function the funtion which should determine the results
       * @param resultCollection
       * @param maximumResults maximum number of results. -1 for for no limitation
       */
      public CasesRulesFunctor(final Function<IExpr, IExpr> function,
          IASTAppendable resultCollection, int maximumResults) {
        this.function = function;
        this.resultCollection = resultCollection;
        this.maximumResults = maximumResults;
      }

      @Override
      public IExpr apply(final IExpr arg) throws AbortException {
        IExpr temp = function.apply(arg);
        if (temp.isPresent()) {
          resultCollection.append(temp);
          if (maximumResults >= 0) {
            resultsCounter++;
            if (resultsCounter >= maximumResults) {
              throw AbortException.ABORTED;
            }
          }
        }
        return F.NIL;
      }
    }


    @Override
    protected IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine) {

      try {
        boolean includeHeads = options[0].isTrue();
        if (argSize >= 2 && argSize <= 4) {
          // final OptionArgs options = OptionArgs.createOptionArgs(ast, engine);
          // if (options != null) {
          // IExpr option = options.getOption(S.Heads);
          // if (option.isTrue()) {
          // includeHeads = true;
          // }
          // ast = ast.most();
          // }
          final IExpr arg1 = engine.evaluate(ast.arg1());
          if (arg1.isASTOrAssociation()) {
            final IExpr arg2 = engine.evalPattern(ast.arg2());
            if (argSize >= 3) {
              final IExpr arg3 = engine.evaluate(ast.arg3());
              int maximumResults = -1;
              if (argSize == 4) {
                maximumResults = Validate.checkIntType(ast, 4);
              }
              IASTAppendable result = F.ListAlloc(8);
              if (arg2.isRuleAST()) {
                try {
                  Function<IExpr, IExpr> function = Functors.rules(arg2, engine);
                  CasesRulesFunctor crf = new CasesRulesFunctor(function, result, maximumResults);
                  VisitorLevelSpecification level =
                      new VisitorLevelSpecification(crf, arg3, includeHeads, engine);
                  arg1.accept(level);
                } catch (AbortException aex) {
                  // reached maximum number of results
                }
                return result;
              }

              try {
                final IPatternMatcher matcher = engine.evalPatternMatcher(arg2);
                CasesPatternMatcherFunctor cpmf =
                    new CasesPatternMatcherFunctor(matcher, result, maximumResults);
                VisitorLevelSpecification level =
                    new VisitorLevelSpecification(cpmf, arg3, includeHeads, engine);
                arg1.accept(level);
              } catch (AbortException aex) {
                // reached maximum number of results
              }
              return result;
            } else {
              return cases((IAST) arg1, arg2, includeHeads, engine);
            }
          }
          return F.CEmptyList;
        }
      } catch (final ValidateException ve) {
        // see level specification and int number validation
        return Errors.printMessage(S.Cases, ve, engine);
      } catch (final RuntimeException rex) {
        return Errors.printMessage(S.Cases, rex, engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_4_1;
    }

    public static IAST cases(final IAST ast, final IExpr pattern, boolean heads,
        EvalEngine engine) {
      if (pattern.isRuleAST()) {
        Function<IExpr, IExpr> function = Functors.rules(pattern, engine);
        IAST[] results = ast.filterNIL(function);
        return results[0];
      }
      final IPatternMatcher matcher = engine.evalPatternMatcher(pattern);
      IASTAppendable resultAST = F.ListAlloc(ast.size());
      ast.forEach(heads ? 0 : 1, ast.size(), appendIfMatched(matcher, resultAST));
      return resultAST;
    }

    /**
     * Create a <code>Consumer</code> which, if <code>matcher</code> returns <code>true</code>,
     * appends the argument to the <code>resultAST
     * </code>
     *
     * @param matcher
     * @param resultAST
     * @return
     */
    private static Consumer<? super IExpr> appendIfMatched(final IPatternMatcher matcher,
        IASTAppendable resultAST) {
      return x -> {
        if (matcher.test(x)) {
          resultAST.append(x);
        }
      };
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
      setOptions(newSymbol, S.Heads, S.False);
    }

  }

  /**
   *
   *
   * <pre>
   * Catenate({l1, l2, ...})
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * concatenates the lists <code>l1, l2, ...</code>
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Catenate({{1, 2, 3}, {4, 5}})
   * {1, 2, 3, 4, 5}
   * </pre>
   */
  private static final class Catenate extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isList()) {
        IAST list = (IAST) ast.arg1();
        int[] calculatedAllocSize = {1};
        if (list.forAll(isListOrAssociation(calculatedAllocSize))) {
          IASTAppendable resultList = F.ast(S.List, calculatedAllocSize[0]);
          list.forEach(x -> resultList.appendArgs((IAST) x));
          return resultList;
        }
      }
      return F.NIL;
    }

    /**
     * Gives a <code>Predicate</code> which tests if the arguments are all a <code>List</code> or an
     * <code>Association</code>.
     *
     * @param calculatedAllocSize the size needed to allocate for a reuslt list
     * @return
     */
    private static Predicate<? super IExpr> isListOrAssociation(int[] calculatedAllocSize) {
      return x -> {
        if (x.isList() || x.isAssociation()) {
          calculatedAllocSize[0] += x.argSize();
          return true;
        }
        return false;
      };
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   *
   *
   * <pre>
   * <code>Commonest(dataValueList)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * the mode of a list of data values is the value that appears most often.
   *
   * </blockquote>
   *
   * <pre>
   * <code>Commonest(dataValueList, n)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * return the <code>n</code> values that appears most often.
   *
   * </blockquote>
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Mode_(statistics)">Wikipedia - Mode (statistics)</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; Commonest({1, 3, 6, 6, 6, 6, 7, 7, 12, 12, 17})
   * {6}
   * </code>
   * </pre>
   *
   * <p>
   * Given the list of data <code>{1, 1, 2, 4, 4}</code> the mode is not unique – the dataset may be
   * said to be <strong>bimodal</strong>, while a set with more than two modes may be described as
   * <strong>multimodal</strong>.
   *
   * <pre>
   * <code>&gt;&gt; Commonest({1, 1, 2, 4, 4})
   * {1,4}
   * </code>
   * </pre>
   */
  private static final class Commonest extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isRealVector() && ast.isAST1()) {
        double[] values = arg1.toDoubleVector();
        if (values == null) {
          return F.NIL;
        }
        // The mode of a set of data is implemented as Commonest(data).
        return new ASTRealVector(StatUtils.mode(values), false);
      }
      if (arg1.isList()) {
        IAST list = (IAST) arg1;
        int n = -1;
        if (ast.isAST2()) {
          n = Validate.checkIntType(S.Commonest, ast.arg2(), 0, engine);
          if (F.isNotPresent(n)) {
            return F.NIL;
          }
        }

        IASTAppendable tallyResult = tally(list);
        EvalAttributes.sort(//
            tallyResult, //
            (o1, o2) -> o2.second().compareTo(o1.second()));

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
            if (counter < n) {
              // print warning (not an error)
              // The requested number of elements `1` is greater than the number of distinct
              // elements `2` only `2` elements will be returned.
              Errors.printMessage(ast.topHead(), "dstlms", F.List(F.ZZ(n), F.ZZ(counter)), engine);
            }
            return result;
          }
        }
        return F.CEmptyList;
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   *
   *
   * <pre>
   * Complement(set1, set2)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * get the complement set from <code>set1</code> and <code>set2</code>.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Complement_(set_theory)">Wikipedia - Complement (set
   * theory)</a>
   * </ul>
   *
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
  private static final class Complement extends AbstractFunctionOptionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, final int argSize, final IExpr[] option,
        final EvalEngine engine, IAST originalAST) {
      if (ast.arg1().isASTOrAssociation() && ast.arg2().isASTOrAssociation()) {
        final BiPredicate<IExpr, IExpr> test = Predicates.sameTest(option[0], engine);
        SameTestComparator sameTest = new Comparators.SameTestComparator(test);
        IExpr head1 = ast.arg1().head();
        if (!ast.arg2().head().equals(head1)) {
          // Heads `1` and `2` at positions `3` and `4` are expected to be the same.
          return Errors.printMessage(S.Complement, "heads2",
              F.List(ast.arg2().head(), head1, F.C2, F.C1), engine);
        }
        if (ast.exists(x -> !x.isASTOrAssociation(), 3)) {
          return F.NIL;
        }
        final IAST arg1 = (IAST) ast.arg1();
        final IAST arg2 = (IAST) ast.arg2();
        IAST result = complement(head1, arg1, arg2, sameTest);
        if (result.isPresent()) {
          for (int i = 3; i < argSize + 1; i++) {
            IExpr expr = ast.get(i);
            if (!expr.head().equals(head1)) {
              // Heads `1` and `2` at positions `3` and `4` are expected to be the same.
              return Errors.printMessage(S.Complement, "heads2",
                  F.List(expr.head(), head1, F.ZZ(i), F.C1), engine);
            }
            if (expr.isASTOrAssociation()) {
              result = complement(head1, result, (IAST) expr, sameTest);
            }
          }
          return result;
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_INFINITY;
    }

    public static IAST complement(IExpr head, final IAST arg1, IAST arg2,
        SameTestComparator sameTest) {
      Set<IExpr> set2 = arg2.asSortedSet(sameTest);
      if (set2 != null) {
        Set<IExpr> set3 = new TreeSet<IExpr>(sameTest);
        IASTMutable arg1Copy = arg1.copy();
        EvalAttributes.sort(arg1Copy);
        arg1Copy.forEach(x -> {
          if (!set2.contains(x)) {
            set3.add(x);
          }
        });
        IASTAppendable result = F.ast(head, set3.size());
        result.appendAll(set3);
        EvalAttributes.sort(result);
        return result;
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, S.SameTest, S.Automatic);
    }
  }

  /**
   *
   *
   * <pre>
   * Composition(sym1, sym2,...)[arg1, arg2,...]
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * creates a composition of the symbols applied at the arguments.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Composition(u, v, w)[x, y]
   * u(v(w(x,y)))
   * </pre>
   */
  private static final class Composition extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.head().equals(S.Composition)) {
        if (ast.isAST0()) {
          return S.Identity;
        }
        return ast.remove(x -> x.equals(S.Identity));
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
   *
   *
   * <pre>
   * ComposeList(list - of - symbols, variable)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * creates a list of compositions of the symbols applied at the argument <code>x</code>.
   *
   * </blockquote>
   *
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
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   *
   *
   * <pre>
   * ConstantArray(expr, n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns a list of <code>n</code> copies of <code>expr</code>.
   *
   * </blockquote>
   *
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
  private static final class ConstantArray extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        if ((ast.size() >= 3) && (ast.size() <= 5)) {
          int indx1, indx2;
          final List<ArrayIterator> iterList = new ArrayList<ArrayIterator>();
          final IExpr constantExpr = ast.arg1();
          if ((ast.isAST2()) && (ast.arg2().isInteger())) {
            indx1 = Validate.checkIntType(ast, 2);
            return constantExpr.constantArray(S.List, 0, indx1);
          } else if ((ast.isAST2()) && ast.arg2().isList()) {
            final IAST dimensions = (IAST) ast.arg2();
            int[] dim = new int[dimensions.argSize()];
            for (int i = 1; i < dimensions.size(); i++) {
              indx1 = Validate.checkIntType(dimensions, i);
              dim[i - 1] = indx1;
            }
            if (dim.length == 0) {
              return F.CEmptyList;
            }
            return constantExpr.constantArray(S.List, 0, dim);
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
            IAST resultList = F.CEmptyList;
            if (ast.size() == 5) {
              resultList = F.ast(ast.arg4());
            }
            final TableGenerator generator = new TableGenerator(iterList, resultList,
                new MultipleConstArrayFunction(constantExpr));
            return generator.tableRecursive();
          }
        }
      } catch (final ValidateException ve) {
        // int number validation
        return Errors.printMessage(ast.topHead(), ve, engine);
      } catch (final ClassCastException | ArithmeticException e) {
        // ClassCastException: the iterators are generated only from IASTs
        // ArithmeticException: the toInt() function throws ArithmeticExceptions
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  /**
   *
   *
   * <pre>
   * Count(list, pattern)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the number of times <code>pattern</code> appears in <code>list</code>.
   *
   * </blockquote>
   *
   * <pre>
   * Count(list, pattern, ls)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * counts the elements matching at levelspec <code>ls</code>.
   *
   * </blockquote>
   *
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
  private static final class Count extends AbstractFunctionOptionEvaluator {
    private static class CountFunctor implements Function<IExpr, IExpr> {
      protected final IPatternMatcher matcher;
      protected int counter;

      /** @return the counter */
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
    public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] option,
        final EvalEngine engine, IAST originalAST) {

      boolean includeHeads = option[0].isTrue();

      if (ast.headID() != ID.Count) {
        // operator form
        if (!ast.isAST1()) {
          return Errors.printArgMessage(ast, ARGS_1_1, engine);
        }
        if (!ast.head().isAST1()) {
          return Errors.printArgMessage((IAST) ast.head(), ARGS_1_1, engine);
        }
        return F.Count(ast.arg1(), ast.head().first());
      } else {
        if (ast.isAST1()) {
          return F.NIL;
        }
        final IExpr arg1 = ast.arg1();
        final VisitorLevelSpecification level;
        CountFunctor mf = new CountFunctor(engine.evalPatternMatcher(ast.arg2()));
        if (ast.size() >= 4) {
          final IExpr arg3 = engine.evaluate(ast.arg3());
          level = new VisitorLevelSpecification(mf, arg3, includeHeads, engine);
        } else {
          level = new VisitorLevelSpecification(mf, 1, includeHeads);
        }
        arg1.accept(level);
        return F.ZZ(mf.getCounter());
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3_0;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, S.Heads, S.False);
    }
  }

  /**
   *
   *
   * <pre>
   * <code>CountDistinct(list)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the number of distinct entries in <code>list</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; CountDistinct({3, 7, 10, 7, 5, 3, 7, 10})
   * 4
   *
   * &gt;&gt; CountDistinct({{a, a}, {a, a, a}, a, a})
   * 3
   * </code>
   * </pre>
   */
  private static final class CountDistinct extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr arg1 = engine.evaluate(ast.arg1());
      if (arg1.isASTOrAssociation()) {
        final Set<IExpr> map = new HashSet<IExpr>();
        ((IAST) arg1).forEach(x -> map.add(x));
        return F.ZZ(map.size());
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /** Delete(list,n) - delete the n-th argument from the list. Negative n counts from the end. */
  private static class Delete extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();

      if (arg1.isASTOrAssociation()) {
        try {
          IAST ast1 = (IAST) arg1;
          if (arg2.isInteger() || arg2.isString() || arg2.isKey()) {
            // `All` and `Span` cannot be used in Delete and Insert
            arg2 = arg2.makeList();
          }
          if (arg2.isListOfLists()) {
            IAST listOfLists = ((IAST) arg2);
            return DeletePositions.deleteListOfPositions(ast1, listOfLists);
          } else if (arg2.isList()) {
            IExpr temp = DeletePositions.deletePositions(ast1, (IAST) arg2);
            if (temp.isPresent()) {
              return temp;
            }
            return ast1;
          }
        } catch (final ValidateException ve) {
          return Errors.printMessage(ast.topHead(), ve, engine);
        } catch (RuntimeException rex) {
          if (Config.DEBUG) {
            rex.printStackTrace();
          }
          Errors.rethrowsInterruptException(rex);
          return Errors.printMessage(S.Delete, rex, engine);
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2_1;
    }
  }

  /**
   *
   *
   * <pre>
   * DeleteCases(list, pattern)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the elements of <code>list</code> that do not match <code>pattern</code>.
   *
   * </blockquote>
   *
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
  private static final class DeleteCases extends AbstractCoreFunctionEvaluator {

    private static class DeleteCasesPatternMatcherFunctor implements Function<IExpr, IExpr> {
      private final IPatternMatcher matcher;

      /** @param matcher the pattern-matcher */
      public DeleteCasesPatternMatcherFunctor(final IPatternMatcher matcher) {
        this.matcher = matcher;
      }

      @Override
      public IExpr apply(final IExpr arg) {
        if (matcher.test(arg)) {
          return S.Null;
        }
        return F.NIL;
      }
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr arg1 = engine.evaluate(ast.arg1());
      if (arg1.isASTOrAssociation()) {
        final IPatternMatcher matcher = engine.evalPatternMatcher(ast.arg2());
        if (ast.isAST3() || ast.size() == 5) {
          final IExpr arg3 = engine.evaluate(ast.arg3());
          int maximumRemoveOperations = -1;
          IASTAppendable arg1RemoveClone = ((IAST) arg1).copyAppendable();
          try {
            if (ast.size() == 5) {
              if (ast.arg4().isInfinity()) {
                maximumRemoveOperations = Integer.MAX_VALUE;
              } else {
                maximumRemoveOperations = Validate.checkIntType(ast, 4);
              }
            }

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

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_4;
    }

    public static IAST deleteCases(final IAST ast, final IPatternMatcher matcher) {
      return ast.removeIf(matcher);
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  /**
   *
   *
   * <pre>
   * <code>DeleteDuplicates(list)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * deletes duplicates from <code>list</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; DeleteDuplicates({1, 7, 8, 4, 3, 4, 1, 9, 9, 2, 1})
   * {1,7,8,4,3,9,2}
   * </code>
   * </pre>
   *
   * <pre>
   * <code>&gt;&gt; DeleteDuplicates({3,2,1,2,3,4}, Less)
   * {3,2,1}
   * </code>
   * </pre>
   */
  private static final class DeleteDuplicates extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr test = S.Equal;
      if (ast.isAST2()) {
        test = ast.arg2();
      }
      if (ast.arg1().isList()) {
        IAST list = (IAST) ast.arg1();

        BiPredicate<IExpr, IExpr> biPredicate = Predicates.isBinaryTrue(test);
        int size = list.size();
        final IASTAppendable result = F.ListAlloc(size);
        iLoop: for (int i = 1; i < size; i++) {
          IExpr listElement = list.get(i);
          for (int j = 1; j < result.size(); j++) {
            if (biPredicate.test(result.get(j), listElement)) {
              continue iLoop;
            }
          }
          result.append(listElement);
        }
        return result;
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static final class DeleteDuplicatesBy extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.isAST2()) {
        if (ast.arg1().isList() || ast.arg1().isAssociation()) {
          IExpr test = ast.arg2();
          Set<IExpr> set = new HashSet<IExpr>();
          if (ast.arg1().isList()) {
            IAST list = (IAST) ast.arg1();
            int size = list.size();
            final IASTAppendable result = list.copyHead(size);
            for (int i = 1; i < size; i++) {
              IExpr listElement = list.get(i);
              IExpr x = engine.evaluate(F.unaryAST1(test, listElement));
              if (!set.contains(x)) {
                result.append(listElement);
                set.add(x);
              }
            }
            return result;
          } else {
            IAssociation assoc = (IAssociation) ast.arg1();
            int size = assoc.size();
            final IASTAppendable result = assoc.copyHead(size);
            for (int i = 1; i < size; i++) {
              IExpr assocRule = assoc.getRule(i);
              IExpr x = engine.evaluate(F.unaryAST1(test, assocRule.second()));
              if (!set.contains(x)) {
                result.append(assocRule);
                set.add(x);
              }
            }
            return result;
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2_1;
    }
  }

  private static final class DeleteMissing extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        if (ast.arg1().isList()) {
          IAST list = (IAST) ast.arg1();
          return list.select(x -> !x.isAST(S.Missing));
        } else if (ast.arg1().isAssociation()) {
          IAssociation assoc = (IAssociation) ast.arg1();
          return assoc.select(x -> !x.isAST(S.Missing));
        }
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class Dispatch extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast instanceof DispatchExpr) {
        return F.NIL;
      }
      if (ast.isAST1()) {
        // print no message if args have wrong type
        try {
          IExpr evaledArg1 = engine.evaluate(ast.arg1());

          if (evaledArg1.isListOfRules(false)) {
            return DispatchExpr.newInstance((IAST) evaledArg1);
          } else if (evaledArg1.isRuleAST()) {
            return DispatchExpr.newInstance(F.list(evaledArg1));
          } else if (evaledArg1.isAssociation()) {
            return DispatchExpr.newInstance((IAssociation) evaledArg1);
            // } else {
            // throw new ArgumentTypeException(
            // "rule expressions (x->y) expected instead of " + arg1.toString());
          }
        } catch (ValidateException ve) {
          // return engine.printMessage(ast.topHead(), ve);
        }
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  private static final class DuplicateFreeQ extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr test = S.Equal;
      if (ast.isAST2()) {
        test = ast.arg2();
      }
      if (ast.arg1().isList()) {
        IAST list = (IAST) ast.arg1();

        BiPredicate<IExpr, IExpr> biPredicate = Predicates.isBinaryTrue(test);
        int size = list.size();
        for (int i = 1; i < size; i++) {
          IExpr listElement = list.get(i);
          for (int j = i + 1; j < list.size(); j++) {
            if (biPredicate.test(list.get(j), listElement)) {
              return S.False;
            }
          }
        }
        return S.True;
      }
      return S.False;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   *
   *
   * <pre>
   * Drop(expr, n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns <code>expr</code> with the first <code>n</code> leaves removed.
   *
   * </blockquote>
   *
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
   *
   * <p>
   * Drop a submatrix:
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
   *
   * <p>
   * Cannot drop positions -5 through -2 in {1, 2, 3, 4, 5, 6}.
   *
   * <pre>
   * &gt;&gt; Drop(Range(6), {-5, -2, -2})
   * Drop({1, 2, 3, 4, 5, 6}, {-5, -2, -2})
   * </pre>
   */
  private static final class Drop extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr arg1 = ast.arg1();
      if (!arg1.isASTOrAssociation() && !arg1.isSparseArray()) {
        // Nonatomic expression expected at position `1` in `2`.
        return Errors.printMessage(ast.topHead(), "normal", F.List(F.C1, ast), engine);
      }
      if (ast.isAST2() && ast.arg2().isZero()) {
        return arg1;
      }
      try {
        final ISequence[] sequ =
            Sequence.createSequences(ast, 2, ast.size(), "drop", S.Drop, engine);
        if (sequ == null) {
          return F.NIL;
        }
        if (arg1.isASTOrAssociation()) {
          final IAST list = (IAST) arg1;
          final IASTAppendable resultList = list.copyAppendable();
          drop(resultList, 0, sequ);
          return resultList;
        } else if (arg1.isSparseArray()) {
          // TODO return sparse array instead of lists
          final IASTAppendable resultList = ((ISparseArray) arg1).normal(false).copyAppendable();
          drop(resultList, 0, sequ);
          return resultList;
        }
      } catch (ValidateException ve) {
        Errors.printMessage(ast.topHead(), ve, engine);
      } catch (IndexOutOfBoundsException e) {
        Errors.printMessage(S.Drop, e, engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_INFINITY;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.NHOLDREST);
    }

    /**
     * Drop (remove) the list elements according to the <code>sequenceSpecifications</code> for the
     * list indexes.
     *
     * @param resultList
     * @param level recursion level
     * @param sequenceSpecifications one or more ISequence specifications
     * @return
     */
    private static IAST drop(final IASTAppendable resultList, final int level,
        final ISequence[] sequenceSpecifications) {
      sequenceSpecifications[level].setListSize(resultList.size());
      final int newLevel = level + 1;
      int j = sequenceSpecifications[level].getStart();
      int end = sequenceSpecifications[level].getEnd();
      int step = sequenceSpecifications[level].getStep();
      if (step < 0) {
        end--;
        if (j < end || end <= 0) {
          throw new ArgumentTypeException("cannot drop positions "
              + sequenceSpecifications[level].getStartOffset() + " through "
              + sequenceSpecifications[level].getEndOffset() + " in " + resultList);
        }
        for (int i = j; i >= end; i += step) {
          if (j >= resultList.size()) {
            throw new ArgumentTypeException("cannot drop positions "
                + sequenceSpecifications[level].getStartOffset() + " through "
                + sequenceSpecifications[level].getEndOffset() + " in " + resultList);
          }
          resultList.remove(j);
          j += step;
        }
      } else {
        if (j == 0) {
          throw new ArgumentTypeException("cannot drop positions "
              + sequenceSpecifications[level].getStartOffset() + " through "
              + sequenceSpecifications[level].getEndOffset() + " in " + resultList);
        }
        for (int i = j; i < end; i += step) {
          if (j >= resultList.size()) {
            throw new ArgumentTypeException("cannot drop positions "
                + sequenceSpecifications[level].getStartOffset() + " through "
                + sequenceSpecifications[level].getEndOffset() + " in " + resultList);
          }
          resultList.remove(j);
          j += step - 1;
        }
      }
      for (int j2 = 1; j2 < resultList.size(); j2++) {
        if (sequenceSpecifications.length > newLevel) {
          if (resultList.get(j2).isAST()) {
            final IASTAppendable tempList = ((IAST) resultList.get(j2)).copyAppendable();
            resultList.set(j2, drop(tempList, newLevel, sequenceSpecifications));
          } else {
            throw new ArgumentTypeException(
                "Cannot execute drop for argument: " + resultList.get(j2).toString());
          }
        }
      }
      return resultList;
    }
  }

  /**
   *
   *
   * <pre>
   * <code>Entropy(list)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * return the base <code>E</code> (Shannon) information entropy of the elements in <code>list
   * </code>.
   *
   * </blockquote>
   *
   * <pre>
   * <code>Entropy(b, list)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * return the base <code>b</code> (Shannon) information entropy of the elements in <code>list
   * </code>.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Entropy_(information_theory)">Wikipedia - Entropy
   * (information theory)</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; Entropy({a, b, b})
   * 2/3*Log(3/2)+Log(3)/3
   *
   * &gt;&gt; Entropy({a, b, b,c,c,c,d})
   * 3/7*Log(7/3)+2/7*Log(7/2)+2/7*Log(7)
   *
   * &gt;&gt; Entropy(b,{a,c,c})
   * 2/3*Log(3/2)/Log(b)+Log(3)/(3*Log(b))
   * </code>
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="Commonest.md">Commonest</a>, <a href="Counts.md">Counts</a>, <a href="E.md">E</a>,
   * <a href="Tally.md">Tally</a>
   */
  private static final class Entropy extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IAST list = F.NIL;
      if (ast.isAST1()) {
        list = Validate.checkListType(ast, 1, engine);
      } else if (ast.isAST2()) {
        list = Validate.checkListType(ast, 2, engine);
      }
      if (list.isPresent()) {
        if (ast.isAST1()) {
          // use base E logarithm
          return entropy(list, F.Log(F.Slot1), 1);
        }
        IExpr shannonBase = ast.arg1();
        return entropy(list, F.Log(shannonBase, F.Slot1), 2);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    private static IExpr entropy(IAST list, IAST logAST, int logASTIndex) {
      java.util.Map<IExpr, Integer> map = new LinkedHashMap<IExpr, Integer>();
      for (int i = 1; i < list.size(); i++) {
        Integer value = map.get(list.get(i));
        if (value == null) {
          map.put(list.get(i), 1);
        } else {
          map.put(list.get(i), value + 1);
        }
      }
      IASTAppendable result = F.PlusAlloc(map.size());
      int n = list.argSize();
      for (java.util.Map.Entry<IExpr, Integer> entry : map.entrySet()) {
        int count = entry.getValue();
        IRational p = F.QQ(count, n);
        IAST times = F.Times(p, logAST.setAtCopy(logASTIndex, p));
        result.append(times);
      }
      return result.negate();
    }
  }

  /**
   *
   *
   * <pre>
   * Extract(expr, list)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * extracts parts of <code>expr</code> specified by <code>list</code>.
   *
   * </blockquote>
   *
   * <pre>
   * Extract(expr, {list1, list2, ...})'
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * extracts a list of parts.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <p>
   * <code>Extract(expr, i, j, ...)</code> is equivalent to <code>Part(expr, {i, j, ...})</code>.
   *
   * <pre>
   * &gt;&gt; Extract(a + b + c, {2})
   * b
   *
   * &gt;&gt; Extract({{a, b}, {c, d}}, {{1}, {2, 2}})
   * {{a,b},d}
   * </pre>
   */
  private static final class Extract extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.arg1().isASTOrAssociation()) {
        IAST list = (IAST) ast.arg1();
        IExpr arg3 = F.NIL;
        if (ast.isAST3()) {
          arg3 = ast.arg3();
        }
        if (ast.arg2().isInteger()) {
          int indx = ast.arg2().toIntDefault();
          if (F.isNotPresent(indx)) {
            return F.NIL;
          }
          if (indx < 0) {
            // negative n counts from the end
            indx = list.size() + indx;
            if (indx <= 0) {
              // == 0 - for MMA behaviour
              return F.NIL;
            }
          }
          if (indx < list.size()) {
            if (arg3.isPresent()) {
              return F.unaryAST1(arg3, list.get(indx));
            }
            return list.get(indx);
          }
        } else if (ast.arg2().isList()) {
          IAST arg2 = (IAST) ast.arg2();
          if (arg2.isListOfLists()) {
            final int arg2Size = arg2.size();
            IASTAppendable result = F.ListAlloc(arg2Size);
            for (int i = 1; i < arg2Size; i++) {
              IAST positions = arg2.getAST(i);
              if (!checkPositions(ast, positions, engine)) {
                return F.NIL;
              }
              IExpr temp = extract(list, positions, engine);
              if (temp.isNIL()) {
                return F.NIL;
              }
              if (arg3.isPresent()) {
                result.append(F.unaryAST1(arg3, temp));
              } else {
                result.append(temp);
              }
            }
            return result;

          }
          if (arg2.isEmptyList()) {
            return F.CEmptyList;
          }
          if (!checkPositions(ast, arg2, engine)) {
            return F.NIL;
          }
          return extract(list, arg2, engine);
        }
      }
      return F.NIL;
    }

    private boolean checkPositions(IAST ast, IAST positions, EvalEngine engine) {
      for (int j = 1; j < positions.size(); j++) {
        IExpr arg = positions.get(j);
        if (arg.isAST(S.Key)) {
          continue;
        }
        int intValue = arg.toIntDefault();
        if (F.isNotPresent(intValue)) {
          // Position specification `1` in `2` is not applicable.
          Errors.printMessage(ast.topHead(), "psl1", F.list(ast.arg2(), ast), engine);
          return false;
        }
      }
      return true;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3_1;
    }

    private static IExpr extract(final IAST list, final IAST position, EvalEngine engine) {
      IASTAppendable part = F.Part(position.argSize(), list);
      part.appendAll(position, 1, position.size());
      return engine.evaluate(part);
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.NHOLDREST);
    }

    /**
     * Traverse all <code>list</code> element's and filter out the elements in the given <code>
     * positions</code> list.
     *
     * @param list
     * @param positions
     * @param headOffset
     * @return
     */
    // private static IExpr extract(final IAST list, final IAST positions, int headOffset) {
    // int p = 0;
    // IAST temp = list;
    // if (temp.isNIL()) {
    // return F.NIL;
    // }
    // int posSize = positions.argSize();
    // IExpr expr = list;
    // for (int i = headOffset; i <= posSize; i++) {
    // p = positions.get(i).toIntDefault(); // positionConverter.toInt(positions.get(i));
    // if (p >= 0) {
    // if (temp.size() <= p) {
    // return F.NIL;
    // }
    // expr = temp.get(p);
    // if (expr.isASTOrAssociation()) {
    // temp = (IAST) expr;
    // } else {
    // if (i < positions.size()) {
    // temp = F.NIL;
    // }
    // }
    // } else if (positions.get(i).isKey()) {
    // expr = temp.get(p);
    // if (expr.isASTOrAssociation()) {
    // temp = (IAST) expr;
    // } else {
    // if (i < positions.size()) {
    // temp = F.NIL;
    // }
    // }
    // }
    // }
    // return expr;
    // }
  }

  /**
   *
   *
   * <pre>
   * First(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the first element in <code>expr</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <p>
   * <code>First(expr)</code> is equivalent to <code>expr[[1]]</code>.
   *
   * <pre>
   * &gt;&gt; First({a, b, c})
   * a
   *
   * &gt;&gt; First(a + b + c)
   * a
   * </pre>
   *
   * <p>
   * Nonatomic expression expected.
   *
   * <pre>
   * &gt;&gt; First(x)
   * First(x)
   * </pre>
   */
  private static final class First extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr first = ast.arg1().first();
      if (first.isPresent()) {
        return first;
      }
      if (ast.isAST2()) {
        // default value
        return ast.arg2();
      }
      if (ast.arg1().size() == 1) {
        // `1` has zero length and no first element.
        return Errors.printMessage(ast.topHead(), "nofirst", F.list(ast.arg1()), engine);
      }
      // Nonatomic expression expected at position `1` in `2`.
      return Errors.printMessage(ast.topHead(), "normal", F.list(F.C1, ast), engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDREST);
    }
  }

  private static final class FirstCase extends AbstractFunctionOptionEvaluator {

    private static class FirstCasePatternMatcherFunctor implements Function<IExpr, IExpr> {
      protected final IPatternMatcher matcher;

      /** @param matcher the pattern-matcher */
      public FirstCasePatternMatcherFunctor(final IPatternMatcher matcher) {
        this.matcher = matcher;
      }

      @Override
      public IExpr apply(final IExpr arg) throws AbortException {
        if (matcher.test(arg)) {
          throw new ResultException(arg);
        }
        return F.NIL;
      }
    }

    private static class FirstCaseRulesFunctor implements Function<IExpr, IExpr> {
      protected final Function<IExpr, IExpr> function;

      /** @param function the function which should determine the results */
      public FirstCaseRulesFunctor(final Function<IExpr, IExpr> function) {
        this.function = function;
      }

      @Override
      public IExpr apply(final IExpr arg) throws AbortException {
        IExpr temp = function.apply(arg);
        if (temp.isPresent()) {
          throw new ResultException(temp);
        }
        return F.NIL;
      }
    }

    @Override
    public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] option,
        final EvalEngine engine, IAST originalAST) {

      boolean includeHeads = option[0].isTrue();

      try {
        IExpr defaultValue = F.CMissingNotFound;
        if (argSize >= 2 && argSize <= 4) {
          final IExpr arg1 = ast.arg1();
          if (arg1.isASTOrAssociation()) {
            final IExpr arg2 = engine.evalPattern(ast.arg2());
            if (argSize == 3 || argSize == 4) {
              defaultValue = ast.arg3();
              IExpr levelValue = F.CListC1;
              if (argSize == 4) {
                levelValue = engine.evaluate(ast.arg4());
              }

              if (arg2.isRuleAST()) {
                Function<IExpr, IExpr> function = Functors.rules(arg2, engine);
                FirstCaseRulesFunctor fcrf = new FirstCaseRulesFunctor(function);
                VisitorLevelSpecification level =
                    new VisitorLevelSpecification(fcrf, levelValue, includeHeads, engine);
                arg1.accept(level);
              } else {
                final IPatternMatcher matcher = engine.evalPatternMatcher(arg2);
                matcher.throwExceptionArgIfMatched(true);
                FirstCasePatternMatcherFunctor cpmf = new FirstCasePatternMatcherFunctor(matcher);
                VisitorLevelSpecification level =
                    new VisitorLevelSpecification(cpmf, levelValue, includeHeads, engine);
                arg1.accept(level);
              }
              return defaultValue;
            } else {
              return firstCase((IAST) arg1, arg2, defaultValue, includeHeads, engine);
            }
          }
          return defaultValue;
        }
      } catch (ResultException frex) {
        // we get the result with this exception
        return frex.getValue();
      } catch (final ValidateException ve) {
        Errors.printMessage(ast.topHead(), ve, engine);
      } catch (final RuntimeException rex) {
        Errors.printMessage(S.FirstCase, rex, engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_4_1;
    }

    private static IExpr firstCase(final IAST list, final IExpr pattern, IExpr defaultValue,
        boolean heads, EvalEngine engine) {
      if (pattern.isRuleAST()) {
        Function<IExpr, IExpr> function = Functors.rules(pattern, engine);
        IExpr[] result = new IExpr[] {F.NIL};
        int index = list.indexOf(x -> ruleEval(x, function, result));
        if (index > 0 && result[0].isPresent()) {
          return result[0];
        }
        return defaultValue;
      }
      final IPatternMatcher matcher = engine.evalPatternMatcher(pattern);
      int index = list.indexOf(matcher, heads ? 0 : 1);
      if (index >= 0) {
        return list.get(index);
      }
      return defaultValue;
    }

    private static boolean ruleEval(IExpr x, Function<IExpr, IExpr> function, IExpr[] result) {
      result[0] = function.apply(x);
      return result[0].isPresent();
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDREST);
      setOptions(newSymbol, S.Heads, S.False);
    }
  }

  private static final class FirstPosition extends AbstractFunctionOptionEvaluator {

    private static class RecursionData {
      final LevelSpec level;
      final Predicate<? super IExpr> matcher;
      final PositionConverter positionConverter;
      int headOffset;

      private RecursionData(final LevelSpec level, final Predicate<? super IExpr> matcher,
          final PositionConverter positionConverter, int headOffset) {
        this.level = level;
        this.matcher = matcher;
        this.positionConverter = positionConverter;
        this.headOffset = headOffset;
      }

      /**
       * Throw an exception with the result of the first positions where the matching expression
       * appears in <code>list</code>. The <code>positionConverter</code> converts the <code>int
       * </code> position into a result object.
       *
       * @param ast
       */
      private void positionRecursive(final IAST ast, IAST prototypeList) {
        int minDepth = 0;
        level.incCurrentLevel();
        IASTAppendable clone = null;
        final int size = ast.size();
        for (int i = headOffset; i < size; i++) {
          if (ast.get(i).isASTOrAssociation()) {
            clone = prototypeList.copyAppendable(1);
            if (ast.isAssociation()) {
              clone.append(((IAssociation) ast).getKey(i));
            } else {
              clone.append(positionConverter.toObject(i));
            }
            positionRecursive((IAST) ast.get(i), clone);
            if (level.getCurrentDepth() < minDepth) {
              minDepth = level.getCurrentDepth();
            }
          }
          if (matcher.test(ast.get(i))) {
            if (level.isInRange()) {
              clone = prototypeList.copyAppendable(1);
              if (ast.isAssociation() && i > 0) {
                clone.append(((IAssociation) ast).getKey(i));
              } else {
                clone.append(positionConverter.toObject(i));
              }
              throw new ResultException(clone);
            }
          }
        }
        level.setCurrentDepth(--minDepth);
        level.decCurrentLevel();
      }
    }

    /**
     * @param ast
     * @param pattern
     * @param level
     * @param engine
     */
    private static void position(final IAST ast, final IExpr pattern, final LevelSpec level,
        EvalEngine engine) {
      final IPatternMatcher matcher = engine.evalPatternMatcher(pattern);
      final PositionConverter positionConverter = new PositionConverter();

      final IAST cloneList = F.CEmptyList;
      int headOffset = 1;
      if (level.isIncludeHeads()) {
        headOffset = 0;
      }
      RecursionData recursionData =
          new RecursionData(level, matcher, positionConverter, headOffset);
      recursionData.positionRecursive(ast, cloneList);
    }

    @Override
    public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] option,
        final EvalEngine engine, IAST originalAST) {

      boolean includeHeads = option[0].isTrue();
      if (argSize < 2) {
        return F.NIL;
      }

      final IExpr arg1 = ast.arg1();
      IExpr defaultValue = F.CMissingNotFound;
      try {
        if (arg1.isASTOrAssociation()) {
          final IExpr arg2 = engine.evalPattern(ast.arg2());
          if (argSize == 2) {
            final LevelSpec level = new LevelSpec(0, Integer.MAX_VALUE, includeHeads);
            position((IAST) arg1, arg2, level, engine);
            return defaultValue;
          }

          defaultValue = ast.arg3();
          if (argSize == 3) {
            final LevelSpec level = new LevelSpec(0, Integer.MAX_VALUE, includeHeads);
            position((IAST) arg1, arg2, level, engine);
            return defaultValue;
          }
          IExpr arg4 = engine.evaluate(ast.arg4());
          final LevelSpec level = new LevelSpecification(arg4, includeHeads);
          position((IAST) arg1, arg2, level, engine);
          return defaultValue;
        }
      } catch (final ResultException frex) {
        return frex.getValue();
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_4_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDREST);
      setOptions(newSymbol, S.Heads, S.True);
    }
  }

  /**
   *
   *
   * <pre>
   * <code>Fold[f, x, {a, b}]
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns <code>f[f[x, a], b]</code>, and this nesting continues for lists of arbitrary length.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   */
  private static final class Fold extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        IExpr arg1 = ast.arg1();
        IExpr arg2 = ast.arg2();
        if (ast.isAST2()) {
          if (!arg2.isAST()) {
            // Nonatomic expression expected at position `1` in `2`.
            return Errors.printMessage(S.Fold, "normal", F.list(F.C2, ast), engine);
          }
          if (arg2.size() <= 1) {
            // an empty IAST cannot be folded
            return F.NIL;
          }
          return F.Fold(ast.arg1(), arg2.first(), arg2.rest());
        }
        IExpr temp = engine.evaluate(ast.arg3());
        if (temp.isAST()) {
          final IAST list = (IAST) temp;
          return list.foldLeft((x, y) -> F.binaryAST2(arg1, x, y), arg2, 1);
        }
      } catch (final ArithmeticException e) {

      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   *
   *
   * <pre>
   * <code>FoldList[f, x, {a, b}]
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns <code>{x, f[x, a], f[f[x, a], b]}</code>
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   */
  private static final class FoldList extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        IExpr arg2 = ast.arg2();
        if (ast.size() == 3) {
          if (!arg2.isAST()) {
            // Nonatomic expression expected at position `1` in `2`.
            return Errors.printMessage(S.FoldList, "normal", F.list(F.C2, ast), engine);
          }
          return evaluateNestList3(ast, engine);
        } else if (ast.size() == 4) {
          return evaluateNestList4(ast, engine);
        }
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        Errors.printMessage(S.FoldList, rex, engine);
      }
      return F.NIL;
    }

    private static IAST evaluateNestList3(final IAST ast, EvalEngine engine) {
      IExpr temp = ast.arg2();
      if (temp.isAST()) {
        IAST list = (IAST) temp;
        IExpr arg1 = ast.arg1();
        if (list.isEmpty() || list.size() == 2) {
          return list;
        }
        final IASTAppendable resultList = F.ast(list.head(), list.size());
        IExpr arg2 = list.arg1();
        list = list.rest();
        return foldLeft(arg2, list, 1, list.size(), (x, y) -> F.binaryAST2(arg1, x, y), resultList);
      }
      return F.NIL;
    }

    private static IAST evaluateNestList4(final IAST ast, EvalEngine engine) {

      IExpr temp = ast.arg3();
      if (temp.isAST()) {
        final IAST list = (IAST) temp;
        IExpr arg1 = ast.arg1();
        IExpr arg2 = ast.arg2();
        if (list.isEmpty()) {
          return F.unaryAST1(list.head(), arg2);
        }
        final IASTAppendable resultList = F.ast(list.head(), list.size());
        return foldLeft(arg2, list, 1, list.size(), (x, y) -> F.binaryAST2(arg1, x, y), resultList);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   *
   *
   * <pre>
   * <code>Gather(list, test)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * gathers leaves of <code>list</code> into sub lists of items that are the same according to
   * <code>test</code>.
   *
   * </blockquote>
   *
   * <pre>
   * <code>Gather(list)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * gathers leaves of <code>list</code> into sub lists of items that are the same.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <p>
   * The order of the items inside the sub lists is the same as in the original list.
   *
   * <pre>
   * <code>&gt;&gt; Gather({1, 7, 3, 7, 2, 3, 9})
   * {{1},{7,7},{3,3},{2},{9}}
   *
   * &gt;&gt; Gather({1/3, 2/6, 1/9})
   * {{1/3,1/3},{1/9}}
   * </code>
   * </pre>
   */
  private static final class Gather extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int size = ast.size();
      if (ast.arg1().isAST()) {
        IAST arg1AST = (IAST) ast.arg1();
        final DefaultDict<IASTAppendable> defaultdict;
        if (size > 2) {
          IExpr arg2 = ast.arg2();
          defaultdict = new DefaultDict<IASTAppendable>(
              new TreeMap<IExpr, IExpr>(Comparators.binaryPredicateComparator(arg2)),
              () -> F.ListAlloc());
        } else {
          defaultdict =
              new DefaultDict<IASTAppendable>(new TreeMap<IExpr, IExpr>(), () -> F.ListAlloc());
        }
        IASTAppendable result = F.ListAlloc(arg1AST.size());
        for (int i = 1; i < arg1AST.size(); i++) {
          IExpr arg = arg1AST.get(i);
          IASTAppendable subResult = defaultdict.getValue(arg);
          if (subResult.isEmpty()) {
            result.append(subResult);
          }
          subResult.append(arg);
        }
        return result;
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   *
   *
   * <pre>
   * <code>GatherBy(list, f)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * gathers leaves of <code>list</code> into sub lists of items whose image under <code>f</code>
   * identical.
   *
   * </blockquote>
   *
   * <pre>
   * <code>GatherBy(list, {f, g,...})
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * gathers leaves of <code>list</code> into sub lists of items whose image under <code>f</code>
   * identical. Then, gathers these sub lists again into sub sub lists, that are identical under
   * <code>g</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; GatherBy({{1, 3}, {2, 2}, {1, 1}}, Total)
   * {{{1,3},{2,2}},{{1,1}}}
   *
   * &gt;&gt; GatherBy({&quot;xy&quot;, &quot;abc&quot;, &quot;ab&quot;}, StringLength)
   * {{xy,ab},{abc}}
   *
   * &gt;&gt; GatherBy({{2, 0}, {1, 5}, {1, 0}}, Last)
   * {{{2,0},{1,0}},{{1,5}}}
   *
   * &gt;&gt; GatherBy({{1, 2}, {2, 1}, {3, 5}, {5, 1}, {2, 2, 2}}, {Total, Length})
   * {{{{1,2},{2,1}}},{{{3,5}}},{{{5,1}},{{2,2,2}}}}
   * </code>
   * </pre>
   */
  private static final class GatherBy extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.arg1().isList()) {
        return Errors.printMessage(ast.topHead(), "list", F.CEmptyList, engine);
      }
      IAST list1 = (IAST) ast.arg1();
      if (ast.isAST1()) {
        return F.GatherBy(list1, S.Identity);
      }
      IExpr arg2 = ast.arg2();
      if (arg2.isList()) {
        final IAST list2 = (IAST) arg2;
        final int size2 = list2.argSize();
        switch (size2) {
          case 0:
            return F.GatherBy(ast.arg1(), S.Identity);
          case 1:
            return F.GatherBy(ast.arg1(), list2.arg1());
          case 2:
            return F.Map(F.Function(F.GatherBy(F.Slot1, list2.arg2())),
                F.GatherBy(list1, list2.arg1()));
        }
        IAST r = list2.copyUntil(size2);
        IExpr f = list2.last();
        // GatherBy(l_, {r__, f_}) := Map(GatherBy(#, f)&, GatherBy(l, {r}), {Length({r})})
        return F.Map(F.Function(F.GatherBy(F.Slot1, f)), F.GatherBy(list1, r),
            F.list(F.ZZ(r.argSize())));
      }
      DefaultDict<IASTAppendable> defaultdict =
          new DefaultDict<IASTAppendable>(new TreeMap<IExpr, IExpr>(), () -> F.ListAlloc());
      IASTAppendable result = F.ListAlloc(F.allocMin8(list1.size()));
      for (int i = 1; i < list1.size(); i++) {
        IExpr list1Element = list1.get(i);
        IExpr temp = engine.evaluate(F.unaryAST1(arg2, list1Element));
        IASTAppendable subResult = defaultdict.getValue(temp);
        if (subResult.isEmpty()) {
          result.append(subResult);
        }
        subResult.append(list1Element);
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static final class GroupBy extends AbstractEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.size() >= 3) {
        IExpr arg1 = ast.arg1();
        IExpr arg2 = ast.arg2();
        IAST list2 = arg2.makeList();
        if (list2.isEmptyList()) {
          return arg1;
        }

        if (arg1.isListOrAssociation()) {
          return recurseGroupBy((IAST) arg1, list2, 1, ast, engine);
        } else if (arg1.isDataset()) {
          List<String> listOfStrings = Convert.toStringList(arg2);
          if (listOfStrings != null) {
            return ((IASTDataset) arg1).groupBy(listOfStrings);
          }
        }
      }
      return F.NIL;
    }

    /**
     * @param list first argument of GroupBy
     * @param listOfHeads second argument of GroupBy
     * @param positionOfHeads the current position in <code>listOfHeads</code>
     * @param ast
     * @param engine
     * @return
     */
    private IExpr recurseGroupBy(IAST list, IAST listOfHeads, int positionOfHeads, IAST ast,
        EvalEngine engine) {
      IExpr arg = listOfHeads.get(positionOfHeads);
      IExpr rule;
      IExpr first = arg;
      IExpr last = F.NIL;
      if (arg.isRuleAST()) {
        first = arg.first();
        last = arg.second();
      }

      Map<IExpr, IASTAppendable> map = new TreeMap<IExpr, IASTAppendable>();
      for (int i = 1; i < list.size(); i++) {
        arg = list.get(i);
        rule = list.getRule(i);
        IExpr group = engine.evaluate(F.unaryAST1(first, arg));
        IASTAppendable rhs = map.get(group);
        if (rhs == null) {
          rhs = list.copyHead(F.allocMin32(list));
          map.put(group, rhs);
        }
        if (last.isPresent()) {
          IExpr temp = engine.evaluate(F.unaryAST1(last, arg));
          if (rhs.isAssociation() || rhs.head().equals(S.Association)) {
            rhs.appendRule(F.Rule(rule.first(), temp));
          } else {
            rhs.append(temp);
          }
        } else {
          rhs.appendRule(rule);
        }
      }

      positionOfHeads++;
      IAssociation result = F.assoc();
      IExpr reduce = (ast.isAST3() && positionOfHeads >= listOfHeads.size()) ? ast.arg3() : F.NIL;
      for (Map.Entry<IExpr, IASTAppendable> entry : map.entrySet()) {
        IExpr temp;
        if (reduce.isPresent()) {
          temp = engine.evaluate(F.unaryAST1(reduce, entry.getValue()));
        } else {
          temp = engine.evaluate(entry.getValue());
        }

        if (positionOfHeads < listOfHeads.size()) {
          // recurse with next argument in listOfHeads
          if (temp.isListOrAssociation()) {
            temp = recurseGroupBy((IAST) temp, listOfHeads, positionOfHeads, ast, engine);
            if (temp.isPresent()) {
              result.appendRule(F.Rule(entry.getKey(), temp));
              continue;
            }
          }
          return F.NIL;
        }

        result.appendRule(F.Rule(entry.getKey(), temp));
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   *
   *
   * <pre>
   * Intersection(set1, set2, ...)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * get the intersection set from <code>set1</code> and <code>set2</code> &hellip;.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="http://en.wikipedia.org/wiki/Intersection_(set_theory)">Wikipedia - Intersection
   * (set theory)</a>
   * </ul>
   */
  private static final class Intersection extends AbstractFunctionOptionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, final int argSize, final IExpr[] option,
        final EvalEngine engine, IAST originalAST) {
      if (argSize > 0) {
        final BiPredicate<IExpr, IExpr> test = Predicates.sameTest(option[0], engine);
        SameTestComparator sameTest = new Comparators.SameTestComparator(test);
        if (argSize == 1) {
          if (ast.arg1().isASTOrAssociation()) {
            IAST arg1 = (IAST) ast.arg1();
            arg1 = EvalAttributes.copySort(arg1);
            Set<IExpr> set = arg1.asSortedSet(sameTest);
            if (set != null) {
              final IASTMutable result = F.ListAlloc(set);
              // EvalAttributes.sort(result, Comparators.CANONICAL_COMPARATOR);
              return result;
            }
          }
          return F.NIL;
        }

        if (ast.arg1().isASTOrAssociation()) {
          IAST result = ((IAST) ast.arg1());
          if (ast.exists(x -> !x.isASTOrAssociation(), 2)) {
            return F.NIL;
          }
          IExpr head1 = result.head();
          for (int i = 2; i < argSize + 1; i++) {
            IAST expr = (IAST) ast.get(i);
            if (!expr.head().equals(head1)) {
              // Heads `1` and `2` at positions `3` and `4` are expected to be the same.
              return Errors.printMessage(S.Intersection, "heads2",
                  F.List(expr.head(), head1, F.ZZ(i), F.C1), engine);
            }

            if (option[0].equals(S.Automatic)) {
              result = intersection(head1, result, expr);
            } else {
              result = intersection(head1, result, expr, sameTest);
            }
          }
          if (result.size() > 2) {
            EvalAttributes.sort((IASTMutable) result, Comparators.CANONICAL_COMPARATOR);
          }
          return result;
        }
      }
      return F.NIL;
    }



    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.FLAT | ISymbol.ONEIDENTITY);
      setOptions(newSymbol, S.SameTest, S.Automatic);
    }
  }

  private static class Insert extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.isAST3()) {
        IExpr arg1 = ast.arg1();
        IExpr arg2 = ast.arg2();
        IExpr arg3 = ast.arg3();

        if (arg1.isASTOrAssociation()) {
          try {
            IAST ast1 = (IAST) arg1;
            if (arg3.isInteger() || arg3.isString() || arg3.isKey()) {
              // `All` and `Span` cannot be used in Delete and Insert
              arg3 = arg3.makeList();
            }
            if (arg3.isListOfLists()) {
              IAST listOfLists = ((IAST) arg3);
              return InsertPositions.insertListOfPositions(ast1, arg2, listOfLists);
            } else if (arg3.isList()) {
              IExpr temp = InsertPositions.insertPositions(ast1, arg2, (IAST) arg3);
              if (temp.isPresent()) {
                return temp;
              }
              return ast1;
            }
          } catch (final ValidateException ve) {
            return Errors.printMessage(ast.topHead(), ve, engine);
          } catch (final ArgumentTypeStopException atse) {
            Errors.printMessage(ast.topHead(), atse, engine);
            return arg1;
          } catch (RuntimeException rex) {
            if (Config.DEBUG) {
              rex.printStackTrace();
            }
            Errors.rethrowsInterruptException(rex);
            return Errors.printMessage(S.Delete, rex, engine);
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3_1;
    }
  }

  /**
   *
   *
   * <pre>
   * Join(l1, l2)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * concatenates the lists <code>l1</code> and <code>l2</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <p>
   * <code>Join</code> concatenates lists:
   *
   * <pre>
   * &gt;&gt; Join({a, b}, {c, d, e})
   * {a,b,c,d,e}
   *
   * &gt;&gt; Join({{a, b}, {c, d}}, {{1, 2}, {3, 4}})
   * {{a,b},{c,d},{1,2},{3,4}}
   * </pre>
   *
   * <p>
   * The concatenated expressions may have any head:
   *
   * <pre>
   * &gt;&gt; Join(a + b, c + d, e + f)
   * a+b+c+d+e+f
   * </pre>
   *
   * <p>
   * However, it must be the same for all expressions:
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
  private static final class Join extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int index = ast.indexOf(x -> x.isAtom() && !x.isAssociation() && !x.isSparseArray());
      if (index > 0) {
        // Nonatomic expression expected at position `1` in `2`.
        return Errors.printMessage(ast.topHead(), "normal", F.list(F.ZZ(index), ast), engine);
      }
      if (ast.size() == 2) {
        return ast.arg1();
      }

      int astSize = ast.size();
      int size = 0;
      IExpr head = null;
      IAST temp;
      boolean isAssociation = false;
      boolean isSparseArray = false;
      boolean useNormal = false;
      for (int i = 1; i < astSize; i++) {
        IExpr arg = ast.get(i);
        if (arg.isSparseArray()) {
          isSparseArray = true;
          if (head == S.List || useNormal) {
            useNormal = true;
            continue;
          }
          if (i > 1 && !isSparseArray) {
            // incompatible elements in `1` cannot be joined.
            return Errors.printMessage(ast.topHead(), "incpt", F.list(ast), engine);
          }
          continue;
        }

        useNormal = true;
        temp = (IAST) arg;
        size += temp.argSize();
        if (head == null) {
          head = temp.head();
          isAssociation = temp.isAssociation();
        } else {
          if (!head.equals(temp.head())) {
            // incompatible elements in `1` cannot be joined.
            return Errors.printMessage(ast.topHead(), "incpt", F.list(ast), engine);
          }
          if (temp.isAssociation() != isAssociation) {
            // incompatible elements in `1` cannot be joined.
            return Errors.printMessage(ast.topHead(), "incpt", F.list(ast), engine);
          }
        }
      }
      if (isAssociation) {
        if (isSparseArray) {
          // incompatible elements in `1` cannot be joined.
          return Errors.printMessage(ast.topHead(), "incpt", F.list(ast), engine);
        }
        final IAssociation result = F.assoc(F.CEmptyList);
        for (int i = 1; i < ast.size(); i++) {
          IExpr arg = ast.get(i);
          result.appendRules((IAST) arg);
        }
        return result;
      }
      if (isSparseArray && !useNormal) {
        ISparseArray result = (ISparseArray) ast.arg1();
        int[] dim1 = result.getDimension();
        IExpr defaultValue1 = result.getDefaultValue();
        if (dim1.length != 2) {
          return F.NIL;
        }
        for (int i = 2; i < ast.size(); i++) {
          ISparseArray arg = (ISparseArray) ast.get(i);
          int[] dim = arg.getDimension();
          if (dim.length != dim1.length) {
            return F.NIL;
          }
          if (dim[dim.length - 1] != dim1[dim.length - 1]) {
            return F.NIL;
          }
          if (!defaultValue1.equals(arg.getDefaultValue())) {
            return F.NIL;
          }
        }
        for (int i = 2; i < ast.size(); i++) {
          ISparseArray arg = (ISparseArray) ast.get(i);
          result = result.join(arg);
        }
        return result;
      }
      final IASTAppendable result = F.ast(head, size);
      for (int i = 1; i < ast.size(); i++) {
        IExpr arg = ast.get(i);
        if (arg.isSparseArray()) {
          if (useNormal) {
            arg = arg.normal(false);
          }
        }
        result.appendArgs((IAST) arg);
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.FLAT | ISymbol.ONEIDENTITY);
    }
  }

  /**
   *
   *
   * <pre>
   * Last(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the last element in <code>expr</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <p>
   * <code>Last(expr)</code> is equivalent to <code>expr[[-1]]</code>.
   *
   * <pre>
   * &gt;&gt; Last({a, b, c})
   * c
   * </pre>
   *
   * <p>
   * Nonatomic expression expected.
   *
   * <pre>
   * &gt;&gt; Last(x)
   * Last(x)
   * </pre>
   */
  private static final class Last extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr last = ast.arg1().last();
      if (last.isPresent()) {
        return last;
      }
      if (ast.isAST2()) {
        // default value
        return ast.arg2();
      }
      if (ast.arg1().size() == 1) {
        // `1` has zero length and no last element.
        return Errors.printMessage(ast.topHead(), "nolast", F.list(ast.arg1()), engine);
      }
      // Nonatomic expression expected at position `1` in `2`.
      return Errors.printMessage(ast.topHead(), "normal", F.list(F.C1, ast), engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDREST);
    }
  }

  /**
   *
   *
   * <pre>
   * Length(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the number of leaves in <code>expr</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <p>
   * Length of a list:
   *
   * <pre>
   * &gt;&gt; Length({1, 2, 3})
   * 3
   * </pre>
   *
   * <p>
   * 'Length' operates on the 'FullForm' of expressions:
   *
   * <pre>
   * &gt;&gt; Length(Exp(x))
   * 2
   *
   * &gt;&gt; FullForm(Exp(x))
   * Power(E, x)
   * </pre>
   *
   * <p>
   * The length of atoms is 0:
   *
   * <pre>
   * &gt;&gt; Length(a)
   * 0
   * </pre>
   *
   * <p>
   * Note that rational and complex numbers are atoms, although their 'FullForm' might suggest the
   * opposite:
   *
   * <pre>
   * &gt;&gt; Length(1/3)
   * 0
   *
   * &gt;&gt; FullForm(1/3)
   * Rational(1, 3)
   * </pre>
   */
  private static final class Length extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = engine.evaluate(ast.arg1());
      return arg1.isASTOrAssociation() ? F.ZZ(arg1.argSize()) : F.C0;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static final class LengthWhile extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      if (arg1.isAST()) {
        IAST list = (IAST) arg1;
        final int[] n = new int[] {0};
        list.forAll(x -> {
          if (engine.evalTrue(arg2, x)) {
            n[0]++;
            return true;
          }
          return false;
        }, 1);
        return F.ZZ(n[0]);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   *
   *
   * <pre>
   * Level(expr, levelspec)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * gives a list of all sub-expressions of <code>expr</code> at the level(s) specified by <code>
   * levelspec</code>.
   *
   * </blockquote>
   *
   * <p>
   * Level uses standard level specifications:
   *
   * <pre>
   * n
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * levels <code>1</code> through <code>n</code>
   *
   * </blockquote>
   *
   * <pre>
   * Infinity
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * all levels from level <code>1</code>
   *
   * </blockquote>
   *
   * <pre>
   * {n}
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * level <code>n</code> only
   *
   * </blockquote>
   *
   * <pre>
   * {m, n}
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * levels <code>m</code> through <code>n</code>
   *
   * </blockquote>
   *
   * <p>
   * Level 0 corresponds to the whole expression. A negative level <code>-n</code> consists of parts
   * with depth <code>n</code>.
   *
   * <h3>Examples</h3>
   *
   * <p>
   * Level <code>-1</code> is the set of atoms in an expression:
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
   *
   * <p>
   * Use the option <code>Heads -&gt; True</code> to include heads:
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
  private static final class Level extends AbstractFunctionOptionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] option,
        final EvalEngine engine, IAST originalAST) {

      boolean includeHeads = option[0].isTrue();

      if (ast.arg1().isASTOrAssociation()) {
        final IAST arg1 = (IAST) ast.arg1();
        int allocSize = F.allocMin32(arg1.argSize() * 8);
        IExpr head = (argSize == 3) ? ast.arg3() : S.List;
        IASTAppendable resultList = F.ast(head, allocSize);
        final VisitorLevelSpecification level = new VisitorLevelSpecification(x -> {
          resultList.append(x);
          return F.NIL;
        }, ast.arg2(), includeHeads, engine);
        arg1.accept(level);

        return resultList;
      }
      return F.CEmptyList;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, S.Heads, S.False);
    }
  }

  /**
   *
   *
   * <pre>
   * LevelQ(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * tests whether <code>expr</code> is a valid level specification.
   *
   * </blockquote>
   *
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
  private static final class LevelQ extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = engine.evaluate(ast.arg1());
      try {
        // throws MathException if Level isn't defined correctly
        new VisitorLevelSpecification(null, arg1, false, engine);
        return S.True;
      } catch (final RuntimeException rex) {
        // ArgumentTypeException from VisitorLevelSpecification level specification checks
        // return engine.printMessage("LevelQ: " + rex.getMessage());
      }
      return S.False;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   *
   *
   * <pre>
   * Most(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns <code>expr</code> with the last element removed.
   *
   * </blockquote>
   *
   * <p>
   * <code>Most(expr)</code> is equivalent to <code>expr[[;;-2]]</code>.
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Most({a, b, c})
   * {a,b}
   *
   * &gt;&gt; Most(a + b + c)
   * a+b
   * </pre>
   *
   * <p>
   * Nonatomic expression expected.
   *
   * <pre>
   * &gt;&gt; Most(x)
   * Most(x)
   * </pre>
   */
  private static final class Most extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isASTOrAssociation()) {
        if (arg1.argSize() > 0) {
          return ((IAST) arg1).most();
        }
        // Cannot take Most of expression `1` with length zero.
        return Errors.printMessage(ast.topHead(), "nomost", F.list(arg1), engine);
      }
      // Nonatomic expression expected at position `1` in `2`.
      return Errors.printMessage(ast.topHead(), "normal", F.list(F.C1, ast), engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static final class Nearest extends AbstractFunctionOptionEvaluator {
    @Override
    public IExpr evaluate(IAST ast, int argSize, IExpr[] option, EvalEngine engine,
        IAST originalAST) {
      if (ast.arg1().isASTOrAssociation()) {
        IAST listArg1 = (IAST) ast.arg1();
        if (listArg1.argSize() > 0) {
          if (argSize == 2) {
            if (ast.arg2().isNumber()) {
              INumber arg2 = (INumber) ast.arg2();
              IExpr distanceFunction;
              if (option[0] == S.Automatic) {
                // Norm() is the default distance function for numeric data
                distanceFunction = F.Function(F.Norm(F.Subtract(F.Slot1, F.Slot2)));
              } else {
                distanceFunction = F.Function(F.binary(option[0], F.Slot1, F.Slot2));
              }
              return nearest(listArg1, arg2, distanceFunction, engine);
            }

            IExpr arg2 = ast.arg2();
            IExpr distanceFunction;
            if (option[0] == S.Automatic) {
              // Norm() is the default distance function for numeric data
              distanceFunction = F.Function(F.Norm(F.Subtract(F.Slot1, F.Slot2)));
            } else {
              distanceFunction = F.Function(F.binary(option[0], F.Slot1, F.Slot2));
            }
            return nearest(listArg1, arg2, distanceFunction, engine);
          }
        }
      }


      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, S.DistanceFunction, S.Automatic);
    }

    /**
     * Gives the list of elements from <code>inputList</code> to which x is nearest.
     *
     * @param inputList
     * @param x
     * @param engine
     * @return the list of elements from <code>inputList</code> to which x is nearest
     */
    private static IAST nearest(IAST inputList, IExpr x, IExpr distanceFunction,
        EvalEngine engine) {
      try {
        IASTAppendable result = null;
        IExpr distance = F.NIL;
        IASTAppendable temp;
        for (int i = 1; i < inputList.size(); i++) {
          temp = F.ast(distanceFunction);
          temp.append(x);
          temp.append(inputList.get(i));
          if (result == null) {
            result = F.ListAlloc(8);
            result.append(inputList.get(i));
            distance = temp;
          } else {
            IExpr comparisonResult = engine.evaluate(F.Greater(distance, temp));
            if (comparisonResult.isTrue()) {
              result = F.ListAlloc(8);
              result.append(inputList.get(i));
              distance = temp;
            } else if (comparisonResult.isFalse()) {
              if (S.Equal.ofQ(engine, distance, temp)) {
                result.append(inputList.get(i));
              }
              continue;
            } else {
              // undefined
              return F.NIL;
            }
          }
        }
        return result;
      } catch (RuntimeException e) {
        Errors.rethrowsInterruptException(e);
      }
      return F.NIL;
    }
  }

  private static class NearestTo extends AbstractFunctionOptionEvaluator {

    IBuiltInSymbol operatorHead;
    IBuiltInSymbol comparatorHead;

    public NearestTo() {
      this.operatorHead = S.NearestTo;
      this.comparatorHead = S.Nearest;
    }

    @Override
    public IExpr evaluate(IAST ast, int argSize, IExpr[] option, EvalEngine engine,
        IAST originalAST) {
      if (ast.argSize() == 1) {
        IExpr head = ast.head();
        if (head.isAST(operatorHead)) {
          option = new IExpr[1];
          argSize = AbstractFunctionEvaluator.determineOptions(option, (IAST) head, head.argSize(),
              ARGS_1_1, optionSymbols, engine);


          IAST distanceFunction = F.Rule(S.DistanceFunction, option[0]);
          if (argSize == 1) {
            return F.ternaryAST3(comparatorHead, ast.arg1(), head.first(), distanceFunction);
          } else if (argSize == 2) {
            return F.quaternary(comparatorHead, ast.arg1(), head.first(), head.second(),
                distanceFunction);
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_2_0;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, S.DistanceFunction, S.Automatic);
    }
  }

  /**
   *
   *
   * <pre>
   * PadLeft(list, n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * pads <code>list</code> to length <code>n</code> by adding <code>0</code> on the left.
   *
   * </blockquote>
   *
   * <pre>
   * PadLeft(list, n, x)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * pads <code>list</code> to length <code>n</code> by adding <code>x</code> on the left.
   *
   * </blockquote>
   *
   * <pre>
   * PadLeft(list)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * turns the ragged list <code>list</code> into a regular list by adding '0' on the left.
   *
   * </blockquote>
   *
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
  private static final class PadLeft extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.arg1().isAST()) {
        // Nonatomic expression expected at position `1` in `2`.
        return Errors.printMessage(ast.topHead(), "normal", F.list(F.C1, ast), engine);
      }
      IAST list = (IAST) ast.arg1();

      if (ast.isAST1()) {
        if (list.isListOfLists()) {
          int maxSize = -1;
          for (int i = 1; i < list.size(); i++) {
            IAST subList = (IAST) list.get(i);
            if (subList.size() > maxSize) {
              maxSize = subList.size();
            }
          }
          if (maxSize > 0) {
            final int mSize = maxSize - 1;
            return F.mapRange(1, list.size(), i -> padLeftAtom(list.getAST(i), mSize, F.C0));
          }
        }
        return ast.arg1();
      }

      if (ast.argSize() > 1 && ast.arg2().isList()) {
        int[] levels = Validate.checkListOfInts(ast, ast.arg2(), true, false, engine);
        if (levels != null && levels.length > 0) {
          int listLevel = list.depth(false) - 1;
          if (levels.length > listLevel) {
            // The padding specification `1` involves `2` levels, the list `3` has only `4`
            // level.
            return Errors.printMessage(ast.topHead(), "levelpad",
                F.List(ast.arg2(), F.ZZ(levels.length), list, F.ZZ(listLevel)), engine);
          }

          IExpr defaultValue = F.C0;
          if (ast.argSize() > 2) {
            defaultValue = ast.arg3();
          }
          IASTAppendable result = list.copyHead(levels[0]);
          if (padLeftASTList(list, list.head(), (IAST) ast.arg1(), defaultValue, levels, 1,
              levels[0], result)) {
            return result;
          }
        }
        return F.NIL;
      }

      int n = Validate.checkIntType(ast, 2);
      if (ast.size() > 3) {
        if (ast.arg3().isList()) {
          IAST arg3 = (IAST) ast.arg3();
          return padLeftAST(list, n, arg3);
        } else {
          return padLeftAtom(list, n, ast.arg3());
        }
      }
      return padLeftAtom(list, n, F.C0);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    public static IExpr padLeftAtom(IAST ast, int n, IExpr atom) {
      int length = n - ast.size() + 1;
      if (length > 0) {
        long intialCapacity = (long) length + (long) ast.argSize();
        if (Config.MAX_AST_SIZE < intialCapacity) {
          ASTElementLimitExceeded.throwIt(intialCapacity);
        }
        IASTAppendable result = ast.copyHead((int) intialCapacity);
        result.appendArgs(0, length, i -> atom);
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
        long intialCapacity = (long) length + (long) ast.argSize();
        if (Config.MAX_AST_SIZE < intialCapacity) {
          ASTElementLimitExceeded.throwIt(intialCapacity);
        }
        IASTAppendable result = ast.copyHead((int) intialCapacity);
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

    private static boolean padLeftASTList(IAST originalAST, IExpr mainHead, IAST list, IExpr x,
        int[] levels, int position, int length, IASTAppendable result) {
      if (position >= levels.length) {
        int padSize = length;
        if (list.isPresent()) {
          if (length > list.argSize()) {
            padSize = length - list.argSize();
          } else {
            padSize = 0;
          }
        }

        for (int i = 0; i < padSize; i++) {
          result.append(x);
        }
        int j = 1;
        if (list.isPresent() && list.argSize() > length) {
          j = list.size() - length;
        }
        for (int i = padSize; i < length; i++) {
          result.append(list.get(j++));
        }
        return true;
      }
      int subLength = levels[position];
      position++;
      IAST subList;

      int padSize = length;
      if (list.isPresent() && length > list.argSize()) {
        padSize = length - list.size();
      }
      int j = 1;
      for (int i = 0; i < length; i++) {
        IASTAppendable subResult;
        if (i > padSize) {
          if (list.isPresent() && list.get(j).isASTOrAssociation()) {
            subList = (IAST) list.get(j++);
          } else {
            // The padding specification `1` involves `2` levels; the list `3` has only `4` level.
            throw new ArgumentTypeException(Errors.getMessage("padlevel",
                F.List(F.List(levels), F.ZZ(levels.length), originalAST, F.ZZ(position - 1)),
                EvalEngine.get()));
          }
        } else {
          subList = F.NIL;
        }
        if (subList.isPresent()) {
          subResult = subList.copyHead(subLength);
        } else {
          subResult = F.ast(mainHead, subLength);
        }
        if (!padLeftASTList(originalAST, mainHead, subList, x, levels, position, subLength,
            subResult)) {
          return false;
        }
        result.append(subResult);
      }
      return true;
    }
  }

  /**
   *
   *
   * <pre>
   * PadRight(list, n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * pads <code>list</code> to length <code>n</code> by adding <code>0</code> on the right.
   *
   * </blockquote>
   *
   * <pre>
   * PadRight(list, n, x)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * pads <code>list</code> to length <code>n</code> by adding <code>x</code> on the right.
   *
   * </blockquote>
   *
   * <pre>
   * PadRight(list)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * turns the ragged list <code>list</code> into a regular list by adding '0' on the right.
   *
   * </blockquote>
   *
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
  private static final class PadRight extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isAtom()) {
        // Nonatomic expression expected at position `1` in `2`.
        return Errors.printMessage(ast.topHead(), "normal", F.list(F.C1, ast), engine);
      }
      IAST list = (IAST) ast.arg1();

      if (ast.isAST1()) {
        if (list.isListOfLists()) {
          int maxSize = -1;
          for (int i = 1; i < list.size(); i++) {
            IAST subList = (IAST) list.get(i);
            if (subList.size() > maxSize) {
              maxSize = subList.size();
            }
          }
          if (maxSize > 0) {
            final int mSize = maxSize - 1;
            return F.mapRange(1, list.size(), i -> padRightAtom(list.getAST(i), mSize, F.C0));
          }
        }
        return ast.arg1();
      }

      if (ast.argSize() > 1 && ast.arg2().isList()) {
        int[] levels = Validate.checkListOfInts(ast, ast.arg2(), true, false, engine);
        if (levels != null && levels.length > 0) {
          int listLevel = list.depth(false) - 1;
          if (levels.length > listLevel) {
            // The padding specification `1` involves `2` levels, the list `3` has only `4`
            // level.
            return Errors.printMessage(ast.topHead(), "levelpad",
                F.List(ast.arg2(), F.ZZ(levels.length), list, F.ZZ(listLevel)), engine);
          }

          IExpr defaultValue = F.C0;
          if (ast.argSize() > 2) {
            defaultValue = ast.arg3();
          }
          IASTAppendable result = list.copyHead(levels[0]);
          if (padRightASTList(list, list.head(), (IAST) ast.arg1(), defaultValue, levels, 1,
              levels[0], result)) {
            return result;
          }
        }
        return F.NIL;
      }

      int n = Validate.checkIntType(ast, 2);

      if (ast.size() > 3) {
        if (ast.arg3().isList()) {
          IAST arg3 = (IAST) ast.arg3();
          return padRightAST(list, n, arg3);
        }
        return padRightAtom(list, n, ast.arg3());
      }
      return padRightAtom(list, n, F.C0);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    public static IExpr padRightAtom(IAST ast, int n, IExpr atom) {
      int length = n - ast.size() + 1;
      if (length > 0) {
        long intialCapacity = (long) length + (long) ast.argSize();
        if (Config.MAX_AST_SIZE < intialCapacity) {
          ASTElementLimitExceeded.throwIt(intialCapacity);
        }
        IASTAppendable result = ast.copyHead((int) intialCapacity);
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
        long intialCapacity = (long) length + (long) ast.argSize();
        if (Config.MAX_AST_SIZE < intialCapacity) {
          ASTElementLimitExceeded.throwIt(intialCapacity);
        }
        IASTAppendable result = ast.copyHead((int) intialCapacity);
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

    private static boolean padRightASTList(IAST originalAST, IExpr mainHead, IAST list, IExpr x,
        int[] levels, int position, int length, IASTAppendable result) {
      if (position >= levels.length) {
        if (list.isPresent()) {
          int astLength = list.argSize() > length ? length : list.argSize();
          if (astLength > 0) {
            for (int i = 0; i < astLength; i++) {
              result.append(list.get(i + 1));
            }
          }
          length -= astLength;
        }
        for (int i = 0; i < length; i++) {
          result.append(x);
        }
        return true;
      }
      int subLength = levels[position];
      position++;
      IAST subList;
      for (int i = 0; i < length; i++) {
        IASTAppendable subResult;
        if (i < list.argSize()) {
          if (list.isPresent() && list.get(i + 1).isASTOrAssociation()) {
            subList = (IAST) list.get(i + 1);
          } else {
            // The padding specification `1` involves `2` levels; the list `3` has only `4` level.
            throw new ArgumentTypeException(Errors.getMessage("padlevel",
                F.List(F.List(levels), F.ZZ(levels.length), originalAST, F.ZZ(position - 1)),
                EvalEngine.get()));
          }
        } else {
          subList = F.NIL;
        }
        if (subList.isPresent()) {
          subResult = subList.copyHead(subLength);
        } else {
          subResult = F.ast(mainHead, subLength);
        }
        if (!padRightASTList(originalAST, mainHead, subList, x, levels, position, subLength,
            subResult)) {
          return false;
        }
        result.append(subResult);
      }
      return true;
    }
  }

  /**
   *
   *
   * <pre>
   * <code>Pick(nestedList, nestedSelection)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the elements of <code>nestedList</code> that have value <code>True</code> in the
   * corresponding position in <code>nestedSelection</code>.
   *
   * </blockquote>
   *
   * <pre>
   * <code>Pick(nestedList, nestedSelection, pattern)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the elements of <code>nestedList</code> those values in the corresponding position in
   * <code>nestedSelection</code> match the <code>pattern</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; Pick({{1, 2}, {2, 3}, {5, 6}}, {{1}, {2, 3}, {{3, 4}, {4, 5}}}, {1} | 2 | {4, 5})
   * {{1,2},{2},{6}}
   * </code>
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="Cases.md">Cases</a>, <a href="Select.md">Select</a>
   */
  private static final class Pick extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr list = ast.arg1();
      IExpr selection = ast.arg2();
      IExpr pattern = S.True;
      if (ast.isAST3()) {
        pattern = ast.arg3();
      }
      final IPatternMatcher matcher = engine.evalPatternMatcher(pattern);
      if (matcher.test(selection)) {
        return list;
      }
      if ((list.isASTOrAssociation() || list.isSparseArray())
          && (selection.isASTOrAssociation() || selection.isSparseArray())) {
        // TODO optimize for SparseArray
        IAST arg1AST = (IAST) list.normal(false);
        IAST arg2AST = (IAST) selection.normal(false);
        if (arg1AST.size() != arg2AST.size()) {
          // Expressions `1` and `2` have incompatible shapes.
          return Errors.printMessage(ast.topHead(), "incomp", F.list(list, selection), engine);
        }
        try {
          IASTAppendable result = arg1AST.copyHead();
          IExpr temp = pickRecursive(arg1AST, arg2AST, matcher, result);
          return temp;
        } catch (AbortException aex) {
          // Expressions `1` and `2` have incompatible shapes.
          return Errors.printMessage(ast.topHead(), "incomp", F.list(list, selection), engine);
        }
      }
      return F.CEmptySequence;
    }

    /**
     * Return the elements of <code>list</code> those values in the corresponding position in <code>
     * selection</code> match with <code>matcher</code>. See:
     * <a href="https://mathematica.stackexchange.com/a/119235/21734">Stackexchange - The Pick
     * Process</a>
     *
     * @param list
     * @param selection
     * @param matcher
     * @param result append the matched elements to this <code>IASTAppendable</code>
     * @return the elements of <code>list</code> those values in the corresponding position in
     *         <code>selection</code> match with <code>matcher</code>.
     */
    private static IExpr pickRecursive(IAST list, IAST selection, IPatternMatcher matcher,
        IASTAppendable result) {
      for (int i = 1; i < list.size(); i++) {
        IExpr arg1 = list.getRule(i);
        IExpr arg2 = selection.getRule(i);
        if (matcher.test(arg2)) {
          result.append(arg1);
        } else if (arg1.isASTOrAssociation() && arg2.isASTOrAssociation()) {
          if (arg1.size() != arg2.size()) {
            throw AbortException.ABORTED;
          }
          IASTAppendable appendable = ((IAST) arg1).copyHead();
          IExpr temp = pickRecursive((IAST) arg1, (IAST) arg2, matcher, appendable);
          result.append(temp);
        }
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }
  }

  /**
   *
   *
   * <pre>
   * Position(expr, patt)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the list of positions for which <code>expr</code> matches <code>patt</code>.
   *
   * </blockquote>
   *
   * <pre>
   * Position(expr, patt, ls)
   * &gt; returns the positions on levels specified by levelspec `ls`.
   *
   * ### Examples
   * </pre>
   *
   * <blockquote>
   *
   * <blockquote>
   *
   * <p>
   * Position({1, 2, 2, 1, 2, 3, 2}, 2) {{2},{3},{5},{7}}
   *
   * </blockquote>
   *
   * </blockquote>
   *
   * <p>
   * Find positions upto 3 levels deep
   *
   * <pre>
   * &gt;&gt; Position({1 + Sin(x), x, (Tan(x) - y)^2}, x, 3)
   * {{1,2,1},{2}}
   * </pre>
   *
   * <p>
   * Find all powers of x
   *
   * <pre>
   * &gt;&gt; Position({1 + x^2, x y ^ 2,  4 y,  x ^ z}, x^_)
   * {{1,2},{4}}
   * </pre>
   *
   * <p>
   * Use Position as an operator
   *
   * <pre>
   * &gt;&gt; Position(_Integer)({1.5, 2, 2.5})
   * {{2}}
   * </pre>
   */
  private static final class Position extends AbstractFunctionEvaluator {

    private static class RecursionData {
      final IASTAppendable resultCollection;
      final int maxResults;
      final LevelSpec level;
      final Predicate<? super IExpr> matcher;
      final PositionConverter positionConverter;
      final int headOffset;

      /**
       * @param resultCollection
       * @param maxResults the maximum number of results which should be returned in <code>
       *     resultCollection</code>
       * @param level
       * @param matcher
       * @param positionConverter
       * @param headOffset
       */
      private RecursionData(final IASTAppendable resultCollection, int maxResults,
          final LevelSpec level, final Predicate<? super IExpr> matcher,
          final PositionConverter positionConverter, int headOffset) {
        this.resultCollection = resultCollection;
        this.maxResults = maxResults;
        this.level = level;
        this.matcher = matcher;
        this.positionConverter = positionConverter;
        this.headOffset = headOffset;
      }

      /**
       * Add the positions to the <code>resultCollection</code> where the matching expressions
       * appear in <code>list</code>. The <code>positionConverter</code> converts the <code>int
       * </code> position into an object for the <code>resultCollection</code>.
       *
       * @param ast
       * @param prototypeList
       * @return
       */
      private IAST positionRecursive(final IAST ast, final IAST prototypeList) {
        int minDepth = 0;
        level.incCurrentLevel();
        IASTAppendable clone = null;
        final int size = ast.size();
        for (int i = headOffset; i < size; i++) {
          IExpr arg = ast.get(i);
          if (arg.isASTOrAssociation()) {
            // clone = (INestedList<IExpr>) prototypeList.clone();
            clone = prototypeList.copyAppendable(1);
            if (ast.isAssociation()) {
              clone.append(((IAssociation) ast).getKey(i));
            } else {
              clone.append(positionConverter.toObject(i));
            }
            positionRecursive((IAST) arg, clone);
            if (level.getCurrentDepth() < minDepth) {
              minDepth = level.getCurrentDepth();
            }
          }
          if (matcher.test(arg)) {
            if (level.isInRange()) {
              clone = prototypeList.copyAppendable(1);
              if (ast.isAssociation() && i > 0) {
                clone.append(((IAssociation) ast).getKey(i));
              } else {
                clone.append(positionConverter.toObject(i));
              }
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
    }

    /**
     * @param expr
     * @param pattern
     * @param level
     * @param maxResults the maximum number of results which should be returned in the resulting
     *        <code>List</code>
     * @param engine
     * @return a <code>F.list()</code> of result positions
     */
    private static IAST position(final IExpr expr, final IExpr pattern, final LevelSpec level,
        int maxResults, EvalEngine engine) {
      final IPatternMatcher matcher = engine.evalPatternMatcher(pattern);

      if (expr.isASTOrAssociation()) {
        final PositionConverter positionConverter = new PositionConverter();
        final IAST ast = (IAST) expr;
        final IAST cloneList = F.CEmptyList;
        final IASTAppendable resultList = F.ListAlloc(F.allocMax32(ast));
        int headOffset = 1;
        if (level.isIncludeHeads()) {
          headOffset = 0;
        }
        RecursionData recursionData = new RecursionData(resultList, maxResults, level, matcher,
            positionConverter, headOffset);
        recursionData.positionRecursive(ast, cloneList);
        return resultList;
      }
      if (matcher.test(expr)) {
        if (level.isInRange()) {
          return F.List(F.CEmptyList);
        }
      }
      return F.CEmptyList;
    }

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.size() < 3) {
        return F.NIL;
      }

      int maxResults = Integer.MAX_VALUE;
      if (ast.size() >= 5) {
        maxResults = engine.evaluate(ast.arg4()).toIntDefault();
        if (maxResults < 0) {
          if (ast.arg4().isInfinity()) {
            maxResults = Integer.MAX_VALUE;
          } else {
            // Non-negative integer or Infinity expected at position `1` in `2`.
            return Errors.printMessage(S.Position, "innf", F.List(F.C4, ast), engine);
          }
        }
      }
      final IExpr arg1 = ast.arg1();
      final IExpr arg2 = engine.evalPattern(ast.arg2());
      if (ast.isAST2()) {
        final LevelSpec level = new LevelSpec(0, Integer.MAX_VALUE);
        return position(arg1, arg2, level, Integer.MAX_VALUE, engine);
      }
      if (ast.size() >= 4) {
        IExpr option = S.True;
        final OptionArgs options = OptionArgs.createOptionArgs(ast, engine);
        if (options != null) {
          option = options.getOption(S.Heads);
          if (option.isPresent()) {
            if (option.isTrue()) {
              final LevelSpec level = new LevelSpec(0, Integer.MAX_VALUE, true);
              return position(arg1, arg2, level, Integer.MAX_VALUE, engine);
            }
            if (option.isFalse()) {
              final LevelSpec level = new LevelSpec(0, Integer.MAX_VALUE, false);
              return position(arg1, arg2, level, maxResults, engine);
            }
            return F.NIL;
          }
        }

        final IExpr arg3 = engine.evaluate(ast.arg3());
        final LevelSpec level = new LevelSpecification(arg3, true);
        return position(arg1, arg2, level, maxResults, engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDREST);
      setOptions(newSymbol, //
          F.list(F.Rule(S.Heads, S.True)));
    }
  }

  /**
   *
   *
   * <pre>
   * Prepend(expr, item)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns <code>expr</code> with <code>item</code> prepended to its leaves.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <p>
   * <code>Prepend</code> is similar to <code>Append</code>, but adds <code>item</code> to the
   * beginning of <code>expr</code>:
   *
   * <pre>
   * &gt;&gt; Prepend({2, 3, 4}, 1)
   * {1,2,3,4}
   * </pre>
   *
   * <p>
   * <code>Prepend</code> works on expressions with heads other than 'List':<br>
   *
   * <pre>
   * &gt;&gt; Prepend(f(b, c), a)
   * f(a,b,c)
   * </pre>
   *
   * <p>
   * Unlike <code>Join</code>, <code>Prepend</code> does not flatten lists in <code>item</code>:
   * <br>
   *
   * <pre>
   * &gt;&gt; Prepend({c, d}, {a, b})
   * {{a,b},c,d}
   * </pre>
   *
   * <p>
   * Nonatomic expression expected.<br>
   *
   * <pre>
   * &gt;&gt; Prepend(a, b)
   * Prepend(a,b)
   * </pre>
   */
  private static final class Prepend extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr arg1 = engine.evaluate(ast.arg1());
      IAST arg1AST = Validate.checkASTOrAssociationType(ast, arg1, 1, engine);
      if (arg1AST.isNIL()) {
        return F.NIL;
      }
      IExpr arg2 = engine.evaluate(ast.arg2());
      if (arg1.isAssociation()) {
        if (arg2.isRuleAST() || arg2.isListOfRules() || arg2.isAssociation()) {
          IAssociation result = ((IAssociation) arg1).copy();
          result.prependRules((IAST) arg2);
          return result;
        } else {
          // The argument is not a rule or a list of rules.
          return Errors.printMessage(ast.topHead(), "invdt", F.CEmptyList, EvalEngine.get());
        }
      }
      return arg1AST.appendAtClone(1, arg2);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2_1;
    }
  }

  /**
   *
   *
   * <pre>
   * PrependTo(s, item)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * prepend <code>item</code> to value of <code>s</code> and sets <code>s</code> to the result.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * Assign s to a list
   * &gt;&gt; s = {1, 2, 4, 9}
   * {1,2,4,9}
   * </pre>
   *
   * <p>
   * Add a new value at the beginning of the list:<br>
   *
   * <pre>
   * &gt;&gt; PrependTo(s, 0)
   * {0,1,2,4,9}
   * </pre>
   *
   * <p>
   * The value assigned to s has changed:<br>
   *
   * <pre>
   * &gt;&gt; s
   * {0,1,2,4,9}
   * </pre>
   *
   * <p>
   * 'PrependTo' works with a head other than 'List':
   *
   * <pre>
   * &gt;&gt; y = f(a, b, c)
   * &gt;&gt; PrependTo(y, x)
   * f(x,a,b,c)
   *
   * &gt;&gt; y
   * f(x,a,b,c)
   * </pre>
   *
   * <p>
   * {a, b} is not a variable with a value, so its value cannot be changed.
   *
   * <pre>
   * &gt;&gt; PrependTo({a, b}, 1)
   * PrependTo({a,b},1)
   * </pre>
   *
   * <p>
   * a is not a variable with a value, so its value cannot be changed.
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
   *
   * <p>
   * Nonatomic expression expected at position 1 in PrependTo
   *
   * <pre>
   * &gt;&gt; PrependTo(x, {3, 4})
   * PrependTo(x,{3,4})
   * </pre>
   */
  private static final class PrependTo extends AbstractCoreFunctionEvaluator {

    private static class PrependToFunction implements Function<IExpr, IExpr> {
      private final IExpr value;

      public PrependToFunction(final IExpr value) {
        this.value = value;
      }

      @Override
      public IExpr apply(final IExpr symbolValue) {
        if (symbolValue.isAssociation()) {
          if (value.isRuleAST() || value.isListOfRules() || value.isAssociation()) {
            IAssociation result = ((IAssociation) symbolValue);
            result.prependRules((IAST) value);
            return result;
          } else {
            // The argument is not a rule or a list of rules.
            return Errors.printMessage(S.PrependTo, "invdt", F.CEmptyList, EvalEngine.get());
          }
        }
        if (!symbolValue.isASTOrAssociation()) {
          return F.NIL;
        }
        return ((IAST) symbolValue).appendAtClone(1, value);
      }
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isASTSizeGE(S.Part, 3) && arg1.first().isSymbol()) {
        ISymbol sym = (ISymbol) arg1.first();
        return assignPartTo(sym, (IAST) arg1, S.Prepend, ast, engine);
      }

      IExpr sym = Validate.checkIsVariable(ast, 1, engine);
      if (sym.isSymbol()) {
        IExpr arg2 = engine.evaluate(ast.arg2());
        Function<IExpr, IExpr> function = new PrependToFunction(arg2);
        IExpr[] results = ((ISymbol) sym).reassignSymbolValue(function, S.PrependTo, engine);
        if (results != null) {
          return results[1];
        }
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDFIRST);
    }
  }

  /**
   *
   *
   * <pre>
   * Range(n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns a list of integers from <code>1</code> to <code>n</code>.
   *
   * </blockquote>
   *
   * <pre>
   * Range(a, b)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns a list of integers from <code>a</code> to <code>b</code>.
   *
   * </blockquote>
   *
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
  private static final class Range extends AbstractEvaluator {
    private static class UnaryRangeFunction implements IVariablesFunction {

      public UnaryRangeFunction() {}

      @Override
      public IExpr evaluate(final ISymbol[] variables, final IExpr[] index) {
        return index[0];
      }

    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isEmptyList()) {
        return ast.arg1();
      }

      if (ast.isAST1() && ast.arg1().isReal()) {
        int size = ast.arg1().toIntDefault();
        if (F.isPresent(size)) {
          return IAST.range(size + 1);
        }
        // `1`.
        return Errors.printMessage(S.Range, "error",
            F.List("argument " + ast.arg1() + " is greater than Javas Integer.MAX_VALUE-3."));
      }
      if (ast.isAST3()) {
        if (ast.arg3().isZero()) {
          ArithmeticUtil.printInfy(ast.topHead(), ast.arg2(), ast.arg3());
          return F.NIL;
        }
        if (ast.arg3().isDirectedInfinity()) {
          return ast.arg1();
        }
      }

      return evaluateTable(ast, F.List(), engine);
    }

    public IExpr evaluateTable(final IAST ast, final IAST resultList, EvalEngine engine) {
      List<IIterator<IExpr>> iterList = null;
      try {
        if ((ast.size() > 1) && (ast.size() <= 4)) {
          iterList = new ArrayList<IIterator<IExpr>>();
          iterList.add(Iterator.create(ast, null, engine));

          final TableGenerator generator =
              new TableGenerator(iterList, resultList, new UnaryRangeFunction(), F.CEmptyList);
          return generator.tableRecursive();
        }
      } catch (NoEvalException nev) {
        // Range specification in `1` does not have appropriate bounds.
        return Errors.printMessage(ast.topHead(), "range", F.list(ast), engine);
      } catch (final ArithmeticException | ClassCastException e) {
        // ClassCastException: the iterators are generated only from IASTs
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  private static class RankedMax extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      if (arg1.isList()) {
        IAST list = (IAST) arg1;
        int argSize = list.argSize();
        int n = arg2.toIntDefault();
        if (F.isPresent(n)) {
          if (n == 1) {
            return list.setAtCopy(0, S.Max);
          } else if (n == -1 || n == argSize) {
            return list.setAtCopy(0, S.Min);
          }
          if (n < 0) {
            // nth smallest element
            int pn = -n;
            if (pn < 1 || pn > argSize) {
              // The rank `1` is not an integer between `2` and `3`.
              return Errors.printMessage(ast.topHead(), "rank",
                  F.list(F.ZZ(n), F.C1, F.ZZ(argSize)), engine);
            }
            return rankedMin(list, pn, ast, engine);
          } else {
            // nth largest element
            if (n < 1 || n > argSize) {
              // The rank `1` is not an integer between `2` and `3`.
              return Errors.printMessage(ast.topHead(), "rank",
                  F.list(F.ZZ(n), F.C1, F.ZZ(argSize)), engine);
            }
            return rankedMin(list, list.size() - n, ast, engine);
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  private static class RankedMin extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      if (arg1.isListOrAssociation()) {
        IAST list = (IAST) arg1;
        int argSize = list.argSize();
        int n = arg2.toIntDefault();
        if (F.isPresent(n)) {
          if (n == 1) {
            return list.setAtCopy(0, S.Min);
          } else if (n == -1 || n == argSize) {
            return list.setAtCopy(0, S.Max);
          }
          if (n < 0) {
            // nth largest element
            int pn = -n;
            if (pn < 1 || pn > argSize) {
              // The rank `1` is not an integer between `2` and `3`.
              return Errors.printMessage(ast.topHead(), "rank",
                  F.list(F.ZZ(n), F.C1, F.ZZ(argSize)), engine);
            }
            return rankedMin(list, list.size() + n, ast, engine);
          } else {
            // nth smallest element
            if (n < 1 || n > argSize) {
              // The rank `1` is not an integer between `2` and `3`.
              return Errors.printMessage(ast.topHead(), "rank",
                  F.list(F.ZZ(n), F.C1, F.ZZ(argSize)), engine);
            }
            return rankedMin(list, n, ast, engine);
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  private static final class Replace extends AbstractEvaluator {

    private static final class ReplaceFunction implements Function<IExpr, IExpr> {
      private final EvalEngine engine;
      private IExpr rules;

      public ReplaceFunction(final IExpr rules, final EvalEngine engine) {
        this.rules = rules;
        this.engine = engine;
      }

      /**
       * Replace the <code>input</code> expression with the given rules.
       *
       * @param input the expression which should be replaced by the given rules
       * @return the expression created by the replacements or <code>null</code> if no replacement
       *         occurs
       */
      @Override
      public IExpr apply(IExpr input) {
        if (rules.isRuleAST()) {
          return replaceRule(input, (IAST) rules, engine);
        } else if (rules.isListOfRules()) {
          Function<IExpr, IExpr> function = Functors.rules(rules, engine);
          IExpr temp = function.apply(input);
          if (temp.isPresent()) {
            return temp;
          }
          return input;
        } else if (rules instanceof DispatchExpr) {
          DispatchExpr dispatch = (DispatchExpr) rules;
          IExpr temp = dispatch.apply(input);
          if (temp.isPresent()) {
            return temp;
          }
          return input;
        } else if (rules.isAssociation()) {
          return replaceRule(input, (IAST) rules.normal(false), engine);
        }

        throw new ArgumentTypeException(
            "rule expressions (x->y) expected instead of " + rules.toString());
      }

      public void setRule(IExpr rules) {
        this.rules = rules;
      }
    }

    private static IExpr replaceExpr(final IAST ast, IExpr arg1, IExpr rules,
        final EvalEngine engine) {
      if (rules.isList()) {
        for (IExpr element : (IAST) rules) {
          if (element.isRuleAST()) {
            IAST rule = (IAST) element;
            Function<IExpr, IExpr> function = Functors.rules(rule, engine);
            IExpr temp = function.apply(arg1);
            if (temp.isPresent()) {
              return temp;
            }
          } else {
            throw new ArgumentTypeException(
                "rule expressions (x->y) expected instead of " + element.toString());
          }
        }
        return arg1;
      }
      if (rules.isRuleAST()) {
        return replaceRule(arg1, (IAST) rules, engine);
      }
      throw new ArgumentTypeException(
          "rule expressions (x->y) expected instead of " + rules.toString());
    }

    private static IExpr replaceExprWithLevelSpecification(final IAST ast, IExpr arg1, IExpr rules,
        IExpr levelSpecification, EvalEngine engine) {
      // use replaceFunction#setRule() method to set the current rules which
      // are initialized with an empty list { }
      ReplaceFunction replaceFunction = new ReplaceFunction(F.CEmptyList, engine);
      VisitorLevelSpecification level =
          new VisitorLevelSpecification(replaceFunction, levelSpecification, false, 0, engine);
      replaceFunction.setRule(rules);
      return arg1.accept(level).orElse(arg1);
    }

    /**
     * Try to apply one single rule.
     *
     * @param arg1
     * @param rule
     * @return
     */
    private static IExpr replaceRule(IExpr arg1, IAST rule, EvalEngine engine) {
      Function<IExpr, IExpr> function = Functors.rules(rule, engine);
      IExpr temp = function.apply(arg1);
      if (temp.isPresent()) {
        return temp;
      }
      return arg1;
    }

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.size() < 3 || ast.size() > 4) {
        return F.NIL;
      }
      IExpr arg1 = ast.arg1();
      IExpr rules = ast.arg2();
      if (rules.isListOfLists()) {
        return rules.mapThread(ast, 2);
      }

      if (ast.isAST3()) {
        // arg3 should contain a "level specification":
        return replaceExprWithLevelSpecification(ast, arg1, rules, ast.arg3(), engine);
      }
      return replaceExpr(ast, arg1, rules, engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3_1;
    }

  }

  /**
   *
   *
   * <pre>
   * ReplaceAll(expr, i -&gt; new)
   * </pre>
   *
   * <p>
   * or
   *
   * <pre>
   * expr /. i -&gt; new
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * replaces all <code>i</code> in <code>expr</code> with <code>new</code>.
   *
   * </blockquote>
   *
   * <pre>
   * ReplaceAll(expr, {i1 -&gt; new1, i2 -&gt; new2, ... } )
   * </pre>
   *
   * <p>
   * or
   *
   * <pre>
   * expr /. {i1 -&gt; new1, i2 -&gt; new2, ... }
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * replaces all <code>i</code>s in <code>expr</code> with <code>new</code>s.
   *
   * </blockquote>
   *
   * <pre>
   * &gt;&gt; f(a) + f(b) /. f(x_) -&gt; x^2
   * a^2+b^2
   * </pre>
   */
  private static class ReplaceAll extends AbstractEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {

      if (ast.size() == 3) {
        IExpr arg1 = ast.arg1();
        IExpr arg2 = ast.arg2();
        if (arg2.isListOfLists()) {
          return arg2.mapThread(ast, 2);
        }

        VisitorReplaceAll visitor = VisitorReplaceAll.createVisitor(arg2);
        return arg1.accept(visitor).orElse(arg1);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   *
   *
   * <pre>
   * <code>ReplaceList(expr, lhs -&gt; rhs)
   * </code>
   * </pre>
   *
   * <p>
   * or
   *
   * <pre>
   * <code>ReplaceList(expr, lhs :&gt; rhs)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * replaces the left-hand-side pattern expression <code>lhs</code> in <code>expr</code> with the
   * right-hand-side <code>rhs</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; ReplaceList(a+b+c,(x_+y_) :&gt; {{x},{y}})
   * {{{a},{b+c}},{{b},{a+c}},{{c},{a+b}},{{a+b},{c}},{{a+c},{b}},{{b+c},{a}}}
   * </code>
   * </pre>
   */
  private static final class ReplaceList extends AbstractEvaluator {

    private static IExpr replaceExpr(final IAST ast, IExpr arg1, IExpr rules, IASTAppendable result,
        int maxNumberOfResults, final EvalEngine engine) {
      if (rules.isList()) {
        IAST rulesList = (IAST) rules;
        for (IExpr element : rulesList) {
          if (element.isRuleAST()) {
            IAST rule = (IAST) element;
            Function<IExpr, IExpr> function = Functors.listRules(rule, result, engine);
            // https://errorprone.info/bugpattern/ReturnValueIgnored
            IExpr temp = function.apply(arg1);
          } else {
            throw new ArgumentTypeException(
                "rule expressions (x->y) expected instead of " + element.toString());
          }
        }

        return result;
      }
      if (rules.isRuleAST()) {
        Function<IExpr, IExpr> function = Functors.listRules((IAST) rules, result, engine);
        IExpr temp = function.apply(arg1);
        if (temp.isPresent()) {
          if (temp.isList() && maxNumberOfResults < temp.argSize() && maxNumberOfResults > 0) {
            return ((IAST) temp).copyUntil(maxNumberOfResults + 1);
          }
          return temp;
        }
      } else {
        throw new ArgumentTypeException(
            "rule expressions (x->y) expected instead of " + rules.toString());
      }
      return result;
    }

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.size() == 2 && ast.head().isAST(S.ReplaceList, 2)) {
        return F.ReplaceList(ast.first(), ast.head().first());
      }
      if (ast.size() >= 3 && ast.size() <= 4) {
        try {
          int maxNumberOfResults = Integer.MAX_VALUE;
          IExpr arg1 = ast.arg1();
          IExpr rules = ast.arg2();
          if (ast.isAST3()) {
            IExpr arg3 = engine.evaluate(ast.arg3());
            if (!ast.arg3().isInfinity()) {
              maxNumberOfResults = arg3.toIntDefault();
            }
            if (maxNumberOfResults < 0) {
              // Non-negative integer or Infinity expected at position `1` in `2`.
              return Errors.printMessage(S.ReplaceList, "innf", F.List(F.C3, ast), engine);
            }
            if (maxNumberOfResults == 0) {
              return F.CEmptyList;
            }
          }
          IASTAppendable result = F.ListAlloc();
          return replaceExpr(ast, arg1, rules, result, maxNumberOfResults, engine);
        } catch (ArithmeticException ae) {
          return Errors.printMessage(S.ReplaceList, ae, engine);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3_1;
    }

  }

  /**
   *
   *
   * <pre>
   * ReplacePart(expr, i -&gt; new)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * replaces part <code>i</code> in <code>expr</code> with <code>new</code>.
   *
   * </blockquote>
   *
   * <pre>
   * ReplacePart(expr, {{i, j} -&gt; e1, {k, l} -&gt; e2})'
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * replaces parts <code>i</code> and <code>j</code> with <code>e1</code>, and parts <code>k
   * </code> and <code>l</code> with <code>e2</code>.
   *
   * </blockquote>
   *
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
   *
   * <p>
   * Delayed rules are evaluated once for each replacement:
   *
   * <pre>
   * &gt;&gt; n = 1
   * &gt;&gt; ReplacePart({a, b, c, d}, {{1}, {3}} :&gt; n++)
   * {1,b,2,d}
   * </pre>
   *
   * <p>
   * Non-existing parts are simply ignored:
   *
   * <pre>
   * &gt;&gt; ReplacePart({a, b, c}, 4 -&gt; t)
   * {a,b,c}
   * </pre>
   *
   * <p>
   * You can replace heads by replacing part <code>0</code>:
   *
   * <pre>
   * &gt;&gt; ReplacePart({a, b, c}, 0 -&gt; Times)
   * a*b*c
   * </pre>
   *
   * <p>
   * Negative part numbers count from the end:
   *
   * <pre>
   * &gt;&gt; ReplacePart({a, b, c}, -1 -&gt; t)
   * {a,b,t}
   * </pre>
   */
  private static final class ReplacePart extends AbstractFunctionOptionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, int argSize, IExpr[] option, EvalEngine engine,
        IAST originalAST) {
      IExpr result = ast.arg1();
      COMPARE_TERNARY heads = COMPARE_TERNARY.UNDECIDABLE;
      IExpr headsOption = option[0];
      if (headsOption.isTrue()) {
        heads = COMPARE_TERNARY.TRUE;
      } else if (headsOption.isFalse()) {
        heads = COMPARE_TERNARY.FALSE;
      } else {
        if (headsOption != S.Automatic) {
          // Value of option `1` -> `2` should be True, False or Automatic.
          return Errors.printMessage(S.ReplacePart, "opttfa", F.List(S.Heads, headsOption), engine);
        }
      }
      if (argSize == 3) {
        IExpr lhs = ast.arg3();
        IExpr rhs = ast.arg2();
        if (lhs.isList()) {
          if (lhs.exists(x -> !x.isInteger())) {
            // Position specification `1` in `2` is not a machine sized integer or a list of
            // machine-sized integers.
            return Errors.printMessage(S.ReplacePart, "psl", F.List(F.C3, ast), engine);
          }
        } else {
          int position = lhs.toIntDefault();
          if (F.isNotPresent(position)) {
            // Position specification `1` in `2` is not a machine sized integer or a list of
            // machine-sized integers.
            return Errors.printMessage(S.ReplacePart, "psl", F.List(F.C3, ast), engine);
          }
        }
        // Note: Rubi uses this kind of rule:
        return result.replacePart(lhs, rhs, heads).orElse(result);
      }

      final IExpr arg2 = ast.arg2();
      if (arg2.isRuleAST()) {
        IAST ruleAST = (IAST) arg2;
        return ast.arg1().replacePart(ruleAST.arg1(), ruleAST.arg2(), heads).orElse(ast.arg1());
      }

      if (arg2.isList()) {
        IAST listAST = (IAST) arg2;
        if (ast.arg2().isListOfRules()) {
          IExpr expr = result.replacePart(listAST, heads);
          if (expr.isPresent()) {
            result = expr;
          }
          return result;
        }
        for (IExpr subList : listAST) {
          IExpr expr = result.replacePart(F.Rule(subList, ast.arg2()), heads);
          if (expr.isPresent()) {
            result = expr;
          }
        }
        return result;
      }
      // The expression `1` cannot be used as a part specification
      Errors.printMessage(S.ReplacePart, "pkspec1", F.List(arg2), engine);
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, S.Heads, S.Automatic);
    }


  }

  /**
   *
   *
   * <pre>
   * <code>ReplaceRepeated(expr, lhs -&gt; rhs)
   *
   * expr //. lhs -&gt; rhs
   * </code>
   * </pre>
   *
   * <p>
   * or
   *
   * <pre>
   * <code>ReplaceRepeated(expr, lhs :&gt; rhs)
   *
   * expr //. lhs :&gt; rhs
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * repeatedly applies the rule <code>lhs -&gt; rhs</code> to <code>expr</code> until the result no
   * longer changes.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; a+b+c //. c-&gt;d
   * a+b+d
   * </code>
   * </pre>
   *
   * <p>
   * Simplification of logarithms:
   *
   * <pre>
   * <code>&gt;&gt; logrules = {Log(x_ * y_) :&gt; Log(x) + Log(y), Log(x_^y_) :&gt; y * Log(x)};
   *
   * &gt;&gt; Log(a * (b * c) ^ d ^ e * f) //. logrules
   * Log(a)+d^e*(Log(b)+Log(c))+Log(f)
   * </code>
   * </pre>
   *
   * <p>
   * <code>ReplaceAll</code> just performs a single replacement:
   *
   * <pre>
   * <code>&gt;&gt; Log(a * (b * c) ^ d ^ e * f) /. logrules
   * Log(a)+Log((b*c)^d^e*f)
   * </code>
   * </pre>
   */
  private static final class ReplaceRepeated extends AbstractEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      if (arg2.isListOfLists()) {
        return arg2.mapThread(ast, 2);
      }

      int maxIterations = -1;
      if (ast.isAST3()) {
        final OptionArgs options = new OptionArgs(ast.topHead(), ast, 3, engine);
        maxIterations = options.getOptionMaxIterations(S.MaxIterations);
        if (F.isNotPresent(maxIterations)) {
          return F.NIL;
        }
      }
      VisitorReplaceAll visitor = VisitorReplaceAll.createVisitor(arg2);
      return arg1.replaceRepeated(visitor, maxIterations);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   *
   *
   * <pre>
   * Rest(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns <code>expr</code> with the first element removed.
   *
   * </blockquote>
   *
   * <p>
   * <code>Rest(expr)</code> is equivalent to <code>expr[[2;;]]</code>.
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Rest({a, b, c})
   * {b,c}
   *
   * &gt;&gt; Rest(a + b + c)
   * b+c
   * </pre>
   *
   * <p>
   * Nonatomic expression expected.
   *
   * <pre>
   * &gt;&gt; Rest(x)
   * Rest(x)
   * </pre>
   */
  private static final class Rest extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr rest = ast.arg1().rest();
      if (rest.isPresent()) {
        return rest;
      }
      // if (arg1.isASTOrAssociation() && ((IAST) arg1).size() > 1) {
      // return arg1.rest();
      // }
      // Nonatomic expression expected at position `1` in `2`.
      return Errors.printMessage(ast.topHead(), "normal", F.list(F.C1, ast), engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   *
   *
   * <pre>
   * Reverse(list)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * reverse the elements of the <code>list</code>.
   *
   * </blockquote>
   *
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
  private static final class Reverse extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isAssociation()) {
        IAssociation assoc = (IAssociation) arg1;
        return assoc.reverse(F.assoc());
      }
      if (arg1.isAST()) {
        IAST list = (IAST) arg1;
        return reverse(list);
      }

      // Nonatomic expression expected at position `1` in `2`.
      return Errors.printMessage(S.Reverse, "normal", F.list(F.C1, ast), engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static final class RightComposition extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.head().equals(S.RightComposition)) {
        if (ast.isAST0()) {
          return S.Identity;
        }
        return ast.remove(x -> x.equals(S.Identity));
      }
      if (ast.head().isAST()) {

        IAST headList = (IAST) ast.head();
        if (headList.size() > 1) {
          IASTAppendable inner = F.ast(headList.last());
          IAST result = inner;
          IASTAppendable temp;
          for (int i = headList.size() - 2; i >= 1; i--) {
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
   *
   *
   * <pre>
   * Riffle(list1, list2)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * insert elements of <code>list2</code> between the elements of <code>list1</code>.
   *
   * </blockquote>
   *
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
  private static final class Riffle extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = engine.evaluate(ast.arg1());
      if (!arg1.isList()) {
        // List expected at position `1` in `2`.
        return Errors.printMessage(ast.topHead(), "list", F.list(F.C1, ast), engine);
      }
      IExpr arg2 = engine.evaluate(ast.arg2());

      IAST list = (IAST) arg1;
      if (arg2.isASTOrAssociation()) {
        return riffleAST(list, (IAST) arg2);
      } else {
        return riffleAtom(list, arg2);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    public static IExpr riffleAtom(IAST arg1, final IExpr arg2) {
      if (arg1.size() < 2) {
        return arg1;
      }
      IASTAppendable result = arg1.copyHead(arg1.argSize() * 2 + 1);
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
      IASTAppendable result = arg1.copyHead(arg1.size() * 2);
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
   *
   *
   * <pre>
   * RotateLeft(list)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * rotates the items of <code>list</code> by one item to the left.
   *
   * </blockquote>
   *
   * <pre>
   * RotateLeft(list, n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * rotates the items of <code>list</code> by <code>n</code> items to the left.
   *
   * </blockquote>
   *
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
  private static final class RotateLeft extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isASTOrAssociation()) {
        final int argSize = arg1.argSize();
        if (argSize == 0) {
          return arg1;
        }
        IAST list = (IAST) arg1;

        if (ast.isAST1()) {
          final IASTAppendable result = F.ast(list.head(), list.size() + 1);
          list.rotateLeft(result, 1);
          return result;
        } else {
          int n = ast.arg2().toIntDefault();
          if (F.isNotPresent(n)) {
            // Rotation specification `1` should be a machine-sized integer or list of machine-sized
            // integers.
            return Errors.printMessage(S.RotateRight, "rspec", F.List(ast.arg2()), engine);
          }
          if (n < 0) {
            n = -n;
            n = n % argSize;
            final IASTAppendable result = F.ast(list.head(), list.size() + n);
            list.rotateRight(result, n);
            return result;
          }
          n = n % argSize;
          final IASTAppendable result = F.ast(list.head(), list.size() + n);
          list.rotateLeft(result, n);
          return result;
        }
      }
      // Nonatomic expression expected at position `1` in `2`.
      return Errors.printMessage(S.RotateLeft, "normal", F.list(F.C1, ast), engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   *
   *
   * <pre>
   * RotateRight(list)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * rotates the items of <code>list</code> by one item to the right.
   *
   * </blockquote>
   *
   * <pre>
   * RotateRight(list, n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * rotates the items of <code>list</code> by <code>n</code> items to the right.
   *
   * </blockquote>
   *
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
  private static final class RotateRight extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isASTOrAssociation()) {
        final int argSize = arg1.argSize();
        if (argSize == 0) {
          return arg1;
        }
        IAST list = (IAST) arg1;

        if (ast.isAST1()) {
          final IASTAppendable result = F.ast(list.head(), list.size() + 1);
          list.rotateRight(result, 1);
          return result;
        } else {
          int n = ast.arg2().toIntDefault();
          if (F.isNotPresent(n)) {
            // Rotation specification `1` should be a machine-sized integer or list of machine-sized
            // integers.
            return Errors.printMessage(S.RotateRight, "rspec", F.List(ast.arg2()), engine);
          }
          if (n < 0) {
            n = -n;
            n = n % argSize;
            final IASTAppendable result = F.ast(list.head(), list.size() + n);
            list.rotateLeft(result, n);
            return result;
          }
          n = n % argSize;
          final IASTAppendable result = F.ast(list.head(), list.size() + n);
          list.rotateRight(result, n);
          return result;
        }
      }
      // Nonatomic expression expected at position `1` in `2`.
      return Errors.printMessage(S.RotateRight, "normal", F.list(F.C1, ast), engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   *
   *
   * <pre>
   * Select({e1, e2, ...}, f)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns a list of the elements <code>ei</code> for which <code>f(ei)</code> returns <code>
   * True</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <p>
   * Find numbers greater than zero:
   *
   * <pre>
   * &gt;&gt; Select({-3, 0, 1, 3, a}, #&gt;0&amp;)
   * {1,3}
   * </pre>
   *
   * <p>
   * <code>Select</code> works on an expression with any head:
   *
   * <pre>
   * &gt;&gt; Select(f(a, 2, 3), NumberQ)
   * f(2,3)
   * </pre>
   *
   * <p>
   * Nonatomic expression expected.
   *
   * <pre>
   * &gt;&gt; Select(a, True)
   * Select(a,True)
   * </pre>
   */
  private static final class Select extends AbstractEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      try {
        if (ast.arg1().isASTOrAssociation()) {
          int maxNumberOfResults = Integer.MAX_VALUE;
          IAST list = (IAST) ast.arg1();
          IExpr predicateHead = ast.arg2();
          if (ast.isAST2()) {
            return list.select(x -> engine.evalTrue(predicateHead, x));
          } else if (ast.isAST3()) {
            IExpr arg3 = engine.evaluate(ast.arg3());
            if (!ast.arg3().isInfinity()) {
              maxNumberOfResults = arg3.toIntDefault();
              if (maxNumberOfResults < 0) {
                // Non-negative integer or Infinity expected at position `1` in `2`.
                return Errors.printMessage(S.Select, "innf", F.List(F.C3, ast), engine);
              }
              if (maxNumberOfResults == 0) {
                return F.CEmptyList;
              }
            }
            return list.select(x -> engine.evalTrue(predicateHead, x), maxNumberOfResults);
          }
        }
      } catch (IllegalArgumentException iae) {
        return Errors.printMessage(S.Select, iae, engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static final class SelectFirst extends AbstractEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {

      int argSize = ast.argSize();
      if (argSize > 1) {
        if (ast.arg1().isASTOrAssociation()) {
          IAST list = (IAST) ast.arg1();
          IExpr predicateHead = ast.arg2();
          IExpr defaultValue = F.CMissingNotFound;
          if (argSize == 3) {
            defaultValue = ast.arg3();
          }
          int index = list.indexOf(x -> engine.evalTrue(predicateHead, x));
          if (index > 0) {
            return list.get(index);
          }
          return defaultValue;
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDREST);
    }
  }

  /**
   *
   *
   * <pre>
   * Split(list)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * splits <code>list</code> into collections of consecutive identical elements.
   *
   * </blockquote>
   *
   * <pre>
   * Split(list, test)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * splits <code>list</code> based on whether the function <code>test</code> yields 'True' on
   * consecutive elements.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Split({x, x, x, y, x, y, y, z})
   * {{x,x,x},{y},{x},{y,y},{z}}
   *
   * &gt;&gt; Split({x, x, x, y, x, y, y, z}, x)
   * {{x},{x},{x},{y},{x},{y},{y},{z}}
   * </pre>
   *
   * <p>
   * Split into increasing or decreasing runs of elements
   *
   * <pre>
   * &gt;&gt; Split({1, 5, 6, 3, 6, 1, 6, 3, 4, 5, 4}, Less)
   * {{1,5,6},{3,6},{1,6},{3,4,5},{4}}
   *
   * &gt;&gt; Split({1, 5, 6, 3, 6, 1, 6, 3, 4, 5, 4}, Greater)
   * {{1},{5},{6,3},{6,1},{6,3},{4},{5,4}}
   * </pre>
   *
   * <p>
   * Split based on first element
   *
   * <pre>
   * &gt;&gt; Split({x -&gt; a, x -&gt; y, 2 -&gt; a, z -&gt; c, z -&gt; a}, First(#1) === First(#2) &amp;)
   * {{x-&gt;a,x-&gt;y},{2-&gt;a},{z-&gt;c,z-&gt;a}}
   *
   * &gt;&gt; Split({})
   * {}
   * </pre>
   */
  private static final class Split extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isASTOrAssociation()) {
        IExpr predicateHead = S.Equal;
        if (ast.isAST2()) {
          predicateHead = ast.arg2();
        }
        BiPredicate<IExpr, IExpr> pred = Predicates.isBinaryTrue(predicateHead);
        IAST list = (IAST) ast.arg1();

        IASTAppendable result = F.ListAlloc(8);
        if (list.size() > 1) {
          IExpr current = list.arg1();
          IASTAppendable subResultList = F.ListAlloc(8);
          result.append(subResultList);
          subResultList.append(current);
          for (int i = 2; i < list.size(); i++) {
            IExpr listElement = list.get(i);
            if (pred.test(current, listElement)) {
            } else {
              subResultList = F.ListAlloc(8);
              result.append(subResultList);
            }
            subResultList.append(listElement);
            current = listElement;
          }
        }
        return result;
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   *
   *
   * <pre>
   * SplitBy(list, f)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * splits <code>list</code> into collections of consecutive elements that give the same result
   * when <code>f</code> is applied.
   *
   * </blockquote>
   *
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
  private static final class SplitBy extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isASTOrAssociation()) {
        return splitByFunction(ast.arg2().makeList(), 1, (IAST) ast.arg1(), engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
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
          IExpr listElement = list.get(i);
          current = function.apply(listElement);
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
          temp.append(listElement);
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
    public void setUp(final ISymbol newSymbol) {}
  }

  private static final class Subdivide extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      IExpr arg1 = ast.arg1();
      if (ast.isAST1()) {
        int n = arg1.toIntDefault(-1);
        if (n <= 0) {
          // The number of subdivisions given in position `1` of `2` should be a positive
          // machine-sized integer.
          return Errors.printMessage(S.Subdivide, "sdmint", F.list(F.C1, ast), engine);
        }
        return F.subdivide(n);
      }
      IExpr arg2 = ast.arg2();
      if (ast.isAST2()) {
        int n = arg2.toIntDefault(-1);
        if (n <= 0) {
          // The number of subdivisions given in position `1` of `2` should be a positive
          // machine-sized integer.
          return Errors.printMessage(S.Subdivide, "sdmint", F.list(F.C2, ast), engine);
        }
        IAST factorList = subdivide(arg2, n);
        return factorList.map(x -> arg1.times(x), 1);
      }

      if (arg1.isList() && arg2.isList()) {
        if (arg1.size() != arg2.size()) {
          return F.NIL;
        }
      }
      IExpr arg3 = ast.arg3();
      int n = arg3.toIntDefault(-1);
      if (n <= 0) {
        // The number of subdivisions given in position `1` of `2` should be a positive
        // machine-sized integer.
        return Errors.printMessage(S.Subdivide, "sdmint", F.list(F.C3, ast), engine);
      }
      IAST factorList = subdivide(arg3, n);
      return factorList.map(x -> arg1.plus(arg2.times(x).subtract(arg1.times(x))), 1);
    }

    public static IAST subdivide(IExpr arg1, int n) {
      return IAST.range(0, n + 1).map(x -> x.divide(arg1), 1);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      // newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  /**
   *
   *
   * <pre>
   * Table(expr, {i, n})
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * evaluates <code>expr</code> with <code>i</code> ranging from <code>1</code> to <code>n
   * </code>, returning a list of the results.
   *
   * </blockquote>
   *
   * <pre>
   * Table(expr, {i, start, stop, step})
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * evaluates <code>expr</code> with <code>i</code> ranging from <code>start</code> to <code>
   * stop</code>, incrementing by <code>step</code>.
   *
   * </blockquote>
   *
   * <pre>
   * Table(expr, {i, {e1, e2, ..., ei}})
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * evaluates <code>expr</code> with <code>i</code> taking on the values <code>e1, e2, ..., ei
   * </code>.
   *
   * </blockquote>
   *
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
   *
   * <p>
   * <code>Table</code> supports multi-dimensional tables:
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

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return evaluateTable(ast, F.CEmptyList, F.CEmptyList, engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_INFINITY;
    }

    /**
     * Generate a table from standard iterator notation.
     *
     * @param ast
     * @param resultList the result list to which the generated expressions should be appended.
     * @param defaultValue the default value used in the iterator
     * @param engine the current evaluation engine
     * @return {@link F#NIL} if no evaluation is possible
     */
    protected static IExpr evaluateTable(final IAST ast, final IAST resultList, IExpr defaultValue,
        EvalEngine engine) {
      try {
        if (ast.size() > 2) {
          final List<IIterator<IExpr>> iterList = new ArrayList<IIterator<IExpr>>();
          for (int i = 2; i < ast.size(); i++) {
            IExpr arg = ast.get(i);
            if (arg.isList()) {
              iterList.add(Iterator.create((IAST) arg, i, engine));
            } else {
              IExpr evaledArg = engine.evaluate(arg);
              if (evaledArg.isReal()) {
                iterList.add(Iterator.create(F.list(evaledArg), i, engine));
              } else {
                // Non-list iterator `1` at position `2` does not evaluate to a real numeric value.
                return Errors.printMessage(ast.topHead(), "nliter", F.list(arg, F.ZZ(i)), engine);
              }
            }
          }

          final TableGenerator generator = new TableGenerator(iterList, resultList,
              new TableFunction(engine, ast.arg1()), defaultValue);
          return generator.tableRecursive();
        }
      } catch (final ArrayIndexOutOfBoundsException e) {
        return Errors.printMessage(S.Table, e, EvalEngine.get());
      } catch (final NoEvalException | ClassCastException | ArithmeticException e) {
        // ClassCastException: the iterators are generated only from IASTs
        // ArithmeticException example: division / by zero if step==-1
      }
      return F.NIL;
    }

    protected static IExpr evaluateTableThrow(final IAST ast, final IAST resultList,
        IExpr defaultValue, EvalEngine engine) {
      try {
        if (ast.size() > 2) {
          final List<IIterator<IExpr>> iterList = new ArrayList<IIterator<IExpr>>();
          for (int i = 2; i < ast.size(); i++) {
            IExpr arg = ast.get(i);
            iterList.add(Iterator.create(arg.makeList(), i, engine));
          }

          final TableGenerator generator = new TableGenerator(iterList, resultList,
              new TableFunction(engine, ast.arg1()), defaultValue);
          return generator.tableThrowRecursive();
        }
      } catch (final ArrayIndexOutOfBoundsException e) {
        return Errors.printMessage(S.Table, e, EvalEngine.get());
      } catch (final NoEvalException | ClassCastException | ArithmeticException e) {
        // ClassCastException: the iterators are generated only from IASTs
        // ArithmeticException example: division / by zero if step==-1
      }
      return F.NIL;
    }

    /**
     * Evaluate only the last iterator in <code>iter</code> for <code>Sum()</code> or <code>
     * Product()</code> function calls.
     *
     * @param expr
     * @param iter the iterator function
     * @param resultList the result list to which the generated expressions should be appended.
     * @param defaultValue the default value used if the iterator is invalid
     * @return {@link F#NIL} if no evaluation is possible
     * @see Product
     * @see Sum
     */
    protected static IExpr evaluateLast(final IExpr expr, final IIterator<IExpr> iter,
        final IAST resultList, IExpr defaultValue) {
      try {
        final List<IIterator<IExpr>> iterList = new ArrayList<IIterator<IExpr>>();
        iterList.add(iter);

        final TableGenerator generator = new TableGenerator(iterList, resultList,
            new TableFunction(EvalEngine.get(), expr), defaultValue);
        return generator.tableRecursive();
      } catch (final ArrayIndexOutOfBoundsException e) {
        return Errors.printMessage(S.Table, e, EvalEngine.get());
      } catch (final NoEvalException | ClassCastException | ArithmeticException e) {
        // ClassCastException: the iterators are generated only from IASTs
        // ArithmeticException example: division / by zero if step==-1
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
     * @return a list of local variables
     */
    public IAST determineIteratorVariables(final IAST ast) {
      int size = ast.size();
      return F.mapRange(2, size, i -> {
        final IExpr arg = ast.get(i);
        if (arg.isVariable()) {
          return arg;
        }
        if (arg.isList() && arg.size() >= 2 && arg.first().isVariable()) {
          return arg.first();
        }
        return F.NIL;
      });
    }

    /**
     * Determine all local variables of the iterators starting with index <code>2</code> in the
     * given <code>ast</code>.
     *
     * @param ast
     * @return the variable set of local variables
     */
    public static VariablesSet determineIteratorExprVariables(final IAST ast) {
      VariablesSet variableList = new VariablesSet();
      for (int i = 2; i < ast.size(); i++) {
        IExpr arg = ast.get(i);
        if (arg.isVariable()) {
          variableList.add(arg);
        } else {
          if (arg.isList() && arg.size() >= 2 && arg.first().isVariable()) {
            variableList.add(arg.first());
          }
        }
      }
      return variableList;
    }

    /**
     * Disable the <code>Reap() and Sow()</code> mode temporary and evaluate an expression for the
     * given &quot;local variables list&quot;. If evaluation is not possible return the input
     * object.
     *
     * @param expr the expression which should be evaluated
     * @param localVariablesList a list of symbols which should be used as local variables inside
     *        the block
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
        Errors.rethrowsInterruptException(rex);
        // ignore
      } finally {
        engine.setReapList(reapList);
        engine.setQuietMode(quietMode);
      }
      return expr;
    }
  }

  /**
   *
   *
   * <pre>
   * <code>Tally(list)
   * </code>
   * </pre>
   *
   * <p>
   * or
   *
   * <pre>
   * <code>Tally(list, binaryPredicate)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * return the elements and their number of occurrences in <code>list</code> in a new result list.
   * The <code>binaryPredicate</code> tests if two elements are equivalent. <code>SameQ
   * </code> is used as the default <code>binaryPredicate</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; str=&quot;The quick brown fox jumps over the lazy dog&quot;;
   *
   * &gt;&gt; Tally(Characters(str)) // InputForm
   * {{&quot;T&quot;,1},{&quot;h&quot;,2},{&quot;e&quot;,3},{&quot; &quot;,8},{&quot;q&quot;,1},{&quot;u&quot;,2},{&quot;i&quot;,1},{&quot;c&quot;,1},{&quot;k&quot;,1},{&quot;b&quot;,1},{&quot;r&quot;,2},{&quot;o&quot;,4},{&quot;w&quot;,1},{&quot;n&quot;,1},{&quot;f&quot;,1},{&quot;x&quot;,1},{&quot;j&quot;,1},{&quot;m&quot;,1},{&quot;p&quot;,1},{&quot;s&quot;,1},{&quot;v&quot;,1},{&quot;t&quot;,1},{&quot;l&quot;,1},{&quot;a&quot;,1},{&quot;z&quot;,1},{&quot;y&quot;,1},{&quot;d&quot;,1},{&quot;g&quot;,1}}
   * </code>
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="Commonest.md">Commonest</a>, <a href="Counts.md">Counts</a>
   */
  private static final class Tally extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IAST list = Validate.checkListType(ast, 1, engine);
      if (list.isPresent()) {
        int size = ast.size();
        if (size == 2) {
          return tally(list);
        } else if (size == 3) {
          BiPredicate<IExpr, IExpr> biPredicate = Predicates.isBinaryTrue(ast.arg2());
          return tally(list, biPredicate);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

  }

  /**
   *
   *
   * <pre>
   * Take(expr, n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns <code>expr</code> with all but the first <code>n</code> leaves removed.
   *
   * </blockquote>
   *
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
   *
   * <p>
   * Take a submatrix:
   *
   * <pre>
   * &gt;&gt; A = {{a, b, c}, {d, e, f}}
   * &gt;&gt; Take(A, 2, 2)
   * {{a,b},{d,e}}
   * </pre>
   *
   * <p>
   * Take a single column:
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
   *
   * <p>
   * Cannot take positions <code>-5</code> through <code>-2</code> in <code>{1, 2, 3, 4, 5, 6}
   * </code>.
   *
   * <pre>
   * &gt;&gt; Take(Range(6), {-5, -2, -2})
   * Take({1, 2, 3, 4, 5, 6}, {-5, -2, -2})
   * </pre>
   *
   * <p>
   * Nonatomic expression expected at position <code>1</code> in <code>Take(l, {-1})</code>.
   *
   * <pre>
   * &gt;&gt; Take(l, {-1})
   * Take(l,{-1})
   * </pre>
   *
   * <p>
   * Empty case
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
   *
   * <p>
   * Cannot take positions <code>1</code> through <code>0</code> in <code>{1, 2, 3, 4, 5}</code>.
   *
   * <pre>
   * &gt;&gt; Take({1, 2, 3, 4, 5}, {1, 0, -1})
   * Take({1, 2, 3, 4, 5}, {1, 0, -1})
   * </pre>
   */
  private static final class Take extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // IAST evaledAST = (IAST) engine.evalAttributes(S.Take, ast);
      // if (evaledAST.isNIL()) {
      // evaledAST = ast;
      // }

      try {
        final ISequence[] sequ =
            Sequence.createSequences(ast, 2, ast.size(), "take", S.Take, engine);
        if (sequ == null) {
          return F.NIL;
        }
        if (ast.arg1().isASTOrAssociation()) {
          final IAST arg1 = (IAST) ast.arg1();
          if (arg1.isAssociation()) {
            return take((IAssociation) arg1, 0, sequ);
          }
          return take(arg1, 0, sequ);
        } else if (ast.arg1().isSparseArray()) {
          // TODO return sparse array instead of lists
          final IAST normal = ((ISparseArray) ast.arg1()).normal(false);
          return take(normal, 0, sequ);
        } else {
          // Nonatomic expression expected at position `1` in `2`.
          return Errors.printMessage(ast.topHead(), "normal", F.List(F.C1, ast), engine);
        }
      } catch (final ValidateException ve) {
        Errors.printMessage(ast.topHead(), ve, engine);
      } catch (final RuntimeException rex) {
        Errors.printMessage(S.Take, rex, EvalEngine.get());
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_INFINITY;
    }

    /**
     * Take the list elements according to the <code>sequenceSpecifications</code> for the list
     * indexes.
     *
     * @param list
     * @param level recursion level
     * @param sequenceSpecifications one or more ISequence specifications
     * @return
     */
    private static IAST take(final IAST list, final int level,
        final ISequence[] sequenceSpecifications) {
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
        if (start < end || end <= 0 || start >= list.size()) {
          // Cannot take positions `1` through `2` in `3`.
          String str =
              Errors.getMessage("take", F.list(F.ZZ(start), F.ZZ(end), list), EvalEngine.get());
          throw new ArgumentTypeException(str);
        }
        // negative step used here
        for (int i = start; i >= end; i += step) {
          IExpr arg = list.get(i);
          if (sequenceSpecifications.length > newLevel) {
            if (arg.isAssociation()) {
              resultList.append(take((IAssociation) arg, newLevel, sequenceSpecifications));
            } else if (arg.isASTOrAssociation()) {
              resultList.append(take((IAST) arg, newLevel, sequenceSpecifications));
            } else {
              throw new ArgumentTypeException(
                  "cannot execute take for argument: " + arg.toString());
            }
          } else {
            resultList.append(arg);
          }
        }
      } else {
        if (start == 0) {
          return resultList;
        }
        if (end > list.size()) {
          // Cannot take positions `1` through `2` in `3`.
          String str =
              Errors.getMessage("take", F.list(F.ZZ(start), F.ZZ(end - 1), list), EvalEngine.get());
          throw new ArgumentTypeException(str);
        }
        for (int i = start; i < end; i += step) {
          IExpr arg = list.get(i);
          if (sequenceSpecifications.length > newLevel) {
            if (arg.isAssociation()) {
              resultList.append(take((IAssociation) arg, newLevel, sequenceSpecifications));
            } else if (arg.isASTOrAssociation()) {
              resultList.append(take((IAST) arg, newLevel, sequenceSpecifications));
            } else {
              // List expected at position `1` in `2`.
              String str = Errors.getMessage("list", F.list(F.ZZ(i), list), EvalEngine.get());
              throw new ArgumentTypeException(str);
            }
          } else {
            resultList.append(arg);
          }
        }
      }
      return resultList;
    }

    private static IAST take(final IAssociation assoc2, final int level,
        final ISequence[] sequenceSpecifications) {
      ISequence sequ = sequenceSpecifications[level];
      // IAST normal = assoc2.normal(false);
      int size = assoc2.size();
      sequ.setListSize(size);
      final IASTAppendable resultAssoc = assoc2.copyHead(10 > size ? size : 10);
      final int newLevel = level + 1;
      int start = sequ.getStart();
      int end = sequ.getEnd();
      int step = sequ.getStep();
      if (step < 0) {
        end--;
        if (start < end || end <= 0 || start >= assoc2.size()) {
          // Cannot take positions `1` through `2` in `3`.
          String str =
              Errors.getMessage("take", F.list(F.ZZ(start), F.ZZ(end), assoc2), EvalEngine.get());
          throw new ArgumentTypeException(str);
        }
        // negative step used here
        for (int i = start; i >= end; i += step) {
          IAST rule = assoc2.getRule(i);
          IExpr arg = rule.second();
          if (sequenceSpecifications.length > newLevel) {
            if (arg.isAssociation()) {
              resultAssoc.appendRule(
                  F.Rule(rule.first(), take((IAssociation) arg, newLevel, sequenceSpecifications)));
            } else if (arg.isASTOrAssociation()) {
              resultAssoc.appendRule(
                  F.Rule(rule.first(), take((IAST) arg, newLevel, sequenceSpecifications)));
            } else {
              throw new ArgumentTypeException(
                  "cannot execute take for argument: " + arg.toString());
            }
          } else {
            resultAssoc.appendRule(rule);
          }
        }
      } else {
        if (start == 0) {
          return resultAssoc;
        }
        if (end > assoc2.size()) {
          // Cannot take positions `1` through `2` in `3`.
          String str = Errors.getMessage("take", F.list(F.ZZ(start), F.ZZ(end - 1), assoc2),
              EvalEngine.get());
          throw new ArgumentTypeException(str);
        }
        for (int i = start; i < end; i += step) {
          IAST rule = assoc2.getRule(i);
          IExpr arg = rule.second();
          if (sequenceSpecifications.length > newLevel) {
            if (arg.isAssociation()) {
              resultAssoc.appendRule(
                  F.Rule(rule.first(), take((IAssociation) arg, newLevel, sequenceSpecifications)));
            } else if (arg.isASTOrAssociation()) {
              resultAssoc.appendRule(
                  F.Rule(rule.first(), take((IAST) arg, newLevel, sequenceSpecifications)));
            } else {
              // List expected at position `1` in `2`.
              String str = Errors.getMessage("list", F.list(F.ZZ(i), assoc2), EvalEngine.get());
              throw new ArgumentTypeException(str);
            }
          } else {
            resultAssoc.appendRule(rule);
          }
        }
      }
      return resultAssoc;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.NHOLDREST);
    }
  }

  private static final class TakeLargest extends AbstractEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.isAST2()) {
        try {
          if (ast.arg1().isASTOrAssociation()) {
            IAST cleanedList = cleanList((IAST) ast.arg1());
            try {
              int n = ast.arg2().toIntDefault();
              if (n > 0) {
                if (n > cleanedList.argSize()) {
                  return Errors.printMessage(ast.topHead(), "insuff",
                      F.List(n, cleanedList.argSize()), engine);
                }
                IExpr temp = engine.evalN(cleanedList);
                if (temp.isListOrAssociation()) {
                  LargestIndexComparator comparator =
                      new LargestIndexComparator((IAST) temp, engine);
                  Integer[] indexes = comparator.createIndexArray();
                  Arrays.sort(indexes, comparator);
                  int[] largestIndexes = new int[n];
                  for (int i = 0; i < n; i++) {
                    largestIndexes[i] = indexes[i];
                  }
                  return cleanedList.getItems(largestIndexes, largestIndexes.length);
                }
              }
            } catch (NoEvalException neex) {
              // Input `1` is not a real-valued vector.
              return Errors.printMessage(ast.topHead(), "rvec2", F.list(cleanedList), engine);
            }
          }
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          return Errors.printMessage(S.TakeLargest, rex, EvalEngine.get());
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static final class TakeLargestBy extends AbstractEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.isAST2()) {
        ast = F.operatorForm1Append(ast);
        if (ast.isNIL()) {
          return F.NIL;
        }
      }
      if (ast.isAST3()) {
        try {
          if (ast.arg1().isASTOrAssociation()) {
            IAST cleanedList = cleanList((IAST) ast.arg1());
            int n = ast.arg3().toIntDefault();
            if (n > 0) {
              if (n > cleanedList.argSize()) {
                // Cannot take `1` elements from a list of length `2`.
                return Errors.printMessage(ast.topHead(), "insuff",
                    F.List(n, cleanedList.argSize()), engine);
              }
              IAST list = cleanedList.mapThread(F.unary(ast.arg2(), F.Slot1), 1);
              IExpr temp = engine.evalN(list);
              if (temp.isListOrAssociation()) {
                list = (IAST) temp;
                try {
                  LargestIndexComparator comparator = new LargestIndexComparator(list, engine);
                  Integer[] indexes = comparator.createIndexArray();
                  Arrays.sort(indexes, comparator);
                  int[] largestIndexes = new int[n];
                  for (int i = 0; i < n; i++) {
                    largestIndexes[i] = indexes[i];
                  }
                  return cleanedList.getItems(largestIndexes, largestIndexes.length);
                } catch (NoEvalException neex) {
                  // neex.printStackTrace();
                  // Values `1` produced by the function `2` cannot be used for numerical sorting
                  // because they are not all real.
                  return Errors.printMessage(ast.topHead(), "tbnval", F.list(list, ast.arg2()),
                      engine);
                }
              }
            }
          }
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          return Errors.printMessage(S.TakeLargestBy, rex, EvalEngine.get());
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3_0;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static final class TakeSmallest extends AbstractEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.isAST2()) {
        try {
          if (ast.arg1().isASTOrAssociation()) {
            IAST cleanedList = cleanList((IAST) ast.arg1());
            try {
              int n = ast.arg2().toIntDefault();
              if (n > 0) {
                if (n > cleanedList.argSize()) {
                  return Errors.printMessage(ast.topHead(), "insuff",
                      F.List(n, cleanedList.argSize()), engine);
                }
                IExpr temp = engine.evalN(cleanedList);
                if (temp.isListOrAssociation()) {
                  SmallestIndexComparator comparator =
                      new SmallestIndexComparator((IAST) temp, engine);
                  Integer[] indexes = comparator.createIndexArray();
                  Arrays.sort(indexes, comparator);
                  int[] smallestIndexes = new int[n];
                  for (int i = 0; i < n; i++) {
                    smallestIndexes[i] = indexes[i];
                  }
                  return cleanedList.getItems(smallestIndexes, smallestIndexes.length);
                }
              }
            } catch (NoEvalException neex) {
              // Input `1` is not a real-valued vector.
              return Errors.printMessage(ast.topHead(), "rvec2", F.list(cleanedList), engine);
            }
          }
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          return Errors.printMessage(S.TakeSmallest, rex, EvalEngine.get());
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static final class TakeSmallestBy extends AbstractEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.isAST2()) {
        ast = F.operatorForm1Append(ast);
        if (ast.isNIL()) {
          return F.NIL;
        }
      }
      if (ast.isAST3()) {
        try {
          if (ast.arg1().isASTOrAssociation()) {
            IAST cleanedList = cleanList((IAST) ast.arg1());
            int n = ast.arg3().toIntDefault();
            if (n > 0) {
              if (n > cleanedList.argSize()) {
                return Errors.printMessage(ast.topHead(), "insuff",
                    F.List(n, cleanedList.argSize()), engine);
              }
              IAST list = cleanedList.mapThread(F.unary(ast.arg2(), F.Slot1), 1);
              IExpr temp = engine.evalN(list);
              if (temp.isListOrAssociation()) {
                list = (IAST) temp;
                try {
                  SmallestIndexComparator comparator = new SmallestIndexComparator(list, engine);
                  Integer[] indexes = comparator.createIndexArray();
                  Arrays.sort(indexes, comparator);
                  int[] smallestIndexes = new int[n];
                  for (int i = 0; i < n; i++) {
                    smallestIndexes[i] = indexes[i];
                  }
                  return cleanedList.getItems(smallestIndexes, smallestIndexes.length);
                } catch (NoEvalException neex) {
                  // Values `1` produced by the function `2` cannot be used for numerical sorting
                  // because they are not all real.
                  return Errors.printMessage(ast.topHead(), "tbnval", F.list(list, ast.arg2()),
                      engine);
                }
              }
            }
          }
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          return Errors.printMessage(S.TakeSmallestBy, rex, EvalEngine.get());
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3_0;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static final class TakeWhile extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      if (arg1.isAST()) {
        IAST list = (IAST) arg1;
        IASTAppendable result = F.ast(list.head(), F.allocMax32(list));
        list.forAll(x -> {
          if (engine.evalTrue(arg2, x)) {
            result.append(x);
            return true;
          }
          return false;
        }, 1);
        return result;
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   *
   *
   * <pre>
   * Total(list)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * adds all values in <code>list</code>.
   *
   * </blockquote>
   *
   * <pre>
   * Total(list, n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * adds all values up to level <code>n</code>.
   *
   * </blockquote>
   *
   * <pre>
   * Total(list, {n})
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * totals only the values at level <code>{n}</code>.
   *
   * </blockquote>
   *
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
  private static final class Total extends AbstractFunctionEvaluator {

    private static class TotalLevelSpecification extends VisitorLevelSpecification {
      public TotalLevelSpecification(final Function<IExpr, IExpr> function,
          final IExpr unevaledLevelExpr, boolean includeHeads, final EvalEngine engine) {
        super(function, unevaledLevelExpr, includeHeads, engine);
      }

      public TotalLevelSpecification(final Function<IExpr, IExpr> function, final int level,
          final boolean includeHeads) {
        super(function, level, includeHeads);
      }

      @Override
      public IASTMutable createResult(IAST ast, final IExpr x) {
        if (x.isASTOrAssociation()) {
          return ast.copy();
        }
        return ast.setAtCopy(0, S.Plus);
      }
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      VisitorLevelSpecification level = null;
      Function<IExpr, IExpr> tf = x -> x.isASTOrAssociation() ? ((IAST) x).setAtCopy(0, S.Plus) : x;

      if (ast.isAST2()) {
        level = new TotalLevelSpecification(tf, ast.arg2(), false, engine);
        // increment level because we select only subexpressions
      } else {
        level = new TotalLevelSpecification(tf, 1, false);
      }

      IExpr arg1 = ast.arg1();
      if (arg1.isSparseArray()) {
        ISparseArray sparseArray = (ISparseArray) arg1;
        int[] dims = sparseArray.getDimension();
        IExpr defaultValue = sparseArray.getDefaultValue();
        if (defaultValue.isZero()) {
          if (ast.isAST2()) {
            IExpr arg2 = ast.arg2();
            if (arg2.isInfinity() || arg2.toIntDefault() >= dims.length) {
              return sparseArray.total(S.Plus);
            }
          } else if (ast.isAST1()) {
            if (dims.length == 1) {
              return sparseArray.total(S.Plus);
            }
          }
        }
        arg1 = sparseArray.normal(false);
      }
      if (arg1.isASTOrAssociation()) {
        // increment level because we select only subexpressions
        level.incCurrentLevel();
        IExpr temp = ((IAST) arg1).copyAST().accept(level);
        if (temp.isPresent()) {
          try {
            if (temp.isListableAST() && temp.exists(x -> x.isList())) {
              IAST total = (IAST) temp;
              IAST resultList = engine.threadASTListArgs(total, S.Total, "tllen");
              if (resultList.isPresent()) {
                return engine.evaluate(resultList);
              } else {
                return F.NIL;
              }
            }
            return engine.evaluate(temp);
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            Errors.printMessage(S.Total, rex, EvalEngine.get());
            return F.NIL;
          }
        }
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   *
   *
   * <pre>
   * Union(set1, set2)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * get the union set from <code>set1</code> and <code>set2</code>.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="http://en.wikipedia.org/wiki/Union_(set_theory)">Wikipedia - Union (set
   * theory)</a><br>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Union({1,2,3},{2,3,4})
   * {1,2,3,4}
   * </pre>
   */
  private static final class Union extends AbstractFunctionOptionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, final int argSize, final IExpr[] option,
        final EvalEngine engine, IAST originalAST) {
      if (argSize > 0) {
        final BiPredicate<IExpr, IExpr> test = Predicates.sameTest(option[0], engine);
        SameTestComparator sameTest = new Comparators.SameTestComparator(test);
        if (argSize == 1) {
          if (ast.arg1().isASTOrAssociation()) {
            IAST arg1 = (IAST) ast.arg1();
            arg1 = EvalAttributes.copySort(arg1);
            Set<IExpr> set = arg1.asSortedSet(sameTest);
            if (set != null) {
              final IASTAppendable result = F.mapSet(set, x -> x);
              return result;
            }
          }
          return F.NIL;
        }

        if (ast.arg1().isASTOrAssociation()) {
          if (ast.exists(x -> !x.isASTOrAssociation(), 2)) {
            return F.NIL;
          }
          IAST result = ((IAST) ast.arg1());
          IExpr head1 = result.head();
          for (int i = 2; i < argSize + 1; i++) {
            IAST expr = (IAST) ast.get(i);
            if (!expr.head().equals(head1)) {
              // Heads `1` and `2` at positions `3` and `4` are expected to be the same.
              return Errors.printMessage(S.Union, "heads2",
                  F.List(expr.head(), head1, F.ZZ(i), F.ZZ(1)), engine);
            }
            if (option[0].equals(S.Automatic)) {
              result = union(head1, result, expr);
            } else {
              result = union(head1, result, expr, sameTest);
            }
          }
          return result;
        }
      }
      return F.NIL;
    }


    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.FLAT | ISymbol.ONEIDENTITY);
      setOptions(newSymbol, S.SameTest, S.Automatic);
    }
  }

  /**
   * Fold the list from <code>start</code> index including to <code>end</code> index excluding into
   * the <code>resultCollection</code>. If the <i>binaryFunction</i> returns <code>null</code>, the
   * left element will be added to the result list, otherwise the result will be <i>folded</i> again
   * with the next element in the list.
   *
   * @param expr initial value. If <code>null</code>use first element of list as initial value.
   * @param list
   * @param start
   * @param end
   * @param binaryFunction
   * @param resultCollection
   */
  public static IAST foldLeft(final IExpr expr, final IAST list, final int start, final int end,
      final BiFunction<IExpr, IExpr, ? extends IExpr> binaryFunction,
      final IASTAppendable resultCollection) {
    if (start < end) {
      IExpr elem;
      int from = start;
      if (expr != null) {
        elem = expr;
      } else {
        elem = list.get(from++);
      }
      resultCollection.append(elem);
      final IExpr[] temp = {elem};
      resultCollection.appendArgs(from, end, i -> {
        temp[0] = binaryFunction.apply(temp[0], list.get(i));
        return temp[0];
      });
    }
    return resultCollection;
  }

  /**
   * Assign the evaluated modified part to symbol.
   *
   * @param symbol
   * @param part the <code>Part(symbol,...)</code> expression
   * @param header <code>Append</code> or <code>Prepend</code>
   * @param ast
   * @param engine
   * @return
   */
  private static IExpr assignPartTo(ISymbol symbol, IAST part, IBuiltInSymbol header,
      final IAST ast, EvalEngine engine) {
    if (symbol.hasAssignedSymbolValue()) {
      IExpr arg2 = engine.evaluate(ast.arg2());
      IExpr partAppend = engine.evaluate(F.binaryAST2(header, part, arg2));
      engine.evaluate(F.Set(part, partAppend));
      return partAppend;
    }
    // `1` is not a variable with a value, so its value cannot be changed.
    return Errors.printMessage(ast.topHead(), "rvalue", F.list(symbol), engine);
  }

  /**
   * Create the (ordered) intersection set from both ASTs.
   *
   * @param ast1 first AST set
   * @param ast2 second AST set
   * @return the intersection set of the sets ast1 and ast2
   */
  public static IAST intersection(IExpr head, IAST ast1, IAST ast2) {
    return intersection(head, ast1, ast2, Comparators.CANONICAL_COMPARATOR);
  }

  /**
   * Create the (ordered) intersection set from both ASTs.
   * 
   * @param head
   * @param ast1 first AST set
   * @param ast2 second AST set
   * @param sameTest comparator for identifying the same objects
   * @return the intersection set of the sets ast1 and ast2 according to the <code>sameTest</code>
   *         comparator
   */
  public static IAST intersection(IExpr head, IAST ast1, IAST ast2, Comparator<IExpr> sameTest) {
    if (ast1.isEmpty() || ast2.isEmpty()) {
      if (head == S.List) {
        return F.CEmptyList;
      }
      return F.headAST0(head);
    }
    IAST smaller = ast1;
    IAST larger = ast2;
    if (smaller.size() > larger.size()) {
      IAST temp = smaller;
      smaller = larger;
      larger = temp;
    }
    SortedSet<IExpr> hashSet = smaller.asSortedSet(sameTest);
    IASTAppendable result = F.ast(head, Math.max(smaller.size() / 10, 2));
    IASTMutable largerCopy = larger.copy();
    largerCopy.sortInplace(Comparators.REVERSE_CANONICAL_COMPARATOR);
    for (final IExpr expr : largerCopy) {
      if (hashSet.contains(expr)) {
        result.append(expr);
        hashSet.remove(expr);
      }
    }
    return result;
  }


  /**
   * Create the (ordered) union from both ASTs.
   * 
   * @param ast1 first AST set
   * @param ast2 second AST set
   *
   * @return the union of the sets ast1 and ast2
   */
  public static IASTMutable union(IExpr newHead, IAST ast1, IAST ast2) {
    SortedSet<IExpr> resultSet = ast1.asSortedSet();
    int size = ast2.size();
    for (int i = 1; i < size; i++) {
      resultSet.add(ast2.get(i));
    }
    IASTAppendable result = F.ast(newHead, resultSet.size());
    result.appendAll(resultSet);
    return result;
  }

  /**
   * Create the (ordered) union from both ASTs.
   * 
   * @param ast1 first AST set
   * @param ast2 second AST set
   *
   * @return the union of the sets ast1 and ast2
   */
  public static IASTMutable union(IExpr newHead, IAST ast1, IAST ast2,
      SameTestComparator sameTest) {
    IASTAppendable unionList = F.ListAlloc(ast1.size() + ast2.size());
    unionList.appendArgs(ast1);
    unionList.appendArgs(ast2);
    unionList.sortInplace();
    SortedSet<IExpr> resultSet = unionList.asSortedSet(sameTest);
    IASTAppendable result = F.ast(newHead, resultSet.size());
    result.appendAll(resultSet);
    return result;
  }

  /**
   * Exclude <code>Indeterminate, Missing(), None, Null</code> and other symbolic expressions from
   * list.
   *
   * @param list
   * @return
   */
  private static IAST cleanList(IAST list) {
    return list.select(
        x -> !(x.isIndeterminate() || x.equals(S.Null) || x.equals(S.None) || x.isAST(S.Missing)));
  }

  /**
   * Sort the list of real numbers and return the n-th smallest element, or sort the values of an
   * association of real numbers and return the n-th smallest element.
   *
   * @param listOrAssociation list or association of real elements
   * @param n must be in the range (1..list.argSize())
   * @param ast
   * @param engine
   * @return {@link F#NIL} if not all elements are real
   */
  private static IExpr rankedMin(IAST listOrAssociation, int n, final IAST ast, EvalEngine engine) {
    // TODO choose better algorithm: https://www.baeldung.com/cs/k-smallest-numbers-array

    // check for non real elements (especially complex numbers)
    int quantities = 0;
    for (int i = 1; i < listOrAssociation.size(); i++) {
      IExpr element = listOrAssociation.getValue(i);
      if (element.isQuantity()) {
        quantities++;
        continue;
      }
      IReal r = element.evalReal();
      if (r == null //
          && !(element.isInfinity() || element.isNegativeInfinity())) {
        for (int j = i; j < listOrAssociation.size(); j++) {
          element = listOrAssociation.get(j);
          if (element.isComplexNumeric() || element.isComplex()) {
            // Input `1` is not a vector of reals or integers.
            return Errors.printMessage(ast.topHead(), "rvec", F.list(listOrAssociation), engine);
          }
        }
        return F.NIL;
      }
    }
    if (quantities > 0 && quantities != listOrAssociation.argSize()) {
      return F.NIL;
    }
    IASTMutable orderedList = listOrAssociation.copyAST();
    return EvalAttributes.copySortLess(orderedList).get(n);
  }

  /**
   * Reverse the elements in the given <code>list</code>.
   *
   * @param list
   * @return
   */
  public static IAST reverse(IAST list) {
    return list.reverse(F.ast(list.head(), list.size()));
  }

  private static IASTAppendable createResultList(java.util.Map<IExpr, Integer> map) {
    IASTAppendable result = F.ListAlloc(map.size());
    for (java.util.Map.Entry<IExpr, Integer> entry : map.entrySet()) {
      result.append(F.list(entry.getKey(), F.ZZ(entry.getValue())));
    }
    return result;
  }

  /**
   * Tallies the elements in <code>list</code>, listing all distinct elements together with their
   * multiplicities.
   * 
   * @param list
   * @return
   */
  public static IASTAppendable tally(IAST list) {
    java.util.Map<IExpr, Integer> map = new LinkedHashMap<IExpr, Integer>();
    for (int i = 1; i < list.size(); i++) {
      IExpr arg = list.get(i);
      Integer value = map.get(arg);
      map.put(arg, value == null ? 1 : value + 1);
    }
    return createResultList(map);
  }

  /**
   * Tallies the elements in <code>list</code>, listing all distinct elements together with their
   * multiplicities. Test is used for determining equivalent elements.
   * 
   * @param list
   * @param test
   * @return
   */
  public static IAST tally(IAST list, BiPredicate<IExpr, IExpr> test) {
    java.util.Map<IExpr, Integer> map = new LinkedHashMap<IExpr, Integer>();
    iLoop: for (int i = 1; i < list.size(); i++) {
      IExpr arg = list.get(i);
      for (java.util.Map.Entry<IExpr, Integer> entry : map.entrySet()) {
        if (test.test(entry.getKey(), arg)) {
          map.put(entry.getKey(), entry.getValue() + 1);
          continue iLoop;
        }
      }
      map.put(arg, 1);
    }
    return createResultList(map);
  }

  public static void initialize() {
    Initializer.init();
  }

  private ListFunctions() {}
}
