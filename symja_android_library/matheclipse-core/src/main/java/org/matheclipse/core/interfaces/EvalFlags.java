package org.matheclipse.core.interfaces;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;

/**
 * The vocabulary of the evaluation flags which an {@link IAST} memoizes about itself.
 * <p>
 * The flags are stored as the bits of a single <code>int</code> field, so this class adds no memory
 * to an expression - it only gives the bits a type. Use it in this order of preference:
 * <ol>
 * <li>{@link Trait} for the three-valued properties, because it is the only form in which the
 * illegal &quot;both yes and no&quot; state cannot be expressed;</li>
 * <li>{@link Flag} and {@link Group} for everything else;</li>
 * <li>{@link Mask} only in the few hot paths which need a <code>javac</code> compile-time constant
 * - see its javadoc.</li>
 * </ol>
 *
 * <h3>Not to be confused with</h3>
 * <p>
 * Three further <code>int</code> flag namespaces exist in this hierarchy, and none of them may be
 * mixed with these:
 * </p>
 * <ul>
 * <li>the <b>symbol attributes</b> {@link ISymbol#FLAT}, {@link ISymbol#ORDERLESS},
 * {@link ISymbol#LISTABLE}, {@link ISymbol#NONTHREADABLE}, ... set with
 * {@link ISymbol#addAttributes(int)};</li>
 * <li>the <b>symbol evaluation flags</b> {@link ISymbol#DIRTY_FLAG_ASSIGNED_VALUE}, ... stored in
 * an unrelated field of {@code Symbol};</li>
 * <li>the argument type flags in {@code org.matheclipse.core.expression.UniformFlags}.</li>
 * </ul>
 *
 * @see IAST#getEvalFlags()
 */
public final class EvalFlags {

  private EvalFlags() {}

  /**
   * The raw bit values of the {@link Flag} constants.
   * <p>
   * <b>Internal.</b> These are <i>constant variables</i> in the sense of the Java Language
   * Specification, so <code>javac</code> inlines them and a test against one of them compiles to a
   * single <code>and</code> against an immediate. A {@link Flag} constant cannot do that - reading
   * <code>flag.mask()</code> is a static-field load plus an instance-field read, which HotSpot
   * inlines and hoists but never folds into the instruction, and which cannot appear in a
   * <code>case</code> label at all.
   * <p>
   * So: use {@link Flag} everywhere, and reach for {@link Mask} only in a hot path which measurably
   * needs the folded form. This is the single source of truth for the bit layout; {@link Flag} is
   * constructed from it, so the two views cannot drift.
   */
  public static final class Mask {

    private Mask() {}

    /** No flag at all. */
    public static final int NONE = 0x00000000;

    // ---- pattern content -------------------------------------------------------------------

    /** @see Flag#CONTAINS_PATTERN */
    public static final int CONTAINS_PATTERN = 0x00000001;
    /** @see Flag#CONTAINS_PATTERN_SEQUENCE */
    public static final int CONTAINS_PATTERN_SEQUENCE = 0x00000002;
    /** @see Flag#CONTAINS_DEFAULT_PATTERN */
    public static final int CONTAINS_DEFAULT_PATTERN = 0x00000004;
    /** @see Flag#CONTAINS_NO_PATTERN */
    public static final int CONTAINS_NO_PATTERN = 0x00000010;
    /** @see Flag#CONTAINS_ALL_DEFAULT_PATTERN */
    public static final int CONTAINS_ALL_DEFAULT_PATTERN = 0x00000008;

    // ---- structure -------------------------------------------------------------------------

