/*
 * $Id$
 */

package edu.jas.gbufd;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.jas.gb.GroebnerBaseAbstract;
import edu.jas.gb.PairList;
import edu.jas.gb.Reduction;
import edu.jas.gb.ReductionSeq;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.TermOrder;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;


/**
 * Groebner Base sequential FGLM algorithm. Implements Groebner base computation
 * via FGLM algorithm.
 * @param <C> coefficient type
 * @author Jan Suess
 *
 * @see edu.jas.application.GBAlgorithmBuilder
 * @see edu.jas.gbufd.GBFactory
 */
public class GroebnerBaseFGLM<C extends GcdRingElem<C>> extends GroebnerBaseAbstract<C> {


    private static final Logger logger = Logger.getLogger(GroebnerBaseFGLM.class);


    //private final boolean debug = logger.isDebugEnabled();


    /**
     * The backing GB algorithm implementation.
     */
    private GroebnerBaseAbstract<C> sgb;


    /**
     * Constructor.
     */
    public GroebnerBaseFGLM() {
        super();
        sgb = null;
    }


    /**
     * Constructor.
     * @param red Reduction engine
     */
    public GroebnerBaseFGLM(Reduction<C> red) {
        super(red);
        sgb = null;
    }


    /**
     * Constructor.
     * @param red Reduction engine
     * @param pl pair selection strategy
     */
    public GroebnerBaseFGLM(Reduction<C> red, PairList<C> pl) {
        super(red, pl);
        sgb = null;
    }


    /**
     * Constructor.
     * @param red Reduction engine
     * @param pl pair selection strategy
     * @param gb backing GB algorithm.
     */
    public GroebnerBaseFGLM(Reduction<C> red, PairList<C> pl, GroebnerBaseAbstract<C> gb) {
        super(red, pl);
        sgb = gb;
    }


    /**
     * Constructor.
     * @param gb backing GB algorithm.
     */
    public GroebnerBaseFGLM(GroebnerBaseAbstract<C> gb) {
        super();
        sgb = gb;
    }


    /**
     * Groebner base using FGLM algorithm.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return GB(F) a inv lex term order Groebner base of F.
     */
    public List<GenPolynomial<C>> GB(int modv, List<GenPolynomial<C>> F) {
        if (modv != 0) {
            throw new UnsupportedOperationException("case modv != 0 not yet implemented");
        }
        if (F == null || F.size() == 0) {
            return F;
        }
        List<GenPolynomial<C>> G = new ArrayList<GenPolynomial<C>>();
        if (F.size() <= 1) {
            GenPolynomial<C> p = F.get(0).monic();
            G.add(p);
            return G;
        }
        // convert to graded term order
        List<GenPolynomial<C>> Fp = new ArrayList<GenPolynomial<C>>(F.size());
        GenPolynomialRing<C> pfac = F.get(0).ring;
        if (!pfac.coFac.isField()) {
            throw new IllegalArgumentException("coefficients not from a field: " + pfac.coFac);
        }
        TermOrder tord = new TermOrder(TermOrder.IGRLEX);
        GenPolynomialRing<C> gfac = new GenPolynomialRing<C>(pfac.coFac, pfac.nvar, tord, pfac.getVars());
        for (GenPolynomial<C> p : F) {
            GenPolynomial<C> g = gfac.copy(p); // change term order
            Fp.add(g);
        }
        // compute graded term order Groebner base
        if (sgb == null) {
            sgb = GBFactory.<C> getImplementation(pfac.coFac);
        }
        List<GenPolynomial<C>> Gp = sgb.GB(modv, Fp);
        logger.info("graded GB = " + Gp);
        if (tord.equals(pfac.tord)) {
            return Gp;
        }
        if (Gp.size() == 0) {
            return Gp;
        }
        if (Gp.size() == 1) {
            GenPolynomial<C> p = pfac.copy(Gp.get(0)); // change term order
            G.add(p);
            return G;
        }
        // compute invlex Groebner base via FGLM
        G = convGroebnerToLex(Gp);
        return G;
    }


