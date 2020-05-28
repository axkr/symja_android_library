/**
 *
 * NumericalChameleon 3.0.0 - more than an unit converter - a NumericalChameleon
 * Copyright (c) 2001-2020 Dipl.-Inf. (FH) Johann Nepomuk Loefflmann, All Rights
 * Reserved, <http://www.numericalchameleon.net>.
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package net.numericalchameleon.util.spokennumbers;

/**
 * Source: Apa Guide Indien, Polyglott, 1. Auflage 1999 (0-9)
 *
 */
public class IndianHindiNumber extends SpokenNumber {
    /*
     * Some numbers have similarity in pronunciation, but there is no clear rule
     * for numbers in Hindi.
     */

    private final static String field[] = 
    {
// http://www.hindi.co/ginatee/numbers_saNkhyaaENn.html
"शून्य", // 0
"एक",
"दो",
"तीन",
"चार",
"पाँच",
"छह",
"सात",
"आठ",
"नौ",
"दस",
"ग्यारह",
"बारह",
"तेरह",
"चौदह",
"पन्द्रह",
"सोलह",
"सत्रह",
"अठारह",
"उन्नीस",
"बीस",
"इक्कीस",
"बाईस",
"तेईस",
"चौबीस",
"पच्चीस",
"छब्बीस",
"सत्ताईस",
"अट्ठाईस",
"उनतीस",
"तीस",
"इकतीस",
"बत्तीस",
"तैंतीस",
"चौंतीस",
"पैंतीस",
"छत्तीस",
"सैंतीस",
"अड़तीस",
"उनतालीस",
"चालीस",
"इकतालीस",
"बयालीस",
"तैंतालीस",
"चौवालीस",
"पैंतालीस",
"छियालीस",
"सैंतालीस",
"अड़तालीस",
"उनचास",
"पचास",
"इक्यावन",
"बावन",
"तिरेपन",
"चौवन",
"पचपन",
"छप्पन",
"सत्तावन",
"अट्ठावन",
"उनसठ",
"साठ",
"इकसठ",
"बासठ",
"तिरेसठ",
"चौंसठ",
"पैंसठ",
"छियासठ",
"सड़सठ",
"अड़सठ",
"उनहत्तर",
"सत्तर",
"इकहत्तर",
"बहत्तर",
"तिहत्तर",
"चौहत्तर",
"पचहत्तर",
"छिहत्तर",
"सतहत्तर",
"अठहत्तर",
"उनासी",
"अस्सी",
"इक्यासी",
"बयासी",
"तिरासी",
"चौरासी",
"पचासी",
"छियासी",
"सत्तासी",
"अट्ठासी",
"नवासी",
"नब्बे",
"इक्यानबे",
"बानबे",
"तिरानबे",
"चौरानबे",
"पंचानबे",
"छियानबे",
"सत्तानबे",
"अट्ठानबे",
"निन्यानबे" // 99

};

    private final static String transliteration[] = 
    {
"śunya",
"ēk",
"do",
"tīn",
"chār",
"pānch",
"chah",
"sāat",
"āat",
"nau",
"das",
"gyārah",
"bārah",
"tērah",
"chaudah",
"pandrah",
"solah",
"satrah",
"atthārah",
"unnis",
"bīs",
"ikkīsa",
"bāīsa",
"tēīsa",
"chaubīsa",
"pachchīsa",
"chabbīsa",
"sattāīsa",
"atthāīsa",
"unatīsa",
"tīsa",
"ikatīsa",
"battīsa",
"taimtīsa",
"chaumtīsa",
"paimtīsa",
"chattīsa",
"saimtīsa",
"aratīsa",
"unchālīsa",
"chālīsa",
"ikatālīsa",
"bayālīsa",
"taimtālīsa",
"chaumtālīsa",
"paimtālīsa",
"chiyālīsa",
"saimtālīsa",
"aratālīsa",
"unacāsa",
"pacāsa",
"ikyābana",
"bāvana",
"tirēpana",
"chaubana",
"pachapana",
"chappana",
"sattāvana",
"atthāvana",
"unasatha",
"sātha",
"ikasatha",
"bāsatha",
"tirasatha",
"chaumsatha",
"paimsatha",
"chiyāsatha",
"sarasatha",
"arasatha",
"unahattara",
"sattara",
"ikahattara",
"bahattara",
"tihattara",
"chauhattara",
"pachahattara",
"chihattara",
"satahattara",
"athahattara",
"unāsī",
"assī",
"ikyāsī",
"bayāsī",
"tirāsī",
"chaurāsī",
"pachāsī",
"chiyāsī",
"satāsī",
"athāsī",
"navāsī",
"nabbē",
"ikyānabē",
"bānavē",
"tirānavē",
"chaurānavē",
"pachānavē",
"chiyānavē",
"sattānavē",
"atthānavē",
"ninyānavē"
};
    
