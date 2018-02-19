/*
 * $Id$
 */

package edu.jas.poly;


import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import edu.jas.kern.PrettyPrint;
import edu.jas.structure.RingElem;


/**
 * RelationTable for solvable polynomials. This class maintains the
 * non-commutative multiplication relations of solvable polynomial rings. The
 * table entries are initialized with relations of the form x<sub>j</sub> *
 * x<sub>i</sub> = p<sub>ij</sub>. During multiplication the relations are
 * updated by relations of the form x<sub>j</sub><sup>k</sup> *
 * x<sub>i</sub><sup>l</sup> = p<sub>ijkl</sub>. If no relation for
 * x<sub>j</sub> * x<sub>i</sub> is found in the table, this multiplication is
 * assumed to be commutative x<sub>i</sub> x<sub>j</sub>. Can also be used for
 * relations between coefficients and main variables.
 *
 * @author Heinz Kredel
 */

public class RelationTable<C extends RingElem<C>> implements Serializable {


    private static final Logger logger = Logger.getLogger(RelationTable.class);
    private static final boolean debug = logger.isDebugEnabled();
    /**
     * The data structure for the relations.
     */
    public final Map<List<Integer>, List> table;
    /**
     * The factory for the solvable polynomial ring.
     */
    public final GenSolvablePolynomialRing<C> ring;
    /**
     * Usage indicator: table or coeffTable.
     */
    public final boolean coeffTable;


    /**
     * Constructor for RelationTable requires ring factory. Note: This
     * constructor is called within the constructor of the ring factory, so
     * methods of this class can only be used after the other constructor has
     * terminated.
     *
     * @param r solvable polynomial ring factory.
     */
    public RelationTable(GenSolvablePolynomialRing<C> r) {
        this(r, false);
    }


    /**
     * Constructor for RelationTable requires ring factory. Note: This
     * constructor is called within the constructor of the ring factory, so
     * methods of this class can only be used after the other constructor has
     * terminated.
     *
     * @param r          solvable polynomial ring factory.
     * @param coeffTable indicator for coeffTable.
     */
    public RelationTable(GenSolvablePolynomialRing<C> r, boolean coeffTable) {
        table = new HashMap<List<Integer>, List>();
        ring = r;
        if (ring == null) {
            throw new IllegalArgumentException("RelationTable no ring");
        }
        this.coeffTable = coeffTable;
    }


    /**
     * RelationTable equals. Tests same keySets and base relations.
     *
     * @see Object#equals(Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object p) {
        if (p == null) {
            return false;
        }
        if (!(p instanceof RelationTable)) {
            logger.info("no RelationTable");
            return false;
        }
        RelationTable<C> tab = (RelationTable<C>) p;
        // not possible because of infinite recursion:
        //if (!ring.equals(tab.ring)) {
        //    logger.info("not same Ring " + ring.toScript() + ", " + tab.ring.toScript());
        //    return false;
        //}
        if (!table.keySet().equals(tab.table.keySet())) {
            logger.info("keySet != :  a = " + table.keySet() + ", b = " + tab.table.keySet());
            return false;
        }
        for (Map.Entry<List<Integer>, List> me : table.entrySet()) {
            List<Integer> k = me.getKey();
            List a = me.getValue();
            List b = tab.table.get(k);
            // check contents, but only base relations
            Map<ExpVectorPair, GenPolynomial<C>> t1ex = fromListDeg2(a);
            Map<ExpVectorPair, GenPolynomial<C>> t2ex = fromListDeg2(b);
            if (!equalMaps(t1ex, t2ex)) {
                //System.out.println("a != b, a = " + t1ex + ", b = " + t2ex);
                return false;
            }
        }
        return true;
    }


    /**
     * Convert mixed list to map for base relations.
     *
     * @param a mixed list
     * @returns a map constructed from the list with deg(key) == 2.
     */
    @SuppressWarnings("unchecked")
    Map<ExpVectorPair, GenPolynomial<C>> fromListDeg2(List a) {
        Map<ExpVectorPair, GenPolynomial<C>> tex = new HashMap<ExpVectorPair, GenPolynomial<C>>();
        Iterator ait = a.iterator();
        while (ait.hasNext()) {
            ExpVectorPair ae = (ExpVectorPair) ait.next();
            if (!ait.hasNext()) {
                break;
            }
            GenPolynomial<C> p = (GenPolynomial<C>) ait.next();
            if (ae.totalDeg() == 2) { // only base relations
                //System.out.println("ae => p: " + ae + " => " + p);
                tex.put(ae, p);
            }
        }
        return tex;
    }


