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
 * The flags the OR-Tools C++ library reads. They are kept so that programs which set them run
 * unchanged; the Java port does not dump models or write C++ logs.
 */
public class CppFlags {
  private int stderrthreshold = 2;
  private boolean log_prefix = false;
  private String cp_model_dump_prefix = "";
  private boolean cp_model_dump_models = false;
  private boolean cp_model_dump_submodels = false;
  private boolean cp_model_dump_response = false;

  public int getStderrthreshold() {
    return stderrthreshold;
  }

  public void setStderrthreshold(int value) {
    stderrthreshold = value;
  }

  public boolean getLog_prefix() {
    return log_prefix;
  }

  public void setLog_prefix(boolean value) {
    log_prefix = value;
  }

  public String getCp_model_dump_prefix() {
    return cp_model_dump_prefix;
  }

  public void setCp_model_dump_prefix(String value) {
    cp_model_dump_prefix = value;
  }

  public boolean getCp_model_dump_models() {
    return cp_model_dump_models;
  }

  public void setCp_model_dump_models(boolean value) {
    cp_model_dump_models = value;
  }

  public boolean getCp_model_dump_submodels() {
    return cp_model_dump_submodels;
  }

  public void setCp_model_dump_submodels(boolean value) {
    cp_model_dump_submodels = value;
  }

  public boolean getCp_model_dump_response() {
    return cp_model_dump_response;
  }

  public void setCp_model_dump_response(boolean value) {
    cp_model_dump_response = value;
  }
}
