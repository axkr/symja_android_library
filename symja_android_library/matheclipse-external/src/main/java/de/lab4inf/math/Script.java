/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2012,  Prof. Dr. Nikolaus Wulff
 * University of Applied Sciences, Muenster, Germany
 * Lab for Computer Sciences (Lab4Inf).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/

package de.lab4inf.math;

import java.io.Reader;
import java.util.Set;

import javax.script.Invocable;
import javax.script.ScriptException;

/**
 * Common Lab4Math script facade interface. This interface will be implemented
 * by appropriate javax.script engine implementation(s).
 * <p>
 * Within Lab4Math they are best compiled via the L4MScriptEngine:
 * <pre>
 *
 *   L4MScriptEngineFactor factory = L4MLoader.load(L4MScriptEngineFactory.class);
 *   L4MScriptEngine engine = factory.getL4MScriptEngine();;
 *
 *   String define = "a=0.4; mixedFct(x) = (1-a)*cos(x) + a*sin(x)";
 *   Script script = engine.parse(define);
 *   Function aFct = script.getFunction("mixedFct");
 *
 * </pre>
 *
 * @author nwulff
 * @version $Id: Script.java,v 1.5 2014/11/18 23:41:21 nwulff Exp $
 * @since 05.01.2012
 */
public interface Script extends Invocable {
    /**
     * Set the script definition via a String instance.
     *
     * @param script definition
     * @throws ScriptException in case of an error
     */
    void setScript(String script) throws ScriptException;

    /**
     * Set the script definition via a Reader instance.
     *
     * @param reader to use
     * @throws ScriptException in case of an error
     */
    void setScript(Reader reader) throws ScriptException;

    /**
     * Get a function implementation, maybe defined by the script.
     *
     * @param name of the function
     * @return function implementation/proxy
     * @throws ScriptException in case of an error
     */
    Function getFunction(String name) throws ScriptException;

    /**
     * Evaluate a function F via the script.
     *
     * @param name of function
     * @param x    function parameters
     * @return result F(x)
     * @throws ScriptException in case of an error
     */
    double evalFct(String name, double... x) throws ScriptException;

    /**
     * Get the value of a named variable.
     *
     * @param name of variable
     * @return value
     * @throws ScriptException in case of an error
     */
    double getVariable(String name) throws ScriptException;

    /**
     * Set the value for a named variable
     *
     * @param name  of variable
     * @param value to set
     * @throws ScriptException in case of an error
     */
    void setVariable(String name, double value) throws ScriptException;

    /**
     * Get the name of the last assigned named variable within the script context.
     *
     * @return name
     */
    String getRetString();

    /**
     * Get the name of the last defined new function within the script context.
     *
     * @return name
     */
    String getFctName();

    /**
     * Get the result of the last script execution.
     *
     * @return value
     */
    double getRetValue();

    /**
     * Get all script known function names
     *
     * @return set with function names
     */
    Set<String> getFunctionNames();

    /**
     * Get all script known variable names
     *
     * @return set with variable names
     */
    Set<String> getVariableNames();

    /**
     * Signal if the script should throw exception for unknown variables.
     *
     * @param shouldThrow flag to indicate exception
     */
    void setThrowing(boolean shouldThrow);
}
 