/* $Id: ObjectDumper.java 539 2010-03-24 15:37:26Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.internal.util;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * Trivial but slightly useful helper for creating deep dumps of Objects and their
 * its properties, which can be useful for displaying status messages and debugging.
 *
 * (This is copied from <tt>ph-commons-util</tt>.)
 * 
 * @author  David McKain
 * @version $Revision: 539 $
 */
public final class ObjectDumper {
    
    public static int DEFAULT_INDENT_WIDTH = 4;
    public static int DEFAULT_MAX_DEPTH = 10;
    
    private static final String EMPTY = "(empty)";
    
    /** {@link StringBuilder} that the dump will append to */
    private final StringBuilder result;
    
    /** Number of spaces to indent at each level of Object depth */
    private int indentWidth;
    
    /** Maximum child Object depth to traverse to before issuing circularity error */
    private int maxDepth;
    
    public ObjectDumper(StringBuilder result) {
        this.result = result;
        this.indentWidth = DEFAULT_INDENT_WIDTH;
        this.maxDepth = DEFAULT_MAX_DEPTH;
    }
    
    //-------------------------------------------------------

    /**
     * Convenience method to do a {@link DumpMode#DEEP} dump of a single Object
     * and return the resulting String.
     */
    public static String dumpObject(Object object, DumpMode dumpMode) {
        StringBuilder builder = new StringBuilder();
        new ObjectDumper(builder).appendObject(object, dumpMode);
        return builder.toString();
    }
    
    //-------------------------------------------------------
    
    public int getIndentWidth() {
        return this.indentWidth;
    }
    
