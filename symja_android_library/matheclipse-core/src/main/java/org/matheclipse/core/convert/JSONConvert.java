package org.matheclipse.core.convert;

import java.util.Iterator;
import java.util.Map.Entry;
import org.apfloat.Apfloat;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IASTAppendable;
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

  public static IExpr importJSONRecursive(JsonNode node) {
    if (node instanceof ArrayNode) {
      ArrayNode arrayNode = (ArrayNode) node;
      Iterator<JsonNode> iter = arrayNode.elements();
      IASTAppendable list = F.ListAlloc(arrayNode.size());
      while (iter.hasNext()) {
        JsonNode next = iter.next();
        IExpr temp = importJSONRecursive(next);
        if (temp.isPresent()) {
          list.append(temp);
        }
      }
      return list;
    } else if (node instanceof ObjectNode) {
      IASTAppendable list = F.ListAlloc();
      ObjectNode objectNode = (ObjectNode) node;
      Iterator<Entry<String, JsonNode>> iter = objectNode.fields();
      while (iter.hasNext()) {
        Entry<String, JsonNode> next = iter.next();
        IExpr temp = importJSONRecursive(next.getValue());
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
   * @return
   * @throws JsonMappingException
   * @throws JsonProcessingException
   */
  public static IExpr importJSON(String jsonStr)
      throws JsonMappingException, JsonProcessingException {
    JsonNode node = JSON_OBJECT_MAPPER.readTree(jsonStr);
    return importJSONRecursive(node);
  }
}
