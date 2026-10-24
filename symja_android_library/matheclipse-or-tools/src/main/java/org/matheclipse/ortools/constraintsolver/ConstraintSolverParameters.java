package org.matheclipse.ortools.constraintsolver;

/**
 * The settings of a {@link Solver}, shaped like the protocol buffer message
 * <code>operations_research.ConstraintSolverParameters</code> of the OR-Tools Java wrapper
 * (<code>toBuilder()</code>, <code>newBuilder()</code>, getters and builder setters). This class
 * reimplements a subset of that API (OR-Tools 10.0: the profiling fields 7 and 8 are reserved) as
 * a plain immutable Java object; the simplified engine of this module stores the values but only
 * <code>trace_search</code> has an effect (decisions are logged at level FINE).
 */
public final class ConstraintSolverParameters {

  /** Compression of the search trail (ignored by this implementation). */
  public enum TrailCompression {
    /** No compression. */
    NO_COMPRESSION(0),
    /** Zlib compression (number 1 is reserved since OR-Tools 10.0). */
    COMPRESS_WITH_ZLIB(2),
    /** Gzip compression. */
    COMPRESS_WITH_GZIP(3),
    /** An unknown value. */
    UNRECOGNIZED(-1);

    private final int number;

    TrailCompression(int number) {
      this.number = number;
    }

    /** The protobuf number of the value. */
    public int getNumber() {
      return number;
    }

    /** The value with the given number, or null. */
    public static TrailCompression forNumber(int value) {
      switch (value) {
        case 0:
          return NO_COMPRESSION;
        case 2:
          return COMPRESS_WITH_ZLIB;
        case 3:
          return COMPRESS_WITH_GZIP;
        default:
          return null;
      }
    }
  }

  private final TrailCompression compressTrail;

  private final int trailBlockSize;

  private final int arraySplitSize;

  private final boolean storeNames;

  private final boolean nameCastVariables;

  private final boolean nameAllVariables;



  private final boolean tracePropagation;

  private final boolean traceSearch;

  private final boolean printModel;

  private final boolean printModelStats;

  private final boolean printAddedConstraints;

  private final boolean disableSolve;

  private final boolean skipLocallyOptimalPaths;

  private final int checkSolutionPeriod;

  private ConstraintSolverParameters(Builder b) {
    compressTrail = b.compressTrail;
    trailBlockSize = b.trailBlockSize;
    arraySplitSize = b.arraySplitSize;
    storeNames = b.storeNames;
    nameCastVariables = b.nameCastVariables;
    nameAllVariables = b.nameAllVariables;
    tracePropagation = b.tracePropagation;
    traceSearch = b.traceSearch;
    printModel = b.printModel;
    printModelStats = b.printModelStats;
    printAddedConstraints = b.printAddedConstraints;
    disableSolve = b.disableSolve;
    skipLocallyOptimalPaths = b.skipLocallyOptimalPaths;
    checkSolutionPeriod = b.checkSolutionPeriod;
  }

  /** The message with all fields at their protobuf defaults (zero, false, empty). */
  public static ConstraintSolverParameters getDefaultInstance() {
    return new Builder().build();
  }

  /** A builder with all fields at their protobuf defaults. */
  public static Builder newBuilder() {
    return new Builder();
  }

  /** A builder initialized with <code>prototype</code>. */
  public static Builder newBuilder(ConstraintSolverParameters prototype) {
    return new Builder().mergeFrom(prototype);
  }

  /** A builder initialized with this message. */
  public Builder toBuilder() {
    return newBuilder(this);
  }

  /** The trail compression. */
  public TrailCompression getCompressTrail() {
    return compressTrail;
  }

  /** The trail block size. */
  public int getTrailBlockSize() {
    return trailBlockSize;
  }

  /** The array split size. */
  public int getArraySplitSize() {
    return arraySplitSize;
  }

  /** Whether names are stored. */
  public boolean getStoreNames() {
    return storeNames;
  }

  /** Whether cast variables are named. */
  public boolean getNameCastVariables() {
    return nameCastVariables;
  }

  /** Whether all variables are named. */
  public boolean getNameAllVariables() {
    return nameAllVariables;
  }

  /** Whether propagation is traced. */
  public boolean getTracePropagation() {
    return tracePropagation;
  }

  /** Whether the search is traced. */
  public boolean getTraceSearch() {
    return traceSearch;
  }

  /** Whether the model is printed. */
  public boolean getPrintModel() {
    return printModel;
  }

