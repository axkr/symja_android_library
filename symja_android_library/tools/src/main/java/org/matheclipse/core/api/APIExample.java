package org.matheclipse.core.api;

import java.io.IOException;
import java.net.MalformedURLException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;

public class APIExample {
  private static String BASE_URL = "https://www.symja.org";

  public static void main(String[] args) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      JsonNode node =
          mapper.readTree(URI.create(BASE_URL + "/v1/api?i=D(sin(x)%2Cx)&f=plaintext&appid=DEMO")
              .toURL().openStream());
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
