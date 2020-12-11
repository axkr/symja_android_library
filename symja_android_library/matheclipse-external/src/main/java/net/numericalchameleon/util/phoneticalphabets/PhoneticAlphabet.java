/**
 * NumericalChameleon 3.0.0 - more than an unit converter - a NumericalChameleon Copyright (c)
 * 2001-2020 Dipl.-Inf. (FH) Johann Nepomuk Loefflmann, All Rights Reserved,
 * <http://www.numericalchameleon.net>.
 *
 * <p>This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * <p>You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package net.numericalchameleon.util.phoneticalphabets;

import java.util.Hashtable;
import java.util.Vector;

public class PhoneticAlphabet {

  private Hashtable hash;

  // see also
  // http://montgomery.cas.muohio.edu/meyersde/PhoneticAlphabets.htm
  // http://www.faqs.org/faqs/radio/phonetic-alph/full/
  // http://en.wikipedia.org/wiki/RAF_phonetic_alphabet
  public static final String[]
      INTERNATIONAL =
          {
            "Amsterdam",
            "Baltimore",
            "Casablanca",
            "Danemark",
            "Edison",
            "Florida",
            "Gallipoli",
            "Havanna",
            "Italia",
            "Jerusalem",
            "Kilogramme",
            "Liverpool",
            "Madagaskar",
            "New York",
            "Oslo",
            "Paris",
            "Québec",
            "Roma",
            "Santiago",
            "Tripoli",
            "Upsala",
            "Valencia",
            "Washington",
            "Xanthippe",
            "Yokohama",
            "Zürich"
          },
      NATO =
          {
            "Alfa", "Bravo", "Charlie", "Delta", "Echo", "Foxtrot", "Golf", "Hotel", "India",
                "Juliett",
            "Kilo", "Lima", "Mike", "November", "Oscar", "Papa", "Quebec", "Romeo", "Sierra",
                "Tango",
            "Uniform", "Viktor", "Whiskey", "X-ray", "Yankee", "Zulu"
          },
      AMERICAN =
          {
            "Abel", "Baker", "Charlie", "Dog", "Easy", "Fox", "George", "How", "Item", "Jig",
            "King", "Love", "Mike", "Nan", "Oboe", "Peter", "Queen", "Roger", "Sugar", "Tare",
            "Uncle", "Victor", "William", "X", "Yoke", "Zebra"
          },

      // http://en.wikipedia.org/wiki/Joint_Army/Navy_Phonetic_Alphabet
      // Joint Army/Navy (1941-1955)
      ARMYNAVY =
          {
            "Able", "Baker", "Charlie", "Dog", "Easy", "Fox", "George", "How", "Item", "Jig",
            "King", "Love", "Mike", "Nutley", "Oboe", "Peter", "Queen", "Roger", "Sail", "Tare",
            "Uncle", "Victor", "William", "X-ray", "Zebra"
          },
      BRITISH =
          {
            "Andrew",
            "Benjamin",
            "Charlie",
            "David",
            "Edward",
            "Frederick",
            "George",
            "Harry",
            "Isaac",
            "Jack",
            "King",
            "Lucy",
            "Mary",
            "Nellie",
            "Oliver",
            "Peter",
            "Queenie",
            "Robert",
            "Sugar",
            "Tommy",
            "Uncle",
            "Victor",
            "William",
            "Xmas",
            "Yellow",
            "Zebra"
          },

      // Langenscheidt
      GERMAN =
          {
            "Anton", "Ärger", "Berta", "Cäsar", "CHarlotte", "Dora", "Emil", "Friedrich", "Gustav",
            "Heinrich", "Ida", "Julius", "Kaufmann", "Ludwig", "Martha", "Nordpol", "Otto",
                "Ökonom",
            "Paula", "Quelle", "Richard", "Samuel", "SCHule", "Theodor", "Ulrich", "Übermut",
                "Viktor",
            "Wilhelm", "Xanthippe", "Ypsilon", "Zacharias"
          },

      // Berlitz phrase book
      FRENCH =
          {
            "Anatole",
            "Berthe",
            "Célestin",
            "Désiré",
            "Eugène",
            "Émile",
            "François",
            "Gaston",
            "Henri",
            "Irma",
            "Joseph",
            "Kléber",
            "Louis",
            "Marcel",
            "Nicolas",
            "Oscar",
            "Pierre",
            "Quintal",
            "Raoul",
            "Suzanne",
            "Thérèse",
            "Ursule",
            "Victor",
            "William",
            "Xavier",
            "Yvonne",
            "Zoé"
          },
      SPANISH =
          {
            "Antonio",
            "Barcelona",
            "Carmen",
            "CHocolate",
            "Dolores",
            "Enrique",
            "Francia",
            "Gerona",
            "Historia",
            "Inés",
            "José",
            "Kilo",
            "Lorenzo",
            "LLobregat",
            "Madrid",
            "Navarra",
            "Ñoño",
            "Oviedo",
            "París",
            "Querido",
            "Ramón",
            "Sábado",
            "Tarragona",
            "Ulises",
            "Valencia",
            "Washington",
            "Xiquena",
            "Yegua",
            "Zaragoza"
          },
      ITALIAN =
          {
            "Ancona",
            "Bologna",
            "Como",
            "Domodossola",
            "Empoli",
            "Forli",
            "Genova",
            "Hotel",
            "Imola",
            "Jolanda",
            "Kappa",
            "Livorno",
            "Milano",
            "Napoli",
            "Otranto",
            "Pisa",
            "Quattro",
            "Roma",
            "Salerno",
            "Torino",
            "Udine",
            "Venezia",
            "Washington",
            "Xilofono",
            "Ypsilon",
            "Zeta"
          },
      PORTUGUESE =
          {
            "Aveiro", "Braga", "Coimbra", "Dafundo", "Évora", "Faro", "Guarda", "Horta", "Itália",
            "José", "Kodak", "Lisboa", "Maria", "Nazaré", "Ovar", "Porto", "Queluz", "Rossio",
            "Setúbal", "Tavira", "Unidade", "Vidago", "Waldemar", "Xavier", "York", "Zulmira"
          },

      // Berlitz phrase book
      DANISH =
          {
            "Anna", "Åase", "Ægir", "Bernhard", "Cecilia", "David", "Erik", "Frederik", "Georg",
            "Hans", "Ida", "Johan", "Karen", "Ludvig", "Marie", "Nikolaj", "Odin", "Øeresund",
            "Peter", "Quintus", "Rasmus", "Soeren", "Theodor", "Ulla", "Viggo", "William", "Xerxes",
            "Yrsa", "Zacharias"
          },

      // Berlitz phrase book
      DUTCH =
          {
            "Anna",
            "Bernard",
            "Cornelis",
            "Dirk",
            "Eduard",
            "Ferdinand",
            "Gerard",
            "Hendrik",
            "Izaak",
            "Jan",
            "Karel",
            "Lodewijk",
            "Marie",
            "Nico",
            "Otto",
            "Pieter",
            "Quadraat",
            "Rudolf",
            "Simon",
            "Teunis",
            "Utrecht",
            "Victor",
            "Willem",
            "Xantippe",
            "IJmuiden",
            "Ypsilon",
            "Zaandam"
          },
      SWEDISH =
          {
            "Adam", "Bertil", "Cesar", "David", "Erik", "Filip", "Gustav", "Helge", "Ivar", "Johan",
            "Kalle", "Ludvig", "Martin", "Niklas", "Olof", "Petter", "Quintus", "Rudolf", "Sigurd",
            "Tore", "Urban", "Viktor", "Wilhelm", "Xerxes", "Yngve", "Zäta", "Åke", "Ärlig", "Östen"
          },
      HEBREW =
          {
            "Affula",
            "Binyamina",
            "Carmel",
            "Dalia",
            "Eretz",
            "France",
            "Gedera",
            "Haifa",
            "Israel",
            "Jaffa",
            "Karkur",
            "Lod",
            "Moledet",
            "Naan",
            "Ogen",
            "Pardes",
            "Queen",
            "Rishon",
            "Sefer",
            "Tveria",
            "Urim",
            "Vered",
            "Wingate",
            "Express",
            "Yavniel",
            "Zikhron"
          },

      // Royal Air Force (1924-1942)
      RAF_1924_1942 =
          {
            "Ace", "Beer", "Charlie", "Don", "Edward", "Freddie", "George", "Harry", "Ink",
            "Johnnie", "King", "London", "Monkey", "Nuts", "Orange", "Pip", "Queen", "Robert",
            "Sugar", "Toc", "Uncle", "Vic", "William", "X-ray", "Yorker", "Zebra"
          },

      // Royal Air Force (1942-1943)
      RAF_1942_1943 =
          {
            "Apple", "Beer", "Charlie", "Dog", "Edward", "Freddy", "George", "Harry", "In",
                "Johnny",
            "King", "Love", "Mother", "Nuts", "Orange", "Peter", "Queen", "Roger", "Sugar", "Tommy",
            "Uncle", "Vic", "William", "X-ray", "Yorker", "Zebra"
          },

      // Royal Air Force (1943-1956)
      RAF_1943_1956 =
          {
            "Able-Affirm", "Baker", "Charlie", "Dog", "Easy", "Fox", "George", "How", "Item",
                "Johnny",
            "King", "Love", "Mike", "Negat", "Oboe", "Peter", "Queen", "Roger", "Sugar", "Tare",
            "Uncle", "Victor", "William", "X-ray", "Yoke", "Zebra"
          },

      // Royal Navy (World War I)
      BRITISH_ROYAL_NAVY =
          {
            "Apples", "Butter", "Charlie", "Duff", "Edward", "Freddy", "George", "Harry", "Ink",
            "Johnny", "King", "London", "Monkey", "Nuts", "Orange", "Pudding", "Queenie", "Robert",
            "Sugar", "Tommy", "Uncle", "Vinegar", "Willie", "Xerxes", "Yellow", "Zebra"
          },
      AUSTRIAN =
          {
            "Anton",
            "Ärger",
            "Berta",
            "Cäsar", /* "CHristine", */
            "Dora",
            "Emil",
            "Friedrich",
            "Gustav",
            "Heinrich",
            "Ida",
            "Julius",
            "Konrad",
            "Ludwig",
            "Martha",
            "Nordpol",
            "Otto",
            "Österreich",
            "Paula",
            "Quelle",
            "Richard",
            "Siegfried",
            "SCHule",
            "Theodor",
            "Ulrich",
            "Übel",
            "Viktor",
            "Wilhelm",
            "Xaver",
            "Ypsilon",
            "Zürich"
          },
      SWISS =
          {
            "Anna",
            "Äsch",
            "Berta",
            "Cäsar",
            "Daniel",
            "Emil",
            "Friedrich",
            "Gustav",
            "Heinrich",
            "Ida",
            "Jakob",
            "Kaiser",
            "Leopold",
            "Marie",
            "Niklaus",
            "Otto",
            "Örlikon",
            "Peter",
            "Quasi",
            "Rosa",
            "Sophie",
            "Theodor",
            "Ulrich",
            "Übermut",
            "Viktor",
            "Wilhelm",
            "Xaver",
            "Yverdon",
            "Zürich"
          },
      MORSE =
          {
            // ABCDEFGHIJKLMNOPQRSTUVWXYZ
            ".-",
            "-...",
            "-.-.",
            "-..",
            ".",
            "..-.",
            "--.",
            "....",
            "..",
            ".---",
            "-.-",
            ".-..",
            "--",
            "-.",
            "---",
            ".--.",
            "--.-",
            ".-.",
            "...",
            "-",
            "..-",
            "...-",
            ".--",
            "-..-",
            "-.--",
            "--..",

            // 0123456789
            "-----",
            ".----",
            "..---",
            "...--",
            "....-",
            ".....",
            "-....",
            "--...",
            "---..",
            "----.",

            // ,.?;:/-'()_"
            "--..--",
            ".-.-.-",
            "..--..",
            "-.-.-",
            "---...",
            "-..-.",
            "-....-",
            ".----.",
            "-.--.-",
            "-.--.-",
            "..--.-",
            ".-..-.",

            // �������@
            ".-.-",
            ".--.-",
            ".--.-",
            "..-..",
            "--.--",
            "---.",
            "..--",
            ".--.-."
          };

  // register all special prefixes with this array
  private String[] moreThanOne = null;

  /**
   * Creates a new instance of Spell
   *
   * @param alphabet an array of Strings that represents a spelling-alphabet
   */
  public PhoneticAlphabet(String[] alphabet) {
    hash = new Hashtable();
    Vector moreThanOneVector = new Vector();
    // fill the hashtable
    if (alphabet == MORSE) {
      String code = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.?;:/-'()_\"�������@";
      for (int i = 0; i < code.length(); i++) {
        hash.put(code.substring(i, i + 1), alphabet[i]);
      }
      hash.put("CH", "----");
      moreThanOneVector.add("CH");
    } else {
      int i, k;
      for (i = 0; i < alphabet.length; i++) {
        // search for the first lowercase char
        for (k = 0; k < alphabet[i].length(); k++) {
          if (alphabet[i].charAt(k) == Character.toLowerCase(alphabet[i].charAt(k))) break;
        }
        // Values with first uppercase only
        if (k <= 1) hash.put(alphabet[i].substring(0, k), alphabet[i]);
        else {
          hash.put(
              alphabet[i].substring(0, k),
              alphabet[i].substring(0, 1)
                  + alphabet[i].substring(1, k).toLowerCase()
                  + alphabet[i].substring(k, alphabet[i].length()));
          // add definition to the vector
          moreThanOneVector.add(alphabet[i].substring(0, k));
        }
      }
    }
    // to String array
    moreThanOne = new String[moreThanOneVector.size()];
    for (int i = 0; i < moreThanOneVector.size(); i++) {
      moreThanOne[i] = (String) moreThanOneVector.get(i);
    }
  }

  /** Default constuctor with INTERNATIONAL alphabet */
  public PhoneticAlphabet() {
    this(INTERNATIONAL);
  }

  /** @param text the text to spell */
  public Vector get(String text) {
    Vector<String> v = new Vector<String>();
    String textupper = text.toUpperCase();
    String temp;
    boolean done;
    for (int i = 0; i < textupper.length(); i++) {
      done = false;
      // perhaps there is a special alphabet (e. g. the german one)
      for (int x = 0; x < moreThanOne.length; x++) {
        int len = moreThanOne[x].length();
        if ((textupper.length() - i >= len)
            && (textupper.substring(i, i + len).equals(moreThanOne[x].toUpperCase()))) {
          temp = ((String) hash.get(moreThanOne[x]));
          if (temp != null) { // I found it in the hashtable
            v.add(temp);
            done = true; // I have done the job already!
            i += (len - 1);
          }
        }
      }

      if (!done) {
        temp = (String) hash.get(textupper.substring(i, i + 1));
        if (temp != null) v.add(temp);
        else v.add(textupper.substring(i, i + 1));
      }
    }
    return v;
  }

  public String get(char c) {
    // 1.4+ only:
    // String key = Character.toString(c);
    String key = String.valueOf(c);
    String temp = (String) hash.get(key.toUpperCase());
    return ((temp != null) ? temp : key);
  }
}
