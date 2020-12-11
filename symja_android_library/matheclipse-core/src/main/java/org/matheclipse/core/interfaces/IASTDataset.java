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

  public IExpr select(IExpr row, IExpr column);

  public IASTDataset structure();

  public IASTDataset summary();

  public String datasetToJSForm() throws IOException;
}
