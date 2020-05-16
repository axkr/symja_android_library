package org.matheclipse.api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SymjaClient {
	public static void main(String[] args) {
		ObjectMapper mapper = new ObjectMapper();

		// JSON URL to Java object
		try {
			JsonNode node = mapper
					.readTree(new URL("http://localhost:8080/?input=D(sin(x)%2Cx)&format=mathml&format=plaintext"));
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
