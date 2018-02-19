/*
 * $Id$
 */

package edu.jas.gbufd;


import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import edu.jas.arith.BigRational;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.gb.GroebnerBase;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialTokenizer;
import edu.jas.poly.Monomial;
import edu.jas.poly.OrderedPolynomialList;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.PolynomialList;


/**
 * Groebner base FGLM examples.
 *
 * @author Jan Suess
 */

public class GroebnerBaseFGLMExamples {


    //field Q
    String all = "Zahlbereich | Ordnung    | Elements G | Elements L | bitHeight G | bitHeight L | Deg G | Deg L | Time G | Time FGLM | Time L";


    /*
     * Constructs a <CODE>GroebnerBaseFGLMExamples</CODE> object.
     * @param name String.
    public GroebnerBaseFGLMExamples(String name) {
        super(name);
    }
     */


    /*
     * suite.
    public static Test suite() {
        TestSuite suite = new TestSuite(GroebnerBaseFGLMExamples.class);
        return suite;
    }
     */
    String grad = "Zahlbereich | Ordnung    | Elements G | bitHeight G | Deg G | Time G | vDim";
    String lex = "Zahlbereich | Ordnung      | Elements L | bitHeight L | Deg L | Time L";
    String fglm = "Zahlbereich | Ordnung      | Elements G | Elements L  | bitHeight G | bitHeight L |  Deg G | Deg L | Time G | Time FGLM";
    //MOD 1831
    String modAll = "Zahlbereich | Ordnung    | Elements G | Elements L | Deg G | Deg L | Time G | Time FGLM | Time L";
    String modfglm = "Zahlbereich | Ordnung      | Elements G | Elements L | Deg G | Deg L | Time G | Time FGLM";


    //String modGrad = "Zahlbereich | Ordnung      | Elements G | Deg G | Time G";

    /**
     * main
     */
    public static void main(String[] args) {
        //BasicConfigurator.configure();
        //junit.textui.TestRunner.run(suite());
        GroebnerBaseFGLMExamples ex = new GroebnerBaseFGLMExamples();
        ex.testC5();
        /*
        ex.xtestFiveVarsOrder();
        ex.xtestCAP();
        ex.xtestAUX();
        ex.xtestModC5();
        ex.xtestC6();
        ex.xtestIsaac();
        ex.xtestNiermann();
        ex.ytestWalkS7();
        ex.ytestCassouMod1();
        ex.ytestOmdi1();
        ex.ytestLamm1();
        ex.xtestEquilibrium();
        ex.xtestTrinks2();
        ex.xtestHairerRungeKutta_1();
        */
    }


    /*
    @Override
    protected void setUp() {
        System.out.println("Setup");
    }


    @Override
    protected void tearDown() {
        System.out.println("Tear Down");
    }
    */

    //Test with five variables and different variable orders
    public void xtestFiveVarsOrder() {
        String polynomials = "( "
                + " (v^8*x*y*z), ( w^3*x - 2*v), ( 4*x*y - 2 + y), ( 3*y^5 - 3 + z ), ( 8*y^2*z^2 + x * y^6 )"
                + ") ";
        String[] order = new String[]{"v", "w", "x", "y", "z"};

        //String order1 = shuffle(order);
        String order2 = shuffle(order);
        //String order3 = shuffle(order);
        //String order4 = shuffle(order);
        //String order5 = shuffle(order);
        //String order6 = "(z,w,v,y,x)"; //langsam
        //String order7 = "(v,z,w,y,x)"; //langsam
        //String order8 = "(w,z,v,x,y)"; //langsam

        /*
          String erg1 = testGeneral(order1, polynomials);
          String erg2 = testGeneral(order2, polynomials);
          String erg3 = testGeneral(order3, polynomials);
          String erg4 = testGeneral(order4, polynomials);
          String erg5 = testGeneral(order5, polynomials);
        */
        String ergM13 = modAll(order2, polynomials, 13);
        String ergM7 = modAll(order2, polynomials, 7);

        /*
          String ergOnlyL_1 = testOnlyLex(order1, polynomials);
          String ergOnlyL_2 = testOnlyLex(order2, polynomials);
          String ergOnlyL_3 = testOnlyLex(order3, polynomials);
          String ergOnlyL_4 = testOnlyLex(order4, polynomials);
          String ergOnlyL_5 = testOnlyLex(order5, polynomials);
          String erg6 = testGeneral(order6, polynomials);
          String erg7 = testGeneral(order7, polynomials);
          String erg8 = testGeneral(order8, polynomials);
        */
        //langsam: (z,w,v,y,x), (v,z,w,y,x)
        /*
          System.out.println(categoryLex);
          System.out.println(ergOnlyL_1);
          System.out.println(ergOnlyL_2);
          System.out.println(ergOnlyL_3);
          System.out.println(ergOnlyL_4);
          System.out.println(ergOnlyL_5);
                
          System.out.println(category);
          System.out.println(erg6);
          System.out.println(erg7);
          System.out.println(erg8);
                
                
          System.out.println(erg1);
          System.out.println(erg2);
          System.out.println(erg3);
          System.out.println(erg4);
          System.out.println(erg5);
        */
        System.out.println(all);
        System.out.println("Mod 13");
        System.out.println(ergM13);
        System.out.println("Mod 7");
        System.out.println(ergM7);
    }


    //===================================================================
    //Examples taken from "Efficient Computation of Zero-Dimensional Gröbner Bases by Change of Ordering", 
    // 1994, Faugere, Gianni, Lazard, Mora (FGLM)
    //===================================================================       
    public void xtestCAP() {
        String polynomials = "( " + " (y^2*z + 2*x*y*t - 2*x - z),"
                + "(-x^3*z + 4*x*y^2*z + 4*x^2*y*t + 2*y^3*t + 4*x^2 - 10*y^2 + 4*x*z - 10*y*t + 2),"
                + "(2*y*z*t + x*t^2 - x - 2*z),"
                + "(-x*z^3 + 4*y*z^2*t + 4*x*z*t^2 + 2*y*t^3 + 4*x*z + 4*z^2 - 10*y*t -10*t^2 + 2)"
                + ") ";

        String orderINV = "(x,y,z,t)";
        String orderL = "(t,z,y,x)";

        //Tests
        String erg_deg = grad(orderINV, polynomials);
        System.out.println(grad);
        System.out.println(erg_deg);

        String erg1 = all(orderINV, polynomials);
        String erg2 = all(orderL, polynomials);
        String ergMod1 = modAll(orderINV, polynomials, 1831);
        String ergMod2 = modAll(orderL, polynomials, 1831);
        System.out.println(all);
        System.out.println(erg1);
        System.out.println(erg2);
        System.out.println("\n");
        System.out.println(modAll);
        System.out.println(ergMod1);
        System.out.println(ergMod2);
    }


