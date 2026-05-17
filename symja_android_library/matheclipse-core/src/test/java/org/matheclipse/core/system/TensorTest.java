package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;

/** Tests for tensor functions */
public class TensorTest extends ExprEvaluatorTestCase {

  @Test
  public void testHodgeDual() {
    // check(
    // "Normal@HodgeDual({{a, b},{ c,d}})", //
    // "(b-c)/2");
    check("Normal@HodgeDual({a, b, c})", //
        "{{0,c,-b},{-c,0,a},{b,-a,0}}");
    check("Normal@HodgeDual({a, b, c, d})", //
        "{{{0,0,0,0},{0,0,-d,c},{0,d,0,-b},{0,-c,b,0}},{{0,0,d,-c},{0,0,0,0},{-d,0,0,a},{c,\n"
            + "0,-a,0}},{{0,-d,0,b},{d,0,0,-a},{0,0,0,0},{-b,a,0,0}},{{0,c,-b,0},{-c,0,a,0},{b,-a,\n"
            + "0,0},{0,0,0,0}}}");
    check("Normal@HodgeDual({a, b, c, d, e})", //
        "{{{{0,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0}},{{0,0,0,0,0},{0,\n"
            + "0,0,0,0},{0,0,0,e,-d},{0,0,-e,0,c},{0,0,d,-c,0}},{{0,0,0,0,0},{0,0,0,-e,d},{0,0,\n"
            + "0,0,0},{0,e,0,0,-b},{0,-d,0,b,0}},{{0,0,0,0,0},{0,0,e,0,-c},{0,-e,0,0,b},{0,0,0,\n"
            + "0,0},{0,c,-b,0,0}},{{0,0,0,0,0},{0,0,-d,c,0},{0,d,0,-b,0},{0,-c,b,0,0},{0,0,0,0,\n"
            + "0}}},{{{0,0,0,0,0},{0,0,0,0,0},{0,0,0,-e,d},{0,0,e,0,-c},{0,0,-d,c,0}},{{0,0,0,0,\n"
            + "0},{0,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0}},{{0,0,0,e,-d},{0,0,0,0,0},{0,\n"
            + "0,0,0,0},{-e,0,0,0,a},{d,0,0,-a,0}},{{0,0,-e,0,c},{0,0,0,0,0},{e,0,0,0,-a},{0,0,\n"
            + "0,0,0},{-c,0,a,0,0}},{{0,0,d,-c,0},{0,0,0,0,0},{-d,0,0,a,0},{c,0,-a,0,0},{0,0,0,\n"
            + "0,0}}},{{{0,0,0,0,0},{0,0,0,e,-d},{0,0,0,0,0},{0,-e,0,0,b},{0,d,0,-b,0}},{{0,0,0,-e,d},{\n"
            + "0,0,0,0,0},{0,0,0,0,0},{e,0,0,0,-a},{-d,0,0,a,0}},{{0,0,0,0,0},{0,0,0,0,0},{0,0,\n"
            + "0,0,0},{0,0,0,0,0},{0,0,0,0,0}},{{0,e,0,0,-b},{-e,0,0,0,a},{0,0,0,0,0},{0,0,0,0,\n"
            + "0},{b,-a,0,0,0}},{{0,-d,0,b,0},{d,0,0,-a,0},{0,0,0,0,0},{-b,a,0,0,0},{0,0,0,0,0}}},{{{\n"
            + "0,0,0,0,0},{0,0,-e,0,c},{0,e,0,0,-b},{0,0,0,0,0},{0,-c,b,0,0}},{{0,0,e,0,-c},{0,\n"
            + "0,0,0,0},{-e,0,0,0,a},{0,0,0,0,0},{c,0,-a,0,0}},{{0,-e,0,0,b},{e,0,0,0,-a},{0,0,\n"
            + "0,0,0},{0,0,0,0,0},{-b,a,0,0,0}},{{0,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0},{\n"
            + "0,0,0,0,0}},{{0,c,-b,0,0},{-c,0,a,0,0},{b,-a,0,0,0},{0,0,0,0,0},{0,0,0,0,0}}},{{{\n"
            + "0,0,0,0,0},{0,0,d,-c,0},{0,-d,0,b,0},{0,c,-b,0,0},{0,0,0,0,0}},{{0,0,-d,c,0},{0,\n"
            + "0,0,0,0},{d,0,0,-a,0},{-c,0,a,0,0},{0,0,0,0,0}},{{0,d,0,-b,0},{-d,0,0,a,0},{0,0,\n"
            + "0,0,0},{b,-a,0,0,0},{0,0,0,0,0}},{{0,-c,b,0,0},{c,0,-a,0,0},{-b,a,0,0,0},{0,0,0,\n"
            + "0,0},{0,0,0,0,0}},{{0,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0}}}}");
  }

