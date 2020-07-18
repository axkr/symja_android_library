/* $Id: LaTeXTokeniser.java 567 2010-05-20 14:21:46Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.internal;

import uk.ac.ed.ph.snuggletex.ErrorCode;
import uk.ac.ed.ph.snuggletex.InputError;
import uk.ac.ed.ph.snuggletex.SnuggleInput;
import uk.ac.ed.ph.snuggletex.SnuggleLogicException;
import uk.ac.ed.ph.snuggletex.definitions.BuiltinCommand;
import uk.ac.ed.ph.snuggletex.definitions.BuiltinEnvironment;
import uk.ac.ed.ph.snuggletex.definitions.Command;
import uk.ac.ed.ph.snuggletex.definitions.CommandOrEnvironment;
import uk.ac.ed.ph.snuggletex.definitions.CoreErrorCode;
import uk.ac.ed.ph.snuggletex.definitions.CorePackageDefinitions;
import uk.ac.ed.ph.snuggletex.definitions.Globals;
import uk.ac.ed.ph.snuggletex.definitions.LaTeXMode;
import uk.ac.ed.ph.snuggletex.definitions.TextFlowContext;
import uk.ac.ed.ph.snuggletex.definitions.UserDefinedCommand;
import uk.ac.ed.ph.snuggletex.definitions.UserDefinedCommandOrEnvironment;
import uk.ac.ed.ph.snuggletex.definitions.UserDefinedEnvironment;
import uk.ac.ed.ph.snuggletex.internal.WorkingDocument.SourceContext;
import uk.ac.ed.ph.snuggletex.internal.util.ArrayListStack;
import uk.ac.ed.ph.snuggletex.semantics.Interpretation;
import uk.ac.ed.ph.snuggletex.semantics.InterpretationType;
import uk.ac.ed.ph.snuggletex.semantics.MathIdentifierInterpretation;
import uk.ac.ed.ph.snuggletex.semantics.MathNumberInterpretation;
import uk.ac.ed.ph.snuggletex.tokens.ArgumentContainerToken;
import uk.ac.ed.ph.snuggletex.tokens.BraceContainerToken;
import uk.ac.ed.ph.snuggletex.tokens.CommandToken;
import uk.ac.ed.ph.snuggletex.tokens.EnvironmentToken;
import uk.ac.ed.ph.snuggletex.tokens.ErrorToken;
import uk.ac.ed.ph.snuggletex.tokens.FlowToken;
import uk.ac.ed.ph.snuggletex.tokens.SimpleToken;
import uk.ac.ed.ph.snuggletex.tokens.Token;
import uk.ac.ed.ph.snuggletex.tokens.TokenType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class reads in SnuggleTeX input and builds a tree of literal parsed {@link Token}s.
 * <p>
 * This is probably the most complex class in SnuggleTeX!
 * 
 * @author  David McKain
 * @version $Revision: 567 $
 */
public final class LaTeXTokeniser {
    
    /** 
     * Set of reserved commands. These cannot be redefined and are not all listed in
     * {@link CorePackageDefinitions}.
     */
    public static final Set<String> reservedCommands = new HashSet<String>(Arrays.asList(new String[] {
        "begin",
        "end",
        "(",
        ")",
        "[",
        "]",
        "newcommand",
        "renewcommand",
        "newenvironment",
        "renewenvironment"
    }));
    
    /** 
     * Name of internal command that gets temporarily appended after the begin clauses of a
     * user-defined environment has been substituted in order to make sure the
     * {@link #openEnvironmentStack} is kept in the correct order.
     * <p>
     * No trace of this command will exist once tokenisation has finished.
     * <p>
     * I've chosen a non-ASCII name for this command so as to make it impossible to be used in
     * "real" inputs.
     * 
     * See {@link #handleUserDefinedEnvironmentControl()}
     */
    private static final String UDE_POST_BEGIN = "\u00a3";
    
    /**
     * Provides access to the current {@link SessionContext}.
     */
    private final SessionContext sessionContext;
    
    //-----------------------------------------
    // Tokenisation state
    
    /** 
     * Current "working document", taking into account command/environment substitutions. This
     * starts off identical to the content of the {@link SnuggleInput} being read but will change
     * during tokenisation.
     */
    private WorkingDocument workingDocument;
    
    /** 
     * Current position within {@link #workingDocument}. This starts at 0 and generally increases
     * monotonically until it hits the length of the {@link #workingDocument}, though we may
     * sometimes move backwards when performing substitutions.
     */
    private int position;
    
    /**
     * Index of the start of the current token being parsed. This is set in {@link #readNextToken()}.
     */
    private int startTokenIndex;

    /** Current parsing "mode" */
    private ModeState currentModeState;
    
    /** Stack of parsing modes, with the last entry being equal to {@link #currentModeState} */
    private final ArrayListStack<ModeState> modeStack;
    
    /** Stack of open environments */
    private final ArrayListStack<String> openEnvironmentStack;
    
    /**
     * Represents the "terminator" characters that signify the end of a parsing mode. I have
     * made this an interface is some terminators are static Strings, whereas others are best
     * considered as {@link Pattern}s.
     */
    public static interface Terminator {
        
        /**
         * Tests whether this Terminator matches the {@link WorkingDocument} at the given index.
         * 
         * @param workingDocument {@link WorkingDocument} to check
         * @param index index to match at
         * 
         * @return -1 if no match, otherwise the index <strong>after</strong> the match.
         */
        int matchesAt(final WorkingDocument workingDocument, int index);
        
        /**
         * Finds the next match of this Terminator in the given {@link WorkingDocument} starting
         * at the given index.
         * 
         * @param workingDocument {@link WorkingDocument} to check
         * @param index index to search from
         */
        int nextMatchFrom(final WorkingDocument workingDocument, int index);
    }
    
    /**
     * This implementation of {@link Terminator} is used to handle fixed {@link String}s as
     * terminators.
     */
    public static final class StringTerminator implements Terminator {
        
        private final String terminatorString;
        
        public StringTerminator(final String terminatorString) {
            this.terminatorString = terminatorString;
        }
        
        public int matchesAt(final WorkingDocument workingDocument, int index) {
            return workingDocument.matchesAt(index, terminatorString) ?
                    index + terminatorString.length() : -1;
        }
        
        public int nextMatchFrom(WorkingDocument workingDocument, int index) {
            return workingDocument.indexOf(index, terminatorString);
        }
        
        @Override
        public String toString() {
            return terminatorString;
        }
    }
    
    /**
     * This implementation of {@link Terminator} represents terminators specified as regular
     * expressions. This is generally only useful for hunting out the end of <tt>verbatim</tt>
     * environments. 
     */
    public static final class PatternTerminator implements Terminator {
        
        private final Pattern terminatorPattern;
        
        public PatternTerminator(final Pattern terminatorPattern) {
            this.terminatorPattern = terminatorPattern;
        }
        
        public int matchesAt(final WorkingDocument workingDocument, int index) {
            Matcher matcher = terminatorPattern.matcher(workingDocument.extract());
            return matcher.find(index) && matcher.start()==index ? matcher.end() : -1;
        }
        
        public int nextMatchFrom(WorkingDocument workingDocument, int index) {
            Matcher matcher = terminatorPattern.matcher(workingDocument.extract());
            return matcher.find(index) ? matcher.start() : -1;
        }
        
        @Override
        public String toString() {
            return "(pattern) " + terminatorPattern;
        }
    }
    
    /**
     * Enumeration of the various "tokenisation modes" we will be running in. This is only
     * really used for documentation during debugging!
     */
    public static enum TokenisationMode {
        TOP_LEVEL,
        BRACE,
        MATH,
        BUILTIN_COMMAND_ARGUMENT,
        BUILTIN_ENVIRONMENT_CONTENT,
        ;
    }
    
    /**
     * Trivial "struct" encapsulating information about the state of a tokenisation mode.
     */
    public static class ModeState {
        
        /** Mode we're tokenising in */
        public final TokenisationMode tokenisationMode;
        
        /** Current LaTeX mode we are parsing in */
        public LaTeXMode latexMode;
        
        /** Position within {@link WorkingDocument} where this mode started */
        public final int startPosition;
        
        /** Terminator we are searching for */
        public final Terminator terminator;
        
        /** Tokens accumulated in this mode */
        public final List<FlowToken> tokens;
        
        /** 
         * If terminator is not null, then this indicates whether the terminator was found when
         * parsing in this mode ended.
         */
        public boolean foundTerminator;
        
        public ModeState(final TokenisationMode tokenisationMode, final LaTeXMode latexMode,
                final int startPosition, final Terminator terminator) {
            this.tokenisationMode = tokenisationMode;
            this.latexMode = latexMode;
            this.startPosition = startPosition;
            this.terminator = terminator;
            this.tokens = new ArrayList<FlowToken>();
            this.foundTerminator = false;
        }
        
        /**
         * Gets the end index of the {@link FrozenSlice} corresponding to the last {@link Token}
         * recorded, returning the start position if nothing has been recorded.
         * This is useful for getting at the
         * "useful" content of a parse as it won't include the terminator.
         */
        public int computeLastTokenEndIndex() {
            if (tokens.isEmpty()) {
                return startPosition;
            }
            return tokens.get(tokens.size()-1).getSlice().endIndex;
        }
    }
    
    //-----------------------------------------
    
    public LaTeXTokeniser(final SessionContext sessionContext) {
        this.sessionContext = sessionContext;
        this.modeStack = new ArrayListStack<ModeState>();
        this.openEnvironmentStack = new ArrayListStack<String>();
    }
    
    /**  Resets the parsing state of this tokeniser. */
    public void reset() {
        workingDocument = null;
        position = 0;
        startTokenIndex = -1;
        modeStack.clear();
        currentModeState = null;
        openEnvironmentStack.clear();
    }
    
    /**
     * Tokenises the input specified by the given {@link SnuggleInputReader}, returning an
     * {@link ArgumentContainerToken} containing the roots of the parsed token tree.
     */
    public ArgumentContainerToken tokenise(final SnuggleInputReader reader)
            throws SnuggleParseException, IOException {
        /* Reset state (should already be clean) */
        reset();
        
        /* Create WorkingDocument for this input */
        this.workingDocument = reader.createWorkingDocument();
        
        /* Parse document in "top level" mode */
        try {
            ModeState topLevelResult = tokeniseInNewState(TokenisationMode.TOP_LEVEL, null, LaTeXMode.PARAGRAPH);
            
            /* Check that all environments have been closed, recording a client error for each that is not */
            while (!openEnvironmentStack.isEmpty()) {
                topLevelResult.tokens.add(createError(CoreErrorCode.TTEE04, position, position,
                        openEnvironmentStack.pop()));
            }

            /* That's it! Simply reset state and return the tokens that have been accrued */
            return new ArgumentContainerToken(workingDocument.freezeSlice(0, workingDocument.length()),
                    LaTeXMode.PARAGRAPH, topLevelResult.tokens);
        }
        finally {
            /* Tidy up state for next run */
            reset();
        }
    }
    
