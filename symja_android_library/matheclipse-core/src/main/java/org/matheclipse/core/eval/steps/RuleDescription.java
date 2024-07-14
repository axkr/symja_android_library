package org.matheclipse.core.eval.steps;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import org.apache.commons.io.IOUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

/** A map for the description templates of the rule steps. */
public class RuleDescription {

  /** The english steps description */
  public static final RuleDescription EN_RULES = new RuleDescription();

  static {
    try {

      Map<String, List<String>> supportedLanguages =
          Map.of("en", Lists.newArrayList("i18n/en.json"));

      for (Map.Entry<String, List<String>> entry : supportedLanguages.entrySet()) {
        for (String file : entry.getValue()) {
          ClassLoader classLoader = RuleDescription.class.getClassLoader();
          InputStream stream = classLoader.getResourceAsStream(file);
          String jsonString =
              IOUtils.toString(Objects.requireNonNull(stream), StandardCharsets.UTF_8);
          stream.close();

          ObjectMapper mapper = new ObjectMapper();
          JsonNode jsonNode = mapper.readTree(jsonString);
          Iterator<String> nameIter = jsonNode.fieldNames();
          while (nameIter.hasNext()) {
            String key = nameIter.next();
            JsonNode pair = jsonNode.get(key);
            String value = pair.textValue();
            EN_RULES.put(key, value);
          }
        }
      }

    } catch (Exception ignored) {
      ignored.printStackTrace();
    }
  }

  private Map<String, String> descriptionMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);;

  public RuleDescription() {}

  public boolean containsKey(String key) {
    return descriptionMap.containsKey(key);
  }

  public String get(String key) {
    return descriptionMap.get(key);
  }

  public String put(String key, String value) {
    return descriptionMap.put(key, value);
  }

}