    /**
     * Hash code for base relations.
     *
     * @param a mixed list
     * @returns hashCode(a)
     */
    @SuppressWarnings("unchecked")
    int fromListDeg2HashCode(List a) {
        int h = 0;
        Iterator ait = a.iterator();
        while (ait.hasNext()) {
            ExpVectorPair ae = (ExpVectorPair) ait.next();
            h = 31 * h + ae.hashCode();
            if (!ait.hasNext()) {
                break;
            }
            GenPolynomial<C> p = (GenPolynomial<C>) ait.next();
            if (ae.totalDeg() == 2) { // only base relations
                //System.out.println("ae => p: " + ae + " => " + p);
                h = 31 * h + p.val.hashCode(); // avoid hash of ring
            }
        }
        return h;
    }


    /**
     * Equals for special maps.
     *
     * @param m1 first map
     * @param m2 second map
     * @returns true if both maps are equal
     */
    @SuppressWarnings("unchecked")
    boolean equalMaps(Map<ExpVectorPair, GenPolynomial<C>> m1, Map<ExpVectorPair, GenPolynomial<C>> m2) {
        if (!m1.keySet().equals(m2.keySet())) {
            return false;
        }
        for (Map.Entry<ExpVectorPair, GenPolynomial<C>> me : m1.entrySet()) {
            GenPolynomial<C> p1 = me.getValue();
            ExpVectorPair ep = me.getKey();
            GenPolynomial<C> p2 = m2.get(ep);
            if (p1.compareTo(p2) != 0) { // not working: !p1.equals(p2)) { // TODO
                logger.info("ep = " + ep + ", p1 = " + p1 + ", p2 = " + p2);
                //logger.info("p1.compareTo(p2) = " + p1.compareTo(p2));
                //logger.info("p1.equals(p2) = " + p1.equals(p2));
                return false;
            }
        }
        return true;
    }


    /**
     * Hash code for this relation table.
     *
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        //int h = ring.hashCode(); // infinite recursion
        int h = 0; //table.hashCode();
        h = table.keySet().hashCode();
        for (Map.Entry<List<Integer>, List> me : table.entrySet()) {
            //List<Integer> k = me.getKey();
            List a = me.getValue();
            int t1 = fromListDeg2HashCode(a);
            h = 31 * h + t1;
        }
        return h;
    }


    /**
     * Test if the table is empty.
     *
     * @return true if the table is empty, else false.
     */
    public boolean isEmpty() {
        return table.isEmpty();
    }


    /**
     * Get the String representation.
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
        List v;
        StringBuffer s = new StringBuffer("RelationTable[");
        boolean first = true;
        for (Map.Entry<List<Integer>, List> me : table.entrySet()) {
            List<Integer> k = me.getKey();
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            s.append(k.toString());
            v = me.getValue();
            s.append("=");
            s.append(v.toString());
        }
        s.append("]");
        return s.toString();
    }


    /**
     * Get the String representation.
     *
     * @param vars names for the variables.
     * @see Object#toString()
     */
    @SuppressWarnings({"unchecked", "cast"})
    public String toString(String[] vars) {
        if (vars == null) {
            return toString();
        }
        StringBuffer s = new StringBuffer("");
        String[] cvars = null;
        if (coeffTable) {
            if (ring.coFac instanceof GenPolynomialRing) {
                cvars = ((GenPolynomialRing<C>) (Object) ring.coFac).getVars();
            } else if (ring.coFac instanceof GenWordPolynomialRing) {
                cvars = ((GenWordPolynomialRing<C>) (Object) ring.coFac).getVars();
            }
            s.append("Coefficient ");
        }
        s.append("RelationTable\n(");
        List v;
        if (PrettyPrint.isTrue()) {
            boolean first = true;
            for (Map.Entry<List<Integer>, List> me : table.entrySet()) {
                //List<Integer> k = me.getKey();
                if (first) {
                    first = false;
                    s.append("\n");
                } else {
                    s.append(",\n");
                }
                v = me.getValue();
                for (Iterator jt = v.iterator(); jt.hasNext(); ) {
                    ExpVectorPair ep = (ExpVectorPair) jt.next();
                    GenSolvablePolynomial<C> p = (GenSolvablePolynomial<C>) jt.next();
                    if (ep.totalDeg() != 2) { // only base relations
                        continue;
                    }
                    s.append("( " + ep.getFirst().toString(vars) + " ), ");
                    if (cvars == null) {
                        s.append("( " + ep.getSecond().toString(vars) + " ), ");
                    } else {
                        s.append("( " + ep.getSecond().toString(cvars) + " ), ");
                    }
                    s.append("( " + p.toString(vars) + " )");
                    if (jt.hasNext()) {
                        s.append(",\n");
                    }
                }
            }
        } else {
            boolean first = true;
            for (Map.Entry<List<Integer>, List> me : table.entrySet()) {
                //List<Integer> k = me.getKey();
                if (first) {
                    first = false;
                } else {
                    s.append(",\n");
                }
                v = me.getValue();
                for (Iterator jt = v.iterator(); jt.hasNext(); ) {
                    ExpVectorPair ep = (ExpVectorPair) jt.next();
                    s.append("( " + ep.getFirst().toString(vars) + " ), ");
                    if (cvars == null) {
                        s.append("( " + ep.getSecond().toString(vars) + " ), ");
                    } else {
                        s.append("( " + ep.getSecond().toString(cvars) + " ), ");
                    }
                    GenSolvablePolynomial<C> p = (GenSolvablePolynomial<C>) jt.next();
                    //s.append("( " + p.toString(vars) + " )");
                    s.append(" " + p.toString(vars));
                    if (jt.hasNext()) {
                        s.append(",\n");
                    }
                }
            }
        }
        s.append("\n)\n");
        return s.toString();
    }


