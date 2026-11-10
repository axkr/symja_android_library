package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** Regularized spectral deconvolution of vectors and matrices. */
public class ListDeconvolveTest extends ExprEvaluatorTestCase {

  @Test
  public void testSymmetricKernelDampedLeastSquares() {
    // a symmetric kernel is diagonalized by the cosine transform, the regularization parameter is
    // chosen by generalized cross validation
    check("Max(Abs(ListDeconvolve({1/3., 1/3., 1/3.}, {1., 2., 4., 3., 1.}) - "
        + "{-0.018649687947053906, 2.6438560404579636, 4.646802405940675, 3.682627891913726, "
        + "0.045363349634687244})) < 10^-12", //
        "True");
    check("Max(Abs(ListDeconvolve({1, 2, 1}, {1, 2, 3, 4}) - "
        + "{0.21040304527329096, 0.45842666147640565, 0.7915733385235943, 1.039596954726709})) "
        + "< 10^-12", //
        "True");
    // the identity kernel: the parameter is 1, the linear part of the data is halved
    check("Max(Abs(ListDeconvolve({0, 1, 0}, {1., 2., 3.}) - {1.5, 2., 2.5})) < 10^-12", //
        "True");
    // exact numbers give machine numbers
    check("MachineNumberQ /@ ListDeconvolve({1, 2, 1}, {1, 2, 3, 4})", //
        "{True,True,True,True}");
  }

  @Test
  public void testMethods() {
    check("Max(Abs(ListDeconvolve({1/3., 1/3., 1/3.}, {1., 2., 4., 3., 1.}, Method -> \"Wiener\") - "
        + "{-1.6571628213599867, 3.671058757546204, 4.09356090917753, 3.8823098333618655, "
        + "0.9013224301852774})) < 10^-12", //
        "True");
    check("Max(Abs(ListDeconvolve({1/3., 1/3., 1/3.}, {1., 2., 4., 3., 1.}, "
        + "Method -> {\"Tikhonov\", 0.5}) - {0.35696694388147354, 2.0823705360262763, "
        + "3.1648780487804875, 2.666897756656649, 0.5288867146551122})) < 10^-12", //
        "True");
    // without regularization the identity kernel returns the data
    check("ListDeconvolve({0, 1, 0}, {2., 7., 1., 8.}, Method -> {\"Tikhonov\", 0})", //
        "{2.0,7.0,1.0,8.0}");
    // the noise to signal ratio of the Wiener filter
    check("Max(Abs(ListDeconvolve({1, 2, 5}, {3, 1, 4, 1, 5, 9, 2, 6}, Method -> {\"Wiener\", 0.1}) - "
        + "{-0.07686408412673099, 0.8309406591325416, -0.2701847898648808, 0.36694617064393165, "
        + "1.6284481276497773, -0.09978861709276224, 1.0257489328904144, 0.46370835895803647})) "
        + "< 10^-12", //
        "True");
    // methods which are not supported stay unevaluated
    check("ListDeconvolve({1, 2, 1}, {1, 2, 3, 4}, Method -> \"SteepestDescent\")", //
        "ListDeconvolve({1,2,1},{1,2,3,4},Method->SteepestDescent)");
  }

  @Test
  public void testTruncatedDecomposition() {
    // the truncation chosen by generalized cross validation drops every component here
    check("ListDeconvolve({1, 2, 5}, {3, 1, 4, 1, 5, 9, 2, 6}, Method -> \"TSVD\")", //
        "{0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0}");
    // a symmetric kernel: the two smallest of the eight components are dropped
    check("Max(Abs(ListDeconvolve({1, 2, 1}, {3, 1, 4, 1, 5, 9, 2, 6}, Method -> \"TSVD\") - "
        + "{0.16941315498258916, 1.5109135597375438, 0.17115343313431172, -0.19741114281090846, "
        + "2.152678095777277, 1.9504825750430395, 0.49245043208510364, 1.5003198920510417})) "
        + "< 10^-12", //
        "True");
    // a given truncation keeps the components above it, divided by the modulus of the eigenvalue
    check("Max(Abs(ListDeconvolve({1, 2, 5}, {3, 1, 4, 1, 5, 9, 2, 6}, Method -> {\"TSVD\", 3.}) - "
        + "{0.36049476696677724, -0.100064514583753, 0.791635346717741, -0.2109842597937624, "
        + "0.6443620307832015, 1.5917247112087216, -0.23399214446772015, 1.0318240631687936})) "
        + "< 10^-12", //
        "True");
    check("ListDeconvolve({1, 2, 5}, {3, 1, 4, 1, 5, 9, 2, 6}, Method -> {\"TSVD\", 7.}) // Chop", //
        "{0.484375,0.484375,0.484375,0.484375,0.484375,0.484375,0.484375,0.484375}");
  }

