package org.matheclipse.chem.builtin;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import org.matheclipse.chem.convert.ChemColors;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.GeometryUtil;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.modeling.builder3d.ModelBuilder3D;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * Tier C and the 3D half of Tier D: molecule depiction.
 *
 * <p>
 * Nothing here renders. CDK computes coordinates - <code>cdk-sdg</code> in 2D,
 * <code>cdk-builder3d</code> in 3D - and these functions emit ordinary Symja <code>Graphics</code>
 * / <code>Graphics3D</code> primitives, so the existing SVG and WebGL renderers, <code>Show</code>,
 * <code>Manipulate</code> and <code>Export</code> all apply unchanged. That is also why
 * <code>cdk-depict</code> is not a dependency and why no class in this module touches
 * <code>java.awt</code>.
 */
public class DepictionFunctions {

  /**
   * Fraction of a bond's length left blank at an end that carries an element label, so the line
   * stops short of the glyph instead of running underneath it.
   */
  private static final double LABEL_GAP_FRACTION = 0.16;

  /** Perpendicular distance between the parallel lines of a double or triple bond. */
  private static final double BOND_LINE_SPACING = 0.12;

  /**
   * Fraction pulled back from each end of the offset line of a ring-style double bond, which is
   * what makes the shorter inner line of an aromatic ring read as inner.
   */
  private static final double INNER_LINE_INSET = 0.15;

  /** Radius of the cylinders standing in for single bonds in a ball-and-stick model. */
  private static final double STICK_RADIUS = 0.15;

  /** The thinner cylinders a double or triple bond is drawn with. */
  private static final double MULTIPLE_STICK_RADIUS = 0.08;

  /** How far each line of a multiple bond sits from the bond axis, in angstrom. */
  private static final double MULTIPLE_BOND_OFFSET = 0.14;

  /** Radius of the shape a highlight paints behind an atom or along a bond. */
  private static final double HIGHLIGHT_RADIUS = 0.2269145;

  /** Radius of the invisible disc that gives an unlabelled atom something to be hovered over. */
  private static final double ATOM_HIT_RADIUS = 0.2;

  /** Stroke width of a bond, in printer's points. */
  private static final double BOND_THICKNESS = 1.3;

  /** Half the width of the broad end of a stereo wedge. */
  private static final double WEDGE_HALF_WIDTH = 0.15;

  /** Strokes in a hashed wedge. */
  private static final int WEDGE_HASH_COUNT = 6;

  private static final IAST WHITE = F.unaryAST1(S.GrayLevel, F.C1);

  private static final IAST WEDGE_COLOR = F.unaryAST1(S.GrayLevel, F.C0);

  /**
   * The highlight the reference implementation gives a model. Symja's WebGL renderer honours both
   * this and the <code>EdgeForm(None)</code> beside it.
   */
  private static final IAST SPECULARITY =
      F.binaryAST2(S.Specularity, F.unaryAST1(S.GrayLevel, F.C1), F.ZZ(100));

  private static class Initializer {

    private static void init() {
      S.AtomDiagramCoordinates.setEvaluator(new AtomDiagramCoordinates());
      S.MoleculePlot.setEvaluator(new MoleculePlot());
      S.MoleculeDraw.setEvaluator(new MoleculeDraw());
      S.MoleculePlot3D.setEvaluator(new MoleculePlot3D());
    }
  }

