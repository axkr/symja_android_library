/* $Id: TokenFixer.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.internal;

import uk.ac.ed.ph.snuggletex.InputError;
import uk.ac.ed.ph.snuggletex.SnuggleLogicException;
import uk.ac.ed.ph.snuggletex.definitions.BuiltinCommand;
import uk.ac.ed.ph.snuggletex.definitions.BuiltinEnvironment;
import uk.ac.ed.ph.snuggletex.definitions.Command;
import uk.ac.ed.ph.snuggletex.definitions.CoreErrorCode;
import uk.ac.ed.ph.snuggletex.definitions.CorePackageDefinitions;
import uk.ac.ed.ph.snuggletex.definitions.Globals;
import uk.ac.ed.ph.snuggletex.definitions.LaTeXMode;
import uk.ac.ed.ph.snuggletex.definitions.TextFlowContext;
import uk.ac.ed.ph.snuggletex.semantics.InterpretationType;
import uk.ac.ed.ph.snuggletex.semantics.MathBracketInterpretation;
import uk.ac.ed.ph.snuggletex.semantics.MathIdentifierInterpretation;
import uk.ac.ed.ph.snuggletex.semantics.MathMLSymbol;
import uk.ac.ed.ph.snuggletex.semantics.MathNumberInterpretation;
import uk.ac.ed.ph.snuggletex.semantics.MathOperatorInterpretation;
import uk.ac.ed.ph.snuggletex.semantics.MathBracketInterpretation.BracketType;
import uk.ac.ed.ph.snuggletex.tokens.ArgumentContainerToken;
import uk.ac.ed.ph.snuggletex.tokens.BraceContainerToken;
import uk.ac.ed.ph.snuggletex.tokens.CommandToken;
import uk.ac.ed.ph.snuggletex.tokens.EnvironmentToken;
import uk.ac.ed.ph.snuggletex.tokens.ErrorToken;
import uk.ac.ed.ph.snuggletex.tokens.FlowToken;
import uk.ac.ed.ph.snuggletex.tokens.SimpleToken;
import uk.ac.ed.ph.snuggletex.tokens.Token;
import uk.ac.ed.ph.snuggletex.tokens.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * This takes the output from {@link LaTeXTokeniser} and performs simplifications and
 * groupings on the {@link FlowToken}s that makes them easier to convert to a DOM.
 * 
 * @see LaTeXTokeniser
 * 
 * @author  David McKain
 * @version $Revision: 525 $
 */
public final class TokenFixer {
    
    private final SessionContext sessionContext;
    
    public TokenFixer(final SessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }
    
    //-----------------------------------------

    public void fixTokenTree(ArgumentContainerToken token) throws SnuggleParseException {
        visitBranch(token);
    }
    
    //-----------------------------------------
    
    private void visitBranch(Token rootToken) throws SnuggleParseException {
        /* Dive into containers */
        switch (rootToken.getType()) {
            case ARGUMENT_CONTAINER:
                visitContainerContent((ArgumentContainerToken) rootToken);
                break;
                
            case COMMAND:
                visitCommand((CommandToken) rootToken);
                break;
                
            case ENVIRONMENT:
                visitEnvironment(((EnvironmentToken) rootToken));
                break;
                
            case TEXT_MODE_TEXT:
                /* Currently not doing anything to this */
                break;
                
            case BRACE_CONTAINER:
                visitContainerContent(((BraceContainerToken) rootToken).getBraceContent());
                break;
                
            case VERBATIM_MODE_TEXT:
            case LR_MODE_NEW_PARAGRAPH:
            case MATH_NUMBER:
            case SINGLE_CHARACTER_MATH_IDENTIFIER:
            case SINGLE_CHARACTER_MATH_SPECIAL:
            case ERROR:
            case TAB_CHARACTER:
                /* Nothing to do here */
                break;
                
            case NEW_PARAGRAPH:
                throw new SnuggleLogicException("Unfixed " + rootToken.getType() + " token: "
                        + rootToken);
                
            default:
                throw new SnuggleLogicException("Unhandled type " + rootToken.getType());
        }
    }

    private void visitEnvironment(EnvironmentToken environmentToken) throws SnuggleParseException {
        /* We may do special handling for certain environments */
        BuiltinEnvironment environment = environmentToken.getEnvironment();
        if (environment==CorePackageDefinitions.ENV_ITEMIZE || environment==CorePackageDefinitions.ENV_ENUMERATE) {
            fixListEnvironmentContent(environmentToken);
        }
        else if (environment.hasInterpretation(InterpretationType.TABULAR)) {
            fixTabularEnvironmentContent(environmentToken);
        }
        
        /* Visit arguments (usually)...
         * 
         * We don't drill into the arguments of ENV_BRACKETED as that will end up with an infinite
         * loop of parenthesis nesting!
         */
        if (environment!=CorePackageDefinitions.ENV_BRACKETED) {
            ArgumentContainerToken optArgument = environmentToken.getOptionalArgument();
            if (optArgument!=null) {
                visitContainerContent(optArgument);
            }
            ArgumentContainerToken[] arguments = environmentToken.getArguments();
            if (arguments!=null) {
                for (ArgumentContainerToken argument : arguments) {
                    visitContainerContent(argument);
                }
            }
        }
        
        /* Visit content */
        visitContainerContent(environmentToken.getContent());
    }
    
    private void visitCommand(CommandToken commandToken) throws SnuggleParseException {
        /* (Currently no requirement for any specific handling for certain commands) */
        
        /* Visit arguments and content */
        ArgumentContainerToken optArgument = commandToken.getOptionalArgument();
        if (optArgument!=null) {
            visitContainerContent(optArgument);
        }
        ArgumentContainerToken[] arguments = commandToken.getArguments();
        if (arguments!=null) {
            for (ArgumentContainerToken argument : arguments) {
                visitContainerContent(argument);
            }
        }
    }
    
