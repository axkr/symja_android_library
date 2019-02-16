package com.twosigma.beakerx.symjamma.output;

public class MarkdownNotebookOutput extends NotebookOutput {

    public MarkdownNotebookOutput(String content) {
	super("text/markdown", content);
    }

}