  @Test
  public void testShortData() {
    // data shorter than 3 in a dimension is continued by its edge before
    check("Max(Abs(ListDeconvolve({{0, 1}, {1, 0}}, {{1., 2.}, {3., 4.}}, "
        + "Method -> {\"DampedLS\", 0.5}) - {{0.25, 0.5833333333333334}, {0.9166666666666667, 1.25}})) "
        + "< 10^-12", //
        "True");
    check("Max(Abs(ListDeconvolve({{0, 1}, {1, 0}}, {{1., 2., 5.}, {3., 4., 1.}}) - "
        + "{{1.1665300709365307, 1.2423188260494868, 1.420891947258271}, "
        + "{1.1788365878643932, 1.2869619793153964, 1.4311902779858716}})) < 10^-9", //
        "True");
  }

  @Test
  public void testMatrix() {
    // a kernel of one row acts on every row of a matrix
    check("ListDeconvolve({{1, 3, 1}}, Table({2, 5, 4, 1, 3}, 3), "
        + "Method -> {\"DampedLS\", 0.4}) - "
        + "Table(ListDeconvolve({1, 3, 1}, {2, 5, 4, 1, 3}, Method -> {\"DampedLS\", 0.4}), 3) "
        + "// Chop", //
        "{{0,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0}}");
    check("ListDeconvolve({{2, 1}, {0, 1}}, {{3, 1, 4}, {1, 5, 9}, {2, 6, 5}}, "
        + "Method -> {\"Tikhonov\", 0.3}) - "
        + "Transpose(ListDeconvolve({{2, 0}, {1, 1}}, {{3, 1, 2}, {1, 5, 6}, {4, 9, 5}}, "
        + "Method -> {\"Tikhonov\", 0.3})) // Chop", //
        "{{0,0,0},{0,0,0},{0,0,0}}");
  }

  @Test
  public void testMessages() {
    check("Check(ListDeconvolve({1., 2., 1.}, {{1., 2.}, {3., 4.}}), failed, ListDeconvolve::dims)", //
        "failed");
    check("Check(ListDeconvolve({}, {1, 2}), failed, ListDeconvolve::arginv)", //
        "failed");
    check("Check(ListDeconvolve({1, 2}, {}), failed, ListDeconvolve::arginv)", //
        "failed");
    check("Check(ListDeconvolve({1, b, 1}, {3, 1, 2}), failed, ListDeconvolve::arginv)", //
        "failed");
    check("Check(ListDeconvolve({1, 1, 1, 1}, {1, 2}), failed, ListDeconvolve::kldims)", //
        "failed");
    check("Check(ListDeconvolve({1, 3, 1}, {2, 4, 6, 8}, Method -> {\"Tikhonov\", -2}), failed, "
        + "ListDeconvolve::param)", //
        "failed");
    check("Check(ListDeconvolve({1, 3, 1}, {2, 4, 6, 8}, 5), failed, ListDeconvolve::nonopt)", //
        "failed");
    check("ListDeconvolve({1, 1, 1, 1}, {1, 2})", //
        "ListDeconvolve({1,1,1,1},{1,2})");
    check("ListDeconvolve::kldims", //
        "The dimensions of a kernel cannot be larger than the dimensions of a list.");
  }
}
