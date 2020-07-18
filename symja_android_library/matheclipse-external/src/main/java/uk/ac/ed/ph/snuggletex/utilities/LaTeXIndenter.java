/* $Id: LaTeXIndenter.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple utility class that does very basic indenting of LaTeX.
 * 
 * NOTE: This has not been properly tested yet so is to be assumed
 * experimental for the time being...!
 * 
 * @author  David McKain
 * @version $Revision: 525 $
 */
public final class LaTeXIndenter {
    
    /** Default indent width */
    public static int DEFAULT_INDENT_WIDTH = 2;
    
    /** Default maximum indent level */
    public static int DEFAULT_MAX_INDENT_LEVEL = 10;
    
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final String BEGIN = "\\begin";
    private static final String END = "\\end";
    private static final Pattern BEGIN_PATTERN = Pattern.compile("\\\\begin\\s*+\\{(.+?)\\}");
    private static final Pattern END_PATTERN   = Pattern.compile("\\\\end\\s*+\\{(.+?)\\}");
    
    // --------------------------------------------------------------
    // Properties
    
    /** Writer that we are outputting to */
    private final Writer outputWriter;
   
    /** Current indent width */
    private int indentWidth;
    
    /** Current maximum indent level */
    private int maxIndentLevel;
    
    /** Whether to compact >1 blank lines up as a single blank line */
    private boolean compactingBlankLines;
    
    // --------------------------------------------------------------
    // State
    
    /** Encapsulates the different states our parser can be in */
    private static enum ParsingState {
        /** Normal TeX */
        DEFAULT,
        
        /** Handling a run of empty lines */
        EMPTY_LINES,
        
        /** Waiting to resolve a \\begin that started on the previous line */
        BEGIN_DECLARATION,
        
        /** Waiting to resolve a \\end that started on the previous line */
        END_DECLARATION,
    }
    
    /** Number of consecutive blank lines encountered */
    private int blankLineCount;
    
    /** Current indent level */
    private int indentLevel;
    
    /** Set when we are in verbatim mode */
    private boolean verbatimMode;
    
    /** Current parsing state */
    private ParsingState parsingState;
    
    /**
     * Creates a new indenter that will output to {@link #outputWriter}. This will have
     * default properties set and will compact blank lines by default.
     * 
     * @param outputWriter
     */
    public LaTeXIndenter(final Writer outputWriter) {
        this.outputWriter = outputWriter;
        this.indentWidth = DEFAULT_INDENT_WIDTH;
        this.maxIndentLevel = DEFAULT_MAX_INDENT_LEVEL;
        this.compactingBlankLines = true;
        
        /* Initialise parsing state */
        reset();
    }
    
    //--------------------------------------------------------------
    
    public int getIndentWidth() {
        return this.indentWidth;
    }
    
    public void setIndentWidth(int indentWidth) {
        if (indentWidth<0) {
            throw new IllegalArgumentException("indentWidth must be non-negative");
        }
        this.indentWidth = indentWidth;
    }
    
    
    public int getMaxIndentLevel() {
        return this.maxIndentLevel;
    }
    
    public void setMaxIndentLevel(int maxIndentLevel) {
        if (maxIndentLevel<0) {
            throw new IllegalArgumentException("maxIndentLevel must be non-negative");
        }
        this.maxIndentLevel = maxIndentLevel;
    }
    
    
    public boolean isCompactingBlankLines() {
        return this.compactingBlankLines;
    }
    
    public void setCompactingBlankLines(boolean condenseBlankLines) {
        this.compactingBlankLines = condenseBlankLines;
    }
    
    //--------------------------------------------------------------
    
    public void reset() {
        this.indentLevel = 0;
        this.verbatimMode = false;
        this.blankLineCount = 0;
        this.parsingState = ParsingState.DEFAULT;
    }

    /**
     * Indents the TeX read from the given inputReader, sending the results to
     * {@link #outputWriter}. The inputReader will be closed afterwards; the
     * {@link #outputWriter} is left open so the caller should arrange to
     * flush or close it as required.
     */
    public void run(Reader inputReader) throws IOException {
        /* Reset state */
        reset();
        
        /* Ensure input is buffered */
        BufferedReader reader;
        if (inputReader instanceof BufferedReader) {
            reader = (BufferedReader) inputReader;
        }
        else {
            reader = new BufferedReader(inputReader);
        }
        /* Now read line-by-line */
        String line;
        try {
            while ((line=reader.readLine())!=null) {
                handleLine(line);
            }
        }
        finally {
            inputReader.close();
        }
    }
    
    //--------------------------------------------------------------

