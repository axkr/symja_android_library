/*
 * Project: Lab4Math
 *
 * Copyright (c) 2006-2010,  Prof. Dr. Nikolaus Wulff
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

package de.lab4inf.math.sets;

import de.lab4inf.math.Operand;

/**
 * Multi-valued Goedel and Lukasiewicz logic.
 * This implementation is enhanced for usage with the Groovy scripting language
 * and has methods suitable for operator overloading. So you can do things like
 * <pre>
 *    z = x + y   // which is x OR y
 *    z = x * y   // which is x AND y
 *    z = x & y   // which is x AND y
 *    z = x | y   // which is x OR y
 *    z = x >> y  // which is z = x IMPLIES y
 * </pre>
 * for x,y,z as elements of a multi-valued logic.
 *
 * @author nwulff
 * @version $Id: MVLogic.java,v 1.4 2012/01/09 14:49:51 nwulff Exp $
 * @since 21.11.2010
 */

public abstract class MVLogic {
    private static final String DIFFERENT_LOGICS = "different logic types";
    private static final String DIFFERENT_ORDER = "different order";
    protected final int n, order;

    /**
     * Constructor for a logic element.
     *
     * @param q   the value with 0<=q<=max
     * @param max the maximal order
     */
    protected MVLogic(final int q, final int max) {
        n = q;
        order = max;
        if (max < 2) {
            throw new IllegalArgumentException("wrong max:" + max);
        }
        if (q < 0 || q > max) {
            throw new IllegalArgumentException("wrong q:" + q);
        }
    }

    /**
     * Logical implication p => q
     *
     * @param p antecedent
     * @param q consequent
     * @return truth of (p => q)
     */
    public static MVLogic implication(final MVLogic p, final MVLogic q) {
        return p.implication(q);
    }

    /**
     * Create the ONE element of the Goedel logic.
     *
     * @param max maximal element of the logic
     * @return the element with the truth value of one
     */
    public static GoedelSet createGoedelLogic(final int max) {
        return new GoedelSet(max - 1, max - 1);
    }

    /**
     * Create the ONE element of the Lukasiewicz logic.
     *
     * @param max maximal element of the logic
     * @return the element with the truth value of one
     */
    public static LucaSet createLucaLogic(final int max) {
        return new LucaSet(max - 1, max - 1);
    }

    /**
     * The truth value of this logic object.
     *
     * @return truth value
     */
    public double truthValue() {
        return ((double) n) / ((double) order);
    }

    /**
     * Internal check that the sets are of same type and order.
     * This method throws an IllegalArgumentException if the
     * given argument set does not match to this set.
     *
     * @param set to match against
     */
    protected void assertSetsMatch(final MVLogic set) {
        if (this.getClass() != set.getClass()) {
            throw new IllegalArgumentException(DIFFERENT_LOGICS);
        }
        if (set.order != this.order) {
            throw new IllegalArgumentException(DIFFERENT_ORDER);
        }
    }

    /**
     * Logical or operation.
     *
     * @param obj element to or.
     * @return this OR obj
     */
    @Operand(symbol = "|")
    public MVLogic or(final MVLogic obj) {
        assertSetsMatch(obj);
        int p = Math.max(n, obj.n);
        return createElement(p);
    }

    /**
     * Logical and operation.
     *
     * @param obj element to or.
     * @return this AND obj
     */
    @Operand(symbol = "&")
    public MVLogic and(final MVLogic obj) {
        assertSetsMatch(obj);
        int p = Math.min(n, obj.n);
        return createElement(p);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Operand(symbol = "==")
    @Override
    public boolean equals(final Object obj) {
        if (null == obj) {
            return false;
        } else if (this == obj) {
            return true;
        }
        if (this.getClass() == obj.getClass()) {
            MVLogic element = (MVLogic) obj;
            return (n == element.n) && (order == element.order);
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return n;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("%d/%d", n, order);
    }

    /**
     * Logical or for Groovy usage with the '+' operator.
     *
     * @param obj element to or.
     * @return this + obj
     */
    @Operand(symbol = "+")
    public MVLogic plus(final MVLogic obj) {
        return or(obj);
    }

    /**
     * Logical and for Groovy usage with the '*' operator.
     *
     * @param obj element to or.
     * @return this * obj
     */
    @Operand(symbol = "*")
    public MVLogic multiply(final MVLogic obj) {
        return and(obj);
    }

    /**
     * Logical negation for Groovy usage with the '~' prefix operator
     *
     * @return ~this
     */
    @Operand(symbol = "~")
    public MVLogic negate() {
        return not();
    }

    /**
     * Logical implication p >> q for Groovy script usage.
     *
     * @param q consequent
     * @return this implies q
     */
    @Operand(symbol = ">>")
    public MVLogic rightShift(final MVLogic q) {
        return implication(this, q);
    }

    /**
     * Logical negation.
     *
     * @return not this
     */
    public abstract MVLogic not();

    /**
     * Logical implication this => q
     *
     * @param q consequent
     * @return truth of (this => q)
     */
    public abstract MVLogic implication(final MVLogic q);

    /**
     * Factory method to construct logical elements.
     *
     * @param q truth degree with 0 <= q <= max
     * @return logical set
     */
    public abstract MVLogic createElement(final int q);

    static class GoedelSet extends MVLogic {

        /**
         * @param q
         * @param max
         */
        public GoedelSet(final int q, final int max) {
            super(q, max);
        }

        /* (non-Javadoc)
         * @see de.lab4inf.math.sets.MVLogic#createElement(int)
         */
        @Override
        public MVLogic createElement(final int q) {
            return new GoedelSet(q, order);
        }


        /* (non-Javadoc)
         * @see de.lab4inf.math.sets.MVLogic#implication(de.lab4inf.math.sets.MVLogic)
         */
        @Override
        public MVLogic implication(final MVLogic q) {
            assertSetsMatch(q);
            if (n <= q.n) {
                return createElement(order);
            }
            return createElement(0);
        }

        /* (non-Javadoc)
         * @see de.lab4inf.math.sets.MVLogic#not()
         */
        @Override
        public MVLogic not() {
            if (n == 0) {
                return createElement(order);
            }
            return createElement(0);
        }

        /*
         * (non-Javadoc)
         * @see de.lab4inf.math.sets.MVLogic#toString()
         */
        @Override
        public String toString() {
            return String.format("Goedel:%s", super.toString());
        }
    }

    static class LucaSet extends MVLogic {

        /**
         * @param q
         * @param max
         */
        public LucaSet(final int q, final int max) {
            super(q, max);
        }

        /* (non-Javadoc)
         * @see de.lab4inf.math.sets.MVLogic#not()
         */
        @Override
        public MVLogic not() {
            return createElement(order - n);
        }

        /* (non-Javadoc)
         * @see de.lab4inf.math.sets.MVLogic#implication(de.lab4inf.math.sets.MVLogic)
         */
        @Override
        public MVLogic implication(final MVLogic q) {
            assertSetsMatch(q);
            int t = Math.min(order, order - n + q.n);
            return createElement(t);
        }

        /* (non-Javadoc)
         * @see de.lab4inf.math.sets.MVLogic#createElement(int)
         */
        @Override
        public MVLogic createElement(final int q) {
            return new LucaSet(q, order);
        }

        /*
         * (non-Javadoc)
         * @see de.lab4inf.math.sets.MVLogic#toString()
         */
        @Override
        public String toString() {
            return String.format("Luca:%s", super.toString());
        }
    }
}
 
 
 