    //-----------------------------------------
    
    private void visitContainerContent(ArgumentContainerToken parent) throws SnuggleParseException {
        /* Handle content as appropriate for the current mode */
        List<FlowToken> content = parent.getContents();
        switch (parent.getLatexMode()) {
            case PARAGRAPH:
                visitSiblingsParagraphMode(parent, content);
                break;
                
            case LR:
                visitSiblingsLRMode(parent, content);
                break;
                
            case MATH:
                visitSiblingsMathMode(parent, content);
                break;
                
            case VERBATIM:
                /* Nothing to do here! */
                break;
                
            default:
                throw new SnuggleLogicException("Unhandled mode " + parent.getLatexMode());
        }
    }
    
    //-----------------------------------------
    // PARAGRAPH mode stuff
    
    /**
     * NOTE: This does fix in place!
     * 
     * @throws SnuggleParseException 
     */
    private void visitSiblingsParagraphMode(Token parentToken, List<FlowToken> tokens) throws SnuggleParseException {
        groupStyleCommands(parentToken, tokens);
        stripRedundantWhitespaceTokens(tokens);
        inferParagraphs(tokens);
        for (FlowToken token : tokens) {
            visitBranch(token);
        }
    }
    
    /**
     * We go through looking for old TeX "style" and sizing commands like
     * 
     * <tt>A \\bf B ...</tt>
     * 
     * and replace with more explicit environment versions:
     * 
     * <tt>A \begin{bf} B ... \end{bf}</tt>
     *  
     * to make things a bit easier to handle.
     * <p>
     * Only commands that take no arguments are handled this way, since LaTeX style commands like
     * <tt>\\underline</tt> behave more like normal commands.
     */
    private void groupStyleCommands(Token parentToken, List<FlowToken> tokens) {
        FlowToken token;
        for (int i=0; i<tokens.size(); i++) { /* (This does fix-in-place) */
            token = tokens.get(i);
            if (token.getType()==TokenType.COMMAND && token.hasInterpretationType(InterpretationType.STYLE_DECLARATION)
                    && ((CommandToken) token).getCommand().getArgumentCount()==0) {
                /* Look up the corresponding environment (having the same name as the command) */
                CommandToken commandToken = (CommandToken) token;
                BuiltinCommand command = commandToken.getCommand();
                BuiltinEnvironment environment = sessionContext.getBuiltinEnvironmentByTeXName(command.getTeXName());
                if (environment==null) {
                    throw new SnuggleLogicException("No environment defined to replace old TeX command " + command);
                }
                if (i+1<tokens.size()) {
                    /* Replacement environment content is everything from after the token */
                    FlowToken lastToken = tokens.get(tokens.size()-1);
                    ArgumentContainerToken contentToken = ArgumentContainerToken.createFromContiguousTokens(parentToken, token.getLatexMode(), tokens, i+1, tokens.size());
                    FrozenSlice replacementSlice = token.getSlice().rightOuterSpan(lastToken.getSlice());
                    
                    /* Now make replacement and remove all tokens that come afterwards */
                    EnvironmentToken replacement = new EnvironmentToken(replacementSlice, token.getLatexMode(), environment, contentToken);
                    tokens.set(i, replacement);
                    tokens.subList(i+1, tokens.size()).clear();
                }
                else {
                    /* Something like {\\bf}, which we'll convert to an empty environment */
                    ArgumentContainerToken contentToken = ArgumentContainerToken.createEmptyContainer(token, token.getLatexMode());
                    EnvironmentToken replacement = new EnvironmentToken(token.getSlice(), token.getLatexMode(), environment, contentToken);
                    tokens.set(i, replacement);
                }
                break;
            }
        }
    }
    
    /**
     * Strips out redundant whitespace text tokens at the start and end of a List of siblings
     * and between a pair of "block" tokens.
     * 
     * @param tokens
     */
    private void stripRedundantWhitespaceTokens(List<FlowToken> tokens) {
        boolean blockBefore, blockAfter;
        FlowToken token;
        for (int i=0; i<tokens.size(); ) { /* (This does fix-in-place) */
            token = tokens.get(i);
            if (token.getType()==TokenType.TEXT_MODE_TEXT) {
                if ((i==0 || i==tokens.size()-1) && token.getSlice().isWhitespace()) {
                    /* Remove leading/trailing space token */
                    tokens.remove(i);
                    continue;
                }
                blockBefore = (i==0) || tokens.get(i-1).getTextFlowContext()==TextFlowContext.START_NEW_XHTML_BLOCK;
                blockAfter = (i==tokens.size()-1) || tokens.get(i+1).getTextFlowContext()==TextFlowContext.START_NEW_XHTML_BLOCK;
                if (blockBefore && blockAfter && token.getSlice().isWhitespace()) {
                    /* This token is whitespace between 2 blocks */
                    tokens.remove(i);
                    continue;
                }
            }
            /* If still here, then token was kept so move on */
            i++;
        }
    }
    
