package org.matheclipse.core.convert;

import java.io.IOException;
import java.io.StringWriter;

import org.apfloat.Apfloat;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

/** Export an expression to <code>ExpressionJSON</code> format. */
public class ExpressionJSONConvert {

  static final ObjectMapper objectMapper = new ObjectMapper();

  public ExpressionJSONConvert() {}

  /**
   * Export an expression to <code>ExpressionJSON</code> format.
   *
   * @param expr
   * @return
   * @throws IOException
   * @throws JsonGenerationException
   * @throws JsonMappingException
   */
  public static String exportString(IExpr expr)
      throws IOException, JsonGenerationException, JsonMappingException {
    return exportJSON(expr).toString();
  }

  /**
   * Export an expression to <code>ExpressionJSON</code> format.
   *
   * @param expr
   * @return
   * @throws IOException
   * @throws JsonGenerationException
   * @throws JsonMappingException
   */
  public static IStringX exportIStringX(IExpr expr)
      throws IOException, JsonGenerationException, JsonMappingException {
    return F.stringx(exportString(expr));
  }

  /**
   * Export an expression to <code>ExpressionJSON</code> format.
   *
   * @param expr
   * @return
   * @throws IOException
   * @throws JsonGenerationException
   * @throws JsonMappingException
   */
  public static JsonNode exportJSON(IExpr expr)
      throws IOException, JsonGenerationException, JsonMappingException {
    if (expr.isASTOrAssociation()) {
      IAST ast = (IAST) expr;
      ArrayNode temp = objectMapper.createArrayNode();
      temp.add(ast.head().toString());
      for (int i = 1; i < ast.size(); i++) {
        IExpr arg = ast.getRule(i);
        if (arg.isComplexNumeric()) {
          IComplexNum complexNum = (IComplexNum) arg;
          ArrayNode complexJson = objectMapper.createArrayNode();
          complexJson.add("Complex");
          complexJson.add(complexNum.reDoubleValue());
          complexJson.add(complexNum.imDoubleValue());
          temp.add(complexJson);
        } else if (arg instanceof Num) {
          temp.add(((Num) arg).doubleValue());
        } else if (arg instanceof ApfloatNum) {
          Apfloat apfloatValue = ((ApfloatNum) arg).apfloatValue();
          if (apfloatValue.precision() > 20L) {
            temp.add(apfloatValue.toString());
          } else {
            temp.add(apfloatValue.doubleValue());
          }
        } else if (arg.isNumber() || arg.isSymbol()) {
          temp.add(arg.toString());
        } else if (arg.isString()) {
          temp.add("'" + arg.toString() + "'");
        } else {
          temp.add(exportJSON(arg));
        }
      }
      return temp;
    }
    ArrayNode temp = objectMapper.createArrayNode();
    temp.add(temp.toString());
    return temp;
  }
}
