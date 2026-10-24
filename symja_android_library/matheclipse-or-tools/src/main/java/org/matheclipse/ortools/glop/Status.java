// Copyright 2010-2025 Google LLC
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
// Modified for the Symja matheclipse-or-tools module: ported from ortools/glop/status.h/.cc C++
// to Java, see NOTICE in the module root.

package org.matheclipse.ortools.glop;

/** The result of an internal Glop operation: OK or an error code with a message. */
public final class Status {

  /** The error codes. */
  public enum ErrorCode {
    /** No error. */
    GLOP_OK,
    /** The LU factorization failed (singular basis). */
    ERROR_LU,
    /** A bound is invalid. */
    ERROR_BOUND,
    /** An internal pointer was null. */
    ERROR_NULL,
    /** An invalid parameter. */
    ERROR_INVALID_PARAMETER;
  }

  /** The shared OK status. */
  public static final Status OK = new Status(ErrorCode.GLOP_OK, "");

  private final ErrorCode errorCode;
  private final String errorMessage;

  /** Creates a status. */
  public Status(ErrorCode errorCode, String errorMessage) {
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
  }

  /** Returns true if there is no error. */
  public boolean ok() {
    return errorCode == ErrorCode.GLOP_OK;
  }

  /** The error code. */
  public ErrorCode errorCode() {
    return errorCode;
  }

  /** The error message. */
  public String errorMessage() {
    return errorMessage;
  }

  @Override
  public String toString() {
    return ok() ? "OK" : errorCode + ": " + errorMessage;
  }
}