  /**
   * <code>AtomDiagramCoordinates(mol)</code> — the 2D layout coordinates, one <code>{x, y}</code>
   * per atom.
   *
   * <p>
   * Exposing the coordinates separately keeps layout bugs separable from rendering bugs: this is
   * assertable without comparing images.
   */
  private static class AtomDiagramCoordinates extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IAtomContainer molecule = prepared(ast, engine, includeHydrogens(ast, engine, false));
      if (molecule == null) {
        return F.NIL;
      }
      IASTAppendable result = F.ListAlloc(molecule.getAtomCount());
      for (IAtom atom : molecule.atoms()) {
        Point2d point = atom.getPoint2d();
        if (point == null) {
          return F.NIL;
        }
        result.append(F.List(F.num(point.x), F.num(point.y)));
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, F.List(F.Rule(S.IncludeHydrogens, S.False)));
    }
  }

  /** <code>MoleculePlot(mol)</code> — a 2D structure diagram as a <code>Graphics</code> object. */
  private static class MoleculePlot extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return plot2D(ast, engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, plotOptions());
    }
  }

  private static class MoleculeDraw extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return plot2D(ast, engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, plotOptions());
    }
  }

  /**
   * <code>MoleculePlot3D(mol)</code> — a ball-and-stick model as a <code>Graphics3D</code> object,
   * or a space-filling one under <code>PlotTheme -&gt; "SpaceFilling"</code>.
   */
  private static class MoleculePlot3D extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IAtomContainer molecule = MoleculeFunctions.molecule(ast.arg1());
      if (molecule == null) {
        return F.NIL;
      }
      // a ball-and-stick model conventionally shows hydrogens, but the option decides
      molecule = includeHydrogens(ast, engine, true) //
          ? MoleculeFunctions.withExplicitHydrogens(molecule)
          : withoutExplicitHydrogens(molecule);

      IAtomContainer positioned = layout3D(molecule);
      if (positioned == null) {
        positioned = flattened(layout2D(molecule));
      }
      if (positioned == null) {
        return Errors.printMessage(ast.topHead(), "chem3d", F.List(ast.arg1()), engine);
      }
      MoleculeHighlights highlights = highlights(ast);
      MoleculeHighlights.Match match =
          highlights == null ? null : highlights.match(positioned, ast, engine);
      IASTAppendable graphics = F.Graphics3D( //
          primitives3D(positioned, spaceFilling(ast, engine), highlights, match));
      appendUserOptions(graphics, ast);
      appendLegend(graphics, ast, highlights);
      appendModelOptions(graphics);
      return graphics;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, F.List( //
          F.Rule(S.IncludeHydrogens, S.True), //
          F.Rule(S.PlotTheme, F.stringx("BallAndStick"))));
    }
  }

  // ---------------------------------------------------------------- 2D

  private static IExpr plot2D(IAST ast, EvalEngine engine) {
    IAtomContainer molecule = diagram(ast, engine);
    if (molecule == null) {
      return F.NIL;
    }
    MoleculeHighlights highlights = highlights(ast);
    MoleculeHighlights.Match match =
        highlights == null ? null : highlights.match(molecule, ast, engine);

    IASTAppendable blocks = F.ListAlloc(3);
    if (match != null) {
      // the highlights are the background the structure is drawn over
      blocks.append(highlightBlock2D(molecule, highlights, match));
    }
    blocks.append(bondBlock2D(molecule, match));
    blocks.append(atomBlock2D(molecule, match));

    IExpr body = blocks;
    // the SMILES describes the molecule the caller named, not the copy the diagram drew: the
    // hydrogens added for the picture are a depiction choice and have no business in it
    IAtomContainer asGiven = MoleculeFunctions.molecule(ast.arg1());
    String smiles = asGiven == null ? null : MoleculeFunctions.depictionSmiles(asGiven);
    if (smiles != null) {
      // the structure the picture came from, carried along with it
      body = F.binaryAST2(S.Annotation, blocks, F.Rule(F.stringx("SMILES"), F.stringx(smiles)));
    }
    IASTAppendable graphics = F.Graphics(body);
    appendUserOptions(graphics, ast);
    appendLegend(graphics, ast, highlights);
    return graphics;
  }

  /**
   * The highlighted parts, as the filled shapes the rest of the diagram is drawn on top of: a disc
   * for an atom, and for a bond the stadium that a disc swept along it traces.
   */
  private static IAST highlightBlock2D(IAtomContainer molecule, MoleculeHighlights highlights,
      MoleculeHighlights.Match match) {
    Map<Integer, IASTAppendable> bySlot = new TreeMap<Integer, IASTAppendable>();
    for (int i = 0; i < molecule.getAtomCount(); i++) {
      int slot = match.ofAtom(i);
      Point2d point = molecule.getAtom(i).getPoint2d();
      if (slot < 0 || point == null) {
        continue;
      }
      shapesOf(bySlot, slot).append(F.binaryAST2(S.Disk, //
          F.List(F.num(point.x), F.num(point.y)), F.num(HIGHLIGHT_RADIUS)));
    }
    for (int i = 0; i < molecule.getBondCount(); i++) {
      int slot = match.ofBond(i);
      IBond bond = molecule.getBond(i);
      Point2d from = bond.getBegin().getPoint2d();
      Point2d to = bond.getEnd().getPoint2d();
      if (slot < 0 || from == null || to == null) {
        continue;
      }
      appendStadium(shapesOf(bySlot, slot), from, to);
    }

    IASTAppendable result = F.ListAlloc(bySlot.size());
    for (Map.Entry<Integer, IASTAppendable> entry : bySlot.entrySet()) {
      result.append(
          F.binaryAST2(S.Style, entry.getValue(), highlights.color(entry.getKey().intValue())));
    }
    return result;
  }

  private static IASTAppendable shapesOf(Map<Integer, IASTAppendable> bySlot, int slot) {
    Integer key = Integer.valueOf(slot);
    IASTAppendable shapes = bySlot.get(key);
    if (shapes == null) {
      shapes = F.ListAlloc(4);
      bySlot.put(key, shapes);
    }
    return shapes;
  }

  /** A disc at each end and the rectangle between them - the shape a highlighted bond wears. */
  private static void appendStadium(IASTAppendable shapes, Point2d from, Point2d to) {
    shapes.append(
        F.binaryAST2(S.Disk, F.List(F.num(from.x), F.num(from.y)), F.num(HIGHLIGHT_RADIUS)));
    shapes.append(F.binaryAST2(S.Disk, F.List(F.num(to.x), F.num(to.y)), F.num(HIGHLIGHT_RADIUS)));
    double dx = to.x - from.x;
    double dy = to.y - from.y;
    double length = Math.sqrt(dx * dx + dy * dy);
    if (length == 0.0) {
      return;
    }
    double nx = -dy / length * HIGHLIGHT_RADIUS;
    double ny = dx / length * HIGHLIGHT_RADIUS;
    shapes.append(F.unaryAST1(S.Polygon, F.List( //
        F.List(F.num(from.x + nx), F.num(from.y + ny)), //
        F.List(F.num(to.x + nx), F.num(to.y + ny)), //
        F.List(F.num(to.x - nx), F.num(to.y - ny)), //
        F.List(F.num(from.x - nx), F.num(from.y - ny)))));
  }

  /**
   * The bonds, as lines that stop at the midpoint so each half can take the colour of the atom it
   * grows from - the 2D counterpart of the half-cylinders of the model - grouped one
   * <code>Style</code> per colour. A bond lying on a highlight is drawn white instead, so the
   * structure stays legible against the colour behind it.
   */
  private static IAST bondBlock2D(IAtomContainer molecule, MoleculeHighlights.Match match) {
    Map<String, IASTAppendable> byColor = new LinkedHashMap<String, IASTAppendable>();
    Map<String, IAST> colors = new LinkedHashMap<String, IAST>();

    for (int i = 0; i < molecule.getBondCount(); i++) {
      IBond bond = molecule.getBond(i);
      Point2d from = bond.getBegin().getPoint2d();
      Point2d to = bond.getEnd().getPoint2d();
      if (from == null || to == null) {
        continue;
      }
      boolean highlighted = match != null && match.ofBond(i) >= 0;
      IAST beginColor =
          highlighted ? WHITE : ChemColors.diagramColorOf(bond.getBegin().getSymbol());
      IAST endColor = highlighted ? WHITE //
          : ChemColors.diagramColorOf(bond.getEnd().getSymbol());

      double startGap = isLabelled(bond.getBegin()) ? LABEL_GAP_FRACTION : 0.0;
      double endGap = isLabelled(bond.getEnd()) ? LABEL_GAP_FRACTION : 0.0;

      if (isWedge(bond)) {
        // a stereo bond is one solid or hashed shape rather than two coloured halves
        appendWedge(group(byColor, colors, WEDGE_COLOR), from, to, startGap, endGap,
            bond.getStereo());
        continue;
      }
      appendHalfBonds2D(byColor, colors, molecule, bond, from, to, startGap, endGap, beginColor,
          endColor);
    }

    IASTAppendable styles = F.ListAlloc(byColor.size());
    for (Map.Entry<String, IASTAppendable> entry : byColor.entrySet()) {
      styles.append(F.binaryAST2(S.Style, entry.getValue(), colors.get(entry.getKey())));
    }
    return F.List(F.unaryAST1(S.AbsoluteThickness, F.num(BOND_THICKNESS)), styles);
  }

  /**
   * The two halves of one bond. Every parallel line of a multiple bond goes into a single
   * <code>Line</code> per half, which is how the reference implementation writes them.
   */
  private static void appendHalfBonds2D(Map<String, IASTAppendable> byColor,
      Map<String, IAST> colors, IAtomContainer molecule, IBond bond, Point2d from, Point2d to,
      double startGap, double endGap, IAST beginColor, IAST endColor) {
    double dx = to.x - from.x;
    double dy = to.y - from.y;
    double length = Math.sqrt(dx * dx + dy * dy);
    if (length == 0.0) {
      return;
    }
    double nx = -dy / length;
    double ny = dx / length;

    int lines = 1;
    if (bond.getOrder() == IBond.Order.DOUBLE) {
      lines = 2;
    } else if (bond.getOrder() == IBond.Order.TRIPLE) {
      lines = 3;
    }
    double[] offsets = new double[lines];
    double[] insets = new double[lines];
    if (lines == 2) {
      int side = crowdedSide(molecule, bond, from, nx, ny);
      if (side != 0) {
        offsets[0] = 0.0;
        offsets[1] = side * BOND_LINE_SPACING;
        insets[1] = INNER_LINE_INSET;
      } else {
        offsets[0] = -BOND_LINE_SPACING / 2.0;
        offsets[1] = BOND_LINE_SPACING / 2.0;
      }
    } else {
      for (int i = 0; i < lines; i++) {
        offsets[i] = (i - (lines - 1) / 2.0) * BOND_LINE_SPACING;
      }
    }

    IASTAppendable beginSegments = F.ListAlloc(lines);
    IASTAppendable endSegments = F.ListAlloc(lines);
    for (int i = 0; i < lines; i++) {
      double tStart = startGap + insets[i];
      double tEnd = 1.0 - endGap - insets[i];
      if (tEnd <= tStart) {
        tStart = 0.0;
        tEnd = 1.0;
      }
      double tMid = (tStart + tEnd) / 2.0;
      beginSegments.append(segment(from, dx, dy, nx, ny, tStart, tMid, offsets[i]));
      endSegments.append(segment(from, dx, dy, nx, ny, tMid, tEnd, offsets[i]));
    }
    group(byColor, colors, beginColor).append(F.unaryAST1(S.Line, unwrapSingle(beginSegments)));
    group(byColor, colors, endColor).append(F.unaryAST1(S.Line, unwrapSingle(endSegments)));
  }

  /**
   * A single line is written <code>Line({p, q})</code> and a multiple bond
   * <code>Line({{p, q}, {r, s}})</code>, so a lone segment loses its wrapper.
   */
  private static IExpr unwrapSingle(IAST segments) {
    return segments.argSize() == 1 ? segments.arg1() : segments;
  }

  private static IAST segment(Point2d from, double dx, double dy, double nx, double ny,
      double tStart, double tEnd, double offset) {
    return F.List( //
        F.List(F.num(from.x + dx * tStart + nx * offset), //
            F.num(from.y + dy * tStart + ny * offset)), //
        F.List(F.num(from.x + dx * tEnd + nx * offset), //
            F.num(from.y + dy * tEnd + ny * offset)));
  }

  private static IASTAppendable group(Map<String, IASTAppendable> byColor, Map<String, IAST> colors,
      IAST color) {
    String key = color.toString();
    IASTAppendable primitives = byColor.get(key);
    if (primitives == null) {
      primitives = F.ListAlloc(8);
      byColor.put(key, primitives);
      colors.put(key, color);
    }
    return primitives;
  }

  /** Whether a bond carries the stereochemistry a wedge depicts. */
  private static boolean isWedge(IBond bond) {
    IBond.Stereo stereo = bond.getStereo();
    return stereo == IBond.Stereo.UP || stereo == IBond.Stereo.DOWN
        || stereo == IBond.Stereo.UP_INVERTED || stereo == IBond.Stereo.DOWN_INVERTED;
  }

  /**
   * A stereo bond: a solid triangle widening away from the stereocentre for a bond coming up out of
   * the page, and a ladder of widening strokes for one going down behind it.
   */
  private static void appendWedge(IASTAppendable primitives, Point2d from, Point2d to,
      double startGap, double endGap, IBond.Stereo stereo) {
    boolean inverted = stereo == IBond.Stereo.UP_INVERTED || stereo == IBond.Stereo.DOWN_INVERTED;
    Point2d tip = inverted ? to : from;
    Point2d base = inverted ? from : to;
    double tipGap = inverted ? endGap : startGap;
    double baseGap = inverted ? startGap : endGap;

    double dx = base.x - tip.x;
    double dy = base.y - tip.y;
    double length = Math.sqrt(dx * dx + dy * dy);
    if (length == 0.0) {
      return;
    }
    double nx = -dy / length;
    double ny = dx / length;
    double tStart = tipGap;
    double tEnd = 1.0 - baseGap;
    if (tEnd <= tStart) {
      tStart = 0.0;
      tEnd = 1.0;
    }
    boolean solid = stereo == IBond.Stereo.UP || stereo == IBond.Stereo.UP_INVERTED;
    if (solid) {
      primitives.append(F.unaryAST1(S.Polygon, F.List( //
          F.List(F.num(tip.x + dx * tStart), F.num(tip.y + dy * tStart)), //
          F.List(F.num(tip.x + dx * tEnd + nx * WEDGE_HALF_WIDTH),
              F.num(tip.y + dy * tEnd + ny * WEDGE_HALF_WIDTH)), //
          F.List(F.num(tip.x + dx * tEnd - nx * WEDGE_HALF_WIDTH),
              F.num(tip.y + dy * tEnd - ny * WEDGE_HALF_WIDTH)))));
      return;
    }
    for (int i = 1; i <= WEDGE_HASH_COUNT; i++) {
      double t = tStart + (tEnd - tStart) * i / (double) WEDGE_HASH_COUNT;
      double half = WEDGE_HALF_WIDTH * i / (double) WEDGE_HASH_COUNT;
      primitives.append(F.unaryAST1(S.Line, F.List( //
          F.List(F.num(tip.x + dx * t + nx * half), F.num(tip.y + dy * t + ny * half)), //
          F.List(F.num(tip.x + dx * t - nx * half), F.num(tip.y + dy * t - ny * half)))));
    }
  }

  /**
   * The atoms: a label for everything but carbon, and for the rest an invisible disc that gives the
   * atom something to be hovered over. Both carry the atom's index, so a reader can ask the picture
   * which atom they are looking at.
   */
  private static IAST atomBlock2D(IAtomContainer molecule, MoleculeHighlights.Match match) {
    IASTAppendable result = F.ListAlloc(molecule.getAtomCount());
    for (int i = 0; i < molecule.getAtomCount(); i++) {
      IAtom atom = molecule.getAtom(i);
      Point2d point = atom.getPoint2d();
      if (point == null) {
        continue;
      }
      IAST position = F.List(F.num(point.x), F.num(point.y));
      IExpr shape;
      if (isLabelled(atom)) {
        boolean highlighted = match != null && match.ofAtom(i) >= 0;
        IAST color = highlighted ? WHITE : ChemColors.diagramColorOf(atom.getSymbol());
        shape = F.List(color, F.binaryAST2(S.Text, F.stringx(atom.getSymbol()), position));
      } else {
        shape = F.List(F.unaryAST1(S.Opacity, F.C0), F.unaryAST1(S.EdgeForm, S.None),
            F.binaryAST2(S.Disk, position, F.num(ATOM_HIT_RADIUS)));
      }
      result.append(F.binaryAST2(S.Tooltip, shape, F.ZZ(i + 1)));
    }
    return result;
  }

  /**
   * Which side of a bond carries more of its neighbouring atoms: <code>+1</code>, <code>-1</code>,
   * or <code>0</code> when the two sides balance.
   */
  private static int crowdedSide(IAtomContainer molecule, IBond bond, Point2d from, double nx,
      double ny) {
    double sum = neighbourSide(molecule, bond, bond.getBegin(), from, nx, ny)
        + neighbourSide(molecule, bond, bond.getEnd(), from, nx, ny);
    if (sum > 1.0e-6) {
      return 1;
    }
    return sum < -1.0e-6 ? -1 : 0;
  }

  /** The signed perpendicular distances of an atom's other neighbours from the bond axis. */
  private static double neighbourSide(IAtomContainer molecule, IBond bond, IAtom atom, Point2d from,
      double nx, double ny) {
    double sum = 0.0;
    for (IAtom neighbour : molecule.getConnectedAtomsList(atom)) {
      if (neighbour == bond.getBegin() || neighbour == bond.getEnd()) {
        continue;
      }
      Point2d point = neighbour.getPoint2d();
      if (point == null) {
        continue;
      }
      sum += (point.x - from.x) * nx + (point.y - from.y) * ny;
    }
    return sum;
  }

  /** Structure diagrams label every element but carbon. */
  private static boolean isLabelled(IAtom atom) {
    return !"C".equals(atom.getSymbol());
  }

  // ---------------------------------------------------------------- 3D

  /**
   * Atoms grouped by element into one <code>Sphere</code> primitive each, then bonds grouped into
   * one <code>Cylinder</code> chain.
   *
   * <p>
   * Both CDK-side heads accept a list: <code>Sphere({{x,y,z},...}, r)</code> draws every centre and
   * <code>Cylinder({p1,p2,p3,p4,...}, r)</code> consumes the points pairwise. So a 200-atom
   * fragment becomes about a dozen primitives rather than 400.
   *
   * <p>
   * A space-filling model is the same spheres at van der Waals radius with the bonds left out - at
   * that size the spheres already touch, which is the point of the representation.
   */
  private static IAST primitives3D(IAtomContainer molecule, boolean spaceFilling,
      MoleculeHighlights highlights, MoleculeHighlights.Match match) {
    // The vertex pool of the GraphicsComplex. Atoms occupy 1..atomCount, so an atom's index is its
    // position in the container plus one; the points bonds need are appended after them.
    IASTAppendable points = F.ListAlloc(molecule.getAtomCount() * 3);
    for (IAtom atom : molecule.atoms()) {
      Point3d point = atom.getPoint3d();
      points.append(point == null //
          ? F.List(F.C0, F.C0, F.C0)
          : F.List(F.num(point.x), F.num(point.y), F.num(point.z)));
    }

    IAST spheres = spheres3D(molecule, spaceFilling, highlights, match);
    if (spaceFilling) {
      // at van der Waals size the spheres already touch, so there are no bonds to draw
      return F.List(SPECULARITY, F.unaryAST1(S.EdgeForm, S.None), //
          F.binaryAST2(S.GraphicsComplex, points, F.unaryAST1(S.List, spheres)));
    }
    IAST bonds = bonds3D(molecule, points, highlights, match);
    return F.List(SPECULARITY, F.unaryAST1(S.EdgeForm, S.None), //
        F.binaryAST2(S.GraphicsComplex, points, F.List(spheres, bonds)));
  }

  /**
   * One <code>Sphere</code> per element, listing the indices of the atoms it covers - split further
   * by highlight, so every group keeps a single colour.
   */
  private static IAST spheres3D(IAtomContainer molecule, boolean spaceFilling,
      MoleculeHighlights highlights, MoleculeHighlights.Match match) {
    // sorted by element, then by highlight, so the groups come out in a stable readable order
    Map<String, List<Integer>> groups = new TreeMap<String, List<Integer>>();
    for (int i = 0; i < molecule.getAtomCount(); i++) {
      int highlight = match == null ? -1 : match.ofAtom(i);
      String key = molecule.getAtom(i).getSymbol() + "|" + highlight;
      List<Integer> indices = groups.get(key);
      if (indices == null) {
        indices = new ArrayList<Integer>();
        groups.put(key, indices);
      }
      indices.add(Integer.valueOf(i));
    }

    IASTAppendable result = F.ListAlloc(groups.size());
    for (Map.Entry<String, List<Integer>> entry : groups.entrySet()) {
      List<Integer> indices = entry.getValue();
      String element = molecule.getAtom(indices.get(0).intValue()).getSymbol();
      int highlight = match == null ? -1 : match.ofAtom(indices.get(0).intValue());

      IASTAppendable vertices = F.ListAlloc(indices.size());
      for (Integer index : indices) {
        vertices.append(F.ZZ(index.intValue() + 1));
      }
      double radius = spaceFilling //
          ? ChemColors.vanDerWaalsRadius(element)
          : ChemColors.ballRadius(element);
      IAST color = highlight < 0 ? ChemColors.ballColorOf(element) : highlights.color(highlight);
      result.append(F.List(color, F.binaryAST2(S.Sphere, vertices, F.num(radius))));
    }
    return result;
  }

  /**
   * Bonds as pairs of half-cylinders meeting at the bond's midpoint, each half taking the colour of
   * the atom it grows from. That is what makes a ball-and-stick model readable: the stick tells you
   * what it connects.
   *
   * <p>
   * A double or triple bond becomes two or three thinner parallel pairs, offset from the bond axis.
   */
  private static IAST bonds3D(IAtomContainer molecule, IASTAppendable points,
      MoleculeHighlights highlights, MoleculeHighlights.Match match) {
    IASTAppendable result = F.ListAlloc(molecule.getBondCount());
    for (int i = 0; i < molecule.getBondCount(); i++) {
      IBond bond = molecule.getBond(i);
      Point3d from = bond.getBegin().getPoint3d();
      Point3d to = bond.getEnd().getPoint3d();
      if (from == null || to == null) {
        continue;
      }
      int beginIndex = molecule.indexOf(bond.getBegin());
      int endIndex = molecule.indexOf(bond.getEnd());
      if (beginIndex < 0 || endIndex < 0) {
        continue;
      }
      int highlight = match == null ? -1 : match.ofBond(i);
      IAST beginColor = highlight >= 0 ? highlights.color(highlight)
          : ChemColors.ballColorOf(bond.getBegin().getSymbol());
      IAST endColor = highlight >= 0 ? highlights.color(highlight)
          : ChemColors.ballColorOf(bond.getEnd().getSymbol());

      int lines = 1;
      if (bond.getOrder() == IBond.Order.DOUBLE) {
        lines = 2;
      } else if (bond.getOrder() == IBond.Order.TRIPLE) {
        lines = 3;
      }
      IASTAppendable halves = F.ListAlloc(lines * 2);
      if (lines == 1) {
        appendStick(halves, points, beginIndex + 1, endIndex + 1, from, to, beginColor, endColor,
            STICK_RADIUS);
      } else {
        double[] normal = perpendicular(molecule, bond, from, to);
        for (int line = 0; line < lines; line++) {
          double offset = (line - (lines - 1) / 2.0) * MULTIPLE_BOND_OFFSET * 2.0;
          Point3d start = shifted(from, normal, offset);
          Point3d end = shifted(to, normal, offset);
          int startIndex = appendPoint(points, start);
          int endPointIndex = appendPoint(points, end);
          appendStick(halves, points, startIndex, endPointIndex, start, end, beginColor, endColor,
              MULTIPLE_STICK_RADIUS);
        }
      }
      result.append(halves);
    }
    return result;
  }

  /**
   * One stick of a bond: two half-cylinders meeting at the midpoint, each coloured by the atom it
   * grows from - or a single undivided cylinder when both ends would be the same colour, which
   * spares the vertex pool a midpoint nothing would distinguish.
   */
  private static void appendStick(IASTAppendable halves, IASTAppendable points, int startIndex,
      int endIndex, Point3d from, Point3d to, IAST beginColor, IAST endColor, double radius) {
    if (beginColor.equals(endColor)) {
      halves.append(F.List(beginColor, //
          F.binaryAST2(S.Cylinder, F.List(F.ZZ(startIndex), F.ZZ(endIndex)), F.num(radius))));
      return;
    }
    int midpoint = appendPoint(points, midpoint(from, to));
    halves.append(F.List(beginColor, //
        F.binaryAST2(S.Cylinder, F.List(F.ZZ(startIndex), F.ZZ(midpoint)), F.num(radius))));
    halves.append(F.List(endColor, //
        F.binaryAST2(S.Cylinder, F.List(F.ZZ(midpoint), F.ZZ(endIndex)), F.num(radius))));
  }

  /** Appends a point to the vertex pool and returns its 1-based index. */
  private static int appendPoint(IASTAppendable points, Point3d point) {
    points.append(F.List(F.num(point.x), F.num(point.y), F.num(point.z)));
    return points.argSize();
  }

  private static Point3d midpoint(Point3d from, Point3d to) {
    return new Point3d((from.x + to.x) / 2.0, (from.y + to.y) / 2.0, (from.z + to.z) / 2.0);
  }

  private static Point3d shifted(Point3d point, double[] normal, double offset) {
    return new Point3d(point.x + normal[0] * offset, point.y + normal[1] * offset,
        point.z + normal[2] * offset);
  }

  /**
   * A unit vector perpendicular to a bond, used to offset the lines of a multiple bond.
   *
   * <p>
   * Taken from a neighbouring atom where there is one, so the lines of a double bond lie in the
   * plane of the group they belong to rather than at some arbitrary angle to it.
   */
  private static double[] perpendicular(IAtomContainer molecule, IBond bond, Point3d from,
      Point3d to) {
    double[] axis = normalized(to.x - from.x, to.y - from.y, to.z - from.z);
    for (IAtom neighbour : molecule.getConnectedAtomsList(bond.getBegin())) {
      if (neighbour == bond.getEnd() || neighbour.getPoint3d() == null) {
        continue;
      }
      Point3d point = neighbour.getPoint3d();
      double[] candidate = rejection(point.x - from.x, point.y - from.y, point.z - from.z, axis);
      if (candidate != null) {
        return candidate;
      }
    }
    // an isolated bond: any perpendicular will do, so take the least aligned coordinate axis
    double[] fallback = Math.abs(axis[0]) < 0.9 //
        ? rejection(1.0, 0.0, 0.0, axis)
        : rejection(0.0, 1.0, 0.0, axis);
    return fallback == null ? new double[] {0.0, 0.0, 1.0} : fallback;
  }

  /** The component of a vector perpendicular to {@code axis}, normalized, or {@code null}. */
  private static double[] rejection(double x, double y, double z, double[] axis) {
    double dot = x * axis[0] + y * axis[1] + z * axis[2];
    return normalizedOrNull(x - dot * axis[0], y - dot * axis[1], z - dot * axis[2]);
  }

  private static double[] normalized(double x, double y, double z) {
    double[] result = normalizedOrNull(x, y, z);
    return result == null ? new double[] {1.0, 0.0, 0.0} : result;
  }

  private static double[] normalizedOrNull(double x, double y, double z) {
    double length = Math.sqrt(x * x + y * y + z * z);
    if (length < 1.0e-9) {
      return null;
    }
    return new double[] {x / length, y / length, z / length};
  }

  /**
   * 3D coordinates from <code>cdk-builder3d</code>.
   *
   * <p>
   * CDK's builder cannot handle every structure - verified: it throws for water, while methane and
   * ethanol succeed - so <code>null</code> here is a routine outcome, not an exceptional one, and
   * the caller falls back to a flattened 2D layout.
   */
  private static IAtomContainer layout3D(IAtomContainer molecule) {
    try {
      ModelBuilder3D builder = ModelBuilder3D.getInstance(molecule.getBuilder());
      return builder.generate3DCoordinates(molecule, false);
    } catch (CDKException e) {
      return null;
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * The 2D layout lifted into the z = 0 plane.
   *
   * <p>
   * This is the fallback for the structures <code>ModelBuilder3D</code> rejects. A flat model is
   * wrong as chemistry but right as a depiction of connectivity, and it beats returning the input
   * unevaluated for something as ordinary as water.
   */
  private static IAtomContainer flattened(IAtomContainer molecule) {
    if (molecule == null) {
      return null;
    }
    boolean any = false;
    for (IAtom atom : molecule.atoms()) {
      Point2d point = atom.getPoint2d();
      if (point == null) {
        continue;
      }
      atom.setPoint3d(new Point3d(point.x, point.y, 0.0));
      any = true;
    }
    return any ? molecule : null;
  }

  // ---------------------------------------------------------------- helpers

  /** A copy of the argument with 2D coordinates assigned, or <code>null</code>. */
  private static IAtomContainer prepared(IAST ast, EvalEngine engine, boolean includeHydrogens) {
    IAtomContainer molecule = MoleculeFunctions.molecule(ast.arg1());
    if (molecule == null) {
      return null;
    }
    if (includeHydrogens) {
      molecule = MoleculeFunctions.withExplicitHydrogens(molecule);
    }
    return layout2D(molecule);
  }

  /**
   * 2D coordinates from <code>cdk-sdg</code>, scaled to a bond length of one.
   *
   * <p>
   * CDK lays a diagram out with bonds of length 1.5. Everything else here - the gap left for a
   * label, the separation of the lines of a double bond, the radius of a highlight - is a plain
   * number in the same coordinates, and those numbers are the reference implementation's, which
   * works at a bond length of one. Scaling here is what lets them all be used as written.
   */
  private static IAtomContainer layout2D(IAtomContainer molecule) {
    try {
      StructureDiagramGenerator generator = new StructureDiagramGenerator();
      generator.setMolecule(molecule, false);
      generator.generateCoordinates();
      IAtomContainer laidOut = generator.getMolecule();
      double median = GeometryUtil.getBondLengthMedian(laidOut);
      if (median > 0.0 && !Double.isNaN(median)) {
        GeometryUtil.scaleMolecule(laidOut, 1.0 / median);
      }
      centre(laidOut);
      return laidOut;
    } catch (CDKException e) {
      return null;
    } catch (RuntimeException e) {
      return null;
    }
  }

  /** Moves a laid-out molecule so that it sits around the origin. */
  private static void centre(IAtomContainer molecule) {
    double x = 0.0;
    double y = 0.0;
    int count = 0;
    for (IAtom atom : molecule.atoms()) {
      Point2d point = atom.getPoint2d();
      if (point != null) {
        x += point.x;
        y += point.y;
        count++;
      }
    }
    if (count == 0) {
      return;
    }
    double centreX = x / count;
    double centreY = y / count;
    for (IAtom atom : molecule.atoms()) {
      Point2d point = atom.getPoint2d();
      if (point != null) {
        point.set(point.x - centreX, point.y - centreY);
      }
    }
  }

  /**
   * The molecule a structure diagram draws.
   *
   * <p>
   * Its hydrogens are the ones a chemist writes: those on a heteroatom, which the diagram spells
   * out, and not those on a carbon, which the convention leaves implicit.
   * <code>IncludeHydrogens -&gt; True</code> asks for all of them instead.
   */
  private static IAtomContainer diagram(IAST ast, EvalEngine engine) {
    IAtomContainer molecule = MoleculeFunctions.molecule(ast.arg1());
    if (molecule == null) {
      return null;
    }
    molecule = includeHydrogens(ast, engine, false) //
        ? MoleculeFunctions.withExplicitHydrogens(molecule)
        : withHeteroatomHydrogens(molecule);
    return layout2D(molecule);
  }

  /** A copy whose heteroatoms carry their hydrogens explicitly, and whose carbons do not. */
  private static IAtomContainer withHeteroatomHydrogens(IAtomContainer molecule) {
    try {
      IAtomContainer copy = molecule.clone();
      IChemObjectBuilder builder = copy.getBuilder();
      for (int i = copy.getAtomCount() - 1; i >= 0; i--) {
        IAtom atom = copy.getAtom(i);
        if (!isLabelled(atom)) {
          continue;
        }
        Integer count = atom.getImplicitHydrogenCount();
        if (count == null || count.intValue() <= 0) {
          continue;
        }
        for (int h = 0; h < count.intValue(); h++) {
          IAtom hydrogen = builder.newInstance(IAtom.class, "H");
          hydrogen.setImplicitHydrogenCount(Integer.valueOf(0));
          copy.addAtom(hydrogen);
          copy.addBond(builder.newInstance(IBond.class, atom, copy.getAtom(copy.getAtomCount() - 1),
              IBond.Order.SINGLE));
        }
        atom.setImplicitHydrogenCount(Integer.valueOf(0));
      }
      return copy;
    } catch (CloneNotSupportedException e) {
      return molecule;
    } catch (RuntimeException e) {
      return molecule;
    }
  }

  /**
   * The counterpart of {@link MoleculeFunctions#withExplicitHydrogens}: a copy whose hydrogens are
   * folded back into their neighbours' implicit-hydrogen counts.
   */
  private static IAtomContainer withoutExplicitHydrogens(IAtomContainer molecule) {
    try {
      return AtomContainerManipulator.suppressHydrogens(molecule.clone());
    } catch (CloneNotSupportedException e) {
      return molecule;
    } catch (RuntimeException e) {
      return molecule;
    }
  }

  /**
   * <code>IncludeHydrogens</code> is not a display flag: it changes which atoms exist, so it
   * changes the coordinate list and the diagram alike.
   */
  private static boolean includeHydrogens(IAST ast, EvalEngine engine, boolean defaultValue) {
    int start = optionStart(ast);
    if (ast.argSize() < start) {
      return defaultValue;
    }
    final OptionArgs options = new OptionArgs(ast.topHead(), ast, start, engine);
    IExpr value = options.getOption(S.IncludeHydrogens);
    return value.isPresent() ? value.isTrue() : defaultValue;
  }

  /** <code>PlotTheme -&gt; "SpaceFilling"</code> switches the 3D model to van der Waals spheres. */
  private static boolean spaceFilling(IAST ast, EvalEngine engine) {
    int start = optionStart(ast);
    if (ast.argSize() < start) {
      return false;
    }
    final OptionArgs options = new OptionArgs(ast.topHead(), ast, start, engine);
    IExpr value = options.getOption(S.PlotTheme);
    return value.isPresent() && "spacefilling".equalsIgnoreCase(value.toString());
  }

  /**
   * Forwards the caller's own option rules onto the emitted <code>Graphics</code> object, so
   * <code>PlotLabel</code>, <code>ImageSize</code> and the rest reach the renderer. The options
   * this module consumes itself are dropped: they describe the molecule, not the picture.
   */
  private static void appendUserOptions(IASTAppendable graphics, IAST ast) {
    for (int i = optionStart(ast); i < ast.size(); i++) {
      appendUserOption(graphics, ast.get(i));
    }
  }

  private static void appendUserOption(IASTAppendable graphics, IExpr option) {
    if (option.isList()) {
      IAST list = (IAST) option;
      for (int i = 1; i < list.size(); i++) {
        appendUserOption(graphics, list.get(i));
      }
      return;
    }
    if (option.isRule()) {
      IExpr key = ((IAST) option).arg1();
      if (key == S.IncludeHydrogens || key == S.PlotTheme) {
        return;
      }
      graphics.append(option);
    }
  }

  /**
   * The optional highlight argument, or <code>null</code>. It sits between the molecule and the
   * options.
   */
  private static MoleculeHighlights highlights(IAST ast) {
    return ast.argSize() >= 2 ? MoleculeHighlights.parse(ast.arg2()) : null;
  }

  /** The index of the first option rule: after the molecule, and after any highlight argument. */
  private static int optionStart(IAST ast) {
    return highlights(ast) == null ? 2 : 3;
  }

  /**
   * Named highlights become the legend of the emitted graphics, following the same
   * <code>PlotLegends</code> plus <code>PlotStyle</code> pairing the other Symja plots use. An
   * explicit <code>PlotLegends</code> from the caller wins.
   */
  private static void appendLegend(IASTAppendable graphics, IAST ast,
      MoleculeHighlights highlights) {
    if (highlights == null || !highlights.hasLabels()) {
      return;
    }
    for (int i = 1; i < graphics.size(); i++) {
      IExpr arg = graphics.get(i);
      if (arg.isRule() && ((IAST) arg).arg1() == S.PlotLegends) {
        return;
      }
    }
    graphics.append(F.Rule(S.PlotLegends, highlights.legendLabels()));
    graphics.append(F.Rule(S.PlotStyle, highlights.legendColors()));
  }

  /**
   * The presentation a molecular model wants: no bounding box, and the neutral lighting that keeps
   * the CPK colours recognisable. Anything the caller set explicitly wins.
   */
  private static void appendModelOptions(IASTAppendable graphics) {
    appendDefaultOption(graphics, S.Boxed, S.False);
    appendDefaultOption(graphics, S.Lighting, F.stringx("Neutral"));
  }

  private static void appendDefaultOption(IASTAppendable graphics, IExpr key, IExpr value) {
    for (int i = 1; i < graphics.size(); i++) {
      IExpr arg = graphics.get(i);
      if (arg.isRule() && ((IAST) arg).arg1() == key) {
        return;
      }
    }
    graphics.append(F.Rule(key, value));
  }

  private static IAST plotOptions() {
    return F.List( //
        F.Rule(S.IncludeHydrogens, S.False), //
        F.Rule(S.PlotLabel, S.None));
  }

  public static void initialize() {
    Initializer.init();
  }

  private DepictionFunctions() {}
}