    /** @see Flag#CONTAINS_NO_SPECIAL_ARG */
    public static final int CONTAINS_NO_SPECIAL_ARG = 0x00020000;
    /** @see Flag#IS_MATRIX */
    public static final int IS_MATRIX = 0x00008000;
    /** @see Flag#IS_VECTOR */
    public static final int IS_VECTOR = 0x00010000;
    /** @see Flag#IS_DECOMPOSED_PARTIAL_FRACTION */
    public static final int IS_DECOMPOSED_PARTIAL_FRACTION = 0x00000100;
    /** @see Flag#IS_FLATTENED */
    public static final int IS_FLATTENED = 0x00000020;
    /** @see Flag#IS_SORTED */
    public static final int IS_SORTED = 0x00000040;
    /** @see Flag#IS_LISTABLE_THREADED */
    public static final int IS_LISTABLE_THREADED = 0x00040000;
    /** @see Flag#IS_FLAT_ORDERLESS_EVALED */
    public static final int IS_FLAT_ORDERLESS_EVALED = 0x00080000;
    /** @see Flag#IS_EXPANDED */
    public static final int IS_EXPANDED = 0x00800000;
    /** @see Flag#IS_ALL_EXPANDED */
    public static final int IS_ALL_EXPANDED = 0x01000000;
    /** @see Flag#IS_HASH_EVALED */
    public static final int IS_HASH_EVALED = 0x00100000;
    /** @see Flag#IS_DERIVATIVE_EVALED */
    public static final int IS_DERIVATIVE_EVALED = 0x02000000;
    /** @see Flag#BUILT_IN_EVALED */
    public static final int BUILT_IN_EVALED = 0x00200000;
    /** @see Flag#SEQUENCE_FLATTENED */
    public static final int SEQUENCE_FLATTENED = 0x00400000;
    /** @see Flag#IS_COPIED */
    public static final int IS_COPIED = 0x20000000;

    // ---- numeric ---------------------------------------------------------------------------

    /** @see Flag#CONTAINS_NUMERIC_ARG */
    public static final int CONTAINS_NUMERIC_ARG = 0x04000000;
    /** @see Flag#IS_NOT_NUMERIC_CONSTANT */
    public static final int IS_NOT_NUMERIC_CONSTANT = 0x00004000;
    /** @see Flag#IS_NUMERIC_FUNCTION */
    public static final int IS_NUMERIC_FUNCTION = 0x00000200;
    /** @see Flag#IS_NOT_NUMERIC_FUNCTION */
    public static final int IS_NOT_NUMERIC_FUNCTION = 0x00000400;
    /** @see Flag#IS_NUMERIC_FUNCTION_OR_LIST */
    public static final int IS_NUMERIC_FUNCTION_OR_LIST = 0x00000800;
    /** @see Flag#IS_NOT_NUMERIC_FUNCTION_OR_LIST */
    public static final int IS_NOT_NUMERIC_FUNCTION_OR_LIST = 0x00001000;
    /** @see Flag#NUMERIC_DOUBLE_EVALED */
    public static final int NUMERIC_DOUBLE_EVALED = 0x08000000;
    /** @see Flag#NUMERIC_ARBITRARY_EVALED */
    public static final int NUMERIC_ARBITRARY_EVALED = 0x10000000;
    /** @see Flag#IS_NUMERIC_CONSTANT */
    public static final int IS_NUMERIC_CONSTANT = 0x00002000;

    // ---- parser and output -----------------------------------------------------------------

    /** @see Flag#OUTPUT_MULTILINE */
    public static final int OUTPUT_MULTILINE = 0x40000000;
    /** @see Flag#TIMES_PARSED_IMPLICIT */
    public static final int TIMES_PARSED_IMPLICIT = 0x00000080;

    // ---- groups ----------------------------------------------------------------------------

    /** @see Group#PATTERN_EXPR */
    public static final int PATTERN_EXPR =
        CONTAINS_PATTERN | CONTAINS_PATTERN_SEQUENCE | CONTAINS_DEFAULT_PATTERN;
    /** @see Group#MATRIX_OR_VECTOR */
    public static final int MATRIX_OR_VECTOR = IS_MATRIX | IS_VECTOR;
    /** @see Group#FLATTENED_OR_SORTED */
    public static final int FLATTENED_OR_SORTED = IS_FLATTENED | IS_SORTED;
    /** @see Group#NUMERIC */
    public static final int NUMERIC = IS_NUMERIC_FUNCTION | IS_NOT_NUMERIC_FUNCTION
        | IS_NUMERIC_FUNCTION_OR_LIST | IS_NOT_NUMERIC_FUNCTION_OR_LIST | IS_NUMERIC_CONSTANT
        | IS_NOT_NUMERIC_CONSTANT;
    /** @see Group#ARGUMENTS_CHANGED */
    public static final int ARGUMENTS_CHANGED = IS_LISTABLE_THREADED | CONTAINS_NO_SPECIAL_ARG;
    /**
     * The flags which survive an externalization round trip: everything which cannot be
     * recomputed from the tree afterwards. Deliberately the contiguous low block, and
     * deliberately below <code>0x8000</code>, so that the <code>writeShort()</code> the AST
     * classes use can never sign-extend it back.
     *
     * @see Group#PERSISTENT
     */
    public static final int PERSISTENT = CONTAINS_PATTERN | CONTAINS_PATTERN_SEQUENCE
        | CONTAINS_DEFAULT_PATTERN | CONTAINS_ALL_DEFAULT_PATTERN | CONTAINS_NO_PATTERN
        | IS_FLATTENED | IS_SORTED | TIMES_PARSED_IMPLICIT | IS_DECOMPOSED_PARTIAL_FRACTION;

