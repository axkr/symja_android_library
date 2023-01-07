package org.matheclipse.api.client;

import java.io.IOException;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONQueryResult {

  @JsonProperty("queryresult")
  JSONPods queryresult;

  public JSONQueryResult() {

  }

  public boolean isSuccess() {
    return queryresult.success;
  }

  public boolean isError() {
    return queryresult.error.error;
  }

  public int numberOfPods() {
    return queryresult.numberOfPods();
  }

  public boolean containsPodsError() {
    JSONPod[] pods = queryresult.getPods();
    for (int j = 0; j < pods.length; j++) {
      if (pods[j].getError().equals("true")) {
        return true;
      }
    }
    return false;
  }

  public boolean containsPodsScanner(String[] scanners) {
    JSONPod[] pods = getPods();
    for (int i = 0; i < pods.length; i++) {
      for (int j = 0; j < scanners.length; j++) {
        if (pods[i].scanner.equals(scanners[j])) {
          return true;
        }
      }
    }
    return false;
  }

  public JSONPod[] getPods() {
    return queryresult.pods;
  }

  public static JSONQueryResult queryResult(JsonNode node) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      JSONQueryResult queryResult = mapper.readValue(node.toString(), JSONQueryResult.class);
      return queryResult;
    } catch (StreamReadException e) {
      e.printStackTrace();
    } catch (DatabindException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
