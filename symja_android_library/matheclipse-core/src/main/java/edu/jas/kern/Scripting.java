/*
 * $Id$
 */

package edu.jas.kern;


/**
 * Scripting, defines script language for output in toScript() method.
 *
 * @author Heinz Kredel
 */

public class Scripting {


    private static Lang script = Lang.Python;

    ;
    private static CAS cas = CAS.JAS;
    private static int precision = -1; // == fraction output

    ;


    protected Scripting() {
    }

    /**
     * Get scripting language which is in effect.
     *
     * @return language which is to be used for toScript().
     */
    public static Lang getLang() {
        return script;
    }

    /**
     * Set scripting language.
     *
     * @param s language which is to be used for toScript()
     * @return old language setting.
     */
    public static Lang setLang(Lang s) {
        Lang o = script;
        script = s;
        return o;
    }

    /**
     * Get CAS for Order which is in effect.
     *
     * @return CAS which is to be used for toScript().
     */
    public static CAS getCAS() {
        return cas;
    }

    /**
     * Set CAS for order.
     *
     * @param s CAS which is to be used for toScript()
     * @return old CAS setting.
     */
    public static CAS setCAS(CAS s) {
        CAS o = cas;
        cas = s;
        return o;
    }

    /**
     * Get decimal approximation precision for scripting.
     *
     * @return number of decimals after '.'.
     */
    public static int getPrecision() {
        return precision;
    }

    /**
     * Set decimal approximation precision for scripting.
     *
     * @param p number of decimals after '.'
     * @return old number of decimals after '.'.
     */
    public static int setPrecision(int p) {
        int o = precision;
        precision = p;
        return o;
    }


    public static enum Lang {
        Python, Ruby
    }


    public static enum CAS {
        JAS, Math, Sage, Singular
    }

}
