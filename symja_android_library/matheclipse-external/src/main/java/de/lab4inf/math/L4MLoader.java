/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2012,  Prof. Dr. Nikolaus Wulff
 * University of Applied Sciences, Muenster, Germany
 * Lab for computer sciences (Lab4Inf).
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

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

import static java.lang.String.format;

/**
 * Common ServiceLoader utility for Lab4Math services using
 * the java.util.ServiceLoader facility. With this loader
 * it is easy to obtain a reference to a Lab4Math service
 * via its interface. For example to integrate a function:
 * <pre>
 * Integrator service = L4MLoader.load(Integrator.class);
 * Function fct = new Sine();
 * double y = service.integrate(fct, 0.2, 0.4); // y = cos(0.2) - cos(0.4)
 * </pre>
 *
 * @param <T> type of service
 * @author nwulff
 * @version $Id: L4MLoader.java,v 1.8 2014/11/18 23:41:21 nwulff Exp $
 * @since 07.01.2012
 */
public final class L4MLoader<T> extends L4MObject {
    /**
     * Reference to L4MLoader.java.
     */
    static final String NO_IMPLEMENTATION_FOUND = "no implementation found";
    /**
     * Reference to L4MLoader.java.
     */
    static final String NOT_A_SERVICE = "is not a service";
    private static final Map<Class<?>, L4MLoader<?>> LOADERS = new HashMap<Class<?>, L4MLoader<?>>();
    private final Class<T> type;
    private ServiceLoader<T> loader;
    private T aService;

    private L4MLoader(final Class<T> type) {
        this.type = type;
    }

    /**
     * Internal method to get a cached or fresh loader for type T.
     *
     * @param clazz of the service interface
     * @return loader
     */
    static <T> L4MLoader<T> getLoader(final Class<T> clazz) {
        @SuppressWarnings("unchecked")
        L4MLoader<T> sl = (L4MLoader<T>) LOADERS.get(clazz);
        if (null == sl) {
            sl = new L4MLoader<T>(clazz);
            LOADERS.put(clazz, sl);
            getLogger().info(format("created %s", sl));
        }
        return sl;
    }

    /**
     * Load a service of type T
     *
     * @param clazz object for T
     * @param <T>   type of service
     * @return T instance
     */
    public static <T> T load(final Class<T> clazz) {
        final L4MLoader<T> sl = getLoader(clazz);
        return sl.loadService(clazz);
    }

    /**
     * Internal wrapper for the java.util.ServiceLoader
     *
     * @param clazz the type of the loader
     * @return instance of type T
     */
    private T loadService(final Class<T> clazz) {
        String msg;
        if (null == aService) {
            final String fmt = "%s %s";
            final Annotation ano = clazz.getAnnotation(Service.class);
            if (null == ano) {
                msg = format(fmt, NOT_A_SERVICE, clazz.getName());
                getLogger().info(msg);
                throw new IllegalArgumentException(msg);
            }
            loader = ServiceLoader.load(clazz);
            final Iterator<T> ite = loader.iterator();
            if (ite.hasNext()) {
                aService = ite.next();
            }
            if (null == aService) {
                msg = format(fmt, NO_IMPLEMENTATION_FOUND, clazz.getName());
                getLogger().info(msg);
                throw new IllegalArgumentException(msg);
            }
            msg = format("%s implements %s", aService.getClass().getSimpleName(), clazz.getSimpleName());
            getLogger().info(msg);
        }
        return aService;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.L4MObject#toString()
     */
    @Override
    public String toString() {
        return format("%s-%s", getClass().getSimpleName(), type.getSimpleName());
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.L4MObject#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object o) {
        if (super.equals(o)) {
            return type.equals(((L4MLoader<?>) o).type);
        }
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.L4MObject#hashCode()
     */
    @Override
    public int hashCode() {
        return type.hashCode();
    }
}
 