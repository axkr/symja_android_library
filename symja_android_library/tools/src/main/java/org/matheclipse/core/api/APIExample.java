package org.matheclipse.core.api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class APIExample {
  private static String BASE_URL = "https://www.symja.org";

  public static void main(String[] args) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      JsonNode node =
          mapper.readTree(new URL(BASE_URL + "/v1/api?i=D(sin(x)%2Cx)&f=plaintext&appid=DEMO"));
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
