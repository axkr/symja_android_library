package org.matheclipse.core.reflection.system;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Map.Entry;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apfloat.Apfloat;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.io.Extension;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.ast.ASTNode;
import com.fasterxml.jackson.core.JsonParseException;
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

/** Import some data from a given string. */
public class ImportString extends AbstractEvaluator {
  private static final Logger LOGGER = LogManager.getLogger();

  public ImportString() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (!(ast.arg1() instanceof IStringX)) {
      return F.NIL;
    }

    String str1 = ((IStringX) ast.arg1()).toString();
    Extension format = Extension.TXT;

    if (ast.size() > 2) {
      if (!(ast.arg2() instanceof IStringX)) {
        return F.NIL;
      }
      format = Extension.importExtension(((IStringX) ast.arg2()).toString());
    }

    try {
      switch (format) {
        case JSON:
          return ofJSON(str1);
        case TABLE:
          AST2Expr ast2Expr = new AST2Expr(engine.isRelaxedSyntax(), engine);
          final Parser parser = new Parser(engine.isRelaxedSyntax(), true);

          CSVFormat csvFormat = CSVFormat.RFC4180.withDelimiter(',');
          Iterable<CSVRecord> records = csvFormat.parse(new StringReader(str1));
          IASTAppendable rowList = F.ListAlloc(256);
          for (CSVRecord record : records) {
            IASTAppendable columnList = F.ListAlloc(record.size());
            for (String string : record) {
              final ASTNode node = parser.parse(string);
              IExpr temp = ast2Expr.convert(node);
              columnList.append(temp);
            }
            rowList.append(columnList);
          }
          return rowList;
        case STRING:
          return ofString(str1, engine);
        case TXT:
          return ofText(str1, engine);
        default:
      }

    } catch (SyntaxError se) {
      LOGGER.log(engine.getLogLevel(), "ImportString: syntax error!", se);
    } catch (Exception ex) {
      LOGGER.log(engine.getLogLevel(), "ImportString", ex);
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_2;
  }

  public static IExpr importJSONRecursive(JsonNode node) {
    if (node instanceof ArrayNode) {
      IASTAppendable list = F.ListAlloc();
      ArrayNode arrayNode = (ArrayNode) node;
      Iterator<JsonNode> iter = arrayNode.elements();
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

  public static IExpr ofJSON(String str) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode node = mapper.readTree(str);
      return ImportString.importJSONRecursive(node);
    } catch (JsonParseException e) {
      e.printStackTrace();
    } catch (JsonMappingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return F.NIL;
  }

  /**
   * Get arbitrary data represented as a Symja expression string
   *
   * @param str
   * @param engine
   * @return
   */
  public static IExpr ofString(String str, EvalEngine engine) {
    AST2Expr ast2Expr = new AST2Expr(engine.isRelaxedSyntax(), engine);
    final Parser parser = new Parser(engine.isRelaxedSyntax(), true);
    final ASTNode node = parser.parse(str);
    return ast2Expr.convert(node);
  }

  /**
   * Get plain text from file
   *
   * @param str
   * @param engine
   * @return
   */
  public static IExpr ofText(String str, EvalEngine engine) {
    return F.stringx(str);
  }
}
