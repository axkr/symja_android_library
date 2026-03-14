package org.matheclipse.core.basic;

public class RuleConfig {

  /**
   * Base penalty subtracted from the rule priority for leaf nodes, adjusted by the tree level.
   */
  public static int PRIORITY_BASE_PENALTY = 50;
  /**
   * Penalty subtracted from the rule priority for each AST node or Association encountered.
   */
  public static int PRIORITY_AST_PENALTY = 11;
  public static int PRIORITY_BLANK_OPTIONAL = 2;
  public static int PRIORITY_BLANK = 5;
  public static int PRIORITY_BLANK_HEADTEST = 2;
  public static int PRIORITY_PATTERN_OPTIONAL = 3;
  public static int PRIORITY_PATTERN = 6;
  public static int PRIORITY_PATTERN_HEADTEST = 2;
  public static int PRIORITY_PATTERN_SEQUENCE = 1;
  public static int PRIORITY_PATTERN_SEQUENCE_HEADTEST = 2;
  public static int PRIORITY_REPEATED_PATTERN = 1;
  public static int PRIORITY_OPTIONS_PATTERN = 1;

  // public static int PRIORITY_BASE_PENALTY = 13;
  // public static int PRIORITY_AST_PENALTY = 0;
  // public static int PRIORITY_BLANK_OPTIONAL = 11;
  // public static int PRIORITY_BLANK = 43;
  // public static int PRIORITY_BLANK_HEADTEST = 26;
  // public static int PRIORITY_PATTERN_OPTIONAL = 0;
  // public static int PRIORITY_PATTERN = 541;
  // public static int PRIORITY_PATTERN_HEADTEST = 0;
  // public static int PRIORITY_PATTERN_SEQUENCE = 4;
  // public static int PRIORITY_PATTERN_SEQUENCE_HEADTEST = 44;
  // public static int PRIORITY_REPEATED_PATTERN = 11;
  // public static int PRIORITY_OPTIONS_PATTERN = 29;
}