    public void xtestAUX() {
        String polynomials = "( " + " (a^2*b*c + a*b^2*c + a*b*c^2 + a*b*c + a*b + a*c + b*c),"
                + "(a^2*b^2*c + a*b^2*c^2 + a^2*b*c + a*b*c + b*c + a + c ),"
                + "(a^2*b^2*c^2 + a^2*b^2*c + a*b^2*c + a*b*c + a*c + c + 1)" + ") ";

        String orderINV = "(a,b,c)";
        String orderL = "(c,b,a)";

        //Tests
        String erg_deg = grad(orderINV, polynomials);
        System.out.println(grad);
        System.out.println(erg_deg);

        String erg1 = all(orderINV, polynomials);
        String erg2 = all(orderL, polynomials);
        String ergMod1 = modAll(orderINV, polynomials, 1831);
        String ergMod2 = modAll(orderL, polynomials, 1831);
        System.out.println(all);
        System.out.println(erg1);
        System.out.println(erg2);
        System.out.println("\n");
        System.out.println(modAll);
        System.out.println(ergMod1);
        System.out.println(ergMod2);
    }


    public void testC5() {
        String polynomials = "( " + " (a + b + c + d + e)," + "(a*b + b*c + c*d + a*e + d*e),"
                + "(a*b*c + b*c*d + a*b*e + a*d*e + c*d*e),"
                + "(a*b*c*d + a*b*c*e + a*b*d*e + a*c*d*e + b*c*d*e)," + "(a*b*c*d*e -1)" + ") ";

        String orderINV = "(a,b,c,d,e)";
        String orderL = "(e,d,c,b,a)";

        //Tests
        String erg_deg = grad(orderINV, polynomials);
        //System.out.println(grad);
        //System.out.println(erg_deg);

        String erg1 = all(orderINV, polynomials);
        String erg2 = all(orderL, polynomials);
        String ergMod1 = modAll(orderINV, polynomials, 1831);
        String ergMod2 = modAll(orderL, polynomials, 1831);

        System.out.println(grad);
        System.out.println(erg_deg);
        System.out.println("");
        System.out.println(all);
        System.out.println(erg1);
        System.out.println(erg2);
        System.out.println("\n");
        System.out.println(modAll);
        System.out.println(ergMod1);
        System.out.println(ergMod2);
    }


    public void xtestModC5() {
        String polynomials = "( " + " (a + b + c + d + e)," + "(a*b + b*c + c*d + a*e + d*e),"
                + "(a*b*c + b*c*d + a*b*e + a*d*e + c*d*e),"
                + "(b*c*d + a*b*c*e + a*b*d*e + a*c*d*e + b*c*d*e)," + "(a*b*c*d*e -1)" + ") ";

        //String orderINV = "(a,b,c,d,e)";
        String orderL = "(e,d,c,b,a)";

        //Tests
        String erg_deg = grad(orderL, polynomials);
        System.out.println(grad);
        System.out.println(erg_deg);

        /*              
          String ergOnlyFGLM_1 = fglm(orderINV, polynomials);
          System.out.println(fglm);
          System.out.println(ergOnlyFGLM_1);            
                
          //Tests MODULO
                
          String ergOnlyG_1 = modGrad(orderINV, polynomials, 1831);
          System.out.println(modGrad);
          System.out.println(ergOnlyG_1);
        
          String erg1 = modfglm(orderINV, polynomials, 1831);
          System.out.println(modfglm);
          System.out.println(erg1);
        */
    }


    public void xtestC6() {
        String polynomials = "( " + " (a + b + c + d + e + f)," + "(a*b + b*c + c*d + d*e + e*f + a*f),"
                + "(a*b*c + b*c*d + c*d*e + d*e*f + a*e*f + a*b*f),"
                + "(a*b*c*d + b*c*d*e + c*d*e*f + a*d*e*f + a*b*e*f + a*b*c*f),"
                + "(a*b*c*d*e + b*c*d*e*f + a*c*d*e*f + a*b*d*e*f + a*b*c*e*f + a*b*c*d*f),"
                + "(a*b*c*d*e*f - 1)" + ") ";

        String orderINV = "(a,b,c,d,e,f)";
        //String orderL = "(f,e,d,c,b,a)";

        //Tests      
        String erg2 = modAll(orderINV, polynomials, 1831);
        System.out.println(modAll);
        System.out.println(erg2);
        /*                
          String ergOnlyG_1 = modGrad(orderINV, polynomials, 1831);
          System.out.println(modGrad);
          System.out.println(ergOnlyG_1);

          String erg1 = modfglm(orderINV, polynomials, 1831);
          System.out.println(modfglm);
          System.out.println(erg1);
        */
    }


