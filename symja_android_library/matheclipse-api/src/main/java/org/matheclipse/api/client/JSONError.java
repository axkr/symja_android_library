package org.matheclipse.api.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class JSONError {
  private static final String ERROR_NAME = "error";
  private static final String ERRORTEXT_NAME = "#errorText";

  @JsonProperty(ERROR_NAME)
  boolean error;

  @JsonProperty("code")
  int code;
  @JsonProperty("msg")
  String msg;

  @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
  public JSONError(String error) {
    this(error, null);
  }

  @JsonCreator
  public JSONError(@JsonProperty(ERROR_NAME) String error,
      @JsonProperty(ERRORTEXT_NAME) String text) {
    if (error == null || error.equals("true")) {
      this.error = true;
    } else {
      this.error = false;
    }
  }

  public int getCode() {
    return code;
  }

  public String getMsg() {
    return error ? msg : "";
  }

  public boolean isError() {
    return error;
  }
}
