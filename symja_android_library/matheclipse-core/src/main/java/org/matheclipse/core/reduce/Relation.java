package org.matheclipse.core.reduce;

/** The relation of an atom <code>term REL 0</code>. */
public enum Relation {
  EQUAL, NOT_EQUAL, LESS, LESS_EQUAL, GREATER, GREATER_EQUAL;

  /** The relation which holds exactly when this one does not. */
  public Relation negated() {
    switch (this) {
      case EQUAL:
        return NOT_EQUAL;
      case NOT_EQUAL:
        return EQUAL;
      case LESS:
        return GREATER_EQUAL;
      case LESS_EQUAL:
        return GREATER;
      case GREATER:
        return LESS_EQUAL;
      default:
        return LESS;
    }
  }

  /** The relation after multiplying both sides by a negative number. */
  public Relation reversed() {
    switch (this) {
      case LESS:
        return GREATER;
      case LESS_EQUAL:
        return GREATER_EQUAL;
      case GREATER:
        return LESS;
      case GREATER_EQUAL:
        return LESS_EQUAL;
      default:
        return this;
    }
  }
}
