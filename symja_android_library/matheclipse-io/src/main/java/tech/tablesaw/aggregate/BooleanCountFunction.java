package tech.tablesaw.aggregate;

import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.ColumnType;

/**
 * A partial implementation of an AggregateFunction that returns an Integer value when applied to a
 * Boolean Column
 *
 * @deprecated Use {@link BooleanIntAggregateFunction} instead
 */
@Deprecated
abstract class BooleanCountFunction extends AggregateFunction<BooleanColumn, Integer> {

  /**
   * Constructs a BooleanCountFunction with the given name. The name may be used to name a column in
   * the output when this function is used by {@link Summarizer}
   */
  public BooleanCountFunction(String functionName) {
    super(functionName);
  }

  /** Returns an Integer as a result of applying this function to the given column */
  @Override
  public abstract Integer summarize(BooleanColumn column);

  /** {@inheritDoc} */
  @Override
  public boolean isCompatibleColumn(ColumnType type) {
    return type.equals(ColumnType.BOOLEAN);
  }

  /** {@inheritDoc} */
  @Override
  public ColumnType returnType() {
    return ColumnType.DOUBLE;
  }
}
