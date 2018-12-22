/* $Id:ErrorCode.java 179 2008-08-01 13:41:24Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.definitions;

import uk.ac.ed.ph.snuggletex.ErrorCode;
import uk.ac.ed.ph.snuggletex.ErrorGroup;

/**
 * Enumerates the various types of client-induced errors that can arise when using the
 * functionality provided by the core SnuggleTeX module.
 * 
 * <h2>Developer Notes</h2>
 * 
 * <ul>
 *   <li>
 *     See the <tt>core-error-messages_en.properties</tt> file for descriptions of each code.
 *   </li>
 *   <li>
 *     To make things easier to read, add a comment whenever raising an error that briefly
 *     summarises the error.
 *   </li>
 *   <li>
 *     I'm using a kind of naming convention for these, as mentioned
 *     in the error-messages_en.properties. Note that the 3rd character
 *     indicates the type of error, which in all cases so far is 'E' for Error.
 *   </li>
 * </ul>
 * 
 * @since 1.2.0
 *
 * @author  David McKain
 * @version $Revision:179 $
 */
public enum CoreErrorCode implements ErrorCode {
    
    /* ================ Errors in the normal SnuggleTeX -> MathML Process (T) =============== */
    
    /* Tokenisation errors */
    TTEG00(CoreErrorGroup.TTE),
    TTEG01(CoreErrorGroup.TTE),
    TTEG02(CoreErrorGroup.TTE),
    TTEG03(CoreErrorGroup.TTE),
    TTEG04(CoreErrorGroup.TTE),
    TTEM00(CoreErrorGroup.TTE),
    TTEM01(CoreErrorGroup.TTE),
    TTEM02(CoreErrorGroup.TTE),
    TTEM03(CoreErrorGroup.TTE),
    TTEM04(CoreErrorGroup.TTE),
    TTEV00(CoreErrorGroup.TTE),
    TTEV01(CoreErrorGroup.TTE),
    TTEC00(CoreErrorGroup.TTE),
    TTEC01(CoreErrorGroup.TTE),
    TTEC02(CoreErrorGroup.TTE),
    TTEC03(CoreErrorGroup.TTE),
    TTEC04(CoreErrorGroup.TTE),
    TTEE00(CoreErrorGroup.TTE),
    TTEE01(CoreErrorGroup.TTE),
    TTEE02(CoreErrorGroup.TTE),
    TTEE03(CoreErrorGroup.TTE),
    TTEE04(CoreErrorGroup.TTE),
    TTEE05(CoreErrorGroup.TTE),
    TTEE06(CoreErrorGroup.TTE),
    TTEU00(CoreErrorGroup.TTE),
    TTEUC0(CoreErrorGroup.TTE),
    TTEUC1(CoreErrorGroup.TTE),
    TTEUC2(CoreErrorGroup.TTE),
    TTEUC3(CoreErrorGroup.TTE),
    TTEUC4(CoreErrorGroup.TTE),
    TTEUC5(CoreErrorGroup.TTE),
    TTEUC6(CoreErrorGroup.TTE),
    TTEUC7(CoreErrorGroup.TTE),
    TTEUC8(CoreErrorGroup.TTE),
    TTEUC9(CoreErrorGroup.TTE),
    TTEUCA(CoreErrorGroup.TTE),
    TTEUE0(CoreErrorGroup.TTE),
    TTEUE1(CoreErrorGroup.TTE),
    TTEUE2(CoreErrorGroup.TTE),
    TTEUE3(CoreErrorGroup.TTE),
//    TTEUE4(CoreErrorGroup.TTE),
    TTEUE5(CoreErrorGroup.TTE),
    TTEUE6(CoreErrorGroup.TTE),
    
    /* Fixing errors */
    TFEG00(CoreErrorGroup.TFE),
    TFEL00(CoreErrorGroup.TFE),
    TFEM00(CoreErrorGroup.TFE),
    TFEM01(CoreErrorGroup.TFE),
    TFEM02(CoreErrorGroup.TFE),
    TFEM03(CoreErrorGroup.TFE),
    TFEM04(CoreErrorGroup.TFE),
    TFETB0(CoreErrorGroup.TFE),
    
    /* DOM Building errors */
    TDEG00(CoreErrorGroup.TDE),
    TDEX00(CoreErrorGroup.TDE),
    TDEX01(CoreErrorGroup.TDE),
    TDEX02(CoreErrorGroup.TDE),
    TDEX03(CoreErrorGroup.TDE),
    TDEX04(CoreErrorGroup.TDE),
    TDEX05(CoreErrorGroup.TDE),
    TDEXU0(CoreErrorGroup.TDE),
    TDEXU1(CoreErrorGroup.TDE),
    TDEM00(CoreErrorGroup.TDE),
    TDEM01(CoreErrorGroup.TDE),
    TDEMA0(CoreErrorGroup.TDE),
    TDEMA1(CoreErrorGroup.TDE),
    TDEMA2(CoreErrorGroup.TDE),
    TDEMM0(CoreErrorGroup.TDE),
    TDEL00(CoreErrorGroup.TDE),
    TDETA0(CoreErrorGroup.TDE),
    TDETA1(CoreErrorGroup.TDE),
    TDETA2(CoreErrorGroup.TDE),
    TDETB0(CoreErrorGroup.TDE),
    TDETB1(CoreErrorGroup.TDE),
    TDETB2(CoreErrorGroup.TDE),
    TDETB3(CoreErrorGroup.TDE),
    TDEUN0(CoreErrorGroup.TDE),
    TDEUN1(CoreErrorGroup.TDE),
    TDEUN2(CoreErrorGroup.TDE),

    ;
    
    private final CoreErrorGroup errorGroup;
    
    private CoreErrorCode(CoreErrorGroup errorGroup) {
        this.errorGroup = errorGroup;
    }
    
    public String getName() {
        return name();
    }
    
    public ErrorGroup getErrorGroup() {
        return errorGroup;
    }
}
