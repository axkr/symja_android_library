/* $Id: Token.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.tokens;

import uk.ac.ed.ph.snuggletex.SnugglePackage;
import uk.ac.ed.ph.snuggletex.internal.util.DumpMode;
import uk.ac.ed.ph.snuggletex.internal.util.ObjectDumperOptions;
import uk.ac.ed.ph.snuggletex.internal.util.ObjectUtilities;
import uk.ac.ed.ph.snuggletex.definitions.BuiltinCommand;
import uk.ac.ed.ph.snuggletex.definitions.LaTeXMode;
import uk.ac.ed.ph.snuggletex.internal.FrozenSlice;
import uk.ac.ed.ph.snuggletex.semantics.Interpretation;
import uk.ac.ed.ph.snuggletex.semantics.InterpretationType;

import java.util.EnumMap;

/**
 * Base interface for a parsed SnuggleTeX token.
 * 
 * @author  David McKain
 * @version $Revision: 525 $
 */
public abstract class Token {
    
    /** Extent of this entire token */
    protected final FrozenSlice slice;
    
    /** Classifies the token */
    protected final TokenType type;
    
    /** LaTeX Mode that this token was parsed in */
    protected final LaTeXMode latexMode;
    
    /** Interpretation(s) of this token, if it can be readily deduced from the input */
    protected final EnumMap<InterpretationType, Interpretation> interpretationMap;
    
    protected Token(final FrozenSlice slice, final TokenType type, final LaTeXMode latexMode,
            final Interpretation... interpretations) {
        this.slice = slice;
        this.type = type;
        this.latexMode = latexMode;
        this.interpretationMap = SnugglePackage.makeInterpretationMap(interpretations);
    }
    
    protected Token(final FrozenSlice slice, final TokenType type, final LaTeXMode latexMode,
            final EnumMap<InterpretationType, Interpretation> interpretationMap) {
        this.slice = slice;
        this.type = type;
        this.latexMode = latexMode;
        this.interpretationMap = interpretationMap;
    }
    
    @ObjectDumperOptions(DumpMode.TO_STRING)
    public FrozenSlice getSlice() {
        return slice;
    }
    
    public TokenType getType() {
        return type;
    }
    
    public LaTeXMode getLatexMode() {
        return latexMode;
    }

    @ObjectDumperOptions(DumpMode.TO_STRING)
    public EnumMap<InterpretationType, Interpretation> getInterpretationMap() {
        return interpretationMap;
    }
    
    public Interpretation getInterpretation(InterpretationType type) {
        return interpretationMap!=null ? interpretationMap.get(type) : null;
    }
    
    //------------------------------------------------------
    // Convenience

    public boolean hasInterpretationType(InterpretationType... types) {
        if (interpretationMap==null) {
            return false;
        }
        for (InterpretationType testType : types) {
            if (interpretationMap.containsKey(testType)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isCommand(BuiltinCommand command) {
        return this instanceof CommandToken && ((CommandToken) this).getCommand()==command;
    }

    @Override
    public String toString() {
        return ObjectUtilities.beanToString(this);
    }
}
