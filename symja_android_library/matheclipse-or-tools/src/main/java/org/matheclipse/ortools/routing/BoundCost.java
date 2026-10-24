package org.matheclipse.ortools.routing;

/**
 * A bound with the cost per unit of excess, mirroring
 * <code>com.google.ortools.routing.BoundCost</code> of the OR-Tools Java wrapper
 * (reimplemented as a plain Java object). Used for soft span upper bounds of routing dimensions.
 */
public class BoundCost {

  private long bound;

  private long cost;

  /** A bound and cost of zero. */
  public BoundCost() {}

  /** Creates the bound with the given cost. */
  public BoundCost(long bound, long cost) {
    this.bound = bound;
    this.cost = cost;
  }

  /** Changes the bound. */
  public void setBound(long value) {
    bound = value;
  }

  /** The bound. */
  public long getBound() {
    return bound;
  }

  /** Changes the cost. */
  public void setCost(long value) {
    cost = value;
  }

  /** The cost per unit above the bound. */
  public long getCost() {
    return cost;
  }
}
