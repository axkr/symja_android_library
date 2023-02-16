package org.matheclipse.api;

import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import org.matheclipse.api.client.JSONPod;
import org.matheclipse.api.client.JSONQueryResult;
import org.matheclipse.api.client.JSONSubPod;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SymjaClientExample {
  public static void main(String[] args) {
    ObjectMapper mapper = new ObjectMapper();
    // JSON URL to Java object
    try {
      JsonNode node = mapper.readTree(new URL(
          "http://localhost:8080/v1/api?i=D(Sin(x)%2Cx)&f=latex&f=plaintext&f=sinput&appid=DEMO"));
      // simply print out the complete JSON string
      System.out.println(node.toPrettyString());

      // traverse the JSON data structure and extract specific data:
      JSONQueryResult queryResult = JSONQueryResult.queryResult(node);
      System.out.println("Error: " + queryResult.isError());
      JSONPod[] pods = queryResult.getPods();
      for (int i = 0; i < pods.length; i++) {
        JSONPod jsonPod = pods[i];
        JSONSubPod[] subpods = jsonPod.getSubpods();
        String scanner = jsonPod.getScanner();
        System.out.println("  Scanner: " + scanner);
        for (int j = 0; j < subpods.length; j++) {
          String plaintext = subpods[j].getPlaintext();
          System.out.println("    " + plaintext);
        }
      }
    } catch (ConnectException cex) {
      System.out.println("ConnectException: Please ensure the Symja server is running!");
      cex.printStackTrace();
    } catch (JsonParseException e) {
      e.printStackTrace();
    } catch (JsonMappingException e) {
      e.printStackTrace();
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