    /** Every bit which is assigned to a {@link Flag}. Bit 31 is the only one still free. */
    public static final int ALL = CONTAINS_PATTERN | CONTAINS_PATTERN_SEQUENCE
        | CONTAINS_DEFAULT_PATTERN | CONTAINS_NO_PATTERN | CONTAINS_ALL_DEFAULT_PATTERN
        | CONTAINS_NO_SPECIAL_ARG | IS_MATRIX | IS_VECTOR | IS_DECOMPOSED_PARTIAL_FRACTION
        | IS_FLATTENED | IS_SORTED | IS_LISTABLE_THREADED | IS_FLAT_ORDERLESS_EVALED | IS_EXPANDED
        | IS_ALL_EXPANDED | IS_HASH_EVALED | IS_DERIVATIVE_EVALED | BUILT_IN_EVALED
        | SEQUENCE_FLATTENED | IS_COPIED | CONTAINS_NUMERIC_ARG | IS_NOT_NUMERIC_CONSTANT
        | IS_NUMERIC_FUNCTION | IS_NOT_NUMERIC_FUNCTION | IS_NUMERIC_FUNCTION_OR_LIST
        | IS_NOT_NUMERIC_FUNCTION_OR_LIST | NUMERIC_DOUBLE_EVALED | NUMERIC_ARBITRARY_EVALED
        | IS_NUMERIC_CONSTANT | OUTPUT_MULTILINE | TIMES_PARSED_IMPLICIT;
  }

  /**
   * A single evaluation flag of an {@link IAST}.
   *
   * @see IAST#hasFlag(Flag)
   * @see IAST#addFlag(Flag)
   */
  public enum Flag {

    /** The head or one of the arguments of the list or sublists contains a pattern object. */
    CONTAINS_PATTERN(Mask.CONTAINS_PATTERN, Mask.CONTAINS_NO_PATTERN),

    /** The head or one of the arguments of the list or sublists contains a pattern sequence. */
    CONTAINS_PATTERN_SEQUENCE(Mask.CONTAINS_PATTERN_SEQUENCE, Mask.CONTAINS_NO_PATTERN),

    /**
     * One of the arguments of the list contains a pattern object which can be set to a default
     * value (or optional value).
     */
    CONTAINS_DEFAULT_PATTERN(Mask.CONTAINS_DEFAULT_PATTERN, Mask.CONTAINS_NO_PATTERN),

    /**
     * All of the arguments of the list are pattern objects which can be set to a default value (or
     * optional value).
     */
    CONTAINS_ALL_DEFAULT_PATTERN(Mask.CONTAINS_ALL_DEFAULT_PATTERN, Mask.CONTAINS_NO_PATTERN),

    /**
     * The list or the lists subexpressions contain no pattern object.
     * <p>
     * Mutually exclusive with every other <code>CONTAINS_..._PATTERN</code> flag. Note that pattern
     * presence is deliberately <b>not</b> modelled as a {@link Trait}: the &quot;yes&quot; side is a
     * set rather than a single answer, and {@code PatternMatcher} distinguishes the individual
     * kinds.
     */
    CONTAINS_NO_PATTERN(Mask.CONTAINS_NO_PATTERN,
        Mask.PATTERN_EXPR | Mask.CONTAINS_ALL_DEFAULT_PATTERN),

    /**
     * Is set, if the arguments of this expression were scanned and none of them is one of the few
     * expressions the evaluation loop has to look for: {@link S#Unevaluated}, {@link S#Sequence},
     * {@link S#ConditionalExpression}, <code>Rubi`Dist</code> or the symbol {@link S#Nothing}.
     * <p>
     * Evaluating one expression used to scan its arguments for these separately and repeatedly -
     * {@link EvalEngine#evalArgs(IAST, int, boolean)}, {@link EvalEngine#evalRules(ISymbol, IAST)},
     * {@code evalNoAttributes()}, {@link F#flattenSequence(IAST)} and
     * {@code extractConditionalExpression(false)} - although all of them are extremely rare
     * (measured over the test suite: 7 {@link S#Sequence} and 393 {@link S#ConditionalExpression}
     * arguments in tens of millions of scans). One scan now answers all of them and the result is
     * memoized here.
     * <p>
     * Dropped again by {@code AbstractAST#argumentsChanged()} whenever an argument is appended or
     * replaced.
     *
     * @see IAST#hasSpecialArg()
     * @see Group#ARGUMENTS_CHANGED
     */
    CONTAINS_NO_SPECIAL_ARG(Mask.CONTAINS_NO_SPECIAL_ARG, Mask.NONE),