    /**
     * Infers explicit paragraphs by searching for the traditional LaTeX
     * {@link TokenType#NEW_PARAGRAPH} and/or {@link CorePackageDefinitions#CMD_PAR} tokens, replacing with
     * the more tree-like {@link CorePackageDefinitions#CMD_PARAGRAPH}.
     * 
     * @param tokens
     */
    private void inferParagraphs(List<FlowToken> tokens) {
        List<FlowToken> paragraphBuilder = new ArrayList<FlowToken>(); /* Builds up paragraph content */
        List<FlowToken> resultBuilder = new ArrayList<FlowToken>(); /* Builds up individual "paragraphs" */
        int paragraphCount = 0;
        boolean hasParagraphs = false;
        for (int i=0; i<tokens.size(); i++) {
            FlowToken token = tokens.get(i);
            if (token.getType()==TokenType.NEW_PARAGRAPH || token.isCommand(CorePackageDefinitions.CMD_PAR)) {
                /* This token is an explicit "end current paragraph" token */
                hasParagraphs = true;
                if (!paragraphBuilder.isEmpty()) {
                    resultBuilder.add(buildGroupedCommandToken(token, CorePackageDefinitions.CMD_PARAGRAPH, paragraphBuilder));
                    paragraphCount++;
                }
            }
            else if (token.getTextFlowContext()==TextFlowContext.START_NEW_XHTML_BLOCK) {
                /* This token wants to start a new block, so first end current paragraph if one is
                 * being built and then add token. */
                hasParagraphs = true;
                if (!paragraphBuilder.isEmpty()) {
                    CommandToken leftOver = buildGroupedCommandToken(tokens.get(0), CorePackageDefinitions.CMD_PARAGRAPH, paragraphBuilder);
                    resultBuilder.add(leftOver);
                    paragraphCount++;
                }
                resultBuilder.add(token);
            }
            else if (token.getTextFlowContext()==TextFlowContext.IGNORE && paragraphBuilder.isEmpty()) {
                /* This token makes no output and the current paragraph is empty so we'll just
                 * emit the token into the output grouping
                 */
                resultBuilder.add(token);
            }
            else {
                /* Normal inline token, or one which makes no output and occurs within the
                 * current paragraph, so add to this paragraph.
                 */
                paragraphBuilder.add(token);
            }
        }
        if (!hasParagraphs) {
            /* We didn't make any changes */
            return;
        }
        
        /* Finish off current paragraph */
        if (!paragraphBuilder.isEmpty()) {
            CommandToken leftOver = buildGroupedCommandToken(tokens.get(0), CorePackageDefinitions.CMD_PARAGRAPH, paragraphBuilder);
            resultBuilder.add(leftOver);
            paragraphCount++;
        }

        /* We'll replace the existing tokens */
        tokens.clear();
        
        if (paragraphCount>1) {
            /* We ended up with multiple paragraphs */
            tokens.addAll(resultBuilder);
        }
        else {
            /* We ended up with a single paragraph, possibly mixed in with other tokens like
             * comments and stuff. As a slight optimisation, we'll pull up the paragraph's contents.
             */
            for (FlowToken resultToken : resultBuilder) {
                if (resultToken.isCommand(CorePackageDefinitions.CMD_PARAGRAPH)) {
                    tokens.addAll(((CommandToken) resultToken).getArguments()[0].getContents());
                }
                else {
                    tokens.add(resultToken);
                }
            }
        }
    }
    
    //-----------------------------------------
    // LR mode stuff
    
    /**
     * NOTE: This does fix in place!
     * 
     * @throws SnuggleParseException 
     */
    private void visitSiblingsLRMode(Token parentToken, List<FlowToken> tokens) throws SnuggleParseException {
        groupStyleCommands(parentToken, tokens);
        stripBlocks(tokens);
        for (FlowToken token : tokens) {
            visitBranch(token);
        }
    }
    
    /**
     * LR Mode doesn't let "block" stuff do its normal thing. We strip out any "new paragraph"
     * markers and fail on any other kind of block tokens.
     * 
     * @param tokens
     * @throws SnuggleParseException 
     */
    private void stripBlocks(List<FlowToken> tokens) throws SnuggleParseException {
        /* This does fix-in-place */
        for (int i=0; i<tokens.size(); i++) {
            FlowToken token = tokens.get(i);
            if (token.getType()==TokenType.NEW_PARAGRAPH || token.isCommand(CorePackageDefinitions.CMD_PAR)) {
                /* We'll replace with a space */
                tokens.set(i, new SimpleToken(token.getSlice(), TokenType.LR_MODE_NEW_PARAGRAPH,
                        LaTeXMode.LR, TextFlowContext.ALLOW_INLINE));
            }
            else if (token.getType()==TokenType.ERROR) {
                /* Keep errors as-is */
            }
            else if (token.getTextFlowContext()==TextFlowContext.START_NEW_XHTML_BLOCK) {
                /* We're not allowing blocks inside LR mode, which is more prescriptive but generally
                 * consistent with LaTeX.
                 */
                tokens.set(i, createError(token, CoreErrorCode.TFEG00, token.getSlice().extract().toString()));
            }
        }
    }
    
    //-----------------------------------------
    // Commands and Environments

    /**
     * Helper to fix the representation of a list environment.
     * The original content should be of the form:
     * 
     * \item ..... \item ..... \item ......
     * 
     * (and maybe include {@link CorePackageDefinitions#CMD_LIST_ITEM}s as well)
     * 
     * We replace with a more tree-like version.
     * 
     * If \item is used outside a suitable environment, then it will be left as-is. The
     * {@link DOMBuilder} will cope with this in due course.
     * 
     * @throws SnuggleParseException 
     */
    private void fixListEnvironmentContent(EnvironmentToken environmentToken)
            throws SnuggleParseException {
        List<FlowToken> contents = environmentToken.getContent().getContents();
        List<FlowToken> itemBuilder = new ArrayList<FlowToken>();
        List<FlowToken> resultBuilder = new ArrayList<FlowToken>();
        
        /* Go through contents, building up item groups */
        FlowToken token;
        boolean foundItem = false;
        for (int i=0, size=contents.size(); i<size; i++) {
            token = contents.get(i);
            if (token.isCommand(CorePackageDefinitions.CMD_ITEM)) {
                /* Old-style \item. Stop building up content (if appropriate) and replace with
                 * new LIST_ITEM command */
                if (foundItem) {
                    CommandToken itemBefore = buildGroupedCommandToken(environmentToken, CorePackageDefinitions.CMD_LIST_ITEM, itemBuilder);
                    resultBuilder.add(itemBefore);
                }
                foundItem = true;
                continue;
            }
            else if (!foundItem) {
                /* Found stuff before first \item. The only thing we allow is whitespace text */
                if (token.getType()==TokenType.TEXT_MODE_TEXT && token.getSlice().isWhitespace()
                        || token.getType()==TokenType.NEW_PARAGRAPH) {
                    /* This is whitespace, so we'll just ignore this token */
                }
                else {
                    /* Error: (non-trivial) content before first \item */
                    resultBuilder.add(createError(token, CoreErrorCode.TFEL00));
                }
            }
            else {
                /* Add to current item */
                itemBuilder.add(token);
            }
        }
        /* At end, finish off last item */
        if (foundItem) {
            resultBuilder.add(buildGroupedCommandToken(environmentToken, CorePackageDefinitions.CMD_LIST_ITEM, itemBuilder));
        }
        
        /* Replace content */
        contents.clear();
        contents.addAll(resultBuilder);
    }
    
