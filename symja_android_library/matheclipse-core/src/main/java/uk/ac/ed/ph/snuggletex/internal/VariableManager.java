/* $Id: VariableManager.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.internal;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages the values of user-defined variables.
 * 
 * @since 1.2.0
 *
 * @author  David McKain
 * @version $Revision: 525 $
 */
public final class VariableManager {
    
    /** Map of namespace -> variableName -> value */
    private final Map<String, Map<String, Object>> variableMap;
    
    public VariableManager() {
        this.variableMap = new HashMap<String, Map<String, Object>>();
    }
    
    public Object getVariable(String namespace, String variableName) {
        Map<String, Object> byNamespaceMap = variableMap.get(namespace);
        return byNamespaceMap!=null ? byNamespaceMap.get(variableName) : null;
    }
    
    public Map<String, Object> getVariableMapForNamespace(String namespace) {
        return variableMap.get(namespace);
    }
    
    public void setVariable(String namespace, String variableName, Object value) {
        Map<String, Object> byNamespaceMap = variableMap.get(namespace);
        if (byNamespaceMap==null) {
            byNamespaceMap = new HashMap<String, Object>();
            variableMap.put(namespace, byNamespaceMap);
        }
        byNamespaceMap.put(variableName, value);
    }
    
}