    public void setIndentWidth(int indentWidth) {
        this.indentWidth = indentWidth;
    }

    
    public int getMaxDepth() {
        return this.maxDepth;
    }
    
    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }
    
    //-------------------------------------------------------

    /**
     * Does a dump of the given Object, using whatever {@link DumpMode} is providied for its
     * class. If the Object is null, then {@link DumpMode#TO_STRING} is used.
     */
    public void appendObject(Object object) {
        DumpMode dumpMode = DumpMode.TO_STRING;
        if (object!=null) {
            dumpMode = getElementDumpMode(object, dumpMode);
        }
        appendObject(object, dumpMode);
    }
    
    /**
     * Does a dump of the given Object using the given {@link DumpMode}.
     * 
     * @param object
     * @param dumpMode if {@link DumpMode#DEEP} then the Object is dumped deeply, if
     *   {@link DumpMode#TO_STRING} then the Object's {@link #toString()} is called,
     *   if {@link DumpMode#IGNORE} then nothing happens.
     */
    public void appendObject(Object object, DumpMode dumpMode) {
        ConstraintUtilities.ensureNotNull(dumpMode, "dumpMode");
        appendObject(object, dumpMode, 0);
        result.append("\n");
    }
    
    //-------------------------------------------------------
    
    private void appendObject(Object object, DumpMode dumpMode, int depth) {
        /* First check depth */
        if (object!=null && depth > maxDepth) {
            result.append("[Maximum graph depth exceeded by child Object of type " + object.getClass() + "]");
            return;
        }
        /* Now decide what to dump */
        if (dumpMode==DumpMode.IGNORE) {
            /* Do nothing */
            return;
        }
        else if (object==null) {
            result.append("null");
        }
        else {
            if (object instanceof Object[]) {
                appendArray((Object[]) object, dumpMode, depth);
            }
            else if (object instanceof List<?>) {
                appendList((List<?>) object, dumpMode, depth);
            }
            else if (object instanceof Set<?>) {
                appendSet((Set<?>) object, dumpMode, depth);
            }
            else if (object instanceof Map<?,?>) {
                appendMap((Map<?,?>) object, dumpMode, depth);
            }
            else if (object instanceof Collection<?>) {
                appendCollection((Collection<?>) object, dumpMode, depth);
            }
            else {
                /* Now decide what to do */
                switch (dumpMode) {
                    case DEEP:
                        appendObjectDeep(object, depth);
                        break;
                        
                    case TO_STRING:
                        result.append(object.toString());
                        break;
                        
                    case IGNORE:
                        break;
                        
                    default:
                        throw new IllegalStateException("Unexpected DumpMode " + dumpMode);
                }
            }
        }
    }

    private void appendArray(Object[] array, DumpMode dumpMode, int depth) {
        Class<?> componentType = array.getClass().getComponentType();
        result.append(componentType.getName())
            .append("[]@")
            .append(Integer.toHexString(array.hashCode()))
            .append("[");
        if (array.length==0) {
            result.append(EMPTY);
        }
        else {
            result.append("\n");
            for (int i=0; i<array.length; i++) {
                makeIndent(depth+1);
                result.append(i).append(" => ");
                appendObject(array[i], getElementDumpMode(array[i], dumpMode), depth+1);
                result.append("\n");
            }
            makeIndent(depth);
        }
        result.append("]");
    }
    
    private <E> void appendSet(Set<E> set, DumpMode dumpMode, int depth) {
        Class<?> collectionClass = set.getClass();
        result.append(collectionClass.getName()).append("{");
        if (set.isEmpty()) {
            result.append(EMPTY);
        }
        else {
            result.append("\n");
            for (E element : set) {
                makeIndent(depth+1);
                appendObject(element, getElementDumpMode(element, dumpMode), depth+1);
                result.append("\n");
            }
            makeIndent(depth);
        }
        result.append("}");
    }
    
    private <E> void appendList(List<E> list, DumpMode dumpMode, int depth) {
        Class<?> collectionClass = list.getClass();
        result.append(collectionClass.getName()).append("[");
        if (list.isEmpty()) {
            result.append(EMPTY);
        }
        else {
            result.append("\n");
            E element;
            for (int i=0; i<list.size(); i++) {
                element = list.get(i);
                makeIndent(depth+1);
                result.append(i).append(" => ");
                appendObject(element, getElementDumpMode(element, dumpMode), depth+1);
                result.append("\n");
            }
            makeIndent(depth);
        }
        result.append("]");
    }
    
    private <E> void appendCollection(Collection<E> collection, DumpMode dumpMode, int depth) {
        Class<?> collectionClass = collection.getClass();
        result.append(collectionClass.getName()).append("[");
        if (collection.isEmpty()) {
            result.append(EMPTY);
        }
        else {
            result.append("\n");
            for (E element : collection) {
                makeIndent(depth+1);
                appendObject(element, getElementDumpMode(element, dumpMode), depth+1);
                result.append("\n");
            }
            makeIndent(depth);
        }

        result.append("]");
    }
    
    private <K,V> void appendMap(Map<K,V> map, DumpMode dumpMode, int depth) {
        Class<?> collectionClass = map.getClass();
        result.append(collectionClass.getName()).append("(");
        if (map.isEmpty()) {
            result.append(EMPTY);
        }
        else {
            result.append("\n");
            K key;
            V value;
            for (Entry<K,V> entry : map.entrySet()) {
                key = entry.getKey();
                value = entry.getValue();
                makeIndent(depth+1);
                result.append(key).append(" => ");
                appendObject(value, getElementDumpMode(value, dumpMode), depth+1);
                result.append("\n");
            }
            makeIndent(depth);
        }
        result.append(")");
    }
    
    private void appendObjectDeep(Object object, int depth) {
        Class<?> objectClass = object.getClass();
        result.append(objectClass.getName())
            .append("@")
            .append(Integer.toHexString(object.hashCode()))
            .append("(");
        
        /* Traverse all properties */
        boolean hasOutputProperty = false;
        Method[] methods = objectClass.getMethods();
        String methodName, rawPropertyName;
        for (Method method : methods) {
            /* See if we've got a getter method. If so, extract property name (with first char still
             * capitalised) */
            methodName = method.getName();
            if (methodName.startsWith("get") && method.getParameterTypes().length==0
                    && !methodName.equals("getClass") && methodName.length() > "get".length()) {
                rawPropertyName = methodName.substring("get".length());
            }
            else if (methodName.startsWith("is") && method.getParameterTypes().length==0
                    && methodName.length() > "is".length()) {
                rawPropertyName = methodName.substring("is".length());
            }
            else {
                continue;
            }
            /* See if a DumpMode has been explicitly provided for this property. */
            ObjectDumperOptions annotation = method.getAnnotation(ObjectDumperOptions.class);
            DumpMode targetDumpMode = (annotation!=null) ? annotation.value() : null;
            if (targetDumpMode==DumpMode.IGNORE) {
                /* We can ignore this property so let's bail out now */
                continue;
            }

            /* If still here then it's a getP() or isP() method that we've chosen to dump */
            if (!hasOutputProperty) {
                /* Need to do an initial newline before first property */
                result.append("\n");
                hasOutputProperty = true;
            }
            makeIndent(depth+1);
            
            /* Make first character after 'get' or 'is' lower case so that it really
             * is the name of the property */
            result.append(Character.toLowerCase(rawPropertyName.charAt(0)));
            if (methodName.length()>1) {
                result.append(rawPropertyName.substring(1));
            }
            result.append(" => ");
            
            /* Now get the value of the property */
            Object value = null;
            try {
                value = method.invoke(object);
            }
            catch (Exception e) {
                result.append("[Caused Exception ").append(e).append("]");
                continue;
            }
            
            /* If no DumpMode was specified for this property, then take the DumpMode of the
             * target's class. If nothing is specified then we'll use TO_STRING.
             */
            if (targetDumpMode==null) {
                targetDumpMode = getElementDumpMode(value, DumpMode.TO_STRING);
            }
            
            /* Dump property value in appropriate way */
            appendObject(value, targetDumpMode, depth+1);
            result.append("\n");
        }
        if (hasOutputProperty) {
            makeIndent(depth);
        }
        result.append(")");
    }
    
    /**
     * Works out the most appropriate {@link DumpMode} for the given Object by inspecting
     * its class for the relevant annotation.
     * 
     * @param object Object being dumped
     * @param currentDumpMode DumpMode currently in operation, used if nothing is specified
     *   for the Object's class or if the Object is null.
     */
    private DumpMode getElementDumpMode(Object object, DumpMode currentDumpMode) {
        DumpMode resultingDumpMode = currentDumpMode;
        if (object!=null) {
            ObjectDumperOptions annotation = object.getClass().getAnnotation(ObjectDumperOptions.class);
            if (annotation!=null) {
                resultingDumpMode = annotation.value();
            }
        }
        return resultingDumpMode;
    }
    
    private void makeIndent(int depth) {
        for (int i=0; i<depth * DEFAULT_INDENT_WIDTH; i++) {
            result.append(' ');
        }
    }
}