    /**
     * Helper to fix the content of tabular environments to make it more clearly demarcated
     * as rows and columns. This kills off instances of {@link CorePackageDefinitions#CMD_CHAR_BACKSLASH} and
     * {@link TokenType#TAB_CHARACTER} within tables/arrays and replaces then with
     * zero or more {@link CorePackageDefinitions#CMD_TABLE_ROW} each containing zero or more
     * {@link CorePackageDefinitions#CMD_TABLE_COLUMN}.
     */
    private void fixTabularEnvironmentContent(EnvironmentToken environmentToken)
            throws SnuggleParseException {
        List<FlowToken> resultBuilder = new ArrayList<FlowToken>();
        List<CommandToken> rowBuilder = new ArrayList<CommandToken>();
        List<FlowToken> columnBuilder = new ArrayList<FlowToken>();
        List<FlowToken> contents = environmentToken.getContent().getContents();
        
        /* It's easier to process things if we explicitly add a final explicit "end row"
         * token to the environment contents if there's not one there already as it simplifies
         * the end of row processing. We'll cheat and use 'null' to signify this rather than
         * create a fake token.
         */
        List<FlowToken> entries = contents;
        if (entries.size()>0 && !entries.get(entries.size()-1).isCommand(CorePackageDefinitions.CMD_CHAR_BACKSLASH)) {
            entries = new ArrayList<FlowToken>(entries);
            entries.add(null);
        }
        
        /* Go through contents, building up rows and columns */
        FlowToken token;
        FlowToken lastGoodToken = null;
        for (int i=0, size=entries.size(); i<size; i++) {
            token = entries.get(i);
            if (token==null || token.isCommand(CorePackageDefinitions.CMD_CHAR_BACKSLASH)) {
                /* End of a row (see above). */
                if (token==null && lastGoodToken!=null && lastGoodToken.isCommand(CorePackageDefinitions.CMD_HLINE)) {
                    /* Last good token was \\hline so leave it there */
                    break;
                }
                /* First, finish off the last column (which may be
                 * completely empty but should always exist) */
                rowBuilder.add(buildGroupedCommandToken(environmentToken, CorePackageDefinitions.CMD_TABLE_COLUMN, columnBuilder));
                
                /* Then add row */
                resultBuilder.add(buildGroupedCommandToken(environmentToken, CorePackageDefinitions.CMD_TABLE_ROW, rowBuilder));
            }
            else if (token.getType()==TokenType.TEXT_MODE_TEXT && token.getSlice().isWhitespace()) {
                /* Whitespace token - we'll ignore this */
                continue;
            }
            else if (token.getType()==TokenType.TAB_CHARACTER) {
                /* Ends the column being built. This may be null (e.g. '& &') so we need to consider
                 * that case carefully.
                 */
                rowBuilder.add(buildGroupedCommandToken(environmentToken, CorePackageDefinitions.CMD_TABLE_COLUMN, columnBuilder));
            }
            else if (token.isCommand(CorePackageDefinitions.CMD_HLINE)) {
                /* \\hline must be the only token in a row. It immediately ends the current row */
                if (!columnBuilder.isEmpty()) {
                    /* Error: \\hline must be on its own within a row */
                    resultBuilder.add(createError(columnBuilder.get(0), CoreErrorCode.TFETB0));
                    columnBuilder.clear(); 
                }
                else if (!rowBuilder.isEmpty()) {
                    /* Error: \\hline must be on its own within a row */
                    resultBuilder.add(createError(rowBuilder.get(0), CoreErrorCode.TFETB0));
                    rowBuilder.clear();
                }
                /* Add \\hline to result as a "row" */
                resultBuilder.add(token);
            }
            else {
                /* Add to current column */
                columnBuilder.add(token);
            }
            /* If we didn't "continue" above, then record this token for the next loop to help
             * us to decide what to do on the last token.
             */
            lastGoodToken = token;
        }
        /* Replace content */
        contents.clear();
        contents.addAll(resultBuilder);
    }
    
    //-----------------------------------------
    // MathML stuff
    
    private void visitSiblingsMathMode(ArgumentContainerToken parentToken, List<FlowToken> tokens)
            throws SnuggleParseException {
        if (tokens.isEmpty()) {
            return;
        }
        
        /* Perform fixes and semantic guess work as required if the tokens are in a context that would normally
         * make up some kind of expression. Examples where this is not the case is in the structural parts
         * of tabular content (after being fixed) which contain either a number of TABLE_ROW or TABLE_COLUMN
         * tokens.
         * 
         * NOTE: We may need to add things here if new types of structures need to be considered.
         */
        boolean isStructural = false;
        FlowToken firstToken = tokens.get(0);
        if (firstToken.getType()==TokenType.COMMAND) {
            Command command = ((CommandToken) firstToken).getCommand();
            if (command==CorePackageDefinitions.CMD_TABLE_ROW || command==CorePackageDefinitions.CMD_TABLE_COLUMN) {
                isStructural = true;
            }
        }
        
        /* If it looks like we've got an expression then tidy it up and perform some basic semantic inference */
        if (!isStructural) {
            /* The order below is important in order to establish precedence */
            fixLeadingNegativeNumber(tokens);
            groupStyleCommands(parentToken, tokens);
            fencePairedParentheses(parentToken, tokens); /* (Want to get parentheses first) */
            fixOverInstances(parentToken, tokens);
            inferParenthesisFences(parentToken, tokens);
            fixSubscriptAndSuperscripts(parentToken, tokens);
            fixPrimes(tokens);
        }
        
        /* Visit each sub-token */
        for (FlowToken token : tokens) {
            visitBranch(token);
        }
    }
    
