package org.matheclipse.api;

import static io.undertow.Handlers.resource;

import java.util.Deque;
import java.util.Map;

import org.matheclipse.api.parser.FuzzyParserFactory;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;

import com.fasterxml.jackson.databind.node.ObjectNode;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;

public class SymjaServer {

	private static final class APIHandler implements HttpHandler {
		@Override
		public void handleRequest(final HttpServerExchange exchange) throws Exception {
			HeaderMap responseHeaders = exchange.getResponseHeaders();
			responseHeaders.put(new HttpString("Access-Control-Allow-Origin"), "*");
			responseHeaders.put(Headers.CONTENT_TYPE, "application/json");

			Map<String, Deque<String>> queryParameters = exchange.getQueryParameters();
			String inputStr = SymjaServer.getParam(queryParameters, "input", "i", "");
			String[] formformatStrs = SymjaServer.getParams(queryParameters, "format", "f", Pods.PLAIN_STR);
			int formats = Pods.internFormat(formformatStrs);
			// exchange.getResponseSender().send(createJSONJavaScript(inExpr.toString()));
			ObjectNode messageJSON = Pods.createResult(inputStr, formats);
			final String jsonStr = messageJSON.toString();
			// System.out.println(jsonStr);
			exchange.getResponseSender().send(jsonStr);
		}
	}

	public static void main(final String[] args) {
		ToggleFeature.COMPILE = false;
		Config.FUZZY_PARSER = true;
		Config.UNPROTECT_ALLOWED = false;
		Config.USE_MANIPULATE_JS = true;
		Config.JAS_NO_THREADS = false;
		// Config.THREAD_FACTORY = com.google.appengine.api.ThreadManager.currentRequestThreadFactory();
		Config.MATHML_TRIG_LOWERCASE = false;
		Config.MAX_AST_SIZE = ((int) Short.MAX_VALUE) * 8;
		Config.MAX_OUTPUT_SIZE = Short.MAX_VALUE;
		Config.MAX_BIT_LENGTH = ((int) Short.MAX_VALUE) * 8;
		Config.MAX_INPUT_LEAVES = 100L;
		EvalEngine.get().setPackageMode(true);
		F.initSymbols(null, null, false);// new SymbolObserver(), false);
		FuzzyParserFactory.initialize();
 
		final APIHandler apiHandler = new APIHandler();
		
		PathHandler path = new PathHandler()
                .addPrefixPath("/", resource(new ClassPathResourceManager(SymjaServer.class.getClassLoader(),
                		SymjaServer.class.getPackage())).addWelcomeFiles("index.html"))
                .addExactPath("/api",apiHandler);
		
		Undertow server = Undertow.builder().//
				addHttpListener(8080, "localhost").//
				setHandler(path).//
				build();
		server.start();
		System.out.println("started");
	} 

	static String getParam(Map<String, Deque<String>> queryParameters, String longParameter, String shortParameter,
			String defaultStr) {
		Deque<String> d = queryParameters.get(shortParameter);
		if (d != null && !d.isEmpty()) {
			return d.getFirst();
		}
		d = queryParameters.get(longParameter);
		if (d != null && !d.isEmpty()) {
			return d.getFirst();
		}
		return defaultStr;
	}

	static String[] getParams(Map<String, Deque<String>> queryParameters, String longParameter, String shortParameter,
			String defaultStr) {
		Deque<String> d = queryParameters.get(shortParameter);
		if (d != null && !d.isEmpty()) {
			String[] result = d.toArray(new String[d.size()]);
			if (result.length == 1) {
				String[] split = result[0].split(",");
				return split;
			}
			return result;
		}
		d = queryParameters.get(longParameter);
		if (d != null && !d.isEmpty()) {
			String[] result = d.toArray(new String[d.size()]);
			if (result.length == 1) {
				String[] split = result[0].split(",");
				return split;
			}
			return result;
		}
		return new String[] { defaultStr };
	}
}
