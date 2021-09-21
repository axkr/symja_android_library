package com.twosigma.beakerx.symjamma.autocomplete;

import java.util.ArrayList;
import org.matheclipse.core.builtin.IOFunctions;
import com.twosigma.beakerx.autocomplete.AutocompleteResult;
import com.twosigma.beakerx.autocomplete.AutocompleteServiceBeakerx;
import com.twosigma.beakerx.autocomplete.MagicCommandAutocompletePatterns;

public class SymjaMMAAutocomplete extends AutocompleteServiceBeakerx {

  @Override
  protected AutocompleteResult doAutocomplete(String txt, int cur) {
    try {
      int lastPosition = cur--;
      while (cur >= 0) {
        char ch = txt.charAt(cur);
        if (Character.isJavaIdentifierPart(ch)) {
          cur--;
          continue;
        }
        break;
      }
      cur++;
      if (lastPosition > cur + 1) {
        String name = txt.substring(cur, lastPosition); // .substring(1);
        return new AutocompleteResult(IOFunctions.getAutoCompletionList(name), cur);
      }
    } catch (Exception e) {
    }
    return new AutocompleteResult(new ArrayList<>(), 0);
  }

  public SymjaMMAAutocomplete(MagicCommandAutocompletePatterns autocompletePatterns) {
    super(autocompletePatterns);
  }

  // public static void main(String[] args) {
  // SymjaMMAAutocomplete auto = new SymjaMMAAutocomplete(new
  // MagicCommandAutocompletePatternsImpl());
  // auto.doAutocomplete("Facto", 5);
  // }
}
