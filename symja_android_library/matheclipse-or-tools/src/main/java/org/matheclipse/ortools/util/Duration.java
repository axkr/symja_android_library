package org.matheclipse.ortools.util;

/**
 * A span of time with nanosecond resolution, shaped like the protocol buffer message
 * <code>com.google.protobuf.Duration</code>.
 *
 * <p>
 * This class reimplements the small part of the protobuf API which OR-Tools programs use (for
 * example <code>Duration.newBuilder().setSeconds(5).build()</code> as the time limit of a routing
 * search), so that such programs only need their imports changed. Instances are immutable; use
 * {@link #toBuilder()} or {@link #newBuilder()} to create modified copies.
 */
public final class Duration {

  private static final Duration DEFAULT_INSTANCE = new Duration(0L, 0);

  private final long seconds;

  private final int nanos;

  private Duration(long seconds, int nanos) {
    this.seconds = seconds;
    this.nanos = nanos;
  }

  /** The duration of length zero. */
  public static Duration getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  /** A new builder whose fields are all zero. */
  public static Builder newBuilder() {
    return new Builder();
  }

  /** A new builder initialized with the fields of <code>prototype</code>. */
  public static Builder newBuilder(Duration prototype) {
    return new Builder().mergeFrom(prototype);
  }

  /** A builder initialized with the fields of this duration. */
  public Builder toBuilder() {
    return newBuilder(this);
  }

  /** The number of whole seconds. */
  public long getSeconds() {
    return seconds;
  }

  /** The fraction of a second in nanoseconds, between -999,999,999 and 999,999,999. */
  public int getNanos() {
    return nanos;
  }

  /** The duration in milliseconds, rounded towards zero (saturated on overflow). */
  public long toMillis() {
    long ms = nanos / 1_000_000L;
    if (seconds > Long.MAX_VALUE / 1000L) {
      return Long.MAX_VALUE;
    }
    if (seconds < Long.MIN_VALUE / 1000L) {
      return Long.MIN_VALUE;
    }
    return seconds * 1000L + ms;
  }

  /** The duration in seconds as a double. */
  public double toSecondsDouble() {
    return seconds + nanos / 1e9;
  }

  /** The duration in nanoseconds, saturated on overflow. */
  public long toNanos() {
    if (seconds > Long.MAX_VALUE / 1_000_000_000L) {
      return Long.MAX_VALUE;
    }
    if (seconds < Long.MIN_VALUE / 1_000_000_000L) {
      return Long.MIN_VALUE;
    }
    return seconds * 1_000_000_000L + nanos;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Duration)) {
      return false;
    }
    Duration other = (Duration) obj;
    return seconds == other.seconds && nanos == other.nanos;
  }

  @Override
  public int hashCode() {
    return Long.hashCode(seconds) * 31 + nanos;
  }

  /** The text format of the protobuf message, for example <code>seconds: 5</code>. */
  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    if (seconds != 0) {
      buf.append("seconds: ").append(seconds);
    }
    if (nanos != 0) {
      if (buf.length() > 0) {
        buf.append(' ');
      }
      buf.append("nanos: ").append(nanos);
    }
    return buf.toString();
  }

  /** Builder of {@link Duration} values. */
  public static final class Builder {
    private long seconds;

    private int nanos;

    private Builder() {}

    /** Sets the number of whole seconds. */
    public Builder setSeconds(long value) {
      seconds = value;
      return this;
    }

    /** Sets the fraction of a second in nanoseconds. */
    public Builder setNanos(int value) {
      nanos = value;
      return this;
    }

    /** The number of whole seconds set so far. */
    public long getSeconds() {
      return seconds;
    }

    /** The nanoseconds set so far. */
    public int getNanos() {
      return nanos;
    }

    /** Resets both fields to zero. */
    public Builder clear() {
      seconds = 0L;
      nanos = 0;
      return this;
    }

    /** Copies the non-zero fields of <code>other</code> into this builder. */
    public Builder mergeFrom(Duration other) {
      if (other != null) {
        if (other.seconds != 0) {
          seconds = other.seconds;
        }
        if (other.nanos != 0) {
          nanos = other.nanos;
        }
      }
      return this;
    }

    /** Creates the immutable duration. */
    public Duration build() {
      return new Duration(seconds, nanos);
    }
  }
}