    //===================================================================
    //Examples taken from "Der FGLM-Algorithmus: verallgemeinert und implementiert in SINGULAR", 1997, Wichmann
    //===================================================================
    public void xtestIsaac() {
        String polynomials = "( "
                + " (8*w^2 + 5*w*x - 4*w*y + 2*w*z + 3*w + 5*x^2 + 2*x*y - 7*x*z - 7*x + 7*y^2 -8*y*z - 7*y + 7*z^2 - 8*z + 8),"
                + "(3*w^2 - 5*w*x - 3*w*y - 6*w*z + 9*w + 4*x^2 + 2*x*y - 2*x*z + 7*x + 9*y^2 + 6*y*z + 5*y + 7*z^2 + 7*z + 5),"
                + "(-2*w^2 + 9*w*x + 9*w*y - 7*w*z - 4*w + 8*x^2 + 9*x*y - 3*x*z + 8*x + 6*y^2 - 7*y*z + 4*y - 6*z^2 + 8*z + 2),"
                + "(7*w^2 + 5*w*x + 3*w*y - 5*w*z - 5*w + 2*x^2 + 9*x*y - 7*x*z + 4*x -4*y^2 - 5*y*z + 6*y - 4*z^2 - 9*z + 2)"
                + ") ";

        //String orderINV = "(w,x,y,z)";
        String orderL = "(z,y,x,w)";

        //Tests
        String erg_deg = grad(orderL, polynomials);
        System.out.println(grad);
        System.out.println(erg_deg);
        /*              
          String erg3 = all(orderINV, polynomials);
          System.out.println(all);
          System.out.println(erg3);
                
          String ergOnlyLex_1 = lex(orderINV, polynomials);
          String ergOnlyLex_2 = lex(orderL, polynomials);
          System.out.println(lex);
          System.out.println(ergOnlyLex_1);
          System.out.println(ergOnlyLex_2);
                
          String ergOnlyFGLM_1 = fglm(orderINV, polynomials);
          String ergOnlyFGLM_2 = fglm(orderL, polynomials);
          System.out.println(fglm);
          System.out.println(ergOnlyFGLM_1);
          System.out.println(ergOnlyFGLM_2);
                        
          String ergm1 = modAll(orderINV, polynomials, 2147464751);
          String ergm2 = modAll(orderL, polynomials, 2147464751);
          System.out.println(modAll);
          System.out.println(ergm1);
          System.out.println(ergm2);
        */
    }


    public void xtestNiermann() {
        String polynomials = "( " + " (x^2 + x*y^2*z - 2*x*y + y^4 + y^2 + z^2),"
                + "(-x^3*y^2 + x*y^2*z + x*y*z^3 - 2*x*y + y^4)," + "(-2*x^2*y + x*y^4 + y*z^4 - 3)"
                + ") ";

        String orderINV = "(x,y,z)";
        String orderL = "(z,y,x)";

        //Tests
        String erg_deg = grad(orderINV, polynomials);
        System.out.println(grad);
        System.out.println(erg_deg);
        /*
          String erg1 = fglm(orderINV, polynomials);
          String erg2 = fglm(orderL, polynomials);
          System.out.println(fglm);
          System.out.println(erg1);
          System.out.println(erg2);
        */
        String ergm1 = modfglm(orderINV, polynomials, 1831);
        String ergm2 = modfglm(orderL, polynomials, 2147464751);
        System.out.println(modfglm);
        System.out.println(ergm1);
        System.out.println(ergm2);
    }


    public void ytestWalkS7() {
        String polynomials = "( " + " (2*g*b + 2*f*c + 2*e*d + a^2 + a),"
                + "(2*g*c + 2*f*d + e^2 + 2*b*a + b)," + "(2*g*d + 2*f*e + 2*c*a + c + b^2),"
                + "(2*g*e + f^2 + 2*d*a + d + 2*c*b)," + "(2*g*f + 2*e*a + e + 2*d*b + c^2),"
                + "(g^2 + 2*f*a + f + 2*e*b + 2*d*c)," + "(2*g*a + g + 2*f*b + 2*e*c + d^2)" + ") ";

        //String orderINV = "(a,b,c,d,e,f,g)";
        String orderL = "(g,f,e,d,c,b,a)";

        //Tests
        //String ergm1 = modAll(orderINV, polynomials, 2147464751);
        //String ergm2 = modfglm(orderL, polynomials, 1831);
        //System.out.println(modfglm);
        //System.out.println(ergm1);
        //System.out.println(ergm2);

        String erg2 = fglm(orderL, polynomials);
        System.out.println(fglm);
        System.out.println(erg2);
    }


    public void ytestCassouMod1() {
        String polynomials = "( "
                + " (15*a^4*b*c^2 + 6*a^4*b^3 + 21*a^4*b^2*c - 144*a^2*b - 8*a^2*b^2*d - 28*a^2*b*c*d - 648*a^2*c + 36*c^2*d + 9*a^4*c^3 - 120),"
                + "(30*b^3*a^4*c - 32*c*d^2*b - 720*c*a^2*b - 24*b^3*a^2*d - 432*b^2*a^2 + 576*d*b - 576*c*d + 16*b*a^2*c^2*d + 16*c^2*d^2 + 16*d^2*b^2 + 9*b^4*a^4 + 5184 + 39*c^2*a^4*b^2 + 18*c^3*a^4*b - 432*c^2*a^2 + 24*c^3*a^2*d - 16*b^2*a^2*c*d - 240*b),"
                + "(216*c*a^2*b - 162*c^2*a^2 - 81*b^2*a^2 + 5184 + 1008*d*b - 1008*c*d + 15*b^2*a^2*c*d - 15*b^3*a^2*d - 80*c*d^2*b + 40*c^2*d^2 + 40*d^2*b^2),"
                + "(261 + 4*c*a^2*b - 3*c^2*a^2 - 4*b^2*a^2 + 22*d*b - 22*c*d)" + ") ";

        String orderINV = "(a,b,c,d)";
        String orderL = "(d,c,b,a)";

        //Tests
        String ergm1 = modfglm(orderL, polynomials, 1831);
        String ergm2 = modfglm(orderINV, polynomials, 1831);
        System.out.println(modfglm);
        System.out.println(ergm1);
        System.out.println(ergm2);
    }