  @Test
  public void testHodgeDualRankGreater1() {
    // --- rank-2 tensor, n=2 (2D) → scalar (dualRank = 2-2 = 0) ---
    // HodgeDual({{a,b},{c,d}}) = (1/2!) * Sum_{i,j} M[i,j]*ε[i,j]
    // = (1/2)*(b*ε[1,2] + c*ε[2,1]) = (1/2)*(b - c)
    check("HodgeDual({{a, b}, {c, d}})", //
        "1/2*(b-c)");

    // Antisymmetric 2×2 matrix: off-diagonal = ±1
    check("HodgeDual({{0, 1}, {-1, 0}})", //
        "1");

    // --- rank-2 tensor, n=3 (3D) → vector (dualRank = 3-2 = 1) ---
    // HodgeDual[M][k] = (1/2) * Sum_{i,j} M[i,j]*ε[i,j,k]
    // For M = HodgeDual[{1,0,0}] = {{0,0,0},{0,0,1},{0,-1,0}},
    // the double dual should recover {1,0,0}:
    check("Normal@HodgeDual({{0,0,0},{0,0,1},{0,-1,0}})", //
        "{1,0,0}");

    // Double dual in 3D: HodgeDual[HodgeDual[v]] == v for a rank-1 vector
    check("Normal@HodgeDual(Normal@HodgeDual({a, b, c}))", //
        "{a,b,c}");

    // Fully antisymmetric rank-2 tensor in 3D: M = {{0,c,-b},{-c,0,a},{b,-a,0}}
    // HodgeDual recovers {a, b, c}
    check("Normal@HodgeDual({{0,c,-b},{-c,0,a},{b,-a,0}})", //
        "{a,b,c}");

    // --- rank-2 tensor, n=4 (4D) → rank-2 tensor (dualRank = 4-2 = 2) ---
    // Use a simple rank-2 antisymmetric tensor with one non-zero pair: M[1,2]=1, M[2,1]=-1
    // HodgeDual[M][j1,j2] = (1/2)*Sum_{i1,i2} M[i1,i2]*ε[i1,i2,j1,j2]
    // non-zero contributions: M[1,2]*ε[1,2,j1,j2] + M[2,1]*ε[2,1,j1,j2]
    // = (1/2)*(ε[1,2,j1,j2] - ε[2,1,j1,j2]) = ε[1,2,j1,j2]
    // Only non-zero for (j1,j2) = (3,4) → ε[1,2,3,4]=1 and (4,3) → ε[1,2,4,3]=-1
    check("Normal@HodgeDual({{0,1,0,0},{-1,0,0,0},{0,0,0,0},{0,0,0,0}})", //
        "{{0,0,0,0},{0,0,0,0},{0,0,0,1},{0,0,-1,0}}");

    // --- HodgeDual[tensor, dim] (2-argument form) ---
    // All slots have dimension 3 → same as 1-argument form
    check("Normal@HodgeDual({a, b, c}, 3)", //
        "{{0,c,-b},{-c,0,a},{b,-a,0}}");

    // Rank-2 tensor in 2D with explicit dim=2 → same as 1-argument form
    check("HodgeDual({{a, b}, {c, d}}, 2)", //
        "1/2*(b-c)");

    // --- HodgeDual(tensor, dim, slots) (3-argument form) ---
    // Dualize only slot 1 of {a,b,c} (dim=3, rank being dualized=1) → same as rank-1 result
    check("Normal@HodgeDual({a, b, c}, 3, {1})", //
        "{{0,c,-b},{-c,0,a},{b,-a,0}}");

    // Dualize both slots of a 3×3 matrix with dim=3 → same as 1-argument form (rank-2 dual)
    check("Normal@HodgeDual({{0,c,-b},{-c,0,a},{b,-a,0}}, 3, {1,2})", //
        "{a,b,c}");

    // Rank-2 tensor in 2D with explicit slots {1,2} → scalar
    check("HodgeDual({{a, b}, {c, d}}, 2, {1, 2})", //
        "1/2*(b-c)");
  }

