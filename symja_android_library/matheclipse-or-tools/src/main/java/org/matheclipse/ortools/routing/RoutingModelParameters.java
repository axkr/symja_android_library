package org.matheclipse.ortools.routing;

import org.matheclipse.ortools.constraintsolver.ConstraintSolverParameters;
/**
 * The parameters of a {@link Model}, shaped like the protocol buffer message
 * <code>operations_research.routing.RoutingModelParameters</code> of the OR-Tools Java wrapper.
 * This class reimplements the message as an immutable Java object; the simplified routing engine
 * of this module only passes <code>solver_parameters</code> to the underlying
 * {@link org.matheclipse.ortools.constraintsolver.Solver}.
 */
public final class RoutingModelParameters {

  private final ConstraintSolverParameters solverParameters;

  private final boolean reduceVehicleCostModel;

  private final int maxCallbackCacheSize;

  private RoutingModelParameters(Builder b) {
    this.solverParameters = b.solverParameters;
    this.reduceVehicleCostModel = b.reduceVehicleCostModel;
    this.maxCallbackCacheSize = b.maxCallbackCacheSize;
  }

  /** The message with all fields at their protobuf defaults. */
  public static RoutingModelParameters getDefaultInstance() {
    return new Builder().build();
  }

  /** A builder with all fields at their protobuf defaults. */
  public static Builder newBuilder() {
    return new Builder();
  }

  /** A builder initialized with <code>prototype</code>. */
  public static Builder newBuilder(RoutingModelParameters prototype) {
    Builder b = new Builder();
    b.solverParameters = prototype.solverParameters;
    b.reduceVehicleCostModel = prototype.reduceVehicleCostModel;
    b.maxCallbackCacheSize = prototype.maxCallbackCacheSize;
    return b;
  }

  /** A builder initialized with this message. */
  public Builder toBuilder() {
    return newBuilder(this);
  }

  /** Whether the solver parameters are set. */
  public boolean hasSolverParameters() {
    return solverParameters != null;
  }

  /** The parameters of the underlying constraint solver. */
  public ConstraintSolverParameters getSolverParameters() {
    return solverParameters == null ? ConstraintSolverParameters.getDefaultInstance()
        : solverParameters;
  }

  /** Whether vehicles with the same costs are merged (no effect). */
  public boolean getReduceVehicleCostModel() {
    return reduceVehicleCostModel;
  }

  /** Callback cache size (no effect: callbacks are always cached up to 2500 indices). */
  public int getMaxCallbackCacheSize() {
    return maxCallbackCacheSize;
  }

  /** Builder of {@link RoutingModelParameters}. */
  public static final class Builder {
    private ConstraintSolverParameters solverParameters;

    private boolean reduceVehicleCostModel;

    private int maxCallbackCacheSize;

    private Builder() {}

    /** Sets the parameters of the underlying constraint solver. */
    public Builder setSolverParameters(ConstraintSolverParameters value) {
      solverParameters = value;
      return this;
    }

    /** Sets the parameters of the underlying constraint solver from a builder. */
    public Builder setSolverParameters(ConstraintSolverParameters.Builder value) {
      solverParameters = value.build();
      return this;
    }

    /** Sets whether vehicles with the same costs are merged. */
    public Builder setReduceVehicleCostModel(boolean value) {
      reduceVehicleCostModel = value;
      return this;
    }

    /** Sets the callback cache size. */
    public Builder setMaxCallbackCacheSize(int value) {
      maxCallbackCacheSize = value;
      return this;
    }

    /** Creates the message. */
    public RoutingModelParameters build() {
      return new RoutingModelParameters(this);
    }
  }
}
