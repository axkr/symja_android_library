/* $Id: ObjectDumperOptions.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.internal.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used by {@link ObjectDumper} to specify how types or properties
 * should be dumped.
 * <p>
 * It can be applied to both types and properties (i.e. <tt>getP()</tt> methods).
 *
 * (This is copied from <tt>ph-commons-util</tt>.)
 * 
 * @author  David McKain
 * @version $Revision: 525 $
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface ObjectDumperOptions {
    
    DumpMode value() default DumpMode.TO_STRING;

}