package org.matheclipse.api.client;

import com.fasterxml.jackson.annotation.JsonProperty;

final public class JSONPods {
  @JsonProperty("success")
  boolean success;

  @JsonProperty("error")
  JSONError error;

  @JsonProperty("numpods")
  int numpods;

  @JsonProperty("version")
  String version;

  @JsonProperty("pods")
  JSONPod[] pods;

  public JSONPods() {

  }

  public JSONError getError() {
    return error;
  }

  public JSONPod[] getPods() {
    return pods;
  }

  public boolean getSuccess() {
    return success;
  }

  public String getVersion() {
    return version;
  }

  public int numberOfPods() {
    return numpods;
  }
}