    /**
     * Tokenises the {@link #workingDocument} from the current {@link #position} in the given
     * {@link LaTeXMode} using the newly provided {@link TokenisationMode} and terminator.
     * <p>
     * When this returns, {@link #position} will either be at the end of input or after the
     * provided terminator.
     * 
     * @param tokenisationMode
     * @param terminator
     * @throws SnuggleParseException
     */
    private ModeState tokeniseInNewState(final TokenisationMode tokenisationMode,
            final Terminator terminator, final LaTeXMode latexMode) throws SnuggleParseException {
        /* Create new parsing state */
        currentModeState = new ModeState(tokenisationMode, latexMode, position, terminator);
        modeStack.push(currentModeState);
        
        /* Parse in current state until exhaustion */
        FlowToken token;
        while ((token = readNextToken())!=null) {
            currentModeState.tokens.add(token);
        }
        
        /*
         * When parsing in VERBATIM Mode, if there was no real content before
         * the terminator (or end of input) then the list of resulting tokens will be empty. In
         * this case, it is more useful to add a token representing the empty content.
         */
        if (currentModeState.latexMode==LaTeXMode.VERBATIM && currentModeState.tokens.isEmpty()) {
            FrozenSlice emptySlice = workingDocument.freezeSlice(currentModeState.startPosition, currentModeState.startPosition);
            currentModeState.tokens.add(new SimpleToken(emptySlice, TokenType.VERBATIM_MODE_TEXT, LaTeXMode.VERBATIM, null));
        }
        
        /* Check that we got the required terminator */
        if (terminator!=null && !currentModeState.foundTerminator) {
            /* Error: Input ended before required terminator */
            currentModeState.tokens.add(createError(CoreErrorCode.TTEG00, position, position, terminator));
        }
        
        /* Revert to previous parsing state and return results of this parse */
        ModeState result = currentModeState;
        modeStack.pop(); /* Removes current entry from stack */
        currentModeState = modeStack.isEmpty() ? null : modeStack.peek(); /* Set current to previous state */
        return result;
    }
    
    /**
     * Reads the next token before the current terminator, returning null on end of input or
     * if the terminator is discovered.
     * <p>
     * The value of {@link #position} is advanced to just after the found token, or if no
     * token is found then it will be the end of input or after the terminator, whichever
     * is appropriate.
     * <p>
     * The caller should be prepared to handle the case where input ended before a required
     * terminator was discovered. In this case, a null will have been returned and
     * {@link ModeState#foundTerminator} will be false.
     * 
     * @throws SnuggleParseException
     * 
     * @return next token read in, or null if the input ended or we hit the terminator for the
     *   current {@link ModeState}. (You can test whether we hit a terminator by checking
     *   {@link ModeState#foundTerminator}.)
     */
    private FlowToken readNextToken() throws SnuggleParseException {
//        /* Uncomment these lines when debugging the tokeniser. This is often
//         * sufficient to work out what is going wrong... honest!
//         */
//        System.out.println("rNT: position=" + position
//                + ", length=" + workingDocument.length()
//                + ", tokMode=" + currentModeState.tokenisationMode
//                + ", latexMode=" + currentModeState.latexMode
//                + ", terminator=" + currentModeState.terminator
//                + ", modeStackSize=" + modeStack.size()
//                + ", envsOpen=" + openEnvironmentStack
//                + ", errorCount=" + sessionContext.getErrors().size()
//                + ", remainder='" + workingDocument.extract(position, Math.min(position+20, workingDocument.length())) + "'");
        
        /* In MATH Mode, we skip over any leading whitespace and comments; in TEXT modes we skip
         * over any comments */
        if (currentModeState.latexMode==LaTeXMode.MATH) {
            skipOverCommentsAndWhitespace();
        }
        else if (currentModeState.latexMode!=LaTeXMode.VERBATIM) {
            skipOverComments();
        }
        
        /* See if we are at our required terminator or at the end of the document */
        if (currentModeState.terminator!=null) {
            int afterTerminator = currentModeState.terminator.matchesAt(workingDocument, position);
            if (afterTerminator!=-1) {
                /* Terminator found, so mark success and return null */
                position = afterTerminator;
                currentModeState.foundTerminator = true;
                return null;
            }
        }
        if (position==workingDocument.length()) {
            /* Natural end of document */
            return null;
        }
        
        /* Record position of the start of this token so that 'position' can be messed
         * about incrementally in the parsing methods below.
         */
        startTokenIndex = position;
        
        /* Now branch off according to the LaTeX mode we've currently in */
        FlowToken result;
        switch (currentModeState.latexMode) {
            case PARAGRAPH:
            case LR:
                result = readNextTokenTextMode();
                break;
                
            case MATH:
                result = readNextTokenMathMode();
                break;
                
            case VERBATIM:
                result = readNextTokenVerbatimMode();
                break;
                
            default:
                throw new SnuggleLogicException("Unexpected switch case " + currentModeState.latexMode);
        }
        
        /* Advance current position past this token, if we actually got something */
        if (result!=null) {
            position = result.getSlice().endIndex;
        }
        
        /* Return resulting token */
        return result;
    }
    
    /**
     * Makes a substitution within the working document, replacing the position of the
     * {@link WorkingDocument} from startIndex to endIndex with the given replacement
     * {@link CharSequence}.
     * <p>
     * The value of {@link #position} is updated to point to the start of the replacement
     * afterwards.
     * <p>
     * The given {@link SourceContext} provides optional contextual information about where
     * the substitution came from, if required.
     */
    private ErrorToken makeSubstitutionAndRewind(final int startIndex, final int endIndex,
            final CharSequence replacement) throws SnuggleParseException {
        int expansionLimit = sessionContext.getConfiguration().getExpansionLimit();
        if (expansionLimit>0 && workingDocument.getSubstitutionDepth(startIndex) >= expansionLimit) {
            /* Fail: Substitution limit exceeded (avoids infinite recursion) */
            return createError(CoreErrorCode.TTEU00, startIndex, endIndex,
                    Integer.valueOf(expansionLimit));
        }
        workingDocument.substitute(startIndex, endIndex, replacement);
        position = startIndex;
        return null;
    }
    
    /**
     * This reads the next token in {@link LaTeXMode#VERBATIM} Mode, which is "simply" a case
     * of pulling in everything until the next match of the required terminator.
     * This always returns exactly one {@link Token}.
     */
    private FlowToken readNextTokenVerbatimMode() {
        Terminator terminator = currentModeState.terminator;
        if (terminator==null) {
            throw new SnuggleLogicException("No terminator specified for VERBATIM Mode");
        }
        int endIndex = terminator.nextMatchFrom(workingDocument, startTokenIndex);
        if (endIndex==-1) {
            /* No terminator found, so pull in the rest of the document */
            endIndex = workingDocument.length();
        }

        FrozenSlice verbatimContentSlice = workingDocument.freezeSlice(startTokenIndex, endIndex);
        return new SimpleToken(verbatimContentSlice, TokenType.VERBATIM_MODE_TEXT, LaTeXMode.VERBATIM, null);
    }
    
    //-----------------------------------------
    // Tokenisation in MATH Mode
    
    private FlowToken readNextTokenMathMode() throws SnuggleParseException {
        /* Look at first non-whitespace character */
        int c = workingDocument.charAt(position);
        switch(c) {
            case -1:
                return null;
                
            case '\\':
                /* Macro or special characters. Read in macro name, look it up and then
                 * read in arguments and report back */
                return readSlashToken();
                
            case '{':
                /* Start of a region in braces */
                return readBraceRegion();
                
            case '%':
                /* Start of a comment. This should have been caught earlier */
                throw new SnuggleLogicException("Comment should be have been skipped before getting here!");
                
            case '&':
                /* 'Tab' character */
                return new SimpleToken(workingDocument.freezeSlice(position, position+1),
                        TokenType.TAB_CHARACTER, currentModeState.latexMode, null);
                
            case '#':
                /* Error: This is only allowed inside command/environment definitions */
                return createError(CoreErrorCode.TTEG04, position, position+1);
                
            case '$':
                /* Error: dollar sign inside Math mode */
                return createError(CoreErrorCode.TTEM04, position, position+1);
                
            default:
                /* Mathematical symbol, operator, number etc... */
                return readNextMathNumberOrSymbol();
        }
    }
    
    private FlowToken readNextMathNumberOrSymbol() {
        /* Let's see if we can reasonably parse a number */
        SimpleToken numberToken = tryReadMathNumber();
        if (numberToken!=null) {
            return numberToken;
        }
        
        /* If still here, then it wasn't a number. So we'll look again at the first non-whitespace
         * character */
        char c = (char) workingDocument.charAt(position);
        FrozenSlice thisCharSlice = workingDocument.freezeSlice(position, position+1);
        
        /* Is this a special math character? */
        EnumMap<InterpretationType, Interpretation> interpretationMap = Globals.getMathCharacterInterpretationMap(c);
        if (interpretationMap!=null) {
            return new SimpleToken(thisCharSlice,
                    TokenType.SINGLE_CHARACTER_MATH_SPECIAL,
                    LaTeXMode.MATH,
                    null, interpretationMap);
        }
        /* If still here, then we'll treat this as an identifier (e.g. 'x', 'y' etc.) */
        return new SimpleToken(thisCharSlice,
                TokenType.SINGLE_CHARACTER_MATH_IDENTIFIER,
                LaTeXMode.MATH,
                null,
                new MathIdentifierInterpretation(String.valueOf(c)));
    }
    
    /**
     * Attempts to read in a (positive) number at the current position, returning null if the input
     * doesn't look like a number.
     * 
     * @return SimpleToken representing the number, or null if input wasn't a number.
     */
    private SimpleToken tryReadMathNumber() {
        /* See if we can reasonably parse a number, returning null if we couldn't
         * or an appropriate token if we could.
         * 
         * TODO: Localisation! This is assuming the number is using '.' as decimal separator.
         * How does LaTeX do this?
         */
        int index = position; /* Current number search index */
        int c;
        boolean foundDigitsBeforeDecimalPoint = false;
        boolean foundDigitsAfterDecimalPoint  = false;
        boolean foundDecimalPoint = false;
        
        /* Read zero or more digits */
        while(true) {
            c = workingDocument.charAt(index);
            if (c>='0' && c<='9') {
                foundDigitsBeforeDecimalPoint = true;
                index++;
            }
            else {
                break;
            }
        }
        /* Maybe read decimal point */
        if (workingDocument.charAt(index)=='.') {
            /* Found leading decimal point, so only allow digits afterwards */
            foundDecimalPoint = true;
            index++;
        }
        /* Bail out if we didn't find a number before and didn't find a decimal point */
        if (!foundDigitsBeforeDecimalPoint && !foundDecimalPoint) {
            return null;
        }
        /* Read zero or more digits */
        while(true) {
            c = workingDocument.charAt(index);
            if (c>='0' && c<='9') {
                foundDigitsAfterDecimalPoint = true;
                index++;
            }
            else {
                break;
            }
        }
        /* Make sure we read in some number! */
        if (!foundDigitsBeforeDecimalPoint && !foundDigitsAfterDecimalPoint) {
            return null;
        }
        FrozenSlice numberSlice = workingDocument.freezeSlice(position, index);
        return new SimpleToken(numberSlice, TokenType.MATH_NUMBER, LaTeXMode.MATH,
                null,
                new MathNumberInterpretation(numberSlice.extract()));
    }
    
    //-----------------------------------------
    // Tokenisation in PARAGRAPH or LR mode
    
    private FlowToken readNextTokenTextMode() throws SnuggleParseException {
        /* Look at first character to decide what type of token we're going to read.
         * 
         * NB: If adding any trigger characters to the switch below, then you'll need to add
         * them to {@link #readNextSimpleTextParaMode()} as well to ensure that they terminate
         * regions of plain text as well.
         */
        int c = workingDocument.charAt(position);
        switch(c) {
            case -1:
                return null;
                
            case '\\':
                /* This is \command, \verb or an environment control */
                return readSlashToken();
                
            case '$':
                /* Math mode $ or $$ */
                return readDollarMath();
                
            case '{':
                /* Start of a region in braces */
                return readBraceRegion();
                
            case '%':
                /* Start of a comment. This should have been caught earlier */
                throw new SnuggleLogicException("Comment should be have been skipped before getting here!");
                
            case '&':
                /* 'Tab' character */
                return new SimpleToken(workingDocument.freezeSlice(position, position+1),
                        TokenType.TAB_CHARACTER, currentModeState.latexMode, null);
                
            case '_':
            case '^':
                /* Error: These are only allowed in MATH mode */
                return createError(CoreErrorCode.TTEM03, position, position+1);
                
            case '#':
                /* Error: This is only allowed inside command/environment definitions */
                return createError(CoreErrorCode.TTEG04, position, position+1);

            default:
                /* Plain text or some paragraph nonsense */
                return readNextSimpleTextParaMode();
        }
    }
    
