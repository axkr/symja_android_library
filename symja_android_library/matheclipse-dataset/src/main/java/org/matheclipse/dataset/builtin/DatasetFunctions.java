package org.matheclipse.dataset.builtin;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTDataset;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.dataset.expression.ASTDataset;
import org.matheclipse.dataset.expression.DatasetOptions;
import tech.tablesaw.aggregate.AggregateFunction;
import tech.tablesaw.aggregate.AggregateFunctions;
import tech.tablesaw.api.Table;

public class DatasetFunctions {
  private static final Logger LOGGER = LogManager.getLogger(DatasetFunctions.class);

  /**
   * The display options of <code>Dataset</code>.
   *
   * <p>
   * They are accepted and validated but do not yet change the rendering - the table still prints
   * the way <code>tech.tablesaw</code> prints it. Accepting them first means a notebook written
   * against the reference does not fail to parse here, and the styling can land without another
   * change to the symbol table.
   */
  private static final IBuiltInSymbol[] DATASET_OPTIONS = new IBuiltInSymbol[] { //
      S.Alignment, S.AllowedDimensions, S.Background, S.DatasetDisplayFormat, S.DatasetTheme,
      S.HeaderAlignment, S.HeaderBackground, S.HeaderDisplayFunction, S.HeaderSize, S.HeaderStyle,
      S.HiddenItems, S.ItemDisplayFunction, S.ItemSize, S.ItemStyle, S.MaxItems};

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      if (Config.FILESYSTEM_ENABLED) {
        S.Dataset.setEvaluator(new Dataset());
        S.ToDataset.setEvaluator(new ToDataset());
        S.FromDataset.setEvaluator(new FromDataset());
        S.JoinAcross.setEvaluator(new JoinAcross());
        S.AggregateBy.setEvaluator(new AggregateBy());
        S.TableView.setEvaluator(new TableView());
      }
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  /**
   * <code>Dataset</code> is two functions sharing one symbol: it builds a dataset from an
   * association, and it is the head under which <code>dataset[row, column]</code> selection
   * arrives, because the evaluator is reached through {@link IExpr#topHead()}.
   */
  private static class Dataset extends AbstractEvaluator {