  /** Whether model statistics are printed. */
  public boolean getPrintModelStats() {
    return printModelStats;
  }

  /** Whether added constraints are printed. */
  public boolean getPrintAddedConstraints() {
    return printAddedConstraints;
  }

  /** Whether solving is disabled. */
  public boolean getDisableSolve() {
    return disableSolve;
  }

  /** Whether locally optimal paths are skipped by local search. */
  public boolean getSkipLocallyOptimalPaths() {
    return skipLocallyOptimalPaths;
  }

  /** The solution check period. */
  public int getCheckSolutionPeriod() {
    return checkSolutionPeriod;
  }

  @Override
  public String toString() {
    return "compress_trail: " + compressTrail + " trail_block_size: " + trailBlockSize
        + " array_split_size: " + arraySplitSize + " store_names: " + storeNames
        + " trace_search: " + traceSearch;
  }

  /** Builder of {@link ConstraintSolverParameters}. */
  public static final class Builder {
    private TrailCompression compressTrail = TrailCompression.NO_COMPRESSION;

    private int trailBlockSize;

    private int arraySplitSize;

    private boolean storeNames;

    private boolean nameCastVariables;

    private boolean nameAllVariables;



    private boolean tracePropagation;

    private boolean traceSearch;

    private boolean printModel;

    private boolean printModelStats;

    private boolean printAddedConstraints;

    private boolean disableSolve;

    private boolean skipLocallyOptimalPaths;

    private int checkSolutionPeriod;

    private Builder() {}

    /** Copies all fields of <code>other</code>. */
    public Builder mergeFrom(ConstraintSolverParameters other) {
      compressTrail = other.compressTrail;
      trailBlockSize = other.trailBlockSize;
      arraySplitSize = other.arraySplitSize;
      storeNames = other.storeNames;
      nameCastVariables = other.nameCastVariables;
      nameAllVariables = other.nameAllVariables;
      tracePropagation = other.tracePropagation;
      traceSearch = other.traceSearch;
      printModel = other.printModel;
      printModelStats = other.printModelStats;
      printAddedConstraints = other.printAddedConstraints;
      disableSolve = other.disableSolve;
      skipLocallyOptimalPaths = other.skipLocallyOptimalPaths;
      checkSolutionPeriod = other.checkSolutionPeriod;
      return this;
    }

    /** Sets the trail compression. */
    public Builder setCompressTrail(TrailCompression value) {
      compressTrail = value;
      return this;
    }

    /** Sets the trail block size. */
    public Builder setTrailBlockSize(int value) {
      trailBlockSize = value;
      return this;
    }

    /** Sets the array split size. */
    public Builder setArraySplitSize(int value) {
      arraySplitSize = value;
      return this;
    }

    /** Sets whether names are stored. */
    public Builder setStoreNames(boolean value) {
      storeNames = value;
      return this;
    }

    /** Sets whether cast variables are named. */
    public Builder setNameCastVariables(boolean value) {
      nameCastVariables = value;
      return this;
    }

    /** Sets whether all variables are named. */
    public Builder setNameAllVariables(boolean value) {
      nameAllVariables = value;
      return this;
    }

    /** Sets whether propagation is traced. */
    public Builder setTracePropagation(boolean value) {
      tracePropagation = value;
      return this;
    }

    /** Sets whether the search is traced. */
    public Builder setTraceSearch(boolean value) {
      traceSearch = value;
      return this;
    }

    /** Sets whether the model is printed. */
    public Builder setPrintModel(boolean value) {
      printModel = value;
      return this;
    }

    /** Sets whether model statistics are printed. */
    public Builder setPrintModelStats(boolean value) {
      printModelStats = value;
      return this;
    }

    /** Sets whether added constraints are printed. */
    public Builder setPrintAddedConstraints(boolean value) {
      printAddedConstraints = value;
      return this;
    }

    /** Sets whether solving is disabled. */
    public Builder setDisableSolve(boolean value) {
      disableSolve = value;
      return this;
    }

    /** Sets whether locally optimal paths are skipped by local search. */
    public Builder setSkipLocallyOptimalPaths(boolean value) {
      skipLocallyOptimalPaths = value;
      return this;
    }

    /** Sets the solution check period. */
    public Builder setCheckSolutionPeriod(int value) {
      checkSolutionPeriod = value;
      return this;
    }

    /** Whether the search is traced. */
    public boolean getTraceSearch() {
      return traceSearch;
    }

    /** Creates the message. */
    public ConstraintSolverParameters build() {
      return new ConstraintSolverParameters(this);
    }
  }
}