    /**
     * Reads either the next text lump or "new paragraph" tokens, until the start of
     * something else.
     */
    private SimpleToken readNextSimpleTextParaMode() {
        /* Need to keep an eye out for 2 or more newlines, which signifies a new paragraph */
        SimpleToken result = null;
        int newLineCount = 0;
        int whitespaceStartIndex;
        int index, c;
        
        /* See if we start with blanks containing at least 2 newlines, consuming as many
         * newlines as we possibly can. */
        for (index=position; index<workingDocument.length(); index++) {
            c = workingDocument.charAt(index);
            if (c=='\n') {
                newLineCount++;
            }
            else if (!Character.isWhitespace(c)){
                break;
            }
        }
        if (newLineCount>=2) {
            /* We started with a paragraph break so return token. */
            return new SimpleToken(workingDocument.freezeSlice(position, index),
                    TokenType.NEW_PARAGRAPH, currentModeState.latexMode, TextFlowContext.ALLOW_INLINE);
        }
        /* If still here then it's normal text. Read until the start of something more interesting,
         * such as 2 newlines or a 'start' character */
        newLineCount = 0;
        whitespaceStartIndex = -1;
        for (index=position; index<workingDocument.length(); index++) {
            c = workingDocument.charAt(index);
            if (c=='\\' || c=='$' || c=='{' || c=='%' || c=='&' || c=='#' || c=='^' || c=='_') {
                break;
            }
            else if (currentModeState.terminator!=null && currentModeState.terminator.matchesAt(workingDocument, index)!=-1) {
                break;
            }
            else if (Character.isWhitespace(c)) {
                if (whitespaceStartIndex==-1) {
                    whitespaceStartIndex = index;
                }
                if (c=='\n') {
                    newLineCount++;
                    if (newLineCount==2) {
                        /* We've had 2 newlines amongst the last whitespace */
                        break;
                    }
                }
            }
            else {
                /* Normal text so turn off whitespace gathering */
                newLineCount=0;
                whitespaceStartIndex = -1;
            }
        }
        if (newLineCount==2) {
            /* Found a newline, return everything until the whitespace started. */
            result = new SimpleToken(workingDocument.freezeSlice(position, whitespaceStartIndex),
                    TokenType.TEXT_MODE_TEXT, currentModeState.latexMode, TextFlowContext.ALLOW_INLINE);
        }
        else {
            /* Just text */
            result = new SimpleToken(workingDocument.freezeSlice(position, index),
                    TokenType.TEXT_MODE_TEXT, currentModeState.latexMode, TextFlowContext.ALLOW_INLINE);
        }
        return result;
    }
    
    /**
     * Reads in a Math environment opened with <tt>$</tt> or <tt>$$</tt>.
     */
    private FlowToken readDollarMath() throws SnuggleParseException {
        /* Record current LaTeX mode and position */
        int openDollarPosition = position;
        LaTeXMode startLatexMode = currentModeState.latexMode;
        
        /* See if we are doing '$' or '$$' */
        boolean isDisplayMath = workingDocument.matchesAt(position, "$$");
        String delimiter = isDisplayMath ? "$$" : "$";
        
        /* Advance past the delimiter */
        position += delimiter.length();
        
        /* Now we parse this as an environment until we find the delimiter again. This works OK
         * with nested delimiters since they will always be the argument of a command, which
         * are tokenised separately so we don't have to worry.
         */
        int startContentIndex = position; /* And record this position as we're going to move on */
        ModeState contentResult = tokeniseInNewState(TokenisationMode.BUILTIN_ENVIRONMENT_CONTENT,
                new StringTerminator(delimiter), LaTeXMode.MATH);
        
        /* position now points just after the delimiter. */
        int endContentIndex = contentResult.foundTerminator ? position - delimiter.length() : position;
        
        /* Better also check that if the delimiter is '$' then we haven't ended up at '$$' */
        if (delimiter.equals("$") && workingDocument.charAt(position)=='$') {
            /* Error: $ ended by $$ */
            return createError(CoreErrorCode.TTEM01, position, position+1);
        }
        
        /* Right, that's it! */
        FrozenSlice contentSlice = workingDocument.freezeSlice(startContentIndex, endContentIndex);
        ArgumentContainerToken contentToken = new ArgumentContainerToken(contentSlice, LaTeXMode.MATH, contentResult.tokens);
        FrozenSlice environmentSlice = workingDocument.freezeSlice(openDollarPosition, position);
        BuiltinEnvironment environment = isDisplayMath ? CorePackageDefinitions.ENV_DISPLAYMATH : CorePackageDefinitions.ENV_MATH;
        return new EnvironmentToken(environmentSlice, startLatexMode, environment, contentToken);
    }
    
    /**
     * Reads the content of a region explicitly delimited inside <tt>{....}</tt>. This is the
     * simplest type of "mode change" in the tokenisation process so is a useful template in
     * understanding more complicated changes.
     */
    private BraceContainerToken readBraceRegion() throws SnuggleParseException {
        int openBraceIndex = position; /* Record position of '{' */
        LaTeXMode openLaTeXMode = currentModeState.latexMode; /* Record initial LaTeX mode */
        position++; /* Advance over the '{' */
        
        /* Go out and tokenise from this point onwards until the end of the '}' */
        ModeState result = tokeniseInNewState(TokenisationMode.BRACE, new StringTerminator("}"),
                currentModeState.latexMode);
        
        int endInnerIndex = result.foundTerminator ? position-1 : position;
        FrozenSlice braceOuterSlice = workingDocument.freezeSlice(openBraceIndex, position); /* Includes {...} */
        FrozenSlice braceInnerSlice = workingDocument.freezeSlice(openBraceIndex+1, endInnerIndex); /* Without {...} */
        ArgumentContainerToken braceContents = new ArgumentContainerToken(braceInnerSlice, openLaTeXMode, result.tokens);
        return new BraceContainerToken(braceOuterSlice, openLaTeXMode, braceContents);
    }
    
    /** 
     * Reads in a token starting with a <tt>\\</tt>
     */
    private FlowToken readSlashToken() throws SnuggleParseException {
        FlowToken result;
        int afterSlashIndex = position+1;
        int c = workingDocument.charAt(afterSlashIndex);
        if (c==-1) {
            /* Nothing following \\ */
            result = createError(CoreErrorCode.TTEG01, position, afterSlashIndex, currentModeState.latexMode);
        }
        else if (c=='(' || c=='[') {
            /* It's the start of a math environment specified using \( or \[ */
            if (currentModeState.latexMode==LaTeXMode.MATH) {
                /* Error: Already in Math mode - not allowed \( or \[ */
                result = createError(CoreErrorCode.TTEM00, position, afterSlashIndex);
            }
            else {
                int startCommandIndex = position;
                /* Advance over the delimiter and parse environment content */
                position += 2;
                int startContentIndex = position;
                String closer = (c=='(') ? "\\)" : "\\]";
                ModeState contentResult = tokeniseInNewState(TokenisationMode.BUILTIN_ENVIRONMENT_CONTENT,
                        new StringTerminator(closer), LaTeXMode.MATH);
                if (!contentResult.foundTerminator) {
                    /* Error: We didn't find the required terminator so we'll register an error. There
                     * are probably lots of other errors caused by the missing terminator so we'll
                     * add this error *first* in the list for the environment.
                     */
                    contentResult.tokens.add(0, createError(CoreErrorCode.TTEM02, startCommandIndex, position,
                            "\\" + Character.valueOf((char) c), closer));
                }
                int endContentIndex = contentResult.computeLastTokenEndIndex();
                FrozenSlice contentSlice = workingDocument.freezeSlice(startContentIndex, endContentIndex);
                FrozenSlice mathSlice = workingDocument.freezeSlice(startCommandIndex, position);
                ArgumentContainerToken contentToken = new ArgumentContainerToken(contentSlice, LaTeXMode.MATH, contentResult.tokens);
                BuiltinEnvironment environment = (c=='(') ? CorePackageDefinitions.ENV_MATH : CorePackageDefinitions.ENV_DISPLAYMATH;
                result = new EnvironmentToken(mathSlice, currentModeState.latexMode, environment, contentToken);
            }
        }
        else if (c==')' || c==']') {
            /* Close of a math environment specified using \) or \]. This should have
             * been discovered at the time the environment was opened (above), so this is
             * always an error if we get here.
             */
            result = createError(CoreErrorCode.TTEG03, position, position+2,
                    workingDocument.freezeSlice(position, position+2).extract());
        }
        else {
            /* It's not math, so must be \verb(*), \command or environment control */
            result = readCommandOrEnvironmentOrVerb();
        }
        return result;
    }
    
    //-----------------------------------------
    // Commands and Environments (this is by far the most complicated part of tokenisation!)
    
    /**
     * Reads in a command, <tt>\\verb(*)</tt> or environment control token.
     */
    private FlowToken readCommandOrEnvironmentOrVerb() throws SnuggleParseException {
        /* We are handling either:
         * 
         * 1. \command[opt]{arg}{...}
         * 2. \verb(*)...
         * 3. \begin{env}[opt]{arg}{...}...\end{env}
         * 
         * Get the first character after '\' - which the caller has already checked existence of -
         * and turn this into a command name. The name 'begin' indicates that this is the
         * start of an environment.
         */
        int startCommandNameIndex = position+1;
        String commandName = readCommandOrEnvironmentName(startCommandNameIndex);
        if (commandName==null) {
            /* The calling method should have picked this up */
            throw new SnuggleLogicException("Expected caller to have picked the commandName==null case up");
        }
        
        /* Advance over the command name */
        position += 1 + commandName.length();
        
        /* Convert whitespace-only command name to ' ', which allows a trailing '\' on a line
         * to be equivalent to '\ ' 
         */
        boolean isWhitespaceCommand = true;
        for (int i=0; i<commandName.length(); i++) {
            if (!Character.isWhitespace(commandName.charAt(i))) {
                isWhitespaceCommand = false;
                break;
            }
        }
        if (isWhitespaceCommand) {
            commandName = " ";
        }
        
        /* Now see if we're doing a command or an environment */
        FlowToken result = null;
        if (commandName.equals("begin")) {
            /* It's the start of a built-in or user-defined environment. */
            result = finishBeginEnvironment();
        }
        else if (commandName.equals("end")) {
            /* It's the end of a built-in or user-defined environment */
            result = finishEndEnvironment();
        }
        else if (commandName.equals(UDE_POST_BEGIN)) {
            /* Internal only! */
            result = handleUserDefinedEnvironmentControl();
        }
        else if (commandName.equals(CorePackageDefinitions.CMD_VERB.getTeXName())) {
            /* It's \\verb... which needs special parsing */
            result = finishVerbToken(CorePackageDefinitions.CMD_VERB);
        }
        else if (commandName.equals(CorePackageDefinitions.CMD_VERBSTAR.getTeXName())) {
            /* Same with \\verb* */
            result = finishVerbToken(CorePackageDefinitions.CMD_VERBSTAR);
        }
        else {
            /* It's a built-in or user-defined command. */
            result = finishCommand(commandName);
        }
        return result;
    }
    
