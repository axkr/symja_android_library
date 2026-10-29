package org.matheclipse.core.data;

import java.util.Arrays;
import java.util.Map;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;

/**
 * <code>LatticeData(name, property)</code> - the value of a property of a named lattice.
 *
 * <p>
 * The known lattices are the three cubic lattices <code>"SimpleCubic"</code>,
 * <code>"BodyCenteredCubic"</code> and <code>"FaceCenteredCubic"</code>, each given by an integral
 * basis. The metric properties - Gram matrix, determinant, volume, packing radius and density - are
 * computed from the basis and the minimal norm. <code>LatticeData()</code> is the list of the
 * lattices and <code>LatticeData("Properties")</code> the list of the property names.
 */
public class LatticeData extends AbstractFunctionEvaluator {

  /** One entry of the lattice table. */
  private static final class Lattice {
    final String name;
    final String[] alternateNames;
    /** The rows are the basis vectors. */
    final int[][] basis;
    /** The smallest squared length of a non-zero lattice vector. */
    final int minimalNorm;
    final int kissingNumber;
    /**
     * The points of the cubic unit cell besides its corners, in units of half the side of the
     * cell.
     */
    final int[][] cellPoints;

    Lattice(String name, String[] alternateNames, int[][] basis, int minimalNorm,
        int kissingNumber, int[][] cellPoints) {
      this.name = name;
      this.alternateNames = alternateNames;
      this.basis = basis;
      this.minimalNorm = minimalNorm;
      this.kissingNumber = kissingNumber;
      this.cellPoints = cellPoints;
    }
  }

  /** The lattices by their standard names. */
  private static final Map<String, Lattice> LATTICES = Map.of( //
      "SimpleCubic", new Lattice("simple cubic", new String[] {"sc"}, //
          new int[][] {{1, 0, 0}, {0, 1, 0}, {0, 0, 1}}, 1, 6, //
          new int[0][]), //
      "BodyCenteredCubic", new Lattice("body-centered cubic", new String[] {"bcc"},
          new int[][] {{2, 0, 0}, {0, 2, 0}, {1, 1, 1}}, 3, 8, //
          new int[][] {{1, 1, 1}}), //
      "FaceCenteredCubic", new Lattice("face-centered cubic", new String[] {"fcc"},
          new int[][] {{-1, -1, 0}, {1, -1, 0}, {0, 1, -1}}, 2, 12, //
          new int[][] {{1, 1, 0}, {1, 1, 2}, {1, 0, 1}, {1, 2, 1}, {0, 1, 1}, {2, 1, 1}}));

  /** The property names, in alphabetical order. */
  private static final String[] PROPERTIES = {"AlternateNames", "AutomorphismGroupOrder", "Basis",
      "CenterDensity", "Classes", "CoveringRadius", "CoxeterNumber", "Density", "Determinant",
      "Dimension", "Dual", "Even", "Extremal", "GeneratorMatrix", "Genus", "GlueVectors",
      "GramMatrix", "HermiteInvariant", "Image", "Integral", "KissingNumber", "MinimalNorm",
      "MinimalVectors", "ModularNumber", "Name", "Nonextremal", "Nonintegral", "Nonunimodular",
      "Notation", "Odd", "PackingRadius", "QuadraticForm", "RadialFunction", "StandardName",
      "ThetaSeriesFunction", "Thickness", "Unimodular", "Volume"};

  private static final String NOTPROP =
      "`1` is not a known property for LatticeData. Use LatticeData[\"Properties\"] for a list of properties.";

