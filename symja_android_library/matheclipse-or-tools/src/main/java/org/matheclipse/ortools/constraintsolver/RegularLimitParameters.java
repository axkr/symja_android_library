package org.matheclipse.ortools.constraintsolver;

/**
 * The settings of a {@link RegularLimit}, shaped like the protocol buffer message
 * <code>operations_research.RegularLimitParameters</code> of the OR-Tools Java wrapper
 * (<code>toBuilder()</code>, <code>newBuilder()</code>, getters and builder setters). This class
 * reimplements that API as a plain immutable Java object.
 */
public final class RegularLimitParameters {

  private final long time;

  private final long branches;

  private final long failures;

  private final long solutions;

  private final boolean smartTimeCheck;

  private final boolean cumulative;

  private RegularLimitParameters(Builder b) {
    this.time = b.time;
    this.branches = b.branches;
    this.failures = b.failures;
    this.solutions = b.solutions;
    this.smartTimeCheck = b.smartTimeCheck;
    this.cumulative = b.cumulative;
  }

  /** The message with all fields zero. */
  public static RegularLimitParameters getDefaultInstance() {
    return new Builder().build();
  }

  /** A builder with all fields zero. */
  public static Builder newBuilder() {
    return new Builder();
  }

  /** A builder initialized with <code>prototype</code>. */
  public static Builder newBuilder(RegularLimitParameters prototype) {
    return new Builder().mergeFrom(prototype);
  }

  /** A builder initialized with this message. */
  public Builder toBuilder() {
    return newBuilder(this);
  }

  /** The time limit in milliseconds. */
  public long getTime() {
    return time;
  }

  /** The branch limit. */
  public long getBranches() {
    return branches;
  }

  /** The failure limit. */
  public long getFailures() {
    return failures;
  }

  /** The solution limit. */
  public long getSolutions() {
    return solutions;
  }

  /** Whether the time is checked only periodically (ignored by this implementation). */
  public boolean getSmartTimeCheck() {
    return smartTimeCheck;
  }

  /** Whether the limits count across searches. */
  public boolean getCumulative() {
    return cumulative;
  }

  @Override
  public String toString() {
    return "time: " + time + " branches: " + branches + " failures: " + failures + " solutions: "
        + solutions + " smart_time_check: " + smartTimeCheck + " cumulative: " + cumulative;
  }

  /** Builder of {@link RegularLimitParameters}. */
  public static final class Builder {
    private long time;

    private long branches;

    private long failures;

    private long solutions;

    private boolean smartTimeCheck;

    private boolean cumulative;

    private Builder() {}

    /** Copies the fields of <code>other</code>. */
    public Builder mergeFrom(RegularLimitParameters other) {
      time = other.time;
      branches = other.branches;
      failures = other.failures;
      solutions = other.solutions;
      smartTimeCheck = other.smartTimeCheck;
      cumulative = other.cumulative;
      return this;
    }

    /** Sets the time limit in milliseconds. */
    public Builder setTime(long value) {
      time = value;
      return this;
    }

    /** Sets the branch limit. */
    public Builder setBranches(long value) {
      branches = value;
      return this;
    }

    /** Sets the failure limit. */
    public Builder setFailures(long value) {
      failures = value;
      return this;
    }

    /** Sets the solution limit. */
    public Builder setSolutions(long value) {
      solutions = value;
      return this;
    }

    /** Sets the smart time check flag. */
    public Builder setSmartTimeCheck(boolean value) {
      smartTimeCheck = value;
      return this;
    }

    /** Sets the cumulative flag. */
    public Builder setCumulative(boolean value) {
      cumulative = value;
      return this;
    }

    /** The time limit set so far. */
    public long getTime() {
      return time;
    }

    /** The branch limit set so far. */
    public long getBranches() {
      return branches;
    }

    /** The failure limit set so far. */
    public long getFailures() {
      return failures;
    }

    /** The solution limit set so far. */
    public long getSolutions() {
      return solutions;
    }

    /** Creates the message. */
    public RegularLimitParameters build() {
      return new RegularLimitParameters(this);
    }
  }
}