    /**
     * Helper to read in the command or environment name starting at the given index, obeying the
     * esoteric rules of naming.
     * <p>
     * Note that this might return '(', ')', '[' and ']' which are considered special by the
     * parsing process so may not be re-defined.
     * <p>
     * This DOES NOT change position or any other component of the current parsing state.
     * 
     * @param startCommandNameIndex index to start reading from.
     * @return command name String, which will contain at least one character, or null if there
     *   were no further characters in the Slice.
     */
    private String readCommandOrEnvironmentName(final int startCommandNameIndex) {
        int index = startCommandNameIndex;
        int c = workingDocument.charAt(index);
        String commandName;
        if (c==-1) {
            /* Nothing to read! */
            commandName = null;
        }
        else if (!((c>='a' && c<='z') || (c>='A' && c<='Z'))) {
            /* Funny symbols are always exactly one character, which may be whitespace and include
             * reserved characters (,),[ or ].
             */
            commandName = Character.toString((char) c);
        }
        else {
            /* 1 or more alphanumeric characters, followed by an optional star */
            index++;
            while (true) {
                c = workingDocument.charAt(index);
                if (c>='a' && c<='z' || c>='A' && c<='Z') {
                    index++;
                    continue;
                }
                else if (c=='*') {
                    index++;
                    break;
                }
                else {
                    break;
                }
            }
            commandName = workingDocument.extract(startCommandNameIndex, index).toString();
        }
        return commandName;
    }
    
    /**
     * This is called once it has become clear that the next token is <tt>\verb</tt>
     * or <tt>\verb*</tt>.
     * <p>
     * As with LaTeX, this next character is used to delimit the verbatim region, which must
     * end on the same line. No whitespace can occur after \verb.
     * <p>
     * PRE-CONDITION: position will point to the character immediately after <tt>\verb</tt>.
     * 
     * @throws SnuggleParseException 
     */
    private FlowToken finishVerbToken(BuiltinCommand verbCommand) throws SnuggleParseException {
        /* Get the character immediately after the \verb or \verb* command - whitespace not allowed */
        int startDelimitIndex = position;
        int delimitChar = workingDocument.charAt(startDelimitIndex);
        if (delimitChar==-1) {
            /* Error: Could not find delimiter */
            return createError(CoreErrorCode.TTEV00, startTokenIndex, startDelimitIndex);
        }
        else if (Character.isWhitespace(delimitChar)) {
            /* Error: delimiter is whitespace */
            return createError(CoreErrorCode.TTEV00, startTokenIndex, startDelimitIndex+1);
        }
        /* Now parse from the character after the delimiter until the next instance of
         * the delimiter is found. This should result in zero or one content tokens, plus
         * a possible error token if the end delimiter was not found.
         */
        position++; /* Advance over delimiter */
        ModeState contentState = tokeniseInNewState(TokenisationMode.BUILTIN_COMMAND_ARGUMENT,
                new StringTerminator(Character.toString((char) delimitChar)), LaTeXMode.VERBATIM);
        List<FlowToken> contentTokens = contentState.tokens;
        SimpleToken verbatimContentToken = null;
        boolean endDelimiterNotFound = false;
        for (FlowToken resultToken : contentTokens) {
            if (resultToken.getType()==TokenType.VERBATIM_MODE_TEXT && verbatimContentToken==null) {
                /* Got content */
                verbatimContentToken = (SimpleToken) resultToken;
            }
            else if (resultToken.getType()==TokenType.ERROR) {
                /* Only error we expect is if delimiter is not found */
                if (((ErrorToken) resultToken).getError().getErrorCode()==CoreErrorCode.TTEG00) {
                    endDelimiterNotFound = true;
                }
                else {
                    throw new SnuggleLogicException("Unexpected error when parsing \\verb content: " + resultToken);
                }
            }
            else {
                throw new SnuggleLogicException("Unexpected token when examining \\verb content: " + resultToken);
            }
        }
        if (verbatimContentToken==null) {
            /* This shouldn't have happened as we're guaranteed to get something! */
            throw new SnuggleLogicException("\\verb had no proper content token");
        }
        
        /* Recall that the content of \\verb must be on a single line, so make sure that is the
         * case.
         */
        FrozenSlice contentSlice = verbatimContentToken.getSlice();
        int newlineIndex = workingDocument.indexOf(contentSlice.startIndex, '\n');
        if (newlineIndex!=-1 && newlineIndex<contentSlice.endIndex) {
            /* Error: Line ended before end of \\verb content */
            return createError(CoreErrorCode.TTEV01, startTokenIndex,
                    contentSlice.endIndex + (endDelimiterNotFound ? 0 : 1));
        }
        
        /* That's it! */
        FrozenSlice verbatimSlice = workingDocument.freezeSlice(startTokenIndex, position);
        return new CommandToken(verbatimSlice, currentModeState.latexMode, verbCommand,
                null,
                new ArgumentContainerToken[] {
                    new ArgumentContainerToken(contentSlice, LaTeXMode.VERBATIM, contentTokens)
                });
    }
    
    //-----------------------------------------
    // Commands
    
    /**
     * Finishes reading a Command, which will either be a {@link BuiltinCommand} or a
     * {@link UserDefinedCommand}.
     * <p>
     * PRE-CONDITION: position will point to the character immediately after <tt>\commandName</tt>.
     * 
     * @param commandName name of the command being read in
     */
    private FlowToken finishCommand(final String commandName) throws SnuggleParseException {
        /* First resolve the command as either a user-defined or built-in. We search
         * for user-defined commands first, and then built-ins.
         */
        FlowToken result = null;
        UserDefinedCommand userCommand = sessionContext.getUserCommandMap().get(commandName);
        if (userCommand!=null) {
            result = finishUserDefinedCommand(userCommand);
        }
        else {
            BuiltinCommand builtinCommand = sessionContext.getBuiltinCommandByTeXName(commandName);
            if (builtinCommand!=null) {
                result = finishBuiltinCommand(builtinCommand);
            }
            else {
                /* Undefined command */
                result = createError(CoreErrorCode.TTEC00, startTokenIndex, position, commandName);
            }
        }
        return result;
    }

    /** 
     * Finishes reading in a {@link BuiltinCommand}, catering for the different types of
     * those commands.
     * <p>
     * PRE-CONDITION: position will point to the character immediately after <tt>\commandName</tt>.
     *  
     * @throws SnuggleParseException
     */
    private FlowToken finishBuiltinCommand(final BuiltinCommand command)
            throws SnuggleParseException {
        /* Make sure we can use this command in the current mode */
        if (!command.getAllowedModes().contains(currentModeState.latexMode)) {
            /* Not allowed to use this command in this mode */
            return createError(CoreErrorCode.TTEC01, startTokenIndex, position,
                    command.getTeXName(), currentModeState.latexMode);
        }
        
        /* Command and environment definitions need to be handled specifically as their structure is quite
         * specific
         */
        if (command==CorePackageDefinitions.CMD_NEWCOMMAND || command==CorePackageDefinitions.CMD_RENEWCOMMAND) {
            return finishCommandDefinition(command);
        }
        if (command==CorePackageDefinitions.CMD_NEWENVIRONMENT || command==CorePackageDefinitions.CMD_RENEWENVIRONMENT) {
            return finishEnvironmentDefinition(command);
        }

        /* All other commands are handled according to their type */
        switch (command.getType()) {
            case SIMPLE:
                /* Not expecting any more to read so bail out now */
                return finishSimpleCommand(command);
                
            case COMBINER:
                /* Read in next token and combine up */
                return finishCombiningCommand(command);
                
            case COMPLEX:
                /* Read arguments */
                return finishComplexCommand(command);
                
            default:
                throw new SnuggleLogicException("Unexpected switch case " + command.getType());
        }
    }
    
    /**
     * Finishes the reading of a simple command. These include "funny" (i.e. single character
     * non-alphanumeric) commands which leave trailing whitespace intact so we need to be
     * a little bit careful here.
     * <p>
     * PRE-CONDITION: position will point to the character immediately after <tt>\commandName</tt>.
     */
    private FlowToken finishSimpleCommand(final BuiltinCommand command) {
        /* Work out the next significant index after the command:
         * single non-alpha (=funny) commands do not eat up trailing whitespace;
         * all other commands do.
         */
        boolean isFunnyCommand = false;
        String commandName = command.getTeXName();
        if (commandName.length()==1) {
            char c = commandName.charAt(0);
            isFunnyCommand = !((c>='a' && c<='z') || (c>='A' && c<='Z'));
        }
        if (!isFunnyCommand) {
            skipOverTrailingWhitespace();
        }
        return new CommandToken(workingDocument.freezeSlice(startTokenIndex, position),
                currentModeState.latexMode, command);
    }

    /** 
     * Deals with pulling in the next token after something like <tt>\not</tt>
     * <p>
     * PRE-CONDITION: position will point to the character immediately after <tt>\commandName</tt>.
     *  
     * @throws SnuggleParseException
     */
    private FlowToken finishCombiningCommand(final BuiltinCommand command) throws SnuggleParseException {
        /* We always skip trailing comments/whitespace for these types of commands */
        skipOverCommentsAndWhitespace();
        int afterWhitespaceIndex = position;
        
        /* Record index of the start of this command since we're going to read in the next token */
        int startCommandIndex = startTokenIndex;
        
        /* Read in the next token */
        FlowToken nextToken = readNextToken();
        if (nextToken==null) {
            /* Could not find target for this combiner */
            return createError(CoreErrorCode.TTEC03, startTokenIndex, afterWhitespaceIndex,
                    command.getTeXName());
        }
        /* Make sure this next token is allowed to be combined with this one */
        if (!command.getCombinerTargetMatcher().isAllowed(nextToken)) {
            /* Inappropriate combiner target */
            return createError(CoreErrorCode.TTEC04, startTokenIndex, nextToken.getSlice().endIndex,
                    command.getTeXName());
        }
        /* Create combined token spanning the two "raw" tokens */
        return new CommandToken(workingDocument.freezeSlice(startCommandIndex, nextToken.getSlice().endIndex),
                currentModeState.latexMode, command, nextToken);
    }
    
    /**
     * Finishes the process or reading in a "complex" command, by searching for any required
     * and optional arguments.
     * <p>
     * PRE-CONDITION: position will point to the character immediately after <tt>\commandName</tt>.
     */
    private FlowToken finishComplexCommand(final BuiltinCommand command) throws SnuggleParseException {
        int startCommandIndex = startTokenIndex; /* Record this as we'll be parsing following tokens */
        
        /* Read in and tokenise arguments, passing a "struct" to store the results in.
         * I've done it this way as this process is used in a number of different places but
         * we still need to be able to return an error if required.
         * 
         * We preserve trailing whitespace after these types of commands.
         */
        BuiltinCommandOrEnvironmentArgumentSearchResult argumentSearchResult = new BuiltinCommandOrEnvironmentArgumentSearchResult();
        ErrorToken errorToken = advanceOverBuiltinCommandOrEnvironmentArguments(command, argumentSearchResult);
        if (errorToken!=null) {
            return errorToken;
        }

        /* That's it! */
        FrozenSlice commandSlice = workingDocument.freezeSlice(startCommandIndex, position);
        return new CommandToken(commandSlice, currentModeState.latexMode, command,
                argumentSearchResult.optionalArgument,
                argumentSearchResult.requiredArguments);
    }
    
