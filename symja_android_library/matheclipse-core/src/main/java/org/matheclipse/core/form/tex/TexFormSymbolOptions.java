package org.matheclipse.core.form.tex;

public class TexFormSymbolOptions {
  private String timesSymbol = " \\cdot ";
  private String derivativeSymbol = "\\partial";
  private String equalSymbol = " == ";
  private String logFunction = "log ";

  public String getDerivativeSymbol() {
    return derivativeSymbol;
  }

  public void setDerivativeSymbol(String derivativeSymbol) {
    this.derivativeSymbol = derivativeSymbol;
  }

  public String getTimesSymbol() {
    return timesSymbol;
  }

  public void setTimesSymbol(String timesSymbol) {
    this.timesSymbol = timesSymbol;
  }

  public String getEqualSymbol() {
    return equalSymbol;
  }

  public void setEqualSymbol(String equalSymbol) {
    this.equalSymbol = equalSymbol;
  }

  public String getLogFunction() {
    return logFunction;
  }

  public void setLogFunction(String logFunction) {
    this.logFunction = logFunction;
  }
}