  @Test
  public void testHodgeDualSpectatorSlots() {
    // -----------------------------------------------------------------------
    // buildSpectatorResult for-loop is exercised whenever there is at least
    // one spectator slot (a dimension that does NOT equal the dualized dim).
    // -----------------------------------------------------------------------

    // --- Shape {2, 3}: row index (dim=2) is spectator, column (dim=3) is contracted ---
    // HodgeDual[M, 3] with M of shape {2,3}:
    // r=1, dualRank=2, spectatorDims={2} → for-loop iterates k=0,1 (2 spectator entries)
    //
    // For each spectator row s: HodgeDual of that row is computed as usual.
    // Row {a,b,c} → {{0,c,-b},{-c,0,a},{b,-a,0}}
    // Row {d,e,f} → {{0,f,-e},{-f,0,d},{e,-d,0}}
    // Result shape {3,3,2}: result[j0][j1][s] = HodgeDual_row_s[j0][j1]
    check("Normal@HodgeDual({{a,b,c},{d,e,f}}, 3)", //
        "{{{0,0},{c,f},{-b,-e}},{{-c,-f},{0,0},{a,d}},{{b,e},{-a,-d},{0,0}}}");

    // Same case with explicit numeric values so the for-loop output is easier to trace
    // Row {1,2,3} → HodgeDual = {{0,3,-2},{-3,0,1},{2,-1,0}}
    // Row {4,5,6} → HodgeDual = {{0,6,-5},{-6,0,4},{5,-4,0}}
    check("Normal@HodgeDual({{1,2,3},{4,5,6}}, 3)", //
        "{{{0,0},{3,6},{-2,-5}},{{-3,-6},{0,0},{1,4}},{{2,5},{-1,-4},{0,0}}}");

    // --- 3-argument form: explicit slot {2} selects column index (1-based) ---
    // Should give identical result to the 2-argument form above
    check("Normal@HodgeDual({{a,b,c},{d,e,f}}, 3, {2})", //
        "{{{0,0},{c,f},{-b,-e}},{{-c,-f},{0,0},{a,d}},{{b,e},{-a,-d},{0,0}}}");

    // --- Shape {3, 2}: row index (dim=3) is spectator, column (dim=2) is contracted ---
    // HodgeDual[M, 2] with M of shape {3,2}:
    // r=1, dualRank=1, spectatorDims={3} → for-loop iterates k=0,1,2 (3 spectator entries)
    //
    // For spectator row s={a,b}: HodgeDual[{a,b}][j] = sum_i {a,b}[i]*ε2[i,j]
    // ε2 = {{0,1},{-1,0}}: result[j,s] = -b (j=1), a (j=2)
    // Rows: {a,b} → {-b, a}; {c,d} → {-d, c}; {e,f} → {-f, e}
    // Result shape {2,3}: result[j][s] where j=dual index, s=spectator row
    check("Normal@HodgeDual({{a,b},{c,d},{e,f}}, 2)", //
        "{{-b,-d,-f},{a,c,e}}");

    // Same with explicit slot {2}
    check("Normal@HodgeDual({{a,b},{c,d},{e,f}}, 2, {2})", //
        "{{-b,-d,-f},{a,c,e}}");

    // --- Two spectator dimensions: shape {2,3,2}, contract only middle slot (dim=3) ---
    // HodgeDual[T, 3, {2}]:
    // r=1, dualRank=2, spectatorDims={2,2}
    // for-loop in buildSpectatorResult runs twice (once for each spectator dimension)
    //
    // T[s0, i, s1] where s0∈{1,2}, i∈{1,2,3}, s1∈{1,2}
    // Use T = Array[t, {2, 3, 2}] so T[s0,i,s1] = t[s0,i,s1]
    // Result[j0, j1, s0, s1] = sum_i T[s0,i,s1] * epsilon[i, j0, j1]
    // = HodgeDual(T[s0, :, s1])[j0, j1]
    //
    // For a concrete example: T[1,:,1]={1,0,0}, T[1,:,2]={0,1,0},
    // T[2,:,1]={0,0,1}, T[2,:,2]={1,1,1}
    // Using Array form for simplicity — just verify shape & a few entries
    check(
        "m3 = {{{1,0},{0,1},{0,0}},{{0,0},{0,1},{1,0}}}; res = Normal@HodgeDual(m3, 3, {2}); Dimensions(res)", //
        "{3,3,2,2}");

    // Verify one specific entry: result[1,2,1,1]
    // = sum_i m3[1,i,1] * epsilon[i,1,2]
    // m3[1,:,1] = {1,0,0} → HodgeDual[{1,0,0}][1,2] = ε[1,1,2]=0 + 0 + 0 = 0
    // Actually HodgeDual[{1,0,0}][j0,j1] = ε[1,j0,j1]
    // [1,2]: ε[1,1,2]=0; [1,3]: ε[1,1,3]=0; [2,1]: ε[1,2,1]=-1; [3,1]: ε[1,3,1]=0;
    // [2,3]: ε[1,2,3]=1; [3,2]: ε[1,3,2]=-1 → result[2,3,1,1]=1
    check("m3 = {{{1,0},{0,1},{0,0}},{{0,0},{0,1},{1,0}}}; Normal@HodgeDual(m3, 3, {2})[[2,3,1,1]]", //
        "1");
    check("m3 = {{{1,0},{0,1},{0,0}},{{0,0},{0,1},{1,0}}}; Normal@HodgeDual(m3, 3, {2})", //
        "{{{{0,0},{0,0}},{{0,0},{1,0}},{{0,-1},{0,-1}}},{{{0,0},{-1,0}},{{0,0},{0,0}},{{1,\n" //
            + "0},{0,0}}},{{{0,1},{0,1}},{{-1,0},{0,0}},{{0,0},{0,0}}}}");
  }

