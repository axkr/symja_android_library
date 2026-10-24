package org.matheclipse.ortools.sat;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

/**
 * Tests of the protobuf binary and text formats of the message classes and of the OR-Tools 10.0
 * wrapper methods ({@code toByteArray}, {@code parseFrom}, {@code mergeFrom(byte[])},
 * {@code parseTextFormat}, {@code copyFrom}, modifiable {@code getXxxList()} views). The expected
 * byte sequences are the canonical protobuf encodings of the messages, written by hand from the
 * field numbers of cp_model.proto and sat_parameters.proto.
 */
public class ProtoFormatTest {

  private static byte[] bytes(int... values) {
    byte[] b = new byte[values.length];
    for (int i = 0; i < values.length; i++) {
      b[i] = (byte) values[i];
    }
    return b;
  }

  @Test
  public void testCanonicalEncodings() {
    IntegerVariableProto var =
        IntegerVariableProto.newBuilder().setName("x").addDomain(0).addDomain(10).build();
    assertArrayEquals(bytes(0x0A, 0x01, 'x', 0x12, 0x02, 0x00, 0x0A), var.toByteArray());

    // negative int64 in a packed field: 10 byte varint; offset after the repeated fields
    LinearExpressionProto expr =
        LinearExpressionProto.newBuilder().addVars(1).addCoeffs(-1).setOffset(5).build();
    assertArrayEquals(bytes(0x0A, 0x01, 0x01, 0x12, 0x0A, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
        0xFF, 0xFF, 0xFF, 0x01, 0x18, 0x05), expr.toByteArray());

    // negative int32: sign extended to 10 bytes as well
    BoolArgumentProto lits = BoolArgumentProto.newBuilder().addLiterals(-2).build();
    assertArrayEquals(bytes(0x0A, 0x0A, 0xFE, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
        0x01), lits.toByteArray());

    // oneof member, also when empty
    ConstraintProto.Builder ct = ConstraintProto.newBuilder();
    ct.getBoolOrBuilder().addLiterals(1);
    assertArrayEquals(bytes(0x1A, 0x03, 0x0A, 0x01, 0x01), ct.build().toByteArray());
    ConstraintProto.Builder empty = ConstraintProto.newBuilder();
    empty.getBoolOrBuilder();
    assertArrayEquals(bytes(0x1A, 0x00), empty.build().toByteArray());

    // fields in field number order: vars (1), offset (2, double), coeffs (4)
    CpObjectiveProto obj =
        CpObjectiveProto.newBuilder().addVars(0).addCoeffs(2).setOffset(1.5).build();
    assertArrayEquals(bytes(0x0A, 0x01, 0x00, 0x11, 0, 0, 0, 0, 0, 0, 0xF8, 0x3F, 0x22, 0x01,
        0x02), obj.toByteArray());

    // proto3 defaults are not written
    assertEquals(0, LinearExpressionProto.newBuilder().setOffset(0).build().toByteArray().length);
    assertEquals(0, CpModelProto.getDefaultInstance().toByteArray().length);

    // proto2: set fields are written even with their default value, repeated enums unpacked
    SatParameters params = SatParameters.newBuilder()
        .addRestartAlgorithms(SatParameters.RestartAlgorithm.LUBY_RESTART)
        .addRestartAlgorithms(SatParameters.RestartAlgorithm.NO_RESTART)
        .setMaxTimeInSeconds(1.5).setNumWorkers(0).build();
    assertArrayEquals(bytes(0xA1, 0x02, 0, 0, 0, 0, 0, 0, 0xF8, 0x3F, 0xE8, 0x03, 0x01, 0xE8,
        0x03, 0x00, 0xF0, 0x0C, 0x00), params.toByteArray());
    assertEquals(0, SatParameters.getDefaultInstance().toByteArray().length);
  }

  @Test
  public void testParseAcceptsPackedAndUnpacked() {
    // unpacked encoding of literals = [3, -1]
    byte[] unpacked = bytes(0x08, 0x03, 0x08, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
        0xFF, 0x01);
    BoolArgumentProto b = BoolArgumentProto.parseFrom(unpacked);
    assertNotNull(b);
    assertEquals(Arrays.asList(3, -1), b.getLiteralsList());
    // unknown fields (varint 15, length delimited 16, fixed32 17, fixed64 18) are skipped
    byte[] withUnknown = bytes(0x78, 0x05, 0x82, 0x01, 0x02, 0x41, 0x42, 0x8D, 0x01, 1, 2, 3, 4,
        0x91, 0x01, 1, 2, 3, 4, 5, 6, 7, 8, 0x0A, 0x01, 0x07);
    assertEquals(Arrays.asList(7), BoolArgumentProto.parseFrom(withUnknown).getLiteralsList());
    // packed SatParameters restart algorithms are accepted too
    SatParameters p = assertDoesNotThrowParse(bytes(0xEA, 0x03, 0x02, 0x01, 0x02));
    assertEquals(Arrays.asList(SatParameters.RestartAlgorithm.LUBY_RESTART,
        SatParameters.RestartAlgorithm.DL_MOVING_AVERAGE_RESTART), p.getRestartAlgorithmsList());
  }

