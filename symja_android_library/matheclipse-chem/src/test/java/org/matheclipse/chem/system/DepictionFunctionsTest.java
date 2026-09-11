package org.matheclipse.chem.system;

import org.junit.jupiter.api.Test;

/**
 * Tier C and the 3D half of Tier D: depiction.
 *
 * <p>
 * These assert on the returned expression, never on a rendered image: the whole point of emitting
 * <code>Graphics</code> primitives is that the result is inspectable as data.
 */
public class DepictionFunctionsTest extends AbstractTestCase {

  @Test
  public void testAtomDiagramCoordinates() {
    // one {x, y} pair per heavy atom; hydrogens are excluded by default
    check("Length(AtomDiagramCoordinates(Molecule(\"CCO\")))", //
        "3");
    check("Length(AtomDiagramCoordinates(Molecule(\"c1ccccc1\")))", //
        "6");
    // IncludeHydrogens is not a display flag: it changes which atoms exist
    check("Length(AtomDiagramCoordinates(Molecule(\"CCO\"), IncludeHydrogens -> True))", //
        "9");
  }

  @Test
  public void testMoleculePlotIsAGraphicsObject() {
    check("Head(MoleculePlot(Molecule(\"CCO\")))", //
        "Graphics");
    check("Head(MoleculeDraw(Molecule(\"CCO\")))", //
        "Graphics");
  }

  @Test
  public void testMoleculePlotShape() {
    // the reference implementation's shape: an Annotation recording the structure, wrapping the
    // blocks the diagram is built from - the bonds, then the atoms
    check("Head(MoleculePlot(Molecule(\"CCO\"))[[1]])", //
        "Annotation");
    check("MoleculePlot(Molecule(\"CCO\"))[[1,2]]", //
        "SMILES->CCO");
    check("Length(MoleculePlot(Molecule(\"CCO\"))[[1,1]])", //
        "2");
    // the bond block opens with the stroke width every bond is drawn at
    check("MoleculePlot(Molecule(\"CCO\"))[[1,1,1,1]]", //
        "AbsoluteThickness(1.3)");
  }

  @Test
  public void testMoleculePlotHalvesBondsByAtom() {
    // each bond stops at its midpoint so the two halves can take the colour of the atom they grow
    // from, the flat counterpart of the half-cylinders of the 3D model
    check("Cases(MoleculePlot(Molecule(\"C=O\")), Style(s_, c_) :> c, Infinity)[[1]]", //
        "RGBColor(0.4,0.4,0.4)");
    check("Cases(MoleculePlot(Molecule(\"C=O\")), Style(s_, c_) :> c, Infinity)[[2]]", //
        "RGBColor(0.800498,0.201504,0.192061)");
  }

  @Test
  public void testMoleculePlotLeavesRoomForAtomLabels() {
    // the carbon of formaldehyde sits at x = -0.5 and its oxygen at x = 0.5, one bond length
    // apart. The bond stops short of the oxygen rather than running underneath the glyph.
    check("Cases(MoleculePlot(Molecule(\"C=O\"))[[1,1,2]], Text(t_, p_) :> p[[1]], Infinity)", //
        "{0.5}");
    check("Max(Cases(MoleculePlot(Molecule(\"C=O\"))[[1,1,1]], "
        + "{x_?NumberQ, y_?NumberQ} :> x, Infinity))", //
        "0.34");
  }

  @Test
  public void testMoleculePlotDoubleBondPlacement() {
    // an acyclic double bond straddles the bond axis, one line either side of it
    check("Union(Cases(MoleculePlot(Molecule(\"C=O\"))[[1,1,1]], "
        + "{x_?NumberQ, y_?NumberQ} :> y, Infinity))", //
        "{-0.06,0.06}");
    // the parallel lines of one multiple bond travel together in a single Line, so a Line of a
    // double bond nests one level deeper than a Line of a single bond. Benzene keeps its Kekule
    // structure: six of its twelve half-bonds belong to a double bond.
    check("Cases(MoleculePlot(Molecule(\"c1ccccc1\")), Line(p_) :> Length(Dimensions(p)), "
        + "Infinity)", //
        "{3,3,2,2,3,3,2,2,3,3,2,2}");
  }

  @Test
  public void testMoleculePlotShowsHeteroatomHydrogens() {
    // a structure diagram spells out the hydrogens on a heteroatom and leaves those on carbon
    // implicit, so ethanol shows the hydroxyl hydrogen and nothing else
    check("Cases(MoleculePlot(Molecule(\"CCO\")), Text(t_, p_) :> t, Infinity)", //
        "{O,H}");
    // IncludeHydrogens -> True asks for all of them
    check("Count(MoleculePlot(Molecule(\"CCO\"), IncludeHydrogens -> True), _Text, Infinity)", //
        "7");
  }