    /** This expression represents a matrix. */
    IS_MATRIX(Mask.IS_MATRIX, Mask.NONE),

    /** This expression represents a vector. */
    IS_VECTOR(Mask.IS_VECTOR, Mask.NONE),

    /**
     * This expression represents an already decomposed partial fraction expression.
     *
     * @see S#Apart
     */
    IS_DECOMPOSED_PARTIAL_FRACTION(Mask.IS_DECOMPOSED_PARTIAL_FRACTION, Mask.NONE),

    /** This expression is an already flattened expression. */
    IS_FLATTENED(Mask.IS_FLATTENED, Mask.NONE),

    /**
     * This expression is an already sorted expression (i.e. sorted with the <code>Order()</code>
     * function).
     */
    IS_SORTED(Mask.IS_SORTED, Mask.NONE),

    /**
     * This expression has already applied the <code>Listable</code> attribute to its argument
     * expressions.
     * <p>
     * <b>Not</b> to be confused with the symbol attribute {@link ISymbol#NONTHREADABLE}, which is a
     * user-visible property of a <i>head</i> saying that it must never be threaded into. The two
     * are read in the same method - {@code EvalEngine#threadASTListArgs} memoizes this flag while
     * consulting {@code NONTHREADABLE} on the head symbol - and they live in different fields, even
     * though {@code NONTHREADABLE} happens to have the same bit value as
     * {@link #SEQUENCE_FLATTENED}.
     * <p>
     * Dropped again by {@code AbstractAST#argumentsChanged()}.
     *
     * @see Group#ARGUMENTS_CHANGED
     */
    IS_LISTABLE_THREADED(Mask.IS_LISTABLE_THREADED, Mask.NONE),

    /** This expression is an already evaled expression. */
    IS_FLAT_ORDERLESS_EVALED(Mask.IS_FLAT_ORDERLESS_EVALED, Mask.NONE),

    /** This expression is already evaluated by the <code>Expand()</code> function. */
    IS_EXPANDED(Mask.IS_EXPANDED, Mask.NONE),

    /** This expression is already evaluated by the <code>ExpandAll()</code> function. */
    IS_ALL_EXPANDED(Mask.IS_ALL_EXPANDED, Mask.NONE),

    /** This expression is already evaluated by a <code>HashedOrderlessMatcher</code> function. */
    IS_HASH_EVALED(Mask.IS_HASH_EVALED, Mask.NONE),

    /** This expression is already evaluated in the <code>Derivative[]</code> function. */
    IS_DERIVATIVE_EVALED(Mask.IS_DERIVATIVE_EVALED, Mask.NONE),

    /**
     * Is set, if the built-in function associated with this object was evaluated and no further
     * evaluation is needed for the built-in evaluation function.
     * <p>
     * An expression has exactly one head, so this single bit carries a head-specific meaning at
     * three places: <code>INTERVAL_NORMALIZED</code> in {@code IntervalSym},
     * <code>INTERVAL_DATA_NORMALIZED</code> in {@code IntervalDataSym} and
     * <code>POWER_EVALED</code> in {@code Arithmetic}. Those readings cannot collide with each
     * other, because no expression has two of those heads.
     */
    BUILT_IN_EVALED(Mask.BUILT_IN_EVALED, Mask.NONE),

    /** The {@link S#Sequence} arguments of this expression were already spliced in. */
    SEQUENCE_FLATTENED(Mask.SEQUENCE_FLATTENED, Mask.NONE),

    /**
     * Flag which will be set for new allocated IAST expressions during a capsulated traversal
     * algorithm. Temporary flag which should be deleted after traversing the expression.
     */
    IS_COPIED(Mask.IS_COPIED, Mask.NONE),

    /**
     * Is set, if one of the (nested) arguments of a numeric function contains a numeric expression.
     */
    CONTAINS_NUMERIC_ARG(Mask.CONTAINS_NUMERIC_ARG, Mask.NONE),