    public void ytestOmdi1() {
        String polynomials = "( " + " (a + c + v + 2*x - 1)," + "(a*b + c*u + 2*v*w + 2*x*y + 2*x*z -2/3),"
                + "(a*b^2 + c*u^2 + 2*v*w^2 + 2*x*y^2 + 2*x*z^2 - 2/5),"
                + "(a*b^3 + c*u^3 + 2*v*w^3 + 2*x*y^3 + 2*x*z^3 - 2/7),"
                + "(a*b^4 + c*u^4 + 2*v*w^4 + 2*x*y^4 + 2*x*z^4 - 2/9)," + "(v*w^2 + 2*x*y*z - 1/9),"
                + "(v*w^4 + 2*x*y^2*z^2 - 1/25)," + "(v*w^3 + 2*x*y*z^2 + x*y^2*z - 1/15),"
                + "(v*w^4 + x*y*z^3 + x*y^3*z -1/21)" + ") ";

        //String orderINV = "(a,b,c,u,v,w,x,y,z)";
        String orderL = "(z,y,x,w,v,u,c,b,a)";

        //Tests
        String erg_deg = grad(orderL, polynomials);
        System.out.println(grad);
        System.out.println(erg_deg);
        /*
          String ergm1 = modfglm(orderL, polynomials, 1831);
          String ergm2 = modfglm(orderINV, polynomials, 1831);
          System.out.println(modfglm);
          System.out.println(ergm1);
          System.out.println(ergm2);
        */
    }


    public void ytestLamm1() {
        String polynomials = "( "
                + " (45*x^8 + 3*x^7 + 39*x^6 + 30*x^5 + 13*x^4 + 41*x^3 + 5*x^2 + 46*x + 7),"
                + "(49*x^7*y + 35*x*y^7 + 37*x*y^6 + 9*y^7 + 4*x^6 + 6*y^6 + 27*x^3*y^2 + 20*x*y^4 + 31*x^4 + 33*x^2*y + 24*x^2 + 49*y + 43)"
                + ") ";

        String orderINV = "(x,y)";
        String orderL = "(y,x)";

        //Tests
        String erg_deg = grad(orderINV, polynomials);
        System.out.println(grad);
        System.out.println(erg_deg);

        String erg1 = all(orderINV, polynomials);
        String erg2 = all(orderL, polynomials);
        String ergMod1 = modAll(orderINV, polynomials, 1831);
        String ergMod2 = modAll(orderL, polynomials, 1831);
        System.out.println(all);
        System.out.println(erg1);
        System.out.println(erg2);
        System.out.println("\n");
        System.out.println(modAll);
        System.out.println(ergMod1);
        System.out.println(ergMod2);
    }


    //===================================================================
    //Examples taken from "Some Examples for Solving Systems of Algebraic Equations by Calculating Gröbner Bases", 1984, Boege, Gebauer, Kredel
    //===================================================================               
    public void xtestEquilibrium() {
        String polynomials = "( "
                + " (y^4 - 20/7*z^2),"
                + "(z^2*x^4 + 7/10*z*x^4 + 7/48*x^4 - 50/27*z^2 - 35/27*z - 49/216),"
                + "(x^5*y^3 + 7/5*z^4*y^3 + 609/1000 *z^3*y^3 + 49/1250*z^2*y^3 - 27391/800000*z*y^3 - 1029/160000*y^3 + 3/7*z^5*x*y^2 +"
                + "3/5*z^6*x*y^2 + 63/200*z^3*x*y^2 + 147/2000*z^2*x*y^2 + 4137/800000*z*x*y^2 - 7/20*z^4*x^2*y - 77/125*z^3*x^2*y"
                + "- 23863/60000*z^2*x^2*y - 1078/9375*z*x^2*y - 24353/1920000*x^2*y - 3/20*z^4*x^3 - 21/100*z^3*x^3"
                + "- 91/800*z^2*x^3 - 5887/200000*z*x^3 - 343/128000*x^3)" + ") ";

        String order = "(x,y,z)";

        //Tests
        String ergOnlyG_1 = grad(order, polynomials);
        System.out.println(grad);
        System.out.println(ergOnlyG_1);
    }


    public void xtestTrinks2() {
        String polynomials = "( " + " (45*p + 35*s - 165*b - 36)," + "(35*p + 40*z + 25*t - 27*s),"
                + "(15*w + 25*p*s + 30*z - 18*t - 165*b^2)," + "(-9*w + 15*p*t + 20*z*s),"
                + "(w*p + 2*z*t - 11*b^3)," + "(99*w - 11*s*b + 3*b^2),"
                + "(b^2 + 33/50*b + 2673/10000)" + ") ";

        String order1 = "(b,s,t,z,p,w)";
        String order2 = "(s,b,t,z,p,w)";
        String order3 = "(s,t,b,z,p,w)";
        String order4 = "(s,t,z,p,b,w)";
        String order5 = "(s,t,z,p,w,b)";
        String order6 = "(s,z,p,w,b,t)";
        String order7 = "(p,w,b,t,s,z)";
        String order8 = "(z,w,b,s,t,p)";
        String order9 = "(t,z,p,w,b,s)";
        String order10 = "(z,p,w,b,s,t)";
        String order11 = "(p,w,b,s,t,z)";
        String order12 = "(w,b,s,t,z,p)";

        //Tests
        String erg_1 = all(order1, polynomials);
        String erg_2 = all(order2, polynomials);
        String erg_3 = all(order3, polynomials);
        String erg_4 = all(order4, polynomials);
        String erg_5 = all(order5, polynomials);
        String erg_6 = all(order6, polynomials);
        String erg_7 = all(order7, polynomials);
        String erg_8 = all(order8, polynomials);
        String erg_9 = all(order9, polynomials);
        String erg_10 = all(order10, polynomials);
        String erg_11 = all(order11, polynomials);
        String erg_12 = all(order12, polynomials);
        System.out.println(all);
        System.out.println(erg_1);
        System.out.println(erg_2);
        System.out.println(erg_3);
        System.out.println(erg_4);
        System.out.println(erg_5);
        System.out.println(erg_6);
        System.out.println(erg_7);
        System.out.println(erg_8);
        System.out.println(erg_9);
        System.out.println(erg_10);
        System.out.println(erg_11);
        System.out.println(erg_12);
    }


