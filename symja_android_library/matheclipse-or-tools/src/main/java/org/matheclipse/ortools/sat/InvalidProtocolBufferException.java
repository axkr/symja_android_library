package org.matheclipse.ortools.sat;

import java.io.IOException;

/**
 * Thrown when parsing {@link SatParameters} from the protobuf binary format fails; replaces
 * {@code com.google.protobuf.InvalidProtocolBufferException}, which the protobuf-java
 * {@code SatParameters} class of OR-Tools throws. (The messages of {@code cp_model.proto}
 * follow the OR-Tools 10.0 SWIG wrappers instead: {@code parseFrom(byte[])} returns null and
 * {@code mergeFrom(byte[])} throws a {@link RuntimeException}.)
 */
public class InvalidProtocolBufferException extends IOException {
  private static final long serialVersionUID = 1L;

  /** Creates the exception with a description of the error. */
  public InvalidProtocolBufferException(String description) {
    super(description);
  }
}