    /**
     * Get a scripting compatible string representation.
     *
     * @return script compatible representation for this relation table.
     */
    @SuppressWarnings({"unchecked", "cast"})
    public String toScript() {
        // Python case
        String[] vars = ring.vars;
        String[] cvars = null;
        if (coeffTable) {
            if (ring.coFac instanceof GenPolynomialRing) {
                cvars = ((GenPolynomialRing<C>) (Object) ring.coFac).getVars();
            } else if (ring.coFac instanceof GenWordPolynomialRing) {
                cvars = ((GenWordPolynomialRing<C>) (Object) ring.coFac).getVars();
            }
        }
        List v;
        StringBuffer s = new StringBuffer("[");
        boolean first = true;
        for (Map.Entry<List<Integer>, List> me : table.entrySet()) {
            //List<Integer> k = me.getKey();
            if (first) {
                first = false;
                s.append("");
            } else {
                s.append(", ");
            }
            v = me.getValue();
            for (Iterator jt = v.iterator(); jt.hasNext(); ) {
                ExpVectorPair ep = (ExpVectorPair) jt.next();
                GenPolynomial<C> p = (GenPolynomial<C>) jt.next();
                if (ep.totalDeg() > 2) { // only base relations
                    continue;
                }
                s.append("" + ep.getFirst().toScript(vars) + ", ");
                if (coeffTable) {
                    String eps = ep.getSecond().toScript(cvars);
                    if (eps.isEmpty()) { // if from a deeper down ring
                        eps = p.leadingBaseCoefficient().abs().toScript();
                    }
                    s.append("" + eps + ", ");
                } else {
                    s.append("" + ep.getSecond().toScript(vars) + ", ");
                }
                //s.append("( " + p.toScript() + " )");
                s.append(" " + p.toScript());
                if (jt.hasNext()) {
                    s.append(", ");
                }
            }
        }
        s.append("]");
        return s.toString();
    }