    /**
     * Converts leading occurrences of {@link MathMLSymbol#SUBTRACT} followed by a {@link MathNumberInterpretation}
     * into a single token representing the negation of the given number.
     * 
     * @param tokens
     */
    private void fixLeadingNegativeNumber(List<FlowToken> tokens) {
        if (tokens.size() < 2) {
            return;
        }
        FlowToken firstToken = tokens.get(0);
        FlowToken secondToken = tokens.get(1);
        if (firstToken.hasInterpretationType(InterpretationType.MATH_OPERATOR) &&
                ((MathOperatorInterpretation) firstToken.getInterpretation(InterpretationType.MATH_OPERATOR)).getMathMLOperatorContent()==MathMLSymbol.SUBTRACT
                && secondToken.hasInterpretationType(InterpretationType.MATH_NUMBER)) {
            CharSequence negation = "-" + ((MathNumberInterpretation) secondToken.getInterpretation(InterpretationType.MATH_NUMBER)).getNumber();
            SimpleToken replacementToken = new SimpleToken(firstToken.getSlice().rightOuterSpan(secondToken.getSlice()),
                    TokenType.MATH_NUMBER, firstToken.getLatexMode(),
                    null, new MathNumberInterpretation(negation));
            tokens.remove(0);
            tokens.set(0, replacementToken);
        }
    }
    
    /**
     * This handles the old-fashioned "... \over ..." by refactoring the tokens into a \frac{...}{...}.
     * As with LaTeX, we only allow one \over in a single level.
     * @throws SnuggleParseException 
     */
    private void fixOverInstances(ArgumentContainerToken parentToken, List<FlowToken> tokens) throws SnuggleParseException {
        int overIndex = -1; /* Will be set to index of \over token, if found */
        FlowToken token;
        for (int i=0; i<tokens.size(); i++) { /* Note: size() may change here */
            token = tokens.get(i);
            if (token.isCommand(CorePackageDefinitions.CMD_OVER)) {
                if (overIndex!=-1) {
                    /* Multiple \over occurrence, which we're not going to allow so kill this expression */
                    tokens.clear();
                    tokens.add(createError(token, CoreErrorCode.TFEM00));
                    return;
                }
                overIndex = i;
            }
        }
        if (overIndex!=-1) {
            /* OK, we've got {... \over ...} which we'll convert into \frac{...}{...} */
            List<FlowToken> beforeTokens = new ArrayList<FlowToken>(tokens.subList(0, overIndex));
            List<FlowToken> afterTokens = new ArrayList<FlowToken>(tokens.subList(overIndex+1, tokens.size()));
            CommandToken replacement = new CommandToken(parentToken.getSlice(),
                    LaTeXMode.MATH,
                    CorePackageDefinitions.CMD_FRAC,
                    null, /* No optional arg */
                    new ArgumentContainerToken[] {
                        ArgumentContainerToken.createFromContiguousTokens(parentToken, LaTeXMode.MATH, beforeTokens), /* Numerator */
                        ArgumentContainerToken.createFromContiguousTokens(parentToken, LaTeXMode.MATH, afterTokens)  /* Denominator */
            });
            /* Now we'll continue with our \frac{...}{...} token replacing the original tokens */
            tokens.clear();
            tokens.add(replacement);
        }
    }
    
    /**
     * Hunts through tokens for occurrences of primes used as <tt>f'</tt>, which are converted
     * to a superscript by binding to the preceding token
     * 
     * @param tokens
     */
    private void fixPrimes(List<FlowToken> tokens) {
        FlowToken leftToken, maybePrimeToken, replacementToken;
        FrozenSlice replacementSlice;
        for (int i=0; i<tokens.size()-1; i++) { /* We're fixing in place so tokens.size() may decrease over time */
            maybePrimeToken = tokens.get(i+1);
            if (maybePrimeToken.hasInterpretationType(InterpretationType.MATH_IDENTIFIER)
                    && ((MathIdentifierInterpretation) maybePrimeToken.getInterpretation(InterpretationType.MATH_IDENTIFIER)).getName().equals("'")) {
                /* Found a prime, so combine with previous token */
                leftToken = tokens.get(i);
                replacementSlice = leftToken.getSlice().rightOuterSpan(maybePrimeToken.getSlice());
                replacementToken = new CommandToken(replacementSlice, LaTeXMode.MATH, CorePackageDefinitions.CMD_MSUP_OR_MOVER, null,
                        new ArgumentContainerToken[] {
                            ArgumentContainerToken.createFromSingleToken(LaTeXMode.MATH, leftToken),
                            ArgumentContainerToken.createFromSingleToken(LaTeXMode.MATH, maybePrimeToken),
                });
                tokens.set(i, replacementToken);
                tokens.remove(i+1);
                /* Keep searching! */
            }
        }
    }
    