    /**
     * Trivial "struct" Object to hold the results of searching for command and/or environment
     * arguments.
     * 
     * @see LaTeXTokeniser#advanceOverBuiltinCommandOrEnvironmentArguments(CommandOrEnvironment, BuiltinCommandOrEnvironmentArgumentSearchResult)
     */
    static class BuiltinCommandOrEnvironmentArgumentSearchResult {
        
        /** Tokenised version of optional argument, null if not supported or not requested. */
        public ArgumentContainerToken optionalArgument;
        
        /** 
         * Tokenised versions of required arguments, will include null entries for arguments
         * where tokenisation was not asked for and did not occur.
         */
        public ArgumentContainerToken[] requiredArguments;
    }
    
    /**
     * This helper reads in the optional and required arguments for a Command or Environment,
     * starting at the current position.
     * <p>
     * Trailing whitespace is always preserved after the command/environment.
     * <p>
     * PRE-CONDITION: position will point to the character immediately after <tt>\commandName</tt>.
     * POST-CONDITION: position will point to immediately after the last argument.
     * 
     * @param commandOrEnvironment
     * @param result blank result Object that will be filled in by this method.
     * 
     * @return ErrorToken if parsing failed, null otherwise.
     * @throws SnuggleParseException
     */
    private ErrorToken advanceOverBuiltinCommandOrEnvironmentArguments(final CommandOrEnvironment commandOrEnvironment,
            final BuiltinCommandOrEnvironmentArgumentSearchResult result) throws SnuggleParseException {
        /* First of all see if we're expecting arguments and bail if not */
        if (commandOrEnvironment.getArgumentCount()==0 && !commandOrEnvironment.isAllowingOptionalArgument()) {
            result.optionalArgument = null;
            result.requiredArguments = ArgumentContainerToken.EMPTY_ARRAY;
            return null;
        }
        
        /* If still here, we're expecting arguments so skip any comments/whitespace before first argument */
        skipOverCommentsAndWhitespace();
        
        /* Consider optional argument, if allowed */
        ArgumentContainerToken optionalArgument = null;
        FrozenSlice optionalArgumentSlice = null;
        int c;
        int argumentIndex = 0;
        LaTeXMode argumentMode;
        ModeState argumentResult;
        if (commandOrEnvironment.isAllowingOptionalArgument()) {
            /* Decide what mode we'll be parsing this argument in, defaulting to current mode if
             * nothing is specified */
            argumentMode = commandOrEnvironment.getArgumentMode(argumentIndex++);
            if (argumentMode==null) {
                argumentMode = currentModeState.latexMode;
            }
            /* Now handle optional argument, if provided */
            c = workingDocument.charAt(position);
            if (c=='[') {
                int startArgumentContentIndex = ++position; /* Skip over '[' */
                
                /* Go out and tokenise from this point onwards until the end of the ']' */
                argumentResult = tokeniseInNewState(TokenisationMode.BUILTIN_COMMAND_ARGUMENT,
                        new StringTerminator("]"), argumentMode);
                int endArgumentContentIndex = (argumentResult.foundTerminator) ? position-1 : position;
                optionalArgumentSlice = workingDocument.freezeSlice(startArgumentContentIndex, endArgumentContentIndex);
                optionalArgument = new ArgumentContainerToken(optionalArgumentSlice, argumentMode, argumentResult.tokens);
            }
        }
        
        /* Look for required arguments.
         * 
         * These must all be specified using {....}
         * 
         * HOWEVER: If the command takes no 1 argument and no optional argument has been provided,
         * then LaTeX allows the next single token to be taken as the argument if it is not a brace.
         * 
         * E.g. \sqrt x is interpreted the same way as \sqrt{x}
         * 
         * We will allow that behaviour here.
         * 
         * TODO: Maybe make this optional?
         * 
         * Note that \sqrt xy is interpreted as \sqrt{x}y !!!
         */
        int argCount = commandOrEnvironment.getArgumentCount();
        ArgumentContainerToken[] requiredArguments = new ArgumentContainerToken[argCount];
        FrozenSlice[] requiredArgumentSlices = new FrozenSlice[argCount];
        for (int i=0; i<argCount; i++) {
            /* Skip any comments/whitespace before this argument */
            skipOverCommentsAndWhitespace();
            
            /* Decide which parsing mode to use for this argument, using current if none specified */
            argumentMode = commandOrEnvironment.getArgumentMode(argumentIndex++);
            if (argumentMode==null) {
                argumentMode = currentModeState.latexMode;
            }
            
            /* Now look for this required argument */
            c = workingDocument.charAt(position);
            if (c=='{') {
                int startArgumentContentIndex = ++position; /* Skip over '{' */
                
                /* Go out and tokenise from this point onwards until the end of the '}' */
                argumentResult = tokeniseInNewState(TokenisationMode.BUILTIN_COMMAND_ARGUMENT,
                        new StringTerminator("}"), argumentMode);
                int endArgumentContentIndex = argumentResult.foundTerminator ? position-1 : position;
                requiredArgumentSlices[i] = workingDocument.freezeSlice(startArgumentContentIndex, endArgumentContentIndex);
                requiredArguments[i] = new ArgumentContainerToken(requiredArgumentSlices[i], argumentMode, argumentResult.tokens);
            }
            else if (c!=-1 && i==0 && argCount==1 && optionalArgument==null && commandOrEnvironment instanceof Command) {
                /* Special case listed above: command with 1 argument as a single token with no braces.
                 * Temporarily change LaTeX mode to that of the argument, pull in the next
                 * token and revert the mode to what we had initially.
                 */
                LaTeXMode currentLaTeXMode = currentModeState.latexMode;
                currentModeState.latexMode = argumentMode;
                FlowToken nextToken = readNextToken();
                currentModeState.latexMode = currentLaTeXMode;
                if (nextToken!=null) {
                    requiredArguments[i] = ArgumentContainerToken.createFromSingleToken(argumentMode, nextToken);
                    requiredArgumentSlices[i] = requiredArguments[i].getSlice();
                }
                else {
                    /* Error: Missing first (and only) argument. */
                    return createError(CoreErrorCode.TTEC02, startTokenIndex, position,
                            commandOrEnvironment.getTeXName(), Integer.valueOf(1));
                }
            }
            else {
                /* Error: Missing '#n' argument (where n=i+1).
                 * (There is one variant of this for commands and one for environments)
                 */
                return createError(
                        (commandOrEnvironment instanceof Command) ? CoreErrorCode.TTEC02 : CoreErrorCode.TTEE06,
                        startTokenIndex, position,
                        commandOrEnvironment.getTeXName(), Integer.valueOf(i+1));
            }
        }
        
        /* Fill in result and return null indicating success */
        result.optionalArgument = optionalArgument;
        result.requiredArguments = requiredArguments;
        return null;
    }
    
    /**
     * Finishes the process of reading in and evaluating a {@link UserDefinedCommand}.
     * <p>
     * PRE-CONDITION: position will point to the character immediately after <tt>\commandName</tt>.
     */
    private FlowToken finishUserDefinedCommand(final UserDefinedCommand command)
            throws SnuggleParseException {
        /* Read in argument as raw Slices, since we're going to perform parameter
         * interpolation on them.
         * 
         * We also KEEP the final trailing whitespace after the last part of the
         * command so that when it is substituted, there is whitespace left for further parsing.
         */
        UserDefinedCommandOrEnvironmentArgumentSearchResult argumentSearchResult = new UserDefinedCommandOrEnvironmentArgumentSearchResult();
        ErrorToken errorToken = advanceOverUserDefinedCommandOrEnvironmentArguments(command, argumentSearchResult);
        if (errorToken!=null) {
            return errorToken;
        }
        
        /* Right... what we now do is create a temporary document with the place-holders in the
         * command definition replaced with the arguments. This is then tokenised along with
         * enough of the rest of the existing document to ensure tokens are correctly balanced
         * or finished, and the resulting token becomes the final result. Phew!
         */
        String replacement = substituteArguments(command.getDefinitionSlice(), command, argumentSearchResult);
        
        /* Now we rewind to the start of the command and replace it with our substitution, and
         * then continue parsing as normal.
         */
        int afterCommandIndex = position;
        errorToken = makeSubstitutionAndRewind(startTokenIndex, afterCommandIndex, replacement);
        return errorToken==null ? readNextToken() : errorToken;
    }
    
    /**
     * This helper substitutes the arguments provided for a user-defined command/environment
     * into the content of the given {@link FrozenSlice}, handling correctly escaped arguments
     * such as <tt>\#1</tt>.
     * <p>
     * As with built-in commands, the arguments are substituted into occurrences of #n, which
     * denotes the n'th argument (with the optional argument counting first, if applicable).
     */
    private String substituteArguments(final FrozenSlice slice, final UserDefinedCommandOrEnvironment commandOrEnvironment,
            final UserDefinedCommandOrEnvironmentArgumentSearchResult argumentSearchResult) {
        boolean inEscape = false; /* Whether we are in the middle of a character escape */
        boolean inArgument = false; /* Whether we are inside an argument */
        int index, argumentIndex;
        char c;
        StringBuilder substitutionBuilder = new StringBuilder();
        WorkingDocument sliceDocument = slice.getDocument();
        for (index=slice.startIndex; index<slice.endIndex; index++) {
            c = (char) sliceDocument.charAt(index);
            if (!inEscape && c=='\\') {
                inEscape = true;
                substitutionBuilder.append(c);
            }
            else if (inEscape) {
                inEscape = false;
                substitutionBuilder.append(c);
            }
            else if (c=='#') {
                inArgument = true; /* (Will get actual argument on next iteration) */
            }
            else if (inArgument) {
                /* NB: The argument will already have been checked for sanity */
                inArgument = false;
                argumentIndex = c-'0';
                if (commandOrEnvironment.isAllowingOptionalArgument()) {
                    argumentIndex--;
                }
                if (argumentIndex>0) {
                    /* Required argument */
                    substitutionBuilder.append(argumentSearchResult.requiredArguments[argumentIndex-1]);
                }
                else {
                    /* Optional argument */
                    substitutionBuilder.append(argumentSearchResult.optionalArgument!=null ? argumentSearchResult.optionalArgument : commandOrEnvironment.getOptionalArgument());
                }
            }
            else {
                substitutionBuilder.append(c);
            }
        }
        return substitutionBuilder.toString();
    }
    
    /**
     * Trivial "struct" Object to hold the results of searching for user-defined
     * command and/or environment arguments, which are initially treated as unparsed
     * but balanced {@link WorkingDocument}s.
     */
    static class UserDefinedCommandOrEnvironmentArgumentSearchResult {
        
        /** Optional argument, null if not provided or not supported. */
        public CharSequence optionalArgument;
        
        /** Required arguments */
        public CharSequence[] requiredArguments;
    }
    
