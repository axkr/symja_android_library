package org.matheclipse.core.interfaces;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.function.Supplier;

public interface IASTDataset extends IASTMutable {
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

  public IASTDataset structure();

  public IASTDataset summary();

  public String datasetToJSForm() throws IOException;
}