    /**
     * We'll look for any occurrences of T1_T2[^T3] or T1^T2[_T3], where [..] denotes
     * "optional". If found, we'll replace with the more tree-like equivalents:
     * 
     * \msub{T1}{T2}
     * \msup{T1}{T2}
     * \msubsup{T1}{T2}{T3}
     * 
     * which will be easier to handle later on.
     */
    private void fixSubscriptAndSuperscripts(Token parentToken, List<FlowToken> tokens) throws SnuggleParseException {
        int size, startModifyIndex;
        FlowToken subOrSuperToken;
        FlowToken t1, t2, t3;
        String tokenOperator = null;
        String followingOperator;
        boolean isSubOrSuper;
        boolean firstIsSuper;
        for (int i=0; i<tokens.size(); i++) { /* NB: tokens.size() may decrease during this loop! */
            size = tokens.size();
            subOrSuperToken = tokens.get(i);
            firstIsSuper = false;
            isSubOrSuper = false;
            if (subOrSuperToken.hasInterpretationType(InterpretationType.MATH_OPERATOR)) {
                tokenOperator = ((MathOperatorInterpretation) subOrSuperToken.getInterpretation(InterpretationType.MATH_OPERATOR)).getMathMLOperatorContent();
                isSubOrSuper = tokenOperator==Globals.SUP_PLACEHOLDER || tokenOperator==Globals.SUB_PLACEHOLDER;
            }
            if (!isSubOrSuper) {
                continue;
            }
            /* OK, we've found a '_' or '^'. As with LaTeX, we raise an error if it is *last* token
             * amongst siblings but allow it to be the first, in which case it is applied to a fake
             * empty token.
             */
            if (i==size-1) {
                /* Error: Trailing subscript/superscript */
                tokens.set(i, createError(subOrSuperToken, CoreErrorCode.TFEM01));
                continue;
            }
            if (i==0) {
                /* No token before sub/super, so we'll make a pretend one */
                ArgumentContainerToken emptyBeforeContainer = ArgumentContainerToken.createEmptyContainer(parentToken, LaTeXMode.MATH);
                t1 = new BraceContainerToken(emptyBeforeContainer.getSlice(), LaTeXMode.MATH, emptyBeforeContainer);
                startModifyIndex = i;
            }
            else {
                /* Found token before sub/super */
                t1 = tokens.get(i-1);
                startModifyIndex = i-1;
            }
            t2 = tokens.get(i+1);
            
            /* See if there's another '^' or '_' afterwards */
            t3 = null;
            followingOperator = null;
            if (i+2<size && tokens.get(i+2).hasInterpretationType(InterpretationType.MATH_OPERATOR)) {
                followingOperator = ((MathOperatorInterpretation) tokens.get(i+2).getInterpretation(InterpretationType.MATH_OPERATOR)).getMathMLOperatorContent();
                if (followingOperator==Globals.SUP_PLACEHOLDER || followingOperator==Globals.SUB_PLACEHOLDER) {
                    /* OK, need to find the "T3" operator! */
                    if (i+3>=size) {
                        /* Trailing super/subscript */
                        tokens.set(i-1, createError(subOrSuperToken, CoreErrorCode.TFEM01));
                        tokens.subList(i, i+3).clear();
                        continue;
                    }
                    t3 = tokens.get(i+3);
                    
                    /* Make sure we've got the right pair of operators e.g. not something like T1^T2^T3 */
                    if (tokenOperator==Globals.SUP_PLACEHOLDER && followingOperator==Globals.SUP_PLACEHOLDER
                            || tokenOperator==Globals.SUB_PLACEHOLDER && followingOperator==Globals.SUB_PLACEHOLDER) {
                        /* Double super/subscript */
                        tokens.set(i-1, createError(subOrSuperToken, CoreErrorCode.TFEM02));
                        tokens.subList(i, i+3).clear();
                        continue;
                    }
                }
            }
            /* Now be build the replacements */
            FrozenSlice replacementSlice;
            BuiltinCommand replacementCommand;
            firstIsSuper = tokenOperator==Globals.SUP_PLACEHOLDER;
            if (t3!=null) {
                /* Create replacement, replacing tokens at i-1,i+1,i+2 and i+3 */
                replacementSlice = t1.getSlice().rightOuterSpan(t3.getSlice());
                replacementCommand = CorePackageDefinitions.CMD_MSUBSUP_OR_MUNDEROVER;
                CommandToken replacement = new CommandToken(replacementSlice,
                        LaTeXMode.MATH,
                        replacementCommand,
                        null, /* No optional args */
                        new ArgumentContainerToken[] {
                            ArgumentContainerToken.createFromSingleToken(LaTeXMode.MATH, t1),
                            firstIsSuper ? ArgumentContainerToken.createFromSingleToken(LaTeXMode.MATH, t3) : ArgumentContainerToken.createFromSingleToken(LaTeXMode.MATH, t2),
                            firstIsSuper ? ArgumentContainerToken.createFromSingleToken(LaTeXMode.MATH, t2) : ArgumentContainerToken.createFromSingleToken(LaTeXMode.MATH, t3)        
                });
                tokens.set(startModifyIndex, replacement);
                tokens.subList(startModifyIndex+1, i+4).clear();
            }
            else {
                /* Just replace tokens at i-1, i, i+1 */
                replacementSlice = t1.getSlice().rightOuterSpan(t2.getSlice());
                replacementCommand = firstIsSuper ? CorePackageDefinitions.CMD_MSUP_OR_MOVER : CorePackageDefinitions.CMD_MSUB_OR_MUNDER;
                CommandToken replacement = new CommandToken(replacementSlice, LaTeXMode.MATH,
                        replacementCommand,
                        null, /* No optional args */
                        new ArgumentContainerToken[] {
                            ArgumentContainerToken.createFromSingleToken(LaTeXMode.MATH, t1),
                            ArgumentContainerToken.createFromSingleToken(LaTeXMode.MATH, t2)
                });
                tokens.set(startModifyIndex, replacement);
                tokens.subList(startModifyIndex+1, i+2).clear();
            }
        }
    }
    
