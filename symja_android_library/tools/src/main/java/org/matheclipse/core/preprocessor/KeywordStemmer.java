package org.matheclipse.core.preprocessor;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.standard.ClassicFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.matheclipse.core.expression.F;

public class KeywordStemmer {

  static class Keyword implements Comparable<Keyword> {
    private final String stem;
    private final Set<String> terms = new HashSet<>();
    private int frequency;

    public Keyword(String stem) {
      this.stem = stem;
    }

    public void add(String term) {
      this.terms.add(term);
      this.frequency++;
    }

    @Override
    public int compareTo(Keyword keyword) {
      return Integer.valueOf(keyword.frequency).compareTo(this.frequency);
    }

    @Override
    public int hashCode() {
      return this.getStem().hashCode();
    }

    @Override
    public boolean equals(Object o) {
      if (this == o)
        return true;

      if (!(o instanceof Keyword))
        return false;

      Keyword that = (Keyword) o;

      return this.getStem().equals(that.getStem());
    }

    // ----- getters, setters

    public String getStem() {
      return this.stem;
    }

    public Set<String> getTerms() {
      return this.terms;
    }

    public int getFrequency() {
      return this.frequency;
    }
  }

  static class KeywordsExtractor {

    private List<Keyword> getKeywordsList(String fullText) throws IOException {

      TokenStream tokenStream = null;

      try {

        StandardTokenizer stdToken = new StandardTokenizer();
        stdToken.setReader(new StringReader(fullText));

        tokenStream =
            new StopFilter(new ASCIIFoldingFilter(new ClassicFilter(new LowerCaseFilter(stdToken))),
                EnglishAnalyzer.getDefaultStopSet());
        tokenStream.reset();

        List<Keyword> cardKeywords = new LinkedList<>();

        CharTermAttribute token = tokenStream.getAttribute(CharTermAttribute.class);

        while (tokenStream.incrementToken()) {

          String term = token.toString();
          String stem = getStemForm(term);

          if (stem != null && stem.length() > 3) {
            Keyword cardKeyword = find(cardKeywords, new Keyword(stem.replaceAll("-0", "-")));
            // treat the dashed words back, let look them pretty
            cardKeyword.add(term.replaceAll("-0", "-"));
          }
        }

        // reverse sort by frequency
        Collections.sort(cardKeywords);

        return cardKeywords;
      } finally {
        if (tokenStream != null) {
          try {
            tokenStream.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    }

    private String getStemForm(String term) throws IOException {

      TokenStream tokenStream = null;

      try {
        StandardTokenizer stdToken = new StandardTokenizer();
        stdToken.setReader(new StringReader(term));

        tokenStream = new PorterStemFilter(stdToken);
        tokenStream.reset();

        // eliminate duplicate tokens by adding them to a set
        Set<String> stems = new HashSet<>();

        CharTermAttribute token = tokenStream.getAttribute(CharTermAttribute.class);

        while (tokenStream.incrementToken()) {
          stems.add(token.toString());
        }

        // if stem form was not found or more than 2 stems have been found, return null
        if (stems.size() != 1) {
          return null;
        }

        String stem = stems.iterator().next();

        // if the stem form has non-alphanumerical chars, return null
        if (!stem.matches("[a-zA-Z0-9-]+")) {
          return null;
        }

        return stem;
      } finally {
        if (tokenStream != null) {
          try {
            tokenStream.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    }

    private <T> T find(Collection<T> collection, T sample) {

      for (T element : collection) {
        if (element.equals(sample)) {
          return element;
        }
      }

      collection.add(sample);

      return sample;
    }
  }

  private static void extract(String text) throws IOException {
    KeywordsExtractor keywordsExtractor = new KeywordsExtractor();
    List<Keyword> keywords = keywordsExtractor.getKeywordsList(text);

    for (Keyword keyword : keywords) {
      StringBuilder buffer = new StringBuilder();
      // buffer.append(keyword.getFrequency());
      buffer.append(" \"" + keyword.getStem() + "\"");
      buffer.append(", F." + text + ", //");

      // buffer.append(" [");
      // for (String term : keyword.getTerms()) {
      // buffer.append(term + " ");
      // }
      // buffer.append("]");

      System.out.println(buffer.toString());
    }
  }

  public static void main(String[] args) {
    try {
      F.initSymja();
      String userHonme = System.getProperty("user.home");
      File sourceLocation = new File(
          // C:\\Users\\dev\\git\\symja_android_library\\
          userHonme + "/git/symja_android_library/symja_android_library/doc/functions");
      if (sourceLocation.exists()) {
        // Get the list of the files contained in the package
        final String[] files = sourceLocation.list();
        if (files != null) {
          System.out.println("	public static final Object[] STEMS = new Object[] { //");
          for (int i = 0; i < files.length; i++) {
            // we are only interested in .md files
            if (files[i].endsWith(".md")) {
              String className = files[i].substring(0, files[i].length() - 3);
              extract(className);
            }
          }
          System.out.println("};");
        }
      }

      // extract("expandall fullform hornerform horner mathmlform mathml texform tex treeform
      // tree");
      //
      // System.out.println("<<<<<");
      // extract("simplifing expanding converting integrating deriving differentiating factoring
      // Reducing
      // solving");
      // System.out.println("<<<<<");
      // extract("simplify expand convert integrate derive differentiate factor Reduce reducer solve
      // solver");
      // System.out.println("<<<<<");
      // extract("simplification expansion conversion integral integration derivation
      // differentiation
      // factorization reducing solving");
      // System.out.println("<<<<<");
      //
      // System.out.println("<<<<<");

    } catch (Exception ex) {
      System.err.println("caught ex: " + ex.getMessage());
    }
  }
}
