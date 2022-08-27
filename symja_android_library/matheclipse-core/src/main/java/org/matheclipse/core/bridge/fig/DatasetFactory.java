// code adapted from https://github.com/datahaki/bridge
package org.matheclipse.core.bridge.fig;

import java.util.function.Function;
import java.util.function.UnaryOperator;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.TimeTableXYDataset;
import org.jfree.data.xy.CategoryTableXYDataset;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;

/**
 * functionality to convert {@link VisualSet} to a dataset for the instantiation of a JFreeChart
 * object
 */
/* package */ class DatasetFactory {
  ;
  /**
   * Quote from the JFreeChart javadoc: "[XYSeries] represents a sequence of zero or more data items
   * in the form (x, y)."
   * 
   * @param visualSet
   * @return
   * @see ListPlot
   */
  public static XYSeriesCollection<String> xySeriesCollection(VisualSet visualSet) {
    XYSeriesCollection<String> xySeriesCollection = new XYSeriesCollection<>();
    UnaryOperator<IExpr> toRealsX = visualSet.getAxisX().toReals();
    UnaryOperator<IExpr> toRealsY = visualSet.getAxisY().toReals();
    for (VisualRow visualRow : visualSet.visualRows()) {
      String labelString = visualRow.getLabelString();
      XYSeries<String> xySeries = new XYSeries<>(labelString.isEmpty() //
          ? Integer.toString(xySeriesCollection.getSeriesCount())
          : labelString, //
          visualRow.getAutoSort());
      IAST points = visualRow.points();
      for (int i = 1; i < points.size(); i++) {
        IAST point = (IAST) points.get(i);
        Number numberX = toRealsX.apply(point.arg1()).toNumber();
        Number numberY = toRealsY.apply(point.arg2()).toNumber();
        xySeries.add( //
            numberX, //
            // infinity throws an IllegalArgumentException
            Double.isFinite(numberY.doubleValue()) //
                ? numberY
                : Double.NaN);
      }
      xySeriesCollection.addSeries(xySeries);
    }
    return xySeriesCollection;
  }

  /**
   * @param visualSet
   * @param naming for instance ISignedNumber::toString
   * @return
   */
  public static CategoryDataset<ComparableLabel, String> defaultCategoryDataset(VisualSet visualSet,
      Function<IExpr, String> naming) {
    // TODO BRIDGE may result in "unordered" domain depending on ordering of rows
    DefaultCategoryDataset<ComparableLabel, String> defaultCategoryDataset =
        new DefaultCategoryDataset<>();
    UnaryOperator<IExpr> toRealsY = visualSet.getAxisY().toReals();
    for (VisualRow visualRow : visualSet.visualRows()) {
      IAST points = visualRow.points();
      for (int i = 1; i < points.size(); i++) {
        IAST point = (IAST) points.get(i);
        defaultCategoryDataset.addValue( //
            toRealsY.apply(point.arg2()).toNumber(), //
            visualRow.getLabel(), //
            naming.apply(point.arg1()));
      }
    }
    return defaultCategoryDataset;
  }

  /**
   * Quote from the JFreeChart javadoc: "[...] The {@link TableXYDataset} interface requires all
   * series to share the same set of x-values. When adding a new item <code>(x, y)</code> to one
   * series, all other series automatically get a new item <code>(x, null)</code> unless a non-null
   * item has already been specified."
   * 
   * @param visualSet
   * @return
   */
  public static TableXYDataset timeTableXYDataset(VisualSet visualSet) {
    TimeTableXYDataset timeTableXYDataset = new TimeTableXYDataset();
    UnaryOperator<IExpr> toRealsY = visualSet.getAxisY().toReals();
    for (VisualRow visualRow : visualSet.visualRows()) {
      IAST points = visualRow.points();
      for (int i = 1; i < points.size(); i++) {
        IAST point = (IAST) points.get(i);
        timeTableXYDataset.add( //
            StaticHelper.timePeriod((ISignedNumber) point.arg1()), //
            toRealsY.apply(point.arg2()).toNumber(), //
            visualRow.getLabel(), //
            true);
      }
    }
    return timeTableXYDataset;
  }

  public static TableXYDataset categoryTableXYDataset(VisualSet visualSet) {
    CategoryTableXYDataset categoryTableXYDataset = new CategoryTableXYDataset();
    UnaryOperator<IExpr> toRealsX = visualSet.getAxisX().toReals();
    UnaryOperator<IExpr> toRealsY = visualSet.getAxisY().toReals();
    for (VisualRow visualRow : visualSet.visualRows()) {
      String label = visualRow.getLabelString().isEmpty() //
          ? String.valueOf(categoryTableXYDataset.getSeriesCount())
          : visualRow.getLabelString();
      IAST points = visualRow.points();
      for (int i = 1; i < points.size(); i++) {
        IAST point = (IAST) points.get(i);
        categoryTableXYDataset.add( //
            toRealsX.apply(point.arg1()).toNumber(), //
            toRealsY.apply(point.arg2()).toNumber(), //
            label, //
            true); // requires string, might lead to overwriting
      }
    }
    return categoryTableXYDataset;
  }
}
