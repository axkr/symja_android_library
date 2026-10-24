package org.matheclipse.ortools.sat;

/**
 * Common interface of the plain Java classes which replace the protobuf messages of the OR-Tools
 * CP-SAT solver ({@link CpModelProto}, {@link SatParameters}, {@link CpSolverResponse}, ...).
 *
 * <p>
 * The messages have the accessor names of the protobuf generated Java code but don't depend on
 * the protobuf runtime. They can print themselves in protobuf text format, see
 * {@link TextFormat}.
 */
public interface ProtoMessage {

  /** Prints the set fields of this message in field number order. */
  void printTo(ProtoPrinter printer);
}
