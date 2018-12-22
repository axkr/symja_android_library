/* $Id:DefinitionMap.java 179 2008-08-01 13:41:24Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex;

import uk.ac.ed.ph.snuggletex.definitions.BuiltinCommand;
import uk.ac.ed.ph.snuggletex.definitions.BuiltinEnvironment;
import uk.ac.ed.ph.snuggletex.definitions.CombinerTargetMatcher;
import uk.ac.ed.ph.snuggletex.definitions.CommandType;
import uk.ac.ed.ph.snuggletex.definitions.CorePackageDefinitions;
import uk.ac.ed.ph.snuggletex.definitions.Globals;
import uk.ac.ed.ph.snuggletex.definitions.LaTeXMode;
import uk.ac.ed.ph.snuggletex.definitions.TextFlowContext;
import uk.ac.ed.ph.snuggletex.dombuilding.CommandHandler;
import uk.ac.ed.ph.snuggletex.dombuilding.EnvironmentHandler;
import uk.ac.ed.ph.snuggletex.dombuilding.InterpretableSimpleMathHandler;
import uk.ac.ed.ph.snuggletex.internal.util.ConstraintUtilities;
import uk.ac.ed.ph.snuggletex.semantics.Interpretation;
import uk.ac.ed.ph.snuggletex.semantics.InterpretationType;
import uk.ac.ed.ph.snuggletex.semantics.MathInterpretation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * A {@link SnugglePackage} defines a collection of {@link BuiltinCommand}s, {@link BuiltinEnvironment}s,
 * and {@link ErrorCode}s/{@link ErrorGroup}s that can be registered with a {@link SnuggleEngine}
 * to add in extra functionality.
 * <p>
 * The core SnuggleTeX distribution comes with what is essentially a built-in package defined
 * in {@link CorePackageDefinitions} which provides its core functionality.
 * <p>
 * <strong>Note:</strong> This replaces "DefinitionMap" from SnuggleTeX 1.0/1.1, which had fewer
 * features.
 * 
 * @see BuiltinCommand
 * @see BuiltinEnvironment
 * 
 * @since 1.2.0
 *
 * @author  David McKain
 * @version $Revision:179 $
 */
public final class SnugglePackage {
    
    /** Share instance of {@link InterpretableSimpleMathHandler} since it is stateless */
    public static final InterpretableSimpleMathHandler interpretableSimpleMathBuilder = new InterpretableSimpleMathHandler();
    
    /** Short name, used when formatting {@link ErrorCode}s */
    private final String name;
    
    /** Map of built-in commands, keyed on name */
    private final Map<String, BuiltinCommand> builtinCommandMap;
    
    /** Map of built-in environments, keyed on name */
    private final Map<String, BuiltinEnvironment> builtinEnvironmentMap;

    /** {@link List} of all {@link ErrorGroup}s defined by this package */
    private final LinkedHashMap<ErrorGroup, List<ErrorCode>> errorGroupMap;
    
    /** {@link ResourceBundle} providing details for formatting {@link ErrorCode}s */
    private ResourceBundle errorMessageBundle;
    
    public SnugglePackage(final String name) {
        ConstraintUtilities.ensureNotNull(name, "name");
        this.name = name;
        this.builtinCommandMap = new HashMap<String, BuiltinCommand>();
        this.builtinEnvironmentMap = new HashMap<String, BuiltinEnvironment>();
        this.errorGroupMap = new LinkedHashMap<ErrorGroup, List<ErrorCode>>();
    }
    
    /**
     * Returns the name for this package.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns all of the {@link ErrorGroup}s declared for this package. The resulting {@link Collection}
     * should not be modified.
     */
    public Collection<ErrorGroup> getErrorGroups() {
        return errorGroupMap.keySet();
    }
    
    /**
     * Returns the {@link ErrorCode}s corresponding to the given {@link ErrorGroup}.
     * The resulting {@link Collection} should not be modified.
     * 
     */
    public Collection<ErrorCode> getErrorCodes(ErrorGroup errorGroup) {
        return errorGroupMap.get(errorGroup);
    }

    
    /**
     * Returns the {@link ResourceBundle} used to format error messages.
     */
    public ResourceBundle getErrorMessageBundle() {
        return errorMessageBundle;
    }

    /**
     * Sets the {@link ResourceBundle} used to format error messages.
     */
    public void setErrorMessageBundle(ResourceBundle errorMessageBundle) {
        this.errorMessageBundle = errorMessageBundle;
    }

    
    /**
     * Returns the {@link BuiltinCommand} corresponding to LaTeX command called
     * <tt>\texName</tt> defined by this package, or null if this command is not defined.
     */
    public BuiltinCommand getBuiltinCommandByTeXName(String texName) {
        return builtinCommandMap.get(texName);
    }
    