    /**
     * Algorithm CONVGROEBNER: Converts Groebner bases w.r.t. total degree
     * termorder into Groebner base w.r.t to inverse lexicographical term order
     * @return Groebner base w.r.t to inverse lexicographical term order
     */
    public List<GenPolynomial<C>> convGroebnerToLex(List<GenPolynomial<C>> groebnerBasis) {
        if (groebnerBasis == null || groebnerBasis.size() == 0) {
            throw new IllegalArgumentException("G may not be null or empty");
        }
        int z = commonZeroTest(groebnerBasis);
        if (z != 0) {
            throw new IllegalArgumentException("ideal(G) not zero dimensional, dim =  " + z);
        }
        //Polynomial ring of input Groebnerbasis G
        GenPolynomialRing<C> ring = groebnerBasis.get(0).ring;
        int numberOfVariables = ring.nvar; //Number of Variables of the given Polynomial Ring
        String[] ArrayOfVariables = ring.getVars(); //Variables of given polynomial ring w.r.t. to input G
        RingFactory<C> cfac = ring.coFac;

        //Main Algorithm
        //Initialization

        TermOrder invlex = new TermOrder(TermOrder.INVLEX);
        //Polynomial ring of newGB with invlex order
        GenPolynomialRing<C> ufac = new GenPolynomialRing<C>(cfac, numberOfVariables, invlex,
                        ArrayOfVariables);

        //Local Lists
        List<GenPolynomial<C>> newGB = new ArrayList<GenPolynomial<C>>(); //Instantiate the return list of polynomials
        List<GenPolynomial<C>> H = new ArrayList<GenPolynomial<C>>(); //Instantiate a help list of polynomials
        List<GenPolynomial<C>> redTerms = new ArrayList<GenPolynomial<C>>();//Instantiate the return list of reduced terms

        //Local Polynomials
        GenPolynomial<C> t = ring.ONE; //Create ONE polynom of original polynomial ring
        GenPolynomial<C> h; //Create help polynomial
        GenPolynomial<GenPolynomial<C>> hh; //h as polynomial in rfac
        GenPolynomial<GenPolynomial<C>> p; //Create another help polynomial
        redTerms.add(t); //Add ONE to list of reduced terms

        //create new indeterminate Y1
        int indeterminates = 1; //Number of indeterminates, starting with Y1
        GenPolynomialRing<C> cpfac = createRingOfIndeterminates(ring, indeterminates);
        GenPolynomialRing<GenPolynomial<C>> rfac = new GenPolynomialRing<GenPolynomial<C>>(cpfac, ring);
        GenPolynomial<GenPolynomial<C>> q = rfac.getZERO().sum(cpfac.univariate(0));

        //Main while loop
        z = -1;
        t = lMinterm(H, t);
        while (t != null) {
            //System.out.println("t = " + t);
            h = red.normalform(groebnerBasis, t);
            //System.out.println("Zwischennormalform h = " + h.toString());
            hh = PolyUtil.<C> toRecursive(rfac, h);
            p = hh.sum(q);
            List<GenPolynomial<C>> Cf = new ArrayList<GenPolynomial<C>>(p.getMap().values());
            Cf = red.irreducibleSet(Cf);
            //System.out.println("Cf = " + Cf);
            //System.out.println("Current Polynomial ring in Y_n: " + rfac.toString());

            z = commonZeroTest(Cf);
            //System.out.println("z = " + z);
            if (z != 0) { //z=1 OR z=-1 --> Infinite number of solutions OR No solution
                indeterminates++; //then, increase number of indeterminates by one
                redTerms.add(t); //add current t to list of reduced terms
                cpfac = addIndeterminate(cpfac);
                rfac = new GenPolynomialRing<GenPolynomial<C>>(cpfac, ring);
                hh = PolyUtil.<C> toRecursive(rfac, h);
                GenPolynomial<GenPolynomial<C>> Yt = rfac.getZERO().sum(cpfac.univariate(0));
                GenPolynomial<GenPolynomial<C>> Yth = hh.multiply(Yt);
                q = PolyUtil.<C> extendCoefficients(rfac, q, 0, 0L);
                q = Yth.sum(q);
            } else { // z=0 --> one solution
                GenPolynomial<C> g = ufac.getZERO();
                for (GenPolynomial<C> pc : Cf) {
                    ExpVector e = pc.leadingExpVector();
                    //System.out.println("e = " + e);
                    if (e == null) {
                        continue;
                    }
                    int[] v = e.dependencyOnVariables();
                    if (v == null || v.length == 0) {
                        continue;
                    }
                    int vi = v[0];
                    vi = indeterminates - vi;
                    C tc = pc.trailingBaseCoefficient();
                    if (!tc.isZERO()) {
                        tc = tc.negate();
                        GenPolynomial<C> csRedterm = redTerms.get(vi - 1).multiply(tc);
                        //System.out.println("csRedterm = " + csRedterm);
                        g = g.sum(csRedterm);
                    }
                }
                g = g.sum(t);
                g = ufac.copy(g);
                H.add(t);
                if (!g.isZERO()) {
                    newGB.add(g);
                    logger.info("new element for GB = " + g.leadingExpVector());
                }
            }
            t = lMinterm(H, t); // compute lMINTERM of current t (lexMinterm)
        }
        //logger.info("GB = " + newGB);
        return newGB;
    }


