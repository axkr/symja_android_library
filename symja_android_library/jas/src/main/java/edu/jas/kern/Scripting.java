/*
 * $Id$
 */

package edu.jas.kern;


/**
 * Scripting, defines script language for output in toScript() method.
 * @author Heinz Kredel
 */

public class Scripting {


    public static enum Lang {
        Python, Ruby
    };


    private static Lang script = Lang.Python;


    protected Scripting() {
    }


    /**
     * Get scripting language which is in effect.
     * @return language which is to be used for toScript().
     */
    public static Lang getLang() {
        return script;
    }


    /**
     * Set scripting language.
     * @param s language which is to be used for toScript()
     * @return old language setting.
     */
    public static Lang setLang(Lang s) {
        Lang o = script;
        script = s;
        return o;
    }

}