  private static SatParameters assertDoesNotThrowParse(byte[] data) {
    try {
      return SatParameters.parseFrom(data);
    } catch (InvalidProtocolBufferException e) {
      throw new AssertionError(e);
    }
  }

  @Test
  public void testInvalidBinaryData() {
    // the 10.0 wrapper test data: field number 0 is invalid
    byte[] bad = bytes(0, 0, 0);
    assertNull(CpModelProto.parseFrom(bad));
    CpModelProto.Builder dst = CpModelProto.newBuilder().setName("dst");
    assertThrows(RuntimeException.class, () -> dst.mergeFrom(bad));
    // truncated length delimited field
    assertNull(IntegerVariableProto.parseFrom(bytes(0x0A, 0x05, 'x')));
    // truncated varint
    assertNull(LinearExpressionProto.parseFrom(bytes(0x18, 0x80)));
    // invalid UTF-8 in a proto3 string
    assertNull(IntegerVariableProto.parseFrom(bytes(0x0A, 0x01, 0xFF)));
    assertThrows(InvalidProtocolBufferException.class, () -> SatParameters.parseFrom(bad));
  }

  @Test
  public void testMergeFromBytesReplacesContent() {
    // like the 10.0 wrapper (ParsePartialFromArray), mergeFrom(byte[]) clears the builder first
    IntegerVariableProto src = IntegerVariableProto.newBuilder().setName("src").addDomain(1)
        .addDomain(2).build();
    IntegerVariableProto.Builder dst =
        IntegerVariableProto.newBuilder().setName("dst").addDomain(5).addDomain(6);
    dst.mergeFrom(src.toByteArray());
    assertEquals(src, dst.build());
    // the protobuf-java SatParameters builder merges
    SatParameters.Builder p = SatParameters.newBuilder().setNumWorkers(3);
    try {
      p.mergeFrom(SatParameters.newBuilder().setMaxTimeInSeconds(2).build().toByteArray());
    } catch (InvalidProtocolBufferException e) {
      throw new AssertionError(e);
    }
    assertEquals(3, p.getNumWorkers());
    assertEquals(2.0, p.getMaxTimeInSeconds());
  }

  private static CpModel randomModel(Random r) {
    CpModel model = new CpModel();
    int n = 2 + r.nextInt(4);
    IntVar[] x = new IntVar[n];
    for (int i = 0; i < n; i++) {
      x[i] = model.newIntVar(-r.nextInt(5), r.nextInt(100_000), "x" + i);
    }
    BoolVar b = model.newBoolVar("b");
    model.addAllDifferent(x);
    model.addLinearConstraint(LinearExpr.sum(x), -3, 7).onlyEnforceIf(b.not());
    model.addMaxEquality(x[0], new IntVar[] {x[1], x[n - 1]});
    model.addInverse(new IntVar[] {x[0], x[1]}, new IntVar[] {x[1], x[0]});
    model.addElement(x[0], new long[] {3, -4, 5}, x[1]);
    model.addBoolXor(new Literal[] {b, b.not()});
    model.addHint(x[0], 3);
    model.addAssumption(b);
    IntervalVar iv = model.newFixedSizeIntervalVar(x[0], 3, "iv");
    model.addNoOverlap(new IntervalVar[] {iv});
    model.addCumulative(5).addDemand(iv, 2);
    model.minimize(LinearExpr.sum(x));
    model.getBuilder().getObjectiveBuilder().setScalingFactor(-0.25).setOffset(1e-5);
    model.addDecisionStrategy(x, DecisionStrategyProto.VariableSelectionStrategy.CHOOSE_MIN_DOMAIN_SIZE,
        DecisionStrategyProto.DomainReductionStrategy.SELECT_MAX_VALUE);
    return model;
  }