    /**
     * Algorithm lMinterm: MINTERM algorithm for inverse lexicographical term
     * order.
     * @param t Term
     * @param G Groebner basis
     * @return Term that specifies condition (D) or null (Condition (D) in
     *         "A computational approach to commutative algebra", Becker,
     *         Weispfenning, Kredel, 1993, p. 427)
     */
    public GenPolynomial<C> lMinterm(List<GenPolynomial<C>> G, GenPolynomial<C> t) {
        //not ok: if ( G == null || G.size() == 0 ) ...
        GenPolynomialRing<C> ring = t.ring;
        int numberOfVariables = ring.nvar;
        GenPolynomial<C> u = new GenPolynomial<C>(ring, t.leadingBaseCoefficient(), t.leadingExpVector()); //HeadTerm of of input polynomial
        ReductionSeq<C> redHelp = new ReductionSeq<C>(); // Create instance of ReductionSeq to use method isReducible
        //not ok: if ( redHelp.isTopReducible(G,u) ) ...
        for (int i = numberOfVariables - 1; i >= 0; i--) { // Walk through all variables, starting with least w.r.t to lex-order
            GenPolynomial<C> x = ring.univariate(i); // Create Linear Polynomial X_i
            u = u.multiply(x); // Multiply current u with x
            if (!redHelp.isTopReducible(G, u)) { // Check if any term in HT(G) divides current u
                return u;
            }
            GenPolynomial<C> s = ring.univariate(i, u.degree(numberOfVariables - (i + 1))); //if not, eliminate variable x_i
            u = u.divide(s);
        }
        return null;
    }