    /**
     * This expression is a numeric constant.
     *
     * @see Trait#NUMERIC_CONSTANT
     */
    IS_NUMERIC_CONSTANT(Mask.IS_NUMERIC_CONSTANT, Mask.IS_NOT_NUMERIC_CONSTANT),

    /**
     * This expression is not a numeric constant.
     *
     * @see Trait#NUMERIC_CONSTANT
     */
    IS_NOT_NUMERIC_CONSTANT(Mask.IS_NOT_NUMERIC_CONSTANT, Mask.IS_NUMERIC_CONSTANT),

    /**
     * This expression is a numeric function.
     *
     * @see Trait#NUMERIC_FUNCTION
     */
    IS_NUMERIC_FUNCTION(Mask.IS_NUMERIC_FUNCTION, Mask.IS_NOT_NUMERIC_FUNCTION),

    /**
     * This expression is not a numeric function.
     *
     * @see Trait#NUMERIC_FUNCTION
     */
    IS_NOT_NUMERIC_FUNCTION(Mask.IS_NOT_NUMERIC_FUNCTION, Mask.IS_NUMERIC_FUNCTION),

    /**
     * This expression is a numeric function or a list.
     *
     * @see Trait#NUMERIC_FUNCTION_OR_LIST
     */
    IS_NUMERIC_FUNCTION_OR_LIST(Mask.IS_NUMERIC_FUNCTION_OR_LIST,
        Mask.IS_NOT_NUMERIC_FUNCTION_OR_LIST),

    /**
     * This expression is neither a numeric function nor a list.
     *
     * @see Trait#NUMERIC_FUNCTION_OR_LIST
     */
    IS_NOT_NUMERIC_FUNCTION_OR_LIST(Mask.IS_NOT_NUMERIC_FUNCTION_OR_LIST,
        Mask.IS_NUMERIC_FUNCTION_OR_LIST),

    /** This expression was already evaluated to a machine precision floating point number. */
    NUMERIC_DOUBLE_EVALED(Mask.NUMERIC_DOUBLE_EVALED, Mask.NONE),

    /** This expression was already evaluated to an arbitrary precision floating point number. */
    NUMERIC_ARBITRARY_EVALED(Mask.NUMERIC_ARBITRARY_EVALED, Mask.NONE),

    /** This List expression args should be printed in multi-line style. */
    OUTPUT_MULTILINE(Mask.OUTPUT_MULTILINE, Mask.NONE),

    /**
     * The <code>Times(...)</code> expression was determined implicitly in the expression parser.
     */
    TIMES_PARSED_IMPLICIT(Mask.TIMES_PARSED_IMPLICIT, Mask.NONE);

    private final int mask;

    private final int clears;

    private Flag(int mask, int clears) {
      this.mask = mask;
      this.clears = clears;
    }

    /**
     * The single bit which represents this flag.
     *
     * @return the bit value of this flag
     */
    public int mask() {
      return mask;
    }

    /**
     * The bits which have to be dropped when this flag is set, because they state the opposite of
     * it. Setting a flag through {@link IAST#addFlag(Flag)} clears them, which is what makes the
     * contradictory state unreachable.
     *
     * @return the bits to clear, or {@link Mask#NONE} if this flag excludes no other
     */
    public int clears() {
      return clears;
    }
  }

  /**
   * A named group of evaluation flags.
   * <p>
   * Because a group has more than one bit, asking about it forces the caller to name the
   * quantifier: {@link IAST#hasAnyFlag(Group)}, {@link IAST#hasAllFlags(Group)} or
   * {@link IAST#hasNoFlag(Group)}. That is deliberate - the raw <code>int</code> API this replaces
   * had an <code>isEvalFlagOn</code> which meant &quot;all of&quot; and an
   * <code>isEvalFlagOff</code> which meant &quot;none of&quot;, so the two were not negations of
   * each other.
   */
  public enum Group {

    /**
     * The head or one of the arguments (or sublists) contains a pattern object of some kind.
     *
     * @see IExpr#isPatternExpr()
     */
    PATTERN_EXPR(Mask.PATTERN_EXPR),

    /** This expression represents a matrix or a vector. */
    MATRIX_OR_VECTOR(Mask.MATRIX_OR_VECTOR),

    /** This expression is already flattened or sorted. */
    FLATTENED_OR_SORTED(Mask.FLATTENED_OR_SORTED),

    /** The memoized answers about whether this expression is a numeric function. */
    NUMERIC(Mask.NUMERIC),