  @Test
  public void testLeviCivitaTensor() {
    // if (Config.EXPENSIVE_JUNIT_TESTS) {
    check("LeviCivitaTensor(7)", //
        "SparseArray(Number of elements: 5040 Dimensions: {7,7,7,7,7,7,7} Default value: 0)");
    // }
    check("LeviCivitaTensor(1)//Normal", //
        "{1}");
    check("{x,y,z}.Normal(LeviCivitaTensor(3))  ", //
        "{{0,z,-y},{-z,0,x},{y,-x,0}}");
    check("Array(Signature({##})&,{3,3,3})", //
        "{{{0,0,0},{0,0,1},{0,-1,0}},{{0,0,-1},{0,0,0},{1,0,0}},{{0,1,0},{-1,0,0},{0,0,0}}}");
    check("LeviCivitaTensor(3)", //
        "SparseArray(Number of elements: 6 Dimensions: {3,3,3} Default value: 0)");
    check("LeviCivitaTensor(3,List)", //
        "{{{0,0,0},{0,0,1},{0,-1,0}},{{0,0,-1},{0,0,0},{1,0,0}},{{0,1,0},{-1,0,0},{0,0,0}}}");
    check("LeviCivitaTensor(4) // Normal", //
        "{{{{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0}},{{0,0,0,0},{0,0,0,0},{0,0,0,1},{0,0,-\n"
            + "1,0}},{{0,0,0,0},{0,0,0,-1},{0,0,0,0},{0,1,0,0}},{{0,0,0,0},{0,0,1,0},{0,-1,0,0},{\n"
            + "0,0,0,0}}},{{{0,0,0,0},{0,0,0,0},{0,0,0,-1},{0,0,1,0}},{{0,0,0,0},{0,0,0,0},{0,0,\n"
            + "0,0},{0,0,0,0}},{{0,0,0,1},{0,0,0,0},{0,0,0,0},{-1,0,0,0}},{{0,0,-1,0},{0,0,0,0},{\n"
            + "1,0,0,0},{0,0,0,0}}},{{{0,0,0,0},{0,0,0,1},{0,0,0,0},{0,-1,0,0}},{{0,0,0,-1},{0,\n"
            + "0,0,0},{0,0,0,0},{1,0,0,0}},{{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0}},{{0,1,0,0},{-\n"
            + "1,0,0,0},{0,0,0,0},{0,0,0,0}}},{{{0,0,0,0},{0,0,-1,0},{0,1,0,0},{0,0,0,0}},{{0,0,\n"
            + "1,0},{0,0,0,0},{-1,0,0,0},{0,0,0,0}},{{0,-1,0,0},{1,0,0,0},{0,0,0,0},{0,0,0,0}},{{\n"
            + "0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0}}}}");
  }