    /**
     * Update or initialize RelationTable with new relation. relation is e * f =
     * p.
     *
     * @param e first term.
     * @param f second term.
     * @param p solvable product polynomial.
     */
    @SuppressWarnings({"unchecked", "cast"})
    public synchronized void update(ExpVector e, ExpVector f, GenSolvablePolynomial<C> p) {
        if (p == null || e == null || f == null) {
            throw new IllegalArgumentException("RelationTable update e|f|p == null");
        }
        GenSolvablePolynomialRing<C> sring = p.ring;
        if (debug) {
            logger.info("new relation = " + sring.toScript(e) + " .*. " + sring.toScript(f) + " = "
                    + p.toScript());
        }
        // test equal HTs for left and right side
        if (!coeffTable) { // old case
            if (e.totalDeg() == 1 && f.totalDeg() == 1) { // higher or mixed degrees TODO
                int[] de = e.dependencyOnVariables();
                int[] df = f.dependencyOnVariables();
                logger.debug("update e ? f " + de[0] + " " + df[0]);
                //int t = ring.tord.getDescendComparator().compare(e,f);
                //System.out.println("update compare(e,f) = " + t);
                if (de[0] == df[0]) { // error 
                    throw new IllegalArgumentException("RelationTable update e==f");
                }
                if (de[0] > df[0]) { // invalid update 
                    logger.error("update e < f: " + sring.toScript(e) + " < " + sring.toScript(f));
                    //if (debug && false) {
                    //    throw new IllegalArgumentException("update e < f");
                    //}
                    ExpVector tmp = e;
                    e = f;
                    f = tmp;
                    Map.Entry<ExpVector, C> m = p.leadingMonomial();
                    ExpVector ef = e.sum(f);
                    if (!ef.equals(m.getKey())) {
                        throw new IllegalArgumentException("update e*f != lt(p): " + sring.toScript(ef)
                                + ", lt = " + sring.toScript(m.getKey()));
                    }
                    GenPolynomial<C> r = p.reductum(); //subtract(m.getValue(), m.getKey());
                    r = r.negate();
                    //p = (GenSolvablePolynomial<C>) r.sum(m.getValue(), m.getKey());
                    p = (GenSolvablePolynomial<C>) r;
                    p.doPutToMap(m.getKey(), m.getValue());
                }
            }
            ExpVector ef = e.sum(f);
            ExpVector lp = p.leadingExpVector();
            if (!ef.equals(lp)) { // check for suitable term order
                logger.error("relation term order = " + ring.tord);
                throw new IllegalArgumentException("update e*f != lt(p): " + sring.toScript(ef) + " != "
                        + sring.toScript(lp));
            }
        } else { // is coeffTable
            ExpVector lp = p.leadingExpVector();
            if (!e.equals(lp)) { // check for suitable term order
                logger.error("relation term order = " + ring.tord);
                throw new IllegalArgumentException("Coefficient RelationTable update e != lt(p): "
                        + sring.toScript(e) + " != " + sring.toScript(lp));
            }
            if (p.leadingBaseCoefficient() instanceof GenPolynomial) {
                lp = ((GenPolynomial<C>) (Object) p.leadingBaseCoefficient()).leadingExpVector();
                if (!f.equals(lp)) { // check for suitable term order
                    logger.error("relation term order = " + ring.tord);
                    logger.error("Coefficient RelationTable update f != lt(lfcd(p)): " + sring.toScript(e)
                            + ", f = " + f + ", p = " + p.toScript());
                    throw new IllegalArgumentException("Coefficient RelationTable update f != lt(lfcd(p)): "
                            + e + ", f = " + f + ", p = " + p);
                }
            } else if (p.leadingBaseCoefficient() instanceof GenWordPolynomial) {
                lp = ((GenWordPolynomial<C>) (Object) p.leadingBaseCoefficient()).leadingWord()
                        .leadingExpVector();
                if (!f.equals(lp)) { // check for suitable term order and word structure
                    logger.error("relation term order = " + ring.tord);
                    logger.error("Coefficient RelationTable update f != lt(lfcd(p)): " + sring.toScript(e)
                            + ", f = " + f + ", p = " + p.toScript());
                    throw new IllegalArgumentException("Coefficient RelationTable update f != lt(lfcd(p)): "
                            + e + ", f = " + f + ", p = " + p);
                }
            }
        }
        List<Integer> key = makeKey(e, f);
        ExpVectorPair evp = new ExpVectorPair(e, f);
        if (key.size() != 2) {
            logger.warn("key = " + key + ", evp = " + evp);
        }
        List part = table.get(key);
        if (part == null) { // initialization only
            part = new LinkedList();
            part.add(evp);
            part.add(p);
            table.put(key, part);
            return;
        }
        @SuppressWarnings("unused")
        Object skip;
        int index = -1;
        synchronized (part) { // with lookup()
            for (ListIterator it = part.listIterator(); it.hasNext(); ) {
                ExpVectorPair look = (ExpVectorPair) it.next();
                skip = it.next(); // skip poly
                if (look.isMultiple(evp)) {
                    index = it.nextIndex();
                    // last index of or first index of: break
                }
            }
            if (index < 0) {
                index = 0;
            }
            part.add(index, evp);
            part.add(index + 1, p);
        }
        // table.put( key, part ); // required??
    }


    /**
     * Update or initialize RelationTable with new relation. relation is e * f =
     * p.
     *
     * @param E first term polynomial.
     * @param F second term polynomial.
     * @param p solvable product polynomial.
     */
    @SuppressWarnings({"unchecked", "cast"})
    public void update(GenPolynomial<C> E, GenPolynomial<C> F, GenSolvablePolynomial<C> p) {
        if (E.isZERO() || F.isZERO()) {
            throw new IllegalArgumentException("polynomials may not be zero: " + E + ", " + F);
        }
        C ce = E.leadingBaseCoefficient();
        C cf = F.leadingBaseCoefficient();
        if (!ce.isONE()) {
            throw new IllegalArgumentException("lbcf of polynomials must be one: " + ce + ", " + cf
                    + ", p = " + p);
        }
        ExpVector e = E.leadingExpVector();
        ExpVector f = F.leadingExpVector();
        if (coeffTable && f.isZERO()) {
            if (cf instanceof GenPolynomial) {
                f = ((GenPolynomial<C>) (Object) cf).leadingExpVector();
            } else if (cf instanceof GenWordPolynomial) {
                f = ((GenWordPolynomial<C>) (Object) cf).leadingWord().leadingExpVector();
            }
        }
        //logger.info("update: " + e + " .*. " + f + " = " + p);
        update(e, f, p);
    }