    /**
     * The memoized answers about the arguments which become invalid as soon as an argument is
     * appended or replaced.
     *
     * @see Flag#IS_LISTABLE_THREADED
     * @see Flag#CONTAINS_NO_SPECIAL_ARG
     */
    ARGUMENTS_CHANGED(Mask.ARGUMENTS_CHANGED),

    /**
     * The flags which survive an externalization round trip - the ones which cannot be recomputed
     * from the tree afterwards.
     * <p>
     * The pattern flags are here because {@link IExpr#isPatternExpr()} is a pure memo read with no
     * fallback computation; {@link Flag#IS_FLATTENED} / {@link Flag#IS_SORTED} because
     * {@code PatternMatching#evalLHS} reads "neither set" as "this left-hand side still needs
     * evaluation"; {@link Flag#TIMES_PARSED_IMPLICIT} because only the parser can know it; and
     * {@link Flag#IS_DECOMPOSED_PARTIAL_FRACTION} because {@code Integrate} reads it as a guard.
     * Everything else is a cache which a deserialized expression recomputes on demand.
     */
    PERSISTENT(Mask.PERSISTENT);

    private final int mask;

    private Group(int mask) {
      this.mask = mask;
    }

    /**
     * The bits which belong to this group.
     *
     * @return the combined bit values of the group members
     */
    public int mask() {
      return mask;
    }
  }

  /**
   * A property of an {@link IAST} whose answer is memoized in two bits, so that &quot;not computed
   * yet&quot; is distinguishable from &quot;computed, and the answer is no&quot;.
   *
   * @see IAST#getTrait(Trait)
   * @see IAST#setTrait(Trait, boolean)
   */
  public enum Trait {

    /**
     * Is this expression a numeric function?
     *
     * @see IExpr#isNumericFunction(boolean)
     */
    NUMERIC_FUNCTION(Mask.IS_NUMERIC_FUNCTION, Mask.IS_NOT_NUMERIC_FUNCTION),

    /**
     * Is this expression a numeric function or a list?
     *
     * @see IExpr#isNumericFunction(boolean)
     */
    NUMERIC_FUNCTION_OR_LIST(Mask.IS_NUMERIC_FUNCTION_OR_LIST,
        Mask.IS_NOT_NUMERIC_FUNCTION_OR_LIST),

    /**
     * Is this expression a numeric constant?
     *
     * @see IExpr#isNumericConstant()
     */
    NUMERIC_CONSTANT(Mask.IS_NUMERIC_CONSTANT, Mask.IS_NOT_NUMERIC_CONSTANT);

    private final int yesMask;

    private final int noMask;

    private Trait(int yesMask, int noMask) {
      this.yesMask = yesMask;
      this.noMask = noMask;
    }

    /** The bit which is set if the answer is &quot;yes&quot;. */
    public int yesMask() {
      return yesMask;
    }

    /** The bit which is set if the answer is &quot;no&quot;. */
    public int noMask() {
      return noMask;
    }

    /** Both bits of this trait. */
    public int mask() {
      return yesMask | noMask;
    }

    /**
     * Read this trait out of a raw flag word.
     *
     * @param evalFlags the raw evaluation flags
     * @return the memoized answer, or {@link Ternary#UNKNOWN} if it was not computed yet
     */
    public Ternary read(int evalFlags) {
      if ((evalFlags & yesMask) != 0) {
        return Ternary.TRUE;
      }
      if ((evalFlags & noMask) != 0) {
        return Ternary.FALSE;
      }
      return Ternary.UNKNOWN;
    }

    /**
     * Write this trait into a raw flag word, replacing whatever was memoized before. The bit for
     * the opposite answer is always cleared, so the contradictory state cannot be produced.
     *
     * @param evalFlags the raw evaluation flags
     * @param value the answer to memoize
     * @return the updated evaluation flags
     */
    public int write(int evalFlags, Ternary value) {
      int cleared = evalFlags & ~(yesMask | noMask);
      switch (value) {
        case TRUE:
          return cleared | yesMask;
        case FALSE:
          return cleared | noMask;
        default:
          return cleared;
      }
    }
  }

  /**
   * The value of a {@link Trait}.
   * <p>
   * {@link #UNKNOWN} means &quot;not computed yet, ask again&quot;. That is a different statement
   * from {@link IExpr.COMPARE_TERNARY#UNDECIDABLE}, which is a final answer.
   */
  public enum Ternary {

    /** The property was not computed yet. */
    UNKNOWN,

    /** The property was computed and holds. */
    TRUE,

    /** The property was computed and does not hold. */
    FALSE
  }
}