    /**
     * This looks for an outermost pairs of \left and \right and converts them to a more tree-like
     * fence token.
     * <p>
     * Mismatched pairs will cause an error, as they do in LaTeX.
     * 
     * @see #inferParenthesisFences(Token, List)
     * 
     * @param tokens
     * @throws SnuggleParseException
     */
    private void fencePairedParentheses(Token parentToken, List<FlowToken> tokens) throws SnuggleParseException {
        FlowToken token;
        LEFT_SEARCH: for (int i=0; i<tokens.size(); i++) { /* (List may change from 'i' onwards during loop) */
            token = tokens.get(i);
            
            /* Is this a \left? If so, work out where its balancing \right is and
             * wrap inside a fake environment. We'll also make sure we don't find a 
             * \right before a left!
             */
            if (token.isCommand(CorePackageDefinitions.CMD_RIGHT)) {
                /* Error: \right had not preceding \left */
                tokens.set(i, createError(token, CoreErrorCode.TFEM03));
                continue LEFT_SEARCH;
            }
            else if (token.isCommand(CorePackageDefinitions.CMD_LEFT)) {
                /* Now search forward for matching \right */
                List<FlowToken> innerTokens = new ArrayList<FlowToken>();
                CommandToken openBracketToken = (CommandToken) token;
                CommandToken matchingCloseBracketToken = null;
                int matchingCloseBracketIndex = -1;
                int bracketLevel = 1;
                FlowToken innerToken;
                MATCH_SEARCH: for (int j=i+1; j<tokens.size(); j++) { /* 'j' is search index from current point onwards */
                    innerToken = tokens.get(j);
                    if (innerToken.isCommand(CorePackageDefinitions.CMD_LEFT)) {
                        bracketLevel++;
                    }
                    else if (innerToken.isCommand(CorePackageDefinitions.CMD_RIGHT)) {
                        bracketLevel--;
                        if (bracketLevel==0) {
                            /* We've found the matcher */
                            matchingCloseBracketToken = (CommandToken) innerToken;
                            matchingCloseBracketIndex = j;
                            break MATCH_SEARCH;
                        }
                    }
                    innerTokens.add(innerToken);
                }
                if (matchingCloseBracketToken==null) {
                    /* Error: We never found a match for \\left so we'll kill the whole expression off */
                    tokens.set(i, createError(token, CoreErrorCode.TFEM04));
                    tokens.subList(i+1, tokens.size()).clear();
                    break LEFT_SEARCH;
                }
                /* Now replace this bracket with a fence */
                FrozenSlice replacementSlice = openBracketToken.getSlice().rightOuterSpan(matchingCloseBracketToken.getSlice());
                EnvironmentToken replacementToken = new EnvironmentToken(replacementSlice,
                        LaTeXMode.MATH,
                        CorePackageDefinitions.ENV_BRACKETED,
                        null,
                        new ArgumentContainerToken[] {
                            ArgumentContainerToken.createFromSingleToken(LaTeXMode.MATH, openBracketToken.getCombinerTarget()),
                            ArgumentContainerToken.createFromSingleToken(LaTeXMode.MATH, matchingCloseBracketToken.getCombinerTarget())
                        },
                        ArgumentContainerToken.createFromContiguousTokens(parentToken, LaTeXMode.MATH, innerTokens)
                );
                tokens.set(i, replacementToken);
                tokens.subList(i+1, matchingCloseBracketIndex+1).clear();
                continue LEFT_SEARCH;
            }
        }
    }
    