    /**
     * Update or initialize RelationTable with new relation. relation is e * f =
     * p.
     *
     * @param E first term polynomial.
     * @param F second term polynomial.
     * @param p product polynomial.
     */
    public void update(GenPolynomial<C> E, GenPolynomial<C> F, GenPolynomial<C> p) {
        if (p.isZERO()) {
            throw new IllegalArgumentException("polynomial may not be zero: " + p);
        }
        if (p.isONE()) {
            throw new IllegalArgumentException("product of polynomials may not be one: " + p);
        }
        GenSolvablePolynomial<C> sp = new GenSolvablePolynomial<C>(ring, p.val);
        update(E, F, sp);
    }


    /**
     * Update or initialize RelationTable with new relation. relation is e * f =
     * p.
     *
     * @param e first term.
     * @param f second term.
     * @param p solvable product polynomial.
     */
    public void update(ExpVector e, ExpVector f, GenPolynomial<C> p) {
        if (p.isZERO()) {
            throw new IllegalArgumentException("polynomial may not be zero: " + p);
        }
        if (p.isONE()) {
            throw new IllegalArgumentException("product of polynomials may not be one: " + p);
        }
        GenSolvablePolynomial<C> sp = new GenSolvablePolynomial<C>(ring, p.val);
        update(e, f, sp);
    }


    /**
     * Lookup RelationTable for existing relation. Find p with e * f = p. If no
     * relation for e * f is contained in the table then return the symmetric
     * product p = 1 e f.
     *
     * @param e first term.
     * @param f second term.
     * @return t table relation container, contains e' and f' with e f = e'
     * lt(p) f'.
     */
    @SuppressWarnings({"unchecked", "cast"})
    public TableRelation<C> lookup(ExpVector e, ExpVector f) {
        List<Integer> key = makeKey(e, f);
        List part = table.get(key);
        if (part == null) { // symmetric product
            GenSolvablePolynomial<C> p = null;
            C c = null;
            if (!coeffTable) {
                ExpVector ef = e.sum(f);
                //p = new GenSolvablePolynomial<C>(ring, ring.getONECoefficient(), ef);
                p = ring.valueOf(ef);
            } else {
                if (ring.coFac instanceof GenPolynomialRing) {
                    GenPolynomialRing<C> cofac = (GenPolynomialRing<C>) (Object) ring.coFac;
                    //System.out.println("f = " + f + ", e = " + e);
                    GenPolynomial<C> pc = cofac.valueOf(f);
                    c = (C) (Object) pc;
                } else if (ring.coFac instanceof GenWordPolynomialRing) {
                    GenWordPolynomialRing<C> cofac = (GenWordPolynomialRing<C>) (Object) ring.coFac;
                    //System.out.println("f = " + f + ", e = " + e);
                    GenWordPolynomial<C> pc = cofac.valueOf(f);
                    c = (C) (Object) pc;
                }
                p = new GenSolvablePolynomial<C>(ring, c, e);
                //System.out.println("pc = " + pc + ", p = " + p);
            }
            return new TableRelation<C>(null, null, p);
        }
        // no distinction between coefficient f or polynomial f
        ExpVectorPair evp = new ExpVectorPair(e, f);
        ExpVector ep = null;
        ExpVector fp = null;
        ExpVectorPair look = null;
        GenSolvablePolynomial<C> p = null;
        synchronized (part) { // with update()
            for (Iterator it = part.iterator(); it.hasNext(); ) {
                look = (ExpVectorPair) it.next();
                p = (GenSolvablePolynomial<C>) it.next();
                if (evp.isMultiple(look)) {
                    ep = e.subtract(look.getFirst());
                    fp = f.subtract(look.getSecond());
                    if (ep.isZERO()) {
                        ep = null;
                    }
                    if (fp.isZERO()) {
                        fp = null;
                    }
                    if (debug) {
                        if (p != null && p.ring.vars != null) {
                            logger.info("found relation = " + e.toString(p.ring.vars) + " .*. "
                                    + f.toString(p.ring.vars) + " = " + p);
                        } else {
                            logger.info("found relation = " + e + " .*. " + f + " = " + p);
                        }
                    }
                    return new TableRelation<C>(ep, fp, p);
                }
            }
        }
        // unreacheable code!
        throw new RuntimeException("no entry found in relation table for " + evp);
    }