  @Test
  public void testRoundTrips() {
    Random r = new Random(3);
    for (int iter = 0; iter < 30; iter++) {
      CpModelProto proto = randomModel(r).model();
      CpModelProto back = CpModelProto.parseFrom(proto.toByteArray());
      assertEquals(proto, back);
      assertArrayEquals(proto.toByteArray(), back.toByteArray());
      CpModelProto.Builder text = CpModelProto.newBuilder();
      assertTrue(text.parseTextFormat(proto.toString()), proto.toString());
      assertEquals(proto, text.build());
      CpModelProto.Builder shortText = CpModelProto.newBuilder();
      assertTrue(shortText.parseTextFormat(TextFormat.shortDebugString(proto)));
      assertEquals(proto, shortText.build());
    }
    CpSolver solver = new CpSolver();
    CpModel model = new CpModel();
    IntVar a = model.newIntVar(0, 3, "a");
    IntVar c = model.newIntVar(0, 3, "c");
    model.addDifferent(a, c);
    model.maximize(LinearExpr.weightedSum(new IntVar[] {a, c}, new long[] {3, 1}));
    solver.getParameters().setLogToResponse(true).setLogSearchProgress(true).setLogToStdout(false);
    assertEquals(CpSolverStatus.OPTIMAL, solver.solve(model));
    CpSolverResponse response = solver.response();
    assertEquals(response, CpSolverResponse.parseFrom(response.toByteArray()));
    CpSolverResponse.Builder rb = CpSolverResponse.newBuilder();
    assertTrue(rb.parseTextFormat(response.toString()));
    assertEquals(response, rb.build());
    SatParameters params = solver.getParameters().setLogPrefix("a\"b\\c\n")
        .addSubsolvers("core").addSubsolvers("lns").setRandomSeed(-7).build();
    SatParameters.Builder pb = SatParameters.newBuilder();
    try {
      assertEquals(params, SatParameters.parseFrom(params.toByteArray()));
      TextFormat.merge(params.toString(), pb);
    } catch (java.io.IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(params, pb.build());
  }

  @Test
  public void testExportToFile(@org.junit.jupiter.api.io.TempDir java.nio.file.Path dir)
      throws java.io.IOException {
    CpModel model = randomModel(new Random(5));
    CpModelProto proto = model.model();
    java.nio.file.Path bin = dir.resolve("model.pb");
    java.nio.file.Path txt = dir.resolve("model.pb.txt");
    assertTrue(model.exportToFile(bin.toString()));
    assertTrue(model.exportToFile(txt.toString()));
    assertEquals(proto, CpModelProto.parseFrom(java.nio.file.Files.readAllBytes(bin)));
    CpModelProto.Builder b = CpModelProto.newBuilder();
    assertTrue(b.parseTextFormat(new String(java.nio.file.Files.readAllBytes(txt),
        java.nio.charset.StandardCharsets.UTF_8)));
    assertEquals(proto, b.build());
    assertFalse(model.exportToFile(dir.resolve("missing/dir/model.pb").toString()));
  }

  @Test
  public void testParseTextFormat() {
    // the 10.0 wrapper test: parse the printed message
    IntegerVariableProto.Builder v = IntegerVariableProto.newBuilder();
    v.setName("y");
    IntegerVariableProto.Builder v2 = IntegerVariableProto.newBuilder();
    assertTrue(v2.parseTextFormat(v.toString()));
    assertEquals("y", v2.getName());

    CpModelProto.Builder m = CpModelProto.newBuilder();
    assertFalse(m.parseTextFormat("linear_is_not_a_model_field { }"));
    assertTrue(m.parseTextFormat("# a comment\n name: 'm' variables { domain: [0, 0x0A] }"
        + " variables < name: \"b\" \"c\" domain: 0 domain: 1 >;"
        + " constraints { enforcement_literal: -2 linear { vars: 0 coeffs: -3 domain: [-5, 012] } }"
        + " objective { vars: 0 coeffs: 1 offset: -1.5e1 scaling_factor: inf }"
        + " search_strategy { variables: 0 domain_reduction_strategy: SELECT_MAX_VALUE"
        + " variable_selection_strategy: 2 }"));
    assertEquals("m", m.getName());
    assertEquals(Arrays.asList(0L, 10L), m.getVariables(0).getDomainList());
    assertEquals("bc", m.getVariables(1).getName());
    assertEquals(Arrays.asList(-5L, 10L), m.getConstraints(0).getLinear().getDomainList());
    assertEquals(-3L, m.getConstraints(0).getLinear().getCoeffs(0));
    assertEquals(-15.0, m.getObjective().getOffset());
    assertEquals(Double.POSITIVE_INFINITY, m.getObjective().getScalingFactor());
    assertEquals(DecisionStrategyProto.DomainReductionStrategy.SELECT_MAX_VALUE,
        m.getSearchStrategy(0).getDomainReductionStrategy());
    assertEquals(DecisionStrategyProto.VariableSelectionStrategy.CHOOSE_HIGHEST_MAX,
        m.getSearchStrategy(0).getVariableSelectionStrategy());
    assertEquals(-2, m.getConstraints(0).getEnforcementLiteral(0));

    // the builder is cleared first
    assertTrue(m.parseTextFormat(""));
    assertEquals(CpModelProto.getDefaultInstance(), m.build());

    // errors
    CpModelProto.Builder e = CpModelProto.newBuilder();
    assertFalse(e.parseTextFormat("unknown_field: 1"));
    assertFalse(e.parseTextFormat("name: 'a' name: 'b'"), "non-repeated field given twice");
    assertFalse(e.parseTextFormat("objective { } objective { }"));
    assertFalse(e.parseTextFormat("variables { domain: 1.5 }"));
    assertFalse(e.parseTextFormat("variables { domain: 99999999999999999999 }"));
    assertFalse(e.parseTextFormat("assumptions: 3000000000"));
    assertFalse(e.parseTextFormat("constraints { bool_or { } bool_and { } }"), "two oneof members");
    assertFalse(e.parseTextFormat("search_strategy { domain_reduction_strategy: NOT_A_VALUE }"));
    assertFalse(e.parseTextFormat("search_strategy { domain_reduction_strategy: UNRECOGNIZED }"));
    assertFalse(e.parseTextFormat("variables { name: \"x"));
    assertFalse(e.parseTextFormat("variables { name: 'x' "));
    assertFalse(e.parseTextFormat("variables { name 'x' }"), "a scalar field needs a colon");
    // an open (proto3) enum accepts unknown numbers
    assertTrue(e.parseTextFormat("search_strategy { domain_reduction_strategy: 77 }"));
    assertEquals(77, e.getSearchStrategy(0).getDomainReductionStrategyValue());
  }

  @Test
  public void testSatParametersTextFormat() throws TextFormat.ParseException {
    SatParameters.Builder p = SatParameters.newBuilder().setNumWorkers(4);
    TextFormat.merge("max_time_in_seconds: 10 search_branching: FIXED_SEARCH"
        + " restart_algorithms: [LUBY_RESTART, 2] log_search_progress: true"
        + " max_time_in_seconds: 2.5", p);
    assertEquals(4, p.getNumWorkers());
    assertEquals(2.5, p.getMaxTimeInSeconds()); // protobuf-java allows singular overwrites
    assertEquals(SatParameters.SearchBranching.FIXED_SEARCH, p.getSearchBranching());
    assertEquals(Arrays.asList(SatParameters.RestartAlgorithm.LUBY_RESTART,
        SatParameters.RestartAlgorithm.DL_MOVING_AVERAGE_RESTART), p.getRestartAlgorithmsList());
    assertTrue(p.getLogSearchProgress());
    TextFormat.merge("search_branching: ALT_FIXED_SEARCH", p);
    assertEquals(SatParameters.SearchBranching.ALT_FIXED_SEARCH, p.getSearchBranching());
    assertThrows(TextFormat.ParseException.class,
        () -> TextFormat.merge("check_drat_proof: true", SatParameters.newBuilder()),
        "field removed in 10.0");
    assertThrows(TextFormat.ParseException.class,
        () -> TextFormat.merge("search_branching: 99", SatParameters.newBuilder()),
        "unknown value of a closed (proto2) enum");
  }

  @Test
  public void testBuilderListViewsAndCopy() {
    CpModelProto.Builder m = CpModelProto.newBuilder();
    List<Integer> assumptions = m.getAssumptionsList();
    assumptions.add(3);
    assumptions.add(4);
    assumptions.set(0, 5);
    assumptions.remove(1);
    assertEquals(Arrays.asList(5), m.build().getAssumptionsList());
    assertThrows(NullPointerException.class, () -> assumptions.add(null));

    IntegerVariableProto.Builder vb = IntegerVariableProto.newBuilder().addDomain(0).addDomain(1);
    m.getVariablesList().add(vb);
    vb.setName("changed later"); // the list stores a copy, like the C++ RepeatedPtrField
    assertEquals("", m.getVariables(0).getName());
    assertEquals(1, m.build().getVariablesCount());

    // immutable messages keep unmodifiable lists
    CpModelProto built = m.build();
    assertThrows(UnsupportedOperationException.class, () -> built.getAssumptionsList().add(1));

    CpModelProto.Builder copy = CpModelProto.newBuilder().setName("old");
    copy.copyFrom(built);
    assertEquals(built, copy.build());

    // the protobuf-java SatParameters builder keeps unmodifiable lists
    SatParameters.Builder p = SatParameters.newBuilder();
    assertThrows(UnsupportedOperationException.class, () -> p.getSubsolversList().add("x"));
  }
}
