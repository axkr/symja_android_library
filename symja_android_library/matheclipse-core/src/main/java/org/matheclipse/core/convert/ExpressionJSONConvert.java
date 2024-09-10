package org.matheclipse.core.convert;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;
import org.apfloat.Apfloat;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.parser.client.Scanner;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BigIntegerNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DecimalNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.FloatNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ShortNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.node.ValueNode;

/** Import and export an expression to and from <code>ExpressionJSON</code> format. */
public class ExpressionJSONConvert {

  public static final ObjectMapper JSON_OBJECT_MAPPER = new ObjectMapper();

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
  public static String exportExpressionJSONString(IExpr expr)
      throws IOException, JsonGenerationException, JsonMappingException {
    return exportExpressionJSON(expr).toString();
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
  public static IStringX exportExpressionJSONIStringX(IExpr expr)
      throws IOException, JsonGenerationException, JsonMappingException {
    return F.stringx(exportExpressionJSONString(expr));
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
  public static JsonNode exportExpressionJSON(IExpr expr) {
    if (expr.isASTOrAssociation()) {
      IAST ast = (IAST) expr;
      ArrayNode temp = JSON_OBJECT_MAPPER.createArrayNode();
      temp.add(ast.head().toString());
      for (int i = 1; i < ast.size(); i++) {
        IExpr arg = ast.getRule(i);
        if (arg.isComplexNumeric()) {
          IComplexNum complexNum = (IComplexNum) arg;
          ArrayNode complexJson = JSON_OBJECT_MAPPER.createArrayNode();
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
          if (arg.isTrue()) {
            temp.add(true);
          } else if (arg.isFalse()) {
            temp.add(false);
          } else {
            temp.add(arg.toString());
          }
        } else if (arg.isString()) {
          temp.add("'" + arg.toString() + "'");
        } else {
          temp.add(exportExpressionJSON(arg));
        }
      }
      return temp;
    }
    ArrayNode temp = JSON_OBJECT_MAPPER.createArrayNode();
    if (expr.isSymbol()) {
      if (expr.isTrue()) {
        temp.add(true);
      } else if (expr.isFalse()) {
        temp.add(false);
      } else {
        temp.add(temp.toString());
      }
    } else {
      temp.add(temp.toString());
    }
    return temp;
  }

  // private static JsonNode exportGraphics3DJSON(IExpr data3D) {
  // if (data3D.isList()) {
  // IAST list = (IAST) data3D;
  // ArrayNode temp = JSON_OBJECT_MAPPER.createArrayNode();
  // for (int i = 1; i < list.size(); i++) {
  // IExpr arg = list.getAST(i);
  // if (arg.isAST()) {
  // IAST ast = (IAST) arg;
  // if (ast.isBuiltInFunction()) {
  // StringBuilder buf = new StringBuilder();
  // IBuiltInSymbol symbol = (IBuiltInSymbol) ast.head();
  // IEvaluator evaluator = symbol.getEvaluator();
  // if (evaluator instanceof IGraphics3D) {
  // // JsonNode n = ((IGraphics3D) evaluator).graphics3D(buf, (IAST) ast);
  // // temp.add(n);
  // }
  // }
  // }
  // }
  // return temp;
  // }
  // ArrayNode temp = JSON_OBJECT_MAPPER.createArrayNode();
  // temp.add(temp.toString());
  // return temp;
  // }

  public static IExpr importExpressionJSONRecursive(JsonNode node) {
    if (node instanceof ArrayNode) {
      ArrayNode arrayNode = (ArrayNode) node;
      Iterator<JsonNode> iter = arrayNode.elements();
      IASTAppendable ast;
      if (iter.hasNext()) {
        JsonNode next = iter.next();
        IExpr temp = importExpressionJSONRecursive(next);
        if (temp.isPresent()) {
          ast = F.ast(temp, arrayNode.size() - 1);
          while (iter.hasNext()) {
            next = iter.next();
            temp = importExpressionJSONRecursive(next);
            if (temp.isPresent()) {
              ast.append(temp);
            }
          }
          return ast;
        }
      }
      return F.NIL;
    } else if (node instanceof ObjectNode) {
      IASTAppendable list = F.ListAlloc();
      ObjectNode objectNode = (ObjectNode) node;
      Iterator<Entry<String, JsonNode>> iter = objectNode.fields();
      while (iter.hasNext()) {
        Entry<String, JsonNode> next = iter.next();
        IExpr temp = importExpressionJSONRecursive(next.getValue());
        if (temp.isPresent()) {
          list.append(F.Rule(F.$str(next.getKey()), temp));
        }
      }
      return list;
    } else if (node instanceof ValueNode) {
      ValueNode valueNode = (ValueNode) node;
      if (valueNode instanceof NumericNode) {
        if (valueNode instanceof DoubleNode) {
          return F.num(valueNode.doubleValue());
        } else if (valueNode instanceof FloatNode) {
          return F.num(valueNode.doubleValue());
        } else if (valueNode instanceof IntNode) {
          return F.ZZ(valueNode.intValue());
        } else if (valueNode instanceof LongNode) {
          return F.ZZ(valueNode.longValue());
        } else if (valueNode instanceof ShortNode) {
          return F.ZZ(valueNode.intValue());
        } else if (valueNode instanceof BigIntegerNode) {
          return F.ZZ(valueNode.bigIntegerValue());
        } else if (valueNode instanceof DecimalNode) {
          return F.num(new Apfloat(valueNode.decimalValue()));
        }
      }
      if (valueNode instanceof BooleanNode) {
        return valueNode.booleanValue() ? S.True : S.False;
      } else if (valueNode instanceof NullNode) {
        return S.Null;
      } else if (valueNode instanceof TextNode) {
        String symbolName = valueNode.textValue();
        if (symbolName.length() > 1 && symbolName.charAt(0) == '\''
            && symbolName.charAt(symbolName.length() - 1) == '\'') {
          return F.$str(symbolName.substring(1, symbolName.length() - 1));
        }
        if (Scanner.isIdentifier(symbolName)) {
          return F.symbol(symbolName);
        }
        return F.$str(symbolName);
      }
      return F.$str(valueNode.toString());
    }
    return F.NIL;
  }

  /**
   * Convert the JSON String into a Symja expression.
   * 
   * @param jsonStr
   * @return
   * @throws JsonMappingException
   * @throws JsonProcessingException
   */
  public static IExpr importExpressionJSON(String jsonStr)
      throws JsonMappingException, JsonProcessingException {
    JsonNode node = JSON_OBJECT_MAPPER.readTree(jsonStr);
    return importExpressionJSONRecursive(node);
  }
}