    public Dataset() {}

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      if (ast.isAST1() && ast.arg1().isList()) {
        if (((IAST) ast.arg1()).forAll(x -> x.isAssociation())) {
          // return DataSetExpr.newInstance((IAST) ast.arg1());
        }
      }
      if (ast.head().isDataset()) {
        IASTDataset dataSet = (IASTDataset) ast.head();
        IExpr arg1 = ast.arg1();
        try {
          if (ast.isAST1() && arg1.isAST(S.Query)) {
            // dataset[Query[op1, op2, …]] is dataset[op1, op2, …] - the same query, written the
            // other way round. Query's own evaluator answers Query[…][dataset] the same way
            IAST specs = (IAST) arg1;
            if (specs.argSize() == 0) {
              return dataSet;
            }
            IASTAppendable query = F.ast(dataSet, specs.argSize());
            query.appendArgs(specs);
            return engine.evaluate(query);
          }
          IExpr result;
          if (ast.isAST1()) {
            // A string in the only slot is a key, not a column. On a dataset built from an
            // association of associations it names the row with that key; on one built from a list
            // of associations there are no keys to name, and the reference answers with a Failure
            // rather than guessing a column - dataset[All, "column"] is how a column is asked for.
            if (arg1.isString()) {
              IExpr keyed = selectByRowKey(dataSet, arg1);
              return keyed.isPresent() ? keyed : partNotApplicable(arg1);
            }
            result = dataSet.select(arg1, S.All);
          } else if (ast.isAST2()) {
            result = dataSet.select(arg1, ast.arg2());
          } else {
            result = dataSet.select(ast);
          }
          if (result.isPresent()) {
            return result;
          }
          IExpr arg2 = S.All;
          if (ast.size() >= 3) {
            arg2 = ast.arg2();
          }

          final IExpr rowFunction = arg2;
          if (arg1 == S.All && ast.isAST2() && isApplicable(rowFunction)) {
            // dataset[All, f] applies f to each row - the chapter's dataset[All, Total] and
            // dataset[All, PieChart]. Reached only when the second slot was not a column
            // specification, because select() is asked first and answers those.
            return applyToRows(dataSet, rowFunction);
          }

          if (arg1 != S.All) {
            if (arg1.isBuiltInSymbol() || arg1.isAST(S.TakeLargest, 2)
                || arg1.isAST(S.TakeLargestBy, 3)) {
              IExpr expr = dataSet.select(S.All, arg2);
              if (expr.isDataset()) {
                return IASTDataset
                    .restoreDataset(engine.evaluate(F.unaryAST1(arg1, expr.normal(false))));
              }
              IExpr absent = columnNotPresent(ast, arg2);
              if (absent.isPresent()) {
                return absent;
              }
            } else {
              // an operator form - Select(test), SortBy(f), TakeLargestBy(f, n). Some of them know
              // what a dataset is and answer with one; the rest are asked again with the rows,
              // which is what SortBy(f) needs and what left dataset[SortBy(f)] unevaluated before.
              IExpr expr = engine.evaluate(F.unaryAST1(arg1, dataSet));
              if (expr.isDataset()) {
                return ((IASTDataset) expr).select(S.All, arg2);
              }
              if (ast.isAST1() && !expr.isAST(arg1)) {
                // The operator form answered with something that is not a dataset - a count, a
                // total - and with no column asked for, that answer is the whole result. It used
                // to be computed and then dropped, leaving dataset[Count[_]] unevaluated.
                // Checking the head keeps the guard below intact: an operator form which could not
                // be answered comes back as itself applied to the rows, and still falls through.
                return expr;
              }
              if (wantsRows(arg1)) {
                IExpr selected = dataSet.select(S.All, arg2);
                if (selected.isDataset()) {
                  return IASTDataset.restoreDataset(engine.evaluate(
                      F.unaryAST1(arg1, ((IASTDataset) selected).normal(false))));
                }
              }
              if (arg1.isSymbol() || arg1.isAST(S.Function)) {
                // A name or a pure function is something to apply to the selection, which is what
                // `dataset[f, "column"]` means - and what the branch above already does when the
                // name happens to be a built-in one. Without this a function of one's own was
                // simply not applied: `dataset[Total, "z"]` answered while `dataset[f, "z"]`
                // stayed unevaluated.
                //
                // An operator form is deliberately not applied here. `Select(test)` and its
                // relatives were offered the dataset above; one which could not be answered - a
                // test naming a column which is not in the table - has already been reported, and
                // applying it to the rows anyway would turn that into an empty selection which
                // looks like an answer.
                IExpr column = dataSet.select(S.All, arg2);
                if (column.isDataset()) {
                  return IASTDataset.restoreDataset(
                      engine.evaluate(F.unaryAST1(arg1, ((IASTDataset) column).normal(false))));
                }
                IExpr absent = columnNotPresent(ast, arg2);
                if (absent.isPresent()) {
                  return absent;
                }
              }
            }
          }
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
          // the query could not be applied to this dataset; the reference reports that as a
          // Failure object rather than by leaving the expression unevaluated
          return queryFailure(ast, rex);
        }
        return F.NIL;
      }
      if (ast.head() == S.Dataset) {
        int argSize = optionsStart(ast, S.Dataset, engine);
        if (argSize < 0) {
          return F.NIL;
        }
        if (argSize == 1) {
          IExpr dataset = construct(ast.arg1());
          if (dataset.isNIL()) {
            return F.NIL;
          }
          return applyOptions((ASTDataset) dataset, ast, argSize, S.Dataset, engine);
        }
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      IAST defaults = F.mapRange(0, DATASET_OPTIONS.length,
          i -> F.Rule(DATASET_OPTIONS[i], S.Automatic));
      setOptions(newSymbol, defaults);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY_0;
    }
  }

