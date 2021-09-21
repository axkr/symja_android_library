package org.matheclipse.api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SymjaClientExample {
  public static void main(String[] args) {
    ObjectMapper mapper = new ObjectMapper();
    // JSON URL to Java object
    try {
      JsonNode node =
          mapper.readTree(
              new URL(
                  "http://localhost:8080/v1/api?i=D(Sin(x)%2Cx)&f=latex&f=plaintext&f=sinput&appid=DEMO"));
      System.out.println(node.toPrettyString());
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