    /**
     * Construct a key for (e,f).
     *
     * @param e first term.
     * @param f second term.
     * @return k key for (e,f).
     */
    protected List<Integer> makeKey(ExpVector e, ExpVector f) {
        int[] de = e.dependencyOnVariables();
        int[] df = f.dependencyOnVariables();
        List<Integer> key = new ArrayList<Integer>(de.length + df.length);
        for (int i = 0; i < de.length; i++) {
            key.add(Integer.valueOf(de[i]));
        }
        for (int i = 0; i < df.length; i++) {
            key.add(Integer.valueOf(df[i]));
        }
        return key;
    }


    /**
     * Size of the table.
     *
     * @return n number of non-commutative relations.
     */
    public int size() {
        int s = 0;
        if (table == null || table.isEmpty()) {
            return s;
        }
        for (Iterator<List> it = table.values().iterator(); it.hasNext(); ) {
            List list = it.next();
            s += list.size() / 2;
        }
        return s;
    }


    /**
     * Extend variables. Used e.g. in module embedding. Extend all ExpVectors
     * and polynomials of the given relation table by i elements and put the
     * relations into this table, i.e. this should be empty.
     *
     * @param tab a relation table to be extended and inserted into this.
     */
    @SuppressWarnings("unchecked")
    public void extend(RelationTable<C> tab) {
        if (tab.table.isEmpty()) {
            return;
        }
        // assert this.size() == 0
        int i = ring.nvar - tab.ring.nvar;
        int j = 0;
        long k = 0l;
        List val;
        for (List<Integer> key : tab.table.keySet()) {
            val = tab.table.get(key);
            for (Iterator jt = val.iterator(); jt.hasNext(); ) {
                ExpVectorPair ep = (ExpVectorPair) jt.next();
                ExpVector e = ep.getFirst();
                ExpVector f = ep.getSecond();
                GenSolvablePolynomial<C> p = (GenSolvablePolynomial<C>) jt.next();
                ExpVector ex = e.extend(i, j, k);
                ExpVector fx;
                if (coeffTable) {
                    fx = f;
                } else {
                    fx = f.extend(i, j, k);
                }
                GenSolvablePolynomial<C> px = (GenSolvablePolynomial<C>) p.extend(ring, j, k);
                this.update(ex, fx, px);
            }
        }
        return;
    }


    /**
     * Contract variables. Used e.g. in module embedding. Contract all
     * ExpVectors and polynomials of the given relation table by i elements and
     * put the relations into this table, i.e. this should be empty.
     *
     * @param tab a relation table to be contracted and inserted into this.
     */
    @SuppressWarnings("unchecked")
    public void contract(RelationTable<C> tab) {
        if (tab.table.isEmpty()) {
            return;
        }
        // assert this.size() == 0
        int i = tab.ring.nvar - ring.nvar;
        List val;
        for (List<Integer> key : tab.table.keySet()) {
            val = tab.table.get(key);
            //System.out.println("key = " + key + ", val = " + val);
            for (Iterator jt = val.iterator(); jt.hasNext(); ) {
                ExpVectorPair ep = (ExpVectorPair) jt.next();
                ExpVector e = ep.getFirst();
                ExpVector f = ep.getSecond();
                GenSolvablePolynomial<C> p = (GenSolvablePolynomial<C>) jt.next();
                ExpVector ec = e.contract(i, e.length() - i);
                ExpVector fc;
                if (coeffTable) {
                    fc = f;
                } else {
                    fc = f.contract(i, f.length() - i);
                }
                //System.out.println("ec = " + ec + ", fc = " + fc);
                if (ec.isZERO()) {
                    continue;
                }
                Map<ExpVector, GenPolynomial<C>> mc = p.contract(ring);
                if (mc.size() != 1) {
                    continue;
                }
                GenPolynomial<C> pc = mc.values().iterator().next();
                this.update(ec, fc, pc);
            }
        }
        return;
    }