    /**
     * Handles a single line, which may or may not end in a newline character
     */
    private void handleLine(String line) throws IOException {
        /* Trim off trailing newline stuff */
        line = line.replace(LINE_SEPARATOR, "");
        
        /* If we're not currently in verbatim mode, then trim off leading and trailing whitespace */
        if (!verbatimMode) {
            line = line.trim();
        }
        /* Record whether line is blank and in non-verbatim mode */
        boolean isBlankLine = !verbatimMode && line.length()==0;
        
//        /* Log what we have at the moment */
//        System.out.println("Handling line: state=" + parsingState
//                    + ", vbMode=" + verbatimMode
//                    + ", iLevel=" + indentLevel
//                    + ", line=" + line
//                    + ", isBlank=" + isBlankLine);
        
        /* Now handle line, which will depend on the current parsing state */
        switch (parsingState) {
            case DEFAULT: {
                if (isBlankLine) {
                    /* Mark change of state */
                    parsingState = ParsingState.EMPTY_LINES;
                    blankLineCount++;
                }
                else {
                    /* Handle normally */
                    handleLineInDefaultState(line);
                }
                break;
            }
                
            case EMPTY_LINES: {
                if (isBlankLine) {
                    /* Do nothing, we've just had a blank line */
                    blankLineCount++;
                }
                else {
                    /* End of blank lines. So output blank (or blanks), return to default state
                     * and handle the line as normal.
                     */
                    outputWriter.write(LINE_SEPARATOR);
                    if (!compactingBlankLines) {
                        for (int i=1; i<blankLineCount; i++) {
                            outputWriter.write(LINE_SEPARATOR);
                        }
                    }
                    blankLineCount = 0;
                    parsingState = ParsingState.DEFAULT;
                    handleLineInDefaultState(line);
                }
                break;
            }

            case BEGIN_DECLARATION: {
                if (isBlankLine) {
                    /* Ignore */
                }
                else {
                    /* Should be finishing off the declaration */
                    parsingState = ParsingState.DEFAULT;
                    handleLineInDefaultState(BEGIN + line);
                }
                break;
            }
            
            case END_DECLARATION: {
                if (isBlankLine) {
                    /* Ignore */
                }
                else {
                    /* Should be finishing off the declaration */
                    parsingState = ParsingState.DEFAULT;
                    handleLineInDefaultState(END + line);
                }
                break;
            }
                
            default:
                throw new IllegalStateException("Unexpected switch case " + parsingState);
        }
    }
    
    private void handleLineInDefaultState(String line) throws IOException {
        /* Does line end with a \begin or \end which is unclosed */
        if (line.endsWith(BEGIN)) {
            handleLineFragmentInDefaultState(line.substring(0, line.length() - BEGIN.length()));
            parsingState = ParsingState.BEGIN_DECLARATION;
        }
        else if (line.endsWith(END)) {
            handleLineFragmentInDefaultState(line.substring(0, line.length() - END.length()));
            parsingState = ParsingState.END_DECLARATION;

        }
        else {
            handleLineFragmentInDefaultState(line);
        }
    }
    
    private void handleLineFragmentInDefaultState(String line) throws IOException {
//        System.out.println("Handling line fragment: state=" + parsingState
//                    + ", vbLevel=" + verbatimMode
//                    + ", iLevel=" + indentLevel
//                    + ", line=" + line
//                    );
        
        /* If not in verbatim, we'll indent the line as appropriate. This will be the
         * current indent, unless it starts with an '\end' in which case we'll unindent
         * immediately.
         */
        boolean initialUnindent = false;
        if (!verbatimMode) {
            if (line.startsWith(END)) {
                initialUnindent = true;
                indentLevel--;
            }
            createIndent(indentLevel);
        }

        /* Now output line */
        outputWriter.write(line);
        outputWriter.write(LINE_SEPARATOR);
        
        /* We now adjust the indentLevel and verbatimLevel.
         * 
         * First find all of the \end{...}'s.
         */
        Matcher matcher = END_PATTERN.matcher(line);
        while (matcher.find()) {
            if (!verbatimMode) {
                indentLevel--;

            }
            else {
                if (matcher.group(1).equals("verbatim")) {
                    /* (Leaving verbatim should apply to next line) */
                    verbatimMode = false;
                }
            }
        }
        /* Account for any initial unindent we found earlier */
        if (initialUnindent) {
            indentLevel++;
        }
        /* Then find all of the \begin{...}s */
        matcher = BEGIN_PATTERN.matcher(line);
        while (matcher.find()) {
            if (!verbatimMode) {
                if (matcher.group(1).equals("verbatim")) {
                    verbatimMode = true;
                }
                else {
                    indentLevel++;
                }
            }
        }
    }
    
    private void createIndent(int level) throws IOException {
        if (level > maxIndentLevel) {
            level = maxIndentLevel;
        }
        int toIndent = level * indentWidth;
        for (int i=0; i<toIndent; i++) {
            outputWriter.write(' ');
        }
    }
}