  @Test
  public void testMoleculePlotDrawsStereoBonds() {
    // the stereocentre of asparagine is drawn as a wedge rather than a plain line
    check("Count(MoleculePlot(Molecule(\"NC(=O)C[C@H](C(=O)O)N\")), _Polygon, Infinity)", //
        "1");
    check("Count(MoleculePlot(Molecule(\"CCO\")), _Polygon, Infinity)", //
        "0");
  }

  @Test
  public void testMoleculePlotAtomsCarryTheirIndex() {
    // every atom is hoverable and says which one it is; an unlabelled carbon needs an invisible
    // disc to have anything to hover over
    check("Count(MoleculePlot(Molecule(\"CCO\")), _Tooltip, Infinity)", //
        "4");
    check("MoleculePlot(Molecule(\"CCO\"))[[1,1,2,1,2]]", //
        "1");
  }

  @Test
  public void testMoleculePlotForwardsGraphicsOptions() {
    // PlotLabel and friends belong to the picture, so they reach the Graphics object
    check("MoleculePlot(Molecule(\"CCO\"), PlotLabel -> \"Ethanol\")[[2]]", //
        "PlotLabel->Ethanol");
    // ... while the options this module consumes itself describe the molecule, not the picture,
    // and are not passed on
    check("Length(MoleculePlot(Molecule(\"CCO\"), IncludeHydrogens -> True))", //
        "1");
    check("FreeQ(MoleculePlot3D(Molecule(\"CCO\"), PlotTheme -> \"SpaceFilling\"), PlotTheme)", //
        "True");
  }

  @Test
  public void testMoleculePlot3DIsAGraphics3DObject() {
    check("Head(MoleculePlot3D(Molecule(\"CCO\")))", //
        "Graphics3D");
  }

  @Test
  public void testMoleculePlot3DShape() {
    // the reference implementation's shape: a Specularity and an EdgeForm directive, then one
    // GraphicsComplex holding every coordinate once, with the primitives referring to them by
    // index - {spheres, bonds}
    check("MoleculePlot3D(Molecule(\"CCO\"))[[1,1]]", //
        "Specularity(GrayLevel(1),100)");
    check("MoleculePlot3D(Molecule(\"CCO\"))[[1,2]]", //
        "EdgeForm(None)");
    check("Head(MoleculePlot3D(Molecule(\"CCO\"))[[1,3]])", //
        "GraphicsComplex");
    check("Rest(MoleculePlot3D(Molecule(\"CCO\")))", //
        "Graphics3D(Boxed->False,Lighting->Neutral)");
    // one Sphere per element, listing the atoms it covers; radii and colours follow the reference
    check("MoleculePlot3D(Molecule(\"CCO\"), IncludeHydrogens -> False)[[1,3,2,1,1]]", //
        "{RGBColor(0.4,0.4,0.4),Sphere({1,2},0.34)}");
    check("MoleculePlot3D(Molecule(\"CCO\"), IncludeHydrogens -> False)[[1,3,2,1,2]]", //
        "{RGBColor(0.800498,0.201504,0.192061),Sphere({3},0.31)}");
    // both ends of the C-C bond are the same colour, so one undivided cylinder says it all
    check("MoleculePlot3D(Molecule(\"CCO\"), IncludeHydrogens -> False)[[1,3,2,2,1]]", //
        "{{RGBColor(0.4,0.4,0.4),Cylinder({1,2},0.15)}}");
    // the C-O bond is two half cylinders meeting at a midpoint, each coloured by its own atom
    check("MoleculePlot3D(Molecule(\"CCO\"), IncludeHydrogens -> False)[[1,3,2,2,2,1]]", //
        "{RGBColor(0.4,0.4,0.4),Cylinder({2,4},0.15)}");
    check("MoleculePlot3D(Molecule(\"CCO\"), IncludeHydrogens -> False)[[1,3,2,2,2,2]]", //
        "{RGBColor(0.800498,0.201504,0.192061),Cylinder({4,3},0.15)}");
  }

  @Test
  public void testMoleculePlot3DIncludeHydrogens() {
    // one Sphere group per element: C, H, O
    check("Length(MoleculePlot3D(Molecule(\"CCO\"))[[1,3,2,1]])", //
        "3");
    // without hydrogens the H group goes away
    check("Length(MoleculePlot3D(Molecule(\"CCO\"), IncludeHydrogens -> False)[[1,3,2,1]])", //
        "2");
  }

