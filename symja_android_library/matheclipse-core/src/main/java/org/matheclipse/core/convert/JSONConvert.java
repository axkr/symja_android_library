package org.matheclipse.core.convert;

import java.util.Iterator;
import java.util.Map.Entry;
import org.apfloat.Apfloat;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IExpr;
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

/** Import and export an expression to and from <code>JSON</code> format. */
public class JSONConvert {

  public static final ObjectMapper JSON_OBJECT_MAPPER = new ObjectMapper();

  public JSONConvert() {}

  public static IExpr importJSONRecursive(JsonNode node, boolean rawJSON) {
    if (node instanceof ArrayNode) {
      ArrayNode arrayNode = (ArrayNode) node;
      Iterator<JsonNode> iter = arrayNode.elements();
      IASTAppendable list = F.ListAlloc(arrayNode.size());
      while (iter.hasNext()) {
        JsonNode next = iter.next();
        IExpr temp = importJSONRecursive(next, rawJSON);
        if (temp.isPresent()) {
          list.append(temp);
        }
      }
      return list;
    } else if (node instanceof ObjectNode) {
      if (rawJSON) {
        // identify JSON objects as associations of rules
        IAssociation assoc = F.assoc();
        ObjectNode objectNode = (ObjectNode) node;
        Iterator<Entry<String, JsonNode>> iter = objectNode.fields();
        while (iter.hasNext()) {
          Entry<String, JsonNode> next = iter.next();
          IExpr temp = importJSONRecursive(next.getValue(), rawJSON);
          if (temp.isPresent()) {
            assoc.appendRule(F.Rule(F.$str(next.getKey()), temp));
          }
        }
        return assoc;
      }
      // identify JSON objects as list of rules
      IASTAppendable list = F.ListAlloc();
      ObjectNode objectNode = (ObjectNode) node;
      Iterator<Entry<String, JsonNode>> iter = objectNode.fields();
      while (iter.hasNext()) {
        Entry<String, JsonNode> next = iter.next();
        IExpr temp = importJSONRecursive(next.getValue(), rawJSON);
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
        return F.$str(valueNode.textValue());
      }
      return F.$str(valueNode.toString());
    }
    return F.NIL;
  }

  /**
   * Convert the JSON String into a Symja expression.
   * 
   * @param jsonStr
   * @param rawJSON TODO
   * @return
   * @throws JsonMappingException
   * @throws JsonProcessingException
   */
  /**
   * Write an expression as JSON.
   *
   * <p>
   * An association becomes an object, a list an array, and <code>True</code>/<code>False</code>/
   * <code>Null</code> the JSON literals; anything else that is not a number or a string is written
   * as its input form, which is the only faithful thing to do with it.
   *
   * @param rawJSON <code>true</code> for the "RawJSON" shape, which is the same text - the
   *        difference between the two formats is how they are read back, not how they are written
   */
  public static String exportJSON(IExpr expr, boolean rawJSON) {
    StringBuilder buffer = new StringBuilder();
    writeJSON(expr, buffer);
    return buffer.toString();
  }

  private static void writeJSON(IExpr expr, StringBuilder buffer) {
    if (expr.isAssociation()) {
      IAssociation association = (IAssociation) expr;
      IAST rules = association.normal(false);
      buffer.append('{');
      for (int i = 1; i < rules.size(); i++) {
        if (i > 1) {
          buffer.append(',');
        }
        IExpr rule = rules.get(i);
        writeString(rule.first().toString(), buffer);
        buffer.append(':');
        writeJSON(rule.second(), buffer);
      }
      buffer.append('}');
      return;
    }
    if (expr.isList()) {
      IAST list = (IAST) expr;
      if (list.forAll(x -> x.isRuleAST())) {
        // a list of rules is an object too, the way Import reads one back
        buffer.append('{');
        for (int i = 1; i < list.size(); i++) {
          if (i > 1) {
            buffer.append(',');
          }
          writeString(list.get(i).first().toString(), buffer);
          buffer.append(':');
          writeJSON(list.get(i).second(), buffer);
        }
        buffer.append('}');
        return;
      }
      buffer.append('[');
      for (int i = 1; i < list.size(); i++) {
        if (i > 1) {
          buffer.append(',');
        }
        writeJSON(list.get(i), buffer);
      }
      buffer.append(']');
      return;
    }
    if (expr.isString()) {
      writeString(expr.toString(), buffer);
      return;
    }
    if (expr.isTrue()) {
      buffer.append("true");
      return;
    }
    if (expr.isFalse()) {
      buffer.append("false");
      return;
    }
    if (expr == S.Null || expr.isAST(S.Missing)) {
      buffer.append("null");
      return;
    }
    if (expr.isNumber() && expr.isReal()) {
      buffer.append(expr.toString());
      return;
    }
    writeString(expr.toString(), buffer);
  }

  private static void writeString(String value, StringBuilder buffer) {
    buffer.append('"');
    for (int i = 0; i < value.length(); i++) {
      char ch = value.charAt(i);
      switch (ch) {
        case '"':
          buffer.append("\\\"");
          break;
        case '\\':
          buffer.append("\\\\");
          break;
        case '\n':
          buffer.append("\\n");
          break;
        case '\r':
          buffer.append("\\r");
          break;
        case '\t':
          buffer.append("\\t");
          break;
        default:
          if (ch < 0x20) {
            buffer.append(String.format("\\u%04x", (int) ch));
          } else {
            buffer.append(ch);
          }
      }
    }
    buffer.append('"');
  }

  public static IExpr importJSON(String jsonStr, boolean rawJSON)
      throws JsonMappingException, JsonProcessingException {
    JsonNode node = JSON_OBJECT_MAPPER.readTree(jsonStr);
    return importJSONRecursive(node, rawJSON);
  }

}
