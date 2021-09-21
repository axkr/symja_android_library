package org.matheclipse.api;

import org.apache.commons.lang3.StringUtils;
import org.matheclipse.core.data.ElementData;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class ElementDataPod implements IPod {
  String word;

  public ElementDataPod(String word) {
    this.word = word;
  }

  @Override
  public short podType() {
    return ELEMENT_DATA;
  }

  @Override
  public String keyWord() {
    return word;
  }

  @Override
  public int addJSON(ArrayNode podsArray, int formats, EvalEngine engine) {
    String[] properties = ElementData.PROPERTIES_DATA;
    final IStringX wordStrX = F.stringx(word);
    int numpods = 0;
    for (int i = 0; i < properties.length; i++) {
      IExpr inExpr = F.ElementData(wordStrX, F.stringx(properties[i]));
      IExpr podOut = EvalEngine.get().evaluate(inExpr);
      if (!podOut.isAST(S.Missing)) {
        String title =
            StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(properties[i]), ' ');
        Pods.addSymjaPod(podsArray, inExpr, podOut, title, "Data", formats, engine);
        numpods++;
      }
    }

    return numpods;
  }
}