  @Test
  public void testMoleculePlot3DSpaceFilling() {
    // van der Waals spheres for C, O and H, and no bonds: at that size the spheres already touch
    check("Cases(MoleculePlot3D(Molecule(\"CCO\"), PlotTheme -> \"SpaceFilling\"), "
        + "Sphere(c_, r_) :> r, Infinity)", //
        "{1.7,1.2,1.52}");
    check("Count(MoleculePlot3D(Molecule(\"CCO\"), PlotTheme -> \"SpaceFilling\"), _Cylinder, "
        + "Infinity)", //
        "0");
    // the default ball-and-stick model keeps them
    check("Count(MoleculePlot3D(Molecule(\"CCO\")), _Cylinder, Infinity)", //
        "15");
  }

  @Test
  public void testMoleculePlot3DFallsBackToAFlatModel() {
    // CDK's 3D model builder throws for water. Rather than return the input unevaluated for
    // something that ordinary, the 2D layout is lifted into the z = 0 plane: wrong as chemistry,
    // right as a depiction of connectivity.
    check("Head(MoleculePlot3D(Molecule(\"O\")))", //
        "Graphics3D");
    // every coordinate in the pool has z = 0, which is what "flat" means here
    check("Union(MoleculePlot3D(Molecule(\"O\"))[[1,3,1,All,3]])", //
        "{0.0}");
    check("Length(MoleculePlot3D(Molecule(\"O\"))[[1,3,2,1]])", //
        "2");
  }

  @Test
  public void testMoleculePlot3DHighlightsAreLabelled() {
    // an association names each highlight, and the names become the legend of the graphics
    check("MoleculePlot3D(\"O=C(C1CCC1)S[C@@H]1CCC1(C)C\", "
        + "<|\"carbonyl\" -> Bond({\"C\",\"O\"},\"Double\"), "
        + "\"ring carbons\" -> Atom(\"C\", \"RingAtomQ\" -> True)|>)[[2,2]]", //
        "{carbonyl,ring carbons}");
    // ... paired with the colours those labels stand for, from the shared chart cycle
    check("MoleculePlot3D(\"CCO\", <|\"alcohol\" -> \"CO\"|>)[[2,1]]", //
        "{RGBColor(0.985248,0.676238,0.0398315)}");
    // highlighted parts are split into their own primitive groups so each keeps one colour
    check("Length(MoleculePlot3D(\"O=C(C1CCC1)S[C@@H]1CCC1(C)C\")[[1,3,2,1]])", //
        "3");
    check("Length(MoleculePlot3D(\"O=C(C1CCC1)S[C@@H]1CCC1(C)C\", "
        + "<|\"carbonyl\" -> Bond({\"C\",\"O\"},\"Double\"), "
        + "\"ring carbons\" -> Atom(\"C\", \"RingAtomQ\" -> True)|>)[[1,1,3,2,1]])", //
        "4");
  }

  @Test
  public void testMoleculePlotHighlightsRingAtoms() {
    // the two cyclobutane rings contribute eight ring carbons and nothing else. The highlights
    // are the background the structure is drawn over, so they are the diagram's first block.
    check("Count(MoleculePlot(\"O=C(C1CCC1)S[C@@H]1CCC1(C)C\", "
        + "<|\"ring carbons\" -> Atom(\"C\", \"RingAtomQ\" -> True)|>)[[1,1,1,1]], _Disk, "
        + "Infinity)", //
        "8");
    check("Count(MoleculePlot(\"O=C(C1CCC1)S[C@@H]1CCC1(C)C\")[[1,1,1]], _Disk, Infinity)", //
        "0");
  }

  @Test
  public void testHighlightsAcceptSmartsAndPlainLists() {
    // a SMARTS string is matched with the same machinery the substructure queries use
    check("MoleculePlot3D(\"CCO\", <|\"alcohol\" -> MoleculePattern(\"CO\")|>)[[2,2]]", //
        "{alcohol}");
    // an unnamed list still highlights, but earns no legend
    check("FreeQ(MoleculePlot3D(\"CCO\", {Atom(\"O\")}), PlotLegends)", //
        "True");
  }

  @Test
  public void testOptionsStillParseAfterHighlights() {
    // the highlight argument sits between the molecule and the options, so the options that
    // follow it must still be found
    check("Count(MoleculePlot3D(\"CCO\", <|\"alcohol\" -> \"CO\"|>, "
        + "PlotTheme -> \"SpaceFilling\"), _Cylinder, Infinity)", //
        "0");
    check("MoleculePlot3D(\"CCO\", <|\"alcohol\" -> \"CO\"|>, PlotLabel -> \"x\")[[1,2]]", //
        "PlotLabel->x");
  }
}
