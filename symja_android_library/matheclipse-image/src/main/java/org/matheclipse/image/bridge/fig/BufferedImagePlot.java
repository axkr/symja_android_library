// code adapted from https://github.com/datahaki/bridge
package org.matheclipse.image.bridge.fig;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.AxisCollection;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.Pannable;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PlotState;
import org.jfree.chart.plot.SeriesRenderingOrder;
import org.jfree.chart.plot.ValueAxisPlot;
import org.jfree.chart.plot.Zoomable;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.chart.util.Args;
import org.jfree.chart.util.ShadowGenerator;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetUtils;
import org.jfree.data.xy.XYDataset;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.tensor.img.ImageResize;
import org.matheclipse.core.tensor.sca.Clip;

/** plot of a {@link BufferedImage} */
public class BufferedImagePlot extends Plot implements ValueAxisPlot, Pannable, Zoomable {
  /** The default grid line stroke. */
  public static final Stroke DEFAULT_GRIDLINE_STROKE = //
      new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0.0f,
          new float[] {2.0f, 2.0f}, 0.0f);
  /** The default grid line paint. */
  public static final Paint DEFAULT_GRIDLINE_PAINT = Color.LIGHT_GRAY;
  /** The resourceBundle for the localization. */
  protected static ResourceBundle localizationResources =
      ResourceBundle.getBundle("org.jfree.chart.plot.LocalizationBundle");
  /** The plot orientation. */
  private PlotOrientation orientation;
  /** The offset between the data area and the axes. */
  private RectangleInsets axisOffset;
  /** The domain axis / axes (used for the x-values). */
  private final NumberAxis domainAxis;
  /** The domain axis locations. */
  private final Map<Integer, AxisLocation> domainAxisLocations;
  /** The range axis (used for the y-values). */
  private final NumberAxis rangeAxis;
  /** The range axis location. */
  private final Map<Integer, AxisLocation> rangeAxisLocations;
  /** Storage for the datasets. */
  private final XYDataset xyDataset;
  /** Storage for the renderers. */
  private final XYItemRenderer xyItemRenderer;
  /**
   * Storage for the mapping between datasets/renderers and domain axes. The keys in the map are
   * Integer objects, corresponding to the dataset index. The values in the map are List objects
   * containing Integer objects (corresponding to the axis indices). If the map contains no entry
   * for a dataset, it is assumed to map to the primary domain axis (index = 0).
   */
  private final Map<Integer, List<Integer>> datasetToDomainAxesMap;
  /**
   * Storage for the mapping between datasets/renderers and range axes. The keys in the map are
   * Integer objects, corresponding to the dataset index. The values in the map are List objects
   * containing Integer objects (corresponding to the axis indices). If the map contains no entry
   * for a dataset, it is assumed to map to the primary domain axis (index = 0).
   */
  private final Map<Integer, List<Integer>> datasetToRangeAxesMap;
  /** A flag that controls whether the domain grid-lines are visible. */
  private boolean domainGridlinesVisible;
  /** The stroke used to draw the domain grid-lines. */
  private transient Stroke domainGridlineStroke;
  /** The paint used to draw the domain grid-lines. */
  private transient Paint domainGridlinePaint;
  /** A flag that controls whether the range grid-lines are visible. */
  private boolean rangeGridlinesVisible;
  /** The stroke used to draw the range grid-lines. */
  private transient Stroke rangeGridlineStroke;
  /** The paint used to draw the range grid-lines. */
  private transient Paint rangeGridlinePaint;
  /** A flag that controls whether the domain minor grid-lines are visible. */
  private boolean domainMinorGridlinesVisible;
  /** The stroke used to draw the domain minor grid-lines. */
  private transient Stroke domainMinorGridlineStroke;
  /** The paint used to draw the domain minor grid-lines. */
  private transient Paint domainMinorGridlinePaint;
  /** A flag that controls whether the range minor grid-lines are visible. */
  private boolean rangeMinorGridlinesVisible;
  /** The stroke used to draw the range minor grid-lines. */
  private transient Stroke rangeMinorGridlineStroke;
  /** The paint used to draw the range minor grid-lines. */
  private transient Paint rangeMinorGridlinePaint;
  /**
   * A flag that controls whether or not the zero baseline against the domain axis is visible.
   */
  private boolean domainZeroBaselineVisible;
  /** The stroke used for the zero baseline against the domain axis. */
  private transient Stroke domainZeroBaselineStroke;
  /** The paint used for the zero baseline against the domain axis. */
  private transient Paint domainZeroBaselinePaint;
  /**
   * A flag that controls whether or not the zero baseline against the range axis is visible.
   */
  private boolean rangeZeroBaselineVisible;
  /** The stroke used for the zero baseline against the range axis. */
  private transient Stroke rangeZeroBaselineStroke;
  /** The paint used for the zero baseline against the range axis. */
  private transient Paint rangeZeroBaselinePaint;
  /** The fixed domain axis space. */
  private AxisSpace fixedDomainAxisSpace;
  /** The fixed range axis space. */
  private AxisSpace fixedRangeAxisSpace;
  /**
   * The order of the dataset rendering (REVERSE draws the primary dataset last so that it appears
   * to be on top).
   */
  private DatasetRenderingOrder datasetRenderingOrder = DatasetRenderingOrder.REVERSE;
  /**
   * The order of the series rendering (REVERSE draws the primary series last so that it appears to
   * be on top).
   */
  private SeriesRenderingOrder seriesRenderingOrder = SeriesRenderingOrder.REVERSE;
  /**
   * The weight for this plot (only relevant if this is a subplot in a combined plot).
   */
  private int weight;
  /**
   * A flag that controls whether or not panning is enabled for the domain axis/axes.
   */
  private boolean domainPannable;
  /**
   * A flag that controls whether or not panning is enabled for the range axis/axes.
   */
  private boolean rangePannable;
  /** The shadow generator ({@code null} permitted). */
  private ShadowGenerator shadowGenerator;
  private final VisualImage visualImage;
  private final Clip clipX;
  private final Clip clipY;

  public BufferedImagePlot(VisualImage visualImage) {
    this.visualImage = visualImage;
    {
      VisualSet visualSet = new VisualSet();
      clipX = visualImage.getAxisX().getOptionalClip().orElseThrow();
      clipY = visualImage.getAxisY().getOptionalClip().orElseThrow();
      visualSet.add( //
          F.List(clipX.min(), clipX.max()), //
          F.List(clipY.min(), clipY.max()));
      xyDataset = DatasetFactory.xySeriesCollection(visualSet);
    }
    {
      Axis axis = visualImage.getAxisX();
      domainAxis = new NumberAxis(axis.getAxisLabel());
      StaticHelper.setRange(axis, domainAxis);
      domainAxis.setTickLabelFont(domainAxis.getTickLabelFont().deriveFont(12f));
    }
    {
      Axis axis = visualImage.getAxisY();
      rangeAxis = new NumberAxis(axis.getAxisLabel());
      StaticHelper.setRange(axis, rangeAxis);
      rangeAxis.setTickLabelFont(rangeAxis.getTickLabelFont().deriveFont(12f));
    }
    xyItemRenderer = new XYLineAndShapeRenderer(false, false);
    this.orientation = PlotOrientation.VERTICAL;
    this.weight = 1; // only relevant when this is a subplot
    this.axisOffset = new RectangleInsets(4, 4, 4, 4); // seems to be the default of XYPlot
    // allocate storage for datasets, axes and renderers (all optional)
    this.domainAxisLocations = new HashMap<>();
    this.rangeAxisLocations = new HashMap<>();
    this.datasetToDomainAxesMap = new TreeMap<>();
    this.datasetToRangeAxesMap = new TreeMap<>();
    // dataset.addChangeListener(this);
    mapDatasetToDomainAxis(0, 0);
    domainAxis.setPlot(this);
    domainAxis.addChangeListener(this);
    this.domainAxisLocations.put(0, AxisLocation.BOTTOM_OR_LEFT);
    mapDatasetToRangeAxis(0, 0);
    rangeAxis.setPlot(this);
    rangeAxis.addChangeListener(this);
    // ---
    this.rangeAxisLocations.put(0, AxisLocation.BOTTOM_OR_LEFT);
    configureDomainAxes();
    configureRangeAxes();
    this.domainGridlinesVisible = true;
    this.domainGridlineStroke = DEFAULT_GRIDLINE_STROKE;
    this.domainGridlinePaint = DEFAULT_GRIDLINE_PAINT;
    this.domainMinorGridlinesVisible = false;
    this.domainMinorGridlineStroke = DEFAULT_GRIDLINE_STROKE;
    this.domainMinorGridlinePaint = Color.WHITE;
    this.domainZeroBaselineVisible = false;
    this.domainZeroBaselinePaint = Color.BLACK;
    this.domainZeroBaselineStroke = new BasicStroke(0.5f);
    this.rangeGridlinesVisible = true;
    this.rangeGridlineStroke = DEFAULT_GRIDLINE_STROKE;
    this.rangeGridlinePaint = DEFAULT_GRIDLINE_PAINT;
    this.rangeMinorGridlinesVisible = false;
    this.rangeMinorGridlineStroke = DEFAULT_GRIDLINE_STROKE;
    this.rangeMinorGridlinePaint = Color.WHITE;
    this.rangeZeroBaselineVisible = false;
    this.rangeZeroBaselinePaint = Color.BLACK;
    this.rangeZeroBaselineStroke = new BasicStroke(0.5f);
    this.shadowGenerator = null;
  }

  /**
   * Returns the plot type as a string.
   *
   * @return A short string describing the type of plot.
   */
  @Override
  public String getPlotType() {
    return localizationResources.getString("XY_Plot");
  }

  /**
   * Returns the orientation of the plot.
   *
   * @return The orientation (never {@code null}).
   *
   * @see #setOrientation(PlotOrientation)
   */
  @Override
  public PlotOrientation getOrientation() {
    return this.orientation;
  }

  /**
   * Sets the orientation for the plot and sends a {@link PlotChangeEvent} to all registered
   * listeners.
   *
   * @param orientation the orientation ({@code null} not allowed).
   *
   * @see #getOrientation()
   */
  public void setOrientation(PlotOrientation orientation) {
    Args.nullNotPermitted(orientation, "orientation");
    if (orientation != this.orientation) {
      this.orientation = orientation;
      fireChangeEvent();
    }
  }

  /**
   * Returns the axis offset.
   *
   * @return The axis offset (never {@code null}).
   *
   * @see #setAxisOffset(RectangleInsets)
   */
  public RectangleInsets getAxisOffset() {
    return this.axisOffset;
  }

  /**
   * Sets the axis offsets (gap between the data area and the axes) and sends a
   * {@link PlotChangeEvent} to all registered listeners.
   *
   * @param offset the offset ({@code null} not permitted).
   *
   * @see #getAxisOffset()
   */
  public void setAxisOffset(RectangleInsets offset) {
    Args.nullNotPermitted(offset, "offset");
    this.axisOffset = offset;
    fireChangeEvent();
  }

  /**
   * Returns the domain axis with index 0. If the domain axis for this plot is {@code null}, then
   * the method will return the parent plot's domain axis (if there is a parent plot).
   *
   * @return The domain axis (possibly {@code null}).
   *
   * @see #getDomainAxis(int)
   * @see #setDomainAxis(ValueAxis)
   */
  public ValueAxis getDomainAxis() {
    return domainAxis;
  }

  /**
   * Returns the location of the primary domain axis.
   *
   * @return The location (never {@code null}).
   *
   * @see #setDomainAxisLocation(AxisLocation)
   */
  public AxisLocation getDomainAxisLocation() {
    return this.domainAxisLocations.get(0);
  }

  /**
   * Sets the location of the primary domain axis and sends a {@link PlotChangeEvent} to all
   * registered listeners.
   *
   * @param location the location ({@code null} not permitted).
   *
   * @see #getDomainAxisLocation()
   */
  public void setDomainAxisLocation(AxisLocation location) {
    // delegate...
    setDomainAxisLocation(0, location, true);
  }

  /**
   * Sets the location of the domain axis and, if requested, sends a {@link PlotChangeEvent} to all
   * registered listeners.
   *
   * @param location the location ({@code null} not permitted).
   * @param notify notify listeners?
   *
   * @see #getDomainAxisLocation()
   */
  public void setDomainAxisLocation(AxisLocation location, boolean notify) {
    // delegate...
    setDomainAxisLocation(0, location, notify);
  }

  /**
   * Returns the edge for the primary domain axis (taking into account the plot's orientation).
   *
   * @return The edge.
   *
   * @see #getDomainAxisLocation()
   * @see #getOrientation()
   */
  public RectangleEdge getDomainAxisEdge() {
    return Plot.resolveDomainAxisLocation(getDomainAxisLocation(), this.orientation);
  }

  /** Configures the domain axes. */
  public void configureDomainAxes() {
    domainAxis.configure();
  }

  /**
   * Returns the location for a domain axis. If this hasn't been set explicitly, the method returns
   * the location that is opposite to the primary domain axis location.
   *
   * @param index the axis index (must be &gt;= 0).
   *
   * @return The location (never {@code null}).
   *
   * @see #setDomainAxisLocation(int, AxisLocation)
   */
  public AxisLocation getDomainAxisLocation(int index) {
    AxisLocation result = this.domainAxisLocations.get(index);
    if (result == null) {
      result = AxisLocation.getOpposite(getDomainAxisLocation());
    }
    return result;
  }

  /**
   * Sets the location for a domain axis and sends a {@link PlotChangeEvent} to all registered
   * listeners.
   *
   * @param index the axis index.
   * @param location the location ({@code null} not permitted for index 0).
   *
   * @see #getDomainAxisLocation(int)
   */
  public void setDomainAxisLocation(int index, AxisLocation location) {
    // delegate...
    setDomainAxisLocation(index, location, true);
  }

  /**
   * Sets the axis location for a domain axis and, if requested, sends a {@link PlotChangeEvent} to
   * all registered listeners.
   *
   * @param index the axis index (must be &gt;= 0).
   * @param location the location ({@code null} not permitted for index 0).
   * @param notify notify listeners?
   *
   * @see #getDomainAxisLocation(int)
   * @see #setRangeAxisLocation(int, AxisLocation, boolean)
   */
  public void setDomainAxisLocation(int index, AxisLocation location, boolean notify) {
    if (index == 0 && location == null) {
      throw new IllegalArgumentException("Null 'location' for index 0 not permitted.");
    }
    this.domainAxisLocations.put(index, location);
    if (notify) {
      fireChangeEvent();
    }
  }

  /**
   * Returns the edge for a domain axis.
   *
   * @param index the axis index.
   *
   * @return The edge.
   *
   * @see #getRangeAxisEdge(int)
   */
  public RectangleEdge getDomainAxisEdge(int index) {
    AxisLocation location = getDomainAxisLocation(index);
    return Plot.resolveDomainAxisLocation(location, this.orientation);
  }

  /**
   * Returns the range axis for the plot. If the range axis for this plot is {@code null}, then the
   * method will return the parent plot's range axis (if there is a parent plot).
   *
   * @return The range axis.
   *
   * @see #getRangeAxis(int)
   * @see #setRangeAxis(ValueAxis)
   */
  public ValueAxis getRangeAxis() {
    return rangeAxis;
  }

  /**
   * Returns the location of the primary range axis.
   *
   * @return The location (never {@code null}).
   *
   * @see #setRangeAxisLocation(AxisLocation)
   */
  public AxisLocation getRangeAxisLocation() {
    return this.rangeAxisLocations.get(0);
  }

  /**
   * Sets the location of the primary range axis and sends a {@link PlotChangeEvent} to all
   * registered listeners.
   *
   * @param location the location ({@code null} not permitted).
   *
   * @see #getRangeAxisLocation()
   */
  public void setRangeAxisLocation(AxisLocation location) {
    // delegate...
    setRangeAxisLocation(0, location, true);
  }

  /**
   * Sets the location of the primary range axis and, if requested, sends a {@link PlotChangeEvent}
   * to all registered listeners.
   *
   * @param location the location ({@code null} not permitted).
   * @param notify notify listeners?
   *
   * @see #getRangeAxisLocation()
   */
  public void setRangeAxisLocation(AxisLocation location, boolean notify) {
    // delegate...
    setRangeAxisLocation(0, location, notify);
  }

  /**
   * Returns the edge for the primary range axis.
   *
   * @return The range axis edge.
   *
   * @see #getRangeAxisLocation()
   * @see #getOrientation()
   */
  public RectangleEdge getRangeAxisEdge() {
    return Plot.resolveRangeAxisLocation(getRangeAxisLocation(), this.orientation);
  }

  public void configureRangeAxes() {
    rangeAxis.configure();
  }

  /**
   * Returns the location for a range axis. If this hasn't been set explicitly, the method returns
   * the location that is opposite to the primary range axis location.
   *
   * @param index the axis index (must be &gt;= 0).
   *
   * @return The location (never {@code null}).
   *
   * @see #setRangeAxisLocation(int, AxisLocation)
   */
  public AxisLocation getRangeAxisLocation(int index) {
    AxisLocation result = this.rangeAxisLocations.get(index);
    if (result == null) {
      result = AxisLocation.getOpposite(getRangeAxisLocation());
    }
    return result;
  }

  /**
   * Sets the location for a range axis and sends a {@link PlotChangeEvent} to all registered
   * listeners.
   *
   * @param index the axis index.
   * @param location the location ({@code null} permitted).
   *
   * @see #getRangeAxisLocation(int)
   */
  public void setRangeAxisLocation(int index, AxisLocation location) {
    // delegate...
    setRangeAxisLocation(index, location, true);
  }

  /**
   * Sets the axis location for a domain axis and, if requested, sends a {@link PlotChangeEvent} to
   * all registered listeners.
   *
   * @param index the axis index.
   * @param location the location ({@code null} not permitted for index 0).
   * @param notify notify listeners?
   *
   * @see #getRangeAxisLocation(int)
   * @see #setDomainAxisLocation(int, AxisLocation, boolean)
   */
  public void setRangeAxisLocation(int index, AxisLocation location, boolean notify) {
    if (index == 0 && location == null) {
      throw new IllegalArgumentException("Null 'location' for index 0 not permitted.");
    }
    this.rangeAxisLocations.put(index, location);
    if (notify) {
      fireChangeEvent();
    }
  }

  /**
   * Returns the edge for a range axis.
   *
   * @param index the axis index.
   *
   * @return The edge.
   *
   * @see #getRangeAxisLocation(int)
   * @see #getOrientation()
   */
  public RectangleEdge getRangeAxisEdge(int index) {
    AxisLocation location = getRangeAxisLocation(index);
    return Plot.resolveRangeAxisLocation(location, this.orientation);
  }

  /**
   * Returns the primary dataset for the plot.
   *
   * @return The primary dataset (possibly {@code null}).
   *
   * @see #getDataset(int)
   * @see #setDataset(XYDataset)
   */
  public XYDataset getDataset() {
    return xyDataset;
  }

  /**
   * Maps a dataset to a particular domain axis. All data will be plotted against axis zero by
   * default, no mapping is required for this case.
   *
   * @param index the dataset index (zero-based).
   * @param axisIndex the axis index.
   *
   * @see #mapDatasetToRangeAxis(int, int)
   */
  public void mapDatasetToDomainAxis(int index, int axisIndex) {
    List<Integer> axisIndices = new ArrayList<>(1);
    axisIndices.add(axisIndex);
    mapDatasetToDomainAxes(index, axisIndices);
  }

  /**
   * Maps the specified dataset to the axes in the list. Note that the conversion of data values
   * into Java2D space is always performed using the first axis in the list.
   *
   * @param index the dataset index (zero-based).
   * @param axisIndices the axis indices ({@code null} permitted).
   */
  public void mapDatasetToDomainAxes(int index, List<Integer> axisIndices) {
    Args.requireNonNegative(index, "index");
    checkAxisIndices(axisIndices);
    Integer key = index;
    this.datasetToDomainAxesMap.put(key, new ArrayList<>(axisIndices));
    // fake a dataset change event to update axes...
    datasetChanged(new DatasetChangeEvent(this, getDataset()));
  }

  /**
   * Maps a dataset to a particular range axis. All data will be plotted against axis zero by
   * default, no mapping is required for this case.
   *
   * @param index the dataset index (zero-based).
   * @param axisIndex the axis index.
   *
   * @see #mapDatasetToDomainAxis(int, int)
   */
  public void mapDatasetToRangeAxis(int index, int axisIndex) {
    List<Integer> axisIndices = new ArrayList<>(1);
    axisIndices.add(axisIndex);
    mapDatasetToRangeAxes(index, axisIndices);
  }

  /**
   * Maps the specified dataset to the axes in the list. Note that the conversion of data values
   * into Java2D space is always performed using the first axis in the list.
   *
   * @param index the dataset index (zero-based).
   * @param axisIndices the axis indices ({@code null} permitted).
   */
  public void mapDatasetToRangeAxes(int index, List<Integer> axisIndices) {
    Args.requireNonNegative(index, "index");
    checkAxisIndices(axisIndices);
    Integer key = index;
    this.datasetToRangeAxesMap.put(key, new ArrayList<>(axisIndices));
    // fake a dataset change event to update axes...
    datasetChanged(new DatasetChangeEvent(this, getDataset()));
  }

  /**
   * This method is used to perform argument checking on the list of axis indices passed to
   * mapDatasetToDomainAxes() and mapDatasetToRangeAxes().
   *
   * @param indices the list of indices ({@code null} permitted).
   */
  private static void checkAxisIndices(List<Integer> indices) {
    // axisIndices can be:
    // 1. null;
    // 2. non-empty, containing only Integer objects that are unique.
    if (indices == null) {
      return; // OK
    }
    int count = indices.size();
    if (count == 0) {
      throw new IllegalArgumentException("Empty list not permitted.");
    }
    Set<Integer> set = new HashSet<>();
    for (Integer item : indices) {
      if (set.contains(item)) {
        throw new IllegalArgumentException("Indices must be unique.");
      }
      set.add(item);
    }
  }

  /**
   * Returns the renderer for the primary dataset.
   *
   * @return The item renderer (possibly {@code null}).
   *
   * @see #setRenderer(XYItemRenderer)
   */
  public XYItemRenderer getRenderer() {
    return xyItemRenderer;
  }

  /**
   * Returns the dataset rendering order.
   *
   * @return The order (never {@code null}).
   *
   * @see #setDatasetRenderingOrder(DatasetRenderingOrder)
   */
  public DatasetRenderingOrder getDatasetRenderingOrder() {
    return this.datasetRenderingOrder;
  }

  /**
   * Sets the rendering order and sends a {@link PlotChangeEvent} to all registered listeners. By
   * default, the plot renders the primary dataset last (so that the primary dataset overlays the
   * secondary datasets). You can reverse this if you want to.
   *
   * @param order the rendering order ({@code null} not permitted).
   *
   * @see #getDatasetRenderingOrder()
   */
  public void setDatasetRenderingOrder(DatasetRenderingOrder order) {
    Args.nullNotPermitted(order, "order");
    this.datasetRenderingOrder = order;
    fireChangeEvent();
  }

  /**
   * Returns the series rendering order.
   *
   * @return the order (never {@code null}).
   *
   * @see #setSeriesRenderingOrder(SeriesRenderingOrder)
   */
  public SeriesRenderingOrder getSeriesRenderingOrder() {
    return this.seriesRenderingOrder;
  }

  /**
   * Sets the series order and sends a {@link PlotChangeEvent} to all registered listeners. By
   * default, the plot renders the primary series last (so that the primary series appears to be on
   * top). You can reverse this if you want to.
   *
   * @param order the rendering order ({@code null} not permitted).
   *
   * @see #getSeriesRenderingOrder()
   */
  public void setSeriesRenderingOrder(SeriesRenderingOrder order) {
    Args.nullNotPermitted(order, "order");
    this.seriesRenderingOrder = order;
    fireChangeEvent();
  }

  /**
   * Returns the weight for this plot when it is used as a subplot within a combined plot.
   *
   * @return The weight.
   *
   * @see #setWeight(int)
   */
  public int getWeight() {
    return this.weight;
  }

  /**
   * Sets the weight for the plot and sends a {@link PlotChangeEvent} to all registered listeners.
   *
   * @param weight the weight.
   *
   * @see #getWeight()
   */
  public void setWeight(int weight) {
    this.weight = weight;
    fireChangeEvent();
  }

  /**
   * Returns {@code true} if the domain gridlines are visible, and {@code false} otherwise.
   *
   * @return {@code true} or {@code false}.
   *
   * @see #setDomainGridlinesVisible(boolean)
   */
  public boolean isDomainGridlinesVisible() {
    return this.domainGridlinesVisible;
  }

  /**
   * Sets the flag that controls whether or not the domain grid-lines are visible.
   * <p>
   * If the flag value is changed, a {@link PlotChangeEvent} is sent to all registered listeners.
   *
   * @param visible the new value of the flag.
   *
   * @see #isDomainGridlinesVisible()
   */
  public void setDomainGridlinesVisible(boolean visible) {
    if (this.domainGridlinesVisible != visible) {
      this.domainGridlinesVisible = visible;
      fireChangeEvent();
    }
  }

  /**
   * Returns {@code true} if the domain minor gridlines are visible, and {@code false} otherwise.
   *
   * @return {@code true} or {@code false}.
   *
   * @see #setDomainMinorGridlinesVisible(boolean)
   */
  public boolean isDomainMinorGridlinesVisible() {
    return this.domainMinorGridlinesVisible;
  }

  /**
   * Sets the flag that controls whether or not the domain minor grid-lines are visible.
   * <p>
   * If the flag value is changed, a {@link PlotChangeEvent} is sent to all registered listeners.
   *
   * @param visible the new value of the flag.
   *
   * @see #isDomainMinorGridlinesVisible()
   */
  public void setDomainMinorGridlinesVisible(boolean visible) {
    if (this.domainMinorGridlinesVisible != visible) {
      this.domainMinorGridlinesVisible = visible;
      fireChangeEvent();
    }
  }

  /**
   * Returns the stroke for the grid-lines (if any) plotted against the domain axis.
   *
   * @return The stroke (never {@code null}).
   *
   * @see #setDomainGridlineStroke(Stroke)
   */
  public Stroke getDomainGridlineStroke() {
    return this.domainGridlineStroke;
  }

  /**
   * Sets the stroke for the grid lines plotted against the domain axis, and sends a
   * {@link PlotChangeEvent} to all registered listeners.
   *
   * @param stroke the stroke ({@code null} not permitted).
   *
   * @throws IllegalArgumentException if {@code stroke} is {@code null}.
   *
   * @see #getDomainGridlineStroke()
   */
  public void setDomainGridlineStroke(Stroke stroke) {
    Args.nullNotPermitted(stroke, "stroke");
    this.domainGridlineStroke = stroke;
    fireChangeEvent();
  }

  /**
   * Returns the stroke for the minor grid-lines (if any) plotted against the domain axis.
   *
   * @return The stroke (never {@code null}).
   *
   * @see #setDomainMinorGridlineStroke(Stroke)
   */
  public Stroke getDomainMinorGridlineStroke() {
    return this.domainMinorGridlineStroke;
  }

  /**
   * Sets the stroke for the minor grid lines plotted against the domain axis, and sends a
   * {@link PlotChangeEvent} to all registered listeners.
   *
   * @param stroke the stroke ({@code null} not permitted).
   *
   * @throws IllegalArgumentException if {@code stroke} is {@code null}.
   *
   * @see #getDomainMinorGridlineStroke()
   */
  public void setDomainMinorGridlineStroke(Stroke stroke) {
    Args.nullNotPermitted(stroke, "stroke");
    this.domainMinorGridlineStroke = stroke;
    fireChangeEvent();
  }

  /**
   * Returns the paint for the grid lines (if any) plotted against the domain axis.
   *
   * @return The paint (never {@code null}).
   *
   * @see #setDomainGridlinePaint(Paint)
   */
  public Paint getDomainGridlinePaint() {
    return this.domainGridlinePaint;
  }

  /**
   * Sets the paint for the grid lines plotted against the domain axis, and sends a
   * {@link PlotChangeEvent} to all registered listeners.
   *
   * @param paint the paint ({@code null} not permitted).
   *
   * @throws IllegalArgumentException if {@code Paint} is {@code null}.
   *
   * @see #getDomainGridlinePaint()
   */
  public void setDomainGridlinePaint(Paint paint) {
    Args.nullNotPermitted(paint, "paint");
    this.domainGridlinePaint = paint;
    fireChangeEvent();
  }

  /**
   * Returns the paint for the minor grid lines (if any) plotted against the domain axis.
   *
   * @return The paint (never {@code null}).
   *
   * @see #setDomainMinorGridlinePaint(Paint)
   */
  public Paint getDomainMinorGridlinePaint() {
    return this.domainMinorGridlinePaint;
  }

  /**
   * Sets the paint for the minor grid lines plotted against the domain axis, and sends a
   * {@link PlotChangeEvent} to all registered listeners.
   *
   * @param paint the paint ({@code null} not permitted).
   *
   * @throws IllegalArgumentException if {@code Paint} is {@code null}.
   *
   * @see #getDomainMinorGridlinePaint()
   */
  public void setDomainMinorGridlinePaint(Paint paint) {
    Args.nullNotPermitted(paint, "paint");
    this.domainMinorGridlinePaint = paint;
    fireChangeEvent();
  }

  /**
   * Returns {@code true} if the range axis grid is visible, and {@code false} otherwise.
   *
   * @return A boolean.
   *
   * @see #setRangeGridlinesVisible(boolean)
   */
  public boolean isRangeGridlinesVisible() {
    return this.rangeGridlinesVisible;
  }

  /**
   * Sets the flag that controls whether or not the range axis grid lines are visible.
   * <p>
   * If the flag value is changed, a {@link PlotChangeEvent} is sent to all registered listeners.
   *
   * @param visible the new value of the flag.
   *
   * @see #isRangeGridlinesVisible()
   */
  public void setRangeGridlinesVisible(boolean visible) {
    if (this.rangeGridlinesVisible != visible) {
      this.rangeGridlinesVisible = visible;
      fireChangeEvent();
    }
  }

  /**
   * Returns the stroke for the grid lines (if any) plotted against the range axis.
   *
   * @return The stroke (never {@code null}).
   *
   * @see #setRangeGridlineStroke(Stroke)
   */
  public Stroke getRangeGridlineStroke() {
    return this.rangeGridlineStroke;
  }

  /**
   * Sets the stroke for the grid lines plotted against the range axis, and sends a
   * {@link PlotChangeEvent} to all registered listeners.
   *
   * @param stroke the stroke ({@code null} not permitted).
   *
   * @see #getRangeGridlineStroke()
   */
  public void setRangeGridlineStroke(Stroke stroke) {
    Args.nullNotPermitted(stroke, "stroke");
    this.rangeGridlineStroke = stroke;
    fireChangeEvent();
  }

  /**
   * Returns the paint for the grid lines (if any) plotted against the range axis.
   *
   * @return The paint (never {@code null}).
   *
   * @see #setRangeGridlinePaint(Paint)
   */
  public Paint getRangeGridlinePaint() {
    return this.rangeGridlinePaint;
  }

  /**
   * Sets the paint for the grid lines plotted against the range axis and sends a
   * {@link PlotChangeEvent} to all registered listeners.
   *
   * @param paint the paint ({@code null} not permitted).
   *
   * @see #getRangeGridlinePaint()
   */
  public void setRangeGridlinePaint(Paint paint) {
    Args.nullNotPermitted(paint, "paint");
    this.rangeGridlinePaint = paint;
    fireChangeEvent();
  }

  /**
   * Returns {@code true} if the range axis minor grid is visible, and {@code false} otherwise.
   *
   * @return A boolean.
   *
   * @see #setRangeMinorGridlinesVisible(boolean)
   */
  public boolean isRangeMinorGridlinesVisible() {
    return this.rangeMinorGridlinesVisible;
  }

  /**
   * Sets the flag that controls whether or not the range axis minor grid lines are visible.
   * <p>
   * If the flag value is changed, a {@link PlotChangeEvent} is sent to all registered listeners.
   *
   * @param visible the new value of the flag.
   *
   * @see #isRangeMinorGridlinesVisible()
   */
  public void setRangeMinorGridlinesVisible(boolean visible) {
    if (this.rangeMinorGridlinesVisible != visible) {
      this.rangeMinorGridlinesVisible = visible;
      fireChangeEvent();
    }
  }

  /**
   * Returns the stroke for the minor grid lines (if any) plotted against the range axis.
   *
   * @return The stroke (never {@code null}).
   *
   * @see #setRangeMinorGridlineStroke(Stroke)
   */
  public Stroke getRangeMinorGridlineStroke() {
    return this.rangeMinorGridlineStroke;
  }

  /**
   * Sets the stroke for the minor grid lines plotted against the range axis, and sends a
   * {@link PlotChangeEvent} to all registered listeners.
   *
   * @param stroke the stroke ({@code null} not permitted).
   *
   * @see #getRangeMinorGridlineStroke()
   */
  public void setRangeMinorGridlineStroke(Stroke stroke) {
    Args.nullNotPermitted(stroke, "stroke");
    this.rangeMinorGridlineStroke = stroke;
    fireChangeEvent();
  }

  /**
   * Returns the paint for the minor grid lines (if any) plotted against the range axis.
   *
   * @return The paint (never {@code null}).
   *
   * @see #setRangeMinorGridlinePaint(Paint)
   */
  public Paint getRangeMinorGridlinePaint() {
    return this.rangeMinorGridlinePaint;
  }

  /**
   * Sets the paint for the minor grid lines plotted against the range axis and sends a
   * {@link PlotChangeEvent} to all registered listeners.
   *
   * @param paint the paint ({@code null} not permitted).
   *
   * @see #getRangeMinorGridlinePaint()
   */
  public void setRangeMinorGridlinePaint(Paint paint) {
    Args.nullNotPermitted(paint, "paint");
    this.rangeMinorGridlinePaint = paint;
    fireChangeEvent();
  }

  /**
   * Returns a flag that controls whether or not a zero baseline is displayed for the domain axis.
   *
   * @return A boolean.
   *
   * @see #setDomainZeroBaselineVisible(boolean)
   */
  public boolean isDomainZeroBaselineVisible() {
    return this.domainZeroBaselineVisible;
  }

  /**
   * Sets the flag that controls whether or not the zero baseline is displayed for the domain axis,
   * and sends a {@link PlotChangeEvent} to all registered listeners.
   *
   * @param visible the flag.
   *
   * @see #isDomainZeroBaselineVisible()
   */
  public void setDomainZeroBaselineVisible(boolean visible) {
    this.domainZeroBaselineVisible = visible;
    fireChangeEvent();
  }

  /**
   * Returns the stroke used for the zero baseline against the domain axis.
   *
   * @return The stroke (never {@code null}).
   *
   * @see #setDomainZeroBaselineStroke(Stroke)
   */
  public Stroke getDomainZeroBaselineStroke() {
    return this.domainZeroBaselineStroke;
  }

  /**
   * Sets the stroke for the zero baseline for the domain axis, and sends a {@link PlotChangeEvent}
   * to all registered listeners.
   *
   * @param stroke the stroke ({@code null} not permitted).
   *
   * @see #getRangeZeroBaselineStroke()
   */
  public void setDomainZeroBaselineStroke(Stroke stroke) {
    Args.nullNotPermitted(stroke, "stroke");
    this.domainZeroBaselineStroke = stroke;
    fireChangeEvent();
  }

  /**
   * Returns the paint for the zero baseline (if any) plotted against the domain axis.
   *
   * @return The paint (never {@code null}).
   *
   * @see #setDomainZeroBaselinePaint(Paint)
   */
  public Paint getDomainZeroBaselinePaint() {
    return this.domainZeroBaselinePaint;
  }

  /**
   * Sets the paint for the zero baseline plotted against the domain axis and sends a
   * {@link PlotChangeEvent} to all registered listeners.
   *
   * @param paint the paint ({@code null} not permitted).
   *
   * @see #getDomainZeroBaselinePaint()
   */
  public void setDomainZeroBaselinePaint(Paint paint) {
    Args.nullNotPermitted(paint, "paint");
    this.domainZeroBaselinePaint = paint;
    fireChangeEvent();
  }

  /**
   * Returns a flag that controls whether or not a zero baseline is displayed for the range axis.
   *
   * @return A boolean.
   *
   * @see #setRangeZeroBaselineVisible(boolean)
   */
  public boolean isRangeZeroBaselineVisible() {
    return this.rangeZeroBaselineVisible;
  }

  /**
   * Sets the flag that controls whether or not the zero baseline is displayed for the range axis,
   * and sends a {@link PlotChangeEvent} to all registered listeners.
   *
   * @param visible the flag.
   *
   * @see #isRangeZeroBaselineVisible()
   */
  public void setRangeZeroBaselineVisible(boolean visible) {
    this.rangeZeroBaselineVisible = visible;
    fireChangeEvent();
  }

  /**
   * Returns the stroke used for the zero baseline against the range axis.
   *
   * @return The stroke (never {@code null}).
   *
   * @see #setRangeZeroBaselineStroke(Stroke)
   */
  public Stroke getRangeZeroBaselineStroke() {
    return this.rangeZeroBaselineStroke;
  }

  /**
   * Sets the stroke for the zero baseline for the range axis, and sends a {@link PlotChangeEvent}
   * to all registered listeners.
   *
   * @param stroke the stroke ({@code null} not permitted).
   *
   * @see #getRangeZeroBaselineStroke()
   */
  public void setRangeZeroBaselineStroke(Stroke stroke) {
    Args.nullNotPermitted(stroke, "stroke");
    this.rangeZeroBaselineStroke = stroke;
    fireChangeEvent();
  }

  /**
   * Returns the paint for the zero baseline (if any) plotted against the range axis.
   *
   * @return The paint (never {@code null}).
   *
   * @see #setRangeZeroBaselinePaint(Paint)
   */
  public Paint getRangeZeroBaselinePaint() {
    return this.rangeZeroBaselinePaint;
  }

  /**
   * Sets the paint for the zero baseline plotted against the range axis and sends a
   * {@link PlotChangeEvent} to all registered listeners.
   *
   * @param paint the paint ({@code null} not permitted).
   *
   * @see #getRangeZeroBaselinePaint()
   */
  public void setRangeZeroBaselinePaint(Paint paint) {
    Args.nullNotPermitted(paint, "paint");
    this.rangeZeroBaselinePaint = paint;
    fireChangeEvent();
  }

  /**
   * Returns the shadow generator for the plot, if any.
   *
   * @return The shadow generator (possibly {@code null}).
   */
  public ShadowGenerator getShadowGenerator() {
    return this.shadowGenerator;
  }

  /**
   * Sets the shadow generator for the plot and sends a {@link PlotChangeEvent} to all registered
   * listeners.
   *
   * @param generator the generator ({@code null} permitted).
   */
  public void setShadowGenerator(ShadowGenerator generator) {
    this.shadowGenerator = generator;
    fireChangeEvent();
  }

  /**
   * Calculates the space required for all the axes in the plot.
   *
   * @param g2 the graphics device.
   * @param plotArea the plot area.
   *
   * @return The required space.
   */
  protected AxisSpace calculateAxisSpace(Graphics2D g2, Rectangle2D plotArea) {
    AxisSpace space = new AxisSpace();
    space = calculateRangeAxisSpace(g2, plotArea, space);
    Rectangle2D revPlotArea = space.shrink(plotArea, null);
    space = calculateDomainAxisSpace(g2, revPlotArea, space);
    return space;
  }

  /**
   * Calculates the space required for the domain axis/axes.
   *
   * @param g2 the graphics device.
   * @param plotArea the plot area.
   * @param space a carrier for the result ({@code null} permitted).
   *
   * @return The required space.
   */
  protected AxisSpace calculateDomainAxisSpace(Graphics2D g2, Rectangle2D plotArea,
      AxisSpace space) {
    if (space == null) {
      space = new AxisSpace();
    }
    // reserve some space for the domain axis...
    if (this.fixedDomainAxisSpace != null) {
      if (this.orientation == PlotOrientation.HORIZONTAL) {
        space.ensureAtLeast(this.fixedDomainAxisSpace.getLeft(), RectangleEdge.LEFT);
        space.ensureAtLeast(this.fixedDomainAxisSpace.getRight(), RectangleEdge.RIGHT);
      } else if (this.orientation == PlotOrientation.VERTICAL) {
        space.ensureAtLeast(this.fixedDomainAxisSpace.getTop(), RectangleEdge.TOP);
        space.ensureAtLeast(this.fixedDomainAxisSpace.getBottom(), RectangleEdge.BOTTOM);
      }
    } else {
      // reserve space for the domain axes...
      RectangleEdge edge = getDomainAxisEdge(0);
      space = domainAxis.reserveSpace(g2, this, plotArea, edge, space);
    }
    return space;
  }

  /**
   * Calculates the space required for the range axis/axes.
   *
   * @param g2 the graphics device.
   * @param plotArea the plot area.
   * @param space a carrier for the result ({@code null} permitted).
   *
   * @return The required space.
   */
  protected AxisSpace calculateRangeAxisSpace(Graphics2D g2, Rectangle2D plotArea,
      AxisSpace space) {
    if (space == null) {
      space = new AxisSpace();
    }
    // reserve some space for the range axis...
    if (this.fixedRangeAxisSpace != null) {
      if (this.orientation == PlotOrientation.HORIZONTAL) {
        space.ensureAtLeast(this.fixedRangeAxisSpace.getTop(), RectangleEdge.TOP);
        space.ensureAtLeast(this.fixedRangeAxisSpace.getBottom(), RectangleEdge.BOTTOM);
      } else if (this.orientation == PlotOrientation.VERTICAL) {
        space.ensureAtLeast(this.fixedRangeAxisSpace.getLeft(), RectangleEdge.LEFT);
        space.ensureAtLeast(this.fixedRangeAxisSpace.getRight(), RectangleEdge.RIGHT);
      }
    } else {
      // reserve space for the range axes...
      RectangleEdge edge = getRangeAxisEdge(0);
      space = rangeAxis.reserveSpace(g2, this, plotArea, edge, space);
    }
    return space;
  }

  /**
   * Trims a rectangle to integer coordinates.
   *
   * @param rect the incoming rectangle.
   *
   * @return A rectangle with integer coordinates.
   */
  private static Rectangle integerise(Rectangle2D rect) {
    int x0 = (int) Math.ceil(rect.getMinX());
    int y0 = (int) Math.ceil(rect.getMinY());
    int x1 = (int) Math.floor(rect.getMaxX());
    int y1 = (int) Math.floor(rect.getMaxY());
    return new Rectangle(x0, y0, (x1 - x0), (y1 - y0));
  }

  /**
   * Draws the plot within the specified area on a graphics device.
   *
   * @param g2 the graphics device.
   * @param area the plot area (in Java2D space).
   * @param anchor an anchor point in Java2D space ({@code null} permitted).
   * @param parentState the state from the parent plot, if there is one ({@code null} permitted).
   * @param info collects chart drawing information ({@code null} permitted).
   */
  @Override
  public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState parentState,
      PlotRenderingInfo info) {
    // if the plot area is too small, just return...
    boolean b1 = (area.getWidth() <= MINIMUM_WIDTH_TO_DRAW);
    boolean b2 = (area.getHeight() <= MINIMUM_HEIGHT_TO_DRAW);
    if (b1 || b2) {
      return;
    }
    // record the plot area...
    if (info != null) {
      info.setPlotArea(area);
    }
    // adjust the drawing area for the plot insets (if any)...
    RectangleInsets insets = getInsets();
    insets.trim(area);
    AxisSpace space = calculateAxisSpace(g2, area);
    Rectangle2D dataArea = space.shrink(area, null);
    this.axisOffset.trim(dataArea);
    dataArea = integerise(dataArea);
    if (dataArea.isEmpty()) {
      return;
    }
    createAndAddEntity((Rectangle2D) dataArea.clone(), info, null, null);
    if (info != null) {
      info.setDataArea(dataArea);
    }
    // draw the plot background and axes...
    drawBackground(g2, dataArea);
    Map<org.jfree.chart.axis.Axis, AxisState> axisStateMap = drawAxes(g2, area, dataArea, info);
    Shape originalClip = g2.getClip();
    Composite originalComposite = g2.getComposite();
    g2.clip(dataArea);
    // g2.clip(null);
    {
      double x1 =
          getDomainAxis().valueToJava2D(clipX.min().evalf(), dataArea, getDomainAxisEdge());
      double x2 =
          getDomainAxis().valueToJava2D(clipX.max().evalf(), dataArea, getDomainAxisEdge());
      double y1 =
          getRangeAxis().valueToJava2D(clipY.min().evalf(), dataArea, getRangeAxisEdge());
      double y2 =
          getRangeAxis().valueToJava2D(clipY.max().evalf(), dataArea, getRangeAxisEdge());
      BufferedImage bufferedImage = ImageResize.of( //
          visualImage.getBufferedImage(), //
          (int) (x2 - x1 + 1), //
          (int) (y1 - y2 + 1));
      g2.drawImage(bufferedImage, //
          (int) x1, //
          (int) y2, //
          null);
    }
    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getForegroundAlpha()));
    AxisState domainAxisState = axisStateMap.get(getDomainAxis());
    if (domainAxisState == null) {
      if (parentState != null) {
        domainAxisState = (AxisState) parentState.getSharedAxisStates().get(getDomainAxis());
      }
    }
    AxisState rangeAxisState = axisStateMap.get(getRangeAxis());
    if (rangeAxisState == null) {
      if (parentState != null) {
        rangeAxisState = (AxisState) parentState.getSharedAxisStates().get(getRangeAxis());
      }
    }
    Graphics2D savedG2 = g2;
    BufferedImage dataImage = null;
    boolean suppressShadow =
        Boolean.TRUE.equals(g2.getRenderingHint(JFreeChart.KEY_SUPPRESS_SHADOW_GENERATION));
    if (this.shadowGenerator != null && !suppressShadow) {
      dataImage = new BufferedImage((int) dataArea.getWidth(), (int) dataArea.getHeight(),
          BufferedImage.TYPE_INT_ARGB);
      g2 = dataImage.createGraphics();
      g2.translate(-dataArea.getX(), -dataArea.getY());
      g2.setRenderingHints(savedG2.getRenderingHints());
    }
    // now draw annotations and render data items...
    boolean foundData = false;
    if (!foundData) {
      drawNoDataMessage(g2, dataArea);
    }
    if (this.shadowGenerator != null && !suppressShadow) {
      BufferedImage shadowImage = this.shadowGenerator.createDropShadow(dataImage);
      g2 = savedG2;
      g2.drawImage(shadowImage, (int) dataArea.getX() + this.shadowGenerator.calculateOffsetX(),
          (int) dataArea.getY() + this.shadowGenerator.calculateOffsetY(), null);
      g2.drawImage(dataImage, (int) dataArea.getX(), (int) dataArea.getY(), null);
    }
    g2.setClip(originalClip);
    g2.setComposite(originalComposite);
    drawOutline(g2, dataArea);
  }

  /**
   * Draws the background for the plot.
   *
   * @param g2 the graphics device.
   * @param area the area.
   */
  @Override
  public void drawBackground(Graphics2D g2, Rectangle2D area) {
    fillBackground(g2, area, this.orientation);
    drawBackgroundImage(g2, area);
  }

  /**
   * A utility method for drawing the axes.
   *
   * @param g2 the graphics device ({@code null} not permitted).
   * @param plotArea the plot area ({@code null} not permitted).
   * @param dataArea the data area ({@code null} not permitted).
   * @param plotState collects information about the plot ({@code null} permitted).
   *
   * @return A map containing the state for each axis drawn.
   */
  protected Map<org.jfree.chart.axis.Axis, AxisState> drawAxes(Graphics2D g2, Rectangle2D plotArea,
      Rectangle2D dataArea, PlotRenderingInfo plotState) {
    AxisCollection axisCollection = new AxisCollection();
    // add domain axes to lists...
    // ValueAxis axis = ;
    // int axisIndex = findDomainAxisIndex(domainAxis);
    axisCollection.add(domainAxis, getDomainAxisEdge(0));
    // add range axes to lists...
    axisCollection.add(rangeAxis, getRangeAxisEdge(0));
    Map<org.jfree.chart.axis.Axis, AxisState> axisStateMap = new HashMap<>();
    // draw the top axes
    double cursor = dataArea.getMinY() - this.axisOffset.calculateTopOutset(dataArea.getHeight());
    Iterator<org.jfree.chart.axis.Axis> iterator = axisCollection.getAxesAtTop().iterator();
    while (iterator.hasNext()) {
      ValueAxis axis = (ValueAxis) iterator.next();
      AxisState info = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.TOP, plotState);
      cursor = info.getCursor();
      axisStateMap.put(axis, info);
    }
    // draw the bottom axes
    cursor = dataArea.getMaxY() + this.axisOffset.calculateBottomOutset(dataArea.getHeight());
    iterator = axisCollection.getAxesAtBottom().iterator();
    while (iterator.hasNext()) {
      ValueAxis axis = (ValueAxis) iterator.next();
      AxisState info = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.BOTTOM, plotState);
      cursor = info.getCursor();
      axisStateMap.put(axis, info);
    }
    // draw the left axes
    cursor = dataArea.getMinX() - this.axisOffset.calculateLeftOutset(dataArea.getWidth());
    iterator = axisCollection.getAxesAtLeft().iterator();
    while (iterator.hasNext()) {
      ValueAxis axis = (ValueAxis) iterator.next();
      AxisState info = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.LEFT, plotState);
      cursor = info.getCursor();
      axisStateMap.put(axis, info);
    }
    // draw the right axes
    cursor = dataArea.getMaxX() + this.axisOffset.calculateRightOutset(dataArea.getWidth());
    iterator = axisCollection.getAxesAtRight().iterator();
    while (iterator.hasNext()) {
      ValueAxis axis = (ValueAxis) iterator.next();
      AxisState info = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.RIGHT, plotState);
      cursor = info.getCursor();
      axisStateMap.put(axis, info);
    }
    return axisStateMap;
  }

  /**
   * Draws a representation of the data within the dataArea region, using the current renderer.
   * <P>
   * The {@code info} and {@code crosshairState} arguments may be {@code null}.
   *
   * @param g2 the graphics device.
   * @param dataArea the region in which the data is to be drawn.
   * @param index the dataset index.
   * @param info an optional object for collection dimension information.
   * @param crosshairState collects crosshair information ({@code null} permitted).
   *
   * @return A flag that indicates whether any data was actually rendered.
   */
  public boolean render(Graphics2D g2, Rectangle2D dataArea, int index, PlotRenderingInfo info,
      CrosshairState crosshairState) {
    boolean foundData = false;
    XYDataset dataset = getDataset();
    if (!DatasetUtils.isEmptyOrNull(dataset)) {
      foundData = true;
      ValueAxis xAxis = getDomainAxisForDataset(index);
      ValueAxis yAxis = getRangeAxisForDataset(index);
      if (xAxis == null || yAxis == null) {
        return foundData; // can't render anything without axes
      }
    }
    return foundData;
  }

  /**
   * Returns the domain axis for a dataset.
   *
   * @param index the dataset index (must be &gt;= 0).
   *
   * @return The axis.
   */
  public ValueAxis getDomainAxisForDataset(int index) {
    return domainAxis;
  }

  /**
   * Returns the range axis for a dataset.
   *
   * @param index the dataset index (must be &gt;= 0).
   *
   * @return The axis.
   */
  public ValueAxis getRangeAxisForDataset(int index) {
    return rangeAxis;
  }

  /**
   * A utility method that returns a list of datasets that are mapped to a particular axis.
   *
   * @param axisIndex the axis index ({@code null} not permitted).
   *
   * @return A list of datasets.
   */
  private List<XYDataset> getDatasetsMappedToDomainAxis(Integer axisIndex) {
    Args.nullNotPermitted(axisIndex, "axisIndex");
    List<XYDataset> result = new ArrayList<>();
    // XYDataset> entry : this.datasets.entrySet()) {
    int index = 0;
    List<Integer> mappedAxes = this.datasetToDomainAxesMap.get(index);
    if (mappedAxes == null) {
      if (axisIndex.equals(ZERO)) {
        result.add(xyDataset);
      }
    } else {
      if (mappedAxes.contains(axisIndex)) {
        result.add(xyDataset);
      }
    }
    return result;
  }

  /**
   * A utility method that returns a list of datasets that are mapped to a particular axis.
   *
   * @param axisIndex the axis index ({@code null} not permitted).
   *
   * @return A list of datasets.
   */
  private List<XYDataset> getDatasetsMappedToRangeAxis(Integer axisIndex) {
    Args.nullNotPermitted(axisIndex, "axisIndex");
    List<XYDataset> result = new ArrayList<>();
    int index = 0;
    List<Integer> mappedAxes = this.datasetToRangeAxesMap.get(index);
    if (mappedAxes == null) {
      if (axisIndex.equals(ZERO)) {
        result.add(xyDataset);
      }
    } else {
      if (mappedAxes.contains(axisIndex)) {
        result.add(xyDataset);
      }
    }
    return result;
  }

  /**
   * Returns the range for the specified axis.
   *
   * @param axis the axis.
   *
   * @return The range.
   */
  @Override
  public Range getDataRange(ValueAxis axis) {
    Range result = null;
    List<XYDataset> mappedDatasets = new ArrayList<>();
    boolean isDomainAxis = true;
    // is it a domain axis?
    // int domainIndex = getDomainAxisIndex(axis);
    if (axis == domainAxis) {
      isDomainAxis = true;
      mappedDatasets.addAll(getDatasetsMappedToDomainAxis(0));
    }
    // or is it a range axis?
    // int rangeIndex = getRangeAxisIndex(axis);
    if (axis == rangeAxis) {
      isDomainAxis = false;
      mappedDatasets.addAll(getDatasetsMappedToRangeAxis(0));
    }
    // iterate through the datasets that map to the axis and get the union
    // of the ranges.
    for (XYDataset d : mappedDatasets) {
      if (d != null) {
        XYItemRenderer r = xyItemRenderer;
        if (isDomainAxis) {
          if (r != null) {
            result = Range.combine(result, r.findDomainBounds(d));
          } else {
            result = Range.combine(result, DatasetUtils.findDomainBounds(d));
          }
        } else {
          if (r != null) {
            result = Range.combine(result, r.findRangeBounds(d));
          } else {
            result = Range.combine(result, DatasetUtils.findRangeBounds(d));
          }
        }
      }
    }
    return result;
  }

  /**
   * Returns the fixed domain axis space.
   *
   * @return The fixed domain axis space (possibly {@code null}).
   *
   * @see #setFixedDomainAxisSpace(AxisSpace)
   */
  public AxisSpace getFixedDomainAxisSpace() {
    return this.fixedDomainAxisSpace;
  }

  /**
   * Sets the fixed domain axis space and sends a {@link PlotChangeEvent} to all registered
   * listeners.
   *
   * @param space the space ({@code null} permitted).
   *
   * @see #getFixedDomainAxisSpace()
   */
  public void setFixedDomainAxisSpace(AxisSpace space) {
    setFixedDomainAxisSpace(space, true);
  }

  /**
   * Sets the fixed domain axis space and, if requested, sends a {@link PlotChangeEvent} to all
   * registered listeners.
   *
   * @param space the space ({@code null} permitted).
   * @param notify notify listeners?
   *
   * @see #getFixedDomainAxisSpace()
   */
  public void setFixedDomainAxisSpace(AxisSpace space, boolean notify) {
    this.fixedDomainAxisSpace = space;
    if (notify) {
      fireChangeEvent();
    }
  }

  /**
   * Returns the fixed range axis space.
   *
   * @return The fixed range axis space (possibly {@code null}).
   *
   * @see #setFixedRangeAxisSpace(AxisSpace)
   */
  public AxisSpace getFixedRangeAxisSpace() {
    return this.fixedRangeAxisSpace;
  }

  /**
   * Sets the fixed range axis space and sends a {@link PlotChangeEvent} to all registered
   * listeners.
   *
   * @param space the space ({@code null} permitted).
   *
   * @see #getFixedRangeAxisSpace()
   */
  public void setFixedRangeAxisSpace(AxisSpace space) {
    setFixedRangeAxisSpace(space, true);
  }

  /**
   * Sets the fixed range axis space and, if requested, sends a {@link PlotChangeEvent} to all
   * registered listeners.
   *
   * @param space the space ({@code null} permitted).
   * @param notify notify listeners?
   *
   * @see #getFixedRangeAxisSpace()
   */
  public void setFixedRangeAxisSpace(AxisSpace space, boolean notify) {
    this.fixedRangeAxisSpace = space;
    if (notify) {
      fireChangeEvent();
    }
  }

  /**
   * Returns {@code true} if panning is enabled for the domain axes, and {@code false} otherwise.
   *
   * @return A boolean.
   */
  @Override
  public boolean isDomainPannable() {
    return this.domainPannable;
  }

  /**
   * Sets the flag that enables or disables panning of the plot along the domain axes.
   *
   * @param pannable the new flag value.
   */
  public void setDomainPannable(boolean pannable) {
    this.domainPannable = pannable;
  }

  /**
   * Returns {@code true} if panning is enabled for the range axis/axes, and {@code false}
   * otherwise. The default value is {@code false}.
   *
   * @return A boolean.
   */
  @Override
  public boolean isRangePannable() {
    return this.rangePannable;
  }

  /**
   * Sets the flag that enables or disables panning of the plot along the range axis/axes.
   *
   * @param pannable the new flag value.
   */
  public void setRangePannable(boolean pannable) {
    this.rangePannable = pannable;
  }

  /**
   * Pans the domain axes by the specified percentage.
   *
   * @param percent the distance to pan (as a percentage of the axis length).
   * @param info the plot info
   * @param source the source point where the pan action started.
   */
  @Override
  public void panDomainAxes(double percent, PlotRenderingInfo info, Point2D source) {
    if (!isDomainPannable()) {
      return;
    }
    domainAxis.pan(domainAxis.isInverted() ? -percent : percent);
  }

  /**
   * Pans the range axes by the specified percentage.
   *
   * @param percent the distance to pan (as a percentage of the axis length).
   * @param info the plot info
   * @param source the source point where the pan action started.
   */
  @Override
  public void panRangeAxes(double percent, PlotRenderingInfo info, Point2D source) {
    if (!isRangePannable()) {
      return;
    }
    rangeAxis.pan(rangeAxis.isInverted() ? -percent : percent);
  }

  /**
   * Multiplies the range on the domain axis/axes by the specified factor.
   *
   * @param factor the zoom factor.
   * @param info the plot rendering info.
   * @param source the source point (in Java2D space).
   *
   * @see #zoomRangeAxes(double, PlotRenderingInfo, Point2D)
   */
  @Override
  public void zoomDomainAxes(double factor, PlotRenderingInfo info, Point2D source) {
    // delegate to other method
    zoomDomainAxes(factor, info, source, false);
  }

  /**
   * Multiplies the range on the domain axis/axes by the specified factor.
   *
   * @param factor the zoom factor.
   * @param info the plot rendering info.
   * @param source the source point (in Java2D space).
   * @param useAnchor use source point as zoom anchor?
   *
   * @see #zoomRangeAxes(double, PlotRenderingInfo, Point2D, boolean)
   */
  @Override
  public void zoomDomainAxes(double factor, PlotRenderingInfo info, Point2D source,
      boolean useAnchor) {
    // perform the zoom on each domain axis
    if (useAnchor) {
      // get the relevant source coordinate given the plot orientation
      double sourceX = source.getX();
      if (this.orientation == PlotOrientation.HORIZONTAL) {
        sourceX = source.getY();
      }
      double anchorX = domainAxis.java2DToValue(sourceX, info.getDataArea(), getDomainAxisEdge());
      domainAxis.resizeRange2(factor, anchorX);
    } else {
      domainAxis.resizeRange(factor);
    }
  }

  /**
   * Zooms in on the domain axis/axes. The new lower and upper bounds are specified as percentages
   * of the current axis range, where 0 percent is the current lower bound and 100 percent is the
   * current upper bound.
   *
   * @param lowerPercent a percentage that determines the new lower bound for the axis (e.g. 0.20 is
   *        twenty percent).
   * @param upperPercent a percentage that determines the new upper bound for the axis (e.g. 0.80 is
   *        eighty percent).
   * @param info the plot rendering info.
   * @param source the source point (ignored).
   *
   * @see #zoomRangeAxes(double, double, PlotRenderingInfo, Point2D)
   */
  @Override
  public void zoomDomainAxes(double lowerPercent, double upperPercent, PlotRenderingInfo info,
      Point2D source) {
    domainAxis.zoomRange(lowerPercent, upperPercent);
  }

  /**
   * Multiplies the range on the range axis/axes by the specified factor.
   *
   * @param factor the zoom factor.
   * @param info the plot rendering info.
   * @param source the source point.
   *
   * @see #zoomDomainAxes(double, PlotRenderingInfo, Point2D, boolean)
   */
  @Override
  public void zoomRangeAxes(double factor, PlotRenderingInfo info, Point2D source) {
    // delegate to other method
    zoomRangeAxes(factor, info, source, false);
  }

  /**
   * Multiplies the range on the range axis/axes by the specified factor.
   *
   * @param factor the zoom factor.
   * @param info the plot rendering info.
   * @param source the source point.
   * @param useAnchor a flag that controls whether or not the source point is used for the zoom
   *        anchor.
   *
   * @see #zoomDomainAxes(double, PlotRenderingInfo, Point2D, boolean)
   */
  @Override
  public void zoomRangeAxes(double factor, PlotRenderingInfo info, Point2D source,
      boolean useAnchor) {
    // perform the zoom on each range axis
    if (useAnchor) {
      // get the relevant source coordinate given the plot orientation
      double sourceY = source.getY();
      if (this.orientation == PlotOrientation.HORIZONTAL) {
        sourceY = source.getX();
      }
      double anchorY = rangeAxis.java2DToValue(sourceY, info.getDataArea(), getRangeAxisEdge());
      rangeAxis.resizeRange2(factor, anchorY);
    } else {
      rangeAxis.resizeRange(factor);
    }
  }

  /**
   * Zooms in on the range axes.
   *
   * @param lowerPercent the lower bound.
   * @param upperPercent the upper bound.
   * @param info the plot rendering info.
   * @param source the source point.
   *
   * @see #zoomDomainAxes(double, double, PlotRenderingInfo, Point2D)
   */
  @Override
  public void zoomRangeAxes(double lowerPercent, double upperPercent, PlotRenderingInfo info,
      Point2D source) {
    rangeAxis.zoomRange(lowerPercent, upperPercent);
  }

  /**
   * Returns {@code true}, indicating that the domain axis/axes for this plot are zoomable.
   *
   * @return A boolean.
   *
   * @see #isRangeZoomable()
   */
  @Override
  public boolean isDomainZoomable() {
    return true;
  }

  /**
   * Returns {@code true}, indicating that the range axis/axes for this plot are zoomable.
   *
   * @return A boolean.
   *
   * @see #isDomainZoomable()
   */
  @Override
  public boolean isRangeZoomable() {
    return true;
  }

  /**
   * Returns the legend items for the plot. Each legend item is generated by the plot's renderer,
   * since the renderer is responsible for the visual representation of the data.
   *
   * @return The legend items.
   */
  @Override
  public LegendItemCollection getLegendItems() {
    return new LegendItemCollection();
  }
}
