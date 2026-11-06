package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** Mixed cases whose expected values were checked against a reference system. */
public class GroundTruthCasesTest extends ExprEvaluatorTestCase {

  @Test
  public void testInsertAssociationExistingKey() {
    // an existing key is removed and the rule goes to the position
    check("Insert(<|u -> 7, v -> 8, w -> 9|>, u -> 0, 4)", //
        "<|v->8,w->9,u->0|>");
    check("Insert(<|u -> 7, v -> 8, w -> 9|>, w -> 0, 2)", //
        "<|u->7,w->0,v->8|>");
    check("Insert(<|u -> 7, v -> 8|>, {s -> 1, u -> 2, t -> 3}, 3)", //
        "<|v->8,s->1,u->2,t->3|>");
    // after the last rule
    check("Insert(<|u -> 7, v -> 8|>, t -> 1, -1)", //
        "<|u->7,v->8,t->1|>");
    // the keys are found after an insertion
    check("With({g = Insert(<|u -> 7, v -> 8|>, t -> 1, 2)}, {g(t), g(v), KeyExistsQ(g, t)})", //
        "{1,8,True}");
  }

  @Test
  public void testSolveIntegersMultiplicityWithConstraint() {
    check("Solve((y + 3)^3*(y - 2) == 0 && y < 0, y, Integers)", //
        "{{y->-3},{y->-3},{y->-3}}");
    check("Solve(y^2*(y - 4) == 0 && y >= 0, y, Integers)", //
        "{{y->0},{y->0},{y->4}}");
  }

  @Test
  public void testParentDirectoryOfFile() {
    check("ParentDirectory(File(\"/opt/data/raw\"))", //
        "/opt/data");
    check("StringQ(ParentDirectory(File(\"/opt/data/raw\"), 2))", //
        "True");
  }

  @Test
  public void testFindDivisionsInexactIntegers() {
    check("FindDivisions({-1.5, 18.2}, 4)", //
        "{-5,0,5,10,15,20}");
    // points which are not integers stay machine numbers
    check("FindDivisions({0.1, 0.45}, 3)", //
        "{0.0,0.2,0.4,0.6}");
  }

  @Test
  public void testGaussianOrthogonalMatrixDistributionDefaultScale() {
    check("GaussianOrthogonalMatrixDistribution(3)", //
        "GaussianOrthogonalMatrixDistribution(1,3)");
    check("GaussianOrthogonalMatrixDistribution(2, 4)", //
        "GaussianOrthogonalMatrixDistribution(2,4)");
  }

  @Test
  public void testSmoothKernelUnitSupport() {
    // the triweight and semicircle kernels live on [-bw, bw]
    check("CDF(SmoothKernelDistribution({0.}, 1., \"Triweight\"), 1.2)", //
        "1.0");
    check("CDF(SmoothKernelDistribution({0.}, 1., \"SemiCircle\"), 1.2)", //
        "1.0");
    check("PDF(SmoothKernelDistribution({0.}, 1., \"SemiCircle\"), 1.5)", //
        "0.0");
  }

  @Test
  public void testFactorSquareFreeListModulusFactors() {
    check("FactorSquareFreeList(z^3*(z + 2)^2, Modulus -> 7)", //
        "{{1,1},{z,3},{2+z,2}}");
    check("FactorSquareFreeList((z + 1)^2*(z + 4)^2*(z + 2), Modulus -> 7)", //
        "{{1,1},{1+z,2},{2+z,1},{4+z,2}}");
  }

  @Test
  public void testWithPatternShadowsInRule() {
    check("With({b = 5}, Hold(b_ -> b + 1))", //
        "Hold(b_->b+1)");
    check("With({b = 5}, Hold(f(b_) = b))", //
        "Hold(f(b_)=b)");
    // a variable which is no pattern variable is substituted
    check("With({b = 5}, Hold(c_ -> b))", //
        "Hold(c$_->5)");
  }

  @Test
  public void testLinearModelFitWeights() {
    // the weighted sums give the slope 21/11 and the intercept 9/11
    check("Rationalize(LinearModelFit({{0, 1}, {1, 2}, {2, 5}}, t, t, Weights -> {2, 1, 1})"
        + "[\"BestFitParameters\"], 10^-12)", //
        "{9/11,21/11}");
    check("Rationalize(LinearModelFit({{0, 1}, {1, 2}, {2, 5}}, t, t, Weights -> {2, 1, 1})[2],"
        + " 10^-12)", //
        "51/11");
    // non-positive weights
    check("LinearModelFit({{0, 1}, {1, 2}, {2, 5}}, t, t, Weights -> {2, 0, 1})", //
        "LinearModelFit({{0,1},{1,2},{2,5}},t,t,Weights->{2,0,1})");
  }

  @Test
  public void testDivideSidesSymbolicDivisor() {
    check("DivideSides(p*q == r, p)", //
        "Piecewise({{q==r/p,p!=0}},p*q==r)");
    check("DivideSides(p*q == r, 3)", //
        "1/3*p*q==r/3");
    check("DivideSides(p*q == r, p, Assumptions -> p > 1)", //
        "q==r/p");
  }

  @Test
  public void testModularInverseGaussianModInteger() {
    check("ModularInverse(1 + 2*I, 5)", //
        "ModularInverse(1+I*2,5)");
    // (3+I)*(1+2*I) is 1+7*I
    check("ModularInverse(3 + I, 7)", //
        "1+I*2");
    check("Mod((2 + 3*I)*ModularInverse(2 + 3*I, 5), 5)", //
        "1");
  }

  @Test
  public void testDegreeTrigCofunctions() {
    check("SinDegrees(110)", //
        "CosDegrees(20)");
    check("CosDegrees(70)", //
        "SinDegrees(20)");
    check("SinDegrees(20)", //
        "SinDegrees(20)");
    check("CosDegrees(90 + y)", //
        "-SinDegrees(y)");
    check("ArcSinDegrees(2*I)", //
        "(I*180*ArcSinh(2))/Pi");
  }

  @Test
  public void testBioSequencePeptideWildcardCircularReverse() {
    check("StringCount(BioSequence(\"Peptide\", \"AXWX\"), \"X\")", //
        "4");
    check("StringCount(BioSequence(\"Peptide\", \"AXWX\"), \"W\")", //
        "1");
    check("StringReverse(BioSequence(\"CircularRNA\", \"GGAU\"))", //
        "BioSequence(CircularRNA,UAGG)");
  }
}