    /**
     * Returns the {@link BuiltinEnvironment} corresponding to LaTeX environment
     * called <tt>texName</tt> defined by this package, or null if this environment is not defined.
     */
    public BuiltinEnvironment getBuiltinEnvironmentByTeXName(String texName) {
        return builtinEnvironmentMap.get(texName);
    }
    
    //-------------------------------------------------------
    
    /**
     * Tests whether the name of a command or environment is "inputable". Ones which cannot be
     * directly input are enclosed in angle brackets. These commands are created during token
     * fixing.
     * 
     * @param texName
     */
    public static boolean isInputableTeXName(final String texName) {
        return texName!=null && !(texName.charAt(0)=='<' && texName.length()>3 && texName.endsWith(">"));
    }
    
    //-------------------------------------------------------
    
    public BuiltinCommand addSimpleCommand(final String name, final EnumSet<LaTeXMode> allowedModes,
            final CommandHandler handler, final TextFlowContext context) {
        return addCommand(new BuiltinCommand(name, CommandType.SIMPLE, false, 0,
                allowedModes, null, null,
                handler, context, null));
    }
    
    public BuiltinCommand addSimpleCommand(final String name, final EnumSet<LaTeXMode> allowedModes,
            final Interpretation[] interpretations, final CommandHandler handler,
            final TextFlowContext context) {
        return addCommand(new BuiltinCommand(name, CommandType.SIMPLE, false, 0,
                allowedModes, null, makeInterpretationMap(interpretations),
                handler, context, null));
    }
    
    public BuiltinCommand addSimpleCommand(final String name, final EnumSet<LaTeXMode> allowedModes,
            final Interpretation interpretation, final CommandHandler handler,
            final TextFlowContext context) {
        return addCommand(new BuiltinCommand(name, CommandType.SIMPLE, false, 0,
                allowedModes, null, makeInterpretationMap(interpretation),
                handler, context, null));
    }

    public BuiltinCommand addSimpleMathCommand(final String name, final MathInterpretation... interpretations) {
        return addSimpleMathCommand(name, interpretations, interpretableSimpleMathBuilder);
    }
    
    public BuiltinCommand addSimpleMathCommand(final String name,
            final MathInterpretation interpretation, final CommandHandler handler) {
        return addCommand(new BuiltinCommand(name, CommandType.SIMPLE, false, 0,
                Globals.MATH_MODE_ONLY, null, makeInterpretationMap(interpretation),
                handler, null, null));
    }
    
    public BuiltinCommand addSimpleMathCommand(final String name,
            final MathInterpretation[] interpretations, final CommandHandler handler) {
        return addCommand(new BuiltinCommand(name, CommandType.SIMPLE, false, 0,
                Globals.MATH_MODE_ONLY, null, makeInterpretationMap(interpretations),
                handler, null, null));
    }
    
    public BuiltinCommand addCombinerCommand(final String name, final EnumSet<LaTeXMode> allowedModes,
            final CombinerTargetMatcher combinerTargetMatcher,
            final CommandHandler handler, final TextFlowContext context) {
        return addCommand(new BuiltinCommand(name, CommandType.COMBINER, false, 0,
                allowedModes, null,
                null, handler,
                context, combinerTargetMatcher));
    }
    
    public BuiltinCommand addComplexCommand(final String name, final boolean allowOptionalArgument,
            final int arguments, final EnumSet<LaTeXMode> allowedModes,
            final LaTeXMode[] argumentModes,
            final CommandHandler handler, final TextFlowContext context) {
        return addCommand(new BuiltinCommand(name, CommandType.COMPLEX, allowOptionalArgument, arguments,
                allowedModes, argumentModes,
                null, handler,
                context, null));
    }
    
    public BuiltinCommand addComplexCommandSameArgMode(final String name, final boolean allowOptionalArgument,
            final int arguments, final EnumSet<LaTeXMode> allowedModes, final CommandHandler handler,
            final TextFlowContext context) {
        return addCommand(new BuiltinCommand(name, CommandType.COMPLEX, allowOptionalArgument, arguments,
                allowedModes, null,
                null, handler,
                context, null));
    }
    