    /**
     * Recursive representation. Split all ExpVectors and polynomials of the
     * given relation table to lower elements and upper elements and put the
     * relations into this table or this as coefficient table, i.e. this should
     * be empty.
     *
     * @param tab a relation table to be contracted and inserted into this.
     */
    @SuppressWarnings({"unchecked", "cast"})
    public void recursive(RelationTable tab) { //<C>
        if (tab.table.isEmpty()) {
            return;
        }
        //System.out.println("rel ring = " + ring.toScript());
        // assert this.size() == 0
        GenPolynomialRing<C> cring = (GenPolynomialRing<C>) (Object) ring.coFac;
        //System.out.println("cring    = " + cring.toScript());
        GenSolvablePolynomial<C> pc;
        int i = ring.nvar; // tab.ring.nvar -
        for (Object okey : tab.table.keySet()) {
            List<Integer> key = (List<Integer>) okey;
            List val = (List) tab.table.get(key);
            //System.out.println("key = " + key + ", val = " + val);
            for (Iterator jt = val.iterator(); jt.hasNext(); ) {
                ExpVectorPair ep = (ExpVectorPair) jt.next();
                ExpVector e = ep.getFirst();
                ExpVector f = ep.getSecond();
                GenSolvablePolynomial<C> p = (GenSolvablePolynomial<C>) jt.next();
                ExpVector ec = e.contract(0, i);
                ExpVector fc;
                if (coeffTable) {
                    fc = f;
                } else {
                    fc = f.contract(0, i);
                }
                //System.out.println("ec = " + ec + ", fc = " + fc);
                if (ec.isZERO()) {
                    continue;
                }
                Map<ExpVector, GenPolynomial<C>> mc = p.contract(cring);
                //System.out.println("mc = " + mc + ", p = " + p);
                //System.out.println("mc.ring = " + mc.values().iterator().next().ring.toScript());
                if (mc.size() == 1) { // < 1 only for p == 0
                    pc = (GenSolvablePolynomial<C>) mc.values().iterator().next();
                    this.update(ec, fc, pc);
                } else { // construct recursive polynomial
                    GenSolvablePolynomial<C> qr = ring.getZERO();
                    for (Map.Entry<ExpVector, GenPolynomial<C>> mce : mc.entrySet()) {
                        ExpVector g = mce.getKey();
                        GenPolynomial<C> q = mce.getValue();
                        C cq = (C) (Object) q;
                        GenSolvablePolynomial<C> qp = new GenSolvablePolynomial<C>(ring, cq, g);
                        qr = (GenSolvablePolynomial<C>) qr.sum(qp);
                    }
                    if (coeffTable) {
                        fc = ((GenPolynomial<C>) (Object) qr.leadingBaseCoefficient()).leadingExpVector();
                    }
                    if (fc.isZERO()) {
                        continue;
                    }
                    //System.out.println("ec = " + ec + ", fc = " + fc + ", qr = " + qr);
                    if (coeffTable) {
                        String qrs = ring.toScript(ec) + " * " + qr.leadingBaseCoefficient() + " = " + qr.toScript();
                        logger.info("coeffTable: adding " + qrs);
                    } else {
                        String qrs = ring.toScript(ec) + " * " + ring.toScript(fc) + " = " + qr.toScript();
                        logger.info("no coeffTable: adding " + qrs);
                    }
                    this.update(ec, fc, qr);
                }
            }
        }
        return;
    }


    /**
     * Reverse variables and relations. Used e.g. in opposite rings. Reverse all
     * ExpVectors and polynomials of the given relation table and put the
     * modified relations into this table, i.e. this should be empty.
     *
     * @param tab a relation table to be reverted and inserted into this.
     */
    @SuppressWarnings("unchecked")
    public void reverse(RelationTable<C> tab) {
        if (tab.table.isEmpty()) {
            return;
        }
        // assert this.size() == 0
        if (!table.isEmpty()) {
            logger.error("reverse table not empty");
        }
        int k = -1;
        if (ring.tord.getEvord2() != 0 && ring.partial) {
            k = ring.tord.getSplit();
        }
        logger.debug("k split = " + k);
        //System.out.println("k split = " + k );
        //System.out.println("reversed ring = " + ring.toScript() );
        for (List<Integer> key : tab.table.keySet()) {
            List val = tab.table.get(key);
            for (Iterator jt = val.iterator(); jt.hasNext(); ) {
                ExpVectorPair ep = (ExpVectorPair) jt.next();
                ExpVector e = ep.getFirst();
                ExpVector f = ep.getSecond();
                GenSolvablePolynomial<C> p = (GenSolvablePolynomial<C>) jt.next();
                //logger.info("e pre reverse = " + e );
                //logger.info("f pre reverse = " + f );
                //logger.info("p pre reverse = " + p );
                //if (e.degree() > 1 || f.degree() > 1) { // not required
                //    continue; // revert only base relations
                //}
                ExpVector ex;
                ExpVector fx;
                GenSolvablePolynomial<C> px;
                boolean change = true; // if relevant vars reversed
                if (k >= 0) {
                    ex = e.reverse(k);
                    if (coeffTable) {
                        fx = f;
                    } else {
                        fx = f.reverse(k);
                    }
                    //int[] ed = ex.dependencyOnVariables(); // = e
                    //if (ed.length == 0 || ed[0] >= k) { // k >= 0
                    //    change = false; todo
                    //}
                    //int[] fd = fx.dependencyOnVariables(); // = f
                    //if (fd.length == 0 || fd[0] >= k) { // k >= 0
                    //    change = false; todo
                    //}
                } else {
                    ex = e.reverse();
                    if (coeffTable) {
                        fx = f;
                    } else {
                        fx = f.reverse();
                    }
                }
                px = (GenSolvablePolynomial<C>) p.reverse(ring);
                //System.out.println("update, p, px: " + p.toScript() + " reverse:" + px.toScript() );
                if (!change) {
                    this.update(e, f, px); // same order
                } else {
                    if (coeffTable) {
                        this.update(ex, fx, px); // same order
                    } else {
                        this.update(fx, ex, px); // opposite order
                    }
                }
            }
        }
        return;
    }


