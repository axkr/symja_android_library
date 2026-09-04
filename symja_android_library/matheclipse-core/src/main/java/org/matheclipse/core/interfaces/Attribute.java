package org.matheclipse.core.interfaces;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.Comparators;

/**
 * The WMA <b>attributes</b> a symbol can carry - {@code Flat}, {@code Orderless}, {@code HoldAll},
 * {@code Listable}, {@code Protected}, ... - as a type.
 * <p>
 * The attributes are stored as the bits of a single <code>int</code> word (see
 * {@link ISymbol#getAttributes()}), so this enum adds no memory to a symbol; it only gives the bits
 * a name and owns the translation to and from the user-visible symbols.
 *
 * <h3>Composite attributes</h3>
 * <p>
 * Unlike a plain flag set, several attributes are <b>unions of others</b>:
 * {@code HOLDALL == HOLDFIRST | HOLDREST}, {@code HOLDCOMPLETE} adds a bit to {@code HOLDALL},
 * {@code HOLDALLCOMPLETE} adds one more plus {@code SEQUENCEHOLD}, {@code NHOLDALL == NHOLDFIRST |
 * NHOLDREST}, and {@code LOCKED} adds a bit to {@code PROTECTED}. That is why
 * {@link #isSetIn(int)} asks whether <b>all</b> bits are set, and why {@link #toList(int)} reports
 * only the most specific attribute of a chain.
 *
 * <h3>Not to be confused with</h3>
 * <p>
 * {@link ISymbol#DIRTY_FLAG_ASSIGNED_VALUE} and friends are the symbol's <i>evaluation flags</i>, a
 * different word; {@link org.matheclipse.core.interfaces.EvalFlags} are the evaluation flags of an
 * {@link IAST}. All three live in different fields and must never be mixed.
 *
 * <h3>Initialization</h3>
 * <p>
 * <b>This enum must never gain a reference-typed instance field.</b> It stores the
 * {@link ID} of its symbol as an <code>int</code> - a compile-time constant - and resolves the
 * symbol lazily in {@link #symbol()}. Holding an {@code IBuiltInSymbol} directly would create the
 * initialization cycle {@code ISymbol -> Attribute -> S -> BuiltInSymbol -> ISymbol}, and if
 * {@code S.<clinit>} happened to run first the nested initialization would be skipped and every
 * constant here would silently capture <code>null</code>. {@code AttributeTest} pins this by
 * asserting that every instance field is primitive.
 */
public enum Attribute {

  /** A symbol with a constant value. */
  CONSTANT(ISymbol.CONSTANT, ID.Constant),

  /** An associative function; evaluation flattens the argument list. */
  FLAT(ISymbol.FLAT, ID.Flat),

  /** No argument is evaluated. Union of {@link #HOLDFIRST} and {@link #HOLDREST}. */
  HOLDALL(ISymbol.HOLDALL, ID.HoldAll),

  /** Like {@link #HOLDCOMPLETE}, and additionally holds {@link S#Sequence} arguments. */
  HOLDALLCOMPLETE(ISymbol.HOLDALLCOMPLETE, ID.HoldAllComplete),

  /** Like {@link #HOLDALL}, and additionally excluded from upvalue search. */
  HOLDCOMPLETE(ISymbol.HOLDCOMPLETE, ID.HoldComplete),

  /** The first argument is not evaluated. */
  HOLDFIRST(ISymbol.HOLDFIRST, ID.HoldFirst),

  /** All arguments but the first are not evaluated. */
  HOLDREST(ISymbol.HOLDREST, ID.HoldRest),

  /** A function which threads over lists in its arguments. */
  LISTABLE(ISymbol.LISTABLE, ID.Listable),

  /**
   * A locked symbol, whose attributes cannot be changed again.
   * <p>
   * Two peculiarities, both preserved from the pre-enum behaviour: {@code Attributes[f]} reports
   * <b>both</b> {@code Locked} and {@code Protected} (hence {@code suppressesSubsumed = false}),
   * and {@code ClearAttributes} refuses to remove it (hence {@code userClearable = false}).
   */
  LOCKED(ISymbol.LOCKED, ID.Locked, false, false),

