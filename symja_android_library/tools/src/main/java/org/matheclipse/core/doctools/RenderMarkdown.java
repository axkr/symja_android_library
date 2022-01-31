package org.matheclipse.core.doctools;

import java.util.Collections;
import java.util.Set;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class RenderMarkdown {
  private static String TABLE = "\n" + //
      "##  Expression types\n" + //
      "\n" + //
      "\n" + //
      "|Type                   | Description                                                                                                     | Input Example                           |\n"
      + //
      "| ----------------------|:---------------------------------------------------------------------------------------------------------------:| ---------------------------------------:|\n"
      + //
      "|Integer                | integer numbers                                                                                                 | `` 42 ``                                |\n"
      + //
      "|Rational               | rational numbers                                                                                                | `` 13/17 ``                             |\n"
      + //
      "|Complex                | complex numbers                                                                                                 | `` 2+I*(1/3) ``                         |\n"
      + //
      "|Real                   | double values                                                                                                   | `` 0.5 ``                               |\n"
      + //
      "|Complex Real           | complex double values                                                                                           | `` 0.5-I*0.25 ``                        |\n"
      + //
      "|Evaluation Precedence	| control precedence with `` (…) ``                                                                             | `` (a+b)*c ``                           |\n"
      + //
      "|Lists                  | comma separated list of elements which are sourrounded by `` { … } ``                                         | `` {a, b, c, d} ``                      |\n"
      + //
      "|Vectors	            | vectors are like list, but cannot contain sublists `` { … } ``                                                | `` {1, 2, 3, 4} ``                      |\n"
      + //
      "|Matrices	            | a matrix contains the rows as sublists                                                                          | `` { {1, 2}, {3, 4} } ``                |\n"
      + //
      "|Predefined Functions	| predefined function names start with an upper case character and the arguments are surrounded by `` ( … ) ``  | `` Sin(0), PrimeQ(13) ``                |\n"
      + //
      "|Predefined Constants	| predefined constant names start with an upper case character                                                    | `` Degree, E, Pi, False, True, … ``   |\n"
      + //
      "|Userdefined variables	| identifiers which you would like to assign a value start with a '``$``' character                               | `` $a=42 ``                             |\n"
      + //
      "|Userdefined rules	    | identifiers which you would like to assign a rule start with a '``$``' character                                | `` $f(x_,y_):={x,y} ``                  |\n"
      + //
      "|Pattern Symbols	    | patterns end with a preceding '``_``' and could have a constraint                                               | `` $f(x_IntegerQ):={x} ``               |\n"
      + //
      "|Strings	            | character strings are enclosed by double quote characters                                                       | `` \"Hello World\"``                      |\n"
      + //
      "|Slots	                | a `` # `` character followed by an optional number                                                              | `` # `` or `` #2 ``                     |\n"
      + //
      "|Pure Functions	        | pure functions can be expressed with the & operator                                                             | `` (#^3)&[x] `` gives ``x^3``           |\n"
      + //
      "|Parts of an expression	| `` expression[ [index] ] ``                                                                                     | `` {a, b, c, d}[ [2] ] `` gives `` b `` |\n"
      + //
      "";

  public static void main(String[] args) {
    Set<Extension> EXTENSIONS = Collections.singleton(TablesExtension.create());
    Parser parser = Parser.builder().extensions(EXTENSIONS).build();
    Node document = parser.parse(TABLE);
    HtmlRenderer renderer = HtmlRenderer.builder().extensions(EXTENSIONS).build();
    String html = renderer.render(document);
    System.out.println(html);
  }
}