    public void xtestHairerRungeKutta_1() {
        String polynomials = "( " + " (a-f),(b-h-g),(e+d+c-1),(d*a+c*b-1/2),(d*a^2+c*b^2-1/3),(c*g*a-1/6)"
                + ") ";

        String[] order = new String[]{"a", "b", "c", "d", "e", "f", "g", "h"};

        String order1 = shuffle(order);
        String order2 = shuffle(order);
        String order3 = shuffle(order);
        String order4 = shuffle(order);
        String order5 = shuffle(order);

        // langsam (e,d,h,c,g,a,f,b), (h,d,b,e,c,g,a,f) um die 120

        // sehr langsam (e,h,d,c,g,b,a,f) um die 1000

        // sehr schnell (g,b,f,h,c,d,a,e), (h,c,a,g,d,f,e,b) 1 millisec

        String ergOnlyG_1 = grad(order1, polynomials);
        System.out.println(grad);
        System.out.println(ergOnlyG_1);

        String ergOnlyL_1 = lex(order1, polynomials);
        String ergOnlyL_2 = lex(order2, polynomials);
        String ergOnlyL_3 = lex(order3, polynomials);
        String ergOnlyL_4 = lex(order4, polynomials);
        String ergOnlyL_5 = lex(order5, polynomials);

        System.out.println(lex);
        System.out.println(ergOnlyL_1);
        System.out.println(ergOnlyL_2);
        System.out.println(ergOnlyL_3);
        System.out.println(ergOnlyL_4);
        System.out.println(ergOnlyL_5);

        //String ergGeneral = all(order, polynomials);
        //System.out.println(all);
        //System.out.println(ergGeneral);
    }


    //================================================================================================= 
    //Internal methods
    //=================================================================================================
    @SuppressWarnings({"unchecked", "cast"})
    public String all(String order, String polynomials) {
        GroebnerBaseFGLM<BigRational> IdealObjectFGLM;
        BigRational coeff = new BigRational();
        GroebnerBase<BigRational> gb = GBFactory.getImplementation(coeff);

        String polynomials_Grad = order + " G " + polynomials;
        String polynomials_Lex = order + " L " + polynomials;

        Reader sourceG = new StringReader(polynomials_Grad);
        GenPolynomialTokenizer parserG = new GenPolynomialTokenizer(sourceG);
        PolynomialList<BigRational> G = null;
        Reader sourceL = new StringReader(polynomials_Lex);
        GenPolynomialTokenizer parserL = new GenPolynomialTokenizer(sourceL);
        PolynomialList<BigRational> L = null;

        try {
            G = (PolynomialList<BigRational>) parserG.nextPolynomialSet();
            L = (PolynomialList<BigRational>) parserL.nextPolynomialSet();
        } catch (IOException e) {
            e.printStackTrace();
            return "fail";
        }
        System.out.println("Input " + G);
        System.out.println("Input " + L);

        //Computation of the Groebnerbase with Buchberger w.r.t INVLEX
        long buchberger_Lex = System.currentTimeMillis();
        List<GenPolynomial<BigRational>> GL = gb.GB(L.list);
        buchberger_Lex = System.currentTimeMillis() - buchberger_Lex;

        //Computation of the Groebnerbase with Buchberger w.r.t GRADLEX (Total degree + INVLEX)
        long buchberger_Grad = System.currentTimeMillis();
        List<GenPolynomial<BigRational>> GG = gb.GB(G.list);
        buchberger_Grad = System.currentTimeMillis() - buchberger_Grad;

        //PolynomialList<BigRational> GGG = new PolynomialList<BigRational>(G.ring, GG);
        //PolynomialList<BigRational> GLL = new PolynomialList<BigRational>(L.ring, GL);

        IdealObjectFGLM = new GroebnerBaseFGLM<BigRational>(); //GGG);
        //IdealObjectLex = new GroebnerBaseSeq<BigRational>(GLL);

        long tconv = System.currentTimeMillis();
        List<GenPolynomial<BigRational>> resultFGLM = IdealObjectFGLM.convGroebnerToLex(GG);
        tconv = System.currentTimeMillis() - tconv;

        OrderedPolynomialList<BigRational> o1 = new OrderedPolynomialList<BigRational>(GG.get(0).ring, GG);
        OrderedPolynomialList<BigRational> o2 = new OrderedPolynomialList<BigRational>(
                resultFGLM.get(0).ring, resultFGLM);
        //List<GenPolynomial<BigRational>> resultBuchberger = GL;
        OrderedPolynomialList<BigRational> o3 = new OrderedPolynomialList<BigRational>(GL.get(0).ring, GL);

        int grad_numberOfElements = GG.size();
        int lex_numberOfElements = resultFGLM.size();
        long grad_maxPolyGrad = PolyUtil.<BigRational>totalDegreeLeadingTerm(GG); // IdealObjectFGLM.maxDegreeOfGB();
        long lex_maxPolyGrad = PolyUtil.<BigRational>totalDegreeLeadingTerm(GL); // IdealObjectLex.maxDegreeOfGB();

        int grad_height = bitHeight(GG);
        int lex_height = bitHeight(resultFGLM);

        System.out.println("Order of Variables: " + order);
        System.out.println("Groebnerbases: ");
        System.out.println("Groebnerbase Buchberger (IGRLEX) " + o1);
        System.out.println("Groebnerbase FGML (INVLEX) computed from Buchberger (IGRLEX) " + o2);
        System.out.println("Groebnerbase Buchberger (INVLEX) " + o3);

        String erg = "BigRational |" + order + " |" + grad_numberOfElements + "          |"
                + lex_numberOfElements + "          |" + grad_height + "   |" + lex_height
                + "           |" + grad_maxPolyGrad + "      |" + lex_maxPolyGrad + "    |"
                + buchberger_Grad + "      |" + tconv + "       |" + buchberger_Lex;

        //assertEquals(o2, o3);
        if (!o2.equals(o3)) {
            throw new RuntimeException("FGLM != GB: " + o2 + " != " + o3);
        }
        return erg;
    }