    /**
     * This helper reads in the optional and required arguments for a Command or Environment,
     * starting at the current position.
     * <p>
     * The value of {@link #position} will be updated by this.
     * <p>
     * Trailing whitespace is always preserved after the command/environment.
     * <p>
     * PRE-CONDITION: position will point to the character immediately after <tt>\commandName</tt>.
     * POST-CONDITION: position will point to immediately after the last argument.
     * 
     * @param commandOrEnvironment
     * @param result blank result Object that will be filled in by this method.
     * 
     * @return ErrorToken if parsing failed, null otherwise.
     * @throws SnuggleParseException
     */
    private ErrorToken advanceOverUserDefinedCommandOrEnvironmentArguments(final CommandOrEnvironment commandOrEnvironment,
            UserDefinedCommandOrEnvironmentArgumentSearchResult result) throws SnuggleParseException {
        /* First of all see if we're expecting arguments and bail if not */
        if (commandOrEnvironment.getArgumentCount()==0 && !commandOrEnvironment.isAllowingOptionalArgument()) {
            result.optionalArgument = null;
            result.requiredArguments = new CharSequence[0];
            return null;
        }
        
        /* Skip any whitespace before arguments */
        skipOverCommentsAndWhitespace();
        
        /* Consider optional argument, if allowed */
        int c;
        CharSequence optionalArgument = null;
        if (commandOrEnvironment.isAllowingOptionalArgument()) {
            /* Now handle optional argument, if provided */
            c = workingDocument.charAt(position);
            if (c=='[') {
                int openBracketIndex = position;
                int closeBracketIndex = findEndSquareBrackets(openBracketIndex);
                if (closeBracketIndex==-1) {
                    /* Error: no matching ']' */
                    return createError(CoreErrorCode.TTEG00, startTokenIndex, workingDocument.length(), ']');
                }
                optionalArgument = workingDocument.extract(openBracketIndex+1, closeBracketIndex);
                position = closeBracketIndex + 1; /* Move past ']' */
            }
        }
        
        /* Look for required arguments in the same way that we do it for built-in commands and
         * environments.
         */
        int argCount = commandOrEnvironment.getArgumentCount();
        CharSequence[] requiredArguments = new CharSequence[argCount];
        for (int i=0; i<argCount; i++) {
            /* Skip any comments/whitespace before this argument */
            skipOverCommentsAndWhitespace();
            
            /* Now look for argument */
            c = workingDocument.charAt(position);
            if (c=='{') {
                int openBraceIndex = position;
                int closeBraceIndex = findEndCurlyBrackets(openBraceIndex);
                if (closeBraceIndex==-1) {
                    /* Error: no matching '}' */
                    return createError(CoreErrorCode.TTEG00, startTokenIndex, workingDocument.length(), '}');
                }
                requiredArguments[i] = workingDocument.extract(openBraceIndex+1, closeBraceIndex);
                position = closeBraceIndex + 1; /* Move past '}' */
            }
            else if (c!=-1 && i==0 && argCount==1 && result.optionalArgument==null) {
                /* Special case listed above: 1 argument as a single token with no braces.
                 * 
                 * NOTE: This one is slightly complicated here in that we need to read the next token
                 * in now, which has an unwelcome side-effect of freezing the working document up
                 * to the end of this token. Since all we want is the content of this token, we
                 * "unfreeze" the working document back to the start of the current command.
                 */
                FlowToken nextToken = readNextToken();
                if (nextToken==null) {
                    /* Error: Missing first (and only) argument. */
                    return createError(CoreErrorCode.TTEC02, startTokenIndex, position,
                            commandOrEnvironment.getTeXName(), Integer.valueOf(1));
                }
                FrozenSlice nextSlice = nextToken.getSlice();
                requiredArguments[i] = nextSlice.extract();
                workingDocument.unfreeze(startTokenIndex); /* (nextSlice is now invalid!) */
            }
            else {
                /* Error: Missing '#n' argument (where n=i+1) */
                return createError(commandOrEnvironment instanceof Command ? CoreErrorCode.TTEC02 : CoreErrorCode.TTEE06,
                        startTokenIndex, position,
                        commandOrEnvironment.getTeXName(), Integer.valueOf(i+1));
            }
        }
        /* Record result and return null indicating success */
        result.optionalArgument = optionalArgument;
        result.requiredArguments = requiredArguments;
        return null;
    }
    
    //-----------------------------------------
    // Environments
    
    /**
     * Handles <tt>\\begin...</tt>
     * <p>
     * POST-CONDITION: position will point to the character immediately after <tt>\\begin</tt>.
     */
    private FlowToken finishBeginEnvironment() throws SnuggleParseException {
        /* Read {envName} */
        String environmentName = advanceOverBracesAndEnvironmentName();
        if (environmentName==null) {
            /* Expected to find {envName} */
            return createError(CoreErrorCode.TTEE01, startTokenIndex, position);
        }
        
        /* Look up environment, taking user-defined on in preference to built-in */
        UserDefinedEnvironment userEnvironment = sessionContext.getUserEnvironmentMap().get(environmentName);
        FlowToken result = null;
        if (userEnvironment!=null) {
            result = finishBeginUserDefinedEnvironment(userEnvironment);
        }
        else {
            BuiltinEnvironment builtinEnvironment = sessionContext.getBuiltinEnvironmentByTeXName(environmentName);
            if (builtinEnvironment!=null) {
                result = finishBeginBuiltinEnvironment(builtinEnvironment);
                
            }
            else {
                /* Undefined environment name */
                result = createError(CoreErrorCode.TTEE02, startTokenIndex, position, environmentName);
            }
        }
        return result;
    }
    
    /**
     * Handles <tt>\\end...</tt>
     * <p>
     * PRE-CONDITION: position will point to the character immediately after <tt>\\end</tt>.
     */
    private FlowToken finishEndEnvironment() throws SnuggleParseException {
        /* Read {envName} */
        String environmentName = advanceOverBracesAndEnvironmentName();
        if (environmentName==null) {
            /* Expected to find {envName} */
            return createError(CoreErrorCode.TTEE01, startTokenIndex, position);
        }
        
        /* First, we make sure this balances with what is open */
        String lastOpenName = openEnvironmentStack.isEmpty() ? null : openEnvironmentStack.peek();
        if (lastOpenName==null) {
            /* No environments are open */
            return createError(CoreErrorCode.TTEE05, startTokenIndex, position);
        }
        else if (!environmentName.equals(lastOpenName)) {
            /* Got end of envName, rather than one in the stack */
            return createError(CoreErrorCode.TTEE00, startTokenIndex, position,
                    environmentName, lastOpenName);

        }
        else {
            openEnvironmentStack.pop();
        }
        
        /* Look up environment, taking user-defined on in preference to built-in */
        UserDefinedEnvironment userEnvironment = sessionContext.getUserEnvironmentMap().get(environmentName);
        FlowToken result = null;
        if (userEnvironment!=null) {
            result = finishEndUserDefinedEnvironment(userEnvironment);
        }
        else {
            BuiltinEnvironment builtinEnvironment = sessionContext.getBuiltinEnvironmentByTeXName(environmentName);
            if (builtinEnvironment!=null) {
                /* The end of a build-in environment is special as the \\begin{env} will have
                 * started a new tokenisation level. We simply return null to indicate there's
                 * no more at this level. 
                 */
            }
            else {
                /* Undefined environment name */
                result = createError(CoreErrorCode.TTEE02, startTokenIndex, position, environmentName);
            }
        }
        return result;
    }
    
    /**
     * Reads text of the form '{envName}' from the current position onwards, skipping any leading
     * comments/whitespace and advancing the current position to just after the final '}'.
     * <p>
     * Returns null if this information was not found.
     * <p>
     * PRE-CONDITION: position will point to the character immediately after <tt>\\begin</tt> or <tt>\\end</tt>
     * POST-CONDITION: position will point to just after the <tt>{envName}</tt>, if found
     */
    private String advanceOverBracesAndEnvironmentName() {
        /* Skip leading comments/whitespace */
        skipOverCommentsAndWhitespace();
        
        /* Read in name of environment and move beyond it */
        if (workingDocument.charAt(position)!='{') {
            return null;
        }
        String environmentName = readCommandOrEnvironmentName(++position);
        position += environmentName.length(); /* Move after the name, to hopefully '}' */
        if (workingDocument.charAt(position)!='}') {
            /* Expected to find {envName} */
            return null;
        }
        position++; /* Move after the '}' */
        return environmentName;
    }
    
    //------------------------------------------------------
    // Built-in Environments
    
    /**
     * Finishes off reading a built-in environment.
     * <p>
     * PRE-CONDITION: position will point to the character immediately after <tt>\\begin{envName}</tt>.
     * 
     * @param environment environment being read in
     */
    private FlowToken finishBeginBuiltinEnvironment(final BuiltinEnvironment environment) throws SnuggleParseException {
        /* Record that this environment has opened */
        openEnvironmentStack.push(environment.getTeXName());
        
        /* Record where the \\begin token starts as we're going to advance */
        int startEnvironmentIndex = startTokenIndex;
        
        /* Check whether we can use this environment in this mode.
         * 
         * We won't bail immediately on error here as it is prudent to pull in the content
         * anyway to ensure things match up */
        LaTeXMode startLatexMode = currentModeState.latexMode;
        ErrorToken errorToken = null;
        if (!environment.getAllowedModes().contains(currentModeState.latexMode)) {
            /* Error: this environment can't be used in the current mode */
            errorToken = createError(CoreErrorCode.TTEE03, startTokenIndex, position,
                    environment.getTeXName(), startLatexMode);
        }
        
        /* Read in arguments, the same way that we do with commands. */
        BuiltinCommandOrEnvironmentArgumentSearchResult argumentSearchResult = new BuiltinCommandOrEnvironmentArgumentSearchResult();
        ErrorToken argumentErrorToken = advanceOverBuiltinCommandOrEnvironmentArguments(environment, argumentSearchResult);
        if (argumentErrorToken!=null && errorToken==null) {
            errorToken = argumentErrorToken;
        }
        
        /* Work out what mode we're going to parse the content in */
        LaTeXMode contentMode = environment.getContentMode();
        if (contentMode==null) {
            contentMode = currentModeState.latexMode;
        }
        
        /* We now pull in the content. The non-VERBATIM and VERBATIM cases differ here */
        ArgumentContainerToken contentToken;
        if (contentMode==LaTeXMode.VERBATIM) {
            /* Parse looking for the explicit \\end{envName} terminator. Because whitespace is
             * allowed, we use a PatternTerminator for this.
             */
            int startContentIndex = position; /* Record this position as we're going to move on */
            Pattern terminatorPattern = Pattern.compile("\\\\end\\s*\\{"
                    + environment.getTeXName()
                    + "\\}\\s*");
            ModeState contentResult = tokeniseInNewState(TokenisationMode.BUILTIN_ENVIRONMENT_CONTENT,
                    new PatternTerminator(terminatorPattern), LaTeXMode.VERBATIM);
            int endContentIndex = contentResult.computeLastTokenEndIndex();
            FrozenSlice contentSlice = workingDocument.freezeSlice(startContentIndex, endContentIndex);
            contentToken = new ArgumentContainerToken(contentSlice, contentMode, contentResult.tokens);
            
            /* The above will have pulled in the end environment, so we need to explicitly pop the stack
             * of what is open */
            openEnvironmentStack.pop();
        }
        else {
            /* Gobble up any comments/whitespace before the start of the content */
            skipOverCommentsAndWhitespace();
            
            /* Now we parse the environment content. We don't set a terminator here - the tokenisation
             * logic knows to search for \\end and make sure things are balanced up with whatever
             * is open. When an \\end is encountered, it returns a null token and leaves the
             * position just after the \\end.
             */
            int startContentIndex = position; /* And record this position as we're going to move on */
            ModeState contentResult = tokeniseInNewState(TokenisationMode.BUILTIN_ENVIRONMENT_CONTENT, null, contentMode);
            int endContentIndex = contentResult.computeLastTokenEndIndex();
            FrozenSlice contentSlice = workingDocument.freezeSlice(startContentIndex, endContentIndex);
            contentToken = new ArgumentContainerToken(contentSlice, contentMode, contentResult.tokens);
            
            /* (The normal parsing will have found the \\end{envName} and popped the stack for us) */
        }
        
        /* (position now points just after the \\end{envName}) */
        
        /* Bail now if we encountered any errors */
        if (errorToken!=null) {
            /* We'll recreate the ErrorToken so that it ends at the current position so that
             * parsing will continue from after \\end{envName}.
             */
            sessionContext.getErrors().remove(errorToken.getError()); /* Remove entry added above */
            return createError(errorToken.getError().getErrorCode(),
                    startTokenIndex, position, errorToken.getError().getArguments());
        }
        
        /* Success! */
        FrozenSlice environmentSlice = workingDocument.freezeSlice(startEnvironmentIndex, position);
        return new EnvironmentToken(environmentSlice, startLatexMode, environment,
                argumentSearchResult.optionalArgument,
                argumentSearchResult.requiredArguments,
                contentToken);
    }
    