  /** Not evaluated numerically. Union of {@link #NHOLDFIRST} and {@link #NHOLDREST}. */
  NHOLDALL(ISymbol.NHOLDALL, ID.NHoldAll),

  /** The first argument is not evaluated numerically. */
  NHOLDFIRST(ISymbol.NHOLDFIRST, ID.NHoldFirst),

  /** All arguments but the first are not evaluated numerically. */
  NHOLDREST(ISymbol.NHOLDREST, ID.NHoldRest),

  /**
   * A non-scalar quantity, which is never combined with the elements of a list argument.
   *
   * @see <a href="https://reference.wolfram.com/language/ref/NonThreadable.html">NonThreadable</a>
   */
  NONTHREADABLE(ISymbol.NONTHREADABLE, ID.NonThreadable),

  /** A numeric function. */
  NUMERICFUNCTION(ISymbol.NUMERICFUNCTION, ID.NumericFunction),

  /** A function transformation: {@code f(x)} gives {@code x}. */
  ONEIDENTITY(ISymbol.ONEIDENTITY, ID.OneIdentity),

  /** A commutative function; evaluation sorts the arguments. */
  ORDERLESS(ISymbol.ORDERLESS, ID.Orderless),

  /** A symbol for which no rule definition is possible. */
  PROTECTED(ISymbol.PROTECTED, ID.Protected),

  /** A symbol whose definition is not displayed. */
  READPROTECTED(ISymbol.READPROTECTED, ID.ReadProtected),

  /** A function whose {@link S#Sequence} arguments are not flattened out. */
  SEQUENCEHOLD(ISymbol.SEQUENCEHOLD, ID.SequenceHold);

  /**
   * Cached because {@link #values()} clones its array on every call, and {@link #toList(int)}
   * iterates it twice.
   */
  private static final Attribute[] VALUES = values();

  private final int mask;

  private final int symbolID;

  private final boolean suppressesSubsumed;

  private final boolean userClearable;

  private Attribute(int mask, int symbolID) {
    this(mask, symbolID, true, true);
  }

  private Attribute(int mask, int symbolID, boolean suppressesSubsumed, boolean userClearable) {
    this.mask = mask;
    this.symbolID = symbolID;
    this.suppressesSubsumed = suppressesSubsumed;
    this.userClearable = userClearable;
  }

  /**
   * The bits of this attribute. A composite attribute has more than one bit set.
   *
   * @return the bit mask, identical to the {@code ISymbol} constant of the same name
   */
  public int mask() {
    return mask;
  }

  /**
   * Is this attribute set in the given attribute word?
   * <p>
   * <b>All</b> bits have to be set, which is what makes the test correct for a composite: a symbol
   * with only {@code HoldFirst} does not have {@link #HOLDALL}.
   *
   * @param attributes the attribute word
   * @return <code>true</code> if every bit of this attribute is set
   */
  public boolean isSetIn(int attributes) {
    return (attributes & mask) == mask;
  }

  /**
   * Is <b>at least one</b> bit of this attribute set in the given attribute word?
   * <p>
   * For a single-bit attribute this is the same question as {@link #isSetIn(int)}. For a composite
   * it is deliberately weaker: {@code HOLDALL.isAnySetIn(w)} asks &quot;HoldFirst <i>or</i>
   * HoldRest&quot;, whereas {@code HOLDALL.isSetIn(w)} asks for both. Callers which used to write
   * <code>(word &amp; HOLDALL) == NOATTRIBUTE</code> meant this one.
   *
   * @param attributes the attribute word
   * @return <code>true</code> if any bit of this attribute is set
   */
  public boolean isAnySetIn(int attributes) {
    return (attributes & mask) != 0;
  }

  /**
   * Switch this attribute on in the given attribute word.
   *
   * @param attributes the attribute word
   * @return the updated word
   */
  public int setIn(int attributes) {
    return attributes | mask;
  }

  /**
   * Switch this attribute off in the given attribute word.
   *
   * @param attributes the attribute word
   * @return the updated word
   */
  public int clearIn(int attributes) {
    return attributes & ~mask;
  }

