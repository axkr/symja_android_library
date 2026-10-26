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
 * The version of OR-Tools this Java port follows: 10.0, the release candidate of commit
 * <code>ca2ec93777</code>. Without a <code>v10.0</code> tag the OR-Tools build numbers it
 * 10.0.0.
 */
public class OrToolsVersion {

  /** The major version number. */
  public static int getMajorNumber() {
    return 10;
  }

  /** The minor version number. */
  public static int getMinorNumber() {
    return 0;
  }

  /** The patch number; 0 for the 10.0 release candidate. */
  public static int getPatchNumber() {
    return 0;
  }

  /** The version as <code>major.minor.patch</code>. */
  public static String getVersionString() {
    return getMajorNumber() + "." + getMinorNumber() + "." + getPatchNumber();
  }
}