    /**
     * This attempts to group (not necessarily matching) pairs of open and close brackets into a
     * corresponding {@link CorePackageDefinitions#ENV_BRACKETED} environment that's easier to handle.
     * <p>
     * It also handles brackets which are not correctly nested, using fake empty opener/closer
     * delimiters at the start/end of the group as required.
     * <p>
     * Note that this means that while $[1,2)$ will successfully be matched, the notation
     * $[1,2[$ will *not* be considered matched, even though it is common in subjects like
     * Mathematical Analysis. To get the correct semantics here, you must use
     * <tt>$\left[1,2\right[$ and let {@link #fencePairedParentheses(Token, List)}
     * take care of this for you.
     * 
     * @see #fencePairedParentheses(Token, List)
     */
    private void inferParenthesisFences(Token parentToken, List<FlowToken> tokens) {
        /* The algorithm used here is similar to groupPairedParentheses() */
        FlowToken token;
        LEFT_SEARCH: for (int i=0; i<tokens.size(); i++) { /* (List may change from 'i' onwards during loop) */
            token = tokens.get(i);
            if (!token.hasInterpretationType(InterpretationType.MATH_BRACKET)) {
                continue LEFT_SEARCH;
            }
            MathBracketInterpretation interpretation = (MathBracketInterpretation) token.getInterpretation(InterpretationType.MATH_BRACKET);
            if (!interpretation.isPairingInferencePossible()) {
                /* Too dangerous to try to pair up this type of bracket (e.g. < or |) */
                continue LEFT_SEARCH;
            }
            BracketType bracketType = interpretation.getBracketType();
            if (bracketType==BracketType.CLOSER) {
                /* First thing found is a closer, so make a fence with an empty opener closing at this point */
                FrozenSlice replacementSlice = tokens.get(0).getSlice().rightOuterSpan(token.getSlice());
                List<FlowToken> innerTokens = new ArrayList<FlowToken>(tokens.subList(0, i));
                EnvironmentToken replacementToken = new EnvironmentToken(replacementSlice,
                        LaTeXMode.MATH,
                        CorePackageDefinitions.ENV_BRACKETED,
                        null,
                        new ArgumentContainerToken[] {
                            ArgumentContainerToken.createEmptyContainer(parentToken, LaTeXMode.MATH),
                            ArgumentContainerToken.createFromSingleToken(LaTeXMode.MATH, token)
                        },
                        ArgumentContainerToken.createFromContiguousTokens(parentToken, LaTeXMode.MATH, innerTokens)
                );
                tokens.set(0, replacementToken);
                tokens.subList(1, i+1).clear();
                i = 0; /* (Rewind back to this new fence) */
                continue LEFT_SEARCH;
            }
            else if (bracketType==BracketType.OPENER_OR_CLOSER) {
                /* Brackets like |...| can't be inferred so ignore but continue */
                continue LEFT_SEARCH;
            }
            /* If we're here, then we found some sort of open bracket. We'll search forward for
             * the matching close, taking care to balance up matching open/close pairs we see on our way.
             */
            List<FlowToken> innerTokens = new ArrayList<FlowToken>();
            FlowToken openBracketToken = token;
            FlowToken matchingCloseBracketToken = null;
            int matchingCloseBracketIndex = -1;
            FlowToken afterToken;
            Stack<MathBracketInterpretation> openerStack = new Stack<MathBracketInterpretation>();
            openerStack.add(interpretation);
            MATCH_SEARCH: for (int j=i+1; j<tokens.size(); j++) { /* 'j' is search index from current point onwards */
                afterToken = tokens.get(j);
                if (afterToken.hasInterpretationType(InterpretationType.MATH_BRACKET)) {
                    MathBracketInterpretation afterInterpretation = (MathBracketInterpretation) afterToken.getInterpretation(InterpretationType.MATH_BRACKET);
                    BracketType afterBracketType = afterInterpretation.getBracketType();
                    switch (afterBracketType) {
                        case OPENER:
                            openerStack.add(afterInterpretation);
                            break;
                            
                        case OPENER_OR_CLOSER:
                            /* Treat this like any other token */
                            break;
                            
                        case CLOSER:
                            /* Pop the last opener. (Note that we no longer check that it matches
                             * the closer we've just found.)
                             */
                            openerStack.pop(); /* (This will always succeed here) */
                            if (openerStack.isEmpty()) {
                                /* Yay! We've found a balance */
                                matchingCloseBracketToken = afterToken;
                                matchingCloseBracketIndex = j;
                                break MATCH_SEARCH;
                            }
                            break;
                    }
                }
                innerTokens.add(afterToken);
            }
            /* Now replace this bracket (if found) or whole expression to the end with a fence */
            EnvironmentToken replacementToken;
            FrozenSlice replacementSlice;
            if (matchingCloseBracketToken!=null) {
                replacementSlice = openBracketToken.getSlice().rightOuterSpan(matchingCloseBracketToken.getSlice());
                replacementToken = new EnvironmentToken(replacementSlice,
                        LaTeXMode.MATH,
                        CorePackageDefinitions.ENV_BRACKETED,
                        null,
                        new ArgumentContainerToken[] {
                            ArgumentContainerToken.createFromSingleToken(LaTeXMode.MATH, openBracketToken),
                            ArgumentContainerToken.createFromSingleToken(LaTeXMode.MATH, matchingCloseBracketToken)
                        },
                        ArgumentContainerToken.createFromContiguousTokens(parentToken, LaTeXMode.MATH, innerTokens)
                );
                tokens.set(i, replacementToken);
                tokens.subList(i+1, matchingCloseBracketIndex+1).clear();
            }
            else {
                replacementSlice = openBracketToken.getSlice().rightOuterSpan(tokens.get(tokens.size()-1).getSlice());
                replacementToken = new EnvironmentToken(replacementSlice,
                        LaTeXMode.MATH,
                        CorePackageDefinitions.ENV_BRACKETED,
                        null,
                        new ArgumentContainerToken[] {
                            ArgumentContainerToken.createFromSingleToken(LaTeXMode.MATH, openBracketToken),
                            ArgumentContainerToken.createEmptyContainer(parentToken, LaTeXMode.MATH),
                        },
                        ArgumentContainerToken.createFromContiguousTokens(parentToken, LaTeXMode.MATH, innerTokens)
                );
                tokens.set(i, replacementToken);
                tokens.subList(i+1, tokens.size()).clear();
            }
            continue LEFT_SEARCH;
        }
    }
    
    //-----------------------------------------
    // Helpers
    
    /**
     * Useful helper. Takes a "builder" that has been accumulating tokens. Groups all tokens
     * into a {@link CommandToken} containing the accumulated tokens as a single argument
     * and clears the builder.
     * <p>
     * An empty group is legal and will result in a "fake" empty token assuming the same Slice
     * and LaTeXMode as parentToken.
     * 
     * @param command
     * @param itemBuilder
     * @return null if the builder is empty, otherwise grouped {@link CommandToken}
     */
    private CommandToken buildGroupedCommandToken(final Token parentToken,
            final BuiltinCommand command, final List<? extends FlowToken> itemBuilder) {
        ArgumentContainerToken contentToken;
        if (itemBuilder.isEmpty()) {
            contentToken = ArgumentContainerToken.createEmptyContainer(parentToken, parentToken.getLatexMode());
        }
        else {
            contentToken = ArgumentContainerToken.createFromContiguousTokens(parentToken, itemBuilder.get(0).getLatexMode(), itemBuilder);
        }
        CommandToken result = new CommandToken(contentToken.getSlice(), contentToken.getLatexMode(),
                command,
                null, /* No optional argument */
                new ArgumentContainerToken[] { contentToken } /* Single argument containing content */
        );
        itemBuilder.clear();
        return result;
    }

    private ErrorToken createError(final FlowToken token, final CoreErrorCode errorCode,
            final Object... arguments) throws SnuggleParseException {
        FrozenSlice slice = token.getSlice();
        InputError error = new InputError(errorCode, slice, arguments);
        sessionContext.registerError(error);
        return new ErrorToken(error, token.getLatexMode());
    }
}
