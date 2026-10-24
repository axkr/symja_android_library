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
// Modified for the Symja matheclipse-or-tools module: sample ported to run as a JUnit test, see NOTICE in the module root.

// [START program]
package org.matheclipse.ortools.sat.samples;

import org.matheclipse.ortools.Loader;
import org.matheclipse.ortools.sat.BoolVar;
import org.matheclipse.ortools.sat.CpModel;
import org.matheclipse.ortools.sat.Literal;

/** Code sample to demonstrate Boolean variable and literals. */
public class LiteralSampleSat {
  public static void main(String[] args) throws Exception {
    Loader.loadNativeLibraries();
    CpModel model = new CpModel();
    BoolVar x = model.newBoolVar("x");
    Literal notX = x.not();
    System.out.println(notX);
  }
}
// [END program]
