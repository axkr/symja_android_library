package org.matheclipse.ortools.constraintsolver;

/**
 * A monitor which stops the search when a limit is reached, mirroring
 * <code>com.google.ortools.constraintsolver.SearchLimit</code> of the OR-Tools Java wrapper.
 *
 * <p>
 * Part of the reimplementation of the OR-Tools constraint solver API on a simpler engine; the
 * search checks {@link #check()} at every node.
 */
public class SearchLimit extends SearchMonitor {

  private boolean crossed;

  SearchLimit(Solver s) {
    super(s);
  }

  /** Whether the limit has been reached in the current or last search. */
  public boolean crossed() {
    return crossed;
  }

  /** Checks the limit, records and returns whether it has been reached. */
  public boolean check() {
    if (!crossed && isLimitReached()) {
      crossed = true;
    }
    return crossed;
  }

  /** The limit condition; subclasses override it. */
  boolean isLimitReached() {
    return false;
  }

  /** Resets the limit at the start of a search. */
  public void init() {
    crossed = false;
  }

  /** Copies the settings of another limit (not supported in general). */
  public void copy(SearchLimit limit) {
    throw new UnsupportedOperationException("SearchLimit.copy");
  }

  /** A limit with the same settings (not supported in general). */
  public SearchLimit makeClone() {
    throw new UnsupportedOperationException("SearchLimit.makeClone");
  }

  @Override
  public void enterSearch() {
    init();
  }

  @Override
  public String toString() {
    return "SearchLimit(crossed = " + crossed + ")";
  }
}
