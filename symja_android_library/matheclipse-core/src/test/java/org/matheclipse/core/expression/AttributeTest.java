package org.matheclipse.core.expression;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.generic.Comparators;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Characterization tests for the symbol attribute word.
 * <p>
 * {@link #reference(int)} is a verbatim copy of {@code ISymbol.attributesList} as it stood
 * <i>before</i> the attribute enum was introduced. Every later change to the decoder has to keep
 * producing exactly what this copy produces, for every reachable attribute word - which is what
 * makes it safe to replace a 74-line hand-ordered cascade whose output is asserted by ~40 string
 * comparisons in {@code LowercaseTestCase}.
 */
public class AttributeTest {

  /** The bits which {@link ISymbol#ALL_ATTRIBUTES} can actually contain. */
  private static final int[] ATTRIBUTE_BITS = attributeBits();

  @BeforeEach
  public void setUp() throws Exception {
    // the decoder appends S.* symbols, so the symbol table has to be up
    F.await();
  }

  /**
   * The whole point of this class: the decoder is exercised over <b>every</b> attribute word that
   * can be built from the defined bits - 2^17 of them - and must agree with the pre-refactor
   * implementation on all of them.
   */
  @Test
  public void testDecoderMatchesTheReferenceImplementationExhaustively() {
    List<String> problems = new ArrayList<String>();
    final int total = 1 << ATTRIBUTE_BITS.length;
    for (int combination = 0; combination < total; combination++) {
      int attributes = wordOf(combination);

      IAST expected = reference(attributes);
      IAST actual = ISymbol.attributesList(attributes);
      if (!expected.equals(actual) && problems.size() < 10) {
        problems.add(String.format("0x%08X: expected %s but was %s", attributes, expected, actual));
      }
    }
    assertEquals(Collections.emptyList(), problems,
        "the attribute decoder changed for " + total + " checked words");
  }

  /** The {@link ISymbol} overload has to agree with the {@code int} one on a real symbol. */
  @Test
  public void testSymbolOverloadAgreesWithTheIntOverload() {
    List<String> problems = new ArrayList<String>();
    // Context.DUMMY is neither SYSTEM nor RUBI, so isLocked() is false and setAttributes works
    ISymbol symbol = new Symbol("attrTest", Context.DUMMY);
    for (int combination = 0; combination < (1 << ATTRIBUTE_BITS.length); combination += 97) {
      int attributes = wordOf(combination);
      symbol.setAttributes(attributes);

      IAST expected = reference(attributes);
      IAST actual = ISymbol.attributesList(symbol);
      if (!expected.equals(actual) && problems.size() < 10) {
        problems.add(String.format("0x%08X: expected %s but was %s", attributes, expected, actual));
      }
    }
    assertEquals(Collections.emptyList(), problems);
  }

  /**
   * The composite attributes are unions of their parts, and the discriminator bits keep them
   * distinguishable. This is the invariant the bit values must preserve.
   */
  @Test
  public void testCompositesAreUnionsOfTheirParts() {
    assertEquals(ISymbol.HOLDFIRST | ISymbol.HOLDREST, ISymbol.HOLDALL, "HOLDALL");
    assertEquals(ISymbol.NHOLDFIRST | ISymbol.NHOLDREST, ISymbol.NHOLDALL, "NHOLDALL");
    assertEquals(ISymbol.FLAT | ISymbol.ORDERLESS, ISymbol.FLATORDERLESS, "FLATORDERLESS");

    // strictly larger than what they subsume, so Attributes[] can tell them apart
    assertEquals(ISymbol.HOLDALL, ISymbol.HOLDCOMPLETE & ISymbol.HOLDALL, "HOLDCOMPLETE > HOLDALL");
    assertEquals(ISymbol.HOLDCOMPLETE | ISymbol.SEQUENCEHOLD,
        ISymbol.HOLDALLCOMPLETE & (ISymbol.HOLDCOMPLETE | ISymbol.SEQUENCEHOLD),
        "HOLDALLCOMPLETE > HOLDCOMPLETE|SEQUENCEHOLD");
    assertEquals(ISymbol.PROTECTED, ISymbol.LOCKED & ISymbol.PROTECTED, "LOCKED > PROTECTED");
  }

  /**
   * The enum decoder must match the pre-refactor cascade on every reachable word. This is what
   * makes replacing {@code ISymbol.attributesList} safe, and it is asserted <i>before</i> the
   * replacement happens.
   */
  @Test
  public void testEnumDecoderMatchesTheReferenceImplementationExhaustively() {
    List<String> problems = new ArrayList<String>();
    final int total = 1 << ATTRIBUTE_BITS.length;
    for (int combination = 0; combination < total; combination++) {
      int attributes = wordOf(combination);

      IAST expected = reference(attributes);
      IAST actual = Attribute.toList(attributes);
      if (!expected.equals(actual) && problems.size() < 10) {
        problems.add(String.format("0x%08X: expected %s but was %s", attributes, expected, actual));
      }
    }
    assertEquals(Collections.emptyList(), problems,
        "Attribute.toList disagrees with the pre-enum decoder");
  }

  /** Every constant must carry exactly the bits of the {@code ISymbol} field of the same name. */
  @Test
  public void testMasksMatchTheISymbolConstants() throws Exception {
    for (Attribute attribute : Attribute.values()) {
      int expected = ISymbol.class.getField(attribute.name()).getInt(null);
      assertEquals(expected, attribute.mask(), attribute.name());
    }
  }

  @Test
  public void testMasksAreDistinctNonZeroAndKnown() {
    int union = ISymbol.NOATTRIBUTE;
    for (Attribute attribute : Attribute.values()) {
      assertNotEquals(ISymbol.NOATTRIBUTE, attribute.mask(), attribute.name());
      assertEquals(attribute.mask(), attribute.mask() & ISymbol.ALL_ATTRIBUTES,
          attribute + " has a bit which is not in ALL_ATTRIBUTES");
      union |= attribute.mask();
      for (Attribute other : Attribute.values()) {
        if (attribute != other) {
          assertNotEquals(attribute.mask(), other.mask(), attribute + " and " + other);
        }
      }
    }
    assertEquals(ISymbol.ALL_ATTRIBUTES, union, "ALL_ATTRIBUTES is not the union of the attributes");
  }

  /**
   * The enum must stay a leaf in the class-initialization graph: a reference-typed instance field
   * could only be an {@code S} symbol, which would create the cycle described in
   * {@link Attribute}'s javadoc and would silently capture <code>null</code>.
   */
  @Test
  public void testNoReferenceTypedInstanceFields() {
    for (Field field : Attribute.class.getDeclaredFields()) {
      if (field.isSynthetic() || Modifier.isStatic(field.getModifiers())) {
        continue;
      }
      assertTrue(field.getType().isPrimitive(),
          "Attribute." + field.getName() + " is " + field.getType().getSimpleName()
              + "; a reference field here re-creates the S initialization cycle");
    }
  }

  @Test
  public void testSymbolRoundTrip() {
    for (Attribute attribute : Attribute.values()) {
      assertNotNull(attribute.symbol(), attribute.name());
      assertSame(attribute, Attribute.of(attribute.symbol()), attribute.name());
      assertSame(attribute, Attribute.ofSymbolID(attribute.symbolID()), attribute.name());
      // guards against a symbol-table regeneration silently renumbering the IDs
      assertEquals(attribute.name(), attribute.symbol().getSymbolName().toUpperCase(),
          "the id of " + attribute + " points at a different symbol");
    }
  }

  @Test
  public void testNonAttributeSymbolsAreNotAttributes() {
    assertNull(Attribute.ofSymbolID(ID.UNKNOWN));
    assertNull(Attribute.ofSymbolID(ID.Sin), "Sin is not an attribute");
    assertNull(Attribute.of(S.Sin), "Sin is not an attribute");
    assertNull(Attribute.of(F.C1), "an integer is not an attribute");
  }

  /**
   * The engine gate is now derived, so this pins what it claims: exactly the attributes the
   * evaluator never dispatches on are excluded.
   */
  @Test
  public void testEngineGateExcludesExactlyTheNonDispatchingAttributes() {
    int excluded = ISymbol.PROTECTED | ISymbol.READPROTECTED | ISymbol.LOCKED
        | ISymbol.NONTHREADABLE | ISymbol.SEQUENCEHOLD;
    assertEquals(excluded, ISymbol.ALL_ATTRIBUTES & ~ISymbol.EVAL_ENGINE_ATTRIBUTES,
        "the derived engine gate excludes a different set than documented");

    // it differs from the hand-written 0xFFF077FF it replaced only by the bit freed with
    // PACKAGE_LOADED, and including a free bit is the conservative direction: an unknown
    // attribute takes the slow path
    assertEquals(0x00000800, ISymbol.EVAL_ENGINE_ATTRIBUTES ^ 0xFFF077FF,
        "the engine gate changed by more than the freed PACKAGE_LOADED bit");
  }

  private static int wordOf(int combination) {
    int attributes = ISymbol.NOATTRIBUTE;
    for (int bit = 0; bit < ATTRIBUTE_BITS.length; bit++) {
      if ((combination & (1 << bit)) != 0) {
        attributes |= ATTRIBUTE_BITS[bit];
      }
    }
    return attributes;
  }

  private static int[] attributeBits() {
    int[] bits = new int[Integer.bitCount(ISymbol.ALL_ATTRIBUTES)];
    int index = 0;
    for (int bit = 0; bit < 32; bit++) {
      if ((ISymbol.ALL_ATTRIBUTES & (1 << bit)) != 0) {
        bits[index++] = 1 << bit;
      }
    }
    return bits;
  }

  // ---------------------------------------------------------------------------------------------
  // Verbatim copy of ISymbol.attributesList as of the commit before the Attribute enum.
  // Do not "clean up" - its value is that it is the old code.
  // ---------------------------------------------------------------------------------------------
  private static IAST reference(int attributes) {
    IASTAppendable result = F.ListAlloc(Integer.bitCount(attributes));

    if ((attributes & ISymbol.CONSTANT) != ISymbol.NOATTRIBUTE) {
      result.append(S.Constant);
    }
    if ((attributes & ISymbol.FLAT) != ISymbol.NOATTRIBUTE) {
      result.append(S.Flat);
    }
    if ((attributes & ISymbol.HOLDALLCOMPLETE) == ISymbol.HOLDALLCOMPLETE) {
      result.append(S.HoldAllComplete);
    } else if ((attributes & ISymbol.HOLDCOMPLETE) == ISymbol.HOLDCOMPLETE) {
      result.append(S.HoldComplete);
    } else if ((attributes & ISymbol.HOLDALL) == ISymbol.HOLDALL) {
      result.append(S.HoldAll);
    } else {
      if ((attributes & ISymbol.HOLDFIRST) != ISymbol.NOATTRIBUTE) {
        result.append(S.HoldFirst);
      }
      if ((attributes & ISymbol.HOLDREST) != ISymbol.NOATTRIBUTE) {
        result.append(S.HoldRest);
      }
    }
    if ((attributes & ISymbol.LISTABLE) != ISymbol.NOATTRIBUTE) {
      result.append(S.Listable);
    }
    if ((attributes & ISymbol.NHOLDALL) == ISymbol.NHOLDALL) {
      result.append(S.NHoldAll);
    } else {
      if ((attributes & ISymbol.NHOLDFIRST) != ISymbol.NOATTRIBUTE) {
        result.append(S.NHoldFirst);
      }
      if ((attributes & ISymbol.NHOLDREST) != ISymbol.NOATTRIBUTE) {
        result.append(S.NHoldRest);
      }
    }
    if ((attributes & ISymbol.NONTHREADABLE) != ISymbol.NOATTRIBUTE) {
      result.append(S.NonThreadable);
    }
    if ((attributes & ISymbol.NUMERICFUNCTION) != ISymbol.NOATTRIBUTE) {
      result.append(S.NumericFunction);
    }
    if ((attributes & ISymbol.ONEIDENTITY) != ISymbol.NOATTRIBUTE) {
      result.append(S.OneIdentity);
    }
    if ((attributes & ISymbol.ORDERLESS) != ISymbol.NOATTRIBUTE) {
      result.append(S.Orderless);
    }
    if ((attributes & ISymbol.LOCKED) == ISymbol.LOCKED) {
      result.append(S.Locked);
      result.append(S.Protected);
    } else {
      if ((attributes & ISymbol.PROTECTED) != ISymbol.NOATTRIBUTE) {
        result.append(S.Protected);
      }
    }
    if ((attributes & ISymbol.READPROTECTED) != ISymbol.NOATTRIBUTE) {
      result.append(S.ReadProtected);
    }
    if ((attributes & ISymbol.SEQUENCEHOLD) == ISymbol.SEQUENCEHOLD
        && ((attributes & ISymbol.HOLDALLCOMPLETE) != ISymbol.HOLDALLCOMPLETE)) {
      result.append(S.SequenceHold);
    }
    result.sortInplace(Comparators.CANONICAL_COMPARATOR);
    return result;
  }
}
