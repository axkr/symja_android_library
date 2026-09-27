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
    // a name this table does not know is no answer at all, as in the reference implementation
    check("IsotopeData(\"Xx\")", //
        "IsotopeData(Xx)");
  }

  @Test
  public void testIsotopeAbundances() {
    // every naturally occurring isotope, as massNumber -> percent; the other properties speak for
    // the most abundant isotope alone and so cannot answer this
    check("IsotopeData(\"C\", \"Abundances\")", //
        "<|Entity(Isotope,Carbon12)->Quantity(98.93,\"Percents\"),Entity(Isotope,Carbon13)->Quantity(1.07,\"Percents\")|>");
  }

  @Test
  public void testElementDataReachesTheIsotopeTable() {
    // ElementData lives in matheclipse-core, which has neither CDK nor an isotope table; these
    // properties are answered through IsotopeData, so they work exactly when this module is loaded
    check("Take(ElementData(\"Carbon\", \"KnownIsotopes\"), 3)", //
        "{Entity(Isotope,Carbon8),Entity(Isotope,Carbon9),Entity(Isotope,Carbon10)}");
    check("ElementData(\"Carbon\", \"IsotopeAbundances\")", //
        "<|Entity(Isotope,Carbon12)->Quantity(98.93,\"Percents\"),Entity(Isotope,Carbon13)->Quantity(1.07,\"Percents\")|>");
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
    // an element and a mass number as a pair is not a specifier, which is the reference's rule
    check("IsotopeData({\"Carbon\", 12})", //
        "IsotopeData({Carbon,12})");
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
    check("IsotopeData(\"Aluminum27\")", //
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
    check("IsotopeData(\"Xx12\")", //
        "IsotopeData(Xx12)");
  }

  @Test
  public void testIsotopeProperties() {
    check("IsotopeData(\"Properties\")", //
        "{EntityProperty(Isotope,AtomicMass),EntityProperty(Isotope,AtomicNumber),"
            + "EntityProperty(Isotope,BindingEnergy),EntityProperty(Isotope,IsotopeAbundance),"
            + "EntityProperty(Isotope,MassNumber),EntityProperty(Isotope,NeutronNumber),"
            + "EntityProperty(Isotope,StandardName)}");
    check("IsotopeData(Entity(\"Isotope\", \"Carbon12\"), \"AtomicMass\")", //
        "Quantity(12.0,\"AtomicMassUnit\")");
    check("IsotopeData(Entity(\"Isotope\", \"Carbon12\"), \"AtomicNumber\")", //
        "6");
    check("IsotopeData(Entity(\"Isotope\", \"Carbon12\"), \"MassNumber\")", //
        "12");
    check("IsotopeData(Entity(\"Isotope\", \"Carbon12\"), \"NeutronNumber\")", //
        "6");
    check("IsotopeData(Entity(\"Isotope\", \"Carbon12\"), \"StandardName\")", //
        "Carbon12");
    // a percentage, and zero rather than missing for a nuclide that does not occur in nature
    check("IsotopeData(Entity(\"Isotope\", \"Carbon12\"), \"IsotopeAbundance\")", //
        "Quantity(98.93,\"Percents\")");
    check("IsotopeData(Entity(\"Isotope\", \"Carbon14\"), \"IsotopeAbundance\")", //
        "Quantity(0,\"Percents\")");
  }

  /**
   * BE = (Z m(H1) + N m(n) - M(A,Z)) c^2 / A - per nucleon, as the reference reports it, so
   * carbon-12 is 7.68 MeV rather than its 92.16 MeV total. Measured in Mathematica 2026-09-19:
   * 7.6801446 and 8.7903563.
   */
  @Test
  public void testBindingEnergy() {
    check("Round(QuantityMagnitude(IsotopeData(Entity(\"Isotope\", \"Carbon12\"),"
        + " \"BindingEnergy\")), 0.0001)", //
        "7.6801");
    check("QuantityUnit(IsotopeData(Entity(\"Isotope\", \"Carbon12\"), \"BindingEnergy\"))", //
        "Megaelectronvolts");
    // iron-56 is at the peak of the curve
    check("Round(QuantityMagnitude(IsotopeData(Entity(\"Isotope\", \"Iron56\"),"
        + " \"BindingEnergy\")), 0.001)", //
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
   * The stable isotopes, as the reference implementation lists them - measured in Mathematica on
   * 2026-09-19 for every element. Stable means stable, not "occurs in nature": uranium occurs in
   * nature and has none.
   */
  @Test
  public void testStableIsotopes() {
    check("ElementData(6, \"StableIsotopes\")", //
        "{Entity(Isotope,Carbon12),Entity(Isotope,Carbon13)}");
    // technetium, polonium and every uranium isotope are radioactive
    check("{ElementData(43, \"StableIsotopes\"), ElementData(84, \"StableIsotopes\"),"
        + " ElementData(92, \"StableIsotopes\")}", //
        "{{},{},{}}");
    // the reference's own choices, which are not all the textbook's: thorium-232 is in,
    // bismuth-209 is out, and so is samarium-147 while samarium-149 is in
    check("ElementData(90, \"StableIsotopes\")", //
        "{Entity(Isotope,Thorium232)}");
    check("ElementData(83, \"StableIsotopes\")", //
        "{}");
    check("IsotopeData(#, \"MassNumber\")& /@ ElementData(62, \"StableIsotopes\")", //
        "{144,149,150,152,154}");
    // in the order of the entity names, which puts ruthenium-100 ahead of ruthenium-96
    check("IsotopeData(#, \"MassNumber\")& /@ ElementData(44, \"StableIsotopes\")", //
        "{100,101,102,104,96,98,99}");
    check("Total(Table(Length(ElementData(z, \"StableIsotopes\")), {z, 118}))", //
        "257");
    // every one of them is an isotope this table can answer for, named as its element is
    check("AllTrue(Flatten(Table(ElementData(z, \"StableIsotopes\"), {z, 118})),"
        + " IntegerQ(IsotopeData(#, \"MassNumber\"))&)", //
        "True");
    check("ElementData(55, \"StableIsotopes\")", //
        "{Entity(Isotope,Cesium133)}");
    check("MemberQ(ElementData(\"Properties\"), EntityProperty(\"Element\", \"StableIsotopes\"))", //
        "True");
  }
}