    @SuppressWarnings({"unchecked", "cast"})
    public String fglm(String order, String polynomials) {
        GroebnerBaseFGLM<BigRational> IdealObjectGrad;
        //GroebnerBaseAbstract<BigRational> IdealObjectLex;

        BigRational coeff = new BigRational();
        GroebnerBase<BigRational> gb = GBFactory.getImplementation(coeff);

        String polynomials_Grad = order + " G " + polynomials;

        Reader sourceG = new StringReader(polynomials_Grad);
        GenPolynomialTokenizer parserG = new GenPolynomialTokenizer(sourceG);
        PolynomialList<BigRational> G = null;

        try {
            G = (PolynomialList<BigRational>) parserG.nextPolynomialSet();
        } catch (IOException e) {
            e.printStackTrace();
            return "fail";
        }
        System.out.println("Input " + G);

        //Computation of the Groebnerbase with Buchberger w.r.t GRADLEX (Total degree + INVLEX)
        long buchberger_Grad = System.currentTimeMillis();
        List<GenPolynomial<BigRational>> GG = gb.GB(G.list);
        buchberger_Grad = System.currentTimeMillis() - buchberger_Grad;

        //PolynomialList<BigRational> GGG = new PolynomialList<BigRational>(G.ring, GG);

        IdealObjectGrad = new GroebnerBaseFGLM<BigRational>(); //GGG);

        long tconv = System.currentTimeMillis();
        List<GenPolynomial<BigRational>> resultFGLM = IdealObjectGrad.convGroebnerToLex(GG);
        tconv = System.currentTimeMillis() - tconv;

        //PolynomialList<BigRational> LLL = new PolynomialList<BigRational>(G.ring, resultFGLM);
        //IdealObjectLex  = new GroebnerBaseSeq<BigRational>(); //LLL);

        OrderedPolynomialList<BigRational> o1 = new OrderedPolynomialList<BigRational>(GG.get(0).ring, GG);
        OrderedPolynomialList<BigRational> o2 = new OrderedPolynomialList<BigRational>(
                resultFGLM.get(0).ring, resultFGLM);

        int grad_numberOfElements = GG.size();
        int lex_numberOfElements = resultFGLM.size();
        long grad_maxPolyGrad = PolyUtil.<BigRational>totalDegreeLeadingTerm(GG); //IdealObjectGrad.maxDegreeOfGB();
        long lex_maxPolyGrad = PolyUtil.<BigRational>totalDegreeLeadingTerm(resultFGLM); //IdealObjectLex.maxDegreeOfGB();

        int grad_height = bitHeight(GG);
        int lex_height = bitHeight(resultFGLM);

        System.out.println("Order of Variables: " + order);
        System.out.println("Groebnerbases: ");
        System.out.println("Groebnerbase Buchberger (IGRLEX) " + o1);
        System.out.println("Groebnerbase FGML (INVLEX) computed from Buchberger (IGRLEX) " + o2);

        String erg = "BigRational |" + order + " |" + grad_numberOfElements + "         |"
                + lex_numberOfElements + "  |" + grad_height + "   |" + lex_height + "           |"
                + grad_maxPolyGrad + "      |" + lex_maxPolyGrad + "    |" + buchberger_Grad
                + "      |" + tconv;
        return erg;
    }


    @SuppressWarnings({"unchecked", "cast"})
    public String grad(String order, String polynomials) {
        BigRational coeff = new BigRational();
        GroebnerBase<BigRational> gb = GBFactory.getImplementation(coeff);

        String polynomials_Grad = order + " G " + polynomials;

        Reader sourceG = new StringReader(polynomials_Grad);
        GenPolynomialTokenizer parserG = new GenPolynomialTokenizer(sourceG);
        PolynomialList<BigRational> G = null;

        try {
            G = (PolynomialList<BigRational>) parserG.nextPolynomialSet();
        } catch (IOException e) {
            e.printStackTrace();
            return "fail";
        }
        System.out.println("Input " + G);

        //Computation of the Groebnerbase with Buchberger w.r.t GRADLEX (Total degree + INVLEX)
        long buchberger_Grad = System.currentTimeMillis();
        List<GenPolynomial<BigRational>> GG = gb.GB(G.list);
        buchberger_Grad = System.currentTimeMillis() - buchberger_Grad;

        //PolynomialList<BigRational> GGG = new PolynomialList<BigRational>(G.ring, GG);
        OrderedPolynomialList<BigRational> o1 = new OrderedPolynomialList<BigRational>(GG.get(0).ring, GG);

        GroebnerBaseFGLM<BigRational> IdealObjectGrad;
        IdealObjectGrad = new GroebnerBaseFGLM<BigRational>(); //GGG);
        long grad_maxPolyGrad = PolyUtil.<BigRational>totalDegreeLeadingTerm(GG); //IdealObjectGrad.maxDegreeOfGB();
        List<GenPolynomial<BigRational>> reducedTerms = IdealObjectGrad.redTerms(GG);
        OrderedPolynomialList<BigRational> o4 = new OrderedPolynomialList<BigRational>(
                reducedTerms.get(0).ring, reducedTerms);
        int grad_numberOfReducedElements = reducedTerms.size();
        int grad_numberOfElements = GG.size();
        int grad_height = bitHeight(GG);

        System.out.println("Order of Variables: " + order);
        System.out.println("Groebnerbases: ");
        System.out.println("Groebnerbase Buchberger (IGRLEX) " + o1);
        System.out.println("Reduced Terms" + o4);

        String erg = "BigRational |" + order + " |" + grad_numberOfElements + "    |" + grad_height + "    |"
                + grad_maxPolyGrad + "    |" + buchberger_Grad + "    |"
                + grad_numberOfReducedElements;
        return erg;
    }


