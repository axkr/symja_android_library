package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.BuiltInDummy;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Symbol;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * The attribute word has to survive serialization.
 * <p>
 * {@code Symbol} writes it with {@code writeInt}, but {@code BuiltInDummy} used
 * {@code stream.write(int)} / {@code stream.read()} - a single <b>byte</b> - so every attribute
 * from {@link Attribute#LISTABLE} (<code>0x200</code>) upwards was silently lost. Only
 * {@code OneIdentity}, {@code Constant}, {@code Orderless}, {@code Flat}, {@code HoldFirst} and
 * {@code HoldRest} fitted through.
 */
public class AttributeSerializationTest {

  /** Attributes whose bits sit above the byte that {@code BuiltInDummy} used to write. */
  private static final Attribute[] ABOVE_A_BYTE = {Attribute.LISTABLE, Attribute.NUMERICFUNCTION,
      Attribute.NHOLDFIRST, Attribute.NHOLDREST, Attribute.PROTECTED, Attribute.READPROTECTED,
      Attribute.SEQUENCEHOLD, Attribute.NONTHREADABLE};

  @BeforeEach
  public void setUp() throws Exception {
    F.await();
  }

  @Test
  public void testEveryAttributeSurvivesASymbolRoundTrip() {
    List<String> problems = new ArrayList<String>();
    for (Attribute attribute : Attribute.values()) {
      // Context.DUMMY is neither SYSTEM nor RUBI, so isLocked() is false
      Symbol symbol = new Symbol("attrSer", Context.DUMMY);
      symbol.setAttributes(attribute.mask());

      int roundTripped = ((ISymbol) roundTrip(symbol)).getAttributes();
      if (roundTripped != attribute.mask()) {
        problems.add(String.format("%s: expected 0x%08X but was 0x%08X", attribute,
            attribute.mask(), roundTripped));
      }
    }
    assertEquals(Collections.emptyList(), problems);
  }

  @Test
  public void testEveryAttributeSurvivesABuiltInDummyRoundTrip() {
    List<String> problems = new ArrayList<String>();
    for (Attribute attribute : Attribute.values()) {
      BuiltInDummy dummy = new BuiltInDummy("$attrSer");
      dummy.setAttributes(attribute.mask());

      int roundTripped = ((ISymbol) roundTrip(dummy)).getAttributes();
      if (roundTripped != attribute.mask()) {
        problems.add(String.format("%s: expected 0x%08X but was 0x%08X", attribute,
            attribute.mask(), roundTripped));
      }
    }
    assertEquals(Collections.emptyList(), problems,
        "BuiltInDummy truncates the attribute word to a single byte");
  }

  /** The whole word at once, which is what a real symbol carries. */
  @Test
  public void testACombinedAttributeWordSurvives() {
    int combined = ISymbol.NOATTRIBUTE;
    for (Attribute attribute : ABOVE_A_BYTE) {
      combined = attribute.setIn(combined);
    }

    Symbol symbol = new Symbol("attrSer", Context.DUMMY);
    symbol.setAttributes(combined);
    assertEquals(combined, ((ISymbol) roundTrip(symbol)).getAttributes(), "Symbol");

    BuiltInDummy dummy = new BuiltInDummy("$attrSer");
    dummy.setAttributes(combined);
    assertEquals(combined, ((ISymbol) roundTrip(dummy)).getAttributes(), "BuiltInDummy");
  }

  /** {@code writeRules}/{@code readRules} is a second, separate code path with the same bug. */
  @Test
  public void testTheRulesStreamKeepsTheAttributeWord() {
    int combined = Attribute.LISTABLE.setIn(Attribute.PROTECTED.setIn(ISymbol.NOATTRIBUTE));
    try {
      BuiltInDummy source = new BuiltInDummy("$attrSerRules");
      source.setAttributes(combined);

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      source.writeRules(oos);
      oos.close();

      BuiltInDummy target = new BuiltInDummy("$attrSerRules");
      ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
      target.readRules(ois);
      ois.close();

      assertEquals(combined, target.getAttributes());
    } catch (ClassNotFoundException | IOException e) {
      throw new AssertionError("rules stream round trip failed: " + e, e);
    }
  }

  private static Object roundTrip(Object original) {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(original);
      oos.close();

      ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
      Object copy = ois.readObject();
      ois.close();
      return copy;
    } catch (ClassNotFoundException | IOException e) {
      throw new AssertionError("serialization of " + original.getClass().getSimpleName()
          + " failed: " + e, e);
    }
  }
}