  public LatticeData() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.isAST0()) {
      String[] names = LATTICES.keySet().toArray(new String[0]);
      Arrays.sort(names);
      return F.mapRange(0, names.length, i -> F.stringx(names[i]));
    }
    IExpr arg1 = ast.arg1();
    if (ast.isAST1()) {
      if (arg1.isString("Properties")) {
        return F.mapRange(0, PROPERTIES.length, i -> F.stringx(PROPERTIES[i]));
      }
      if (arg1.isString() && LATTICES.containsKey(arg1.toString())) {
        return F.NIL;
      }
      // `1` is not a known entity, class or tag for `2`. Use `2`[] for a list of entities.
      return Errors.printMessage(S.LatticeData, "notent", F.List(arg1, S.LatticeData), engine);
    }
    // an unknown property is reported before an unknown lattice
    IExpr arg2 = ast.arg2();
    if (!arg2.isString() || Arrays.binarySearch(PROPERTIES, arg2.toString()) < 0) {
      // `1` is not a known property for LatticeData.
      return Errors.printMessage(S.LatticeData, "notprop", F.List(arg2), engine);
    }
    Lattice lattice = arg1.isString() ? LATTICES.get(arg1.toString()) : null;
    if (lattice == null) {
      // `1` is not a known entity, class or tag for `2`. Use `2`[] for a list of entities.
      return Errors.printMessage(S.LatticeData, "notent", F.List(arg1, S.LatticeData), engine);
    }
    return property(arg1.toString(), lattice, arg2.toString(), engine);
  }

  private static IExpr property(String standardName, Lattice lattice, String property,
      EvalEngine engine) {
    switch (property) {
      case "AlternateNames":
        return F.List(lattice.alternateNames);
      case "Basis":
      case "GeneratorMatrix":
        IAST basis = F.mapRange(0, lattice.basis.length, i -> F.List(lattice.basis[i]));
        // the identity basis of the simple cubic lattice is given as a sparse array
        return isIdentity(lattice.basis) ? engine.evaluate(F.unaryAST1(S.SparseArray, basis))
            : basis;
      case "CenterDensity":
        // the packing radius cubed per unit of volume
        return engine.evaluate(F.Divide(F.Power(packingRadius(lattice), F.C3), volume(lattice)));
      case "Density":
        // the volume of the ball of the packing radius per unit of volume
        return engine.evaluate(F.Times(F.QQ(4, 3), S.Pi,
            F.Divide(F.Power(packingRadius(lattice), F.C3), volume(lattice))));
      case "Determinant":
        return engine.evaluate(F.Det(gramMatrix(lattice)));
      case "Dimension":
        return F.ZZ(lattice.basis.length);
      case "GramMatrix":
        return gramMatrix(lattice);
      case "Image":
        return image(lattice);
      case "KissingNumber":
        return F.ZZ(lattice.kissingNumber);
      case "MinimalNorm":
        return F.ZZ(lattice.minimalNorm);
      case "Name":
        return F.stringx(lattice.name);
      case "PackingRadius":
        return engine.evaluate(packingRadius(lattice));
      case "StandardName":
        return F.stringx(standardName);
      case "Volume":
        return engine.evaluate(volume(lattice));
      default:
        return F.Missing(S.NotAvailable);
    }
  }

  private static boolean isIdentity(int[][] matrix) {
    for (int i = 0; i < matrix.length; i++) {
      for (int j = 0; j < matrix[i].length; j++) {
        if (matrix[i][j] != (i == j ? 1 : 0)) {
          return false;
        }
      }
    }
    return true;
  }

  /** The matrix of the scalar products of the basis vectors. */
  private static IAST gramMatrix(Lattice lattice) {
    int[][] basis = lattice.basis;
    return F.mapRange(0, basis.length, i -> F.mapRange(0, basis.length, j -> {
      int sum = 0;
      for (int k = 0; k < basis[i].length; k++) {
        sum += basis[i][k] * basis[j][k];
      }
      return F.ZZ(sum);
    }));
  }

  /** The volume of the fundamental cell, the square root of the determinant. */
  private static IExpr volume(Lattice lattice) {
    return F.Sqrt(F.Det(gramMatrix(lattice)));
  }

  /** Half the length of the shortest non-zero lattice vector. */
  private static IExpr packingRadius(Lattice lattice) {
    return F.Times(F.C1D2, F.Sqrt(F.ZZ(lattice.minimalNorm)));
  }

  /** A cubic unit cell of side 1: its twelve edges and its lattice points. */
  private static IExpr image(Lattice lattice) {
    IASTAppendable edges = F.ListAlloc(12);
    IASTAppendable points = F.ListAlloc(8 + lattice.cellPoints.length);
    for (int corner = 0; corner < 8; corner++) {
      points.append(cornerPoint(corner));
      for (int bit = 1; bit < 8; bit <<= 1) {
        if ((corner & bit) == 0) {
          edges.append(F.Line(F.List(cornerPoint(corner), cornerPoint(corner | bit))));
        }
      }
    }
    for (int[] p : lattice.cellPoints) {
      points.append(F.List(F.QQ(p[0], 2), F.QQ(p[1], 2), F.QQ(p[2], 2)));
    }
    return F.unaryAST1(S.Graphics3D,
        F.List(F.List(S.Gray, edges), F.List(S.Red, F.PointSize(F.num(0.04)), F.Point(points))));
  }

  /** The corner of the unit cube whose coordinates are the bits of <code>corner</code>. */
  private static IAST cornerPoint(int corner) {
    return F.List(F.ZZ(corner & 1), F.ZZ((corner >> 1) & 1), F.ZZ((corner >> 2) & 1));
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_0_2;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.putMessage(IPatternMatcher.SET, "notprop", F.stringx(NOTPROP));
  }
}
