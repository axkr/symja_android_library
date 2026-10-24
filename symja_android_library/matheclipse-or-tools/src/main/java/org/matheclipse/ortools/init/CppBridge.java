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
// Modified for the Symja matheclipse-or-tools module: ported from ortools/init/init.h to Java,
// see NOTICE in the module root.

package org.matheclipse.ortools.init;

/**
 * The C++ initialization entry points of OR-Tools. The Java port has no C++ runtime, so the
 * methods only remember their arguments.
 */
public class CppBridge {
  private static volatile CppFlags flags = new CppFlags();

  /** Initialize the logging of the library; nothing to do in Java. */
  public static void initLogging(String usage) {
    // java.util.logging needs no initialization
  }

  /** Shut down the logging of the library; nothing to do in Java. */
  public static void shutdownLogging() {
    // nothing to shut down
  }

  /** Set the C++ flags. */
  public static void setFlags(CppFlags cppFlags) {
    flags = cppFlags;
  }

  /** The flags set last with {@link #setFlags(CppFlags)}. */
  public static CppFlags getFlags() {
    return flags;
  }

  /**
   * Load the Gurobi shared library. Gurobi is not available in the Java port.
   *
   * @return always <code>false</code>
   */
  public static boolean loadGurobiSharedLibrary(String fullLibraryPath) {
    return false;
  }
}
