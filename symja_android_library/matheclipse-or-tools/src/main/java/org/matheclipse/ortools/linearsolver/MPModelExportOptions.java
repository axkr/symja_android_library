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
// Modified for the Symja matheclipse-or-tools module: adapted from the OR-Tools Java wrapper
// (ortools/linear_solver/model_exporter.h ported to Java), see NOTICE in the module root.

package org.matheclipse.ortools.linearsolver;

/** Export options of the LP and MPS model exporter. */
public class MPModelExportOptions {

  private boolean obfuscate;
  private boolean logInvalidNames;
  private boolean showUnusedVariables;
  private int maxLineLength = 10000;

  /** Creates the default options. */
  public MPModelExportOptions() {}

  /** Does nothing: the memory is managed by the garbage collector. */
  public void delete() {
    // nothing to release
  }

  /** Obfuscates variable and constraint names. */
  public void setObfuscate(boolean value) {
    obfuscate = value;
  }

  /** Obfuscates variable and constraint names. */
  public boolean getObfuscate() {
    return obfuscate;
  }

  /** Whether to log invalid variable and constraint names. */
  public void setLogInvalidNames(boolean value) {
    logInvalidNames = value;
  }

  /** Whether to log invalid variable and constraint names. */
  public boolean getLogInvalidNames() {
    return logInvalidNames;
  }

  /**
   * For .lp files only. Decides whether variables unused in the objective and constraints are
   * shown when exported to a file.
   */
  public void setShowUnusedVariables(boolean value) {
    showUnusedVariables = value;
  }

  /**
   * For .lp files only. Decides whether variables unused in the objective and constraints are
   * shown when exported to a file.
   */
  public boolean getShowUnusedVariables() {
    return showUnusedVariables;
  }

  /**
   * For .lp files only. Maximum line length in exported files. The default was chosen so that SCIP
   * can read the files.
   */
  public void setMaxLineLength(int value) {
    maxLineLength = value;
  }

  /**
   * For .lp files only. Maximum line length in exported files. The default was chosen so that SCIP
   * can read the files.
   */
  public int getMaxLineLength() {
    return maxLineLength;
  }
}