    @SuppressWarnings({"unchecked", "cast"})
    public String lex(String order, String polynomials) {
        //GroebnerBaseAbstract<BigRational> IdealObjectLex;
        BigRational coeff = new BigRational();
        GroebnerBase<BigRational> gb = GBFactory.getImplementation(coeff);

        String polynomials_Lex = order + " L " + polynomials;

        Reader sourceL = new StringReader(polynomials_Lex);
        GenPolynomialTokenizer parserL = new GenPolynomialTokenizer(sourceL);
        PolynomialList<BigRational> L = null;

        try {
            L = (PolynomialList<BigRational>) parserL.nextPolynomialSet();
        } catch (IOException e) {
            e.printStackTrace();
            return "fail";
        }
        System.out.println("Input " + L);

        //Computation of the Groebnerbase with Buchberger w.r.t INVLEX
        long buchberger_Lex = System.currentTimeMillis();
        List<GenPolynomial<BigRational>> GL = gb.GB(L.list);
        buchberger_Lex = System.currentTimeMillis() - buchberger_Lex;

        //PolynomialList<BigRational> GLL = new PolynomialList<BigRational>(L.ring, GL);
        //IdealObjectLex = new GroebnerBaseAbstract<BigRational>(GLL);

        OrderedPolynomialList<BigRational> o3 = new OrderedPolynomialList<BigRational>(GL.get(0).ring, GL);

        int lex_numberOfElements = GL.size();
        long lex_maxPolyGrad = PolyUtil.<BigRational>totalDegreeLeadingTerm(GL); //IdealObjectLex.maxDegreeOfGB();
        int lexHeigth = bitHeight(GL);

        System.out.println("Order of Variables: " + order);
        System.out.println("Groebnerbase Buchberger (INVLEX) " + o3);

        String erg = "BigRational" + order + "|" + lex_numberOfElements + "     |" + lexHeigth + "    |"
                + lex_maxPolyGrad + "    |" + buchberger_Lex;
        return erg;
    }


    @SuppressWarnings({"unchecked", "cast"})
    public String modAll(String order, String polynomials, Integer m) {
        GroebnerBaseFGLM<ModInteger> IdealObjectFGLM;
        //GroebnerBaseAbstract<ModInteger> IdealObjectLex;
        ModIntegerRing ring = new ModIntegerRing(m);
        GroebnerBase<ModInteger> gb = GBFactory.getImplementation(ring);

        String polynomials_Grad = "Mod " + ring.modul + " " + order + " G " + polynomials;
        String polynomials_Lex = "Mod " + ring.modul + " " + order + " L " + polynomials;

        Reader sourceG = new StringReader(polynomials_Grad);
        GenPolynomialTokenizer parserG = new GenPolynomialTokenizer(sourceG);
        PolynomialList<ModInteger> G = null;
        Reader sourceL = new StringReader(polynomials_Lex);
        GenPolynomialTokenizer parserL = new GenPolynomialTokenizer(sourceL);
        PolynomialList<ModInteger> L = null;

        try {
            G = (PolynomialList<ModInteger>) parserG.nextPolynomialSet();
            L = (PolynomialList<ModInteger>) parserL.nextPolynomialSet();
        } catch (IOException e) {
            e.printStackTrace();
            return "fail";
        }
        System.out.println("G= " + G);
        System.out.println("L= " + L);

        //Computation of the Groebnerbase with Buchberger w.r.t INVLEX
        long buchberger_Lex = System.currentTimeMillis();
        List<GenPolynomial<ModInteger>> GL = gb.GB(L.list);
        buchberger_Lex = System.currentTimeMillis() - buchberger_Lex;

        //Computation of the Groebnerbase with Buchberger w.r.t GRADLEX (Total degree + INVLEX)
        long buchberger_Grad = System.currentTimeMillis();
        List<GenPolynomial<ModInteger>> GG = gb.GB(G.list);
        buchberger_Grad = System.currentTimeMillis() - buchberger_Grad;

        //PolynomialList<ModInteger> GGG = new PolynomialList<ModInteger>(G.ring, GG);
        //PolynomialList<ModInteger> GLL = new PolynomialList<ModInteger>(L.ring, GL);

        IdealObjectFGLM = new GroebnerBaseFGLM<ModInteger>(); //GGG);
        //IdealObjectLex = new GroebnerBaseAbstract<ModInteger>(GLL);

        long tconv = System.currentTimeMillis();
        List<GenPolynomial<ModInteger>> resultFGLM = IdealObjectFGLM.convGroebnerToLex(GG);
        tconv = System.currentTimeMillis() - tconv;

        OrderedPolynomialList<ModInteger> o1 = new OrderedPolynomialList<ModInteger>(GG.get(0).ring, GG);
        OrderedPolynomialList<ModInteger> o2 = new OrderedPolynomialList<ModInteger>(resultFGLM.get(0).ring,
                resultFGLM);
        List<GenPolynomial<ModInteger>> resultBuchberger = GL;
        OrderedPolynomialList<ModInteger> o3 = new OrderedPolynomialList<ModInteger>(
                resultBuchberger.get(0).ring, resultBuchberger);

        int grad_numberOfElements = GG.size();
        int lex_numberOfElements = resultFGLM.size();
        long grad_maxPolyGrad = PolyUtil.<ModInteger>totalDegreeLeadingTerm(GG); //IdealObjectFGLM.maxDegreeOfGB();
        long lex_maxPolyGrad = PolyUtil.<ModInteger>totalDegreeLeadingTerm(GL); //IdealObjectLex.maxDegreeOfGB();

        System.out.println("Order of Variables: " + order);
        System.out.println("Groebnerbases: ");
        System.out.println("Groebnerbase Buchberger (IGRLEX) " + o1);
        System.out.println("Groebnerbase FGML (INVLEX) computed from Buchberger (IGRLEX) " + o2);
        System.out.println("Groebnerbase Buchberger (INVLEX) " + o3);

        String erg = "Mod " + m + "    |" + order + " |" + grad_numberOfElements + "          |"
                + lex_numberOfElements + "          |" + grad_maxPolyGrad + "    |" + lex_maxPolyGrad
                + "    |" + buchberger_Grad + "     |" + tconv + "    |" + buchberger_Lex;

        //assertEquals(o2, o3);
        if (!o2.equals(o3)) {
            throw new RuntimeException("FGLM != GB: " + o2 + " != " + o3);
        }
        return erg;
    }


