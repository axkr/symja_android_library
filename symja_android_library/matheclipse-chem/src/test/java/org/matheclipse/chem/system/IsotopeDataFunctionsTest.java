package org.matheclipse.chem.system;

import org.junit.jupiter.api.Test;

/**
 * <code>IsotopeData</code>, the <code>Isotope</code> entity type, and the element properties that
 * are answered out of the same table.
 */
public class IsotopeDataFunctionsTest extends AbstractTestCase {

  @Test
  public void testIsotopeData() {
    check("IsotopeData(\"C\", \"MassNumber\")", //
        "12");
    check("IsotopeData(\"C\", \"AtomicMass\")", //
        "12.0");
    check("IsotopeData(\"H\", \"Abundance\")", //
        "99.9885");
    check("IsotopeData(\"Xx\")", //
        "Missing(NotAvailable)");
  }

  @Test
  public void testIsotopeAbundances() {
    // every naturally occurring isotope, as massNumber -> percent; the other properties speak for
    // the most abundant isotope alone and so cannot answer this
    check("IsotopeData(\"C\", \"Abundances\")", //
        "{12->98.93,13->1.07}");
    check("IsotopeData(\"W\", \"Abundances\")", //
        "{180->0.12,182->26.5,183->14.31,184->30.64,186->28.43}");
  }

  @Test
  public void testElementDataReachesTheIsotopeTable() {
    // ElementData lives in matheclipse-core, which has neither CDK nor an isotope table; these
    // properties are answered through IsotopeData, so they work exactly when this module is loaded
    check("ElementData(\"Carbon\", \"KnownIsotopes\")", //
        "{8,9,10,11,12,13,14,15,16,17,18,19,20,21,22}");
    check("ElementData(\"Carbon\", \"IsotopeAbundances\")", //
        "{12->98.93,13->1.07}");
    // the nucleons of the most abundant isotope that are not protons
    check("ElementData(\"Carbon\", \"NeutronCount\")", //
        "6");
    check("ElementData(\"Tungsten\", \"NeutronCount\")", //
        "110");
    // an element with no isotope data says so
    check("ElementData(\"Og\", \"NeutronCount\")", //
        "Missing(NotAvailable)");
  }

  /** A single nuclide is named by an entity, however it was asked for. */
  @Test
  public void testIsotopeEntities() {
    check("IsotopeData(\"Carbon12\")", //
        "Entity(Isotope,Carbon12)");
    check("IsotopeData(\"C12\")", //
        "Entity(Isotope,Carbon12)");
    check("IsotopeData({\"Carbon\", 12})", //
        "Entity(Isotope,Carbon12)");
    check("IsotopeData({6, 12})", //
        "Entity(Isotope,Carbon12)");
    // an element answers with its isotopes, lightest first
    check("Take(IsotopeData(6), 3)", //
        "{Entity(Isotope,Carbon8),Entity(Isotope,Carbon9),Entity(Isotope,Carbon10)}");
    check("IsotopeData(\"Hydrogen\")", //
        "{Entity(Isotope,Hydrogen1),Entity(Isotope,Hydrogen2),Entity(Isotope,Hydrogen3),"
            + "Entity(Isotope,Hydrogen4),Entity(Isotope,Hydrogen5),Entity(Isotope,Hydrogen6),"
            + "Entity(Isotope,Hydrogen7)}");
    check("Length(IsotopeData(6))", //
        "15");
    // the name Symja gives the element, not the one CDK gives it
    check("IsotopeData({13, 27})", //
        "Entity(Isotope,Aluminum27)");
  }

  /**
   * A nuclide that could exist but is not in the table is no answer at all, which is what lets
   * <code>EntityValue</code> tell an unknown thing from an unknown property.
   */
  @Test
  public void testUnknownIsotope() {
    check("IsotopeData(\"Carbon99\")", //
        "IsotopeData(Carbon99)");
    check("IsotopeData(\"Carbon99\", \"MassNumber\")", //
        "IsotopeData(Carbon99,MassNumber)");
    // a name that names no element at all is the missing data it has always been
    check("IsotopeData(\"Xx12\")", //
        "Missing(NotAvailable)");
  }

