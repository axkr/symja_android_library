package org.matheclipse.nlp;

import org.matheclipse.core.expression.S; 

public class NLPInit {
  public static void init() { 
    S.IntegerName.setEvaluator(new org.matheclipse.nlp.builtin.IntegerName());
    S.RemoveDiacritics.setEvaluator(new org.matheclipse.nlp.builtin.RemoveDiacritics());
    S.Transliterate.setEvaluator(new org.matheclipse.nlp.builtin.Transliterate());
  }
}