  @Test
  public void testTensorDimensions() {
    check("A=ArraySymbol(a, {2, 3, 4});TensorDimensions(A)", //
        "{2,3,4}");
    // TODO
    // check("TensorDimensions(A+2)", //
    // "{2,3,4}");
    // check("TensorDimensions(A*3)", //
    // "{2,3,4}");
    // check("TensorDimensions(A^3)", //
    // "{2,3,4}");
    check("A=Array(a, {2, 3, 4});TensorDimensions(A)", //
        "{2,3,4}");
    check("TensorDimensions({{1,2},{3,4},{a,b},{c,d}})", //
        "{4,2}");
    check("m = SparseArray({{1, 2, 3} -> a}, {2, 3, 4});TensorDimensions(m)", //
        "{2,3,4}");
  }

  @Test
  public void testTensorRank() {
    check("TensorRank(3.14)", //
        "0");
    check("TensorRank(Pi+E)", //
        "0");
    check("TensorRank({})", //
        "1");
    check("TensorRank({{}})", //
        "2");
    check("A=Array(a, {2, 3, 4});TensorRank(A)", //
        "3");
    check("TensorRank({{1,2},{3,4},{a,b},{c,d}})", //
        "2");
    check("m = SparseArray({{1, 2, 3} -> a}, {2, 3, 4});TensorRank(m)", //
        "3");
  }

  @Test
  public void testTensorSymmetry() {
    check("TensorSymmetry({{a,b,c,d}, {b,e,f,g}, {c,f,h,i},{d,g,i,j}})", //
        "Symmetric({1,2})");
    check("TensorSymmetry({{0, a, b}, {-a, 0, c}, {-b, -c, 0}})", //
        "Antisymmetric({1,2})");
    check("TensorSymmetry({{a}})", //
        "Symmetric({1,2})");
    check("TensorSymmetry({{0}})", //
        "ZeroSymmetric({})");
    check("TensorSymmetry({{0,0}, {0,0}})", //
        "ZeroSymmetric({})");
    check("TensorSymmetry({{a,b}, {b,c}})", //
        "Symmetric({1,2})");
  }

