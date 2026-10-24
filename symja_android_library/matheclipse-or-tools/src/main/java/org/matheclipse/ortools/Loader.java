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
// Modified for the Symja matheclipse-or-tools module: the native library loader of the
// OR-Tools Java wrapper is replaced by a no-op, see NOTICE in the module root.

package org.matheclipse.ortools;

/**
 * Compatibility stand-in for <code>com.google.ortools.Loader</code>.
 *
 * <p>
 * The OR-Tools Java wrapper extracts and loads the <code>jniortools</code> native library. This
 * module is pure Java, so there is nothing to load; the method exists so that programs written
 * against the OR-Tools Java API run unchanged after their imports are switched to
 * <code>org.matheclipse.ortools</code>.
 */
public final class Loader {

  private Loader() {}

  /** Does nothing: the solvers of this module are implemented in Java. */
  public static void loadNativeLibraries() {
    // nothing to load
  }
}