    /**
     * Convert relation table to list of polynomial triples.
     *
     * @return rel = (e1,f1,p1, ...) where ei * fi = pi are solvable relations.
     */
    @SuppressWarnings({"unchecked", "cast"})
    public List<GenSolvablePolynomial<C>> relationList() {
        List<GenSolvablePolynomial<C>> rels = new ArrayList<GenSolvablePolynomial<C>>();
        //C one = ring.getONECoefficient();
        for (Map.Entry<List<Integer>, List> me : table.entrySet()) {
            //List<Integer> k = me.getKey();
            List v = me.getValue();
            for (Iterator jt = v.iterator(); jt.hasNext(); ) {
                ExpVectorPair ep = (ExpVectorPair) jt.next();
                ExpVector e = ep.getFirst();
                GenSolvablePolynomial<C> pe = ring.valueOf(e);
                ExpVector f = ep.getSecond();
                GenSolvablePolynomial<C> pf = null;
                if (coeffTable) { // todo
                    C cf = null;
                    if (ring.coFac instanceof GenPolynomialRing) {
                        GenPolynomial<C> cpf;
                        cpf = ((GenPolynomialRing<C>) (Object) ring.coFac).valueOf(f);
                        cf = (C) (Object) cpf; // down cast
                    } else if (ring.coFac instanceof GenWordPolynomialRing) {
                        GenWordPolynomial<C> cpf;
                        cpf = ((GenWordPolynomialRing<C>) (Object) ring.coFac).valueOf(f);
                        cf = (C) (Object) cpf; // down cast
                    }
                    //pf = new GenSolvablePolynomial<C>(ring, cf);
                    pf = ring.valueOf(cf);
                } else {
                    pf = ring.valueOf(f);
                }
                GenSolvablePolynomial<C> p = (GenSolvablePolynomial<C>) jt.next();
                rels.add(pe);
                rels.add(pf);
                rels.add(p);
            }
        }
        return rels;
    }


    /**
     * Add list of polynomial triples as relations.
     *
     * @param rel = (e1,f1,p1, ...) where ei * fi = pi are solvable relations.
     *            <b>Note:</b> Only because of type erasure, aequivalent to
     *            addRelations().
     */
    //@SuppressWarnings("unchecked")
    public void addSolvRelations(List<GenSolvablePolynomial<C>> rel) {
        PolynomialList<C> Prel = new PolynomialList<C>(ring, rel);
        addRelations(Prel.getList());
    }


    /**
     * Add list of polynomial triples as relations.
     *
     * @param rel = (e1,f1,p1, ...) where ei * fi = pi are solvable relations.
     */
    @SuppressWarnings({"unchecked", "cast"})
    public void addRelations(List<GenPolynomial<C>> rel) {
        if (rel == null || rel.isEmpty()) {
            return;
        }
        Iterator<GenPolynomial<C>> relit = rel.iterator();
        while (relit.hasNext()) {
            GenPolynomial<C> E = relit.next();
            ExpVector e = E.leadingExpVector();
            ExpVector f = null;
            if (!relit.hasNext()) {
                throw new IllegalArgumentException("F and poly part missing");
            }
            GenPolynomial<C> F = relit.next();
            if (!relit.hasNext()) {
                throw new IllegalArgumentException("poly part missing");
            }
            GenPolynomial<C> P = relit.next();
            if (coeffTable && F.isConstant()) { // todo
                if (ring.coFac instanceof GenPolynomialRing) {
                    f = ((GenPolynomial<C>) (Object) F.leadingBaseCoefficient()).leadingExpVector();
                } else if (ring.coFac instanceof GenWordPolynomialRing) {
                    f = ((GenWordPolynomial<C>) (Object) F.leadingBaseCoefficient()).leadingWord()
                            .leadingExpVector();
                }
            } else {
                f = F.leadingExpVector();
            }
            update(e, f, P);
        }
        return;
    }

}
