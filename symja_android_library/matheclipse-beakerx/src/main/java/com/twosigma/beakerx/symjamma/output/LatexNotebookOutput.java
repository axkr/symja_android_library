package com.twosigma.beakerx.symjamma.output;

public class LatexNotebookOutput extends NotebookOutput {

  public LatexNotebookOutput(String content) {
    super("text/latex", content);
  }
}