    @SuppressWarnings({"unchecked", "cast"})
    public String modGrad(String order, String polynomials, Integer m) {
        //GroebnerBaseFGLM<ModInteger> IdealObjectFGLM;
        ModIntegerRing ring = new ModIntegerRing(m);
        GroebnerBase<ModInteger> gb = GBFactory.getImplementation(ring);

        String polynomials_Grad = "Mod " + ring.modul + " " + order + " G " + polynomials;

        Reader sourceG = new StringReader(polynomials_Grad);
        GenPolynomialTokenizer parserG = new GenPolynomialTokenizer(sourceG);
        PolynomialList<ModInteger> G = null;

        try {
            G = (PolynomialList<ModInteger>) parserG.nextPolynomialSet();
        } catch (IOException e) {
            e.printStackTrace();
            return "fail";
        }
        System.out.println("G= " + G);

        //Computation of the Groebnerbase with Buchberger w.r.t GRADLEX (Total degree + INVLEX)
        long buchberger_Grad = System.currentTimeMillis();
        List<GenPolynomial<ModInteger>> GG = gb.GB(G.list);
        buchberger_Grad = System.currentTimeMillis() - buchberger_Grad;

        //PolynomialList<ModInteger> GGG = new PolynomialList<ModInteger>(G.ring, GG);
        //IdealObjectFGLM = new GroebnerBaseFGLM<ModInteger>(); //GGG);

        OrderedPolynomialList<ModInteger> o1 = new OrderedPolynomialList<ModInteger>(GG.get(0).ring, GG);

        int grad_numberOfElements = GG.size();
        long grad_maxPolyGrad = PolyUtil.<ModInteger>totalDegreeLeadingTerm(GG); //IdealObjectFGLM.maxDegreeOfGB();

        System.out.println("Order of Variables: " + order);
        System.out.println("Groebnerbases: ");
        System.out.println("Groebnerbase Buchberger (IGRLEX) " + o1);

        String erg = "Mod " + m + "    |" + order + " |" + grad_numberOfElements + "           |"
                + grad_maxPolyGrad + "    |" + buchberger_Grad;
        return erg;
    }


    @SuppressWarnings({"unchecked", "cast"})
    public String modfglm(String order, String polynomials, Integer m) {
        GroebnerBaseFGLM<ModInteger> IdealObjectFGLM;
        //GroebnerBaseAbstract<ModInteger> IdealObjectLex;
        ModIntegerRing ring = new ModIntegerRing(m);
        GroebnerBase<ModInteger> gb = GBFactory.getImplementation(ring);

        String polynomials_Grad = "Mod " + ring.modul + " " + order + " G " + polynomials;

        Reader sourceG = new StringReader(polynomials_Grad);
        GenPolynomialTokenizer parserG = new GenPolynomialTokenizer(sourceG);
        PolynomialList<ModInteger> G = null;

        try {
            G = (PolynomialList<ModInteger>) parserG.nextPolynomialSet();
        } catch (IOException e) {
            e.printStackTrace();
            return "fail";
        }
        System.out.println("G= " + G);

        //Computation of the Groebnerbase with Buchberger w.r.t GRADLEX (Total degree + INVLEX)
        long buchberger_Grad = System.currentTimeMillis();
        List<GenPolynomial<ModInteger>> GG = gb.GB(G.list);
        buchberger_Grad = System.currentTimeMillis() - buchberger_Grad;

        //PolynomialList<ModInteger> GGG = new PolynomialList<ModInteger>(G.ring, GG);
        IdealObjectFGLM = new GroebnerBaseFGLM<ModInteger>(); //GGG);

        long tconv = System.currentTimeMillis();
        List<GenPolynomial<ModInteger>> resultFGLM = IdealObjectFGLM.convGroebnerToLex(GG);
        tconv = System.currentTimeMillis() - tconv;

        //PolynomialList<ModInteger> LLL = new PolynomialList<ModInteger>(G.ring, resultFGLM);
        //IdealObjectLex  = new GroebnerBaseAbstract<ModInteger>(LLL);

        OrderedPolynomialList<ModInteger> o1 = new OrderedPolynomialList<ModInteger>(GG.get(0).ring, GG);
        OrderedPolynomialList<ModInteger> o2 = new OrderedPolynomialList<ModInteger>(resultFGLM.get(0).ring,
                resultFGLM);

        int grad_numberOfElements = GG.size();
        int lex_numberOfElements = resultFGLM.size();
        long grad_maxPolyGrad = PolyUtil.<ModInteger>totalDegreeLeadingTerm(GG); //IdealObjectFGLM.maxDegreeOfGB();
        long lex_maxPolyGrad = PolyUtil.<ModInteger>totalDegreeLeadingTerm(resultFGLM); //IdealObjectLex.maxDegreeOfGB();

        System.out.println("Order of Variables: " + order);
        System.out.println("Groebnerbases: ");
        System.out.println("Groebnerbase Buchberger (IGRLEX) " + o1);
        System.out.println("Groebnerbase FGML (INVLEX) computed from Buchberger (IGRLEX) " + o2);

        String erg = "Mod " + m + "    |" + order + " |" + grad_numberOfElements + "         |"
                + lex_numberOfElements + "           |" + grad_maxPolyGrad + "    |"
                + lex_maxPolyGrad + "    |" + buchberger_Grad + "     |" + tconv;
        return erg;
    }


    /**
     * Method shuffle returns a random permutation of a string of variables.
     */
    public String shuffle(String[] tempOrder) {
        Collections.shuffle(Arrays.asList(tempOrder));
        StringBuffer ret = new StringBuffer("(");
        ret.append(ExpVector.varsToString(tempOrder));
        ret.append(")");
        return ret.toString();
    }


    /**
     * Method bitHeight returns the bitlength of the greatest number occurring
     * during the computation of a Groebner base.
     */
    public int bitHeight(List<GenPolynomial<BigRational>> list) {
        BigInteger denom = BigInteger.ONE;
        BigInteger num = BigInteger.ONE;
        for (GenPolynomial<BigRational> g : list) {
            for (Monomial<BigRational> m : g) {
                BigRational bi = m.coefficient();
                BigInteger i = bi.denominator().abs();
                BigInteger j = bi.numerator().abs();
                if (i.compareTo(denom) > 0)
                    denom = i;
                if (j.compareTo(num) > 0)
                    num = j;
            }
        }
        int erg;
        if (denom.compareTo(num) > 0) {
            erg = denom.bitLength();
        } else {
            erg = num.bitLength();
        }
        return erg;
    }

}