    public BuiltinCommand addComplexCommandSameArgMode(final String name, final boolean allowOptionalArgument,
            final int arguments, final EnumSet<LaTeXMode> allowedModes,
            final Interpretation interpretation, final CommandHandler handler,
            final TextFlowContext context) {
        return addCommand(new BuiltinCommand(name, CommandType.COMPLEX, allowOptionalArgument, arguments,
                allowedModes, null,
                makeInterpretationMap(interpretation), handler,
                context, null));
    }
    
    public BuiltinCommand addComplexCommandOneArg(final String name, final boolean allowOptionalArgument,
            final EnumSet<LaTeXMode> allowedModes, final LaTeXMode argumentMode,
            final CommandHandler handler, final TextFlowContext context) {
        return addCommand(new BuiltinCommand(name, CommandType.COMPLEX, allowOptionalArgument, 1,
                allowedModes, new LaTeXMode[] { argumentMode },
                null, handler,
                context, null));
    }
    
    public BuiltinCommand addComplexCommandOneArg(final String name, final boolean allowOptionalArgument,
            final EnumSet<LaTeXMode> allowedModes, final LaTeXMode argumentMode,
            final Interpretation interpretation, final CommandHandler handler,
            final TextFlowContext context) {
        return addCommand(new BuiltinCommand(name, CommandType.COMPLEX, allowOptionalArgument, 1,
                allowedModes, new LaTeXMode[] { argumentMode },
                makeInterpretationMap(interpretation), handler,
                context, null));
    }
    
    public BuiltinCommand addCommand(final BuiltinCommand command) {
        if (isInputableTeXName(command.getTeXName())) {
            builtinCommandMap.put(command.getTeXName(), command);
        }
        return command;
    }
    
    //-------------------------------------------------------
    
    public BuiltinEnvironment addEnvironment(final String name, final EnumSet<LaTeXMode> allowedModes,
            final LaTeXMode contentMode, final Interpretation interpretation,
            final EnvironmentHandler handler, final TextFlowContext context) {
        return addEnvironment(new BuiltinEnvironment(name, false, 0, allowedModes,
                contentMode, makeInterpretationMap(interpretation), handler, context));
    }
    
    public BuiltinEnvironment addEnvironment(final String name, final boolean allowOptionalArgument,
            final int argumentCount, final EnumSet<LaTeXMode> allowedModes,
            final LaTeXMode contentMode, final Interpretation interpretation,
            final EnvironmentHandler handler, final TextFlowContext context) {
        return addEnvironment(new BuiltinEnvironment(name, allowOptionalArgument, argumentCount,
                allowedModes, contentMode, makeInterpretationMap(interpretation), handler, context));
    }
    
    public BuiltinEnvironment addEnvironment(final BuiltinEnvironment environment) {
        if (isInputableTeXName(environment.getTeXName())) {
            builtinEnvironmentMap.put(environment.getTeXName(), environment);
        }
        return environment;
    }
    
    //-------------------------------------------------------
    
    public static EnumMap<InterpretationType, Interpretation> makeInterpretationMap(final Interpretation interpretation) {
        if (interpretation==null) {
            return null;
        }
        EnumMap<InterpretationType, Interpretation> result = new EnumMap<InterpretationType, Interpretation>(InterpretationType.class);
        result.put(interpretation.getType(), interpretation);
        return result;
    }
    
    public static EnumMap<InterpretationType, Interpretation> makeInterpretationMap(final Interpretation... interpretations) {
        if (interpretations.length==0) {
            return null;
        }
        EnumMap<InterpretationType, Interpretation> result = new EnumMap<InterpretationType, Interpretation>(InterpretationType.class);
        for (Interpretation interpretation : interpretations) {
            result.put(interpretation.getType(), interpretation);
        }
        return result;
    }
    
    //-------------------------------------------------------
    
    public void addErrorCode(ErrorCode errorCode) {
        ConstraintUtilities.ensureNotNull(errorCode, "errorCode");
        ErrorGroup errorGroup = errorCode.getErrorGroup();
        ConstraintUtilities.ensureNotNull(errorGroup, "errorCode.errorGroup");
        List<ErrorCode> errorCodesForGroup = errorGroupMap.get(errorGroup);
        if (errorCodesForGroup==null) {
            errorCodesForGroup = new ArrayList<ErrorCode>();
            errorGroupMap.put(errorGroup, errorCodesForGroup);
        }
        errorCodesForGroup.add(errorCode);
    }
    
    public void addErrorCodes(ErrorCode... errorCodes) {
        for (ErrorCode errorCode : errorCodes) {
            addErrorCode(errorCode);
        }
    }
}