  /**
   * <code>ToDataset(expr)</code> - the constructor as a named function, so that it can be mapped
   * and composed where the <code>Dataset(...)</code> head cannot, and so that a list of lists with
   * a header row has a way in.
   */
  private static class ToDataset extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isDataset()) {
        return arg1;
      }
      // the header-and-rows form first: a list of lists is now also a legitimate dataset of a
      // vector of vectors, and construct would answer with that and never reach this
      IExpr headed = fromHeaderAndRows(arg1);
      if (headed.isPresent()) {
        return headed;
      }
      return construct(arg1);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /** <code>FromDataset(dataset)</code> - the inverse of {@link ToDataset}, i.e. <code>Normal</code>. */
  private static class FromDataset extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isDataset()) {
        return ((IASTDataset) arg1).normal(false);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   * <code>JoinAcross(dataset1, dataset2)</code>, <code>JoinAcross(dataset1, dataset2, key)</code>,
   * <code>JoinAcross(dataset1, dataset2, key, type)</code> - a relational join, where
   * <code>type</code> is <code>"Inner"</code>, <code>"Left"</code>, <code>"Right"</code> or
   * <code>"Outer"</code>.
   *
   * <p>
   * Without a <code>key</code> the columns the two datasets have in common are used, which is what
   * a natural join does.
   */
  private static class JoinAcross extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // JoinAcross is a list operation in the reference, which datasets inherit - so a list of
      // associations is joined the same way and gives a list back. Only the dataset form worked
      // here, which is the opposite way round from every other function on this page.
      IExpr arg1 = IASTDataset.restoreDataset(ast.arg1());
      IExpr arg2 = IASTDataset.restoreDataset(ast.arg2());
      boolean plainLists = !ast.arg1().isDataset() && !ast.arg2().isDataset();
      Table left = tableOf(arg1);
      Table right = tableOf(arg2);
      if (left == null || right == null) {
        return F.NIL;
      }
      try {
        String[] keys;
        if (ast.size() >= 4) {
          keys = columnNames(ast.arg3());
          if (keys == null || keys.length == 0) {
            return F.NIL;
          }
        } else {
          List<String> common = new ArrayList<String>(left.columnNames());
          common.retainAll(right.columnNames());
          if (common.isEmpty()) {
            // The datasets have no column in common to join on.
            return Errors.printMessage(S.JoinAcross, "nojoin", F.CEmptyList, engine);
          }
          keys = common.toArray(new String[0]);
        }

        String type = ast.size() >= 5 ? ast.arg4().toString() : "Inner";
        Table joined;
        switch (type) {
          case "Inner":
            joined = left.joinOn(keys).inner(right, keys);
            break;
          case "Left":
            joined = left.joinOn(keys).leftOuter(right, keys);
            break;
          case "Right":
            joined = left.joinOn(keys).rightOuter(right, keys);
            break;
          case "Outer":
            // fullOuter has no (Table, String[]) overload, only the fully explicit one
            joined = left.joinOn(keys).fullOuter(right, false, false, keys);
            break;
          default:
            // `1` is not a known join type.
            return Errors.printMessage(S.JoinAcross, "jointype", F.list(ast.arg4()), engine);
        }
        ASTDataset result = ASTDataset.newTablesawTable(joined);
        // a list in gives a list out, a dataset in gives a dataset out
        return plainLists ? result.normal(false) : result;
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        return Errors.printMessage(S.JoinAcross, rex, engine);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_4;
    }
  }

  /**
   * <code>AggregateBy(dataset, groupColumns, valueColumn, aggregator)</code> - group the rows by
   * <code>groupColumns</code> and reduce <code>valueColumn</code> in each group.
   *
   * <p>
   * <code>aggregator</code> is one of <code>Mean</code>, <code>Total</code>, <code>Max</code>,
   * <code>Min</code>, <code>Median</code>, <code>Length</code>, <code>CountDistinct</code>,
   * <code>StandardDeviation</code>, <code>Variance</code>, <code>First</code> or
   * <code>Last</code>.
   */
  private static class AggregateBy extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      Table table = tableOf(ast.arg1());
      if (table == null) {
        return F.NIL;
      }
      String[] groups = columnNames(ast.arg2());
      if (groups == null || groups.length == 0 || !ast.arg3().isString()) {
        return F.NIL;
      }
      AggregateFunction<?, ?> function = aggregator(ast.arg4());
      if (function == null) {
        // `1` is not a known aggregator.
        return Errors.printMessage(S.AggregateBy, "aggspec", F.list(ast.arg4()), engine);
      }
      try {
        return ASTDataset
            .newTablesawTable(table.summarize(ast.arg3().toString(), function).by(groups));
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        return Errors.printMessage(S.AggregateBy, rex, engine);
      }
    }

    private static AggregateFunction<?, ?> aggregator(IExpr spec) {
      if (spec == S.Mean) {
        return AggregateFunctions.mean;
      } else if (spec == S.Total) {
        return AggregateFunctions.sum;
      } else if (spec == S.Max) {
        return AggregateFunctions.max;
      } else if (spec == S.Min) {
        return AggregateFunctions.min;
      } else if (spec == S.Median) {
        return AggregateFunctions.median;
      } else if (spec == S.Length) {
        return AggregateFunctions.count;
      } else if (spec == S.CountDistinct) {
        return AggregateFunctions.countUnique;
      } else if (spec == S.StandardDeviation) {
        return AggregateFunctions.standardDeviation;
      } else if (spec == S.Variance) {
        return AggregateFunctions.variance;
      } else if (spec == S.First) {
        return AggregateFunctions.first;
      } else if (spec == S.Last) {
        return AggregateFunctions.last;
      }
      return null;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_4_4;
    }
  }

  /**
   * <code>TableView(expr)</code>, <code>TableView(expr, n)</code> - show <code>expr</code> as a
   * table, at most <code>n</code> rows of it.
   *
   * <p>
   * The reference's <code>TableView</code> is an interactive, scrolling view. There is no front end
   * here to scroll in, so this is the static part of it: the paging is a plain truncation, which is
   * what makes a large dataset readable in a console or a notebook cell.
   */
  private static class TableView extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (!arg1.isDataset()) {
        IExpr dataset = construct(arg1);
        if (dataset.isNIL()) {
          dataset = fromHeaderAndRows(arg1);
        }
        if (dataset.isNIL()) {
          return F.NIL;
        }
        arg1 = dataset;
      }
      if (ast.isAST1()) {
        return arg1;
      }
      int rows = ast.arg2().toIntDefault();
      if (rows < 0) {
        return F.NIL;
      }
      Table table = tableOf(arg1);
      if (table == null) {
        return F.NIL;
      }
      return ASTDataset.newTablesawTable(table.first(Math.min(rows, table.rowCount())));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  // ---------------------------------------------------------------- helpers

  /** Build a <code>Dataset</code> from the forms the <code>Dataset(...)</code> head accepts. */
  private static IExpr construct(IExpr arg1) {
    if (arg1.isList()) {
      IAST list = (IAST) arg1;
      // the empty one first: forAll below is vacuously true of it, and would read it as a list of
      // no rows rather than as what it is, a collection of no values
      if (list.argSize() == 0) {
        return ASTDataset.newVector(list);
      }
      if (list.forAll(x -> x.isAssociation())) {
        return ASTDataset.newListOfAssociations(list);
      }
      // a bare list of values - the reference's Dataset[{3, 7, 11}]
      return ASTDataset.newVector(list);
    } else if (arg1.isAssociation()) {
      IAssociation assoc = (IAssociation) arg1;
      if (assoc.argSize() == 0) {
        return ASTDataset.newAssociation(assoc);
      }
      if (assoc.forAll(x -> x.isRuleAST() && x.second().isAssociation())) {
        return ASTDataset.newAssociationOfAssociations(assoc);
      }
      // a bare association - the reference's Dataset[<|"a" -> 3|>]
      return ASTDataset.newAssociation(assoc);
    }
    return F.NIL;
  }

  /**
   * Build a <code>Dataset</code> from <code>{{header...}, {row...}, ...}</code>. The
   * <code>Dataset(...)</code> head has no form for this, which is the main reason
   * <code>ToDataset</code> exists.
   */
  private static IExpr fromHeaderAndRows(IExpr arg1) {
    if (!arg1.isList() || arg1.size() < 2) {
      return F.NIL;
    }
    IAST rows = (IAST) arg1;
    if (!rows.arg1().isList()) {
      return F.NIL;
    }
    IAST header = (IAST) rows.arg1();
    if (header.argSize() == 0 || !header.forAll(x -> x.isString())) {
      return F.NIL;
    }
    for (int i = 2; i < rows.size(); i++) {
      if (!rows.get(i).isList() || rows.get(i).size() != header.size()) {
        return F.NIL;
      }
    }
    IAST listOfAssociations = F.mapRange(1, rows.size() - 1, i -> {
      IAST row = (IAST) rows.get(i + 1);
      IAssociation assoc = F.assoc();
      for (int j = 1; j < header.size(); j++) {
        assoc.appendRule(F.Rule(header.get(j), row.get(j)));
      }
      return assoc;
    });
    return ASTDataset.newListOfAssociations(listOfAssociations);
  }

  /**
   * The row a key names, for a dataset built from an association of associations.
   *
   * <p>
   * Those datasets carry the outer keys in a first column of their own, named with the empty
   * string, so the row is the one whose key column holds <code>key</code> - with that column taken
   * out again, because it named the row rather than being data in it.
   *
   * @return the row, or {@link F#NIL} when this dataset has no keys or has no such key
   */
  private static IExpr selectByRowKey(IASTDataset dataSet, IExpr key) {
    if (dataSet instanceof ASTDataset
        && ((ASTDataset) dataSet).getShape() == ASTDataset.Shape.ASSOCIATION) {
      // a bare association: the key names a value, and it comes back bare
      return dataSet.getValue(key, () -> F.NIL);
    }
    IASTAppendable names = dataSet.columnNames();
    if (names.argSize() == 0 || !names.arg1().isString() || !names.arg1().toString().isEmpty()) {
      return F.NIL;
    }
    // the rows of this dataset have names, so `normal` gives them back keyed by those names and
    // the key names one of them
    IExpr rows = dataSet.normal(false);
    if (!rows.isAssociation()) {
      return F.NIL;
    }
    IExpr row = ((IAssociation) rows).getValue(key, () -> F.NIL);
    // a row of a dataset is a dataset - the reference answers `dataset["a"]` with
    // `Dataset[<|"x" -> 1, …|>]` and not with the bare association. A scalar still comes back
    // bare, which is what restoreDataset does with anything that is not a collection.
    return row.isPresent() ? IASTDataset.restoreDataset(row) : F.NIL;
  }

  /**
   * The report for a second slot that names a column which is not there.
   *
   * <p>
   * That slot is a column specification wherever it appears: <code>dataset[Total, 1]</code> totals
   * column 1 and <code>dataset[RandomSample, 1]</code> shuffles it. So a number past the last
   * column names nothing, and saying so beats leaving the query unevaluated - which is what a
   * reader saw once the index stopped throwing IndexOutOfBoundsException.
   *
   * @return the failure, or {@link F#NIL} when this slot was not a column specification at all and
   *         so has some other reading
   */
  private static IExpr columnNotPresent(IAST ast, IExpr arg2) {
    if (ast.size() >= 3 && arg2 != S.All && !isApplicable(arg2)) {
      return partNotApplicable(arg2);
    }
    return F.NIL;
  }

  /**
   * What the reference answers when a part specification does not apply to a dataset -
   * <code>dataset["x"]</code> on a dataset of rows, where <code>"x"</code> names no row.
   */
  private static IExpr partNotApplicable(IExpr part) {
    IAssociation parameters = F.assoc();
    parameters.appendRule(F.Rule(F.$str("Part"), part));
    parameters.appendRule(F.Rule(F.$str("Symbol"), S.Part));
    IAssociation failure = F.assoc();
    failure.appendRule(F.Rule(F.$str("MessageTemplate"),
        F.$str("The part specification `1` is not applicable to this dataset.")));
    failure.appendRule(F.Rule(F.$str("MessageParameters"), parameters));
    return F.binaryAST2(S.Failure, S.Dataset, failure);
  }

  /**
   * Whether this operator form is one to hand the rows to.
   *
   * <p>
   * A list rather than "anything that did not answer with a dataset", and deliberately. Several
   * operators do understand a dataset, and when one of them declines it has usually declined for a
   * reason - <code>dataset[Select(#Missing &lt; 13000 &amp;)]</code> reports that there is no such
   * column and stays unevaluated. Retrying that against the rows turns the report into an empty
   * list, which looks like an answer. Only operators known to want rows are asked twice.
   */
  private static boolean wantsRows(IExpr operator) {
    return operator.isAST(S.SortBy, 2);
  }

  /**
   * Apply <code>rowFunction</code> to every row.
   *
   * <p>
   * The rows of a dataset built from an association of associations have names, and
   * <code>normal</code> gives them back keyed by those names; the function then runs on each row
   * and the names key the result.
   */
  private static IExpr applyToRows(IASTDataset dataSet, IExpr rowFunction) {
    IExpr rows = dataSet.normal(false);
    if (rows.isAssociation()) {
      // the rows have names: the function runs on each row and the names key the result, which is
      // how `Dataset[<|"a" -> <|"x" -> 1, …|>, …|>][All, Total]` comes to be `<|"a" -> 6, …|>`
      IAssociation assoc = (IAssociation) rows;
      IAssociation result = F.assoc();
      for (int i = 1; i < assoc.size(); i++) {
        IAST rule = assoc.getRule(i);
        result.appendRule(F.Rule(rule.first(), F.unaryAST1(rowFunction, rule.second())));
      }
      return IASTDataset.restoreDataset(EvalEngine.get().evaluate(result));
    }
    if (!rows.isList()) {
      return F.unaryAST1(rowFunction, rows);
    }
    IAST rowList = (IAST) rows;
    // a dataset back, as the reference gives: Head[dataset[All, Total]] is Dataset
    return IASTDataset.restoreDataset(EvalEngine.get()
        .evaluate(F.mapRange(1, rowList.size(), i -> F.unaryAST1(rowFunction, rowList.get(i)))));
  }

  /**
   * Whether this is something <code>dataset[All, …]</code> can apply to a row, as against a column
   * specification. A column is named by a string, a number, a list of either, or <code>All</code>;
   * anything else in that slot is a function.
   */
  private static boolean isApplicable(IExpr expr) {
    if (expr == S.All || expr.isString() || expr.isNumber() || expr.isList() || expr.isAST(S.Span)) {
      return false;
    }
    return expr.isSymbol() || expr.isAST() || expr.isFunction();
  }

  /** The tablesaw table behind a <code>Dataset</code>, or <code>null</code>. */
  private static Table tableOf(IExpr expr) {
    return (expr instanceof ASTDataset) ? ((ASTDataset) expr).toData() : null;
  }

  /** A column name or a list of them, as strings. */
  private static String[] columnNames(IExpr expr) {
    if (expr.isString()) {
      return new String[] {expr.toString()};
    }
    if (expr.isList() && expr.size() > 1) {
      IAST list = (IAST) expr;
      String[] names = new String[list.argSize()];
      for (int i = 0; i < names.length; i++) {
        names[i] = list.get(i + 1).toString();
      }
      return names;
    }
    return null;
  }

  /**
   * Attach the option rules that follow <code>argSize</code> to the dataset, after checking the one
   * of them that constrains the data rather than its appearance.
   */
  private static IExpr applyOptions(ASTDataset dataset, IAST ast, int argSize, IBuiltInSymbol head,
      EvalEngine engine) {
    IASTAppendable rules = F.ListAlloc(ast.argSize() - argSize);
    for (int i = argSize + 1; i < ast.size(); i++) {
      IExpr arg = ast.get(i);
      if (arg.isRuleAST()) {
        rules.append(arg);
      } else if (arg.isList()) {
        rules.appendArgs((IAST) arg);
      }
    }
    if (rules.isEmpty()) {
      return dataset;
    }
    Table table = dataset.toData();
    IExpr violation = DatasetOptions.allowedDimensionsViolation(rules, table.rowCount(),
        table.columnCount());
    if (violation.isPresent()) {
      // The data has dimensions `1` which `2` does not allow.
      Errors.printMessage(head, "dsdims",
          F.list(F.list(F.ZZ(table.rowCount()), F.ZZ(table.columnCount())), violation), engine);
      return F.NIL;
    }
    return dataset.withOptions(rules);
  }

  /**
   * The index one past the last non-option argument, or <code>-1</code> if an option is not one of
   * {@link #DATASET_OPTIONS}.
   */
  private static int optionsStart(IAST ast, IBuiltInSymbol head, EvalEngine engine) {
    int argSize = ast.argSize();
    while (argSize >= 1) {
      IExpr arg = ast.get(argSize);
      if (arg.isRuleAST()) {
        if (!isKnownOption(arg)) {
          // Unknown option `1` in `2`.
          Errors.printMessage(head, "optx", F.list(arg, ast), engine);
          return -1;
        }
        argSize--;
      } else if (arg.isList() && arg.size() > 1 && ((IAST) arg).forAll(x -> x.isRuleAST())) {
        IAST list = (IAST) arg;
        for (int i = 1; i < list.size(); i++) {
          if (!isKnownOption(list.get(i))) {
            Errors.printMessage(head, "optx", F.list(list.get(i), ast), engine);
            return -1;
          }
        }
        argSize--;
      } else {
        break;
      }
    }
    return argSize;
  }

  private static boolean isKnownOption(IExpr rule) {
    IExpr lhs = rule.first();
    for (int i = 0; i < DATASET_OPTIONS.length; i++) {
      if (lhs == DATASET_OPTIONS[i]) {
        return true;
      }
    }
    return false;
  }

  /**
   * The reference answers a query it cannot apply with a <code>Failure</code> object rather than by
   * leaving the expression unevaluated, which makes the difference between "no rule matched" and
   * "the query is wrong" visible to the caller.
   */
  private static IExpr queryFailure(IAST ast, RuntimeException rex) {
    // Everything in here is inert. Putting the offending expression in unquoted made the Failure
    // re-evaluate it, which threw again, which built another Failure around it - a recursion the
    // engine only stopped at $RecursionLimit. The query is recorded as text instead, and without
    // the dataset itself, which would print its whole table into the message.
    IASTAppendable query = F.ListAlloc(ast.argSize());
    for (int i = 1; i < ast.size(); i++) {
      query.append(F.$str(ast.get(i).toString()));
    }
    IAssociation assoc = F.assoc();
    assoc.appendRule(
        F.Rule(F.$str("MessageTemplate"), F.$str("Cannot apply the query `1` to the dataset.")));
    assoc.appendRule(F.Rule(F.$str("MessageParameters"), query));
    String message = rex.getMessage();
    assoc.appendRule(
        F.Rule(F.$str("Message"), F.$str(message != null ? message : rex.getClass().getName())));
    return F.binaryAST2(S.Failure, F.$str("DatasetQueryFailure"), assoc);
  }

  private DatasetFunctions() {}
}