    /**
     * Compute the residues to given polynomial list.
     * @return List of reduced terms
     */
    public List<GenPolynomial<C>> redTerms(List<GenPolynomial<C>> groebnerBasis) {
        if (groebnerBasis == null || groebnerBasis.size() == 0) {
            throw new IllegalArgumentException("groebnerBasis may not be null or empty");
        }
        GenPolynomialRing<C> ring = groebnerBasis.get(0).ring;
        int numberOfVariables = ring.nvar; //Number of Variables of the given Polynomial Ring
        long[] degrees = new long[numberOfVariables]; //Array for the degree-limits for the reduced terms

        List<GenPolynomial<C>> terms = new ArrayList<GenPolynomial<C>>(); //Instantiate the return object
        for (GenPolynomial<C> g : groebnerBasis) { //For each polynomial of G
            if (g.isONE()) {
                terms.clear();
                return terms; //If 1 e G, return empty list terms
            }
            ExpVector e = g.leadingExpVector(); //Take the exponent of the leading monomial             
            if (e.totalDeg() == e.maxDeg()) { //and check, whether a variable x_i is isolated
                for (int i = 0; i < numberOfVariables; i++) {
                    long exp = e.getVal(i);
                    if (exp > 0) {
                        degrees[i] = exp; //if true, add the degree of univariate x_i to array degrees
                    }
                }
            }
        }
        long max = maxArray(degrees); //Find maximum in Array degrees
        for (int i = 0; i < degrees.length; i++) { //Set all zero grades to maximum of array "degrees"
            if (degrees[i] == 0) {
                logger.info("dimension not zero, setting degree to " + max);
                degrees[i] = max; //--> to "make" the ideal zero-dimensional
            }
        }
        terms.add(ring.ONE); //Add the one-polynomial of the ring to the list of reduced terms
        ReductionSeq<C> s = new ReductionSeq<C>(); //Create instance of ReductionSeq to use method isReducible

        //Main Algorithm
        for (int i = 0; i < numberOfVariables; i++) {
            GenPolynomial<C> x = ring.univariate(i); //Create  Linear Polynomial X_i
            List<GenPolynomial<C>> S = new ArrayList<GenPolynomial<C>>(terms); //Copy all entries of return list "terms" into list "S"
            for (GenPolynomial<C> t : S) {
                for (int l = 1; l <= degrees[i]; l++) {
                    t = t.multiply(x); //Multiply current element t with Linear Polynomial X_i
                    if (!s.isReducible(groebnerBasis, t)) { //Check, if t is irreducible mod groebnerbase
                        terms.add(t); //Add t to return list terms
                    }
                }
            }
        }
        return terms;
    }


    /**
     * Internal method to create a polynomial ring in i indeterminates. Create
     * new ring over coefficients of ring with i variables Y1,...,Yi
     * (indeterminate)
     * @return polynomial ring with variables Y1...Yi and coefficient of ring.
     */
    GenPolynomialRing<C> createRingOfIndeterminates(GenPolynomialRing<C> ring, int i) {
        RingFactory<C> cfac = ring.coFac;
        int indeterminates = i;
        String[] stringIndeterminates = new String[indeterminates];
        for (int j = 1; j <= indeterminates; j++) {
            stringIndeterminates[j - 1] = ("Y" + j);
        }
        TermOrder invlex = new TermOrder(TermOrder.INVLEX);
        GenPolynomialRing<C> cpfac = new GenPolynomialRing<C>(cfac, indeterminates, invlex,
                        stringIndeterminates);
        return cpfac;
    }


    /**
     * Internal method to add new indeterminates. Add another variabe
     * (indeterminate) Y_{i+1} to existing ring
     * @return polynomial ring with variables Y1,..,Yi,Yi+1 and coefficients of
     *         ring.
     */
    GenPolynomialRing<C> addIndeterminate(GenPolynomialRing<C> ring) {
        String[] stringIndeterminates = new String[1];
        int number = ring.nvar + 1;
        stringIndeterminates[0] = ("Y" + number);
        ring = ring.extend(stringIndeterminates);
        return ring;
    }


    /**
     * Maximum of an array.
     * @return maximum of an array
     */
    long maxArray(long[] t) {
        if (t.length == 0) {
            return 0L;
        }
        long maximum = t[0];
        for (int i = 1; i < t.length; i++) {
            if (t[i] > maximum) {
                maximum = t[i];
            }
        }
        return maximum;
    }


    /**
     * Cleanup and terminate ThreadPool.
     */
    public void terminate() {
        if ( sgb == null ) {
            return;
        }
        sgb.terminate();
    }


    /**
     * Cancel ThreadPool.
     */
    public int cancel() {
        if ( sgb == null ) {
            return 0;
        }
        return sgb.cancel();
    }

}