  @Test
  public void testKroneckerProduct() {
    // https://rosettacode.org/wiki/Kronecker_product
    check("a = {{0, 1, 0},{1, 1, 1},{0, 1, 0}}; b = {{1, 1, 1, 1},{1, 0, 0, 1},{1, 1, 1, 1}};", //
        "");
    check("KroneckerProduct(a,b)", //
        "{{0,0,0,0,1,1,1,1,0,0,0,0}," //
            + "{0,0,0,0,1,0,0,1,0,0,0,0}," //
            + "{0,0,0,0,1,1,1,1,0,0,0,0},{\n" //
            + "1,1,1,1,1,1,1,1,1,1,1,1}," //
            + "{1,0,0,1,1,0,0,1,1,0,0,1}," //
            + "{1,1,1,1,1,1,1,1,1,1,1,1},{0,\n" //
            + "0,0,0,1,1,1,1,0,0,0,0}," //
            + "{0,0,0,0,1,0,0,1,0,0,0,0}," //
            + "{0,0,0,0,1,1,1,1,0,0,0,0}}");


    check("av = {1, 2, 3}; bv = {4, 5};", //
        "");
    check("KroneckerProduct(av,bv)", //
        "{{4,5},{8,10},{12,15}}");

    check("am = {{1, 2}, {3, 4}}; bm = {{5, 6}, {7, 8}, {9, 10}};", //
        "");
    check("KroneckerProduct(am,bm)", //
        "{{5,6,10,12},{7,8,14,16},{9,10,18,20},{15,18,20,24},{21,24,28,32},{27,30,36,40}}");

    // TODO improve FlattenArray / message ArrayFlatten: Function (rank != 2) not implemented.
    check("am = {{{1, 2}, {3, 4}}}; bm = {{{5, 6}, {7, 8}, {9, 10}}};", //
        "");
    check("KroneckerProduct(am,bm)", //
        "KroneckerProduct({{{{{{5,6},{7,8},{9,10}}},{{{10,12},{14,16},{18,20}}}},{{{{15,\n" //
            + "18},{21,24},{27,30}}},{{{20,24},{28,32},{36,40}}}}}})");
    // check("KroneckerProduct(am,bm)", //
    // "{{5,6,10,12},{7,8,14,16},{9,10,18,20},{15,18,20,24},{21,24,28,32},{27,30,36,40}}");

    check("ta = {{0, 1}, {-1, 0}}; tb = {{1, 2}, {3, 4}};", //
        "");
    check("KroneckerProduct(ta, tb)", //
        "{{0,0,1,2},{0,0,3,4},{-1,-2,0,0},{-3,-4,0,0}}");

    check("ta = {{1, 2}, {3, 4}, {5, 6}}; tb = {{7, 8}, {9, 0}};", //
        "");
    check("KroneckerProduct(ta, tb) // MatrixForm", //
        "{{7,8,14,16},\n" //
            + " {9,0,18,0},\n" //
            + " {21,24,28,32},\n" //
            + " {27,0,36,0},\n" //
            + " {35,40,42,48},\n" //
            + " {45,0,54,0}}");

    check("ta = {{1,4,-7}, {-2,3,3}}; tb = {{8,-9,-6,5},{1,-3,-4,7},{2,8,-8,-3},{1,2,-5,-1}};", //
        "");
    check("KroneckerProduct(ta, tb) // MatrixForm", //
        "{{8,-9,-6,5,32,-36,-24,20,-56,63,42,-35},\n" //
            + " {1,-3,-4,7,4,-12,-16,28,-7,21,28,-49},\n" //
            + " {2,8,-8,-3,8,32,-32,-12,-14,-56,56,21},\n" //
            + " {1,2,-5,-1,4,8,-20,-4,-7,-14,35,7},\n" //
            + " {-16,18,12,-10,24,-27,-18,15,24,-27,-18,15},\n" //
            + " {-2,6,8,-14,3,-9,-12,21,3,-9,-12,21},\n" //
            + " {-4,-16,16,6,6,24,-24,-9,6,24,-24,-9},\n" //
            + " {-2,-4,10,2,3,6,-15,-3,3,6,-15,-3}}");

    check("ta = {{1,2}, {3,4}}; tb = {{0,5}, {6,7}};", //
        "");
    check("KroneckerProduct(ta, tb)", //
        "{{0,5,0,10},{6,7,12,14},{0,15,0,20},{18,21,24,28}}");

    check("a = {{a11, a12}, {a21, a22}}; b = {{b11, b12}, {b21, b22}};", //
        "");
    check("KroneckerProduct(a, b)", //
        "{{a11*b11,a11*b12,a12*b11,a12*b12},{a11*b21,a11*b22,a12*b21,a12*b22},{a21*b11,a21*b12,a22*b11,a22*b12},{a21*b21,a21*b22,a22*b21,a22*b22}}");
  }