    //------------------------------------------------------
    // User-defined Environments

    /**
     * Finishes the handling of the start of a user-defined environment. This replaces the
     * <tt>\\begin{envName}</tt> with the appropriate substitution and reparses.
     * <p>
     * PRE-CONDITION: position will point to the character immediately after <tt>\\begin{envName}</tt>.
     */
    private FlowToken finishBeginUserDefinedEnvironment(final UserDefinedEnvironment environment) throws SnuggleParseException {
        /* Read in arguments in unparsed form. */
        UserDefinedCommandOrEnvironmentArgumentSearchResult argumentSearchResult = new UserDefinedCommandOrEnvironmentArgumentSearchResult();
        ErrorToken errorToken = advanceOverUserDefinedCommandOrEnvironmentArguments(environment, argumentSearchResult);
        if (errorToken!=null) {
            return errorToken;
        }
        
        /* Now, as per LaTeX 2e, we make substitutions in the *begin* definition. */
        FrozenSlice beginSlice = environment.getBeginDefinitionSlice();
        String resolvedBegin = substituteArguments(beginSlice, environment, argumentSearchResult);
        
        /* We add an extra command after the replacement to do housekeeping once the environment
         * has finished opening up.
         */
        resolvedBegin += "\\" + UDE_POST_BEGIN + "{" + environment.getTeXName() + "}";
      
        /* Substitute our \begin{...} clause with the replacement */
        int endBeginIndex = position;
        errorToken = makeSubstitutionAndRewind(startTokenIndex, endBeginIndex, resolvedBegin);
        if (errorToken!=null) {
            return errorToken;
        }
        
        /* Then just return the next token */
        return readNextToken();
    }
    
    /**
     * Finishes the handling of the end of a user-defined environment. This replaces the
     * <tt>\\end{envName}</tt> with the appropriate substitution and reparses.
     * <p>
     * PRE-CONDITION: position will point to the character immediately after <tt>\\begin{envName}</tt>.
     */
    private FlowToken finishEndUserDefinedEnvironment(final UserDefinedEnvironment environment)
            throws SnuggleParseException {
        /* Substitute the whole \end{...} clause with the definition */
        int endEndIndex = position;
        ErrorToken errorToken = makeSubstitutionAndRewind(startTokenIndex, endEndIndex,
                environment.getEndDefinitionSlice().extract());
        return errorToken==null ? readNextToken() : errorToken;
    }
    
    /**
     * Handles the special internal "begin user-defined environment finished" token that is
     * substituted into the input when processing the start of a user-defined environment.
     * This prompts some house-keeping, which is performed here.
     * <p>
     * PRE-CONDITION: position will point to the character immediately after <tt>\\end</tt>.
     */
    private FlowToken handleUserDefinedEnvironmentControl() throws SnuggleParseException {
        /* Read {envName} */
        String environmentName = advanceOverBracesAndEnvironmentName();
        if (environmentName==null) {
            /* Expected to find {envName} */
            throw new SnuggleLogicException("Expected to find {envName}");
        }
        
        /* Look up environment */
        UserDefinedEnvironment userEnvironment = sessionContext.getUserEnvironmentMap().get(environmentName);
        if (userEnvironment==null) {
            throw new SnuggleLogicException("Environment is not user-defined");
        }
        
        /* Now we can register this environment as being open */
        openEnvironmentStack.push(environmentName);
        
        /* Next, we obliterate this temporary token from the input and re-parse */
        ErrorToken errorToken = makeSubstitutionAndRewind(startTokenIndex, position, "");
        return errorToken==null ? readNextToken() : errorToken;
    }
    
    //-----------------------------------------
    // Command and Environment Definition

    /**
     * Finishes reading the definition of a user-defined command specified using <tt>\\newcommand</tt>
     * or similar.
     * <p>
     * PRE-CONDITION: position will point to the character immediately after <tt>\\newcommand</tt>.
     */
    private FlowToken finishCommandDefinition(final BuiltinCommand definitionCommand)
            throws SnuggleParseException {
        /* Skip whitespace/comments after \\newcommand or whatever. */
        skipOverCommentsAndWhitespace();
        
        /* First thing to read is the name of the command, which is written as either
         * 
         * {\name} or \name
         */
        boolean nameIsInBraces = false;
        int c = workingDocument.charAt(position);
        if (c==-1) {
            /* Error: input terminated before name of new command */
            return createError(CoreErrorCode.TTEUC0, startTokenIndex, position);
        }
        else if (c=='{') { /* It's {\name}, with possible whitespace */
            position++;
            skipOverCommentsAndWhitespace();
            nameIsInBraces = true;
        }
        /* Try to read in \name */
        if (workingDocument.charAt(position)!='\\') {
            /* Error: command name must be preceded by \\ */
            return createError(CoreErrorCode.TTEUC1, startTokenIndex, position);
        }
        String commandName = readCommandOrEnvironmentName(++position);
        if (commandName==null) {
            /* Error: input terminated before name of new command */
            return createError(CoreErrorCode.TTEUC0, startTokenIndex, position);
        }
        else if (reservedCommands.contains(commandName)) {
            /* Error: Not allowed to redefine reserved command */
            return createError(CoreErrorCode.TTEUC8, startTokenIndex, position+commandName.length(),
                    commandName);
        }
        position += commandName.length();
        if (nameIsInBraces) {
            /* Skip over whitespace and make sure closing brace is there */
            skipOverCommentsAndWhitespace();
            if (workingDocument.charAt(position)!='}') {
                /* Error: No matching '}' after command name */
                return createError(CoreErrorCode.TTEUC6, startTokenIndex, position);
            }
            position++;
        }
        
        /* Read in specification of arguments. */
        ArgumentDefinitionResult argumentDefinitionResult = new ArgumentDefinitionResult();
        ErrorToken error = advanceOverUserDefinedCommandOrEnvironmentArgumentDefinition(commandName, argumentDefinitionResult);
        if (error!=null) {
            return error;
        }
        
        /* Finally we read the command body, specified within {...} */
        c = workingDocument.charAt(position);
        if (c!='{') {
            /* Error: No command definition found */
            return createError(CoreErrorCode.TTEUC3, startTokenIndex, position, commandName);
        }
        int startCurlyIndex = position;
        int endCurlyIndex = findEndCurlyBrackets(position);
        if (endCurlyIndex==-1) {
            /* Error: Document ended before end of command definition */
            return createError(CoreErrorCode.TTEUC2, startTokenIndex, workingDocument.length());
        }
        
        /* Skip trailing whitespace */
        position = endCurlyIndex + 1;
        skipOverCommentsAndWhitespace();
        
        /* Extract command definition and make sure parameter references are sane */
        FrozenSlice definitionSlice = workingDocument.freezeSlice(startCurlyIndex+1, endCurlyIndex);
        error = checkDefinitionArguments(definitionSlice, commandName, argumentDefinitionResult, CoreErrorCode.TTEUCA);
        if (error!=null) {
            return error;
        }
        
        /* Now create the new command */
        UserDefinedCommand userCommand = new UserDefinedCommand(commandName,
                argumentDefinitionResult.optionalArgument,
                argumentDefinitionResult.requiredArgumentCount,
                definitionSlice);
        
        /* Register the command so that it can be used, depending on whether we are doing a renew
         * or not. */
        Map<String, UserDefinedCommand> userCommandMap = sessionContext.getUserCommandMap();
        boolean isRenewing = definitionCommand==CorePackageDefinitions.CMD_RENEWCOMMAND;
        boolean isCommandAlreadyDefined = userCommandMap.containsKey(commandName) || sessionContext.getBuiltinCommandByTeXName(commandName)!=null;
        if (isRenewing && !isCommandAlreadyDefined) {
            /* Command does not already exist so can't be renewed */
            return createError(CoreErrorCode.TTEUC4, startTokenIndex, position, commandName);
        }
        else if (!isRenewing && isCommandAlreadyDefined) {
            /* Command already exists so can't be "new"ed */
            return createError(CoreErrorCode.TTEUC5, startTokenIndex, position, commandName);
        }
        userCommandMap.put(commandName, userCommand);
        
        /* Finally, return token representing the definition */
        return new CommandToken(workingDocument.freezeSlice(startTokenIndex, position),
                currentModeState.latexMode, definitionCommand);
    }

    /**
     * Finishes reading the definition of a user-defined environment specified using
     * <tt>\\newenvironment</tt> or similar.
     * <p>
     * PRE-CONDITION: position will point to the character immediately after <tt>\\newenvironment</tt>.
     */
    private FlowToken finishEnvironmentDefinition(final BuiltinCommand definitionCommand)
            throws SnuggleParseException {
        /* Skip comments/whitespace after \\newenvironment or whatever. */
        skipOverCommentsAndWhitespace();
        
        /* Read name of new environment, specified as {envName} */
        String environmentName = advanceOverBracesAndEnvironmentName();
        if (environmentName==null) {
            /* Expected to find {envName} */
            return createError(CoreErrorCode.TTEUE0, startTokenIndex, position);
        }
        else if (reservedCommands.contains(environmentName)) {
            /* Error: Cannot redefine these special commands */
            return createError(CoreErrorCode.TTEUC8, startTokenIndex, position+2+environmentName.length() /* Skip {envName} */,
                    environmentName);
        }
        
        /* Skip whitespace after name */
        skipOverCommentsAndWhitespace();
        
        /* Read in specification of arguments. */
        ArgumentDefinitionResult argumentDefinitionResult = new ArgumentDefinitionResult();
        ErrorToken error = advanceOverUserDefinedCommandOrEnvironmentArgumentDefinition(environmentName, argumentDefinitionResult);
        if (error!=null) {
            return error;
        }
        
        /* Finally we read the 'begin' and 'end' bodies, specified within {...} */
        FrozenSlice[] definitionSlices = new FrozenSlice[2];
        int c;
        for (int i=0; i<2; i++) {
            c = workingDocument.charAt(position);
            if (c!='{') {
                /* Missing definition for begin/end */
                return createError(CoreErrorCode.TTEUE1, startTokenIndex, position,
                        ((i==0) ? "begin" : "end"), environmentName);
            }
            int startCurlyIndex = position;
            int endCurlyIndex = findEndCurlyBrackets(position);
            
            /* Skip trailing whitespace */
            position = endCurlyIndex + 1;
            skipOverCommentsAndWhitespace();

            /* Record slice */
            definitionSlices[i] = workingDocument.freezeSlice(startCurlyIndex+1, endCurlyIndex);
        }
 
        /* Check parameters in definitions */
        error = checkDefinitionArguments(definitionSlices[0], environmentName, argumentDefinitionResult, CoreErrorCode.TTEUE5);
        if (error==null) {
            error = checkDefinitionArguments(definitionSlices[1], environmentName, null, CoreErrorCode.TTEUE6);
        }
        if (error!=null) {
            return error;
        }
        
        /* Now create new environment */
        UserDefinedEnvironment userEnvironment = new UserDefinedEnvironment(environmentName,
                argumentDefinitionResult.optionalArgument,
                argumentDefinitionResult.requiredArgumentCount,
                definitionSlices[0], definitionSlices[1]);
        
        /* Register the environment so that it can be used, depending on whether we are doing a renew
         * or not. */
        Map<String, UserDefinedEnvironment> userEnvironmentMap = sessionContext.getUserEnvironmentMap();
        boolean isRenewing = definitionCommand==CorePackageDefinitions.CMD_RENEWENVIRONMENT;
        boolean isEnvAlreadyDefined = userEnvironmentMap.containsKey(environmentName) || sessionContext.getBuiltinEnvironmentByTeXName(environmentName)!=null;
        if (isRenewing && !isEnvAlreadyDefined) {
            /* Error: Environment is not already defined so can't be renewed */
            return createError(CoreErrorCode.TTEUE2, startTokenIndex, position, environmentName);
        }
        else if (!isRenewing && isEnvAlreadyDefined) {
            /* Error: Environment is already defined so can't be "new"ed */
            return createError(CoreErrorCode.TTEUE3, startTokenIndex, position, environmentName);
        }
        userEnvironmentMap.put(environmentName, userEnvironment);
        
        /* Finally, return token representing the definition */
        CommandToken result = new CommandToken(workingDocument.freezeSlice(startTokenIndex, position),
                currentModeState.latexMode, definitionCommand);
        return result;
    }
    