  @Test
  public void testIsotopeProperties() {
    check("IsotopeData(\"Properties\")", //
        "{EntityProperty(Isotope,AtomicMass),EntityProperty(Isotope,AtomicNumber),"
            + "EntityProperty(Isotope,BindingEnergy),EntityProperty(Isotope,IsotopeAbundance),"
            + "EntityProperty(Isotope,MassNumber),EntityProperty(Isotope,NeutronNumber),"
            + "EntityProperty(Isotope,StandardName)}");
    check("IsotopeData(Entity(\"Isotope\", \"Carbon12\"), \"AtomicMass\")", //
        "Quantity(12.0,\"Daltons\")");
    check("IsotopeData(Entity(\"Isotope\", \"Carbon12\"), \"AtomicNumber\")", //
        "6");
    check("IsotopeData(Entity(\"Isotope\", \"Carbon12\"), \"MassNumber\")", //
        "12");
    check("IsotopeData(Entity(\"Isotope\", \"Carbon12\"), \"NeutronNumber\")", //
        "6");
    check("IsotopeData(Entity(\"Isotope\", \"Carbon12\"), \"StandardName\")", //
        "Carbon12");
    // an entity's abundance is a fraction of one; the element form stays in the table's percent
    check("IsotopeData(Entity(\"Isotope\", \"Carbon12\"), \"IsotopeAbundance\")", //
        "0.9893");
    check("IsotopeData(Entity(\"Isotope\", \"Carbon14\"), \"IsotopeAbundance\")", //
        "Missing(NotAvailable)");
  }

  /**
   * BE = (Z m(H1) + N m(n) - M(A,Z)) c^2, which gives carbon-12 the 92.16 MeV of the textbooks.
   * The last figures are not pinned: they follow the table's masses rather than a tabulated energy.
   */
  @Test
  public void testBindingEnergy() {
    check("Round(QuantityMagnitude(IsotopeData(Entity(\"Isotope\", \"Carbon12\"),"
        + " \"BindingEnergy\")), 0.01)", //
        "92.16");
    check("QuantityUnit(IsotopeData(Entity(\"Isotope\", \"Carbon12\"), \"BindingEnergy\"))", //
        "Megaelectronvolts");
    // iron-56 is near the peak of the curve, at 8.79 MeV per nucleon
    check("Round(QuantityMagnitude(IsotopeData(Entity(\"Isotope\", \"Iron56\"),"
        + " \"BindingEnergy\"))/56, 0.01)", //
        "8.79");
  }

  @Test
  public void testAllIsotopes() {
    check("Length(IsotopeData())", //
        "3171");
    check("IsotopeData() === IsotopeData(All)", //
        "True");
    check("First(IsotopeData(All))", //
        "Entity(Isotope,Hydrogen1)");
    // loading the table is all "Preload" asks for
    check("IsotopeData(All, \"Preload\")", //
        "");
  }

  /** The type is registered, so the generic entity functions reach it. */
  @Test
  public void testIsotopeEntityLayer() {
    check("EntityValue(Entity(\"Isotope\", \"Carbon12\"), \"MassNumber\")", //
        "12");
    check("EntityValue({Entity(\"Isotope\", \"Carbon12\"), Entity(\"Isotope\", \"Carbon13\")},"
        + " \"NeutronNumber\")", //
        "{6,7}");
    check("EntityValue(Entity(\"Isotope\", \"Carbon12\"), {\"MassNumber\", \"AtomicNumber\"})", //
        "{12,6}");
    check("EntityValue(Entity(\"Isotope\", \"Carbon12\"), \"Nonsense\")", //
        "Missing(UnknownProperty,{Isotope,Nonsense})");
    check("EntityValue(Entity(\"Isotope\", \"Carbon99\"), \"MassNumber\")", //
        "Missing(UnknownEntity,{Isotope,Carbon99})");
    check("Length(EntityList(\"Isotope\"))", //
        "3171");
  }

  /**
   * The isotopes that occur in nature. That takes in the long lived primordial ones and leaves
   * technetium and polonium with none, which is how a chemistry reference describes them.
   */
  @Test
  public void testStableIsotopes() {
    check("ElementData(6, \"StableIsotopes\")", //
        "{Entity(Isotope,Carbon12),Entity(Isotope,Carbon13)}");
    check("ElementData(84, \"StableIsotopes\")", //
        "{}");
    check("ElementData(43, \"StableIsotopes\")", //
        "{}");
    // answered, but left out of the enumeration, as the reference implementation leaves it out
    check("MemberQ(ElementData(\"Properties\"), EntityProperty(\"Element\", \"StableIsotopes\"))", //
        "False");
  }
}