  @Test
  public void testTensorProduct() {

    check("ta = {{1,4,-7}, {-2,3,3}}; tb = {{8,-9,-6,5},{1,-3,-4,7},{2,8,-8,-3},{1,2,-5,-1}};", //
        "");
    check("TensorProduct(ta, tb)  ", //
        "{{{{8,-9,-6,5},{1,-3,-4,7},{2,8,-8,-3},{1,2,-5,-1}},{{32,-36,-24,20},{4,-12,-16,\n" //
            + "28},{8,32,-32,-12},{4,8,-20,-4}},{{-56,63,42,-35},{-7,21,28,-49},{-14,-56,56,21},{-\n" //
            + "7,-14,35,7}}},{{{-16,18,12,-10},{-2,6,8,-14},{-4,-16,16,6},{-2,-4,10,2}},{{24,-\n" //
            + "27,-18,15},{3,-9,-12,21},{6,24,-24,-9},{3,6,-15,-3}},{{24,-27,-18,15},{3,-9,-12,\n" //
            + "21},{6,24,-24,-9},{3,6,-15,-3}}}}");

    check(
        "TensorProduct({{{1,2,3},{4,5,6},{7,8,9}},{{2,0,0},{0,3,0},{0,0,1}}},{{2,1,4},{0,3,0},{0,0,1}})", //
        "{{{{{2,1,4},{0,3,0},{0,0,1}},{{4,2,8},{0,6,0},{0,0,2}},{{6,3,12},{0,9,0},{0,0,3}}},{{{\n"
            + "8,4,16},{0,12,0},{0,0,4}},{{10,5,20},{0,15,0},{0,0,5}},{{12,6,24},{0,18,0},{0,0,\n"
            + "6}}},{{{14,7,28},{0,21,0},{0,0,7}},{{16,8,32},{0,24,0},{0,0,8}},{{18,9,36},{0,27,\n"
            + "0},{0,0,9}}}},{{{{4,2,8},{0,6,0},{0,0,2}},{{0,0,0},{0,0,0},{0,0,0}},{{0,0,0},{0,\n"
            + "0,0},{0,0,0}}},{{{0,0,0},{0,0,0},{0,0,0}},{{6,3,12},{0,9,0},{0,0,3}},{{0,0,0},{0,\n"
            + "0,0},{0,0,0}}},{{{0,0,0},{0,0,0},{0,0,0}},{{0,0,0},{0,0,0},{0,0,0}},{{2,1,4},{0,\n"
            + "3,0},{0,0,1}}}}}");

    // Unicode symbol \[TensorProduct] - \uF3DA looks nearly the same as \[CircleTimes] - \u2997
    check("TensorProduct(a + 2*b, c)", //
        "(a+2*b)\uF3DAc");

    check("TensorProduct({2, 3}, {{a, b}, {c, d}} )", //
        "{{{2*a,2*b},{2*c,2*d}},{{3*a,3*b},{3*c,3*d}}}");
    check("TensorProduct({{{2*a,2*b},{2*c,2*d}},{{3*a,3*b},{3*c,3*d}}}, {x, y})", //
        "{{{{2*a*x,2*a*y},{2*b*x,2*b*y}},{{2*c*x,2*c*y},{2*d*x,2*d*y}}},{{{3*a*x,3*a*y},{\n"
            + "3*b*x,3*b*y}},{{3*c*x,3*c*y},{3*d*x,3*d*y}}}}");
    check("TensorProduct({2, 3}, {{a, b}, {c, d}}, {x, y})", //
        "{{{{2*a*x,2*a*y},{2*b*x,2*b*y}},{{2*c*x,2*c*y},{2*d*x,2*d*y}}},{{{3*a*x,3*a*y},{\n"
            + "3*b*x,3*b*y}},{{3*c*x,3*c*y},{3*d*x,3*d*y}}}}");
    check("TensorProduct({a, b}, {x, y})", //
        "{{a*x,a*y},{b*x,b*y}}");
    check("TensorProduct({x,y}, {a,b})", //
        "{{a*x,b*x},{a*y,b*y}}");

    check(
        "TensorProduct({{3, 8, 2, 7, 7}, {0, 3, 9, 9, 8}}, {{8, 10, 4, 9}, {5, 6, 7, 4}, {2, 3, 2, 9}})", //
        "{{{{24,30,12,27},{15,18,21,12},{6,9,6,27}},{{64,80,32,72},{40,48,56,32},{16,24,\n"
            + "16,72}},{{16,20,8,18},{10,12,14,8},{4,6,4,18}},{{56,70,28,63},{35,42,49,28},{14,\n"
            + "21,14,63}},{{56,70,28,63},{35,42,49,28},{14,21,14,63}}},{{{0,0,0,0},{0,0,0,0},{0,\n"
            + "0,0,0}},{{24,30,12,27},{15,18,21,12},{6,9,6,27}},{{72,90,36,81},{45,54,63,36},{\n"
            + "18,27,18,81}},{{72,90,36,81},{45,54,63,36},{18,27,18,81}},{{64,80,32,72},{40,48,\n"
            + "56,32},{16,24,16,72}}}}");
  }



  @Test
  public void testRotationTransform() {
    check("TransformationFunction(#2)[Sequence()]", //
        "TransformationFunction(#2)[]"); //

    check("RotationTransform(Pi).TranslationTransform({1, -1})", //
        "TransformationFunction(\n" //
            + "{{-1,0,-1},\n" //
            + " {0,-1,1},\n" //
            + " {0,0,1}})"); //

    check("TranslationTransform({1, -1}).RotationTransform(Pi)", //
        "TransformationFunction(\n" //
            + "{{-1,0,1},\n" //
            + " {0,-1,-1},\n" //
            + " {0,0,1}})"); //

    check("RotationTransform(alpha)", //
        "TransformationFunction({{Cos(alpha),-Sin(alpha),0},{Sin(alpha),Cos(alpha),0},{0,\n" //
            + "0,1}})");
  }