    /*{"shoonya", "ek", "do", "teen", "chaar", "paanch", "chah", "sāt", "āth", "nau", // 0-9
        "das", "gyāreh", "barah", "tērah", "caudah", "pundruh", "soluh", "satruh", "atthaaruh", "unnees", // 10-19
        "bees", "ikkees", "baaees", "tayees", "chaubees", "puchchees", "chhubbees", "sattaaees", "atthaaees", "unatees", // 20-29
        "tees", "ikattees", "battees", "tayntees", "chauntees", "payntees", "chchattees", "sayntees", "aratees", "untaalees", // 30-39
        "chaalees", "iktaalees", "bayaalees", "tayntaalees", "chouaalees", "payntaalees", "chhiyaatees", "sayntaalees", "artaalees", "unchaas", // 40-49
        "puchaas", "ikyaawun", "baawun", "tirpun", "chouwun", "pachpun", "chhappun", "sattaawun", "atthhaawun", "unsuth", // 50-59
        "saath", "iksath", "baasuth", "tirsath", "chaunsath", "paynsath", "chhiyaasath", "sursath", "arsath", "unhattar", // 60-69
        "suttar", "ikhuttar", "buhuttar", "tihuttar", "chauhuttar", "puchuhuttar", "chhihuttar", "sutuhuttar", "athuhuttar", "unyaasee", // 70-79
        "ussee", "ikyaasee", "buyaasee", "tiraasee", "chauraasee", "puchaasee", "chhiyaasee", "suttaasee", "utthaasee", "nuwaasee", // 80-89
        "nubbay", "ikyaanuway", "baanuway", "tiraanuway", "chauraanuway", "puchaanuway", "chhiyaanuway", "suttaanuway", "utthhaanuway", "ninyaanuway" // 90-99
    },
            
*/

private final static String MINUS = "-";
    private int index = 0;
    /**
     * Creates new IndianHindi
     */
    public IndianHindiNumber() {
        super();
    }

    public IndianHindiNumber(long number) throws Exception {
        super(number);
    }

    public IndianHindiNumber(String number) throws Exception {
        super(number);
    }

    @Override
    protected int getSupportedDigits() {
        return 2;
    }

    @Override
    public String getSoundDir() {
        return "hindi";
    }

    /**
     * wird zum Konvertieren der Zahl in Silben benötigt, Ergebnis wird in einen
     * Vektor geschrieben.
     */
    @Override
    protected void convert2Syllables() throws Exception {

        if (number.charAt(0) == '-') {
            number = number.substring(1);
            syllables.add(MINUS + " ");
        }

        // we just would like the digits
        if (numberType == DIGITS) {
            fillSyllables(field);
            return;
        }

        number2digits();
        index =  (digits[1] * 10) + digits[0];
        syllables.add(field[index]);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < syllables.size(); i++) {
            sb.append(syllables.get(i).toString());
            if (numberType == DIGITS && appendBlank) {
                sb.append(" ");
            }
        }
        if (numberType != DIGITS) {
            sb.append(" (");
            sb.append(transliteration[index]);
            sb.append(")");
        }
        return sb.toString().trim();
    }
    
    /**
     * gibt Nummer in Hindi Worten zurück.
     *
     * @param number die zu konvertierende Zahl
     */
    public static String toString(long number) throws Exception {
        return (new IndianHindiNumber(number)).toString();
    }

    public static String toString(String number) throws Exception {
        return (new IndianHindiNumber(number)).toString();
    }
}
