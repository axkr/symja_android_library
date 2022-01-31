package org.matheclipse.io.others;

import java.util.List;
import org.matheclipse.core.builtin.FileFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.ParserConfig;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.ast.ASTNode;
import junit.framework.TestCase;

public class GetTestSingleRunJUnit extends TestCase {

  private static final String VECTOR_ANALYSIS = "(* ::Package:: *)\n" + "\n"
      + "(* :Title: Three-Dimensional Vector Analysis *)\n" + "\n" + "(* :Summary:\n"
      + "Standard vector calculus operators in three dimensions. Includes coordinate\n"
      + "transformations between orthogonal coordinate systems. *)\n" + "\n" + "\n"
      + "BeginPackage[\"VectorAnalysis`\"]\n" + "\n"
      + "$CoordSysList = {Cartesian, Spherical, Cylindrical};\n" + "\n"
      + "$IsVecQ[v_] := Length[v] == 3 && VectorQ[v];\n" + "\n" + "\n"
      + "Unprotect[DotProduct, CrossProduct, ScalarTripleProduct, \n"
      + "    CoordinatesToCartesian, CoordinatesFromCartesian, Coordinates,\n"
      + "    Parameters, CoordinateRanges, ParameterRanges, SetCoordinates,\n"
      + "    CoordinateSystem, ScaleFactors, JacobianDeterminant, JacobianMatrix];\n" + "\n"
      + "  (* ============================ Dot Product ============================ *)\n" + "\n"
      + "DotProduct::usage =\n"
      + "\"DotProduct[v1, v2] gives the dot product between v1 and v2 in three spatial\n"
      + "dimensions. DotProduct[v1, v2, coordsys] gives the dot product of vectors v1\n"
      + "and v2 in the specified coodrinate system, coordsys.\";\n" + "\n"
      + "DotProduct[v1_?$IsVecQ, v2_?$IsVecQ, coordsys_:CoordinateSystem] :=\n"
      + "    Module[{c1, c2},\n" + "    c1 = CoordinatesToCartesian[v1, coordsys];\n"
      + "    c2 = CoordinatesToCartesian[v2, coordsys];\n" + "    Apply[Plus, c1 * c2]]\n" + "\n"
      + "Attributes[DotProduct] = {ReadProtected, Protected};\n" + "\n" + "\n"
      + "  (* =========================== Cross Product =========================== *)\n" + "\n"
      + "CrossProduct::usage =\n"
      + "\"CrossProduct[v1, v2] gives the cross product between v1 and v2 in three\n"
      + "spatial dimensions. DotProduct[v1, v2, coordsys] gives the cross product of\n"
      + "vectors v1 and v2 in the specified coodrinate system, coordsys.\";\n" + "\n"
      + "CrossProduct[v1_?$IsVecQ, v2_?$IsVecQ, coordsys_:CoordinateSystem] :=\n"
      + "    Module[{c1, c2},\n" + "    c1 = CoordinatesToCartesian[v1, coordsys];\n"
      + "    c2 = CoordinatesToCartesian[v2, coordsys];\n"
      + "    CoordinatesFromCartesian[Det[{IdentityMatrix[3], c1, c2}]]]\n" + "\n"
      + "Attributes[CrossProduct] = {ReadProtected, Protected};\n" + "\n" + "\n"
      + "  (* ======================= Scalar Triple Product ======================= *)\n" + "\n"
      + "ScalarTripleProduct::usage =\n"
      + "\"ScalarTripleProduct[v1, v2, v3] gives the scalar triple product product\n"
      + "between v1, v2 and v3 in three spatial dimensions.\n"
      + "ScalarTripleProduct[v1, v2, v3, coordsys] gives the scalar triple product of\n"
      + "vectors v1, v2 and v3 in the specified coodrinate system, coordsys.\";\n" + "\n"
      + "ScalarTripleProduct[v1_?$IsVecQ, v2_?$IsVecQ, v3_?$IsVecQ,\n"
      + "        coordsys_:CoordinateSystem] :=\n" + "    Module[{c1, c2, c3},\n"
      + "    c1 = CoordinatesToCartesian[v1, coordsys];\n"
      + "    c2 = CoordinatesToCartesian[v2, coordsys];\n"
      + "    c3 = CoordinatesToCartesian[v3, coordsys];\n" + "    Det[{c1, c2, c3}]]\n" + "\n"
      + "Attributes[ScalarTripleProduct] = {ReadProtected, Protected};\n" + "\n" + "\n"
      + "  (* ======================= Coordinates To Cartesian ==================== *)\n" + "\n"
      + "CoordinatesToCartesian::usage =\n"
      + "\"CoordinatesToCartesian[pt] converts the given point, pt, from the default\n"
      + "coordinates to Cartesian coordinates. CoordinatesToCartesian[pt, coordsys]\n"
      + "converts the given point, pt, from the specified coordinate system, coordsys,\n"
      + "to Cartesian coordinates.\";\n" + "\n"
      + "CoordinatesToCartesian[pt_?$IsVecQ, coordsys_:CoordinateSystem] :=\n"
      + "    Module[{v1 = pt[[1]], v2 = pt[[2]], v3 = pt[[3]]},\n" + "        Switch[coordsys,\n"
      + "            Cartesian, {v1, v2, v3},\n"
      + "            Spherical, {v1 Sin[v2] Cos[v3], v1 Sin[v2] Sin[v3], v1 Cos[v2]},\n"
      + "            Cylindrical, {v1 Cos[v2], v1 Sin[v2], v3}\n" + "        ]\n" + "    ]\n" + "\n"
      + "Attributes[CoordinatesToCartesian] = {ReadProtected, Protected};\n" + "\n" + "\n"
      + "  (* ====================== Coordinates From Cartesian =================== *)\n" + "\n"
      + "CoordinatesFromCartesian::usage =\n"
      + "\"CoordinatesFromCartesian[pt] converts the given point, pt, from Cartesian\n"
      + "coordinates to the default coordinate system.\n"
      + "CoordinatesFromCartesian[pt, coordsys] converts the given point, pt, from\n"
      + "Cartesian coordinates to the specified coordinate system, coordsys.\";\n" + "\n"
      + "CoordinatesFromCartesian[pt_?$IsVecQ, coordsys_:CoordinateSystem] :=\n"
      + "    Module[{v1 = pt[[1]], v2 = pt[[2]], v3 = pt[[3]]},\n" + "        Switch[coordsys,\n"
      + "            Cartesian, {v1, v2, v3},\n"
      + "            Spherical, {Sqrt[v1^2 + v2^2 + v3^2],\n"
      + "                ArcCos[v3/Sqrt[v1^2 + v2^2 + v3^2]], ArcTan[v1, v2]},\n"
      + "            Cylindrical, {Sqrt[v1^2 + v2^2], ArcTan[v1, v2], v3}\n" + "        ]\n"
      + "    ]\n" + "\n" + "Attributes[CoordinatesFromCartesian] = {ReadProtected, Protected};\n"
      + "\n" + "\n"
      + "  (* ============================ Coordinates ============================ *)\n" + "\n"
      + "Coordinates::usage =\n"
      + "\"Coordinates[] gives the default cordinate variables of the current coordinate\n"
      + "system. Coordinates[coordsys] gives the default coordinate variables of the\n"
      + "specified coordinate system, coordsys.\";\n" + "\n"
      + "Coordinates::invalid = \"`1` is not a valid coordinate system specification.\";\n" + "\n"
      + "Coordinates[] := Coordinates[CoordinateSystem];\n"
      + "Coordinates[Cartesian] ^= {Xx, Yy, Zz};\n"
      + "Coordinates[Spherical] ^= {Rr, Ttheta, Pphi};\n"
      + "Coordinates[Cylindrical] ^= {Rr, Ttheta, Zz};\n" + "\n"
      + "Attributes[Coordinates] = {ReadProtected, Protected};\n" + "\n" + "\n"
      + "  (* ============================= Parameters ============================ *)\n" + "\n"
      + "Parameters::usage =\n"
      + "\"Parameters[] gives the default paramater variables of the current coordinate\n"
      + "system. Parameters[coordsys] gives the default paramater variables for the\n"
      + "specified coordinate system, coordsys.\";\n" + "\n"
      + "Parameters[] := Parameters[CoordinateSystem];\n" + "Parameters[Cartesian] ^= {};\n"
      + "Parameters[Spherical] ^= {};\n" + "Parameters[Cylindrical] ^= {};\n" + "\n"
      + "Attributes[Parameters] = {ReadProtected, Protected};\n" + "\n" + "\n"
      + "  (* ========================= Coordinate Ranges ========================= *)\n" + "\n"
      + "CoordinateRanges::usage =\n"
      + "\"CoordinateRanges[] gives the acceptable range of coordinates for the current\n"
      + "coordinate system. CoordinateRanges[coordsys] gives the acceptable range of\n"
      + "coordinates for the specified coordinate system, coordsys.\";\n" + "\n"
      + "CoordinateRanges[] := CoordinateRanges[CoordinateSystem];\n"
      + "CoordinateRanges[Cartesian] ^= {-Infinity < Xx < Infinity,\n"
      + "    -Infinity < Yy < Infinity, -Infinity < Zz <Infinity};\n"
      + "CoordinateRanges[Spherical] ^=   {0 <= Rr < Infinity, 0 <= Ttheta <= Pi,\n"
      + "    -Pi < Pphi <= Pi};\n"
      + "CoordinateRanges[Cylindrical] ^= {0 <= Rr < Infinity, -Pi < Ttheta <= Pi,\n"
      + "     -Infinity < Zz <Infinity};\n" + "\n"
      + "Attributes[CoordinateRanges] = {ReadProtected, Protected};\n" + "\n" + "\n"
      + "  (* ========================== Parameter Ranges ========================= *)\n" + "\n"
      + "ParameterRanges::usage =\n"
      + "\"ParameterRanges[] gives the acceptable range of parameters for the current\n"
      + "coordinate system. ParameterRanges[coordsys] gives the acceptable range of\n"
      + "parameters for the specified coordinate system, coordsys.\";\n" + "\n"
      + "ParameterRanges[] := ParametersRanges[CoordinateSystem];\n"
      + "ParameterRanges[Cartesian] ^= Null;\n" + "ParameterRanges[Spherical] ^= Null;\n"
      + "ParameterRanges[Cylindrical] ^= Null;\n" + "\n"
      + "Attributes[ParameterRanges] = {ReadProtected, Protected};\n" + "\n" + "\n"
      + "  (* ========================== Set Coordinates ========================== *)\n" + "\n"
      + "SetCoordinates::usage =\n"
      + "\"SetCoordinates[coordsys] sets the current coordinate system\";\n" + "\n"
      + "SetCoordinates[coordsys_Symbol] :=\n" + "    If[MemberQ[$CoordSysList, coordsys],\n"
      + "        Unprotect[CoordinateSystem];\n" + "        CoordinateSystem = coordsys;\n"
      + "        Protect[CoordinateSystem];\n" + "        Apply[coordsys, Coordinates[coordsys]],\n"
      + "        Message[Coordinates::invalid, coordsys];\n" + "        $Failed]\n" + "\n"
      + "Attributes[SetCoordinates] = {ReadProtected, Protected};\n" + "\n" + "\n"
      + "  (* ========================= Coordinate System ========================= *)\n" + "\n"
      + "CoordinateSystem::usage = \"CoordinateSystem is the current coordinate system\";\n" + "\n"
      + "CoordinateSystem = Cartesian;\n" + "\n"
      + "Attributes[CoordinateSystem] = {ReadProtected, Protected};\n" + "\n" + "\n"
      + "  (* =========================== Scale Factors =========================== *)\n" + "\n"
      + "ScaleFactors::usage = \n"
      + "\"ScaleFactors[pt] gives the scale factors at point pt in the current\n"
      + "coordinate system. ScaleFactors[pt, coordsys] gives the scale factors in the\n"
      + "specified coordinate system at point pt.\";\n" + "\n"
      + "ScaleFactors[pt_?$IsVecQ, coordsys_:CoordinateSystem] :=\n" + "    Module[{},\n"
      + "        Switch[coordsys,\n" + "            Cartesian, {1, 1, 1},\n"
      + "            Spherical, {1, pt[[1]], pt[[1]] Sin[pt[[2]]]},\n"
      + "            Cylindrical, {1, pt[[1]], 1}\n" + "        ]\n" + "    ]\n" + "\n"
      + "ScaleFactors[coordsys_Symbol:CoordinateSystem] := \n"
      + "        ScaleFactors[Coordinates[coordsys], coordsys]\n" + "        \n"
      + "Attributes[ScaleFactors] = {ReadProtected, Protected};\n" + "\n" + "\n"
      + "  (* ======================== Jacobian Determinant ======================= *)\n" + "\n"
      + "JacobianDeterminant::usage =\n"
      + "\"JacobianDeterminant[pt] gives the determinant of the Jacobian describing\n"
      + "the transformation from the current coordinate system to Cartesian\n"
      + "coordinates at the specified point pt. JacobianDeterminant[pt, coordsys]\n"
      + "gives the determinant of the Jacobian Matrix in the specified coordinates\n"
      + "at the given point pt.\";\n" + "\n"
      + "JacobianDeterminant[pt_?$IsVecQ, coordsys_:CoordinateSystem] :=\n"
      + "    Times @@ ScaleFactors[pt, coordsys]\n" + "\n"
      + "JacobianDeterminant[coordsys_Symbol:CoordinateSystem] :=\n"
      + "    Times @@ ScaleFactors[coordsys]\n" + "\n"
      + "Attributes[JacobianDeterminant] = {ReadProtected, Protected};\n" + "\n" + "\n"
      + "  (* ========================== Jacobian Matrix ========================== *)\n" + "\n"
      + "JacobianMatrix::usage =\n"
      + "\"JacobianMatrix[pt] gives the Jacobian describing the transformation from \n"
      + "the current coordinate system to Cartesian coordinates at the specified \n"
      + "point pt. JacobianMatrix[pt, coordsys] gives the Jacobian Matrix in the \n"
      + "specified coordinates at the given point pt.\";\n" + "\n"
      + "JacobianMatrix[pt_?$IsVecQ, coordsys_:CoordinateSystem] :=\n" + "    Module[{cpt},\n"
      + "        cpt = CoordinatesToCartesian[pt, coordsys];\n"
      + "        Outer[D, cpt, Coordinates[coordsys]]\n" + "    ]\n" + "\n"
      + "JacobianMatrix[coordsys_Symbol:CoordinateSystem] :=\n"
      + "    JacobianMatrix[Coordinates[coordsys], coordsys]\n" + "\n"
      + "Attributes[JacobianMatrix] = {ReadProtected, Protected};\n" + "\n" + "\n"
      + "EndPackage[]\n" + "";

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS = false;
    // wait for initializing of Integrate() rules:
    F.await();
  }

  public void testGet() {
    EvalEngine engine = new EvalEngine(false);
    ExprEvaluator fEvaluator = new ExprEvaluator(engine, false, (short) 100);
    fEvaluator.getEvalEngine().setFileSystemEnabled(true);

    // DecimalFormatSymbols usSymbols = new DecimalFormatSymbols(Locale.US);
    // DecimalFormat decimalFormat = new DecimalFormat("0.0####", usSymbols);
    OutputFormFactory fOutputFactory = OutputFormFactory.get(false, false, 5, 7);

    final Parser parser = new Parser(engine.isRelaxedSyntax(), true);
    final List<ASTNode> node = parser.parsePackage(VECTOR_ANALYSIS);
    FileFunctions.evaluatePackage(node, engine);

    // print Coordinates::usage to console
    IExpr result = fEvaluator.eval("?Coordinates");
    assertEquals("Null", //
        //
        fOutputFactory.toString(result));

    result = fEvaluator.eval("DotProduct[{a,b,c},{d,e,f}, Cartesian]");
    assertEquals("a*d+b*e+c*f", //
        fOutputFactory.toString(result));

    result = fEvaluator.eval("DotProduct[{a,b,c},{d,e,f}, Spherical]");
    assertEquals(
        "a*d*Cos[b]*Cos[e]+a*d*Cos[c]*Cos[f]*Sin[b]*Sin[e]+a*d*Sin[b]*Sin[c]*Sin[e]*Sin[f]", //
        fOutputFactory.toString(result));

    result = fEvaluator.eval("DotProduct[{a,b,c},{d,e,f}, Cylindrical]");
    assertEquals("c*f+a*d*Cos[b]*Cos[e]+a*d*Sin[b]*Sin[e]", //
        //
        fOutputFactory.toString(result));

    result = fEvaluator.eval("CoordinatesToCartesian[{2, Pi, 3}, Cylindrical]");
    assertEquals("{-2,0,3}", //
        //
        fOutputFactory.toString(result));

    result = fEvaluator.eval("CoordinatesFromCartesian[{-2,0,3}, Cylindrical]");
    assertEquals("{2,Pi,3}", //
        //
        fOutputFactory.toString(result));

    result = fEvaluator.eval("CoordinatesToCartesian[{2, Pi / 4, Pi / 3}, Spherical]");
    assertEquals("{1/Sqrt[2],Sqrt[3/2],Sqrt[2]}", //
        //
        fOutputFactory.toString(result));

    result = fEvaluator.eval("CoordinatesFromCartesian[{1/Sqrt[2],Sqrt[3/2],Sqrt[2]}, Spherical]");
    assertEquals("{2,Pi/4,Pi/3}", //
        //
        fOutputFactory.toString(result));

    result = fEvaluator.eval("Coordinates[]");
    assertEquals("{Xx,Yy,Zz}", //
        //
        fOutputFactory.toString(result));

    result = fEvaluator.eval("CoordinateRanges[]");
    assertEquals("{-Infinity<Xx<Infinity,-Infinity<Yy<Infinity,-Infinity<Zz<Infinity}", //
        //
        fOutputFactory.toString(result));

    result = fEvaluator.eval("CoordinateRanges[Spherical]");
    assertEquals("{0<=Rr&&Rr<Infinity,0<=Ttheta<=Pi,-Pi<Pphi&&Pphi<=Pi}", //
        //
        fOutputFactory.toString(result));

    // TODO
    // buf = new StringBuilder();
    // result = fEvaluator.eval("CrossProduct[{1,2,3}, {4,5,6}, Spherical]");
    // fOutputFactory.convert(buf, result);
    // assertEquals("c*f+a*d*Cos[b]*Cos[e]+a*d*Sin[b]*Sin[e]", //
    // //
    // fOutputFactory.toString(result));

  }
}