    /**
     * This helper checks the definition of a user-defined command or environment to make sure
     * that any argument references are in line with what is defined.
     */
    private ErrorToken checkDefinitionArguments(final FrozenSlice definitionSlice,
            final String commandOrEnvironmentName, final ArgumentDefinitionResult argumentDefinitionResult,
            final ErrorCode errorCode)
            throws SnuggleParseException {
        int argumentCount = argumentDefinitionResult!=null ? argumentDefinitionResult.requiredArgumentCount + (argumentDefinitionResult.optionalArgument!=null ? 1 : 0) : 0;
        boolean inEscape = false; /* Whether we are in the middle of a character escape */
        boolean inArgument = false; /* Whether we are inside an argument */
        int index;
        int c;
        WorkingDocument sliceDocument = definitionSlice.getDocument();
        for (index=definitionSlice.startIndex; index<definitionSlice.endIndex; index++) {
            c = sliceDocument.charAt(index);
            if (!inEscape && c=='\\') {
                inEscape = true;
            }
            else if (inEscape) {
                inEscape = false;
            }
            else if (c=='#') {
                inArgument = true; /* (Will get actual argument on next iteration) */
            }
            else if (inArgument) {
                if (argumentCount==0 || !(c>='1' && c<='0' + argumentCount)) {
                    /* Error: Illegal argument */
                    return createError(errorCode, index-1, index,
                            commandOrEnvironmentName,
                            Character.valueOf((char) c), Integer.valueOf(argumentCount));
                }
                inArgument = false;
            }
        }
        if (inArgument) {
            /* Error: # at end of command. For simplicity, we'll handle the same way as above */
            return createError(errorCode, index-1, index,
                    commandOrEnvironmentName,
                    null, Integer.valueOf(argumentCount));
        }
        return null;
    }
    
    /**
     * Trivial result Object for {@link LaTeXTokeniser#advanceOverUserDefinedCommandOrEnvironmentArgumentDefinition(String, ArgumentDefinitionResult)}
     */
    static final class ArgumentDefinitionResult {
        
        /** Optional argument, null if not supported */
        public String optionalArgument;
        
        /** Number of required arguments */
        public int requiredArgumentCount;
        
    }
    
    /**
     * Reads in the argument specification for a new command or environment. This will be of
     * the form:
     * <tt>[n]</tt>
     * OR
     * <tt>[n][opt]</tt>.
     * <p>
     * PRE-CONDITION: position should be set to just after <tt>\\newcommand{name}</tt>
     *   or <tt>\\newenvironment{name}</tt>
     * POST-CONDITION: position will now be directly after the arguments
     * 
     * @param commandOrEnvironmentName
     * @param result
     * @throws SnuggleParseException
     */
    private ErrorToken advanceOverUserDefinedCommandOrEnvironmentArgumentDefinition(final String commandOrEnvironmentName,
            final ArgumentDefinitionResult result) throws SnuggleParseException {
        /* Skip initial comments/whitespace */
        skipOverCommentsAndWhitespace();
        
        /* Next we read in the number of arguments, specified as [<1-9>],
         * then whether the first argument is optional (specified as a further [<anything>])
         * 
         * Also note that the number of arguments includes whether there is an optional argument!
         * 
         * NOTE: Don't blame me - I didn't write \newcommand and friends!!
         * 
         * Examples:
         * 
         * [2] -> no optional argument, 2 required arguments
         * [2][opt] -> 1 optional argument, 2-1=1 required argument
         */
        int requiredArgumentCount = 0;
        String optionalArgument = null;
        int c = workingDocument.charAt(position);
        if (c=='[') {
            int afterOpenSquare = position + 1;
            int closeSquareIndex = findEndSquareBrackets(position);
            if (closeSquareIndex==-1) {
                /* Error: no ']' found! */
                return createError(CoreErrorCode.TTEUC9, startTokenIndex, workingDocument.length());
            }
            position = closeSquareIndex + 1; /* Move on to after ']' */
            String rawArgCount = workingDocument.extract(afterOpenSquare, closeSquareIndex).toString().trim();
            try {
                requiredArgumentCount = Integer.parseInt(rawArgCount);
            }
            catch (NumberFormatException e) {
                /* Error: Not an integer! */
                return createError(CoreErrorCode.TTEUC7, startTokenIndex, position,
                        commandOrEnvironmentName, rawArgCount);
            }
            if (requiredArgumentCount<1 || requiredArgumentCount>9) {
                /* Error: Number of args must be between 1 and 9 inclusive */
                return createError(CoreErrorCode.TTEUC7, startTokenIndex, position,
                        commandOrEnvironmentName, rawArgCount);
            }
            skipOverCommentsAndWhitespace();
            if (workingDocument.charAt(position)=='[') {
                requiredArgumentCount--;
                closeSquareIndex = findEndSquareBrackets(position);
                if (closeSquareIndex==-1) {
                    /* Error: no ']' found! */
                    return createError(CoreErrorCode.TTEUC9, startTokenIndex, workingDocument.length());
                }
                optionalArgument = workingDocument.extract(position+1, closeSquareIndex).toString();
                position = closeSquareIndex + 1; /* Move past ']' */
            }
        }
        
        /* Skip trailing comments/whitespace */
        skipOverCommentsAndWhitespace();
        
        /* Fill in result and return null to indicate 'no error' */
        result.optionalArgument = optionalArgument;
        result.requiredArgumentCount = requiredArgumentCount;
        return null;
    }
    
    //-----------------------------------------
    // General Helpers
    
    /**
     * Returns the index of the next ']', handling any balanced braces appropriately.
     * Returns -1 if no corresponding ']' was found.
     */
    private int findEndSquareBrackets(final int openSquareBracketIndex) {
        /* NOTE: Curly brackets protect square brackets */
        boolean inEscape = false; /* Whether we are in the middle of a character escape */
        boolean inComment = false; /* Whether we are inside a comment, i.e. skipping to end of line */
        int index;
        int c;
        for (index=openSquareBracketIndex; index<workingDocument.length(); index++) {
            c = workingDocument.charAt(index);
            if (inComment) {
                /* We're in a comment, so skip this character */
                if (c=='\n') {
                    /* End of line so end of comment */
                    inComment = false;
                }
            }
            else if (c==']') {
                return index;
            }
            else if (inEscape) {
                /* End of an escape - stop escaping and go back to normal */
                inEscape = false;
            }
            else if (c=='\\') {
                /* Start of an escape */
                inEscape = true;
            }
            else if (c=='{') {
                /* We have started {....}, which will protect any square brackets inside.
                 * Let's move over this.
                 */
                index = findEndCurlyBrackets(index);
            }
            else if (c=='%') {
                /* Start of a comment */
                inComment = true;
            }
        }
        return -1;
    }

    
    /**
     * Returns the index of the next balanced '}', or -1 if no balance was found.
     * 
     * @param openBraceIndex
     */
    private int findEndCurlyBrackets(final int openBraceIndex) {
        boolean inEscape = false; /* Whether we are in the middle of a character escape */
        boolean inComment = false; /* Whether we are inside a comment, i.e. skipping to end of line */
        int depth = 0; /* Current depth of brackets */
        int index;
        int c;
        for (index=openBraceIndex; index<workingDocument.length(); index++) {
            c = workingDocument.charAt(index);
            if (!inEscape && c=='\\') {
                /* Start of an escape */
                inEscape = true;
            }
            else if (inEscape) {
                /* End of an escape - stop escaping and go back to normal */
                inEscape = false;
            }
            else if (inComment) {
                /* In a comment, so skip */
                if (c=='\n') {
                    /* End of a comment line */
                    inComment = false;
                }
            }
            else if (c=='%') {
                /* Start of a comment */
                inComment = true;
            }
            else if (c=='{') {
                /* Found generic opener */
                depth++;
            }
            else if (c=='}') {
                /* Generic end of bracket */
                depth--;
                if (depth==0) {
                    /* Balanced delimiters */
                    return index;
                }
            }
        }
        return -1;
    }

    /**
     * Advances the current position past any comments and/or whitespace, including newlines.
     */
    private void skipOverCommentsAndWhitespace() {
        int c;
        while (position<workingDocument.length()) {
            c = workingDocument.charAt(position);
            if (c=='%') {
                skipOverComment();
            }
            else if (Character.isWhitespace(c)) {
                position++;
            }
            else {
                break;
            }
        }
    }

    /**
     * Advances the current position past any comments and/or whitespace, including newlines.
     */
    private void skipOverComments() {
        int c;
        while (position<workingDocument.length()) {
            c = workingDocument.charAt(position);
            if (c=='%') {
                skipOverComment();
            }
            else {
                break;
            }
        }
    }
    
    /**
     * Advances the current position past a comment, indicated by a '%' symbol.
     * <p>
     * As per LaTeX convention, comments continue until the end of the current line.
     * Moreover, if the next line contains non-whitespace characters then LaTeX comments
     * also absorb the newline at the end of the original line plus any leading whitespace
     * on the next line(!)
     */
    private void skipOverComment() {
        /* Read to end of current line */
        if (workingDocument.charAt(position)!='%') {
            return;
        }
        int index = position + 1;
        while (index<workingDocument.length() && workingDocument.charAt(index)!='\n') {
            index++;
        }
        if (workingDocument.charAt(index)=='\n') {
            /* See if the next line contains non-whitespace */
            int searchIndex = index + 1;
            int c;
            while (searchIndex<workingDocument.length() && (c=workingDocument.charAt(searchIndex))!='\n') {
                if (!Character.isWhitespace(c)) {
                    /* Found non-whitespace so terminate comment here and stop */
                    index = searchIndex;
                    break;
                }
                searchIndex++;
            }
        }
        position = index;
    }
    
    /**
     * Advances the current position past any whitespace, excluding newlines.
     */
    private void skipOverTrailingWhitespace() {
        int c;
        while (position<workingDocument.length()
                && Character.isWhitespace(c = workingDocument.charAt(position))
                && c!='\n') {
            position++;
        }
    }
    
    //-----------------------------------------
    // Error Handling
    
    private ErrorToken createError(final ErrorCode errorCode, final int errorStartIndex,
            final int errorEndIndex, final Object... arguments) throws SnuggleParseException {
        FrozenSlice errorSlice = workingDocument.freezeSlice(errorStartIndex, errorEndIndex);
        InputError error = new InputError(errorCode, errorSlice, arguments);
        sessionContext.registerError(error);
        return new ErrorToken(error, currentModeState!=null ? currentModeState.latexMode : LaTeXMode.PARAGRAPH);
    }
}