  @Test
  public void testScalingTransform() {
    check("ScalingTransform({a, b, c})", //
        "TransformationFunction(\n" //
            + "{{a,0,0,0},\n" //
            + " {0,b,0,0},\n" //
            + " {0,0,c,0},\n" //
            + " {0,0,0,1}})");

    check("ScalingTransform(s,{1,1})", //
        "TransformationFunction({{1/2*(1+s),1/2*(-1+s),0},{1/2*(-1+s),1/2*(1+s),0},{0,0,1}})");
  }

  @Test
  public void testShearingTransform() {
    check("ShearingTransform(\\[Theta], {1, 0}, {0, 1})", //
        "TransformationFunction({{1,Tan(θ),0},{0,1,0},{0,0,1}})");
  }

  @Test
  public void testTransformationFunction() {
    check("r = RotationTransform(\\[Theta]);", //
        "");
    check("r({x, y})", //
        "{x*Cos(θ)-y*Sin(θ),y*Cos(θ)+x*Sin(θ)}");
  }

  @Test
  public void testTranslationTransform() {

    check("t = TranslationTransform({x0, y0})", //
        "TransformationFunction(\n" //
            + "{{1,0,x0},\n" //
            + " {0,1,y0},\n" //
            + " {0,0,1}})");
    check("t({x, y})", //
        "{x+x0,y+y0}");


    check("TranslationTransform({a,b,c,d})", //
        "TransformationFunction(\n" //
            + "{{1,0,0,0,a},\n" //
            + " {0,1,0,0,b},\n" //
            + " {0,0,1,0,c},\n" //
            + " {0,0,0,1,d},\n" //
            + " {0,0,0,0,1}})");

    check("TranslationTransform({1, 2})", //
        "TransformationFunction(\n" //
            + "{{1,0,1},\n" //
            + " {0,1,2},\n" //
            + " {0,0,1}})"); //
  }

  @Test
  public void testSymmetrize() {
    check("Symmetrize({{a, b}, {c, d}})", //
        "{{a,1/2*(b+c)},{1/2*(b+c),d}}");
  }

  @Test
  public void testSymmetrizeTwoArgument() {
    // Symmetrizing a matrix with specific symmetric and antisymmetric bounds
    check("Symmetrize({{a, b}, {c, d}}, Symmetric({1, 2}))", "{{a,1/2*(b+c)},{1/2*(b+c),d}}");

    // Antisymmetrizing causes the diagonal to collapse to 0
    check("Symmetrize({{a, b}, {c, d}}, Antisymmetric({1, 2}))", "{{0,1/2*(b-c)},{1/2*(-b+c),0}}");
  }

  @Test
  public void testSymmetrize3DTensorPartial() {
    // Only swap the 1st and 3rd axes of a 2x2x2 tensor
    String tensor3D = "{{{a, b}, {c, d}}, {{e, f}, {g, h}}}";

    check("Symmetrize(" + tensor3D + ", Symmetric({1, 3}))",
        "{{{a,1/2*(b+e)},{c,1/2*(d+g)}},{{1/2*(b+e),f},{1/2*(d+g),h}}}");

    check("Symmetrize(" + tensor3D + ", Antisymmetric({1, 3}))",
        "{{{0,1/2*(b-e)},{0,1/2*(d-g)}},{{1/2*(-b+e),0},{1/2*(-d+g),0}}}");
  }

  @Test
  public void testSymmetrizeMultipleFolds() {
    // prints Symmetrize: Symmetry specification {Symmetric({1,2}),Antisymmetric({1,2})} is only
    // compatible with the zero tensor.
    check("Symmetrize({{a, b}, {c, d}}, {Symmetric({1, 2}), Antisymmetric({1, 2})})",
        "{{0,0},{0,0}}");
  }


  /** The JUnit setup method */
  @Override
  public void setUp() {
    super.setUp();
    Config.MAX_AST_SIZE = 1000000;
    EvalEngine.get().setIterationLimit(50000);
  }

}