  /**
   * The {@link ID} of the symbol which denotes this attribute.
   *
   * @return the symbol id
   */
  public int symbolID() {
    return symbolID;
  }

  /**
   * The user-visible symbol which denotes this attribute, for example {@link S#HoldAll}.
   * <p>
   * Resolved lazily on every call - see the initialization note in the class javadoc.
   *
   * @return the attribute symbol
   */
  public IBuiltInSymbol symbol() {
    return S.symbol(symbolID);
  }

  /**
   * May {@code ClearAttributes} remove this attribute?
   *
   * @return <code>false</code> for {@link #LOCKED}, <code>true</code> otherwise
   */
  public boolean isUserClearable() {
    return userClearable;
  }

  /**
   * Get the attribute denoted by the symbol with the given {@link ID}.
   *
   * @param symbolID the id of a built-in symbol
   * @return the attribute, or <code>null</code> if the symbol does not denote one
   */
  public static Attribute ofSymbolID(int symbolID) {
    switch (symbolID) {
      case ID.Constant:
        return CONSTANT;
      case ID.Flat:
        return FLAT;
      case ID.HoldAll:
        return HOLDALL;
      case ID.HoldAllComplete:
        return HOLDALLCOMPLETE;
      case ID.HoldComplete:
        return HOLDCOMPLETE;
      case ID.HoldFirst:
        return HOLDFIRST;
      case ID.HoldRest:
        return HOLDREST;
      case ID.Listable:
        return LISTABLE;
      case ID.Locked:
        return LOCKED;
      case ID.NHoldAll:
        return NHOLDALL;
      case ID.NHoldFirst:
        return NHOLDFIRST;
      case ID.NHoldRest:
        return NHOLDREST;
      case ID.NonThreadable:
        return NONTHREADABLE;
      case ID.NumericFunction:
        return NUMERICFUNCTION;
      case ID.OneIdentity:
        return ONEIDENTITY;
      case ID.Orderless:
        return ORDERLESS;
      case ID.Protected:
        return PROTECTED;
      case ID.ReadProtected:
        return READPROTECTED;
      case ID.SequenceHold:
        return SEQUENCEHOLD;
      default:
        return null;
    }
  }

  /**
   * Get the attribute denoted by the given expression.
   *
   * @param expr the expression, expected to be an attribute symbol like {@link S#HoldAll}
   * @return the attribute, or <code>null</code> if the expression does not denote one
   */
  public static Attribute of(IExpr expr) {
    return expr.isSymbol() ? ofSymbolID(((ISymbol) expr).ordinal()) : null;
  }

  /**
   * Decode an attribute word into the list of symbols {@code Attributes[]} reports for it.
   * <p>
   * Only the most specific attribute of a chain is emitted - a symbol with {@link #HOLDALL} set
   * reports {@code HoldAll}, not {@code HoldFirst} and {@code HoldRest}. {@link #LOCKED} is the
   * documented exception: it reports {@code Protected} alongside itself.
   *
   * @param attributes the attribute word
   * @return the attributes as a canonically sorted list of symbols
   */
  public static IAST toList(int attributes) {
    IASTAppendable result = F.ListAlloc(Integer.bitCount(attributes));
    for (int i = 0; i < VALUES.length; i++) {
      Attribute attribute = VALUES[i];
      if (attribute.isSetIn(attributes) && !attribute.isSubsumedIn(attributes)) {
        result.append(attribute.symbol());
      }
    }
    result.sortInplace(Comparators.CANONICAL_COMPARATOR);
    return result;
  }

  /**
   * Is this attribute implied by a broader attribute which is also set, and which absorbs the
   * attributes it contains?
   */
  private boolean isSubsumedIn(int attributes) {
    for (int i = 0; i < VALUES.length; i++) {
      Attribute broader = VALUES[i];
      if (broader.suppressesSubsumed && broader.mask != mask && (mask & broader.mask) == mask
          && broader.isSetIn(attributes)) {
        return true;
      }
    }
    return false;
  }
}
