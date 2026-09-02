package org.matheclipse.core.interfaces;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.function.Supplier;

public interface IASTDataset extends IASTMutable {

  /**
   * The rows of <code>expr</code> when it is a <code>Dataset</code>, and <code>expr</code> itself
   * otherwise.
   *
   * <p>
   * A built-in that walks a collection structurally - through <code>copyAST().accept(visitor)</code>
   * or <code>setAtCopy</code> or <code>apply</code> - has to call this on its argument first. A
   * <code>Dataset</code> is an {@link IAST}, so that machinery treats it as one: it reads
   * <code>get(int)</code> happily and then calls <code>set(int, IExpr)</code>, which a dataset
   * cannot answer and which throws. <code>Total</code> and <code>Apply</code> aborted the whole
   * evaluation that way, with a message of "null"; <code>Map</code> quietly produced nonsense.
   *
   * <p>
   * The rows are what those functions want in any case: <code>Total</code> of a dataset is the
   * total of its rows, which is what <code>Total(Normal(dataset))</code> has always given.
   *
   * <p>
   * The same shape of trap caught <code>ASTSeriesData</code> before this, and <code>Total</code>
   * still carries the branch that was written for it.
   */
  /**
   * Builds a <code>Dataset</code> from a list of associations. Installed by
   * <code>org.matheclipse.dataset.DatasetInit</code>; <code>null</code> when
   * <code>matheclipse-dataset</code> is not on the classpath, in which case a result that would
   * have been re-wrapped stays the plain list it already is.
   */
  java.util.function.Function<IExpr, IExpr>[] ROW_FACTORY = new java.util.function.Function[1];

  /** Install the factory. Called from <code>DatasetInit</code>. */
  static void installRowFactory(java.util.function.Function<IExpr, IExpr> factory) {
    ROW_FACTORY[0] = factory;
  }

  /**
   * Run this call against the rows of a <code>Dataset</code> first argument, and give a
   * <code>Dataset</code> back.
   *
   * <p>
   * <code>Take</code>, <code>Reverse</code>, <code>Rest</code> and the rest of that family build
   * their result with the head of what they were given, which for a dataset produced
   * <code>Dataset(row, row)</code> - a dataset head wrapped round one-row datasets. Asking the same
   * question of the rows and wrapping the answer gives what the reference gives:
   * <code>Head[Take[dataset, 2]]</code> is <code>Dataset</code> and
   * <code>Normal[Take[dataset, 2]]</code> is the two rows.
   *
   * <p>
   * The re-dispatch cannot loop: the rows are a plain list, so the second call takes the ordinary
   * path.
   *
   * @return the result, or {@link org.matheclipse.core.expression.F#NIL} when the first argument is
   *         not a dataset and the caller should carry on as usual
   */
  static IExpr onDatasetRows(IAST ast, org.matheclipse.core.eval.EvalEngine engine) {
    if (ast.size() < 2 || !ast.arg1().isDataset()) {
      return org.matheclipse.core.expression.F.NIL;
    }
    IAST call = ast;
    // every dataset argument, not only the first: Union[dataset1, dataset2] asks the same question
    // of two sets of rows, and one of them left as a dataset would be read as a structure
    for (int i = 1; i < ast.size(); i++) {
      IExpr argument = ast.get(i);
      if (argument.isDataset()) {
        IExpr rows = ((IASTDataset) argument).normal(false);
        if (i > 1 && !rows.isList()) {
          // A dataset of one row normalizes to that row - an association, not a list of one. That
          // is right for the thing being operated on, and wrong for a collection being combined
          // with it: Union[dataset, oneRowDataset] would be a list against an association, whose
          // heads do not match. Only the arguments after the first are wrapped, so nothing that
          // reads its own first argument changes.
          rows = org.matheclipse.core.expression.F.List(rows);
        }
        call = call.setAtCopy(i, rows);
      }
    }
    return restoreDataset(engine.evaluate(call));
  }

  /**
   * The result as a <code>Dataset</code> when it is a list of associations, and as it stands
   * otherwise.
   *
   * <p>
   * A dataset wraps a collection - a list of rows, a bare list of values, an association - and a
   * scalar comes back bare, which is the rule the reference follows: <code>Total</code> of a
   * dataset of numbers is a number and not a dataset of one.
   */
  static IExpr restoreDataset(IExpr result) {
    if (ROW_FACTORY[0] == null || !(result.isList() || result.isAssociation())) {
      // never an atom: a dataset wraps a collection, and the reference gives a scalar back bare -
      // Total of a dataset of numbers is a number, not a dataset of one
      return result;
    }
    IExpr dataset = ROW_FACTORY[0].apply(result);
    return dataset.isPresent() ? dataset : result;
  }

  static IExpr normalizeDataset(IExpr expr) {
    return expr.isDataset() ? ((IASTDataset) expr).normal(false) : expr;
  }

  public IASTAppendable columnNames();

  public void csv(Writer writer) throws IOException;

  /**
   * Return the value associated to the <code>key</code>. If no value is available return <code>
   * Missing("KeyAbsent", key)</code>
   *
   * @param key
   * @return
   */
  public IExpr getValue(IExpr key);

  /**
   * Return the value associated to the <code>key</code>. If no value is available return the <code>
   * defaultValue</code>
   *
   * @param key
   * @param defaultValue
   * @return
   */
  public IExpr getValue(IExpr key, Supplier<IExpr> defaultValue);

  public IExpr groupBy(List<String> group);

  /**
   * Select the row and column of a dataset.
   *
   * @param row
   * @param column
   * @return
   */
  public IExpr select(IExpr row, IExpr column);

  /**
   * Select the row and column and parts of a dataset.
   *
   * @param ast
   * @return
   */
  public IExpr select(IAST ast);

  /**
   * <code>n</code> rows of this dataset chosen at random, as a dataset. When <code>n</code> is at
   * least the row count every row is returned, in a random order.
   *
   * @param random the engine's generator, passed in rather than taken from anywhere here so that
   *        <code>SeedRandom</code> governs this the way it governs <code>RandomSample</code> of a
   *        list. Tablesaw's own <code>Table.sampleN</code> samples from a static generator of its
   *        own and must not be used for this.
   */
  public IASTDataset randomSample(int n, java.util.Random random);

  /**
   * How many rows there are to draw from, so that a caller can tell a sample that is too large from
   * one that fits without normalizing the whole dataset to count it.
   */
  public int rowCount();

  public IASTDataset structure();

  public IASTDataset summary();

  public String datasetToJSForm() throws IOException;
}
